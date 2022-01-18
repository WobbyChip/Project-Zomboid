// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.fileSystem.IFileTaskCallback;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;

public final class FileTask_LoadWorldMapBinary extends FileTask
{
    WorldMapData m_worldMapData;
    String m_filename;
    
    public FileTask_LoadWorldMapBinary(final WorldMapData worldMapData, final String filename, final FileSystem fileSystem, final IFileTaskCallback fileTaskCallback) {
        super(fileSystem, fileTaskCallback);
        this.m_worldMapData = worldMapData;
        this.m_filename = filename;
    }
    
    @Override
    public String getErrorMessage() {
        return this.m_filename;
    }
    
    @Override
    public void done() {
        this.m_worldMapData = null;
        this.m_filename = null;
    }
    
    @Override
    public Object call() throws Exception {
        return new WorldMapBinary().read(this.m_filename, this.m_worldMapData) ? Boolean.TRUE : Boolean.FALSE;
    }
}
