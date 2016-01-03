package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;
import java.util.Map;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.Callable;

import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;


public interface ICommand  {
  public CharBuffer asCharBuffer();
  public Boolean isIgnored();
  public Integer getAckNumber();
  public String getCommandName();
  
  public String getOrigin();
  public void setOrigin(String origin);
  
  
  public Object getClientVariable(String variableName);
  public Object getServerVariable(String variableName);
  
  public void doNetworkAction(Observer actionObserver, String server);
  public void doRelatedCommandAction(Observer actionObserver, String server);
  public void doRunCommandAction(Observer actionObserver, String server);
  
  //public void setCommandResponseRelatedAction(ACommandResponseAction newAction);
  //public void performCommandResponseNetworkAction(String server, Observer actionObject);
  //public void performCommandResponseRelatedAction(String server, Observer actionObject);

  public Boolean isType(String commandType);
  
  public ICommandState getState(String server);
  public void setState(ICommandState state);
  public void setState(String server, ICommandState state);
  
  public void setServers(Set<String> servers);
  
  

  
  public Boolean stateEquals(String server, ICommandState comparisonState);
  //public void setErrorState();
  //public void setReducedState();
  
  public void setReturnCode(Integer returnCode);
  public Integer getReturnCode();

  //public void doNetworkAction(Observer actionObject, String server);
  // public void doRelatedCommandAction(Observer actionObserver, ACommand relatedCommand);
  
  public Boolean isReadyToReduce();
  
  public Callable<Map.Entry<Integer, Integer>> reduce();
}
