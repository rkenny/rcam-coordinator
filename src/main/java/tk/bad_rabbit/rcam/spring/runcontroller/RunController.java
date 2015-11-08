package tk.bad_rabbit.rcam.spring.runcontroller;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReducedState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ErrorCommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;

@Controller
@Scope("singleton")
public class RunController implements Observer {
  boolean running;
  ExecutorService commandExecutor;
  List<Future<Pair<Integer, Integer>>> commandResults;
  ConcurrentHashMap<String, ConcurrentHashMap<Integer, ACommand>> commandList;
  
  
  @Autowired
  @Qualifier("commandFactory")
  ICommandFactory commandFactory;
  
  public RunController() {
   this.commandList = new  ConcurrentHashMap<String, ConcurrentHashMap<Integer, ACommand>>();
  }
  
  public void commandResultReceived(String server, Integer ackNumber, Integer resultCode) {
    if(!(commandList.get(server) == null) && 
        !(commandList.get(server).get(ackNumber) == null) && 
        !(commandList.get(server).get(ackNumber).stateEquals(server, new ErrorCommandState()))) { 
      commandList.get(server).get(ackNumber).setReturnCode(resultCode);
      commandList.get(server).get(ackNumber).setState(server, new CommandCompletedState());
      
    }    
  }
  
  public void readyToReduce(String server, ACommand command) {
    
    if(command.isReadyToReduce()) {
       commandExecutor.submit(command.reduce()); 
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
  }
  
  public void update(Observable updatedCommand, Object arg) {

    synchronized(updatedCommand) {

      if(arg instanceof AbstractMap.Entry) {
        Map.Entry<ACommand, Map.Entry<String, ICommandState>> commandDetails = (Map.Entry<ACommand, Map.Entry<String, ICommandState>>) arg;
        String server = commandDetails.getValue().getKey();
        
        if(!commandList.containsKey(server)) {
          ConcurrentHashMap<Integer, ACommand> newCommandEntry = new ConcurrentHashMap<Integer, ACommand>();
          commandList.put(server,  newCommandEntry);
        }
        
        
        if(!commandList.get(server).containsKey(((ACommand) updatedCommand).getAckNumber())) {
          commandList.get(server).put(((ACommand) updatedCommand).getAckNumber(), (ACommand) updatedCommand);
        }
        
        if(((ACommand) updatedCommand).stateEquals(server, new ErrorCommandState())) {
          removeCommand(server, (ACommand) updatedCommand);
        }
      }

    }
  }
 
}