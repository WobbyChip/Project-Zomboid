// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.iso.IsoDirections;
import zombie.vehicles.BaseVehicle;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import zombie.core.Core;
import org.joml.Vector2f;
import zombie.iso.Vector2;
import zombie.core.math.PZMath;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import java.util.Iterator;
import zombie.SandboxOptions;
import zombie.core.Rand;
import zombie.iso.IsoWorld;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;
import java.util.HashMap;
import zombie.randomizedWorld.RandomizedWorldBase;

public class RandomizedVehicleStoryBase extends RandomizedWorldBase
{
    private int chance;
    private static int totalChance;
    private static HashMap<RandomizedVehicleStoryBase, Integer> rvsMap;
    protected boolean horizontalZone;
    protected int zoneWidth;
    public static final float baseChance = 12.5f;
    protected int minX;
    protected int minY;
    protected int maxX;
    protected int maxY;
    protected int minZoneWidth;
    protected int minZoneHeight;
    
    public RandomizedVehicleStoryBase() {
        this.chance = 0;
        this.horizontalZone = false;
        this.zoneWidth = 0;
        this.minX = 0;
        this.minY = 0;
        this.maxX = 0;
        this.maxY = 0;
        this.minZoneWidth = 0;
        this.minZoneHeight = 0;
    }
    
    public static void initAllRVSMapChance(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk) {
        RandomizedVehicleStoryBase.totalChance = 0;
        RandomizedVehicleStoryBase.rvsMap.clear();
        for (int i = 0; i < IsoWorld.instance.getRandomizedVehicleStoryList().size(); ++i) {
            final RandomizedVehicleStoryBase key = IsoWorld.instance.getRandomizedVehicleStoryList().get(i);
            if (key.isValid(zone, isoChunk, false) && key.isTimeValid(false)) {
                RandomizedVehicleStoryBase.totalChance += key.getChance();
                RandomizedVehicleStoryBase.rvsMap.put(key, key.getChance());
            }
        }
    }
    
    public static boolean doRandomStory(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final boolean b) {
        float n = Rand.Next(0.0f, 500.0f);
        switch (SandboxOptions.instance.VehicleStoryChance.getValue()) {
            case 1: {
                return false;
            }
            case 2: {
                n = Rand.Next(0.0f, 1000.0f);
                break;
            }
            case 4: {
                n = Rand.Next(0.0f, 300.0f);
                break;
            }
            case 5: {
                n = Rand.Next(0.0f, 175.0f);
                break;
            }
            case 6: {
                n = Rand.Next(0.0f, 50.0f);
                break;
            }
        }
        if (n >= 12.5f) {
            return false;
        }
        if (!isoChunk.vehicles.isEmpty()) {
            return false;
        }
        initAllRVSMapChance(zone, isoChunk);
        final RandomizedVehicleStoryBase randomStory = getRandomStory();
        if (randomStory == null) {
            return false;
        }
        isoChunk.setRandomVehicleStoryToSpawnLater(randomStory.initSpawnDataForChunk(zone, isoChunk));
        return true;
    }
    
