package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class CancelCommandAction implements ICommandResponseAction {

  public void doStuff(Observer actionObject, String server,ACommand actionSubject) {
    System.out.println("This will cancel Command("+actionSubject.getCommandName()+"["+actionSubject.getAckNumber()+"]) on " + server);
    actionSubject.nextState(server);
  }

  public void nextState(String server, ACommand command) {
    // TODO Auto-generated method stub

  }

}
