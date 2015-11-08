package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;


public class ReceivedCommandState extends ACommandState {

  public void doAction(Observer observer, String server, ACommand actionSubject) {
    ((ACommand) actionSubject).performCommandResponseAction(server, observer);
  }
}
