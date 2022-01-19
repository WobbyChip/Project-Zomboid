// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.network.packets.INetworkPacket;

public abstract class HitCharacterPacket implements INetworkPacket
{
    private final HitType hitType;
    
    public HitCharacterPacket(final HitType hitType) {
        this.hitType = hitType;
    }
    
    public static HitCharacterPacket process(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        if (value > HitType.Min.ordinal() && value < HitType.Max.ordinal()) {
            INetworkPacket networkPacket = null;
            switch (HitType.values()[value]) {
                case PlayerHitSquare: {
                    networkPacket = new PlayerHitSquarePacket();
                    break;
                }
                case PlayerHitVehicle: {
                    networkPacket = new PlayerHitVehiclePacket();
                    break;
                }
                case PlayerHitZombie: {
                    networkPacket = new PlayerHitZombiePacket();
                    break;
                }
                case PlayerHitPlayer: {
                    networkPacket = new PlayerHitPlayerPacket();
                    break;
                }
                case ZombieHitPlayer: {
                    networkPacket = new ZombieHitPlayerPacket();
                    break;
                }
                case VehicleHitZombie: {
                    networkPacket = new VehicleHitZombiePacket();
                    break;
                }
                case VehicleHitPlayer: {
                    networkPacket = new VehicleHitPlayerPacket();
                    break;
                }
                default: {
                    networkPacket = null;
                    break;
                }
            }
            return (HitCharacterPacket)networkPacket;
        }
        return null;
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putByte((byte)this.hitType.ordinal());
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), this.hitType.name());
    }
    
    public String getHitDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), this.hitType.name());
    }
    
    public void tryProcess() {
        if (GameClient.bClient && (HitType.VehicleHitZombie.equals(this.hitType) || HitType.VehicleHitPlayer.equals(this.hitType))) {
            this.postpone();
        }
        else {
            this.tryProcessInternal();
        }
    }
    
    public void tryProcessInternal() {
        if (this.isConsistent()) {
            this.preProcess();
            this.process();
            this.postProcess();
            if (GameClient.bClient) {
                this.attack();
            }
            this.react();
        }
        else {
            DebugLog.Multiplayer.warn((Object)"HitCharacter: check error");
        }
    }
    
    public abstract boolean isRelevant(final UdpConnection p0);
    
    protected abstract void attack();
    
    protected abstract void react();
    
    protected void preProcess() {
    }
    
    protected void process() {
    }
    
    protected void postProcess() {
    }
    
    protected void postpone() {
    }
    
    public enum HitType
    {
        Min, 
        PlayerHitSquare, 
        PlayerHitVehicle, 
        PlayerHitZombie, 
        PlayerHitPlayer, 
        ZombieHitPlayer, 
        VehicleHitZombie, 
        VehicleHitPlayer, 
        Max;
        
        private static /* synthetic */ HitType[] $values() {
            return new HitType[] { HitType.Min, HitType.PlayerHitSquare, HitType.PlayerHitVehicle, HitType.PlayerHitZombie, HitType.PlayerHitPlayer, HitType.ZombieHitPlayer, HitType.VehicleHitZombie, HitType.VehicleHitPlayer, HitType.Max };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
