// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.characters.AttachedItems.AttachedLocationGroup;
import zombie.inventory.InventoryItem;
import zombie.characters.AttachedItems.AttachedItems;

public interface ILuaGameCharacterAttachedItems
{
    AttachedItems getAttachedItems();
    
    void setAttachedItems(final AttachedItems p0);
    
    InventoryItem getAttachedItem(final String p0);
    
    void setAttachedItem(final String p0, final InventoryItem p1);
    
    void removeAttachedItem(final InventoryItem p0);
    
    void clearAttachedItems();
    
    AttachedLocationGroup getAttachedLocationGroup();
}
