// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.stash;

import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.VirtualZombieManager;
import zombie.ZombieSpawnRecorder;
import zombie.iso.objects.IsoTrap;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.ItemContainer;
import zombie.iso.objects.IsoThumpable;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.InventoryItemFactory;
import zombie.debug.DebugLog;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoObject;
import zombie.inventory.ItemPickerJava;
import zombie.iso.BuildingDef;
import zombie.GameTime;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.RoomDef;
import zombie.iso.IsoWorld;
import zombie.ui.UIFont;
import zombie.network.GameServer;
import zombie.worldMap.symbols.WorldMapBaseSymbol;
import zombie.util.Type;
import zombie.inventory.types.MapItem;
import zombie.core.Rand;
import zombie.SandboxOptions;
import zombie.network.GameClient;
import zombie.inventory.InventoryItem;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;
import java.util.ArrayList;

public final class StashSystem
{
    public static ArrayList<Stash> allStashes;
    public static ArrayList<StashBuilding> possibleStashes;
    public static ArrayList<StashBuilding> buildingsToDo;
    private static final ArrayList<String> possibleTrap;
    private static ArrayList<String> alreadyReadMap;
    
    public static void init() {
        if (StashSystem.possibleStashes == null) {
            initAllStashes();
            StashSystem.buildingsToDo = new ArrayList<StashBuilding>();
            StashSystem.possibleTrap.add("Base.FlameTrapSensorV1");
            StashSystem.possibleTrap.add("Base.SmokeBombSensorV1");
            StashSystem.possibleTrap.add("Base.NoiseTrapSensorV1");
            StashSystem.possibleTrap.add("Base.NoiseTrapSensorV2");
            StashSystem.possibleTrap.add("Base.AerosolbombSensorV1");
        }
    }
    
