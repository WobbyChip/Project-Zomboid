// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.Iterator;
import zombie.core.stash.StashSystem;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import zombie.iso.areas.IsoRoom;
import zombie.inventory.types.Food;
import zombie.inventory.ItemContainer;
import java.util.Collection;
import zombie.core.Rand;
import zombie.Lua.LuaManager;
import java.util.HashSet;
import zombie.inventory.InventoryItem;
import gnu.trove.list.array.TShortArrayList;
import se.krka.kahlua.vm.KahluaTable;
import java.util.ArrayList;

public final class BuildingDef
{
    static final ArrayList<IsoGridSquare> squareChoices;
    public final ArrayList<RoomDef> emptyoutside;
    public KahluaTable table;
    public boolean seen;
    public boolean hasBeenVisited;
    public String stash;
    public int lootRespawnHour;
    public TShortArrayList overlappedChunks;
    public boolean bAlarmed;
    public int x;
    public int y;
    public int x2;
    public int y2;
    public final ArrayList<RoomDef> rooms;
    public IsoMetaGrid.Zone zone;
    public int food;
    public ArrayList<InventoryItem> items;
    public HashSet<String> itemTypes;
    int ID;
    private int keySpawned;
    private int keyId;
    
    public BuildingDef() {
        this.emptyoutside = new ArrayList<RoomDef>();
        this.table = null;
        this.seen = false;
        this.hasBeenVisited = false;
        this.stash = null;
        this.lootRespawnHour = -1;
        this.bAlarmed = false;
        this.x = 10000000;
        this.y = 10000000;
        this.x2 = -10000000;
        this.y2 = -10000000;
        this.rooms = new ArrayList<RoomDef>();
        this.items = new ArrayList<InventoryItem>();
        this.itemTypes = new HashSet<String>();
        this.ID = 0;
        this.keySpawned = 0;
        this.keyId = -1;
        this.table = LuaManager.platform.newTable();
        this.setKeyId(Rand.Next(100000000));
    }
    
    public KahluaTable getTable() {
        return this.table;
    }
    
    public ArrayList<RoomDef> getRooms() {
        return this.rooms;
    }
    
    public RoomDef getRoom(final String anotherString) {
        for (int i = 0; i < this.rooms.size(); ++i) {
            final RoomDef roomDef = this.rooms.get(i);
            if (roomDef.getName().equalsIgnoreCase(anotherString)) {
                return roomDef;
            }
        }
        return null;
    }
    
    public boolean isAllExplored() {
        for (int i = 0; i < this.rooms.size(); ++i) {
            if (!this.rooms.get(i).bExplored) {
                return false;
            }
        }
        return true;
    }
    
    public void setAllExplored(final boolean explored) {
        for (int i = 0; i < this.rooms.size(); ++i) {
            this.rooms.get(i).setExplored(explored);
        }
    }
    
    public RoomDef getFirstRoom() {
        return this.rooms.get(0);
    }
    
    public int getChunkX() {
        return this.x / 10;
    }
    
