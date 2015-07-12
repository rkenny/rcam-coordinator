package tk.bad_rabbit.rcam.spring.runcontroller;

import java.util.ArrayList;
import java.util.List;
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

import tk.bad_rabbit.rcam.distributed_backend.client.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.AckedState;
import tk.bad_rabbit.rcam.distributed_backend.command.Pair;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;

@Controller
@Scope("singleton")
public class RunController implements Observer {
  boolean running;
  ExecutorService commandExecutor;
  List<Future<Pair<Integer, Integer>>> commandResults; // commands return 'true' for success, 'false' for fail
  ConcurrentHashMap<Integer, ACommand> commandList;
  
  
  @Autowired
  @Qualifier("commandFactory")
  ICommandFactory commandFactory;
  
  public RunController() {
   this.commandList = new ConcurrentHashMap<Integer, ACommand>();
  }
  
  public void ackCommandReceived(Integer ackNumber) {
    commandList.get(ackNumber).setState(new AckedState());
  }
  
  public void commandResultReceived(Integer ackNumber, String resultCode) {
    commandList.get(ackNumber).setReturnCode(resultCode);
    commandList.get(ackNumber).setState(new CommandCompletedState());
  }
  
  public void readyToReduce(ACommand command) {
    //check each server for command. If they're all done, reduce it.
    //for now, though, there's only one command. So, reduce it.
    commandExecutor.execute(command.reduce());
    command.setState(new CommandReducedState());
  }
  
  public void removeCommand(ACommand command) {
    commandList.remove(command.getAckNumber());
  }
  
  public void observeCommand(ACommand command) {
    command.addObserver(this);
  }
  
  @PostConstruct
  public void initializeRunController() {
    this.commandExecutor = Executors.newFixedThreadPool(5);
    commandResults = new ArrayList<Future<Pair<Integer, Integer>>>();
    //runControllerThread = new Thread(this);
    //runControllerThread.run();
    System.out.println("RunController created");
  }
  
  //@Scheduled(fixedRate=125) // look into using an Observer for this
  //public void run() {
  //  running = true;
    //while(running) {
    //  try {
    //    Thread.sleep(2000);
    //  } catch (InterruptedException e) {
    //    e.printStackTrace();
    //  }
      //System.out.println("Looping in the RunController.");
    //  handleCompletedCommands();
    //  runCommandReductions();
      
      
//      while((command = commandQueuer.getNextReadyToExecuteCommand()) != null) {
//        if(!command.isIgnored()) {
//          commandResults.add(commandExecutor.submit(command.setDone()));
//        } else {
//          command.setDone();
//        }
//       }
//      
//      Iterator<Future<Pair<Integer, Integer>>> resultIterator = commandResults.iterator();
//      while(resultIterator.hasNext()) {
//        Future<Pair<Integer, Integer>> commandResult = resultIterator.next();
//        try {
//          ICommand returnCommand = commandFactory.createResultCommand(commandResult.get());
//          commandQueuer.addOutgoingCommand(returnCommand.readyToSend());
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        } catch (ExecutionException e) {
//          e.printStackTrace();
//        }
//        resultIterator.remove();
//      }
//    }
    //commandExecutor.shutdown();
  //}
  
  //private void runCommandReductions() {
    //Collection<Pair<ACommand, String>> commands = commandQueuer.getCommandReturnCode();
    //Iterator<Pair<ACommand, String>> commandIterator = commands.iterator();
    //Pair<ACommand, String> commandPair;
    //synchronized(commands) {
    //  while(commandIterator.hasNext()) {
    //    commandPair = commandIterator.next();
    //    String returnCode = commandPair.getRight();
    //    if(returnCode != null && returnCode.equals("0")) {
    //      this.commandExecutor.execute(commandPair.getLeft().reduce());
    //     // commandQueuer.removeOutgoingCommand(commandPair.getLeft());
    //      commandIterator.remove();
    //    }
        
    //  }
   // }
  //}

//private void handleCompletedCommands() {
//  List<Pair<String, ACommand>> commandResults = commandQueuer.getNextIncomingCommandResults();
//  ICommand command;
//    
//  Iterator<Pair<String, ACommand>> serverCommandIterator = commandResults.iterator();
//  Pair<String, ACommand> serverCommand;
//  synchronized(commandResults) {
//    while(serverCommandIterator.hasNext()) {
//      serverCommand = serverCommandIterator.next();
//      command = serverCommand.getRight();
//      //System.out.println("Checking " + serverCommand.getLeft());
//      if(command != null) {
//        commandQueuer.setResultCodeForCommand(serverCommand.getLeft(), command.getClientVariable("ackNumber"), command.getClientVariable("resultCode"));
//        commandQueuer.setStateForCommand(serverCommand.getLeft(), command.getClientVariable("ackNumber"), new DoneState());
//      }
//    }
//  }
//}



public void update(Observable updatedCommand, Object arg) {
  synchronized(updatedCommand) {
    if(! (commandList.containsKey(((ACommand) updatedCommand).getAckNumber()))) {
      commandList.put(((ACommand) updatedCommand).getAckNumber(), (ACommand) updatedCommand);
    }
    
    ((ACommand) updatedCommand).doAction(this);
  }
  

}
 
}