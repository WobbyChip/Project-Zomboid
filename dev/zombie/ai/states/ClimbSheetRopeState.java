// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.characters.skills.PerkFactory;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.IsoGridSquare;
import zombie.characters.IsoPlayer;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoWindow;
import zombie.iso.IsoWorld;
import zombie.GameTime;
import zombie.iso.IsoDirections;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ClimbSheetRopeState extends State
{
    public static final float CLIMB_SPEED = 0.16f;
    private static final float CLIMB_SLOWDOWN = 0.5f;
    private static final ClimbSheetRopeState _instance;
    
    public static ClimbSheetRopeState instance() {
        return ClimbSheetRopeState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(true);
        isoGameCharacter.setbClimbing(true);
        isoGameCharacter.setVariable("ClimbRope", true);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.getStateMachineParams(this);
        float n = 0.0f;
        float n2 = 0.0f;
        if (isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetN) || isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopN)) {
            isoGameCharacter.setDir(IsoDirections.N);
            n = 0.54f;
            n2 = 0.39f;
        }
        if (isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetS) || isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopS)) {
            isoGameCharacter.setDir(IsoDirections.S);
            n = 0.118f;
            n2 = 0.5756f;
        }
        if (isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetW) || isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopW)) {
            isoGameCharacter.setDir(IsoDirections.W);
            n = 0.4f;
            n2 = 0.7f;
        }
        if (isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetE) || isoGameCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopE)) {
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
        final float climbSheetRopeSpeed = this.getClimbSheetRopeSpeed(isoGameCharacter);
        isoGameCharacter.getSpriteDef().AnimFrameIncrease = climbSheetRopeSpeed;
        final float min = Math.min(isoGameCharacter.z + climbSheetRopeSpeed / 10.0f * GameTime.instance.getMultiplier(), 7.0f);
        for (int n5 = (int)isoGameCharacter.z; n5 <= min; ++n5) {
            final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare(isoGameCharacter.getX(), isoGameCharacter.getY(), n5);
            if (IsoWindow.isTopOfSheetRopeHere(gridSquare)) {
                isoGameCharacter.z = (float)n5;
                isoGameCharacter.setCurrent(gridSquare);
                isoGameCharacter.setCollidable(true);
                final IsoGridSquare isoGridSquare = gridSquare.nav[isoGameCharacter.dir.index()];
                if (isoGridSquare != null) {
                    if (!isoGridSquare.TreatAsSolidFloor()) {
                        isoGameCharacter.climbDownSheetRope();
                        return;
                    }
                    final IsoWindow windowTo = gridSquare.getWindowTo(isoGridSquare);
                    if (windowTo != null) {
                        if (!windowTo.open) {
                            windowTo.ToggleWindow(isoGameCharacter);
                        }
                        if (!windowTo.canClimbThrough(isoGameCharacter)) {
                            isoGameCharacter.climbDownSheetRope();
                            return;
                        }
                        isoGameCharacter.climbThroughWindow(windowTo, 4);
                    }
                    else {
                        final IsoThumpable windowThumpableTo = gridSquare.getWindowThumpableTo(isoGridSquare);
                        if (windowThumpableTo != null) {
                            if (!windowThumpableTo.canClimbThrough(isoGameCharacter)) {
                                isoGameCharacter.climbDownSheetRope();
                                return;
                            }
                            isoGameCharacter.climbThroughWindow(windowThumpableTo, 4);
                        }
                        else if (gridSquare.getHoppableThumpableTo(isoGridSquare) != null) {
                            if (!IsoWindow.canClimbThroughHelper(isoGameCharacter, gridSquare, isoGridSquare, isoGameCharacter.dir == IsoDirections.N || isoGameCharacter.dir == IsoDirections.S)) {
                                isoGameCharacter.climbDownSheetRope();
                                return;
                            }
                            isoGameCharacter.climbOverFence(isoGameCharacter.dir);
                        }
                        else {
                            final IsoObject windowFrameTo = gridSquare.getWindowFrameTo(isoGridSquare);
                            if (windowFrameTo != null) {
                                if (!IsoWindowFrame.canClimbThrough(windowFrameTo, isoGameCharacter)) {
                                    isoGameCharacter.climbDownSheetRope();
                                    return;
                                }
                                isoGameCharacter.climbThroughWindowFrame(windowFrameTo);
                            }
                            else if (gridSquare.getWallHoppableTo(isoGridSquare) != null) {
                                if (!IsoWindow.canClimbThroughHelper(isoGameCharacter, gridSquare, isoGridSquare, isoGameCharacter.dir == IsoDirections.N || isoGameCharacter.dir == IsoDirections.S)) {
                                    isoGameCharacter.climbDownSheetRope();
                                    return;
                                }
                                isoGameCharacter.climbOverFence(isoGameCharacter.dir);
                            }
                        }
                    }
                }
                return;
            }
        }
        isoGameCharacter.z = min;
        if (isoGameCharacter.z >= 7.0f) {
            isoGameCharacter.setCollidable(true);
            isoGameCharacter.clearVariable("ClimbRope");
        }
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
        isoGameCharacter.setbClimbing(false);
        isoGameCharacter.clearVariable("ClimbRope");
    }
    
    public float getClimbSheetRopeSpeed(final IsoGameCharacter isoGameCharacter) {
        float n = 0.16f;
        switch (isoGameCharacter.getPerkLevel(PerkFactory.Perks.Strength)) {
            case 1: {
                n -= 0.1f;
                break;
            }
            case 2: {
                n -= 0.1f;
                break;
            }
            case 6: {
                n += 0.05f;
                break;
            }
            case 7: {
                n += 0.05f;
                break;
            }
            case 8: {
                n += 0.1f;
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
        _instance = new ClimbSheetRopeState();
    }
}
