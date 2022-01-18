// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.io;

public interface BitHeaderRead
{
    int getStartPosition();
    
    void read();
    
    boolean hasFlags(final int p0);
    
    boolean hasFlags(final long p0);
    
    boolean equals(final int p0);
    
    boolean equals(final long p0);
    
    int getLen();
    
    void release();
}
