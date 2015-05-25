package tk.bad_rabbit.rcam.distributed_backend.client;

public interface IClient extends  Runnable {
  public void record();
  public void startClientThread();
  //private void addOutgoingCommand(ICommand command);
}
