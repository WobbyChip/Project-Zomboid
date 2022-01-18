// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ai.states;

import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import java.util.HashMap;
import zombie.iso.IsoObject;
import zombie.vehicles.BaseVehicle;
import zombie.iso.IsoDirections;
import zombie.characters.IsoPlayer;
import zombie.vehicles.VehicleWindow;
import zombie.iso.objects.IsoWindow;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.inventory.types.HandWeapon;
import zombie.characters.IsoGameCharacter;
import zombie.ai.State;

public final class SmashWindowState extends State
{
    private static final SmashWindowState _instance;
    
    public static SmashWindowState instance() {
        return SmashWindowState._instance;
    }
    
    @Override
    public void enter(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(true);
        isoGameCharacter.setVariable("bSmashWindow", true);
        final HandWeapon handWeapon = Type.tryCastTo(isoGameCharacter.getPrimaryHandItem(), HandWeapon.class);
        if (handWeapon != null && handWeapon.isRanged()) {
            isoGameCharacter.playSound("AttackShove");
        }
        else if (handWeapon != null && !StringUtils.isNullOrWhitespace(handWeapon.getSwingSound())) {
            isoGameCharacter.playSound(handWeapon.getSwingSound());
        }
    }
    
    @Override
    public void execute(final IsoGameCharacter isoGameCharacter) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if (!(stateMachineParams.get(0) instanceof IsoWindow) && !(stateMachineParams.get(0) instanceof VehicleWindow)) {
            isoGameCharacter.setVariable("bSmashWindow", false);
            return;
        }
        final IsoPlayer isoPlayer = Type.tryCastTo(isoGameCharacter, IsoPlayer.class);
        if (isoPlayer.pressedMovement(false) || isoPlayer.pressedCancelAction()) {
            isoGameCharacter.setVariable("bSmashWindow", false);
            return;
        }
        if (stateMachineParams.get(0) instanceof IsoWindow) {
            final IsoWindow isoWindow = stateMachineParams.get(0);
            if (isoWindow.getObjectIndex() == -1 || (isoWindow.isDestroyed() && !"true".equals(isoGameCharacter.getVariableString("OwnerSmashedIt")))) {
                isoGameCharacter.setVariable("bSmashWindow", false);
                return;
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
        }
        else if (stateMachineParams.get(0) instanceof VehicleWindow) {
            final VehicleWindow vehicleWindow = stateMachineParams.get(0);
            isoGameCharacter.faceThisObject((IsoObject)stateMachineParams.get(1));
            if (vehicleWindow.isDestroyed() && !"true".equals(isoGameCharacter.getVariableString("OwnerSmashedIt"))) {
                isoGameCharacter.setVariable("bSmashWindow", false);
            }
        }
    }
    
    @Override
    public void exit(final IsoGameCharacter isoGameCharacter) {
        isoGameCharacter.setIgnoreMovement(false);
        isoGameCharacter.clearVariable("bSmashWindow");
        isoGameCharacter.clearVariable("OwnerSmashedIt");
    }
    
    @Override
    public void animEvent(final IsoGameCharacter isoGameCharacter, final AnimEvent animEvent) {
        final HashMap<Object, Object> stateMachineParams = isoGameCharacter.getStateMachineParams(this);
        if (stateMachineParams.get(0) instanceof IsoWindow) {
            final IsoWindow isoWindow = stateMachineParams.get(0);
            if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck")) {
                isoGameCharacter.setVariable("OwnerSmashedIt", true);
                IsoPlayer.getInstance().ContextPanic = 0.0f;
                isoWindow.WeaponHit(isoGameCharacter, null);
                if (!(isoGameCharacter.getPrimaryHandItem() instanceof HandWeapon)) {
                    if (!(isoGameCharacter.getSecondaryHandItem() instanceof HandWeapon)) {
                        isoGameCharacter.getBodyDamage().setScratchedWindow();
                    }
                }
            }
            else if (animEvent.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {
                isoGameCharacter.setVariable("bSmashWindow", false);
                if (Boolean.TRUE == stateMachineParams.get(3)) {
                    isoGameCharacter.climbThroughWindow(isoWindow);
                }
            }
        }
        else if (stateMachineParams.get(0) instanceof VehicleWindow) {
            final VehicleWindow vehicleWindow = stateMachineParams.get(0);
            if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck")) {
                isoGameCharacter.setVariable("OwnerSmashedIt", true);
                IsoPlayer.getInstance().ContextPanic = 0.0f;
                vehicleWindow.hit(isoGameCharacter);
                if (!(isoGameCharacter.getPrimaryHandItem() instanceof HandWeapon)) {
                    if (!(isoGameCharacter.getSecondaryHandItem() instanceof HandWeapon)) {
                        isoGameCharacter.getBodyDamage().setScratchedWindow();
                    }
                }
            }
            else if (animEvent.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {
                isoGameCharacter.setVariable("bSmashWindow", false);
            }
        }
    }
    
    @Override
    public boolean isDoingActionThatCanBeCancelled() {
        return true;
    }
    
    static {
        _instance = new SmashWindowState();
    }
}
