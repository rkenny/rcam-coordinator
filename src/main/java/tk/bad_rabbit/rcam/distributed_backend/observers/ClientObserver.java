package tk.bad_rabbit.rcam.distributed_backend.observers;

import java.util.Observable;
import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ACommandState;

public class ClientObserver implements Observer {
  Observer observer;
  
  public ClientObserver(Observer observer) {
    this.observer = observer;
  }
  public void update(Observable observedCommand, Object client) {
     synchronized(observedCommand) {
      ACommand command;
      if(observedCommand instanceof ACommand && client instanceof String) {
        command = (ACommand) observedCommand;
        System.out.println("CommandController observed a changed to client in " + observedCommand.getClass().getSimpleName() + " to client " + (String) client);
      }
    }
  }

}
