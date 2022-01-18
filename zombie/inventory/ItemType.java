// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory;

public enum ItemType
{
    None(0), 
    Weapon(1), 
    Food(2), 
    Literature(3), 
    Drainable(4), 
    Clothing(5), 
    Key(6), 
    KeyRing(7), 
    Moveable(8), 
    AlarmClock(9), 
    AlarmClockClothing(10);
    
    private int index;
    
    private ItemType(final int index) {
        this.index = index;
    }
    
    public int index() {
        return this.index;
    }
    
    public static ItemType fromIndex(final int n) {
        return ItemType.class.getEnumConstants()[n];
    }
    
    private static /* synthetic */ ItemType[] $values() {
        return new ItemType[] { ItemType.None, ItemType.Weapon, ItemType.Food, ItemType.Literature, ItemType.Drainable, ItemType.Clothing, ItemType.Key, ItemType.KeyRing, ItemType.Moveable, ItemType.AlarmClock, ItemType.AlarmClockClothing };
    }
    
    static {
        $VALUES = $values();
    }
}
