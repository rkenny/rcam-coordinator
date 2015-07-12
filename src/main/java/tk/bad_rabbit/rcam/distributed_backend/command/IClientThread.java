package tk.bad_rabbit.rcam.distributed_backend.command;

import tk.bad_rabbit.rcam.distributed_backend.client.ACommand;

public interface IClientThread {
  public void send(ACommand command);
}
