package cs455.scaling.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool{
    // instantiate blockingQueue 
    private final BlockingQueue<Runnable> blockingQueue;
    private final WorkerPool[] workers;
    private AtomicBoolean isKillThreads;

    public ThreadPool(int threadPoolSize){
        // create new blockingQueue
        blockingQueue = new LinkedBlockingQueue<>(); // new BlockingQueue that is unbounded.
        workers = new WorkerPool[threadPoolSize]; // set the workerPool with threadpool size
        isKillThreads = new AtomicBoolean(false); // set killthreads method to false
        // this needs to be edited because currently all workers can go, but we want to limit when workers can go
        for(int i=0; i<threadPoolSize; i++){
            workers[i] = new WorkerPool();
            workers[i].start();
        }

    }

    public void executeThreadPool(Runnable task){
        // we only want to execute the task based on conditions, such as batch-size or batch-time
        synchronized (blockingQueue){
            blockingQueue.add(task);
            blockingQueue.notify();
        }
    }

    public void killThreads(){
        isKillThreads = new AtomicBoolean(true);
    }


    private class WorkerPool extends Thread{
        private Runnable task;
        public void run(){
            while(true){
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
                            task.run(); // run the task
                            System.out.println("Thread has finished its task"); // message for testing can be removed later
                        }
                        catch (RuntimeException e){
                            System.out.println("There was a problem with running the task");
                        }
            }
        }

    }

}
}