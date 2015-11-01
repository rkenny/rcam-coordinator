package tk.bad_rabbit.rcam.distributed_backend.command;

import java.util.Observable;
import java.util.Observer;

public abstract class ACommand extends Observable implements ICommand, Observer {
  
  public void update(Observable clientThread, Object arg) {
   // System.out.println("A client thread just notified a command of something. That something is " + arg.toString());
    
  }
  
  @Override
  public void notifyObservers(Object arg) {
    synchronized(this) {
      super.notifyObservers(arg);
    }
  }
}
