package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.IClientThread;

public class ReadyToSendState implements ICommandState {
  
  public void doAction(Observer observer, ACommand actionSubject) {
    if(observer instanceof IClientThread ) {
      ((IClientThread) observer).send((ACommand) actionSubject);
    }
  }

}
