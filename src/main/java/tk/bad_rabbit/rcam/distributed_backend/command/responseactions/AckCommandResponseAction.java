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

  public void nextState(String server, ACommand command) {
    command.setState(server, new DoneState());
  }

}
