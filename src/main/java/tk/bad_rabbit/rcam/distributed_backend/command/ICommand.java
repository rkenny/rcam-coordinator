package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;
import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;


public interface ICommand {
  public CharBuffer asCharBuffer();
  public Boolean isIgnored();
  public Integer getAckNumber();
  public String getCommandName();
  
  //public Object getCommandVariable(String variableName);
  public Object getClientVariable(String variableName);
  public Object getServerVariable(String variableName);

  public void performCommandResponseAction(String server, Object actionObject);
//  public ACommand wasReceived();
//  public ACommand wasAcked();
//  public ICommand readyToSend();
//  public ACommand wasSent();
//  public ACommand commandError();
  
  public Boolean isType(String commandType);
  
  //public ACommand copy();
  public ICommandState setState(String server, ICommandState state);
  
  public void setReturnCode(Integer returnCode);
  public Integer getReturnCode();

  public void doAction(Observer actionObject, String server);
  
  public Boolean isReadyToReduce();
  public Runnable reduce();

}
