package cs455.scaling.client;

import java.util.Random;

public class Client {


    public Client(){

    }

     private byte[] generateRandomByteMessage(){
        Random random = new Random();
        byte[] byteMessage = new byte[8000];
        random.nextBytes(byteMessage);
        return byteMessage;
    } 

}
