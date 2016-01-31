package tk.bad_rabbit.rcam.distributed_backend.client.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.IClientThread;

public class DefaultClientState extends AClientState {

  public void doCommandAction(ACommand command) {
    System.out.println("RCam Coordinator - DefaultClientState - will do something to Command("+command.getCommandName() + "["+command.getAckNumber()+"])");
  }

}
