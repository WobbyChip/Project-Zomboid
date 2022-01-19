// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.opengl;

import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.IndieGL;
import zombie.core.SpriteRenderer;
import zombie.iso.IsoObject;
import zombie.iso.IsoGridSquare;
import zombie.GameTime;
import zombie.debug.DebugOptions;
import zombie.iso.weather.ClimateMoon;
import zombie.SandboxOptions;
import zombie.iso.weather.WorldFlares;
import zombie.iso.weather.ClimateManager;
import zombie.iso.SearchMode;
import zombie.core.textures.ColorInfo;
import zombie.iso.weather.ClimateColorInfo;
import zombie.core.Core;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.debug.DebugLog;
import zombie.core.Color;
import zombie.core.textures.Texture;

public final class RenderSettings
{
    private static RenderSettings instance;
    private static Texture texture;
    private static final float AMBIENT_MIN_SHADER = 0.4f;
    private static final float AMBIENT_MAX_SHADER = 1.0f;
    private static final float AMBIENT_MIN_LEGACY = 0.4f;
    private static final float AMBIENT_MAX_LEGACY = 1.0f;
    private final PlayerRenderSettings[] playerSettings;
    private Color defaultClear;
    
    public static RenderSettings getInstance() {
        if (RenderSettings.instance == null) {
            RenderSettings.instance = new RenderSettings();
        }
        return RenderSettings.instance;
    }
    
    public RenderSettings() {
        this.playerSettings = new PlayerRenderSettings[4];
        this.defaultClear = new Color(0, 0, 0, 1);
        for (int i = 0; i < this.playerSettings.length; ++i) {
            this.playerSettings[i] = new PlayerRenderSettings();
        }
        RenderSettings.texture = Texture.getSharedTexture("media/textures/weather/fogwhite.png");
        if (RenderSettings.texture == null) {
            DebugLog.log("Missing texture: media/textures/weather/fogwhite.png");
        }
    }
    
    public PlayerRenderSettings getPlayerSettings(final int n) {
        return this.playerSettings[n];
    }
    
    public void update() {
        if (GameServer.bServer) {
            return;
        }
        for (int i = 0; i < 4; ++i) {
            if (IsoPlayer.players[i] != null) {
                this.playerSettings[i].updateRenderSettings(i, IsoPlayer.players[i]);
            }
        }
    }
    
    public void applyRenderSettings(final int n) {
        if (GameServer.bServer) {
            return;
        }
        this.getPlayerSettings(n).applyRenderSettings(n);
    }
    
    public void legacyPostRender(final int n) {
        if (GameServer.bServer) {
            return;
        }
        if (Core.getInstance().RenderShader == null || Core.getInstance().getOffscreenBuffer() == null) {
            this.getPlayerSettings(n).legacyPostRender(n);
        }
    }
    
    public float getAmbientForPlayer(final int n) {
        final PlayerRenderSettings playerSettings = this.getPlayerSettings(n);
        if (playerSettings != null) {
            return playerSettings.getAmbient();
        }
        return 0.0f;
    }
    
    public Color getMaskClearColorForPlayer(final int n) {
        final PlayerRenderSettings playerSettings = this.getPlayerSettings(n);
        if (playerSettings != null) {
            return playerSettings.getMaskClearColor();
        }
        return this.defaultClear;
    }
    
    public static class PlayerRenderSettings
    {
        public ClimateColorInfo CM_GlobalLight;
        public float CM_NightStrength;
        public float CM_Desaturation;
        public float CM_GlobalLightIntensity;
        public float CM_Ambient;
        public float CM_ViewDistance;
        public float CM_DayLightStrength;
        public float CM_FogIntensity;
        private Color blendColor;
        private ColorInfo blendInfo;
        private float blendIntensity;
        private float desaturation;
        private float darkness;
        private float night;
        private float viewDistance;
        private float ambient;
        private boolean applyNightVisionGoggles;
        private float goggleMod;
        private boolean isExterior;
        private float fogMod;
        private float rmod;
        private float gmod;
        private float bmod;
        private float SM_Radius;
        private float SM_Alpha;
        private Color maskClearColor;
        
