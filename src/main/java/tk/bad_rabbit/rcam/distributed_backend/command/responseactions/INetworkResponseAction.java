package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.concurrent.Callable;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;

public interface INetworkResponseAction {
  public Callable<Integer> getNetworkCallable(ServerThread serverThread);
}
