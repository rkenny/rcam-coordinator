package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

public class AwaitingAckCommandAction extends AResponseAction implements ICommandAction {

  Integer permitsRequired;
  ACommand command;
  
  public AwaitingAckCommandAction(Integer permitsRequired) {
    this.permitsRequired = permitsRequired; 
  }
  
  @Override
  public void setCommandDetails(ACommand command) {
    synchronized(command) {
      super.setCommandDetails(command);   
      this.command = command;
      command.setCountdownLatch("Ack", new CountDownLatch(permitsRequired));
    }
  }


  public void takeNecessaryInfo(IConfigurationProvider configurationProvider) {
    
  }

  public Callable<ICommandAction> nextAction() {
    final Integer _permitsRequired = permitsRequired;
    final CountDownLatch latch = getCommand().getCountdownLatch("Ack");
    synchronized(latch) {
      return new Callable<ICommandAction>() {
        public ICommandAction call() {
          //ICommandAction awaitingAckAction = new AwaitingReduceCommandAction(permitsRequired);
          try {
            System.out.println(Thread.currentThread().getName() + " Is going to await a permit in AwaitingAckCommand");
            latch.await();
            System.out.println(Thread.currentThread().getName() + " received a permit in AwaitingAckCommand");
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          command.addPendingAction(new AwaitingReduceCommandAction(permitsRequired));
          return null;
        }
      };
    }
  }
  
  public void nextActionOld() {
    synchronized(this) {
      //try {
      //  System.out.println("AwaitingAckCommandAction will attempt to acquire " + permitsRequired + " permits");
        //getCommand().getSemaphore("Ack").
      //  System.out.println(Thread.currentThread().getName() + " Will block.");
        //getCommand().getCountdownLatch("Ack").await();
      //  System.out.println("AwaitingAckCommandAction has acquired " + permitsRequired + " permits");
      //} catch (InterruptedException e) {
        // TODO Auto-generated catch block
      //  e.printStackTrace();
      //} finally {
        //getCommand().getSemaphore("Ack").release(permitsRequired);
      //}
      
      command.addPendingAction(new AwaitingReduceCommandAction(permitsRequired));
    }
  }


}
