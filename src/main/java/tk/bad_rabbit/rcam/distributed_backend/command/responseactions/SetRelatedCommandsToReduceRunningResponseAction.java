package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class SetRelatedCommandsToReduceRunningResponseAction extends ACommandResponseAction {
  //public void nextState(String server, ACommand command) {
    //command.setState(new CommandReduceRunningState());
  //  command.nextState(server);
  //}

  @Override
  public Future<Integer> doRelatedAction(CommandController actionObject, String server, ACommand actionSubject) {
    System.out.println("RCam Coordinator - " + this.getClass().getSimpleName() + " - will set the state of Command("+actionSubject.getCommandName()+"["+actionSubject.getAckNumber()+"]) to CommandReduceRunning.");
    
    return null;
    
    //nextState(server, actionSubject);
  }

}
