package tk.bad_rabbit.rcam.distributed_backend.commandfactory;

import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import tk.bad_rabbit.rcam.distributed_backend.command.Command;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

@Service(value="commandFactory")
public class CommandFactory implements ICommandFactory {

    Map<String, List<String>> commandConfigurations;
    Map<String, Map<String, String>> commandVariables;
    Map<String, String> serverVariables;
    
    Random rand;

    @Autowired
    @Qualifier(value="configurationProvider")
    IConfigurationProvider configurationProvider;
    
    public CommandFactory() {
    }
    
    @PostConstruct
    private void initializeCommandFactory() {
      this.commandConfigurations = configurationProvider.getCommandConfigurations();
      this.commandVariables = configurationProvider.getCommandVariables();
      this.serverVariables = configurationProvider.getServerVariables();
      rand = new Random();
    }
    
    public CommandFactory(Map<String, List<String>> commandConfigurations, Map<String,
        Map<String, String>> commandVariables, Map<String, String> serverVariables) {
      this.commandConfigurations = commandConfigurations;
      this.commandVariables = commandVariables;
      this.serverVariables = serverVariables;
    }
        
    
    public ICommand createAckCommand(ICommand command) {
      return createCommand("Ack(command=" + command.getCommandName() + ",ackNumber="+command.getAckNumber()+")");
    }
    
    public ICommand createCommand(CharBuffer commandBuffer) {
      return createCommand(commandBuffer.toString());
    }
    
    @Bean(name="Command")
    public ICommand createCommand(@Value("${commandString}") String commandString) {
      ICommand command = null;
      String commandType;
      int commandTypeLength;
      
      System.out.println("Creating command " + commandString);
      
      commandTypeLength = commandString.indexOf("(") > 0 ? commandString.indexOf("(") : commandString.length();
      commandTypeLength = (commandString.indexOf("[") < commandTypeLength  
          && commandString.indexOf("[") > 0 )? commandString.indexOf("[") : commandTypeLength;
      commandType = commandString.substring(0, commandTypeLength).trim();
      
      Integer commandAckNumber;
      if(commandString.indexOf("[") > 0 && commandString.indexOf("[") < commandString.indexOf("(") ) {
        commandAckNumber = Integer.parseInt(commandString.substring(commandString.indexOf("[")+1, commandString.indexOf("]")));
      } else {
        commandAckNumber = new Random().nextInt((99999 - 10000) + 1) + 10000;
      }
      //Something => Something[12345] => Ack(Something[12345])
      //Record(duration=200) => Record[123456](duration=200) => Ack(Record[123456])
      
      if(commandConfigurations.containsKey(commandType)) {
        command = new Command(commandType, commandAckNumber, commandConfigurations.get(commandType), createClientVariablesMap(commandString),
            commandVariables.get(commandType), serverVariables);
      } 
        // won't hit this yet
      //  System.out.println("Won't instantiate that command [" +commandString+ "]");

      return command;
    }

    private Map<String, String> createClientVariablesMap(String commandString) {
      Map<String, String> clientVariables = new HashMap<String, String>();
      int variablesSubstringStart = commandString.indexOf("(");
      int variablesSubstringEnd = commandString.indexOf(")");

      if(variablesSubstringStart > 0 && variablesSubstringEnd > 0 && variablesSubstringEnd <= commandString.length()) {
        String[] clientVariableArray;
        if(commandString.indexOf(",") > 0) {
          clientVariableArray = commandString.substring(variablesSubstringStart+1, variablesSubstringEnd).split(",");
        } else {
          clientVariableArray = new String[1];
          clientVariableArray[0] = commandString.substring(variablesSubstringStart+1, variablesSubstringEnd);
        }
        
        for(String clientVariable : clientVariableArray) {
          if(clientVariable.indexOf("=") > 0) {
            String[] variableAndValue = clientVariable.split("=");
            clientVariables.put(variableAndValue[0], variableAndValue[1]);
          }
        }
      }
      
      return clientVariables;
    }   
 }