    public static void initAllStashes() {
        StashSystem.allStashes = new ArrayList<Stash>();
        StashSystem.possibleStashes = new ArrayList<StashBuilding>();
        final KahluaTableIterator iterator = ((KahluaTable)LuaManager.env.rawget((Object)"StashDescriptions")).iterator();
        while (iterator.advance()) {
            final KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)iterator.getValue();
            final Stash e = new Stash(kahluaTableImpl.rawgetStr((Object)"name"));
            e.load(kahluaTableImpl);
            StashSystem.allStashes.add(e);
        }
    }
    
    public static void checkStashItem(final InventoryItem inventoryItem) {
        if (GameClient.bClient || StashSystem.possibleStashes.isEmpty()) {
            return;
        }
        int stashChance = 60;
        if (inventoryItem.getStashChance() > 0) {
            stashChance = inventoryItem.getStashChance();
        }
        switch (SandboxOptions.instance.AnnotatedMapChance.getValue()) {
            case 1: {
                return;
            }
            case 2: {
                stashChance += 15;
                break;
            }
            case 3: {
                stashChance += 10;
                break;
            }
            case 5: {
                stashChance -= 10;
                break;
            }
            case 6: {
                stashChance -= 20;
                break;
            }
        }
        if (Rand.Next(100) > 100 - stashChance) {
            return;
        }
        final ArrayList<Stash> list = new ArrayList<Stash>();
        for (int i = 0; i < StashSystem.allStashes.size(); ++i) {
            final Stash e = StashSystem.allStashes.get(i);
            if (e.item.equals(inventoryItem.getFullType()) && checkSpecificSpawnProperties(e, inventoryItem)) {
                boolean b = false;
                for (int j = 0; j < StashSystem.possibleStashes.size(); ++j) {
                    if (StashSystem.possibleStashes.get(j).stashName.equals(e.name)) {
                        b = true;
                        break;
                    }
                }
                if (b) {
                    list.add(e);
                }
            }
        }
        if (list.isEmpty()) {
            return;
        }
        doStashItem(list.get(Rand.Next(0, list.size())), inventoryItem);
    }
    
    public static void doStashItem(final Stash stash, final InventoryItem inventoryItem) {
        if (stash.customName != null) {
            inventoryItem.setName(stash.customName);
        }
        if ("Map".equals(stash.type)) {
            final MapItem mapItem = Type.tryCastTo(inventoryItem, MapItem.class);
            if (mapItem == null) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Lzombie/inventory/InventoryItem;)Ljava/lang/String;, inventoryItem));
            }
            if (stash.annotations != null) {
                for (int i = 0; i < stash.annotations.size(); ++i) {
                    final StashAnnotation stashAnnotation = stash.annotations.get(i);
                    if (stashAnnotation.symbol != null) {
                        mapItem.getSymbols().addTexture(stashAnnotation.symbol, stashAnnotation.x, stashAnnotation.y, 0.5f, 0.5f, WorldMapBaseSymbol.DEFAULT_SCALE, stashAnnotation.r, stashAnnotation.g, stashAnnotation.b, 1.0f);
                    }
                    else if (stashAnnotation.text != null && !GameServer.bServer) {
                        mapItem.getSymbols().addUntranslatedText(stashAnnotation.text, UIFont.Handwritten, stashAnnotation.x, stashAnnotation.y, stashAnnotation.r, stashAnnotation.g, stashAnnotation.b, 1.0f);
                    }
                }
            }
            removeFromPossibleStash(stash);
            inventoryItem.setStashMap(stash.name);
        }
    }
    
    public static void prepareBuildingStash(final String s) {
        if (s == null) {
            return;
        }
        final Stash stash = getStash(s);
        if (stash != null && !StashSystem.alreadyReadMap.contains(s)) {
            StashSystem.alreadyReadMap.add(s);
            StashSystem.buildingsToDo.add(new StashBuilding(stash.name, stash.buildingX, stash.buildingY));
            final RoomDef room = IsoWorld.instance.getMetaGrid().getRoomAt(stash.buildingX, stash.buildingY, 0);
            if (room != null && room.getBuilding() != null && room.getBuilding().isFullyStreamedIn()) {
                doBuildingStash(room.getBuilding());
            }
        }
    }
    
    private static boolean checkSpecificSpawnProperties(final Stash stash, final InventoryItem inventoryItem) {
        return (!stash.spawnOnlyOnZed || (inventoryItem.getContainer() != null && inventoryItem.getContainer().getParent() instanceof IsoDeadBody)) && (stash.minDayToSpawn <= -1 || GameTime.instance.getDaysSurvived() >= stash.minDayToSpawn) && (stash.maxDayToSpawn <= -1 || GameTime.instance.getDaysSurvived() <= stash.maxDayToSpawn);
    }
    
    private static void removeFromPossibleStash(final Stash stash) {
        for (int i = 0; i < StashSystem.possibleStashes.size(); ++i) {
            final StashBuilding stashBuilding = StashSystem.possibleStashes.get(i);
            if (stashBuilding.buildingX == stash.buildingX && stashBuilding.buildingY == stash.buildingY) {
                StashSystem.possibleStashes.remove(i);
                --i;
            }
        }
    }
    
    public static void doBuildingStash(final BuildingDef buildingDef) {
        if (StashSystem.buildingsToDo == null) {
            init();
        }
        for (int i = 0; i < StashSystem.buildingsToDo.size(); ++i) {
            final StashBuilding stashBuilding = StashSystem.buildingsToDo.get(i);
            if (stashBuilding.buildingX > buildingDef.x && stashBuilding.buildingX < buildingDef.x2 && stashBuilding.buildingY > buildingDef.y && stashBuilding.buildingY < buildingDef.y2) {
                if (buildingDef.hasBeenVisited) {
                    StashSystem.buildingsToDo.remove(i);
                    --i;
                }
                else {
                    final Stash stash = getStash(stashBuilding.stashName);
                    if (stash != null) {
                        final ItemPickerJava.ItemPickerRoom itemPickerRoom = (ItemPickerJava.ItemPickerRoom)ItemPickerJava.rooms.get((Object)stash.spawnTable);
                        buildingDef.setAllExplored(true);
                        doSpecificBuildingProperties(stash, buildingDef);
                        for (int j = buildingDef.x - 1; j < buildingDef.x2 + 1; ++j) {
                            for (int k = buildingDef.y - 1; k < buildingDef.y2 + 1; ++k) {
                                for (int l = 0; l < 8; ++l) {
                                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(j, k, l);
                                    if (gridSquare != null) {
                                        for (int n = 0; n < gridSquare.getObjects().size(); ++n) {
                                            final IsoObject isoObject = gridSquare.getObjects().get(n);
                                            if (isoObject.getContainer() != null && gridSquare.getRoom() != null && gridSquare.getRoom().getBuilding().getDef() == buildingDef && gridSquare.getRoom().getName() != null && itemPickerRoom.Containers.containsKey((Object)isoObject.getContainer().getType())) {
                                                final ItemPickerJava.ItemPickerRoom itemPickerRoom2 = (ItemPickerJava.ItemPickerRoom)ItemPickerJava.rooms.get((Object)gridSquare.getRoom().getName());
                                                boolean b = false;
                                                if (itemPickerRoom2 == null || !itemPickerRoom2.Containers.containsKey((Object)isoObject.getContainer().getType())) {
                                                    isoObject.getContainer().clear();
                                                    b = true;
                                                }
                                                ItemPickerJava.fillContainerType(itemPickerRoom, isoObject.getContainer(), "", null);
                                                ItemPickerJava.updateOverlaySprite(isoObject);
                                                if (b) {
                                                    isoObject.getContainer().setExplored(true);
                                                }
                                            }
                                            final BarricadeAble barricadeAble = Type.tryCastTo(isoObject, BarricadeAble.class);
                                            if (stash.barricades > -1 && barricadeAble != null && barricadeAble.isBarricadeAllowed() && Rand.Next(100) < stash.barricades) {
                                                if (isoObject instanceof IsoDoor) {
                                                    ((IsoDoor)isoObject).addRandomBarricades();
                                                }
                                                else if (isoObject instanceof IsoWindow) {
                                                    ((IsoWindow)isoObject).addRandomBarricades();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        StashSystem.buildingsToDo.remove(i);
                        --i;
                    }
                }
            }
        }
    }
    
    private static void doSpecificBuildingProperties(final Stash stash, final BuildingDef buildingDef) {
        if (stash.containers != null) {
            final ArrayList<RoomDef> list = new ArrayList<RoomDef>();
            for (int i = 0; i < stash.containers.size(); ++i) {
                final StashContainer stashContainer = stash.containers.get(i);
                IsoGridSquare isoGridSquare = null;
                if (!"all".equals(stashContainer.room)) {
                    for (int j = 0; j < buildingDef.rooms.size(); ++j) {
                        final RoomDef e = buildingDef.rooms.get(j);
                        if (stashContainer.room.equals(e.name)) {
                            list.add(e);
                        }
                    }
                }
                else if (stashContainer.contX > -1 && stashContainer.contY > -1 && stashContainer.contZ > -1) {
                    isoGridSquare = IsoWorld.instance.getCell().getGridSquare(stashContainer.contX, stashContainer.contY, stashContainer.contZ);
                }
                else {
                    isoGridSquare = buildingDef.getFreeSquareInRoom();
                }
                if (!list.isEmpty()) {
                    isoGridSquare = list.get(Rand.Next(0, list.size())).getFreeSquare();
                }
                if (isoGridSquare != null) {
                    if (stashContainer.containerItem != null && !stashContainer.containerItem.isEmpty()) {
                        final ItemPickerJava.ItemPickerRoom itemPickerRoom = (ItemPickerJava.ItemPickerRoom)ItemPickerJava.rooms.get((Object)stash.spawnTable);
                        if (itemPickerRoom == null) {
                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, stash.spawnTable));
                            return;
                        }
                        final InventoryItem createItem = InventoryItemFactory.CreateItem(stashContainer.containerItem);
                        if (createItem == null) {
                            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, stashContainer.containerItem));
                            return;
                        }
                        ItemPickerJava.rollContainerItem((InventoryContainer)createItem, null, (ItemPickerJava.ItemPickerContainer)itemPickerRoom.Containers.get((Object)createItem.getType()));
                        isoGridSquare.AddWorldInventoryItem(createItem, 0.0f, 0.0f, 0.0f);
                    }
                    else {
                        final IsoThumpable isoThumpable = new IsoThumpable(isoGridSquare.getCell(), isoGridSquare, stashContainer.containerSprite, false, null);
                        isoThumpable.setIsThumpable(false);
                        isoThumpable.container = new ItemContainer(stashContainer.containerType, isoGridSquare, isoThumpable);
                        isoGridSquare.AddSpecialObject(isoThumpable);
                        isoGridSquare.RecalcAllWithNeighbours(true);
                    }
                }
                else {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, stash.name));
                }
            }
        }
        if (stash.minTrapToSpawn > -1) {
            for (int k = stash.minTrapToSpawn; k < stash.maxTrapToSpawn; ++k) {
                final IsoGridSquare freeSquareInRoom = buildingDef.getFreeSquareInRoom();
                if (freeSquareInRoom != null) {
                    final HandWeapon handWeapon = (HandWeapon)InventoryItemFactory.CreateItem(StashSystem.possibleTrap.get(Rand.Next(0, StashSystem.possibleTrap.size())));
                    if (GameServer.bServer) {
                        GameServer.AddExplosiveTrap(handWeapon, freeSquareInRoom, handWeapon.getSensorRange() > 0);
                    }
                    else {
                        freeSquareInRoom.AddTileObject(new IsoTrap(handWeapon, freeSquareInRoom.getCell(), freeSquareInRoom));
                    }
                }
            }
        }
        if (stash.zombies > -1) {
            for (int l = 0; l < buildingDef.rooms.size(); ++l) {
                final RoomDef roomDef = buildingDef.rooms.get(l);
                if (IsoWorld.getZombiesEnabled()) {
                    final int n = 1;
                    int n2 = 0;
                    for (int n3 = 0; n3 < roomDef.area; ++n3) {
                        if (Rand.Next(100) < stash.zombies) {
                            ++n2;
                        }
                    }
                    if (SandboxOptions.instance.Zombies.getValue() == 1) {
                        n2 += 4;
                    }
                    else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                        n2 += 3;
                    }
                    else if (SandboxOptions.instance.Zombies.getValue() == 3) {
                        n2 += 2;
                    }
                    else if (SandboxOptions.instance.Zombies.getValue() == 5) {
                        n2 -= 4;
                    }
                    if (n2 > roomDef.area / 2) {
                        n2 = roomDef.area / 2;
                    }
                    if (n2 < n) {
                        n2 = n;
                    }
                    ZombieSpawnRecorder.instance.record(VirtualZombieManager.instance.addZombiesToMap(n2, roomDef, false), "StashSystem");
                }
            }
        }
    }
    
    public static Stash getStash(final String anObject) {
        for (int i = 0; i < StashSystem.allStashes.size(); ++i) {
            final Stash stash = StashSystem.allStashes.get(i);
            if (stash.name.equals(anObject)) {
                return stash;
            }
        }
        return null;
    }
    
    public static void visitedBuilding(final BuildingDef buildingDef) {
        if (GameClient.bClient) {
            return;
        }
        for (int i = 0; i < StashSystem.possibleStashes.size(); ++i) {
            final StashBuilding stashBuilding = StashSystem.possibleStashes.get(i);
            if (stashBuilding.buildingX > buildingDef.x && stashBuilding.buildingX < buildingDef.x2 && stashBuilding.buildingY > buildingDef.y && stashBuilding.buildingY < buildingDef.y2) {
                StashSystem.possibleStashes.remove(i);
                --i;
            }
        }
    }
    
    public static void load(final ByteBuffer byteBuffer, final int n) {
        init();
        StashSystem.alreadyReadMap = new ArrayList<String>();
        StashSystem.possibleStashes = new ArrayList<StashBuilding>();
        StashSystem.buildingsToDo = new ArrayList<StashBuilding>();
        for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
            StashSystem.possibleStashes.add(new StashBuilding(GameWindow.ReadString(byteBuffer), byteBuffer.getInt(), byteBuffer.getInt()));
        }
        for (int int2 = byteBuffer.getInt(), j = 0; j < int2; ++j) {
            StashSystem.buildingsToDo.add(new StashBuilding(GameWindow.ReadString(byteBuffer), byteBuffer.getInt(), byteBuffer.getInt()));
        }
        if (n >= 109) {
            for (int int3 = byteBuffer.getInt(), k = 0; k < int3; ++k) {
                StashSystem.alreadyReadMap.add(GameWindow.ReadString(byteBuffer));
            }
        }
    }
    
    public static void save(final ByteBuffer byteBuffer) {
        if (StashSystem.allStashes == null) {
            return;
        }
        byteBuffer.putInt(StashSystem.possibleStashes.size());
        for (int i = 0; i < StashSystem.possibleStashes.size(); ++i) {
            final StashBuilding stashBuilding = StashSystem.possibleStashes.get(i);
            GameWindow.WriteString(byteBuffer, stashBuilding.stashName);
            byteBuffer.putInt(stashBuilding.buildingX);
            byteBuffer.putInt(stashBuilding.buildingY);
        }
        byteBuffer.putInt(StashSystem.buildingsToDo.size());
        for (int j = 0; j < StashSystem.buildingsToDo.size(); ++j) {
            final StashBuilding stashBuilding2 = StashSystem.buildingsToDo.get(j);
            GameWindow.WriteString(byteBuffer, stashBuilding2.stashName);
            byteBuffer.putInt(stashBuilding2.buildingX);
            byteBuffer.putInt(stashBuilding2.buildingY);
        }
        byteBuffer.putInt(StashSystem.alreadyReadMap.size());
        for (int k = 0; k < StashSystem.alreadyReadMap.size(); ++k) {
            GameWindow.WriteString(byteBuffer, StashSystem.alreadyReadMap.get(k));
        }
    }
    
    public static ArrayList<StashBuilding> getPossibleStashes() {
        return StashSystem.possibleStashes;
    }
    
    public static void reinit() {
        StashSystem.possibleStashes = null;
        StashSystem.alreadyReadMap = new ArrayList<String>();
        init();
    }
    
    public static void Reset() {
        StashSystem.allStashes = null;
        StashSystem.possibleStashes = null;
        StashSystem.buildingsToDo = null;
        StashSystem.possibleTrap.clear();
        StashSystem.alreadyReadMap.clear();
    }
    
    static {
        possibleTrap = new ArrayList<String>();
        StashSystem.alreadyReadMap = new ArrayList<String>();
    }
}
