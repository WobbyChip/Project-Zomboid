// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.meta.Meta;
import zombie.network.GameServer;
import zombie.core.textures.ColorInfo;
import java.util.Collection;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.opengl.RenderSettings;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.weather.ClimateManager;
import zombie.characters.Moodles.MoodleType;
import java.util.Stack;
import zombie.vehicles.BaseVehicle;
import zombie.core.PerformanceSettings;
import zombie.iso.objects.IsoLightSwitch;
import zombie.SandboxOptions;
import zombie.GameTime;
import zombie.iso.objects.IsoGenerator;
import zombie.network.GameClient;
import zombie.vehicles.VehicleLight;
import zombie.vehicles.VehiclePart;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.characters.IsoGameCharacter;
import java.util.ArrayList;

public final class LightingJNI
{
    public static final int ROOM_SPAWN_DIST = 50;
    public static boolean init;
    public static final int[][] ForcedVis;
    private static final ArrayList<IsoGameCharacter.TorchInfo> torches;
    private static final ArrayList<IsoGameCharacter.TorchInfo> activeTorches;
    private static final ArrayList<IsoLightSource> JNILights;
    private static final int[] updateCounter;
    private static boolean bWasElecShut;
    private static boolean bWasNight;
    private static final Vector2 tempVector2;
    private static final int MAX_PLAYERS = 256;
    private static final int MAX_LIGHTS_PER_PLAYER = 4;
    private static final int MAX_LIGHTS_PER_VEHICLE = 10;
    private static final ArrayList<InventoryItem> tempItems;
    
    public static void init() {
        if (LightingJNI.init) {
            return;
        }
        String s = "";
        if ("1".equals(System.getProperty("zomboid.debuglibs.lighting"))) {
            DebugLog.log("***** Loading debug version of Lighting");
            s = "d";
        }
        try {
            if (System.getProperty("os.name").contains("OS X")) {
                System.loadLibrary("Lighting");
            }
            else if (System.getProperty("os.name").startsWith("Win")) {
                if (System.getProperty("sun.arch.data.model").equals("64")) {
                    System.loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                }
                else {
                    System.loadLibrary(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                }
            }
            else if (System.getProperty("sun.arch.data.model").equals("64")) {
                System.loadLibrary("Lighting64");
            }
            else {
                System.loadLibrary("Lighting32");
            }
            for (int i = 0; i < 4; ++i) {
                LightingJNI.updateCounter[i] = -1;
            }
            configure(0.005f);
            LightingJNI.init = true;
        }
        catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            unsatisfiedLinkError.printStackTrace();
            try {
                Thread.sleep(3000L);
            }
            catch (InterruptedException ex) {}
            System.exit(1);
        }
    }
    
    private static int getTorchIndexById(final int n) {
        for (int i = 0; i < LightingJNI.torches.size(); ++i) {
            if (LightingJNI.torches.get(i).id == n) {
                return i;
            }
        }
        return -1;
    }
    
    private static void checkTorch(final IsoPlayer isoPlayer, final InventoryItem inventoryItem, final int id) {
        final int torchIndexById = getTorchIndexById(id);
        IsoGameCharacter.TorchInfo alloc;
        if (torchIndexById == -1) {
            alloc = IsoGameCharacter.TorchInfo.alloc();
            LightingJNI.torches.add(alloc);
        }
        else {
            alloc = LightingJNI.torches.get(torchIndexById);
        }
        alloc.set(isoPlayer, inventoryItem);
        if (alloc.id == 0) {
            alloc.id = id;
        }
        updateTorch(alloc.id, alloc.x, alloc.y, alloc.z, alloc.angleX, alloc.angleY, alloc.dist, alloc.strength, alloc.bCone, alloc.dot, alloc.focusing);
        LightingJNI.activeTorches.add(alloc);
    }
    
    private static int checkPlayerTorches(final IsoPlayer isoPlayer, final int n) {
        final ArrayList<InventoryItem> tempItems = LightingJNI.tempItems;
        tempItems.clear();
        isoPlayer.getActiveLightItems(tempItems);
        final int min = Math.min(tempItems.size(), 4);
        for (int i = 0; i < min; ++i) {
            checkTorch(isoPlayer, tempItems.get(i), n * 4 + i + 1);
        }
        return min;
    }
    
    private static void clearPlayerTorches(final int n, final int n2) {
        for (int i = n2; i < 4; ++i) {
            final int torchIndexById = getTorchIndexById(n * 4 + i + 1);
            if (torchIndexById != -1) {
                final IsoGameCharacter.TorchInfo torchInfo = LightingJNI.torches.get(torchIndexById);
                removeTorch(torchInfo.id);
                torchInfo.id = 0;
                IsoGameCharacter.TorchInfo.release(torchInfo);
                LightingJNI.torches.remove(torchIndexById);
                break;
            }
        }
    }
    
