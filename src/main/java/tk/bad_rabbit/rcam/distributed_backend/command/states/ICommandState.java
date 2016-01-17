package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;

public interface ICommandState {
  
  public void doNetworkAction(Observer actionObserver, String server, ACommand actionSubject);
  public void doRelatedCommandAction(Observer actionObserver, String server, ACommand actionSubject);
  
  
  ICommandResponseAction getNetworkResponseAction();
  ICommandResponseAction getRelatedCommandResponseAction();
  
  void setNetworkResponseAction(ICommandResponseAction newNetworkResponseAction);
  void setRelatedCommandResponseAction(ICommandResponseAction newRelatedCommandResponseAction);
  
  ICommandResponseAction getRunCommandResponseAction();
  void setRunCommandResponseAction(ICommandResponseAction newRunCommandResponseAction);
  public void doRunCommandAction(Observer actionObserver, String server, ACommand actionSubject);
  
  abstract void nextState(String server, ACommand actionSubject);
  
  public Boolean typeEquals(ICommandState comparisonState);
}
