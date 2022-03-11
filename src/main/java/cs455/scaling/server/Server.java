package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;

import cs455.scaling.threadpool.ThreadPool;
import cs455.scaling.threadpool.ThreadPoolManager;
import cs455.scaling.util.Hashing;
import java.nio.ByteBuffer;
import java.util.List;

public class Server implements Runnable {

    private ServerSocketChannel serverSocket;
    private SocketChannel clientSocket;
    private boolean stillWaiting = true;
    private boolean stillRunning = true;
    private Selector selector;
    private static final Hashing hashDevice = new Hashing();
    private static List<byte[]> batches;
    private final int batchSize;
//    private final int threadPoolSize;
//    private final int batchTime;
    private final int portNum;
    private final ThreadPoolManager threadPoolManager;

    // empty constructor currently
    public Server(int pn, int bs, int bt, int tps) throws IOException {
        this.portNum = pn;
        this.batchSize = bs;
//        this.threadPoolSize = tps;
//        this.batchTime = bt;
        this.threadPoolManager = new ThreadPoolManager(tps, bs, bt);
        threadPoolManager.start();
    }

    private void openServerChannel(int pn) throws IOException{
        selector = Selector.open(); // created once
        serverSocket = ServerSocketChannel.open(); // open channel
        serverSocket.socket().bind( new InetSocketAddress("localhost", pn)); // bind to relevant information
        System.out.println("Server started on port " + pn);
        serverSocket.configureBlocking( false ); // blocking is false
        serverSocket.register(selector, SelectionKey.OP_ACCEPT ); // register selector
    }

    private void waitForConnections() throws IOException, NoSuchAlgorithmException {
        while(stillWaiting){
            System.out.println("Waiting for connections");
            selector.select();
            if ( selector.selectNow() == 0 ) continue;
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while ( iter.hasNext() ) {
                    SelectionKey key = iter.next();
                    if (key.isAcceptable()){
                        registerConnection();
                    }
                    else if (key.isReadable()){
                        readConnectionMessage(key);
                    }
                    else if(key.isWritable()){
                        writeConnectionMessage(key, batches, batchSize);
                    }
                    else{                       
                        System.out.println("Key is not readable or acceptable");
                    }
                }
                    iter.remove();
            }
        }
    

    private void registerConnection() throws IOException{
        clientSocket = serverSocket.accept();
        clientSocket.configureBlocking(false);
        clientSocket.register(selector, SelectionKey.OP_READ);
        System.out.println("A new connection has registered");
    }


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
    }

    private static void writeConnectionMessage(SelectionKey key, List<byte[]> batches, int batchSize) throws IOException, NoSuchAlgorithmException {
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
    }

    private static List<byte[]> splitIntoBatches(List<byte[]> batches, int batchSize){
        List<byte[]> batch = new ArrayList<>(batchSize);
        if (batches.size() == batchSize){
            for (int i=0; i<batchSize; i++){
                batch.set(i, batches.get(i));
            }
        }
        return batch;
    }

    @Override
    public void run() {
        try {
            waitForConnections();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {
        int pn = Integer.parseInt(args[0]);
        int bs = Integer.parseInt(args[1]);
        int bt = Integer.parseInt(args[2]);
        int tps = Integer.parseInt(args[3]);
        Server server = new Server(pn, bs, bt, tps);
        server.openServerChannel(pn);
        ThreadPool tp = new ThreadPool(tps);
        while(server.stillRunning){
            //refactor 
            //tp.executeThreadPool(server);
            if (!server.stillRunning){
                tp.killThreads();
            }
        }

    }


}
