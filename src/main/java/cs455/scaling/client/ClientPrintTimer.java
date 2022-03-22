package cs455.scaling.client;

import java.util.Date;
import java.util.TimerTask;

public class ClientPrintTimer extends TimerTask {

    Client client;
    ClientPrintTimer(Client client) {
        this.client = client;
    }


    @Override
    public void run() {
        String date = new Date().toString(); // Get timestamp
        long sent = client.getTotalSent();
        long received = client.getTotalReceived();

        System.out.printf("\n[%s] Total Sent Count: %d, Total Received Count: %d \n", date, sent, received);
        System.out.println("IN QUEUE: " + client.getNumHashesInQueue() + "\n");
    }
}
