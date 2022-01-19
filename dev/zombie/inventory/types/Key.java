// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.scripting.objects.Item;
import zombie.inventory.ItemType;
import zombie.inventory.InventoryItem;

public final class Key extends InventoryItem
{
    private int keyId;
    private boolean padlock;
    private int numberOfKey;
    private boolean digitalPadlock;
    public static final Key[] highlightDoor;
    
    public Key(final String s, final String s2, final String s3, final String s4) {
        super(s, s2, s3, s4);
        this.keyId = -1;
        this.padlock = false;
        this.numberOfKey = 0;
        this.digitalPadlock = false;
        this.cat = ItemType.Key;
    }
    
    @Override
    public int getSaveType() {
        return Item.Type.Key.ordinal();
    }
    
    public void takeKeyId() {
        if (this.getContainer() != null && this.getContainer().getSourceGrid() != null && this.getContainer().getSourceGrid().getBuilding() != null && this.getContainer().getSourceGrid().getBuilding().def != null) {
            this.setKeyId(this.getContainer().getSourceGrid().getBuilding().def.getKeyId());
        }
    }
    
    public static void setHighlightDoors(final int n, final InventoryItem inventoryItem) {
        if (inventoryItem instanceof Key && !((Key)inventoryItem).isPadlock() && !((Key)inventoryItem).isDigitalPadlock()) {
            Key.highlightDoor[n] = (Key)inventoryItem;
        }
        else {
            Key.highlightDoor[n] = null;
        }
    }
    
    @Override
    public int getKeyId() {
        return this.keyId;
    }
    
    @Override
    public void setKeyId(final int keyId) {
        this.keyId = keyId;
    }
    
    @Override
    public String getCategory() {
        if (this.mainCategory != null) {
            return this.mainCategory;
        }
        return "Key";
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        byteBuffer.putInt(this.getKeyId());
        byteBuffer.put((byte)this.numberOfKey);
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        super.load(byteBuffer, n);
        this.setKeyId(byteBuffer.getInt());
        this.numberOfKey = byteBuffer.get();
    }
    
    public boolean isPadlock() {
        return this.padlock;
    }
    
    public void setPadlock(final boolean padlock) {
        this.padlock = padlock;
    }
    
    public int getNumberOfKey() {
        return this.numberOfKey;
    }
    
    public void setNumberOfKey(final int numberOfKey) {
        this.numberOfKey = numberOfKey;
    }
    
    public boolean isDigitalPadlock() {
        return this.digitalPadlock;
    }
    
    public void setDigitalPadlock(final boolean digitalPadlock) {
        this.digitalPadlock = digitalPadlock;
    }
    
    static {
        highlightDoor = new Key[4];
    }
}
