// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.weather;

import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.iso.Vector2;
import zombie.core.Rand;
import zombie.GameTime;
import zombie.iso.weather.fx.SteppedUpdateFloat;
import zombie.debug.LineDrawer;
import zombie.iso.IsoUtils;
import zombie.characters.IsoPlayer;
import zombie.core.opengl.RenderSettings;
import java.util.ArrayList;

public class WorldFlares
{
    public static final boolean ENABLED = true;
    public static boolean DEBUG_DRAW;
    public static int NEXT_ID;
    private static ArrayList<Flare> flares;
    
    public static void Clear() {
        WorldFlares.flares.clear();
    }
    
    public static int getFlareCount() {
        return WorldFlares.flares.size();
    }
    
    public static Flare getFlare(final int index) {
        return WorldFlares.flares.get(index);
    }
    
    public static Flare getFlareID(final int n) {
        for (int i = 0; i < WorldFlares.flares.size(); ++i) {
            if (WorldFlares.flares.get(i).id == n) {
                return WorldFlares.flares.get(i);
            }
        }
        return null;
    }
    
    public static void launchFlare(final float maxLifeTime, final int n, final int n2, final int range, final float windSpeed, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        if (WorldFlares.flares.size() > 100) {
            WorldFlares.flares.remove(0);
        }
        final Flare e = new Flare();
        e.id = WorldFlares.NEXT_ID++;
        e.x = (float)n;
        e.y = (float)n2;
        e.range = range;
        e.windSpeed = windSpeed;
        e.color.setExterior(n3, n4, n5, 1.0f);
        e.color.setInterior(n6, n7, n8, 1.0f);
        e.hasLaunched = true;
        e.maxLifeTime = maxLifeTime;
        WorldFlares.flares.add(e);
    }
    
    public static void update() {
        for (int i = WorldFlares.flares.size() - 1; i >= 0; --i) {
            WorldFlares.flares.get(i).update();
            if (!WorldFlares.flares.get(i).hasLaunched) {
                WorldFlares.flares.remove(i);
            }
        }
    }
    
    public static void applyFlaresForPlayer(final RenderSettings.PlayerRenderSettings playerRenderSettings, final int n, final IsoPlayer isoPlayer) {
        for (int i = WorldFlares.flares.size() - 1; i >= 0; --i) {
            if (WorldFlares.flares.get(i).hasLaunched) {
                WorldFlares.flares.get(i).applyFlare(playerRenderSettings, n, isoPlayer);
            }
        }
    }
    
    public static void setDebugDraw(final boolean debug_DRAW) {
        WorldFlares.DEBUG_DRAW = debug_DRAW;
    }
    
    public static boolean getDebugDraw() {
        return WorldFlares.DEBUG_DRAW;
    }
    
    public static void debugRender() {
        if (!WorldFlares.DEBUG_DRAW) {
            return;
        }
        final float n = 0.0f;
        for (int i = WorldFlares.flares.size() - 1; i >= 0; --i) {
            final Flare flare = WorldFlares.flares.get(i);
            final float n2 = 0.5f;
            for (double n3 = 0.0; n3 < 6.283185307179586; n3 += 0.15707963267948966) {
                DrawIsoLine(flare.x + flare.range * (float)Math.cos(n3), flare.y + flare.range * (float)Math.sin(n3), flare.x + flare.range * (float)Math.cos(n3 + 0.15707963267948966), flare.y + flare.range * (float)Math.sin(n3 + 0.15707963267948966), n, 1.0f, 1.0f, 1.0f, 0.25f, 1);
                DrawIsoLine(flare.x + n2 * (float)Math.cos(n3), flare.y + n2 * (float)Math.sin(n3), flare.x + n2 * (float)Math.cos(n3 + 0.15707963267948966), flare.y + n2 * (float)Math.sin(n3 + 0.15707963267948966), n, 1.0f, 1.0f, 1.0f, 0.25f, 1);
            }
        }
    }
    
