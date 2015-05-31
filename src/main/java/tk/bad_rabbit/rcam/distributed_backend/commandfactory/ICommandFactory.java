package tk.bad_rabbit.rcam.distributed_backend.commandfactory;

import java.nio.CharBuffer;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public interface ICommandFactory {
  ICommand createCommand(CharBuffer readFromChannel);
  ICommand createCommand(String command);
}
