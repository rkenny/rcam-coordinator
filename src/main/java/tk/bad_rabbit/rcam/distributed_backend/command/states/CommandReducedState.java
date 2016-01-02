package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class CommandReducedState extends ACommandState {

  //public void doAction(Observer observer, String server, ACommand actionSubject) {
  //  if(observer instanceof ClientThread) {
  //    ((ClientThread) observer).removeCommand((ACommand) actionSubject);
  //  }
  //}
  
  public void doNetworkStuff(Observer observer, String server, ACommand actionSubject) {}
  public void nextState(String server, ACommand actionSubject) {}
  public void doRelatedCommandStuff(Observer actionObserver, String server, ACommand actionSubject) {  }

}
