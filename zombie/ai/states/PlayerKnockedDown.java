// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.GameTime;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class PlayerKnockedDown extends State
{
    private static final PlayerKnockedDown _instance;
    
    public static PlayerKnockedDown instance() {
        return PlayerKnockedDown._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(true);
        ((IsoPlayer)isoGameCharacter).setBlockMovement(true);
        isoGameCharacter.setHitReaction("");
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter.isDead()) {
            if (!GameServer.bServer && !GameClient.bClient) {
                isoGameCharacter.Kill(null);
            }
        }
        else {
            isoGameCharacter.setReanimateTimer(isoGameCharacter.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6f);
        }
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
            isoGameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
        if (animEvent.m_EventName.equalsIgnoreCase("FallOnBack")) {
            isoGameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
        if (animEvent.m_EventName.equalsIgnoreCase("setSitOnGround")) {
            isoGameCharacter.setSitOnGround(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(false);
        ((IsoPlayer)isoGameCharacter).setBlockMovement(false);
        ((IsoPlayer)isoGameCharacter).setKnockedDown(false);
        isoGameCharacter.setOnFloor(true);
    }
    
    static {
        _instance = new PlayerKnockedDown();
    }
}
