package cs455.scaling.threadpool;

import cs455.scaling.tasks.Task;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool{
    // instantiate blockingQueue 
    private BlockingQueue<Task> taskList; {
    };
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
            workers[i].start(); // run the worker
        }

    }

    public void addTaskList(BlockingQueue<Task> taskList){
        synchronized (batchList){
            batchList.add(taskList);
            batchList.notify();
        }
    }

    public void killThreads(){
        isKillThreads.set(true);
    }


    private class Worker extends Thread{
        private Task task;
        private AtomicBoolean isAvailable = new AtomicBoolean(false);
        private AtomicBoolean running = new AtomicBoolean(false);

        private void killIndividualThread(){
            running.set(false);
        }

        public void run(){
            running.set(true);
            while(running.get()){
                isAvailable.set(false);
                synchronized (taskList){
                    while (taskList.size() == 0){
                        try{
                            taskList.wait();
                            isAvailable.set(true);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    task = taskList.poll();
                    try{
                        assert task != null;
                        System.out.println("Worker Thread: Thread starting");
                        task.executeTask(); // run the task
                        System.out.println("Worker Thread: Worker has finished a task"); // message for testing can be removed later
                    }
                    catch (RuntimeException e){
                        System.out.println("Worker Thread: There was a problem with running the task" + e);
                    }
            }
        }

    }

}
}