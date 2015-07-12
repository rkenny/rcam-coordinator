package tk.bad_rabbit.rcam.distributed_backend.command.states;

import tk.bad_rabbit.rcam.distributed_backend.client.IClient;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class AwaitingAckState implements ICommandState {

  public void doAction(Object actionObject, Object actionSubject) {
    ((IClient) actionObject).sendAck((ACommand)actionSubject);
  }

}
