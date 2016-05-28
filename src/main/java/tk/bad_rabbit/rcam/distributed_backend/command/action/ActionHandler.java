package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.util.concurrent.Future;

public interface ActionHandler {
  public Future<Integer> handleAction(ICommandAction action);
}
