// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.Set;
import zombie.core.math.PZMath;
import zombie.Lua.LuaEventManager;
import zombie.iso.objects.IsoMannequin;
import java.util.ArrayList;

public final class IsoMetaCell
{
    public final ArrayList<IsoMetaGrid.VehicleZone> vehicleZones;
    public final IsoMetaChunk[] ChunkMap;
    public LotHeader info;
    public final ArrayList<IsoMetaGrid.Trigger> triggers;
    private int wx;
    private int wy;
    public final ArrayList<IsoMannequin.MannequinZone> mannequinZones;
    
    public IsoMetaCell(final int wx, final int wy) {
        this.vehicleZones = new ArrayList<IsoMetaGrid.VehicleZone>();
        this.ChunkMap = new IsoMetaChunk[900];
        this.info = null;
        this.triggers = new ArrayList<IsoMetaGrid.Trigger>();
        this.wx = 0;
        this.wy = 0;
        this.mannequinZones = new ArrayList<IsoMannequin.MannequinZone>();
        this.wx = wx;
        this.wy = wy;
        for (int i = 0; i < 900; ++i) {
            this.ChunkMap[i] = new IsoMetaChunk();
        }
    }
    
    public void addTrigger(final BuildingDef buildingDef, final int n, final int n2, final String s) {
        this.triggers.add(new IsoMetaGrid.Trigger(buildingDef, n, n2, s));
    }
    
    public void checkTriggers() {
        if (IsoCamera.CamCharacter == null) {
            return;
        }
        final int n = (int)IsoCamera.CamCharacter.getX();
        final int n2 = (int)IsoCamera.CamCharacter.getY();
        for (int i = 0; i < this.triggers.size(); ++i) {
            final IsoMetaGrid.Trigger trigger = this.triggers.get(i);
            if (n >= trigger.def.x - trigger.triggerRange && n <= trigger.def.x2 + trigger.triggerRange && n2 >= trigger.def.y - trigger.triggerRange && n2 <= trigger.def.y2 + trigger.triggerRange) {
                if (!trigger.triggered) {
                    LuaEventManager.triggerEvent("OnTriggerNPCEvent", trigger.type, trigger.data, trigger.def);
                }
                LuaEventManager.triggerEvent("OnMultiTriggerNPCEvent", trigger.type, trigger.data, trigger.def);
                trigger.triggered = true;
            }
        }
    }
    
    public IsoMetaChunk getChunk(final int n, final int n2) {
        if (n2 >= 30 || n >= 30 || n < 0 || n2 < 0) {
            return null;
        }
        return this.ChunkMap[n2 * 30 + n];
    }
    
    public void addZone(final IsoMetaGrid.Zone zone, final int n, final int n2) {
        final int n3 = zone.x / 10;
        final int n4 = zone.y / 10;
        int n5 = (zone.x + zone.w) / 10;
        if ((zone.x + zone.w) % 10 == 0) {
            --n5;
        }
        int n6 = (zone.y + zone.h) / 10;
        if ((zone.y + zone.h) % 10 == 0) {
            --n6;
        }
        final int clamp = PZMath.clamp(n3, n / 10, (n + 300) / 10);
        final int clamp2 = PZMath.clamp(n4, n2 / 10, (n2 + 300) / 10);
        final int clamp3 = PZMath.clamp(n5, n / 10, (n + 300) / 10 - 1);
        for (int clamp4 = PZMath.clamp(n6, n2 / 10, (n2 + 300) / 10 - 1), i = clamp2; i <= clamp4; ++i) {
            for (int j = clamp; j <= clamp3; ++j) {
                if (zone.intersects(j * 10, i * 10, zone.z, 10, 10)) {
                    final int n7 = j - n / 10 + (i - n2 / 10) * 30;
                    if (this.ChunkMap[n7] != null) {
                        this.ChunkMap[n7].addZone(zone);
                    }
                }
            }
        }
    }
    
