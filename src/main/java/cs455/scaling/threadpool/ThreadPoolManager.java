package cs455.scaling.threadpool;

import cs455.scaling.tasks.HANDLE_TRAFFIC;
import cs455.scaling.tasks.REGISTER_CLIENT;
import cs455.scaling.tasks.Task;

import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPoolManager implements Runnable{

    private final ThreadPool threadPool;
    private LinkedBlockingQueue<Task> taskList;
    private final int batchSize;
    private final long batchTime;
    private TimerTask timerTask;
    private long startTime;
    private boolean tasksToAdd;

    public ThreadPoolManager (int threadPoolSize, int bs, long bt) {

        threadPool = new ThreadPool(threadPoolSize);
        batchSize = bs;
        batchTime = bt * 1000000000; // SECOND-TO-NANO Conversion
        tasksToAdd = true;
    }

    @Override
    public void run() {
        // If taskList doesn't exist, the batchTime has passed or the batchSize is met
        while (tasksToAdd) {
            // Create start the first taskList
            if (taskList == null || timesUp() || taskList.size() >= batchSize) {
                startNewTaskList();
            }
        }
        // Ensure any last are added to the ThreadPool for completion
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

    // Check for time left on batchTimer;
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

    public synchronized void terminate() {
        tasksToAdd = false;
    }

}
