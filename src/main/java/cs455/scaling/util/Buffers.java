package cs455.scaling.util;

import java.nio.ByteBuffer;

public class Buffers {
    public ByteBuffer writeBuffer = ByteBuffer.allocate(40);
    public ByteBuffer readBuffer = ByteBuffer.allocate(8196);
}
