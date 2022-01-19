// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.IndieGL;
import zombie.core.opengl.RenderThread;
import zombie.core.SpriteRenderer;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.opengl.ShaderProgram;

public final class TallFenceShader
{
    public static final TallFenceShader instance;
    private ShaderProgram shaderProgram;
    private int u_alpha;
    private int u_outlineColor;
    private int u_stepSize;
    
    public void initShader() {
        this.shaderProgram = ShaderProgram.createShaderProgram("tallFence", false, true);
        if (this.shaderProgram.isCompiled()) {
            this.u_alpha = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"u_alpha");
            this.u_stepSize = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"u_stepSize");
            this.u_outlineColor = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"u_outlineColor");
            ARBShaderObjects.glUseProgramObjectARB(this.shaderProgram.getShaderID());
            ARBShaderObjects.glUniform2fARB(this.u_stepSize, 0.001f, 0.001f);
            ARBShaderObjects.glUseProgramObjectARB(0);
        }
    }
    
    public void setAlpha(final float n) {
        SpriteRenderer.instance.ShaderUpdate1f(this.shaderProgram.getShaderID(), this.u_alpha, n);
    }
    
    public void setOutlineColor(final float n, final float n2, final float n3, final float n4) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.u_outlineColor, n, n2, n3, n4);
    }
    
    public void setStepSize(final float n, final int n2, final int n3) {
        SpriteRenderer.instance.ShaderUpdate2f(this.shaderProgram.getShaderID(), this.u_stepSize, n / n2, n / n3);
    }
    
    public boolean StartShader() {
        if (this.shaderProgram == null) {
            RenderThread.invokeOnRenderContext(this::initShader);
        }
        if (this.shaderProgram.isCompiled()) {
            IndieGL.StartShader(this.shaderProgram.getShaderID(), 0);
            return true;
        }
        return false;
    }
    
    static {
        instance = new TallFenceShader();
    }
}
