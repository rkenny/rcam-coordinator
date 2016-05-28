package tk.bad_rabbit.rcam.coordinator.server;

import java.io.IOException;
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
  
  
  @PostConstruct
  public void initializeServer() {
    System.out.println("RCam Coordinator - The coordinator will accept client connections.");
    
    //serverThread.injectObserver(commandController);
    //serverThread.injectObserver(serverThread);
    
    //serverThread.addObserver(commandController);
    
    serverThread.start();
  }
  
  public void send(ACommand newCommand) {
    synchronized(newCommand) {
      try {
        serverThread.send(newCommand);
      } catch (IOException e) {
        System.out.println("Server-  sending the new command failed");
      }
    }
  }
  
}
