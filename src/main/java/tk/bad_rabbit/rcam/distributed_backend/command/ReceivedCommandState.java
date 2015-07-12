package tk.bad_rabbit.rcam.distributed_backend.command;

import tk.bad_rabbit.rcam.distributed_backend.client.ACommand;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;


public class ReceivedCommandState implements StateObject {

  public void doAction(Object actionObject, Object actionSubject) {
    if(actionObject instanceof RunController) {
      ((ACommand) actionSubject).performCommandResponseAction(actionObject);
    }
  }

}
