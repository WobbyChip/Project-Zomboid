// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.skinnedmodel.visual.BaseVisual;
import zombie.characters.SurvivorDesc;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.inventory.InventoryItem;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;
import zombie.iso.IsoCell;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoObject;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.types.WeaponPart;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;
import zombie.inventory.InventoryItemFactory;
import java.util.Collection;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.ItemContainer;
import zombie.ZombieSpawnRecorder;
import zombie.iso.IsoDirections;
import zombie.VirtualZombieManager;
import zombie.characters.IsoZombie;
import zombie.iso.IsoGridSquare;
import java.util.Iterator;
import zombie.SandboxOptions;
import zombie.core.Core;
import zombie.iso.SpawnPoints;
import zombie.core.Rand;
import java.util.ArrayList;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.RoomDef;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.iso.IsoWorld;
import zombie.iso.BuildingDef;
import java.util.HashMap;
import zombie.randomizedWorld.RandomizedWorldBase;

public class RandomizedBuildingBase extends RandomizedWorldBase
{
    private int chance;
    private static int totalChance;
    private static HashMap<RandomizedBuildingBase, Integer> rbMap;
    protected static final int KBBuildingX = 10744;
    protected static final int KBBuildingY = 9409;
    private boolean alwaysDo;
    private static HashMap<String, String> weaponsList;
    
    public RandomizedBuildingBase() {
        this.chance = 0;
        this.alwaysDo = false;
    }
    
    public void randomizeBuilding(final BuildingDef buildingDef) {
        buildingDef.bAlarmed = false;
    }
    
    public void init() {
        if (!RandomizedBuildingBase.weaponsList.isEmpty()) {
            return;
        }
        RandomizedBuildingBase.weaponsList.put("Base.Shotgun", "Base.ShotgunShellsBox");
        RandomizedBuildingBase.weaponsList.put("Base.Pistol", "Base.Bullets9mmBox");
        RandomizedBuildingBase.weaponsList.put("Base.Pistol2", "Base.Bullets45Box");
        RandomizedBuildingBase.weaponsList.put("Base.Pistol3", "Base.Bullets44Box");
        RandomizedBuildingBase.weaponsList.put("Base.VarmintRifle", "Base.223Box");
        RandomizedBuildingBase.weaponsList.put("Base.HuntingRifle", "Base.308Box");
    }
    
    public static void initAllRBMapChance() {
        for (int i = 0; i < IsoWorld.instance.getRandomizedBuildingList().size(); ++i) {
            RandomizedBuildingBase.totalChance += IsoWorld.instance.getRandomizedBuildingList().get(i).getChance();
            RandomizedBuildingBase.rbMap.put(IsoWorld.instance.getRandomizedBuildingList().get(i), IsoWorld.instance.getRandomizedBuildingList().get(i).getChance());
        }
    }
    
