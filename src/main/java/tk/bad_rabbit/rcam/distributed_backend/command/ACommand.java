package tk.bad_rabbit.rcam.distributed_backend.command;

import java.util.Observable;

public abstract class ACommand extends Observable implements ICommand {
  @Override
  public void notifyObservers(Object arg) {
    synchronized(this) {
      super.notifyObservers(arg);
    }
  }
}
