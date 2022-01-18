// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.ai.states.FishingState;
import zombie.iso.Vector2;

public class NetworkPlayerVariables
{
    static Vector2 deferredMovement;
    
    public static int getBooleanVariables(final IsoPlayer isoPlayer) {
        final int n = ((false | isoPlayer.isSneaking()) ? 1 : 0) | (isoPlayer.isOnFire() ? 2 : 0) | (isoPlayer.isAsleep() ? 4 : 0) | (FishingState.instance().equals(isoPlayer.getCurrentState()) ? 8 : 0) | (isoPlayer.isRunning() ? 16 : 0) | (isoPlayer.isSprinting() ? 32 : 0) | (isoPlayer.isAiming() ? 64 : 0) | (isoPlayer.isCharging ? 128 : 0) | (isoPlayer.isChargingLT ? 256 : 0) | (isoPlayer.bDoShove ? 512 : 0);
        isoPlayer.getDeferredMovement(NetworkPlayerVariables.deferredMovement);
        return n | ((NetworkPlayerVariables.deferredMovement.getLength() > 0.0f) ? 1024 : 0) | (isoPlayer.isOnFloor() ? 2048 : 0) | (isoPlayer.isSitOnGround() ? 8192 : 0) | ("fall".equals(isoPlayer.getVariableString("ClimbFenceOutcome")) ? 262144 : 0);
    }
    
    public static void setBooleanVariables(final IsoPlayer isoPlayer, final int n) {
        isoPlayer.setSneaking((n & 0x1) != 0x0);
        if ((n & 0x2) != 0x0) {
            isoPlayer.SetOnFire();
        }
        else {
            isoPlayer.StopBurning();
        }
        isoPlayer.setAsleep((n & 0x4) != 0x0);
        final boolean b = (n & 0x8) != 0x0;
        if (FishingState.instance().equals(isoPlayer.getCurrentState()) && !b) {
            isoPlayer.SetVariable("FishingFinished", "true");
        }
        isoPlayer.setRunning((n & 0x10) != 0x0);
        isoPlayer.setSprinting((n & 0x20) != 0x0);
        isoPlayer.setIsAiming((n & 0x40) != 0x0);
        isoPlayer.isCharging = ((n & 0x80) != 0x0);
        isoPlayer.isChargingLT = ((n & 0x100) != 0x0);
        if (!isoPlayer.bDoShove && (n & 0x200) != 0x0) {
            isoPlayer.setDoShove((n & 0x200) != 0x0);
        }
        isoPlayer.networkAI.moving = ((n & 0x400) != 0x0);
        isoPlayer.setOnFloor((n & 0x800) != 0x0);
        isoPlayer.setSitOnGround((n & 0x2000) != 0x0);
        isoPlayer.networkAI.climbFenceOutcomeFall = ((n & 0x40000) != 0x0);
    }
    
    static {
        NetworkPlayerVariables.deferredMovement = new Vector2();
    }
}
