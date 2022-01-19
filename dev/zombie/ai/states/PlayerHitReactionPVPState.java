// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.network.GameServer;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class PlayerHitReactionPVPState extends State
{
    private static final PlayerHitReactionPVPState _instance;
    
    public static PlayerHitReactionPVPState instance() {
        return PlayerHitReactionPVPState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        if (!isoGameCharacter.getCharacterActions().isEmpty()) {
            isoGameCharacter.getCharacterActions().get(0).forceStop();
        }
        isoGameCharacter.setSitOnGround(false);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(false);
        isoGameCharacter.setHitReaction("");
        isoGameCharacter.setVariable("hitpvp", false);
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if (animEvent.m_EventName.equalsIgnoreCase("PushAwayZombie")) {
            isoGameCharacter.getAttackedBy().setHitForce(0.03f);
            if (isoGameCharacter.getAttackedBy() instanceof IsoZombie) {
                ((IsoZombie)isoGameCharacter.getAttackedBy()).setPlayerAttackPosition(null);
                ((IsoZombie)isoGameCharacter.getAttackedBy()).setStaggerBack(true);
            }
        }
        if (animEvent.m_EventName.equalsIgnoreCase("Defend")) {
            isoGameCharacter.getAttackedBy().setHitReaction("BiteDefended");
        }
        if (animEvent.m_EventName.equalsIgnoreCase("DeathSound")) {
            if (isoGameCharacter.isPlayingDeathSound()) {
                return;
            }
            isoGameCharacter.setPlayingDeathSound(true);
            String s = "Male";
            if (isoGameCharacter.isFemale()) {
                s = "Female";
            }
            isoGameCharacter.playSound(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        if (animEvent.m_EventName.equalsIgnoreCase("Death")) {
            isoGameCharacter.setOnFloor(true);
            if (!GameServer.bServer) {
                isoGameCharacter.Kill(isoGameCharacter.getAttackedBy());
            }
        }
    }
    
    static {
        _instance = new PlayerHitReactionPVPState();
    }
}
