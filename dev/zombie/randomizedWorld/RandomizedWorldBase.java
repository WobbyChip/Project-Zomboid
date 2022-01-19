// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld;

import zombie.Lua.MapObjects;
import zombie.iso.IsoObject;
import zombie.SandboxOptions;
import zombie.GameTime;
import java.util.List;
import zombie.util.list.PZArrayUtil;
import zombie.iso.BuildingDef;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.characters.SurvivorFactory;
import zombie.randomizedWorld.randomizedBuilding.RandomizedBuildingBase;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.characters.IsoGameCharacter;
import zombie.characterTextures.BloodBodyPartType;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.ZombieSpawnRecorder;
import zombie.VirtualZombieManager;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.RoomDef;
import java.util.Collection;
import zombie.characters.IsoZombie;
import java.util.ArrayList;
import zombie.iso.IsoCell;
import zombie.core.Rand;
import zombie.vehicles.VehiclesDB2;
import zombie.vehicles.VehicleType;
import zombie.iso.IsoWorld;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;
import zombie.iso.IsoDirections;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.Vector2;

public class RandomizedWorldBase
{
    private static final Vector2 s_tempVector2;
    protected int minimumDays;
    protected int maximumDays;
    protected int minimumRooms;
    protected boolean unique;
    private boolean rvsVehicleKeyAddedToZombie;
    protected String name;
    protected String debugLine;
    
    public RandomizedWorldBase() {
        this.minimumDays = 0;
        this.maximumDays = 0;
        this.minimumRooms = 0;
        this.unique = false;
        this.rvsVehicleKeyAddedToZombie = false;
        this.name = null;
        this.debugLine = "";
    }
    
    public BaseVehicle addVehicle(final IsoMetaGrid.Zone zone, final IsoGridSquare isoGridSquare, final IsoChunk isoChunk, final String s, final String s2, final IsoDirections isoDirections) {
        return this.addVehicle(zone, isoGridSquare, isoChunk, s, s2, null, isoDirections, null);
    }
    
    public BaseVehicle addVehicleFlipped(final IsoMetaGrid.Zone zone, final IsoGridSquare isoGridSquare, final IsoChunk isoChunk, final String s, final String s2, final Integer n, IsoDirections random, final String s3) {
        if (isoGridSquare == null) {
            return null;
        }
        if (random == null) {
            random = IsoDirections.getRandom();
        }
        return this.addVehicleFlipped(zone, (float)isoGridSquare.x, (float)isoGridSquare.y, (float)isoGridSquare.z, random.ToVector().getDirection(), s, s2, n, s3);
    }
    
    public BaseVehicle addVehicleFlipped(final IsoMetaGrid.Zone zone, final float x, final float y, final float z, final float n, String s, final String scriptName, final Integer n2, final String specificDistributionId) {
        if (StringUtils.isNullOrEmpty(s)) {
            s = "junkyard";
        }
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(x, y, z);
        if (gridSquare == null) {
            return null;
        }
        final IsoChunk chunk = gridSquare.getChunk();
        final IsoDirections fromAngle = IsoDirections.fromAngle(n);
        final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
        e.specificDistributionId = specificDistributionId;
        final VehicleType randomVehicleType = VehicleType.getRandomVehicleType(s, false);
        if (!StringUtils.isNullOrEmpty(scriptName)) {
            e.setScriptName(scriptName);
            e.setScript();
            if (n2 != null) {
                e.setSkinIndex(n2);
            }
        }
        else {
            if (randomVehicleType == null) {
                return null;
            }
            e.setVehicleType(randomVehicleType.name);
            if (!chunk.RandomizeModel(e, zone, s, randomVehicleType)) {
                return null;
            }
        }
        if (randomVehicleType.isSpecialCar) {
            e.setDoColor(false);
        }
        e.setDir(fromAngle);
        float n3;
        for (n3 = n - 1.5707964f; n3 > 6.283185307179586; n3 -= (float)6.283185307179586) {}
        e.savedRot.rotationXYZ(0.0f, -n3, 3.1415927f);
        e.jniTransform.setRotation(e.savedRot);
        e.setX(x);
        e.setY(y);
        e.setZ(z);
        if (IsoChunk.doSpawnedVehiclesInInvalidPosition(e)) {
            e.setSquare(gridSquare);
            gridSquare.chunk.vehicles.add(e);
            e.chunk = gridSquare.chunk;
            e.addToWorld();
            VehiclesDB2.instance.addVehicle(e);
        }
        e.setGeneralPartCondition(0.2f, 70.0f);
        e.rust = ((Rand.Next(100) < 70) ? 1.0f : 0.0f);
        return e;
    }
    
