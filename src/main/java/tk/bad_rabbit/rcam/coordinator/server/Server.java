package tk.bad_rabbit.rcam.coordinator.server;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.commands.CommandController;


@Service(value="server")
@Scope("singleton")
public class Server {
  @Autowired
  ServerThread serverThread;
  
  @Autowired
  CommandController commandController;
  
  @PostConstruct
  public void initializeServer() {
    System.out.println("RCam Coordinator - The coordinator will accept client connections.");
    serverThread.injectCommandController(commandController);
    
    serverThread.start();
  }
  
  public void send(ACommand newCommand) {
    synchronized(newCommand) {
      serverThread.send(newCommand);
    }
  }
  
}
