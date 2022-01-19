// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.Texture;

import zombie.core.textures.Texture;
import java.util.HashMap;

public class TextureManager
{
    public static TextureManager Instance;
    public HashMap<String, Texture2D> Textures;
    
    public TextureManager() {
        this.Textures = new HashMap<String, Texture2D>();
    }
    
    public boolean AddTexture(final String key) {
        final Texture sharedTexture = Texture.getSharedTexture(key);
        if (sharedTexture == null) {
            return false;
        }
        this.Textures.put(key, new Texture2D(sharedTexture));
        return true;
    }
    
    public void AddTexture(final String s, final Texture texture) {
        if (!this.Textures.containsKey(s)) {
            this.Textures.put(s, new Texture2D(texture));
        }
    }
    
    static {
        TextureManager.Instance = new TextureManager();
    }
}
