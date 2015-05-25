package tk.bad_rabbit.rcam.distributed_backend.commandqueuer;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public interface ICommandQueuer {
  public void addIncomingCommand(ICommand command);
  public void addOutgoingCommand(ICommand command);
  
  public ICommand getNextIncomingCommand();
  public ICommand getNextOutgoingCommand();
  
}
