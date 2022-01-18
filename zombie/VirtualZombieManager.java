// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.iso.IsoMetaChunk;
import zombie.popman.ZombiePopulationManager;
import zombie.iso.areas.IsoRoom;
import java.util.Collection;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.iso.RoomDef;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMovingObject;
import zombie.vehicles.PolygonalMap2;
import zombie.iso.Vector2;
import zombie.network.ServerMap;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.IsoDirections;
import zombie.characters.action.ActionGroup;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoFireManager;
import zombie.characters.ZombiesZoneDefinition;
import zombie.inventory.InventoryItemFactory;
import zombie.core.Rand;
import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.popman.NetworkZombieSimulator;
import zombie.iso.IsoWorld;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoGridSquare;
import java.util.ArrayList;
import java.util.HashSet;
import zombie.characters.IsoZombie;
import java.util.ArrayDeque;

public final class VirtualZombieManager
{
    private final ArrayDeque<IsoZombie> ReusableZombies;
    private final HashSet<IsoZombie> ReusableZombieSet;
    private final ArrayList<IsoZombie> ReusedThisFrame;
    private final ArrayList<IsoZombie> RecentlyRemoved;
    public static VirtualZombieManager instance;
    public int MaxRealZombies;
    private final ArrayList<IsoZombie> m_tempZombies;
    public final ArrayList<IsoGridSquare> choices;
    private final ArrayList<IsoGridSquare> bestchoices;
    HandWeapon w;
    
    public VirtualZombieManager() {
        this.ReusableZombies = new ArrayDeque<IsoZombie>();
        this.ReusableZombieSet = new HashSet<IsoZombie>();
        this.ReusedThisFrame = new ArrayList<IsoZombie>();
        this.RecentlyRemoved = new ArrayList<IsoZombie>();
        this.MaxRealZombies = 1;
        this.m_tempZombies = new ArrayList<IsoZombie>();
        this.choices = new ArrayList<IsoGridSquare>();
        this.bestchoices = new ArrayList<IsoGridSquare>();
        this.w = null;
    }
    
    public boolean removeZombieFromWorld(final IsoZombie isoZombie) {
        final boolean b = isoZombie.getCurrentSquare() != null;
        isoZombie.getEmitter().unregister();
        isoZombie.removeFromWorld();
        isoZombie.removeFromSquare();
        return b;
    }
    
    private void reuseZombie(final IsoZombie o) {
        if (o == null) {
            return;
        }
        assert !IsoWorld.instance.CurrentCell.getObjectList().contains(o);
        assert !IsoWorld.instance.CurrentCell.getZombieList().contains(o);
        assert !o.getCurrentSquare().getMovingObjects().contains(o);
        if (this.isReused(o)) {
            return;
        }
        NetworkZombieSimulator.getInstance().remove(o);
        o.resetForReuse();
        this.addToReusable(o);
    }
    
    public void addToReusable(final IsoZombie e) {
        if (e != null && !this.ReusableZombieSet.contains(e)) {
            this.ReusableZombies.addLast(e);
            this.ReusableZombieSet.add(e);
        }
    }
    
    public boolean isReused(final IsoZombie o) {
        return this.ReusableZombieSet.contains(o);
    }
    
    public void init() {
        if (GameClient.bClient) {
            return;
        }
        if (IsoWorld.getZombiesDisabled()) {
            return;
        }
        for (int i = 0; i < this.MaxRealZombies; ++i) {
            final IsoZombie isoZombie = new IsoZombie(IsoWorld.instance.CurrentCell);
            isoZombie.getEmitter().unregister();
            this.addToReusable(isoZombie);
        }
    }
    
    public void Reset() {
        this.bestchoices.clear();
        this.choices.clear();
        this.ReusableZombies.clear();
        this.ReusableZombieSet.clear();
        this.ReusedThisFrame.clear();
    }
    
