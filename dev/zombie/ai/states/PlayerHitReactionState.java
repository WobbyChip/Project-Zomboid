// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.network.GameServer;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoMovingObject;
import zombie.network.GameClient;
import zombie.debug.DebugLog;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class PlayerHitReactionState extends State
{
    private static final PlayerHitReactionState _instance;
    
    public static PlayerHitReactionState instance() {
        return PlayerHitReactionState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(true);
        if (!isoGameCharacter.getCharacterActions().isEmpty()) {
            isoGameCharacter.getCharacterActions().get(0).forceStop();
        }
        isoGameCharacter.setIsAiming(false);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(false);
        isoGameCharacter.setHitReaction("");
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if (isoGameCharacter.getAttackedBy() == null || !(isoGameCharacter.getAttackedBy() instanceof IsoZombie)) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, animEvent.m_EventName));
            return;
        }
        if (animEvent.m_EventName.equalsIgnoreCase("PushAwayZombie")) {
            isoGameCharacter.getAttackedBy().setHitForce(0.03f);
            ((IsoZombie)isoGameCharacter.getAttackedBy()).setPlayerAttackPosition(null);
            ((IsoZombie)isoGameCharacter.getAttackedBy()).setStaggerBack(true);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("Defend")) {
            isoGameCharacter.getAttackedBy().setHitReaction("BiteDefended");
            if (GameClient.bClient) {
                GameClient.sendHitCharacter(isoGameCharacter.getAttackedBy(), isoGameCharacter, null, 0.0f, false, 1.0f, false, false, false);
            }
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
        _instance = new PlayerHitReactionState();
    }
}
