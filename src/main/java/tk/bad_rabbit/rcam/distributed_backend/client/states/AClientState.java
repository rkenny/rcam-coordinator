package tk.bad_rabbit.rcam.distributed_backend.client.states;


public abstract class AClientState implements IClientState {
  public boolean equals(Object state) {
    System.out.println("AClientState: comparing " + this.getClass().getSimpleName() + " to " + state.getClass().getSimpleName());
    System.out.println("AClientState: are they equal?" + this.getClass().getSimpleName().equals(state.getClass().getSimpleName()));
    return this.getClass().getSimpleName().equals(state.getClass().getSimpleName());
  }
}
