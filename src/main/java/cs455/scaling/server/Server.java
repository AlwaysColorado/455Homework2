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

public class Server {

    private  ServerSocketChannel serverSocket;
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
            if ( selector.selectNow() == 0 ) continue;
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while ( iter.hasNext() ) {
                SelectionKey key = iter.next();
                switch (key){
                    case key.isAcceptable(): 
                        registerConnection();
                        break;
                    case key.isReadable():
                        // read previously connected socket for message
                        break;
                    default:
                        System.out.println("Key is not readable or acceptable");
                }
                iter.remove();
                }
    }

    private static void registerConnection() throws IOException{
        clientSocket.accept();
        clientSocket.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("A new connection has registered");
    }


    private static readConnectionMessage(){

    }

    public static void main(String[] args) throws IOException{
        Server server = new Server();
        server.openServerChannel(Integer.parseInt(args[0])); // args 0 for current test will change to 1 most likely with build gradle
    }

}
