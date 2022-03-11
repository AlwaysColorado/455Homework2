package cs455.scaling.tasks;

import java.io.IOException;
import java.net.Socket;
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
    public void executeTask() throws IOException{
        clientSocket.configureBlocking(false);
        clientSocket.register(selector, SelectionKey.OP_READ);
    }
}
