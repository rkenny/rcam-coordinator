package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class SendReductionCompleteNetworkAction extends ACommandResponseAction {
  @Override
  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
    ((ServerThread) actionObject).sendReductionComplete(actionSubject);
  }

  //@Override
  //public void doRelatedCommandStuff(Observer actionObject, String server, ACommand actionSubject) {
  //}
  
  public void nextState(String server, ACommand command) {}
}
