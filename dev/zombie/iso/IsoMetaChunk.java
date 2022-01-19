// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.Arrays;
import java.util.Set;
import java.util.ArrayList;
import zombie.core.Rand;
import zombie.SandboxOptions;

public final class IsoMetaChunk
{
    public static final float zombiesMinPerChunk = 0.06f;
    public static final float zombiesFullPerChunk = 12.0f;
    private int ZombieIntensity;
    private IsoMetaGrid.Zone[] zones;
    private int zonesSize;
    private RoomDef[] rooms;
    private int roomsSize;
    
    public IsoMetaChunk() {
        this.ZombieIntensity = 0;
    }
    
    public float getZombieIntensity(final boolean b) {
        float n = (float)this.ZombieIntensity;
        if (SandboxOptions.instance.Distribution.getValue() == 2) {
            n = 128.0f;
        }
        float n2 = n * 0.5f;
        if (SandboxOptions.instance.Zombies.getValue() == 1) {
            n2 *= 4.0f;
        }
        else if (SandboxOptions.instance.Zombies.getValue() == 2) {
            n2 *= 3.0f;
        }
        else if (SandboxOptions.instance.Zombies.getValue() == 3) {
            n2 *= 2.0f;
        }
        else if (SandboxOptions.instance.Zombies.getValue() == 5) {
            n2 *= 0.35f;
        }
        else if (SandboxOptions.instance.Zombies.getValue() == 6) {
            n2 = 0.0f;
        }
        final float n3 = n2 / 255.0f;
        float n4 = 0.06f + 11.94f * n3;
        if (!b) {
            return n4;
        }
        final float n5 = n3 * 10.0f;
        if (Rand.Next(3) == 0) {
            return 0.0f;
        }
        final float n6 = n5 * 0.5f;
        int n7 = 1000;
        if (SandboxOptions.instance.Zombies.getValue() == 1) {
            n7 /= (int)2.0f;
        }
        else if (SandboxOptions.instance.Zombies.getValue() == 2) {
            n7 /= (int)1.7f;
        }
        else if (SandboxOptions.instance.Zombies.getValue() == 3) {
            n7 /= (int)1.5f;
        }
        else if (SandboxOptions.instance.Zombies.getValue() == 5) {
            n7 *= (int)1.5f;
        }
        if (Rand.Next(n7) < n6 && IsoWorld.getZombiesEnabled()) {
            n4 = 120.0f;
            if (n4 > 12.0f) {
                n4 = 12.0f;
            }
        }
        return n4;
    }
    
    public float getZombieIntensity() {
        return this.getZombieIntensity(true);
    }
    
    public void setZombieIntensity(final int zombieIntensity) {
        if (zombieIntensity >= 0) {
            this.ZombieIntensity = zombieIntensity;
        }
    }
    
    public float getLootZombieIntensity() {
        final float n = this.ZombieIntensity / 255.0f;
        float n2 = 0.06f + 11.94f * n;
        if (Rand.Next(300) <= n * 10.0f) {
            n2 = 120.0f;
        }
        if (IsoWorld.getZombiesDisabled()) {
            return 400.0f;
        }
        return n2;
    }
    
    public int getUnadjustedZombieIntensity() {
        return this.ZombieIntensity;
    }
    
    public void addZone(final IsoMetaGrid.Zone zone) {
        if (this.zones == null) {
            this.zones = new IsoMetaGrid.Zone[8];
        }
        if (this.zonesSize == this.zones.length) {
            final IsoMetaGrid.Zone[] zones = new IsoMetaGrid.Zone[this.zones.length + 8];
            System.arraycopy(this.zones, 0, zones, 0, this.zonesSize);
            this.zones = zones;
        }
        this.zones[this.zonesSize++] = zone;
    }
    
    public void removeZone(final IsoMetaGrid.Zone zone) {
        if (this.zones == null) {
            return;
        }
        for (int i = 0; i < this.zonesSize; ++i) {
            if (this.zones[i] == zone) {
                while (i < this.zonesSize - 1) {
                    this.zones[i] = this.zones[i + 1];
                    ++i;
                }
                this.zones[this.zonesSize - 1] = null;
                --this.zonesSize;
                break;
            }
        }
    }
    
    public IsoMetaGrid.Zone getZone(final int n) {
        if (n < 0 || n >= this.zonesSize) {
            return null;
        }
        return this.zones[n];
    }
    
