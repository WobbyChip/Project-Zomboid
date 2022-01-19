// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import zombie.core.utils.DirectBufferAllocator;
import zombie.asset.AssetPath;
import zombie.core.opengl.RenderThread;
import java.util.Objects;
import zombie.fileSystem.FileSystem;
import zombie.asset.FileTask_LoadImageData;
import zombie.asset.AssetTask;
import zombie.fileSystem.FileTask;
import zombie.asset.AssetTask_RunFileTask;
import zombie.asset.FileTask_LoadPackImage;
import zombie.asset.Asset;
import zombie.asset.AssetManager;

public final class TextureIDAssetManager extends AssetManager
{
    public static final TextureIDAssetManager instance;
    
    @Override
    protected void startLoading(final Asset asset) {
        final TextureID textureID = (TextureID)asset;
        final FileSystem fileSystem = this.getOwner().getFileSystem();
        if (textureID.assetParams != null && textureID.assetParams.subTexture != null) {
            final FileSystem.SubTexture subTexture = textureID.assetParams.subTexture;
            final FileTask_LoadPackImage fileTask_LoadPackImage = new FileTask_LoadPackImage(subTexture.m_pack_name, subTexture.m_page_name, fileSystem, o -> this.onFileTaskFinished(asset, o));
            fileTask_LoadPackImage.setPriority(7);
            final AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask(fileTask_LoadPackImage, asset);
            this.setTask(asset, assetTask_RunFileTask);
            assetTask_RunFileTask.execute();
        }
        else {
            final FileTask_LoadImageData fileTask_LoadImageData = new FileTask_LoadImageData(asset.getPath().getPath(), fileSystem, o2 -> this.onFileTaskFinished(asset, o2));
            fileTask_LoadImageData.setPriority(7);
            final AssetTask_RunFileTask assetTask_RunFileTask2 = new AssetTask_RunFileTask(fileTask_LoadImageData, asset);
            this.setTask(asset, assetTask_RunFileTask2);
            assetTask_RunFileTask2.execute();
        }
    }
    
    @Override
    protected void unloadData(final Asset asset) {
        final TextureID textureID = (TextureID)asset;
        if (textureID.isDestroyed()) {
            return;
        }
        final TextureID obj = textureID;
        Objects.requireNonNull(obj);
        RenderThread.invokeOnRenderContext(obj::destroy);
    }
    
    @Override
    protected Asset createAsset(final AssetPath assetPath, final AssetParams assetParams) {
        return new TextureID(assetPath, this, (TextureID.TextureIDAssetParams)assetParams);
    }
    
    @Override
    protected void destroyAsset(final Asset asset) {
    }
    
    private void onFileTaskFinished(final Asset asset, final Object o) {
        final TextureID textureID = (TextureID)asset;
        if (o instanceof ImageData) {
            textureID.setImageData((ImageData)o);
            this.onLoadingSucceeded(asset);
        }
        else {
            this.onLoadingFailed(asset);
        }
    }
    
    public void waitFileTask() {
        while (DirectBufferAllocator.getBytesAllocated() > 52428800L) {
            try {
                Thread.sleep(20L);
            }
            catch (InterruptedException ex) {}
        }
    }
    
    static {
        instance = new TextureIDAssetManager();
    }
}
