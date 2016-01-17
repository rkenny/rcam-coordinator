package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandSentState;

public class SendCommandAction extends ACommandResponseAction {

  public void nextState(String server, ACommand command) {
    command.setState(server, new CommandSentState());
  }

  @Override
  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
    ((ServerThread) actionObject).send(actionSubject);
    //actionSubject.deleteObserver(actionObject);
    nextState(server, actionSubject);
  }

}
