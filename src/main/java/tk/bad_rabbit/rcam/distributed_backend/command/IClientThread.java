package tk.bad_rabbit.rcam.distributed_backend.command;


public interface IClientThread {
  public void send(ACommand command);
}
