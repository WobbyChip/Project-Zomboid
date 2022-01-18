// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather.fx;

import zombie.core.math.PZMath;
import org.joml.Matrix4f;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.shader.ShaderManager;
import zombie.IndieGL;
import zombie.core.SpriteRenderer;
import zombie.core.PerformanceSettings;
import zombie.SandboxOptions;
import zombie.iso.weather.ClimateManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.GameTime;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.IsoCamera;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.core.skinnedmodel.shader.Shader;
import java.util.ArrayList;
import zombie.core.Color;
import zombie.core.textures.Texture;

public class IsoWeatherFX
{
    private static boolean VERBOSE;
    protected static boolean DEBUG_BOUNDS;
    private static float DELTA;
    private ParticleRectangle cloudParticles;
    private ParticleRectangle fogParticles;
    private ParticleRectangle snowParticles;
    private ParticleRectangle rainParticles;
    private static int ID_CLOUD;
    private static int ID_FOG;
    private static int ID_SNOW;
    private static int ID_RAIN;
    public static float ZoomMod;
    protected boolean playerIndoors;
    protected SteppedUpdateFloat windPrecipIntensity;
    protected SteppedUpdateFloat windIntensity;
    protected SteppedUpdateFloat windAngleIntensity;
    protected SteppedUpdateFloat precipitationIntensity;
    protected SteppedUpdateFloat precipitationIntensitySnow;
    protected SteppedUpdateFloat precipitationIntensityRain;
    protected SteppedUpdateFloat cloudIntensity;
    protected SteppedUpdateFloat fogIntensity;
    protected SteppedUpdateFloat windAngleMod;
    protected boolean precipitationIsSnow;
    private float fogOverlayAlpha;
    private float windSpeedMax;
    protected float windSpeed;
    protected float windSpeedFog;
    protected float windAngle;
    protected float windAngleClouds;
    private Texture texFogCircle;
    private Texture texFogWhite;
    private Color fogColor;
    protected SteppedUpdateFloat indoorsAlphaMod;
    private ArrayList<ParticleRectangle> particleRectangles;
    protected static IsoWeatherFX instance;
    private float windUpdCounter;
    static Shader s_shader;
    static final Drawer[][] s_drawer;
    
    public IsoWeatherFX() {
        this.playerIndoors = false;
        this.windPrecipIntensity = new SteppedUpdateFloat(0.0f, 0.025f, 0.0f, 1.0f);
        this.windIntensity = new SteppedUpdateFloat(0.0f, 0.005f, 0.0f, 1.0f);
        this.windAngleIntensity = new SteppedUpdateFloat(0.0f, 0.005f, -1.0f, 1.0f);
        this.precipitationIntensity = new SteppedUpdateFloat(0.0f, 0.005f, 0.0f, 1.0f);
        this.precipitationIntensitySnow = new SteppedUpdateFloat(0.0f, 0.005f, 0.0f, 1.0f);
        this.precipitationIntensityRain = new SteppedUpdateFloat(0.0f, 0.005f, 0.0f, 1.0f);
        this.cloudIntensity = new SteppedUpdateFloat(0.0f, 0.005f, 0.0f, 1.0f);
        this.fogIntensity = new SteppedUpdateFloat(0.0f, 0.005f, 0.0f, 1.0f);
        this.windAngleMod = new SteppedUpdateFloat(0.0f, 0.005f, 0.0f, 1.0f);
        this.precipitationIsSnow = true;
        this.fogOverlayAlpha = 0.0f;
        this.windSpeedMax = 6.0f;
        this.windSpeed = 0.0f;
        this.windSpeedFog = 0.0f;
        this.windAngle = 90.0f;
        this.windAngleClouds = 90.0f;
        this.fogColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.indoorsAlphaMod = new SteppedUpdateFloat(1.0f, 0.05f, 0.0f, 1.0f);
        this.particleRectangles = new ArrayList<ParticleRectangle>(0);
        this.windUpdCounter = 0.0f;
        IsoWeatherFX.instance = this;
    }
    
