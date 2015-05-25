package tk.bad_rabbit.rcam.distributed_backend.commandfactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.CharBuffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import tk.bad_rabbit.rcam.distributed_backend.command.AckCommand;
import tk.bad_rabbit.rcam.distributed_backend.command.ErrorCommand;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

@Service(value="commandFactory")
public class CommandFactory implements ICommandFactory {
    
  public ICommand createCommand(CharBuffer commandCharBuffer) {
    return createCommand(commandCharBuffer.toString());
  }
  
  public ICommand createCommand(String commandString) {
    ICommand command = null;
    Class<?> commandClass;
    System.out.println(commandString);
    try {
      if(commandString.contains("Record")) {
        
        commandClass = Class.forName("tk.bad_rabbit.rcam.distributed_backend.command." + commandString.trim() + "Command");
        Constructor<?> commandConstructor = commandClass.getConstructor();
        command = (ICommand) commandConstructor.newInstance();
      }
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
        return command;
  }

  public ICommand ackCommand() {
    // TODO Auto-generated method stub
    return new AckCommand();
  }
  
  public ICommand errorCommand() {
    return new ErrorCommand();
  }

}