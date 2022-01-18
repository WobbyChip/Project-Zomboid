// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.bucket;

import java.nio.file.FileSystems;
import java.util.Iterator;
import zombie.DebugFileWatcher;
import zombie.core.skinnedmodel.ModelManager;
import zombie.PredicatedFileWatcher;
import java.nio.file.FileSystem;
import zombie.core.textures.Texture;
import java.nio.file.Path;
import java.util.HashMap;

public final class Bucket
{
    private String m_name;
    private final HashMap<Path, Texture> m_textures;
    private static final FileSystem m_fs;
    private final PredicatedFileWatcher m_fileWatcher;
    
    public Bucket() {
        this.m_textures = new HashMap<Path, Texture>();
        this.m_fileWatcher = new PredicatedFileWatcher(s -> this.HasTexture(s), s2 -> {
            this.getTexture(s2).reloadFromFile(s2);
            ModelManager.instance.reloadAllOutfits();
            return;
        });
        DebugFileWatcher.instance.add(this.m_fileWatcher);
    }
    
    public void AddTexture(final Path key, final Texture value) {
        if (value == null) {
            return;
        }
        this.m_textures.put(key, value);
    }
    
    public void AddTexture(final String s, final Texture texture) {
        if (texture == null) {
            return;
        }
        this.AddTexture(Bucket.m_fs.getPath(s, new String[0]), texture);
    }
    
    public void Dispose() {
        final Iterator<Texture> iterator = this.m_textures.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().destroy();
        }
        this.m_textures.clear();
    }
    
    public Texture getTexture(final Path key) {
        return this.m_textures.get(key);
    }
    
    public Texture getTexture(final String s) {
        return this.getTexture(Bucket.m_fs.getPath(s, new String[0]));
    }
    
    public boolean HasTexture(final Path key) {
        return this.m_textures.containsKey(key);
    }
    
    public boolean HasTexture(final String s) {
        return this.HasTexture(Bucket.m_fs.getPath(s, new String[0]));
    }
    
    String getName() {
        return this.m_name;
    }
    
    void setName(final String name) {
        this.m_name = name;
    }
    
    public void forgetTexture(final String key) {
        this.m_textures.remove(key);
    }
    
    static {
        m_fs = FileSystems.getDefault();
    }
}
