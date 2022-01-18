// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.inventory.InventoryItem;

public interface ILuaGameCharacterHealth
{
    void setSleepingTabletEffect(final float p0);
    
    float getSleepingTabletEffect();
    
    float getFatigueMod();
    
    boolean Eat(final InventoryItem p0, final float p1);
    
    boolean Eat(final InventoryItem p0);
    
    float getTemperature();
    
    void setTemperature(final float p0);
    
    float getReduceInfectionPower();
    
    void setReduceInfectionPower(final float p0);
    
    int getLastHourSleeped();
    
    void setLastHourSleeped(final int p0);
    
    void setTimeOfSleep(final float p0);
}
