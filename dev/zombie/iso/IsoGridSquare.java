// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.core.profiling.PerformanceProfileProbe;
import org.lwjgl.opengl.GL20;
import zombie.core.opengl.ShaderProgram;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import zombie.iso.objects.IsoCompost;
import zombie.network.ServerMap;
import zombie.util.StringUtils;
import zombie.core.math.PZMath;
import zombie.ai.State;
import zombie.ai.states.ZombieIdleState;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.inventory.types.HandWeapon;
import zombie.erosion.categories.ErosionCategory;
import zombie.meta.Meta;
import zombie.debug.DebugType;
import zombie.core.opengl.RenderSettings;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.IsoSurvivor;
import zombie.iso.sprite.shapers.FloorShaper;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.shapers.FloorShaperDeDiamond;
import zombie.iso.sprite.shapers.FloorShaperDiamond;
import zombie.iso.sprite.shapers.FloorShaperAttachedSprites;
import zombie.iso.objects.IsoCarBatteryCharger;
import zombie.iso.objects.IsoFire;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.objects.IsoLightSwitch;
import zombie.core.PerformanceSettings;
import zombie.vehicles.BaseVehicle;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;
import zombie.inventory.ItemContainer;
import zombie.iso.objects.IsoFireManager;
import zombie.SoundManager;
import zombie.inventory.types.Food;
import zombie.iso.objects.IsoWaveSignal;
import zombie.core.raknet.UdpConnection;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.areas.SafeHouse;
import zombie.network.ServerOptions;
import zombie.iso.objects.IsoGenerator;
import zombie.characters.IsoZombie;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.InventoryItem;
import zombie.MapCollisionData;
import zombie.core.Rand;
import zombie.vehicles.PolygonalMap2;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.network.ServerLOS;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.characters.IsoPlayer;
import zombie.iso.objects.IsoWindowFrame;
import zombie.scripting.objects.Item;
import zombie.util.io.BitHeaderRead;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.scripting.ScriptManager;
import zombie.debug.DebugLog;
import zombie.iso.objects.IsoBrokenGlass;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import java.util.List;
import zombie.Lua.LuaEventManager;
import zombie.core.logger.ExceptionLogger;
import zombie.network.GameClient;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import zombie.util.io.BitHeaderWrite;
import zombie.iso.objects.IsoDeadBody;
import zombie.GameWindow;
import zombie.util.io.BitHeader;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import zombie.iso.objects.IsoTree;
import zombie.Lua.LuaManager;
import zombie.iso.sprite.shapers.WallShaperW;
import zombie.iso.sprite.shapers.WallShaperN;
import zombie.IndieGL;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.opengl.Shader;
import zombie.core.SpriteRenderer;
import zombie.debug.DebugOptions;
import zombie.core.textures.Texture;
import zombie.iso.sprite.shapers.WallShaperWhole;
import zombie.iso.sprite.IsoSpriteInstance;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import zombie.util.Type;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoDoor;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.network.GameServer;
import zombie.core.Core;
import zombie.erosion.ErosionData;
import zombie.iso.objects.IsoRainSplash;
import zombie.iso.objects.IsoRaindrop;
import java.util.Comparator;
import zombie.core.Color;
import zombie.core.textures.ColorInfo;
import se.krka.kahlua.vm.KahluaTable;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.core.properties.PropertyContainer;
import zombie.ZomboidBitFlag;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.util.list.PZArrayList;
import zombie.characters.IsoGameCharacter;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.isoregion.regions.IWorldRegion;
import zombie.iso.areas.IsoRoom;
import java.util.ArrayList;

public final class IsoGridSquare
{
    private boolean hasTree;
    private ArrayList<Float> LightInfluenceB;
    private ArrayList<Float> LightInfluenceG;
    private ArrayList<Float> LightInfluenceR;
    public final IsoGridSquare[] nav;
    public int collideMatrix;
    public int pathMatrix;
    public int visionMatrix;
    public IsoRoom room;
    public IsoGridSquare w;
    public IsoGridSquare nw;
    public IsoGridSquare sw;
    public IsoGridSquare s;
    public IsoGridSquare n;
    public IsoGridSquare ne;
    public IsoGridSquare se;
    public IsoGridSquare e;
    public boolean haveSheetRope;
    private IWorldRegion isoWorldRegion;
    private boolean hasSetIsoWorldRegion;
    public int ObjectsSyncCount;
    public IsoBuilding roofHideBuilding;
    public boolean bFlattenGrassEtc;
    private static final long VisiFlagTimerPeriod_ms = 750L;
    private final boolean[] playerCutawayFlags;
    private final long[] playerCutawayFlagLockUntilTimes;
    private final boolean[] targetPlayerCutawayFlags;
    private final boolean[] playerIsDissolvedFlags;
    private final long[] playerIsDissolvedFlagLockUntilTimes;
    private final boolean[] targetPlayerIsDissolvedFlags;
    private IsoWaterGeometry water;
    private IsoPuddlesGeometry puddles;
    private float puddlesCacheSize;
    private float puddlesCacheLevel;
    public final ILighting[] lighting;
    public int x;
    public int y;
    public int z;
    private int CachedScreenValue;
    public float CachedScreenX;
    public float CachedScreenY;
    private static long torchTimer;
    public boolean SolidFloorCached;
    public boolean SolidFloor;
    private boolean CacheIsFree;
    private boolean CachedIsFree;
    public IsoChunk chunk;
    public int roomID;
    public Integer ID;
    public IsoMetaGrid.Zone zone;
    private final ArrayList<IsoGameCharacter> DeferedCharacters;
    private int DeferredCharacterTick;
    private final ArrayList<IsoMovingObject> StaticMovingObjects;
    private final ArrayList<IsoMovingObject> MovingObjects;
    protected final PZArrayList<IsoObject> Objects;
    protected final PZArrayList<IsoObject> localTemporaryObjects;
    private final ArrayList<IsoWorldInventoryObject> WorldObjects;
    final ZomboidBitFlag hasTypes;
    private final PropertyContainer Properties;
    private final ArrayList<IsoObject> SpecialObjects;
    public boolean haveRoof;
    private boolean burntOut;
    private boolean bHasFlies;
    private IsoGridOcclusionData OcclusionDataCache;
    public static final ConcurrentLinkedQueue<IsoGridSquare> isoGridSquareCache;
    public static ArrayDeque<IsoGridSquare> loadGridSquareCache;
    private boolean overlayDone;
    private KahluaTable table;
    private int trapPositionX;
    private int trapPositionY;
    private int trapPositionZ;
    private boolean haveElectricity;
    public static int gridSquareCacheEmptyTimer;
    private static float darkStep;
    public static int RecalcLightTime;
    private static int lightcache;
    public static final ArrayList<IsoGridSquare> choices;
    public static boolean USE_WALL_SHADER;
    private static final int cutawayY = 0;
    private static final int cutawayNWWidth = 66;
    private static final int cutawayNWHeight = 226;
    private static final int cutawaySEXCut = 1084;
    private static final int cutawaySEXUncut = 1212;
    private static final int cutawaySEWidth = 6;
    private static final int cutawaySEHeight = 196;
    private static final int cutawayNXFullyCut = 700;
    private static final int cutawayNXCutW = 444;
    private static final int cutawayNXUncut = 828;
    private static final int cutawayNXCutE = 956;
    private static final int cutawayWXFullyCut = 512;
    private static final int cutawayWXCutS = 768;
    private static final int cutawayWXUncut = 896;
    private static final int cutawayWXCutN = 256;
    private static final int cutawayFenceXOffset = 1;
    private static final int cutawayLogWallXOffset = 1;
    private static final int cutawaySpiffoWindowXOffset = -24;
    private static final int cutawayRoof4XOffset = -60;
    private static final int cutawayRoof17XOffset = -46;
    private static final int cutawayRoof28XOffset = -60;
    private static final int cutawayRoof41XOffset = -46;
    private static final ColorInfo lightInfoTemp;
    private static final float doorWindowCutawayLightMin = 0.3f;
    private static boolean bWallCutawayW;
    private static boolean bWallCutawayN;
    public boolean isSolidFloorCache;
    public boolean isExteriorCache;
    public boolean isVegitationCache;
    public int hourLastSeen;
    static IsoGridSquare lastLoaded;
    public static int IDMax;
    static int col;
    static int path;
    static int pathdoor;
    static int vision;
    public long hashCodeObjects;
    static final Color tr;
    static final Color tl;
    static final Color br;
    static final Color bl;
    static final Color interp1;
    static final Color interp2;
    static final Color finalCol;
    public static final CellGetSquare cellGetSquare;
    public boolean propertiesDirty;
    public static boolean UseSlowCollision;
    private static boolean bDoSlowPathfinding;
    private static final Comparator<IsoMovingObject> comp;
    public static boolean isOnScreenLast;
    private float splashX;
    private float splashY;
    private float splashFrame;
    private int splashFrameNum;
    private final ColorInfo[] lightInfo;
    static String[] rainsplashCache;
    private static final ColorInfo defColorInfo;
    private static final ColorInfo blackColorInfo;
    static int colu;
    static int coll;
    static int colr;
    static int colu2;
    static int coll2;
    static int colr2;
    public static boolean CircleStencil;
    public static float rmod;
    public static float gmod;
    public static float bmod;
    static final Vector2 tempo;
    static final Vector2 tempo2;
    private IsoRaindrop RainDrop;
    private IsoRainSplash RainSplash;
    private ErosionData.Square erosion;
    public static final int WALL_TYPE_N = 1;
    public static final int WALL_TYPE_S = 2;
    public static final int WALL_TYPE_W = 4;
    public static final int WALL_TYPE_E = 8;
    
    public static boolean getMatrixBit(final int n, final int n2, final int n3, final int n4) {
        return getMatrixBit(n, (byte)n2, (byte)n3, (byte)n4);
    }
    
    public static boolean getMatrixBit(final int n, final byte b, final byte b2, final byte b3) {
        return (n >> b + b2 * 3 + b3 * 9 & 0x1) != 0x0;
    }
    
    public static int setMatrixBit(final int n, final int n2, final int n3, final int n4, final boolean b) {
        return setMatrixBit(n, (byte)n2, (byte)n3, (byte)n4, b);
    }
    
    public static int setMatrixBit(final int n, final byte b, final byte b2, final byte b3, final boolean b4) {
        if (b4) {
            return n | 1 << b + b2 * 3 + b3 * 9;
        }
        return n & ~(1 << b + b2 * 3 + b3 * 9);
    }
    
    public void setPlayerCutawayFlag(final int n, final boolean b, final long n2) {
        this.targetPlayerCutawayFlags[n] = b;
        if (n2 > this.playerCutawayFlagLockUntilTimes[n] && this.playerCutawayFlags[n] != this.targetPlayerCutawayFlags[n]) {
            this.playerCutawayFlags[n] = this.targetPlayerCutawayFlags[n];
            this.playerCutawayFlagLockUntilTimes[n] = n2 + 750L;
        }
    }
    
    public boolean getPlayerCutawayFlag(final int n, final long n2) {
        if (n2 > this.playerCutawayFlagLockUntilTimes[n]) {
            return this.targetPlayerCutawayFlags[n];
        }
        return this.playerCutawayFlags[n];
    }
    
    public void setIsDissolved(final int n, final boolean b, final long n2) {
        this.targetPlayerIsDissolvedFlags[n] = b;
        if (n2 > this.playerIsDissolvedFlagLockUntilTimes[n] && this.playerIsDissolvedFlags[n] != this.targetPlayerIsDissolvedFlags[n]) {
            this.playerIsDissolvedFlags[n] = this.targetPlayerIsDissolvedFlags[n];
            this.playerIsDissolvedFlagLockUntilTimes[n] = n2 + 750L;
        }
    }
    
    public boolean getIsDissolved(final int n, final long n2) {
        if (n2 > this.playerIsDissolvedFlagLockUntilTimes[n]) {
            return this.targetPlayerIsDissolvedFlags[n];
        }
        return this.playerIsDissolvedFlags[n];
    }
    
    public IsoWaterGeometry getWater() {
        if (this.water != null && this.water.m_adjacentChunkLoadedCounter != this.chunk.m_adjacentChunkLoadedCounter) {
            this.water.m_adjacentChunkLoadedCounter = this.chunk.m_adjacentChunkLoadedCounter;
            if (this.water.hasWater || this.water.bShore) {
                this.clearWater();
            }
        }
        if (this.water == null) {
            try {
                this.water = IsoWaterGeometry.pool.alloc();
                this.water.m_adjacentChunkLoadedCounter = this.chunk.m_adjacentChunkLoadedCounter;
                if (this.water.init(this) == null) {
                    IsoWaterGeometry.pool.release(this.water);
                    this.water = null;
                }
            }
            catch (Exception ex) {
                this.clearWater();
            }
        }
        return this.water;
    }
    
    public void clearWater() {
        if (this.water != null) {
            IsoWaterGeometry.pool.release(this.water);
            this.water = null;
        }
    }
    
    public IsoPuddlesGeometry getPuddles() {
        if (this.puddles == null) {
            try {
                synchronized (IsoPuddlesGeometry.pool) {
                    this.puddles = IsoPuddlesGeometry.pool.alloc();
                }
                this.puddles.square = this;
                this.puddles.bRecalc = true;
            }
            catch (Exception ex) {
                this.clearPuddles();
            }
        }
        return this.puddles;
    }
    
    public void clearPuddles() {
        if (this.puddles != null) {
            this.puddles.square = null;
            synchronized (IsoPuddlesGeometry.pool) {
                IsoPuddlesGeometry.pool.release(this.puddles);
            }
            this.puddles = null;
        }
    }
    
    public float getPuddlesInGround() {
        if (this.isInARoom()) {
            return -1.0f;
        }
        if (Math.abs(IsoPuddles.getInstance().getPuddlesSize() + Core.getInstance().getPerfPuddles() + IsoCamera.frameState.OffscreenWidth - this.puddlesCacheSize) > 0.01) {
            this.puddlesCacheSize = IsoPuddles.getInstance().getPuddlesSize() + Core.getInstance().getPerfPuddles() + IsoCamera.frameState.OffscreenWidth;
            this.puddlesCacheLevel = IsoPuddlesCompute.computePuddle(this);
        }
        return this.puddlesCacheLevel;
    }
    
    public IsoGridOcclusionData getOcclusionData() {
        return this.OcclusionDataCache;
    }
    
    public IsoGridOcclusionData getOrCreateOcclusionData() {
        assert !GameServer.bServer;
        if (this.OcclusionDataCache == null) {
            this.OcclusionDataCache = new IsoGridOcclusionData(this);
        }
        return this.OcclusionDataCache;
    }
    
    public void softClear() {
        this.zone = null;
        this.room = null;
        this.w = null;
        this.nw = null;
        this.sw = null;
        this.s = null;
        this.n = null;
        this.ne = null;
        this.se = null;
        this.e = null;
        this.isoWorldRegion = null;
        this.hasSetIsoWorldRegion = false;
        for (int i = 0; i < 8; ++i) {
            this.nav[i] = null;
        }
    }
    
    public float getGridSneakModifier(final boolean b) {
        if (!b) {
            if (this.Properties.Is("CloseSneakBonus")) {
                return Integer.parseInt(this.Properties.Val("CloseSneakBonus")) / 100.0f;
            }
            if (this.Properties.Is(IsoFlagType.collideN) || this.Properties.Is(IsoFlagType.collideW) || this.Properties.Is(IsoFlagType.WindowN) || this.Properties.Is(IsoFlagType.WindowW) || this.Properties.Is(IsoFlagType.doorN) || this.Properties.Is(IsoFlagType.doorW)) {
                return 8.0f;
            }
        }
        else if (this.Properties.Is(IsoFlagType.solidtrans)) {
            return 4.0f;
        }
        return 1.0f;
    }
    
    public boolean isSomethingTo(final IsoGridSquare isoGridSquare) {
        return this.isWallTo(isoGridSquare) || this.isWindowTo(isoGridSquare) || this.isDoorTo(isoGridSquare);
    }
    
    public IsoObject getTransparentWallTo(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare == this || !this.isWallTo(isoGridSquare)) {
            return null;
        }
        if (isoGridSquare.x > this.x && isoGridSquare.Properties.Is(IsoFlagType.SpearOnlyAttackThrough) && !isoGridSquare.Properties.Is(IsoFlagType.WindowW)) {
            return isoGridSquare.getWall();
        }
        if (this.x > isoGridSquare.x && this.Properties.Is(IsoFlagType.SpearOnlyAttackThrough) && !this.Properties.Is(IsoFlagType.WindowW)) {
            return this.getWall();
        }
        if (isoGridSquare.y > this.y && isoGridSquare.Properties.Is(IsoFlagType.SpearOnlyAttackThrough) && !isoGridSquare.Properties.Is(IsoFlagType.WindowN)) {
            return isoGridSquare.getWall();
        }
        if (this.y > isoGridSquare.y && this.Properties.Is(IsoFlagType.SpearOnlyAttackThrough) && !this.Properties.Is(IsoFlagType.WindowN)) {
            return this.getWall();
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y) {
            final IsoObject transparentWallTo = this.getTransparentWallTo(IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.x, this.y, this.z));
            final IsoObject transparentWallTo2 = this.getTransparentWallTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, isoGridSquare.y, this.z));
            if (transparentWallTo != null) {
                return transparentWallTo;
            }
            if (transparentWallTo2 != null) {
                return transparentWallTo2;
            }
            final IsoObject transparentWallTo3 = isoGridSquare.getTransparentWallTo(IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.x, this.y, this.z));
            final IsoObject transparentWallTo4 = isoGridSquare.getTransparentWallTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, isoGridSquare.y, this.z));
            if (transparentWallTo3 != null) {
                return transparentWallTo3;
            }
            if (transparentWallTo4 != null) {
                return transparentWallTo4;
            }
        }
        return null;
    }
    
    public boolean isWallTo(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare == this) {
            return false;
        }
        if (isoGridSquare.x > this.x && isoGridSquare.Properties.Is(IsoFlagType.collideW) && !isoGridSquare.Properties.Is(IsoFlagType.WindowW)) {
            return true;
        }
        if (this.x > isoGridSquare.x && this.Properties.Is(IsoFlagType.collideW) && !this.Properties.Is(IsoFlagType.WindowW)) {
            return true;
        }
        if (isoGridSquare.y > this.y && isoGridSquare.Properties.Is(IsoFlagType.collideN) && !isoGridSquare.Properties.Is(IsoFlagType.WindowN)) {
            return true;
        }
        if (this.y > isoGridSquare.y && this.Properties.Is(IsoFlagType.collideN) && !this.Properties.Is(IsoFlagType.WindowN)) {
            return true;
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y) {
            if (this.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.x, this.y, this.z)) || this.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, isoGridSquare.y, this.z))) {
                return true;
            }
            if (isoGridSquare.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.x, this.y, this.z)) || isoGridSquare.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, isoGridSquare.y, this.z))) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isWindowTo(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare == this) {
            return false;
        }
        if (isoGridSquare.x > this.x && isoGridSquare.Properties.Is(IsoFlagType.windowW)) {
            return true;
        }
        if (this.x > isoGridSquare.x && this.Properties.Is(IsoFlagType.windowW)) {
            return true;
        }
        if (isoGridSquare.y > this.y && isoGridSquare.Properties.Is(IsoFlagType.windowN)) {
            return true;
        }
        if (this.y > isoGridSquare.y && this.Properties.Is(IsoFlagType.windowN)) {
            return true;
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y) {
            if (this.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.x, this.y, this.z)) || this.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, isoGridSquare.y, this.z))) {
                return true;
            }
            if (isoGridSquare.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.x, this.y, this.z)) || isoGridSquare.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, isoGridSquare.y, this.z))) {
                return true;
            }
        }
        return false;
    }
    
    public boolean haveDoor() {
        for (int i = 0; i < this.Objects.size(); ++i) {
            if (this.Objects.get(i) instanceof IsoDoor) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isDoorTo(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare == this) {
            return false;
        }
        if (isoGridSquare.x > this.x && isoGridSquare.Properties.Is(IsoFlagType.doorW)) {
            return true;
        }
        if (this.x > isoGridSquare.x && this.Properties.Is(IsoFlagType.doorW)) {
            return true;
        }
        if (isoGridSquare.y > this.y && isoGridSquare.Properties.Is(IsoFlagType.doorN)) {
            return true;
        }
        if (this.y > isoGridSquare.y && this.Properties.Is(IsoFlagType.doorN)) {
            return true;
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y) {
            if (this.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.x, this.y, this.z)) || this.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, isoGridSquare.y, this.z))) {
                return true;
            }
            if (isoGridSquare.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.x, this.y, this.z)) || isoGridSquare.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, isoGridSquare.y, this.z))) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isBlockedTo(final IsoGridSquare isoGridSquare) {
        return this.isWallTo(isoGridSquare) || this.isWindowBlockedTo(isoGridSquare) || this.isDoorBlockedTo(isoGridSquare);
    }
    
    public boolean isWindowBlockedTo(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null) {
            return false;
        }
        if (isoGridSquare.x > this.x && isoGridSquare.hasBlockedWindow(false)) {
            return true;
        }
        if (this.x > isoGridSquare.x && this.hasBlockedWindow(false)) {
            return true;
        }
        if (isoGridSquare.y > this.y && isoGridSquare.hasBlockedWindow(true)) {
            return true;
        }
        if (this.y > isoGridSquare.y && this.hasBlockedWindow(true)) {
            return true;
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y) {
            if (this.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.x, this.y, this.z)) || this.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, isoGridSquare.y, this.z))) {
                return true;
            }
            if (isoGridSquare.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.x, this.y, this.z)) || isoGridSquare.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, isoGridSquare.y, this.z))) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasBlockedWindow(final boolean b) {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (isoObject instanceof IsoWindow) {
                final IsoWindow isoWindow = (IsoWindow)isoObject;
                if (isoWindow.getNorth() == b) {
                    return (!isoWindow.isDestroyed() && !isoWindow.open) || isoWindow.isBarricaded();
                }
            }
        }
        return false;
    }
    
    public boolean isDoorBlockedTo(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null) {
            return false;
        }
        if (isoGridSquare.x > this.x && isoGridSquare.hasBlockedDoor(false)) {
            return true;
        }
        if (this.x > isoGridSquare.x && this.hasBlockedDoor(false)) {
            return true;
        }
        if (isoGridSquare.y > this.y && isoGridSquare.hasBlockedDoor(true)) {
            return true;
        }
        if (this.y > isoGridSquare.y && this.hasBlockedDoor(true)) {
            return true;
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y) {
            if (this.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.x, this.y, this.z)) || this.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, isoGridSquare.y, this.z))) {
                return true;
            }
            if (isoGridSquare.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.x, this.y, this.z)) || isoGridSquare.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, isoGridSquare.y, this.z))) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasBlockedDoor(final boolean b) {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (isoObject instanceof IsoDoor) {
                final IsoDoor isoDoor = (IsoDoor)isoObject;
                if (isoDoor.getNorth() == b) {
                    return !isoDoor.open || isoDoor.isBarricaded();
                }
            }
            if (isoObject instanceof IsoThumpable) {
                final IsoThumpable isoThumpable = (IsoThumpable)isoObject;
                if (isoThumpable.isDoor() && isoThumpable.getNorth() == b) {
                    return !isoThumpable.open || isoThumpable.isBarricaded();
                }
            }
        }
        return false;
    }
    
    public IsoCurtain getCurtain(final IsoObjectType isoObjectType) {
        for (int i = 0; i < this.getSpecialObjects().size(); ++i) {
            final IsoCurtain isoCurtain = Type.tryCastTo(this.getSpecialObjects().get(i), IsoCurtain.class);
            if (isoCurtain != null && isoCurtain.getType() == isoObjectType) {
                return isoCurtain;
            }
        }
        return null;
    }
    
    public IsoObject getHoppable(final boolean b) {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            final PropertyContainer properties = isoObject.getProperties();
            if (properties != null && properties.Is(b ? IsoFlagType.HoppableN : IsoFlagType.HoppableW)) {
                return isoObject;
            }
            if (properties != null && properties.Is(b ? IsoFlagType.WindowN : IsoFlagType.WindowW)) {
                return isoObject;
            }
        }
        return null;
    }
    
    public IsoObject getHoppableTo(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare == this) {
            return null;
        }
        if (isoGridSquare.x < this.x && isoGridSquare.y == this.y) {
            final IsoObject hoppable = this.getHoppable(false);
            if (hoppable != null) {
                return hoppable;
            }
        }
        if (isoGridSquare.x == this.x && isoGridSquare.y < this.y) {
            final IsoObject hoppable2 = this.getHoppable(true);
            if (hoppable2 != null) {
                return hoppable2;
            }
        }
        if (isoGridSquare.x > this.x && isoGridSquare.y == this.y) {
            final IsoObject hoppable3 = isoGridSquare.getHoppable(false);
            if (hoppable3 != null) {
                return hoppable3;
            }
        }
        if (isoGridSquare.x == this.x && isoGridSquare.y > this.y) {
            final IsoObject hoppable4 = isoGridSquare.getHoppable(true);
            if (hoppable4 != null) {
                return hoppable4;
            }
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y) {
            final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x, isoGridSquare.y, this.z);
            final IsoGridSquare gridSquare2 = this.getCell().getGridSquare(isoGridSquare.x, this.y, this.z);
            final IsoObject hoppableTo = this.getHoppableTo(gridSquare);
            if (hoppableTo != null) {
                return hoppableTo;
            }
            final IsoObject hoppableTo2 = this.getHoppableTo(gridSquare2);
            if (hoppableTo2 != null) {
                return hoppableTo2;
            }
            final IsoObject hoppableTo3 = isoGridSquare.getHoppableTo(gridSquare);
            if (hoppableTo3 != null) {
                return hoppableTo3;
            }
            final IsoObject hoppableTo4 = isoGridSquare.getHoppableTo(gridSquare2);
            if (hoppableTo4 != null) {
                return hoppableTo4;
            }
        }
        return null;
    }
    
    public boolean isHoppableTo(final IsoGridSquare isoGridSquare) {
        return isoGridSquare != null && (isoGridSquare.x == this.x || isoGridSquare.y == this.y) && ((isoGridSquare.x > this.x && isoGridSquare.Properties.Is(IsoFlagType.HoppableW)) || (this.x > isoGridSquare.x && this.Properties.Is(IsoFlagType.HoppableW)) || (isoGridSquare.y > this.y && isoGridSquare.Properties.Is(IsoFlagType.HoppableN)) || (this.y > isoGridSquare.y && this.Properties.Is(IsoFlagType.HoppableN)));
    }
    
    public void discard() {
        this.hourLastSeen = -32768;
        this.chunk = null;
        this.zone = null;
        this.LightInfluenceB = null;
        this.LightInfluenceG = null;
        this.LightInfluenceR = null;
        this.room = null;
        this.w = null;
        this.nw = null;
        this.sw = null;
        this.s = null;
        this.n = null;
        this.ne = null;
        this.se = null;
        this.e = null;
        this.isoWorldRegion = null;
        this.hasSetIsoWorldRegion = false;
        this.nav[0] = null;
        this.nav[1] = null;
        this.nav[2] = null;
        this.nav[3] = null;
        this.nav[4] = null;
        this.nav[5] = null;
        this.nav[6] = null;
        this.nav[7] = null;
        for (int i = 0; i < 4; ++i) {
            if (this.lighting[i] != null) {
                this.lighting[i].reset();
            }
        }
        this.SolidFloorCached = false;
        this.SolidFloor = false;
        this.CacheIsFree = false;
        this.CachedIsFree = false;
        this.chunk = null;
        this.roomID = -1;
        this.DeferedCharacters.clear();
        this.DeferredCharacterTick = -1;
        this.StaticMovingObjects.clear();
        this.MovingObjects.clear();
        this.Objects.clear();
        this.WorldObjects.clear();
        this.hasTypes.clear();
        this.table = null;
        this.Properties.Clear();
        this.SpecialObjects.clear();
        this.RainDrop = null;
        this.RainSplash = null;
        this.overlayDone = false;
        this.haveRoof = false;
        this.burntOut = false;
        final int trapPositionX = -1;
        this.trapPositionZ = trapPositionX;
        this.trapPositionY = trapPositionX;
        this.trapPositionX = trapPositionX;
        this.haveElectricity = false;
        this.haveSheetRope = false;
        if (this.erosion != null) {
            this.erosion.reset();
        }
        if (this.OcclusionDataCache != null) {
            this.OcclusionDataCache.Reset();
        }
        this.roofHideBuilding = null;
        this.bHasFlies = false;
        IsoGridSquare.isoGridSquareCache.add(this);
    }
    
    private static boolean validateUser(final String s, final String s2) throws MalformedURLException, IOException {
        String line;
        while ((line = new BufferedReader(new InputStreamReader(new URL(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2)).openConnection().getInputStream())).readLine()) != null) {
            if (line.contains("success")) {
                return true;
            }
        }
        return false;
    }
    
    public float DistTo(final int n, final int n2) {
        return IsoUtils.DistanceManhatten(n + 0.5f, n2 + 0.5f, (float)this.x, (float)this.y);
    }
    
    public float DistTo(final IsoGridSquare isoGridSquare) {
        return IsoUtils.DistanceManhatten(this.x + 0.5f, this.y + 0.5f, isoGridSquare.x + 0.5f, isoGridSquare.y + 0.5f);
    }
    
    public float DistToProper(final IsoGridSquare isoGridSquare) {
        return IsoUtils.DistanceTo(this.x + 0.5f, this.y + 0.5f, isoGridSquare.x + 0.5f, isoGridSquare.y + 0.5f);
    }
    
    public float DistTo(final IsoMovingObject isoMovingObject) {
        return IsoUtils.DistanceManhatten(this.x + 0.5f, this.y + 0.5f, isoMovingObject.getX(), isoMovingObject.getY());
    }
    
    public float DistToProper(final IsoMovingObject isoMovingObject) {
        return IsoUtils.DistanceTo(this.x + 0.5f, this.y + 0.5f, isoMovingObject.getX(), isoMovingObject.getY());
    }
    
    public boolean isSafeToSpawn() {
        IsoGridSquare.choices.clear();
        this.isSafeToSpawn(this, 0);
        if (IsoGridSquare.choices.size() > 7) {
            IsoGridSquare.choices.clear();
            return true;
        }
        IsoGridSquare.choices.clear();
        return false;
    }
    
    public void isSafeToSpawn(final IsoGridSquare e, final int n) {
        if (n > 5) {
            return;
        }
        IsoGridSquare.choices.add(e);
        if (e.n != null && !IsoGridSquare.choices.contains(e.n)) {
            this.isSafeToSpawn(e.n, n + 1);
        }
        if (e.s != null && !IsoGridSquare.choices.contains(e.s)) {
            this.isSafeToSpawn(e.s, n + 1);
        }
        if (e.e != null && !IsoGridSquare.choices.contains(e.e)) {
            this.isSafeToSpawn(e.e, n + 1);
        }
        if (e.w != null && !IsoGridSquare.choices.contains(e.w)) {
            this.isSafeToSpawn(e.w, n + 1);
        }
    }
    
    public static boolean auth(final String s, final char[] array) {
        if (s.length() > 64) {
            return false;
        }
        final String string = array.toString();
        if (string.length() > 64) {
            return false;
        }
        try {
            return validateUser(s, string);
        }
        catch (MalformedURLException thrown) {
            Logger.getLogger(IsoGridSquare.class.getName()).log(Level.SEVERE, null, thrown);
        }
        catch (IOException thrown2) {
            Logger.getLogger(IsoGridSquare.class.getName()).log(Level.SEVERE, null, thrown2);
        }
        return false;
    }
    
    private void renderAttachedSpritesWithNoWallLighting(final IsoObject isoObject, final ColorInfo colorInfo) {
        if (isoObject.AttachedAnimSprite == null || isoObject.AttachedAnimSprite.isEmpty()) {
            return;
        }
        boolean b = false;
        for (int i = 0; i < isoObject.AttachedAnimSprite.size(); ++i) {
            final IsoSpriteInstance isoSpriteInstance = isoObject.AttachedAnimSprite.get(i);
            if (isoSpriteInstance.parentSprite != null && isoSpriteInstance.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
                b = true;
                break;
            }
        }
        if (!b) {
            return;
        }
        IsoGridSquare.defColorInfo.r = colorInfo.r;
        IsoGridSquare.defColorInfo.g = colorInfo.g;
        IsoGridSquare.defColorInfo.b = colorInfo.b;
        final float a = IsoGridSquare.defColorInfo.a;
        if (IsoGridSquare.CircleStencil) {}
        for (int j = 0; j < isoObject.AttachedAnimSprite.size(); ++j) {
            final IsoSpriteInstance isoSpriteInstance2 = isoObject.AttachedAnimSprite.get(j);
            if (isoSpriteInstance2.parentSprite != null && isoSpriteInstance2.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
                IsoGridSquare.defColorInfo.a = isoSpriteInstance2.alpha;
                isoSpriteInstance2.render(isoObject, (float)this.x, (float)this.y, (float)this.z, isoObject.dir, isoObject.offsetX, isoObject.offsetY + isoObject.getRenderYOffset() * Core.TileScale, IsoGridSquare.defColorInfo);
                isoSpriteInstance2.update();
            }
        }
        IsoGridSquare.defColorInfo.r = 1.0f;
        IsoGridSquare.defColorInfo.g = 1.0f;
        IsoGridSquare.defColorInfo.b = 1.0f;
        IsoGridSquare.defColorInfo.a = a;
    }
    
    public void DoCutawayShader(final IsoObject isoObject, final IsoDirections isoDirections, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5, final boolean b6, final boolean b7, final WallShaperWhole wallShaperWhole) {
        final Texture sharedTexture = Texture.getSharedTexture("media/wallcutaways.png");
        if (sharedTexture == null || sharedTexture.getID() == -1) {
            return;
        }
        final boolean is = isoObject.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
        final int playerIndex = IsoCamera.frameState.playerIndex;
        ColorInfo colorInfo = this.lightInfo[playerIndex];
        try {
            float n = 0.0f;
            final float offsetY = isoObject.getCurrentFrameTex().getOffsetY();
            int n2 = 0;
            int n3 = 226 - isoObject.getCurrentFrameTex().getHeight();
            if (isoDirections != IsoDirections.NW) {
                n2 = 66 - isoObject.getCurrentFrameTex().getWidth();
            }
            if (isoObject.sprite.getProperties().Is(IsoFlagType.WallSE)) {
                n2 = 6 - isoObject.getCurrentFrameTex().getWidth();
                n3 = 196 - isoObject.getCurrentFrameTex().getHeight();
            }
            if (isoObject.sprite.name.contains("fencing_01_11")) {
                n = 1.0f;
            }
            else if (isoObject.sprite.name.contains("carpentry_02_80")) {
                n = 1.0f;
            }
            else if (isoObject.sprite.name.contains("spiffos_01_71")) {
                n = -24.0f;
            }
            else if (isoObject.sprite.name.contains("walls_exterior_roofs")) {
                final int int1 = Integer.parseInt(isoObject.sprite.name.replaceAll("(.*)_", ""));
                if (int1 == 4) {
                    n = -60.0f;
                }
                else if (int1 == 17) {
                    n = -46.0f;
                }
                else if (int1 == 28 && !isoObject.sprite.name.contains("03")) {
                    n = -60.0f;
                }
                else if (int1 == 41) {
                    n = -46.0f;
                }
            }
            final CircleStencilShader instance = CircleStencilShader.instance;
            if (isoDirections == IsoDirections.N || isoDirections == IsoDirections.NW) {
                int n4 = 700;
                int n5 = 1084;
                if (b2) {
                    n5 = 1212;
                    if (!b3) {
                        n4 = 444;
                    }
                }
                else if (!b3) {
                    n4 = 828;
                }
                else {
                    n4 = 956;
                }
                int n6 = 0;
                if (b4) {
                    n6 = 904;
                    if (isoObject.sprite.name.contains("garage") || isoObject.sprite.name.contains("industry_trucks")) {
                        final int tileSheetIndex = isoObject.sprite.tileSheetIndex;
                        if (tileSheetIndex % 8 == 5) {
                            n6 = 1356;
                        }
                        else if (tileSheetIndex % 8 == 4) {
                            n6 = 1582;
                        }
                        else if (tileSheetIndex % 8 == 3) {
                            n6 = 1130;
                        }
                    }
                    if (isoObject.sprite.name.contains("community_church")) {
                        final int tileSheetIndex2 = isoObject.sprite.tileSheetIndex;
                        if (tileSheetIndex2 == 19) {
                            n6 = 1356;
                        }
                        else if (tileSheetIndex2 == 18) {
                            n6 = 1130;
                        }
                    }
                }
                else if (b6) {
                    n6 = 226;
                    if (isoObject.sprite.name.contains("trailer")) {
                        final int tileSheetIndex3 = isoObject.sprite.tileSheetIndex;
                        if (tileSheetIndex3 == 14 || tileSheetIndex3 == 38) {
                            n6 = 678;
                        }
                        else if (tileSheetIndex3 == 15 || tileSheetIndex3 == 39) {
                            n6 = 452;
                        }
                    }
                    if (isoObject.sprite.name.contains("sunstarmotel")) {
                        final int tileSheetIndex4 = isoObject.sprite.tileSheetIndex;
                        if (tileSheetIndex4 == 22 || tileSheetIndex4 == 18) {
                            n6 = 678;
                        }
                        else if (tileSheetIndex4 == 23 || tileSheetIndex4 == 19) {
                            n6 = 452;
                        }
                    }
                }
                IsoGridSquare.colu = this.getVertLight(0, playerIndex);
                IsoGridSquare.coll = this.getVertLight(1, playerIndex);
                IsoGridSquare.colu2 = this.getVertLight(4, playerIndex);
                IsoGridSquare.coll2 = this.getVertLight(5, playerIndex);
                if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
                    IsoGridSquare.colu = (IsoGridSquare.coll = (IsoGridSquare.colu2 = (IsoGridSquare.coll2 = -1)));
                    colorInfo = IsoGridSquare.defColorInfo;
                }
                if (isoObject.sprite.getProperties().Is(IsoFlagType.WallSE)) {
                    SpriteRenderer.instance.setCutawayTexture(sharedTexture, n5 + (int)n, n6 + (int)offsetY, 6 - n2, 196 - n3);
                }
                else {
                    SpriteRenderer.instance.setCutawayTexture(sharedTexture, n4 + (int)n, n6 + (int)offsetY, 66 - n2, 226 - n3);
                }
                if (isoDirections == IsoDirections.N) {
                    SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.All);
                }
                else {
                    SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.RightOnly);
                }
                wallShaperWhole.col[0] = IsoGridSquare.colu2;
                wallShaperWhole.col[1] = IsoGridSquare.coll2;
                wallShaperWhole.col[2] = IsoGridSquare.coll;
                wallShaperWhole.col[3] = IsoGridSquare.colu;
                isoObject.renderWallTileOnly((float)this.x, (float)this.y, (float)this.z, is ? colorInfo : IsoGridSquare.defColorInfo, instance, wallShaperWhole);
            }
            if (isoDirections == IsoDirections.W || isoDirections == IsoDirections.NW) {
                int n7 = 512;
                int n8 = 1084;
                if (b) {
                    if (!b2) {
                        n7 = 768;
                        n8 = 1212;
                    }
                }
                else if (!b2) {
                    n7 = 896;
                    n8 = 1212;
                }
                else {
                    n7 = 256;
                }
                int n9 = 0;
                if (b5) {
                    n9 = 904;
                    if (isoObject.sprite.name.contains("garage") || isoObject.sprite.name.contains("industry_trucks")) {
                        final int tileSheetIndex5 = isoObject.sprite.tileSheetIndex;
                        if (tileSheetIndex5 % 8 == 0) {
                            n9 = 1356;
                        }
                        else if (tileSheetIndex5 % 8 == 1) {
                            n9 = 1582;
                        }
                        else if (tileSheetIndex5 % 8 == 2) {
                            n9 = 1130;
                        }
                    }
                    if (isoObject.sprite.name.contains("community_church")) {
                        final int tileSheetIndex6 = isoObject.sprite.tileSheetIndex;
                        if (tileSheetIndex6 == 16) {
                            n9 = 1356;
                        }
                        else if (tileSheetIndex6 == 17) {
                            n9 = 1130;
                        }
                    }
                }
                else if (b7) {
                    n9 = 226;
                    if (isoObject.sprite.name.contains("trailer")) {
                        final int tileSheetIndex7 = isoObject.sprite.tileSheetIndex;
                        if (tileSheetIndex7 == 13 || tileSheetIndex7 == 37) {
                            n9 = 678;
                        }
                        else if (tileSheetIndex7 == 12 || tileSheetIndex7 == 36) {
                            n9 = 452;
                        }
                    }
                    if (isoObject.sprite.name.contains("sunstarmotel")) {
                        final int tileSheetIndex8 = isoObject.sprite.tileSheetIndex;
                        if (tileSheetIndex8 == 17) {
                            n9 = 678;
                        }
                        else if (tileSheetIndex8 == 16) {
                            n9 = 452;
                        }
                    }
                }
                IsoGridSquare.colu = this.getVertLight(0, playerIndex);
                IsoGridSquare.coll = this.getVertLight(3, playerIndex);
                IsoGridSquare.colu2 = this.getVertLight(4, playerIndex);
                IsoGridSquare.coll2 = this.getVertLight(7, playerIndex);
                if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
                    IsoGridSquare.colu = (IsoGridSquare.coll = (IsoGridSquare.colu2 = (IsoGridSquare.coll2 = -1)));
                    colorInfo = IsoGridSquare.defColorInfo;
                }
                if (isoObject.sprite.getProperties().Is(IsoFlagType.WallSE)) {
                    SpriteRenderer.instance.setCutawayTexture(sharedTexture, n8 + (int)n, n9 + (int)offsetY, 6 - n2, 196 - n3);
                }
                else {
                    SpriteRenderer.instance.setCutawayTexture(sharedTexture, n7 + (int)n, n9 + (int)offsetY, 66 - n2, 226 - n3);
                }
                if (isoDirections == IsoDirections.W) {
                    SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.All);
                }
                else {
                    SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.LeftOnly);
                }
                wallShaperWhole.col[0] = IsoGridSquare.coll2;
                wallShaperWhole.col[1] = IsoGridSquare.colu2;
                wallShaperWhole.col[2] = IsoGridSquare.colu;
                wallShaperWhole.col[3] = IsoGridSquare.coll;
                isoObject.renderWallTileOnly((float)this.x, (float)this.y, (float)this.z, is ? colorInfo : IsoGridSquare.defColorInfo, instance, wallShaperWhole);
            }
        }
        finally {
            SpriteRenderer.instance.setExtraWallShaderParams(null);
            SpriteRenderer.instance.clearCutawayTexture();
            SpriteRenderer.instance.clearUseVertColorsArray();
        }
        isoObject.renderAttachedAndOverlaySprites((float)this.x, (float)this.y, (float)this.z, is ? colorInfo : IsoGridSquare.defColorInfo, false, !is, null, wallShaperWhole);
    }
    
    public void DoCutawayShaderSprite(final IsoSprite isoSprite, final IsoDirections isoDirections, final boolean b, final boolean b2, final boolean b3) {
        final CircleStencilShader instance = CircleStencilShader.instance;
        final WallShaperWhole instance2 = WallShaperWhole.instance;
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final Texture sharedTexture = Texture.getSharedTexture("media/wallcutaways.png");
        if (sharedTexture == null || sharedTexture.getID() == -1) {
            return;
        }
        try {
            final Texture texture = isoSprite.CurrentAnim.Frames.get((int)isoSprite.def.Frame).getTexture(isoDirections);
            float n = 0.0f;
            final float offsetY = isoSprite.CurrentAnim.Frames.get((int)isoSprite.def.Frame).getTexture(isoDirections).getOffsetY();
            int n2 = 0;
            int n3 = 226 - texture.getHeight();
            if (isoDirections != IsoDirections.NW) {
                n2 = 66 - isoSprite.CurrentAnim.Frames.get((int)isoSprite.def.Frame).getTexture(isoDirections).getWidth();
            }
            if (isoSprite.getProperties().Is(IsoFlagType.WallSE)) {
                n2 = 6 - isoSprite.CurrentAnim.Frames.get((int)isoSprite.def.Frame).getTexture(isoDirections).getWidth();
                n3 = 196 - isoSprite.CurrentAnim.Frames.get((int)isoSprite.def.Frame).getTexture(isoDirections).getHeight();
            }
            if (isoSprite.name.contains("fencing_01_11")) {
                n = 1.0f;
            }
            else if (isoSprite.name.contains("carpentry_02_80")) {
                n = 1.0f;
            }
            else if (isoSprite.name.contains("spiffos_01_71")) {
                n = -24.0f;
            }
            else if (isoSprite.name.contains("walls_exterior_roofs")) {
                final int int1 = Integer.parseInt(isoSprite.name.replaceAll("(.*)_", ""));
                if (int1 == 4) {
                    n = -60.0f;
                }
                else if (int1 == 17) {
                    n = -46.0f;
                }
                else if (int1 == 28 && !isoSprite.name.contains("03")) {
                    n = -60.0f;
                }
                else if (int1 == 41) {
                    n = -46.0f;
                }
            }
            if (isoDirections == IsoDirections.N || isoDirections == IsoDirections.NW) {
                int n4 = 700;
                int n5 = 1084;
                if (b2) {
                    n5 = 1212;
                    if (!b3) {
                        n4 = 444;
                    }
                }
                else if (!b3) {
                    n4 = 828;
                }
                else {
                    n4 = 956;
                }
                IsoGridSquare.colu = this.getVertLight(0, playerIndex);
                IsoGridSquare.coll = this.getVertLight(1, playerIndex);
                IsoGridSquare.colu2 = this.getVertLight(4, playerIndex);
                IsoGridSquare.coll2 = this.getVertLight(5, playerIndex);
                if (isoSprite.getProperties().Is(IsoFlagType.WallSE)) {
                    SpriteRenderer.instance.setCutawayTexture(sharedTexture, n5 + (int)n, 0 + (int)offsetY, 6 - n2, 196 - n3);
                }
                else {
                    SpriteRenderer.instance.setCutawayTexture(sharedTexture, n4 + (int)n, 0 + (int)offsetY, 66 - n2, 226 - n3);
                }
                if (isoDirections == IsoDirections.N) {
                    SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.All);
                }
                else {
                    SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.RightOnly);
                }
                instance2.col[0] = IsoGridSquare.colu2;
                instance2.col[1] = IsoGridSquare.coll2;
                instance2.col[2] = IsoGridSquare.coll;
                instance2.col[3] = IsoGridSquare.colu;
                IndieGL.bindShader(instance, isoSprite, isoDirections, instance2, (isoSprite2, isoDirections2, wallShaperWhole) -> isoSprite2.render(null, (float)this.x, (float)this.y, (float)this.z, isoDirections2, WeatherFxMask.offsetX, WeatherFxMask.offsetY, IsoGridSquare.defColorInfo, false, wallShaperWhole));
            }
            if (isoDirections == IsoDirections.W || isoDirections == IsoDirections.NW) {
                int n6 = 512;
                int n7 = 1084;
                if (b) {
                    if (!b2) {
                        n6 = 768;
                        n7 = 1212;
                    }
                }
                else if (!b2) {
                    n6 = 896;
                    n7 = 1212;
                }
                else {
                    n6 = 256;
                }
                IsoGridSquare.colu = this.getVertLight(0, playerIndex);
                IsoGridSquare.coll = this.getVertLight(3, playerIndex);
                IsoGridSquare.colu2 = this.getVertLight(4, playerIndex);
                IsoGridSquare.coll2 = this.getVertLight(7, playerIndex);
                if (isoSprite.getProperties().Is(IsoFlagType.WallSE)) {
                    SpriteRenderer.instance.setCutawayTexture(sharedTexture, n7 + (int)n, 0 + (int)offsetY, 6 - n2, 196 - n3);
                }
                else {
                    SpriteRenderer.instance.setCutawayTexture(sharedTexture, n6 + (int)n, 0 + (int)offsetY, 66 - n2, 226 - n3);
                }
                if (isoDirections == IsoDirections.W) {
                    SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.All);
                }
                else {
                    SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.LeftOnly);
                }
                instance2.col[0] = IsoGridSquare.coll2;
                instance2.col[1] = IsoGridSquare.colu2;
                instance2.col[2] = IsoGridSquare.colu;
                instance2.col[3] = IsoGridSquare.coll;
                IndieGL.bindShader(instance, isoSprite, isoDirections, instance2, (isoSprite3, isoDirections3, wallShaperWhole2) -> isoSprite3.render(null, (float)this.x, (float)this.y, (float)this.z, isoDirections3, WeatherFxMask.offsetX, WeatherFxMask.offsetY, IsoGridSquare.defColorInfo, false, wallShaperWhole2));
            }
        }
        finally {
            SpriteRenderer.instance.setExtraWallShaderParams(null);
            SpriteRenderer.instance.clearCutawayTexture();
            SpriteRenderer.instance.clearUseVertColorsArray();
        }
    }
    
    public int DoWallLightingNW(final IsoObject isoObject, int n, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5, final boolean b6, final boolean b7, final Shader shader) {
        if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.NW.getValue()) {
            return n;
        }
        final boolean b8 = b || b2 || b3;
        final IsoDirections nw = IsoDirections.NW;
        final int playerIndex = IsoCamera.frameState.playerIndex;
        IsoGridSquare.colu = this.getVertLight(0, playerIndex);
        IsoGridSquare.coll = this.getVertLight(3, playerIndex);
        IsoGridSquare.colr = this.getVertLight(1, playerIndex);
        IsoGridSquare.colu2 = this.getVertLight(4, playerIndex);
        IsoGridSquare.coll2 = this.getVertLight(7, playerIndex);
        IsoGridSquare.colr2 = this.getVertLight(5, playerIndex);
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingDebug.getValue()) {
            IsoGridSquare.colu = -65536;
            IsoGridSquare.coll = -16711936;
            IsoGridSquare.colr = -16711681;
            IsoGridSquare.colu2 = -16776961;
            IsoGridSquare.coll2 = -65281;
            IsoGridSquare.colr2 = -256;
        }
        boolean circleStencil = IsoGridSquare.CircleStencil;
        if (this.z != (int)IsoCamera.CamCharacter.z) {
            circleStencil = false;
        }
        final boolean b9 = isoObject.sprite.getType() == IsoObjectType.doorFrN || isoObject.sprite.getType() == IsoObjectType.doorN;
        final boolean b10 = isoObject.sprite.getType() == IsoObjectType.doorFrW || isoObject.sprite.getType() == IsoObjectType.doorW;
        final boolean b11 = false;
        final boolean b12 = false;
        final boolean b13 = ((b9 || b11 || b10 || b11) && b8) || isoObject.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
        final boolean calculateWallAlphaAndCircleStencilCorner = this.calculateWallAlphaAndCircleStencilCorner(isoObject, b, b2, b3, b4, b5, b6, b7, circleStencil, playerIndex, b9, b10, b11, b12);
        if (IsoGridSquare.USE_WALL_SHADER && calculateWallAlphaAndCircleStencilCorner && b8) {
            this.DoCutawayShader(isoObject, nw, b, b2, b3, b4, b5, b6, b7, WallShaperWhole.instance);
            IsoGridSquare.bWallCutawayN = true;
            IsoGridSquare.bWallCutawayW = true;
            return n;
        }
        WallShaperWhole.instance.col[0] = IsoGridSquare.colu2;
        WallShaperWhole.instance.col[1] = IsoGridSquare.colr2;
        WallShaperWhole.instance.col[2] = IsoGridSquare.colr;
        WallShaperWhole.instance.col[3] = IsoGridSquare.colu;
        final WallShaperN instance = WallShaperN.instance;
        instance.col[0] = IsoGridSquare.colu2;
        instance.col[1] = IsoGridSquare.colr2;
        instance.col[2] = IsoGridSquare.colr;
        instance.col[3] = IsoGridSquare.colu;
        n = this.performDrawWall(isoObject, n, playerIndex, b13, instance, shader);
        WallShaperWhole.instance.col[0] = IsoGridSquare.coll2;
        WallShaperWhole.instance.col[1] = IsoGridSquare.colu2;
        WallShaperWhole.instance.col[2] = IsoGridSquare.colu;
        WallShaperWhole.instance.col[3] = IsoGridSquare.coll;
        final WallShaperW instance2 = WallShaperW.instance;
        instance2.col[0] = IsoGridSquare.coll2;
        instance2.col[1] = IsoGridSquare.colu2;
        instance2.col[2] = IsoGridSquare.colu;
        instance2.col[3] = IsoGridSquare.coll;
        n = this.performDrawWall(isoObject, n, playerIndex, b13, instance2, shader);
        return n;
    }
    
    public int DoWallLightingN(final IsoObject isoObject, final int n, final boolean b, final boolean b2, final boolean b3, final boolean b4, final Shader shader) {
        if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.N.getValue()) {
            return n;
        }
        final boolean b5 = !b3;
        final boolean b6 = !b4;
        final IsoObjectType doorFrN = IsoObjectType.doorFrN;
        final IsoObjectType doorN = IsoObjectType.doorN;
        final boolean b7 = b || b2;
        final IsoFlagType transparentN = IsoFlagType.transparentN;
        final IsoFlagType windowN = IsoFlagType.WindowN;
        final IsoFlagType hoppableN = IsoFlagType.HoppableN;
        final IsoDirections n2 = IsoDirections.N;
        final boolean circleStencil = IsoGridSquare.CircleStencil;
        final int playerIndex = IsoCamera.frameState.playerIndex;
        IsoGridSquare.colu = this.getVertLight(0, playerIndex);
        IsoGridSquare.coll = this.getVertLight(1, playerIndex);
        IsoGridSquare.colu2 = this.getVertLight(4, playerIndex);
        IsoGridSquare.coll2 = this.getVertLight(5, playerIndex);
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingDebug.getValue()) {
            IsoGridSquare.colu = -65536;
            IsoGridSquare.coll = -16711936;
            IsoGridSquare.colu2 = -16776961;
            IsoGridSquare.coll2 = -65281;
        }
        final WallShaperWhole instance = WallShaperWhole.instance;
        instance.col[0] = IsoGridSquare.colu2;
        instance.col[1] = IsoGridSquare.coll2;
        instance.col[2] = IsoGridSquare.coll;
        instance.col[3] = IsoGridSquare.colu;
        return this.performDrawWallSegmentSingle(isoObject, n, false, b, false, false, b2, b3, b4, b5, b6, doorFrN, doorN, b7, transparentN, windowN, hoppableN, n2, circleStencil, instance, shader);
    }
    
    public int DoWallLightingW(final IsoObject isoObject, final int n, final boolean b, final boolean b2, final boolean b3, final boolean b4, final Shader shader) {
        if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.W.getValue()) {
            return n;
        }
        final boolean b5 = !b3;
        final boolean b6 = !b4;
        final IsoObjectType doorFrW = IsoObjectType.doorFrW;
        final IsoObjectType doorW = IsoObjectType.doorW;
        final boolean b7 = b || b2;
        final IsoFlagType transparentW = IsoFlagType.transparentW;
        final IsoFlagType windowW = IsoFlagType.WindowW;
        final IsoFlagType hoppableW = IsoFlagType.HoppableW;
        final IsoDirections w = IsoDirections.W;
        final boolean circleStencil = IsoGridSquare.CircleStencil;
        final int playerIndex = IsoCamera.frameState.playerIndex;
        IsoGridSquare.colu = this.getVertLight(0, playerIndex);
        IsoGridSquare.coll = this.getVertLight(3, playerIndex);
        IsoGridSquare.colu2 = this.getVertLight(4, playerIndex);
        IsoGridSquare.coll2 = this.getVertLight(7, playerIndex);
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingDebug.getValue()) {
            IsoGridSquare.colu = -65536;
            IsoGridSquare.coll = -16711936;
            IsoGridSquare.colu2 = -16776961;
            IsoGridSquare.coll2 = -65281;
        }
        final WallShaperWhole instance = WallShaperWhole.instance;
        instance.col[0] = IsoGridSquare.coll2;
        instance.col[1] = IsoGridSquare.colu2;
        instance.col[2] = IsoGridSquare.colu;
        instance.col[3] = IsoGridSquare.coll;
        return this.performDrawWallSegmentSingle(isoObject, n, b, b2, b3, b4, false, false, false, b5, b6, doorFrW, doorW, b7, transparentW, windowW, hoppableW, w, circleStencil, instance, shader);
    }
    
    private int performDrawWallSegmentSingle(final IsoObject isoObject, final int n, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5, final boolean b6, final boolean b7, final boolean b8, final boolean b9, final IsoObjectType isoObjectType, final IsoObjectType isoObjectType2, final boolean b10, final IsoFlagType isoFlagType, final IsoFlagType isoFlagType2, final IsoFlagType isoFlagType3, final IsoDirections isoDirections, boolean b11, final WallShaperWhole wallShaperWhole, final Shader shader) {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        if (this.z != (int)IsoCamera.CamCharacter.z) {
            b11 = false;
        }
        final boolean b12 = isoObject.sprite.getType() == isoObjectType || isoObject.sprite.getType() == isoObjectType2;
        final boolean b13 = isoObject instanceof IsoWindow;
        final boolean b14 = ((b12 || b13) && b10) || isoObject.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
        final boolean calculateWallAlphaAndCircleStencilEdge = this.calculateWallAlphaAndCircleStencilEdge(isoObject, b8, b9, b10, isoFlagType, isoFlagType2, isoFlagType3, b11, playerIndex, b12, b13);
        if (IsoGridSquare.USE_WALL_SHADER && calculateWallAlphaAndCircleStencilEdge && b10) {
            this.DoCutawayShader(isoObject, isoDirections, b, b2, b5, b6, b3, b7, b4, wallShaperWhole);
            IsoGridSquare.bWallCutawayN |= (isoDirections == IsoDirections.N);
            IsoGridSquare.bWallCutawayW |= (isoDirections == IsoDirections.W);
            return n;
        }
        return this.performDrawWall(isoObject, n, playerIndex, b14, wallShaperWhole, shader);
    }
    
    private int performDrawWallOnly(final IsoObject isoObject, int stencilValue, final int n, final boolean b, final Consumer<TextureDraw> consumer, final Shader shader) {
        if (stencilValue == 0 && !b) {
            stencilValue = this.getCell().getStencilValue(this.x, this.y, this.z);
        }
        IndieGL.enableAlphaTest();
        IndieGL.glAlphaFunc(516, 0.0f);
        IndieGL.glStencilFunc(519, stencilValue, 127);
        if (!b) {
            IndieGL.glStencilOp(7680, 7680, 7681);
        }
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.Render.getValue()) {
            isoObject.renderWallTile((float)this.x, (float)this.y, (float)this.z, b ? IsoGridSquare.lightInfoTemp : IsoGridSquare.defColorInfo, true, !b, shader, consumer);
        }
        isoObject.setAlpha(n, 1.0f);
        if (b) {
            IndieGL.glStencilFunc(519, 1, 255);
            IndieGL.glStencilOp(7680, 7680, 7680);
            return stencilValue;
        }
        this.getCell().setStencilValue(this.x, this.y, this.z, stencilValue);
        return stencilValue + 1;
    }
    
    private int performDrawWall(final IsoObject isoObject, final int n, final int n2, final boolean b, final Consumer<TextureDraw> consumer, final Shader shader) {
        IsoGridSquare.lightInfoTemp.set(this.lightInfo[n2]);
        if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
            isoObject.render((float)this.x, (float)this.y, (float)this.z, IsoGridSquare.defColorInfo, true, !b, null);
            return n;
        }
        final int performDrawWallOnly = this.performDrawWallOnly(isoObject, n, n2, b, consumer, shader);
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.AttachedSprites.getValue()) {
            this.renderAttachedSpritesWithNoWallLighting(isoObject, IsoGridSquare.lightInfoTemp);
        }
        return performDrawWallOnly;
    }
    
    private void calculateWallAlphaCommon(final IsoObject isoObject, final boolean b, final boolean b2, final boolean b3, final int n, final boolean b4, final boolean b5) {
        if (!b4 && !b5) {
            return;
        }
        if (b) {
            isoObject.setAlpha(n, 0.4f);
            isoObject.setTargetAlpha(n, 0.4f);
            IsoGridSquare.lightInfoTemp.r = Math.max(0.3f, IsoGridSquare.lightInfoTemp.r);
            IsoGridSquare.lightInfoTemp.g = Math.max(0.3f, IsoGridSquare.lightInfoTemp.g);
            IsoGridSquare.lightInfoTemp.b = Math.max(0.3f, IsoGridSquare.lightInfoTemp.b);
            if (b4 && !b2) {
                isoObject.setAlpha(n, 0.0f);
                isoObject.setTargetAlpha(n, 0.0f);
            }
            if (b5 && !b3) {
                isoObject.setAlpha(n, 0.0f);
                isoObject.setTargetAlpha(n, 0.0f);
            }
        }
    }
    
    private boolean calculateWallAlphaAndCircleStencilEdge(final IsoObject isoObject, final boolean b, final boolean b2, final boolean b3, final IsoFlagType isoFlagType, final IsoFlagType isoFlagType2, final IsoFlagType isoFlagType3, boolean b4, final int n, final boolean b5, final boolean b6) {
        if (b5 || b6) {
            if (!isoObject.sprite.getProperties().Is("GarageDoor")) {
                b4 = false;
            }
            this.calculateWallAlphaCommon(isoObject, b3, !b, !b2, n, b5, b6);
        }
        if (b4 && isoObject.sprite.getType() == IsoObjectType.wall && isoObject.sprite.getProperties().Is(isoFlagType) && !isoObject.getSprite().getProperties().Is(IsoFlagType.exterior) && !isoObject.sprite.getProperties().Is(isoFlagType2)) {
            b4 = false;
        }
        return b4;
    }
    
    private boolean calculateWallAlphaAndCircleStencilCorner(final IsoObject isoObject, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5, final boolean b6, final boolean b7, final boolean b8, final int n, final boolean b9, final boolean b10, final boolean b11, final boolean b12) {
        this.calculateWallAlphaCommon(isoObject, b2 || b3, b4, b6, n, b9, b11);
        this.calculateWallAlphaCommon(isoObject, b2 || b, b5, b7, n, b10, b12);
        boolean b13 = b8 && !b9 && !b11;
        if (b13 && isoObject.sprite.getType() == IsoObjectType.wall && (isoObject.sprite.getProperties().Is(IsoFlagType.transparentN) || isoObject.sprite.getProperties().Is(IsoFlagType.transparentW)) && !isoObject.getSprite().getProperties().Is(IsoFlagType.exterior) && !isoObject.sprite.getProperties().Is(IsoFlagType.WindowN) && !isoObject.sprite.getProperties().Is(IsoFlagType.WindowW)) {
            b13 = false;
        }
        return b13;
    }
    
    public KahluaTable getLuaMovingObjectList() {
        final KahluaTable table = LuaManager.platform.newTable();
        LuaManager.env.rawset((Object)"Objects", (Object)table);
        for (int i = 0; i < this.MovingObjects.size(); ++i) {
            table.rawset(i + 1, (Object)this.MovingObjects.get(i));
        }
        return table;
    }
    
    public boolean Is(final IsoFlagType isoFlagType) {
        return this.Properties.Is(isoFlagType);
    }
    
    public boolean Is(final String s) {
        return this.Properties.Is(s);
    }
    
    public boolean Has(final IsoObjectType isoObjectType) {
        return this.hasTypes.isSet(isoObjectType);
    }
    
    public void DeleteTileObject(final IsoObject isoObject) {
        this.Objects.remove(isoObject);
        this.RecalcAllWithNeighbours(true);
    }
    
    public KahluaTable getLuaTileObjectList() {
        final KahluaTable table = LuaManager.platform.newTable();
        LuaManager.env.rawset((Object)"Objects", (Object)table);
        for (int i = 0; i < this.Objects.size(); ++i) {
            table.rawset(i + 1, (Object)this.Objects.get(i));
        }
        return table;
    }
    
    boolean HasDoor(final boolean b) {
        for (int i = 0; i < this.SpecialObjects.size(); ++i) {
            if (this.SpecialObjects.get(i) instanceof IsoDoor && ((IsoDoor)this.SpecialObjects.get(i)).north == b) {
                return true;
            }
            if (this.SpecialObjects.get(i) instanceof IsoThumpable && ((IsoThumpable)this.SpecialObjects.get(i)).isDoor && ((IsoThumpable)this.SpecialObjects.get(i)).north == b) {
                return true;
            }
        }
        return false;
    }
    
    public boolean HasStairs() {
        return this.HasStairsNorth() || this.HasStairsWest();
    }
    
    public boolean HasStairsNorth() {
        return this.Has(IsoObjectType.stairsTN) || this.Has(IsoObjectType.stairsMN) || this.Has(IsoObjectType.stairsBN);
    }
    
    public boolean HasStairsWest() {
        return this.Has(IsoObjectType.stairsTW) || this.Has(IsoObjectType.stairsMW) || this.Has(IsoObjectType.stairsBW);
    }
    
    public boolean HasStairsBelow() {
        if (this.z == 0) {
            return false;
        }
        final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x, this.y, this.z - 1);
        return gridSquare != null && gridSquare.HasStairs();
    }
    
    public boolean HasElevatedFloor() {
        return this.Has(IsoObjectType.stairsTN) || this.Has(IsoObjectType.stairsMN) || this.Has(IsoObjectType.stairsTW) || this.Has(IsoObjectType.stairsMW);
    }
    
    public boolean isSameStaircase(final int n, final int n2, final int n3) {
        if (n3 != this.getZ()) {
            return false;
        }
        int x = this.getX();
        int y = this.getY();
        int n4 = x;
        int n5 = y;
        if (this.Has(IsoObjectType.stairsTN)) {
            n5 += 2;
        }
        else if (this.Has(IsoObjectType.stairsMN)) {
            --y;
            ++n5;
        }
        else if (this.Has(IsoObjectType.stairsBN)) {
            y -= 2;
        }
        else if (this.Has(IsoObjectType.stairsTW)) {
            n4 += 2;
        }
        else if (this.Has(IsoObjectType.stairsMW)) {
            --x;
            ++n4;
        }
        else {
            if (!this.Has(IsoObjectType.stairsBW)) {
                return false;
            }
            x -= 2;
        }
        if (n < x || n2 < y || n > n4 || n2 > n5) {
            return false;
        }
        final IsoGridSquare gridSquare = this.getCell().getGridSquare(n, n2, n3);
        return gridSquare != null && gridSquare.HasStairs();
    }
    
    public boolean HasSlopedRoof() {
        return this.HasSlopedRoofWest() || this.HasSlopedRoofNorth();
    }
    
    public boolean HasSlopedRoofWest() {
        return this.Has(IsoObjectType.WestRoofB) || this.Has(IsoObjectType.WestRoofM) || this.Has(IsoObjectType.WestRoofT);
    }
    
    public boolean HasSlopedRoofNorth() {
        return this.Has(IsoObjectType.WestRoofB) || this.Has(IsoObjectType.WestRoofM) || this.Has(IsoObjectType.WestRoofT);
    }
    
    public boolean HasTree() {
        return this.hasTree;
    }
    
    public IsoTree getTree() {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoTree isoTree = Type.tryCastTo(this.Objects.get(i), IsoTree.class);
            if (isoTree != null) {
                return isoTree;
            }
        }
        return null;
    }
    
    private void fudgeShadowsToAlpha(final IsoObject isoObject, final Color color) {
        final float b = 1.0f - isoObject.getAlpha();
        if (color.r < b) {
            color.r = b;
        }
        if (color.g < b) {
            color.g = b;
        }
        if (color.b < b) {
            color.b = b;
        }
    }
    
    public boolean shouldSave() {
        return !this.Objects.isEmpty();
    }
    
    public void save(final ByteBuffer byteBuffer, final ObjectOutputStream objectOutputStream) throws IOException {
        this.save(byteBuffer, objectOutputStream, false);
    }
    
    public void save(final ByteBuffer byteBuffer, final ObjectOutputStream objectOutputStream, final boolean b) throws IOException {
        this.getErosionData().save(byteBuffer);
        final BitHeaderWrite allocWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, byteBuffer);
        final int size = this.Objects.size();
        if (this.Objects.size() > 0) {
            allocWrite.addFlags(1);
            if (size == 2) {
                allocWrite.addFlags(2);
            }
            else if (size == 3) {
                allocWrite.addFlags(4);
            }
            else if (size >= 4) {
                allocWrite.addFlags(8);
            }
            if (b) {
                GameWindow.WriteString(byteBuffer, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, size));
            }
            if (size >= 4) {
                byteBuffer.putShort((short)this.Objects.size());
            }
            for (int i = 0; i < this.Objects.size(); ++i) {
                final int position = byteBuffer.position();
                if (b) {
                    byteBuffer.putInt(0);
                }
                byte b2 = 0;
                if (this.SpecialObjects.contains(this.Objects.get(i))) {
                    b2 |= 0x2;
                }
                if (this.WorldObjects.contains(this.Objects.get(i))) {
                    b2 |= 0x4;
                }
                byteBuffer.put(b2);
                if (b) {
                    GameWindow.WriteStringUTF(byteBuffer, this.Objects.get(i).getClass().getName());
                }
                this.Objects.get(i).save(byteBuffer, b);
                if (b) {
                    final int position2 = byteBuffer.position();
                    byteBuffer.position(position);
                    byteBuffer.putInt(position2 - position);
                    byteBuffer.position(position2);
                }
            }
            if (b) {
                byteBuffer.put((byte)67);
                byteBuffer.put((byte)82);
                byteBuffer.put((byte)80);
                byteBuffer.put((byte)83);
            }
        }
        if (this.isOverlayDone()) {
            allocWrite.addFlags(16);
        }
        if (this.haveRoof) {
            allocWrite.addFlags(32);
        }
        final BitHeaderWrite allocWrite2 = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, byteBuffer);
        int n = 0;
        for (int j = 0; j < this.StaticMovingObjects.size(); ++j) {
            if (this.StaticMovingObjects.get(j) instanceof IsoDeadBody) {
                ++n;
            }
        }
        if (n > 0) {
            allocWrite2.addFlags(1);
            if (b) {
                GameWindow.WriteString(byteBuffer, "Number of bodies");
            }
            byteBuffer.putShort((short)n);
            for (int k = 0; k < this.StaticMovingObjects.size(); ++k) {
                final IsoMovingObject isoMovingObject = this.StaticMovingObjects.get(k);
                if (isoMovingObject instanceof IsoDeadBody) {
                    if (b) {
                        GameWindow.WriteStringUTF(byteBuffer, isoMovingObject.getClass().getName());
                    }
                    isoMovingObject.save(byteBuffer, b);
                }
            }
        }
        if (this.table != null && !this.table.isEmpty()) {
            allocWrite2.addFlags(2);
            this.table.save(byteBuffer);
        }
        if (this.burntOut) {
            allocWrite2.addFlags(4);
        }
        if (this.getTrapPositionX() > 0) {
            allocWrite2.addFlags(8);
            byteBuffer.putInt(this.getTrapPositionX());
            byteBuffer.putInt(this.getTrapPositionY());
            byteBuffer.putInt(this.getTrapPositionZ());
        }
        if (this.haveSheetRope) {
            allocWrite2.addFlags(16);
        }
        if (!allocWrite2.equals(0)) {
            allocWrite.addFlags(64);
            allocWrite2.write();
        }
        else {
            byteBuffer.position(allocWrite2.getStartPosition());
        }
        allocWrite.write();
        allocWrite.release();
        allocWrite2.release();
    }
    
    static void loadmatrix(final boolean[][][] array, final DataInputStream dataInputStream) throws IOException {
    }
    
    static void savematrix(final boolean[][][] array, final DataOutputStream dataOutputStream) throws IOException {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                for (int k = 0; k < 3; ++k) {
                    dataOutputStream.writeBoolean(array[i][j][k]);
                }
            }
        }
    }
    
    public boolean isCommonGrass() {
        if (this.Objects.isEmpty()) {
            return false;
        }
        final IsoObject isoObject = this.Objects.get(0);
        return isoObject.sprite.getProperties().Is(IsoFlagType.solidfloor) && ("TileFloorExt_3".equals(isoObject.tile) || "TileFloorExt_4".equals(isoObject.tile));
    }
    
    public static boolean toBoolean(final byte[] array) {
        return array != null && array.length != 0 && array[0] != 0;
    }
    
    public void removeCorpse(final IsoDeadBody isoDeadBody, final boolean b) {
        if (GameClient.bClient && !b) {
            try {
                GameClient.instance.checkAddedRemovedItems(isoDeadBody);
            }
            catch (Exception ex) {
                GameClient.connection.cancelPacket();
                ExceptionLogger.logException(ex);
            }
            GameClient.sendRemoveCorpseFromMap(isoDeadBody);
        }
        isoDeadBody.removeFromWorld();
        isoDeadBody.removeFromSquare();
        if (!GameServer.bServer) {
            LuaEventManager.triggerEvent("OnContainerUpdate", this);
        }
    }
    
    public IsoDeadBody getDeadBody() {
        for (int i = 0; i < this.StaticMovingObjects.size(); ++i) {
            if (this.StaticMovingObjects.get(i) instanceof IsoDeadBody) {
                return (IsoDeadBody)this.StaticMovingObjects.get(i);
            }
        }
        return null;
    }
    
    public List<IsoDeadBody> getDeadBodys() {
        final ArrayList<IsoDeadBody> list = new ArrayList<IsoDeadBody>();
        for (int i = 0; i < this.StaticMovingObjects.size(); ++i) {
            if (this.StaticMovingObjects.get(i) instanceof IsoDeadBody) {
                list.add((IsoDeadBody)this.StaticMovingObjects.get(i));
            }
        }
        return list;
    }
    
    public void addCorpse(final IsoDeadBody isoDeadBody, final boolean b) {
        if (GameClient.bClient && !b) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.AddCorpseToMap.doPacket(startPacket);
            startPacket.putInt(this.x);
            startPacket.putInt(this.y);
            startPacket.putInt(this.z);
            isoDeadBody.writeToRemoteBuffer(startPacket);
            PacketTypes.PacketType.AddCorpseToMap.send(GameClient.connection);
        }
        if (!this.StaticMovingObjects.contains(isoDeadBody)) {
            this.StaticMovingObjects.add(isoDeadBody);
        }
        isoDeadBody.addToWorld();
        this.burntOut = false;
        this.Properties.UnSet(IsoFlagType.burntOut);
    }
    
    public IsoBrokenGlass getBrokenGlass() {
        for (int i = 0; i < this.SpecialObjects.size(); ++i) {
            final IsoObject isoObject = this.SpecialObjects.get(i);
            if (isoObject instanceof IsoBrokenGlass) {
                return (IsoBrokenGlass)isoObject;
            }
        }
        return null;
    }
    
    public IsoBrokenGlass addBrokenGlass() {
        if (!this.isFree(false)) {
            return this.getBrokenGlass();
        }
        IsoBrokenGlass brokenGlass = this.getBrokenGlass();
        if (brokenGlass == null) {
            brokenGlass = new IsoBrokenGlass(this.getCell());
            brokenGlass.setSquare(this);
            this.AddSpecialObject(brokenGlass);
            if (GameServer.bServer) {
                GameServer.transmitBrokenGlass(this);
            }
        }
        return brokenGlass;
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        this.load(byteBuffer, n, false);
    }
    
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        this.getErosionData().load(byteBuffer, n);
        final BitHeaderRead allocRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
        if (!allocRead.equals(0)) {
            if (allocRead.hasFlags(1)) {
                if (b) {
                    DebugLog.log(GameWindow.ReadStringUTF(byteBuffer));
                }
                int short1 = 1;
                if (allocRead.hasFlags(2)) {
                    short1 = 2;
                }
                else if (allocRead.hasFlags(4)) {
                    short1 = 3;
                }
                else if (allocRead.hasFlags(8)) {
                    short1 = byteBuffer.getShort();
                }
                for (int i = 0; i < short1; ++i) {
                    final int position = byteBuffer.position();
                    int int1 = 0;
                    if (b) {
                        int1 = byteBuffer.getInt();
                    }
                    final byte value = byteBuffer.get();
                    final boolean b2 = (value & 0x2) != 0x0;
                    final boolean b3 = (value & 0x4) != 0x0;
                    if (b) {
                        DebugLog.log(GameWindow.ReadStringUTF(byteBuffer));
                    }
                    final IsoObject factoryFromFileInput = IsoObject.factoryFromFileInput(this.getCell(), byteBuffer);
                    if (factoryFromFileInput == null) {
                        if (b) {
                            final int position2 = byteBuffer.position();
                            if (position2 - position != int1) {
                                DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, position2 - position, int1, short1));
                                if (factoryFromFileInput.getSprite() != null && factoryFromFileInput.getSprite().getName() != null) {
                                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, factoryFromFileInput.getSprite().getName()));
                                }
                            }
                        }
                    }
                    else {
                        factoryFromFileInput.square = this;
                        try {
                            factoryFromFileInput.load(byteBuffer, n, b);
                        }
                        catch (Exception cause) {
                            this.debugPrintGridSquare();
                            if (IsoGridSquare.lastLoaded != null) {
                                IsoGridSquare.lastLoaded.debugPrintGridSquare();
                            }
                            throw new RuntimeException(cause);
                        }
                        if (b) {
                            final int position3 = byteBuffer.position();
                            if (position3 - position != int1) {
                                DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, position3 - position, int1, short1));
                                if (factoryFromFileInput.getSprite() != null && factoryFromFileInput.getSprite().getName() != null) {
                                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, factoryFromFileInput.getSprite().getName()));
                                }
                            }
                        }
                        if (factoryFromFileInput instanceof IsoWorldInventoryObject) {
                            if (((IsoWorldInventoryObject)factoryFromFileInput).getItem() == null) {
                                continue;
                            }
                            final String fullType = ((IsoWorldInventoryObject)factoryFromFileInput).getItem().getFullType();
                            final Item findItem = ScriptManager.instance.FindItem(fullType);
                            if (findItem != null && findItem.getObsolete()) {
                                continue;
                            }
                            final String[] split = fullType.split("_");
                            if (((IsoWorldInventoryObject)factoryFromFileInput).dropTime > -1.0 && SandboxOptions.instance.HoursForWorldItemRemoval.getValue() > 0.0 && ((SandboxOptions.instance.WorldItemRemovalList.getValue().contains(split[0]) && !SandboxOptions.instance.ItemRemovalListBlacklistToggle.getValue()) || (!SandboxOptions.instance.WorldItemRemovalList.getValue().contains(split[0]) && SandboxOptions.instance.ItemRemovalListBlacklistToggle.getValue())) && !((IsoWorldInventoryObject)factoryFromFileInput).isIgnoreRemoveSandbox() && GameTime.instance.getWorldAgeHours() > ((IsoWorldInventoryObject)factoryFromFileInput).dropTime + SandboxOptions.instance.HoursForWorldItemRemoval.getValue()) {
                                continue;
                            }
                        }
                        if (factoryFromFileInput instanceof IsoWindow && factoryFromFileInput.getSprite() != null) {
                            if ("walls_special_01_8".equals(factoryFromFileInput.getSprite().getName())) {
                                continue;
                            }
                            if ("walls_special_01_9".equals(factoryFromFileInput.getSprite().getName())) {
                                continue;
                            }
                        }
                        this.Objects.add(factoryFromFileInput);
                        if (b2) {
                            this.SpecialObjects.add(factoryFromFileInput);
                        }
                        if (b3) {
                            if (Core.bDebug && !(factoryFromFileInput instanceof IsoWorldInventoryObject)) {
                                DebugLog.log(invokedynamic(makeConcatWithConstants:(BLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, value, factoryFromFileInput.getObjectName(), (factoryFromFileInput.getSprite() != null) ? factoryFromFileInput.getSprite().getName() : "unknown"));
                            }
                            this.WorldObjects.add((IsoWorldInventoryObject)factoryFromFileInput);
                            factoryFromFileInput.square.chunk.recalcHashCodeObjects();
                        }
                    }
                }
                if (b) {
                    final byte value2 = byteBuffer.get();
                    final byte value3 = byteBuffer.get();
                    final byte value4 = byteBuffer.get();
                    final byte value5 = byteBuffer.get();
                    if (value2 != 67 || value3 != 82 || value4 != 80 || value5 != 83) {
                        DebugLog.log("***** Expected CRPS here");
                    }
                }
            }
            this.setOverlayDone(allocRead.hasFlags(16));
            this.haveRoof = allocRead.hasFlags(32);
            if (allocRead.hasFlags(64)) {
                final BitHeaderRead allocRead2 = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
                if (allocRead2.hasFlags(1)) {
                    if (b) {
                        DebugLog.log(GameWindow.ReadStringUTF(byteBuffer));
                    }
                    for (short short2 = byteBuffer.getShort(), n2 = 0; n2 < short2; ++n2) {
                        if (b) {
                            DebugLog.log(GameWindow.ReadStringUTF(byteBuffer));
                        }
                        IsoMovingObject e;
                        try {
                            e = (IsoMovingObject)IsoObject.factoryFromFileInput(this.getCell(), byteBuffer);
                        }
                        catch (Exception cause2) {
                            this.debugPrintGridSquare();
                            if (IsoGridSquare.lastLoaded != null) {
                                IsoGridSquare.lastLoaded.debugPrintGridSquare();
                            }
                            throw new RuntimeException(cause2);
                        }
                        if (e != null) {
                            e.square = this;
                            e.current = this;
                            try {
                                e.load(byteBuffer, n, b);
                            }
                            catch (Exception cause3) {
                                this.debugPrintGridSquare();
                                if (IsoGridSquare.lastLoaded != null) {
                                    IsoGridSquare.lastLoaded.debugPrintGridSquare();
                                }
                                throw new RuntimeException(cause3);
                            }
                            this.StaticMovingObjects.add(e);
                            this.recalcHashCodeObjects();
                        }
                    }
                }
                if (allocRead2.hasFlags(2)) {
                    if (this.table == null) {
                        this.table = LuaManager.platform.newTable();
                    }
                    this.table.load(byteBuffer, n);
                }
                this.burntOut = allocRead2.hasFlags(4);
                if (allocRead2.hasFlags(8)) {
                    this.setTrapPositionX(byteBuffer.getInt());
                    this.setTrapPositionY(byteBuffer.getInt());
                    this.setTrapPositionZ(byteBuffer.getInt());
                }
                this.haveSheetRope = allocRead2.hasFlags(16);
                allocRead2.release();
            }
        }
        allocRead.release();
        IsoGridSquare.lastLoaded = this;
    }
    
    private void debugPrintGridSquare() {
        System.out.println(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, this.x, this.y, this.z));
        System.out.println("objects");
        for (int i = 0; i < this.Objects.size(); ++i) {
            this.Objects.get(i).debugPrintout();
        }
        System.out.println("staticmovingobjects");
        for (int j = 0; j < this.StaticMovingObjects.size(); ++j) {
            this.Objects.get(j).debugPrintout();
        }
    }
    
    public float scoreAsWaypoint(final int n, final int n2) {
        return 2.0f - IsoUtils.DistanceManhatten((float)n, (float)n2, (float)this.getX(), (float)this.getY()) * 5.0f;
    }
    
    public void InvalidateSpecialObjectPaths() {
    }
    
    public boolean isSolid() {
        return this.Properties.Is(IsoFlagType.solid);
    }
    
    public boolean isSolidTrans() {
        return this.Properties.Is(IsoFlagType.solidtrans);
    }
    
    public boolean isFree(final boolean b) {
        if (b && this.MovingObjects.size() > 0) {
            return false;
        }
        if (this.CachedIsFree) {
            return this.CacheIsFree;
        }
        this.CachedIsFree = true;
        this.CacheIsFree = true;
        if (this.Properties.Is(IsoFlagType.solid) || this.Properties.Is(IsoFlagType.solidtrans) || this.Has(IsoObjectType.tree)) {
            this.CacheIsFree = false;
        }
        if (!this.Properties.Is(IsoFlagType.solidfloor)) {
            this.CacheIsFree = false;
        }
        if (this.Has(IsoObjectType.stairsBN) || this.Has(IsoObjectType.stairsMN) || this.Has(IsoObjectType.stairsTN)) {
            this.CacheIsFree = true;
        }
        else if (this.Has(IsoObjectType.stairsBW) || this.Has(IsoObjectType.stairsMW) || this.Has(IsoObjectType.stairsTW)) {
            this.CacheIsFree = true;
        }
        return this.CacheIsFree;
    }
    
    public boolean isFreeOrMidair(final boolean b) {
        if (b && this.MovingObjects.size() > 0) {
            return false;
        }
        boolean b2 = true;
        if (this.Properties.Is(IsoFlagType.solid) || this.Properties.Is(IsoFlagType.solidtrans) || this.Has(IsoObjectType.tree)) {
            b2 = false;
        }
        if (this.Has(IsoObjectType.stairsBN) || this.Has(IsoObjectType.stairsMN) || this.Has(IsoObjectType.stairsTN)) {
            b2 = true;
        }
        else if (this.Has(IsoObjectType.stairsBW) || this.Has(IsoObjectType.stairsMW) || this.Has(IsoObjectType.stairsTW)) {
            b2 = true;
        }
        return b2;
    }
    
    public boolean isFreeOrMidair(final boolean b, final boolean b2) {
        if (b && this.MovingObjects.size() > 0) {
            if (!b2) {
                return false;
            }
            for (int i = 0; i < this.MovingObjects.size(); ++i) {
                if (!(this.MovingObjects.get(i) instanceof IsoDeadBody)) {
                    return false;
                }
            }
        }
        boolean b3 = true;
        if (this.Properties.Is(IsoFlagType.solid) || this.Properties.Is(IsoFlagType.solidtrans) || this.Has(IsoObjectType.tree)) {
            b3 = false;
        }
        if (this.Has(IsoObjectType.stairsBN) || this.Has(IsoObjectType.stairsMN) || this.Has(IsoObjectType.stairsTN)) {
            b3 = true;
        }
        else if (this.Has(IsoObjectType.stairsBW) || this.Has(IsoObjectType.stairsMW) || this.Has(IsoObjectType.stairsTW)) {
            b3 = true;
        }
        return b3;
    }
    
    public boolean connectedWithFloor() {
        if (this.getZ() == 0) {
            return true;
        }
        final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.getX() - 1, this.getY(), this.getZ());
        if (gridSquare != null && gridSquare.Properties.Is(IsoFlagType.solidfloor)) {
            return true;
        }
        final IsoGridSquare gridSquare2 = this.getCell().getGridSquare(this.getX() + 1, this.getY(), this.getZ());
        if (gridSquare2 != null && gridSquare2.Properties.Is(IsoFlagType.solidfloor)) {
            return true;
        }
        final IsoGridSquare gridSquare3 = this.getCell().getGridSquare(this.getX(), this.getY() - 1, this.getZ());
        if (gridSquare3 != null && gridSquare3.Properties.Is(IsoFlagType.solidfloor)) {
            return true;
        }
        final IsoGridSquare gridSquare4 = this.getCell().getGridSquare(this.getX(), this.getY() + 1, this.getZ());
        return gridSquare4 != null && gridSquare4.Properties.Is(IsoFlagType.solidfloor);
    }
    
    public boolean hasFloor(final boolean b) {
        if (this.Properties.Is(IsoFlagType.solidfloor)) {
            return true;
        }
        IsoGridSquare isoGridSquare;
        if (b) {
            isoGridSquare = this.getCell().getGridSquare(this.getX(), this.getY() - 1, this.getZ());
        }
        else {
            isoGridSquare = this.getCell().getGridSquare(this.getX() - 1, this.getY(), this.getZ());
        }
        return isoGridSquare != null && isoGridSquare.Properties.Is(IsoFlagType.solidfloor);
    }
    
    public boolean isNotBlocked(final boolean b) {
        if (!this.CachedIsFree) {
            this.CacheIsFree = true;
            this.CachedIsFree = true;
            if (this.Properties.Is(IsoFlagType.solid) || this.Properties.Is(IsoFlagType.solidtrans)) {
                this.CacheIsFree = false;
            }
            if (!this.Properties.Is(IsoFlagType.solidfloor)) {
                this.CacheIsFree = false;
            }
        }
        else if (!this.CacheIsFree) {
            return false;
        }
        return !b || this.MovingObjects.size() <= 0;
    }
    
    public IsoObject getDoor(final boolean b) {
        for (int i = 0; i < this.SpecialObjects.size(); ++i) {
            final IsoObject isoObject = this.SpecialObjects.get(i);
            if (isoObject instanceof IsoThumpable) {
                final IsoThumpable isoThumpable = (IsoThumpable)isoObject;
                if (isoThumpable.isDoor() && b == isoThumpable.north) {
                    return isoThumpable;
                }
            }
            if (isoObject instanceof IsoDoor) {
                final IsoDoor isoDoor = (IsoDoor)isoObject;
                if (b == isoDoor.north) {
                    return isoDoor;
                }
            }
        }
        return null;
    }
    
    public IsoDoor getIsoDoor() {
        for (int i = 0; i < this.SpecialObjects.size(); ++i) {
            final IsoObject isoObject = this.SpecialObjects.get(i);
            if (isoObject instanceof IsoDoor) {
                return (IsoDoor)isoObject;
            }
        }
        return null;
    }
    
    public IsoObject getDoorTo(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare == this) {
            return null;
        }
        if (isoGridSquare.x < this.x) {
            final IsoObject door = this.getDoor(false);
            if (door != null) {
                return door;
            }
        }
        if (isoGridSquare.y < this.y) {
            final IsoObject door2 = this.getDoor(true);
            if (door2 != null) {
                return door2;
            }
        }
        if (isoGridSquare.x > this.x) {
            final IsoObject door3 = isoGridSquare.getDoor(false);
            if (door3 != null) {
                return door3;
            }
        }
        if (isoGridSquare.y > this.y) {
            final IsoObject door4 = isoGridSquare.getDoor(true);
            if (door4 != null) {
                return door4;
            }
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y) {
            final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x, isoGridSquare.y, this.z);
            final IsoGridSquare gridSquare2 = this.getCell().getGridSquare(isoGridSquare.x, this.y, this.z);
            final IsoObject doorTo = this.getDoorTo(gridSquare);
            if (doorTo != null) {
                return doorTo;
            }
            final IsoObject doorTo2 = this.getDoorTo(gridSquare2);
            if (doorTo2 != null) {
                return doorTo2;
            }
            final IsoObject doorTo3 = isoGridSquare.getDoorTo(gridSquare);
            if (doorTo3 != null) {
                return doorTo3;
            }
            final IsoObject doorTo4 = isoGridSquare.getDoorTo(gridSquare2);
            if (doorTo4 != null) {
                return doorTo4;
            }
        }
        return null;
    }
    
    public IsoWindow getWindow(final boolean b) {
        for (int i = 0; i < this.SpecialObjects.size(); ++i) {
            final IsoObject isoObject = this.SpecialObjects.get(i);
            if (isoObject instanceof IsoWindow) {
                final IsoWindow isoWindow = (IsoWindow)isoObject;
                if (b == isoWindow.north) {
                    return isoWindow;
                }
            }
        }
        return null;
    }
    
    public IsoWindow getWindow() {
        for (int i = 0; i < this.SpecialObjects.size(); ++i) {
            final IsoObject isoObject = this.SpecialObjects.get(i);
            if (isoObject instanceof IsoWindow) {
                return (IsoWindow)isoObject;
            }
        }
        return null;
    }
    
    public IsoWindow getWindowTo(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare == this) {
            return null;
        }
        if (isoGridSquare.x < this.x) {
            final IsoWindow window = this.getWindow(false);
            if (window != null) {
                return window;
            }
        }
        if (isoGridSquare.y < this.y) {
            final IsoWindow window2 = this.getWindow(true);
            if (window2 != null) {
                return window2;
            }
        }
        if (isoGridSquare.x > this.x) {
            final IsoWindow window3 = isoGridSquare.getWindow(false);
            if (window3 != null) {
                return window3;
            }
        }
        if (isoGridSquare.y > this.y) {
            final IsoWindow window4 = isoGridSquare.getWindow(true);
            if (window4 != null) {
                return window4;
            }
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y) {
            final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x, isoGridSquare.y, this.z);
            final IsoGridSquare gridSquare2 = this.getCell().getGridSquare(isoGridSquare.x, this.y, this.z);
            final IsoWindow windowTo = this.getWindowTo(gridSquare);
            if (windowTo != null) {
                return windowTo;
            }
            final IsoWindow windowTo2 = this.getWindowTo(gridSquare2);
            if (windowTo2 != null) {
                return windowTo2;
            }
            final IsoWindow windowTo3 = isoGridSquare.getWindowTo(gridSquare);
            if (windowTo3 != null) {
                return windowTo3;
            }
            final IsoWindow windowTo4 = isoGridSquare.getWindowTo(gridSquare2);
            if (windowTo4 != null) {
                return windowTo4;
            }
        }
        return null;
    }
    
    public boolean isAdjacentToWindow() {
        if (this.getWindow() != null) {
            return true;
        }
        if (this.hasWindowFrame()) {
            return true;
        }
        if (this.getThumpableWindow(false) != null || this.getThumpableWindow(true) != null) {
            return true;
        }
        final IsoGridSquare isoGridSquare = this.nav[IsoDirections.S.index()];
        if (isoGridSquare != null && (isoGridSquare.getWindow(true) != null || isoGridSquare.getWindowFrame(true) != null || isoGridSquare.getThumpableWindow(true) != null)) {
            return true;
        }
        final IsoGridSquare isoGridSquare2 = this.nav[IsoDirections.E.index()];
        return isoGridSquare2 != null && (isoGridSquare2.getWindow(false) != null || isoGridSquare2.getWindowFrame(false) != null || isoGridSquare2.getThumpableWindow(false) != null);
    }
    
    public IsoThumpable getThumpableWindow(final boolean b) {
        for (int i = 0; i < this.SpecialObjects.size(); ++i) {
            final IsoObject isoObject = this.SpecialObjects.get(i);
            if (isoObject instanceof IsoThumpable) {
                final IsoThumpable isoThumpable = (IsoThumpable)isoObject;
                if (isoThumpable.isWindow() && b == isoThumpable.north) {
                    return isoThumpable;
                }
            }
        }
        return null;
    }
    
    public IsoThumpable getWindowThumpableTo(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare == this) {
            return null;
        }
        if (isoGridSquare.x < this.x) {
            final IsoThumpable thumpableWindow = this.getThumpableWindow(false);
            if (thumpableWindow != null) {
                return thumpableWindow;
            }
        }
        if (isoGridSquare.y < this.y) {
            final IsoThumpable thumpableWindow2 = this.getThumpableWindow(true);
            if (thumpableWindow2 != null) {
                return thumpableWindow2;
            }
        }
        if (isoGridSquare.x > this.x) {
            final IsoThumpable thumpableWindow3 = isoGridSquare.getThumpableWindow(false);
            if (thumpableWindow3 != null) {
                return thumpableWindow3;
            }
        }
        if (isoGridSquare.y > this.y) {
            final IsoThumpable thumpableWindow4 = isoGridSquare.getThumpableWindow(true);
            if (thumpableWindow4 != null) {
                return thumpableWindow4;
            }
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y) {
            final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x, isoGridSquare.y, this.z);
            final IsoGridSquare gridSquare2 = this.getCell().getGridSquare(isoGridSquare.x, this.y, this.z);
            final IsoThumpable windowThumpableTo = this.getWindowThumpableTo(gridSquare);
            if (windowThumpableTo != null) {
                return windowThumpableTo;
            }
            final IsoThumpable windowThumpableTo2 = this.getWindowThumpableTo(gridSquare2);
            if (windowThumpableTo2 != null) {
                return windowThumpableTo2;
            }
            final IsoThumpable windowThumpableTo3 = isoGridSquare.getWindowThumpableTo(gridSquare);
            if (windowThumpableTo3 != null) {
                return windowThumpableTo3;
            }
            final IsoThumpable windowThumpableTo4 = isoGridSquare.getWindowThumpableTo(gridSquare2);
            if (windowThumpableTo4 != null) {
                return windowThumpableTo4;
            }
        }
        return null;
    }
    
    public IsoThumpable getHoppableThumpable(final boolean b) {
        for (int i = 0; i < this.SpecialObjects.size(); ++i) {
            final IsoObject isoObject = this.SpecialObjects.get(i);
            if (isoObject instanceof IsoThumpable) {
                final IsoThumpable isoThumpable = (IsoThumpable)isoObject;
                if (isoThumpable.isHoppable() && b == isoThumpable.north) {
                    return isoThumpable;
                }
            }
        }
        return null;
    }
    
    public IsoThumpable getHoppableThumpableTo(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare == this) {
            return null;
        }
        if (isoGridSquare.x < this.x) {
            final IsoThumpable hoppableThumpable = this.getHoppableThumpable(false);
            if (hoppableThumpable != null) {
                return hoppableThumpable;
            }
        }
        if (isoGridSquare.y < this.y) {
            final IsoThumpable hoppableThumpable2 = this.getHoppableThumpable(true);
            if (hoppableThumpable2 != null) {
                return hoppableThumpable2;
            }
        }
        if (isoGridSquare.x > this.x) {
            final IsoThumpable hoppableThumpable3 = isoGridSquare.getHoppableThumpable(false);
            if (hoppableThumpable3 != null) {
                return hoppableThumpable3;
            }
        }
        if (isoGridSquare.y > this.y) {
            final IsoThumpable hoppableThumpable4 = isoGridSquare.getHoppableThumpable(true);
            if (hoppableThumpable4 != null) {
                return hoppableThumpable4;
            }
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y) {
            final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x, isoGridSquare.y, this.z);
            final IsoGridSquare gridSquare2 = this.getCell().getGridSquare(isoGridSquare.x, this.y, this.z);
            final IsoThumpable hoppableThumpableTo = this.getHoppableThumpableTo(gridSquare);
            if (hoppableThumpableTo != null) {
                return hoppableThumpableTo;
            }
            final IsoThumpable hoppableThumpableTo2 = this.getHoppableThumpableTo(gridSquare2);
            if (hoppableThumpableTo2 != null) {
                return hoppableThumpableTo2;
            }
            final IsoThumpable hoppableThumpableTo3 = isoGridSquare.getHoppableThumpableTo(gridSquare);
            if (hoppableThumpableTo3 != null) {
                return hoppableThumpableTo3;
            }
            final IsoThumpable hoppableThumpableTo4 = isoGridSquare.getHoppableThumpableTo(gridSquare2);
            if (hoppableThumpableTo4 != null) {
                return hoppableThumpableTo4;
            }
        }
        return null;
    }
    
    public IsoObject getWallHoppable(final boolean b) {
        for (int i = 0; i < this.Objects.size(); ++i) {
            if (this.Objects.get(i).isHoppable() && b == this.Objects.get(i).isNorthHoppable()) {
                return this.Objects.get(i);
            }
        }
        return null;
    }
    
    public IsoObject getWallHoppableTo(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare == this) {
            return null;
        }
        if (isoGridSquare.x < this.x) {
            final IsoObject wallHoppable = this.getWallHoppable(false);
            if (wallHoppable != null) {
                return wallHoppable;
            }
        }
        if (isoGridSquare.y < this.y) {
            final IsoObject wallHoppable2 = this.getWallHoppable(true);
            if (wallHoppable2 != null) {
                return wallHoppable2;
            }
        }
        if (isoGridSquare.x > this.x) {
            final IsoObject wallHoppable3 = isoGridSquare.getWallHoppable(false);
            if (wallHoppable3 != null) {
                return wallHoppable3;
            }
        }
        if (isoGridSquare.y > this.y) {
            final IsoObject wallHoppable4 = isoGridSquare.getWallHoppable(true);
            if (wallHoppable4 != null) {
                return wallHoppable4;
            }
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y) {
            final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x, isoGridSquare.y, this.z);
            final IsoGridSquare gridSquare2 = this.getCell().getGridSquare(isoGridSquare.x, this.y, this.z);
            final IsoObject wallHoppableTo = this.getWallHoppableTo(gridSquare);
            if (wallHoppableTo != null) {
                return wallHoppableTo;
            }
            final IsoObject wallHoppableTo2 = this.getWallHoppableTo(gridSquare2);
            if (wallHoppableTo2 != null) {
                return wallHoppableTo2;
            }
            final IsoObject wallHoppableTo3 = isoGridSquare.getWallHoppableTo(gridSquare);
            if (wallHoppableTo3 != null) {
                return wallHoppableTo3;
            }
            final IsoObject wallHoppableTo4 = isoGridSquare.getWallHoppableTo(gridSquare2);
            if (wallHoppableTo4 != null) {
                return wallHoppableTo4;
            }
        }
        return null;
    }
    
    public IsoObject getBedTo(final IsoGridSquare isoGridSquare) {
        ArrayList<IsoObject> list;
        if (isoGridSquare.y < this.y || isoGridSquare.x < this.x) {
            list = this.SpecialObjects;
        }
        else {
            list = isoGridSquare.SpecialObjects;
        }
        for (int i = 0; i < list.size(); ++i) {
            final IsoObject isoObject = list.get(i);
            if (isoObject.getProperties().Is(IsoFlagType.bed)) {
                return isoObject;
            }
        }
        return null;
    }
    
    public IsoObject getWindowFrame(final boolean b) {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (!(isoObject instanceof IsoWorldInventoryObject)) {
                if (IsoWindowFrame.isWindowFrame(isoObject, b)) {
                    return isoObject;
                }
            }
        }
        return null;
    }
    
    public IsoObject getWindowFrameTo(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare == this) {
            return null;
        }
        if (isoGridSquare.x < this.x) {
            final IsoObject windowFrame = this.getWindowFrame(false);
            if (windowFrame != null) {
                return windowFrame;
            }
        }
        if (isoGridSquare.y < this.y) {
            final IsoObject windowFrame2 = this.getWindowFrame(true);
            if (windowFrame2 != null) {
                return windowFrame2;
            }
        }
        if (isoGridSquare.x > this.x) {
            final IsoObject windowFrame3 = isoGridSquare.getWindowFrame(false);
            if (windowFrame3 != null) {
                return windowFrame3;
            }
        }
        if (isoGridSquare.y > this.y) {
            final IsoObject windowFrame4 = isoGridSquare.getWindowFrame(true);
            if (windowFrame4 != null) {
                return windowFrame4;
            }
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y) {
            final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x, isoGridSquare.y, this.z);
            final IsoGridSquare gridSquare2 = this.getCell().getGridSquare(isoGridSquare.x, this.y, this.z);
            final IsoObject windowFrameTo = this.getWindowFrameTo(gridSquare);
            if (windowFrameTo != null) {
                return windowFrameTo;
            }
            final IsoObject windowFrameTo2 = this.getWindowFrameTo(gridSquare2);
            if (windowFrameTo2 != null) {
                return windowFrameTo2;
            }
            final IsoObject windowFrameTo3 = isoGridSquare.getWindowFrameTo(gridSquare);
            if (windowFrameTo3 != null) {
                return windowFrameTo3;
            }
            final IsoObject windowFrameTo4 = isoGridSquare.getWindowFrameTo(gridSquare2);
            if (windowFrameTo4 != null) {
                return windowFrameTo4;
            }
        }
        return null;
    }
    
    public boolean hasWindowFrame() {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (!(isoObject instanceof IsoWorldInventoryObject)) {
                if (IsoWindowFrame.isWindowFrame(isoObject)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean hasWindowOrWindowFrame() {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (!(isoObject instanceof IsoWorldInventoryObject)) {
                if (this.isWindowOrWindowFrame(isoObject, true) || this.isWindowOrWindowFrame(isoObject, false)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private IsoObject getSpecialWall(final boolean b) {
        for (int i = this.SpecialObjects.size() - 1; i >= 0; --i) {
            final IsoObject isoObject = this.SpecialObjects.get(i);
            if (isoObject instanceof IsoThumpable) {
                final IsoThumpable isoThumpable = (IsoThumpable)isoObject;
                if (isoThumpable.isStairs()) {
                    continue;
                }
                if (!isoThumpable.isThumpable() && !isoThumpable.isWindow() && !isoThumpable.isDoor()) {
                    continue;
                }
                if (isoThumpable.isDoor() && isoThumpable.open) {
                    continue;
                }
                if (isoThumpable.isBlockAllTheSquare()) {
                    continue;
                }
                if (b == isoThumpable.north && !isoThumpable.isCorner()) {
                    return isoThumpable;
                }
            }
            if (isoObject instanceof IsoWindow) {
                final IsoWindow isoWindow = (IsoWindow)isoObject;
                if (b == isoWindow.north) {
                    return isoWindow;
                }
            }
            if (isoObject instanceof IsoDoor) {
                final IsoDoor isoDoor = (IsoDoor)isoObject;
                if (b == isoDoor.north && !isoDoor.open) {
                    return isoDoor;
                }
            }
        }
        if ((b && !this.Is(IsoFlagType.WindowN)) || (!b && !this.Is(IsoFlagType.WindowW))) {
            return null;
        }
        final IsoObject windowFrame = this.getWindowFrame(b);
        if (windowFrame != null) {
            return windowFrame;
        }
        return null;
    }
    
    public IsoObject getSheetRope() {
        for (int i = 0; i < this.getObjects().size(); ++i) {
            final IsoObject isoObject = this.getObjects().get(i);
            if (isoObject.sheetRope) {
                return isoObject;
            }
        }
        return null;
    }
    
    public boolean damageSpriteSheetRopeFromBottom(final IsoPlayer isoPlayer, final boolean b) {
        IsoGridSquare gridSquare = this;
        IsoFlagType isoFlagType;
        if (b) {
            if (this.Is(IsoFlagType.climbSheetN)) {
                isoFlagType = IsoFlagType.climbSheetN;
            }
            else {
                if (!this.Is(IsoFlagType.climbSheetS)) {
                    return false;
                }
                isoFlagType = IsoFlagType.climbSheetS;
            }
        }
        else if (this.Is(IsoFlagType.climbSheetW)) {
            isoFlagType = IsoFlagType.climbSheetW;
        }
        else {
            if (!this.Is(IsoFlagType.climbSheetE)) {
                return false;
            }
            isoFlagType = IsoFlagType.climbSheetE;
        }
        while (gridSquare != null) {
            int i = 0;
            while (i < gridSquare.getObjects().size()) {
                final IsoObject isoObject = gridSquare.getObjects().get(i);
                if (isoObject.getProperties() != null && isoObject.getProperties().Is(isoFlagType)) {
                    int int1 = Integer.parseInt(isoObject.getSprite().getName().split("_")[2]);
                    if (int1 > 14) {
                        return false;
                    }
                    final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, isoObject.getSprite().getName().split("_")[0], isoObject.getSprite().getName().split("_")[1]);
                    int1 += 40;
                    isoObject.setSprite(IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, int1)));
                    isoObject.transmitUpdatedSprite();
                    break;
                }
                else {
                    ++i;
                }
            }
            if (gridSquare.getZ() == 7) {
                break;
            }
            gridSquare = gridSquare.getCell().getGridSquare(gridSquare.getX(), gridSquare.getY(), gridSquare.getZ() + 1);
        }
        return true;
    }
    
    public boolean removeSheetRopeFromBottom(final IsoPlayer isoPlayer, final boolean b) {
        IsoGridSquare gridSquare = this;
        IsoFlagType isoFlagType;
        IsoFlagType isoFlagType2;
        if (b) {
            if (this.Is(IsoFlagType.climbSheetN)) {
                isoFlagType = IsoFlagType.climbSheetTopN;
                isoFlagType2 = IsoFlagType.climbSheetN;
            }
            else {
                if (!this.Is(IsoFlagType.climbSheetS)) {
                    return false;
                }
                isoFlagType = IsoFlagType.climbSheetTopS;
                isoFlagType2 = IsoFlagType.climbSheetS;
                final String anObject = "crafted_01_4";
                for (int i = 0; i < gridSquare.getObjects().size(); ++i) {
                    final IsoObject isoObject = gridSquare.getObjects().get(i);
                    if (isoObject.sprite != null && isoObject.sprite.getName() != null && isoObject.sprite.getName().equals(anObject)) {
                        gridSquare.transmitRemoveItemFromSquare(isoObject);
                        break;
                    }
                }
            }
        }
        else if (this.Is(IsoFlagType.climbSheetW)) {
            isoFlagType = IsoFlagType.climbSheetTopW;
            isoFlagType2 = IsoFlagType.climbSheetW;
        }
        else {
            if (!this.Is(IsoFlagType.climbSheetE)) {
                return false;
            }
            isoFlagType = IsoFlagType.climbSheetTopE;
            isoFlagType2 = IsoFlagType.climbSheetE;
            final String anObject2 = "crafted_01_3";
            for (int j = 0; j < gridSquare.getObjects().size(); ++j) {
                final IsoObject isoObject2 = gridSquare.getObjects().get(j);
                if (isoObject2.sprite != null && isoObject2.sprite.getName() != null && isoObject2.sprite.getName().equals(anObject2)) {
                    gridSquare.transmitRemoveItemFromSquare(isoObject2);
                    break;
                }
            }
        }
        boolean b2 = false;
        IsoGridSquare isoGridSquare = null;
        while (gridSquare != null) {
            int k = 0;
            while (k < gridSquare.getObjects().size()) {
                final IsoObject isoObject3 = gridSquare.getObjects().get(k);
                if (isoObject3.getProperties() != null && (isoObject3.getProperties().Is(isoFlagType) || isoObject3.getProperties().Is(isoFlagType2))) {
                    isoGridSquare = gridSquare;
                    b2 = true;
                    gridSquare.transmitRemoveItemFromSquare(isoObject3);
                    if (GameServer.bServer) {
                        if (isoPlayer != null) {
                            isoPlayer.sendObjectChange("addItemOfType", new Object[] { "type", isoObject3.getName() });
                            break;
                        }
                        break;
                    }
                    else {
                        if (isoPlayer != null) {
                            isoPlayer.getInventory().AddItem(isoObject3.getName());
                            break;
                        }
                        break;
                    }
                }
                else {
                    ++k;
                }
            }
            if (gridSquare.getZ() == 7) {
                break;
            }
            gridSquare = gridSquare.getCell().getGridSquare(gridSquare.getX(), gridSquare.getY(), gridSquare.getZ() + 1);
            b2 = false;
        }
        if (!b2) {
            final IsoGridSquare gridSquare2 = isoGridSquare.getCell().getGridSquare(isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ());
            final IsoGridSquare isoGridSquare2 = b ? gridSquare2.nav[IsoDirections.S.index()] : gridSquare2.nav[IsoDirections.E.index()];
            if (isoGridSquare2 == null) {
                return true;
            }
            for (int l = 0; l < isoGridSquare2.getObjects().size(); ++l) {
                final IsoObject isoObject4 = isoGridSquare2.getObjects().get(l);
                if (isoObject4.getProperties() != null && (isoObject4.getProperties().Is(isoFlagType) || isoObject4.getProperties().Is(isoFlagType2))) {
                    isoGridSquare2.transmitRemoveItemFromSquare(isoObject4);
                    break;
                }
            }
        }
        return true;
    }
    
    private IsoObject getSpecialSolid() {
        for (int i = 0; i < this.SpecialObjects.size(); ++i) {
            final IsoObject isoObject = this.SpecialObjects.get(i);
            if (isoObject instanceof IsoThumpable) {
                final IsoThumpable isoThumpable = (IsoThumpable)isoObject;
                if (!isoThumpable.isStairs()) {
                    if (isoThumpable.isThumpable()) {
                        if (isoThumpable.isBlockAllTheSquare()) {
                            if (isoThumpable.getProperties().Is(IsoFlagType.solidtrans) && this.isAdjacentToWindow()) {
                                return null;
                            }
                            return isoThumpable;
                        }
                    }
                }
            }
        }
        int j = 0;
        while (j < this.Objects.size()) {
            final IsoObject isoObject2 = this.Objects.get(j);
            if (isoObject2.isMovedThumpable()) {
                if (this.isAdjacentToWindow()) {
                    return null;
                }
                return isoObject2;
            }
            else {
                ++j;
            }
        }
        return null;
    }
    
    public IsoObject testCollideSpecialObjects(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null || isoGridSquare == this) {
            return null;
        }
        if (isoGridSquare.x < this.x && isoGridSquare.y == this.y) {
            if (isoGridSquare.z == this.z && this.Has(IsoObjectType.stairsTW)) {
                return null;
            }
            final IsoObject specialWall = this.getSpecialWall(false);
            if (specialWall != null) {
                return specialWall;
            }
            if (this.isBlockedTo(isoGridSquare)) {
                return null;
            }
            final IsoObject specialSolid = isoGridSquare.getSpecialSolid();
            if (specialSolid != null) {
                return specialSolid;
            }
            return null;
        }
        else if (isoGridSquare.x == this.x && isoGridSquare.y < this.y) {
            if (isoGridSquare.z == this.z && this.Has(IsoObjectType.stairsTN)) {
                return null;
            }
            final IsoObject specialWall2 = this.getSpecialWall(true);
            if (specialWall2 != null) {
                return specialWall2;
            }
            if (this.isBlockedTo(isoGridSquare)) {
                return null;
            }
            final IsoObject specialSolid2 = isoGridSquare.getSpecialSolid();
            if (specialSolid2 != null) {
                return specialSolid2;
            }
            return null;
        }
        else if (isoGridSquare.x > this.x && isoGridSquare.y == this.y) {
            final IsoObject specialWall3 = isoGridSquare.getSpecialWall(false);
            if (specialWall3 != null) {
                return specialWall3;
            }
            if (this.isBlockedTo(isoGridSquare)) {
                return null;
            }
            final IsoObject specialSolid3 = isoGridSquare.getSpecialSolid();
            if (specialSolid3 != null) {
                return specialSolid3;
            }
            return null;
        }
        else if (isoGridSquare.x == this.x && isoGridSquare.y > this.y) {
            final IsoObject specialWall4 = isoGridSquare.getSpecialWall(true);
            if (specialWall4 != null) {
                return specialWall4;
            }
            if (this.isBlockedTo(isoGridSquare)) {
                return null;
            }
            final IsoObject specialSolid4 = isoGridSquare.getSpecialSolid();
            if (specialSolid4 != null) {
                return specialSolid4;
            }
            return null;
        }
        else if (isoGridSquare.x < this.x && isoGridSquare.y < this.y) {
            final IsoObject specialWall5 = this.getSpecialWall(true);
            if (specialWall5 != null) {
                return specialWall5;
            }
            final IsoObject specialWall6 = this.getSpecialWall(false);
            if (specialWall6 != null) {
                return specialWall6;
            }
            final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x, this.y - 1, this.z);
            if (gridSquare != null && !this.isBlockedTo(gridSquare)) {
                final IsoObject specialSolid5 = gridSquare.getSpecialSolid();
                if (specialSolid5 != null) {
                    return specialSolid5;
                }
                final IsoObject specialWall7 = gridSquare.getSpecialWall(false);
                if (specialWall7 != null) {
                    return specialWall7;
                }
            }
            final IsoGridSquare gridSquare2 = this.getCell().getGridSquare(this.x - 1, this.y, this.z);
            if (gridSquare2 != null && !this.isBlockedTo(gridSquare2)) {
                final IsoObject specialSolid6 = gridSquare2.getSpecialSolid();
                if (specialSolid6 != null) {
                    return specialSolid6;
                }
                final IsoObject specialWall8 = gridSquare2.getSpecialWall(true);
                if (specialWall8 != null) {
                    return specialWall8;
                }
            }
            if (gridSquare == null || this.isBlockedTo(gridSquare) || gridSquare2 == null || this.isBlockedTo(gridSquare2)) {
                return null;
            }
            if (gridSquare.isBlockedTo(isoGridSquare) || gridSquare2.isBlockedTo(isoGridSquare)) {
                return null;
            }
            final IsoObject specialSolid7 = isoGridSquare.getSpecialSolid();
            if (specialSolid7 != null) {
                return specialSolid7;
            }
            return null;
        }
        else if (isoGridSquare.x > this.x && isoGridSquare.y < this.y) {
            final IsoObject specialWall9 = this.getSpecialWall(true);
            if (specialWall9 != null) {
                return specialWall9;
            }
            final IsoGridSquare gridSquare3 = this.getCell().getGridSquare(this.x, this.y - 1, this.z);
            if (gridSquare3 != null && !this.isBlockedTo(gridSquare3)) {
                final IsoObject specialSolid8 = gridSquare3.getSpecialSolid();
                if (specialSolid8 != null) {
                    return specialSolid8;
                }
            }
            final IsoGridSquare gridSquare4 = this.getCell().getGridSquare(this.x + 1, this.y, this.z);
            if (gridSquare4 != null) {
                final IsoObject specialWall10 = gridSquare4.getSpecialWall(false);
                if (specialWall10 != null) {
                    return specialWall10;
                }
                if (!this.isBlockedTo(gridSquare4)) {
                    final IsoObject specialSolid9 = gridSquare4.getSpecialSolid();
                    if (specialSolid9 != null) {
                        return specialSolid9;
                    }
                    final IsoObject specialWall11 = gridSquare4.getSpecialWall(true);
                    if (specialWall11 != null) {
                        return specialWall11;
                    }
                }
            }
            if (gridSquare3 == null || this.isBlockedTo(gridSquare3) || gridSquare4 == null || this.isBlockedTo(gridSquare4)) {
                return null;
            }
            final IsoObject specialWall12 = isoGridSquare.getSpecialWall(false);
            if (specialWall12 != null) {
                return specialWall12;
            }
            if (gridSquare3.isBlockedTo(isoGridSquare) || gridSquare4.isBlockedTo(isoGridSquare)) {
                return null;
            }
            final IsoObject specialSolid10 = isoGridSquare.getSpecialSolid();
            if (specialSolid10 != null) {
                return specialSolid10;
            }
            return null;
        }
        else if (isoGridSquare.x > this.x && isoGridSquare.y > this.y) {
            final IsoGridSquare gridSquare5 = this.getCell().getGridSquare(this.x, this.y + 1, this.z);
            if (gridSquare5 != null) {
                final IsoObject specialWall13 = gridSquare5.getSpecialWall(true);
                if (specialWall13 != null) {
                    return specialWall13;
                }
                if (!this.isBlockedTo(gridSquare5)) {
                    final IsoObject specialSolid11 = gridSquare5.getSpecialSolid();
                    if (specialSolid11 != null) {
                        return specialSolid11;
                    }
                }
            }
            final IsoGridSquare gridSquare6 = this.getCell().getGridSquare(this.x + 1, this.y, this.z);
            if (gridSquare6 != null) {
                final IsoObject specialWall14 = gridSquare6.getSpecialWall(false);
                if (specialWall14 != null) {
                    return specialWall14;
                }
                if (!this.isBlockedTo(gridSquare6)) {
                    final IsoObject specialSolid12 = gridSquare6.getSpecialSolid();
                    if (specialSolid12 != null) {
                        return specialSolid12;
                    }
                }
            }
            if (gridSquare5 == null || this.isBlockedTo(gridSquare5) || gridSquare6 == null || this.isBlockedTo(gridSquare6)) {
                return null;
            }
            final IsoObject specialWall15 = isoGridSquare.getSpecialWall(false);
            if (specialWall15 != null) {
                return specialWall15;
            }
            final IsoObject specialWall16 = isoGridSquare.getSpecialWall(true);
            if (specialWall16 != null) {
                return specialWall16;
            }
            if (gridSquare5.isBlockedTo(isoGridSquare) || gridSquare6.isBlockedTo(isoGridSquare)) {
                return null;
            }
            final IsoObject specialSolid13 = isoGridSquare.getSpecialSolid();
            if (specialSolid13 != null) {
                return specialSolid13;
            }
            return null;
        }
        else {
            if (isoGridSquare.x >= this.x || isoGridSquare.y <= this.y) {
                return null;
            }
            final IsoObject specialWall17 = this.getSpecialWall(false);
            if (specialWall17 != null) {
                return specialWall17;
            }
            final IsoGridSquare gridSquare7 = this.getCell().getGridSquare(this.x, this.y + 1, this.z);
            if (gridSquare7 != null) {
                final IsoObject specialWall18 = gridSquare7.getSpecialWall(true);
                if (specialWall18 != null) {
                    return specialWall18;
                }
                if (!this.isBlockedTo(gridSquare7)) {
                    final IsoObject specialSolid14 = gridSquare7.getSpecialSolid();
                    if (specialSolid14 != null) {
                        return specialSolid14;
                    }
                }
            }
            final IsoGridSquare gridSquare8 = this.getCell().getGridSquare(this.x - 1, this.y, this.z);
            if (gridSquare8 != null && !this.isBlockedTo(gridSquare8)) {
                final IsoObject specialSolid15 = gridSquare8.getSpecialSolid();
                if (specialSolid15 != null) {
                    return specialSolid15;
                }
            }
            if (gridSquare7 == null || this.isBlockedTo(gridSquare7) || gridSquare8 == null || this.isBlockedTo(gridSquare8)) {
                return null;
            }
            final IsoObject specialWall19 = isoGridSquare.getSpecialWall(true);
            if (specialWall19 != null) {
                return specialWall19;
            }
            if (gridSquare7.isBlockedTo(isoGridSquare) || gridSquare8.isBlockedTo(isoGridSquare)) {
                return null;
            }
            final IsoObject specialSolid16 = isoGridSquare.getSpecialSolid();
            if (specialSolid16 != null) {
                return specialSolid16;
            }
            return null;
        }
    }
    
    public IsoObject getDoorFrameTo(final IsoGridSquare isoGridSquare) {
        ArrayList<IsoObject> list;
        if (isoGridSquare.y < this.y || isoGridSquare.x < this.x) {
            list = this.SpecialObjects;
        }
        else {
            list = isoGridSquare.SpecialObjects;
        }
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) instanceof IsoDoor) {
                final IsoDoor isoDoor = list.get(i);
                final boolean north = isoDoor.north;
                if (north && isoGridSquare.y != this.y) {
                    return isoDoor;
                }
                if (!north && isoGridSquare.x != this.x) {
                    return isoDoor;
                }
            }
            else if (list.get(i) instanceof IsoThumpable && list.get(i).isDoor) {
                final IsoThumpable isoThumpable = list.get(i);
                final boolean north2 = isoThumpable.north;
                if (north2 && isoGridSquare.y != this.y) {
                    return isoThumpable;
                }
                if (!north2 && isoGridSquare.x != this.x) {
                    return isoThumpable;
                }
            }
        }
        return null;
    }
    
    public static void getSquaresForThread(final ArrayDeque<IsoGridSquare> arrayDeque, final int n) {
        for (int i = 0; i < n; ++i) {
            final IsoGridSquare e = IsoGridSquare.isoGridSquareCache.poll();
            if (e == null) {
                arrayDeque.add(new IsoGridSquare(null, null, 0, 0, 0));
            }
            else {
                arrayDeque.add(e);
            }
        }
    }
    
    public static IsoGridSquare getNew(final IsoCell isoCell, final SliceY sliceY, final int x, final int y, final int z) {
        final IsoGridSquare isoGridSquare = IsoGridSquare.isoGridSquareCache.poll();
        if (isoGridSquare == null) {
            return new IsoGridSquare(isoCell, sliceY, x, y, z);
        }
        isoGridSquare.x = x;
        isoGridSquare.y = y;
        isoGridSquare.z = z;
        isoGridSquare.CachedScreenValue = -1;
        IsoGridSquare.col = 0;
        IsoGridSquare.path = 0;
        IsoGridSquare.pathdoor = 0;
        IsoGridSquare.vision = 0;
        isoGridSquare.collideMatrix = 134217727;
        isoGridSquare.pathMatrix = 134217727;
        isoGridSquare.visionMatrix = 0;
        return isoGridSquare;
    }
    
    public static IsoGridSquare getNew(final ArrayDeque<IsoGridSquare> arrayDeque, final IsoCell isoCell, final SliceY sliceY, final int x, final int y, final int z) {
        if (arrayDeque.isEmpty()) {
            return new IsoGridSquare(isoCell, sliceY, x, y, z);
        }
        final IsoGridSquare isoGridSquare = arrayDeque.pop();
        isoGridSquare.x = x;
        isoGridSquare.y = y;
        isoGridSquare.z = z;
        isoGridSquare.CachedScreenValue = -1;
        IsoGridSquare.col = 0;
        IsoGridSquare.path = 0;
        IsoGridSquare.pathdoor = 0;
        IsoGridSquare.vision = 0;
        isoGridSquare.collideMatrix = 134217727;
        isoGridSquare.pathMatrix = 134217727;
        isoGridSquare.visionMatrix = 0;
        return isoGridSquare;
    }
    
    @Deprecated
    public long getHashCodeObjects() {
        this.recalcHashCodeObjects();
        return this.hashCodeObjects;
    }
    
    @Deprecated
    public int getHashCodeObjectsInt() {
        this.recalcHashCodeObjects();
        return (int)this.hashCodeObjects;
    }
    
    @Deprecated
    public void recalcHashCodeObjects() {
        this.hashCodeObjects = 0L;
    }
    
    @Deprecated
    public int hashCodeNoOverride() {
        final int n = 0;
        this.recalcHashCodeObjects();
        int n2 = (int)(n * 2 + this.Objects.size() + this.getHashCodeObjects());
        for (int i = 0; i < this.Objects.size(); ++i) {
            n2 = n2 * 2 + this.Objects.get(i).hashCode();
        }
        int n3 = 0;
        for (int j = 0; j < this.StaticMovingObjects.size(); ++j) {
            if (this.StaticMovingObjects.get(j) instanceof IsoDeadBody) {
                ++n3;
            }
        }
        int n4 = n2 * 2 + n3;
        for (int k = 0; k < this.StaticMovingObjects.size(); ++k) {
            final IsoMovingObject isoMovingObject = this.StaticMovingObjects.get(k);
            if (isoMovingObject instanceof IsoDeadBody) {
                n4 = n4 * 2 + isoMovingObject.hashCode();
            }
        }
        if (this.table != null && !this.table.isEmpty()) {
            n4 = n4 * 2 + this.table.hashCode();
        }
        int n5 = 0;
        if (this.isOverlayDone()) {
            n5 = (byte)(n5 | 0x1);
        }
        if (this.haveRoof) {
            n5 = (byte)(n5 | 0x2);
        }
        if (this.burntOut) {
            n5 = (byte)(n5 | 0x4);
        }
        int n6 = (n4 * 2 + n5) * 2 + this.getErosionData().hashCode();
        if (this.getTrapPositionX() > 0) {
            n6 = ((n6 * 2 + this.getTrapPositionX()) * 2 + this.getTrapPositionY()) * 2 + this.getTrapPositionZ();
        }
        return (n6 * 2 + (this.haveElectricity() ? 1 : 0)) * 2 + (this.haveSheetRope ? 1 : 0);
    }
    
    public IsoGridSquare(final IsoCell isoCell, final SliceY sliceY, final int x, final int y, final int z) {
        this.nav = new IsoGridSquare[8];
        this.collideMatrix = -1;
        this.pathMatrix = -1;
        this.visionMatrix = -1;
        this.room = null;
        this.haveSheetRope = false;
        this.hasSetIsoWorldRegion = false;
        this.ObjectsSyncCount = 0;
        this.playerCutawayFlags = new boolean[4];
        this.playerCutawayFlagLockUntilTimes = new long[4];
        this.targetPlayerCutawayFlags = new boolean[4];
        this.playerIsDissolvedFlags = new boolean[4];
        this.playerIsDissolvedFlagLockUntilTimes = new long[4];
        this.targetPlayerIsDissolvedFlags = new boolean[4];
        this.water = null;
        this.puddles = null;
        this.puddlesCacheSize = -1.0f;
        this.puddlesCacheLevel = -1.0f;
        this.lighting = new ILighting[4];
        this.CachedScreenValue = -1;
        this.SolidFloorCached = false;
        this.SolidFloor = false;
        this.CacheIsFree = false;
        this.CachedIsFree = false;
        this.roomID = -1;
        this.ID = -999;
        this.DeferedCharacters = new ArrayList<IsoGameCharacter>();
        this.DeferredCharacterTick = -1;
        this.StaticMovingObjects = new ArrayList<IsoMovingObject>(0);
        this.MovingObjects = new ArrayList<IsoMovingObject>(0);
        this.Objects = new PZArrayList<IsoObject>(IsoObject.class, 2);
        this.localTemporaryObjects = new PZArrayList<IsoObject>(IsoObject.class, 2);
        this.WorldObjects = new ArrayList<IsoWorldInventoryObject>();
        this.hasTypes = new ZomboidBitFlag(IsoObjectType.MAX.index());
        this.Properties = new PropertyContainer();
        this.SpecialObjects = new ArrayList<IsoObject>(0);
        this.haveRoof = false;
        this.burntOut = false;
        this.bHasFlies = false;
        this.OcclusionDataCache = null;
        this.overlayDone = false;
        this.table = null;
        this.trapPositionX = -1;
        this.trapPositionY = -1;
        this.trapPositionZ = -1;
        this.haveElectricity = false;
        this.hourLastSeen = Integer.MIN_VALUE;
        this.propertiesDirty = true;
        this.splashFrame = -1.0f;
        this.lightInfo = new ColorInfo[4];
        this.RainDrop = null;
        this.RainSplash = null;
        this.ID = ++IsoGridSquare.IDMax;
        this.x = x;
        this.y = y;
        this.z = z;
        this.CachedScreenValue = -1;
        IsoGridSquare.col = 0;
        IsoGridSquare.path = 0;
        IsoGridSquare.pathdoor = 0;
        IsoGridSquare.vision = 0;
        this.collideMatrix = 134217727;
        this.pathMatrix = 134217727;
        this.visionMatrix = 0;
        for (int i = 0; i < 4; ++i) {
            if (GameServer.bServer) {
                if (i == 0) {
                    this.lighting[i] = new ServerLOS.ServerLighting();
                }
            }
            else if (LightingJNI.init) {
                this.lighting[i] = new LightingJNI.JNILighting(i, this);
            }
            else {
                this.lighting[i] = new Lighting();
            }
        }
    }
    
    public IsoGridSquare getTileInDirection(final IsoDirections isoDirections) {
        if (isoDirections == IsoDirections.N) {
            return this.getCell().getGridSquare(this.x, this.y - 1, this.z);
        }
        if (isoDirections == IsoDirections.NE) {
            return this.getCell().getGridSquare(this.x + 1, this.y - 1, this.z);
        }
        if (isoDirections == IsoDirections.NW) {
            return this.getCell().getGridSquare(this.x - 1, this.y - 1, this.z);
        }
        if (isoDirections == IsoDirections.E) {
            return this.getCell().getGridSquare(this.x + 1, this.y, this.z);
        }
        if (isoDirections == IsoDirections.W) {
            return this.getCell().getGridSquare(this.x - 1, this.y, this.z);
        }
        if (isoDirections == IsoDirections.SE) {
            return this.getCell().getGridSquare(this.x + 1, this.y + 1, this.z);
        }
        if (isoDirections == IsoDirections.SW) {
            return this.getCell().getGridSquare(this.x - 1, this.y + 1, this.z);
        }
        if (isoDirections == IsoDirections.S) {
            return this.getCell().getGridSquare(this.x, this.y + 1, this.z);
        }
        return null;
    }
    
    IsoObject getWall() {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (isoObject != null) {
                if (isoObject.sprite != null) {
                    if (isoObject.sprite.cutW || isoObject.sprite.cutN) {
                        return isoObject;
                    }
                }
            }
        }
        return null;
    }
    
    public IsoObject getThumpableWall(final boolean b) {
        final IsoObject wall = this.getWall(b);
        if (wall != null && wall instanceof IsoThumpable) {
            return wall;
        }
        return null;
    }
    
    public IsoObject getHoppableWall(final boolean b) {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (isoObject != null) {
                if (isoObject.sprite != null) {
                    final PropertyContainer properties = isoObject.getProperties();
                    final boolean b2 = properties.Is(IsoFlagType.TallHoppableW) && !properties.Is(IsoFlagType.WallWTrans);
                    final boolean b3 = properties.Is(IsoFlagType.TallHoppableN) && !properties.Is(IsoFlagType.WallNTrans);
                    if ((b2 && !b) || (b3 && b)) {
                        return isoObject;
                    }
                }
            }
        }
        return null;
    }
    
    public IsoObject getThumpableWallOrHoppable(final boolean b) {
        final IsoObject thumpableWall = this.getThumpableWall(b);
        final IsoObject hoppableWall = this.getHoppableWall(b);
        if (thumpableWall != null && hoppableWall != null && thumpableWall == hoppableWall) {
            return thumpableWall;
        }
        if (thumpableWall == null && hoppableWall != null) {
            return hoppableWall;
        }
        if (thumpableWall != null && hoppableWall == null) {
            return thumpableWall;
        }
        return null;
    }
    
    public Boolean getWallFull() {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (isoObject != null) {
                if (isoObject.sprite != null) {
                    if (isoObject.sprite.cutN || isoObject.sprite.cutW || isoObject.sprite.getProperties().Is(IsoFlagType.WallN) || isoObject.sprite.getProperties().Is(IsoFlagType.WallW)) {
                        return true;
                    }
                }
            }
        }
        final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x, this.y + 1, this.z);
        if (gridSquare != null && this.isWallTo(gridSquare)) {
            return true;
        }
        return false;
    }
    
    public IsoObject getWall(final boolean b) {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (isoObject != null) {
                if (isoObject.sprite != null) {
                    if ((isoObject.sprite.cutN && b) || (isoObject.sprite.cutW && !b)) {
                        return isoObject;
                    }
                }
            }
        }
        return null;
    }
    
    public IsoObject getWallSE() {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (isoObject != null) {
                if (isoObject.sprite != null) {
                    if (isoObject.sprite.getProperties().Is(IsoFlagType.WallSE)) {
                        return isoObject;
                    }
                }
            }
        }
        return null;
    }
    
    public IsoObject getFloor() {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (isoObject.sprite != null && isoObject.sprite.getProperties().Is(IsoFlagType.solidfloor)) {
                return isoObject;
            }
        }
        return null;
    }
    
    public IsoObject getPlayerBuiltFloor() {
        if (this.getBuilding() != null || this.roofHideBuilding != null) {
            return null;
        }
        return this.getFloor();
    }
    
    public void interpolateLight(final ColorInfo colorInfo, float n, float n2) {
        this.getCell();
        if (n < 0.0f) {
            n = 0.0f;
        }
        if (n > 1.0f) {
            n = 1.0f;
        }
        if (n2 < 0.0f) {
            n2 = 0.0f;
        }
        if (n2 > 1.0f) {
            n2 = 1.0f;
        }
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final int vertLight = this.getVertLight(0, playerIndex);
        final int vertLight2 = this.getVertLight(1, playerIndex);
        final int vertLight3 = this.getVertLight(2, playerIndex);
        final int vertLight4 = this.getVertLight(3, playerIndex);
        IsoGridSquare.tl.fromColor(vertLight);
        IsoGridSquare.bl.fromColor(vertLight4);
        IsoGridSquare.tr.fromColor(vertLight2);
        IsoGridSquare.br.fromColor(vertLight3);
        IsoGridSquare.tl.interp(IsoGridSquare.tr, n, IsoGridSquare.interp1);
        IsoGridSquare.bl.interp(IsoGridSquare.br, n, IsoGridSquare.interp2);
        IsoGridSquare.interp1.interp(IsoGridSquare.interp2, n2, IsoGridSquare.finalCol);
        colorInfo.r = IsoGridSquare.finalCol.r;
        colorInfo.g = IsoGridSquare.finalCol.g;
        colorInfo.b = IsoGridSquare.finalCol.b;
        colorInfo.a = IsoGridSquare.finalCol.a;
    }
    
    public void EnsureSurroundNotNull() {
        assert !GameServer.bServer;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (i != 0 || j != 0) {
                    if (IsoWorld.instance.isValidSquare(this.x + i, this.y + j, this.z)) {
                        if (this.getCell().getChunkForGridSquare(this.x + i, this.y + j, this.z) != null) {
                            if (this.getCell().getGridSquare(this.x + i, this.y + j, this.z) == null) {
                                this.getCell().ConnectNewSquare(getNew(this.getCell(), null, this.x + i, this.y + j, this.z), false);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public IsoObject addFloor(final String s) {
        IsoRegions.setPreviousFlags(this);
        final IsoObject isoObject = new IsoObject(this.getCell(), this, s);
        boolean b = false;
        for (int i = 0; i < this.getObjects().size(); ++i) {
            final IsoObject isoObject2 = this.getObjects().get(i);
            final IsoSprite sprite = isoObject2.sprite;
            if (sprite != null && (sprite.getProperties().Is(IsoFlagType.solidfloor) || sprite.getProperties().Is(IsoFlagType.noStart) || (sprite.getProperties().Is(IsoFlagType.vegitation) && isoObject2.getType() != IsoObjectType.tree) || (sprite.getName() != null && sprite.getName().startsWith("blends_grassoverlays")))) {
                if (sprite.getName() != null && sprite.getName().startsWith("floors_rugs")) {
                    b = true;
                }
                else {
                    this.transmitRemoveItemFromSquare(isoObject2);
                    --i;
                }
            }
        }
        isoObject.sprite.getProperties().Set(IsoFlagType.solidfloor);
        if (b) {
            this.getObjects().add(0, isoObject);
        }
        else {
            this.getObjects().add(isoObject);
        }
        this.EnsureSurroundNotNull();
        this.RecalcProperties();
        this.getCell().checkHaveRoof(this.x, this.y);
        for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
            LosUtil.cachecleared[j] = true;
        }
        setRecalcLightTime(-1);
        GameTime.getInstance().lightSourceUpdate = 100.0f;
        isoObject.transmitCompleteItemToServer();
        this.RecalcAllWithNeighbours(true);
        for (int k = this.z - 1; k > 0; --k) {
            IsoGridSquare isoGridSquare = this.getCell().getGridSquare(this.x, this.y, k);
            if (isoGridSquare == null) {
                isoGridSquare = getNew(this.getCell(), null, this.x, this.y, k);
                this.getCell().ConnectNewSquare(isoGridSquare, false);
            }
            isoGridSquare.EnsureSurroundNotNull();
            isoGridSquare.RecalcAllWithNeighbours(true);
        }
        this.setCachedIsFree(false);
        PolygonalMap2.instance.squareChanged(this);
        IsoGridOcclusionData.SquareChanged();
        IsoRegions.squareChanged(this);
        this.clearWater();
        return isoObject;
    }
    
    public IsoThumpable AddStairs(final boolean b, final int n, final String s, final String s2, final KahluaTable kahluaTable) {
        IsoRegions.setPreviousFlags(this);
        this.EnsureSurroundNotNull();
        final boolean b2 = !this.TreatAsSolidFloor() && !this.HasStairsBelow();
        this.CachedIsFree = false;
        final IsoThumpable isoThumpable = new IsoThumpable(this.getCell(), this, s, b, kahluaTable);
        if (b) {
            if (n == 0) {
                isoThumpable.setType(IsoObjectType.stairsBN);
            }
            if (n == 1) {
                isoThumpable.setType(IsoObjectType.stairsMN);
            }
            if (n == 2) {
                isoThumpable.setType(IsoObjectType.stairsTN);
                isoThumpable.sprite.getProperties().Set(b ? IsoFlagType.cutN : IsoFlagType.cutW);
            }
        }
        if (!b) {
            if (n == 0) {
                isoThumpable.setType(IsoObjectType.stairsBW);
            }
            if (n == 1) {
                isoThumpable.setType(IsoObjectType.stairsMW);
            }
            if (n == 2) {
                isoThumpable.setType(IsoObjectType.stairsTW);
                isoThumpable.sprite.getProperties().Set(b ? IsoFlagType.cutN : IsoFlagType.cutW);
            }
        }
        this.AddSpecialObject(isoThumpable);
        if (b2 && n == 2) {
            int i = this.z - 1;
            IsoGridSquare isoGridSquare = this.getCell().getGridSquare(this.x, this.y, i);
            if (isoGridSquare == null) {
                isoGridSquare = new IsoGridSquare(this.getCell(), null, this.x, this.y, i);
                this.getCell().ConnectNewSquare(isoGridSquare, true);
            }
            while (i >= 0) {
                isoGridSquare.AddSpecialObject(new IsoThumpable(this.getCell(), isoGridSquare, s2, b, kahluaTable));
                if (isoGridSquare.TreatAsSolidFloor()) {
                    break;
                }
                --i;
                if (this.getCell().getGridSquare(isoGridSquare.x, isoGridSquare.y, i) == null) {
                    isoGridSquare = new IsoGridSquare(this.getCell(), null, isoGridSquare.x, isoGridSquare.y, i);
                    this.getCell().ConnectNewSquare(isoGridSquare, true);
                }
                else {
                    isoGridSquare = this.getCell().getGridSquare(isoGridSquare.x, isoGridSquare.y, i);
                }
            }
        }
        if (n == 2) {
            IsoGridSquare isoGridSquare2 = null;
            if (b) {
                if (IsoWorld.instance.isValidSquare(this.x, this.y - 1, this.z + 1)) {
                    isoGridSquare2 = this.getCell().getGridSquare(this.x, this.y - 1, this.z + 1);
                    if (isoGridSquare2 == null) {
                        isoGridSquare2 = new IsoGridSquare(this.getCell(), null, this.x, this.y - 1, this.z + 1);
                        this.getCell().ConnectNewSquare(isoGridSquare2, false);
                    }
                    if (!isoGridSquare2.Properties.Is(IsoFlagType.solidfloor)) {
                        isoGridSquare2.addFloor("carpentry_02_57");
                    }
                }
            }
            else if (IsoWorld.instance.isValidSquare(this.x - 1, this.y, this.z + 1)) {
                isoGridSquare2 = this.getCell().getGridSquare(this.x - 1, this.y, this.z + 1);
                if (isoGridSquare2 == null) {
                    isoGridSquare2 = new IsoGridSquare(this.getCell(), null, this.x - 1, this.y, this.z + 1);
                    this.getCell().ConnectNewSquare(isoGridSquare2, false);
                }
                if (!isoGridSquare2.Properties.Is(IsoFlagType.solidfloor)) {
                    isoGridSquare2.addFloor("carpentry_02_57");
                }
            }
            isoGridSquare2.getModData().rawset(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, b), (Object)true);
            if (this.getCell().getGridSquare(this.x, this.y, this.z + 1) == null) {
                this.getCell().ConnectNewSquare(new IsoGridSquare(this.getCell(), null, this.x, this.y, this.z + 1), false);
            }
        }
        for (int j = this.getX() - 1; j <= this.getX() + 1; ++j) {
            for (int k = this.getY() - 1; k <= this.getY() + 1; ++k) {
                for (int l = this.getZ() - 1; l <= this.getZ() + 1; ++l) {
                    if (IsoWorld.instance.isValidSquare(j, k, l)) {
                        IsoGridSquare gridSquare = this.getCell().getGridSquare(j, k, l);
                        if (gridSquare == null) {
                            gridSquare = new IsoGridSquare(this.getCell(), null, j, k, l);
                            this.getCell().ConnectNewSquare(gridSquare, false);
                        }
                        gridSquare.ReCalculateCollide(this);
                        gridSquare.ReCalculateVisionBlocked(this);
                        gridSquare.ReCalculatePathFind(this);
                        this.ReCalculateCollide(gridSquare);
                        this.ReCalculatePathFind(gridSquare);
                        this.ReCalculateVisionBlocked(gridSquare);
                        gridSquare.CachedIsFree = false;
                    }
                }
            }
        }
        return isoThumpable;
    }
    
    void ReCalculateAll(final IsoGridSquare isoGridSquare) {
        this.ReCalculateAll(isoGridSquare, IsoGridSquare.cellGetSquare);
    }
    
    void ReCalculateAll(final IsoGridSquare isoGridSquare, final GetSquare getSquare) {
        if (isoGridSquare == null || isoGridSquare == this) {
            return;
        }
        this.SolidFloorCached = false;
        isoGridSquare.SolidFloorCached = false;
        this.RecalcPropertiesIfNeeded();
        isoGridSquare.RecalcPropertiesIfNeeded();
        this.ReCalculateCollide(isoGridSquare, getSquare);
        isoGridSquare.ReCalculateCollide(this, getSquare);
        this.ReCalculatePathFind(isoGridSquare, getSquare);
        isoGridSquare.ReCalculatePathFind(this, getSquare);
        this.ReCalculateVisionBlocked(isoGridSquare, getSquare);
        isoGridSquare.ReCalculateVisionBlocked(this, getSquare);
        this.setBlockedGridPointers(getSquare);
        isoGridSquare.setBlockedGridPointers(getSquare);
    }
    
    void ReCalculateAll(final boolean b, final IsoGridSquare isoGridSquare, final GetSquare getSquare) {
        if (isoGridSquare == null || isoGridSquare == this) {
            return;
        }
        this.SolidFloorCached = false;
        isoGridSquare.SolidFloorCached = false;
        this.RecalcPropertiesIfNeeded();
        if (b) {
            isoGridSquare.RecalcPropertiesIfNeeded();
        }
        this.ReCalculateCollide(isoGridSquare, getSquare);
        if (b) {
            isoGridSquare.ReCalculateCollide(this, getSquare);
        }
        this.ReCalculatePathFind(isoGridSquare, getSquare);
        if (b) {
            isoGridSquare.ReCalculatePathFind(this, getSquare);
        }
        this.ReCalculateVisionBlocked(isoGridSquare, getSquare);
        if (b) {
            isoGridSquare.ReCalculateVisionBlocked(this, getSquare);
        }
        this.setBlockedGridPointers(getSquare);
        if (b) {
            isoGridSquare.setBlockedGridPointers(getSquare);
        }
    }
    
    void ReCalculateMineOnly(final IsoGridSquare isoGridSquare) {
        this.SolidFloorCached = false;
        this.RecalcProperties();
        this.ReCalculateCollide(isoGridSquare);
        this.ReCalculatePathFind(isoGridSquare);
        this.ReCalculateVisionBlocked(isoGridSquare);
        this.setBlockedGridPointers(IsoGridSquare.cellGetSquare);
    }
    
    public void RecalcAllWithNeighbours(final boolean b) {
        this.RecalcAllWithNeighbours(b, IsoGridSquare.cellGetSquare);
    }
    
    public void RecalcAllWithNeighbours(final boolean b, final GetSquare getSquare) {
        this.SolidFloorCached = false;
        this.RecalcPropertiesIfNeeded();
        for (int i = this.getX() - 1; i <= this.getX() + 1; ++i) {
            for (int j = this.getY() - 1; j <= this.getY() + 1; ++j) {
                for (int k = this.getZ() - 1; k <= this.getZ() + 1; ++k) {
                    if (IsoWorld.instance.isValidSquare(i, j, k)) {
                        final int n = i - this.getX();
                        final int n2 = j - this.getY();
                        final int n3 = k - this.getZ();
                        if (n != 0 || n2 != 0 || n3 != 0) {
                            final IsoGridSquare gridSquare = getSquare.getGridSquare(i, j, k);
                            if (gridSquare != null) {
                                gridSquare.DirtySlice();
                                this.ReCalculateAll(b, gridSquare, getSquare);
                            }
                        }
                    }
                }
            }
        }
        IsoWorld.instance.CurrentCell.DoGridNav(this, getSquare);
        final IsoGridSquare isoGridSquare = this.nav[IsoDirections.N.index()];
        final IsoGridSquare isoGridSquare2 = this.nav[IsoDirections.S.index()];
        final IsoGridSquare isoGridSquare3 = this.nav[IsoDirections.W.index()];
        final IsoGridSquare isoGridSquare4 = this.nav[IsoDirections.E.index()];
        if (isoGridSquare != null && isoGridSquare3 != null) {
            isoGridSquare.ReCalculateAll(isoGridSquare3, getSquare);
        }
        if (isoGridSquare != null && isoGridSquare4 != null) {
            isoGridSquare.ReCalculateAll(isoGridSquare4, getSquare);
        }
        if (isoGridSquare2 != null && isoGridSquare3 != null) {
            isoGridSquare2.ReCalculateAll(isoGridSquare3, getSquare);
        }
        if (isoGridSquare2 != null && isoGridSquare4 != null) {
            isoGridSquare2.ReCalculateAll(isoGridSquare4, getSquare);
        }
    }
    
    public void RecalcAllWithNeighboursMineOnly() {
        this.SolidFloorCached = false;
        this.RecalcProperties();
        for (int i = this.getX() - 1; i <= this.getX() + 1; ++i) {
            for (int j = this.getY() - 1; j <= this.getY() + 1; ++j) {
                for (int k = this.getZ() - 1; k <= this.getZ() + 1; ++k) {
                    if (k >= 0) {
                        final int n = i - this.getX();
                        final int n2 = j - this.getY();
                        final int n3 = k - this.getZ();
                        if (n != 0 || n2 != 0 || n3 != 0) {
                            final IsoGridSquare gridSquare = this.getCell().getGridSquare(i, j, k);
                            if (gridSquare != null) {
                                gridSquare.DirtySlice();
                                this.ReCalculateMineOnly(gridSquare);
                            }
                        }
                    }
                }
            }
        }
    }
    
    boolean IsWindow(final int n, final int n2, final int n3) {
        final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x + n, this.y + n2, this.z + n3);
        return this.getWindowTo(gridSquare) != null || this.getWindowThumpableTo(gridSquare) != null;
    }
    
    void RemoveAllWith(final IsoFlagType isoFlagType) {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject o = this.Objects.get(i);
            if (o.sprite != null && o.sprite.getProperties().Is(isoFlagType)) {
                this.Objects.remove(o);
                this.SpecialObjects.remove(o);
                --i;
            }
        }
        this.RecalcAllWithNeighbours(true);
    }
    
    public boolean hasSupport() {
        final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x, this.y + 1, this.z);
        final IsoGridSquare gridSquare2 = this.getCell().getGridSquare(this.x + 1, this.y, this.z);
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (isoObject.sprite != null && (isoObject.sprite.getProperties().Is(IsoFlagType.solid) || ((isoObject.sprite.getProperties().Is(IsoFlagType.cutW) || isoObject.sprite.getProperties().Is(IsoFlagType.cutN)) && !isoObject.sprite.Properties.Is(IsoFlagType.halfheight)))) {
                return true;
            }
        }
        return (gridSquare != null && gridSquare.Properties.Is(IsoFlagType.cutN) && !gridSquare.Properties.Is(IsoFlagType.halfheight)) || (gridSquare2 != null && gridSquare2.Properties.Is(IsoFlagType.cutW) && !gridSquare.Properties.Is(IsoFlagType.halfheight));
    }
    
    public Integer getID() {
        return this.ID;
    }
    
    public void setID(final int i) {
        this.ID = i;
    }
    
    private int savematrix(final boolean[][][] array, final byte[] array2, int n) {
        for (int i = 0; i <= 2; ++i) {
            for (int j = 0; j <= 2; ++j) {
                for (int k = 0; k <= 2; ++k) {
                    array2[n] = (byte)(array[i][j][k] ? 1 : 0);
                    ++n;
                }
            }
        }
        return n;
    }
    
    private int loadmatrix(final boolean[][][] array, final byte[] array2, int n) {
        for (int i = 0; i <= 2; ++i) {
            for (int j = 0; j <= 2; ++j) {
                for (int k = 0; k <= 2; ++k) {
                    array[i][j][k] = (array2[n] != 0);
                    ++n;
                }
            }
        }
        return n;
    }
    
    private void savematrix(final boolean[][][] array, final ByteBuffer byteBuffer) {
        for (int i = 0; i <= 2; ++i) {
            for (int j = 0; j <= 2; ++j) {
                for (int k = 0; k <= 2; ++k) {
                    byteBuffer.put((byte)(array[i][j][k] ? 1 : 0));
                }
            }
        }
    }
    
    private void loadmatrix(final boolean[][][] array, final ByteBuffer byteBuffer) {
        for (int i = 0; i <= 2; ++i) {
            for (int j = 0; j <= 2; ++j) {
                for (int k = 0; k <= 2; ++k) {
                    array[i][j][k] = (byteBuffer.get() != 0);
                }
            }
        }
    }
    
    public void DirtySlice() {
    }
    
    public void setHourSeenToCurrent() {
        this.hourLastSeen = (int)GameTime.instance.getWorldAgeHours();
    }
    
    public void splatBlood(final int n, float n2) {
        n2 *= 2.0f;
        n2 *= 3.0f;
        if (n2 > 1.0f) {
            n2 = 1.0f;
        }
        IsoGridSquare gridSquare = this;
        IsoGridSquare gridSquare2 = this;
        for (int i = 0; i < n; ++i) {
            if (gridSquare != null) {
                gridSquare = this.getCell().getGridSquare(this.getX(), this.getY() - i, this.getZ());
            }
            if (gridSquare2 != null) {
                gridSquare2 = this.getCell().getGridSquare(this.getX() - i, this.getY(), this.getZ());
            }
            final float n3 = 0.0f;
            if (gridSquare2 != null && gridSquare2.testCollideAdjacent(null, -1, 0, 0)) {
                boolean b = false;
                boolean b2 = false;
                int n4 = 0;
                int n5 = 0;
                if (gridSquare2.getS() != null && gridSquare2.getS().testCollideAdjacent(null, -1, 0, 0)) {
                    b = true;
                }
                if (gridSquare2.getN() != null && gridSquare2.getN().testCollideAdjacent(null, -1, 0, 0)) {
                    b2 = true;
                }
                if (b) {
                    n4 = -1;
                }
                if (b2) {
                    n5 = 1;
                }
                final int n6 = n5 - n4;
                boolean b3 = false;
                int n7 = 0;
                int n8 = 0;
                if (n6 > 0 && Rand.Next(2) == 0) {
                    b3 = true;
                    if (n6 > 1) {
                        if (Rand.Next(2) == 0) {
                            n7 = -1;
                            n8 = 0;
                        }
                        else {
                            n7 = 0;
                            n8 = 1;
                        }
                    }
                    else {
                        n7 = n4;
                        n8 = n5;
                    }
                }
                float n9 = Rand.Next(100) / 300.0f;
                final IsoGridSquare gridSquare3 = this.getCell().getGridSquare(gridSquare2.getX(), gridSquare2.getY() + n7, gridSquare2.getZ());
                final IsoGridSquare gridSquare4 = this.getCell().getGridSquare(gridSquare2.getX(), gridSquare2.getY() + n8, gridSquare2.getZ());
                if (gridSquare3 == null || gridSquare4 == null || !gridSquare3.Is(IsoFlagType.cutW) || !gridSquare4.Is(IsoFlagType.cutW) || gridSquare3.getProperties().Is(IsoFlagType.WallSE) || gridSquare4.getProperties().Is(IsoFlagType.WallSE) || gridSquare3.Is(IsoFlagType.HoppableW) || gridSquare4.Is(IsoFlagType.HoppableW)) {
                    b3 = false;
                }
                if (b3) {
                    int n10 = 24 + Rand.Next(2) * 2;
                    if (Rand.Next(2) == 0) {
                        n10 += 8;
                    }
                    gridSquare3.DoSplat(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n10 + 1), false, IsoFlagType.cutW, n3, n9, n2);
                    gridSquare4.DoSplat(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n10 + 0), false, IsoFlagType.cutW, n3, n9, n2);
                }
                else {
                    int n11 = 0;
                    switch (Rand.Next(3)) {
                        case 0: {
                            n11 = 0 + Rand.Next(4);
                            break;
                        }
                        case 1: {
                            n11 = 8 + Rand.Next(4);
                            break;
                        }
                        case 2: {
                            n11 = 16 + Rand.Next(4);
                            break;
                        }
                    }
                    if (n11 == 17 || n11 == 19) {
                        n9 = 0.0f;
                    }
                    if (gridSquare2.Is(IsoFlagType.HoppableW)) {
                        gridSquare2.DoSplat(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n11), false, IsoFlagType.HoppableW, n3, 0.0f, n2);
                    }
                    else {
                        gridSquare2.DoSplat(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n11), false, IsoFlagType.cutW, n3, n9, n2);
                    }
                }
                gridSquare2 = null;
            }
            if (gridSquare != null && gridSquare.testCollideAdjacent(null, 0, -1, 0)) {
                boolean b4 = false;
                boolean b5 = false;
                int n12 = 0;
                int n13 = 0;
                if (gridSquare.getW() != null && gridSquare.getW().testCollideAdjacent(null, 0, -1, 0)) {
                    b4 = true;
                }
                if (gridSquare.getE() != null && gridSquare.getE().testCollideAdjacent(null, 0, -1, 0)) {
                    b5 = true;
                }
                if (b4) {
                    n12 = -1;
                }
                if (b5) {
                    n13 = 1;
                }
                final int n14 = n13 - n12;
                boolean b6 = false;
                int n15 = 0;
                int n16 = 0;
                if (n14 > 0 && Rand.Next(2) == 0) {
                    b6 = true;
                    if (n14 > 1) {
                        if (Rand.Next(2) == 0) {
                            n15 = -1;
                            n16 = 0;
                        }
                        else {
                            n15 = 0;
                            n16 = 1;
                        }
                    }
                    else {
                        n15 = n12;
                        n16 = n13;
                    }
                }
                float n17 = Rand.Next(100) / 300.0f;
                final IsoGridSquare gridSquare5 = this.getCell().getGridSquare(gridSquare.getX() + n15, gridSquare.getY(), gridSquare.getZ());
                final IsoGridSquare gridSquare6 = this.getCell().getGridSquare(gridSquare.getX() + n16, gridSquare.getY(), gridSquare.getZ());
                if (gridSquare5 == null || gridSquare6 == null || !gridSquare5.Is(IsoFlagType.cutN) || !gridSquare6.Is(IsoFlagType.cutN) || gridSquare5.getProperties().Is(IsoFlagType.WallSE) || gridSquare6.getProperties().Is(IsoFlagType.WallSE) || gridSquare5.Is(IsoFlagType.HoppableN) || gridSquare6.Is(IsoFlagType.HoppableN)) {
                    b6 = false;
                }
                if (b6) {
                    int n18 = 28 + Rand.Next(2) * 2;
                    if (Rand.Next(2) == 0) {
                        n18 += 8;
                    }
                    gridSquare5.DoSplat(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n18 + 0), false, IsoFlagType.cutN, n3, n17, n2);
                    gridSquare6.DoSplat(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n18 + 1), false, IsoFlagType.cutN, n3, n17, n2);
                }
                else {
                    int n19 = 0;
                    switch (Rand.Next(3)) {
                        case 0: {
                            n19 = 4 + Rand.Next(4);
                            break;
                        }
                        case 1: {
                            n19 = 12 + Rand.Next(4);
                            break;
                        }
                        case 2: {
                            n19 = 20 + Rand.Next(4);
                            break;
                        }
                    }
                    if (n19 == 20 || n19 == 22) {
                        n17 = 0.0f;
                    }
                    if (gridSquare.Is(IsoFlagType.HoppableN)) {
                        gridSquare.DoSplat(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n19), false, IsoFlagType.HoppableN, n3, n17, n2);
                    }
                    else {
                        gridSquare.DoSplat(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n19), false, IsoFlagType.cutN, n3, n17, n2);
                    }
                }
                gridSquare = null;
            }
        }
    }
    
    public boolean haveBlood() {
        if (Core.OptionBloodDecals == 0) {
            return false;
        }
        for (int i = 0; i < this.getObjects().size(); ++i) {
            final IsoObject isoObject = this.getObjects().get(i);
            if (isoObject.wallBloodSplats != null && !isoObject.wallBloodSplats.isEmpty()) {
                return true;
            }
        }
        for (int j = 0; j < this.getChunk().FloorBloodSplats.size(); ++j) {
            final IsoFloorBloodSplat isoFloorBloodSplat = this.getChunk().FloorBloodSplats.get(j);
            final float n = isoFloorBloodSplat.x + this.getChunk().wx * 10;
            final float n2 = isoFloorBloodSplat.y + this.getChunk().wy * 10;
            if ((int)n - 1 <= this.x && (int)n + 1 >= this.x && (int)n2 - 1 <= this.y && (int)n2 + 1 >= this.y) {
                return true;
            }
        }
        return false;
    }
    
    public void removeBlood(final boolean b, final boolean b2) {
        for (int i = 0; i < this.getObjects().size(); ++i) {
            final IsoObject isoObject = this.getObjects().get(i);
            if (isoObject.wallBloodSplats != null) {
                isoObject.wallBloodSplats.clear();
            }
        }
        if (!b2) {
            for (int j = 0; j < this.getChunk().FloorBloodSplats.size(); ++j) {
                final IsoFloorBloodSplat isoFloorBloodSplat = this.getChunk().FloorBloodSplats.get(j);
                final int n = (int)(this.getChunk().wx * 10 + isoFloorBloodSplat.x);
                final int n2 = (int)(this.getChunk().wy * 10 + isoFloorBloodSplat.y);
                if (n >= this.getX() - 1 && n <= this.getX() + 1 && n2 >= this.getY() - 1 && n2 <= this.getY() + 1) {
                    this.getChunk().FloorBloodSplats.remove(j);
                    --j;
                }
            }
        }
        if (GameClient.bClient && !b) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.RemoveBlood.doPacket(startPacket);
            startPacket.putInt(this.x);
            startPacket.putInt(this.y);
            startPacket.putInt(this.z);
            startPacket.putBoolean(b2);
            PacketTypes.PacketType.RemoveBlood.send(GameClient.connection);
        }
    }
    
    public void DoSplat(final String s, final boolean b, final IsoFlagType isoFlagType, final float n, final float n2, final float n3) {
        for (int i = 0; i < this.getObjects().size(); ++i) {
            final IsoObject isoObject = this.getObjects().get(i);
            if (isoObject.sprite != null && isoObject.sprite.getProperties().Is(isoFlagType)) {
                final IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, s, 0);
                if (sprite == null) {
                    return;
                }
                if (isoObject.wallBloodSplats == null) {
                    isoObject.wallBloodSplats = new ArrayList<IsoWallBloodSplat>();
                }
                isoObject.wallBloodSplats.add(new IsoWallBloodSplat((float)GameTime.getInstance().getWorldAgeHours(), sprite));
            }
        }
    }
    
    public void ClearTileObjects() {
        this.Objects.clear();
        this.RecalcProperties();
    }
    
    public void ClearTileObjectsExceptFloor() {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (isoObject.sprite == null || !isoObject.sprite.getProperties().Is(IsoFlagType.solidfloor)) {
                this.Objects.remove(isoObject);
                --i;
            }
        }
        this.RecalcProperties();
    }
    
    public int RemoveTileObject(final IsoObject isoObject) {
        IsoRegions.setPreviousFlags(this);
        int n = this.Objects.indexOf(isoObject);
        if (!this.Objects.contains(isoObject)) {
            n = this.SpecialObjects.indexOf(isoObject);
        }
        if (isoObject != null && this.Objects.contains(isoObject)) {
            if (isoObject.isTableSurface()) {
                for (int i = this.Objects.indexOf(isoObject) + 1; i < this.Objects.size(); ++i) {
                    final IsoObject isoObject2 = this.Objects.get(i);
                    if (isoObject2.isTableTopObject() || isoObject2.isTableSurface()) {
                        isoObject2.setRenderYOffset(isoObject2.getRenderYOffset() - isoObject.getSurfaceOffset());
                        isoObject2.sx = 0.0f;
                        isoObject2.sy = 0.0f;
                    }
                }
            }
            if (isoObject == this.getPlayerBuiltFloor()) {
                IsoGridOcclusionData.SquareChanged();
            }
            LuaEventManager.triggerEvent("OnObjectAboutToBeRemoved", isoObject);
            if (!this.Objects.contains(isoObject)) {
                throw new IllegalArgumentException("OnObjectAboutToBeRemoved not allowed to remove the object");
            }
            n = this.Objects.indexOf(isoObject);
            isoObject.removeFromWorld();
            isoObject.removeFromSquare();
            assert !this.Objects.contains(isoObject);
            assert !this.SpecialObjects.contains(isoObject);
            if (!(isoObject instanceof IsoWorldInventoryObject)) {
                this.RecalcAllWithNeighbours(true);
                this.getCell().checkHaveRoof(this.getX(), this.getY());
                for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                    LosUtil.cachecleared[j] = true;
                }
                setRecalcLightTime(-1);
                GameTime.instance.lightSourceUpdate = 100.0f;
            }
        }
        MapCollisionData.instance.squareChanged(this);
        LuaEventManager.triggerEvent("OnTileRemoved", isoObject);
        PolygonalMap2.instance.squareChanged(this);
        IsoRegions.squareChanged(this, true);
        return n;
    }
    
    public int RemoveTileObjectErosionNoRecalc(final IsoObject o) {
        final int index = this.Objects.indexOf(o);
        final IsoGridSquare square = o.square;
        o.removeFromWorld();
        o.removeFromSquare();
        square.RecalcPropertiesIfNeeded();
        assert !this.Objects.contains(o);
        assert !this.SpecialObjects.contains(o);
        return index;
    }
    
    public void AddSpecialObject(final IsoObject isoObject) {
        this.AddSpecialObject(isoObject, -1);
    }
    
    public void AddSpecialObject(final IsoObject e, int placeWallAndDoorCheck) {
        if (e == null) {
            return;
        }
        IsoRegions.setPreviousFlags(this);
        placeWallAndDoorCheck = this.placeWallAndDoorCheck(e, placeWallAndDoorCheck);
        if (placeWallAndDoorCheck != -1 && placeWallAndDoorCheck >= 0 && placeWallAndDoorCheck <= this.Objects.size()) {
            this.Objects.add(placeWallAndDoorCheck, e);
        }
        else {
            this.Objects.add(e);
        }
        this.SpecialObjects.add(e);
        this.burntOut = false;
        e.addToWorld();
        if (!GameServer.bServer && !GameClient.bClient) {
            this.restackSheetRope();
        }
        this.RecalcAllWithNeighbours(true);
        if (!(e instanceof IsoWorldInventoryObject)) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                LosUtil.cachecleared[i] = true;
            }
            setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0f;
            if (e == this.getPlayerBuiltFloor()) {
                IsoGridOcclusionData.SquareChanged();
            }
        }
        MapCollisionData.instance.squareChanged(this);
        PolygonalMap2.instance.squareChanged(this);
        IsoRegions.squareChanged(this);
    }
    
    public void AddTileObject(final IsoObject isoObject) {
        this.AddTileObject(isoObject, -1);
    }
    
    public void AddTileObject(final IsoObject isoObject, int placeWallAndDoorCheck) {
        if (isoObject == null) {
            return;
        }
        IsoRegions.setPreviousFlags(this);
        placeWallAndDoorCheck = this.placeWallAndDoorCheck(isoObject, placeWallAndDoorCheck);
        if (placeWallAndDoorCheck != -1 && placeWallAndDoorCheck >= 0 && placeWallAndDoorCheck <= this.Objects.size()) {
            this.Objects.add(placeWallAndDoorCheck, isoObject);
        }
        else {
            this.Objects.add(isoObject);
        }
        this.burntOut = false;
        isoObject.addToWorld();
        this.RecalcAllWithNeighbours(true);
        if (!(isoObject instanceof IsoWorldInventoryObject)) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                LosUtil.cachecleared[i] = true;
            }
            setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0f;
            if (isoObject == this.getPlayerBuiltFloor()) {
                IsoGridOcclusionData.SquareChanged();
            }
        }
        MapCollisionData.instance.squareChanged(this);
        PolygonalMap2.instance.squareChanged(this);
        IsoRegions.squareChanged(this);
    }
    
    public int placeWallAndDoorCheck(final IsoObject isoObject, int n) {
        int n2 = -1;
        if (isoObject.sprite != null) {
            final IsoObjectType type = isoObject.sprite.getType();
            final boolean b = type == IsoObjectType.doorN || type == IsoObjectType.doorW;
            final boolean b2 = !b && (isoObject.sprite.cutW || isoObject.sprite.cutN || type == IsoObjectType.doorFrN || type == IsoObjectType.doorFrW || isoObject.sprite.treatAsWallOrder);
            if (b2 || b) {
                for (int i = 0; i < this.Objects.size(); ++i) {
                    final IsoObject isoObject2 = this.Objects.get(i);
                    final IsoObjectType max = IsoObjectType.MAX;
                    if (isoObject2.sprite != null) {
                        final IsoObjectType type2 = isoObject2.sprite.getType();
                        if (b2 && (type2 == IsoObjectType.doorN || type2 == IsoObjectType.doorW)) {
                            n2 = i;
                        }
                        if (b && (type2 == IsoObjectType.doorFrN || type2 == IsoObjectType.doorFrW || isoObject2.sprite.cutW || isoObject2.sprite.cutN || isoObject2.sprite.treatAsWallOrder)) {
                            n2 = i;
                        }
                    }
                }
                if (b && n2 > n) {
                    n = n2 + 1;
                    return n;
                }
                if (b2 && n2 >= 0 && (n2 < n || n < 0)) {
                    n = n2;
                    return n;
                }
            }
        }
        return n;
    }
    
    public void transmitAddObjectToSquare(final IsoObject isoObject, final int n) {
        if (isoObject == null || this.Objects.contains(isoObject)) {
            return;
        }
        this.AddTileObject(isoObject, n);
        if (GameClient.bClient) {
            isoObject.transmitCompleteItemToServer();
        }
        if (GameServer.bServer) {
            isoObject.transmitCompleteItemToClients();
        }
    }
    
    public int transmitRemoveItemFromSquare(final IsoObject isoObject) {
        if (isoObject == null || !this.Objects.contains(isoObject)) {
            return -1;
        }
        if (GameClient.bClient) {
            try {
                GameClient.instance.checkAddedRemovedItems(isoObject);
            }
            catch (Exception ex) {
                GameClient.connection.cancelPacket();
                ExceptionLogger.logException(ex);
            }
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.RemoveItemFromSquare.doPacket(startPacket);
            startPacket.putInt(this.getX());
            startPacket.putInt(this.getY());
            startPacket.putInt(this.getZ());
            startPacket.putInt(this.Objects.indexOf(isoObject));
            PacketTypes.PacketType.RemoveItemFromSquare.send(GameClient.connection);
        }
        if (GameServer.bServer) {
            return GameServer.RemoveItemFromMap(isoObject);
        }
        return this.RemoveTileObject(isoObject);
    }
    
    public void transmitRemoveItemFromSquareOnServer(final IsoObject isoObject) {
        if (isoObject == null || !this.Objects.contains(isoObject)) {
            return;
        }
        if (GameServer.bServer) {
            GameServer.RemoveItemFromMap(isoObject);
        }
    }
    
    public void transmitModdata() {
        if (GameClient.bClient) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.SendModData.doPacket(startPacket);
            startPacket.putInt(this.getX());
            startPacket.putInt(this.getY());
            startPacket.putInt(this.getZ());
            try {
                this.getModData().save(startPacket.bb);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            PacketTypes.PacketType.SendModData.send(GameClient.connection);
        }
        else if (GameServer.bServer) {
            GameServer.loadModData(this);
        }
    }
    
    public void AddWorldInventoryItem(final String s, final float n, final float n2, final float n3, final int n4) {
        for (int i = 0; i < n4; ++i) {
            this.AddWorldInventoryItem(s, n, n2, n3);
        }
    }
    
    public InventoryItem AddWorldInventoryItem(final String s, final float n, final float n2, final float n3) {
        final InventoryItem createItem = InventoryItemFactory.CreateItem(s);
        if (createItem == null) {
            return null;
        }
        final IsoWorldInventoryObject isoWorldInventoryObject = new IsoWorldInventoryObject(createItem, this, n, n2, n3);
        createItem.setWorldItem(isoWorldInventoryObject);
        isoWorldInventoryObject.setKeyId(createItem.getKeyId());
        isoWorldInventoryObject.setName(createItem.getName());
        this.Objects.add(isoWorldInventoryObject);
        this.WorldObjects.add(isoWorldInventoryObject);
        isoWorldInventoryObject.square.chunk.recalcHashCodeObjects();
        if (GameClient.bClient) {
            isoWorldInventoryObject.transmitCompleteItemToServer();
        }
        if (GameServer.bServer) {
            isoWorldInventoryObject.transmitCompleteItemToClients();
        }
        return createItem;
    }
    
    public InventoryItem AddWorldInventoryItem(final InventoryItem inventoryItem, final float n, final float n2, final float n3) {
        return this.AddWorldInventoryItem(inventoryItem, n, n2, n3, true);
    }
    
    public InventoryItem AddWorldInventoryItem(final InventoryItem inventoryItem, final float n, final float n2, final float n3, final boolean b) {
        if (inventoryItem.getFullType().contains(".Corpse")) {
            if (inventoryItem.byteData == null) {
                final IsoZombie isoZombie = new IsoZombie(IsoWorld.instance.CurrentCell);
                isoZombie.setDir(IsoDirections.fromIndex(Rand.Next(8)));
                isoZombie.getForwardDirection().set(isoZombie.dir.ToVector());
                isoZombie.setFakeDead(false);
                isoZombie.setHealth(0.0f);
                isoZombie.upKillCount = false;
                isoZombie.setX(this.x + n);
                isoZombie.setY(this.y + n2);
                isoZombie.setZ((float)this.z);
                isoZombie.square = this;
                isoZombie.current = this;
                isoZombie.dressInRandomOutfit();
                isoZombie.DoZombieInventory();
                final IsoDeadBody isoDeadBody = new IsoDeadBody(isoZombie, true);
                this.addCorpse(isoDeadBody, false);
                if (GameServer.bServer) {
                    GameServer.sendCorpse(isoDeadBody);
                }
                return inventoryItem;
            }
            IsoDeadBody isoDeadBody2 = new IsoDeadBody(IsoWorld.instance.CurrentCell);
            try {
                final byte value = inventoryItem.byteData.get();
                final byte value2 = inventoryItem.byteData.get();
                final byte value3 = inventoryItem.byteData.get();
                final byte value4 = inventoryItem.byteData.get();
                int int1 = 56;
                if (value == 87 && value2 == 86 && value3 == 69 && value4 == 82) {
                    int1 = inventoryItem.byteData.getInt();
                }
                else {
                    inventoryItem.byteData.rewind();
                }
                isoDeadBody2.load(inventoryItem.byteData, int1);
            }
            catch (IOException ex) {
                ex.printStackTrace();
                final IsoZombie isoZombie2 = new IsoZombie(null);
                isoZombie2.dir = isoDeadBody2.dir;
                isoZombie2.current = this;
                isoZombie2.x = isoDeadBody2.x;
                isoZombie2.y = isoDeadBody2.y;
                isoZombie2.z = isoDeadBody2.z;
                isoDeadBody2 = new IsoDeadBody(isoZombie2);
            }
            isoDeadBody2.setX(this.x + n);
            isoDeadBody2.setY(this.y + n2);
            isoDeadBody2.setZ((float)this.z);
            (isoDeadBody2.square = this).addCorpse(isoDeadBody2, false);
            if (GameServer.bServer) {
                GameServer.sendCorpse(isoDeadBody2);
            }
            return inventoryItem;
        }
        else {
            if (inventoryItem.getFullType().contains(".Generator")) {
                new IsoGenerator(inventoryItem, IsoWorld.instance.CurrentCell, this);
                return inventoryItem;
            }
            final IsoWorldInventoryObject isoWorldInventoryObject = new IsoWorldInventoryObject(inventoryItem, this, n, n2, n3);
            isoWorldInventoryObject.setName(inventoryItem.getName());
            isoWorldInventoryObject.setKeyId(inventoryItem.getKeyId());
            this.Objects.add(isoWorldInventoryObject);
            this.WorldObjects.add(isoWorldInventoryObject);
            isoWorldInventoryObject.square.chunk.recalcHashCodeObjects();
            inventoryItem.setWorldItem(isoWorldInventoryObject);
            isoWorldInventoryObject.addToWorld();
            if (b) {
                if (GameClient.bClient) {
                    isoWorldInventoryObject.transmitCompleteItemToServer();
                }
                if (GameServer.bServer) {
                    isoWorldInventoryObject.transmitCompleteItemToClients();
                }
            }
            return inventoryItem;
        }
    }
    
    public void restackSheetRope() {
        if (!this.Is(IsoFlagType.climbSheetW) && !this.Is(IsoFlagType.climbSheetN) && !this.Is(IsoFlagType.climbSheetE) && !this.Is(IsoFlagType.climbSheetS)) {
            return;
        }
        for (int i = 0; i < this.getObjects().size() - 1; ++i) {
            final IsoObject isoObject = this.getObjects().get(i);
            if (isoObject.getProperties() != null) {
                if (isoObject.getProperties().Is(IsoFlagType.climbSheetW) || isoObject.getProperties().Is(IsoFlagType.climbSheetN) || isoObject.getProperties().Is(IsoFlagType.climbSheetE) || isoObject.getProperties().Is(IsoFlagType.climbSheetS)) {
                    if (GameServer.bServer) {
                        this.transmitRemoveItemFromSquare(isoObject);
                        this.Objects.add(isoObject);
                        isoObject.transmitCompleteItemToClients();
                        break;
                    }
                    if (GameClient.bClient) {
                        break;
                    }
                    this.Objects.remove(isoObject);
                    this.Objects.add(isoObject);
                    break;
                }
            }
        }
    }
    
    public void Burn() {
        if ((GameServer.bServer || GameClient.bClient) && ServerOptions.instance.NoFire.getValue()) {
            return;
        }
        if (this.getCell() == null) {
            return;
        }
        this.BurnWalls(true);
        LuaEventManager.triggerEvent("OnGridBurnt", this);
    }
    
    public void Burn(final boolean b) {
        if ((GameServer.bServer || GameClient.bClient) && ServerOptions.instance.NoFire.getValue()) {
            return;
        }
        if (this.getCell() == null) {
            return;
        }
        this.BurnWalls(b);
    }
    
    public void BurnWalls(final boolean b) {
        if (GameClient.bClient) {
            return;
        }
        if (GameServer.bServer && SafeHouse.isSafeHouse(this, null, false) != null) {
            if (ServerOptions.instance.NoFire.getValue()) {
                return;
            }
            if (!ServerOptions.instance.SafehouseAllowFire.getValue()) {
                return;
            }
        }
        for (int i = 0; i < this.SpecialObjects.size(); ++i) {
            final IsoObject isoObject = this.SpecialObjects.get(i);
            if (isoObject instanceof IsoThumpable && ((IsoThumpable)isoObject).haveSheetRope()) {
                ((IsoThumpable)isoObject).removeSheetRope(null);
            }
            if (isoObject instanceof IsoWindow) {
                if (((IsoWindow)isoObject).haveSheetRope()) {
                    ((IsoWindow)isoObject).removeSheetRope(null);
                }
                ((IsoWindow)isoObject).removeSheet(null);
            }
            if (IsoWindowFrame.isWindowFrame(isoObject) && IsoWindowFrame.haveSheetRope(isoObject)) {
                IsoWindowFrame.removeSheetRope(isoObject, null);
            }
            if (isoObject instanceof BarricadeAble) {
                final IsoBarricade barricadeOnSameSquare = ((IsoWindow)isoObject).getBarricadeOnSameSquare();
                final IsoBarricade barricadeOnOppositeSquare = ((IsoWindow)isoObject).getBarricadeOnOppositeSquare();
                if (barricadeOnSameSquare != null) {
                    if (GameServer.bServer) {
                        GameServer.RemoveItemFromMap(barricadeOnSameSquare);
                    }
                    else {
                        this.RemoveTileObject(barricadeOnSameSquare);
                    }
                }
                if (barricadeOnOppositeSquare != null) {
                    if (GameServer.bServer) {
                        GameServer.RemoveItemFromMap(barricadeOnOppositeSquare);
                    }
                    else {
                        barricadeOnOppositeSquare.getSquare().RemoveTileObject(barricadeOnOppositeSquare);
                    }
                }
            }
        }
        this.SpecialObjects.clear();
        boolean b2 = false;
        if (!this.getProperties().Is(IsoFlagType.burntOut)) {
            int n = 0;
            for (int j = 0; j < this.Objects.size(); ++j) {
                final IsoObject isoObject2 = this.Objects.get(j);
                boolean b3 = false;
                if (isoObject2.getSprite() != null) {
                    if (isoObject2.getSprite().getName() != null) {
                        if (!isoObject2.getSprite().getProperties().Is(IsoFlagType.water)) {
                            if (!isoObject2.getSprite().getName().contains("_burnt_")) {
                                if (isoObject2 instanceof IsoThumpable && isoObject2.getSprite().burntTile != null) {
                                    final IsoObject new1 = IsoObject.getNew();
                                    new1.setSprite(IsoSpriteManager.instance.getSprite(isoObject2.getSprite().burntTile));
                                    new1.setSquare(this);
                                    if (GameServer.bServer) {
                                        isoObject2.sendObjectChange("replaceWith", "object", new1);
                                    }
                                    isoObject2.removeFromWorld();
                                    this.Objects.set(j, new1);
                                }
                                else if (isoObject2.getSprite().burntTile != null) {
                                    isoObject2.sprite = IsoSpriteManager.instance.getSprite(isoObject2.getSprite().burntTile);
                                    isoObject2.RemoveAttachedAnims();
                                    if (isoObject2.Children != null) {
                                        isoObject2.Children.clear();
                                    }
                                    isoObject2.transmitUpdatedSpriteToClients();
                                    isoObject2.setOverlaySprite(null);
                                }
                                else if (isoObject2.getType() == IsoObjectType.tree) {
                                    isoObject2.sprite = IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(15, 19) + 1));
                                    isoObject2.RemoveAttachedAnims();
                                    if (isoObject2.Children != null) {
                                        isoObject2.Children.clear();
                                    }
                                    isoObject2.transmitUpdatedSpriteToClients();
                                    isoObject2.setOverlaySprite(null);
                                }
                                else if (!(isoObject2 instanceof IsoTrap)) {
                                    if (isoObject2 instanceof IsoBarricade || isoObject2 instanceof IsoMannequin) {
                                        if (GameServer.bServer) {
                                            GameServer.RemoveItemFromMap(isoObject2);
                                        }
                                        else {
                                            this.Objects.remove(isoObject2);
                                        }
                                        --j;
                                    }
                                    else if (isoObject2 instanceof IsoGenerator) {
                                        final IsoGenerator isoGenerator = (IsoGenerator)isoObject2;
                                        if (isoGenerator.getFuel() > 0.0f) {
                                            n += 20;
                                        }
                                        if (isoGenerator.isActivated()) {
                                            isoGenerator.activated = false;
                                            isoGenerator.setSurroundingElectricity();
                                            if (GameServer.bServer) {
                                                isoGenerator.syncIsoObject(false, (byte)0, null, null);
                                            }
                                        }
                                        if (GameServer.bServer) {
                                            GameServer.RemoveItemFromMap(isoObject2);
                                        }
                                        else {
                                            this.RemoveTileObject(isoObject2);
                                        }
                                        --j;
                                    }
                                    else {
                                        if (isoObject2.getType() == IsoObjectType.wall && !isoObject2.getProperties().Is(IsoFlagType.DoorWallW) && !isoObject2.getProperties().Is(IsoFlagType.DoorWallN) && !isoObject2.getProperties().Is("WindowN") && !isoObject2.getProperties().Is(IsoFlagType.WindowW) && !isoObject2.getSprite().getName().startsWith("walls_exterior_roofs_") && !isoObject2.getSprite().getName().startsWith("fencing_") && !isoObject2.getSprite().getName().startsWith("fixtures_railings_")) {
                                            if (isoObject2.getSprite().getProperties().Is(IsoFlagType.collideW) && !isoObject2.getSprite().getProperties().Is(IsoFlagType.collideN)) {
                                                isoObject2.sprite = IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (Rand.Next(2) == 0) ? "0" : "4"));
                                            }
                                            else if (isoObject2.getSprite().getProperties().Is(IsoFlagType.collideN) && !isoObject2.getSprite().getProperties().Is(IsoFlagType.collideW)) {
                                                isoObject2.sprite = IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (Rand.Next(2) == 0) ? "1" : "5"));
                                            }
                                            else if (isoObject2.getSprite().getProperties().Is(IsoFlagType.collideW) && isoObject2.getSprite().getProperties().Is(IsoFlagType.collideN)) {
                                                isoObject2.sprite = IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (Rand.Next(2) == 0) ? "2" : "6"));
                                            }
                                            else if (isoObject2.getProperties().Is(IsoFlagType.WallSE)) {
                                                isoObject2.sprite = IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (Rand.Next(2) == 0) ? "3" : "7"));
                                            }
                                        }
                                        else {
                                            if (isoObject2 instanceof IsoDoor || isoObject2 instanceof IsoWindow || isoObject2 instanceof IsoCurtain) {
                                                if (GameServer.bServer) {
                                                    GameServer.RemoveItemFromMap(isoObject2);
                                                }
                                                else {
                                                    this.RemoveTileObject(isoObject2);
                                                    b2 = true;
                                                }
                                                --j;
                                                continue;
                                            }
                                            if (isoObject2.getProperties().Is(IsoFlagType.WindowW)) {
                                                isoObject2.sprite = IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (Rand.Next(2) == 0) ? "8" : "12"));
                                            }
                                            else if (isoObject2.getProperties().Is("WindowN")) {
                                                isoObject2.sprite = IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (Rand.Next(2) == 0) ? "9" : "13"));
                                            }
                                            else if (isoObject2.getProperties().Is(IsoFlagType.DoorWallW)) {
                                                isoObject2.sprite = IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (Rand.Next(2) == 0) ? "10" : "14"));
                                            }
                                            else if (isoObject2.getProperties().Is(IsoFlagType.DoorWallN)) {
                                                isoObject2.sprite = IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (Rand.Next(2) == 0) ? "11" : "15"));
                                            }
                                            else if (isoObject2.getSprite().getProperties().Is(IsoFlagType.solidfloor) && !isoObject2.getSprite().getProperties().Is(IsoFlagType.exterior)) {
                                                isoObject2.sprite = IsoSpriteManager.instance.getSprite("floors_burnt_01_0");
                                            }
                                            else {
                                                if (isoObject2 instanceof IsoWaveSignal) {
                                                    if (GameServer.bServer) {
                                                        GameServer.RemoveItemFromMap(isoObject2);
                                                    }
                                                    else {
                                                        this.RemoveTileObject(isoObject2);
                                                        b2 = true;
                                                    }
                                                    --j;
                                                    continue;
                                                }
                                                if (isoObject2.getContainer() != null && isoObject2.getContainer().getItems() != null) {
                                                    for (int k = 0; k < isoObject2.getContainer().getItems().size(); ++k) {
                                                        final InventoryItem inventoryItem = isoObject2.getContainer().getItems().get(k);
                                                        if ((inventoryItem instanceof Food && ((Food)inventoryItem).isAlcoholic()) || inventoryItem.getType().equals("PetrolCan") || inventoryItem.getType().equals("Bleach")) {
                                                            n += 20;
                                                            if (n > 100) {
                                                                n = 100;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    isoObject2.sprite = IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(1, 2)));
                                                    for (int l = 0; l < isoObject2.getContainerCount(); ++l) {
                                                        final ItemContainer containerByIndex = isoObject2.getContainerByIndex(l);
                                                        containerByIndex.removeItemsFromProcessItems();
                                                        containerByIndex.removeAllItems();
                                                    }
                                                    isoObject2.removeAllContainers();
                                                    if (isoObject2.getOverlaySprite() != null) {
                                                        isoObject2.setOverlaySprite(null);
                                                    }
                                                    b3 = true;
                                                }
                                                else if (isoObject2.getSprite().getProperties().Is(IsoFlagType.solidtrans) || isoObject2.getSprite().getProperties().Is(IsoFlagType.bed) || isoObject2.getSprite().getProperties().Is(IsoFlagType.waterPiped)) {
                                                    isoObject2.sprite = IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(1, 2)));
                                                    if (isoObject2.getOverlaySprite() != null) {
                                                        isoObject2.setOverlaySprite(null);
                                                    }
                                                }
                                                else if (isoObject2.getSprite().getName().startsWith("walls_exterior_roofs_")) {
                                                    isoObject2.sprite = IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, isoObject2.getSprite().getName().substring(isoObject2.getSprite().getName().lastIndexOf("_") + 1, isoObject2.getSprite().getName().length())));
                                                }
                                                else if (!isoObject2.getSprite().getName().startsWith("roofs_accents")) {
                                                    if (isoObject2.getSprite().getName().startsWith("roofs_")) {
                                                        isoObject2.sprite = IsoSpriteManager.instance.getSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, isoObject2.getSprite().getName().substring(isoObject2.getSprite().getName().lastIndexOf("_") + 1, isoObject2.getSprite().getName().length())));
                                                    }
                                                    else if ((isoObject2.getSprite().getName().startsWith("fencing_") || isoObject2.getSprite().getName().startsWith("fixtures_railings_")) && (isoObject2.getSprite().getProperties().Is(IsoFlagType.HoppableN) || isoObject2.getSprite().getProperties().Is(IsoFlagType.HoppableW))) {
                                                        if (isoObject2.getSprite().getProperties().Is(IsoFlagType.transparentW) && !isoObject2.getSprite().getProperties().Is(IsoFlagType.transparentN)) {
                                                            isoObject2.sprite = IsoSpriteManager.instance.getSprite("fencing_burnt_01_0");
                                                        }
                                                        else if (isoObject2.getSprite().getProperties().Is(IsoFlagType.transparentN) && !isoObject2.getSprite().getProperties().Is(IsoFlagType.transparentW)) {
                                                            isoObject2.sprite = IsoSpriteManager.instance.getSprite("fencing_burnt_01_1");
                                                        }
                                                        else {
                                                            isoObject2.sprite = IsoSpriteManager.instance.getSprite("fencing_burnt_01_2");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (b3 || isoObject2 instanceof IsoThumpable) {
                                            final IsoObject new2 = IsoObject.getNew();
                                            new2.setSprite(isoObject2.getSprite());
                                            new2.setSquare(this);
                                            if (GameServer.bServer) {
                                                isoObject2.sendObjectChange("replaceWith", "object", new2);
                                            }
                                            this.Objects.set(j, new2);
                                        }
                                        else {
                                            isoObject2.RemoveAttachedAnims();
                                            isoObject2.transmitUpdatedSpriteToClients();
                                            isoObject2.setOverlaySprite(null);
                                        }
                                        if (isoObject2.emitter != null) {
                                            isoObject2.emitter.stopAll();
                                            isoObject2.emitter = null;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (n > 0 && b) {
                if (GameServer.bServer) {
                    GameServer.PlayWorldSoundServer("BurnedObjectExploded", false, this, 0.0f, 50.0f, 1.0f, false);
                }
                else {
                    SoundManager.instance.PlayWorldSound("BurnedObjectExploded", this, 0.0f, 50.0f, 1.0f, false);
                }
                IsoFireManager.explode(this.getCell(), this, n);
            }
        }
        if (!b2) {
            this.RecalcProperties();
        }
        this.getProperties().Set(IsoFlagType.burntOut);
        this.burntOut = true;
        MapCollisionData.instance.squareChanged(this);
        PolygonalMap2.instance.squareChanged(this);
    }
    
    public void BurnWallsTCOnly() {
        for (int i = 0; i < this.Objects.size(); ++i) {
            if (this.Objects.get(i).sprite == null) {}
        }
    }
    
    public void BurnTick() {
        if (GameClient.bClient) {
            return;
        }
        for (int i = 0; i < this.StaticMovingObjects.size(); ++i) {
            final IsoMovingObject o = this.StaticMovingObjects.get(i);
            if (o instanceof IsoDeadBody) {
                ((IsoDeadBody)o).Burn();
                if (!this.StaticMovingObjects.contains(o)) {
                    --i;
                }
            }
        }
    }
    
    public boolean CalculateCollide(final IsoGridSquare isoGridSquare, final boolean b, final boolean b2, final boolean b3) {
        return this.CalculateCollide(isoGridSquare, b, b2, b3, false);
    }
    
    public boolean CalculateCollide(final IsoGridSquare isoGridSquare, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        return this.CalculateCollide(isoGridSquare, b, b2, b3, b4, IsoGridSquare.cellGetSquare);
    }
    
    public boolean CalculateCollide(final IsoGridSquare isoGridSquare, final boolean b, final boolean b2, final boolean b3, final boolean b4, final GetSquare getSquare) {
        if (isoGridSquare == null && b2) {
            return true;
        }
        if (isoGridSquare == null) {
            return false;
        }
        if (!b || isoGridSquare.Properties.Is(IsoFlagType.trans)) {}
        boolean b5 = false;
        boolean b6 = false;
        boolean b7 = false;
        boolean b8 = false;
        if (isoGridSquare.x < this.x) {
            b5 = true;
        }
        if (isoGridSquare.y < this.y) {
            b7 = true;
        }
        if (isoGridSquare.x > this.x) {
            b6 = true;
        }
        if (isoGridSquare.y > this.y) {
            b8 = true;
        }
        if (!b4 && isoGridSquare.Properties.Is(IsoFlagType.solid)) {
            return (!this.Has(IsoObjectType.stairsTW) || b2 || isoGridSquare.x >= this.x || isoGridSquare.y != this.y || isoGridSquare.z != this.z) && (!this.Has(IsoObjectType.stairsTN) || b2 || isoGridSquare.x != this.x || isoGridSquare.y >= this.y || isoGridSquare.z != this.z);
        }
        if (!b3 && isoGridSquare.Properties.Is(IsoFlagType.solidtrans)) {
            if (this.Has(IsoObjectType.stairsTW) && !b2 && isoGridSquare.x < this.x && isoGridSquare.y == this.y && isoGridSquare.z == this.z) {
                return false;
            }
            if (this.Has(IsoObjectType.stairsTN) && !b2 && isoGridSquare.x == this.x && isoGridSquare.y < this.y && isoGridSquare.z == this.z) {
                return false;
            }
            int n = 0;
            if (isoGridSquare.Properties.Is(IsoFlagType.windowW) || isoGridSquare.Properties.Is(IsoFlagType.windowN)) {
                n = 1;
            }
            if (n == 0 && (isoGridSquare.Properties.Is(IsoFlagType.WindowW) || isoGridSquare.Properties.Is(IsoFlagType.WindowN))) {
                n = 1;
            }
            if (n == 0) {
                final IsoGridSquare gridSquare = getSquare.getGridSquare(isoGridSquare.x, isoGridSquare.y + 1, this.z);
                if (gridSquare != null && (gridSquare.Is(IsoFlagType.windowN) || gridSquare.Is(IsoFlagType.WindowN))) {
                    n = 1;
                }
            }
            if (n == 0) {
                final IsoGridSquare gridSquare2 = getSquare.getGridSquare(isoGridSquare.x + 1, isoGridSquare.y, this.z);
                if (gridSquare2 != null && (gridSquare2.Is(IsoFlagType.windowW) || gridSquare2.Is(IsoFlagType.WindowW))) {
                    n = 1;
                }
            }
            if (n == 0) {
                return true;
            }
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y && this.z != isoGridSquare.z && b2) {
            return true;
        }
        Label_0626: {
            if (b2 && isoGridSquare.z < this.z) {
                if (this.SolidFloorCached) {
                    if (this.SolidFloor) {
                        break Label_0626;
                    }
                }
                else if (this.TreatAsSolidFloor()) {
                    break Label_0626;
                }
                return isoGridSquare.Has(IsoObjectType.stairsTN) || isoGridSquare.Has(IsoObjectType.stairsTW);
            }
        }
        if (b2 && isoGridSquare.z == this.z) {
            if (isoGridSquare.x > this.x && isoGridSquare.y == this.y && isoGridSquare.Properties.Is(IsoFlagType.windowW)) {
                return false;
            }
            if (isoGridSquare.y > this.y && isoGridSquare.x == this.x && isoGridSquare.Properties.Is(IsoFlagType.windowN)) {
                return false;
            }
            if (isoGridSquare.x < this.x && isoGridSquare.y == this.y && this.Properties.Is(IsoFlagType.windowW)) {
                return false;
            }
            if (isoGridSquare.y < this.y && isoGridSquare.x == this.x && this.Properties.Is(IsoFlagType.windowN)) {
                return false;
            }
        }
        if (isoGridSquare.x > this.x && isoGridSquare.z < this.z && isoGridSquare.Has(IsoObjectType.stairsTW)) {
            return false;
        }
        if (isoGridSquare.y > this.y && isoGridSquare.z < this.z && isoGridSquare.Has(IsoObjectType.stairsTN)) {
            return false;
        }
        final IsoGridSquare gridSquare3 = getSquare.getGridSquare(isoGridSquare.x, isoGridSquare.y, isoGridSquare.z - 1);
        if (isoGridSquare.x != this.x && isoGridSquare.z == this.z && isoGridSquare.Has(IsoObjectType.stairsTN) && (gridSquare3 == null || !gridSquare3.Has(IsoObjectType.stairsTN) || b2)) {
            return true;
        }
        if (isoGridSquare.y > this.y && isoGridSquare.x == this.x && isoGridSquare.z == this.z && isoGridSquare.Has(IsoObjectType.stairsTN) && (gridSquare3 == null || !gridSquare3.Has(IsoObjectType.stairsTN) || b2)) {
            return true;
        }
        if (isoGridSquare.x > this.x && isoGridSquare.y == this.y && isoGridSquare.z == this.z && isoGridSquare.Has(IsoObjectType.stairsTW) && (gridSquare3 == null || !gridSquare3.Has(IsoObjectType.stairsTW) || b2)) {
            return true;
        }
        if (isoGridSquare.y != this.y && isoGridSquare.z == this.z && isoGridSquare.Has(IsoObjectType.stairsTW) && (gridSquare3 == null || !gridSquare3.Has(IsoObjectType.stairsTW) || b2)) {
            return true;
        }
        if (isoGridSquare.x != this.x && isoGridSquare.z == this.z && isoGridSquare.Has(IsoObjectType.stairsMN)) {
            return true;
        }
        if (isoGridSquare.y != this.y && isoGridSquare.z == this.z && isoGridSquare.Has(IsoObjectType.stairsMW)) {
            return true;
        }
        if (isoGridSquare.x != this.x && isoGridSquare.z == this.z && isoGridSquare.Has(IsoObjectType.stairsBN)) {
            return true;
        }
        if (isoGridSquare.y != this.y && isoGridSquare.z == this.z && isoGridSquare.Has(IsoObjectType.stairsBW)) {
            return true;
        }
        if (isoGridSquare.x != this.x && isoGridSquare.z == this.z && this.Has(IsoObjectType.stairsTN)) {
            return true;
        }
        if (isoGridSquare.y != this.y && isoGridSquare.z == this.z && this.Has(IsoObjectType.stairsTW)) {
            return true;
        }
        if (isoGridSquare.x != this.x && isoGridSquare.z == this.z && this.Has(IsoObjectType.stairsMN)) {
            return true;
        }
        if (isoGridSquare.y != this.y && isoGridSquare.z == this.z && this.Has(IsoObjectType.stairsMW)) {
            return true;
        }
        if (isoGridSquare.x != this.x && isoGridSquare.z == this.z && this.Has(IsoObjectType.stairsBN)) {
            return true;
        }
        if (isoGridSquare.y != this.y && isoGridSquare.z == this.z && this.Has(IsoObjectType.stairsBW)) {
            return true;
        }
        if (isoGridSquare.y < this.y && isoGridSquare.x == this.x && isoGridSquare.z > this.z && this.Has(IsoObjectType.stairsTN)) {
            return false;
        }
        if (isoGridSquare.x < this.x && isoGridSquare.y == this.y && isoGridSquare.z > this.z && this.Has(IsoObjectType.stairsTW)) {
            return false;
        }
        if (isoGridSquare.y > this.y && isoGridSquare.x == this.x && isoGridSquare.z < this.z && isoGridSquare.Has(IsoObjectType.stairsTN)) {
            return false;
        }
        if (isoGridSquare.x > this.x && isoGridSquare.y == this.y && isoGridSquare.z < this.z && isoGridSquare.Has(IsoObjectType.stairsTW)) {
            return false;
        }
        Label_1679: {
            if (isoGridSquare.z == this.z) {
                if (isoGridSquare.SolidFloorCached) {
                    if (isoGridSquare.SolidFloor) {
                        break Label_1679;
                    }
                }
                else if (isoGridSquare.TreatAsSolidFloor()) {
                    break Label_1679;
                }
                if (b2) {
                    return true;
                }
            }
        }
        Label_1751: {
            if (isoGridSquare.z == this.z) {
                if (isoGridSquare.SolidFloorCached) {
                    if (isoGridSquare.SolidFloor) {
                        break Label_1751;
                    }
                }
                else if (isoGridSquare.TreatAsSolidFloor()) {
                    break Label_1751;
                }
                if (isoGridSquare.z > 0 && getSquare.getGridSquare(isoGridSquare.x, isoGridSquare.y, isoGridSquare.z - 1) == null) {
                    return true;
                }
            }
        }
        if (this.z != isoGridSquare.z) {
            if (isoGridSquare.z < this.z && isoGridSquare.x == this.x && isoGridSquare.y == this.y) {
                if (this.SolidFloorCached) {
                    if (this.SolidFloor) {
                        return true;
                    }
                }
                else if (this.TreatAsSolidFloor()) {
                    return true;
                }
                return false;
            }
            return true;
        }
        int n2 = (b7 && this.Properties.Is(IsoFlagType.collideN)) ? 1 : 0;
        int n3 = (b5 && this.Properties.Is(IsoFlagType.collideW)) ? 1 : 0;
        int n4 = (b8 && isoGridSquare.Properties.Is(IsoFlagType.collideN)) ? 1 : 0;
        int n5 = (b6 && isoGridSquare.Properties.Is(IsoFlagType.collideW)) ? 1 : 0;
        if (n2 != 0 && b2 && this.Properties.Is(IsoFlagType.canPathN)) {
            n2 = 0;
        }
        if (n3 != 0 && b2 && this.Properties.Is(IsoFlagType.canPathW)) {
            n3 = 0;
        }
        if (n4 != 0 && b2 && isoGridSquare.Properties.Is(IsoFlagType.canPathN)) {
            n4 = 0;
        }
        if (n5 != 0 && b2 && isoGridSquare.Properties.Is(IsoFlagType.canPathW)) {
            n5 = 0;
        }
        if (n3 != 0 && this.Has(IsoObjectType.stairsTW) && !b2) {
            n3 = 0;
        }
        if (n2 != 0 && this.Has(IsoObjectType.stairsTN) && !b2) {
            n2 = 0;
        }
        if (n2 != 0 || n3 != 0 || n4 != 0 || n5 != 0) {
            return true;
        }
        if (isoGridSquare.x != this.x && isoGridSquare.y != this.y) {
            final IsoGridSquare gridSquare4 = getSquare.getGridSquare(this.x, isoGridSquare.y, this.z);
            final IsoGridSquare gridSquare5 = getSquare.getGridSquare(isoGridSquare.x, this.y, this.z);
            if (gridSquare4 != null && gridSquare4 != this && gridSquare4 != isoGridSquare) {
                gridSquare4.RecalcPropertiesIfNeeded();
            }
            if (gridSquare5 != null && gridSquare5 != this && gridSquare5 != isoGridSquare) {
                gridSquare5.RecalcPropertiesIfNeeded();
            }
            if (isoGridSquare == this || gridSquare4 == gridSquare5 || gridSquare4 == this || gridSquare5 == this || gridSquare4 == isoGridSquare || gridSquare5 == isoGridSquare) {
                return true;
            }
            if (isoGridSquare.x == this.x + 1 && isoGridSquare.y == this.y + 1 && gridSquare4 != null && gridSquare5 != null && gridSquare4.Is(IsoFlagType.windowN) && gridSquare5.Is(IsoFlagType.windowW)) {
                return true;
            }
            if (isoGridSquare.x == this.x - 1 && isoGridSquare.y == this.y - 1 && gridSquare4 != null && gridSquare5 != null && gridSquare4.Is(IsoFlagType.windowW) && gridSquare5.Is(IsoFlagType.windowN)) {
                return true;
            }
            if (this.CalculateCollide(gridSquare4, b, b2, b3, false, getSquare)) {
                return true;
            }
            if (this.CalculateCollide(gridSquare5, b, b2, b3, false, getSquare)) {
                return true;
            }
            if (isoGridSquare.CalculateCollide(gridSquare4, b, b2, b3, false, getSquare)) {
                return true;
            }
            if (isoGridSquare.CalculateCollide(gridSquare5, b, b2, b3, false, getSquare)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean CalculateVisionBlocked(final IsoGridSquare isoGridSquare) {
        return this.CalculateVisionBlocked(isoGridSquare, IsoGridSquare.cellGetSquare);
    }
    
    public boolean CalculateVisionBlocked(final IsoGridSquare isoGridSquare, final GetSquare getSquare) {
        if (isoGridSquare == null) {
            return false;
        }
        if (Math.abs(isoGridSquare.getX() - this.getX()) > 1 || Math.abs(isoGridSquare.getY() - this.getY()) > 1) {
            return true;
        }
        boolean b = false;
        boolean b2 = false;
        boolean b3 = false;
        boolean b4 = false;
        if (isoGridSquare.x < this.x) {
            b = true;
        }
        if (isoGridSquare.y < this.y) {
            b3 = true;
        }
        if (isoGridSquare.x > this.x) {
            b2 = true;
        }
        if (isoGridSquare.y > this.y) {
            b4 = true;
        }
        if (isoGridSquare.Properties.Is(IsoFlagType.trans) || this.Properties.Is(IsoFlagType.trans)) {
            return false;
        }
        Label_0407: {
            if (this.z != isoGridSquare.z) {
                if (isoGridSquare.z > this.z) {
                    Label_0195: {
                        if (isoGridSquare.SolidFloorCached) {
                            if (!isoGridSquare.SolidFloor) {
                                break Label_0195;
                            }
                        }
                        else if (!isoGridSquare.TreatAsSolidFloor()) {
                            break Label_0195;
                        }
                        if (!isoGridSquare.getProperties().Is(IsoFlagType.transparentFloor)) {
                            return true;
                        }
                    }
                    if (this.Properties.Is(IsoFlagType.noStart)) {
                        return true;
                    }
                    final IsoGridSquare gridSquare = getSquare.getGridSquare(this.x, this.y, isoGridSquare.z);
                    if (gridSquare == null) {
                        return false;
                    }
                    Label_0280: {
                        if (gridSquare.SolidFloorCached) {
                            if (!gridSquare.SolidFloor) {
                                break Label_0280;
                            }
                        }
                        else if (!gridSquare.TreatAsSolidFloor()) {
                            break Label_0280;
                        }
                        if (!gridSquare.getProperties().Is(IsoFlagType.transparentFloor)) {
                            return true;
                        }
                    }
                }
                else {
                    Label_0322: {
                        if (this.SolidFloorCached) {
                            if (!this.SolidFloor) {
                                break Label_0322;
                            }
                        }
                        else if (!this.TreatAsSolidFloor()) {
                            break Label_0322;
                        }
                        if (!this.getProperties().Is(IsoFlagType.transparentFloor)) {
                            return true;
                        }
                    }
                    if (this.Properties.Is(IsoFlagType.noStart)) {
                        return true;
                    }
                    final IsoGridSquare gridSquare2 = getSquare.getGridSquare(isoGridSquare.x, isoGridSquare.y, this.z);
                    if (gridSquare2 == null) {
                        return false;
                    }
                    if (gridSquare2.SolidFloorCached) {
                        if (!gridSquare2.SolidFloor) {
                            break Label_0407;
                        }
                    }
                    else if (!gridSquare2.TreatAsSolidFloor()) {
                        break Label_0407;
                    }
                    if (!gridSquare2.getProperties().Is(IsoFlagType.transparentFloor)) {
                        return true;
                    }
                }
            }
        }
        if (isoGridSquare.x > this.x && isoGridSquare.Properties.Is(IsoFlagType.transparentW)) {
            return false;
        }
        if (isoGridSquare.y > this.y && isoGridSquare.Properties.Is(IsoFlagType.transparentN)) {
            return false;
        }
        if (isoGridSquare.x < this.x && this.Properties.Is(IsoFlagType.transparentW)) {
            return false;
        }
        if (isoGridSquare.y < this.y && this.Properties.Is(IsoFlagType.transparentN)) {
            return false;
        }
        if (isoGridSquare.x > this.x && isoGridSquare.Properties.Is(IsoFlagType.doorW)) {
            return false;
        }
        if (isoGridSquare.y > this.y && isoGridSquare.Properties.Is(IsoFlagType.doorN)) {
            return false;
        }
        if (isoGridSquare.x < this.x && this.Properties.Is(IsoFlagType.doorW)) {
            return false;
        }
        if (isoGridSquare.y < this.y && this.Properties.Is(IsoFlagType.doorN)) {
            return false;
        }
        final boolean b5 = b3 && this.Properties.Is(IsoFlagType.collideN);
        final boolean b6 = b && this.Properties.Is(IsoFlagType.collideW);
        final boolean b7 = b4 && isoGridSquare.Properties.Is(IsoFlagType.collideN);
        final boolean b8 = b2 && isoGridSquare.Properties.Is(IsoFlagType.collideW);
        if (b5 || b6 || b7 || b8) {
            return true;
        }
        final boolean b9 = isoGridSquare.x != this.x && isoGridSquare.y != this.y;
        if (isoGridSquare.Properties.Is(IsoFlagType.solid) || isoGridSquare.Properties.Is(IsoFlagType.blocksight)) {
            return true;
        }
        if (b9) {
            final IsoGridSquare gridSquare3 = getSquare.getGridSquare(this.x, isoGridSquare.y, this.z);
            final IsoGridSquare gridSquare4 = getSquare.getGridSquare(isoGridSquare.x, this.y, this.z);
            if (gridSquare3 != null && gridSquare3 != this && gridSquare3 != isoGridSquare) {
                gridSquare3.RecalcPropertiesIfNeeded();
            }
            if (gridSquare4 != null && gridSquare4 != this && gridSquare4 != isoGridSquare) {
                gridSquare4.RecalcPropertiesIfNeeded();
            }
            if (this.CalculateVisionBlocked(gridSquare3)) {
                return true;
            }
            if (this.CalculateVisionBlocked(gridSquare4)) {
                return true;
            }
            if (isoGridSquare.CalculateVisionBlocked(gridSquare3)) {
                return true;
            }
            if (isoGridSquare.CalculateVisionBlocked(gridSquare4)) {
                return true;
            }
        }
        return false;
    }
    
    public IsoGameCharacter FindFriend(final IsoGameCharacter isoGameCharacter, final int n, final Stack<IsoGameCharacter> stack) {
        final Stack<IsoGameCharacter> stack2 = new Stack<IsoGameCharacter>();
        for (int i = 0; i < isoGameCharacter.getLocalList().size(); ++i) {
            final IsoMovingObject o = isoGameCharacter.getLocalList().get(i);
            if (o != isoGameCharacter) {
                if (o != isoGameCharacter.getFollowingTarget()) {
                    if (o instanceof IsoGameCharacter && !(o instanceof IsoZombie) && !stack.contains(o)) {
                        stack2.add((IsoGameCharacter)o);
                    }
                }
            }
        }
        float n2 = 1000000.0f;
        IsoGameCharacter isoGameCharacter2 = null;
        for (final IsoGameCharacter isoGameCharacter3 : stack2) {
            final float n3 = 0.0f + Math.abs(this.getX() - isoGameCharacter3.getX()) + Math.abs(this.getY() - isoGameCharacter3.getY()) + Math.abs(this.getZ() - isoGameCharacter3.getZ());
            if (n3 < n2) {
                isoGameCharacter2 = isoGameCharacter3;
                n2 = n3;
            }
            if (isoGameCharacter3 == IsoPlayer.getInstance()) {
                isoGameCharacter2 = isoGameCharacter3;
            }
        }
        if (n2 > n) {
            return null;
        }
        return isoGameCharacter2;
    }
    
    public IsoGameCharacter FindEnemy(final IsoGameCharacter isoGameCharacter, final int n, final ArrayList<IsoMovingObject> list, final IsoGameCharacter isoGameCharacter2, final int n2) {
        float n3 = 1000000.0f;
        IsoGameCharacter isoGameCharacter3 = null;
        for (int i = 0; i < list.size(); ++i) {
            final IsoGameCharacter isoGameCharacter4 = list.get(i);
            final float n4 = 0.0f + Math.abs(this.getX() - isoGameCharacter4.getX()) + Math.abs(this.getY() - isoGameCharacter4.getY()) + Math.abs(this.getZ() - isoGameCharacter4.getZ());
            if (n4 < n && n4 < n3 && isoGameCharacter4.DistTo(isoGameCharacter2) < n2) {
                isoGameCharacter3 = isoGameCharacter4;
                n3 = n4;
            }
        }
        if (n3 > n) {
            return null;
        }
        return isoGameCharacter3;
    }
    
    public IsoGameCharacter FindEnemy(final IsoGameCharacter isoGameCharacter, final int n, final ArrayList<IsoMovingObject> list) {
        float n2 = 1000000.0f;
        IsoGameCharacter isoGameCharacter2 = null;
        for (int i = 0; i < list.size(); ++i) {
            final IsoGameCharacter isoGameCharacter3 = list.get(i);
            final float n3 = 0.0f + Math.abs(this.getX() - isoGameCharacter3.getX()) + Math.abs(this.getY() - isoGameCharacter3.getY()) + Math.abs(this.getZ() - isoGameCharacter3.getZ());
            if (n3 < n2) {
                isoGameCharacter2 = isoGameCharacter3;
                n2 = n3;
            }
        }
        if (n2 > n) {
            return null;
        }
        return isoGameCharacter2;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public void RecalcProperties() {
        this.CachedIsFree = false;
        String val = null;
        if (this.Properties.Is("waterAmount")) {
            val = this.Properties.Val("waterAmount");
        }
        String val2 = null;
        if (this.Properties.Is("fuelAmount")) {
            val2 = this.Properties.Val("fuelAmount");
        }
        if (this.zone == null) {
            this.zone = IsoWorld.instance.MetaGrid.getZoneAt(this.x, this.y, this.z);
        }
        this.Properties.Clear();
        this.hasTypes.clear();
        this.hasTree = false;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        boolean b = false;
        final int size = this.Objects.size();
        for (final IsoObject isoObject : this.Objects.getElements()) {
            if (isoObject != null) {
                final PropertyContainer properties = isoObject.getProperties();
                if (properties != null) {
                    if (!properties.Is(IsoFlagType.blueprint)) {
                        if (isoObject.sprite.forceRender) {
                            b = true;
                        }
                        if (isoObject.getType() == IsoObjectType.tree) {
                            this.hasTree = true;
                        }
                        this.hasTypes.set(isoObject.getType(), true);
                        this.Properties.AddProperties(properties);
                        if (properties.Is(IsoFlagType.water)) {
                            n2 = 0;
                        }
                        else {
                            if (n2 == 0 && properties.Is(IsoFlagType.solidfloor)) {
                                n2 = 1;
                            }
                            if (n == 0 && properties.Is(IsoFlagType.solidtrans)) {
                                n = 1;
                            }
                            if (n3 == 0 && properties.Is(IsoFlagType.solidfloor) && !properties.Is(IsoFlagType.transparentFloor)) {
                                n3 = 1;
                            }
                        }
                        if (n4 == 0 && properties.Is(IsoFlagType.collideN) && !properties.Is(IsoFlagType.HoppableN)) {
                            n4 = 1;
                        }
                        if (n5 == 0 && properties.Is(IsoFlagType.collideW) && !properties.Is(IsoFlagType.HoppableW)) {
                            n5 = 1;
                        }
                        if (n6 == 0 && properties.Is(IsoFlagType.cutN) && !properties.Is(IsoFlagType.transparentN)) {
                            n6 = 1;
                        }
                        if (n7 == 0 && properties.Is(IsoFlagType.cutW) && !properties.Is(IsoFlagType.transparentW)) {
                            n7 = 1;
                        }
                    }
                }
            }
        }
        if (this.roomID != -1 || this.haveRoof) {
            this.getProperties().UnSet(IsoFlagType.exterior);
            try {
                this.getPuddles().bRecalc = true;
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else {
            this.getProperties().Set(IsoFlagType.exterior);
            try {
                this.getPuddles().bRecalc = true;
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
        if (val != null) {
            this.getProperties().Set("waterAmount", val, false);
        }
        if (val2 != null) {
            this.getProperties().Set("fuelAmount", val2, false);
        }
        if (this.RainDrop != null) {
            this.Properties.Set(IsoFlagType.HasRaindrop);
        }
        if (b) {
            this.Properties.Set(IsoFlagType.forceRender);
        }
        if (this.RainSplash != null) {
            this.Properties.Set(IsoFlagType.HasRainSplashes);
        }
        if (this.burntOut) {
            this.Properties.Set(IsoFlagType.burntOut);
        }
        if (n == 0 && n2 != 0 && this.Properties.Is(IsoFlagType.water)) {
            this.Properties.UnSet(IsoFlagType.solidtrans);
        }
        if (n3 != 0 && this.Properties.Is(IsoFlagType.transparentFloor)) {
            this.Properties.UnSet(IsoFlagType.transparentFloor);
        }
        if (n4 != 0 && this.Properties.Is(IsoFlagType.HoppableN)) {
            this.Properties.UnSet(IsoFlagType.canPathN);
            this.Properties.UnSet(IsoFlagType.HoppableN);
        }
        if (n5 != 0 && this.Properties.Is(IsoFlagType.HoppableW)) {
            this.Properties.UnSet(IsoFlagType.canPathW);
            this.Properties.UnSet(IsoFlagType.HoppableW);
        }
        if (n6 != 0 && this.Properties.Is(IsoFlagType.transparentN)) {
            this.Properties.UnSet(IsoFlagType.transparentN);
        }
        if (n7 != 0 && this.Properties.Is(IsoFlagType.transparentW)) {
            this.Properties.UnSet(IsoFlagType.transparentW);
        }
        this.propertiesDirty = (this.chunk == null || this.chunk.bLoaded);
        if (this.chunk != null) {
            final boolean[] lightCheck = this.chunk.lightCheck;
            final int n8 = 0;
            final boolean[] lightCheck2 = this.chunk.lightCheck;
            final int n9 = 1;
            final boolean[] lightCheck3 = this.chunk.lightCheck;
            final int n10 = 2;
            final boolean[] lightCheck4 = this.chunk.lightCheck;
            final int n11 = 3;
            final boolean b2 = true;
            lightCheck3[n10] = (lightCheck4[n11] = b2);
            lightCheck[n8] = (lightCheck2[n9] = b2);
        }
        if (this.chunk != null) {
            this.chunk.physicsCheck = true;
            this.chunk.collision.clear();
        }
        this.isExteriorCache = this.Is(IsoFlagType.exterior);
        this.isSolidFloorCache = this.Is(IsoFlagType.solidfloor);
        this.isVegitationCache = this.Is(IsoFlagType.vegitation);
    }
    
    public void RecalcPropertiesIfNeeded() {
        if (this.propertiesDirty) {
            this.RecalcProperties();
        }
    }
    
    public void ReCalculateCollide(final IsoGridSquare isoGridSquare) {
        this.ReCalculateCollide(isoGridSquare, IsoGridSquare.cellGetSquare);
    }
    
    public void ReCalculateCollide(final IsoGridSquare isoGridSquare, final GetSquare getSquare) {
        if (1 + (isoGridSquare.x - this.x) < 0 || 1 + (isoGridSquare.y - this.y) < 0 || 1 + (isoGridSquare.z - this.z) < 0) {
            DebugLog.log("ERROR");
        }
        this.collideMatrix = setMatrixBit(this.collideMatrix, 1 + (isoGridSquare.x - this.x), 1 + (isoGridSquare.y - this.y), 1 + (isoGridSquare.z - this.z), this.CalculateCollide(isoGridSquare, false, false, false, false, getSquare));
    }
    
    public void ReCalculatePathFind(final IsoGridSquare isoGridSquare) {
        this.ReCalculatePathFind(isoGridSquare, IsoGridSquare.cellGetSquare);
    }
    
    public void ReCalculatePathFind(final IsoGridSquare isoGridSquare, final GetSquare getSquare) {
        this.pathMatrix = setMatrixBit(this.pathMatrix, 1 + (isoGridSquare.x - this.x), 1 + (isoGridSquare.y - this.y), 1 + (isoGridSquare.z - this.z), this.CalculateCollide(isoGridSquare, false, true, false, false, getSquare));
    }
    
    public void ReCalculateVisionBlocked(final IsoGridSquare isoGridSquare) {
        this.ReCalculateVisionBlocked(isoGridSquare, IsoGridSquare.cellGetSquare);
    }
    
    public void ReCalculateVisionBlocked(final IsoGridSquare isoGridSquare, final GetSquare getSquare) {
        this.visionMatrix = setMatrixBit(this.visionMatrix, 1 + (isoGridSquare.x - this.x), 1 + (isoGridSquare.y - this.y), 1 + (isoGridSquare.z - this.z), this.CalculateVisionBlocked(isoGridSquare, getSquare));
    }
    
    private static boolean testCollideSpecialObjects(final IsoMovingObject isoMovingObject, final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        for (int i = 0; i < isoGridSquare2.SpecialObjects.size(); ++i) {
            final IsoObject collidedObject = isoGridSquare2.SpecialObjects.get(i);
            if (collidedObject.TestCollide(isoMovingObject, isoGridSquare, isoGridSquare2)) {
                if (collidedObject instanceof IsoDoor) {
                    isoMovingObject.setCollidedWithDoor(true);
                }
                else if (collidedObject instanceof IsoThumpable && ((IsoThumpable)collidedObject).isDoor) {
                    isoMovingObject.setCollidedWithDoor(true);
                }
                isoMovingObject.setCollidedObject(collidedObject);
                return true;
            }
        }
        return false;
    }
    
    public boolean testCollideAdjacent(final IsoMovingObject isoMovingObject, final int n, final int n2, final int n3) {
        if (isoMovingObject instanceof IsoPlayer && ((IsoPlayer)isoMovingObject).isNoClip()) {
            return false;
        }
        if (this.collideMatrix == -1) {
            return true;
        }
        if (n < -1 || n > 1 || n2 < -1 || n2 > 1 || n3 < -1 || n3 > 1) {
            return true;
        }
        if (this.x + n < 0 || this.y + n2 < 0 || !IsoWorld.instance.MetaGrid.isValidChunk((this.x + n) / 10, (this.y + n2) / 10)) {
            return true;
        }
        final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x + n, this.y + n2, this.z + n3);
        SafeHouse safeHouse = null;
        if ((GameServer.bServer || GameClient.bClient) && isoMovingObject instanceof IsoPlayer && !ServerOptions.instance.SafehouseAllowTrepass.getValue()) {
            safeHouse = SafeHouse.isSafeHouse(this.getCell().getGridSquare(this.x + n, this.y + n2, 0), ((IsoPlayer)isoMovingObject).getUsername(), true);
        }
        if (safeHouse != null) {
            return true;
        }
        if (gridSquare != null && isoMovingObject != null) {
            final IsoObject testCollideSpecialObjects = this.testCollideSpecialObjects(gridSquare);
            if (testCollideSpecialObjects != null) {
                isoMovingObject.collideWith(testCollideSpecialObjects);
                if (testCollideSpecialObjects instanceof IsoDoor) {
                    isoMovingObject.setCollidedWithDoor(true);
                }
                else if (testCollideSpecialObjects instanceof IsoThumpable && ((IsoThumpable)testCollideSpecialObjects).isDoor) {
                    isoMovingObject.setCollidedWithDoor(true);
                }
                isoMovingObject.setCollidedObject(testCollideSpecialObjects);
                return true;
            }
        }
        if (IsoGridSquare.UseSlowCollision) {
            return this.CalculateCollide(gridSquare, false, false, false);
        }
        if (isoMovingObject instanceof IsoPlayer && getMatrixBit(this.collideMatrix, n + 1, n2 + 1, n3 + 1)) {
            this.RecalcAllWithNeighbours(true);
        }
        return getMatrixBit(this.collideMatrix, n + 1, n2 + 1, n3 + 1);
    }
    
    public boolean testCollideAdjacentAdvanced(final int n, final int n2, final int n3, final boolean b) {
        if (this.collideMatrix == -1) {
            return true;
        }
        if (n < -1 || n > 1 || n2 < -1 || n2 > 1 || n3 < -1 || n3 > 1) {
            return true;
        }
        final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x + n, this.y + n2, this.z + n3);
        if (gridSquare != null) {
            if (!gridSquare.SpecialObjects.isEmpty()) {
                for (int i = 0; i < gridSquare.SpecialObjects.size(); ++i) {
                    if (gridSquare.SpecialObjects.get(i).TestCollide(null, this, gridSquare)) {
                        return true;
                    }
                }
            }
            if (!this.SpecialObjects.isEmpty()) {
                for (int j = 0; j < this.SpecialObjects.size(); ++j) {
                    if (this.SpecialObjects.get(j).TestCollide(null, this, gridSquare)) {
                        return true;
                    }
                }
            }
        }
        if (IsoGridSquare.UseSlowCollision) {
            return this.CalculateCollide(gridSquare, false, false, false);
        }
        return getMatrixBit(this.collideMatrix, n + 1, n2 + 1, n3 + 1);
    }
    
    public static void setCollisionMode() {
        IsoGridSquare.UseSlowCollision = !IsoGridSquare.UseSlowCollision;
    }
    
    public boolean testPathFindAdjacent(final IsoMovingObject isoMovingObject, final int n, final int n2, final int n3) {
        return this.testPathFindAdjacent(isoMovingObject, n, n2, n3, IsoGridSquare.cellGetSquare);
    }
    
    public boolean testPathFindAdjacent(final IsoMovingObject isoMovingObject, final int n, final int n2, final int n3, final GetSquare getSquare) {
        if (n < -1 || n > 1 || n2 < -1 || n2 > 1 || n3 < -1 || n3 > 1) {
            return true;
        }
        if (this.Has(IsoObjectType.stairsTN) || this.Has(IsoObjectType.stairsTW)) {
            final IsoGridSquare gridSquare = getSquare.getGridSquare(n + this.x, n2 + this.y, n3 + this.z);
            if (gridSquare == null) {
                return true;
            }
            if (this.Has(IsoObjectType.stairsTN) && gridSquare.y < this.y && gridSquare.z == this.z) {
                return true;
            }
            if (this.Has(IsoObjectType.stairsTW) && gridSquare.x < this.x && gridSquare.z == this.z) {
                return true;
            }
        }
        if (IsoGridSquare.bDoSlowPathfinding) {
            return this.CalculateCollide(getSquare.getGridSquare(n + this.x, n2 + this.y, n3 + this.z), false, true, false, false, getSquare);
        }
        return getMatrixBit(this.pathMatrix, n + 1, n2 + 1, n3 + 1);
    }
    
    public LosUtil.TestResults testVisionAdjacent(final int n, final int n2, final int n3, final boolean b, final boolean b2) {
        if (n < -1 || n > 1 || n2 < -1 || n2 > 1 || n3 < -1 || n3 > 1) {
            return LosUtil.TestResults.Blocked;
        }
        if (n3 == 1 && (n != 0 || n2 != 0) && this.HasElevatedFloor()) {
            final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.x, this.y, this.z + n3);
            if (gridSquare != null) {
                return gridSquare.testVisionAdjacent(n, n2, 0, b, b2);
            }
        }
        if (n3 == -1 && (n != 0 || n2 != 0)) {
            final IsoGridSquare gridSquare2 = this.getCell().getGridSquare(this.x + n, this.y + n2, this.z + n3);
            if (gridSquare2 != null && gridSquare2.HasElevatedFloor()) {
                return this.testVisionAdjacent(n, n2, 0, b, b2);
            }
        }
        final LosUtil.TestResults clear = LosUtil.TestResults.Clear;
        if (n != 0 && n2 != 0 && b) {
            LosUtil.TestResults testResults = this.DoDiagnalCheck(n, n2, n3, b2);
            if (testResults == LosUtil.TestResults.Clear || testResults == LosUtil.TestResults.ClearThroughWindow || testResults == LosUtil.TestResults.ClearThroughOpenDoor || testResults == LosUtil.TestResults.ClearThroughClosedDoor) {
                final IsoGridSquare gridSquare3 = this.getCell().getGridSquare(this.x + n, this.y + n2, this.z + n3);
                if (gridSquare3 != null) {
                    testResults = gridSquare3.DoDiagnalCheck(-n, -n2, -n3, b2);
                }
            }
            return testResults;
        }
        final IsoGridSquare gridSquare4 = this.getCell().getGridSquare(this.x + n, this.y + n2, this.z + n3);
        LosUtil.TestResults testResults2 = LosUtil.TestResults.Clear;
        if (gridSquare4 != null && gridSquare4.z == this.z) {
            if (!this.SpecialObjects.isEmpty()) {
                for (int i = 0; i < this.SpecialObjects.size(); ++i) {
                    final IsoObject isoObject = this.SpecialObjects.get(i);
                    if (isoObject == null) {
                        return LosUtil.TestResults.Clear;
                    }
                    final IsoObject.VisionResult testVision = isoObject.TestVision(this, gridSquare4);
                    if (testVision != IsoObject.VisionResult.NoEffect) {
                        if (testVision == IsoObject.VisionResult.Unblocked && isoObject instanceof IsoDoor) {
                            testResults2 = (((IsoDoor)isoObject).IsOpen() ? LosUtil.TestResults.ClearThroughOpenDoor : LosUtil.TestResults.ClearThroughClosedDoor);
                        }
                        else if (testVision == IsoObject.VisionResult.Unblocked && isoObject instanceof IsoThumpable && ((IsoThumpable)isoObject).isDoor) {
                            testResults2 = LosUtil.TestResults.ClearThroughOpenDoor;
                        }
                        else if (testVision == IsoObject.VisionResult.Unblocked && isoObject instanceof IsoWindow) {
                            testResults2 = LosUtil.TestResults.ClearThroughWindow;
                        }
                        else {
                            if (testVision == IsoObject.VisionResult.Blocked && isoObject instanceof IsoDoor && !b2) {
                                return LosUtil.TestResults.Blocked;
                            }
                            if (testVision == IsoObject.VisionResult.Blocked && isoObject instanceof IsoThumpable && ((IsoThumpable)isoObject).isDoor && !b2) {
                                return LosUtil.TestResults.Blocked;
                            }
                            if (testVision == IsoObject.VisionResult.Blocked && isoObject instanceof IsoThumpable && ((IsoThumpable)isoObject).isWindow()) {
                                return LosUtil.TestResults.Blocked;
                            }
                            if (testVision == IsoObject.VisionResult.Blocked && isoObject instanceof IsoCurtain) {
                                return LosUtil.TestResults.Blocked;
                            }
                            if (testVision == IsoObject.VisionResult.Blocked && isoObject instanceof IsoWindow) {
                                return LosUtil.TestResults.Blocked;
                            }
                            if (testVision == IsoObject.VisionResult.Blocked && isoObject instanceof IsoBarricade) {
                                return LosUtil.TestResults.Blocked;
                            }
                        }
                    }
                }
            }
            if (!gridSquare4.SpecialObjects.isEmpty()) {
                for (int j = 0; j < gridSquare4.SpecialObjects.size(); ++j) {
                    final IsoObject isoObject2 = gridSquare4.SpecialObjects.get(j);
                    if (isoObject2 == null) {
                        return LosUtil.TestResults.Clear;
                    }
                    final IsoObject.VisionResult testVision2 = isoObject2.TestVision(this, gridSquare4);
                    if (testVision2 != IsoObject.VisionResult.NoEffect) {
                        if (testVision2 == IsoObject.VisionResult.Unblocked && isoObject2 instanceof IsoDoor) {
                            testResults2 = (((IsoDoor)isoObject2).IsOpen() ? LosUtil.TestResults.ClearThroughOpenDoor : LosUtil.TestResults.ClearThroughClosedDoor);
                        }
                        else if (testVision2 == IsoObject.VisionResult.Unblocked && isoObject2 instanceof IsoThumpable && ((IsoThumpable)isoObject2).isDoor) {
                            testResults2 = LosUtil.TestResults.ClearThroughOpenDoor;
                        }
                        else if (testVision2 == IsoObject.VisionResult.Unblocked && isoObject2 instanceof IsoWindow) {
                            testResults2 = LosUtil.TestResults.ClearThroughWindow;
                        }
                        else {
                            if (testVision2 == IsoObject.VisionResult.Blocked && isoObject2 instanceof IsoDoor && !b2) {
                                return LosUtil.TestResults.Blocked;
                            }
                            if (testVision2 == IsoObject.VisionResult.Blocked && isoObject2 instanceof IsoThumpable && ((IsoThumpable)isoObject2).isDoor && !b2) {
                                return LosUtil.TestResults.Blocked;
                            }
                            if (testVision2 == IsoObject.VisionResult.Blocked && isoObject2 instanceof IsoThumpable && ((IsoThumpable)isoObject2).isWindow()) {
                                return LosUtil.TestResults.Blocked;
                            }
                            if (testVision2 == IsoObject.VisionResult.Blocked && isoObject2 instanceof IsoCurtain) {
                                return LosUtil.TestResults.Blocked;
                            }
                            if (testVision2 == IsoObject.VisionResult.Blocked && isoObject2 instanceof IsoWindow) {
                                return LosUtil.TestResults.Blocked;
                            }
                            if (testVision2 == IsoObject.VisionResult.Blocked && isoObject2 instanceof IsoBarricade) {
                                return LosUtil.TestResults.Blocked;
                            }
                        }
                    }
                }
            }
        }
        final LosUtil.TestResults testResults3 = testResults2;
        return getMatrixBit(this.visionMatrix, n + 1, n2 + 1, n3 + 1) ? LosUtil.TestResults.Blocked : testResults3;
    }
    
    public boolean TreatAsSolidFloor() {
        if (this.SolidFloorCached) {
            return this.SolidFloor;
        }
        if (this.Properties.Is(IsoFlagType.solidfloor) || this.HasStairs()) {
            this.SolidFloor = true;
        }
        else {
            this.SolidFloor = false;
        }
        this.SolidFloorCached = true;
        return this.SolidFloor;
    }
    
    public void AddSpecialTileObject(final IsoObject isoObject) {
        this.AddSpecialObject(isoObject);
    }
    
    public void renderCharacters(final int n, final boolean b, final boolean b2) {
        if (this.z >= n) {
            return;
        }
        if (!IsoGridSquare.isOnScreenLast) {}
        if (b2) {
            IndieGL.glBlendFunc(770, 771);
        }
        if (this.MovingObjects.size() > 1) {
            Collections.sort(this.MovingObjects, IsoGridSquare.comp);
        }
        final ColorInfo colorInfo = this.lightInfo[IsoCamera.frameState.playerIndex];
        for (int size = this.StaticMovingObjects.size(), i = 0; i < size; ++i) {
            final IsoMovingObject isoMovingObject = this.StaticMovingObjects.get(i);
            if (isoMovingObject.sprite != null || isoMovingObject instanceof IsoDeadBody) {
                if (b) {
                    if (!(isoMovingObject instanceof IsoDeadBody)) {
                        continue;
                    }
                    if (this.HasStairs()) {
                        continue;
                    }
                }
                if (b || !(isoMovingObject instanceof IsoDeadBody) || this.HasStairs()) {
                    isoMovingObject.render(isoMovingObject.getX(), isoMovingObject.getY(), isoMovingObject.getZ(), colorInfo, true, false, null);
                }
            }
        }
        for (int size2 = this.MovingObjects.size(), j = 0; j < size2; ++j) {
            final IsoMovingObject isoMovingObject2 = this.MovingObjects.get(j);
            if (isoMovingObject2 != null) {
                if (isoMovingObject2.sprite != null) {
                    int n2 = isoMovingObject2.bOnFloor ? 1 : 0;
                    if (n2 != 0 && isoMovingObject2 instanceof IsoZombie) {
                        n2 = (((IsoZombie)isoMovingObject2).isProne() ? 1 : 0);
                        if (!BaseVehicle.RENDER_TO_TEXTURE) {
                            n2 = 0;
                        }
                    }
                    if (!b || n2 != 0) {
                        if (b || n2 == 0) {
                            isoMovingObject2.render(isoMovingObject2.getX(), isoMovingObject2.getY(), isoMovingObject2.getZ(), colorInfo, true, false, null);
                        }
                    }
                }
            }
        }
    }
    
    public void renderDeferredCharacters(final int n) {
        if (this.DeferedCharacters.isEmpty()) {
            return;
        }
        if (this.DeferredCharacterTick != this.getCell().DeferredCharacterTick) {
            this.DeferedCharacters.clear();
            return;
        }
        if (this.z >= n) {
            this.DeferedCharacters.clear();
            return;
        }
        if (PerformanceSettings.LightingFrameSkip == 3) {
            return;
        }
        final short stencilValue2z = this.getCell().getStencilValue2z(this.x, this.y, this.z - 1);
        this.getCell().setStencilValue2z(this.x, this.y, this.z - 1, stencilValue2z);
        IndieGL.enableAlphaTest();
        IndieGL.glAlphaFunc(516, 0.0f);
        IndieGL.glStencilFunc(519, stencilValue2z, 127);
        IndieGL.glStencilOp(7680, 7680, 7681);
        final float xToScreen = IsoUtils.XToScreen((float)this.x, (float)this.y, (float)this.z, 0);
        final float yToScreen = IsoUtils.YToScreen((float)this.x, (float)this.y, (float)this.z, 0);
        final float n2 = xToScreen - IsoCamera.frameState.OffX;
        final float n3 = yToScreen - IsoCamera.frameState.OffY;
        IndieGL.glColorMask(false, false, false, false);
        Texture.getWhite().renderwallnw(n2, n3, (float)(64 * Core.TileScale), (float)(32 * Core.TileScale), -1, -1, -1, -1, -1, -1);
        IndieGL.glColorMask(true, true, true, true);
        IndieGL.enableAlphaTest();
        IndieGL.glAlphaFunc(516, 0.0f);
        IndieGL.glStencilFunc(514, stencilValue2z, 127);
        IndieGL.glStencilOp(7680, 7680, 7680);
        final ColorInfo colorInfo = this.lightInfo[IsoCamera.frameState.playerIndex];
        Collections.sort(this.DeferedCharacters, IsoGridSquare.comp);
        for (int i = 0; i < this.DeferedCharacters.size(); ++i) {
            final IsoGameCharacter isoGameCharacter = this.DeferedCharacters.get(i);
            if (isoGameCharacter.sprite != null) {
                isoGameCharacter.setbDoDefer(false);
                isoGameCharacter.render(isoGameCharacter.getX(), isoGameCharacter.getY(), isoGameCharacter.getZ(), colorInfo, true, false, null);
                isoGameCharacter.renderObjectPicker(isoGameCharacter.getX(), isoGameCharacter.getY(), isoGameCharacter.getZ(), colorInfo);
                isoGameCharacter.setbDoDefer(true);
            }
        }
        this.DeferedCharacters.clear();
        IndieGL.glAlphaFunc(516, 0.0f);
        IndieGL.glStencilFunc(519, 1, 255);
        IndieGL.glStencilOp(7680, 7680, 7680);
    }
    
    public void switchLight(final boolean active) {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (isoObject instanceof IsoLightSwitch) {
                ((IsoLightSwitch)isoObject).setActive(active);
            }
        }
    }
    
    public boolean IsOnScreen() {
        return this.IsOnScreen(false);
    }
    
    public boolean IsOnScreen(final boolean b) {
        if (this.CachedScreenValue != Core.TileScale) {
            this.CachedScreenX = IsoUtils.XToScreen((float)this.x, (float)this.y, (float)this.z, 0);
            this.CachedScreenY = IsoUtils.YToScreen((float)this.x, (float)this.y, (float)this.z, 0);
            this.CachedScreenValue = Core.TileScale;
        }
        final float cachedScreenX = this.CachedScreenX;
        final float cachedScreenY = this.CachedScreenY;
        final float n = cachedScreenX - IsoCamera.frameState.OffX;
        final float n2 = cachedScreenY - IsoCamera.frameState.OffY;
        final int n3 = b ? (32 * Core.TileScale) : 0;
        if (this.hasTree) {
            final int n4 = 384 * Core.TileScale / 2 - 96 * Core.TileScale;
            final int n5 = 256 * Core.TileScale - 32 * Core.TileScale;
            return n + n4 > 0 - n3 && n2 + 32 * Core.TileScale > 0 - n3 && n - n4 < IsoCamera.frameState.OffscreenWidth + n3 && n2 - n5 < IsoCamera.frameState.OffscreenHeight + n3;
        }
        return n + 32 * Core.TileScale > 0 - n3 && n2 + 32 * Core.TileScale > 0 - n3 && n - 32 * Core.TileScale < IsoCamera.frameState.OffscreenWidth + n3 && n2 - 96 * Core.TileScale < IsoCamera.frameState.OffscreenHeight + n3;
    }
    
    void cacheLightInfo() {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        this.lightInfo[playerIndex] = this.lighting[playerIndex].lightInfo();
    }
    
    public void setLightInfoServerGUIOnly(final ColorInfo colorInfo) {
        this.lightInfo[0] = colorInfo;
    }
    
    int renderFloor(final Shader shader) {
        try {
            s_performance.renderFloor.start();
            return this.renderFloorInternal(shader);
        }
        finally {
            s_performance.renderFloor.end();
        }
    }
    
    private int renderFloorInternal(final Shader shader) {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final ColorInfo colorInfo = this.lightInfo[playerIndex];
        final IsoGridSquare camCharacterSquare = IsoCamera.frameState.CamCharacterSquare;
        final boolean bCouldSee = this.lighting[playerIndex].bCouldSee();
        final float darkMulti = this.lighting[playerIndex].darkMulti();
        final boolean b = GameClient.bClient && IsoPlayer.players[playerIndex] != null && IsoPlayer.players[playerIndex].isSeeNonPvpZone();
        final boolean b2 = Core.bDebug && GameClient.bClient && SafeHouse.isSafeHouse(this, null, true) != null;
        boolean b3 = true;
        float n = 1.0f;
        float n2 = 1.0f;
        if (camCharacterSquare != null) {
            final int roomID = this.getRoomID();
            if (roomID != -1) {
                final int getEffectivePlayerRoomId = IsoWorld.instance.CurrentCell.GetEffectivePlayerRoomId();
                if (getEffectivePlayerRoomId == -1 && IsoWorld.instance.CurrentCell.CanBuildingSquareOccludePlayer(this, playerIndex)) {
                    b3 = false;
                    n = 1.0f;
                    n2 = 1.0f;
                }
                else if (!bCouldSee && roomID != getEffectivePlayerRoomId && darkMulti < 0.5f) {
                    b3 = false;
                    n = 0.0f;
                    n2 = darkMulti * 2.0f;
                }
            }
        }
        final IsoWaterGeometry isoWaterGeometry = (this.z == 0) ? this.getWater() : null;
        final boolean b4 = isoWaterGeometry != null && isoWaterGeometry.bShore;
        final float n3 = (isoWaterGeometry == null) ? 0.0f : isoWaterGeometry.depth[0];
        final float n4 = (isoWaterGeometry == null) ? 0.0f : isoWaterGeometry.depth[3];
        final float n5 = (isoWaterGeometry == null) ? 0.0f : isoWaterGeometry.depth[2];
        final float n6 = (isoWaterGeometry == null) ? 0.0f : isoWaterGeometry.depth[1];
        int n7 = 0;
        final int size = this.Objects.size();
        for (final IsoObject isoObject : this.Objects.getElements()) {
            if (b && (isoObject.highlightFlags & 0x1) == 0x0) {
                isoObject.setHighlighted(true);
                if (NonPvpZone.getNonPvpZone(this.x, this.y) != null) {
                    isoObject.setHighlightColor(0.6f, 0.6f, 1.0f, 0.5f);
                }
                else {
                    isoObject.setHighlightColor(1.0f, 0.6f, 0.6f, 0.5f);
                }
            }
            if (b2) {
                isoObject.setHighlighted(true);
                isoObject.setHighlightColor(1.0f, 0.0f, 0.0f, 1.0f);
            }
            boolean b5 = true;
            if (isoObject.sprite != null && !isoObject.sprite.solidfloor && isoObject.sprite.renderLayer != 1) {
                b5 = false;
                n7 |= 0x4;
            }
            if (isoObject instanceof IsoFire || isoObject instanceof IsoCarBatteryCharger) {
                b5 = false;
                n7 |= 0x4;
            }
            if (!b5) {
                final boolean b6 = isoObject.sprite != null && (isoObject.sprite.isBush || isoObject.sprite.canBeRemoved || isoObject.sprite.attachedFloor);
                if (this.bFlattenGrassEtc && b6) {
                    n7 |= 0x2;
                }
            }
            else {
                IndieGL.glAlphaFunc(516, 0.0f);
                isoObject.setTargetAlpha(playerIndex, n2);
                if (b3) {
                    isoObject.setAlpha(playerIndex, n);
                }
                if (DebugOptions.instance.Terrain.RenderTiles.RenderGridSquares.getValue() && isoObject.sprite != null) {
                    IndieGL.StartShader(shader, playerIndex);
                    final FloorShaperAttachedSprites instance = FloorShaperAttachedSprites.instance;
                    FloorShaper floorShaper;
                    if (isoObject.getProperties().Is(IsoFlagType.diamondFloor) || isoObject.getProperties().Is(IsoFlagType.water)) {
                        floorShaper = FloorShaperDiamond.instance;
                    }
                    else {
                        floorShaper = FloorShaperDeDiamond.instance;
                    }
                    int vertLight = this.getVertLight(0, playerIndex);
                    int vertLight2 = this.getVertLight(1, playerIndex);
                    int vertLight3 = this.getVertLight(2, playerIndex);
                    int vertLight4 = this.getVertLight(3, playerIndex);
                    if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.LightingDebug.getValue()) {
                        vertLight = -65536;
                        vertLight2 = -65536;
                        vertLight3 = -16776961;
                        vertLight4 = -16776961;
                    }
                    instance.setShore(b4);
                    instance.setWaterDepth(n3, n4, n5, n6);
                    instance.setVertColors(vertLight, vertLight2, vertLight3, vertLight4);
                    floorShaper.setShore(b4);
                    floorShaper.setWaterDepth(n3, n4, n5, n6);
                    floorShaper.setVertColors(vertLight, vertLight2, vertLight3, vertLight4);
                    isoObject.renderFloorTile((float)this.x, (float)this.y, (float)this.z, (PerformanceSettings.LightingFrameSkip < 3) ? IsoGridSquare.defColorInfo : colorInfo, true, false, shader, floorShaper, instance);
                    IndieGL.StartShader(null);
                }
                n7 |= 0x1;
                if ((isoObject.highlightFlags & 0x1) == 0x0) {
                    n7 |= 0x8;
                }
                if ((isoObject.highlightFlags & 0x2) != 0x0) {
                    final IsoObject isoObject2 = isoObject;
                    isoObject2.highlightFlags &= 0xFFFFFFFC;
                }
            }
        }
        if ((this.getCell().rainIntensity > 0 || (RainManager.isRaining() && RainManager.RainIntensity > 0.0f)) && this.isExteriorCache && !this.isVegitationCache && this.isSolidFloorCache && this.isCouldSee(playerIndex)) {
            if (!IsoCamera.frameState.Paused) {
                final int n8 = (this.getCell().rainIntensity == 0) ? ((int)Math.min(Math.floor(RainManager.RainIntensity / 0.2f) + 1.0, 5.0)) : this.getCell().rainIntensity;
                if (this.splashFrame < 0.0f && Rand.Next(Rand.AdjustForFramerate((int)(5.0f / n8) * 100)) == 0) {
                    this.splashFrame = 0.0f;
                }
            }
            if (this.splashFrame >= 0.0f) {
                final int n9 = (int)(this.splashFrame * 4.0f);
                if (IsoGridSquare.rainsplashCache[n9] == null) {
                    IsoGridSquare.rainsplashCache[n9] = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n9);
                }
                final Texture sharedTexture = Texture.getSharedTexture(IsoGridSquare.rainsplashCache[n9]);
                if (sharedTexture != null) {
                    SpriteRenderer.instance.render(sharedTexture, IsoUtils.XToScreen(this.x + this.splashX, this.y + this.splashY, (float)this.z, 0) - IsoCamera.frameState.OffX - sharedTexture.getWidth() / 2 * Core.TileScale, IsoUtils.YToScreen(this.x + this.splashX, this.y + this.splashY, (float)this.z, 0) - IsoCamera.frameState.OffY - sharedTexture.getHeight() / 2 * Core.TileScale, (float)(sharedTexture.getWidth() * Core.TileScale), (float)(sharedTexture.getHeight() * Core.TileScale), 0.8f * colorInfo.r, 0.9f * colorInfo.g, 1.0f * colorInfo.b, 0.6f * ((this.getCell().rainIntensity > 0) ? 1.0f : RainManager.RainIntensity) * ((Core.getInstance().RenderShader != null) ? 0.6f : 1.0f), null);
                }
                if (!IsoCamera.frameState.Paused && this.splashFrameNum != IsoCamera.frameState.frameCount) {
                    this.splashFrame += 0.08f * (30.0f / PerformanceSettings.getLockFPS());
                    if (this.splashFrame >= 1.0f) {
                        this.splashX = Rand.Next(0.1f, 0.9f);
                        this.splashY = Rand.Next(0.1f, 0.9f);
                        this.splashFrame = -1.0f;
                    }
                    this.splashFrameNum = IsoCamera.frameState.frameCount;
                }
            }
        }
        else {
            this.splashFrame = -1.0f;
        }
        return n7;
    }
    
    private boolean isSpriteOnSouthOrEastWall(final IsoObject isoObject) {
        if (isoObject instanceof IsoBarricade) {
            return isoObject.getDir() == IsoDirections.S || isoObject.getDir() == IsoDirections.E;
        }
        if (isoObject instanceof IsoCurtain) {
            final IsoCurtain isoCurtain = (IsoCurtain)isoObject;
            return isoCurtain.getType() == IsoObjectType.curtainS || isoCurtain.getType() == IsoObjectType.curtainE;
        }
        final PropertyContainer properties = isoObject.getProperties();
        return properties != null && (properties.Is(IsoFlagType.attachedE) || properties.Is(IsoFlagType.attachedS));
    }
    
    public void RenderOpenDoorOnly() {
        final int size = this.Objects.size();
        final IsoObject[] array = this.Objects.getElements();
        try {
            final int n = 0;
            for (int n2 = size - 1, i = n; i <= n2; ++i) {
                final IsoObject isoObject = array[i];
                if (isoObject.sprite != null) {
                    if (isoObject.sprite.getProperties().Is(IsoFlagType.attachedN) || isoObject.sprite.getProperties().Is(IsoFlagType.attachedW)) {
                        isoObject.renderFxMask((float)this.x, (float)this.y, (float)this.z, false);
                    }
                }
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public boolean RenderMinusFloorFxMask(final int n, final boolean b, final boolean b2) {
        boolean b3 = false;
        final int size = this.Objects.size();
        final IsoObject[] array = this.Objects.getElements();
        final long currentTimeMillis = System.currentTimeMillis();
        try {
            final int n2 = b ? (size - 1) : 0;
            final int n3 = b ? 0 : (size - 1);
            int n4 = n2;
            while (true) {
                if (b) {
                    if (n4 < n3) {
                        break;
                    }
                }
                else if (n4 > n3) {
                    break;
                }
                final IsoObject isoObject = array[n4];
                Label_0548: {
                    if (isoObject.sprite != null) {
                        boolean b4 = true;
                        final IsoObjectType type = isoObject.sprite.getType();
                        if (isoObject.sprite.solidfloor || isoObject.sprite.renderLayer == 1) {
                            b4 = false;
                        }
                        if (this.z >= n && !isoObject.sprite.alwaysDraw) {
                            b4 = false;
                        }
                        final boolean b5 = isoObject.sprite.isBush || isoObject.sprite.canBeRemoved || isoObject.sprite.attachedFloor;
                        if (b2) {
                            if (!b5) {
                                break Label_0548;
                            }
                            if (!this.bFlattenGrassEtc) {
                                break Label_0548;
                            }
                        }
                        if (b2 || !b5 || !this.bFlattenGrassEtc) {
                            if ((type == IsoObjectType.WestRoofB || type == IsoObjectType.WestRoofM || type == IsoObjectType.WestRoofT) && this.z == n - 1 && this.z == (int)IsoCamera.CamCharacter.getZ()) {
                                b4 = false;
                            }
                            if (this.isSpriteOnSouthOrEastWall(isoObject)) {
                                if (!b) {
                                    b4 = false;
                                }
                                b3 = true;
                            }
                            else if (b) {
                                b4 = false;
                            }
                            if (b4) {
                                if (isoObject.sprite.cutW || isoObject.sprite.cutN) {
                                    final int playerIndex = IsoCamera.frameState.playerIndex;
                                    final boolean cutN = isoObject.sprite.cutN;
                                    final boolean cutW = isoObject.sprite.cutW;
                                    final IsoGridSquare isoGridSquare = this.nav[IsoDirections.S.index()];
                                    final IsoGridSquare isoGridSquare2 = this.nav[IsoDirections.E.index()];
                                    final boolean b6 = isoGridSquare != null && isoGridSquare.getPlayerCutawayFlag(playerIndex, currentTimeMillis);
                                    final boolean playerCutawayFlag = this.getPlayerCutawayFlag(playerIndex, currentTimeMillis);
                                    final boolean b7 = isoGridSquare2 != null && isoGridSquare2.getPlayerCutawayFlag(playerIndex, currentTimeMillis);
                                    IsoDirections isoDirections;
                                    if (cutN && cutW) {
                                        isoDirections = IsoDirections.NW;
                                    }
                                    else if (cutN) {
                                        isoDirections = IsoDirections.N;
                                    }
                                    else if (cutW) {
                                        isoDirections = IsoDirections.W;
                                    }
                                    else {
                                        isoDirections = IsoDirections.W;
                                    }
                                    this.DoCutawayShaderSprite(isoObject.sprite, isoDirections, b6, playerCutawayFlag, b7);
                                }
                                else {
                                    isoObject.renderFxMask((float)this.x, (float)this.y, (float)this.z, false);
                                }
                            }
                        }
                    }
                }
                n4 += (b ? -1 : 1);
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
        return b3;
    }
    
    private boolean isWindowOrWindowFrame(final IsoObject isoObject, final boolean b) {
        if (isoObject == null || isoObject.sprite == null) {
            return false;
        }
        if (b && isoObject.sprite.getProperties().Is(IsoFlagType.windowN)) {
            return true;
        }
        if (!b && isoObject.sprite.getProperties().Is(IsoFlagType.windowW)) {
            return true;
        }
        final IsoThumpable isoThumpable = Type.tryCastTo(isoObject, IsoThumpable.class);
        if (isoThumpable != null && isoThumpable.isWindow()) {
            return b == isoThumpable.getNorth();
        }
        return IsoWindowFrame.isWindowFrame(isoObject, b);
    }
    
    boolean renderMinusFloor(final int n, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5, final Shader shader) {
        boolean renderMinusFloor = false;
        if (!this.localTemporaryObjects.isEmpty()) {
            renderMinusFloor = this.renderMinusFloor(this.localTemporaryObjects, n, b, b2, b3, b4, b5, shader);
        }
        return this.renderMinusFloor(this.Objects, n, b, b2, b3, b4, b5, shader) || renderMinusFloor;
    }
    
    boolean renderMinusFloor(final PZArrayList<IsoObject> list, final int n, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5, final Shader shader) {
        if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.RenderMinusFloor.getValue()) {
            return false;
        }
        IndieGL.glBlendFunc(770, 771);
        int n2 = 0;
        IsoGridSquare.isOnScreenLast = this.IsOnScreen();
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final IsoGridSquare camCharacterSquare = IsoCamera.frameState.CamCharacterSquare;
        ColorInfo defColorInfo = this.lightInfo[playerIndex];
        final boolean bCouldSee = this.lighting[playerIndex].bCouldSee();
        final float darkMulti = this.lighting[playerIndex].darkMulti();
        final boolean canBuildingSquareOccludePlayer = IsoWorld.instance.CurrentCell.CanBuildingSquareOccludePlayer(this, playerIndex);
        defColorInfo.a = 1.0f;
        IsoGridSquare.defColorInfo.r = 1.0f;
        IsoGridSquare.defColorInfo.g = 1.0f;
        IsoGridSquare.defColorInfo.b = 1.0f;
        IsoGridSquare.defColorInfo.a = 1.0f;
        if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
            defColorInfo = IsoGridSquare.defColorInfo;
        }
        final float n3 = this.CachedScreenX - IsoCamera.frameState.OffX;
        final float n4 = this.CachedScreenY - IsoCamera.frameState.OffY;
        boolean b6 = true;
        final IsoCell cell = this.getCell();
        if (n3 + 32 * Core.TileScale <= cell.StencilX1 || n3 - 32 * Core.TileScale >= cell.StencilX2 || n4 + 32 * Core.TileScale <= cell.StencilY1 || n4 - 96 * Core.TileScale >= cell.StencilY2) {
            b6 = false;
        }
        boolean b7 = false;
        final int size = list.size();
        final IsoObject[] array = list.getElements();
        final int n5 = b ? (size - 1) : 0;
        final int n6 = b ? 0 : (size - 1);
        boolean b8 = false;
        boolean b9 = false;
        boolean b10 = false;
        boolean b11 = false;
        if (!b) {
            for (int i = n5; i <= n6; ++i) {
                final IsoObject isoObject = array[i];
                if (this.isWindowOrWindowFrame(isoObject, true) && (b4 || b5)) {
                    final IsoGridSquare isoGridSquare = this.nav[IsoDirections.N.index()];
                    b10 = (bCouldSee || (isoGridSquare != null && isoGridSquare.isCouldSee(playerIndex)));
                }
                if (this.isWindowOrWindowFrame(isoObject, false) && (b4 || b3)) {
                    final IsoGridSquare isoGridSquare2 = this.nav[IsoDirections.W.index()];
                    b11 = (bCouldSee || (isoGridSquare2 != null && isoGridSquare2.isCouldSee(playerIndex)));
                }
                if (isoObject.sprite != null && (isoObject.sprite.getType() == IsoObjectType.doorFrN || isoObject.sprite.getType() == IsoObjectType.doorN) && (b4 || b5)) {
                    final IsoGridSquare isoGridSquare3 = this.nav[IsoDirections.N.index()];
                    b8 = (bCouldSee || (isoGridSquare3 != null && isoGridSquare3.isCouldSee(playerIndex)));
                }
                if (isoObject.sprite != null && (isoObject.sprite.getType() == IsoObjectType.doorFrW || isoObject.sprite.getType() == IsoObjectType.doorW) && (b4 || b3)) {
                    final IsoGridSquare isoGridSquare4 = this.nav[IsoDirections.W.index()];
                    b9 = (bCouldSee || (isoGridSquare4 != null && isoGridSquare4.isCouldSee(playerIndex)));
                }
            }
        }
        final int getEffectivePlayerRoomId = IsoWorld.instance.CurrentCell.GetEffectivePlayerRoomId();
        IsoGridSquare.bWallCutawayN = false;
        IsoGridSquare.bWallCutawayW = false;
        int n7 = n5;
        while (true) {
            if (b) {
                if (n7 < n6) {
                    break;
                }
            }
            else if (n7 > n6) {
                break;
            }
            final IsoObject isoObject2 = array[n7];
            int n8 = 1;
            IsoObjectType isoObjectType = IsoObjectType.MAX;
            if (isoObject2.sprite != null) {
                isoObjectType = isoObject2.sprite.getType();
            }
            IsoGridSquare.CircleStencil = false;
            if (isoObject2.sprite != null && (isoObject2.sprite.solidfloor || isoObject2.sprite.renderLayer == 1)) {
                n8 = 0;
            }
            if (isoObject2 instanceof IsoFire) {
                n8 = (b2 ? 0 : 1);
            }
            if (this.z >= n && (isoObject2.sprite == null || !isoObject2.sprite.alwaysDraw)) {
                n8 = 0;
            }
            final boolean b12 = isoObject2.sprite != null && (isoObject2.sprite.isBush || isoObject2.sprite.canBeRemoved || isoObject2.sprite.attachedFloor);
            Label_2739: {
                if (b2) {
                    if (!b12) {
                        break Label_2739;
                    }
                    if (!this.bFlattenGrassEtc) {
                        break Label_2739;
                    }
                }
                if (b2 || !b12 || !this.bFlattenGrassEtc) {
                    if (isoObject2.sprite != null && (isoObjectType == IsoObjectType.WestRoofB || isoObjectType == IsoObjectType.WestRoofM || isoObjectType == IsoObjectType.WestRoofT) && this.z == n - 1 && this.z == (int)IsoCamera.CamCharacter.getZ()) {
                        n8 = 0;
                    }
                    final boolean b13 = isoObjectType == IsoObjectType.doorFrW || isoObjectType == IsoObjectType.doorW || (isoObject2.sprite != null && isoObject2.sprite.cutW);
                    final boolean b14 = isoObjectType == IsoObjectType.doorFrN || isoObjectType == IsoObjectType.doorN || (isoObject2.sprite != null && isoObject2.sprite.cutN);
                    final boolean b15 = (isoObject2 instanceof IsoDoor && ((IsoDoor)isoObject2).open) || (isoObject2 instanceof IsoThumpable && ((IsoThumpable)isoObject2).open);
                    final boolean b16 = isoObject2.container != null;
                    final boolean b17 = isoObject2.sprite != null && isoObject2.sprite.getProperties().Is(IsoFlagType.waterPiped);
                    if (isoObject2.sprite != null && isoObjectType == IsoObjectType.MAX && !(isoObject2 instanceof IsoDoor) && !(isoObject2 instanceof IsoWindow) && !b16 && !b17) {
                        if (!b13 && isoObject2.sprite.getProperties().Is(IsoFlagType.attachedW) && (canBuildingSquareOccludePlayer || b3 || b4)) {
                            n8 = (IsoGridSquare.bWallCutawayW ? 0 : 1);
                        }
                        else if (!b14 && isoObject2.sprite.getProperties().Is(IsoFlagType.attachedN) && (canBuildingSquareOccludePlayer || b4 || b5)) {
                            n8 = (IsoGridSquare.bWallCutawayN ? 0 : 1);
                        }
                    }
                    if (isoObject2.sprite != null && !isoObject2.sprite.solidfloor && IsoPlayer.getInstance().isClimbing()) {
                        n8 = 1;
                    }
                    if (this.isSpriteOnSouthOrEastWall(isoObject2)) {
                        if (!b) {
                            n8 = 0;
                        }
                        b7 = true;
                    }
                    else if (b) {
                        n8 = 0;
                    }
                    if (n8 != 0) {
                        IndieGL.glAlphaFunc(516, 0.0f);
                        isoObject2.bAlphaForced = false;
                        if (b15) {
                            isoObject2.setTargetAlpha(playerIndex, 0.6f);
                            isoObject2.setAlpha(playerIndex, 0.6f);
                        }
                        if (isoObject2.sprite != null && (b13 || b14)) {
                            if (PerformanceSettings.LightingFrameSkip < 3) {
                                if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.DoorsAndWalls.getValue()) {
                                    IsoGridSquare.CircleStencil = true;
                                    if (camCharacterSquare != null && this.getRoomID() != -1 && getEffectivePlayerRoomId == -1 && canBuildingSquareOccludePlayer) {
                                        isoObject2.setTargetAlpha(playerIndex, 0.5f);
                                        isoObject2.setAlpha(playerIndex, 0.5f);
                                    }
                                    else if (!b15) {
                                        isoObject2.setTargetAlpha(playerIndex, 1.0f);
                                        isoObject2.setAlpha(playerIndex, 1.0f);
                                    }
                                    isoObject2.bAlphaForced = true;
                                    if (isoObject2.sprite.cutW && isoObject2.sprite.cutN) {
                                        n2 = this.DoWallLightingNW(isoObject2, n2, b3, b4, b5, b8, b9, b10, b11, shader);
                                    }
                                    else if (isoObject2.sprite.getType() == IsoObjectType.doorFrW || isoObjectType == IsoObjectType.doorW || isoObject2.sprite.cutW) {
                                        n2 = this.DoWallLightingW(isoObject2, n2, b3, b4, b9, b11, shader);
                                    }
                                    else if (isoObjectType == IsoObjectType.doorFrN || isoObjectType == IsoObjectType.doorN || isoObject2.sprite.cutN) {
                                        n2 = this.DoWallLightingN(isoObject2, n2, b4, b5, b8, b10, shader);
                                    }
                                    if (isoObject2 instanceof IsoWindow && isoObject2.getTargetAlpha(playerIndex) < 1.0f) {
                                        IsoGridSquare.bWallCutawayN |= isoObject2.sprite.cutN;
                                        IsoGridSquare.bWallCutawayW |= isoObject2.sprite.cutW;
                                    }
                                }
                            }
                            else if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.DoorsAndWalls_SimpleLighting.getValue()) {
                                if (this.z != (int)IsoCamera.frameState.CamCharacterZ || isoObjectType == IsoObjectType.doorFrW || isoObjectType == IsoObjectType.doorFrN || isoObject2 instanceof IsoWindow) {
                                    b6 = false;
                                }
                                if (isoObject2.getTargetAlpha(playerIndex) < 1.0f) {
                                    if (!b6) {
                                        isoObject2.setTargetAlpha(playerIndex, 1.0f);
                                    }
                                    isoObject2.setAlphaToTarget(playerIndex);
                                    IsoObject.LowLightingQualityHack = false;
                                    isoObject2.render((float)this.x, (float)this.y, (float)this.z, defColorInfo, true, false, null);
                                    if (!IsoObject.LowLightingQualityHack) {
                                        isoObject2.setTargetAlpha(playerIndex, 1.0f);
                                    }
                                }
                                else {
                                    isoObject2.render((float)this.x, (float)this.y, (float)this.z, defColorInfo, true, false, null);
                                }
                            }
                        }
                        else if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Objects.getValue()) {
                            if (this.getRoomID() != -1 && this.getRoomID() != getEffectivePlayerRoomId && IsoPlayer.players[playerIndex].isSeatedInVehicle() && IsoPlayer.players[playerIndex].getVehicle().getCurrentSpeedKmHour() >= 50.0f) {
                                break;
                            }
                            if (this.getRoomID() == -1 && (b5 || b3) && (isoObjectType == IsoObjectType.WestRoofB || isoObjectType == IsoObjectType.WestRoofM || isoObjectType == IsoObjectType.WestRoofT)) {
                                isoObject2.setTargetAlpha(playerIndex, 0.0f);
                            }
                            else if (camCharacterSquare != null && !bCouldSee && this.getRoomID() != getEffectivePlayerRoomId && darkMulti < 0.5f) {
                                isoObject2.setTargetAlpha(playerIndex, darkMulti * 2.0f);
                            }
                            else {
                                if (!b15) {
                                    isoObject2.setTargetAlpha(playerIndex, 1.0f);
                                }
                                if ((IsoPlayer.getInstance() != null && isoObject2.getProperties() != null && (isoObject2.getProperties().Is(IsoFlagType.solid) || isoObject2.getProperties().Is(IsoFlagType.solidtrans) || isoObject2.getProperties().Is(IsoFlagType.attachedCeiling))) || (isoObjectType.index() > 2 && isoObjectType.index() < 9 && IsoCamera.frameState.CamCharacterZ <= isoObject2.getZ())) {
                                    int n9 = 2;
                                    float n10 = 0.75f;
                                    if (isoObjectType.index() > 2 && isoObjectType.index() < 9) {
                                        n9 = 4;
                                        n10 = 0.5f;
                                    }
                                    final int n11 = this.getX() - (int)IsoPlayer.getInstance().getX();
                                    final int n12 = this.getY() - (int)IsoPlayer.getInstance().getY();
                                    if ((n11 > 0 && n11 < n9 && n12 >= 0 && n12 < n9) || (n12 > 0 && n12 < n9 && n11 >= 0 && n11 < n9)) {
                                        isoObject2.setTargetAlpha(playerIndex, n10);
                                    }
                                    final IsoZombie nearestVisibleZombie = IsoCell.getInstance().getNearestVisibleZombie(playerIndex);
                                    if (nearestVisibleZombie != null && nearestVisibleZombie.getCurrentSquare() != null && nearestVisibleZombie.getCurrentSquare().isCanSee(playerIndex)) {
                                        final int n13 = this.getX() - (int)nearestVisibleZombie.x;
                                        final int n14 = this.getY() - (int)nearestVisibleZombie.y;
                                        if ((n13 > 0 && n13 < n9 && n14 >= 0 && n14 < n9) || (n14 > 0 && n14 < n9 && n13 >= 0 && n13 < n9)) {
                                            isoObject2.setTargetAlpha(playerIndex, n10);
                                        }
                                    }
                                }
                            }
                            if (isoObject2 instanceof IsoWindow) {
                                final IsoWindow isoWindow = (IsoWindow)isoObject2;
                                if (isoObject2.getTargetAlpha(playerIndex) < 1.0E-4f) {
                                    final IsoGridSquare oppositeSquare = isoWindow.getOppositeSquare();
                                    if (oppositeSquare != null && oppositeSquare != this && oppositeSquare.lighting[playerIndex].bSeen()) {
                                        isoObject2.setTargetAlpha(playerIndex, oppositeSquare.lighting[playerIndex].darkMulti() * 2.0f);
                                    }
                                }
                                if (isoObject2.getTargetAlpha(playerIndex) > 0.4f) {
                                    if ((b4 || b5) && isoObject2.sprite.getProperties().Is(IsoFlagType.windowN)) {
                                        isoObject2.setTargetAlpha(playerIndex, 0.4f);
                                        IsoGridSquare.bWallCutawayN = true;
                                    }
                                    else if ((b4 || b3) && isoObject2.sprite.getProperties().Is(IsoFlagType.windowW)) {
                                        isoObject2.setTargetAlpha(playerIndex, 0.4f);
                                        IsoGridSquare.bWallCutawayW = true;
                                    }
                                }
                            }
                            if (isoObject2 instanceof IsoTree) {
                                if (b6 && this.x >= (int)IsoCamera.frameState.CamCharacterX && this.y >= (int)IsoCamera.frameState.CamCharacterY && camCharacterSquare != null && camCharacterSquare.Is(IsoFlagType.exterior)) {
                                    ((IsoTree)isoObject2).bRenderFlag = true;
                                    isoObject2.setTargetAlpha(playerIndex, Math.min(0.99f, isoObject2.getTargetAlpha(playerIndex)));
                                }
                                else {
                                    ((IsoTree)isoObject2).bRenderFlag = false;
                                }
                            }
                            isoObject2.render((float)this.x, (float)this.y, (float)this.z, defColorInfo, true, false, null);
                        }
                        if ((isoObject2.highlightFlags & 0x2) != 0x0) {
                            final IsoTree isoTree = (IsoTree)isoObject2;
                            isoTree.highlightFlags &= 0xFFFFFFFC;
                        }
                    }
                }
            }
            n7 += (b ? -1 : 1);
        }
        return b7;
    }
    
    void RereouteWallMaskTo(final IsoObject rerouteMask) {
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject = this.Objects.get(i);
            if (isoObject.sprite.getProperties().Is(IsoFlagType.collideW) || isoObject.sprite.getProperties().Is(IsoFlagType.collideN)) {
                isoObject.rerouteMask = rerouteMask;
            }
        }
    }
    
    void setBlockedGridPointers(final GetSquare getSquare) {
        this.w = getSquare.getGridSquare(this.x - 1, this.y, this.z);
        this.e = getSquare.getGridSquare(this.x + 1, this.y, this.z);
        this.s = getSquare.getGridSquare(this.x, this.y + 1, this.z);
        this.n = getSquare.getGridSquare(this.x, this.y - 1, this.z);
        this.ne = getSquare.getGridSquare(this.x + 1, this.y - 1, this.z);
        this.nw = getSquare.getGridSquare(this.x - 1, this.y - 1, this.z);
        this.se = getSquare.getGridSquare(this.x + 1, this.y + 1, this.z);
        this.sw = getSquare.getGridSquare(this.x - 1, this.y + 1, this.z);
        if (this.s != null && this.testPathFindAdjacent(null, this.s.x - this.x, this.s.y - this.y, this.s.z - this.z, getSquare)) {
            this.s = null;
        }
        if (this.w != null && this.testPathFindAdjacent(null, this.w.x - this.x, this.w.y - this.y, this.w.z - this.z, getSquare)) {
            this.w = null;
        }
        if (this.n != null && this.testPathFindAdjacent(null, this.n.x - this.x, this.n.y - this.y, this.n.z - this.z, getSquare)) {
            this.n = null;
        }
        if (this.e != null && this.testPathFindAdjacent(null, this.e.x - this.x, this.e.y - this.y, this.e.z - this.z, getSquare)) {
            this.e = null;
        }
        if (this.sw != null && this.testPathFindAdjacent(null, this.sw.x - this.x, this.sw.y - this.y, this.sw.z - this.z, getSquare)) {
            this.sw = null;
        }
        if (this.se != null && this.testPathFindAdjacent(null, this.se.x - this.x, this.se.y - this.y, this.se.z - this.z, getSquare)) {
            this.se = null;
        }
        if (this.nw != null && this.testPathFindAdjacent(null, this.nw.x - this.x, this.nw.y - this.y, this.nw.z - this.z, getSquare)) {
            this.nw = null;
        }
        if (this.ne != null && this.testPathFindAdjacent(null, this.ne.x - this.x, this.ne.y - this.y, this.ne.z - this.z, getSquare)) {
            this.ne = null;
        }
    }
    
    public IsoObject getContainerItem(final String s) {
        final int size = this.getObjects().size();
        for (final IsoObject isoObject : this.getObjects().getElements()) {
            if (isoObject.getContainer() != null && s.equals(isoObject.getContainer().getType())) {
                return isoObject;
            }
        }
        return null;
    }
    
    public void StartFire() {
        IsoFireManager.StartFire(this.getCell(), this, true, 100000);
    }
    
    public void explode() {
        IsoFireManager.explode(this.getCell(), this, 100000);
    }
    
    public int getHourLastSeen() {
        return this.hourLastSeen;
    }
    
    public float getHoursSinceLastSeen() {
        return (float)GameTime.instance.getWorldAgeHours() - this.hourLastSeen;
    }
    
    public void CalcVisibility(final int n) {
        final IsoPlayer isoPlayer = IsoPlayer.players[n];
        final ILighting lighting = this.lighting[n];
        lighting.bCanSee(false);
        lighting.bCouldSee(false);
        if (!GameServer.bServer && (isoPlayer == null || (isoPlayer.isDead() && isoPlayer.ReanimatedCorpse == null))) {
            lighting.bSeen(true);
            lighting.bCanSee(true);
            lighting.bCouldSee(true);
            return;
        }
        if (isoPlayer == null) {
            return;
        }
        final IsoGameCharacter.LightInfo lightInfo2 = isoPlayer.getLightInfo2();
        final IsoGridSquare square = lightInfo2.square;
        if (square == null) {
            return;
        }
        if (this.getChunk() == null) {
            return;
        }
        IsoGridSquare.tempo.x = this.x + 0.5f;
        IsoGridSquare.tempo.y = this.y + 0.5f;
        IsoGridSquare.tempo2.x = lightInfo2.x;
        IsoGridSquare.tempo2.y = lightInfo2.y;
        final Vector2 tempo2 = IsoGridSquare.tempo2;
        tempo2.x -= IsoGridSquare.tempo.x;
        final Vector2 tempo3 = IsoGridSquare.tempo2;
        tempo3.y -= IsoGridSquare.tempo.y;
        final Vector2 tempo4 = IsoGridSquare.tempo;
        final float length = IsoGridSquare.tempo2.getLength();
        IsoGridSquare.tempo2.normalize();
        if (isoPlayer instanceof IsoSurvivor) {
            isoPlayer.setForwardDirection(tempo4);
            lightInfo2.angleX = tempo4.x;
            lightInfo2.angleY = tempo4.y;
        }
        tempo4.x = lightInfo2.angleX;
        tempo4.y = lightInfo2.angleY;
        tempo4.normalize();
        float dot = IsoGridSquare.tempo2.dot(tempo4);
        if (square == this) {
            dot = -1.0f;
        }
        if (!GameServer.bServer) {
            float n2 = isoPlayer.getStats().fatigue - 0.6f;
            if (n2 < 0.0f) {
                n2 = 0.0f;
            }
            float n3 = n2 * 2.5f;
            if (isoPlayer.Traits.HardOfHearing.isSet() && n3 < 0.7f) {
                n3 = 0.7f;
            }
            float n4 = 2.0f;
            if (isoPlayer.Traits.KeenHearing.isSet()) {
                n4 += 3.0f;
            }
            if (length < n4 * (1.0f - n3) && !isoPlayer.Traits.Deaf.isSet()) {
                dot = -1.0f;
            }
        }
        final LosUtil.TestResults lineClearCached = LosUtil.lineClearCached(this.getCell(), this.x, this.y, this.z, (int)lightInfo2.x, (int)lightInfo2.y, (int)lightInfo2.z, false, n);
        float n5 = -0.2f - (isoPlayer.getStats().fatigue - 0.6f);
        if (n5 > -0.2f) {
            n5 = -0.2f;
        }
        if (isoPlayer.getStats().fatigue >= 1.0f) {
            n5 -= 0.2f;
        }
        if (isoPlayer.getMoodles().getMoodleLevel(MoodleType.Panic) == 4) {
            n5 -= 0.2f;
        }
        if (n5 < -0.9f) {
            n5 = -0.9f;
        }
        if (isoPlayer.Traits.EagleEyed.isSet()) {
            n5 += 0.2f;
        }
        if (isoPlayer instanceof IsoPlayer && isoPlayer.getVehicle() != null) {
            n5 = 1.0f;
        }
        if (dot > n5 || lineClearCached == LosUtil.TestResults.Blocked) {
            if (lineClearCached == LosUtil.TestResults.Blocked) {
                lighting.bCouldSee(false);
            }
            else {
                lighting.bCouldSee(true);
            }
            if (!GameServer.bServer) {
                if (lighting.bSeen()) {
                    final float ambientForPlayer = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex());
                    float n6;
                    if (!lighting.bCouldSee()) {
                        n6 = ambientForPlayer * 0.5f;
                    }
                    else {
                        n6 = ambientForPlayer * 0.94f;
                    }
                    if (this.room == null && square.getRoom() == null) {
                        lighting.targetDarkMulti(n6);
                    }
                    else if (this.room != null && square.getRoom() != null && this.room.building == square.getRoom().building) {
                        if (this.room != square.getRoom() && !lighting.bCouldSee()) {
                            lighting.targetDarkMulti(0.0f);
                        }
                        else {
                            lighting.targetDarkMulti(n6);
                        }
                    }
                    else if (this.room == null) {
                        lighting.targetDarkMulti(n6 / 2.0f);
                    }
                    else if (lighting.lampostTotalR() + lighting.lampostTotalG() + lighting.lampostTotalB() == 0.0f) {
                        lighting.targetDarkMulti(0.0f);
                    }
                    if (this.room != null) {
                        lighting.targetDarkMulti(lighting.targetDarkMulti() * 0.7f);
                    }
                }
                else {
                    lighting.targetDarkMulti(0.0f);
                    lighting.darkMulti(0.0f);
                }
            }
        }
        else {
            lighting.bCouldSee(true);
            if (this.room != null && this.room.def != null && !this.room.def.bExplored) {
                int n7 = 10;
                if (lightInfo2.square != null && lightInfo2.square.getBuilding() == this.room.building) {
                    n7 = 50;
                }
                if (!GameServer.bServer || !(isoPlayer instanceof IsoPlayer) || !isoPlayer.isGhostMode()) {
                    if (IsoUtils.DistanceManhatten(lightInfo2.x, lightInfo2.y, (float)this.x, (float)this.y) < n7 && this.z == (int)lightInfo2.z) {
                        if (GameServer.bServer) {
                            DebugLog.log(DebugType.Zombie, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.room.def.ID));
                        }
                        this.room.def.bExplored = true;
                        this.room.onSee();
                        this.room.seen = 0;
                    }
                }
            }
            if (!GameClient.bClient) {
                Meta.instance.dealWithSquareSeen(this);
            }
            lighting.bCanSee(true);
            lighting.bSeen(true);
            lighting.targetDarkMulti(1.0f);
        }
        if (dot > n5) {
            lighting.targetDarkMulti(lighting.targetDarkMulti() * 0.85f);
        }
        if (GameServer.bServer) {
            return;
        }
        for (int i = 0; i < lightInfo2.torches.size(); ++i) {
            final IsoGameCharacter.TorchInfo torchInfo = lightInfo2.torches.get(i);
            IsoGridSquare.tempo2.x = torchInfo.x;
            IsoGridSquare.tempo2.y = torchInfo.y;
            final Vector2 tempo5 = IsoGridSquare.tempo2;
            tempo5.x -= this.x + 0.5f;
            final Vector2 tempo6 = IsoGridSquare.tempo2;
            tempo6.y -= this.y + 0.5f;
            final float length2 = IsoGridSquare.tempo2.getLength();
            IsoGridSquare.tempo2.normalize();
            tempo4.x = torchInfo.angleX;
            tempo4.y = torchInfo.angleY;
            tempo4.normalize();
            float dot2 = IsoGridSquare.tempo2.dot(tempo4);
            if ((int)torchInfo.x == this.getX() && (int)torchInfo.y == this.getY() && (int)torchInfo.z == this.getZ()) {
                dot2 = -1.0f;
            }
            boolean b = false;
            if (IsoUtils.DistanceManhatten((float)this.getX(), (float)this.getY(), torchInfo.x, torchInfo.y) < torchInfo.dist && ((torchInfo.bCone && dot2 < -torchInfo.dot) || dot2 == -1.0f || (!torchInfo.bCone && dot2 < 0.8f))) {
                b = true;
            }
            if (((torchInfo.bCone && length2 < torchInfo.dist) || (!torchInfo.bCone && length2 < torchInfo.dist)) && lighting.bCanSee() && b && this.z == (int)isoPlayer.getZ()) {
                float n8 = length2 / torchInfo.dist;
                if (n8 > 1.0f) {
                    n8 = 1.0f;
                }
                if (n8 < 0.0f) {
                    n8 = 0.0f;
                }
                lighting.targetDarkMulti(lighting.targetDarkMulti() + torchInfo.strength * (1.0f - n8) * 3.0f);
                if (lighting.targetDarkMulti() > 2.5f) {
                    lighting.targetDarkMulti(2.5f);
                }
                IsoGridSquare.torchTimer = lightInfo2.time;
            }
        }
    }
    
    private LosUtil.TestResults DoDiagnalCheck(final int n, final int n2, final int n3, final boolean b) {
        final LosUtil.TestResults testVisionAdjacent = this.testVisionAdjacent(n, 0, n3, false, b);
        if (testVisionAdjacent == LosUtil.TestResults.Blocked) {
            return LosUtil.TestResults.Blocked;
        }
        final LosUtil.TestResults testVisionAdjacent2 = this.testVisionAdjacent(0, n2, n3, false, b);
        if (testVisionAdjacent2 == LosUtil.TestResults.Blocked) {
            return LosUtil.TestResults.Blocked;
        }
        if (testVisionAdjacent == LosUtil.TestResults.ClearThroughWindow || testVisionAdjacent2 == LosUtil.TestResults.ClearThroughWindow) {
            return LosUtil.TestResults.ClearThroughWindow;
        }
        return this.testVisionAdjacent(n, n2, n3, false, b);
    }
    
    boolean HasNoCharacters() {
        for (int i = 0; i < this.MovingObjects.size(); ++i) {
            if (this.MovingObjects.get(i) instanceof IsoGameCharacter) {
                return false;
            }
        }
        for (int j = 0; j < this.SpecialObjects.size(); ++j) {
            if (this.SpecialObjects.get(j) instanceof IsoBarricade) {
                return false;
            }
        }
        return true;
    }
    
    public IsoZombie getZombie() {
        for (int i = 0; i < this.MovingObjects.size(); ++i) {
            if (this.MovingObjects.get(i) instanceof IsoZombie) {
                return (IsoZombie)this.MovingObjects.get(i);
            }
        }
        return null;
    }
    
    public IsoPlayer getPlayer() {
        for (int i = 0; i < this.MovingObjects.size(); ++i) {
            if (this.MovingObjects.get(i) instanceof IsoPlayer) {
                return (IsoPlayer)this.MovingObjects.get(i);
            }
        }
        return null;
    }
    
    public static float getDarkStep() {
        return IsoGridSquare.darkStep;
    }
    
    public static void setDarkStep(final float darkStep) {
        IsoGridSquare.darkStep = darkStep;
    }
    
    public static int getRecalcLightTime() {
        return IsoGridSquare.RecalcLightTime;
    }
    
    public static void setRecalcLightTime(final int recalcLightTime) {
        IsoGridSquare.RecalcLightTime = recalcLightTime;
    }
    
    public static int getLightcache() {
        return IsoGridSquare.lightcache;
    }
    
    public static void setLightcache(final int lightcache) {
        IsoGridSquare.lightcache = lightcache;
    }
    
    public boolean isCouldSee(final int n) {
        return this.lighting[n].bCouldSee();
    }
    
    public void setCouldSee(final int n, final boolean b) {
        this.lighting[n].bCouldSee(b);
    }
    
    public boolean isCanSee(final int n) {
        return this.lighting[n].bCanSee();
    }
    
    public void setCanSee(final int n, final boolean b) {
        this.lighting[n].bCanSee(b);
    }
    
    public IsoCell getCell() {
        return IsoWorld.instance.CurrentCell;
    }
    
    public IsoGridSquare getE() {
        return this.e;
    }
    
    public void setE(final IsoGridSquare e) {
        this.e = e;
    }
    
    public ArrayList<Float> getLightInfluenceB() {
        return this.LightInfluenceB;
    }
    
    public void setLightInfluenceB(final ArrayList<Float> lightInfluenceB) {
        this.LightInfluenceB = lightInfluenceB;
    }
    
    public ArrayList<Float> getLightInfluenceG() {
        return this.LightInfluenceG;
    }
    
    public void setLightInfluenceG(final ArrayList<Float> lightInfluenceG) {
        this.LightInfluenceG = lightInfluenceG;
    }
    
    public ArrayList<Float> getLightInfluenceR() {
        return this.LightInfluenceR;
    }
    
    public void setLightInfluenceR(final ArrayList<Float> lightInfluenceR) {
        this.LightInfluenceR = lightInfluenceR;
    }
    
    public ArrayList<IsoMovingObject> getStaticMovingObjects() {
        return this.StaticMovingObjects;
    }
    
    public ArrayList<IsoMovingObject> getMovingObjects() {
        return this.MovingObjects;
    }
    
    public IsoGridSquare getN() {
        return this.n;
    }
    
    public void setN(final IsoGridSquare n) {
        this.n = n;
    }
    
    public PZArrayList<IsoObject> getObjects() {
        return this.Objects;
    }
    
    public PropertyContainer getProperties() {
        return this.Properties;
    }
    
    public IsoRoom getRoom() {
        if (this.roomID == -1) {
            return null;
        }
        return this.room;
    }
    
    public void setRoom(final IsoRoom room) {
        this.room = room;
    }
    
    public IsoBuilding getBuilding() {
        final IsoRoom room = this.getRoom();
        if (room != null) {
            return room.getBuilding();
        }
        return null;
    }
    
    public IsoGridSquare getS() {
        return this.s;
    }
    
    public void setS(final IsoGridSquare s) {
        this.s = s;
    }
    
    public ArrayList<IsoObject> getSpecialObjects() {
        return this.SpecialObjects;
    }
    
    public IsoGridSquare getW() {
        return this.w;
    }
    
    public void setW(final IsoGridSquare w) {
        this.w = w;
    }
    
    public float getLampostTotalR() {
        return this.lighting[0].lampostTotalR();
    }
    
    public void setLampostTotalR(final float n) {
        this.lighting[0].lampostTotalR(n);
    }
    
    public float getLampostTotalG() {
        return this.lighting[0].lampostTotalG();
    }
    
    public void setLampostTotalG(final float n) {
        this.lighting[0].lampostTotalG(n);
    }
    
    public float getLampostTotalB() {
        return this.lighting[0].lampostTotalB();
    }
    
    public void setLampostTotalB(final float n) {
        this.lighting[0].lampostTotalB(n);
    }
    
    public boolean isSeen(final int n) {
        return this.lighting[n].bSeen();
    }
    
    public void setIsSeen(final int n, final boolean b) {
        this.lighting[n].bSeen(b);
    }
    
    public float getDarkMulti(final int n) {
        return this.lighting[n].darkMulti();
    }
    
    public void setDarkMulti(final int n, final float n2) {
        this.lighting[n].darkMulti(n2);
    }
    
    public float getTargetDarkMulti(final int n) {
        return this.lighting[n].targetDarkMulti();
    }
    
    public void setTargetDarkMulti(final int n, final float n2) {
        this.lighting[n].targetDarkMulti(n2);
    }
    
    public void setX(final int x) {
        this.x = x;
        this.CachedScreenValue = -1;
    }
    
    public void setY(final int y) {
        this.y = y;
        this.CachedScreenValue = -1;
    }
    
    public void setZ(final int z) {
        this.z = z;
        this.CachedScreenValue = -1;
    }
    
    public ArrayList<IsoGameCharacter> getDeferedCharacters() {
        return this.DeferedCharacters;
    }
    
    public void addDeferredCharacter(final IsoGameCharacter e) {
        if (this.DeferredCharacterTick != this.getCell().DeferredCharacterTick) {
            if (!this.DeferedCharacters.isEmpty()) {
                this.DeferedCharacters.clear();
            }
            this.DeferredCharacterTick = this.getCell().DeferredCharacterTick;
        }
        this.DeferedCharacters.add(e);
    }
    
    public boolean isCacheIsFree() {
        return this.CacheIsFree;
    }
    
    public void setCacheIsFree(final boolean cacheIsFree) {
        this.CacheIsFree = cacheIsFree;
    }
    
    public boolean isCachedIsFree() {
        return this.CachedIsFree;
    }
    
    public void setCachedIsFree(final boolean cachedIsFree) {
        this.CachedIsFree = cachedIsFree;
    }
    
    public static boolean isbDoSlowPathfinding() {
        return IsoGridSquare.bDoSlowPathfinding;
    }
    
    public static void setbDoSlowPathfinding(final boolean bDoSlowPathfinding) {
        IsoGridSquare.bDoSlowPathfinding = bDoSlowPathfinding;
    }
    
    public boolean isSolidFloorCached() {
        return this.SolidFloorCached;
    }
    
    public void setSolidFloorCached(final boolean solidFloorCached) {
        this.SolidFloorCached = solidFloorCached;
    }
    
    public boolean isSolidFloor() {
        return this.SolidFloor;
    }
    
    public void setSolidFloor(final boolean solidFloor) {
        this.SolidFloor = solidFloor;
    }
    
    public static ColorInfo getDefColorInfo() {
        return IsoGridSquare.defColorInfo;
    }
    
    public boolean isOutside() {
        return this.Properties.Is(IsoFlagType.exterior);
    }
    
    public boolean HasPushable() {
        for (int size = this.MovingObjects.size(), i = 0; i < size; ++i) {
            if (this.MovingObjects.get(i) instanceof IsoPushableObject) {
                return true;
            }
        }
        return false;
    }
    
    public void setRoomID(final int roomID) {
        this.roomID = roomID;
        if (roomID != -1) {
            this.getProperties().UnSet(IsoFlagType.exterior);
            this.room = this.chunk.getRoom(roomID);
        }
    }
    
    public int getRoomID() {
        return this.roomID;
    }
    
    public boolean getCanSee(final int n) {
        return this.lighting[n].bCanSee();
    }
    
    public boolean getSeen(final int n) {
        return this.lighting[n].bSeen();
    }
    
    public IsoChunk getChunk() {
        return this.chunk;
    }
    
    public IsoObject getDoorOrWindow(final boolean b) {
        for (int i = this.SpecialObjects.size() - 1; i >= 0; --i) {
            final IsoObject isoObject = this.SpecialObjects.get(i);
            if (isoObject instanceof IsoDoor && ((IsoDoor)isoObject).north == b) {
                return isoObject;
            }
            if (isoObject instanceof IsoThumpable && ((IsoThumpable)isoObject).north == b && (((IsoThumpable)isoObject).isDoor() || ((IsoThumpable)isoObject).isWindow())) {
                return isoObject;
            }
            if (isoObject instanceof IsoWindow && ((IsoWindow)isoObject).north == b) {
                return isoObject;
            }
        }
        return null;
    }
    
    public IsoObject getDoorOrWindowOrWindowFrame(final IsoDirections isoDirections, final boolean b) {
        for (int i = this.Objects.size() - 1; i >= 0; --i) {
            final IsoObject isoObject = this.Objects.get(i);
            final IsoDoor isoDoor = Type.tryCastTo(isoObject, IsoDoor.class);
            final IsoThumpable isoThumpable = Type.tryCastTo(isoObject, IsoThumpable.class);
            final IsoWindow isoWindow = Type.tryCastTo(isoObject, IsoWindow.class);
            if (isoDoor != null && isoDoor.getSpriteEdge(b) == isoDirections) {
                return isoObject;
            }
            if (isoThumpable != null && isoThumpable.getSpriteEdge(b) == isoDirections) {
                return isoObject;
            }
            if (isoWindow != null) {
                if (isoWindow.north && isoDirections == IsoDirections.N) {
                    return isoObject;
                }
                if (!isoWindow.north && isoDirections == IsoDirections.W) {
                    return isoObject;
                }
            }
            if (IsoWindowFrame.isWindowFrame(isoObject)) {
                if (IsoWindowFrame.isWindowFrame(isoObject, true) && isoDirections == IsoDirections.N) {
                    return isoObject;
                }
                if (IsoWindowFrame.isWindowFrame(isoObject, false) && isoDirections == IsoDirections.W) {
                    return isoObject;
                }
            }
        }
        return null;
    }
    
    public IsoObject getOpenDoor(final IsoDirections isoDirections) {
        for (int i = 0; i < this.SpecialObjects.size(); ++i) {
            final IsoObject isoObject = this.SpecialObjects.get(i);
            final IsoDoor isoDoor = Type.tryCastTo(isoObject, IsoDoor.class);
            final IsoThumpable isoThumpable = Type.tryCastTo(isoObject, IsoThumpable.class);
            if (isoDoor != null && isoDoor.open && isoDoor.getSpriteEdge(false) == isoDirections) {
                return isoDoor;
            }
            if (isoThumpable != null && isoThumpable.open && isoThumpable.getSpriteEdge(false) == isoDirections) {
                return isoThumpable;
            }
        }
        return null;
    }
    
    public void removeWorldObject(final IsoWorldInventoryObject isoWorldInventoryObject) {
        if (isoWorldInventoryObject == null) {
            return;
        }
        isoWorldInventoryObject.removeFromWorld();
        isoWorldInventoryObject.removeFromSquare();
    }
    
    public void removeAllWorldObjects() {
        for (int i = 0; i < this.getWorldObjects().size(); --i, ++i) {
            final IsoWorldInventoryObject isoWorldInventoryObject = this.getWorldObjects().get(i);
            isoWorldInventoryObject.removeFromWorld();
            isoWorldInventoryObject.removeFromSquare();
        }
    }
    
    public ArrayList<IsoWorldInventoryObject> getWorldObjects() {
        return this.WorldObjects;
    }
    
    public PZArrayList<IsoObject> getLocalTemporaryObjects() {
        return this.localTemporaryObjects;
    }
    
    public KahluaTable getModData() {
        if (this.table == null) {
            this.table = LuaManager.platform.newTable();
        }
        return this.table;
    }
    
    public boolean hasModData() {
        return this.table != null && !this.table.isEmpty();
    }
    
    public ZomboidBitFlag getHasTypes() {
        return this.hasTypes;
    }
    
    public void setVertLight(final int n, final int n2, final int n3) {
        this.lighting[n3].lightverts(n, n2);
    }
    
    public int getVertLight(final int n, final int n2) {
        return this.lighting[n2].lightverts(n);
    }
    
    public void setRainDrop(final IsoRaindrop rainDrop) {
        this.RainDrop = rainDrop;
    }
    
    public IsoRaindrop getRainDrop() {
        return this.RainDrop;
    }
    
    public void setRainSplash(final IsoRainSplash rainSplash) {
        this.RainSplash = rainSplash;
    }
    
    public IsoRainSplash getRainSplash() {
        return this.RainSplash;
    }
    
    public IsoMetaGrid.Zone getZone() {
        return this.zone;
    }
    
    public String getZoneType() {
        if (this.zone != null) {
            return this.zone.getType();
        }
        return null;
    }
    
    public boolean isOverlayDone() {
        return this.overlayDone;
    }
    
    public void setOverlayDone(final boolean overlayDone) {
        this.overlayDone = overlayDone;
    }
    
    public ErosionData.Square getErosionData() {
        if (this.erosion == null) {
            this.erosion = new ErosionData.Square();
        }
        return this.erosion;
    }
    
    public void disableErosion() {
        final ErosionData.Square erosionData = this.getErosionData();
        if (erosionData != null && !erosionData.doNothing) {
            erosionData.doNothing = true;
        }
    }
    
    public void removeErosionObject(final String anObject) {
        if (this.erosion == null) {
            return;
        }
        if ("WallVines".equals(anObject)) {
            for (int i = 0; i < this.erosion.regions.size(); ++i) {
                final ErosionCategory.Data data = this.erosion.regions.get(i);
                if (data.regionID == 2 && data.categoryID == 0) {
                    this.erosion.regions.remove(i);
                    break;
                }
            }
        }
    }
    
    public void syncIsoTrap(final HandWeapon handWeapon) {
        final ByteBufferWriter startPacket = GameClient.connection.startPacket();
        PacketTypes.PacketType.AddExplosiveTrap.doPacket(startPacket);
        startPacket.putInt(this.getX());
        startPacket.putInt(this.getY());
        startPacket.putInt(this.getZ());
        try {
            handWeapon.saveWithSize(startPacket.bb, false);
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
        PacketTypes.PacketType.AddExplosiveTrap.send(GameClient.connection);
    }
    
    public void drawCircleExplosion(int n, final IsoTrap isoTrap, final IsoTrap.ExplosionMode explosionMode) {
        if (n > 15) {
            n = 15;
        }
        for (int i = this.getX() - n; i <= this.getX() + n; ++i) {
            for (int j = this.getY() - n; j <= this.getY() + n; ++j) {
                if (IsoUtils.DistanceTo(i + 0.5f, j + 0.5f, this.getX() + 0.5f, this.getY() + 0.5f) <= n) {
                    final LosUtil.TestResults lineClear = LosUtil.lineClear(this.getCell(), (int)isoTrap.getX(), (int)isoTrap.getY(), (int)isoTrap.getZ(), i, j, this.z, false);
                    if (lineClear != LosUtil.TestResults.Blocked) {
                        if (lineClear != LosUtil.TestResults.ClearThroughClosedDoor) {
                            final IsoGridSquare gridSquare = this.getCell().getGridSquare(i, j, this.getZ());
                            if (gridSquare != null) {
                                if (explosionMode == IsoTrap.ExplosionMode.Smoke) {
                                    if (!GameClient.bClient && Rand.Next(2) == 0) {
                                        IsoFireManager.StartSmoke(this.getCell(), gridSquare, true, 40, 0);
                                    }
                                    gridSquare.smoke();
                                }
                                if (explosionMode == IsoTrap.ExplosionMode.Explosion) {
                                    if (!GameClient.bClient && isoTrap.getExplosionPower() > 0 && Rand.Next(80 - isoTrap.getExplosionPower()) <= 0) {
                                        gridSquare.Burn();
                                    }
                                    gridSquare.explosion(isoTrap);
                                    if (!GameClient.bClient && isoTrap.getExplosionPower() > 0 && Rand.Next(100 - isoTrap.getExplosionPower()) == 0) {
                                        IsoFireManager.StartFire(this.getCell(), gridSquare, true, 20);
                                    }
                                }
                                if (explosionMode == IsoTrap.ExplosionMode.Fire && !GameClient.bClient && Rand.Next(100 - isoTrap.getFirePower()) == 0) {
                                    IsoFireManager.StartFire(this.getCell(), gridSquare, true, 40);
                                }
                                if (explosionMode == IsoTrap.ExplosionMode.Sensor) {
                                    gridSquare.setTrapPositionX(this.getX());
                                    gridSquare.setTrapPositionY(this.getY());
                                    gridSquare.setTrapPositionZ(this.getZ());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void explosion(final IsoTrap isoTrap) {
        if (GameServer.bServer && isoTrap.isInstantExplosion()) {
            return;
        }
        for (int i = 0; i < this.getMovingObjects().size(); ++i) {
            final IsoMovingObject isoMovingObject = this.getMovingObjects().get(i);
            if (isoMovingObject instanceof IsoGameCharacter) {
                if (GameServer.bServer || !(isoMovingObject instanceof IsoZombie) || !((IsoZombie)isoMovingObject).isRemoteZombie()) {
                    final int min = Math.min(isoTrap.getExplosionPower(), 80);
                    isoMovingObject.Hit((HandWeapon)InventoryItemFactory.CreateItem("Base.Axe"), IsoWorld.instance.CurrentCell.getFakeZombieForHit(), Rand.Next(min / 30.0f, min / 30.0f * 2.0f) + isoTrap.getExtraDamage(), false, 1.0f);
                    if (isoTrap.getExplosionPower() > 0) {
                        for (int j = (isoMovingObject instanceof IsoZombie) ? 0 : 1; j != 0; j = 1) {
                            j = 0;
                            ((IsoZombie)isoMovingObject).getBodyDamage().getBodyPart(BodyPartType.FromIndex(Rand.Next(15))).setBurned();
                            if (Rand.Next((100 - min) / 2) == 0) {}
                        }
                    }
                }
                if (GameClient.bClient && isoMovingObject instanceof IsoZombie && ((IsoZombie)isoMovingObject).isRemoteZombie()) {
                    isoMovingObject.Hit((HandWeapon)InventoryItemFactory.CreateItem("Base.Axe"), IsoWorld.instance.CurrentCell.getFakeZombieForHit(), 0.0f, true, 0.0f);
                }
            }
        }
    }
    
    public void smoke() {
        for (int i = 0; i < this.getMovingObjects().size(); ++i) {
            final IsoMovingObject isoMovingObject = this.getMovingObjects().get(i);
            if (isoMovingObject instanceof IsoZombie) {
                ((IsoZombie)isoMovingObject).setTarget(null);
                ((IsoZombie)isoMovingObject).changeState(ZombieIdleState.instance());
            }
        }
    }
    
    public void explodeTrap() {
        final IsoGridSquare gridSquare = this.getCell().getGridSquare(this.getTrapPositionX(), this.getTrapPositionY(), this.getTrapPositionZ());
        if (gridSquare != null) {
            for (int i = 0; i < gridSquare.getObjects().size(); ++i) {
                final IsoObject isoObject = gridSquare.getObjects().get(i);
                if (isoObject instanceof IsoTrap) {
                    final IsoTrap isoTrap = (IsoTrap)isoObject;
                    isoTrap.triggerExplosion(false);
                    for (int sensorRange = isoTrap.getSensorRange(), j = gridSquare.getX() - sensorRange; j <= gridSquare.getX() + sensorRange; ++j) {
                        for (int k = gridSquare.getY() - sensorRange; k <= gridSquare.getY() + sensorRange; ++k) {
                            if (IsoUtils.DistanceTo(j + 0.5f, k + 0.5f, gridSquare.getX() + 0.5f, gridSquare.getY() + 0.5f) <= sensorRange) {
                                final IsoGridSquare gridSquare2 = this.getCell().getGridSquare(j, k, this.getZ());
                                if (gridSquare2 != null) {
                                    gridSquare2.setTrapPositionX(-1);
                                    gridSquare2.setTrapPositionY(-1);
                                    gridSquare2.setTrapPositionZ(-1);
                                }
                            }
                        }
                    }
                    return;
                }
            }
        }
    }
    
    public int getTrapPositionX() {
        return this.trapPositionX;
    }
    
    public void setTrapPositionX(final int trapPositionX) {
        this.trapPositionX = trapPositionX;
    }
    
    public int getTrapPositionY() {
        return this.trapPositionY;
    }
    
    public void setTrapPositionY(final int trapPositionY) {
        this.trapPositionY = trapPositionY;
    }
    
    public int getTrapPositionZ() {
        return this.trapPositionZ;
    }
    
    public void setTrapPositionZ(final int trapPositionZ) {
        this.trapPositionZ = trapPositionZ;
    }
    
    public boolean haveElectricity() {
        return ((this.chunk == null || !this.chunk.bLoaded) && this.haveElectricity) || ((SandboxOptions.getInstance().AllowExteriorGenerator.getValue() || !this.Is(IsoFlagType.exterior)) && this.chunk != null && this.chunk.isGeneratorPoweringSquare(this.x, this.y, this.z));
    }
    
    public void setHaveElectricity(final boolean b) {
        if (!b) {
            this.haveElectricity = false;
        }
        if (this.getObjects() != null) {
            for (int i = 0; i < this.getObjects().size(); ++i) {
                if (this.getObjects().get(i) instanceof IsoLightSwitch) {
                    ((IsoLightSwitch)this.getObjects().get(i)).update();
                }
            }
        }
    }
    
    public IsoGenerator getGenerator() {
        if (this.getSpecialObjects() != null) {
            for (int i = 0; i < this.getSpecialObjects().size(); ++i) {
                if (this.getSpecialObjects().get(i) instanceof IsoGenerator) {
                    return (IsoGenerator)this.getSpecialObjects().get(i);
                }
            }
        }
        return null;
    }
    
    public void stopFire() {
        IsoFireManager.RemoveAllOn(this);
        this.getProperties().Set(IsoFlagType.burntOut);
        this.getProperties().UnSet(IsoFlagType.burning);
        this.burntOut = true;
    }
    
    public void transmitStopFire() {
        if (GameClient.bClient) {
            GameClient.sendStopFire(this);
        }
    }
    
    public long playSound(final String s) {
        return IsoWorld.instance.getFreeEmitter(this.x + 0.5f, this.y + 0.5f, (float)this.z).playSound(s);
    }
    
    @Deprecated
    public long playSound(final String s, final boolean b) {
        return IsoWorld.instance.getFreeEmitter(this.x + 0.5f, this.y + 0.5f, (float)this.z).playSound(s, b);
    }
    
    public void FixStackableObjects() {
        IsoObject isoObject = null;
        for (int i = 0; i < this.Objects.size(); ++i) {
            final IsoObject isoObject2 = this.Objects.get(i);
            if (!(isoObject2 instanceof IsoWorldInventoryObject)) {
                if (isoObject2.sprite != null) {
                    PropertyContainer propertyContainer = isoObject2.sprite.getProperties();
                    if (propertyContainer.getStackReplaceTileOffset() != 0) {
                        isoObject2.sprite = IsoSprite.getSprite(IsoSpriteManager.instance, isoObject2.sprite.ID + propertyContainer.getStackReplaceTileOffset());
                        if (isoObject2.sprite == null) {
                            continue;
                        }
                        propertyContainer = isoObject2.sprite.getProperties();
                    }
                    if (propertyContainer.isTable() || propertyContainer.isTableTop()) {
                        final float n = propertyContainer.isSurfaceOffset() ? ((float)propertyContainer.getSurface()) : 0.0f;
                        if (isoObject != null) {
                            isoObject2.setRenderYOffset(isoObject.getRenderYOffset() + isoObject.getSurfaceOffset() - n);
                        }
                        else {
                            isoObject2.setRenderYOffset(0.0f - n);
                        }
                    }
                    if (propertyContainer.isTable()) {
                        isoObject = isoObject2;
                    }
                    if (isoObject2 instanceof IsoLightSwitch) {
                        if (propertyContainer.isTableTop() && isoObject != null && !propertyContainer.Is("IgnoreSurfaceSnap")) {
                            final int tryParseInt = PZMath.tryParseInt(propertyContainer.Val("Noffset"), 0);
                            final int tryParseInt2 = PZMath.tryParseInt(propertyContainer.Val("Soffset"), 0);
                            final int tryParseInt3 = PZMath.tryParseInt(propertyContainer.Val("Woffset"), 0);
                            final int tryParseInt4 = PZMath.tryParseInt(propertyContainer.Val("Eoffset"), 0);
                            final String val = propertyContainer.Val("Facing");
                            final String val2 = isoObject.getProperties().Val("Facing");
                            if (!StringUtils.isNullOrWhitespace(val2)) {
                                if (!val2.equals(val)) {
                                    int n2 = 0;
                                    if ("N".equals(val2)) {
                                        if (tryParseInt != 0) {
                                            n2 = tryParseInt;
                                        }
                                        else if (tryParseInt2 != 0) {
                                            n2 = tryParseInt2;
                                        }
                                    }
                                    else if ("S".equals(val2)) {
                                        if (tryParseInt2 != 0) {
                                            n2 = tryParseInt2;
                                        }
                                        else if (tryParseInt != 0) {
                                            n2 = tryParseInt;
                                        }
                                    }
                                    else if ("W".equals(val2)) {
                                        if (tryParseInt3 != 0) {
                                            n2 = tryParseInt3;
                                        }
                                        else if (tryParseInt4 != 0) {
                                            n2 = tryParseInt4;
                                        }
                                    }
                                    else if ("E".equals(val2)) {
                                        if (tryParseInt4 != 0) {
                                            n2 = tryParseInt4;
                                        }
                                        else if (tryParseInt3 != 0) {
                                            n2 = tryParseInt3;
                                        }
                                    }
                                    if (n2 != 0) {
                                        final IsoSprite sprite = IsoSpriteManager.instance.getSprite(isoObject2.sprite.ID + n2);
                                        if (sprite != null) {
                                            isoObject2.setSprite(sprite);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public BaseVehicle getVehicleContainer() {
        final int n = (int)((this.x - 4.0f) / 10.0f);
        final int n2 = (int)((this.y - 4.0f) / 10.0f);
        final int n3 = (int)Math.ceil((this.x + 4.0f) / 10.0f);
        for (int n4 = (int)Math.ceil((this.y + 4.0f) / 10.0f), i = n2; i < n4; ++i) {
            for (int j = n; j < n3; ++j) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(j, i) : IsoWorld.instance.CurrentCell.getChunk(j, i);
                if (isoChunk != null) {
                    for (int k = 0; k < isoChunk.vehicles.size(); ++k) {
                        final BaseVehicle baseVehicle = isoChunk.vehicles.get(k);
                        if (baseVehicle.isIntersectingSquare(this.x, this.y, this.z)) {
                            return baseVehicle;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public boolean isVehicleIntersecting() {
        final int n = (int)((this.x - 4.0f) / 10.0f);
        final int n2 = (int)((this.y - 4.0f) / 10.0f);
        final int n3 = (int)Math.ceil((this.x + 4.0f) / 10.0f);
        for (int n4 = (int)Math.ceil((this.y + 4.0f) / 10.0f), i = n2; i < n4; ++i) {
            for (int j = n; j < n3; ++j) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(j, i) : IsoWorld.instance.CurrentCell.getChunk(j, i);
                if (isoChunk != null) {
                    for (int k = 0; k < isoChunk.vehicles.size(); ++k) {
                        if (isoChunk.vehicles.get(k).isIntersectingSquare(this.x, this.y, this.z)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public IsoCompost getCompost() {
        if (this.getSpecialObjects() != null) {
            for (int i = 0; i < this.getSpecialObjects().size(); ++i) {
                if (this.getSpecialObjects().get(i) instanceof IsoCompost) {
                    return (IsoCompost)this.getSpecialObjects().get(i);
                }
            }
        }
        return null;
    }
    
    public void setIsoWorldRegion(final IsoWorldRegion isoWorldRegion) {
        this.hasSetIsoWorldRegion = (isoWorldRegion != null);
        this.isoWorldRegion = isoWorldRegion;
    }
    
    public IWorldRegion getIsoWorldRegion() {
        if (GameServer.bServer) {
            return IsoRegions.getIsoWorldRegion(this.x, this.y, this.z);
        }
        if (!this.hasSetIsoWorldRegion) {
            this.isoWorldRegion = IsoRegions.getIsoWorldRegion(this.x, this.y, this.z);
            this.hasSetIsoWorldRegion = true;
        }
        return this.isoWorldRegion;
    }
    
    public void ResetIsoWorldRegion() {
        this.isoWorldRegion = null;
        this.hasSetIsoWorldRegion = false;
    }
    
    public boolean isInARoom() {
        return this.getRoom() != null || (this.getIsoWorldRegion() != null && this.getIsoWorldRegion().isPlayerRoom());
    }
    
    public int getWallType() {
        int n = 0;
        if (this.getProperties().Is(IsoFlagType.WallN)) {
            n |= 0x1;
        }
        if (this.getProperties().Is(IsoFlagType.WallW)) {
            n |= 0x4;
        }
        if (this.getProperties().Is(IsoFlagType.WallNW)) {
            n |= 0x5;
        }
        final IsoGridSquare isoGridSquare = this.nav[IsoDirections.E.index()];
        if (isoGridSquare != null && (isoGridSquare.getProperties().Is(IsoFlagType.WallW) || isoGridSquare.getProperties().Is(IsoFlagType.WallNW))) {
            n |= 0x8;
        }
        final IsoGridSquare isoGridSquare2 = this.nav[IsoDirections.S.index()];
        if (isoGridSquare2 != null && (isoGridSquare2.getProperties().Is(IsoFlagType.WallN) || isoGridSquare2.getProperties().Is(IsoFlagType.WallNW))) {
            n |= 0x2;
        }
        return n;
    }
    
    public int getPuddlesDir() {
        byte b = PuddlesDirection.PUDDLES_DIR_ALL;
        if (this.isInARoom()) {
            return PuddlesDirection.PUDDLES_DIR_NONE;
        }
        for (int i = 0; i < this.getObjects().size(); ++i) {
            final IsoObject isoObject = this.getObjects().get(i);
            if (isoObject.AttachedAnimSprite != null) {
                for (int j = 0; j < isoObject.AttachedAnimSprite.size(); ++j) {
                    final IsoSprite parentSprite = isoObject.AttachedAnimSprite.get(j).parentSprite;
                    if (parentSprite.name != null) {
                        if (parentSprite.name.equals("street_trafficlines_01_2") || parentSprite.name.equals("street_trafficlines_01_6") || parentSprite.name.equals("street_trafficlines_01_22") || parentSprite.name.equals("street_trafficlines_01_32")) {
                            b = PuddlesDirection.PUDDLES_DIR_NW;
                        }
                        if (parentSprite.name.equals("street_trafficlines_01_4") || parentSprite.name.equals("street_trafficlines_01_0") || parentSprite.name.equals("street_trafficlines_01_16")) {
                            b = PuddlesDirection.PUDDLES_DIR_NE;
                        }
                    }
                }
            }
        }
        return b;
    }
    
    public boolean haveFire() {
        final int size = this.Objects.size();
        final IsoObject[] array = this.Objects.getElements();
        for (int i = 0; i < size; ++i) {
            if (array[i] instanceof IsoFire) {
                return true;
            }
        }
        return false;
    }
    
    public IsoBuilding getRoofHideBuilding() {
        return this.roofHideBuilding;
    }
    
    public IsoGridSquare getAdjacentSquare(final IsoDirections isoDirections) {
        return this.nav[isoDirections.index()];
    }
    
    public IsoGridSquare getAdjacentPathSquare(final IsoDirections isoDirections) {
        switch (isoDirections) {
            case NW: {
                return this.nw;
            }
            case N: {
                return this.n;
            }
            case NE: {
                return this.ne;
            }
            case W: {
                return this.w;
            }
            case E: {
                return this.e;
            }
            case SW: {
                return this.sw;
            }
            case S: {
                return this.s;
            }
            case SE: {
                return this.se;
            }
            default: {
                return null;
            }
        }
    }
    
    public float getApparentZ(float clamp, float clamp2) {
        clamp = PZMath.clamp(clamp, 0.0f, 1.0f);
        clamp2 = PZMath.clamp(clamp2, 0.0f, 1.0f);
        if (this.Has(IsoObjectType.stairsTN)) {
            return this.getZ() + PZMath.lerp(0.6666f, 1.0f, 1.0f - clamp2);
        }
        if (this.Has(IsoObjectType.stairsTW)) {
            return this.getZ() + PZMath.lerp(0.6666f, 1.0f, 1.0f - clamp);
        }
        if (this.Has(IsoObjectType.stairsMN)) {
            return this.getZ() + PZMath.lerp(0.3333f, 0.6666f, 1.0f - clamp2);
        }
        if (this.Has(IsoObjectType.stairsMW)) {
            return this.getZ() + PZMath.lerp(0.3333f, 0.6666f, 1.0f - clamp);
        }
        if (this.Has(IsoObjectType.stairsBN)) {
            return this.getZ() + PZMath.lerp(0.01f, 0.3333f, 1.0f - clamp2);
        }
        if (this.Has(IsoObjectType.stairsBW)) {
            return this.getZ() + PZMath.lerp(0.01f, 0.3333f, 1.0f - clamp);
        }
        return (float)this.getZ();
    }
    
    public float getTotalWeightOfItemsOnFloor() {
        float n = 0.0f;
        for (int i = 0; i < this.WorldObjects.size(); ++i) {
            final InventoryItem item = this.WorldObjects.get(i).getItem();
            if (item != null) {
                n += item.getUnequippedWeight();
            }
        }
        return n;
    }
    
    public boolean getCollideMatrix(final int n, final int n2, final int n3) {
        return getMatrixBit(this.collideMatrix, n + 1, n2 + 1, n3 + 1);
    }
    
    public boolean getPathMatrix(final int n, final int n2, final int n3) {
        return getMatrixBit(this.pathMatrix, n + 1, n2 + 1, n3 + 1);
    }
    
    public boolean getVisionMatrix(final int n, final int n2, final int n3) {
        return getMatrixBit(this.visionMatrix, n + 1, n2 + 1, n3 + 1);
    }
    
    public void checkRoomSeen(final int n) {
        final IsoRoom room = this.getRoom();
        if (room == null || room.def == null || room.def.bExplored) {
            return;
        }
        final IsoPlayer isoPlayer = IsoPlayer.players[n];
        if (isoPlayer == null) {
            return;
        }
        if (this.z != (int)isoPlayer.z) {
            return;
        }
        int n2 = 10;
        if (isoPlayer.getBuilding() == room.building) {
            n2 = 50;
        }
        if (IsoUtils.DistanceToSquared(isoPlayer.x, isoPlayer.y, this.x + 0.5f, this.y + 0.5f) < n2 * n2) {
            room.def.bExplored = true;
            room.onSee();
            room.seen = 0;
        }
    }
    
    public boolean hasFlies() {
        return this.bHasFlies;
    }
    
    public void setHasFlies(final boolean bHasFlies) {
        this.bHasFlies = bHasFlies;
    }
    
    public float getLightLevel(final int n) {
        return (this.lighting[n].lightInfo().r + this.lighting[n].lightInfo().g + this.lighting[n].lightInfo().b) / 3.0f;
    }
    
    static {
        IsoGridSquare.torchTimer = 0L;
        isoGridSquareCache = new ConcurrentLinkedQueue<IsoGridSquare>();
        IsoGridSquare.gridSquareCacheEmptyTimer = 0;
        IsoGridSquare.darkStep = 0.06f;
        IsoGridSquare.RecalcLightTime = 0;
        IsoGridSquare.lightcache = 0;
        choices = new ArrayList<IsoGridSquare>();
        IsoGridSquare.USE_WALL_SHADER = true;
        lightInfoTemp = new ColorInfo();
        IsoGridSquare.lastLoaded = null;
        IsoGridSquare.IDMax = -1;
        IsoGridSquare.col = -1;
        IsoGridSquare.path = -1;
        IsoGridSquare.pathdoor = -1;
        IsoGridSquare.vision = -1;
        tr = new Color(1, 1, 1, 1);
        tl = new Color(1, 1, 1, 1);
        br = new Color(1, 1, 1, 1);
        bl = new Color(1, 1, 1, 1);
        interp1 = new Color(1, 1, 1, 1);
        interp2 = new Color(1, 1, 1, 1);
        finalCol = new Color(1, 1, 1, 1);
        cellGetSquare = new CellGetSquare();
        IsoGridSquare.UseSlowCollision = false;
        IsoGridSquare.bDoSlowPathfinding = false;
        comp = ((isoMovingObject, isoMovingObject2) -> isoMovingObject.compareToY(isoMovingObject2));
        IsoGridSquare.isOnScreenLast = false;
        IsoGridSquare.rainsplashCache = new String[50];
        defColorInfo = new ColorInfo();
        blackColorInfo = new ColorInfo();
        IsoGridSquare.colu = 0;
        IsoGridSquare.coll = 0;
        IsoGridSquare.colr = 0;
        IsoGridSquare.colu2 = 0;
        IsoGridSquare.coll2 = 0;
        IsoGridSquare.colr2 = 0;
        IsoGridSquare.CircleStencil = false;
        IsoGridSquare.rmod = 0.0f;
        IsoGridSquare.gmod = 0.0f;
        IsoGridSquare.bmod = 0.0f;
        tempo = new Vector2();
        tempo2 = new Vector2();
    }
    
    public static final class ResultLight
    {
        public int id;
        public int x;
        public int y;
        public int z;
        public int radius;
        public float r;
        public float g;
        public float b;
        public static final int RLF_NONE = 0;
        public static final int RLF_ROOMLIGHT = 1;
        public static final int RLF_TORCH = 2;
        public int flags;
        
        public ResultLight copyFrom(final ResultLight resultLight) {
            this.id = resultLight.id;
            this.x = resultLight.x;
            this.y = resultLight.y;
            this.z = resultLight.z;
            this.radius = resultLight.radius;
            this.r = resultLight.r;
            this.g = resultLight.g;
            this.b = resultLight.b;
            this.flags = resultLight.flags;
            return this;
        }
    }
    
    public static final class Lighting implements ILighting
    {
        private final int[] lightverts;
        private float lampostTotalR;
        private float lampostTotalG;
        private float lampostTotalB;
        private boolean bSeen;
        private boolean bCanSee;
        private boolean bCouldSee;
        private float darkMulti;
        private float targetDarkMulti;
        private final ColorInfo lightInfo;
        
        public Lighting() {
            this.lightverts = new int[8];
            this.lampostTotalR = 0.0f;
            this.lampostTotalG = 0.0f;
            this.lampostTotalB = 0.0f;
            this.lightInfo = new ColorInfo();
        }
        
        @Override
        public int lightverts(final int n) {
            return this.lightverts[n];
        }
        
        @Override
        public float lampostTotalR() {
            return this.lampostTotalR;
        }
        
        @Override
        public float lampostTotalG() {
            return this.lampostTotalG;
        }
        
        @Override
        public float lampostTotalB() {
            return this.lampostTotalB;
        }
        
        @Override
        public boolean bSeen() {
            return this.bSeen;
        }
        
        @Override
        public boolean bCanSee() {
            return this.bCanSee;
        }
        
        @Override
        public boolean bCouldSee() {
            return this.bCouldSee;
        }
        
        @Override
        public float darkMulti() {
            return this.darkMulti;
        }
        
        @Override
        public float targetDarkMulti() {
            return this.targetDarkMulti;
        }
        
        @Override
        public ColorInfo lightInfo() {
            return this.lightInfo;
        }
        
        @Override
        public void lightverts(final int n, final int n2) {
            this.lightverts[n] = n2;
        }
        
        @Override
        public void lampostTotalR(final float lampostTotalR) {
            this.lampostTotalR = lampostTotalR;
        }
        
        @Override
        public void lampostTotalG(final float lampostTotalG) {
            this.lampostTotalG = lampostTotalG;
        }
        
        @Override
        public void lampostTotalB(final float lampostTotalB) {
            this.lampostTotalB = lampostTotalB;
        }
        
        @Override
        public void bSeen(final boolean bSeen) {
            this.bSeen = bSeen;
        }
        
        @Override
        public void bCanSee(final boolean bCanSee) {
            this.bCanSee = bCanSee;
        }
        
        @Override
        public void bCouldSee(final boolean bCouldSee) {
            this.bCouldSee = bCouldSee;
        }
        
        @Override
        public void darkMulti(final float darkMulti) {
            this.darkMulti = darkMulti;
        }
        
        @Override
        public void targetDarkMulti(final float targetDarkMulti) {
            this.targetDarkMulti = targetDarkMulti;
        }
        
        @Override
        public int resultLightCount() {
            return 0;
        }
        
        @Override
        public ResultLight getResultLight(final int n) {
            return null;
        }
        
        @Override
        public void reset() {
            this.lampostTotalR = 0.0f;
            this.lampostTotalG = 0.0f;
            this.lampostTotalB = 0.0f;
            this.bSeen = false;
            this.bCouldSee = false;
            this.bCanSee = false;
            this.targetDarkMulti = 0.0f;
            this.darkMulti = 0.0f;
            this.lightInfo.r = 0.0f;
            this.lightInfo.g = 0.0f;
            this.lightInfo.b = 0.0f;
            this.lightInfo.a = 1.0f;
        }
    }
    
    public static class CellGetSquare implements GetSquare
    {
        @Override
        public IsoGridSquare getGridSquare(final int n, final int n2, final int n3) {
            return IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        }
    }
    
    public static final class CircleStencilShader extends Shader
    {
        public static final CircleStencilShader instance;
        public int a_wallShadeColor;
        
        public CircleStencilShader() {
            super("CircleStencil");
            this.a_wallShadeColor = -1;
        }
        
        @Override
        protected void onCompileSuccess(final ShaderProgram shaderProgram) {
            this.Start();
            this.a_wallShadeColor = GL20.glGetAttribLocation(this.getID(), (CharSequence)"a_wallShadeColor");
            shaderProgram.setSamplerUnit("texture", 0);
            shaderProgram.setSamplerUnit("CutawayStencil", 1);
            this.End();
        }
        
        static {
            instance = new CircleStencilShader();
        }
    }
    
    public static final class NoCircleStencilShader
    {
        public static final NoCircleStencilShader instance;
        private ShaderProgram shaderProgram;
        public int ShaderID;
        public int a_wallShadeColor;
        
        public NoCircleStencilShader() {
            this.ShaderID = -1;
            this.a_wallShadeColor = -1;
        }
        
        private void initShader() {
            this.shaderProgram = ShaderProgram.createShaderProgram("NoCircleStencil", false, true);
            if (this.shaderProgram.isCompiled()) {
                this.ShaderID = this.shaderProgram.getShaderID();
                this.a_wallShadeColor = GL20.glGetAttribLocation(this.ShaderID, (CharSequence)"a_wallShadeColor");
            }
        }
        
        static {
            instance = new NoCircleStencilShader();
        }
    }
    
    public static class PuddlesDirection
    {
        public static byte PUDDLES_DIR_NONE;
        public static byte PUDDLES_DIR_NE;
        public static byte PUDDLES_DIR_NW;
        public static byte PUDDLES_DIR_ALL;
        
        static {
            PuddlesDirection.PUDDLES_DIR_NONE = 1;
            PuddlesDirection.PUDDLES_DIR_NE = 2;
            PuddlesDirection.PUDDLES_DIR_NW = 4;
            PuddlesDirection.PUDDLES_DIR_ALL = 8;
        }
    }
    
    private static final class s_performance
    {
        static final PerformanceProfileProbe renderFloor;
        
        static {
            renderFloor = new PerformanceProfileProbe("IsoGridSquare.renderFloor", false);
        }
    }
    
    public interface ILighting
    {
        int lightverts(final int p0);
        
        float lampostTotalR();
        
        float lampostTotalG();
        
        float lampostTotalB();
        
        boolean bSeen();
        
        boolean bCanSee();
        
        boolean bCouldSee();
        
        float darkMulti();
        
        float targetDarkMulti();
        
        ColorInfo lightInfo();
        
        void lightverts(final int p0, final int p1);
        
        void lampostTotalR(final float p0);
        
        void lampostTotalG(final float p0);
        
        void lampostTotalB(final float p0);
        
        void bSeen(final boolean p0);
        
        void bCanSee(final boolean p0);
        
        void bCouldSee(final boolean p0);
        
        void darkMulti(final float p0);
        
        void targetDarkMulti(final float p0);
        
        int resultLightCount();
        
        ResultLight getResultLight(final int p0);
        
        void reset();
    }
    
    public interface GetSquare
    {
        IsoGridSquare getGridSquare(final int p0, final int p1, final int p2);
    }
    
    private interface RenderWallCallback
    {
        void invoke(final Texture p0, final float p1, final float p2);
    }
}