    public void update() {
        final long currentTimeMillis = System.currentTimeMillis();
        for (int i = this.RecentlyRemoved.size() - 1; i >= 0; --i) {
            final IsoZombie e = this.RecentlyRemoved.get(i);
            e.updateEmitter();
            if (currentTimeMillis - e.removedFromWorldMS > 5000L) {
                if (e.vocalEvent != 0L) {
                    e.getEmitter().stopSound(e.vocalEvent);
                    e.vocalEvent = 0L;
                }
                e.getEmitter().stopAll();
                this.RecentlyRemoved.remove(i);
                this.ReusedThisFrame.add(e);
            }
        }
        if (GameClient.bClient || GameServer.bServer) {
            for (int j = 0; j < this.ReusedThisFrame.size(); ++j) {
                this.reuseZombie(this.ReusedThisFrame.get(j));
            }
            this.ReusedThisFrame.clear();
            return;
        }
        for (int k = 0; k < IsoWorld.instance.CurrentCell.getZombieList().size(); ++k) {
            final IsoZombie isoZombie = IsoWorld.instance.CurrentCell.getZombieList().get(k);
            if (!isoZombie.KeepItReal && isoZombie.getCurrentSquare() == null) {
                isoZombie.removeFromWorld();
                isoZombie.removeFromSquare();
                assert this.ReusedThisFrame.contains(isoZombie);
                assert !IsoWorld.instance.CurrentCell.getZombieList().contains(isoZombie);
                --k;
            }
        }
        for (int l = 0; l < this.ReusedThisFrame.size(); ++l) {
            this.reuseZombie(this.ReusedThisFrame.get(l));
        }
        this.ReusedThisFrame.clear();
    }
    
    public IsoZombie createRealZombieAlways(final int n, final boolean b) {
        return this.createRealZombieAlways(n, b, 0);
    }
    
    public IsoZombie createRealZombieAlways(final int n, final int n2, final boolean b) {
        return this.createRealZombieAlways(n2, b, PersistentOutfits.instance.getOutfit(n));
    }
    
