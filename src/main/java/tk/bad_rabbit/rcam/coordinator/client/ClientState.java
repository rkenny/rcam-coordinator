package tk.bad_rabbit.rcam.coordinator.client;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public interface ClientState {
  public void doCommandAction(ACommand command);
}