        public PlayerRenderSettings() {
            this.CM_GlobalLight = new ClimateColorInfo();
            this.CM_NightStrength = 0.0f;
            this.CM_Desaturation = 0.0f;
            this.CM_GlobalLightIntensity = 0.0f;
            this.CM_Ambient = 0.0f;
            this.CM_ViewDistance = 0.0f;
            this.CM_DayLightStrength = 0.0f;
            this.CM_FogIntensity = 0.0f;
            this.blendColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
            this.blendInfo = new ColorInfo();
            this.blendIntensity = 0.0f;
            this.desaturation = 0.0f;
            this.darkness = 0.0f;
            this.night = 0.0f;
            this.viewDistance = 0.0f;
            this.ambient = 0.0f;
            this.applyNightVisionGoggles = false;
            this.goggleMod = 0.0f;
            this.isExterior = false;
            this.fogMod = 1.0f;
            this.SM_Radius = 0.0f;
            this.SM_Alpha = 0.0f;
            this.maskClearColor = new Color(0, 0, 0, 1);
        }
        
        private void updateRenderSettings(final int n, final IsoPlayer isoPlayer) {
            SearchMode.getInstance();
            this.SM_Alpha = 0.0f;
            this.SM_Radius = 0.0f;
            final ClimateManager instance = ClimateManager.getInstance();
            this.CM_GlobalLight = instance.getGlobalLight();
            this.CM_GlobalLightIntensity = instance.getGlobalLightIntensity();
            this.CM_Ambient = instance.getAmbient();
            this.CM_DayLightStrength = instance.getDayLightStrength();
            this.CM_NightStrength = instance.getNightStrength();
            this.CM_Desaturation = instance.getDesaturation();
            this.CM_ViewDistance = instance.getViewDistance();
            this.CM_FogIntensity = instance.getFogIntensity();
            instance.getThunderStorm().applyLightningForPlayer(this, n, isoPlayer);
            WorldFlares.applyFlaresForPlayer(this, n, isoPlayer);
            int value = SandboxOptions.instance.NightDarkness.getValue();
            this.desaturation = this.CM_Desaturation;
            this.viewDistance = this.CM_ViewDistance;
            this.applyNightVisionGoggles = (isoPlayer != null && isoPlayer.isWearingNightVisionGoggles());
            this.isExterior = (isoPlayer != null && isoPlayer.getCurrentSquare() != null && !isoPlayer.getCurrentSquare().isInARoom());
            this.fogMod = 1.0f - this.CM_FogIntensity * 0.5f;
            this.night = this.CM_NightStrength;
            this.darkness = 1.0f - this.CM_DayLightStrength;
            if (this.isExterior) {
                this.setBlendColor(this.CM_GlobalLight.getExterior());
                this.blendIntensity = this.CM_GlobalLight.getExterior().a;
            }
            else {
                this.setBlendColor(this.CM_GlobalLight.getInterior());
                this.blendIntensity = this.CM_GlobalLight.getInterior().a;
            }
            this.ambient = this.CM_Ambient;
            this.viewDistance = this.CM_ViewDistance;
            --value;
            float n2 = 0.2f + 0.1f * value + 0.075f * ClimateMoon.getMoonFloat() * this.night;
            if (!this.isExterior) {
                n2 *= 0.925f - 0.075f * this.darkness;
                this.desaturation *= 0.25f;
            }
            if (this.ambient < 0.2f && isoPlayer.getCharacterTraits().NightVision.isSet()) {
                this.ambient = 0.2f;
            }
            this.ambient = n2 + (1.0f - n2) * this.ambient;
            if (Core.bLastStand) {
                this.ambient = 0.65f;
                this.darkness = 0.25f;
                this.night = 0.25f;
            }
            if (DebugOptions.instance.MultiplayerLightAmbient.getValue()) {
                this.ambient = 0.99f;
                this.darkness = 0.01f;
                this.night = 0.01f;
            }
            if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                if (this.applyNightVisionGoggles) {
                    this.ambient = 1.0f;
                    this.rmod = GameTime.getInstance().Lerp(1.0f, 0.7f, this.darkness);
                    this.gmod = GameTime.getInstance().Lerp(1.0f, 0.7f, this.darkness);
                    this.bmod = GameTime.getInstance().Lerp(1.0f, 0.7f, this.darkness);
                    this.maskClearColor.r = 0.0f;
                    this.maskClearColor.g = 0.0f;
                    this.maskClearColor.b = 0.0f;
                    this.maskClearColor.a = 0.0f;
                }
                else {
                    this.rmod = 1.0f;
                    this.gmod = 1.0f;
                    this.bmod = 1.0f;
                    if (!this.isExterior) {
                        this.maskClearColor.r = this.CM_GlobalLight.getInterior().r;
                        this.maskClearColor.g = this.CM_GlobalLight.getInterior().g;
                        this.maskClearColor.b = this.CM_GlobalLight.getInterior().b;
                        this.maskClearColor.a = this.CM_GlobalLight.getInterior().a;
                    }
                    else {
                        this.maskClearColor.r = 0.0f;
                        this.maskClearColor.g = 0.0f;
                        this.maskClearColor.b = 0.0f;
                        this.maskClearColor.a = 0.0f;
                    }
                }
            }
            else {
                this.desaturation *= 1.0f - this.darkness;
                this.blendInfo.r = this.blendColor.r;
                this.blendInfo.g = this.blendColor.g;
                this.blendInfo.b = this.blendColor.b;
                this.blendInfo.desaturate(this.desaturation);
                this.rmod = GameTime.getInstance().Lerp(1.0f, this.blendInfo.r, this.blendIntensity);
                this.gmod = GameTime.getInstance().Lerp(1.0f, this.blendInfo.g, this.blendIntensity);
                this.bmod = GameTime.getInstance().Lerp(1.0f, this.blendInfo.b, this.blendIntensity);
                if (this.applyNightVisionGoggles) {
                    this.goggleMod = 1.0f - 0.9f * this.darkness;
                    this.blendIntensity = 0.0f;
                    this.night = 0.0f;
                    this.ambient = 0.8f;
                    this.rmod = 1.0f;
                    this.gmod = 1.0f;
                    this.bmod = 1.0f;
                }
            }
        }
        
