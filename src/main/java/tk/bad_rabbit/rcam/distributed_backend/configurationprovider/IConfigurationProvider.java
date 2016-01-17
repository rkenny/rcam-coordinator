package tk.bad_rabbit.rcam.distributed_backend.configurationprovider;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ACommandResponseAction;

public interface IConfigurationProvider {
  // This is the coordinator's configuration provider
  //String getHostname();

  //int getServerPort();

  //String getBaseUrl();

  Iterator<Entry<String, Integer>> getBackendMapIterator();
  
  public Map<String, JSONObject> getCommandConfigurations();
  public JSONObject getCommandConfiguration(String commandType);
  public String getCommandConfigurationPath();
  
  //public Map<String, JSONObject> getCommandVariables();
  public JSONObject getServerVariables();
  public Object getServerVariable(String variable);
  public void setServerVariable(String key, Object value);
  //public List<String> getBackendList();

  ACommandResponseAction getCommandResponseAction(String commandType);
}
