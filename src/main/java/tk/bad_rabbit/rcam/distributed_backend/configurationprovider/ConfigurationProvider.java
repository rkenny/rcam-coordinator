package tk.bad_rabbit.rcam.distributed_backend.configurationprovider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component(value="configurationProvider")
public class ConfigurationProvider implements IConfigurationProvider {

  Map<String, Integer> clientInfo;

  Map<String, List<String>> commandConfigurations;
  Map<String, Map<String, String>> commandVariables;
  Map<String, String> serverVariables;

  
  
  public ConfigurationProvider() {
    //
    //clientInfo.put("localhost", new Integer(12345));
    readClientsConfiguration();
    
    serverVariables = new HashMap<String, String>();
    readServerConfiguration();
    readCommandConfigurations();
    
    addAckCommand();
  }
  
  private void addAckCommand() {
    List<String> ackCommand = new ArrayList<String>();
    ackCommand.add("&command[&ackNumber]");
    commandConfigurations.put("Ack", ackCommand);
    Map<String, String> ackCommandVariables = new HashMap<String, String>();
    ackCommandVariables.put("ignored", "true");
    commandVariables.put("Ack", ackCommandVariables);
  }
  
  private void readClientsConfiguration() {
    clientInfo = new HashMap<String, Integer>();
    File clientsConfigFile = new File("config/clients.conf");
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(clientsConfigFile));
      String configFileLine;
      while((configFileLine = reader.readLine()) != null) {
        parseClientConfigLine(configFileLine);
      }  
    } catch(FileNotFoundException e) {
      System.out.println("File not found. Going with defaults");
      clientInfo.put("127.0.0.1", 12345);
    } catch(IOException e) {
      System.out.println("Error setting server configuration. Going with the defaults.");
      clientInfo.put("127.0.0.1", 12345);
    }
  }
  
  
  
  private void readServerConfiguration() {
    //serverPort = 12345;
    File serverConfigFile = new File("config/server.conf");
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(serverConfigFile));
      String configFileLine;
      while((configFileLine = reader.readLine()) != null) {
        parseServerConfigLine(configFileLine);
      }
    } catch(FileNotFoundException e) {
      System.out.println("File not found. Going with defaults");
      serverVariables.put("port", "8080");
    } catch(IOException e) {
      System.out.println("Error setting server configuration. Going with the defaults.");
      serverVariables.put("port", "12345");
    }
    
  }
  
  private void parseClientConfigLine(String configFileLine) {
    String[] clientAndPort;
    if(configFileLine.indexOf(":") > 0) {
      clientAndPort = configFileLine.split(":");
      clientInfo.put(clientAndPort[0], Integer.parseInt(clientAndPort[1]));
    }
  }
  
  private void parseServerConfigLine(String configFileLine) {
    String[] variableAndValue;
    if(configFileLine.indexOf("=") > 0) {
      variableAndValue = configFileLine.split("=");
      serverVariables.put(variableAndValue[0], variableAndValue[1]);
    }  
  }
  
  private void readCommandConfigurations() {
    commandConfigurations = new HashMap<String, List<String>>();
    commandVariables = new HashMap<String, Map<String, String>>();
    File commandConfigFolder = new File("config/commands");
    for(File commandConfigDirectory : commandConfigFolder.listFiles()) {
      if(commandConfigDirectory.isDirectory()) {
        File commandConfigFile = new File(commandConfigDirectory, "command");
        
        List<String> commandArgs = new ArrayList<String>();
        if(commandConfigFile.isFile()) {
          BufferedReader reader;
          try {
            reader = new BufferedReader(new FileReader(commandConfigFile));
            String configFileLine;
            while((configFileLine = reader.readLine()) != null) {
              commandArgs.add(configFileLine);
            }
            reader.close();
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        
        File commandVariableFile = new File(commandConfigDirectory, "vars");
        Map<String, String> commandVars = new HashMap<String, String>();
        if(commandVariableFile.isFile()) {
          BufferedReader reader;
          try {
            reader = new BufferedReader(new FileReader(commandVariableFile));
            String configFileLine;
            while((configFileLine = reader.readLine()) != null) {
              if(configFileLine.indexOf("=") > 0) {
                String[] variableAndValue = configFileLine.split("=");
                commandVars.put(variableAndValue[0], variableAndValue[1]);
              }
            }
            reader.close();
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        
        commandConfigurations.put(commandConfigDirectory.getName(), commandArgs);
        commandVariables.put(commandConfigDirectory.getName(), commandVars);
      }
      
           
    }
  }
  
  public String getHostname() {
    return "localhost";
  }


  public String getBaseUrl() {
    return "/";
  }
  
  
  public Iterator<Map.Entry<String, Integer>> getClientMapIterator() {
    return clientInfo.entrySet().iterator();
  }
  
  public Map<String, String> getServerVariables() {
    return serverVariables;
  }
  
  public int getServerPort() {
    return Integer.parseInt(serverVariables.get("port"));
  }

  public Map<String, List<String>> getCommandConfigurations() {
    return commandConfigurations;
  }
  
  public Map<String, Map<String, String>> getCommandVariables() {
    return commandVariables;
  }

}
