package tk.bad_rabbit.rcam.distributed_backend.commandfactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.Command;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.spring.commandcoordinator.CommandCoordinator;
import tk.bad_rabbit.rcam.spring.commands.CommandController;

@Service(value="commandFactory")
public class CommandFactory implements ICommandFactory {

    Map<String, JSONObject> commandConfigurations;
    JSONObject serverVariables;
    
    Random rand;

    @Autowired
    @Qualifier(value="configurationProvider")
    IConfigurationProvider configurationProvider;
    
    @Autowired
    CommandCoordinator commandCoordinator;
    
    @Autowired
    CommandController commandController;
    
    public CommandFactory() {
    }
    
    @PostConstruct
    private void initializeCommandFactory() {
      this.commandConfigurations = configurationProvider.getCommandConfigurations();
      this.serverVariables = configurationProvider.getServerVariables();
      rand = new Random();
    }
    
    public CommandFactory(Map<String, JSONObject> commandConfigurations,  JSONObject serverVariables) {
      this.commandConfigurations = commandConfigurations;
      this.serverVariables = serverVariables;
    }
    
    public JSONObject createCommandConfiguration(String commandType) {
      StringBuilder commandArgs = new StringBuilder();
      File commandConfigFolder = new File("./config/commands/" + commandType);
      if(commandConfigFolder.isDirectory()) {
        File commandConfigFile = new File(commandConfigFolder, "command");
        
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
      } else if(configurationProvider.getCommandConfiguration(commandType) != null) {
        return configurationProvider.getCommandConfiguration(commandType);
      } else {
        commandArgs.append("{}");
      }

      return new JSONObject(commandArgs.toString());
    }
    
    //public ACommand createAckCommand(ACommand command) {
    //  JSONObject commandVariables = new JSONObject();
    //  commandVariables.put("command", command.getCommandName())
    //    .put("ackNumber", command.getAckNumber());
    //  return createCommand("Ack", commandVariables);
    //}
    
    //public ACommand createCancelCommand(ACommand commandToCancel) {
    //  JSONObject commandVariables = new JSONObject();
    // commandVariables.put("command",  commandToCancel.getCommandName())
    //    .put("ackNumber", commandToCancel.getAckNumber());
    //  
    //  return createCommand("Cancel", commandVariables);
    //  
    //}
    
    //public ACommand createReductionCompleteCommand(ACommand command) {
    //  JSONObject commandVariables = new JSONObject();
    //  commandVariables.put("command", command.getCommandName());
    //  commandVariables.put("ackNumber", command.getAckNumber());
      
    //  return createCommand("ReductionComplete", commandVariables);
    //}
    
   
    public String getCommandType(String commandString) {
      int commandTypeLength = commandString.indexOf("{") > 0 ? commandString.indexOf("{") : commandString.length();
      
      commandTypeLength = (commandString.indexOf("[") < commandTypeLength  
          && commandString.indexOf("[") > 0 ) ? commandString.indexOf("[") : commandTypeLength;
          
      return commandString.substring(0, commandTypeLength).trim();
    }
    
    
    public ACommand createCommand(CharBuffer commandBuffer) {
      String commandString = commandBuffer.toString();
      
      if(commandString.length() == 0) {
        return null;
      }
      
      JSONObject commandJSON = new JSONObject(commandString);
      JSONObject commandVariables = commandJSON.getJSONObject("details");
      String commandName = commandJSON.getString("commandName");
      
      Integer commandAckNumber;
      if(commandJSON.has("ackNumber")) {
        commandAckNumber = commandJSON.getInt("ackNumber");
      } else {
        commandAckNumber = 0;
      }
      
      //JSONObject clientVariables //= new JSONObject(commandString.substring(commandString.indexOf("{"), commandString.length()));
                  
      //return createCommand(getCommandType(commandString), commandAckNumber, clientVariables);
      return createCommand(commandName, commandAckNumber, commandVariables);
    }
 
    public ACommand createCommand(@Value("${commandType}") String commandType, JSONObject clientVariables) {
      return createCommand(commandType, new Random().nextInt((99999 - 10000) + 1) + 10000, clientVariables);
    }
    
    public ACommand createCommand(String commandType, Integer ackNumber, JSONObject details) {
      ACommand command = null;
      
      command = new Command(commandType, ackNumber, details);
      
      System.out.println("RCam Coordinator - CommandFactory - creating Command("+commandType+"["+ackNumber+"])");
      
      command.addObserver(commandCoordinator);
      commandController.addCommand(command);
      
      return command;
    }

 }