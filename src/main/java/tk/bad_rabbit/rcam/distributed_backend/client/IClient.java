package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.Map;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public interface IClient extends  Runnable {
  public void startClientThread();
  public void addOutgoingCommand(ICommand command);
  public void joinIncomingCommandQueue(Map<Integer, ICommand> commandQueue);
  public void joinOutgoingCommandQueue(Map<Integer, ICommand> commandQueue);
}
