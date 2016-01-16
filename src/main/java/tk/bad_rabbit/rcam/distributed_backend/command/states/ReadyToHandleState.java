package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.AckCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ResultCommandResponseAction;

public class ReadyToHandleState extends ACommandState {

  
  public void doRelatedCommandAction(Observer actionObserver, String server, ACommand actionSubject) {
    System.out.println("RCam Coordinator - ReceivedCommandState - this is the command name: ["+actionSubject.getCommandName()+"]");
    System.out.println("RCam Coordinator - This will happen on the following observer: " + actionObserver.getClass().getSimpleName());

    if(actionSubject.getCommandName().equals("Ack")) {
      setRelatedCommandResponseAction(new AckCommandResponseAction());
    }
    if(actionSubject.getCommandName().equals("CommandResult")) {
      setRelatedCommandResponseAction(new ResultCommandResponseAction());
    }
    
    
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

  
  
}
