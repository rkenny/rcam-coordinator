package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;

public class RecordCommand implements ICommand {

  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
    return CharBuffer.wrap("Record");
  }

}
