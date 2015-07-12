package tk.bad_rabbit.rcam.distributed_backend.command;


public class AckedState implements StateObject {

  public void doAction(Object actionObject, Object actionSubject) {
    //System.out.println("Command acked. Wait for the response now.");
  }

}
