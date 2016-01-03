package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.AckedState;

public class DefaultCommandResponseAction extends ACommandResponseAction {
  @Override
  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {}

  //@Override
  //public void doRelatedCommandStuff(Observer actionObject, String server, ACommand actionSubject) {
  //}
  
  public void nextState(String server, ACommand command) {}
}
