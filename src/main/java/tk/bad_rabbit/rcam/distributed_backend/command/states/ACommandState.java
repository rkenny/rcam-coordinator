package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.DefaultCommandResponseAction;

public abstract class ACommandState implements ICommandState {
  public ACommandState() {
    setNetworkResponseAction(new DefaultCommandResponseAction());
    setRelatedCommandResponseAction(new DefaultCommandResponseAction());
  }
  
  
  public void doNetworkAction(Observer actionObserver, String server, ACommand actionSubject) {
    System.out.println("RCam Coordinator - " + getClass().getSimpleName() + " - Telling "+actionObserver.getClass().getSimpleName()  +" to doStuff to " + actionSubject.getAckNumber());
    getNetworkResponseAction().doStuff(actionObserver, server, actionSubject);
    
    //this.nextState(server, actionSubject);
  //this.doNetworkStuff(actionObserver, server, actionSubject);
    
  }
  
  public void doRelatedCommandAction(Observer actionObserver, String server, ACommand actionSubject) {
    System.out.println("RCam Coordinator - " + getClass().getSimpleName() + " - Telling "+ actionObserver.getClass().getSimpleName() + " to doStuff to related commands");
    getRelatedCommandResponseAction().doStuff(actionObserver, server, actionSubject);
    //this.doRelatedCommandStuff(actionObserver, server, actionSubject);
    //actionSubject.performCommandResponseRelatedAction(server, actionObserver);
    //this.nextState(server, actionSubject);
  }
  
  @Override
  public boolean equals(Object comparisonState) {
    if(comparisonState instanceof ICommandState) {
      return (getClass().getSimpleName().equals(comparisonState.getClass().getSimpleName()));  
    }
    return false;
  }  
  
  public Boolean typeEquals(ICommandState comparisonState) {
    return this.equals(comparisonState);
  }

}
