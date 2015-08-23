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

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.AckCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.DefaultCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ResultCommandResponseAction;

@Component(value="configurationProvider")
public class ConfigurationProvider implements IConfigurationProvider {

  Map<String, Integer> backendInfo;

  Map<String, JSONObject> commandConfigurations;
  //Map<String, JSONObject> commandVariables;
  JSONObject serverVariables;
  Map<String, ICommandResponseAction> commandResponseActions;
  
  
  public ConfigurationProvider() {
    //
    //clientInfo.put("localhost", new Integer(12345));
    readClientsConfiguration();
    
    serverVariables = new JSONObject();
    commandResponseActions = new HashMap<String, ICommandResponseAction>();
    readServerConfiguration();
    readCommandConfigurations();
    
    JSONObject ackConfiguration = new JSONObject();
    
    ackConfiguration.put("clientVars", new String[]{"ackNumber", "command"});
    ackConfiguration.put("commandVars", new JSONObject("{ignored: true}"));
    addSystemCommand("Ack", ackConfiguration, new AckCommandResponseAction());
    
    JSONObject commandResultConfiguration = new JSONObject();
    commandResultConfiguration.put("clientVars", new String[]{"ackNumber", "resultCode"});
    commandResultConfiguration.put("commandVars", new JSONObject("{ignored: false}"));
    addSystemCommand("CommandResult", commandResultConfiguration, new ResultCommandResponseAction());
  }
  
  private void addSystemCommand(String commandType, JSONObject commandConfiguration, ICommandResponseAction commandResponseAction) {
    commandConfigurations.put(commandType, commandConfiguration);
    commandResponseActions.put(commandType, commandResponseAction);
  }
     
  private void readClientsConfiguration() {
    backendInfo = new HashMap<String, Integer>();
    File clientsConfigFile = new File("config/backends.conf");
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(clientsConfigFile));
      String configFileLine;
      while((configFileLine = reader.readLine()) != null) {
        parseBackendConfigLine(configFileLine);
      }  
    } catch(FileNotFoundException e) {
      System.out.println("File not found. Going with defaults");
      backendInfo.put("127.0.0.1", 12345);
    } catch(IOException e) {
      System.out.println("Error setting server configuration. Going with the defaults.");
      backendInfo.put("127.0.0.1", 12345);
    }
  }
  
  
  
  private void readServerConfiguration() {
    //serverPort = 12345;
    File serverConfigFile = new File("config/server.conf");
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(serverConfigFile));
      String configFileLine;
      StringBuilder serverConfig = new StringBuilder();
      while((configFileLine = reader.readLine()) != null) {
        //parseServerConfigLine(configFileLine);
        serverConfig.append(configFileLine);
      }
      serverVariables = new JSONObject(serverConfig.toString());
    } catch(FileNotFoundException e) {
      System.out.println("File not found. Going with defaults");
      serverVariables.put("port", "8080");
    } catch(IOException e) {
      System.out.println("Error setting server configuration. Going with the defaults.");
      serverVariables.put("port", "8080");
    }
    
  }
  
  private void parseBackendConfigLine(String configFileLine) {
    String[] nameAndPort;
    if(configFileLine.indexOf(":") > 0) {
      nameAndPort = configFileLine.split(":");
      backendInfo.put(nameAndPort[0], Integer.parseInt(nameAndPort[1]));
      System.out.println("Added " + nameAndPort[0] + ":" + Integer.parseInt(nameAndPort[1]));
    }
    
  }
  
  private void readCommandConfigurations() {
    commandConfigurations = new HashMap<String, JSONObject>();
    File commandConfigFolder = new File("config/commands");
    for(File commandConfigDirectory : commandConfigFolder.listFiles()) {
      if(commandConfigDirectory.isDirectory()) {
        File commandConfigFile = new File(commandConfigDirectory, "command");
        
        StringBuilder commandArgs = new StringBuilder();
        if(commandConfigFile.isFile()) {
          BufferedReader reader;
          try {
            reader = new BufferedReader(new FileReader(commandConfigFile));
            String configFileLine;
            while((configFileLine = reader.readLine()) != null) {
              commandArgs.append(configFileLine);
            }
            reader.close();
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        commandConfigurations.put(commandConfigDirectory.getName(), new JSONObject(commandArgs.toString()));
        
        commandResponseActions.put(commandConfigDirectory.getName(), new DefaultCommandResponseAction());
      }
    }
  }
  
  public String getHostname() {
    return "localhost";
  }


  public String getBaseUrl() {
    return "/";
  }
  
  
  public Iterator<Map.Entry<String, Integer>> getBackendMapIterator() {
    return backendInfo.entrySet().iterator();
  }
  
  public JSONObject getServerVariables() {
    return serverVariables;
  }
  
  public int getServerPort() {
    return new Integer(serverVariables.getInt("port")); //Integer.parseInt(serverVariables.getInt("port"));
  }

  public Map<String, JSONObject> getCommandConfigurations() {
    return commandConfigurations;
  }

  public List<String> getBackendList() {
    List<String> backendList = new ArrayList<String>();
    for(String server : backendInfo.keySet()) {
      backendList.add(server + ":" + backendInfo.get(server));
    }
        
    return backendList;
  }

  public ICommandResponseAction getCommandResponseAction(String commandType) {
    return commandResponseActions.get(commandType);
  }

}
