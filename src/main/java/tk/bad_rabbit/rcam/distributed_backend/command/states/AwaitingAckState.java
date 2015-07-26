package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.client.IClient;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class AwaitingAckState implements ICommandState {

  public void doAction(Observer observer, ACommand actionSubject) {
    ((IClient) observer).sendAck((ACommand)actionSubject);
  }

}
