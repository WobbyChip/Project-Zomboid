// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather.fog;

import zombie.IndieGL;
import zombie.core.opengl.RenderThread;
import zombie.core.SpriteRenderer;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.opengl.ShaderProgram;

public class FogShader
{
    public static final FogShader instance;
    private ShaderProgram shaderProgram;
    private int noiseTexture;
    private int screenInfo;
    private int textureInfo;
    private int rectangleInfo;
    private int worldOffset;
    private int scalingInfo;
    private int colorInfo;
    private int paramInfo;
    private int cameraInfo;
    
    public void initShader() {
        this.shaderProgram = ShaderProgram.createShaderProgram("fog", false, true);
        if (this.shaderProgram.isCompiled()) {
            this.noiseTexture = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"NoiseTexture");
            this.screenInfo = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"screenInfo");
            this.textureInfo = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"textureInfo");
            this.rectangleInfo = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"rectangleInfo");
            this.scalingInfo = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"scalingInfo");
            this.colorInfo = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"colorInfo");
            this.worldOffset = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"worldOffset");
            this.paramInfo = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"paramInfo");
            this.cameraInfo = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), (CharSequence)"cameraInfo");
            ARBShaderObjects.glUseProgramObjectARB(this.shaderProgram.getShaderID());
            ARBShaderObjects.glUseProgramObjectARB(0);
        }
    }
    
    public void setScreenInfo(final float n, final float n2, final float n3, final float n4) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.screenInfo, n, n2, n3, n4);
    }
    
    public void setTextureInfo(final float n, final float n2, final float n3, final float n4) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.textureInfo, n, n2, n3, n4);
    }
    
    public void setRectangleInfo(final float n, final float n2, final float n3, final float n4) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.rectangleInfo, n, n2, n3, n4);
    }
    
    public void setScalingInfo(final float n, final float n2, final float n3, final float n4) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.scalingInfo, n, n2, n3, n4);
    }
    
    public void setColorInfo(final float n, final float n2, final float n3, final float n4) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.colorInfo, n, n2, n3, n4);
    }
    
    public void setWorldOffset(final float n, final float n2, final float n3, final float n4) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.worldOffset, n, n2, n3, n4);
    }
    
    public void setParamInfo(final float n, final float n2, final float n3, final float n4) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.paramInfo, n, n2, n3, n4);
    }
    
    public void setCameraInfo(final float n, final float n2, final float n3, final float n4) {
        SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.cameraInfo, n, n2, n3, n4);
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
    
    protected void reloadShader() {
        if (this.shaderProgram != null) {
            this.shaderProgram = null;
        }
    }
    
    static {
        instance = new FogShader();
    }
}
