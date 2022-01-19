// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.AttachedItems;

import java.util.ArrayList;

public final class AttachedWeaponCustomOutfit
{
    public String outfit;
    public int chance;
    public int maxitem;
    public final ArrayList<AttachedWeaponDefinition> weapons;
    
    public AttachedWeaponCustomOutfit() {
        this.weapons = new ArrayList<AttachedWeaponDefinition>();
    }
}
