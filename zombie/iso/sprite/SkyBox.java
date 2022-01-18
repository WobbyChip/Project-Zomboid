// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite;

import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.debug.DebugOptions;
import zombie.core.SpriteRenderer;
import zombie.iso.IsoCamera;
import zombie.core.opengl.RenderThread;
import org.lwjgl.opengl.GL;
import zombie.core.Core;
import zombie.interfaces.ITexture;
import org.joml.Vector3fc;
import zombie.GameTime;
import zombie.iso.weather.ClimateManager;
import zombie.core.textures.Texture;
import zombie.core.Color;
import org.joml.Vector3f;
import zombie.core.utils.UpdateLimit;
import zombie.core.opengl.Shader;
import zombie.core.textures.TextureFBO;
import zombie.iso.IsoObject;

public class SkyBox extends IsoObject
{
    private static SkyBox instance;
    public IsoSpriteInstance def;
    private TextureFBO textureFBOA;
    private TextureFBO textureFBOB;
    private boolean isCurrentA;
    public Shader Effect;
    private final UpdateLimit renderLimit;
    private boolean isUpdated;
    private int SkyBoxTime;
    private float SkyBoxParamCloudCount;
    private float SkyBoxParamCloudSize;
    private final Vector3f SkyBoxParamSunLight;
    private final Color SkyBoxParamSunColor;
    private final Color SkyBoxParamSkyHColour;
    private final Color SkyBoxParamSkyLColour;
    private float SkyBoxParamCloudLight;
    private float SkyBoxParamStars;
    private float SkyBoxParamFog;
    private final Vector3f SkyBoxParamWind;
    private boolean isSetAVG;
    private float SkyBoxParamCloudCountAVG;
    private float SkyBoxParamCloudSizeAVG;
    private final Vector3f SkyBoxParamSunLightAVG;
    private final Color SkyBoxParamSunColorAVG;
    private final Color SkyBoxParamSkyHColourAVG;
    private final Color SkyBoxParamSkyLColourAVG;
    private float SkyBoxParamCloudLightAVG;
    private float SkyBoxParamStarsAVG;
    private float SkyBoxParamFogAVG;
    private final Vector3f SkyBoxParamWindINT;
    private Texture texAM;
    private Texture texPM;
    private final Color SkyHColourDay;
    private final Color SkyHColourDawn;
    private final Color SkyHColourDusk;
    private final Color SkyHColourNight;
    private final Color SkyLColourDay;
    private final Color SkyLColourDawn;
    private final Color SkyLColourDusk;
    private final Color SkyLColourNight;
    private int apiId;
    
    public static synchronized SkyBox getInstance() {
        if (SkyBox.instance == null) {
            SkyBox.instance = new SkyBox();
        }
        return SkyBox.instance;
    }
    
