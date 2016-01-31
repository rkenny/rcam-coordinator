package tk.bad_rabbit.rcam.distributed_backend.command;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import tk.bad_rabbit.rcam.coordinator.client.Client;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ACommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ReceivedCommandState;

public abstract class ACommand extends Observable implements ICommand, Observer {
  
  public void update(Observable serverThread, Object arg) {
    ((Client) arg).doCommandAction(this);
  }
  
  public void doNetworkAction(Observer actionObserver, String server) {
    this.getState(server).doNetworkAction(actionObserver, server, this);
  }
  
  public void doRelatedCommandAction(Observer actionObserver, String server) {
    this.getState(server).doRelatedCommandAction(actionObserver, server, this);
  }
    
  
  public void doRunCommandAction(Observer actionObserver, String server) {
    this.getState(server).doRunCommandAction(actionObserver, server, this);
  }
  
  public void nextState(String server) {
    this.setState(this.getState(server).getNextState());
  }
  

  
  public Boolean isNoLongerNew() {
    ACommandState state = new ReceivedCommandState();
    for(String server : this.getServers()) {
      if(this.getState(server).typeEquals(state)) {
        return false;
      }
    }
    return true;
  }
  
  abstract Set<String> getServers();
  
  
  
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
