package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.DoneState;

public class SetRelatedCommandsToDoneResponseAction extends ACommandResponseAction {
  public void nextState(String server, ACommand command) {
    command.setState(new DoneState());
  }

  @Override
  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
    System.out.println("RCam Coordinator - " + this.getClass().getSimpleName() + " - will set the state of Command("+actionSubject.getCommandName()+"["+actionSubject.getAckNumber()+"]) to DoneState.");
    System.out.println("RCam Coordinator - " + this.getClass().getSimpleName() + " - will deleteObserver(CommandController)");
    actionSubject.deleteObserver(actionObject);
    nextState(server, actionSubject);
  }

}
