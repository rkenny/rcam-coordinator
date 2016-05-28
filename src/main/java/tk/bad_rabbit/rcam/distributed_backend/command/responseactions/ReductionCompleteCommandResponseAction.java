package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Map;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ACommandState;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class ReductionCompleteCommandResponseAction {  // extends ARunResponseAction {
  
  public Future<Integer> doRunAction(RunController actionObject, ACommand actionSubject, ACommandState commandState) {
    System.out.println("RCam Coordinator - Does ReductionCompleteCommandResponseAction ever run?");
    Future<Map.Entry<Integer, Integer>> results;
    //results = ((RunController) actionObject).run(actionSubject, "reductionCommand");
    //while(!results.isDone()) {
      // spin. This will cause a block.
    //}
    return null;
  }

  
  //public void nextState(String server, ACommand command) {}
}
