// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.characters.WornItems.BodyLocationGroup;
import zombie.inventory.InventoryItem;
import zombie.characters.WornItems.WornItems;

public interface ILuaGameCharacterClothing
{
    void dressInNamedOutfit(final String p0);
    
    void dressInPersistentOutfit(final String p0);
    
    void dressInPersistentOutfitID(final int p0);
    
    String getOutfitName();
    
    WornItems getWornItems();
    
    void setWornItems(final WornItems p0);
    
    InventoryItem getWornItem(final String p0);
    
    void setWornItem(final String p0, final InventoryItem p1);
    
    void removeWornItem(final InventoryItem p0);
    
    void clearWornItems();
    
    BodyLocationGroup getBodyLocationGroup();
    
    void setClothingItem_Head(final InventoryItem p0);
    
    void setClothingItem_Torso(final InventoryItem p0);
    
    void setClothingItem_Back(final InventoryItem p0);
    
    void setClothingItem_Hands(final InventoryItem p0);
    
    void setClothingItem_Legs(final InventoryItem p0);
    
    void setClothingItem_Feet(final InventoryItem p0);
    
    void Dressup(final SurvivorDesc p0);
}
