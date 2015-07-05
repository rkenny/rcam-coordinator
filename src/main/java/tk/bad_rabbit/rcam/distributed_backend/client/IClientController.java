package tk.bad_rabbit.rcam.distributed_backend.client;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public interface IClientController {
  public void send(ICommand command);
}