    public void init() {
        if (GameServer.bServer) {
            return;
        }
        int n = 0;
        final Texture[] array = new Texture[6];
        for (int i = 0; i < array.length; ++i) {
            array[i] = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            if (array[i] == null) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            }
        }
        this.cloudParticles = new ParticleRectangle(8192, 4096);
        final WeatherParticle[] array2 = new WeatherParticle[16];
        for (int j = 0; j < array2.length; ++j) {
            final Texture texture = array[Rand.Next(array.length)];
            final CloudParticle cloudParticle = new CloudParticle(texture, texture.getWidth() * 8, texture.getHeight() * 8);
            cloudParticle.position.set((float)Rand.Next(0, this.cloudParticles.getWidth()), (float)Rand.Next(0, this.cloudParticles.getHeight()));
            cloudParticle.speed = Rand.Next(0.01f, 0.1f);
            cloudParticle.angleOffset = 180.0f - Rand.Next(0.0f, 360.0f);
            cloudParticle.alpha = Rand.Next(0.25f, 0.75f);
            array2[j] = cloudParticle;
        }
        this.cloudParticles.SetParticles(array2);
        this.cloudParticles.SetParticlesStrength(1.0f);
        this.particleRectangles.add(n, this.cloudParticles);
        IsoWeatherFX.ID_CLOUD = n++;
        if (this.texFogCircle == null) {
            this.texFogCircle = Texture.getSharedTexture("media/textures/weather/fogcircle_tex.png", 35);
        }
        if (this.texFogWhite == null) {
            this.texFogWhite = Texture.getSharedTexture("media/textures/weather/fogwhite_tex.png", 35);
        }
        final Texture[] array3 = new Texture[6];
        for (int k = 0; k < array3.length; ++k) {
            array3[k] = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, k));
            if (array3[k] == null) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, k));
            }
        }
        this.fogParticles = new ParticleRectangle(2048, 1024);
        final WeatherParticle[] array4 = new WeatherParticle[16];
        for (int l = 0; l < array4.length; ++l) {
            final Texture texture2 = array3[Rand.Next(array3.length)];
            final FogParticle fogParticle = new FogParticle(texture2, texture2.getWidth() * 2, texture2.getHeight() * 2);
            fogParticle.position.set((float)Rand.Next(0, this.fogParticles.getWidth()), (float)Rand.Next(0, this.fogParticles.getHeight()));
            fogParticle.speed = Rand.Next(0.01f, 0.1f);
            fogParticle.angleOffset = 180.0f - Rand.Next(0.0f, 360.0f);
            fogParticle.alpha = Rand.Next(0.05f, 0.25f);
            array4[l] = fogParticle;
        }
        this.fogParticles.SetParticles(array4);
        this.fogParticles.SetParticlesStrength(1.0f);
        this.particleRectangles.add(n, this.fogParticles);
        IsoWeatherFX.ID_FOG = n++;
        final Texture[] array5 = new Texture[3];
        for (int n2 = 0; n2 < array5.length; ++n2) {
            array5[n2] = Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2 + 1));
            if (array5[n2] == null) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2 + 1));
            }
        }
        this.snowParticles = new ParticleRectangle(512, 512);
        final WeatherParticle[] array6 = new WeatherParticle[1024];
        for (int n3 = 0; n3 < array6.length; ++n3) {
            final SnowParticle snowParticle = new SnowParticle(array5[Rand.Next(array5.length)]);
            snowParticle.position.set((float)Rand.Next(0, this.snowParticles.getWidth()), (float)Rand.Next(0, this.snowParticles.getHeight()));
            snowParticle.speed = Rand.Next(1.0f, 2.0f);
            snowParticle.angleOffset = 15.0f - Rand.Next(0.0f, 30.0f);
            snowParticle.alpha = Rand.Next(0.25f, 0.6f);
            array6[n3] = snowParticle;
        }
        this.snowParticles.SetParticles(array6);
        this.particleRectangles.add(n, this.snowParticles);
        IsoWeatherFX.ID_SNOW = n++;
        this.rainParticles = new ParticleRectangle(512, 512);
        final WeatherParticle[] array7 = new WeatherParticle[1024];
        for (int n4 = 0; n4 < array7.length; ++n4) {
            final RainParticle rainParticle = new RainParticle(this.texFogWhite, Rand.Next(5, 12));
            rainParticle.position.set((float)Rand.Next(0, this.rainParticles.getWidth()), (float)Rand.Next(0, this.rainParticles.getHeight()));
            rainParticle.speed = (float)Rand.Next(7, 12);
            rainParticle.angleOffset = 3.0f - Rand.Next(0.0f, 6.0f);
            rainParticle.alpha = Rand.Next(0.5f, 0.8f);
            rainParticle.color = new Color(Rand.Next(0.75f, 0.8f), Rand.Next(0.85f, 0.9f), Rand.Next(0.95f, 1.0f), 1.0f);
            array7[n4] = rainParticle;
        }
        this.rainParticles.SetParticles(array7);
        this.particleRectangles.add(n, this.rainParticles);
        IsoWeatherFX.ID_RAIN = n++;
    }
    
    public void update() {
        if (GameServer.bServer) {
            return;
        }
        this.playerIndoors = (IsoCamera.frameState.CamCharacterSquare != null && !IsoCamera.frameState.CamCharacterSquare.Is(IsoFlagType.exterior));
        IsoWeatherFX.DELTA = GameTime.getInstance().getMultiplier();
        if (!WeatherFxMask.playerHasMaskToDraw(IsoCamera.frameState.playerIndex)) {
            if (this.playerIndoors && this.indoorsAlphaMod.value() > 0.0f) {
                this.indoorsAlphaMod.setTarget(this.indoorsAlphaMod.value() - 0.05f * IsoWeatherFX.DELTA);
            }
            else if (!this.playerIndoors && this.indoorsAlphaMod.value() < 1.0f) {
                this.indoorsAlphaMod.setTarget(this.indoorsAlphaMod.value() + 0.05f * IsoWeatherFX.DELTA);
            }
        }
        else {
            this.indoorsAlphaMod.setTarget(1.0f);
        }
        this.indoorsAlphaMod.update(IsoWeatherFX.DELTA);
        this.cloudIntensity.update(IsoWeatherFX.DELTA);
        this.windIntensity.update(IsoWeatherFX.DELTA);
        this.windPrecipIntensity.update(IsoWeatherFX.DELTA);
        this.windAngleIntensity.update(IsoWeatherFX.DELTA);
        this.precipitationIntensity.update(IsoWeatherFX.DELTA);
        this.fogIntensity.update(IsoWeatherFX.DELTA);
        if (this.precipitationIsSnow) {
            this.precipitationIntensitySnow.setTarget(this.precipitationIntensity.getTarget());
        }
        else {
            this.precipitationIntensitySnow.setTarget(0.0f);
        }
        if (!this.precipitationIsSnow) {
            this.precipitationIntensityRain.setTarget(this.precipitationIntensity.getTarget());
        }
        else {
            this.precipitationIntensityRain.setTarget(0.0f);
        }
        if (this.precipitationIsSnow) {
            this.windAngleMod.setTarget(0.3f);
        }
        else {
            this.windAngleMod.setTarget(0.6f);
        }
        this.precipitationIntensitySnow.update(IsoWeatherFX.DELTA);
        this.precipitationIntensityRain.update(IsoWeatherFX.DELTA);
        this.windAngleMod.update(IsoWeatherFX.DELTA);
        this.fogOverlayAlpha = 0.8f * (this.fogIntensity.value() * this.indoorsAlphaMod.value());
        final float windUpdCounter = this.windUpdCounter + 1.0f;
        this.windUpdCounter = windUpdCounter;
        if (windUpdCounter > 15.0f) {
            this.windUpdCounter = 0.0f;
            if (this.windAngleIntensity.value() > 0.0f) {
                this.windAngle = lerp(this.windPrecipIntensity.value(), 90.0f, 0.0f + 54.0f * this.windAngleMod.value());
                if (this.windAngleIntensity.value() < 0.5f) {
                    this.windAngleClouds = lerp(this.windAngleIntensity.value() * 2.0f, 90.0f, 0.0f);
                }
                else {
                    this.windAngleClouds = lerp((this.windAngleIntensity.value() - 0.5f) * 2.0f, 360.0f, 270.0f);
                }
            }
            else if (this.windAngleIntensity.value() < 0.0f) {
                this.windAngle = lerp(Math.abs(this.windPrecipIntensity.value()), 90.0f, 180.0f - 54.0f * this.windAngleMod.value());
                this.windAngleClouds = lerp(Math.abs(this.windAngleIntensity.value()), 90.0f, 270.0f);
            }
            else {
                this.windAngle = 90.0f;
            }
            this.windSpeed = this.windSpeedMax * this.windPrecipIntensity.value();
            this.windSpeedFog = this.windSpeedMax * this.windIntensity.value() * (4.0f + 16.0f * Math.abs(this.windAngleIntensity.value()));
            if (this.windSpeed < 1.0f) {
                this.windSpeed = 1.0f;
            }
            if (this.windSpeedFog < 1.0f) {
                this.windSpeedFog = 1.0f;
            }
        }
        final float zoom = Core.getInstance().getZoom(IsoPlayer.getInstance().getPlayerNum());
        final float n = 1.0f - (zoom - 0.5f) * 0.5f * 0.75f;
        IsoWeatherFX.ZoomMod = 0.0f;
        if (Core.getInstance().isZoomEnabled() && zoom > 1.0f) {
            IsoWeatherFX.ZoomMod = ClimateManager.clamp(0.0f, 1.0f, (zoom - 1.0f) * 0.6666667f);
        }
        if (this.cloudIntensity.value() <= 0.0f) {
            this.cloudParticles.SetParticlesStrength(0.0f);
        }
        else {
            this.cloudParticles.SetParticlesStrength(1.0f);
        }
        if (this.fogIntensity.value() <= 0.0f) {
            this.fogParticles.SetParticlesStrength(0.0f);
        }
        else {
            this.fogParticles.SetParticlesStrength(1.0f);
        }
        this.snowParticles.SetParticlesStrength(this.precipitationIntensitySnow.value() * n);
        this.rainParticles.SetParticlesStrength(this.precipitationIntensityRain.value() * n);
        for (int i = 0; i < this.particleRectangles.size(); ++i) {
            if (this.particleRectangles.get(i).requiresUpdate()) {
                this.particleRectangles.get(i).update(IsoWeatherFX.DELTA);
            }
        }
    }
    
    public void setDebugBounds(final boolean debug_BOUNDS) {
        IsoWeatherFX.DEBUG_BOUNDS = debug_BOUNDS;
    }
    
    public boolean isDebugBounds() {
        return IsoWeatherFX.DEBUG_BOUNDS;
    }
    
    public void setWindAngleIntensity(final float target) {
        this.windAngleIntensity.setTarget(target);
        if (IsoWeatherFX.VERBOSE) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.windAngleIntensity.getTarget()));
        }
    }
    
    public float getWindAngleIntensity() {
        return this.windAngleIntensity.value();
    }
    
    public float getRenderWindAngleRain() {
        return this.windAngle;
    }
    
    public void setWindPrecipIntensity(final float target) {
        this.windPrecipIntensity.setTarget(target);
        if (IsoWeatherFX.VERBOSE) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.windPrecipIntensity.getTarget()));
        }
    }
    
    public float getWindPrecipIntensity() {
        return this.windPrecipIntensity.value();
    }
    
    public void setWindIntensity(final float target) {
        this.windIntensity.setTarget(target);
        if (IsoWeatherFX.VERBOSE) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.windIntensity.getTarget()));
        }
    }
    
    public float getWindIntensity() {
        return this.windIntensity.value();
    }
    
    public void setFogIntensity(float target) {
        if (SandboxOptions.instance.MaxFogIntensity.getValue() == 2) {
            target = Math.min(target, 0.75f);
        }
        else if (SandboxOptions.instance.MaxFogIntensity.getValue() == 3) {
            target = Math.min(target, 0.5f);
        }
        this.fogIntensity.setTarget(target);
        if (IsoWeatherFX.VERBOSE) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.fogIntensity.getTarget()));
        }
    }
    
    public float getFogIntensity() {
        return this.fogIntensity.value();
    }
    
    public void setCloudIntensity(final float target) {
        this.cloudIntensity.setTarget(target);
        if (IsoWeatherFX.VERBOSE) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.cloudIntensity.getTarget()));
        }
    }
    
    public float getCloudIntensity() {
        return this.cloudIntensity.value();
    }
    
    public void setPrecipitationIntensity(float target) {
        if (SandboxOptions.instance.MaxRainFxIntensity.getValue() == 2) {
            target *= 0.75f;
        }
        else if (SandboxOptions.instance.MaxRainFxIntensity.getValue() == 3) {
            target *= 0.5f;
        }
        if (target > 0.0f) {
            target = 0.05f + 0.95f * target;
        }
        this.precipitationIntensity.setTarget(target);
        if (IsoWeatherFX.VERBOSE) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.precipitationIntensity.getTarget()));
        }
    }
    
    public float getPrecipitationIntensity() {
        return this.precipitationIntensity.value();
    }
    
    public void setPrecipitationIsSnow(final boolean precipitationIsSnow) {
        this.precipitationIsSnow = precipitationIsSnow;
    }
    
    public boolean getPrecipitationIsSnow() {
        return this.precipitationIsSnow;
    }
    
    public boolean hasCloudsToRender() {
        return this.cloudIntensity.value() > 0.0f || this.particleRectangles.get(IsoWeatherFX.ID_CLOUD).requiresUpdate();
    }
    
    public boolean hasPrecipitationToRender() {
        return this.precipitationIntensity.value() > 0.0f || this.particleRectangles.get(IsoWeatherFX.ID_SNOW).requiresUpdate() || this.particleRectangles.get(IsoWeatherFX.ID_RAIN).requiresUpdate();
    }
    
    public boolean hasFogToRender() {
        return this.fogIntensity.value() > 0.0f || this.particleRectangles.get(IsoWeatherFX.ID_FOG).requiresUpdate();
    }
    
    public void render() {
        if (GameServer.bServer) {
            return;
        }
        for (int i = 0; i < this.particleRectangles.size(); ++i) {
            if (i == IsoWeatherFX.ID_FOG) {
                if (PerformanceSettings.FogQuality != 2) {
                    continue;
                }
                this.renderFogCircle();
            }
            if ((i != IsoWeatherFX.ID_RAIN && i != IsoWeatherFX.ID_SNOW) || Core.OptionRenderPrecipitation <= 2) {
                if (this.particleRectangles.get(i).requiresUpdate()) {
                    this.particleRectangles.get(i).render();
                }
            }
        }
    }
    
    public void renderLayered(final boolean b, final boolean b2, final boolean b3) {
        if (b) {
            this.renderClouds();
        }
        else if (b2) {
            this.renderFog();
        }
        else if (b3) {
            this.renderPrecipitation();
        }
    }
    
    public void renderClouds() {
        if (GameServer.bServer) {
            return;
        }
        if (this.particleRectangles.get(IsoWeatherFX.ID_CLOUD).requiresUpdate()) {
            this.particleRectangles.get(IsoWeatherFX.ID_CLOUD).render();
        }
    }
    
    public void renderFog() {
        if (GameServer.bServer) {
            return;
        }
        this.renderFogCircle();
        if (this.particleRectangles.get(IsoWeatherFX.ID_FOG).requiresUpdate()) {
            this.particleRectangles.get(IsoWeatherFX.ID_FOG).render();
        }
    }
    
    public void renderPrecipitation() {
        if (GameServer.bServer) {
            return;
        }
        if (this.particleRectangles.get(IsoWeatherFX.ID_SNOW).requiresUpdate()) {
            this.particleRectangles.get(IsoWeatherFX.ID_SNOW).render();
        }
        if (this.particleRectangles.get(IsoWeatherFX.ID_RAIN).requiresUpdate()) {
            this.particleRectangles.get(IsoWeatherFX.ID_RAIN).render();
        }
    }
    
    private void renderFogCircle() {
        if (this.fogOverlayAlpha <= 0.0f) {
            return;
        }
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final float currentPlayerZoom = Core.getInstance().getCurrentPlayerZoom();
        final int screenWidth = IsoCamera.getScreenWidth(playerIndex);
        final int screenHeight = IsoCamera.getScreenHeight(playerIndex);
        final int n = 2048 - (int)(512.0f * this.fogIntensity.value());
        final int n2 = 1024 - (int)(256.0f * this.fogIntensity.value());
        final int n3 = (int)(n / currentPlayerZoom);
        final int n4 = (int)(n2 / currentPlayerZoom);
        final int n5 = screenWidth / 2 - n3 / 2;
        final int n6 = screenHeight / 2 - n4 / 2;
        final int n7 = (int)(n5 - IsoCamera.getRightClickOffX() / currentPlayerZoom);
        final int n8 = (int)(n6 - IsoCamera.getRightClickOffY() / currentPlayerZoom);
        final int n9 = n7 + n3;
        final int n10 = n8 + n4;
        SpriteRenderer.instance.glBind(this.texFogWhite.getID());
        IndieGL.glTexParameteri(3553, 10241, 9728);
        IndieGL.glTexParameteri(3553, 10240, 9728);
        if (IsoWeatherFX.s_shader == null) {
            RenderThread.invokeOnRenderContext(() -> IsoWeatherFX.s_shader = ShaderManager.instance.getOrCreateShader("fogCircle", false));
        }
        if (IsoWeatherFX.s_shader.getShaderProgram().isCompiled()) {
            IndieGL.StartShader(IsoWeatherFX.s_shader.getID(), playerIndex);
            final int mainStateIndex = SpriteRenderer.instance.getMainStateIndex();
            if (IsoWeatherFX.s_drawer[playerIndex][mainStateIndex] == null) {
                IsoWeatherFX.s_drawer[playerIndex][mainStateIndex] = new Drawer();
            }
            IsoWeatherFX.s_drawer[playerIndex][mainStateIndex].init(screenWidth, screenHeight);
        }
        SpriteRenderer.instance.renderi(this.texFogCircle, n7, n8, n3, n4, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha, null);
        SpriteRenderer.instance.renderi(this.texFogWhite, 0, 0, n7, screenHeight, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha, null);
        SpriteRenderer.instance.renderi(this.texFogWhite, n7, 0, n3, n8, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha, null);
        SpriteRenderer.instance.renderi(this.texFogWhite, n9, 0, screenWidth - n9, screenHeight, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha, null);
        SpriteRenderer.instance.renderi(this.texFogWhite, n7, n10, n3, screenHeight - n10, this.fogColor.r, this.fogColor.g, this.fogColor.b, this.fogOverlayAlpha, null);
        if (IsoWeatherFX.s_shader.getShaderProgram().isCompiled()) {
            IndieGL.EndShader();
        }
        if (Core.getInstance().getOffscreenBuffer() != null) {
            if (Core.getInstance().isZoomEnabled() && Core.getInstance().getZoom(playerIndex) > 0.5f) {
                IndieGL.glTexParameteri(3553, 10241, 9729);
            }
            else {
                IndieGL.glTexParameteri(3553, 10241, 9728);
            }
            if (Core.getInstance().getZoom(playerIndex) == 0.5f) {
                IndieGL.glTexParameteri(3553, 10240, 9728);
            }
            else {
                IndieGL.glTexParameteri(3553, 10240, 9729);
            }
        }
    }
    
    public static float clamp(final float a, final float a2, float n) {
        n = Math.min(a2, n);
        n = Math.max(a, n);
        return n;
    }
    
    public static float lerp(final float n, final float n2, final float n3) {
        return n2 + n * (n3 - n2);
    }
    
    public static float clerp(final float n, final float n2, final float n3) {
        final float n4 = (float)(1.0 - Math.cos(n * 3.141592653589793)) / 2.0f;
        return n2 * (1.0f - n4) + n3 * n4;
    }
    
    static {
        IsoWeatherFX.VERBOSE = false;
        IsoWeatherFX.DEBUG_BOUNDS = false;
        IsoWeatherFX.ID_CLOUD = 0;
        IsoWeatherFX.ID_FOG = 1;
        IsoWeatherFX.ID_SNOW = 2;
        IsoWeatherFX.ID_RAIN = 3;
        IsoWeatherFX.ZoomMod = 1.0f;
        s_drawer = new Drawer[4][3];
    }
    
    private static final class Drawer extends TextureDraw.GenericDrawer
    {
        static final Matrix4f s_matrix4f;
        final org.lwjgl.util.vector.Matrix4f m_mvp;
        int m_width;
        int m_height;
        boolean m_bSet;
        
        private Drawer() {
            this.m_mvp = new org.lwjgl.util.vector.Matrix4f();
            this.m_bSet = false;
        }
        
        void init(final int width, final int height) {
            if (width == this.m_width && height == this.m_height && this.m_bSet) {
                return;
            }
            this.m_width = width;
            this.m_height = height;
            this.m_bSet = false;
            Drawer.s_matrix4f.setOrtho(0.0f, (float)this.m_width, (float)this.m_height, 0.0f, -1.0f, 1.0f);
            PZMath.convertMatrix(Drawer.s_matrix4f, this.m_mvp);
            this.m_mvp.transpose();
            SpriteRenderer.instance.drawGeneric(this);
        }
        
        @Override
        public void render() {
            IsoWeatherFX.s_shader.getShaderProgram().setValue("u_mvp", this.m_mvp);
            this.m_bSet = true;
        }
        
        static {
            s_matrix4f = new Matrix4f();
        }
    }
}
