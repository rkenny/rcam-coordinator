package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.DefaultCommandResponseAction;

public abstract class ACommandState implements ICommandState {
  public ACommandState() {
    setNetworkResponseAction(new DefaultCommandResponseAction());
    setRelatedCommandResponseAction(new DefaultCommandResponseAction());
    setRunCommandResponseAction(new DefaultCommandResponseAction());
  }
  
  
  public void doNetworkAction(Observer actionObserver, String server, ACommand actionSubject) {
    getNetworkResponseAction().doStuff(actionObserver, server, actionSubject);
  }
  
  public void doRelatedCommandAction(Observer actionObserver, String server, ACommand actionSubject) {
    getRelatedCommandResponseAction().doStuff(actionObserver, server, actionSubject);
  }
  
  public void doRunCommandAction(Observer actionObserver, String server, ACommand actionSubject) {
    getRunCommandResponseAction().doStuff(actionObserver, server, actionSubject);
  }
  
  @Override
  public boolean equals(Object comparisonState) {
    if(comparisonState instanceof ICommandState) {
      return (getClass().getSimpleName().equals(comparisonState.getClass().getSimpleName()));  
    }
    return false;
  }  
  
  public Boolean typeEquals(ICommandState comparisonState) {
    return this.getClass().getSimpleName().equals(comparisonState.getClass().getSimpleName());
  }

  public void nextState(String server, ACommand actionSubject) {}
  
}
