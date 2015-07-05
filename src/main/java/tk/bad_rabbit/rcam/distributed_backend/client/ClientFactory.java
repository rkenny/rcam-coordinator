package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import tk.bad_rabbit.rcam.distributed_backend.commandfactory.CommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.ConfigurationProvider;


@Service(value="clientFactory")
//@Scope("session")
public class ClientFactory implements IClientFactory  {
  private List<IClient> remoteClients;
  
  @Autowired
  @Qualifier("commandQueuer")
  private CommandQueuer commandQueuer;
  
  @Autowired
  @Qualifier("commandFactory")
  private CommandFactory commandFactory;
  
  @Autowired
  @Qualifier("configurationProvider")
  private ConfigurationProvider configurationProvider;
  
  
  public ClientFactory() {
    remoteClients = new ArrayList<IClient>(); 
  }
  
  public Client createClient(String remoteAddress, Integer remotePort) {
    Client newClient = new Client(remoteAddress, remotePort, commandQueuer, commandFactory, configurationProvider);
    return newClient;
  }
  
  
  public List<IClient> getRemoteClients() {
    return remoteClients;
  }
  

  
}
