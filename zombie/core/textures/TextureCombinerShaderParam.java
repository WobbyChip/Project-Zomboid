// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

public final class TextureCombinerShaderParam
{
    public String name;
    public float min;
    public float max;
    
    public TextureCombinerShaderParam(final String name, final float min, final float max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }
    
    public TextureCombinerShaderParam(final String name, final float n) {
        this.name = name;
        this.min = n;
        this.max = n;
    }
}
