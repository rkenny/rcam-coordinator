package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class AwaitingAckState extends ACommandState {
  
  public void doNetworkStuff(Observer observer, String server, ACommand actionSubject) {}
  public void nextState(String server, ACommand actionSubject) {}
  public void doRelatedCommandStuff(Observer actionObserver, String server, ACommand actionSubject) {  }
}
