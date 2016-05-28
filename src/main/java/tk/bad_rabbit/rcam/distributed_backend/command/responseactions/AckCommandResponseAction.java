package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;
import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.AckedState;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class AckCommandResponseAction extends ACommandResponseAction {
  
  
  //@Override
  public Future doRelatedAction(CommandController actionObject, String server, ACommand actionSubject) {
  //  ACommand relatedCommand = ((CommandController) actionObject).getCommand((Integer) actionSubject.getClientVariable("ackNumber"));
    //relatedCommand.setState(server, new AckedState());
    //nextState(server, actionSubject);
    
    return ConcurrentUtils.constantFuture(true);
   }

  //public void nextState(String server, ACommand command) {
  //  command.nextState(server);
  //}

}