    private static RandomizedVehicleStoryBase getRandomStory() {
        final int next = Rand.Next(RandomizedVehicleStoryBase.totalChance);
        final Iterator<RandomizedVehicleStoryBase> iterator = RandomizedVehicleStoryBase.rvsMap.keySet().iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final RandomizedVehicleStoryBase key = iterator.next();
            n += RandomizedVehicleStoryBase.rvsMap.get(key);
            if (next < n) {
                return key;
            }
        }
        return null;
    }
    
    public int getMinZoneWidth() {
        return (this.minZoneWidth <= 0) ? 10 : this.minZoneWidth;
    }
    
    public int getMinZoneHeight() {
        return (this.minZoneHeight <= 0) ? 5 : this.minZoneHeight;
    }
    
    public void randomizeVehicleStory(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk) {
    }
    
    public IsoGridSquare getCenterOfChunk(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk) {
        final int max = Math.max(zone.x, isoChunk.wx * 10);
        final int max2 = Math.max(zone.y, isoChunk.wy * 10);
        final int min = Math.min(zone.x + zone.w, (isoChunk.wx + 1) * 10);
        final int min2 = Math.min(zone.y + zone.h, (isoChunk.wy + 1) * 10);
        int n;
        int n2;
        if (this.horizontalZone) {
            n = (zone.y + (zone.y + zone.h)) / 2;
            n2 = (max + min) / 2;
        }
        else {
            n = (max2 + min2) / 2;
            n2 = (zone.x + (zone.x + zone.w)) / 2;
        }
        return IsoCell.getInstance().getGridSquare(n2, n, zone.z);
    }
    
    public boolean isValid(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final boolean b) {
        this.horizontalZone = false;
        this.zoneWidth = 0;
        this.debugLine = "";
        if (!b && zone.hourLastSeen != 0) {
            return false;
        }
        if (!b && zone.haveConstruction) {
            return false;
        }
        if (!"Nav".equals(zone.getType())) {
            this.debugLine = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.debugLine);
            return false;
        }
        this.minX = Math.max(zone.x, isoChunk.wx * 10);
        this.minY = Math.max(zone.y, isoChunk.wy * 10);
        this.maxX = Math.min(zone.x + zone.w, (isoChunk.wx + 1) * 10);
        this.maxY = Math.min(zone.y + zone.h, (isoChunk.wy + 1) * 10);
        return this.getSpawnPoint(zone, isoChunk, null);
    }
    
    public VehicleStorySpawnData initSpawnDataForChunk(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk) {
        final int minZoneWidth = this.getMinZoneWidth();
        final int minZoneHeight = this.getMinZoneHeight();
        final float[] array = new float[3];
        if (!this.getSpawnPoint(zone, isoChunk, array)) {
            return null;
        }
        final float n = array[0];
        final float n2 = array[1];
        final float n3 = array[2];
        final int[] array2 = new int[4];
        VehicleStorySpawner.getInstance().getAABB(n, n2, (float)minZoneWidth, (float)minZoneHeight, n3, array2);
        return new VehicleStorySpawnData(this, zone, n, n2, n3, array2[0], array2[1], array2[2], array2[3]);
    }
    
    public boolean getSpawnPoint(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final float[] array) {
        return this.getRectangleSpawnPoint(zone, isoChunk, array) || this.getPolylineSpawnPoint(zone, isoChunk, array);
    }
    
    public boolean getRectangleSpawnPoint(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final float[] array) {
        if (!zone.isRectangle()) {
            return false;
        }
        final int minZoneWidth = this.getMinZoneWidth();
        final int minZoneHeight = this.getMinZoneHeight();
        if (zone.w > 30 && zone.h < 15) {
            this.horizontalZone = true;
            this.zoneWidth = zone.h;
            if (zone.getWidth() < minZoneHeight) {
                this.debugLine = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, zone.getWidth(), zone.getHeight());
                return false;
            }
            if (zone.getHeight() < minZoneWidth) {
                this.debugLine = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, zone.getWidth(), zone.getHeight());
                return false;
            }
            if (array == null) {
                return true;
            }
            final float n = (float)zone.getX();
            final float n2 = (float)(zone.getX() + zone.getWidth());
            final float n3 = zone.getY() + zone.getHeight() / 2.0f;
            array[0] = PZMath.clamp(isoChunk.wx * 10 + 5.0f, n + minZoneHeight / 2.0f, n2 - minZoneHeight / 2.0f);
            array[1] = n3;
            array[2] = Vector2.getDirection(n2 - n, 0.0f);
            return true;
        }
        else {
            if (zone.h <= 30 || zone.w >= 15) {
                this.debugLine = "Zone too small or too large";
                return false;
            }
            this.horizontalZone = false;
            this.zoneWidth = zone.w;
            if (zone.getWidth() < minZoneWidth) {
                this.debugLine = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, zone.getWidth(), zone.getHeight());
                return false;
            }
            if (zone.getHeight() < minZoneHeight) {
                this.debugLine = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, zone.getWidth(), zone.getHeight());
                return false;
            }
            if (array == null) {
                return true;
            }
            final float n4 = (float)zone.getY();
            final float n5 = (float)(zone.getY() + zone.getHeight());
            array[0] = zone.getX() + zone.getWidth() / 2.0f;
            array[1] = PZMath.clamp(isoChunk.wy * 10 + 5.0f, n4 + minZoneHeight / 2.0f, n5 - minZoneHeight / 2.0f);
            array[2] = Vector2.getDirection(0.0f, n4 - n5);
            return true;
        }
    }
    
    public boolean getPolylineSpawnPoint(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final float[] array) {
        if (!zone.isPolyline() || zone.polylineWidth <= 0) {
            return false;
        }
        final int minZoneWidth = this.getMinZoneWidth();
        final int minZoneHeight = this.getMinZoneHeight();
        if (zone.polylineWidth < minZoneWidth) {
            this.debugLine = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, zone.polylineWidth);
            return false;
        }
        final double[] array2 = new double[2];
        final int clippedSegmentOfPolyline = zone.getClippedSegmentOfPolyline(isoChunk.wx * 10, isoChunk.wy * 10, (isoChunk.wx + 1) * 10, (isoChunk.wy + 1) * 10, array2);
        if (clippedSegmentOfPolyline == -1) {
            return false;
        }
        final double n = array2[0];
        final double n2 = array2[1];
        final float n3 = (zone.polylineWidth % 2 == 0) ? 0.0f : 0.5f;
        final float n4 = zone.points.get(clippedSegmentOfPolyline * 2) + n3;
        final float n5 = zone.points.get(clippedSegmentOfPolyline * 2 + 1) + n3;
        final float n6 = zone.points.get(clippedSegmentOfPolyline * 2 + 2) + n3;
        final float n7 = zone.points.get(clippedSegmentOfPolyline * 2 + 3) + n3;
        final float n8 = n6 - n4;
        final float n9 = n7 - n5;
        final float length = Vector2f.length(n8, n9);
        if (length < minZoneHeight) {
            return false;
        }
        this.zoneWidth = zone.polylineWidth;
        if (array == null) {
            return true;
        }
        final float n10 = minZoneHeight / 2.0f / length;
        final float max = PZMath.max((float)n - n10, n10);
        final float min = PZMath.min((float)n2 + n10, 1.0f - n10);
        final float n11 = n4 + n8 * max;
        final float n12 = n5 + n9 * max;
        final float n13 = n4 + n8 * min;
        final float n14 = n5 + n9 * min;
        float next = Rand.Next(0.0f, 1.0f);
        if (Core.bDebug) {
            next = System.currentTimeMillis() / 20L % 360L / 360.0f;
        }
        array[0] = n11 + (n13 - n11) * next;
        array[1] = n12 + (n14 - n12) * next;
        array[2] = Vector2.getDirection(n8, n9);
        return true;
    }
    
    public boolean isFullyStreamedIn(final int n, final int n2, final int n3, final int n4) {
        final int n5 = 10;
        final int n6 = n / n5;
        final int n7 = n2 / n5;
        final int n8 = (n3 - 1) / n5;
        for (int n9 = (n4 - 1) / n5, i = n7; i <= n9; ++i) {
            for (int j = n6; j <= n8; ++j) {
                if (!this.isChunkLoaded(j, i)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isChunkLoaded(final int n, final int n2) {
        final IsoChunk isoChunk = GameServer.bServer ? ServerMap.instance.getChunk(n, n2) : IsoWorld.instance.CurrentCell.getChunk(n, n2);
        return isoChunk != null && isoChunk.bLoaded;
    }
    
    public boolean initVehicleStorySpawner(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final boolean b) {
        return false;
    }
    
    public boolean callVehicleStorySpawner(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final float n) {
        final float[] array = new float[3];
        if (!this.getSpawnPoint(zone, isoChunk, array)) {
            return false;
        }
        this.initVehicleStorySpawner(zone, isoChunk, false);
        final VehicleStorySpawner instance = VehicleStorySpawner.getInstance();
        float n2 = array[2];
        if (Rand.NextBool(2)) {
            n2 += 3.1415927f;
        }
        instance.spawn(array[0], array[1], 0.0f, n2 + n + 1.5707964f, this::spawnElement);
        return true;
    }
    
    public void spawnElement(final VehicleStorySpawner vehicleStorySpawner, final VehicleStorySpawner.Element element) {
    }
    
    public BaseVehicle[] addSmashedOverlay(BaseVehicle setSmashed, BaseVehicle setSmashed2, final int n, final int n2, final boolean b, final boolean b2) {
        final IsoDirections dir = setSmashed.getDir();
        final IsoDirections dir2 = setSmashed2.getDir();
        String smashed;
        String smashed2;
        if (!b) {
            smashed = "Front";
            if (dir2 == IsoDirections.W) {
                if (dir == IsoDirections.S) {
                    smashed2 = "Right";
                }
                else {
                    smashed2 = "Left";
                }
            }
            else if (dir == IsoDirections.S) {
                smashed2 = "Left";
            }
            else {
                smashed2 = "Right";
            }
        }
        else {
            if (dir == IsoDirections.S) {
                if (n > 0) {
                    smashed = "Left";
                }
                else {
                    smashed = "Right";
                }
            }
            else if (n < 0) {
                smashed = "Left";
            }
            else {
                smashed = "Right";
            }
            smashed2 = "Front";
        }
        setSmashed = setSmashed.setSmashed(smashed);
        setSmashed2 = setSmashed2.setSmashed(smashed2);
        if (b2) {
            setSmashed.setBloodIntensity(smashed, 1.0f);
            setSmashed2.setBloodIntensity(smashed2, 1.0f);
        }
        return new BaseVehicle[] { setSmashed, setSmashed2 };
    }
    
    public int getChance() {
        return this.chance;
    }
    
    public void setChance(final int chance) {
        this.chance = chance;
    }
    
    public int getMinimumDays() {
        return this.minimumDays;
    }
    
    public void setMinimumDays(final int minimumDays) {
        this.minimumDays = minimumDays;
    }
    
    @Override
    public int getMaximumDays() {
        return this.maximumDays;
    }
    
    @Override
    public void setMaximumDays(final int maximumDays) {
        this.maximumDays = maximumDays;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getDebugLine() {
        return this.debugLine;
    }
    
    public void registerCustomOutfits() {
    }
    
    static {
        RandomizedVehicleStoryBase.totalChance = 0;
        RandomizedVehicleStoryBase.rvsMap = new HashMap<RandomizedVehicleStoryBase, Integer>();
    }
}
