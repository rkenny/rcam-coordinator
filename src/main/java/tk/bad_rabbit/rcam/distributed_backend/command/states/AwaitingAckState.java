package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.client.IClient;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class AwaitingAckState extends ACommandState {

  public void doAction(Observer observer, String server, ACommand actionSubject) {
    System.out.println("AwaitingAckState doAction called. Does this ever happen?");
    ((IClient) observer).sendAck((ACommand)actionSubject);
  }

}
