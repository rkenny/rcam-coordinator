package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class CommandReducedState extends ACommandState {

  public void doAction(Observer observer, String server, ACommand actionSubject) {
    if(observer instanceof RunController) {
      ((RunController) observer).removeCommand((ACommand) actionSubject);
    }
  }

}