    private static void DrawIsoLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final int n10) {
        LineDrawer.drawLine(IsoUtils.XToScreenExact(n, n2, n5, 0), IsoUtils.YToScreenExact(n, n2, n5, 0), IsoUtils.XToScreenExact(n3, n4, n5, 0), IsoUtils.YToScreenExact(n3, n4, n5, 0), n6, n7, n8, n9, n10);
    }
    
    static {
        WorldFlares.DEBUG_DRAW = false;
        WorldFlares.NEXT_ID = 0;
        WorldFlares.flares = new ArrayList<Flare>();
    }
    
    private static class PlayerFlareLightInfo
    {
        private float intensity;
        private float lerp;
        private float distMod;
        private ClimateColorInfo flareCol;
        private ClimateColorInfo outColor;
        
        private PlayerFlareLightInfo() {
            this.flareCol = new ClimateColorInfo(1.0f, 1.0f, 1.0f, 1.0f);
            this.outColor = new ClimateColorInfo(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
    
    public static class Flare
    {
        private int id;
        private float x;
        private float y;
        private int range;
        private float windSpeed;
        private ClimateColorInfo color;
        private boolean hasLaunched;
        private SteppedUpdateFloat intensity;
        private float maxLifeTime;
        private float lifeTime;
        private int nextRandomTargetIntens;
        private float perc;
        private PlayerFlareLightInfo[] infos;
        
        public Flare() {
            this.windSpeed = 0.0f;
            this.color = new ClimateColorInfo(1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f);
            this.hasLaunched = false;
            this.intensity = new SteppedUpdateFloat(0.0f, 0.01f, 0.0f, 1.0f);
            this.nextRandomTargetIntens = 10;
            this.perc = 0.0f;
            this.infos = new PlayerFlareLightInfo[4];
            for (int i = 0; i < this.infos.length; ++i) {
                this.infos[i] = new PlayerFlareLightInfo();
            }
        }
        
        public int getId() {
            return this.id;
        }
        
        public float getX() {
            return this.x;
        }
        
        public float getY() {
            return this.y;
        }
        
        public int getRange() {
            return this.range;
        }
        
        public float getWindSpeed() {
            return this.windSpeed;
        }
        
        public ClimateColorInfo getColor() {
            return this.color;
        }
        
        public boolean isHasLaunched() {
            return this.hasLaunched;
        }
        
        public float getIntensity() {
            return this.intensity.value();
        }
        
        public float getMaxLifeTime() {
            return this.maxLifeTime;
        }
        
        public float getLifeTime() {
            return this.lifeTime;
        }
        
        public float getPercent() {
            return this.perc;
        }
        
        public float getIntensityPlayer(final int n) {
            return this.infos[n].intensity;
        }
        
        public float getLerpPlayer(final int n) {
            return this.infos[n].lerp;
        }
        
        public float getDistModPlayer(final int n) {
            return this.infos[n].distMod;
        }
        
        public ClimateColorInfo getColorPlayer(final int n) {
            return this.infos[n].flareCol;
        }
        
        public ClimateColorInfo getOutColorPlayer(final int n) {
            return this.infos[n].outColor;
        }
        
        private int GetDistance(final int n, final int n2, final int n3, final int n4) {
            return (int)Math.sqrt(Math.pow(n - n3, 2.0) + Math.pow(n2 - n4, 2.0));
        }
        
        private void update() {
            if (this.hasLaunched) {
                if (this.lifeTime > this.maxLifeTime) {
                    this.hasLaunched = false;
                    return;
                }
                this.perc = this.lifeTime / this.maxLifeTime;
                this.nextRandomTargetIntens -= (int)GameTime.instance.getMultiplier();
                if (this.nextRandomTargetIntens <= 0) {
                    this.intensity.setTarget(Rand.Next(0.8f, 1.0f));
                    this.nextRandomTargetIntens = Rand.Next(5, 30);
                }
                this.intensity.update(GameTime.instance.getMultiplier());
                if (this.windSpeed > 0.0f) {
                    final Vector2 vector2 = new Vector2(this.windSpeed / 60.0f * ClimateManager.getInstance().getWindIntensity() * (float)Math.sin(ClimateManager.getInstance().getWindAngleRadians()), this.windSpeed / 60.0f * ClimateManager.getInstance().getWindIntensity() * (float)Math.cos(ClimateManager.getInstance().getWindAngleRadians()));
                    this.x += vector2.x * GameTime.instance.getMultiplier();
                    this.y += vector2.y * GameTime.instance.getMultiplier();
                }
                for (int i = 0; i < 4; ++i) {
                    final PlayerFlareLightInfo playerFlareLightInfo = this.infos[i];
                    final IsoPlayer isoPlayer = IsoPlayer.players[i];
                    if (isoPlayer == null) {
                        playerFlareLightInfo.intensity = 0.0f;
                    }
                    else {
                        final int getDistance = this.GetDistance((int)this.x, (int)this.y, (int)isoPlayer.getX(), (int)isoPlayer.getY());
                        if (getDistance > this.range) {
                            playerFlareLightInfo.intensity = 0.0f;
                            playerFlareLightInfo.lerp = 1.0f;
                        }
                        else {
                            playerFlareLightInfo.distMod = 1.0f - getDistance / (float)this.range;
                            if (this.perc < 0.75f) {
                                playerFlareLightInfo.lerp = 0.0f;
                            }
                            else {
                                playerFlareLightInfo.lerp = (this.perc - 0.75f) / 0.25f;
                            }
                            playerFlareLightInfo.intensity = this.intensity.value();
                        }
                        final float n = (1.0f - playerFlareLightInfo.lerp) * playerFlareLightInfo.distMod * playerFlareLightInfo.intensity;
                        final ClimateManager.ClimateFloat dayLightStrength = ClimateManager.getInstance().dayLightStrength;
                        dayLightStrength.finalValue += (1.0f - ClimateManager.getInstance().dayLightStrength.finalValue) * n;
                        if (isoPlayer != null) {
                            isoPlayer.dirtyRecalcGridStackTime = 1.0f;
                        }
                    }
                }
                this.lifeTime += GameTime.instance.getMultiplier();
            }
        }
        
        private void applyFlare(final RenderSettings.PlayerRenderSettings playerRenderSettings, final int n, final IsoPlayer isoPlayer) {
            final PlayerFlareLightInfo playerFlareLightInfo = this.infos[n];
            if (playerFlareLightInfo.distMod > 0.0f) {
                final float n2 = 1.0f - playerRenderSettings.CM_DayLightStrength;
                final float clamp = PZMath.clamp(((playerRenderSettings.CM_NightStrength > n2) ? playerRenderSettings.CM_NightStrength : n2) * 2.0f, 0.0f, 1.0f);
                final float n3 = (1.0f - playerFlareLightInfo.lerp) * playerFlareLightInfo.distMod;
                final ClimateColorInfo cm_GlobalLight = playerRenderSettings.CM_GlobalLight;
                playerFlareLightInfo.outColor.setTo(cm_GlobalLight);
                playerFlareLightInfo.outColor.getExterior().g *= 1.0f - clamp * n3 * playerFlareLightInfo.intensity * 0.5f;
                playerFlareLightInfo.outColor.getInterior().g *= 1.0f - clamp * n3 * playerFlareLightInfo.intensity * 0.5f;
                playerFlareLightInfo.outColor.getExterior().b *= 1.0f - clamp * n3 * playerFlareLightInfo.intensity * 0.8f;
                playerFlareLightInfo.outColor.getInterior().b *= 1.0f - clamp * n3 * playerFlareLightInfo.intensity * 0.8f;
                playerFlareLightInfo.flareCol.setTo(this.color);
                playerFlareLightInfo.flareCol.scale(clamp);
                playerFlareLightInfo.flareCol.getExterior().a = 1.0f;
                playerFlareLightInfo.flareCol.getInterior().a = 1.0f;
                playerFlareLightInfo.outColor.getExterior().r = ((playerFlareLightInfo.outColor.getExterior().r > playerFlareLightInfo.flareCol.getExterior().r) ? playerFlareLightInfo.outColor.getExterior().r : playerFlareLightInfo.flareCol.getExterior().r);
                playerFlareLightInfo.outColor.getExterior().g = ((playerFlareLightInfo.outColor.getExterior().g > playerFlareLightInfo.flareCol.getExterior().g) ? playerFlareLightInfo.outColor.getExterior().g : playerFlareLightInfo.flareCol.getExterior().g);
                playerFlareLightInfo.outColor.getExterior().b = ((playerFlareLightInfo.outColor.getExterior().b > playerFlareLightInfo.flareCol.getExterior().b) ? playerFlareLightInfo.outColor.getExterior().b : playerFlareLightInfo.flareCol.getExterior().b);
                playerFlareLightInfo.outColor.getExterior().a = ((playerFlareLightInfo.outColor.getExterior().a > playerFlareLightInfo.flareCol.getExterior().a) ? playerFlareLightInfo.outColor.getExterior().a : playerFlareLightInfo.flareCol.getExterior().a);
                playerFlareLightInfo.outColor.getInterior().r = ((playerFlareLightInfo.outColor.getInterior().r > playerFlareLightInfo.flareCol.getInterior().r) ? playerFlareLightInfo.outColor.getInterior().r : playerFlareLightInfo.flareCol.getInterior().r);
                playerFlareLightInfo.outColor.getInterior().g = ((playerFlareLightInfo.outColor.getInterior().g > playerFlareLightInfo.flareCol.getInterior().g) ? playerFlareLightInfo.outColor.getInterior().g : playerFlareLightInfo.flareCol.getInterior().g);
                playerFlareLightInfo.outColor.getInterior().b = ((playerFlareLightInfo.outColor.getInterior().b > playerFlareLightInfo.flareCol.getInterior().b) ? playerFlareLightInfo.outColor.getInterior().b : playerFlareLightInfo.flareCol.getInterior().b);
                playerFlareLightInfo.outColor.getInterior().a = ((playerFlareLightInfo.outColor.getInterior().a > playerFlareLightInfo.flareCol.getInterior().a) ? playerFlareLightInfo.outColor.getInterior().a : playerFlareLightInfo.flareCol.getInterior().a);
                final float n4 = 1.0f - n3 * playerFlareLightInfo.intensity;
                playerFlareLightInfo.outColor.interp(cm_GlobalLight, n4, cm_GlobalLight);
                final float lerp = ClimateManager.lerp(n4, 0.35f, playerRenderSettings.CM_Ambient);
                playerRenderSettings.CM_Ambient = ((playerRenderSettings.CM_Ambient > lerp) ? playerRenderSettings.CM_Ambient : lerp);
                final float lerp2 = ClimateManager.lerp(n4, 0.6f * playerFlareLightInfo.intensity, playerRenderSettings.CM_DayLightStrength);
                playerRenderSettings.CM_DayLightStrength = ((playerRenderSettings.CM_DayLightStrength > lerp2) ? playerRenderSettings.CM_DayLightStrength : lerp2);
                if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                    final float lerp3 = ClimateManager.lerp(n4, 1.0f * clamp, playerRenderSettings.CM_Desaturation);
                    playerRenderSettings.CM_Desaturation = ((playerRenderSettings.CM_Desaturation > lerp3) ? playerRenderSettings.CM_Desaturation : lerp3);
                }
            }
        }
    }
}
