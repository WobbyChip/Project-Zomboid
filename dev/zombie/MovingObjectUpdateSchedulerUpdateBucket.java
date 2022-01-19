// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.util.Type;
import zombie.characters.IsoZombie;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.IsoMovingObject;
import java.util.ArrayList;

public final class MovingObjectUpdateSchedulerUpdateBucket
{
    public int frameMod;
    ArrayList<IsoMovingObject>[] buckets;
    
    public MovingObjectUpdateSchedulerUpdateBucket(final int n) {
        this.init(n);
    }
    
    public void init(final int frameMod) {
        this.frameMod = frameMod;
        this.buckets = (ArrayList<IsoMovingObject>[])new ArrayList[frameMod];
        for (int i = 0; i < this.buckets.length; ++i) {
            this.buckets[i] = new ArrayList<IsoMovingObject>();
        }
    }
    
    public void clear() {
        for (int i = 0; i < this.buckets.length; ++i) {
            this.buckets[i].clear();
        }
    }
    
    public void remove(final IsoMovingObject o) {
        for (int i = 0; i < this.buckets.length; ++i) {
            this.buckets[i].remove(o);
        }
    }
    
    public void add(final IsoMovingObject e) {
        this.buckets[e.getID() % this.frameMod].add(e);
    }
    
    public void update(final int n) {
        GameTime.getInstance().PerObjectMultiplier = (float)this.frameMod;
        final ArrayList<IsoMovingObject> list = this.buckets[n % this.frameMod];
        for (int i = 0; i < list.size(); ++i) {
            final IsoMovingObject e = list.get(i);
            if (e instanceof IsoDeadBody) {
                IsoWorld.instance.getCell().getRemoveList().add(e);
            }
            else {
                final IsoZombie isoZombie = Type.tryCastTo(e, IsoZombie.class);
                if (isoZombie != null && VirtualZombieManager.instance.isReused(isoZombie)) {
                    DebugLog.log(DebugType.Zombie, invokedynamic(makeConcatWithConstants:(Lzombie/iso/IsoMovingObject;)Ljava/lang/String;, e));
                }
                else {
                    e.preupdate();
                    e.update();
                }
            }
        }
        GameTime.getInstance().PerObjectMultiplier = 1.0f;
    }
    
    public void postupdate(final int n) {
        GameTime.getInstance().PerObjectMultiplier = (float)this.frameMod;
        final ArrayList<IsoMovingObject> list = this.buckets[n % this.frameMod];
        for (int i = 0; i < list.size(); ++i) {
            final IsoMovingObject isoMovingObject = list.get(i);
            final IsoZombie isoZombie = Type.tryCastTo(isoMovingObject, IsoZombie.class);
            if (isoZombie != null && VirtualZombieManager.instance.isReused(isoZombie)) {
                DebugLog.log(DebugType.Zombie, invokedynamic(makeConcatWithConstants:(Lzombie/iso/IsoMovingObject;)Ljava/lang/String;, isoMovingObject));
            }
            else {
                isoMovingObject.postupdate();
            }
        }
        GameTime.getInstance().PerObjectMultiplier = 1.0f;
    }
    
    public void removeObject(final IsoMovingObject o) {
        for (int i = 0; i < this.buckets.length; ++i) {
            this.buckets[i].remove(o);
        }
    }
    
    public ArrayList<IsoMovingObject> getBucket(final int n) {
        return this.buckets[n % this.frameMod];
    }
}
