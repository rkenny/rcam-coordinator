package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public abstract class ACommandResponseAction implements ICommandResponseAction {
  
  abstract public void doStuff(Observer actionObject, String server, ACommand actionSubject);
  public void nextState(String server, ACommand command) {}
}