    public void update(final ClimateManager climateManager) {
        if (this.isUpdated) {
            return;
        }
        this.isUpdated = true;
        final GameTime instance = GameTime.getInstance();
        final ClimateManager.DayInfo currentDay = climateManager.getCurrentDay();
        final float dawn = currentDay.season.getDawn();
        final float dusk = currentDay.season.getDusk();
        final float dayHighNoon = currentDay.season.getDayHighNoon();
        final float timeOfDay = instance.getTimeOfDay();
        if (timeOfDay < dawn || timeOfDay > dusk) {
            final float n = 24.0f - dusk + dawn;
            if (timeOfDay > dusk) {
                final float n2 = (timeOfDay - dusk) / n;
                this.SkyHColourDusk.interp(this.SkyHColourDawn, n2, this.SkyBoxParamSkyHColour);
                this.SkyLColourDusk.interp(this.SkyLColourDawn, n2, this.SkyBoxParamSkyLColour);
                this.SkyBoxParamSunLight.set(0.35f, 0.22f, 0.3f);
                this.SkyBoxParamSunLight.normalize();
                this.SkyBoxParamSunLight.mul(Math.min(1.0f, n2 * 5.0f));
            }
            else {
                final float n3 = (24.0f - dusk + timeOfDay) / n;
                this.SkyHColourDusk.interp(this.SkyHColourDawn, n3, this.SkyBoxParamSkyHColour);
                this.SkyLColourDusk.interp(this.SkyLColourDawn, n3, this.SkyBoxParamSkyLColour);
                this.SkyBoxParamSunLight.set(0.35f, 0.22f, 0.3f);
                this.SkyBoxParamSunLight.normalize();
                this.SkyBoxParamSunLight.mul(Math.min(1.0f, (1.0f - n3) * 5.0f));
            }
            this.SkyBoxParamSunColor.set(climateManager.getGlobalLight().getExterior());
            this.SkyBoxParamSunColor.scale(climateManager.getNightStrength());
        }
        else if (timeOfDay < dayHighNoon) {
            final float n4 = (timeOfDay - dawn) / (dayHighNoon - dawn);
            this.SkyHColourDawn.interp(this.SkyHColourDay, n4, this.SkyBoxParamSkyHColour);
            this.SkyLColourDawn.interp(this.SkyLColourDay, n4, this.SkyBoxParamSkyLColour);
            this.SkyBoxParamSunLight.set(4.0f * n4 - 4.0f, 0.22f, 0.3f);
            this.SkyBoxParamSunLight.normalize();
            this.SkyBoxParamSunLight.mul(Math.min(1.0f, n4 * 10.0f));
            this.SkyBoxParamSunColor.set(climateManager.getGlobalLight().getExterior());
        }
        else {
            final float n5 = (timeOfDay - dayHighNoon) / (dusk - dayHighNoon);
            this.SkyHColourDay.interp(this.SkyHColourDusk, n5, this.SkyBoxParamSkyHColour);
            this.SkyLColourDay.interp(this.SkyLColourDusk, n5, this.SkyBoxParamSkyLColour);
            this.SkyBoxParamSunLight.set(4.0f * n5, 0.22f, 0.3f);
            this.SkyBoxParamSunLight.normalize();
            this.SkyBoxParamSunLight.mul(Math.min(1.0f, (1.0f - n5) * 10.0f));
            this.SkyBoxParamSunColor.set(climateManager.getGlobalLight().getExterior());
        }
        this.SkyBoxParamSkyHColour.interp(this.SkyHColourNight, climateManager.getNightStrength(), this.SkyBoxParamSkyHColour);
        this.SkyBoxParamSkyLColour.interp(this.SkyLColourNight, climateManager.getNightStrength(), this.SkyBoxParamSkyLColour);
        this.SkyBoxParamCloudCount = Math.min(Math.max(climateManager.getCloudIntensity(), climateManager.getPrecipitationIntensity() * 2.0f), 0.999f);
        this.SkyBoxParamCloudSize = 0.02f + climateManager.getTemperature() / 70.0f;
        this.SkyBoxParamFog = climateManager.getFogIntensity();
        this.SkyBoxParamStars = climateManager.getNightStrength();
        this.SkyBoxParamCloudLight = (float)(1.0 - (1.0 - 1.0 * Math.pow(1000.0, -climateManager.getPrecipitationIntensity() - climateManager.getNightStrength())));
        final float n6 = (1.0f - (climateManager.getWindAngleIntensity() + 1.0f) * 0.5f + 0.25f) % 1.0f * 360.0f;
        this.SkyBoxParamWind.set((float)Math.cos(Math.toRadians(n6)), 0.0f, (float)Math.sin(Math.toRadians(n6)));
        this.SkyBoxParamWind.mul(climateManager.getWindIntensity());
        if (!this.isSetAVG) {
            this.isSetAVG = true;
            this.SkyBoxParamCloudCountAVG = this.SkyBoxParamCloudCount;
            this.SkyBoxParamCloudSizeAVG = this.SkyBoxParamCloudSize;
            this.SkyBoxParamSunLightAVG.set((Vector3fc)this.SkyBoxParamSunLight);
            this.SkyBoxParamSunColorAVG.set(this.SkyBoxParamSunColor);
            this.SkyBoxParamSkyHColourAVG.set(this.SkyBoxParamSkyHColour);
            this.SkyBoxParamSkyLColourAVG.set(this.SkyBoxParamSkyLColour);
            this.SkyBoxParamCloudLightAVG = this.SkyBoxParamCloudLight;
            this.SkyBoxParamStarsAVG = this.SkyBoxParamStars;
            this.SkyBoxParamFogAVG = this.SkyBoxParamFog;
            this.SkyBoxParamWindINT.set((Vector3fc)this.SkyBoxParamWind);
        }
        else {
            this.SkyBoxParamCloudCountAVG += (this.SkyBoxParamCloudCount - this.SkyBoxParamCloudCountAVG) * 0.1f;
            this.SkyBoxParamCloudSizeAVG += (this.SkyBoxParamCloudSizeAVG + this.SkyBoxParamCloudSize) * 0.1f;
            this.SkyBoxParamSunLightAVG.lerp((Vector3fc)this.SkyBoxParamSunLight, 0.1f);
            this.SkyBoxParamSunColorAVG.interp(this.SkyBoxParamSunColor, 0.1f, this.SkyBoxParamSunColorAVG);
            this.SkyBoxParamSkyHColourAVG.interp(this.SkyBoxParamSkyHColour, 0.1f, this.SkyBoxParamSkyHColourAVG);
            this.SkyBoxParamSkyLColourAVG.interp(this.SkyBoxParamSkyLColour, 0.1f, this.SkyBoxParamSkyLColourAVG);
            this.SkyBoxParamCloudLightAVG += (this.SkyBoxParamCloudLight - this.SkyBoxParamCloudLightAVG) * 0.1f;
            this.SkyBoxParamStarsAVG += (this.SkyBoxParamStars - this.SkyBoxParamStarsAVG) * 0.1f;
            this.SkyBoxParamFogAVG += (this.SkyBoxParamFog - this.SkyBoxParamFogAVG) * 0.1f;
            this.SkyBoxParamWindINT.add((Vector3fc)this.SkyBoxParamWind);
        }
    }
    
