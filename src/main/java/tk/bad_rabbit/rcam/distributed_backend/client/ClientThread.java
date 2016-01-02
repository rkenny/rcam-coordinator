package tk.bad_rabbit.rcam.distributed_backend.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import tk.bad_rabbit.rcam.distributed_backend.client.states.AClientState;
import tk.bad_rabbit.rcam.distributed_backend.client.states.ConnectedClientState;
import tk.bad_rabbit.rcam.distributed_backend.client.states.DisconnectedClientState;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.IClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.states.AckedState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReadyToReduceState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ErrorCommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ReceivedCommandState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class ClientThread { // extends Observable implements Runnable, Observer, IClientThread {
//
//  Thread clientThread;
//  //Client thisClient;
//  AClientState clientState;
//  SocketChannel socketChannel;
//  Selector clientSelector;
//  ByteBuffer clientBuffer = ByteBuffer.allocate(1024);
//  
//  CharsetDecoder asciiDecoder;
//  CharsetEncoder asciiEncoder;
//  boolean running;
//  String remoteAddress;
//  int remotePort;
//  
//  RunController runController;
//  ICommandFactory commandFactory;
//  
//  public void observeCommand(ACommand command) {
//    command.addObserver(this);
//  }
//  
//  
//  public synchronized AClientState setState(AClientState clientState) {
//    
//    this.clientState = clientState;
//    
//    setChanged();
//    notifyObservers(remoteAddress+":"+remotePort);
//    
//    return clientState;
//  }
//  
//  public synchronized void doAction(Observer actionObserver) {
//    this.clientState.doAction(actionObserver, this);
//  }
//  
//  public void update(Observable updatedCommand, Object arg) {
//    synchronized(updatedCommand) {
//      
//      Set<SelectionKey> selectedKeys = clientSelector.selectedKeys();
//      Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
//      while(keyIterator.hasNext()) {
//        SelectionKey key = keyIterator.next();
//        SocketChannel selectedChannel = (SocketChannel) key.channel();
//      }
//      
//      
//      if(arg instanceof Map.Entry) {
//        Map.Entry<ACommand, Map.Entry<String, ICommandState>> commandStateMap = (Map.Entry<ACommand, Map.Entry<String, ICommandState>> ) arg;
//        if(this.getServerString().equals(commandStateMap.getValue().getKey())) {
//          //((ACommand) updatedCommand).doAction(this, commandStateMap.getValue().getKey());
//        }
//      }
//    }
//  }
//  
//  public String getServerString() {
//    return remoteAddress+":"+remotePort;
//  }
//  
//  public void ackCommandReceived(ACommand command) {
//    command.setState(getServerString(), new AckedState());
//  }
//  
//  public void commandResultReceived(Integer ackNumber, Integer resultCode) {
//    this.runController.commandResultReceived(getServerString(), ackNumber, resultCode);
//  }
//  
//  public void removeCommand(ACommand command) {
//    this.runController.removeCommand(getServerString(), command);
//  }
//  
//  public void readyToReduce(ACommand command) {
//    command.setState(getServerString(), new CommandReadyToReduceState());
//    this.runController.readyToReduce(getServerString(), command);
//  }
//  
//  
//  public ClientThread(RunController runController, ICommandFactory commandFactory, String remoteAddress, int remotePort ) {
//    this.remoteAddress = remoteAddress;
//    this.remotePort = remotePort;
//    
//    this.commandFactory = commandFactory;
//    this.runController = runController;
//    
//    this.asciiDecoder = Charset.forName("US-ASCII").newDecoder();
//    this.asciiEncoder = Charset.forName("US-ASCII").newEncoder();
//  }
//  
//  public void start() {
//    this.clientThread = new Thread(this);
//    clientThread.start();
//  }
//  
//  private void initializeClient() throws IOException, ClosedChannelException {
//    clientSelector = Selector.open();
//    socketChannel = SocketChannel.open();
//    socketChannel.configureBlocking(false);
//    socketChannel.connect(new InetSocketAddress(remoteAddress, remotePort));
//    
//    socketChannel.register(clientSelector, SelectionKey.OP_CONNECT);
//  }
//  
//  public void run() {
//    try {
//      initializeClient();
//
//      running = true;
//    } catch(Exception e) {
//      running = false;
//      e.printStackTrace();
//    }
//    
//    while(running) {
//      try {
//        Thread.sleep(125);
//        performPendingSocketIO();
//      } catch(InterruptedException e) {
//        e.printStackTrace();
//        running = false;
//      } catch(IOException e) {
//        running = false;
//        e.printStackTrace();
//      }
//    }
//    
//    System.out.println("Client("+this.getServerString()+".run() is finished.");
//  }
//
//  public void send(ACommand command) {
//    Set<SelectionKey> selectedKeys = clientSelector.selectedKeys();
//    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
//    while(keyIterator.hasNext()) {
//      SelectionKey key = keyIterator.next();
//      SocketChannel selectedChannel = (SocketChannel) key.channel();
//
//      try {
//        if(key.isWritable()) {
//          keyIterator.remove();
//          writeCommandToChannel(selectedChannel, command);
//        } else {
//          System.out.println("Key is not writable.");
//        }
//      } catch(IOException ioException) {
//        System.err.println("Client("+getServerString()+": Error reading from a channel. Closing that channel.");
//        //command.setState(getServerString(), new ErrorCommandState());
//        try {
//          this.setState(new DisconnectedClientState());
//          selectedChannel.close();
//        } catch (IOException e) {
//          System.err.println("Error closing the channel.");
//          e.printStackTrace();
//        }
//      }
//    }
//  }
//  
//  public void sendCancel(ACommand command) {
//      send(commandFactory.createCancelCommand(command));
//  }
//  
//  private void performPendingSocketIO() throws IOException{
//    if(clientSelector.select() == 0) { 
//      return; 
//    }
//    
//    Set<SelectionKey> selectedKeys = clientSelector.selectedKeys();
//    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
//    while(keyIterator.hasNext()) {
//      SelectionKey key = keyIterator.next();
//      SocketChannel selectedChannel = (SocketChannel) key.channel();
//      
//      
//      if(key.isConnectable()) {
//        selectedChannel.finishConnect();
//        selectedChannel.register(clientSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
//        this.setState(new ConnectedClientState());
//      }
//      
//      try {
//        if(key.isReadable()) {
//          keyIterator.remove();
//          List<CharBuffer> newCommands = readFromChannel(selectedChannel);
//          for(CharBuffer newCommand : newCommands) {
//
//            // ACommand processingCommand = commandFactory.createCommand(newCommand);
//            //if(processingCommand != null) {
//              
//              
//            //  this.addObserver(processingCommand);
//              //runController.observeCommand(processingCommand);
//            //  this.observeCommand(processingCommand);
//            //  processingCommand.setState(getServerString(), new ReceivedCommandState());
//            //}
//          }
//        }
//
//      } catch(IOException ioException) {
//        System.err.println("Client("+getServerString()+": Error reading from a channel. Closing that channel.");
//        try {
//          this.setState(new DisconnectedClientState());
//          selectedChannel.close();
//        } catch (IOException e) {
//          System.err.println("Error closing the channel.");
//          e.printStackTrace();
//        }
//      }
//    }
//  }
//  
//  public List<CharBuffer> readFromChannel(SocketChannel selectedChannel) throws IOException {
//    ArrayList<CharBuffer> returnedList = new ArrayList<CharBuffer>();
//    
//    String returnedBuffer;
//    ByteBuffer buffer = ByteBuffer.allocate(1024);
//    
//    if(selectedChannel.read(buffer) == -1) {
//      throw new IOException();  
//    }
//    buffer.flip();
//    
//    try {
//      returnedBuffer = asciiDecoder.decode(buffer).toString();
//    } catch (CharacterCodingException e) {
//      e.printStackTrace();
//      returnedBuffer =""; // CharBuffer.allocate(1024);
//    }
//    
//    String[] tokens = returnedBuffer.split("\n");
//    
//    for(String commandString : tokens) {
//      returnedList.add(CharBuffer.wrap(commandString));
//    }
//    
//    return returnedList;
//    
//  }
//  
//
//  
//  public void writeCommandToChannel(SocketChannel selectedChannel, ACommand command) throws IOException {
//    ByteBuffer buffer = asciiEncoder.encode(command.asCharBuffer());
//
//    while(buffer.hasRemaining()) {
//        selectedChannel.write(buffer);
//    }    
//    buffer.clear();
//  }
//  
//  

  
}
