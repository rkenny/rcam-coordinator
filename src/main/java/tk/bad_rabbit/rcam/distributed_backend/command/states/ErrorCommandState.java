package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;


public class ErrorCommandState extends ACommandState {

  public void doAction(Observer observer, String server, ACommand actionSubject) {
    System.out.println("Depending on the command type, one Client reporting an error state"
        + "\nwill either cause a command failure, or it will remove the command from one client."
        + " This one occured on server " + server);
    
    System.out.println("The observer is " + observer.getClass().getSimpleName());
    
    // This can affect all servers, or just the current server. First, let's implement a one-error fail scenario.
    //actionSubject.setErrorState();
    
  }

}
