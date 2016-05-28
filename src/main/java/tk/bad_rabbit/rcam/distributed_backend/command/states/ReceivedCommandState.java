package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observable;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ACommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ANetworkResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ARunResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.AckCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ResultCommandResponseAction;
import tk.bad_rabbit.rcam.spring.commands.CommandController;


public class ReceivedCommandState extends ACommandState {
  
  public Future<Integer> doRelatedCommandAction(CommandController actionObserver, String server, ACommand actionSubject) {
    
    // feels like there's a better way to handle these two commands.
    if(actionSubject.getCommandName().equals("Ack")) {
      setRelatedCommandResponseAction(new AckCommandResponseAction());
    }
    if(actionSubject.getCommandName().equals("CommandResult")) {
      setRelatedCommandResponseAction(new ResultCommandResponseAction());
    }
    
    
    return getRelatedCommandResponseAction().doRelatedAction(actionObserver, server, actionSubject);
  }
  
  ANetworkResponseAction networkResponseAction;
  ACommandResponseAction relatedCommandAction;
  
  
  
  
  
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
    return new DoneState();
  }
  
  public void update(Observable observedAction, Object actionClass) {
    System.out.println(this.getClass().getSimpleName() + " - Observed a change in " + observedAction.getClass().getSimpleName() + " it is " + actionClass);
  }

  
}
