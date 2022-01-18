// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.Stats;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class FakeDeadAttackState extends State
{
    private static final FakeDeadAttackState _instance;
    
    public static FakeDeadAttackState instance() {
        return FakeDeadAttackState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        isoZombie.DirectionFromVector(isoZombie.vectorToTarget);
        isoZombie.setFakeDead(false);
        isoGameCharacter.setVisibleToNPCs(true);
        isoGameCharacter.setCollidable(true);
        String s = "MaleZombieAttack";
        if (isoGameCharacter.isFemale()) {
            s = "FemaleZombieAttack";
        }
        isoGameCharacter.getEmitter().playSound(s);
        if (isoZombie.target instanceof IsoPlayer && !((IsoPlayer)isoZombie.target).getCharacterTraits().Desensitized.isSet()) {
            final IsoPlayer isoPlayer = (IsoPlayer)isoZombie.target;
            final Stats stats = isoPlayer.getStats();
            stats.Panic += isoPlayer.getBodyDamage().getPanicIncreaseValue() * 3.0f;
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        final IsoZombie isoZombie = (IsoZombie)isoGameCharacter;
        if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck") && isoGameCharacter.isAlive() && isoZombie.isTargetInCone(1.5f, 0.9f) && isoZombie.target instanceof IsoGameCharacter) {
            final IsoGameCharacter isoGameCharacter2 = (IsoGameCharacter)isoZombie.target;
            if (isoGameCharacter2.getVehicle() == null || isoGameCharacter2.getVehicle().couldCrawlerAttackPassenger(isoGameCharacter2)) {
                isoGameCharacter2.getBodyDamage().AddRandomDamageFromZombie((IsoZombie)isoGameCharacter, null);
            }
        }
        if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
            isoZombie.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
        if (animEvent.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {
            isoZombie.setCrawler(true);
        }
    }
    
    static {
        _instance = new FakeDeadAttackState();
    }
}
