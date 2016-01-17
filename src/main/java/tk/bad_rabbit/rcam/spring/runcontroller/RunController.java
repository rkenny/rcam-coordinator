package tk.bad_rabbit.rcam.spring.runcontroller;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandCompletedState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReducedState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ErrorCommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;

@Controller(value="runController")
@Scope("singleton")
public class RunController implements Observer {
//  boolean running;
  ExecutorService commandExecutor;
  List<Future<Map.Entry<Integer, Integer>>> commandResults;
  
  @PostConstruct
  public void initializeRunController() {
    this.commandExecutor = Executors.newFixedThreadPool(5);
  }
  
  public void update(Observable o, Object arg) {
    ACommand updatedCommand = (ACommand) o;
    //System.out.println("RCam Coordinator - RunController - Receieved an update for command " + updatedCommand.getAckNumber());
    
    if(arg instanceof Entry) {
      Entry<ACommand, Entry<String, ICommandState>> details = (Entry<ACommand, Entry<String, ICommandState>> ) arg;
      String server = details.getValue().getKey();
      updatedCommand = details.getKey();
      //System.out.println("RCam Coordinator - RunController - Updating a related command on server " + server);
      //System.out.println("RCam Coordinator - RunController - updating related command with ackNumber " + updatedCommand.getAckNumber());
      //System.out.println("RCam Coordinator - RunController - updating related command with variable ackNumber " + updatedCommand.getClientVariable("ackNumber"));

      updatedCommand.doRunCommandAction(this, server);
      
    } 
  }
  
  public Future<Map.Entry<Integer, Integer>> reduce(ACommand command) {
    //System.out.println("RCam Coordinator - RunController - This will reduce Command("+command.getCommandName()+"["+command.getAckNumber()+"])");
    return commandExecutor.submit(command.reduce());
    
  }
   
}