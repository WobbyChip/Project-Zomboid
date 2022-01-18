// 
// Decompiled by Procyon v0.5.36
// 

package zombie.popman;

import java.util.List;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.PolygonalMap2;
import zombie.core.Rand;
import zombie.VirtualZombieManager;
import zombie.GameTime;
import zombie.iso.IsoWorld;
import java.util.Arrays;
import zombie.WorldSoundManager;
import zombie.iso.IsoGridSquare;
import zombie.MapCollisionData;
import zombie.ai.states.PathFindState;
import zombie.ai.states.WalkTowardState;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoChunk;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SandboxOptions;
import java.util.Iterator;
import gnu.trove.list.array.TIntArrayList;
import zombie.PersistentOutfits;
import zombie.network.GameClient;
import zombie.iso.IsoMetaGrid;
import zombie.characters.IsoPlayer;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.PredicatedFileWatcher;
import zombie.ZomboidFileSystem;
import zombie.DebugFileWatcher;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.core.Core;
import zombie.iso.IsoDirections;
import zombie.gameStates.ChooseGameInfo;
import gnu.trove.set.hash.TIntHashSet;
import java.nio.ByteBuffer;
import zombie.characters.IsoZombie;
import java.util.ArrayList;

public final class ZombiePopulationManager
{
    public static final ZombiePopulationManager instance;
    protected static final int SQUARES_PER_CHUNK = 10;
    protected static final int CHUNKS_PER_CELL = 30;
    protected static final int SQUARES_PER_CELL = 300;
    protected static final byte OLD_ZOMBIE_CRAWLER_CAN_WALK = 1;
    protected static final byte OLD_ZOMBIE_FAKE_DEAD = 2;
    protected static final byte OLD_ZOMBIE_CRAWLER = 3;
    protected static final byte OLD_ZOMBIE_WALKER = 4;
    protected static final int ZOMBIE_STATE_INITIALIZED = 1;
    protected static final int ZOMBIE_STATE_CRAWLING = 2;
    protected static final int ZOMBIE_STATE_CAN_WALK = 4;
    protected static final int ZOMBIE_STATE_FAKE_DEAD = 8;
    protected static final int ZOMBIE_STATE_CRAWL_UNDER_VEHICLE = 16;
    protected int minX;
    protected int minY;
    protected int width;
    protected int height;
    protected boolean bStopped;
    protected boolean bClient;
    private final DebugCommands dbgCommands;
    public static boolean bDebugLoggingEnabled;
    private final LoadedAreas loadedAreas;
    private final LoadedAreas loadedServerCells;
    private final PlayerSpawns playerSpawns;
    private short[] realZombieCount;
    private short[] realZombieCount2;
    private long realZombieUpdateTime;
    private final ArrayList<IsoZombie> saveRealZombieHack;
    private final ByteBuffer byteBuffer;
    private final TIntHashSet newChunks;
    private final ArrayList<ChooseGameInfo.SpawnOrigin> spawnOrigins;
    public float[] radarXY;
    public int radarCount;
    public boolean radarRenderFlag;
    public boolean radarRequestFlag;
    private final ArrayList<IsoDirections> m_sittingDirections;
    
    ZombiePopulationManager() {
        this.dbgCommands = new DebugCommands();
        this.loadedAreas = new LoadedAreas(false);
        this.loadedServerCells = new LoadedAreas(true);
        this.playerSpawns = new PlayerSpawns();
        this.realZombieUpdateTime = 0L;
        this.saveRealZombieHack = new ArrayList<IsoZombie>();
        this.byteBuffer = ByteBuffer.allocateDirect(1024);
        this.newChunks = new TIntHashSet();
        this.spawnOrigins = new ArrayList<ChooseGameInfo.SpawnOrigin>();
        this.m_sittingDirections = new ArrayList<IsoDirections>();
        this.newChunks.setAutoCompactionFactor(0.0f);
    }
    
    private static native void n_init(final boolean p0, final boolean p1, final int p2, final int p3, final int p4, final int p5);
    
    private static native void n_config(final float p0, final float p1, final float p2, final int p3, final float p4, final float p5, final float p6, final float p7, final int p8);
    
    private static native void n_setSpawnOrigins(final int[] p0);
    
    private static native void n_setOutfitNames(final String[] p0);
    
