package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class ReadyToSendState extends ACommandState {
  
  public void doRelatedCommandStuff(Observer actionObserver, String server, ACommand actionSubject) {  }
  
  public void doNetworkStuff(Observer observer, String client, ACommand actionSubject) {
    if(observer instanceof ServerThread ) {
      System.out.println("RCam Coordinator - ReadyToSendState - Telling ServerThread to send " + actionSubject.getAckNumber());
      ((ServerThread) observer).send(client, actionSubject);
    }
     
  }
  
  public void nextState (String server, ACommand actionSubject) {
    actionSubject.setState(server, new CommandSentState());
  }

}
