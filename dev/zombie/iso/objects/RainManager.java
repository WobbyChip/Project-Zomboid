// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.iso.IsoObject;
import zombie.Lua.LuaEventManager;
import zombie.network.GameServer;
import zombie.iso.weather.ClimateManager;
import zombie.iso.IsoCell;
import zombie.GameTime;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.core.Rand;
import zombie.iso.IsoWorld;
import zombie.characters.IsoPlayer;
import zombie.core.PerformanceSettings;
import java.util.Stack;
import java.util.ArrayList;
import zombie.core.textures.ColorInfo;
import fmod.fmod.Audio;
import zombie.iso.IsoGridSquare;

public class RainManager
{
    public static boolean IsRaining;
    public static int NumActiveRainSplashes;
    public static int NumActiveRaindrops;
    public static int MaxRainSplashObjects;
    public static int MaxRaindropObjects;
    public static float RainSplashAnimDelay;
    public static int AddNewSplashesDelay;
    public static int AddNewSplashesTimer;
    public static float RaindropGravity;
    public static float GravModMin;
    public static float GravModMax;
    public static float RaindropStartDistance;
    public static IsoGridSquare[] PlayerLocation;
    public static IsoGridSquare[] PlayerOldLocation;
    public static boolean PlayerMoved;
    public static int RainRadius;
    public static Audio RainAmbient;
    public static Audio ThunderAmbient;
    public static ColorInfo RainSplashTintMod;
    public static ColorInfo RaindropTintMod;
    public static ColorInfo DarkRaindropTintMod;
    public static ArrayList<IsoRainSplash> RainSplashStack;
    public static ArrayList<IsoRaindrop> RaindropStack;
    public static Stack<IsoRainSplash> RainSplashReuseStack;
    public static Stack<IsoRaindrop> RaindropReuseStack;
    private static float RainChangeTimer;
    private static float RainChangeRate;
    private static float RainChangeRateMin;
    private static float RainChangeRateMax;
    public static float RainIntensity;
    public static float RainDesiredIntensity;
    private static int randRain;
    public static int randRainMin;
    public static int randRainMax;
    private static boolean stopRain;
    static Audio OutsideAmbient;
    static Audio OutsideNightAmbient;
    static ColorInfo AdjustedRainSplashTintMod;
    
    public static void reset() {
        RainManager.RainSplashStack.clear();
        RainManager.RaindropStack.clear();
        RainManager.RaindropReuseStack.clear();
        RainManager.RainSplashReuseStack.clear();
        RainManager.NumActiveRainSplashes = 0;
        RainManager.NumActiveRaindrops = 0;
        for (int i = 0; i < 4; ++i) {
            RainManager.PlayerLocation[i] = null;
            RainManager.PlayerOldLocation[i] = null;
        }
        RainManager.RainAmbient = null;
        RainManager.ThunderAmbient = null;
        RainManager.IsRaining = false;
        RainManager.stopRain = false;
    }
    
    public static void AddRaindrop(final IsoRaindrop isoRaindrop) {
        if (RainManager.NumActiveRaindrops < RainManager.MaxRaindropObjects) {
            RainManager.RaindropStack.add(isoRaindrop);
            ++RainManager.NumActiveRaindrops;
        }
        else {
            IsoRaindrop isoRaindrop2 = null;
            int life = -1;
            for (int i = 0; i < RainManager.RaindropStack.size(); ++i) {
                if (RainManager.RaindropStack.get(i).Life > life) {
                    life = RainManager.RaindropStack.get(i).Life;
                    isoRaindrop2 = RainManager.RaindropStack.get(i);
                }
            }
            if (isoRaindrop2 != null) {
                RemoveRaindrop(isoRaindrop2);
                RainManager.RaindropStack.add(isoRaindrop);
                ++RainManager.NumActiveRaindrops;
            }
        }
    }
    
