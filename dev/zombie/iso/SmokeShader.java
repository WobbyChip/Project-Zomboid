// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import zombie.core.textures.TextureDraw;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.opengl.ShaderProgram;
import zombie.core.opengl.Shader;

public final class SmokeShader extends Shader
{
    private int mvpMatrix;
    private int FireTime;
    private int FireParam;
    private int FireTexture;
    
    public SmokeShader(final String s) {
        super(s);
    }
    
    @Override
    protected void onCompileSuccess(final ShaderProgram shaderProgram) {
        final int shaderID = shaderProgram.getShaderID();
        this.FireTexture = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"FireTexture");
        this.mvpMatrix = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"mvpMatrix");
        this.FireTime = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"FireTime");
        this.FireParam = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"FireParam");
        this.Start();
        if (this.FireTexture != -1) {
            ARBShaderObjects.glUniform1iARB(this.FireTexture, 0);
        }
        this.End();
    }
    
    public void updateSmokeParams(final TextureDraw textureDraw, final int n, final float n2) {
        final ParticlesFire instance = ParticlesFire.getInstance();
        GL13.glActiveTexture(33984);
        instance.getFireSmokeTexture().bind();
        GL11.glTexEnvi(8960, 8704, 7681);
        ARBShaderObjects.glUniformMatrix4fvARB(this.mvpMatrix, true, instance.getMVPMatrix());
        ARBShaderObjects.glUniform1fARB(this.FireTime, n2);
        ARBShaderObjects.glUniformMatrix3fvARB(this.FireParam, true, instance.getParametersFire());
        if (this.FireTexture != -1) {
            ARBShaderObjects.glUniform1iARB(this.FireTexture, 0);
        }
    }
}
