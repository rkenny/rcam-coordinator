package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.util.concurrent.Callable;

import tk.bad_rabbit.rcam.spring.commands.CommandController;

public interface IRelatedCommandAction {
  public Callable<Integer> getRelatedCallable(CommandController commandController);
}
