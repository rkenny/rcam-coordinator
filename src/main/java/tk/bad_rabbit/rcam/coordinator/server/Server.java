package tk.bad_rabbit.rcam.coordinator.server;

import java.util.Observer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;


@Service(value="server")
@Scope("singleton")
public class Server {
  @Autowired
  ServerThread serverThread;
  
  @Autowired
  @Qualifier("commandController")
  Observer commandController;
  
  @PostConstruct
  public void initializeServer() {
    System.out.println("RCam Coordinator - The coordinator will accept client connections.");
    
    serverThread.injectObserver(commandController);
    serverThread.injectObserver(serverThread);
    
    serverThread.start();
  }
  
  public void send(ACommand newCommand) {
    synchronized(newCommand) {
      serverThread.send(newCommand);
    }
  }
  
}
