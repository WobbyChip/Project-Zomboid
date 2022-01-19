// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.IsoObject;
import zombie.iso.SliceY;
import java.io.File;
import java.io.FileOutputStream;
import zombie.ZomboidFileSystem;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import zombie.iso.WorldReuserThread;
import zombie.core.logger.LoggerManager;
import zombie.vehicles.VehiclesDB2;
import java.nio.ByteBuffer;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.debug.DebugLog;
import zombie.iso.IsoGridSquare;
import java.util.ArrayDeque;
import java.util.concurrent.LinkedBlockingQueue;
import zombie.GameTime;
import zombie.iso.IsoChunk;
import java.util.Collection;
import java.util.ArrayList;
import zombie.debug.DebugType;
import java.util.zip.CRC32;

public class ServerChunkLoader
{
    private long debugSlowMapLoadingDelay;
    private boolean MapLoading;
    private LoaderThread threadLoad;
    private SaveChunkThread threadSave;
    private final CRC32 crcSave;
    private RecalcAllThread threadRecalc;
    
    public ServerChunkLoader() {
        this.debugSlowMapLoadingDelay = 0L;
        this.MapLoading = false;
        this.crcSave = new CRC32();
        (this.threadLoad = new LoaderThread()).setName("LoadChunk");
        this.threadLoad.setDaemon(true);
        this.threadLoad.start();
        (this.threadRecalc = new RecalcAllThread()).setName("RecalcAll");
        this.threadRecalc.setDaemon(true);
        this.threadRecalc.setPriority(10);
        this.threadRecalc.start();
        (this.threadSave = new SaveChunkThread()).setName("SaveChunk");
        this.threadSave.setDaemon(true);
        this.threadSave.start();
    }
    
    public void addJob(final ServerMap.ServerCell e) {
        this.MapLoading = DebugType.Do(DebugType.MapLoading);
        this.threadLoad.toThread.add(e);
        MPStatistic.getInstance().LoaderThreadTasks.Added();
    }
    
    public void getLoaded(final ArrayList<ServerMap.ServerCell> c) {
        this.threadLoad.fromThread.drainTo(c);
    }
    
