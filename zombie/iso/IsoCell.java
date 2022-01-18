// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.core.profiling.PerformanceProfileProbeList;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.scripting.objects.VehicleScript;
import zombie.iso.weather.ClimateManager;
import java.io.FileNotFoundException;
import java.io.File;
import zombie.gameStates.GameLoadingState;
import zombie.core.Translator;
import zombie.network.ServerOptions;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import zombie.savefile.ClientPlayerDB;
import zombie.ReanimatedPlayers;
import zombie.savefile.PlayerDB;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import zombie.ZomboidFileSystem;
import java.io.DataOutputStream;
import java.io.IOException;
import zombie.iso.objects.IsoGenerator;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.popman.NetworkZombieSimulator;
import zombie.network.GameClient;
import zombie.iso.objects.IsoTree;
import zombie.core.textures.ColorInfo;
import se.krka.kahlua.integration.annotations.LuaMethod;
import zombie.network.ServerMap;
import zombie.Lua.LuaHookManager;
import zombie.ai.astar.Mover;
import java.util.Arrays;
import zombie.network.GameServer;
import zombie.input.JoypadManager;
import zombie.ui.UIManager;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.JavaFunction;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.sprite.IsoSprite;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.GameWindow;
import java.util.Collection;
import java.util.Iterator;
import zombie.MovingObjectUpdateScheduler;
import zombie.VirtualZombieManager;
import zombie.iso.areas.IsoRoomExit;
import zombie.iso.sprite.shapers.FloorShaper;
import zombie.iso.sprite.shapers.FloorShaperDiamond;
import zombie.iso.sprite.shapers.FloorShaperAttachedSprites;
import zombie.SandboxOptions;
import java.util.List;
import java.util.Collections;
import zombie.core.PerformanceSettings;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.debug.LineDrawer;
import zombie.core.physics.WorldSimulation;
import zombie.core.SpriteRenderer;
import zombie.iso.objects.IsoDeadBody;
import zombie.util.Type;
import zombie.iso.sprite.CorpseFlies;
import zombie.iso.weather.fog.ImprovedFog;
import zombie.Lua.LuaEventManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.GameTime;
import zombie.core.opengl.RenderThread;
import zombie.debug.DebugOptions;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.Core;
import zombie.IndieGL;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.core.utils.OnceEvery;
import zombie.iso.weather.fx.IsoWeatherFX;
import java.awt.Rectangle;
import zombie.characters.IsoSurvivor;
import se.krka.kahlua.vm.KahluaTable;
import zombie.erosion.utils.Noise2D;
import org.joml.Vector2i;
import zombie.core.textures.Texture;
import zombie.vehicles.BaseVehicle;
import java.util.Stack;
import zombie.core.utils.IntGrid;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.inventory.InventoryItem;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.BuildingScore;
import java.util.HashMap;
import zombie.iso.objects.IsoWindow;
import zombie.iso.areas.IsoBuilding;
import java.util.ArrayList;
import zombie.core.opengl.Shader;

public final class IsoCell
{
    public static int MaxHeight;
    private static Shader m_floorRenderShader;
    private static Shader m_wallRenderShader;
    public ArrayList<IsoGridSquare> Trees;
    static final ArrayList<IsoGridSquare> stchoices;
    public final IsoChunkMap[] ChunkMap;
    public final ArrayList<IsoBuilding> BuildingList;
    private final ArrayList<IsoWindow> WindowList;
    private final ArrayList<IsoMovingObject> ObjectList;
    private final ArrayList<IsoPushableObject> PushableObjectList;
    private final HashMap<Integer, BuildingScore> BuildingScores;
    private final ArrayList<IsoRoom> RoomList;
    private final ArrayList<IsoObject> StaticUpdaterObjectList;
    private final ArrayList<IsoZombie> ZombieList;
    private final ArrayList<IsoGameCharacter> RemoteSurvivorList;
    private final ArrayList<IsoMovingObject> removeList;
    private final ArrayList<IsoMovingObject> addList;
    private final ArrayList<IsoObject> ProcessIsoObject;
    private final ArrayList<IsoObject> ProcessIsoObjectRemove;
    private final ArrayList<InventoryItem> ProcessItems;
    private final ArrayList<InventoryItem> ProcessItemsRemove;
    private final ArrayList<IsoWorldInventoryObject> ProcessWorldItems;
    public final ArrayList<IsoWorldInventoryObject> ProcessWorldItemsRemove;
    private final IsoGridSquare[][] gridSquares;
    public static final boolean ENABLE_SQUARE_CACHE = true;
    private int height;
    private int width;
    private int worldX;
    private int worldY;
    public IntGrid DangerScore;
    private boolean safeToAdd;
    private final Stack<IsoLightSource> LamppostPositions;
    public final ArrayList<IsoRoomLight> roomLights;
    private final ArrayList<IsoHeatSource> heatSources;
    public final ArrayList<BaseVehicle> addVehicles;
    public final ArrayList<BaseVehicle> vehicles;
    public static final int ISOANGLEFACTOR = 3;
    private static final int ZOMBIESCANBUDGET = 10;
    private static final float NEARESTZOMBIEDISTSQRMAX = 150.0f;
    private int zombieScanCursor;
    private final IsoZombie[] nearestVisibleZombie;
    private final float[] nearestVisibleZombieDistSqr;
    private static Stack<BuildingScore> buildingscores;
    static ArrayList<IsoGridSquare> GridStack;
    public static final int RTF_SolidFloor = 1;
    public static final int RTF_VegetationCorpses = 2;
    public static final int RTF_MinusFloorCharacters = 4;
    public static final int RTF_ShadedFloor = 8;
    public static final int RTF_Shadows = 16;
    private static final ArrayList<IsoGridSquare> ShadowSquares;
    private static final ArrayList<IsoGridSquare> MinusFloorCharacters;
    private static final ArrayList<IsoGridSquare> SolidFloor;
    private static final ArrayList<IsoGridSquare> ShadedFloor;
    private static final ArrayList<IsoGridSquare> VegetationCorpses;
    public static final PerPlayerRender[] perPlayerRender;
    private final int[] StencilXY;
    private final int[] StencilXY2z;
    public int StencilX1;
    public int StencilY1;
    public int StencilX2;
    public int StencilY2;
    private Texture m_stencilTexture;
    private final DiamondMatrixIterator diamondMatrixIterator;
    private final Vector2i diamondMatrixPos;
    public int DeferredCharacterTick;
    private boolean hasSetupSnowGrid;
    private SnowGridTiles snowGridTiles_Square;
    private SnowGridTiles[] snowGridTiles_Strip;
    private SnowGridTiles[] snowGridTiles_Edge;
    private SnowGridTiles[] snowGridTiles_Cove;
    private SnowGridTiles snowGridTiles_Enclosed;
    private int m_snowFirstNonSquare;
    private Noise2D snowNoise2D;
    private SnowGrid snowGridCur;
    private SnowGrid snowGridPrev;
    private int snowFracTarget;
    private long snowFadeTime;
    private float snowTransitionTime;
    private int raport;
    private static final int SNOWSHORE_NONE = 0;
    private static final int SNOWSHORE_N = 1;
    private static final int SNOWSHORE_E = 2;
    private static final int SNOWSHORE_S = 4;
    private static final int SNOWSHORE_W = 8;
    public boolean recalcFloors;
    static int wx;
    static int wy;
    final KahluaTable[] drag;
    final ArrayList<IsoSurvivor> SurvivorList;
    private static Texture texWhite;
    private static IsoCell instance;
    private int currentLX;
    private int currentLY;
    private int currentLZ;
    int recalcShading;
    int lastMinX;
    int lastMinY;
    private float rainScroll;
    private int[] rainX;
    private int[] rainY;
    private Texture[] rainTextures;
    private long[] rainFileTime;
    private float rainAlphaMax;
    private float[] rainAlpha;
    protected int rainIntensity;
    protected int rainSpeed;
    int lightUpdateCount;
    public boolean bRendering;
    final boolean[] bHideFloors;
    final int[] unhideFloorsCounter;
    boolean bOccludedByOrphanStructureFlag;
    int playerPeekedRoomId;
    final ArrayList<ArrayList<IsoBuilding>> playerOccluderBuildings;
    final IsoBuilding[][] playerOccluderBuildingsArr;
    final int[] playerWindowPeekingRoomId;
    final boolean[] playerHidesOrphanStructures;
    final boolean[] playerCutawaysDirty;
    final Vector2 tempCutawaySqrVector;
    ArrayList<Integer> tempPrevPlayerCutawayRoomIDs;
    ArrayList<Integer> tempPlayerCutawayRoomIDs;
    final IsoGridSquare[] lastPlayerSquare;
    final boolean[] lastPlayerSquareHalf;
    final IsoDirections[] lastPlayerDir;
    final Vector2[] lastPlayerAngle;
    int hidesOrphanStructuresAbove;
    final Rectangle buildingRectTemp;
    final ArrayList<ArrayList<IsoBuilding>> zombieOccluderBuildings;
    final IsoBuilding[][] zombieOccluderBuildingsArr;
    final IsoGridSquare[] lastZombieSquare;
    final boolean[] lastZombieSquareHalf;
    final ArrayList<ArrayList<IsoBuilding>> otherOccluderBuildings;
    final IsoBuilding[][] otherOccluderBuildingsArr;
    final int mustSeeSquaresRadius = 4;
    final int mustSeeSquaresGridSize = 10;
    final ArrayList<IsoGridSquare> gridSquaresTempLeft;
    final ArrayList<IsoGridSquare> gridSquaresTempRight;
    private IsoWeatherFX weatherFX;
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int minZ;
    private int maxZ;
    private OnceEvery dangerUpdate;
    private Thread LightInfoUpdate;
    private final Stack<IsoRoom> SpottedRooms;
    private IsoZombie fakeZombieForHit;
    
    public static int getMaxHeight() {
        return IsoCell.MaxHeight;
    }
    
    public LotHeader getCurrentLotHeader() {
        return this.getChunkForGridSquare((int)IsoCamera.CamCharacter.x, (int)IsoCamera.CamCharacter.y, (int)IsoCamera.CamCharacter.z).lotheader;
    }
    
    public IsoChunkMap getChunkMap(final int n) {
        return this.ChunkMap[n];
    }
    
    public IsoGridSquare getFreeTile(final RoomDef roomDef) {
        IsoCell.stchoices.clear();
        for (int i = 0; i < roomDef.rects.size(); ++i) {
            final RoomDef.RoomRect roomRect = roomDef.rects.get(i);
            for (int j = roomRect.x; j < roomRect.x + roomRect.w; ++j) {
                for (int k = roomRect.y; k < roomRect.y + roomRect.h; ++k) {
                    final IsoGridSquare gridSquare = this.getGridSquare(j, k, roomDef.level);
                    if (gridSquare != null) {
                        gridSquare.setCachedIsFree(false);
                        gridSquare.setCacheIsFree(false);
                        if (gridSquare.isFree(false)) {
                            IsoCell.stchoices.add(gridSquare);
                        }
                    }
                }
            }
        }
        if (IsoCell.stchoices.isEmpty()) {
            return null;
        }
        final IsoGridSquare isoGridSquare = IsoCell.stchoices.get(Rand.Next(IsoCell.stchoices.size()));
        IsoCell.stchoices.clear();
        return isoGridSquare;
    }
    
    public static Stack<BuildingScore> getBuildings() {
        return IsoCell.buildingscores;
    }
    
    public static void setBuildings(final Stack<BuildingScore> buildingscores) {
        IsoCell.buildingscores = buildingscores;
    }
    
    public IsoZombie getNearestVisibleZombie(final int n) {
        return this.nearestVisibleZombie[n];
    }
    
