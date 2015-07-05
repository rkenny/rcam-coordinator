package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.commandqueuer.ICommandQueuer;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

@Controller(value="clientController")
@Scope("singleton")
public class ClientController implements IClientController {
  @Autowired
  @Qualifier("configurationProvider")
  IConfigurationProvider configurationProvider;
  

  @Autowired
  @Qualifier("commandQueuer")
  ICommandQueuer commandQueuer;
  
  @Autowired
  @Qualifier("clientFactory")
  IClientFactory clientFactory;
  
  
  List<IClient> remoteClients;

  
  public ClientController() {
    remoteClients = new ArrayList<IClient>();
  }
  
  @PostConstruct
  public void initializeClients() {
    Iterator<Map.Entry<String, Integer>> clientIterator = configurationProvider.getBackendMapIterator();
  
    while(clientIterator.hasNext()) {
      Map.Entry<String, Integer> mapEntry = (Map.Entry<String, Integer>) clientIterator.next();
      Client newClient = clientFactory.createClient(mapEntry.getKey(), mapEntry.getValue());

      remoteClients.add(newClient);
      newClient.startClientThread();
    }
  }
  
  public void send(ICommand command) {
    Iterator<IClient> remoteClientIterator = remoteClients.iterator();
    IClient client;
    while(remoteClientIterator.hasNext()) {
      client = remoteClientIterator.next();
      client.addOutgoingCommand(command);
    }
  }
  
}
