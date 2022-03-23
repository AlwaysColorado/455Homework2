package cs455.scaling.util;

import java.nio.ByteBuffer;

public class Buffers {
    public final ByteBuffer writeBuffer = ByteBuffer.allocate(40);
    public final ByteBuffer readBuffer = ByteBuffer.allocate(8196);
}
