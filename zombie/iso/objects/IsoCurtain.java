// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.core.properties.PropertyContainer;
import zombie.util.list.PZArrayList;
import zombie.util.Type;
import zombie.iso.objects.interfaces.BarricadeAble;
import java.util.Iterator;
import zombie.Lua.LuaEventManager;
import zombie.network.PacketTypes;
import zombie.core.network.ByteBufferWriter;
import zombie.iso.IsoCamera;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.core.raknet.UdpConnection;
import java.io.IOException;
import zombie.network.GameClient;
import zombie.SystemDisabler;
import java.nio.ByteBuffer;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.Vector2;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.GameTime;
import zombie.iso.LosUtil;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import zombie.characters.IsoGameCharacter;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoObject;

public class IsoCurtain extends IsoObject
{
    public boolean Barricaded;
    public Integer BarricideMaxStrength;
    public Integer BarricideStrength;
    public Integer Health;
    public boolean Locked;
    public Integer MaxHealth;
    public Integer PushedMaxStrength;
    public Integer PushedStrength;
    IsoSprite closedSprite;
    public boolean north;
    public boolean open;
    IsoSprite openSprite;
    private boolean destroyed;
    
    public void removeSheet(final IsoGameCharacter isoGameCharacter) {
        this.square.transmitRemoveItemFromSquare(this);
        if (GameServer.bServer) {
            isoGameCharacter.sendObjectChange("addItemOfType", new Object[] { "type", "Base.Sheet" });
        }
        else {
            isoGameCharacter.getInventory().AddItem("Base.Sheet");
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            LosUtil.cachecleared[i] = true;
        }
        GameTime.instance.lightSourceUpdate = 100.0f;
        IsoGridSquare.setRecalcLightTime(-1);
    }
    
