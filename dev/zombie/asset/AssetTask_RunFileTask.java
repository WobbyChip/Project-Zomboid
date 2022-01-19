// 
// Decompiled by Procyon v0.5.36
// 

package zombie.asset;

import zombie.fileSystem.FileTask;

public final class AssetTask_RunFileTask extends AssetTask
{
    protected final FileTask m_file_task;
    int m_async_op;
    
    public AssetTask_RunFileTask(final FileTask file_task, final Asset asset) {
        super(asset);
        this.m_async_op = -1;
        this.m_file_task = file_task;
    }
    
    @Override
    public void execute() {
        this.m_async_op = this.m_asset.getAssetManager().getOwner().getFileSystem().runAsync(this.m_file_task);
    }
    
    @Override
    public void cancel() {
        this.m_asset.getAssetManager().getOwner().getFileSystem().cancelAsync(this.m_async_op);
        this.m_async_op = -1;
    }
}
