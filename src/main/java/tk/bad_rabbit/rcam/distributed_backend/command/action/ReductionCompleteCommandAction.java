package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.INetworkResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.IRunResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

public class ReductionCompleteCommandAction extends AResponseAction implements ICommandAction, INetworkResponseAction, IRunResponseAction {

  Integer ackNumber;
  
  @Override
  public void setCommandDetails(ACommand command) {
    super.setCommandDetails(command);
    ackNumber = command.getAckNumber();
  }


  private String executable;  
  public void takeNecessaryInfo(IConfigurationProvider configurationProvider) {
    this.executable = configurationProvider.getCommandConfiguration(commandName).getJSONObject("executables").getString(this.getClass().getSimpleName());
  }
  
  public CharBuffer asCharBuffer() {
    JSONObject reduceComplete = new JSONObject();
    JSONObject commandVariables = new JSONObject();
    commandVariables.put("commandName", commandName);
    commandVariables.put("ackNumber", ackNumber);
    reduceComplete.put("commandName", "ReduceComplete");
    reduceComplete.put("ackNumber", 0);
    reduceComplete.put("details", commandVariables);
    return CharBuffer.wrap(reduceComplete.toString() + "\n");
  }

  public Callable<ICommandAction> nextAction() {
    // TODO Auto-generated method stub
    return null;
  }

  public Callable<Integer> getRunCallable() {
    synchronized(this) {
      final String thisExecutable = executable;
    
      return new Callable<Integer>() {
        public Integer call() {
          ProcessBuilder pb = new ProcessBuilder(thisExecutable);
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
  }


  public Callable<Integer> getNetworkCallable(final ServerThread serverThread) {
    synchronized(this) {
      final ICommandAction thisAction = this;
      return new Callable<Integer>() {
        public Integer call() {
          try {
            serverThread.send(thisAction);
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          return 0;
        }
      };
    }
  }


}
