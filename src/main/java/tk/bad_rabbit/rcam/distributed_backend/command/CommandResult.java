package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;

public class CommandResult implements ICommand {
  public String commandType;
  private Boolean success;
  
  public CommandResult(String commandType) {
    this.commandType = commandType;
    success = false;
  }
  
  
  public CommandResult setSuccess() {
    this.success = true;
    return this;
  }
  
  public String toString() {
    return "this was toStringed.";
  }
  
  public String notificationCommand()  { 
    return success ? commandType + "(Success)" : commandType + "(Fail)";
  }


  public CommandResult call() throws Exception {
    // TODO Auto-generated method stub
    return null;
  }


  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
    return CharBuffer.wrap(notificationCommand());
  }
}
