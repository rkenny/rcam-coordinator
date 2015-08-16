package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observer;
import java.util.Set;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReadyToReduceState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;

public class Command extends ACommand {
  private List<String> commandString;
  private String commandName;
  private Integer commandAckNumber;
  private JSONObject clientVariables;
  private Map<String, String> commandVariables;
  private Map<String, String> serverVariables;
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
  
  public Command(String commandName, Integer commandAckNumber, List<String> commandString, JSONObject clientVariables,
      Map<String, String> commandVariables, Map<String, String> serverVariables, ICommandResponseAction commandResponseAction) {
    this();
    this.commandName = commandName;
    this.commandString = commandString;
    this.commandAckNumber = commandAckNumber;
    this.clientVariables = clientVariables;
    this.commandVariables = commandVariables;
    this.serverVariables = serverVariables;
    
    this.commandResponseAction = commandResponseAction;
    
    System.out.println("Created command " + commandName + "[" + commandAckNumber + "]" + clientVariables);
  }
  
//  public Command(String commandName, Integer commandAckNumber, List<String> commandString, Map<String, String> clientVariables,
//      Map<String, String> commandVariables, Map<String, String> serverVariables, ICommandResponseAction commandResponseAction) {
//    this();
//    this.commandName = commandName;
//    this.commandString = commandString;
//    this.commandAckNumber = commandAckNumber;
//    this.clientVariables = clientVariables;
//    this.commandVariables = commandVariables;
//    this.serverVariables = serverVariables;
//    
//    this.commandResponseAction = commandResponseAction;
//  }
  
//  public ACommand copy() {
//    ACommand copiedCommand;
//    copiedCommand = new Command(this.commandName, this.commandAckNumber, this.commandString, this.clientVariables,
//        this.commandVariables, this.serverVariables, this.commandResponseAction);
//    copiedCommand.setState(this.state);
//    return copiedCommand;
//  }
  
  public String getCommandVariable(String variableName) {
    return this.commandVariables.get(variableName);
  }
  public Object getClientVariable(String variableName) {
    return this.clientVariables.get(variableName);
  }
  public String getServerVariable(String variableName) {
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
    return(commandVariables.get("ignored") == "true");
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
    String finalCommandString = commandString.toString();
    System.out.println(clientVariables);
    Iterator<String> clientVariableIterator = clientVariables.keys();
    while(clientVariableIterator.hasNext()) {
      String key = clientVariableIterator.next();
      finalCommandString = finalCommandString.replace("&"+key, clientVariables.get(key).toString());
    }

    for(String key : commandVariables.keySet()) {
      finalCommandString = finalCommandString.replace("@"+key, commandVariables.get(key));
    }
    for(String key : serverVariables.keySet()) {
      finalCommandString = finalCommandString.replace("$"+key, serverVariables.get(key));
    }
    finalCommandString = finalCommandString.replaceFirst("\\[", "{");
    finalCommandString = finalCommandString.substring(0, finalCommandString.length() - 1).concat("}");

    return finalCommandString;
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
        System.out.println("Reducing " + commandName);

      }
      
    };
  }

}

