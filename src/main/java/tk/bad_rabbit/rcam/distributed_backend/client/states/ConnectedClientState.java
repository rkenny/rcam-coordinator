package tk.bad_rabbit.rcam.distributed_backend.client.states;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class ConnectedClientState extends AClientState {

  public void doCommandAction( ACommand command) {
    System.out.println("RCam Coordinator - ConnectedClientState - will notify Command("+command.getCommandName()+"["+command.getAckNumber()+"])");
  }

}
