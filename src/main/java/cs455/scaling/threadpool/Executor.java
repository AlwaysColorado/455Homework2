package cs455.scaling.threadpool;

public class Executor implements Runnable{
    private BlockingQueue<Runnable> blockingQueue;

    public Executor(BlockingQueue<Runnable> bq){
        this.blockingQueue = bq;
    }

    @Override
    public void run(){
        try{
            while(true){
                Runnable executorTask = blockingQueue.remove(); // this will remove a task from the list 
                executorTask.run(); // run the task 
                System.out.println("Thread has finised its task"); // message for testing can be removed later
            }
        } catch (InterruptedException e){
            System.out.println("Interrupted Exception at Executor");
        }
    }
}