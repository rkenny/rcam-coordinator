package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;

public class Command implements ICommand {
  private List<String> commandString;
  private String commandName;
  private Integer commandAckNumber;
  private Map<String, String> clientVariables;
  private Map<String, String> commandVariables;
  private Map<String, String> serverVariables;
  
  
  public Command(String commandName, Integer commandAckNumber, List<String> commandString, Map<String, String> clientVariables,
      Map<String, String> commandVariables, Map<String, String> serverVariables) {
    this.commandName = commandName;
    this.commandString = commandString;
    this.commandAckNumber = commandAckNumber;
    this.clientVariables = clientVariables;
    this.commandVariables = commandVariables;
    this.serverVariables = serverVariables;
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

  public CommandResult call() throws Exception {
    return new CommandResult(commandName).setSuccess();//commandName + " " + finalizeCommandString();
  }

}

