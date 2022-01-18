// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.core.Color;
import zombie.core.PerformanceSettings;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.GameTime;
import java.io.IOException;
import zombie.network.GameServer;
import zombie.iso.areas.IsoRoom;
import zombie.characters.IsoGameCharacter;
import zombie.core.physics.WorldSimulation;
import zombie.ui.TextManager;
import zombie.debug.DebugLog;
import java.util.List;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleCache;
import zombie.vehicles.VehicleManager;
import zombie.network.GameClient;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.debug.DebugOptions;
import zombie.core.utils.UpdateLimit;
import java.util.ArrayList;
import zombie.core.textures.ColorInfo;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.HashMap;

public final class IsoChunkMap
{
    public static final int LEVELS = 8;
    public static final int ChunksPerWidth = 10;
    public static final HashMap<Integer, IsoChunk> SharedChunks;
    public static int MPWorldXA;
    public static int MPWorldYA;
    public static int MPWorldZA;
    public static int WorldXA;
    public static int WorldYA;
    public static int WorldZA;
    public static final int[] SWorldX;
    public static final int[] SWorldY;
    public static final ConcurrentLinkedQueue<IsoChunk> chunkStore;
    public static final ReentrantLock bSettingChunk;
    private static int StartChunkGridWidth;
    public static int ChunkGridWidth;
    public static int ChunkWidthInTiles;
    private static final ColorInfo inf;
    private static final ArrayList<IsoChunk> saveList;
    private static final ArrayList<ArrayList<IsoFloorBloodSplat>> splatByType;
    public int PlayerID;
    public boolean ignore;
    public int WorldX;
    public int WorldY;
    public final ArrayList<String> filenameServerRequests;
    protected IsoChunk[] chunksSwapB;
    protected IsoChunk[] chunksSwapA;
    boolean bReadBufferA;
    int XMinTiles;
    int YMinTiles;
    int XMaxTiles;
    int YMaxTiles;
    private IsoCell cell;
    private final UpdateLimit checkVehiclesFrequency;
    
    public IsoChunkMap(final IsoCell cell) {
        this.PlayerID = 0;
        this.ignore = false;
        this.WorldX = tileToChunk(IsoChunkMap.WorldXA);
        this.WorldY = tileToChunk(IsoChunkMap.WorldYA);
        this.filenameServerRequests = new ArrayList<String>();
        this.bReadBufferA = true;
        this.XMinTiles = -1;
        this.YMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMaxTiles = -1;
        this.checkVehiclesFrequency = new UpdateLimit(3000L);
        this.cell = cell;
        WorldReuserThread.instance.finished = false;
        this.chunksSwapB = new IsoChunk[IsoChunkMap.ChunkGridWidth * IsoChunkMap.ChunkGridWidth];
        this.chunksSwapA = new IsoChunk[IsoChunkMap.ChunkGridWidth * IsoChunkMap.ChunkGridWidth];
    }
    
    public static void CalcChunkWidth() {
        if (DebugOptions.instance.WorldChunkMap5x5.getValue()) {
            IsoChunkMap.ChunkGridWidth = 5;
            IsoChunkMap.ChunkWidthInTiles = IsoChunkMap.ChunkGridWidth * 10;
            return;
        }
        float n = Core.getInstance().getScreenWidth() / 1920.0f;
        if (n > 1.0f) {
            n = 1.0f;
        }
        IsoChunkMap.ChunkGridWidth = (int)(IsoChunkMap.StartChunkGridWidth * n * 1.5);
        if (IsoChunkMap.ChunkGridWidth / 2 * 2 == IsoChunkMap.ChunkGridWidth) {
            ++IsoChunkMap.ChunkGridWidth;
        }
        IsoChunkMap.ChunkWidthInTiles = IsoChunkMap.ChunkGridWidth * 10;
    }
    
    public static void setWorldStartPos(final int n, final int n2) {
        IsoChunkMap.SWorldX[IsoPlayer.getPlayerIndex()] = tileToChunk(n);
        IsoChunkMap.SWorldY[IsoPlayer.getPlayerIndex()] = tileToChunk(n2);
    }
    
    public void Dispose() {
        WorldReuserThread.instance.finished = true;
        IsoChunk.loadGridSquare.clear();
        this.chunksSwapA = null;
        this.chunksSwapB = null;
    }
    
    public void setInitialPos(final int worldX, final int worldY) {
        this.WorldX = worldX;
        this.WorldY = worldY;
        this.XMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMinTiles = -1;
        this.YMaxTiles = -1;
    }
    
