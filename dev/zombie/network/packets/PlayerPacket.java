// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets;

import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.network.NetworkVariables;

public class PlayerPacket implements INetworkPacket
{
    public static final int PACKET_SIZE_BYTES = 43;
    public short id;
    public float x;
    public float y;
    public byte z;
    public float direction;
    public boolean usePathFinder;
    public NetworkVariables.PredictionTypes moveType;
    public short VehicleID;
    public short VehicleSeat;
    public int booleanVariables;
    public byte footstepSoundRadius;
    public byte bleedingLevel;
    public float realx;
    public float realy;
    public byte realz;
    public byte realdir;
    public int realt;
    public float collidePointX;
    public float collidePointY;
    public PlayerVariables variables;
    
    public PlayerPacket() {
        this.variables = new PlayerVariables();
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.id = byteBuffer.getShort();
        this.x = byteBuffer.getFloat();
        this.y = byteBuffer.getFloat();
        this.z = byteBuffer.get();
        this.direction = byteBuffer.getFloat();
        this.usePathFinder = (byteBuffer.get() == 1);
        this.moveType = NetworkVariables.PredictionTypes.fromByte(byteBuffer.get());
        this.VehicleID = byteBuffer.getShort();
        this.VehicleSeat = byteBuffer.getShort();
        this.booleanVariables = byteBuffer.getInt();
        this.footstepSoundRadius = byteBuffer.get();
        this.bleedingLevel = byteBuffer.get();
        this.realx = byteBuffer.getFloat();
        this.realy = byteBuffer.getFloat();
        this.realz = byteBuffer.get();
        this.realdir = byteBuffer.get();
        this.realt = byteBuffer.getInt();
        this.collidePointX = byteBuffer.getFloat();
        this.collidePointY = byteBuffer.getFloat();
        this.variables.parse(byteBuffer);
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.id);
        byteBufferWriter.putFloat(this.x);
        byteBufferWriter.putFloat(this.y);
        byteBufferWriter.putByte(this.z);
        byteBufferWriter.putFloat(this.direction);
        byteBufferWriter.putBoolean(this.usePathFinder);
        byteBufferWriter.putByte((byte)this.moveType.ordinal());
        byteBufferWriter.putShort(this.VehicleID);
        byteBufferWriter.putShort(this.VehicleSeat);
        byteBufferWriter.putInt(this.booleanVariables);
        byteBufferWriter.putByte(this.footstepSoundRadius);
        byteBufferWriter.putByte(this.bleedingLevel);
        byteBufferWriter.putFloat(this.realx);
        byteBufferWriter.putFloat(this.realy);
        byteBufferWriter.putByte(this.realz);
        byteBufferWriter.putByte(this.realdir);
        byteBufferWriter.putInt(this.realt);
        byteBufferWriter.putFloat(this.collidePointX);
        byteBufferWriter.putFloat(this.collidePointY);
        this.variables.write(byteBufferWriter);
    }
    
    @Override
    public int getPacketSizeBytes() {
        return 43;
    }
    
    public boolean set(final IsoPlayer isoPlayer) {
        this.id = isoPlayer.OnlineID;
        this.bleedingLevel = isoPlayer.bleedingLevel;
        this.variables.set(isoPlayer);
        return isoPlayer.networkAI.set(this);
    }
    
    public void copy(final PlayerPacket playerPacket) {
        this.id = playerPacket.id;
        this.x = playerPacket.x;
        this.y = playerPacket.y;
        this.z = playerPacket.z;
        this.direction = playerPacket.direction;
        this.usePathFinder = playerPacket.usePathFinder;
        this.moveType = playerPacket.moveType;
        this.VehicleID = playerPacket.VehicleID;
        this.VehicleSeat = playerPacket.VehicleSeat;
        this.booleanVariables = playerPacket.booleanVariables;
        this.footstepSoundRadius = playerPacket.footstepSoundRadius;
        this.bleedingLevel = playerPacket.bleedingLevel;
        this.realx = playerPacket.realx;
        this.realy = playerPacket.realy;
        this.realz = playerPacket.realz;
        this.realdir = playerPacket.realdir;
        this.realt = playerPacket.realt;
        this.collidePointX = playerPacket.collidePointX;
        this.collidePointY = playerPacket.collidePointY;
        this.variables.copy(playerPacket.variables);
    }
    
    public static class l_receive
    {
        public static PlayerPacket playerPacket;
        
        static {
            l_receive.playerPacket = new PlayerPacket();
        }
    }
    
    public static class l_send
    {
        public static PlayerPacket playerPacket;
        
        static {
            l_send.playerPacket = new PlayerPacket();
        }
    }
}
