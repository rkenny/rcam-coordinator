package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class NetworkNotificationAction implements ICommandResponseAction {

  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
    System.out.println("This will notify of an action to Command("+actionSubject.getCommandName()+"["+actionSubject.getAckNumber()+"]) on " + server);
    ((ServerThread) actionObject).sendCancelCommand(server, actionSubject);
  }

  public void nextState(String server, ACommand command) {
    command.nextState(server);
  }

}
