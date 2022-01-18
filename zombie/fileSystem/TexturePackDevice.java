// 
// Decompiled by Procyon v0.5.36
// 

package zombie.fileSystem;

import java.io.FilterInputStream;
import java.io.FileNotFoundException;
import zombie.core.textures.TexturePackPage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import java.io.IOException;
import java.io.InputStream;
import zombie.ZomboidFileSystem;
import java.util.HashMap;
import java.util.ArrayList;

public final class TexturePackDevice implements IFileDevice
{
    String m_name;
    String m_filename;
    final ArrayList<Page> m_pages;
    final HashMap<String, Page> m_pagemap;
    final HashMap<String, SubTexture> m_submap;
    int m_textureFlags;
    
    public TexturePackDevice(final String name, final int textureFlags) {
        this.m_pages = new ArrayList<Page>();
        this.m_pagemap = new HashMap<String, Page>();
        this.m_submap = new HashMap<String, SubTexture>();
        this.m_name = name;
        this.m_filename = ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
        this.m_textureFlags = textureFlags;
    }
    
    @Override
    public IFile createFile(final IFile file) {
        return null;
    }
    
    @Override
    public void destroyFile(final IFile file) {
    }
    
    @Override
    public InputStream createStream(final String s, final InputStream inputStream) throws IOException {
        this.initMetaData();
        return new TexturePackInputStream(s, this);
    }
    
    @Override
    public void destroyStream(final InputStream inputStream) {
        if (inputStream instanceof TexturePackInputStream) {}
    }
    
    @Override
    public String name() {
        return this.m_name;
    }
    
    public void getSubTextureInfo(final FileSystem.TexturePackTextures texturePackTextures) throws IOException {
        this.initMetaData();
        for (final SubTexture subTexture : this.m_submap.values()) {
            texturePackTextures.put(subTexture.m_info.name, new FileSystem.SubTexture(this.name(), subTexture.m_page.m_name, subTexture.m_info));
        }
    }
    
    private void initMetaData() throws IOException {
        if (!this.m_pages.isEmpty()) {
            return;
        }
        final FileInputStream in = new FileInputStream(this.m_filename);
        try {
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
            try {
                final PositionInputStream positionInputStream = new PositionInputStream(bufferedInputStream);
                try {
                    for (int int1 = TexturePackPage.readInt(positionInputStream), i = 0; i < int1; ++i) {
                        final Page page = this.readPage(positionInputStream);
                        this.m_pages.add(page);
                        this.m_pagemap.put(page.m_name, page);
                        for (final TexturePackPage.SubTextureInfo subTextureInfo : page.m_sub) {
                            this.m_submap.put(subTextureInfo.name, new SubTexture(page, subTextureInfo));
                        }
                    }
                    positionInputStream.close();
                }
                catch (Throwable t) {
                    try {
                        positionInputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                bufferedInputStream.close();
            }
            catch (Throwable t2) {
                try {
                    bufferedInputStream.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
            in.close();
        }
        catch (Throwable t3) {
            try {
                in.close();
            }
            catch (Throwable exception3) {
                t3.addSuppressed(exception3);
            }
            throw t3;
        }
    }
    
    private Page readPage(final PositionInputStream positionInputStream) throws IOException {
        final Page page = new Page();
        final String readString = TexturePackPage.ReadString(positionInputStream);
        final int int1 = TexturePackPage.readInt(positionInputStream);
        final boolean has_alpha = TexturePackPage.readInt(positionInputStream) != 0;
        page.m_name = readString;
        page.m_has_alpha = has_alpha;
        for (int i = 0; i < int1; ++i) {
            page.m_sub.add(new TexturePackPage.SubTextureInfo(TexturePackPage.readInt(positionInputStream), TexturePackPage.readInt(positionInputStream), TexturePackPage.readInt(positionInputStream), TexturePackPage.readInt(positionInputStream), TexturePackPage.readInt(positionInputStream), TexturePackPage.readInt(positionInputStream), TexturePackPage.readInt(positionInputStream), TexturePackPage.readInt(positionInputStream), TexturePackPage.ReadString(positionInputStream)));
        }
        page.m_png_start = positionInputStream.getPosition();
        while (TexturePackPage.readIntByte(positionInputStream) != -559038737) {}
        return page;
    }
    
    public boolean isAlpha(final String key) {
        return this.m_pagemap.get(key).m_has_alpha;
    }
    
    public int getTextureFlags() {
        return this.m_textureFlags;
    }
    
    static final class Page
    {
        String m_name;
        boolean m_has_alpha;
        long m_png_start;
        final ArrayList<TexturePackPage.SubTextureInfo> m_sub;
        
        Page() {
            this.m_has_alpha = false;
            this.m_png_start = -1L;
            this.m_sub = new ArrayList<TexturePackPage.SubTextureInfo>();
        }
    }
    
    static final class SubTexture
    {
        final Page m_page;
        final TexturePackPage.SubTextureInfo m_info;
        
        SubTexture(final Page page, final TexturePackPage.SubTextureInfo info) {
            this.m_page = page;
            this.m_info = info;
        }
    }
    
    static class TexturePackInputStream extends FileInputStream
    {
        TexturePackDevice m_device;
        
        TexturePackInputStream(final String key, final TexturePackDevice device) throws IOException {
            super(device.m_filename);
            this.m_device = device;
            final Page page = this.m_device.m_pagemap.get(key);
            if (page == null) {
                throw new FileNotFoundException();
            }
            this.skip(page.m_png_start);
        }
    }
    
    public final class PositionInputStream extends FilterInputStream
    {
        private long pos;
        private long mark;
        
        public PositionInputStream(final InputStream in) {
            super(in);
            this.pos = 0L;
            this.mark = 0L;
        }
        
        public synchronized long getPosition() {
            return this.pos;
        }
        
        @Override
        public synchronized int read() throws IOException {
            final int read = super.read();
            if (read >= 0) {
                ++this.pos;
            }
            return read;
        }
        
        @Override
        public synchronized int read(final byte[] b, final int off, final int len) throws IOException {
            final int read = super.read(b, off, len);
            if (read > 0) {
                this.pos += read;
            }
            return read;
        }
        
        @Override
        public synchronized long skip(final long n) throws IOException {
            final long skip = super.skip(n);
            if (skip > 0L) {
                this.pos += skip;
            }
            return skip;
        }
        
        @Override
        public synchronized void mark(final int readlimit) {
            super.mark(readlimit);
            this.mark = this.pos;
        }
        
        @Override
        public synchronized void reset() throws IOException {
            if (!this.markSupported()) {
                throw new IOException("Mark not supported.");
            }
            super.reset();
            this.pos = this.mark;
        }
    }
}
