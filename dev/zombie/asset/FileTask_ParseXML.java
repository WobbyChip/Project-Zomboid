// 
// Decompiled by Procyon v0.5.36
// 

package zombie.asset;

import zombie.util.PZXmlUtil;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.IFileTaskCallback;
import zombie.fileSystem.FileTask;

public final class FileTask_ParseXML extends FileTask
{
    Class<?> m_class;
    String m_filename;
    
    public FileTask_ParseXML(final Class<?> class1, final String filename, final IFileTaskCallback fileTaskCallback, final FileSystem fileSystem) {
        super(fileSystem, fileTaskCallback);
        this.m_class = class1;
        this.m_filename = filename;
    }
    
    @Override
    public String getErrorMessage() {
        return this.m_filename;
    }
    
    @Override
    public void done() {
        this.m_class = null;
        this.m_filename = null;
    }
    
    @Override
    public Object call() throws Exception {
        return PZXmlUtil.parse(this.m_class, this.m_filename);
    }
}
