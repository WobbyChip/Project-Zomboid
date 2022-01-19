// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoMovingObject;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.iso.IsoWorld;
import zombie.core.properties.PropertyContainer;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.IsoGridSquare;
import zombie.core.math.PZMath;
import zombie.iso.IsoObject;
import fmod.fmod.FMODManager;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoZombie;
import java.util.HashMap;
import zombie.GameTime;
import zombie.iso.IsoDirections;
import zombie.characters.Stats;
import zombie.characters.IsoPlayer;
import zombie.ZomboidGlobals;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class ClimbOverWallState extends State
{
    private static final ClimbOverWallState _instance;
    static final Integer PARAM_START_X;
    static final Integer PARAM_START_Y;
    static final Integer PARAM_Z;
    static final Integer PARAM_END_X;
    static final Integer PARAM_END_Y;
    static final Integer PARAM_DIR;
    static final int FENCE_TYPE_WOOD = 0;
    static final int FENCE_TYPE_METAL = 1;
    
    public static ClimbOverWallState instance() {
        return ClimbOverWallState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(true);
        isoGameCharacter.setHideWeaponModel(true);
        isoGameCharacter.getStateMachineParams(this);
        final Stats stats = isoGameCharacter.getStats();
        stats.endurance -= (float)(ZomboidGlobals.RunningEnduranceReduce * 1200.0);
        final IsoPlayer isoPlayer = (IsoPlayer)isoGameCharacter;
        final boolean climbOverWallStruggle = isoPlayer.isClimbOverWallStruggle();
        if (climbOverWallStruggle) {
            final Stats stats2 = isoGameCharacter.getStats();
            stats2.endurance -= (float)(ZomboidGlobals.RunningEnduranceReduce * 500.0);
        }
        final boolean climbOverWallSuccess = isoPlayer.isClimbOverWallSuccess();
        isoGameCharacter.setVariable("ClimbFenceFinished", false);
        isoGameCharacter.setVariable("ClimbFenceOutcome", climbOverWallSuccess ? "success" : "fail");
        isoGameCharacter.setVariable("ClimbFenceStarted", false);
        isoGameCharacter.setVariable("ClimbFenceStruggle", climbOverWallStruggle);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final IsoDirections dir = stateMachineParams.get(ClimbOverWallState.PARAM_DIR);
        isoGameCharacter.setAnimated(true);
        isoGameCharacter.setDir(dir);
        if (!isoGameCharacter.getVariableBoolean("ClimbFenceStarted")) {
            final int intValue = stateMachineParams.get(ClimbOverWallState.PARAM_START_X);
            final int intValue2 = stateMachineParams.get(ClimbOverWallState.PARAM_START_Y);
            final float n = 0.15f;
            float x = isoGameCharacter.getX();
            float y = isoGameCharacter.getY();
            switch (dir) {
                case N: {
                    y = intValue2 + n;
                    break;
                }
                case S: {
                    y = intValue2 + 1 - n;
                    break;
                }
                case W: {
                    x = intValue + n;
                    break;
                }
                case E: {
                    x = intValue + 1 - n;
                    break;
                }
            }
            final float n2 = GameTime.getInstance().getMultiplier() / 1.6f / 8.0f;
            isoGameCharacter.setX(isoGameCharacter.x + (x - isoGameCharacter.x) * n2);
            isoGameCharacter.setY(isoGameCharacter.y + (y - isoGameCharacter.y) * n2);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.clearVariable("ClimbFenceFinished");
        isoGameCharacter.clearVariable("ClimbFenceOutcome");
        isoGameCharacter.clearVariable("ClimbFenceStarted");
        isoGameCharacter.clearVariable("ClimbFenceStruggle");
        isoGameCharacter.setIgnoreMovement(false);
        isoGameCharacter.setHideWeaponModel(false);
        if (isoGameCharacter instanceof IsoZombie) {
            ((IsoZombie)isoGameCharacter).networkAI.isClimbing = false;
        }
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        if (animEvent.m_EventName.equalsIgnoreCase("PlayFenceSound")) {
            final IsoObject fence = this.getFence(isoGameCharacter);
            if (fence == null) {
                return;
            }
            isoGameCharacter.getEmitter().setParameterValue(isoGameCharacter.playSound(animEvent.m_ParameterValue), FMODManager.instance.getParameterDescription("FenceTypeHigh"), (float)this.getFenceType(fence));
        }
    }
    
    @Override
    public boolean isIgnoreCollide(final IsoGameCharacter isoGameCharacter, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final int intValue = stateMachineParams.get(ClimbOverWallState.PARAM_START_X);
        final int intValue2 = stateMachineParams.get(ClimbOverWallState.PARAM_START_Y);
        final int intValue3 = stateMachineParams.get(ClimbOverWallState.PARAM_END_X);
        final int intValue4 = stateMachineParams.get(ClimbOverWallState.PARAM_END_Y);
        final int intValue5 = stateMachineParams.get(ClimbOverWallState.PARAM_Z);
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
    
    private IsoObject getClimbableWallN(final IsoGridSquare isoGridSquare) {
        final IsoObject[] array = isoGridSquare.getObjects().getElements();
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject isoObject = array[i];
            final PropertyContainer properties = isoObject.getProperties();
            if (properties != null) {
                if (!properties.Is(IsoFlagType.CantClimb)) {
                    if (isoObject.getType() == IsoObjectType.wall) {
                        if (properties.Is(IsoFlagType.collideN)) {
                            if (!properties.Is(IsoFlagType.HoppableN)) {
                                return isoObject;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private IsoObject getClimbableWallW(final IsoGridSquare isoGridSquare) {
        final IsoObject[] array = isoGridSquare.getObjects().getElements();
        for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
            final IsoObject isoObject = array[i];
            final PropertyContainer properties = isoObject.getProperties();
            if (properties != null) {
                if (!properties.Is(IsoFlagType.CantClimb)) {
                    if (isoObject.getType() == IsoObjectType.wall) {
                        if (properties.Is(IsoFlagType.collideW)) {
                            if (!properties.Is(IsoFlagType.HoppableW)) {
                                return isoObject;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private IsoObject getFence(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        final int intValue = stateMachineParams.get(ClimbOverWallState.PARAM_START_X);
        final int intValue2 = stateMachineParams.get(ClimbOverWallState.PARAM_START_Y);
        final int intValue3 = stateMachineParams.get(ClimbOverWallState.PARAM_Z);
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(intValue, intValue2, intValue3);
        final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare(stateMachineParams.get(ClimbOverWallState.PARAM_END_X), stateMachineParams.get(ClimbOverWallState.PARAM_END_Y), intValue3);
        if (gridSquare == null || gridSquare2 == null) {
            return null;
        }
        IsoObject isoObject = null;
        switch ((IsoDirections)stateMachineParams.get(ClimbOverWallState.PARAM_DIR)) {
            case N: {
                isoObject = this.getClimbableWallN(gridSquare);
                break;
            }
            case E: {
                isoObject = this.getClimbableWallW(gridSquare2);
                break;
            }
            case W: {
                isoObject = this.getClimbableWallW(gridSquare);
                break;
            }
            case S: {
                isoObject = this.getClimbableWallN(gridSquare2);
                break;
            }
            default: {
                isoObject = null;
                break;
            }
        }
        return isoObject;
    }
    
    private int getFenceType(final IsoObject isoObject) {
        if (isoObject.getSprite() == null) {
            return 0;
        }
        final String val = isoObject.getSprite().getProperties().Val("FenceTypeHigh");
        if (val != null) {
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
                default: {
                    n2 = 0;
                    break;
                }
            }
            return n2;
        }
        return 0;
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
        stateMachineParams.put(ClimbOverWallState.PARAM_START_X, i);
        stateMachineParams.put(ClimbOverWallState.PARAM_START_Y, j);
        stateMachineParams.put(ClimbOverWallState.PARAM_Z, z);
        stateMachineParams.put(ClimbOverWallState.PARAM_END_X, k);
        stateMachineParams.put(ClimbOverWallState.PARAM_END_Y, l);
        stateMachineParams.put(ClimbOverWallState.PARAM_DIR, value);
        final IsoPlayer isoPlayer = (IsoPlayer)isoGameCharacter;
        if (isoPlayer.isLocalPlayer()) {
            int b = 20 + isoGameCharacter.getPerkLevel(PerkFactory.Perks.Fitness) * 2 + isoGameCharacter.getPerkLevel(PerkFactory.Perks.Strength) * 2 - isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Endurance) * 5 - isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 8;
            if (isoGameCharacter.getTraits().contains("Emaciated") || isoGameCharacter.Traits.Obese.isSet() || isoGameCharacter.getTraits().contains("Very Underweight")) {
                b -= 25;
            }
            if (isoGameCharacter.getTraits().contains("Underweight") || isoGameCharacter.getTraits().contains("Overweight")) {
                b -= 15;
            }
            final IsoGridSquare currentSquare = isoGameCharacter.getCurrentSquare();
            if (currentSquare != null) {
                for (int index = 0; index < currentSquare.getMovingObjects().size(); ++index) {
                    final IsoMovingObject isoMovingObject = currentSquare.getMovingObjects().get(index);
                    if (isoMovingObject instanceof IsoZombie) {
                        if (((IsoZombie)isoMovingObject).target == isoGameCharacter && ((IsoZombie)isoMovingObject).getCurrentState() == AttackState.instance()) {
                            b -= 25;
                        }
                        else {
                            b -= 7;
                        }
                    }
                }
            }
            final int max = Math.max(0, b);
            boolean nextBool = Rand.NextBool(max / 2);
            if ("Tutorial".equals(Core.GameMode)) {
                nextBool = false;
            }
            final boolean climbOverWallSuccess = !Rand.NextBool(max);
            isoPlayer.setClimbOverWallStruggle(nextBool);
            isoPlayer.setClimbOverWallSuccess(climbOverWallSuccess);
        }
    }
    
    static {
        _instance = new ClimbOverWallState();
        PARAM_START_X = 0;
        PARAM_START_Y = 1;
        PARAM_Z = 2;
        PARAM_END_X = 3;
        PARAM_END_Y = 4;
        PARAM_DIR = 5;
    }
}