    public IsoZombie createRealZombieAlways(final int n, final boolean b, int pickPersistentOutfit) {
        if (!SystemDisabler.doZombieCreation) {
            return null;
        }
        if (this.choices == null || this.choices.isEmpty()) {
            return null;
        }
        final IsoGridSquare current = this.choices.get(Rand.Next(this.choices.size()));
        if (current == null) {
            return null;
        }
        if (this.w == null) {
            this.w = (HandWeapon)InventoryItemFactory.CreateItem("Base.Axe");
        }
        if ((GameServer.bServer || GameClient.bClient) && pickPersistentOutfit == 0) {
            pickPersistentOutfit = ZombiesZoneDefinition.pickPersistentOutfit(current);
        }
        IsoZombie e;
        if (this.ReusableZombies.isEmpty()) {
            e = new IsoZombie(IsoWorld.instance.CurrentCell);
            e.bDressInRandomOutfit = (pickPersistentOutfit == 0);
            e.setPersistentOutfitID(pickPersistentOutfit);
            IsoWorld.instance.CurrentCell.getObjectList().add(e);
        }
        else {
            e = this.ReusableZombies.removeFirst();
            this.ReusableZombieSet.remove(e);
            e.getHumanVisual().clear();
            e.clearAttachedItems();
            e.clearItemsToSpawnAtDeath();
            e.bDressInRandomOutfit = (pickPersistentOutfit == 0);
            e.setPersistentOutfitID(pickPersistentOutfit);
            e.setSitAgainstWall(false);
            e.setOnDeathDone(false);
            e.setOnKillDone(false);
            e.setDoDeathSound(true);
            e.setHitTime(0);
            e.setFallOnFront(false);
            e.setFakeDead(false);
            e.setReanimatedPlayer(false);
            e.setStateMachineLocked(false);
            final Vector2 toVector;
            final Vector2 forwardDirection = toVector = e.dir.ToVector();
            toVector.x += Rand.Next(200) / 100.0f - 0.5f;
            final Vector2 vector2 = forwardDirection;
            vector2.y += Rand.Next(200) / 100.0f - 0.5f;
            forwardDirection.normalize();
            e.setForwardDirection(forwardDirection);
            IsoWorld.instance.CurrentCell.getObjectList().add(e);
            e.walkVariant = "ZombieWalk";
            e.DoZombieStats();
            if (e.isOnFire()) {
                IsoFireManager.RemoveBurningCharacter(e);
                e.setOnFire(false);
            }
            if (e.AttachedAnimSprite != null) {
                e.AttachedAnimSprite.clear();
            }
            e.thumpFlag = 0;
            e.thumpSent = false;
            e.mpIdleSound = false;
            e.soundSourceTarget = null;
            e.soundAttract = 0.0f;
            e.soundAttractTimeout = 0.0f;
            e.bodyToEat = null;
            e.eatBodyTarget = null;
            e.atlasTex = null;
            e.clearVariables();
            e.setStaggerBack(false);
            e.setKnockedDown(false);
            e.setKnifeDeath(false);
            e.setJawStabAttach(false);
            e.setCrawler(false);
            e.initializeStates();
            e.actionContext.setGroup(ActionGroup.getActionGroup("zombie"));
            e.advancedAnimator.OnAnimDataChanged(false);
            e.setDefaultState();
            e.getAnimationPlayer().resetBoneModelTransforms();
        }
        e.dir = IsoDirections.fromIndex(n);
        e.setForwardDirection(e.dir.ToVector());
        e.getInventory().setExplored(false);
        if (b) {
            e.bDressInRandomOutfit = true;
        }
        e.target = null;
        e.TimeSinceSeenFlesh = 100000.0f;
        if (!e.isFakeDead()) {
            if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
                e.setHealth(3.5f + Rand.Next(0.0f, 0.3f));
            }
            if (SandboxOptions.instance.Lore.Toughness.getValue() == 2) {
                e.setHealth(1.5f + Rand.Next(0.0f, 0.3f));
            }
            if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
                e.setHealth(0.5f + Rand.Next(0.0f, 0.3f));
            }
            if (SandboxOptions.instance.Lore.Toughness.getValue() == 4) {
                e.setHealth(Rand.Next(0.5f, 3.5f) + Rand.Next(0.0f, 0.3f));
            }
        }
        else {
            e.setHealth(0.5f + Rand.Next(0.0f, 0.3f));
        }
        final float n2 = (float)Rand.Next(0, 1000);
        final float n3 = (float)Rand.Next(0, 1000);
        final float n4 = n2 / 1000.0f;
        final float n5 = n3 / 1000.0f;
        final float x = n4 + current.getX();
        final float y = n5 + current.getY();
        e.setCurrent(current);
        e.setMovingSquareNow();
        e.setX(x);
        e.setY(y);
        e.setZ((float)current.getZ());
        if ((GameClient.bClient || GameServer.bServer) && e.networkAI != null) {
            e.networkAI.reset();
        }
        e.upKillCount = true;
        if (b) {
            e.setDir(IsoDirections.fromIndex(Rand.Next(8)));
            e.setForwardDirection(e.dir.ToVector());
            e.setFakeDead(false);
            e.setHealth(0.0f);
            e.upKillCount = false;
            e.DoZombieInventory();
            new IsoDeadBody(e, true);
            return e;
        }
        synchronized (IsoWorld.instance.CurrentCell.getZombieList()) {
            e.getEmitter().register();
            IsoWorld.instance.CurrentCell.getZombieList().add(e);
            if (GameClient.bClient) {
                e.bRemote = true;
            }
            if (GameServer.bServer) {
                e.OnlineID = ServerMap.instance.getUniqueZombieId();
                if (e.OnlineID == -1) {
                    IsoWorld.instance.CurrentCell.getZombieList().remove(e);
                    IsoWorld.instance.CurrentCell.getObjectList().remove(e);
                    this.ReusedThisFrame.add(e);
                    return null;
                }
                ServerMap.instance.ZombieMap.put(e.OnlineID, e);
            }
            return e;
        }
    }
    
    private IsoGridSquare pickEatingZombieSquare(final float n, final float n2, final float n3, final float n4, final int n5) {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n3, n4, n5);
        if (gridSquare == null || !this.canSpawnAt(gridSquare.x, gridSquare.y, gridSquare.z) || gridSquare.HasStairs()) {
            return null;
        }
        if (PolygonalMap2.instance.lineClearCollide(n, n2, n3, n4, n5, null, false, true)) {
            return null;
        }
        return gridSquare;
    }
    
    public void createEatingZombies(final IsoDeadBody isoDeadBody, final int n) {
        if (IsoWorld.getZombiesDisabled()) {
            return;
        }
        for (int i = 0; i < n; ++i) {
            float x = isoDeadBody.x;
            float y = isoDeadBody.y;
            switch (i) {
                case 0: {
                    x -= 0.5f;
                    break;
                }
                case 1: {
                    x += 0.5f;
                    break;
                }
                case 2: {
                    y -= 0.5f;
                    break;
                }
                case 3: {
                    y += 0.5f;
                    break;
                }
            }
            final IsoGridSquare pickEatingZombieSquare = this.pickEatingZombieSquare(isoDeadBody.x, isoDeadBody.y, x, y, (int)isoDeadBody.z);
            if (pickEatingZombieSquare != null) {
                this.choices.clear();
                this.choices.add(pickEatingZombieSquare);
                final IsoZombie realZombieAlways = this.createRealZombieAlways(1, false);
                if (realZombieAlways != null) {
                    ZombieSpawnRecorder.instance.record(realZombieAlways, "createEatingZombies");
                    realZombieAlways.bDressInRandomOutfit = true;
                    realZombieAlways.setX(x);
                    realZombieAlways.setY(y);
                    realZombieAlways.setZ(isoDeadBody.z);
                    realZombieAlways.faceLocationF(isoDeadBody.x, isoDeadBody.y);
                    realZombieAlways.setEatBodyTarget(isoDeadBody, true);
                }
            }
        }
    }
    
    private IsoZombie createRealZombie(final int n, final boolean b) {
        if (GameClient.bClient) {
            return null;
        }
        return this.createRealZombieAlways(n, b);
    }
    
    public void AddBloodToMap(final int n, final IsoChunk isoChunk) {
        for (int i = 0; i < n; ++i) {
            int n2 = 0;
            IsoGridSquare gridSquare;
            do {
                gridSquare = isoChunk.getGridSquare(Rand.Next(10), Rand.Next(10), 0);
            } while (++n2 < 100 && (gridSquare == null || !gridSquare.isFree(false)));
            if (gridSquare != null) {
                int n3 = 5;
                if (Rand.Next(10) == 0) {
                    n3 = 10;
                }
                if (Rand.Next(40) == 0) {
                    n3 = 20;
                }
                for (int j = 0; j < n3; ++j) {
                    isoChunk.addBloodSplat(gridSquare.getX() + (Rand.Next(3000) / 1000.0f - 1.5f), gridSquare.getY() + (Rand.Next(3000) / 1000.0f - 1.5f), (float)gridSquare.getZ(), Rand.Next(12) + 8);
                }
            }
        }
    }
    
    public ArrayList<IsoZombie> addZombiesToMap(final int n, final RoomDef roomDef) {
        return this.addZombiesToMap(n, roomDef, true);
    }
    
    public ArrayList<IsoZombie> addZombiesToMap(int min, final RoomDef roomDef, final boolean b) {
        final ArrayList<IsoZombie> list = new ArrayList<IsoZombie>();
        if ("Tutorial".equals(Core.GameMode)) {
            return list;
        }
        this.choices.clear();
        this.bestchoices.clear();
        for (int i = 0; i < roomDef.rects.size(); ++i) {
            final int level = roomDef.level;
            final RoomDef.RoomRect roomRect = roomDef.rects.get(i);
            for (int j = roomRect.x; j < roomRect.getX2(); ++j) {
                for (int k = roomRect.y; k < roomRect.getY2(); ++k) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(j, k, level);
                    if (gridSquare != null && this.canSpawnAt(j, k, level)) {
                        this.choices.add(gridSquare);
                        boolean b2 = false;
                        for (int l = 0; l < IsoPlayer.numPlayers; ++l) {
                            if (IsoPlayer.players[l] != null && gridSquare.isSeen(l)) {
                                b2 = true;
                            }
                        }
                        if (!b2) {
                            this.bestchoices.add(gridSquare);
                        }
                    }
                }
            }
        }
        min = Math.min(min, this.choices.size());
        if (!this.bestchoices.isEmpty()) {
            this.choices.addAll(this.bestchoices);
            this.choices.addAll(this.bestchoices);
        }
        for (int n = 0; n < min; ++n) {
            if (!this.choices.isEmpty()) {
                roomDef.building.bAlarmed = false;
                final int next = Rand.Next(8);
                final int n2 = 4;
                final IsoZombie realZombie = this.createRealZombie(next, b && Rand.Next(n2) == 0);
                if (realZombie != null && realZombie.getSquare() != null) {
                    if (!GameServer.bServer) {
                        realZombie.bDressInRandomOutfit = true;
                    }
                    realZombie.setX((int)realZombie.getX() + Rand.Next(2, 8) / 10.0f);
                    realZombie.setY((int)realZombie.getY() + Rand.Next(2, 8) / 10.0f);
                    this.choices.remove(realZombie.getSquare());
                    this.choices.remove(realZombie.getSquare());
                    this.choices.remove(realZombie.getSquare());
                    list.add(realZombie);
                }
            }
            else {
                System.out.println("No choices for zombie.");
            }
        }
        this.bestchoices.clear();
        this.choices.clear();
        return list;
    }
    
    public void tryAddIndoorZombies(final RoomDef roomDef, final boolean b) {
    }
    
    private void addIndoorZombies(int min, final RoomDef roomDef, final boolean b) {
        this.choices.clear();
        this.bestchoices.clear();
        for (int i = 0; i < roomDef.rects.size(); ++i) {
            final int level = roomDef.level;
            final RoomDef.RoomRect roomRect = roomDef.rects.get(i);
            for (int j = roomRect.x; j < roomRect.getX2(); ++j) {
                for (int k = roomRect.y; k < roomRect.getY2(); ++k) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(j, k, level);
                    if (gridSquare != null && this.canSpawnAt(j, k, level)) {
                        this.choices.add(gridSquare);
                    }
                }
            }
        }
        min = Math.min(min, this.choices.size());
        if (!this.bestchoices.isEmpty()) {
            this.choices.addAll(this.bestchoices);
            this.choices.addAll(this.bestchoices);
        }
        for (int l = 0; l < min; ++l) {
            if (!this.choices.isEmpty()) {
                roomDef.building.bAlarmed = false;
                final int next = Rand.Next(8);
                final int n = 4;
                final IsoZombie realZombie = this.createRealZombie(next, b && Rand.Next(n) == 0);
                if (realZombie != null && realZombie.getSquare() != null) {
                    ZombieSpawnRecorder.instance.record(realZombie, "addIndoorZombies");
                    realZombie.bIndoorZombie = true;
                    realZombie.setX((int)realZombie.getX() + Rand.Next(2, 8) / 10.0f);
                    realZombie.setY((int)realZombie.getY() + Rand.Next(2, 8) / 10.0f);
                    this.choices.remove(realZombie.getSquare());
                    this.choices.remove(realZombie.getSquare());
                    this.choices.remove(realZombie.getSquare());
                }
            }
            else {
                System.out.println("No choices for zombie.");
            }
        }
        this.bestchoices.clear();
        this.choices.clear();
    }
    
    public void addIndoorZombiesToChunk(final IsoChunk isoChunk, final IsoRoom isoRoom, final int n, final ArrayList<IsoZombie> list) {
        if (n <= 0) {
            return;
        }
        final int a = (int)Math.ceil(n * isoRoom.getRoomDef().getAreaOverlapping(isoChunk));
        if (a <= 0) {
            return;
        }
        this.choices.clear();
        final int level = isoRoom.def.level;
        for (int i = 0; i < isoRoom.rects.size(); ++i) {
            final RoomDef.RoomRect roomRect = isoRoom.rects.get(i);
            final int max = Math.max(isoChunk.wx * 10, roomRect.x);
            final int max2 = Math.max(isoChunk.wy * 10, roomRect.y);
            final int min = Math.min((isoChunk.wx + 1) * 10, roomRect.x + roomRect.w);
            final int min2 = Math.min((isoChunk.wy + 1) * 10, roomRect.y + roomRect.h);
            for (int j = max; j < min; ++j) {
                for (int k = max2; k < min2; ++k) {
                    final IsoGridSquare gridSquare = isoChunk.getGridSquare(j - isoChunk.wx * 10, k - isoChunk.wy * 10, level);
                    if (gridSquare != null && this.canSpawnAt(j, k, level)) {
                        this.choices.add(gridSquare);
                    }
                }
            }
        }
        if (this.choices.isEmpty()) {
            return;
        }
        isoRoom.def.building.bAlarmed = false;
        for (int min3 = Math.min(a, this.choices.size()), l = 0; l < min3; ++l) {
            final IsoZombie realZombie = this.createRealZombie(Rand.Next(8), false);
            if (realZombie != null && realZombie.getSquare() != null) {
                if (!GameServer.bServer) {
                    realZombie.bDressInRandomOutfit = true;
                }
                realZombie.setX((int)realZombie.getX() + Rand.Next(2, 8) / 10.0f);
                realZombie.setY((int)realZombie.getY() + Rand.Next(2, 8) / 10.0f);
                this.choices.remove(realZombie.getSquare());
                list.add(realZombie);
            }
        }
        this.choices.clear();
    }
    
    public void addIndoorZombiesToChunk(final IsoChunk isoChunk, final IsoRoom isoRoom) {
        if (isoRoom.def.spawnCount == -1) {
            isoRoom.def.spawnCount = this.getZombieCountForRoom(isoRoom);
        }
        this.m_tempZombies.clear();
        this.addIndoorZombiesToChunk(isoChunk, isoRoom, isoRoom.def.spawnCount, this.m_tempZombies);
        ZombieSpawnRecorder.instance.record(this.m_tempZombies, "addIndoorZombiesToChunk");
    }
    
    public void addDeadZombiesToMap(int min, final RoomDef roomDef) {
        this.choices.clear();
        this.bestchoices.clear();
        for (int i = 0; i < roomDef.rects.size(); ++i) {
            final int level = roomDef.level;
            final RoomDef.RoomRect roomRect = roomDef.rects.get(i);
            for (int j = roomRect.x; j < roomRect.getX2(); ++j) {
                for (int k = roomRect.y; k < roomRect.getY2(); ++k) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(j, k, level);
                    if (gridSquare != null && gridSquare.isFree(false)) {
                        this.choices.add(gridSquare);
                        if (!GameServer.bServer) {
                            boolean b = false;
                            for (int l = 0; l < IsoPlayer.numPlayers; ++l) {
                                if (IsoPlayer.players[l] != null && gridSquare.isSeen(l)) {
                                    b = true;
                                }
                            }
                            if (!b) {
                                this.bestchoices.add(gridSquare);
                            }
                        }
                    }
                }
            }
        }
        min = Math.min(min, this.choices.size());
        if (!this.bestchoices.isEmpty()) {
            this.choices.addAll(this.bestchoices);
            this.choices.addAll(this.bestchoices);
        }
        for (int n = 0; n < min; ++n) {
            if (!this.choices.isEmpty()) {
                this.createRealZombie(Rand.Next(8), true);
            }
        }
        this.bestchoices.clear();
        this.choices.clear();
    }
    
    public void RemoveZombie(final IsoZombie isoZombie) {
        if (isoZombie.isReanimatedPlayer()) {
            if (isoZombie.vocalEvent != 0L) {
                isoZombie.getEmitter().stopSound(isoZombie.vocalEvent);
                isoZombie.vocalEvent = 0L;
            }
            ReanimatedPlayers.instance.removeReanimatedPlayerFromWorld(isoZombie);
            return;
        }
        if (isoZombie.isDead()) {
            if (!this.RecentlyRemoved.contains(isoZombie)) {
                isoZombie.removedFromWorldMS = System.currentTimeMillis();
                this.RecentlyRemoved.add(isoZombie);
            }
        }
        else if (!this.ReusedThisFrame.contains(isoZombie)) {
            this.ReusedThisFrame.add(isoZombie);
        }
    }
    
    public void createHordeFromTo(final float n, final float n2, final float n3, final float n4, final int n5) {
        ZombiePopulationManager.instance.createHordeFromTo((int)n, (int)n2, (int)n3, (int)n4, n5);
    }
    
    public IsoZombie createRealZombie(final float n, final float n2, final float n3) {
        this.choices.clear();
        this.choices.add(IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3));
        if (!this.choices.isEmpty()) {
            return this.createRealZombie(Rand.Next(8), true);
        }
        return null;
    }
    
    public IsoZombie createRealZombieNow(final float n, final float n2, final float n3) {
        this.choices.clear();
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (gridSquare == null) {
            return null;
        }
        this.choices.add(gridSquare);
        if (!this.choices.isEmpty()) {
            return this.createRealZombie(Rand.Next(8), false);
        }
        return null;
    }
    
    private int getZombieCountForRoom(final IsoRoom isoRoom) {
        if (IsoWorld.getZombiesDisabled()) {
            return 0;
        }
        if (GameClient.bClient) {
            return 0;
        }
        if (Core.bLastStand) {
            return 0;
        }
        int b = 7;
        if (SandboxOptions.instance.Zombies.getValue() == 1) {
            b = 3;
        }
        else if (SandboxOptions.instance.Zombies.getValue() == 2) {
            b = 4;
        }
        else if (SandboxOptions.instance.Zombies.getValue() == 3) {
            b = 6;
        }
        else if (SandboxOptions.instance.Zombies.getValue() == 5) {
            b = 15;
        }
        float lootZombieIntensity = 0.0f;
        final IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(isoRoom.def.x / 10, isoRoom.def.y / 10);
        if (metaChunk != null) {
            lootZombieIntensity = metaChunk.getLootZombieIntensity();
            if (lootZombieIntensity > 4.0f) {
                b -= (int)(lootZombieIntensity / 2.0f - 2.0f);
            }
        }
        if (isoRoom.def.getArea() > 100) {
            b -= 2;
        }
        final int max = Math.max(2, b);
        if (isoRoom.getBuilding() != null) {
            final int area = isoRoom.def.getArea();
            if (isoRoom.getBuilding().getRoomsNumber() > 100 && area >= 20) {
                int n = isoRoom.getBuilding().getRoomsNumber() - 95;
                if (n > 20) {
                    n = 20;
                }
                if (SandboxOptions.instance.Zombies.getValue() == 1) {
                    n += 10;
                }
                else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                    n += 7;
                }
                else if (SandboxOptions.instance.Zombies.getValue() == 3) {
                    n += 5;
                }
                else if (SandboxOptions.instance.Zombies.getValue() == 4) {
                    n -= 10;
                }
                if (area < 30) {
                    n -= 6;
                }
                if (area < 50) {
                    n -= 10;
                }
                if (area < 70) {
                    n -= 13;
                }
                return Rand.Next(n, n + 10);
            }
        }
        if (Rand.Next(max) == 0) {
            int b2 = (int)(1 + (lootZombieIntensity / 2.0f - 2.0f));
            if (isoRoom.def.getArea() < 30) {
                b2 -= 4;
            }
            if (isoRoom.def.getArea() > 85) {
                b2 += 2;
            }
            if (isoRoom.getBuilding().getRoomsNumber() < 7) {
                b2 -= 2;
            }
            if (SandboxOptions.instance.Zombies.getValue() == 1) {
                b2 += 3;
            }
            else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                b2 += 2;
            }
            else if (SandboxOptions.instance.Zombies.getValue() == 3) {
                ++b2;
            }
            else if (SandboxOptions.instance.Zombies.getValue() == 5) {
                b2 -= 2;
            }
            final int min = Math.min(7, Math.max(0, b2));
            return Rand.Next(min, min + 2);
        }
        return 0;
    }
    
    public void roomSpotted(final IsoRoom isoRoom) {
        if (GameClient.bClient) {
            return;
        }
        isoRoom.def.forEachChunk((roomDef, isoChunk) -> isoChunk.addSpawnedRoom(roomDef.ID));
        if (isoRoom.def.spawnCount == -1) {
            isoRoom.def.spawnCount = this.getZombieCountForRoom(isoRoom);
        }
        if (isoRoom.def.spawnCount <= 0) {
            return;
        }
        if (isoRoom.getBuilding().getDef().isFullyStreamedIn()) {
            ZombieSpawnRecorder.instance.record(this.addZombiesToMap(isoRoom.def.spawnCount, isoRoom.def, false), "roomSpotted");
        }
        else {
            this.m_tempZombies.clear();
            isoRoom.def.forEachChunk((p1, isoChunk2) -> this.addIndoorZombiesToChunk(isoChunk2, isoRoom, isoRoom.def.spawnCount, this.m_tempZombies));
            ZombieSpawnRecorder.instance.record(this.m_tempZombies, "roomSpotted");
        }
    }
    
    private boolean isBlockedInAllDirections(final int n, final int n2, final int n3) {
        final IsoGridSquare isoGridSquare = GameServer.bServer ? ServerMap.instance.getGridSquare(n, n2, n3) : IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare == null) {
            return false;
        }
        final boolean b = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 1, 0, 1) && isoGridSquare.nav[IsoDirections.N.index()] != null;
        final boolean b2 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 1, 2, 1) && isoGridSquare.nav[IsoDirections.S.index()] != null;
        final boolean b3 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 0, 1, 1) && isoGridSquare.nav[IsoDirections.W.index()] != null;
        final boolean b4 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 2, 1, 1) && isoGridSquare.nav[IsoDirections.E.index()] != null;
        return b && b2 && b3 && b4;
    }
    
    private boolean canPathOnlyN(final int n, final int n2, final int n3) {
        final IsoGridSquare isoGridSquare = GameServer.bServer ? ServerMap.instance.getGridSquare(n, n2, n3) : IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare == null) {
            return false;
        }
        final boolean b = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 1, 0, 1) && isoGridSquare.nav[IsoDirections.N.index()] != null;
        final boolean b2 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 1, 2, 1) && isoGridSquare.nav[IsoDirections.S.index()] != null;
        final boolean b3 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 0, 1, 1) && isoGridSquare.nav[IsoDirections.W.index()] != null;
        final boolean b4 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 2, 1, 1) && isoGridSquare.nav[IsoDirections.E.index()] != null;
        return !b && b2 && b3 && b4;
    }
    
    private boolean canPathOnlyS(final int n, final int n2, final int n3) {
        final IsoGridSquare isoGridSquare = GameServer.bServer ? ServerMap.instance.getGridSquare(n, n2, n3) : IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare == null) {
            return false;
        }
        final boolean b = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 1, 0, 1) && isoGridSquare.nav[IsoDirections.N.index()] != null;
        final boolean b2 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 1, 2, 1) && isoGridSquare.nav[IsoDirections.S.index()] != null;
        final boolean b3 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 0, 1, 1) && isoGridSquare.nav[IsoDirections.W.index()] != null;
        final boolean b4 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 2, 1, 1) && isoGridSquare.nav[IsoDirections.E.index()] != null;
        return b && !b2 && b3 && b4;
    }
    
    private boolean canPathOnlyW(final int n, final int n2, final int n3) {
        final IsoGridSquare isoGridSquare = GameServer.bServer ? ServerMap.instance.getGridSquare(n, n2, n3) : IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare == null) {
            return false;
        }
        final boolean b = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 1, 0, 1) && isoGridSquare.nav[IsoDirections.N.index()] != null;
        final boolean b2 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 1, 2, 1) && isoGridSquare.nav[IsoDirections.S.index()] != null;
        final boolean b3 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 0, 1, 1) && isoGridSquare.nav[IsoDirections.W.index()] != null;
        final boolean b4 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 2, 1, 1) && isoGridSquare.nav[IsoDirections.E.index()] != null;
        return b && b2 && !b3 && b4;
    }
    
    private boolean canPathOnlyE(final int n, final int n2, final int n3) {
        final IsoGridSquare isoGridSquare = GameServer.bServer ? ServerMap.instance.getGridSquare(n, n2, n3) : IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare == null) {
            return false;
        }
        final boolean b = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 1, 0, 1) && isoGridSquare.nav[IsoDirections.N.index()] != null;
        final boolean b2 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 1, 2, 1) && isoGridSquare.nav[IsoDirections.S.index()] != null;
        final boolean b3 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 0, 1, 1) && isoGridSquare.nav[IsoDirections.W.index()] != null;
        final boolean b4 = IsoGridSquare.getMatrixBit(isoGridSquare.pathMatrix, 2, 1, 1) && isoGridSquare.nav[IsoDirections.E.index()] != null;
        return b && b2 && b3 && !b4;
    }
    
    public boolean canSpawnAt(final int n, final int n2, final int n3) {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        return gridSquare != null && gridSquare.isFree(false) && !this.isBlockedInAllDirections(n, n2, n3) && (!this.canPathOnlyE(n, n2, n3) || !this.canPathOnlyW(n + 1, n2, n3)) && (!this.canPathOnlyE(n - 1, n2, n3) || !this.canPathOnlyW(n, n2, n3)) && (!this.canPathOnlyS(n, n2, n3) || !this.canPathOnlyN(n, n2 + 1, n3)) && (!this.canPathOnlyS(n, n2 - 1, n3) || !this.canPathOnlyN(n, n2, n3));
    }
    
    public int reusableZombiesSize() {
        return this.ReusableZombies.size();
    }
    
    static {
        VirtualZombieManager.instance = new VirtualZombieManager();
    }
}
