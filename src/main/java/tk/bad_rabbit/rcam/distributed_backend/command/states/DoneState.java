package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;


public class DoneState extends ACommandState {

  public void doAction(Observer observer, String server, ACommand actionSubject) {
    System.out.println("Done state does nothing yet");
  }

}
