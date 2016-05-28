package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.io.IOException;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class NetworkNotificationAction { // extends ANetworkResponseAction {

  public Future<Integer> doNetworkAction(ServerThread  actionObject, String server, ACommand actionSubject) {
    System.out.println("This will notify of an action to Command("+actionSubject.getCommandName()+"["+actionSubject.getAckNumber()+"]) on " + server);
    //try {
      //((ServerThread) actionObject).sendCancelCommand(server, actionSubject);
//      return null;
    //} catch(IOException e) {
//      System.out.println("Sending the network notification action failed.");
    //}
    
    return null;
  }

  //public void nextState(String server, ACommand command) {
  //  command.nextState(server);
  //}

}
