// 
// Decompiled by Procyon v0.5.36
// 

package zombie.fileSystem;

import java.io.InputStream;

public interface IFile
{
    boolean open(final String p0, final int p1);
    
    void close();
    
    boolean read(final byte[] p0, final long p1);
    
    boolean write(final byte[] p0, final long p1);
    
    byte[] getBuffer();
    
    long size();
    
    boolean seek(final FileSeekMode p0, final long p1);
    
    long pos();
    
    InputStream getInputStream();
    
    IFileDevice getDevice();
    
    void release();
}
