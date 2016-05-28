package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class CancelCommandAction extends ACommandResponseAction {

  public Future doRelatedAction(CommandController actionObject, String server,ACommand actionSubject) {
    System.out.println("This will cancel Command("+actionSubject.getCommandName()+"["+actionSubject.getAckNumber()+"]) on " + server);
    //actionSubject.nextState(server);
    
    return ConcurrentUtils.constantFuture(true);
  }

  //public void nextState(String server, ACommand command) {
    // TODO Auto-generated method stub
  //}

}
