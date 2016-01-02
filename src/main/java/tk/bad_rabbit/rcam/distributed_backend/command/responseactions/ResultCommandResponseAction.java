package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReadyToReduceState;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class ResultCommandResponseAction extends ACommandResponseAction {

  public void doNetworkStuff(Observer actionObject, String server, ACommand actionSubject) {
  }
  
  public void doRelatedCommandStuff(Observer actionObject, String server, ACommand actionSubject) {
    //
    
    // actionObject will be an ACommand. It's referred to as the related command in the logs.
    // actionSubject is the CommandResult command.
    ACommand command = (ACommand) actionObject;
    System.out.println("RCam Coordinator - ResultCommandResponseAction - " + this.getClass().getSimpleName() + " - will doRelatedStuff to " + command.getAckNumber() + " for " + actionSubject.getAckNumber() + " on server " + server);
        
    
    
    //if(actionSubject.isType("CommandResult")) {
      
      //Integer ackNumber = (Integer) actionSubject.getClientVariable("ackNumber");
      Integer resultCode = (Integer) actionSubject.getClientVariable("resultCode");
      
      command.setReturnCode(resultCode);
      command.setCommandResponseRelatedAction(new ReduceCommandResponseAction());
      command.setState(server, new CommandReadyToReduceState());
      
      //if(ackObject instanceof Integer && resultCode instanceof Integer) {
        //((CommandController) actionObject).commandResultReceived((Integer) ackNumber,  (Integer) resultCode);
        
        
        //((ServerThread) actionObject).removeCommand(actionSubject);
      //}
      
    //}
    
  }

}
