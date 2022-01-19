// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.util.StringUtils;
import zombie.core.Rand;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
import zombie.util.Type;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoFireplace;
import java.util.HashMap;
import zombie.inventory.types.HandWeapon;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class PlayerSitOnGroundState extends State
{
    private static final PlayerSitOnGroundState _instance;
    private static final int RAND_EXT = 2500;
    private static final Integer PARAM_FIRE;
    private static final Integer PARAM_SITGROUNDANIM;
    private static final Integer PARAM_CHECK_FIRE;
    private static final Integer PARAM_CHANGE_ANIM;
    
    public static PlayerSitOnGroundState instance() {
        return PlayerSitOnGroundState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        stateMachineParams.put(PlayerSitOnGroundState.PARAM_FIRE, this.checkFire(isoGameCharacter));
        stateMachineParams.put(PlayerSitOnGroundState.PARAM_CHECK_FIRE, System.currentTimeMillis());
        stateMachineParams.put(PlayerSitOnGroundState.PARAM_CHANGE_ANIM, 0L);
        isoGameCharacter.setSitOnGround(true);
        if ((isoGameCharacter.getPrimaryHandItem() == null || !(isoGameCharacter.getPrimaryHandItem() instanceof HandWeapon)) && (isoGameCharacter.getSecondaryHandItem() == null || !(isoGameCharacter.getSecondaryHandItem() instanceof HandWeapon))) {
            isoGameCharacter.setHideWeaponModel(true);
        }
        if (isoGameCharacter.getStateMachine().getPrevious() == IdleState.instance()) {
            isoGameCharacter.clearVariable("SitGroundStarted");
            isoGameCharacter.clearVariable("forceGetUp");
            isoGameCharacter.clearVariable("SitGroundAnim");
        }
    }
    
    private boolean checkFire(final IsoGameCharacter isoGameCharacter) {
        final IsoGridSquare currentSquare = isoGameCharacter.getCurrentSquare();
        for (int i = -4; i < 4; ++i) {
            for (int j = -4; j < 4; ++j) {
                final IsoGridSquare gridSquare = currentSquare.getCell().getGridSquare(currentSquare.x + i, currentSquare.y + j, currentSquare.z);
                if (gridSquare != null) {
                    if (gridSquare.haveFire()) {
                        return true;
                    }
                    for (int k = 0; k < gridSquare.getObjects().size(); ++k) {
                        final IsoFireplace isoFireplace = Type.tryCastTo(gridSquare.getObjects().get(k), IsoFireplace.class);
                        if (isoFireplace != null && isoFireplace.isLit()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final IsoPlayer isoPlayer = (IsoPlayer)isoGameCharacter;
        if (isoPlayer.pressedMovement(false)) {
            isoGameCharacter.StopAllActionQueue();
            isoGameCharacter.setVariable("forceGetUp", true);
        }
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > stateMachineParams.get(PlayerSitOnGroundState.PARAM_CHECK_FIRE) + 5000L) {
            stateMachineParams.put(PlayerSitOnGroundState.PARAM_FIRE, this.checkFire(isoGameCharacter));
            stateMachineParams.put(PlayerSitOnGroundState.PARAM_CHECK_FIRE, currentTimeMillis);
        }
        if (isoGameCharacter.hasTimedActions()) {
            stateMachineParams.put(PlayerSitOnGroundState.PARAM_FIRE, false);
            isoGameCharacter.setVariable("SitGroundAnim", "Idle");
        }
        if (stateMachineParams.get(PlayerSitOnGroundState.PARAM_FIRE)) {
            if (currentTimeMillis > stateMachineParams.get(PlayerSitOnGroundState.PARAM_CHANGE_ANIM)) {
                if ("Idle".equals(isoGameCharacter.getVariableString("SitGroundAnim"))) {
                    isoGameCharacter.setVariable("SitGroundAnim", "WarmHands");
                }
                else if ("WarmHands".equals(isoGameCharacter.getVariableString("SitGroundAnim"))) {
                    isoGameCharacter.setVariable("SitGroundAnim", "Idle");
                }
                stateMachineParams.put(PlayerSitOnGroundState.PARAM_CHANGE_ANIM, currentTimeMillis + Rand.Next(30000, 90000));
            }
        }
        else if (isoGameCharacter.getVariableBoolean("SitGroundStarted")) {
            isoGameCharacter.clearVariable("FireNear");
            isoGameCharacter.setVariable("SitGroundAnim", "Idle");
        }
        if ("WarmHands".equals(isoGameCharacter.getVariableString("SitGroundAnim")) && Rand.Next(Rand.AdjustForFramerate(2500)) == 0) {
            stateMachineParams.put(PlayerSitOnGroundState.PARAM_SITGROUNDANIM, isoGameCharacter.getVariableString("SitGroundAnim"));
            isoGameCharacter.setVariable("SitGroundAnim", "rubhands");
        }
        isoPlayer.setInitiateAttack(false);
        isoPlayer.attackStarted = false;
        isoPlayer.setAttackType(null);
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setHideWeaponModel(false);
        if (StringUtils.isNullOrEmpty(isoGameCharacter.getVariableString("HitReaction"))) {
            isoGameCharacter.clearVariable("SitGroundStarted");
            isoGameCharacter.clearVariable("forceGetUp");
            isoGameCharacter.clearVariable("SitGroundAnim");
            isoGameCharacter.setIgnoreMovement(false);
        }
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if (animEvent.m_EventName.equalsIgnoreCase("SitGroundStarted")) {
            isoGameCharacter.setVariable("SitGroundStarted", true);
            if (isoGameCharacter.getStateMachineParams(this).get(PlayerSitOnGroundState.PARAM_FIRE)) {
                isoGameCharacter.setVariable("SitGroundAnim", "WarmHands");
            }
            else {
                isoGameCharacter.setVariable("SitGroundAnim", "Idle");
            }
        }
        if (animEvent.m_EventName.equalsIgnoreCase("ResetSitOnGroundAnim")) {
            isoGameCharacter.setVariable("SitGroundAnim", isoGameCharacter.getStateMachineParams(this).get(PlayerSitOnGroundState.PARAM_SITGROUNDANIM));
        }
    }
    
    static {
        _instance = new PlayerSitOnGroundState();
        PARAM_FIRE = 0;
        PARAM_SITGROUNDANIM = 1;
        PARAM_CHECK_FIRE = 2;
        PARAM_CHANGE_ANIM = 3;
    }
}
