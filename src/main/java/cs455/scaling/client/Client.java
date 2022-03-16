package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;

import cs455.scaling.util.Hashing;

public class Client {

    private final Queue<String> hashed_list = new LinkedList<>();
    private final String serverHostName;
    private final int serverPort, messageRate;
    private static SocketChannel clientSocket;
    private final Hashing hashingDevice = new Hashing();
    private final ByteBuffer writeBuffer = ByteBuffer.allocate(8196);
    private static ByteBuffer buffer;
    private long totalSent;
    private long totalReceived;
    Timer timer;

    public Client(String serverHostName, int serverPort, int messageRate) {
        this.serverHostName = serverHostName;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
    }

    public void runClient() {
        try {
            // connect to the server
            clientSocket = SocketChannel.open(new InetSocketAddress(serverHostName, serverPort));
            //TODO: send messages(sendMessageAndCheckResponse()) at rate this.rate
        } catch(IOException e){
            System.out.println("ClientSocket will not open");
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

    //TODO: this method probably needs to be refactored.
    // (read isn't blocking in this context considering the ThreadPool.)
    // Maybe handle reads and writes separately?
    private void sendMessageAndCheckResponse() throws IOException {
        byte[] message = generateRandomByteMessage();
        //save for read allocate
        String hashedMessage =  hashRandomByteMessages(message); // add it to the list (hashed)
        writeBuffer.put(message); // add it to buffer
        //try to dynamically allocate the SHA1 hash response length.
        ByteBuffer readBuffer = ByteBuffer.allocate(hashedMessage.getBytes().length);
        try{
            clientSocket.write(writeBuffer);
            writeBuffer.clear();
            incrementSent();
            clientSocket.read(readBuffer);
            String hash_response = new String(readBuffer.array()).trim();
            boolean hashInTable = checkAndDeleteHash(hash_response);
            // probably need to handle if hashInTable is false
            if (hashInTable){
                inrcementReceived();
                readBuffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
            clientSocket.close();
        }
    }

    private synchronized void incrementSent() {
        totalSent ++;
    }

    private synchronized void inrcementReceived() {
        totalReceived ++;
    }



    private byte[] generateRandomByteMessage(){
        Random random = new Random();
        byte[] byteMessage = new byte[8196];
        random.nextBytes(byteMessage);
        return byteMessage;
    }

    private String hashRandomByteMessages(byte[] message) {
        String hashed_message = hashingDevice.SHA1FromBytes(message);
        hashed_list.add(hashed_message);
        return hashed_message;
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

    public static void main(String[] args) {
        if(args.length != 3){
            System.out.println("Client expecting args in format: <Server Hostname> <Server Port> <Message rate/s>");
        }
        String hostname = args[0];
        int port = 0, rate = 0;
        try{
            port = Integer.parseInt(args[1]);
            rate = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.printf("Couldn't parse argument as an integer: %s\n", e.getMessage());
            System.exit(-1);
        }
        Client client = new Client(hostname, port, rate);
        client.runClient();
        /*
        for (int i=0; i<100; i++) {
            Client client = new Client();
            client.sendMessageAndCheckResponse();
        }*/
    }
}

