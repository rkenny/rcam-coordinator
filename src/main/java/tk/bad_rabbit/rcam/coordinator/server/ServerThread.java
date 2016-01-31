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
import tk.bad_rabbit.rcam.distributed_backend.command.states.ReceivedCommandState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

@Service(value="serverThread")
@Scope("singleton")
public class ServerThread extends Observable implements Runnable, Observer  {
  
  
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
    this.observers = new ArrayList<Observer>();
    this.observers.add(this);
  }
  
  public void injectObserver(Observer newObserver) {
    this.observers.add(newObserver);
  }
  
  public Set<String> getConnectedServers() {
    return clients.getConnectedServers();
  }
  
  public void update(Observable observable, Object arg) {
      ACommand updatedCommand = (ACommand) observable;
      
      for(String rC : clients) {
        if(rC.equals((String) arg)) {
          updatedCommand.doNetworkAction(this, rC);
        }
      }
  }
  
  public void start() {
    clients = new Clients();
    this.asciiDecoder = Charset.forName("US-ASCII").newDecoder();
    this.asciiEncoder = Charset.forName("US-ASCII").newEncoder();
    thread = new Thread(this);
    thread.start();
    
    System.out.println("RCam Coordinator - ServerThread will start");
    System.out.println("RCam Coordinator - ServerThread - port: "+ (Integer) configurationProvider.getServerVariable("backendConnectPort") );
  }
  
  
  
  
  public void run() {
    boolean running;
    
    try {
      initializeServer();
      running = true;
    } catch(IOException ioE) {
      running = false;
      ioE.printStackTrace();
    }
    
    while(running) {
      try {
        Thread.sleep(1250);
      } catch (InterruptedException interrupted) {
        interrupted.printStackTrace();
      }
      
      acceptPendingConnections();
      performPendingSocketIOs();
    
      pollConnectedSockets();

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
  
  private void initializeServer() throws IOException{
    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);

    serverSocketChannel.socket().bind(new InetSocketAddress((String) configurationProvider.getServerVariable("backendConnectAddress"), (Integer) configurationProvider.getServerVariable("backendConnectPort")));
    serverSelector = Selector.open();
    
    serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
  }
  
 private void acceptPendingConnections()  {
    
    if(serverSocketChannel != null) {
      try {
        if(serverSelector.select() == 0) {
          return;
        }
      } catch(IOException e) {
        e.printStackTrace();
        return;
      }
    }
    
    Iterator<SelectionKey> serverKeyIterator = serverSelector.selectedKeys().iterator();
    while(serverKeyIterator.hasNext()) {
      SelectionKey selectedKey = serverKeyIterator.next();
      
      if(selectedKey.isValid() && selectedKey.isAcceptable()) {
        System.out.println("RCam Coordinator - ServerThread - Accepting a new connection.");
        SocketChannel newSocketChannel;
        try {
          newSocketChannel = serverSocketChannel.accept();
          newSocketChannel.configureBlocking(false);
          newSocketChannel.register(serverSelector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
          System.out.println("RCam Coordinator - ServerThread - The remote address is " + newSocketChannel.getRemoteAddress().toString().substring(1));
          addSocketChannel(newSocketChannel.getRemoteAddress().toString().substring(1), newSocketChannel);
          
          
        } catch(IOException e) {
          System.out.println("RCam Coordinator - ServerThread - Accepting a connection failed.");
        }
      }
      
      serverKeyIterator.remove();
    }
  }
 
 private void addSocketChannel(String remoteConnection, SocketChannel socketChannel) throws IOException {
   
   Client newClient = new Client(socketChannel.getRemoteAddress().toString().substring(1), socketChannel);
   clients.add(newClient);
   newClient.setState(new ConnectedClientState());
   
   setChanged();
   notifyObservers(clients.get(socketChannel.getRemoteAddress().toString().substring(1)));
 }
 
 private void closeSocketChannel(String remoteConnection, SocketChannel socketChannel)  {
   try {
     Client client = clients.get(remoteConnection);
     client.getSocketChannel().close();
     client.setState(new DisconnectedClientState());
     
   } catch(IOException e) {
     e.printStackTrace();
   }
   
   
   setChanged();
   notifyObservers(clients.get(remoteConnection));
   
   clients.remove(remoteConnection);
 }
 
 private void performPendingSocketIOs()  {
   //synchronized(runController) {
   
   for(String rC : clients) {
     SocketChannel socketChannel = clients.get(rC).getSocketChannel();
     SelectionKey selectedKey = socketChannel.keyFor(serverSelector);
     ACommand newCommand;
     
     if(selectedKey.isValid() && selectedKey.isReadable()) {
       try {
         
         List<CharBuffer> newCommands = readFromChannel(socketChannel);
         
         for(CharBuffer cB : newCommands) {
         
           //System.out.println(cB.toString().length());
           if(cB.toString().length() > 3) {
             newCommand = commandFactory.createCommand(cB, rC);
             if(newCommand != null) {
               newCommand.addObservers(observers);
               this.addObserver(newCommand);
               
               newCommand.setState(rC, new ReceivedCommandState());
             }
           }
         }
       } catch(IOException e) {
         closeSocketChannel(rC, socketChannel);
         continue;
       }
        
     }
   }
   return;
 }
 
 public List<CharBuffer> readFromChannel(SocketChannel selectedChannel) throws IOException {
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

   String[] tokens = returnedBuffer.split("\n");
   
   for(String commandString : tokens) {
     returnedList.add(CharBuffer.wrap(commandString));
   }
   
   return returnedList;
   
 }
 
 public void writeToChannel(SocketChannel selectedChannel, CharBuffer charBuffer) throws IOException {
   ByteBuffer buffer = asciiEncoder.encode(charBuffer);

   while(buffer.hasRemaining()) {
       selectedChannel.write(buffer);
   }
   buffer.clear();

 }
 
 public void sendReductionComplete(ACommand command) {
   send(commandFactory.createReductionCompleteCommand(command));
 }
 
 public void sendCancelCommand(String server, ACommand command) {
   send(server, commandFactory.createCancelCommand(command));
 }
 
 public void send(ACommand command) {
   Iterator<SelectionKey> keyIterator = serverSelector.selectedKeys().iterator();
   
   for(String rC : clients) {
     sendCommandToClient(rC, command);
   }
 }
 
 public void send(String client, ACommand command) {
   sendCommandToClient(client, command);
 }
 
 public void sendCommandToClient(String client, ACommand command) {
   //synchronized(runController) {
     SocketChannel connectedChannel = clients.get(client).getSocketChannel();
     SelectionKey key = connectedChannel.keyFor(serverSelector);
     
     try {
       if(key.isValid() && key.isWritable()) {
         this.addObserver(command);
         writeToChannel(connectedChannel, command.asCharBuffer());
       } else {
         System.out.println("Key is not writable.");
       }
     } catch(IOException ioException) {
       System.err.println("Error reading from a channel. Closing that channel.");
       closeSocketChannel(client, connectedChannel);
     }  
   }
 
   private void pollConnectedSockets()  {
     CharBuffer buffer = CharBuffer.wrap("!\n");
     List<String> socketsToClose = new ArrayList<String>();
     for(String rC : clients) {
       SocketChannel socketChannel = clients.get(rC).getSocketChannel();
       SelectionKey selectedKey = socketChannel.keyFor(serverSelector);
       
       if(selectedKey.isWritable()) {
         try {
           writeToChannel(socketChannel, buffer);
         } catch(IOException e) {
           socketsToClose.add(rC);
         }
       }
     }
     
     for(String socketToClose : socketsToClose) {
       closeSocketChannel(socketToClose, clients.get(socketToClose).getSocketChannel());
     }
   }

  
}
