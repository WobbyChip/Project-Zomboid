// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather;

import zombie.core.opengl.ShaderProgram;
import zombie.core.PerformanceSettings;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.iso.PlayerCamera;
import zombie.iso.IsoCamera;
import zombie.iso.SearchMode;
import zombie.core.Core;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.opengl.RenderSettings;
import zombie.core.textures.TextureDraw;
import zombie.core.opengl.Shader;

public class WeatherShader extends Shader
{
    public int timeOfDay;
    private int PixelOffset;
    private int PixelSize;
    private int bloom;
    private int timer;
    private int BlurStrength;
    private int TextureSize;
    private int Zoom;
    private int Light;
    private int LightIntensity;
    private int NightValue;
    private int Exterior;
    private int NightVisionGoggles;
    private int DesaturationVal;
    private int FogMod;
    private int SearchModeID;
    private int ScreenInfo;
    private int ParamInfo;
    private int VarInfo;
    private int timerVal;
    private boolean bAlt;
    private static final int texdVarsSize = 22;
    private static float[][] floatArrs;
    
    public WeatherShader(final String s) {
        super(s);
        this.timeOfDay = 0;
        this.bAlt = false;
    }
    
    @Override
    public void startMainThread(final TextureDraw textureDraw, final int n) {
        if (n < 0 || n >= 4) {
            return;
        }
        final RenderSettings.PlayerRenderSettings playerSettings = RenderSettings.getInstance().getPlayerSettings(n);
        final IsoPlayer isoPlayer = IsoPlayer.players[n];
        playerSettings.isExterior();
        if (Math.abs(GameTime.instance.TimeOfDay / 12.0f - 1.0f) > 0.8f && isoPlayer != null && isoPlayer.Traits.NightVision.isSet() && !isoPlayer.isWearingNightVisionGoggles()) {}
        final int offscreenWidth = Core.getInstance().getOffscreenWidth(n);
        final int offscreenHeight = Core.getInstance().getOffscreenHeight(n);
        if (textureDraw.vars == null) {
            textureDraw.vars = getFreeFloatArray();
            if (textureDraw.vars == null) {
                textureDraw.vars = new float[22];
            }
        }
        textureDraw.vars[0] = playerSettings.getBlendColor().r;
        textureDraw.vars[1] = playerSettings.getBlendColor().g;
        textureDraw.vars[2] = playerSettings.getBlendColor().b;
        textureDraw.vars[3] = playerSettings.getBlendIntensity();
        textureDraw.vars[4] = playerSettings.getDesaturation();
        textureDraw.vars[5] = (playerSettings.isApplyNightVisionGoggles() ? 1.0f : 0.0f);
        final SearchMode.PlayerSearchMode searchModeForPlayer = SearchMode.getInstance().getSearchModeForPlayer(n);
        textureDraw.vars[6] = searchModeForPlayer.getShaderBlur();
        textureDraw.vars[7] = searchModeForPlayer.getShaderRadius();
        textureDraw.vars[8] = (float)IsoCamera.getOffscreenLeft(n);
        textureDraw.vars[9] = (float)IsoCamera.getOffscreenTop(n);
        final PlayerCamera playerCamera = IsoCamera.cameras[n];
        textureDraw.vars[10] = (float)IsoCamera.getOffscreenWidth(n);
        textureDraw.vars[11] = (float)IsoCamera.getOffscreenHeight(n);
        textureDraw.vars[12] = playerCamera.RightClickX;
        textureDraw.vars[13] = playerCamera.RightClickY;
        textureDraw.vars[14] = Core.getInstance().getZoom(n);
        textureDraw.vars[15] = ((Core.TileScale == 2) ? 64.0f : 32.0f);
        textureDraw.vars[16] = searchModeForPlayer.getShaderGradientWidth() * textureDraw.vars[15] / 2.0f;
        textureDraw.vars[17] = searchModeForPlayer.getShaderDesat();
        textureDraw.vars[18] = (searchModeForPlayer.isShaderEnabled() ? 1.0f : 0.0f);
        textureDraw.vars[19] = searchModeForPlayer.getShaderDarkness();
        textureDraw.flipped = playerSettings.isExterior();
        textureDraw.f1 = playerSettings.getDarkness();
        textureDraw.col0 = offscreenWidth;
        textureDraw.col1 = offscreenHeight;
        textureDraw.col2 = Core.getInstance().getOffscreenTrueWidth();
        textureDraw.col3 = Core.getInstance().getOffscreenTrueHeight();
        textureDraw.bSingleCol = (Core.getInstance().getZoom(n) > 2.0f || (Core.getInstance().getZoom(n) < 2.0 && Core.getInstance().getZoom(n) >= 1.75f));
    }
    
