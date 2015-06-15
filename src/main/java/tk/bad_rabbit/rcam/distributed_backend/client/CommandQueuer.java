package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.commandqueuer.ICommandQueuer;

public class CommandQueuer implements ICommandQueuer {
  Map<String, Queue<ICommand>> serverIncomingCommandQueue;
  //Queue<ICommand> incomingCommandsQueue = null;
  Map<String, Queue<ICommand>> serverOutgoingCommandQueue = null;
  
  public CommandQueuer(List<String> serverList) {
    serverIncomingCommandQueue = new ConcurrentHashMap<String, Queue<ICommand>>();
    serverOutgoingCommandQueue = new ConcurrentHashMap<String, Queue<ICommand>>();
    
    for(String server : serverList) {
      serverIncomingCommandQueue.put(server, new ConcurrentLinkedQueue<ICommand>());
      serverOutgoingCommandQueue.put(server, new ConcurrentLinkedQueue<ICommand>());
    }
  }

  public void addIncomingCommand(String server, ICommand command) {
    serverIncomingCommandQueue.get(server).add(command);

  }

  public void addOutgoingCommand(String server, ICommand command) {
    serverOutgoingCommandQueue.get(server).add(command);
  }

  public ICommand getNextOutgoingCommand(String server) {
    return serverOutgoingCommandQueue.get(server).poll();
  }

  public ICommand getNextIncomingCommand(String server) {
    return serverIncomingCommandQueue.get(server).poll();
  }

  public Queue<ICommand> getIncomingCommandQueue(String server) {
    // TODO Auto-generated method stub
    return serverIncomingCommandQueue.get(server);
  }

  public Queue<ICommand> getOutgoingCommandQueue(String server) {
    // TODO Auto-generated method stub
    return serverOutgoingCommandQueue.get(server);
  }

}
