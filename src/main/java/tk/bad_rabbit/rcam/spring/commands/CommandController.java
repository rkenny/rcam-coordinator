package tk.bad_rabbit.rcam.spring.commands;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import tk.bad_rabbit.rcam.distributed_backend.client.IClient;
import tk.bad_rabbit.rcam.distributed_backend.client.IClientFactory;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;

@Controller
@Scope("request")
public class CommandController {
  
  
  @Autowired
  @Qualifier("clientFactory")
  IClientFactory clientFactory;
  
  @Autowired
  @Qualifier("commandFactory")
  ICommandFactory commandFactory;
  
  List<IClient> remoteClients;
  
  @RequestMapping(value= "/command/{commandString}", method = RequestMethod.POST)
  public @ResponseBody String command(@PathVariable("commandString") String commandString) {
    remoteClients = clientFactory.getRemoteClients();
    Iterator<IClient> clientIterator = remoteClients.iterator();
    while(clientIterator.hasNext()) {
      IClient currentClient = clientIterator.next();
      ICommand command = commandFactory.createCommand(commandString);
      currentClient.addOutgoingCommand(command.readyToSend());
  //    //clientIterator.remove();
    }
    
    return "Recieved "+commandString+" post.";
  }
}
