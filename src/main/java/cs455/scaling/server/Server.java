package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.Random;
import java.nio.ByteBuffer;

public class Server {

    private ServerSocketChannel serverSocket;
    private SocketChannel clientSocket;
    private boolean stillWaiting = true;
    private Selector selector;

    // empty constructor currently
    public Server(){
        
    }

    private void openServerChannel(int pn) throws IOException{
        selector = Selector.open(); // created once
        serverSocket = ServerSocketChannel.open(); // open channel
        serverSocket.socket().bind( new InetSocketAddress("localhost", pn)); // bind to relevant information
        System.out.println("Server started on port " + pn);
        serverSocket.configureBlocking( false ); // blocking is false
        serverSocket.register(selector, SelectionKey.OP_ACCEPT ); // register selector
    }

    private void waitForConnections() throws IOException{
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


    private static void readConnectionMessage(SelectionKey key) throws IOException{
        ByteBuffer readBuffer = ByteBuffer.allocate(256); // allocate buffer size 
        SocketChannel clientSocketX = (SocketChannel) key.channel(); // get the channel key
        int bytesReadSoFar = clientSocketX.read(readBuffer); // number of bytes read / reading from it 

        switch (bytesReadSoFar){
            case -1:
                clientSocketX.close(); // deals with error and closes the clientSocket 
                System.out.println("Closing clientSocket"); // message
                break;
            default: 
                String message = new String(readBuffer.array());
                // add to batch
                
                // can flip the buffer here and write if needed. I was thinking another method but it might be simple to do here.  
                readBuffer.clear(); // clear buffer 
                break; 
        }
    }

    public static void main(String[] args) throws IOException{
        Server server = new Server();
        server.openServerChannel(Integer.parseInt(args[0])); // args 0 for current test will change to 1 most likely with build gradle
        byte[] test = server.generateRandomByteMessage();
        System.out.println(test); // quick test to check output 
        System.out.println(test.length); // quick test to check length - make sure it is 8k
        server.waitForConnections();
    }

}
