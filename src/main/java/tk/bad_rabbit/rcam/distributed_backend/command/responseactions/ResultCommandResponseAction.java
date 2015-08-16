package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class ResultCommandResponseAction implements ICommandResponseAction {

  public void doAction(Object actionObject, String server, ACommand actionSubject) {
    System.out.println("Performing resultCommandResponseAction");
    ACommand command = (ACommand) actionSubject;
    if(command.isType("CommandResult")) {
      
      Object ackObject = command.getClientVariable("ackNumber");
      Object resultCode = command.getClientVariable("resultCode");
      System.out.println("ackObject is " + ackObject.getClass().getName());
      if(ackObject instanceof Integer && resultCode instanceof Integer) {
        ((ClientThread) actionObject).commandResultReceived((Integer) ackObject,  (Integer) resultCode);
        ((ClientThread) actionObject).removeCommand(actionSubject);
      }
      
    }
  }

}
