package tk.bad_rabbit.rcam.distributed_backend.command.states;

import tk.bad_rabbit.rcam.distributed_backend.client.IClient;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.IClientThread;

public class ReadyToSendState implements ICommandState {
  
  public void doAction(Object actionObject, Object actionSubject) {
    if(actionObject instanceof IClientThread ) {
      ((IClientThread) actionObject).send((ACommand) actionSubject);
    }
  }

}
