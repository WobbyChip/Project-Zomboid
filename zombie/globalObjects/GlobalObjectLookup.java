// 
// Decompiled by Procyon v0.5.36
// 

package zombie.globalObjects;

import java.util.Arrays;
import zombie.network.GameClient;
import zombie.network.GameServer;
import java.util.ArrayList;
import zombie.debug.DebugLog;
import zombie.iso.IsoMetaGrid;

public final class GlobalObjectLookup
{
    private static final int SQUARES_PER_CHUNK = 10;
    private static final int SQUARES_PER_CELL = 300;
    private static final int CHUNKS_PER_CELL = 30;
    private static IsoMetaGrid metaGrid;
    private static final Shared sharedServer;
    private static final Shared sharedClient;
    private final GlobalObjectSystem system;
    private final Shared shared;
    private final Cell[] cells;
    
    public GlobalObjectLookup(final GlobalObjectSystem system) {
        this.system = system;
        this.shared = ((system instanceof SGlobalObjectSystem) ? GlobalObjectLookup.sharedServer : GlobalObjectLookup.sharedClient);
        this.cells = this.shared.cells;
    }
    
    private Cell getCellAt(final int n, final int n2, final boolean b) {
        final int n3 = n - GlobalObjectLookup.metaGrid.minX * 300;
        final int n4 = n2 - GlobalObjectLookup.metaGrid.minY * 300;
        if (n3 < 0 || n4 < 0 || n3 >= GlobalObjectLookup.metaGrid.getWidth() * 300 || n4 >= GlobalObjectLookup.metaGrid.getHeight() * 300) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n, n2));
            return null;
        }
        final int n5 = n3 / 300;
        final int n6 = n4 / 300;
        final int n7 = n5 + n6 * GlobalObjectLookup.metaGrid.getWidth();
        if (this.cells[n7] == null && b) {
            this.cells[n7] = new Cell(GlobalObjectLookup.metaGrid.minX + n5, GlobalObjectLookup.metaGrid.minY + n6);
        }
        return this.cells[n7];
    }
    
    private Cell getCellForObject(final GlobalObject globalObject, final boolean b) {
        return this.getCellAt(globalObject.x, globalObject.y, b);
    }
    
    private Chunk getChunkForChunkPos(final int n, final int n2, final boolean b) {
        final Cell cell = this.getCellAt(n * 10, n2 * 10, b);
        if (cell == null) {
            return null;
        }
        return cell.getChunkAt(n * 10, n2 * 10, b);
    }
    
    public void addObject(final GlobalObject globalObject) {
        final Cell cellForObject = this.getCellForObject(globalObject, true);
        if (cellForObject == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, globalObject.x, globalObject.y));
            return;
        }
        cellForObject.addObject(globalObject);
    }
    
    public void removeObject(final GlobalObject globalObject) {
        final Cell cellForObject = this.getCellForObject(globalObject, false);
        if (cellForObject == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, globalObject.x, globalObject.y));
            return;
        }
        cellForObject.removeObject(globalObject);
    }
    
    public GlobalObject getObjectAt(final int n, final int n2, final int n3) {
        final Cell cell = this.getCellAt(n, n2, false);
        if (cell == null) {
            return null;
        }
        final Chunk chunk = cell.getChunkAt(n, n2, false);
        if (chunk == null) {
            return null;
        }
        for (int i = 0; i < chunk.objects.size(); ++i) {
            final GlobalObject globalObject = chunk.objects.get(i);
            if (globalObject.system == this.system && globalObject.x == n && globalObject.y == n2 && globalObject.z == n3) {
                return globalObject;
            }
        }
        return null;
    }
    
    public boolean hasObjectsInChunk(final int n, final int n2) {
        final Chunk chunkForChunkPos = this.getChunkForChunkPos(n, n2, false);
        if (chunkForChunkPos == null) {
            return false;
        }
        for (int i = 0; i < chunkForChunkPos.objects.size(); ++i) {
            if (chunkForChunkPos.objects.get(i).system == this.system) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<GlobalObject> getObjectsInChunk(final int n, final int n2, final ArrayList<GlobalObject> list) {
        final Chunk chunkForChunkPos = this.getChunkForChunkPos(n, n2, false);
        if (chunkForChunkPos == null) {
            return list;
        }
        for (int i = 0; i < chunkForChunkPos.objects.size(); ++i) {
            final GlobalObject e = chunkForChunkPos.objects.get(i);
            if (e.system == this.system) {
                list.add(e);
            }
        }
        return list;
    }
    
    public ArrayList<GlobalObject> getObjectsAdjacentTo(final int n, final int n2, final int n3, final ArrayList<GlobalObject> list) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                final GlobalObject object = this.getObjectAt(n + j, n2 + i, n3);
                if (object != null && object.system == this.system) {
                    list.add(object);
                }
            }
        }
        return list;
    }
    
    public static void init(final IsoMetaGrid metaGrid) {
        GlobalObjectLookup.metaGrid = metaGrid;
        if (GameServer.bServer) {
            GlobalObjectLookup.sharedServer.init(metaGrid);
        }
        else if (GameClient.bClient) {
            GlobalObjectLookup.sharedClient.init(metaGrid);
        }
        else {
            GlobalObjectLookup.sharedServer.init(metaGrid);
            GlobalObjectLookup.sharedClient.init(metaGrid);
        }
    }
    
    public static void Reset() {
        GlobalObjectLookup.sharedServer.reset();
        GlobalObjectLookup.sharedClient.reset();
    }
    
    static {
        sharedServer = new Shared();
        sharedClient = new Shared();
    }
    
    private static final class Chunk
    {
        final ArrayList<GlobalObject> objects;
        
        private Chunk() {
            this.objects = new ArrayList<GlobalObject>();
        }
        
        void Reset() {
            this.objects.clear();
        }
    }
    
    private static final class Cell
    {
        final int cx;
        final int cy;
        final Chunk[] chunks;
        
        Cell(final int cx, final int cy) {
            this.chunks = new Chunk[900];
            this.cx = cx;
            this.cy = cy;
        }
        
        Chunk getChunkAt(final int n, final int n2, final boolean b) {
            final int n3 = (n - this.cx * 300) / 10 + (n2 - this.cy * 300) / 10 * 30;
            if (this.chunks[n3] == null && b) {
                this.chunks[n3] = new Chunk();
            }
            return this.chunks[n3];
        }
        
        Chunk getChunkForObject(final GlobalObject globalObject, final boolean b) {
            return this.getChunkAt(globalObject.x, globalObject.y, b);
        }
        
        void addObject(final GlobalObject globalObject) {
            final Chunk chunkForObject = this.getChunkForObject(globalObject, true);
            if (chunkForObject.objects.contains(globalObject)) {
                throw new IllegalStateException("duplicate object");
            }
            chunkForObject.objects.add(globalObject);
        }
        
        void removeObject(final GlobalObject globalObject) {
            final Chunk chunkForObject = this.getChunkForObject(globalObject, false);
            if (chunkForObject == null || !chunkForObject.objects.contains(globalObject)) {
                throw new IllegalStateException("chunk doesn't contain object");
            }
            chunkForObject.objects.remove(globalObject);
        }
        
        void Reset() {
            for (int i = 0; i < this.chunks.length; ++i) {
                final Chunk chunk = this.chunks[i];
                if (chunk != null) {
                    chunk.Reset();
                }
            }
            Arrays.fill(this.chunks, null);
        }
    }
    
    private static final class Shared
    {
        Cell[] cells;
        
        void init(final IsoMetaGrid isoMetaGrid) {
            this.cells = new Cell[isoMetaGrid.getWidth() * isoMetaGrid.getHeight()];
        }
        
        void reset() {
            if (this.cells == null) {
                return;
            }
            for (int i = 0; i < this.cells.length; ++i) {
                final Cell cell = this.cells[i];
                if (cell != null) {
                    cell.Reset();
                }
            }
            this.cells = null;
        }
    }
}
