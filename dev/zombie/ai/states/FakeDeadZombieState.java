// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.Core;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameServer;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class FakeDeadZombieState extends State
{
    private static final FakeDeadZombieState _instance;
    
    public static FakeDeadZombieState instance() {
        return FakeDeadZombieState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setVisibleToNPCs(false);
        isoGameCharacter.setCollidable(false);
        ((IsoZombie)isoGameCharacter).setFakeDead(true);
        isoGameCharacter.setOnFloor(true);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter.isDead()) {
            if (GameServer.bServer) {
                GameServer.sendZombieDeath((IsoZombie)isoGameCharacter);
            }
            new IsoDeadBody(isoGameCharacter);
            return;
        }
        if (Core.bLastStand) {
            ((IsoZombie)isoGameCharacter).setFakeDead(false);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        ((IsoZombie)isoGameCharacter).setFakeDead(false);
    }
    
    static {
        _instance = new FakeDeadZombieState();
    }
}
