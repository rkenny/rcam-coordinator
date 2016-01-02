package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public abstract class ACommandResponseAction implements ICommandResponseAction {

  public void doNetworkAction(Observer actionObject, String server, ACommand actionSubject) {
    //System.out.println("RCam Coordinator - "+getClass().getSimpleName()+" received for " + actionSubject.getCommandName());
    doNetworkStuff(actionObject, server, actionSubject);
  }

  public void doRelatedCommandAction(Observer actionObject, String server, ACommand actionSubject) {
    //System.out.println("RCam Coordinator - "+getClass().getSimpleName()+" - "+actionSubject.getAckNumber() +" will doRelatedCommandStuff to  " + ((ACommand) actionObject).getAckNumber() + " for server " + server);
    doRelatedCommandStuff(actionObject, server, actionSubject);
  }
  
  abstract public void doNetworkStuff(Observer actionObject, String server, ACommand actionSubject);
  abstract public void doRelatedCommandStuff(Observer actionObject, String server, ACommand actionSubject);

}
