package tk.bad_rabbit.rcam.distributed_backend.configurationprovider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import tk.bad_rabbit.rcam.distributed_backend.command.action.ActionHandler;
import tk.bad_rabbit.rcam.distributed_backend.command.action.ICommandAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.AckCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.DefaultCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.IResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ReductionCompleteCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ResultCommandResponseAction;

@Component(value="configurationProvider")
public class ConfigurationProvider implements IConfigurationProvider {

  Map<String, Integer> backendInfo;

  Map<String, JSONObject> commandConfigurations;

  JSONObject serverVariables;
  Map<String, IResponseAction> commandResponseActions;
  
  
  public ConfigurationProvider() {
    readClientsConfiguration();
    
    serverVariables = new JSONObject();
    commandResponseActions = new HashMap<String, IResponseAction>();
    readServerConfiguration();
    readCommandConfigurations();
    
    JSONObject ackConfiguration = new JSONObject();
    JSONArray ackClientVars = new JSONArray();
    ackClientVars.put("ackNumber");
    ackClientVars.put("command");
    ackConfiguration.put("clientVars", ackClientVars);
    ackConfiguration.put("commandVars", new JSONObject("{ignored: true}"));
    addSystemCommand("Ack", ackConfiguration, new AckCommandResponseAction());
    
    
    JSONObject commandResultConfiguration = new JSONObject();
    JSONArray resultClientVars = new JSONArray();
    resultClientVars.put("ackNumber");
    resultClientVars.put("resultCode");
    commandResultConfiguration.put("clientVars", resultClientVars);
    commandResultConfiguration.put("commandVars", new JSONObject("{ignored: false}"));
    addSystemCommand("CommandResult", commandResultConfiguration, new ResultCommandResponseAction());
    
    JSONObject cancelConfiguration = new JSONObject();
    JSONArray cancelClientVars = new JSONArray();
    cancelClientVars.put("command");
    cancelClientVars.put("ackNumber");
    cancelConfiguration.put("clientVars", cancelClientVars);
    cancelConfiguration.put("commandVars", new JSONObject("{ignored: true}"));
    addSystemCommand("Cancel", cancelConfiguration, new DefaultCommandResponseAction());
    
    JSONObject reductionCompleteCommand = new JSONObject();
    JSONArray reductionCompleteVars = new JSONArray();
    reductionCompleteVars.put("ackNumber");
    reductionCompleteVars.put("command");
    reductionCompleteCommand.put("clientVars", reductionCompleteVars);
    reductionCompleteCommand.put("commandVars", new JSONObject("{ignored: true}"));
    // addSystemCommand("ReductionComplete", reductionCompleteCommand, new ReductionCompleteCommandResponseAction());
  }
  
  private void addSystemCommand(String commandType, JSONObject commandConfiguration, IResponseAction commandResponseAction) {
    commandConfigurations.put(commandType, commandConfiguration);
    commandResponseActions.put(commandType, commandResponseAction); // This isn't used anymore?
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
    File serverConfigFile = new File("config/server.conf");
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(serverConfigFile));
      String configFileLine;
      StringBuilder serverConfig = new StringBuilder();
      while((configFileLine = reader.readLine()) != null) {
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
        
        commandResponseActions.put(commandConfigDirectory.getName(), new DefaultCommandResponseAction());
      }
    }
  }
  
  
  public Iterator<Map.Entry<String, Integer>> getBackendMapIterator() {
    return backendInfo.entrySet().iterator();
  }
  
  public JSONObject getServerVariables() {
    return serverVariables;
  }
  
  public Object getServerVariable(String variable) {
    return serverVariables.get(variable);
  }
  
  public void setServerVariable(String key, Object value) {
    this.serverVariables.put(key, value);
  }

  public Map<String, JSONObject> getCommandConfigurations() {
    return commandConfigurations;
  }
  
  public String getCommandConfigurationPath() {
    return "config/commands";
  }
  
  //public JSONObject getCommandConfiguration(String commandType) {
  //  return this.commandConfigurations.get(commandType);
  //}

  public JSONObject getCommandConfiguration(String commandName) {
    JSONObject commandConfiguration;
    StringBuilder commandArgs = new StringBuilder();
    File commandConfigDirectory = new File("config/commands/"+commandName);
    if(commandConfigDirectory.isDirectory()) {
      File commandConfigFile = new File(commandConfigDirectory, "command");
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
      }   
    return new JSONObject(commandArgs.toString());
  }
  
  public IResponseAction getCommandResponseAction(String commandType) {
    return commandResponseActions.get(commandType);
  }
   
}
