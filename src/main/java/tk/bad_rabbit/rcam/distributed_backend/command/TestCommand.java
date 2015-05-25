package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;

public class TestCommand implements ICommand {
    public void run() {
      System.out.println("Test");
    }

    public CharBuffer asCharBuffer() {    
      return CharBuffer.wrap("Test");
    }
    
}
