package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;


public class ReceivedCommandState extends ACommandState {

//  public void doAction(Observer observer, String client, ACommand actionSubject) {
//    synchronized(actionSubject) {
//      ((ACommand) actionSubject).performCommandResponseAction(client, observer);
//    }
  //}
  
  public void doNetworkStuff(Observer observer, String server, ACommand actionSubject) {
    synchronized(actionSubject) {
      actionSubject.performCommandResponseNetworkAction(server, observer);
    }   
  }
  
  public void doRelatedCommandStuff(Observer actionObserver, String server, ACommand actionSubject) {
    actionSubject.performCommandResponseRelatedAction(server, actionObserver);
  }
  
  public void nextState(String server, ACommand actionSubject) {}
}
