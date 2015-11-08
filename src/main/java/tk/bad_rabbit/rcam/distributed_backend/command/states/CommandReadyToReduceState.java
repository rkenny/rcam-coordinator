package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class CommandReadyToReduceState extends ACommandState {

  public void doAction(Observer actionObserver, String server,ACommand actionSubject) { }
}
