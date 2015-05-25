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
  
  public ClientFactory() {
    remoteClients = new ArrayList<IClient>(); 
  }
  
  @PostConstruct
  public void initializeClients() {
    Iterator<Map.Entry<String, Integer>> clientIterator = configurationProvider.getClientMapIterator();
    while(clientIterator.hasNext()) {
      Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>) clientIterator.next(); 
      System.out.println(pair.getKey() + " " + pair.getValue());
      Client newClient =new Client(pair.getKey(), pair.getValue()); 
      remoteClients.add(newClient);
      newClient.startClientThread();
      clientIterator.remove();
    }
  }
  
  
  public List<IClient> getRemoteClients() {
    return remoteClients;
  }
  
}