    public void processAllLoadGridSquare() {
        for (IsoChunk isoChunk = IsoChunk.loadGridSquare.poll(); isoChunk != null; isoChunk = IsoChunk.loadGridSquare.poll()) {
            IsoChunkMap.bSettingChunk.lock();
            try {
                boolean b = false;
                for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                    final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[i];
                    if (!isoChunkMap.ignore && isoChunkMap.setChunkDirect(isoChunk, false)) {
                        b = true;
                    }
                }
                if (!b) {
                    WorldReuserThread.instance.addReuseChunk(isoChunk);
                }
                else {
                    isoChunk.doLoadGridsquare();
                }
            }
            finally {
                IsoChunkMap.bSettingChunk.unlock();
            }
        }
    }
    
    public void update() {
        int i = IsoChunk.loadGridSquare.size();
        if (i != 0) {
            i = 1 + i * 3 / IsoChunkMap.ChunkGridWidth;
        }
        while (i > 0) {
            final IsoChunk isoChunk = IsoChunk.loadGridSquare.poll();
            if (isoChunk != null) {
                boolean b = false;
                for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                    final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[j];
                    if (!isoChunkMap.ignore && isoChunkMap.setChunkDirect(isoChunk, false)) {
                        b = true;
                    }
                }
                if (!b) {
                    WorldReuserThread.instance.addReuseChunk(isoChunk);
                    --i;
                    continue;
                }
                isoChunk.bLoaded = true;
                IsoChunkMap.bSettingChunk.lock();
                try {
                    isoChunk.doLoadGridsquare();
                    if (GameClient.bClient) {
                        VehicleManager.instance.sendRequestGetFull(VehicleCache.vehicleGet(isoChunk.wx, isoChunk.wy));
                    }
                }
                finally {
                    IsoChunkMap.bSettingChunk.unlock();
                }
                for (int k = 0; k < IsoPlayer.numPlayers; ++k) {
                    final IsoPlayer isoPlayer = IsoPlayer.players[k];
                    if (isoPlayer != null) {
                        isoPlayer.dirtyRecalcGridStackTime = 20.0f;
                    }
                }
            }
            --i;
        }
        for (int l = 0; l < IsoChunkMap.ChunkGridWidth; ++l) {
            for (int n = 0; n < IsoChunkMap.ChunkGridWidth; ++n) {
                final IsoChunk chunk = this.getChunk(n, l);
                if (chunk != null) {
                    chunk.update();
                }
            }
        }
        if (this.checkVehiclesFrequency.Check() && GameClient.bClient) {
            this.checkVehicles();
        }
    }
    
    private void checkVehicles() {
        for (int i = 0; i < IsoChunkMap.ChunkGridWidth; ++i) {
            for (int j = 0; j < IsoChunkMap.ChunkGridWidth; ++j) {
                final IsoChunk chunk = this.getChunk(j, i);
                if (chunk != null && chunk.bLoaded) {
                    final List<VehicleCache> vehicleGet = VehicleCache.vehicleGet(chunk.wx, chunk.wy);
                    if (vehicleGet != null && chunk.vehicles.size() != vehicleGet.size()) {
                        for (int k = 0; k < vehicleGet.size(); ++k) {
                            final short id = vehicleGet.get(k).id;
                            boolean b = false;
                            for (int l = 0; l < chunk.vehicles.size(); ++l) {
                                if (chunk.vehicles.get(l).getId() == id) {
                                    b = true;
                                    break;
                                }
                            }
                            if (!b && VehicleManager.instance.getVehicleByID(id) == null) {
                                VehicleManager.instance.sendRequestGetFull(id);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void checkIntegrity() {
        IsoWorld.instance.CurrentCell.ChunkMap[0].XMinTiles = -1;
        for (int i = IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles(); i < IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMaxTiles(); ++i) {
            for (int j = IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMinTiles(); j < IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMaxTiles(); ++j) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(i, j, 0);
                if (gridSquare != null && (gridSquare.getX() != i || gridSquare.getY() != j)) {
                    final int n = i / 10;
                    final int n2 = j / 10;
                    final int n3 = n - IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMin();
                    final int n4 = n2 - IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMin();
                    final IsoChunk isoChunk = new IsoChunk(IsoWorld.instance.CurrentCell);
                    isoChunk.refs.add(IsoWorld.instance.CurrentCell.ChunkMap[0]);
                    WorldStreamer.instance.addJob(isoChunk, i / 10, j / 10, false);
                    while (!isoChunk.bLoaded) {
                        try {
                            Thread.sleep(13L);
                        }
                        catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    public void checkIntegrityThread() {
        IsoWorld.instance.CurrentCell.ChunkMap[0].XMinTiles = -1;
        for (int i = IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles(); i < IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMaxTiles(); ++i) {
            for (int j = IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMinTiles(); j < IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMaxTiles(); ++j) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(i, j, 0);
                if (gridSquare != null && (gridSquare.getX() != i || gridSquare.getY() != j)) {
                    final int n = i / 10;
                    final int n2 = j / 10;
                    final int n3 = n - IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMin();
                    final int n4 = n2 - IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMin();
                    final IsoChunk isoChunk = new IsoChunk(IsoWorld.instance.CurrentCell);
                    isoChunk.refs.add(IsoWorld.instance.CurrentCell.ChunkMap[0]);
                    WorldStreamer.instance.addJobInstant(isoChunk, i, j, i / 10, j / 10);
                }
                if (gridSquare != null) {}
            }
        }
    }
    
    public void LoadChunk(final int n, final int n2, final int n3, final int n4) {
        if (IsoChunkMap.SharedChunks.containsKey((n << 16) + n2)) {
            final IsoChunk isoChunk = IsoChunkMap.SharedChunks.get((n << 16) + n2);
            isoChunk.setCache();
            this.setChunk(n3, n4, isoChunk);
            isoChunk.refs.add(this);
        }
        else {
            IsoChunk value = IsoChunkMap.chunkStore.poll();
            if (value == null) {
                value = new IsoChunk(this.cell);
            }
            IsoChunkMap.SharedChunks.put((n << 16) + n2, value);
            value.refs.add(this);
            WorldStreamer.instance.addJob(value, n, n2, false);
        }
    }
    
    public IsoChunk LoadChunkForLater(final int n, final int n2, final int n3, final int n4) {
        if (!IsoWorld.instance.getMetaGrid().isValidChunk(n, n2)) {
            return null;
        }
        IsoChunk value;
        if (IsoChunkMap.SharedChunks.containsKey((n << 16) + n2)) {
            value = IsoChunkMap.SharedChunks.get((n << 16) + n2);
            if (!value.refs.contains(this)) {
                value.refs.add(this);
                value.lightCheck[this.PlayerID] = true;
            }
            if (!value.bLoaded) {
                return value;
            }
            this.setChunk(n3, n4, value);
        }
        else {
            value = IsoChunkMap.chunkStore.poll();
            if (value == null) {
                value = new IsoChunk(this.cell);
            }
            IsoChunkMap.SharedChunks.put((n << 16) + n2, value);
            value.refs.add(this);
            WorldStreamer.instance.addJob(value, n, n2, true);
        }
        return value;
    }
    
    public IsoChunk getChunkForGridSquare(int gridSquareToTileX, int gridSquareToTileY) {
        gridSquareToTileX = this.gridSquareToTileX(gridSquareToTileX);
        gridSquareToTileY = this.gridSquareToTileY(gridSquareToTileY);
        if (this.isTileOutOfrange(gridSquareToTileX) || this.isTileOutOfrange(gridSquareToTileY)) {
            return null;
        }
        return this.getChunk(tileToChunk(gridSquareToTileX), tileToChunk(gridSquareToTileY));
    }
    
    public IsoChunk getChunkCurrent(final int n, final int n2) {
        if (n < 0 || n >= IsoChunkMap.ChunkGridWidth || n2 < 0 || n2 >= IsoChunkMap.ChunkGridWidth) {
            return null;
        }
        if (!this.bReadBufferA) {
            return this.chunksSwapA[IsoChunkMap.ChunkGridWidth * n2 + n];
        }
        return this.chunksSwapB[IsoChunkMap.ChunkGridWidth * n2 + n];
    }
    
    public void setGridSquare(final IsoGridSquare isoGridSquare, final int n, final int n2, final int maxLevel) {
        assert isoGridSquare.x == n && isoGridSquare.y == n2 && isoGridSquare.z == maxLevel;
        final int gridSquareToTileX = this.gridSquareToTileX(n);
        final int gridSquareToTileY = this.gridSquareToTileY(n2);
        if (this.isTileOutOfrange(gridSquareToTileX) || this.isTileOutOfrange(gridSquareToTileY) || this.isGridSquareOutOfRangeZ(maxLevel)) {
            return;
        }
        final IsoChunk chunk = this.getChunk(tileToChunk(gridSquareToTileX), tileToChunk(gridSquareToTileY));
        if (chunk == null) {
            return;
        }
        if (maxLevel > chunk.maxLevel) {
            chunk.maxLevel = maxLevel;
        }
        chunk.setSquare(this.tileToGridSquare(gridSquareToTileX), this.tileToGridSquare(gridSquareToTileY), maxLevel, isoGridSquare);
    }
    
    public IsoGridSquare getGridSquare(int gridSquareToTileX, int gridSquareToTileY, final int n) {
        gridSquareToTileX = this.gridSquareToTileX(gridSquareToTileX);
        gridSquareToTileY = this.gridSquareToTileY(gridSquareToTileY);
        return this.getGridSquareDirect(gridSquareToTileX, gridSquareToTileY, n);
    }
    
    public IsoGridSquare getGridSquareDirect(final int n, final int n2, final int n3) {
        if (this.isTileOutOfrange(n) || this.isTileOutOfrange(n2) || this.isGridSquareOutOfRangeZ(n3)) {
            return null;
        }
        final IsoChunk chunk = this.getChunk(tileToChunk(n), tileToChunk(n2));
        if (chunk == null) {
            return null;
        }
        return chunk.getGridSquare(this.tileToGridSquare(n), this.tileToGridSquare(n2), n3);
    }
    
    private int tileToGridSquare(final int n) {
        return n % 10;
    }
    
    private static int tileToChunk(final int n) {
        return n / 10;
    }
    
    private boolean isTileOutOfrange(final int n) {
        return n < 0 || n >= this.getWidthInTiles();
    }
    
    private boolean isGridSquareOutOfRangeZ(final int n) {
        return n < 0 || n >= 8;
    }
    
    private int gridSquareToTileX(final int n) {
        return n - (this.WorldX - IsoChunkMap.ChunkGridWidth / 2) * 10;
    }
    
    private int gridSquareToTileY(final int n) {
        return n - (this.WorldY - IsoChunkMap.ChunkGridWidth / 2) * 10;
    }
    
    public IsoChunk getChunk(final int n, final int n2) {
        if (n < 0 || n >= IsoChunkMap.ChunkGridWidth || n2 < 0 || n2 >= IsoChunkMap.ChunkGridWidth) {
            return null;
        }
        if (this.bReadBufferA) {
            return this.chunksSwapA[IsoChunkMap.ChunkGridWidth * n2 + n];
        }
        return this.chunksSwapB[IsoChunkMap.ChunkGridWidth * n2 + n];
    }
    
    private void setChunk(final int n, final int n2, final IsoChunk isoChunk) {
        if (!this.bReadBufferA) {
            this.chunksSwapA[IsoChunkMap.ChunkGridWidth * n2 + n] = isoChunk;
        }
        else {
            this.chunksSwapB[IsoChunkMap.ChunkGridWidth * n2 + n] = isoChunk;
        }
    }
    
    public boolean setChunkDirect(final IsoChunk isoChunk, final boolean b) {
        final long nanoTime = System.nanoTime();
        if (b) {
            IsoChunkMap.bSettingChunk.lock();
        }
        final long nanoTime2 = System.nanoTime();
        final int n = isoChunk.wx - this.WorldX;
        final int n2 = isoChunk.wy - this.WorldY;
        int n3 = n + IsoChunkMap.ChunkGridWidth / 2;
        int n4 = n2 + IsoChunkMap.ChunkGridWidth / 2;
        if (isoChunk.jobType == IsoChunk.JobType.Convert) {
            n3 = 0;
            n4 = 0;
        }
        if (isoChunk.refs.isEmpty() || n3 < 0 || n4 < 0 || n3 >= IsoChunkMap.ChunkGridWidth || n4 >= IsoChunkMap.ChunkGridWidth) {
            if (isoChunk.refs.contains(this)) {
                isoChunk.refs.remove(this);
                if (isoChunk.refs.isEmpty()) {
                    IsoChunkMap.SharedChunks.remove((isoChunk.wx << 16) + isoChunk.wy);
                }
            }
            if (b) {
                IsoChunkMap.bSettingChunk.unlock();
            }
            return false;
        }
        try {
            if (this.bReadBufferA) {
                this.chunksSwapA[IsoChunkMap.ChunkGridWidth * n4 + n3] = isoChunk;
            }
            else {
                this.chunksSwapB[IsoChunkMap.ChunkGridWidth * n4 + n3] = isoChunk;
            }
            isoChunk.bLoaded = true;
            if (isoChunk.jobType == IsoChunk.JobType.None) {
                isoChunk.setCache();
                isoChunk.updateBuildings();
            }
            final double n5 = (System.nanoTime() - nanoTime2) / 1000000.0;
            final double n6 = (System.nanoTime() - nanoTime) / 1000000.0;
            if (LightingThread.DebugLockTime && n6 > 10.0) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(DD)Ljava/lang/String;, n5, n6));
            }
        }
        finally {
            if (b) {
                IsoChunkMap.bSettingChunk.unlock();
            }
        }
        return true;
    }
    
    public void drawDebugChunkMap() {
        int n = 64;
        for (int i = 0; i < IsoChunkMap.ChunkGridWidth; ++i) {
            int n2 = 0;
            for (int j = 0; j < IsoChunkMap.ChunkGridWidth; ++j) {
                n2 += 64;
                final IsoChunk chunk = this.getChunk(i, j);
                if (chunk != null) {
                    if (chunk.getGridSquare(0, 0, 0) == null) {
                        TextManager.instance.DrawString((double)n, (double)n2, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, chunk.wx, chunk.wy));
                    }
                }
            }
            n += 128;
        }
    }
    
    private void LoadLeft() {
        this.XMinTiles = -1;
        this.YMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMaxTiles = -1;
        this.Left();
        WorldSimulation.instance.scrollGroundLeft(this.PlayerID);
        this.XMinTiles = -1;
        this.YMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMaxTiles = -1;
        for (int i = -(IsoChunkMap.ChunkGridWidth / 2); i <= IsoChunkMap.ChunkGridWidth / 2; ++i) {
            this.LoadChunkForLater(this.WorldX - IsoChunkMap.ChunkGridWidth / 2, this.WorldY + i, 0, i + IsoChunkMap.ChunkGridWidth / 2);
        }
        this.SwapChunkBuffers();
        this.XMinTiles = -1;
        this.YMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMaxTiles = -1;
        this.UpdateCellCache();
        LightingThread.instance.scrollLeft(this.PlayerID);
    }
    
    public void SwapChunkBuffers() {
        for (int i = 0; i < IsoChunkMap.ChunkGridWidth * IsoChunkMap.ChunkGridWidth; ++i) {
            if (this.bReadBufferA) {
                this.chunksSwapA[i] = null;
            }
            else {
                this.chunksSwapB[i] = null;
            }
        }
        final int n = -1;
        this.XMaxTiles = n;
        this.XMinTiles = n;
        final int n2 = -1;
        this.YMaxTiles = n2;
        this.YMinTiles = n2;
        this.bReadBufferA = !this.bReadBufferA;
    }
    
    private void setChunk(final int n, final IsoChunk isoChunk) {
        if (!this.bReadBufferA) {
            this.chunksSwapA[n] = isoChunk;
        }
        else {
            this.chunksSwapB[n] = isoChunk;
        }
    }
    
    private IsoChunk getChunk(final int n) {
        if (this.bReadBufferA) {
            return this.chunksSwapA[n];
        }
        return this.chunksSwapB[n];
    }
    
    private void LoadRight() {
        this.XMinTiles = -1;
        this.YMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMaxTiles = -1;
        this.Right();
        WorldSimulation.instance.scrollGroundRight(this.PlayerID);
        this.XMinTiles = -1;
        this.YMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMaxTiles = -1;
        for (int i = -(IsoChunkMap.ChunkGridWidth / 2); i <= IsoChunkMap.ChunkGridWidth / 2; ++i) {
            this.LoadChunkForLater(this.WorldX + IsoChunkMap.ChunkGridWidth / 2, this.WorldY + i, IsoChunkMap.ChunkGridWidth - 1, i + IsoChunkMap.ChunkGridWidth / 2);
        }
        this.SwapChunkBuffers();
        this.XMinTiles = -1;
        this.YMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMaxTiles = -1;
        this.UpdateCellCache();
        LightingThread.instance.scrollRight(this.PlayerID);
    }
    
    private void LoadUp() {
        this.XMinTiles = -1;
        this.YMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMaxTiles = -1;
        this.Up();
        WorldSimulation.instance.scrollGroundUp(this.PlayerID);
        this.XMinTiles = -1;
        this.YMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMaxTiles = -1;
        for (int i = -(IsoChunkMap.ChunkGridWidth / 2); i <= IsoChunkMap.ChunkGridWidth / 2; ++i) {
            this.LoadChunkForLater(this.WorldX + i, this.WorldY - IsoChunkMap.ChunkGridWidth / 2, i + IsoChunkMap.ChunkGridWidth / 2, 0);
        }
        this.SwapChunkBuffers();
        this.XMinTiles = -1;
        this.YMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMaxTiles = -1;
        this.UpdateCellCache();
        LightingThread.instance.scrollUp(this.PlayerID);
    }
    
    private void LoadDown() {
        this.XMinTiles = -1;
        this.YMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMaxTiles = -1;
        this.Down();
        WorldSimulation.instance.scrollGroundDown(this.PlayerID);
        this.XMinTiles = -1;
        this.YMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMaxTiles = -1;
        for (int i = -(IsoChunkMap.ChunkGridWidth / 2); i <= IsoChunkMap.ChunkGridWidth / 2; ++i) {
            this.LoadChunkForLater(this.WorldX + i, this.WorldY + IsoChunkMap.ChunkGridWidth / 2, i + IsoChunkMap.ChunkGridWidth / 2, IsoChunkMap.ChunkGridWidth - 1);
        }
        this.SwapChunkBuffers();
        this.XMinTiles = -1;
        this.YMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMaxTiles = -1;
        this.UpdateCellCache();
        LightingThread.instance.scrollDown(this.PlayerID);
    }
    
    private void UpdateCellCache() {
        for (int widthInTiles = this.getWidthInTiles(), i = 0; i < widthInTiles; ++i) {
            for (int j = 0; j < widthInTiles; ++j) {
                for (int k = 0; k < 8; ++k) {
                    IsoWorld.instance.CurrentCell.setCacheGridSquareLocal(i, j, k, this.getGridSquare(i + this.getWorldXMinTiles(), j + this.getWorldYMinTiles(), k), this.PlayerID);
                }
            }
        }
    }
    
    private void Up() {
        for (int i = 0; i < IsoChunkMap.ChunkGridWidth; ++i) {
            for (int j = IsoChunkMap.ChunkGridWidth - 1; j > 0; --j) {
                IsoChunk chunk = this.getChunk(i, j);
                if (chunk == null && j == IsoChunkMap.ChunkGridWidth - 1) {
                    chunk = IsoChunkMap.SharedChunks.get((this.WorldX - IsoChunkMap.ChunkGridWidth / 2 + i << 16) + (this.WorldY - IsoChunkMap.ChunkGridWidth / 2 + j));
                    if (chunk != null) {
                        if (chunk.refs.contains(this)) {
                            chunk.refs.remove(this);
                            if (chunk.refs.isEmpty()) {
                                IsoChunkMap.SharedChunks.remove((chunk.wx << 16) + chunk.wy);
                            }
                        }
                        chunk = null;
                    }
                }
                if (chunk != null && j == IsoChunkMap.ChunkGridWidth - 1) {
                    chunk.refs.remove(this);
                    if (chunk.refs.isEmpty()) {
                        IsoChunkMap.SharedChunks.remove((chunk.wx << 16) + chunk.wy);
                        chunk.removeFromWorld();
                        ChunkSaveWorker.instance.Add(chunk);
                    }
                }
                this.setChunk(i, j, this.getChunk(i, j - 1));
            }
            this.setChunk(i, 0, null);
        }
        --this.WorldY;
    }
    
    private void Down() {
        for (int i = 0; i < IsoChunkMap.ChunkGridWidth; ++i) {
            for (int j = 0; j < IsoChunkMap.ChunkGridWidth - 1; ++j) {
                IsoChunk chunk = this.getChunk(i, j);
                if (chunk == null && j == 0) {
                    chunk = IsoChunkMap.SharedChunks.get((this.WorldX - IsoChunkMap.ChunkGridWidth / 2 + i << 16) + (this.WorldY - IsoChunkMap.ChunkGridWidth / 2 + j));
                    if (chunk != null) {
                        if (chunk.refs.contains(this)) {
                            chunk.refs.remove(this);
                            if (chunk.refs.isEmpty()) {
                                IsoChunkMap.SharedChunks.remove((chunk.wx << 16) + chunk.wy);
                            }
                        }
                        chunk = null;
                    }
                }
                if (chunk != null && j == 0) {
                    chunk.refs.remove(this);
                    if (chunk.refs.isEmpty()) {
                        IsoChunkMap.SharedChunks.remove((chunk.wx << 16) + chunk.wy);
                        chunk.removeFromWorld();
                        ChunkSaveWorker.instance.Add(chunk);
                    }
                }
                this.setChunk(i, j, this.getChunk(i, j + 1));
            }
            this.setChunk(i, IsoChunkMap.ChunkGridWidth - 1, null);
        }
        ++this.WorldY;
    }
    
    private void Left() {
        for (int i = 0; i < IsoChunkMap.ChunkGridWidth; ++i) {
            for (int j = IsoChunkMap.ChunkGridWidth - 1; j > 0; --j) {
                IsoChunk chunk = this.getChunk(j, i);
                if (chunk == null && j == IsoChunkMap.ChunkGridWidth - 1) {
                    chunk = IsoChunkMap.SharedChunks.get((this.WorldX - IsoChunkMap.ChunkGridWidth / 2 + j << 16) + (this.WorldY - IsoChunkMap.ChunkGridWidth / 2 + i));
                    if (chunk != null) {
                        if (chunk.refs.contains(this)) {
                            chunk.refs.remove(this);
                            if (chunk.refs.isEmpty()) {
                                IsoChunkMap.SharedChunks.remove((chunk.wx << 16) + chunk.wy);
                            }
                        }
                        chunk = null;
                    }
                }
                if (chunk != null && j == IsoChunkMap.ChunkGridWidth - 1) {
                    chunk.refs.remove(this);
                    if (chunk.refs.isEmpty()) {
                        IsoChunkMap.SharedChunks.remove((chunk.wx << 16) + chunk.wy);
                        chunk.removeFromWorld();
                        ChunkSaveWorker.instance.Add(chunk);
                    }
                }
                this.setChunk(j, i, this.getChunk(j - 1, i));
            }
            this.setChunk(0, i, null);
        }
        --this.WorldX;
    }
    
    private void Right() {
        for (int i = 0; i < IsoChunkMap.ChunkGridWidth; ++i) {
            for (int j = 0; j < IsoChunkMap.ChunkGridWidth - 1; ++j) {
                IsoChunk chunk = this.getChunk(j, i);
                if (chunk == null && j == 0) {
                    chunk = IsoChunkMap.SharedChunks.get((this.WorldX - IsoChunkMap.ChunkGridWidth / 2 + j << 16) + (this.WorldY - IsoChunkMap.ChunkGridWidth / 2 + i));
                    if (chunk != null) {
                        if (chunk.refs.contains(this)) {
                            chunk.refs.remove(this);
                            if (chunk.refs.isEmpty()) {
                                IsoChunkMap.SharedChunks.remove((chunk.wx << 16) + chunk.wy);
                            }
                        }
                        chunk = null;
                    }
                }
                if (chunk != null && j == 0) {
                    chunk.refs.remove(this);
                    if (chunk.refs.isEmpty()) {
                        IsoChunkMap.SharedChunks.remove((chunk.wx << 16) + chunk.wy);
                        chunk.removeFromWorld();
                        ChunkSaveWorker.instance.Add(chunk);
                    }
                }
                this.setChunk(j, i, this.getChunk(j + 1, i));
            }
            this.setChunk(IsoChunkMap.ChunkGridWidth - 1, i, null);
        }
        ++this.WorldX;
    }
    
    public int getWorldXMin() {
        return this.WorldX - IsoChunkMap.ChunkGridWidth / 2;
    }
    
    public int getWorldYMin() {
        return this.WorldY - IsoChunkMap.ChunkGridWidth / 2;
    }
    
    public void ProcessChunkPos(final IsoGameCharacter isoGameCharacter) {
        int n = (int)isoGameCharacter.getX();
        int n2 = (int)isoGameCharacter.getY();
        final int n3 = (int)isoGameCharacter.getZ();
        if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance().getVehicle() != null) {
            final IsoPlayer instance = IsoPlayer.getInstance();
            final float n4 = instance.getVehicle().getCurrentSpeedKmHour() / 5.0f;
            n += Math.round(instance.getForwardDirection().x * n4);
            n2 += Math.round(instance.getForwardDirection().y * n4);
        }
        final int worldX = n / 10;
        final int worldY = n2 / 10;
        if (worldX == this.WorldX && worldY == this.WorldY) {
            return;
        }
        final long nanoTime = System.nanoTime();
        IsoChunkMap.bSettingChunk.lock();
        final long nanoTime2 = System.nanoTime();
        try {
            if (Math.abs(worldX - this.WorldX) >= IsoChunkMap.ChunkGridWidth || Math.abs(worldY - this.WorldY) >= IsoChunkMap.ChunkGridWidth) {
                if (LightingJNI.init) {
                    LightingJNI.teleport(this.PlayerID, worldX - IsoChunkMap.ChunkGridWidth / 2, worldY - IsoChunkMap.ChunkGridWidth / 2);
                }
                this.Unload();
                final IsoPlayer isoPlayer = IsoPlayer.players[this.PlayerID];
                isoPlayer.removeFromSquare();
                isoPlayer.square = null;
                this.WorldX = worldX;
                this.WorldY = worldY;
                WorldSimulation.instance.activateChunkMap(this.PlayerID);
                final int n5 = this.WorldX - IsoChunkMap.ChunkGridWidth / 2;
                final int n6 = this.WorldY - IsoChunkMap.ChunkGridWidth / 2;
                final int n7 = this.WorldX + IsoChunkMap.ChunkGridWidth / 2;
                final int n8 = this.WorldY + IsoChunkMap.ChunkGridWidth / 2;
                for (int i = n5; i <= n7; ++i) {
                    for (int j = n6; j <= n8; ++j) {
                        this.LoadChunkForLater(i, j, i - n5, j - n6);
                    }
                }
                this.SwapChunkBuffers();
                this.UpdateCellCache();
                if (!IsoWorld.instance.getCell().getObjectList().contains(isoPlayer)) {
                    IsoWorld.instance.getCell().getAddList().add(isoPlayer);
                }
            }
            else if (worldX != this.WorldX) {
                if (worldX < this.WorldX) {
                    this.LoadLeft();
                }
                else {
                    this.LoadRight();
                }
            }
            else if (worldY != this.WorldY) {
                if (worldY < this.WorldY) {
                    this.LoadUp();
                }
                else {
                    this.LoadDown();
                }
            }
        }
        finally {
            IsoChunkMap.bSettingChunk.unlock();
        }
        final double n9 = (System.nanoTime() - nanoTime2) / 1000000.0;
        final double n10 = (System.nanoTime() - nanoTime) / 1000000.0;
        if (LightingThread.DebugLockTime && n10 > 10.0) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(DD)Ljava/lang/String;, n9, n10));
        }
    }
    
    public IsoRoom getRoom(final int n) {
        return null;
    }
    
    public int getWidthInTiles() {
        return IsoChunkMap.ChunkWidthInTiles;
    }
    
    public int getWorldXMinTiles() {
        if (this.XMinTiles != -1) {
            return this.XMinTiles;
        }
        return this.XMinTiles = this.getWorldXMin() * 10;
    }
    
    public int getWorldYMinTiles() {
        if (this.YMinTiles != -1) {
            return this.YMinTiles;
        }
        return this.YMinTiles = this.getWorldYMin() * 10;
    }
    
    public int getWorldXMaxTiles() {
        if (this.XMaxTiles != -1) {
            return this.XMaxTiles;
        }
        return this.XMaxTiles = this.getWorldXMin() * 10 + this.getWidthInTiles();
    }
    
    public int getWorldYMaxTiles() {
        if (this.YMaxTiles != -1) {
            return this.YMaxTiles;
        }
        return this.YMaxTiles = this.getWorldYMin() * 10 + this.getWidthInTiles();
    }
    
    public void Save() {
        if (GameServer.bServer) {
            return;
        }
        for (int i = 0; i < IsoChunkMap.ChunkGridWidth; ++i) {
            for (int j = 0; j < IsoChunkMap.ChunkGridWidth; ++j) {
                final IsoChunk chunk = this.getChunk(i, j);
                if (chunk != null && !IsoChunkMap.saveList.contains(chunk)) {
                    try {
                        chunk.Save(true);
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    
    public void renderBloodForChunks(final int n) {
        if (!DebugOptions.instance.Terrain.RenderTiles.BloodDecals.getValue()) {
            return;
        }
        if (n > IsoCamera.CamCharacter.z) {
            return;
        }
        if (Core.OptionBloodDecals == 0) {
            return;
        }
        final float n2 = (float)GameTime.getInstance().getWorldAgeHours();
        final int playerIndex = IsoCamera.frameState.playerIndex;
        for (int i = 0; i < IsoFloorBloodSplat.FloorBloodTypes.length; ++i) {
            IsoChunkMap.splatByType.get(i).clear();
        }
        for (int j = 0; j < IsoChunkMap.ChunkGridWidth; ++j) {
            for (int k = 0; k < IsoChunkMap.ChunkGridWidth; ++k) {
                final IsoChunk chunk = this.getChunk(j, k);
                if (chunk != null) {
                    for (int l = 0; l < chunk.FloorBloodSplatsFade.size(); ++l) {
                        final IsoFloorBloodSplat e = chunk.FloorBloodSplatsFade.get(l);
                        if (e.index < 1 || e.index > 10 || IsoChunk.renderByIndex[Core.OptionBloodDecals - 1][e.index - 1] != 0) {
                            if ((int)e.z == n && e.Type >= 0 && e.Type < IsoFloorBloodSplat.FloorBloodTypes.length) {
                                e.chunk = chunk;
                                IsoChunkMap.splatByType.get(e.Type).add(e);
                            }
                        }
                    }
                    if (!chunk.FloorBloodSplats.isEmpty()) {
                        for (int n3 = 0; n3 < chunk.FloorBloodSplats.size(); ++n3) {
                            final IsoFloorBloodSplat e2 = chunk.FloorBloodSplats.get(n3);
                            if (e2.index < 1 || e2.index > 10 || IsoChunk.renderByIndex[Core.OptionBloodDecals - 1][e2.index - 1] != 0) {
                                if ((int)e2.z == n && e2.Type >= 0 && e2.Type < IsoFloorBloodSplat.FloorBloodTypes.length) {
                                    e2.chunk = chunk;
                                    IsoChunkMap.splatByType.get(e2.Type).add(e2);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int index = 0; index < IsoChunkMap.splatByType.size(); ++index) {
            final ArrayList<IsoFloorBloodSplat> list = IsoChunkMap.splatByType.get(index);
            if (!list.isEmpty()) {
                final String key = IsoFloorBloodSplat.FloorBloodTypes[index];
                IsoSprite isoSprite;
                if (!IsoFloorBloodSplat.SpriteMap.containsKey(key)) {
                    final IsoSprite createSprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
                    createSprite.LoadFramesPageSimple(key, key, key, key);
                    IsoFloorBloodSplat.SpriteMap.put(key, createSprite);
                    isoSprite = createSprite;
                }
                else {
                    isoSprite = IsoFloorBloodSplat.SpriteMap.get(key);
                }
                for (int index2 = 0; index2 < list.size(); ++index2) {
                    final IsoFloorBloodSplat o = list.get(index2);
                    IsoChunkMap.inf.r = 1.0f;
                    IsoChunkMap.inf.g = 1.0f;
                    IsoChunkMap.inf.b = 1.0f;
                    IsoChunkMap.inf.a = 0.27f;
                    final float n4 = (o.x + o.y / o.x) * (o.Type + 1);
                    final float n5 = n4 * o.x / o.y * (o.Type + 1) / (n4 + o.y);
                    final float n6 = n5 * n4 * n5 * o.x / (o.y + 2.0f);
                    final float n7 = n4 * 42367.543f;
                    final float n8 = n5 * 6367.123f;
                    final float n9 = n6 * 23367.133f;
                    final float n10 = n7 % 1000.0f;
                    final float n11 = n8 % 1000.0f;
                    final float n12 = n9 % 1000.0f;
                    float n13 = n10 / 1000.0f;
                    final float n14 = n11 / 1000.0f;
                    final float n15 = n12 / 1000.0f;
                    if (n13 > 0.25f) {
                        n13 = 0.25f;
                    }
                    final ColorInfo inf = IsoChunkMap.inf;
                    inf.r -= n13 * 2.0f;
                    final ColorInfo inf2 = IsoChunkMap.inf;
                    inf2.g -= n13 * 2.0f;
                    final ColorInfo inf3 = IsoChunkMap.inf;
                    inf3.b -= n13 * 2.0f;
                    final ColorInfo inf4 = IsoChunkMap.inf;
                    inf4.r += n14 / 3.0f;
                    final ColorInfo inf5 = IsoChunkMap.inf;
                    inf5.g -= n15 / 3.0f;
                    final ColorInfo inf6 = IsoChunkMap.inf;
                    inf6.b -= n15 / 3.0f;
                    final float n16 = n2 - o.worldAge;
                    if (n16 >= 0.0f && n16 < 72.0f) {
                        final float n17 = 1.0f - n16 / 72.0f;
                        final ColorInfo inf7 = IsoChunkMap.inf;
                        inf7.r *= 0.2f + n17 * 0.8f;
                        final ColorInfo inf8 = IsoChunkMap.inf;
                        inf8.g *= 0.2f + n17 * 0.8f;
                        final ColorInfo inf9 = IsoChunkMap.inf;
                        inf9.b *= 0.2f + n17 * 0.8f;
                        final ColorInfo inf10 = IsoChunkMap.inf;
                        inf10.a *= 0.25f + n17 * 0.75f;
                    }
                    else {
                        final ColorInfo inf11 = IsoChunkMap.inf;
                        inf11.r *= 0.2f;
                        final ColorInfo inf12 = IsoChunkMap.inf;
                        inf12.g *= 0.2f;
                        final ColorInfo inf13 = IsoChunkMap.inf;
                        inf13.b *= 0.2f;
                        final ColorInfo inf14 = IsoChunkMap.inf;
                        inf14.a *= 0.25f;
                    }
                    if (o.fade > 0) {
                        final ColorInfo inf15 = IsoChunkMap.inf;
                        inf15.a *= o.fade / (PerformanceSettings.getLockFPS() * 5.0f);
                        final IsoFloorBloodSplat isoFloorBloodSplat = o;
                        if (--isoFloorBloodSplat.fade == 0) {
                            o.chunk.FloorBloodSplatsFade.remove(o);
                        }
                    }
                    final IsoGridSquare gridSquare = o.chunk.getGridSquare((int)o.x, (int)o.y, (int)o.z);
                    if (gridSquare != null) {
                        final int vertLight = gridSquare.getVertLight(0, playerIndex);
                        final int vertLight2 = gridSquare.getVertLight(1, playerIndex);
                        final int vertLight3 = gridSquare.getVertLight(2, playerIndex);
                        final int vertLight4 = gridSquare.getVertLight(3, playerIndex);
                        final float redChannelFromABGR = Color.getRedChannelFromABGR(vertLight);
                        final float greenChannelFromABGR = Color.getGreenChannelFromABGR(vertLight);
                        final float blueChannelFromABGR = Color.getBlueChannelFromABGR(vertLight);
                        final float redChannelFromABGR2 = Color.getRedChannelFromABGR(vertLight2);
                        final float greenChannelFromABGR2 = Color.getGreenChannelFromABGR(vertLight2);
                        final float blueChannelFromABGR2 = Color.getBlueChannelFromABGR(vertLight2);
                        final float redChannelFromABGR3 = Color.getRedChannelFromABGR(vertLight3);
                        final float greenChannelFromABGR3 = Color.getGreenChannelFromABGR(vertLight3);
                        final float blueChannelFromABGR3 = Color.getBlueChannelFromABGR(vertLight3);
                        final float redChannelFromABGR4 = Color.getRedChannelFromABGR(vertLight4);
                        final float greenChannelFromABGR4 = Color.getGreenChannelFromABGR(vertLight4);
                        final float blueChannelFromABGR4 = Color.getBlueChannelFromABGR(vertLight4);
                        final ColorInfo inf16 = IsoChunkMap.inf;
                        inf16.r *= (redChannelFromABGR + redChannelFromABGR2 + redChannelFromABGR3 + redChannelFromABGR4) / 4.0f;
                        final ColorInfo inf17 = IsoChunkMap.inf;
                        inf17.g *= (greenChannelFromABGR + greenChannelFromABGR2 + greenChannelFromABGR3 + greenChannelFromABGR4) / 4.0f;
                        final ColorInfo inf18 = IsoChunkMap.inf;
                        inf18.b *= (blueChannelFromABGR + blueChannelFromABGR2 + blueChannelFromABGR3 + blueChannelFromABGR4) / 4.0f;
                    }
                    isoSprite.renderBloodSplat(o.chunk.wx * 10 + o.x, o.chunk.wy * 10 + o.y, o.z, IsoChunkMap.inf);
                }
            }
        }
    }
    
    public void copy(final IsoChunkMap isoChunkMap) {
        this.WorldX = isoChunkMap.WorldX;
        this.WorldY = isoChunkMap.WorldY;
        this.XMinTiles = -1;
        this.YMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMaxTiles = -1;
        for (int i = 0; i < IsoChunkMap.ChunkGridWidth * IsoChunkMap.ChunkGridWidth; ++i) {
            this.bReadBufferA = isoChunkMap.bReadBufferA;
            if (this.bReadBufferA) {
                if (isoChunkMap.chunksSwapA[i] != null) {
                    isoChunkMap.chunksSwapA[i].refs.add(this);
                    this.chunksSwapA[i] = isoChunkMap.chunksSwapA[i];
                }
            }
            else if (isoChunkMap.chunksSwapB[i] != null) {
                isoChunkMap.chunksSwapB[i].refs.add(this);
                this.chunksSwapB[i] = isoChunkMap.chunksSwapB[i];
            }
        }
    }
    
    public void Unload() {
        for (int i = 0; i < IsoChunkMap.ChunkGridWidth; ++i) {
            for (int j = 0; j < IsoChunkMap.ChunkGridWidth; ++j) {
                final IsoChunk chunk = this.getChunk(j, i);
                if (chunk != null) {
                    if (chunk.refs.contains(this)) {
                        chunk.refs.remove(this);
                        if (chunk.refs.isEmpty()) {
                            IsoChunkMap.SharedChunks.remove((chunk.wx << 16) + chunk.wy);
                            chunk.removeFromWorld();
                            ChunkSaveWorker.instance.Add(chunk);
                        }
                    }
                    this.chunksSwapA[i * IsoChunkMap.ChunkGridWidth + j] = null;
                    this.chunksSwapB[i * IsoChunkMap.ChunkGridWidth + j] = null;
                }
            }
        }
        WorldSimulation.instance.deactivateChunkMap(this.PlayerID);
        this.XMinTiles = -1;
        this.XMaxTiles = -1;
        this.YMinTiles = -1;
        this.YMaxTiles = -1;
        if (IsoWorld.instance != null && IsoWorld.instance.CurrentCell != null) {
            IsoWorld.instance.CurrentCell.clearCacheGridSquare(this.PlayerID);
        }
    }
    
    static {
        SharedChunks = new HashMap<Integer, IsoChunk>();
        IsoChunkMap.MPWorldXA = 0;
        IsoChunkMap.MPWorldYA = 0;
        IsoChunkMap.MPWorldZA = 0;
        IsoChunkMap.WorldXA = 11702;
        IsoChunkMap.WorldYA = 6896;
        IsoChunkMap.WorldZA = 0;
        SWorldX = new int[4];
        SWorldY = new int[4];
        chunkStore = new ConcurrentLinkedQueue<IsoChunk>();
        bSettingChunk = new ReentrantLock(true);
        IsoChunkMap.StartChunkGridWidth = 13;
        IsoChunkMap.ChunkGridWidth = IsoChunkMap.StartChunkGridWidth;
        IsoChunkMap.ChunkWidthInTiles = 10 * IsoChunkMap.ChunkGridWidth;
        inf = new ColorInfo();
        saveList = new ArrayList<IsoChunk>();
        splatByType = new ArrayList<ArrayList<IsoFloorBloodSplat>>();
        for (int i = 0; i < IsoFloorBloodSplat.FloorBloodTypes.length; ++i) {
            IsoChunkMap.splatByType.add(new ArrayList<IsoFloorBloodSplat>());
        }
    }
}
