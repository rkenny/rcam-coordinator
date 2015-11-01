package tk.bad_rabbit.rcam.distributed_backend.command;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.app.Pair;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReadyToReduceState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ErrorCommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public class Command extends ACommand {
  private String commandName;
  private Integer commandAckNumber;
  private JSONObject clientVariables;
  private JSONObject commandConfiguration;
  private JSONObject serverVariables;
  private ICommandResponseAction commandResponseAction;
  
  private volatile Map<String, ICommandState> state;
  
  private Integer returnCode;
  
  public void setReturnCode(Integer returnCode) {
    this.returnCode = returnCode;
  }
  
  public Integer getReturnCode() {
    return this.returnCode;
  }
 
  public synchronized void update(Observable updatedClient, Object serverWithPort) {
    if(updatedClient instanceof IClientThread) {
      System.out.println("A command just received notification that a client's state changed " + serverWithPort);
      System.out.println(state.get(serverWithPort).getClass().getSimpleName().toString());
      ((IClientThread) updatedClient).doAction(this);      
    }

  }
  
  
  public void performCommandResponseAction(String server, Object actionObject) {
    this.commandResponseAction.doAction(actionObject, server, this);
  }
  
  public Command() {
    this.state = new HashMap<String, ICommandState>();
  }
  
  public Command(String commandName, Integer commandAckNumber, JSONObject commandConfiguration,
      JSONObject clientVariables, JSONObject serverVariables, ICommandResponseAction commandResponseAction) {
    this();
    this.commandName = commandName;
    this.commandConfiguration = commandConfiguration;
    this.commandAckNumber = commandAckNumber;
    this.clientVariables = clientVariables;
    this.serverVariables = serverVariables;
    this.commandResponseAction = commandResponseAction;
    
    System.out.println("Created command " + commandName + "[" + commandAckNumber + "]");
  }
  
  
  public Object getClientVariable(String variableName) {
    return this.clientVariables.get(variableName);
  }
  
  public Object getServerVariable(String variableName) {
    return this.serverVariables.get(variableName);
  }
  
  
  public synchronized ICommandState setState(String server, ICommandState state) {
    System.out.println("Trying to change the state for command " + getAckNumber() + " on " + server + " to " + state.getClass().getSimpleName());
    this.state.put(server,  state);
    
    Entry<String, ICommandState> serverState = new AbstractMap.SimpleEntry<String, ICommandState>(server, state);
    Entry<ACommand, Entry<String, ICommandState>> commandDetails = new AbstractMap.SimpleEntry<ACommand, Entry<String, ICommandState>>(this, serverState);
    
    setChanged();
    notifyObservers(commandDetails);
    
    return state;
  }
  
  public synchronized void setErrorState() {
    Set<Entry<String, ICommandState>> servers = this.state.entrySet();
    Iterator<Entry<String, ICommandState>> serversIterator = servers.iterator();
    while(serversIterator.hasNext()) {
      String server = serversIterator.next().getKey();
      System.out.println("Going to put " + server + " " + getAckNumber() + " into an error state");
      System.out.println(ErrorCommandState.class.getSimpleName());
      System.out.println(this.state.get(server).getClass().getSimpleName());
      
    }
  }
  
  public synchronized void doAction(Observer actionObject, String server) {
    System.out.println("Command.doAction()");
    System.out.println("About to doAction for server " + server + " on command " + getAckNumber() + " with state " + this.state.get(server).getClass().getSimpleName());
    state.get(server).doAction(actionObject, server, this);
  }
  

  public Boolean isType(String commandType) {
    return this.commandName.equals(commandType);
  }
  
  public Boolean isIgnored() {
    return(commandConfiguration.getJSONObject("commandVars").get("ignored") == "true");
  }
  
  public Boolean isReadyToReduce() {
    Set<Entry<String, ICommandState>> servers = this.state.entrySet();
    Iterator<Entry<String, ICommandState>> serversIterator = servers.iterator();
    CommandReadyToReduceState readyToReduceState = new CommandReadyToReduceState();
    Boolean isReadyToReduce = true;
    while(serversIterator.hasNext()) {
     ICommandState serverState = serversIterator.next().getValue();
     if(!serverState.typeEquals(readyToReduceState)) {
       isReadyToReduce = false;
     }
    }
    
    System.out.println("Is command ready to reduce? " + isReadyToReduce);
    return isReadyToReduce;
  }
  
  
  public String finalizeCommandString() {
    StringBuilder finalCommandString = new StringBuilder();
    // System.out.println("finalCommandString is " + finalCommandString + " before the replaces");
    finalCommandString.append("{");
    if(commandConfiguration.has("clientVars")) {
      for(int i = 0; i < commandConfiguration.getJSONArray("clientVars").length(); i++) {
        finalCommandString.append("\""+commandConfiguration.getJSONArray("clientVars").get(i).toString()+"\":");
        finalCommandString.append("\""+clientVariables.get(commandConfiguration.getJSONArray("clientVars").get(i).toString())+"\"");
        finalCommandString.append(",");
      }
    }

    Iterator<String> variableIterator = commandConfiguration.getJSONObject("commandVars").keys();
    while(variableIterator.hasNext()) {
      String key = variableIterator.next();
      finalCommandString.append("\"" + key + "\":\"" + commandConfiguration.getJSONObject("commandVars").get(key).toString()+"\"");
      finalCommandString.append(",");
    }
    
    if(commandConfiguration.has("serverVars")) {
      for(int i = 0; i < commandConfiguration.getJSONArray("serverVars").length(); i++) {
      finalCommandString.append("\""+commandConfiguration.getJSONArray("serverVars").get(i).toString()+"\":");
      finalCommandString.append("\""+clientVariables.get(commandConfiguration.getJSONArray("serverVars").get(i).toString())+"\"");
      finalCommandString.append(",");
      }
    }
    
    finalCommandString.deleteCharAt(finalCommandString.length()-1);
    finalCommandString.append("}");
    
    // System.out.println("FinalCommandString after is " + finalCommandString + " after the replaces");
    return finalCommandString.toString();
  }

  public String getCommandName() {
    return commandName;
  }

  public Integer getAckNumber() {
    return commandAckNumber;
  }
  
  public CharBuffer asCharBuffer() {
    System.out.println("Command.asCharBuffer: " + commandName + "[" + commandAckNumber.toString() +"]" + finalizeCommandString());
    return CharBuffer.wrap(commandName + "[" + commandAckNumber.toString() +"]" + finalizeCommandString());
  }
  
  public Callable<Pair<Integer, Integer>> reduce() {
    final Integer commandAckNumber = this.commandAckNumber;
    final String[] command = {commandConfiguration.getString("reductionCommand")};
    class ReductionCommand implements  Callable<Pair<Integer, Integer>> {

      public Pair<Integer, Integer> call() throws Exception {

        System.out.println("Reducing " + commandName + " by running " + commandConfiguration.get("reductionCommand").toString());
        
        System.out.println("Calling command");
        System.out.println(command);
        ProcessBuilder pb = new ProcessBuilder(command);
        
        
        //setupEnvironment(pb.environment());
        
        Process process = pb.start();
        System.out.println("started the process");
        //Read out dir output
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        System.out.printf("Output of running %s is:\n", Arrays.toString(command));
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        

        Integer exitValue = null;
        try {
          exitValue = process.waitFor();
          commandConfiguration.put("returnCode", Integer.toString(exitValue));
          System.out.println(commandConfiguration);
          System.out.println("\n\nExit Value is " + exitValue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         
        return new Pair<Integer, Integer>(commandAckNumber, exitValue);
        
      }
    }
    
    return new ReductionCommand();
  }

 
}

