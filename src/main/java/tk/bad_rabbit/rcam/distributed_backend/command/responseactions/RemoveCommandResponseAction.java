package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class RemoveCommandResponseAction extends ACommandResponseAction {
  @Override
  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
    System.out.println("RCam Coordinator - " + this.getClass().getSimpleName() + " - will destroy Command("+actionSubject.getCommandName()+"["+actionSubject.getAckNumber()+"]) ");
  }

  
  public void nextState(String server, ACommand command) {
    System.out.println("RCam Coordinator - RemoveCommandResponseAction - command will not exist. (command == null) equals " + (command == null));
  }
}
