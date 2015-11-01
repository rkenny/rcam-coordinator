package tk.bad_rabbit.rcam.distributed_backend.client;

import tk.bad_rabbit.rcam.distributed_backend.client.states.AClientState;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public interface IClient {
  public void startClientThread();
  
  public void send(ACommand command);
  public void sendAck(ACommand actionSubject);
  
  public void setRemoteAddress(String server);
  public void setPort(int port);
  
  public AClientState setState(AClientState clientState);
  
  public String getServerString();
  
  public void observeCommand(ACommand command);
}
