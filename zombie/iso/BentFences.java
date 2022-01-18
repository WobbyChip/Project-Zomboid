// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.iso.sprite.IsoSprite;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.vehicles.PolygonalMap2;
import zombie.MapCollisionData;
import zombie.iso.sprite.IsoSpriteManager;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.j2se.KahluaTableImpl;
import java.util.HashMap;
import java.util.ArrayList;

public class BentFences
{
    private static final BentFences instance;
    private final ArrayList<Entry> m_entries;
    private final HashMap<String, ArrayList<Entry>> m_bentMap;
    private final HashMap<String, ArrayList<Entry>> m_unbentMap;
    
    public BentFences() {
        this.m_entries = new ArrayList<Entry>();
        this.m_bentMap = new HashMap<String, ArrayList<Entry>>();
        this.m_unbentMap = new HashMap<String, ArrayList<Entry>>();
    }
    
    public static BentFences getInstance() {
        return BentFences.instance;
    }
    
    private void tableToTiles(final KahluaTableImpl kahluaTableImpl, final ArrayList<String> list) {
        if (kahluaTableImpl == null) {
            return;
        }
        final KahluaTableIterator iterator = kahluaTableImpl.iterator();
        while (iterator.advance()) {
            list.add(iterator.getValue().toString());
        }
    }
    
    private void tableToTiles(final KahluaTable kahluaTable, final ArrayList<String> list, final String s) {
        this.tableToTiles((KahluaTableImpl)kahluaTable.rawget((Object)s), list);
    }
    
