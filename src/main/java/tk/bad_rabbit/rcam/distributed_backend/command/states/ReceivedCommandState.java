package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.AckCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ResultCommandResponseAction;


public class ReceivedCommandState extends ACommandState {

//  public void doAction(Observer observer, String client, ACommand actionSubject) {
//    synchronized(actionSubject) {
//      ((ACommand) actionSubject).performCommandResponseAction(client, observer);
//    }
  //}
  
  //public void doRelatedNetworkAction(Observer observer, String server, ACommand actionSubject) {
    //synchronized(actionSubject) {
    //  actionSubject.performCommandResponseNetworkAction(server, observer);
    //}   
  //}
  
  public void doRelatedCommandAction(Observer actionObserver, String server, ACommand actionSubject) {
    System.out.println("RCam Coordinator - ReceivedCommandState - this is the command name: ["+actionSubject.getCommandName()+"]");
    System.out.println("RCam Coordinator - This will happen on the following observer: " + actionObserver.getClass().getSimpleName());
    //actionSubject.addObserver(actionObserver);
    //actionSubject.setState(server, new ReadyToHandleState());
    
    // feels like there's a better way to handle these two commands.
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
  
  public ReceivedCommandState() {
    
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
