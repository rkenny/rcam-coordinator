package tk.bad_rabbit.rcam.distributed_backend.command.states;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.SendCommandAction;

public class ReadyToSendState extends ACommandState {
  
  //public void doRelatedCommandStuff(Observer actionObserver, String server, ACommand actionSubject) {  }
  
  //public void doNetworkStuff(Observer observer, String client, ACommand actionSubject) {
    //if(observer instanceof ServerThread ) {
    // this needs to be a commandResponseAction
  //    System.out.println("RCam Coordinator - ReadyToSendState - Telling ServerThread to send " + actionSubject.getAckNumber());
  //    ((ServerThread) observer).send(client, actionSubject);
   // }
     
  //}
  
  //public void nextState (String server, ACommand actionSubject) {
  //  actionSubject.setState(server, new CommandSentState());
  //}
  
  public void nextState(String server, ACommand actionSubject) {}
  
  ICommandResponseAction networkResponseAction;
  ICommandResponseAction relatedCommandAction;
  
  public ReadyToSendState() {
    System.out.println("RCamCoordinator - " + this.getClass().getSimpleName() + " Constructor called");
    setNetworkResponseAction(new SendCommandAction());
  }
  
  
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
