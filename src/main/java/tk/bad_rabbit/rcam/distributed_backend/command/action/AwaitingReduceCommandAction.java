package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.nio.CharBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class AwaitingReduceCommandAction extends AResponseAction implements ICommandAction, IRelatedCommandAction {

  Integer permits;
  ACommand command;
  public AwaitingReduceCommandAction() {
    super();
  }
  
  public AwaitingReduceCommandAction(Integer permitsRequired) {
    super();
    this.permits = permitsRequired;
  }

  @Override
  public void setCommandDetails(ACommand command) {
    super.setCommandDetails(command);
    command.setCountdownLatch("Reduce", new CountDownLatch(permits));
    this.command = command;
  }
  
  public Callable<Integer> getRelatedCallable(final CommandController commandController) {
    synchronized(this) {
      final Integer _ackNumber = ackNumber;
      final String _commandName = commandName;
      final Integer _permits = permits;
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

  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
    return null;
  }

  public Callable<ICommandAction> nextAction() {
    final Integer _permitsRequired = permits;
    final CountDownLatch latch = getCommand().getCountdownLatch("Reduce");
    synchronized(latch) {
    
    return new Callable<ICommandAction>() {
      public ICommandAction call() {
        try {
          System.out.println(Thread.currentThread().getName() + " Is going to await a permit in AwaitingReduceCommand");
          latch.await();
          System.out.println(Thread.currentThread().getName() + " received a permit in AwaitingReduceCommand");
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        ICommandAction awaitingAckAction = new ReduceCommandAction();
        command.addPendingAction(awaitingAckAction);
        return null;
      }
    };
    }
  }
  
  public ICommandAction nextActionOld() {
    // TODO Auto-generated method stub
    synchronized(this) {
      try {
        getCommand().getCountdownLatch("Reduce").await();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return new ReduceCommandAction();
    }
  }

}
