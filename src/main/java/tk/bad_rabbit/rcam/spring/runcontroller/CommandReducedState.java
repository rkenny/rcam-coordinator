package tk.bad_rabbit.rcam.spring.runcontroller;

import tk.bad_rabbit.rcam.distributed_backend.client.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.StateObject;

public class CommandReducedState implements StateObject {

  public void doAction(Object actionObject, Object actionSubject) {
    if(actionObject instanceof RunController) {
      ((RunController) actionObject).removeCommand((ACommand) actionSubject);
    }
  }

}
