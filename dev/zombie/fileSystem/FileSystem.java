// 
// Decompiled by Procyon v0.5.36
// 

package zombie.fileSystem;

import java.util.HashMap;
import zombie.core.textures.TexturePackPage;
import java.io.IOException;
import java.io.InputStream;

public abstract class FileSystem
{
    public static final int INVALID_ASYNC = -1;
    
    public abstract boolean mount(final IFileDevice p0);
    
    public abstract boolean unMount(final IFileDevice p0);
    
    public abstract IFile open(final DeviceList p0, final String p1, final int p2);
    
    public abstract void close(final IFile p0);
    
    public abstract int openAsync(final DeviceList p0, final String p1, final int p2, final IFileTask2Callback p3);
    
    public abstract void closeAsync(final IFile p0, final IFileTask2Callback p1);
    
    public abstract void cancelAsync(final int p0);
    
    public abstract InputStream openStream(final DeviceList p0, final String p1) throws IOException;
    
    public abstract void closeStream(final InputStream p0);
    
    public abstract int runAsync(final FileTask p0);
    
    public abstract void updateAsyncTransactions();
    
    public abstract boolean hasWork();
    
    public abstract DeviceList getDefaultDevice();
    
    public abstract void mountTexturePack(final String p0, final TexturePackTextures p1, final int p2);
    
    public abstract DeviceList getTexturePackDevice(final String p0);
    
    public abstract int getTexturePackFlags(final String p0);
    
    public abstract boolean getTexturePackAlpha(final String p0, final String p1);
    
    public static final class SubTexture
    {
        public String m_pack_name;
        public String m_page_name;
        public TexturePackPage.SubTextureInfo m_info;
        
        public SubTexture(final String pack_name, final String page_name, final TexturePackPage.SubTextureInfo info) {
            this.m_pack_name = pack_name;
            this.m_page_name = page_name;
            this.m_info = info;
        }
    }
    
    public static final class TexturePackTextures extends HashMap<String, SubTexture>
    {
    }
}
