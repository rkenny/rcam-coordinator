package tk.bad_rabbit.rcam.spring.commandcoordinator;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.action.ICommandAction;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.ConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.observers.ClientObserver;
import tk.bad_rabbit.rcam.distributed_backend.observers.CommandStateObserver;
import tk.bad_rabbit.rcam.spring.commands.CommandController;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;


@Controller("commandCoordinator")
@Scope("singleton")
public class CommandCoordinator implements Observer {

  @Autowired
  @Qualifier("serverThread")
  ServerThread serverThread;
  
  @Autowired
  @Qualifier("runController")
  RunController runController;
  
  @Autowired
  @Qualifier("commandController")
  CommandController commandController;
  
  @Autowired
  @Qualifier("configurationProvider")
  ConfigurationProvider configurationProvider;
  
  CommandStateObserver commandStateObserver;
  ClientObserver clientObserver;
  ExecutorService threadPool = Executors.newFixedThreadPool(3);
  ExecutorService nextThreads = Executors.newFixedThreadPool(5);
  
  @PostConstruct
  public void postConstruct() {
    this.commandStateObserver = new CommandStateObserver(this);
    this.clientObserver = new ClientObserver(this);
  }
 
  
  
  public void update(Observable observedCommand, Object arg) {
    synchronized(observedCommand) {
      
      if(observedCommand instanceof ACommand) {
        final ACommand command = (ACommand) observedCommand;
        threadPool.execute(new Runnable() {

          public void run() {
            ICommandAction pendingAction = command.popAction();
            if(pendingAction != null) {
              pendingAction.takeNecessaryInfo(configurationProvider);
              
              Future<Integer> runAction = runController.handleAction(pendingAction);
              Future<Integer> commandAction = commandController.handleAction(pendingAction);
              Future<Integer> serverAction = serverThread.handleAction(pendingAction);        
              
              try {
                if(runAction != null) { runAction.get(10, TimeUnit.MINUTES); }
                if(serverAction != null) { serverAction.get(10, TimeUnit.MINUTES); }
                if(commandAction != null) { commandAction.get(10, TimeUnit.MINUTES); }
                
                if(pendingAction.nextAction() != null) {
                  nextThreads.submit(pendingAction.nextAction()).get(10, TimeUnit.MINUTES);
                }
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              } catch (TimeoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
          }
          
        }
      );
        
      }
    }
  }

}
