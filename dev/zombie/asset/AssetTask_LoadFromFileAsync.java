// 
// Decompiled by Procyon v0.5.36
// 

package zombie.asset;

import zombie.fileSystem.IFile;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.IFileTask2Callback;

final class AssetTask_LoadFromFileAsync extends AssetTask implements IFileTask2Callback
{
    int m_async_op;
    boolean bStream;
    
    AssetTask_LoadFromFileAsync(final Asset asset, final boolean bStream) {
        super(asset);
        this.m_async_op = -1;
        this.bStream = bStream;
    }
    
    @Override
    public void execute() {
        final FileSystem fileSystem = this.m_asset.getAssetManager().getOwner().getFileSystem();
        this.m_async_op = fileSystem.openAsync(fileSystem.getDefaultDevice(), this.m_asset.getPath().m_path, 0x4 | (this.bStream ? 16 : 1), this);
    }
    
    @Override
    public void cancel() {
        this.m_asset.getAssetManager().getOwner().getFileSystem().cancelAsync(this.m_async_op);
        this.m_async_op = -1;
    }
    
    @Override
    public void onFileTaskFinished(final IFile file, final Object o) {
        this.m_async_op = -1;
        if (this.m_asset.m_priv.m_desired_state != Asset.State.READY) {
            return;
        }
        if (o != Boolean.TRUE) {
            this.m_asset.m_priv.onLoadingFailed();
            return;
        }
        if (!this.m_asset.getAssetManager().loadDataFromFile(this.m_asset, file)) {
            this.m_asset.m_priv.onLoadingFailed();
            return;
        }
        this.m_asset.m_priv.onLoadingSucceeded();
    }
}
