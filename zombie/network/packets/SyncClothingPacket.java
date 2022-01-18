// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets;

import zombie.core.skinnedmodel.visual.ItemVisuals;
import java.io.IOException;
import zombie.core.network.ByteBufferWriter;
import zombie.network.ServerGUI;
import zombie.core.logger.ExceptionLogger;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.inventory.InventoryItem;
import zombie.characters.IsoPlayer;

public class SyncClothingPacket implements INetworkPacket
{
    private IsoPlayer player;
    private String location;
    private InventoryItem item;
    
    public SyncClothingPacket() {
        this.player = null;
        this.location = "";
        this.item = null;
    }
    
    public void set(final IsoPlayer player, final String location, final InventoryItem item) {
        this.player = player;
        this.location = location;
        this.item = item;
    }
    
    public boolean isEquals(final IsoPlayer isoPlayer, final String anObject, final InventoryItem inventoryItem) {
        return this.player.OnlineID == isoPlayer.OnlineID && this.location.equals(anObject) && this.item == inventoryItem;
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        final short short1 = byteBuffer.getShort();
        this.location = GameWindow.ReadString(byteBuffer);
        final byte value = byteBuffer.get();
        if (value == 1) {
            try {
                this.item = InventoryItem.loadItem(byteBuffer, 186);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            if (this.item == null) {
                return;
            }
        }
        if (GameServer.bServer) {
            this.player = GameServer.IDToPlayerMap.get(short1);
        }
        else {
            this.player = GameClient.IDToPlayerMap.get(short1);
        }
        if (this.player == null) {
            return;
        }
        try {
            this.player.getHumanVisual().load(byteBuffer, 186);
            this.player.getItemVisuals().load(byteBuffer, 186);
        }
        catch (Throwable t) {
            ExceptionLogger.logException(t);
            return;
        }
        if (value == 1) {
            this.player.getWornItems().setItem(this.location, this.item);
        }
        if ((GameServer.bServer && ServerGUI.isCreated()) || GameClient.bClient) {
            this.player.resetModelNextFrame();
        }
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort((byte)this.player.OnlineID);
        byteBufferWriter.putUTF(this.location);
        if (this.item == null) {
            byteBufferWriter.putByte((byte)0);
        }
        else {
            byteBufferWriter.putByte((byte)1);
            try {
                this.item.saveWithSize(byteBufferWriter.bb, false);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            this.player.getHumanVisual().save(byteBufferWriter.bb);
            final ItemVisuals itemVisuals = new ItemVisuals();
            this.player.getItemVisuals(itemVisuals);
            itemVisuals.save(byteBufferWriter.bb);
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }
    
    @Override
    public boolean isConsistent() {
        return this.player != null;
    }
    
    @Override
    public String getDescription() {
        String format;
        if (this.player == null) {
            format = "player=null";
        }
        else {
            format = String.format("player=%s(oid:%d)", this.player.username, this.player.OnlineID);
        }
        final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, format, this.location);
        String s2;
        if (this.item == null) {
            s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        else {
            s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.item.getFullType());
        }
        return s2;
    }
}