    public static void AddRainSplash(final IsoRainSplash isoRainSplash) {
        if (RainManager.NumActiveRainSplashes < RainManager.MaxRainSplashObjects) {
            RainManager.RainSplashStack.add(isoRainSplash);
            ++RainManager.NumActiveRainSplashes;
        }
        else {
            IsoRainSplash isoRainSplash2 = null;
            int age = -1;
            for (int i = 0; i < RainManager.RainSplashStack.size(); ++i) {
                if (RainManager.RainSplashStack.get(i).Age > age) {
                    age = RainManager.RainSplashStack.get(i).Age;
                    isoRainSplash2 = RainManager.RainSplashStack.get(i);
                }
            }
            RemoveRainSplash(isoRainSplash2);
            RainManager.RainSplashStack.add(isoRainSplash);
            ++RainManager.NumActiveRainSplashes;
        }
    }
    
    public static void AddSplashes() {
        if (RainManager.AddNewSplashesTimer > 0) {
            --RainManager.AddNewSplashesTimer;
        }
        else {
            RainManager.AddNewSplashesTimer = (int)(RainManager.AddNewSplashesDelay * (PerformanceSettings.getLockFPS() / 30.0f));
            if (!RainManager.stopRain) {
                if (RainManager.PlayerMoved) {
                    for (int i = RainManager.RainSplashStack.size() - 1; i >= 0; --i) {
                        final IsoRainSplash isoRainSplash = RainManager.RainSplashStack.get(i);
                        if (!inBounds(isoRainSplash.square)) {
                            RemoveRainSplash(isoRainSplash);
                        }
                    }
                    for (int j = RainManager.RaindropStack.size() - 1; j >= 0; --j) {
                        final IsoRaindrop isoRaindrop = RainManager.RaindropStack.get(j);
                        if (!inBounds(isoRaindrop.square)) {
                            RemoveRaindrop(isoRaindrop);
                        }
                    }
                }
                int n = 0;
                for (int k = 0; k < IsoPlayer.numPlayers; ++k) {
                    if (IsoPlayer.players[k] != null) {
                        ++n;
                    }
                }
                final int min = Math.min(RainManager.MaxRainSplashObjects, RainManager.RainRadius * 2 * RainManager.RainRadius * 2 / (RainManager.randRain + 1));
                while (RainManager.NumActiveRainSplashes > min * n) {
                    RemoveRainSplash(RainManager.RainSplashStack.get(0));
                }
                while (RainManager.NumActiveRaindrops > min * n) {
                    RemoveRaindrop(RainManager.RaindropStack.get(0));
                }
                final IsoCell currentCell = IsoWorld.instance.CurrentCell;
                for (int l = 0; l < IsoPlayer.numPlayers; ++l) {
                    if (IsoPlayer.players[l] != null) {
                        if (RainManager.PlayerLocation[l] != null) {
                            for (int n2 = 0; n2 < min; ++n2) {
                                final IsoGridSquare gridSquare = currentCell.getGridSquare(RainManager.PlayerLocation[l].getX() + Rand.Next(-RainManager.RainRadius, RainManager.RainRadius), RainManager.PlayerLocation[l].getY() + Rand.Next(-RainManager.RainRadius, RainManager.RainRadius), 0);
                                if (gridSquare != null && gridSquare.isSeen(l) && !gridSquare.getProperties().Is(IsoFlagType.vegitation) && gridSquare.getProperties().Is(IsoFlagType.exterior)) {
                                    StartRainSplash(currentCell, gridSquare, true);
                                }
                            }
                        }
                    }
                }
            }
            RainManager.PlayerMoved = false;
            if (!RainManager.stopRain) {
                --RainManager.randRain;
                if (RainManager.randRain < RainManager.randRainMin) {
                    RainManager.randRain = RainManager.randRainMin;
                }
            }
            else {
                RainManager.randRain -= (int)(1.0f * GameTime.instance.getMultiplier());
                if (RainManager.randRain < RainManager.randRainMin) {
                    removeAll();
                    RainManager.randRain = RainManager.randRainMin;
                }
                else {
                    for (int index = RainManager.RainSplashStack.size() - 1; index >= 0; --index) {
                        if (Rand.Next(RainManager.randRain) == 0) {
                            RemoveRainSplash(RainManager.RainSplashStack.get(index));
                        }
                    }
                    for (int index2 = RainManager.RaindropStack.size() - 1; index2 >= 0; --index2) {
                        if (Rand.Next(RainManager.randRain) == 0) {
                            RemoveRaindrop(RainManager.RaindropStack.get(index2));
                        }
                    }
                }
            }
        }
    }
    
