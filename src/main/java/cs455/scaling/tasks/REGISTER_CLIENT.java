package cs455.scaling.tasks;

import cs455.scaling.server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.*;

//Task to register a client
public class REGISTER_CLIENT extends Task{
    //References to the server's selector and the client's socket.
    public Selector selector;
    public ServerSocketChannel serverSocket;
    private final Server parent;

    public REGISTER_CLIENT(Selector selector, ServerSocketChannel serverSocket, Server parent) {
        super(TaskType.REGISTER_CLIENT);
        this.selector = selector;
        this.serverSocket = serverSocket;
        this.parent = parent;
        System.out.println("register created.");
    }

    @Override
    public void executeTask(){
        System.out.println("register executed.");
        try {
            SocketChannel clientSocket = serverSocket.accept();
            clientSocket.configureBlocking(false);
            clientSocket.register(selector, SelectionKey.OP_READ);
            System.out.println("registered client to selector.");
            parent.registerOneClient(clientSocket.getLocalAddress());
        } catch (ClosedChannelException e) {
            //if the client socket is closed, the server should move on. Do nothing.
            //e.printStackTrace();
        } catch (IOException e) {
            //if and IOException occurs, the most likely cause is the socket closing.
            // The server should move on. Do nothing.
            //e.printStackTrace();
        }
    }
}
