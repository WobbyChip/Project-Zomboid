// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.nio.ByteBuffer;

public final class SliceY
{
    public static final ByteBuffer SliceBuffer;
    public static final Object SliceBufferLock;
    
    static {
        SliceBuffer = ByteBuffer.allocate(10485760);
        SliceBufferLock = "SliceY SliceBufferLock";
    }
}
