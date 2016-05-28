package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class SetRelatedCommandsToDoneResponseAction extends ACommandResponseAction {
  //public void nextState(String server, ACommand command) {
    //command.setState(new DoneState());
  //  command.nextState(server);
  //}

  public Future<Integer> doRelatedAction(CommandController actionObject, String server, ACommand actionSubject) {
    //actionSubject.deleteObserver(actionObject);
    
    return null;
    
    //nextState(server, actionSubject);
  }

}
