package cs455.scaling.threadpool;

import cs455.scaling.tasks.Task;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool{
    // instantiate blockingQueue 
    private final BlockingQueue<Task> blockingQueue;
    private final Worker[] workers;
    private AtomicBoolean isKillThreads = new AtomicBoolean(false);

    public ThreadPool(int threadPoolSize) {
        // create new blockingQueue
        blockingQueue = new LinkedBlockingQueue<>(); // new BlockingQueue that is unbounded.
        workers = new Worker[threadPoolSize]; // set the workerPool with threadpool size
        //this.isKillThreads = new AtomicBoolean(false); // set kill-threads method to false
        // this needs to be edited because currently all workers can go, but we want to limit when workers can go
        for(int i=0; i<threadPoolSize; i++){
            workers[i] = new Worker();
            workers[i].start();
        }

    }

    public void executeThreadPool(Task task){
        // we only want to execute the task based on conditions, such as batch-size or batch-time
        synchronized (blockingQueue){
            blockingQueue.add(task);
            blockingQueue.notify();
        }
    }

    public void killThreads(){
        isKillThreads = new AtomicBoolean(true);
    }


    private class Worker extends Thread{
        private Task task;
        public void run(){
            while(!isKillThreads.get()){
                synchronized (blockingQueue){
                    while (blockingQueue.size() == 0){
                        try{
                            blockingQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    task = blockingQueue.poll();
                    try{
                        assert task != null;
                        System.out.println("ThreadPool: Thread starting");
                        task.executeTask(); // run the task
                        System.out.println("ThreadPool: Thread has finished its task"); // message for testing can be removed later
                    }
                    catch (RuntimeException e){
                        System.out.println("ThreadPool: There was a problem with running the task" + e);
                    }
            }
        }

    }

}
}