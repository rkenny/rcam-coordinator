package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;


public interface ICommandResponseAction {
  public void doNetworkAction(Observer actionObject, String server, ACommand actionSubject);
  public void doRelatedCommandAction(Observer actionObject, String server, ACommand actionSubject);
  abstract public void doNetworkStuff(Observer actionObject, String server, ACommand actionSubject);
  abstract public void doRelatedCommandStuff(Observer actionObject, String server, ACommand actionSubject);
  
}