    public BaseVehicle addVehicle(final IsoMetaGrid.Zone zone, final IsoGridSquare isoGridSquare, final IsoChunk isoChunk, final String s, final String s2, final Integer n, IsoDirections random, final String s3) {
        if (isoGridSquare == null) {
            return null;
        }
        if (random == null) {
            random = IsoDirections.getRandom();
        }
        final Vector2 toVector = random.ToVector();
        toVector.rotate(Rand.Next(-0.5f, 0.5f));
        return this.addVehicle(zone, (float)isoGridSquare.x, (float)isoGridSquare.y, (float)isoGridSquare.z, toVector.getDirection(), s, s2, n, s3);
    }
    
    public BaseVehicle addVehicle(final IsoMetaGrid.Zone zone, final float x, final float y, final float z, final float n, String s, final String scriptName, final Integer n2, final String specificDistributionId) {
        if (StringUtils.isNullOrEmpty(s)) {
            s = "junkyard";
        }
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(x, y, z);
        if (gridSquare == null) {
            return null;
        }
        final IsoChunk chunk = gridSquare.getChunk();
        final IsoDirections fromAngle = IsoDirections.fromAngle(n);
        final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
        e.specificDistributionId = specificDistributionId;
        final VehicleType randomVehicleType = VehicleType.getRandomVehicleType(s, false);
        if (!StringUtils.isNullOrEmpty(scriptName)) {
            e.setScriptName(scriptName);
            e.setScript();
            if (n2 != null) {
                e.setSkinIndex(n2);
            }
        }
        else {
            if (randomVehicleType == null) {
                return null;
            }
            e.setVehicleType(randomVehicleType.name);
            if (!chunk.RandomizeModel(e, zone, s, randomVehicleType)) {
                return null;
            }
        }
        if (randomVehicleType.isSpecialCar) {
            e.setDoColor(false);
        }
        e.setDir(fromAngle);
        float n3;
        for (n3 = n - 1.5707964f; n3 > 6.283185307179586; n3 -= (float)6.283185307179586) {}
        e.savedRot.setAngleAxis(-n3, 0.0f, 1.0f, 0.0f);
        e.jniTransform.setRotation(e.savedRot);
        e.setX(x);
        e.setY(y);
        e.setZ(z);
        if (IsoChunk.doSpawnedVehiclesInInvalidPosition(e)) {
            e.setSquare(gridSquare);
            gridSquare.chunk.vehicles.add(e);
            e.chunk = gridSquare.chunk;
            e.addToWorld();
            VehiclesDB2.instance.addVehicle(e);
        }
        e.setGeneralPartCondition(0.2f, 70.0f);
        e.rust = ((Rand.Next(100) < 70) ? 1.0f : 0.0f);
        return e;
    }
    
    public static void removeAllVehiclesOnZone(final IsoMetaGrid.Zone zone) {
        for (int i = zone.x; i < zone.x + zone.w; ++i) {
            for (int j = zone.y; j < zone.y + zone.h; ++j) {
                final IsoGridSquare gridSquare = IsoCell.getInstance().getGridSquare(i, j, 0);
                if (gridSquare != null) {
                    final BaseVehicle vehicleContainer = gridSquare.getVehicleContainer();
                    if (vehicleContainer != null) {
                        vehicleContainer.permanentlyRemove();
                    }
                }
            }
        }
    }
    
    public ArrayList<IsoZombie> addZombiesOnVehicle(int i, final String s, final Integer n, final BaseVehicle baseVehicle) {
        final ArrayList<IsoZombie> list = new ArrayList<IsoZombie>();
        if (baseVehicle == null) {
            return list;
        }
        int j = 100;
        final IsoGridSquare square = baseVehicle.getSquare();
        if (square == null || square.getCell() == null) {
            return list;
        }
        while (i > 0) {
            while (j > 0) {
                final IsoGridSquare gridSquare = square.getCell().getGridSquare(Rand.Next(square.x - 4, square.x + 4), Rand.Next(square.y - 4, square.y + 4), square.z);
                if (gridSquare != null && gridSquare.getVehicleContainer() == null) {
                    --i;
                    list.addAll(this.addZombiesOnSquare(1, s, n, gridSquare));
                    break;
                }
                --j;
            }
            j = 100;
        }
        if (!this.rvsVehicleKeyAddedToZombie && !list.isEmpty()) {
            list.get(Rand.Next(0, list.size())).addItemToSpawnAtDeath(baseVehicle.createVehicleKey());
            this.rvsVehicleKeyAddedToZombie = true;
        }
        return list;
    }
    
