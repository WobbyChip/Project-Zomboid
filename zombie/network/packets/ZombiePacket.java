// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets;

import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.network.NetworkVariables;

public class ZombiePacket implements INetworkPacket
{
    private static final int PACKET_SIZE_BYTES = 55;
    public short id;
    public float x;
    public float y;
    public byte z;
    public int descriptorID;
    public NetworkVariables.PredictionTypes moveType;
    public short booleanVariables;
    public short target;
    public int timeSinceSeenFlesh;
    public int smParamTargetAngle;
    public short speedMod;
    public NetworkVariables.WalkType walkType;
    public float realX;
    public float realY;
    public byte realZ;
    public short realHealth;
    public NetworkVariables.ZombieState realState;
    public byte pfbType;
    public short pfbTarget;
    public float pfbTargetX;
    public float pfbTargetY;
    public byte pfbTargetZ;
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.id = byteBuffer.getShort();
        this.x = byteBuffer.getFloat();
        this.y = byteBuffer.getFloat();
        this.z = byteBuffer.get();
        this.descriptorID = byteBuffer.getInt();
        this.moveType = NetworkVariables.PredictionTypes.fromByte(byteBuffer.get());
        this.booleanVariables = byteBuffer.getShort();
        this.target = byteBuffer.getShort();
        this.timeSinceSeenFlesh = byteBuffer.getInt();
        this.smParamTargetAngle = byteBuffer.getInt();
        this.speedMod = byteBuffer.getShort();
        this.walkType = NetworkVariables.WalkType.fromByte(byteBuffer.get());
        this.realX = byteBuffer.getFloat();
        this.realY = byteBuffer.getFloat();
        this.realZ = byteBuffer.get();
        this.realHealth = byteBuffer.getShort();
        this.realState = NetworkVariables.ZombieState.fromByte(byteBuffer.get());
        this.pfbType = byteBuffer.get();
        if (this.pfbType == 1) {
            this.pfbTarget = byteBuffer.getShort();
        }
        else if (this.pfbType > 1) {
            this.pfbTargetX = byteBuffer.getFloat();
            this.pfbTargetY = byteBuffer.getFloat();
            this.pfbTargetZ = byteBuffer.get();
        }
    }
    
    public void write(final ByteBuffer byteBuffer) {
        byteBuffer.putShort(this.id);
        byteBuffer.putFloat(this.x);
        byteBuffer.putFloat(this.y);
        byteBuffer.put(this.z);
        byteBuffer.putInt(this.descriptorID);
        byteBuffer.put((byte)this.moveType.ordinal());
        byteBuffer.putShort(this.booleanVariables);
        byteBuffer.putShort(this.target);
        byteBuffer.putInt(this.timeSinceSeenFlesh);
        byteBuffer.putInt(this.smParamTargetAngle);
        byteBuffer.putShort(this.speedMod);
        byteBuffer.put((byte)this.walkType.ordinal());
        byteBuffer.putFloat(this.realX);
        byteBuffer.putFloat(this.realY);
        byteBuffer.put(this.realZ);
        byteBuffer.putShort(this.realHealth);
        byteBuffer.put((byte)this.realState.ordinal());
        byteBuffer.put(this.pfbType);
        if (this.pfbType == 1) {
            byteBuffer.putShort(this.pfbTarget);
        }
        else if (this.pfbType > 1) {
            byteBuffer.putFloat(this.pfbTargetX);
            byteBuffer.putFloat(this.pfbTargetY);
            byteBuffer.put(this.pfbTargetZ);
        }
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        this.write(byteBufferWriter.bb);
    }
    
    @Override
    public int getPacketSizeBytes() {
        return 55;
    }
    
    public void copy(final ZombiePacket zombiePacket) {
        this.id = zombiePacket.id;
        this.x = zombiePacket.x;
        this.y = zombiePacket.y;
        this.z = zombiePacket.z;
        this.descriptorID = zombiePacket.descriptorID;
        this.moveType = zombiePacket.moveType;
        this.booleanVariables = zombiePacket.booleanVariables;
        this.target = zombiePacket.target;
        this.timeSinceSeenFlesh = zombiePacket.timeSinceSeenFlesh;
        this.smParamTargetAngle = zombiePacket.smParamTargetAngle;
        this.speedMod = zombiePacket.speedMod;
        this.walkType = zombiePacket.walkType;
        this.realX = zombiePacket.realX;
        this.realY = zombiePacket.realY;
        this.realZ = zombiePacket.realZ;
        this.realHealth = zombiePacket.realHealth;
        this.realState = zombiePacket.realState;
        this.pfbType = zombiePacket.pfbType;
        this.pfbTarget = zombiePacket.pfbTarget;
        this.pfbTargetX = zombiePacket.pfbTargetX;
        this.pfbTargetY = zombiePacket.pfbTargetY;
        this.pfbTargetZ = zombiePacket.pfbTargetZ;
    }
    
    public void set(final IsoZombie isoZombie) {
        this.id = isoZombie.OnlineID;
        this.descriptorID = isoZombie.getPersistentOutfitID();
        isoZombie.networkAI.set(this);
        isoZombie.networkAI.mindSync.set(this);
        isoZombie.thumpSent = true;
    }
}
