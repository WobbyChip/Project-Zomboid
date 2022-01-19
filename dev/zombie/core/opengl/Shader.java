// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.opengl;

import zombie.core.textures.TextureDraw;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.textures.Texture;
import java.util.HashMap;

public class Shader implements IShaderProgramListener
{
    public static HashMap<Integer, Shader> ShaderMap;
    public String name;
    private int m_shaderMapID;
    private final ShaderProgram m_shaderProgram;
    public Texture tex;
    public int width;
    public int height;
    
    public Shader(final String name) {
        this.m_shaderMapID = 0;
        this.name = name;
        (this.m_shaderProgram = ShaderProgram.createShaderProgram(name, false, false)).addCompileListener(this);
        this.m_shaderProgram.compile();
    }
    
    public void setTexture(final Texture tex) {
        this.tex = tex;
    }
    
    public int getID() {
        return this.m_shaderProgram.getShaderID();
    }
    
    public void Start() {
        ARBShaderObjects.glUseProgramObjectARB(this.m_shaderProgram.getShaderID());
    }
    
    public void End() {
        ARBShaderObjects.glUseProgramObjectARB(0);
    }
    
    public void destroy() {
        this.m_shaderProgram.destroy();
        Shader.ShaderMap.remove(this.m_shaderMapID);
        this.m_shaderMapID = 0;
    }
    
    public void startMainThread(final TextureDraw textureDraw, final int n) {
    }
    
    public void startRenderThread(final TextureDraw textureDraw) {
    }
    
    public void postRender(final TextureDraw textureDraw) {
    }
    
    public boolean isCompiled() {
        return this.m_shaderProgram.isCompiled();
    }
    
    @Override
    public void callback(final ShaderProgram shaderProgram) {
        Shader.ShaderMap.remove(this.m_shaderMapID);
        this.m_shaderMapID = shaderProgram.getShaderID();
        Shader.ShaderMap.put(this.m_shaderMapID, this);
        this.onCompileSuccess(shaderProgram);
    }
    
    protected void onCompileSuccess(final ShaderProgram shaderProgram) {
    }
    
    public ShaderProgram getProgram() {
        return this.m_shaderProgram;
    }
    
    static {
        Shader.ShaderMap = new HashMap<Integer, Shader>();
    }
}
