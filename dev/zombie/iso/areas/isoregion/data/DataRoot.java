// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion.data;

import zombie.core.Colors;
import zombie.iso.areas.isoregion.regions.IsoChunkRegion;
import zombie.iso.areas.isoregion.IsoRegions;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import java.util.ArrayList;
import zombie.iso.areas.isoregion.regions.IsoRegionManager;
import java.util.Map;

public final class DataRoot
{
    private final Map<Integer, DataCell> cellMap;
    public final SelectInfo select;
    private final SelectInfo selectInternal;
    public final IsoRegionManager regionManager;
    private final ArrayList<IsoWorldRegion> dirtyIsoWorldRegions;
    private final ArrayList<DataChunk> dirtyChunks;
    protected static int recalcs;
    protected static int floodFills;
    protected static int merges;
    private static final long[] t_start;
    private static final long[] t_end;
    private static final long[] t_time;
    
    public DataRoot() {
        this.cellMap = new HashMap<Integer, DataCell>();
        this.select = new SelectInfo(this);
        this.selectInternal = new SelectInfo(this);
        this.dirtyIsoWorldRegions = new ArrayList<IsoWorldRegion>();
        this.dirtyChunks = new ArrayList<DataChunk>();
        this.regionManager = new IsoRegionManager(this);
    }
    
