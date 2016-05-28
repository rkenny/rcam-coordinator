package tk.bad_rabbit.rcam.coordinator.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import tk.bad_rabbit.rcam.coordinator.client.Client;
import tk.bad_rabbit.rcam.coordinator.client.Clients;
import tk.bad_rabbit.rcam.distributed_backend.client.states.ConnectedClientState;
import tk.bad_rabbit.rcam.distributed_backend.client.states.DisconnectedClientState;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.action.ActionHandler;
import tk.bad_rabbit.rcam.distributed_backend.command.action.ICommandAction;
import tk.bad_rabbit.rcam.distributed_backend.command.action.ReceivedCommandAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.INetworkResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

@Service(value = "serverThread")
@Scope("singleton")
public class ServerThread extends Observable implements Runnable, ActionHandler {

  ExecutorService commandExecutor;

  @Autowired
  @Qualifier("commandFactory")
  ICommandFactory commandFactory;

  @Autowired
  @Qualifier("configurationProvider")
  IConfigurationProvider configurationProvider;

  List<Observer> observers;

  ServerSocketChannel serverSocketChannel;
  Clients clients;
  Selector serverSelector;

  CharsetDecoder asciiDecoder;
  CharsetEncoder asciiEncoder;
  public Thread thread;

  @PostConstruct
  public void initialize() {
    this.commandExecutor = Executors.newFixedThreadPool(5);
  }

  public Set<String> getConnectedServers() {
    synchronized (clients) {
      return clients.getConnectedServers();
    }
  }

  public void start() {
    clients = new Clients();
    this.asciiDecoder = Charset.forName("US-ASCII").newDecoder();
    this.asciiEncoder = Charset.forName("US-ASCII").newEncoder();
    thread = new Thread(this);
    thread.start();

    System.out.println("RCam Coordinator - ServerThread will start");
    System.out.println("RCam Coordinator - ServerThread - port: "
        + (Integer) configurationProvider
            .getServerVariable("backendConnectPort"));
  }

  public void run() {
    boolean running;

    try {
      initializeServer();
      running = true;
    } catch (IOException ioE) {
      running = false;
      ioE.printStackTrace();
    }

    while (running) {
      try {
        Thread.sleep(1250);
      } catch (InterruptedException interrupted) {
        interrupted.printStackTrace();
      }

      try {
        acceptPendingConnections();
        performPendingSocketIOs();

        pollConnectedSockets();
      } catch (IOException e) {
        e.printStackTrace();
        running = false;
      }
    }
    shutdownServer();
  }

