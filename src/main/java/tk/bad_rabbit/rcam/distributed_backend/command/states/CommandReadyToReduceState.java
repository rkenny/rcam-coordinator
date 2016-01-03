package tk.bad_rabbit.rcam.distributed_backend.command.states;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ReduceCommandResponseAction;

public class CommandReadyToReduceState extends ACommandState {
  //public void doNetworkStuff(Observer observer, String server, ACommand actionSubject) {}

  
  //public void doRelatedCommandStuff(Observer actionObserver, String server, ACommand actionSubject) {
  //  System.out.println("RCam Coordinator - CommandReadyToReduceState - When all servers report CommandReadyToReduce, start reducing");
  //}
  
  ICommandResponseAction networkResponseAction;
  ICommandResponseAction relatedCommandAction;
  
  public CommandReadyToReduceState() {
    setRunCommandResponseAction(new ReduceCommandResponseAction());
  }
  
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


  
  
}
