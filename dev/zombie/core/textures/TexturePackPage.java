// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.util.Stack;
import java.util.HashMap;

public final class TexturePackPage
{
    public static HashMap<String, Stack<String>> FoundTextures;
    public static final HashMap<String, Texture> subTextureMap;
    public static final HashMap<String, Texture> subTextureMap2;
    public static final HashMap<String, TexturePackPage> texturePackPageMap;
    public static final HashMap<String, String> TexturePackPageNameMap;
    public final HashMap<String, Texture> subTextures;
    public Texture tex;
    static ByteBuffer SliceBuffer;
    static boolean bHasCache;
    static int percent;
    public static int chl1;
    public static int chl2;
    public static int chl3;
    public static int chl4;
    static StringBuilder v;
    public static ArrayList<SubTextureInfo> TempSubTextureInfo;
    public static ArrayList<String> tempFilenameCheck;
    public static boolean bIgnoreWorldItemTextures;
    
    public TexturePackPage() {
        this.subTextures = new HashMap<String, Texture>();
        this.tex = null;
    }
    
    public static void LoadDir(final String s) throws URISyntaxException {
    }
    
    public static void searchFolders(final File file) {
    }
    
    public static Texture getTexture(final String s) {
        if (s.contains(".png")) {
            return Texture.getSharedTexture(s);
        }
        if (TexturePackPage.subTextureMap.containsKey(s)) {
            return TexturePackPage.subTextureMap.get(s);
        }
        return null;
    }
    
    public static int readInt(final InputStream inputStream) throws EOFException, IOException {
        final int read = inputStream.read();
        final int read2 = inputStream.read();
        final int read3 = inputStream.read();
        final int read4 = inputStream.read();
        TexturePackPage.chl1 = read;
        TexturePackPage.chl2 = read2;
        TexturePackPage.chl3 = read3;
        TexturePackPage.chl4 = read4;
        if ((read | read2 | read3 | read4) < 0) {
            throw new EOFException();
        }
        return (read << 0) + (read2 << 8) + (read3 << 16) + (read4 << 24);
    }
    
    public static int readInt(final ByteBuffer byteBuffer) throws EOFException, IOException {
        final byte value = byteBuffer.get();
        final byte value2 = byteBuffer.get();
        final byte value3 = byteBuffer.get();
        final byte value4 = byteBuffer.get();
        TexturePackPage.chl1 = value;
        TexturePackPage.chl2 = value2;
        TexturePackPage.chl3 = value3;
        return (value << 0) + (value2 << 8) + (value3 << 16) + ((TexturePackPage.chl4 = value4) << 24);
    }
    
    public static int readIntByte(final InputStream inputStream) throws EOFException, IOException {
        final int chl2 = TexturePackPage.chl2;
        final int chl3 = TexturePackPage.chl3;
        final int chl4 = TexturePackPage.chl4;
        final int read = inputStream.read();
        TexturePackPage.chl1 = chl2;
        TexturePackPage.chl2 = chl3;
        TexturePackPage.chl3 = chl4;
        TexturePackPage.chl4 = read;
        if ((chl2 | chl3 | chl4 | read) < 0) {
            throw new EOFException();
        }
        return (chl2 << 0) + (chl3 << 8) + (chl4 << 16) + (read << 24);
    }
    
    public static String ReadString(final InputStream inputStream) throws IOException {
        TexturePackPage.v.setLength(0);
        for (int int1 = readInt(inputStream), i = 0; i < int1; ++i) {
            TexturePackPage.v.append((char)inputStream.read());
        }
        return TexturePackPage.v.toString();
    }
    
    public void loadFromPackFileDDS(final BufferedInputStream bufferedInputStream) throws IOException {
        final String readString = ReadString(bufferedInputStream);
        TexturePackPage.tempFilenameCheck.add(readString);
        final int int1 = readInt(bufferedInputStream);
        final boolean b = readInt(bufferedInputStream) != 0;
        TexturePackPage.TempSubTextureInfo.clear();
        for (int i = 0; i < int1; ++i) {
            final String readString2 = ReadString(bufferedInputStream);
            final int int2 = readInt(bufferedInputStream);
            final int int3 = readInt(bufferedInputStream);
            final int int4 = readInt(bufferedInputStream);
            final int int5 = readInt(bufferedInputStream);
            final int int6 = readInt(bufferedInputStream);
            final int int7 = readInt(bufferedInputStream);
            final int int8 = readInt(bufferedInputStream);
            final int int9 = readInt(bufferedInputStream);
            if (readString2.contains("ZombieWalk") && readString2.contains("BobZ_")) {
                TexturePackPage.TempSubTextureInfo.add(new SubTextureInfo(int2, int3, int4, int5, int6, int7, int8, int9, readString2));
            }
        }
        if (TexturePackPage.TempSubTextureInfo.isEmpty()) {
            while (readIntByte(bufferedInputStream) != -559038737) {}
            return;
        }
        final Texture texture = new Texture(readString, bufferedInputStream, b, Texture.PZFileformat.DDS);
        for (int j = 0; j < TexturePackPage.TempSubTextureInfo.size(); ++j) {
            final SubTextureInfo subTextureInfo = TexturePackPage.TempSubTextureInfo.get(j);
            final Texture split = texture.split(subTextureInfo.x, subTextureInfo.y, subTextureInfo.w, subTextureInfo.h);
            split.copyMaskRegion(texture, subTextureInfo.x, subTextureInfo.y, subTextureInfo.w, subTextureInfo.h);
            split.setName(subTextureInfo.name);
            this.subTextures.put(subTextureInfo.name, split);
            TexturePackPage.subTextureMap.put(subTextureInfo.name, split);
            split.offsetX = (float)subTextureInfo.ox;
            split.offsetY = (float)subTextureInfo.oy;
            split.widthOrig = subTextureInfo.fx;
            split.heightOrig = subTextureInfo.fy;
        }
        texture.mask = null;
        TexturePackPage.texturePackPageMap.put(readString, this);
        while (readIntByte(bufferedInputStream) != -559038737) {}
    }
    
