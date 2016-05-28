package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.io.IOException;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class SendCommandAction { // extends ANetworkResponseAction {

  //public void nextState(String server, ACommand command) {
    //command.setState(server, new CommandSentState());
  //  command.nextState(server);
  //}

  public Future<Integer> doNetworkAction(ServerThread actionObject, String server, ACommand actionSubject) {
    try {
      ((ServerThread) actionObject).send(server, actionSubject);
      return null;
    } catch(IOException e) {
      System.out.println("Sending the command failed.");
    }
    return null;
  }

}
