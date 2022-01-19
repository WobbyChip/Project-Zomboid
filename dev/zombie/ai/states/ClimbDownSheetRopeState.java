// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.characters.skills.PerkFactory;
import zombie.iso.IsoGridSquare;
import java.util.HashMap;
import zombie.characters.IsoPlayer;
import zombie.iso.objects.IsoWindow;
import zombie.iso.IsoWorld;
import zombie.GameTime;
import zombie.iso.IsoDirections;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ClimbDownSheetRopeState extends State
{
    public static final float CLIMB_DOWN_SPEED = 0.16f;
    private static final float CLIMB_DOWN_SLOWDOWN = 0.5f;
    private static final ClimbDownSheetRopeState _instance;
    
    public static ClimbDownSheetRopeState instance() {
        return ClimbDownSheetRopeState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(true);
        isoGameCharacter.setHideWeaponModel(true);
        isoGameCharacter.setbClimbing(true);
        isoGameCharacter.setVariable("ClimbRope", true);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        float n = 0.0f;
        float n2 = 0.0f;
        if (isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopN) || isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetN)) {
            isoGameCharacter.setDir(IsoDirections.N);
            n = 0.54f;
            n2 = 0.39f;
        }
        if (isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopS) || isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetS)) {
            isoGameCharacter.setDir(IsoDirections.S);
            n = 0.118f;
            n2 = 0.5756f;
        }
        if (isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopW) || isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetW)) {
            isoGameCharacter.setDir(IsoDirections.W);
            n = 0.4f;
            n2 = 0.7f;
        }
        if (isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopE) || isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetE)) {
            isoGameCharacter.setDir(IsoDirections.E);
            n = 0.5417f;
            n2 = 0.3144f;
        }
        final float n3 = isoGameCharacter.x - (int)isoGameCharacter.x;
        final float n4 = isoGameCharacter.y - (int)isoGameCharacter.y;
        if (n3 != n) {
            isoGameCharacter.x = (int)isoGameCharacter.x + (n3 + (n - n3) / 4.0f);
        }
        if (n4 != n2) {
            isoGameCharacter.y = (int)isoGameCharacter.y + (n4 + (n2 - n4) / 4.0f);
        }
        isoGameCharacter.nx = isoGameCharacter.x;
        isoGameCharacter.ny = isoGameCharacter.y;
        final float climbDownSheetRopeSpeed = this.getClimbDownSheetRopeSpeed(isoGameCharacter);
        isoGameCharacter.getSpriteDef().AnimFrameIncrease = climbDownSheetRopeSpeed;
        final float max = Math.max(isoGameCharacter.z - climbDownSheetRopeSpeed / 10.0f * GameTime.instance.getMultiplier(), 0.0f);
        for (int i = (int)isoGameCharacter.z; i >= (int)max; --i) {
            final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare(isoGameCharacter.getX(), isoGameCharacter.getY(), i);
            if ((gridSquare.Is(IsoFlagType.solidtrans) || gridSquare.TreatAsSolidFloor() || i == 0) && max <= i) {
                isoGameCharacter.z = (float)i;
                stateMachineParams.clear();
                isoGameCharacter.clearVariable("ClimbRope");
                isoGameCharacter.setCollidable(true);
                isoGameCharacter.setbClimbing(false);
                return;
            }
        }
        isoGameCharacter.z = max;
        if (!IsoWindow.isSheetRopeHere(isoGameCharacter.getCurrentSquare())) {
            isoGameCharacter.setCollidable(true);
            isoGameCharacter.setbClimbing(false);
            isoGameCharacter.setbFalling(true);
            isoGameCharacter.clearVariable("ClimbRope");
        }
        if (isoGameCharacter instanceof IsoPlayer && ((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
            ((IsoPlayer)isoGameCharacter).dirtyRecalcGridStackTime = 2.0f;
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(false);
        isoGameCharacter.setHideWeaponModel(false);
        isoGameCharacter.clearVariable("ClimbRope");
        isoGameCharacter.setbClimbing(false);
    }
    
    public float getClimbDownSheetRopeSpeed(final IsoGameCharacter isoGameCharacter) {
        float n = 0.16f;
        switch (isoGameCharacter.getPerkLevel(PerkFactory.Perks.Strength)) {
            case 0: {
                n -= 0.1f;
                break;
            }
            case 1: {
                n -= 0.02f;
                break;
            }
            case 2: {
                n -= 0.03f;
                break;
            }
            case 3: {
                n -= 0.05f;
                break;
            }
            case 6: {
                n += 0.05f;
                break;
            }
            case 7: {
                n += 0.07f;
                break;
            }
            case 8: {
                n += 0.09f;
                break;
            }
            case 9: {
                n += 0.1f;
                break;
            }
        }
        return n * 0.5f;
    }
    
    static {
        _instance = new ClimbDownSheetRopeState();
    }
}
