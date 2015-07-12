package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.AckedState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.AwaitingAckState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.CommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.ConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class Client implements IClient  {

  String remoteAddress;
  int remotePort;
  
  public void observeCommand(ACommand command) {
    newClientThread.observeCommand(command);
  }

  
  
  public void send(ACommand command) {
    sendCommand(command);
    command.setState(new AwaitingAckState());
  }

  private void sendCommand(ACommand command) {
    synchronized(command) {
      newClientThread.send(command);
    }
  }
  
  public void sendAck(ACommand command) {
    sendCommand(commandFactory.createAckCommand(command));
    command.setState(new AckedState());
  }
  
  public void setRemoteAddress(String remoteAddress) {
    this.remoteAddress = remoteAddress;
  }
  
  public void setPort(int remotePort) {
    this.remotePort = remotePort;
  }
  
  
  ICommandFactory commandFactory;
  
  IConfigurationProvider configurationProvider;
  
  RunController runController;
  ClientThread newClientThread;
  ConcurrentLinkedQueue<ACommand> commandExchangeQueue;

  
  public Client() {
    this.commandExchangeQueue = new ConcurrentLinkedQueue<ACommand>();
  }
  
  public Client(String remoteAddress, int remotePort) {
    this();
    
    this.remoteAddress = remoteAddress;
    this.remotePort = remotePort;
  }
  
  public Client(String remoteAddress, Integer remotePort, CommandFactory commandFactory2, ConfigurationProvider configurationProvider, RunController runController) {
    this(remoteAddress, remotePort);
    this.configurationProvider = configurationProvider;
    this.commandFactory = commandFactory2;
    this.runController = runController;
    this.newClientThread = new ClientThread(runController, commandFactory, remoteAddress, remotePort);
    
  }

  public void startClientThread() {
    newClientThread.start();
  }
  
  
   
}
