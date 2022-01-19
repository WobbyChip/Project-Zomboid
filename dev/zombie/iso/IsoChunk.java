// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.io.OutputStream;
import java.io.PrintStream;
import zombie.GameWindow;
import java.nio.BufferOverflowException;
import java.io.ObjectOutputStream;
import zombie.network.ClientChunkRequest;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import zombie.network.ChunkChecksum;
import zombie.randomizedWorld.randomizedZoneStory.RandomizedZoneStoryBase;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.core.raknet.UdpConnection;
import zombie.randomizedWorld.randomizedBuilding.RandomizedBuildingBase;
import zombie.core.stash.StashSystem;
import zombie.LootRespawn;
import zombie.iso.objects.IsoGenerator;
import zombie.ReanimatedPlayers;
import zombie.globalObjects.SGlobalObjects;
import zombie.Lua.MapObjects;
import zombie.erosion.ErosionMain;
import zombie.core.math.PZMath;
import zombie.LoadGridsquarePerformanceWorkaround;
import zombie.vehicles.VehiclesDB2;
import zombie.network.ServerOptions;
import java.io.IOException;
import zombie.iso.objects.IsoTree;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoSurvivor;
import zombie.iso.objects.RainManager;
import zombie.vehicles.PolygonalMap2;
import zombie.popman.ZombiePopulationManager;
import zombie.MapCollisionData;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ExceptionLogger;
import zombie.core.properties.PropertyContainer;
import zombie.debug.DebugType;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.IsoThumpable;
import zombie.core.physics.Bullet;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.ChunkMapFilenames;
import zombie.randomizedWorld.randomizedVehicleStory.RandomizedVehicleStoryBase;
import zombie.ZombieSpawnRecorder;
import zombie.util.StringUtils;
import zombie.debug.DebugLog;
import zombie.scripting.ScriptManager;
import zombie.network.GameClient;
import zombie.debug.DebugOptions;
import zombie.SystemDisabler;
import zombie.vehicles.VehicleType;
import zombie.core.physics.WorldSimulation;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import zombie.characters.IsoZombie;
import zombie.iso.objects.IsoDeadBody;
import zombie.characterTextures.BloodBodyPartType;
import zombie.VirtualZombieManager;
import zombie.SandboxOptions;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.core.Rand;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.GameTime;
import java.util.HashMap;
import zombie.erosion.ErosionData;
import java.util.zip.CRC32;
import java.util.Stack;
import zombie.iso.areas.IsoBuilding;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.vehicles.BaseVehicle;
import zombie.core.utils.BoundedQueue;
import zombie.randomizedWorld.randomizedVehicleStory.VehicleStorySpawnData;
import zombie.vehicles.CollideWithObstaclesPoly;
import gnu.trove.list.array.TIntArrayList;
import zombie.WorldSoundManager;
import zombie.characters.IsoGameCharacter;
import java.util.ArrayList;
import zombie.FliesSound;

public final class IsoChunk
{
    public static boolean bDoServerRequests;
    public int wx;
    public int wy;
    public final IsoGridSquare[][] squares;
    public FliesSound.ChunkData corpseData;
    public final NearestWalls.ChunkData nearestWalls;
    private ArrayList<IsoGameCharacter.Location> generatorsTouchingThisChunk;
    public int maxLevel;
    public final ArrayList<WorldSoundManager.WorldSound> SoundList;
    private int m_treeCount;
    private int m_numberOfWaterTiles;
    private IsoMetaGrid.Zone m_scavengeZone;
    private final TIntArrayList m_spawnedRooms;
    public IsoChunk next;
    public final CollideWithObstaclesPoly.ChunkData collision;
    public int m_adjacentChunkLoadedCounter;
    public VehicleStorySpawnData m_vehicleStorySpawnData;
    public JobType jobType;
    public LotHeader lotheader;
    public final BoundedQueue<IsoFloorBloodSplat> FloorBloodSplats;
    public final ArrayList<IsoFloorBloodSplat> FloorBloodSplatsFade;
    private static final int MAX_BLOOD_SPLATS = 1000;
    private int nextSplatIndex;
    public static final byte[][] renderByIndex;
    public final ArrayList<IsoChunkMap> refs;
    public boolean bLoaded;
    private boolean blam;
    private boolean addZombies;
    private boolean bFixed2x;
    public final boolean[] lightCheck;
    public final boolean[] bLightingNeverDone;
    public final ArrayList<IsoRoomLight> roomLights;
    public final ArrayList<BaseVehicle> vehicles;
    public int lootRespawnHour;
    private long hashCodeObjects;
    public int ObjectsSyncCount;
    private static int AddVehicles_ForTest_vtype;
    private static int AddVehicles_ForTest_vskin;
    private static int AddVehicles_ForTest_vrot;
    private static final ArrayList<BaseVehicle> BaseVehicleCheckedVehicles;
    protected boolean physicsCheck;
    private static final int MAX_SHAPES = 4;
    private final PhysicsShapes[] shapes;
    private static final byte[] bshapes;
    private static ChunkGetter chunkGetter;
    private boolean loadedPhysics;
    public final Object vehiclesForAddToWorldLock;
    public ArrayList<BaseVehicle> vehiclesForAddToWorld;
    public static final ConcurrentLinkedQueue<IsoChunk> loadGridSquare;
    private static final int BLOCK_SIZE = 65536;
    private static ByteBuffer SliceBuffer;
    private static ByteBuffer SliceBufferLoad;
    private static final Object WriteLock;
    private static final ArrayList<RoomDef> tempRoomDefs;
    private static final ArrayList<IsoBuilding> tempBuildings;
    private static final ArrayList<ChunkLock> Locks;
    private static final Stack<ChunkLock> FreeLocks;
    private static final SanityCheck sanityCheck;
    private static final CRC32 crcLoad;
    private static final CRC32 crcSave;
    private static String prefix;
    private ErosionData.Chunk erosion;
    private static final HashMap<String, String> Fix2xMap;
    public int randomID;
    public long revision;
    
    public void updateSounds() {
        synchronized (WorldSoundManager.instance.SoundList) {
            for (int size = this.SoundList.size(), i = 0; i < size; ++i) {
                final WorldSoundManager.WorldSound worldSound = this.SoundList.get(i);
                if (worldSound == null || worldSound.life <= 0) {
                    this.SoundList.remove(i);
                    --i;
                    --size;
                }
            }
        }
    }
    
    public IsoChunk(final IsoCell isoCell) {
        this.wx = 0;
        this.wy = 0;
        this.nearestWalls = new NearestWalls.ChunkData();
        this.maxLevel = -1;
        this.SoundList = new ArrayList<WorldSoundManager.WorldSound>();
        this.m_treeCount = 0;
        this.m_numberOfWaterTiles = 0;
        this.m_scavengeZone = null;
        this.m_spawnedRooms = new TIntArrayList();
        this.collision = new CollideWithObstaclesPoly.ChunkData();
        this.m_adjacentChunkLoadedCounter = 0;
        this.jobType = JobType.None;
        this.FloorBloodSplats = new BoundedQueue<IsoFloorBloodSplat>(1000);
        this.FloorBloodSplatsFade = new ArrayList<IsoFloorBloodSplat>();
        this.refs = new ArrayList<IsoChunkMap>();
        this.lightCheck = new boolean[4];
        this.bLightingNeverDone = new boolean[4];
        this.roomLights = new ArrayList<IsoRoomLight>();
        this.vehicles = new ArrayList<BaseVehicle>();
        this.lootRespawnHour = -1;
        this.ObjectsSyncCount = 0;
        this.physicsCheck = false;
        this.shapes = new PhysicsShapes[4];
        this.loadedPhysics = false;
        this.vehiclesForAddToWorldLock = new Object();
        this.vehiclesForAddToWorld = null;
        this.squares = new IsoGridSquare[8][100];
        for (int i = 0; i < 4; ++i) {
            this.lightCheck[i] = true;
            this.bLightingNeverDone[i] = true;
        }
    }
    
    @Deprecated
    public long getHashCodeObjects() {
        this.recalcHashCodeObjects();
        return this.hashCodeObjects;
    }
    
    @Deprecated
    public void recalcHashCodeObjects() {
        this.hashCodeObjects = 0L;
    }
    
    @Deprecated
    public int hashCodeNoOverride() {
        return (int)this.hashCodeObjects;
    }
    
    public void addBloodSplat(final float n, final float n2, final float n3, final int n4) {
        if (n < this.wx * 10 || n >= (this.wx + 1) * 10) {
            return;
        }
        if (n2 < this.wy * 10 || n2 >= (this.wy + 1) * 10) {
            return;
        }
        final IsoGridSquare gridSquare = this.getGridSquare((int)(n - this.wx * 10), (int)(n2 - this.wy * 10), (int)n3);
        if (gridSquare != null && gridSquare.isSolidFloor()) {
            final IsoFloorBloodSplat isoFloorBloodSplat = new IsoFloorBloodSplat(n - this.wx * 10, n2 - this.wy * 10, n3, n4, (float)GameTime.getInstance().getWorldAgeHours());
            if (n4 < 8) {
                isoFloorBloodSplat.index = ++this.nextSplatIndex;
                if (this.nextSplatIndex >= 10) {
                    this.nextSplatIndex = 0;
                }
            }
            if (this.FloorBloodSplats.isFull()) {
                final IsoFloorBloodSplat e = this.FloorBloodSplats.removeFirst();
                e.fade = PerformanceSettings.getLockFPS() * 5;
                this.FloorBloodSplatsFade.add(e);
            }
            this.FloorBloodSplats.add(isoFloorBloodSplat);
        }
    }
    
    public void AddCorpses(final int n, final int n2) {
        if (IsoWorld.getZombiesDisabled() || "Tutorial".equals(Core.GameMode)) {
            return;
        }
        final IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(n, n2);
        if (metaChunk != null) {
            final float n3 = metaChunk.getZombieIntensity() * 0.1f;
            int next = 0;
            if (n3 < 1.0f) {
                if (Rand.Next(100) < n3 * 100.0f) {
                    next = 1;
                }
            }
            else {
                next = Rand.Next(0, (int)n3);
            }
            if (next > 0) {
                int n4 = 0;
                IsoGridSquare gridSquare;
                do {
                    gridSquare = this.getGridSquare(Rand.Next(10), Rand.Next(10), 0);
                } while (++n4 < 100 && (gridSquare == null || !RandomizedWorldBase.is2x2AreaClear(gridSquare)));
                if (n4 == 100) {
                    return;
                }
                if (gridSquare != null) {
                    int n5 = 14;
                    if (Rand.Next(10) == 0) {
                        n5 = 50;
                    }
                    if (Rand.Next(40) == 0) {
                        n5 = 100;
                    }
                    for (int i = 0; i < n5; ++i) {
                        this.addBloodSplat(gridSquare.getX() + (Rand.Next(3000) / 1000.0f - 1.5f), gridSquare.getY() + (Rand.Next(3000) / 1000.0f - 1.5f), (float)gridSquare.getZ(), Rand.Next(20));
                    }
                    final boolean skeleton = Rand.Next(15 - SandboxOptions.instance.TimeSinceApo.getValue()) == 0;
                    VirtualZombieManager.instance.choices.clear();
                    VirtualZombieManager.instance.choices.add(gridSquare);
                    final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(Rand.Next(8), false);
                    realZombieAlways.setX((float)gridSquare.x);
                    realZombieAlways.setY((float)gridSquare.y);
                    realZombieAlways.setFakeDead(false);
                    realZombieAlways.setHealth(0.0f);
                    realZombieAlways.upKillCount = false;
                    if (!skeleton) {
                        realZombieAlways.dressInRandomOutfit();
                        for (int j = 0; j < 10; ++j) {
                            realZombieAlways.addHole(null);
                            realZombieAlways.addBlood(null, false, true, false);
                            realZombieAlways.addDirt(null, null, false);
                        }
                        realZombieAlways.DoCorpseInventory();
                    }
                    realZombieAlways.setSkeleton(skeleton);
                    if (skeleton) {
                        realZombieAlways.getHumanVisual().setSkinTextureIndex(2);
                    }
                    final IsoDeadBody isoDeadBody = new IsoDeadBody(realZombieAlways, true);
                    if (!skeleton && Rand.Next(3) == 0) {
                        VirtualZombieManager.instance.createEatingZombies(isoDeadBody, Rand.Next(1, 4));
                    }
                }
            }
        }
    }
    
    public void AddBlood(final int n, final int n2) {
        final IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(n, n2);
        if (metaChunk != null) {
            float n3 = metaChunk.getZombieIntensity() * 0.1f;
            if (Rand.Next(40) == 0) {
                n3 += 10.0f;
            }
            int next = 0;
            if (n3 < 1.0f) {
                if (Rand.Next(100) < n3 * 100.0f) {
                    next = 1;
                }
            }
            else {
                next = Rand.Next(0, (int)n3);
            }
            if (next > 0) {
                VirtualZombieManager.instance.AddBloodToMap(next, this);
            }
        }
    }
    
