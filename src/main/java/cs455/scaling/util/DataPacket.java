package cs455.scaling.util;

import java.nio.channels.SocketChannel;

//for storing information about a client's data packet
public class DataPacket {
    //Information about where to send the response
    public SocketChannel client;
    //The raw data the client sent
    public byte[] packet;
    //The hash, computed in BATCH_DATA?
    public String hash;

    public DataPacket(SocketChannel client, byte[] packet){
        this.client = client;
        this.packet = packet;
    }
}
