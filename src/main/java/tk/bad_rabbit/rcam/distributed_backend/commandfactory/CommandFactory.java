package tk.bad_rabbit.rcam.distributed_backend.commandfactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.List;
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

@Service(value="commandFactory")
public class CommandFactory implements ICommandFactory {

    Map<String, JSONObject> commandConfigurations;
    //Map<String, JSONObject> commandVariables;
    JSONObject serverVariables;
    
    Random rand;

    @Autowired
    @Qualifier(value="configurationProvider")
    IConfigurationProvider configurationProvider;
    
    public CommandFactory() {
    }
    
    @PostConstruct
    private void initializeCommandFactory() {
      this.commandConfigurations = configurationProvider.getCommandConfigurations();
      //this.commandVariables = configurationProvider.getCommandVariables();
      this.serverVariables = configurationProvider.getServerVariables();
      rand = new Random();
    }
    
    public CommandFactory(Map<String, JSONObject> commandConfigurations,  JSONObject serverVariables) {
      this.commandConfigurations = commandConfigurations;
      //this.commandVariables = commandVariables;
      this.serverVariables = serverVariables;
    }
    
    public JSONObject createCommandConfiguration(String commandType) {
      StringBuilder commandArgs = new StringBuilder();
      // System.out.println("Got to createCommandConfiguration");
      File commandConfigFolder = new File("./config/commands/" + commandType);
      // System.out.println("Looking for a file in ./config/commands/"+ commandType);
      if(commandConfigFolder.isDirectory()) {
        File commandConfigFile = new File(commandConfigFolder, "command");
        // System.out.println("Found the command file for " + commandType);
        
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
      // System.out.println("commandArgs is " + commandArgs.toString());
      return new JSONObject(commandArgs.toString());
    }
    
    public ACommand createAckCommand(ACommand command) {
      JSONObject commandVariables = new JSONObject();
      commandVariables.put("command", command.getCommandName())
        .put("ackNumber", command.getAckNumber());
      //return createCommand("Ack(command=" + command.getCommandName() + ",ackNumber="+command.getAckNumber()+")");
      return createCommand("Ack", commandVariables);
    }
    
    public ACommand createCommand(CharBuffer commandBuffer) {
      String commandType;
      int commandTypeLength;
      String commandString = commandBuffer.toString();
      
      if(commandString.length() == 0) {
        System.out.println("commandFactory.createCommand(): commandString is 0. Returning null.");
        return null;
      }
      
      System.out.println("CommandFactory: Creating " + commandString + " from a charBuffer");
      commandTypeLength = commandString.indexOf("{") > 0 ? commandString.indexOf("{") : commandString.length();
      
      commandTypeLength = (commandString.indexOf("[") < commandTypeLength  
          && commandString.indexOf("[") > 0 ) ? commandString.indexOf("[") : commandTypeLength;
      commandType = commandString.substring(0, commandTypeLength).trim();
        
      Integer commandAckNumber;

      commandAckNumber = Integer.parseInt(commandString.substring(commandString.indexOf("[")+1, commandString.indexOf("]")));

      JSONObject clientVariables = new JSONObject(commandString.substring(commandString.indexOf("{"), commandString.length()));
      return createCommand(commandType, commandAckNumber, clientVariables);
    }
 
    public ACommand createCommand(@Value("${commandType}") String commandType, JSONObject clientVariables) {
      System.out.println("Going to create a command " + commandType);
      return createCommand(commandType, new Random().nextInt((99999 - 10000) + 1) + 10000, clientVariables);
    }
    
    public ACommand createCommand(String commandType, Integer ackNumber, JSONObject clientVariables) {
      ACommand command = null;
      
      command = new Command(commandType, ackNumber, 
                  createCommandConfiguration(commandType), clientVariables, serverVariables, 
                  configurationProvider.getCommandResponseAction(commandType)
                );

      
      return command;
    }

 }