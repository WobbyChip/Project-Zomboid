// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.Collection;
import java.util.ArrayList;
import zombie.iso.areas.IsoBuilding;
import gnu.trove.set.hash.THashSet;

public class IsoGridOcclusionData
{
    public static final int MAXBUILDINGOCCLUDERS = 3;
    private static final THashSet<IsoBuilding> _leftBuildings;
    private static final THashSet<IsoBuilding> _rightBuildings;
    private static final THashSet<IsoBuilding> _allBuildings;
    private static int _ObjectEpoch;
    private final ArrayList<IsoBuilding> _leftBuildingsArray;
    private final ArrayList<IsoBuilding> _rightBuildingsArray;
    private final ArrayList<IsoBuilding> _allBuildingsArray;
    private IsoGridSquare _ownerSquare;
    private boolean _bSoftInitialized;
    private boolean _bLeftOccludedByOrphanStructures;
    private boolean _bRightOccludedByOrphanStructures;
    private int _objectEpoch;
    
    public IsoGridOcclusionData(final IsoGridSquare ownerSquare) {
        this._leftBuildingsArray = new ArrayList<IsoBuilding>(3);
        this._rightBuildingsArray = new ArrayList<IsoBuilding>(3);
        this._allBuildingsArray = new ArrayList<IsoBuilding>(3);
        this._ownerSquare = null;
        this._bSoftInitialized = false;
        this._bLeftOccludedByOrphanStructures = false;
        this._bRightOccludedByOrphanStructures = false;
        this._objectEpoch = -1;
        this._ownerSquare = ownerSquare;
    }
    
    public static void SquareChanged() {
        ++IsoGridOcclusionData._ObjectEpoch;
        if (IsoGridOcclusionData._ObjectEpoch < 0) {
            IsoGridOcclusionData._ObjectEpoch = 0;
        }
    }
    
    public void Reset() {
        this._bSoftInitialized = false;
        this._bLeftOccludedByOrphanStructures = false;
        this._bRightOccludedByOrphanStructures = false;
        this._allBuildingsArray.clear();
        this._leftBuildingsArray.clear();
        this._rightBuildingsArray.clear();
        this._objectEpoch = -1;
    }
    
    public boolean getCouldBeOccludedByOrphanStructures(final OcclusionFilter occlusionFilter) {
        if (this._objectEpoch != IsoGridOcclusionData._ObjectEpoch) {
            if (this._bSoftInitialized) {
                this.Reset();
            }
            this._objectEpoch = IsoGridOcclusionData._ObjectEpoch;
        }
        if (!this._bSoftInitialized) {
            this.LazyInitializeSoftOccluders();
        }
        if (occlusionFilter == OcclusionFilter.Left) {
            return this._bLeftOccludedByOrphanStructures;
        }
        if (occlusionFilter == OcclusionFilter.Right) {
            return this._bRightOccludedByOrphanStructures;
        }
        return this._bLeftOccludedByOrphanStructures || this._bRightOccludedByOrphanStructures;
    }
    
    public ArrayList<IsoBuilding> getBuildingsCouldBeOccluders(final OcclusionFilter occlusionFilter) {
        if (this._objectEpoch != IsoGridOcclusionData._ObjectEpoch) {
            if (this._bSoftInitialized) {
                this.Reset();
            }
            this._objectEpoch = IsoGridOcclusionData._ObjectEpoch;
        }
        if (!this._bSoftInitialized) {
            this.LazyInitializeSoftOccluders();
        }
        if (occlusionFilter == OcclusionFilter.Left) {
            return this._leftBuildingsArray;
        }
        if (occlusionFilter == OcclusionFilter.Right) {
            return this._rightBuildingsArray;
        }
        return this._allBuildingsArray;
    }
    
