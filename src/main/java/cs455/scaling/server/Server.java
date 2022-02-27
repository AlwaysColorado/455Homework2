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
import cs455.scaling.util.Hashing;
import java.nio.ByteBuffer;
import java.util.List;

public class Server {

    private ServerSocketChannel serverSocket;
    private SocketChannel clientSocket;
    private boolean stillWaiting = true;
    private Selector selector;
    private static final Hashing hashDevice = new Hashing();
    private static List<byte[]> batches;
    private final int batchSize;
    private int threadPoolSize;
    private int batchTime;

    // empty constructor currently
    public Server(int portNum, int bs, int bt, int tps) throws IOException {
        openServerChannel(portNum);
        this.batchSize = bs;
        this.threadPoolSize = tps;
        this.batchTime = bt;
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

    private static void writeConnectionMessage(SelectionKey key, List<byte[]> batches, int batchSize) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.allocate(256); // allocate buffer size
        SocketChannel clientSocketW = (SocketChannel) key.channel(); // get the channel key
        List<byte[]> batch = splitIntoBatches(batches, batchSize);
        for (byte[] bytes : batch) {
            writeBuffer.put(bytes);
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

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Server server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        server.waitForConnections();
    }

}