    public void addFenceTiles(final int n, final KahluaTableImpl kahluaTableImpl) {
        final KahluaTableIterator iterator = kahluaTableImpl.iterator();
        while (iterator.advance()) {
            final KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)iterator.getValue();
            final Entry e = new Entry();
            e.dir = IsoDirections.valueOf(kahluaTableImpl2.rawgetStr((Object)"dir"));
            this.tableToTiles((KahluaTable)kahluaTableImpl2, e.unbent, "unbent");
            this.tableToTiles((KahluaTable)kahluaTableImpl2, e.bent, "bent");
            if (!e.unbent.isEmpty()) {
                if (e.unbent.size() != e.bent.size()) {
                    continue;
                }
                this.m_entries.add(e);
                for (final String s : e.unbent) {
                    ArrayList<Entry> value = this.m_unbentMap.get(s);
                    if (value == null) {
                        value = new ArrayList<Entry>();
                        this.m_unbentMap.put(s, value);
                    }
                    value.add(e);
                }
                for (final String s2 : e.bent) {
                    ArrayList<Entry> value2 = this.m_bentMap.get(s2);
                    if (value2 == null) {
                        value2 = new ArrayList<Entry>();
                        this.m_bentMap.put(s2, value2);
                    }
                    value2.add(e);
                }
            }
        }
    }
    
    public boolean isBentObject(final IsoObject isoObject) {
        return this.getEntryForObject(isoObject, null) != null;
    }
    
    public boolean isUnbentObject(final IsoObject isoObject) {
        return this.getEntryForObject(isoObject, IsoDirections.Max) != null;
    }
    
    private Entry getEntryForObject(final IsoObject isoObject, final IsoDirections isoDirections) {
        if (isoObject == null || isoObject.sprite == null || isoObject.sprite.name == null) {
            return null;
        }
        final boolean b = isoDirections != null;
        final ArrayList<Entry> list = b ? this.m_unbentMap.get(isoObject.sprite.name) : this.m_bentMap.get(isoObject.sprite.name);
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                final Entry entry = list.get(i);
                if (!b || isoDirections == IsoDirections.Max || isoDirections == entry.dir) {
                    if (this.isValidObject(isoObject, entry, b)) {
                        return entry;
                    }
                }
            }
        }
        return null;
    }
    
    private boolean isValidObject(final IsoObject isoObject, final Entry entry, final boolean b) {
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        final ArrayList<String> list = b ? entry.unbent : entry.bent;
        final int n = list.get(2).equals(isoObject.sprite.name) ? 2 : (list.get(3).equals(isoObject.sprite.name) ? 3 : -1);
        if (n == -1) {
            return false;
        }
        for (int i = 0; i < list.size(); ++i) {
            final IsoGridSquare gridSquare = currentCell.getGridSquare(isoObject.square.x + (entry.isNorth() ? (i - n) : 0), isoObject.square.y + (entry.isNorth() ? 0 : (i - n)), isoObject.square.z);
            if (gridSquare == null) {
                return false;
            }
            if (n != i) {
                if (this.getObjectForEntry(gridSquare, list, i) == null) {
                    return false;
                }
            }
        }
        return true;
    }
    
    IsoObject getObjectForEntry(final IsoGridSquare isoGridSquare, final ArrayList<String> list, final int index) {
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            if (isoObject.sprite != null) {
                if (isoObject.sprite.name != null) {
                    if (list.get(index).equals(isoObject.sprite.name)) {
                        return isoObject;
                    }
                }
            }
        }
        return null;
    }
    
    public void swapTiles(final IsoObject isoObject, final IsoDirections isoDirections) {
        final boolean b = isoDirections != null;
        final Entry entryForObject = this.getEntryForObject(isoObject, isoDirections);
        if (entryForObject == null) {
            return;
        }
        if (b) {
            if (entryForObject.isNorth() && isoDirections != IsoDirections.N && isoDirections != IsoDirections.S) {
                return;
            }
            if (!entryForObject.isNorth() && isoDirections != IsoDirections.W && isoDirections != IsoDirections.E) {
                return;
            }
        }
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        final ArrayList<String> list = b ? entryForObject.unbent : entryForObject.bent;
        final int n = list.get(2).equals(isoObject.sprite.name) ? 2 : (list.get(3).equals(isoObject.sprite.name) ? 3 : -1);
        for (int i = 0; i < list.size(); ++i) {
            final IsoGridSquare gridSquare = currentCell.getGridSquare(isoObject.square.x + (entryForObject.isNorth() ? (i - n) : 0), isoObject.square.y + (entryForObject.isNorth() ? 0 : (i - n)), isoObject.square.z);
            if (gridSquare != null) {
                final IsoObject objectForEntry = this.getObjectForEntry(gridSquare, list, i);
                if (objectForEntry != null) {
                    final String name = b ? entryForObject.bent.get(i) : entryForObject.unbent.get(i);
                    final IsoSprite sprite = IsoSpriteManager.instance.getSprite(name);
                    sprite.name = name;
                    objectForEntry.setSprite(sprite);
                    objectForEntry.transmitUpdatedSprite();
                    gridSquare.RecalcAllWithNeighbours(true);
                    MapCollisionData.instance.squareChanged(gridSquare);
                    PolygonalMap2.instance.squareChanged(gridSquare);
                    IsoRegions.squareChanged(gridSquare);
                }
            }
        }
    }
    
    public void bendFence(final IsoObject isoObject, final IsoDirections isoDirections) {
        this.swapTiles(isoObject, isoDirections);
    }
    
    public void unbendFence(final IsoObject isoObject) {
        this.swapTiles(isoObject, null);
    }
    
    public void Reset() {
        this.m_entries.clear();
        this.m_bentMap.clear();
        this.m_unbentMap.clear();
    }
    
    static {
        instance = new BentFences();
    }
    
    private static final class Entry
    {
        IsoDirections dir;
        final ArrayList<String> unbent;
        final ArrayList<String> bent;
        
        private Entry() {
            this.dir = IsoDirections.Max;
            this.unbent = new ArrayList<String>();
            this.bent = new ArrayList<String>();
        }
        
        boolean isNorth() {
            return this.dir == IsoDirections.N || this.dir == IsoDirections.S;
        }
    }
}
