// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.network.ByteBufferWriter;
import java.nio.ByteBuffer;
import zombie.iso.IsoWorld;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.network.packets.INetworkPacket;

public class NetObject implements INetworkPacket
{
    public final byte objectTypeNone = 0;
    public final byte objectTypeObject = 1;
    private boolean isProcessed;
    private byte objectType;
    private short objectId;
    private int squareX;
    private int squareY;
    private byte squareZ;
    private IsoObject object;
    
    public NetObject() {
        this.isProcessed = false;
        this.objectType = 0;
    }
    
    public void setObject(final IsoObject object) {
        this.object = object;
        this.isProcessed = true;
        if (this.object == null) {
            this.objectType = 0;
            this.objectId = 0;
            return;
        }
        final IsoGridSquare square = this.object.square;
        this.objectType = 1;
        this.objectId = (short)square.getObjects().indexOf(this.object);
        this.squareX = square.getX();
        this.squareY = square.getY();
        this.squareZ = (byte)square.getZ();
    }
    
    public IsoObject getObject() {
        if (!this.isProcessed) {
            if (this.objectType == 0) {
                this.object = null;
            }
            if (this.objectType == 1) {
                final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(this.squareX, this.squareY, this.squareZ);
                if (gridSquare == null) {
                    this.object = null;
                }
                else {
                    this.object = gridSquare.getObjects().get(this.objectId);
                }
            }
            this.isProcessed = true;
        }
        return this.object;
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.objectType = byteBuffer.get();
        if (this.objectType == 1) {
            this.objectId = byteBuffer.getShort();
            this.squareX = byteBuffer.getInt();
            this.squareY = byteBuffer.getInt();
            this.squareZ = byteBuffer.get();
        }
        this.isProcessed = false;
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putByte(this.objectType);
        if (this.objectType == 1) {
            byteBufferWriter.putShort(this.objectId);
            byteBufferWriter.putInt(this.squareX);
            byteBufferWriter.putInt(this.squareY);
            byteBufferWriter.putByte(this.squareZ);
        }
    }
    
    @Override
    public int getPacketSizeBytes() {
        if (this.objectType == 1) {
            return 12;
        }
        return 1;
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
                s = "NetObject";
                break;
            }
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;BSIIB)Ljava/lang/String;, s, this.objectType, this.objectId, this.squareX, this.squareY, this.squareZ);
    }
}
