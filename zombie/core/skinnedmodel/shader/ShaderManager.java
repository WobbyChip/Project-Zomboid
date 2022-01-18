// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.shader;

import java.util.ArrayList;

public final class ShaderManager
{
    public static final ShaderManager instance;
    private final ArrayList<Shader> shaders;
    
    public ShaderManager() {
        this.shaders = new ArrayList<Shader>();
    }
    
    private Shader getShader(final String s, final boolean b) {
        for (int i = 0; i < this.shaders.size(); ++i) {
            final Shader shader = this.shaders.get(i);
            if (s.equals(shader.name) && b == shader.bStatic) {
                return shader;
            }
        }
        return null;
    }
    
    public Shader getOrCreateShader(final String s, final boolean b) {
        final Shader shader = this.getShader(s, b);
        if (shader != null) {
            return shader;
        }
        for (int i = 0; i < this.shaders.size(); ++i) {
            final Shader shader2 = this.shaders.get(i);
            if (shader2.name.equalsIgnoreCase(s) && !shader2.name.equals(s)) {
                throw new IllegalArgumentException("shader filenames are case-sensitive");
            }
        }
        final Shader e = new Shader(s, b);
        this.shaders.add(e);
        return e;
    }
    
    static {
        instance = new ShaderManager();
    }
}