    public void removeZone(final IsoMetaGrid.Zone zone) {
        int n = (zone.x + zone.w) / 10;
        if ((zone.x + zone.w) % 10 == 0) {
            --n;
        }
        int n2 = (zone.y + zone.h) / 10;
        if ((zone.y + zone.h) % 10 == 0) {
            --n2;
        }
        final int n3 = this.wx * 300;
        final int n4 = this.wy * 300;
        for (int i = zone.y / 10; i <= n2; ++i) {
            for (int j = zone.x / 10; j <= n; ++j) {
                if (j >= n3 / 10 && j < (n3 + 300) / 10 && i >= n4 / 10 && i < (n4 + 300) / 10) {
                    final int n5 = j - n3 / 10 + (i - n4 / 10) * 30;
                    if (this.ChunkMap[n5] != null) {
                        this.ChunkMap[n5].removeZone(zone);
                    }
                }
            }
        }
    }
    
    public void addRoom(final RoomDef roomDef, final int n, final int n2) {
        int n3 = roomDef.x2 / 10;
        if (roomDef.x2 % 10 == 0) {
            --n3;
        }
        int n4 = roomDef.y2 / 10;
        if (roomDef.y2 % 10 == 0) {
            --n4;
        }
        for (int i = roomDef.y / 10; i <= n4; ++i) {
            for (int j = roomDef.x / 10; j <= n3; ++j) {
                if (j >= n / 10 && j < (n + 300) / 10 && i >= n2 / 10 && i < (n2 + 300) / 10) {
                    final int n5 = j - n / 10 + (i - n2 / 10) * 30;
                    if (this.ChunkMap[n5] != null) {
                        this.ChunkMap[n5].addRoom(roomDef);
                    }
                }
            }
        }
    }
    
    public void getZonesUnique(final Set<IsoMetaGrid.Zone> set) {
        for (int i = 0; i < this.ChunkMap.length; ++i) {
            final IsoMetaChunk isoMetaChunk = this.ChunkMap[i];
            if (isoMetaChunk != null) {
                isoMetaChunk.getZonesUnique(set);
            }
        }
    }
    
    public void getZonesIntersecting(final int n, final int n2, final int n3, final int n4, final int n5, final ArrayList<IsoMetaGrid.Zone> list) {
        int n6 = (n + n4) / 10;
        if ((n + n4) % 10 == 0) {
            --n6;
        }
        int n7 = (n2 + n5) / 10;
        if ((n2 + n5) % 10 == 0) {
            --n7;
        }
        final int n8 = this.wx * 300;
        final int n9 = this.wy * 300;
        for (int i = n2 / 10; i <= n7; ++i) {
            for (int j = n / 10; j <= n6; ++j) {
                if (j >= n8 / 10 && j < (n8 + 300) / 10 && i >= n9 / 10 && i < (n9 + 300) / 10) {
                    final int n10 = j - n8 / 10 + (i - n9 / 10) * 30;
                    if (this.ChunkMap[n10] != null) {
                        this.ChunkMap[n10].getZonesIntersecting(n, n2, n3, n4, n5, list);
                    }
                }
            }
        }
    }
    
    public void getRoomsIntersecting(final int n, final int n2, final int n3, final int n4, final ArrayList<RoomDef> list) {
        int n5 = (n + n3) / 10;
        if ((n + n3) % 10 == 0) {
            --n5;
        }
        int n6 = (n2 + n4) / 10;
        if ((n2 + n4) % 10 == 0) {
            --n6;
        }
        final int n7 = this.wx * 300;
        final int n8 = this.wy * 300;
        for (int i = n2 / 10; i <= n6; ++i) {
            for (int j = n / 10; j <= n5; ++j) {
                if (j >= n7 / 10 && j < (n7 + 300) / 10 && i >= n8 / 10 && i < (n8 + 300) / 10) {
                    final int n9 = j - n7 / 10 + (i - n8 / 10) * 30;
                    if (this.ChunkMap[n9] != null) {
                        this.ChunkMap[n9].getRoomsIntersecting(n, n2, n3, n4, list);
                    }
                }
            }
        }
    }
    
    public void Dispose() {
        for (int i = 0; i < this.ChunkMap.length; ++i) {
            final IsoMetaChunk isoMetaChunk = this.ChunkMap[i];
            if (isoMetaChunk != null) {
                isoMetaChunk.Dispose();
                this.ChunkMap[i] = null;
            }
        }
    }
}
