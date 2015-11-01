package tk.bad_rabbit.rcam.distributed_backend.client.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.IClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ErrorCommandState;

public class DisconnectedClientState extends AClientState {

  public void doAction(Observer observingCommand, IClientThread actionClientThread) {
    System.out.println("Entered the DisconnectedClientState");
    
    System.out.println("Trying to change the state of " + ((ACommand) observingCommand).getAckNumber());
    //((ACommand) observingCommand).setState(actionClientThread.getServerString(), new ErrorCommandState());
    ((ACommand) observingCommand).setErrorState();
    System.out.println("Succeeded in setting the state of " + ((ACommand) observingCommand).getAckNumber());
    
  }

}
