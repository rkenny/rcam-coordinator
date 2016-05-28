package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observable;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ACommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ANetworkResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ARunResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;

public class CommandSentState extends ACommandState {

  //public void nextState(String server, ACommand actionSubject) {
  //  actionSubject.setState(server, new AwaitingAckState());
  //}
  
  ANetworkResponseAction networkResponseAction;
  ACommandResponseAction relatedCommandAction;
  ARunResponseAction runCommandAction;
  
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

  
  public ARunResponseAction getRunCommandResponseAction() { return this.runCommandAction; }
  public void setRunCommandResponseAction(ARunResponseAction newRunCommandAction) { this.runCommandAction = newRunCommandAction; }

  public ACommandState getNextState() {
    return new AwaitingAckState();
  }
  
  public void update(Observable observedAction, Object actionClass) {
    System.out.println(this.getClass().getSimpleName() + " - Observed a change in " + observedAction.getClass().getSimpleName() + " it is " + actionClass);
  }


}