    public IsoChunk getChunkForGridSquare(int n, int n2, final int n3) {
        final int n4 = n;
        final int n5 = n2;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (!this.ChunkMap[i].ignore) {
                n = n4;
                n2 = n5;
                n -= this.ChunkMap[i].getWorldXMinTiles();
                n2 -= this.ChunkMap[i].getWorldYMinTiles();
                if (n >= 0) {
                    if (n2 >= 0) {
                        final int n6 = n;
                        final IsoChunkMap isoChunkMap = this.ChunkMap[i];
                        n = n6 / 10;
                        final int n7 = n2;
                        final IsoChunkMap isoChunkMap2 = this.ChunkMap[i];
                        n2 = n7 / 10;
                        final IsoChunk chunk = this.ChunkMap[i].getChunk(n, n2);
                        if (chunk != null) {
                            return chunk;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public IsoChunk getChunk(final int n, final int n2) {
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoChunkMap isoChunkMap = this.ChunkMap[i];
            if (!isoChunkMap.ignore) {
                final IsoChunk chunk = isoChunkMap.getChunk(n - isoChunkMap.getWorldXMin(), n2 - isoChunkMap.getWorldYMin());
                if (chunk != null) {
                    return chunk;
                }
            }
        }
        return null;
    }
    
    public IsoCell(final int width, final int height) {
        this.Trees = new ArrayList<IsoGridSquare>();
        this.ChunkMap = new IsoChunkMap[4];
        this.BuildingList = new ArrayList<IsoBuilding>();
        this.WindowList = new ArrayList<IsoWindow>();
        this.ObjectList = new ArrayList<IsoMovingObject>();
        this.PushableObjectList = new ArrayList<IsoPushableObject>();
        this.BuildingScores = new HashMap<Integer, BuildingScore>();
        this.RoomList = new ArrayList<IsoRoom>();
        this.StaticUpdaterObjectList = new ArrayList<IsoObject>();
        this.ZombieList = new ArrayList<IsoZombie>();
        this.RemoteSurvivorList = new ArrayList<IsoGameCharacter>();
        this.removeList = new ArrayList<IsoMovingObject>();
        this.addList = new ArrayList<IsoMovingObject>();
        this.ProcessIsoObject = new ArrayList<IsoObject>();
        this.ProcessIsoObjectRemove = new ArrayList<IsoObject>();
        this.ProcessItems = new ArrayList<InventoryItem>();
        this.ProcessItemsRemove = new ArrayList<InventoryItem>();
        this.ProcessWorldItems = new ArrayList<IsoWorldInventoryObject>();
        this.ProcessWorldItemsRemove = new ArrayList<IsoWorldInventoryObject>();
        this.gridSquares = new IsoGridSquare[4][IsoChunkMap.ChunkWidthInTiles * IsoChunkMap.ChunkWidthInTiles * 8];
        this.safeToAdd = true;
        this.LamppostPositions = new Stack<IsoLightSource>();
        this.roomLights = new ArrayList<IsoRoomLight>();
        this.heatSources = new ArrayList<IsoHeatSource>();
        this.addVehicles = new ArrayList<BaseVehicle>();
        this.vehicles = new ArrayList<BaseVehicle>();
        this.zombieScanCursor = 0;
        this.nearestVisibleZombie = new IsoZombie[4];
        this.nearestVisibleZombieDistSqr = new float[4];
        this.StencilXY = new int[] { 0, 0, -1, 0, 0, -1, -1, -1, -2, -1, -1, -2, -2, -2, -3, -2, -2, -3, -3, -3 };
        this.StencilXY2z = new int[] { 0, 0, -1, 0, 0, -1, -1, -1, -2, -1, -1, -2, -2, -2, -3, -2, -2, -3, -3, -3, -4, -3, -3, -4, -4, -4, -5, -4, -4, -5, -5, -5, -6, -5, -5, -6, -6, -6 };
        this.m_stencilTexture = null;
        this.diamondMatrixIterator = new DiamondMatrixIterator(123);
        this.diamondMatrixPos = new Vector2i();
        this.DeferredCharacterTick = 0;
        this.hasSetupSnowGrid = false;
        this.m_snowFirstNonSquare = -1;
        this.snowNoise2D = new Noise2D();
        this.snowFracTarget = 0;
        this.snowFadeTime = 0L;
        this.snowTransitionTime = 5000.0f;
        this.raport = 0;
        this.recalcFloors = false;
        this.drag = new KahluaTable[4];
        this.SurvivorList = new ArrayList<IsoSurvivor>();
        this.currentLX = 0;
        this.currentLY = 0;
        this.currentLZ = 0;
        this.recalcShading = 30;
        this.lastMinX = -1234567;
        this.lastMinY = -1234567;
        this.rainX = new int[4];
        this.rainY = new int[4];
        this.rainTextures = new Texture[5];
        this.rainFileTime = new long[5];
        this.rainAlphaMax = 0.6f;
        this.rainAlpha = new float[4];
        this.rainIntensity = 0;
        this.rainSpeed = 6;
        this.lightUpdateCount = 11;
        this.bRendering = false;
        this.bHideFloors = new boolean[4];
        this.unhideFloorsCounter = new int[4];
        this.bOccludedByOrphanStructureFlag = false;
        this.playerPeekedRoomId = -1;
        this.playerOccluderBuildings = new ArrayList<ArrayList<IsoBuilding>>(4);
        this.playerOccluderBuildingsArr = new IsoBuilding[4][];
        this.playerWindowPeekingRoomId = new int[4];
        this.playerHidesOrphanStructures = new boolean[4];
        this.playerCutawaysDirty = new boolean[4];
        this.tempCutawaySqrVector = new Vector2();
        this.tempPrevPlayerCutawayRoomIDs = new ArrayList<Integer>();
        this.tempPlayerCutawayRoomIDs = new ArrayList<Integer>();
        this.lastPlayerSquare = new IsoGridSquare[4];
        this.lastPlayerSquareHalf = new boolean[4];
        this.lastPlayerDir = new IsoDirections[4];
        this.lastPlayerAngle = new Vector2[4];
        this.hidesOrphanStructuresAbove = IsoCell.MaxHeight;
        this.buildingRectTemp = new Rectangle();
        this.zombieOccluderBuildings = new ArrayList<ArrayList<IsoBuilding>>(4);
        this.zombieOccluderBuildingsArr = new IsoBuilding[4][];
        this.lastZombieSquare = new IsoGridSquare[4];
        this.lastZombieSquareHalf = new boolean[4];
        this.otherOccluderBuildings = new ArrayList<ArrayList<IsoBuilding>>(4);
        this.otherOccluderBuildingsArr = new IsoBuilding[4][];
        this.gridSquaresTempLeft = new ArrayList<IsoGridSquare>(100);
        this.gridSquaresTempRight = new ArrayList<IsoGridSquare>(100);
        this.dangerUpdate = new OnceEvery(0.4f, false);
        this.LightInfoUpdate = null;
        this.SpottedRooms = new Stack<IsoRoom>();
        IsoWorld.instance.CurrentCell = this;
        IsoCell.instance = this;
        this.width = width;
        this.height = height;
        for (int i = 0; i < 4; ++i) {
            this.ChunkMap[i] = new IsoChunkMap(this);
            this.ChunkMap[i].PlayerID = i;
            this.ChunkMap[i].ignore = (i > 0);
            this.playerOccluderBuildings.add(new ArrayList<IsoBuilding>(5));
            this.zombieOccluderBuildings.add(new ArrayList<IsoBuilding>(5));
            this.otherOccluderBuildings.add(new ArrayList<IsoBuilding>(5));
        }
        WorldReuserThread.instance.run();
    }
    
    public short getStencilValue(final int n, final int n2, final int n3) {
        final short[][][] stencilValues = IsoCell.perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;
        int min = 0;
        int max = 0;
        for (int i = 0; i < this.StencilXY.length; i += 2) {
            final int n4 = -n3 * 3;
            final int n5 = n + n4 + this.StencilXY[i];
            final int n6 = n2 + n4 + this.StencilXY[i + 1];
            if (n5 >= this.minX && n5 < this.maxX && n6 >= this.minY && n6 < this.maxY) {
                final short[] array = stencilValues[n5 - this.minX][n6 - this.minY];
                if (array[0] != 0) {
                    if (min == 0) {
                        min = array[0];
                        max = array[1];
                    }
                    else {
                        min = Math.min(array[0], min);
                        max = Math.max(array[1], max);
                    }
                }
            }
        }
        if (min == 0) {
            return 1;
        }
        if (min > 10) {
            return (short)(min - 10);
        }
        return (short)(max + 1);
    }
    
    public void setStencilValue(final int n, final int n2, final int n3, final int n4) {
        final short[][][] stencilValues = IsoCell.perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;
        for (int i = 0; i < this.StencilXY.length; i += 2) {
            final int n5 = -n3 * 3;
            final int n6 = n + n5 + this.StencilXY[i];
            final int n7 = n2 + n5 + this.StencilXY[i + 1];
            if (n6 >= this.minX && n6 < this.maxX && n7 >= this.minY && n7 < this.maxY) {
                final short[] array = stencilValues[n6 - this.minX][n7 - this.minY];
                if (array[0] == 0) {
                    array[0] = (short)n4;
                    array[1] = (short)n4;
                }
                else {
                    array[0] = (short)Math.min(array[0], n4);
                    array[1] = (short)Math.max(array[1], n4);
                }
            }
        }
    }
    
    public short getStencilValue2z(final int n, final int n2, final int n3) {
        final short[][][] stencilValues = IsoCell.perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;
        int min = 0;
        int max = 0;
        final int n4 = -n3 * 3;
        for (int i = 0; i < this.StencilXY2z.length; i += 2) {
            final int n5 = n + n4 + this.StencilXY2z[i];
            final int n6 = n2 + n4 + this.StencilXY2z[i + 1];
            if (n5 >= this.minX && n5 < this.maxX && n6 >= this.minY && n6 < this.maxY) {
                final short[] array = stencilValues[n5 - this.minX][n6 - this.minY];
                if (array[0] != 0) {
                    if (min == 0) {
                        min = array[0];
                        max = array[1];
                    }
                    else {
                        min = Math.min(array[0], min);
                        max = Math.max(array[1], max);
                    }
                }
            }
        }
        if (min == 0) {
            return 1;
        }
        if (min > 10) {
            return (short)(min - 10);
        }
        return (short)(max + 1);
    }
    
    public void setStencilValue2z(final int n, final int n2, final int n3, final int n4) {
        final short[][][] stencilValues = IsoCell.perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;
        final int n5 = -n3 * 3;
        for (int i = 0; i < this.StencilXY2z.length; i += 2) {
            final int n6 = n + n5 + this.StencilXY2z[i];
            final int n7 = n2 + n5 + this.StencilXY2z[i + 1];
            if (n6 >= this.minX && n6 < this.maxX && n7 >= this.minY && n7 < this.maxY) {
                final short[] array = stencilValues[n6 - this.minX][n7 - this.minY];
                if (array[0] == 0) {
                    array[0] = (short)n4;
                    array[1] = (short)n4;
                }
                else {
                    array[0] = (short)Math.min(array[0], n4);
                    array[1] = (short)Math.max(array[1], n4);
                }
            }
        }
    }
    
    public void CalculateVertColoursForTile(final IsoGridSquare isoGridSquare, final int n, final int n2, final int n3, final int n4) {
        final IsoGridSquare isoGridSquare2 = IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 0, 0, 1) ? null : isoGridSquare.nav[IsoDirections.NW.index()];
        final IsoGridSquare isoGridSquare3 = IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 1, 0, 1) ? null : isoGridSquare.nav[IsoDirections.N.index()];
        final IsoGridSquare isoGridSquare4 = IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 2, 0, 1) ? null : isoGridSquare.nav[IsoDirections.NE.index()];
        final IsoGridSquare isoGridSquare5 = IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 2, 1, 1) ? null : isoGridSquare.nav[IsoDirections.E.index()];
        final IsoGridSquare isoGridSquare6 = IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 2, 2, 1) ? null : isoGridSquare.nav[IsoDirections.SE.index()];
        final IsoGridSquare isoGridSquare7 = IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 1, 2, 1) ? null : isoGridSquare.nav[IsoDirections.S.index()];
        final IsoGridSquare isoGridSquare8 = IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 0, 2, 1) ? null : isoGridSquare.nav[IsoDirections.SW.index()];
        final IsoGridSquare isoGridSquare9 = IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 0, 1, 1) ? null : isoGridSquare.nav[IsoDirections.W.index()];
        this.CalculateColor(isoGridSquare2, isoGridSquare3, isoGridSquare9, isoGridSquare, 0, n4);
        this.CalculateColor(isoGridSquare3, isoGridSquare4, isoGridSquare5, isoGridSquare, 1, n4);
        this.CalculateColor(isoGridSquare6, isoGridSquare7, isoGridSquare5, isoGridSquare, 2, n4);
        this.CalculateColor(isoGridSquare8, isoGridSquare7, isoGridSquare9, isoGridSquare, 3, n4);
    }
    
    private Texture getStencilTexture() {
        if (this.m_stencilTexture == null) {
            this.m_stencilTexture = Texture.getSharedTexture("media/mask_circledithernew.png");
        }
        return this.m_stencilTexture;
    }
    
    public void DrawStencilMask() {
        final Texture stencilTexture = this.getStencilTexture();
        if (stencilTexture == null) {
            return;
        }
        IndieGL.glStencilMask(255);
        IndieGL.glClear(1280);
        final int n = IsoCamera.getOffscreenWidth(IsoPlayer.getPlayerIndex()) / 2;
        final int n2 = IsoCamera.getOffscreenHeight(IsoPlayer.getPlayerIndex()) / 2;
        final int n3 = n - stencilTexture.getWidth() / (2 / Core.TileScale);
        final int n4 = n2 - stencilTexture.getHeight() / (2 / Core.TileScale);
        IndieGL.enableStencilTest();
        IndieGL.enableAlphaTest();
        IndieGL.glAlphaFunc(516, 0.1f);
        IndieGL.glStencilFunc(519, 128, 255);
        IndieGL.glStencilOp(7680, 7680, 7681);
        IndieGL.glColorMask(false, false, false, false);
        stencilTexture.renderstrip(n3 - (int)IsoCamera.getRightClickOffX(), n4 - (int)IsoCamera.getRightClickOffY(), stencilTexture.getWidth() * Core.TileScale, stencilTexture.getHeight() * Core.TileScale, 1.0f, 1.0f, 1.0f, 1.0f, null);
        IndieGL.glColorMask(true, true, true, true);
        IndieGL.glStencilFunc(519, 0, 255);
        IndieGL.glStencilOp(7680, 7680, 7680);
        IndieGL.glStencilMask(127);
        IndieGL.glAlphaFunc(519, 0.0f);
        this.StencilX1 = n3 - (int)IsoCamera.getRightClickOffX();
        this.StencilY1 = n4 - (int)IsoCamera.getRightClickOffY();
        this.StencilX2 = this.StencilX1 + stencilTexture.getWidth() * Core.TileScale;
        this.StencilY2 = this.StencilY1 + stencilTexture.getHeight() * Core.TileScale;
    }
    
    public void RenderTiles(final int i) {
        s_performance.isoCellRenderTiles.invokeAndMeasure(this, i, IsoCell::renderTilesInternal);
    }
    
    private void renderTilesInternal(final int n) {
        if (!DebugOptions.instance.Terrain.RenderTiles.Enable.getValue()) {
            return;
        }
        if (IsoCell.m_floorRenderShader == null) {
            RenderThread.invokeOnRenderContext(this::initTileShaders);
        }
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final IsoPlayer isoPlayer2;
        final IsoPlayer isoPlayer = isoPlayer2 = IsoPlayer.players[playerIndex];
        isoPlayer2.dirtyRecalcGridStackTime -= GameTime.getInstance().getMultiplier() / 4.0f;
        final PerPlayerRender perPlayerRender = this.getPerPlayerRenderAt(playerIndex);
        perPlayerRender.setSize(this.maxX - this.minX + 1, this.maxY - this.minY + 1);
        final long currentTimeMillis = System.currentTimeMillis();
        if (this.minX != perPlayerRender.minX || this.minY != perPlayerRender.minY || this.maxX != perPlayerRender.maxX || this.maxY != perPlayerRender.maxY) {
            perPlayerRender.minX = this.minX;
            perPlayerRender.minY = this.minY;
            perPlayerRender.maxX = this.maxX;
            perPlayerRender.maxY = this.maxY;
            isoPlayer.dirtyRecalcGridStack = true;
            WeatherFxMask.forceMaskUpdate(playerIndex);
        }
        s_performance.renderTiles.recalculateAnyGridStacks.start();
        final boolean dirtyRecalcGridStack = isoPlayer.dirtyRecalcGridStack;
        this.recalculateAnyGridStacks(perPlayerRender, n, playerIndex, currentTimeMillis);
        s_performance.renderTiles.recalculateAnyGridStacks.end();
        ++this.DeferredCharacterTick;
        s_performance.renderTiles.flattenAnyFoliage.start();
        this.flattenAnyFoliage(perPlayerRender, playerIndex);
        s_performance.renderTiles.flattenAnyFoliage.end();
        if (this.SetCutawayRoomsForPlayer() || dirtyRecalcGridStack) {
            final IsoGridStack gridStacks = perPlayerRender.GridStacks;
            for (int i = 0; i < n + 1; ++i) {
                IsoCell.GridStack = gridStacks.Squares.get(i);
                for (int j = 0; j < IsoCell.GridStack.size(); ++j) {
                    final IsoGridSquare isoGridSquare = IsoCell.GridStack.get(j);
                    isoGridSquare.setPlayerCutawayFlag(playerIndex, this.IsCutawaySquare(isoGridSquare, currentTimeMillis), currentTimeMillis);
                }
            }
        }
        s_performance.renderTiles.performRenderTiles.start();
        this.performRenderTiles(perPlayerRender, n, playerIndex, currentTimeMillis);
        s_performance.renderTiles.performRenderTiles.end();
        this.playerCutawaysDirty[playerIndex] = false;
        IsoCell.ShadowSquares.clear();
        IsoCell.MinusFloorCharacters.clear();
        IsoCell.ShadedFloor.clear();
        IsoCell.SolidFloor.clear();
        IsoCell.VegetationCorpses.clear();
        s_performance.renderTiles.renderDebugPhysics.start();
        this.renderDebugPhysics(playerIndex);
        s_performance.renderTiles.renderDebugPhysics.end();
        s_performance.renderTiles.renderDebugLighting.start();
        this.renderDebugLighting(perPlayerRender, n);
        s_performance.renderTiles.renderDebugLighting.end();
    }
    
    private void initTileShaders() {
        if (DebugLog.isEnabled(DebugType.Shader)) {
            DebugLog.Shader.debugln("Loading shader: \"floorTile\"");
        }
        IsoCell.m_floorRenderShader = new Shader("floorTile");
        if (DebugLog.isEnabled(DebugType.Shader)) {
            DebugLog.Shader.debugln("Loading shader: \"wallTile\"");
        }
        IsoCell.m_wallRenderShader = new Shader("wallTile");
    }
    
    private PerPlayerRender getPerPlayerRenderAt(final int n) {
        if (IsoCell.perPlayerRender[n] == null) {
            IsoCell.perPlayerRender[n] = new PerPlayerRender();
        }
        return IsoCell.perPlayerRender[n];
    }
    
    private void recalculateAnyGridStacks(final PerPlayerRender perPlayerRender, final int n, final int diamondIterDone, final long n2) {
        final IsoPlayer isoPlayer = IsoPlayer.players[diamondIterDone];
        if (!isoPlayer.dirtyRecalcGridStack) {
            return;
        }
        isoPlayer.dirtyRecalcGridStack = false;
        final IsoGridStack gridStacks = perPlayerRender.GridStacks;
        final boolean[][][] visiOccludedFlags = perPlayerRender.VisiOccludedFlags;
        final boolean[][] visiCulledFlags = perPlayerRender.VisiCulledFlags;
        int n3 = -1;
        int n4 = -1;
        int maxLevel = -1;
        WeatherFxMask.setDiamondIterDone(diamondIterDone);
        for (int i = n; i >= 0; --i) {
            (IsoCell.GridStack = gridStacks.Squares.get(i)).clear();
            if (i < this.maxZ) {
                if (DebugOptions.instance.Terrain.RenderTiles.NewRender.getValue()) {
                    final DiamondMatrixIterator reset = this.diamondMatrixIterator.reset(this.maxX - this.minX);
                    final Vector2i diamondMatrixPos = this.diamondMatrixPos;
                    while (reset.next(diamondMatrixPos)) {
                        if (diamondMatrixPos.y >= this.maxY - this.minY + 1) {
                            continue;
                        }
                        final IsoGridSquare gridSquare = this.ChunkMap[diamondIterDone].getGridSquare(diamondMatrixPos.x + this.minX, diamondMatrixPos.y + this.minY, i);
                        if (i == 0) {
                            visiOccludedFlags[diamondMatrixPos.x][diamondMatrixPos.y][0] = false;
                            visiOccludedFlags[diamondMatrixPos.x][diamondMatrixPos.y][1] = false;
                            visiCulledFlags[diamondMatrixPos.x][diamondMatrixPos.y] = false;
                        }
                        if (gridSquare == null) {
                            WeatherFxMask.addMaskLocation(null, diamondMatrixPos.x + this.minX, diamondMatrixPos.y + this.minY, i);
                        }
                        else {
                            if (gridSquare.getChunk() == null || !gridSquare.IsOnScreen(true)) {
                                continue;
                            }
                            WeatherFxMask.addMaskLocation(gridSquare, diamondMatrixPos.x + this.minX, diamondMatrixPos.y + this.minY, i);
                            gridSquare.setIsDissolved(diamondIterDone, this.IsDissolvedSquare(gridSquare, diamondIterDone), n2);
                            if (gridSquare.getIsDissolved(diamondIterDone, n2)) {
                                continue;
                            }
                            gridSquare.cacheLightInfo();
                            IsoCell.GridStack.add(gridSquare);
                        }
                    }
                }
                else {
                    for (int j = this.minY; j < this.maxY; ++j) {
                        int k = this.minX;
                        IsoGridSquare e = this.ChunkMap[diamondIterDone].getGridSquare(k, j, i);
                        final int index = IsoDirections.E.index();
                        while (k < this.maxX) {
                            if (i == 0) {
                                visiOccludedFlags[k - this.minX][j - this.minY][0] = false;
                                visiOccludedFlags[k - this.minX][j - this.minY][1] = false;
                                visiCulledFlags[k - this.minX][j - this.minY] = false;
                            }
                            if (e != null && e.getY() != j) {
                                e = null;
                            }
                            final int n5 = k;
                            final int n6 = j;
                            final int n7 = n5;
                            final int worldX = this.ChunkMap[diamondIterDone].WorldX;
                            final IsoChunkMap isoChunkMap = this.ChunkMap[diamondIterDone];
                            final int n8 = worldX - IsoChunkMap.ChunkGridWidth / 2;
                            final IsoChunkMap isoChunkMap2 = this.ChunkMap[diamondIterDone];
                            final int n9 = n7 - n8 * 10;
                            final int n10 = n6;
                            final int worldY = this.ChunkMap[diamondIterDone].WorldY;
                            final IsoChunkMap isoChunkMap3 = this.ChunkMap[diamondIterDone];
                            final int n11 = worldY - IsoChunkMap.ChunkGridWidth / 2;
                            final IsoChunkMap isoChunkMap4 = this.ChunkMap[diamondIterDone];
                            final int n12 = n10 - n11 * 10;
                            final int n13 = n9;
                            final IsoChunkMap isoChunkMap5 = this.ChunkMap[diamondIterDone];
                            final int n14 = n13 / 10;
                            final int n15 = n12;
                            final IsoChunkMap isoChunkMap6 = this.ChunkMap[diamondIterDone];
                            final int n16 = n15 / 10;
                            final int n17 = n14;
                            final int n18 = n16;
                            if (n17 != n3 || n18 != n4) {
                                final IsoChunk chunkForGridSquare = this.ChunkMap[diamondIterDone].getChunkForGridSquare(k, j);
                                if (chunkForGridSquare != null) {
                                    maxLevel = chunkForGridSquare.maxLevel;
                                }
                            }
                            n3 = n17;
                            n4 = n18;
                            if (maxLevel < i) {
                                ++k;
                            }
                            else {
                                if (e == null) {
                                    e = this.getGridSquare(k, j, i);
                                    if (e == null) {
                                        e = this.ChunkMap[diamondIterDone].getGridSquare(k, j, i);
                                        if (e == null) {
                                            ++k;
                                            continue;
                                        }
                                    }
                                }
                                if (e.getChunk() != null && e.IsOnScreen(true)) {
                                    WeatherFxMask.addMaskLocation(e, e.x, e.y, i);
                                    e.setIsDissolved(diamondIterDone, this.IsDissolvedSquare(e, diamondIterDone), n2);
                                    if (!e.getIsDissolved(diamondIterDone, n2)) {
                                        e.cacheLightInfo();
                                        IsoCell.GridStack.add(e);
                                    }
                                }
                                e = e.nav[index];
                                ++k;
                            }
                        }
                    }
                }
            }
        }
        this.CullFullyOccludedSquares(gridStacks, visiOccludedFlags, visiCulledFlags);
    }
    
    private void flattenAnyFoliage(final PerPlayerRender perPlayerRender, final int n) {
        final short[][][] stencilValues = perPlayerRender.StencilValues;
        final boolean[][] flattenGrassEtc = perPlayerRender.FlattenGrassEtc;
        for (int i = this.minY; i <= this.maxY; ++i) {
            for (int j = this.minX; j <= this.maxX; ++j) {
                stencilValues[j - this.minX][i - this.minY][0] = 0;
                stencilValues[j - this.minX][i - this.minY][1] = 0;
                flattenGrassEtc[j - this.minX][i - this.minY] = false;
            }
        }
        for (int k = 0; k < this.vehicles.size(); ++k) {
            final BaseVehicle baseVehicle = this.vehicles.get(k);
            if (baseVehicle.getAlpha(n) > 0.0f) {
                for (int l = -2; l < 5; ++l) {
                    for (int n2 = -2; n2 < 5; ++n2) {
                        final int n3 = (int)baseVehicle.x + n2;
                        final int n4 = (int)baseVehicle.y + l;
                        if (n3 >= this.minX && n3 <= this.maxX && n4 >= this.minY && n4 <= this.maxY) {
                            flattenGrassEtc[n3 - this.minX][n4 - this.minY] = true;
                        }
                    }
                }
            }
        }
    }
    
    private void performRenderTiles(final PerPlayerRender perPlayerRender, final int n, final int n2, final long n3) {
        final IsoGridStack gridStacks = perPlayerRender.GridStacks;
        final boolean[][] flattenGrassEtc = perPlayerRender.FlattenGrassEtc;
        Shader floorRenderShader;
        Shader wallRenderShader;
        if (!Core.bDebug || DebugOptions.instance.Terrain.RenderTiles.UseShaders.getValue()) {
            floorRenderShader = IsoCell.m_floorRenderShader;
            wallRenderShader = IsoCell.m_wallRenderShader;
        }
        else {
            floorRenderShader = null;
            wallRenderShader = null;
        }
        for (int i = 0; i < n + 1; ++i) {
            final s_performance.renderTiles.PperformRenderTilesLayer pperformRenderTilesLayer = s_performance.renderTiles.performRenderTilesLayers.start(i);
            IsoCell.GridStack = gridStacks.Squares.get(i);
            IsoCell.ShadowSquares.clear();
            IsoCell.SolidFloor.clear();
            IsoCell.ShadedFloor.clear();
            IsoCell.VegetationCorpses.clear();
            IsoCell.MinusFloorCharacters.clear();
            IndieGL.glClear(256);
            if (i == 0 && DebugOptions.instance.Terrain.RenderTiles.Water.getValue() && DebugOptions.instance.Terrain.RenderTiles.WaterBody.getValue()) {
                pperformRenderTilesLayer.renderIsoWater.start();
                IsoWater.getInstance().render(IsoCell.GridStack, false);
                pperformRenderTilesLayer.renderIsoWater.end();
            }
            pperformRenderTilesLayer.renderFloor.start();
            for (int j = 0; j < IsoCell.GridStack.size(); ++j) {
                final IsoGridSquare e = IsoCell.GridStack.get(j);
                if (e.chunk == null || !e.chunk.bLightingNeverDone[n2]) {
                    e.bFlattenGrassEtc = (i == 0 && flattenGrassEtc[e.x - this.minX][e.y - this.minY]);
                    int renderFloor = e.renderFloor(floorRenderShader);
                    if (!e.getStaticMovingObjects().isEmpty()) {
                        renderFloor = (renderFloor | 0x2 | 0x10);
                        if (e.HasStairs()) {
                            renderFloor |= 0x4;
                        }
                    }
                    if (!e.getWorldObjects().isEmpty()) {
                        renderFloor |= 0x2;
                    }
                    if (!e.getLocalTemporaryObjects().isEmpty()) {
                        renderFloor |= 0x4;
                    }
                    for (int k = 0; k < e.getMovingObjects().size(); ++k) {
                        final IsoMovingObject isoMovingObject = e.getMovingObjects().get(k);
                        int n4 = isoMovingObject.bOnFloor ? 1 : 0;
                        if (n4 != 0 && isoMovingObject instanceof IsoZombie) {
                            n4 = (((IsoZombie)isoMovingObject).isProne() ? 1 : 0);
                            if (!BaseVehicle.RENDER_TO_TEXTURE) {
                                n4 = 0;
                            }
                        }
                        int n5;
                        if (n4 != 0) {
                            n5 = (renderFloor | 0x2);
                        }
                        else {
                            n5 = (renderFloor | 0x4);
                        }
                        renderFloor = (n5 | 0x10);
                    }
                    if (!e.getDeferedCharacters().isEmpty()) {
                        renderFloor |= 0x4;
                    }
                    if (e.hasFlies()) {
                        renderFloor |= 0x4;
                    }
                    if ((renderFloor & 0x1) != 0x0) {
                        IsoCell.SolidFloor.add(e);
                    }
                    if ((renderFloor & 0x8) != 0x0) {
                        IsoCell.ShadedFloor.add(e);
                    }
                    if ((renderFloor & 0x2) != 0x0) {
                        IsoCell.VegetationCorpses.add(e);
                    }
                    if ((renderFloor & 0x4) != 0x0) {
                        IsoCell.MinusFloorCharacters.add(e);
                    }
                    if ((renderFloor & 0x10) != 0x0) {
                        IsoCell.ShadowSquares.add(e);
                    }
                }
            }
            pperformRenderTilesLayer.renderFloor.end();
            pperformRenderTilesLayer.renderPuddles.start();
            IsoPuddles.getInstance().render(IsoCell.SolidFloor, i);
            pperformRenderTilesLayer.renderPuddles.end();
            if (i == 0 && DebugOptions.instance.Terrain.RenderTiles.Water.getValue() && DebugOptions.instance.Terrain.RenderTiles.WaterShore.getValue()) {
                pperformRenderTilesLayer.renderShore.start();
                IsoWater.getInstance().render(null, true);
                pperformRenderTilesLayer.renderShore.end();
            }
            if (!IsoCell.SolidFloor.isEmpty()) {
                pperformRenderTilesLayer.renderSnow.start();
                this.RenderSnow(i);
                pperformRenderTilesLayer.renderSnow.end();
            }
            if (!IsoCell.GridStack.isEmpty()) {
                pperformRenderTilesLayer.renderBlood.start();
                this.ChunkMap[n2].renderBloodForChunks(i);
                pperformRenderTilesLayer.renderBlood.end();
            }
            if (!IsoCell.ShadedFloor.isEmpty()) {
                pperformRenderTilesLayer.renderFloorShading.start();
                this.RenderFloorShading(i);
                pperformRenderTilesLayer.renderFloorShading.end();
            }
            WorldMarkers.instance.renderGridSquareMarkers(perPlayerRender, i, n2);
            if (DebugOptions.instance.Terrain.RenderTiles.Shadows.getValue()) {
                pperformRenderTilesLayer.renderShadows.start();
                this.renderShadows();
                pperformRenderTilesLayer.renderShadows.end();
            }
            if (DebugOptions.instance.Terrain.RenderTiles.Lua.getValue()) {
                pperformRenderTilesLayer.luaOnPostFloorLayerDraw.start();
                LuaEventManager.triggerEvent("OnPostFloorLayerDraw", i);
                pperformRenderTilesLayer.luaOnPostFloorLayerDraw.end();
            }
            IsoMarkers.instance.renderIsoMarkers(perPlayerRender, i, n2);
            if (DebugOptions.instance.Terrain.RenderTiles.VegetationCorpses.getValue()) {
                pperformRenderTilesLayer.vegetationCorpses.start();
                for (int l = 0; l < IsoCell.VegetationCorpses.size(); ++l) {
                    final IsoGridSquare isoGridSquare = IsoCell.VegetationCorpses.get(l);
                    isoGridSquare.renderMinusFloor(this.maxZ, false, true, false, false, false, wallRenderShader);
                    isoGridSquare.renderCharacters(this.maxZ, true, true);
                }
                pperformRenderTilesLayer.vegetationCorpses.end();
            }
            ImprovedFog.startRender(n2, i);
            if (DebugOptions.instance.Terrain.RenderTiles.MinusFloorCharacters.getValue()) {
                pperformRenderTilesLayer.minusFloorCharacters.start();
                for (int index = 0; index < IsoCell.MinusFloorCharacters.size(); ++index) {
                    final IsoGridSquare isoGridSquare2 = IsoCell.MinusFloorCharacters.get(index);
                    final IsoGridSquare isoGridSquare3 = isoGridSquare2.nav[IsoDirections.S.index()];
                    final IsoGridSquare isoGridSquare4 = isoGridSquare2.nav[IsoDirections.E.index()];
                    final boolean b = isoGridSquare3 != null && isoGridSquare3.getPlayerCutawayFlag(n2, n3);
                    final boolean playerCutawayFlag = isoGridSquare2.getPlayerCutawayFlag(n2, n3);
                    final boolean b2 = isoGridSquare4 != null && isoGridSquare4.getPlayerCutawayFlag(n2, n3);
                    this.currentLY = isoGridSquare2.getY() - this.minY;
                    this.currentLZ = i;
                    ImprovedFog.renderRowsBehind(isoGridSquare2);
                    final boolean renderMinusFloor = isoGridSquare2.renderMinusFloor(this.maxZ, false, false, b, playerCutawayFlag, b2, wallRenderShader);
                    isoGridSquare2.renderDeferredCharacters(this.maxZ);
                    isoGridSquare2.renderCharacters(this.maxZ, false, true);
                    if (isoGridSquare2.hasFlies()) {
                        CorpseFlies.render(isoGridSquare2.x, isoGridSquare2.y, isoGridSquare2.z);
                    }
                    if (renderMinusFloor) {
                        isoGridSquare2.renderMinusFloor(this.maxZ, true, false, b, playerCutawayFlag, b2, wallRenderShader);
                    }
                }
                pperformRenderTilesLayer.minusFloorCharacters.end();
            }
            IsoMarkers.instance.renderIsoMarkersDeferred(perPlayerRender, i, n2);
            ImprovedFog.endRender();
            pperformRenderTilesLayer.end();
        }
    }
    
    private void renderShadows() {
        final boolean optionCorpseShadows = Core.getInstance().getOptionCorpseShadows();
        for (int i = 0; i < IsoCell.ShadowSquares.size(); ++i) {
            final IsoGridSquare isoGridSquare = IsoCell.ShadowSquares.get(i);
            for (int j = 0; j < isoGridSquare.getMovingObjects().size(); ++j) {
                final IsoMovingObject isoMovingObject = isoGridSquare.getMovingObjects().get(j);
                final IsoGameCharacter isoGameCharacter = Type.tryCastTo(isoMovingObject, IsoGameCharacter.class);
                if (isoGameCharacter != null) {
                    isoGameCharacter.renderShadow(isoGameCharacter.getX(), isoGameCharacter.getY(), isoGameCharacter.getZ());
                }
                else {
                    final BaseVehicle baseVehicle = Type.tryCastTo(isoMovingObject, BaseVehicle.class);
                    if (baseVehicle != null) {
                        baseVehicle.renderShadow();
                    }
                }
            }
            if (optionCorpseShadows) {
                for (int k = 0; k < isoGridSquare.getStaticMovingObjects().size(); ++k) {
                    final IsoDeadBody isoDeadBody = Type.tryCastTo(isoGridSquare.getStaticMovingObjects().get(k), IsoDeadBody.class);
                    if (isoDeadBody != null) {
                        isoDeadBody.renderShadow();
                    }
                }
            }
        }
    }
    
    private void renderDebugPhysics(final int n) {
        if (Core.bDebug && DebugOptions.instance.PhysicsRender.getValue()) {
            SpriteRenderer.instance.drawGeneric(WorldSimulation.getDrawer(n));
        }
    }
    
    private void renderDebugLighting(final PerPlayerRender perPlayerRender, final int n) {
        if (Core.bDebug && DebugOptions.instance.LightingRender.getValue()) {
            final IsoGridStack gridStacks = perPlayerRender.GridStacks;
            final int n2 = 1;
            for (int i = 0; i < n + 1; ++i) {
                IsoCell.GridStack = gridStacks.Squares.get(i);
                for (int j = 0; j < IsoCell.GridStack.size(); ++j) {
                    final IsoGridSquare isoGridSquare = IsoCell.GridStack.get(j);
                    final float xToScreenExact = IsoUtils.XToScreenExact(isoGridSquare.x + 0.3f, (float)isoGridSquare.y, 0.0f, 0);
                    final float yToScreenExact = IsoUtils.YToScreenExact(isoGridSquare.x + 0.3f, (float)isoGridSquare.y, 0.0f, 0);
                    final float xToScreenExact2 = IsoUtils.XToScreenExact(isoGridSquare.x + 0.6f, (float)isoGridSquare.y, 0.0f, 0);
                    final float yToScreenExact2 = IsoUtils.YToScreenExact(isoGridSquare.x + 0.6f, (float)isoGridSquare.y, 0.0f, 0);
                    final float xToScreenExact3 = IsoUtils.XToScreenExact((float)(isoGridSquare.x + 1), isoGridSquare.y + 0.3f, 0.0f, 0);
                    final float yToScreenExact3 = IsoUtils.YToScreenExact((float)(isoGridSquare.x + 1), isoGridSquare.y + 0.3f, 0.0f, 0);
                    final float xToScreenExact4 = IsoUtils.XToScreenExact((float)(isoGridSquare.x + 1), isoGridSquare.y + 0.6f, 0.0f, 0);
                    final float yToScreenExact4 = IsoUtils.YToScreenExact((float)(isoGridSquare.x + 1), isoGridSquare.y + 0.6f, 0.0f, 0);
                    final float xToScreenExact5 = IsoUtils.XToScreenExact(isoGridSquare.x + 0.6f, (float)(isoGridSquare.y + 1), 0.0f, 0);
                    final float yToScreenExact5 = IsoUtils.YToScreenExact(isoGridSquare.x + 0.6f, (float)(isoGridSquare.y + 1), 0.0f, 0);
                    final float xToScreenExact6 = IsoUtils.XToScreenExact(isoGridSquare.x + 0.3f, (float)(isoGridSquare.y + 1), 0.0f, 0);
                    final float yToScreenExact6 = IsoUtils.YToScreenExact(isoGridSquare.x + 0.3f, (float)(isoGridSquare.y + 1), 0.0f, 0);
                    final float xToScreenExact7 = IsoUtils.XToScreenExact((float)isoGridSquare.x, isoGridSquare.y + 0.6f, 0.0f, 0);
                    final float yToScreenExact7 = IsoUtils.YToScreenExact((float)isoGridSquare.x, isoGridSquare.y + 0.6f, 0.0f, 0);
                    final float xToScreenExact8 = IsoUtils.XToScreenExact((float)isoGridSquare.x, isoGridSquare.y + 0.3f, 0.0f, 0);
                    final float yToScreenExact8 = IsoUtils.YToScreenExact((float)isoGridSquare.x, isoGridSquare.y + 0.3f, 0.0f, 0);
                    if (IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 0, 0, n2)) {
                        LineDrawer.drawLine(xToScreenExact, yToScreenExact, xToScreenExact2, yToScreenExact2, 1.0f, 0.0f, 0.0f, 1.0f, 0);
                    }
                    if (IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 0, 1, n2)) {
                        LineDrawer.drawLine(xToScreenExact2, yToScreenExact2, xToScreenExact3, yToScreenExact3, 1.0f, 0.0f, 0.0f, 1.0f, 0);
                    }
                    if (IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 0, 2, n2)) {
                        LineDrawer.drawLine(xToScreenExact3, yToScreenExact3, xToScreenExact4, yToScreenExact4, 1.0f, 0.0f, 0.0f, 1.0f, 0);
                    }
                    if (IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 1, 2, n2)) {
                        LineDrawer.drawLine(xToScreenExact4, yToScreenExact4, xToScreenExact5, yToScreenExact5, 1.0f, 0.0f, 0.0f, 1.0f, 0);
                    }
                    if (IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 2, 2, n2)) {
                        LineDrawer.drawLine(xToScreenExact5, yToScreenExact5, xToScreenExact6, yToScreenExact6, 1.0f, 0.0f, 0.0f, 1.0f, 0);
                    }
                    if (IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 2, 1, n2)) {
                        LineDrawer.drawLine(xToScreenExact6, yToScreenExact6, xToScreenExact7, yToScreenExact7, 1.0f, 0.0f, 0.0f, 1.0f, 0);
                    }
                    if (IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 2, 0, n2)) {
                        LineDrawer.drawLine(xToScreenExact7, yToScreenExact7, xToScreenExact8, yToScreenExact8, 1.0f, 0.0f, 0.0f, 1.0f, 0);
                    }
                    if (IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 1, 0, n2)) {
                        LineDrawer.drawLine(xToScreenExact8, yToScreenExact8, xToScreenExact, yToScreenExact, 1.0f, 0.0f, 0.0f, 1.0f, 0);
                    }
                }
            }
        }
    }
    
    private void CullFullyOccludedSquares(final IsoGridStack isoGridStack, final boolean[][][] array, final boolean[][] array2) {
        int n = 0;
        for (int i = 1; i < IsoCell.MaxHeight + 1; ++i) {
            n += isoGridStack.Squares.get(i).size();
        }
        if (n < 500) {
            return;
        }
        int n2 = 0;
        for (int j = IsoCell.MaxHeight; j >= 0; --j) {
            IsoCell.GridStack = isoGridStack.Squares.get(j);
            for (int k = IsoCell.GridStack.size() - 1; k >= 0; --k) {
                final IsoGridSquare isoGridSquare = IsoCell.GridStack.get(k);
                final int n3 = isoGridSquare.getX() - j * 3 - this.minX;
                final int n4 = isoGridSquare.getY() - j * 3 - this.minY;
                if (n3 < 0 || n3 >= array2.length) {
                    IsoCell.GridStack.remove(k);
                }
                else if (n4 < 0 || n4 >= array2[0].length) {
                    IsoCell.GridStack.remove(k);
                }
                else {
                    if (j < IsoCell.MaxHeight) {
                        boolean b = !array2[n3][n4];
                        if (b) {
                            if (n3 > 2) {
                                if (n4 > 2) {
                                    b = (!array[n3 - 3][n4 - 3][0] || !array[n3 - 3][n4 - 3][1] || !array[n3 - 3][n4 - 2][0] || !array[n3 - 2][n4 - 3][1] || !array[n3 - 2][n4 - 2][0] || !array[n3 - 2][n4 - 2][1] || !array[n3 - 2][n4 - 1][0] || !array[n3 - 1][n4 - 2][0] || !array[n3 - 1][n4 - 1][1] || !array[n3 - 1][n4 - 1][0] || !array[n3 - 1][n4][0] || !array[n3][n4 - 1][1] || !array[n3][n4][0] || !array[n3][n4][1]);
                                }
                                else if (n4 > 1) {
                                    b = (!array[n3 - 3][n4 - 2][0] || !array[n3 - 2][n4 - 2][0] || !array[n3 - 2][n4 - 2][1] || !array[n3 - 2][n4 - 1][0] || !array[n3 - 1][n4 - 2][0] || !array[n3 - 1][n4 - 1][1] || !array[n3 - 1][n4 - 1][0] || !array[n3 - 1][n4][0] || !array[n3][n4 - 1][1] || !array[n3][n4][0] || !array[n3][n4][1]);
                                }
                                else if (n4 > 0) {
                                    b = (!array[n3 - 2][n4 - 1][0] || !array[n3 - 1][n4 - 1][1] || !array[n3 - 1][n4 - 1][0] || !array[n3 - 1][n4][0] || !array[n3][n4 - 1][1] || !array[n3][n4][0] || !array[n3][n4][1]);
                                }
                                else {
                                    b = (!array[n3 - 1][n4][0] || !array[n3][n4][0] || !array[n3][n4][1]);
                                }
                            }
                            else if (n3 > 1) {
                                if (n4 > 2) {
                                    b = (!array[n3 - 2][n4 - 3][1] || !array[n3 - 2][n4 - 2][0] || !array[n3 - 2][n4 - 2][1] || !array[n3 - 2][n4 - 1][0] || !array[n3 - 1][n4 - 2][0] || !array[n3 - 1][n4 - 1][1] || !array[n3 - 1][n4 - 1][0] || !array[n3 - 1][n4][0] || !array[n3][n4 - 1][1] || !array[n3][n4][0] || !array[n3][n4][1]);
                                }
                                else if (n4 > 1) {
                                    b = (!array[n3 - 2][n4 - 2][0] || !array[n3 - 2][n4 - 2][1] || !array[n3 - 2][n4 - 1][0] || !array[n3 - 1][n4 - 2][0] || !array[n3 - 1][n4 - 1][1] || !array[n3 - 1][n4 - 1][0] || !array[n3 - 1][n4][0] || !array[n3][n4 - 1][1] || !array[n3][n4][0] || !array[n3][n4][1]);
                                }
                                else if (n4 > 0) {
                                    b = (!array[n3 - 2][n4 - 1][0] || !array[n3 - 1][n4 - 1][1] || !array[n3 - 1][n4 - 1][0] || !array[n3 - 1][n4][0] || !array[n3][n4 - 1][1] || !array[n3][n4][0] || !array[n3][n4][1]);
                                }
                                else {
                                    b = (!array[n3 - 1][n4][0] || !array[n3][n4][0] || !array[n3][n4][1]);
                                }
                            }
                            else if (n3 > 0) {
                                if (n4 > 2) {
                                    b = (!array[n3 - 1][n4 - 2][0] || !array[n3 - 1][n4 - 1][1] || !array[n3 - 1][n4 - 1][0] || !array[n3 - 1][n4][0] || !array[n3][n4 - 1][1] || !array[n3][n4][0] || !array[n3][n4][1]);
                                }
                                else if (n4 > 1) {
                                    b = (!array[n3 - 1][n4 - 2][0] || !array[n3 - 1][n4 - 1][1] || !array[n3 - 1][n4 - 1][0] || !array[n3 - 1][n4][0] || !array[n3][n4 - 1][1] || !array[n3][n4][0] || !array[n3][n4][1]);
                                }
                                else if (n4 > 0) {
                                    b = (!array[n3 - 1][n4 - 1][1] || !array[n3 - 1][n4 - 1][0] || !array[n3 - 1][n4][0] || !array[n3][n4 - 1][1] || !array[n3][n4][0] || !array[n3][n4][1]);
                                }
                                else {
                                    b = (!array[n3 - 1][n4][0] || !array[n3][n4][0] || !array[n3][n4][1]);
                                }
                            }
                            else if (n4 > 2) {
                                b = (!array[n3][n4 - 1][1] || !array[n3][n4][0] || !array[n3][n4][1]);
                            }
                            else if (n4 > 1) {
                                b = (!array[n3][n4 - 1][1] || !array[n3][n4][0] || !array[n3][n4][1]);
                            }
                            else if (n4 > 0) {
                                b = (!array[n3][n4 - 1][1] || !array[n3][n4][0] || !array[n3][n4][1]);
                            }
                            else {
                                b = (!array[n3][n4][0] || !array[n3][n4][1]);
                            }
                        }
                        if (!b) {
                            IsoCell.GridStack.remove(k);
                            array2[n3][n4] = true;
                            continue;
                        }
                    }
                    ++n2;
                    final boolean b2 = IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 0, 1, 1) && isoGridSquare.getProperties().Is(IsoFlagType.cutW);
                    final boolean b3 = IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 1, 0, 1) && isoGridSquare.getProperties().Is(IsoFlagType.cutN);
                    int n5 = 0;
                    if (b2 || b3) {
                        n5 = (((isoGridSquare.x > IsoCamera.frameState.CamCharacterX || isoGridSquare.y > IsoCamera.frameState.CamCharacterY) && isoGridSquare.z >= (int)IsoCamera.frameState.CamCharacterZ) ? 1 : 0);
                        if (n5 != 0) {
                            final int n6 = (int)(isoGridSquare.CachedScreenX - IsoCamera.frameState.OffX);
                            final int n7 = (int)(isoGridSquare.CachedScreenY - IsoCamera.frameState.OffY);
                            if (n6 + 32 * Core.TileScale <= this.StencilX1 || n6 - 32 * Core.TileScale >= this.StencilX2 || n7 + 32 * Core.TileScale <= this.StencilY1 || n7 - 96 * Core.TileScale >= this.StencilY2) {
                                n5 = 0;
                            }
                        }
                    }
                    int n8 = 0;
                    if (b2 && n5 == 0) {
                        ++n8;
                        if (n3 > 0) {
                            array[n3 - 1][n4][0] = true;
                            if (n4 > 0) {
                                array[n3 - 1][n4 - 1][1] = true;
                            }
                        }
                        if (n3 > 1 && n4 > 0) {
                            array[n3 - 2][n4 - 1][0] = true;
                            if (n4 > 1) {
                                array[n3 - 2][n4 - 2][1] = true;
                            }
                        }
                        if (n3 > 2 && n4 > 1) {
                            array[n3 - 3][n4 - 2][0] = true;
                            if (n4 > 2) {
                                array[n3 - 3][n4 - 3][1] = true;
                            }
                        }
                    }
                    if (b3 && n5 == 0) {
                        ++n8;
                        if (n4 > 0) {
                            array[n3][n4 - 1][1] = true;
                            if (n3 > 0) {
                                array[n3 - 1][n4 - 1][0] = true;
                            }
                        }
                        if (n4 > 1 && n3 > 0) {
                            array[n3 - 1][n4 - 2][1] = true;
                            if (n3 > 1) {
                                array[n3 - 2][n4 - 2][0] = true;
                            }
                        }
                        if (n4 > 2 && n3 > 1) {
                            array[n3 - 2][n4 - 3][1] = true;
                            if (n3 > 2) {
                                array[n3 - 3][n4 - 3][0] = true;
                            }
                        }
                    }
                    if (IsoGridSquare.getMatrixBit(isoGridSquare.visionMatrix, 1, 1, 0)) {
                        ++n8;
                        array[n3][n4][0] = true;
                        array[n3][n4][1] = true;
                    }
                    if (n8 == 3) {
                        array2[n3][n4] = true;
                    }
                }
            }
        }
    }
    
    public void RenderFloorShading(final int n) {
        if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.LightingOld.getValue() || DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.Lighting.getValue()) {
            return;
        }
        if (n >= this.maxZ || PerformanceSettings.LightingFrameSkip >= 3) {
            return;
        }
        if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
            return;
        }
        if (IsoCell.texWhite == null) {
            IsoCell.texWhite = Texture.getWhite();
        }
        final Texture texWhite = IsoCell.texWhite;
        if (texWhite == null) {
            return;
        }
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final int n2 = (int)IsoCamera.frameState.OffX;
        final int n3 = (int)IsoCamera.frameState.OffY;
        for (int i = 0; i < IsoCell.ShadedFloor.size(); ++i) {
            final IsoGridSquare isoGridSquare = IsoCell.ShadedFloor.get(i);
            if (isoGridSquare.getProperties().Is(IsoFlagType.solidfloor)) {
                float n4 = 0.0f;
                float n5 = 0.0f;
                final float n6 = 0.0f;
                if (isoGridSquare.getProperties().Is(IsoFlagType.FloorHeightOneThird)) {
                    n5 = (n4 = -1.0f);
                }
                else if (isoGridSquare.getProperties().Is(IsoFlagType.FloorHeightTwoThirds)) {
                    n5 = (n4 = -2.0f);
                }
                final float xToScreen = IsoUtils.XToScreen(isoGridSquare.getX() + n4, isoGridSquare.getY() + n5, n + n6, 0);
                final float yToScreen = IsoUtils.YToScreen(isoGridSquare.getX() + n4, isoGridSquare.getY() + n5, n + n6, 0);
                final float n7 = xToScreen - n2;
                final float n8 = yToScreen - n3;
                int vertLight = isoGridSquare.getVertLight(0, playerIndex);
                int vertLight2 = isoGridSquare.getVertLight(1, playerIndex);
                int vertLight3 = isoGridSquare.getVertLight(2, playerIndex);
                int vertLight4 = isoGridSquare.getVertLight(3, playerIndex);
                if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.LightingDebug.getValue()) {
                    vertLight = -65536;
                    vertLight2 = -65536;
                    vertLight3 = -16776961;
                    vertLight4 = -16776961;
                }
                texWhite.renderdiamond(n7 - 32 * Core.TileScale, n8 + 16 * Core.TileScale, (float)(64 * Core.TileScale), (float)(32 * Core.TileScale), vertLight4, vertLight, vertLight2, vertLight3);
            }
        }
    }
    
    public boolean IsPlayerWindowPeeking(final int n) {
        return this.playerWindowPeekingRoomId[n] != -1;
    }
    
    public boolean CanBuildingSquareOccludePlayer(final IsoGridSquare isoGridSquare, final int index) {
        final ArrayList<IsoBuilding> list = this.playerOccluderBuildings.get(index);
        for (int i = 0; i < list.size(); ++i) {
            final IsoBuilding isoBuilding = list.get(i);
            final int x = isoBuilding.getDef().getX();
            final int y = isoBuilding.getDef().getY();
            this.buildingRectTemp.setBounds(x - 1, y - 1, isoBuilding.getDef().getX2() - x + 2, isoBuilding.getDef().getY2() - y + 2);
            if (this.buildingRectTemp.contains(isoGridSquare.getX(), isoGridSquare.getY())) {
                return true;
            }
        }
        return false;
    }
    
    public int GetEffectivePlayerRoomId() {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        int n = this.playerWindowPeekingRoomId[playerIndex];
        if (IsoPlayer.players[playerIndex] != null && IsoPlayer.players[playerIndex].isClimbing()) {
            n = -1;
        }
        if (n != -1) {
            return n;
        }
        final IsoGridSquare current = IsoPlayer.players[playerIndex].current;
        if (current != null) {
            return current.getRoomID();
        }
        return -1;
    }
    
    private boolean SetCutawayRoomsForPlayer() {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final IsoPlayer isoPlayer = IsoPlayer.players[playerIndex];
        final ArrayList<Integer> tempPrevPlayerCutawayRoomIDs = this.tempPrevPlayerCutawayRoomIDs;
        this.tempPrevPlayerCutawayRoomIDs = this.tempPlayerCutawayRoomIDs;
        (this.tempPlayerCutawayRoomIDs = tempPrevPlayerCutawayRoomIDs).clear();
        final IsoGridSquare square = isoPlayer.getSquare();
        if (square == null) {
            return false;
        }
        square.getBuilding();
        final int roomID = square.getRoomID();
        boolean b = false;
        if (roomID == -1) {
            if (this.playerWindowPeekingRoomId[playerIndex] != -1) {
                this.tempPlayerCutawayRoomIDs.add(this.playerWindowPeekingRoomId[playerIndex]);
            }
            else {
                b = this.playerCutawaysDirty[playerIndex];
            }
        }
        else {
            final int n = (int)(isoPlayer.getX() - 1.5f);
            final int n2 = (int)(isoPlayer.getY() - 1.5f);
            final int n3 = (int)(isoPlayer.getX() + 1.5f);
            final int n4 = (int)(isoPlayer.getY() + 1.5f);
            for (int i = n; i <= n3; ++i) {
                for (int j = n2; j <= n4; ++j) {
                    final IsoGridSquare gridSquare = this.getGridSquare(i, j, square.getZ());
                    if (gridSquare != null) {
                        final int roomID2 = gridSquare.getRoomID();
                        if (gridSquare.getCanSee(playerIndex) && roomID2 != -1 && !this.tempPlayerCutawayRoomIDs.contains(roomID2)) {
                            this.tempCutawaySqrVector.set(gridSquare.getX() + 0.5f - isoPlayer.getX(), gridSquare.getY() + 0.5f - isoPlayer.getY());
                            if (square == gridSquare || isoPlayer.getForwardDirection().dot(this.tempCutawaySqrVector) > 0.0f) {
                                this.tempPlayerCutawayRoomIDs.add(roomID2);
                            }
                        }
                    }
                }
            }
            Collections.sort(this.tempPlayerCutawayRoomIDs);
        }
        return b || !this.tempPlayerCutawayRoomIDs.equals(this.tempPrevPlayerCutawayRoomIDs);
    }
    
    private boolean IsCutawaySquare(final IsoGridSquare isoGridSquare, final long n) {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final IsoPlayer isoPlayer = IsoPlayer.players[playerIndex];
        if (isoPlayer.current == null) {
            return false;
        }
        if (isoGridSquare == null) {
            return false;
        }
        final IsoGridSquare current = isoPlayer.current;
        if (current.getZ() != isoGridSquare.getZ()) {
            return false;
        }
        if (this.tempPlayerCutawayRoomIDs.isEmpty()) {
            final IsoGridSquare isoGridSquare2 = isoGridSquare.nav[IsoDirections.N.index()];
            final IsoGridSquare isoGridSquare3 = isoGridSquare.nav[IsoDirections.W.index()];
            if (this.IsCollapsibleBuildingSquare(isoGridSquare)) {
                return true;
            }
            if (isoGridSquare2 != null && isoGridSquare3 != null && (isoGridSquare2.getThumpableWallOrHoppable(false) != null || isoGridSquare3.getThumpableWallOrHoppable(true) != null || isoGridSquare.getThumpableWallOrHoppable(true) != null || isoGridSquare.getThumpableWallOrHoppable(false) != null)) {
                return this.DoesSquareHaveValidCutawayPlayerWalls(current, isoGridSquare, playerIndex, n);
            }
        }
        else {
            final IsoGridSquare isoGridSquare4 = isoGridSquare.nav[IsoDirections.N.index()];
            final IsoGridSquare isoGridSquare5 = isoGridSquare.nav[IsoDirections.E.index()];
            final IsoGridSquare isoGridSquare6 = isoGridSquare.nav[IsoDirections.S.index()];
            final IsoGridSquare isoGridSquare7 = isoGridSquare.nav[IsoDirections.W.index()];
            final IsoGridSquare isoGridSquare8 = current.nav[IsoDirections.N.index()];
            final IsoGridSquare isoGridSquare9 = current.nav[IsoDirections.E.index()];
            final IsoGridSquare isoGridSquare10 = current.nav[IsoDirections.S.index()];
            final IsoGridSquare isoGridSquare11 = current.nav[IsoDirections.W.index()];
            boolean b = false;
            boolean b2 = false;
            for (int i = 0; i < 8; ++i) {
                if (isoGridSquare.nav[i] != null && isoGridSquare.nav[i].getRoomID() != isoGridSquare.getRoomID()) {
                    b = true;
                    break;
                }
            }
            if (!this.tempPlayerCutawayRoomIDs.contains(isoGridSquare.getRoomID())) {
                b2 = true;
            }
            if (b || b2 || isoGridSquare.getWall() != null) {
                IsoGridSquare isoGridSquare12 = isoGridSquare;
                for (int j = 0; j < 3; ++j) {
                    isoGridSquare12 = isoGridSquare12.nav[IsoDirections.NW.index()];
                    if (isoGridSquare12 == null) {
                        break;
                    }
                    if (isoGridSquare12.getRoomID() != -1 && this.tempPlayerCutawayRoomIDs.contains(isoGridSquare12.getRoomID())) {
                        if (b || b2) {
                            return true;
                        }
                        if (isoGridSquare.getWall() != null && isoGridSquare12.isCouldSee(playerIndex)) {
                            return true;
                        }
                    }
                }
            }
            if (isoGridSquare4 != null && isoGridSquare7 != null && (isoGridSquare4.getThumpableWallOrHoppable(false) != null || isoGridSquare7.getThumpableWallOrHoppable(true) != null || isoGridSquare.getThumpableWallOrHoppable(true) != null || isoGridSquare.getThumpableWallOrHoppable(false) != null)) {
                return this.DoesSquareHaveValidCutawayPlayerWalls(current, isoGridSquare, playerIndex, n);
            }
            if (current.getRoomID() == -1 && ((isoGridSquare8 != null && isoGridSquare8.getRoomID() != -1) || (isoGridSquare9 != null && isoGridSquare9.getRoomID() != -1) || (isoGridSquare10 != null && isoGridSquare10.getRoomID() != -1) || (isoGridSquare11 != null && isoGridSquare11.getRoomID() != -1))) {
                final int n2 = current.x - isoGridSquare.x;
                final int n3 = current.y - isoGridSquare.y;
                if (n2 < 0 && n3 < 0) {
                    if (n2 >= -3) {
                        if (n3 >= -3) {
                            return true;
                        }
                        if (isoGridSquare4 != null && isoGridSquare6 != null && isoGridSquare.getWall(false) != null && isoGridSquare4.getWall(false) != null && isoGridSquare6.getWall(false) != null && isoGridSquare6.getPlayerCutawayFlag(playerIndex, n)) {
                            return true;
                        }
                    }
                    else if (isoGridSquare5 != null && isoGridSquare7 != null) {
                        if (isoGridSquare.getWall(true) != null && isoGridSquare7.getWall(true) != null && isoGridSquare5.getWall(true) != null && isoGridSquare5.getPlayerCutawayFlag(playerIndex, n)) {
                            return true;
                        }
                        if (isoGridSquare.getWall(true) != null && isoGridSquare7.getWall(true) != null && isoGridSquare5.getWall(true) != null && isoGridSquare5.getPlayerCutawayFlag(playerIndex, n)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private boolean DoesSquareHaveValidCutawayPlayerWalls(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2, final int n, final long n2) {
        final IsoGridSquare isoGridSquare3 = isoGridSquare2.nav[IsoDirections.N.index()];
        final IsoGridSquare isoGridSquare4 = isoGridSquare2.nav[IsoDirections.E.index()];
        final IsoGridSquare isoGridSquare5 = isoGridSquare2.nav[IsoDirections.S.index()];
        final IsoGridSquare isoGridSquare6 = isoGridSquare2.nav[IsoDirections.W.index()];
        final IsoObject thumpableWallOrHoppable = isoGridSquare2.getThumpableWallOrHoppable(true);
        final IsoObject thumpableWallOrHoppable2 = isoGridSquare2.getThumpableWallOrHoppable(false);
        IsoObject thumpableWallOrHoppable3 = null;
        IsoObject thumpableWallOrHoppable4 = null;
        if (isoGridSquare3 != null) {
            thumpableWallOrHoppable4 = isoGridSquare3.getThumpableWallOrHoppable(false);
        }
        if (isoGridSquare6 != null) {
            thumpableWallOrHoppable3 = isoGridSquare6.getThumpableWallOrHoppable(true);
        }
        if (thumpableWallOrHoppable2 != null || thumpableWallOrHoppable != null || thumpableWallOrHoppable4 != null || thumpableWallOrHoppable3 != null) {
            IsoGridSquare isoGridSquare7 = isoGridSquare2.nav[IsoDirections.NW.index()];
            for (int i = 0; i < 2; ++i) {
                if (isoGridSquare7 == null) {
                    break;
                }
                if (isoGridSquare7.getRoomID() != isoGridSquare.getRoomID()) {
                    break;
                }
                final IsoGridSquare isoGridSquare8 = isoGridSquare7.nav[IsoDirections.S.index()];
                final IsoGridSquare isoGridSquare9 = isoGridSquare7.nav[IsoDirections.E.index()];
                if (isoGridSquare8 != null && isoGridSquare8.getBuilding() != null) {
                    break;
                }
                if (isoGridSquare9 != null && isoGridSquare9.getBuilding() != null) {
                    break;
                }
                if (isoGridSquare7.isCanSee(n) && isoGridSquare7.isCouldSee(n) && isoGridSquare7.DistTo(isoGridSquare) <= 6 - (i + 1)) {
                    return true;
                }
                if (isoGridSquare7.getBuilding() == null) {
                    isoGridSquare7 = isoGridSquare7.nav[IsoDirections.NW.index()];
                }
            }
        }
        final int n3 = isoGridSquare.x - isoGridSquare2.x;
        final int n4 = isoGridSquare.y - isoGridSquare2.y;
        if ((thumpableWallOrHoppable != null && thumpableWallOrHoppable.sprite.name.contains("fencing")) || (thumpableWallOrHoppable2 != null && thumpableWallOrHoppable2.sprite.name.contains("fencing"))) {
            if (thumpableWallOrHoppable != null && thumpableWallOrHoppable3 != null && n4 >= -6 && n4 < 0) {
                return true;
            }
            if (thumpableWallOrHoppable2 != null && thumpableWallOrHoppable4 != null && n3 >= -6 && n3 < 0) {
                return true;
            }
        }
        else if (isoGridSquare2.DistTo(isoGridSquare) <= 6.0f && (isoGridSquare2.getWall(true) == null || isoGridSquare2.getWall(true) == thumpableWallOrHoppable) && (isoGridSquare2.getWall(false) == null || isoGridSquare2.getWall(false) == thumpableWallOrHoppable2)) {
            if (isoGridSquare5 != null && isoGridSquare3 != null && n4 != 0) {
                if (n4 > 0 && thumpableWallOrHoppable2 != null && isoGridSquare5.getThumpableWallOrHoppable(false) != null && isoGridSquare3.getWall(false) != null && isoGridSquare5.getPlayerCutawayFlag(n, n2)) {
                    return true;
                }
                if (n4 < 0 && thumpableWallOrHoppable2 != null && isoGridSquare3.getThumpableWallOrHoppable(false) != null && isoGridSquare3.getPlayerCutawayFlag(n, n2)) {
                    return true;
                }
            }
            if (isoGridSquare4 != null && isoGridSquare6 != null && n3 != 0) {
                if (n3 > 0 && thumpableWallOrHoppable != null && isoGridSquare4.getThumpableWallOrHoppable(true) != null && isoGridSquare6.getWall(true) != null && isoGridSquare4.getPlayerCutawayFlag(n, n2)) {
                    return true;
                }
                if (n3 < 0 && thumpableWallOrHoppable != null && isoGridSquare6.getThumpableWallOrHoppable(true) != null && isoGridSquare6.getPlayerCutawayFlag(n, n2)) {
                    return true;
                }
            }
        }
        if (isoGridSquare2 == isoGridSquare) {
            if (thumpableWallOrHoppable != null && isoGridSquare3.isCanSee(n) && isoGridSquare3.isCouldSee(n)) {
                return true;
            }
            if (thumpableWallOrHoppable2 != null && isoGridSquare6.isCanSee(n) && isoGridSquare6.isCouldSee(n)) {
                return true;
            }
        }
        return (isoGridSquare3 != null && isoGridSquare6 != null && n3 != 0 && n4 != 0 && thumpableWallOrHoppable4 != null && thumpableWallOrHoppable3 != null && isoGridSquare3.getPlayerCutawayFlag(n, n2) && isoGridSquare6.getPlayerCutawayFlag(n, n2)) || (n3 < 0 && n3 >= -6 && n4 < 0 && n4 >= -6 && ((thumpableWallOrHoppable2 != null && isoGridSquare2.getWall(true) == null) || (thumpableWallOrHoppable != null && isoGridSquare2.getWall(false) == null)));
    }
    
    private boolean IsCollapsibleBuildingSquare(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare.getProperties().Is(IsoFlagType.forceRender)) {
            return false;
        }
        for (int i = 0; i < 4; ++i) {
            for (int n = 500, j = 0; j < n; ++j) {
                if (this.playerOccluderBuildingsArr[i] == null) {
                    break;
                }
                final IsoBuilding isoBuilding = this.playerOccluderBuildingsArr[i][j];
                if (isoBuilding == null) {
                    break;
                }
                final BuildingDef def = isoBuilding.getDef();
                final int x = def.getX();
                final int y = def.getY();
                this.buildingRectTemp.setBounds(x - 1, y - 1, def.getX2() - x + 2, def.getY2() - y + 2);
                if (this.buildingRectTemp.contains(isoGridSquare.getX(), isoGridSquare.getY())) {
                    return true;
                }
            }
        }
        final int playerIndex = IsoCamera.frameState.playerIndex;
        if (IsoPlayer.players[playerIndex].getVehicle() != null) {
            return false;
        }
        for (int k = 0; k < 500; ++k) {
            if (this.zombieOccluderBuildingsArr[playerIndex] == null) {
                break;
            }
            final IsoBuilding isoBuilding2 = this.zombieOccluderBuildingsArr[playerIndex][k];
            if (isoBuilding2 == null) {
                break;
            }
            final BuildingDef def2 = isoBuilding2.getDef();
            final int x2 = def2.getX();
            final int y2 = def2.getY();
            this.buildingRectTemp.setBounds(x2 - 1, y2 - 1, def2.getX2() - x2 + 2, def2.getY2() - y2 + 2);
            if (this.buildingRectTemp.contains(isoGridSquare.getX(), isoGridSquare.getY())) {
                return true;
            }
        }
        for (int l = 0; l < 500; ++l) {
            if (this.otherOccluderBuildingsArr[playerIndex] == null) {
                break;
            }
            final IsoBuilding isoBuilding3 = this.otherOccluderBuildingsArr[playerIndex][l];
            if (isoBuilding3 == null) {
                break;
            }
            final BuildingDef def3 = isoBuilding3.getDef();
            final int x3 = def3.getX();
            final int y3 = def3.getY();
            this.buildingRectTemp.setBounds(x3 - 1, y3 - 1, def3.getX2() - x3 + 2, def3.getY2() - y3 + 2);
            if (this.buildingRectTemp.contains(isoGridSquare.getX(), isoGridSquare.getY())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean IsDissolvedSquare(final IsoGridSquare isoGridSquare, final int n) {
        final IsoPlayer isoPlayer = IsoPlayer.players[n];
        if (isoPlayer.current == null) {
            return false;
        }
        if (isoPlayer.current.getZ() >= isoGridSquare.getZ()) {
            return false;
        }
        if (!PerformanceSettings.NewRoofHiding) {
            return this.bHideFloors[n] && isoGridSquare.getZ() >= this.maxZ;
        }
        if (isoGridSquare.getZ() > this.hidesOrphanStructuresAbove) {
            IsoBuilding isoBuilding = isoGridSquare.getBuilding();
            if (isoBuilding == null) {
                isoBuilding = isoGridSquare.roofHideBuilding;
            }
            for (int n2 = isoGridSquare.getZ() - 1; n2 >= 0 && isoBuilding == null; --n2) {
                final IsoGridSquare gridSquare = this.getGridSquare(isoGridSquare.x, isoGridSquare.y, n2);
                if (gridSquare != null) {
                    isoBuilding = gridSquare.getBuilding();
                    if (isoBuilding == null) {
                        isoBuilding = gridSquare.roofHideBuilding;
                    }
                }
            }
            if (isoBuilding == null) {
                if (isoGridSquare.isSolidFloor()) {
                    return true;
                }
                final IsoGridSquare isoGridSquare2 = isoGridSquare.nav[IsoDirections.N.index()];
                if (isoGridSquare2 != null && isoGridSquare2.getBuilding() == null) {
                    if (isoGridSquare2.getPlayerBuiltFloor() != null) {
                        return true;
                    }
                    if (isoGridSquare2.HasStairsBelow()) {
                        return true;
                    }
                }
                final IsoGridSquare isoGridSquare3 = isoGridSquare.nav[IsoDirections.W.index()];
                if (isoGridSquare3 != null && isoGridSquare3.getBuilding() == null) {
                    if (isoGridSquare3.getPlayerBuiltFloor() != null) {
                        return true;
                    }
                    if (isoGridSquare3.HasStairsBelow()) {
                        return true;
                    }
                }
                if (isoGridSquare.Is(IsoFlagType.WallSE)) {
                    final IsoGridSquare isoGridSquare4 = isoGridSquare.nav[IsoDirections.NW.index()];
                    if (isoGridSquare4 != null && isoGridSquare4.getBuilding() == null) {
                        if (isoGridSquare4.getPlayerBuiltFloor() != null) {
                            return true;
                        }
                        if (isoGridSquare4.HasStairsBelow()) {
                            return true;
                        }
                    }
                }
            }
        }
        return this.IsCollapsibleBuildingSquare(isoGridSquare);
    }
    
    private int GetBuildingHeightAt(final IsoBuilding isoBuilding, final int n, final int n2, final int n3) {
        for (int i = IsoCell.MaxHeight; i > n3; --i) {
            final IsoGridSquare gridSquare = this.getGridSquare(n, n2, i);
            if (gridSquare != null && gridSquare.getBuilding() == isoBuilding) {
                return i;
            }
        }
        return n3;
    }
    
    private void updateSnow(final int n) {
        if (this.snowGridCur == null) {
            this.snowGridCur = new SnowGrid(n);
            this.snowGridPrev = new SnowGrid(0);
            return;
        }
        if (n != this.snowGridCur.frac) {
            this.snowGridPrev.init(this.snowGridCur.frac);
            this.snowGridCur.init(n);
            this.snowFadeTime = System.currentTimeMillis();
            DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.snowGridPrev.frac, this.snowGridCur.frac));
        }
    }
    
    public void setSnowTarget(int snowFracTarget) {
        if (!SandboxOptions.instance.EnableSnowOnGround.getValue()) {
            snowFracTarget = 0;
        }
        this.snowFracTarget = snowFracTarget;
    }
    
    public boolean gridSquareIsSnow(final int n, final int n2, final int n3) {
        final IsoGridSquare gridSquare = this.getGridSquare(n, n2, n3);
        return gridSquare != null && gridSquare.getProperties().Is(IsoFlagType.solidfloor) && !gridSquare.getProperties().Is(IsoFlagType.water) && gridSquare.getProperties().Is(IsoFlagType.exterior) && gridSquare.room == null && !gridSquare.isInARoom() && this.snowGridCur.check(gridSquare.getX() % this.snowGridCur.w, gridSquare.getY() % this.snowGridCur.h);
    }
    
    private void RenderSnow(final int n) {
        if (!DebugOptions.instance.Weather.Snow.getValue()) {
            return;
        }
        this.updateSnow(this.snowFracTarget);
        final SnowGrid snowGridCur = this.snowGridCur;
        if (snowGridCur == null) {
            return;
        }
        final SnowGrid snowGridPrev = this.snowGridPrev;
        if (snowGridCur.frac <= 0 && snowGridPrev.frac <= 0) {
            return;
        }
        float n2 = 1.0f;
        float n3 = 0.0f;
        final long n4 = System.currentTimeMillis() - this.snowFadeTime;
        if (n4 < this.snowTransitionTime) {
            n2 = n4 / this.snowTransitionTime;
            n3 = 1.0f - n2;
        }
        Shader floorRenderShader = null;
        if (DebugOptions.instance.Terrain.RenderTiles.UseShaders.getValue()) {
            floorRenderShader = IsoCell.m_floorRenderShader;
        }
        FloorShaperAttachedSprites.instance.setShore(false);
        FloorShaperDiamond.instance.setShore(false);
        IndieGL.StartShader(floorRenderShader, IsoCamera.frameState.playerIndex);
        final int n5 = (int)IsoCamera.frameState.OffX;
        final int n6 = (int)IsoCamera.frameState.OffY;
        for (int i = 0; i < IsoCell.SolidFloor.size(); ++i) {
            final IsoGridSquare isoGridSquare = IsoCell.SolidFloor.get(i);
            if (isoGridSquare.room == null) {
                if (isoGridSquare.getProperties().Is(IsoFlagType.exterior)) {
                    if (isoGridSquare.getProperties().Is(IsoFlagType.solidfloor)) {
                        int shoreInt;
                        if (isoGridSquare.getProperties().Is(IsoFlagType.water)) {
                            shoreInt = getShoreInt(isoGridSquare);
                            if (shoreInt == 0) {
                                continue;
                            }
                        }
                        else {
                            shoreInt = 0;
                        }
                        final int n7 = isoGridSquare.getX() % snowGridCur.w;
                        final int n8 = isoGridSquare.getY() % snowGridCur.h;
                        final float xToScreen = IsoUtils.XToScreen((float)isoGridSquare.getX(), (float)isoGridSquare.getY(), (float)n, 0);
                        final float yToScreen = IsoUtils.YToScreen((float)isoGridSquare.getX(), (float)isoGridSquare.getY(), (float)n, 0);
                        final float n9 = xToScreen - n5;
                        final float n10 = yToScreen - n6;
                        final float n11 = (float)(32 * Core.TileScale);
                        final float n12 = (float)(96 * Core.TileScale);
                        final float n13 = n9 - n11;
                        final float n14 = n10 - n12;
                        final int playerIndex = IsoCamera.frameState.playerIndex;
                        int vertLight = isoGridSquare.getVertLight(0, playerIndex);
                        int vertLight2 = isoGridSquare.getVertLight(1, playerIndex);
                        int vertLight3 = isoGridSquare.getVertLight(2, playerIndex);
                        int vertLight4 = isoGridSquare.getVertLight(3, playerIndex);
                        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.LightingDebug.getValue()) {
                            vertLight = -65536;
                            vertLight2 = -65536;
                            vertLight3 = -16776961;
                            vertLight4 = -16776961;
                        }
                        FloorShaperAttachedSprites.instance.setVertColors(vertLight, vertLight2, vertLight3, vertLight4);
                        FloorShaperDiamond.instance.setVertColors(vertLight, vertLight2, vertLight3, vertLight4);
                        for (int j = 0; j < 2; ++j) {
                            if (n3 > n2) {
                                this.renderSnowTileGeneral(snowGridCur, n2, isoGridSquare, shoreInt, n7, n8, (int)n13, (int)n14, j);
                                this.renderSnowTileGeneral(snowGridPrev, n3, isoGridSquare, shoreInt, n7, n8, (int)n13, (int)n14, j);
                            }
                            else {
                                this.renderSnowTileGeneral(snowGridPrev, n3, isoGridSquare, shoreInt, n7, n8, (int)n13, (int)n14, j);
                                this.renderSnowTileGeneral(snowGridCur, n2, isoGridSquare, shoreInt, n7, n8, (int)n13, (int)n14, j);
                            }
                        }
                    }
                }
            }
        }
        IndieGL.StartShader(null);
    }
    
    private void renderSnowTileGeneral(final SnowGrid snowGrid, final float n, final IsoGridSquare isoGridSquare, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        if (n <= 0.0f) {
            return;
        }
        final Texture texture = snowGrid.grid[n3][n4][n7];
        if (texture == null) {
            return;
        }
        if (n7 == 0) {
            this.renderSnowTile(snowGrid, n3, n4, n7, isoGridSquare, n2, texture, n5, n6, n);
        }
        else if (n2 == 0) {
            this.renderSnowTileBase(texture, n5, n6, n, snowGrid.gridType[n3][n4][n7] < this.m_snowFirstNonSquare);
        }
    }
    
    private void renderSnowTileBase(final Texture texture, final int n, final int n2, final float alpha4, final boolean b) {
        final FloorShaper floorShaper = b ? FloorShaperDiamond.instance : FloorShaperAttachedSprites.instance;
        floorShaper.setAlpha4(alpha4);
        texture.render((float)n, (float)n2, (float)texture.getWidth(), (float)texture.getHeight(), 1.0f, 1.0f, 1.0f, alpha4, floorShaper);
    }
    
    private void renderSnowTile(final SnowGrid snowGrid, final int n, final int n2, final int n3, final IsoGridSquare isoGridSquare, final int n4, Texture texture, final int n5, final int n6, final float n7) {
        if (n4 == 0) {
            this.renderSnowTileBase(texture, n5, n6, n7, snowGrid.gridType[n][n2][n3] < this.m_snowFirstNonSquare);
            return;
        }
        int n8 = 0;
        final boolean check = snowGrid.check(n, n2);
        final boolean b = (n4 & 0x1) == 0x1 && (check || snowGrid.check(n, n2 - 1));
        final boolean b2 = (n4 & 0x2) == 0x2 && (check || snowGrid.check(n + 1, n2));
        final boolean b3 = (n4 & 0x4) == 0x4 && (check || snowGrid.check(n, n2 + 1));
        final boolean b4 = (n4 & 0x8) == 0x8 && (check || snowGrid.check(n - 1, n2));
        if (b) {
            ++n8;
        }
        if (b3) {
            ++n8;
        }
        if (b2) {
            ++n8;
        }
        if (b4) {
            ++n8;
        }
        SnowGridTiles snowGridTiles_Enclosed = null;
        SnowGridTiles snowGridTiles = null;
        boolean b5 = false;
        if (n8 == 0) {
            return;
        }
        if (n8 == 1) {
            if (b) {
                snowGridTiles_Enclosed = this.snowGridTiles_Strip[0];
            }
            else if (b3) {
                snowGridTiles_Enclosed = this.snowGridTiles_Strip[1];
            }
            else if (b2) {
                snowGridTiles_Enclosed = this.snowGridTiles_Strip[3];
            }
            else if (b4) {
                snowGridTiles_Enclosed = this.snowGridTiles_Strip[2];
            }
        }
        else if (n8 == 2) {
            if (b && b3) {
                snowGridTiles_Enclosed = this.snowGridTiles_Strip[0];
                snowGridTiles = this.snowGridTiles_Strip[1];
            }
            else if (b2 && b4) {
                snowGridTiles_Enclosed = this.snowGridTiles_Strip[2];
                snowGridTiles = this.snowGridTiles_Strip[3];
            }
            else if (b) {
                snowGridTiles_Enclosed = this.snowGridTiles_Edge[b4 ? 0 : 3];
            }
            else if (b3) {
                snowGridTiles_Enclosed = this.snowGridTiles_Edge[b4 ? 2 : 1];
            }
            else if (b4) {
                snowGridTiles_Enclosed = this.snowGridTiles_Edge[b ? 0 : 2];
            }
            else if (b2) {
                snowGridTiles_Enclosed = this.snowGridTiles_Edge[b ? 3 : 1];
            }
        }
        else if (n8 == 3) {
            if (!b) {
                snowGridTiles_Enclosed = this.snowGridTiles_Cove[1];
            }
            else if (!b3) {
                snowGridTiles_Enclosed = this.snowGridTiles_Cove[0];
            }
            else if (!b2) {
                snowGridTiles_Enclosed = this.snowGridTiles_Cove[2];
            }
            else if (!b4) {
                snowGridTiles_Enclosed = this.snowGridTiles_Cove[3];
            }
            b5 = true;
        }
        else if (n8 == 4) {
            snowGridTiles_Enclosed = this.snowGridTiles_Enclosed;
            b5 = true;
        }
        if (snowGridTiles_Enclosed != null) {
            final int n9 = (isoGridSquare.getX() + isoGridSquare.getY()) % snowGridTiles_Enclosed.size();
            texture = snowGridTiles_Enclosed.get(n9);
            if (texture != null) {
                this.renderSnowTileBase(texture, n5, n6, n7, b5);
            }
            if (snowGridTiles != null) {
                texture = snowGridTiles.get(n9);
                if (texture != null) {
                    this.renderSnowTileBase(texture, n5, n6, n7, false);
                }
            }
        }
    }
    
    private static int getShoreInt(final IsoGridSquare isoGridSquare) {
        int n = 0;
        if (isSnowShore(isoGridSquare, 0, -1)) {
            n |= 0x1;
        }
        if (isSnowShore(isoGridSquare, 1, 0)) {
            n |= 0x2;
        }
        if (isSnowShore(isoGridSquare, 0, 1)) {
            n |= 0x4;
        }
        if (isSnowShore(isoGridSquare, -1, 0)) {
            n |= 0x8;
        }
        return n;
    }
    
    private static boolean isSnowShore(final IsoGridSquare isoGridSquare, final int n, final int n2) {
        final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare(isoGridSquare.getX() + n, isoGridSquare.getY() + n2, 0);
        return gridSquare != null && !gridSquare.getProperties().Is(IsoFlagType.water);
    }
    
    public IsoBuilding getClosestBuildingExcept(final IsoGameCharacter isoGameCharacter, final IsoRoom isoRoom) {
        IsoBuilding isoBuilding = null;
        float n = 1000000.0f;
        for (int i = 0; i < this.BuildingList.size(); ++i) {
            final IsoBuilding isoBuilding2 = this.BuildingList.get(i);
            for (int j = 0; j < isoBuilding2.Exits.size(); ++j) {
                final float distTo = isoGameCharacter.DistTo(isoBuilding2.Exits.get(j).x, isoBuilding2.Exits.get(j).y);
                if (distTo < n && (isoRoom == null || isoRoom.building != isoBuilding2)) {
                    isoBuilding = isoBuilding2;
                    n = distTo;
                }
            }
        }
        return isoBuilding;
    }
    
    public int getDangerScore(final int n, final int n2) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            return 1000000;
        }
        return this.DangerScore.getValue(n, n2);
    }
    
    private void ObjectDeletionAddition() {
        for (int i = 0; i < this.removeList.size(); ++i) {
            final IsoMovingObject o = this.removeList.get(i);
            if (o instanceof IsoZombie) {
                VirtualZombieManager.instance.RemoveZombie((IsoZombie)o);
            }
            if (!(o instanceof IsoPlayer) || ((IsoPlayer)o).isDead()) {
                MovingObjectUpdateScheduler.instance.removeObject(o);
                this.ObjectList.remove(o);
                if (o.getCurrentSquare() != null) {
                    o.getCurrentSquare().getMovingObjects().remove(o);
                }
                if (o.getLastSquare() != null) {
                    o.getLastSquare().getMovingObjects().remove(o);
                }
            }
        }
        this.removeList.clear();
        for (int j = 0; j < this.addList.size(); ++j) {
            this.ObjectList.add(this.addList.get(j));
        }
        this.addList.clear();
        for (int k = 0; k < this.addVehicles.size(); ++k) {
            final BaseVehicle baseVehicle = this.addVehicles.get(k);
            if (!this.ObjectList.contains(baseVehicle)) {
                this.ObjectList.add(baseVehicle);
            }
            if (!this.vehicles.contains(baseVehicle)) {
                this.vehicles.add(baseVehicle);
            }
        }
        this.addVehicles.clear();
    }
    
    private void ProcessItems(final Iterator<InventoryItem> iterator) {
        for (int size = this.ProcessItems.size(), i = 0; i < size; ++i) {
            final InventoryItem e = this.ProcessItems.get(i);
            e.update();
            if (e.finishupdate()) {
                this.ProcessItemsRemove.add(e);
            }
        }
        for (int size2 = this.ProcessWorldItems.size(), j = 0; j < size2; ++j) {
            final IsoWorldInventoryObject e2 = this.ProcessWorldItems.get(j);
            e2.update();
            if (e2.finishupdate()) {
                this.ProcessWorldItemsRemove.add(e2);
            }
        }
    }
    
    private void ProcessIsoObject() {
        this.ProcessIsoObject.removeAll(this.ProcessIsoObjectRemove);
        this.ProcessIsoObjectRemove.clear();
        for (int size = this.ProcessIsoObject.size(), i = 0; i < size; ++i) {
            final IsoObject isoObject = this.ProcessIsoObject.get(i);
            if (isoObject != null) {
                isoObject.update();
                if (size > this.ProcessIsoObject.size()) {
                    --i;
                    --size;
                }
            }
        }
    }
    
    private void ProcessObjects(final Iterator<IsoMovingObject> iterator) {
        MovingObjectUpdateScheduler.instance.update();
    }
    
    private void ProcessRemoveItems(final Iterator<InventoryItem> iterator) {
        this.ProcessItems.removeAll(this.ProcessItemsRemove);
        this.ProcessWorldItems.removeAll(this.ProcessWorldItemsRemove);
        this.ProcessItemsRemove.clear();
        this.ProcessWorldItemsRemove.clear();
    }
    
    private void ProcessStaticUpdaters() {
        for (int size = this.StaticUpdaterObjectList.size(), i = 0; i < size; ++i) {
            try {
                this.StaticUpdaterObjectList.get(i).update();
            }
            catch (Exception thrown) {
                Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, thrown);
            }
            if (size > this.StaticUpdaterObjectList.size()) {
                --i;
                --size;
            }
        }
    }
    
    public void addToProcessIsoObject(final IsoObject e) {
        if (e == null) {
            return;
        }
        this.ProcessIsoObjectRemove.remove(e);
        if (!this.ProcessIsoObject.contains(e)) {
            this.ProcessIsoObject.add(e);
        }
    }
    
    public void addToProcessIsoObjectRemove(final IsoObject e) {
        if (e == null) {
            return;
        }
        if (!this.ProcessIsoObject.contains(e)) {
            return;
        }
        if (!this.ProcessIsoObjectRemove.contains(e)) {
            this.ProcessIsoObjectRemove.add(e);
        }
    }
    
    public void addToStaticUpdaterObjectList(final IsoObject isoObject) {
        if (isoObject == null) {
            return;
        }
        if (!this.StaticUpdaterObjectList.contains(isoObject)) {
            this.StaticUpdaterObjectList.add(isoObject);
        }
    }
    
    public void addToProcessItems(final InventoryItem e) {
        if (e == null) {
            return;
        }
        this.ProcessItemsRemove.remove(e);
        if (!this.ProcessItems.contains(e)) {
            this.ProcessItems.add(e);
        }
    }
    
    public void addToProcessItems(final ArrayList<InventoryItem> list) {
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            final InventoryItem e = list.get(i);
            if (e != null) {
                this.ProcessItemsRemove.remove(e);
                if (!this.ProcessItems.contains(e)) {
                    this.ProcessItems.add(e);
                }
            }
        }
    }
    
    public void addToProcessItemsRemove(final InventoryItem inventoryItem) {
        if (inventoryItem == null) {
            return;
        }
        if (!this.ProcessItemsRemove.contains(inventoryItem)) {
            this.ProcessItemsRemove.add(inventoryItem);
        }
    }
    
    public void addToProcessItemsRemove(final ArrayList<InventoryItem> list) {
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            final InventoryItem inventoryItem = list.get(i);
            if (inventoryItem != null) {
                if (!this.ProcessItemsRemove.contains(inventoryItem)) {
                    this.ProcessItemsRemove.add(inventoryItem);
                }
            }
        }
    }
    
    public void addToProcessWorldItems(final IsoWorldInventoryObject e) {
        if (e == null) {
            return;
        }
        this.ProcessWorldItemsRemove.remove(e);
        if (!this.ProcessWorldItems.contains(e)) {
            this.ProcessWorldItems.add(e);
        }
    }
    
    public void addToProcessWorldItemsRemove(final IsoWorldInventoryObject isoWorldInventoryObject) {
        if (isoWorldInventoryObject == null) {
            return;
        }
        if (!this.ProcessWorldItemsRemove.contains(isoWorldInventoryObject)) {
            this.ProcessWorldItemsRemove.add(isoWorldInventoryObject);
        }
    }
    
    public IsoSurvivor getNetworkPlayer(final int n) {
        for (int size = this.RemoteSurvivorList.size(), i = 0; i < size; ++i) {
            if (this.RemoteSurvivorList.get(i).getRemoteID() == n) {
                return (IsoSurvivor)this.RemoteSurvivorList.get(i);
            }
        }
        return null;
    }
    
    IsoGridSquare ConnectNewSquare(final IsoGridSquare isoGridSquare, final boolean b, final boolean b2) {
        this.setCacheGridSquare(isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ(), isoGridSquare);
        this.DoGridNav(isoGridSquare, IsoGridSquare.cellGetSquare);
        return isoGridSquare;
    }
    
    public void DoGridNav(final IsoGridSquare isoGridSquare, final IsoGridSquare.GetSquare getSquare) {
        final int x = isoGridSquare.getX();
        final int y = isoGridSquare.getY();
        final int z = isoGridSquare.getZ();
        isoGridSquare.nav[IsoDirections.N.index()] = getSquare.getGridSquare(x, y - 1, z);
        isoGridSquare.nav[IsoDirections.NW.index()] = getSquare.getGridSquare(x - 1, y - 1, z);
        isoGridSquare.nav[IsoDirections.W.index()] = getSquare.getGridSquare(x - 1, y, z);
        isoGridSquare.nav[IsoDirections.SW.index()] = getSquare.getGridSquare(x - 1, y + 1, z);
        isoGridSquare.nav[IsoDirections.S.index()] = getSquare.getGridSquare(x, y + 1, z);
        isoGridSquare.nav[IsoDirections.SE.index()] = getSquare.getGridSquare(x + 1, y + 1, z);
        isoGridSquare.nav[IsoDirections.E.index()] = getSquare.getGridSquare(x + 1, y, z);
        isoGridSquare.nav[IsoDirections.NE.index()] = getSquare.getGridSquare(x + 1, y - 1, z);
        if (isoGridSquare.nav[IsoDirections.N.index()] != null) {
            isoGridSquare.nav[IsoDirections.N.index()].nav[IsoDirections.S.index()] = isoGridSquare;
        }
        if (isoGridSquare.nav[IsoDirections.NW.index()] != null) {
            isoGridSquare.nav[IsoDirections.NW.index()].nav[IsoDirections.SE.index()] = isoGridSquare;
        }
        if (isoGridSquare.nav[IsoDirections.W.index()] != null) {
            isoGridSquare.nav[IsoDirections.W.index()].nav[IsoDirections.E.index()] = isoGridSquare;
        }
        if (isoGridSquare.nav[IsoDirections.SW.index()] != null) {
            isoGridSquare.nav[IsoDirections.SW.index()].nav[IsoDirections.NE.index()] = isoGridSquare;
        }
        if (isoGridSquare.nav[IsoDirections.S.index()] != null) {
            isoGridSquare.nav[IsoDirections.S.index()].nav[IsoDirections.N.index()] = isoGridSquare;
        }
        if (isoGridSquare.nav[IsoDirections.SE.index()] != null) {
            isoGridSquare.nav[IsoDirections.SE.index()].nav[IsoDirections.NW.index()] = isoGridSquare;
        }
        if (isoGridSquare.nav[IsoDirections.E.index()] != null) {
            isoGridSquare.nav[IsoDirections.E.index()].nav[IsoDirections.W.index()] = isoGridSquare;
        }
        if (isoGridSquare.nav[IsoDirections.NE.index()] != null) {
            isoGridSquare.nav[IsoDirections.NE.index()].nav[IsoDirections.SW.index()] = isoGridSquare;
        }
    }
    
    public IsoGridSquare ConnectNewSquare(final IsoGridSquare isoGridSquare, final boolean b) {
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (!this.ChunkMap[i].ignore) {
                this.ChunkMap[i].setGridSquare(isoGridSquare, isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ());
            }
        }
        return this.ConnectNewSquare(isoGridSquare, b, false);
    }
    
    public void PlaceLot(final String s, final int n, final int n2, final int n3, final boolean b) {
    }
    
    public void PlaceLot(final IsoLot isoLot, final int n, final int n2, final int n3, final boolean b) {
        final int min = Math.min(n3 + isoLot.info.levels, n3 + 8);
        for (int i = n; i < n + isoLot.info.width; ++i) {
            for (int j = n2; j < n2 + isoLot.info.height; ++j) {
                for (int k = n3; k < min; ++k) {
                    final int n4 = i - n;
                    final int n5 = j - n2;
                    final int n6 = k - n3;
                    if (i < this.width && j < this.height && i >= 0 && j >= 0) {
                        if (k >= 0) {
                            final int n7 = isoLot.m_offsetInData[n4 + n5 * 10 + n6 * 100];
                            if (n7 != -1) {
                                final int quick = isoLot.m_data.getQuick(n7);
                                if (quick > 0) {
                                    boolean b2 = false;
                                    for (int l = 0; l < quick; ++l) {
                                        final String key = isoLot.info.tilesUsed.get(isoLot.m_data.getQuick(n7 + 1 + l));
                                        final IsoSprite isoSprite = IsoSpriteManager.instance.NamedMap.get(key);
                                        if (isoSprite == null) {
                                            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key));
                                        }
                                        else {
                                            IsoGridSquare isoGridSquare = this.getGridSquare(i, j, k);
                                            if (isoGridSquare == null) {
                                                if (IsoGridSquare.loadGridSquareCache != null) {
                                                    isoGridSquare = IsoGridSquare.getNew(IsoGridSquare.loadGridSquareCache, this, null, i, j, k);
                                                }
                                                else {
                                                    isoGridSquare = IsoGridSquare.getNew(this, null, i, j, k);
                                                }
                                                this.ChunkMap[IsoPlayer.getPlayerIndex()].setGridSquare(isoGridSquare, i, j, k);
                                            }
                                            else {
                                                if (b && l == 0 && isoSprite.getProperties().Is(IsoFlagType.solidfloor) && (!isoSprite.Properties.Is(IsoFlagType.hidewalls) || quick > 1)) {
                                                    b2 = true;
                                                }
                                                if (b2 && l == 0) {
                                                    isoGridSquare.getObjects().clear();
                                                }
                                            }
                                            CellLoader.DoTileObjectCreation(isoSprite, isoSprite.getType(), isoGridSquare, this, i, j, k, key);
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
    
    public void PlaceLot(final IsoLot isoLot, final int n, final int n2, final int n3, final IsoChunk isoChunk, int n4, int n5) {
        n4 *= 10;
        n5 *= 10;
        final IsoMetaGrid metaGrid = IsoWorld.instance.getMetaGrid();
        final int min = Math.min(n3 + isoLot.info.levels, n3 + 8);
        try {
            for (int i = n4 + n; i < n4 + n + 10; ++i) {
                for (int j = n5 + n2; j < n5 + n2 + 10; ++j) {
                    for (int k = n3; k < min; ++k) {
                        final int n6 = i - n4 - n;
                        final int n7 = j - n5 - n2;
                        final int n8 = k - n3;
                        if (i < n4 + 10 && j < n5 + 10 && i >= n4 && j >= n5) {
                            if (k >= 0) {
                                final int n9 = isoLot.m_offsetInData[n6 + n7 * 10 + n8 * 100];
                                if (n9 != -1) {
                                    final int quick = isoLot.m_data.getQuick(n9);
                                    if (quick > 0) {
                                        IsoGridSquare isoGridSquare = isoChunk.getGridSquare(i - n4, j - n5, k);
                                        if (isoGridSquare == null) {
                                            if (IsoGridSquare.loadGridSquareCache != null) {
                                                isoGridSquare = IsoGridSquare.getNew(IsoGridSquare.loadGridSquareCache, this, null, i, j, k);
                                            }
                                            else {
                                                isoGridSquare = IsoGridSquare.getNew(this, null, i, j, k);
                                            }
                                            isoGridSquare.setX(i);
                                            isoGridSquare.setY(j);
                                            isoGridSquare.setZ(k);
                                            isoChunk.setSquare(i - n4, j - n5, k, isoGridSquare);
                                        }
                                        for (int l = -1; l <= 1; ++l) {
                                            for (int n10 = -1; n10 <= 1; ++n10) {
                                                if (l != 0 || n10 != 0) {
                                                    if (l + i - n4 >= 0) {
                                                        if (l + i - n4 < 10) {
                                                            if (n10 + j - n5 >= 0) {
                                                                if (n10 + j - n5 < 10) {
                                                                    if (isoChunk.getGridSquare(i + l - n4, j + n10 - n5, k) == null) {
                                                                        isoChunk.setSquare(i + l - n4, j + n10 - n5, k, IsoGridSquare.getNew(this, null, i + l, j + n10, k));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        final RoomDef room = metaGrid.getRoomAt(i, j, k);
                                        isoGridSquare.setRoomID((room != null) ? room.ID : -1);
                                        isoGridSquare.ResetIsoWorldRegion();
                                        final RoomDef emptyOutside = metaGrid.getEmptyOutsideAt(i, j, k);
                                        if (emptyOutside != null) {
                                            final IsoRoom room2 = isoChunk.getRoom(emptyOutside.ID);
                                            isoGridSquare.roofHideBuilding = ((room2 == null) ? null : room2.building);
                                        }
                                        boolean b = true;
                                        for (int n11 = 0; n11 < quick; ++n11) {
                                            String fix2x = isoLot.info.tilesUsed.get(isoLot.m_data.get(n9 + 1 + n11));
                                            if (!isoLot.info.bFixed2x) {
                                                fix2x = IsoChunk.Fix2x(fix2x);
                                            }
                                            final IsoSprite isoSprite = IsoSpriteManager.instance.NamedMap.get(fix2x);
                                            if (isoSprite == null) {
                                                Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, fix2x));
                                            }
                                            else {
                                                if (n11 == 0 && isoSprite.getProperties().Is(IsoFlagType.solidfloor) && (!isoSprite.Properties.Is(IsoFlagType.hidewalls) || quick > 1)) {
                                                    b = true;
                                                }
                                                if (b && n11 == 0) {
                                                    isoGridSquare.getObjects().clear();
                                                }
                                                CellLoader.DoTileObjectCreation(isoSprite, isoSprite.getType(), isoGridSquare, this, i, j, k, fix2x);
                                            }
                                        }
                                        isoGridSquare.FixStackableObjects();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            DebugLog.log("Failed to load chunk, blocking out area");
            ExceptionLogger.logException(ex);
            for (int n12 = n4 + n; n12 < n4 + n + 10; ++n12) {
                for (int n13 = n5 + n2; n13 < n5 + n2 + 10; ++n13) {
                    for (int n14 = n3; n14 < min; ++n14) {
                        isoChunk.setSquare(n12 - n4 - n, n13 - n5 - n2, n14 - n3, null);
                        this.setCacheGridSquare(n12, n13, n14, null);
                    }
                }
            }
        }
    }
    
    public void setDrag(final KahluaTable kahluaTable, final int n) {
        if (n < 0 || n >= 4) {
            return;
        }
        if (this.drag[n] != null && this.drag[n] != kahluaTable) {
            final Object rawget = this.drag[n].rawget((Object)"deactivate");
            if (rawget instanceof JavaFunction || rawget instanceof LuaClosure) {
                LuaManager.caller.pcallvoid(LuaManager.thread, rawget, (Object)this.drag[n]);
            }
        }
        this.drag[n] = kahluaTable;
    }
    
    public KahluaTable getDrag(final int n) {
        if (n < 0 || n >= 4) {
            return null;
        }
        return this.drag[n];
    }
    
    public boolean DoBuilding(final int n, final boolean b) {
        try {
            s_performance.isoCellDoBuilding.start();
            return this.doBuildingInternal(n, b);
        }
        finally {
            s_performance.isoCellDoBuilding.end();
        }
    }
    
    private boolean doBuildingInternal(final int n, final boolean b) {
        if (UIManager.getPickedTile() != null && this.drag[n] != null && JoypadManager.instance.getFromPlayer(n) == null) {
            if (!IsoWorld.instance.isValidSquare((int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ())) {
                return false;
            }
            IsoGridSquare isoGridSquare = this.getGridSquare((int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ());
            if (!b) {
                if (isoGridSquare == null) {
                    isoGridSquare = this.createNewGridSquare((int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ(), true);
                    if (isoGridSquare == null) {
                        return false;
                    }
                }
                isoGridSquare.EnsureSurroundNotNull();
            }
            LuaEventManager.triggerEvent("OnDoTileBuilding2", this.drag[n], b, (int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ(), isoGridSquare);
        }
        if (this.drag[n] != null && JoypadManager.instance.getFromPlayer(n) != null) {
            LuaEventManager.triggerEvent("OnDoTileBuilding3", this.drag[n], b, (int)IsoPlayer.players[n].getX(), (int)IsoPlayer.players[n].getY(), (int)IsoCamera.CamCharacter.getZ());
        }
        if (b) {
            IndieGL.glBlendFunc(770, 771);
        }
        return false;
    }
    
    public float DistanceFromSupport(final int n, final int n2, final int n3) {
        return 0.0f;
    }
    
    public ArrayList<IsoBuilding> getBuildingList() {
        return this.BuildingList;
    }
    
    public ArrayList<IsoWindow> getWindowList() {
        return this.WindowList;
    }
    
    public void addToWindowList(final IsoWindow isoWindow) {
        if (GameServer.bServer) {
            return;
        }
        if (isoWindow == null) {
            return;
        }
        if (!this.WindowList.contains(isoWindow)) {
            this.WindowList.add(isoWindow);
        }
    }
    
    public void removeFromWindowList(final IsoWindow o) {
        this.WindowList.remove(o);
    }
    
    public ArrayList<IsoMovingObject> getObjectList() {
        return this.ObjectList;
    }
    
    public IsoRoom getRoom(final int n) {
        return this.ChunkMap[IsoPlayer.getPlayerIndex()].getRoom(n);
    }
    
    public ArrayList<IsoPushableObject> getPushableObjectList() {
        return this.PushableObjectList;
    }
    
    public HashMap<Integer, BuildingScore> getBuildingScores() {
        return this.BuildingScores;
    }
    
    public ArrayList<IsoRoom> getRoomList() {
        return this.RoomList;
    }
    
    public ArrayList<IsoObject> getStaticUpdaterObjectList() {
        return this.StaticUpdaterObjectList;
    }
    
    public ArrayList<IsoZombie> getZombieList() {
        return this.ZombieList;
    }
    
    public ArrayList<IsoGameCharacter> getRemoteSurvivorList() {
        return this.RemoteSurvivorList;
    }
    
    public ArrayList<IsoMovingObject> getRemoveList() {
        return this.removeList;
    }
    
    public ArrayList<IsoMovingObject> getAddList() {
        return this.addList;
    }
    
    public void addMovingObject(final IsoMovingObject e) {
        this.addList.add(e);
    }
    
    public ArrayList<InventoryItem> getProcessItems() {
        return this.ProcessItems;
    }
    
    public ArrayList<IsoWorldInventoryObject> getProcessWorldItems() {
        return this.ProcessWorldItems;
    }
    
    public ArrayList<IsoObject> getProcessIsoObjects() {
        return this.ProcessIsoObject;
    }
    
    public ArrayList<InventoryItem> getProcessItemsRemove() {
        return this.ProcessItemsRemove;
    }
    
    public ArrayList<BaseVehicle> getVehicles() {
        return this.vehicles;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public void setHeight(final int height) {
        this.height = height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public void setWidth(final int width) {
        this.width = width;
    }
    
    public int getWorldX() {
        return this.worldX;
    }
    
    public void setWorldX(final int worldX) {
        this.worldX = worldX;
    }
    
    public int getWorldY() {
        return this.worldY;
    }
    
    public void setWorldY(final int worldY) {
        this.worldY = worldY;
    }
    
    public boolean isSafeToAdd() {
        return this.safeToAdd;
    }
    
    public void setSafeToAdd(final boolean safeToAdd) {
        this.safeToAdd = safeToAdd;
    }
    
    public Stack<IsoLightSource> getLamppostPositions() {
        return this.LamppostPositions;
    }
    
    public IsoLightSource getLightSourceAt(final int n, final int n2, final int n3) {
        for (int i = 0; i < this.LamppostPositions.size(); ++i) {
            final IsoLightSource isoLightSource = this.LamppostPositions.get(i);
            if (isoLightSource.getX() == n && isoLightSource.getY() == n2 && isoLightSource.getZ() == n3) {
                return isoLightSource;
            }
        }
        return null;
    }
    
    public void addLamppost(final IsoLightSource isoLightSource) {
        if (isoLightSource == null || this.LamppostPositions.contains(isoLightSource)) {
            return;
        }
        this.LamppostPositions.add(isoLightSource);
        IsoGridSquare.RecalcLightTime = -1;
        GameTime.instance.lightSourceUpdate = 100.0f;
    }
    
    public IsoLightSource addLamppost(final int n, final int n2, final int n3, final float n4, final float n5, final float n6, final int n7) {
        final IsoLightSource e = new IsoLightSource(n, n2, n3, n4, n5, n6, n7);
        this.LamppostPositions.add(e);
        IsoGridSquare.RecalcLightTime = -1;
        GameTime.instance.lightSourceUpdate = 100.0f;
        return e;
    }
    
    public void removeLamppost(final int n, final int n2, final int n3) {
        for (int i = 0; i < this.LamppostPositions.size(); ++i) {
            final IsoLightSource o = this.LamppostPositions.get(i);
            if (o.getX() == n && o.getY() == n2 && o.getZ() == n3) {
                o.clearInfluence();
                this.LamppostPositions.remove(o);
                IsoGridSquare.RecalcLightTime = -1;
                GameTime.instance.lightSourceUpdate = 100.0f;
                return;
            }
        }
    }
    
    public void removeLamppost(final IsoLightSource isoLightSource) {
        isoLightSource.life = 0;
        IsoGridSquare.RecalcLightTime = -1;
        GameTime.instance.lightSourceUpdate = 100.0f;
    }
    
    public int getCurrentLightX() {
        return this.currentLX;
    }
    
    public void setCurrentLightX(final int currentLX) {
        this.currentLX = currentLX;
    }
    
    public int getCurrentLightY() {
        return this.currentLY;
    }
    
    public void setCurrentLightY(final int currentLY) {
        this.currentLY = currentLY;
    }
    
    public int getCurrentLightZ() {
        return this.currentLZ;
    }
    
    public void setCurrentLightZ(final int currentLZ) {
        this.currentLZ = currentLZ;
    }
    
    public int getMinX() {
        return this.minX;
    }
    
    public void setMinX(final int minX) {
        this.minX = minX;
    }
    
    public int getMaxX() {
        return this.maxX;
    }
    
    public void setMaxX(final int maxX) {
        this.maxX = maxX;
    }
    
    public int getMinY() {
        return this.minY;
    }
    
    public void setMinY(final int minY) {
        this.minY = minY;
    }
    
    public int getMaxY() {
        return this.maxY;
    }
    
    public void setMaxY(final int maxY) {
        this.maxY = maxY;
    }
    
    public int getMinZ() {
        return this.minZ;
    }
    
    public void setMinZ(final int minZ) {
        this.minZ = minZ;
    }
    
    public int getMaxZ() {
        return this.maxZ;
    }
    
    public void setMaxZ(final int maxZ) {
        this.maxZ = maxZ;
    }
    
    public OnceEvery getDangerUpdate() {
        return this.dangerUpdate;
    }
    
    public void setDangerUpdate(final OnceEvery dangerUpdate) {
        this.dangerUpdate = dangerUpdate;
    }
    
    public Thread getLightInfoUpdate() {
        return this.LightInfoUpdate;
    }
    
    public void setLightInfoUpdate(final Thread lightInfoUpdate) {
        this.LightInfoUpdate = lightInfoUpdate;
    }
    
    public ArrayList<IsoSurvivor> getSurvivorList() {
        return this.SurvivorList;
    }
    
    public static int getRComponent(final int n) {
        return n & 0xFF;
    }
    
    public static int getGComponent(final int n) {
        return (n & 0xFF00) >> 8;
    }
    
    public static int getBComponent(final int n) {
        return (n & 0xFF0000) >> 16;
    }
    
    public static int toIntColor(final float n, final float n2, final float n3, final float n4) {
        return (int)(n * 255.0f) << 0 | (int)(n2 * 255.0f) << 8 | (int)(n3 * 255.0f) << 16 | (int)(n4 * 255.0f) << 24;
    }
    
    public IsoGridSquare getRandomOutdoorTile() {
        IsoGridSquare gridSquare;
        do {
            gridSquare = this.getGridSquare(this.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldXMin() * 10 + Rand.Next(this.width), this.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldYMin() * 10 + Rand.Next(this.height), 0);
            if (gridSquare != null) {
                gridSquare.setCachedIsFree(false);
            }
        } while (gridSquare == null || !gridSquare.isFree(false) || gridSquare.getRoom() != null);
        return gridSquare;
    }
    
    private static void InsertAt(final int n, final BuildingScore buildingScore, final BuildingScore[] array) {
        for (int i = array.length - 1; i > n; --i) {
            array[i] = array[i - 1];
        }
        array[n] = buildingScore;
    }
    
    static void Place(final BuildingScore buildingScore, final BuildingScore[] array, final BuildingSearchCriteria buildingSearchCriteria) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                boolean b = false;
                if (array[i] == null) {
                    b = true;
                }
                else {
                    switch (buildingSearchCriteria) {
                        case General: {
                            if (array[i].food + array[i].defense + array[i].size + array[i].weapons < buildingScore.food + buildingScore.defense + buildingScore.size + buildingScore.weapons) {
                                b = true;
                                break;
                            }
                            break;
                        }
                        case Food: {
                            if (array[i].food < buildingScore.food) {
                                b = true;
                                break;
                            }
                            break;
                        }
                        case Wood: {
                            if (array[i].wood < buildingScore.wood) {
                                b = true;
                                break;
                            }
                            break;
                        }
                        case Weapons: {
                            if (array[i].weapons < buildingScore.weapons) {
                                b = true;
                                break;
                            }
                            break;
                        }
                        case Defense: {
                            if (array[i].defense < buildingScore.defense) {
                                b = true;
                                break;
                            }
                            break;
                        }
                    }
                }
                if (b) {
                    InsertAt(i, buildingScore, array);
                    return;
                }
            }
        }
    }
    
    public Stack<BuildingScore> getBestBuildings(final BuildingSearchCriteria buildingSearchCriteria, final int n) {
        final BuildingScore[] a = new BuildingScore[n];
        if (this.BuildingScores.isEmpty()) {
            for (int size = this.BuildingList.size(), i = 0; i < size; ++i) {
                this.BuildingList.get(i).update();
            }
        }
        for (int size2 = this.BuildingScores.size(), j = 0; j < size2; ++j) {
            Place(this.BuildingScores.get(j), a, buildingSearchCriteria);
        }
        IsoCell.buildingscores.clear();
        IsoCell.buildingscores.addAll((Collection<?>)Arrays.asList(a));
        return IsoCell.buildingscores;
    }
    
    public boolean blocked(final Mover mover, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final IsoGridSquare gridSquare = this.getGridSquare(n4, n5, n6);
        if (gridSquare == null) {
            return true;
        }
        if (mover instanceof IsoMovingObject) {
            if (gridSquare.testPathFindAdjacent((IsoMovingObject)mover, n - n4, n2 - n5, n3 - n6)) {
                return true;
            }
        }
        else if (gridSquare.testPathFindAdjacent(null, n - n4, n2 - n5, n3 - n6)) {
            return true;
        }
        return false;
    }
    
    public void Dispose() {
        for (int i = 0; i < this.ObjectList.size(); ++i) {
            final IsoMovingObject isoMovingObject = this.ObjectList.get(i);
            if (isoMovingObject instanceof IsoZombie) {
                isoMovingObject.setCurrent(null);
                isoMovingObject.setLast(null);
                VirtualZombieManager.instance.addToReusable((IsoZombie)isoMovingObject);
            }
        }
        for (int j = 0; j < this.RoomList.size(); ++j) {
            this.RoomList.get(j).TileList.clear();
            this.RoomList.get(j).Exits.clear();
            this.RoomList.get(j).WaterSources.clear();
            this.RoomList.get(j).lightSwitches.clear();
            this.RoomList.get(j).Beds.clear();
        }
        for (int k = 0; k < this.BuildingList.size(); ++k) {
            this.BuildingList.get(k).Exits.clear();
            this.BuildingList.get(k).Rooms.clear();
            this.BuildingList.get(k).container.clear();
            this.BuildingList.get(k).Windows.clear();
        }
        LuaEventManager.clear();
        LuaHookManager.clear();
        this.LamppostPositions.clear();
        this.ProcessItems.clear();
        this.ProcessItemsRemove.clear();
        this.ProcessWorldItems.clear();
        this.ProcessWorldItemsRemove.clear();
        this.BuildingScores.clear();
        this.BuildingList.clear();
        this.WindowList.clear();
        this.PushableObjectList.clear();
        this.RoomList.clear();
        this.SurvivorList.clear();
        this.ObjectList.clear();
        this.ZombieList.clear();
        for (int l = 0; l < this.ChunkMap.length; ++l) {
            this.ChunkMap[l].Dispose();
            this.ChunkMap[l] = null;
        }
        for (int n = 0; n < this.gridSquares.length; ++n) {
            if (this.gridSquares[n] != null) {
                Arrays.fill(this.gridSquares[n], null);
                this.gridSquares[n] = null;
            }
        }
    }
    
    @LuaMethod(name = "getGridSquare")
    public IsoGridSquare getGridSquare(final double n, final double n2, final double n3) {
        if (GameServer.bServer) {
            return ServerMap.instance.getGridSquare((int)n, (int)n2, (int)n3);
        }
        return this.getGridSquare((int)n, (int)n2, (int)n3);
    }
    
    @LuaMethod(name = "getOrCreateGridSquare")
    public IsoGridSquare getOrCreateGridSquare(final double n, final double n2, final double n3) {
        if (GameServer.bServer) {
            IsoGridSquare isoGridSquare = ServerMap.instance.getGridSquare((int)n, (int)n2, (int)n3);
            if (isoGridSquare == null) {
                isoGridSquare = IsoGridSquare.getNew(this, null, (int)n, (int)n2, (int)n3);
                ServerMap.instance.setGridSquare((int)n, (int)n2, (int)n3, isoGridSquare);
                this.ConnectNewSquare(isoGridSquare, true);
            }
            return isoGridSquare;
        }
        IsoGridSquare isoGridSquare2 = this.getGridSquare((int)n, (int)n2, (int)n3);
        if (isoGridSquare2 == null) {
            isoGridSquare2 = IsoGridSquare.getNew(this, null, (int)n, (int)n2, (int)n3);
            this.ConnectNewSquare(isoGridSquare2, true);
        }
        return isoGridSquare2;
    }
    
    public void setCacheGridSquare(final int n, final int n2, final int n3, final IsoGridSquare isoGridSquare) {
        assert n == isoGridSquare.getX() && n2 == isoGridSquare.getY() && n3 == isoGridSquare.getZ();
        if (GameServer.bServer) {
            return;
        }
        assert this.getChunkForGridSquare(n, n2, n3) != null;
        final int chunkWidthInTiles = IsoChunkMap.ChunkWidthInTiles;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (!this.ChunkMap[i].ignore) {
                this.ChunkMap[i].YMinTiles = -1;
                this.ChunkMap[i].XMinTiles = -1;
                this.ChunkMap[i].YMaxTiles = -1;
                this.ChunkMap[i].XMaxTiles = -1;
                final int n4 = n - this.ChunkMap[i].getWorldXMinTiles();
                final int n5 = n2 - this.ChunkMap[i].getWorldYMinTiles();
                if (n3 < 8 && n3 >= 0 && n4 >= 0 && n4 < chunkWidthInTiles && n5 >= 0) {
                    if (n5 < chunkWidthInTiles) {
                        this.gridSquares[i][n4 + n5 * chunkWidthInTiles + n3 * chunkWidthInTiles * chunkWidthInTiles] = isoGridSquare;
                    }
                }
            }
        }
    }
    
    public void setCacheChunk(final IsoChunk isoChunk) {
        if (GameServer.bServer) {
            return;
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            this.setCacheChunk(isoChunk, i);
        }
    }
    
    public void setCacheChunk(final IsoChunk isoChunk, final int n) {
        if (GameServer.bServer) {
            return;
        }
        final int chunkWidthInTiles = IsoChunkMap.ChunkWidthInTiles;
        final IsoChunkMap isoChunkMap = this.ChunkMap[n];
        if (isoChunkMap.ignore) {
            return;
        }
        final int n2 = isoChunk.wx - isoChunkMap.getWorldXMin();
        final int n3 = isoChunk.wy - isoChunkMap.getWorldYMin();
        if (n2 < 0 || n2 >= IsoChunkMap.ChunkGridWidth || n3 < 0 || n3 >= IsoChunkMap.ChunkGridWidth) {
            return;
        }
        final IsoGridSquare[] array = this.gridSquares[n];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 10; ++j) {
                for (int k = 0; k < 10; ++k) {
                    array[n2 * 10 + k + (n3 * 10 + j) * chunkWidthInTiles + i * chunkWidthInTiles * chunkWidthInTiles] = isoChunk.squares[i][k + j * 10];
                }
            }
        }
    }
    
    public void clearCacheGridSquare(final int n) {
        if (GameServer.bServer) {
            return;
        }
        final int chunkWidthInTiles = IsoChunkMap.ChunkWidthInTiles;
        this.gridSquares[n] = new IsoGridSquare[chunkWidthInTiles * chunkWidthInTiles * 8];
    }
    
    public void setCacheGridSquareLocal(final int n, final int n2, final int n3, final IsoGridSquare isoGridSquare, final int n4) {
        if (GameServer.bServer) {
            return;
        }
        final int chunkWidthInTiles = IsoChunkMap.ChunkWidthInTiles;
        if (n3 >= 8 || n3 < 0 || n < 0 || n >= chunkWidthInTiles || n2 < 0 || n2 >= chunkWidthInTiles) {
            return;
        }
        this.gridSquares[n4][n + n2 * chunkWidthInTiles + n3 * chunkWidthInTiles * chunkWidthInTiles] = isoGridSquare;
    }
    
    public IsoGridSquare getGridSquare(final Double n, final Double n2, final Double n3) {
        return this.getGridSquare(n.intValue(), n2.intValue(), n3.intValue());
    }
    
    public IsoGridSquare getGridSquare(final int n, final int n2, final int n3) {
        if (GameServer.bServer) {
            return ServerMap.instance.getGridSquare(n, n2, n3);
        }
        final int chunkWidthInTiles = IsoChunkMap.ChunkWidthInTiles;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            if (!this.ChunkMap[i].ignore) {
                if (n3 == 0) {}
                final int n4 = n - this.ChunkMap[i].getWorldXMinTiles();
                final int n5 = n2 - this.ChunkMap[i].getWorldYMinTiles();
                if (n3 < 8 && n3 >= 0 && n4 >= 0 && n4 < chunkWidthInTiles && n5 >= 0) {
                    if (n5 < chunkWidthInTiles) {
                        final IsoGridSquare isoGridSquare = this.gridSquares[i][n4 + n5 * chunkWidthInTiles + n3 * chunkWidthInTiles * chunkWidthInTiles];
                        if (isoGridSquare != null) {
                            return isoGridSquare;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public void EnsureSurroundNotNull(final int n, final int n2, final int n3) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                this.createNewGridSquare(n + i, n2 + j, n3, false);
            }
        }
    }
    
    public void DeleteAllMovingObjects() {
        this.ObjectList.clear();
    }
    
    @LuaMethod(name = "getMaxFloors")
    public int getMaxFloors() {
        return 8;
    }
    
    public KahluaTable getLuaObjectList() {
        final KahluaTable table = LuaManager.platform.newTable();
        LuaManager.env.rawset((Object)"Objects", (Object)table);
        for (int i = 0; i < this.ObjectList.size(); ++i) {
            table.rawset(i + 1, (Object)this.ObjectList.get(i));
        }
        return table;
    }
    
    public int getHeightInTiles() {
        return this.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
    }
    
    public int getWidthInTiles() {
        return this.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
    }
    
    public boolean isNull(final int n, final int n2, final int n3) {
        final IsoGridSquare gridSquare = this.getGridSquare(n, n2, n3);
        return gridSquare == null || !gridSquare.isFree(false);
    }
    
    public void Remove(final IsoMovingObject e) {
        if (e instanceof IsoPlayer && !((IsoPlayer)e).isDead()) {
            return;
        }
        this.removeList.add(e);
    }
    
    boolean isBlocked(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        return isoGridSquare.room != isoGridSquare2.room;
    }
    
    private int CalculateColor(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2, final IsoGridSquare isoGridSquare3, final IsoGridSquare isoGridSquare4, final int n, final int n2) {
        float n3 = 0.0f;
        float n4 = 0.0f;
        float n5 = 0.0f;
        if (isoGridSquare4 == null) {
            return 0;
        }
        float n6 = 0.0f;
        if (isoGridSquare != null && isoGridSquare4.room == isoGridSquare.room && isoGridSquare.getChunk() != null) {
            ++n6;
            final ColorInfo lightInfo = isoGridSquare.lighting[n2].lightInfo();
            n3 += lightInfo.r;
            n4 += lightInfo.g;
            n5 += lightInfo.b;
        }
        if (isoGridSquare2 != null && isoGridSquare4.room == isoGridSquare2.room && isoGridSquare2.getChunk() != null) {
            ++n6;
            final ColorInfo lightInfo2 = isoGridSquare2.lighting[n2].lightInfo();
            n3 += lightInfo2.r;
            n4 += lightInfo2.g;
            n5 += lightInfo2.b;
        }
        if (isoGridSquare3 != null && isoGridSquare4.room == isoGridSquare3.room && isoGridSquare3.getChunk() != null) {
            ++n6;
            final ColorInfo lightInfo3 = isoGridSquare3.lighting[n2].lightInfo();
            n3 += lightInfo3.r;
            n4 += lightInfo3.g;
            n5 += lightInfo3.b;
        }
        if (isoGridSquare4 != null) {
            ++n6;
            final ColorInfo lightInfo4 = isoGridSquare4.lighting[n2].lightInfo();
            n3 += lightInfo4.r;
            n4 += lightInfo4.g;
            n5 += lightInfo4.b;
        }
        if (n6 != 0.0f) {
            n3 /= n6;
            n4 /= n6;
            n5 /= n6;
        }
        if (n3 > 1.0f) {
            n3 = 1.0f;
        }
        if (n4 > 1.0f) {
            n4 = 1.0f;
        }
        if (n5 > 1.0f) {
            n5 = 1.0f;
        }
        if (n3 < 0.0f) {
            n3 = 0.0f;
        }
        if (n4 < 0.0f) {
            n4 = 0.0f;
        }
        if (n5 < 0.0f) {
            n5 = 0.0f;
        }
        if (isoGridSquare4 != null) {
            isoGridSquare4.setVertLight(n, (int)(n3 * 255.0f) << 0 | (int)(n4 * 255.0f) << 8 | (int)(n5 * 255.0f) << 16 | 0xFF000000, n2);
            isoGridSquare4.setVertLight(n + 4, (int)(n3 * 255.0f) << 0 | (int)(n4 * 255.0f) << 8 | (int)(n5 * 255.0f) << 16 | 0xFF000000, n2);
        }
        return n;
    }
    
    public static IsoCell getInstance() {
        return IsoCell.instance;
    }
    
    public void render() {
        s_performance.isoCellRender.invokeAndMeasure(this, IsoCell::renderInternal);
    }
    
    private void renderInternal() {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final IsoPlayer isoPlayer = IsoPlayer.players[playerIndex];
        if (isoPlayer.dirtyRecalcGridStackTime > 0.0f) {
            isoPlayer.dirtyRecalcGridStack = true;
        }
        else {
            isoPlayer.dirtyRecalcGridStack = false;
        }
        if (!PerformanceSettings.NewRoofHiding) {
            if (this.bHideFloors[playerIndex] && this.unhideFloorsCounter[playerIndex] > 0) {
                final int[] unhideFloorsCounter = this.unhideFloorsCounter;
                final int n = playerIndex;
                --unhideFloorsCounter[n];
            }
            if (this.unhideFloorsCounter[playerIndex] <= 0) {
                this.bHideFloors[playerIndex] = false;
                this.unhideFloorsCounter[playerIndex] = 60;
            }
        }
        int hidesOrphanStructuresAbove = 8;
        if (hidesOrphanStructuresAbove < 8) {
            ++hidesOrphanStructuresAbove;
        }
        --this.recalcShading;
        final int n2 = 0;
        final int n3 = 0;
        final int n4 = n2 + IsoCamera.getOffscreenWidth(playerIndex);
        final int n5 = n3 + IsoCamera.getOffscreenHeight(playerIndex);
        final float xToIso = IsoUtils.XToIso((float)n2, (float)n3, 0.0f);
        final float yToIso = IsoUtils.YToIso((float)n4, (float)n3, 0.0f);
        final float xToIso2 = IsoUtils.XToIso((float)n4, (float)n5, 6.0f);
        final float yToIso2 = IsoUtils.YToIso((float)n2, (float)n5, 6.0f);
        this.minY = (int)yToIso;
        this.maxY = (int)yToIso2;
        this.minX = (int)xToIso;
        this.maxX = (int)xToIso2;
        this.minX -= 2;
        this.minY -= 2;
        this.maxZ = IsoCell.MaxHeight;
        if (IsoCamera.CamCharacter == null) {
            this.maxZ = 1;
        }
        if (GameTime.instance.FPSMultiplier > 1.5f) {}
        if (this.minX != this.lastMinX || this.minY != this.lastMinY) {
            this.lightUpdateCount = 10;
        }
        if (!PerformanceSettings.NewRoofHiding) {
            final IsoGridSquare isoGridSquare = (IsoCamera.CamCharacter == null) ? null : IsoCamera.CamCharacter.getCurrentSquare();
            if (isoGridSquare != null) {
                final IsoGridSquare gridSquare = this.getGridSquare(Math.round(IsoCamera.CamCharacter.getX()), Math.round(IsoCamera.CamCharacter.getY()), IsoCamera.CamCharacter.getZ());
                if (gridSquare != null && this.IsBehindStuff(gridSquare)) {
                    this.bHideFloors[playerIndex] = true;
                }
                if ((!this.bHideFloors[playerIndex] && isoGridSquare.getProperties().Is(IsoFlagType.hidewalls)) || !isoGridSquare.getProperties().Is(IsoFlagType.exterior)) {
                    this.bHideFloors[playerIndex] = true;
                }
            }
            if (this.bHideFloors[playerIndex]) {
                this.maxZ = (int)IsoCamera.CamCharacter.getZ() + 1;
            }
        }
        if (PerformanceSettings.LightingFrameSkip < 3) {
            this.DrawStencilMask();
        }
        if (PerformanceSettings.LightingFrameSkip == 3) {
            final int n6 = IsoCamera.getOffscreenWidth(playerIndex) / 2;
            final int n7 = IsoCamera.getOffscreenHeight(playerIndex) / 2;
            final int n8 = 409;
            final int n9 = n6 - n8 / (2 / Core.TileScale);
            final int n10 = n7 - n8 / (2 / Core.TileScale);
            this.StencilX1 = n9 - (int)IsoCamera.cameras[playerIndex].RightClickX;
            this.StencilY1 = n10 - (int)IsoCamera.cameras[playerIndex].RightClickY;
            this.StencilX2 = this.StencilX1 + n8 * Core.TileScale;
            this.StencilY2 = this.StencilY1 + n8 * Core.TileScale;
        }
        if (PerformanceSettings.NewRoofHiding && isoPlayer.dirtyRecalcGridStack) {
            this.hidesOrphanStructuresAbove = hidesOrphanStructuresAbove;
            IsoGridSquare isoGridSquare2 = null;
            this.otherOccluderBuildings.get(playerIndex).clear();
            if (this.otherOccluderBuildingsArr[playerIndex] != null) {
                this.otherOccluderBuildingsArr[playerIndex][0] = null;
            }
            else {
                this.otherOccluderBuildingsArr[playerIndex] = new IsoBuilding[500];
            }
            if (IsoCamera.CamCharacter != null && IsoCamera.CamCharacter.getCurrentSquare() != null) {
                final IsoGridSquare currentSquare = IsoCamera.CamCharacter.getCurrentSquare();
                int size = 10;
                if (this.ZombieList.size() < 10) {
                    size = this.ZombieList.size();
                }
                if (this.nearestVisibleZombie[playerIndex] != null) {
                    if (this.nearestVisibleZombie[playerIndex].isDead()) {
                        this.nearestVisibleZombie[playerIndex] = null;
                    }
                    else {
                        final float n11 = this.nearestVisibleZombie[playerIndex].x - IsoCamera.CamCharacter.x;
                        final float n12 = this.nearestVisibleZombie[playerIndex].y - IsoCamera.CamCharacter.y;
                        this.nearestVisibleZombieDistSqr[playerIndex] = n11 * n11 + n12 * n12;
                    }
                }
                for (int i = 0; i < size; ++i, ++this.zombieScanCursor) {
                    if (this.zombieScanCursor >= this.ZombieList.size()) {
                        this.zombieScanCursor = 0;
                    }
                    final IsoZombie isoZombie = this.ZombieList.get(this.zombieScanCursor);
                    if (isoZombie != null) {
                        final IsoGridSquare currentSquare2 = isoZombie.getCurrentSquare();
                        if (currentSquare2 != null && currentSquare.z == currentSquare2.z && currentSquare2.getCanSee(playerIndex)) {
                            if (this.nearestVisibleZombie[playerIndex] == null) {
                                this.nearestVisibleZombie[playerIndex] = isoZombie;
                                final float n13 = this.nearestVisibleZombie[playerIndex].x - IsoCamera.CamCharacter.x;
                                final float n14 = this.nearestVisibleZombie[playerIndex].y - IsoCamera.CamCharacter.y;
                                this.nearestVisibleZombieDistSqr[playerIndex] = n13 * n13 + n14 * n14;
                            }
                            else {
                                final float n15 = isoZombie.x - IsoCamera.CamCharacter.x;
                                final float n16 = isoZombie.y - IsoCamera.CamCharacter.y;
                                final float n17 = n15 * n15 + n16 * n16;
                                if (n17 < this.nearestVisibleZombieDistSqr[playerIndex]) {
                                    this.nearestVisibleZombie[playerIndex] = isoZombie;
                                    this.nearestVisibleZombieDistSqr[playerIndex] = n17;
                                }
                            }
                        }
                    }
                }
                for (int j = 0; j < 4; ++j) {
                    final IsoPlayer isoPlayer2 = IsoPlayer.players[j];
                    if (isoPlayer2 != null && isoPlayer2.getCurrentSquare() != null) {
                        final IsoGridSquare currentSquare3 = isoPlayer2.getCurrentSquare();
                        if (j == playerIndex) {
                            isoGridSquare2 = currentSquare3;
                        }
                        final boolean b = isoPlayer2.x - Math.floor(isoPlayer2.x) > isoPlayer2.y - Math.floor(isoPlayer2.y);
                        if (this.lastPlayerAngle[j] == null) {
                            this.lastPlayerAngle[j] = new Vector2(isoPlayer2.getForwardDirection());
                            this.playerCutawaysDirty[j] = true;
                        }
                        else if (isoPlayer2.getForwardDirection().dot(this.lastPlayerAngle[j]) < 0.98f) {
                            this.lastPlayerAngle[j].set(isoPlayer2.getForwardDirection());
                            this.playerCutawaysDirty[j] = true;
                        }
                        final IsoDirections fromAngle = IsoDirections.fromAngle(isoPlayer2.getForwardDirection());
                        if (this.lastPlayerSquare[j] != currentSquare3 || this.lastPlayerSquareHalf[j] != b || this.lastPlayerDir[j] != fromAngle) {
                            this.playerCutawaysDirty[j] = true;
                            this.lastPlayerSquare[j] = currentSquare3;
                            this.lastPlayerSquareHalf[j] = b;
                            this.lastPlayerDir[j] = fromAngle;
                            IsoBuilding isoBuilding = currentSquare3.getBuilding();
                            this.playerWindowPeekingRoomId[j] = -1;
                            this.GetBuildingsInFrontOfCharacter(this.playerOccluderBuildings.get(j), currentSquare3, b);
                            if (this.playerOccluderBuildingsArr[playerIndex] == null) {
                                this.playerOccluderBuildingsArr[playerIndex] = new IsoBuilding[500];
                            }
                            this.playerHidesOrphanStructures[j] = this.bOccludedByOrphanStructureFlag;
                            if (isoBuilding == null && !isoPlayer2.bRemote) {
                                isoBuilding = this.GetPeekedInBuilding(currentSquare3, fromAngle);
                                if (isoBuilding != null) {
                                    this.playerWindowPeekingRoomId[j] = this.playerPeekedRoomId;
                                }
                            }
                            if (isoBuilding != null) {
                                this.AddUniqueToBuildingList(this.playerOccluderBuildings.get(j), isoBuilding);
                            }
                            final ArrayList<IsoBuilding> list = this.playerOccluderBuildings.get(j);
                            for (int k = 0; k < list.size(); ++k) {
                                this.playerOccluderBuildingsArr[playerIndex][k] = list.get(k);
                            }
                            this.playerOccluderBuildingsArr[playerIndex][list.size()] = null;
                        }
                        if (j == playerIndex && isoGridSquare2 != null) {
                            this.gridSquaresTempLeft.clear();
                            this.gridSquaresTempRight.clear();
                            this.GetSquaresAroundPlayerSquare(isoPlayer2, isoGridSquare2, this.gridSquaresTempLeft, this.gridSquaresTempRight);
                            for (int l = 0; l < this.gridSquaresTempLeft.size(); ++l) {
                                final IsoGridSquare isoGridSquare3 = this.gridSquaresTempLeft.get(l);
                                if (isoGridSquare3.getCanSee(playerIndex) && (isoGridSquare3.getBuilding() == null || isoGridSquare3.getBuilding() == isoGridSquare2.getBuilding())) {
                                    final ArrayList<IsoBuilding> getBuildingsInFrontOfMustSeeSquare = this.GetBuildingsInFrontOfMustSeeSquare(isoGridSquare3, IsoGridOcclusionData.OcclusionFilter.Right);
                                    for (int index = 0; index < getBuildingsInFrontOfMustSeeSquare.size(); ++index) {
                                        this.AddUniqueToBuildingList(this.otherOccluderBuildings.get(playerIndex), getBuildingsInFrontOfMustSeeSquare.get(index));
                                    }
                                    final boolean[] playerHidesOrphanStructures = this.playerHidesOrphanStructures;
                                    final int n18 = playerIndex;
                                    playerHidesOrphanStructures[n18] |= this.bOccludedByOrphanStructureFlag;
                                }
                            }
                            for (int index2 = 0; index2 < this.gridSquaresTempRight.size(); ++index2) {
                                final IsoGridSquare isoGridSquare4 = this.gridSquaresTempRight.get(index2);
                                if (isoGridSquare4.getCanSee(playerIndex) && (isoGridSquare4.getBuilding() == null || isoGridSquare4.getBuilding() == isoGridSquare2.getBuilding())) {
                                    final ArrayList<IsoBuilding> getBuildingsInFrontOfMustSeeSquare2 = this.GetBuildingsInFrontOfMustSeeSquare(isoGridSquare4, IsoGridOcclusionData.OcclusionFilter.Left);
                                    for (int index3 = 0; index3 < getBuildingsInFrontOfMustSeeSquare2.size(); ++index3) {
                                        this.AddUniqueToBuildingList(this.otherOccluderBuildings.get(playerIndex), getBuildingsInFrontOfMustSeeSquare2.get(index3));
                                    }
                                    final boolean[] playerHidesOrphanStructures2 = this.playerHidesOrphanStructures;
                                    final int n19 = playerIndex;
                                    playerHidesOrphanStructures2[n19] |= this.bOccludedByOrphanStructureFlag;
                                }
                            }
                            final ArrayList<IsoBuilding> list2 = this.otherOccluderBuildings.get(playerIndex);
                            if (this.otherOccluderBuildingsArr[playerIndex] == null) {
                                this.otherOccluderBuildingsArr[playerIndex] = new IsoBuilding[500];
                            }
                            for (int index4 = 0; index4 < list2.size(); ++index4) {
                                this.otherOccluderBuildingsArr[playerIndex][index4] = list2.get(index4);
                            }
                            this.otherOccluderBuildingsArr[playerIndex][list2.size()] = null;
                        }
                        if (this.playerHidesOrphanStructures[j] && this.hidesOrphanStructuresAbove > currentSquare3.getZ()) {
                            this.hidesOrphanStructuresAbove = currentSquare3.getZ();
                        }
                    }
                }
                if (isoGridSquare2 != null && this.hidesOrphanStructuresAbove < isoGridSquare2.getZ()) {
                    this.hidesOrphanStructuresAbove = isoGridSquare2.getZ();
                }
                boolean b2 = false;
                if (this.nearestVisibleZombie[playerIndex] != null && this.nearestVisibleZombieDistSqr[playerIndex] < 150.0f) {
                    final IsoGridSquare currentSquare4 = this.nearestVisibleZombie[playerIndex].getCurrentSquare();
                    if (currentSquare4 != null && currentSquare4.getCanSee(playerIndex)) {
                        final boolean b3 = this.nearestVisibleZombie[playerIndex].x - Math.floor(this.nearestVisibleZombie[playerIndex].x) > this.nearestVisibleZombie[playerIndex].y - Math.floor(this.nearestVisibleZombie[playerIndex].y);
                        b2 = true;
                        if (this.lastZombieSquare[playerIndex] != currentSquare4 || this.lastZombieSquareHalf[playerIndex] != b3) {
                            this.lastZombieSquare[playerIndex] = currentSquare4;
                            this.lastZombieSquareHalf[playerIndex] = b3;
                            this.GetBuildingsInFrontOfCharacter(this.zombieOccluderBuildings.get(playerIndex), currentSquare4, b3);
                            final ArrayList<IsoBuilding> list3 = this.zombieOccluderBuildings.get(playerIndex);
                            if (this.zombieOccluderBuildingsArr[playerIndex] == null) {
                                this.zombieOccluderBuildingsArr[playerIndex] = new IsoBuilding[500];
                            }
                            for (int index5 = 0; index5 < list3.size(); ++index5) {
                                this.zombieOccluderBuildingsArr[playerIndex][index5] = list3.get(index5);
                            }
                            this.zombieOccluderBuildingsArr[playerIndex][list3.size()] = null;
                        }
                    }
                }
                if (!b2) {
                    this.zombieOccluderBuildings.get(playerIndex).clear();
                    if (this.zombieOccluderBuildingsArr[playerIndex] != null) {
                        this.zombieOccluderBuildingsArr[playerIndex][0] = null;
                    }
                    else {
                        this.zombieOccluderBuildingsArr[playerIndex] = new IsoBuilding[500];
                    }
                }
            }
            else {
                for (int index6 = 0; index6 < 4; ++index6) {
                    this.playerOccluderBuildings.get(index6).clear();
                    if (this.playerOccluderBuildingsArr[index6] != null) {
                        this.playerOccluderBuildingsArr[index6][0] = null;
                    }
                    else {
                        this.playerOccluderBuildingsArr[index6] = new IsoBuilding[500];
                    }
                    this.lastPlayerSquare[index6] = null;
                    this.playerCutawaysDirty[index6] = true;
                }
                this.playerWindowPeekingRoomId[playerIndex] = -1;
                this.zombieOccluderBuildings.get(playerIndex).clear();
                if (this.zombieOccluderBuildingsArr[playerIndex] != null) {
                    this.zombieOccluderBuildingsArr[playerIndex][0] = null;
                }
                else {
                    this.zombieOccluderBuildingsArr[playerIndex] = new IsoBuilding[500];
                }
                this.lastZombieSquare[playerIndex] = null;
            }
        }
        if (!PerformanceSettings.NewRoofHiding) {
            for (int n20 = 0; n20 < IsoPlayer.numPlayers; ++n20) {
                this.playerWindowPeekingRoomId[n20] = -1;
                final IsoPlayer isoPlayer3 = IsoPlayer.players[n20];
                if (isoPlayer3 != null) {
                    if (isoPlayer3.getCurrentBuilding() == null && this.GetPeekedInBuilding(isoPlayer3.getCurrentSquare(), IsoDirections.fromAngle(isoPlayer3.getForwardDirection())) != null) {
                        this.playerWindowPeekingRoomId[n20] = this.playerPeekedRoomId;
                    }
                }
            }
        }
        if (IsoCamera.CamCharacter != null && IsoCamera.CamCharacter.getCurrentSquare() != null && IsoCamera.CamCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.hidewalls)) {
            this.maxZ = (int)IsoCamera.CamCharacter.getZ() + 1;
        }
        this.bRendering = true;
        try {
            this.RenderTiles(hidesOrphanStructuresAbove);
        }
        catch (Exception thrown) {
            this.bRendering = false;
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, thrown);
        }
        this.bRendering = false;
        if (IsoGridSquare.getRecalcLightTime() < 0) {
            IsoGridSquare.setRecalcLightTime(60);
        }
        if (IsoGridSquare.getLightcache() <= 0) {
            IsoGridSquare.setLightcache(90);
        }
        for (int index7 = 0; index7 < this.ObjectList.size(); ++index7) {
            this.ObjectList.get(index7).renderlast();
        }
        for (int index8 = 0; index8 < this.StaticUpdaterObjectList.size(); ++index8) {
            this.StaticUpdaterObjectList.get(index8).renderlast();
        }
        IsoTree.renderChopTreeIndicators();
        if (Core.bDebug) {}
        this.lastMinX = this.minX;
        this.lastMinY = this.minY;
        this.DoBuilding(IsoPlayer.getPlayerIndex(), true);
        this.renderRain();
    }
    
    public void invalidatePeekedRoom(final int n) {
        this.lastPlayerDir[n] = IsoDirections.Max;
    }
    
    private boolean initWeatherFx() {
        if (GameServer.bServer) {
            return false;
        }
        if (this.weatherFX == null) {
            (this.weatherFX = new IsoWeatherFX()).init();
        }
        return true;
    }
    
    private void updateWeatherFx() {
        if (this.initWeatherFx()) {
            this.weatherFX.update();
        }
    }
    
    private void renderWeatherFx() {
        if (this.initWeatherFx()) {
            this.weatherFX.render();
        }
    }
    
    public IsoWeatherFX getWeatherFX() {
        return this.weatherFX;
    }
    
    private void renderRain() {
    }
    
    public void setRainAlpha(final int n) {
        this.rainAlphaMax = n / 100.0f;
    }
    
    public void setRainIntensity(final int rainIntensity) {
        this.rainIntensity = rainIntensity;
    }
    
    public void setRainSpeed(final int rainSpeed) {
        this.rainSpeed = rainSpeed;
    }
    
    public void reloadRainTextures() {
    }
    
    private void GetBuildingsInFrontOfCharacter(final ArrayList<IsoBuilding> list, final IsoGridSquare isoGridSquare, final boolean b) {
        list.clear();
        this.bOccludedByOrphanStructureFlag = false;
        if (isoGridSquare == null) {
            return;
        }
        final int x = isoGridSquare.getX();
        final int y = isoGridSquare.getY();
        final int z = isoGridSquare.getZ();
        this.GetBuildingsInFrontOfCharacterSquare(x, y, z, b, list);
        if (z < IsoCell.MaxHeight) {
            this.GetBuildingsInFrontOfCharacterSquare(x - 1 + 3, y - 1 + 3, z + 1, b, list);
            this.GetBuildingsInFrontOfCharacterSquare(x - 2 + 3, y - 2 + 3, z + 1, b, list);
            if (b) {
                this.GetBuildingsInFrontOfCharacterSquare(x + 3, y - 1 + 3, z + 1, !b, list);
                this.GetBuildingsInFrontOfCharacterSquare(x - 1 + 3, y - 2 + 3, z + 1, !b, list);
            }
            else {
                this.GetBuildingsInFrontOfCharacterSquare(x - 1 + 3, y + 3, z + 1, !b, list);
                this.GetBuildingsInFrontOfCharacterSquare(x - 2 + 3, y - 1 + 3, z + 1, !b, list);
            }
        }
    }
    
    private void GetBuildingsInFrontOfCharacterSquare(final int n, final int n2, final int n3, final boolean b, final ArrayList<IsoBuilding> list) {
        final IsoGridSquare gridSquare = this.getGridSquare(n, n2, n3);
        if (gridSquare == null) {
            if (n3 < IsoCell.MaxHeight) {
                this.GetBuildingsInFrontOfCharacterSquare(n + 3, n2 + 3, n3 + 1, b, list);
            }
            return;
        }
        final IsoGridOcclusionData orCreateOcclusionData = gridSquare.getOrCreateOcclusionData();
        final IsoGridOcclusionData.OcclusionFilter occlusionFilter = b ? IsoGridOcclusionData.OcclusionFilter.Right : IsoGridOcclusionData.OcclusionFilter.Left;
        this.bOccludedByOrphanStructureFlag |= orCreateOcclusionData.getCouldBeOccludedByOrphanStructures(occlusionFilter);
        final ArrayList<IsoBuilding> buildingsCouldBeOccluders = orCreateOcclusionData.getBuildingsCouldBeOccluders(occlusionFilter);
        for (int i = 0; i < buildingsCouldBeOccluders.size(); ++i) {
            this.AddUniqueToBuildingList(list, buildingsCouldBeOccluders.get(i));
        }
    }
    
    private ArrayList<IsoBuilding> GetBuildingsInFrontOfMustSeeSquare(final IsoGridSquare isoGridSquare, final IsoGridOcclusionData.OcclusionFilter occlusionFilter) {
        final IsoGridOcclusionData orCreateOcclusionData = isoGridSquare.getOrCreateOcclusionData();
        this.bOccludedByOrphanStructureFlag = orCreateOcclusionData.getCouldBeOccludedByOrphanStructures(IsoGridOcclusionData.OcclusionFilter.All);
        return orCreateOcclusionData.getBuildingsCouldBeOccluders(occlusionFilter);
    }
    
    private IsoBuilding GetPeekedInBuilding(final IsoGridSquare isoGridSquare, final IsoDirections isoDirections) {
        this.playerPeekedRoomId = -1;
        if (isoGridSquare == null) {
            return null;
        }
        if ((isoDirections == IsoDirections.NW || isoDirections == IsoDirections.N || isoDirections == IsoDirections.NE) && LosUtil.lineClear(this, isoGridSquare.x, isoGridSquare.y, isoGridSquare.z, isoGridSquare.x, isoGridSquare.y - 1, isoGridSquare.z, false) != LosUtil.TestResults.Blocked) {
            final IsoGridSquare isoGridSquare2 = isoGridSquare.nav[IsoDirections.N.index()];
            if (isoGridSquare2 != null) {
                final IsoBuilding building = isoGridSquare2.getBuilding();
                if (building != null) {
                    this.playerPeekedRoomId = isoGridSquare2.getRoomID();
                    return building;
                }
            }
        }
        if ((isoDirections == IsoDirections.SW || isoDirections == IsoDirections.W || isoDirections == IsoDirections.NW) && LosUtil.lineClear(this, isoGridSquare.x, isoGridSquare.y, isoGridSquare.z, isoGridSquare.x - 1, isoGridSquare.y, isoGridSquare.z, false) != LosUtil.TestResults.Blocked) {
            final IsoGridSquare isoGridSquare3 = isoGridSquare.nav[IsoDirections.W.index()];
            if (isoGridSquare3 != null) {
                final IsoBuilding building2 = isoGridSquare3.getBuilding();
                if (building2 != null) {
                    this.playerPeekedRoomId = isoGridSquare3.getRoomID();
                    return building2;
                }
            }
        }
        if ((isoDirections == IsoDirections.SE || isoDirections == IsoDirections.S || isoDirections == IsoDirections.SW) && LosUtil.lineClear(this, isoGridSquare.x, isoGridSquare.y, isoGridSquare.z, isoGridSquare.x, isoGridSquare.y + 1, isoGridSquare.z, false) != LosUtil.TestResults.Blocked) {
            final IsoGridSquare isoGridSquare4 = isoGridSquare.nav[IsoDirections.S.index()];
            if (isoGridSquare4 != null) {
                final IsoBuilding building3 = isoGridSquare4.getBuilding();
                if (building3 != null) {
                    this.playerPeekedRoomId = isoGridSquare4.getRoomID();
                    return building3;
                }
            }
        }
        if ((isoDirections == IsoDirections.NE || isoDirections == IsoDirections.E || isoDirections == IsoDirections.SE) && LosUtil.lineClear(this, isoGridSquare.x, isoGridSquare.y, isoGridSquare.z, isoGridSquare.x + 1, isoGridSquare.y, isoGridSquare.z, false) != LosUtil.TestResults.Blocked) {
            final IsoGridSquare isoGridSquare5 = isoGridSquare.nav[IsoDirections.E.index()];
            if (isoGridSquare5 != null) {
                final IsoBuilding building4 = isoGridSquare5.getBuilding();
                if (building4 != null) {
                    this.playerPeekedRoomId = isoGridSquare5.getRoomID();
                    return building4;
                }
            }
        }
        return null;
    }
    
    void GetSquaresAroundPlayerSquare(final IsoPlayer isoPlayer, final IsoGridSquare isoGridSquare, final ArrayList<IsoGridSquare> list, final ArrayList<IsoGridSquare> list2) {
        final float n = isoPlayer.x - 4.0f;
        final float n2 = isoPlayer.y - 4.0f;
        final int n3 = (int)n;
        final int n4 = (int)n2;
        final int z = isoGridSquare.getZ();
        for (int i = n4; i < n4 + 10; ++i) {
            for (int j = n3; j < n3 + 10; ++j) {
                if ((j >= (int)isoPlayer.x || i >= (int)isoPlayer.y) && (j != (int)isoPlayer.x || i != (int)isoPlayer.y)) {
                    final float n5 = j - isoPlayer.x;
                    final float n6 = i - isoPlayer.y;
                    if (n6 < n5 + 4.5 && n6 > n5 - 4.5) {
                        final IsoGridSquare gridSquare = this.getGridSquare(j, i, z);
                        if (gridSquare != null) {
                            if (n6 >= n5) {
                                list.add(gridSquare);
                            }
                            if (n6 <= n5) {
                                list2.add(gridSquare);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean IsBehindStuff(final IsoGridSquare isoGridSquare) {
        if (!isoGridSquare.getProperties().Is(IsoFlagType.exterior)) {
            return true;
        }
        for (int n = 1; n < 8 && isoGridSquare.getZ() + n < IsoCell.MaxHeight; ++n) {
            for (int i = -5; i <= 6; ++i) {
                for (int j = -5; j <= 6; ++j) {
                    final int n2 = i;
                    if (j >= n2 - 5) {
                        if (j <= n2 + 5) {
                            final IsoGridSquare gridSquare = this.getGridSquare(isoGridSquare.getX() + j + n * 3, isoGridSquare.getY() + i + n * 3, isoGridSquare.getZ() + n);
                            if (gridSquare != null && !gridSquare.getObjects().isEmpty()) {
                                if (n == 1 && gridSquare.getObjects().size() == 1) {
                                    final IsoObject isoObject = gridSquare.getObjects().get(0);
                                    if (isoObject.sprite != null && isoObject.sprite.name != null && isoObject.sprite.name.startsWith("lighting_outdoor")) {
                                        continue;
                                    }
                                }
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static IsoDirections FromMouseTile() {
        IsoDirections isoDirections = IsoDirections.N;
        final float x = UIManager.getPickedTileLocal().x;
        final float y = UIManager.getPickedTileLocal().y;
        final float n = 0.5f - Math.abs(0.5f - y);
        final float n2 = 0.5f - Math.abs(0.5f - x);
        if (x > 0.5f && n2 < n) {
            isoDirections = IsoDirections.E;
        }
        else if (y > 0.5f && n2 > n) {
            isoDirections = IsoDirections.S;
        }
        else if (x < 0.5f && n2 < n) {
            isoDirections = IsoDirections.W;
        }
        else if (y < 0.5f && n2 > n) {
            isoDirections = IsoDirections.N;
        }
        return isoDirections;
    }
    
    public void update() {
        s_performance.isoCellUpdate.invokeAndMeasure(this, IsoCell::updateInternal);
    }
    
    private void updateInternal() {
        MovingObjectUpdateScheduler.instance.startFrame();
        IsoSprite.alphaStep = 0.075f * (GameTime.getInstance().getMultiplier() / 1.6f);
        ++IsoGridSquare.gridSquareCacheEmptyTimer;
        this.ProcessSpottedRooms();
        if (!GameServer.bServer) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                if (IsoPlayer.players[i] != null && (!IsoPlayer.players[i].isDead() || IsoPlayer.players[i].ReanimatedCorpse != null)) {
                    IsoPlayer.setInstance(IsoPlayer.players[i]);
                    IsoCamera.CamCharacter = IsoPlayer.players[i];
                    this.ChunkMap[i].update();
                }
            }
        }
        this.ProcessRemoveItems(null);
        this.ProcessItems(null);
        this.ProcessRemoveItems(null);
        this.ProcessIsoObject();
        this.safeToAdd = false;
        this.ProcessObjects(null);
        if (GameClient.bClient && ((NetworkZombieSimulator.getInstance().anyUnknownZombies() && GameClient.instance.sendZombieRequestsTimer.Check()) || GameClient.instance.sendZombieTimer.Check())) {
            NetworkZombieSimulator.getInstance().send();
            GameClient.instance.sendZombieTimer.Reset();
            GameClient.instance.sendZombieRequestsTimer.Reset();
        }
        this.safeToAdd = true;
        this.ProcessStaticUpdaters();
        this.ObjectDeletionAddition();
        IsoDeadBody.updateBodies();
        IsoGridSquare.setLightcache(IsoGridSquare.getLightcache() - 1);
        IsoGridSquare.setRecalcLightTime(IsoGridSquare.getRecalcLightTime() - 1);
        if (GameServer.bServer) {
            this.LamppostPositions.clear();
            this.roomLights.clear();
        }
        if (!GameTime.isGamePaused()) {
            this.rainScroll += this.rainSpeed / 10.0f * 0.075f * (30.0f / PerformanceSettings.getLockFPS());
            if (this.rainScroll > 1.0f) {
                this.rainScroll = 0.0f;
            }
        }
        if (!GameServer.bServer) {
            this.updateWeatherFx();
        }
    }
    
    IsoGridSquare getRandomFreeTile() {
        boolean b;
        IsoGridSquare gridSquare;
        do {
            b = true;
            gridSquare = this.getGridSquare(Rand.Next(this.width), Rand.Next(this.height), 0);
            if (gridSquare == null) {
                b = false;
            }
            else if (!gridSquare.isFree(false)) {
                b = false;
            }
            else if (gridSquare.getProperties().Is(IsoFlagType.solid) || gridSquare.getProperties().Is(IsoFlagType.solidtrans)) {
                b = false;
            }
            else if (gridSquare.getMovingObjects().size() > 0) {
                b = false;
            }
            else if (gridSquare.Has(IsoObjectType.stairsBN) || gridSquare.Has(IsoObjectType.stairsMN) || gridSquare.Has(IsoObjectType.stairsTN)) {
                b = false;
            }
            else {
                if (!gridSquare.Has(IsoObjectType.stairsBW) && !gridSquare.Has(IsoObjectType.stairsMW) && !gridSquare.Has(IsoObjectType.stairsTW)) {
                    continue;
                }
                b = false;
            }
        } while (!b);
        return gridSquare;
    }
    
    IsoGridSquare getRandomOutdoorFreeTile() {
        boolean b;
        IsoGridSquare gridSquare;
        do {
            b = true;
            gridSquare = this.getGridSquare(Rand.Next(this.width), Rand.Next(this.height), 0);
            if (gridSquare == null) {
                b = false;
            }
            else if (!gridSquare.isFree(false)) {
                b = false;
            }
            else if (gridSquare.getRoom() != null) {
                b = false;
            }
            else if (gridSquare.getProperties().Is(IsoFlagType.solid) || gridSquare.getProperties().Is(IsoFlagType.solidtrans)) {
                b = false;
            }
            else if (gridSquare.getMovingObjects().size() > 0) {
                b = false;
            }
            else if (gridSquare.Has(IsoObjectType.stairsBN) || gridSquare.Has(IsoObjectType.stairsMN) || gridSquare.Has(IsoObjectType.stairsTN)) {
                b = false;
            }
            else {
                if (!gridSquare.Has(IsoObjectType.stairsBW) && !gridSquare.Has(IsoObjectType.stairsMW) && !gridSquare.Has(IsoObjectType.stairsTW)) {
                    continue;
                }
                b = false;
            }
        } while (!b);
        return gridSquare;
    }
    
    public IsoGridSquare getRandomFreeTileInRoom() {
        final Stack<Object> stack = new Stack<Object>();
        for (int i = 0; i < this.RoomList.size(); ++i) {
            if (this.RoomList.get(i).TileList.size() > 9 && !this.RoomList.get(i).Exits.isEmpty() && this.RoomList.get(i).TileList.get(0).getProperties().Is(IsoFlagType.solidfloor)) {
                stack.add(this.RoomList.get(i));
            }
        }
        if (stack.isEmpty()) {
            return null;
        }
        return ((IsoRoom)stack.get(Rand.Next(stack.size()))).getFreeTile();
    }
    
    public void roomSpotted(final IsoRoom isoRoom) {
        synchronized (this.SpottedRooms) {
            if (!this.SpottedRooms.contains(isoRoom)) {
                this.SpottedRooms.push(isoRoom);
            }
        }
    }
    
    public void ProcessSpottedRooms() {
        synchronized (this.SpottedRooms) {
            for (int i = 0; i < this.SpottedRooms.size(); ++i) {
                final IsoRoom isoRoom = this.SpottedRooms.get(i);
                if (!isoRoom.def.bDoneSpawn) {
                    isoRoom.def.bDoneSpawn = true;
                    LuaEventManager.triggerEvent("OnSeeNewRoom", isoRoom);
                    VirtualZombieManager.instance.roomSpotted(isoRoom);
                    if (!GameClient.bClient && !Core.bLastStand && ("shed".equals(isoRoom.def.name) || "garagestorage".equals(isoRoom.def.name) || "storageunit".equals(isoRoom.def.name))) {
                        int n = 7;
                        if ("shed".equals(isoRoom.def.name) || "garagestorage".equals(isoRoom.def.name)) {
                            n = 4;
                        }
                        switch (SandboxOptions.instance.GeneratorSpawning.getValue()) {
                            case 1: {
                                n += 3;
                                break;
                            }
                            case 2: {
                                n += 2;
                                break;
                            }
                            case 4: {
                                n -= 2;
                                break;
                            }
                            case 5: {
                                n -= 3;
                                break;
                            }
                        }
                        if (Rand.Next(n) == 0) {
                            final IsoGridSquare randomFreeSquare = isoRoom.getRandomFreeSquare();
                            if (randomFreeSquare != null) {
                                final IsoGenerator isoGenerator = new IsoGenerator(InventoryItemFactory.CreateItem("Base.Generator"), this, randomFreeSquare);
                                if (GameServer.bServer) {
                                    isoGenerator.transmitCompleteItemToClients();
                                }
                            }
                        }
                    }
                }
            }
            this.SpottedRooms.clear();
        }
    }
    
    public void savePlayer() throws IOException {
        if (IsoPlayer.players[0] != null && !IsoPlayer.players[0].isDead()) {
            IsoPlayer.players[0].save();
        }
        GameClient.instance.sendPlayerSave(IsoPlayer.players[0]);
    }
    
    public void save(DataOutputStream dataOutputStream, final boolean b) throws IOException {
        while (ChunkSaveWorker.instance.bSaving) {
            try {
                Thread.sleep(30L);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            this.ChunkMap[i].Save();
        }
        dataOutputStream.writeInt(this.width);
        dataOutputStream.writeInt(this.height);
        dataOutputStream.writeInt(IsoCell.MaxHeight);
        dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(ZomboidFileSystem.instance.getFileInCurrentSave("map_t.bin"))));
        GameTime.instance.save(dataOutputStream);
        dataOutputStream.flush();
        dataOutputStream.close();
        IsoWorld.instance.MetaGrid.save();
        if (PlayerDB.isAllow()) {
            PlayerDB.getInstance().savePlayers();
        }
        ReanimatedPlayers.instance.saveReanimatedPlayers();
    }
    
    public boolean LoadPlayer(int int1) throws FileNotFoundException, IOException {
        if (GameClient.bClient) {
            return ClientPlayerDB.getInstance().loadNetworkPlayer(1);
        }
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("map_p.bin");
        if (!fileInCurrentSave.exists()) {
            PlayerDB.getInstance().importPlayersFromVehiclesDB();
            return PlayerDB.getInstance().loadLocalPlayer(1);
        }
        final FileInputStream in = new FileInputStream(fileInCurrentSave);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        synchronized (SliceY.SliceBufferLock) {
            SliceY.SliceBuffer.clear();
            SliceY.SliceBuffer.limit(bufferedInputStream.read(SliceY.SliceBuffer.array()));
            final byte value = SliceY.SliceBuffer.get();
            final byte value2 = SliceY.SliceBuffer.get();
            final byte value3 = SliceY.SliceBuffer.get();
            final byte value4 = SliceY.SliceBuffer.get();
            if (value == 80 && value2 == 76 && value3 == 89 && value4 == 82) {
                int1 = SliceY.SliceBuffer.getInt();
            }
            else {
                SliceY.SliceBuffer.rewind();
            }
            if (int1 >= 69) {
                String s = GameWindow.ReadString(SliceY.SliceBuffer);
                if (GameClient.bClient && int1 < 71) {
                    s = ServerOptions.instance.ServerPlayerID.getValue();
                }
                if (GameClient.bClient && !IsoPlayer.isServerPlayerIDValid(s)) {
                    GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_ServerPlayerIDMismatch");
                    GameLoadingState.playerWrongIP = true;
                    return false;
                }
            }
            IsoCell.instance.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX = SliceY.SliceBuffer.getInt() + IsoWorld.saveoffsetx * 30;
            IsoCell.instance.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY = SliceY.SliceBuffer.getInt() + IsoWorld.saveoffsety * 30;
            SliceY.SliceBuffer.getInt();
            SliceY.SliceBuffer.getInt();
            SliceY.SliceBuffer.getInt();
            if (IsoPlayer.getInstance() == null) {
                IsoPlayer.setInstance(new IsoPlayer(IsoCell.instance));
                IsoPlayer.players[0] = IsoPlayer.getInstance();
            }
            IsoPlayer.getInstance().load(SliceY.SliceBuffer, int1);
            in.close();
        }
        PlayerDB.getInstance().saveLocalPlayersForce();
        fileInCurrentSave.delete();
        PlayerDB.getInstance().uploadLocalPlayers2DB();
        return true;
    }
    
    public IsoGridSquare getRelativeGridSquare(int n, int n2, final int n3) {
        final int worldXMin = this.ChunkMap[0].getWorldXMin();
        final IsoChunkMap isoChunkMap = this.ChunkMap[0];
        final int n4 = worldXMin * 10;
        final int worldYMin = this.ChunkMap[0].getWorldYMin();
        final IsoChunkMap isoChunkMap2 = this.ChunkMap[0];
        final int n5 = worldYMin * 10;
        n += n4;
        n2 += n5;
        return this.getGridSquare(n, n2, n3);
    }
    
    public IsoGridSquare createNewGridSquare(final int n, final int n2, final int n3, final boolean b) {
        if (!IsoWorld.instance.isValidSquare(n, n2, n3)) {
            return null;
        }
        IsoGridSquare isoGridSquare = this.getGridSquare(n, n2, n3);
        if (isoGridSquare != null) {
            return isoGridSquare;
        }
        if (GameServer.bServer) {
            if (ServerMap.instance.getChunk(n / 10, n2 / 10) != null) {
                isoGridSquare = IsoGridSquare.getNew(this, null, n, n2, n3);
                ServerMap.instance.setGridSquare(n, n2, n3, isoGridSquare);
            }
        }
        else if (this.getChunkForGridSquare(n, n2, n3) != null) {
            isoGridSquare = IsoGridSquare.getNew(this, null, n, n2, n3);
            this.ConnectNewSquare(isoGridSquare, true);
        }
        if (isoGridSquare != null && b) {
            isoGridSquare.RecalcAllWithNeighbours(true);
        }
        return isoGridSquare;
    }
    
    public IsoGridSquare getGridSquareDirect(final int n, final int n2, final int n3, final int n4) {
        final int chunkWidthInTiles = IsoChunkMap.ChunkWidthInTiles;
        return this.gridSquares[n4][n + n2 * chunkWidthInTiles + n3 * chunkWidthInTiles * chunkWidthInTiles];
    }
    
    public boolean isInChunkMap(final int n, final int n2) {
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final int worldXMinTiles = this.ChunkMap[i].getWorldXMinTiles();
            final int worldXMaxTiles = this.ChunkMap[i].getWorldXMaxTiles();
            final int worldYMinTiles = this.ChunkMap[i].getWorldYMinTiles();
            final int worldYMaxTiles = this.ChunkMap[i].getWorldYMaxTiles();
            if (n >= worldXMinTiles && n < worldXMaxTiles && n2 >= worldYMinTiles && n2 < worldYMaxTiles) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<IsoObject> getProcessIsoObjectRemove() {
        return this.ProcessIsoObjectRemove;
    }
    
    public void checkHaveRoof(final int n, final int n2) {
        boolean haveRoof = false;
        for (int i = 8; i >= 0; --i) {
            final IsoGridSquare gridSquare = this.getGridSquare(n, n2, i);
            if (gridSquare != null) {
                if (haveRoof != gridSquare.haveRoof) {
                    gridSquare.haveRoof = haveRoof;
                    gridSquare.RecalcAllWithNeighbours(true);
                }
                if (gridSquare.Is(IsoFlagType.solidfloor)) {
                    haveRoof = true;
                }
            }
        }
    }
    
    public IsoZombie getFakeZombieForHit() {
        if (this.fakeZombieForHit == null) {
            this.fakeZombieForHit = new IsoZombie(this);
        }
        return this.fakeZombieForHit;
    }
    
    public void addHeatSource(final IsoHeatSource isoHeatSource) {
        if (GameServer.bServer) {
            return;
        }
        if (this.heatSources.contains(isoHeatSource)) {
            DebugLog.log("ERROR addHeatSource called again with the same HeatSource");
            return;
        }
        this.heatSources.add(isoHeatSource);
    }
    
    public void removeHeatSource(final IsoHeatSource o) {
        if (GameServer.bServer) {
            return;
        }
        this.heatSources.remove(o);
    }
    
    public void updateHeatSources() {
        if (GameServer.bServer) {
            return;
        }
        for (int i = this.heatSources.size() - 1; i >= 0; --i) {
            if (!this.heatSources.get(i).isInBounds()) {
                this.heatSources.remove(i);
            }
        }
    }
    
    public int getHeatSourceTemperature(final int n, final int n2, final int n3) {
        int n4 = 0;
        for (int i = 0; i < this.heatSources.size(); ++i) {
            final IsoHeatSource isoHeatSource = this.heatSources.get(i);
            if (isoHeatSource.getZ() == n3) {
                final float distanceToSquared = IsoUtils.DistanceToSquared((float)n, (float)n2, (float)isoHeatSource.getX(), (float)isoHeatSource.getY());
                if (distanceToSquared < isoHeatSource.getRadius() * isoHeatSource.getRadius()) {
                    final LosUtil.TestResults lineClear = LosUtil.lineClear(this, isoHeatSource.getX(), isoHeatSource.getY(), isoHeatSource.getZ(), n, n2, n3, false);
                    if (lineClear == LosUtil.TestResults.Clear || lineClear == LosUtil.TestResults.ClearThroughOpenDoor) {
                        n4 += (int)(isoHeatSource.getTemperature() * (1.0 - Math.sqrt(distanceToSquared) / isoHeatSource.getRadius()));
                    }
                }
            }
        }
        return n4;
    }
    
    public float getHeatSourceHighestTemperature(final float n, final int n2, final int n3, final int n4) {
        float n5 = n;
        for (int i = 0; i < this.heatSources.size(); ++i) {
            final IsoHeatSource isoHeatSource = this.heatSources.get(i);
            if (isoHeatSource.getZ() == n4) {
                final float distanceToSquared = IsoUtils.DistanceToSquared((float)n2, (float)n3, (float)isoHeatSource.getX(), (float)isoHeatSource.getY());
                final IsoGridSquare gridSquare = this.getGridSquare(isoHeatSource.getX(), isoHeatSource.getY(), isoHeatSource.getZ());
                float n6 = 0.0f;
                if (gridSquare != null) {
                    if (!gridSquare.isInARoom()) {
                        n6 = n - 30.0f;
                        if (n6 < -15.0f) {
                            n6 = -15.0f;
                        }
                        else if (n6 > 5.0f) {
                            n6 = 5.0f;
                        }
                    }
                    else {
                        n6 = n - 30.0f;
                        if (n6 < -7.0f) {
                            n6 = -7.0f;
                        }
                        else if (n6 > 7.0f) {
                            n6 = 7.0f;
                        }
                    }
                }
                final float lerp = ClimateManager.lerp((float)(1.0 - Math.sqrt(distanceToSquared) / isoHeatSource.getRadius()), n, isoHeatSource.getTemperature() + n6);
                if (lerp > n5) {
                    if (distanceToSquared < isoHeatSource.getRadius() * isoHeatSource.getRadius()) {
                        final LosUtil.TestResults lineClear = LosUtil.lineClear(this, isoHeatSource.getX(), isoHeatSource.getY(), isoHeatSource.getZ(), n2, n3, n4, false);
                        if (lineClear == LosUtil.TestResults.Clear || lineClear == LosUtil.TestResults.ClearThroughOpenDoor) {
                            n5 = lerp;
                        }
                    }
                }
            }
        }
        return n5;
    }
    
    public void putInVehicle(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter == null || isoGameCharacter.savedVehicleSeat == -1) {
            return;
        }
        final int n = ((int)isoGameCharacter.getX() - 4) / 10;
        final int n2 = ((int)isoGameCharacter.getY() - 4) / 10;
        final int n3 = ((int)isoGameCharacter.getX() + 4) / 10;
        for (int n4 = ((int)isoGameCharacter.getY() + 4) / 10, i = n2; i <= n4; ++i) {
            for (int j = n; j <= n3; ++j) {
                final IsoChunk chunkForGridSquare = this.getChunkForGridSquare(j * 10, i * 10, (int)isoGameCharacter.getZ());
                if (chunkForGridSquare != null) {
                    for (int k = 0; k < chunkForGridSquare.vehicles.size(); ++k) {
                        final BaseVehicle baseVehicle = chunkForGridSquare.vehicles.get(k);
                        if ((int)baseVehicle.getZ() == (int)isoGameCharacter.getZ()) {
                            if (IsoUtils.DistanceToSquared(baseVehicle.getX(), baseVehicle.getY(), isoGameCharacter.savedVehicleX, isoGameCharacter.savedVehicleY) < 0.010000001f) {
                                if (baseVehicle.VehicleID == -1) {
                                    return;
                                }
                                final VehicleScript.Position passengerPosition = baseVehicle.getPassengerPosition(isoGameCharacter.savedVehicleSeat, "inside");
                                if (passengerPosition != null && !baseVehicle.isSeatOccupied(isoGameCharacter.savedVehicleSeat)) {
                                    baseVehicle.enter(isoGameCharacter.savedVehicleSeat, isoGameCharacter, passengerPosition.offset);
                                    LuaEventManager.triggerEvent("OnEnterVehicle", isoGameCharacter);
                                    if (baseVehicle.getCharacter(isoGameCharacter.savedVehicleSeat) == isoGameCharacter && isoGameCharacter.savedVehicleRunning) {
                                        baseVehicle.resumeRunningAfterLoad();
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Deprecated
    public void resumeVehicleSounds(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter == null || isoGameCharacter.savedVehicleSeat == -1) {
            return;
        }
        final int n = ((int)isoGameCharacter.getX() - 4) / 10;
        final int n2 = ((int)isoGameCharacter.getY() - 4) / 10;
        final int n3 = ((int)isoGameCharacter.getX() + 4) / 10;
        for (int n4 = ((int)isoGameCharacter.getY() + 4) / 10, i = n2; i <= n4; ++i) {
            for (int j = n; j <= n3; ++j) {
                final IsoChunk chunkForGridSquare = this.getChunkForGridSquare(j * 10, i * 10, (int)isoGameCharacter.getZ());
                if (chunkForGridSquare != null) {
                    for (int k = 0; k < chunkForGridSquare.vehicles.size(); ++k) {
                        final BaseVehicle baseVehicle = chunkForGridSquare.vehicles.get(k);
                        if (baseVehicle.lightbarSirenMode.isEnable()) {
                            baseVehicle.setLightbarSirenMode(baseVehicle.lightbarSirenMode.get());
                        }
                    }
                }
            }
        }
    }
    
    private void AddUniqueToBuildingList(final ArrayList<IsoBuilding> list, final IsoBuilding e) {
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) == e) {
                return;
            }
        }
        list.add(e);
    }
    
    public IsoSpriteManager getSpriteManager() {
        return IsoSpriteManager.instance;
    }
    
    static {
        IsoCell.MaxHeight = 8;
        stchoices = new ArrayList<IsoGridSquare>();
        IsoCell.buildingscores = new Stack<BuildingScore>();
        IsoCell.GridStack = null;
        ShadowSquares = new ArrayList<IsoGridSquare>(1000);
        MinusFloorCharacters = new ArrayList<IsoGridSquare>(1000);
        SolidFloor = new ArrayList<IsoGridSquare>(5000);
        ShadedFloor = new ArrayList<IsoGridSquare>(5000);
        VegetationCorpses = new ArrayList<IsoGridSquare>(5000);
        perPlayerRender = new PerPlayerRender[4];
    }
    
    public static final class PerPlayerRender
    {
        public final IsoGridStack GridStacks;
        public boolean[][][] VisiOccludedFlags;
        public boolean[][] VisiCulledFlags;
        public short[][][] StencilValues;
        public boolean[][] FlattenGrassEtc;
        public int minX;
        public int minY;
        public int maxX;
        public int maxY;
        
        public PerPlayerRender() {
            this.GridStacks = new IsoGridStack(9);
        }
        
        public void setSize(final int n, final int n2) {
            if (this.VisiOccludedFlags == null || this.VisiOccludedFlags.length < n || this.VisiOccludedFlags[0].length < n2) {
                this.VisiOccludedFlags = new boolean[n][n2][2];
                this.VisiCulledFlags = new boolean[n][n2];
                this.StencilValues = new short[n][n2][2];
                this.FlattenGrassEtc = new boolean[n][n2];
            }
        }
    }
    
    protected class SnowGridTiles
    {
        protected byte ID;
        private int counter;
        private final ArrayList<Texture> textures;
        
        public SnowGridTiles(final byte id) {
            this.ID = -1;
            this.counter = -1;
            this.textures = new ArrayList<Texture>();
            this.ID = id;
        }
        
        protected void add(final Texture e) {
            this.textures.add(e);
        }
        
        protected Texture getNext() {
            ++this.counter;
            if (this.counter >= this.textures.size()) {
                this.counter = 0;
            }
            return this.textures.get(this.counter);
        }
        
        protected Texture get(final int index) {
            return this.textures.get(index);
        }
        
        protected int size() {
            return this.textures.size();
        }
        
        protected Texture getRand() {
            return this.textures.get(Rand.Next(4));
        }
        
        protected boolean contains(final Texture o) {
            return this.textures.contains(o);
        }
        
        protected void resetCounter() {
            this.counter = 0;
        }
    }
    
    private class SnowGrid
    {
        public int w;
        public int h;
        public int frac;
        public static final int N = 0;
        public static final int S = 1;
        public static final int W = 2;
        public static final int E = 3;
        public static final int A = 0;
        public static final int B = 1;
        public final Texture[][][] grid;
        public final byte[][][] gridType;
        
        public SnowGrid(final int n) {
            this.w = 256;
            this.h = 256;
            this.frac = 0;
            this.grid = new Texture[this.w][this.h][2];
            this.gridType = new byte[this.w][this.h][2];
            this.init(n);
        }
        
        public SnowGrid init(final int frac) {
            if (!IsoCell.this.hasSetupSnowGrid) {
                (IsoCell.this.snowNoise2D = new Noise2D()).addLayer(16, 0.5f, 3.0f);
                IsoCell.this.snowNoise2D.addLayer(32, 2.0f, 5.0f);
                IsoCell.this.snowNoise2D.addLayer(64, 5.0f, 8.0f);
                final byte b = 0;
                final IsoCell this$0 = IsoCell.this;
                final IsoCell this$2 = IsoCell.this;
                final byte b2 = b;
                final byte b3 = (byte)(b + 1);
                this$0.snowGridTiles_Square = this$2.new SnowGridTiles(b2);
                final int n = 40;
                for (int i = 0; i < 4; ++i) {
                    IsoCell.this.snowGridTiles_Square.add(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n + i)));
                }
                final IsoCell this$3 = IsoCell.this;
                final IsoCell this$4 = IsoCell.this;
                final byte b4 = b3;
                byte snowFirstNonSquare = (byte)(b3 + 1);
                this$3.snowGridTiles_Enclosed = this$4.new SnowGridTiles(b4);
                int n2 = 0;
                for (int j = 0; j < 4; ++j) {
                    IsoCell.this.snowGridTiles_Enclosed.add(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2 + j)));
                }
                IsoCell.this.snowGridTiles_Cove = new SnowGridTiles[4];
                for (int k = 0; k < 4; ++k) {
                    final SnowGridTiles[] snowGridTiles_Cove = IsoCell.this.snowGridTiles_Cove;
                    final int n3 = k;
                    final IsoCell this$5 = IsoCell.this;
                    final byte b5 = snowFirstNonSquare;
                    ++snowFirstNonSquare;
                    snowGridTiles_Cove[n3] = this$5.new SnowGridTiles(b5);
                    if (k == 0) {
                        n2 = 7;
                    }
                    if (k == 2) {
                        n2 = 4;
                    }
                    if (k == 1) {
                        n2 = 5;
                    }
                    if (k == 3) {
                        n2 = 6;
                    }
                    for (int l = 0; l < 3; ++l) {
                        IsoCell.this.snowGridTiles_Cove[k].add(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2 + l * 4)));
                    }
                }
                IsoCell.this.m_snowFirstNonSquare = snowFirstNonSquare;
                IsoCell.this.snowGridTiles_Edge = new SnowGridTiles[4];
                for (int n4 = 0; n4 < 4; ++n4) {
                    final SnowGridTiles[] snowGridTiles_Edge = IsoCell.this.snowGridTiles_Edge;
                    final int n5 = n4;
                    final IsoCell this$6 = IsoCell.this;
                    final byte b6 = snowFirstNonSquare;
                    ++snowFirstNonSquare;
                    snowGridTiles_Edge[n5] = this$6.new SnowGridTiles(b6);
                    if (n4 == 0) {
                        n2 = 16;
                    }
                    if (n4 == 2) {
                        n2 = 18;
                    }
                    if (n4 == 1) {
                        n2 = 17;
                    }
                    if (n4 == 3) {
                        n2 = 19;
                    }
                    for (int n6 = 0; n6 < 3; ++n6) {
                        IsoCell.this.snowGridTiles_Edge[n4].add(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2 + n6 * 4)));
                    }
                }
                IsoCell.this.snowGridTiles_Strip = new SnowGridTiles[4];
                for (int n7 = 0; n7 < 4; ++n7) {
                    final SnowGridTiles[] snowGridTiles_Strip = IsoCell.this.snowGridTiles_Strip;
                    final int n8 = n7;
                    final IsoCell this$7 = IsoCell.this;
                    final byte b7 = snowFirstNonSquare;
                    ++snowFirstNonSquare;
                    snowGridTiles_Strip[n8] = this$7.new SnowGridTiles(b7);
                    if (n7 == 0) {
                        n2 = 28;
                    }
                    if (n7 == 2) {
                        n2 = 29;
                    }
                    if (n7 == 1) {
                        n2 = 31;
                    }
                    if (n7 == 3) {
                        n2 = 30;
                    }
                    for (int n9 = 0; n9 < 3; ++n9) {
                        IsoCell.this.snowGridTiles_Strip[n7].add(Texture.getSharedTexture(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n2 + n9 * 4)));
                    }
                }
                IsoCell.this.hasSetupSnowGrid = true;
            }
            IsoCell.this.snowGridTiles_Square.resetCounter();
            IsoCell.this.snowGridTiles_Enclosed.resetCounter();
            for (int n10 = 0; n10 < 4; ++n10) {
                IsoCell.this.snowGridTiles_Cove[n10].resetCounter();
                IsoCell.this.snowGridTiles_Edge[n10].resetCounter();
                IsoCell.this.snowGridTiles_Strip[n10].resetCounter();
            }
            this.frac = frac;
            final Noise2D snowNoise2D = IsoCell.this.snowNoise2D;
            for (int n11 = 0; n11 < this.h; ++n11) {
                for (int n12 = 0; n12 < this.w; ++n12) {
                    for (int n13 = 0; n13 < 2; ++n13) {
                        this.grid[n12][n11][n13] = null;
                        this.gridType[n12][n11][n13] = -1;
                    }
                    if (snowNoise2D.layeredNoise(n12 / 10.0f, n11 / 10.0f) <= frac / 100.0f) {
                        this.grid[n12][n11][0] = IsoCell.this.snowGridTiles_Square.getNext();
                        this.gridType[n12][n11][0] = IsoCell.this.snowGridTiles_Square.ID;
                    }
                }
            }
            for (int n14 = 0; n14 < this.h; ++n14) {
                for (int n15 = 0; n15 < this.w; ++n15) {
                    if (this.grid[n15][n14][0] == null) {
                        final boolean check = this.check(n15, n14 - 1);
                        final boolean check2 = this.check(n15, n14 + 1);
                        final boolean check3 = this.check(n15 - 1, n14);
                        final boolean check4 = this.check(n15 + 1, n14);
                        int n16 = 0;
                        if (check) {
                            ++n16;
                        }
                        if (check2) {
                            ++n16;
                        }
                        if (check4) {
                            ++n16;
                        }
                        if (check3) {
                            ++n16;
                        }
                        if (n16 != 0) {
                            if (n16 == 1) {
                                if (check) {
                                    this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Strip[0]);
                                }
                                else if (check2) {
                                    this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Strip[1]);
                                }
                                else if (check4) {
                                    this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Strip[3]);
                                }
                                else if (check3) {
                                    this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Strip[2]);
                                }
                            }
                            else if (n16 == 2) {
                                if (check && check2) {
                                    this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Strip[0]);
                                    this.set(n15, n14, 1, IsoCell.this.snowGridTiles_Strip[1]);
                                }
                                else if (check4 && check3) {
                                    this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Strip[2]);
                                    this.set(n15, n14, 1, IsoCell.this.snowGridTiles_Strip[3]);
                                }
                                else if (check) {
                                    this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Edge[check3 ? 0 : 3]);
                                }
                                else if (check2) {
                                    this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Edge[check3 ? 2 : 1]);
                                }
                                else if (check3) {
                                    this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Edge[check ? 0 : 2]);
                                }
                                else if (check4) {
                                    this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Edge[check ? 3 : 1]);
                                }
                            }
                            else if (n16 == 3) {
                                if (!check) {
                                    this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Cove[1]);
                                }
                                else if (!check2) {
                                    this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Cove[0]);
                                }
                                else if (!check4) {
                                    this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Cove[2]);
                                }
                                else if (!check3) {
                                    this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Cove[3]);
                                }
                            }
                            else if (n16 == 4) {
                                this.set(n15, n14, 0, IsoCell.this.snowGridTiles_Enclosed);
                            }
                        }
                    }
                }
            }
            return this;
        }
        
        public boolean check(int n, int n2) {
            if (n == this.w) {
                n = 0;
            }
            if (n == -1) {
                n = this.w - 1;
            }
            if (n2 == this.h) {
                n2 = 0;
            }
            if (n2 == -1) {
                n2 = this.h - 1;
            }
            return n >= 0 && n < this.w && n2 >= 0 && n2 < this.h && IsoCell.this.snowGridTiles_Square.contains(this.grid[n][n2][0]);
        }
        
        public boolean checkAny(int n, int n2) {
            if (n == this.w) {
                n = 0;
            }
            if (n == -1) {
                n = this.w - 1;
            }
            if (n2 == this.h) {
                n2 = 0;
            }
            if (n2 == -1) {
                n2 = this.h - 1;
            }
            return n >= 0 && n < this.w && n2 >= 0 && n2 < this.h && this.grid[n][n2][0] != null;
        }
        
        public void set(int n, int n2, final int n3, final SnowGridTiles snowGridTiles) {
            if (n == this.w) {
                n = 0;
            }
            if (n == -1) {
                n = this.w - 1;
            }
            if (n2 == this.h) {
                n2 = 0;
            }
            if (n2 == -1) {
                n2 = this.h - 1;
            }
            if (n < 0 || n >= this.w) {
                return;
            }
            if (n2 < 0 || n2 >= this.h) {
                return;
            }
            this.grid[n][n2][n3] = snowGridTiles.getNext();
            this.gridType[n][n2][n3] = snowGridTiles.ID;
        }
        
        public void subtract(final SnowGrid snowGrid) {
            for (int i = 0; i < this.h; ++i) {
                for (int j = 0; j < this.w; ++j) {
                    for (int k = 0; k < 2; ++k) {
                        if (snowGrid.gridType[j][i][k] == this.gridType[j][i][k]) {
                            this.grid[j][i][k] = null;
                            this.gridType[j][i][k] = -1;
                        }
                    }
                }
            }
        }
    }
    
    public enum BuildingSearchCriteria
    {
        Food, 
        Defense, 
        Wood, 
        Weapons, 
        General;
        
        private static /* synthetic */ BuildingSearchCriteria[] $values() {
            return new BuildingSearchCriteria[] { BuildingSearchCriteria.Food, BuildingSearchCriteria.Defense, BuildingSearchCriteria.Wood, BuildingSearchCriteria.Weapons, BuildingSearchCriteria.General };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    private static class s_performance
    {
        static final PerformanceProfileProbe isoCellUpdate;
        static final PerformanceProfileProbe isoCellRender;
        static final PerformanceProfileProbe isoCellRenderTiles;
        static final PerformanceProfileProbe isoCellDoBuilding;
        
        static {
            isoCellUpdate = new PerformanceProfileProbe("IsoCell.update");
            isoCellRender = new PerformanceProfileProbe("IsoCell.render");
            isoCellRenderTiles = new PerformanceProfileProbe("IsoCell.renderTiles");
            isoCellDoBuilding = new PerformanceProfileProbe("IsoCell.doBuilding");
        }
        
        static class renderTiles
        {
            static final PerformanceProfileProbe performRenderTiles;
            static final PerformanceProfileProbe recalculateAnyGridStacks;
            static final PerformanceProfileProbe flattenAnyFoliage;
            static final PerformanceProfileProbe renderDebugPhysics;
            static final PerformanceProfileProbe renderDebugLighting;
            static PerformanceProfileProbeList<PperformRenderTilesLayer> performRenderTilesLayers;
            
            static {
                performRenderTiles = new PerformanceProfileProbe("performRenderTiles");
                recalculateAnyGridStacks = new PerformanceProfileProbe("recalculateAnyGridStacks");
                flattenAnyFoliage = new PerformanceProfileProbe("flattenAnyFoliage");
                renderDebugPhysics = new PerformanceProfileProbe("renderDebugPhysics");
                renderDebugLighting = new PerformanceProfileProbe("renderDebugLighting");
                renderTiles.performRenderTilesLayers = PerformanceProfileProbeList.construct("performRenderTiles", 8, PperformRenderTilesLayer.class, PperformRenderTilesLayer::new);
            }
            
            static class PperformRenderTilesLayer extends PerformanceProfileProbe
            {
                final PerformanceProfileProbe renderIsoWater;
                final PerformanceProfileProbe renderFloor;
                final PerformanceProfileProbe renderPuddles;
                final PerformanceProfileProbe renderShore;
                final PerformanceProfileProbe renderSnow;
                final PerformanceProfileProbe renderBlood;
                final PerformanceProfileProbe vegetationCorpses;
                final PerformanceProfileProbe renderFloorShading;
                final PerformanceProfileProbe renderShadows;
                final PerformanceProfileProbe luaOnPostFloorLayerDraw;
                final PerformanceProfileProbe minusFloorCharacters;
                
                PperformRenderTilesLayer(final String s) {
                    super(s);
                    this.renderIsoWater = new PerformanceProfileProbe("renderIsoWater");
                    this.renderFloor = new PerformanceProfileProbe("renderFloor");
                    this.renderPuddles = new PerformanceProfileProbe("renderPuddles");
                    this.renderShore = new PerformanceProfileProbe("renderShore");
                    this.renderSnow = new PerformanceProfileProbe("renderSnow");
                    this.renderBlood = new PerformanceProfileProbe("renderBlood");
                    this.vegetationCorpses = new PerformanceProfileProbe("vegetationCorpses");
                    this.renderFloorShading = new PerformanceProfileProbe("renderFloorShading");
                    this.renderShadows = new PerformanceProfileProbe("renderShadows");
                    this.luaOnPostFloorLayerDraw = new PerformanceProfileProbe("luaOnPostFloorLayerDraw");
                    this.minusFloorCharacters = new PerformanceProfileProbe("minusFloorCharacters");
                }
            }
        }
    }
}
