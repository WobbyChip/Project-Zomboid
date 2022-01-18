// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class TextureBinder
{
    public static TextureBinder instance;
    public int maxTextureUnits;
    public int[] textureUnitIDs;
    public int textureUnitIDStart;
    public int textureIndex;
    public int activeTextureIndex;
    
    public TextureBinder() {
        this.maxTextureUnits = 0;
        this.textureUnitIDStart = 33984;
        this.textureIndex = 0;
        this.activeTextureIndex = 0;
        this.maxTextureUnits = 1;
        this.textureUnitIDs = new int[this.maxTextureUnits];
        for (int i = 0; i < this.maxTextureUnits; ++i) {
            this.textureUnitIDs[i] = -1;
        }
    }
    
    public void bind(final int n) {
        for (int i = 0; i < this.maxTextureUnits; ++i) {
            if (this.textureUnitIDs[i] == n) {
                final int activeTextureIndex = i + this.textureUnitIDStart;
                GL13.glActiveTexture(activeTextureIndex);
                this.activeTextureIndex = activeTextureIndex;
                return;
            }
        }
        this.textureUnitIDs[this.textureIndex] = n;
        GL13.glActiveTexture(this.textureUnitIDStart + this.textureIndex);
        GL11.glBindTexture(3553, n);
        this.activeTextureIndex = this.textureUnitIDStart + this.textureIndex;
        ++this.textureIndex;
        if (this.textureIndex >= this.maxTextureUnits) {
            this.textureIndex = 0;
        }
    }
    
    static {
        TextureBinder.instance = new TextureBinder();
    }
}