        private void applyRenderSettings(final int n) {
            IsoGridSquare.rmod = this.rmod;
            IsoGridSquare.gmod = this.gmod;
            IsoGridSquare.bmod = this.bmod;
            IsoObject.rmod = this.rmod;
            IsoObject.gmod = this.gmod;
            IsoObject.bmod = this.bmod;
        }
        
        private void legacyPostRender(final int n) {
            SpriteRenderer.instance.glIgnoreStyles(true);
            if (this.applyNightVisionGoggles) {
                IndieGL.glBlendFunc(770, 768);
                SpriteRenderer.instance.render(RenderSettings.texture, 0.0f, 0.0f, (float)Core.getInstance().getOffscreenWidth(n), (float)Core.getInstance().getOffscreenHeight(n), 0.05f, 0.95f, 0.05f, this.goggleMod, null);
                IndieGL.glBlendFunc(770, 771);
            }
            else {
                IndieGL.glBlendFunc(774, 774);
                SpriteRenderer.instance.render(RenderSettings.texture, 0.0f, 0.0f, (float)Core.getInstance().getOffscreenWidth(n), (float)Core.getInstance().getOffscreenHeight(n), this.blendInfo.r, this.blendInfo.g, this.blendInfo.b, 1.0f, null);
                IndieGL.glBlendFunc(770, 771);
            }
            SpriteRenderer.instance.glIgnoreStyles(false);
        }
        
        public Color getBlendColor() {
            return this.blendColor;
        }
        
        public float getBlendIntensity() {
            return this.blendIntensity;
        }
        
        public float getDesaturation() {
            return this.desaturation;
        }
        
        public float getDarkness() {
            return this.darkness;
        }
        
        public float getNight() {
            return this.night;
        }
        
        public float getViewDistance() {
            return this.viewDistance;
        }
        
        public float getAmbient() {
            return this.ambient;
        }
        
        public boolean isApplyNightVisionGoggles() {
            return this.applyNightVisionGoggles;
        }
        
        public float getRmod() {
            return this.rmod;
        }
        
        public float getGmod() {
            return this.gmod;
        }
        
        public float getBmod() {
            return this.bmod;
        }
        
        public boolean isExterior() {
            return this.isExterior;
        }
        
        public float getFogMod() {
            return this.fogMod;
        }
        
        private void setBlendColor(final Color color) {
            this.blendColor.a = color.a;
            this.blendColor.r = color.r;
            this.blendColor.g = color.g;
            this.blendColor.b = color.b;
        }
        
        public Color getMaskClearColor() {
            return this.maskClearColor;
        }
        
        public float getSM_Radius() {
            return this.SM_Radius;
        }
        
        public float getSM_Alpha() {
            return this.SM_Alpha;
        }
    }
}
