// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite;

import zombie.core.opengl.ShaderProgram;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.textures.TextureDraw;
import zombie.core.opengl.Shader;

final class SkyBoxShader extends Shader
{
    private int SkyBoxTime;
    private int SkyBoxParamCloudCount;
    private int SkyBoxParamCloudSize;
    private int SkyBoxParamSunLight;
    private int SkyBoxParamSunColor;
    private int SkyBoxParamSkyHColour;
    private int SkyBoxParamSkyLColour;
    private int SkyBoxParamCloudLight;
    private int SkyBoxParamStars;
    private int SkyBoxParamFog;
    private int SkyBoxParamWind;
    
    public SkyBoxShader(final String s) {
        super(s);
    }
    
    @Override
    public void startRenderThread(final TextureDraw textureDraw) {
        final SkyBox instance = SkyBox.getInstance();
        ARBShaderObjects.glUniform1fARB(this.SkyBoxTime, (float)instance.getShaderTime());
        ARBShaderObjects.glUniform1fARB(this.SkyBoxParamCloudCount, instance.getShaderCloudCount());
        ARBShaderObjects.glUniform1fARB(this.SkyBoxParamCloudSize, instance.getShaderCloudSize());
        ARBShaderObjects.glUniform3fARB(this.SkyBoxParamSunLight, instance.getShaderSunLight().x, instance.getShaderSunLight().y, instance.getShaderSunLight().z);
        ARBShaderObjects.glUniform3fARB(this.SkyBoxParamSunColor, instance.getShaderSunColor().r, instance.getShaderSunColor().g, instance.getShaderSunColor().b);
        ARBShaderObjects.glUniform3fARB(this.SkyBoxParamSkyHColour, instance.getShaderSkyHColour().r, instance.getShaderSkyHColour().g, instance.getShaderSkyHColour().b);
        ARBShaderObjects.glUniform3fARB(this.SkyBoxParamSkyLColour, instance.getShaderSkyLColour().r, instance.getShaderSkyLColour().g, instance.getShaderSkyLColour().b);
        ARBShaderObjects.glUniform1fARB(this.SkyBoxParamCloudLight, instance.getShaderCloudLight());
        ARBShaderObjects.glUniform1fARB(this.SkyBoxParamStars, instance.getShaderStars());
        ARBShaderObjects.glUniform1fARB(this.SkyBoxParamFog, instance.getShaderFog());
        ARBShaderObjects.glUniform3fARB(this.SkyBoxParamWind, instance.getShaderWind().x, instance.getShaderWind().y, instance.getShaderWind().z);
    }
    
    public void onCompileSuccess(final ShaderProgram shaderProgram) {
        final int id = this.getID();
        this.SkyBoxTime = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"SBTime");
        this.SkyBoxParamCloudCount = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"SBParamCloudCount");
        this.SkyBoxParamCloudSize = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"SBParamCloudSize");
        this.SkyBoxParamSunLight = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"SBParamSunLight");
        this.SkyBoxParamSunColor = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"SBParamSunColour");
        this.SkyBoxParamSkyHColour = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"SBParamSkyHColour");
        this.SkyBoxParamSkyLColour = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"SBParamSkyLColour");
        this.SkyBoxParamCloudLight = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"SBParamCloudLight");
        this.SkyBoxParamStars = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"SBParamStars");
        this.SkyBoxParamFog = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"SBParamFog");
        this.SkyBoxParamWind = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"SBParamWind");
    }
}
