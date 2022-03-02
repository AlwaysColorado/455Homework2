package cs455.scaling.Tasks;

//Describes a task to be carried out by a worker thread
public abstract class Task {
    //What kind of task it is.
    // This will only be useful if we need to look up the type of task from an abstract context.
    private final TaskType taskType;
    //Other shared fields of all tasks go here

    //super() constructor, takes enum/any other task info that's necessary from above.
    public Task (TaskType taskType){
        this.taskType = taskType;
    }

    //This can be called from a worker on any given task,
    // without caring what kind of task it is.
    // Leave the specifics of execution to the child.
    public abstract void executeTask();
}
