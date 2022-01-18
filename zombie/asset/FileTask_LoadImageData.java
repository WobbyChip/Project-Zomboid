// 
// Decompiled by Procyon v0.5.36
// 

package zombie.asset;

import zombie.core.textures.ImageData;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import zombie.debug.DebugOptions;
import zombie.core.textures.TextureIDAssetManager;
import zombie.fileSystem.IFileTaskCallback;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;

public final class FileTask_LoadImageData extends FileTask
{
    String m_image_name;
    boolean bMask;
    
    public FileTask_LoadImageData(final String image_name, final FileSystem fileSystem, final IFileTaskCallback fileTaskCallback) {
        super(fileSystem, fileTaskCallback);
        this.bMask = false;
        this.m_image_name = image_name;
    }
    
    @Override
    public String getErrorMessage() {
        return this.m_image_name;
    }
    
    @Override
    public void done() {
    }
    
    @Override
    public Object call() throws Exception {
        TextureIDAssetManager.instance.waitFileTask();
        if (DebugOptions.instance.AssetSlowLoad.getValue()) {
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException ex) {}
        }
        final FileInputStream in = new FileInputStream(this.m_image_name);
        try {
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
            try {
                final ImageData imageData = new ImageData(bufferedInputStream, this.bMask);
                bufferedInputStream.close();
                in.close();
                return imageData;
            }
            catch (Throwable t) {
                try {
                    bufferedInputStream.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
                throw t;
            }
        }
        catch (Throwable t2) {
            try {
                in.close();
            }
            catch (Throwable exception2) {
                t2.addSuppressed(exception2);
            }
            throw t2;
        }
    }
}
