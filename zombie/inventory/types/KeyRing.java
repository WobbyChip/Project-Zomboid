// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import java.util.Collection;
import zombie.scripting.objects.Item;
import zombie.inventory.ItemType;
import java.util.ArrayList;
import zombie.inventory.InventoryItem;

public final class KeyRing extends InventoryItem
{
    private final ArrayList<Key> keys;
    
    public KeyRing(final String s, final String s2, final String s3, final String s4) {
        super(s, s2, s3, s4);
        this.keys = new ArrayList<Key>();
        this.cat = ItemType.KeyRing;
    }
    
    @Override
    public int getSaveType() {
        return Item.Type.KeyRing.ordinal();
    }
    
    public void addKey(final Key e) {
        this.keys.add(e);
    }
    
    public boolean containsKeyId(final int n) {
        for (int i = 0; i < this.keys.size(); ++i) {
            if (this.keys.get(i).getKeyId() == n) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getCategory() {
        if (this.mainCategory != null) {
            return this.mainCategory;
        }
        return "Key Ring";
    }
    
    public ArrayList<Key> getKeys() {
        return this.keys;
    }
    
    public void setKeys(final ArrayList<Key> c) {
        c.clear();
        this.keys.addAll(c);
    }
}
