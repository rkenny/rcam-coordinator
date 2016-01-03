package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReducedState;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class ReduceCommandResponseAction extends ACommandResponseAction {
//
//  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
//    ACommand command = (ACommand) actionSubject;
//    System.out.println("RCam Coordinator - " + this.getClass().getSimpleName() + " - will reduce ("+command.getCommandName()+"[" + command.getAckNumber() + "]) if it is ready to reduce.");    
//  }
  
  @Override
  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
    System.out.println("RCam Coordinator - " + this.getClass().getSimpleName() + " - " + actionObject.getClass().getSimpleName() + " will tell Command("+actionSubject.getCommandName()+"["+actionSubject.getAckNumber()+"]) to run");
    if(actionSubject.isReadyToReduce()) {
      ((RunController) actionObject).reduce(actionSubject);
      nextState(server, actionSubject);
    }
  }

  
  public void nextState(String server, ACommand command) {
    command.setState(new CommandReducedState());
  }

}
