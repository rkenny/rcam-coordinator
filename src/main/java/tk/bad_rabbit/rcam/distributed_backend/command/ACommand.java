package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;
import java.util.Observable;

import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandState;

public abstract class ACommand extends Observable implements ICommand {
  @Override
  public void notifyObservers(Object arg) {
    synchronized(this) {
      super.notifyObservers(arg);
    }
  }
}
