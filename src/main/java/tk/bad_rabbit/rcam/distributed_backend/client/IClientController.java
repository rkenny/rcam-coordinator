package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.List;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public interface IClientController {
  public void register(ACommand command);
  public List<IClient> getClients();
}
