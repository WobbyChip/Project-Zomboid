// 
// Decompiled by Procyon v0.5.36
// 

package zombie.asset;

import java.io.File;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.IFileTaskCallback;
import zombie.fileSystem.FileTask;

public final class FileTask_Exists extends FileTask
{
    String fileName;
    
    public FileTask_Exists(final String fileName, final IFileTaskCallback fileTaskCallback, final FileSystem fileSystem) {
        super(fileSystem, fileTaskCallback);
        this.fileName = fileName;
    }
    
    @Override
    public void done() {
    }
    
    @Override
    public Object call() throws Exception {
        return new File(this.fileName).exists();
    }
}