    @Override
    public void startRenderThread(final TextureDraw textureDraw) {
        final float f1 = textureDraw.f1;
        final boolean flipped = textureDraw.flipped;
        final int col0 = textureDraw.col0;
        final int col2 = textureDraw.col1;
        final int col3 = textureDraw.col2;
        final int col4 = textureDraw.col3;
        final float n = textureDraw.bSingleCol ? 1.0f : 0.0f;
        ARBShaderObjects.glUniform1fARB(this.width, (float)col0);
        ARBShaderObjects.glUniform1fARB(this.height, (float)col2);
        ARBShaderObjects.glUniform3fARB(this.Light, textureDraw.vars[0], textureDraw.vars[1], textureDraw.vars[2]);
        ARBShaderObjects.glUniform1fARB(this.LightIntensity, textureDraw.vars[3]);
        ARBShaderObjects.glUniform1fARB(this.NightValue, f1);
        ARBShaderObjects.glUniform1fARB(this.DesaturationVal, textureDraw.vars[4]);
        ARBShaderObjects.glUniform1fARB(this.NightVisionGoggles, textureDraw.vars[5]);
        ARBShaderObjects.glUniform1fARB(this.Exterior, flipped ? 1.0f : 0.0f);
        ARBShaderObjects.glUniform1fARB(this.timer, (float)(this.timerVal / 2));
        if (PerformanceSettings.getLockFPS() >= 60) {
            if (this.bAlt) {
                ++this.timerVal;
            }
            this.bAlt = !this.bAlt;
        }
        else {
            this.timerVal += 2;
        }
        final float n2 = 1.0f / col0;
        final float n3 = 1.0f / col2;
        ARBShaderObjects.glUniform2fARB(this.TextureSize, (float)col3, (float)col4);
        ARBShaderObjects.glUniform1fARB(this.Zoom, n);
        ARBShaderObjects.glUniform4fARB(this.SearchModeID, textureDraw.vars[6], textureDraw.vars[7], textureDraw.vars[8], textureDraw.vars[9]);
        ARBShaderObjects.glUniform4fARB(this.ScreenInfo, textureDraw.vars[10], textureDraw.vars[11], textureDraw.vars[12], textureDraw.vars[13]);
        ARBShaderObjects.glUniform4fARB(this.ParamInfo, textureDraw.vars[14], textureDraw.vars[15], textureDraw.vars[16], textureDraw.vars[17]);
        ARBShaderObjects.glUniform4fARB(this.VarInfo, textureDraw.vars[18], textureDraw.vars[19], textureDraw.vars[20], textureDraw.vars[21]);
    }
    
    public void onCompileSuccess(final ShaderProgram shaderProgram) {
        final int id = this.getID();
        this.timeOfDay = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"TimeOfDay");
        this.bloom = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"BloomVal");
        this.PixelOffset = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"PixelOffset");
        this.PixelSize = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"PixelSize");
        this.BlurStrength = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"BlurStrength");
        this.width = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"bgl_RenderedTextureWidth");
        this.height = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"bgl_RenderedTextureHeight");
        this.timer = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"timer");
        this.TextureSize = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"TextureSize");
        this.Zoom = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"Zoom");
        this.Light = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"Light");
        this.LightIntensity = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"LightIntensity");
        this.NightValue = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"NightValue");
        this.Exterior = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"Exterior");
        this.NightVisionGoggles = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"NightVisionGoggles");
        this.DesaturationVal = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"DesaturationVal");
        this.FogMod = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"FogMod");
        this.SearchModeID = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"SearchMode");
        this.ScreenInfo = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"ScreenInfo");
        this.ParamInfo = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"ParamInfo");
        this.VarInfo = ARBShaderObjects.glGetUniformLocationARB(id, (CharSequence)"VarInfo");
    }
    
    @Override
    public void postRender(final TextureDraw textureDraw) {
        if (textureDraw.vars != null) {
            returnFloatArray(textureDraw.vars);
            textureDraw.vars = null;
        }
    }
    
    private static float[] getFreeFloatArray() {
        for (int i = 0; i < WeatherShader.floatArrs.length; ++i) {
            if (WeatherShader.floatArrs[i] != null) {
                final float[] array = WeatherShader.floatArrs[i];
                WeatherShader.floatArrs[i] = null;
                return array;
            }
        }
        return new float[22];
    }
    
    private static void returnFloatArray(final float[] array) {
        for (int i = 0; i < WeatherShader.floatArrs.length; ++i) {
            if (WeatherShader.floatArrs[i] == null) {
                WeatherShader.floatArrs[i] = array;
                break;
            }
        }
    }
    
    static {
        WeatherShader.floatArrs = new float[5][];
    }
}
