// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion.regions;

import java.util.Collection;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.core.Core;
import java.util.ArrayList;
import zombie.core.Color;

public final class IsoChunkRegion implements IChunkRegion
{
    private final IsoRegionManager manager;
    private boolean isInPool;
    private Color color;
    private int ID;
    private byte zLayer;
    private byte squareSize;
    private byte roofCnt;
    private byte chunkBorderSquaresCnt;
    private final boolean[] enclosed;
    private boolean enclosedCache;
    private final ArrayList<IsoChunkRegion> connectedNeighbors;
    private final ArrayList<IsoChunkRegion> allNeighbors;
    private boolean isDirtyEnclosed;
    private IsoWorldRegion isoWorldRegion;
    
    public int getID() {
        return this.ID;
    }
    
    public int getSquareSize() {
        return this.squareSize;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public int getzLayer() {
        return this.zLayer;
    }
    
    public IsoWorldRegion getIsoWorldRegion() {
        return this.isoWorldRegion;
    }
    
    public void setIsoWorldRegion(final IsoWorldRegion isoWorldRegion) {
        this.isoWorldRegion = isoWorldRegion;
    }
    
    protected boolean isInPool() {
        return this.isInPool;
    }
    
    protected IsoChunkRegion(final IsoRegionManager manager) {
        this.isInPool = false;
        this.squareSize = 0;
        this.roofCnt = 0;
        this.chunkBorderSquaresCnt = 0;
        this.enclosed = new boolean[4];
        this.enclosedCache = true;
        this.connectedNeighbors = new ArrayList<IsoChunkRegion>();
        this.allNeighbors = new ArrayList<IsoChunkRegion>();
        this.isDirtyEnclosed = false;
        this.manager = manager;
    }
    
    protected void init(final int id, final int n) {
        this.isInPool = false;
        this.ID = id;
        this.zLayer = (byte)n;
        this.resetChunkBorderSquaresCnt();
        if (this.color == null) {
            this.color = this.manager.getColor();
        }
        this.squareSize = 0;
        this.roofCnt = 0;
        this.resetEnclosed();
    }
    
    protected IsoChunkRegion reset() {
        this.isInPool = true;
        this.unlinkNeighbors();
        final IsoWorldRegion unlinkFromIsoWorldRegion = this.unlinkFromIsoWorldRegion();
        if (unlinkFromIsoWorldRegion != null && unlinkFromIsoWorldRegion.size() <= 0) {
            if (Core.bDebug) {
                throw new RuntimeException("ChunkRegion.reset IsoChunkRegion has IsoWorldRegion with 0 members.");
            }
            this.manager.releaseIsoWorldRegion(unlinkFromIsoWorldRegion);
            IsoRegions.warn("ChunkRegion.reset IsoChunkRegion has IsoWorldRegion with 0 members.");
        }
        this.resetChunkBorderSquaresCnt();
        this.ID = -1;
        this.squareSize = 0;
        this.roofCnt = 0;
        this.resetEnclosed();
        return this;
    }
    
    public IsoWorldRegion unlinkFromIsoWorldRegion() {
        if (this.isoWorldRegion != null) {
            final IsoWorldRegion isoWorldRegion = this.isoWorldRegion;
            this.isoWorldRegion.removeIsoChunkRegion(this);
            this.isoWorldRegion = null;
            return isoWorldRegion;
        }
        return null;
    }
    
    public int getRoofCnt() {
        return this.roofCnt;
    }
    
    public void addRoof() {
        ++this.roofCnt;
        if (this.roofCnt > this.squareSize) {
            IsoRegions.warn("ChunkRegion.addRoof roofCount exceed squareSize.");
            this.roofCnt = this.squareSize;
            return;
        }
        if (this.isoWorldRegion != null) {
            this.isoWorldRegion.addRoof();
        }
    }
    
    public void resetRoofCnt() {
        if (this.isoWorldRegion != null) {
            this.isoWorldRegion.removeRoofs(this.roofCnt);
        }
        this.roofCnt = 0;
    }
    
    public void addSquareCount() {
        ++this.squareSize;
    }
    
    public int getChunkBorderSquaresCnt() {
        return this.chunkBorderSquaresCnt;
    }
    
    public void addChunkBorderSquaresCnt() {
        ++this.chunkBorderSquaresCnt;
    }
    
    protected void removeChunkBorderSquaresCnt() {
        --this.chunkBorderSquaresCnt;
        if (this.chunkBorderSquaresCnt < 0) {
            this.chunkBorderSquaresCnt = 0;
        }
    }
    
    protected void resetChunkBorderSquaresCnt() {
        this.chunkBorderSquaresCnt = 0;
    }
    
    private void resetEnclosed() {
        for (int i = 0; i < 4; i = (byte)(i + 1)) {
            this.enclosed[i] = true;
        }
        this.isDirtyEnclosed = false;
        this.enclosedCache = true;
    }
    
    public void setEnclosed(final byte b, final boolean b2) {
        this.isDirtyEnclosed = true;
        this.enclosed[b] = b2;
    }
    
    protected void setDirtyEnclosed() {
        this.isDirtyEnclosed = true;
        if (this.isoWorldRegion != null) {
            this.isoWorldRegion.setDirtyEnclosed();
        }
    }
    
    public boolean getIsEnclosed() {
        if (!this.isDirtyEnclosed) {
            return this.enclosedCache;
        }
        this.isDirtyEnclosed = false;
        this.enclosedCache = true;
        for (int i = 0; i < 4; i = (byte)(i + 1)) {
            if (!this.enclosed[i]) {
                this.enclosedCache = false;
            }
        }
        if (this.isoWorldRegion != null) {
            this.isoWorldRegion.setDirtyEnclosed();
        }
        return this.enclosedCache;
    }
    
    public ArrayList<IsoChunkRegion> getConnectedNeighbors() {
        return this.connectedNeighbors;
    }
    
    public void addConnectedNeighbor(final IsoChunkRegion isoChunkRegion) {
        if (isoChunkRegion == null) {
            return;
        }
        if (!this.connectedNeighbors.contains(isoChunkRegion)) {
            this.connectedNeighbors.add(isoChunkRegion);
        }
    }
    
    protected void removeConnectedNeighbor(final IsoChunkRegion o) {
        this.connectedNeighbors.remove(o);
    }
    
    public int getNeighborCount() {
        return this.allNeighbors.size();
    }
    
    protected ArrayList<IsoChunkRegion> getAllNeighbors() {
        return this.allNeighbors;
    }
    
    public void addNeighbor(final IsoChunkRegion isoChunkRegion) {
        if (isoChunkRegion == null) {
            return;
        }
        if (!this.allNeighbors.contains(isoChunkRegion)) {
            this.allNeighbors.add(isoChunkRegion);
        }
    }
    
    protected void removeNeighbor(final IsoChunkRegion o) {
        this.allNeighbors.remove(o);
    }
    
    protected void unlinkNeighbors() {
        for (int i = 0; i < this.connectedNeighbors.size(); ++i) {
            this.connectedNeighbors.get(i).removeConnectedNeighbor(this);
        }
        this.connectedNeighbors.clear();
        for (int j = 0; j < this.allNeighbors.size(); ++j) {
            this.allNeighbors.get(j).removeNeighbor(this);
        }
        this.allNeighbors.clear();
    }
    
    public ArrayList<IsoChunkRegion> getDebugConnectedNeighborCopy() {
        final ArrayList<IsoChunkRegion> list = new ArrayList<IsoChunkRegion>();
        if (this.connectedNeighbors.size() == 0) {
            return list;
        }
        list.addAll(this.connectedNeighbors);
        return list;
    }
    
    public boolean containsConnectedNeighbor(final IsoChunkRegion o) {
        return this.connectedNeighbors.contains(o);
    }
    
    public boolean containsConnectedNeighborID(final int n) {
        if (this.connectedNeighbors.size() == 0) {
            return false;
        }
        for (int i = 0; i < this.connectedNeighbors.size(); ++i) {
            if (this.connectedNeighbors.get(i).getID() == n) {
                return true;
            }
        }
        return false;
    }
    
    public IsoChunkRegion getConnectedNeighborWithLargestIsoWorldRegion() {
        if (this.connectedNeighbors.size() == 0) {
            return null;
        }
        IsoWorldRegion isoWorldRegion = null;
        IsoChunkRegion isoChunkRegion = null;
        for (int i = 0; i < this.connectedNeighbors.size(); ++i) {
            final IsoChunkRegion isoChunkRegion2 = this.connectedNeighbors.get(i);
            final IsoWorldRegion isoWorldRegion2 = isoChunkRegion2.getIsoWorldRegion();
            if (isoWorldRegion2 != null && (isoWorldRegion == null || isoWorldRegion2.getSquareSize() > isoWorldRegion.getSquareSize())) {
                isoWorldRegion = isoWorldRegion2;
                isoChunkRegion = isoChunkRegion2;
            }
        }
        return isoChunkRegion;
    }
    
    protected IsoChunkRegion getFirstNeighborWithIsoWorldRegion() {
        if (this.connectedNeighbors.size() == 0) {
            return null;
        }
        for (int i = 0; i < this.connectedNeighbors.size(); ++i) {
            final IsoChunkRegion isoChunkRegion = this.connectedNeighbors.get(i);
            if (isoChunkRegion.getIsoWorldRegion() != null) {
                return isoChunkRegion;
            }
        }
        return null;
    }
}
