package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class DefaultCommandResponseAction extends ACommandResponseAction {
  @Override
  public Future<Integer> doRelatedAction(CommandController actionObject, String server, ACommand actionSubject) {
    //actionSubject.nextState(server);
    return null;
  }
  

}
