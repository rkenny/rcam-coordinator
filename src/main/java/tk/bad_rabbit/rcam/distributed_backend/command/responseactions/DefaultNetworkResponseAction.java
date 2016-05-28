package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class DefaultNetworkResponseAction { //extends ANetworkResponseAction {
  public Future<Integer> doNetworkAction(ServerThread serverThread, String server, ACommand actionSubject) {
    System.out.println("Nothing to do for this network action");
    return null;
  }
}
