package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import tk.bad_rabbit.rcam.distributed_backend.command.CommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.command.Pair;
import tk.bad_rabbit.rcam.distributed_backend.commandqueuer.ICommandQueuer;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

@Service(value="commandQueuer")
@Scope("singleton")
public class CommandQueuer implements ICommandQueuer, Observer {
  Map<String, Map<Integer, ICommand>> serverIncomingCommandQueue;
  Map<String, Map<Integer, ICommand>> serverOutgoingCommandQueue;
  
  
  @Autowired
  @Qualifier("configurationProvider")
  IConfigurationProvider configurationProvider;
  
  public CommandQueuer() {
    
  }
  
  public void update(Observable updatedCommand, Object arg) {
    System.out.println("A command was updated.");
  }
  
  @PostConstruct
  public void initializeCommandQueuer() {
    System.out.println("Initializing command queuer");
    List<String> serverList = configurationProvider.getBackendList();
    serverIncomingCommandQueue = new ConcurrentHashMap<String, Map<Integer, ICommand>>();
    serverOutgoingCommandQueue = new ConcurrentHashMap<String, Map<Integer, ICommand>>();
    
    for(String server : serverList) {
      Map<Integer, ICommand> incomingCommandsMap = Collections.synchronizedMap(new LinkedHashMap<Integer, ICommand>());
      Map<Integer, ICommand> outgoingCommandsMap = Collections.synchronizedMap(new LinkedHashMap<Integer, ICommand>());
      System.out.println("Adding a commandQueue for " + server);
      serverIncomingCommandQueue.put(server, incomingCommandsMap);
      serverOutgoingCommandQueue.put(server, outgoingCommandsMap);
      
    }
  
  }
  

  public void addIncomingCommand(String server, ICommand command) {
    serverIncomingCommandQueue.get(server).put(command.getAckNumber(), command);
  }

  public void addOutgoingCommand(String server, ICommand command) {
    serverOutgoingCommandQueue.get(server).put(command.getAckNumber(), command);
  }

  public List<Pair<String, ICommand>> getNextIncomingCommandResults() {
    List<Pair<String, ICommand>> incomingCommandResults = new ArrayList<Pair<String, ICommand>>();
    //System.out.println("CommandQueuer getNextIncomingCommandResults()");
    synchronized(serverIncomingCommandQueue) {
      Iterator<String> serverIterator = serverIncomingCommandQueue.keySet().iterator();
      String server;
      if(serverIterator.hasNext()) {
        //System.out.println("CommandQueuer adding another Server entry");
        server = serverIterator.next();
        incomingCommandResults.add(new Pair<String, ICommand>(server, getNextIncomingCommandResult(server)));
      }
    }
    
    //System.out.println("CommandQueuer returning NextIncomingCommandResults");
    return incomingCommandResults;
  }
  
  public ICommand getNextIncomingCommandResult(String server) {
    ICommand returnCommand = null;
    //System.out.println("CommandQueuer getNextIncomingCommandResult");
    Map<Integer, ICommand> incomingCommands = serverIncomingCommandQueue.get(server);
    synchronized(incomingCommands) {
      Collection<ICommand> commands = incomingCommands.values();
      Iterator<ICommand> i = commands.iterator();
      ICommand command = null;
      Boolean keepIterating = true;
      if(i.hasNext() && keepIterating) {
        command = i.next();
        //System.out.println("CommandQueuer iteratingAgain");
        
        if(command.isType("CommandResult")) {
          returnCommand = command;
          keepIterating = false;
        }
        i.remove();
      }
    }
    //System.out.println("CommadQueuer returning NextIncomingCommandResult");
    return returnCommand;
  }
  
  public ICommand getNextOutgoingCommand(String server, CommandState state) {
    Map<Integer, ICommand> outgoingCommands = serverOutgoingCommandQueue.get(server);
    
    synchronized(outgoingCommands) {
      Collection<ICommand> commands =  outgoingCommands.values();
      Iterator<ICommand> i = commands.iterator();
      ICommand command;
      if(i.hasNext()) {
        command = i.next();
        if(command.isInState(state)) {
          return command;
        }
      }
      return null;
    }
  }

  public ICommand getNextIncomingCommand(String server, CommandState state) {
    Map<Integer, ICommand> incomingCommands = serverIncomingCommandQueue.get(server);
    
    synchronized(incomingCommands) {
      Collection<ICommand> commands =  incomingCommands.values();
      Iterator<ICommand> i = commands.iterator();
      ICommand command;
      if(i.hasNext()) {
        command = i.next();
        if(command.isInState(state)) {
          return command;
        }
      }
      return null;
    }
  }
  
  public Map<Integer, ICommand> getIncomingCommandQueue(String server) {
    return serverIncomingCommandQueue.get(server);
  }

  public Map<Integer, ICommand> getOutgoingCommandQueue(String server) {
    return serverOutgoingCommandQueue.get(server);
  }

  public void setResultCodeForCommand(String server, Integer ackNumber, String returnCode) {
    this.serverOutgoingCommandQueue.get(server).get(ackNumber).setReturnCode(returnCode);
  }
  
  public void setResultCodeForCommand(String server, String ackNumber, String returnCode) {
    this.setResultCodeForCommand(server, Integer.parseInt(ackNumber), returnCode);
  }
  
  public void setStateForCommand(String server, Integer ackNumber, CommandState commandState) {
    this.serverOutgoingCommandQueue.get(server).get(ackNumber).setState(commandState);
  }
  
  public void setStateForCommand(String server, String ackNumber, CommandState commandState) {
    this.setStateForCommand(server, Integer.parseInt(ackNumber), commandState);
  }
  
  public void removeOutgoingCommand(ICommand command) {
    Collection<Map<Integer, ICommand>> outgoingCommandQueues = serverOutgoingCommandQueue.values();
    Iterator<Map<Integer, ICommand>> outgoingCommandQueueIterator = outgoingCommandQueues.iterator();
    Map<Integer, ICommand> outgoingCommandQueue;
    while(outgoingCommandQueueIterator.hasNext()) {
      outgoingCommandQueue = outgoingCommandQueueIterator.next();
      if(outgoingCommandQueue.containsKey(command.getAckNumber())) {
        outgoingCommandQueue.remove(command.getAckNumber());
      }
    }
  }

  public Collection<Pair<ICommand, String>> getCommandReturnCode() {
    
    Map<Integer, Pair<ICommand, String>> commandReturnCodes = new HashMap<Integer, Pair<ICommand, String>>();
    synchronized(this.serverOutgoingCommandQueue) {
      Collection<Map<Integer, ICommand>> outgoingCommandQueues = serverOutgoingCommandQueue.values();
      Iterator<Map<Integer, ICommand>> outgoingCommandQueueIterator = outgoingCommandQueues.iterator();
      
      Map<Integer, ICommand> outgoingCommandQueue;

      Iterator<ICommand> outgoingCommandIterator;
      ICommand command;
      
      while(outgoingCommandQueueIterator.hasNext()) {
        outgoingCommandQueue = outgoingCommandQueueIterator.next();
        outgoingCommandIterator = outgoingCommandQueue.values().iterator();
        while(outgoingCommandIterator.hasNext()) {
          command = outgoingCommandIterator.next();
          if(!commandReturnCodes.containsKey(command.getAckNumber())
              || commandReturnCodes.get(command.getAckNumber()).getRight() != "0") {
            commandReturnCodes.put(command.getAckNumber(), new Pair<ICommand, String>(command, command.getReturnCode()));
          } 
          
        }
        
      }
    }
    return commandReturnCodes.values();
  }

}
