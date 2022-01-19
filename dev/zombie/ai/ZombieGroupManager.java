// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai;

import zombie.iso.IsoCell;
import zombie.core.Rand;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.PathFindState;
import zombie.iso.IsoWorld;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.ai.states.ZombieIdleState;
import zombie.network.GameClient;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoGridSquare;
import zombie.VirtualZombieManager;
import zombie.core.Core;
import zombie.characters.IsoZombie;
import java.util.Collection;
import zombie.SandboxOptions;
import zombie.GameTime;
import zombie.iso.Vector3;
import zombie.iso.Vector2;
import java.util.ArrayDeque;
import zombie.characters.ZombieGroup;
import java.util.ArrayList;

public final class ZombieGroupManager
{
    public static final ZombieGroupManager instance;
    private final ArrayList<ZombieGroup> groups;
    private final ArrayDeque<ZombieGroup> freeGroups;
    private final Vector2 tempVec2;
    private final Vector3 tempVec3;
    private float tickCount;
    
    public ZombieGroupManager() {
        this.groups = new ArrayList<ZombieGroup>();
        this.freeGroups = new ArrayDeque<ZombieGroup>();
        this.tempVec2 = new Vector2();
        this.tempVec3 = new Vector3();
        this.tickCount = 30.0f;
    }
    
    public void preupdate() {
        this.tickCount += GameTime.getInstance().getMultiplier() / 1.6f;
        if (this.tickCount >= 30.0f) {
            this.tickCount = 0.0f;
        }
        SandboxOptions.instance.zombieConfig.RallyGroupSize.getValue();
        for (int i = 0; i < this.groups.size(); ++i) {
            final ZombieGroup e = this.groups.get(i);
            e.update();
            if (e.isEmpty()) {
                this.freeGroups.push(e);
                this.groups.remove(i--);
            }
        }
    }
    
    public void Reset() {
        this.freeGroups.addAll(this.groups);
        this.groups.clear();
    }
    
    public boolean shouldBeInGroup(final IsoZombie isoZombie) {
        if (isoZombie == null) {
            return false;
        }
        if (SandboxOptions.instance.zombieConfig.RallyGroupSize.getValue() <= 1) {
            return false;
        }
        if (!Core.getInstance().isZombieGroupSound()) {
            return false;
        }
        if (isoZombie.isUseless()) {
            return false;
        }
        if (isoZombie.isDead() || isoZombie.isFakeDead()) {
            return false;
        }
        if (isoZombie.isSitAgainstWall()) {
            return false;
        }
        if (isoZombie.target != null) {
            return false;
        }
        if (isoZombie.getCurrentBuilding() != null) {
            return false;
        }
        if (VirtualZombieManager.instance.isReused(isoZombie)) {
            return false;
        }
        final IsoGridSquare square = isoZombie.getSquare();
        final IsoMetaGrid.Zone zone = (square == null) ? null : square.getZone();
        return zone == null || (!"Forest".equals(zone.getType()) && !"DeepForest".equals(zone.getType()));
    }
    
