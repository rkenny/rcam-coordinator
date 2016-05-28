package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.nio.CharBuffer;
import java.util.concurrent.Semaphore;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.IResponseAction;

public abstract class AResponseAction implements ICommandAction, IResponseAction {

  String commandName;
  Integer ackNumber;
  JSONObject clientVariables;
 
  ACommand command;
  
  public AResponseAction() {
   super();
  }
  
  public void setCommandDetails(ACommand command) {
    synchronized(command) {
      commandName = command.getCommandName();
      ackNumber = command.getAckNumber();
      if(command.getClientVariables() != null) { // code smell.
        clientVariables = command.getClientVariables();
      }
      this.command = command;
    }
  }
  
  public ACommand getCommand() {
    return this.command;
  }
  
  
  public String getCommandName() {
    return commandName;
  }
  
  public Integer getAckNumber() {
    return ackNumber;
  }
  
  public JSONObject getClientVariables() {
    return clientVariables;
  }
  
  public CharBuffer asCharBuffer() {
    JSONObject details = new JSONObject();
    details.put("commandName", commandName);
    details.put("ackNumber", ackNumber);
    details.put("details", clientVariables);
    return CharBuffer.wrap(details.toString()+"\n"); 
  }
  
}
