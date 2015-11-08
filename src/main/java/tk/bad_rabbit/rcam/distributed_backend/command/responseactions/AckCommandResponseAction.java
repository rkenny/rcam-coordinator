package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class AckCommandResponseAction implements ICommandResponseAction {

  public void doAction(Object actionObject, String server, ACommand actionSubject) {
    if( ((ACommand) actionSubject).isType("Ack")) {
      if(actionObject instanceof ClientThread) {
        ((ClientThread) actionObject).ackCommandReceived(actionSubject);
        actionSubject = null;
      }
    }
  }

}
