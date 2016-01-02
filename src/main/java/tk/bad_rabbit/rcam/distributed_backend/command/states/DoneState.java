package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;


public class DoneState extends ACommandState {

  public void doNetworkStuff(Observer observer, String server, ACommand actionSubject) {}
  public void doRelatedCommandStuff(Observer observer, String server, ACommand actionSubject) {}
  public void nextState(String server, ACommand actionSubject) {}

}