    public static void RemoveRaindrop(final IsoRaindrop isoRaindrop) {
        if (isoRaindrop.square != null) {
            isoRaindrop.square.getProperties().UnSet(IsoFlagType.HasRaindrop);
            isoRaindrop.square.setRainDrop(null);
            isoRaindrop.square = null;
        }
        RainManager.RaindropStack.remove(isoRaindrop);
        --RainManager.NumActiveRaindrops;
        RainManager.RaindropReuseStack.push(isoRaindrop);
    }
    
    public static void RemoveRainSplash(final IsoRainSplash isoRainSplash) {
        if (isoRainSplash.square != null) {
            isoRainSplash.square.getProperties().UnSet(IsoFlagType.HasRainSplashes);
            isoRainSplash.square.setRainSplash(null);
            isoRainSplash.square = null;
        }
        RainManager.RainSplashStack.remove(isoRainSplash);
        --RainManager.NumActiveRainSplashes;
        RainManager.RainSplashReuseStack.push(isoRainSplash);
    }
    
    public static void SetPlayerLocation(final int n, final IsoGridSquare isoGridSquare) {
        RainManager.PlayerOldLocation[n] = RainManager.PlayerLocation[n];
        RainManager.PlayerLocation[n] = isoGridSquare;
        if (RainManager.PlayerOldLocation[n] != RainManager.PlayerLocation[n]) {
            RainManager.PlayerMoved = true;
        }
    }
    
    public static Boolean isRaining() {
        return ClimateManager.getInstance().isRaining();
    }
    
    public static void stopRaining() {
        RainManager.stopRain = true;
        RainManager.randRain = RainManager.randRainMax;
        RainManager.RainDesiredIntensity = 0.0f;
        if (GameServer.bServer) {
            GameServer.stopRain();
        }
        LuaEventManager.triggerEvent("OnRainStop");
    }
    
    public static void startRaining() {
    }
    
    public static void StartRaindrop(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final boolean b) {
        if (!isoGridSquare.getProperties().Is(IsoFlagType.HasRaindrop)) {
            if (!RainManager.RaindropReuseStack.isEmpty()) {
                if (b) {
                    if (isoGridSquare.getRainDrop() != null) {
                        return;
                    }
                    final IsoRaindrop rainDrop = RainManager.RaindropReuseStack.pop();
                    rainDrop.Reset(isoGridSquare, b);
                    isoGridSquare.setRainDrop(rainDrop);
                }
            }
            else if (b) {
                if (isoGridSquare.getRainDrop() != null) {
                    return;
                }
                isoGridSquare.setRainDrop(new IsoRaindrop(isoCell, isoGridSquare, b));
            }
        }
    }
    
    public static void StartRainSplash(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final boolean b) {
    }
    
    public static void Update() {
        RainManager.IsRaining = ClimateManager.getInstance().isRaining();
        RainManager.RainIntensity = (RainManager.IsRaining ? ClimateManager.getInstance().getPrecipitationIntensity() : 0.0f);
        if (IsoPlayer.getInstance() == null) {
            return;
        }
        if (IsoPlayer.getInstance().getCurrentSquare() == null) {
            return;
        }
        if (!GameServer.bServer) {
            AddSplashes();
        }
    }
    
    public static void UpdateServer() {
    }
    
    public static void setRandRainMax(final int randRainMax) {
        RainManager.randRainMax = randRainMax;
        RainManager.randRain = RainManager.randRainMax;
    }
    
    public static void setRandRainMin(final int randRainMin) {
        RainManager.randRainMin = randRainMin;
    }
    
