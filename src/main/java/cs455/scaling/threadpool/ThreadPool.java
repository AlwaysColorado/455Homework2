package cs455.scaling.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ThreadPool{
    // instantiate blockingQueue 
    private BlockingQueue<Runnable> blockingQueue;
    private WorkerPool[] workers;

    public ThreadPool(int threadPoolSize){
        // create new blockingQueue
        blockingQueue = new LinkedBlockingQueue<Runnable>(); // new BlockingQueue that is unbounded. 
        workers = new WorkerPool[threadPoolSize];
        for(int i=0; i<threadPoolSize; i++){
            workers[i] = new WorkerPool();
            workers[i].start();
        }

    }

    public void killThreads(){
        // I think this needs an interrupt or something maybe someone else will know the answer. 
    }


    private class WorkerPool extends Thread{
    
        public void run(){
            while(true){
                Runnable task = blockingQueue.take(); // this will remove a task from the list, take is useful as it waits for non empty.
                try{
                    executorTask.run(); // run the task 
                    System.out.println("Thread has finised its task"); // message for testing can be removed later
                }
                catch (RuntimeException e){
                    System.out.println("There was a problem with running the task");
                }
            }
        }

    }

}