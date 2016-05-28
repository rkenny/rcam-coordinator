package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.io.IOException;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class SendReductionCompleteNetworkAction { // extends ANetworkResponseAction {
  public Future<Integer> doNetworkAction(ServerThread actionObject, String server, ACommand actionSubject) {
    //try {
//      ((ServerThread) actionObject).sendReductionComplete(actionSubject);
      //return null;
    //} catch(IOException e) {
//      System.out.println("Sending the reduction complete notification failed.");
    //}
    return null; //ConcurrentUtils.constantFuture(true);
    
    
    //actionSubject.nextState(server);
  }

}
