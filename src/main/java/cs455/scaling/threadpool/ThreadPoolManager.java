package cs455.scaling.threadpool;

import cs455.scaling.tasks.HANDLE_TRAFFIC;
import cs455.scaling.tasks.REGISTER_CLIENT;
import cs455.scaling.tasks.Task;

import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolManager implements Runnable{

    private final ThreadPool threadPool;
    private LinkedBlockingQueue<Task> taskList;
    private final int batchSize;
    private final long batchTime;
    private long startTime;

    private final long NANO_PER_SECOND = 1000000000;

    public ThreadPoolManager (int threadPoolSize, int bs, long bt) {
        threadPool = new ThreadPool(threadPoolSize);
        batchSize = bs;
        batchTime = bt * NANO_PER_SECOND;
    }

    @Override
    public void run() {

    }




}
