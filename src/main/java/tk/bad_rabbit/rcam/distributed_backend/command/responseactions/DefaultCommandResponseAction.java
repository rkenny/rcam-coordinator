package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class DefaultCommandResponseAction extends ACommandResponseAction {
  public void doNetworkStuff(Observer actionObject, String server, ACommand actionSubject) { }
  public void doRelatedCommandStuff(Observer actionObject, String server, ACommand actionSubject) {}
}
