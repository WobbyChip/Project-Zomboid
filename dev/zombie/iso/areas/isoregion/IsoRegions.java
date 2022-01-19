// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion;

import java.util.HashMap;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoChunk;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import zombie.iso.IsoWorld;
import zombie.iso.areas.isoregion.regions.IChunkRegion;
import zombie.iso.areas.isoregion.data.DataChunk;
import zombie.iso.areas.isoregion.regions.IWorldRegion;
import zombie.core.Colors;
import zombie.iso.IsoChunkMap;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import java.nio.ByteBuffer;
import zombie.core.Color;
import zombie.ZomboidFileSystem;
import zombie.iso.areas.isoregion.data.DataSquarePos;
import zombie.core.Core;
import zombie.iso.areas.isoregion.data.DataRoot;
import java.util.Map;
import java.io.File;

public final class IsoRegions
{
    public static final int SINGLE_CHUNK_PACKET_SIZE = 1024;
    public static final int CHUNKS_DATA_PACKET_SIZE = 65536;
    public static boolean PRINT_D;
    public static final int CELL_DIM = 300;
    public static final int CELL_CHUNK_DIM = 30;
    public static final int CHUNK_DIM = 10;
    public static final int CHUNK_MAX_Z = 8;
    public static final byte BIT_EMPTY = 0;
    public static final byte BIT_WALL_N = 1;
    public static final byte BIT_WALL_W = 2;
    public static final byte BIT_PATH_WALL_N = 4;
    public static final byte BIT_PATH_WALL_W = 8;
    public static final byte BIT_HAS_FLOOR = 16;
    public static final byte BIT_STAIRCASE = 32;
    public static final byte BIT_HAS_ROOF = 64;
    public static final byte DIR_NONE = -1;
    public static final byte DIR_N = 0;
    public static final byte DIR_W = 1;
    public static final byte DIR_2D_NW = 2;
    public static final byte DIR_S = 2;
    public static final byte DIR_E = 3;
    public static final byte DIR_2D_MAX = 4;
    public static final byte DIR_TOP = 4;
    public static final byte DIR_BOT = 5;
    public static final byte DIR_MAX = 6;
    protected static final int CHUNK_LOAD_DIMENSIONS = 7;
    protected static boolean DEBUG_LOAD_ALL_CHUNKS;
    public static final String FILE_PRE = "datachunk_";
    public static final String FILE_SEP = "_";
    public static final String FILE_EXT = ".bin";
    public static final String FILE_DIR = "isoregiondata";
    private static final int SQUARE_CHANGE_WARN_THRESHOLD = 20;
    private static int SQUARE_CHANGE_PER_TICK;
    private static String cacheDir;
    private static File cacheDirFile;
    private static File headDataFile;
    private static final Map<Integer, File> chunkFileNames;
    private static IsoRegionWorker regionWorker;
    private static DataRoot dataRoot;
    private static IsoRegionsLogger logger;
    protected static int lastChunkX;
    protected static int lastChunkY;
    private static byte previousFlags;
    
    public static File getHeaderFile() {
        return IsoRegions.headDataFile;
    }
    
    public static File getDirectory() {
        return IsoRegions.cacheDirFile;
    }
    
