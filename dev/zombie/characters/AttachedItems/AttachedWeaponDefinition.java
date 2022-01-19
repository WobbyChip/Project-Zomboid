// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.AttachedItems;

import zombie.characterTextures.BloodBodyPartType;
import java.util.ArrayList;

public final class AttachedWeaponDefinition
{
    public String id;
    public int chance;
    public final ArrayList<String> outfit;
    public final ArrayList<String> weaponLocation;
    public final ArrayList<BloodBodyPartType> bloodLocations;
    public boolean addHoles;
    public int daySurvived;
    public String ensureItem;
    public final ArrayList<String> weapons;
    
    public AttachedWeaponDefinition() {
        this.outfit = new ArrayList<String>();
        this.weaponLocation = new ArrayList<String>();
        this.bloodLocations = new ArrayList<BloodBodyPartType>();
        this.weapons = new ArrayList<String>();
    }
}
