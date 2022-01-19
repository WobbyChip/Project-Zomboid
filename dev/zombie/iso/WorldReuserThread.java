// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.debug.DebugLog;
import zombie.core.Core;
import java.util.Collection;
import zombie.GameWindow;
import zombie.network.MPStatistic;
import zombie.core.ThreadGroups;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.iso.objects.IsoTree;
import java.util.ArrayList;

public final class WorldReuserThread
{
    public static final WorldReuserThread instance;
    private final ArrayList<IsoObject> objectsToReuse;
    private final ArrayList<IsoTree> treesToReuse;
    public boolean finished;
    private Thread worldReuser;
    private final ConcurrentLinkedQueue<IsoChunk> reuseGridSquares;
    
    public WorldReuserThread() {
        this.objectsToReuse = new ArrayList<IsoObject>();
        this.treesToReuse = new ArrayList<IsoTree>();
        this.reuseGridSquares = new ConcurrentLinkedQueue<IsoChunk>();
    }
    
    public void run() {
        (this.worldReuser = new Thread(ThreadGroups.Workers, () -> {
            while (!this.finished) {
                MPStatistic.getInstance().WorldReuser.Start();
                this.testReuseChunk();
                this.reconcileReuseObjects();
                MPStatistic.getInstance().WorldReuser.End();
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            return;
        })).setName("WorldReuser");
        this.worldReuser.setDaemon(true);
        this.worldReuser.setUncaughtExceptionHandler(GameWindow::uncaughtException);
        this.worldReuser.start();
    }
    
    public void reconcileReuseObjects() {
        synchronized (this.objectsToReuse) {
            if (!this.objectsToReuse.isEmpty()) {
                synchronized (CellLoader.isoObjectCache) {
                    if (CellLoader.isoObjectCache.size() < 320000) {
                        CellLoader.isoObjectCache.addAll(this.objectsToReuse);
                    }
                }
                this.objectsToReuse.clear();
            }
        }
        synchronized (this.treesToReuse) {
            if (!this.treesToReuse.isEmpty()) {
                synchronized (CellLoader.isoTreeCache) {
                    if (CellLoader.isoTreeCache.size() < 40000) {
                        CellLoader.isoTreeCache.addAll(this.treesToReuse);
                    }
                }
                this.treesToReuse.clear();
            }
        }
    }
    
    public void testReuseChunk() {
        for (IsoChunk isoChunk = this.reuseGridSquares.poll(); isoChunk != null; isoChunk = this.reuseGridSquares.poll()) {
            if (Core.bDebug) {
                if (ChunkSaveWorker.instance.toSaveQueue.contains(isoChunk)) {
                    DebugLog.log("ERROR: reusing chunk that needs to be saved");
                }
                if (IsoChunkMap.chunkStore.contains(isoChunk)) {
                    DebugLog.log("ERROR: reusing chunk in chunkStore");
                }
                if (!isoChunk.refs.isEmpty()) {
                    DebugLog.log("ERROR: reusing chunk with refs");
                }
            }
            if (Core.bDebug) {}
            this.reuseGridSquares(isoChunk);
            if (this.treesToReuse.size() > 1000 || this.objectsToReuse.size() > 5000) {
                this.reconcileReuseObjects();
            }
        }
    }
    
    public void addReuseChunk(final IsoChunk e) {
        this.reuseGridSquares.add(e);
    }
    
    public void reuseGridSquares(final IsoChunk e) {
        final int n = 100;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < n; ++j) {
                final IsoGridSquare isoGridSquare = e.squares[i][j];
                if (isoGridSquare != null) {
                    for (int k = 0; k < isoGridSquare.getObjects().size(); ++k) {
                        final IsoObject e2 = isoGridSquare.getObjects().get(k);
                        if (e2 instanceof IsoTree) {
                            e2.reset();
                            synchronized (this.treesToReuse) {
                                this.treesToReuse.add((IsoTree)e2);
                            }
                        }
                        else if (((IsoTree)e2).getClass() == IsoObject.class) {
                            e2.reset();
                            synchronized (this.objectsToReuse) {
                                this.objectsToReuse.add(e2);
                            }
                        }
                        else {
                            e2.reuseGridSquare();
                        }
                    }
                    isoGridSquare.discard();
                    e.squares[i][j] = null;
                }
            }
        }
        e.resetForStore();
        IsoChunkMap.chunkStore.add(e);
    }
    
    static {
        instance = new WorldReuserThread();
    }
}
