// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.io.RandomAccessFile;
import zombie.util.BufferedRandomAccessFile;
import zombie.Lua.LuaManager;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.vehicles.PolygonalMap2;
import zombie.randomizedWorld.randomizedZoneStory.RandomizedZoneStoryBase;
import zombie.MapGroups;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import zombie.GameWindow;
import zombie.randomizedWorld.randomizedBuilding.RBBasic;
import zombie.network.GameClient;
import zombie.core.stash.StashSystem;
import zombie.network.GameServer;
import zombie.characters.Faction;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import java.util.Map;
import zombie.iso.objects.IsoMannequin;
import zombie.gameStates.ChooseGameInfo;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.util.Type;
import se.krka.kahlua.vm.KahluaTable;
import gnu.trove.TIntCollection;
import gnu.trove.list.array.TIntArrayList;
import zombie.characters.IsoPlayer;
import zombie.util.SharedStrings;
import zombie.characters.IsoGameCharacter;
import java.awt.Rectangle;
import java.nio.ByteBuffer;
import zombie.vehicles.ClipperOffset;
import zombie.vehicles.Clipper;
import java.util.ArrayList;

public final class IsoMetaGrid
{
    private static final int NUM_LOADER_THREADS = 8;
    private static ArrayList<String> s_PreferredZoneTypes;
    private static Clipper s_clipper;
    private static ClipperOffset s_clipperOffset;
    private static ByteBuffer s_clipperBuffer;
    static Rectangle a;
    static Rectangle b;
    static ArrayList<RoomDef> roomChoices;
    private final ArrayList<RoomDef> tempRooms;
    private final ArrayList<Zone> tempZones1;
    private final ArrayList<Zone> tempZones2;
    private final MetaGridLoaderThread[] threads;
    public int minX;
    public int minY;
    public int maxX;
    public int maxY;
    public final ArrayList<Zone> Zones;
    public final ArrayList<BuildingDef> Buildings;
    public final ArrayList<VehicleZone> VehiclesZones;
    public IsoMetaCell[][] Grid;
    public final ArrayList<IsoGameCharacter> MetaCharacters;
    final ArrayList<Vector2> HighZombieList;
    private int width;
    private int height;
    private final SharedStrings sharedStrings;
    private long createStartTime;
    
    public IsoMetaGrid() {
        this.tempRooms = new ArrayList<RoomDef>();
        this.tempZones1 = new ArrayList<Zone>();
        this.tempZones2 = new ArrayList<Zone>();
        this.threads = new MetaGridLoaderThread[8];
        this.minX = 10000000;
        this.minY = 10000000;
        this.maxX = -10000000;
        this.maxY = -10000000;
        this.Zones = new ArrayList<Zone>();
        this.Buildings = new ArrayList<BuildingDef>();
        this.VehiclesZones = new ArrayList<VehicleZone>();
        this.MetaCharacters = new ArrayList<IsoGameCharacter>();
        this.HighZombieList = new ArrayList<Vector2>();
        this.sharedStrings = new SharedStrings();
    }
    
    public void AddToMeta(final IsoGameCharacter isoGameCharacter) {
        IsoWorld.instance.CurrentCell.Remove(isoGameCharacter);
        if (!this.MetaCharacters.contains(isoGameCharacter)) {
            this.MetaCharacters.add(isoGameCharacter);
        }
    }
    
    public void RemoveFromMeta(final IsoPlayer e) {
        this.MetaCharacters.remove(e);
        if (!IsoWorld.instance.CurrentCell.getObjectList().contains(e)) {
            IsoWorld.instance.CurrentCell.getObjectList().add(e);
        }
    }
    
    public int getMinX() {
        return this.minX;
    }
    
    public int getMinY() {
        return this.minY;
    }
    
    public int getMaxX() {
        return this.maxX;
    }
    
    public int getMaxY() {
        return this.maxY;
    }
    
    public Zone getZoneAt(final int n, final int n2, final int n3) {
        final IsoMetaChunk chunkDataFromTile = this.getChunkDataFromTile(n, n2);
        if (chunkDataFromTile != null) {
            return chunkDataFromTile.getZoneAt(n, n2, n3);
        }
        return null;
    }
    
    public ArrayList<Zone> getZonesAt(final int n, final int n2, final int n3) {
        return this.getZonesAt(n, n2, n3, new ArrayList<Zone>());
    }
    
    public ArrayList<Zone> getZonesAt(final int n, final int n2, final int n3, final ArrayList<Zone> list) {
        final IsoMetaChunk chunkDataFromTile = this.getChunkDataFromTile(n, n2);
        if (chunkDataFromTile != null) {
            return chunkDataFromTile.getZonesAt(n, n2, n3, list);
        }
        return list;
    }
    
    public ArrayList<Zone> getZonesIntersecting(final int n, final int n2, final int n3, final int n4, final int n5) {
        return this.getZonesIntersecting(n, n2, n3, n4, n5, new ArrayList<Zone>());
    }
    
    public ArrayList<Zone> getZonesIntersecting(final int n, final int n2, final int n3, final int n4, final int n5, final ArrayList<Zone> list) {
        for (int i = n2 / 300; i <= (n2 + n5) / 300; ++i) {
            for (int j = n / 300; j <= (n + n4) / 300; ++j) {
                if (j >= this.minX && j <= this.maxX && i >= this.minY && i <= this.maxY && this.Grid[j - this.minX][i - this.minY] != null) {
                    this.Grid[j - this.minX][i - this.minY].getZonesIntersecting(n, n2, n3, n4, n5, list);
                }
            }
        }
        return list;
    }
    
    public VehicleZone getVehicleZoneAt(final int n, final int n2, final int n3) {
        final IsoMetaCell metaGridFromTile = this.getMetaGridFromTile(n, n2);
        if (metaGridFromTile == null || metaGridFromTile.vehicleZones.isEmpty()) {
            return null;
        }
        for (int i = 0; i < metaGridFromTile.vehicleZones.size(); ++i) {
            final VehicleZone vehicleZone = metaGridFromTile.vehicleZones.get(i);
            if (vehicleZone.z == n3 && n >= vehicleZone.x && n < vehicleZone.x + vehicleZone.w && n2 >= vehicleZone.y && n2 < vehicleZone.y + vehicleZone.h) {
                return vehicleZone;
            }
        }
        return null;
    }
    
    public BuildingDef getBuildingAt(final int n, final int n2) {
        for (int i = 0; i < this.Buildings.size(); ++i) {
            final BuildingDef buildingDef = this.Buildings.get(i);
            if (buildingDef.x <= n && buildingDef.y <= n2 && buildingDef.getW() > n - buildingDef.x && buildingDef.getH() > n2 - buildingDef.y) {
                return buildingDef;
            }
        }
        return null;
    }
    
    public BuildingDef getBuildingAtRelax(final int n, final int n2) {
        for (int i = 0; i < this.Buildings.size(); ++i) {
            final BuildingDef buildingDef = this.Buildings.get(i);
            if (buildingDef.x <= n + 1 && buildingDef.y <= n2 + 1 && buildingDef.getW() > n - buildingDef.x - 1 && buildingDef.getH() > n2 - buildingDef.y - 1) {
                return buildingDef;
            }
        }
        return null;
    }
    
    public RoomDef getRoomAt(final int n, final int n2, final int n3) {
        final IsoMetaChunk chunkDataFromTile = this.getChunkDataFromTile(n, n2);
        if (chunkDataFromTile != null) {
            return chunkDataFromTile.getRoomAt(n, n2, n3);
        }
        return null;
    }
    
    public RoomDef getEmptyOutsideAt(final int n, final int n2, final int n3) {
        final IsoMetaChunk chunkDataFromTile = this.getChunkDataFromTile(n, n2);
        if (chunkDataFromTile != null) {
            return chunkDataFromTile.getEmptyOutsideAt(n, n2, n3);
        }
        return null;
    }
    
    public void getRoomsIntersecting(final int n, final int n2, final int n3, final int n4, final ArrayList<RoomDef> list) {
        for (int i = n2 / 300; i <= (n2 + this.height) / 300; ++i) {
            for (int j = n / 300; j <= (n + this.width) / 300; ++j) {
                if (j >= this.minX && j <= this.maxX && i >= this.minY && i <= this.maxY) {
                    final IsoMetaCell isoMetaCell = this.Grid[j - this.minX][i - this.minY];
                    if (isoMetaCell != null) {
                        isoMetaCell.getRoomsIntersecting(n, n2, n3, n4, list);
                    }
                }
            }
        }
    }
    
    public int countRoomsIntersecting(final int n, final int n2, final int n3, final int n4) {
        this.tempRooms.clear();
        for (int i = n2 / 300; i <= (n2 + this.height) / 300; ++i) {
            for (int j = n / 300; j <= (n + this.width) / 300; ++j) {
                if (j >= this.minX && j <= this.maxX && i >= this.minY && i <= this.maxY) {
                    final IsoMetaCell isoMetaCell = this.Grid[j - this.minX][i - this.minY];
                    if (isoMetaCell != null) {
                        isoMetaCell.getRoomsIntersecting(n, n2, n3, n4, this.tempRooms);
                    }
                }
            }
        }
        return this.tempRooms.size();
    }
    
    public int countNearbyBuildingsRooms(final IsoPlayer isoPlayer) {
        return this.countRoomsIntersecting((int)isoPlayer.getX() - 20, (int)isoPlayer.getY() - 20, 40, 40);
    }
    
    private boolean isInside(final Zone zone, final BuildingDef buildingDef) {
        IsoMetaGrid.a.x = zone.x;
        IsoMetaGrid.a.y = zone.y;
        IsoMetaGrid.a.width = zone.w;
        IsoMetaGrid.a.height = zone.h;
        IsoMetaGrid.b.x = buildingDef.x;
        IsoMetaGrid.b.y = buildingDef.y;
        IsoMetaGrid.b.width = buildingDef.getW();
        IsoMetaGrid.b.height = buildingDef.getH();
        return IsoMetaGrid.a.contains(IsoMetaGrid.b);
    }
    
    private boolean isAdjacent(final Zone zone, final Zone zone2) {
        if (zone == zone2) {
            return false;
        }
        IsoMetaGrid.a.x = zone.x;
        IsoMetaGrid.a.y = zone.y;
        IsoMetaGrid.a.width = zone.w;
        IsoMetaGrid.a.height = zone.h;
        IsoMetaGrid.b.x = zone2.x;
        IsoMetaGrid.b.y = zone2.y;
        IsoMetaGrid.b.width = zone2.w;
        IsoMetaGrid.b.height = zone2.h;
        final Rectangle a = IsoMetaGrid.a;
        --a.x;
        final Rectangle a2 = IsoMetaGrid.a;
        --a2.y;
        final Rectangle a3 = IsoMetaGrid.a;
        a3.width += 2;
        final Rectangle a4 = IsoMetaGrid.a;
        a4.height += 2;
        final Rectangle b = IsoMetaGrid.b;
        --b.x;
        final Rectangle b2 = IsoMetaGrid.b;
        --b2.y;
        final Rectangle b3 = IsoMetaGrid.b;
        b3.width += 2;
        final Rectangle b4 = IsoMetaGrid.b;
        b4.height += 2;
        return IsoMetaGrid.a.intersects(IsoMetaGrid.b);
    }
    
