package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;

public class ErrorCommand implements ICommand {
  public void run() {
    System.out.println("Error");
  }

  public CharBuffer asCharBuffer() {    
    return CharBuffer.wrap("Error");
  }
}
