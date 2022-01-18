// 
// Decompiled by Procyon v0.5.36
// 

package zombie.fileSystem;

import java.util.concurrent.Callable;

public abstract class FileTask implements Callable<Object>
{
    protected final FileSystem m_file_system;
    protected final IFileTaskCallback m_cb;
    protected int m_priority;
    
    public FileTask(final FileSystem file_system) {
        this.m_priority = 5;
        this.m_file_system = file_system;
        this.m_cb = null;
    }
    
    public FileTask(final FileSystem file_system, final IFileTaskCallback cb) {
        this.m_priority = 5;
        this.m_file_system = file_system;
        this.m_cb = cb;
    }
    
    public void handleResult(final Object o) {
        if (this.m_cb != null) {
            this.m_cb.onFileTaskFinished(o);
        }
    }
    
    public void setPriority(final int priority) {
        this.m_priority = priority;
    }
    
    public abstract void done();
    
    public String getErrorMessage() {
        return null;
    }
}
