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
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import tk.bad_rabbit.rcam.distributed_backend.command.CommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.ErrorCommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.IClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ReceivedCommandState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class ClientThread implements Runnable, Observer, IClientThread {

  Thread clientThread;
  Client thisClient;
  
  SocketChannel socketChannel;
  Selector clientSelector;
  ByteBuffer clientBuffer = ByteBuffer.allocate(1024);
  
  CharsetDecoder asciiDecoder;
  CharsetEncoder asciiEncoder;
  boolean running;
  String remoteAddress;
  int remotePort;
  
  RunController runController;
  ICommandFactory commandFactory;
  
  public void observeCommand(ACommand command) {
    command.addObserver(this);
  }
  
  public void update(Observable updatedCommand, Object arg) {
    synchronized(updatedCommand) {
      ((ACommand) updatedCommand).doAction(this);
    }
  }
  
  public ClientThread(RunController runController, ICommandFactory commandFactory, String remoteAddress, int remotePort ) {
    this.remoteAddress = remoteAddress;
    this.remotePort = remotePort;
    
    this.commandFactory = commandFactory;
    this.runController = runController;
    
    this.asciiDecoder = Charset.forName("US-ASCII").newDecoder();
    this.asciiEncoder = Charset.forName("US-ASCII").newEncoder();
  }
  
  public void start() {
    this.clientThread = new Thread(this);
    clientThread.start();
  }
  
  private void initializeClient() throws IOException, ClosedChannelException {
    clientSelector = Selector.open();
    socketChannel = SocketChannel.open();
    socketChannel.configureBlocking(false);
    socketChannel.connect(new InetSocketAddress(remoteAddress, remotePort));
    
    socketChannel.register(clientSelector, SelectionKey.OP_CONNECT);
  }
  
  public void run() {
    try {
      initializeClient();

      running = true;
    } catch(Exception e) {
      running = false;
      e.printStackTrace();
    }
    
    while(running) {
      try {
        Thread.sleep(125);
        performPendingSocketIO();
      } catch(InterruptedException e) {
        e.printStackTrace();
        running = false;
      } catch(IOException e) {
        running = false;
        e.printStackTrace();
      }
    }
    
    System.out.println("That's it, folks.");
  }

  public void send(ACommand command) {
    Set<SelectionKey> selectedKeys = clientSelector.selectedKeys();
    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
    while(keyIterator.hasNext()) {
      SelectionKey key = keyIterator.next();
      SocketChannel selectedChannel = (SocketChannel) key.channel();

      try {
        if(key.isWritable()) {
          keyIterator.remove();
          writeCommandToChannel(selectedChannel, command);
        } else {
          System.out.println("Key is not writable.");
        }
      } catch(IOException ioException) {
        System.err.println("Error reading from a channel. Closing that channel.");
        command.setState(new ErrorCommandState());
        try {
          selectedChannel.close();
        } catch (IOException e) {
          System.err.println("Error closing the channel.");
          e.printStackTrace();
        }
      }
    }
  }
  
  private void performPendingSocketIO() throws IOException{
    if(clientSelector.select() == 0) { 
      //System.out.println("Select returned 0");
      return; 
    }
    
    Set<SelectionKey> selectedKeys = clientSelector.selectedKeys();
    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
    while(keyIterator.hasNext()) {
      SelectionKey key = keyIterator.next();
      SocketChannel selectedChannel = (SocketChannel) key.channel();
      
      
      if(key.isConnectable()) {
        selectedChannel.finishConnect();
        selectedChannel.register(clientSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
      }
      
      try {
        if(key.isReadable()) {
          keyIterator.remove();
          ACommand processingCommand = commandFactory.createCommand(readFromChannel(selectedChannel));
          
          if(processingCommand != null) {
            runController.observeCommand(processingCommand);
            processingCommand.setState(new ReceivedCommandState());
          }
        }

      } catch(IOException ioException) {
        System.err.println("Error reading from a channel. Closing that channel.");
        //if(null != processingCommand) {
        //  processingCommand.setState(new ErrorCommandState());
        //}
        try {
          selectedChannel.close();
        } catch (IOException e) {
          System.err.println("Error closing the channel.");
          e.printStackTrace();
        }
      }
    }
  }
  
  public CharBuffer readFromChannel(SocketChannel selectedChannel) throws IOException {
    CharBuffer returnedBuffer;
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    
    selectedChannel.read(buffer);
    buffer.flip();
    
    try {
      returnedBuffer = asciiDecoder.decode(buffer);
    } catch (CharacterCodingException e) {
      e.printStackTrace();
      returnedBuffer = CharBuffer.allocate(1024);
    }
    
    return returnedBuffer;
    
  }
  

  
  public void writeCommandToChannel(SocketChannel selectedChannel, ACommand command) throws IOException {
    ByteBuffer buffer = asciiEncoder.encode(command.asCharBuffer());
    while(buffer.hasRemaining()) {
        selectedChannel.write(buffer);
    }    
    buffer.clear();
  }
  
  

  
}
