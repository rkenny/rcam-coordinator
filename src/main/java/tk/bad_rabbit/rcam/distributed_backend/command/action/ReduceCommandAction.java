package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.IRunResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

public class ReduceCommandAction implements ICommandAction, IRunResponseAction {
  
  ACommand command;
  CountDownLatch latch;
  
  public ReduceCommandAction() {
    super();
    latch = new CountDownLatch(1);
  }
  
  public Callable<Integer> getRunCallable() {
    System.out.println("Running ReduceCommandAction");
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
            latch.countDown();
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


  private String commandName;
  private Integer ackNumber;
  private String executable;
  
  public void setCommandDetails(ACommand aCommand) {
    synchronized(aCommand) {
      if(aCommand != null) {
        this.commandName = aCommand.getCommandName();
        this.ackNumber = aCommand.getAckNumber();
        this.command = aCommand;      
      }
    }
    
  }
  
  public void takeNecessaryInfo(IConfigurationProvider configurationProvider) {
    this.executable = configurationProvider.getCommandConfiguration(commandName).getJSONObject("executables").getString(this.getClass().getSimpleName());
  }

  public Future<Integer> doAction() {
    return null;
  }


  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
    return null;
  }

  public Callable<ICommandAction> nextAction() {
    return new Callable<ICommandAction>() {
      public ICommandAction call() {
        ICommandAction awaitingAckAction = new ReductionCompleteCommandAction();
        try {
          latch.await();
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        command.addPendingAction(awaitingAckAction);
        return null;
      }
    };
  }
  
  public ICommandAction nextActionOld() {
    // Start by
    // 1 - sending a ReductionComplete command
    //    = that (sends a command which) cleans up the environment on each client
    //    = that cleans up the environment on the coordinator
    // 2 - add variables back in.
    
    return new ReductionCompleteCommandAction();
  }

}
