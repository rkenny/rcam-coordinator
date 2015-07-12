package tk.bad_rabbit.rcam.spring.runcontroller;

import tk.bad_rabbit.rcam.distributed_backend.command.StateObject;

public class DoneState implements StateObject {

  public void doAction(Object actionObject, Object actionSubject) {
    System.out.println("Done state does nothing yet");
  }

}
