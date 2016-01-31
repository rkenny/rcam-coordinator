package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.NewCommandState;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class RunNewCommandAction extends ACommandResponseAction {
  @Override
  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
    if(actionObject instanceof RunController) {
      if(stateIsReadyToRun(actionSubject)) {
        Future<Map.Entry<Integer, Integer>> reductionResult = ((RunController) actionObject).run(actionSubject, "newCommand");
        try {
          reductionResult.get();
          nextState(server, actionSubject);
        } catch(InterruptedException e) {
          e.printStackTrace();
        } catch(ExecutionException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public Boolean stateIsReadyToRun(ACommand actionSubject) {
    Iterator<Entry<String, ICommandState>> serversIterator = actionSubject.getStates().entrySet().iterator();
    
    NewCommandState newCommandState = new NewCommandState();
    
    while(serversIterator.hasNext()) {
      ICommandState serverState = serversIterator.next().getValue();
      
     
      if(serverState == null || !serverState.typeEquals(newCommandState)) {
        return false;
      }
    }
    
    return true;
     
  }
  
  
  public void nextState(String server, ACommand command) {
    command.nextState(server);
  }
}
