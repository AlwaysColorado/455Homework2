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
    private AtomicBoolean pullTaskList = new AtomicBoolean(false);

    public ThreadPool(int tps) throws InterruptedException {
        // create new blockingQueue
        taskList = new LinkedBlockingQueue<>();

        Worker[] workers = new Worker[tps];
        for (int i = 0; i < tps; i++) {
            workers[i] = new Worker(); // create a new worker to do all tasks from batch
        }
        pullTaskList.set(true);
        while(pullTaskList.get()) {
            // pull a taskList from batchList
            synchronized (batchList) {
                if (batchList.isEmpty()) {
                    batchList.wait();
                }
                // task list is a batch which contains tasks
                // polls a batch
                // taskList size is 1
                taskList = batchList.poll();
            }
            // once it polls the task we want a worker to come and get the taskList
            for (int i = 0; i < tps; i++) {
                // I think there needs to be a condition to check if its available
                // not sure however, as the lock mechanism of synchronized in worker may make this
                // obsolete
                System.out.println("Worker collecting batch");
                workers[i].start();
                System.out.println("Worker finished with batch");
            }

            // if (!condition){
            //      pullTaskList.set(false);
            //}
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