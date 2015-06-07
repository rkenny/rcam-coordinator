package tk.bad_rabbit.rcam.distributed_backend.commandfactory;

import java.nio.CharBuffer;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public interface ICommandFactory {
  ICommand createCommand(CharBuffer commandBuffer);
  ICommand createCommand(String command);
  ICommand createAckCommand(ICommand incomingCommand);

}
