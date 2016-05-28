package tk.bad_rabbit.rcam.distributed_backend.command;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ACommandState;
import tk.bad_rabbit.rcam.spring.commands.CommandController;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;


public interface ICommand  {
  //public CharBuffer asCharBuffer();
  //public Boolean isIgnored();
  public Integer getAckNumber();
  public String getCommandName();
  public JSONObject getClientVariables();
  
  //public Object getClientVariable(String variableName);
  //public Object getServerVariable(String variableName);
  //public Object getConfigurationObject(String variableName);
  
  public Future<Integer> doNetworkAction(ServerThread actionObserver, String server);
  public Future<Integer> doRelatedCommandAction(CommandController actionObserver, String server);
  public Future<Integer> doRunCommandAction(RunController actionObserver, ACommandState commandState);
  
  //public void setCommandResponseRelatedAction(ACommandResponseAction newAction);
  //public void performCommandResponseNetworkAction(String server, Observer actionObject);
  //public void performCommandResponseRelatedAction(String server, Observer actionObject);

  public Boolean isType(String commandType);
  
  public ACommandState getState(String server);
  public void setState(ACommandState state);
  public void setState(String server, ACommandState state);
  //public void nextState(String server);
  
  public void setServers(Set<String> servers);
  
  

  
  public Boolean stateEquals(String server, ACommandState comparisonState);
  public Map<String, ACommandState> getStates();
  //public void setErrorState();
  //public void setReducedState();
  
  //public void setReturnCode(Integer returnCode);
  //public Integer getReturnCode();

  //public void doNetworkAction(Observer actionObject, String server);
  // public void doRelatedCommandAction(Observer actionObserver, ACommand relatedCommand);
  
  //public Boolean isReadyToReduce();
  
  //public Callable<Map.Entry<Integer, Integer>> run(final String scriptToRun);
  //public Callable<Void> run(final String scriptToRun);
}
