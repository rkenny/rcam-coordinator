package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.client.IClient;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.IClientThread;


public class ErrorCommandState  {// extends ACommandState {

  public void doAction(Observer observer, String server, ACommand actionSubject) {
    if(observer instanceof IClientThread) {
      ((IClientThread) observer).sendCancel((ACommand) actionSubject);
    }
  }

}
