package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public abstract class ACommandResponseAction implements ICommandResponseAction {
  
//  public void doNetworkAction(Observer actionObject, String server, ACommand actionSubject) {
//    System.out.println("RCam Coordinator- " + this.getClass().getSimpleName() + " doNetworkStuff called");
//    doNetworkStuff(actionObject, server, actionSubject);
//  }
//  
//  public void doRelatedCommandAction(Observer actionObject, String server, ACommand actionSubject) {
//    System.out.println("RCam Coordinator- " + this.getClass().getSimpleName() + " doRelatedCommandStuff called");
//    doRelatedCommandStuff(actionObject, server, actionSubject);
//  }
  
  abstract public void doStuff(Observer actionObject, String server, ACommand actionSubject);
  

}
