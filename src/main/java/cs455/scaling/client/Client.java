package cs455.scaling.client;

import java.io.IOException;
import java.net.ConnectException;
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
    private long totalSent;
    private long totalReceived;
    Timer timerForPrint;
    Timer timerForMessage;

    public Client(String serverHostName, int serverPort, int messageRate) {
        this.serverHostName = serverHostName;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
    }

    public void runClient()  {
        try {
            // connect to the server
            clientSocket = SocketChannel.open();
            clientSocket.connect(new InetSocketAddress(serverHostName, serverPort));
            //clientSocket.finishConnect();
        } catch (ConnectException e) {
            System.out.println("Waiting for connection");
        } catch (IOException e) {
            System.out.println("ClientSocket will not open");
        }

        totalSent = 0;
        totalReceived = 0;

        // CLIENT TIMER:  10 minutes in milliseconds
        long clientTimeoutDuration = 600000;
        ClientTimer clientTimer = new ClientTimer(clientTimeoutDuration);
        clientTimer.start();

        // PRINT TIMER: Print totals every 20 seconds
        timerForPrint = new Timer();
        timerForPrint.scheduleAtFixedRate(new ClientPrintTimer(this), 0, 20000);

        // SEND MESSAGE TIMER: Send messages at messageRate
        timerForMessage = new Timer();
        timerForMessage.scheduleAtFixedRate(new ClientMessageTimer(this), 0, 1000/messageRate);

        // CHECK FOR MESSAGES
        checkMessages();

    }

    public void sendMessages() throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.wrap(generateRandomByteMessage());
        hashRandomByteMessages(writeBuffer.array()); // add it to the list (hashed)
        int bytesWritten = 0;
        try {
            while(writeBuffer.hasRemaining())
                bytesWritten += clientSocket.write(writeBuffer);
            if(bytesWritten == 0){
                System.out.println("Write failed, nothing got written.");
            }
            writeBuffer.clear();
            incrementSent();
        }catch (IOException e) {
            e.printStackTrace();
            clientSocket.close();
            System.out.println("Client Socket Closed due to SendMessage error");
        }
    }

    private void checkMessages() {
        ByteBuffer readBuffer = ByteBuffer.allocate(40);
        while(true){
            int bytesRead = 0;
            // want to access the stored list and check if the hash is there
            // I think that it should always be 8196.
            // Could potentially be changed to ByteBuffer.allocate(8196);
            try{
                while(readBuffer.hasRemaining() && bytesRead != -1)
                    bytesRead = clientSocket.read(readBuffer);
                if(bytesRead == -1){
                    //-1 means that the channel is closed. close the socket.
                    clientSocket.close();
                    return;
                }
                String hash_response = new String(readBuffer.array()).trim(); // not sure if trim is needed
                boolean hashInTable = checkAndDeleteHash(hash_response);
                // probably need to handle if hashInTable is false
                if (hashInTable){
                    inrcementReceived();
                }
                else{
                    // error checking message
                    System.out.println("Receiving a hash from server that is not in the LinkedList");
                }
                readBuffer.clear();
            } catch (IOException e) {
                //if .read fails, the server is likely dead. kill this client.
                System.out.println("Lost connection to the Server. Exiting.");
                System.exit(0);
            }
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

    private void hashRandomByteMessages(byte[] message) {
        String hashed_message = hashingDevice.SHA1FromBytes(message);
        synchronized (hashed_list) {
            hashed_list.add(hashed_message);
        }
   }

    private boolean checkAndDeleteHash(String message){
        if (hashed_list.contains(message)){
            synchronized (hashed_list) {
                hashed_list.remove(message);
            }
            return true;
        }
        else{
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
            System.exit(-1);
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

    }
}

