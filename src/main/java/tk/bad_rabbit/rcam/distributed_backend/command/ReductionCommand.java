package tk.bad_rabbit.rcam.distributed_backend.command;

import java.util.concurrent.Callable;

import tk.bad_rabbit.rcam.app.Pair;

public abstract class ReductionCommand implements Callable<Pair<Integer, Integer>>{

}
