package tk.bad_rabbit.rcam.distributed_backend.command;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.client.states.AClientState;


public interface IClientThread {
  public void send(ACommand command);
  
  public void ackCommandReceived(ACommand command);
  public void commandResultReceived(Integer ackNumber, Integer resultCode);
  public void removeCommand(ACommand command);
  public void readyToReduce(ACommand command);
  
  public AClientState setState(AClientState clientState);
  public void doAction(Observer actionObserver);

  public String getServerString();
}