  private void shutdownServer() {
    try {
      serverSocketChannel.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initializeServer() throws IOException {
    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);

    serverSocketChannel.socket().bind(
        new InetSocketAddress((String) configurationProvider
            .getServerVariable("backendConnectAddress"),
            (Integer) configurationProvider
                .getServerVariable("backendConnectPort")));
    serverSelector = Selector.open();

    serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
  }

  private void acceptPendingConnections() throws IOException {

    if (serverSocketChannel != null) {
      // try {
      if (serverSelector.select() == 0) {
        return;
      }
      // } catch(IOException e) {
      // e.printStackTrace();
      // return;
      // }
    }

    Iterator<SelectionKey> serverKeyIterator = serverSelector.selectedKeys()
        .iterator();
    while (serverKeyIterator.hasNext()) {
      SelectionKey selectedKey = serverKeyIterator.next();

      if (selectedKey.isValid() && selectedKey.isAcceptable()) {
        System.out
            .println("RCam Coordinator - ServerThread - Accepting a new connection.");
        SocketChannel newSocketChannel;
        try {
          newSocketChannel = serverSocketChannel.accept();
          newSocketChannel.configureBlocking(false);
          newSocketChannel.register(serverSelector, SelectionKey.OP_WRITE
              | SelectionKey.OP_READ);
          System.out
              .println("RCam Coordinator - ServerThread - The remote address is "
                  + newSocketChannel.getRemoteAddress().toString().substring(1));
          addSocketChannel(newSocketChannel.getRemoteAddress().toString()
              .substring(1), newSocketChannel);

        } catch (IOException e) {
          System.out
              .println("RCam Coordinator - ServerThread - Accepting a connection failed.");
        }
      }

      serverKeyIterator.remove();
    }
  }

  private void addSocketChannel(String remoteConnection,
      SocketChannel socketChannel) throws IOException {

    Client newClient = new Client(socketChannel.getRemoteAddress().toString()
        .substring(1), socketChannel);
    clients.add(newClient);
    newClient.setState(new ConnectedClientState());

    setChanged();
    notifyObservers(clients.get(socketChannel.getRemoteAddress().toString()
        .substring(1)));
  }

  private void closeSocketChannel(String remoteConnection,
      SocketChannel socketChannel) {
    try {
      Client client = clients.get(remoteConnection);
      client.getSocketChannel().close();
      client.setState(new DisconnectedClientState());

    } catch (IOException e) {
      e.printStackTrace();
    }

    setChanged();
    notifyObservers(clients.get(remoteConnection));

    clients.remove(remoteConnection);
  }

  private void performPendingSocketIOs() {
    synchronized (this) {
      for (String rC : clients) {
        SocketChannel socketChannel = clients.get(rC).getSocketChannel();
        SelectionKey selectedKey = socketChannel.keyFor(serverSelector);
        ACommand newCommand;

        if (selectedKey.isValid() && selectedKey.isReadable()) {
          try {
            List<CharBuffer> newCommands = readFromChannel(socketChannel);

            for (CharBuffer cB : newCommands) {
              if (cB.toString().length() > 3) {
                newCommand = commandFactory.createCommand(cB);
                if (newCommand != null) {
                  this.addObserver(newCommand);
                  System.out.println(Thread.currentThread().getName() + " Will add a pending ReceivedCommandAction to the new command");
                  newCommand.addPendingAction(new ReceivedCommandAction());
                }
              }
            }
          } catch (IOException e) {
            closeSocketChannel(rC, socketChannel);
            continue;
          }

        }
      }
      return;
    }
  }

  public List<CharBuffer> readFromChannel(SocketChannel selectedChannel)
      throws IOException {
    ArrayList<CharBuffer> returnedList = new ArrayList<CharBuffer>();

    String returnedBuffer = "";
    ByteBuffer buffer = ByteBuffer.allocate(1024);

    selectedChannel.read(buffer);
    buffer.flip();

    try {
      returnedBuffer = asciiDecoder.decode(buffer).toString();
    } catch (CharacterCodingException e) {
      e.printStackTrace();
    }
    buffer.clear();

    if (returnedBuffer.toString().length() > 3) {
      System.out.println("RCAm Distributed Backend - " + returnedBuffer.toString());
    }

    String[] tokens = returnedBuffer.split("\n");

    for (String commandString : tokens) {
      returnedList.add(CharBuffer.wrap(commandString));
    }

    return returnedList;

  }

  public void writeToChannel(SocketChannel selectedChannel, ICommandAction commandAction) throws IOException {
    writeToChannel(selectedChannel, commandAction.asCharBuffer());

  }

  public void writeToChannel(SocketChannel selectedChannel, CharBuffer charBuffer) throws IOException {
    ByteBuffer buffer = asciiEncoder.encode(charBuffer);

    while (buffer.hasRemaining()) {
      selectedChannel.write(buffer);
    }
    buffer.clear();
    
  }

  //public void sendReductionComplete(ACommand command) throws IOException {
  //  send(commandFactory.createReductionCompleteCommand(command));
  //}

  //public void sendCancelCommand(String server, ACommand command) throws IOException {
//    send(server, commandFactory.createCancelCommand(command));
  //}

  public void send(ICommandAction commandAction) throws IOException {

    Iterator<String> i = clients.iterator();
    while (i.hasNext()) {
      String client = i.next();
      SocketChannel connectedChannel = clients.get(client).getSocketChannel();
      SelectionKey key = connectedChannel.keyFor(serverSelector);
      if (key.isValid() && key.isWritable()) {
        writeToChannel(connectedChannel, commandAction);
      }
    }
  }

  public void send(ACommand command) throws IOException {
    Iterator<SelectionKey> keyIterator = serverSelector.selectedKeys()
        .iterator();

    for (String rC : clients) {
      sendCommandToClient(rC, command);
    }
  }

  public void send(String client, ACommand command) throws IOException {
    sendCommandToClient(client, command);
  }

  public void sendCommandToClient(String client, ACommand command)
      throws IOException {
    SocketChannel connectedChannel = clients.get(client).getSocketChannel();
    SelectionKey key = connectedChannel.keyFor(serverSelector);

    try {
      if (key.isValid() && key.isWritable()) {
        this.addObserver(command);
        System.out.println("Line 321 in Server Thread sends the command");
        // writeToChannel(connectedChannel, command.asCharBuffer());
      } else {
        System.out.println("Key is not writable.");
      }
      // } catch(IOException ioException) {
      // System.err.println("Error reading from a channel. Closing that channel.");
      // closeSocketChannel(client, connectedChannel);
      // throw ioException;
    } finally {
      System.out
          .println("this is here to keep the try block at line 318 in ServerThread");
    }
  }

  private void pollConnectedSockets() {
    CharBuffer buffer = CharBuffer.wrap("!\n");
    List<String> socketsToClose = new ArrayList<String>();
    for (String rC : clients) {
      SocketChannel socketChannel = clients.get(rC).getSocketChannel();
      SelectionKey selectedKey = socketChannel.keyFor(serverSelector);

      if (selectedKey.isWritable()) {
        try {
          writeToChannel(socketChannel, buffer);
        } catch (IOException e) {
          socketsToClose.add(rC);
        }
      }
    }

    for (String socketToClose : socketsToClose) {
      closeSocketChannel(socketToClose, clients.get(socketToClose)
          .getSocketChannel());
    }
  }

  public Future<Integer> handleAction(ICommandAction action) {
    if (action instanceof INetworkResponseAction) {
      return commandExecutor.submit(((INetworkResponseAction) action)
          .getNetworkCallable(this));
    }
    return null;
  }

}