    public void loadFromPackFile(final BufferedInputStream bufferedInputStream) throws Exception {
        final String readString = ReadString(bufferedInputStream);
        TexturePackPage.tempFilenameCheck.add(readString);
        final int int1 = readInt(bufferedInputStream);
        final boolean b = readInt(bufferedInputStream) != 0;
        if (b) {}
        TexturePackPage.TempSubTextureInfo.clear();
        for (int i = 0; i < int1; ++i) {
            final String readString2 = ReadString(bufferedInputStream);
            final int int2 = readInt(bufferedInputStream);
            final int int3 = readInt(bufferedInputStream);
            final int int4 = readInt(bufferedInputStream);
            final int int5 = readInt(bufferedInputStream);
            final int int6 = readInt(bufferedInputStream);
            final int int7 = readInt(bufferedInputStream);
            final int int8 = readInt(bufferedInputStream);
            final int int9 = readInt(bufferedInputStream);
            if (!TexturePackPage.bIgnoreWorldItemTextures || !readString2.startsWith("WItem_")) {
                TexturePackPage.TempSubTextureInfo.add(new SubTextureInfo(int2, int3, int4, int5, int6, int7, int8, int9, readString2));
            }
        }
        final Texture texture = new Texture(readString, bufferedInputStream, b);
        for (int j = 0; j < TexturePackPage.TempSubTextureInfo.size(); ++j) {
            final SubTextureInfo subTextureInfo = TexturePackPage.TempSubTextureInfo.get(j);
            final Texture split = texture.split(subTextureInfo.x, subTextureInfo.y, subTextureInfo.w, subTextureInfo.h);
            split.copyMaskRegion(texture, subTextureInfo.x, subTextureInfo.y, subTextureInfo.w, subTextureInfo.h);
            split.setName(subTextureInfo.name);
            this.subTextures.put(subTextureInfo.name, split);
            TexturePackPage.subTextureMap.put(subTextureInfo.name, split);
            split.offsetX = (float)subTextureInfo.ox;
            split.offsetY = (float)subTextureInfo.oy;
            split.widthOrig = subTextureInfo.fx;
            split.heightOrig = subTextureInfo.fy;
        }
        texture.mask = null;
        TexturePackPage.texturePackPageMap.put(readString, this);
        while (readIntByte(bufferedInputStream) != -559038737) {}
    }
    
    static {
        TexturePackPage.FoundTextures = new HashMap<String, Stack<String>>();
        subTextureMap = new HashMap<String, Texture>();
        subTextureMap2 = new HashMap<String, Texture>();
        texturePackPageMap = new HashMap<String, TexturePackPage>();
        TexturePackPageNameMap = new HashMap<String, String>();
        TexturePackPage.SliceBuffer = null;
        TexturePackPage.bHasCache = false;
        TexturePackPage.percent = 0;
        TexturePackPage.chl1 = 0;
        TexturePackPage.chl2 = 0;
        TexturePackPage.chl3 = 0;
        TexturePackPage.chl4 = 0;
        TexturePackPage.v = new StringBuilder(50);
        TexturePackPage.TempSubTextureInfo = new ArrayList<SubTextureInfo>();
        TexturePackPage.tempFilenameCheck = new ArrayList<String>();
        TexturePackPage.bIgnoreWorldItemTextures = true;
    }
    
    public static class SubTextureInfo
    {
        public int w;
        public int h;
        public int x;
        public int y;
        public int ox;
        public int oy;
        public int fx;
        public int fy;
        public String name;
        
        public SubTextureInfo(final int x, final int y, final int w, final int h, final int ox, final int oy, final int fx, final int fy, final String name) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.ox = ox;
            this.oy = oy;
            this.fx = fx;
            this.fy = fy;
            this.name = name;
        }
    }
}
