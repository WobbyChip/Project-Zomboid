// 
// Decompiled by Procyon v0.5.36
// 

package zombie.popman;

import zombie.iso.RoomDef;
import zombie.iso.IsoWorld;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import java.util.ArrayList;

final class PlayerSpawns
{
    private final ArrayList<PlayerSpawn> playerSpawns;
    
    PlayerSpawns() {
        this.playerSpawns = new ArrayList<PlayerSpawn>();
    }
    
    public void addSpawn(final int n, final int n2, final int n3) {
        final PlayerSpawn e = new PlayerSpawn(n, n2, n3);
        if (e.building != null) {
            this.playerSpawns.add(e);
        }
    }
    
    public void update() {
        final long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < this.playerSpawns.size(); ++i) {
            final PlayerSpawn playerSpawn = this.playerSpawns.get(i);
            if (playerSpawn.counter == -1L) {
                playerSpawn.counter = currentTimeMillis;
            }
            if (playerSpawn.counter + 10000L <= currentTimeMillis) {
                this.playerSpawns.remove(i--);
            }
        }
    }
    
    public boolean allowZombie(final IsoGridSquare isoGridSquare) {
        for (int i = 0; i < this.playerSpawns.size(); ++i) {
            if (!this.playerSpawns.get(i).allowZombie(isoGridSquare)) {
                return false;
            }
        }
        return true;
    }
    
    private static class PlayerSpawn
    {
        public int x;
        public int y;
        public long counter;
        public BuildingDef building;
        
        public PlayerSpawn(final int x, final int y, final int n) {
            this.x = x;
            this.y = y;
            this.counter = -1L;
            final RoomDef room = IsoWorld.instance.getMetaGrid().getRoomAt(x, y, n);
            if (room != null) {
                this.building = room.getBuilding();
            }
        }
        
        public boolean allowZombie(final IsoGridSquare isoGridSquare) {
            return this.building == null || ((isoGridSquare.getBuilding() == null || this.building != isoGridSquare.getBuilding().getDef()) && (isoGridSquare.getX() < this.building.getX() - 15 || isoGridSquare.getX() >= this.building.getX2() + 15 || isoGridSquare.getY() < this.building.getY() - 15 || isoGridSquare.getY() >= this.building.getY2() + 15));
        }
    }
}
