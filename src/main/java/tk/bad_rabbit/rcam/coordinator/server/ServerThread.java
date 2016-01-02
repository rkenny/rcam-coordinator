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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ReceivedCommandState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

@Service(value="serverThread")
@Scope("singleton")
public class ServerThread extends Observable implements Runnable, Observer  {
  CommandController commandController;
  
  @Autowired
  @Qualifier("commandFactory")
  ICommandFactory commandFactory;
  
  @Autowired
  @Qualifier("configurationProvider")
  IConfigurationProvider configurationProvider;
  
  ServerSocketChannel serverSocketChannel;
  Map<String, SocketChannel> socketChannels;
  Selector serverSelector;
  
  int port;
  
  CharsetDecoder asciiDecoder;
  CharsetEncoder asciiEncoder;
  public Thread thread;
  
  
  public void injectCommandController(CommandController commandController) {
    this.commandController = commandController;
  }
  
  public Set<String> getConnectedServers() {
    return socketChannels.keySet();
    
  }
  
  public void update(Observable observable, Object arg) {
    //synchronized(runController) {
      ACommand updatedCommand = (ACommand) observable;
      System.out.println("RCam Coordinator - ServerThread - Receieved an update for command " + updatedCommand.getAckNumber());
      Set<String> connectedClients = socketChannels.keySet();
      Iterator<String> i = connectedClients.iterator();
      while(i.hasNext()) {
        String rC = i.next();
        SocketChannel selectedChannel = socketChannels.get(rC);
      
        if(arg instanceof ICommandState) {
          updatedCommand.doNetworkAction(this, rC);
        }
      
        
        if(arg instanceof Map.Entry) {
          Map.Entry<ACommand, Map.Entry<String, ICommandState>> commandStateMap = (Map.Entry<ACommand, Map.Entry<String, ICommandState>> ) arg;
          if(rC.equals(commandStateMap.getValue().getKey())) {
            updatedCommand.doNetworkAction(this, rC);
          }  
        }
      }
  }
  
  public void start() {
    port = (Integer) configurationProvider.getServerVariable("backendConnectPort");
    socketChannels = new HashMap<String, SocketChannel>();
    this.asciiDecoder = Charset.forName("US-ASCII").newDecoder();
    this.asciiEncoder = Charset.forName("US-ASCII").newEncoder();
    thread = new Thread(this);
    thread.start();
    
    System.out.println("RCam Coordinator - ServerThread will start");
    System.out.println("RCam Coordinator - ServerThread - port: "+ port );
  }
  
  
  
  
  public void run() {
    boolean running;
    System.out.println("Trying to start server on port " + port);
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
    serverSocketChannel.socket().bind(new InetSocketAddress(port));
   
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
             newCommand.addObserver(this);
             newCommand.addObserver(commandController);
             newCommand.setServers(getConnectedServers());
             newCommand.setState(new ReceivedCommandState());
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
     //returnedBuffer = CharBuffer.allocate(1024);
   }
   buffer.clear();
   

   String[] tokens = returnedBuffer.split("\n");
   
   for(String commandString : tokens) {
     System.out.println(commandString);
     returnedList.add(CharBuffer.wrap(commandString));
   }
   System.out.println("RCam Coordinator - ServerThread("+selectedChannel.getRemoteAddress().toString().substring(1)+")  read:[" + returnedBuffer.toString() + "]");
   
   return returnedList;
   
 }
 
 public void writeToChannel(SocketChannel selectedChannel, CharBuffer charBuffer) throws IOException {
   ByteBuffer buffer = asciiEncoder.encode(charBuffer);

   while(buffer.hasRemaining()) {
       selectedChannel.write(buffer);
   }
   System.out.println("RCam Coordinator -ServerThread("+selectedChannel.getRemoteAddress().toString().substring(1)+")  " + charBuffer.toString());
   buffer.clear();
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
 
   //public void commandResultReceived(Integer ackNumber, Integer resultCode) {
   //  this.runController.commandResultReceived(getServerString(), ackNumber, resultCode);
   //}

 
 
 //public void ackCommandReceived(String client, ACommand command) {
 //    command.setState(client, new AckedState());
 //}
 
 //public void sendAck(String client, ACommand command) {
 //  send(client, commandFactory.createAckCommand(command));
 //  command.setState(client, new AckedState());
 //}
   
  //public void sendAck(ACommand command) {
  //    ACommand ackCommand = commandFactory.createAckCommand(command);
  //    ackCommand.addObserver(this);
  //    send(ackCommand);
  //    
  //    ICommandState ackedState = new AckedState();
  //    command.setState(ackedState);
 // }
  
  //public void sendResult(ACommand command) {
//    ACommand resultCommand = commandFactory.createResultCommand(command);
    //send(resultCommand);
  //}
  
  
}
