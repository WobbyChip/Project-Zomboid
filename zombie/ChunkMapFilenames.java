// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.core.Core;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public final class ChunkMapFilenames
{
    public static ChunkMapFilenames instance;
    public final ConcurrentHashMap<Long, Object> Map;
    public final ConcurrentHashMap<Long, Object> HeaderMap;
    String prefix;
    private File dirFile;
    private String cacheDir;
    
    public ChunkMapFilenames() {
        this.Map = new ConcurrentHashMap<Long, Object>();
        this.HeaderMap = new ConcurrentHashMap<Long, Object>();
        this.prefix = "map_";
    }
    
    public void clear() {
        this.dirFile = null;
        this.cacheDir = null;
        this.Map.clear();
        this.HeaderMap.clear();
    }
    
    public File getFilename(final int n, final int n2) {
        final long l = (long)n << 32 | (long)n2;
        if (this.Map.containsKey(l)) {
            return (File)this.Map.get(l);
        }
        if (this.cacheDir == null) {
            this.cacheDir = ZomboidFileSystem.instance.getGameModeCacheDir();
        }
        final File value = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;II)Ljava/lang/String;, this.cacheDir, File.separator, Core.GameSaveWorld, File.separator, this.prefix, n, n2));
        this.Map.put(l, value);
        return value;
    }
    
    public File getDir(final String s) {
        if (this.cacheDir == null) {
            this.cacheDir = ZomboidFileSystem.instance.getGameModeCacheDir();
        }
        if (this.dirFile == null) {
            this.dirFile = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.cacheDir, File.separator, s));
        }
        return this.dirFile;
    }
    
    public String getHeader(final int n, final int n2) {
        final long l = (long)n << 32 | (long)n2;
        if (this.HeaderMap.containsKey(l)) {
            return this.HeaderMap.get(l).toString();
        }
        final String value = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n, n2);
        this.HeaderMap.put(l, value);
        return value;
    }
    
    static {
        ChunkMapFilenames.instance = new ChunkMapFilenames();
    }
}
