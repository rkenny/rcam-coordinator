package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReadyToReduceState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.DoneState;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public class ResultCommandResponseAction extends ACommandResponseAction {

  @Override
  public void doStuff(Observer actionObject, String server, ACommand actionSubject) {
    ((CommandController) actionObject).getCommand((Integer) actionSubject.getClientVariable("ackNumber")).setState(server, new CommandReadyToReduceState());
    nextState(server, actionSubject);
  }

  
  
  public void nextState(String server, ACommand command) {
    command.nextState(server);
  }

  
}
