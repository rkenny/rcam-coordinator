package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;


public interface ICommandResponseAction {
  public void doAction(Object actionObject, ACommand actionSubject);
}
