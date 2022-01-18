// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import zombie.core.SpriteRenderer;
import zombie.iso.sprite.SkyBox;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.opengl.ShaderProgram;
import zombie.core.opengl.Shader;

public final class PuddlesShader extends Shader
{
    private int WaterGroundTex;
    private int PuddlesHM;
    private int WaterTextureReflectionA;
    private int WaterTextureReflectionB;
    private int WaterTime;
    private int WaterOffset;
    private int WaterViewport;
    private int WaterReflectionParam;
    private int PuddlesParams;
    
    public PuddlesShader(final String s) {
        super(s);
    }
    
    @Override
    protected void onCompileSuccess(final ShaderProgram shaderProgram) {
        final int shaderID = shaderProgram.getShaderID();
        this.WaterGroundTex = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WaterGroundTex");
        this.WaterTextureReflectionA = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WaterTextureReflectionA");
        this.WaterTextureReflectionB = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WaterTextureReflectionB");
        this.PuddlesHM = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"PuddlesHM");
        this.WaterTime = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WTime");
        this.WaterOffset = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WOffset");
        this.WaterViewport = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WViewport");
        this.WaterReflectionParam = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WReflectionParam");
        this.PuddlesParams = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"PuddlesParams");
        this.Start();
        if (this.WaterGroundTex != -1) {
            ARBShaderObjects.glUniform1iARB(this.WaterGroundTex, 0);
        }
        if (this.WaterTextureReflectionA != -1) {
            ARBShaderObjects.glUniform1iARB(this.WaterTextureReflectionA, 1);
        }
        if (this.WaterTextureReflectionB != -1) {
            ARBShaderObjects.glUniform1iARB(this.WaterTextureReflectionB, 2);
        }
        if (this.PuddlesHM != -1) {
            ARBShaderObjects.glUniform1iARB(this.PuddlesHM, 3);
        }
        this.End();
    }
    
    public void updatePuddlesParams(final int n, final int n2) {
        final IsoPuddles instance = IsoPuddles.getInstance();
        final SkyBox instance2 = SkyBox.getInstance();
        final PlayerCamera renderingPlayerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(n);
        GL13.glActiveTexture(33985);
        instance2.getTextureCurrent().bind();
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexEnvi(8960, 8704, 7681);
        GL13.glActiveTexture(33986);
        instance2.getTexturePrev().bind();
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexEnvi(8960, 8704, 7681);
        GL13.glActiveTexture(33987);
        instance.getHMTexture().bind();
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexEnvi(8960, 8704, 7681);
        ARBShaderObjects.glUniform1fARB(this.WaterTime, instance.getShaderTime());
        final Vector4f shaderOffset = instance.getShaderOffset();
        ARBShaderObjects.glUniform4fARB(this.WaterOffset, shaderOffset.x - 90000.0f, shaderOffset.y - 640000.0f, shaderOffset.z, shaderOffset.w);
        ARBShaderObjects.glUniform4fARB(this.WaterViewport, (float)IsoCamera.getOffscreenLeft(n), (float)IsoCamera.getOffscreenTop(n), renderingPlayerCamera.OffscreenWidth / renderingPlayerCamera.zoom, renderingPlayerCamera.OffscreenHeight / renderingPlayerCamera.zoom);
        ARBShaderObjects.glUniform1fARB(this.WaterReflectionParam, instance2.getTextureShift());
        ARBShaderObjects.glUniformMatrix4fvARB(this.PuddlesParams, true, instance.getPuddlesParams(n2));
    }
}