    public Zone registerZone(final String s, final String s2, final int n, final int n2, final int n3, final int n4, final int n5) {
        return this.registerZone(s, s2, n, n2, n3, n4, n5, ZoneGeometryType.INVALID, null, 0);
    }
    
    public Zone registerZone(String value, String value2, final int n, final int n2, final int n3, final int n4, final int n5, final ZoneGeometryType geometryType, final TIntArrayList list, final int polylineWidth) {
        value = this.sharedStrings.get(value);
        value2 = this.sharedStrings.get(value2);
        final Zone zone = new Zone(value, value2, n, n2, n3, n4, n5);
        zone.geometryType = geometryType;
        if (list != null) {
            zone.points.addAll((TIntCollection)list);
            zone.polylineWidth = polylineWidth;
        }
        zone.isPreferredZoneForSquare = isPreferredZoneForSquare(value2);
        if (n < this.minX * 300 - 100 || n2 < this.minY * 300 - 100 || n + n4 > (this.maxX + 1) * 300 + 100 || n2 + n5 > (this.maxY + 1) * 300 + 100 || n3 < 0 || n3 >= 8 || n4 > 600 || n5 > 600) {
            return zone;
        }
        this.addZone(zone);
        return zone;
    }
    
    public Zone registerGeometryZone(final String s, final String s2, final int n, final String s3, final KahluaTable kahluaTable, final KahluaTable kahluaTable2) {
        int min = Integer.MAX_VALUE;
        int min2 = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int max2 = Integer.MIN_VALUE;
        final TIntArrayList list = new TIntArrayList(kahluaTable.len());
        for (int i = 0; i < kahluaTable.len(); i += 2) {
            final Object rawget = kahluaTable.rawget(i + 1);
            final Object rawget2 = kahluaTable.rawget(i + 2);
            final int intValue = ((Double)rawget).intValue();
            final int intValue2 = ((Double)rawget2).intValue();
            list.add(intValue);
            list.add(intValue2);
            min = Math.min(min, intValue);
            min2 = Math.min(min2, intValue2);
            max = Math.max(max, intValue);
            max2 = Math.max(max2, intValue2);
        }
        ZoneGeometryType zoneGeometryType = null;
        switch (s3) {
            case "point": {
                zoneGeometryType = ZoneGeometryType.Point;
                break;
            }
            case "polygon": {
                zoneGeometryType = ZoneGeometryType.Polygon;
                break;
            }
            case "polyline": {
                zoneGeometryType = ZoneGeometryType.Polyline;
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown zone geometry type");
            }
        }
        final ZoneGeometryType geometryType = zoneGeometryType;
        final Double n3 = (geometryType != ZoneGeometryType.Polyline || kahluaTable2 == null) ? null : Type.tryCastTo(kahluaTable2.rawget((Object)"LineWidth"), Double.class);
        if (n3 != null) {
            final int[] array = new int[4];
            this.calculatePolylineOutlineBounds(list, n3.intValue(), array);
            min = array[0];
            min2 = array[1];
            max = array[2];
            max2 = array[3];
        }
        if (s2.equals("Vehicle") || s2.equals("ParkingStall")) {
            final Zone registerVehiclesZone = this.registerVehiclesZone(s, s2, min, min2, n, max - min + 1, max2 - min2 + 1, kahluaTable2);
            if (registerVehiclesZone != null) {
                registerVehiclesZone.geometryType = geometryType;
                registerVehiclesZone.points.addAll((TIntCollection)list);
            }
            return registerVehiclesZone;
        }
        final Zone registerZone = this.registerZone(s, s2, min, min2, n, max - min + 1, max2 - min2 + 1, geometryType, list, (n3 == null) ? 0 : n3.intValue());
        list.clear();
        return registerZone;
    }
    
    private void calculatePolylineOutlineBounds(final TIntArrayList list, final int n, final int[] array) {
        if (IsoMetaGrid.s_clipperOffset == null) {
            IsoMetaGrid.s_clipperOffset = new ClipperOffset();
            IsoMetaGrid.s_clipperBuffer = ByteBuffer.allocateDirect(2048);
        }
        IsoMetaGrid.s_clipperOffset.clear();
        IsoMetaGrid.s_clipperBuffer.clear();
        final float n2 = (n % 2 == 0) ? 0.0f : 0.5f;
        for (int i = 0; i < list.size(); i += 2) {
            final int value = list.get(i);
            final int value2 = list.get(i + 1);
            IsoMetaGrid.s_clipperBuffer.putFloat(value + n2);
            IsoMetaGrid.s_clipperBuffer.putFloat(value2 + n2);
        }
        IsoMetaGrid.s_clipperBuffer.flip();
        IsoMetaGrid.s_clipperOffset.addPath(list.size() / 2, IsoMetaGrid.s_clipperBuffer, ClipperOffset.JoinType.jtMiter.ordinal(), ClipperOffset.EndType.etOpenButt.ordinal());
        IsoMetaGrid.s_clipperOffset.execute(n / 2.0f);
        if (IsoMetaGrid.s_clipperOffset.getPolygonCount() < 1) {
            DebugLog.General.warn((Object)"Failed to generate polyline outline");
            return;
        }
        IsoMetaGrid.s_clipperBuffer.clear();
        IsoMetaGrid.s_clipperOffset.getPolygon(0, IsoMetaGrid.s_clipperBuffer);
        final short short1 = IsoMetaGrid.s_clipperBuffer.getShort();
        float min = Float.MAX_VALUE;
        float min2 = Float.MAX_VALUE;
        float max = -3.4028235E38f;
        float max2 = -3.4028235E38f;
        for (short n3 = 0; n3 < short1; ++n3) {
            final float float1 = IsoMetaGrid.s_clipperBuffer.getFloat();
            final float float2 = IsoMetaGrid.s_clipperBuffer.getFloat();
            min = PZMath.min(min, float1);
            min2 = PZMath.min(min2, float2);
            max = PZMath.max(max, float1);
            max2 = PZMath.max(max2, float2);
        }
        array[0] = (int)PZMath.floor(min);
        array[1] = (int)PZMath.floor(min2);
        array[2] = (int)PZMath.ceil(max);
        array[3] = (int)PZMath.ceil(max2);
    }
    
    @Deprecated
    public Zone registerZoneNoOverlap(final String s, final String s2, final int n, final int n2, final int n3, final int n4, final int n5) {
        if (n < this.minX * 300 - 100 || n2 < this.minY * 300 - 100 || n + n4 > (this.maxX + 1) * 300 + 100 || n2 + n5 > (this.maxY + 1) * 300 + 100 || n3 < 0 || n3 >= 8 || n4 > 600 || n5 > 600) {
            return null;
        }
        return this.registerZone(s, s2, n, n2, n3, n4, n5);
    }
    
    private void addZone(final Zone e) {
        this.Zones.add(e);
        for (int i = e.y / 300; i <= (e.y + e.h) / 300; ++i) {
            for (int j = e.x / 300; j <= (e.x + e.w) / 300; ++j) {
                if (j >= this.minX && j <= this.maxX && i >= this.minY && i <= this.maxY && this.Grid[j - this.minX][i - this.minY] != null) {
                    this.Grid[j - this.minX][i - this.minY].addZone(e, j * 300, i * 300);
                }
            }
        }
    }
    
    public void removeZone(final Zone o) {
        this.Zones.remove(o);
        for (int i = o.y / 300; i <= (o.y + o.h) / 300; ++i) {
            for (int j = o.x / 300; j <= (o.x + o.w) / 300; ++j) {
                if (j >= this.minX && j <= this.maxX && i >= this.minY && i <= this.maxY && this.Grid[j - this.minX][i - this.minY] != null) {
                    this.Grid[j - this.minX][i - this.minY].removeZone(o);
                }
            }
        }
    }
    
    public void removeZonesForCell(final int n, final int n2) {
        final IsoMetaCell cellData = this.getCellData(n, n2);
        if (cellData == null) {
            return;
        }
        final ArrayList<Zone> tempZones1 = this.tempZones1;
        tempZones1.clear();
        for (int i = 0; i < 900; ++i) {
            cellData.ChunkMap[i].getZonesIntersecting(n * 300, n2 * 300, 0, 300, 300, tempZones1);
        }
        for (int j = 0; j < tempZones1.size(); ++j) {
            final Zone zone = tempZones1.get(j);
            final ArrayList<Zone> tempZones2 = this.tempZones2;
            if (zone.difference(n * 300, n2 * 300, 0, 300, 300, tempZones2)) {
                this.removeZone(zone);
                for (int k = 0; k < tempZones2.size(); ++k) {
                    this.addZone(tempZones2.get(k));
                }
            }
        }
        if (!cellData.vehicleZones.isEmpty()) {
            cellData.vehicleZones.clear();
        }
        if (!cellData.mannequinZones.isEmpty()) {
            cellData.mannequinZones.clear();
        }
    }
    
