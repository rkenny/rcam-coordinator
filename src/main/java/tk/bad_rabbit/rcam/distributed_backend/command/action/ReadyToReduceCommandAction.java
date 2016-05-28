package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.INetworkResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class ReadyToReduceCommandAction  extends AResponseAction implements ICommandAction, IRelatedCommandAction{
  ACommand command;
  
  @Override
  public void setCommandDetails(ACommand command) {
    super.setCommandDetails(command);
    this.command = command;
    command.getCountdownLatch("Reduce").countDown();
    if(command.getCountdownLatch("Reduce") != null) {
      synchronized(command.getCountdownLatch("Reduce")) {
        command.getCountdownLatch("Reduce").countDown();
      }
    }
//    if(command.getSemaphore(semaphoreKey) == null) {
//      setSemaphore(semaphoreKey, new Semaphore(permitsRequired));
//      getSemaphore(semaphoreKey).drainPermits();
//    }
  }
  
  public Callable<Integer> getRelatedCallable(final CommandController commandController) {
    synchronized(this) {
      final Integer _ackNumber = ackNumber;
      final ICommandAction _this = this;
      return new Callable<Integer>() {
        public Integer call() {

          return 0;
        }
      };
    }
  }


  public void takeNecessaryInfo(IConfigurationProvider configurationProvider) {
    // TODO Auto-generated method stub
    
  }

  public Callable<ICommandAction> nextAction() {
    // TODO Auto-generated method stub
    return null;
  }

}
