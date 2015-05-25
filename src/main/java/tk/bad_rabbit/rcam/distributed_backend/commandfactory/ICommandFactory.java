package tk.bad_rabbit.rcam.distributed_backend.commandfactory;

import java.nio.CharBuffer;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public interface ICommandFactory {
  public ICommand createCommand(CharBuffer command);
  public ICommand createCommand(String command);
  
  public ICommand ackCommand();
  public ICommand errorCommand();
}