    private static void checkTorch(final VehiclePart vehiclePart, final int id) {
        final VehicleLight light = vehiclePart.getLight();
        if (light != null && light.getActive()) {
            IsoGameCharacter.TorchInfo alloc = null;
            for (int i = 0; i < LightingJNI.torches.size(); ++i) {
                alloc = LightingJNI.torches.get(i);
                if (alloc.id == id) {
                    break;
                }
                alloc = null;
            }
            if (alloc == null) {
                alloc = IsoGameCharacter.TorchInfo.alloc();
                LightingJNI.torches.add(alloc);
            }
            alloc.set(vehiclePart);
            if (alloc.id == 0) {
                alloc.id = id;
            }
            updateTorch(alloc.id, alloc.x, alloc.y, alloc.z, alloc.angleX, alloc.angleY, alloc.dist, alloc.strength, alloc.bCone, alloc.dot, alloc.focusing);
            LightingJNI.activeTorches.add(alloc);
        }
        else {
            for (int j = 0; j < LightingJNI.torches.size(); ++j) {
                final IsoGameCharacter.TorchInfo torchInfo = LightingJNI.torches.get(j);
                if (torchInfo.id == id) {
                    removeTorch(torchInfo.id);
                    torchInfo.id = 0;
                    IsoGameCharacter.TorchInfo.release(torchInfo);
                    LightingJNI.torches.remove(j--);
                }
            }
        }
    }
    
