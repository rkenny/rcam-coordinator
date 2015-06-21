package tk.bad_rabbit.rcam.distributed_backend.command;

public enum CommandState {
  NEW, AWAITING_ACK, ACKED, SENT, DONE, ERROR, RECEIVED
}
