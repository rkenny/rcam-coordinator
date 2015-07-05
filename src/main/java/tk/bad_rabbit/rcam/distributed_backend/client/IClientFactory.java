package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.List;
import java.util.Map;

public interface IClientFactory {
  public List<IClient> getRemoteClients();
  public Client createClient(String remoteAddress, Integer remotePort);
}
