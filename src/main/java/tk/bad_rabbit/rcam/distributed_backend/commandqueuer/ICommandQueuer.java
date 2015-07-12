package tk.bad_rabbit.rcam.distributed_backend.commandqueuer;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import tk.bad_rabbit.rcam.distributed_backend.client.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.CommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.Pair;
import tk.bad_rabbit.rcam.distributed_backend.command.StateObject;

public interface ICommandQueuer {
  public void addIncomingCommand(String server, ACommand command);
  public void addOutgoingCommand(String server, ACommand command);
  
  //public ICommand getNextIncomingCommand(String server);
  //public ICommand getNextOutgoingCommand(String server);
  
  public ACommand getNextIncomingCommand(String server, StateObject state);
  public ACommand getNextOutgoingCommand(String server, StateObject state);
  
  public void removeOutgoingCommand(ACommand command);
  
  public List<Pair<String, ACommand>> getNextIncomingCommandResults();
  
  public Map<Integer, ACommand> getIncomingCommandQueue(String server);
  public Map<Integer, ACommand> getOutgoingCommandQueue(String server);
  public void setResultCodeForCommand(String server, Integer ackNumber, String returnCode);
  public void setStateForCommand(String server, Integer ackNumber, StateObject commandState);
  
  public void setResultCodeForCommand(String server, String ackNumber, String returnCode);
  public void setStateForCommand(String server, String ackNumber, StateObject commandState);
  public Collection<Pair<ACommand, String>> getCommandReturnCode();
  
}
