package tk.bad_rabbit.rcam.distributed_backend.command.states;


public class DoneState implements ICommandState {

  public void doAction(Object actionObject, Object actionSubject) {
    System.out.println("Done state does nothing yet");
  }

}
