package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.DefaultCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.DefaultNetworkResponseAction;
import tk.bad_rabbit.rcam.spring.commands.CommandController;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public abstract class ACommandState extends Observable implements ICommandState, Observer  { 
  Future<Integer> networkResponseActionResult;
  Future<Integer> relatedCommandActionResult;
  Future<Integer> runCommandActionResult;
  
  
  public ACommandState() {
    // setNetworkResponseAction(new DefaultNetworkResponseAction());
    setRelatedCommandResponseAction(new DefaultCommandResponseAction());
    // setRunCommandResponseAction(new DefaultRunResponseAction());
  }
    
  public Future<Integer> doNetworkAction(ServerThread serverThread, String server, ACommand actionSubject) {
    //return getNetworkResponseAction().doNetworkAction(serverThread, server, actionSubject);
    return null;
  }
  
  public Future<Integer> doRelatedCommandAction(CommandController actionObserver, String server, ACommand actionSubject) {
    return getRelatedCommandResponseAction().doRelatedAction(actionObserver, server, actionSubject);
  }
  
  public Future<Integer> doRunCommandAction(RunController actionObserver, ACommand actionSubject, ACommandState commandState) {
    //return getRunCommandResponseAction().doRunAction(actionObserver, actionSubject, commandState);
    return null;
  }
  
  @Override
  public boolean equals(Object comparisonState) {
    if(comparisonState instanceof ICommandState) {
      return (getClass().getSimpleName().equals(comparisonState.getClass().getSimpleName()));  
    }
    return false;
  }  
  
  public Boolean typeEquals(ICommandState comparisonState) {
    return this.getClass().getSimpleName().equals(comparisonState.getClass().getSimpleName());
  }
  
}
