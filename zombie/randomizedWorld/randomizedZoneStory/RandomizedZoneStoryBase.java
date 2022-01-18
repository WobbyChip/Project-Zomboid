// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedZoneStory;

import zombie.iso.objects.IsoDeadBody;
import zombie.iso.IsoObject;
import zombie.iso.IsoChunk;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import java.util.Iterator;
import zombie.iso.IsoGridSquare;
import zombie.core.Rand;
import zombie.SandboxOptions;
import zombie.iso.IsoWorld;
import zombie.iso.IsoMetaGrid;
import java.util.HashMap;
import java.util.ArrayList;
import zombie.randomizedWorld.RandomizedWorldBase;

public class RandomizedZoneStoryBase extends RandomizedWorldBase
{
    public boolean alwaysDo;
    public static final int baseChance = 15;
    public static int totalChance;
    public static final String zoneStory = "ZoneStory";
    public int chance;
    protected int minZoneWidth;
    protected int minZoneHeight;
    public final ArrayList<String> zoneType;
    private static final HashMap<RandomizedZoneStoryBase, Integer> rzsMap;
    
    public RandomizedZoneStoryBase() {
        this.alwaysDo = false;
        this.chance = 0;
        this.minZoneWidth = 0;
        this.minZoneHeight = 0;
        this.zoneType = new ArrayList<String>();
    }
    
    public static boolean isValidForStory(final IsoMetaGrid.Zone zone, final boolean b) {
        if (zone.pickedXForZoneStory > 0 && zone.pickedYForZoneStory > 0 && zone.pickedRZStory != null && checkCanSpawnStory(zone, b)) {
            zone.pickedRZStory.randomizeZoneStory(zone);
            zone.pickedRZStory = null;
            zone.pickedXForZoneStory = 0;
            zone.pickedYForZoneStory = 0;
        }
        if (!b && zone.hourLastSeen != 0) {
            return false;
        }
        if (!b && zone.haveConstruction) {
            return false;
        }
        if ("ZoneStory".equals(zone.type)) {
            doRandomStory(zone);
            return true;
        }
        return false;
    }
    
    public static void initAllRZSMapChance(final IsoMetaGrid.Zone zone) {
        RandomizedZoneStoryBase.totalChance = 0;
        RandomizedZoneStoryBase.rzsMap.clear();
        for (int i = 0; i < IsoWorld.instance.getRandomizedZoneList().size(); ++i) {
            final RandomizedZoneStoryBase key = IsoWorld.instance.getRandomizedZoneList().get(i);
            if (key.isValid(zone, false) && key.isTimeValid(false)) {
                RandomizedZoneStoryBase.totalChance += key.chance;
                RandomizedZoneStoryBase.rzsMap.put(key, key.chance);
            }
        }
    }
    
    public boolean isValid(final IsoMetaGrid.Zone zone, final boolean b) {
        boolean b2 = false;
        for (int i = 0; i < this.zoneType.size(); ++i) {
            if (this.zoneType.get(i).equals(zone.name)) {
                b2 = true;
                break;
            }
        }
        return b2 && zone.w >= this.minZoneWidth && zone.h >= this.minZoneHeight;
    }
    
    private static boolean doRandomStory(final IsoMetaGrid.Zone zone) {
        ++zone.hourLastSeen;
        int n = 6;
        switch (SandboxOptions.instance.ZoneStoryChance.getValue()) {
            case 1: {
                return false;
            }
            case 2: {
                n = 2;
                break;
            }
            case 4: {
                n = 12;
                break;
            }
            case 5: {
                n = 20;
                break;
            }
            case 6: {
                n = 40;
                break;
            }
        }
        RandomizedZoneStoryBase pickedRZStory = null;
        for (int i = 0; i < IsoWorld.instance.getRandomizedZoneList().size(); ++i) {
            final RandomizedZoneStoryBase randomizedZoneStoryBase = IsoWorld.instance.getRandomizedZoneList().get(i);
            if (randomizedZoneStoryBase.alwaysDo && randomizedZoneStoryBase.isValid(zone, false) && randomizedZoneStoryBase.isTimeValid(false)) {
                pickedRZStory = randomizedZoneStoryBase;
            }
        }
        if (pickedRZStory != null) {
            final int x = zone.x;
            final int y = zone.y;
            final int n2 = zone.x + zone.w - pickedRZStory.minZoneWidth / 2;
            final int n3 = zone.y + zone.h - pickedRZStory.minZoneHeight / 2;
            zone.pickedXForZoneStory = Rand.Next(x, n2 + 1);
            zone.pickedYForZoneStory = Rand.Next(y, n3 + 1);
            zone.pickedRZStory = pickedRZStory;
            return true;
        }
        if (Rand.Next(100) >= n) {
            return false;
        }
        initAllRZSMapChance(zone);
        final RandomizedZoneStoryBase randomStory = getRandomStory();
        if (randomStory == null) {
            return false;
        }
        final int x2 = zone.x;
        final int y2 = zone.y;
        final int n4 = zone.x + zone.w - randomStory.minZoneWidth / 2;
        final int n5 = zone.y + zone.h - randomStory.minZoneHeight / 2;
        zone.pickedXForZoneStory = Rand.Next(x2, n4 + 1);
        zone.pickedYForZoneStory = Rand.Next(y2, n5 + 1);
        zone.pickedRZStory = randomStory;
        return true;
    }
    
