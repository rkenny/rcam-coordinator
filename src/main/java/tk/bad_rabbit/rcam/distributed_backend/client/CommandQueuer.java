package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tk.bad_rabbit.rcam.distributed_backend.command.CommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.commandqueuer.ICommandQueuer;

//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class CommandQueuer implements ICommandQueuer {
  Map<String, Map<Integer, ICommand>> serverIncomingCommandQueue2;
  Map<String, Map<Integer, ICommand>> serverOutgoingCommandQueue2;
  
  //Map<String, Queue<ICommand>> serverIncomingCommandQueue;
  //Queue<ICommand> incomingCommandsQueue = null;//
  //Map<String, Queue<ICommand>> serverOutgoingCommandQueue = null;
  
  public CommandQueuer(List<String> serverList) {
    serverIncomingCommandQueue2 = new ConcurrentHashMap<String, Map<Integer, ICommand>>();
    serverOutgoingCommandQueue2 = new ConcurrentHashMap<String, Map<Integer, ICommand>>();
    
    //serverIncomingCommandQueue = new ConcurrentHashMap<String, Queue<ICommand>>();
    //serverOutgoingCommandQueue = new ConcurrentHashMap<String, Queue<ICommand>>();
    
    for(String server : serverList) {
      Map<Integer, ICommand> incomingCommandsMap = Collections.synchronizedMap(new LinkedHashMap<Integer, ICommand>());
      Map<Integer, ICommand> outgoingCommandsMap = Collections.synchronizedMap(new LinkedHashMap<Integer, ICommand>());
      
      serverIncomingCommandQueue2.put(server, incomingCommandsMap);
      serverOutgoingCommandQueue2.put(server, outgoingCommandsMap);
      
      //serverIncomingCommandQueue.put(server, new ConcurrentLinkedQueue<ICommand>());
      //serverOutgoingCommandQueue.put(server, new ConcurrentLinkedQueue<ICommand>());
    }
  }

  public void addIncomingCommand(String server, ICommand command) {
    
    serverIncomingCommandQueue2.get(server).put(command.getAckNumber(), command);
    //serverIncomingCommandQueue.get(server).add(command);

  }

  public void addOutgoingCommand(String server, ICommand command) {
    
    serverOutgoingCommandQueue2.get(server).put(command.getAckNumber(), command);
    //serverOutgoingCommandQueue.get(server).add(command);
  }

  public ICommand getNextOutgoingCommand(String server, CommandState state) {
    Map<Integer, ICommand> outgoingCommands = serverOutgoingCommandQueue2.get(server);
    
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
    //return serverOutgoingCommandQueue.get(server).poll();
  }

  public ICommand getNextIncomingCommand(String server, CommandState state) {
    Map<Integer, ICommand> incomingCommands = serverIncomingCommandQueue2.get(server);
    
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
    //return serverOutgoingCommandQueue.get(server).poll();
  }
  
  //public ICommand getNextIncomingCommand(String server) {
    //return serverIncomingCommandQueue.get(server).poll();
  //}

  //public Queue<ICommand> getIncomingCommandQueue(String server) {
  public Map<Integer, ICommand> getIncomingCommandQueue(String server) {
  // TODO Auto-generated method stub
    //return serverIncomingCommandQueue.get(server);
    return serverIncomingCommandQueue2.get(server);
  }

  //public Queue<ICommand> getOutgoingCommandQueue(String server) {
  public Map<Integer, ICommand> getOutgoingCommandQueue(String server) {
    // TODO Auto-generated method stub
    //return serverOutgoingCommandQueue.get(server);
    return serverOutgoingCommandQueue2.get(server);
  }

}