    private static void checkLights() {
        if (IsoWorld.instance.CurrentCell == null) {
            return;
        }
        if (GameClient.bClient) {
            IsoGenerator.updateSurroundingNow();
        }
        final boolean b = GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier();
        final Stack<IsoLightSource> lamppostPositions = IsoWorld.instance.CurrentCell.getLamppostPositions();
        for (int i = 0; i < lamppostPositions.size(); ++i) {
            final IsoLightSource isoLightSource = lamppostPositions.get(i);
            final IsoChunk chunkForGridSquare = IsoWorld.instance.CurrentCell.getChunkForGridSquare(isoLightSource.x, isoLightSource.y, isoLightSource.z);
            if (chunkForGridSquare != null && isoLightSource.chunk != null && isoLightSource.chunk != chunkForGridSquare) {
                isoLightSource.life = 0;
            }
            if (isoLightSource.life == 0 || !isoLightSource.isInBounds()) {
                lamppostPositions.remove(i);
                if (isoLightSource.ID != 0) {
                    final int id = isoLightSource.ID;
                    isoLightSource.ID = 0;
                    LightingJNI.JNILights.remove(isoLightSource);
                    removeLight(id);
                    GameTime.instance.lightSourceUpdate = 100.0f;
                }
                --i;
            }
            else {
                if (isoLightSource.bHydroPowered) {
                    if (isoLightSource.switches.isEmpty()) {
                        assert false;
                        boolean bActive = b;
                        if (!bActive) {
                            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(isoLightSource.x, isoLightSource.y, isoLightSource.z);
                            bActive = (gridSquare != null && gridSquare.haveElectricity());
                        }
                        if (isoLightSource.bActive != bActive) {
                            isoLightSource.bActive = bActive;
                            GameTime.instance.lightSourceUpdate = 100.0f;
                        }
                    }
                    else {
                        final IsoLightSwitch isoLightSwitch = isoLightSource.switches.get(0);
                        boolean canSwitchLight = isoLightSwitch.canSwitchLight();
                        if (isoLightSwitch.bStreetLight && GameTime.getInstance().getNight() < 0.5f) {
                            canSwitchLight = false;
                        }
                        if (isoLightSource.bActive && !canSwitchLight) {
                            isoLightSource.bActive = false;
                            GameTime.instance.lightSourceUpdate = 100.0f;
                        }
                        else if (!isoLightSource.bActive && canSwitchLight && isoLightSwitch.isActivated()) {
                            isoLightSource.bActive = true;
                            GameTime.instance.lightSourceUpdate = 100.0f;
                        }
                    }
                }
                if (isoLightSource.ID == 0) {
                    isoLightSource.ID = IsoLightSource.NextID++;
                    if (isoLightSource.life != -1) {
                        addTempLight(isoLightSource.ID, isoLightSource.x, isoLightSource.y, isoLightSource.z, isoLightSource.radius, isoLightSource.r, isoLightSource.g, isoLightSource.b, (int)(isoLightSource.life * PerformanceSettings.getLockFPS() / 30.0f));
                        lamppostPositions.remove(i--);
                    }
                    else {
                        isoLightSource.rJNI = isoLightSource.r;
                        isoLightSource.gJNI = isoLightSource.g;
                        isoLightSource.bJNI = isoLightSource.b;
                        isoLightSource.bActiveJNI = isoLightSource.bActive;
                        LightingJNI.JNILights.add(isoLightSource);
                        addLight(isoLightSource.ID, isoLightSource.x, isoLightSource.y, isoLightSource.z, isoLightSource.radius, isoLightSource.r, isoLightSource.g, isoLightSource.b, (isoLightSource.localToBuilding == null) ? -1 : isoLightSource.localToBuilding.ID, isoLightSource.bActive);
                    }
                }
                else {
                    if (isoLightSource.r != isoLightSource.rJNI || isoLightSource.g != isoLightSource.gJNI || isoLightSource.b != isoLightSource.bJNI) {
                        isoLightSource.rJNI = isoLightSource.r;
                        isoLightSource.gJNI = isoLightSource.g;
                        isoLightSource.bJNI = isoLightSource.b;
                        setLightColor(isoLightSource.ID, isoLightSource.r, isoLightSource.g, isoLightSource.b);
                    }
                    if (isoLightSource.bActiveJNI != isoLightSource.bActive) {
                        isoLightSource.bActiveJNI = isoLightSource.bActive;
                        setLightActive(isoLightSource.ID, isoLightSource.bActive);
                    }
                }
            }
        }
        for (int j = 0; j < LightingJNI.JNILights.size(); ++j) {
            final IsoLightSource o = LightingJNI.JNILights.get(j);
            if (!lamppostPositions.contains(o)) {
                final int id2 = o.ID;
                o.ID = 0;
                LightingJNI.JNILights.remove(j--);
                removeLight(id2);
            }
        }
        final ArrayList<IsoRoomLight> roomLights = IsoWorld.instance.CurrentCell.roomLights;
        for (int k = 0; k < roomLights.size(); ++k) {
            final IsoRoomLight isoRoomLight = roomLights.get(k);
            if (isoRoomLight.isInBounds()) {
                isoRoomLight.bActive = isoRoomLight.room.def.bLightsActive;
                if (!b) {
                    int n = 0;
                    for (int index = 0; n == 0 && index < isoRoomLight.room.lightSwitches.size(); ++index) {
                        final IsoLightSwitch isoLightSwitch2 = isoRoomLight.room.lightSwitches.get(index);
                        if (isoLightSwitch2.square != null && isoLightSwitch2.square.haveElectricity()) {
                            n = 1;
                        }
                    }
                    if (n == 0 && isoRoomLight.bActive) {
                        isoRoomLight.bActive = false;
                        if (isoRoomLight.bActiveJNI) {
                            IsoGridSquare.RecalcLightTime = -1;
                            GameTime.instance.lightSourceUpdate = 100.0f;
                        }
                    }
                    else if (n != 0 && isoRoomLight.bActive && !isoRoomLight.bActiveJNI) {
                        IsoGridSquare.RecalcLightTime = -1;
                        GameTime.instance.lightSourceUpdate = 100.0f;
                    }
                }
                if (isoRoomLight.ID == 0) {
                    addRoomLight(isoRoomLight.ID = 100000 + IsoRoomLight.NextID++, isoRoomLight.room.building.ID, isoRoomLight.room.def.ID, isoRoomLight.x, isoRoomLight.y, isoRoomLight.z, isoRoomLight.width, isoRoomLight.height, isoRoomLight.bActive);
                    isoRoomLight.bActiveJNI = isoRoomLight.bActive;
                    GameTime.instance.lightSourceUpdate = 100.0f;
                }
                else if (isoRoomLight.bActiveJNI != isoRoomLight.bActive) {
                    setRoomLightActive(isoRoomLight.ID, isoRoomLight.bActive);
                    isoRoomLight.bActiveJNI = isoRoomLight.bActive;
                    GameTime.instance.lightSourceUpdate = 100.0f;
                }
            }
            else {
                roomLights.remove(k--);
                if (isoRoomLight.ID != 0) {
                    final int id3 = isoRoomLight.ID;
                    isoRoomLight.ID = 0;
                    removeRoomLight(id3);
                    GameTime.instance.lightSourceUpdate = 100.0f;
                }
            }
        }
        LightingJNI.activeTorches.clear();
        if (GameClient.bClient) {
            final ArrayList<IsoPlayer> players = GameClient.instance.getPlayers();
            for (int l = 0; l < players.size(); ++l) {
                final IsoPlayer isoPlayer = players.get(l);
                checkPlayerTorches(isoPlayer, isoPlayer.OnlineID + 1);
            }
        }
        else {
            for (int n2 = 0; n2 < IsoPlayer.numPlayers; ++n2) {
                final IsoPlayer isoPlayer2 = IsoPlayer.players[n2];
                if (isoPlayer2 == null || isoPlayer2.isDead() || isoPlayer2.getVehicle() != null) {
                    clearPlayerTorches(n2, 0);
                }
                else {
                    clearPlayerTorches(n2, checkPlayerTorches(isoPlayer2, n2));
                }
            }
        }
        for (int index2 = 0; index2 < IsoWorld.instance.CurrentCell.getVehicles().size(); ++index2) {
            final BaseVehicle baseVehicle = IsoWorld.instance.CurrentCell.getVehicles().get(index2);
            if (baseVehicle.VehicleID != -1) {
                for (int n3 = 0; n3 < baseVehicle.getLightCount(); ++n3) {
                    checkTorch(baseVehicle.getLightByIndex(n3), 1024 + baseVehicle.VehicleID * 10 + n3);
                }
            }
        }
        for (int index3 = 0; index3 < LightingJNI.torches.size(); ++index3) {
            final IsoGameCharacter.TorchInfo o2 = LightingJNI.torches.get(index3);
            if (!LightingJNI.activeTorches.contains(o2)) {
                removeTorch(o2.id);
                o2.id = 0;
                IsoGameCharacter.TorchInfo.release(o2);
                LightingJNI.torches.remove(index3--);
            }
        }
    }
    
