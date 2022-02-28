package cs455.scaling.threadpool;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

public class ThreadPoolManager implements Runnable{

    // these will come as command line arguments 
    private int batchSize;
    private int threadPoolSize;
    private int batchTime;
    private Timer timer;

    public ThreadPoolManager(){
    }


    // basically all tasks should be called from this run method, i.e. register client, read client message
    public void run(){
        Random r = new Random();
        int number = r.nextInt();
        System.out.println(number);
    }


    public static void main(String[] args){
//      int portNum = Integer.parseInt(args[1]);
//      int threadPoolSize = Integer.parseInt(args[2]);
//      int batchSize = Integer.parseInt(args[3]);
//      int batchTime = Integer.parseInt(args[4]);
        ThreadPool tp = new ThreadPool(10);
        System.out.println("ThreadPool Created");
        for (int i=0; i<15; i++){
            ThreadPoolManager tpm = new ThreadPoolManager();
            System.out.println("New Task");
            tp.executeThreadPool(tpm);
            tp.killThreads();
        }
    }

}