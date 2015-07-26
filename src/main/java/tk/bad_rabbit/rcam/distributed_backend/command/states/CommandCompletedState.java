package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class CommandCompletedState implements ICommandState {

  public void doAction(Observer observer, ACommand actionSubject) {
    if(observer instanceof RunController) {
      ((RunController) observer).readyToReduce((ACommand) actionSubject);
    }
  }

}
