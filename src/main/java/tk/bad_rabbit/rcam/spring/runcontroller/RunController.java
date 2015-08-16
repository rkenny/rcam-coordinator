package tk.bad_rabbit.rcam.spring.runcontroller;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import tk.bad_rabbit.rcam.app.Pair;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandCompletedState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReadyToReduceState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReducedState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;

@Controller
@Scope("singleton")
public class RunController implements Observer {
  boolean running;
  ExecutorService commandExecutor;
  List<Future<Pair<Integer, Integer>>> commandResults; // commands return 'true' for success, 'false' for fail
  ConcurrentHashMap<String, ConcurrentHashMap<Integer, ACommand>> commandList;
  
  
  @Autowired
  @Qualifier("commandFactory")
  ICommandFactory commandFactory;
  
  public RunController() {
   this.commandList = new  ConcurrentHashMap<String, ConcurrentHashMap<Integer, ACommand>>();
  }
  
  public void commandResultReceived(String server, Integer ackNumber, String resultCode) {
    commandList.get(server).get(ackNumber).setReturnCode(resultCode);
    commandList.get(server).get(ackNumber).setState(server, new CommandCompletedState());
    
    System.out.println("Command completed state reached");
  }
  
  public void readyToReduce(String server, ACommand command) {
    //check each server for command. If they're all done, reduce it.
    //for now, though, there's only one command. So, reduce it.
    System.out.println("need to check if the command is ready to reduce on all servers, then reduce it if it is.");
    System.out.println("It is command "  + command.getAckNumber());
    
    
    if(command.isReadyToReduce()) {
       commandExecutor.execute(command.reduce()); 
       command.setState(server, new CommandReducedState());
     }
    
  }
  
  public void removeCommand(String server, ACommand command) {
    commandList.get(server).remove(command.getAckNumber());
    command = null;
  }
  
  public void observeCommand(ACommand command) {
    command.addObserver(this);
  }
  
  @PostConstruct
  public void initializeRunController() {
    this.commandExecutor = Executors.newFixedThreadPool(5);
    commandResults = new ArrayList<Future<Pair<Integer, Integer>>>();
    System.out.println("RunController created");
  }
  
  public void update(Observable updatedCommand, Object arg) {

    synchronized(updatedCommand) {
      if(arg instanceof AbstractMap.Entry) {
        Map.Entry<ACommand, Map.Entry<String, ICommandState>> doesThisWork = (Map.Entry<ACommand, Map.Entry<String, ICommandState>>) arg;
        String server = doesThisWork.getValue().getKey();
        if(!commandList.containsKey(server)) {
          ConcurrentHashMap<Integer, ACommand> newCommandEntry = new ConcurrentHashMap<Integer, ACommand>();
          commandList.put(server,  newCommandEntry);
        }
        
        
        if(!commandList.get(server).containsKey(((ACommand) updatedCommand).getAckNumber())) {
          commandList.get(server).put(((ACommand) updatedCommand).getAckNumber(), (ACommand) updatedCommand);
        }
      }

    }
  }
 
}