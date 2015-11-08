package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.concurrent.ConcurrentLinkedQueue;

import tk.bad_rabbit.rcam.distributed_backend.client.states.AClientState;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.AckedState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.AwaitingAckState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.CommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.ConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class Client implements IClient  {
  ICommandFactory commandFactory;
  IConfigurationProvider configurationProvider;
  
  RunController runController;
  ClientThread newClientThread;
  ConcurrentLinkedQueue<ACommand> commandExchangeQueue;
  String remoteAddress;
  int remotePort;
  
  public synchronized AClientState setState(AClientState state) {
    this.newClientThread.setState(state);
    return state;
  }
  
  public synchronized void observeCommand(ACommand command) {
    newClientThread.addObserver(command);
    command.addObserver(newClientThread);
  }

  public String getServerString() {
    return remoteAddress + ":"+ new Integer(remotePort).toString();
  }
  
  
  public void send(ACommand command) {
    sendCommand(command);
    command.setState(getServerString(), new AwaitingAckState());
  }

  private void sendCommand(ACommand command) {
    synchronized(command) {
      newClientThread.send(command);
    }
  }
  
  public void sendAck(ACommand command) {
    sendCommand(commandFactory.createAckCommand(command));
    command.setState(getServerString(), new AckedState());
  }
  

  
  public void setRemoteAddress(String remoteAddress) {
    this.remoteAddress = remoteAddress;
  }
  
  public void setPort(int remotePort) {
    this.remotePort = remotePort;
  }
  
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
