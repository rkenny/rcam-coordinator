package tk.bad_rabbit.rcam.coordinator.server;

import java.io.IOException;
import java.net.InetAddress;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;
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
  Map<String, SocketChannel> socketChannels;
  Selector serverSelector;
  
  //int port;
  
  CharsetDecoder asciiDecoder;
  CharsetEncoder asciiEncoder;
  public Thread thread;
  
  
  @PostConstruct
  public void initialize() {
    this.observers = new ArrayList<Observer>();
  }
  
  public void injectObserver(Observer newObserver) {
    this.observers.add(newObserver);
  }
  
  public Set<String> getConnectedServers() {
    return socketChannels.keySet();
  }
  
  public void update(Observable observable, Object arg) {
      ACommand updatedCommand = (ACommand) observable;
      
      Set<String> connectedClients = socketChannels.keySet();
      Iterator<String> i = connectedClients.iterator();
      while(i.hasNext()) {
        String rC = i.next();
        SocketChannel selectedChannel = socketChannels.get(rC);
        
        if(rC.equals((String) arg)) {
          updatedCommand.doNetworkAction(this, rC);
        }
      }
  }
  
  public void start() {
    socketChannels = new HashMap<String, SocketChannel>();
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
        Thread.sleep(125);
      } catch (InterruptedException interrupted) {
        interrupted.printStackTrace();
      }
      try {
        acceptPendingConnections();
        performPendingSocketIOs();
      } catch(IOException ioE) {
        System.err.println("Ran into a critical error. Shutting down the server.");
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
  
  private void initializeServer() throws IOException{
    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);

    serverSocketChannel.socket().bind(new InetSocketAddress((String) configurationProvider.getServerVariable("backendConnectAddress"), (Integer) configurationProvider.getServerVariable("backendConnectPort")));
    serverSelector = Selector.open();
    
    serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
  }
  
 private void acceptPendingConnections() throws IOException {
    
    if(serverSocketChannel != null) {
      if(serverSelector.select() == 0) {
        return;
      }
    }
    
    Iterator<SelectionKey> serverKeyIterator = serverSelector.selectedKeys().iterator();
    while(serverKeyIterator.hasNext()) {
      SelectionKey selectedKey = serverKeyIterator.next();
      
      if(selectedKey.isValid() && selectedKey.isAcceptable()) {
        System.out.println("RCam Coordinator - ServerThread - Accepting a new connection.");
        SocketChannel newSocketChannel;
        newSocketChannel = serverSocketChannel.accept();
        newSocketChannel.configureBlocking(false);
        newSocketChannel.register(serverSelector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
        System.out.println("RCam Coordinator - ServerThread - The remote address is " + newSocketChannel.getRemoteAddress().toString().substring(1));
        socketChannels.put(newSocketChannel.getRemoteAddress().toString().substring(1), newSocketChannel);
        
        setChanged();
        notifyObservers(newSocketChannel.getRemoteAddress().toString().substring(1));
        
      }
      
      serverKeyIterator.remove();
    }
  }
 
 private void performPendingSocketIOs() throws IOException {
   //synchronized(runController) {
   Set<String> connectedChannels = socketChannels.keySet();
     Iterator<String> i = connectedChannels.iterator();
     while(i.hasNext()) {
       String rC = i.next();
       SocketChannel socketChannel = socketChannels.get(rC);
       SelectionKey selectedKey = socketChannel.keyFor(serverSelector);
       if(selectedKey.isValid() && selectedKey.isReadable()) {
         List<CharBuffer> newCommands = readFromChannel(socketChannel);
         for(CharBuffer cB : newCommands) {
           ACommand newCommand = commandFactory.createCommand(cB, rC);
           if(newCommand != null) {
             newCommand.addObservers(observers);
             
             //newCommand.setServers(getConnectedServers());
             newCommand.setState(socketChannel.getRemoteAddress().toString().substring(1), new ReceivedCommandState());
           }
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
     //System.out.println(commandString);
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
 
 public void send(ACommand command) {
   Iterator<SelectionKey> keyIterator = serverSelector.selectedKeys().iterator();
   
   Set<String> connectedChannels = socketChannels.keySet();
   Iterator<String> i = connectedChannels.iterator();
   
   while(i.hasNext()) {
     sendCommandToClient(i.next(), command);
   }
 }
 
 public void send(String client, ACommand command) {
   sendCommandToClient(client, command);
 }
 
 public void sendCommandToClient(String client, ACommand command) {
   //synchronized(runController) {
     SocketChannel connectedChannel = socketChannels.get(client);
     SelectionKey key = connectedChannel.keyFor(serverSelector);
     
     try {
       if(key.isWritable()) {
         writeToChannel(connectedChannel, command.asCharBuffer());
       } else {
         System.out.println("Key is not writable.");
       }
     } catch(IOException ioException) {
       System.err.println("Error reading from a channel. Closing that channel.");
       try {
         connectedChannel.close();
       } catch (IOException e) {
         System.err.println("Error closing the channel.");
         e.printStackTrace();
       }
     }  
   }
 

  
}