    public boolean isValid(final BuildingDef buildingDef, final boolean b) {
        this.debugLine = "";
        if (GameClient.bClient) {
            return false;
        }
        if (buildingDef.isAllExplored() && !b) {
            return false;
        }
        if (!GameServer.bServer) {
            if (!b && IsoPlayer.getInstance().getSquare() != null && IsoPlayer.getInstance().getSquare().getBuilding() != null && IsoPlayer.getInstance().getSquare().getBuilding().def == buildingDef) {
                this.customizeStartingHouse(IsoPlayer.getInstance().getSquare().getBuilding().def);
                return false;
            }
        }
        else if (!b) {
            for (int i = 0; i < GameServer.Players.size(); ++i) {
                final IsoPlayer isoPlayer = GameServer.Players.get(i);
                if (isoPlayer.getSquare() != null && isoPlayer.getSquare().getBuilding() != null && isoPlayer.getSquare().getBuilding().def == buildingDef) {
                    return false;
                }
            }
        }
        boolean b2 = false;
        boolean b3 = false;
        boolean b4 = false;
        for (int j = 0; j < buildingDef.rooms.size(); ++j) {
            final RoomDef roomDef = buildingDef.rooms.get(j);
            if ("bedroom".equals(roomDef.name)) {
                b2 = true;
            }
            if ("kitchen".equals(roomDef.name) || "livingroom".equals(roomDef.name)) {
                b3 = true;
            }
            if ("bathroom".equals(roomDef.name)) {
                b4 = true;
            }
        }
        if (!b2) {
            this.debugLine = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.debugLine);
        }
        if (!b4) {
            this.debugLine = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.debugLine);
        }
        if (!b3) {
            this.debugLine = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.debugLine);
        }
        return b2 && b4 && b3;
    }
    
    private void customizeStartingHouse(final BuildingDef buildingDef) {
    }
    
    public int getMinimumDays() {
        return this.minimumDays;
    }
    
    public void setMinimumDays(final int minimumDays) {
        this.minimumDays = minimumDays;
    }
    
    public int getMinimumRooms() {
        return this.minimumRooms;
    }
    
    public void setMinimumRooms(final int minimumRooms) {
        this.minimumRooms = minimumRooms;
    }
    
    public static void ChunkLoaded(final IsoBuilding isoBuilding) {
        if (!GameClient.bClient && isoBuilding.def != null && !isoBuilding.def.seen && isoBuilding.def.isFullyStreamedIn()) {
            if (GameServer.bServer && GameServer.Players.isEmpty()) {
                return;
            }
            for (int i = 0; i < isoBuilding.Rooms.size(); ++i) {
                if (isoBuilding.Rooms.get(i).def.bExplored) {
                    return;
                }
            }
            final ArrayList<RandomizedBuildingBase> list = new ArrayList<RandomizedBuildingBase>();
            for (int j = 0; j < IsoWorld.instance.getRandomizedBuildingList().size(); ++j) {
                final RandomizedBuildingBase e = IsoWorld.instance.getRandomizedBuildingList().get(j);
                if (e.isAlwaysDo() && e.isValid(isoBuilding.def, false)) {
                    list.add(e);
                }
            }
            isoBuilding.def.seen = true;
            if (isoBuilding.def.x == 10744 && isoBuilding.def.y == 9409 && Rand.Next(100) < 31) {
                new RBKateAndBaldspot().randomizeBuilding(isoBuilding.def);
                return;
            }
            if (!list.isEmpty()) {
                final RandomizedBuildingBase randomizedBuildingBase = list.get(Rand.Next(0, list.size()));
                if (randomizedBuildingBase != null) {
                    randomizedBuildingBase.randomizeBuilding(isoBuilding.def);
                    return;
                }
            }
            if (GameServer.bServer && SpawnPoints.instance.isSpawnBuilding(isoBuilding.getDef())) {
                return;
            }
            RandomizedBuildingBase randomizedBuildingBase2 = IsoWorld.instance.getRBBasic();
            if ("Tutorial".equals(Core.GameMode)) {
                return;
            }
            try {
                int n = 10;
                switch (SandboxOptions.instance.SurvivorHouseChance.getValue()) {
                    case 1: {
                        return;
                    }
                    case 2: {
                        n -= 5;
                        break;
                    }
                    case 4: {
                        n += 5;
                        break;
                    }
                    case 5: {
                        n += 10;
                        break;
                    }
                    case 6: {
                        n += 20;
                        break;
                    }
                }
                if (Rand.Next(100) <= n) {
                    if (RandomizedBuildingBase.totalChance == 0) {
                        initAllRBMapChance();
                    }
                    randomizedBuildingBase2 = getRandomStory();
                    if (randomizedBuildingBase2 == null) {
                        return;
                    }
                }
                if (randomizedBuildingBase2.isValid(isoBuilding.def, false) && randomizedBuildingBase2.isTimeValid(false)) {
                    randomizedBuildingBase2.randomizeBuilding(isoBuilding.def);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public int getChance() {
        return this.chance;
    }
    
    public void setChance(final int chance) {
        this.chance = chance;
    }
    
    public boolean isAlwaysDo() {
        return this.alwaysDo;
    }
    
    public void setAlwaysDo(final boolean alwaysDo) {
        this.alwaysDo = alwaysDo;
    }
    
    private static RandomizedBuildingBase getRandomStory() {
        final int next = Rand.Next(RandomizedBuildingBase.totalChance);
        final Iterator<RandomizedBuildingBase> iterator = RandomizedBuildingBase.rbMap.keySet().iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final RandomizedBuildingBase key = iterator.next();
            n += RandomizedBuildingBase.rbMap.get(key);
            if (next < n) {
                return key;
            }
        }
        return null;
    }
    
    @Override
    public ArrayList<IsoZombie> addZombiesOnSquare(final int n, final String anObject, final Integer n2, final IsoGridSquare e) {
        if (IsoWorld.getZombiesDisabled() || "Tutorial".equals(Core.GameMode)) {
            return null;
        }
        final ArrayList<IsoZombie> list = new ArrayList<IsoZombie>();
        for (int i = 0; i < n; ++i) {
            VirtualZombieManager.instance.choices.clear();
            VirtualZombieManager.instance.choices.add(e);
            final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
            if (realZombieAlways != null) {
                if ("Kate".equals(anObject) || "Bob".equals(anObject) || "Raider".equals(anObject)) {
                    realZombieAlways.doDirtBloodEtc = false;
                }
                if (n2 != null) {
                    realZombieAlways.setFemaleEtc(Rand.Next(100) < n2);
                }
                if (anObject != null) {
                    realZombieAlways.dressInPersistentOutfit(anObject);
                    realZombieAlways.bDressInRandomOutfit = false;
                }
                else {
                    realZombieAlways.bDressInRandomOutfit = true;
                }
                list.add(realZombieAlways);
            }
        }
        ZombieSpawnRecorder.instance.record(list, this.getClass().getSimpleName());
        return list;
    }
    
    public ArrayList<IsoZombie> addZombies(final BuildingDef buildingDef, final int n, final String s, final Integer n2, RoomDef roomDef) {
        final boolean b = roomDef == null;
        final ArrayList<IsoZombie> list = new ArrayList<IsoZombie>();
        if (IsoWorld.getZombiesDisabled() || "Tutorial".equals(Core.GameMode)) {
            return list;
        }
        if (roomDef == null) {
            roomDef = this.getRandomRoom(buildingDef, 6);
        }
        int n3 = 2;
        int n4 = roomDef.area / 2;
        if (n == 0) {
            if (SandboxOptions.instance.Zombies.getValue() == 1) {
                n4 += 4;
            }
            else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                n4 += 3;
            }
            else if (SandboxOptions.instance.Zombies.getValue() == 3) {
                n4 += 2;
            }
            else if (SandboxOptions.instance.Zombies.getValue() == 5) {
                n4 -= 4;
            }
            if (n4 > 8) {
                n4 = 8;
            }
            if (n4 < n3) {
                n4 = n3 + 1;
            }
        }
        else {
            n3 = n;
            n4 = n;
        }
        for (int next = Rand.Next(n3, n4), i = 0; i < next; ++i) {
            final IsoGridSquare randomSpawnSquare = RandomizedWorldBase.getRandomSpawnSquare(roomDef);
            if (randomSpawnSquare == null) {
                break;
            }
            VirtualZombieManager.instance.choices.clear();
            VirtualZombieManager.instance.choices.add(randomSpawnSquare);
            final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.getRandom().index(), false);
            if (realZombieAlways != null) {
                if (n2 != null) {
                    realZombieAlways.setFemaleEtc(Rand.Next(100) < n2);
                }
                if (s != null) {
                    realZombieAlways.dressInPersistentOutfit(s);
                    realZombieAlways.bDressInRandomOutfit = false;
                }
                else {
                    realZombieAlways.bDressInRandomOutfit = true;
                }
                list.add(realZombieAlways);
                if (b) {
                    roomDef = this.getRandomRoom(buildingDef, 6);
                }
            }
        }
        ZombieSpawnRecorder.instance.record(list, this.getClass().getSimpleName());
        return list;
    }
    
    public HandWeapon addRandomRangedWeapon(final ItemContainer itemContainer, final boolean b, final boolean b2, final boolean b3) {
        if (RandomizedBuildingBase.weaponsList == null || RandomizedBuildingBase.weaponsList.isEmpty()) {
            this.init();
        }
        final ArrayList<String> list = new ArrayList<String>(RandomizedBuildingBase.weaponsList.keySet());
        final String key = list.get(Rand.Next(0, list.size()));
        final HandWeapon addWeapon = this.addWeapon(key, b);
        if (addWeapon == null) {
            return null;
        }
        if (b2) {
            itemContainer.addItem(InventoryItemFactory.CreateItem(RandomizedBuildingBase.weaponsList.get(key)));
        }
        if (b3) {
            final KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget((Object)"WeaponUpgrades");
            if (kahluaTable == null) {
                return null;
            }
            final KahluaTable kahluaTable2 = (KahluaTable)kahluaTable.rawget((Object)addWeapon.getType());
            if (kahluaTable2 == null) {
                return null;
            }
            for (int next = Rand.Next(1, kahluaTable2.len() + 1), i = 1; i <= next; ++i) {
                addWeapon.attachWeaponPart((WeaponPart)InventoryItemFactory.CreateItem((String)kahluaTable2.rawget(Rand.Next(kahluaTable2.len()) + 1)));
            }
        }
        return addWeapon;
    }
    
    public void spawnItemsInContainers(final BuildingDef buildingDef, final String s, final int n) {
        final ArrayList<ItemContainer> list = new ArrayList<ItemContainer>();
        final ItemPickerJava.ItemPickerRoom itemPickerRoom = (ItemPickerJava.ItemPickerRoom)ItemPickerJava.rooms.get((Object)s);
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        for (int i = buildingDef.x - 1; i < buildingDef.x2 + 1; ++i) {
            for (int j = buildingDef.y - 1; j < buildingDef.y2 + 1; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare gridSquare = currentCell.getGridSquare(i, j, k);
                    if (gridSquare != null) {
                        for (int l = 0; l < gridSquare.getObjects().size(); ++l) {
                            final IsoObject isoObject = gridSquare.getObjects().get(l);
                            if (Rand.Next(100) <= n && isoObject.getContainer() != null && gridSquare.getRoom() != null && gridSquare.getRoom().getName() != null && itemPickerRoom.Containers.containsKey((Object)isoObject.getContainer().getType())) {
                                isoObject.getContainer().clear();
                                list.add(isoObject.getContainer());
                                isoObject.getContainer().setExplored(true);
                            }
                        }
                    }
                }
            }
        }
        for (int index = 0; index < list.size(); ++index) {
            final ItemContainer itemContainer = list.get(index);
            ItemPickerJava.fillContainerType(itemPickerRoom, itemContainer, "", null);
            ItemPickerJava.updateOverlaySprite(itemContainer.getParent());
            if (GameServer.bServer) {
                GameServer.sendItemsInContainer(itemContainer.getParent(), itemContainer);
            }
        }
    }
    
    protected void removeAllZombies(final BuildingDef buildingDef) {
        for (int i = buildingDef.x - 1; i < buildingDef.x + buildingDef.x2 + 1; ++i) {
            for (int j = buildingDef.y - 1; j < buildingDef.y + buildingDef.y2 + 1; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare sq = this.getSq(i, j, k);
                    if (sq != null) {
                        for (int l = 0; l < sq.getMovingObjects().size(); --l, ++l) {
                            sq.getMovingObjects().remove(l);
                        }
                    }
                }
            }
        }
    }
    
    public IsoWindow getWindow(final IsoGridSquare isoGridSquare) {
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            if (isoObject instanceof IsoWindow) {
                return (IsoWindow)isoObject;
            }
        }
        return null;
    }
    
    public IsoDoor getDoor(final IsoGridSquare isoGridSquare) {
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            if (isoObject instanceof IsoDoor) {
                return (IsoDoor)isoObject;
            }
        }
        return null;
    }
    
    public void addBarricade(final IsoGridSquare isoGridSquare, final int n) {
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            if (isoObject instanceof IsoDoor) {
                if (!((IsoDoor)isoObject).isBarricadeAllowed()) {
                    continue;
                }
                final IsoGridSquare isoGridSquare2 = (isoGridSquare.getRoom() == null) ? isoGridSquare : ((IsoDoor)isoObject).getOppositeSquare();
                if (isoGridSquare2 != null && isoGridSquare2.getRoom() == null) {
                    final IsoBarricade addBarricadeToObject = IsoBarricade.AddBarricadeToObject((BarricadeAble)isoObject, isoGridSquare2 != isoGridSquare);
                    if (addBarricadeToObject != null) {
                        for (int j = 0; j < n; ++j) {
                            addBarricadeToObject.addPlank(null, null);
                        }
                        if (GameServer.bServer) {
                            addBarricadeToObject.transmitCompleteItemToClients();
                        }
                    }
                }
            }
            if (isoObject instanceof IsoWindow) {
                if (((IsoWindow)isoObject).isBarricadeAllowed()) {
                    final IsoBarricade addBarricadeToObject2 = IsoBarricade.AddBarricadeToObject((BarricadeAble)isoObject, ((isoGridSquare.getRoom() == null) ? isoGridSquare : ((IsoWindow)isoObject).getOppositeSquare()) != isoGridSquare);
                    if (addBarricadeToObject2 != null) {
                        for (int k = 0; k < n; ++k) {
                            addBarricadeToObject2.addPlank(null, null);
                        }
                        if (GameServer.bServer) {
                            addBarricadeToObject2.transmitCompleteItemToClients();
                        }
                    }
                }
            }
        }
    }
    
    public InventoryItem addWorldItem(final String s, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3) {
        return this.addWorldItem(s, isoGridSquare, n, n2, n3, 0);
    }
    
    public InventoryItem addWorldItem(final String s, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final int worldZRotation) {
        if (s == null || isoGridSquare == null) {
            return null;
        }
        final InventoryItem createItem = InventoryItemFactory.CreateItem(s);
        if (createItem != null) {
            createItem.setAutoAge();
            createItem.setWorldZRotation(worldZRotation);
            if (createItem instanceof HandWeapon) {
                createItem.setCondition(Rand.Next(2, createItem.getConditionMax()));
            }
            return isoGridSquare.AddWorldInventoryItem(createItem, n, n2, n3);
        }
        return null;
    }
    
    public InventoryItem addWorldItem(final String s, final IsoGridSquare isoGridSquare, final IsoObject isoObject) {
        if (s == null || isoGridSquare == null) {
            return null;
        }
        float n = 0.0f;
        if (isoObject != null) {
            n = isoObject.getSurfaceOffsetNoTable() / 96.0f;
        }
        final InventoryItem createItem = InventoryItemFactory.CreateItem(s);
        if (createItem != null) {
            createItem.setAutoAge();
            return isoGridSquare.AddWorldInventoryItem(createItem, Rand.Next(0.3f, 0.9f), Rand.Next(0.3f, 0.9f), n);
        }
        return null;
    }
    
    public boolean isTableFor3DItems(final IsoObject isoObject, final IsoGridSquare isoGridSquare) {
        return isoObject.getSurfaceOffsetNoTable() > 0.0f && isoObject.getContainer() == null && isoGridSquare.getProperties().Val("waterAmount") == null && !isoObject.hasWater() && isoObject.getProperties().Val("BedType") == null;
    }
    
    static {
        RandomizedBuildingBase.totalChance = 0;
        RandomizedBuildingBase.rbMap = new HashMap<RandomizedBuildingBase, Integer>();
        RandomizedBuildingBase.weaponsList = new HashMap<String, String>();
    }
    
    public static final class HumanCorpse extends IsoGameCharacter implements IHumanVisual
    {
        final HumanVisual humanVisual;
        final ItemVisuals itemVisuals;
        public boolean isSkeleton;
        
        public HumanCorpse(final IsoCell isoCell, final float n, final float n2, final float n3) {
            super(isoCell, n, n2, n3);
            this.humanVisual = new HumanVisual(this);
            this.itemVisuals = new ItemVisuals();
            this.isSkeleton = false;
            isoCell.getObjectList().remove(this);
            isoCell.getAddList().remove(this);
        }
        
        @Override
        public void dressInNamedOutfit(final String s) {
            this.getHumanVisual().dressInNamedOutfit(s, this.itemVisuals);
            this.getHumanVisual().synchWithOutfit(this.getHumanVisual().getOutfit());
        }
        
        @Override
        public HumanVisual getHumanVisual() {
            return this.humanVisual;
        }
        
        @Override
        public HumanVisual getVisual() {
            return this.humanVisual;
        }
        
        @Override
        public void Dressup(final SurvivorDesc survivorDesc) {
            this.wornItems.setFromItemVisuals(this.itemVisuals);
            this.wornItems.addItemsToItemContainer(this.inventory);
        }
        
        @Override
        public boolean isSkeleton() {
            return this.isSkeleton;
        }
    }
}
