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
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.stereotype.Component;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.CommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.commandqueuer.ICommandQueuer;


@Component(value="client")
@Scope("request")
@Configurable(preConstruction = true)
@EnableSpringConfigured
@EnableLoadTimeWeaving
public class Client implements IClient {

  String remoteAddress;
  int remotePort;
  
  Thread clientThread;
  
  SocketChannel socketChannel;
  Selector clientSelector;
  ByteBuffer clientBuffer = ByteBuffer.allocate(1024);
  
  CharsetDecoder asciiDecoder;
  CharsetEncoder asciiEncoder;
  boolean running;
  
  ICommandQueuer commandQueuer;
  
  @Autowired
  @Qualifier("commandFactory")
  ICommandFactory commandFactory;
  
  public Client(String remoteAddress, int remotePort) {
    this();
    this.remoteAddress = remoteAddress;
    this.remotePort = remotePort;
  }
  
  public Client() {
    this.commandQueuer = new CommandQueuer();
    this.commandFactory = new CommandFactory();
    this.asciiDecoder = Charset.forName("US-ASCII").newDecoder();
    this.asciiEncoder = Charset.forName("US-ASCII").newEncoder();
  }

  public void startClientThread() {
    this.clientThread = new Thread(this);
    this.clientThread.start();
  }
  
  public void record() {
    System.out.println("Is commandQueur null? " + (commandQueuer == null));
    System.out.println("Is commandFactory null? " + (commandFactory == null));
    commandQueuer.addOutgoingCommand(commandFactory.createCommand("Record"));
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
      } catch(InterruptedException e) {
        e.printStackTrace();
      } try {
        performPendingSocketIO();
      } catch(IOException e) {
        running = false;
        e.printStackTrace();
      }
      
      
    }
    
    //socketChannel.close();
      
 
    
    System.out.println("That's it, folks.");
  }

  private void performPendingSocketIO() throws IOException{
    if(clientSelector.select() == 0) { 
      //System.out.println("clientSelector.select() = 0");
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
      
      if(key.isReadable()) { /*running = false;*/ 
        try {
          ICommand incomingCommand = commandFactory.createCommand(readFromChannel(selectedChannel));
          if(null != incomingCommand) {
            commandQueuer.addIncomingCommand(incomingCommand);
            writeCommandToChannel(selectedChannel, commandFactory.ackCommand());  
          } else {
            //writeCommandToChannel(selectedChannel, commandFactory.errorCommand());  
          }
        }  catch(IOException ioException) {
          System.err.println("Error reading from a channel. Closing that channel.");
          try {
            selectedChannel.close();
          } catch (IOException e) {
            System.err.println("Error closing the channel.");
            e.printStackTrace();
            continue;
          }
          continue;
        }
      }
      if(key.isWritable()) {
        try {
          ICommand outgoingCommand;
          while((outgoingCommand = commandQueuer.getNextOutgoingCommand()) != null) {
            writeCommandToChannel(selectedChannel, outgoingCommand);
          }
        } catch(IOException ioException) {
          System.err.println("Error writing to a channel. Closing that channel");
          ioException.printStackTrace();
          try {
            socketChannel.register(clientSelector, SelectionKey.OP_READ);
            selectedChannel.close();
          } catch (IOException e) {
            System.out.println("Error closing that channel");
            e.printStackTrace();
            continue;
          }
          continue;
        }
      }
      keyIterator.remove();
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
  
  private void initializeClient() throws IOException, ClosedChannelException {
    clientSelector = Selector.open();
    socketChannel = SocketChannel.open();
    socketChannel.configureBlocking(false);
    socketChannel.connect(new InetSocketAddress(remoteAddress, remotePort));
    
    socketChannel.register(clientSelector, SelectionKey.OP_CONNECT);
  }
  
  public void writeCommandToChannel(SocketChannel selectedChannel, ICommand command) throws IOException {
    ByteBuffer buffer = asciiEncoder.encode(command.asCharBuffer());
    while(buffer.hasRemaining()) {
        selectedChannel.write(buffer);
    }    
    buffer.clear();
  }


  public void addOutgoingCommand(ICommand command) {
    // TODO Auto-generated method stub
    
  }
   
}
