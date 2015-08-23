package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observer;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReadyToReduceState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;

public class Command extends ACommand {
  //private String commandString;
  private String commandName;
  private Integer commandAckNumber;
  private JSONObject clientVariables;
  //private JSONObject commandVariables;
  private JSONObject commandConfiguration;
  private JSONObject serverVariables;
  private ICommandResponseAction commandResponseAction;
  
  private volatile Map<String, ICommandState> state;
  
  private Integer returnCode;
  
  public void setReturnCode(Integer returnCode) {
    this.returnCode = returnCode;
  }
  
  public Integer getReturnCode() {
    return this.returnCode;
  }
  
  
  public void performCommandResponseAction(String server, Object actionObject) {
    this.commandResponseAction.doAction(actionObject, server, this);
  }
  
  public Command() {
    this.state = new HashMap<String, ICommandState>();
  }
  
  public Command(String commandName, Integer commandAckNumber, JSONObject commandConfiguration,
      JSONObject clientVariables, JSONObject serverVariables, ICommandResponseAction commandResponseAction) {
    this();
    this.commandName = commandName;
    this.commandConfiguration = commandConfiguration;
    this.commandAckNumber = commandAckNumber;
    this.clientVariables = clientVariables;
    //this.commandVariables = commandVariables;
    this.serverVariables = serverVariables;
    
    this.commandResponseAction = commandResponseAction;
    
    
    
    System.out.println("Created command " + commandName + "[" + commandAckNumber + "]");
    //System.out.println("The commandString is " + commandString.toString());
  }
  
//  public Object getCommandVariable(String variableName) {
//    return this.commandVariables.get(variableName);
//  }
  
  public Object getClientVariable(String variableName) {
    return this.clientVariables.get(variableName);
  }
  
  public Object getServerVariable(String variableName) {
    return this.serverVariables.get(variableName);
  }
  
  
  public synchronized ICommandState setState(String server, ICommandState state) {
  
    this.state.put(server,  state);
    
    Entry<String, ICommandState> doesThisWork = new AbstractMap.SimpleEntry<String, ICommandState>(server, state);
    Entry<ACommand, Entry<String, ICommandState>> really = new AbstractMap.SimpleEntry<ACommand, Entry<String, ICommandState>>(this, doesThisWork);
    
    setChanged();
    notifyObservers(really);
    
    return state;
  }
  
  public void doAction(Observer actionObject, String server) {
    System.out.println("About to doAction for server " + server + " on command " + getAckNumber() + " with state " + this.state.get(server).getClass().getSimpleName());
    state.get(server).doAction(actionObject, server, this);
  }
  

  public Boolean isType(String commandType) {
    return this.commandName.equals(commandType);
  }
  
  public Boolean isIgnored() {
    return(commandConfiguration.getJSONObject("commandVars").get("ignored") == "true");
  }
  
  public Boolean isReadyToReduce() {
    Set<Entry<String, ICommandState>> servers = this.state.entrySet();
    Iterator<Entry<String, ICommandState>> serversIterator = servers.iterator();
    CommandReadyToReduceState readyToReduceState = new CommandReadyToReduceState();
    Boolean isReadyToReduce = true;
    while(serversIterator.hasNext()) {
     ICommandState serverState = serversIterator.next().getValue();
     if(!serverState.typeEquals(readyToReduceState)) {
       isReadyToReduce = false;
     }
    }
    
    System.out.println("Is command ready to reduce? " + isReadyToReduce);
    return isReadyToReduce;
  }
  
  
  public String finalizeCommandString() {
    StringBuilder finalCommandString = new StringBuilder();
    //System.out.println(clientVariables);
    System.out.println("finalCommandString is " + finalCommandString + " before the replaces");
    finalCommandString.append("{");
    if(commandConfiguration.has("clientVars")) {
      for(int i = 0; i < commandConfiguration.getJSONArray("clientVars").length(); i++) {
        finalCommandString.append("\""+commandConfiguration.getJSONArray("clientVars").get(i).toString()+"\":");
        finalCommandString.append("\""+clientVariables.get(commandConfiguration.getJSONArray("clientVars").get(i).toString())+"\"");
        finalCommandString.append(",");
      }
    }
    //while(variableIterator.hasNext()) {
    //  String key = variableIterator.next();
      //finalCommandString = finalCommandString.replace("&"+key, clientVariables.get(key).toString());
    //  finalCommandString
    //}

    Iterator<String> variableIterator = commandConfiguration.getJSONObject("commandVars").keys();
    while(variableIterator.hasNext()) {
      String key = variableIterator.next();
      //finalCommandString = finalCommandString.replace("@"+key, commandVariables.get(key).toString());
      finalCommandString.append("\"" + key + "\":\"" + commandConfiguration.getJSONObject("commandVars").get(key).toString()+"\"");
      finalCommandString.append(",");
    }
    
    //variableIterator = serverVariables.keys();
    //while(variableIterator.hasNext()) {
    //  String key = variableIterator.next();
    //  finalCommandString = finalCommandString.replace("$"+key, serverVariables.get(key).toString());
   // }
    
    if(commandConfiguration.has("serverVars")) {
      for(int i = 0; i < commandConfiguration.getJSONArray("serverVars").length(); i++) {
      finalCommandString.append("\""+commandConfiguration.getJSONArray("serverVars").get(i).toString()+"\":");
      finalCommandString.append("\""+clientVariables.get(commandConfiguration.getJSONArray("serverVars").get(i).toString())+"\"");
      finalCommandString.append(",");
      }
    }
    
    finalCommandString.deleteCharAt(finalCommandString.length()-1);
    finalCommandString.append("}");
    
    System.out.println("FinalCommandString after is " + finalCommandString + " after the replaces");
    return finalCommandString.toString();
  }

  public String getCommandName() {
    return commandName;
  }

  public Integer getAckNumber() {
    return commandAckNumber;
  }
  
  public CharBuffer asCharBuffer() {
    System.out.println("Command.asCharBuffer: " + commandName + "[" + commandAckNumber.toString() +"]" + finalizeCommandString());
    return CharBuffer.wrap(commandName + "[" + commandAckNumber.toString() +"]" + finalizeCommandString());
  }
  
  public Runnable reduce() {
    return new Runnable() {

      public void run() {
        System.out.println("Reducing " + commandName + " by running " + commandConfiguration.get("reductionCommand").toString());

      }
      
    };
  }

}

