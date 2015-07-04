package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;
import java.util.concurrent.Callable;

public interface ICommand {
  public CharBuffer asCharBuffer();
  public Boolean isIgnored();
  public Integer getAckNumber();
  public String getCommandName();
  
  public String getCommandVariable(String variableName);
  public String getClientVariable(String variableName);
  public String getServerVariable(String variableName);

  public ICommand wasReceived();
  public ICommand wasAcked();
  public ICommand readyToSend();
  public ICommand wasSent();
  public ICommand commandError();
  
  public Boolean isType(String commandType);
  public Boolean isReadyToSend();
  public Boolean isInState(CommandState state);
  public ICommand copy();
  public CommandState setState(CommandState state);
  
  public void setReturnCode(String returnCode);
  public String getReturnCode();
  
  public Runnable reduce();

}
