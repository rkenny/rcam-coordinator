package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observable;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ACommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ANetworkResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ARunResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.CancelCommandAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.NetworkNotificationAction;
import tk.bad_rabbit.rcam.spring.commands.CommandController;


public class ErrorCommandState {  //extends ACommandState {


  public ErrorCommandState() {
    // setNetworkResponseAction(new NetworkNotificationAction());
    setRelatedCommandResponseAction(new CancelCommandAction());
  }
  
  public Future<Integer> doRelatedCommandAction(CommandController actionObserver, String server, ACommand actionSubject) {
    return getRelatedCommandResponseAction().doRelatedAction(actionObserver, server, actionSubject);
  }
  
  ANetworkResponseAction networkResponseAction;
  ACommandResponseAction relatedCommandAction;

  
  public void nextState(String server, ACommand actionSubject) {}
  
  
  public ANetworkResponseAction getNetworkResponseAction() {
    return networkResponseAction;
  }
  
  public void setNetworkResponseAction(ANetworkResponseAction newNetworkResponseAction) {
    this.networkResponseAction = newNetworkResponseAction;
  }
  
  
  public void setRelatedCommandResponseAction(ACommandResponseAction newRelatedCommandResponseAction) {
    this.relatedCommandAction = newRelatedCommandResponseAction;
  }
  
  
  public ACommandResponseAction getRelatedCommandResponseAction() {
    return relatedCommandAction;
  }

  ARunResponseAction runCommandAction;
  public ARunResponseAction getRunCommandResponseAction() { return this.runCommandAction; }
  public void setRunCommandResponseAction(ARunResponseAction newRunCommandAction) { this.runCommandAction = newRunCommandAction; }

  
  public ACommandState getNextState() {
    return new DoneState(); // needs a CancellingState.
  }
  
  public void update(Observable observedAction, Object actionClass) {
    System.out.println(this.getClass().getSimpleName() + " - Observed a change in " + observedAction.getClass().getSimpleName() + " it is " + actionClass);
  }


}
