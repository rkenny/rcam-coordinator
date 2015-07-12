package tk.bad_rabbit.rcam.distributed_backend.configurationprovider;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.Pair;

public interface IConfigurationProvider {
  // This is the coordinator's configuration provider
  String getHostname();

  int getServerPort();

  String getBaseUrl();

  Iterator<Entry<String, Integer>> getBackendMapIterator();
  
  public Map<String, List<String>> getCommandConfigurations();
  public Map<String, Map<String, String>> getCommandVariables();
  public Map<String, String> getServerVariables();
  public List<String> getBackendList();

  ICommandResponseAction getCommandResponseAction(String commandType);
}
