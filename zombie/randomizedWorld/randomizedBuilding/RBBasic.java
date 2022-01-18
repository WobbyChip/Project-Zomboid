// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding;

import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHockeyPsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSTinFoilHat;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHouseParty;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPoliceAtHouse;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSCorpsePsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSkeletonPsycho;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPrisonEscapeWithPolice;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPrisonEscape;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSuicidePact;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSPokerNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSStudentNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSStagDo;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSHenDo;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSFootballNight;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBedroomZed;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBathroomZed;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBandPractice;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSZombiesEating;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSSpecificProfession;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSDeadDrunk;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSZombieLockedBathroom;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSGunmanInBathroom;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSGunslinger;
import zombie.randomizedWorld.randomizedDeadSurvivor.RDSBleach;
import java.util.Iterator;
import zombie.randomizedWorld.randomizedBuilding.TableStories.RBTableStoryBase;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.objects.IsoStove;
import zombie.iso.IsoDirections;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoRadio;
import zombie.inventory.InventoryItem;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import zombie.network.GameServer;
import zombie.iso.objects.IsoWindow;
import java.nio.ByteBuffer;
import zombie.core.raknet.UdpConnection;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoDoor;
import se.krka.kahlua.vm.KahluaTable;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.IsoWorld;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.ItemContainer;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoObject;
import zombie.randomizedWorld.randomizedDeadSurvivor.RandomizedDeadSurvivorBase;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public final class RBBasic extends RandomizedBuildingBase
{
    private final ArrayList<String> specificProfessionDistribution;
    private final Map<String, String> specificProfessionRoomDistribution;
    private static final HashMap<Integer, String> kitchenSinkItems;
    private static final HashMap<Integer, String> kitchenCounterItems;
    private static final HashMap<Integer, String> kitchenStoveItems;
    private static final HashMap<Integer, String> bathroomSinkItems;
    private final ArrayList<String> coldFood;
    private final Map<String, String> plankStash;
    private final ArrayList<RandomizedDeadSurvivorBase> deadSurvivorsStory;
    private int totalChanceRDS;
    private static final HashMap<RandomizedDeadSurvivorBase, Integer> rdsMap;
    private static final ArrayList<String> uniqueRDSSpawned;
    private ArrayList<IsoObject> tablesDone;
    private boolean doneTable;
    
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        this.tablesDone = new ArrayList<IsoObject>();
        final boolean b = Rand.Next(100) <= 20;
        final ArrayList<ItemContainer> list = new ArrayList<ItemContainer>();
        final String s = this.specificProfessionDistribution.get(Rand.Next(0, this.specificProfessionDistribution.size()));
        final ItemPickerJava.ItemPickerRoom itemPickerRoom = (ItemPickerJava.ItemPickerRoom)ItemPickerJava.rooms.get((Object)s);
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        int nextBool = Rand.NextBool(9) ? 1 : 0;
        for (int i = buildingDef.x - 1; i < buildingDef.x2 + 1; ++i) {
            for (int j = buildingDef.y - 1; j < buildingDef.y2 + 1; ++j) {
                for (int k = 0; k < 8; ++k) {
                    final IsoGridSquare gridSquare = currentCell.getGridSquare(i, j, k);
                    if (gridSquare != null) {
                        if (nextBool != 0 && gridSquare.getFloor() != null && this.plankStash.containsKey(gridSquare.getFloor().getSprite().getName())) {
                            final IsoThumpable isoThumpable = new IsoThumpable(gridSquare.getCell(), gridSquare, this.plankStash.get(gridSquare.getFloor().getSprite().getName()), false, null);
                            isoThumpable.setIsThumpable(false);
                            isoThumpable.container = new ItemContainer("plankstash", gridSquare, isoThumpable);
                            gridSquare.AddSpecialObject(isoThumpable);
                            gridSquare.RecalcAllWithNeighbours(true);
                            nextBool = 0;
                        }
                        for (int l = 0; l < gridSquare.getObjects().size(); ++l) {
                            final IsoObject o = gridSquare.getObjects().get(l);
                            if (Rand.Next(100) <= 65 && o instanceof IsoDoor && !((IsoDoor)o).isExteriorDoor(null)) {
                                ((IsoDoor)o).ToggleDoorSilent();
                                ((IsoDoor)o).syncIsoObject(true, (byte)1, null, null);
                            }
                            if (o instanceof IsoWindow) {
                                final IsoWindow isoWindow = (IsoWindow)o;
                                if (Rand.NextBool(80)) {
                                    buildingDef.bAlarmed = false;
                                    isoWindow.ToggleWindow(null);
                                }
                                final IsoCurtain hasCurtains = isoWindow.HasCurtains();
                                if (hasCurtains != null && Rand.NextBool(15)) {
                                    hasCurtains.ToggleDoorSilent();
                                }
                            }
                            if (b && Rand.Next(100) <= 70 && o.getContainer() != null && gridSquare.getRoom() != null && gridSquare.getRoom().getName() != null && this.specificProfessionRoomDistribution.get(s).contains(gridSquare.getRoom().getName()) && itemPickerRoom.Containers.containsKey((Object)o.getContainer().getType())) {
                                o.getContainer().clear();
                                list.add(o.getContainer());
                                o.getContainer().setExplored(true);
                            }
                            if (Rand.Next(100) < 15 && o.getContainer() != null && o.getContainer().getType().equals("stove")) {
                                final InventoryItem addItem = o.getContainer().AddItem(this.coldFood.get(Rand.Next(0, this.coldFood.size())));
                                addItem.setCooked(true);
                                addItem.setAutoAge();
                            }
                            if (!this.tablesDone.contains(o) && o.getProperties().isTable() && o.getProperties().getSurface() == 34 && o.getContainer() == null && !this.doneTable) {
                                this.checkForTableSpawn(buildingDef, o);
                            }
                        }
                        if (gridSquare.getRoom() != null && "kitchen".equals(gridSquare.getRoom().getName())) {
                            this.doKitchenStuff(gridSquare);
                        }
                        if (gridSquare.getRoom() != null && "bathroom".equals(gridSquare.getRoom().getName())) {
                            this.doBathroomStuff(gridSquare);
                        }
                        if (gridSquare.getRoom() != null && "bedroom".equals(gridSquare.getRoom().getName())) {
                            this.doBedroomStuff(gridSquare);
                        }
                        if (gridSquare.getRoom() != null && "livingroom".equals(gridSquare.getRoom().getName())) {
                            this.doLivingRoomStuff(gridSquare);
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
        if (!b && Rand.Next(100) < 25) {
            this.addRandomDeadSurvivorStory(buildingDef);
            buildingDef.setAllExplored(true);
            buildingDef.bAlarmed = false;
        }
        this.doneTable = false;
    }
    
    private void doLivingRoomStuff(final IsoGridSquare isoGridSquare) {
        IsoObject isoObject = null;
        boolean b = false;
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject isoObject2 = isoGridSquare.getObjects().get(i);
            if (Rand.NextBool(5) && isoObject2.getProperties().Val("BedType") == null && isoObject2.getSurfaceOffsetNoTable() > 0.0f && isoObject2.getSurfaceOffsetNoTable() < 30.0f && !(isoObject2 instanceof IsoRadio)) {
                isoObject = isoObject2;
            }
            if (isoObject2 instanceof IsoRadio || isoObject2 instanceof IsoTelevision) {
                b = true;
                break;
            }
        }
        if (!b && isoObject != null) {
            final int next = Rand.Next(0, 6);
            String s = "Base.TVRemote";
            switch (next) {
                case 0: {
                    s = "Base.TVRemote";
                    break;
                }
                case 1: {
                    s = "Base.TVMagazine";
                    break;
                }
                case 2: {
                    s = "Base.Newspaper";
                    break;
                }
                case 3: {
                    s = "Base.VideoGame";
                    break;
                }
                case 4: {
                    s = "Base.Mugl";
                    break;
                }
                case 5: {
                    s = "Base.Headphones";
                    break;
                }
            }
            final IsoDirections facing = this.getFacing(isoObject.getSprite());
            if (facing != null) {
                if (facing == IsoDirections.E) {
                    this.addWorldItem(s, isoGridSquare, 0.4f, Rand.Next(0.34f, 0.74f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                }
                if (facing == IsoDirections.W) {
                    this.addWorldItem(s, isoGridSquare, 0.64f, Rand.Next(0.34f, 0.74f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                }
                if (facing == IsoDirections.N) {
                    this.addWorldItem(s, isoGridSquare, Rand.Next(0.44f, 0.64f), 0.67f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                }
                if (facing == IsoDirections.S) {
                    this.addWorldItem(s, isoGridSquare, Rand.Next(0.44f, 0.64f), 0.42f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                }
            }
        }
    }
    
    private void doBedroomStuff(final IsoGridSquare isoGridSquare) {
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            if (isoObject.getSprite() == null || isoObject.getSprite().getName() == null) {
                return;
            }
            if (Rand.NextBool(7) && isoObject.getSprite().getName().contains("bedding") && isoObject.getProperties().Val("BedType") != null) {
                switch (Rand.Next(0, 14)) {
                    case 0: {
                        this.addWorldItem("Shirt_FormalTINT", isoGridSquare, 0.6f, 0.6f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 1: {
                        this.addWorldItem("Shirt_FormalWhite_ShortSleeveTINT", isoGridSquare, 0.6f, 0.6f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 2: {
                        this.addWorldItem("Tshirt_DefaultDECAL_TINT", isoGridSquare, 0.6f, 0.6f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 3: {
                        this.addWorldItem("Tshirt_PoloStripedTINT", isoGridSquare, 0.6f, 0.6f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 4: {
                        this.addWorldItem("Tshirt_PoloTINT", isoGridSquare, 0.6f, 0.6f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 5: {
                        this.addWorldItem("Jacket_WhiteTINT", isoGridSquare, 0.6f, 0.6f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 6: {
                        this.addWorldItem("Jumper_DiamondPatternTINT", isoGridSquare, 0.6f, 0.6f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 7: {
                        this.addWorldItem("Jumper_TankTopDiamondTINT", isoGridSquare, 0.6f, 0.6f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 8: {
                        this.addWorldItem("HoodieDOWN_WhiteTINT", isoGridSquare, 0.6f, 0.6f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 9: {
                        this.addWorldItem("Trousers_DefaultTEXTURE_TINT", isoGridSquare, 0.6f, 0.6f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 10: {
                        this.addWorldItem("Trousers_WhiteTINT", isoGridSquare, 0.6f, 0.6f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 11: {
                        this.addWorldItem("Trousers_Denim", isoGridSquare, 0.6f, 0.6f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 12: {
                        this.addWorldItem("Trousers_Padded", isoGridSquare, 0.6f, 0.6f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 13: {
                        this.addWorldItem("TrousersMesh_DenimLight", isoGridSquare, 0.6f, 0.6f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                }
            }
            if (Rand.NextBool(7) && isoObject.getContainer() != null && "sidetable".equals(isoObject.getContainer().getType())) {
                final int next = Rand.Next(0, 4);
                String s = "Base.Book";
                switch (next) {
                    case 0: {
                        s = "Base.Book";
                        break;
                    }
                    case 1: {
                        s = "Base.Notebook";
                        break;
                    }
                    case 2: {
                        s = "Base.VideoGame";
                        break;
                    }
                    case 3: {
                        s = "Base.CDPlayer";
                        break;
                    }
                }
                final IsoDirections facing = this.getFacing(isoObject.getSprite());
                if (facing != null) {
                    if (facing == IsoDirections.E) {
                        this.addWorldItem(s, isoGridSquare, 0.42f, Rand.Next(0.34f, 0.74f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                    }
                    if (facing == IsoDirections.W) {
                        this.addWorldItem(s, isoGridSquare, 0.64f, Rand.Next(0.34f, 0.74f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                    }
                    if (facing == IsoDirections.N) {
                        this.addWorldItem(s, isoGridSquare, Rand.Next(0.44f, 0.64f), 0.67f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                    }
                    if (facing == IsoDirections.S) {
                        this.addWorldItem(s, isoGridSquare, Rand.Next(0.44f, 0.64f), 0.42f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                    }
                }
                return;
            }
        }
    }
    
    private void doKitchenStuff(final IsoGridSquare isoGridSquare) {
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            if (isoObject.getSprite() == null || isoObject.getSprite().getName() == null) {
                return;
            }
            if (n == 0 && isoObject.getSprite().getName().contains("sink") && Rand.NextBool(4)) {
                final IsoDirections facing = this.getFacing(isoObject.getSprite());
                if (facing != null) {
                    this.generateSinkClutter(facing, isoObject, isoGridSquare, RBBasic.kitchenSinkItems);
                    n = 1;
                }
            }
            else if (n2 == 0 && isoObject.getContainer() != null && "counter".equals(isoObject.getContainer().getType()) && Rand.NextBool(6)) {
                boolean b = true;
                for (int j = 0; j < isoGridSquare.getObjects().size(); ++j) {
                    final IsoObject isoObject2 = isoGridSquare.getObjects().get(j);
                    if ((isoObject2.getSprite() != null && isoObject2.getSprite().getName() != null && isoObject2.getSprite().getName().contains("sink")) || isoObject2 instanceof IsoStove || isoObject2 instanceof IsoRadio) {
                        b = false;
                        break;
                    }
                }
                if (b) {
                    final IsoDirections facing2 = this.getFacing(isoObject.getSprite());
                    if (facing2 != null) {
                        this.generateCounterClutter(facing2, isoObject, isoGridSquare, RBBasic.kitchenCounterItems);
                        n2 = 1;
                    }
                }
            }
            else if (isoObject instanceof IsoStove && isoObject.getContainer() != null && "stove".equals(isoObject.getContainer().getType()) && Rand.NextBool(4)) {
                final IsoDirections facing3 = this.getFacing(isoObject.getSprite());
                if (facing3 != null) {
                    this.generateKitchenStoveClutter(facing3, isoObject, isoGridSquare);
                }
            }
        }
    }
    
    private void doBathroomStuff(final IsoGridSquare isoGridSquare) {
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            if (isoObject.getSprite() == null || isoObject.getSprite().getName() == null) {
                return;
            }
            if (n == 0 && n2 == 0 && isoObject.getSprite().getName().contains("sink") && Rand.NextBool(5) && isoObject.getSurfaceOffsetNoTable() > 0.0f) {
                final IsoDirections facing = this.getFacing(isoObject.getSprite());
                if (facing != null) {
                    this.generateSinkClutter(facing, isoObject, isoGridSquare, RBBasic.bathroomSinkItems);
                    n = 1;
                }
            }
            else if (n == 0 && n2 == 0 && isoObject.getContainer() != null && "counter".equals(isoObject.getContainer().getType()) && Rand.NextBool(5)) {
                boolean b = true;
                for (int j = 0; j < isoGridSquare.getObjects().size(); ++j) {
                    final IsoObject isoObject2 = isoGridSquare.getObjects().get(j);
                    if ((isoObject2.getSprite() != null && isoObject2.getSprite().getName() != null && isoObject2.getSprite().getName().contains("sink")) || isoObject2 instanceof IsoStove || isoObject2 instanceof IsoRadio) {
                        b = false;
                        break;
                    }
                }
                if (b) {
                    final IsoDirections facing2 = this.getFacing(isoObject.getSprite());
                    if (facing2 != null) {
                        this.generateCounterClutter(facing2, isoObject, isoGridSquare, RBBasic.bathroomSinkItems);
                        n2 = 1;
                    }
                }
            }
        }
    }
    
    private void generateKitchenStoveClutter(final IsoDirections isoDirections, final IsoObject isoObject, final IsoGridSquare isoGridSquare) {
        final int next = Rand.Next(1, 3);
        final String s = RBBasic.kitchenStoveItems.get(Rand.Next(1, RBBasic.kitchenStoveItems.size()));
        if (isoDirections == IsoDirections.W) {
            switch (next) {
                case 1: {
                    this.addWorldItem(s, isoGridSquare, 0.5703125f, 0.8046875f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                    break;
                }
                case 2: {
                    this.addWorldItem(s, isoGridSquare, 0.5703125f, 0.2578125f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                    break;
                }
            }
        }
        if (isoDirections == IsoDirections.E) {
            switch (next) {
                case 1: {
                    this.addWorldItem(s, isoGridSquare, 0.5f, 0.7890625f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                    break;
                }
                case 2: {
                    this.addWorldItem(s, isoGridSquare, 0.5f, 0.1875f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                    break;
                }
            }
        }
        if (isoDirections == IsoDirections.S) {
            switch (next) {
                case 1: {
                    this.addWorldItem(s, isoGridSquare, 0.3125f, 0.53125f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                    break;
                }
                case 2: {
                    this.addWorldItem(s, isoGridSquare, 0.875f, 0.53125f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                    break;
                }
            }
        }
        if (isoDirections == IsoDirections.N) {
            switch (next) {
                case 1: {
                    this.addWorldItem(s, isoGridSquare, 0.3203f, 0.523475f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                    break;
                }
                case 2: {
                    this.addWorldItem(s, isoGridSquare, 0.8907f, 0.523475f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                    break;
                }
            }
        }
    }
    
    private void generateCounterClutter(final IsoDirections isoDirections, final IsoObject isoObject, final IsoGridSquare isoGridSquare, final HashMap<Integer, String> hashMap) {
        final int next = Rand.Next(1, Math.min(5, hashMap.size() + 1));
        final ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < next; ++i) {
            int n = Rand.Next(1, 5);
            int j = 0;
            while (j == 0) {
                if (!list.contains(n)) {
                    list.add(n);
                    j = 1;
                }
                else {
                    n = Rand.Next(1, 5);
                }
            }
            if (list.size() == 4) {}
        }
        final ArrayList<String> list2 = new ArrayList<String>();
        for (int k = 0; k < list.size(); ++k) {
            final int intValue = list.get(k);
            int l = Rand.Next(1, hashMap.size() + 1);
            String s;
            for (s = null; s == null; s = null, l = Rand.Next(1, hashMap.size() + 1)) {
                s = hashMap.get(l);
                if (list2.contains(s)) {}
            }
            list2.add(s);
            if (isoDirections == IsoDirections.S) {
                switch (intValue) {
                    case 1: {
                        this.addWorldItem(s, isoGridSquare, 0.138f, Rand.Next(0.2f, 0.523f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 2: {
                        this.addWorldItem(s, isoGridSquare, 0.383f, Rand.Next(0.2f, 0.523f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 3: {
                        this.addWorldItem(s, isoGridSquare, 0.633f, Rand.Next(0.2f, 0.523f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 4: {
                        this.addWorldItem(s, isoGridSquare, 0.78f, Rand.Next(0.2f, 0.523f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                }
            }
            if (isoDirections == IsoDirections.N) {
                switch (intValue) {
                    case 1: {
                        isoGridSquare.AddWorldInventoryItem(s, 0.133f, Rand.Next(0.53125f, 0.9375f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 2: {
                        isoGridSquare.AddWorldInventoryItem(s, 0.38f, Rand.Next(0.53125f, 0.9375f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 3: {
                        isoGridSquare.AddWorldInventoryItem(s, 0.625f, Rand.Next(0.53125f, 0.9375f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 4: {
                        isoGridSquare.AddWorldInventoryItem(s, 0.92f, Rand.Next(0.53125f, 0.9375f), isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                }
            }
            if (isoDirections == IsoDirections.E) {
                switch (intValue) {
                    case 1: {
                        isoGridSquare.AddWorldInventoryItem(s, Rand.Next(0.226f, 0.593f), 0.14f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 2: {
                        isoGridSquare.AddWorldInventoryItem(s, Rand.Next(0.226f, 0.593f), 0.33f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 3: {
                        isoGridSquare.AddWorldInventoryItem(s, Rand.Next(0.226f, 0.593f), 0.64f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 4: {
                        isoGridSquare.AddWorldInventoryItem(s, Rand.Next(0.226f, 0.593f), 0.92f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                }
            }
            if (isoDirections == IsoDirections.W) {
                switch (intValue) {
                    case 1: {
                        isoGridSquare.AddWorldInventoryItem(s, Rand.Next(0.5859375f, 0.9f), 0.21875f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 2: {
                        isoGridSquare.AddWorldInventoryItem(s, Rand.Next(0.5859375f, 0.9f), 0.421875f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 3: {
                        isoGridSquare.AddWorldInventoryItem(s, Rand.Next(0.5859375f, 0.9f), 0.71f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 4: {
                        isoGridSquare.AddWorldInventoryItem(s, Rand.Next(0.5859375f, 0.9f), 0.9175f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                }
            }
        }
    }
    
    private void generateSinkClutter(final IsoDirections isoDirections, final IsoObject isoObject, final IsoGridSquare isoGridSquare, final HashMap<Integer, String> hashMap) {
        final int next = Rand.Next(1, Math.min(5, hashMap.size() + 1));
        final ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < next; ++i) {
            int n = Rand.Next(1, 5);
            int j = 0;
            while (j == 0) {
                if (!list.contains(n)) {
                    list.add(n);
                    j = 1;
                }
                else {
                    n = Rand.Next(1, 5);
                }
            }
            if (list.size() == 4) {}
        }
        final ArrayList<String> list2 = new ArrayList<String>();
        for (int k = 0; k < list.size(); ++k) {
            final int intValue = list.get(k);
            int l = Rand.Next(1, hashMap.size() + 1);
            String s;
            for (s = null; s == null; s = null, l = Rand.Next(1, hashMap.size() + 1)) {
                s = hashMap.get(l);
                if (list2.contains(s)) {}
            }
            list2.add(s);
            if (isoDirections == IsoDirections.S) {
                switch (intValue) {
                    case 1: {
                        this.addWorldItem(s, isoGridSquare, 0.71875f, 0.125f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 2: {
                        this.addWorldItem(s, isoGridSquare, 0.0935f, 0.21875f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 3: {
                        this.addWorldItem(s, isoGridSquare, 0.1328125f, 0.589375f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 4: {
                        this.addWorldItem(s, isoGridSquare, 0.7890625f, 0.589375f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                }
            }
            if (isoDirections == IsoDirections.N) {
                switch (intValue) {
                    case 1: {
                        this.addWorldItem(s, isoGridSquare, 0.921875f, 0.921875f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 2: {
                        this.addWorldItem(s, isoGridSquare, 0.1640625f, 0.8984375f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 3: {
                        this.addWorldItem(s, isoGridSquare, 0.021875f, 0.5f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 4: {
                        this.addWorldItem(s, isoGridSquare, 0.8671875f, 0.5f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                }
            }
            if (isoDirections == IsoDirections.E) {
                switch (intValue) {
                    case 1: {
                        this.addWorldItem(s, isoGridSquare, 0.234375f, 0.859375f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 2: {
                        this.addWorldItem(s, isoGridSquare, 0.59375f, 0.875f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 3: {
                        this.addWorldItem(s, isoGridSquare, 0.53125f, 0.125f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 4: {
                        this.addWorldItem(s, isoGridSquare, 0.210937f, 0.1328125f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                }
            }
            if (isoDirections == IsoDirections.W) {
                switch (intValue) {
                    case 1: {
                        this.addWorldItem(s, isoGridSquare, 0.515625f, 0.109375f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 2: {
                        this.addWorldItem(s, isoGridSquare, 0.578125f, 0.890625f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 3: {
                        this.addWorldItem(s, isoGridSquare, 0.8828125f, 0.8984375f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                    case 4: {
                        this.addWorldItem(s, isoGridSquare, 0.8671875f, 0.1653125f, isoObject.getSurfaceOffsetNoTable() / 96.0f);
                        break;
                    }
                }
            }
        }
    }
    
    private IsoDirections getFacing(final IsoSprite isoSprite) {
        if (isoSprite != null && isoSprite.getProperties().Is("Facing")) {
            final String val = isoSprite.getProperties().Val("Facing");
            switch (val) {
                case "N": {
                    return IsoDirections.N;
                }
                case "S": {
                    return IsoDirections.S;
                }
                case "W": {
                    return IsoDirections.W;
                }
                case "E": {
                    return IsoDirections.E;
                }
            }
        }
        return null;
    }
    
    private void checkForTableSpawn(final BuildingDef buildingDef, final IsoObject isoObject) {
        if (Rand.NextBool(10)) {
            final RBTableStoryBase randomStory = RBTableStoryBase.getRandomStory(isoObject.getSquare(), isoObject);
            if (randomStory != null) {
                randomStory.randomizeBuilding(buildingDef);
                this.doneTable = true;
            }
        }
    }
    
    private IsoObject checkForTable(final IsoGridSquare isoGridSquare, final IsoObject o) {
        if (this.tablesDone.contains(o) || isoGridSquare == null) {
            return null;
        }
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject o2 = isoGridSquare.getObjects().get(i);
            if (!this.tablesDone.contains(o2) && o2.getProperties().isTable() && o2.getProperties().getSurface() == 34 && o2.getContainer() == null && o2 != o) {
                return o2;
            }
        }
        return null;
    }
    
    public void doProfessionStory(final BuildingDef buildingDef, final String s) {
        this.spawnItemsInContainers(buildingDef, s, 70);
    }
    
    private void addRandomDeadSurvivorStory(final BuildingDef buildingDef) {
        this.initRDSMap(buildingDef);
        final int next = Rand.Next(this.totalChanceRDS);
        final Iterator<RandomizedDeadSurvivorBase> iterator = RBBasic.rdsMap.keySet().iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final RandomizedDeadSurvivorBase key = iterator.next();
            n += RBBasic.rdsMap.get(key);
            if (next < n) {
                key.randomizeDeadSurvivor(buildingDef);
                if (key.isUnique()) {
                    getUniqueRDSSpawned().add(key.getName());
                    break;
                }
                break;
            }
        }
    }
    
    private void initRDSMap(final BuildingDef buildingDef) {
        this.totalChanceRDS = 0;
        RBBasic.rdsMap.clear();
        for (int i = 0; i < this.deadSurvivorsStory.size(); ++i) {
            final RandomizedDeadSurvivorBase randomizedDeadSurvivorBase = this.deadSurvivorsStory.get(i);
            if (randomizedDeadSurvivorBase.isValid(buildingDef, false) && randomizedDeadSurvivorBase.isTimeValid(false) && ((randomizedDeadSurvivorBase.isUnique() && !getUniqueRDSSpawned().contains(randomizedDeadSurvivorBase.getName())) || !randomizedDeadSurvivorBase.isUnique())) {
                this.totalChanceRDS += this.deadSurvivorsStory.get(i).getChance();
                RBBasic.rdsMap.put(this.deadSurvivorsStory.get(i), this.deadSurvivorsStory.get(i).getChance());
            }
        }
    }
    
    public void doRandomDeadSurvivorStory(final BuildingDef buildingDef, final RandomizedDeadSurvivorBase randomizedDeadSurvivorBase) {
        randomizedDeadSurvivorBase.randomizeDeadSurvivor(buildingDef);
    }
    
    public RBBasic() {
        this.specificProfessionDistribution = new ArrayList<String>();
        this.specificProfessionRoomDistribution = new HashMap<String, String>();
        this.coldFood = new ArrayList<String>();
        this.plankStash = new HashMap<String, String>();
        this.deadSurvivorsStory = new ArrayList<RandomizedDeadSurvivorBase>();
        this.totalChanceRDS = 0;
        this.tablesDone = new ArrayList<IsoObject>();
        this.doneTable = false;
        this.name = "RBBasic";
        this.deadSurvivorsStory.add(new RDSBleach());
        this.deadSurvivorsStory.add(new RDSGunslinger());
        this.deadSurvivorsStory.add(new RDSGunmanInBathroom());
        this.deadSurvivorsStory.add(new RDSZombieLockedBathroom());
        this.deadSurvivorsStory.add(new RDSDeadDrunk());
        this.deadSurvivorsStory.add(new RDSSpecificProfession());
        this.deadSurvivorsStory.add(new RDSZombiesEating());
        this.deadSurvivorsStory.add(new RDSBandPractice());
        this.deadSurvivorsStory.add(new RDSBathroomZed());
        this.deadSurvivorsStory.add(new RDSBedroomZed());
        this.deadSurvivorsStory.add(new RDSFootballNight());
        this.deadSurvivorsStory.add(new RDSHenDo());
        this.deadSurvivorsStory.add(new RDSStagDo());
        this.deadSurvivorsStory.add(new RDSStudentNight());
        this.deadSurvivorsStory.add(new RDSPokerNight());
        this.deadSurvivorsStory.add(new RDSSuicidePact());
        this.deadSurvivorsStory.add(new RDSPrisonEscape());
        this.deadSurvivorsStory.add(new RDSPrisonEscapeWithPolice());
        this.deadSurvivorsStory.add(new RDSSkeletonPsycho());
        this.deadSurvivorsStory.add(new RDSCorpsePsycho());
        this.deadSurvivorsStory.add(new RDSPoliceAtHouse());
        this.deadSurvivorsStory.add(new RDSHouseParty());
        this.deadSurvivorsStory.add(new RDSTinFoilHat());
        this.deadSurvivorsStory.add(new RDSHockeyPsycho());
        this.specificProfessionDistribution.add("Carpenter");
        this.specificProfessionDistribution.add("Electrician");
        this.specificProfessionDistribution.add("Farmer");
        this.specificProfessionDistribution.add("Nurse");
        this.specificProfessionRoomDistribution.put("Carpenter", "kitchen");
        this.specificProfessionRoomDistribution.put("Electrician", "kitchen");
        this.specificProfessionRoomDistribution.put("Farmer", "kitchen");
        this.specificProfessionRoomDistribution.put("Nurse", "kitchen");
        this.specificProfessionRoomDistribution.put("Nurse", "bathroom");
        this.coldFood.add("Base.Chicken");
        this.coldFood.add("Base.Steak");
        this.coldFood.add("Base.PorkChop");
        this.coldFood.add("Base.MuttonChop");
        this.coldFood.add("Base.MeatPatty");
        this.coldFood.add("Base.FishFillet");
        this.coldFood.add("Base.Salmon");
        this.plankStash.put("floors_interior_tilesandwood_01_40", "floors_interior_tilesandwood_01_56");
        this.plankStash.put("floors_interior_tilesandwood_01_41", "floors_interior_tilesandwood_01_57");
        this.plankStash.put("floors_interior_tilesandwood_01_42", "floors_interior_tilesandwood_01_58");
        this.plankStash.put("floors_interior_tilesandwood_01_43", "floors_interior_tilesandwood_01_59");
        this.plankStash.put("floors_interior_tilesandwood_01_44", "floors_interior_tilesandwood_01_60");
        this.plankStash.put("floors_interior_tilesandwood_01_45", "floors_interior_tilesandwood_01_61");
        this.plankStash.put("floors_interior_tilesandwood_01_46", "floors_interior_tilesandwood_01_62");
        this.plankStash.put("floors_interior_tilesandwood_01_47", "floors_interior_tilesandwood_01_63");
        this.plankStash.put("floors_interior_tilesandwood_01_52", "floors_interior_tilesandwood_01_68");
        RBBasic.kitchenSinkItems.put(1, "Soap2");
        RBBasic.kitchenSinkItems.put(2, "CleaningLiquid2");
        RBBasic.kitchenSinkItems.put(3, "Sponge");
        RBBasic.kitchenCounterItems.put(1, "Dogfood");
        RBBasic.kitchenCounterItems.put(2, "CannedCorn");
        RBBasic.kitchenCounterItems.put(3, "CannedPeas");
        RBBasic.kitchenCounterItems.put(4, "CannedPotato2");
        RBBasic.kitchenCounterItems.put(5, "CannedSardines");
        RBBasic.kitchenCounterItems.put(6, "CannedTomato2");
        RBBasic.kitchenCounterItems.put(7, "CannedCarrots2");
        RBBasic.kitchenCounterItems.put(8, "CannedChili");
        RBBasic.kitchenCounterItems.put(9, "CannedBolognese");
        RBBasic.kitchenCounterItems.put(10, "TinOpener");
        RBBasic.kitchenCounterItems.put(11, "WaterBottleFull");
        RBBasic.kitchenCounterItems.put(12, "Cereal");
        RBBasic.kitchenCounterItems.put(13, "CerealBowl");
        RBBasic.kitchenCounterItems.put(14, "Spoon");
        RBBasic.kitchenCounterItems.put(15, "Fork");
        RBBasic.kitchenCounterItems.put(16, "KitchenKnife");
        RBBasic.kitchenCounterItems.put(17, "ButterKnife");
        RBBasic.kitchenCounterItems.put(18, "BreadKnife");
        RBBasic.kitchenCounterItems.put(19, "DishCloth");
        RBBasic.kitchenCounterItems.put(20, "RollingPin");
        RBBasic.kitchenCounterItems.put(21, "EmptyJar");
        RBBasic.kitchenCounterItems.put(22, "Bowl");
        RBBasic.kitchenCounterItems.put(23, "MugWhite");
        RBBasic.kitchenCounterItems.put(24, "MugRed");
        RBBasic.kitchenCounterItems.put(25, "Mugl");
        RBBasic.kitchenCounterItems.put(26, "WaterPot");
        RBBasic.kitchenCounterItems.put(27, "WaterSaucepan");
        RBBasic.kitchenCounterItems.put(28, "PotOfSoup");
        RBBasic.kitchenCounterItems.put(29, "StewBowl");
        RBBasic.kitchenCounterItems.put(30, "SoupBowl");
        RBBasic.kitchenCounterItems.put(31, "WaterSaucepanPasta");
        RBBasic.kitchenCounterItems.put(32, "WaterSaucepanRice");
        RBBasic.kitchenStoveItems.put(1, "WaterSaucepanRice");
        RBBasic.kitchenStoveItems.put(2, "WaterSaucepanPasta");
        RBBasic.kitchenStoveItems.put(3, "WaterPot");
        RBBasic.kitchenStoveItems.put(4, "PotOfSoup");
        RBBasic.kitchenStoveItems.put(5, "WaterSaucepan");
        RBBasic.kitchenStoveItems.put(6, "PotOfStew");
        RBBasic.kitchenStoveItems.put(7, "PastaPot");
        RBBasic.kitchenStoveItems.put(8, "RicePot");
        RBBasic.bathroomSinkItems.put(1, "Comb");
        RBBasic.bathroomSinkItems.put(2, "Cologne");
        RBBasic.bathroomSinkItems.put(3, "Antibiotics");
        RBBasic.bathroomSinkItems.put(4, "Bandage");
        RBBasic.bathroomSinkItems.put(5, "Pills");
        RBBasic.bathroomSinkItems.put(6, "PillsAntiDep");
        RBBasic.bathroomSinkItems.put(7, "PillsBeta");
        RBBasic.bathroomSinkItems.put(8, "PillsSleepingTablets");
        RBBasic.bathroomSinkItems.put(9, "PillsVitamins");
        RBBasic.bathroomSinkItems.put(10, "Lipstick");
        RBBasic.bathroomSinkItems.put(11, "MakeupEyeshadow");
        RBBasic.bathroomSinkItems.put(12, "MakeupFoundation");
        RBBasic.bathroomSinkItems.put(13, "Perfume");
        RBBasic.bathroomSinkItems.put(14, "Razor");
        RBBasic.bathroomSinkItems.put(15, "Toothbrush");
        RBBasic.bathroomSinkItems.put(16, "Toothpaste");
        RBBasic.bathroomSinkItems.put(17, "Tweezers");
    }
    
    public ArrayList<RandomizedDeadSurvivorBase> getSurvivorStories() {
        return this.deadSurvivorsStory;
    }
    
    public ArrayList<String> getSurvivorProfession() {
        return this.specificProfessionDistribution;
    }
    
    public static ArrayList<String> getUniqueRDSSpawned() {
        return RBBasic.uniqueRDSSpawned;
    }
    
    static {
        kitchenSinkItems = new HashMap<Integer, String>();
        kitchenCounterItems = new HashMap<Integer, String>();
        kitchenStoveItems = new HashMap<Integer, String>();
        bathroomSinkItems = new HashMap<Integer, String>();
        rdsMap = new HashMap<RandomizedDeadSurvivorBase, Integer>();
        uniqueRDSSpawned = new ArrayList<String>();
    }
}
