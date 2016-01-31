package tk.bad_rabbit.rcam.coordinator.client;

import java.nio.channels.SocketChannel;
import java.util.Observable;

import tk.bad_rabbit.rcam.distributed_backend.client.states.AClientState;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class Client extends Observable {

  SocketChannel socketChannel;
  String address;
  AClientState clientState;
  
  
  public Client(String address, SocketChannel socketChannel) {
    this.address = address;
    this.socketChannel = socketChannel;
  }
  
  public String getAddress() {
    return this.address;
  }
  
  public SocketChannel getSocketChannel() {
    return this.socketChannel;
  }
  
  public void doCommandAction(ACommand command) {
    clientState.doCommandAction(command);
  }
  
  public void setState(AClientState newState) {
    this.clientState = newState;
    setChanged();
    notifyObservers(newState);
  }
  
}
