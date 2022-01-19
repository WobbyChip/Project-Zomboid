// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

public final class ZombieFootstepManager extends BaseZombieSoundManager
{
    public static final ZombieFootstepManager instance;
    
    public ZombieFootstepManager() {
        super(40, 500);
    }
    
    @Override
    public void playSound(final IsoZombie isoZombie) {
        isoZombie.getEmitter().playFootsteps("ZombieFootstepsCombined", isoZombie.getFootstepVolume());
    }
    
    @Override
    public void postUpdate() {
    }
    
    static {
        instance = new ZombieFootstepManager();
    }
}
