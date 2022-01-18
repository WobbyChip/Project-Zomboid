// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import java.util.Arrays;
import zombie.inventory.InventoryItem;
import zombie.characters.IsoGameCharacter;
import java.util.List;

public enum WeaponType
{
    barehand("", Arrays.asList(""), true, false), 
    twohanded("2handed", Arrays.asList("default", "default", "overhead", "uppercut"), true, false), 
    onehanded("1handed", Arrays.asList("default", "default", "overhead", "uppercut"), true, false), 
    heavy("heavy", Arrays.asList("default", "default", "overhead"), true, false), 
    knife("knife", Arrays.asList("default", "default", "overhead", "uppercut"), true, false), 
    spear("spear", Arrays.asList("default"), true, false), 
    handgun("handgun", Arrays.asList(""), false, true), 
    firearm("firearm", Arrays.asList(""), false, true), 
    throwing("throwing", Arrays.asList(""), false, true), 
    chainsaw("chainsaw", Arrays.asList("default"), true, false);
    
    public String type;
    public List<String> possibleAttack;
    public boolean canMiss;
    public boolean isRanged;
    
    private WeaponType(final String type, final List<String> possibleAttack, final boolean canMiss, final boolean isRanged) {
        this.type = "";
        this.canMiss = true;
        this.isRanged = false;
        this.type = type;
        this.possibleAttack = possibleAttack;
        this.canMiss = canMiss;
        this.isRanged = isRanged;
    }
    
    public static WeaponType getWeaponType(final HandWeapon handWeapon) {
        if (handWeapon.getSwingAnim().equalsIgnoreCase("Stab")) {
            return WeaponType.knife;
        }
        if (handWeapon.getSwingAnim().equalsIgnoreCase("Heavy")) {
            return WeaponType.heavy;
        }
        if (handWeapon.getSwingAnim().equalsIgnoreCase("Throw")) {
            return WeaponType.throwing;
        }
        WeaponType weaponType;
        if (!handWeapon.isRanged()) {
            weaponType = WeaponType.onehanded;
            if (handWeapon.isTwoHandWeapon()) {
                weaponType = WeaponType.twohanded;
                if (handWeapon.getSwingAnim().equalsIgnoreCase("Spear")) {
                    return WeaponType.spear;
                }
                if ("Chainsaw".equals(handWeapon.getType())) {
                    return WeaponType.chainsaw;
                }
            }
        }
        else {
            weaponType = WeaponType.handgun;
            if (handWeapon.isTwoHandWeapon()) {
                weaponType = WeaponType.firearm;
            }
        }
        if (weaponType == null) {
            weaponType = WeaponType.barehand;
        }
        return weaponType;
    }
    
    public static WeaponType getWeaponType(final IsoGameCharacter isoGameCharacter) {
        if (isoGameCharacter == null) {
            return null;
        }
        WeaponType weaponType = null;
        isoGameCharacter.setVariable("rangedWeapon", false);
        final InventoryItem primaryHandItem = isoGameCharacter.getPrimaryHandItem();
        final InventoryItem secondaryHandItem = isoGameCharacter.getSecondaryHandItem();
        if (primaryHandItem != null && primaryHandItem instanceof HandWeapon) {
            if (primaryHandItem.getSwingAnim().equalsIgnoreCase("Stab")) {
                return WeaponType.knife;
            }
            if (primaryHandItem.getSwingAnim().equalsIgnoreCase("Heavy")) {
                return WeaponType.heavy;
            }
            if (primaryHandItem.getSwingAnim().equalsIgnoreCase("Throw")) {
                isoGameCharacter.setVariable("rangedWeapon", true);
                return WeaponType.throwing;
            }
            if (!((HandWeapon)primaryHandItem).isRanged()) {
                weaponType = WeaponType.onehanded;
                if (primaryHandItem == secondaryHandItem && primaryHandItem.isTwoHandWeapon()) {
                    weaponType = WeaponType.twohanded;
                    if (primaryHandItem.getSwingAnim().equalsIgnoreCase("Spear")) {
                        return WeaponType.spear;
                    }
                    if ("Chainsaw".equals(primaryHandItem.getType())) {
                        return WeaponType.chainsaw;
                    }
                }
            }
            else {
                weaponType = WeaponType.handgun;
                if (primaryHandItem == secondaryHandItem && primaryHandItem.isTwoHandWeapon()) {
                    weaponType = WeaponType.firearm;
                }
            }
        }
        if (weaponType == null) {
            weaponType = WeaponType.barehand;
        }
        isoGameCharacter.setVariable("rangedWeapon", weaponType == WeaponType.handgun || weaponType == WeaponType.firearm);
        return weaponType;
    }
    
    public String getType() {
        return this.type;
    }
    
    private static /* synthetic */ WeaponType[] $values() {
        return new WeaponType[] { WeaponType.barehand, WeaponType.twohanded, WeaponType.onehanded, WeaponType.heavy, WeaponType.knife, WeaponType.spear, WeaponType.handgun, WeaponType.firearm, WeaponType.throwing, WeaponType.chainsaw };
    }
    
    static {
        $VALUES = $values();
    }
}
