package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.nio.CharBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.INetworkResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class ReceivedCommandAction implements ICommandAction, IRelatedCommandAction {
  private String client;
  private String commandName;
  private ACommand relatedCommand;
  private Integer ackNumber;
  
  public ReceivedCommandAction() {
    super();
  }

  
  
  public void setCommandDetails(ACommand command) {
    this.commandName = command.getCommandName();
    this.ackNumber = command.getClientVariables().getInt("ackNumber");
  }


  public void takeNecessaryInfo(IConfigurationProvider configurationProvider) {
    
  }

  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
    return null;
  }

  public Callable<ICommandAction> nextAction() {
    synchronized(relatedCommand) { 
      final ACommand _related = relatedCommand;
      return new Callable<ICommandAction>() {
        public ICommandAction call() {
          synchronized(relatedCommand) {
            if(commandName.toLowerCase().equals("ack")) {
              relatedCommand.addPendingAction(new AckedCommandAction());  
            }
            if(commandName.toLowerCase().equals("readytoreduce")) {
              relatedCommand.addPendingAction(new ReadyToReduceCommandAction());
            }
            return null;
            }
        }
      };
    }
  }
  
  public Callable<Integer> getRelatedCallable(final CommandController commandController) {
    synchronized(commandController) {
      final Integer relatedAckNumber = ackNumber;
      return new Callable<Integer>() {
        public Integer call() {
          // this is the first goal for tomorrow. Get rid of this if statement.
          relatedCommand = commandController.getCommand(relatedAckNumber);
          
          
          return 0;
        }
      };
    }
  }

}
