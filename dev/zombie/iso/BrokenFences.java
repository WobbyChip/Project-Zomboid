// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.core.Rand;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.objects.IsoThumpable;
import zombie.SoundManager;
import zombie.network.GameServer;
import zombie.core.properties.PropertyContainer;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.vehicles.PolygonalMap2;
import zombie.MapCollisionData;
import zombie.iso.sprite.IsoSpriteManager;
import java.util.Map;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import java.util.ArrayList;
import se.krka.kahlua.j2se.KahluaTableImpl;
import gnu.trove.map.hash.THashMap;

public class BrokenFences
{
    private static final BrokenFences instance;
    private final THashMap<String, Tile> s_unbrokenMap;
    private final THashMap<String, Tile> s_brokenLeftMap;
    private final THashMap<String, Tile> s_brokenRightMap;
    private final THashMap<String, Tile> s_allMap;
    
    public BrokenFences() {
        this.s_unbrokenMap = (THashMap<String, Tile>)new THashMap();
        this.s_brokenLeftMap = (THashMap<String, Tile>)new THashMap();
        this.s_brokenRightMap = (THashMap<String, Tile>)new THashMap();
        this.s_allMap = (THashMap<String, Tile>)new THashMap();
    }
    
    public static BrokenFences getInstance() {
        return BrokenFences.instance;
    }
    
    private ArrayList<String> tableToTiles(final KahluaTableImpl kahluaTableImpl) {
        if (kahluaTableImpl == null) {
            return null;
        }
        ArrayList<String> list = null;
        final KahluaTableIterator iterator = kahluaTableImpl.iterator();
        while (iterator.advance()) {
            if (list == null) {
                list = new ArrayList<String>();
            }
            list.add(iterator.getValue().toString());
        }
        return list;
    }
    
    private ArrayList<String> tableToTiles(final KahluaTable kahluaTable, final String s) {
        return this.tableToTiles((KahluaTableImpl)kahluaTable.rawget((Object)s));
    }
    
