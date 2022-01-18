// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects.interfaces;

import zombie.inventory.types.HandWeapon;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoMovingObject;

public interface Thumpable
{
    boolean isDestroyed();
    
    void Thump(final IsoMovingObject p0);
    
    void WeaponHit(final IsoGameCharacter p0, final HandWeapon p1);
    
    Thumpable getThumpableFor(final IsoGameCharacter p0);
    
    float getThumpCondition();
}
