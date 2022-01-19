// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.math.PZMath;
import zombie.iso.IsoDirections;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.iso.Vector2;
import zombie.ai.State;

public final class CrawlingZombieTurnState extends State
{
    private static final CrawlingZombieTurnState _instance;
    private static final Vector2 tempVector2_1;
    private static final Vector2 tempVector2_2;
    
    public static CrawlingZombieTurnState instance() {
        return CrawlingZombieTurnState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        ((IsoZombie)isoGameCharacter).AllowRepathDelay = 0.0f;
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if (animEvent.m_EventName.equalsIgnoreCase("TurnSome")) {
            isoGameCharacter.setForwardDirection(PZMath.lerp(CrawlingZombieTurnState.tempVector2_2, CrawlingZombieTurnState.tempVector2_1.set(isoGameCharacter.dir.ToVector()), "left".equalsIgnoreCase(animEvent.m_ParameterValue) ? IsoDirections.fromIndex(isoGameCharacter.dir.index() + 1).ToVector() : IsoDirections.fromIndex(isoGameCharacter.dir.index() - 1).ToVector(), animEvent.m_TimePc));
            return;
        }
        if (animEvent.m_EventName.equalsIgnoreCase("TurnComplete")) {
            if ("left".equalsIgnoreCase(animEvent.m_ParameterValue)) {
                isoGameCharacter.dir = IsoDirections.fromIndex(isoGameCharacter.dir.index() + 1);
            }
            else {
                isoGameCharacter.dir = IsoDirections.fromIndex(isoGameCharacter.dir.index() - 1);
            }
            isoGameCharacter.getVectorFromDirection(isoGameCharacter.getForwardDirection());
        }
    }
    
    public static boolean calculateDir(final IsoGameCharacter isoGameCharacter, final IsoDirections isoDirections) {
        if (isoDirections.index() > isoGameCharacter.dir.index()) {
            return isoDirections.index() - isoGameCharacter.dir.index() <= 4;
        }
        return isoDirections.index() - isoGameCharacter.dir.index() < -4;
    }
    
    static {
        _instance = new CrawlingZombieTurnState();
        tempVector2_1 = new Vector2();
        tempVector2_2 = new Vector2();
    }
}
