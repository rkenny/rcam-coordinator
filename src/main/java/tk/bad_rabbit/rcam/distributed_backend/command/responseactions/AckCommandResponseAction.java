package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class AckCommandResponseAction implements ICommandResponseAction {

  public void doAction(Object actionObject, ACommand actionSubject) {
    if( ((ACommand) actionSubject).isType("Ack")) {
      ((RunController) actionObject).ackCommandReceived(Integer.parseInt(((ACommand) actionSubject).getClientVariable("ackNumber")));
      ((RunController) actionObject).removeCommand(actionSubject);
    }
    
    if( ((ACommand) actionSubject).isType("CommandResult")) {
      System.out.println("This is a CommandResult for command="+Integer.parseInt(((ACommand) actionSubject).getClientVariable("ackNumber")));
    }
    
  }

}
