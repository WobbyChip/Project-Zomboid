// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.network.MPStatistic;
import java.util.ArrayDeque;
import java.util.Queue;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoChunk;
import zombie.gameStates.IngameState;
import zombie.popman.ZombiePopulationManager;
import zombie.network.GameServer;
import zombie.iso.IsoMetaChunk;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoLot;
import zombie.iso.IsoWorld;
import zombie.core.Core;
import zombie.network.GameClient;
import zombie.iso.IsoMetaGrid;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Stack;

public final class MapCollisionData
{
    public static final MapCollisionData instance;
    public static final byte BIT_SOLID = 1;
    public static final byte BIT_WALLN = 2;
    public static final byte BIT_WALLW = 4;
    public static final byte BIT_WATER = 8;
    public static final byte BIT_ROOM = 16;
    private static final int SQUARES_PER_CHUNK = 10;
    private static final int CHUNKS_PER_CELL = 30;
    private static final int SQUARES_PER_CELL = 300;
    private static int[] curXY;
    public final Object renderLock;
    private final Stack<PathTask> freePathTasks;
    private final ConcurrentLinkedQueue<PathTask> pathTaskQueue;
    private final ConcurrentLinkedQueue<PathTask> pathResultQueue;
    private final Sync sync;
    private final byte[] squares;
    private final int SQUARE_UPDATE_SIZE = 9;
    private final ByteBuffer squareUpdateBuffer;
    private boolean bClient;
    private boolean bPaused;
    private boolean bNoSave;
    private MCDThread thread;
    private long lastUpdate;
    
    public MapCollisionData() {
        this.renderLock = new Object();
        this.freePathTasks = new Stack<PathTask>();
        this.pathTaskQueue = new ConcurrentLinkedQueue<PathTask>();
        this.pathResultQueue = new ConcurrentLinkedQueue<PathTask>();
        this.sync = new Sync();
        this.squares = new byte[100];
        this.squareUpdateBuffer = ByteBuffer.allocateDirect(1024);
    }
    
    private static native void n_init(final int p0, final int p1, final int p2, final int p3);
    
    private static native void n_chunkUpdateTask(final int p0, final int p1, final byte[] p2);
    
    private static native void n_squareUpdateTask(final int p0, final ByteBuffer p1);
    
    private static native int n_pathTask(final int p0, final int p1, final int p2, final int p3, final int[] p4);
    
    private static native boolean n_hasDataForThread();
    
    private static native boolean n_shouldWait();
    
    private static native void n_update();
    
    private static native void n_save();
    
    private static native void n_stop();
    
    private static native void n_setGameState(final String p0, final boolean p1);
    
    private static native void n_setGameState(final String p0, final double p1);
    
    private static native void n_setGameState(final String p0, final float p1);
    
    private static native void n_setGameState(final String p0, final int p1);
    
    private static native void n_setGameState(final String p0, final String p1);
    
    private static native void n_initMetaGrid(final int p0, final int p1, final int p2, final int p3);
    
    private static native void n_initMetaCell(final int p0, final int p1, final String p2);
    