    private static native void n_updateMain(final float p0, final double p1);
    
    private static native boolean n_hasDataForThread();
    
    private static native void n_updateThread();
    
    private static native boolean n_shouldWait();
    
    private static native void n_beginSaveRealZombies(final int p0);
    
    private static native void n_saveRealZombies(final int p0, final ByteBuffer p1);
    
    private static native void n_save();
    
    private static native void n_stop();
    
    private static native void n_addZombie(final float p0, final float p1, final float p2, final byte p3, final int p4, final int p5, final int p6, final int p7);
    
    private static native void n_aggroTarget(final int p0, final int p1, final int p2);
    
    private static native void n_loadChunk(final int p0, final int p1, final boolean p2);
    
    private static native void n_loadedAreas(final int p0, final int[] p1, final boolean p2);
    
    protected static native void n_realZombieCount(final short p0, final short[] p1);
    
    protected static native void n_spawnHorde(final int p0, final int p1, final int p2, final int p3, final float p4, final float p5, final int p6);
    
    private static native void n_worldSound(final int p0, final int p1, final int p2, final int p3);
    
    private static native int n_getAddZombieCount();
    
    private static native int n_getAddZombieData(final int p0, final ByteBuffer p1);
    
    private static native boolean n_hasRadarData();
    
    private static native void n_requestRadarData();
    
    private static native int n_getRadarZombieData(final float[] p0);
    
    private static void noise(final String s) {
        if (ZombiePopulationManager.bDebugLoggingEnabled && (Core.bDebug || (GameServer.bServer && GameServer.bDebug))) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
    }
    
