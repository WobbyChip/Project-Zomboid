// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import zombie.core.SpriteRenderer;
import zombie.iso.sprite.SkyBox;
import zombie.core.textures.TextureDraw;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.opengl.ShaderProgram;
import zombie.core.opengl.Shader;

public final class WaterShader extends Shader
{
    private int WaterGroundTex;
    private int WaterTextureReflectionA;
    private int WaterTextureReflectionB;
    private int WaterTime;
    private int WaterOffset;
    private int WaterViewport;
    private int WaterReflectionParam;
    private int WaterParamWind;
    private int WaterParamWindSpeed;
    private int WaterParamRainIntensity;
    
    public WaterShader(final String s) {
        super(s);
    }
    
    @Override
    protected void onCompileSuccess(final ShaderProgram shaderProgram) {
        final int shaderID = shaderProgram.getShaderID();
        this.WaterGroundTex = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WaterGroundTex");
        this.WaterTextureReflectionA = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WaterTextureReflectionA");
        this.WaterTextureReflectionB = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WaterTextureReflectionB");
        this.WaterTime = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WTime");
        this.WaterOffset = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WOffset");
        this.WaterViewport = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WViewport");
        this.WaterReflectionParam = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WReflectionParam");
        this.WaterParamWind = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WParamWind");
        this.WaterParamWindSpeed = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WParamWindSpeed");
        this.WaterParamRainIntensity = ARBShaderObjects.glGetUniformLocationARB(shaderID, (CharSequence)"WParamRainIntensity");
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
        this.End();
    }
    
    @Override
    public void startMainThread(final TextureDraw textureDraw, final int n) {
        final IsoWater instance = IsoWater.getInstance();
        final SkyBox instance2 = SkyBox.getInstance();
        textureDraw.u0 = instance.getWaterWindX();
        textureDraw.u1 = instance.getWaterWindY();
        textureDraw.u2 = instance.getWaterWindSpeed();
        textureDraw.u3 = IsoPuddles.getInstance().getRainIntensity();
        textureDraw.v0 = instance.getShaderTime();
        textureDraw.v1 = instance2.getTextureShift();
    }
    
    public void updateWaterParams(final TextureDraw textureDraw, final int n) {
        final IsoWater instance = IsoWater.getInstance();
        final SkyBox instance2 = SkyBox.getInstance();
        final PlayerCamera renderingPlayerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(n);
        GL13.glActiveTexture(33984);
        instance.getTextureBottom().bind();
        GL11.glTexEnvi(8960, 8704, 7681);
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
        ARBShaderObjects.glUniform1fARB(this.WaterTime, textureDraw.v0);
        final Vector4f shaderOffset = instance.getShaderOffset();
        ARBShaderObjects.glUniform4fARB(this.WaterOffset, shaderOffset.x - 90000.0f, shaderOffset.y - 640000.0f, shaderOffset.z, shaderOffset.w);
        ARBShaderObjects.glUniform4fARB(this.WaterViewport, (float)IsoCamera.getOffscreenLeft(n), (float)IsoCamera.getOffscreenTop(n), renderingPlayerCamera.OffscreenWidth / renderingPlayerCamera.zoom, renderingPlayerCamera.OffscreenHeight / renderingPlayerCamera.zoom);
        ARBShaderObjects.glUniform1fARB(this.WaterReflectionParam, textureDraw.v1);
        ARBShaderObjects.glUniform2fARB(this.WaterParamWind, textureDraw.u0, textureDraw.u1);
        ARBShaderObjects.glUniform1fARB(this.WaterParamWindSpeed, textureDraw.u2);
        ARBShaderObjects.glUniform1fARB(this.WaterParamRainIntensity, textureDraw.u3);
    }
}
