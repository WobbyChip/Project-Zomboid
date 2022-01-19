// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import zombie.core.math.PZMath;
import zombie.ui.UIManager;
import zombie.iso.IsoPuddles;
import zombie.iso.IsoWater;

public final class PerformanceSettings
{
    public static int ManualFrameSkips;
    private static int s_lockFPS;
    private static boolean s_uncappedFPS;
    public static int LightingFrameSkip;
    public static int WaterQuality;
    public static int PuddlesQuality;
    public static boolean NewRoofHiding;
    public static boolean LightingThread;
    public static int LightingFPS;
    public static boolean auto3DZombies;
    public static final PerformanceSettings instance;
    public static boolean InterpolateAnims;
    public static int AnimationSkip;
    public static boolean ModelLighting;
    public static int ZombieAnimationSpeedFalloffCount;
    public static int ZombieBonusFullspeedFalloff;
    public static int BaseStaticAnimFramerate;
    public static boolean UseFBOs;
    public static int numberZombiesBlended;
    public static int FogQuality;
    
    public static int getLockFPS() {
        return PerformanceSettings.s_lockFPS;
    }
    
    public static void setLockFPS(final int s_lockFPS) {
        PerformanceSettings.s_lockFPS = s_lockFPS;
    }
    
    public static boolean isUncappedFPS() {
        return PerformanceSettings.s_uncappedFPS;
    }
    
    public static void setUncappedFPS(final boolean s_uncappedFPS) {
        PerformanceSettings.s_uncappedFPS = s_uncappedFPS;
    }
    
    public int getFramerate() {
        return getLockFPS();
    }
    
    public void setFramerate(final int lockFPS) {
        setLockFPS(lockFPS);
    }
    
    public boolean isFramerateUncapped() {
        return isUncappedFPS();
    }
    
    public void setFramerateUncapped(final boolean uncappedFPS) {
        setUncappedFPS(uncappedFPS);
    }
    
    public void setLightingQuality(final int lightingFrameSkip) {
        PerformanceSettings.LightingFrameSkip = lightingFrameSkip;
    }
    
    public int getLightingQuality() {
        return PerformanceSettings.LightingFrameSkip;
    }
    
    public void setWaterQuality(final int waterQuality) {
        PerformanceSettings.WaterQuality = waterQuality;
        IsoWater.getInstance().applyWaterQuality();
    }
    
    public int getWaterQuality() {
        return PerformanceSettings.WaterQuality;
    }
    
    public void setPuddlesQuality(final int puddlesQuality) {
        PerformanceSettings.PuddlesQuality = puddlesQuality;
        if (puddlesQuality > 2 || puddlesQuality < 0) {
            PerformanceSettings.PuddlesQuality = 0;
        }
        IsoPuddles.getInstance().applyPuddlesQuality();
    }
    
    public int getPuddlesQuality() {
        return PerformanceSettings.PuddlesQuality;
    }
    
    public void setNewRoofHiding(final boolean newRoofHiding) {
        PerformanceSettings.NewRoofHiding = newRoofHiding;
    }
    
    public boolean getNewRoofHiding() {
        return PerformanceSettings.NewRoofHiding;
    }
    
    public void setLightingFPS(int b) {
        b = (PerformanceSettings.LightingFPS = Math.max(1, Math.min(120, b)));
        System.out.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, PerformanceSettings.LightingFPS));
    }
    
    public int getLightingFPS() {
        return PerformanceSettings.LightingFPS;
    }
    
    public int getUIRenderFPS() {
        return UIManager.useUIFBO ? Core.OptionUIRenderFPS : PerformanceSettings.s_lockFPS;
    }
    
    public int getFogQuality() {
        return PerformanceSettings.FogQuality;
    }
    
    public void setFogQuality(final int n) {
        PerformanceSettings.FogQuality = PZMath.clamp(n, 0, 2);
    }
    
    static {
        PerformanceSettings.ManualFrameSkips = 0;
        PerformanceSettings.s_lockFPS = 60;
        PerformanceSettings.s_uncappedFPS = false;
        PerformanceSettings.LightingFrameSkip = 0;
        PerformanceSettings.WaterQuality = 0;
        PerformanceSettings.PuddlesQuality = 0;
        PerformanceSettings.NewRoofHiding = true;
        PerformanceSettings.LightingThread = true;
        PerformanceSettings.LightingFPS = 15;
        PerformanceSettings.auto3DZombies = false;
        instance = new PerformanceSettings();
        PerformanceSettings.InterpolateAnims = true;
        PerformanceSettings.AnimationSkip = 1;
        PerformanceSettings.ModelLighting = true;
        PerformanceSettings.ZombieAnimationSpeedFalloffCount = 6;
        PerformanceSettings.ZombieBonusFullspeedFalloff = 3;
        PerformanceSettings.BaseStaticAnimFramerate = 60;
        PerformanceSettings.UseFBOs = false;
        PerformanceSettings.numberZombiesBlended = 20;
        PerformanceSettings.FogQuality = 0;
    }
}
