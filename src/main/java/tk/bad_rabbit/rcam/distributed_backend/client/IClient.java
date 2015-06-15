package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.Queue;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public interface IClient extends  Runnable {
  public void startClientThread();
  public void addOutgoingCommand(ICommand command);
  public void joinIncomingCommandQueue(Queue<ICommand> commandQueue);
  public void joinOutgoingCommandQueue(Queue<ICommand> commandQueue);
}