    public void addBrokenTiles(final KahluaTableImpl kahluaTableImpl) {
        final KahluaTableIterator iterator = kahluaTableImpl.iterator();
        while (iterator.advance()) {
            final String string = iterator.getKey().toString();
            if ("VERSION".equalsIgnoreCase(string)) {
                continue;
            }
            final KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)iterator.getValue();
            final Tile tile = new Tile();
            tile.self = this.tableToTiles((KahluaTable)kahluaTableImpl2, "self");
            tile.left = this.tableToTiles((KahluaTable)kahluaTableImpl2, "left");
            tile.right = this.tableToTiles((KahluaTable)kahluaTableImpl2, "right");
            this.s_unbrokenMap.put((Object)string, (Object)tile);
            PZArrayUtil.forEach(tile.left, s -> this.s_brokenLeftMap.put((Object)s, (Object)tile));
            PZArrayUtil.forEach(tile.right, s2 -> this.s_brokenRightMap.put((Object)s2, (Object)tile));
        }
        this.s_allMap.putAll((Map)this.s_unbrokenMap);
        this.s_allMap.putAll((Map)this.s_brokenLeftMap);
        this.s_allMap.putAll((Map)this.s_brokenRightMap);
    }
    
    public void addDebrisTiles(final KahluaTableImpl kahluaTableImpl) {
        final KahluaTableIterator iterator = kahluaTableImpl.iterator();
        while (iterator.advance()) {
            final String string = iterator.getKey().toString();
            if ("VERSION".equalsIgnoreCase(string)) {
                continue;
            }
            final KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)iterator.getValue();
            final Tile tile = (Tile)this.s_unbrokenMap.get((Object)string);
            if (tile == null) {
                throw new IllegalArgumentException("addDebrisTiles() with unknown tile");
            }
            tile.debrisN = this.tableToTiles((KahluaTable)kahluaTableImpl2, "north");
            tile.debrisS = this.tableToTiles((KahluaTable)kahluaTableImpl2, "south");
            tile.debrisW = this.tableToTiles((KahluaTable)kahluaTableImpl2, "west");
            tile.debrisE = this.tableToTiles((KahluaTable)kahluaTableImpl2, "east");
        }
    }
    
    public void setDestroyed(final IsoObject isoObject) {
        isoObject.RemoveAttachedAnims();
        this.updateSprite(isoObject, true, true);
    }
    
    public void setDamagedLeft(final IsoObject isoObject) {
        this.updateSprite(isoObject, true, false);
    }
    
    public void setDamagedRight(final IsoObject isoObject) {
        this.updateSprite(isoObject, false, true);
    }
    
    public void updateSprite(final IsoObject isoObject, final boolean b, final boolean b2) {
        if (!this.isBreakableObject(isoObject)) {
            return;
        }
        final Tile tile = (Tile)this.s_allMap.get((Object)isoObject.sprite.name);
        String name = null;
        if (b && b2) {
            name = tile.pickRandom(tile.self);
        }
        else if (b) {
            name = tile.pickRandom(tile.left);
        }
        else if (b2) {
            name = tile.pickRandom(tile.right);
        }
        if (name != null) {
            final IsoSprite sprite = IsoSpriteManager.instance.getSprite(name);
            sprite.name = name;
            isoObject.setSprite(sprite);
            isoObject.transmitUpdatedSprite();
            isoObject.getSquare().RecalcAllWithNeighbours(true);
            MapCollisionData.instance.squareChanged(isoObject.getSquare());
            PolygonalMap2.instance.squareChanged(isoObject.getSquare());
            IsoRegions.squareChanged(isoObject.getSquare());
        }
    }
    
    private boolean isNW(final IsoObject isoObject) {
        final PropertyContainer properties = isoObject.getProperties();
        return properties.Is(IsoFlagType.collideN) && properties.Is(IsoFlagType.collideW);
    }
    
    private void damageAdjacent(final IsoGridSquare isoGridSquare, final IsoDirections isoDirections, final IsoDirections isoDirections2) {
        final IsoGridSquare adjacentSquare = isoGridSquare.getAdjacentSquare(isoDirections);
        if (adjacentSquare == null) {
            return;
        }
        final IsoObject breakableObject = this.getBreakableObject(adjacentSquare, isoDirections == IsoDirections.W || isoDirections == IsoDirections.E);
        if (breakableObject == null) {
            return;
        }
        final boolean b = isoDirections == IsoDirections.N || isoDirections == IsoDirections.E;
        final boolean b2 = isoDirections == IsoDirections.S || isoDirections == IsoDirections.W;
        if (this.isNW(breakableObject) && (isoDirections == IsoDirections.S || isoDirections == IsoDirections.E)) {
            return;
        }
        if (b && this.isBrokenRight(breakableObject)) {
            this.destroyFence(breakableObject, isoDirections2);
            return;
        }
        if (b2 && this.isBrokenLeft(breakableObject)) {
            this.destroyFence(breakableObject, isoDirections2);
            return;
        }
        this.updateSprite(breakableObject, b, b2);
    }
    
    public void destroyFence(IsoObject destroyed, final IsoDirections isoDirections) {
        if (!this.isBreakableObject(destroyed)) {
            return;
        }
        final IsoGridSquare square = destroyed.getSquare();
        if (GameServer.bServer) {
            GameServer.PlayWorldSoundServer("BreakObject", false, square, 1.0f, 20.0f, 1.0f, true);
        }
        else {
            SoundManager.instance.PlayWorldSound("BreakObject", square, 1.0f, 20.0f, 1.0f, true);
        }
        final boolean is = destroyed.getProperties().Is(IsoFlagType.collideN);
        final boolean is2 = destroyed.getProperties().Is(IsoFlagType.collideW);
        if (destroyed instanceof IsoThumpable) {
            final IsoObject new1 = IsoObject.getNew();
            new1.setSquare(square);
            new1.setSprite(destroyed.getSprite());
            final int objectIndex = destroyed.getObjectIndex();
            square.transmitRemoveItemFromSquare(destroyed);
            square.transmitAddObjectToSquare(new1, objectIndex);
            destroyed = new1;
        }
        this.addDebrisObject(destroyed, isoDirections);
        this.setDestroyed(destroyed);
        if (is && is2) {
            this.damageAdjacent(square, IsoDirections.S, isoDirections);
            this.damageAdjacent(square, IsoDirections.E, isoDirections);
        }
        else if (is) {
            this.damageAdjacent(square, IsoDirections.W, isoDirections);
            this.damageAdjacent(square, IsoDirections.E, isoDirections);
        }
        else if (is2) {
            this.damageAdjacent(square, IsoDirections.N, isoDirections);
            this.damageAdjacent(square, IsoDirections.S, isoDirections);
        }
        square.RecalcAllWithNeighbours(true);
        MapCollisionData.instance.squareChanged(square);
        PolygonalMap2.instance.squareChanged(square);
        IsoRegions.squareChanged(square);
    }
    
    private boolean isUnbroken(final IsoObject isoObject) {
        return isoObject != null && isoObject.sprite != null && isoObject.sprite.name != null && this.s_unbrokenMap.contains((Object)isoObject.sprite.name);
    }
    
    private boolean isBrokenLeft(final IsoObject isoObject) {
        return isoObject != null && isoObject.sprite != null && isoObject.sprite.name != null && this.s_brokenLeftMap.contains((Object)isoObject.sprite.name);
    }
    
    private boolean isBrokenRight(final IsoObject isoObject) {
        return isoObject != null && isoObject.sprite != null && isoObject.sprite.name != null && this.s_brokenRightMap.contains((Object)isoObject.sprite.name);
    }
    
    public boolean isBreakableObject(final IsoObject isoObject) {
        return isoObject != null && isoObject.sprite != null && isoObject.sprite.name != null && this.s_allMap.containsKey((Object)isoObject.sprite.name);
    }
    
    private IsoObject getBreakableObject(final IsoGridSquare isoGridSquare, final boolean b) {
        for (int i = 0; i < isoGridSquare.Objects.size(); ++i) {
            final IsoObject isoObject = isoGridSquare.Objects.get(i);
            if (this.isBreakableObject(isoObject) && ((b && isoObject.getProperties().Is(IsoFlagType.collideN)) || (!b && isoObject.getProperties().Is(IsoFlagType.collideW)))) {
                return isoObject;
            }
        }
        return null;
    }
    
    private void addItems(final IsoObject isoObject, final IsoGridSquare isoGridSquare) {
        final PropertyContainer properties = isoObject.getProperties();
        if (properties == null) {
            return;
        }
        final String val = properties.Val("Material");
        final String val2 = properties.Val("Material2");
        final String val3 = properties.Val("Material3");
        if ("Wood".equals(val) || "Wood".equals(val2) || "Wood".equals(val3)) {
            isoGridSquare.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Plank"), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
            if (Rand.NextBool(5)) {
                isoGridSquare.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Plank"), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
            }
        }
        if (("MetalBars".equals(val) || "MetalBars".equals(val2) || "MetalBars".equals(val3)) && Rand.NextBool(2)) {
            isoGridSquare.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.MetalBar"), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
        }
        if (("MetalWire".equals(val) || "MetalWire".equals(val2) || "MetalWire".equals(val3)) && Rand.NextBool(3)) {
            isoGridSquare.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Wire"), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
        }
        if (("Nails".equals(val) || "Nails".equals(val2) || "Nails".equals(val3)) && Rand.NextBool(2)) {
            isoGridSquare.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Nails"), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
        }
        if (("Screws".equals(val) || "Screws".equals(val2) || "Screws".equals(val3)) && Rand.NextBool(2)) {
            isoGridSquare.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Screws"), Rand.Next(0.0f, 0.5f), Rand.Next(0.0f, 0.5f), 0.0f);
        }
    }
    
    private void addDebrisObject(final IsoObject isoObject, final IsoDirections isoDirections) {
        if (!this.isBreakableObject(isoObject)) {
            return;
        }
        final Tile tile = (Tile)this.s_allMap.get((Object)isoObject.sprite.name);
        IsoGridSquare isoGridSquare = isoObject.getSquare();
        String s = null;
        switch (isoDirections) {
            case N: {
                s = tile.pickRandom(tile.debrisN);
                isoGridSquare = isoGridSquare.getAdjacentSquare(isoDirections);
                break;
            }
            case S: {
                s = tile.pickRandom(tile.debrisS);
                break;
            }
            case W: {
                s = tile.pickRandom(tile.debrisW);
                isoGridSquare = isoGridSquare.getAdjacentSquare(isoDirections);
                break;
            }
            case E: {
                s = tile.pickRandom(tile.debrisE);
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid direction");
            }
        }
        if (s != null && isoGridSquare != null && isoGridSquare.TreatAsSolidFloor()) {
            isoGridSquare.transmitAddObjectToSquare(IsoObject.getNew(isoGridSquare, s, null, false), (isoGridSquare == isoObject.getSquare()) ? isoObject.getObjectIndex() : -1);
            this.addItems(isoObject, isoGridSquare);
        }
    }
    
    public void Reset() {
        this.s_unbrokenMap.clear();
        this.s_brokenLeftMap.clear();
        this.s_brokenRightMap.clear();
        this.s_allMap.clear();
    }
    
    static {
        instance = new BrokenFences();
    }
    
    private static final class Tile
    {
        ArrayList<String> self;
        ArrayList<String> left;
        ArrayList<String> right;
        ArrayList<String> debrisN;
        ArrayList<String> debrisS;
        ArrayList<String> debrisW;
        ArrayList<String> debrisE;
        
        private Tile() {
            this.self = null;
            this.left = null;
            this.right = null;
            this.debrisN = null;
            this.debrisS = null;
            this.debrisW = null;
            this.debrisE = null;
        }
        
        String pickRandom(final ArrayList<String> list) {
            if (list == null) {
                return null;
            }
            return PZArrayUtil.pickRandom(list);
        }
    }
}
