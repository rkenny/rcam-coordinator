package tk.bad_rabbit.rcam.distributed_backend.command.states;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.SetRelatedCommandsToReduceRunningResponseAction;

public class CommandReduceRunningState extends ACommandState {
  
  
  public CommandReduceRunningState() {
    setRelatedCommandResponseAction(new SetRelatedCommandsToReduceRunningResponseAction());
  }
  
  public void nextState(String server, ACommand actionSubject) {}
  
  ICommandResponseAction networkResponseAction;
  public ICommandResponseAction getNetworkResponseAction() { return networkResponseAction; }
  public void setNetworkResponseAction(ICommandResponseAction newNetworkResponseAction) {this.networkResponseAction = newNetworkResponseAction; }
  
  
  ICommandResponseAction relatedCommandAction;
  public ICommandResponseAction getRelatedCommandResponseAction() { return relatedCommandAction; }
  public void setRelatedCommandResponseAction(ICommandResponseAction newRelatedCommandResponseAction) { this.relatedCommandAction = newRelatedCommandResponseAction; }

  ICommandResponseAction runCommandAction;
  public ICommandResponseAction getRunCommandResponseAction() { return this.runCommandAction; }
  public void setRunCommandResponseAction(ICommandResponseAction newRunCommandAction) { this.runCommandAction = newRunCommandAction; }

  public ICommandState getNextState() {
    return new CommandReducedState();
  }
  
}
