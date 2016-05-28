package tk.bad_rabbit.rcam.distributed_backend.client.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.IClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ErrorCommandState;

public class DisconnectedClientState extends AClientState {

  public void doCommandAction(ACommand command) {
    System.out.println("RCam Coordinator - DisconnectedClientState - will notify Command("+command.getCommandName()+"["+command.getAckNumber()+"])");
    //command.setState(new ErrorCommandState());
  }

}
