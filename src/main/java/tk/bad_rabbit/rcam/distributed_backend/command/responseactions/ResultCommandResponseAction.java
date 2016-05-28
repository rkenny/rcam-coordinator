package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class ResultCommandResponseAction extends ACommandResponseAction {

  @Override
  public Future<Integer> doRelatedAction(CommandController actionObject, String server, ACommand actionSubject) {
    //((CommandController) actionObject).getCommand((Integer) actionSubject.getClientVariable("ackNumber")).setState(server, new CommandReadyToReduceState());
    return null;
    
  }

  
  
  //public void nextState(String server, ACommand command) {
  //  command.nextState(server);
  //}

  
}
