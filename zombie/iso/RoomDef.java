// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.List;
import zombie.util.list.PZArrayUtil;
import java.util.function.Predicate;
import zombie.iso.areas.IsoRoom;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.HashMap;
import java.util.ArrayList;

public final class RoomDef
{
    private static final ArrayList<IsoGridSquare> squareChoices;
    public boolean bExplored;
    public boolean bDoneSpawn;
    public int IndoorZombies;
    public int spawnCount;
    public boolean bLightsActive;
    public String name;
    public int level;
    public BuildingDef building;
    public int ID;
    public final ArrayList<RoomRect> rects;
    public final ArrayList<MetaObject> objects;
    public int x;
    public int y;
    public int x2;
    public int y2;
    public int area;
    private final HashMap<String, Integer> proceduralSpawnedContainer;
    private boolean roofFixed;
    
    public RoomDef(final int id, final String name) {
        this.bExplored = false;
        this.bDoneSpawn = false;
        this.IndoorZombies = 0;
        this.spawnCount = -1;
        this.bLightsActive = false;
        this.ID = -1;
        this.rects = new ArrayList<RoomRect>(1);
        this.objects = new ArrayList<MetaObject>(0);
        this.x = 100000;
        this.y = 100000;
        this.x2 = -10000;
        this.y2 = -10000;
        this.proceduralSpawnedContainer = new HashMap<String, Integer>();
        this.roofFixed = false;
        this.ID = id;
        this.name = name;
    }
    
    public int getID() {
        return this.ID;
    }
    
    public boolean isExplored() {
        return this.bExplored;
    }
    
    public boolean isInside(final int n, final int n2, final int n3) {
        final int x = this.building.x;
        final int y = this.building.y;
        for (int i = 0; i < this.rects.size(); ++i) {
            final int x2 = this.rects.get(i).x;
            final int y2 = this.rects.get(i).y;
            final int x3 = this.rects.get(i).getX2();
            final int y3 = this.rects.get(i).getY2();
            if (n >= x2 && n2 >= y2 && n < x3 && n2 < y3 && n3 == this.level) {
                return true;
            }
        }
        return false;
    }
    
    public boolean intersects(final int n, final int n2, final int n3, final int n4) {
        for (int i = 0; i < this.rects.size(); ++i) {
            final RoomRect roomRect = this.rects.get(i);
            if (n + n3 > roomRect.getX() && n < roomRect.getX2() && n2 + n4 > roomRect.getY() && n2 < roomRect.getY2()) {
                return true;
            }
        }
        return false;
    }
    
    public float getAreaOverlapping(final IsoChunk isoChunk) {
        return this.getAreaOverlapping(isoChunk.wx * 10, isoChunk.wy * 10, 10, 10);
    }
    
    public float getAreaOverlapping(final int a, final int a2, final int n, final int n2) {
        int n3 = 0;
        int n4 = 0;
        for (int i = 0; i < this.rects.size(); ++i) {
            final RoomRect roomRect = this.rects.get(i);
            n3 += roomRect.w * roomRect.h;
            final int max = Math.max(a, roomRect.x);
            final int max2 = Math.max(a2, roomRect.y);
            final int min = Math.min(a + n, roomRect.x + roomRect.w);
            final int min2 = Math.min(a2 + n2, roomRect.y + roomRect.h);
            if (min >= max && min2 >= max2) {
                n4 += (min - max) * (min2 - max2);
            }
        }
        if (n4 <= 0) {
            return 0.0f;
        }
        return n4 / (float)n3;
    }
    
