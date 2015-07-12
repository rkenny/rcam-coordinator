package tk.bad_rabbit.rcam.distributed_backend.command;

import tk.bad_rabbit.rcam.distributed_backend.client.ACommand;

public interface ICommandResponseAction {
  public void doAction(Object actionObject, ACommand actionSubject);
}
