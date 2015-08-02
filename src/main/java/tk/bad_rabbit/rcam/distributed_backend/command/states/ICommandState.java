package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public interface ICommandState {

  public void doAction(Observer actionObserver, String server, ACommand actionSubject);
  public Boolean typeEquals(ICommandState comparisonState);
}
