// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.iso.objects.IsoDeadBody;
import zombie.iso.IsoDirections;
import zombie.ui.TutorialManager;
import zombie.network.GameServer;
import zombie.characters.IsoZombie;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class BurntToDeath extends State
{
    private static final BurntToDeath _instance;
    
    public static BurntToDeath instance() {
        return BurntToDeath._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter instanceof IsoSurvivor) {
            isoGameCharacter.getDescriptor().bDead = true;
        }
        if (!(isoGameCharacter instanceof IsoZombie)) {
            isoGameCharacter.PlayAnimUnlooped("Die");
        }
        else {
            isoGameCharacter.PlayAnimUnlooped("ZombieDeath");
        }
        isoGameCharacter.def.AnimFrameIncrease = 0.25f;
        isoGameCharacter.setStateMachineLocked(true);
        isoGameCharacter.getEmitter().playVocals(isoGameCharacter.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath");
        if (GameServer.bServer && isoGameCharacter instanceof IsoZombie) {
            GameServer.sendZombieSound(IsoZombie.ZombieSound.Burned, (IsoZombie)isoGameCharacter);
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        if ((int)isoGameCharacter.def.Frame == isoGameCharacter.sprite.CurrentAnim.Frames.size() - 1) {
            if (isoGameCharacter == TutorialManager.instance.wife) {
                isoGameCharacter.dir = IsoDirections.S;
            }
            isoGameCharacter.RemoveAttachedAnims();
            if (GameServer.bServer && isoGameCharacter instanceof IsoZombie) {
                GameServer.sendZombieDeath((IsoZombie)isoGameCharacter);
            }
            final IsoDeadBody isoDeadBody = new IsoDeadBody(isoGameCharacter);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
    }
    
    static {
        _instance = new BurntToDeath();
    }
}
