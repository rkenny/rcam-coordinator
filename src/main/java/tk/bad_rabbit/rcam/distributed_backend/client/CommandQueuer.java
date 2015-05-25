package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.commandqueuer.ICommandQueuer;

public class CommandQueuer implements ICommandQueuer {
  Queue<ICommand> incomingCommandsQueue = null;
  Queue<ICommand> outgoingCommandsQueue = null;
  
  public CommandQueuer() {
    incomingCommandsQueue = new ConcurrentLinkedQueue<ICommand>();
    outgoingCommandsQueue = new ConcurrentLinkedQueue<ICommand>();
  }

  public void addIncomingCommand(ICommand command) {
    incomingCommandsQueue.add(command);

  }

  public void addOutgoingCommand(ICommand command) {
    outgoingCommandsQueue.add(command);
  }

  public ICommand getNextOutgoingCommand() {
    return outgoingCommandsQueue.poll();
  }

  public ICommand getNextIncomingCommand() {
    return incomingCommandsQueue.poll();
  }

}
