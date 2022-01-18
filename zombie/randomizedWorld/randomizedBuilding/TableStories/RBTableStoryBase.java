// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding.TableStories;

import zombie.iso.IsoCell;
import zombie.iso.IsoWorld;
import java.util.Iterator;
import zombie.core.Rand;
import zombie.iso.IsoGridSquare;
import java.util.HashMap;
import zombie.iso.IsoObject;
import java.util.ArrayList;
import zombie.randomizedWorld.randomizedBuilding.RandomizedBuildingBase;

public class RBTableStoryBase extends RandomizedBuildingBase
{
    public static ArrayList<RBTableStoryBase> allStories;
    public static int totalChance;
    protected int chance;
    protected ArrayList<String> rooms;
    protected boolean need2Tables;
    protected boolean ignoreAgainstWall;
    protected IsoObject table2;
    protected IsoObject table1;
    protected boolean westTable;
    private static final HashMap<RBTableStoryBase, Integer> rbtsmap;
    public ArrayList<HashMap<String, Integer>> fullTableMap;
    
    public RBTableStoryBase() {
        this.chance = 0;
        this.rooms = new ArrayList<String>();
        this.need2Tables = false;
        this.ignoreAgainstWall = false;
        this.table2 = null;
        this.table1 = null;
        this.westTable = false;
        this.fullTableMap = new ArrayList<HashMap<String, Integer>>();
    }
    
    public static void initStories(final IsoGridSquare isoGridSquare, final IsoObject isoObject) {
        if (RBTableStoryBase.allStories.isEmpty()) {
            RBTableStoryBase.allStories.add(new RBTSBreakfast());
            RBTableStoryBase.allStories.add(new RBTSDinner());
            RBTableStoryBase.allStories.add(new RBTSSoup());
            RBTableStoryBase.allStories.add(new RBTSSewing());
            RBTableStoryBase.allStories.add(new RBTSElectronics());
            RBTableStoryBase.allStories.add(new RBTSFoodPreparation());
            RBTableStoryBase.allStories.add(new RBTSButcher());
            RBTableStoryBase.allStories.add(new RBTSSandwich());
            RBTableStoryBase.allStories.add(new RBTSDrink());
        }
        RBTableStoryBase.totalChance = 0;
        RBTableStoryBase.rbtsmap.clear();
        for (int i = 0; i < RBTableStoryBase.allStories.size(); ++i) {
            final RBTableStoryBase key = RBTableStoryBase.allStories.get(i);
            if (key.isValid(isoGridSquare, isoObject, false) && key.isTimeValid(false)) {
                RBTableStoryBase.totalChance += key.chance;
                RBTableStoryBase.rbtsmap.put(key, key.chance);
            }
        }
    }
    
    public static RBTableStoryBase getRandomStory(final IsoGridSquare isoGridSquare, final IsoObject table1) {
        initStories(isoGridSquare, table1);
        final int next = Rand.Next(RBTableStoryBase.totalChance);
        final Iterator<RBTableStoryBase> iterator = RBTableStoryBase.rbtsmap.keySet().iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final RBTableStoryBase key = iterator.next();
            n += RBTableStoryBase.rbtsmap.get(key);
            if (next < n) {
                key.table1 = table1;
                return key;
            }
        }
        return null;
    }
    
    public boolean isValid(final IsoGridSquare isoGridSquare, final IsoObject isoObject, final boolean b) {
        if (b) {
            return true;
        }
        if (this.rooms != null && isoGridSquare.getRoom() != null && !this.rooms.contains(isoGridSquare.getRoom().getName())) {
            return false;
        }
        if (this.need2Tables) {
            this.table2 = this.getSecondTable(isoObject);
            if (this.table2 == null) {
                return false;
            }
        }
        return !this.ignoreAgainstWall || !isoGridSquare.getWallFull();
    }
    
    public IsoObject getSecondTable(final IsoObject isoObject) {
        this.westTable = true;
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        final IsoGridSquare gridSquare = currentCell.getGridSquare((int)isoObject.getX(), (int)isoObject.getY(), isoObject.getZ());
        if (this.ignoreAgainstWall && gridSquare.getWallFull()) {
            return null;
        }
        IsoObject isoObject2 = this.checkForTable(currentCell.getGridSquare((int)isoObject.getX() - 1, (int)isoObject.getY(), isoObject.getZ()), isoObject);
        final IsoGridSquare gridSquare2 = currentCell.getGridSquare((int)isoObject.getX() + 1, (int)isoObject.getY(), isoObject.getZ());
        if (isoObject2 == null) {
            isoObject2 = this.checkForTable(gridSquare2, isoObject);
        }
        if (isoObject2 == null) {
            this.westTable = false;
        }
        final IsoGridSquare gridSquare3 = currentCell.getGridSquare((int)isoObject.getX(), (int)isoObject.getY() - 1, isoObject.getZ());
        if (isoObject2 == null) {
            isoObject2 = this.checkForTable(gridSquare3, isoObject);
        }
        final IsoGridSquare gridSquare4 = currentCell.getGridSquare((int)isoObject.getX(), (int)isoObject.getY() + 1, isoObject.getZ());
        if (isoObject2 == null) {
            isoObject2 = this.checkForTable(gridSquare4, isoObject);
        }
        if (isoObject2 != null) {
            currentCell.getGridSquare((int)isoObject2.getX(), (int)isoObject2.getY(), isoObject2.getZ());
            if (this.ignoreAgainstWall && (gridSquare4.getWall(true) != null || gridSquare4.getWall(false) != null)) {
                return null;
            }
        }
        return isoObject2;
    }
    
    private IsoObject checkForTable(final IsoGridSquare isoGridSquare, final IsoObject isoObject) {
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject isoObject2 = isoGridSquare.getObjects().get(i);
            if (isoObject2.getProperties().isTable() && isoObject2.getProperties().getSurface() == 34 && isoObject2.getContainer() == null && isoObject2 != isoObject) {
                return isoObject2;
            }
        }
        return null;
    }
    
    static {
        RBTableStoryBase.allStories = new ArrayList<RBTableStoryBase>();
        RBTableStoryBase.totalChance = 0;
        rbtsmap = new HashMap<RBTableStoryBase, Integer>();
    }
}
