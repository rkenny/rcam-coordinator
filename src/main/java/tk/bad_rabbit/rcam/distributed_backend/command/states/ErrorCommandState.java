package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.client.IClient;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.IClientThread;


public class ErrorCommandState extends ACommandState {

  public void doAction(Observer observer, String server, ACommand actionSubject) {
    System.out.println("Depending on the command type, one Client reporting an error state"
        + "\nwill either cause a command failure, or it will remove the command from one client."
        + " This one occured on server " + server);
    
    System.out.println("The observer is " + observer.getClass().getSimpleName());
    if(observer instanceof IClient) {
      System.out.println("IClient caught observing a command failure... somehow.");
    }
    
    if(observer instanceof IClientThread) {
      System.out.println("Observer is a client thread. Do I need to change things so a Client can cancel commands?");
      ((IClientThread) observer).sendCancel((ACommand) actionSubject);
    }
    // This can affect all servers, or just the current server. First, let's implement a one-error fail scenario.
    //actionSubject.setErrorState();
    
  }

}
