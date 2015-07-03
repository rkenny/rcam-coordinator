package tk.bad_rabbit.rcam.spring.runcontroller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.command.Pair;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.commandqueuer.ICommandQueuer;

@Service
public class RunController{
  boolean running;
  ExecutorService commandExecutor;
  List<Future<Pair<Integer, Integer>>> commandResults; // commands return 'true' for success, 'false' for fail
  
  @Autowired
  @Qualifier("commandQueuer")
  ICommandQueuer commandQueuer;
  
  @Autowired
  @Qualifier("commandFactory")
  ICommandFactory commandFactory;
  
  public RunController() {
   
  }
  
  @PostConstruct
  public void initializeRunController() {
    this.commandExecutor = Executors.newFixedThreadPool(5);
    commandResults = new ArrayList<Future<Pair<Integer, Integer>>>();
    //runControllerThread = new Thread(this);
    //runControllerThread.run();
    System.out.println("RunController created");
  }
  
//  public RunController(ICommandQueuer commandQueuer, ICommandFactory commandFactory) {
//    this.commandQueuer = commandQueuer;
//    commandExecutor = Executors.newFixedThreadPool(5);
//    commandResults = new ArrayList<Future<Pair<Integer, Integer>>>();
//    this.commandFactory = commandFactory;
//  }
//  
//  public RunController(ICommandQueuer commandQueuer, IConfigurationProvider configurationProvider) {
//    this.commandQueuer = commandQueuer;
//    commandExecutor = Executors.newFixedThreadPool(5);
//    commandResults = new ArrayList<Future<Pair<Integer, Integer>>>();
//    commandFactory = new CommandFactory(configurationProvider.getCommandConfigurations(), 
//        configurationProvider.getCommandVariables(), configurationProvider.getServerVariables());
//    
//  }
  
  @Scheduled(fixedRate=125) // look into using an Observer for this
  public void run() {
    running = true;
    ICommand command;
    //while(running) {
    //  try {
    //    Thread.sleep(2000);
    //  } catch (InterruptedException e) {
    //    e.printStackTrace();
    //  }
      //System.out.println("Looping in the RunController.");
      List<Pair<String, ICommand>> commandResults = commandQueuer.getNextIncomingCommandResults();
      
      Iterator<Pair<String, ICommand>> serverCommandIterator = commandResults.iterator();
      Pair<String, ICommand> serverCommand;
      synchronized(commandResults) {
        while(serverCommandIterator.hasNext()) {
          serverCommand = serverCommandIterator.next();
          command = serverCommand.getRight();
          //System.out.println("Checking " + serverCommand.getLeft());
          if(command != null) {
            System.out.println("Received a Command(type="+command.getCommandName()+") for commandNumber = " +command.getClientVariable("ackNumber")
                +" resultCode="+command.getClientVariable("resultCode"));
            
          }
        }
      }
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
  }
 
}