// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.iso.IsoUtils;
import zombie.network.NetworkVariables;
import zombie.iso.IsoWorld;
import zombie.iso.IsoDirections;
import zombie.network.packets.PlayerPacket;
import zombie.iso.IsoGridSquare;
import zombie.network.MPStatisticClient;
import zombie.Lua.LuaManager;
import zombie.debug.DebugOptions;
import zombie.core.Core;

public class NetworkTeleport
{
    public static boolean enable;
    public static boolean enableInstantTeleport;
    private Type teleportType;
    private IsoGameCharacter character;
    private boolean setNewPos;
    private float nx;
    private float ny;
    private byte nz;
    public float ndirection;
    private float tx;
    private float ty;
    private byte tz;
    private long startTime;
    private long duration;
    
    public NetworkTeleport(final IsoGameCharacter character, final Type teleportType, final float nx, final float ny, final byte nz, final float n) {
        this.teleportType = Type.none;
        this.character = null;
        this.setNewPos = false;
        this.nx = 0.0f;
        this.ny = 0.0f;
        this.nz = 0;
        this.tx = 0.0f;
        this.ty = 0.0f;
        this.tz = 0;
        this.character = character;
        this.setNewPos = false;
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        this.teleportType = teleportType;
        this.startTime = System.currentTimeMillis();
        this.duration = (long)(1000.0 * n);
        character.setTeleport(this);
        if (Core.bDebug && character.getNetworkCharacterAI() != null && DebugOptions.instance.MultiplayerShowTeleport.getValue()) {
            character.getNetworkCharacterAI().setTeleportDebug(new NetworkTeleportDebug(character.getOnlineID(), character.x, character.y, character.z, nx, ny, nz, character.getNetworkCharacterAI().predictionType));
        }
    }
    
    public void process(final int n) {
        if (!NetworkTeleport.enable) {
            this.character.setX(this.nx);
            this.character.setY(this.ny);
            this.character.setZ(this.nz);
            this.character.ensureOnTile();
            this.character.setTeleport(null);
            this.character = null;
            return;
        }
        this.character.getCurrentSquare().isCanSee(n);
        final float min = Math.min(1.0f, (System.currentTimeMillis() - this.startTime) / (float)this.duration);
        switch (this.teleportType) {
            case disappearing: {
                if (min < 0.99f) {
                    this.character.setAlpha(n, Math.min(this.character.getAlpha(n), 1.0f - min));
                    break;
                }
                this.stop(n);
                break;
            }
            case teleportation: {
                if (min < 0.5f) {
                    if (this.character.isoPlayer == null || (this.character.isoPlayer != null && this.character.isoPlayer.spottedByPlayer)) {
                        this.character.setAlpha(n, Math.min(this.character.getAlpha(n), 1.0f - min * 2.0f));
                        break;
                    }
                    break;
                }
                else {
                    if (min >= 0.99f) {
                        this.stop(n);
                        break;
                    }
                    if (!this.setNewPos) {
                        this.setNewPos = true;
                        this.character.setX(this.nx);
                        this.character.setY(this.ny);
                        this.character.setZ(this.nz);
                        this.character.ensureOnTile();
                    }
                    if (this.character.isoPlayer == null || (this.character.isoPlayer != null && this.character.isoPlayer.spottedByPlayer)) {
                        this.character.setAlpha(n, Math.min(this.character.getTargetAlpha(n), (min - 0.5f) * 2.0f));
                        break;
                    }
                    break;
                }
                break;
            }
            case materialization: {
                if (min < 0.99f) {
                    this.character.setAlpha(n, Math.min(this.character.getTargetAlpha(n), min));
                    break;
                }
                this.stop(n);
                break;
            }
        }
    }
    
    public void stop(final int n) {
        this.character.setTeleport(null);
        switch (this.teleportType) {
            case disappearing: {
                this.character.setAlpha(n, Math.min(this.character.getAlpha(n), 0.0f));
                break;
            }
        }
        this.character = null;
    }
    
    public static boolean teleport(final IsoGameCharacter isoGameCharacter, final Type type, final float x, final float y, final byte b, final float n) {
        if (!NetworkTeleport.enable) {
            return false;
        }
        if (isoGameCharacter.getCurrentSquare() != null && NetworkTeleport.enableInstantTeleport) {
            boolean b2 = false;
            for (int i = 0; i < 4; ++i) {
                if (isoGameCharacter.getCurrentSquare().isCanSee(i)) {
                    b2 = true;
                    break;
                }
            }
            final IsoGridSquare gridSquare = LuaManager.GlobalObject.getCell().getGridSquare((int)x, (int)y, b);
            if (gridSquare != null) {
                for (int j = 0; j < 4; ++j) {
                    if (gridSquare.isCanSee(j)) {
                        b2 = true;
                        break;
                    }
                }
            }
            if (!b2) {
                isoGameCharacter.setX(x);
                isoGameCharacter.setY(y);
                isoGameCharacter.setZ(b);
                isoGameCharacter.ensureOnTile();
                return false;
            }
        }
        if (!isoGameCharacter.isTeleporting()) {
            if (isoGameCharacter instanceof IsoZombie) {
                MPStatisticClient.getInstance().incrementZombiesTeleports();
            }
            else {
                MPStatisticClient.getInstance().incrementRemotePlayersTeleports();
            }
            new NetworkTeleport(isoGameCharacter, type, x, y, b, n);
            return true;
        }
        return false;
    }
    
