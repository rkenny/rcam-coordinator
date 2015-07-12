package tk.bad_rabbit.rcam.distributed_backend.command;

public interface StateObject {

  public void doAction(Object actionObject, Object actionSubject);
}
