// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.GameTime;
import zombie.characters.skills.PerkFactory;
import java.nio.ByteBuffer;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoObject;
import zombie.core.Rand;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import java.util.HashMap;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoDirections;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import zombie.iso.objects.IsoWindow;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class OpenWindowState extends State
{
    private static final OpenWindowState _instance;
    private static final Integer PARAM_WINDOW;
    
    public static OpenWindowState instance() {
        return OpenWindowState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(true);
        isoGameCharacter.setHideWeaponModel(true);
        final IsoWindow isoWindow = isoGameCharacter.getStateMachineParams(this).get(OpenWindowState.PARAM_WINDOW);
        if (Core.bDebug && DebugOptions.instance.CheatWindowUnlock.getValue() && isoWindow.getSprite() != null && !isoWindow.getSprite().getProperties().Is("WindowLocked")) {
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
        isoGameCharacter.setVariable("bOpenWindow", true);
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if (!isoGameCharacter.getVariableBoolean("bOpenWindow")) {
            return;
        }
        final IsoPlayer isoPlayer = (IsoPlayer)isoGameCharacter;
        if (isoPlayer.pressedMovement(false) || isoPlayer.pressedCancelAction()) {
            isoGameCharacter.setVariable("bOpenWindow", false);
            return;
        }
        final IsoWindow isoWindow = stateMachineParams.get(OpenWindowState.PARAM_WINDOW);
        if (isoWindow == null || isoWindow.getObjectIndex() == -1) {
            isoGameCharacter.setVariable("bOpenWindow", false);
            return;
        }
        if (IsoPlayer.getInstance().ContextPanic > 5.0f) {
            IsoPlayer.getInstance().ContextPanic = 0.0f;
            isoGameCharacter.setVariable("bOpenWindow", false);
            isoGameCharacter.smashWindow(isoWindow);
            isoGameCharacter.getStateMachineParams(SmashWindowState.instance()).put(3, Boolean.TRUE);
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
        if (Core.bTutorial) {
            if (isoGameCharacter.x != isoWindow.getX() + 0.5f && isoWindow.north) {
                this.slideX(isoGameCharacter, isoWindow.getX() + 0.5f);
            }
            if (isoGameCharacter.y != isoWindow.getY() + 0.5f && !isoWindow.north) {
                this.slideY(isoGameCharacter, isoWindow.getY() + 0.5f);
            }
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(false);
        isoGameCharacter.clearVariable("bOpenWindow");
        isoGameCharacter.clearVariable("OpenWindowOutcome");
        isoGameCharacter.clearVariable("StopAfterAnimLooped");
        isoGameCharacter.setHideWeaponModel(false);
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if (!isoGameCharacter.getVariableBoolean("bOpenWindow")) {
            return;
        }
        final IsoWindow isoWindow = stateMachineParams.get(OpenWindowState.PARAM_WINDOW);
        if (isoWindow == null) {
            isoGameCharacter.setVariable("bOpenWindow", false);
            return;
        }
        if (animEvent.m_EventName.equalsIgnoreCase("WindowAnimLooped")) {
            if ("start".equalsIgnoreCase(animEvent.m_ParameterValue)) {
                if (!isoWindow.isPermaLocked() && (!isoWindow.Locked || !isoGameCharacter.getCurrentSquare().Is(IsoFlagType.exterior))) {
                    isoGameCharacter.setVariable("OpenWindowOutcome", "success");
                }
                else {
                    isoGameCharacter.setVariable("OpenWindowOutcome", "struggle");
                }
                return;
            }
            if (animEvent.m_ParameterValue.equalsIgnoreCase(isoGameCharacter.getVariableString("StopAfterAnimLooped"))) {
                isoGameCharacter.setVariable("bOpenWindow", false);
            }
        }
        if (animEvent.m_EventName.equalsIgnoreCase("WindowOpenAttempt")) {
            this.onAttemptFinished(isoGameCharacter, isoWindow);
        }
        else if (animEvent.m_EventName.equalsIgnoreCase("WindowOpenSuccess")) {
            this.onSuccess(isoGameCharacter, isoWindow);
        }
        else if (animEvent.m_EventName.equalsIgnoreCase("WindowStruggleSound") && "struggle".equals(isoGameCharacter.getVariableString("OpenWindowOutcome"))) {
            isoGameCharacter.playSound("WindowIsLocked");
        }
    }
    
    @Override
    public boolean isDoingActionThatCanBeCancelled() {
        return true;
    }
    
    private void onAttemptFinished(final IsoGameCharacter isoGameCharacter, final IsoWindow isoWindow) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        this.exert(isoGameCharacter);
        if (isoWindow.isPermaLocked()) {
            if (!isoGameCharacter.getEmitter().isPlaying("WindowIsLocked")) {}
            isoGameCharacter.setVariable("OpenWindowOutcome", "fail");
            isoGameCharacter.setVariable("StopAfterAnimLooped", "fail");
            return;
        }
        int n = 10;
        if (isoGameCharacter.Traits.Burglar.isSet()) {
            n = 5;
        }
        if (isoWindow.Locked && isoGameCharacter.getCurrentSquare().Is(IsoFlagType.exterior)) {
            if (Rand.Next(100) < n) {
                isoGameCharacter.getEmitter().playSound("BreakLockOnWindow", isoWindow);
                isoWindow.setPermaLocked(true);
                isoWindow.syncIsoObject(false, (byte)0, null, null);
                stateMachineParams.put(OpenWindowState.PARAM_WINDOW, null);
                isoGameCharacter.setVariable("OpenWindowOutcome", "fail");
                isoGameCharacter.setVariable("StopAfterAnimLooped", "fail");
                return;
            }
            boolean b = false;
            if (isoGameCharacter.getPerkLevel(PerkFactory.Perks.Strength) > 7 && Rand.Next(100) < 20) {
                b = true;
            }
            else if (isoGameCharacter.getPerkLevel(PerkFactory.Perks.Strength) > 5 && Rand.Next(100) < 10) {
                b = true;
            }
            else if (isoGameCharacter.getPerkLevel(PerkFactory.Perks.Strength) > 3 && Rand.Next(100) < 6) {
                b = true;
            }
            else if (isoGameCharacter.getPerkLevel(PerkFactory.Perks.Strength) > 1 && Rand.Next(100) < 4) {
                b = true;
            }
            else if (Rand.Next(100) <= 1) {
                b = true;
            }
            if (b) {
                isoGameCharacter.setVariable("OpenWindowOutcome", "success");
            }
        }
        else {
            isoGameCharacter.setVariable("OpenWindowOutcome", "success");
        }
    }
    
    private void onSuccess(final IsoGameCharacter isoGameCharacter, final IsoWindow isoWindow) {
        isoGameCharacter.setVariable("StopAfterAnimLooped", "success");
        IsoPlayer.getInstance().ContextPanic = 0.0f;
        if (isoWindow.getObjectIndex() != -1 && !isoWindow.open) {
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
    
    public void setParams(final IsoGameCharacter isoGameCharacter, final IsoWindow value) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        stateMachineParams.clear();
        stateMachineParams.put(OpenWindowState.PARAM_WINDOW, value);
    }
    
    static {
        _instance = new OpenWindowState();
        PARAM_WINDOW = 1;
    }
}
