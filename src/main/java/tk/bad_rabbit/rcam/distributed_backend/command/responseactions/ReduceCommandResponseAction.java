package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class ReduceCommandResponseAction extends ACommandResponseAction {
  public void doNetworkStuff(Observer actionObject, String server, ACommand actionSubject) { }
  public void doRelatedCommandStuff(Observer actionObject, String server, ACommand actionSubject) {
    ACommand command = (ACommand) actionSubject;
    System.out.println("RCam Coordinator - " + this.getClass().getSimpleName() + " - will reduce ("+command.getCommandName()+"[" + command.getAckNumber() + "]) if it is ready to reduce.");
    
  }
}
