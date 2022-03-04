package cs455.scaling.threadpool;

import cs455.scaling.tasks.Task;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool{
    // instantiate blockingQueue 
    private BlockingQueue<Task> taskList;
    private AtomicBoolean isKillThreads = new AtomicBoolean(false);
    private Queue<BlockingQueue<Task>> batchList = new LinkedList<>();

    public ThreadPool(int threadPoolSize) throws InterruptedException {
        // create new blockingQueue
        taskList = new LinkedBlockingQueue<>();

        Worker[] workers = new Worker[threadPoolSize];

        // pull a taskList from batchList
        synchronized (batchList) {
            if (batchList.isEmpty()) {
                batchList.wait();
            }
            // task list is a batch which contains tasks
            // polls a batch
            taskList = batchList.poll();
        }
        // each worker should work on a separate batch
        for (int i = 0; i < threadPoolSize; i++) {
            workers[i] = new Worker(); // create a new worker to do all tasks from batch
            if (workers[i].isAvailable.get()) { // check if there is a free worker
                workers[i].start(); // run the worker
            }
        }
    }

    public void addTaskList(BlockingQueue<Task> taskList){
        synchronized (batchList){
            batchList.add(taskList);
            batchList.notify();
        }
    }

    public void killThreads(){
        isKillThreads = new AtomicBoolean(true);
    }


    private class Worker extends Thread{
        private Task task;
        private AtomicBoolean isAvailable;
        private AtomicBoolean running = new AtomicBoolean(true);

        private void killIndividualThread(){
            running = new AtomicBoolean(false);
        }

        public void run(){
            while(running.get()){
                synchronized (taskList){
                    while (taskList.size() == 0){
                        try{
                            taskList.wait();
                            isAvailable = new AtomicBoolean(false);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    task = taskList.poll();
                    isAvailable = new AtomicBoolean(true);
                    try{
                        assert task != null;
                        System.out.println("ThreadPool: Thread starting");
                        task.executeTask(); // run the task
                        System.out.println("ThreadPool: Worker has finished a task"); // message for testing can be removed later
                    }
                    catch (RuntimeException e){
                        System.out.println("ThreadPool: There was a problem with running the task" + e);
                    }
            }
        }

    }

}
}