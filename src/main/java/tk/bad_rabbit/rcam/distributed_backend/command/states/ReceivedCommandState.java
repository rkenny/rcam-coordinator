package tk.bad_rabbit.rcam.distributed_backend.command.states;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;


public class ReceivedCommandState implements ICommandState {

  public void doAction(Object actionObject, Object actionSubject) {
    if(actionObject instanceof RunController) {
      ((ACommand) actionSubject).performCommandResponseAction(actionObject);
    }
  }

}
