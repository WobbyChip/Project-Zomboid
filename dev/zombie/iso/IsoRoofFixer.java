// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.HashMap;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.SpriteDetails.IsoFlagType;
import java.util.ArrayList;
import zombie.core.properties.PropertyContainer;
import zombie.iso.areas.IsoRoom;
import java.util.Map;
import zombie.iso.sprite.IsoSprite;

public final class IsoRoofFixer
{
    private static final boolean PER_ROOM_MODE = true;
    private static final int MAX_Z = 8;
    private static final int SCAN_RANGE = 3;
    private static final boolean ALWAYS_INVIS_FLOORS = false;
    private static boolean roofTileGlassCacheDirty;
    private static boolean roofTileIsGlass;
    private static IsoSprite roofTileCache;
    private static int roofTilePlaceFloorIndexCache;
    private static String invisFloor;
    private static Map<Integer, String> roofGroups;
    private static PlaceFloorInfo[] placeFloorInfos;
    private static int floorInfoIndex;
    private static IsoGridSquare[] sqCache;
    private static IsoRoom workingRoom;
    private static int[] interiorAirSpaces;
    private static final int I_UNCHECKED = 0;
    private static final int I_TRUE = 1;
    private static final int I_FALSE = 2;
    
    private static void ensureCapacityFloorInfos() {
        if (IsoRoofFixer.floorInfoIndex == IsoRoofFixer.placeFloorInfos.length) {
            final PlaceFloorInfo[] placeFloorInfos = IsoRoofFixer.placeFloorInfos;
            System.arraycopy(placeFloorInfos, 0, IsoRoofFixer.placeFloorInfos = new PlaceFloorInfo[IsoRoofFixer.placeFloorInfos.length + 400], 0, placeFloorInfos.length);
        }
    }
    
    private static void setRoofTileCache(final IsoObject isoObject) {
        final IsoSprite roofTileCache = (isoObject != null) ? isoObject.sprite : null;
        if (IsoRoofFixer.roofTileCache != roofTileCache) {
            IsoRoofFixer.roofTileCache = roofTileCache;
            IsoRoofFixer.roofTilePlaceFloorIndexCache = 0;
            if (roofTileCache != null && roofTileCache.getProperties() != null && roofTileCache.getProperties().Val("RoofGroup") != null) {
                try {
                    final int int1 = Integer.parseInt(roofTileCache.getProperties().Val("RoofGroup"));
                    if (IsoRoofFixer.roofGroups.containsKey(int1)) {
                        IsoRoofFixer.roofTilePlaceFloorIndexCache = int1;
                    }
                }
                catch (Exception ex) {}
            }
            IsoRoofFixer.roofTileGlassCacheDirty = true;
        }
    }
    
    private static boolean isRoofTileCacheGlass() {
        if (IsoRoofFixer.roofTileGlassCacheDirty) {
            IsoRoofFixer.roofTileIsGlass = false;
            if (IsoRoofFixer.roofTileCache != null) {
                final PropertyContainer properties = IsoRoofFixer.roofTileCache.getProperties();
                if (properties != null) {
                    final String val = properties.Val("Material");
                    IsoRoofFixer.roofTileIsGlass = (val != null && val.equalsIgnoreCase("glass"));
                }
            }
            IsoRoofFixer.roofTileGlassCacheDirty = false;
        }
        return IsoRoofFixer.roofTileIsGlass;
    }
    
