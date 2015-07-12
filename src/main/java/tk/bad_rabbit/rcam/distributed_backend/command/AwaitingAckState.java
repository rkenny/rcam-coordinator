package tk.bad_rabbit.rcam.distributed_backend.command;

import tk.bad_rabbit.rcam.distributed_backend.client.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.client.IClient;

public class AwaitingAckState implements StateObject {

  public void doAction(Object actionObject, Object actionSubject) {
    ((IClient) actionObject).sendAck((ACommand)actionSubject);
  }

}
