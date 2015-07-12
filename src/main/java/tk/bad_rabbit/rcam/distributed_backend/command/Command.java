package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;

import tk.bad_rabbit.rcam.distributed_backend.client.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.client.IClient;

public class Command extends ACommand {
  private List<String> commandString;
  private String commandName;
  private Integer commandAckNumber;
  private Map<String, String> clientVariables;
  private Map<String, String> commandVariables;
  private Map<String, String> serverVariables;
  private ICommandResponseAction commandResponseAction;
  
  private StateObject state;
  
  private String returnCode;
  
  public void setReturnCode(String returnCode) {
    this.returnCode = returnCode;
  }
  
  public String getReturnCode() {
    return this.returnCode;
  }
  
  
  public void performCommandResponseAction(Object actionObject) {
    this.commandResponseAction.doAction(actionObject, this);
  }
  
  public Command() {
  }
  
  public Command(String commandName, Integer commandAckNumber, List<String> commandString, Map<String, String> clientVariables,
      Map<String, String> commandVariables, Map<String, String> serverVariables, ICommandResponseAction commandResponseAction) {
    this();
    this.commandName = commandName;
    this.commandString = commandString;
    this.commandAckNumber = commandAckNumber;
    this.clientVariables = clientVariables;
    this.commandVariables = commandVariables;
    this.serverVariables = serverVariables;
    
    this.commandResponseAction = commandResponseAction;
  }
  
  public ACommand copy() {
    ACommand copiedCommand;
    copiedCommand = new Command(this.commandName, this.commandAckNumber, this.commandString, this.clientVariables,
        this.commandVariables, this.serverVariables, this.commandResponseAction);
    copiedCommand.setState(this.state);
    return copiedCommand;
  }
  
  public String getCommandVariable(String variableName) {
    return this.commandVariables.get(variableName);
  }
  public String getClientVariable(String variableName) {
    return this.clientVariables.get(variableName);
  }
  public String getServerVariable(String variableName) {
    return this.serverVariables.get(variableName);
  }
  
  
  public StateObject setState(StateObject state) {
    this.state = state;
    
    setChanged();
    notifyObservers(state);
    
    return state;
  }
  
  public void doAction(Object actionObject) {
    state.doAction(actionObject, this);
  }
  
  public Boolean isType(String commandType) {
    return this.commandName.equals(commandType);
  }
  
  public Boolean isIgnored() {
    return(commandVariables.get("ignored") == "true");
  }
  
  
  public String finalizeCommandString() {
    String finalCommandString = commandString.toString();
    for(String key : clientVariables.keySet()) {
      finalCommandString = finalCommandString.replace("&"+key, clientVariables.get(key));
    }
    for(String key : commandVariables.keySet()) {
      finalCommandString = finalCommandString.replace("@"+key, commandVariables.get(key));
    }
    for(String key : serverVariables.keySet()) {
      finalCommandString = finalCommandString.replace("$"+key, serverVariables.get(key));
    }
    finalCommandString = finalCommandString.replaceFirst("\\[", "(");
    finalCommandString = finalCommandString.substring(0, finalCommandString.length() - 1).concat(")");

    return finalCommandString;
  }

  public String getCommandName() {
    return commandName;
  }

  public Integer getAckNumber() {
    return commandAckNumber;
  }
  
  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
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

