package tk.bad_rabbit.rcam.coordinator.client;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Clients implements Iterable<String> {
  
  Map<String, Client> clientMap;
  
  public Clients() {
    this.clientMap = new HashMap<String, Client>();
  }
  
  public Set<String> getConnectedServers() {
    return clientMap.keySet();
  }

  public Iterator<String> iterator() { 
    Iterator<String> i = clientMap.keySet().iterator();
    return i;
  }
  
  public void add(String address, SocketChannel socketChannel) {
    this.clientMap.put(address, new Client(address, socketChannel));
  }
  
  public void add(Client newClient) {
    this.clientMap.put(newClient.getAddress(), newClient);
  }
  
  
  public Client get(String address) {
    return this.clientMap.get(address);
  }
  
  public void remove(String address) {
    this.clientMap.remove(address);
  }
  
}