    private static native void n_initMetaChunk(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    private static void writeToStdErr(final String x) {
        System.err.println(x);
    }
    
    public void init(final IsoMetaGrid isoMetaGrid) {
        this.bClient = GameClient.bClient;
        if (this.bClient) {
            return;
        }
        final int minX = isoMetaGrid.getMinX();
        final int minY = isoMetaGrid.getMinY();
        final int width = isoMetaGrid.getWidth();
        final int height = isoMetaGrid.getHeight();
        n_setGameState("Core.GameMode", Core.getInstance().getGameMode());
        n_setGameState("Core.GameSaveWorld", Core.GameSaveWorld);
        n_setGameState("Core.bLastStand", Core.bLastStand);
        n_setGameState("Core.noSave", this.bNoSave = Core.getInstance().isNoSave());
        n_setGameState("GameWindow.CacheDir", ZomboidFileSystem.instance.getCacheDir());
        n_setGameState("GameWindow.GameModeCacheDir", ZomboidFileSystem.instance.getGameModeCacheDir());
        n_setGameState("GameWindow.SaveDir", ZomboidFileSystem.instance.getSaveDir());
        n_setGameState("SandboxOptions.Distribution", SandboxOptions.instance.Distribution.getValue());
        n_setGameState("SandboxOptions.Zombies", SandboxOptions.instance.Zombies.getValue());
        n_setGameState("World.ZombiesDisabled", IsoWorld.getZombiesDisabled());
        n_setGameState("PAUSED", this.bPaused = true);
        n_initMetaGrid(minX, minY, width, height);
        for (int i = minY; i < minY + height; ++i) {
            for (int j = minX; j < minX + width; ++j) {
                final IsoMetaCell cellData = isoMetaGrid.getCellData(j, i);
                n_initMetaCell(j, i, IsoLot.InfoFileNames.get(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, j, i)));
                if (cellData != null) {
                    for (int k = 0; k < 30; ++k) {
                        for (int l = 0; l < 30; ++l) {
                            final IsoMetaChunk chunk = cellData.getChunk(l, k);
                            if (chunk != null) {
                                n_initMetaChunk(j, i, l, k, chunk.getUnadjustedZombieIntensity());
                            }
                        }
                    }
                }
            }
        }
        n_init(minX, minY, width, height);
    }
    
    public void start() {
        if (this.bClient) {
            return;
        }
        if (this.thread != null) {
            return;
        }
        (this.thread = new MCDThread()).setDaemon(true);
        this.thread.setName("MapCollisionDataJNI");
        if (GameServer.bServer) {
            this.thread.start();
        }
    }
    
    public void startGame() {
        if (GameClient.bClient) {
            return;
        }
        this.updateMain();
        ZombiePopulationManager.instance.updateMain();
        n_update();
        ZombiePopulationManager.instance.updateThread();
        this.updateMain();
        ZombiePopulationManager.instance.updateMain();
        this.thread.start();
    }
    
