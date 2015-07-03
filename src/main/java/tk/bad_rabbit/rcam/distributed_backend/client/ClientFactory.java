package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

@Component(value="clientFactory")
@Scope("session")
public class ClientFactory implements IClientFactory {
  private List<IClient> remoteClients;
  
  @Autowired
  @Qualifier("configurationProvider")
  IConfigurationProvider configurationProvider;
  
  @Autowired
  @Qualifier("commandQueuer")
  private CommandQueuer commandQueuer;
  
  public ClientFactory() {
    remoteClients = new ArrayList<IClient>(); 
  }
  
  @PostConstruct
  public void initializeClients() {
    Iterator<Map.Entry<String, Integer>> clientIterator = configurationProvider.getBackendMapIterator();
    //commandQueuer = new CommandQueuer(configurationProvider.getBackendList());
    while(clientIterator.hasNext()) {
      Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>) clientIterator.next(); 
      Client newClient = new Client(pair.getKey(), pair.getValue());
      newClient.joinIncomingCommandQueue(commandQueuer.getIncomingCommandQueue(pair.getKey() + ":" + pair.getValue()));
      newClient.joinOutgoingCommandQueue(commandQueuer.getOutgoingCommandQueue(pair.getKey() + ":" + pair.getValue()));
      remoteClients.add(newClient);
      newClient.startClientThread();
      //clientIterator.remove();
    }
  }
  
  
  public List<IClient> getRemoteClients() {
    return remoteClients;
  }
  
  // this needs to go into a Spring bean. One commandQueuer for the entire application.
  public CommandQueuer getCommandQueuer() {
    return commandQueuer;
  }
  
}