    public void removeZonesForLotDirectory(final String s) {
        if (this.Zones.isEmpty()) {
            return;
        }
        final File file = new File(ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s)));
        if (!file.isDirectory()) {
            return;
        }
        if (ChooseGameInfo.getMapDetails(s) == null) {
            return;
        }
        final String[] list = file.list();
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.length; ++i) {
            final String s2 = list[i];
            if (s2.endsWith(".lotheader")) {
                final String[] split = s2.split("_");
                split[1] = split[1].replace(".lotheader", "");
                this.removeZonesForCell(Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim()));
            }
        }
    }
    
    public void processZones() {
        int max = 0;
        for (int i = this.minX; i <= this.maxX; ++i) {
            for (int j = this.minY; j <= this.maxY; ++j) {
                if (this.Grid[i - this.minX][j - this.minY] != null) {
                    for (int k = 0; k < 30; ++k) {
                        for (int l = 0; l < 30; ++l) {
                            max = Math.max(max, this.Grid[i - this.minX][j - this.minY].getChunk(l, k).numZones());
                        }
                    }
                }
            }
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, max));
    }
    
    public Zone registerVehiclesZone(String value, String value2, final int n, final int n2, final int n3, final int n4, final int n5, final KahluaTable kahluaTable) {
        if (value2.equals("Vehicle") || value2.equals("ParkingStall")) {
            value = this.sharedStrings.get(value);
            value2 = this.sharedStrings.get(value2);
            final VehicleZone vehicleZone = new VehicleZone(value, value2, n, n2, n3, n4, n5, kahluaTable);
            this.VehiclesZones.add(vehicleZone);
            final int n6 = (int)Math.ceil((vehicleZone.x + vehicleZone.w) / 300.0f);
            for (int n7 = (int)Math.ceil((vehicleZone.y + vehicleZone.h) / 300.0f), i = vehicleZone.y / 300; i < n7; ++i) {
                for (int j = vehicleZone.x / 300; j < n6; ++j) {
                    if (j >= this.minX && j <= this.maxX && i >= this.minY && i <= this.maxY && this.Grid[j - this.minX][i - this.minY] != null) {
                        this.Grid[j - this.minX][i - this.minY].vehicleZones.add(vehicleZone);
                    }
                }
            }
            return vehicleZone;
        }
        return null;
    }
    
    public void checkVehiclesZones() {
        int i = 0;
        while (i < this.VehiclesZones.size()) {
            boolean b = true;
            for (int j = 0; j < i; ++j) {
                final VehicleZone vehicleZone = this.VehiclesZones.get(i);
                final VehicleZone vehicleZone2 = this.VehiclesZones.get(j);
                if (vehicleZone.getX() == vehicleZone2.getX() && vehicleZone.getY() == vehicleZone2.getY() && vehicleZone.h == vehicleZone2.h && vehicleZone.w == vehicleZone2.w) {
                    b = false;
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;IILjava/lang/String;Ljava/lang/String;II)Ljava/lang/String;, vehicleZone.name, vehicleZone.type, vehicleZone.x, vehicleZone.y, vehicleZone2.name, vehicleZone2.type, vehicleZone2.x, vehicleZone2.y));
                    break;
                }
            }
            if (b) {
                ++i;
            }
            else {
                this.VehiclesZones.remove(i);
            }
        }
    }
    
    public Zone registerMannequinZone(String value, String value2, final int n, final int n2, final int n3, final int n4, final int n5, final KahluaTable kahluaTable) {
        if ("Mannequin".equals(value2)) {
            value = this.sharedStrings.get(value);
            value2 = this.sharedStrings.get(value2);
            final IsoMannequin.MannequinZone e = new IsoMannequin.MannequinZone(value, value2, n, n2, n3, n4, n5, kahluaTable);
            final int n6 = (int)Math.ceil((e.x + e.w) / 300.0f);
            for (int n7 = (int)Math.ceil((e.y + e.h) / 300.0f), i = e.y / 300; i < n7; ++i) {
                for (int j = e.x / 300; j < n6; ++j) {
                    if (j >= this.minX && j <= this.maxX && i >= this.minY && i <= this.maxY && this.Grid[j - this.minX][i - this.minY] != null) {
                        this.Grid[j - this.minX][i - this.minY].mannequinZones.add(e);
                    }
                }
            }
            return e;
        }
        return null;
    }
    
    public void save(final ByteBuffer byteBuffer) {
        this.savePart(byteBuffer, 0, false);
        this.savePart(byteBuffer, 1, false);
    }
    
    public void savePart(final ByteBuffer byteBuffer, final int n, final boolean b) {
        if (n == 0) {
            byteBuffer.put((byte)77);
            byteBuffer.put((byte)69);
            byteBuffer.put((byte)84);
            byteBuffer.put((byte)65);
            byteBuffer.putInt(186);
            byteBuffer.putInt(this.Grid.length);
            byteBuffer.putInt(this.Grid[0].length);
            for (int i = 0; i < this.Grid.length; ++i) {
                for (int j = 0; j < this.Grid[0].length; ++j) {
                    final IsoMetaCell isoMetaCell = this.Grid[i][j];
                    int size = 0;
                    if (isoMetaCell.info != null) {
                        size = isoMetaCell.info.Rooms.values().size();
                    }
                    byteBuffer.putInt(size);
                    if (isoMetaCell.info != null) {
                        for (final Map.Entry<Integer, RoomDef> entry : isoMetaCell.info.Rooms.entrySet()) {
                            final RoomDef roomDef = entry.getValue();
                            byteBuffer.putInt(entry.getKey());
                            short n2 = 0;
                            if (roomDef.bExplored) {
                                n2 |= 0x1;
                            }
                            if (roomDef.bLightsActive) {
                                n2 |= 0x2;
                            }
                            if (roomDef.bDoneSpawn) {
                                n2 |= 0x4;
                            }
                            if (roomDef.isRoofFixed()) {
                                n2 |= 0x8;
                            }
                            byteBuffer.putShort(n2);
                        }
                    }
                    if (isoMetaCell.info != null) {
                        byteBuffer.putInt(isoMetaCell.info.Buildings.size());
                    }
                    else {
                        byteBuffer.putInt(0);
                    }
                    if (isoMetaCell.info != null) {
                        for (final BuildingDef buildingDef : isoMetaCell.info.Buildings) {
                            byteBuffer.put((byte)(buildingDef.bAlarmed ? 1 : 0));
                            byteBuffer.putInt(buildingDef.getKeyId());
                            byteBuffer.put((byte)(buildingDef.seen ? 1 : 0));
                            byteBuffer.put((byte)(buildingDef.isHasBeenVisited() ? 1 : 0));
                            byteBuffer.putInt(buildingDef.lootRespawnHour);
                        }
                    }
                }
            }
            return;
        }
        byteBuffer.putInt(SafeHouse.getSafehouseList().size());
        for (int k = 0; k < SafeHouse.getSafehouseList().size(); ++k) {
            SafeHouse.getSafehouseList().get(k).save(byteBuffer);
        }
        byteBuffer.putInt(NonPvpZone.getAllZones().size());
        for (int l = 0; l < NonPvpZone.getAllZones().size(); ++l) {
            NonPvpZone.getAllZones().get(l).save(byteBuffer);
        }
        byteBuffer.putInt(Faction.getFactions().size());
        for (int index = 0; index < Faction.getFactions().size(); ++index) {
            Faction.getFactions().get(index).save(byteBuffer);
        }
        if (GameServer.bServer) {
            final int position = byteBuffer.position();
            byteBuffer.putInt(0);
            StashSystem.save(byteBuffer);
            byteBuffer.putInt(position, byteBuffer.position());
        }
        else if (!GameClient.bClient) {
            StashSystem.save(byteBuffer);
        }
        byteBuffer.putInt(RBBasic.getUniqueRDSSpawned().size());
        for (int index2 = 0; index2 < RBBasic.getUniqueRDSSpawned().size(); ++index2) {
            GameWindow.WriteString(byteBuffer, RBBasic.getUniqueRDSSpawned().get(index2));
        }
    }
    
    public void load() {
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("map_meta.bin");
        try {
            final FileInputStream in = new FileInputStream(fileInCurrentSave);
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    synchronized (SliceY.SliceBufferLock) {
                        SliceY.SliceBuffer.clear();
                        SliceY.SliceBuffer.limit(bufferedInputStream.read(SliceY.SliceBuffer.array()));
                        this.load(SliceY.SliceBuffer);
                    }
                    bufferedInputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedInputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                in.close();
            }
            catch (Throwable t2) {
                try {
                    in.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (FileNotFoundException ex2) {}
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public void load(final ByteBuffer byteBuffer) {
        byteBuffer.mark();
        final byte value = byteBuffer.get();
        final byte value2 = byteBuffer.get();
        final byte value3 = byteBuffer.get();
        final byte value4 = byteBuffer.get();
        int int1;
        if (value == 77 && value2 == 69 && value3 == 84 && value4 == 65) {
            int1 = byteBuffer.getInt();
        }
        else {
            int1 = 33;
            byteBuffer.reset();
        }
        int a = byteBuffer.getInt();
        int a2 = byteBuffer.getInt();
        if (a != this.Grid.length || a2 != this.Grid[0].length) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(IIII)Ljava/lang/String;, a, a2, this.Grid.length, this.Grid[0].length));
            a = Math.min(a, this.Grid.length);
            a2 = Math.min(a2, this.Grid[0].length);
        }
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < a; ++i) {
            for (int j = 0; j < a2; ++j) {
                final IsoMetaCell isoMetaCell = this.Grid[i][j];
                for (int int2 = byteBuffer.getInt(), k = 0; k < int2; ++k) {
                    final int int3 = byteBuffer.getInt();
                    boolean bDoneSpawn = false;
                    boolean roofFixed = false;
                    boolean explored;
                    boolean bLightsActive;
                    if (int1 >= 160) {
                        final short short1 = byteBuffer.getShort();
                        explored = ((short1 & 0x1) != 0x0);
                        bLightsActive = ((short1 & 0x2) != 0x0);
                        bDoneSpawn = ((short1 & 0x4) != 0x0);
                        roofFixed = ((short1 & 0x8) != 0x0);
                    }
                    else {
                        explored = (byteBuffer.get() == 1);
                        if (int1 >= 34) {
                            bLightsActive = (byteBuffer.get() == 1);
                        }
                        else {
                            bLightsActive = (Rand.Next(2) == 0);
                        }
                    }
                    if (isoMetaCell.info != null) {
                        final RoomDef roomDef = isoMetaCell.info.Rooms.get(int3);
                        if (roomDef != null) {
                            roomDef.setExplored(explored);
                            roomDef.bLightsActive = bLightsActive;
                            roomDef.bDoneSpawn = bDoneSpawn;
                            roomDef.setRoofFixed(roofFixed);
                        }
                        else {
                            DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, int3, i, j));
                        }
                    }
                }
                final int int4 = byteBuffer.getInt();
                n += int4;
                for (int l = 0; l < int4; ++l) {
                    final boolean bAlarmed = byteBuffer.get() == 1;
                    final int keyId = (int1 >= 57) ? byteBuffer.getInt() : -1;
                    final boolean seen = int1 >= 74 && byteBuffer.get() == 1;
                    final boolean hasBeenVisited = int1 >= 107 && byteBuffer.get() == 1;
                    final int n3 = (int1 >= 111 && int1 < 121) ? byteBuffer.getInt() : 0;
                    final int lootRespawnHour = (int1 >= 125) ? byteBuffer.getInt() : 0;
                    if (isoMetaCell.info != null && l < isoMetaCell.info.Buildings.size()) {
                        final BuildingDef buildingDef = isoMetaCell.info.Buildings.get(l);
                        if (bAlarmed) {
                            ++n2;
                        }
                        buildingDef.bAlarmed = bAlarmed;
                        buildingDef.setKeyId(keyId);
                        if (int1 >= 74) {
                            buildingDef.seen = seen;
                        }
                        buildingDef.hasBeenVisited = hasBeenVisited;
                        buildingDef.lootRespawnHour = lootRespawnHour;
                    }
                }
            }
        }
        if (int1 <= 112) {
            this.Zones.clear();
            for (int n4 = 0; n4 < this.height; ++n4) {
                for (int n5 = 0; n5 < this.width; ++n5) {
                    final IsoMetaCell isoMetaCell2 = this.Grid[n5][n4];
                    if (isoMetaCell2 != null) {
                        for (int n6 = 0; n6 < 30; ++n6) {
                            for (int n7 = 0; n7 < 30; ++n7) {
                                isoMetaCell2.ChunkMap[n7 + n6 * 30].clearZones();
                            }
                        }
                    }
                }
            }
            this.loadZone(byteBuffer, int1);
        }
        SafeHouse.clearSafehouseList();
        for (int int5 = byteBuffer.getInt(), n8 = 0; n8 < int5; ++n8) {
            SafeHouse.load(byteBuffer, int1);
        }
        NonPvpZone.nonPvpZoneList.clear();
        for (int int6 = byteBuffer.getInt(), n9 = 0; n9 < int6; ++n9) {
            final NonPvpZone e = new NonPvpZone();
            e.load(byteBuffer, int1);
            NonPvpZone.getAllZones().add(e);
        }
        Faction.factions = new ArrayList<Faction>();
        for (int int7 = byteBuffer.getInt(), n10 = 0; n10 < int7; ++n10) {
            final Faction e2 = new Faction();
            e2.load(byteBuffer, int1);
            Faction.getFactions().add(e2);
        }
        if (GameServer.bServer) {
            byteBuffer.getInt();
            StashSystem.load(byteBuffer, int1);
        }
        else if (GameClient.bClient) {
            byteBuffer.position(byteBuffer.getInt());
        }
        else {
            StashSystem.load(byteBuffer, int1);
        }
        final ArrayList<String> uniqueRDSSpawned = RBBasic.getUniqueRDSSpawned();
        uniqueRDSSpawned.clear();
        for (int int8 = byteBuffer.getInt(), n11 = 0; n11 < int8; ++n11) {
            uniqueRDSSpawned.add(GameWindow.ReadString(byteBuffer));
        }
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public IsoMetaCell getCellData(final int n, final int n2) {
        if (n - this.minX < 0 || n2 - this.minY < 0 || n - this.minX >= this.width || n2 - this.minY >= this.height) {
            return null;
        }
        return this.Grid[n - this.minX][n2 - this.minY];
    }
    
    public IsoMetaCell getCellDataAbs(final int n, final int n2) {
        return this.Grid[n][n2];
    }
    
    public IsoMetaCell getCurrentCellData() {
        final int worldX = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX;
        final int worldY = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY;
        final float n = (float)worldX;
        final float n2 = (float)worldY;
        float n3 = n / 30.0f;
        float n4 = n2 / 30.0f;
        if (n3 < 0.0f) {
            n3 = (float)((int)n3 - 1);
        }
        if (n4 < 0.0f) {
            n4 = (float)((int)n4 - 1);
        }
        return this.getCellData((int)n3, (int)n4);
    }
    
    public IsoMetaCell getMetaGridFromTile(final int n, final int n2) {
        return this.getCellData(n / 300, n2 / 300);
    }
    
    public IsoMetaChunk getCurrentChunkData() {
        final int worldX = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX;
        final int worldY = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY;
        final float n = (float)worldX;
        final float n2 = (float)worldY;
        float n3 = n / 30.0f;
        float n4 = n2 / 30.0f;
        if (n3 < 0.0f) {
            n3 = (float)((int)n3 - 1);
        }
        if (n4 < 0.0f) {
            n4 = (float)((int)n4 - 1);
        }
        final int n5 = (int)n3;
        final int n6 = (int)n4;
        return this.getCellData(n5, n6).getChunk(IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX - n5 * 30, IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY - n6 * 30);
    }
    
    public IsoMetaChunk getChunkData(final int n, final int n2) {
        final float n3 = (float)n;
        final float n4 = (float)n2;
        float n5 = n3 / 30.0f;
        float n6 = n4 / 30.0f;
        if (n5 < 0.0f) {
            n5 = (float)((int)n5 - 1);
        }
        if (n6 < 0.0f) {
            n6 = (float)((int)n6 - 1);
        }
        final int n7 = (int)n5;
        final int n8 = (int)n6;
        final IsoMetaCell cellData = this.getCellData(n7, n8);
        if (cellData == null) {
            return null;
        }
        return cellData.getChunk(n - n7 * 30, n2 - n8 * 30);
    }
    
    public IsoMetaChunk getChunkDataFromTile(final int n, final int n2) {
        final int n3 = n / 10;
        final int n4 = n2 / 10;
        final int n5 = n3 - this.minX * 30;
        final int n6 = n4 - this.minY * 30;
        final int n7 = n5 / 30;
        final int n8 = n6 / 30;
        final int n9 = n5 + this.minX * 30;
        final int n10 = n6 + this.minY * 30;
        final int n11 = n7 + this.minX;
        final int n12 = n8 + this.minY;
        final IsoMetaCell cellData = this.getCellData(n11, n12);
        if (cellData == null) {
            return null;
        }
        return cellData.getChunk(n9 - n11 * 30, n10 - n12 * 30);
    }
    
    public boolean isValidSquare(final int n, final int n2) {
        return n >= this.minX * 300 && n < (this.maxX + 1) * 300 && n2 >= this.minY * 300 && n2 < (this.maxY + 1) * 300;
    }
    
    public boolean isValidChunk(int n, int n2) {
        n *= 10;
        n2 *= 10;
        return n >= this.minX * 300 && n < (this.maxX + 1) * 300 && n2 >= this.minY * 300 && n2 < (this.maxY + 1) * 300 && this.Grid[n / 300 - this.minX][n2 / 300 - this.minY].info != null;
    }
    
    public void Create() {
        this.CreateStep1();
        this.CreateStep2();
    }
    
    public void CreateStep1() {
        this.minX = 10000000;
        this.minY = 10000000;
        this.maxX = -10000000;
        this.maxY = -10000000;
        IsoLot.InfoHeaders.clear();
        IsoLot.InfoHeaderNames.clear();
        IsoLot.InfoFileNames.clear();
        final long currentTimeMillis = System.currentTimeMillis();
        DebugLog.log("IsoMetaGrid.Create: begin scanning directories");
        final ArrayList<String> lotDirectories = this.getLotDirectories();
        DebugLog.log("Looking in these map folders:");
        final Iterator<String> iterator = lotDirectories.iterator();
        while (iterator.hasNext()) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, new File(ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (String)iterator.next()))).getAbsolutePath()));
        }
        DebugLog.log("<End of map-folders list>");
        for (final String s : lotDirectories) {
            final File file = new File(ZomboidFileSystem.instance.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s)));
            if (file.isDirectory()) {
                final ChooseGameInfo.Map mapDetails = ChooseGameInfo.getMapDetails(s);
                final String[] list = file.list();
                for (int i = 0; i < list.length; ++i) {
                    if (!IsoLot.InfoFileNames.containsKey(list[i])) {
                        if (list[i].endsWith(".lotheader")) {
                            final String[] split = list[i].split("_");
                            split[1] = split[1].replace(".lotheader", "");
                            final int int1 = Integer.parseInt(split[0].trim());
                            final int int2 = Integer.parseInt(split[1].trim());
                            if (int1 < this.minX) {
                                this.minX = int1;
                            }
                            if (int2 < this.minY) {
                                this.minY = int2;
                            }
                            if (int1 > this.maxX) {
                                this.maxX = int1;
                            }
                            if (int2 > this.maxY) {
                                this.maxY = int2;
                            }
                            IsoLot.InfoFileNames.put(list[i], invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath(), File.separator, list[i]));
                            final LotHeader value = new LotHeader();
                            value.bFixed2x = mapDetails.isFixed2x();
                            IsoLot.InfoHeaders.put(list[i], value);
                            IsoLot.InfoHeaderNames.add(list[i]);
                        }
                        else if (list[i].endsWith(".lotpack")) {
                            IsoLot.InfoFileNames.put(list[i], invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath(), File.separator, list[i]));
                        }
                        else if (list[i].startsWith("chunkdata_")) {
                            IsoLot.InfoFileNames.put(list[i], invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath(), File.separator, list[i]));
                        }
                    }
                }
            }
        }
        if (this.maxX < this.minX || this.maxY < this.minY) {
            throw new IllegalStateException("Failed to find any .lotheader files");
        }
        this.Grid = new IsoMetaCell[this.maxX - this.minX + 1][this.maxY - this.minY + 1];
        this.width = this.maxX - this.minX + 1;
        this.height = this.maxY - this.minY + 1;
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, (System.currentTimeMillis() - currentTimeMillis) / 1000.0f));
        DebugLog.log("IsoMetaGrid.Create: begin loading");
        this.createStartTime = System.currentTimeMillis();
        for (int j = 0; j < 8; ++j) {
            final MetaGridLoaderThread metaGridLoaderThread = new MetaGridLoaderThread(this.minY + j);
            metaGridLoaderThread.setDaemon(true);
            metaGridLoaderThread.setName(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, j));
            metaGridLoaderThread.start();
            this.threads[j] = metaGridLoaderThread;
        }
    }
    
    public void CreateStep2() {
        int i = 1;
        while (i != 0) {
            i = 0;
            for (int j = 0; j < 8; ++j) {
                if (this.threads[j].isAlive()) {
                    i = 1;
                    try {
                        Thread.sleep(100L);
                    }
                    catch (InterruptedException ex) {}
                    break;
                }
            }
        }
        for (int k = 0; k < 8; ++k) {
            this.threads[k].postLoad();
            this.threads[k] = null;
        }
        for (int l = 0; l < this.Buildings.size(); ++l) {
            final BuildingDef buildingDef = this.Buildings.get(l);
            if (!Core.GameMode.equals("LastStand") && buildingDef.rooms.size() > 2) {
                int n = 11;
                if (SandboxOptions.instance.getElecShutModifier() > -1 && GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier()) {
                    n = 9;
                }
                if (SandboxOptions.instance.Alarm.getValue() == 1) {
                    n = -1;
                }
                else if (SandboxOptions.instance.Alarm.getValue() == 2) {
                    n += 5;
                }
                else if (SandboxOptions.instance.Alarm.getValue() == 3) {
                    n += 3;
                }
                else if (SandboxOptions.instance.Alarm.getValue() == 5) {
                    n -= 3;
                }
                else if (SandboxOptions.instance.Alarm.getValue() == 6) {
                    n -= 5;
                }
                if (n > -1) {
                    buildingDef.bAlarmed = (Rand.Next(n) == 0);
                }
            }
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, (System.currentTimeMillis() - this.createStartTime) / 1000.0f));
    }
    
    public void Dispose() {
        if (this.Grid == null) {
            return;
        }
        for (int i = 0; i < this.Grid.length; ++i) {
            final IsoMetaCell[] a = this.Grid[i];
            for (int j = 0; j < a.length; ++j) {
                final IsoMetaCell isoMetaCell = a[j];
                if (isoMetaCell != null) {
                    isoMetaCell.Dispose();
                }
            }
            Arrays.fill(a, null);
        }
        Arrays.fill(this.Grid, null);
        this.Grid = null;
        final Iterator<BuildingDef> iterator = this.Buildings.iterator();
        while (iterator.hasNext()) {
            iterator.next().Dispose();
        }
        this.Buildings.clear();
        this.VehiclesZones.clear();
        final Iterator<Zone> iterator2 = this.Zones.iterator();
        while (iterator2.hasNext()) {
            iterator2.next().Dispose();
        }
        this.Zones.clear();
        this.sharedStrings.clear();
    }
    
    public Vector2 getRandomIndoorCoord() {
        return null;
    }
    
    public RoomDef getRandomRoomBetweenRange(final float n, final float n2, final float n3, final float n4) {
        IsoMetaGrid.roomChoices.clear();
        for (int i = 0; i < IsoLot.InfoHeaderNames.size(); ++i) {
            final LotHeader lotHeader = IsoLot.InfoHeaders.get(IsoLot.InfoHeaderNames.get(i));
            if (!lotHeader.RoomList.isEmpty()) {
                for (int j = 0; j < lotHeader.RoomList.size(); ++j) {
                    final RoomDef e = lotHeader.RoomList.get(j);
                    final float distanceManhatten = IsoUtils.DistanceManhatten(n, n2, (float)e.x, (float)e.y);
                    if (distanceManhatten > n3 && distanceManhatten < n4) {
                        IsoMetaGrid.roomChoices.add(e);
                    }
                }
            }
        }
        if (!IsoMetaGrid.roomChoices.isEmpty()) {
            return IsoMetaGrid.roomChoices.get(Rand.Next(IsoMetaGrid.roomChoices.size()));
        }
        return null;
    }
    
    public RoomDef getRandomRoomNotInRange(final float n, final float n2, final int n3) {
        RoomDef roomDef;
        do {
            LotHeader lotHeader;
            do {
                lotHeader = IsoLot.InfoHeaders.get(IsoLot.InfoHeaderNames.get(Rand.Next(IsoLot.InfoHeaderNames.size())));
            } while (lotHeader.RoomList.isEmpty());
            roomDef = lotHeader.RoomList.get(Rand.Next(lotHeader.RoomList.size()));
        } while (roomDef == null || IsoUtils.DistanceManhatten(n, n2, (float)roomDef.x, (float)roomDef.y) < n3);
        return roomDef;
    }
    
    public void save() {
        try {
            final FileOutputStream out = new FileOutputStream(ZomboidFileSystem.instance.getFileInCurrentSave("map_meta.bin"));
            try {
                final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);
                try {
                    synchronized (SliceY.SliceBufferLock) {
                        SliceY.SliceBuffer.clear();
                        this.save(SliceY.SliceBuffer);
                        bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
                    }
                    bufferedOutputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedOutputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                out.close();
            }
            catch (Throwable t2) {
                try {
                    out.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
            final FileOutputStream out2 = new FileOutputStream(ZomboidFileSystem.instance.getFileInCurrentSave("map_zone.bin"));
            try {
                final BufferedOutputStream bufferedOutputStream2 = new BufferedOutputStream(out2);
                try {
                    synchronized (SliceY.SliceBufferLock) {
                        SliceY.SliceBuffer.clear();
                        this.saveZone(SliceY.SliceBuffer);
                        bufferedOutputStream2.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
                    }
                    bufferedOutputStream2.close();
                }
                catch (Throwable t3) {
                    try {
                        bufferedOutputStream2.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                    throw t3;
                }
                out2.close();
            }
            catch (Throwable t4) {
                try {
                    out2.close();
                }
                catch (Throwable exception4) {
                    t4.addSuppressed(exception4);
                }
                throw t4;
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public void loadZones() {
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("map_zone.bin");
        try {
            final FileInputStream in = new FileInputStream(fileInCurrentSave);
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    synchronized (SliceY.SliceBufferLock) {
                        SliceY.SliceBuffer.clear();
                        SliceY.SliceBuffer.limit(bufferedInputStream.read(SliceY.SliceBuffer.array()));
                        this.loadZone(SliceY.SliceBuffer, -1);
                    }
                    bufferedInputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedInputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                in.close();
            }
            catch (Throwable t2) {
                try {
                    in.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (FileNotFoundException ex2) {}
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public void loadZone(final ByteBuffer byteBuffer, int int1) {
        if (int1 == -1) {
            final byte value = byteBuffer.get();
            final byte value2 = byteBuffer.get();
            final byte value3 = byteBuffer.get();
            final byte value4 = byteBuffer.get();
            if (value != 90 || value2 != 79 || value3 != 78 || value4 != 69) {
                DebugLog.log("ERROR: expected 'ZONE' at start of map_zone.bin");
                return;
            }
            int1 = byteBuffer.getInt();
        }
        final int size = this.Zones.size();
        if ((!GameServer.bServer && int1 >= 34) || (GameServer.bServer && int1 >= 36)) {
            final Iterator<Zone> iterator = this.Zones.iterator();
            while (iterator.hasNext()) {
                iterator.next().Dispose();
            }
            this.Zones.clear();
            for (int i = 0; i < this.height; ++i) {
                for (int j = 0; j < this.width; ++j) {
                    final IsoMetaCell isoMetaCell = this.Grid[j][i];
                    if (isoMetaCell != null) {
                        for (int k = 0; k < 30; ++k) {
                            for (int l = 0; l < 30; ++l) {
                                isoMetaCell.ChunkMap[l + k * 30].clearZones();
                            }
                        }
                    }
                }
            }
            final ZoneGeometryType[] values = ZoneGeometryType.values();
            final TIntArrayList list = new TIntArrayList();
            if (int1 >= 141) {
                final int int2 = byteBuffer.getInt();
                final HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
                for (int m = 0; m < int2; ++m) {
                    hashMap.put(m, GameWindow.ReadStringUTF(byteBuffer));
                }
                final int int3 = byteBuffer.getInt();
                DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, int3));
                for (int n = 0; n < int3; ++n) {
                    final String s = hashMap.get((int)byteBuffer.getShort());
                    final String s2 = hashMap.get((int)byteBuffer.getShort());
                    final int int4 = byteBuffer.getInt();
                    final int int5 = byteBuffer.getInt();
                    final byte value5 = byteBuffer.get();
                    final int int6 = byteBuffer.getInt();
                    final int int7 = byteBuffer.getInt();
                    ZoneGeometryType invalid = ZoneGeometryType.INVALID;
                    list.clear();
                    int clamp = 0;
                    if (int1 >= 185) {
                        int value6 = byteBuffer.get();
                        if (value6 < 0 || value6 >= values.length) {
                            value6 = 0;
                        }
                        invalid = values[value6];
                        if (invalid != ZoneGeometryType.INVALID) {
                            if (int1 >= 186 && invalid == ZoneGeometryType.Polyline) {
                                clamp = PZMath.clamp(byteBuffer.get(), 0, 255);
                            }
                            for (short short1 = byteBuffer.getShort(), n2 = 0; n2 < short1; ++n2) {
                                list.add(byteBuffer.getInt());
                            }
                        }
                    }
                    final int int8 = byteBuffer.getInt();
                    final Zone registerZone = this.registerZone(s, s2, int4, int5, value5, int6, int7, invalid, (invalid == ZoneGeometryType.INVALID) ? null : list, clamp);
                    registerZone.hourLastSeen = int8;
                    registerZone.haveConstruction = (byteBuffer.get() == 1);
                    registerZone.lastActionTimestamp = byteBuffer.getInt();
                    registerZone.setOriginalName(hashMap.get((int)byteBuffer.getShort()));
                    registerZone.id = byteBuffer.getDouble();
                }
                for (int int9 = byteBuffer.getInt(), n3 = 0; n3 < int9; ++n3) {
                    final String readString = GameWindow.ReadString(byteBuffer);
                    final ArrayList<Double> value7 = new ArrayList<Double>();
                    for (int int10 = byteBuffer.getInt(), n4 = 0; n4 < int10; ++n4) {
                        value7.add(byteBuffer.getDouble());
                    }
                    IsoWorld.instance.getSpawnedZombieZone().put(readString, value7);
                }
                return;
            }
            final int int11 = byteBuffer.getInt();
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, int11));
            if (int1 <= 112 && int11 > size * 2) {
                DebugLog.log("ERROR: seems like too many zones in map_zone.bin");
                return;
            }
            for (int n5 = 0; n5 < int11; ++n5) {
                final String readString2 = GameWindow.ReadString(byteBuffer);
                final String readString3 = GameWindow.ReadString(byteBuffer);
                final int int12 = byteBuffer.getInt();
                final int int13 = byteBuffer.getInt();
                final int int14 = byteBuffer.getInt();
                final int int15 = byteBuffer.getInt();
                final int int16 = byteBuffer.getInt();
                final int n6 = (int1 < 121) ? byteBuffer.getInt() : 0;
                final int hourLastSeen = (int1 < 68) ? byteBuffer.getShort() : byteBuffer.getInt();
                final Zone registerZone2 = this.registerZone(readString2, readString3, int12, int13, int14, int15, int16);
                registerZone2.hourLastSeen = hourLastSeen;
                if (int1 >= 35) {
                    registerZone2.haveConstruction = (byteBuffer.get() == 1);
                }
                if (int1 >= 41) {
                    registerZone2.lastActionTimestamp = byteBuffer.getInt();
                }
                if (int1 >= 98) {
                    registerZone2.setOriginalName(GameWindow.ReadString(byteBuffer));
                }
                if (int1 >= 110 && int1 < 121) {
                    byteBuffer.getInt();
                }
                registerZone2.id = byteBuffer.getDouble();
            }
        }
    }
    
    public void saveZone(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)90);
        byteBuffer.put((byte)79);
        byteBuffer.put((byte)78);
        byteBuffer.put((byte)69);
        byteBuffer.putInt(186);
        final HashSet<String> c = new HashSet<String>();
        for (int i = 0; i < this.Zones.size(); ++i) {
            final Zone zone = this.Zones.get(i);
            c.add(zone.getName());
            c.add(zone.getOriginalName());
            c.add(zone.getType());
        }
        final ArrayList list = new ArrayList<String>(c);
        final HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        for (int j = 0; j < list.size(); ++j) {
            hashMap.put(list.get(j), j);
        }
        if (list.size() > 32767) {
            throw new IllegalStateException("IsoMetaGrid.saveZone() string table is too large");
        }
        byteBuffer.putInt(list.size());
        for (int k = 0; k < list.size(); ++k) {
            GameWindow.WriteString(byteBuffer, list.get(k));
        }
        byteBuffer.putInt(this.Zones.size());
        for (int l = 0; l < this.Zones.size(); ++l) {
            final Zone zone2 = this.Zones.get(l);
            byteBuffer.putShort(hashMap.get(zone2.getName()).shortValue());
            byteBuffer.putShort(hashMap.get(zone2.getType()).shortValue());
            byteBuffer.putInt(zone2.x);
            byteBuffer.putInt(zone2.y);
            byteBuffer.put((byte)zone2.z);
            byteBuffer.putInt(zone2.w);
            byteBuffer.putInt(zone2.h);
            byteBuffer.put((byte)zone2.geometryType.ordinal());
            if (!zone2.isRectangle()) {
                if (zone2.isPolyline()) {
                    byteBuffer.put((byte)zone2.polylineWidth);
                }
                byteBuffer.putShort((short)zone2.points.size());
                for (int n = 0; n < zone2.points.size(); ++n) {
                    byteBuffer.putInt(zone2.points.get(n));
                }
            }
            byteBuffer.putInt(zone2.hourLastSeen);
            byteBuffer.put((byte)(zone2.haveConstruction ? 1 : 0));
            byteBuffer.putInt(zone2.lastActionTimestamp);
            byteBuffer.putShort(hashMap.get(zone2.getOriginalName()).shortValue());
            byteBuffer.putDouble(zone2.id);
        }
        c.clear();
        list.clear();
        hashMap.clear();
        byteBuffer.putInt(IsoWorld.instance.getSpawnedZombieZone().size());
        for (final String key : IsoWorld.instance.getSpawnedZombieZone().keySet()) {
            final ArrayList<Double> list2 = IsoWorld.instance.getSpawnedZombieZone().get(key);
            GameWindow.WriteString(byteBuffer, key);
            byteBuffer.putInt(list2.size());
            for (int index = 0; index < list2.size(); ++index) {
                byteBuffer.putDouble(list2.get(index));
            }
        }
    }
    
    private void getLotDirectories(final String s, final ArrayList<String> list) {
        if (list.contains(s)) {
            return;
        }
        final ChooseGameInfo.Map mapDetails = ChooseGameInfo.getMapDetails(s);
        if (mapDetails == null) {
            return;
        }
        list.add(s);
        final Iterator<String> iterator = mapDetails.getLotDirectories().iterator();
        while (iterator.hasNext()) {
            this.getLotDirectories(iterator.next(), list);
        }
    }
    
    public ArrayList<String> getLotDirectories() {
        if (GameClient.bClient) {
            Core.GameMap = GameClient.GameMap;
        }
        if (GameServer.bServer) {
            Core.GameMap = GameServer.GameMap;
        }
        if (Core.GameMap.equals("DEFAULT")) {
            final MapGroups mapGroups = new MapGroups();
            mapGroups.createGroups();
            if (mapGroups.getNumberOfGroups() != 1) {
                throw new RuntimeException("GameMap is DEFAULT but there are multiple worlds to choose from");
            }
            mapGroups.setWorld(0);
        }
        final ArrayList<String> list = new ArrayList<String>();
        if (Core.GameMap.contains(";")) {
            final String[] split = Core.GameMap.split(";");
            for (int i = 0; i < split.length; ++i) {
                final String trim = split[i].trim();
                if (!trim.isEmpty() && !list.contains(trim)) {
                    list.add(trim);
                }
            }
        }
        else {
            this.getLotDirectories(Core.GameMap, list);
        }
        return list;
    }
    
    public static boolean isPreferredZoneForSquare(final String o) {
        return IsoMetaGrid.s_PreferredZoneTypes.contains(o);
    }
    
    static {
        IsoMetaGrid.s_PreferredZoneTypes = new ArrayList<String>();
        IsoMetaGrid.s_clipper = null;
        IsoMetaGrid.s_clipperOffset = null;
        IsoMetaGrid.s_clipperBuffer = null;
        IsoMetaGrid.a = new Rectangle();
        IsoMetaGrid.b = new Rectangle();
        IsoMetaGrid.roomChoices = new ArrayList<RoomDef>(50);
        IsoMetaGrid.s_PreferredZoneTypes.add("DeepForest");
        IsoMetaGrid.s_PreferredZoneTypes.add("Farm");
        IsoMetaGrid.s_PreferredZoneTypes.add("FarmLand");
        IsoMetaGrid.s_PreferredZoneTypes.add("Forest");
        IsoMetaGrid.s_PreferredZoneTypes.add("Vegitation");
        IsoMetaGrid.s_PreferredZoneTypes.add("Nav");
        IsoMetaGrid.s_PreferredZoneTypes.add("TownZone");
        IsoMetaGrid.s_PreferredZoneTypes.add("TrailerPark");
    }
    
    public enum ZoneGeometryType
    {
        INVALID, 
        Point, 
        Polyline, 
        Polygon;
        
        private static /* synthetic */ ZoneGeometryType[] $values() {
            return new ZoneGeometryType[] { ZoneGeometryType.INVALID, ZoneGeometryType.Point, ZoneGeometryType.Polyline, ZoneGeometryType.Polygon };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public static class Zone
    {
        public Double id;
        public int hourLastSeen;
        public int lastActionTimestamp;
        public boolean haveConstruction;
        public final HashMap<String, Integer> spawnedZombies;
        public String zombiesTypeToSpawn;
        public Boolean spawnSpecialZombies;
        public String name;
        public String type;
        public int x;
        public int y;
        public int z;
        public int w;
        public int h;
        public ZoneGeometryType geometryType;
        public final TIntArrayList points;
        public int polylineWidth;
        public float[] polylineOutlinePoints;
        public float[] triangles;
        public int pickedXForZoneStory;
        public int pickedYForZoneStory;
        public RandomizedZoneStoryBase pickedRZStory;
        private String originalName;
        public boolean isPreferredZoneForSquare;
        static final PolygonalMap2.LiangBarsky LIANG_BARSKY;
        static final Vector2 L_lineSegmentIntersects;
        
        public Zone(final String s, final String type, final int x, final int y, final int z, final int w, final int h) {
            this.id = 0.0;
            this.hourLastSeen = 0;
            this.lastActionTimestamp = 0;
            this.haveConstruction = false;
            this.spawnedZombies = new HashMap<String, Integer>();
            this.zombiesTypeToSpawn = null;
            this.spawnSpecialZombies = null;
            this.geometryType = ZoneGeometryType.INVALID;
            this.points = new TIntArrayList();
            this.polylineWidth = 0;
            this.isPreferredZoneForSquare = false;
            this.id = Rand.Next(9999999) + 100000.0;
            this.originalName = s;
            this.name = s;
            this.type = type;
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
            this.h = h;
        }
        
        public void setX(final int x) {
            this.x = x;
        }
        
        public void setY(final int y) {
            this.y = y;
        }
        
        public void setW(final int w) {
            this.w = w;
        }
        
        public void setH(final int h) {
            this.h = h;
        }
        
        public boolean isPoint() {
            return this.geometryType == ZoneGeometryType.Point;
        }
        
        public boolean isPolygon() {
            return this.geometryType == ZoneGeometryType.Polygon;
        }
        
        public boolean isPolyline() {
            return this.geometryType == ZoneGeometryType.Polyline;
        }
        
        public boolean isRectangle() {
            return this.geometryType == ZoneGeometryType.INVALID;
        }
        
        public void setPickedXForZoneStory(final int pickedXForZoneStory) {
            this.pickedXForZoneStory = pickedXForZoneStory;
        }
        
        public void setPickedYForZoneStory(final int pickedYForZoneStory) {
            this.pickedYForZoneStory = pickedYForZoneStory;
        }
        
        public float getHoursSinceLastSeen() {
            return (float)GameTime.instance.getWorldAgeHours() - this.hourLastSeen;
        }
        
        public void setHourSeenToCurrent() {
            this.hourLastSeen = (int)GameTime.instance.getWorldAgeHours();
        }
        
        public void setHaveConstruction(final boolean haveConstruction) {
            this.haveConstruction = haveConstruction;
            if (GameClient.bClient) {
                final ByteBufferWriter startPacket = GameClient.connection.startPacket();
                PacketTypes.PacketType.ConstructedZone.doPacket(startPacket);
                startPacket.putInt(this.x);
                startPacket.putInt(this.y);
                startPacket.putInt(this.z);
                PacketTypes.PacketType.ConstructedZone.send(GameClient.connection);
            }
        }
        
        public boolean haveCons() {
            return this.haveConstruction;
        }
        
        public int getZombieDensity() {
            final IsoMetaChunk chunkDataFromTile = IsoWorld.instance.MetaGrid.getChunkDataFromTile(this.x, this.y);
            if (chunkDataFromTile != null) {
                return chunkDataFromTile.getUnadjustedZombieIntensity();
            }
            return 0;
        }
        
        public boolean contains(final int n, final int n2, final int n3) {
            if (n3 != this.z) {
                return false;
            }
            if (n < this.x || n >= this.x + this.w) {
                return false;
            }
            if (n2 < this.y || n2 >= this.y + this.h) {
                return false;
            }
            if (this.isPoint()) {
                return false;
            }
            if (!this.isPolyline()) {
                return !this.isPolygon() || this.isPointInPolygon_WindingNumber(n + 0.5f, n2 + 0.5f, 0) == PolygonHit.Inside;
            }
            if (this.polylineWidth > 0) {
                this.checkPolylineOutline();
                return this.isPointInPolyline_WindingNumber(n + 0.5f, n2 + 0.5f, 0) == PolygonHit.Inside;
            }
            return false;
        }
        
        public boolean intersects(final int n, final int n2, final int n3, final int n4, final int n5) {
            if (this.z != n3) {
                return false;
            }
            if (n + n4 <= this.x || n >= this.x + this.w) {
                return false;
            }
            if (n2 + n5 <= this.y || n2 >= this.y + this.h) {
                return false;
            }
            if (this.isPolygon()) {
                return this.polygonRectIntersect(n, n2, n4, n5);
            }
            if (!this.isPolyline()) {
                return true;
            }
            if (this.polylineWidth > 0) {
                this.checkPolylineOutline();
                return this.polylineOutlineRectIntersect(n, n2, n4, n5);
            }
            for (int i = 0; i < this.points.size() - 2; i += 2) {
                final int quick = this.points.getQuick(i);
                final int quick2 = this.points.getQuick(i + 1);
                if (Zone.LIANG_BARSKY.lineRectIntersect((float)quick, (float)quick2, (float)(this.points.getQuick(i + 2) - quick), (float)(this.points.getQuick(i + 3) - quick2), (float)n, (float)n2, (float)(n + n4), (float)(n2 + n5))) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean difference(final int n, final int n2, final int n3, final int n4, final int n5, final ArrayList<Zone> list) {
            list.clear();
            if (!this.intersects(n, n2, n3, n4, n5)) {
                return false;
            }
            if (this.isRectangle()) {
                if (this.x < n) {
                    final int max = Math.max(n2, this.y);
                    list.add(new Zone(this.name, this.type, this.x, max, n3, n - this.x, Math.min(n2 + n5, this.y + this.h) - max));
                }
                if (n + n4 < this.x + this.w) {
                    final int max2 = Math.max(n2, this.y);
                    list.add(new Zone(this.name, this.type, n + n4, max2, n3, this.x + this.w - (n + n4), Math.min(n2 + n5, this.y + this.h) - max2));
                }
                if (this.y < n2) {
                    list.add(new Zone(this.name, this.type, this.x, this.y, n3, this.w, n2 - this.y));
                }
                if (n2 + n5 < this.y + this.h) {
                    list.add(new Zone(this.name, this.type, this.x, n2 + n5, n3, this.w, this.y + this.h - (n2 + n5)));
                }
                return true;
            }
            if (this.isPolygon()) {
                if (IsoMetaGrid.s_clipper == null) {
                    IsoMetaGrid.s_clipper = new Clipper();
                    IsoMetaGrid.s_clipperBuffer = ByteBuffer.allocateDirect(2048);
                }
                final Clipper s_clipper = IsoMetaGrid.s_clipper;
                final ByteBuffer s_clipperBuffer = IsoMetaGrid.s_clipperBuffer;
                s_clipperBuffer.clear();
                for (int i = 0; i < this.points.size(); i += 2) {
                    s_clipperBuffer.putFloat((float)this.points.getQuick(i));
                    s_clipperBuffer.putFloat((float)this.points.getQuick(i + 1));
                }
                s_clipper.clear();
                s_clipper.addPath(this.points.size() / 2, s_clipperBuffer, false);
                s_clipper.clipAABB((float)n, (float)n2, (float)(n + n4), (float)(n2 + n5));
                for (int generatePolygons = s_clipper.generatePolygons(), j = 0; j < generatePolygons; ++j) {
                    s_clipperBuffer.clear();
                    s_clipper.getPolygon(j, s_clipperBuffer);
                    final short short1 = s_clipperBuffer.getShort();
                    if (short1 < 3) {
                        s_clipperBuffer.position(s_clipperBuffer.position() + short1 * 4 * 2);
                    }
                    else {
                        final Zone e = new Zone(this.name, this.type, this.x, this.y, this.z, this.w, this.h);
                        e.geometryType = ZoneGeometryType.Polygon;
                        for (short n6 = 0; n6 < short1; ++n6) {
                            e.points.add((int)s_clipperBuffer.getFloat());
                            e.points.add((int)s_clipperBuffer.getFloat());
                        }
                        list.add(e);
                    }
                }
            }
            if (this.isPolyline()) {}
            return true;
        }
        
        public IsoGridSquare getRandomSquareInZone() {
            return IsoWorld.instance.getCell().getGridSquare(Rand.Next(this.x, this.x + this.w), Rand.Next(this.y, this.y + this.h), this.z);
        }
        
        public IsoGridSquare getRandomUnseenSquareInZone() {
            return null;
        }
        
        public void addSquare(final IsoGridSquare isoGridSquare) {
        }
        
        public ArrayList<IsoGridSquare> getSquares() {
            return null;
        }
        
        public void removeSquare(final IsoGridSquare isoGridSquare) {
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getType() {
            return this.type;
        }
        
        public void setType(final String type) {
            this.type = type;
        }
        
        public int getLastActionTimestamp() {
            return this.lastActionTimestamp;
        }
        
        public void setLastActionTimestamp(final int lastActionTimestamp) {
            this.lastActionTimestamp = lastActionTimestamp;
        }
        
        public int getX() {
            return this.x;
        }
        
        public int getY() {
            return this.y;
        }
        
        public int getHeight() {
            return this.h;
        }
        
        public int getWidth() {
            return this.w;
        }
        
        public void sendToServer() {
            if (GameClient.bClient) {
                GameClient.registerZone(this, true);
            }
        }
        
        public String getOriginalName() {
            return this.originalName;
        }
        
        public void setOriginalName(final String originalName) {
            this.originalName = originalName;
        }
        
        public int getClippedSegmentOfPolyline(final int n, final int n2, final int n3, final int n4, final double[] array) {
            if (!this.isPolyline()) {
                return -1;
            }
            final float n5 = (this.polylineWidth % 2 == 0) ? 0.0f : 0.5f;
            for (int i = 0; i < this.points.size() - 2; i += 2) {
                final int quick = this.points.getQuick(i);
                final int quick2 = this.points.getQuick(i + 1);
                if (Zone.LIANG_BARSKY.lineRectIntersect(quick + n5, quick2 + n5, (float)(this.points.getQuick(i + 2) - quick), (float)(this.points.getQuick(i + 3) - quick2), (float)n, (float)n2, (float)n3, (float)n4, array)) {
                    return i / 2;
                }
            }
            return -1;
        }
        
        private void checkPolylineOutline() {
            if (this.polylineOutlinePoints != null) {
                return;
            }
            if (!this.isPolyline()) {
                return;
            }
            if (this.polylineWidth <= 0) {
                return;
            }
            if (IsoMetaGrid.s_clipperOffset == null) {
                IsoMetaGrid.s_clipperOffset = new ClipperOffset();
                IsoMetaGrid.s_clipperBuffer = ByteBuffer.allocateDirect(2048);
            }
            final ClipperOffset s_clipperOffset = IsoMetaGrid.s_clipperOffset;
            final ByteBuffer s_clipperBuffer = IsoMetaGrid.s_clipperBuffer;
            s_clipperOffset.clear();
            s_clipperBuffer.clear();
            final float n = (this.polylineWidth % 2 == 0) ? 0.0f : 0.5f;
            for (int i = 0; i < this.points.size(); i += 2) {
                final int value = this.points.get(i);
                final int value2 = this.points.get(i + 1);
                s_clipperBuffer.putFloat(value + n);
                s_clipperBuffer.putFloat(value2 + n);
            }
            s_clipperBuffer.flip();
            s_clipperOffset.addPath(this.points.size() / 2, s_clipperBuffer, ClipperOffset.JoinType.jtMiter.ordinal(), ClipperOffset.EndType.etOpenButt.ordinal());
            s_clipperOffset.execute(this.polylineWidth / 2.0f);
            if (s_clipperOffset.getPolygonCount() < 1) {
                DebugLog.General.warn((Object)"Failed to generate polyline outline");
                return;
            }
            s_clipperBuffer.clear();
            s_clipperOffset.getPolygon(0, s_clipperBuffer);
            final short short1 = s_clipperBuffer.getShort();
            this.polylineOutlinePoints = new float[short1 * 2];
            for (short n2 = 0; n2 < short1; ++n2) {
                this.polylineOutlinePoints[n2 * 2] = s_clipperBuffer.getFloat();
                this.polylineOutlinePoints[n2 * 2 + 1] = s_clipperBuffer.getFloat();
            }
        }
        
        float isLeft(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            return (n3 - n) * (n6 - n2) - (n5 - n) * (n4 - n2);
        }
        
        PolygonHit isPointInPolygon_WindingNumber(final float n, final float n2, final int n3) {
            int n4 = 0;
            for (int i = 0; i < this.points.size(); i += 2) {
                final int quick = this.points.getQuick(i);
                final int quick2 = this.points.getQuick(i + 1);
                final int quick3 = this.points.getQuick((i + 2) % this.points.size());
                final int quick4 = this.points.getQuick((i + 3) % this.points.size());
                if (quick2 <= n2) {
                    if (quick4 > n2 && this.isLeft((float)quick, (float)quick2, (float)quick3, (float)quick4, n, n2) > 0.0f) {
                        ++n4;
                    }
                }
                else if (quick4 <= n2 && this.isLeft((float)quick, (float)quick2, (float)quick3, (float)quick4, n, n2) < 0.0f) {
                    --n4;
                }
            }
            return (n4 == 0) ? PolygonHit.Outside : PolygonHit.Inside;
        }
        
        PolygonHit isPointInPolyline_WindingNumber(final float n, final float n2, final int n3) {
            int n4 = 0;
            final float[] polylineOutlinePoints = this.polylineOutlinePoints;
            if (polylineOutlinePoints == null) {
                return PolygonHit.Outside;
            }
            for (int i = 0; i < polylineOutlinePoints.length; i += 2) {
                final float n5 = polylineOutlinePoints[i];
                final float n6 = polylineOutlinePoints[i + 1];
                final float n7 = polylineOutlinePoints[(i + 2) % polylineOutlinePoints.length];
                final float n8 = polylineOutlinePoints[(i + 3) % polylineOutlinePoints.length];
                if (n6 <= n2) {
                    if (n8 > n2 && this.isLeft(n5, n6, n7, n8, n, n2) > 0.0f) {
                        ++n4;
                    }
                }
                else if (n8 <= n2 && this.isLeft(n5, n6, n7, n8, n, n2) < 0.0f) {
                    --n4;
                }
            }
            return (n4 == 0) ? PolygonHit.Outside : PolygonHit.Inside;
        }
        
        boolean polygonRectIntersect(final int n, final int n2, final int n3, final int n4) {
            return (this.x >= n && this.x + this.w <= n + n3 && this.y >= n2 && this.y + this.h <= n2 + n4) || this.lineSegmentIntersects((float)n, (float)n2, (float)(n + n3), (float)n2) || this.lineSegmentIntersects((float)(n + n3), (float)n2, (float)(n + n3), (float)(n2 + n4)) || this.lineSegmentIntersects((float)(n + n3), (float)(n2 + n4), (float)n, (float)(n2 + n4)) || this.lineSegmentIntersects((float)n, (float)(n2 + n4), (float)n, (float)n2);
        }
        
        boolean lineSegmentIntersects(final float n, final float n2, final float n3, final float n4) {
            Zone.L_lineSegmentIntersects.set(n3 - n, n4 - n2);
            final float length = Zone.L_lineSegmentIntersects.getLength();
            Zone.L_lineSegmentIntersects.normalize();
            final float x = Zone.L_lineSegmentIntersects.x;
            final float y = Zone.L_lineSegmentIntersects.y;
            for (int i = 0; i < this.points.size(); i += 2) {
                final float n5 = (float)this.points.getQuick(i);
                final float n6 = (float)this.points.getQuick(i + 1);
                final float n7 = (float)this.points.getQuick((i + 2) % this.points.size());
                final float n8 = (float)this.points.getQuick((i + 3) % this.points.size());
                final float n9 = n5;
                final float n10 = n6;
                final float n11 = n7;
                final float n12 = n8;
                final float n13 = n - n9;
                final float n14 = n2 - n10;
                final float n15 = n11 - n9;
                final float n16 = n12 - n10;
                final float n17 = 1.0f / (n16 * x - n15 * y);
                final float n18 = (n15 * n14 - n16 * n13) * n17;
                if (n18 >= 0.0f && n18 <= length) {
                    final float n19 = (n14 * x - n13 * y) * n17;
                    if (n19 >= 0.0f && n19 <= 1.0f) {
                        return true;
                    }
                }
            }
            return this.isPointInPolygon_WindingNumber((n + n3) / 2.0f, (n2 + n4) / 2.0f, 0) != PolygonHit.Outside;
        }
        
        boolean polylineOutlineRectIntersect(final int n, final int n2, final int n3, final int n4) {
            return this.polylineOutlinePoints != null && ((this.x >= n && this.x + this.w <= n + n3 && this.y >= n2 && this.y + this.h <= n2 + n4) || this.polylineOutlineSegmentIntersects((float)n, (float)n2, (float)(n + n3), (float)n2) || this.polylineOutlineSegmentIntersects((float)(n + n3), (float)n2, (float)(n + n3), (float)(n2 + n4)) || this.polylineOutlineSegmentIntersects((float)(n + n3), (float)(n2 + n4), (float)n, (float)(n2 + n4)) || this.polylineOutlineSegmentIntersects((float)n, (float)(n2 + n4), (float)n, (float)n2));
        }
        
        boolean polylineOutlineSegmentIntersects(final float n, final float n2, final float n3, final float n4) {
            Zone.L_lineSegmentIntersects.set(n3 - n, n4 - n2);
            final float length = Zone.L_lineSegmentIntersects.getLength();
            Zone.L_lineSegmentIntersects.normalize();
            final float x = Zone.L_lineSegmentIntersects.x;
            final float y = Zone.L_lineSegmentIntersects.y;
            final float[] polylineOutlinePoints = this.polylineOutlinePoints;
            for (int i = 0; i < polylineOutlinePoints.length; i += 2) {
                final float n5 = polylineOutlinePoints[i];
                final float n6 = polylineOutlinePoints[i + 1];
                final float n7 = polylineOutlinePoints[(i + 2) % polylineOutlinePoints.length];
                final float n8 = polylineOutlinePoints[(i + 3) % polylineOutlinePoints.length];
                final float n9 = n5;
                final float n10 = n6;
                final float n11 = n7;
                final float n12 = n8;
                final float n13 = n - n9;
                final float n14 = n2 - n10;
                final float n15 = n11 - n9;
                final float n16 = n12 - n10;
                final float n17 = 1.0f / (n16 * x - n15 * y);
                final float n18 = (n15 * n14 - n16 * n13) * n17;
                if (n18 >= 0.0f && n18 <= length) {
                    final float n19 = (n14 * x - n13 * y) * n17;
                    if (n19 >= 0.0f && n19 <= 1.0f) {
                        return true;
                    }
                }
            }
            return this.isPointInPolyline_WindingNumber((n + n3) / 2.0f, (n2 + n4) / 2.0f, 0) != PolygonHit.Outside;
        }
        
        private boolean isClockwise() {
            if (!this.isPolygon()) {
                return false;
            }
            float n = 0.0f;
            for (int i = 0; i < this.points.size(); i += 2) {
                n += (this.points.getQuick((i + 2) % this.points.size()) - this.points.getQuick(i)) * (this.points.getQuick((i + 3) % this.points.size()) + this.points.getQuick(i + 1));
            }
            return n > 0.0;
        }
        
        public float[] getPolygonTriangles() {
            if (this.triangles != null) {
                return this.triangles;
            }
            if (!this.isPolygon()) {
                return null;
            }
            if (IsoMetaGrid.s_clipper == null) {
                IsoMetaGrid.s_clipper = new Clipper();
                IsoMetaGrid.s_clipperBuffer = ByteBuffer.allocateDirect(2048);
            }
            final Clipper s_clipper = IsoMetaGrid.s_clipper;
            final ByteBuffer s_clipperBuffer = IsoMetaGrid.s_clipperBuffer;
            s_clipperBuffer.clear();
            if (this.isClockwise()) {
                for (int i = this.points.size() - 1; i > 0; i -= 2) {
                    s_clipperBuffer.putFloat((float)this.points.getQuick(i - 1));
                    s_clipperBuffer.putFloat((float)this.points.getQuick(i));
                }
            }
            else {
                for (int j = 0; j < this.points.size(); j += 2) {
                    s_clipperBuffer.putFloat((float)this.points.getQuick(j));
                    s_clipperBuffer.putFloat((float)this.points.getQuick(j + 1));
                }
            }
            s_clipper.clear();
            s_clipper.addPath(this.points.size() / 2, s_clipperBuffer, false);
            if (s_clipper.generatePolygons() < 1) {
                return null;
            }
            s_clipperBuffer.clear();
            final int triangulate = s_clipper.triangulate(0, s_clipperBuffer);
            this.triangles = new float[triangulate * 2];
            for (int k = 0; k < triangulate; ++k) {
                this.triangles[k * 2] = s_clipperBuffer.getFloat();
                this.triangles[k * 2 + 1] = s_clipperBuffer.getFloat();
            }
            return this.triangles;
        }
        
        public float[] getPolylineOutlineTriangles() {
            if (this.triangles != null) {
                return this.triangles;
            }
            if (!this.isPolyline() || this.polylineWidth <= 0) {
                return null;
            }
            this.checkPolylineOutline();
            final float[] polylineOutlinePoints = this.polylineOutlinePoints;
            if (polylineOutlinePoints == null) {
                return null;
            }
            if (IsoMetaGrid.s_clipper == null) {
                IsoMetaGrid.s_clipper = new Clipper();
                IsoMetaGrid.s_clipperBuffer = ByteBuffer.allocateDirect(2048);
            }
            final Clipper s_clipper = IsoMetaGrid.s_clipper;
            final ByteBuffer s_clipperBuffer = IsoMetaGrid.s_clipperBuffer;
            s_clipperBuffer.clear();
            if (this.isClockwise()) {
                for (int i = polylineOutlinePoints.length - 1; i > 0; i -= 2) {
                    s_clipperBuffer.putFloat(polylineOutlinePoints[i - 1]);
                    s_clipperBuffer.putFloat(polylineOutlinePoints[i]);
                }
            }
            else {
                for (int j = 0; j < polylineOutlinePoints.length; j += 2) {
                    s_clipperBuffer.putFloat(polylineOutlinePoints[j]);
                    s_clipperBuffer.putFloat(polylineOutlinePoints[j + 1]);
                }
            }
            s_clipper.clear();
            s_clipper.addPath(polylineOutlinePoints.length / 2, s_clipperBuffer, false);
            if (s_clipper.generatePolygons() < 1) {
                return null;
            }
            s_clipperBuffer.clear();
            final int triangulate = s_clipper.triangulate(0, s_clipperBuffer);
            this.triangles = new float[triangulate * 2];
            for (int k = 0; k < triangulate; ++k) {
                this.triangles[k * 2] = s_clipperBuffer.getFloat();
                this.triangles[k * 2 + 1] = s_clipperBuffer.getFloat();
            }
            return this.triangles;
        }
        
        public void Dispose() {
            this.pickedRZStory = null;
            this.points.clear();
            this.polylineOutlinePoints = null;
            this.spawnedZombies.clear();
            this.triangles = null;
        }
        
        static {
            LIANG_BARSKY = new PolygonalMap2.LiangBarsky();
            L_lineSegmentIntersects = new Vector2();
        }
        
        private enum PolygonHit
        {
            OnEdge, 
            Inside, 
            Outside;
            
            private static /* synthetic */ PolygonHit[] $values() {
                return new PolygonHit[] { PolygonHit.OnEdge, PolygonHit.Inside, PolygonHit.Outside };
            }
            
            static {
                $VALUES = $values();
            }
        }
    }
    
    public static final class VehicleZone extends Zone
    {
        public static final short VZF_FaceDirection = 1;
        public IsoDirections dir;
        public short flags;
        
        public VehicleZone(final String s, final String s2, final int n, final int n2, final int n3, final int n4, final int n5, final KahluaTable kahluaTable) {
            super(s, s2, n, n2, n3, n4, n5);
            this.dir = IsoDirections.Max;
            this.flags = 0;
            if (kahluaTable != null) {
                final Object rawget = kahluaTable.rawget((Object)"Direction");
                if (rawget instanceof String) {
                    this.dir = IsoDirections.valueOf((String)rawget);
                }
                if (kahluaTable.rawget((Object)"FaceDirection") == Boolean.TRUE) {
                    this.flags |= 0x1;
                }
            }
        }
        
        public boolean isFaceDirection() {
            return (this.flags & 0x1) != 0x0;
        }
    }
    
    public static final class Trigger
    {
        public BuildingDef def;
        public int triggerRange;
        public int zombieExclusionRange;
        public String type;
        public boolean triggered;
        public KahluaTable data;
        
        public Trigger(final BuildingDef def, final int triggerRange, final int zombieExclusionRange, final String type) {
            this.triggered = false;
            this.def = def;
            this.triggerRange = triggerRange;
            this.zombieExclusionRange = zombieExclusionRange;
            this.type = type;
            this.data = LuaManager.platform.newTable();
        }
        
        public KahluaTable getModData() {
            return this.data;
        }
    }
    
    private final class MetaGridLoaderThread extends Thread
    {
        final SharedStrings sharedStrings;
        final ArrayList<BuildingDef> Buildings;
        final ArrayList<RoomDef> tempRooms;
        int wY;
        
        MetaGridLoaderThread(final int wy) {
            this.sharedStrings = new SharedStrings();
            this.Buildings = new ArrayList<BuildingDef>();
            this.tempRooms = new ArrayList<RoomDef>();
            this.wY = wy;
        }
        
        @Override
        public void run() {
            try {
                this.runInner();
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
        }
        
        void runInner() {
            for (int i = this.wY; i <= IsoMetaGrid.this.maxY; i += 8) {
                for (int j = IsoMetaGrid.this.minX; j <= IsoMetaGrid.this.maxX; ++j) {
                    this.loadCell(j, i);
                }
            }
        }
        
        void loadCell(final int n, final int n2) {
            final IsoMetaCell isoMetaCell = new IsoMetaCell(n, n2);
            IsoMetaGrid.this.Grid[n - IsoMetaGrid.this.minX][n2 - IsoMetaGrid.this.minY] = isoMetaCell;
            final String key = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n, n2);
            if (!IsoLot.InfoFileNames.containsKey(key)) {
                return;
            }
            final LotHeader info = IsoLot.InfoHeaders.get(key);
            if (info == null) {
                return;
            }
            final File file = new File(IsoLot.InfoFileNames.get(key));
            if (!file.exists()) {
                return;
            }
            isoMetaCell.info = info;
            try {
                final BufferedRandomAccessFile bufferedRandomAccessFile = new BufferedRandomAccessFile(file.getAbsolutePath(), "r", 4096);
                try {
                    info.version = IsoLot.readInt(bufferedRandomAccessFile);
                    for (int int1 = IsoLot.readInt(bufferedRandomAccessFile), i = 0; i < int1; ++i) {
                        info.tilesUsed.add(this.sharedStrings.get(IsoLot.readString(bufferedRandomAccessFile).trim()));
                    }
                    bufferedRandomAccessFile.read();
                    info.width = IsoLot.readInt(bufferedRandomAccessFile);
                    info.height = IsoLot.readInt(bufferedRandomAccessFile);
                    info.levels = IsoLot.readInt(bufferedRandomAccessFile);
                    for (int int2 = IsoLot.readInt(bufferedRandomAccessFile), j = 0; j < int2; ++j) {
                        final RoomDef roomDef = new RoomDef(j, this.sharedStrings.get(IsoLot.readString(bufferedRandomAccessFile)));
                        roomDef.level = IsoLot.readInt(bufferedRandomAccessFile);
                        for (int int3 = IsoLot.readInt(bufferedRandomAccessFile), k = 0; k < int3; ++k) {
                            roomDef.rects.add(new RoomDef.RoomRect(IsoLot.readInt(bufferedRandomAccessFile) + n * 300, IsoLot.readInt(bufferedRandomAccessFile) + n2 * 300, IsoLot.readInt(bufferedRandomAccessFile), IsoLot.readInt(bufferedRandomAccessFile)));
                        }
                        roomDef.CalculateBounds();
                        info.Rooms.put(roomDef.ID, roomDef);
                        info.RoomList.add(roomDef);
                        isoMetaCell.addRoom(roomDef, n * 300, n2 * 300);
                        for (int int4 = IsoLot.readInt(bufferedRandomAccessFile), l = 0; l < int4; ++l) {
                            roomDef.objects.add(new MetaObject(IsoLot.readInt(bufferedRandomAccessFile), IsoLot.readInt(bufferedRandomAccessFile) + n * 300 - roomDef.x, IsoLot.readInt(bufferedRandomAccessFile) + n2 * 300 - roomDef.y, roomDef));
                        }
                        roomDef.bLightsActive = (Rand.Next(2) == 0);
                    }
                    for (int int5 = IsoLot.readInt(bufferedRandomAccessFile), id = 0; id < int5; ++id) {
                        final BuildingDef e = new BuildingDef();
                        final int int6 = IsoLot.readInt(bufferedRandomAccessFile);
                        e.ID = id;
                        for (int n3 = 0; n3 < int6; ++n3) {
                            final RoomDef roomDef2 = info.Rooms.get(IsoLot.readInt(bufferedRandomAccessFile));
                            roomDef2.building = e;
                            if (roomDef2.isEmptyOutside()) {
                                e.emptyoutside.add(roomDef2);
                            }
                            else {
                                e.rooms.add(roomDef2);
                            }
                        }
                        e.CalculateBounds(this.tempRooms);
                        info.Buildings.add(e);
                        this.Buildings.add(e);
                    }
                    for (int n4 = 0; n4 < 30; ++n4) {
                        for (int n5 = 0; n5 < 30; ++n5) {
                            isoMetaCell.getChunk(n4, n5).setZombieIntensity(bufferedRandomAccessFile.read());
                        }
                    }
                    bufferedRandomAccessFile.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedRandomAccessFile.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
            }
            catch (Exception ex) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath()));
                ExceptionLogger.logException(ex);
            }
        }
        
        void postLoad() {
            IsoMetaGrid.this.Buildings.addAll(this.Buildings);
            this.Buildings.clear();
            this.sharedStrings.clear();
            this.tempRooms.clear();
        }
    }
}
