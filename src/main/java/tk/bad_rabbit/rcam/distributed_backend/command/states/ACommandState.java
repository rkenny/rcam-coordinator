package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public abstract class ACommandState implements ICommandState {

  @Override
  public boolean equals(Object comparisonState) {
    if(comparisonState instanceof ICommandState) {
      System.out.println("ACommmandState.equals(): Comparing " + getClass().getSimpleName() + " to " + ((ACommandState) comparisonState).getClass().getSimpleName());
      return (getClass().getSimpleName().equals(comparisonState.getClass().getSimpleName()));  
    }
    System.out.println("ACommmandState.equals(): Returning false");
    return false;
  }  
  
  public Boolean typeEquals(ICommandState comparisonState) {
    return this.equals(comparisonState);
  }

}