    public static float calculateVisionCone(final IsoGameCharacter isoGameCharacter) {
        float n2;
        if (isoGameCharacter.getVehicle() == null) {
            float n = -0.2f - (isoGameCharacter.getStats().fatigue - 0.6f);
            if (n > -0.2f) {
                n = -0.2f;
            }
            if (isoGameCharacter.getStats().fatigue >= 1.0f) {
                n -= 0.2f;
            }
            if (isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) == 4) {
                n -= 0.2f;
            }
            if (isoGameCharacter.isInARoom()) {
                n2 = n - 0.2f * (1.0f - ClimateManager.getInstance().getDayLightStrength());
            }
            else {
                n2 = n - 0.7f * (1.0f - ClimateManager.getInstance().getDayLightStrength());
            }
            if (n2 < -0.9f) {
                n2 = -0.9f;
            }
            if (isoGameCharacter.Traits.EagleEyed.isSet()) {
                n2 += 0.2f * ClimateManager.getInstance().getDayLightStrength();
            }
            if (isoGameCharacter.Traits.NightVision.isSet()) {
                n2 += 0.2f * (1.0f - ClimateManager.getInstance().getDayLightStrength());
            }
            if (n2 > 0.0f) {
                n2 = 0.0f;
            }
        }
        else {
            if (isoGameCharacter.getVehicle().getHeadlightsOn() && isoGameCharacter.getVehicle().getHeadlightCanEmmitLight()) {
                n2 = 0.8f - 3.0f * (1.0f - ClimateManager.getInstance().getDayLightStrength());
                if (n2 < -0.8f) {
                    n2 = -0.8f;
                }
            }
            else {
                n2 = 0.8f - 3.0f * (1.0f - ClimateManager.getInstance().getDayLightStrength());
                if (n2 < -0.95f) {
                    n2 = -0.95f;
                }
            }
            if (isoGameCharacter.Traits.NightVision.isSet()) {
                n2 += 0.2f * (1.0f - ClimateManager.getInstance().getDayLightStrength());
            }
            if (n2 > 1.0f) {
                n2 = 1.0f;
            }
        }
        return n2;
    }
    
    public static void updatePlayer(final int n) {
        final IsoPlayer isoPlayer = IsoPlayer.players[n];
        if (isoPlayer != null) {
            float n2 = isoPlayer.getStats().fatigue - 0.6f;
            if (n2 < 0.0f) {
                n2 = 0.0f;
            }
            float n3 = n2 * 2.5f;
            if (isoPlayer.Traits.HardOfHearing.isSet() && n3 < 0.7f) {
                n3 = 0.7f;
            }
            float n4 = 2.0f;
            if (isoPlayer.Traits.KeenHearing.isSet()) {
                n4 += 3.0f;
            }
            final float calculateVisionCone = calculateVisionCone(isoPlayer);
            final Vector2 lookVector = isoPlayer.getLookVector(LightingJNI.tempVector2);
            final BaseVehicle vehicle = isoPlayer.getVehicle();
            if (vehicle != null && !isoPlayer.isAiming() && !isoPlayer.isLookingWhileInVehicle() && vehicle.isDriver(isoPlayer) && vehicle.getCurrentSpeedKmHour() < -1.0f) {
                lookVector.rotate(3.1415927f);
            }
            playerSet(isoPlayer.x, isoPlayer.y, isoPlayer.z, lookVector.x, lookVector.y, false, isoPlayer.ReanimatedCorpse != null, isoPlayer.isGhostMode(), isoPlayer.Traits.ShortSighted.isSet(), n3, n4, calculateVisionCone);
        }
    }
    
    public static void updateChunk(final IsoChunk isoChunk) {
        chunkBeginUpdate(isoChunk.wx, isoChunk.wy);
        for (int i = 0; i < IsoCell.MaxHeight; ++i) {
            for (int j = 0; j < 10; ++j) {
                for (int k = 0; k < 10; ++k) {
                    final IsoGridSquare gridSquare = isoChunk.getGridSquare(k, j, i);
                    if (gridSquare != null) {
                        squareBeginUpdate(k, j, i);
                        squareSet(gridSquare.w != null, gridSquare.n != null, gridSquare.e != null, gridSquare.s != null, gridSquare.Has(IsoObjectType.stairsTN) || gridSquare.Has(IsoObjectType.stairsMN) || gridSquare.Has(IsoObjectType.stairsTW) || gridSquare.Has(IsoObjectType.stairsMW), gridSquare.visionMatrix, (gridSquare.getRoom() != null) ? gridSquare.getBuilding().ID : -1, gridSquare.getRoomID());
                        for (int l = 0; l < gridSquare.getSpecialObjects().size(); ++l) {
                            final IsoObject isoObject = gridSquare.getSpecialObjects().get(l);
                            if (isoObject instanceof IsoCurtain) {
                                final IsoCurtain isoCurtain = (IsoCurtain)isoObject;
                                int n = 0;
                                if (isoCurtain.getType() == IsoObjectType.curtainW) {
                                    n |= 0x4;
                                }
                                else if (isoCurtain.getType() == IsoObjectType.curtainN) {
                                    n |= 0x8;
                                }
                                else if (isoCurtain.getType() == IsoObjectType.curtainE) {
                                    n |= 0x10;
                                }
                                else if (isoCurtain.getType() == IsoObjectType.curtainS) {
                                    n |= 0x20;
                                }
                                squareAddCurtain(n, isoCurtain.open);
                            }
                            else if (isoObject instanceof IsoDoor) {
                                final IsoDoor isoDoor = (IsoDoor)isoObject;
                                final boolean b = isoDoor.sprite != null && isoDoor.sprite.getProperties().Is("doorTrans");
                                boolean b2 = isoDoor.open || (b && (isoDoor.HasCurtains() == null || isoDoor.isCurtainOpen()));
                                final IsoBarricade barricadeOnSameSquare = isoDoor.getBarricadeOnSameSquare();
                                final IsoBarricade barricadeOnOppositeSquare = isoDoor.getBarricadeOnOppositeSquare();
                                if (barricadeOnSameSquare != null && barricadeOnSameSquare.isBlockVision()) {
                                    b2 = false;
                                }
                                if (barricadeOnOppositeSquare != null && barricadeOnOppositeSquare.isBlockVision()) {
                                    b2 = false;
                                }
                                if (isoDoor.IsOpen() && IsoDoor.getGarageDoorIndex(isoDoor) != -1) {
                                    b2 = true;
                                }
                                squareAddDoor(isoDoor.north, isoDoor.open, b2);
                            }
                            else if (isoObject instanceof IsoThumpable) {
                                final IsoThumpable isoThumpable = (IsoThumpable)isoObject;
                                boolean is = isoThumpable.getSprite().getProperties().Is("doorTrans");
                                if (isoThumpable.isDoor && isoThumpable.open) {
                                    is = true;
                                }
                                squareAddThumpable(isoThumpable.north, isoThumpable.open, isoThumpable.isDoor, is);
                                final IsoThumpable isoThumpable2 = (IsoThumpable)isoObject;
                                boolean b3 = false;
                                final IsoBarricade barricadeOnSameSquare2 = isoThumpable2.getBarricadeOnSameSquare();
                                final IsoBarricade barricadeOnOppositeSquare2 = isoThumpable2.getBarricadeOnOppositeSquare();
                                if (barricadeOnSameSquare2 != null) {
                                    b3 |= barricadeOnSameSquare2.isBlockVision();
                                }
                                if (barricadeOnOppositeSquare2 != null) {
                                    b3 |= barricadeOnOppositeSquare2.isBlockVision();
                                }
                                squareAddWindow(isoThumpable2.north, isoThumpable2.open, b3);
                            }
                            else if (isoObject instanceof IsoWindow) {
                                final IsoWindow isoWindow = (IsoWindow)isoObject;
                                boolean b4 = false;
                                final IsoBarricade barricadeOnSameSquare3 = isoWindow.getBarricadeOnSameSquare();
                                final IsoBarricade barricadeOnOppositeSquare3 = isoWindow.getBarricadeOnOppositeSquare();
                                if (barricadeOnSameSquare3 != null) {
                                    b4 |= barricadeOnSameSquare3.isBlockVision();
                                }
                                if (barricadeOnOppositeSquare3 != null) {
                                    b4 |= barricadeOnOppositeSquare3.isBlockVision();
                                }
                                squareAddWindow(isoWindow.north, isoWindow.open, b4);
                            }
                        }
                        squareEndUpdate();
                    }
                    else {
                        squareSetNull(k, j, i);
                    }
                }
            }
        }
        chunkEndUpdate();
    }
    
    public static void update() {
        if (IsoWorld.instance == null || IsoWorld.instance.CurrentCell == null) {
            return;
        }
        checkLights();
        final GameTime instance = GameTime.getInstance();
        final RenderSettings instance2 = RenderSettings.getInstance();
        final boolean bWasElecShut = instance.getNightsSurvived() < SandboxOptions.instance.getElecShutModifier();
        final boolean bWasNight = GameTime.getInstance().getNight() < 0.5f;
        if (bWasElecShut != LightingJNI.bWasElecShut || bWasNight != LightingJNI.bWasNight) {
            LightingJNI.bWasElecShut = bWasElecShut;
            LightingJNI.bWasNight = bWasNight;
            IsoGridSquare.RecalcLightTime = -1;
            instance.lightSourceUpdate = 100.0f;
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[i];
            if (isoChunkMap != null) {
                if (!isoChunkMap.ignore) {
                    final RenderSettings.PlayerRenderSettings playerSettings = instance2.getPlayerSettings(i);
                    stateBeginUpdate(i, isoChunkMap.getWorldXMin(), isoChunkMap.getWorldYMin(), IsoChunkMap.ChunkGridWidth, IsoChunkMap.ChunkGridWidth);
                    updatePlayer(i);
                    stateEndFrame(playerSettings.getRmod(), playerSettings.getGmod(), playerSettings.getBmod(), playerSettings.getAmbient(), playerSettings.getNight(), playerSettings.getViewDistance(), instance.getViewDistMax(), LosUtil.cachecleared[i], instance.lightSourceUpdate);
                    if (LosUtil.cachecleared[i]) {
                        LosUtil.cachecleared[i] = false;
                        IsoWorld.instance.CurrentCell.invalidatePeekedRoom(i);
                    }
                    for (int j = 0; j < IsoChunkMap.ChunkGridWidth; ++j) {
                        for (int k = 0; k < IsoChunkMap.ChunkGridWidth; ++k) {
                            final IsoChunk chunk = isoChunkMap.getChunk(k, j);
                            if (chunk != null && chunk.lightCheck[i]) {
                                updateChunk(chunk);
                                chunk.lightCheck[i] = false;
                            }
                            if (chunk != null) {
                                chunk.bLightingNeverDone[i] = !chunkLightingDone(chunk.wx, chunk.wy);
                            }
                        }
                    }
                    stateEndUpdate();
                    LightingJNI.updateCounter[i] = stateUpdateCounter(i);
                    if (instance.lightSourceUpdate > 0.0f && IsoPlayer.players[i] != null) {
                        IsoPlayer.players[i].dirtyRecalcGridStackTime = 20.0f;
                    }
                }
            }
        }
        DeadBodyAtlas.instance.lightingUpdate(LightingJNI.updateCounter[0], instance.lightSourceUpdate > 0.0f);
        instance.lightSourceUpdate = 0.0f;
    }
    
    public static void getTorches(final ArrayList<IsoGameCharacter.TorchInfo> list) {
        list.addAll(LightingJNI.torches);
    }
    
    public static void stop() {
        LightingJNI.torches.clear();
        LightingJNI.JNILights.clear();
        destroy();
        for (int i = 0; i < LightingJNI.updateCounter.length; ++i) {
            LightingJNI.updateCounter[i] = -1;
        }
        LightingJNI.bWasElecShut = false;
        LightingJNI.bWasNight = false;
        IsoLightSource.NextID = 1;
        IsoRoomLight.NextID = 1;
    }
    
    public static native void configure(final float p0);
    
    public static native void scrollLeft(final int p0);
    
    public static native void scrollRight(final int p0);
    
    public static native void scrollUp(final int p0);
    
    public static native void scrollDown(final int p0);
    
    public static native void stateBeginUpdate(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    public static native void stateEndFrame(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final boolean p7, final float p8);
    
    public static native void stateEndUpdate();
    
    public static native int stateUpdateCounter(final int p0);
    
    public static native void teleport(final int p0, final int p1, final int p2);
    
    public static native void DoLightingUpdateNew(final long p0);
    
    public static native boolean WaitingForMain();
    
    public static native void playerSet(final float p0, final float p1, final float p2, final float p3, final float p4, final boolean p5, final boolean p6, final boolean p7, final boolean p8, final float p9, final float p10, final float p11);
    
    public static native boolean chunkLightingDone(final int p0, final int p1);
    
    public static native void chunkBeginUpdate(final int p0, final int p1);
    
    public static native void chunkEndUpdate();
    
    public static native void squareSetNull(final int p0, final int p1, final int p2);
    
    public static native void squareBeginUpdate(final int p0, final int p1, final int p2);
    
    public static native void squareSet(final boolean p0, final boolean p1, final boolean p2, final boolean p3, final boolean p4, final int p5, final int p6, final int p7);
    
    public static native void squareAddCurtain(final int p0, final boolean p1);
    
    public static native void squareAddDoor(final boolean p0, final boolean p1, final boolean p2);
    
    public static native void squareAddThumpable(final boolean p0, final boolean p1, final boolean p2, final boolean p3);
    
    public static native void squareAddWindow(final boolean p0, final boolean p1, final boolean p2);
    
    public static native void squareEndUpdate();
    
    public static native int getVertLight(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    public static native float getLightInfo(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    public static native float getDarkMulti(final int p0, final int p1, final int p2, final int p3);
    
    public static native float getTargetDarkMulti(final int p0, final int p1, final int p2, final int p3);
    
    public static native boolean getSeen(final int p0, final int p1, final int p2, final int p3);
    
    public static native boolean getCanSee(final int p0, final int p1, final int p2, final int p3);
    
    public static native boolean getCouldSee(final int p0, final int p1, final int p2, final int p3);
    
    public static native boolean getSquareLighting(final int p0, final int p1, final int p2, final int p3, final int[] p4);
    
    public static native void addLight(final int p0, final int p1, final int p2, final int p3, final int p4, final float p5, final float p6, final float p7, final int p8, final boolean p9);
    
    public static native void addTempLight(final int p0, final int p1, final int p2, final int p3, final int p4, final float p5, final float p6, final float p7, final int p8);
    
    public static native void removeLight(final int p0);
    
    public static native void setLightActive(final int p0, final boolean p1);
    
    public static native void setLightColor(final int p0, final float p1, final float p2, final float p3);
    
    public static native void addRoomLight(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final boolean p8);
    
    public static native void removeRoomLight(final int p0);
    
    public static native void setRoomLightActive(final int p0, final boolean p1);
    
    public static native void updateTorch(final int p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final boolean p8, final float p9, final int p10);
    
    public static native void removeTorch(final int p0);
    
    public static native void destroy();
    
    static {
        LightingJNI.init = false;
        ForcedVis = new int[][] { { -1, 0, -1, -1, 0, -1, 1, -1, 1, 0, -2, -2, -1, -2, 0, -2, 1, -2, 2, -2 }, { -1, 1, -1, 0, -1, -1, 0, -1, 1, -1, -2, 0, -2, -1, -2, -2, -1, -2, 0, -2 }, { 0, 1, -1, 1, -1, 0, -1, -1, 0, -1, -2, 2, -2, 1, -2, 0, -2, -1, -2, -2 }, { 1, 1, 0, 1, -1, 1, -1, 0, -1, -1, 0, 2, -1, 2, -2, 2, -2, 1, -2, 0 }, { 1, 0, 1, 1, 0, 1, -1, 1, -1, 0, 2, 2, 1, 2, 0, 2, -1, 2, -2, 2 }, { -1, 1, 0, 1, 1, 1, 1, 0, 1, -1, 2, 0, 2, 1, 2, 2, 1, 2, 0, 2 }, { 0, 1, 1, 1, 1, 0, 1, -1, 0, -1, 2, -2, 2, -1, 2, 0, 2, 1, 2, 2 }, { -1, -1, 0, -1, 1, -1, 1, 0, 1, 1, 0, -2, 1, -2, 2, -2, 2, -1, 2, 0 } };
        torches = new ArrayList<IsoGameCharacter.TorchInfo>();
        activeTorches = new ArrayList<IsoGameCharacter.TorchInfo>();
        JNILights = new ArrayList<IsoLightSource>();
        updateCounter = new int[4];
        LightingJNI.bWasElecShut = false;
        LightingJNI.bWasNight = false;
        tempVector2 = new Vector2();
        tempItems = new ArrayList<InventoryItem>();
    }
    
    public static final class JNILighting implements IsoGridSquare.ILighting
    {
        private static final int RESULT_LIGHTS_PER_SQUARE = 5;
        private static final int[] lightInts;
        private static final byte VIS_SEEN = 1;
        private static final byte VIS_CAN_SEE = 2;
        private static final byte VIS_COULD_SEE = 4;
        private int playerIndex;
        private final IsoGridSquare square;
        private ColorInfo lightInfo;
        private byte vis;
        private float cacheDarkMulti;
        private float cacheTargetDarkMulti;
        private int[] cacheVertLight;
        private int updateTick;
        private int lightsCount;
        private IsoGridSquare.ResultLight[] lights;
        
        public JNILighting(final int playerIndex, final IsoGridSquare square) {
            this.lightInfo = new ColorInfo();
            this.updateTick = -1;
            this.playerIndex = playerIndex;
            this.square = square;
            this.cacheDarkMulti = 0.0f;
            this.cacheTargetDarkMulti = 0.0f;
            this.cacheVertLight = new int[8];
            for (int i = 0; i < 8; ++i) {
                this.cacheVertLight[i] = 0;
            }
        }
        
        @Override
        public int lightverts(final int n) {
            return this.cacheVertLight[n];
        }
        
        @Override
        public float lampostTotalR() {
            return 0.0f;
        }
        
        @Override
        public float lampostTotalG() {
            return 0.0f;
        }
        
        @Override
        public float lampostTotalB() {
            return 0.0f;
        }
        
        @Override
        public boolean bSeen() {
            this.update();
            return (this.vis & 0x1) != 0x0;
        }
        
        @Override
        public boolean bCanSee() {
            this.update();
            return (this.vis & 0x2) != 0x0;
        }
        
        @Override
        public boolean bCouldSee() {
            this.update();
            return (this.vis & 0x4) != 0x0;
        }
        
        @Override
        public float darkMulti() {
            return this.cacheDarkMulti;
        }
        
        @Override
        public float targetDarkMulti() {
            return this.cacheTargetDarkMulti;
        }
        
        @Override
        public ColorInfo lightInfo() {
            this.update();
            return this.lightInfo;
        }
        
        @Override
        public void lightverts(final int n, final int n2) {
            throw new IllegalStateException();
        }
        
        @Override
        public void lampostTotalR(final float n) {
            throw new IllegalStateException();
        }
        
        @Override
        public void lampostTotalG(final float n) {
            throw new IllegalStateException();
        }
        
        @Override
        public void lampostTotalB(final float n) {
            throw new IllegalStateException();
        }
        
        @Override
        public void bSeen(final boolean b) {
            throw new IllegalStateException();
        }
        
        @Override
        public void bCanSee(final boolean b) {
            throw new IllegalStateException();
        }
        
        @Override
        public void bCouldSee(final boolean b) {
            throw new IllegalStateException();
        }
        
        @Override
        public void darkMulti(final float n) {
            throw new IllegalStateException();
        }
        
        @Override
        public void targetDarkMulti(final float n) {
            throw new IllegalStateException();
        }
        
        @Override
        public int resultLightCount() {
            return this.lightsCount;
        }
        
        @Override
        public IsoGridSquare.ResultLight getResultLight(final int n) {
            return this.lights[n];
        }
        
        @Override
        public void reset() {
            this.updateTick = -1;
        }
        
        private void update() {
            if (LightingJNI.updateCounter[this.playerIndex] == -1) {
                return;
            }
            if (this.updateTick != LightingJNI.updateCounter[this.playerIndex] && LightingJNI.getSquareLighting(this.playerIndex, this.square.x, this.square.y, this.square.z, JNILighting.lightInts)) {
                final IsoPlayer isoPlayer = IsoPlayer.players[this.playerIndex];
                final boolean b = (this.vis & 0x1) != 0x0;
                int n = 0;
                this.vis = (byte)(JNILighting.lightInts[n++] & 0x7);
                this.lightInfo.r = (JNILighting.lightInts[n] & 0xFF) / 255.0f;
                this.lightInfo.g = (JNILighting.lightInts[n] >> 8 & 0xFF) / 255.0f;
                this.lightInfo.b = (JNILighting.lightInts[n++] >> 16 & 0xFF) / 255.0f;
                this.cacheDarkMulti = JNILighting.lightInts[n++] / 100000.0f;
                this.cacheTargetDarkMulti = JNILighting.lightInts[n++] / 100000.0f;
                float n2 = 1.0f;
                float n3 = 1.0f;
                if (isoPlayer != null) {
                    final int n4 = this.square.z - (int)isoPlayer.z;
                    if (n4 == -1) {
                        n2 = 1.0f;
                        n3 = 0.85f;
                    }
                    else if (n4 < -1) {
                        n2 = 0.85f;
                        n3 = 0.85f;
                    }
                    if ((this.vis & 0x2) == 0x0 && (this.vis & 0x4) != 0x0) {
                        final int n5 = (int)isoPlayer.x;
                        final int n6 = (int)isoPlayer.y;
                        final int a = this.square.x - n5;
                        final int a2 = this.square.y - n6;
                        if (isoPlayer.dir != IsoDirections.Max && Math.abs(a) <= 2 && Math.abs(a2) <= 2) {
                            final int[] array = LightingJNI.ForcedVis[isoPlayer.dir.index()];
                            for (int i = 0; i < array.length; i += 2) {
                                if (a == array[i] && a2 == array[i + 1]) {
                                    this.vis |= 0x2;
                                    break;
                                }
                            }
                        }
                    }
                }
                for (int j = 0; j < 4; ++j) {
                    final int n7 = JNILighting.lightInts[n++];
                    this.cacheVertLight[j] = ((int)((n7 & 0xFF) * n3) << 0 | (int)(((n7 & 0xFF00) >> 8) * n3) << 8 | (int)(((n7 & 0xFF0000) >> 16) * n3) << 16 | 0xFF000000);
                }
                for (int k = 4; k < 8; ++k) {
                    final int n8 = JNILighting.lightInts[n++];
                    this.cacheVertLight[k] = ((int)((n8 & 0xFF) * n2) << 0 | (int)(((n8 & 0xFF00) >> 8) * n2) << 8 | (int)(((n8 & 0xFF0000) >> 16) * n2) << 16 | 0xFF000000);
                }
                this.lightsCount = JNILighting.lightInts[n++];
                for (int l = 0; l < this.lightsCount; ++l) {
                    if (this.lights == null) {
                        this.lights = new IsoGridSquare.ResultLight[5];
                    }
                    if (this.lights[l] == null) {
                        this.lights[l] = new IsoGridSquare.ResultLight();
                    }
                    this.lights[l].id = JNILighting.lightInts[n++];
                    this.lights[l].x = JNILighting.lightInts[n++];
                    this.lights[l].y = JNILighting.lightInts[n++];
                    this.lights[l].z = JNILighting.lightInts[n++];
                    this.lights[l].radius = JNILighting.lightInts[n++];
                    final int n9 = JNILighting.lightInts[n++];
                    this.lights[l].r = (n9 & 0xFF) / 255.0f;
                    this.lights[l].g = (n9 >> 8 & 0xFF) / 255.0f;
                    this.lights[l].b = (n9 >> 16 & 0xFF) / 255.0f;
                    this.lights[l].flags = (n9 >> 24 & 0xFF);
                }
                this.updateTick = LightingJNI.updateCounter[this.playerIndex];
                if ((this.vis & 0x1) != 0x0) {
                    if (b && this.square.getRoom() != null && this.square.getRoom().def != null && !this.square.getRoom().def.bExplored) {}
                    this.square.checkRoomSeen(this.playerIndex);
                    if (!b) {
                        assert !GameServer.bServer;
                        if (!GameClient.bClient) {
                            Meta.instance.dealWithSquareSeen(this.square);
                        }
                    }
                }
            }
        }
        
        static {
            lightInts = new int[43];
        }
    }
}