    public void forEachChunk(final BiConsumer<RoomDef, IsoChunk> biConsumer) {
        final HashSet<Object> set = new HashSet<Object>();
        for (int i = 0; i < this.rects.size(); ++i) {
            final RoomRect roomRect = this.rects.get(i);
            final int n = roomRect.x / 10;
            final int n2 = roomRect.y / 10;
            int n3 = (roomRect.x + roomRect.w) / 10;
            int n4 = (roomRect.y + roomRect.h) / 10;
            if ((roomRect.x + roomRect.w) % 10 == 0) {
                --n3;
            }
            if ((roomRect.y + roomRect.h) % 10 == 0) {
                --n4;
            }
            for (int j = n2; j <= n4; ++j) {
                for (int k = n; k <= n3; ++k) {
                    final IsoChunk e = GameServer.bServer ? ServerMap.instance.getChunk(k, j) : IsoWorld.instance.CurrentCell.getChunk(k, j);
                    if (e != null) {
                        set.add(e);
                    }
                }
            }
        }
        set.forEach(isoChunk -> biConsumer.accept(this, isoChunk));
        set.clear();
    }
    
    public IsoRoom getIsoRoom() {
        return IsoWorld.instance.MetaGrid.getMetaGridFromTile(this.x, this.y).info.getRoom(this.ID);
    }
    
    public ArrayList<MetaObject> getObjects() {
        return this.objects;
    }
    
    public ArrayList<MetaObject> getMetaObjects() {
        return this.objects;
    }
    
    public void refreshSquares() {
        this.getIsoRoom().refreshSquares();
    }
    
    public BuildingDef getBuilding() {
        return this.building;
    }
    
    public void setBuilding(final BuildingDef building) {
        this.building = building;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ArrayList<RoomRect> getRects() {
        return this.rects;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getX() {
        return this.x;
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
    
    public int getZ() {
        return this.level;
    }
    
    public void CalculateBounds() {
        for (int i = 0; i < this.rects.size(); ++i) {
            final RoomRect roomRect = this.rects.get(i);
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
            this.area += roomRect.w * roomRect.h;
        }
    }
    
    public int getArea() {
        return this.area;
    }
    
    public void setExplored(final boolean bExplored) {
        this.bExplored = bExplored;
    }
    
    public IsoGridSquare getFreeSquare() {
        return this.getRandomSquare(isoGridSquare -> isoGridSquare.isFree(false));
    }
    
    public IsoGridSquare getRandomSquare(final Predicate<IsoGridSquare> predicate) {
        RoomDef.squareChoices.clear();
        for (int i = 0; i < this.rects.size(); ++i) {
            final RoomRect roomRect = this.rects.get(i);
            for (int j = roomRect.getX(); j < roomRect.getX2(); ++j) {
                for (int k = roomRect.getY(); k < roomRect.getY2(); ++k) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(j, k, this.getZ());
                    if ((gridSquare != null && predicate != null && predicate.test(gridSquare)) || predicate == null) {
                        RoomDef.squareChoices.add(gridSquare);
                    }
                }
            }
        }
        return PZArrayUtil.pickRandom(RoomDef.squareChoices);
    }
    
    public boolean isEmptyOutside() {
        return "emptyoutside".equalsIgnoreCase(this.name);
    }
    
    public HashMap<String, Integer> getProceduralSpawnedContainer() {
        return this.proceduralSpawnedContainer;
    }
    
    public boolean isRoofFixed() {
        return this.roofFixed;
    }
    
    public void setRoofFixed(final boolean roofFixed) {
        this.roofFixed = roofFixed;
    }
    
    public void Dispose() {
        this.building = null;
        this.rects.clear();
        this.objects.clear();
        this.proceduralSpawnedContainer.clear();
    }
    
    static {
        squareChoices = new ArrayList<IsoGridSquare>();
    }
    
    public static class RoomRect
    {
        public int x;
        public int y;
        public int w;
        public int h;
        
        public RoomRect(final int x, final int y, final int w, final int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
        
        public int getX() {
            return this.x;
        }
        
        public int getY() {
            return this.y;
        }
        
        public int getX2() {
            return this.x + this.w;
        }
        
        public int getY2() {
            return this.y + this.h;
        }
        
        public int getW() {
            return this.w;
        }
        
        public int getH() {
            return this.h;
        }
    }
}
