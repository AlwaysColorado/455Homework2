package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.*;
import java.util.*;

import cs455.scaling.tasks.HANDLE_TRAFFIC;
import cs455.scaling.tasks.REGISTER_CLIENT;
import cs455.scaling.threadpool.ThreadPoolManager;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

    private ServerSocketChannel serverSocket;
    private AtomicBoolean stillWaiting = new AtomicBoolean(true);
    private Selector selector;
    private final int portNum;
    private final Hashtable<SocketAddress, Integer> clientStatistics;
    Timer timer;
    private final ThreadPoolManager threadPoolManager;

    public Server(int pn, int bs, int bt, int tps) {
        this.portNum = pn;
        this.clientStatistics = new Hashtable<>();
        this.threadPoolManager = new ThreadPoolManager(tps, bs, bt);
        threadPoolManager.start();
    }

    private void openServerChannel() {
        try {
            selector = Selector.open(); // created once
            serverSocket = ServerSocketChannel.open(); // open channel
            serverSocket.socket().bind(new InetSocketAddress("localhost", portNum)); // bind to relevant information
            System.out.println("Server started on port " + portNum);
            serverSocket.configureBlocking(false); // blocking is false
            serverSocket.register(selector, SelectionKey.OP_ACCEPT); // register selector
        } catch (ClosedChannelException e) {
            System.out.println("Server channel closed unexpectedly during config. Exiting.");
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.out.printf("Server channel configured in bad state: %s. Exiting.", e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    // Start the timer to print stats every 20 seconds
    private void startStatsTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new ServerStatistics(this), 0, 20000);
    }

    private void waitForConnections() {
        try {
            while (stillWaiting.get()) {
                System.out.println("Waiting for connections");
                selector.select();
                if (selector.selectNow() == 0) continue;
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isAcceptable()) {
                        this.threadPoolManager.addTask(new REGISTER_CLIENT(selector, (SocketChannel) key.channel(), this));
                    } else if (key.isReadable()) {
                        this.threadPoolManager.addTask(new HANDLE_TRAFFIC(key, this));
                    } else {
                        System.out.println("Key is not readable or acceptable");
                    }
                }
                iter.remove();
            }
        } catch (IOException e) {
            //Not sure if an IOException in this loop leaves the program in a bad state.
            // Leaving as just a stack trace for now.
            e.printStackTrace();
        } catch (ClosedSelectorException e){
            //If the selector gets closed, the program's over.
            System.out.println("Selector closed unexpectedly, exiting.");
            System.exit(-1);
        }
    }

    //depreciated
    /*
    private void registerConnection() throws IOException{
        clientSocket = serverSocket.accept();
        clientSocket.configureBlocking(false);
        clientSocket.register(selector, SelectionKey.OP_READ);
        System.out.println("A new connection has registered");
    }*/

    //depreciated
    /*
    private static void readConnectionMessage(SelectionKey key) throws IOException {
        ByteBuffer readBuffer = ByteBuffer.allocate(256); // allocate buffer size 
        SocketChannel clientSocketR = (SocketChannel) key.channel(); // get the channel key
        int bytesReadSoFar = clientSocketR.read(readBuffer); // number of bytes read / reading from it

        if (bytesReadSoFar == -1) {
            clientSocketR.close(); // deals with error and closes the clientSocket
            System.out.println("Closing clientSocket"); // message
        } else {
            String message = new String(readBuffer.array());
            byte[] packet = message.getBytes();
            // add hashed_bytes to batches
            batches = new ArrayList<>(1);
            batches.add(packet);
            readBuffer.clear();
        }
    }*/

    //depreciated
    /*
    private static void writeConnectionMessage(SelectionKey key, List<byte[]> batches, int batchSize) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.allocate(256); // allocate buffer size
        SocketChannel clientSocketW = (SocketChannel) key.channel(); // get the channel key
        List<byte[]> batch = splitIntoBatches(batches, batchSize);
        for (int i=0; i<batchSize; i++) {
            String hashed_message = hashDevice.SHA1FromBytes(batch.get(i));
            byte[] hashed_bytes = hashed_message.getBytes();
            writeBuffer.put(hashed_bytes);
            clientSocketW.write(writeBuffer);
            writeBuffer.clear();
        }
    }*/

    //depreciated
    /*
    private static List<byte[]> splitIntoBatches(List<byte[]> batches, int batchSize){
        List<byte[]> batch = new ArrayList<>(batchSize);
        if (batches.size() == batchSize){
            for (int i=0; i<batchSize; i++){
                batch.set(i, batches.get(i));
            }
        }
        return batch;
    }*/

    public synchronized void incrementClientMsgCount(SocketAddress clientAddress, int msgCount) {
        //update the client's message count with supplied
        clientStatistics.put(clientAddress, clientStatistics.get(clientAddress) + msgCount);
    }

    public void registerOneClient(SocketAddress clientAddress) {
        synchronized (clientStatistics) {
            clientStatistics.put(clientAddress, 0);
        }
    }

    public void deregisterClient(SocketAddress clientAddress) {
        //if the clientAddress is null, the socket closed without a worker thread being able to read the address.
        // don't do anything. (I don't really know how to get around this problem...)
        if(clientAddress == null)
            return;
        synchronized (clientStatistics){
            clientStatistics.remove(clientAddress);
        }
    }

    public synchronized Hashtable<SocketAddress, Integer> getClientStatistics(){
        Hashtable<SocketAddress, Integer> cStats = (Hashtable<SocketAddress, Integer>) clientStatistics.clone(); // Shallow copy
        clientStatistics.replaceAll((key, value) -> 0);  // This *SHOULD* replace all values with zero??
        return cStats;
    }

    public void runServer() {
        //Selector Init
        openServerChannel();
        //Start stats timer
        startStatsTimer();
        //Start listening for clients.
        waitForConnections();
    }

    public void killServer(){
        stillWaiting.set(false);
    }


    public static void main(String[] args) {
        int pn = 0, bs = 0, bt = 0, tps = 0;
        if(args.length != 4){
            System.out.println("Server expecting arg format: <port number> <batch size> " +
                    "<batch time> <thread pool size>");
            System.exit(-1);
        }
        try {
            pn = Integer.parseInt(args[0]);
            bs = Integer.parseInt(args[1]);
            bt = Integer.parseInt(args[2]);
            tps = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.out.println("Couldn't parse an arg as an Integer. Expected Format: <port number> <batch size> " +
                    "<batch time> <thread pool size>");
            System.exit(-1);
        }
        Server server = new Server(pn, bs, bt, tps);
        server.runServer();
    }




}
