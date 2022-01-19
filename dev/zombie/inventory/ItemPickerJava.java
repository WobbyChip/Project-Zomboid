// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory;

import zombie.util.list.PZArrayList;
import zombie.iso.ContainerOverlays;
import zombie.util.list.PZArrayUtil;
import zombie.inventory.types.WeaponPart;
import zombie.core.stash.StashSystem;
import zombie.inventory.types.MapItem;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.DrainableComboItem;
import zombie.core.Translator;
import zombie.inventory.types.Key;
import zombie.inventory.types.Food;
import zombie.iso.IsoMetaChunk;
import zombie.network.GameServer;
import java.util.List;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.IsoObject;
import zombie.iso.areas.IsoRoom;
import zombie.iso.IsoGridSquare;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.inventory.types.InventoryContainer;
import zombie.iso.objects.IsoDeadBody;
import zombie.core.Core;
import zombie.network.GameClient;
import zombie.SandboxOptions;
import se.krka.kahlua.vm.KahluaTableIterator;
import java.util.Arrays;
import zombie.util.StringUtils;
import zombie.scripting.objects.Item;
import zombie.scripting.ScriptManager;
import se.krka.kahlua.vm.KahluaUtil;
import zombie.util.Type;
import zombie.debug.DebugLog;
import java.util.Iterator;
import java.util.Map;
import zombie.Lua.LuaManager;
import se.krka.kahlua.j2se.KahluaTableImpl;
import gnu.trove.map.hash.THashMap;
import java.util.HashMap;
import java.util.ArrayList;
import zombie.characters.IsoPlayer;

public final class ItemPickerJava
{
    private static IsoPlayer player;
    private static float OtherLootModifier;
    private static float FoodLootModifier;
    private static float CannedFoodLootModifier;
    private static float WeaponLootModifier;
    private static float RangedWeaponLootModifier;
    private static float AmmoLootModifier;
    private static float LiteratureLootModifier;
    private static float SurvivalGearsLootModifier;
    private static float MedicalLootModifier;
    private static float BagLootModifier;
    private static float MechanicsLootModifier;
    public static float zombieDensityCap;
    public static final ArrayList<String> NoContainerFillRooms;
    public static final ArrayList<ItemPickerUpgradeWeapons> WeaponUpgrades;
    public static final HashMap<String, ItemPickerUpgradeWeapons> WeaponUpgradeMap;
    public static final THashMap<String, ItemPickerRoom> rooms;
    public static final THashMap<String, ItemPickerContainer> containers;
    public static final THashMap<String, ItemPickerContainer> ProceduralDistributions;
    public static final THashMap<String, VehicleDistribution> VehicleDistributions;
    
    public static void Parse() {
        ItemPickerJava.rooms.clear();
        ItemPickerJava.NoContainerFillRooms.clear();
        ItemPickerJava.WeaponUpgradeMap.clear();
        ItemPickerJava.WeaponUpgrades.clear();
        ItemPickerJava.containers.clear();
        final Iterator<Map.Entry<Object, V>> iterator = ((KahluaTableImpl)LuaManager.env.rawget((Object)"NoContainerFillRooms")).delegate.entrySet().iterator();
        while (iterator.hasNext()) {
            ItemPickerJava.NoContainerFillRooms.add(iterator.next().getKey().toString());
        }
        for (final Map.Entry<Object, V> entry : ((KahluaTableImpl)LuaManager.env.rawget((Object)"WeaponUpgrades")).delegate.entrySet()) {
            final String string = entry.getKey().toString();
            final ItemPickerUpgradeWeapons itemPickerUpgradeWeapons = new ItemPickerUpgradeWeapons();
            itemPickerUpgradeWeapons.name = string;
            ItemPickerJava.WeaponUpgrades.add(itemPickerUpgradeWeapons);
            ItemPickerJava.WeaponUpgradeMap.put(string, itemPickerUpgradeWeapons);
            final Iterator<Map.Entry<K, Object>> iterator3 = ((KahluaTableImpl)entry.getValue()).delegate.entrySet().iterator();
            while (iterator3.hasNext()) {
                itemPickerUpgradeWeapons.Upgrades.add(iterator3.next().getValue().toString());
            }
        }
        ParseSuburbsDistributions();
        ParseVehicleDistributions();
        ParseProceduralDistributions();
    }
    
