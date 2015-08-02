package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public abstract class ACommandState implements ICommandState {

  //public void doAction(Observer actionObserver, String server, ACommand actionSubject) {
    // TODO Auto-generated method stub

  //}

  public Boolean typeEquals(ICommandState comparisonState) {
    System.out.println("Comparing " + this.getClass().getSimpleName() + " to " + comparisonState.getClass().getSimpleName());
    return (this.getClass().getSimpleName().equals(comparisonState.getClass().getSimpleName()));
  }

}
