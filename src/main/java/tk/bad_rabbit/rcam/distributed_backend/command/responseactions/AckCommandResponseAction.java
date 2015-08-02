package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class AckCommandResponseAction implements ICommandResponseAction {

  public void doAction(Object actionObject, String server, ACommand actionSubject) {
    if( ((ACommand) actionSubject).isType("Ack")) {
      //((RunController) actionObject).ackCommandReceived(server, Integer.parseInt(((ACommand) actionSubject).getClientVariable("ackNumber")));
      if(actionObject instanceof ClientThread) {
        //((ClientThread) actionObject).ackCommandReceived(Integer.parseInt(((ACommand) actionSubject).getClientVariable("ackNumber")));
        System.out.println("About to ack command " + actionSubject.getAckNumber());
        ((ClientThread) actionObject).ackCommandReceived(actionSubject);
        actionSubject = null;
      }
      //((RunController) actionObject).removeCommand(actionSubject);
    }
    
    //if( ((ACommand) actionSubject).isType("CommandResult")) {
    //  System.out.println("This is a CommandResult for command="+Integer.parseInt(((ACommand) actionSubject).getClientVariable("ackNumber")));
    //}
    
  }

}
