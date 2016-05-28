package tk.bad_rabbit.rcam.distributed_backend.command.action;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.INetworkResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.IRunResponseAction;

public abstract class ARunNetworkResponseAction extends AResponseAction implements INetworkResponseAction, IRunResponseAction {
  
  private String client;
  public String getClient() {
    return client;
  }
}
