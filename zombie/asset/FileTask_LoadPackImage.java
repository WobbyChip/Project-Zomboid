// 
// Decompiled by Procyon v0.5.36
// 

package zombie.asset;

import java.io.InputStream;
import zombie.core.textures.ImageData;
import zombie.core.textures.TextureIDAssetManager;
import zombie.fileSystem.IFileTaskCallback;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;

public final class FileTask_LoadPackImage extends FileTask
{
    String m_pack_name;
    String m_image_name;
    boolean bMask;
    int m_flags;
    
    public FileTask_LoadPackImage(final String pack_name, final String image_name, final FileSystem fileSystem, final IFileTaskCallback fileTaskCallback) {
        super(fileSystem, fileTaskCallback);
        this.m_pack_name = pack_name;
        this.m_image_name = image_name;
        this.bMask = fileSystem.getTexturePackAlpha(pack_name, image_name);
        this.m_flags = fileSystem.getTexturePackFlags(pack_name);
    }
    
    @Override
    public void done() {
    }
    
    @Override
    public Object call() throws Exception {
        TextureIDAssetManager.instance.waitFileTask();
        final InputStream openStream = this.m_file_system.openStream(this.m_file_system.getTexturePackDevice(this.m_pack_name), this.m_image_name);
        try {
            final ImageData imageData = new ImageData(openStream, this.bMask);
            if ((this.m_flags & 0x40) != 0x0) {
                imageData.initMipMaps();
            }
            final ImageData imageData2 = imageData;
            if (openStream != null) {
                openStream.close();
            }
            return imageData2;
        }
        catch (Throwable t) {
            if (openStream != null) {
                try {
                    openStream.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
            }
            throw t;
        }
    }
}
