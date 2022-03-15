package cs455.scaling.client;

public class ClientTimer extends Thread {

    private final long time;

    public ClientTimer(long time){
        this.time = time;
    }


    @Override
    public void run() {
        try {
            Thread.sleep(time); // 5 minutes
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Client timed out");
        System.exit(0);
    }
}
