package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.nio.CharBuffer;
import java.util.concurrent.Callable;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

public interface ICommandAction  {
  
  public void setCommandDetails(ACommand command);
  public void takeNecessaryInfo(IConfigurationProvider configurationProvider);
  public CharBuffer asCharBuffer();
  
  public Callable<ICommandAction> nextAction();
}
