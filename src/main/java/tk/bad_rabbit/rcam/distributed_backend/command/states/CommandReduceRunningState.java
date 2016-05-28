package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observable;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ACommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ANetworkResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ARunResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.SetRelatedCommandsToReduceRunningResponseAction;

public class CommandReduceRunningState extends ACommandState {
  
  
  public CommandReduceRunningState() {
    setRelatedCommandResponseAction(new SetRelatedCommandsToReduceRunningResponseAction());
  }
  
  public void nextState(String server, ACommand actionSubject) {}
  
  ANetworkResponseAction networkResponseAction;
  public ANetworkResponseAction getNetworkResponseAction() { return networkResponseAction; }
  public void setNetworkResponseAction(ANetworkResponseAction newNetworkResponseAction) {this.networkResponseAction = newNetworkResponseAction; }
  
  
  ACommandResponseAction relatedCommandAction;
  public ACommandResponseAction getRelatedCommandResponseAction() { return relatedCommandAction; }
  public void setRelatedCommandResponseAction(ACommandResponseAction newRelatedCommandResponseAction) { this.relatedCommandAction = newRelatedCommandResponseAction; }

  ARunResponseAction runCommandAction;
  public ARunResponseAction getRunCommandResponseAction() { return this.runCommandAction; }
  public void setRunCommandResponseAction(ARunResponseAction newRunCommandAction) { this.runCommandAction = newRunCommandAction; }

  public ACommandState getNextState() {
    return new CommandReducedState();
  }
  
  public void update(Observable observedAction, Object actionClass) {
    System.out.println(this.getClass().getSimpleName() + " - Observed a change in " + observedAction.getClass().getSimpleName() + " it is " + actionClass);
  }

  
}
