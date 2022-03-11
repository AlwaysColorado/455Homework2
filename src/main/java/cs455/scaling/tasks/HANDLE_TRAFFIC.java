package cs455.scaling.tasks;

import cs455.scaling.util.Hashing;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;

public class HANDLE_TRAFFIC extends Task{
    //The key this will need to both read from and write to the client.
    public SelectionKey key;
    private final Hashing hasher = new Hashing();

    public HANDLE_TRAFFIC(SelectionKey key) {
        super(TaskType.HANDLE_TRAFFIC);
        this.key = key;
    }

    @Override
    public void executeTask() throws IOException {
        //First, we need a buffer to read data. The client will be sending a packet that's 8196 bytes.
        // will one key read multiple such messages?
        ByteBuffer readBuffer = ByteBuffer.allocate(8196);
        //Get the client's SocketChannel
        SocketChannel clientSocket = (SocketChannel) key.channel();
        //read bytes from the channel into the buffer
        int bytesRead = clientSocket.read(readBuffer);
        //if return is -1, close connection
        if(bytesRead == -1)
            clientSocket.close();
        else{
            //Take the byte[] from the packet, get the hash.
            // The packet itself is completely worthless, so we don't need to save it.
            try {
                String hash = hasher.SHA1FromBytes(readBuffer.array());
                //get the hash's bytes, and length in bytes.
                byte[] hashBytes = hash.getBytes();
                int messageLength = hashBytes.length;
                //send it back to the client.
                ByteBuffer writeBuffer = ByteBuffer.allocate(messageLength);
                writeBuffer.put(hashBytes);
                clientSocket.write(writeBuffer);
                writeBuffer.clear();
                //can we please handle this exception this in Hashing?
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }
}
