package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;



public class AckedState implements ICommandState {

  public void doAction(Observer observer, ACommand actionSubject) {
    //System.out.println("Command acked. Wait for the response now.");
  }

}
