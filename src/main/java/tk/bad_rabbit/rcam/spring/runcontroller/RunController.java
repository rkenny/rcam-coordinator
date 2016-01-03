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

import tk.bad_rabbit.rcam.app.Pair;
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
  List<Future<Pair<Integer, Integer>>> commandResults;
  
  @PostConstruct
  public void initializeRunController() {
    this.commandExecutor = Executors.newFixedThreadPool(5);
    commandResults = new ArrayList<Future<Pair<Integer, Integer>>>();
  }
  
  public void update(Observable o, Object arg) {
    ACommand updatedCommand = (ACommand) o;
    System.out.println("RCam Coordinator - RunController - Receieved an update for command " + updatedCommand.getAckNumber());
    
    if(arg instanceof Entry) {
      Entry<ACommand, Entry<String, ICommandState>> details = (Entry<ACommand, Entry<String, ICommandState>> ) arg;
      String server = details.getValue().getKey();
      updatedCommand = details.getKey();
      System.out.println("RCam Coordinator - CommandController - Updating a related command on server " + server);
      System.out.println("RCam Coordinator - CommandController - updating related command with ackNumber " + updatedCommand.getAckNumber());
      System.out.println("RCam Coordinator - CommandController - updating related command with variable ackNumber " + updatedCommand.getClientVariable("ackNumber"));

      updatedCommand.doRunCommandAction(this, server);
      
    } 
  }
  
  public void reduce(ACommand command) {
    System.out.println("This will reduce Command("+command.getCommandName()+"["+command.getAckNumber()+"])");
  }
  
//  
//  ConcurrentHashMap<String, ConcurrentHashMap<Integer, ACommand>> commandList;
//  
//  
//  @Autowired
//  @Qualifier("commandFactory")
//  ICommandFactory commandFactory;
//  
//  public RunController() {
//  }
//  
//  @PostConstruct
//  public void initialize() {
//    this.commandList = new  ConcurrentHashMap<String, ConcurrentHashMap<Integer, ACommand>>();
//  }
//  
//  public void commandResultReceived(String server, Integer ackNumber, Integer resultCode) {
//    if(commandCanBeReduced(server, ackNumber)) { 
//      commandList.get(server).get(ackNumber).setReturnCode(resultCode);
//      commandList.get(server).get(ackNumber).setState(server, new CommandCompletedState());
//    }    
//  }
//
//  private boolean commandCanBeReduced(String server, Integer ackNumber) {
//    return !(commandList.get(server) == null) && 
//        !(commandList.get(server).get(ackNumber) == null) && 
//        // !(commandList.get(server).get(ackNumber).stateEquals(server, new ErrorCommandState())) &&
//        !(commandList.get(server).get(ackNumber).stateEquals(server, new CommandReducedState()));
//  }
//  
//  public void readyToReduce(String server, ACommand command) {
//    if(command.isReadyToReduce()) {
//       commandExecutor.submit(command.reduce()); 
//       //command.setReducedState();
//     }
//  }
//  
//  public void removeCommand(String server, ACommand command) {
//    System.out.println("RunController removing command " + command.getCommandName() + "[" + command.getAckNumber() + "]");
//    commandList.get(server).remove(command.getAckNumber());
//    command = null;
//  }
//  
//  public synchronized void setCommandState(String client, Integer ackNumber, ICommandState newCommandState) {
//    synchronized(commandList) {
//      System.out.println("RCam Coordinator - RunController - SetCommandState debug info follows:");
//      System.out.println(client);
//      System.out.println(ackNumber);
//      System.out.println(newCommandState.getClass().getSimpleName());
//      
//      
//      System.out.println("CommandList has "+client+"? " + (commandList.get(client) != null));
//      System.out.println("CommandList("+client+") has "+ackNumber+"? " + (commandList.get(client).get(ackNumber) != null));
//      System.out.println("CommandList("+client+").("+ackNumber+" is of class " + commandList.get(client).get(ackNumber).getClass().getSimpleName());
//      
//      commandList.get(client).get(ackNumber).setState(client, newCommandState);
//    }
//  }
//  
////  public synchronized void observeCommand(String client, ACommand command) {
////    synchronized(commandList) {
////      System.out.println("RCam Coordinator - RunController - Client("+client+") will begin observing command " + command.getAckNumber());
////      if(!commandList.contains(client)) {
////        System.out.println("RCam Coordinator - RunController - commandList did not contain " + client);
////        commandList.put(client, new ConcurrentHashMap<Integer, ACommand>());
////      }
////      System.out.println("RCam Coordinator - RunController - commandList("+client+") did not contain " +  command.getAckNumber());
////      commandList.get(client).put(command.getAckNumber(), command);
////      System.out.println("CommandList has "+client+"? " + (commandList.get(client) != null));
////      System.out.println("CommandList("+client+") has "+command.getAckNumber()+"? " + (commandList.get(client).get(command.getAckNumber()) != null));
////      System.out.println("CommandList("+client+").("+command.getAckNumber()+" is of class " + commandList.get(client).get(command.getAckNumber()).getClass().getSimpleName());
////
////      command.addObserver(this);
////    }
////  }
//  
//  public synchronized void observeCommand(ACommand command) {
//    command.addObserver(this);
//  }
//  

//  
//  public void update(Observable updatedCommand, Object arg) {
//    
//    synchronized(commandList) {
//      System.out.println("RCam Coordinator - RunController - Receieved an update for command " + ((ACommand) updatedCommand).getAckNumber());
//      if(arg instanceof AbstractMap.Entry) {
//        Map.Entry<ACommand, Map.Entry<String, ICommandState>> commandDetails = (Map.Entry<ACommand, Map.Entry<String, ICommandState>>) arg;
//        String client = commandDetails.getValue().getKey();
//        
//        if(!commandList.containsKey(client)) {
//          ConcurrentHashMap<Integer, ACommand> newCommandEntry = new ConcurrentHashMap<Integer, ACommand>();
//          System.out.println("RCam Coordinator - RunController - commandList did not contain " + client);
//          commandList.put(client,  newCommandEntry);
//        }
//        
//        
//        if(!commandList.get(client).containsKey(((ACommand) updatedCommand).getAckNumber())) {
//          System.out.println("RCam Coordinator - RunController - commandList("+client+") did not contain " + ((ACommand) updatedCommand).getAckNumber());
//          commandList.get(client).put(((ACommand) updatedCommand).getAckNumber(), (ACommand) updatedCommand);
//        }
//        
//       // if(((ACommand) updatedCommand).stateEquals(client, new ErrorCommandState())) {
//       //   removeCommand(client, (ACommand) updatedCommand);
//       // } else {
//          //((ACommand) updatedCommand).doAction(this, client);
//        //}
//      }
//
//    }
//  }
 
}