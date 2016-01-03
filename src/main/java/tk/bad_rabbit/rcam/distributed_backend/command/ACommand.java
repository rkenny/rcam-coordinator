package tk.bad_rabbit.rcam.distributed_backend.command;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public abstract class ACommand extends Observable implements ICommand, Observer {
  
  public void update(Observable serverThread, Object arg) {}
  
  public void doNetworkAction(Observer actionObserver, String server) {
    this.getState(server).doNetworkAction(actionObserver, server, this);
  }
  public void doRelatedCommandAction(Observer actionObserver, String server) {
    this.getState(server).doRelatedCommandAction(actionObserver, server, this);
  }
  public void doRunCommandAction(Observer actionObserver, String server) {
    this.getState(server).doRunCommandAction(actionObserver, server, this);
  }
  
  public void addObservers(List<Observer> observers) {
    for(Observer observer : observers) {
      this.addObserver(observer);
    }
  }
  
  @Override
  public void notifyObservers(Object arg) {
    synchronized(this) {
      super.notifyObservers(arg);
    }
  }
}
