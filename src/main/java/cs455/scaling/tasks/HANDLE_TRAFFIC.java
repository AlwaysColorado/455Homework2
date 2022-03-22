package cs455.scaling.tasks;

import cs455.scaling.server.Server;
import cs455.scaling.util.Hashing;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class HANDLE_TRAFFIC extends Task{
    //The key this will need to both read from and write to the client.
    public SelectionKey key;
    private final Hashing hasher = new Hashing();
    private final Server parent;

    public HANDLE_TRAFFIC(SelectionKey key, Server parent) {
        super(TaskType.HANDLE_TRAFFIC);
        this.key = key;
        this.parent = parent;
    }

    @Override
    public void executeTask(){
        //First, we need a buffer to read data. The client will be sending a packet that's 8196 bytes.
        // will one key read multiple such messages?
        ByteBuffer readBuffer = ByteBuffer.allocate(8196);
        //Get the client's SocketChannel
        SocketChannel clientSocket = (SocketChannel) key.channel();

        //we have to null-check here, unfortunately. reading the address causes an IOException if the connection
        // has problems, but the Server still needs the address if it fails to read...
        SocketAddress clientAddress = null;
        //read bytes from the channel into the buffer
        int bytesRead = 0;
        try {
            //save the client's address beforehand just in case it bugs out and we need to deregister it.
            clientAddress = clientSocket.getRemoteAddress();
            //read 8kb packets from the channel
            while(readBuffer.hasRemaining() && bytesRead != -1) {
                bytesRead = clientSocket.read(readBuffer);
            }
            //handle closed socket
            if(bytesRead == -1){
                clientSocket.close();
                parent.deregisterClient(clientAddress);
                return;
            }
            //Take the byte[] from the packet, get the hash.
            // The packet itself is completely worthless, so we don't need to save it.
            String hash = hasher.SHA1FromBytes(readBuffer.array());
            //get the hash's bytes, and length in bytes.
            byte[] hashBytes = hash.getBytes();
            //send it back to the client.
            ByteBuffer writeBuffer = ByteBuffer.wrap(hashBytes);
            int bytesWritten = 0;
            while(writeBuffer.hasRemaining() && bytesWritten != -1)
                bytesWritten = clientSocket.write(writeBuffer);
            readBuffer.clear();
            writeBuffer.clear();
            //after we've read everything off the stream, let the server know how many messages we got.
            parent.incrementClientMsgCount(clientAddress);
        } catch (IOException e) {
            //This most likely means either the client died or the read/write failed.
            // if the client isn't in the cloud anymore, discard it from the Hashtable.
            parent.deregisterClient(clientAddress);
            try{
                clientSocket.close();
            } catch (IOException ex) {
                //do nothing, because there's nothing we can do if this fails.
            }
            //e.printStackTrace();
        }
    }
}
