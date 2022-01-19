// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import zombie.scripting.objects.Item;
import zombie.inventory.InventoryItem;

public final class ComboItem extends InventoryItem
{
    public ComboItem(final String s, final String s2, final String s3, final String s4) {
        super(s, s2, s3, s4);
    }
    
    public ComboItem(final String s, final String s2, final String s3, final Item item) {
        super(s, s2, s3, item);
    }
    
    @Override
    public int getSaveType() {
        return Item.Type.Normal.ordinal();
    }
}
