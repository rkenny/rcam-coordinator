package tk.bad_rabbit.rcam.distributed_backend.commandqueuer;

import java.util.Queue;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public interface ICommandQueuer {
  public void addIncomingCommand(String server, ICommand command);
  public void addOutgoingCommand(String server, ICommand command);
  
  public ICommand getNextIncomingCommand(String server);
  public ICommand getNextOutgoingCommand(String server);
  
  public Queue<ICommand> getIncomingCommandQueue(String server);
  public Queue<ICommand> getOutgoingCommandQueue(String server);
}
