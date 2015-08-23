package tk.bad_rabbit.rcam.distributed_backend.configurationprovider;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;

public interface IConfigurationProvider {
  // This is the coordinator's configuration provider
  String getHostname();

  int getServerPort();

  String getBaseUrl();

  Iterator<Entry<String, Integer>> getBackendMapIterator();
  
  public Map<String, JSONObject> getCommandConfigurations();
  //public Map<String, JSONObject> getCommandVariables();
  public JSONObject getServerVariables();
  public List<String> getBackendList();

  ICommandResponseAction getCommandResponseAction(String commandType);
}
