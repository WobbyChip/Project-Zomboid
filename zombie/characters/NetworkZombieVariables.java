// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.network.GameServer;
import zombie.network.GameClient;
import zombie.ai.State;
import zombie.ai.states.ZombieTurnAlerted;
import zombie.core.skinnedmodel.advancedanimation.IAnimatable;

public class NetworkZombieVariables
{
    public static int getInt(final IsoZombie isoZombie, final short n) {
        switch (n) {
            case 0: {
                return (int)(isoZombie.Health * 1000.0f);
            }
            case 2: {
                return (int)(isoZombie.speedMod * 1000.0f);
            }
            case 1: {
                if (isoZombie.target == null) {
                    return -1;
                }
                return ((IAnimatable)isoZombie.target).getOnlineID();
            }
            case 3: {
                return (int)isoZombie.TimeSinceSeenFlesh;
            }
            case 4: {
                final Float n2 = isoZombie.getStateMachineParams(ZombieTurnAlerted.instance()).get(ZombieTurnAlerted.PARAM_TARGET_ANGLE);
                if (n2 == null) {
                    return 0;
                }
                return n2.intValue();
            }
            default: {
                return 0;
            }
        }
    }
    
    public static void setInt(final IsoZombie isoZombie, final short n, final int n2) {
        switch (n) {
            case 0: {
                isoZombie.Health = n2 / 1000.0f;
                break;
            }
            case 2: {
                isoZombie.speedMod = n2 / 1000.0f;
                break;
            }
            case 1: {
                if (n2 == -1) {
                    isoZombie.setTargetSeenTime(0.0f);
                    isoZombie.target = null;
                    break;
                }
                IsoPlayer target = GameClient.IDToPlayerMap.get((short)n2);
                if (GameServer.bServer) {
                    target = GameServer.IDToPlayerMap.get((short)n2);
                }
                if (target != isoZombie.target) {
                    isoZombie.setTargetSeenTime(0.0f);
                    isoZombie.target = target;
                }
                break;
            }
            case 3: {
                isoZombie.TimeSinceSeenFlesh = (float)n2;
                break;
            }
            case 4: {
                isoZombie.getStateMachineParams(ZombieTurnAlerted.instance()).put(ZombieTurnAlerted.PARAM_TARGET_ANGLE, n2);
                break;
            }
        }
    }
    
    public static short getBooleanVariables(final IsoZombie isoZombie) {
        return (short)((short)((short)((short)((short)((short)((short)((short)((short)((false | isoZombie.isFakeDead()) ? 1 : 0) | (isoZombie.bLunger ? 2 : 0)) | (isoZombie.bRunning ? 4 : 0)) | (isoZombie.isCrawling() ? 8 : 0)) | (isoZombie.isSitAgainstWall() ? 16 : 0)) | (isoZombie.isReanimatedPlayer() ? 32 : 0)) | (isoZombie.isOnFire() ? 64 : 0)) | (isoZombie.isUseless() ? 128 : 0)) | (isoZombie.isOnFloor() ? 256 : 0));
    }
    
    public static void setBooleanVariables(final IsoZombie isoZombie, final short n) {
        isoZombie.setFakeDead((n & 0x1) != 0x0);
        isoZombie.bLunger = ((n & 0x2) != 0x0);
        isoZombie.bRunning = ((n & 0x4) != 0x0);
        isoZombie.setCrawler((n & 0x8) != 0x0);
        isoZombie.setSitAgainstWall((n & 0x10) != 0x0);
        isoZombie.setReanimatedPlayer((n & 0x20) != 0x0);
        if ((n & 0x40) != 0x0) {
            isoZombie.SetOnFire();
        }
        else {
            isoZombie.StopBurning();
        }
        isoZombie.setUseless((n & 0x80) != 0x0);
        if (isoZombie.isReanimatedPlayer()) {
            isoZombie.setOnFloor((n & 0x100) != 0x0);
        }
    }
    
    public static class VariablesInt
    {
        public static final short health = 0;
        public static final short target = 1;
        public static final short speedMod = 2;
        public static final short timeSinceSeenFlesh = 3;
        public static final short smParamTargetAngle = 4;
        public static final short MAX = 5;
    }
}
