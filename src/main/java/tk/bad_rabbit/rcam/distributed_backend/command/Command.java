package tk.bad_rabbit.rcam.distributed_backend.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.action.ICommandAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.IResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ACommandState;

public class Command extends ACommand  {
  private String commandName;
  private Integer commandAckNumber;
  private JSONObject clientVariables;
  
  public JSONObject getClientVariables() {
    return clientVariables;
  }
  
  //private JSONObject commandConfiguration;
  //private JSONObject serverVariables;
  
  private volatile Map<String, ACommandState> state;
  
  //private Integer returnCode;

  
  
  //public void setReturnCode(Integer returnCode) {
  //  this.returnCode = returnCode;
  //}
  
  //public Integer getReturnCode() {
  //  return this.returnCode;
  //}
     
  public Command() {
    this.state = new HashMap<String, ACommandState>();
  }
  
  public Command(String commandName, Integer commandAckNumber, JSONObject commandConfiguration,
      JSONObject clientVariables, JSONObject serverVariables, IResponseAction commandResponseAction) {
    this();
    this.commandName = commandName;
    //this.commandConfiguration = commandConfiguration;
    this.commandAckNumber = commandAckNumber;
    //this.clientVariables = clientVariables;
    //this.serverVariables = serverVariables;
  }
  
  public Command(String commandName, Integer commandAckNumber, JSONObject clientVariables) {
    this();
    this.commandName = commandName;
    this.commandAckNumber = commandAckNumber;
    this.clientVariables = clientVariables;
  }
  
  
  //public Object getClientVariable(String variableName) {
  //  return clientVariables.has(variableName) ? clientVariables.get(variableName) : null; 
  //}
  
  //public Object getServerVariable(String variableName) {
  //  return serverVariables.has(variableName) ? serverVariables.get(variableName) : null;
  //}
  
  //public Object getConfigurationObject(String variableName) {
  //  return commandConfiguration.has(variableName) ? commandConfiguration.get(variableName) : null;
  //}
  
  //public void setClientVariable(String variableName, Object variable) {
  //  this.clientVariables.put(variableName, variable);
  //}
  
  
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
  
  public  Map<String, ACommandState> getStates() {
    synchronized(this.state) {
      return this.state;
    }
  }
  
  public ACommandState getState(String server) {
    return this.state.get(server);
  }
  
  public void setState(ACommandState newState) {
    Iterator<String> i = state.keySet().iterator();
    
    while(i.hasNext()) {
      this.setState(i.next(), newState);
    }

    setChanged();
    notifyObservers(newState);
    
    return;
  }
  
  
  public synchronized void setState(String client, ACommandState state) {    
    state.addObserver(this);
    this.state.put(client,  state);

    setChanged();
    notifyObservers(client);
    
    return;
  }
  
  public Boolean stateEquals(String server, ACommandState comparisonState) {
    return this.state.get(server).equals(comparisonState);
  }
  
  
  public Boolean isType(String commandType) {
    return this.commandName.equals(commandType);
  }
  
  //public Boolean isIgnored() {
  //  return(commandConfiguration.getJSONObject("commandVars").get("ignored") == "true");
  //}
  
  
//  public String finalizeCommandString() {
//    StringBuilder finalCommandString = new StringBuilder();
//
//    finalCommandString.append("{");
//    if(commandConfiguration.has("clientVars")) {
//      for(int i = 0; i < commandConfiguration.getJSONArray("clientVars").length(); i++) {
//        finalCommandString.append("\""+commandConfiguration.getJSONArray("clientVars").get(i).toString()+"\":");
//        finalCommandString.append("\""+clientVariables.get(commandConfiguration.getJSONArray("clientVars").get(i).toString())+"\"");
//        finalCommandString.append(",");
//      }
//    }
//
//    Iterator<String> variableIterator = commandConfiguration.getJSONObject("commandVars").keys();
//    while(variableIterator.hasNext()) {
//      String key = variableIterator.next();
//      finalCommandString.append("\"" + key + "\":\"" + commandConfiguration.getJSONObject("commandVars").get(key).toString()+"\"");
//      finalCommandString.append(",");
//    }
//    
//    if(commandConfiguration.has("serverVars")) {
//      for(int i = 0; i < commandConfiguration.getJSONArray("serverVars").length(); i++) {
//      finalCommandString.append("\""+commandConfiguration.getJSONArray("serverVars").get(i).toString()+"\":");
//      finalCommandString.append("\""+clientVariables.get(commandConfiguration.getJSONArray("serverVars").get(i).toString())+"\"");
//      finalCommandString.append(",");
//      }
//    }
//    
//    finalCommandString.deleteCharAt(finalCommandString.length()-1);
//    finalCommandString.append("}");
//    
//    return finalCommandString.toString();
//  }

  public String getCommandName() {
    return commandName;
  }

  public Integer getAckNumber() {
    return commandAckNumber;
  }
  
//  public CharBuffer asCharBuffer() {
//    return CharBuffer.wrap(commandName + "[" + commandAckNumber.toString() +"]" + finalizeCommandString()+"\n");
//  }
  
//  public void setupEnvironment(Map<String, String> environment) {    
//    Iterator<String> serverVariableIterator = serverVariables.keys();
//    while(serverVariableIterator.hasNext()) {
//      String key = serverVariableIterator.next();
//      environment.put(key, serverVariables.get(key).toString());
//    }     
//     
//    Iterator<String> variableIterator = commandConfiguration.getJSONObject("commandVars").keys();
//    while(variableIterator.hasNext()) {
//      String key = variableIterator.next();
//      environment.put(key, commandConfiguration.getJSONObject("commandVars").get(key).toString());
//    }
//         
//     Iterator<String> clientVariableIterator = clientVariables.keys();
//     while(clientVariableIterator.hasNext()) {
//       String key = clientVariableIterator.next();
//       environment.put(key, clientVariables.get(key).toString());
//     }
//  }
  
  
  //public Callable<Map.Entry<Integer, Integer>> run(final String scriptToRun) {
//  public Callable<Void> run(final String scriptToRun) {
//    System.out.println("Command("+getCommandName()+"["+getAckNumber()+"]) going to run " + scriptToRun);
//    //class ReductionCommand implements  Callable<Map.Entry<Integer, Integer>> {
//      //public Map.Entry<Integer, Integer> call() throws Exception {
//    class ReductionCommand implements Callable<Void>, ICommandAction {
//      public Void call() throws Exception {
//        ProcessBuilder pb = new ProcessBuilder(commandConfiguration.getJSONObject("executables").getString(scriptToRun));
//        setupEnvironment(pb.environment());
//        
//        Process process = pb.start();
//
//        //Read out dir output
//        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        String line;
//
//        while ((line = br.readLine()) != null) {
//            System.out.println(line);
//        }
//
//        Integer exitValue = null;
//        try {
//          exitValue = process.waitFor();
//          commandConfiguration.put("returnCode", Integer.toString(exitValue));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//         
//        return null; //new AbstractMap.SimpleEntry<Integer, Integer>(commandAckNumber, exitValue);
//        
//      }
//    }
//    
//    return new ReductionCommand();
//  }

 
}

