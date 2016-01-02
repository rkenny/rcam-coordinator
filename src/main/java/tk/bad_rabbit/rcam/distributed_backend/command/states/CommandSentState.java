package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class CommandSentState extends ACommandState {

  
  public void doNetworkStuff(Observer actionObserver, String client, ACommand actionSubject) {
    //if(actionObserver instanceof RunController) {
    //  ((RunController) actionObserver).setCommandState(client, ((ACommand) actionSubject).getAckNumber(), new AwaitingAckState());
    //}
  }

  public void nextState(String server, ACommand actionSubject) {
    actionSubject.setState(server, new AwaitingAckState());
  }
  
  
  public void doRelatedCommandStuff(Observer actionObserver, String server, ACommand actionSubject) {  }
  //public Boolean typeEquals(ICommandState comparisonState) {
  //  // TODO Auto-generated method stub
  //  return null;
  // }

}
