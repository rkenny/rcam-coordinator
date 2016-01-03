package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReadyToReduceState;

public class ResultCommandResponseAction extends ACommandResponseAction {

//  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
//    ACommand command = (ACommand) actionObject;
//    System.out.println("RCam Coordinator - ResultCommandResponseAction - " + this.getClass().getSimpleName() + " - will doRelatedStuff to " + command.getAckNumber() + " for " + actionSubject.getAckNumber() + " on server " + server);
//    Integer resultCode = (Integer) actionSubject.getClientVariable("resultCode");
//      
//    command.setReturnCode(resultCode);
//    //command.setCommandResponseRelatedAction(new ReduceCommandResponseAction());
//    command.setState(server, new CommandReadyToReduceState());
//  }

  @Override
  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
    System.out.println("If the command was successful, the next step will be to check if the command is ready to reduce.");
    nextState(server, actionSubject);
  }

  
  
  public void nextState(String server, ACommand command) {
    command.setState(server, new CommandReadyToReduceState());
  }

  
}
