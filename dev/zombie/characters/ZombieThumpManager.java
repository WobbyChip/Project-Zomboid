// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.core.math.PZMath;
import fmod.fmod.FMODManager;
import zombie.iso.IsoObject;

public final class ZombieThumpManager extends BaseZombieSoundManager
{
    public static final ZombieThumpManager instance;
    
    public ZombieThumpManager() {
        super(40, 100);
    }
    
    @Override
    public void playSound(final IsoZombie isoZombie) {
        long n = 0L;
        if (isoZombie.thumpFlag == 1) {
            n = isoZombie.getEmitter().playSoundImpl("ZombieThumpGeneric", null);
        }
        else if (isoZombie.thumpFlag == 2) {
            isoZombie.getEmitter().playSoundImpl("ZombieThumpGeneric", null);
            n = isoZombie.getEmitter().playSoundImpl("ZombieThumpWindow", null);
        }
        else if (isoZombie.thumpFlag == 3) {
            n = isoZombie.getEmitter().playSoundImpl("ZombieThumpWindow", null);
        }
        else if (isoZombie.thumpFlag == 4) {
            n = isoZombie.getEmitter().playSoundImpl("ZombieThumpMetal", null);
        }
        isoZombie.getEmitter().setParameterValue(n, FMODManager.instance.getParameterDescription("ObjectCondition"), PZMath.ceil(isoZombie.getThumpCondition() * 100.0f));
    }
    
    @Override
    public void postUpdate() {
        for (int i = 0; i < this.characters.size(); ++i) {
            this.characters.get(i).setThumpFlag(0);
        }
    }
    
    static {
        instance = new ZombieThumpManager();
    }
}