    public void updateMain() {
        if (this.bClient) {
            return;
        }
        for (PathTask pathTask = this.pathResultQueue.poll(); pathTask != null; pathTask = this.pathResultQueue.poll()) {
            pathTask.result.finished(pathTask.status, pathTask.curX, pathTask.curY);
            pathTask.release();
        }
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastUpdate > 10000L) {
            this.lastUpdate = currentTimeMillis;
            this.notifyThread();
        }
    }
    
    public boolean hasDataForThread() {
        if (this.squareUpdateBuffer.position() > 0) {
            try {
                n_squareUpdateTask(this.squareUpdateBuffer.position() / 9, this.squareUpdateBuffer);
            }
            finally {
                this.squareUpdateBuffer.clear();
            }
        }
        return n_hasDataForThread();
    }
    
    public void updateGameState() {
        final boolean noSave = Core.getInstance().isNoSave();
        if (this.bNoSave != noSave) {
            n_setGameState("Core.noSave", this.bNoSave = noSave);
        }
        boolean bPaused = GameTime.isGamePaused();
        if (GameWindow.states.current != IngameState.instance) {
            bPaused = true;
        }
        if (GameServer.bServer) {
            bPaused = IngameState.instance.Paused;
        }
        if (bPaused != this.bPaused) {
            n_setGameState("PAUSED", this.bPaused = bPaused);
        }
    }
    
    public void notifyThread() {
        synchronized (this.thread.notifier) {
            this.thread.notifier.notify();
        }
    }
    
    public void addChunkToWorld(final IsoChunk isoChunk) {
        if (this.bClient) {
            return;
        }
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                final IsoGridSquare gridSquare = isoChunk.getGridSquare(j, i, 0);
                if (gridSquare == null) {
                    this.squares[j + i * 10] = 1;
                }
                else {
                    byte b = 0;
                    if (this.isSolid(gridSquare)) {
                        b |= 0x1;
                    }
                    if (this.isBlockedN(gridSquare)) {
                        b |= 0x2;
                    }
                    if (this.isBlockedW(gridSquare)) {
                        b |= 0x4;
                    }
                    if (this.isWater(gridSquare)) {
                        b |= 0x8;
                    }
                    if (this.isRoom(gridSquare)) {
                        b |= 0x10;
                    }
                    this.squares[j + i * 10] = b;
                }
            }
        }
        n_chunkUpdateTask(isoChunk.wx, isoChunk.wy, this.squares);
    }
    
    public void removeChunkFromWorld(final IsoChunk isoChunk) {
        if (this.bClient) {
            return;
        }
    }
    
    public void squareChanged(final IsoGridSquare isoGridSquare) {
        if (this.bClient) {
            return;
        }
        try {
            byte b = 0;
            if (this.isSolid(isoGridSquare)) {
                b |= 0x1;
            }
            if (this.isBlockedN(isoGridSquare)) {
                b |= 0x2;
            }
            if (this.isBlockedW(isoGridSquare)) {
                b |= 0x4;
            }
            if (this.isWater(isoGridSquare)) {
                b |= 0x8;
            }
            if (this.isRoom(isoGridSquare)) {
                b |= 0x10;
            }
            this.squareUpdateBuffer.putInt(isoGridSquare.x);
            this.squareUpdateBuffer.putInt(isoGridSquare.y);
            this.squareUpdateBuffer.put(b);
            if (this.squareUpdateBuffer.remaining() < 9) {
                n_squareUpdateTask(this.squareUpdateBuffer.position() / 9, this.squareUpdateBuffer);
                this.squareUpdateBuffer.clear();
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public void save() {
        if (this.bClient) {
            return;
        }
        ZombiePopulationManager.instance.beginSaveRealZombies();
        if (!this.thread.isAlive()) {
            n_save();
            ZombiePopulationManager.instance.save();
            return;
        }
        this.thread.bSave = true;
        synchronized (this.thread.notifier) {
            this.thread.notifier.notify();
        }
        while (this.thread.bSave) {
            try {
                Thread.sleep(5L);
            }
            catch (InterruptedException ex) {}
        }
        ZombiePopulationManager.instance.endSaveRealZombies();
    }
    
    public void stop() {
        if (this.bClient) {
            return;
        }
        this.thread.bStop = true;
        synchronized (this.thread.notifier) {
            this.thread.notifier.notify();
        }
        while (this.thread.isAlive()) {
            try {
                Thread.sleep(5L);
            }
            catch (InterruptedException ex) {}
        }
        n_stop();
        this.thread = null;
        this.pathTaskQueue.clear();
        this.pathResultQueue.clear();
        this.squareUpdateBuffer.clear();
    }
    
    private boolean isSolid(final IsoGridSquare isoGridSquare) {
        boolean b = isoGridSquare.isSolid() || isoGridSquare.isSolidTrans();
        if (isoGridSquare.HasStairs()) {
            b = true;
        }
        if (isoGridSquare.Is(IsoFlagType.water)) {
            b = false;
        }
        if (isoGridSquare.Has(IsoObjectType.tree)) {
            b = false;
        }
        return b;
    }
    
    private boolean isBlockedN(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare.Is(IsoFlagType.HoppableN)) {
            return false;
        }
        boolean is = isoGridSquare.Is(IsoFlagType.collideN);
        if (isoGridSquare.Has(IsoObjectType.doorFrN)) {
            is = true;
        }
        if (isoGridSquare.getProperties().Is(IsoFlagType.DoorWallN)) {
            is = true;
        }
        if (isoGridSquare.Has(IsoObjectType.windowFN)) {
            is = true;
        }
        if (isoGridSquare.Is(IsoFlagType.windowN)) {
            is = true;
        }
        if (isoGridSquare.getProperties().Is(IsoFlagType.WindowN)) {
            is = true;
        }
        return is;
    }
    
    private boolean isBlockedW(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare.Is(IsoFlagType.HoppableW)) {
            return false;
        }
        boolean is = isoGridSquare.Is(IsoFlagType.collideW);
        if (isoGridSquare.Has(IsoObjectType.doorFrW)) {
            is = true;
        }
        if (isoGridSquare.getProperties().Is(IsoFlagType.DoorWallW)) {
            is = true;
        }
        if (isoGridSquare.Has(IsoObjectType.windowFW)) {
            is = true;
        }
        if (isoGridSquare.Is(IsoFlagType.windowW)) {
            is = true;
        }
        if (isoGridSquare.getProperties().Is(IsoFlagType.WindowW)) {
            is = true;
        }
        return is;
    }
    
    private boolean isWater(final IsoGridSquare isoGridSquare) {
        return isoGridSquare.Is(IsoFlagType.water);
    }
    
    private boolean isRoom(final IsoGridSquare isoGridSquare) {
        return isoGridSquare.getRoom() != null;
    }
    
    static {
        instance = new MapCollisionData();
        MapCollisionData.curXY = new int[2];
    }
    
    static class Sync
    {
        private int fps;
        private long period;
        private long excess;
        private long beforeTime;
        private long overSleepTime;
        
        Sync() {
            this.fps = 10;
            this.period = 1000000000L / this.fps;
            this.beforeTime = System.nanoTime();
            this.overSleepTime = 0L;
        }
        
        void begin() {
            this.beforeTime = System.nanoTime();
            this.overSleepTime = 0L;
        }
        
        void startFrame() {
            this.excess = 0L;
        }
        
        void endFrame() {
            final long nanoTime = System.nanoTime();
            final long n = this.period - (nanoTime - this.beforeTime) - this.overSleepTime;
            if (n > 0L) {
                try {
                    Thread.sleep(n / 1000000L);
                }
                catch (InterruptedException ex) {}
                this.overSleepTime = System.nanoTime() - nanoTime - n;
            }
            else {
                this.excess -= n;
                this.overSleepTime = 0L;
            }
            this.beforeTime = System.nanoTime();
        }
    }
    
    private final class PathTask
    {
        public int startX;
        public int startY;
        public int endX;
        public int endY;
        public int curX;
        public int curY;
        public int status;
        public IPathResult result;
        public boolean myThread;
        
        public void init(final int startX, final int startY, final int endX, final int endY, final IPathResult result) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.status = 0;
            this.result = result;
        }
        
        public void execute() {
            this.status = MapCollisionData.n_pathTask(this.startX, this.startY, this.endX, this.endY, MapCollisionData.curXY);
            this.curX = MapCollisionData.curXY[0];
            this.curY = MapCollisionData.curXY[1];
            if (this.myThread) {
                this.result.finished(this.status, this.curX, this.curY);
            }
            else {
                MapCollisionData.this.pathResultQueue.add(this);
            }
        }
        
        public void release() {
            MapCollisionData.this.freePathTasks.push(this);
        }
    }
    
    private final class MCDThread extends Thread
    {
        public final Object notifier;
        public boolean bStop;
        public volatile boolean bSave;
        public volatile boolean bWaiting;
        public Queue<PathTask> pathTasks;
        
        private MCDThread() {
            this.notifier = new Object();
            this.pathTasks = new ArrayDeque<PathTask>();
        }
        
        @Override
        public void run() {
            while (!this.bStop) {
                try {
                    this.runInner();
                }
                catch (Exception ex) {
                    ExceptionLogger.logException(ex);
                }
            }
        }
        
        private void runInner() {
            MPStatistic.getInstance().MapCollisionThread.Start();
            MapCollisionData.this.sync.startFrame();
            synchronized (MapCollisionData.this.renderLock) {
                for (PathTask pathTask = MapCollisionData.this.pathTaskQueue.poll(); pathTask != null; pathTask = MapCollisionData.this.pathTaskQueue.poll()) {
                    pathTask.execute();
                    pathTask.release();
                }
                if (this.bSave) {
                    MapCollisionData.n_save();
                    ZombiePopulationManager.instance.save();
                    this.bSave = false;
                }
                MapCollisionData.n_update();
                ZombiePopulationManager.instance.updateThread();
            }
            MapCollisionData.this.sync.endFrame();
            MPStatistic.getInstance().MapCollisionThread.End();
            while (this.shouldWait()) {
                synchronized (this.notifier) {
                    this.bWaiting = true;
                    try {
                        this.notifier.wait();
                    }
                    catch (InterruptedException ex) {}
                }
            }
            this.bWaiting = false;
        }
        
        private boolean shouldWait() {
            return !this.bStop && !this.bSave && MapCollisionData.n_shouldWait() && ZombiePopulationManager.instance.shouldWait() && MapCollisionData.this.pathTaskQueue.isEmpty() && this.pathTasks.isEmpty();
        }
    }
    
    public interface IPathResult
    {
        void finished(final int p0, final int p1, final int p2);
    }
}