    public void update(final IsoZombie isoZombie) {
        if (GameClient.bClient && isoZombie.isRemoteZombie()) {
            return;
        }
        if (!this.shouldBeInGroup(isoZombie)) {
            if (isoZombie.group != null) {
                isoZombie.group.remove(isoZombie);
            }
            return;
        }
        if (this.tickCount != 0.0f) {
            return;
        }
        if (isoZombie.group == null) {
            final ZombieGroup nearestGroup = this.findNearestGroup(isoZombie.getX(), isoZombie.getY(), isoZombie.getZ());
            if (nearestGroup == null) {
                final ZombieGroup e = this.freeGroups.isEmpty() ? new ZombieGroup() : this.freeGroups.pop().reset();
                e.add(isoZombie);
                this.groups.add(e);
                return;
            }
            nearestGroup.add(isoZombie);
        }
        if (isoZombie.getCurrentState() != ZombieIdleState.instance()) {
            return;
        }
        if (isoZombie == isoZombie.group.getLeader()) {
            final float n = (float)GameTime.getInstance().getWorldAgeHours();
            isoZombie.group.lastSpreadOutTime = Math.min(isoZombie.group.lastSpreadOutTime, n);
            if (isoZombie.group.lastSpreadOutTime + 0.083333336f > n) {
                return;
            }
            isoZombie.group.lastSpreadOutTime = n;
            final int value = SandboxOptions.instance.zombieConfig.RallyGroupSeparation.getValue();
            final Vector2 set = this.tempVec2.set(0.0f, 0.0f);
            for (int i = 0; i < this.groups.size(); ++i) {
                final ZombieGroup zombieGroup = this.groups.get(i);
                if (zombieGroup.getLeader() != null) {
                    if (zombieGroup != isoZombie.group) {
                        if ((int)zombieGroup.getLeader().getZ() == (int)isoZombie.getZ()) {
                            final float x = zombieGroup.getLeader().getX();
                            final float y = zombieGroup.getLeader().getY();
                            if (IsoUtils.DistanceToSquared(isoZombie.x, isoZombie.y, x, y) <= value * value) {
                                set.x = set.x - x + isoZombie.x;
                                set.y = set.y - y + isoZombie.y;
                            }
                        }
                    }
                }
            }
            if (this.lineClearCollideCount(isoZombie, isoZombie.getCell(), (int)(isoZombie.x + set.x), (int)(isoZombie.y + set.y), (int)isoZombie.z, (int)isoZombie.x, (int)isoZombie.y, (int)isoZombie.z, 10, this.tempVec3) < 1) {
                return;
            }
            if (!GameClient.bClient && !GameServer.bServer && IsoPlayer.getInstance().getHoursSurvived() < 2.0) {
                return;
            }
            if (this.tempVec3.x < 0.0f || this.tempVec3.y < 0.0f || !IsoWorld.instance.MetaGrid.isValidChunk((int)this.tempVec3.x / 10, (int)this.tempVec3.y / 10)) {
                return;
            }
            isoZombie.pathToLocation((int)(this.tempVec3.x + 0.5f), (int)(this.tempVec3.y + 0.5f), (int)this.tempVec3.z);
            if (isoZombie.getCurrentState() == PathFindState.instance() || isoZombie.getCurrentState() == WalkTowardState.instance()) {
                isoZombie.setLastHeardSound(isoZombie.getPathTargetX(), isoZombie.getPathTargetY(), isoZombie.getPathTargetZ());
                isoZombie.AllowRepathDelay = 400.0f;
            }
        }
        else {
            final float x2 = isoZombie.group.getLeader().getX();
            final float y2 = isoZombie.group.getLeader().getY();
            final int value2 = SandboxOptions.instance.zombieConfig.RallyGroupRadius.getValue();
            if (IsoUtils.DistanceToSquared(isoZombie.x, isoZombie.y, x2, y2) < value2 * value2) {
                return;
            }
            if (!GameClient.bClient && !GameServer.bServer && IsoPlayer.getInstance().getHoursSurvived() < 2.0 && !Core.bDebug) {
                return;
            }
            final int n2 = (int)(x2 + Rand.Next(-value2, value2));
            final int n3 = (int)(y2 + Rand.Next(-value2, value2));
            if (n2 < 0 || n3 < 0 || !IsoWorld.instance.MetaGrid.isValidChunk(n2 / 10, n3 / 10)) {
                return;
            }
            isoZombie.pathToLocation(n2, n3, (int)isoZombie.group.getLeader().getZ());
            if (isoZombie.getCurrentState() == PathFindState.instance() || isoZombie.getCurrentState() == WalkTowardState.instance()) {
                isoZombie.setLastHeardSound(isoZombie.getPathTargetX(), isoZombie.getPathTargetY(), isoZombie.getPathTargetZ());
                isoZombie.AllowRepathDelay = 400.0f;
            }
        }
    }
    
    public ZombieGroup findNearestGroup(final float n, final float n2, final float n3) {
        ZombieGroup zombieGroup = null;
        float n4 = Float.MAX_VALUE;
        final int value = SandboxOptions.instance.zombieConfig.RallyTravelDistance.getValue();
        for (int i = 0; i < this.groups.size(); ++i) {
            final ZombieGroup zombieGroup2 = this.groups.get(i);
            if (zombieGroup2.isEmpty()) {
                this.groups.remove(i--);
            }
            else if ((int)zombieGroup2.getLeader().getZ() == (int)n3) {
                if (zombieGroup2.size() < SandboxOptions.instance.zombieConfig.RallyGroupSize.getValue()) {
                    final float distanceToSquared = IsoUtils.DistanceToSquared(n, n2, zombieGroup2.getLeader().getX(), zombieGroup2.getLeader().getY());
                    if (distanceToSquared < value * value && distanceToSquared < n4) {
                        n4 = distanceToSquared;
                        zombieGroup = zombieGroup2;
                    }
                }
            }
        }
        return zombieGroup;
    }
    
