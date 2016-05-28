package tk.bad_rabbit.rcam.spring.runcontroller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import tk.bad_rabbit.rcam.distributed_backend.command.action.ActionHandler;
import tk.bad_rabbit.rcam.distributed_backend.command.action.ICommandAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.IRunResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

@Controller(value="runController")
@Scope("singleton")
public class RunController implements ActionHandler {
  
  
  ExecutorService commandExecutor;
  @PostConstruct
  public void initializeRunController() {
    this.commandExecutor = Executors.newFixedThreadPool(5);
  }

  public Future<Integer> handleAction(ICommandAction commandAction) {
   if(commandAction instanceof IRunResponseAction) {
     return commandExecutor.submit( ((IRunResponseAction) commandAction).getRunCallable());
   }
   return null;
  }
  
}