package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class ReduceCommandResponseAction extends ACommandResponseAction {
//
//  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
//    ACommand command = (ACommand) actionSubject;
//    System.out.println("RCam Coordinator - " + this.getClass().getSimpleName() + " - will reduce ("+command.getCommandName()+"[" + command.getAckNumber() + "]) if it is ready to reduce.");    
//  }
  
  @Override
  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
    System.out.println("When all servers have a state of CommandReadyToReduceState, start reducing the command");
  }

  
  public void nextState(String server, ACommand command) {
    System.out.println("Take a break. I'll think about what comes next after a night off.");
  }

}
