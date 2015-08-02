package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class ResultCommandResponseAction implements ICommandResponseAction {

  public void doAction(Object actionObject, String server, ACommand actionSubject) {
    if( ((ACommand) actionSubject).isType("CommandResult")) {
      ((ClientThread) actionObject).commandResultReceived(server, Integer.parseInt(((ACommand) actionSubject).getClientVariable("ackNumber")),
          ((ACommand) actionSubject).getClientVariable("resultCode"));
      ((ClientThread) actionObject).removeCommand(server, actionSubject);
    }
  }

}
