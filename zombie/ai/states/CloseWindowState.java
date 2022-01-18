// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.characters.skills.PerkFactory;
import zombie.GameTime;
import zombie.iso.IsoObject;
import zombie.characters.Moodles.MoodleType;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.characters.IsoPlayer;
import java.util.HashMap;
import zombie.iso.IsoDirections;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import zombie.iso.objects.IsoWindow;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class CloseWindowState extends State
{
    private static final CloseWindowState _instance;
    
    public static CloseWindowState instance() {
        return CloseWindowState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        isoGameCharacter.setIgnoreMovement(true);
        isoGameCharacter.setHideWeaponModel(true);
        final IsoWindow isoWindow = stateMachineParams.get(0);
        if (Core.bDebug && DebugOptions.instance.CheatWindowUnlock.getValue()) {
            isoWindow.Locked = false;
            isoWindow.PermaLocked = false;
        }
        if (isoWindow.north) {
            if (isoWindow.getSquare().getY() < isoGameCharacter.getY()) {
                isoGameCharacter.setDir(IsoDirections.N);
            }
            else {
                isoGameCharacter.setDir(IsoDirections.S);
            }
        }
        else if (isoWindow.getSquare().getX() < isoGameCharacter.getX()) {
            isoGameCharacter.setDir(IsoDirections.W);
        }
        else {
            isoGameCharacter.setDir(IsoDirections.E);
        }
        isoGameCharacter.setVariable("bCloseWindow", true);
        isoGameCharacter.clearVariable("BlockWindow");
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if (!isoGameCharacter.getVariableBoolean("bCloseWindow")) {
            return;
        }
        final IsoPlayer isoPlayer = (IsoPlayer)isoGameCharacter;
        if (isoPlayer.pressedMovement(false) || isoPlayer.pressedCancelAction()) {
            isoGameCharacter.setVariable("bCloseWindow", false);
            return;
        }
        if (!(stateMachineParams.get(0) instanceof IsoWindow)) {
            isoGameCharacter.setVariable("bCloseWindow", false);
            return;
        }
        final IsoWindow isoWindow = stateMachineParams.get(0);
        if (isoWindow == null || isoWindow.getObjectIndex() == -1) {
            isoGameCharacter.setVariable("bCloseWindow", false);
            return;
        }
        isoPlayer.setCollidable(true);
        isoPlayer.updateLOS();
        if (isoWindow.north) {
            if (isoWindow.getSquare().getY() < isoGameCharacter.getY()) {
                isoGameCharacter.setDir(IsoDirections.N);
            }
            else {
                isoGameCharacter.setDir(IsoDirections.S);
            }
        }
        else if (isoWindow.getSquare().getX() < isoGameCharacter.getX()) {
            isoGameCharacter.setDir(IsoDirections.W);
        }
        else {
            isoGameCharacter.setDir(IsoDirections.E);
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.clearVariable("BlockWindow");
        isoGameCharacter.clearVariable("bCloseWindow");
        isoGameCharacter.clearVariable("CloseWindowOutcome");
        isoGameCharacter.clearVariable("StopAfterAnimLooped");
        isoGameCharacter.setIgnoreMovement(false);
        isoGameCharacter.setHideWeaponModel(false);
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if (!isoGameCharacter.getVariableBoolean("bCloseWindow")) {
            return;
        }
        if (!(stateMachineParams.get(0) instanceof IsoWindow)) {
            isoGameCharacter.setVariable("bCloseWindow", false);
            return;
        }
        final IsoWindow isoWindow = stateMachineParams.get(0);
        if (animEvent.m_EventName.equalsIgnoreCase("WindowAnimLooped")) {
            if ("start".equalsIgnoreCase(animEvent.m_ParameterValue)) {
                Math.max(5 - isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic), 1);
                if (isoWindow.isPermaLocked() || isoWindow.getFirstCharacterClimbingThrough() != null) {
                    isoGameCharacter.setVariable("CloseWindowOutcome", "struggle");
                }
                else {
                    isoGameCharacter.setVariable("CloseWindowOutcome", "success");
                }
                return;
            }
            if (animEvent.m_ParameterValue.equalsIgnoreCase(isoGameCharacter.getVariableString("StopAfterAnimLooped"))) {
                isoGameCharacter.setVariable("bCloseWindow", false);
            }
        }
        if (animEvent.m_EventName.equalsIgnoreCase("WindowCloseAttempt")) {
            this.onAttemptFinished(isoGameCharacter, isoWindow);
        }
        else if (animEvent.m_EventName.equalsIgnoreCase("WindowCloseSuccess")) {
            this.onSuccess(isoGameCharacter, isoWindow);
        }
    }
    
    @Override
    public boolean isDoingActionThatCanBeCancelled() {
        return true;
    }
    
    private void onAttemptFinished(final IsoGameCharacter isoGameCharacter, final IsoWindow isoWindow) {
        this.exert(isoGameCharacter);
        if (isoWindow.isPermaLocked()) {
            isoGameCharacter.getEmitter().playSound("WindowIsLocked", isoWindow);
            isoGameCharacter.setVariable("CloseWindowOutcome", "fail");
            isoGameCharacter.setVariable("StopAfterAnimLooped", "fail");
            return;
        }
        Math.max(5 - isoGameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic), 3);
        if (isoWindow.isPermaLocked() || isoWindow.getFirstCharacterClimbingThrough() != null) {
            isoGameCharacter.setVariable("CloseWindowOutcome", "struggle");
        }
        else {
            isoGameCharacter.setVariable("CloseWindowOutcome", "success");
        }
    }
    
    private void onSuccess(final IsoGameCharacter isoGameCharacter, final IsoWindow isoWindow) {
        isoGameCharacter.setVariable("StopAfterAnimLooped", "success");
        IsoPlayer.getInstance().ContextPanic = 0.0f;
        if (isoWindow.getObjectIndex() != -1 && isoWindow.open) {
            isoWindow.ToggleWindow(isoGameCharacter);
        }
    }
    
    private void exert(final IsoGameCharacter isoGameCharacter) {
        final float n = GameTime.getInstance().getMultiplier() / 1.6f;
        switch (isoGameCharacter.getPerkLevel(PerkFactory.Perks.Fitness)) {
            case 1: {
                isoGameCharacter.exert(0.01f * n);
                break;
            }
            case 2: {
                isoGameCharacter.exert(0.009f * n);
                break;
            }
            case 3: {
                isoGameCharacter.exert(0.008f * n);
                break;
            }
            case 4: {
                isoGameCharacter.exert(0.007f * n);
                break;
            }
            case 5: {
                isoGameCharacter.exert(0.006f * n);
                break;
            }
            case 6: {
                isoGameCharacter.exert(0.005f * n);
                break;
            }
            case 7: {
                isoGameCharacter.exert(0.004f * n);
                break;
            }
            case 8: {
                isoGameCharacter.exert(0.003f * n);
                break;
            }
            case 9: {
                isoGameCharacter.exert(0.0025f * n);
                break;
            }
            case 10: {
                isoGameCharacter.exert(0.002f * n);
                break;
            }
        }
    }
    
    public IsoWindow getWindow(final IsoGameCharacter isoGameCharacter) {
        if (!isoGameCharacter.isCurrentState(this)) {
            return null;
        }
        return isoGameCharacter.getStateMachineParams(this).get(0);
    }
    
    static {
        _instance = new CloseWindowState();
    }
}
