package tk.bad_rabbit.rcam.distributed_backend.commandqueuer;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import tk.bad_rabbit.rcam.distributed_backend.command.CommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.command.Pair;

public interface ICommandQueuer {
  public void addIncomingCommand(String server, ICommand command);
  public void addOutgoingCommand(String server, ICommand command);
  
  //public ICommand getNextIncomingCommand(String server);
  //public ICommand getNextOutgoingCommand(String server);
  
  public ICommand getNextIncomingCommand(String server, CommandState state);
  public ICommand getNextOutgoingCommand(String server, CommandState state);
  
  public void removeOutgoingCommand(ICommand command);
  
  public List<Pair<String, ICommand>> getNextIncomingCommandResults();
  
  public Map<Integer, ICommand> getIncomingCommandQueue(String server);
  public Map<Integer, ICommand> getOutgoingCommandQueue(String server);
  public void setResultCodeForCommand(String server, Integer ackNumber, String returnCode);
  public void setStateForCommand(String server, Integer ackNumber, CommandState commandState);
  
  public void setResultCodeForCommand(String server, String ackNumber, String returnCode);
  public void setStateForCommand(String server, String ackNumber, CommandState commandState);
  public Collection<Pair<ICommand, String>> getCommandReturnCode();
  
}
