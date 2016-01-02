package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class CommandReadyToReduceState extends ACommandState {
  public void doNetworkStuff(Observer observer, String server, ACommand actionSubject) {}
  public void nextState(String server, ACommand actionSubject) {}
  
  public void doRelatedCommandStuff(Observer actionObserver, String server, ACommand actionSubject) {
    System.out.println("RCam Coordinator - CommandReadyToReduceState - When all servers report CommandReadyToReduce, start reducing");
    
  }
}
