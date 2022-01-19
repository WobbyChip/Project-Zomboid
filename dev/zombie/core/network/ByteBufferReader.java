// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.network;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public final class ByteBufferReader
{
    public ByteBuffer bb;
    
    public ByteBufferReader(final ByteBuffer bb) {
        this.bb = bb;
    }
    
    public boolean getBoolean() {
        return this.bb.get() != 0;
    }
    
    public byte getByte() {
        return this.bb.get();
    }
    
    public char getChar() {
        return this.bb.getChar();
    }
    
    public double getDouble() {
        return this.bb.getDouble();
    }
    
    public float getFloat() {
        return this.bb.getFloat();
    }
    
    public int getInt() {
        return this.bb.getInt();
    }
    
    public long getLong() {
        return this.bb.getLong();
    }
    
    public short getShort() {
        return this.bb.getShort();
    }
    
    public String getUTF() {
        final byte[] array = new byte[this.bb.getShort()];
        this.bb.get(array);
        try {
            return new String(array, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Bad encoding!");
        }
    }
}