    public static void FixRoofsAt(final IsoGridSquare isoGridSquare) {
        try {
            FixRoofsPerRoomAt(isoGridSquare);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static void FixRoofsPerRoomAt(final IsoGridSquare isoGridSquare) {
        IsoRoofFixer.floorInfoIndex = 0;
        if (isoGridSquare.getZ() > 0 && !isoGridSquare.TreatAsSolidFloor() && isoGridSquare.getRoom() == null) {
            final IsoRoom roomBelow = getRoomBelow(isoGridSquare);
            if (roomBelow != null && !roomBelow.def.isRoofFixed()) {
                resetInteriorSpaceCache();
                IsoRoofFixer.workingRoom = roomBelow;
                final ArrayList<IsoGridSquare> squares = roomBelow.getSquares();
                for (int i = 0; i < squares.size(); ++i) {
                    final IsoGridSquare roofFloorForColumn = getRoofFloorForColumn(squares.get(i));
                    if (roofFloorForColumn != null) {
                        ensureCapacityFloorInfos();
                        IsoRoofFixer.placeFloorInfos[IsoRoofFixer.floorInfoIndex++].set(roofFloorForColumn, IsoRoofFixer.roofTilePlaceFloorIndexCache);
                    }
                }
                roomBelow.def.setRoofFixed(true);
            }
        }
        for (int j = 0; j < IsoRoofFixer.floorInfoIndex; ++j) {
            IsoRoofFixer.placeFloorInfos[j].square.addFloor(IsoRoofFixer.roofGroups.get(IsoRoofFixer.placeFloorInfos[j].floorType));
        }
    }
    
    private static void clearSqCache() {
        for (int i = 0; i < IsoRoofFixer.sqCache.length; ++i) {
            IsoRoofFixer.sqCache[i] = null;
        }
    }
    
    private static IsoGridSquare getRoofFloorForColumn(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null) {
            return null;
        }
        final IsoCell instance = IsoCell.getInstance();
        int n = 0;
        int n2 = 0;
        for (int i = 7; i >= isoGridSquare.getZ() + 1; --i) {
            final IsoGridSquare gridSquare = instance.getGridSquare(isoGridSquare.x, isoGridSquare.y, i);
            if (gridSquare == null) {
                if (i == isoGridSquare.getZ() + 1 && i > 0 && !isStairsBelow(isoGridSquare.x, isoGridSquare.y, i)) {
                    final IsoGridSquare new1 = IsoGridSquare.getNew(instance, null, isoGridSquare.x, isoGridSquare.y, i);
                    instance.ConnectNewSquare(new1, false);
                    new1.EnsureSurroundNotNull();
                    new1.RecalcAllWithNeighbours(true);
                    IsoRoofFixer.sqCache[n++] = new1;
                }
                n2 = 1;
            }
            else if (gridSquare.TreatAsSolidFloor()) {
                if (gridSquare.getRoom() == null) {
                    final IsoObject floor = gridSquare.getFloor();
                    if (floor != null && isObjectRoof(floor) && floor.getProperties() != null) {
                        final PropertyContainer properties = floor.getProperties();
                        if (!properties.Is(IsoFlagType.FloorHeightOneThird) && !properties.Is(IsoFlagType.FloorHeightTwoThirds)) {
                            final IsoGridSquare gridSquare2 = instance.getGridSquare(isoGridSquare.x, isoGridSquare.y, i - 1);
                            if (gridSquare2 != null && gridSquare2.getRoom() == null) {
                                n2 = 0;
                                continue;
                            }
                        }
                    }
                    return null;
                }
                if (n2 != 0) {
                    final IsoGridSquare new2 = IsoGridSquare.getNew(instance, null, isoGridSquare.x, isoGridSquare.y, i + 1);
                    instance.ConnectNewSquare(new2, false);
                    new2.EnsureSurroundNotNull();
                    new2.RecalcAllWithNeighbours(true);
                    IsoRoofFixer.sqCache[n++] = new2;
                    break;
                }
                break;
            }
            else {
                if (gridSquare.HasStairsBelow()) {
                    break;
                }
                n2 = 0;
                IsoRoofFixer.sqCache[n++] = gridSquare;
            }
        }
        if (n == 0) {
            return null;
        }
        for (int j = 0; j < n; ++j) {
            final IsoGridSquare isoGridSquare2 = IsoRoofFixer.sqCache[j];
            if (isoGridSquare2.getRoom() == null && isInteriorAirSpace(isoGridSquare2.getX(), isoGridSquare2.getY(), isoGridSquare2.getZ())) {
                return null;
            }
            if (isRoofAt(isoGridSquare2, true)) {
                return isoGridSquare2;
            }
            for (int k = isoGridSquare2.x - 3; k <= isoGridSquare2.x + 3; ++k) {
                for (int l = isoGridSquare2.y - 3; l <= isoGridSquare2.y + 3; ++l) {
                    if (k != isoGridSquare2.x || l != isoGridSquare2.y) {
                        final IsoGridSquare gridSquare3 = instance.getGridSquare(k, l, isoGridSquare2.z);
                        if (gridSquare3 != null) {
                            for (int n3 = 0; n3 < gridSquare3.getObjects().size(); ++n3) {
                                final IsoObject roofTileCache = gridSquare3.getObjects().get(n3);
                                if (isObjectRoofNonFlat(roofTileCache)) {
                                    setRoofTileCache(roofTileCache);
                                    return isoGridSquare2;
                                }
                            }
                            final IsoGridSquare gridSquare4 = instance.getGridSquare(gridSquare3.x, gridSquare3.y, gridSquare3.z + 1);
                            if (gridSquare4 != null && gridSquare4.getObjects().size() > 0) {
                                for (int n4 = 0; n4 < gridSquare4.getObjects().size(); ++n4) {
                                    final IsoObject roofTileCache2 = gridSquare4.getObjects().get(n4);
                                    if (isObjectRoofFlatFloor(roofTileCache2)) {
                                        setRoofTileCache(roofTileCache2);
                                        return isoGridSquare2;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private static void FixRoofsPerTileAt(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare.getZ() > 0 && !isoGridSquare.TreatAsSolidFloor() && isoGridSquare.getRoom() == null && hasRoomBelow(isoGridSquare) && (isRoofAt(isoGridSquare, true) || scanIsRoofAt(isoGridSquare, true))) {
            if (isRoofTileCacheGlass()) {
                isoGridSquare.addFloor(IsoRoofFixer.invisFloor);
            }
            else {
                isoGridSquare.addFloor("carpentry_02_58");
            }
        }
    }
    
    private static boolean scanIsRoofAt(final IsoGridSquare isoGridSquare, final boolean b) {
        if (isoGridSquare == null) {
            return false;
        }
        for (int i = isoGridSquare.x - 3; i <= isoGridSquare.x + 3; ++i) {
            for (int j = isoGridSquare.y - 3; j <= isoGridSquare.y + 3; ++j) {
                if (i != isoGridSquare.x || j != isoGridSquare.y) {
                    final IsoGridSquare gridSquare = isoGridSquare.getCell().getGridSquare(i, j, isoGridSquare.z);
                    if (gridSquare != null) {
                        if (isRoofAt(gridSquare, b)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private static boolean isRoofAt(final IsoGridSquare isoGridSquare, final boolean b) {
        if (isoGridSquare == null) {
            return false;
        }
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject roofTileCache = isoGridSquare.getObjects().get(i);
            if (isObjectRoofNonFlat(roofTileCache)) {
                setRoofTileCache(roofTileCache);
                return true;
            }
        }
        if (b) {
            final IsoGridSquare gridSquare = isoGridSquare.getCell().getGridSquare(isoGridSquare.x, isoGridSquare.y, isoGridSquare.z + 1);
            if (gridSquare != null && gridSquare.getObjects().size() > 0) {
                for (int j = 0; j < gridSquare.getObjects().size(); ++j) {
                    final IsoObject roofTileCache2 = gridSquare.getObjects().get(j);
                    if (isObjectRoofFlatFloor(roofTileCache2)) {
                        setRoofTileCache(roofTileCache2);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private static boolean isObjectRoof(final IsoObject isoObject) {
        return isoObject != null && (isoObject.getType() == IsoObjectType.WestRoofT || isoObject.getType() == IsoObjectType.WestRoofB || isoObject.getType() == IsoObjectType.WestRoofM);
    }
    
    private static boolean isObjectRoofNonFlat(final IsoObject isoObject) {
        if (isObjectRoof(isoObject)) {
            final PropertyContainer properties = isoObject.getProperties();
            if (properties != null) {
                return !properties.Is(IsoFlagType.solidfloor) || properties.Is(IsoFlagType.FloorHeightOneThird) || properties.Is(IsoFlagType.FloorHeightTwoThirds);
            }
        }
        return false;
    }
    
    private static boolean isObjectRoofFlatFloor(final IsoObject isoObject) {
        if (isObjectRoof(isoObject)) {
            final PropertyContainer properties = isoObject.getProperties();
            if (properties != null && properties.Is(IsoFlagType.solidfloor)) {
                return !properties.Is(IsoFlagType.FloorHeightOneThird) && !properties.Is(IsoFlagType.FloorHeightTwoThirds);
            }
        }
        return false;
    }
    
    private static boolean hasRoomBelow(final IsoGridSquare isoGridSquare) {
        return getRoomBelow(isoGridSquare) != null;
    }
    
    private static IsoRoom getRoomBelow(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null) {
            return null;
        }
        for (int i = isoGridSquare.z - 1; i >= 0; --i) {
            final IsoGridSquare gridSquare = isoGridSquare.getCell().getGridSquare(isoGridSquare.x, isoGridSquare.y, i);
            if (gridSquare != null) {
                if (gridSquare.TreatAsSolidFloor() && gridSquare.getRoom() == null) {
                    return null;
                }
                if (gridSquare.getRoom() != null) {
                    return gridSquare.getRoom();
                }
            }
        }
        return null;
    }
    
    private static boolean isStairsBelow(final int n, final int n2, final int n3) {
        if (n3 == 0) {
            return false;
        }
        final IsoGridSquare gridSquare = IsoCell.getInstance().getGridSquare(n, n2, n3 - 1);
        return gridSquare != null && gridSquare.HasStairs();
    }
    
    private static void resetInteriorSpaceCache() {
        for (int i = 0; i < IsoRoofFixer.interiorAirSpaces.length; ++i) {
            IsoRoofFixer.interiorAirSpaces[i] = 0;
        }
    }
    
    private static boolean isInteriorAirSpace(final int n, final int n2, final int n3) {
        if (IsoRoofFixer.interiorAirSpaces[n3] != 0) {
            return IsoRoofFixer.interiorAirSpaces[n3] == 1;
        }
        final ArrayList<IsoGridSquare> squares = IsoRoofFixer.workingRoom.getSquares();
        boolean b = false;
        if (squares.size() > 0 && n3 > squares.get(0).getZ()) {
            for (int i = 0; i < IsoRoofFixer.workingRoom.rects.size(); ++i) {
                final RoomDef.RoomRect roomRect = IsoRoofFixer.workingRoom.rects.get(i);
                for (int j = roomRect.getX(); j < roomRect.getX2(); ++j) {
                    if (hasRailing(j, roomRect.getY(), n3, IsoDirections.N) || hasRailing(j, roomRect.getY2() - 1, n3, IsoDirections.S)) {
                        b = true;
                        break;
                    }
                }
                if (b) {
                    break;
                }
                for (int k = roomRect.getY(); k < roomRect.getY2(); ++k) {
                    if (hasRailing(roomRect.getX(), k, n3, IsoDirections.W) || hasRailing(roomRect.getX2() - 1, k, n3, IsoDirections.E)) {
                        b = true;
                        break;
                    }
                }
            }
        }
        IsoRoofFixer.interiorAirSpaces[n3] = (b ? 1 : 2);
        return b;
    }
    
    private static boolean hasRailing(final int n, final int n2, final int n3, final IsoDirections isoDirections) {
        final IsoCell instance = IsoCell.getInstance();
        final IsoGridSquare gridSquare = instance.getGridSquare(n, n2, n3);
        if (gridSquare == null) {
            return false;
        }
        switch (isoDirections) {
            case N: {
                return gridSquare.isHoppableTo(instance.getGridSquare(n, n2 - 1, n3));
            }
            case E: {
                return gridSquare.isHoppableTo(instance.getGridSquare(n + 1, n2, n3));
            }
            case S: {
                return gridSquare.isHoppableTo(instance.getGridSquare(n, n2 + 1, n3));
            }
            case W: {
                return gridSquare.isHoppableTo(instance.getGridSquare(n - 1, n2, n3));
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        IsoRoofFixer.roofTileGlassCacheDirty = true;
        IsoRoofFixer.roofTileIsGlass = false;
        IsoRoofFixer.roofTilePlaceFloorIndexCache = 0;
        IsoRoofFixer.invisFloor = "invisible_01_0";
        IsoRoofFixer.roofGroups = new HashMap<Integer, String>();
        IsoRoofFixer.placeFloorInfos = new PlaceFloorInfo[10000];
        IsoRoofFixer.floorInfoIndex = 0;
        IsoRoofFixer.roofGroups.put(0, "carpentry_02_57");
        IsoRoofFixer.roofGroups.put(1, "roofs_01_22");
        IsoRoofFixer.roofGroups.put(2, "roofs_01_54");
        IsoRoofFixer.roofGroups.put(3, "roofs_02_22");
        IsoRoofFixer.roofGroups.put(4, IsoRoofFixer.invisFloor);
        IsoRoofFixer.roofGroups.put(5, "roofs_03_22");
        IsoRoofFixer.roofGroups.put(6, "roofs_03_54");
        IsoRoofFixer.roofGroups.put(7, "roofs_04_22");
        IsoRoofFixer.roofGroups.put(8, "roofs_04_54");
        IsoRoofFixer.roofGroups.put(9, "roofs_05_22");
        IsoRoofFixer.roofGroups.put(10, "roofs_05_54");
        for (int i = 0; i < IsoRoofFixer.placeFloorInfos.length; ++i) {
            IsoRoofFixer.placeFloorInfos[i] = new PlaceFloorInfo();
        }
        IsoRoofFixer.sqCache = new IsoGridSquare[8];
        IsoRoofFixer.interiorAirSpaces = new int[8];
    }
    
    private static final class PlaceFloorInfo
    {
        private IsoGridSquare square;
        private int floorType;
        
        private void set(final IsoGridSquare square, final int floorType) {
            this.square = square;
            this.floorType = floorType;
        }
    }
}
