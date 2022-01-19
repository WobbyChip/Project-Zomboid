// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import zombie.scripting.ScriptManager;
import zombie.core.Translator;
import zombie.ui.ObjectTooltip;
import zombie.scripting.objects.Item;
import zombie.inventory.ItemType;
import java.util.ArrayList;
import zombie.inventory.InventoryItem;

public final class WeaponPart extends InventoryItem
{
    public static final String TYPE_CANON = "Canon";
    public static final String TYPE_CLIP = "Clip";
    public static final String TYPE_RECOILPAD = "RecoilPad";
    public static final String TYPE_SCOPE = "Scope";
    public static final String TYPE_SLING = "Sling";
    public static final String TYPE_STOCK = "Stock";
    private float maxRange;
    private float minRangeRanged;
    private float damage;
    private float recoilDelay;
    private int clipSize;
    private int reloadTime;
    private int aimingTime;
    private int hitChance;
    private float angle;
    private float weightModifier;
    private final ArrayList<String> mountOn;
    private final ArrayList<String> mountOnDisplayName;
    private String partType;
    
    public WeaponPart(final String s, final String s2, final String s3, final String s4) {
        super(s, s2, s3, s4);
        this.maxRange = 0.0f;
        this.minRangeRanged = 0.0f;
        this.damage = 0.0f;
        this.recoilDelay = 0.0f;
        this.clipSize = 0;
        this.reloadTime = 0;
        this.aimingTime = 0;
        this.hitChance = 0;
        this.angle = 0.0f;
        this.weightModifier = 0.0f;
        this.mountOn = new ArrayList<String>();
        this.mountOnDisplayName = new ArrayList<String>();
        this.partType = null;
        this.cat = ItemType.Weapon;
    }
    
    @Override
    public int getSaveType() {
        return Item.Type.WeaponPart.ordinal();
    }
    
    @Override
    public String getCategory() {
        if (this.mainCategory != null) {
            return this.mainCategory;
        }
        return "WeaponPart";
    }
    
    @Override
    public void DoTooltip(final ObjectTooltip objectTooltip, final ObjectTooltip.Layout layout) {
        final ObjectTooltip.LayoutItem addItem = layout.addItem();
        addItem.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_Type")), 1.0f, 1.0f, 0.8f, 1.0f);
        addItem.setValue(Translator.getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.partType)), 1.0f, 1.0f, 0.8f, 1.0f);
        layout.addItem().setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_CanBeMountOn"), this.mountOnDisplayName.toString().replaceAll("\\[", "").replaceAll("\\]", "")), 1.0f, 1.0f, 0.8f, 1.0f);
    }
    
    public float getMinRangeRanged() {
        return this.minRangeRanged;
    }
    
    public void setMinRangeRanged(final float minRangeRanged) {
        this.minRangeRanged = minRangeRanged;
    }
    
    public float getMaxRange() {
        return this.maxRange;
    }
    
    public void setMaxRange(final float maxRange) {
        this.maxRange = maxRange;
    }
    
    public float getRecoilDelay() {
        return this.recoilDelay;
    }
    
    public void setRecoilDelay(final float recoilDelay) {
        this.recoilDelay = recoilDelay;
    }
    
    public int getClipSize() {
        return this.clipSize;
    }
    
    public void setClipSize(final int clipSize) {
        this.clipSize = clipSize;
    }
    
    public float getDamage() {
        return this.damage;
    }
    
    public void setDamage(final float damage) {
        this.damage = damage;
    }
    
    public ArrayList<String> getMountOn() {
        return this.mountOn;
    }
    
    public void setMountOn(final ArrayList<String> list) {
        this.mountOn.clear();
        this.mountOnDisplayName.clear();
        for (int i = 0; i < list.size(); ++i) {
            String s = list.get(i);
            if (!s.contains(".")) {
                s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getModule(), s);
            }
            final Item item = ScriptManager.instance.getItem(s);
            if (item != null) {
                this.mountOn.add(item.getFullName());
                this.mountOnDisplayName.add(item.getDisplayName());
            }
        }
    }
    
    public String getPartType() {
        return this.partType;
    }
    
    public void setPartType(final String partType) {
        this.partType = partType;
    }
    
    public int getReloadTime() {
        return this.reloadTime;
    }
    
    public void setReloadTime(final int reloadTime) {
        this.reloadTime = reloadTime;
    }
    
    public int getAimingTime() {
        return this.aimingTime;
    }
    
    public void setAimingTime(final int aimingTime) {
        this.aimingTime = aimingTime;
    }
    
    public int getHitChance() {
        return this.hitChance;
    }
    
    public void setHitChance(final int hitChance) {
        this.hitChance = hitChance;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
    }
    
    public float getWeightModifier() {
        return this.weightModifier;
    }
    
    public void setWeightModifier(final float weightModifier) {
        this.weightModifier = weightModifier;
    }
}
