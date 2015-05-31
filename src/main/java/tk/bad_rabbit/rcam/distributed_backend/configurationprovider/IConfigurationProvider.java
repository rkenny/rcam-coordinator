package tk.bad_rabbit.rcam.distributed_backend.configurationprovider;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public interface IConfigurationProvider {
  // This is the coordinator's configuration provider
  String getHostname();

  int getServerPort();

  String getBaseUrl();

  Iterator<Entry<String, Integer>> getClientMapIterator();
  

  public Map<String, List<String>> getCommandConfigurations();
  public Map<String, Map<String, String>> getCommandVariables();
  public Map<String, String> getServerVariables();

}