    public IsoCurtain(final IsoCell isoCell, final IsoGridSquare square, final IsoSprite isoSprite, final boolean north, final boolean b) {
        this.Barricaded = false;
        this.BarricideMaxStrength = 0;
        this.BarricideStrength = 0;
        this.Health = 1000;
        this.Locked = false;
        this.MaxHealth = 1000;
        this.PushedMaxStrength = 0;
        this.PushedStrength = 0;
        this.north = false;
        this.open = false;
        this.destroyed = false;
        this.OutlineOnMouseover = true;
        final Integer value = 2500;
        this.PushedStrength = value;
        this.PushedMaxStrength = value;
        if (b) {
            this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, isoSprite, 4);
            this.closedSprite = isoSprite;
        }
        else {
            this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, isoSprite, -4);
            this.openSprite = isoSprite;
        }
        this.open = true;
        this.sprite = this.openSprite;
        this.square = square;
        this.north = north;
        this.DirtySlice();
    }
    
    public IsoCurtain(final IsoCell isoCell, final IsoGridSquare square, final String s, final boolean north) {
        this.Barricaded = false;
        this.BarricideMaxStrength = 0;
        this.BarricideStrength = 0;
        this.Health = 1000;
        this.Locked = false;
        this.MaxHealth = 1000;
        this.PushedMaxStrength = 0;
        this.PushedStrength = 0;
        this.north = false;
        this.open = false;
        this.destroyed = false;
        this.OutlineOnMouseover = true;
        final Integer value = 2500;
        this.PushedStrength = value;
        this.PushedMaxStrength = value;
        this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, s, -4);
        this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, s, 0);
        this.open = true;
        this.sprite = this.openSprite;
        this.square = square;
        this.north = north;
        this.DirtySlice();
    }
    
    public IsoCurtain(final IsoCell isoCell) {
        super(isoCell);
        this.Barricaded = false;
        this.BarricideMaxStrength = 0;
        this.BarricideStrength = 0;
        this.Health = 1000;
        this.Locked = false;
        this.MaxHealth = 1000;
        this.PushedMaxStrength = 0;
        this.PushedStrength = 0;
        this.north = false;
        this.open = false;
        this.destroyed = false;
    }
    
    @Override
    public String getObjectName() {
        return "Curtain";
    }
    
    @Override
    public Vector2 getFacingPosition(final Vector2 vector2) {
        if (this.square == null) {
            return vector2.set(0.0f, 0.0f);
        }
        if (this.getType() == IsoObjectType.curtainS) {
            return vector2.set(this.getX() + 0.5f, this.getY() + 1.0f);
        }
        if (this.getType() == IsoObjectType.curtainE) {
            return vector2.set(this.getX() + 1.0f, this.getY() + 0.5f);
        }
        if (this.north) {
            return vector2.set(this.getX() + 0.5f, this.getY());
        }
        return vector2.set(this.getX(), this.getY() + 0.5f);
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        super.load(byteBuffer, n, b);
        this.open = (byteBuffer.get() == 1);
        this.north = (byteBuffer.get() == 1);
        this.Health = byteBuffer.getInt();
        this.BarricideStrength = byteBuffer.getInt();
        if (this.open) {
            this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
            this.openSprite = this.sprite;
        }
        else {
            this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
            this.closedSprite = this.sprite;
        }
        if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
            GameClient.instance.objectSyncReq.putRequestLoad(this.square);
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.put((byte)(this.open ? 1 : 0));
        byteBuffer.put((byte)(this.north ? 1 : 0));
        byteBuffer.putInt(this.Health);
        byteBuffer.putInt(this.BarricideStrength);
        if (this.open) {
            byteBuffer.putInt(this.closedSprite.ID);
        }
        else {
            byteBuffer.putInt(this.openSprite.ID);
        }
    }
    
    public boolean getNorth() {
        return this.north;
    }
    
    public boolean IsOpen() {
        return this.open;
    }
    
    @Override
    public boolean onMouseLeftClick(final int n, final int n2) {
        return false;
    }
    
    public boolean canInteractWith(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter == null || isoGameCharacter.getCurrentSquare() == null) {
            return false;
        }
        final IsoGridSquare currentSquare = isoGameCharacter.getCurrentSquare();
        return (this.isAdjacentToSquare(currentSquare) || currentSquare == this.getOppositeSquare()) && !this.getSquare().isBlockedTo(currentSquare);
    }
    
    public IsoGridSquare getOppositeSquare() {
        if (this.getType() == IsoObjectType.curtainN) {
            return this.getCell().getGridSquare(this.getX(), this.getY() - 1.0f, this.getZ());
        }
        if (this.getType() == IsoObjectType.curtainS) {
            return this.getCell().getGridSquare(this.getX(), this.getY() + 1.0f, this.getZ());
        }
        if (this.getType() == IsoObjectType.curtainW) {
            return this.getCell().getGridSquare(this.getX() - 1.0f, this.getY(), this.getZ());
        }
        if (this.getType() == IsoObjectType.curtainE) {
            return this.getCell().getGridSquare(this.getX() + 1.0f, this.getY(), this.getZ());
        }
        return null;
    }
    
    public boolean isAdjacentToSquare(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        if (isoGridSquare == null || isoGridSquare2 == null) {
            return false;
        }
        if (this.getType() == IsoObjectType.curtainN || this.getType() == IsoObjectType.curtainS) {
            return isoGridSquare.y == isoGridSquare2.y && Math.abs(isoGridSquare.x - isoGridSquare2.x) <= 1;
        }
        return isoGridSquare.x == isoGridSquare2.x && Math.abs(isoGridSquare.y - isoGridSquare2.y) <= 1;
    }
    
    public boolean isAdjacentToSquare(final IsoGridSquare isoGridSquare) {
        return this.isAdjacentToSquare(this.getSquare(), isoGridSquare);
    }
    
    @Override
    public VisionResult TestVision(final IsoGridSquare isoGridSquare, final IsoGridSquare isoGridSquare2) {
        if (isoGridSquare2.getZ() != isoGridSquare.getZ()) {
            return VisionResult.NoEffect;
        }
        if ((isoGridSquare == this.square && (this.getType() == IsoObjectType.curtainW || this.getType() == IsoObjectType.curtainN)) || (isoGridSquare != this.square && (this.getType() == IsoObjectType.curtainE || this.getType() == IsoObjectType.curtainS))) {
            if (this.north && isoGridSquare2.getY() < isoGridSquare.getY() && !this.open) {
                return VisionResult.Blocked;
            }
            if (!this.north && isoGridSquare2.getX() < isoGridSquare.getX() && !this.open) {
                return VisionResult.Blocked;
            }
        }
        else {
            if (this.north && isoGridSquare2.getY() > isoGridSquare.getY() && !this.open) {
                return VisionResult.Blocked;
            }
            if (!this.north && isoGridSquare2.getX() > isoGridSquare.getX() && !this.open) {
                return VisionResult.Blocked;
            }
        }
        return VisionResult.NoEffect;
    }
    
    public void ToggleDoor(final IsoGameCharacter isoGameCharacter) {
        if (this.Barricaded) {
            return;
        }
        this.DirtySlice();
        if (this.Locked && isoGameCharacter != null && isoGameCharacter.getCurrentSquare().getRoom() == null && !this.open) {
            return;
        }
        this.open = !this.open;
        this.sprite = this.closedSprite;
        if (this.open) {
            this.sprite = this.openSprite;
            if (isoGameCharacter != null) {
                isoGameCharacter.playSound(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getSoundPrefix()));
            }
        }
        else if (isoGameCharacter != null) {
            isoGameCharacter.playSound(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getSoundPrefix()));
        }
        this.syncIsoObject(false, (byte)(this.open ? 1 : 0), null);
    }
    
    public void ToggleDoorSilent() {
        if (this.Barricaded) {
            return;
        }
        this.DirtySlice();
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            LosUtil.cachecleared[i] = true;
        }
        GameTime.instance.lightSourceUpdate = 100.0f;
        IsoGridSquare.setRecalcLightTime(-1);
        this.open = !this.open;
        this.sprite = this.closedSprite;
        if (this.open) {
            this.sprite = this.openSprite;
        }
        this.syncIsoObject(false, (byte)(this.open ? 1 : 0), null);
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, ColorInfo lightInfo, final boolean b, final boolean b2, final Shader shader) {
        final int playerIndex = IsoCamera.frameState.playerIndex;
        final IsoObject objectAttachedTo = this.getObjectAttachedTo();
        if (objectAttachedTo != null && this.getSquare().getTargetDarkMulti(playerIndex) <= objectAttachedTo.getSquare().getTargetDarkMulti(playerIndex)) {
            lightInfo = objectAttachedTo.getSquare().lighting[playerIndex].lightInfo();
            this.setTargetAlpha(playerIndex, objectAttachedTo.getTargetAlpha(playerIndex));
        }
        super.render(n, n2, n3, lightInfo, b, b2, shader);
    }
    
    @Override
    public void syncIsoObjectSend(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(this.square.getX());
        byteBufferWriter.putInt(this.square.getY());
        byteBufferWriter.putInt(this.square.getZ());
        byteBufferWriter.putByte((byte)this.square.getObjects().indexOf(this));
        byteBufferWriter.putByte((byte)1);
        byteBufferWriter.putByte((byte)(this.open ? 1 : 0));
    }
    
    @Override
    public void syncIsoObject(final boolean b, final byte b2, final UdpConnection udpConnection, final ByteBuffer byteBuffer) {
        this.syncIsoObject(b, b2, udpConnection);
    }
    
    public void syncIsoObject(final boolean b, final byte b2, final UdpConnection udpConnection) {
        if (this.square == null) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
            return;
        }
        if (this.getObjectIndex() == -1) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, this.getClass().getSimpleName(), this.square.getX(), this.square.getY(), this.square.getZ()));
            return;
        }
        if (GameClient.bClient && !b) {
            final ByteBufferWriter startPacket = GameClient.connection.startPacket();
            PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket);
            this.syncIsoObjectSend(startPacket);
            PacketTypes.PacketType.SyncIsoObject.send(GameClient.connection);
        }
        else if (b) {
            if (b2 == 1) {
                this.open = true;
                this.sprite = this.openSprite;
            }
            else {
                this.open = false;
                this.sprite = this.closedSprite;
            }
            if (GameServer.bServer) {
                for (final UdpConnection udpConnection2 : GameServer.udpEngine.connections) {
                    if (udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                        final ByteBufferWriter startPacket2 = udpConnection2.startPacket();
                        PacketTypes.PacketType.SyncIsoObject.doPacket(startPacket2);
                        this.syncIsoObjectSend(startPacket2);
                        PacketTypes.PacketType.SyncIsoObject.send(udpConnection2);
                    }
                }
            }
        }
        this.square.RecalcProperties();
        this.square.RecalcAllWithNeighbours(true);
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            LosUtil.cachecleared[i] = true;
        }
        IsoGridSquare.setRecalcLightTime(-1);
        GameTime.instance.lightSourceUpdate = 100.0f;
        LuaEventManager.triggerEvent("OnContainerUpdate");
        if (this.square != null) {
            this.square.RecalcProperties();
        }
    }
    
    public IsoObject getObjectAttachedTo() {
        final int objectIndex = this.getObjectIndex();
        if (objectIndex == -1) {
            return null;
        }
        final PZArrayList<IsoObject> objects = this.getSquare().getObjects();
        if (this.getType() == IsoObjectType.curtainW || this.getType() == IsoObjectType.curtainN) {
            final boolean b = this.getType() == IsoObjectType.curtainN;
            for (int i = objectIndex - 1; i >= 0; --i) {
                final BarricadeAble barricadeAble = Type.tryCastTo(objects.get(i), BarricadeAble.class);
                if (barricadeAble != null && b == barricadeAble.getNorth()) {
                    return objects.get(i);
                }
            }
        }
        else if (this.getType() == IsoObjectType.curtainE || this.getType() == IsoObjectType.curtainS) {
            final IsoGridSquare oppositeSquare = this.getOppositeSquare();
            if (oppositeSquare != null) {
                final boolean b2 = this.getType() == IsoObjectType.curtainS;
                final PZArrayList<IsoObject> objects2 = oppositeSquare.getObjects();
                for (int j = objects2.size() - 1; j >= 0; --j) {
                    final BarricadeAble barricadeAble2 = Type.tryCastTo(objects2.get(j), BarricadeAble.class);
                    if (barricadeAble2 != null && b2 == barricadeAble2.getNorth()) {
                        return objects2.get(j);
                    }
                }
            }
        }
        return null;
    }
    
    public String getSoundPrefix() {
        if (this.closedSprite == null) {
            return "CurtainShort";
        }
        final PropertyContainer properties = this.closedSprite.getProperties();
        if (properties.Is("CurtainSound")) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, properties.Val("CurtainSound"));
        }
        return "CurtainShort";
    }
    
    public static boolean isSheet(IsoObject isoObject) {
        if (isoObject instanceof IsoDoor) {
            isoObject = ((IsoDoor)isoObject).HasCurtains();
        }
        if (isoObject instanceof IsoThumpable) {
            isoObject = ((IsoThumpable)isoObject).HasCurtains();
        }
        if (isoObject instanceof IsoWindow) {
            isoObject = ((IsoWindow)isoObject).HasCurtains();
        }
        if (isoObject == null || isoObject.getSprite() == null) {
            return false;
        }
        final IsoSprite sprite = isoObject.getSprite();
        return sprite.getProperties().Is("CurtainSound") && "Sheet".equals(sprite.getProperties().Val("CurtainSound"));
    }
}
