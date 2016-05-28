package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.nio.CharBuffer;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.INetworkResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class AckedCommandAction extends AResponseAction implements ICommandAction {
  
  final String semaphoreKey = "Ack";
    
  Integer ackNumber;
  
  
  public AckedCommandAction() {
   super();
   
  }
  
  @Override
  public void setCommandDetails(ACommand command) {
    synchronized(command) {
      if(command != null) {
        super.setCommandDetails(command);
        ackNumber = command.getAckNumber();
        if(command.getCountdownLatch("Ack") != null) {
          synchronized(command.getCountdownLatch("Ack")) {
            command.getCountdownLatch("Ack").countDown();
          }
        }
      }
    }
  }
  
  

  public void takeNecessaryInfo(IConfigurationProvider configurationProvider) {
    
  }
  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
    return null;
  }
  public Callable<ICommandAction> nextAction() {
    return null;
  }



  
}
