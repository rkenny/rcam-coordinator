package tk.bad_rabbit.rcam.distributed_backend.client;

import java.util.Map;
import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public interface IClient {
  public void startClientThread();
  
  public void send(ACommand command);
  public void sendAck(ACommand actionSubject);
  
  public void setRemoteAddress(String server);
  public void setPort(int port);
  
  public void observeCommand(ACommand command);
}
