// 
// Decompiled by Procyon v0.5.36
// 

package zombie.fileSystem;

import java.io.IOException;
import java.io.InputStream;

public interface IFileDevice
{
    IFile createFile(final IFile p0);
    
    void destroyFile(final IFile p0);
    
    InputStream createStream(final String p0, final InputStream p1) throws IOException;
    
    void destroyStream(final InputStream p0);
    
    String name();
}