    public int getChunkY() {
        return this.y / 10;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getX2() {
        return this.x2;
    }
    
    public int getY2() {
        return this.y2;
    }
    
    public int getW() {
        return this.x2 - this.x;
    }
    
    public int getH() {
        return this.y2 - this.y;
    }
    
    public int getID() {
        return this.ID;
    }
    
    public void refreshSquares() {
        for (int i = 0; i < this.rooms.size(); ++i) {
            this.rooms.get(i).refreshSquares();
        }
    }
    
    public void CalculateBounds(final ArrayList<RoomDef> list) {
        for (int i = 0; i < this.rooms.size(); ++i) {
            final RoomDef roomDef = this.rooms.get(i);
            for (int j = 0; j < roomDef.rects.size(); ++j) {
                final RoomDef.RoomRect roomRect = roomDef.rects.get(j);
                if (roomRect.x < this.x) {
                    this.x = roomRect.x;
                }
                if (roomRect.y < this.y) {
                    this.y = roomRect.y;
                }
                if (roomRect.x + roomRect.w > this.x2) {
                    this.x2 = roomRect.x + roomRect.w;
                }
                if (roomRect.y + roomRect.h > this.y2) {
                    this.y2 = roomRect.y + roomRect.h;
                }
            }
        }
        for (int k = 0; k < this.emptyoutside.size(); ++k) {
            final RoomDef roomDef2 = this.emptyoutside.get(k);
            for (int l = 0; l < roomDef2.rects.size(); ++l) {
                final RoomDef.RoomRect roomRect2 = roomDef2.rects.get(l);
                if (roomRect2.x < this.x) {
                    this.x = roomRect2.x;
                }
                if (roomRect2.y < this.y) {
                    this.y = roomRect2.y;
                }
                if (roomRect2.x + roomRect2.w > this.x2) {
                    this.x2 = roomRect2.x + roomRect2.w;
                }
                if (roomRect2.y + roomRect2.h > this.y2) {
                    this.y2 = roomRect2.y + roomRect2.h;
                }
            }
        }
        (this.overlappedChunks = new TShortArrayList(((this.x2 + 0) / 10 - this.x / 10 + 1) * ((this.y2 + 0) / 10 - this.y / 10 + 1) * 2)).clear();
        list.clear();
        list.addAll(this.rooms);
        list.addAll(this.emptyoutside);
        for (int index = 0; index < list.size(); ++index) {
            final RoomDef roomDef3 = list.get(index);
            for (int index2 = 0; index2 < roomDef3.rects.size(); ++index2) {
                final RoomDef.RoomRect roomRect3 = roomDef3.rects.get(index2);
                final int n = roomRect3.x / 10;
                final int n2 = roomRect3.y / 10;
                final int n3 = (roomRect3.x + roomRect3.w + 0) / 10;
                for (int n4 = (roomRect3.y + roomRect3.h + 0) / 10, n5 = n2; n5 <= n4; ++n5) {
                    for (int n6 = n; n6 <= n3; ++n6) {
                        if (!this.overlapsChunk(n6, n5)) {
                            this.overlappedChunks.add((short)n6);
                            this.overlappedChunks.add((short)n5);
                        }
                    }
                }
            }
        }
    }
    
    public void recalculate() {
        this.food = 0;
        this.items.clear();
        this.itemTypes.clear();
        for (int i = 0; i < this.rooms.size(); ++i) {
            final IsoRoom isoRoom = this.rooms.get(i).getIsoRoom();
            for (int j = 0; j < isoRoom.Containers.size(); ++j) {
                final ItemContainer itemContainer = isoRoom.Containers.get(j);
                for (int k = 0; k < itemContainer.Items.size(); ++k) {
                    final InventoryItem e = itemContainer.Items.get(k);
                    this.items.add(e);
                    this.itemTypes.add(e.getFullType());
                    if (e instanceof Food) {
                        ++this.food;
                    }
                }
            }
        }
    }
    
    public boolean overlapsChunk(final int n, final int n2) {
        for (int i = 0; i < this.overlappedChunks.size(); i += 2) {
            if (n == this.overlappedChunks.get(i) && n2 == this.overlappedChunks.get(i + 1)) {
                return true;
            }
        }
        return false;
    }
    
    public IsoGridSquare getFreeSquareInRoom() {
        BuildingDef.squareChoices.clear();
        for (int i = 0; i < this.rooms.size(); ++i) {
            final RoomDef roomDef = this.rooms.get(i);
            for (int j = 0; j < roomDef.rects.size(); ++j) {
                final RoomDef.RoomRect roomRect = roomDef.rects.get(j);
                for (int k = roomRect.getX(); k < roomRect.getX2(); ++k) {
                    for (int l = roomRect.getY(); l < roomRect.getY2(); ++l) {
                        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(k, l, roomDef.getZ());
                        if (gridSquare != null && gridSquare.isFree(false)) {
                            BuildingDef.squareChoices.add(gridSquare);
                        }
                    }
                }
            }
        }
        if (!BuildingDef.squareChoices.isEmpty()) {
            return BuildingDef.squareChoices.get(Rand.Next(BuildingDef.squareChoices.size()));
        }
        return null;
    }
    
    public boolean containsRoom(final String anObject) {
        for (int i = 0; i < this.rooms.size(); ++i) {
            if (this.rooms.get(i).name.equals(anObject)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isFullyStreamedIn() {
        for (int i = 0; i < this.overlappedChunks.size(); i += 2) {
            final short value = this.overlappedChunks.get(i);
            final short value2 = this.overlappedChunks.get(i + 1);
            if ((GameServer.bServer ? ServerMap.instance.getChunk(value, value2) : IsoWorld.instance.CurrentCell.getChunk(value, value2)) == null) {
                return false;
            }
        }
        return true;
    }
    
    public IsoMetaGrid.Zone getZone() {
        return this.zone;
    }
    
    public int getKeyId() {
        return this.keyId;
    }
    
    public void setKeyId(final int keyId) {
        this.keyId = keyId;
    }
    
    public int getKeySpawned() {
        return this.keySpawned;
    }
    
    public void setKeySpawned(final int keySpawned) {
        this.keySpawned = keySpawned;
    }
    
    public boolean isHasBeenVisited() {
        return this.hasBeenVisited;
    }
    
    public void setHasBeenVisited(final boolean hasBeenVisited) {
        if (hasBeenVisited && !this.hasBeenVisited) {
            StashSystem.visitedBuilding(this);
        }
        this.hasBeenVisited = hasBeenVisited;
    }
    
    public boolean isAlarmed() {
        return this.bAlarmed;
    }
    
    public void setAlarmed(final boolean bAlarmed) {
        this.bAlarmed = bAlarmed;
    }
    
    public RoomDef getRandomRoom(final int n) {
        RoomDef roomDef = this.getRooms().get(Rand.Next(0, this.getRooms().size()));
        if (n > 0 && roomDef.area >= n) {
            return roomDef;
        }
        int i = 0;
        while (i <= 20) {
            ++i;
            roomDef = this.getRooms().get(Rand.Next(0, this.getRooms().size()));
            if (roomDef.area >= n) {
                return roomDef;
            }
        }
        return roomDef;
    }
    
    public void Dispose() {
        final Iterator<RoomDef> iterator = this.rooms.iterator();
        while (iterator.hasNext()) {
            iterator.next().Dispose();
        }
        this.emptyoutside.clear();
        this.rooms.clear();
    }
    
    static {
        squareChoices = new ArrayList<IsoGridSquare>();
    }
}
