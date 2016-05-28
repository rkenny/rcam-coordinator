package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ARunResponseAction;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class DefaultRunResponseAction { // extends ARunResponseAction {

  public Future<Integer> doRunAction(RunController runController, ACommand command, ACommandState commandState) {
    System.out.println("Default doRunAction is to do nothing.");
    return null;
  }

}
