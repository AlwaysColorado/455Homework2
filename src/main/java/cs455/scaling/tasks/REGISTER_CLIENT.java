package cs455.scaling.tasks;

import cs455.scaling.server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.*;

//Task to register a client
public class REGISTER_CLIENT extends Task{
    //References to the server's selector and the client's socket.
    //private final Selector selector;
    private final SocketChannel clientSocket;
    private final Server parent;
    //private final SelectionKey key;

    public REGISTER_CLIENT(Selector selector, SocketChannel clientSocket, SelectionKey key, Server parent) {
        super(TaskType.REGISTER_CLIENT);
        //this.selector = selector;
        this.clientSocket = clientSocket;
        this.parent = parent;
        //this.key = key;
    }

    @Override
    public void executeTask(){
        try {
            clientSocket.configureBlocking(false);
            clientSocket.register(parent.selector, SelectionKey.OP_READ);
            parent.registerOneClient(clientSocket.getRemoteAddress());
            parent.selector.wakeup();
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
