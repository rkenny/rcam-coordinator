package tk.bad_rabbit.rcam.spring.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.action.ActionHandler;
import tk.bad_rabbit.rcam.distributed_backend.command.action.ICommandAction;
import tk.bad_rabbit.rcam.distributed_backend.command.action.IRelatedCommandAction;
import tk.bad_rabbit.rcam.distributed_backend.command.action.RunNewCommandAction;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

@Controller(value="commandController")
@Scope("singleton")
public class CommandController implements ActionHandler {
  
  @Autowired
  @Qualifier("serverThread")
  ServerThread serverThread;
  
  @Autowired
  @Qualifier("runController")
  RunController runController;
  
  Map<Integer, ACommand> commandMap;
  
  @Autowired
  @Qualifier("commandFactory")
  ICommandFactory commandFactory;
  
  ExecutorService commandExecutor;
  
  @PostConstruct
  public void initialize() {
    this.commandMap = new HashMap<Integer, ACommand>();
    commandExecutor = Executors.newFixedThreadPool(20);
  }
  
  @CrossOrigin(origins = "*")
  @RequestMapping(value= "/command/{commandType}", method = RequestMethod.POST,  consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<String> createCommand(@PathVariable String commandType, @RequestBody String clientVariables) {
    System.out.println("RCam Coordinator - CommandController - Receieved a request to create " + commandType);
    
    ACommand newCommand = commandFactory.createCommand(commandType, new JSONObject(clientVariables));
    newCommand.setServers(serverThread.getConnectedServers());
    newCommand.addPendingAction(new RunNewCommandAction());
    
    return new ResponseEntity<String>("done",HttpStatus.OK);
  }

  public void addCommand(ACommand command) {
    this.commandMap.put(command.getAckNumber(), command);
  }
  
  public ACommand getCommand(Integer ackNumber) {
    return this.commandMap.get(ackNumber);
  }
  

  public Future<Integer> handleAction(ICommandAction action) {
    if(action instanceof IRelatedCommandAction) {
      return commandExecutor.submit(((IRelatedCommandAction) action).getRelatedCallable(this));
    }
    return null;
  }
}
