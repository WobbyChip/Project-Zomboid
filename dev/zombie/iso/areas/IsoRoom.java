// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas;

import zombie.iso.MetaObject;
import zombie.network.GameServer;
import zombie.iso.IsoWorld;
import zombie.VirtualZombieManager;
import java.util.Collection;
import java.util.Iterator;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.RoomDef;
import zombie.iso.IsoObject;
import zombie.iso.IsoRoomLight;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoWindow;
import zombie.inventory.ItemContainer;
import java.awt.Rectangle;
import java.util.Vector;
import zombie.iso.IsoGridSquare;
import java.util.ArrayList;

public final class IsoRoom
{
    private static final ArrayList<IsoGridSquare> tempSquares;
    public final Vector<IsoGridSquare> Beds;
    public Rectangle bounds;
    public IsoBuilding building;
    public final ArrayList<ItemContainer> Containers;
    public final ArrayList<IsoWindow> Windows;
    public final Vector<IsoRoomExit> Exits;
    public int layer;
    public String RoomDef;
    public final Vector<IsoGridSquare> TileList;
    public int transparentWalls;
    public final ArrayList<IsoLightSwitch> lightSwitches;
    public final ArrayList<IsoRoomLight> roomLights;
    public final ArrayList<IsoObject> WaterSources;
    public int seen;
    public int visited;
    public RoomDef def;
    public final ArrayList<RoomDef.RoomRect> rects;
    public final ArrayList<IsoGridSquare> Squares;
    
    public IsoRoom() {
        this.Beds = new Vector<IsoGridSquare>();
        this.building = null;
        this.Containers = new ArrayList<ItemContainer>();
        this.Windows = new ArrayList<IsoWindow>();
        this.Exits = new Vector<IsoRoomExit>();
        this.RoomDef = "none";
        this.TileList = new Vector<IsoGridSquare>();
        this.transparentWalls = 0;
        this.lightSwitches = new ArrayList<IsoLightSwitch>();
        this.roomLights = new ArrayList<IsoRoomLight>();
        this.WaterSources = new ArrayList<IsoObject>();
        this.seen = 1000000000;
        this.visited = 1000000000;
        this.rects = new ArrayList<RoomDef.RoomRect>(1);
        this.Squares = new ArrayList<IsoGridSquare>();
    }
    
    public IsoBuilding getBuilding() {
        return this.building;
    }
    
    public String getName() {
        return this.RoomDef;
    }
    
    public IsoBuilding CreateBuilding(final IsoCell isoCell) {
        final IsoBuilding isoBuilding = new IsoBuilding(isoCell);
        this.AddToBuilding(isoBuilding);
        return isoBuilding;
    }
    
    public boolean isInside(final int n, final int n2, final int n3) {
        for (int i = 0; i < this.rects.size(); ++i) {
            final int x = this.rects.get(i).x;
            final int y = this.rects.get(i).y;
            final int x2 = this.rects.get(i).getX2();
            final int y2 = this.rects.get(i).getY2();
            if (n >= x && n2 >= y && n < x2 && n2 < y2 && n3 == this.layer) {
                return true;
            }
        }
        return false;
    }
    
    public IsoGridSquare getFreeTile() {
        int n = 0;
        IsoGridSquare isoGridSquare = null;
        int n2 = 100;
        while (n == 0 && n2 > 0) {
            --n2;
            n = 1;
            if (this.TileList.isEmpty()) {
                return null;
            }
            isoGridSquare = this.TileList.get(Rand.Next(this.TileList.size()));
            for (int i = 0; i < this.Exits.size(); ++i) {
                if (isoGridSquare.getX() == this.Exits.get(i).x && isoGridSquare.getY() == this.Exits.get(i).y) {
                    n = 0;
                }
            }
            if (n == 0 || isoGridSquare.isFree(true)) {
                continue;
            }
            n = 0;
        }
        if (n2 < 0) {
            return null;
        }
        return isoGridSquare;
    }
    
