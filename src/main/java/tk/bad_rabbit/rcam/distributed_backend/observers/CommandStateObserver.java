package tk.bad_rabbit.rcam.distributed_backend.observers;

import java.util.Observable;
import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.action.ICommandAction;

public class CommandStateObserver implements Observer {
  
  Observer commandCoordinator;
  
  public CommandStateObserver(Observer commandCoordinator) {
    this.commandCoordinator = commandCoordinator;
  }
  
  public void update(Observable observedCommand, Object commandState) {
    synchronized(observedCommand) {
      ACommand command;
      if(observedCommand instanceof ACommand && commandState instanceof ICommandAction) {
        command = (ACommand) observedCommand;
        System.out.println("CommandController observed a change commandstate in " + observedCommand.getClass().getSimpleName() + " to state " + commandState.getClass().getSimpleName());
        //((ICommandAction) commandState).doAction();
      }
    }
  }
}
