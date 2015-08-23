package tk.bad_rabbit.rcam.distributed_backend.commandfactory;

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
      return createCommand(commandType, new Random().nextInt((99999 - 10000) + 1) + 10000, clientVariables);
    }
    
    public ACommand createCommand(String commandType, Integer ackNumber, JSONObject clientVariables) {
      ACommand command = null;
      
      if(commandConfigurations.containsKey(commandType)) {
        command = new Command(commandType, ackNumber, commandConfigurations.get(commandType), clientVariables,
            serverVariables, configurationProvider.getCommandResponseAction(commandType));
      //  command = new Command(commandType, ackNumber, commandConfigurations.get(commandType), createClientVariablesMap(commandString),
      //      commandVariables.get(commandType), serverVariables, configurationProvider.getCommandResponseAction(commandType));
      
      } 
      
      return command;
    }

 }