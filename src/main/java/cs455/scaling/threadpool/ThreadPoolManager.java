package cs455.scaling.threadpool;

import java.util.Timer;
import java.util.TimerTask;


public class ThreadPoolManager{

    // these will come as command line arguments 
    private int batchSize;
    private int threadPoolSize;
    private int batchTime;
    private Timer timer;


    // create new Threadpool object 

    //ThreadPool threadPool = new ThreadPool(threadPoolSize); // this will keep pullling tasks and completing them

    // tasks to handle: 

    // Client registering 

    // Reading incoming data from a client 

    // Batch has reached batch-size -- get worker thread to do something 

    // Batch has expired batch-time -- get worker thread to do something 





    public Timer(){
        timer = new Timer();
        timer.schedule(new batchToRun(), batchTime)
    }

    class batchToRun() extends TimerTask{
        public void run(){
            
        }
    }

}