package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.concurrent.Callable;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.states.ACommandState;
import tk.bad_rabbit.rcam.spring.runcontroller.RunController;

public interface IRunResponseAction {
  public Callable<Integer> getRunCallable();
}
