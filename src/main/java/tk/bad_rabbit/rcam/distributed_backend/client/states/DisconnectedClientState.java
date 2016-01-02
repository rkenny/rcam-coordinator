package tk.bad_rabbit.rcam.distributed_backend.client.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.IClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ErrorCommandState;

public class DisconnectedClientState extends AClientState {

  public void doAction(Observer observingCommand, IClientThread actionClientThread) {
   // ((ACommand) observingCommand).setErrorState();
  }

}
