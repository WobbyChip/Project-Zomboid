// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.util.Pool;
import zombie.core.opengl.Shader;
import zombie.util.PooledObject;

public final class ShaderStackEntry extends PooledObject
{
    private Shader m_shader;
    private int m_playerIndex;
    private static final Pool<ShaderStackEntry> s_pool;
    
    public Shader getShader() {
        return this.m_shader;
    }
    
    public int getPlayerIndex() {
        return this.m_playerIndex;
    }
    
    public static ShaderStackEntry alloc(final Shader shader, final int playerIndex) {
        final ShaderStackEntry shaderStackEntry = ShaderStackEntry.s_pool.alloc();
        shaderStackEntry.m_shader = shader;
        shaderStackEntry.m_playerIndex = playerIndex;
        return shaderStackEntry;
    }
    
    static {
        s_pool = new Pool<ShaderStackEntry>(ShaderStackEntry::new);
    }
}
