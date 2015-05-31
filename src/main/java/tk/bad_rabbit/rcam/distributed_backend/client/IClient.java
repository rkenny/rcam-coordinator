package tk.bad_rabbit.rcam.distributed_backend.client;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public interface IClient extends  Runnable {
  public void startClientThread();
  public void addOutgoingCommand(ICommand command);
}
