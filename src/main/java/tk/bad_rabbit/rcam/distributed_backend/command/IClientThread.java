package tk.bad_rabbit.rcam.distributed_backend.command;


public interface IClientThread {
  public void send(ACommand command);
  public void ackCommandReceived(String server, int ackNumber);
  public void commandResultReceived(String server, int ackNumber, String resultCode);
  public void removeCommand(String server, ACommand command);
  public void readyToReduce(String server, ACommand command);
}
