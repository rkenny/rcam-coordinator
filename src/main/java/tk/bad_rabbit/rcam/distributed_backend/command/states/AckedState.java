package tk.bad_rabbit.rcam.distributed_backend.command.states;



public class AckedState implements ICommandState {

  public void doAction(Object actionObject, Object actionSubject) {
    //System.out.println("Command acked. Wait for the response now.");
  }

}
