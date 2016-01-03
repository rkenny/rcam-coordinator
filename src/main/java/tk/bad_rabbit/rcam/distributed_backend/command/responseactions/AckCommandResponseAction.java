package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.AckedState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.DoneState;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class AckCommandResponseAction extends ACommandResponseAction {
  
  
  @Override
  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
    System.out.println("RCam Coordinator - AckCommandResponseAction - going to doStuff to command " + actionSubject.getClientVariable("ackNumber"));
    ACommand relatedCommand = ((CommandController) actionObject).getCommand((Integer) actionSubject.getClientVariable("ackNumber"));
    relatedCommand.setState(server, new AckedState());
    nextState(server, actionSubject);
   }

//  @Override
//  public void doRelatedCommandStuff(Observer actionObject, String server, ACommand actionSubject) {
//      //if( ((ACommand) actionSubject).isType("Ack")) {
//       // Object ackNumber = ((ACommand) actionSubject).getClientVariable("ackNumber");
//       // if(ackNumber instanceof Integer) {
//       //   ((Controller) actionObject).ackCommandReceived((Integer) ackNumber);
//       //   ((Controller) actionObject).removeCommand(actionSubject);
//       // }
//     // }
//  }
  
  public void nextState(String server, ACommand command) {
    command.setState(server, new DoneState());
  }
//  public void doNetworkStuff(Observer actionObject, String server, ACommand actionSubject) {
//
//    synchronized(actionObject) {
//      if( actionSubject.isType("Ack")) {
//        if(actionObject instanceof ServerThread) {
//          System.out.println("RCam Coordinator - AckCommandResponseAction - It was observed by a ServerThread for client " + server);
//          System.out.println("RCam Coordinator - AckCommandResponseAction - the actionSubject's ackNumber is " + actionSubject.getAckNumber());
//        }
//        
//      }  
//    }
//  }
//  
//  public void doRelatedCommandStuff(Observer actionObject, String server, ACommand actionSubject) {
//    // actionObject will be an ACommand. It's referred to as the related command in the logs.
//    // actionSubject is the Ack command.
//  }
}
