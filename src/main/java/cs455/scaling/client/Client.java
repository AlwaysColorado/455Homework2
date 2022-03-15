package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;

import cs455.scaling.util.Hashing;

public class Client {

    private final Queue<String> hashed_list = new LinkedList<>();
    private static SocketChannel clientSocket;
    private static ByteBuffer buffer;
    private long totalSent;
    private long totalReceived;
    Timer timer;

    public Client() throws IOException {

        try {
            // connect to the server
            clientSocket = SocketChannel.open(new InetSocketAddress("localhost", 9900)); //local host will be changed later
            buffer = ByteBuffer.allocate(256);
        } catch(IOException e){
            System.out.println("Problem with allocating buffer or clientSocket will not open");
            clientSocket.close();
        }

        totalSent = 0;
        totalReceived = 0;

        // CLIENT TIMER:  5 minutes in milliseconds
        long clientTimeoutDuration = 300000;
        ClientTimer clientTimer = new ClientTimer(clientTimeoutDuration);
        clientTimer.start();

        // PRINT TIMER: Print totals every 20 seconds
        timer = new Timer();
        timer.scheduleAtFixedRate(new ClientPrintTimer(this), 0, 20000);

    }

    private void sendMessageAndCheckResponse() throws NoSuchAlgorithmException, IOException {
        byte[] message = generateRandomByteMessage();
        hashRandomByteMessages(message); // add it to the list (hashed)
        buffer = ByteBuffer.wrap(message); // add it to buffer
        try{
            clientSocket.write(buffer);
            buffer.clear();
            clientSocket.read(buffer);
            String hash_response = new String(buffer.array()).trim();
            boolean hashInTable = checkAndDeleteHash(hash_response);
            // probably need to handle if hashInTable is false
            if (hashInTable){
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
            clientSocket.close();
        }
    }

     private byte[] generateRandomByteMessage(){
        Random random = new Random();
        byte[] byteMessage = new byte[8000];
        random.nextBytes(byteMessage);
        return byteMessage;
    }

    private void hashRandomByteMessages(byte[] message) throws NoSuchAlgorithmException {
        Hashing hashingDevice = new Hashing();
        String hashed_message = hashingDevice.SHA1FromBytes(message);
        hashed_list.add(hashed_message);
    }

    private boolean checkAndDeleteHash(String message){
        if (hashed_list.contains(message)){
            hashed_list.remove(message);
            return true;
        }
        else{
            System.out.println("Hash not found" + message);
            return false;
        }
    }

    public synchronized long getTotalSent() {
        long sent = totalSent;
        totalSent = 0;
        return sent;
    }

    public synchronized long getTotalReceived() {
        long received = totalReceived;
        totalReceived = 0;
        return received;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        for (int i=0; i<100; i++) {
            Client client = new Client();
            client.sendMessageAndCheckResponse();
        }
    }
}