    public static void init() {
        String s = "";
        if ("1".equals(System.getProperty("zomboid.debuglibs.popman"))) {
            DebugLog.log("***** Loading debug version of PZPopMan");
            s = "d";
        }
        if (System.getProperty("os.name").contains("OS X")) {
            System.loadLibrary("PZPopMan");
        }
        else if (System.getProperty("sun.arch.data.model").equals("64")) {
            System.loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        else {
            System.loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        DebugFileWatcher.instance.add(new PredicatedFileWatcher(ZomboidFileSystem.instance.getMessagingDirSub("Trigger_Zombie.xml"), ZombiePopulationManager::onTriggeredZombieFile));
    }
    
    private static void onTriggeredZombieFile(final String s) {
        DebugLog.General.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        ZombieTriggerXmlFile zombieTriggerXmlFile;
        try {
            zombieTriggerXmlFile = PZXmlUtil.parse(ZombieTriggerXmlFile.class, s);
        }
        catch (PZXmlParserException ex) {
            System.err.println(invokedynamic(makeConcatWithConstants:(Lzombie/util/PZXmlParserException;)Ljava/lang/String;, ex));
            ex.printStackTrace();
            return;
        }
        if (zombieTriggerXmlFile.spawnHorde > 0) {
            processTriggerSpawnHorde(zombieTriggerXmlFile);
        }
        if (zombieTriggerXmlFile.setDebugLoggingEnabled && ZombiePopulationManager.bDebugLoggingEnabled != zombieTriggerXmlFile.bDebugLoggingEnabled) {
            ZombiePopulationManager.bDebugLoggingEnabled = zombieTriggerXmlFile.bDebugLoggingEnabled;
            DebugLog.General.println(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, ZombiePopulationManager.bDebugLoggingEnabled));
        }
    }
    
    private static void processTriggerSpawnHorde(final ZombieTriggerXmlFile zombieTriggerXmlFile) {
        DebugLog.General.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, zombieTriggerXmlFile.spawnHorde));
        if (IsoPlayer.getInstance() == null) {
            return;
        }
        final IsoPlayer instance = IsoPlayer.getInstance();
        ZombiePopulationManager.instance.createHordeFromTo((int)instance.x, (int)instance.y, (int)instance.x, (int)instance.y, zombieTriggerXmlFile.spawnHorde);
    }
    
    public void init(final IsoMetaGrid isoMetaGrid) {
        this.bClient = GameClient.bClient;
        if (this.bClient) {
            return;
        }
        this.minX = isoMetaGrid.getMinX();
        this.minY = isoMetaGrid.getMinY();
        this.width = isoMetaGrid.getWidth();
        this.height = isoMetaGrid.getHeight();
        this.bStopped = false;
        n_init(this.bClient, GameServer.bServer, this.minX, this.minY, this.width, this.height);
        this.onConfigReloaded();
        final String[] array = PersistentOutfits.instance.getOutfitNames().toArray(new String[0]);
        for (int i = 0; i < array.length; ++i) {
            array[i] = array[i].toLowerCase();
        }
        n_setOutfitNames(array);
        final TIntArrayList list = new TIntArrayList();
        for (final ChooseGameInfo.SpawnOrigin spawnOrigin : this.spawnOrigins) {
            list.add(spawnOrigin.x);
            list.add(spawnOrigin.y);
            list.add(spawnOrigin.w);
            list.add(spawnOrigin.h);
        }
        n_setSpawnOrigins(list.toArray());
    }
    
    public void onConfigReloaded() {
        final SandboxOptions.ZombieConfig zombieConfig = SandboxOptions.instance.zombieConfig;
        n_config((float)zombieConfig.PopulationMultiplier.getValue(), (float)zombieConfig.PopulationStartMultiplier.getValue(), (float)zombieConfig.PopulationPeakMultiplier.getValue(), zombieConfig.PopulationPeakDay.getValue(), (float)zombieConfig.RespawnHours.getValue(), (float)zombieConfig.RespawnUnseenHours.getValue(), (float)zombieConfig.RespawnMultiplier.getValue() * 100.0f, (float)zombieConfig.RedistributeHours.getValue(), zombieConfig.FollowSoundDistance.getValue());
    }
    
    public void registerSpawnOrigin(final int n, final int n2, final int n3, final int n4, final KahluaTable kahluaTable) {
        if (n < 0 || n2 < 0 || n3 < 0 || n4 < 0) {
            return;
        }
        this.spawnOrigins.add(new ChooseGameInfo.SpawnOrigin(n, n2, n3, n4));
    }
    
    public void playerSpawnedAt(final int n, final int n2, final int n3) {
        this.playerSpawns.addSpawn(n, n2, n3);
    }
    
    public void addChunkToWorld(final IsoChunk isoChunk) {
        if (this.bClient) {
            return;
        }
        if (isoChunk.isNewChunk()) {
            this.newChunks.add(isoChunk.wy << 16 | isoChunk.wx);
        }
        n_loadChunk(isoChunk.wx, isoChunk.wy, true);
    }
    
    public void removeChunkFromWorld(final IsoChunk isoChunk) {
        if (this.bClient) {
            return;
        }
        if (this.bStopped) {
            return;
        }
        n_loadChunk(isoChunk.wx, isoChunk.wy, false);
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 10; ++j) {
                for (int k = 0; k < 10; ++k) {
                    final IsoGridSquare gridSquare = isoChunk.getGridSquare(k, j, i);
                    if (gridSquare != null) {
                        if (!gridSquare.getMovingObjects().isEmpty()) {
                            for (int l = 0; l < gridSquare.getMovingObjects().size(); ++l) {
                                final IsoMovingObject isoMovingObject = gridSquare.getMovingObjects().get(l);
                                if (isoMovingObject instanceof IsoZombie) {
                                    final IsoZombie isoZombie = (IsoZombie)isoMovingObject;
                                    if (!GameServer.bServer || !isoZombie.bIndoorZombie) {
                                        if (!isoZombie.isReanimatedPlayer()) {
                                            final int zombieState = this.getZombieState(isoZombie);
                                            if (i == 0 && gridSquare.getRoom() == null && (isoZombie.getCurrentState() == WalkTowardState.instance() || isoZombie.getCurrentState() == PathFindState.instance())) {
                                                n_addZombie(isoZombie.x, isoZombie.y, isoZombie.z, (byte)isoZombie.dir.index(), isoZombie.getPersistentOutfitID(), zombieState, isoZombie.getPathTargetX(), isoZombie.getPathTargetY());
                                            }
                                            else {
                                                n_addZombie(isoZombie.x, isoZombie.y, isoZombie.z, (byte)isoZombie.dir.index(), isoZombie.getPersistentOutfitID(), zombieState, -1, -1);
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
        this.newChunks.remove(isoChunk.wy << 16 | isoChunk.wx);
        if (GameServer.bServer) {
            MapCollisionData.instance.notifyThread();
        }
    }
    
    public void virtualizeZombie(final IsoZombie isoZombie) {
        n_addZombie(isoZombie.x, isoZombie.y, isoZombie.z, (byte)isoZombie.dir.index(), isoZombie.getPersistentOutfitID(), this.getZombieState(isoZombie), isoZombie.getPathTargetX(), isoZombie.getPathTargetY());
        isoZombie.removeFromWorld();
        isoZombie.removeFromSquare();
    }
    
    private int getZombieState(final IsoZombie isoZombie) {
        int n = 1;
        if (isoZombie.isCrawling()) {
            n |= 0x2;
        }
        if (isoZombie.isCanWalk()) {
            n |= 0x4;
        }
        if (isoZombie.isFakeDead()) {
            n |= 0x8;
        }
        if (isoZombie.isCanCrawlUnderVehicle()) {
            n |= 0x10;
        }
        return n;
    }
    
    public void setAggroTarget(final int n, final int n2, final int n3) {
        n_aggroTarget(n, n2, n3);
    }
    
    public void createHordeFromTo(final int n, final int n2, final int n3, final int n4, final int n5) {
        n_spawnHorde(n, n2, 0, 0, (float)n3, (float)n4, n5);
    }
    
    public void createHordeInAreaTo(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        n_spawnHorde(n, n2, n3, n4, (float)n5, (float)n6, n7);
    }
    
    public void addWorldSound(final WorldSoundManager.WorldSound worldSound, final boolean b) {
        if (this.bClient) {
            return;
        }
        if (worldSound.radius < 50) {
            return;
        }
        if (worldSound.sourceIsZombie) {
            return;
        }
        n_worldSound(worldSound.x, worldSound.y, worldSound.radius, worldSound.volume);
    }
    
    private void updateRealZombieCount() {
        if (this.realZombieCount == null || this.realZombieCount.length != this.width * this.height) {
            this.realZombieCount = new short[this.width * this.height];
            this.realZombieCount2 = new short[this.width * this.height * 2];
        }
        Arrays.fill(this.realZombieCount, (short)0);
        final ArrayList<IsoZombie> zombieList = IsoWorld.instance.CurrentCell.getZombieList();
        for (int i = 0; i < zombieList.size(); ++i) {
            final IsoZombie isoZombie = zombieList.get(i);
            final int n = (int)(isoZombie.x / 300.0f) - this.minX;
            final int n2 = (int)(isoZombie.y / 300.0f) - this.minY;
            final short[] realZombieCount = this.realZombieCount;
            final int n3 = n + n2 * this.width;
            ++realZombieCount[n3];
        }
        short n4 = 0;
        for (int j = 0; j < this.width * this.height; ++j) {
            if (this.realZombieCount[j] > 0) {
                this.realZombieCount2[n4 * 2 + 0] = (short)j;
                this.realZombieCount2[n4 * 2 + 1] = this.realZombieCount[j];
                ++n4;
            }
        }
        n_realZombieCount(n4, this.realZombieCount2);
    }
    
    public void updateMain() {
        if (this.bClient) {
            return;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        n_updateMain(GameTime.getInstance().getMultiplier(), GameTime.getInstance().getWorldAgeHours());
        int n = 0;
        int n2 = 0;
        final int n_getAddZombieCount = n_getAddZombieCount();
        int i = 0;
        while (i < n_getAddZombieCount) {
            this.byteBuffer.clear();
            final int n_getAddZombieData = n_getAddZombieData(i, this.byteBuffer);
            i += n_getAddZombieData;
            for (int j = 0; j < n_getAddZombieData; ++j) {
                final float float1 = this.byteBuffer.getFloat();
                final float float2 = this.byteBuffer.getFloat();
                final float float3 = this.byteBuffer.getFloat();
                final IsoDirections fromIndex = IsoDirections.fromIndex(this.byteBuffer.get());
                final int int1 = this.byteBuffer.getInt();
                final int int2 = this.byteBuffer.getInt();
                int int3 = this.byteBuffer.getInt();
                int int4 = this.byteBuffer.getInt();
                if (this.newChunks.contains((int)float2 / 10 << 16 | (int)float1 / 10)) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare((int)float1, (int)float2, (int)float3);
                    if (gridSquare != null && gridSquare.roomID != -1) {
                        continue;
                    }
                }
                if (int3 != -1 && this.loadedAreas.isOnEdge((int)float1, (int)float2)) {
                    int3 = -1;
                    int4 = -1;
                }
                if (int3 == -1) {
                    this.addZombieStanding(float1, float2, float3, fromIndex, int1, int2);
                    ++n;
                }
                else {
                    this.addZombieMoving(float1, float2, float3, fromIndex, int1, int2, int3, int4);
                    ++n2;
                }
            }
        }
        if (n > 0) {
            noise(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n_getAddZombieCount));
        }
        if (n2 > 0) {
            noise(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n_getAddZombieCount));
        }
        if (this.radarRenderFlag && this.radarXY != null) {
            if (this.radarRequestFlag) {
                if (n_hasRadarData()) {
                    this.radarCount = n_getRadarZombieData(this.radarXY);
                    this.radarRenderFlag = false;
                    this.radarRequestFlag = false;
                }
            }
            else {
                n_requestRadarData();
                this.radarRequestFlag = true;
            }
        }
        this.updateLoadedAreas();
        if (this.realZombieUpdateTime + 5000L < currentTimeMillis) {
            this.realZombieUpdateTime = currentTimeMillis;
            this.updateRealZombieCount();
        }
        if (GameServer.bServer) {
            MPDebugInfo.instance.serverUpdate();
        }
        final boolean n_hasDataForThread = n_hasDataForThread();
        final boolean hasDataForThread = MapCollisionData.instance.hasDataForThread();
        if (n_hasDataForThread || hasDataForThread) {
            MapCollisionData.instance.notifyThread();
        }
        this.playerSpawns.update();
    }
    
    private void addZombieStanding(final float x, final float y, final float n, final IsoDirections isoDirections, final int n2, final int n3) {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare((int)x, (int)y, (int)n);
        Label_0339: {
            if (gridSquare != null) {
                if (gridSquare.SolidFloorCached) {
                    if (!gridSquare.SolidFloor) {
                        break Label_0339;
                    }
                }
                else if (!gridSquare.TreatAsSolidFloor()) {
                    break Label_0339;
                }
                if (!Core.bLastStand && !this.playerSpawns.allowZombie(gridSquare)) {
                    noise(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, (int)x, (int)y, (int)n));
                    return;
                }
                VirtualZombieManager.instance.choices.clear();
                IsoGridSquare squareForSittingZombie = null;
                if (!this.isCrawling(n3) && !this.isFakeDead(n3) && Rand.Next(3) == 0) {
                    squareForSittingZombie = this.getSquareForSittingZombie(x, y, (int)n);
                }
                if (squareForSittingZombie != null) {
                    VirtualZombieManager.instance.choices.add(squareForSittingZombie);
                }
                else {
                    VirtualZombieManager.instance.choices.add(gridSquare);
                }
                final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(n2, isoDirections.index(), false);
                if (realZombieAlways != null) {
                    if (squareForSittingZombie != null) {
                        this.sitAgainstWall(realZombieAlways, squareForSittingZombie);
                    }
                    else {
                        realZombieAlways.setX(x);
                        realZombieAlways.setY(y);
                    }
                    if (this.isFakeDead(n3)) {
                        realZombieAlways.setHealth(0.5f + Rand.Next(0.0f, 0.3f));
                        realZombieAlways.sprite = realZombieAlways.legsSprite;
                        realZombieAlways.setFakeDead(true);
                    }
                    else if (this.isCrawling(n3)) {
                        realZombieAlways.setCrawler(true);
                        realZombieAlways.setCanWalk(this.isCanWalk(n3));
                        realZombieAlways.setOnFloor(true);
                        realZombieAlways.setFallOnFront(true);
                        realZombieAlways.walkVariant = "ZombieWalk";
                        realZombieAlways.DoZombieStats();
                    }
                    if (this.isInitialized(n3)) {
                        realZombieAlways.setCanCrawlUnderVehicle(this.isCanCrawlUnderVehicle(n3));
                    }
                    else {
                        this.firstTimeLoaded(realZombieAlways, n3);
                    }
                }
                return;
            }
        }
        noise("real -> unloaded");
        n_addZombie(x, y, n, (byte)isoDirections.index(), n2, n3, -1, -1);
    }
    
    private IsoGridSquare getSquareForSittingZombie(final float n, final float n2, final int n3) {
        for (int n4 = 3, i = -n4; i < n4; ++i) {
            for (int j = -n4; j < n4; ++j) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare((int)n + i, (int)n2 + j, n3);
                if (gridSquare != null) {
                    if (gridSquare.isFree(true)) {
                        if (gridSquare.getBuilding() == null) {
                            if (gridSquare.getWallType() != 0) {
                                if (!PolygonalMap2.instance.lineClearCollide(n, n2, gridSquare.x + 0.5f, gridSquare.y + 0.5f, gridSquare.z, null, false, true)) {
                                    return gridSquare;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public void sitAgainstWall(final IsoZombie isoZombie, final IsoGridSquare isoGridSquare) {
        final float x = isoGridSquare.x + 0.5f;
        final float y = isoGridSquare.y + 0.5f;
        isoZombie.setX(x);
        isoZombie.setY(y);
        isoZombie.setSitAgainstWall(true);
        final int wallType = isoGridSquare.getWallType();
        if (wallType == 0) {
            return;
        }
        this.m_sittingDirections.clear();
        if ((wallType & 0x1) != 0x0 && (wallType & 0x4) != 0x0) {
            this.m_sittingDirections.add(IsoDirections.SE);
        }
        if ((wallType & 0x1) != 0x0 && (wallType & 0x8) != 0x0) {
            this.m_sittingDirections.add(IsoDirections.SW);
        }
        if ((wallType & 0x2) != 0x0 && (wallType & 0x4) != 0x0) {
            this.m_sittingDirections.add(IsoDirections.NE);
        }
        if ((wallType & 0x2) != 0x0 && (wallType & 0x8) != 0x0) {
            this.m_sittingDirections.add(IsoDirections.NW);
        }
        if ((wallType & 0x1) != 0x0) {
            this.m_sittingDirections.add(IsoDirections.S);
        }
        if ((wallType & 0x2) != 0x0) {
            this.m_sittingDirections.add(IsoDirections.N);
        }
        if ((wallType & 0x4) != 0x0) {
            this.m_sittingDirections.add(IsoDirections.E);
        }
        if ((wallType & 0x8) != 0x0) {
            this.m_sittingDirections.add(IsoDirections.W);
        }
        final IsoDirections dir = PZArrayUtil.pickRandom(this.m_sittingDirections);
        isoZombie.setDir(dir);
        isoZombie.setForwardDirection(dir.ToVector());
        if (isoZombie.getAnimationPlayer() != null) {
            isoZombie.getAnimationPlayer().SetForceDir(isoZombie.getForwardDirection());
        }
    }
    
    private void addZombieMoving(final float x, final float y, final float n, final IsoDirections isoDirections, final int n2, final int n3, final int n4, final int n5) {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare((int)x, (int)y, (int)n);
        Label_0265: {
            if (gridSquare != null) {
                if (gridSquare.SolidFloorCached) {
                    if (!gridSquare.SolidFloor) {
                        break Label_0265;
                    }
                }
                else if (!gridSquare.TreatAsSolidFloor()) {
                    break Label_0265;
                }
                if (!Core.bLastStand && !this.playerSpawns.allowZombie(gridSquare)) {
                    noise(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, (int)x, (int)y, (int)n));
                    return;
                }
                VirtualZombieManager.instance.choices.clear();
                VirtualZombieManager.instance.choices.add(gridSquare);
                final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(n2, isoDirections.index(), false);
                if (realZombieAlways != null) {
                    realZombieAlways.setX(x);
                    realZombieAlways.setY(y);
                    if (this.isCrawling(n3)) {
                        realZombieAlways.setCrawler(true);
                        realZombieAlways.setCanWalk(this.isCanWalk(n3));
                        realZombieAlways.setOnFloor(true);
                        realZombieAlways.setFallOnFront(true);
                        realZombieAlways.walkVariant = "ZombieWalk";
                        realZombieAlways.DoZombieStats();
                    }
                    if (this.isInitialized(n3)) {
                        realZombieAlways.setCanCrawlUnderVehicle(this.isCanCrawlUnderVehicle(n3));
                    }
                    else {
                        this.firstTimeLoaded(realZombieAlways, n3);
                    }
                    if (Math.abs(n4 - x) > 1.0f || Math.abs(n5 - y) > 1.0f) {
                        realZombieAlways.AllowRepathDelay = -1.0f;
                        realZombieAlways.pathToLocation(n4, n5, 0);
                    }
                }
                return;
            }
        }
        noise(invokedynamic(makeConcatWithConstants:(FF)Ljava/lang/String;, x, y));
        n_addZombie(x, y, n, (byte)isoDirections.index(), n2, n3, n4, n5);
    }
    
    private boolean isInitialized(final int n) {
        return (n & 0x1) != 0x0;
    }
    
    private boolean isCrawling(final int n) {
        return (n & 0x2) != 0x0;
    }
    
    private boolean isCanWalk(final int n) {
        return (n & 0x4) != 0x0;
    }
    
    private boolean isFakeDead(final int n) {
        return (n & 0x8) != 0x0;
    }
    
    private boolean isCanCrawlUnderVehicle(final int n) {
        return (n & 0x10) != 0x0;
    }
    
    private void firstTimeLoaded(final IsoZombie isoZombie, final int n) {
    }
    
    public void updateThread() {
        n_updateThread();
    }
    
    public boolean shouldWait() {
        synchronized (MapCollisionData.instance.renderLock) {
            return n_shouldWait();
        }
    }
    
    public void updateLoadedAreas() {
        if (this.loadedAreas.set()) {
            n_loadedAreas(this.loadedAreas.count, this.loadedAreas.areas, false);
        }
        if (GameServer.bServer && this.loadedServerCells.set()) {
            n_loadedAreas(this.loadedServerCells.count, this.loadedServerCells.areas, true);
        }
    }
    
    public void dbgSpawnTimeToZero(final int n, final int n2) {
        if (this.bClient && !GameClient.accessLevel.equals("admin")) {
            return;
        }
        this.dbgCommands.SpawnTimeToZero(n, n2);
    }
    
    public void dbgClearZombies(final int n, final int n2) {
        if (this.bClient && !GameClient.accessLevel.equals("admin")) {
            return;
        }
        this.dbgCommands.ClearZombies(n, n2);
    }
    
    public void dbgSpawnNow(final int n, final int n2) {
        if (this.bClient && !GameClient.accessLevel.equals("admin")) {
            return;
        }
        this.dbgCommands.SpawnNow(n, n2);
    }
    
    public void beginSaveRealZombies() {
        if (this.bClient) {
            return;
        }
        this.saveRealZombieHack.clear();
        for (final IsoZombie e : IsoWorld.instance.CurrentCell.getZombieList()) {
            if (e.isReanimatedPlayer()) {
                continue;
            }
            if (GameServer.bServer && e.bIndoorZombie) {
                continue;
            }
            this.saveRealZombieHack.add(e);
        }
        final int size = this.saveRealZombieHack.size();
        n_beginSaveRealZombies(size);
        int i = 0;
        while (i < size) {
            this.byteBuffer.clear();
            int n = 0;
            while (i < size) {
                final int position = this.byteBuffer.position();
                final IsoZombie isoZombie = this.saveRealZombieHack.get(i++);
                this.byteBuffer.putFloat(isoZombie.x);
                this.byteBuffer.putFloat(isoZombie.y);
                this.byteBuffer.putFloat(isoZombie.z);
                this.byteBuffer.put((byte)isoZombie.dir.index());
                this.byteBuffer.putInt(isoZombie.getPersistentOutfitID());
                this.byteBuffer.putInt(this.getZombieState(isoZombie));
                ++n;
                if (this.byteBuffer.position() + (this.byteBuffer.position() - position) > this.byteBuffer.capacity()) {
                    break;
                }
            }
            n_saveRealZombies(n, this.byteBuffer);
        }
        this.saveRealZombieHack.clear();
    }
    
    public void endSaveRealZombies() {
        if (this.bClient) {
            return;
        }
    }
    
    public void save() {
        if (this.bClient) {
            return;
        }
        n_save();
    }
    
    public void stop() {
        if (this.bClient) {
            return;
        }
        this.bStopped = true;
        n_stop();
        this.loadedAreas.clear();
        this.newChunks.clear();
        this.spawnOrigins.clear();
        this.radarXY = null;
        this.radarCount = 0;
        this.radarRenderFlag = false;
        this.radarRequestFlag = false;
    }
    
    static {
        instance = new ZombiePopulationManager();
        ZombiePopulationManager.bDebugLoggingEnabled = false;
    }
}
