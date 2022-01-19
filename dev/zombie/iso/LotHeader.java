// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import java.util.ArrayList;
import java.util.HashMap;

public final class LotHeader
{
    public int width;
    public int height;
    public int levels;
    public int version;
    public final HashMap<Integer, RoomDef> Rooms;
    public final ArrayList<RoomDef> RoomList;
    public final ArrayList<BuildingDef> Buildings;
    public final HashMap<Integer, IsoRoom> isoRooms;
    public final HashMap<Integer, IsoBuilding> isoBuildings;
    public boolean bFixed2x;
    protected final ArrayList<String> tilesUsed;
    
    public LotHeader() {
        this.width = 0;
        this.height = 0;
        this.levels = 0;
        this.version = 0;
        this.Rooms = new HashMap<Integer, RoomDef>();
        this.RoomList = new ArrayList<RoomDef>();
        this.Buildings = new ArrayList<BuildingDef>();
        this.isoRooms = new HashMap<Integer, IsoRoom>();
        this.isoBuildings = new HashMap<Integer, IsoBuilding>();
        this.tilesUsed = new ArrayList<String>();
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getLevels() {
        return this.levels;
    }
    
    public IsoRoom getRoom(final int n) {
        final RoomDef roomDef = this.Rooms.get(n);
        if (!this.isoRooms.containsKey(n)) {
            final IsoRoom isoRoom = new IsoRoom();
            isoRoom.rects.addAll(roomDef.rects);
            isoRoom.RoomDef = roomDef.name;
            isoRoom.def = roomDef;
            isoRoom.layer = roomDef.level;
            IsoWorld.instance.CurrentCell.getRoomList().add(isoRoom);
            if (roomDef.building == null) {
                roomDef.building = new BuildingDef();
                roomDef.building.ID = this.Buildings.size();
                roomDef.building.rooms.add(roomDef);
                roomDef.building.CalculateBounds(new ArrayList<RoomDef>());
                this.Buildings.add(roomDef.building);
            }
            final int id = roomDef.building.ID;
            this.isoRooms.put(n, isoRoom);
            if (!this.isoBuildings.containsKey(id)) {
                isoRoom.building = new IsoBuilding();
                isoRoom.building.def = roomDef.building;
                this.isoBuildings.put(id, isoRoom.building);
                isoRoom.building.CreateFrom(roomDef.building, this);
            }
            else {
                isoRoom.building = this.isoBuildings.get(id);
            }
            return isoRoom;
        }
        return this.isoRooms.get(n);
    }
    
    @Deprecated
    public int getRoomAt(final int n, final int n2, final int n3) {
        for (final Map.Entry<Integer, RoomDef> entry : this.Rooms.entrySet()) {
            final RoomDef roomDef = entry.getValue();
            for (int i = 0; i < roomDef.rects.size(); ++i) {
                final RoomDef.RoomRect roomRect = roomDef.rects.get(i);
                if (roomRect.x <= n && roomRect.y <= n2 && roomDef.level == n3 && roomRect.getX2() > n && roomRect.getY2() > n2) {
                    return entry.getKey();
                }
            }
        }
        return -1;
    }
}
