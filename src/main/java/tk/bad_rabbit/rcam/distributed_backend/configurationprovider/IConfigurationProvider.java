package tk.bad_rabbit.rcam.distributed_backend.configurationprovider;

import java.util.Iterator;
import java.util.Map.Entry;

public interface IConfigurationProvider {

  String getHostname();

  int getHostPort();

  String getBaseUrl();

  Iterator<Entry<String, Integer>> getClientMapIterator();

}
