package tk.bad_rabbit.rcam.distributed_backend.client.states;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.IClientThread;

public interface IClientState {
  public void doAction(Observer actionObserver, IClientThread actionClientThread);
}