    public static File getChunkFile(final int n, final int n2) {
        final int hash = hash(n, n2);
        if (IsoRegions.chunkFileNames.containsKey(hash) && IsoRegions.chunkFileNames.get(hash) != null) {
            return IsoRegions.chunkFileNames.get(hash);
        }
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, IsoRegions.cacheDir, n, n2));
        IsoRegions.chunkFileNames.put(hash, file);
        return file;
    }
    
    public static byte GetOppositeDir(final byte b) {
        if (b == 0) {
            return 2;
        }
        if (b == 1) {
            return 3;
        }
        if (b == 2) {
            return 0;
        }
        if (b == 3) {
            return 1;
        }
        if (b == 4) {
            return 5;
        }
        if (b == 5) {
            return 4;
        }
        return -1;
    }
    
    public static void setDebugLoadAllChunks(final boolean debug_LOAD_ALL_CHUNKS) {
        IsoRegions.DEBUG_LOAD_ALL_CHUNKS = debug_LOAD_ALL_CHUNKS;
    }
    
    public static boolean isDebugLoadAllChunks() {
        return IsoRegions.DEBUG_LOAD_ALL_CHUNKS;
    }
    
    public static int hash(final int n, final int n2) {
        return n2 << 16 ^ n;
    }
    
    protected static DataRoot getDataRoot() {
        return IsoRegions.dataRoot;
    }
    
    public static void init() {
        if (!Core.bDebug) {
            IsoRegions.PRINT_D = false;
            DataSquarePos.DEBUG_POOL = false;
        }
        IsoRegions.logger = new IsoRegionsLogger(IsoRegions.PRINT_D);
        IsoRegions.chunkFileNames.clear();
        IsoRegions.cacheDir = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getFileNameInCurrentSave("isoregiondata"), File.separator);
        IsoRegions.cacheDirFile = new File(IsoRegions.cacheDir);
        if (!IsoRegions.cacheDirFile.exists()) {
            IsoRegions.cacheDirFile.mkdir();
        }
        IsoRegions.headDataFile = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, IsoRegions.cacheDir));
        IsoRegions.previousFlags = 0;
        IsoRegions.dataRoot = new DataRoot();
        (IsoRegions.regionWorker = new IsoRegionWorker()).create();
        IsoRegions.regionWorker.load();
    }
    
    public static IsoRegionsLogger getLogger() {
        return IsoRegions.logger;
    }
    
    public static void log(final String s) {
        IsoRegions.logger.log(s);
    }
    
    public static void log(final String s, final Color color) {
        IsoRegions.logger.log(s, color);
    }
    
    public static void warn(final String s) {
        IsoRegions.logger.warn(s);
    }
    
    public static void reset() {
        IsoRegions.previousFlags = 0;
        IsoRegions.regionWorker.stop();
        IsoRegions.regionWorker = null;
        IsoRegions.dataRoot = null;
        IsoRegions.chunkFileNames.clear();
    }
    
    public static void receiveServerUpdatePacket(final ByteBuffer byteBuffer) {
        if (IsoRegions.regionWorker == null) {
            IsoRegions.logger.warn("IsoRegion cannot receive server packet, regionWorker == null.");
            return;
        }
        if (GameClient.bClient) {
            IsoRegions.regionWorker.readServerUpdatePacket(byteBuffer);
        }
    }
    
    public static void receiveClientRequestFullDataChunks(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        if (IsoRegions.regionWorker == null) {
            IsoRegions.logger.warn("IsoRegion cannot receive client packet, regionWorker == null.");
            return;
        }
        if (GameServer.bServer) {
            IsoRegions.regionWorker.readClientRequestFullUpdatePacket(byteBuffer, udpConnection);
        }
    }
    
    public static void update() {
        if (Core.bDebug && IsoRegions.SQUARE_CHANGE_PER_TICK > 20) {
            IsoRegions.logger.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, IsoRegions.SQUARE_CHANGE_PER_TICK));
        }
        IsoRegions.SQUARE_CHANGE_PER_TICK = 0;
        if (IsoRegionWorker.isRequestingBufferSwap.get()) {
            IsoRegions.logger.log("IsoRegion Swapping DataRoot");
            final DataRoot dataRoot = IsoRegions.dataRoot;
            IsoRegions.dataRoot = IsoRegions.regionWorker.getRootBuffer();
            IsoRegions.regionWorker.setRootBuffer(dataRoot);
            IsoRegionWorker.isRequestingBufferSwap.set(false);
            if (!GameServer.bServer) {
                clientResetCachedRegionReferences();
            }
        }
        if (!GameClient.bClient && !GameServer.bServer && IsoRegions.DEBUG_LOAD_ALL_CHUNKS && Core.bDebug) {
            final int lastChunkX = (int)IsoPlayer.getInstance().getX() / 10;
            final int lastChunkY = (int)IsoPlayer.getInstance().getY() / 10;
            if (IsoRegions.lastChunkX != lastChunkX || IsoRegions.lastChunkY != lastChunkY) {
                IsoRegions.lastChunkX = lastChunkX;
                IsoRegions.lastChunkY = lastChunkY;
                IsoRegions.regionWorker.readSurroundingChunks(lastChunkX, lastChunkY, IsoChunkMap.ChunkGridWidth - 2, true);
            }
        }
        IsoRegions.regionWorker.update();
        IsoRegions.logger.update();
    }
    
    protected static void forceRecalcSurroundingChunks() {
        if (!Core.bDebug || GameClient.bClient) {
            return;
        }
        IsoRegions.logger.log("[DEBUG] Forcing a full load/recalculate of chunks surrounding player.", Colors.Gold);
        IsoRegions.regionWorker.readSurroundingChunks((int)IsoPlayer.getInstance().getX() / 10, (int)IsoPlayer.getInstance().getY() / 10, IsoChunkMap.ChunkGridWidth - 2, true, true);
    }
    
    public static byte getSquareFlags(final int n, final int n2, final int n3) {
        return IsoRegions.dataRoot.getSquareFlags(n, n2, n3);
    }
    
    public static IWorldRegion getIsoWorldRegion(final int n, final int n2, final int n3) {
        return IsoRegions.dataRoot.getIsoWorldRegion(n, n2, n3);
    }
    
    public static DataChunk getDataChunk(final int n, final int n2) {
        return IsoRegions.dataRoot.getDataChunk(n, n2);
    }
    
    public static IChunkRegion getChunkRegion(final int n, final int n2, final int n3) {
        return IsoRegions.dataRoot.getIsoChunkRegion(n, n2, n3);
    }
    
    public static void ResetAllDataDebug() {
        if (!Core.bDebug) {
            return;
        }
        if (GameServer.bServer || GameClient.bClient) {
            return;
        }
        IsoRegions.regionWorker.addDebugResetJob();
    }
    
    private static void clientResetCachedRegionReferences() {
        if (GameServer.bServer) {
            return;
        }
        final int n = 0;
        final int n2 = 0;
        final int chunkGridWidth = IsoChunkMap.ChunkGridWidth;
        final int chunkGridWidth2 = IsoChunkMap.ChunkGridWidth;
        final IsoChunkMap chunkMap = IsoWorld.instance.getCell().getChunkMap(IsoPlayer.getPlayerIndex());
        if (chunkMap == null) {
            return;
        }
        for (int i = n; i < chunkGridWidth; ++i) {
            for (int j = n2; j < chunkGridWidth2; ++j) {
                final IsoChunk chunk = chunkMap.getChunk(i, j);
                if (chunk != null) {
                    for (int k = 0; k <= chunk.maxLevel; ++k) {
                        for (int l = 0; l < chunk.squares[0].length; ++l) {
                            final IsoGridSquare isoGridSquare = chunk.squares[k][l];
                            if (isoGridSquare != null) {
                                isoGridSquare.setIsoWorldRegion(null);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static void setPreviousFlags(final IsoGridSquare isoGridSquare) {
        IsoRegions.previousFlags = calculateSquareFlags(isoGridSquare);
    }
    
    public static void squareChanged(final IsoGridSquare isoGridSquare) {
        squareChanged(isoGridSquare, false);
    }
    
    public static void squareChanged(final IsoGridSquare isoGridSquare, final boolean b) {
        if (GameClient.bClient) {
            return;
        }
        if (isoGridSquare == null) {
            return;
        }
        final byte calculateSquareFlags = calculateSquareFlags(isoGridSquare);
        if (calculateSquareFlags == IsoRegions.previousFlags) {
            return;
        }
        IsoRegions.regionWorker.addSquareChangedJob(isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ(), b, calculateSquareFlags);
        ++IsoRegions.SQUARE_CHANGE_PER_TICK;
        IsoRegions.previousFlags = 0;
    }
    
    protected static byte calculateSquareFlags(final IsoGridSquare isoGridSquare) {
        int n = 0;
        if (isoGridSquare != null) {
            if (isoGridSquare.Is(IsoFlagType.solidfloor)) {
                n |= 0x10;
            }
            if (isoGridSquare.Is(IsoFlagType.cutN) || isoGridSquare.Has(IsoObjectType.doorFrN)) {
                n |= 0x1;
                if (isoGridSquare.Is(IsoFlagType.WindowN) || isoGridSquare.Is(IsoFlagType.windowN) || isoGridSquare.Is(IsoFlagType.DoorWallN)) {
                    n |= 0x4;
                }
            }
            if (!isoGridSquare.Is(IsoFlagType.WallSE) && (isoGridSquare.Is(IsoFlagType.cutW) || isoGridSquare.Has(IsoObjectType.doorFrW))) {
                n |= 0x2;
                if (isoGridSquare.Is(IsoFlagType.WindowW) || isoGridSquare.Is(IsoFlagType.windowW) || isoGridSquare.Is(IsoFlagType.DoorWallW)) {
                    n |= 0x8;
                }
            }
            if (isoGridSquare.HasStairsNorth() || isoGridSquare.HasStairsWest()) {
                n |= 0x20;
            }
        }
        return (byte)n;
    }
    
    protected static IsoRegionWorker getRegionWorker() {
        return IsoRegions.regionWorker;
    }
    
    static {
        IsoRegions.PRINT_D = false;
        IsoRegions.DEBUG_LOAD_ALL_CHUNKS = false;
        IsoRegions.SQUARE_CHANGE_PER_TICK = 0;
        chunkFileNames = new HashMap<Integer, File>();
        IsoRegions.lastChunkX = -1;
        IsoRegions.lastChunkY = -1;
        IsoRegions.previousFlags = 0;
    }
}
