package tk.bad_rabbit.rcam.spring.commands;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import tk.bad_rabbit.rcam.distributed_backend.client.IClientController;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
//import tk.bad_rabbit.rcam.app.RunController;

@Controller
@Scope("session")
public class CommandController {
  
  @Autowired
  @Qualifier("clientController")
  IClientController clientController;
  
  @Autowired
  @Qualifier("commandFactory")
  ICommandFactory commandFactory;
  
  @RequestMapping(value= "/command/{commandType}", method = RequestMethod.POST,  
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<String> createCommand(@PathVariable String commandType, @RequestBody String clientVariables) {
    
    clientController.register(commandFactory.createCommand(commandType, new JSONObject(clientVariables)));
    
    return new ResponseEntity<String>("done",HttpStatus.OK);

  }
}
