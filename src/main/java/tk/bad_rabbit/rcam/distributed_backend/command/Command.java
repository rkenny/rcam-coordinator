package tk.bad_rabbit.rcam.distributed_backend.command;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observer;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.app.Pair;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ACommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReadyToReduceState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ErrorCommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;

public class Command extends ACommand {
  private String commandName;

  private Integer commandAckNumber;
  
  private JSONObject clientVariables;
  private JSONObject commandConfiguration;
  private JSONObject serverVariables;
  
  private volatile Map<String, ICommandState> state;
  
  private Integer returnCode;
  
  public void setReturnCode(Integer returnCode) {
    this.returnCode = returnCode;
  }
  
  public Integer getReturnCode() {
    return this.returnCode;
  }
 
  
  
//  public synchronized void update(Observable updatedClient, Object serverWithPort) {
//    if(updatedClient instanceof ServerThread) {
//      System.out.println("The server needs to set a command into error state if it's not connected to "+  serverWithPort );
//    }
//  }
  
    
  public Command() {
    this.state = new HashMap<String, ICommandState>();
  }
  
  public Command(String commandName, Integer commandAckNumber, JSONObject commandConfiguration,
      JSONObject clientVariables, JSONObject serverVariables, ACommandResponseAction commandResponseAction) {
    this();
    this.commandName = commandName;
    this.commandConfiguration = commandConfiguration;
    this.commandAckNumber = commandAckNumber;
    this.clientVariables = clientVariables;
    this.serverVariables = serverVariables;

  }
  
  
  public Object getClientVariable(String variableName) {
    return clientVariables.has(variableName) ? clientVariables.get(variableName) : null; 
  }
  
  public Object getServerVariable(String variableName) {
    return serverVariables.has(variableName) ? serverVariables.get(variableName) : null;
  }
  
  public void setClientVariable(String variableName, Object variable) {
    this.clientVariables.put(variableName, variable);
  }
  
  
  public void setServers(Set<String> servers) {
    Iterator<String> i = servers.iterator();
    while(i.hasNext()) {
      String s = i.next();
      state.put(s, null);
    }
  }
  
  public Set<String> getServers() {
    return state.keySet();
  }
  
  public ICommandState getState(String server) {
    return this.state.get(server);
  }
  
  public void setState(ICommandState newState) {
    Iterator<String> i = state.keySet().iterator();
    while(i.hasNext()) {
      String s = i.next();
      this.setState(s, newState);
    }
    return;
  }
  
  
  public synchronized void setState(String client, ICommandState state) {
    //System.out.println("RCam Coordinator - Command("+commandName+"["+getAckNumber()+"]) - SetState called for " + client + " state " + state.getClass().getSimpleName());
    //if(this.state.get(client) == null || !this.state.get(client).equals(new ErrorCommandState()) )  { 
    
      this.state.put(client,  state);
      
      //Entry<String, ICommandState> serverState = new AbstractMap.SimpleEntry<String, ICommandState>(client, state);
      //Entry<ACommand, Entry<String, ICommandState>> commandDetails = new AbstractMap.SimpleEntry<ACommand, Entry<String, ICommandState>>(this, serverState);
      
      //Entry<ACommand, String> commandDetails = new AbstractMap.SimpleEntry<ACommand, String>(this, client);
      
      setChanged();
      notifyObservers(client);
     
    //}
    return;
  }
  
  public Boolean stateEquals(String server, ICommandState comparisonState) {
    return this.state.get(server).equals(comparisonState);
  }
  
  public synchronized void setAllServersState(ICommandState newState) {
    Set<Entry<String, ICommandState>> servers = this.state.entrySet();
    Iterator<Entry<String, ICommandState>> serversIterator = servers.iterator();
    while(serversIterator.hasNext()) {
      String server = serversIterator.next().getKey();
      this.setState(server, newState);
    }
  }
  
  public Boolean isType(String commandType) {
    return this.commandName.equals(commandType);
  }
  
  public Boolean isIgnored() {
    return(commandConfiguration.getJSONObject("commandVars").get("ignored") == "true");
  }
  
  public Boolean isReadyToReduce() {
    //System.out.println("Command("+getCommandName()+"["+getAckNumber()+"]).isReadyToReduce called");
    Iterator<Entry<String, ICommandState>> serversIterator = this.state.entrySet().iterator();
    CommandReadyToReduceState readyToReduceState = new CommandReadyToReduceState();
    
    while(serversIterator.hasNext()) {
      ICommandState serverState = serversIterator.next().getValue();
      
      if(commandConfiguration.has("reduceOnFirstResult") && (commandConfiguration.get("reduceOnFirstResult").equals("true"))) {
        if(serverState.typeEquals(readyToReduceState)) {
          //System.out.println("Command("+getCommandName()+"["+getAckNumber()+"]).isReadyToReduce returning true because the first server is ready to reduce.");
          return true;
        }
      }
     
      if(!serverState.typeEquals(readyToReduceState)) {
        //System.out.println("Command("+getCommandName()+"["+getAckNumber()+"]).isReadyToReduce returning false because a server is not ready to reduce.");
        return false;
      }
    }
    System.out.println("Command("+getCommandName()+"["+getAckNumber()+"]).isReadyToReduce returning true because all servers are ready to reduce.");
    return true;
  }
  
  
  public String finalizeCommandString() {
    StringBuilder finalCommandString = new StringBuilder();

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
    
    return finalCommandString.toString();
  }

  public String getCommandName() {
    return commandName;
  }

  public Integer getAckNumber() {
    return commandAckNumber;
  }
  
  public CharBuffer asCharBuffer() {
    return CharBuffer.wrap(commandName + "[" + commandAckNumber.toString() +"]" + finalizeCommandString());
  }
  
  
public void setupEnvironment(Map<String, String> environment) {
    
    Iterator<String> serverVariableIterator = serverVariables.keys();
    while(serverVariableIterator.hasNext()) {
      String key = serverVariableIterator.next();
      environment.put(key, serverVariables.get(key).toString());
    }     
     
    Iterator<String> variableIterator = commandConfiguration.getJSONObject("commandVars").keys();
    while(variableIterator.hasNext()) {
      String key = variableIterator.next();
      environment.put(key, commandConfiguration.getJSONObject("commandVars").get(key).toString());
    }
         
     Iterator<String> clientVariableIterator = clientVariables.keys();
     while(clientVariableIterator.hasNext()) {
       String key = clientVariableIterator.next();
       environment.put(key, clientVariables.get(key).toString());
     }
  }
  
  public Callable<Map.Entry<Integer, Integer>> reduce() {
    
    
    class ReductionCommand implements  Callable<Map.Entry<Integer, Integer>> {

      public Map.Entry<Integer, Integer> call() throws Exception {

        ProcessBuilder pb = new ProcessBuilder(commandConfiguration.getString("reductionCommand"));
        setupEnvironment(pb.environment());
        
        Process process = pb.start();

        //Read out dir output
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;

        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        

        Integer exitValue = null;
        try {
          exitValue = process.waitFor();
          commandConfiguration.put("returnCode", Integer.toString(exitValue));
          //System.out.println("RCam Coordinator - Command - Reduction Complete - The state of the command needs to move to CommandReducedState.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         
        return new AbstractMap.SimpleEntry<Integer, Integer>(commandAckNumber, exitValue);
        
      }
    }
    
    return new ReductionCommand();
  }

 
}

