package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.List;

public interface IClientFactory {
  public List<IClient> getRemoteClients();
}
