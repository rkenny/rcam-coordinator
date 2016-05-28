package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observable;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

public abstract class ACommandResponseAction extends Observable implements ICommandResponseAction, IResponseAction {
  
  abstract public Future<Integer> doRelatedAction(CommandController actionObject, String server, ACommand actionSubject);
  //public void nextState(String server, ACommand command) {}
}