    private void checkVehiclePos(final BaseVehicle baseVehicle, final IsoChunk isoChunk) {
        this.fixVehiclePos(baseVehicle, isoChunk);
        switch (baseVehicle.getDir()) {
            case E:
            case W: {
                if (baseVehicle.x - isoChunk.wx * 10 < baseVehicle.getScript().getExtents().x) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(baseVehicle.x - baseVehicle.getScript().getExtents().x, baseVehicle.y, baseVehicle.z);
                    if (gridSquare == null) {
                        return;
                    }
                    this.fixVehiclePos(baseVehicle, gridSquare.chunk);
                }
                if (baseVehicle.x - isoChunk.wx * 10 <= 10.0f - baseVehicle.getScript().getExtents().x) {
                    break;
                }
                final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(baseVehicle.x + baseVehicle.getScript().getExtents().x, baseVehicle.y, baseVehicle.z);
                if (gridSquare2 == null) {
                    return;
                }
                this.fixVehiclePos(baseVehicle, gridSquare2.chunk);
                break;
            }
            case N:
            case S: {
                if (baseVehicle.y - isoChunk.wy * 10 < baseVehicle.getScript().getExtents().z) {
                    final IsoGridSquare gridSquare3 = IsoWorld.instance.CurrentCell.getGridSquare(baseVehicle.x, baseVehicle.y - baseVehicle.getScript().getExtents().z, baseVehicle.z);
                    if (gridSquare3 == null) {
                        return;
                    }
                    this.fixVehiclePos(baseVehicle, gridSquare3.chunk);
                }
                if (baseVehicle.y - isoChunk.wy * 10 <= 10.0f - baseVehicle.getScript().getExtents().z) {
                    break;
                }
                final IsoGridSquare gridSquare4 = IsoWorld.instance.CurrentCell.getGridSquare(baseVehicle.x, baseVehicle.y + baseVehicle.getScript().getExtents().z, baseVehicle.z);
                if (gridSquare4 == null) {
                    return;
                }
                this.fixVehiclePos(baseVehicle, gridSquare4.chunk);
                break;
            }
        }
    }
    
    private boolean fixVehiclePos(final BaseVehicle baseVehicle, final IsoChunk isoChunk) {
        final BaseVehicle.MinMaxPosition minMaxPosition = baseVehicle.getMinMaxPosition();
        boolean b = false;
        final IsoDirections dir = baseVehicle.getDir();
        for (int i = 0; i < isoChunk.vehicles.size(); ++i) {
            final BaseVehicle.MinMaxPosition minMaxPosition2 = isoChunk.vehicles.get(i).getMinMaxPosition();
            switch (dir) {
                case E:
                case W: {
                    final float n = minMaxPosition2.minX - minMaxPosition.maxX;
                    if (n > 0.0f && minMaxPosition.minY < minMaxPosition2.maxY && minMaxPosition.maxY > minMaxPosition2.minY) {
                        baseVehicle.x -= n;
                        final BaseVehicle.MinMaxPosition minMaxPosition3 = minMaxPosition;
                        minMaxPosition3.minX -= n;
                        final BaseVehicle.MinMaxPosition minMaxPosition4 = minMaxPosition;
                        minMaxPosition4.maxX -= n;
                        b = true;
                        break;
                    }
                    final float n2 = minMaxPosition.minX - minMaxPosition2.maxX;
                    if (n2 > 0.0f && minMaxPosition.minY < minMaxPosition2.maxY && minMaxPosition.maxY > minMaxPosition2.minY) {
                        baseVehicle.x += n2;
                        final BaseVehicle.MinMaxPosition minMaxPosition5 = minMaxPosition;
                        minMaxPosition5.minX += n2;
                        final BaseVehicle.MinMaxPosition minMaxPosition6 = minMaxPosition;
                        minMaxPosition6.maxX += n2;
                        b = true;
                        break;
                    }
                    break;
                }
                case N:
                case S: {
                    final float n3 = minMaxPosition2.minY - minMaxPosition.maxY;
                    if (n3 > 0.0f && minMaxPosition.minX < minMaxPosition2.maxX && minMaxPosition.maxX > minMaxPosition2.minX) {
                        baseVehicle.y -= n3;
                        final BaseVehicle.MinMaxPosition minMaxPosition7 = minMaxPosition;
                        minMaxPosition7.minY -= n3;
                        final BaseVehicle.MinMaxPosition minMaxPosition8 = minMaxPosition;
                        minMaxPosition8.maxY -= n3;
                        b = true;
                        break;
                    }
                    final float n4 = minMaxPosition.minY - minMaxPosition2.maxY;
                    if (n4 > 0.0f && minMaxPosition.minX < minMaxPosition2.maxX && minMaxPosition.maxX > minMaxPosition2.minX) {
                        baseVehicle.y += n4;
                        final BaseVehicle.MinMaxPosition minMaxPosition9 = minMaxPosition;
                        minMaxPosition9.minY += n4;
                        final BaseVehicle.MinMaxPosition minMaxPosition10 = minMaxPosition;
                        minMaxPosition10.maxY += n4;
                        b = true;
                        break;
                    }
                    break;
                }
            }
        }
        return b;
    }
    
    private boolean isGoodVehiclePos(final BaseVehicle baseVehicle, final IsoChunk isoChunk) {
        final int n = ((int)baseVehicle.x - 4) / 10 - 1;
        final int n2 = ((int)baseVehicle.y - 4) / 10 - 1;
        final int n3 = (int)Math.ceil((baseVehicle.x + 4.0f) / 10.0f) + 1;
        for (int n4 = (int)Math.ceil((baseVehicle.y + 4.0f) / 10.0f) + 1, i = n2; i < n4; ++i) {
            for (int j = n; j < n3; ++j) {
                final IsoChunk isoChunk2 = GameServer.bServer ? ServerMap.instance.getChunk(j, i) : IsoWorld.instance.CurrentCell.getChunkForGridSquare(j * 10, i * 10, 0);
                if (isoChunk2 != null) {
                    for (int k = 0; k < isoChunk2.vehicles.size(); ++k) {
                        final BaseVehicle baseVehicle2 = isoChunk2.vehicles.get(k);
                        if ((int)baseVehicle2.z == (int)baseVehicle.z) {
                            if (baseVehicle.testCollisionWithVehicle(baseVehicle2)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private void AddVehicles_ForTest(final IsoMetaGrid.Zone zone) {
        int i;
        for (i = zone.y - this.wy * 10 + 3; i < 0; i += 6) {}
        int j;
        for (j = zone.x - this.wx * 10 + 2; j < 0; j += 5) {}
        for (int n = i; n < 10 && this.wy * 10 + n < zone.y + zone.h; n += 6) {
            for (int n2 = j; n2 < 10 && this.wx * 10 + n2 < zone.x + zone.w; n2 += 5) {
                final IsoGridSquare gridSquare = this.getGridSquare(n2, n, 0);
                if (gridSquare != null) {
                    final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
                    e.setZone("Test");
                    switch (IsoChunk.AddVehicles_ForTest_vtype) {
                        case 0: {
                            e.setScriptName("Base.CarNormal");
                            break;
                        }
                        case 1: {
                            e.setScriptName("Base.SmallCar");
                            break;
                        }
                        case 2: {
                            e.setScriptName("Base.SmallCar02");
                            break;
                        }
                        case 3: {
                            e.setScriptName("Base.CarTaxi");
                            break;
                        }
                        case 4: {
                            e.setScriptName("Base.CarTaxi2");
                            break;
                        }
                        case 5: {
                            e.setScriptName("Base.PickUpTruck");
                            break;
                        }
                        case 6: {
                            e.setScriptName("Base.PickUpVan");
                            break;
                        }
                        case 7: {
                            e.setScriptName("Base.CarStationWagon");
                            break;
                        }
                        case 8: {
                            e.setScriptName("Base.CarStationWagon2");
                            break;
                        }
                        case 9: {
                            e.setScriptName("Base.VanSeats");
                            break;
                        }
                        case 10: {
                            e.setScriptName("Base.Van");
                            break;
                        }
                        case 11: {
                            e.setScriptName("Base.StepVan");
                            break;
                        }
                        case 12: {
                            e.setScriptName("Base.PickUpTruck");
                            break;
                        }
                        case 13: {
                            e.setScriptName("Base.PickUpVan");
                            break;
                        }
                        case 14: {
                            e.setScriptName("Base.CarStationWagon");
                            break;
                        }
                        case 15: {
                            e.setScriptName("Base.CarStationWagon2");
                            break;
                        }
                        case 16: {
                            e.setScriptName("Base.VanSeats");
                            break;
                        }
                        case 17: {
                            e.setScriptName("Base.Van");
                            break;
                        }
                        case 18: {
                            e.setScriptName("Base.StepVan");
                            break;
                        }
                        case 19: {
                            e.setScriptName("Base.SUV");
                            break;
                        }
                        case 20: {
                            e.setScriptName("Base.OffRoad");
                            break;
                        }
                        case 21: {
                            e.setScriptName("Base.ModernCar");
                            break;
                        }
                        case 22: {
                            e.setScriptName("Base.ModernCar02");
                            break;
                        }
                        case 23: {
                            e.setScriptName("Base.CarLuxury");
                            break;
                        }
                        case 24: {
                            e.setScriptName("Base.SportsCar");
                            break;
                        }
                        case 25: {
                            e.setScriptName("Base.PickUpVanLightsPolice");
                            break;
                        }
                        case 26: {
                            e.setScriptName("Base.CarLightsPolice");
                            break;
                        }
                        case 27: {
                            e.setScriptName("Base.PickUpVanLightsFire");
                            break;
                        }
                        case 28: {
                            e.setScriptName("Base.PickUpTruckLightsFire");
                            break;
                        }
                        case 29: {
                            e.setScriptName("Base.PickUpVanLights");
                            break;
                        }
                        case 30: {
                            e.setScriptName("Base.PickUpTruckLights");
                            break;
                        }
                        case 31: {
                            e.setScriptName("Base.CarLights");
                            break;
                        }
                        case 32: {
                            e.setScriptName("Base.StepVanMail");
                            break;
                        }
                        case 33: {
                            e.setScriptName("Base.VanSpiffo");
                            break;
                        }
                        case 34: {
                            e.setScriptName("Base.VanAmbulance");
                            break;
                        }
                        case 35: {
                            e.setScriptName("Base.VanRadio");
                            break;
                        }
                        case 36: {
                            e.setScriptName("Base.PickupBurnt");
                            break;
                        }
                        case 37: {
                            e.setScriptName("Base.CarNormalBurnt");
                            break;
                        }
                        case 38: {
                            e.setScriptName("Base.TaxiBurnt");
                            break;
                        }
                        case 39: {
                            e.setScriptName("Base.ModernCarBurnt");
                            break;
                        }
                        case 40: {
                            e.setScriptName("Base.ModernCar02Burnt");
                            break;
                        }
                        case 41: {
                            e.setScriptName("Base.SportsCarBurnt");
                            break;
                        }
                        case 42: {
                            e.setScriptName("Base.SmallCarBurnt");
                            break;
                        }
                        case 43: {
                            e.setScriptName("Base.SmallCar02Burnt");
                            break;
                        }
                        case 44: {
                            e.setScriptName("Base.VanSeatsBurnt");
                            break;
                        }
                        case 45: {
                            e.setScriptName("Base.VanBurnt");
                            break;
                        }
                        case 46: {
                            e.setScriptName("Base.SUVBurnt");
                            break;
                        }
                        case 47: {
                            e.setScriptName("Base.OffRoadBurnt");
                            break;
                        }
                        case 48: {
                            e.setScriptName("Base.PickUpVanLightsBurnt");
                            break;
                        }
                        case 49: {
                            e.setScriptName("Base.AmbulanceBurnt");
                            break;
                        }
                        case 50: {
                            e.setScriptName("Base.VanRadioBurnt");
                            break;
                        }
                        case 51: {
                            e.setScriptName("Base.PickupSpecialBurnt");
                            break;
                        }
                        case 52: {
                            e.setScriptName("Base.NormalCarBurntPolice");
                            break;
                        }
                        case 53: {
                            e.setScriptName("Base.LuxuryCarBurnt");
                            break;
                        }
                        case 54: {
                            e.setScriptName("Base.PickUpVanBurnt");
                            break;
                        }
                        case 55: {
                            e.setScriptName("Base.PickUpTruckMccoy");
                            break;
                        }
                    }
                    e.setDir(IsoDirections.W);
                    e.savedRot.setAngleAxis((e.getDir().toAngle() + 3.1415927f) % 6.283185307179586, 0.0, 1.0, 0.0);
                    if (IsoChunk.AddVehicles_ForTest_vrot == 1) {
                        e.savedRot.setAngleAxis(1.5707963267948966, 0.0, 0.0, 1.0);
                    }
                    if (IsoChunk.AddVehicles_ForTest_vrot == 2) {
                        e.savedRot.setAngleAxis(3.141592653589793, 0.0, 0.0, 1.0);
                    }
                    e.jniTransform.setRotation(e.savedRot);
                    e.setX((float)gridSquare.x);
                    e.setY(gridSquare.y + 3.0f - 3.0f);
                    e.setZ((float)gridSquare.z);
                    e.jniTransform.origin.set(e.getX() - WorldSimulation.instance.offsetX, e.getZ(), e.getY() - WorldSimulation.instance.offsetY);
                    e.setScript();
                    this.checkVehiclePos(e, this);
                    this.vehicles.add(e);
                    e.setSkinIndex(IsoChunk.AddVehicles_ForTest_vskin);
                    ++IsoChunk.AddVehicles_ForTest_vrot;
                    if (IsoChunk.AddVehicles_ForTest_vrot >= 2) {
                        IsoChunk.AddVehicles_ForTest_vrot = 0;
                        ++IsoChunk.AddVehicles_ForTest_vskin;
                        if (IsoChunk.AddVehicles_ForTest_vskin >= e.getSkinCount()) {
                            IsoChunk.AddVehicles_ForTest_vtype = (IsoChunk.AddVehicles_ForTest_vtype + 1) % 56;
                            IsoChunk.AddVehicles_ForTest_vskin = 0;
                        }
                    }
                }
            }
        }
    }
    
    private void AddVehicles_OnZone(final IsoMetaGrid.VehicleZone vehicleZone, final String zone) {
        IsoDirections dir = IsoDirections.N;
        int n = 3;
        final int n2 = 4;
        if ((vehicleZone.w == n2 || vehicleZone.w == n2 + 1 || vehicleZone.w == n2 + 2) && (vehicleZone.h <= n || vehicleZone.h >= n2 + 2)) {
            dir = IsoDirections.W;
        }
        int n3 = 5;
        if (vehicleZone.dir != IsoDirections.Max) {
            dir = vehicleZone.dir;
        }
        if (dir != IsoDirections.N && dir != IsoDirections.S) {
            n3 = 3;
            n = 5;
        }
        final int n4 = 10;
        float n5;
        for (n5 = vehicleZone.y - this.wy * 10 + n3 / 2.0f; n5 < 0.0f; n5 += n3) {}
        float n6;
        for (n6 = vehicleZone.x - this.wx * 10 + n / 2.0f; n6 < 0.0f; n6 += n) {}
        for (float n7 = n5; n7 < 10.0f && this.wy * 10 + n7 < vehicleZone.y + vehicleZone.h; n7 += n3) {
            for (float n8 = n6; n8 < 10.0f && this.wx * 10 + n8 < vehicleZone.x + vehicleZone.w; n8 += n) {
                IsoGridSquare isoGridSquare = this.getGridSquare((int)n8, (int)n7, 0);
                if (isoGridSquare != null) {
                    final VehicleType randomVehicleType = VehicleType.getRandomVehicleType(zone);
                    if (randomVehicleType == null) {
                        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, zone));
                        break;
                    }
                    int spawnRate = randomVehicleType.spawnRate;
                    switch (SandboxOptions.instance.CarSpawnRate.getValue()) {
                        case 2: {
                            spawnRate = (int)Math.ceil(spawnRate / 10.0f);
                            break;
                        }
                        case 3: {
                            spawnRate = (int)Math.ceil(spawnRate / 1.5f);
                        }
                        case 5: {
                            spawnRate *= 2;
                            break;
                        }
                    }
                    if (SystemDisabler.doVehiclesEverywhere || DebugOptions.instance.VehicleSpawnEverywhere.getValue()) {
                        spawnRate = 100;
                    }
                    if (Rand.Next(100) <= spawnRate) {
                        final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
                        e.setZone(zone);
                        e.setVehicleType(randomVehicleType.name);
                        if (randomVehicleType.isSpecialCar) {
                            e.setDoColor(false);
                        }
                        if (!this.RandomizeModel(e, vehicleZone, zone, randomVehicleType)) {
                            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lzombie/vehicles/VehicleType;)Ljava/lang/String;, zone, randomVehicleType));
                            return;
                        }
                        int n9 = 15;
                        switch (SandboxOptions.instance.CarAlarm.getValue()) {
                            case 1: {
                                n9 = -1;
                                break;
                            }
                            case 2: {
                                n9 = 3;
                                break;
                            }
                            case 3: {
                                n9 = 8;
                                break;
                            }
                            case 5: {
                                n9 = 25;
                                break;
                            }
                            case 6: {
                                n9 = 50;
                                break;
                            }
                        }
                        if (Rand.Next(100) < n9) {
                            e.setAlarmed(true);
                        }
                        if (vehicleZone.isFaceDirection()) {
                            e.setDir(dir);
                        }
                        else if (dir == IsoDirections.N || dir == IsoDirections.S) {
                            e.setDir((Rand.Next(2) == 0) ? IsoDirections.N : IsoDirections.S);
                        }
                        else {
                            e.setDir((Rand.Next(2) == 0) ? IsoDirections.W : IsoDirections.E);
                        }
                        float next;
                        for (next = e.getDir().toAngle() + 3.1415927f; next > 6.283185307179586; next -= (float)6.283185307179586) {}
                        if (randomVehicleType.randomAngle) {
                            next = Rand.Next(0.0f, 6.2831855f);
                        }
                        e.savedRot.setAngleAxis(next, 0.0f, 1.0f, 0.0f);
                        e.jniTransform.setRotation(e.savedRot);
                        final float z = e.getScript().getExtents().z;
                        final float n10 = 0.5f;
                        float x = isoGridSquare.x + 0.5f;
                        float y = isoGridSquare.y + 0.5f;
                        if (dir == IsoDirections.N) {
                            x = isoGridSquare.x + n / 2.0f - (int)(n / 2.0f);
                            y = vehicleZone.y + z / 2.0f + n10;
                            if (y >= isoGridSquare.y + 1 && (int)n7 < n4 - 1 && this.getGridSquare((int)n8, (int)n7 + 1, 0) != null) {
                                isoGridSquare = this.getGridSquare((int)n8, (int)n7 + 1, 0);
                            }
                        }
                        else if (dir == IsoDirections.S) {
                            x = isoGridSquare.x + n / 2.0f - (int)(n / 2.0f);
                            y = vehicleZone.y + vehicleZone.h - z / 2.0f - n10;
                            if (y < isoGridSquare.y && (int)n7 > 0 && this.getGridSquare((int)n8, (int)n7 - 1, 0) != null) {
                                isoGridSquare = this.getGridSquare((int)n8, (int)n7 - 1, 0);
                            }
                        }
                        else if (dir == IsoDirections.W) {
                            x = vehicleZone.x + z / 2.0f + n10;
                            y = isoGridSquare.y + n3 / 2.0f - (int)(n3 / 2.0f);
                            if (x >= isoGridSquare.x + 1 && (int)n8 < n4 - 1 && this.getGridSquare((int)n8 + 1, (int)n7, 0) != null) {
                                isoGridSquare = this.getGridSquare((int)n8 + 1, (int)n7, 0);
                            }
                        }
                        else if (dir == IsoDirections.E) {
                            x = vehicleZone.x + vehicleZone.w - z / 2.0f - n10;
                            y = isoGridSquare.y + n3 / 2.0f - (int)(n3 / 2.0f);
                            if (x < isoGridSquare.x && (int)n8 > 0 && this.getGridSquare((int)n8 - 1, (int)n7, 0) != null) {
                                isoGridSquare = this.getGridSquare((int)n8 - 1, (int)n7, 0);
                            }
                        }
                        if (x < isoGridSquare.x + 0.005f) {
                            x = isoGridSquare.x + 0.005f;
                        }
                        if (x > isoGridSquare.x + 1 - 0.005f) {
                            x = isoGridSquare.x + 1 - 0.005f;
                        }
                        if (y < isoGridSquare.y + 0.005f) {
                            y = isoGridSquare.y + 0.005f;
                        }
                        if (y > isoGridSquare.y + 1 - 0.005f) {
                            y = isoGridSquare.y + 1 - 0.005f;
                        }
                        e.setX(x);
                        e.setY(y);
                        e.setZ((float)isoGridSquare.z);
                        e.jniTransform.origin.set(e.getX() - WorldSimulation.instance.offsetX, e.getZ(), e.getY() - WorldSimulation.instance.offsetY);
                        e.rust = ((Rand.Next(100) < 100.0f - Math.min(randomVehicleType.baseVehicleQuality * 120.0f, 100.0f)) ? 1.0f : 0.0f);
                        if (doSpawnedVehiclesInInvalidPosition(e) || GameClient.bClient) {
                            this.vehicles.add(e);
                        }
                        if (randomVehicleType.chanceOfOverCar > 0 && Rand.Next(100) <= randomVehicleType.chanceOfOverCar) {
                            this.spawnVehicleRandomAngle(isoGridSquare, vehicleZone, zone);
                        }
                    }
                }
            }
        }
    }
    
    private void AddVehicles_OnZonePolyline(final IsoMetaGrid.VehicleZone vehicleZone, final String zone) {
        final int n = 5;
        final Vector2 vector2 = new Vector2();
        for (int i = 0; i < vehicleZone.points.size() - 2; i += 2) {
            final int quick = vehicleZone.points.getQuick(i);
            final int quick2 = vehicleZone.points.getQuick(i + 1);
            vector2.set((float)(vehicleZone.points.getQuick((i + 2) % vehicleZone.points.size()) - quick), (float)(vehicleZone.points.getQuick((i + 3) % vehicleZone.points.size()) - quick2));
            for (float n2 = n / 2.0f; n2 < vector2.getLength(); n2 += n) {
                float x = quick + vector2.x / vector2.getLength() * n2;
                float y = quick2 + vector2.y / vector2.getLength() * n2;
                if (x >= this.wx * 10 && y >= this.wy * 10 && x < (this.wx + 1) * 10 && y < (this.wy + 1) * 10) {
                    final VehicleType randomVehicleType = VehicleType.getRandomVehicleType(zone);
                    if (randomVehicleType == null) {
                        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, zone));
                        return;
                    }
                    final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
                    e.setZone(zone);
                    e.setVehicleType(randomVehicleType.name);
                    if (randomVehicleType.isSpecialCar) {
                        e.setDoColor(false);
                    }
                    if (!this.RandomizeModel(e, vehicleZone, zone, randomVehicleType)) {
                        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lzombie/vehicles/VehicleType;)Ljava/lang/String;, zone, randomVehicleType));
                        return;
                    }
                    int n3 = 15;
                    switch (SandboxOptions.instance.CarAlarm.getValue()) {
                        case 1: {
                            n3 = -1;
                            break;
                        }
                        case 2: {
                            n3 = 3;
                            break;
                        }
                        case 3: {
                            n3 = 8;
                            break;
                        }
                        case 5: {
                            n3 = 25;
                            break;
                        }
                        case 6: {
                            n3 = 50;
                            break;
                        }
                    }
                    if (Rand.Next(100) < n3) {
                        e.setAlarmed(true);
                    }
                    final float x2 = vector2.x;
                    final float y2 = vector2.y;
                    vector2.normalize();
                    e.setDir(IsoDirections.fromAngle(vector2));
                    float next;
                    for (next = vector2.getDirectionNeg() + 0.0f; next > 6.283185307179586; next -= (float)6.283185307179586) {}
                    vector2.x = x2;
                    vector2.y = y2;
                    if (randomVehicleType.randomAngle) {
                        next = Rand.Next(0.0f, 6.2831855f);
                    }
                    e.savedRot.setAngleAxis(next, 0.0f, 1.0f, 0.0f);
                    e.jniTransform.setRotation(e.savedRot);
                    final IsoGridSquare gridSquare = this.getGridSquare((int)x - this.wx * 10, (int)y - this.wy * 10, 0);
                    if (x < gridSquare.x + 0.005f) {
                        x = gridSquare.x + 0.005f;
                    }
                    if (x > gridSquare.x + 1 - 0.005f) {
                        x = gridSquare.x + 1 - 0.005f;
                    }
                    if (y < gridSquare.y + 0.005f) {
                        y = gridSquare.y + 0.005f;
                    }
                    if (y > gridSquare.y + 1 - 0.005f) {
                        y = gridSquare.y + 1 - 0.005f;
                    }
                    e.setX(x);
                    e.setY(y);
                    e.setZ((float)gridSquare.z);
                    e.jniTransform.origin.set(e.getX() - WorldSimulation.instance.offsetX, e.getZ(), e.getY() - WorldSimulation.instance.offsetY);
                    e.rust = ((Rand.Next(100) < 100.0f - Math.min(randomVehicleType.baseVehicleQuality * 120.0f, 100.0f)) ? 1.0f : 0.0f);
                    if (doSpawnedVehiclesInInvalidPosition(e) || GameClient.bClient) {
                        this.vehicles.add(e);
                    }
                }
            }
        }
    }
    
    public static void removeFromCheckedVehicles(final BaseVehicle o) {
        IsoChunk.BaseVehicleCheckedVehicles.remove(o);
    }
    
    public static void addFromCheckedVehicles(final BaseVehicle baseVehicle) {
        if (!IsoChunk.BaseVehicleCheckedVehicles.contains(baseVehicle)) {
            IsoChunk.BaseVehicleCheckedVehicles.add(baseVehicle);
        }
    }
    
    public static void Reset() {
        IsoChunk.BaseVehicleCheckedVehicles.clear();
    }
    
    public static boolean doSpawnedVehiclesInInvalidPosition(final BaseVehicle baseVehicle) {
        if (GameServer.bServer) {
            final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare((int)baseVehicle.getX(), (int)baseVehicle.getY(), 0);
            if (gridSquare != null && gridSquare.roomID != -1) {
                return false;
            }
        }
        else if (!GameClient.bClient) {
            final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare((int)baseVehicle.getX(), (int)baseVehicle.getY(), 0);
            if (gridSquare2 != null && gridSquare2.roomID != -1) {
                return false;
            }
        }
        boolean b = true;
        for (int i = 0; i < IsoChunk.BaseVehicleCheckedVehicles.size(); ++i) {
            if (IsoChunk.BaseVehicleCheckedVehicles.get(i).testCollisionWithVehicle(baseVehicle)) {
                b = false;
            }
        }
        if (b) {
            addFromCheckedVehicles(baseVehicle);
        }
        return b;
    }
    
    private void spawnVehicleRandomAngle(final IsoGridSquare isoGridSquare, final IsoMetaGrid.Zone zone, final String zone2) {
        boolean b = true;
        int n = 3;
        final int n2 = 4;
        if ((zone.w == n2 || zone.w == n2 + 1 || zone.w == n2 + 2) && (zone.h <= n || zone.h >= n2 + 2)) {
            b = false;
        }
        int n3 = 5;
        if (!b) {
            n3 = 3;
            n = 5;
        }
        final VehicleType randomVehicleType = VehicleType.getRandomVehicleType(zone2);
        if (randomVehicleType == null) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, zone2));
            return;
        }
        final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
        e.setZone(zone2);
        if (!this.RandomizeModel(e, zone, zone2, randomVehicleType)) {
            return;
        }
        if (b) {
            e.setDir((Rand.Next(2) == 0) ? IsoDirections.N : IsoDirections.S);
        }
        else {
            e.setDir((Rand.Next(2) == 0) ? IsoDirections.W : IsoDirections.E);
        }
        e.savedRot.setAngleAxis(Rand.Next(0.0f, 6.2831855f), 0.0f, 1.0f, 0.0f);
        e.jniTransform.setRotation(e.savedRot);
        if (b) {
            e.setX(isoGridSquare.x + n / 2.0f - (int)(n / 2.0f));
            e.setY((float)isoGridSquare.y);
        }
        else {
            e.setX((float)isoGridSquare.x);
            e.setY(isoGridSquare.y + n3 / 2.0f - (int)(n3 / 2.0f));
        }
        e.setZ((float)isoGridSquare.z);
        e.jniTransform.origin.set(e.getX() - WorldSimulation.instance.offsetX, e.getZ(), e.getY() - WorldSimulation.instance.offsetY);
        if (doSpawnedVehiclesInInvalidPosition(e) || GameClient.bClient) {
            this.vehicles.add(e);
        }
    }
    
    public boolean RandomizeModel(final BaseVehicle baseVehicle, final IsoMetaGrid.Zone zone, final String s, final VehicleType vehicleType) {
        if (vehicleType.vehiclesDefinition.isEmpty()) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            return false;
        }
        final float next = Rand.Next(0.0f, 100.0f);
        float n = 0.0f;
        VehicleType.VehicleTypeDefinition vehicleTypeDefinition = null;
        for (int i = 0; i < vehicleType.vehiclesDefinition.size(); ++i) {
            vehicleTypeDefinition = vehicleType.vehiclesDefinition.get(i);
            n += vehicleTypeDefinition.spawnChance;
            if (next < n) {
                break;
            }
        }
        final String vehicleType2 = vehicleTypeDefinition.vehicleType;
        if (ScriptManager.instance.getVehicle(vehicleType2) == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, vehicleType2));
            return false;
        }
        final int index = vehicleTypeDefinition.index;
        baseVehicle.setScriptName(vehicleType2);
        baseVehicle.setScript();
        try {
            if (index > -1) {
                baseVehicle.setSkinIndex(index);
            }
            else {
                baseVehicle.setSkinIndex(Rand.Next(baseVehicle.getSkinCount()));
            }
        }
        catch (Exception ex) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, baseVehicle.getScriptName()));
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    private void AddVehicles_TrafficJam_W(final IsoMetaGrid.Zone zone, final String s) {
        int i;
        for (i = zone.y - this.wy * 10 + 1; i < 0; i += 3) {}
        int j;
        for (j = zone.x - this.wx * 10 + 3; j < 0; j += 6) {}
        for (int n = i; n < 10 && this.wy * 10 + n < zone.y + zone.h; n += 3 + Rand.Next(1)) {
            for (int n2 = j; n2 < 10 && this.wx * 10 + n2 < zone.x + zone.w; n2 += 6 + Rand.Next(1)) {
                final IsoGridSquare gridSquare = this.getGridSquare(n2, n, 0);
                if (gridSquare != null) {
                    final VehicleType randomVehicleType = VehicleType.getRandomVehicleType(s);
                    if (randomVehicleType == null) {
                        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                        break;
                    }
                    int n3 = 80;
                    if (SystemDisabler.doVehiclesEverywhere || DebugOptions.instance.VehicleSpawnEverywhere.getValue()) {
                        n3 = 100;
                    }
                    if (Rand.Next(100) <= n3) {
                        final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
                        e.setZone("TrafficJam");
                        e.setVehicleType(randomVehicleType.name);
                        if (!this.RandomizeModel(e, zone, s, randomVehicleType)) {
                            return;
                        }
                        e.setScript();
                        e.setX(gridSquare.x + Rand.Next(0.0f, 1.0f));
                        e.setY(gridSquare.y + Rand.Next(0.0f, 1.0f));
                        e.setZ((float)gridSquare.z);
                        e.jniTransform.origin.set(e.getX() - WorldSimulation.instance.offsetX, e.getZ(), e.getY() - WorldSimulation.instance.offsetY);
                        if (this.isGoodVehiclePos(e, this)) {
                            e.setSkinIndex(Rand.Next(e.getSkinCount() - 1));
                            e.setDir(IsoDirections.W);
                            float n4;
                            for (n4 = e.getDir().toAngle() + 3.1415927f - 0.25f + Rand.Next(0.0f, Math.min(2.0f, Math.abs(zone.x + zone.w - gridSquare.x) / 20.0f)); n4 > 6.283185307179586; n4 -= (float)6.283185307179586) {}
                            e.savedRot.setAngleAxis(n4, 0.0f, 1.0f, 0.0f);
                            e.jniTransform.setRotation(e.savedRot);
                            if (doSpawnedVehiclesInInvalidPosition(e) || GameClient.bClient) {
                                this.vehicles.add(e);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void AddVehicles_TrafficJam_E(final IsoMetaGrid.Zone zone, final String s) {
        int i;
        for (i = zone.y - this.wy * 10 + 1; i < 0; i += 3) {}
        int j;
        for (j = zone.x - this.wx * 10 + 3; j < 0; j += 6) {}
        for (int n = i; n < 10 && this.wy * 10 + n < zone.y + zone.h; n += 3 + Rand.Next(1)) {
            for (int n2 = j; n2 < 10 && this.wx * 10 + n2 < zone.x + zone.w; n2 += 6 + Rand.Next(1)) {
                final IsoGridSquare gridSquare = this.getGridSquare(n2, n, 0);
                if (gridSquare != null) {
                    final VehicleType randomVehicleType = VehicleType.getRandomVehicleType(s);
                    if (randomVehicleType == null) {
                        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                        break;
                    }
                    int n3 = 80;
                    if (SystemDisabler.doVehiclesEverywhere || DebugOptions.instance.VehicleSpawnEverywhere.getValue()) {
                        n3 = 100;
                    }
                    if (Rand.Next(100) <= n3) {
                        final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
                        e.setZone("TrafficJam");
                        e.setVehicleType(randomVehicleType.name);
                        if (!this.RandomizeModel(e, zone, s, randomVehicleType)) {
                            return;
                        }
                        e.setScript();
                        e.setX(gridSquare.x + Rand.Next(0.0f, 1.0f));
                        e.setY(gridSquare.y + Rand.Next(0.0f, 1.0f));
                        e.setZ((float)gridSquare.z);
                        e.jniTransform.origin.set(e.getX() - WorldSimulation.instance.offsetX, e.getZ(), e.getY() - WorldSimulation.instance.offsetY);
                        if (this.isGoodVehiclePos(e, this)) {
                            e.setSkinIndex(Rand.Next(e.getSkinCount() - 1));
                            e.setDir(IsoDirections.E);
                            float n4;
                            for (n4 = e.getDir().toAngle() + 3.1415927f - 0.25f + Rand.Next(0.0f, Math.min(2.0f, Math.abs(zone.x + zone.w - gridSquare.x - zone.w) / 20.0f)); n4 > 6.283185307179586; n4 -= (float)6.283185307179586) {}
                            e.savedRot.setAngleAxis(n4, 0.0f, 1.0f, 0.0f);
                            e.jniTransform.setRotation(e.savedRot);
                            if (doSpawnedVehiclesInInvalidPosition(e) || GameClient.bClient) {
                                this.vehicles.add(e);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void AddVehicles_TrafficJam_S(final IsoMetaGrid.Zone zone, final String s) {
        int i;
        for (i = zone.y - this.wy * 10 + 3; i < 0; i += 6) {}
        int j;
        for (j = zone.x - this.wx * 10 + 1; j < 0; j += 3) {}
        for (int n = i; n < 10 && this.wy * 10 + n < zone.y + zone.h; n += 6 + Rand.Next(-1, 1)) {
            for (int n2 = j; n2 < 10 && this.wx * 10 + n2 < zone.x + zone.w; n2 += 3 + Rand.Next(1)) {
                final IsoGridSquare gridSquare = this.getGridSquare(n2, n, 0);
                if (gridSquare != null) {
                    final VehicleType randomVehicleType = VehicleType.getRandomVehicleType(s);
                    if (randomVehicleType == null) {
                        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                        break;
                    }
                    int n3 = 80;
                    if (SystemDisabler.doVehiclesEverywhere || DebugOptions.instance.VehicleSpawnEverywhere.getValue()) {
                        n3 = 100;
                    }
                    if (Rand.Next(100) <= n3) {
                        final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
                        e.setZone("TrafficJam");
                        e.setVehicleType(randomVehicleType.name);
                        if (!this.RandomizeModel(e, zone, s, randomVehicleType)) {
                            return;
                        }
                        e.setScript();
                        e.setX(gridSquare.x + Rand.Next(0.0f, 1.0f));
                        e.setY(gridSquare.y + Rand.Next(0.0f, 1.0f));
                        e.setZ((float)gridSquare.z);
                        e.jniTransform.origin.set(e.getX() - WorldSimulation.instance.offsetX, e.getZ(), e.getY() - WorldSimulation.instance.offsetY);
                        if (this.isGoodVehiclePos(e, this)) {
                            e.setSkinIndex(Rand.Next(e.getSkinCount() - 1));
                            e.setDir(IsoDirections.S);
                            float n4;
                            for (n4 = e.getDir().toAngle() + 3.1415927f - 0.25f + Rand.Next(0.0f, Math.min(2.0f, Math.abs(zone.y + zone.h - gridSquare.y - zone.h) / 20.0f)); n4 > 6.283185307179586; n4 -= (float)6.283185307179586) {}
                            e.savedRot.setAngleAxis(n4, 0.0f, 1.0f, 0.0f);
                            e.jniTransform.setRotation(e.savedRot);
                            if (doSpawnedVehiclesInInvalidPosition(e) || GameClient.bClient) {
                                this.vehicles.add(e);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void AddVehicles_TrafficJam_N(final IsoMetaGrid.Zone zone, final String s) {
        int i;
        for (i = zone.y - this.wy * 10 + 3; i < 0; i += 6) {}
        int j;
        for (j = zone.x - this.wx * 10 + 1; j < 0; j += 3) {}
        for (int n = i; n < 10 && this.wy * 10 + n < zone.y + zone.h; n += 6 + Rand.Next(-1, 1)) {
            for (int n2 = j; n2 < 10 && this.wx * 10 + n2 < zone.x + zone.w; n2 += 3 + Rand.Next(1)) {
                final IsoGridSquare gridSquare = this.getGridSquare(n2, n, 0);
                if (gridSquare != null) {
                    final VehicleType randomVehicleType = VehicleType.getRandomVehicleType(s);
                    if (randomVehicleType == null) {
                        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                        break;
                    }
                    int n3 = 80;
                    if (SystemDisabler.doVehiclesEverywhere || DebugOptions.instance.VehicleSpawnEverywhere.getValue()) {
                        n3 = 100;
                    }
                    if (Rand.Next(100) <= n3) {
                        final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
                        e.setZone("TrafficJam");
                        e.setVehicleType(randomVehicleType.name);
                        if (!this.RandomizeModel(e, zone, s, randomVehicleType)) {
                            return;
                        }
                        e.setScript();
                        e.setX(gridSquare.x + Rand.Next(0.0f, 1.0f));
                        e.setY(gridSquare.y + Rand.Next(0.0f, 1.0f));
                        e.setZ((float)gridSquare.z);
                        e.jniTransform.origin.set(e.getX() - WorldSimulation.instance.offsetX, e.getZ(), e.getY() - WorldSimulation.instance.offsetY);
                        if (this.isGoodVehiclePos(e, this)) {
                            e.setSkinIndex(Rand.Next(e.getSkinCount() - 1));
                            e.setDir(IsoDirections.N);
                            float n4;
                            for (n4 = e.getDir().toAngle() + 3.1415927f - 0.25f + Rand.Next(0.0f, Math.min(2.0f, Math.abs(zone.y + zone.h - gridSquare.y) / 20.0f)); n4 > 6.283185307179586; n4 -= (float)6.283185307179586) {}
                            e.savedRot.setAngleAxis(n4, 0.0f, 1.0f, 0.0f);
                            e.jniTransform.setRotation(e.savedRot);
                            if (doSpawnedVehiclesInInvalidPosition(e) || GameClient.bClient) {
                                this.vehicles.add(e);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void AddVehicles() {
        if (SandboxOptions.instance.CarSpawnRate.getValue() == 1) {
            return;
        }
        if (VehicleType.vehicles.isEmpty()) {
            VehicleType.init();
        }
        if (GameClient.bClient) {
            return;
        }
        if (!SandboxOptions.instance.EnableVehicles.getValue()) {
            return;
        }
        WorldSimulation.instance.create();
        final IsoMetaCell cellData = IsoWorld.instance.getMetaGrid().getCellData(this.wx / 30, this.wy / 30);
        final ArrayList<IsoMetaGrid.VehicleZone> list = (cellData == null) ? null : cellData.vehicleZones;
        for (int index = 0; list != null && index < list.size(); ++index) {
            final IsoMetaGrid.VehicleZone vehicleZone = list.get(index);
            if (vehicleZone.x + vehicleZone.w >= this.wx * 10 && vehicleZone.y + vehicleZone.h >= this.wy * 10 && vehicleZone.x < (this.wx + 1) * 10) {
                if (vehicleZone.y < (this.wy + 1) * 10) {
                    String anObject = vehicleZone.name;
                    if (anObject.isEmpty()) {
                        anObject = vehicleZone.type;
                    }
                    if (SandboxOptions.instance.TrafficJam.getValue()) {
                        if ("TrafficJamW".equalsIgnoreCase(anObject)) {
                            this.AddVehicles_TrafficJam_W(vehicleZone, anObject);
                        }
                        if ("TrafficJamE".equalsIgnoreCase(anObject)) {
                            this.AddVehicles_TrafficJam_E(vehicleZone, anObject);
                        }
                        if ("TrafficJamS".equalsIgnoreCase(anObject)) {
                            this.AddVehicles_TrafficJam_S(vehicleZone, anObject);
                        }
                        if ("TrafficJamN".equalsIgnoreCase(anObject)) {
                            this.AddVehicles_TrafficJam_N(vehicleZone, anObject);
                        }
                        if ("RTrafficJamW".equalsIgnoreCase(anObject) && Rand.Next(100) < 10) {
                            this.AddVehicles_TrafficJam_W(vehicleZone, anObject.replaceFirst("rtraffic", "traffic"));
                        }
                        if ("RTrafficJamE".equalsIgnoreCase(anObject) && Rand.Next(100) < 10) {
                            this.AddVehicles_TrafficJam_E(vehicleZone, anObject.replaceFirst("rtraffic", "traffic"));
                        }
                        if ("RTrafficJamS".equalsIgnoreCase(anObject) && Rand.Next(100) < 10) {
                            this.AddVehicles_TrafficJam_S(vehicleZone, anObject.replaceFirst("rtraffic", "traffic"));
                        }
                        if ("RTrafficJamN".equalsIgnoreCase(anObject) && Rand.Next(100) < 10) {
                            this.AddVehicles_TrafficJam_N(vehicleZone, anObject.replaceFirst("rtraffic", "traffic"));
                        }
                    }
                    if (!StringUtils.containsIgnoreCase(anObject, "TrafficJam")) {
                        if ("TestVehicles".equals(anObject)) {
                            this.AddVehicles_ForTest(vehicleZone);
                        }
                        else if (VehicleType.hasTypeForZone(anObject)) {
                            if (vehicleZone.isPolyline()) {
                                this.AddVehicles_OnZonePolyline(vehicleZone, anObject);
                            }
                            else {
                                this.AddVehicles_OnZone(vehicleZone, anObject);
                            }
                        }
                    }
                }
            }
        }
        final IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(this.wx, this.wy);
        if (metaChunk == null) {
            return;
        }
        for (int i = 0; i < metaChunk.numZones(); ++i) {
            this.addRandomCarCrash(metaChunk.getZone(i), false);
        }
    }
    
    public void addSurvivorInHorde(final boolean b) {
        if (!b && IsoWorld.getZombiesDisabled()) {
            return;
        }
        final IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(this.wx, this.wy);
        if (metaChunk == null) {
            return;
        }
        for (int i = 0; i < metaChunk.numZones(); ++i) {
            final IsoMetaGrid.Zone zone = metaChunk.getZone(i);
            if (this.canAddSurvivorInHorde(zone, b)) {
                final int max = Math.max(15, (int)(4 + ((float)GameTime.getInstance().getWorldAgeHours() / 24.0f + (SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30) * 0.03f));
                if (b || Rand.Next(0.0f, 500.0f) < 0.4f * max) {
                    this.addSurvivorInHorde(zone);
                    if (b) {
                        break;
                    }
                }
            }
        }
    }
    
    private boolean canAddSurvivorInHorde(final IsoMetaGrid.Zone zone, final boolean b) {
        return (b || IsoWorld.instance.getTimeSinceLastSurvivorInHorde() <= 0) && (b || !IsoWorld.getZombiesDisabled()) && (b || zone.hourLastSeen == 0) && (b || !zone.haveConstruction) && "Nav".equals(zone.getType());
    }
    
    private void addSurvivorInHorde(final IsoMetaGrid.Zone zone) {
        ++zone.hourLastSeen;
        IsoWorld.instance.setTimeSinceLastSurvivorInHorde(5000);
        final int max = Math.max(zone.x, this.wx * 10);
        final int max2 = Math.max(zone.y, this.wy * 10);
        final int min = Math.min(zone.x + zone.w, (this.wx + 1) * 10);
        final int min2 = Math.min(zone.y + zone.h, (this.wy + 1) * 10);
        final float n = max + (min - max) / 2.0f;
        final float n2 = max2 + (min2 - max2) / 2.0f;
        VirtualZombieManager.instance.choices.clear();
        final IsoGridSquare gridSquare = this.getGridSquare((int)n - this.wx * 10, (int)n2 - this.wy * 10, 0);
        if (gridSquare.getBuilding() != null) {
            return;
        }
        VirtualZombieManager.instance.choices.add(gridSquare);
        for (int next = Rand.Next(15, 20), i = 0; i < next; ++i) {
            final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(Rand.Next(8), false);
            if (realZombieAlways != null) {
                realZombieAlways.dressInRandomOutfit();
                ZombieSpawnRecorder.instance.record(realZombieAlways, "addSurvivorInHorde");
            }
        }
        final IsoZombie realZombieAlways2 = VirtualZombieManager.instance.createRealZombieAlways(Rand.Next(8), false);
        if (realZombieAlways2 != null) {
            ZombieSpawnRecorder.instance.record(realZombieAlways2, "addSurvivorInHorde");
            realZombieAlways2.setAsSurvivor();
        }
    }
    
    public boolean canAddRandomCarCrash(final IsoMetaGrid.Zone zone, final boolean b) {
        if (!b && zone.hourLastSeen != 0) {
            return false;
        }
        if (!b && zone.haveConstruction) {
            return false;
        }
        if (!"Nav".equals(zone.getType())) {
            return false;
        }
        final int max = Math.max(zone.x, this.wx * 10);
        final int max2 = Math.max(zone.y, this.wy * 10);
        final int min = Math.min(zone.x + zone.w, (this.wx + 1) * 10);
        final int min2 = Math.min(zone.y + zone.h, (this.wy + 1) * 10);
        if (zone.w > 30 && zone.h < 13) {
            return min - max >= 10 && min2 - max2 >= 5;
        }
        return zone.h > 30 && zone.w < 13 && min - max >= 5 && min2 - max2 >= 10;
    }
    
    public void addRandomCarCrash(final IsoMetaGrid.Zone zone, final boolean b) {
        if (!this.vehicles.isEmpty()) {
            return;
        }
        if (!"Nav".equals(zone.getType())) {
            return;
        }
        RandomizedVehicleStoryBase.doRandomStory(zone, this, false);
    }
    
    public static boolean FileExists(final int n, final int n2) {
        File file = ChunkMapFilenames.instance.getFilename(n, n2);
        if (file == null) {
            file = ZomboidFileSystem.instance.getFileInCurrentSave(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, IsoChunk.prefix, n, n2));
        }
        return file.exists();
    }
    
    private void checkPhysics() {
        if (!this.physicsCheck) {
            return;
        }
        WorldSimulation.instance.create();
        Bullet.beginUpdateChunk(this);
        final int n = 0;
        if (n < 8) {
            for (int i = 0; i < 10; ++i) {
                for (int j = 0; j < 10; ++j) {
                    this.calcPhysics(j, i, n, this.shapes);
                    int n2 = 0;
                    for (int k = 0; k < 4; ++k) {
                        if (this.shapes[k] != null) {
                            IsoChunk.bshapes[n2++] = (byte)(this.shapes[k].ordinal() + 1);
                        }
                    }
                    Bullet.updateChunk(j, i, n, n2, IsoChunk.bshapes);
                }
            }
        }
        Bullet.endUpdateChunk();
        this.physicsCheck = false;
    }
    
    private void calcPhysics(final int n, final int n2, final int n3, final PhysicsShapes[] array) {
        for (int i = 0; i < 4; ++i) {
            array[i] = null;
        }
        final IsoGridSquare gridSquare = this.getGridSquare(n, n2, n3);
        if (gridSquare == null) {
            return;
        }
        int n4 = 0;
        if (n3 == 0) {
            boolean b = false;
            for (int j = 0; j < gridSquare.getObjects().size(); ++j) {
                final IsoObject isoObject = gridSquare.getObjects().get(j);
                if (isoObject.sprite != null && isoObject.sprite.name != null && (isoObject.sprite.name.contains("lighting_outdoor_") || isoObject.sprite.name.equals("recreational_sports_01_21") || isoObject.sprite.name.equals("recreational_sports_01_19") || isoObject.sprite.name.equals("recreational_sports_01_32")) && (!isoObject.getProperties().Is("MoveType") || !"WallObject".equals(isoObject.getProperties().Val("MoveType")))) {
                    b = true;
                    break;
                }
            }
            if (b) {
                array[n4++] = PhysicsShapes.Tree;
            }
        }
        boolean b2 = false;
        if (!gridSquare.getSpecialObjects().isEmpty()) {
            for (int size = gridSquare.getSpecialObjects().size(), k = 0; k < size; ++k) {
                final IsoObject isoObject2 = gridSquare.getSpecialObjects().get(k);
                if (isoObject2 instanceof IsoThumpable && ((IsoThumpable)isoObject2).isBlockAllTheSquare()) {
                    b2 = true;
                    break;
                }
            }
        }
        final PropertyContainer properties = gridSquare.getProperties();
        if (gridSquare.hasTypes.isSet(IsoObjectType.isMoveAbleObject)) {
            array[n4++] = PhysicsShapes.Tree;
        }
        Label_0634: {
            if (gridSquare.hasTypes.isSet(IsoObjectType.tree)) {
                final String val = gridSquare.getProperties().Val("tree");
                final String val2 = gridSquare.getProperties().Val("WindType");
                if (val == null) {
                    array[n4++] = PhysicsShapes.Tree;
                }
                if (val != null && !val.equals("1") && (val2 == null || !val2.equals("2") || (!val.equals("2") && !val.equals("1")))) {
                    array[n4++] = PhysicsShapes.Tree;
                }
            }
            else if (properties.Is(IsoFlagType.solid) || properties.Is(IsoFlagType.solidtrans) || properties.Is(IsoFlagType.blocksight) || gridSquare.HasStairs() || b2) {
                if (n4 == array.length) {
                    DebugLog.log(DebugType.General, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, gridSquare.x, gridSquare.y, gridSquare.z));
                    return;
                }
                array[n4++] = PhysicsShapes.Solid;
            }
            else if (n3 > 0) {
                if (gridSquare.SolidFloorCached) {
                    if (!gridSquare.SolidFloor) {
                        break Label_0634;
                    }
                }
                else if (!gridSquare.TreatAsSolidFloor()) {
                    break Label_0634;
                }
                if (n4 == array.length) {
                    DebugLog.log(DebugType.General, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, gridSquare.x, gridSquare.y, gridSquare.z));
                    return;
                }
                array[n4++] = PhysicsShapes.Floor;
            }
        }
        if (gridSquare.getProperties().Is("CarSlowFactor")) {
            return;
        }
        if (properties.Is(IsoFlagType.collideW) || properties.Is(IsoFlagType.windowW) || (gridSquare.getProperties().Is(IsoFlagType.DoorWallW) && !gridSquare.getProperties().Is("GarageDoor"))) {
            if (n4 == array.length) {
                DebugLog.log(DebugType.General, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, gridSquare.x, gridSquare.y, gridSquare.z));
                return;
            }
            array[n4++] = PhysicsShapes.WallW;
        }
        if (properties.Is(IsoFlagType.collideN) || properties.Is(IsoFlagType.windowN) || (gridSquare.getProperties().Is(IsoFlagType.DoorWallN) && !gridSquare.getProperties().Is("GarageDoor"))) {
            if (n4 == array.length) {
                DebugLog.log(DebugType.General, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, gridSquare.x, gridSquare.y, gridSquare.z));
                return;
            }
            array[n4++] = PhysicsShapes.WallN;
        }
        if (gridSquare.Is("PhysicsShape")) {
            if (n4 == array.length) {
                DebugLog.log(DebugType.General, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, gridSquare.x, gridSquare.y, gridSquare.z));
                return;
            }
            final String val3 = gridSquare.getProperties().Val("PhysicsShape");
            if ("Solid".equals(val3)) {
                array[n4++] = PhysicsShapes.Solid;
            }
            else if ("WallN".equals(val3)) {
                array[n4++] = PhysicsShapes.WallN;
            }
            else if ("WallW".equals(val3)) {
                array[n4++] = PhysicsShapes.WallW;
            }
            else if ("WallS".equals(val3)) {
                array[n4++] = PhysicsShapes.WallS;
            }
            else if ("WallE".equals(val3)) {
                array[n4++] = PhysicsShapes.WallE;
            }
            else if ("Tree".equals(val3)) {
                array[n4++] = PhysicsShapes.Tree;
            }
            else if ("Floor".equals(val3)) {
                array[n4++] = PhysicsShapes.Floor;
            }
        }
    }
    
    public boolean LoadBrandNew(final int wx, final int wy) {
        this.wx = wx;
        this.wy = wy;
        if (!CellLoader.LoadCellBinaryChunk(IsoWorld.instance.CurrentCell, wx, wy, this)) {
            return false;
        }
        if (!Core.GameMode.equals("Tutorial") && !Core.GameMode.equals("LastStand") && !GameClient.bClient) {
            this.addZombies = true;
        }
        return true;
    }
    
    public boolean LoadOrCreate(final int wx, final int wy, final ByteBuffer byteBuffer) {
        this.wx = wx;
        this.wy = wy;
        if (byteBuffer != null && !this.blam) {
            return this.LoadFromBuffer(wx, wy, byteBuffer);
        }
        File file = ChunkMapFilenames.instance.getFilename(wx, wy);
        if (file == null) {
            file = ZomboidFileSystem.instance.getFileInCurrentSave(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, IsoChunk.prefix, wx, wy));
        }
        if (file.exists() && !this.blam) {
            Label_0148: {
                try {
                    this.LoadFromDisk();
                    break Label_0148;
                }
                catch (Exception ex) {
                    ExceptionLogger.logException((Throwable)ex, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, wx, wy));
                    if (GameServer.bServer) {
                        LoggerManager.getLogger("map").write(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, wx, wy));
                        LoggerManager.getLogger("map").write(ex);
                    }
                    this.BackupBlam(wx, wy, ex);
                    return false;
                }
                return this.LoadBrandNew(wx, wy);
            }
            if (GameClient.bClient) {
                GameClient.instance.worldObjectsSyncReq.putRequestSyncIsoChunk(this);
            }
            return true;
        }
        return this.LoadBrandNew(wx, wy);
    }
    
    public boolean LoadFromBuffer(final int wx, final int wy, final ByteBuffer byteBuffer) {
        this.wx = wx;
        this.wy = wy;
        if (!this.blam) {
            try {
                this.LoadFromDiskOrBuffer(byteBuffer);
                return true;
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
                if (GameServer.bServer) {
                    LoggerManager.getLogger("map").write(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, wx, wy));
                    LoggerManager.getLogger("map").write(ex);
                }
                this.BackupBlam(wx, wy, ex);
                return false;
            }
        }
        return this.LoadBrandNew(wx, wy);
    }
    
    private void ensureSurroundNotNull(final int n, final int n2, final int n3) {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (i != 0 || j != 0) {
                    if (n + i >= 0 && n + i < 10 && n2 + j >= 0) {
                        if (n2 + j < 10) {
                            if (this.getGridSquare(n + i, n2 + j, n3) == null) {
                                this.setSquare(n + i, n2 + j, n3, IsoGridSquare.getNew(currentCell, null, this.wx * 10 + n + i, this.wy * 10 + n2 + j, n3));
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void loadInWorldStreamerThread() {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = 0; i <= this.maxLevel; ++i) {
            for (int j = 0; j < 10; ++j) {
                for (int k = 0; k < 10; ++k) {
                    IsoGridSquare square = this.getGridSquare(k, j, i);
                    if (square == null && i == 0) {
                        square = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, null, this.wx * 10 + k, this.wy * 10 + j, i);
                        this.setSquare(k, j, i, square);
                    }
                    if (i == 0 && square.getFloor() == null) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, square.x, square.y, square.z));
                        final IsoObject new1 = IsoObject.getNew();
                        new1.sprite = IsoSprite.getSprite(IsoSpriteManager.instance, "carpentry_02_58", 0);
                        new1.square = square;
                        square.Objects.add(0, new1);
                    }
                    if (square != null) {
                        if (i > 0 && !square.getObjects().isEmpty()) {
                            this.ensureSurroundNotNull(k, j, i);
                            for (int l = i - 1; l > 0; --l) {
                                if (this.getGridSquare(k, j, l) == null) {
                                    this.setSquare(k, j, l, IsoGridSquare.getNew(currentCell, null, this.wx * 10 + k, this.wy * 10 + j, l));
                                    this.ensureSurroundNotNull(k, j, l);
                                }
                            }
                        }
                        square.RecalcProperties();
                    }
                }
            }
        }
        assert IsoChunk.chunkGetter.chunk == null;
        IsoChunk.chunkGetter.chunk = this;
        for (int n = 0; n < 10; ++n) {
            for (int n2 = 0; n2 < 10; ++n2) {
                for (int maxLevel = this.maxLevel; maxLevel > 0; --maxLevel) {
                    final IsoGridSquare gridSquare = this.getGridSquare(n2, n, maxLevel);
                    if (gridSquare != null && gridSquare.Is(IsoFlagType.solidfloor)) {
                        --maxLevel;
                        while (maxLevel >= 0) {
                            final IsoGridSquare gridSquare2 = this.getGridSquare(n2, n, maxLevel);
                            if (gridSquare2 != null && !gridSquare2.haveRoof) {
                                gridSquare2.haveRoof = true;
                                gridSquare2.getProperties().UnSet(IsoFlagType.exterior);
                            }
                            --maxLevel;
                        }
                        break;
                    }
                }
            }
        }
        for (int n3 = 0; n3 <= this.maxLevel; ++n3) {
            for (int n4 = 0; n4 < 10; ++n4) {
                for (int n5 = 0; n5 < 10; ++n5) {
                    final IsoGridSquare gridSquare3 = this.getGridSquare(n5, n4, n3);
                    if (gridSquare3 != null) {
                        gridSquare3.RecalcAllWithNeighbours(true, IsoChunk.chunkGetter);
                    }
                }
            }
        }
        IsoChunk.chunkGetter.chunk = null;
        for (int n6 = 0; n6 <= this.maxLevel; ++n6) {
            for (int n7 = 0; n7 < 10; ++n7) {
                for (int n8 = 0; n8 < 10; ++n8) {
                    final IsoGridSquare gridSquare4 = this.getGridSquare(n8, n7, n6);
                    if (gridSquare4 != null) {
                        gridSquare4.propertiesDirty = true;
                    }
                }
            }
        }
    }
    
    private void RecalcAllWithNeighbour(final IsoGridSquare isoGridSquare, final IsoDirections isoDirections, final int n) {
        int n2 = 0;
        int n3 = 0;
        if (isoDirections == IsoDirections.W || isoDirections == IsoDirections.NW || isoDirections == IsoDirections.SW) {
            n2 = -1;
        }
        else if (isoDirections == IsoDirections.E || isoDirections == IsoDirections.NE || isoDirections == IsoDirections.SE) {
            n2 = 1;
        }
        if (isoDirections == IsoDirections.N || isoDirections == IsoDirections.NW || isoDirections == IsoDirections.NE) {
            n3 = -1;
        }
        else if (isoDirections == IsoDirections.S || isoDirections == IsoDirections.SW || isoDirections == IsoDirections.SE) {
            n3 = 1;
        }
        final int n4 = isoGridSquare.getX() + n2;
        final int n5 = isoGridSquare.getY() + n3;
        final int n6 = isoGridSquare.getZ() + n;
        final IsoGridSquare isoGridSquare2 = (n == 0) ? isoGridSquare.nav[isoDirections.index()] : IsoWorld.instance.CurrentCell.getGridSquare(n4, n5, n6);
        if (isoGridSquare2 != null) {
            isoGridSquare.ReCalculateCollide(isoGridSquare2);
            isoGridSquare2.ReCalculateCollide(isoGridSquare);
            isoGridSquare.ReCalculatePathFind(isoGridSquare2);
            isoGridSquare2.ReCalculatePathFind(isoGridSquare);
            isoGridSquare.ReCalculateVisionBlocked(isoGridSquare2);
            isoGridSquare2.ReCalculateVisionBlocked(isoGridSquare);
        }
        if (n == 0) {
            switch (isoDirections) {
                case W: {
                    if (isoGridSquare2 == null) {
                        isoGridSquare.w = null;
                        break;
                    }
                    isoGridSquare.w = (isoGridSquare.testPathFindAdjacent(null, -1, 0, 0) ? null : isoGridSquare2);
                    isoGridSquare2.e = (isoGridSquare2.testPathFindAdjacent(null, 1, 0, 0) ? null : isoGridSquare);
                    break;
                }
                case N: {
                    if (isoGridSquare2 == null) {
                        isoGridSquare.n = null;
                        break;
                    }
                    isoGridSquare.n = (isoGridSquare.testPathFindAdjacent(null, 0, -1, 0) ? null : isoGridSquare2);
                    isoGridSquare2.s = (isoGridSquare2.testPathFindAdjacent(null, 0, 1, 0) ? null : isoGridSquare);
                    break;
                }
                case E: {
                    if (isoGridSquare2 == null) {
                        isoGridSquare.e = null;
                        break;
                    }
                    isoGridSquare.e = (isoGridSquare.testPathFindAdjacent(null, 1, 0, 0) ? null : isoGridSquare2);
                    isoGridSquare2.w = (isoGridSquare2.testPathFindAdjacent(null, -1, 0, 0) ? null : isoGridSquare);
                    break;
                }
                case S: {
                    if (isoGridSquare2 == null) {
                        isoGridSquare.s = null;
                        break;
                    }
                    isoGridSquare.s = (isoGridSquare.testPathFindAdjacent(null, 0, 1, 0) ? null : isoGridSquare2);
                    isoGridSquare2.n = (isoGridSquare2.testPathFindAdjacent(null, 0, -1, 0) ? null : isoGridSquare);
                    break;
                }
                case NW: {
                    if (isoGridSquare2 == null) {
                        isoGridSquare.nw = null;
                        break;
                    }
                    isoGridSquare.nw = (isoGridSquare.testPathFindAdjacent(null, -1, -1, 0) ? null : isoGridSquare2);
                    isoGridSquare2.se = (isoGridSquare2.testPathFindAdjacent(null, 1, 1, 0) ? null : isoGridSquare);
                    break;
                }
                case NE: {
                    if (isoGridSquare2 == null) {
                        isoGridSquare.ne = null;
                        break;
                    }
                    isoGridSquare.ne = (isoGridSquare.testPathFindAdjacent(null, 1, -1, 0) ? null : isoGridSquare2);
                    isoGridSquare2.sw = (isoGridSquare2.testPathFindAdjacent(null, -1, 1, 0) ? null : isoGridSquare);
                    break;
                }
                case SE: {
                    if (isoGridSquare2 == null) {
                        isoGridSquare.se = null;
                        break;
                    }
                    isoGridSquare.se = (isoGridSquare.testPathFindAdjacent(null, 1, 1, 0) ? null : isoGridSquare2);
                    isoGridSquare2.nw = (isoGridSquare2.testPathFindAdjacent(null, -1, -1, 0) ? null : isoGridSquare);
                    break;
                }
                case SW: {
                    if (isoGridSquare2 == null) {
                        isoGridSquare.sw = null;
                        break;
                    }
                    isoGridSquare.sw = (isoGridSquare.testPathFindAdjacent(null, -1, 1, 0) ? null : isoGridSquare2);
                    isoGridSquare2.ne = (isoGridSquare2.testPathFindAdjacent(null, 1, -1, 0) ? null : isoGridSquare);
                    break;
                }
            }
        }
    }
    
    private void EnsureSurroundNotNullX(final int n, final int n2, final int n3) {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = n - 1; i <= n + 1; ++i) {
            if (i >= 0) {
                if (i < 10) {
                    if (this.getGridSquare(i, n2, n3) == null) {
                        currentCell.ConnectNewSquare(IsoGridSquare.getNew(currentCell, null, this.wx * 10 + i, this.wy * 10 + n2, n3), false);
                    }
                }
            }
        }
    }
    
    private void EnsureSurroundNotNullY(final int n, final int n2, final int n3) {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = n2 - 1; i <= n2 + 1; ++i) {
            if (i >= 0) {
                if (i < 10) {
                    if (this.getGridSquare(n, i, n3) == null) {
                        currentCell.ConnectNewSquare(IsoGridSquare.getNew(currentCell, null, this.wx * 10 + n, this.wy * 10 + i, n3), false);
                    }
                }
            }
        }
    }
    
    private void EnsureSurroundNotNull(final int n, final int n2, final int n3) {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        if (this.getGridSquare(n, n2, n3) != null) {
            return;
        }
        currentCell.ConnectNewSquare(IsoGridSquare.getNew(currentCell, null, this.wx * 10 + n, this.wy * 10 + n2, n3), false);
    }
    
    public void loadInMainThread() {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        final IsoChunk chunk = currentCell.getChunk(this.wx - 1, this.wy);
        final IsoChunk chunk2 = currentCell.getChunk(this.wx, this.wy - 1);
        final IsoChunk chunk3 = currentCell.getChunk(this.wx + 1, this.wy);
        final IsoChunk chunk4 = currentCell.getChunk(this.wx, this.wy + 1);
        final IsoChunk chunk5 = currentCell.getChunk(this.wx - 1, this.wy - 1);
        final IsoChunk chunk6 = currentCell.getChunk(this.wx + 1, this.wy - 1);
        final IsoChunk chunk7 = currentCell.getChunk(this.wx + 1, this.wy + 1);
        final IsoChunk chunk8 = currentCell.getChunk(this.wx - 1, this.wy + 1);
        for (int i = 1; i < 8; ++i) {
            for (int j = 0; j < 10; ++j) {
                if (chunk2 != null) {
                    final IsoGridSquare gridSquare = chunk2.getGridSquare(j, 9, i);
                    if (gridSquare != null && !gridSquare.getObjects().isEmpty()) {
                        this.EnsureSurroundNotNullX(j, 0, i);
                    }
                }
                if (chunk4 != null) {
                    final IsoGridSquare gridSquare2 = chunk4.getGridSquare(j, 0, i);
                    if (gridSquare2 != null && !gridSquare2.getObjects().isEmpty()) {
                        this.EnsureSurroundNotNullX(j, 9, i);
                    }
                }
            }
            for (int k = 0; k < 10; ++k) {
                if (chunk != null) {
                    final IsoGridSquare gridSquare3 = chunk.getGridSquare(9, k, i);
                    if (gridSquare3 != null && !gridSquare3.getObjects().isEmpty()) {
                        this.EnsureSurroundNotNullY(0, k, i);
                    }
                }
                if (chunk3 != null) {
                    final IsoGridSquare gridSquare4 = chunk3.getGridSquare(0, k, i);
                    if (gridSquare4 != null && !gridSquare4.getObjects().isEmpty()) {
                        this.EnsureSurroundNotNullY(9, k, i);
                    }
                }
            }
            if (chunk5 != null) {
                final IsoGridSquare gridSquare5 = chunk5.getGridSquare(9, 9, i);
                if (gridSquare5 != null && !gridSquare5.getObjects().isEmpty()) {
                    this.EnsureSurroundNotNull(0, 0, i);
                }
            }
            if (chunk6 != null) {
                final IsoGridSquare gridSquare6 = chunk6.getGridSquare(0, 9, i);
                if (gridSquare6 != null && !gridSquare6.getObjects().isEmpty()) {
                    this.EnsureSurroundNotNull(9, 0, i);
                }
            }
            if (chunk7 != null) {
                final IsoGridSquare gridSquare7 = chunk7.getGridSquare(0, 0, i);
                if (gridSquare7 != null && !gridSquare7.getObjects().isEmpty()) {
                    this.EnsureSurroundNotNull(9, 9, i);
                }
            }
            if (chunk8 != null) {
                final IsoGridSquare gridSquare8 = chunk8.getGridSquare(9, 0, i);
                if (gridSquare8 != null && !gridSquare8.getObjects().isEmpty()) {
                    this.EnsureSurroundNotNull(0, 9, i);
                }
            }
        }
        for (int l = 1; l < 8; ++l) {
            for (int n = 0; n < 10; ++n) {
                if (chunk2 != null) {
                    final IsoGridSquare gridSquare9 = this.getGridSquare(n, 0, l);
                    if (gridSquare9 != null && !gridSquare9.getObjects().isEmpty()) {
                        chunk2.EnsureSurroundNotNullX(n, 9, l);
                    }
                }
                if (chunk4 != null) {
                    final IsoGridSquare gridSquare10 = this.getGridSquare(n, 9, l);
                    if (gridSquare10 != null && !gridSquare10.getObjects().isEmpty()) {
                        chunk4.EnsureSurroundNotNullX(n, 0, l);
                    }
                }
            }
            for (int n2 = 0; n2 < 10; ++n2) {
                if (chunk != null) {
                    final IsoGridSquare gridSquare11 = this.getGridSquare(0, n2, l);
                    if (gridSquare11 != null && !gridSquare11.getObjects().isEmpty()) {
                        chunk.EnsureSurroundNotNullY(9, n2, l);
                    }
                }
                if (chunk3 != null) {
                    final IsoGridSquare gridSquare12 = this.getGridSquare(9, n2, l);
                    if (gridSquare12 != null && !gridSquare12.getObjects().isEmpty()) {
                        chunk3.EnsureSurroundNotNullY(0, n2, l);
                    }
                }
            }
            if (chunk5 != null) {
                final IsoGridSquare gridSquare13 = this.getGridSquare(0, 0, l);
                if (gridSquare13 != null && !gridSquare13.getObjects().isEmpty()) {
                    chunk5.EnsureSurroundNotNull(9, 9, l);
                }
            }
            if (chunk6 != null) {
                final IsoGridSquare gridSquare14 = this.getGridSquare(9, 0, l);
                if (gridSquare14 != null && !gridSquare14.getObjects().isEmpty()) {
                    chunk6.EnsureSurroundNotNull(0, 9, l);
                }
            }
            if (chunk7 != null) {
                final IsoGridSquare gridSquare15 = this.getGridSquare(9, 9, l);
                if (gridSquare15 != null && !gridSquare15.getObjects().isEmpty()) {
                    chunk7.EnsureSurroundNotNull(0, 0, l);
                }
            }
            if (chunk8 != null) {
                final IsoGridSquare gridSquare16 = this.getGridSquare(0, 9, l);
                if (gridSquare16 != null && !gridSquare16.getObjects().isEmpty()) {
                    chunk8.EnsureSurroundNotNull(9, 0, l);
                }
            }
        }
        for (int n3 = 0; n3 <= this.maxLevel; ++n3) {
            for (int n4 = 0; n4 < 10; ++n4) {
                for (int n5 = 0; n5 < 10; ++n5) {
                    final IsoGridSquare gridSquare17 = this.getGridSquare(n5, n4, n3);
                    if (gridSquare17 != null) {
                        if (n5 == 0 || n5 == 9 || n4 == 0 || n4 == 9) {
                            IsoWorld.instance.CurrentCell.DoGridNav(gridSquare17, IsoGridSquare.cellGetSquare);
                            for (int n6 = -1; n6 <= 1; ++n6) {
                                if (n5 == 0) {
                                    this.RecalcAllWithNeighbour(gridSquare17, IsoDirections.W, n6);
                                    this.RecalcAllWithNeighbour(gridSquare17, IsoDirections.NW, n6);
                                    this.RecalcAllWithNeighbour(gridSquare17, IsoDirections.SW, n6);
                                }
                                else if (n5 == 9) {
                                    this.RecalcAllWithNeighbour(gridSquare17, IsoDirections.E, n6);
                                    this.RecalcAllWithNeighbour(gridSquare17, IsoDirections.NE, n6);
                                    this.RecalcAllWithNeighbour(gridSquare17, IsoDirections.SE, n6);
                                }
                                if (n4 == 0) {
                                    this.RecalcAllWithNeighbour(gridSquare17, IsoDirections.N, n6);
                                    if (n5 != 0) {
                                        this.RecalcAllWithNeighbour(gridSquare17, IsoDirections.NW, n6);
                                    }
                                    if (n5 != 9) {
                                        this.RecalcAllWithNeighbour(gridSquare17, IsoDirections.NE, n6);
                                    }
                                }
                                else if (n4 == 9) {
                                    this.RecalcAllWithNeighbour(gridSquare17, IsoDirections.S, n6);
                                    if (n5 != 0) {
                                        this.RecalcAllWithNeighbour(gridSquare17, IsoDirections.SW, n6);
                                    }
                                    if (n5 != 9) {
                                        this.RecalcAllWithNeighbour(gridSquare17, IsoDirections.SE, n6);
                                    }
                                }
                            }
                            final IsoGridSquare isoGridSquare = gridSquare17.nav[IsoDirections.N.index()];
                            final IsoGridSquare isoGridSquare2 = gridSquare17.nav[IsoDirections.S.index()];
                            final IsoGridSquare isoGridSquare3 = gridSquare17.nav[IsoDirections.W.index()];
                            final IsoGridSquare isoGridSquare4 = gridSquare17.nav[IsoDirections.E.index()];
                            if (isoGridSquare != null && isoGridSquare3 != null && (n5 == 0 || n4 == 0)) {
                                this.RecalcAllWithNeighbour(isoGridSquare, IsoDirections.W, 0);
                            }
                            if (isoGridSquare != null && isoGridSquare4 != null && (n5 == 9 || n4 == 0)) {
                                this.RecalcAllWithNeighbour(isoGridSquare, IsoDirections.E, 0);
                            }
                            if (isoGridSquare2 != null && isoGridSquare3 != null && (n5 == 0 || n4 == 9)) {
                                this.RecalcAllWithNeighbour(isoGridSquare2, IsoDirections.W, 0);
                            }
                            if (isoGridSquare2 != null && isoGridSquare4 != null && (n5 == 9 || n4 == 9)) {
                                this.RecalcAllWithNeighbour(isoGridSquare2, IsoDirections.E, 0);
                            }
                        }
                        final IsoRoom room = gridSquare17.getRoom();
                        if (room != null) {
                            room.addSquare(gridSquare17);
                        }
                    }
                }
            }
        }
        for (int n7 = 0; n7 < 4; ++n7) {
            if (chunk != null) {
                chunk.lightCheck[n7] = true;
            }
            if (chunk2 != null) {
                chunk2.lightCheck[n7] = true;
            }
            if (chunk3 != null) {
                chunk3.lightCheck[n7] = true;
            }
            if (chunk4 != null) {
                chunk4.lightCheck[n7] = true;
            }
            if (chunk5 != null) {
                chunk5.lightCheck[n7] = true;
            }
            if (chunk6 != null) {
                chunk6.lightCheck[n7] = true;
            }
            if (chunk7 != null) {
                chunk7.lightCheck[n7] = true;
            }
            if (chunk8 != null) {
                chunk8.lightCheck[n7] = true;
            }
        }
        IsoLightSwitch.chunkLoaded(this);
    }
    
    @Deprecated
    public void recalcNeighboursNow() {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare gridSquare = this.getGridSquare(i, j, k);
                    if (gridSquare != null) {
                        if (k > 0 && !gridSquare.getObjects().isEmpty()) {
                            gridSquare.EnsureSurroundNotNull();
                            for (int l = k - 1; l > 0; --l) {
                                if (this.getGridSquare(i, j, l) == null) {
                                    currentCell.ConnectNewSquare(IsoGridSquare.getNew(currentCell, null, this.wx * 10 + i, this.wy * 10 + j, l), false);
                                }
                            }
                        }
                        gridSquare.RecalcProperties();
                    }
                }
            }
        }
        for (int n = 1; n < 8; ++n) {
            for (int n2 = -1; n2 < 11; ++n2) {
                final IsoGridSquare gridSquare2 = currentCell.getGridSquare(this.wx * 10 + n2, this.wy * 10 - 1, n);
                if (gridSquare2 != null && !gridSquare2.getObjects().isEmpty()) {
                    gridSquare2.EnsureSurroundNotNull();
                }
                final IsoGridSquare gridSquare3 = currentCell.getGridSquare(this.wx * 10 + n2, this.wy * 10 + 10, n);
                if (gridSquare3 != null && !gridSquare3.getObjects().isEmpty()) {
                    gridSquare3.EnsureSurroundNotNull();
                }
            }
            for (int n3 = 0; n3 < 10; ++n3) {
                final IsoGridSquare gridSquare4 = currentCell.getGridSquare(this.wx * 10 - 1, this.wy * 10 + n3, n);
                if (gridSquare4 != null && !gridSquare4.getObjects().isEmpty()) {
                    gridSquare4.EnsureSurroundNotNull();
                }
                final IsoGridSquare gridSquare5 = currentCell.getGridSquare(this.wx * 10 + 10, this.wy * 10 + n3, n);
                if (gridSquare5 != null && !gridSquare5.getObjects().isEmpty()) {
                    gridSquare5.EnsureSurroundNotNull();
                }
            }
        }
        for (int n4 = 0; n4 < 10; ++n4) {
            for (int n5 = 0; n5 < 10; ++n5) {
                for (int n6 = 0; n6 < 8; ++n6) {
                    final IsoGridSquare gridSquare6 = this.getGridSquare(n4, n5, n6);
                    if (gridSquare6 != null) {
                        gridSquare6.RecalcAllWithNeighbours(true);
                        final IsoRoom room = gridSquare6.getRoom();
                        if (room != null) {
                            room.addSquare(gridSquare6);
                        }
                    }
                }
            }
        }
        for (int n7 = 0; n7 < 10; ++n7) {
            for (int n8 = 0; n8 < 10; ++n8) {
                for (int n9 = 0; n9 < 8; ++n9) {
                    final IsoGridSquare gridSquare7 = this.getGridSquare(n7, n8, n9);
                    if (gridSquare7 != null) {
                        gridSquare7.propertiesDirty = true;
                    }
                }
            }
        }
        IsoLightSwitch.chunkLoaded(this);
    }
    
    public void updateBuildings() {
    }
    
    public static void updatePlayerInBullet() {
        Bullet.updatePlayerList(GameServer.getPlayers());
    }
    
    public void update() {
        this.checkPhysics();
        if (!this.loadedPhysics) {
            this.loadedPhysics = true;
            for (int i = 0; i < this.vehicles.size(); ++i) {
                this.vehicles.get(i).chunk = this;
            }
        }
        if (this.vehiclesForAddToWorld != null) {
            synchronized (this.vehiclesForAddToWorldLock) {
                for (int j = 0; j < this.vehiclesForAddToWorld.size(); ++j) {
                    this.vehiclesForAddToWorld.get(j).addToWorld();
                }
                this.vehiclesForAddToWorld.clear();
                this.vehiclesForAddToWorld = null;
            }
        }
        this.updateVehicleStory();
    }
    
    public void updateVehicleStory() {
        if (!this.bLoaded || this.m_vehicleStorySpawnData == null) {
            return;
        }
        final IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(this.wx, this.wy);
        if (metaChunk == null) {
            return;
        }
        final VehicleStorySpawnData vehicleStorySpawnData = this.m_vehicleStorySpawnData;
        for (int i = 0; i < metaChunk.numZones(); ++i) {
            final IsoMetaGrid.Zone zone = metaChunk.getZone(i);
            if (vehicleStorySpawnData.isValid(zone, this)) {
                vehicleStorySpawnData.m_story.randomizeVehicleStory(zone, this);
                final IsoMetaGrid.Zone zone2 = zone;
                ++zone2.hourLastSeen;
                break;
            }
        }
    }
    
    public void setSquare(final int n, final int n2, final int n3, final IsoGridSquare isoGridSquare) {
        assert isoGridSquare.x - this.wx * 10 == n && isoGridSquare.y - this.wy * 10 == n2 && isoGridSquare.z == n3;
        if ((this.squares[n3][n2 * 10 + n] = isoGridSquare) != null) {
            isoGridSquare.chunk = this;
            if (isoGridSquare.z > this.maxLevel) {
                this.maxLevel = isoGridSquare.z;
            }
        }
    }
    
    public IsoGridSquare getGridSquare(final int n, final int n2, final int n3) {
        if (n < 0 || n >= 10 || n2 < 0 || n2 >= 10 || n3 >= 8 || n3 < 0) {
            return null;
        }
        return this.squares[n3][n2 * 10 + n];
    }
    
    public IsoRoom getRoom(final int n) {
        return this.lotheader.getRoom(n);
    }
    
    public void removeFromWorld() {
        IsoChunk.loadGridSquare.remove(this);
        try {
            MapCollisionData.instance.removeChunkFromWorld(this);
            ZombiePopulationManager.instance.removeChunkFromWorld(this);
            PolygonalMap2.instance.removeChunkFromWorld(this);
            this.collision.clear();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        final int n = 100;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < n; ++j) {
                final IsoGridSquare isoGridSquare = this.squares[i][j];
                if (isoGridSquare != null) {
                    RainManager.RemoveAllOn(isoGridSquare);
                    isoGridSquare.clearWater();
                    isoGridSquare.clearPuddles();
                    if (isoGridSquare.getRoom() != null) {
                        isoGridSquare.getRoom().removeSquare(isoGridSquare);
                    }
                    if (isoGridSquare.zone != null) {
                        isoGridSquare.zone.removeSquare(isoGridSquare);
                    }
                    final ArrayList<IsoMovingObject> movingObjects = isoGridSquare.getMovingObjects();
                    for (int k = 0; k < movingObjects.size(); ++k) {
                        final IsoMovingObject isoMovingObject = movingObjects.get(k);
                        if (isoMovingObject instanceof IsoSurvivor) {
                            IsoWorld.instance.CurrentCell.getSurvivorList().remove(isoMovingObject);
                            isoMovingObject.Despawn();
                        }
                        isoMovingObject.removeFromWorld();
                        final IsoMovingObject isoMovingObject2 = isoMovingObject;
                        final IsoMovingObject isoMovingObject3 = isoMovingObject;
                        final IsoGridSquare isoGridSquare2 = null;
                        isoMovingObject3.last = isoGridSquare2;
                        isoMovingObject2.current = isoGridSquare2;
                        if (!movingObjects.contains(isoMovingObject)) {
                            --k;
                        }
                    }
                    movingObjects.clear();
                    for (int l = 0; l < isoGridSquare.getObjects().size(); ++l) {
                        isoGridSquare.getObjects().get(l).removeFromWorld();
                    }
                    for (int index = 0; index < isoGridSquare.getStaticMovingObjects().size(); ++index) {
                        isoGridSquare.getStaticMovingObjects().get(index).removeFromWorld();
                    }
                    this.disconnectFromAdjacentChunks(isoGridSquare);
                    isoGridSquare.softClear();
                    isoGridSquare.chunk = null;
                }
            }
        }
        for (int index2 = 0; index2 < this.vehicles.size(); ++index2) {
            final BaseVehicle baseVehicle = this.vehicles.get(index2);
            if (IsoWorld.instance.CurrentCell.getVehicles().contains(baseVehicle) || IsoWorld.instance.CurrentCell.addVehicles.contains(baseVehicle)) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, baseVehicle.VehicleID));
                baseVehicle.removeFromWorld();
            }
        }
    }
    
    private void disconnectFromAdjacentChunks(final IsoGridSquare isoGridSquare) {
        final int n = isoGridSquare.x % 10;
        final int n2 = isoGridSquare.y % 10;
        if (n != 0 && n != 9 && n2 != 0 && n2 != 9) {
            return;
        }
        final int index = IsoDirections.N.index();
        final int index2 = IsoDirections.S.index();
        if (isoGridSquare.nav[index] != null && isoGridSquare.nav[index].chunk != isoGridSquare.chunk) {
            isoGridSquare.nav[index].nav[index2] = null;
            isoGridSquare.nav[index].s = null;
        }
        final int index3 = IsoDirections.NW.index();
        final int index4 = IsoDirections.SE.index();
        if (isoGridSquare.nav[index3] != null && isoGridSquare.nav[index3].chunk != isoGridSquare.chunk) {
            isoGridSquare.nav[index3].nav[index4] = null;
            isoGridSquare.nav[index3].se = null;
        }
        final int index5 = IsoDirections.W.index();
        final int index6 = IsoDirections.E.index();
        if (isoGridSquare.nav[index5] != null && isoGridSquare.nav[index5].chunk != isoGridSquare.chunk) {
            isoGridSquare.nav[index5].nav[index6] = null;
            isoGridSquare.nav[index5].e = null;
        }
        final int index7 = IsoDirections.SW.index();
        final int index8 = IsoDirections.NE.index();
        if (isoGridSquare.nav[index7] != null && isoGridSquare.nav[index7].chunk != isoGridSquare.chunk) {
            isoGridSquare.nav[index7].nav[index8] = null;
            isoGridSquare.nav[index7].ne = null;
        }
        final int index9 = IsoDirections.S.index();
        final int index10 = IsoDirections.N.index();
        if (isoGridSquare.nav[index9] != null && isoGridSquare.nav[index9].chunk != isoGridSquare.chunk) {
            isoGridSquare.nav[index9].nav[index10] = null;
            isoGridSquare.nav[index9].n = null;
        }
        final int index11 = IsoDirections.SE.index();
        final int index12 = IsoDirections.NW.index();
        if (isoGridSquare.nav[index11] != null && isoGridSquare.nav[index11].chunk != isoGridSquare.chunk) {
            isoGridSquare.nav[index11].nav[index12] = null;
            isoGridSquare.nav[index11].nw = null;
        }
        final int index13 = IsoDirections.E.index();
        final int index14 = IsoDirections.W.index();
        if (isoGridSquare.nav[index13] != null && isoGridSquare.nav[index13].chunk != isoGridSquare.chunk) {
            isoGridSquare.nav[index13].nav[index14] = null;
            isoGridSquare.nav[index13].w = null;
        }
        final int index15 = IsoDirections.NE.index();
        final int index16 = IsoDirections.SW.index();
        if (isoGridSquare.nav[index15] != null && isoGridSquare.nav[index15].chunk != isoGridSquare.chunk) {
            isoGridSquare.nav[index15].nav[index16] = null;
            isoGridSquare.nav[index15].sw = null;
        }
    }
    
    public void doReuseGridsquares() {
        final int n = 100;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < n; ++j) {
                final IsoGridSquare isoGridSquare = this.squares[i][j];
                if (isoGridSquare != null) {
                    LuaEventManager.triggerEvent("ReuseGridsquare", isoGridSquare);
                    for (int k = 0; k < isoGridSquare.getObjects().size(); ++k) {
                        final IsoObject e = isoGridSquare.getObjects().get(k);
                        if (e instanceof IsoTree) {
                            e.reset();
                            CellLoader.isoTreeCache.add((IsoTree)e);
                        }
                        else if (e instanceof IsoObject && e.getObjectName().equals("IsoObject")) {
                            e.reset();
                            CellLoader.isoObjectCache.add(e);
                        }
                        else {
                            e.reuseGridSquare();
                        }
                    }
                    isoGridSquare.discard();
                    this.squares[i][j] = null;
                }
            }
        }
        this.resetForStore();
        assert !IsoChunkMap.chunkStore.contains(this);
        IsoChunkMap.chunkStore.add(this);
    }
    
    private static int bufferSize(final int n) {
        return (n + 65536 - 1) / 65536 * 65536;
    }
    
    private static ByteBuffer ensureCapacity(ByteBuffer allocate, final int n) {
        if (allocate == null || allocate.capacity() < n) {
            allocate = ByteBuffer.allocate(bufferSize(n));
        }
        return allocate;
    }
    
    private static ByteBuffer ensureCapacity(final ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return ByteBuffer.allocate(65536);
        }
        if (byteBuffer.capacity() - byteBuffer.position() < 65536) {
            return ensureCapacity(null, byteBuffer.position() + 65536).put(byteBuffer.array(), 0, byteBuffer.position());
        }
        return byteBuffer;
    }
    
    public void LoadFromDisk() throws IOException {
        this.LoadFromDiskOrBuffer(null);
    }
    
    public void LoadFromDiskOrBuffer(final ByteBuffer byteBuffer) throws IOException {
        IsoChunk.sanityCheck.beginLoad(this);
        try {
            ByteBuffer sliceBufferLoad;
            if (byteBuffer == null) {
                IsoChunk.SliceBufferLoad = SafeRead(IsoChunk.prefix, this.wx, this.wy, IsoChunk.SliceBufferLoad);
                sliceBufferLoad = IsoChunk.SliceBufferLoad;
            }
            else {
                sliceBufferLoad = byteBuffer;
            }
            final String header = ChunkMapFilenames.instance.getHeader(this.wx * 10 / 300, this.wy * 10 / 300);
            if (IsoLot.InfoHeaders.containsKey(header)) {
                this.lotheader = IsoLot.InfoHeaders.get(header);
            }
            IsoCell.wx = this.wx;
            IsoCell.wy = this.wy;
            final boolean b = sliceBufferLoad.get() == 1;
            final int int1 = sliceBufferLoad.getInt();
            if (b) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(IZ)Ljava/lang/String;, int1, b));
            }
            if (int1 > 186) {
                throw new RuntimeException(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, int1, this.wx, this.wy));
            }
            this.bFixed2x = (int1 >= 85);
            if (int1 >= 61) {
                IsoChunk.sanityCheck.checkLength(sliceBufferLoad.getInt(), sliceBufferLoad.limit());
                final long long1 = sliceBufferLoad.getLong();
                IsoChunk.crcLoad.reset();
                IsoChunk.crcLoad.update(sliceBufferLoad.array(), 17, sliceBufferLoad.limit() - 1 - 4 - 4 - 8);
                IsoChunk.sanityCheck.checkCRC(long1, IsoChunk.crcLoad.getValue());
            }
            int value = 0;
            if (GameClient.bClient || GameServer.bServer) {
                value = ServerOptions.getInstance().BloodSplatLifespanDays.getValue();
            }
            final float worldAge = (float)GameTime.getInstance().getWorldAgeHours();
            for (int int2 = sliceBufferLoad.getInt(), i = 0; i < int2; ++i) {
                final IsoFloorBloodSplat isoFloorBloodSplat = new IsoFloorBloodSplat();
                isoFloorBloodSplat.load(sliceBufferLoad, int1);
                if (isoFloorBloodSplat.worldAge > worldAge) {
                    isoFloorBloodSplat.worldAge = worldAge;
                }
                if (value <= 0 || worldAge - isoFloorBloodSplat.worldAge < value * 24) {
                    if (int1 < 73 && isoFloorBloodSplat.Type < 8) {
                        isoFloorBloodSplat.index = ++this.nextSplatIndex;
                    }
                    if (isoFloorBloodSplat.Type < 8) {
                        this.nextSplatIndex = isoFloorBloodSplat.index % 10;
                    }
                    this.FloorBloodSplats.add(isoFloorBloodSplat);
                }
            }
            final IsoMetaGrid metaGrid = IsoWorld.instance.getMetaGrid();
            for (int j = 0; j < 10; ++j) {
                for (int k = 0; k < 10; ++k) {
                    final byte value2 = sliceBufferLoad.get();
                    for (int l = 0; l < 8; ++l) {
                        IsoGridSquare isoGridSquare = null;
                        int n = 0;
                        if ((value2 & 1 << l) != 0x0) {
                            n = 1;
                        }
                        if (n == 1) {
                            if (isoGridSquare == null) {
                                if (IsoGridSquare.loadGridSquareCache != null) {
                                    isoGridSquare = IsoGridSquare.getNew(IsoGridSquare.loadGridSquareCache, IsoWorld.instance.CurrentCell, null, j + this.wx * 10, k + this.wy * 10, l);
                                }
                                else {
                                    isoGridSquare = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, null, j + this.wx * 10, k + this.wy * 10, l);
                                }
                            }
                            isoGridSquare.chunk = this;
                            if (this.lotheader != null) {
                                final RoomDef room = metaGrid.getRoomAt(isoGridSquare.x, isoGridSquare.y, isoGridSquare.z);
                                isoGridSquare.setRoomID((room != null) ? room.ID : -1);
                                final RoomDef emptyOutside = metaGrid.getEmptyOutsideAt(isoGridSquare.x, isoGridSquare.y, isoGridSquare.z);
                                if (emptyOutside != null) {
                                    final IsoRoom room2 = this.getRoom(emptyOutside.ID);
                                    isoGridSquare.roofHideBuilding = ((room2 == null) ? null : room2.building);
                                }
                            }
                            isoGridSquare.ResetIsoWorldRegion();
                            this.setSquare(j, k, l, isoGridSquare);
                        }
                        if (n == 1 && isoGridSquare != null) {
                            isoGridSquare.load(sliceBufferLoad, int1, b);
                            isoGridSquare.FixStackableObjects();
                            if (this.jobType == JobType.SoftReset) {
                                if (!isoGridSquare.getStaticMovingObjects().isEmpty()) {
                                    isoGridSquare.getStaticMovingObjects().clear();
                                }
                                for (int n2 = 0; n2 < isoGridSquare.getObjects().size(); ++n2) {
                                    final IsoObject isoObject = isoGridSquare.getObjects().get(n2);
                                    isoObject.softReset();
                                    if (isoObject.getObjectIndex() == -1) {
                                        --n2;
                                    }
                                }
                                isoGridSquare.setOverlayDone(false);
                            }
                        }
                    }
                }
            }
            if (int1 >= 45) {
                this.getErosionData().load(sliceBufferLoad, int1);
                this.getErosionData().set(this);
            }
            if (int1 >= 127) {
                final short short1 = sliceBufferLoad.getShort();
                if (short1 > 0 && this.generatorsTouchingThisChunk == null) {
                    this.generatorsTouchingThisChunk = new ArrayList<IsoGameCharacter.Location>();
                }
                if (this.generatorsTouchingThisChunk != null) {
                    this.generatorsTouchingThisChunk.clear();
                }
                for (short n3 = 0; n3 < short1; ++n3) {
                    this.generatorsTouchingThisChunk.add(new IsoGameCharacter.Location(sliceBufferLoad.getInt(), sliceBufferLoad.getInt(), sliceBufferLoad.get()));
                }
            }
            this.vehicles.clear();
            if (!GameClient.bClient) {
                if (int1 >= 91) {
                    for (short short2 = sliceBufferLoad.getShort(), n4 = 0; n4 < short2; ++n4) {
                        final byte value3 = sliceBufferLoad.get();
                        final byte value4 = sliceBufferLoad.get();
                        final byte value5 = sliceBufferLoad.get();
                        final IsoObject factoryFromFileInput = IsoObject.factoryFromFileInput(IsoWorld.instance.CurrentCell, sliceBufferLoad);
                        if (factoryFromFileInput != null) {
                            if (factoryFromFileInput instanceof BaseVehicle) {
                                final IsoGridSquare gridSquare = this.getGridSquare(value3, value4, value5);
                                factoryFromFileInput.square = gridSquare;
                                ((BaseVehicle)factoryFromFileInput).current = gridSquare;
                                try {
                                    factoryFromFileInput.load(sliceBufferLoad, int1, b);
                                    this.vehicles.add((BaseVehicle)factoryFromFileInput);
                                    addFromCheckedVehicles((BaseVehicle)factoryFromFileInput);
                                    if (this.jobType == JobType.SoftReset) {
                                        factoryFromFileInput.softReset();
                                    }
                                }
                                catch (Exception cause) {
                                    throw new RuntimeException(cause);
                                }
                            }
                        }
                    }
                }
                if (int1 >= 125) {
                    this.lootRespawnHour = sliceBufferLoad.getInt();
                }
                if (int1 >= 160) {
                    for (byte value6 = sliceBufferLoad.get(), b2 = 0; b2 < value6; ++b2) {
                        this.addSpawnedRoom(sliceBufferLoad.getInt());
                    }
                }
            }
        }
        finally {
            IsoChunk.sanityCheck.endLoad(this);
            this.bFixed2x = true;
        }
        if (this.getGridSquare(0, 0, 0) == null && this.getGridSquare(9, 9, 0) == null) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.wx, this.wy));
        }
    }
    
    public void doLoadGridsquare() {
        if (!GameServer.bServer) {
            this.loadInMainThread();
        }
        if (this.addZombies && !VehiclesDB2.instance.isChunkSeen(this.wx, this.wy)) {
            try {
                this.AddVehicles();
            }
            catch (Throwable t) {
                ExceptionLogger.logException(t);
            }
        }
        this.AddZombieZoneStory();
        VehiclesDB2.instance.setChunkSeen(this.wx, this.wy);
        if (this.addZombies) {
            if (IsoWorld.instance.getTimeSinceLastSurvivorInHorde() > 0) {
                IsoWorld.instance.setTimeSinceLastSurvivorInHorde(IsoWorld.instance.getTimeSinceLastSurvivorInHorde() - 1);
            }
            this.addSurvivorInHorde(false);
        }
        this.update();
        if (!GameServer.bServer) {
            FliesSound.instance.chunkLoaded(this);
            NearestWalls.chunkLoaded(this);
        }
        if (this.addZombies) {
            final int min = Math.min(20, 5 + SandboxOptions.instance.TimeSinceApo.getValue());
            if (Rand.Next(min) == 0) {
                this.AddCorpses(this.wx, this.wy);
            }
            if (Rand.Next(min * 2) == 0) {
                this.AddBlood(this.wx, this.wy);
            }
        }
        LoadGridsquarePerformanceWorkaround.init(this.wx, this.wy);
        IsoChunk.tempBuildings.clear();
        if (!GameClient.bClient) {
            for (int i = 0; i < this.vehicles.size(); ++i) {
                final BaseVehicle baseVehicle = this.vehicles.get(i);
                if (!baseVehicle.addedToWorld && VehiclesDB2.instance.isVehicleLoaded(baseVehicle)) {
                    baseVehicle.removeFromSquare();
                    this.vehicles.remove(i);
                    --i;
                }
                else {
                    if (!baseVehicle.addedToWorld) {
                        baseVehicle.addToWorld();
                    }
                    if (baseVehicle.sqlID == -1) {
                        assert false;
                        if (baseVehicle.square == null) {
                            final float n = 5.0E-4f;
                            final int n2 = this.wx * 10;
                            final int n3 = this.wy * 10;
                            baseVehicle.square = this.getGridSquare((int)PZMath.clamp(baseVehicle.x, n2 + n, n2 + 10 - n) - this.wx * 10, (int)PZMath.clamp(baseVehicle.y, n3 + n, n3 + 10 - n) - this.wy * 10, 0);
                        }
                        VehiclesDB2.instance.addVehicle(baseVehicle);
                    }
                }
            }
        }
        this.m_treeCount = 0;
        this.m_scavengeZone = null;
        this.m_numberOfWaterTiles = 0;
        for (int j = 0; j <= this.maxLevel; ++j) {
            for (int k = 0; k < 10; ++k) {
                for (int l = 0; l < 10; ++l) {
                    final IsoGridSquare gridSquare = this.getGridSquare(k, l, j);
                    if (gridSquare != null && !gridSquare.getObjects().isEmpty()) {
                        for (int n4 = 0; n4 < gridSquare.getObjects().size(); ++n4) {
                            final IsoObject isoObject = gridSquare.getObjects().get(n4);
                            isoObject.addToWorld();
                            if (j == 0 && isoObject.getSprite() != null && isoObject.getSprite().getProperties().Is(IsoFlagType.water)) {
                                ++this.m_numberOfWaterTiles;
                            }
                        }
                        if (gridSquare.HasTree()) {
                            ++this.m_treeCount;
                        }
                        if (this.jobType != JobType.SoftReset) {
                            ErosionMain.LoadGridsquare(gridSquare);
                        }
                        if (this.addZombies) {
                            MapObjects.newGridSquare(gridSquare);
                        }
                        MapObjects.loadGridSquare(gridSquare);
                        LuaEventManager.triggerEvent("LoadGridsquare", gridSquare);
                        LoadGridsquarePerformanceWorkaround.LoadGridsquare(gridSquare);
                    }
                    if (gridSquare != null && !gridSquare.getStaticMovingObjects().isEmpty()) {
                        for (int index = 0; index < gridSquare.getStaticMovingObjects().size(); ++index) {
                            gridSquare.getStaticMovingObjects().get(index).addToWorld();
                        }
                    }
                    if (gridSquare != null && gridSquare.getBuilding() != null && !IsoChunk.tempBuildings.contains(gridSquare.getBuilding())) {
                        IsoChunk.tempBuildings.add(gridSquare.getBuilding());
                    }
                }
            }
        }
        if (this.jobType != JobType.SoftReset) {
            ErosionMain.ChunkLoaded(this);
        }
        if (this.jobType != JobType.SoftReset) {
            SGlobalObjects.chunkLoaded(this.wx, this.wy);
        }
        ReanimatedPlayers.instance.addReanimatedPlayersToChunk(this);
        if (this.jobType != JobType.SoftReset) {
            MapCollisionData.instance.addChunkToWorld(this);
            ZombiePopulationManager.instance.addChunkToWorld(this);
            PolygonalMap2.instance.addChunkToWorld(this);
            IsoGenerator.chunkLoaded(this);
            LootRespawn.chunkLoaded(this);
        }
        if (!GameServer.bServer) {
            final ArrayList<IsoRoomLight> roomLights = IsoWorld.instance.CurrentCell.roomLights;
            for (int index2 = 0; index2 < this.roomLights.size(); ++index2) {
                final IsoRoomLight isoRoomLight = this.roomLights.get(index2);
                if (!roomLights.contains(isoRoomLight)) {
                    roomLights.add(isoRoomLight);
                }
            }
        }
        this.roomLights.clear();
        IsoChunk.tempRoomDefs.clear();
        IsoWorld.instance.MetaGrid.getRoomsIntersecting(this.wx * 10 - 1, this.wy * 10 - 1, 11, 11, IsoChunk.tempRoomDefs);
        for (int index3 = 0; index3 < IsoChunk.tempRoomDefs.size(); ++index3) {
            final IsoRoom isoRoom = IsoChunk.tempRoomDefs.get(index3).getIsoRoom();
            if (isoRoom != null) {
                final IsoBuilding building = isoRoom.getBuilding();
                if (!IsoChunk.tempBuildings.contains(building)) {
                    IsoChunk.tempBuildings.add(building);
                }
            }
        }
        for (int index4 = 0; index4 < IsoChunk.tempBuildings.size(); ++index4) {
            final IsoBuilding isoBuilding = IsoChunk.tempBuildings.get(index4);
            if (!GameClient.bClient && isoBuilding.def != null && isoBuilding.def.isFullyStreamedIn()) {
                StashSystem.doBuildingStash(isoBuilding.def);
            }
            RandomizedBuildingBase.ChunkLoaded(isoBuilding);
        }
        if (!GameClient.bClient && !IsoChunk.tempBuildings.isEmpty()) {
            for (int index5 = 0; index5 < IsoChunk.tempBuildings.size(); ++index5) {
                final IsoBuilding isoBuilding2 = IsoChunk.tempBuildings.get(index5);
                for (int index6 = 0; index6 < isoBuilding2.Rooms.size(); ++index6) {
                    final IsoRoom isoRoom2 = isoBuilding2.Rooms.get(index6);
                    if (isoRoom2.def.bDoneSpawn) {
                        if (!this.isSpawnedRoom(isoRoom2.def.ID)) {
                            if (isoRoom2.def.intersects(this.wx * 10, this.wy * 10, 10, 10)) {
                                this.addSpawnedRoom(isoRoom2.def.ID);
                                VirtualZombieManager.instance.addIndoorZombiesToChunk(this, isoRoom2);
                            }
                        }
                    }
                }
            }
        }
        this.checkAdjacentChunks();
        try {
            if (GameServer.bServer && this.jobType != JobType.SoftReset) {
                for (int n5 = 0; n5 < GameServer.udpEngine.connections.size(); ++n5) {
                    final UdpConnection udpConnection = GameServer.udpEngine.connections.get(n5);
                    if (!udpConnection.chunkObjectState.isEmpty()) {
                        for (int n6 = 0; n6 < udpConnection.chunkObjectState.size(); n6 += 2) {
                            final short value = udpConnection.chunkObjectState.get(n6);
                            final short value2 = udpConnection.chunkObjectState.get(n6 + 1);
                            if (value == this.wx && value2 == this.wy) {
                                udpConnection.chunkObjectState.remove(n6, 2);
                                n6 -= 2;
                                final ByteBufferWriter startPacket = udpConnection.startPacket();
                                PacketTypes.PacketType.ChunkObjectState.doPacket(startPacket);
                                startPacket.putShort((short)this.wx);
                                startPacket.putShort((short)this.wy);
                                try {
                                    if (this.saveObjectState(startPacket.bb)) {
                                        PacketTypes.PacketType.ChunkObjectState.send(udpConnection);
                                    }
                                    else {
                                        udpConnection.cancelPacket();
                                    }
                                }
                                catch (Throwable t2) {
                                    t2.printStackTrace();
                                    udpConnection.cancelPacket();
                                }
                            }
                        }
                    }
                }
            }
            if (GameClient.bClient) {
                final ByteBufferWriter startPacket2 = GameClient.connection.startPacket();
                PacketTypes.PacketType.ChunkObjectState.doPacket(startPacket2);
                startPacket2.putShort((short)this.wx);
                startPacket2.putShort((short)this.wy);
                PacketTypes.PacketType.ChunkObjectState.send(GameClient.connection);
            }
        }
        catch (Throwable t3) {
            ExceptionLogger.logException(t3);
        }
    }
    
    private void checkAdjacentChunks() {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (j != 0 || i != 0) {
                    final IsoChunk chunk = currentCell.getChunk(this.wx + j, this.wy + i);
                    if (chunk != null) {
                        final IsoChunk isoChunk = chunk;
                        ++isoChunk.m_adjacentChunkLoadedCounter;
                    }
                }
            }
        }
    }
    
    private void AddZombieZoneStory() {
        final IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(this.wx, this.wy);
        if (metaChunk == null) {
            return;
        }
        for (int i = 0; i < metaChunk.numZones(); ++i) {
            RandomizedZoneStoryBase.isValidForStory(metaChunk.getZone(i), false);
        }
    }
    
    public void setCache() {
        IsoWorld.instance.CurrentCell.setCacheChunk(this);
    }
    
    private static ChunkLock acquireLock(final int n, final int n2) {
        synchronized (IsoChunk.Locks) {
            for (int i = 0; i < IsoChunk.Locks.size(); ++i) {
                if (IsoChunk.Locks.get(i).wx == n && IsoChunk.Locks.get(i).wy == n2) {
                    return IsoChunk.Locks.get(i).ref();
                }
            }
            final ChunkLock e = IsoChunk.FreeLocks.isEmpty() ? new ChunkLock(n, n2) : IsoChunk.FreeLocks.pop().set(n, n2);
            IsoChunk.Locks.add(e);
            return e.ref();
        }
    }
    
    private static void releaseLock(final ChunkLock chunkLock) {
        synchronized (IsoChunk.Locks) {
            if (chunkLock.deref() == 0) {
                IsoChunk.Locks.remove(chunkLock);
                IsoChunk.FreeLocks.push(chunkLock);
            }
        }
    }
    
    public void setCacheIncludingNull() {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 10; ++j) {
                for (int k = 0; k < 10; ++k) {
                    IsoWorld.instance.CurrentCell.setCacheGridSquare(this.wx * 10 + j, this.wy * 10 + k, i, this.getGridSquare(j, k, i));
                }
            }
        }
    }
    
    public void Save(final boolean b) throws IOException {
        if (Core.getInstance().isNoSave() || GameClient.bClient) {
            if (!b && !GameServer.bServer && this.jobType != JobType.Convert) {
                WorldReuserThread.instance.addReuseChunk(this);
            }
            return;
        }
        synchronized (IsoChunk.WriteLock) {
            IsoChunk.sanityCheck.beginSave(this);
            try {
                final File dir = ChunkMapFilenames.instance.getDir(Core.GameSaveWorld);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                IsoChunk.SliceBuffer = this.Save(IsoChunk.SliceBuffer, IsoChunk.crcSave);
                if (GameClient.bClient || GameServer.bServer) {
                    final long checksumIfExists = ChunkChecksum.getChecksumIfExists(this.wx, this.wy);
                    IsoChunk.crcSave.reset();
                    IsoChunk.crcSave.update(IsoChunk.SliceBuffer.array(), 0, IsoChunk.SliceBuffer.position());
                    if (checksumIfExists != IsoChunk.crcSave.getValue()) {
                        ChunkChecksum.setChecksum(this.wx, this.wy, IsoChunk.crcSave.getValue());
                        SafeWrite(IsoChunk.prefix, this.wx, this.wy, IsoChunk.SliceBuffer);
                    }
                }
                else {
                    SafeWrite(IsoChunk.prefix, this.wx, this.wy, IsoChunk.SliceBuffer);
                }
                if (!b && !GameServer.bServer) {
                    if (this.jobType != JobType.Convert) {
                        WorldReuserThread.instance.addReuseChunk(this);
                    }
                    else {
                        this.doReuseGridsquares();
                    }
                }
            }
            finally {
                IsoChunk.sanityCheck.endSave(this);
            }
        }
    }
    
    public static void SafeWrite(final String s, final int n, final int n2, final ByteBuffer byteBuffer) throws IOException {
        final ChunkLock acquireLock = acquireLock(n, n2);
        acquireLock.lockForWriting();
        try {
            final File filename = ChunkMapFilenames.instance.getFilename(n, n2);
            IsoChunk.sanityCheck.beginSaveFile(filename.getAbsolutePath());
            try {
                final FileOutputStream fileOutputStream = new FileOutputStream(filename);
                fileOutputStream.getChannel().truncate(0L);
                fileOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            finally {
                IsoChunk.sanityCheck.endSaveFile();
            }
        }
        finally {
            acquireLock.unlockForWriting();
            releaseLock(acquireLock);
        }
    }
    
    public static ByteBuffer SafeRead(final String s, final int n, final int n2, ByteBuffer ensureCapacity) throws IOException {
        final ChunkLock acquireLock = acquireLock(n, n2);
        acquireLock.lockForReading();
        try {
            File file = ChunkMapFilenames.instance.getFilename(n, n2);
            if (file == null) {
                file = ZomboidFileSystem.instance.getFileInCurrentSave(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, s, n, n2));
            }
            IsoChunk.sanityCheck.beginLoadFile(file.getAbsolutePath());
            try {
                final FileInputStream fileInputStream = new FileInputStream(file);
                try {
                    ensureCapacity = ensureCapacity(ensureCapacity, (int)file.length());
                    ensureCapacity.clear();
                    ensureCapacity.limit(fileInputStream.read(ensureCapacity.array()));
                    fileInputStream.close();
                }
                catch (Throwable t) {
                    try {
                        fileInputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
            }
            finally {
                IsoChunk.sanityCheck.endLoadFile(file.getAbsolutePath());
            }
        }
        finally {
            acquireLock.unlockForReading();
            releaseLock(acquireLock);
        }
        return ensureCapacity;
    }
    
    public void SaveLoadedChunk(final ClientChunkRequest.Chunk chunk, final CRC32 crc32) throws IOException {
        chunk.bb = this.Save(chunk.bb, crc32);
    }
    
    public static boolean IsDebugSave() {
        return !Core.bDebug && false;
    }
    
    public ByteBuffer Save(ByteBuffer byteBuffer, final CRC32 crc32) throws IOException {
        byteBuffer.rewind();
        byteBuffer = ensureCapacity(byteBuffer);
        byteBuffer.rewind();
        byteBuffer.put((byte)(IsDebugSave() ? 1 : 0));
        byteBuffer.putInt(186);
        byteBuffer.putInt(0);
        byteBuffer.putLong(0L);
        final int min = Math.min(1000, this.FloorBloodSplats.size());
        final int n = this.FloorBloodSplats.size() - min;
        byteBuffer.putInt(min);
        for (int i = n; i < this.FloorBloodSplats.size(); ++i) {
            this.FloorBloodSplats.get(i).save(byteBuffer);
        }
        byteBuffer.position();
        for (int j = 0; j < 10; ++j) {
            for (int k = 0; k < 10; ++k) {
                byte b = 0;
                final int position = byteBuffer.position();
                byteBuffer.put(b);
                for (int l = 0; l < 8; ++l) {
                    final IsoGridSquare gridSquare = this.getGridSquare(j, k, l);
                    byteBuffer = ensureCapacity(byteBuffer);
                    if (gridSquare != null && gridSquare.shouldSave()) {
                        b |= (byte)(1 << l);
                        final int position2 = byteBuffer.position();
                        while (true) {
                            try {
                                gridSquare.save(byteBuffer, null, IsDebugSave());
                            }
                            catch (BufferOverflowException ex) {
                                DebugLog.log("IsoChunk.Save: BufferOverflowException, growing ByteBuffer");
                                byteBuffer = ensureCapacity(byteBuffer);
                                byteBuffer.position(position2);
                                continue;
                            }
                            break;
                        }
                    }
                }
                final int position3 = byteBuffer.position();
                byteBuffer.position(position);
                byteBuffer.put(b);
                byteBuffer.position(position3);
            }
        }
        byteBuffer = ensureCapacity(byteBuffer);
        this.getErosionData().save(byteBuffer);
        if (this.generatorsTouchingThisChunk == null) {
            byteBuffer.putShort((short)0);
        }
        else {
            byteBuffer.putShort((short)this.generatorsTouchingThisChunk.size());
            for (int index = 0; index < this.generatorsTouchingThisChunk.size(); ++index) {
                final IsoGameCharacter.Location location = this.generatorsTouchingThisChunk.get(index);
                byteBuffer.putInt(location.x);
                byteBuffer.putInt(location.y);
                byteBuffer.put((byte)location.z);
            }
        }
        byteBuffer.putShort((short)0);
        if (!GameClient.bClient && !GameWindow.bLoadedAsClient) {
            VehiclesDB2.instance.unloadChunk(this);
        }
        if (GameClient.bClient) {
            final int value = ServerOptions.instance.HoursForLootRespawn.getValue();
            if (value <= 0 || GameTime.getInstance().getWorldAgeHours() < value) {
                this.lootRespawnHour = -1;
            }
            else {
                this.lootRespawnHour = 7 + (int)(GameTime.getInstance().getWorldAgeHours() / value) * value;
            }
        }
        byteBuffer.putInt(this.lootRespawnHour);
        assert this.m_spawnedRooms.size() <= 127;
        byteBuffer.put((byte)this.m_spawnedRooms.size());
        for (int n2 = 0; n2 < this.m_spawnedRooms.size(); ++n2) {
            byteBuffer.putInt(this.m_spawnedRooms.get(n2));
        }
        final int position4 = byteBuffer.position();
        crc32.reset();
        crc32.update(byteBuffer.array(), 17, position4 - 1 - 4 - 4 - 8);
        byteBuffer.position(5);
        byteBuffer.putInt(position4);
        byteBuffer.putLong(crc32.getValue());
        byteBuffer.position(position4);
        return byteBuffer;
    }
    
    public boolean saveObjectState(final ByteBuffer byteBuffer) throws IOException {
        boolean b = true;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 10; ++j) {
                for (int k = 0; k < 10; ++k) {
                    final IsoGridSquare gridSquare = this.getGridSquare(k, j, i);
                    if (gridSquare != null) {
                        final int size = gridSquare.getObjects().size();
                        final IsoObject[] array = gridSquare.getObjects().getElements();
                        for (int l = 0; l < size; ++l) {
                            final IsoObject isoObject = array[l];
                            final int position = byteBuffer.position();
                            byteBuffer.position(position + 2 + 2 + 4 + 2);
                            final int position2 = byteBuffer.position();
                            isoObject.saveState(byteBuffer);
                            final int position3 = byteBuffer.position();
                            if (position3 > position2) {
                                byteBuffer.position(position);
                                byteBuffer.putShort((short)(k + j * 10 + i * 10 * 10));
                                byteBuffer.putShort((short)l);
                                byteBuffer.putInt(isoObject.getObjectName().hashCode());
                                byteBuffer.putShort((short)(position3 - position2));
                                byteBuffer.position(position3);
                                b = false;
                            }
                            else {
                                byteBuffer.position(position);
                            }
                        }
                    }
                }
            }
        }
        if (b) {
            return false;
        }
        byteBuffer.putShort((short)(-1));
        return true;
    }
    
    public void loadObjectState(final ByteBuffer byteBuffer) throws IOException {
        for (short n = byteBuffer.getShort(); n != -1; n = byteBuffer.getShort()) {
            final int n2 = n % 10;
            final int n3 = n / 100;
            final int n4 = (n - n3 * 10 * 10) / 10;
            final short short1 = byteBuffer.getShort();
            final int int1 = byteBuffer.getInt();
            final short short2 = byteBuffer.getShort();
            final int position = byteBuffer.position();
            final IsoGridSquare gridSquare = this.getGridSquare(n2, n4, n3);
            if (gridSquare != null && short1 >= 0 && short1 < gridSquare.getObjects().size()) {
                final IsoObject isoObject = gridSquare.getObjects().get(short1);
                if (int1 == isoObject.getObjectName().hashCode()) {
                    isoObject.loadState(byteBuffer);
                    assert byteBuffer.position() == position + short2;
                }
                else {
                    byteBuffer.position(position + short2);
                }
            }
            else {
                byteBuffer.position(position + short2);
            }
        }
    }
    
    public void Blam(final int n, final int n2) {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 10; ++j) {
                for (int k = 0; k < 10; ++k) {
                    this.setSquare(j, k, i, null);
                }
            }
        }
        this.blam = true;
    }
    
    private void BackupBlam(final int n, final int n2, final Exception ex) {
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("blam");
        fileInCurrentSave.mkdirs();
        try {
            final PrintStream s = new PrintStream(new FileOutputStream(new File(invokedynamic(makeConcatWithConstants:(Ljava/io/File;Ljava/lang/String;II)Ljava/lang/String;, fileInCurrentSave, File.separator, n, n2))));
            ex.printStackTrace(s);
            s.close();
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
        final File fileInCurrentSave2 = ZomboidFileSystem.instance.getFileInCurrentSave(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n, n2));
        if (!fileInCurrentSave2.exists()) {
            return;
        }
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;II)Ljava/lang/String;, fileInCurrentSave.getPath(), File.separator, n, n2));
        try {
            copyFile(fileInCurrentSave2, file);
        }
        catch (Exception ex3) {
            ex3.printStackTrace();
        }
    }
    
    private static void copyFile(final File file, final File file2) throws IOException {
        if (!file2.exists()) {
            file2.createNewFile();
        }
        FileChannel channel = null;
        FileChannel channel2 = null;
        try {
            channel = new FileInputStream(file).getChannel();
            channel2 = new FileOutputStream(file2).getChannel();
            channel2.transferFrom(channel, 0L, channel.size());
        }
        finally {
            if (channel != null) {
                channel.close();
            }
            if (channel2 != null) {
                channel2.close();
            }
        }
    }
    
    public ErosionData.Chunk getErosionData() {
        if (this.erosion == null) {
            this.erosion = new ErosionData.Chunk();
        }
        return this.erosion;
    }
    
    private static int newtiledefinitions(final int n, final int n2) {
        return 1 * 100 * 1000 + 10000 + n * 1000 + n2;
    }
    
    public static int Fix2x(final IsoGridSquare isoGridSquare, final int n) {
        if (isoGridSquare == null || isoGridSquare.chunk == null) {
            return n;
        }
        if (isoGridSquare.chunk.bFixed2x) {
            return n;
        }
        final HashMap<String, IsoSprite> namedMap = IsoSpriteManager.instance.NamedMap;
        if (n >= newtiledefinitions(140, 48) && n <= newtiledefinitions(140, 51)) {
            return -1;
        }
        if (n >= newtiledefinitions(8, 14) && n <= newtiledefinitions(8, 71) && n % 8 >= 6) {
            return -1;
        }
        if (n == newtiledefinitions(92, 2)) {
            return n + 20;
        }
        if (n == newtiledefinitions(92, 20)) {
            return n + 1;
        }
        if (n == newtiledefinitions(92, 21)) {
            return n - 1;
        }
        if (n >= newtiledefinitions(92, 26) && n <= newtiledefinitions(92, 29)) {
            return n + 6;
        }
        if (n == newtiledefinitions(11, 16)) {
            return newtiledefinitions(11, 45);
        }
        if (n == newtiledefinitions(11, 17)) {
            return newtiledefinitions(11, 43);
        }
        if (n == newtiledefinitions(11, 18)) {
            return newtiledefinitions(11, 41);
        }
        if (n == newtiledefinitions(11, 19)) {
            return newtiledefinitions(11, 47);
        }
        if (n == newtiledefinitions(11, 24)) {
            return newtiledefinitions(11, 26);
        }
        if (n == newtiledefinitions(11, 25)) {
            return newtiledefinitions(11, 27);
        }
        if (n == newtiledefinitions(27, 42)) {
            return n + 1;
        }
        if (n == newtiledefinitions(27, 43)) {
            return n - 1;
        }
        if (n == newtiledefinitions(27, 44)) {
            return n + 3;
        }
        if (n == newtiledefinitions(27, 47)) {
            return n - 2;
        }
        if (n == newtiledefinitions(27, 45)) {
            return n + 1;
        }
        if (n == newtiledefinitions(27, 46)) {
            return n - 2;
        }
        if (n == newtiledefinitions(34, 4)) {
            return n + 1;
        }
        if (n == newtiledefinitions(34, 5)) {
            return n - 1;
        }
        if (n >= newtiledefinitions(14, 0) && n <= newtiledefinitions(14, 7)) {
            return -1;
        }
        if (n >= newtiledefinitions(14, 8) && n <= newtiledefinitions(14, 12)) {
            return n + 72;
        }
        if (n == newtiledefinitions(14, 13)) {
            return n + 71;
        }
        if (n >= newtiledefinitions(14, 16) && n <= newtiledefinitions(14, 17)) {
            return n + 72;
        }
        if (n == newtiledefinitions(14, 18)) {
            return n + 73;
        }
        if (n == newtiledefinitions(14, 19)) {
            return n + 66;
        }
        if (n == newtiledefinitions(14, 20)) {
            return -1;
        }
        if (n == newtiledefinitions(14, 21)) {
            return newtiledefinitions(14, 89);
        }
        if (n == newtiledefinitions(21, 0)) {
            return newtiledefinitions(125, 16);
        }
        if (n == newtiledefinitions(21, 1)) {
            return newtiledefinitions(125, 32);
        }
        if (n == newtiledefinitions(21, 2)) {
            return newtiledefinitions(125, 48);
        }
        if (n == newtiledefinitions(26, 0)) {
            return newtiledefinitions(26, 6);
        }
        if (n == newtiledefinitions(26, 6)) {
            return newtiledefinitions(26, 0);
        }
        if (n == newtiledefinitions(26, 1)) {
            return newtiledefinitions(26, 7);
        }
        if (n == newtiledefinitions(26, 7)) {
            return newtiledefinitions(26, 1);
        }
        if (n == newtiledefinitions(26, 8)) {
            return newtiledefinitions(26, 14);
        }
        if (n == newtiledefinitions(26, 14)) {
            return newtiledefinitions(26, 8);
        }
        if (n == newtiledefinitions(26, 9)) {
            return newtiledefinitions(26, 15);
        }
        if (n == newtiledefinitions(26, 15)) {
            return newtiledefinitions(26, 9);
        }
        if (n == newtiledefinitions(26, 16)) {
            return newtiledefinitions(26, 22);
        }
        if (n == newtiledefinitions(26, 22)) {
            return newtiledefinitions(26, 16);
        }
        if (n == newtiledefinitions(26, 17)) {
            return newtiledefinitions(26, 23);
        }
        if (n == newtiledefinitions(26, 23)) {
            return newtiledefinitions(26, 17);
        }
        if (n >= newtiledefinitions(148, 0) && n <= newtiledefinitions(148, 16)) {
            return newtiledefinitions(160, n - newtiledefinitions(148, 0));
        }
        if ((n >= newtiledefinitions(42, 44) && n <= newtiledefinitions(42, 47)) || (n >= newtiledefinitions(42, 52) && n <= newtiledefinitions(42, 55))) {
            return -1;
        }
        if (n == newtiledefinitions(43, 24)) {
            return n + 4;
        }
        if (n == newtiledefinitions(43, 26)) {
            return n + 2;
        }
        if (n == newtiledefinitions(43, 33)) {
            return n - 4;
        }
        if (n == newtiledefinitions(44, 0)) {
            return newtiledefinitions(44, 1);
        }
        if (n == newtiledefinitions(44, 1)) {
            return newtiledefinitions(44, 0);
        }
        if (n == newtiledefinitions(44, 2)) {
            return newtiledefinitions(44, 7);
        }
        if (n == newtiledefinitions(44, 3)) {
            return newtiledefinitions(44, 6);
        }
        if (n == newtiledefinitions(44, 4)) {
            return newtiledefinitions(44, 5);
        }
        if (n == newtiledefinitions(44, 5)) {
            return newtiledefinitions(44, 4);
        }
        if (n == newtiledefinitions(44, 6)) {
            return newtiledefinitions(44, 3);
        }
        if (n == newtiledefinitions(44, 7)) {
            return newtiledefinitions(44, 2);
        }
        if (n == newtiledefinitions(44, 16)) {
            return newtiledefinitions(44, 45);
        }
        if (n == newtiledefinitions(44, 17)) {
            return newtiledefinitions(44, 44);
        }
        if (n == newtiledefinitions(44, 18)) {
            return newtiledefinitions(44, 46);
        }
        if (n >= newtiledefinitions(44, 19) && n <= newtiledefinitions(44, 22)) {
            return n + 33;
        }
        if (n == newtiledefinitions(44, 23)) {
            return newtiledefinitions(44, 47);
        }
        if (n == newtiledefinitions(46, 8)) {
            return newtiledefinitions(46, 5);
        }
        if (n == newtiledefinitions(46, 14)) {
            return newtiledefinitions(46, 10);
        }
        if (n == newtiledefinitions(46, 15)) {
            return newtiledefinitions(46, 11);
        }
        if (n == newtiledefinitions(46, 22)) {
            return newtiledefinitions(46, 14);
        }
        if (n == newtiledefinitions(46, 23)) {
            return newtiledefinitions(46, 15);
        }
        if (n == newtiledefinitions(46, 54)) {
            return newtiledefinitions(46, 55);
        }
        if (n == newtiledefinitions(46, 55)) {
            return newtiledefinitions(46, 54);
        }
        if (n == newtiledefinitions(106, 32)) {
            return newtiledefinitions(106, 34);
        }
        if (n == newtiledefinitions(106, 34)) {
            return newtiledefinitions(106, 32);
        }
        if (n == newtiledefinitions(47, 0) || n == newtiledefinitions(47, 4)) {
            return n + 1;
        }
        if (n == newtiledefinitions(47, 1) || n == newtiledefinitions(47, 5)) {
            return n - 1;
        }
        if (n >= newtiledefinitions(47, 8) && n <= newtiledefinitions(47, 13)) {
            return n + 8;
        }
        if (n >= newtiledefinitions(47, 22) && n <= newtiledefinitions(47, 23)) {
            return n - 12;
        }
        if (n >= newtiledefinitions(47, 44) && n <= newtiledefinitions(47, 47)) {
            return n + 4;
        }
        if (n >= newtiledefinitions(47, 48) && n <= newtiledefinitions(47, 51)) {
            return n - 4;
        }
        if (n == newtiledefinitions(48, 56)) {
            return newtiledefinitions(48, 58);
        }
        if (n == newtiledefinitions(48, 58)) {
            return newtiledefinitions(48, 56);
        }
        if (n == newtiledefinitions(52, 57)) {
            return newtiledefinitions(52, 58);
        }
        if (n == newtiledefinitions(52, 58)) {
            return newtiledefinitions(52, 59);
        }
        if (n == newtiledefinitions(52, 45)) {
            return newtiledefinitions(52, 44);
        }
        if (n == newtiledefinitions(52, 46)) {
            return newtiledefinitions(52, 45);
        }
        if (n == newtiledefinitions(54, 13)) {
            return newtiledefinitions(54, 18);
        }
        if (n == newtiledefinitions(54, 15)) {
            return newtiledefinitions(54, 19);
        }
        if (n == newtiledefinitions(54, 21)) {
            return newtiledefinitions(54, 16);
        }
        if (n == newtiledefinitions(54, 22)) {
            return newtiledefinitions(54, 13);
        }
        if (n == newtiledefinitions(54, 23)) {
            return newtiledefinitions(54, 17);
        }
        if (n >= newtiledefinitions(67, 0) && n <= newtiledefinitions(67, 16)) {
            return namedMap.get(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, 64 + Rand.Next(16))).ID;
        }
        if (n == newtiledefinitions(68, 6)) {
            return -1;
        }
        if (n >= newtiledefinitions(68, 16) && n <= newtiledefinitions(68, 17)) {
            return namedMap.get("d_plants_1_53").ID;
        }
        if (n >= newtiledefinitions(68, 18) && n <= newtiledefinitions(68, 23)) {
            return namedMap.get(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(4) * 16 + Rand.Next(8))).ID;
        }
        if (n >= newtiledefinitions(79, 24) && n <= newtiledefinitions(79, 41)) {
            return newtiledefinitions(81, n - newtiledefinitions(79, 24));
        }
        return n;
    }
    
    public static String Fix2x(final String key) {
        if (IsoChunk.Fix2xMap.isEmpty()) {
            final HashMap<String, String> fix2xMap = IsoChunk.Fix2xMap;
            for (int i = 48; i <= 51; ++i) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i), "");
            }
            fix2xMap.put("fencing_01_14", "");
            fix2xMap.put("fencing_01_15", "");
            fix2xMap.put("fencing_01_22", "");
            fix2xMap.put("fencing_01_23", "");
            fix2xMap.put("fencing_01_30", "");
            fix2xMap.put("fencing_01_31", "");
            fix2xMap.put("fencing_01_38", "");
            fix2xMap.put("fencing_01_39", "");
            fix2xMap.put("fencing_01_46", "");
            fix2xMap.put("fencing_01_47", "");
            fix2xMap.put("fencing_01_62", "");
            fix2xMap.put("fencing_01_63", "");
            fix2xMap.put("fencing_01_70", "");
            fix2xMap.put("fencing_01_71", "");
            fix2xMap.put("fixtures_bathroom_02_2", "fixtures_bathroom_02_22");
            fix2xMap.put("fixtures_bathroom_02_20", "fixtures_bathroom_02_21");
            fix2xMap.put("fixtures_bathroom_02_21", "fixtures_bathroom_02_20");
            for (int j = 26; j <= 29; ++j) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, j), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, j + 6));
            }
            fix2xMap.put("fixtures_counters_01_16", "fixtures_counters_01_45");
            fix2xMap.put("fixtures_counters_01_17", "fixtures_counters_01_43");
            fix2xMap.put("fixtures_counters_01_18", "fixtures_counters_01_41");
            fix2xMap.put("fixtures_counters_01_19", "fixtures_counters_01_47");
            fix2xMap.put("fixtures_counters_01_24", "fixtures_counters_01_26");
            fix2xMap.put("fixtures_counters_01_25", "fixtures_counters_01_27");
            for (int k = 0; k <= 7; ++k) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, k), "");
            }
            for (int l = 8; l <= 12; ++l) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, l), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, l + 72));
            }
            fix2xMap.put("fixtures_railings_01_13", "fixtures_railings_01_84");
            for (int n = 16; n <= 17; ++n) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n + 72));
            }
            fix2xMap.put("fixtures_railings_01_18", "fixtures_railings_01_91");
            fix2xMap.put("fixtures_railings_01_19", "fixtures_railings_01_85");
            fix2xMap.put("fixtures_railings_01_20", "");
            fix2xMap.put("fixtures_railings_01_21", "fixtures_railings_01_89");
            fix2xMap.put("floors_exterior_natural_01_0", "blends_natural_01_16");
            fix2xMap.put("floors_exterior_natural_01_1", "blends_natural_01_32");
            fix2xMap.put("floors_exterior_natural_01_2", "blends_natural_01_48");
            fix2xMap.put("floors_rugs_01_0", "floors_rugs_01_6");
            fix2xMap.put("floors_rugs_01_6", "floors_rugs_01_0");
            fix2xMap.put("floors_rugs_01_1", "floors_rugs_01_7");
            fix2xMap.put("floors_rugs_01_7", "floors_rugs_01_1");
            fix2xMap.put("floors_rugs_01_8", "floors_rugs_01_14");
            fix2xMap.put("floors_rugs_01_14", "floors_rugs_01_8");
            fix2xMap.put("floors_rugs_01_9", "floors_rugs_01_15");
            fix2xMap.put("floors_rugs_01_15", "floors_rugs_01_9");
            fix2xMap.put("floors_rugs_01_16", "floors_rugs_01_22");
            fix2xMap.put("floors_rugs_01_22", "floors_rugs_01_16");
            fix2xMap.put("floors_rugs_01_17", "floors_rugs_01_23");
            fix2xMap.put("floors_rugs_01_23", "floors_rugs_01_17");
            fix2xMap.put("furniture_bedding_01_42", "furniture_bedding_01_43");
            fix2xMap.put("furniture_bedding_01_43", "furniture_bedding_01_42");
            fix2xMap.put("furniture_bedding_01_44", "furniture_bedding_01_47");
            fix2xMap.put("furniture_bedding_01_47", "furniture_bedding_01_45");
            fix2xMap.put("furniture_bedding_01_45", "furniture_bedding_01_46");
            fix2xMap.put("furniture_bedding_01_46", "furniture_bedding_01_44");
            fix2xMap.put("furniture_tables_low_01_4", "furniture_tables_low_01_5");
            fix2xMap.put("furniture_tables_low_01_5", "furniture_tables_low_01_4");
            for (int n2 = 0; n2 <= 5; ++n2) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2));
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2 + 8), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2 + 8));
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2));
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2 + 8), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2 + 8));
            }
            for (int n3 = 44; n3 <= 47; ++n3) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n3), "");
            }
            for (int n4 = 52; n4 <= 55; ++n4) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n4), "");
            }
            fix2xMap.put("location_hospitality_sunstarmotel_02_24", "location_hospitality_sunstarmotel_02_28");
            fix2xMap.put("location_hospitality_sunstarmotel_02_26", "location_hospitality_sunstarmotel_02_28");
            fix2xMap.put("location_hospitality_sunstarmotel_02_33", "location_hospitality_sunstarmotel_02_29");
            fix2xMap.put("location_restaurant_bar_01_0", "location_restaurant_bar_01_1");
            fix2xMap.put("location_restaurant_bar_01_1", "location_restaurant_bar_01_0");
            fix2xMap.put("location_restaurant_bar_01_2", "location_restaurant_bar_01_7");
            fix2xMap.put("location_restaurant_bar_01_3", "location_restaurant_bar_01_6");
            fix2xMap.put("location_restaurant_bar_01_4", "location_restaurant_bar_01_5");
            fix2xMap.put("location_restaurant_bar_01_5", "location_restaurant_bar_01_4");
            fix2xMap.put("location_restaurant_bar_01_6", "location_restaurant_bar_01_3");
            fix2xMap.put("location_restaurant_bar_01_7", "location_restaurant_bar_01_2");
            fix2xMap.put("location_restaurant_bar_01_16", "location_restaurant_bar_01_45");
            fix2xMap.put("location_restaurant_bar_01_17", "location_restaurant_bar_01_44");
            fix2xMap.put("location_restaurant_bar_01_18", "location_restaurant_bar_01_46");
            for (int n5 = 19; n5 <= 22; ++n5) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n5), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n5 + 33));
            }
            fix2xMap.put("location_restaurant_bar_01_23", "location_restaurant_bar_01_47");
            fix2xMap.put("location_restaurant_pie_01_8", "location_restaurant_pie_01_5");
            fix2xMap.put("location_restaurant_pie_01_14", "location_restaurant_pie_01_10");
            fix2xMap.put("location_restaurant_pie_01_15", "location_restaurant_pie_01_11");
            fix2xMap.put("location_restaurant_pie_01_22", "location_restaurant_pie_01_14");
            fix2xMap.put("location_restaurant_pie_01_23", "location_restaurant_pie_01_15");
            fix2xMap.put("location_restaurant_pie_01_54", "location_restaurant_pie_01_55");
            fix2xMap.put("location_restaurant_pie_01_55", "location_restaurant_pie_01_54");
            fix2xMap.put("location_pizzawhirled_01_32", "location_pizzawhirled_01_34");
            fix2xMap.put("location_pizzawhirled_01_34", "location_pizzawhirled_01_32");
            fix2xMap.put("location_restaurant_seahorse_01_0", "location_restaurant_seahorse_01_1");
            fix2xMap.put("location_restaurant_seahorse_01_1", "location_restaurant_seahorse_01_0");
            fix2xMap.put("location_restaurant_seahorse_01_4", "location_restaurant_seahorse_01_5");
            fix2xMap.put("location_restaurant_seahorse_01_5", "location_restaurant_seahorse_01_4");
            for (int n6 = 8; n6 <= 13; ++n6) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n6), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n6 + 8));
            }
            for (int n7 = 22; n7 <= 23; ++n7) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n7), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n7 - 12));
            }
            for (int n8 = 44; n8 <= 47; ++n8) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n8), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n8 + 4));
            }
            for (int n9 = 48; n9 <= 51; ++n9) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n9), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n9 - 4));
            }
            fix2xMap.put("location_restaurant_spiffos_01_56", "location_restaurant_spiffos_01_58");
            fix2xMap.put("location_restaurant_spiffos_01_58", "location_restaurant_spiffos_01_56");
            fix2xMap.put("location_shop_fossoil_01_45", "location_shop_fossoil_01_44");
            fix2xMap.put("location_shop_fossoil_01_46", "location_shop_fossoil_01_45");
            fix2xMap.put("location_shop_fossoil_01_57", "location_shop_fossoil_01_58");
            fix2xMap.put("location_shop_fossoil_01_58", "location_shop_fossoil_01_59");
            fix2xMap.put("location_shop_greenes_01_13", "location_shop_greenes_01_18");
            fix2xMap.put("location_shop_greenes_01_15", "location_shop_greenes_01_19");
            fix2xMap.put("location_shop_greenes_01_21", "location_shop_greenes_01_16");
            fix2xMap.put("location_shop_greenes_01_22", "location_shop_greenes_01_13");
            fix2xMap.put("location_shop_greenes_01_23", "location_shop_greenes_01_17");
            fix2xMap.put("location_shop_greenes_01_67", "location_shop_greenes_01_70");
            fix2xMap.put("location_shop_greenes_01_68", "location_shop_greenes_01_67");
            fix2xMap.put("location_shop_greenes_01_70", "location_shop_greenes_01_71");
            fix2xMap.put("location_shop_greenes_01_75", "location_shop_greenes_01_78");
            fix2xMap.put("location_shop_greenes_01_76", "location_shop_greenes_01_75");
            fix2xMap.put("location_shop_greenes_01_78", "location_shop_greenes_01_79");
            for (int n10 = 0; n10 <= 16; ++n10) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n10), "randBush");
            }
            fix2xMap.put("vegetation_groundcover_01_0", "blends_grassoverlays_01_16");
            fix2xMap.put("vegetation_groundcover_01_1", "blends_grassoverlays_01_8");
            fix2xMap.put("vegetation_groundcover_01_2", "blends_grassoverlays_01_0");
            fix2xMap.put("vegetation_groundcover_01_3", "blends_grassoverlays_01_64");
            fix2xMap.put("vegetation_groundcover_01_4", "blends_grassoverlays_01_56");
            fix2xMap.put("vegetation_groundcover_01_5", "blends_grassoverlays_01_48");
            fix2xMap.put("vegetation_groundcover_01_6", "");
            fix2xMap.put("vegetation_groundcover_01_44", "blends_grassoverlays_01_40");
            fix2xMap.put("vegetation_groundcover_01_45", "blends_grassoverlays_01_32");
            fix2xMap.put("vegetation_groundcover_01_46", "blends_grassoverlays_01_24");
            fix2xMap.put("vegetation_groundcover_01_16", "d_plants_1_53");
            fix2xMap.put("vegetation_groundcover_01_17", "d_plants_1_53");
            for (int n11 = 18; n11 <= 23; ++n11) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n11), "randPlant");
            }
            for (int n12 = 20; n12 <= 23; ++n12) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n12), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n12 + 12));
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n12 + 8), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n12 + 8 + 12));
            }
            for (int n13 = 24; n13 <= 41; ++n13) {
                fix2xMap.put(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n13), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n13));
            }
        }
        final String s = IsoChunk.Fix2xMap.get(key);
        if (s == null) {
            return key;
        }
        if ("randBush".equals(s)) {
            return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, 64 + Rand.Next(16));
        }
        if ("randPlant".equals(s)) {
            return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(4) * 16 + Rand.Next(8));
        }
        return s;
    }
    
    public void addGeneratorPos(final int n, final int n2, final int n3) {
        if (this.generatorsTouchingThisChunk == null) {
            this.generatorsTouchingThisChunk = new ArrayList<IsoGameCharacter.Location>();
        }
        for (int i = 0; i < this.generatorsTouchingThisChunk.size(); ++i) {
            final IsoGameCharacter.Location location = this.generatorsTouchingThisChunk.get(i);
            if (location.x == n && location.y == n2 && location.z == n3) {
                return;
            }
        }
        this.generatorsTouchingThisChunk.add(new IsoGameCharacter.Location(n, n2, n3));
    }
    
    public void removeGeneratorPos(final int n, final int n2, final int n3) {
        if (this.generatorsTouchingThisChunk == null) {
            return;
        }
        for (int i = 0; i < this.generatorsTouchingThisChunk.size(); ++i) {
            final IsoGameCharacter.Location location = this.generatorsTouchingThisChunk.get(i);
            if (location.x == n && location.y == n2 && location.z == n3) {
                this.generatorsTouchingThisChunk.remove(i);
                --i;
            }
        }
    }
    
    public boolean isGeneratorPoweringSquare(final int n, final int n2, final int n3) {
        if (this.generatorsTouchingThisChunk == null) {
            return false;
        }
        for (int i = 0; i < this.generatorsTouchingThisChunk.size(); ++i) {
            final IsoGameCharacter.Location location = this.generatorsTouchingThisChunk.get(i);
            if (IsoGenerator.isPoweringSquare(location.x, location.y, location.z, n, n2, n3)) {
                return true;
            }
        }
        return false;
    }
    
    public void checkForMissingGenerators() {
        if (this.generatorsTouchingThisChunk == null) {
            return;
        }
        for (int i = 0; i < this.generatorsTouchingThisChunk.size(); ++i) {
            final IsoGameCharacter.Location location = this.generatorsTouchingThisChunk.get(i);
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(location.x, location.y, location.z);
            if (gridSquare != null) {
                final IsoGenerator generator = gridSquare.getGenerator();
                if (generator == null || !generator.isActivated()) {
                    this.generatorsTouchingThisChunk.remove(i);
                    --i;
                }
            }
        }
    }
    
    public boolean isNewChunk() {
        return this.addZombies;
    }
    
    public void addSpawnedRoom(final int n) {
        if (!this.m_spawnedRooms.contains(n)) {
            this.m_spawnedRooms.add(n);
        }
    }
    
    public boolean isSpawnedRoom(final int n) {
        return this.m_spawnedRooms.contains(n);
    }
    
    public IsoMetaGrid.Zone getScavengeZone() {
        if (this.m_scavengeZone != null) {
            return this.m_scavengeZone;
        }
        final IsoMetaChunk chunkData = IsoWorld.instance.getMetaGrid().getChunkData(this.wx, this.wy);
        if (chunkData != null && chunkData.numZones() > 0) {
            for (int i = 0; i < chunkData.numZones(); ++i) {
                final IsoMetaGrid.Zone zone = chunkData.getZone(i);
                if ("DeepForest".equals(zone.type) || "Forest".equals(zone.type)) {
                    return this.m_scavengeZone = zone;
                }
                if ("Nav".equals(zone.type) || "Town".equals(zone.type)) {
                    return null;
                }
            }
        }
        final int n = 5;
        if (this.m_treeCount < n) {
            return null;
        }
        int n2 = 0;
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                if (k != 0 || j != 0) {
                    final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(this.wx + k, this.wy + j) : IsoWorld.instance.CurrentCell.getChunk(this.wx + k, this.wy + j);
                    if (isoChunk != null && isoChunk.m_treeCount >= n && ++n2 == 8) {
                        final int n3 = 10;
                        return this.m_scavengeZone = new IsoMetaGrid.Zone("", "Forest", this.wx * n3, this.wy * n3, 0, n3, n3);
                    }
                }
            }
        }
        return null;
    }
    
    public void resetForStore() {
        this.randomID = 0;
        this.revision = 0L;
        this.nextSplatIndex = 0;
        this.FloorBloodSplats.clear();
        this.FloorBloodSplatsFade.clear();
        this.jobType = JobType.None;
        this.maxLevel = -1;
        this.bFixed2x = false;
        this.vehicles.clear();
        this.roomLights.clear();
        this.blam = false;
        this.lotheader = null;
        this.bLoaded = false;
        this.addZombies = false;
        this.physicsCheck = false;
        this.loadedPhysics = false;
        this.wx = 0;
        this.wy = 0;
        this.erosion = null;
        this.lootRespawnHour = -1;
        if (this.generatorsTouchingThisChunk != null) {
            this.generatorsTouchingThisChunk.clear();
        }
        this.m_treeCount = 0;
        this.m_scavengeZone = null;
        this.m_numberOfWaterTiles = 0;
        this.m_spawnedRooms.resetQuick();
        this.m_adjacentChunkLoadedCounter = 0;
        for (int i = 0; i < this.squares.length; ++i) {
            for (int j = 0; j < this.squares[0].length; ++j) {
                this.squares[i][j] = null;
            }
        }
        for (int k = 0; k < 4; ++k) {
            this.lightCheck[k] = true;
            this.bLightingNeverDone[k] = true;
        }
        this.refs.clear();
        this.m_vehicleStorySpawnData = null;
    }
    
    public int getNumberOfWaterTiles() {
        return this.m_numberOfWaterTiles;
    }
    
    public void setRandomVehicleStoryToSpawnLater(final VehicleStorySpawnData vehicleStorySpawnData) {
        this.m_vehicleStorySpawnData = vehicleStorySpawnData;
    }
    
    static {
        IsoChunk.bDoServerRequests = true;
        renderByIndex = new byte[][] { { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0 }, { 1, 0, 0, 1, 0, 0, 1, 0, 0, 0 }, { 1, 0, 0, 1, 0, 1, 0, 0, 1, 0 }, { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 }, { 1, 1, 0, 1, 1, 0, 1, 1, 0, 0 }, { 1, 1, 0, 1, 1, 0, 1, 1, 0, 1 }, { 1, 1, 1, 1, 0, 1, 1, 1, 1, 0 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } };
        IsoChunk.AddVehicles_ForTest_vtype = 0;
        IsoChunk.AddVehicles_ForTest_vskin = 0;
        IsoChunk.AddVehicles_ForTest_vrot = 0;
        BaseVehicleCheckedVehicles = new ArrayList<BaseVehicle>();
        bshapes = new byte[4];
        IsoChunk.chunkGetter = new ChunkGetter();
        loadGridSquare = new ConcurrentLinkedQueue<IsoChunk>();
        IsoChunk.SliceBuffer = ByteBuffer.allocate(65536);
        IsoChunk.SliceBufferLoad = ByteBuffer.allocate(65536);
        WriteLock = new Object();
        tempRoomDefs = new ArrayList<RoomDef>();
        tempBuildings = new ArrayList<IsoBuilding>();
        Locks = new ArrayList<ChunkLock>();
        FreeLocks = new Stack<ChunkLock>();
        sanityCheck = new SanityCheck();
        crcLoad = new CRC32();
        crcSave = new CRC32();
        IsoChunk.prefix = "map_";
        Fix2xMap = new HashMap<String, String>();
    }
    
    public enum JobType
    {
        None, 
        Convert, 
        SoftReset;
        
        private static /* synthetic */ JobType[] $values() {
            return new JobType[] { JobType.None, JobType.Convert, JobType.SoftReset };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private enum PhysicsShapes
    {
        Solid, 
        WallN, 
        WallW, 
        WallS, 
        WallE, 
        Tree, 
        Floor;
        
        private static /* synthetic */ PhysicsShapes[] $values() {
            return new PhysicsShapes[] { PhysicsShapes.Solid, PhysicsShapes.WallN, PhysicsShapes.WallW, PhysicsShapes.WallS, PhysicsShapes.WallE, PhysicsShapes.Tree, PhysicsShapes.Floor };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private static class ChunkGetter implements IsoGridSquare.GetSquare
    {
        IsoChunk chunk;
        
        @Override
        public IsoGridSquare getGridSquare(int n, int n2, final int n3) {
            n -= this.chunk.wx * 10;
            n2 -= this.chunk.wy * 10;
            if (n >= 0 && n < 10 && n2 >= 0 && n2 < 10 && n3 >= 0 && n3 < 8) {
                return this.chunk.getGridSquare(n, n2, n3);
            }
            return null;
        }
    }
    
    private static class ChunkLock
    {
        public int wx;
        public int wy;
        public int count;
        public ReentrantReadWriteLock rw;
        
        public ChunkLock(final int wx, final int wy) {
            this.rw = new ReentrantReadWriteLock(true);
            this.wx = wx;
            this.wy = wy;
        }
        
        public ChunkLock set(final int wx, final int wy) {
            assert this.count == 0;
            this.wx = wx;
            this.wy = wy;
            return this;
        }
        
        public ChunkLock ref() {
            ++this.count;
            return this;
        }
        
        public int deref() {
            assert this.count > 0;
            return --this.count;
        }
        
        public void lockForReading() {
            this.rw.readLock().lock();
        }
        
        public void unlockForReading() {
            this.rw.readLock().unlock();
        }
        
        public void lockForWriting() {
            this.rw.writeLock().lock();
        }
        
        public void unlockForWriting() {
            this.rw.writeLock().unlock();
        }
    }
    
    private static class SanityCheck
    {
        public IsoChunk saveChunk;
        public String saveThread;
        public IsoChunk loadChunk;
        public String loadThread;
        public final ArrayList<String> loadFile;
        public String saveFile;
        
        private SanityCheck() {
            this.loadFile = new ArrayList<String>();
        }
        
        public synchronized void beginSave(final IsoChunk saveChunk) {
            if (this.saveChunk != null) {
                this.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, saveChunk.wx, saveChunk.wy));
            }
            if (this.loadChunk == saveChunk) {
                this.log("trying to save the same IsoChunk being loaded");
            }
            this.saveChunk = saveChunk;
            this.saveThread = Thread.currentThread().getName();
        }
        
        public synchronized void endSave(final IsoChunk isoChunk) {
            this.saveChunk = null;
            this.saveThread = null;
        }
        
        public synchronized void beginLoad(final IsoChunk loadChunk) {
            if (this.loadChunk != null) {
                this.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, loadChunk.wx, loadChunk.wy));
            }
            if (this.saveChunk == loadChunk) {
                this.log("trying to load the same IsoChunk being saved");
            }
            this.loadChunk = loadChunk;
            this.loadThread = Thread.currentThread().getName();
        }
        
        public synchronized void endLoad(final IsoChunk isoChunk) {
            this.loadChunk = null;
            this.loadThread = null;
        }
        
        public synchronized void checkCRC(final long n, final long n2) {
            if (n != n2) {
                this.log(invokedynamic(makeConcatWithConstants:(JJ)Ljava/lang/String;, n, n2));
            }
        }
        
        public synchronized void checkLength(final long n, final long n2) {
            if (n != n2) {
                this.log(invokedynamic(makeConcatWithConstants:(JJ)Ljava/lang/String;, n, n2));
            }
        }
        
        public synchronized void beginLoadFile(final String e) {
            if (e.equals(this.saveFile)) {
                this.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e));
            }
            this.loadFile.add(e);
        }
        
        public synchronized void endLoadFile(final String o) {
            this.loadFile.remove(o);
        }
        
        public synchronized void beginSaveFile(final String s) {
            if (this.loadFile.contains(s)) {
                this.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            }
            this.saveFile = s;
        }
        
        public synchronized void endSaveFile() {
            this.saveFile = null;
        }
        
        public synchronized void log(final String s) {
            final StringBuilder sb = new StringBuilder();
            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Thread.currentThread().getName()));
            if (s != null) {
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            }
            if (this.saveChunk != null && this.saveChunk == this.loadChunk) {
                sb.append("exact same IsoChunk being saved + loaded\n");
            }
            if (this.saveChunk != null) {
                sb.append(invokedynamic(makeConcatWithConstants:(IILjava/lang/String;)Ljava/lang/String;, this.saveChunk.wx, this.saveChunk.wy, this.saveThread));
            }
            else {
                sb.append("save chunk=null\n");
            }
            if (this.loadChunk != null) {
                sb.append(invokedynamic(makeConcatWithConstants:(IILjava/lang/String;)Ljava/lang/String;, this.loadChunk.wx, this.loadChunk.wy, this.loadThread));
            }
            else {
                sb.append("load chunk=null\n");
            }
            throw new RuntimeException(sb.toString());
        }
    }
}
