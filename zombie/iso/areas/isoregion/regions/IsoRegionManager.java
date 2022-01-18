// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion.regions;

import zombie.core.Colors;
import zombie.core.Color;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.areas.isoregion.data.DataRoot;
import java.util.ArrayDeque;

public final class IsoRegionManager
{
    private final ArrayDeque<IsoWorldRegion> poolIsoWorldRegion;
    private final ArrayDeque<IsoChunkRegion> poolIsoChunkRegion;
    private final DataRoot dataRoot;
    private final ArrayDeque<Integer> regionIdStack;
    private int nextID;
    private int colorIndex;
    private int worldRegionCount;
    private int chunkRegionCount;
    
    public IsoRegionManager(final DataRoot dataRoot) {
        this.poolIsoWorldRegion = new ArrayDeque<IsoWorldRegion>();
        this.poolIsoChunkRegion = new ArrayDeque<IsoChunkRegion>();
        this.regionIdStack = new ArrayDeque<Integer>();
        this.nextID = 0;
        this.colorIndex = 0;
        this.worldRegionCount = 0;
        this.chunkRegionCount = 0;
        this.dataRoot = dataRoot;
    }
    
    public IsoWorldRegion allocIsoWorldRegion() {
        final IsoWorldRegion isoWorldRegion = this.poolIsoWorldRegion.isEmpty() ? new IsoWorldRegion(this) : this.poolIsoWorldRegion.pop();
        int n;
        if (this.regionIdStack.isEmpty()) {
            this.nextID = (n = this.nextID) + 1;
        }
        else {
            n = this.regionIdStack.pop();
        }
        isoWorldRegion.init(n);
        ++this.worldRegionCount;
        return isoWorldRegion;
    }
    
    public void releaseIsoWorldRegion(final IsoWorldRegion e) {
        this.dataRoot.DequeueDirtyIsoWorldRegion(e);
        if (!e.isInPool()) {
            this.regionIdStack.push(e.getID());
            e.reset();
            this.poolIsoWorldRegion.push(e);
            --this.worldRegionCount;
        }
        else {
            IsoRegions.warn("IsoRegionManager -> Trying to release a MasterRegion twice.");
        }
    }
    
    public IsoChunkRegion allocIsoChunkRegion(final int n) {
        final IsoChunkRegion isoChunkRegion = this.poolIsoChunkRegion.isEmpty() ? new IsoChunkRegion(this) : this.poolIsoChunkRegion.pop();
        int n2;
        if (this.regionIdStack.isEmpty()) {
            this.nextID = (n2 = this.nextID) + 1;
        }
        else {
            n2 = this.regionIdStack.pop();
        }
        isoChunkRegion.init(n2, n);
        ++this.chunkRegionCount;
        return isoChunkRegion;
    }
    
    public void releaseIsoChunkRegion(final IsoChunkRegion e) {
        if (!e.isInPool()) {
            this.regionIdStack.push(e.getID());
            e.reset();
            this.poolIsoChunkRegion.push(e);
            --this.chunkRegionCount;
        }
        else {
            IsoRegions.warn("IsoRegionManager -> Trying to release a ChunkRegion twice.");
        }
    }
    
    public Color getColor() {
        final Color getColorFromIndex = Colors.GetColorFromIndex(this.colorIndex++);
        if (this.colorIndex >= Colors.GetColorsCount()) {
            this.colorIndex = 0;
        }
        return getColorFromIndex;
    }
    
    public int getWorldRegionCount() {
        return this.worldRegionCount;
    }
    
    public int getChunkRegionCount() {
        return this.chunkRegionCount;
    }
}
