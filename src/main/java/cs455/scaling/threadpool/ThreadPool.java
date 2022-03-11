package cs455.scaling.threadpool;

import cs455.scaling.tasks.Task;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPool extends Thread {
    // instantiate blockingQueue
    private BlockingQueue<Task> taskList;
    private AtomicBoolean isKillThreads = new AtomicBoolean(false);
    private Queue<BlockingQueue<Task>> batchList = new LinkedList<>();
    private AtomicBoolean pullTaskList = new AtomicBoolean(false);
    private int tps;
    private Worker[] workers;

    public ThreadPool(int tps) throws InterruptedException {
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
            // just check if available and give to a worker
        }
    }

    // this helper method will check if all of the workers are available and add it to the
    public void workerHelper(BlockingQueue<Task> taskList){
        for(int i=0; i<tps; i++){
            if (workers[i].isAvailable.get()) {
                workers[i].addTaskList(taskList);
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
        isKillThreads.set(true);
    }


    private class Worker extends Thread{
        private Task task;
        private AtomicBoolean isAvailable = new AtomicBoolean(false);
        private AtomicBoolean running = new AtomicBoolean(false);
        private BlockingQueue<Task> taskList;

        private void killIndividualThread(){
            running.set(false);
        }

        public void addTaskList(BlockingQueue<Task> tl){
            this.taskList = tl;
            this.taskList.notify();
        }



        public void run(){
            running.set(true);
            while(running.get()){
                isAvailable.set(false);
                synchronized (taskList){
                    while (taskList.size() == 0){
                        try{
                            isAvailable.set(true);
                            taskList.wait();

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