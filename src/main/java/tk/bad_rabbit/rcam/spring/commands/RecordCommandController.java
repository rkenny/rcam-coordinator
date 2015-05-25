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
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;

@Controller
@Scope("request")
public class RecordCommandController {
  
  
  @Autowired
  @Qualifier("clientFactory")
  IClientFactory clientFactory;
  
  @Autowired
  @Qualifier("commandFactory")
  ICommandFactory commandFactory;
  
  List<IClient> remoteClients;
  
  @RequestMapping(name = "/record", method = RequestMethod.GET)
  public @ResponseBody String beginRecordingGet() {
    remoteClients = clientFactory.getRemoteClients();
    Iterator<IClient> clientIterator = remoteClients.iterator();
    while(clientIterator.hasNext()) {
      IClient currentClient = clientIterator.next();
      currentClient.record();
    }
    return "Recieved beginRecording post.";
  }
  
  @RequestMapping(name = "/record/{duration}", method = RequestMethod.POST)
  public @ResponseBody String beginRecording(@PathVariable("duration") Integer duration) {
    return "Recieved beginRecording post. Duration: " + duration + " seconds";
  }
}
