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

import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

@Controller(value="clientController")
@Scope("singleton")
public class ClientController implements IClientController {
  @Autowired
  @Qualifier("configurationProvider")
  IConfigurationProvider configurationProvider;

  @Autowired
  RunController runController;
  
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
  
  public void register(ACommand command) {
    Iterator<IClient> remoteClientIterator = remoteClients.iterator();
    IClient client;
    while(remoteClientIterator.hasNext()) {
      client = remoteClientIterator.next();
      client.observeCommand(command);
      runController.observeCommand(command);
      //command.addObserver(client.getClientThread());
//      command.addObserver(runController);
      //client.addOutgoingCommand(command);
    }
  }
  
  public List<IClient> getClients() {
    return remoteClients;
  }
  
}
