package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.IClientThread;

public class ReadyToSendState extends ACommandState {
  
  public void doAction(Observer observer, String server, ACommand actionSubject) {
    System.out.println("Made it into the doAction of readyToSendState");
    if(observer instanceof IClientThread ) {
      ((IClientThread) observer).send((ACommand) actionSubject);
    }
  }

}