    public static boolean inBounds(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null) {
            return false;
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (IsoPlayer.players[i] != null) {
                if (RainManager.PlayerLocation[i] != null) {
                    if (isoGridSquare.getX() < RainManager.PlayerLocation[i].getX() - RainManager.RainRadius || isoGridSquare.getX() >= RainManager.PlayerLocation[i].getX() + RainManager.RainRadius) {
                        return true;
                    }
                    if (isoGridSquare.getY() < RainManager.PlayerLocation[i].getY() - RainManager.RainRadius || isoGridSquare.getY() >= RainManager.PlayerLocation[i].getY() + RainManager.RainRadius) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static void RemoveAllOn(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare.getRainDrop() != null) {
            RemoveRaindrop(isoGridSquare.getRainDrop());
        }
        if (isoGridSquare.getRainSplash() != null) {
            RemoveRainSplash(isoGridSquare.getRainSplash());
        }
    }
    
    public static float getRainIntensity() {
        return ClimateManager.getInstance().getPrecipitationIntensity();
    }
    
    private static void removeAll() {
        for (int i = RainManager.RainSplashStack.size() - 1; i >= 0; --i) {
            RemoveRainSplash(RainManager.RainSplashStack.get(i));
        }
        for (int j = RainManager.RaindropStack.size() - 1; j >= 0; --j) {
            RemoveRaindrop(RainManager.RaindropStack.get(j));
        }
        RainManager.RaindropStack.clear();
        RainManager.RainSplashStack.clear();
        RainManager.NumActiveRainSplashes = 0;
        RainManager.NumActiveRaindrops = 0;
    }
    
    private static boolean interruptSleep(final IsoPlayer isoPlayer) {
        if (isoPlayer.isAsleep() && isoPlayer.isOutside() && isoPlayer.getBed() != null && !isoPlayer.getBed().getName().equals("Tent")) {
            final IsoObject bed = isoPlayer.getBed();
            if (bed.getCell().getGridSquare(bed.getX(), bed.getY(), bed.getZ() + 1.0f) == null || bed.getCell().getGridSquare(bed.getX(), bed.getY(), bed.getZ() + 1.0f).getFloor() == null) {
                return true;
            }
        }
        return false;
    }
    
    static {
        RainManager.IsRaining = false;
        RainManager.NumActiveRainSplashes = 0;
        RainManager.NumActiveRaindrops = 0;
        RainManager.MaxRainSplashObjects = 500;
        RainManager.MaxRaindropObjects = 500;
        RainManager.RainSplashAnimDelay = 0.2f;
        RainManager.AddNewSplashesDelay = 30;
        RainManager.AddNewSplashesTimer = RainManager.AddNewSplashesDelay;
        RainManager.RaindropGravity = 0.065f;
        RainManager.GravModMin = 0.28f;
        RainManager.GravModMax = 0.5f;
        RainManager.RaindropStartDistance = 850.0f;
        RainManager.PlayerLocation = new IsoGridSquare[4];
        RainManager.PlayerOldLocation = new IsoGridSquare[4];
        RainManager.PlayerMoved = true;
        RainManager.RainRadius = 18;
        RainManager.ThunderAmbient = null;
        RainManager.RainSplashTintMod = new ColorInfo(0.8f, 0.9f, 1.0f, 0.3f);
        RainManager.RaindropTintMod = new ColorInfo(0.8f, 0.9f, 1.0f, 0.3f);
        RainManager.DarkRaindropTintMod = new ColorInfo(0.8f, 0.9f, 1.0f, 0.3f);
        RainManager.RainSplashStack = new ArrayList<IsoRainSplash>(1600);
        RainManager.RaindropStack = new ArrayList<IsoRaindrop>(1600);
        RainManager.RainSplashReuseStack = new Stack<IsoRainSplash>();
        RainManager.RaindropReuseStack = new Stack<IsoRaindrop>();
        RainManager.RainChangeTimer = 1.0f;
        RainManager.RainChangeRate = 0.01f;
        RainManager.RainChangeRateMin = 0.006f;
        RainManager.RainChangeRateMax = 0.01f;
        RainManager.RainIntensity = 1.0f;
        RainManager.RainDesiredIntensity = 1.0f;
        RainManager.randRain = 0;
        RainManager.randRainMin = 0;
        RainManager.randRainMax = 0;
        RainManager.stopRain = false;
        RainManager.OutsideAmbient = null;
        RainManager.OutsideNightAmbient = null;
        RainManager.AdjustedRainSplashTintMod = new ColorInfo();
    }
}
