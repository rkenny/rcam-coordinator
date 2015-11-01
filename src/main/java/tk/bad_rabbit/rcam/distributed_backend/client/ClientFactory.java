package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import tk.bad_rabbit.rcam.distributed_backend.client.states.DefaultClientState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.CommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.ConfigurationProvider;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;


@Service(value="clientFactory")
//@Scope("session")
public class ClientFactory implements IClientFactory  {
  private List<IClient> remoteClients;
  
  @Autowired
  RunController runController;
  
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
    Client newClient = new Client(remoteAddress, remotePort, commandFactory, configurationProvider, runController);
    newClient.setState(new DefaultClientState());
    System.out.println("Creating a client " + remoteAddress + ":" + remotePort);
    return newClient;
  }
  
  
  public List<IClient> getRemoteClients() {
    return remoteClients;
  }
  

  
}
