package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.AckedState;

public class AckCommandResponseAction extends ACommandResponseAction {

  public void doNetworkStuff(Observer actionObject, String server, ACommand actionSubject) {

    synchronized(actionObject) {
      if( actionSubject.isType("Ack")) {
        if(actionObject instanceof ServerThread) {
          System.out.println("RCam Coordinator - AckCommandResponseAction - It was observed by a ServerThread for client " + server);
          System.out.println("RCam Coordinator - AckCommandResponseAction - the actionSubject's ackNumber is " + actionSubject.getAckNumber());
        }
        
      }  
    }
  }
  
  public void doRelatedCommandStuff(Observer actionObject, String server, ACommand actionSubject) {
    // actionObject will be an ACommand. It's referred to as the related command in the logs.
    // actionSubject is the Ack command.
    synchronized(actionObject) {
      ((ACommand) actionObject).setState(server, new AckedState());
    }
  }
}
