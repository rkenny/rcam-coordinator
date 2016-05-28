package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class RemoveCommandResponseAction extends ACommandResponseAction {

  public Future<Integer> doRelatedAction(CommandController actionObject, String server, ACommand actionSubject) {
    System.out.println("RCam Coordinator - " + this.getClass().getSimpleName() + " - will soon destroy Command("+actionSubject.getCommandName()+"["+actionSubject.getAckNumber()+"]) ");
    return null;
    //actionSubject.nextState(server);
  }


}
