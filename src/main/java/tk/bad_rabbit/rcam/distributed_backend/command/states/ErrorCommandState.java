package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.CancelCommandAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.NetworkNotificationAction;


public class ErrorCommandState  extends ACommandState {


  public ErrorCommandState() {
    setNetworkResponseAction(new NetworkNotificationAction());
    setRelatedCommandResponseAction(new CancelCommandAction());
  }
  
  public void doRelatedCommandAction(Observer actionObserver, String server, ACommand actionSubject) {
    getRelatedCommandResponseAction().doStuff(actionObserver, server, actionSubject);
  }
  
  ICommandResponseAction networkResponseAction;
  ICommandResponseAction relatedCommandAction;

  
  public void nextState(String server, ACommand actionSubject) {}
  
  
  public ICommandResponseAction getNetworkResponseAction() {
    return networkResponseAction;
  }
  
  public void setNetworkResponseAction(ICommandResponseAction newNetworkResponseAction) {
    this.networkResponseAction = newNetworkResponseAction;
  }
  
  
  public void setRelatedCommandResponseAction(ICommandResponseAction newRelatedCommandResponseAction) {
    this.relatedCommandAction = newRelatedCommandResponseAction;
  }
  
  
  public ICommandResponseAction getRelatedCommandResponseAction() {
    return relatedCommandAction;
  }

  ICommandResponseAction runCommandAction;
  public ICommandResponseAction getRunCommandResponseAction() { return this.runCommandAction; }
  public void setRunCommandResponseAction(ICommandResponseAction newRunCommandAction) { this.runCommandAction = newRunCommandAction; }

  
  public ICommandState getNextState() {
    return new DoneState(); // needs a CancellingState.
  }

}
