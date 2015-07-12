package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class ResultCommandResponseAction implements ICommandResponseAction {

  public void doAction(Object actionObject, ACommand actionSubject) {
    if( ((ACommand) actionSubject).isType("CommandResult")) {
      ((RunController) actionObject).commandResultReceived(Integer.parseInt(((ACommand) actionSubject).getClientVariable("ackNumber")),
          ((ACommand) actionSubject).getClientVariable("resultCode"));
      ((RunController) actionObject).removeCommand(actionSubject);
    }
  }

}
