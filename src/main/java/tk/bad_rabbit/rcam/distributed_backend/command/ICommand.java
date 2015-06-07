package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;

public interface ICommand {
  public CharBuffer asCharBuffer();
  public Boolean isIgnored();
  public Integer getAckNumber();
  public String getCommandName();
}
