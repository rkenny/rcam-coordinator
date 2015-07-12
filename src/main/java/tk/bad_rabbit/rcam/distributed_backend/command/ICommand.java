package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;

import tk.bad_rabbit.rcam.distributed_backend.client.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.client.IClient;


public interface ICommand {
  public CharBuffer asCharBuffer();
  public Boolean isIgnored();
  public Integer getAckNumber();
  public String getCommandName();
  
  public String getCommandVariable(String variableName);
  public String getClientVariable(String variableName);
  public String getServerVariable(String variableName);

  public void performCommandResponseAction(Object actionObject);
//  public ACommand wasReceived();
//  public ACommand wasAcked();
//  public ICommand readyToSend();
//  public ACommand wasSent();
//  public ACommand commandError();
  
  public Boolean isType(String commandType);
  
  public ACommand copy();
  public StateObject setState(StateObject state);
  
  public void setReturnCode(String returnCode);
  public String getReturnCode();

  public void doAction(Object actionObject);
  
  public Runnable reduce();

}
