package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;

public class AckCommand implements ICommand {
  public void run() {
    System.out.println("Ack");
  }

  public CharBuffer asCharBuffer() {    
    return CharBuffer.wrap("Ack");
  }
}
