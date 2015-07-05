package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class Command extends Observable implements ICommand  {
  private List<String> commandString;
  private String commandName;
  private Integer commandAckNumber;
  private Map<String, String> clientVariables;
  private Map<String, String> commandVariables;
  private Map<String, String> serverVariables;
  private CommandState state;
  private String returnCode;
  
  public void setReturnCode(String returnCode) {
    this.returnCode = returnCode;
  }
  
  public String getReturnCode() {
    return this.returnCode;
  }
  
  
  public Command(String commandName, Integer commandAckNumber, List<String> commandString, Map<String, String> clientVariables,
      Map<String, String> commandVariables, Map<String, String> serverVariables) {
    this.commandName = commandName;
    this.commandString = commandString;
    this.commandAckNumber = commandAckNumber;
    this.clientVariables = clientVariables;
    this.commandVariables = commandVariables;
    this.serverVariables = serverVariables;
    
    this.state = CommandState.NEW;
  }
  public ICommand copy() {
    ICommand copiedCommand;
    copiedCommand = new Command(this.commandName, this.commandAckNumber, this.commandString, this.clientVariables,
        this.commandVariables, this.serverVariables);
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
  
  
  public CommandState setState(CommandState state) {
    this.state = state;
    System.out.println("Command " + this.commandName + " " + this.commandAckNumber + " had it's state changed");
    setChanged();
    notifyObservers(this);
    return state;
  }
  
  public Boolean isType(String commandType) {
    return this.commandName.equals(commandType);
  }
  
  public Boolean isIgnored() {
    return(commandVariables.get("ignored") == "true");
  }
  
  public Boolean isInState(CommandState state) {
    return this.state == state;
  }
  
  public Boolean isReadyToSend() {
    return (isIgnored() && this.state == CommandState.NEW || !isIgnored() && this.state == CommandState.READY_TO_SEND);
  }
  
  public ICommand readyToSend() {
    this.state = CommandState.READY_TO_SEND;
    return this;
  }
  
  public ICommand wasSent() {
    this.state = isIgnored() ? CommandState.SENT : CommandState.AWAITING_ACK;
    return this;
  }
  
  public ICommand wasReceived() {
    this.state = CommandState.RECEIVED;
    return this;
  }
 ;
  public ICommand wasAcked() {
    this.state = CommandState.ACKED;
    return this;
  }
  
  public ICommand commandError() {
    this.state = CommandState.ERROR;
    return this;
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

  //public CommandResult call() throws Exception {
  //  return new CommandResult(commandName).setSuccess();//commandName + " " + finalizeCommandString();
  // }
  
  public Runnable reduce() {
    return new Runnable() {

      public void run() {
        System.out.println("Reducing " + commandName);

      }
      
    };
  }

}

