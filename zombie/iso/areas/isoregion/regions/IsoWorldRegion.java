// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion.regions;

import java.util.Collection;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.core.Core;
import java.util.ArrayList;
import zombie.core.Color;

public final class IsoWorldRegion implements IWorldRegion
{
    private final IsoRegionManager manager;
    private boolean isInPool;
    private int ID;
    private Color color;
    private boolean enclosed;
    private ArrayList<IsoChunkRegion> isoChunkRegions;
    private int squareSize;
    private int roofCnt;
    private boolean isDirtyEnclosed;
    private boolean isDirtyRoofed;
    private ArrayList<IsoWorldRegion> neighbors;
    
    public int getID() {
        return this.ID;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public int size() {
        return this.isoChunkRegions.size();
    }
    
    public int getSquareSize() {
        return this.squareSize;
    }
    
    protected boolean isInPool() {
        return this.isInPool;
    }
    
    protected IsoWorldRegion(final IsoRegionManager manager) {
        this.isInPool = false;
        this.enclosed = true;
        this.isoChunkRegions = new ArrayList<IsoChunkRegion>();
        this.squareSize = 0;
        this.roofCnt = 0;
        this.isDirtyEnclosed = false;
        this.isDirtyRoofed = false;
        this.neighbors = new ArrayList<IsoWorldRegion>();
        this.manager = manager;
    }
    
    protected void init(final int id) {
        this.isInPool = false;
        this.ID = id;
        if (this.color == null) {
            this.color = this.manager.getColor();
        }
        this.squareSize = 0;
        this.roofCnt = 0;
        this.enclosed = true;
        this.isDirtyEnclosed = false;
        this.isDirtyRoofed = false;
    }
    
    protected IsoWorldRegion reset() {
        this.isInPool = true;
        this.ID = -1;
        this.squareSize = 0;
        this.roofCnt = 0;
        this.enclosed = true;
        this.isDirtyRoofed = false;
        this.isDirtyEnclosed = false;
        this.unlinkNeighbors();
        if (this.isoChunkRegions.size() > 0) {
            if (Core.bDebug) {
                throw new RuntimeException("MasterRegion.reset Resetting master region which still has chunk regions");
            }
            IsoRegions.warn("MasterRegion.reset Resetting master region which still has chunk regions");
            for (int i = 0; i < this.isoChunkRegions.size(); ++i) {
                this.isoChunkRegions.get(i).setIsoWorldRegion(null);
            }
            this.isoChunkRegions.clear();
        }
        return this;
    }
    
    public void unlinkNeighbors() {
        for (int i = 0; i < this.neighbors.size(); ++i) {
            this.neighbors.get(i).removeNeighbor(this);
        }
        this.neighbors.clear();
    }
    
    public void linkNeighbors() {
        for (int i = 0; i < this.isoChunkRegions.size(); ++i) {
            final IsoChunkRegion isoChunkRegion = this.isoChunkRegions.get(i);
            for (int j = 0; j < isoChunkRegion.getAllNeighbors().size(); ++j) {
                final IsoChunkRegion isoChunkRegion2 = isoChunkRegion.getAllNeighbors().get(j);
                if (isoChunkRegion2.getIsoWorldRegion() != null && isoChunkRegion2.getIsoWorldRegion() != this) {
                    this.addNeighbor(isoChunkRegion2.getIsoWorldRegion());
                    isoChunkRegion2.getIsoWorldRegion().addNeighbor(this);
                }
            }
        }
    }
    
    private void addNeighbor(final IsoWorldRegion isoWorldRegion) {
        if (!this.neighbors.contains(isoWorldRegion)) {
            this.neighbors.add(isoWorldRegion);
        }
    }
    
    private void removeNeighbor(final IsoWorldRegion o) {
        this.neighbors.remove(o);
    }
    
    @Override
    public ArrayList<IsoWorldRegion> getNeighbors() {
        return this.neighbors;
    }
    
    @Override
    public ArrayList<IsoWorldRegion> getDebugConnectedNeighborCopy() {
        final ArrayList<IsoWorldRegion> list = new ArrayList<IsoWorldRegion>();
        if (this.neighbors.size() == 0) {
            return list;
        }
        list.addAll(this.neighbors);
        return list;
    }
    
    @Override
    public boolean isFogMask() {
        return this.isEnclosed() && this.isFullyRoofed();
    }
    
    @Override
    public boolean isPlayerRoom() {
        return this.isFogMask();
    }
    
    @Override
    public boolean isFullyRoofed() {
        return this.roofCnt == this.squareSize;
    }
    
    public float getRoofedPercentage() {
        if (this.squareSize == 0) {
            return 0.0f;
        }
        return this.roofCnt / (float)this.squareSize;
    }
    
    @Override
    public int getRoofCnt() {
        return this.roofCnt;
    }
    
    protected void addRoof() {
        ++this.roofCnt;
        if (this.roofCnt > this.squareSize) {
            IsoRegions.warn("WorldRegion.addRoof roofCount exceed squareSize.");
            this.roofCnt = this.squareSize;
        }
    }
    
    protected void removeRoofs(final int n) {
        if (n <= 0) {
            return;
        }
        this.roofCnt -= n;
        if (this.roofCnt < 0) {
            IsoRegions.warn("MasterRegion.removeRoofs Roofcount managed to get below zero.");
            this.roofCnt = 0;
        }
    }
    
    public void addIsoChunkRegion(final IsoChunkRegion isoChunkRegion) {
        if (!this.isoChunkRegions.contains(isoChunkRegion)) {
            this.squareSize += isoChunkRegion.getSquareSize();
            this.roofCnt += isoChunkRegion.getRoofCnt();
            this.isDirtyEnclosed = true;
            this.isoChunkRegions.add(isoChunkRegion);
            isoChunkRegion.setIsoWorldRegion(this);
        }
    }
    
    protected void removeIsoChunkRegion(final IsoChunkRegion o) {
        if (this.isoChunkRegions.remove(o)) {
            this.squareSize -= o.getSquareSize();
            this.roofCnt -= o.getRoofCnt();
            this.isDirtyEnclosed = true;
            o.setIsoWorldRegion(null);
        }
    }
    
    public boolean containsIsoChunkRegion(final IsoChunkRegion o) {
        return this.isoChunkRegions.contains(o);
    }
    
    public ArrayList<IsoChunkRegion> swapIsoChunkRegions(final ArrayList<IsoChunkRegion> isoChunkRegions) {
        final ArrayList<IsoChunkRegion> isoChunkRegions2 = this.isoChunkRegions;
        this.isoChunkRegions = isoChunkRegions;
        return isoChunkRegions2;
    }
    
    protected void resetSquareSize() {
        this.squareSize = 0;
    }
    
    protected void setDirtyEnclosed() {
        this.isDirtyEnclosed = true;
    }
    
    public boolean isEnclosed() {
        if (this.isDirtyEnclosed) {
            this.recalcEnclosed();
        }
        return this.enclosed;
    }
    
    private void recalcEnclosed() {
        this.isDirtyEnclosed = false;
        this.enclosed = true;
        for (int i = 0; i < this.isoChunkRegions.size(); ++i) {
            if (!this.isoChunkRegions.get(i).getIsEnclosed()) {
                this.enclosed = false;
            }
        }
    }
    
    public void merge(final IsoWorldRegion isoWorldRegion) {
        if (isoWorldRegion.isoChunkRegions.size() > 0) {
            for (int i = isoWorldRegion.isoChunkRegions.size() - 1; i >= 0; --i) {
                final IsoChunkRegion isoChunkRegion = isoWorldRegion.isoChunkRegions.get(i);
                isoWorldRegion.removeIsoChunkRegion(isoChunkRegion);
                this.addIsoChunkRegion(isoChunkRegion);
            }
            this.isDirtyEnclosed = true;
            isoWorldRegion.isoChunkRegions.clear();
        }
        if (isoWorldRegion.neighbors.size() > 0) {
            for (int j = 0; j < isoWorldRegion.neighbors.size(); ++j) {
                final IsoWorldRegion isoWorldRegion2 = isoWorldRegion.neighbors.get(j);
                isoWorldRegion2.removeNeighbor(isoWorldRegion);
                this.addNeighbor(isoWorldRegion2);
            }
            isoWorldRegion.neighbors.clear();
        }
        this.manager.releaseIsoWorldRegion(isoWorldRegion);
    }
    
    @Override
    public ArrayList<IsoChunkRegion> getDebugIsoChunkRegionCopy() {
        final ArrayList<IsoChunkRegion> list = new ArrayList<IsoChunkRegion>();
        list.addAll(this.isoChunkRegions);
        return list;
    }
}
