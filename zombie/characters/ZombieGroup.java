// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.ai.ZombieGroupManager;
import zombie.iso.IsoUtils;
import zombie.SandboxOptions;
import java.util.ArrayList;

public final class ZombieGroup
{
    private final ArrayList<IsoZombie> members;
    public float lastSpreadOutTime;
    
    public ZombieGroup() {
        this.members = new ArrayList<IsoZombie>();
    }
    
    public ZombieGroup reset() {
        this.members.clear();
        this.lastSpreadOutTime = -1.0f;
        return this;
    }
    
    public void add(final IsoZombie isoZombie) {
        if (this.members.contains(isoZombie)) {
            return;
        }
        if (isoZombie.group != null) {
            isoZombie.group.remove(isoZombie);
        }
        this.members.add(isoZombie);
        isoZombie.group = this;
    }
    
    public void remove(final IsoZombie o) {
        this.members.remove(o);
        o.group = null;
    }
    
    public IsoZombie getLeader() {
        return this.members.isEmpty() ? null : this.members.get(0);
    }
    
    public boolean isEmpty() {
        return this.members.isEmpty();
    }
    
    public int size() {
        return this.members.size();
    }
    
    public void update() {
        final int value = SandboxOptions.instance.zombieConfig.RallyTravelDistance.getValue();
        for (int i = 0; i < this.members.size(); ++i) {
            final IsoZombie isoZombie = this.members.get(i);
            float distanceToSquared = 0.0f;
            if (i > 0) {
                distanceToSquared = IsoUtils.DistanceToSquared(this.members.get(0).getX(), this.members.get(0).getY(), isoZombie.getX(), isoZombie.getY());
            }
            if (isoZombie.group != this || distanceToSquared > value * value || !ZombieGroupManager.instance.shouldBeInGroup(isoZombie)) {
                if (isoZombie.group == this) {
                    isoZombie.group = null;
                }
                this.members.remove(i--);
            }
        }
    }
}
