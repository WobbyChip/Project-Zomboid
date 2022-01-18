// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.network;

import zombie.GameWindow;
import java.nio.ByteBuffer;

public final class ByteBufferWriter
{
    public ByteBuffer bb;
    
    public ByteBufferWriter(final ByteBuffer bb) {
        this.bb = bb;
    }
    
    public void putBoolean(final boolean b) {
        this.bb.put((byte)(b ? 1 : 0));
    }
    
    public void putByte(final byte b) {
        this.bb.put(b);
    }
    
    public void putChar(final char c) {
        this.bb.putChar(c);
    }
    
    public void putDouble(final double n) {
        this.bb.putDouble(n);
    }
    
    public void putFloat(final float n) {
        this.bb.putFloat(n);
    }
    
    public void putInt(final int n) {
        this.bb.putInt(n);
    }
    
    public void putLong(final long n) {
        this.bb.putLong(n);
    }
    
    public void putShort(final short n) {
        this.bb.putShort(n);
    }
    
    public void putUTF(final String s) {
        GameWindow.WriteStringUTF(this.bb, s);
    }
}
