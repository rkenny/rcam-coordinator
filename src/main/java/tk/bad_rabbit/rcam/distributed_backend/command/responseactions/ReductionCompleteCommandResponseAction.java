package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class ReductionCompleteCommandResponseAction  extends ACommandResponseAction {
  @Override
  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
    ((RunController) actionObject).reduce(actionSubject);
  }

  
  public void nextState(String server, ACommand command) {}
}
