package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.INetworkResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.IRunResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

public class RunNewCommandAction extends ARunNetworkResponseAction implements ICommandAction, INetworkResponseAction, IRunResponseAction {

  CountDownLatch waitForIt;
  ACommand command;
  public RunNewCommandAction() {
    this.waitForIt = new CountDownLatch(1);
  }
  //JSONObject details;
  String executable;
  Set<String> connectedClients;
  
  public void takeNecessaryInfo(IConfigurationProvider configurationProvider) {
    JSONObject commandConfiguration = configurationProvider.getCommandConfiguration(getCommandName()); 
    this.executable = commandConfiguration.getJSONObject("executables").getString("RunNewCommandAction");
  }
  
  @Override
  public void setCommandDetails(ACommand command) {
    super.setCommandDetails(command);
    this.command = command;
  }
  
  
  public Callable<Integer> getRunCallable() {
    return new Callable<Integer>() {
      public Integer call() {
        ProcessBuilder pb = new ProcessBuilder(executable);
        Process process;
        try {
          process = pb.start();
          BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
          String line;

          while ((line = br.readLine()) != null) {
              System.out.println(line);
          }
          process.waitFor();
          return process.exitValue();
      
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
        
        return null;
      }
    };
  }

  

  public Callable<Integer> getNetworkCallable(final ServerThread serverThread) {
    synchronized(serverThread) {
      final ICommandAction commandAction = this;


      
      return new Callable<Integer>() {
        public Integer call() {
          try {
            
            System.out.println("GetNetworkCallable will say that there are " + serverThread.getConnectedServers().size() + " connected clients"); 
            connectedClients = serverThread.getConnectedServers();
            waitForIt.countDown();
            serverThread.send(commandAction);
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }

          return 0;
        }
      };
    }
  }
  
  public Callable<ICommandAction> nextAction() {
    final Integer clientSize = connectedClients.size();
    return new Callable<ICommandAction>() {
      public ICommandAction call() {
        command.addPendingAction(new AwaitingAckCommandAction(clientSize));
        
        return null;
      }
    };
  }

}