    void AddToBuilding(final IsoBuilding building) {
        (this.building = building).AddRoom(this);
        for (final IsoRoomExit isoRoomExit : this.Exits) {
            if (isoRoomExit.To.From != null && isoRoomExit.To.From.building == null) {
                isoRoomExit.To.From.AddToBuilding(building);
            }
        }
    }
    
    public ArrayList<IsoObject> getWaterSources() {
        return this.WaterSources;
    }
    
    public void setWaterSources(final ArrayList<IsoObject> c) {
        this.WaterSources.clear();
        this.WaterSources.addAll(c);
    }
    
    public boolean hasWater() {
        if (this.WaterSources.isEmpty()) {
            return false;
        }
        final Iterator<IsoObject> iterator = this.WaterSources.iterator();
        while (iterator != null && iterator.hasNext()) {
            if (iterator.next().hasWater()) {
                return true;
            }
        }
        return false;
    }
    
    public void useWater() {
        if (this.WaterSources.isEmpty()) {
            return;
        }
        final Iterator<IsoObject> iterator = this.WaterSources.iterator();
        while (iterator != null && iterator.hasNext()) {
            final IsoObject isoObject = iterator.next();
            if (isoObject.hasWater()) {
                isoObject.useWater(1);
                break;
            }
        }
    }
    
    public ArrayList<IsoWindow> getWindows() {
        return this.Windows;
    }
    
    public void addSquare(final IsoGridSquare isoGridSquare) {
        if (this.Squares.contains(isoGridSquare)) {
            return;
        }
        this.Squares.add(isoGridSquare);
    }
    
    public void refreshSquares() {
        this.Windows.clear();
        this.Containers.clear();
        this.WaterSources.clear();
        this.Exits.clear();
        IsoRoom.tempSquares.clear();
        IsoRoom.tempSquares.addAll(this.Squares);
        this.Squares.clear();
        for (int i = 0; i < IsoRoom.tempSquares.size(); ++i) {
            this.addSquare(IsoRoom.tempSquares.get(i));
        }
    }
    
    private void addExitTo(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        IsoRoom room = null;
        IsoRoom room2 = null;
        if (isoGridSquare != null) {
            room = isoGridSquare.getRoom();
        }
        if (isoGridSquare2 != null) {
            room2 = isoGridSquare2.getRoom();
        }
        if (room == null && room2 == null) {
            return;
        }
        IsoRoom isoRoom;
        if ((isoRoom = room) == null) {
            isoRoom = room2;
        }
        final IsoRoomExit isoRoomExit = new IsoRoomExit(isoRoom, isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ());
        isoRoomExit.type = IsoRoomExit.ExitType.Door;
        if (isoRoom == room) {
            if (room2 != null) {
                IsoRoomExit exit = room2.getExitAt(isoGridSquare2.getX(), isoGridSquare2.getY(), isoGridSquare2.getZ());
                if (exit == null) {
                    exit = new IsoRoomExit(room2, isoGridSquare2.getX(), isoGridSquare2.getY(), isoGridSquare2.getZ());
                    room2.Exits.add(exit);
                }
                isoRoomExit.To = exit;
            }
            else {
                room.building.Exits.add(isoRoomExit);
                if (isoGridSquare2 != null) {
                    isoRoomExit.To = new IsoRoomExit(isoRoomExit, isoGridSquare2.getX(), isoGridSquare2.getY(), isoGridSquare2.getZ());
                }
            }
            room.Exits.add(isoRoomExit);
        }
        else {
            room2.building.Exits.add(isoRoomExit);
            if (isoGridSquare2 != null) {
                isoRoomExit.To = new IsoRoomExit(isoRoomExit, isoGridSquare2.getX(), isoGridSquare2.getY(), isoGridSquare2.getZ());
            }
            room2.Exits.add(isoRoomExit);
        }
    }
    