    private void LazyInitializeSoftOccluders() {
        final boolean b = false;
        final int x = this._ownerSquare.getX();
        final int y = this._ownerSquare.getY();
        final int z = this._ownerSquare.getZ();
        IsoGridOcclusionData._allBuildings.clear();
        IsoGridOcclusionData._leftBuildings.clear();
        IsoGridOcclusionData._rightBuildings.clear();
        final boolean b2 = b | this.GetBuildingFloorsProjectedOnSquare(IsoGridOcclusionData._allBuildings, x, y, z) | this.GetBuildingFloorsProjectedOnSquare(IsoGridOcclusionData._allBuildings, x + 1, y + 1, z) | this.GetBuildingFloorsProjectedOnSquare(IsoGridOcclusionData._allBuildings, x + 2, y + 2, z) | this.GetBuildingFloorsProjectedOnSquare(IsoGridOcclusionData._allBuildings, x + 3, y + 3, z);
        this._bLeftOccludedByOrphanStructures |= this.GetBuildingFloorsProjectedOnSquare(IsoGridOcclusionData._leftBuildings, x, y + 1, z);
        this._bLeftOccludedByOrphanStructures |= this.GetBuildingFloorsProjectedOnSquare(IsoGridOcclusionData._leftBuildings, x + 1, y + 2, z);
        this._bLeftOccludedByOrphanStructures |= this.GetBuildingFloorsProjectedOnSquare(IsoGridOcclusionData._leftBuildings, x + 2, y + 3, z);
        this._bRightOccludedByOrphanStructures |= this.GetBuildingFloorsProjectedOnSquare(IsoGridOcclusionData._rightBuildings, x + 1, y, z);
        this._bRightOccludedByOrphanStructures |= this.GetBuildingFloorsProjectedOnSquare(IsoGridOcclusionData._rightBuildings, x + 2, y + 1, z);
        this._bRightOccludedByOrphanStructures |= this.GetBuildingFloorsProjectedOnSquare(IsoGridOcclusionData._rightBuildings, x + 3, y + 2, z);
        this._bLeftOccludedByOrphanStructures |= b2;
        IsoGridOcclusionData._leftBuildings.addAll((Collection)IsoGridOcclusionData._allBuildings);
        this._bRightOccludedByOrphanStructures |= b2;
        IsoGridOcclusionData._rightBuildings.addAll((Collection)IsoGridOcclusionData._allBuildings);
        IsoGridOcclusionData._allBuildings.clear();
        IsoGridOcclusionData._allBuildings.addAll((Collection)IsoGridOcclusionData._leftBuildings);
        IsoGridOcclusionData._allBuildings.addAll((Collection)IsoGridOcclusionData._rightBuildings);
        this._leftBuildingsArray.addAll((Collection<? extends IsoBuilding>)IsoGridOcclusionData._leftBuildings);
        this._rightBuildingsArray.addAll((Collection<? extends IsoBuilding>)IsoGridOcclusionData._rightBuildings);
        this._allBuildingsArray.addAll((Collection<? extends IsoBuilding>)IsoGridOcclusionData._allBuildings);
        this._bSoftInitialized = true;
    }
    
    private boolean GetBuildingFloorsProjectedOnSquare(final THashSet<IsoBuilding> set, final int n, final int n2, final int n3) {
        boolean b = false;
        for (int n4 = n, n5 = n2, i = n3; i < IsoCell.MaxHeight; ++i, n4 += 3, n5 += 3) {
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n4, n5, i);
            if (gridSquare != null) {
                IsoBuilding isoBuilding = gridSquare.getBuilding();
                if (isoBuilding == null) {
                    isoBuilding = gridSquare.roofHideBuilding;
                }
                if (isoBuilding != null) {
                    set.add((Object)isoBuilding);
                }
                for (int n6 = i - 1; n6 >= 0 && isoBuilding == null; --n6) {
                    final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(n4, n5, n6);
                    if (gridSquare2 != null) {
                        isoBuilding = gridSquare2.getBuilding();
                        if (isoBuilding == null) {
                            isoBuilding = gridSquare2.roofHideBuilding;
                        }
                        if (isoBuilding != null) {
                            set.add((Object)isoBuilding);
                        }
                    }
                }
                if (isoBuilding == null && !b && gridSquare.getZ() != 0 && gridSquare.getPlayerBuiltFloor() != null) {
                    b = true;
                }
            }
        }
        return b;
    }
    
    static {
        _leftBuildings = new THashSet(3);
        _rightBuildings = new THashSet(3);
        _allBuildings = new THashSet(3);
        IsoGridOcclusionData._ObjectEpoch = 0;
    }
    
    public enum OcclusionFilter
    {
        Left, 
        Right, 
        All;
        
        private static /* synthetic */ OcclusionFilter[] $values() {
            return new OcclusionFilter[] { OcclusionFilter.Left, OcclusionFilter.Right, OcclusionFilter.All };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public enum OccluderType
    {
        Unknown, 
        NotFull, 
        Full;
        
        private static /* synthetic */ OccluderType[] $values() {
            return new OccluderType[] { OccluderType.Unknown, OccluderType.NotFull, OccluderType.Full };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
