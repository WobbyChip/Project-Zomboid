// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network.packets.hit;

import zombie.core.network.ByteBufferWriter;
import java.nio.BufferUnderflowException;
import java.io.IOException;
import zombie.debug.LogSeverity;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItemFactory;
import zombie.characters.IsoLivingCharacter;
import java.nio.ByteBuffer;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.InventoryItem;
import zombie.network.packets.INetworkPacket;

public class Weapon extends Instance implements INetworkPacket
{
    protected InventoryItem item;
    protected HandWeapon weapon;
    
    public void set(final HandWeapon handWeapon) {
        super.set(handWeapon.getRegistry_id());
        this.item = handWeapon;
        this.weapon = handWeapon;
    }
    
    public void parse(final ByteBuffer byteBuffer, final IsoLivingCharacter isoLivingCharacter) {
        if (byteBuffer.get() == 1) {
            this.ID = byteBuffer.getShort();
            byteBuffer.get();
            if (isoLivingCharacter != null) {
                this.item = isoLivingCharacter.getPrimaryHandItem();
                if (this.item == null || this.item.getRegistry_id() != this.ID) {
                    this.item = InventoryItemFactory.CreateItem(this.ID);
                }
                if (this.item != null) {
                    try {
                        this.item.load(byteBuffer, 186);
                    }
                    catch (IOException | BufferUnderflowException ex) {
                        final Throwable t;
                        DebugLog.Multiplayer.printException(t, "Weapon load error", LogSeverity.Error);
                        this.item = InventoryItemFactory.CreateItem("Base.BareHands");
                    }
                }
            }
        }
        else {
            this.item = InventoryItemFactory.CreateItem("Base.BareHands");
        }
        if (isoLivingCharacter != null) {
            this.weapon = isoLivingCharacter.bareHands;
            if (this.item instanceof HandWeapon) {
                this.weapon = (HandWeapon)this.item;
            }
        }
    }
    
    @Override
    public void parse(final ByteBuffer byteBuffer) {
        DebugLog.Multiplayer.error((Object)"Weapon.parse is not implemented");
    }
    
    @Override
    public void write(final ByteBufferWriter byteBufferWriter) {
        if (this.item == null) {
            byteBufferWriter.putByte((byte)0);
        }
        else {
            byteBufferWriter.putByte((byte)1);
            try {
                this.item.save(byteBufferWriter.bb, false);
            }
            catch (IOException ex) {
                DebugLog.Multiplayer.printException(ex, "Item write error", LogSeverity.Error);
            }
        }
    }
    
    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.weapon != null;
    }
    
    @Override
    public String getDescription() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, super.getDescription(), (this.weapon == null) ? "?" : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.weapon.getDisplayName()));
    }
    
    HandWeapon getWeapon() {
        return this.weapon;
    }
}