    public IsoMetaGrid.Zone getZoneAt(final int n, final int n2, final int n3) {
        if (this.zones == null || this.zonesSize <= 0) {
            return null;
        }
        IsoMetaGrid.Zone zone = null;
        for (int i = this.zonesSize - 1; i >= 0; --i) {
            final IsoMetaGrid.Zone zone2 = this.zones[i];
            if (zone2.contains(n, n2, n3)) {
                if (zone2.isPreferredZoneForSquare) {
                    return zone2;
                }
                if (zone == null) {
                    zone = zone2;
                }
            }
        }
        return zone;
    }
    
    public ArrayList<IsoMetaGrid.Zone> getZonesAt(final int n, final int n2, final int n3, final ArrayList<IsoMetaGrid.Zone> list) {
        for (int i = 0; i < this.zonesSize; ++i) {
            final IsoMetaGrid.Zone e = this.zones[i];
            if (e.contains(n, n2, n3)) {
                list.add(e);
            }
        }
        return list;
    }
    
    public void getZonesUnique(final Set<IsoMetaGrid.Zone> set) {
        for (int i = 0; i < this.zonesSize; ++i) {
            set.add(this.zones[i]);
        }
    }
    
    public void getZonesIntersecting(final int n, final int n2, final int n3, final int n4, final int n5, final ArrayList<IsoMetaGrid.Zone> list) {
        for (int i = 0; i < this.zonesSize; ++i) {
            final IsoMetaGrid.Zone zone = this.zones[i];
            if (!list.contains(zone) && zone.intersects(n, n2, n3, n4, n5)) {
                list.add(zone);
            }
        }
    }
    
    public void clearZones() {
        if (this.zones != null) {
            for (int i = 0; i < this.zones.length; ++i) {
                this.zones[i] = null;
            }
        }
        this.zones = null;
        this.zonesSize = 0;
    }
    
    public void clearRooms() {
        if (this.rooms != null) {
            for (int i = 0; i < this.rooms.length; ++i) {
                this.rooms[i] = null;
            }
        }
        this.rooms = null;
        this.roomsSize = 0;
    }
    
    public int numZones() {
        return this.zonesSize;
    }
    
    public void addRoom(final RoomDef roomDef) {
        if (this.rooms == null) {
            this.rooms = new RoomDef[8];
        }
        if (this.roomsSize == this.rooms.length) {
            final RoomDef[] rooms = new RoomDef[this.rooms.length + 8];
            System.arraycopy(this.rooms, 0, rooms, 0, this.roomsSize);
            this.rooms = rooms;
        }
        this.rooms[this.roomsSize++] = roomDef;
    }
    
    public RoomDef getRoomAt(final int n, final int n2, final int n3) {
        for (int i = 0; i < this.roomsSize; ++i) {
            final RoomDef roomDef = this.rooms[i];
            if (!roomDef.isEmptyOutside()) {
                if (roomDef.level == n3) {
                    for (int j = 0; j < roomDef.rects.size(); ++j) {
                        final RoomDef.RoomRect roomRect = roomDef.rects.get(j);
                        if (roomRect.x <= n && roomRect.y <= n2 && n < roomRect.getX2() && n2 < roomRect.getY2()) {
                            return roomDef;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public RoomDef getEmptyOutsideAt(final int n, final int n2, final int n3) {
        for (int i = 0; i < this.roomsSize; ++i) {
            final RoomDef roomDef = this.rooms[i];
            if (roomDef.isEmptyOutside()) {
                if (roomDef.level == n3) {
                    for (int j = 0; j < roomDef.rects.size(); ++j) {
                        final RoomDef.RoomRect roomRect = roomDef.rects.get(j);
                        if (roomRect.x <= n && roomRect.y <= n2 && n < roomRect.getX2() && n2 < roomRect.getY2()) {
                            return roomDef;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public int getNumRooms() {
        return this.roomsSize;
    }
    
    public void getRoomsIntersecting(final int n, final int n2, final int n3, final int n4, final ArrayList<RoomDef> list) {
        for (int i = 0; i < this.roomsSize; ++i) {
            final RoomDef roomDef = this.rooms[i];
            if (!roomDef.isEmptyOutside()) {
                if (!list.contains(roomDef) && roomDef.intersects(n, n2, n3, n4)) {
                    list.add(roomDef);
                }
            }
        }
    }
    
    public void Dispose() {
        if (this.rooms != null) {
            Arrays.fill(this.rooms, null);
        }
        if (this.zones != null) {
            Arrays.fill(this.zones, null);
        }
    }
}
