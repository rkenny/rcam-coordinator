package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import tk.bad_rabbit.rcam.distributed_backend.command.CommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.Pair;
import tk.bad_rabbit.rcam.distributed_backend.command.StateObject;
import tk.bad_rabbit.rcam.distributed_backend.commandqueuer.ICommandQueuer;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

@Service(value="commandQueuer")
@Scope("singleton")
public class CommandQueuer implements ICommandQueuer  {
  Map<String, Map<Integer, ACommand>> serverIncomingCommandQueue;
  Map<String, Map<Integer, ACommand>> serverOutgoingCommandQueue;
  
  
  @Autowired
  @Qualifier("configurationProvider")
  IConfigurationProvider configurationProvider;
  
  public CommandQueuer() {
    
  }
  
  @PostConstruct
  public void initializeCommandQueuer() {
    System.out.println("Initializing command queuer");
    List<String> serverList = configurationProvider.getBackendList();
    serverIncomingCommandQueue = new ConcurrentHashMap<String, Map<Integer, ACommand>>();
    serverOutgoingCommandQueue = new ConcurrentHashMap<String, Map<Integer, ACommand>>();
    
    for(String server : serverList) {
      Map<Integer, ACommand> incomingCommandsMap = Collections.synchronizedMap(new LinkedHashMap<Integer, ACommand>());
      Map<Integer, ACommand> outgoingCommandsMap = Collections.synchronizedMap(new LinkedHashMap<Integer, ACommand>());
      System.out.println("Adding a commandQueue for " + server);
      serverIncomingCommandQueue.put(server, incomingCommandsMap);
      serverOutgoingCommandQueue.put(server, outgoingCommandsMap);
      
    }
  
  }
  

  public void addIncomingCommand(String server, ACommand command) {
    serverIncomingCommandQueue.get(server).put(command.getAckNumber(), command);
  }

  public void addOutgoingCommand(String server, ACommand command) {
    serverOutgoingCommandQueue.get(server).put(command.getAckNumber(), command);
  }

  public List<Pair<String, ACommand>> getNextIncomingCommandResults() {
    List<Pair<String, ACommand>> incomingCommandResults = new ArrayList<Pair<String, ACommand>>();
    //System.out.println("CommandQueuer getNextIncomingCommandResults()");
    synchronized(serverIncomingCommandQueue) {
      Iterator<String> serverIterator = serverIncomingCommandQueue.keySet().iterator();
      String server;
      if(serverIterator.hasNext()) {
        //System.out.println("CommandQueuer adding another Server entry");
        server = serverIterator.next();
        incomingCommandResults.add(new Pair<String, ACommand>(server, getNextIncomingCommandResult(server)));
      }
    }
    
    //System.out.println("CommandQueuer returning NextIncomingCommandResults");
    return incomingCommandResults;
  }
  
  public ACommand getNextIncomingCommandResult(String server) {
    ACommand returnCommand = null;
    //System.out.println("CommandQueuer getNextIncomingCommandResult");
    Map<Integer, ACommand> incomingCommands = serverIncomingCommandQueue.get(server);
    synchronized(incomingCommands) {
      Collection<ACommand> commands = incomingCommands.values();
      Iterator<ACommand> i = commands.iterator();
      ACommand command = null;
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
  
  public ACommand getNextOutgoingCommand(String server, StateObject state) {
    Map<Integer, ACommand> outgoingCommands = serverOutgoingCommandQueue.get(server);
    
    synchronized(outgoingCommands) {
      Collection<ACommand> commands =  outgoingCommands.values();
      Iterator<ACommand> i = commands.iterator();
      ACommand command;
      if(i.hasNext()) {
        command = i.next();
        //if(command.isInState(state)) {
        //  return command;
//        }
      }
      return null;
    }
  }

  public ACommand getNextIncomingCommand(String server, StateObject state) {
    Map<Integer, ACommand> incomingCommands = serverIncomingCommandQueue.get(server);
    
    synchronized(incomingCommands) {
      Collection<ACommand> commands =  incomingCommands.values();
      Iterator<ACommand> i = commands.iterator();
      ACommand command;
      if(i.hasNext()) {
        command = i.next();
        //if(command.isInState(state)) {
        //  return command;
        // }
      }
      return null;
    }
  }
  
  public Map<Integer, ACommand> getIncomingCommandQueue(String server) {
    return serverIncomingCommandQueue.get(server);
  }

  public Map<Integer, ACommand> getOutgoingCommandQueue(String server) {
    return serverOutgoingCommandQueue.get(server);
  }

  public void setResultCodeForCommand(String server, Integer ackNumber, String returnCode) {
    this.serverOutgoingCommandQueue.get(server).get(ackNumber).setReturnCode(returnCode);
  }
  
  public void setResultCodeForCommand(String server, String ackNumber, String returnCode) {
    this.setResultCodeForCommand(server, Integer.parseInt(ackNumber), returnCode);
  }
  
  public void setStateForCommand(String server, Integer ackNumber, StateObject commandState) {
    this.serverOutgoingCommandQueue.get(server).get(ackNumber).setState(commandState);
  }
  
  public void setStateForCommand(String server, String ackNumber, StateObject commandState) {
    this.setStateForCommand(server, Integer.parseInt(ackNumber), commandState);
  }
  
  public void removeOutgoingCommand(ACommand command) {
    Collection<Map<Integer, ACommand>> outgoingCommandQueues = serverOutgoingCommandQueue.values();
    Iterator<Map<Integer, ACommand>> outgoingCommandQueueIterator = outgoingCommandQueues.iterator();
    Map<Integer, ACommand> outgoingCommandQueue;
    while(outgoingCommandQueueIterator.hasNext()) {
      outgoingCommandQueue = outgoingCommandQueueIterator.next();
      if(outgoingCommandQueue.containsKey(command.getAckNumber())) {
        outgoingCommandQueue.remove(command.getAckNumber());
      }
    }
  }

  public Collection<Pair<ACommand, String>> getCommandReturnCode() {
    
    Map<Integer, Pair<ACommand, String>> commandReturnCodes = new HashMap<Integer, Pair<ACommand, String>>();
    synchronized(this.serverOutgoingCommandQueue) {
      Collection<Map<Integer, ACommand>> outgoingCommandQueues = serverOutgoingCommandQueue.values();
      Iterator<Map<Integer, ACommand>> outgoingCommandQueueIterator = outgoingCommandQueues.iterator();
      
      Map<Integer, ACommand> outgoingCommandQueue;

      Iterator<ACommand> outgoingCommandIterator;
      ACommand command;
      
      while(outgoingCommandQueueIterator.hasNext()) {
        outgoingCommandQueue = outgoingCommandQueueIterator.next();
        outgoingCommandIterator = outgoingCommandQueue.values().iterator();
        while(outgoingCommandIterator.hasNext()) {
          command = outgoingCommandIterator.next();
          if(!commandReturnCodes.containsKey(command.getAckNumber())
              || commandReturnCodes.get(command.getAckNumber()).getRight() != "0") {
            commandReturnCodes.put(command.getAckNumber(), new Pair<ACommand, String>(command, command.getReturnCode()));
          } 
          
        }
        
      }
    }
    return commandReturnCodes.values();
  }

}
