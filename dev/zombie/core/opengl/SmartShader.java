// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.opengl;

import zombie.core.textures.Texture;
import org.lwjgl.util.vector.Matrix4f;
import zombie.iso.Vector2;
import zombie.iso.Vector3;

public final class SmartShader
{
    private final ShaderProgram m_shaderProgram;
    
    public SmartShader(final String s) {
        this.m_shaderProgram = ShaderProgram.createShaderProgram(s, false, true);
    }
    
    public SmartShader(final String s, final boolean b) {
        this.m_shaderProgram = ShaderProgram.createShaderProgram(s, b, true);
    }
    
    public void Start() {
        this.m_shaderProgram.Start();
    }
    
    public void End() {
        this.m_shaderProgram.End();
    }
    
    public void setValue(final String s, final float n) {
        this.m_shaderProgram.setValue(s, n);
    }
    
    public void setValue(final String s, final int n) {
        this.m_shaderProgram.setValue(s, n);
    }
    
    public void setValue(final String s, final Vector3 vector3) {
        this.m_shaderProgram.setValue(s, vector3);
    }
    
    public void setValue(final String s, final Vector2 vector2) {
        this.m_shaderProgram.setValue(s, vector2);
    }
    
    public void setVector2f(final String s, final float n, final float n2) {
        this.m_shaderProgram.setVector2(s, n, n2);
    }
    
    public void setVector3f(final String s, final float n, final float n2, final float n3) {
        this.m_shaderProgram.setVector3(s, n, n2, n3);
    }
    
    public void setVector4f(final String s, final float n, final float n2, final float n3, final float n4) {
        this.m_shaderProgram.setVector4(s, n, n2, n3, n4);
    }
    
    public void setValue(final String s, final Matrix4f matrix4f) {
        this.m_shaderProgram.setValue(s, matrix4f);
    }
    
    public void setValue(final String s, final Texture texture, final int n) {
        this.m_shaderProgram.setValue(s, texture, n);
    }
}
