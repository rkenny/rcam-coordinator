package tk.bad_rabbit.rcam.distributed_backend.command.states;

public interface ICommandState {

  public void doAction(Object actionObject, Object actionSubject);
}
