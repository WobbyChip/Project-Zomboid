// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class PlayerFallDownState extends State
{
    private static final PlayerFallDownState _instance;
    
    public static PlayerFallDownState instance() {
        return PlayerFallDownState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(true);
        isoGameCharacter.clearVariable("bKnockedDown");
        if (isoGameCharacter.isDead() && !GameServer.bServer && !GameClient.bClient) {
            isoGameCharacter.Kill(null);
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(false);
        isoGameCharacter.setOnFloor(true);
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if (GameClient.bClient && animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
            isoGameCharacter.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
    }
    
    static {
        _instance = new PlayerFallDownState();
    }
}
