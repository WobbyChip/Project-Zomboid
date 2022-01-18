// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.core.properties.PropertyContainer;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.skills.PerkFactory;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.Moodles.MoodleType;
import zombie.debug.DebugOptions;
import zombie.util.StringUtils;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.GameTime;
import zombie.characters.MoveDeltaModifiers;
import zombie.audio.parameters.ParameterCharacterMovementSpeed;
import zombie.iso.IsoObject;
import fmod.fmod.FMODManager;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;
import zombie.iso.IsoMovingObject;
import zombie.core.math.PZMath;
import zombie.characters.Stats;
import java.util.HashMap;
import zombie.characters.IsoPlayer;
import zombie.util.Type;
import zombie.characters.IsoZombie;
import zombie.ZomboidGlobals;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ClimbOverFenceState extends State
{
    private static final ClimbOverFenceState _instance;
    static final Integer PARAM_START_X;
    static final Integer PARAM_START_Y;
    static final Integer PARAM_Z;
    static final Integer PARAM_END_X;
    static final Integer PARAM_END_Y;
    static final Integer PARAM_DIR;
    static final Integer PARAM_ZOMBIE_ON_FLOOR;
    static final Integer PARAM_PREV_STATE;
    static final Integer PARAM_SCRATCH;
    static final Integer PARAM_COUNTER;
    static final Integer PARAM_SOLID_FLOOR;
    static final Integer PARAM_SHEET_ROPE;
    static final Integer PARAM_RUN;
    static final Integer PARAM_SPRINT;
    static final Integer PARAM_COLLIDABLE;
    static final int FENCE_TYPE_WOOD = 0;
    static final int FENCE_TYPE_METAL = 1;
    static final int FENCE_TYPE_SANDBAG = 2;
    static final int FENCE_TYPE_GRAVELBAG = 3;
    static final int FENCE_TYPE_BARBWIRE = 4;
    static final int TRIP_WOOD = 0;
    static final int TRIP_METAL = 1;
    static final int TRIP_SANDBAG = 2;
    static final int TRIP_GRAVELBAG = 3;
    static final int TRIP_BARBWIRE = 4;
    public static final int TRIP_TREE = 5;
    public static final int TRIP_ZOMBIE = 6;
    public static final int COLLIDE_WITH_WALL = 7;
    
    public static ClimbOverFenceState instance() {
        return ClimbOverFenceState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setVariable("FenceLungeX", 0.0f);
        isoGameCharacter.setVariable("FenceLungeY", 0.0f);
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        isoGameCharacter.setIgnoreMovement(true);
        if (stateMachineParams.get(ClimbOverFenceState.PARAM_RUN) == Boolean.TRUE) {
            isoGameCharacter.setVariable("VaultOverRun", true);
            final Stats stats = isoGameCharacter.getStats();
            stats.endurance -= (float)(ZomboidGlobals.RunningEnduranceReduce * 300.0);
        }
        else if (stateMachineParams.get(ClimbOverFenceState.PARAM_SPRINT) == Boolean.TRUE) {
            isoGameCharacter.setVariable("VaultOverSprint", true);
            final Stats stats2 = isoGameCharacter.getStats();
            stats2.endurance -= (float)(ZomboidGlobals.RunningEnduranceReduce * 700.0);
        }
        final boolean b = stateMachineParams.get(ClimbOverFenceState.PARAM_COUNTER) == Boolean.TRUE;
        isoGameCharacter.setVariable("ClimbingFence", true);
        isoGameCharacter.setVariable("ClimbFenceStarted", false);
        isoGameCharacter.setVariable("ClimbFenceFinished", false);
        isoGameCharacter.setVariable("ClimbFenceOutcome", b ? "obstacle" : "success");
        isoGameCharacter.clearVariable("ClimbFenceFlopped");
        if ((isoGameCharacter.getVariableBoolean("VaultOverRun") || isoGameCharacter.getVariableBoolean("VaultOverSprint")) && this.shouldFallAfterVaultOver(isoGameCharacter)) {
            isoGameCharacter.setVariable("ClimbFenceOutcome", "fall");
        }
        final IsoZombie lungeXVars = Type.tryCastTo(isoGameCharacter, IsoZombie.class);
        if (!b && lungeXVars != null && lungeXVars.shouldDoFenceLunge()) {
            isoGameCharacter.setVariable("ClimbFenceOutcome", "lunge");
            this.setLungeXVars(lungeXVars);
        }
        if (stateMachineParams.get(ClimbOverFenceState.PARAM_SOLID_FLOOR) == Boolean.FALSE) {
            isoGameCharacter.setVariable("ClimbFenceOutcome", "falling");
        }
        if (!(isoGameCharacter instanceof IsoZombie) && stateMachineParams.get(ClimbOverFenceState.PARAM_SHEET_ROPE) == Boolean.TRUE) {
            isoGameCharacter.setVariable("ClimbFenceOutcome", "rope");
        }
        if (isoGameCharacter instanceof IsoPlayer && ((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
            ((IsoPlayer)isoGameCharacter).dirtyRecalcGridStackTime = 20.0f;
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
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final IsoDirections isoDirections = Type.tryCastTo((Object)stateMachineParams.get(ClimbOverFenceState.PARAM_DIR), IsoDirections.class);
        final int intValue = stateMachineParams.get(ClimbOverFenceState.PARAM_END_X);
        final int intValue2 = stateMachineParams.get(ClimbOverFenceState.PARAM_END_Y);
        isoGameCharacter.setAnimated(true);
        if (isoDirections == IsoDirections.N) {
            isoGameCharacter.setDir(IsoDirections.N);
        }
        else if (isoDirections == IsoDirections.S) {
            isoGameCharacter.setDir(IsoDirections.S);
        }
        else if (isoDirections == IsoDirections.W) {
            isoGameCharacter.setDir(IsoDirections.W);
        }
        else if (isoDirections == IsoDirections.E) {
            isoGameCharacter.setDir(IsoDirections.E);
        }
        final String variableString = isoGameCharacter.getVariableString("ClimbFenceOutcome");
        if (!"lunge".equals(variableString)) {
            final float n = 0.05f;
            if (isoDirections == IsoDirections.N || isoDirections == IsoDirections.S) {
                final float clamp = PZMath.clamp(isoGameCharacter.x, intValue + n, intValue + 1 - n);
                isoGameCharacter.nx = clamp;
                isoGameCharacter.x = clamp;
            }
            else if (isoDirections == IsoDirections.W || isoDirections == IsoDirections.E) {
                final float clamp2 = PZMath.clamp(isoGameCharacter.y, intValue2 + n, intValue2 + 1 - n);
                isoGameCharacter.ny = clamp2;
                isoGameCharacter.y = clamp2;
            }
        }
        if (isoGameCharacter.getVariableBoolean("ClimbFenceStarted") && !"back".equals(variableString) && !"fallback".equals(variableString) && !"lunge".equalsIgnoreCase(variableString) && !"obstacle".equals(variableString) && !"obstacleEnd".equals(variableString)) {
            float n2 = stateMachineParams.get(ClimbOverFenceState.PARAM_START_X);
            float n3 = stateMachineParams.get(ClimbOverFenceState.PARAM_START_Y);
            switch (isoDirections) {
                case N: {
                    n3 -= 0.1f;
                    break;
                }
                case S: {
                    n3 += 1.1f;
                    break;
                }
                case W: {
                    n2 -= 0.1f;
                    break;
                }
                case E: {
                    n2 += 1.1f;
                    break;
                }
            }
            if ((int)isoGameCharacter.x != (int)n2 && (isoDirections == IsoDirections.W || isoDirections == IsoDirections.E)) {
                this.slideX(isoGameCharacter, n2);
            }
            if ((int)isoGameCharacter.y != (int)n3 && (isoDirections == IsoDirections.N || isoDirections == IsoDirections.S)) {
                this.slideY(isoGameCharacter, n3);
            }
        }
        if (isoGameCharacter instanceof IsoZombie) {
            final boolean fallOnFront = stateMachineParams.get(ClimbOverFenceState.PARAM_ZOMBIE_ON_FLOOR) == Boolean.TRUE;
            isoGameCharacter.setOnFloor(fallOnFront);
            ((IsoZombie)isoGameCharacter).setKnockedDown(fallOnFront);
            isoGameCharacter.setFallOnFront(fallOnFront);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if (isoGameCharacter instanceof IsoPlayer && "fall".equals(isoGameCharacter.getVariableString("ClimbFenceOutcome"))) {
            isoGameCharacter.setSprinting(false);
        }
        isoGameCharacter.clearVariable("ClimbingFence");
        isoGameCharacter.clearVariable("ClimbFenceFinished");
        isoGameCharacter.clearVariable("ClimbFenceOutcome");
        isoGameCharacter.clearVariable("ClimbFenceStarted");
        isoGameCharacter.clearVariable("ClimbFenceFlopped");
        isoGameCharacter.ClearVariable("VaultOverSprint");
        isoGameCharacter.ClearVariable("VaultOverRun");
        isoGameCharacter.setIgnoreMovement(false);
        final IsoZombie isoZombie = Type.tryCastTo(isoGameCharacter, IsoZombie.class);
        if (isoZombie != null) {
            isoZombie.AllowRepathDelay = 0.0f;
            if (stateMachineParams.get(ClimbOverFenceState.PARAM_PREV_STATE) == PathFindState.instance()) {
                if (isoGameCharacter.getPathFindBehavior2().getTargetChar() == null) {
                    isoGameCharacter.setVariable("bPathfind", true);
                    isoGameCharacter.setVariable("bMoving", false);
                }
                else if (isoZombie.isTargetLocationKnown()) {
                    isoGameCharacter.pathToCharacter(isoGameCharacter.getPathFindBehavior2().getTargetChar());
                }
                else if (isoZombie.LastTargetSeenX != -1) {
                    isoGameCharacter.pathToLocation(isoZombie.LastTargetSeenX, isoZombie.LastTargetSeenY, isoZombie.LastTargetSeenZ);
                }
            }
            else if (stateMachineParams.get(ClimbOverFenceState.PARAM_PREV_STATE) == WalkTowardState.instance() || stateMachineParams.get(ClimbOverFenceState.PARAM_PREV_STATE) == WalkTowardNetworkState.instance()) {
                isoGameCharacter.setVariable("bPathFind", false);
                isoGameCharacter.setVariable("bMoving", true);
            }
        }
        if (isoGameCharacter instanceof IsoZombie) {
            ((IsoZombie)isoGameCharacter).networkAI.isClimbing = false;
        }
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if (animEvent.m_EventName.equalsIgnoreCase("CheckAttack")) {
            final IsoZombie isoZombie = Type.tryCastTo(isoGameCharacter, IsoZombie.class);
            if (isoZombie != null && isoZombie.target instanceof IsoGameCharacter) {
                ((IsoGameCharacter)isoZombie.target).attackFromWindowsLunge(isoZombie);
            }
        }
        if (animEvent.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {}
        if (animEvent.m_EventName.equalsIgnoreCase("VaultSprintFallLanded")) {
            isoGameCharacter.dropHandItems();
            isoGameCharacter.fallenOnKnees();
        }
        if (animEvent.m_EventName.equalsIgnoreCase("FallenOnKnees")) {
            isoGameCharacter.fallenOnKnees();
        }
        if (animEvent.m_EventName.equalsIgnoreCase("OnFloor")) {
            stateMachineParams.put(ClimbOverFenceState.PARAM_ZOMBIE_ON_FLOOR, Boolean.parseBoolean(animEvent.m_ParameterValue));
            if (Boolean.parseBoolean(animEvent.m_ParameterValue)) {
                this.setLungeXVars((IsoZombie)isoGameCharacter);
                final IsoObject fence = this.getFence(isoGameCharacter);
                if (this.countZombiesClimbingOver(fence) >= 2) {
                    final IsoObject isoObject = fence;
                    isoObject.Damage -= (short)(Rand.Next(7, 12) / (this.isMetalFence(fence) ? 2 : 1));
                    if (fence.Damage <= 0) {
                        fence.destroyFence(Type.tryCastTo((Boolean)stateMachineParams.get(ClimbOverFenceState.PARAM_DIR), IsoDirections.class));
                    }
                }
                isoGameCharacter.setVariable("ClimbFenceFlopped", true);
            }
        }
        if (animEvent.m_EventName.equalsIgnoreCase("PlayFenceSound")) {
            final IsoObject fence2 = this.getFence(isoGameCharacter);
            if (fence2 == null) {
                return;
            }
            final int fenceType = this.getFenceType(fence2);
            final long playSound = isoGameCharacter.playSound(animEvent.m_ParameterValue);
            final ParameterCharacterMovementSpeed parameterCharacterMovementSpeed = ((IsoPlayer)isoGameCharacter).getParameterCharacterMovementSpeed();
            isoGameCharacter.getEmitter().setParameterValue(playSound, parameterCharacterMovementSpeed.getParameterDescription(), parameterCharacterMovementSpeed.calculateCurrentValue());
            isoGameCharacter.getEmitter().setParameterValue(playSound, FMODManager.instance.getParameterDescription("FenceTypeLow"), (float)fenceType);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("PlayTripSound")) {
            final IsoObject fence3 = this.getFence(isoGameCharacter);
            if (fence3 == null) {
                return;
            }
            final int tripType = this.getTripType(fence3);
            final long playSound2 = isoGameCharacter.playSound(animEvent.m_ParameterValue);
            final ParameterCharacterMovementSpeed parameterCharacterMovementSpeed2 = ((IsoPlayer)isoGameCharacter).getParameterCharacterMovementSpeed();
            isoGameCharacter.getEmitter().setParameterValue(playSound2, parameterCharacterMovementSpeed2.getParameterDescription(), parameterCharacterMovementSpeed2.calculateCurrentValue());
            isoGameCharacter.getEmitter().setParameterValue(playSound2, FMODManager.instance.getParameterDescription("TripObstacleType"), (float)tripType);
        }
        if (animEvent.m_EventName.equalsIgnoreCase("SetCollidable")) {
            stateMachineParams.put(ClimbOverFenceState.PARAM_COLLIDABLE, Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
        if (animEvent.m_EventName.equalsIgnoreCase("VaultOverStarted")) {
            if (isoGameCharacter instanceof IsoPlayer && !((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
                return;
            }
            if (isoGameCharacter.isVariable("ClimbFenceOutcome", "fall")) {
                isoGameCharacter.reportEvent("EventFallClimb");
                isoGameCharacter.setVariable("BumpDone", true);
                isoGameCharacter.setFallOnFront(true);
            }
        }
    }
    
    @Override
    public void getDeltaModifiers(final IsoGameCharacter isoGameCharacter, final MoveDeltaModifiers moveDeltaModifiers) {
        final boolean b = isoGameCharacter.getPath2() != null;
        final boolean b2 = isoGameCharacter instanceof IsoPlayer;
        if (b && b2) {
            moveDeltaModifiers.turnDelta = Math.max(moveDeltaModifiers.turnDelta, 10.0f);
        }
    }
    
    @Override
    public boolean isIgnoreCollide(final IsoGameCharacter isoGameCharacter, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final int intValue = stateMachineParams.get(ClimbOverFenceState.PARAM_START_X);
        final int intValue2 = stateMachineParams.get(ClimbOverFenceState.PARAM_START_Y);
        final int intValue3 = stateMachineParams.get(ClimbOverFenceState.PARAM_END_X);
        final int intValue4 = stateMachineParams.get(ClimbOverFenceState.PARAM_END_Y);
        final int intValue5 = stateMachineParams.get(ClimbOverFenceState.PARAM_Z);
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
    
    private void slideX(final IsoGameCharacter isoGameCharacter, final float n) {
        final float a = 0.05f * GameTime.getInstance().getMultiplier() / 1.6f;
        isoGameCharacter.x += ((n > isoGameCharacter.x) ? Math.min(a, n - isoGameCharacter.x) : Math.max(-a, n - isoGameCharacter.x));
        isoGameCharacter.nx = isoGameCharacter.x;
    }
    
    private void slideY(final IsoGameCharacter isoGameCharacter, final float n) {
        final float a = 0.05f * GameTime.getInstance().getMultiplier() / 1.6f;
        isoGameCharacter.y += ((n > isoGameCharacter.y) ? Math.min(a, n - isoGameCharacter.y) : Math.max(-a, n - isoGameCharacter.y));
        isoGameCharacter.ny = isoGameCharacter.y;
    }
    
    private IsoObject getFence(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final int intValue = stateMachineParams.get(ClimbOverFenceState.PARAM_START_X);
        final int intValue2 = stateMachineParams.get(ClimbOverFenceState.PARAM_START_Y);
        final int intValue3 = stateMachineParams.get(ClimbOverFenceState.PARAM_Z);
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(intValue, intValue2, intValue3);
        final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(stateMachineParams.get(ClimbOverFenceState.PARAM_END_X), stateMachineParams.get(ClimbOverFenceState.PARAM_END_Y), intValue3);
        if (gridSquare == null || gridSquare2 == null) {
            return null;
        }
        return gridSquare.getHoppableTo(gridSquare2);
    }
    
    private int getFenceType(final IsoObject isoObject) {
        if (isoObject.getSprite() == null) {
            return 0;
        }
        String val = isoObject.getSprite().getProperties().Val("FenceTypeLow");
        if (val != null) {
            if ("Sandbag".equals(val) && isoObject.getName() != null && StringUtils.containsIgnoreCase(isoObject.getName(), "Gravel")) {
                val = "Gravelbag";
            }
            final String s = val;
            int n2 = 0;
            switch (s) {
                case "Wood": {
                    n2 = 0;
                    break;
                }
                case "Metal": {
                    n2 = 1;
                    break;
                }
                case "Sandbag": {
                    n2 = 2;
                    break;
                }
                case "Gravelbag": {
                    n2 = 3;
                    break;
                }
                case "Barbwire": {
                    n2 = 4;
                    break;
                }
                default: {
                    n2 = 0;
                    break;
                }
            }
            return n2;
        }
        return 0;
    }
    
    private int getTripType(final IsoObject isoObject) {
        if (isoObject.getSprite() == null) {
            return 0;
        }
        String val = isoObject.getSprite().getProperties().Val("FenceTypeLow");
        if (val != null) {
            if ("Sandbag".equals(val) && isoObject.getName() != null && StringUtils.containsIgnoreCase(isoObject.getName(), "Gravel")) {
                val = "Gravelbag";
            }
            final String s = val;
            int n2 = 0;
            switch (s) {
                case "Wood": {
                    n2 = 0;
                    break;
                }
                case "Metal": {
                    n2 = 1;
                    break;
                }
                case "Sandbag": {
                    n2 = 2;
                    break;
                }
                case "Gravelbag": {
                    n2 = 3;
                    break;
                }
                case "Barbwire": {
                    n2 = 4;
                    break;
                }
                default: {
                    n2 = 0;
                    break;
                }
            }
            return n2;
        }
        return 0;
    }
    
    private boolean shouldFallAfterVaultOver(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter instanceof IsoPlayer && !((IsoPlayer)isoGameCharacter).isLocalPlayer()) {
            return ((IsoPlayer)isoGameCharacter).networkAI.climbFenceOutcomeFall;
        }
        if (DebugOptions.instance.Character.Debug.AlwaysTripOverFence.getValue()) {
            return true;
        }
        float n = 0.0f;
        if (isoGameCharacter.getVariableBoolean("VaultOverSprint")) {
            n = 10.0f;
        }
        if (isoGameCharacter.getMoodles() != null) {
            n = n + isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Endurance) * 10 + isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 13 + isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Pain) * 5;
        }
        final BodyPart bodyPart = isoGameCharacter.getBodyDamage().getBodyPart(BodyPartType.Torso_Lower);
        if (bodyPart.getAdditionalPain(true) > 20.0f) {
            n += (bodyPart.getAdditionalPain(true) - 20.0f) / 10.0f;
        }
        if (isoGameCharacter.Traits.Clumsy.isSet()) {
            n += 10.0f;
        }
        if (isoGameCharacter.Traits.Graceful.isSet()) {
            n -= 10.0f;
        }
        if (isoGameCharacter.Traits.VeryUnderweight.isSet()) {
            n += 20.0f;
        }
        if (isoGameCharacter.Traits.Underweight.isSet()) {
            n += 10.0f;
        }
        if (isoGameCharacter.Traits.Obese.isSet()) {
            n += 20.0f;
        }
        if (isoGameCharacter.Traits.Overweight.isSet()) {
            n += 10.0f;
        }
        return Rand.Next(100) < n - isoGameCharacter.getPerkLevel(PerkFactory.Perks.Fitness);
    }
    
    private int countZombiesClimbingOver(final IsoObject isoObject) {
        if (isoObject == null || isoObject.getSquare() == null) {
            return 0;
        }
        final int n = 0;
        final IsoGridSquare square = isoObject.getSquare();
        final int n2 = n + this.countZombiesClimbingOver(isoObject, square);
        IsoGridSquare isoGridSquare;
        if (isoObject.getProperties().Is(IsoFlagType.HoppableN)) {
            isoGridSquare = square.getAdjacentSquare(IsoDirections.N);
        }
        else {
            isoGridSquare = square.getAdjacentSquare(IsoDirections.W);
        }
        return n2 + this.countZombiesClimbingOver(isoObject, isoGridSquare);
    }
    
    private int countZombiesClimbingOver(final IsoObject isoObject, final IsoGridSquare isoGridSquare) {
        if (isoGridSquare == null) {
            return 0;
        }
        int n = 0;
        for (int i = 0; i < isoGridSquare.getMovingObjects().size(); ++i) {
            final IsoZombie isoZombie = Type.tryCastTo(isoGridSquare.getMovingObjects().get(i), IsoZombie.class);
            if (isoZombie != null && isoZombie.target != null) {
                if (isoZombie.isCurrentState(this)) {
                    if (this.getFence(isoZombie) == isoObject) {
                        ++n;
                    }
                }
            }
        }
        return n;
    }
    
    private boolean isMetalFence(final IsoObject isoObject) {
        if (isoObject == null || isoObject.getProperties() == null) {
            return false;
        }
        final PropertyContainer properties = isoObject.getProperties();
        final String val = properties.Val("Material");
        final String val2 = properties.Val("Material2");
        final String val3 = properties.Val("Material3");
        if ("MetalBars".equals(val) || "MetalBars".equals(val2) || "MetalBars".equals(val3)) {
            return true;
        }
        if ("MetalWire".equals(val) || "MetalWire".equals(val2) || "MetalWire".equals(val3)) {
            return true;
        }
        if (isoObject instanceof IsoThumpable && isoObject.hasModData()) {
            final KahluaTableIterator iterator = isoObject.getModData().iterator();
            while (iterator.advance()) {
                final String s = Type.tryCastTo(iterator.getKey(), String.class);
                if (s != null && s.contains("MetalPipe")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setParams(final IsoGameCharacter isoGameCharacter, final IsoDirections value) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final int x = isoGameCharacter.getSquare().getX();
        final int y = isoGameCharacter.getSquare().getY();
        final int z = isoGameCharacter.getSquare().getZ();
        final int i = x;
        final int j = y;
        int k = x;
        int l = y;
        switch (value) {
            case N: {
                --l;
                break;
            }
            case S: {
                ++l;
                break;
            }
            case W: {
                --k;
                break;
            }
            case E: {
                ++k;
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid direction");
            }
        }
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(k, l, z);
        final boolean b = false;
        final boolean b2 = gridSquare != null && gridSquare.Is(IsoFlagType.solidtrans);
        final boolean b3 = gridSquare != null && gridSquare.TreatAsSolidFloor();
        final boolean b4 = gridSquare != null && isoGameCharacter.canClimbDownSheetRope(gridSquare);
        stateMachineParams.put(ClimbOverFenceState.PARAM_START_X, i);
        stateMachineParams.put(ClimbOverFenceState.PARAM_START_Y, j);
        stateMachineParams.put(ClimbOverFenceState.PARAM_Z, z);
        stateMachineParams.put(ClimbOverFenceState.PARAM_END_X, k);
        stateMachineParams.put(ClimbOverFenceState.PARAM_END_Y, l);
        stateMachineParams.put(ClimbOverFenceState.PARAM_DIR, value);
        stateMachineParams.put(ClimbOverFenceState.PARAM_ZOMBIE_ON_FLOOR, Boolean.FALSE);
        stateMachineParams.put(ClimbOverFenceState.PARAM_PREV_STATE, isoGameCharacter.getCurrentState());
        stateMachineParams.put(ClimbOverFenceState.PARAM_SCRATCH, b ? Boolean.TRUE : Boolean.FALSE);
        stateMachineParams.put(ClimbOverFenceState.PARAM_COUNTER, b2 ? Boolean.TRUE : Boolean.FALSE);
        stateMachineParams.put(ClimbOverFenceState.PARAM_SOLID_FLOOR, b3 ? Boolean.TRUE : Boolean.FALSE);
        stateMachineParams.put(ClimbOverFenceState.PARAM_SHEET_ROPE, b4 ? Boolean.TRUE : Boolean.FALSE);
        stateMachineParams.put(ClimbOverFenceState.PARAM_RUN, isoGameCharacter.isRunning() ? Boolean.TRUE : Boolean.FALSE);
        stateMachineParams.put(ClimbOverFenceState.PARAM_SPRINT, isoGameCharacter.isSprinting() ? Boolean.TRUE : Boolean.FALSE);
        stateMachineParams.put(ClimbOverFenceState.PARAM_COLLIDABLE, Boolean.FALSE);
    }
    
    static {
        _instance = new ClimbOverFenceState();
        PARAM_START_X = 0;
        PARAM_START_Y = 1;
        PARAM_Z = 2;
        PARAM_END_X = 3;
        PARAM_END_Y = 4;
        PARAM_DIR = 5;
        PARAM_ZOMBIE_ON_FLOOR = 6;
        PARAM_PREV_STATE = 7;
        PARAM_SCRATCH = 8;
        PARAM_COUNTER = 9;
        PARAM_SOLID_FLOOR = 10;
        PARAM_SHEET_ROPE = 11;
        PARAM_RUN = 12;
        PARAM_SPRINT = 13;
        PARAM_COLLIDABLE = 14;
    }
}