    private IsoRoomExit getExitAt(final int n, final int n2, final int n3) {
        for (int i = 0; i < this.Exits.size(); ++i) {
            final IsoRoomExit isoRoomExit = this.Exits.get(i);
            if (isoRoomExit.x == n && isoRoomExit.y == n2 && isoRoomExit.layer == n3) {
                return isoRoomExit;
            }
        }
        return null;
    }
    
    public void removeSquare(final IsoGridSquare o) {
        this.Squares.remove(o);
        final IsoRoomExit exit = this.getExitAt(o.getX(), o.getY(), o.getZ());
        if (exit != null) {
            this.Exits.remove(exit);
            if (exit.To != null) {
                exit.From = null;
            }
            if (this.building.Exits.contains(exit)) {
                this.building.Exits.remove(exit);
            }
        }
        for (int i = 0; i < o.getObjects().size(); ++i) {
            final IsoObject o2 = o.getObjects().get(i);
            if (o2 instanceof IsoLightSwitch) {
                this.lightSwitches.remove(o2);
            }
        }
    }
    
    public void spawnZombies() {
        VirtualZombieManager.instance.addZombiesToMap(1, this.def, false);
    }
    
    public void onSee() {
        for (int i = 0; i < this.getBuilding().Rooms.size(); ++i) {
            final IsoRoom isoRoom = this.getBuilding().Rooms.elementAt(i);
            if (isoRoom != null && !isoRoom.def.bExplored) {
                isoRoom.def.bExplored = true;
            }
            IsoWorld.instance.getCell().roomSpotted(isoRoom);
        }
    }
    
    public Vector<IsoGridSquare> getTileList() {
        return this.TileList;
    }
    
    public ArrayList<IsoGridSquare> getSquares() {
        return this.Squares;
    }
    
    public ArrayList<ItemContainer> getContainer() {
        return this.Containers;
    }
    
    public IsoGridSquare getRandomSquare() {
        if (this.Squares.isEmpty()) {
            return null;
        }
        return this.Squares.get(Rand.Next(this.Squares.size()));
    }
    
    public IsoGridSquare getRandomFreeSquare() {
        int i = 100;
        if (GameServer.bServer) {
            while (i > 0) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(this.def.getX() + Rand.Next(this.def.getW()), this.def.getY() + Rand.Next(this.def.getH()), this.def.level);
                if (gridSquare != null && gridSquare.getRoom() == this && gridSquare.isFree(true)) {
                    return gridSquare;
                }
                --i;
            }
            return null;
        }
        if (this.Squares.isEmpty()) {
            return null;
        }
        while (i > 0) {
            final IsoGridSquare isoGridSquare = this.Squares.get(Rand.Next(this.Squares.size()));
            if (isoGridSquare.isFree(true)) {
                return isoGridSquare;
            }
            --i;
        }
        return null;
    }
    
    public boolean hasLightSwitches() {
        if (!this.lightSwitches.isEmpty()) {
            return true;
        }
        for (int i = 0; i < this.def.objects.size(); ++i) {
            if (this.def.objects.get(i).getType() == 7) {
                return true;
            }
        }
        return false;
    }
    
    public void createLights(final boolean b) {
        if (!this.roomLights.isEmpty()) {
            return;
        }
        for (int i = 0; i < this.def.rects.size(); ++i) {
            final RoomDef.RoomRect roomRect = this.def.rects.get(i);
            this.roomLights.add(new IsoRoomLight(this, roomRect.x, roomRect.y, this.def.level, roomRect.w, roomRect.h));
        }
    }
    
    public RoomDef getRoomDef() {
        return this.def;
    }
    
    public ArrayList<IsoLightSwitch> getLightSwitches() {
        return this.lightSwitches;
    }
    
    static {
        tempSquares = new ArrayList<IsoGridSquare>();
    }
}
