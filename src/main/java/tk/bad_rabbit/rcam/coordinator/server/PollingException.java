package tk.bad_rabbit.rcam.coordinator.server;

public class PollingException extends Exception {
  private String remoteConnection;
  
  public PollingException(String remoteConnection) {
    this();
    this.remoteConnection = remoteConnection;
  }
  
  public PollingException() {}
  
  public String getRemoteConnection() { return this.remoteConnection; }
}
