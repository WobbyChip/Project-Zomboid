// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.iso.IsoWorld;
import zombie.vehicles.VehicleManager;
import zombie.network.ServerMap;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.iso.IsoGridSquare;
import zombie.vehicles.BaseVehicle;
import zombie.characters.IsoZombie;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoMovingObject;
import zombie.network.packets.INetworkPacket;

public class MovingObject implements INetworkPacket
{
    public final byte objectTypeNone = 0;
    public final byte objectTypePlayer = 1;
    public final byte objectTypeZombie = 2;
    public final byte objectTypeVehicle = 3;
    public final byte objectTypeObject = 4;
    private boolean isProcessed;
    private byte objectType;
    private short objectId;
    private int squareX;
    private int squareY;
    private byte squareZ;
    private IsoMovingObject object;
    
    public MovingObject() {
        this.isProcessed = false;
        this.objectType = 0;
    }
    
    public void setMovingObject(final IsoMovingObject object) {
        this.object = object;
        this.isProcessed = true;
        if (this.object == null) {
            this.objectType = 0;
            this.objectId = 0;
            return;
        }
        if (this.object instanceof IsoPlayer) {
            this.objectType = 1;
            this.objectId = ((IsoPlayer)this.object).getOnlineID();
            return;
        }
        if (this.object instanceof IsoZombie) {
            this.objectType = 2;
            this.objectId = ((IsoZombie)this.object).getOnlineID();
            return;
        }
        if (this.object instanceof BaseVehicle) {
            this.objectType = 3;
            this.objectId = ((BaseVehicle)this.object).VehicleID;
            return;
        }
        final IsoGridSquare currentSquare = this.object.getCurrentSquare();
        this.objectType = 4;
        this.objectId = (short)currentSquare.getMovingObjects().indexOf(this.object);
        this.squareX = currentSquare.getX();
        this.squareY = currentSquare.getY();
        this.squareZ = (byte)currentSquare.getZ();
    }
    
    public IsoMovingObject getMovingObject() {
        if (!this.isProcessed) {
            if (this.objectType == 0) {
                this.object = null;
            }
            if (this.objectType == 1) {
                if (GameServer.bServer) {
                    this.object = GameServer.IDToPlayerMap.get(this.objectId);
                }
                else if (GameClient.bClient) {
                    this.object = GameClient.IDToPlayerMap.get(this.objectId);
                }
            }
            if (this.objectType == 2) {
                if (GameServer.bServer) {
                    this.object = ServerMap.instance.ZombieMap.get(this.objectId);
                }
                else if (GameClient.bClient) {
                    this.object = (IsoMovingObject)GameClient.IDToZombieMap.get(this.objectId);
                }
            }
            if (this.objectType == 3) {
                this.object = VehicleManager.instance.getVehicleByID(this.objectId);
            }
            if (this.objectType == 4) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(this.squareX, this.squareY, this.squareZ);
                if (gridSquare == null) {
                    this.object = null;
                }
                else {
                    this.object = gridSquare.getMovingObjects().get(this.objectId);
                }
            }
            this.isProcessed = true;
        }
        return this.object;
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.objectType = byteBuffer.get();
        this.objectId = byteBuffer.getShort();
        if (this.objectType == 4) {
            this.squareX = byteBuffer.getInt();
            this.squareY = byteBuffer.getInt();
            this.squareZ = byteBuffer.get();
        }
        this.isProcessed = false;
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putByte(this.objectType);
        byteBufferWriter.putShort(this.objectId);
        if (this.objectType == 4) {
            byteBufferWriter.putInt(this.squareX);
            byteBufferWriter.putInt(this.squareY);
            byteBufferWriter.putByte(this.squareZ);
        }
    }
    
    @Override
    public int getPacketSizeBytes() {
        if (this.objectType == 4) {
            return 12;
        }
        return 3;
    }
    
    @Override
    public String getDescription() {
        String s = "";
        switch (this.objectType) {
            case 0: {
                s = "None";
                break;
            }
            case 1: {
                s = "Player";
                break;
            }
            case 2: {
                s = "Zombie";
                break;
            }
            case 3: {
                s = "Vehicle";
                break;
            }
            case 4: {
                s = "NetObject";
                break;
            }
        }
        if (this.objectType == 4) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;BSIIB)Ljava/lang/String;, s, this.objectType, this.objectId, this.squareX, this.squareY, this.squareZ);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;BS)Ljava/lang/String;, s, this.objectType, this.objectId);
    }
}
