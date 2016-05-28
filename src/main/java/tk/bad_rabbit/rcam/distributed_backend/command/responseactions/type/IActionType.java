package tk.bad_rabbit.rcam.distributed_backend.command.responseactions.type;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public interface IActionType {
  public void doStuff(Observer actionObject, String server, ACommand actionSubject);
}