    public static boolean teleport(final IsoGameCharacter isoGameCharacter, final PlayerPacket playerPacket, final float n) {
        if (!NetworkTeleport.enable) {
            return false;
        }
        if (LuaManager.GlobalObject.getCell().getGridSquare((int)playerPacket.x, (int)playerPacket.y, playerPacket.z) == null) {
            isoGameCharacter.setX(playerPacket.x);
            isoGameCharacter.setY(playerPacket.y);
            isoGameCharacter.setZ(playerPacket.z);
            isoGameCharacter.realx = playerPacket.realx;
            isoGameCharacter.realy = playerPacket.realy;
            isoGameCharacter.realz = playerPacket.realz;
            isoGameCharacter.realdir = IsoDirections.fromIndex(playerPacket.realdir);
            isoGameCharacter.ensureOnTile();
        }
        if (isoGameCharacter.getCurrentSquare() != null && NetworkTeleport.enableInstantTeleport) {
            boolean b = false;
            for (int i = 0; i < 4; ++i) {
                if (isoGameCharacter.getCurrentSquare().isCanSee(i)) {
                    b = true;
                    break;
                }
            }
            final IsoGridSquare gridSquare = LuaManager.GlobalObject.getCell().getGridSquare((int)playerPacket.x, (int)playerPacket.y, playerPacket.z);
            if (gridSquare != null) {
                for (int j = 0; j < 4; ++j) {
                    if (gridSquare.isCanSee(j)) {
                        b = true;
                        break;
                    }
                }
            }
            if (!b) {
                isoGameCharacter.setX(playerPacket.x);
                isoGameCharacter.setY(playerPacket.y);
                isoGameCharacter.setZ(playerPacket.z);
                isoGameCharacter.ensureOnTile();
                return false;
            }
        }
        if (isoGameCharacter.isTeleporting()) {
            return false;
        }
        if (isoGameCharacter instanceof IsoZombie) {
            MPStatisticClient.getInstance().incrementZombiesTeleports();
        }
        else {
            MPStatisticClient.getInstance().incrementRemotePlayersTeleports();
        }
        if (IsoWorld.instance.CurrentCell.getGridSquare(isoGameCharacter.x, isoGameCharacter.y, isoGameCharacter.z) == null) {
            IsoWorld.instance.CurrentCell.getGridSquare(playerPacket.realx, playerPacket.realy, playerPacket.realz);
            isoGameCharacter.setAlphaAndTarget(0.0f);
            isoGameCharacter.setX(playerPacket.realx);
            isoGameCharacter.setY(playerPacket.realy);
            isoGameCharacter.setZ(playerPacket.realz);
            isoGameCharacter.ensureOnTile();
            final float n2 = 0.5f;
            final NetworkTeleport networkTeleport = new NetworkTeleport(isoGameCharacter, Type.materialization, n2 * playerPacket.x + (1.0f - n2) * playerPacket.realx, n2 * playerPacket.y + (1.0f - n2) * playerPacket.realy, (byte)(n2 * playerPacket.z + (1.0f - n2) * playerPacket.realz), n);
            networkTeleport.ndirection = playerPacket.direction;
            networkTeleport.tx = playerPacket.x;
            networkTeleport.ty = playerPacket.y;
            networkTeleport.tz = playerPacket.z;
            return true;
        }
        final float n3 = 0.5f;
        final NetworkTeleport networkTeleport2 = new NetworkTeleport(isoGameCharacter, Type.teleportation, n3 * playerPacket.x + (1.0f - n3) * playerPacket.realx, n3 * playerPacket.y + (1.0f - n3) * playerPacket.realy, (byte)(n3 * playerPacket.z + (1.0f - n3) * playerPacket.realz), n);
        networkTeleport2.ndirection = playerPacket.direction;
        networkTeleport2.tx = playerPacket.x;
        networkTeleport2.ty = playerPacket.y;
        networkTeleport2.tz = playerPacket.z;
        return true;
    }
    
    public static void update(final IsoGameCharacter isoGameCharacter, final PlayerPacket playerPacket) {
        if (!isoGameCharacter.isTeleporting()) {
            return;
        }
        final NetworkTeleport teleport = isoGameCharacter.getTeleport();
        if (teleport.teleportType != Type.teleportation) {
            return;
        }
        if (Math.min(1.0f, (System.currentTimeMillis() - teleport.startTime) / (float)teleport.duration) < 0.5f) {
            final float n = 0.5f;
            teleport.nx = n * playerPacket.x + (1.0f - n) * playerPacket.realx;
            teleport.ny = n * playerPacket.y + (1.0f - n) * playerPacket.realy;
            teleport.nz = (byte)(n * playerPacket.z + (1.0f - n) * playerPacket.realz);
        }
        teleport.ndirection = playerPacket.direction;
        teleport.tx = playerPacket.x;
        teleport.ty = playerPacket.y;
        teleport.tz = playerPacket.z;
    }
    
    static {
        NetworkTeleport.enable = true;
        NetworkTeleport.enableInstantTeleport = true;
    }
    
    public enum Type
    {
        none, 
        disappearing, 
        teleportation, 
        materialization;
        
        private static /* synthetic */ Type[] $values() {
            return new Type[] { Type.none, Type.disappearing, Type.teleportation, Type.materialization };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public static class NetworkTeleportDebug
    {
        short id;
        float nx;
        float ny;
        float nz;
        float lx;
        float ly;
        float lz;
        NetworkVariables.PredictionTypes type;
        
        public NetworkTeleportDebug(final short id, final float lx, final float ly, final float lz, final float nx, final float ny, final float nz, final NetworkVariables.PredictionTypes type) {
            this.id = id;
            this.nx = nx;
            this.ny = ny;
            this.nz = nz;
            this.lx = lx;
            this.ly = ly;
            this.lz = lz;
            this.type = type;
        }
        
        public float getDistance() {
            return IsoUtils.DistanceTo(this.lx, this.ly, this.lz, this.nx, this.ny, this.nz);
        }
    }
}