    private int lineClearCollideCount(final IsoMovingObject isoMovingObject, final IsoCell isoCell, final int n, final int n2, final int n3, int i, int j, int k, final int n4, final Vector3 vector3) {
        int n5 = 0;
        final int a = n2 - j;
        final int a2 = n - i;
        final int n6 = n3 - k;
        final float n7 = 0.5f;
        final float n8 = 0.5f;
        IsoGridSquare gridSquare = isoCell.getGridSquare(i, j, k);
        vector3.set((float)i, (float)j, (float)k);
        if (Math.abs(a2) > Math.abs(a) && Math.abs(a2) > Math.abs(n6)) {
            final float n9 = a / (float)a2;
            final float n10 = n6 / (float)a2;
            float n11 = n7 + j;
            float n12 = n8 + k;
            final int n13 = (a2 < 0) ? -1 : 1;
            final float n14 = n9 * n13;
            final float n15 = n10 * n13;
            while (i != n) {
                i += n13;
                n11 += n14;
                n12 += n15;
                final IsoGridSquare gridSquare2 = isoCell.getGridSquare(i, (int)n11, (int)n12);
                if (gridSquare2 != null && gridSquare != null && gridSquare2.testCollideAdjacent(isoMovingObject, gridSquare.getX() - gridSquare2.getX(), gridSquare.getY() - gridSquare2.getY(), gridSquare.getZ() - gridSquare2.getZ())) {
                    return n5;
                }
                gridSquare = gridSquare2;
                vector3.set((float)i, (float)(int)n11, (float)(int)n12);
                if (++n5 >= n4) {
                    return n5;
                }
            }
        }
        else if (Math.abs(a) >= Math.abs(a2) && Math.abs(a) > Math.abs(n6)) {
            final float n16 = a2 / (float)a;
            final float n17 = n6 / (float)a;
            float n18 = n7 + i;
            float n19 = n8 + k;
            final int n20 = (a < 0) ? -1 : 1;
            final float n21 = n16 * n20;
            final float n22 = n17 * n20;
            while (j != n2) {
                j += n20;
                n18 += n21;
                n19 += n22;
                final IsoGridSquare gridSquare3 = isoCell.getGridSquare((int)n18, j, (int)n19);
                if (gridSquare3 != null && gridSquare != null && gridSquare3.testCollideAdjacent(isoMovingObject, gridSquare.getX() - gridSquare3.getX(), gridSquare.getY() - gridSquare3.getY(), gridSquare.getZ() - gridSquare3.getZ())) {
                    return n5;
                }
                gridSquare = gridSquare3;
                vector3.set((float)(int)n18, (float)j, (float)(int)n19);
                if (++n5 >= n4) {
                    return n5;
                }
            }
        }
        else {
            final float n23 = a2 / (float)n6;
            final float n24 = a / (float)n6;
            float n25 = n7 + i;
            float n26 = n8 + j;
            final int n27 = (n6 < 0) ? -1 : 1;
            final float n28 = n23 * n27;
            final float n29 = n24 * n27;
            while (k != n3) {
                k += n27;
                n25 += n28;
                n26 += n29;
                final IsoGridSquare gridSquare4 = isoCell.getGridSquare((int)n25, (int)n26, k);
                if (gridSquare4 != null && gridSquare != null && gridSquare4.testCollideAdjacent(isoMovingObject, gridSquare.getX() - gridSquare4.getX(), gridSquare.getY() - gridSquare4.getY(), gridSquare.getZ() - gridSquare4.getZ())) {
                    return n5;
                }
                gridSquare = gridSquare4;
                vector3.set((float)(int)n25, (float)(int)n26, (float)k);
                if (++n5 >= n4) {
                    return n5;
                }
            }
        }
        return n5;
    }
    
    static {
        instance = new ZombieGroupManager();
    }
}
