// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets;

import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import java.util.Iterator;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.IsoMovingObject;
import zombie.network.GameClient;
import zombie.characters.IsoPlayer;
import zombie.network.ServerMap;
import zombie.network.GameServer;
import java.nio.ByteBuffer;
import zombie.iso.IsoGridSquare;
import zombie.debug.DebugLog;
import zombie.iso.IsoWorld;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoDirections;

public abstract class DeadCharacterPacket implements INetworkPacket
{
    protected short id;
    protected float x;
    protected float y;
    protected float z;
    protected float angle;
    protected IsoDirections direction;
    protected byte characterFlags;
    protected IsoGameCharacter killer;
    protected IsoGameCharacter character;
    
    public void set(final IsoGameCharacter character) {
        this.character = character;
        this.id = character.getOnlineID();
        this.killer = character.getAttackedBy();
        this.x = character.getX();
        this.y = character.getY();
        this.z = character.getZ();
        this.angle = character.getAnimAngleRadians();
        this.direction = character.getDir();
        this.characterFlags = (byte)(character.isFallOnFront() ? 1 : 0);
    }
    
    public void process() {
        if (this.character != null) {
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);
            if (this.character.getCurrentSquare() != gridSquare) {
                DebugLog.Multiplayer.warn((Object)String.format("Corpse %s(%d) teleport: position (%f ; %f) => (%f ; %f)", this.character.getClass().getSimpleName(), this.id, this.character.x, this.character.y, this.x, this.y));
                this.character.setX(this.x);
                this.character.setY(this.y);
                this.character.setZ(this.z);
            }
            if (this.character.getAnimAngleRadians() - this.angle > 1.0E-4f) {
                DebugLog.Multiplayer.warn((Object)String.format("Corpse %s(%d) teleport: direction (%f) => (%f)", this.character.getClass().getSimpleName(), this.id, this.character.getAnimAngleRadians(), this.angle));
                if (this.character.hasAnimationPlayer() && this.character.getAnimationPlayer().isReady() && !this.character.getAnimationPlayer().isBoneTransformsNeedFirstFrame()) {
                    this.character.getAnimationPlayer().setAngle(this.angle);
                }
                else {
                    this.character.getForwardDirection().setDirection(this.angle);
                }
            }
            final boolean fallOnFront = (this.characterFlags & 0x1) != 0x0;
            if (fallOnFront != this.character.isFallOnFront()) {
                DebugLog.Multiplayer.warn((Object)String.format("Corpse %s(%d) teleport: pose (%s) => (%s)", this.character.getClass().getSimpleName(), this.id, this.character.isFallOnFront() ? "front" : "back", fallOnFront ? "front" : "back"));
                this.character.setFallOnFront(fallOnFront);
            }
            this.character.setCurrent(gridSquare);
            this.character.dir = this.direction;
            this.character.setAttackedBy(this.killer);
            this.character.becomeCorpse();
        }
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        this.id = byteBuffer.getShort();
        this.x = byteBuffer.getFloat();
        this.y = byteBuffer.getFloat();
        this.z = byteBuffer.getFloat();
        this.angle = byteBuffer.getFloat();
        this.direction = IsoDirections.fromIndex(byteBuffer.get());
        this.characterFlags = byteBuffer.get();
        final byte value = byteBuffer.get();
        if (GameServer.bServer) {
            switch (value) {
                case 0: {
                    this.killer = null;
                    break;
                }
                case 1: {
                    this.killer = ServerMap.instance.ZombieMap.get(byteBuffer.getShort());
                    break;
                }
                case 2: {
                    this.killer = GameServer.IDToPlayerMap.get(byteBuffer.getShort());
                    break;
                }
                default: {
                    new Exception(invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, value)).printStackTrace();
                    break;
                }
            }
        }
        else {
            switch (value) {
                case 0: {
                    this.killer = null;
                    break;
                }
                case 1: {
                    this.killer = (IsoGameCharacter)GameClient.IDToZombieMap.get(byteBuffer.getShort());
                    break;
                }
                case 2: {
                    this.killer = GameClient.IDToPlayerMap.get(byteBuffer.getShort());
                    break;
                }
                default: {
                    new Exception(invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, value)).printStackTrace();
                    break;
                }
            }
        }
    }
    
    public void parseDeadBodyInventory(final ByteBuffer byteBuffer) {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);
        if (gridSquare != null) {
            for (final IsoMovingObject isoMovingObject : gridSquare.getStaticMovingObjects()) {
                if (isoMovingObject instanceof IsoDeadBody && ((IsoDeadBody)isoMovingObject).getOnlineID() == this.id) {
                    isoMovingObject.getContainer().setType(((IsoDeadBody)isoMovingObject).readInventory(byteBuffer));
                    break;
                }
            }
        }
    }
    
    protected void parseCharacterInventory(final ByteBuffer byteBuffer) {
        if (this.character != null) {
            if (this.character.getContainer() != null) {
                this.character.getContainer().clear();
            }
            if (this.character.getInventory() != null) {
                this.character.getInventory().clear();
            }
            if (this.character.getWornItems() != null) {
                this.character.getWornItems().clear();
            }
            if (this.character.getAttachedItems() != null) {
                this.character.getAttachedItems().clear();
            }
            this.character.getInventory().setSourceGrid(this.character.getCurrentSquare());
            this.character.getInventory().setType(this.character.readInventory(byteBuffer));
            this.character.resetModelNextFrame();
        }
    }
    
    public void writeCharacterInventory(final ByteBufferWriter byteBufferWriter) {
        if (this.character != null) {
            this.character.writeInventory(byteBufferWriter.bb);
        }
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.id);
        byteBufferWriter.putFloat(this.x);
        byteBufferWriter.putFloat(this.y);
        byteBufferWriter.putFloat(this.z);
        byteBufferWriter.putFloat(this.angle);
        byteBufferWriter.putByte((byte)this.direction.index());
        byteBufferWriter.putByte(this.characterFlags);
        if (this.killer == null) {
            byteBufferWriter.putByte((byte)0);
        }
        else {
            if (this.killer instanceof IsoZombie) {
                byteBufferWriter.putByte((byte)1);
            }
            else {
                byteBufferWriter.putByte((byte)2);
            }
            byteBufferWriter.putShort(this.killer.getOnlineID());
        }
    }
    
    @Override
    public String getDescription() {
        String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getDeathDescription());
        if (this.character != null) {
            String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;F)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Z)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Z)Ljava/lang/String;, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Z)Ljava/lang/String;, s, this.character.isDead()), this.character.isOnDeathDone()), this.character.isOnKillDone()), this.character.getHealth());
            if (this.character.getBodyDamage() != null) {
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;F)Ljava/lang/String;, s2, this.character.getBodyDamage().getOverallBodyHealth());
            }
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s2, this.character.getPreviousActionContextStateName(), this.character.getCurrentActionContextStateName());
        }
        return s;
    }
    
    public String getDeathDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;SLjava/lang/String;FFFFLjava/lang/String;Z)Ljava/lang/String;, this.getClass().getSimpleName(), this.id, (this.killer == null) ? "Null" : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;S)Ljava/lang/String;, this.killer.getClass().getSimpleName(), this.killer.getOnlineID()), this.x, this.y, this.z, this.angle, this.direction.name(), (this.characterFlags & 0x1) != 0x0);
    }
    
    @Override
    public boolean isConsistent() {
        return this.character != null;
    }
}
