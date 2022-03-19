package cs455.scaling.threadpool;

import cs455.scaling.tasks.Task;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool extends Thread {
    // instantiate blockingQueue
    private LinkedBlockingQueue<Task> taskList;
    private final AtomicBoolean isKillThreads = new AtomicBoolean(false);
    private final Queue<LinkedBlockingQueue<Task>> batchList = new LinkedList<>();
    private final AtomicBoolean pullTaskList = new AtomicBoolean(false);
    private final int tps;
    private final Worker[] workers;

    public ThreadPool(int tps) {
        // create new blockingQueue
        this.tps = tps;
        this.workers = new Worker[tps];
        for (int i = 0; i < tps; i++) {
            workers[i] = new Worker(); // create a new worker to do all tasks from batch
            workers[i].start();
        }
    }

    public void run() {
        pullTaskList.set(true);
        while (pullTaskList.get()) {
            // pull a taskList from batchList
            synchronized (batchList) {
                if (batchList.isEmpty()) {
                    try {
                        batchList.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // task list is a batch which contains tasks
                // polls a batch
                // taskList size is 1
                taskList = batchList.poll();
                workerHelper(taskList);

            }
        }
    }

    // this helper method will check if all the workers are available and add it to the
    public void workerHelper(LinkedBlockingQueue<Task> taskList){
        boolean foundWorker = false;
        int counter = 0;
        while(!foundWorker){

           if(workers[counter].isAvailable.get()){
               workers[counter].addTaskList(taskList);
               foundWorker = true;
           }
           counter++;
           // if counter hits tps and no worker has been found then we should reset the counter
            // to keep the counter going, so we only exit this loop if we find a worker
           if (counter == tps && !foundWorker){
               counter = 0;
           }
       }
    }


    public void addTaskList(LinkedBlockingQueue<Task> taskList){
        synchronized (batchList){
            batchList.add(taskList);
            batchList.notify();
        }
    }

    public void killThreads(){
        isKillThreads.set(true);
    }


    private class Worker extends Thread{
        private AtomicBoolean isAvailable = new AtomicBoolean(false);
        private AtomicBoolean running = new AtomicBoolean(false);
        private LinkedBlockingQueue<Task> taskList;

        public Worker() {
            taskList = new LinkedBlockingQueue<>();
        }
        private void killIndividualThread(){
            running.set(false);
        }

        public void addTaskList(LinkedBlockingQueue<Task> tl){
            synchronized (taskList) {
                this.taskList = tl;
                this.taskList.notify();
            }
        }

        public void run(){
            running.set(true);
            while(running.get()){
                isAvailable.set(false);
                synchronized (taskList) {
                    while (taskList.size() == 0) {
                        try {
                            isAvailable.set(true);
                            taskList.wait();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Task task = taskList.poll();
                assert task != null;
                System.out.println("Worker Thread: Thread starting");
                task.executeTask(); // run the task
                System.out.println("Worker Thread: Worker has finished a task"); // message for testing can be removed later
            }

        }

    }
}