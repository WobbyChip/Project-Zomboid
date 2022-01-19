// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import java.util.Iterator;
import zombie.core.math.PZMath;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import zombie.ZomboidFileSystem;
import java.util.HashMap;

public final class WorldMapImages
{
    private static final HashMap<String, WorldMapImages> s_filenameToImages;
    private String m_directory;
    private ImagePyramid m_pyramid;
    
    public static WorldMapImages getOrCreate(final String directory) {
        final String string = ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, directory));
        if (!Files.exists(Paths.get(string, new String[0]), new LinkOption[0])) {
            return null;
        }
        WorldMapImages value = WorldMapImages.s_filenameToImages.get(string);
        if (value == null) {
            value = new WorldMapImages();
            value.m_directory = directory;
            (value.m_pyramid = new ImagePyramid()).setZipFile(string);
            WorldMapImages.s_filenameToImages.put(string, value);
        }
        return value;
    }
    
    public ImagePyramid getPyramid() {
        return this.m_pyramid;
    }
    
    public int getMinX() {
        return this.m_pyramid.m_minX;
    }
    
    public int getMinY() {
        return this.m_pyramid.m_minY;
    }
    
    public int getMaxX() {
        return this.m_pyramid.m_maxX;
    }
    
    public int getMaxY() {
        return this.m_pyramid.m_maxY;
    }
    
    public int getZoom(final float n) {
        int n2 = 4;
        if (n >= 16.0) {
            n2 = 0;
        }
        else if (n >= 15.0f) {
            n2 = 1;
        }
        else if (n >= 14.0f) {
            n2 = 2;
        }
        else if (n >= 13.0f) {
            n2 = 3;
        }
        return PZMath.clamp(n2, this.m_pyramid.m_minZ, this.m_pyramid.m_maxZ);
    }
    
    public float getResolution() {
        return this.m_pyramid.m_resolution;
    }
    
    private void destroy() {
        this.m_pyramid.destroy();
    }
    
    public static void Reset() {
        final Iterator<WorldMapImages> iterator = WorldMapImages.s_filenameToImages.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().destroy();
        }
        WorldMapImages.s_filenameToImages.clear();
    }
    
    static {
        s_filenameToImages = new HashMap<String, WorldMapImages>();
    }
}
