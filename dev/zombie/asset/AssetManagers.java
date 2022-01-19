// 
// Decompiled by Procyon v0.5.36
// 

package zombie.asset;

import gnu.trove.map.hash.TLongObjectHashMap;
import zombie.fileSystem.FileSystem;

public final class AssetManagers
{
    private final AssetManagerTable m_managers;
    private final FileSystem m_file_system;
    
    public AssetManagers(final FileSystem file_system) {
        this.m_managers = new AssetManagerTable();
        this.m_file_system = file_system;
    }
    
    public AssetManager get(final AssetType assetType) {
        return (AssetManager)this.m_managers.get(assetType.type);
    }
    
    public void add(final AssetType assetType, final AssetManager assetManager) {
        this.m_managers.put(assetType.type, (Object)assetManager);
    }
    
    public FileSystem getFileSystem() {
        return this.m_file_system;
    }
    
    public static final class AssetManagerTable extends TLongObjectHashMap<AssetManager>
    {
    }
}
