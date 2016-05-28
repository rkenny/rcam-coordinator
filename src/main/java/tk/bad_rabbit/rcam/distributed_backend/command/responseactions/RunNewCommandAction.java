package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ACommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.NewCommandState;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class RunNewCommandAction { // extends ARunResponseAction {
  //@Override
  public Future<Integer> doRunAction(RunController actionObject, ACommand actionSubject, ACommandState commandState) {
    System.out.println("This will run the new command");
    //if(actionObject instanceof RunController) {
      //if(stateIsReadyToRun(actionSubject)) {
        //Future<Void> reductionResult = ((RunController) actionObject).run(actionSubject, commandState);
        //reductionResult.get();
        //return reductionResult;
      //}
//    }
    return null;
  }

  public Boolean stateIsReadyToRun(ACommand actionSubject) {
    Iterator<Entry<String, ACommandState>> serversIterator = actionSubject.getStates().entrySet().iterator();
    
    NewCommandState newCommandState = new NewCommandState();
    
    while(serversIterator.hasNext()) {
      ICommandState serverState = serversIterator.next().getValue();
      
     
      if(serverState == null || !serverState.typeEquals(newCommandState)) {
        return false;
      }
    }
    
    return true;
     
  }
  
  
  //public void nextState(String server, ACommand command) {
  //  command.nextState(server);
  //}
}
