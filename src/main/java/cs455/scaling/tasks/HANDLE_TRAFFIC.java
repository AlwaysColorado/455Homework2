package cs455.scaling.tasks;

import cs455.scaling.server.Server;
import cs455.scaling.util.Hashing;
import cs455.scaling.util.Buffers;

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
        if(key.attachment() == null)
            key.attach(new Buffers());
    }

    @Override
    public void executeTask(){
        //Get the client's SocketChannel
        SocketChannel clientSocket = (SocketChannel) key.channel();
        Buffers buffers;
        if(key.attachment() == null) {
            synchronized (key) {
                try {
                    key.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            synchronized (key){
                key.notifyAll();
            }
        }
        buffers = (Buffers) key.attachment();
        //we have to null-check here, unfortunately. reading the address causes an IOException if the connection
        // has problems, but the Server still needs the address if it fails to read...
        SocketAddress clientAddress = null;
        //read bytes from the channel into the buffer
        int bytesRead = 0;
        try {
            //save the client's address beforehand just in case it bugs out and we need to deregister it.
            clientAddress = clientSocket.getRemoteAddress();
            String hash;
            //read 8kb packets from the channel
            synchronized (buffers.readBuffer) {
                while (buffers.readBuffer.hasRemaining() && bytesRead != -1) {
                    bytesRead = clientSocket.read(buffers.readBuffer);
                }
                hash = hasher.SHA1FromBytes(buffers.readBuffer.array());
                buffers.readBuffer.clear();
            }
            //handle closed socket
            if(bytesRead == -1){
                clientSocket.close();
                parent.deregisterClient(clientAddress);
                return;
            }
            //Take the byte[] from the packet, get the hash.
            // The packet itself is completely worthless, so we don't need to save it.
            //get the hash's bytes, and length in bytes.
            byte[] hashBytes = hash.getBytes();
            int bytesWritten = 0;
            //send it back to the client.
            synchronized (buffers.writeBuffer) {
                buffers.writeBuffer.put(hashBytes);
                buffers.writeBuffer.flip();
                while (buffers.writeBuffer.hasRemaining() && bytesWritten != -1)
                    bytesWritten = clientSocket.write(buffers.writeBuffer);
                buffers.writeBuffer.clear();
            }
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
