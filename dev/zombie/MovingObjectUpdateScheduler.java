// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import java.util.ArrayList;
import zombie.vehicles.BaseVehicle;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.network.GameServer;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;

public final class MovingObjectUpdateScheduler
{
    public static final MovingObjectUpdateScheduler instance;
    final MovingObjectUpdateSchedulerUpdateBucket fullSimulation;
    final MovingObjectUpdateSchedulerUpdateBucket halfSimulation;
    final MovingObjectUpdateSchedulerUpdateBucket quarterSimulation;
    final MovingObjectUpdateSchedulerUpdateBucket eighthSimulation;
    final MovingObjectUpdateSchedulerUpdateBucket sixteenthSimulation;
    long frameCounter;
    private boolean isEnabled;
    
    public MovingObjectUpdateScheduler() {
        this.fullSimulation = new MovingObjectUpdateSchedulerUpdateBucket(1);
        this.halfSimulation = new MovingObjectUpdateSchedulerUpdateBucket(2);
        this.quarterSimulation = new MovingObjectUpdateSchedulerUpdateBucket(4);
        this.eighthSimulation = new MovingObjectUpdateSchedulerUpdateBucket(8);
        this.sixteenthSimulation = new MovingObjectUpdateSchedulerUpdateBucket(16);
        this.isEnabled = true;
    }
    
    public long getFrameCounter() {
        return this.frameCounter;
    }
    
    public void startFrame() {
        ++this.frameCounter;
        this.fullSimulation.clear();
        this.halfSimulation.clear();
        this.quarterSimulation.clear();
        this.eighthSimulation.clear();
        this.sixteenthSimulation.clear();
        final ArrayList<IsoMovingObject> objectList = IsoWorld.instance.getCell().getObjectList();
        for (int i = 0; i < objectList.size(); ++i) {
            final IsoMovingObject isoMovingObject = objectList.get(i);
            if (!GameServer.bServer || !(isoMovingObject instanceof IsoZombie)) {
                boolean b = false;
                boolean b2 = false;
                float n = 1.0E8f;
                boolean b3 = false;
                for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                    final IsoPlayer isoPlayer = IsoPlayer.players[j];
                    if (isoPlayer != null) {
                        if (isoMovingObject.getCurrentSquare() == null) {
                            isoMovingObject.setCurrent(IsoWorld.instance.getCell().getGridSquare(isoMovingObject.x, isoMovingObject.y, isoMovingObject.z));
                        }
                        if (isoPlayer == isoMovingObject) {
                            b3 = true;
                        }
                        if (isoMovingObject.getCurrentSquare() != null) {
                            if (isoMovingObject.getCurrentSquare().isCouldSee(j)) {
                                b = true;
                            }
                            if (isoMovingObject.getCurrentSquare().isCanSee(j)) {
                                b2 = true;
                            }
                            final float distTo = isoMovingObject.DistTo(isoPlayer);
                            if (distTo < n) {
                                n = distTo;
                            }
                        }
                    }
                }
                int n2 = 3;
                if (!b2) {
                    --n2;
                }
                if (!b && n > 10.0f) {
                    --n2;
                }
                if (n > 30.0f) {
                    --n2;
                }
                if (n > 60.0f) {
                    --n2;
                }
                if (n > 80.0f) {
                    --n2;
                }
                if (isoMovingObject instanceof IsoPlayer) {
                    n2 = 3;
                }
                if (isoMovingObject instanceof BaseVehicle) {
                    n2 = 3;
                }
                if (GameServer.bServer) {
                    n2 = 3;
                }
                if (b3) {
                    n2 = 3;
                }
                if (!this.isEnabled) {
                    n2 = 3;
                }
                if (n2 == 3) {
                    this.fullSimulation.add(isoMovingObject);
                }
                if (n2 == 2) {
                    this.halfSimulation.add(isoMovingObject);
                }
                if (n2 == 1) {
                    this.quarterSimulation.add(isoMovingObject);
                }
                if (n2 == 0) {
                    this.eighthSimulation.add(isoMovingObject);
                }
                if (n2 < 0) {
                    this.sixteenthSimulation.add(isoMovingObject);
                }
            }
        }
    }
    
    public void update() {
        GameTime.getInstance().PerObjectMultiplier = 1.0f;
        this.fullSimulation.update((int)this.frameCounter);
        this.halfSimulation.update((int)this.frameCounter);
        this.quarterSimulation.update((int)this.frameCounter);
        this.eighthSimulation.update((int)this.frameCounter);
        this.sixteenthSimulation.update((int)this.frameCounter);
    }
    
    public void postupdate() {
        GameTime.getInstance().PerObjectMultiplier = 1.0f;
        this.fullSimulation.postupdate((int)this.frameCounter);
        this.halfSimulation.postupdate((int)this.frameCounter);
        this.quarterSimulation.postupdate((int)this.frameCounter);
        this.eighthSimulation.postupdate((int)this.frameCounter);
        this.sixteenthSimulation.postupdate((int)this.frameCounter);
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    public void setEnabled(final boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
    public void removeObject(final IsoMovingObject isoMovingObject) {
        this.fullSimulation.removeObject(isoMovingObject);
        this.halfSimulation.removeObject(isoMovingObject);
        this.quarterSimulation.removeObject(isoMovingObject);
        this.eighthSimulation.removeObject(isoMovingObject);
        this.sixteenthSimulation.removeObject(isoMovingObject);
    }
    
    public ArrayList<IsoMovingObject> getBucket() {
        return this.fullSimulation.getBucket((int)this.frameCounter);
    }
    
    static {
        instance = new MovingObjectUpdateScheduler();
    }
}
