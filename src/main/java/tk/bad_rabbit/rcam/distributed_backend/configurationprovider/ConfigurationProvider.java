package tk.bad_rabbit.rcam.distributed_backend.configurationprovider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component(value="configurationProvider")
public class ConfigurationProvider implements IConfigurationProvider {

  Map<String, Integer> clientInfo;
  
  public ConfigurationProvider() {
    clientInfo = new HashMap<String, Integer>();
    clientInfo.put("localhost", new Integer(12345));
  }
  
  public String getHostname() {
    return "localhost";
  }

  public int getHostPort() {
    return 8080;
  }

  public String getBaseUrl() {
    return "/";
  }
  
  public String testThis() {
    return "if you see this, injection is working.";
  }

  public Iterator<Map.Entry<String, Integer>> getClientMapIterator() {
    return clientInfo.entrySet().iterator();
  }

}