    private static void ParseSuburbsDistributions() {
        for (final Map.Entry<Object, V> entry : ((KahluaTableImpl)LuaManager.env.rawget((Object)"SuburbsDistributions")).delegate.entrySet()) {
            final String string = entry.getKey().toString();
            final KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)entry.getValue();
            if (kahluaTableImpl.delegate.containsKey("rolls")) {
                ItemPickerJava.containers.put((Object)string, (Object)ExtractContainersFromLua(kahluaTableImpl));
            }
            else {
                final ItemPickerRoom itemPickerRoom = new ItemPickerRoom();
                ItemPickerJava.rooms.put((Object)string, (Object)itemPickerRoom);
                for (final Map.Entry<Object, V> entry2 : kahluaTableImpl.delegate.entrySet()) {
                    final String string2 = entry2.getKey().toString();
                    if (entry2.getValue() instanceof Double) {
                        itemPickerRoom.fillRand = ((Double)entry2.getValue()).intValue();
                    }
                    else if ("isShop".equals(string2)) {
                        itemPickerRoom.isShop = (boolean)entry2.getValue();
                    }
                    else {
                        KahluaTableImpl kahluaTableImpl2 = null;
                        try {
                            kahluaTableImpl2 = (KahluaTableImpl)entry2.getValue();
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        if (!kahluaTableImpl2.delegate.containsKey("procedural") && (string2.isEmpty() || !kahluaTableImpl2.delegate.containsKey("rolls") || !kahluaTableImpl2.delegate.containsKey("items"))) {
                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, string));
                        }
                        else {
                            itemPickerRoom.Containers.put((Object)string2, (Object)ExtractContainersFromLua(kahluaTableImpl2));
                        }
                    }
                }
            }
        }
    }
    
    private static void ParseVehicleDistributions() {
        ItemPickerJava.VehicleDistributions.clear();
        final KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget((Object)"VehicleDistributions");
        if (kahluaTableImpl == null || !(kahluaTableImpl.rawget(1) instanceof KahluaTableImpl)) {
            return;
        }
        for (final Map.Entry<K, KahluaTableImpl> entry : ((KahluaTableImpl)kahluaTableImpl.rawget(1)).delegate.entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof KahluaTableImpl) {
                final KahluaTableImpl kahluaTableImpl2 = entry.getValue();
                final VehicleDistribution vehicleDistribution = new VehicleDistribution();
                if (kahluaTableImpl2.rawget((Object)"Normal") instanceof KahluaTableImpl) {
                    final KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)kahluaTableImpl2.rawget((Object)"Normal");
                    final ItemPickerRoom normal = new ItemPickerRoom();
                    for (final Map.Entry<Object, V> entry2 : kahluaTableImpl3.delegate.entrySet()) {
                        normal.Containers.put((Object)entry2.getKey().toString(), (Object)ExtractContainersFromLua((KahluaTableImpl)entry2.getValue()));
                    }
                    vehicleDistribution.Normal = normal;
                }
                if (kahluaTableImpl2.rawget((Object)"Specific") instanceof KahluaTableImpl) {
                    final KahluaTableImpl kahluaTableImpl4 = (KahluaTableImpl)kahluaTableImpl2.rawget((Object)"Specific");
                    for (int i = 1; i <= kahluaTableImpl4.len(); ++i) {
                        final KahluaTableImpl kahluaTableImpl5 = (KahluaTableImpl)kahluaTableImpl4.rawget(i);
                        final ItemPickerRoom e = new ItemPickerRoom();
                        for (final Map.Entry<K, String> entry3 : kahluaTableImpl5.delegate.entrySet()) {
                            final String string = entry3.getKey().toString();
                            if (string.equals("specificId")) {
                                e.specificId = entry3.getValue();
                            }
                            else {
                                e.Containers.put((Object)string, (Object)ExtractContainersFromLua((KahluaTableImpl)entry3.getValue()));
                            }
                        }
                        vehicleDistribution.Specific.add(e);
                    }
                }
                if (vehicleDistribution.Normal == null) {
                    continue;
                }
                ItemPickerJava.VehicleDistributions.put((Object)entry.getKey(), (Object)vehicleDistribution);
            }
        }
    }
    
    private static void ParseProceduralDistributions() {
        ItemPickerJava.ProceduralDistributions.clear();
        final KahluaTableImpl kahluaTableImpl = Type.tryCastTo(LuaManager.env.rawget((Object)"ProceduralDistributions"), KahluaTableImpl.class);
        if (kahluaTableImpl == null) {
            return;
        }
        final KahluaTableImpl kahluaTableImpl2 = Type.tryCastTo(kahluaTableImpl.rawget((Object)"list"), KahluaTableImpl.class);
        if (kahluaTableImpl2 == null) {
            return;
        }
        for (final Map.Entry<Object, V> entry : kahluaTableImpl2.delegate.entrySet()) {
            ItemPickerJava.ProceduralDistributions.put((Object)entry.getKey().toString(), (Object)ExtractContainersFromLua((KahluaTableImpl)entry.getValue()));
        }
    }
    
    private static ItemPickerContainer ExtractContainersFromLua(final KahluaTableImpl kahluaTableImpl) {
        final ItemPickerContainer itemPickerContainer = new ItemPickerContainer();
        if (kahluaTableImpl.delegate.containsKey("procedural")) {
            itemPickerContainer.procedural = kahluaTableImpl.rawgetBool((Object)"procedural");
            itemPickerContainer.proceduralItems = ExtractProcList(kahluaTableImpl);
            return itemPickerContainer;
        }
        if (kahluaTableImpl.delegate.containsKey("noAutoAge")) {
            itemPickerContainer.noAutoAge = kahluaTableImpl.rawgetBool((Object)"noAutoAge");
        }
        if (kahluaTableImpl.delegate.containsKey("fillRand")) {
            itemPickerContainer.fillRand = kahluaTableImpl.rawgetInt((Object)"fillRand");
        }
        if (kahluaTableImpl.delegate.containsKey("maxMap")) {
            itemPickerContainer.maxMap = kahluaTableImpl.rawgetInt((Object)"maxMap");
        }
        if (kahluaTableImpl.delegate.containsKey("stashChance")) {
            itemPickerContainer.stashChance = kahluaTableImpl.rawgetInt((Object)"stashChance");
        }
        if (kahluaTableImpl.delegate.containsKey("dontSpawnAmmo")) {
            itemPickerContainer.dontSpawnAmmo = kahluaTableImpl.rawgetBool((Object)"dontSpawnAmmo");
        }
        if (kahluaTableImpl.delegate.containsKey("ignoreZombieDensity")) {
            itemPickerContainer.ignoreZombieDensity = kahluaTableImpl.rawgetBool((Object)"ignoreZombieDensity");
        }
        final double doubleValue = kahluaTableImpl.delegate.get("rolls");
        if (kahluaTableImpl.delegate.containsKey("junk")) {
            itemPickerContainer.junk = ExtractContainersFromLua((KahluaTableImpl)kahluaTableImpl.rawget((Object)"junk"));
        }
        itemPickerContainer.rolls = (float)(int)doubleValue;
        final KahluaTableImpl kahluaTableImpl2 = kahluaTableImpl.delegate.get("items");
        final ArrayList<ItemPickerItem> list = new ArrayList<ItemPickerItem>();
        for (int len = kahluaTableImpl2.len(), i = 0; i < len; i += 2) {
            final String itemName = Type.tryCastTo(kahluaTableImpl2.delegate.get(KahluaUtil.toDouble((long)(i + 1))), String.class);
            final Double n = Type.tryCastTo(kahluaTableImpl2.delegate.get(KahluaUtil.toDouble((long)(i + 2))), Double.class);
            if (itemName != null) {
                if (n != null) {
                    final Item findItem = ScriptManager.instance.FindItem(itemName);
                    if (findItem == null || findItem.OBSOLETE) {
                        DebugLog.General.warn("ignoring invalid ItemPicker item type \"%s\"", itemName);
                    }
                    else {
                        final ItemPickerItem e = new ItemPickerItem();
                        e.itemName = itemName;
                        e.chance = n.floatValue();
                        list.add(e);
                    }
                }
            }
        }
        itemPickerContainer.Items = list.toArray(itemPickerContainer.Items);
        return itemPickerContainer;
    }
    
    private static ArrayList<ProceduralItem> ExtractProcList(final KahluaTableImpl kahluaTableImpl) {
        final ArrayList<ProceduralItem> list = new ArrayList<ProceduralItem>();
        final KahluaTableIterator iterator = ((KahluaTableImpl)kahluaTableImpl.rawget((Object)"procList")).iterator();
        while (iterator.advance()) {
            final KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)iterator.getValue();
            final ProceduralItem e = new ProceduralItem();
            e.name = kahluaTableImpl2.rawgetStr((Object)"name");
            e.min = kahluaTableImpl2.rawgetInt((Object)"min");
            e.max = kahluaTableImpl2.rawgetInt((Object)"max");
            e.weightChance = kahluaTableImpl2.rawgetInt((Object)"weightChance");
            final String rawgetStr = kahluaTableImpl2.rawgetStr((Object)"forceForItems");
            final String rawgetStr2 = kahluaTableImpl2.rawgetStr((Object)"forceForZones");
            final String rawgetStr3 = kahluaTableImpl2.rawgetStr((Object)"forceForTiles");
            final String rawgetStr4 = kahluaTableImpl2.rawgetStr((Object)"forceForRooms");
            if (!StringUtils.isNullOrWhitespace(rawgetStr)) {
                e.forceForItems = Arrays.asList(rawgetStr.split(";"));
            }
            if (!StringUtils.isNullOrWhitespace(rawgetStr2)) {
                e.forceForZones = Arrays.asList(rawgetStr2.split(";"));
            }
            if (!StringUtils.isNullOrWhitespace(rawgetStr3)) {
                e.forceForTiles = Arrays.asList(rawgetStr3.split(";"));
            }
            if (!StringUtils.isNullOrWhitespace(rawgetStr4)) {
                e.forceForRooms = Arrays.asList(rawgetStr4.split(";"));
            }
            list.add(e);
        }
        return list;
    }
    
    public static void InitSandboxLootSettings() {
        ItemPickerJava.OtherLootModifier = doSandboxSettings(SandboxOptions.getInstance().OtherLoot.getValue());
        ItemPickerJava.FoodLootModifier = doSandboxSettings(SandboxOptions.getInstance().FoodLoot.getValue());
        ItemPickerJava.WeaponLootModifier = doSandboxSettings(SandboxOptions.getInstance().WeaponLoot.getValue());
        ItemPickerJava.RangedWeaponLootModifier = doSandboxSettings(SandboxOptions.getInstance().RangedWeaponLoot.getValue());
        ItemPickerJava.AmmoLootModifier = doSandboxSettings(SandboxOptions.getInstance().AmmoLoot.getValue());
        ItemPickerJava.CannedFoodLootModifier = doSandboxSettings(SandboxOptions.getInstance().CannedFoodLoot.getValue());
        ItemPickerJava.LiteratureLootModifier = doSandboxSettings(SandboxOptions.getInstance().LiteratureLoot.getValue());
        ItemPickerJava.SurvivalGearsLootModifier = doSandboxSettings(SandboxOptions.getInstance().SurvivalGearsLoot.getValue());
        ItemPickerJava.MedicalLootModifier = doSandboxSettings(SandboxOptions.getInstance().MedicalLoot.getValue());
        ItemPickerJava.MechanicsLootModifier = doSandboxSettings(SandboxOptions.getInstance().MechanicsLoot.getValue());
    }
    
    private static float doSandboxSettings(final int n) {
        switch (n) {
            case 1: {
                return 0.2f;
            }
            case 2: {
                return 0.6f;
            }
            case 3: {
                return 1.0f;
            }
            case 4: {
                return 2.0f;
            }
            case 5: {
                return 3.0f;
            }
            default: {
                return 0.6f;
            }
        }
    }
    
    public static void fillContainer(final ItemContainer itemContainer, final IsoPlayer isoPlayer) {
        if (GameClient.bClient || "Tutorial".equals(Core.GameMode)) {
            return;
        }
        if (itemContainer == null) {
            return;
        }
        final IsoGridSquare sourceGrid = itemContainer.getSourceGrid();
        if (sourceGrid == null) {
            return;
        }
        final IsoRoom room = sourceGrid.getRoom();
        if (itemContainer.getType().equals("inventorymale") || itemContainer.getType().equals("inventoryfemale")) {
            String s = itemContainer.getType();
            if (itemContainer.getParent() != null && itemContainer.getParent() instanceof IsoDeadBody) {
                s = ((IsoDeadBody)itemContainer.getParent()).getOutfitName();
            }
            for (int i = 0; i < itemContainer.getItems().size(); ++i) {
                if (itemContainer.getItems().get(i) instanceof InventoryContainer) {
                    final ItemPickerContainer itemPickerContainer = (ItemPickerContainer)ItemPickerJava.containers.get((Object)itemContainer.getItems().get(i).getType());
                    if (itemPickerContainer != null && Rand.Next(itemPickerContainer.fillRand) == 0) {
                        rollContainerItem((InventoryContainer)itemContainer.getItems().get(i), null, (ItemPickerContainer)ItemPickerJava.containers.get((Object)itemContainer.getItems().get(i).getType()));
                    }
                }
            }
            ItemPickerContainer itemPickerContainer2 = (ItemPickerContainer)((ItemPickerRoom)ItemPickerJava.rooms.get((Object)"all")).Containers.get(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            if (itemPickerContainer2 == null) {
                itemPickerContainer2 = (ItemPickerContainer)((ItemPickerRoom)ItemPickerJava.rooms.get((Object)"all")).Containers.get((Object)itemContainer.getType());
            }
            rollItem(itemPickerContainer2, itemContainer, true, isoPlayer, null);
            return;
        }
        ItemPickerRoom itemPickerRoom = null;
        if (ItemPickerJava.rooms.containsKey((Object)"all")) {
            itemPickerRoom = (ItemPickerRoom)ItemPickerJava.rooms.get((Object)"all");
        }
        if (room == null || !ItemPickerJava.rooms.containsKey((Object)room.getName())) {
            String name;
            if (room != null) {
                name = room.getName();
            }
            else {
                name = "all";
            }
            fillContainerType(itemPickerRoom, itemContainer, name, isoPlayer);
            LuaEventManager.triggerEvent("OnFillContainer", name, itemContainer.getType(), itemContainer);
            return;
        }
        String name2 = room.getName();
        final ItemPickerRoom itemPickerRoom2 = (ItemPickerRoom)ItemPickerJava.rooms.get((Object)name2);
        ItemPickerContainer itemPickerContainer3 = null;
        if (itemPickerRoom2.Containers.containsKey((Object)itemContainer.getType())) {
            itemPickerContainer3 = (ItemPickerContainer)itemPickerRoom2.Containers.get((Object)itemContainer.getType());
        }
        if (itemPickerContainer3 == null && itemPickerRoom2.Containers.containsKey((Object)"other")) {
            itemPickerContainer3 = (ItemPickerContainer)itemPickerRoom2.Containers.get((Object)"other");
        }
        if (itemPickerContainer3 == null && itemPickerRoom2.Containers.containsKey((Object)"all")) {
            itemPickerContainer3 = (ItemPickerContainer)itemPickerRoom2.Containers.get((Object)"all");
            name2 = "all";
        }
        if (itemPickerContainer3 == null) {
            fillContainerType(itemPickerRoom, itemContainer, name2, isoPlayer);
            LuaEventManager.triggerEvent("OnFillContainer", name2, itemContainer.getType(), itemContainer);
            return;
        }
        if (ItemPickerJava.rooms.containsKey((Object)room.getName())) {
            itemPickerRoom = (ItemPickerRoom)ItemPickerJava.rooms.get((Object)room.getName());
        }
        if (itemPickerRoom != null) {
            fillContainerType(itemPickerRoom, itemContainer, room.getName(), isoPlayer);
            LuaEventManager.triggerEvent("OnFillContainer", room.getName(), itemContainer.getType(), itemContainer);
        }
    }
    
    public static void fillContainerType(final ItemPickerRoom itemPickerRoom, final ItemContainer itemContainer, final String o, final IsoGameCharacter isoGameCharacter) {
        boolean b = true;
        if (ItemPickerJava.NoContainerFillRooms.contains(o)) {
            b = false;
        }
        if (itemPickerRoom.Containers.containsKey((Object)"all")) {
            rollItem((ItemPickerContainer)itemPickerRoom.Containers.get((Object)"all"), itemContainer, b, isoGameCharacter, itemPickerRoom);
        }
        ItemPickerContainer itemPickerContainer = (ItemPickerContainer)itemPickerRoom.Containers.get((Object)itemContainer.getType());
        if (itemPickerContainer == null) {
            itemPickerContainer = (ItemPickerContainer)itemPickerRoom.Containers.get((Object)"other");
        }
        if (itemPickerContainer != null) {
            rollItem(itemPickerContainer, itemContainer, b, isoGameCharacter, itemPickerRoom);
        }
    }
    
    public static InventoryItem tryAddItemToContainer(final ItemContainer itemContainer, final String s, final ItemPickerContainer itemPickerContainer) {
        final Item findItem = ScriptManager.instance.FindItem(s);
        if (findItem == null) {
            return null;
        }
        if (findItem.OBSOLETE) {
            return null;
        }
        final float n = findItem.getActualWeight() * findItem.getCount();
        if (!itemContainer.hasRoomFor(null, n)) {
            return null;
        }
        if (itemContainer.getContainingItem() instanceof InventoryContainer) {
            final ItemContainer container = itemContainer.getContainingItem().getContainer();
            if (container != null && !container.hasRoomFor(null, n)) {
                return null;
            }
        }
        return itemContainer.AddItem(s);
    }
    
    private static void rollProceduralItem(final ArrayList<ProceduralItem> list, final ItemContainer itemContainer, final float n, final IsoGameCharacter isoGameCharacter, final ItemPickerRoom itemPickerRoom) {
        if (itemContainer.getSourceGrid() == null || itemContainer.getSourceGrid().getRoom() == null) {
            return;
        }
        final HashMap<String, Integer> proceduralSpawnedContainer = itemContainer.getSourceGrid().getRoom().getRoomDef().getProceduralSpawnedContainer();
        final HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        final HashMap<String, Integer> hashMap2 = new HashMap<String, Integer>();
        for (int i = 0; i < list.size(); ++i) {
            final ProceduralItem proceduralItem = list.get(i);
            final String name = proceduralItem.name;
            final int min = proceduralItem.min;
            final int max = proceduralItem.max;
            final int weightChance = proceduralItem.weightChance;
            final List<String> forceForItems = proceduralItem.forceForItems;
            final List<String> forceForZones = proceduralItem.forceForZones;
            final List<String> forceForTiles = proceduralItem.forceForTiles;
            final List<String> forceForRooms = proceduralItem.forceForRooms;
            if (proceduralSpawnedContainer.get(name) == null) {
                proceduralSpawnedContainer.put(name, 0);
            }
            if (forceForItems != null) {
                for (int j = itemContainer.getSourceGrid().getRoom().getRoomDef().x; j < itemContainer.getSourceGrid().getRoom().getRoomDef().x2; ++j) {
                    for (int k = itemContainer.getSourceGrid().getRoom().getRoomDef().y; k < itemContainer.getSourceGrid().getRoom().getRoomDef().y2; ++k) {
                        final IsoGridSquare gridSquare = itemContainer.getSourceGrid().getCell().getGridSquare(j, k, itemContainer.getSourceGrid().z);
                        if (gridSquare != null) {
                            for (int l = 0; l < gridSquare.getObjects().size(); ++l) {
                                if (forceForItems.contains(gridSquare.getObjects().get(l).getSprite().name)) {
                                    hashMap.clear();
                                    hashMap.put(name, -1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            else if (forceForZones != null) {
                final ArrayList<IsoMetaGrid.Zone> zones = IsoWorld.instance.MetaGrid.getZonesAt(itemContainer.getSourceGrid().x, itemContainer.getSourceGrid().y, 0);
                for (int n2 = 0; n2 < zones.size(); ++n2) {
                    if (proceduralSpawnedContainer.get(name) < max && (forceForZones.contains(zones.get(n2).type) || forceForZones.contains(zones.get(n2).name))) {
                        hashMap.clear();
                        hashMap.put(name, -1);
                        break;
                    }
                }
            }
            else if (forceForTiles != null) {
                final IsoGridSquare sourceGrid = itemContainer.getSourceGrid();
                if (sourceGrid != null) {
                    for (int n3 = 0; n3 < sourceGrid.getObjects().size(); ++n3) {
                        final IsoObject isoObject = sourceGrid.getObjects().get(n3);
                        if (isoObject.getSprite() != null && forceForTiles.contains(isoObject.getSprite().getName())) {
                            hashMap.clear();
                            hashMap.put(name, -1);
                            break;
                        }
                    }
                }
            }
            else if (forceForRooms != null) {
                final IsoGridSquare sourceGrid2 = itemContainer.getSourceGrid();
                if (sourceGrid2 != null) {
                    for (int n4 = 0; n4 < forceForRooms.size(); ++n4) {
                        if (sourceGrid2.getBuilding().getRandomRoom(forceForRooms.get(n4)) != null) {
                            hashMap.clear();
                            hashMap.put(name, -1);
                            break;
                        }
                    }
                }
            }
            if (forceForItems == null && forceForZones == null && forceForTiles == null && forceForRooms == null) {
                if (min == 1 && proceduralSpawnedContainer.get(name) == 0) {
                    hashMap.put(name, weightChance);
                }
                else if (proceduralSpawnedContainer.get(name) < max) {
                    hashMap2.put(name, weightChance);
                }
            }
        }
        String s = null;
        if (!hashMap.isEmpty()) {
            s = getDistribInHashMap(hashMap);
        }
        else if (!hashMap2.isEmpty()) {
            s = getDistribInHashMap(hashMap2);
        }
        if (s == null) {
            return;
        }
        final ItemPickerContainer itemPickerContainer = (ItemPickerContainer)ItemPickerJava.ProceduralDistributions.get((Object)s);
        if (itemPickerContainer == null) {
            return;
        }
        if (itemPickerContainer.junk != null) {
            doRollItem(itemPickerContainer.junk, itemContainer, n, isoGameCharacter, true, true, itemPickerRoom);
        }
        doRollItem(itemPickerContainer, itemContainer, n, isoGameCharacter, true, false, itemPickerRoom);
        proceduralSpawnedContainer.put(s, proceduralSpawnedContainer.get(s) + 1);
    }
    
    private static String getDistribInHashMap(final HashMap<String, Integer> hashMap) {
        int n = 0;
        int n2 = 0;
        final Iterator<String> iterator = hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            n += hashMap.get(iterator.next());
        }
        if (n == -1) {
            final int next = Rand.Next(hashMap.size());
            final Iterator<String> iterator2 = hashMap.keySet().iterator();
            int n3 = 0;
            while (iterator2.hasNext()) {
                if (n3 == next) {
                    return iterator2.next();
                }
                ++n3;
            }
        }
        final int next2 = Rand.Next(n);
        for (final String key : hashMap.keySet()) {
            n2 += hashMap.get(key);
            if (n2 >= next2) {
                return key;
            }
        }
        return null;
    }
    
    public static void rollItem(final ItemPickerContainer itemPickerContainer, final ItemContainer itemContainer, final boolean b, final IsoGameCharacter isoGameCharacter, final ItemPickerRoom itemPickerRoom) {
        if (!GameClient.bClient && !GameServer.bServer) {
            ItemPickerJava.player = IsoPlayer.getInstance();
        }
        if (itemPickerContainer != null && itemContainer != null) {
            float n = 0.0f;
            IsoMetaChunk metaChunk = null;
            if (ItemPickerJava.player != null && IsoWorld.instance != null) {
                metaChunk = IsoWorld.instance.getMetaChunk((int)ItemPickerJava.player.getX() / 10, (int)ItemPickerJava.player.getY() / 10);
            }
            if (metaChunk != null) {
                n = metaChunk.getLootZombieIntensity();
            }
            if (n > ItemPickerJava.zombieDensityCap) {
                n = ItemPickerJava.zombieDensityCap;
            }
            if (itemPickerContainer.ignoreZombieDensity) {
                n = 0.0f;
            }
            if (itemPickerContainer.procedural) {
                rollProceduralItem(itemPickerContainer.proceduralItems, itemContainer, n, isoGameCharacter, itemPickerRoom);
            }
            else {
                if (itemPickerContainer.junk != null) {
                    doRollItem(itemPickerContainer.junk, itemContainer, n, isoGameCharacter, b, true, itemPickerRoom);
                }
                doRollItem(itemPickerContainer, itemContainer, n, isoGameCharacter, b, false, itemPickerRoom);
            }
        }
    }
    
    public static void doRollItem(final ItemPickerContainer itemPickerContainer, final ItemContainer itemContainer, final float n, final IsoGameCharacter isoGameCharacter, final boolean b, final boolean b2, final ItemPickerRoom itemPickerRoom) {
        boolean set = false;
        boolean set2 = false;
        if (ItemPickerJava.player != null && isoGameCharacter != null) {
            set = isoGameCharacter.Traits.Lucky.isSet();
            set2 = isoGameCharacter.Traits.Unlucky.isSet();
        }
        for (int n2 = 0; n2 < itemPickerContainer.rolls; ++n2) {
            final ItemPickerItem[] items = itemPickerContainer.Items;
            for (int i = 0; i < items.length; ++i) {
                final ItemPickerItem itemPickerItem = items[i];
                float chance = itemPickerItem.chance;
                final String itemName = itemPickerItem.itemName;
                if (set) {
                    chance *= 1.1f;
                }
                if (set2) {
                    chance *= 0.9f;
                }
                float lootModifier = getLootModifier(itemName);
                if (b2) {
                    lootModifier = 1.0f;
                    chance *= (float)1.4;
                }
                if (Rand.Next(10000) <= chance * 100.0f * lootModifier + n * 10.0f) {
                    final InventoryItem tryAddItemToContainer = tryAddItemToContainer(itemContainer, itemName, itemPickerContainer);
                    if (tryAddItemToContainer == null) {
                        return;
                    }
                    checkStashItem(tryAddItemToContainer, itemPickerContainer);
                    if (itemContainer.getType().equals("freezer") && tryAddItemToContainer instanceof Food && ((Food)tryAddItemToContainer).isFreezing()) {
                        ((Food)tryAddItemToContainer).freeze();
                    }
                    if (tryAddItemToContainer instanceof Key) {
                        final Key key = (Key)tryAddItemToContainer;
                        key.takeKeyId();
                        if (!key.getFullType().equals("Base.Padlock")) {
                            key.setName(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, Translator.getText("IGUI_HouseKey"), key.getKeyId()));
                        }
                        if (itemContainer.getSourceGrid() != null && itemContainer.getSourceGrid().getBuilding() != null && itemContainer.getSourceGrid().getBuilding().getDef() != null) {
                            final int keySpawned = itemContainer.getSourceGrid().getBuilding().getDef().getKeySpawned();
                            if (keySpawned < 2) {
                                itemContainer.getSourceGrid().getBuilding().getDef().setKeySpawned(keySpawned + 1);
                            }
                            else {
                                itemContainer.Remove(tryAddItemToContainer);
                            }
                        }
                    }
                    if (ItemPickerJava.WeaponUpgradeMap.containsKey(tryAddItemToContainer.getType())) {
                        DoWeaponUpgrade(tryAddItemToContainer);
                    }
                    if (!itemPickerContainer.noAutoAge) {
                        tryAddItemToContainer.setAutoAge();
                    }
                    boolean isShop = false;
                    if (itemPickerRoom != null) {
                        isShop = itemPickerRoom.isShop;
                    }
                    if (!isShop && Rand.Next(100) < 40 && tryAddItemToContainer instanceof DrainableComboItem) {
                        ((DrainableComboItem)tryAddItemToContainer).setUsedDelta(Rand.Next(1.0f, 1.0f / ((DrainableComboItem)tryAddItemToContainer).getUseDelta() - 1.0f) * ((DrainableComboItem)tryAddItemToContainer).getUseDelta());
                    }
                    if (!isShop && tryAddItemToContainer instanceof HandWeapon && Rand.Next(100) < 40) {
                        tryAddItemToContainer.setCondition(Rand.Next(1, tryAddItemToContainer.getConditionMax()));
                    }
                    if (tryAddItemToContainer instanceof HandWeapon && !itemPickerContainer.dontSpawnAmmo && Rand.Next(100) < 90) {
                        int next = 30;
                        final HandWeapon handWeapon = (HandWeapon)tryAddItemToContainer;
                        if (Core.getInstance().getOptionReloadDifficulty() > 1 && !StringUtils.isNullOrEmpty(handWeapon.getMagazineType()) && Rand.Next(100) < 90) {
                            if (Rand.NextBool(3)) {
                                final InventoryItem addItem = itemContainer.AddItem(handWeapon.getMagazineType());
                                if (Rand.NextBool(5)) {
                                    addItem.setCurrentAmmoCount(Rand.Next(1, addItem.getMaxAmmo()));
                                }
                                if (!Rand.NextBool(5)) {
                                    addItem.setCurrentAmmoCount(addItem.getMaxAmmo());
                                }
                            }
                            else {
                                if (!StringUtils.isNullOrWhitespace(handWeapon.getMagazineType())) {
                                    handWeapon.setContainsClip(true);
                                }
                                if (Rand.NextBool(6)) {
                                    handWeapon.setCurrentAmmoCount(Rand.Next(1, handWeapon.getMaxAmmo()));
                                }
                                else {
                                    next = Rand.Next(60, 100);
                                }
                            }
                            if (handWeapon.haveChamber()) {
                                handWeapon.setRoundChambered(true);
                            }
                        }
                        if (Core.getInstance().getOptionReloadDifficulty() == 1 || (StringUtils.isNullOrEmpty(handWeapon.getMagazineType()) && Rand.Next(100) < 30)) {
                            handWeapon.setCurrentAmmoCount(Rand.Next(1, handWeapon.getMaxAmmo()));
                            if (handWeapon.haveChamber()) {
                                handWeapon.setRoundChambered(true);
                            }
                        }
                        if (!StringUtils.isNullOrEmpty(handWeapon.getAmmoBox()) && Rand.Next(100) < next) {
                            itemContainer.AddItem(handWeapon.getAmmoBox());
                        }
                        else if (!StringUtils.isNullOrEmpty(handWeapon.getAmmoType()) && Rand.Next(100) < 50) {
                            itemContainer.AddItems(handWeapon.getAmmoType(), Rand.Next(1, 5));
                        }
                    }
                    if (tryAddItemToContainer instanceof InventoryContainer && ItemPickerJava.containers.containsKey((Object)tryAddItemToContainer.getType())) {
                        final ItemPickerContainer itemPickerContainer2 = (ItemPickerContainer)ItemPickerJava.containers.get((Object)tryAddItemToContainer.getType());
                        if (b && Rand.Next(itemPickerContainer2.fillRand) == 0) {
                            rollContainerItem((InventoryContainer)tryAddItemToContainer, isoGameCharacter, (ItemPickerContainer)ItemPickerJava.containers.get((Object)tryAddItemToContainer.getType()));
                        }
                    }
                }
            }
        }
    }
    
    private static void checkStashItem(final InventoryItem inventoryItem, final ItemPickerContainer itemPickerContainer) {
        if (itemPickerContainer.stashChance > 0 && inventoryItem instanceof MapItem && !StringUtils.isNullOrEmpty(((MapItem)inventoryItem).getMapID())) {
            inventoryItem.setStashChance(itemPickerContainer.stashChance);
        }
        StashSystem.checkStashItem(inventoryItem);
    }
    
    public static void rollContainerItem(final InventoryContainer inventoryContainer, final IsoGameCharacter isoGameCharacter, final ItemPickerContainer itemPickerContainer) {
        if (itemPickerContainer != null) {
            final ItemContainer inventory = inventoryContainer.getInventory();
            float n = 0.0f;
            IsoMetaChunk metaChunk = null;
            if (ItemPickerJava.player != null && IsoWorld.instance != null) {
                metaChunk = IsoWorld.instance.getMetaChunk((int)ItemPickerJava.player.getX() / 10, (int)ItemPickerJava.player.getY() / 10);
            }
            if (metaChunk != null) {
                n = metaChunk.getLootZombieIntensity();
            }
            if (n > ItemPickerJava.zombieDensityCap) {
                n = ItemPickerJava.zombieDensityCap;
            }
            if (itemPickerContainer.ignoreZombieDensity) {
                n = 0.0f;
            }
            boolean set = false;
            boolean set2 = false;
            if (ItemPickerJava.player != null && isoGameCharacter != null) {
                set = isoGameCharacter.Traits.Lucky.isSet();
                set2 = isoGameCharacter.Traits.Unlucky.isSet();
            }
            for (int n2 = 0; n2 < itemPickerContainer.rolls; ++n2) {
                final ItemPickerItem[] items = itemPickerContainer.Items;
                for (int i = 0; i < items.length; ++i) {
                    final ItemPickerItem itemPickerItem = items[i];
                    float chance = itemPickerItem.chance;
                    final String itemName = itemPickerItem.itemName;
                    if (set) {
                        chance *= 1.1f;
                    }
                    if (set2) {
                        chance *= 0.9f;
                    }
                    if (Rand.Next(10000) <= chance * 100.0f * getLootModifier(itemName) + n * 10.0f) {
                        final InventoryItem tryAddItemToContainer = tryAddItemToContainer(inventory, itemName, itemPickerContainer);
                        if (tryAddItemToContainer == null) {
                            return;
                        }
                        final MapItem mapItem = Type.tryCastTo(tryAddItemToContainer, MapItem.class);
                        if (mapItem != null && !StringUtils.isNullOrEmpty(mapItem.getMapID()) && itemPickerContainer.maxMap > 0) {
                            int n3 = 0;
                            for (int j = 0; j < inventory.getItems().size(); ++j) {
                                final MapItem mapItem2 = Type.tryCastTo(inventory.getItems().get(j), MapItem.class);
                                if (mapItem2 != null && !StringUtils.isNullOrEmpty(mapItem2.getMapID())) {
                                    ++n3;
                                }
                            }
                            if (n3 > itemPickerContainer.maxMap) {
                                inventory.Remove(tryAddItemToContainer);
                            }
                        }
                        checkStashItem(tryAddItemToContainer, itemPickerContainer);
                        if (inventory.getType().equals("freezer") && tryAddItemToContainer instanceof Food && ((Food)tryAddItemToContainer).isFreezing()) {
                            ((Food)tryAddItemToContainer).freeze();
                        }
                        if (tryAddItemToContainer instanceof Key) {
                            final Key key = (Key)tryAddItemToContainer;
                            key.takeKeyId();
                            if (!key.getFullType().equals("Base.Padlock")) {
                                key.setName(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, Translator.getText("IGUI_HouseKey"), key.getKeyId()));
                            }
                            if (inventory.getSourceGrid() != null && inventory.getSourceGrid().getBuilding() != null && inventory.getSourceGrid().getBuilding().getDef() != null) {
                                final int keySpawned = inventory.getSourceGrid().getBuilding().getDef().getKeySpawned();
                                if (keySpawned < 2) {
                                    inventory.getSourceGrid().getBuilding().getDef().setKeySpawned(keySpawned + 1);
                                }
                                else {
                                    inventory.Remove(tryAddItemToContainer);
                                }
                            }
                        }
                        if (!inventory.getType().equals("freezer")) {
                            tryAddItemToContainer.setAutoAge();
                        }
                    }
                }
            }
        }
    }
    
    private static void DoWeaponUpgrade(final InventoryItem inventoryItem) {
        final ItemPickerUpgradeWeapons itemPickerUpgradeWeapons = ItemPickerJava.WeaponUpgradeMap.get(inventoryItem.getType());
        if (itemPickerUpgradeWeapons == null) {
            return;
        }
        if (itemPickerUpgradeWeapons.Upgrades.size() == 0) {
            return;
        }
        for (int next = Rand.Next(itemPickerUpgradeWeapons.Upgrades.size()), i = 0; i < next; ++i) {
            ((HandWeapon)inventoryItem).attachWeaponPart((WeaponPart)InventoryItemFactory.CreateItem(PZArrayUtil.pickRandom(itemPickerUpgradeWeapons.Upgrades)));
        }
    }
    
    public static float getLootModifier(final String s) {
        final Item findItem = ScriptManager.instance.FindItem(s);
        if (findItem == null) {
            return 0.6f;
        }
        float n = ItemPickerJava.OtherLootModifier;
        if (findItem.getType() == Item.Type.Food) {
            if (findItem.CannedFood) {
                n = ItemPickerJava.CannedFoodLootModifier;
            }
            else {
                n = ItemPickerJava.FoodLootModifier;
            }
        }
        if ("Ammo".equals(findItem.getDisplayCategory())) {
            n = ItemPickerJava.AmmoLootModifier;
        }
        if (findItem.getType() == Item.Type.Weapon && !findItem.isRanged()) {
            n = ItemPickerJava.WeaponLootModifier;
        }
        if (findItem.getType() == Item.Type.WeaponPart || (findItem.getType() == Item.Type.Weapon && findItem.isRanged()) || (findItem.getType() == Item.Type.Normal && !StringUtils.isNullOrEmpty(findItem.getAmmoType()))) {
            n = ItemPickerJava.RangedWeaponLootModifier;
        }
        if (findItem.getType() == Item.Type.Literature) {
            n = ItemPickerJava.LiteratureLootModifier;
        }
        if (findItem.Medical) {
            n = ItemPickerJava.MedicalLootModifier;
        }
        if (findItem.SurvivalGear) {
            n = ItemPickerJava.SurvivalGearsLootModifier;
        }
        if (findItem.MechanicsItem) {
            n = ItemPickerJava.MechanicsLootModifier;
        }
        return n;
    }
    
    public static void updateOverlaySprite(final IsoObject isoObject) {
        ContainerOverlays.instance.updateContainerOverlaySprite(isoObject);
    }
    
    public static void doOverlaySprite(final IsoGridSquare isoGridSquare) {
        if (GameClient.bClient) {
            return;
        }
        if (isoGridSquare == null || isoGridSquare.getRoom() == null || isoGridSquare.isOverlayDone()) {
            return;
        }
        final PZArrayList<IsoObject> objects = isoGridSquare.getObjects();
        for (int i = 0; i < objects.size(); ++i) {
            final IsoObject isoObject = objects.get(i);
            if (isoObject != null && isoObject.getContainer() != null && !isoObject.getContainer().isExplored()) {
                fillContainer(isoObject.getContainer(), IsoPlayer.getInstance());
                isoObject.getContainer().setExplored(true);
                if (GameServer.bServer) {
                    LuaManager.GlobalObject.sendItemsInContainer(isoObject, isoObject.getContainer());
                }
            }
            updateOverlaySprite(isoObject);
        }
        isoGridSquare.setOverlayDone(true);
    }
    
    public static ItemPickerContainer getItemContainer(final String s, final String s2, final String s3, final boolean b) {
        final ItemPickerRoom itemPickerRoom = (ItemPickerRoom)ItemPickerJava.rooms.get((Object)s);
        if (itemPickerRoom == null) {
            return null;
        }
        final ItemPickerContainer itemPickerContainer = (ItemPickerContainer)itemPickerRoom.Containers.get((Object)s2);
        if (itemPickerContainer != null && itemPickerContainer.procedural) {
            final ArrayList<ProceduralItem> proceduralItems = itemPickerContainer.proceduralItems;
            for (int i = 0; i < proceduralItems.size(); ++i) {
                if (s3.equals(proceduralItems.get(i).name)) {
                    final ItemPickerContainer itemPickerContainer2 = (ItemPickerContainer)ItemPickerJava.ProceduralDistributions.get((Object)s3);
                    if (itemPickerContainer2.junk != null && b) {
                        return itemPickerContainer2.junk;
                    }
                    if (!b) {
                        return itemPickerContainer2;
                    }
                }
            }
        }
        if (b) {
            return itemPickerContainer.junk;
        }
        return itemPickerContainer;
    }
    
    static {
        ItemPickerJava.zombieDensityCap = 8.0f;
        NoContainerFillRooms = new ArrayList<String>();
        WeaponUpgrades = new ArrayList<ItemPickerUpgradeWeapons>();
        WeaponUpgradeMap = new HashMap<String, ItemPickerUpgradeWeapons>();
        rooms = new THashMap();
        containers = new THashMap();
        ProceduralDistributions = new THashMap();
        VehicleDistributions = new THashMap();
    }
    
    public static final class ItemPickerItem
    {
        public String itemName;
        public float chance;
    }
    
    public static final class ItemPickerContainer
    {
        public ItemPickerItem[] Items;
        public float rolls;
        public boolean noAutoAge;
        public int fillRand;
        public int maxMap;
        public int stashChance;
        public ItemPickerContainer junk;
        public boolean procedural;
        public boolean dontSpawnAmmo;
        public boolean ignoreZombieDensity;
        public ArrayList<ProceduralItem> proceduralItems;
        
        public ItemPickerContainer() {
            this.Items = new ItemPickerItem[0];
            this.dontSpawnAmmo = false;
            this.ignoreZombieDensity = false;
        }
    }
    
    public static final class ItemPickerRoom
    {
        public THashMap<String, ItemPickerContainer> Containers;
        public int fillRand;
        public boolean isShop;
        public String specificId;
        
        public ItemPickerRoom() {
            this.Containers = (THashMap<String, ItemPickerContainer>)new THashMap();
            this.specificId = null;
        }
    }
    
    public static final class ItemPickerUpgradeWeapons
    {
        public String name;
        public ArrayList<String> Upgrades;
        
        public ItemPickerUpgradeWeapons() {
            this.Upgrades = new ArrayList<String>();
        }
    }
    
    public static final class ProceduralItem
    {
        public String name;
        public int min;
        public int max;
        public List<String> forceForItems;
        public List<String> forceForZones;
        public List<String> forceForTiles;
        public List<String> forceForRooms;
        public int weightChance;
    }
    
    public static final class VehicleDistribution
    {
        public ItemPickerRoom Normal;
        public final ArrayList<ItemPickerRoom> Specific;
        
        public VehicleDistribution() {
            this.Specific = new ArrayList<ItemPickerRoom>();
        }
    }
}
