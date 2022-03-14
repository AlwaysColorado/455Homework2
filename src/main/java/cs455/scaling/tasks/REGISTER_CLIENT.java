package cs455.scaling.tasks;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

//Task to register a client
public class REGISTER_CLIENT extends Task{
    //References to the server's selector and the client's socket.
    public Selector selector;
    public SocketChannel clientSocket;

    public REGISTER_CLIENT(Selector selector, SocketChannel clientSocket) {
        super(TaskType.REGISTER_CLIENT);
        this.selector = selector;
        this.clientSocket = clientSocket;
    }

    @Override
    public void executeTask(){
        try {
            clientSocket.configureBlocking(false);
            clientSocket.register(selector, SelectionKey.OP_READ);
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