    public IsoGridSquare getRandomFreeSquare(final RandomizedZoneStoryBase randomizedZoneStoryBase, final IsoMetaGrid.Zone zone) {
        for (int i = 0; i < 1000; ++i) {
            final IsoGridSquare sq = this.getSq(Rand.Next(zone.pickedXForZoneStory - randomizedZoneStoryBase.minZoneWidth / 2, zone.pickedXForZoneStory + randomizedZoneStoryBase.minZoneWidth / 2), Rand.Next(zone.pickedYForZoneStory - randomizedZoneStoryBase.minZoneHeight / 2, zone.pickedYForZoneStory + randomizedZoneStoryBase.minZoneHeight / 2), zone.z);
            if (sq != null && sq.isFree(false)) {
                return sq;
            }
        }
        return null;
    }
    
    public IsoGridSquare getRandomFreeSquareFullZone(final RandomizedZoneStoryBase randomizedZoneStoryBase, final IsoMetaGrid.Zone zone) {
        for (int i = 0; i < 1000; ++i) {
            final IsoGridSquare sq = this.getSq(Rand.Next(zone.x, zone.x + zone.w), Rand.Next(zone.y, zone.y + zone.h), zone.z);
            if (sq != null && sq.isFree(false)) {
                return sq;
            }
        }
        return null;
    }
    
    private static RandomizedZoneStoryBase getRandomStory() {
        final int next = Rand.Next(RandomizedZoneStoryBase.totalChance);
        final Iterator<RandomizedZoneStoryBase> iterator = RandomizedZoneStoryBase.rzsMap.keySet().iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final RandomizedZoneStoryBase key = iterator.next();
            n += RandomizedZoneStoryBase.rzsMap.get(key);
            if (next < n) {
                return key;
            }
        }
        return null;
    }
    
    private static boolean checkCanSpawnStory(final IsoMetaGrid.Zone zone, final boolean b) {
        final int n = zone.pickedXForZoneStory - zone.pickedRZStory.minZoneWidth / 2 - 2;
        final int n2 = zone.pickedYForZoneStory - zone.pickedRZStory.minZoneHeight / 2 - 2;
        final int n3 = zone.pickedXForZoneStory + zone.pickedRZStory.minZoneWidth / 2 + 2;
        final int n4 = zone.pickedYForZoneStory + zone.pickedRZStory.minZoneHeight / 2 + 2;
        final int n5 = n / 10;
        final int n6 = n2 / 10;
        final int n7 = n3 / 10;
        for (int n8 = n4 / 10, i = n6; i <= n8; ++i) {
            for (int j = n5; j <= n7; ++j) {
                final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(j, i) : IsoWorld.instance.CurrentCell.getChunk(j, i);
                if (isoChunk == null || !isoChunk.bLoaded) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void randomizeZoneStory(final IsoMetaGrid.Zone zone) {
    }
    
    public boolean isValid() {
        return true;
    }
    
    public void cleanAreaForStory(final RandomizedZoneStoryBase randomizedZoneStoryBase, final IsoMetaGrid.Zone zone) {
        final int n = zone.pickedXForZoneStory - randomizedZoneStoryBase.minZoneWidth / 2 - 1;
        final int n2 = zone.pickedYForZoneStory - randomizedZoneStoryBase.minZoneHeight / 2 - 1;
        final int n3 = zone.pickedXForZoneStory + randomizedZoneStoryBase.minZoneWidth / 2 + 1;
        final int n4 = zone.pickedYForZoneStory + randomizedZoneStoryBase.minZoneHeight / 2 + 1;
        for (int i = n; i < n3; ++i) {
            for (int j = n2; j < n4; ++j) {
                final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare(i, j, zone.z);
                if (gridSquare != null) {
                    gridSquare.removeBlood(false, false);
                    for (int k = 0; k < gridSquare.getObjects().size(); ++k) {
                        final IsoObject isoObject = gridSquare.getObjects().get(k);
                        if (gridSquare.getFloor() != isoObject) {
                            gridSquare.RemoveTileObject(isoObject);
                        }
                    }
                    for (int l = 0; l < gridSquare.getSpecialObjects().size(); ++l) {
                        gridSquare.RemoveTileObject(gridSquare.getSpecialObjects().get(l));
                    }
                    for (int n5 = 0; n5 < gridSquare.getDeadBodys().size(); ++n5) {
                        gridSquare.removeCorpse(gridSquare.getDeadBodys().get(n5), false);
                    }
                    gridSquare.RecalcProperties();
                    gridSquare.RecalcAllWithNeighbours(true);
                }
            }
        }
    }
    
    public int getMinimumWidth() {
        return this.minZoneWidth;
    }
    
    public int getMinimumHeight() {
        return this.minZoneHeight;
    }
    
    static {
        RandomizedZoneStoryBase.totalChance = 0;
        rzsMap = new HashMap<RandomizedZoneStoryBase, Integer>();
    }
    
    public enum ZoneType
    {
        Forest, 
        Beach, 
        Lake, 
        Baseball, 
        MusicFestStage, 
        MusicFest;
        
        private static /* synthetic */ ZoneType[] $values() {
            return new ZoneType[] { ZoneType.Forest, ZoneType.Beach, ZoneType.Lake, ZoneType.Baseball, ZoneType.MusicFestStage, ZoneType.MusicFest };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
