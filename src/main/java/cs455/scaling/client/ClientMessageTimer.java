package cs455.scaling.client;

import java.io.IOException;
import java.util.TimerTask;

public class ClientMessageTimer extends TimerTask {

    Client client;
    ClientMessageTimer(Client client){this.client = client;}

    @Override
    public void run() {
        try {
            client.sendMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
