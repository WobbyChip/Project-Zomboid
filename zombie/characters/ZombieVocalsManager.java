// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

public final class ZombieVocalsManager extends BaseZombieSoundManager
{
    public static final ZombieVocalsManager instance;
    
    public ZombieVocalsManager() {
        super(40, 1000);
    }
    
    @Override
    public void playSound(final IsoZombie isoZombie) {
    }
    
    @Override
    public void postUpdate() {
    }
    
    static {
        instance = new ZombieVocalsManager();
    }
}
