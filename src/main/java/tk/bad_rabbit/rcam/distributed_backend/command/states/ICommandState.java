package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public interface ICommandState {

  public void doNetworkAction(Observer actionObserver, String server, ACommand actionSubject);
  abstract void nextState(String server, ACommand actionSubject);
  abstract void doNetworkStuff(Observer actionObserver, String server, ACommand actionSubject);
  
  public void doRelatedCommandAction(Observer actionObserver, String server, ACommand actionSubject);
  abstract void doRelatedCommandStuff(Observer actionObserver, String server, ACommand actionSubject);
  
  public Boolean typeEquals(ICommandState comparisonState);
}
