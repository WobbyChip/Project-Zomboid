// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import java.util.Collection;
import zombie.util.list.PZArrayUtil;
import zombie.popman.ObjectPool;
import java.util.ArrayList;
import zombie.core.opengl.SmartShader;

public final class TextureCombinerCommand
{
    public int x;
    public int y;
    public int w;
    public int h;
    public Texture mask;
    public Texture tex;
    public int blendSrc;
    public int blendDest;
    public SmartShader shader;
    public ArrayList<TextureCombinerShaderParam> shaderParams;
    public static final ObjectPool<TextureCombinerCommand> pool;
    
    public TextureCombinerCommand() {
        this.x = -1;
        this.y = -1;
        this.w = -1;
        this.h = -1;
        this.shaderParams = null;
    }
    
    @Override
    public String toString() {
        final String lineSeparator = System.lineSeparator();
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IILjava/lang/String;IILjava/lang/String;Lzombie/core/textures/Texture;Ljava/lang/String;Lzombie/core/textures/Texture;Ljava/lang/String;ILjava/lang/String;ILjava/lang/String;Lzombie/core/opengl/SmartShader;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, lineSeparator, this.x, this.y, lineSeparator, this.w, this.h, lineSeparator, this.mask, lineSeparator, this.tex, lineSeparator, this.blendSrc, lineSeparator, this.blendDest, lineSeparator, this.shader, lineSeparator, PZArrayUtil.arrayToString(this.shaderParams), lineSeparator);
    }
    
    public TextureCombinerCommand init(final Texture texture) {
        this.tex = this.requireNonNull(texture);
        this.blendSrc = 770;
        this.blendDest = 771;
        return this;
    }
    
    public TextureCombinerCommand init(final Texture texture, final int blendSrc, final int blendDest) {
        this.tex = this.requireNonNull(texture);
        this.blendSrc = blendSrc;
        this.blendDest = blendDest;
        return this;
    }
    
    public TextureCombinerCommand init(final Texture texture, final SmartShader shader) {
        this.tex = this.requireNonNull(texture);
        this.shader = shader;
        this.blendSrc = 770;
        this.blendDest = 771;
        return this;
    }
    
    public TextureCombinerCommand init(final Texture texture, final SmartShader shader, final Texture texture2, final int blendSrc, final int blendDest) {
        this.tex = this.requireNonNull(texture);
        this.shader = shader;
        this.blendSrc = blendSrc;
        this.blendDest = blendDest;
        this.mask = this.requireNonNull(texture2);
        return this;
    }
    
    public TextureCombinerCommand init(final Texture texture, final int x, final int y, final int w, final int h) {
        this.tex = this.requireNonNull(texture);
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.blendSrc = 770;
        this.blendDest = 771;
        return this;
    }
    
    public TextureCombinerCommand init(final Texture texture, final SmartShader shader, final ArrayList<TextureCombinerShaderParam> c, final Texture texture2, final int blendSrc, final int blendDest) {
        this.tex = this.requireNonNull(texture);
        this.shader = shader;
        this.blendSrc = blendSrc;
        this.blendDest = blendDest;
        this.mask = this.requireNonNull(texture2);
        if (this.shaderParams == null) {
            this.shaderParams = new ArrayList<TextureCombinerShaderParam>();
        }
        this.shaderParams.clear();
        this.shaderParams.addAll(c);
        return this;
    }
    
    public TextureCombinerCommand init(final Texture texture, final SmartShader shader, final ArrayList<TextureCombinerShaderParam> c) {
        this.tex = this.requireNonNull(texture);
        this.blendSrc = 770;
        this.blendDest = 771;
        this.shader = shader;
        if (this.shaderParams == null) {
            this.shaderParams = new ArrayList<TextureCombinerShaderParam>();
        }
        this.shaderParams.clear();
        this.shaderParams.addAll(c);
        return this;
    }
    
    private Texture requireNonNull(final Texture texture) {
        return (texture == null) ? Texture.getErrorTexture() : texture;
    }
    
    public static TextureCombinerCommand get() {
        final TextureCombinerCommand textureCombinerCommand = TextureCombinerCommand.pool.alloc();
        textureCombinerCommand.x = -1;
        textureCombinerCommand.tex = null;
        textureCombinerCommand.mask = null;
        textureCombinerCommand.shader = null;
        if (textureCombinerCommand.shaderParams != null) {
            textureCombinerCommand.shaderParams.clear();
        }
        return textureCombinerCommand;
    }
    
    static {
        pool = new ObjectPool<TextureCombinerCommand>(TextureCombinerCommand::new);
    }
}
