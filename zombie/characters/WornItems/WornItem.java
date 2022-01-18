// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.WornItems;

import zombie.inventory.InventoryItem;

public final class WornItem
{
    protected final String location;
    protected final InventoryItem item;
    
    public WornItem(final String location, final InventoryItem item) {
        if (location == null) {
            throw new NullPointerException("location is null");
        }
        if (location.isEmpty()) {
            throw new IllegalArgumentException("location is empty");
        }
        if (item == null) {
            throw new NullPointerException("item is null");
        }
        this.location = location;
        this.item = item;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public InventoryItem getItem() {
        return this.item;
    }
}
