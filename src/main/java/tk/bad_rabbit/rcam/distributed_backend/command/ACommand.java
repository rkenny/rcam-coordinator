package tk.bad_rabbit.rcam.distributed_backend.command;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.action.ICommandAction;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ACommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ReceivedCommandState;
import tk.bad_rabbit.rcam.spring.commands.CommandController;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public abstract class ACommand extends Observable implements ICommand, Observer {

  public Stack<ICommandAction> pendingCommandActions;
  
  private Map<String, CountDownLatch> countdownLatch;
  
  public void setCountdownLatch(String key, CountDownLatch countdownLatch) {
    System.out.println("Setting semaphore for "+ this.getCommandName() + " key "+key+" "+ countdownLatch);
    this.countdownLatch.put(key, countdownLatch);
  }
  
  public CountDownLatch getCountdownLatch(String key) {
    return countdownLatch.get(key);
  }
  
  
  public ACommand() {
    this.pendingCommandActions = new Stack<ICommandAction>();
    this.countdownLatch = new ConcurrentHashMap<String, CountDownLatch>();
  }
  
  public void addPendingAction(ICommandAction newAction) {
    synchronized(pendingCommandActions) {
      if(newAction != null) { 
        newAction.setCommandDetails(this);
        this.pendingCommandActions.push(newAction);
        setChanged();
        notifyObservers(newAction);
      }
    }
  }
  
  //public ICommandAction peekNextAction(){
//    synchronized(pendingCommandActions) {
      //return pendingCommandActions.peek();
    //}
  //}
  
  public ICommandAction popAction() {
    synchronized(pendingCommandActions) {
      return pendingCommandActions.pop();
    }
  }
  
  public void update(Observable observedObject, Object arg) {
  }
  
  public Future<Integer> doNetworkAction(ServerThread serverThread, String server) {
    synchronized(this) {
      return this.getState(server).doNetworkAction(serverThread, server, this);
    }
  }
  
  public Future<Integer> doRelatedCommandAction(CommandController actionObserver, String server) {
    synchronized(this) {
      return this.getState(server).doRelatedCommandAction(actionObserver, server, this);
    }
  }
    
  
  public Future<Integer> doRunCommandAction(RunController actionObserver, ACommandState commandState) {
    synchronized(this) {
      return commandState.doRunCommandAction(actionObserver, this, commandState);
    }
  }
  
  
  public Boolean isNoLongerNew() {
    synchronized(this) {
      ACommandState state = new ReceivedCommandState();
      for(String server : this.getServers()) {
        if(this.getState(server).typeEquals(state)) {
          return false;
        }
      }
      return true;
    }
  }
  
  abstract Set<String> getServers();
  
  
  
  //public void addObservers(List<Observer> observers) {
  //  synchronized(this) {
  //    for(Observer observer : observers) {
  //      this.addObserver(observer);
  //    }
  //  }
  //}
  
  @Override
  public void notifyObservers(Object arg) {
    synchronized(this) {
      super.notifyObservers(arg);
    
    
    //while(pendingCommandActions.size() >= 1 && pendingCommandActions.peek().nextAction() == null) {
    //  pendingCommandActions.pop();
    //}
//      ICommandAction commandAction = null;
//      if(pendingCommandActions.peek().nextAction() != null) {
//        commandAction = pendingCommandActions.pop().nextAction();
//        commandAction.setCommandDetails(this);
//        this.pendingCommandActions.push(commandAction);
//      } else {
//        pendingCommandActions.pop();
//      }
    
//      if(pendingCommandActions.size() >= 1 ) {
//      System.out.println("Action...");
//      for(ICommandAction action : pendingCommandActions) {
//        System.out.println(action.getClass().getSimpleName());
//      }
//      System.out.println("...");
      
 //     }
      
      //if(pendingCommandActions.size() >= 1) {
      //  commandAction = pendingCommandActions.pop();
      //}
      //
      //System.out.println("Action...");
      //for(ICommandAction action : pendingCommandActions) {
      //  System.out.println(action.getClass().getSimpleName());
     // }
     // System.out.println("...");
      //System.out.println(" Going to add a pending action of type " + commandAction.getClass().getSimpleName());
      //addPendingAction(commandAction);
      //setChanged();
      //notifyObservers("ignored");
    }
    
  }
}
