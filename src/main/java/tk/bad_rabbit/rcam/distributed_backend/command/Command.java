package tk.bad_rabbit.rcam.distributed_backend.command;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.AbstractMap;
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
import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ACommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.states.CommandReadyToReduceState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ErrorCommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ICommandState;

public class Command extends ACommand {
  private String commandName;
  private String origin;
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
 
  public String getOrigin() {
    return origin;
  }
  
  public void setOrigin(String origin) {
    this.origin = origin;
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
    //this.commandResponseAction = commandResponseAction;
    //this.commandResponseRelatedAction = commandResponseAction;
    
    System.out.println("RCam Coordinator - Command("+commandName+"["+getAckNumber()+"]) - created");
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
    System.out.println("RCam Coordinator - Command("+commandName+"["+getAckNumber()+"]) - SetState called for " + client + " state " + state.getClass().getSimpleName());
    if(this.state.get(client) == null || !this.state.get(client).equals(new ErrorCommandState()) )  { 
    
      this.state.put(client,  state);
      
      Entry<String, ICommandState> serverState = new AbstractMap.SimpleEntry<String, ICommandState>(client, state);
      Entry<ACommand, Entry<String, ICommandState>> commandDetails = new AbstractMap.SimpleEntry<ACommand, Entry<String, ICommandState>>(this, serverState);
      
      setChanged();
      notifyObservers(commandDetails);
     
    }
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
  
  //public synchronized void setErrorState() {
    //setAllServersState(new ErrorCommandState());
  //}
  
  //public synchronized void setReducedState() {
    //setAllServersState(new CommandReducedState());
  //}
  

  

  public Boolean isType(String commandType) {
    return this.commandName.equals(commandType);
  }
  
  public Boolean isIgnored() {
    return(commandConfiguration.getJSONObject("commandVars").get("ignored") == "true");
  }
  
  public Boolean isReadyToReduce() {
    
    Iterator<Entry<String, ICommandState>> serversIterator = this.state.entrySet().iterator();
    CommandReadyToReduceState readyToReduceState = new CommandReadyToReduceState();
    
    while(serversIterator.hasNext()) {
      ICommandState serverState = serversIterator.next().getValue();
      
      if(commandConfiguration.has("reduceOnFirstResult") && (commandConfiguration.get("reduceOnFirstResult").equals("true"))) {
        if(serverState.typeEquals(readyToReduceState)) {
          return true;
        }
      }
     
      if(!serverState.typeEquals(readyToReduceState)) {
        return false;
      }
    }
    
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
  
  public Callable<Pair<Integer, Integer>> reduce() {
    final Integer commandAckNumber = this.commandAckNumber;
    final String[] command = {commandConfiguration.getString("reductionCommand")};
    class ReductionCommand implements  Callable<Pair<Integer, Integer>> {

      public Pair<Integer, Integer> call() throws Exception {

        ProcessBuilder pb = new ProcessBuilder(command);
        
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

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         
        return new Pair<Integer, Integer>(commandAckNumber, exitValue);
        
      }
    }
    
    return new ReductionCommand();
  }

 
}

