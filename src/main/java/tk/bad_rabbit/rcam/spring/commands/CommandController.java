package tk.bad_rabbit.rcam.spring.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//import tk.bad_rabbit.rcam.app.RunController;
import tk.bad_rabbit.rcam.distributed_backend.client.IClient;
import tk.bad_rabbit.rcam.distributed_backend.client.IClientController;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ReadyToSendState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

@Controller
@Scope("session")
public class CommandController {
  
  @Autowired
  @Qualifier("clientController")
  IClientController clientController;
  
  @Autowired
  @Qualifier("commandFactory")
  ICommandFactory commandFactory;
  
  @RequestMapping(value= "/command/{commandString}", method = RequestMethod.POST)
  public @ResponseBody String command(@PathVariable("commandString") String commandString) {
    
    ACommand command = commandFactory.createCommand(commandString);
    
//    for(IClient client : clientController.getClients()) {
//      command.addObserver(client);
//    }
//    command.addObserver(runController);
    clientController.register(command);
    
    command.setState(new ReadyToSendState());
    
    return "Done";
  }
}
