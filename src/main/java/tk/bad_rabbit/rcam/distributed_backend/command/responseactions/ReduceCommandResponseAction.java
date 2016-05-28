package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ACommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReadyToReduceState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class ReduceCommandResponseAction { // extends ARunResponseAction {
//
//  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
//    ACommand command = (ACommand) actionSubject;
//    System.out.println("RCam Coordinator - " + this.getClass().getSimpleName() + " - will reduce ("+command.getCommandName()+"[" + command.getAckNumber() + "]) if it is ready to reduce.");    
//  }
  
  public Future<Integer> doRunAction(RunController actionObject, ACommand actionSubject, ACommandState commandState) {
    //if(stateIsReadyToRun(actionSubject)) {
      //Future<Map.Entry<Integer, Integer>> reductionResult = ((RunController) actionObject).run(actionSubject, "reductionCommand");
      //try {
      //  reductionResult.get();
      //  actionSubject.deleteObserver(actionObject);
        //nextState(server, actionSubject);
      //  return ConcurrentUtils.constantFuture(true);
        
      //} catch(InterruptedException e) {
      //  e.printStackTrace();
      //} catch(ExecutionException e) {
      //  e.printStackTrace();
      //}
    //}
    return null;
  }

//  public boolean stateIsReadyToRun(ACommand actionSubject) {
//    Iterator<Entry<String, ACommandState>> serversIterator = actionSubject.getStates().entrySet().iterator();
//    
//    CommandReadyToReduceState readyToReduceState = new CommandReadyToReduceState();
//    
//    while(serversIterator.hasNext()) {
//      ICommandState serverState = serversIterator.next().getValue();
//      
//      if(actionSubject.getConfigurationObject("reduceOnFirst") != null && actionSubject.getConfigurationObject("reduceOnFirst").equals("true")) {
//        if(serverState.typeEquals(readyToReduceState)) {
//          return true;
//        }
//      }
//     
//      if(!serverState.typeEquals(readyToReduceState)) {
//        return false;
//      }
//    }
//    return true;
//    
//  }
  
  
  //public void nextState(String server, ACommand command) {
  //  command.nextState(server);
  //}

}
