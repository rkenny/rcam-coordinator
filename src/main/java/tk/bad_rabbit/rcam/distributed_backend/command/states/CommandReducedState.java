package tk.bad_rabbit.rcam.distributed_backend.command.states;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.SendReductionCompleteNetworkAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.SetRelatedCommandsToDoneResponseAction;

public class CommandReducedState extends ACommandState {

  //public void doAction(Observer observer, String server, ACommand actionSubject) {
  //  if(observer instanceof ClientThread) {
  //    ((ClientThread) observer).removeCommand((ACommand) actionSubject);
  //  }
  //}
  ICommandResponseAction networkResponseAction;
  ICommandResponseAction relatedCommandAction;
  ICommandResponseAction runCommandAction;
  
  public CommandReducedState() {
    setRelatedCommandResponseAction(new SetRelatedCommandsToDoneResponseAction());
    setNetworkResponseAction(new SendReductionCompleteNetworkAction());
  }
  
  public void nextState(String server, ACommand actionSubject) {}
  
  
  public ICommandResponseAction getNetworkResponseAction() { return networkResponseAction; }
  public void setNetworkResponseAction(ICommandResponseAction newNetworkResponseAction) {this.networkResponseAction = newNetworkResponseAction; }
  
  
  
  public ICommandResponseAction getRelatedCommandResponseAction() { return relatedCommandAction; }
  public void setRelatedCommandResponseAction(ICommandResponseAction newRelatedCommandResponseAction) { this.relatedCommandAction = newRelatedCommandResponseAction; }

  
  public ICommandResponseAction getRunCommandResponseAction() { return this.runCommandAction; }
  public void setRunCommandResponseAction(ICommandResponseAction newRunCommandAction) { this.runCommandAction = newRunCommandAction; }

  

}
