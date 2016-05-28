package tk.bad_rabbit.rcam.distributed_backend.command.states;

import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.coordinator.server.ServerThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ACommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ANetworkResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ARunResponseAction;
import tk.bad_rabbit.rcam.spring.commands.CommandController;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public interface ICommandState {
  
  ANetworkResponseAction getNetworkResponseAction();
  public Future<Integer> doNetworkAction(ServerThread actionObserver, String server, ACommand actionSubject);
  void setNetworkResponseAction(ANetworkResponseAction newNetworkResponseAction);
  
  public Future<Integer> doRelatedCommandAction(CommandController actionObserver, String server, ACommand actionSubject);
  
  
  
  ACommandResponseAction getRelatedCommandResponseAction();
  
  
  void setRelatedCommandResponseAction(ACommandResponseAction newRelatedCommandResponseAction);
  void setRunCommandResponseAction(ARunResponseAction newRunCommandResponseAction);
  
  //void _setNetworkResponseAction(ACommandResponseAction newNetworkResponseAction);
  //void _setRelatedCommandResponseAction(ACommandResponseAction newRelatedCommandResponseAction);
  //void _setRunCommandResponseAction(IActionType newRunCommandResponseAction);
  
  ARunResponseAction getRunCommandResponseAction();
  public Future<Integer> doRunCommandAction(RunController actionObserver, ACommand actionSubject, ACommandState commandState);
  
  
  //abstract void nextState(String server, ACommand actionSubject);
  //abstract ACommandState getNextState();
  
  public Boolean typeEquals(ICommandState comparisonState);
}
