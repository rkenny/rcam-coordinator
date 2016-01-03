package tk.bad_rabbit.rcam.spring.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

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

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ReadyToSendState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
//import tk.bad_rabbit.rcam.app.RunController;

@Controller
@Scope("singleton")
public class CommandController implements Observer {
  
  @Autowired
  ServerThread serverThread;
  
  Map<Integer, ACommand> commandMap;
  
  @Autowired
  @Qualifier("commandFactory")
  ICommandFactory commandFactory;
  
  @PostConstruct
  public void initialize() {
    this.commandMap = new HashMap<Integer, ACommand>();
  }
  
  @RequestMapping(value= "/command/{commandType}", method = RequestMethod.POST,  
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<String> createCommand(@PathVariable String commandType, @RequestBody String clientVariables) {
    System.out.println("RCam Coordinator - CommandController - Receieved a request to create " + commandType);
    
    //clientController.register(commandFactory.createCommand(commandType, new JSONObject(clientVariables)));
    ACommand newCommand = commandFactory.createCommand(commandType, new JSONObject(clientVariables));
    
    commandMap.put(newCommand.getAckNumber(), newCommand);
    
    newCommand.addObserver(this);
    newCommand.addObserver(serverThread);
    newCommand.setServers(serverThread.getConnectedServers());    
    newCommand.setState(new ReadyToSendState());
    
    
    return new ResponseEntity<String>("done",HttpStatus.OK);
  }

  public ACommand getCommand(Integer ackNumber) {
    return this.commandMap.get(ackNumber);
  }
  
  public void update(Observable o, Object arg) {
    ACommand updatedCommand = (ACommand) o;
    ACommand relatedCommand;
    System.out.println("RCam Coordinator - CommandController - Receieved an update for command " + updatedCommand.getAckNumber());
    
    if(arg instanceof Entry) {
      Entry<ACommand, Entry<String, ICommandState>> details = (Entry<ACommand, Entry<String, ICommandState>> ) arg;
      String server = details.getValue().getKey();
      updatedCommand = details.getKey();
      System.out.println("RCam Coordinator - CommandController - Updating a related command on server " + server);
      System.out.println("RCam Coordinator - CommandController - updating related command with ackNumber " + updatedCommand.getAckNumber());
      System.out.println("RCam Coordinator - CommandController - updating related command with variable ackNumber " + updatedCommand.getClientVariable("ackNumber"));

      updatedCommand.doRelatedCommandAction(this, server);
      
      
      //if(updatedCommand.getClientVariable("ackNumber") != null) {
        //relatedCommand = commandMap.get(updatedCommand.getClientVariable("ackNumber"));
        
      //}
      //updatedCommand.performCommandResponseRelatedAction(server, commandMap.get(updatedCommand.getClientVariable("ackNumber")));

      
    } //<ACommand, Entry<String, ICommandState>>
    //ACommand relatedCommand = getCommand((Integer) arg);
    
    
    //updatedCommand.doRelatedCommandAction(this, updatedCommand.getOrigin(), getCommand((Integer) arg));
  }
}
