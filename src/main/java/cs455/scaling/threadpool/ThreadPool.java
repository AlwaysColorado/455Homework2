package cs455.scaling.threadpool;

public class ThreadPool{
    // instantiate blockingQueue 
    private BlockingQueue<Runnable> blockingQueue;

    public ThreadPool(int sizeOfQueue, int numberOfThreads){
        // create new blockingQueue
        blockingQueue = new BlockingQueue<>(sizeOfQueue);
        // should be able to start a new task from this method. 
        

    }

}