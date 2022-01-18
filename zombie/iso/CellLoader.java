// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.Iterator;
import java.io.IOException;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.iso.sprite.IsoSpriteManager;
import java.io.File;
import zombie.core.properties.PropertyContainer;
import zombie.core.Rand;
import zombie.iso.objects.IsoWheelieBin;
import zombie.core.Core;
import zombie.network.GameServer;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.debug.DebugLog;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.sprite.IsoSpriteInstance;
import java.util.ArrayList;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoJukebox;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoClothingWasher;
import zombie.iso.objects.IsoClothingDryer;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoLightSwitch;
import zombie.network.GameClient;
import zombie.iso.objects.IsoDoor;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.sprite.IsoSprite;
import java.util.HashSet;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoTree;
import java.util.ArrayDeque;

public final class CellLoader
{
    public static final ArrayDeque<IsoObject> isoObjectCache;
    public static final ArrayDeque<IsoTree> isoTreeCache;
    static int wanderX;
    static int wanderY;
    static IsoRoom wanderRoom;
    static final HashSet<String> missingTiles;
    
    public static void DoTileObjectCreation(final IsoSprite isoSprite, final IsoObjectType isoObjectType, final IsoGridSquare square, final IsoCell isoCell, final int n, final int n2, final int n3, final String tile) throws NumberFormatException {
        IsoObject isoObject = null;
        if (square == null) {
            return;
        }
        final PropertyContainer properties = isoSprite.getProperties();
        if (isoSprite.solidfloor && properties.Is(IsoFlagType.diamondFloor) && !properties.Is(IsoFlagType.transparentFloor)) {
            final IsoObject floor = square.getFloor();
            if (floor != null && floor.getProperties().Is(IsoFlagType.diamondFloor)) {
                floor.clearAttachedAnimSprite();
                floor.setSprite(isoSprite);
                return;
            }
        }
        if (isoObjectType == IsoObjectType.doorW || isoObjectType == IsoObjectType.doorN) {
            isoObject = new IsoDoor(isoCell, square, isoSprite, isoObjectType == IsoObjectType.doorN);
            AddSpecialObject(square, isoObject);
            if (isoSprite.getProperties().Is(IsoFlagType.SpearOnlyAttackThrough)) {
                square.getProperties().Set(IsoFlagType.SpearOnlyAttackThrough);
            }
            if (isoSprite.getProperties().Is("GarageDoor")) {
                if (IsoDoor.getGarageDoorIndex(isoObject) > 3) {
                    ((IsoDoor)isoObject).open = true;
                    ((IsoDoor)isoObject).Locked = false;
                    ((IsoDoor)isoObject).lockedByKey = false;
                }
                else {
                    ((IsoDoor)isoObject).open = false;
                    ((IsoDoor)isoObject).Locked = true;
                    ((IsoDoor)isoObject).lockedByKey = false;
                }
            }
            GameClient.instance.objectSyncReq.putRequest(square, isoObject);
        }
        else if (isoObjectType == IsoObjectType.lightswitch) {
            isoObject = new IsoLightSwitch(isoCell, square, isoSprite, square.getRoomID());
            AddObject(square, isoObject);
            GameClient.instance.objectSyncReq.putRequest(square, isoObject);
            if (isoObject.sprite.getProperties().Is("lightR")) {
                final float n4 = Float.parseFloat(isoObject.sprite.getProperties().Val("lightR")) / 255.0f;
                final float n5 = Float.parseFloat(isoObject.sprite.getProperties().Val("lightG")) / 255.0f;
                final float n6 = Float.parseFloat(isoObject.sprite.getProperties().Val("lightB")) / 255.0f;
                int int1 = 10;
                if (isoObject.sprite.getProperties().Is("LightRadius") && Integer.parseInt(isoObject.sprite.getProperties().Val("LightRadius")) > 0) {
                    int1 = Integer.parseInt(isoObject.sprite.getProperties().Val("LightRadius"));
                }
                final IsoLightSource e = new IsoLightSource(isoObject.square.getX(), isoObject.square.getY(), isoObject.square.getZ(), n4, n5, n6, int1);
                e.bActive = true;
                e.bHydroPowered = true;
                e.switches.add((IsoLightSwitch)isoObject);
                ((IsoLightSwitch)isoObject).lights.add(e);
            }
            else {
                ((IsoLightSwitch)isoObject).lightRoom = true;
            }
        }
        else if (isoObjectType == IsoObjectType.curtainN || isoObjectType == IsoObjectType.curtainS || isoObjectType == IsoObjectType.curtainE || isoObjectType == IsoObjectType.curtainW) {
            isoObject = new IsoCurtain(isoCell, square, isoSprite, isoObjectType == IsoObjectType.curtainN || isoObjectType == IsoObjectType.curtainS, Integer.parseInt(tile.substring(tile.lastIndexOf("_") + 1)) % 8 <= 3);
            AddSpecialObject(square, isoObject);
            GameClient.instance.objectSyncReq.putRequest(square, isoObject);
        }
        else if (isoSprite.getProperties().Is(IsoFlagType.windowW) || isoSprite.getProperties().Is(IsoFlagType.windowN)) {
            isoObject = new IsoWindow(isoCell, square, isoSprite, isoSprite.getProperties().Is(IsoFlagType.windowN));
            AddSpecialObject(square, isoObject);
            GameClient.instance.objectSyncReq.putRequest(square, isoObject);
        }
        else if (isoSprite.getProperties().Is(IsoFlagType.container) && isoSprite.getProperties().Val("container").equals("barbecue")) {
            isoObject = new IsoBarbecue(isoCell, square, isoSprite);
            AddObject(square, isoObject);
        }
        else if (isoSprite.getProperties().Is(IsoFlagType.container) && isoSprite.getProperties().Val("container").equals("fireplace")) {
            isoObject = new IsoFireplace(isoCell, square, isoSprite);
            AddObject(square, isoObject);
        }
        else if (isoSprite.getProperties().Is(IsoFlagType.container) && isoSprite.getProperties().Val("container").equals("clothingdryer")) {
            isoObject = new IsoClothingDryer(isoCell, square, isoSprite);
            AddObject(square, isoObject);
        }
        else if (isoSprite.getProperties().Is(IsoFlagType.container) && isoSprite.getProperties().Val("container").equals("clothingwasher")) {
            isoObject = new IsoClothingWasher(isoCell, square, isoSprite);
            AddObject(square, isoObject);
        }
        else if (isoSprite.getProperties().Is(IsoFlagType.container) && isoSprite.getProperties().Val("container").equals("woodstove")) {
            isoObject = new IsoFireplace(isoCell, square, isoSprite);
            AddObject(square, isoObject);
        }
        else if (isoSprite.getProperties().Is(IsoFlagType.container) && (isoSprite.getProperties().Val("container").equals("stove") || isoSprite.getProperties().Val("container").equals("microwave"))) {
            isoObject = new IsoStove(isoCell, square, isoSprite);
            AddObject(square, isoObject);
            GameClient.instance.objectSyncReq.putRequest(square, isoObject);
        }
        else if (isoObjectType == IsoObjectType.jukebox) {
            isoObject = new IsoJukebox(isoCell, square, isoSprite);
            isoObject.OutlineOnMouseover = true;
            AddObject(square, isoObject);
        }
        else if (isoObjectType == IsoObjectType.radio) {
            isoObject = new IsoRadio(isoCell, square, isoSprite);
            AddObject(square, isoObject);
        }
        else if (isoSprite.getProperties().Is("signal")) {
            final String val = isoSprite.getProperties().Val("signal");
            if ("radio".equals(val)) {
                isoObject = new IsoRadio(isoCell, square, isoSprite);
            }
            else if ("tv".equals(val)) {
                isoObject = new IsoTelevision(isoCell, square, isoSprite);
            }
            AddObject(square, isoObject);
        }
        else {
            if (isoSprite.getProperties().Is(IsoFlagType.WallOverlay)) {
                IsoObject isoObject2 = null;
                if (isoSprite.getProperties().Is(IsoFlagType.attachedSE)) {
                    isoObject2 = square.getWallSE();
                }
                else if (isoSprite.getProperties().Is(IsoFlagType.attachedW)) {
                    isoObject2 = square.getWall(false);
                }
                else if (isoSprite.getProperties().Is(IsoFlagType.attachedN)) {
                    isoObject2 = square.getWall(true);
                }
                else {
                    for (int i = square.getObjects().size() - 1; i >= 0; --i) {
                        final IsoObject isoObject3 = square.getObjects().get(i);
                        if (isoObject3.sprite.getProperties().Is(IsoFlagType.cutW) || isoObject3.sprite.getProperties().Is(IsoFlagType.cutN)) {
                            isoObject2 = isoObject3;
                            break;
                        }
                    }
                }
                if (isoObject2 != null) {
                    if (isoObject2.AttachedAnimSprite == null) {
                        isoObject2.AttachedAnimSprite = new ArrayList<IsoSpriteInstance>(4);
                    }
                    isoObject2.AttachedAnimSprite.add(IsoSpriteInstance.get(isoSprite));
                }
                else {
                    final IsoObject new1 = IsoObject.getNew();
                    new1.sx = 0.0f;
                    new1.sprite = isoSprite;
                    AddObject(new1.square = square, new1);
                }
                return;
            }
            if (isoSprite.getProperties().Is(IsoFlagType.FloorOverlay)) {
                final IsoObject floor2 = square.getFloor();
                if (floor2 != null) {
                    if (floor2.AttachedAnimSprite == null) {
                        floor2.AttachedAnimSprite = new ArrayList<IsoSpriteInstance>(4);
                    }
                    floor2.AttachedAnimSprite.add(IsoSpriteInstance.get(isoSprite));
                }
            }
            else if (IsoMannequin.isMannequinSprite(isoSprite)) {
                isoObject = new IsoMannequin(isoCell, square, isoSprite);
                AddObject(square, isoObject);
            }
            else if (isoObjectType == IsoObjectType.tree) {
                if (isoSprite.getName() != null && isoSprite.getName().startsWith("vegetation_trees")) {
                    final IsoObject floor3 = square.getFloor();
                    if (floor3 == null || floor3.getSprite() == null || floor3.getSprite().getName() == null || !floor3.getSprite().getName().startsWith("blends_natural")) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, square.x, square.y, square.z));
                        return;
                    }
                }
                isoObject = IsoTree.getNew();
                isoObject.sprite = isoSprite;
                isoObject.square = square;
                isoObject.sx = 0.0f;
                ((IsoTree)isoObject).initTree();
                for (int j = 0; j < square.getObjects().size(); ++j) {
                    final IsoObject isoObject4 = square.getObjects().get(j);
                    if (isoObject4 instanceof IsoTree) {
                        square.getObjects().remove(j);
                        isoObject4.reset();
                        CellLoader.isoTreeCache.push((IsoTree)isoObject4);
                        break;
                    }
                }
                AddObject(square, isoObject);
            }
            else {
                if ((isoSprite.CurrentAnim.Frames.isEmpty() || isoSprite.CurrentAnim.Frames.get(0).getTexture(IsoDirections.N) == null) && !GameServer.bServer) {
                    if (!CellLoader.missingTiles.contains(tile)) {
                        if (Core.bDebug) {
                            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, tile));
                        }
                        CellLoader.missingTiles.add(tile);
                    }
                    isoSprite.LoadFramesNoDirPageSimple(Core.bDebug ? "media/ui/missing-tile-debug.png" : "media/ui/missing-tile.png");
                    if (isoSprite.CurrentAnim.Frames.isEmpty() || isoSprite.CurrentAnim.Frames.get(0).getTexture(IsoDirections.N) == null) {
                        return;
                    }
                }
                final String s = GameServer.bServer ? null : isoSprite.CurrentAnim.Frames.get(0).getTexture(IsoDirections.N).getName();
                boolean b = true;
                if (!GameServer.bServer && s.contains("TileObjectsExt") && (s.contains("_5") || s.contains("_6") || s.contains("_7") || s.contains("_8"))) {
                    isoObject = new IsoWheelieBin(isoCell, n, n2, n3);
                    if (s.contains("_5")) {
                        isoObject.dir = IsoDirections.S;
                    }
                    if (s.contains("_6")) {
                        isoObject.dir = IsoDirections.W;
                    }
                    if (s.contains("_7")) {
                        isoObject.dir = IsoDirections.N;
                    }
                    if (s.contains("_8")) {
                        isoObject.dir = IsoDirections.E;
                    }
                    b = false;
                }
                if (b) {
                    isoObject = IsoObject.getNew();
                    isoObject.sx = 0.0f;
                    isoObject.sprite = isoSprite;
                    AddObject(isoObject.square = square, isoObject);
                    if (isoObject.sprite.getProperties().Is("lightR")) {
                        isoCell.getLamppostPositions().add(new IsoLightSource(isoObject.square.getX(), isoObject.square.getY(), isoObject.square.getZ(), Float.parseFloat(isoObject.sprite.getProperties().Val("lightR")), Float.parseFloat(isoObject.sprite.getProperties().Val("lightG")), Float.parseFloat(isoObject.sprite.getProperties().Val("lightB")), 8));
                    }
                }
            }
        }
        if (isoObject != null) {
            isoObject.tile = tile;
            isoObject.createContainersFromSpriteProperties();
            if (isoObject.sprite.getProperties().Is(IsoFlagType.vegitation)) {
                isoObject.tintr = 0.7f + Rand.Next(30) / 100.0f;
                isoObject.tintg = 0.7f + Rand.Next(30) / 100.0f;
                isoObject.tintb = 0.7f + Rand.Next(30) / 100.0f;
            }
        }
    }
    
    public static boolean LoadCellBinaryChunk(final IsoCell isoCell, final int i, final int j, final IsoChunk isoChunk) {
        final String s = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, i / 30, j / 30);
        if (!IsoLot.InfoFileNames.containsKey(s)) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            return false;
        }
        if (new File(IsoLot.InfoFileNames.get(s)).exists()) {
            IsoLot value = null;
            try {
                value = IsoLot.get(i / 30, j / 30, i, j, isoChunk);
                isoCell.PlaceLot(value, 0, 0, 0, isoChunk, i, j);
            }
            finally {
                if (value != null) {
                    IsoLot.put(value);
                }
            }
            return true;
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        return false;
    }
    
    public static IsoCell LoadCellBinaryChunk(final IsoSpriteManager isoSpriteManager, final int i, final int j) throws IOException {
        CellLoader.wanderX = 0;
        CellLoader.wanderY = 0;
        CellLoader.wanderRoom = null;
        CellLoader.wanderX = 0;
        CellLoader.wanderY = 0;
        final IsoCell isoCell = new IsoCell(300, 300);
        final int numPlayers = IsoPlayer.numPlayers;
        final int n = 1;
        if (!GameServer.bServer) {
            if (GameClient.bClient) {
                WorldStreamer.instance.requestLargeAreaZip(i, j, IsoChunkMap.ChunkGridWidth / 2 + 2);
                IsoChunk.bDoServerRequests = false;
            }
            for (int k = 0; k < n; ++k) {
                isoCell.ChunkMap[k].setInitialPos(i, j);
                IsoPlayer.assumedPlayer = k;
                final IsoChunkMap isoChunkMap = isoCell.ChunkMap[k];
                final int n2 = i - IsoChunkMap.ChunkGridWidth / 2;
                final IsoChunkMap isoChunkMap2 = isoCell.ChunkMap[k];
                final int n3 = j - IsoChunkMap.ChunkGridWidth / 2;
                final IsoChunkMap isoChunkMap3 = isoCell.ChunkMap[k];
                final int n4 = i + IsoChunkMap.ChunkGridWidth / 2 + 1;
                final IsoChunkMap isoChunkMap4 = isoCell.ChunkMap[k];
                final int n5 = j + IsoChunkMap.ChunkGridWidth / 2 + 1;
                for (int l = n2; l < n4; ++l) {
                    for (int n6 = n3; n6 < n5; ++n6) {
                        if (IsoWorld.instance.getMetaGrid().isValidChunk(l, n6)) {
                            isoCell.ChunkMap[k].LoadChunk(l, n6, l - n2, n6 - n3);
                        }
                    }
                }
            }
        }
        IsoPlayer.assumedPlayer = 0;
        LuaEventManager.triggerEvent("OnPostMapLoad", isoCell, i, j);
        ConnectMultitileObjects(isoCell);
        return isoCell;
    }
    
    private static void RecurseMultitileObjects(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2, final ArrayList<IsoPushableObject> list) {
        final Iterator<IsoMovingObject> iterator = isoGridSquare2.getMovingObjects().iterator();
        IsoPushableObject isoPushableObject = null;
        boolean b = false;
        while (iterator != null && iterator.hasNext()) {
            final IsoMovingObject isoMovingObject = iterator.next();
            if (!(isoMovingObject instanceof IsoPushableObject)) {
                continue;
            }
            final IsoPushableObject isoPushableObject2 = (IsoPushableObject)isoMovingObject;
            final int n = isoGridSquare.getX() - isoGridSquare2.getX();
            final int n2 = isoGridSquare.getY() - isoGridSquare2.getY();
            if (n2 != 0 && isoMovingObject.sprite.getProperties().Is("connectY") && Integer.parseInt(isoMovingObject.sprite.getProperties().Val("connectY")) == n2) {
                (isoPushableObject2.connectList = list).add(isoPushableObject2);
                isoPushableObject = isoPushableObject2;
                b = false;
                break;
            }
            if (n != 0 && isoMovingObject.sprite.getProperties().Is("connectX") && Integer.parseInt(isoMovingObject.sprite.getProperties().Val("connectX")) == n) {
                (isoPushableObject2.connectList = list).add(isoPushableObject2);
                isoPushableObject = isoPushableObject2;
                b = true;
                break;
            }
        }
        if (isoPushableObject != null) {
            if (isoPushableObject.sprite.getProperties().Is("connectY") && b) {
                RecurseMultitileObjects(isoCell, isoPushableObject.getCurrentSquare(), isoCell.getGridSquare(isoPushableObject.getCurrentSquare().getX(), isoPushableObject.getCurrentSquare().getY() + Integer.parseInt(isoPushableObject.sprite.getProperties().Val("connectY")), isoPushableObject.getCurrentSquare().getZ()), isoPushableObject.connectList);
            }
            if (isoPushableObject.sprite.getProperties().Is("connectX") && !b) {
                RecurseMultitileObjects(isoCell, isoPushableObject.getCurrentSquare(), isoCell.getGridSquare(isoPushableObject.getCurrentSquare().getX() + Integer.parseInt(isoPushableObject.sprite.getProperties().Val("connectX")), isoPushableObject.getCurrentSquare().getY(), isoPushableObject.getCurrentSquare().getZ()), isoPushableObject.connectList);
            }
        }
    }
    
    private static void ConnectMultitileObjects(final IsoCell isoCell) {
        final Iterator<IsoMovingObject> iterator = isoCell.getObjectList().iterator();
        while (iterator != null && iterator.hasNext()) {
            final IsoMovingObject isoMovingObject = iterator.next();
            if (!(isoMovingObject instanceof IsoPushableObject)) {
                continue;
            }
            final IsoPushableObject e = (IsoPushableObject)isoMovingObject;
            if ((!isoMovingObject.sprite.getProperties().Is("connectY") && !isoMovingObject.sprite.getProperties().Is("connectX")) || e.connectList != null) {
                continue;
            }
            (e.connectList = new ArrayList<IsoPushableObject>()).add(e);
            if (isoMovingObject.sprite.getProperties().Is("connectY")) {
                final IsoGridSquare gridSquare = isoCell.getGridSquare(isoMovingObject.getCurrentSquare().getX(), isoMovingObject.getCurrentSquare().getY() + Integer.parseInt(isoMovingObject.sprite.getProperties().Val("connectY")), isoMovingObject.getCurrentSquare().getZ());
                if (gridSquare == null) {}
                RecurseMultitileObjects(isoCell, e.getCurrentSquare(), gridSquare, e.connectList);
            }
            if (!isoMovingObject.sprite.getProperties().Is("connectX")) {
                continue;
            }
            RecurseMultitileObjects(isoCell, e.getCurrentSquare(), isoCell.getGridSquare(isoMovingObject.getCurrentSquare().getX() + Integer.parseInt(isoMovingObject.sprite.getProperties().Val("connectX")), isoMovingObject.getCurrentSquare().getY(), isoMovingObject.getCurrentSquare().getZ()), e.connectList);
        }
    }
    
    private static void AddObject(final IsoGridSquare isoGridSquare, final IsoObject isoObject) {
        final int placeWallAndDoorCheck = isoGridSquare.placeWallAndDoorCheck(isoObject, isoGridSquare.getObjects().size());
        if (placeWallAndDoorCheck != isoGridSquare.getObjects().size() && placeWallAndDoorCheck >= 0 && placeWallAndDoorCheck <= isoGridSquare.getObjects().size()) {
            isoGridSquare.getObjects().add(placeWallAndDoorCheck, isoObject);
        }
        else {
            isoGridSquare.getObjects().add(isoObject);
        }
    }
    
    private static void AddSpecialObject(final IsoGridSquare isoGridSquare, final IsoObject e) {
        final int placeWallAndDoorCheck = isoGridSquare.placeWallAndDoorCheck(e, isoGridSquare.getObjects().size());
        if (placeWallAndDoorCheck != isoGridSquare.getObjects().size() && placeWallAndDoorCheck >= 0 && placeWallAndDoorCheck <= isoGridSquare.getObjects().size()) {
            isoGridSquare.getObjects().add(placeWallAndDoorCheck, e);
        }
        else {
            isoGridSquare.getObjects().add(e);
            isoGridSquare.getSpecialObjects().add(e);
        }
    }
    
    static {
        isoObjectCache = new ArrayDeque<IsoObject>();
        isoTreeCache = new ArrayDeque<IsoTree>();
        CellLoader.wanderX = 0;
        CellLoader.wanderY = 0;
        CellLoader.wanderRoom = null;
        missingTiles = new HashSet<String>();
    }
}
