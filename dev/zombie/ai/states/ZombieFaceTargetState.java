// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.iso.IsoObject;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ZombieFaceTargetState extends State
{
    private static final ZombieFaceTargetState _instance;
    
    public static ZombieFaceTargetState instance() {
        return ZombieFaceTargetState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        if (isoZombie.getTarget() != null) {
            isoZombie.faceThisObject(isoZombie.getTarget());
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
    }
    
    static {
        _instance = new ZombieFaceTargetState();
    }
}
