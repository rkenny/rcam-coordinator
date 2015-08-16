package tk.bad_rabbit.rcam.distributed_backend.command;


public interface IClientThread {
  public void send(ACommand command);
  
  public void ackCommandReceived(ACommand command);
  public void commandResultReceived(Integer ackNumber, Integer resultCode);
  public void removeCommand(ACommand command);
  public void readyToReduce(ACommand command);
}