    public int getShaderTime() {
        return this.SkyBoxTime;
    }
    
    public float getShaderCloudCount() {
        return this.SkyBoxParamCloudCount;
    }
    
    public float getShaderCloudSize() {
        return this.SkyBoxParamCloudSize;
    }
    
    public Vector3f getShaderSunLight() {
        return this.SkyBoxParamSunLight;
    }
    
    public Color getShaderSunColor() {
        return this.SkyBoxParamSunColor;
    }
    
    public Color getShaderSkyHColour() {
        return this.SkyBoxParamSkyHColour;
    }
    
    public Color getShaderSkyLColour() {
        return this.SkyBoxParamSkyLColour;
    }
    
    public float getShaderCloudLight() {
        return this.SkyBoxParamCloudLight;
    }
    
    public float getShaderStars() {
        return this.SkyBoxParamStars;
    }
    
    public float getShaderFog() {
        return this.SkyBoxParamFog;
    }
    
    public Vector3f getShaderWind() {
        return this.SkyBoxParamWindINT;
    }
    
    public SkyBox() {
        this.def = null;
        this.renderLimit = new UpdateLimit(1000L);
        this.isUpdated = false;
        this.SkyBoxParamSunLight = new Vector3f();
        this.SkyBoxParamSunColor = new Color(1.0f, 1.0f, 1.0f);
        this.SkyBoxParamSkyHColour = new Color(1.0f, 1.0f, 1.0f);
        this.SkyBoxParamSkyLColour = new Color(1.0f, 1.0f, 1.0f);
        this.isSetAVG = false;
        this.SkyBoxParamSunLightAVG = new Vector3f();
        this.SkyBoxParamSunColorAVG = new Color(1.0f, 1.0f, 1.0f);
        this.SkyBoxParamSkyHColourAVG = new Color(1.0f, 1.0f, 1.0f);
        this.SkyBoxParamSkyLColourAVG = new Color(1.0f, 1.0f, 1.0f);
        this.SkyHColourDay = new Color(0.1f, 0.1f, 0.4f);
        this.SkyHColourDawn = new Color(0.2f, 0.2f, 0.3f);
        this.SkyHColourDusk = new Color(0.2f, 0.2f, 0.3f);
        this.SkyHColourNight = new Color(0.01f, 0.01f, 0.04f);
        this.SkyLColourDay = new Color(0.1f, 0.45f, 0.7f);
        this.SkyLColourDawn = new Color(0.1f, 0.4f, 0.6f);
        this.SkyLColourDusk = new Color(0.1f, 0.4f, 0.6f);
        this.SkyLColourNight = new Color(0.01f, 0.045f, 0.07f);
        this.texAM = Texture.getSharedTexture("media/textures/CMVehicleReflection/ref_am.png");
        this.texPM = Texture.getSharedTexture("media/textures/CMVehicleReflection/ref_am.png");
        try {
            final Texture texture = new Texture(512, 512, 16);
            final Texture texture2 = new Texture(512, 512, 16);
            this.textureFBOA = new TextureFBO(texture);
            this.textureFBOB = new TextureFBO(texture2);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        this.def = IsoSpriteInstance.get(this.sprite);
        this.SkyBoxTime = 0;
        this.SkyBoxParamSunLight.set(0.35f, 0.22f, 0.3f);
        this.SkyBoxParamSunColor.set(1.0f, 0.86f, 0.7f, 1.0f);
        this.SkyBoxParamSkyHColour.set(0.1f, 0.1f, 0.4f, 1.0f);
        this.SkyBoxParamSkyLColour.set(0.1f, 0.45f, 0.7f, 1.0f);
        this.SkyBoxParamCloudLight = 0.99f;
        this.SkyBoxParamCloudCount = 0.3f;
        this.SkyBoxParamCloudSize = 0.2f;
        this.SkyBoxParamFog = 0.0f;
        this.SkyBoxParamStars = 0.0f;
        this.SkyBoxParamWind = new Vector3f(0.0f);
        this.SkyBoxParamWindINT = new Vector3f(0.0f);
        RenderThread.invokeOnRenderContext(() -> {
            if (Core.getInstance().getPerfSkybox() == 0) {
                this.Effect = new SkyBoxShader("skybox_hires");
            }
            else {
                this.Effect = new SkyBoxShader("skybox");
            }
            if (GL.getCapabilities().OpenGL30) {
                this.apiId = 1;
            }
            if (GL.getCapabilities().GL_ARB_framebuffer_object) {
                this.apiId = 2;
            }
            if (GL.getCapabilities().GL_EXT_framebuffer_object) {
                this.apiId = 3;
            }
        });
    }
    
    public ITexture getTextureCurrent() {
        if (!Core.getInstance().getUseShaders() || Core.getInstance().getPerfSkybox() == 2) {
            return this.texAM;
        }
        if (this.isCurrentA) {
            return this.textureFBOA.getTexture();
        }
        return this.textureFBOB.getTexture();
    }
    
    public ITexture getTexturePrev() {
        if (!Core.getInstance().getUseShaders() || Core.getInstance().getPerfSkybox() == 2) {
            return this.texPM;
        }
        if (this.isCurrentA) {
            return this.textureFBOB.getTexture();
        }
        return this.textureFBOA.getTexture();
    }
    
    public TextureFBO getTextureFBOPrev() {
        if (!Core.getInstance().getUseShaders() || Core.getInstance().getPerfSkybox() == 2) {
            return null;
        }
        if (this.isCurrentA) {
            return this.textureFBOB;
        }
        return this.textureFBOA;
    }
    
    public float getTextureShift() {
        if (!Core.getInstance().getUseShaders() || Core.getInstance().getPerfSkybox() == 2) {
            return 1.0f - GameTime.getInstance().getNight();
        }
        return (float)this.renderLimit.getTimePeriod();
    }
    
    public void swapTextureFBO() {
        this.renderLimit.updateTimePeriod();
        this.isCurrentA = !this.isCurrentA;
    }
    
    public void render() {
        if (!Core.getInstance().getUseShaders() || Core.getInstance().getPerfSkybox() == 2) {
            return;
        }
        if (!this.renderLimit.Check()) {
            if (GameTime.getInstance().getMultiplier() >= 20.0f) {
                ++this.SkyBoxTime;
            }
            return;
        }
        ++this.SkyBoxTime;
        final int playerIndex = IsoCamera.frameState.playerIndex;
        IsoCamera.getOffscreenLeft(playerIndex);
        IsoCamera.getOffscreenTop(playerIndex);
        IsoCamera.getOffscreenWidth(playerIndex);
        IsoCamera.getOffscreenHeight(playerIndex);
        SpriteRenderer.instance.drawSkyBox(this.Effect, playerIndex, this.apiId, this.getTextureFBOPrev().getBufferId());
        this.isUpdated = false;
    }
    
    public void draw() {
        if (Core.bDebug && DebugOptions.instance.SkyboxShow.getValue()) {
            ((Texture)this.getTextureCurrent()).render(0.0f, 0.0f, 512.0f, 512.0f, 1.0f, 1.0f, 1.0f, 1.0f, null);
        }
    }
}
