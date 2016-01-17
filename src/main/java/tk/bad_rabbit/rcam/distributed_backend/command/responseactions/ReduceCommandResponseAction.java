package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Map;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReducedState;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class ReduceCommandResponseAction extends ACommandResponseAction {
//
//  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
//    ACommand command = (ACommand) actionSubject;
//    System.out.println("RCam Coordinator - " + this.getClass().getSimpleName() + " - will reduce ("+command.getCommandName()+"[" + command.getAckNumber() + "]) if it is ready to reduce.");    
//  }
  
  @Override
  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
    if(actionSubject.isReadyToReduce()) {
      Future<Map.Entry<Integer, Integer>> reductionResult = ((RunController) actionObject).reduce(actionSubject);
      try {
        reductionResult.get();
        actionSubject.deleteObserver(actionObject);
        nextState(server, actionSubject);
      } catch(InterruptedException e) {
        e.printStackTrace();
      } catch(ExecutionException e) {
        e.printStackTrace();
      }
      
    }
  }

  
  public void nextState(String server, ACommand command) {
    command.setState(new CommandReducedState());
  }

}
