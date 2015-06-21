package tk.bad_rabbit.rcam.distributed_backend.commandqueuer;

import java.util.Map;

import tk.bad_rabbit.rcam.distributed_backend.command.CommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public interface ICommandQueuer {
  public void addIncomingCommand(String server, ICommand command);
  public void addOutgoingCommand(String server, ICommand command);
  
  //public ICommand getNextIncomingCommand(String server);
  //public ICommand getNextOutgoingCommand(String server);
  
  public ICommand getNextIncomingCommand(String server, CommandState state);
  public ICommand getNextOutgoingCommand(String server, CommandState state);
  
  public Map<Integer, ICommand> getIncomingCommandQueue(String server);
  public Map<Integer, ICommand> getOutgoingCommandQueue(String server);
}