    public static IsoDeadBody createRandomDeadBody(final RoomDef roomDef, final int n) {
        if (IsoWorld.getZombiesDisabled()) {
            return null;
        }
        if (roomDef == null) {
            return null;
        }
        final IsoGridSquare randomSquareForCorpse = getRandomSquareForCorpse(roomDef);
        if (randomSquareForCorpse == null) {
            return null;
        }
        return createRandomDeadBody(randomSquareForCorpse, null, n, 0, null);
    }
    
    public ArrayList<IsoZombie> addZombiesOnSquare(final int n, final String s, final Integer n2, final IsoGridSquare e) {
        final ArrayList<IsoZombie> list = new ArrayList<IsoZombie>();
        if (IsoWorld.getZombiesDisabled()) {
            return list;
        }
        if (e == null) {
            return list;
        }
        for (int i = 0; i < n; ++i) {
            VirtualZombieManager.instance.choices.clear();
            VirtualZombieManager.instance.choices.add(e);
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
                    realZombieAlways.dressInRandomOutfit();
                    realZombieAlways.bDressInRandomOutfit = false;
                }
                list.add(realZombieAlways);
            }
        }
        ZombieSpawnRecorder.instance.record(list, this.getClass().getSimpleName());
        return list;
    }
    
    public static IsoDeadBody createRandomDeadBody(final int n, final int n2, final int n3, final IsoDirections isoDirections, final int n4) {
        return createRandomDeadBody(n, n2, n3, isoDirections, n4, 0);
    }
    
    public static IsoDeadBody createRandomDeadBody(final int n, final int n2, final int n3, final IsoDirections isoDirections, final int n4, final int n5) {
        return createRandomDeadBody(IsoCell.getInstance().getGridSquare(n, n2, n3), isoDirections, n4, n5, null);
    }
    
    public static IsoDeadBody createRandomDeadBody(final IsoGridSquare isoGridSquare, IsoDirections random, final int n, final int n2, final String s) {
        if (isoGridSquare == null) {
            return null;
        }
        final boolean b = random == null;
        if (b) {
            random = IsoDirections.getRandom();
        }
        return createRandomDeadBody(isoGridSquare.x + Rand.Next(0.05f, 0.95f), isoGridSquare.y + Rand.Next(0.05f, 0.95f), (float)isoGridSquare.z, random.ToVector().getDirection(), b, n, n2, s);
    }
    
    public static IsoDeadBody createRandomDeadBody(final float x, final float y, final float n, final float n2, final boolean b, final int n3, final int n4, final String s) {
        if (IsoWorld.getZombiesDisabled()) {
            return null;
        }
        final IsoGridSquare gridSquare = IsoCell.getInstance().getGridSquare(x, y, n);
        if (gridSquare == null) {
            return null;
        }
        final IsoDirections fromAngle = IsoDirections.fromAngle(n2);
        VirtualZombieManager.instance.choices.clear();
        VirtualZombieManager.instance.choices.add(gridSquare);
        final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(fromAngle.index(), false);
        if (realZombieAlways == null) {
            return null;
        }
        if (s != null) {
            realZombieAlways.dressInPersistentOutfit(s);
            realZombieAlways.bDressInRandomOutfit = false;
        }
        else {
            realZombieAlways.dressInRandomOutfit();
        }
        if (Rand.Next(100) < n4) {
            realZombieAlways.setFakeDead(true);
            realZombieAlways.setCrawler(true);
            realZombieAlways.setCanWalk(false);
            realZombieAlways.setCrawlerType(1);
        }
        else {
            realZombieAlways.setFakeDead(false);
            realZombieAlways.setHealth(0.0f);
        }
        realZombieAlways.upKillCount = false;
        realZombieAlways.getHumanVisual().zombieRotStage = ((HumanVisual)realZombieAlways.getVisual()).pickRandomZombieRotStage();
        for (int i = 0; i < n3; ++i) {
            realZombieAlways.addBlood(null, false, true, true);
        }
        realZombieAlways.DoCorpseInventory();
        realZombieAlways.setX(x);
        realZombieAlways.setY(y);
        realZombieAlways.getForwardDirection().setLengthAndDirection(n2, 1.0f);
        if (b) {
            alignCorpseToSquare(realZombieAlways, gridSquare);
        }
        return new IsoDeadBody(realZombieAlways, true);
    }
    
    public void addTraitOfBlood(final IsoDirections isoDirections, final int n, final int n2, final int n3, final int n4) {
        for (int i = 0; i < n; ++i) {
            float n5 = 0.0f;
            float n6 = 0.0f;
            if (isoDirections == IsoDirections.S) {
                n6 = Rand.Next(-2.0f, 0.5f);
            }
            if (isoDirections == IsoDirections.N) {
                n6 = Rand.Next(-0.5f, 2.0f);
            }
            if (isoDirections == IsoDirections.E) {
                n5 = Rand.Next(-2.0f, 0.5f);
            }
            if (isoDirections == IsoDirections.W) {
                n5 = Rand.Next(-0.5f, 2.0f);
            }
            new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, IsoCell.getInstance(), (float)n2, (float)n3, n4 + 0.2f, n5, n6);
        }
    }
    
    public void addTrailOfBlood(final float n, final float n2, final float n3, final float n4, final int n5) {
        final Vector2 s_tempVector2 = RandomizedWorldBase.s_tempVector2;
        for (int i = 0; i < n5; ++i) {
            final float next = Rand.Next(-0.5f, 2.0f);
            if (next < 0.0f) {
                s_tempVector2.setLengthAndDirection(n4 + 3.1415927f, -next);
            }
            else {
                s_tempVector2.setLengthAndDirection(n4, next);
            }
            new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, IsoCell.getInstance(), n, n2, n3 + 0.2f, s_tempVector2.x, s_tempVector2.y);
        }
    }
    
    public void addBloodSplat(final IsoGridSquare isoGridSquare, final int n) {
        for (int i = 0; i < n; ++i) {
            isoGridSquare.getChunk().addBloodSplat(isoGridSquare.x + Rand.Next(-0.5f, 0.5f), isoGridSquare.y + Rand.Next(-0.5f, 0.5f), (float)isoGridSquare.z, Rand.Next(8));
        }
    }
    
    public void setAttachedItem(final IsoZombie isoZombie, final String s, final String s2, final String s3) {
        final InventoryItem createItem = InventoryItemFactory.CreateItem(s2);
        if (createItem == null) {
            return;
        }
        createItem.setCondition(Rand.Next(Math.max(2, createItem.getConditionMax() - 5), createItem.getConditionMax()));
        if (createItem instanceof HandWeapon) {
            ((HandWeapon)createItem).randomizeBullets();
        }
        isoZombie.setAttachedItem(s, createItem);
        if (!StringUtils.isNullOrEmpty(s3)) {
            isoZombie.addItemToSpawnAtDeath(InventoryItemFactory.CreateItem(s3));
        }
    }
    
    public static IsoGameCharacter createRandomZombie(final RoomDef roomDef) {
        final IsoGridSquare randomSpawnSquare = getRandomSpawnSquare(roomDef);
        return createRandomZombie(randomSpawnSquare.getX(), randomSpawnSquare.getY(), randomSpawnSquare.getZ());
    }
    
    public static IsoGameCharacter createRandomZombieForCorpse(final RoomDef roomDef) {
        final IsoGridSquare randomSquareForCorpse = getRandomSquareForCorpse(roomDef);
        if (randomSquareForCorpse == null) {
            return null;
        }
        final IsoGameCharacter randomZombie = createRandomZombie(randomSquareForCorpse.getX(), randomSquareForCorpse.getY(), randomSquareForCorpse.getZ());
        if (randomZombie != null) {
            alignCorpseToSquare(randomZombie, randomSquareForCorpse);
        }
        return randomZombie;
    }
    
    public static IsoDeadBody createBodyFromZombie(final IsoGameCharacter isoGameCharacter) {
        if (IsoWorld.getZombiesDisabled()) {
            return null;
        }
        for (int i = 0; i < 6; ++i) {
            isoGameCharacter.splatBlood(Rand.Next(1, 4), 0.3f);
        }
        return new IsoDeadBody(isoGameCharacter, true);
    }
    
    public static IsoGameCharacter createRandomZombie(final int n, final int n2, final int n3) {
        final RandomizedBuildingBase.HumanCorpse humanCorpse = new RandomizedBuildingBase.HumanCorpse(IsoWorld.instance.getCell(), (float)n, (float)n2, (float)n3);
        humanCorpse.setDescriptor(SurvivorFactory.CreateSurvivor());
        humanCorpse.setFemale(humanCorpse.getDescriptor().isFemale());
        humanCorpse.setDir(IsoDirections.fromIndex(Rand.Next(8)));
        humanCorpse.initWornItems("Human");
        humanCorpse.initAttachedItems("Human");
        humanCorpse.dressInNamedOutfit(humanCorpse.getRandomDefaultOutfit().m_Name);
        humanCorpse.initSpritePartsEmpty();
        humanCorpse.Dressup(humanCorpse.getDescriptor());
        return humanCorpse;
    }
    
    private static boolean isSquareClear(final IsoGridSquare isoGridSquare) {
        return isoGridSquare != null && canSpawnAt(isoGridSquare) && !isoGridSquare.HasStairs() && !isoGridSquare.HasTree() && !isoGridSquare.getProperties().Is(IsoFlagType.bed) && !isoGridSquare.getProperties().Is(IsoFlagType.waterPiped);
    }
    
    private static boolean isSquareClear(final IsoGridSquare isoGridSquare, final IsoDirections isoDirections) {
        final IsoGridSquare adjacentSquare = isoGridSquare.getAdjacentSquare(isoDirections);
        return isSquareClear(adjacentSquare) && !isoGridSquare.isSomethingTo(adjacentSquare) && isoGridSquare.getRoomID() == adjacentSquare.getRoomID();
    }
    
    public static boolean is1x2AreaClear(final IsoGridSquare isoGridSquare) {
        return isSquareClear(isoGridSquare) && isSquareClear(isoGridSquare, IsoDirections.N);
    }
    
    public static boolean is2x1AreaClear(final IsoGridSquare isoGridSquare) {
        return isSquareClear(isoGridSquare) && isSquareClear(isoGridSquare, IsoDirections.W);
    }
    
    public static boolean is2x1or1x2AreaClear(final IsoGridSquare isoGridSquare) {
        return isSquareClear(isoGridSquare) && (isSquareClear(isoGridSquare, IsoDirections.W) || isSquareClear(isoGridSquare, IsoDirections.N));
    }
    
    public static boolean is2x2AreaClear(final IsoGridSquare isoGridSquare) {
        return isSquareClear(isoGridSquare) && isSquareClear(isoGridSquare, IsoDirections.N) && isSquareClear(isoGridSquare, IsoDirections.W) && isSquareClear(isoGridSquare, IsoDirections.NW);
    }
    
    public static void alignCorpseToSquare(final IsoGameCharacter isoGameCharacter, final IsoGridSquare isoGridSquare) {
        final int x = isoGridSquare.x;
        final int y = isoGridSquare.y;
        IsoDirections fromIndex = IsoDirections.fromIndex(Rand.Next(8));
        boolean is1x2AreaClear = is1x2AreaClear(isoGridSquare);
        boolean is2x1AreaClear = is2x1AreaClear(isoGridSquare);
        if (is1x2AreaClear && is2x1AreaClear) {
            is1x2AreaClear = (Rand.Next(2) == 0);
            is2x1AreaClear = !is1x2AreaClear;
        }
        if (is2x2AreaClear(isoGridSquare)) {
            isoGameCharacter.setX((float)x);
            isoGameCharacter.setY((float)y);
        }
        else if (is1x2AreaClear) {
            isoGameCharacter.setX(x + 0.5f);
            isoGameCharacter.setY((float)y);
            fromIndex = ((Rand.Next(2) == 0) ? IsoDirections.N : IsoDirections.S);
        }
        else if (is2x1AreaClear) {
            isoGameCharacter.setX((float)x);
            isoGameCharacter.setY(y + 0.5f);
            fromIndex = ((Rand.Next(2) == 0) ? IsoDirections.W : IsoDirections.E);
        }
        else if (is1x2AreaClear(isoGridSquare.getAdjacentSquare(IsoDirections.S))) {
            isoGameCharacter.setX(x + 0.5f);
            isoGameCharacter.setY(y + 0.99f);
            fromIndex = ((Rand.Next(2) == 0) ? IsoDirections.N : IsoDirections.S);
        }
        else if (is2x1AreaClear(isoGridSquare.getAdjacentSquare(IsoDirections.E))) {
            isoGameCharacter.setX(x + 0.99f);
            isoGameCharacter.setY(y + 0.5f);
            fromIndex = ((Rand.Next(2) == 0) ? IsoDirections.W : IsoDirections.E);
        }
        isoGameCharacter.setDir(fromIndex);
        final float x2 = isoGameCharacter.x;
        isoGameCharacter.nx = x2;
        isoGameCharacter.lx = x2;
        final float y2 = isoGameCharacter.y;
        isoGameCharacter.ny = y2;
        isoGameCharacter.ly = y2;
        isoGameCharacter.setScriptnx(isoGameCharacter.x);
        isoGameCharacter.setScriptny(isoGameCharacter.y);
    }
    
    public RoomDef getRandomRoom(final BuildingDef buildingDef, final int n) {
        RoomDef roomDef = buildingDef.getRooms().get(Rand.Next(0, buildingDef.getRooms().size()));
        if (n > 0 && roomDef.area >= n) {
            return roomDef;
        }
        int i = 0;
        while (i <= 20) {
            ++i;
            roomDef = buildingDef.getRooms().get(Rand.Next(0, buildingDef.getRooms().size()));
            if (roomDef.area >= n) {
                return roomDef;
            }
        }
        return roomDef;
    }
    
    public RoomDef getRoom(final BuildingDef buildingDef, final String anotherString) {
        for (int i = 0; i < buildingDef.rooms.size(); ++i) {
            final RoomDef roomDef = buildingDef.rooms.get(i);
            if (roomDef.getName().equalsIgnoreCase(anotherString)) {
                return roomDef;
            }
        }
        return null;
    }
    
    public RoomDef getLivingRoomOrKitchen(final BuildingDef buildingDef) {
        RoomDef roomDef = this.getRoom(buildingDef, "livingroom");
        if (roomDef == null) {
            roomDef = this.getRoom(buildingDef, "kitchen");
        }
        return roomDef;
    }
    
    private static boolean canSpawnAt(final IsoGridSquare isoGridSquare) {
        return isoGridSquare != null && !isoGridSquare.HasStairs() && VirtualZombieManager.instance.canSpawnAt(isoGridSquare.x, isoGridSquare.y, isoGridSquare.z);
    }
    
    public static IsoGridSquare getRandomSpawnSquare(final RoomDef roomDef) {
        if (roomDef == null) {
            return null;
        }
        return roomDef.getRandomSquare(RandomizedWorldBase::canSpawnAt);
    }
    
    public static IsoGridSquare getRandomSquareForCorpse(final RoomDef roomDef) {
        IsoGridSquare randomSquare = roomDef.getRandomSquare(RandomizedWorldBase::is2x2AreaClear);
        final IsoGridSquare randomSquare2 = roomDef.getRandomSquare(RandomizedWorldBase::is2x1or1x2AreaClear);
        if (randomSquare == null || (randomSquare2 != null && Rand.Next(4) == 0)) {
            randomSquare = randomSquare2;
        }
        return randomSquare;
    }
    
    public BaseVehicle spawnCarOnNearestNav(final String s, final BuildingDef buildingDef) {
        IsoGridSquare isoGridSquare = null;
        final int n = (buildingDef.x + buildingDef.x2) / 2;
        final int n2 = (buildingDef.y + buildingDef.y2) / 2;
        for (int i = n; i < n + 20; ++i) {
            final IsoGridSquare gridSquare = IsoCell.getInstance().getGridSquare(i, n2, 0);
            if (gridSquare != null && "Nav".equals(gridSquare.getZoneType())) {
                isoGridSquare = gridSquare;
                break;
            }
        }
        if (isoGridSquare != null) {
            return this.spawnCar(s, isoGridSquare);
        }
        for (int j = n; j > n - 20; --j) {
            final IsoGridSquare gridSquare2 = IsoCell.getInstance().getGridSquare(j, n2, 0);
            if (gridSquare2 != null && "Nav".equals(gridSquare2.getZoneType())) {
                isoGridSquare = gridSquare2;
                break;
            }
        }
        if (isoGridSquare != null) {
            return this.spawnCar(s, isoGridSquare);
        }
        for (int k = n2; k < n2 + 20; ++k) {
            final IsoGridSquare gridSquare3 = IsoCell.getInstance().getGridSquare(n, k, 0);
            if (gridSquare3 != null && "Nav".equals(gridSquare3.getZoneType())) {
                isoGridSquare = gridSquare3;
                break;
            }
        }
        if (isoGridSquare != null) {
            return this.spawnCar(s, isoGridSquare);
        }
        for (int l = n2; l > n2 - 20; --l) {
            final IsoGridSquare gridSquare4 = IsoCell.getInstance().getGridSquare(n, l, 0);
            if (gridSquare4 != null && "Nav".equals(gridSquare4.getZoneType())) {
                isoGridSquare = gridSquare4;
                break;
            }
        }
        if (isoGridSquare != null) {
            return this.spawnCar(s, isoGridSquare);
        }
        return null;
    }
    
    private BaseVehicle spawnCar(final String scriptName, final IsoGridSquare square) {
        final BaseVehicle e = new BaseVehicle(IsoWorld.instance.CurrentCell);
        e.setScriptName(scriptName);
        e.setX(square.x + 0.5f);
        e.setY(square.y + 0.5f);
        e.setZ(0.0f);
        e.savedRot.setAngleAxis(Rand.Next(0.0f, 6.2831855f), 0.0f, 1.0f, 0.0f);
        e.jniTransform.setRotation(e.savedRot);
        if (IsoChunk.doSpawnedVehiclesInInvalidPosition(e)) {
            e.keySpawned = 1;
            e.setSquare(square);
            e.square.chunk.vehicles.add(e);
            e.chunk = e.square.chunk;
            e.addToWorld();
            VehiclesDB2.instance.addVehicle(e);
        }
        e.setGeneralPartCondition(0.3f, 70.0f);
        return e;
    }
    
    public InventoryItem addItemOnGround(final IsoGridSquare isoGridSquare, final String s) {
        if (isoGridSquare == null || StringUtils.isNullOrWhitespace(s)) {
            return null;
        }
        return isoGridSquare.AddWorldInventoryItem(s, Rand.Next(0.2f, 0.8f), Rand.Next(0.2f, 0.8f), 0.0f);
    }
    
    public InventoryItem addItemOnGround(final IsoGridSquare isoGridSquare, final InventoryItem inventoryItem) {
        if (isoGridSquare == null || inventoryItem == null) {
            return null;
        }
        return isoGridSquare.AddWorldInventoryItem(inventoryItem, Rand.Next(0.2f, 0.8f), Rand.Next(0.2f, 0.8f), 0.0f);
    }
    
    public void addRandomItemsOnGround(final RoomDef roomDef, final String s, final int n) {
        for (int i = 0; i < n; ++i) {
            this.addItemOnGround(getRandomSpawnSquare(roomDef), s);
        }
    }
    
    public void addRandomItemsOnGround(final RoomDef roomDef, final ArrayList<String> list, final int n) {
        for (int i = 0; i < n; ++i) {
            this.addRandomItemOnGround(getRandomSpawnSquare(roomDef), list);
        }
    }
    
    public InventoryItem addRandomItemOnGround(final IsoGridSquare isoGridSquare, final ArrayList<String> list) {
        if (isoGridSquare == null || list.isEmpty()) {
            return null;
        }
        return this.addItemOnGround(isoGridSquare, PZArrayUtil.pickRandom(list));
    }
    
    public HandWeapon addWeapon(final String s, final boolean b) {
        final HandWeapon handWeapon = (HandWeapon)InventoryItemFactory.CreateItem(s);
        if (handWeapon == null) {
            return null;
        }
        if (handWeapon.isRanged() && b) {
            if (!StringUtils.isNullOrWhitespace(handWeapon.getMagazineType())) {
                handWeapon.setContainsClip(true);
            }
            handWeapon.setCurrentAmmoCount(Rand.Next(Math.max(handWeapon.getMaxAmmo() - 8, 0), handWeapon.getMaxAmmo() - 2));
        }
        return handWeapon;
    }
    
    public IsoDeadBody createSkeletonCorpse(final RoomDef roomDef) {
        if (roomDef == null) {
            return null;
        }
        final IsoGridSquare randomSquare = roomDef.getRandomSquare(RandomizedWorldBase::is2x1or1x2AreaClear);
        if (randomSquare == null) {
            return null;
        }
        VirtualZombieManager.instance.choices.clear();
        VirtualZombieManager.instance.choices.add(randomSquare);
        final IsoZombie realZombieAlways = VirtualZombieManager.instance.createRealZombieAlways(Rand.Next(8), false);
        if (realZombieAlways == null) {
            return null;
        }
        ZombieSpawnRecorder.instance.record(realZombieAlways, this.getClass().getSimpleName());
        alignCorpseToSquare(realZombieAlways, randomSquare);
        realZombieAlways.setFakeDead(false);
        realZombieAlways.setHealth(0.0f);
        realZombieAlways.upKillCount = false;
        realZombieAlways.setSkeleton(true);
        realZombieAlways.getHumanVisual().setSkinTextureIndex(Rand.Next(1, 3));
        return new IsoDeadBody(realZombieAlways, true);
    }
    
    public boolean isTimeValid(final boolean b) {
        if (this.minimumDays == 0 || this.maximumDays == 0) {
            return true;
        }
        final float n = (float)GameTime.getInstance().getWorldAgeHours() / 24.0f + (SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30;
        return (this.minimumDays <= 0 || n >= this.minimumDays) && (this.maximumDays <= 0 || n <= this.maximumDays);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDebugLine() {
        return this.debugLine;
    }
    
    public void setDebugLine(final String debugLine) {
        this.debugLine = debugLine;
    }
    
    public int getMaximumDays() {
        return this.maximumDays;
    }
    
    public void setMaximumDays(final int maximumDays) {
        this.maximumDays = maximumDays;
    }
    
    public boolean isUnique() {
        return this.unique;
    }
    
    public void setUnique(final boolean unique) {
        this.unique = unique;
    }
    
    public IsoGridSquare getSq(final int n, final int n2, final int n3) {
        return IsoWorld.instance.getCell().getGridSquare(n, n2, n3);
    }
    
    public IsoObject addTileObject(final int n, final int n2, final int n3, final String s) {
        return this.addTileObject(this.getSq(n, n2, n3), s);
    }
    
    public IsoObject addTileObject(final IsoGridSquare isoGridSquare, final String s) {
        if (isoGridSquare == null) {
            return null;
        }
        final IsoObject new1 = IsoObject.getNew(isoGridSquare, s, null, false);
        isoGridSquare.AddTileObject(new1);
        MapObjects.newGridSquare(isoGridSquare);
        MapObjects.loadGridSquare(isoGridSquare);
        return new1;
    }
    
    public IsoObject addTentNorthSouth(final int n, final int n2, final int n3) {
        this.addTileObject(n, n2 - 1, n3, "camping_01_1");
        return this.addTileObject(n, n2, n3, "camping_01_0");
    }
    
    public IsoObject addTentWestEast(final int n, final int n2, final int n3) {
        this.addTileObject(n - 1, n2, n3, "camping_01_2");
        return this.addTileObject(n, n2, n3, "camping_01_3");
    }
    
    public BaseVehicle addTrailer(final BaseVehicle baseVehicle, final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final String s, final String s2, final String s3) {
        final IsoGridSquare square = baseVehicle.getSquare();
        final IsoDirections dir = baseVehicle.getDir();
        int n = 0;
        int n2 = 0;
        if (dir == IsoDirections.S) {
            n2 = -3;
        }
        if (dir == IsoDirections.N) {
            n2 = 3;
        }
        if (dir == IsoDirections.W) {
            n = 3;
        }
        if (dir == IsoDirections.E) {
            n = -3;
        }
        final BaseVehicle addVehicle = this.addVehicle(zone, this.getSq(square.x + n, square.y + n2, square.z), isoChunk, s, s3, null, dir, s2);
        if (addVehicle != null) {
            baseVehicle.positionTrailer(addVehicle);
        }
        return addVehicle;
    }
    
    static {
        s_tempVector2 = new Vector2();
    }
}
