package tk.bad_rabbit.rcam.distributed_backend.commandfactory;

import java.nio.CharBuffer;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public interface ICommandFactory {
  ACommand createCommand(CharBuffer commandBuffer);
  //ACommand createCommand(CharBuffer commandBuffer, String server);
  ACommand createCommand(String command, JSONObject details);
  
  //ACommand createAckCommand(ACommand incomingCommand);
  //ACommand createCancelCommand(ACommand command);
  //ACommand createReductionCompleteCommand(ACommand command);
}
