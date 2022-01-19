// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.characters.MoveDeltaModifiers;
import zombie.iso.objects.IsoWindow;
import zombie.iso.IsoObject;
import zombie.core.math.PZMath;
import zombie.iso.objects.IsoThumpable;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.GameTime;
import zombie.core.Rand;
import zombie.characters.skills.PerkFactory;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoGridSquare;
import zombie.characterTextures.BloodBodyPartType;
import zombie.iso.IsoWorld;
import zombie.iso.IsoDirections;
import java.util.HashMap;
import zombie.characters.IsoPlayer;
import zombie.util.Type;
import zombie.characters.IsoZombie;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ClimbThroughWindowState extends State
{
    private static final ClimbThroughWindowState _instance;
    static final Integer PARAM_START_X;
    static final Integer PARAM_START_Y;
    static final Integer PARAM_Z;
    static final Integer PARAM_OPPOSITE_X;
    static final Integer PARAM_OPPOSITE_Y;
    static final Integer PARAM_DIR;
    static final Integer PARAM_ZOMBIE_ON_FLOOR;
    static final Integer PARAM_PREV_STATE;
    static final Integer PARAM_SCRATCH;
    static final Integer PARAM_COUNTER;
    static final Integer PARAM_SOLID_FLOOR;
    static final Integer PARAM_SHEET_ROPE;
    static final Integer PARAM_END_X;
    static final Integer PARAM_END_Y;
    
    public static ClimbThroughWindowState instance() {
        return ClimbThroughWindowState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(true);
        isoGameCharacter.setHideWeaponModel(true);
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final boolean b = stateMachineParams.get(ClimbThroughWindowState.PARAM_COUNTER) == Boolean.TRUE;
        isoGameCharacter.setVariable("ClimbWindowStarted", false);
        isoGameCharacter.setVariable("ClimbWindowEnd", false);
        isoGameCharacter.setVariable("ClimbWindowFinished", false);
        isoGameCharacter.clearVariable("ClimbWindowGetUpBack");
        isoGameCharacter.clearVariable("ClimbWindowGetUpFront");
        isoGameCharacter.setVariable("ClimbWindowOutcome", b ? "obstacle" : "success");
        isoGameCharacter.clearVariable("ClimbWindowFlopped");
        final IsoZombie lungeXVars = Type.tryCastTo(isoGameCharacter, IsoZombie.class);
        if (!b && lungeXVars != null && lungeXVars.shouldDoFenceLunge()) {
            this.setLungeXVars(lungeXVars);
            isoGameCharacter.setVariable("ClimbWindowOutcome", "lunge");
        }
        if (stateMachineParams.get(ClimbThroughWindowState.PARAM_SOLID_FLOOR) == Boolean.FALSE) {
            isoGameCharacter.setVariable("ClimbWindowOutcome", "fall");
        }
        if (!(isoGameCharacter instanceof IsoZombie) && stateMachineParams.get(ClimbThroughWindowState.PARAM_SHEET_ROPE) == Boolean.TRUE) {
            isoGameCharacter.setVariable("ClimbWindowOutcome", "rope");
        }
        if (isoGameCharacter instanceof IsoPlayer && ((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
            ((IsoPlayer)isoGameCharacter).dirtyRecalcGridStackTime = 20.0f;
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if (this.isWindowClosing(isoGameCharacter)) {
            return;
        }
        final IsoDirections dir = stateMachineParams.get(ClimbThroughWindowState.PARAM_DIR);
        isoGameCharacter.setDir(dir);
        final String variableString = isoGameCharacter.getVariableString("ClimbWindowOutcome");
        if (isoGameCharacter instanceof IsoZombie) {
            final boolean fallOnFront = stateMachineParams.get(ClimbThroughWindowState.PARAM_ZOMBIE_ON_FLOOR) == Boolean.TRUE;
            if (!isoGameCharacter.isFallOnFront() && fallOnFront) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(stateMachineParams.get(ClimbThroughWindowState.PARAM_OPPOSITE_X), stateMachineParams.get(ClimbThroughWindowState.PARAM_OPPOSITE_Y), stateMachineParams.get(ClimbThroughWindowState.PARAM_Z));
                if (gridSquare != null && gridSquare.getBrokenGlass() != null) {
                    isoGameCharacter.addBlood(BloodBodyPartType.Head, true, true, true);
                    isoGameCharacter.addBlood(BloodBodyPartType.Head, true, true, true);
                    isoGameCharacter.addBlood(BloodBodyPartType.Head, true, true, true);
                    isoGameCharacter.addBlood(BloodBodyPartType.Head, true, true, true);
                    isoGameCharacter.addBlood(BloodBodyPartType.Head, true, true, true);
                    isoGameCharacter.addBlood(BloodBodyPartType.Neck, true, true, true);
                    isoGameCharacter.addBlood(BloodBodyPartType.Neck, true, true, true);
                    isoGameCharacter.addBlood(BloodBodyPartType.Neck, true, true, true);
                    isoGameCharacter.addBlood(BloodBodyPartType.Neck, true, true, true);
                    isoGameCharacter.addBlood(BloodBodyPartType.Torso_Upper, true, true, true);
                    isoGameCharacter.addBlood(BloodBodyPartType.Torso_Upper, true, true, true);
                    isoGameCharacter.addBlood(BloodBodyPartType.Torso_Upper, true, true, true);
                }
            }
            isoGameCharacter.setOnFloor(fallOnFront);
            ((IsoZombie)isoGameCharacter).setKnockedDown(fallOnFront);
            isoGameCharacter.setFallOnFront(fallOnFront);
        }
        final float n = stateMachineParams.get(ClimbThroughWindowState.PARAM_START_X) + 0.5f;
        final float n2 = stateMachineParams.get(ClimbThroughWindowState.PARAM_START_Y) + 0.5f;
        if (!isoGameCharacter.getVariableBoolean("ClimbWindowStarted")) {
            if (isoGameCharacter.x != n && (dir == IsoDirections.N || dir == IsoDirections.S)) {
                this.slideX(isoGameCharacter, n);
            }
            if (isoGameCharacter.y != n2 && (dir == IsoDirections.W || dir == IsoDirections.E)) {
                this.slideY(isoGameCharacter, n2);
            }
        }
        if (isoGameCharacter instanceof IsoPlayer && variableString.equalsIgnoreCase("obstacle") && isoGameCharacter.DistToSquared(stateMachineParams.get(ClimbThroughWindowState.PARAM_END_X) + 0.5f, stateMachineParams.get(ClimbThroughWindowState.PARAM_END_Y) + 0.5f) < 0.5625f) {
            isoGameCharacter.setVariable("ClimbWindowOutcome", "obstacleEnd");
        }
        if (isoGameCharacter instanceof IsoPlayer && !isoGameCharacter.getVariableBoolean("ClimbWindowEnd") && !"fallfront".equals(variableString) && !"back".equals(variableString) && !"fallback".equals(variableString)) {
            final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(stateMachineParams.get(ClimbThroughWindowState.PARAM_OPPOSITE_X), stateMachineParams.get(ClimbThroughWindowState.PARAM_OPPOSITE_Y), stateMachineParams.get(ClimbThroughWindowState.PARAM_Z));
            if (gridSquare2 != null) {
                this.checkForFallingBack(gridSquare2, isoGameCharacter);
                if (gridSquare2 != isoGameCharacter.getSquare() && gridSquare2.TreatAsSolidFloor()) {
                    this.checkForFallingFront(isoGameCharacter.getSquare(), isoGameCharacter);
                }
            }
        }
        if (isoGameCharacter.getVariableBoolean("ClimbWindowStarted") && !"back".equals(variableString) && !"fallback".equals(variableString) && !"lunge".equals(variableString) && !"obstacle".equals(variableString) && !"obstacleEnd".equals(variableString)) {
            float n3 = stateMachineParams.get(ClimbThroughWindowState.PARAM_START_X);
            float n4 = stateMachineParams.get(ClimbThroughWindowState.PARAM_START_Y);
            switch (dir) {
                case N: {
                    n4 -= 0.1f;
                    break;
                }
                case S: {
                    n4 += 1.1f;
                    break;
                }
                case W: {
                    n3 -= 0.1f;
                    break;
                }
                case E: {
                    n3 += 1.1f;
                    break;
                }
            }
            if ((int)isoGameCharacter.x != (int)n3 && (dir == IsoDirections.W || dir == IsoDirections.E)) {
                this.slideX(isoGameCharacter, n3);
            }
            if ((int)isoGameCharacter.y != (int)n4 && (dir == IsoDirections.N || dir == IsoDirections.S)) {
                this.slideY(isoGameCharacter, n4);
            }
        }
        if (isoGameCharacter.getVariableBoolean("ClimbWindowStarted") && stateMachineParams.get(ClimbThroughWindowState.PARAM_SCRATCH) == Boolean.TRUE) {
            stateMachineParams.put(ClimbThroughWindowState.PARAM_SCRATCH, Boolean.FALSE);
            isoGameCharacter.getBodyDamage().setScratchedWindow();
        }
    }
    
    private void checkForFallingBack(final IsoGridSquare isoGridSquare, final IsoGameCharacter isoGameCharacter) {
        for (int i = 0; i < isoGridSquare.getMovingObjects().size(); ++i) {
            final IsoZombie isoZombie = Type.tryCastTo(isoGridSquare.getMovingObjects().get(i), IsoZombie.class);
            if (isoZombie != null && !isoZombie.isOnFloor()) {
                if (!isoZombie.isSitAgainstWall()) {
                    if (isoZombie.isVariable("AttackOutcome", "success") || Rand.Next(5 + isoGameCharacter.getPerkLevel(PerkFactory.Perks.Fitness)) == 0) {
                        isoZombie.playHurtSound();
                        isoGameCharacter.setVariable("ClimbWindowOutcome", "fallback");
                    }
                    else {
                        isoZombie.playHurtSound();
                        isoGameCharacter.setVariable("ClimbWindowOutcome", "back");
                    }
                }
            }
        }
    }
    
    private void checkForFallingFront(final IsoGridSquare isoGridSquare, final IsoGameCharacter isoGameCharacter) {
        for (int i = 0; i < isoGridSquare.getMovingObjects().size(); ++i) {
            final IsoZombie isoZombie = Type.tryCastTo(isoGridSquare.getMovingObjects().get(i), IsoZombie.class);
            if (isoZombie != null && !isoZombie.isOnFloor()) {
                if (!isoZombie.isSitAgainstWall()) {
                    if (isoZombie.isVariable("AttackOutcome", "success")) {
                        isoZombie.playHurtSound();
                        isoGameCharacter.setVariable("ClimbWindowOutcome", "fallfront");
                    }
                }
            }
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(false);
        isoGameCharacter.setHideWeaponModel(false);
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if (isoGameCharacter.isVariable("ClimbWindowOutcome", "fall") || isoGameCharacter.isVariable("ClimbWindowOutcome", "fallback") || isoGameCharacter.isVariable("ClimbWindowOutcome", "fallfront")) {
            isoGameCharacter.setHitReaction("");
        }
        isoGameCharacter.clearVariable("ClimbWindowFinished");
        isoGameCharacter.clearVariable("ClimbWindowOutcome");
        isoGameCharacter.clearVariable("ClimbWindowStarted");
        isoGameCharacter.clearVariable("ClimbWindowFlopped");
        if (isoGameCharacter instanceof IsoZombie) {
            isoGameCharacter.setOnFloor(false);
            ((IsoZombie)isoGameCharacter).setKnockedDown(false);
        }
        final IsoZombie isoZombie = Type.tryCastTo(isoGameCharacter, IsoZombie.class);
        if (isoZombie != null) {
            isoZombie.AllowRepathDelay = 0.0f;
            if (stateMachineParams.get(ClimbThroughWindowState.PARAM_PREV_STATE) == PathFindState.instance()) {
                if (isoGameCharacter.getPathFindBehavior2().getTargetChar() == null) {
                    isoGameCharacter.setVariable("bPathFind", true);
                    isoGameCharacter.setVariable("bMoving", false);
                }
                else if (isoZombie.isTargetLocationKnown()) {
                    isoGameCharacter.pathToCharacter(isoGameCharacter.getPathFindBehavior2().getTargetChar());
                }
                else if (isoZombie.LastTargetSeenX != -1) {
                    isoGameCharacter.pathToLocation(isoZombie.LastTargetSeenX, isoZombie.LastTargetSeenY, isoZombie.LastTargetSeenZ);
                }
            }
            else if (stateMachineParams.get(ClimbThroughWindowState.PARAM_PREV_STATE) == WalkTowardState.instance() || stateMachineParams.get(ClimbThroughWindowState.PARAM_PREV_STATE) == WalkTowardNetworkState.instance()) {
                isoGameCharacter.setVariable("bPathFind", false);
                isoGameCharacter.setVariable("bMoving", true);
            }
        }
        if (isoGameCharacter instanceof IsoZombie) {
            ((IsoZombie)isoGameCharacter).networkAI.isClimbing = false;
        }
    }
    
    public void slideX(final IsoGameCharacter isoGameCharacter, final float n) {
        final float a = 0.05f * GameTime.getInstance().getMultiplier() / 1.6f;
        isoGameCharacter.x += ((n > isoGameCharacter.x) ? Math.min(a, n - isoGameCharacter.x) : Math.max(-a, n - isoGameCharacter.x));
        isoGameCharacter.nx = isoGameCharacter.x;
    }
    
    public void slideY(final IsoGameCharacter isoGameCharacter, final float n) {
        final float a = 0.05f * GameTime.getInstance().getMultiplier() / 1.6f;
        isoGameCharacter.y += ((n > isoGameCharacter.y) ? Math.min(a, n - isoGameCharacter.y) : Math.max(-a, n - isoGameCharacter.y));
        isoGameCharacter.ny = isoGameCharacter.y;
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final IsoZombie lungeXVars = Type.tryCastTo(isoGameCharacter, IsoZombie.class);
        if (animEvent.m_EventName.equalsIgnoreCase("CheckAttack") && lungeXVars != null && lungeXVars.target instanceof IsoGameCharacter) {
            ((IsoGameCharacter)lungeXVars.target).attackFromWindowsLunge(lungeXVars);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("OnFloor") && lungeXVars != null) {
            final boolean boolean1 = Boolean.parseBoolean(animEvent.m_ParameterValue);
            stateMachineParams.put(ClimbThroughWindowState.PARAM_ZOMBIE_ON_FLOOR, boolean1);
            if (boolean1) {
                this.setLungeXVars(lungeXVars);
                final IsoThumpable isoThumpable = Type.tryCastTo(this.getWindow(isoGameCharacter), IsoThumpable.class);
                if (isoThumpable != null && isoThumpable.getSquare() != null && lungeXVars.target != null) {
                    final IsoThumpable isoThumpable2 = isoThumpable;
                    isoThumpable2.Health -= Rand.Next(10, 20);
                    if (isoThumpable.Health <= 0) {
                        isoThumpable.destroy();
                    }
                }
                isoGameCharacter.setVariable("ClimbWindowFlopped", true);
            }
        }
    }
    
    @Override
    public boolean isIgnoreCollide(final IsoGameCharacter isoGameCharacter, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final int intValue = stateMachineParams.get(ClimbThroughWindowState.PARAM_START_X);
        final int intValue2 = stateMachineParams.get(ClimbThroughWindowState.PARAM_START_Y);
        final int intValue3 = stateMachineParams.get(ClimbThroughWindowState.PARAM_END_X);
        final int intValue4 = stateMachineParams.get(ClimbThroughWindowState.PARAM_END_Y);
        final int intValue5 = stateMachineParams.get(ClimbThroughWindowState.PARAM_Z);
        if (intValue5 != n3 || intValue5 != n6) {
            return false;
        }
        final int min = PZMath.min(intValue, intValue3);
        final int min2 = PZMath.min(intValue2, intValue4);
        final int max = PZMath.max(intValue, intValue3);
        final int max2 = PZMath.max(intValue2, intValue4);
        final int min3 = PZMath.min(n, n4);
        final int min4 = PZMath.min(n2, n5);
        final int max3 = PZMath.max(n, n4);
        final int max4 = PZMath.max(n2, n5);
        return min <= min3 && min2 <= min4 && max >= max3 && max2 >= max4;
    }
    
    public IsoObject getWindow(final IsoGameCharacter isoGameCharacter) {
        if (!isoGameCharacter.isCurrentState(this)) {
            return null;
        }
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final int intValue = stateMachineParams.get(ClimbThroughWindowState.PARAM_START_X);
        final int intValue2 = stateMachineParams.get(ClimbThroughWindowState.PARAM_START_Y);
        final int intValue3 = stateMachineParams.get(ClimbThroughWindowState.PARAM_Z);
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(intValue, intValue2, intValue3);
        final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(stateMachineParams.get(ClimbThroughWindowState.PARAM_END_X), stateMachineParams.get(ClimbThroughWindowState.PARAM_END_Y), intValue3);
        if (gridSquare == null || gridSquare2 == null) {
            return null;
        }
        Object o = gridSquare.getWindowTo(gridSquare2);
        if (o == null) {
            o = gridSquare.getWindowThumpableTo(gridSquare2);
        }
        if (o == null) {
            o = gridSquare.getHoppableTo(gridSquare2);
        }
        return (IsoObject)o;
    }
    
    public boolean isWindowClosing(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if (isoGameCharacter.getVariableBoolean("ClimbWindowStarted")) {
            return false;
        }
        if (isoGameCharacter.getCurrentSquare() != IsoWorld.instance.CurrentCell.getGridSquare((int)stateMachineParams.get(ClimbThroughWindowState.PARAM_START_X), (int)stateMachineParams.get(ClimbThroughWindowState.PARAM_START_Y), (int)stateMachineParams.get(ClimbThroughWindowState.PARAM_Z))) {
            return false;
        }
        final IsoWindow isoWindow = Type.tryCastTo(this.getWindow(isoGameCharacter), IsoWindow.class);
        if (isoWindow == null) {
            return false;
        }
        final IsoGameCharacter firstCharacterClosing = isoWindow.getFirstCharacterClosing();
        if (firstCharacterClosing == null || !firstCharacterClosing.isVariable("CloseWindowOutcome", "success")) {
            return false;
        }
        if (isoGameCharacter.isZombie()) {
            isoGameCharacter.setHitReaction("HeadLeft");
        }
        else {
            isoGameCharacter.setVariable("ClimbWindowFinished", true);
        }
        return true;
    }
    
    @Override
    public void getDeltaModifiers(final IsoGameCharacter isoGameCharacter, final MoveDeltaModifiers moveDeltaModifiers) {
        final boolean b = isoGameCharacter.getPath2() != null;
        final boolean b2 = isoGameCharacter instanceof IsoPlayer;
        if (b && b2) {
            moveDeltaModifiers.turnDelta = Math.max(moveDeltaModifiers.turnDelta, 10.0f);
        }
        if (b2 && isoGameCharacter.getVariableBoolean("isTurning")) {
            moveDeltaModifiers.turnDelta = Math.max(moveDeltaModifiers.turnDelta, 5.0f);
        }
    }
    
    private boolean isFreeSquare(final IsoGridSquare isoGridSquare) {
        return isoGridSquare != null && isoGridSquare.TreatAsSolidFloor() && !isoGridSquare.Is(IsoFlagType.solid) && !isoGridSquare.Is(IsoFlagType.solidtrans);
    }
    
    private boolean isObstacleSquare(final IsoGridSquare isoGridSquare) {
        return isoGridSquare != null && isoGridSquare.TreatAsSolidFloor() && !isoGridSquare.Is(IsoFlagType.solid) && isoGridSquare.Is(IsoFlagType.solidtrans) && !isoGridSquare.Is(IsoFlagType.water);
    }
    
    private IsoGridSquare getFreeSquareAfterObstacles(IsoGridSquare isoGridSquare, final IsoDirections isoDirections) {
        while (true) {
            final IsoGridSquare adjacentSquare = isoGridSquare.getAdjacentSquare(isoDirections);
            if (adjacentSquare == null || isoGridSquare.isSomethingTo(adjacentSquare) || isoGridSquare.getWindowFrameTo(adjacentSquare) != null || isoGridSquare.getWindowThumpableTo(adjacentSquare) != null) {
                return null;
            }
            if (this.isFreeSquare(adjacentSquare)) {
                return adjacentSquare;
            }
            if (!this.isObstacleSquare(adjacentSquare)) {
                return null;
            }
            isoGridSquare = adjacentSquare;
        }
    }
    
    private void setLungeXVars(final IsoZombie isoZombie) {
        final IsoMovingObject target = isoZombie.getTarget();
        if (target == null) {
            return;
        }
        isoZombie.setVariable("FenceLungeX", 0.0f);
        isoZombie.setVariable("FenceLungeY", 0.0f);
        float n = 0.0f;
        final Vector2 forwardDirection = isoZombie.getForwardDirection();
        final PZMath.SideOfLine testSideOfLine = PZMath.testSideOfLine(isoZombie.x, isoZombie.y, isoZombie.x + forwardDirection.x, isoZombie.y + forwardDirection.y, target.x, target.y);
        final float clamp = PZMath.clamp(PZMath.radToDeg((float)Math.acos(isoZombie.getDotWithForwardDirection(target.x, target.y))), 0.0f, 90.0f);
        switch (testSideOfLine) {
            case Left: {
                n = -clamp / 90.0f;
                break;
            }
            case OnLine: {
                n = 0.0f;
                break;
            }
            case Right: {
                n = clamp / 90.0f;
                break;
            }
        }
        isoZombie.setVariable("FenceLungeX", n);
    }
    
    public boolean isPastInnerEdgeOfSquare(final IsoGameCharacter isoGameCharacter, final int n, final int n2, final IsoDirections isoDirections) {
        if (isoDirections == IsoDirections.N) {
            return isoGameCharacter.y < n2 + 1 - 0.3f;
        }
        if (isoDirections == IsoDirections.S) {
            return isoGameCharacter.y > n2 + 0.3f;
        }
        if (isoDirections == IsoDirections.W) {
            return isoGameCharacter.x < n + 1 - 0.3f;
        }
        if (isoDirections == IsoDirections.E) {
            return isoGameCharacter.x > n + 0.3f;
        }
        throw new IllegalArgumentException("unhandled direction");
    }
    
    public boolean isPastOuterEdgeOfSquare(final IsoGameCharacter isoGameCharacter, final int n, final int n2, final IsoDirections isoDirections) {
        if (isoDirections == IsoDirections.N) {
            return isoGameCharacter.y < n2 - 0.3f;
        }
        if (isoDirections == IsoDirections.S) {
            return isoGameCharacter.y > n2 + 1 + 0.3f;
        }
        if (isoDirections == IsoDirections.W) {
            return isoGameCharacter.x < n - 0.3f;
        }
        if (isoDirections == IsoDirections.E) {
            return isoGameCharacter.x > n + 1 + 0.3f;
        }
        throw new IllegalArgumentException("unhandled direction");
    }
    
    public void setParams(final IsoGameCharacter isoGameCharacter, final IsoObject isoObject) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        stateMachineParams.clear();
        boolean b = false;
        boolean b2;
        if (isoObject instanceof IsoWindow) {
            final IsoWindow isoWindow = (IsoWindow)isoObject;
            b2 = isoWindow.north;
            if (isoGameCharacter instanceof IsoPlayer && isoWindow.isDestroyed() && !isoWindow.isGlassRemoved() && Rand.Next(2) == 0) {
                b = true;
            }
        }
        else if (isoObject instanceof IsoThumpable) {
            final IsoThumpable isoThumpable = (IsoThumpable)isoObject;
            b2 = isoThumpable.north;
            if (isoGameCharacter instanceof IsoPlayer && isoThumpable.getName().equals("Barbed Fence") && Rand.Next(101) > 75) {
                b = true;
            }
        }
        else {
            if (!IsoWindowFrame.isWindowFrame(isoObject)) {
                throw new IllegalArgumentException("expected thumpable, window, or window-frame");
            }
            b2 = IsoWindowFrame.isWindowFrame(isoObject, true);
        }
        final int x = isoObject.getSquare().getX();
        final int y = isoObject.getSquare().getY();
        final int z = isoObject.getSquare().getZ();
        int i = x;
        int j = y;
        int k = x;
        int l = y;
        IsoDirections value;
        if (b2) {
            if (y < isoGameCharacter.getY()) {
                --l;
                value = IsoDirections.N;
            }
            else {
                --j;
                value = IsoDirections.S;
            }
        }
        else if (x < isoGameCharacter.getX()) {
            --k;
            value = IsoDirections.W;
        }
        else {
            --i;
            value = IsoDirections.E;
        }
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(k, l, z);
        int n = (gridSquare != null && gridSquare.Is(IsoFlagType.solidtrans)) ? 1 : 0;
        final boolean b3 = gridSquare != null && gridSquare.TreatAsSolidFloor();
        final boolean b4 = gridSquare != null && isoGameCharacter.canClimbDownSheetRope(gridSquare);
        int m = k;
        int i2 = l;
        if (n != 0 && isoGameCharacter.isZombie()) {
            final IsoGridSquare adjacentSquare = gridSquare.getAdjacentSquare(value);
            if (!this.isFreeSquare(adjacentSquare) || gridSquare.isSomethingTo(adjacentSquare) || gridSquare.getWindowFrameTo(adjacentSquare) != null || gridSquare.getWindowThumpableTo(adjacentSquare) != null) {
                n = 0;
            }
            else {
                m = adjacentSquare.x;
                i2 = adjacentSquare.y;
            }
        }
        if (n != 0 && !isoGameCharacter.isZombie()) {
            final IsoGridSquare freeSquareAfterObstacles = this.getFreeSquareAfterObstacles(gridSquare, value);
            if (freeSquareAfterObstacles == null) {
                n = 0;
            }
            else {
                m = freeSquareAfterObstacles.x;
                i2 = freeSquareAfterObstacles.y;
            }
        }
        stateMachineParams.put(ClimbThroughWindowState.PARAM_START_X, i);
        stateMachineParams.put(ClimbThroughWindowState.PARAM_START_Y, j);
        stateMachineParams.put(ClimbThroughWindowState.PARAM_Z, z);
        stateMachineParams.put(ClimbThroughWindowState.PARAM_OPPOSITE_X, k);
        stateMachineParams.put(ClimbThroughWindowState.PARAM_OPPOSITE_Y, l);
        stateMachineParams.put(ClimbThroughWindowState.PARAM_END_X, m);
        stateMachineParams.put(ClimbThroughWindowState.PARAM_END_Y, i2);
        stateMachineParams.put(ClimbThroughWindowState.PARAM_DIR, value);
        stateMachineParams.put(ClimbThroughWindowState.PARAM_ZOMBIE_ON_FLOOR, Boolean.FALSE);
        stateMachineParams.put(ClimbThroughWindowState.PARAM_PREV_STATE, isoGameCharacter.getCurrentState());
        stateMachineParams.put(ClimbThroughWindowState.PARAM_SCRATCH, b ? Boolean.TRUE : Boolean.FALSE);
        stateMachineParams.put(ClimbThroughWindowState.PARAM_COUNTER, (n != 0) ? Boolean.TRUE : Boolean.FALSE);
        stateMachineParams.put(ClimbThroughWindowState.PARAM_SOLID_FLOOR, b3 ? Boolean.TRUE : Boolean.FALSE);
        stateMachineParams.put(ClimbThroughWindowState.PARAM_SHEET_ROPE, b4 ? Boolean.TRUE : Boolean.FALSE);
    }
    
    static {
        _instance = new ClimbThroughWindowState();
        PARAM_START_X = 0;
        PARAM_START_Y = 1;
        PARAM_Z = 2;
        PARAM_OPPOSITE_X = 3;
        PARAM_OPPOSITE_Y = 4;
        PARAM_DIR = 5;
        PARAM_ZOMBIE_ON_FLOOR = 6;
        PARAM_PREV_STATE = 7;
        PARAM_SCRATCH = 8;
        PARAM_COUNTER = 9;
        PARAM_SOLID_FLOOR = 10;
        PARAM_SHEET_ROPE = 11;
        PARAM_END_X = 12;
        PARAM_END_Y = 13;
    }
}
