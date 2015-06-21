package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;

// this class is looking more and more like it needs to be refactored into a Command similar to Ack()
public class CommandResult implements ICommand {
  public String commandType;
  private Boolean success;
  
  public CommandResult(String commandType) {
    this.commandType = commandType;
    success = false;
  }
  
  public Boolean isReadyToSend() {
    return true;
  }
  
  public ICommand readyToSend() {
    return this;
  }
  public Boolean isInState(CommandState state) {
    return true;
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
  
  public String getCommandName() {
    return commandType;
  }

  public Integer getAckNumber() {
    return null;
  }


  public CommandResult call() throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  public Boolean isIgnored() {
    return false;
  }

  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
    return CharBuffer.wrap(notificationCommand());
  }


  public ICommand wasReceived() {
    // TODO Auto-generated method stub
    return this;
  }


  public ICommand wasAcked() {
    // TODO Auto-generated method stub
    return this;
  }


  public ICommand wasSent() {
    // TODO Auto-generated method stub
    return this;
  }


  public ICommand commandError() {
    // TODO Auto-generated method stub
    return this;
  }
}