    public void getAllChunks(final List<DataChunk> list) {
        final Iterator<Map.Entry<Integer, DataCell>> iterator = this.cellMap.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().getValue().getAllChunks(list);
        }
    }
    
    private DataCell getCell(final int i) {
        return this.cellMap.get(i);
    }
    
    private DataCell addCell(final int n, final int n2, final int i) {
        final DataCell dataCell = new DataCell(this, n, n2, i);
        this.cellMap.put(i, dataCell);
        return dataCell;
    }
    
    public DataChunk getDataChunk(final int n, final int n2) {
        final DataCell dataCell = this.cellMap.get(IsoRegions.hash(n / 30, n2 / 30));
        if (dataCell != null) {
            return dataCell.getChunk(IsoRegions.hash(n, n2));
        }
        return null;
    }
    
    private void setDataChunk(final DataChunk chunk) {
        final int hash = IsoRegions.hash(chunk.getChunkX() / 30, chunk.getChunkY() / 30);
        DataCell addCell = this.cellMap.get(hash);
        if (addCell == null) {
            addCell = this.addCell(chunk.getChunkX() / 30, chunk.getChunkY() / 30, hash);
        }
        addCell.setChunk(chunk);
    }
    
    public IsoWorldRegion getIsoWorldRegion(final int n, final int n2, final int n3) {
        this.selectInternal.reset(n, n2, n3, false);
        if (this.selectInternal.chunk != null) {
            final IsoChunkRegion isoChunkRegion = this.selectInternal.chunk.getIsoChunkRegion(this.selectInternal.chunkSquareX, this.selectInternal.chunkSquareY, n3);
            if (isoChunkRegion != null) {
                return isoChunkRegion.getIsoWorldRegion();
            }
        }
        return null;
    }
    
    public byte getSquareFlags(final int n, final int n2, final int n3) {
        this.selectInternal.reset(n, n2, n3, false);
        return this.selectInternal.square;
    }
    
    public IsoChunkRegion getIsoChunkRegion(final int n, final int n2, final int n3) {
        this.selectInternal.reset(n, n2, n3, false);
        if (this.selectInternal.chunk != null) {
            return this.selectInternal.chunk.getIsoChunkRegion(this.selectInternal.chunkSquareX, this.selectInternal.chunkSquareY, n3);
        }
        return null;
    }
    
    public void resetAllData() {
        final ArrayList<IsoWorldRegion> list = new ArrayList<IsoWorldRegion>();
        final Iterator<Map.Entry<Integer, DataCell>> iterator = this.cellMap.entrySet().iterator();
        while (iterator.hasNext()) {
            final DataCell dataCell = iterator.next().getValue();
            final Iterator<Map.Entry<Integer, DataChunk>> iterator2 = dataCell.dataChunks.entrySet().iterator();
            while (iterator2.hasNext()) {
                final DataChunk dataChunk = iterator2.next().getValue();
                for (int i = 0; i < 8; ++i) {
                    for (final IsoChunkRegion isoChunkRegion : dataChunk.getChunkRegions(i)) {
                        if (isoChunkRegion.getIsoWorldRegion() != null && !list.contains(isoChunkRegion.getIsoWorldRegion())) {
                            list.add(isoChunkRegion.getIsoWorldRegion());
                        }
                        isoChunkRegion.setIsoWorldRegion(null);
                        this.regionManager.releaseIsoChunkRegion(isoChunkRegion);
                    }
                }
            }
            dataCell.dataChunks.clear();
        }
        this.cellMap.clear();
        final Iterator<IsoWorldRegion> iterator4 = list.iterator();
        while (iterator4.hasNext()) {
            this.regionManager.releaseIsoWorldRegion(iterator4.next());
        }
    }
    
    public void EnqueueDirtyDataChunk(final DataChunk dataChunk) {
        if (!this.dirtyChunks.contains(dataChunk)) {
            this.dirtyChunks.add(dataChunk);
        }
    }
    
    public void EnqueueDirtyIsoWorldRegion(final IsoWorldRegion isoWorldRegion) {
        if (!this.dirtyIsoWorldRegions.contains(isoWorldRegion)) {
            this.dirtyIsoWorldRegions.add(isoWorldRegion);
        }
    }
    
    public void DequeueDirtyIsoWorldRegion(final IsoWorldRegion o) {
        this.dirtyIsoWorldRegions.remove(o);
    }
    
    public void updateExistingSquare(final int n, final int n2, final int n3, final byte b) {
        this.select.reset(n, n2, n3, false);
        if (this.select.chunk != null) {
            byte square = -1;
            if (this.select.square != -1) {
                square = this.select.square;
            }
            if (b == square) {
                return;
            }
            this.select.chunk.setOrAddSquare(this.select.chunkSquareX, this.select.chunkSquareY, this.select.z, b, true);
        }
        else {
            IsoRegions.warn("DataRoot.updateExistingSquare -> trying to change a square on a unknown chunk");
        }
    }
    
    public void processDirtyChunks() {
        if (this.dirtyChunks.size() > 0) {
            final long nanoTime = System.nanoTime();
            DataRoot.recalcs = 0;
            DataRoot.floodFills = 0;
            DataRoot.merges = 0;
            DataRoot.t_start[0] = System.nanoTime();
            for (int i = 0; i < this.dirtyChunks.size(); ++i) {
                this.dirtyChunks.get(i).recalculate();
                ++DataRoot.recalcs;
            }
            DataRoot.t_end[0] = System.nanoTime();
            DataRoot.t_start[1] = System.nanoTime();
            for (int j = 0; j < this.dirtyChunks.size(); ++j) {
                final DataChunk dataChunk = this.dirtyChunks.get(j);
                dataChunk.link(this.getDataChunk(dataChunk.getChunkX(), dataChunk.getChunkY() - 1), this.getDataChunk(dataChunk.getChunkX() - 1, dataChunk.getChunkY()), this.getDataChunk(dataChunk.getChunkX(), dataChunk.getChunkY() + 1), this.getDataChunk(dataChunk.getChunkX() + 1, dataChunk.getChunkY()));
            }
            DataRoot.t_end[1] = System.nanoTime();
            DataRoot.t_start[2] = System.nanoTime();
            for (int k = 0; k < this.dirtyChunks.size(); ++k) {
                this.dirtyChunks.get(k).interConnect();
            }
            DataRoot.t_end[2] = System.nanoTime();
            DataRoot.t_start[3] = System.nanoTime();
            for (int l = 0; l < this.dirtyChunks.size(); ++l) {
                final DataChunk dataChunk2 = this.dirtyChunks.get(l);
                dataChunk2.recalcRoofs();
                dataChunk2.unsetDirtyAll();
            }
            DataRoot.t_end[3] = System.nanoTime();
            DataRoot.t_start[4] = System.nanoTime();
            if (this.dirtyIsoWorldRegions.size() > 0) {
                for (int index = 0; index < this.dirtyIsoWorldRegions.size(); ++index) {
                    this.dirtyIsoWorldRegions.get(index).unlinkNeighbors();
                }
                for (int index2 = 0; index2 < this.dirtyIsoWorldRegions.size(); ++index2) {
                    this.dirtyIsoWorldRegions.get(index2).linkNeighbors();
                }
                this.dirtyIsoWorldRegions.clear();
            }
            DataRoot.t_end[4] = System.nanoTime();
            this.dirtyChunks.clear();
            final long n = System.nanoTime() - nanoTime;
            if (IsoRegions.PRINT_D) {
                DataRoot.t_time[0] = DataRoot.t_end[0] - DataRoot.t_start[0];
                DataRoot.t_time[1] = DataRoot.t_end[1] - DataRoot.t_start[1];
                DataRoot.t_time[2] = DataRoot.t_end[2] - DataRoot.t_start[2];
                DataRoot.t_time[3] = DataRoot.t_end[3] - DataRoot.t_start[3];
                DataRoot.t_time[4] = DataRoot.t_end[4] - DataRoot.t_start[4];
                IsoRegions.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;III)Ljava/lang/String;, String.format("%.6f", n / 1000000.0), String.format("%.6f", DataRoot.t_time[0] / 1000000.0), String.format("%.6f", DataRoot.t_time[1] / 1000000.0), String.format("%.6f", DataRoot.t_time[2] / 1000000.0), String.format("%.6f", DataRoot.t_time[3] / 1000000.0), String.format("%.6f", DataRoot.t_time[4] / 1000000.0), DataRoot.recalcs, DataRoot.merges, DataRoot.floodFills), Colors.CornFlowerBlue);
            }
        }
    }
    
    static {
        t_start = new long[5];
        t_end = new long[5];
        t_time = new long[5];
    }
    
    public static final class SelectInfo
    {
        public int x;
        public int y;
        public int z;
        public int chunkSquareX;
        public int chunkSquareY;
        public int chunkx;
        public int chunky;
        public int cellx;
        public int celly;
        public int chunkID;
        public int cellID;
        public DataCell cell;
        public DataChunk chunk;
        public byte square;
        private final DataRoot root;
        
        private SelectInfo(final DataRoot root) {
            this.root = root;
        }
        
        public void reset(final int n, final int n2, final int n3, final boolean b) {
            this.reset(n, n2, n3, b, b);
        }
        
        public void reset(final int x, final int y, final int z, final boolean b, final boolean b2) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.chunkSquareX = x % 10;
            this.chunkSquareY = y % 10;
            this.chunkx = x / 10;
            this.chunky = y / 10;
            this.cellx = x / 300;
            this.celly = y / 300;
            this.chunkID = IsoRegions.hash(this.chunkx, this.chunky);
            this.cellID = IsoRegions.hash(this.cellx, this.celly);
            this.cell = null;
            this.chunk = null;
            this.square = -1;
            this.ensureSquare(b2);
            if (this.chunk == null && b) {
                this.ensureChunk(b);
            }
        }
        
        private void ensureCell(final boolean b) {
            if (this.cell == null) {
                this.cell = this.root.getCell(this.cellID);
            }
            if (this.cell == null && b) {
                this.cell = this.root.addCell(this.cellx, this.celly, this.cellID);
            }
        }
        
        private void ensureChunk(final boolean b) {
            this.ensureCell(b);
            if (this.cell == null) {
                return;
            }
            if (this.chunk == null) {
                this.chunk = this.cell.getChunk(this.chunkID);
            }
            if (this.chunk == null && b) {
                this.chunk = this.cell.addChunk(this.chunkx, this.chunky, this.chunkID);
            }
        }
        
        private void ensureSquare(final boolean b) {
            this.ensureCell(b);
            if (this.cell == null) {
                return;
            }
            this.ensureChunk(b);
            if (this.chunk == null) {
                return;
            }
            if (this.square == -1) {
                this.square = this.chunk.getSquare(this.chunkSquareX, this.chunkSquareY, this.z, true);
            }
            if (this.square == -1 && b) {
                this.square = this.chunk.setOrAddSquare(this.chunkSquareX, this.chunkSquareY, this.z, (byte)0, true);
            }
        }
    }
}