    public void quit() {
        this.threadLoad.quit();
        while (this.threadLoad.isAlive()) {
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException ex) {}
        }
        this.threadSave.quit();
        while (this.threadSave.isAlive()) {
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException ex2) {}
        }
    }
    
    public void addSaveUnloadedJob(final IsoChunk isoChunk) {
        this.threadSave.addUnloadedJob(isoChunk);
    }
    
    public void addSaveLoadedJob(final IsoChunk isoChunk) {
        this.threadSave.addLoadedJob(isoChunk);
    }
    
    public void saveLater(final GameTime gameTime) {
        this.threadSave.saveLater(gameTime);
    }
    
    public void updateSaved() {
        this.threadSave.update();
    }
    
    public void addRecalcJob(final ServerMap.ServerCell e) {
        this.threadRecalc.toThread.add(e);
        MPStatistic.getInstance().RecalcThreadTasks.Added();
    }
    
    public void getRecalc(final ArrayList<ServerMap.ServerCell> c) {
        MPStatistic.getInstance().ServerMapLoaded2.Added(this.threadRecalc.fromThread.size());
        this.threadRecalc.fromThread.drainTo(c);
        MPStatistic.getInstance().RecalcThreadTasks.Processed();
    }
    
    private class LoaderThread extends Thread
    {
        private final LinkedBlockingQueue<ServerMap.ServerCell> toThread;
        private final LinkedBlockingQueue<ServerMap.ServerCell> fromThread;
        ArrayDeque<IsoGridSquare> isoGridSquareCache;
        
        private LoaderThread() {
            this.toThread = new LinkedBlockingQueue<ServerMap.ServerCell>();
            this.fromThread = new LinkedBlockingQueue<ServerMap.ServerCell>();
            this.isoGridSquareCache = new ArrayDeque<IsoGridSquare>();
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        MPStatistic.getInstance().LoaderThread.End();
                        final ServerMap.ServerCell e = this.toThread.take();
                        MPStatistic.getInstance().LoaderThread.Start();
                        if (this.isoGridSquareCache.size() < 10000) {
                            IsoGridSquare.getSquaresForThread(this.isoGridSquareCache, 10000);
                            IsoGridSquare.loadGridSquareCache = this.isoGridSquareCache;
                        }
                        if (e.WX == -1 && e.WY == -1) {
                            break;
                        }
                        if (e.bCancelLoading) {
                            if (ServerChunkLoader.this.MapLoading) {
                                DebugLog.log(DebugType.MapLoading, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, e.WX, e.WY));
                            }
                            e.bLoadingWasCancelled = true;
                        }
                        else {
                            final long nanoTime = System.nanoTime();
                            for (int i = 0; i < 5; ++i) {
                                for (int j = 0; j < 5; ++j) {
                                    final int n = e.WX * 5 + i;
                                    final int n2 = e.WY * 5 + j;
                                    if (IsoWorld.instance.MetaGrid.isValidChunk(n, n2)) {
                                        IsoChunk isoChunk = IsoChunkMap.chunkStore.poll();
                                        if (isoChunk == null) {
                                            isoChunk = new IsoChunk(null);
                                        }
                                        ServerChunkLoader.this.threadSave.saveNow(n, n2);
                                        try {
                                            if (isoChunk.LoadOrCreate(n, n2, null)) {
                                                isoChunk.bLoaded = true;
                                            }
                                            else {
                                                ChunkChecksum.setChecksum(n, n2, 0L);
                                                isoChunk.Blam(n, n2);
                                                if (isoChunk.LoadBrandNew(n, n2)) {
                                                    isoChunk.bLoaded = true;
                                                }
                                            }
                                            if (isoChunk.bLoaded) {
                                                VehiclesDB2.instance.loadChunk(isoChunk);
                                            }
                                        }
                                        catch (Exception ex) {
                                            ex.printStackTrace();
                                            LoggerManager.getLogger("map").write(ex);
                                        }
                                        if (isoChunk.bLoaded) {
                                            e.chunks[i][j] = isoChunk;
                                        }
                                    }
                                }
                            }
                            if (GameServer.bDebug && ServerChunkLoader.this.debugSlowMapLoadingDelay > 0L) {
                                Thread.sleep(ServerChunkLoader.this.debugSlowMapLoadingDelay);
                            }
                            final float n3 = (System.nanoTime() - nanoTime) / 1000000.0f;
                            MPStatistic.getInstance().IncrementLoadCellFromDisk();
                            this.fromThread.add(e);
                            MPStatistic.getInstance().LoaderThreadTasks.Processed();
                        }
                    }
                }
                catch (Exception ex2) {
                    ex2.printStackTrace();
                    LoggerManager.getLogger("map").write(ex2);
                    continue;
                }
                break;
            }
        }
        
        public void quit() {
            final ServerMap.ServerCell e = new ServerMap.ServerCell();
            e.WX = -1;
            e.WY = -1;
            this.toThread.add(e);
            MPStatistic.getInstance().LoaderThreadTasks.Added();
        }
    }
    
    private class SaveUnloadedTask implements SaveTask
    {
        private final IsoChunk chunk;
        
        public SaveUnloadedTask(final IsoChunk chunk) {
            this.chunk = chunk;
        }
        
        @Override
        public void save() throws Exception {
            this.chunk.Save(false);
        }
        
        @Override
        public void release() {
            WorldReuserThread.instance.addReuseChunk(this.chunk);
        }
        
        @Override
        public int wx() {
            return this.chunk.wx;
        }
        
        @Override
        public int wy() {
            return this.chunk.wy;
        }
    }
    
    private class SaveLoadedTask implements SaveTask
    {
        private final ClientChunkRequest ccr;
        private final ClientChunkRequest.Chunk chunk;
        
        public SaveLoadedTask(final ClientChunkRequest ccr, final ClientChunkRequest.Chunk chunk) {
            this.ccr = ccr;
            this.chunk = chunk;
        }
        
        @Override
        public void save() throws Exception {
            final long checksumIfExists = ChunkChecksum.getChecksumIfExists(this.chunk.wx, this.chunk.wy);
            ServerChunkLoader.this.crcSave.reset();
            ServerChunkLoader.this.crcSave.update(this.chunk.bb.array(), 0, this.chunk.bb.position());
            if (checksumIfExists != ServerChunkLoader.this.crcSave.getValue()) {
                ChunkChecksum.setChecksum(this.chunk.wx, this.chunk.wy, ServerChunkLoader.this.crcSave.getValue());
                IsoChunk.SafeWrite("map_", this.chunk.wx, this.chunk.wy, this.chunk.bb);
            }
        }
        
        @Override
        public void release() {
            this.ccr.releaseChunk(this.chunk);
        }
        
        @Override
        public int wx() {
            return this.chunk.wx;
        }
        
        @Override
        public int wy() {
            return this.chunk.wy;
        }
    }
    
    private class SaveGameTimeTask implements SaveTask
    {
        private byte[] bytes;
        
        public SaveGameTimeTask(final GameTime gameTime) {
            try {
                final ByteArrayOutputStream out = new ByteArrayOutputStream(32768);
                try {
                    final DataOutputStream dataOutputStream = new DataOutputStream(out);
                    try {
                        gameTime.save(dataOutputStream);
                        dataOutputStream.close();
                        this.bytes = out.toByteArray();
                        dataOutputStream.close();
                    }
                    catch (Throwable t) {
                        try {
                            dataOutputStream.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                        throw t;
                    }
                    out.close();
                }
                catch (Throwable t2) {
                    try {
                        out.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                    throw t2;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        @Override
        public void save() throws Exception {
            if (this.bytes != null) {
                final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("map_t.bin");
                try {
                    final FileOutputStream fileOutputStream = new FileOutputStream(fileInCurrentSave);
                    try {
                        fileOutputStream.write(this.bytes);
                        fileOutputStream.close();
                    }
                    catch (Throwable t) {
                        try {
                            fileOutputStream.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                        throw t;
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        @Override
        public void release() {
        }
        
        @Override
        public int wx() {
            return 0;
        }
        
        @Override
        public int wy() {
            return 0;
        }
    }
    
    private class QuitThreadTask implements SaveTask
    {
        @Override
        public void save() throws Exception {
            ServerChunkLoader.this.threadSave.quit = true;
        }
        
        @Override
        public void release() {
        }
        
        @Override
        public int wx() {
            return 0;
        }
        
        @Override
        public int wy() {
            return 0;
        }
    }
    
    private class SaveChunkThread extends Thread
    {
        private final LinkedBlockingQueue<SaveTask> toThread;
        private final LinkedBlockingQueue<SaveTask> fromThread;
        private boolean quit;
        private final CRC32 crc32;
        private final ClientChunkRequest ccr;
        private final ArrayList<SaveTask> toSaveChunk;
        private final ArrayList<SaveTask> savedChunks;
        
        private SaveChunkThread() {
            this.toThread = new LinkedBlockingQueue<SaveTask>();
            this.fromThread = new LinkedBlockingQueue<SaveTask>();
            this.quit = false;
            this.crc32 = new CRC32();
            this.ccr = new ClientChunkRequest();
            this.toSaveChunk = new ArrayList<SaveTask>();
            this.savedChunks = new ArrayList<SaveTask>();
        }
        
        @Override
        public void run() {
            do {
                SaveTask e = null;
                try {
                    MPStatistic.getInstance().SaveThread.End();
                    e = this.toThread.take();
                    MPStatistic.getInstance().SaveThread.Start();
                    MPStatistic.getInstance().IncrementSaveCellToDisk();
                    e.save();
                    this.fromThread.add(e);
                    MPStatistic.getInstance().SaveTasks.Processed();
                }
                catch (InterruptedException ex2) {}
                catch (Exception ex) {
                    ex.printStackTrace();
                    if (e != null) {
                        LoggerManager.getLogger("map").write(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, e.wx(), e.wy()));
                    }
                    LoggerManager.getLogger("map").write(ex);
                }
            } while (!this.quit || !this.toThread.isEmpty());
        }
        
        public void addUnloadedJob(final IsoChunk isoChunk) {
            this.toThread.add(new SaveUnloadedTask(isoChunk));
            MPStatistic.getInstance().SaveTasks.SaveUnloadedTasksAdded();
        }
        
        public void addLoadedJob(final IsoChunk isoChunk) {
            final ClientChunkRequest.Chunk chunk = this.ccr.getChunk();
            chunk.wx = isoChunk.wx;
            chunk.wy = isoChunk.wy;
            this.ccr.getByteBuffer(chunk);
            try {
                isoChunk.SaveLoadedChunk(chunk, this.crc32);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                LoggerManager.getLogger("map").write(ex);
                this.ccr.releaseChunk(chunk);
                return;
            }
            this.toThread.add(new SaveLoadedTask(this.ccr, chunk));
            MPStatistic.getInstance().SaveTasks.SaveLoadedTasksAdded();
        }
        
        public void saveLater(final GameTime gameTime) {
            this.toThread.add(new SaveGameTimeTask(gameTime));
            MPStatistic.getInstance().SaveTasks.SaveGameTimeTasksAdded();
        }
        
        public void saveNow(final int n, final int n2) {
            this.toSaveChunk.clear();
            this.toThread.drainTo(this.toSaveChunk);
            for (int i = 0; i < this.toSaveChunk.size(); ++i) {
                final SaveTask e = this.toSaveChunk.get(i);
                if (e.wx() == n && e.wy() == n2) {
                    try {
                        this.toSaveChunk.remove(i--);
                        e.save();
                        MPStatistic.getInstance().IncrementServerChunkThreadSaveNow();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        LoggerManager.getLogger("map").write(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n, n2));
                        LoggerManager.getLogger("map").write(ex);
                    }
                    MPStatistic.getInstance().SaveTasks.Processed();
                    this.fromThread.add(e);
                }
            }
            this.toThread.addAll((Collection<?>)this.toSaveChunk);
        }
        
        public void quit() {
            this.toThread.add(new QuitThreadTask());
            MPStatistic.getInstance().SaveTasks.QuitThreadTasksAdded();
        }
        
        public void update() {
            this.savedChunks.clear();
            this.fromThread.drainTo(this.savedChunks);
            for (int i = 0; i < this.savedChunks.size(); ++i) {
                this.savedChunks.get(i).release();
            }
            this.savedChunks.clear();
        }
    }
    
    private class GetSquare implements IsoGridSquare.GetSquare
    {
        ServerMap.ServerCell cell;
        
        @Override
        public IsoGridSquare getGridSquare(int n, int n2, final int n3) {
            n -= this.cell.WX * 50;
            n2 -= this.cell.WY * 50;
            if (n < 0 || n >= 50) {
                return null;
            }
            if (n2 < 0 || n2 >= 50) {
                return null;
            }
            final IsoChunk isoChunk = this.cell.chunks[n / 10][n2 / 10];
            if (isoChunk == null) {
                return null;
            }
            return isoChunk.getGridSquare(n % 10, n2 % 10, n3);
        }
        
        public boolean contains(final int n, final int n2, final int n3) {
            return n >= 0 && n < 50 && n2 >= 0 && n2 < 50;
        }
        
        public IsoChunk getChunkForSquare(int n, int n2) {
            n -= this.cell.WX * 50;
            n2 -= this.cell.WY * 50;
            if (n < 0 || n >= 50) {
                return null;
            }
            if (n2 < 0 || n2 >= 50) {
                return null;
            }
            return this.cell.chunks[n / 10][n2 / 10];
        }
        
        public void EnsureSurroundNotNull(final int n, final int n2, final int n3) {
            final int n4 = this.cell.WX * 50;
            final int n5 = this.cell.WY * 50;
            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    if (i != 0 || j != 0) {
                        if (this.contains(n + i, n2 + j, n3)) {
                            if (this.getGridSquare(n4 + n + i, n5 + n2 + j, n3) == null) {
                                final IsoGridSquare new1 = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, null, n4 + n + i, n5 + n2 + j, n3);
                                final int n6 = (n + i) / 10;
                                final int n7 = (n2 + j) / 10;
                                final int n8 = (n + i) % 10;
                                final int n9 = (n2 + j) % 10;
                                if (this.cell.chunks[n6][n7] != null) {
                                    this.cell.chunks[n6][n7].setSquare(n8, n9, n3, new1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private class RecalcAllThread extends Thread
    {
        private final LinkedBlockingQueue<ServerMap.ServerCell> toThread;
        private final LinkedBlockingQueue<ServerMap.ServerCell> fromThread;
        private final GetSquare serverCellGetSquare;
        
        private RecalcAllThread() {
            this.toThread = new LinkedBlockingQueue<ServerMap.ServerCell>();
            this.fromThread = new LinkedBlockingQueue<ServerMap.ServerCell>();
            this.serverCellGetSquare = new GetSquare();
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        this.runInner();
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }
                break;
            }
        }
        
        private void runInner() throws InterruptedException {
            MPStatistic.getInstance().RecalcAllThread.End();
            final ServerMap.ServerCell serverCell = this.toThread.take();
            MPStatistic.getInstance().RecalcAllThread.Start();
            if (serverCell.bCancelLoading && !this.hasAnyBrandNewChunks(serverCell)) {
                for (int i = 0; i < 5; ++i) {
                    for (int j = 0; j < 5; ++j) {
                        final IsoChunk isoChunk = serverCell.chunks[j][i];
                        if (isoChunk != null) {
                            serverCell.chunks[j][i] = null;
                            WorldReuserThread.instance.addReuseChunk(isoChunk);
                        }
                    }
                }
                if (ServerChunkLoader.this.MapLoading) {
                    DebugLog.log(DebugType.MapLoading, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, serverCell.WX, serverCell.WY));
                }
                serverCell.bLoadingWasCancelled = true;
                return;
            }
            final long nanoTime = System.nanoTime();
            this.serverCellGetSquare.cell = serverCell;
            final int n = serverCell.WX * 50;
            final int n2 = serverCell.WY * 50;
            int maxLevel = 0;
            final int n3 = 100;
            for (int k = 0; k < 5; ++k) {
                for (int l = 0; l < 5; ++l) {
                    final IsoChunk isoChunk2 = serverCell.chunks[k][l];
                    if (isoChunk2 != null) {
                        isoChunk2.bLoaded = false;
                        for (int n4 = 0; n4 < n3; ++n4) {
                            for (int n5 = 0; n5 <= isoChunk2.maxLevel; ++n5) {
                                IsoGridSquare new1 = isoChunk2.squares[n5][n4];
                                if (n5 == 0) {
                                    if (new1 == null) {
                                        final int n6 = isoChunk2.wx * 10 + n4 % 10;
                                        final int n7 = isoChunk2.wy * 10 + n4 / 10;
                                        new1 = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, null, n6, n7, n5);
                                        isoChunk2.setSquare(n6 % 10, n7 % 10, n5, new1);
                                    }
                                    if (new1.getFloor() == null) {
                                        DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, new1.x, new1.y, new1.z));
                                        final IsoObject new2 = IsoObject.getNew();
                                        new2.sprite = IsoSprite.getSprite(IsoSpriteManager.instance, "carpentry_02_58", 0);
                                        new2.square = new1;
                                        new1.getObjects().add(0, new2);
                                    }
                                }
                                if (new1 != null) {
                                    new1.RecalcProperties();
                                }
                            }
                        }
                        if (isoChunk2.maxLevel > maxLevel) {
                            maxLevel = isoChunk2.maxLevel;
                        }
                    }
                }
            }
            for (int n8 = 0; n8 < 5; ++n8) {
                for (int n9 = 0; n9 < 5; ++n9) {
                    final IsoChunk isoChunk3 = serverCell.chunks[n8][n9];
                    if (isoChunk3 != null) {
                        for (int n10 = 0; n10 < n3; ++n10) {
                            for (int n11 = 0; n11 <= isoChunk3.maxLevel; ++n11) {
                                final IsoGridSquare isoGridSquare = isoChunk3.squares[n11][n10];
                                if (isoGridSquare != null) {
                                    if (n11 > 0 && !isoGridSquare.getObjects().isEmpty()) {
                                        this.serverCellGetSquare.EnsureSurroundNotNull(isoGridSquare.x - n, isoGridSquare.y - n2, n11);
                                    }
                                    isoGridSquare.RecalcAllWithNeighbours(true, this.serverCellGetSquare);
                                }
                            }
                        }
                    }
                }
            }
            for (int n12 = 0; n12 < 5; ++n12) {
                for (int n13 = 0; n13 < 5; ++n13) {
                    final IsoChunk isoChunk4 = serverCell.chunks[n12][n13];
                    if (isoChunk4 != null) {
                        for (int n14 = 0; n14 < n3; ++n14) {
                            for (int maxLevel2 = isoChunk4.maxLevel; maxLevel2 > 0; --maxLevel2) {
                                final IsoGridSquare isoGridSquare2 = isoChunk4.squares[maxLevel2][n14];
                                if (isoGridSquare2 != null && isoGridSquare2.Is(IsoFlagType.solidfloor)) {
                                    --maxLevel2;
                                    while (maxLevel2 >= 0) {
                                        final IsoGridSquare isoGridSquare3 = isoChunk4.squares[maxLevel2][n14];
                                        if (isoGridSquare3 != null) {
                                            isoGridSquare3.haveRoof = true;
                                            isoGridSquare3.getProperties().UnSet(IsoFlagType.exterior);
                                        }
                                        --maxLevel2;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (GameServer.bDebug && ServerChunkLoader.this.debugSlowMapLoadingDelay > 0L) {
                Thread.sleep(ServerChunkLoader.this.debugSlowMapLoadingDelay);
            }
            final float n15 = (System.nanoTime() - nanoTime) / 1000000.0f;
            if (ServerChunkLoader.this.MapLoading) {
                DebugLog.log(DebugType.MapLoading, invokedynamic(makeConcatWithConstants:(IIF)Ljava/lang/String;, serverCell.WX, serverCell.WY, n15));
            }
            this.fromThread.add(serverCell);
        }
        
        private boolean hasAnyBrandNewChunks(final ServerMap.ServerCell serverCell) {
            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 5; ++j) {
                    final IsoChunk isoChunk = serverCell.chunks[j][i];
                    if (isoChunk != null) {
                        if (!isoChunk.getErosionData().init) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }
    
    private interface SaveTask
    {
        void save() throws Exception;
        
        void release();
        
        int wx();
        
        int wy();
    }
}
