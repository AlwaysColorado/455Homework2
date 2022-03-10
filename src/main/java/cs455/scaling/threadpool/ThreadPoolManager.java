package cs455.scaling.threadpool;

import cs455.scaling.tasks.Task;

import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolManager implements Runnable{

    private final ThreadPool threadPool;
    private LinkedBlockingQueue<Task> taskList;
    private final int batchSize;
    private final long batchTime;
    private long startTime;
    private boolean tasksToAdd; // Server can set to false to exit run() while loop

    public ThreadPoolManager (int threadPoolSize, int bs, long bt) {

        threadPool = new ThreadPool(threadPoolSize);
        batchSize = bs;
        batchTime = bt * 1000000000; // SECOND-TO-NANO Conversion
        tasksToAdd = true;
    }

    @Override
    public void run() {
        // While the server is still sending tasks
        while (tasksToAdd) {
            // Create the first taskList
            if (taskList == null || timesUp() || taskList.size() >= batchSize) {
                startNewTaskList();
            }
        }
        // Ensure the last TaskList is added to the ThreadPool for completion
        threadPool.addTaskList(taskList);
    }

    // Start a new TaskList
    private void startNewTaskList() {
        // If this isn't the first TaskList, pass the list to the ThreadPool
        if (taskList != null) {
            threadPool.addTaskList(taskList);
        }

        // Create a new taskList and set the start time
        taskList = new LinkedBlockingQueue<>();
        startTime = System.nanoTime();
    }

    // Check for time left on batchTimer
    private boolean timesUp() {
        long currTime = System.nanoTime();
        long timePast = currTime - startTime;
        return ( timePast >= batchTime );
    }


    // Called by Server when a task is available
    public synchronized void addTask(Task task) {
        boolean taskAdded = false;

        // while the task has not been added
        while ( !taskAdded ) {

            // If batchTime or batchSize have been met
            if (timesUp() || taskList.size() == batchSize) {
                startNewTaskList();
            }

            // Try adding the task
            taskAdded = taskList.offer(task);

        }
    }

    // Run while loop condition to false so the final tasks will be added to the
    //      BatchList before closing the TPM
    public synchronized void terminate() {
        tasksToAdd = false;
    }

}
