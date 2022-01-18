// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import java.util.zip.CRC32;
import gnu.trove.map.hash.TIntLongHashMap;

public class ChunkChecksum
{
    private static final TIntLongHashMap checksumCache;
    private static final StringBuilder stringBuilder;
    private static final CRC32 crc32;
    private static final byte[] bytes;
    
    private static void noise(final String s) {
        if (Core.bDebug) {}
    }
    
    public static long getChecksum(final int i, final int j) throws IOException {
        MPStatistic.getInstance().ChunkChecksum.Start();
        long n = 0L;
        synchronized (ChunkChecksum.checksumCache) {
            final int n2 = i + j * 30 * 1000;
            if (ChunkChecksum.checksumCache.containsKey(n2)) {
                noise(invokedynamic(makeConcatWithConstants:(IIJ)Ljava/lang/String;, i, j, ChunkChecksum.checksumCache.get(n2)));
                n = ChunkChecksum.checksumCache.get(n2);
            }
            else {
                ChunkChecksum.stringBuilder.setLength(0);
                ChunkChecksum.stringBuilder.append(ZomboidFileSystem.instance.getGameModeCacheDir());
                ChunkChecksum.stringBuilder.append(File.separator);
                ChunkChecksum.stringBuilder.append(Core.GameSaveWorld);
                ChunkChecksum.stringBuilder.append(File.separator);
                ChunkChecksum.stringBuilder.append("map_");
                ChunkChecksum.stringBuilder.append(i);
                ChunkChecksum.stringBuilder.append("_");
                ChunkChecksum.stringBuilder.append(j);
                ChunkChecksum.stringBuilder.append(".bin");
                n = createChecksum(ChunkChecksum.stringBuilder.toString());
                ChunkChecksum.checksumCache.put(n2, n);
                noise(invokedynamic(makeConcatWithConstants:(IIJ)Ljava/lang/String;, i, j, n));
            }
        }
        MPStatistic.getInstance().ChunkChecksum.End();
        return n;
    }
    
    public static long getChecksumIfExists(final int n, final int n2) throws IOException {
        long value = 0L;
        MPStatistic.getInstance().ChunkChecksum.Start();
        synchronized (ChunkChecksum.checksumCache) {
            final int n3 = n + n2 * 30 * 1000;
            if (ChunkChecksum.checksumCache.containsKey(n3)) {
                value = ChunkChecksum.checksumCache.get(n3);
            }
        }
        MPStatistic.getInstance().ChunkChecksum.End();
        return value;
    }
    
    public static void setChecksum(final int n, final int n2, final long n3) {
        MPStatistic.getInstance().ChunkChecksum.Start();
        synchronized (ChunkChecksum.checksumCache) {
            ChunkChecksum.checksumCache.put(n + n2 * 30 * 1000, n3);
            noise(invokedynamic(makeConcatWithConstants:(IIJ)Ljava/lang/String;, n, n2, n3));
        }
        MPStatistic.getInstance().ChunkChecksum.End();
    }
    
    public static long createChecksum(final String s) throws IOException {
        MPStatistic.getInstance().ChunkChecksum.Start();
        if (!new File(s).exists()) {
            MPStatistic.getInstance().ChunkChecksum.End();
            return 0L;
        }
        final FileInputStream fileInputStream = new FileInputStream(s);
        try {
            ChunkChecksum.crc32.reset();
            int read;
            while ((read = fileInputStream.read(ChunkChecksum.bytes)) != -1) {
                ChunkChecksum.crc32.update(ChunkChecksum.bytes, 0, read);
            }
            final long value = ChunkChecksum.crc32.getValue();
            MPStatistic.getInstance().ChunkChecksum.End();
            final long n = value;
            fileInputStream.close();
            return n;
        }
        catch (Throwable t) {
            try {
                fileInputStream.close();
            }
            catch (Throwable exception) {
                t.addSuppressed(exception);
            }
            throw t;
        }
    }
    
    public static void Reset() {
        MPStatistic.getInstance().ChunkChecksum.Start();
        ChunkChecksum.checksumCache.clear();
        MPStatistic.getInstance().ChunkChecksum.End();
    }
    
    static {
        checksumCache = new TIntLongHashMap();
        stringBuilder = new StringBuilder(128);
        crc32 = new CRC32();
        bytes = new byte[1024];
    }
}
