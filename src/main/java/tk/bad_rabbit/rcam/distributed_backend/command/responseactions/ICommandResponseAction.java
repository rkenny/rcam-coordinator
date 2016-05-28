package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.commands.CommandController;


public interface ICommandResponseAction {
  //public void doNetworkAction(Observer actionObject, String server, ACommand actionSubject);
  //public void doRelatedCommandAction(Observer actionObject, String server, ACommand actionSubject);
  //abstract public void doStuff(Observer actionObject, String server, ACommand actionSubject);
  //abstract public void doNetworkStuff(Observer actionObject, String server, ACommand actionSubject);
  //abstract public void doRelatedCommandStuff(Observer actionObject, String server, ACommand actionSubject);
  
  
  public Future<Integer> doRelatedAction(CommandController actionObject, String server, ACommand actionSubject);
  
  //public void nextState(String server, ACommand command);
  
}
