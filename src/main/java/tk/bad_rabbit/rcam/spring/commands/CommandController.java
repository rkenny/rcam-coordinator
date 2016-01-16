package tk.bad_rabbit.rcam.spring.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.activation.CommandObject;
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

@Controller(value="commandController")
@Scope("singleton")
public class CommandController implements Observer {
  
  @Autowired
  @Qualifier("serverThread")
  ServerThread serverThread;
  
  @Autowired
  @Qualifier("runController")
  Observer runController;
  
  List<Observer> observers;
  
  Map<Integer, ACommand> commandMap;
  //Map<String, Observer> commandControllerObservers;
  
  @Autowired
  @Qualifier("commandFactory")
  ICommandFactory commandFactory;
  
  @PostConstruct
  public void initialize() {
    this.commandMap = new HashMap<Integer, ACommand>();
    observers = new ArrayList<Observer>();
    observers.add(serverThread);
    observers.add(runController);
    observers.add(this);
    //this.commandControllerObservers = new HashMap<String, Observer>();
  }
  
  @RequestMapping(value= "/command/{commandType}", method = RequestMethod.POST,  
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<String> createCommand(@PathVariable String commandType, @RequestBody String clientVariables) {
    System.out.println("RCam Coordinator - CommandController - Receieved a request to create " + commandType);
    
    //clientController.register(commandFactory.createCommand(commandType, new JSONObject(clientVariables)));
    ACommand newCommand = commandFactory.createCommand(commandType, new JSONObject(clientVariables));
    
    commandMap.put(newCommand.getAckNumber(), newCommand);
    
    
    
    newCommand.addObservers(observers);
    //for(String server : commandControllerObservers.keySet()) {
    //  newCommand.addObserver(commandControllerObservers.get(server));
    //}
    
    newCommand.setServers(serverThread.getConnectedServers());
    
    newCommand.setState(new ReadyToSendState());
    
    
    return new ResponseEntity<String>("done",HttpStatus.OK);
  }

  public ACommand getCommand(Integer ackNumber) {
    return this.commandMap.get(ackNumber);
  }
  
  public void update(Observable o, Object arg) {
    //if(o instanceof ServerThread) {
    //  System.out.println("RCam Coordinator - CommandController - Received a notification that there's a new connected server " + arg);
    //  commandControllerObservers.put((String) arg, new CommandControllerObserver());
   // }
    
    
    if(o instanceof ACommand) {
      ACommand newCommand = (ACommand) o;
      System.out.println("RCam Coordinator - CommandController - Received a notification from Command("+newCommand.getCommandName()+"["+newCommand.getAckNumber()+"])");
      Entry<ACommand, Entry<String, ICommandState>> details = (Entry<ACommand, Entry<String, ICommandState>> ) arg;
      String server = details.getValue().getKey();
      //newCommand = details.getKey();
      System.out.println("RCam Coordinator - CommandController - Updating a related command on server " + server);
      System.out.println("RCam Coordinator - CommandController - updating related command with ackNumber " + newCommand.getAckNumber());
      System.out.println("RCam Coordinator - CommandController - updating related command with variable ackNumber " + newCommand.getClientVariable("ackNumber"));
      
      //newCommand.addObserver();
      
      //newCommand.deleteObserver(this);
      newCommand.doRelatedCommandAction(this, server);
      //if(newCommand != null && newCommand.isNoLongerNew()) {
      //  newCommand.deleteObserver(this);
      //}
      
      //for(String server : commandControllerObservers.keySet()) {
        
     // }
      //newCommand.deleteObserver(this);
    } 
  }
}
