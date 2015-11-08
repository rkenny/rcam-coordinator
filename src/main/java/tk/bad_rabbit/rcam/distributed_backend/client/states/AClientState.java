package tk.bad_rabbit.rcam.distributed_backend.client.states;


public abstract class AClientState implements IClientState {
  public boolean equals(Object state) {
    return this.getClass().getSimpleName().equals(state.getClass().getSimpleName());
  }
}
