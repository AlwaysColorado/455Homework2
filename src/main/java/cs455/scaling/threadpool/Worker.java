package cs455.scaling.threadpool;

import cs455.scaling.tasks.Task;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Worker extends Thread{
    public AtomicBoolean isAvailable = new AtomicBoolean(false);
    public AtomicBoolean running = new AtomicBoolean(false);
    private LinkedBlockingQueue<Task> taskList;

    public Worker() {
        this.taskList = new LinkedBlockingQueue<>();
    }
    private void killIndividualThread(){
        running.set(false);
    }

    public void addTaskList(LinkedBlockingQueue<Task> tl){
        this.taskList.addAll(tl);
        synchronized (this.taskList) {
            this.taskList.notify();
        }
    }

    public void run(){
        running.set(true);
        while(running.get()){
            synchronized (this.taskList) {
                while (taskList.size() == 0) {
                    try {
                        isAvailable.set(true);
                        this.taskList.wait();
                        isAvailable.set(false);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Task task = this.taskList.poll();
            assert task != null;
            task.executeTask(); // run the task
        }

    }

}