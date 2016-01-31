package tk.bad_rabbit.rcam.distributed_backend.client.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public interface IClientState {
  public void doCommandAction(ACommand command);
}
