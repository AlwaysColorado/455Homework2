// package cs455.scaling.threadpool;

// import java.util.LinkedList;
// import java.util.Queue;

// public class BlockingQueue<T> {
//     private Queue<T> queue = new LinkedList<T>();
//     private int queueSize;
//     private int empty = 0;

//     public BlockingQueue(int qs){
//         this.queueSize = qs;
//     }

//     public synchronized void add(T task) throws InterruptedError{
//         // when the queue is full we should wait
//         while(this.queue.size() == this.queueSize){
//             wait();
//         }
//         // if the queue is empty lets notify
//         if (this.queue.size() == empty){
//             notifyAll();
//         }
//         // otherwise add the task to the queue
//         this.queue.offer(task);
//     }

//     public synchronized T remove(T task) throws InterruptedError{
//         // when the queue is empty we should wait
//         while(this.queue.size() == this.empty){
//             wait();
//         }
//         // if the queue is full we should notify 
//         if (this.queue.size() == this.queueSize){
//             notifyAll();
//         }
//         // otherwise remove and return the task
//         return this.queue.poll(task);
//     }
// }