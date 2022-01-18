// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.util.io.BitHeaderRead;
import java.io.IOException;
import zombie.util.io.BitHeaderWrite;
import zombie.util.io.BitHeader;
import java.nio.ByteBuffer;
import zombie.core.BoxedStaticValues;
import zombie.characters.skills.PerkFactory;
import zombie.inventory.InventoryItemFactory;
import zombie.core.Core;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Translator;
import zombie.ui.ObjectTooltip;
import zombie.scripting.ScriptManager;
import zombie.util.StringUtils;
import zombie.characters.SurvivorDesc;
import zombie.scripting.objects.Item;
import zombie.inventory.ItemType;
import zombie.scripting.objects.ModelWeaponPart;
import java.util.ArrayList;
import zombie.inventory.InventoryItem;

public final class HandWeapon extends InventoryItem
{
    public float WeaponLength;
    public float SplatSize;
    private int ammoPerShoot;
    private String magazineType;
    protected boolean angleFalloff;
    protected boolean bCanBarracade;
    protected float doSwingBeforeImpact;
    protected String impactSound;
    protected boolean knockBackOnNoDeath;
    protected float maxAngle;
    protected float maxDamage;
    protected int maxHitCount;
    protected float maxRange;
    protected boolean ranged;
    protected float minAngle;
    protected float minDamage;
    protected float minimumSwingTime;
    protected float minRange;
    protected float noiseFactor;
    protected String otherHandRequire;
    protected boolean otherHandUse;
    protected String physicsObject;
    protected float pushBackMod;
    protected boolean rangeFalloff;
    protected boolean shareDamage;
    protected int soundRadius;
    protected int soundVolume;
    protected boolean splatBloodOnNoDeath;
    protected int splatNumber;
    protected String swingSound;
    protected float swingTime;
    protected float toHitModifier;
    protected boolean useEndurance;
    protected boolean useSelf;
    protected String weaponSprite;
    private String originalWeaponSprite;
    protected float otherBoost;
    protected int DoorDamage;
    protected String doorHitSound;
    protected int ConditionLowerChance;
    protected boolean MultipleHitConditionAffected;
    protected boolean shareEndurance;
    protected boolean AlwaysKnockdown;
    protected float EnduranceMod;
    protected float KnockdownMod;
    protected boolean CantAttackWithLowestEndurance;
    public boolean bIsAimedFirearm;
    public boolean bIsAimedHandWeapon;
    public String RunAnim;
    public String IdleAnim;
    public float HitAngleMod;
    private String SubCategory;
    private ArrayList<String> Categories;
    private int AimingPerkCritModifier;
    private float AimingPerkRangeModifier;
    private float AimingPerkHitChanceModifier;
    private int HitChance;
    private float AimingPerkMinAngleModifier;
    private int RecoilDelay;
    private boolean PiercingBullets;
    private float soundGain;
    private WeaponPart scope;
    private WeaponPart canon;
    private WeaponPart clip;
    private WeaponPart recoilpad;
    private WeaponPart sling;
    private WeaponPart stock;
    private int ClipSize;
    private int reloadTime;
    private int aimingTime;
    private float minRangeRanged;
    private int treeDamage;
    private String bulletOutSound;
    private String shellFallSound;
    private int triggerExplosionTimer;
    private boolean canBePlaced;
    private int explosionRange;
    private int explosionPower;
    private int fireRange;
    private int firePower;
    private int smokeRange;
    private int noiseRange;
    private float extraDamage;
    private int explosionTimer;
    private String placedSprite;
    private boolean canBeReused;
    private int sensorRange;
    private float critDmgMultiplier;
    private float baseSpeed;
    private float bloodLevel;
    private String ammoBox;
    private String insertAmmoStartSound;
    private String insertAmmoSound;
    private String insertAmmoStopSound;
    private String ejectAmmoStartSound;
    private String ejectAmmoSound;
    private String ejectAmmoStopSound;
    private String rackSound;
    private String clickSound;
    private boolean containsClip;
    private String weaponReloadType;
    private boolean rackAfterShoot;
    private boolean roundChambered;
    private boolean bSpentRoundChambered;
    private int spentRoundCount;
    private float jamGunChance;
    private boolean isJammed;
    private ArrayList<ModelWeaponPart> modelWeaponPart;
    private boolean haveChamber;
    private String bulletName;
    private String damageCategory;
    private boolean damageMakeHole;
    private String hitFloorSound;
    private boolean insertAllBulletsReload;
    private String fireMode;
    private ArrayList<String> fireModePossibilities;
    public int ProjectileCount;
    public float aimingMod;
    public float CriticalChance;
    private String hitSound;
    
    public float getSplatSize() {
        return this.SplatSize;
    }
    
    @Override
    public boolean CanStack(final InventoryItem inventoryItem) {
        return false;
    }
    
    @Override
    public String getCategory() {
        if (this.mainCategory != null) {
            return this.mainCategory;
        }
        return "Weapon";
    }
    
    public HandWeapon(final String s, final String s2, final String s3, final String s4) {
        super(s, s2, s3, s4);
        this.SplatSize = 1.0f;
        this.ammoPerShoot = 1;
        this.magazineType = null;
        this.angleFalloff = false;
        this.bCanBarracade = false;
        this.doSwingBeforeImpact = 0.0f;
        this.impactSound = "BaseballBatHit";
        this.knockBackOnNoDeath = true;
        this.maxAngle = 1.0f;
        this.maxDamage = 1.5f;
        this.maxHitCount = 1000;
        this.maxRange = 1.0f;
        this.ranged = false;
        this.minAngle = 0.5f;
        this.minDamage = 0.4f;
        this.minimumSwingTime = 0.5f;
        this.minRange = 0.0f;
        this.noiseFactor = 0.0f;
        this.otherHandRequire = null;
        this.otherHandUse = false;
        this.physicsObject = null;
        this.pushBackMod = 1.0f;
        this.rangeFalloff = false;
        this.shareDamage = true;
        this.soundRadius = 0;
        this.soundVolume = 0;
        this.splatBloodOnNoDeath = false;
        this.splatNumber = 2;
        this.swingSound = "BaseballBatSwing";
        this.swingTime = 1.0f;
        this.toHitModifier = 1.0f;
        this.useEndurance = true;
        this.useSelf = false;
        this.weaponSprite = null;
        this.originalWeaponSprite = null;
        this.otherBoost = 1.0f;
        this.DoorDamage = 1;
        this.doorHitSound = "BaseballBatHit";
        this.ConditionLowerChance = 10000;
        this.MultipleHitConditionAffected = true;
        this.shareEndurance = true;
        this.AlwaysKnockdown = false;
        this.EnduranceMod = 1.0f;
        this.KnockdownMod = 1.0f;
        this.CantAttackWithLowestEndurance = false;
        this.bIsAimedFirearm = false;
        this.bIsAimedHandWeapon = false;
        this.RunAnim = "Run";
        this.IdleAnim = "Idle";
        this.HitAngleMod = 0.0f;
        this.SubCategory = "";
        this.Categories = null;
        this.AimingPerkCritModifier = 0;
        this.AimingPerkRangeModifier = 0.0f;
        this.AimingPerkHitChanceModifier = 0.0f;
        this.HitChance = 0;
        this.AimingPerkMinAngleModifier = 0.0f;
        this.RecoilDelay = 0;
        this.PiercingBullets = false;
        this.soundGain = 1.0f;
        this.scope = null;
        this.canon = null;
        this.clip = null;
        this.recoilpad = null;
        this.sling = null;
        this.stock = null;
        this.ClipSize = 0;
        this.reloadTime = 0;
        this.aimingTime = 0;
        this.minRangeRanged = 0.0f;
        this.treeDamage = 0;
        this.bulletOutSound = null;
        this.shellFallSound = null;
        this.triggerExplosionTimer = 0;
        this.canBePlaced = false;
        this.explosionRange = 0;
        this.explosionPower = 0;
        this.fireRange = 0;
        this.firePower = 0;
        this.smokeRange = 0;
        this.noiseRange = 0;
        this.extraDamage = 0.0f;
        this.explosionTimer = 0;
        this.placedSprite = null;
        this.canBeReused = false;
        this.sensorRange = 0;
        this.critDmgMultiplier = 2.0f;
        this.baseSpeed = 1.0f;
        this.bloodLevel = 0.0f;
        this.ammoBox = null;
        this.insertAmmoStartSound = null;
        this.insertAmmoSound = null;
        this.insertAmmoStopSound = null;
        this.ejectAmmoStartSound = null;
        this.ejectAmmoSound = null;
        this.ejectAmmoStopSound = null;
        this.rackSound = null;
        this.clickSound = "Stormy9mmClick";
        this.containsClip = false;
        this.weaponReloadType = "handgun";
        this.rackAfterShoot = false;
        this.roundChambered = false;
        this.bSpentRoundChambered = false;
        this.spentRoundCount = 0;
        this.jamGunChance = 5.0f;
        this.isJammed = false;
        this.modelWeaponPart = null;
        this.haveChamber = true;
        this.bulletName = null;
        this.damageCategory = null;
        this.damageMakeHole = false;
        this.hitFloorSound = "BatOnFloor";
        this.insertAllBulletsReload = false;
        this.fireMode = null;
        this.fireModePossibilities = null;
        this.ProjectileCount = 1;
        this.aimingMod = 1.0f;
        this.CriticalChance = 20.0f;
        this.hitSound = "BaseballBatHit";
        this.cat = ItemType.Weapon;
    }
    
    public HandWeapon(final String s, final String s2, final String s3, final Item item) {
        super(s, s2, s3, item);
        this.SplatSize = 1.0f;
        this.ammoPerShoot = 1;
        this.magazineType = null;
        this.angleFalloff = false;
        this.bCanBarracade = false;
        this.doSwingBeforeImpact = 0.0f;
        this.impactSound = "BaseballBatHit";
        this.knockBackOnNoDeath = true;
        this.maxAngle = 1.0f;
        this.maxDamage = 1.5f;
        this.maxHitCount = 1000;
        this.maxRange = 1.0f;
        this.ranged = false;
        this.minAngle = 0.5f;
        this.minDamage = 0.4f;
        this.minimumSwingTime = 0.5f;
        this.minRange = 0.0f;
        this.noiseFactor = 0.0f;
        this.otherHandRequire = null;
        this.otherHandUse = false;
        this.physicsObject = null;
        this.pushBackMod = 1.0f;
        this.rangeFalloff = false;
        this.shareDamage = true;
        this.soundRadius = 0;
        this.soundVolume = 0;
        this.splatBloodOnNoDeath = false;
        this.splatNumber = 2;
        this.swingSound = "BaseballBatSwing";
        this.swingTime = 1.0f;
        this.toHitModifier = 1.0f;
        this.useEndurance = true;
        this.useSelf = false;
        this.weaponSprite = null;
        this.originalWeaponSprite = null;
        this.otherBoost = 1.0f;
        this.DoorDamage = 1;
        this.doorHitSound = "BaseballBatHit";
        this.ConditionLowerChance = 10000;
        this.MultipleHitConditionAffected = true;
        this.shareEndurance = true;
        this.AlwaysKnockdown = false;
        this.EnduranceMod = 1.0f;
        this.KnockdownMod = 1.0f;
        this.CantAttackWithLowestEndurance = false;
        this.bIsAimedFirearm = false;
        this.bIsAimedHandWeapon = false;
        this.RunAnim = "Run";
        this.IdleAnim = "Idle";
        this.HitAngleMod = 0.0f;
        this.SubCategory = "";
        this.Categories = null;
        this.AimingPerkCritModifier = 0;
        this.AimingPerkRangeModifier = 0.0f;
        this.AimingPerkHitChanceModifier = 0.0f;
        this.HitChance = 0;
        this.AimingPerkMinAngleModifier = 0.0f;
        this.RecoilDelay = 0;
        this.PiercingBullets = false;
        this.soundGain = 1.0f;
        this.scope = null;
        this.canon = null;
        this.clip = null;
        this.recoilpad = null;
        this.sling = null;
        this.stock = null;
        this.ClipSize = 0;
        this.reloadTime = 0;
        this.aimingTime = 0;
        this.minRangeRanged = 0.0f;
        this.treeDamage = 0;
        this.bulletOutSound = null;
        this.shellFallSound = null;
        this.triggerExplosionTimer = 0;
        this.canBePlaced = false;
        this.explosionRange = 0;
        this.explosionPower = 0;
        this.fireRange = 0;
        this.firePower = 0;
        this.smokeRange = 0;
        this.noiseRange = 0;
        this.extraDamage = 0.0f;
        this.explosionTimer = 0;
        this.placedSprite = null;
        this.canBeReused = false;
        this.sensorRange = 0;
        this.critDmgMultiplier = 2.0f;
        this.baseSpeed = 1.0f;
        this.bloodLevel = 0.0f;
        this.ammoBox = null;
        this.insertAmmoStartSound = null;
        this.insertAmmoSound = null;
        this.insertAmmoStopSound = null;
        this.ejectAmmoStartSound = null;
        this.ejectAmmoSound = null;
        this.ejectAmmoStopSound = null;
        this.rackSound = null;
        this.clickSound = "Stormy9mmClick";
        this.containsClip = false;
        this.weaponReloadType = "handgun";
        this.rackAfterShoot = false;
        this.roundChambered = false;
        this.bSpentRoundChambered = false;
        this.spentRoundCount = 0;
        this.jamGunChance = 5.0f;
        this.isJammed = false;
        this.modelWeaponPart = null;
        this.haveChamber = true;
        this.bulletName = null;
        this.damageCategory = null;
        this.damageMakeHole = false;
        this.hitFloorSound = "BatOnFloor";
        this.insertAllBulletsReload = false;
        this.fireMode = null;
        this.fireModePossibilities = null;
        this.ProjectileCount = 1;
        this.aimingMod = 1.0f;
        this.CriticalChance = 20.0f;
        this.hitSound = "BaseballBatHit";
        this.cat = ItemType.Weapon;
    }
    
    @Override
    public boolean IsWeapon() {
        return true;
    }
    
    @Override
    public int getSaveType() {
        return Item.Type.Weapon.ordinal();
    }
    
    @Override
    public float getScore(final SurvivorDesc survivorDesc) {
        float n = 0.0f;
        if (this.getAmmoType() != null && !this.getAmmoType().equals("none") && !this.container.contains(this.getAmmoType())) {
            n -= 100000.0f;
        }
        if (this.Condition == 0) {
            n -= 100000.0f;
        }
        float n2 = n + this.maxDamage * 10.0f + this.maxAngle * 5.0f - this.minimumSwingTime * 0.1f - this.swingTime;
        if (survivorDesc != null && survivorDesc.getInstance().getThreatLevel() <= 2 && this.soundRadius > 5) {
            if (n2 > 0.0f && this.soundRadius > n2) {
                n2 = 1.0f;
            }
            n2 -= this.soundRadius;
        }
        return n2;
    }
    
    @Override
    public float getContentsWeight() {
        float n = 0.0f;
        if (this.haveChamber() && this.isRoundChambered() && !StringUtils.isNullOrWhitespace(this.getAmmoType())) {
            final Item findItem = ScriptManager.instance.FindItem(this.getAmmoType());
            if (findItem != null) {
                n += findItem.getActualWeight();
            }
        }
        if (this.isContainsClip() && !StringUtils.isNullOrWhitespace(this.getMagazineType())) {
            final Item findItem2 = ScriptManager.instance.FindItem(this.getMagazineType());
            if (findItem2 != null) {
                n += findItem2.getActualWeight();
            }
        }
        return n + super.getContentsWeight();
    }
    
    @Override
    public void DoTooltip(final ObjectTooltip objectTooltip, final ObjectTooltip.Layout layout) {
        final float n = 1.0f;
        final float n2 = 1.0f;
        final float n3 = 0.8f;
        final float n4 = 1.0f;
        final float n5 = 0.0f;
        final float n6 = 0.6f;
        final float n7 = 0.0f;
        final float n8 = 0.7f;
        final ObjectTooltip.LayoutItem addItem = layout.addItem();
        addItem.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_Condition")), n, n2, n3, n4);
        addItem.setProgress(this.Condition / (float)this.ConditionMax, n5, n6, n7, n8);
        if (this.getMaxDamage() > 0.0f) {
            final ObjectTooltip.LayoutItem addItem2 = layout.addItem();
            addItem2.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_Damage")), n, n2, n3, n4);
            addItem2.setProgress((this.getMaxDamage() + this.getMinDamage()) / 5.0f, n5, n6, n7, n8);
        }
        if (this.isRanged()) {
            final ObjectTooltip.LayoutItem addItem3 = layout.addItem();
            addItem3.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_Range")), n, n2, n3, 1.0f);
            addItem3.setProgress(this.getMaxRange(IsoPlayer.getInstance()) / 40.0f, n5, n6, n7, n8);
        }
        if (this.isTwoHandWeapon() && !this.isRequiresEquippedBothHands()) {
            layout.addItem().setLabel(Translator.getText("Tooltip_item_TwoHandWeapon"), n, n2, n3, n4);
        }
        if (!StringUtils.isNullOrEmpty(this.getFireMode())) {
            final ObjectTooltip.LayoutItem addItem4 = layout.addItem();
            addItem4.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_item_FireMode")), n, n2, n3, n4);
            addItem4.setValue(Translator.getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getFireMode())), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.CantAttackWithLowestEndurance) {
            layout.addItem().setLabel(Translator.getText("Tooltip_weapon_Unusable_at_max_exertion"), 1.0f, 0.0f, 0.0f, 1.0f);
        }
        String ammoType = this.getAmmoType();
        if (Core.getInstance().isNewReloading()) {
            if (this.getMaxAmmo() > 0) {
                String value = String.valueOf(this.getCurrentAmmoCount());
                if (this.isRoundChambered()) {
                    value = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, value);
                }
                final ObjectTooltip.LayoutItem addItem5 = layout.addItem();
                if (this.bulletName == null) {
                    if (this.getMagazineType() != null) {
                        this.bulletName = InventoryItemFactory.CreateItem(this.getMagazineType()).getDisplayName();
                    }
                    else {
                        this.bulletName = InventoryItemFactory.CreateItem(this.getAmmoType()).getDisplayName();
                    }
                }
                addItem5.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.bulletName), 1.0f, 1.0f, 0.8f, 1.0f);
                addItem5.setValue(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, value, this.getMaxAmmo()), 1.0f, 1.0f, 1.0f, 1.0f);
            }
            if (this.isJammed()) {
                layout.addItem().setLabel(Translator.getText("Tooltip_weapon_Jammed"), 1.0f, 0.1f, 0.1f, 1.0f);
            }
            else if (this.haveChamber() && !this.isRoundChambered() && this.getCurrentAmmoCount() > 0) {
                layout.addItem().setLabel(Translator.getText(this.isSpentRoundChambered() ? "Tooltip_weapon_SpentRoundChambered" : "Tooltip_weapon_NoRoundChambered"), 1.0f, 0.1f, 0.1f, 1.0f);
            }
            else if (this.getSpentRoundCount() > 0) {
                final ObjectTooltip.LayoutItem addItem6 = layout.addItem();
                addItem6.setLabel("Spent Rounds:", 1.0f, 0.1f, 0.1f, 1.0f);
                addItem6.setValue(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.getSpentRoundCount(), this.getMaxAmmo()), 1.0f, 1.0f, 1.0f, 1.0f);
            }
            if (!StringUtils.isNullOrEmpty(this.getMagazineType())) {
                if (this.isContainsClip()) {
                    layout.addItem().setLabel(Translator.getText("Tooltip_weapon_ContainsClip"), 1.0f, 1.0f, 0.8f, 1.0f);
                }
                else {
                    layout.addItem().setLabel(Translator.getText("Tooltip_weapon_NoClip"), 1.0f, 1.0f, 0.8f, 1.0f);
                }
            }
        }
        else {
            if (ammoType == null && this.hasModData()) {
                final Object rawget = this.getModData().rawget((Object)"defaultAmmo");
                if (rawget instanceof String) {
                    ammoType = (String)rawget;
                }
            }
            if (ammoType != null) {
                Item item = ScriptManager.instance.FindItem(ammoType);
                if (item == null) {
                    item = ScriptManager.instance.FindItem(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getModule(), ammoType));
                }
                if (item != null) {
                    final ObjectTooltip.LayoutItem addItem7 = layout.addItem();
                    addItem7.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_Ammo")), n, n2, n3, n4);
                    addItem7.setValue(item.getDisplayName(), 1.0f, 1.0f, 1.0f, 1.0f);
                }
                final Object rawget2 = this.getModData().rawget((Object)"currentCapacity");
                final Object rawget3 = this.getModData().rawget((Object)"maxCapacity");
                if (rawget2 instanceof Double && rawget3 instanceof Double) {
                    String s = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, ((Double)rawget2).intValue(), ((Double)rawget3).intValue());
                    final Object rawget4 = this.getModData().rawget((Object)"roundChambered");
                    if (rawget4 instanceof Double && ((Double)rawget4).intValue() == 1) {
                        s = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, ((Double)rawget2).intValue(), ((Double)rawget3).intValue());
                    }
                    else {
                        final Object rawget5 = this.getModData().rawget((Object)"emptyShellChambered");
                        if (rawget5 instanceof Double && ((Double)rawget5).intValue() == 1) {
                            s = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, ((Double)rawget2).intValue(), ((Double)rawget3).intValue());
                        }
                    }
                    final ObjectTooltip.LayoutItem addItem8 = layout.addItem();
                    addItem8.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_AmmoCount")), 1.0f, 1.0f, 0.8f, 1.0f);
                    addItem8.setValue(s, 1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
        final ObjectTooltip.Layout beginLayout = objectTooltip.beginLayout();
        if (this.getStock() != null) {
            final ObjectTooltip.LayoutItem addItem9 = beginLayout.addItem();
            addItem9.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_Stock")), n, n2, n3, n4);
            addItem9.setValue(this.getStock().getName(), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.getSling() != null) {
            final ObjectTooltip.LayoutItem addItem10 = beginLayout.addItem();
            addItem10.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_Sling")), n, n2, n3, n4);
            addItem10.setValue(this.getSling().getName(), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.getScope() != null) {
            final ObjectTooltip.LayoutItem addItem11 = beginLayout.addItem();
            addItem11.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_Scope")), n, n2, n3, n4);
            addItem11.setValue(this.getScope().getName(), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.getCanon() != null) {
            final ObjectTooltip.LayoutItem addItem12 = beginLayout.addItem();
            addItem12.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_Canon")), n, n2, n3, n4);
            addItem12.setValue(this.getCanon().getName(), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.getClip() != null) {
            final ObjectTooltip.LayoutItem addItem13 = beginLayout.addItem();
            addItem13.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_Clip")), n, n2, n3, n4);
            addItem13.setValue(this.getClip().getName(), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.getRecoilpad() != null) {
            final ObjectTooltip.LayoutItem addItem14 = beginLayout.addItem();
            addItem14.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_RecoilPad")), n, n2, n3, n4);
            addItem14.setValue(this.getRecoilpad().getName(), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (!beginLayout.items.isEmpty()) {
            layout.next = beginLayout;
            beginLayout.nextPadY = objectTooltip.getLineSpacing();
        }
        else {
            objectTooltip.endLayout(beginLayout);
        }
    }
    
    public float getDamageMod(final IsoGameCharacter isoGameCharacter) {
        final int perkLevel = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Blunt);
        if (this.ScriptItem.Categories.contains("Blunt")) {
            if (perkLevel >= 3 && perkLevel <= 6) {
                return 1.1f;
            }
            if (perkLevel >= 7) {
                return 1.2f;
            }
        }
        final int perkLevel2 = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Axe);
        if (this.ScriptItem.Categories.contains("Axe")) {
            if (perkLevel2 >= 3 && perkLevel2 <= 6) {
                return 1.1f;
            }
            if (perkLevel2 >= 7) {
                return 1.2f;
            }
        }
        final int perkLevel3 = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Spear);
        if (this.ScriptItem.Categories.contains("Spear")) {
            if (perkLevel3 >= 3 && perkLevel3 <= 6) {
                return 1.1f;
            }
            if (perkLevel3 >= 7) {
                return 1.2f;
            }
        }
        return 1.0f;
    }
    
    public float getRangeMod(final IsoGameCharacter isoGameCharacter) {
        final int perkLevel = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Blunt);
        if (this.ScriptItem.Categories.contains("Blunt") && perkLevel >= 7) {
            return 1.2f;
        }
        final int perkLevel2 = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Axe);
        if (this.ScriptItem.Categories.contains("Axe") && perkLevel2 >= 7) {
            return 1.2f;
        }
        final int perkLevel3 = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Spear);
        if (this.ScriptItem.Categories.contains("Spear") && perkLevel3 >= 7) {
            return 1.2f;
        }
        return 1.0f;
    }
    
    public float getFatigueMod(final IsoGameCharacter isoGameCharacter) {
        final int perkLevel = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Blunt);
        if (this.ScriptItem.Categories.contains("Blunt") && perkLevel >= 8) {
            return 0.8f;
        }
        final int perkLevel2 = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Axe);
        if (this.ScriptItem.Categories.contains("Axe") && perkLevel2 >= 8) {
            return 0.8f;
        }
        final int perkLevel3 = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Spear);
        if (this.ScriptItem.Categories.contains("Spear") && perkLevel3 >= 8) {
            return 0.8f;
        }
        return 1.0f;
    }
    
    public float getKnockbackMod(final IsoGameCharacter isoGameCharacter) {
        final int perkLevel = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Axe);
        if (this.ScriptItem.Categories.contains("Axe") && perkLevel >= 6) {
            return 2.0f;
        }
        return 1.0f;
    }
    
    public float getSpeedMod(final IsoGameCharacter isoGameCharacter) {
        if (this.ScriptItem.Categories.contains("Blunt")) {
            final int perkLevel = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Blunt);
            if (perkLevel >= 10) {
                return 0.65f;
            }
            if (perkLevel >= 9) {
                return 0.68f;
            }
            if (perkLevel >= 8) {
                return 0.71f;
            }
            if (perkLevel >= 7) {
                return 0.74f;
            }
            if (perkLevel >= 6) {
                return 0.77f;
            }
            if (perkLevel >= 5) {
                return 0.8f;
            }
            if (perkLevel >= 4) {
                return 0.83f;
            }
            if (perkLevel >= 3) {
                return 0.86f;
            }
            if (perkLevel >= 2) {
                return 0.9f;
            }
            if (perkLevel >= 1) {
                return 0.95f;
            }
        }
        if (!this.ScriptItem.Categories.contains("Axe")) {
            if (this.ScriptItem.Categories.contains("Spear")) {
                final int perkLevel2 = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Spear);
                if (perkLevel2 >= 10) {
                    return 0.65f;
                }
                if (perkLevel2 >= 9) {
                    return 0.68f;
                }
                if (perkLevel2 >= 8) {
                    return 0.71f;
                }
                if (perkLevel2 >= 7) {
                    return 0.74f;
                }
                if (perkLevel2 >= 6) {
                    return 0.77f;
                }
                if (perkLevel2 >= 5) {
                    return 0.8f;
                }
                if (perkLevel2 >= 4) {
                    return 0.83f;
                }
                if (perkLevel2 >= 3) {
                    return 0.86f;
                }
                if (perkLevel2 >= 2) {
                    return 0.9f;
                }
                if (perkLevel2 >= 1) {
                    return 0.95f;
                }
            }
            return 1.0f;
        }
        final int perkLevel3 = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Axe);
        float n = 1.0f;
        if (isoGameCharacter.Traits.Axeman.isSet()) {
            n = 0.95f;
        }
        if (perkLevel3 >= 10) {
            return 0.65f * n;
        }
        if (perkLevel3 >= 9) {
            return 0.68f * n;
        }
        if (perkLevel3 >= 8) {
            return 0.71f * n;
        }
        if (perkLevel3 >= 7) {
            return 0.74f * n;
        }
        if (perkLevel3 >= 6) {
            return 0.77f * n;
        }
        if (perkLevel3 >= 5) {
            return 0.8f * n;
        }
        if (perkLevel3 >= 4) {
            return 0.83f * n;
        }
        if (perkLevel3 >= 3) {
            return 0.86f * n;
        }
        if (perkLevel3 >= 2) {
            return 0.9f * n;
        }
        if (perkLevel3 >= 1) {
            return 0.95f * n;
        }
        return 1.0f * n;
    }
    
    public float getToHitMod(final IsoGameCharacter isoGameCharacter) {
        final int perkLevel = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Blunt);
        if (this.ScriptItem.Categories.contains("Blunt")) {
            if (perkLevel == 1) {
                return 1.2f;
            }
            if (perkLevel == 2) {
                return 1.3f;
            }
            if (perkLevel == 3) {
                return 1.4f;
            }
            if (perkLevel == 4) {
                return 1.5f;
            }
            if (perkLevel == 5) {
                return 1.6f;
            }
            if (perkLevel == 6) {
                return 1.7f;
            }
            if (perkLevel == 7) {
                return 1.8f;
            }
            if (perkLevel == 8) {
                return 1.9f;
            }
            if (perkLevel == 9) {
                return 2.0f;
            }
            if (perkLevel == 10) {
                return 100.0f;
            }
        }
        final int perkLevel2 = isoGameCharacter.getPerkLevel(PerkFactory.Perks.Axe);
        if (this.ScriptItem.Categories.contains("Axe")) {
            if (perkLevel2 == 1) {
                return 1.2f;
            }
            if (perkLevel2 == 2) {
                return 1.3f;
            }
            if (perkLevel2 == 3) {
                return 1.4f;
            }
            if (perkLevel2 == 4) {
                return 1.5f;
            }
            if (perkLevel2 == 5) {
                return 1.6f;
            }
            if (perkLevel2 == 6) {
                return 1.7f;
            }
            if (perkLevel2 == 7) {
                return 1.8f;
            }
            if (perkLevel2 == 8) {
                return 1.9f;
            }
            if (perkLevel2 == 9) {
                return 2.0f;
            }
            if (perkLevel2 == 10) {
                return 100.0f;
            }
        }
        isoGameCharacter.getPerkLevel(PerkFactory.Perks.Spear);
        if (this.ScriptItem.Categories.contains("Spear")) {
            if (perkLevel2 == 1) {
                return 1.2f;
            }
            if (perkLevel2 == 2) {
                return 1.3f;
            }
            if (perkLevel2 == 3) {
                return 1.4f;
            }
            if (perkLevel2 == 4) {
                return 1.5f;
            }
            if (perkLevel2 == 5) {
                return 1.6f;
            }
            if (perkLevel2 == 6) {
                return 1.7f;
            }
            if (perkLevel2 == 7) {
                return 1.8f;
            }
            if (perkLevel2 == 8) {
                return 1.9f;
            }
            if (perkLevel2 == 9) {
                return 2.0f;
            }
            if (perkLevel2 == 10) {
                return 100.0f;
            }
        }
        return 1.0f;
    }
    
    public boolean isAngleFalloff() {
        return this.angleFalloff;
    }
    
    public void setAngleFalloff(final boolean angleFalloff) {
        this.angleFalloff = angleFalloff;
    }
    
    public boolean isCanBarracade() {
        return this.bCanBarracade;
    }
    
    public void setCanBarracade(final boolean bCanBarracade) {
        this.bCanBarracade = bCanBarracade;
    }
    
    public float getDoSwingBeforeImpact() {
        return this.doSwingBeforeImpact;
    }
    
    public void setDoSwingBeforeImpact(final float doSwingBeforeImpact) {
        this.doSwingBeforeImpact = doSwingBeforeImpact;
    }
    
    public String getImpactSound() {
        return this.impactSound;
    }
    
    public void setImpactSound(final String impactSound) {
        this.impactSound = impactSound;
    }
    
    public boolean isKnockBackOnNoDeath() {
        return this.knockBackOnNoDeath;
    }
    
    public void setKnockBackOnNoDeath(final boolean knockBackOnNoDeath) {
        this.knockBackOnNoDeath = knockBackOnNoDeath;
    }
    
    public float getMaxAngle() {
        return this.maxAngle;
    }
    
    public void setMaxAngle(final float maxAngle) {
        this.maxAngle = maxAngle;
    }
    
    public float getMaxDamage() {
        return this.maxDamage;
    }
    
    public void setMaxDamage(final float maxDamage) {
        this.maxDamage = maxDamage;
    }
    
    public int getMaxHitCount() {
        return this.maxHitCount;
    }
    
    public void setMaxHitCount(final int maxHitCount) {
        this.maxHitCount = maxHitCount;
    }
    
    public float getMaxRange() {
        return this.maxRange;
    }
    
    public float getMaxRange(final IsoGameCharacter isoGameCharacter) {
        if (this.isRanged()) {
            return this.maxRange + this.getAimingPerkRangeModifier() * (isoGameCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 2.0f);
        }
        return this.maxRange;
    }
    
    public void setMaxRange(final float maxRange) {
        this.maxRange = maxRange;
    }
    
    public boolean isRanged() {
        return this.ranged;
    }
    
    public void setRanged(final boolean ranged) {
        this.ranged = ranged;
    }
    
    public float getMinAngle() {
        return this.minAngle;
    }
    
    public void setMinAngle(final float minAngle) {
        this.minAngle = minAngle;
    }
    
    public float getMinDamage() {
        return this.minDamage;
    }
    
    public void setMinDamage(final float minDamage) {
        this.minDamage = minDamage;
    }
    
    public float getMinimumSwingTime() {
        return this.minimumSwingTime;
    }
    
    public void setMinimumSwingTime(final float minimumSwingTime) {
        this.minimumSwingTime = minimumSwingTime;
    }
    
    public float getMinRange() {
        return this.minRange;
    }
    
    public void setMinRange(final float minRange) {
        this.minRange = minRange;
    }
    
    public float getNoiseFactor() {
        return this.noiseFactor;
    }
    
    public void setNoiseFactor(final float noiseFactor) {
        this.noiseFactor = noiseFactor;
    }
    
    public String getOtherHandRequire() {
        return this.otherHandRequire;
    }
    
    public void setOtherHandRequire(final String otherHandRequire) {
        this.otherHandRequire = otherHandRequire;
    }
    
    public boolean isOtherHandUse() {
        return this.otherHandUse;
    }
    
    public void setOtherHandUse(final boolean otherHandUse) {
        this.otherHandUse = otherHandUse;
    }
    
    public String getPhysicsObject() {
        return this.physicsObject;
    }
    
    public void setPhysicsObject(final String physicsObject) {
        this.physicsObject = physicsObject;
    }
    
    public float getPushBackMod() {
        return this.pushBackMod;
    }
    
    public void setPushBackMod(final float pushBackMod) {
        this.pushBackMod = pushBackMod;
    }
    
    public boolean isRangeFalloff() {
        return this.rangeFalloff;
    }
    
    public void setRangeFalloff(final boolean rangeFalloff) {
        this.rangeFalloff = rangeFalloff;
    }
    
    public boolean isShareDamage() {
        return this.shareDamage;
    }
    
    public void setShareDamage(final boolean shareDamage) {
        this.shareDamage = shareDamage;
    }
    
    public int getSoundRadius() {
        return this.soundRadius;
    }
    
    public void setSoundRadius(final int soundRadius) {
        this.soundRadius = soundRadius;
    }
    
    public int getSoundVolume() {
        return this.soundVolume;
    }
    
    public void setSoundVolume(final int soundVolume) {
        this.soundVolume = soundVolume;
    }
    
    public boolean isSplatBloodOnNoDeath() {
        return this.splatBloodOnNoDeath;
    }
    
    public void setSplatBloodOnNoDeath(final boolean splatBloodOnNoDeath) {
        this.splatBloodOnNoDeath = splatBloodOnNoDeath;
    }
    
    public int getSplatNumber() {
        return this.splatNumber;
    }
    
    public void setSplatNumber(final int splatNumber) {
        this.splatNumber = splatNumber;
    }
    
    public String getSwingSound() {
        return this.swingSound;
    }
    
    public void setSwingSound(final String swingSound) {
        this.swingSound = swingSound;
    }
    
    public float getSwingTime() {
        return this.swingTime;
    }
    
    public void setSwingTime(final float swingTime) {
        this.swingTime = swingTime;
    }
    
    public float getToHitModifier() {
        return this.toHitModifier;
    }
    
    public void setToHitModifier(final float toHitModifier) {
        this.toHitModifier = toHitModifier;
    }
    
    public boolean isUseEndurance() {
        return this.useEndurance;
    }
    
    public void setUseEndurance(final boolean useEndurance) {
        this.useEndurance = useEndurance;
    }
    
    public boolean isUseSelf() {
        return this.useSelf;
    }
    
    public void setUseSelf(final boolean useSelf) {
        this.useSelf = useSelf;
    }
    
    public String getWeaponSprite() {
        return this.weaponSprite;
    }
    
    public void setWeaponSprite(final String weaponSprite) {
        this.weaponSprite = weaponSprite;
    }
    
    public float getOtherBoost() {
        return this.otherBoost;
    }
    
    public void setOtherBoost(final float otherBoost) {
        this.otherBoost = otherBoost;
    }
    
    public int getDoorDamage() {
        return this.DoorDamage;
    }
    
    public void setDoorDamage(final int doorDamage) {
        this.DoorDamage = doorDamage;
    }
    
    public String getDoorHitSound() {
        return this.doorHitSound;
    }
    
    public void setDoorHitSound(final String doorHitSound) {
        this.doorHitSound = doorHitSound;
    }
    
    public int getConditionLowerChance() {
        return this.ConditionLowerChance;
    }
    
    public void setConditionLowerChance(final int conditionLowerChance) {
        this.ConditionLowerChance = conditionLowerChance;
    }
    
    public boolean isMultipleHitConditionAffected() {
        return this.MultipleHitConditionAffected;
    }
    
    public void setMultipleHitConditionAffected(final boolean multipleHitConditionAffected) {
        this.MultipleHitConditionAffected = multipleHitConditionAffected;
    }
    
    public boolean isShareEndurance() {
        return this.shareEndurance;
    }
    
    public void setShareEndurance(final boolean shareEndurance) {
        this.shareEndurance = shareEndurance;
    }
    
    public boolean isAlwaysKnockdown() {
        return this.AlwaysKnockdown;
    }
    
    public void setAlwaysKnockdown(final boolean alwaysKnockdown) {
        this.AlwaysKnockdown = alwaysKnockdown;
    }
    
    public float getEnduranceMod() {
        return this.EnduranceMod;
    }
    
    public void setEnduranceMod(final float enduranceMod) {
        this.EnduranceMod = enduranceMod;
    }
    
    public float getKnockdownMod() {
        return this.KnockdownMod;
    }
    
    public void setKnockdownMod(final float knockdownMod) {
        this.KnockdownMod = knockdownMod;
    }
    
    public boolean isCantAttackWithLowestEndurance() {
        return this.CantAttackWithLowestEndurance;
    }
    
    public void setCantAttackWithLowestEndurance(final boolean cantAttackWithLowestEndurance) {
        this.CantAttackWithLowestEndurance = cantAttackWithLowestEndurance;
    }
    
    public boolean isAimedFirearm() {
        return this.bIsAimedFirearm;
    }
    
    public boolean isAimedHandWeapon() {
        return this.bIsAimedHandWeapon;
    }
    
    public int getProjectileCount() {
        return this.ProjectileCount;
    }
    
    public void setProjectileCount(final int projectileCount) {
        this.ProjectileCount = projectileCount;
    }
    
    public float getAimingMod() {
        return this.aimingMod;
    }
    
    public boolean isAimed() {
        return this.bIsAimedFirearm || this.bIsAimedHandWeapon;
    }
    
    public void setCriticalChance(final float criticalChance) {
        this.CriticalChance = criticalChance;
    }
    
    public float getCriticalChance() {
        return this.CriticalChance;
    }
    
    public void setSubCategory(final String subCategory) {
        this.SubCategory = subCategory;
    }
    
    public String getSubCategory() {
        return this.SubCategory;
    }
    
    public void setZombieHitSound(final String hitSound) {
        this.hitSound = hitSound;
    }
    
    public String getZombieHitSound() {
        return this.hitSound;
    }
    
    public ArrayList<String> getCategories() {
        return this.Categories;
    }
    
    public void setCategories(final ArrayList<String> categories) {
        this.Categories = categories;
    }
    
    public int getAimingPerkCritModifier() {
        return this.AimingPerkCritModifier;
    }
    
    public void setAimingPerkCritModifier(final int aimingPerkCritModifier) {
        this.AimingPerkCritModifier = aimingPerkCritModifier;
    }
    
    public float getAimingPerkRangeModifier() {
        return this.AimingPerkRangeModifier;
    }
    
    public void setAimingPerkRangeModifier(final float aimingPerkRangeModifier) {
        this.AimingPerkRangeModifier = aimingPerkRangeModifier;
    }
    
    public int getHitChance() {
        return this.HitChance;
    }
    
    public void setHitChance(final int hitChance) {
        this.HitChance = hitChance;
    }
    
    public float getAimingPerkHitChanceModifier() {
        return this.AimingPerkHitChanceModifier;
    }
    
    public void setAimingPerkHitChanceModifier(final float aimingPerkHitChanceModifier) {
        this.AimingPerkHitChanceModifier = aimingPerkHitChanceModifier;
    }
    
    public float getAimingPerkMinAngleModifier() {
        return this.AimingPerkMinAngleModifier;
    }
    
    public void setAimingPerkMinAngleModifier(final float aimingPerkMinAngleModifier) {
        this.AimingPerkMinAngleModifier = aimingPerkMinAngleModifier;
    }
    
    public int getRecoilDelay() {
        return this.RecoilDelay;
    }
    
    public void setRecoilDelay(final int recoilDelay) {
        this.RecoilDelay = recoilDelay;
    }
    
    public boolean isPiercingBullets() {
        return this.PiercingBullets;
    }
    
    public void setPiercingBullets(final boolean piercingBullets) {
        this.PiercingBullets = piercingBullets;
    }
    
    public float getSoundGain() {
        return this.soundGain;
    }
    
    public void setSoundGain(final float soundGain) {
        this.soundGain = soundGain;
    }
    
    public WeaponPart getScope() {
        return this.scope;
    }
    
    public void setScope(final WeaponPart scope) {
        this.scope = scope;
    }
    
    public WeaponPart getClip() {
        return this.clip;
    }
    
    public void setClip(final WeaponPart clip) {
        this.clip = clip;
    }
    
    public WeaponPart getCanon() {
        return this.canon;
    }
    
    public void setCanon(final WeaponPart canon) {
        this.canon = canon;
    }
    
    public WeaponPart getRecoilpad() {
        return this.recoilpad;
    }
    
    public void setRecoilpad(final WeaponPart recoilpad) {
        this.recoilpad = recoilpad;
    }
    
    public int getClipSize() {
        return this.ClipSize;
    }
    
    public void setClipSize(final int clipSize) {
        this.ClipSize = clipSize;
        this.getModData().rawset((Object)"maxCapacity", (Object)BoxedStaticValues.toDouble(clipSize));
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        final BitHeaderWrite allocWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Integer, byteBuffer);
        if (this.maxRange != 1.0f) {
            allocWrite.addFlags(1);
            byteBuffer.putFloat(this.maxRange);
        }
        if (this.minRangeRanged != 0.0f) {
            allocWrite.addFlags(2);
            byteBuffer.putFloat(this.minRangeRanged);
        }
        if (this.ClipSize != 0) {
            allocWrite.addFlags(4);
            byteBuffer.putInt(this.ClipSize);
        }
        if (this.minDamage != 0.4f) {
            allocWrite.addFlags(8);
            byteBuffer.putFloat(this.minDamage);
        }
        if (this.maxDamage != 1.5f) {
            allocWrite.addFlags(16);
            byteBuffer.putFloat(this.maxDamage);
        }
        if (this.RecoilDelay != 0) {
            allocWrite.addFlags(32);
            byteBuffer.putInt(this.RecoilDelay);
        }
        if (this.aimingTime != 0) {
            allocWrite.addFlags(64);
            byteBuffer.putInt(this.aimingTime);
        }
        if (this.reloadTime != 0) {
            allocWrite.addFlags(128);
            byteBuffer.putInt(this.reloadTime);
        }
        if (this.HitChance != 0) {
            allocWrite.addFlags(256);
            byteBuffer.putInt(this.HitChance);
        }
        if (this.minAngle != 0.5f) {
            allocWrite.addFlags(512);
            byteBuffer.putFloat(this.minAngle);
        }
        if (this.getScope() != null) {
            allocWrite.addFlags(1024);
            byteBuffer.putShort(this.getScope().getRegistry_id());
        }
        if (this.getClip() != null) {
            allocWrite.addFlags(2048);
            byteBuffer.putShort(this.getClip().getRegistry_id());
        }
        if (this.getRecoilpad() != null) {
            allocWrite.addFlags(4096);
            byteBuffer.putShort(this.getRecoilpad().getRegistry_id());
        }
        if (this.getSling() != null) {
            allocWrite.addFlags(8192);
            byteBuffer.putShort(this.getSling().getRegistry_id());
        }
        if (this.getStock() != null) {
            allocWrite.addFlags(16384);
            byteBuffer.putShort(this.getStock().getRegistry_id());
        }
        if (this.getCanon() != null) {
            allocWrite.addFlags(32768);
            byteBuffer.putShort(this.getCanon().getRegistry_id());
        }
        if (this.getExplosionTimer() != 0) {
            allocWrite.addFlags(65536);
            byteBuffer.putInt(this.getExplosionTimer());
        }
        if (this.maxAngle != 1.0f) {
            allocWrite.addFlags(131072);
            byteBuffer.putFloat(this.maxAngle);
        }
        if (this.bloodLevel != 0.0f) {
            allocWrite.addFlags(262144);
            byteBuffer.putFloat(this.bloodLevel);
        }
        if (this.containsClip) {
            allocWrite.addFlags(524288);
        }
        if (this.roundChambered) {
            allocWrite.addFlags(1048576);
        }
        if (this.isJammed) {
            allocWrite.addFlags(2097152);
        }
        allocWrite.write();
        allocWrite.release();
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        super.load(byteBuffer, n);
        final BitHeaderRead allocRead = BitHeader.allocRead(BitHeader.HeaderSize.Integer, byteBuffer);
        if (!allocRead.equals(0)) {
            if (allocRead.hasFlags(1)) {
                this.setMaxRange(byteBuffer.getFloat());
            }
            if (allocRead.hasFlags(2)) {
                this.setMinRangeRanged(byteBuffer.getFloat());
            }
            if (allocRead.hasFlags(4)) {
                this.setClipSize(byteBuffer.getInt());
            }
            if (allocRead.hasFlags(8)) {
                this.setMinDamage(byteBuffer.getFloat());
            }
            if (allocRead.hasFlags(16)) {
                this.setMaxDamage(byteBuffer.getFloat());
            }
            if (allocRead.hasFlags(32)) {
                this.setRecoilDelay(byteBuffer.getInt());
            }
            if (allocRead.hasFlags(64)) {
                this.setAimingTime(byteBuffer.getInt());
            }
            if (allocRead.hasFlags(128)) {
                this.setReloadTime(byteBuffer.getInt());
            }
            if (allocRead.hasFlags(256)) {
                this.setHitChance(byteBuffer.getInt());
            }
            if (allocRead.hasFlags(512)) {
                this.setMinAngle(byteBuffer.getFloat());
            }
            if (allocRead.hasFlags(1024)) {
                final InventoryItem createItem = InventoryItemFactory.CreateItem(byteBuffer.getShort());
                if (createItem != null && createItem instanceof WeaponPart) {
                    this.attachWeaponPart((WeaponPart)createItem, false);
                }
            }
            if (allocRead.hasFlags(2048)) {
                final InventoryItem createItem2 = InventoryItemFactory.CreateItem(byteBuffer.getShort());
                if (createItem2 != null && createItem2 instanceof WeaponPart) {
                    this.attachWeaponPart((WeaponPart)createItem2, false);
                }
            }
            if (allocRead.hasFlags(4096)) {
                final InventoryItem createItem3 = InventoryItemFactory.CreateItem(byteBuffer.getShort());
                if (createItem3 != null && createItem3 instanceof WeaponPart) {
                    this.attachWeaponPart((WeaponPart)createItem3, false);
                }
            }
            if (allocRead.hasFlags(8192)) {
                final InventoryItem createItem4 = InventoryItemFactory.CreateItem(byteBuffer.getShort());
                if (createItem4 != null && createItem4 instanceof WeaponPart) {
                    this.attachWeaponPart((WeaponPart)createItem4, false);
                }
            }
            if (allocRead.hasFlags(16384)) {
                final InventoryItem createItem5 = InventoryItemFactory.CreateItem(byteBuffer.getShort());
                if (createItem5 != null && createItem5 instanceof WeaponPart) {
                    this.attachWeaponPart((WeaponPart)createItem5, false);
                }
            }
            if (allocRead.hasFlags(32768)) {
                final InventoryItem createItem6 = InventoryItemFactory.CreateItem(byteBuffer.getShort());
                if (createItem6 != null && createItem6 instanceof WeaponPart) {
                    this.attachWeaponPart((WeaponPart)createItem6, false);
                }
            }
            if (allocRead.hasFlags(65536)) {
                this.setExplosionTimer(byteBuffer.getInt());
            }
            if (allocRead.hasFlags(131072)) {
                this.setMaxAngle(byteBuffer.getFloat());
            }
            if (allocRead.hasFlags(262144)) {
                this.setBloodLevel(byteBuffer.getFloat());
            }
            this.setContainsClip(allocRead.hasFlags(524288));
            if (StringUtils.isNullOrWhitespace(this.magazineType)) {
                this.setContainsClip(false);
            }
            this.setRoundChambered(allocRead.hasFlags(1048576));
            this.setJammed(allocRead.hasFlags(2097152));
        }
        allocRead.release();
    }
    
    public float getMinRangeRanged() {
        return this.minRangeRanged;
    }
    
    public void setMinRangeRanged(final float minRangeRanged) {
        this.minRangeRanged = minRangeRanged;
    }
    
    public int getReloadTime() {
        return this.reloadTime;
    }
    
    public void setReloadTime(final int reloadTime) {
        this.reloadTime = reloadTime;
    }
    
    public WeaponPart getSling() {
        return this.sling;
    }
    
    public void setSling(final WeaponPart sling) {
        this.sling = sling;
    }
    
    public int getAimingTime() {
        return this.aimingTime;
    }
    
    public void setAimingTime(final int aimingTime) {
        this.aimingTime = aimingTime;
    }
    
    public WeaponPart getStock() {
        return this.stock;
    }
    
    public void setStock(final WeaponPart stock) {
        this.stock = stock;
    }
    
    public int getTreeDamage() {
        return this.treeDamage;
    }
    
    public void setTreeDamage(final int treeDamage) {
        this.treeDamage = treeDamage;
    }
    
    public String getBulletOutSound() {
        return this.bulletOutSound;
    }
    
    public void setBulletOutSound(final String bulletOutSound) {
        this.bulletOutSound = bulletOutSound;
    }
    
    public String getShellFallSound() {
        return this.shellFallSound;
    }
    
    public void setShellFallSound(final String shellFallSound) {
        this.shellFallSound = shellFallSound;
    }
    
    private void addPartToList(final String s, final ArrayList<WeaponPart> list) {
        final WeaponPart weaponPart = this.getWeaponPart(s);
        if (weaponPart != null) {
            list.add(weaponPart);
        }
    }
    
    public ArrayList<WeaponPart> getAllWeaponParts() {
        final ArrayList<WeaponPart> list = new ArrayList<WeaponPart>();
        this.addPartToList("Scope", list);
        this.addPartToList("Clip", list);
        this.addPartToList("Sling", list);
        this.addPartToList("Canon", list);
        this.addPartToList("Stock", list);
        this.addPartToList("RecoilPad", list);
        return list;
    }
    
    public void setWeaponPart(final String s, final WeaponPart weaponPart) {
        if (weaponPart != null && !s.equalsIgnoreCase(weaponPart.getPartType())) {
            return;
        }
        if ("Scope".equalsIgnoreCase(s)) {
            this.scope = weaponPart;
        }
        else if ("Clip".equalsIgnoreCase(s)) {
            this.clip = weaponPart;
        }
        else if ("Sling".equalsIgnoreCase(s)) {
            this.sling = weaponPart;
        }
        else if ("Canon".equalsIgnoreCase(s)) {
            this.canon = weaponPart;
        }
        else if ("Stock".equalsIgnoreCase(s)) {
            this.stock = weaponPart;
        }
        else if ("RecoilPad".equalsIgnoreCase(s)) {
            this.recoilpad = weaponPart;
        }
        else {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
    }
    
    public WeaponPart getWeaponPart(final String s) {
        if ("Scope".equalsIgnoreCase(s)) {
            return this.scope;
        }
        if ("Clip".equalsIgnoreCase(s)) {
            return this.clip;
        }
        if ("Sling".equalsIgnoreCase(s)) {
            return this.sling;
        }
        if ("Canon".equalsIgnoreCase(s)) {
            return this.canon;
        }
        if ("Stock".equalsIgnoreCase(s)) {
            return this.stock;
        }
        if ("RecoilPad".equalsIgnoreCase(s)) {
            return this.recoilpad;
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        return null;
    }
    
    public void attachWeaponPart(final WeaponPart weaponPart) {
        this.attachWeaponPart(weaponPart, true);
    }
    
    public void attachWeaponPart(final WeaponPart weaponPart, final boolean b) {
        if (weaponPart == null) {
            return;
        }
        final WeaponPart weaponPart2 = this.getWeaponPart(weaponPart.getPartType());
        if (weaponPart2 != null) {
            this.detachWeaponPart(weaponPart2);
        }
        this.setWeaponPart(weaponPart.getPartType(), weaponPart);
        if (b) {
            this.setMaxRange(this.getMaxRange() + weaponPart.getMaxRange());
            this.setMinRangeRanged(this.getMinRangeRanged() + weaponPart.getMinRangeRanged());
            this.setClipSize(this.getClipSize() + weaponPart.getClipSize());
            this.setReloadTime(this.getReloadTime() + weaponPart.getReloadTime());
            this.setRecoilDelay((int)(this.getRecoilDelay() + weaponPart.getRecoilDelay()));
            this.setAimingTime(this.getAimingTime() + weaponPart.getAimingTime());
            this.setHitChance(this.getHitChance() + weaponPart.getHitChance());
            this.setMinAngle(this.getMinAngle() + weaponPart.getAngle());
            this.setActualWeight(this.getActualWeight() + weaponPart.getWeightModifier());
            this.setWeight(this.getWeight() + weaponPart.getWeightModifier());
            this.setMinDamage(this.getMinDamage() + weaponPart.getDamage());
            this.setMaxDamage(this.getMaxDamage() + weaponPart.getDamage());
        }
    }
    
    public void detachWeaponPart(final WeaponPart weaponPart) {
        if (weaponPart == null) {
            return;
        }
        if (this.getWeaponPart(weaponPart.getPartType()) != weaponPart) {
            return;
        }
        this.setWeaponPart(weaponPart.getPartType(), null);
        this.setMaxRange(this.getMaxRange() - weaponPart.getMaxRange());
        this.setMinRangeRanged(this.getMinRangeRanged() - weaponPart.getMinRangeRanged());
        this.setClipSize(this.getClipSize() - weaponPart.getClipSize());
        this.setReloadTime(this.getReloadTime() - weaponPart.getReloadTime());
        this.setRecoilDelay((int)(this.getRecoilDelay() - weaponPart.getRecoilDelay()));
        this.setAimingTime(this.getAimingTime() - weaponPart.getAimingTime());
        this.setHitChance(this.getHitChance() - weaponPart.getHitChance());
        this.setMinAngle(this.getMinAngle() - weaponPart.getAngle());
        this.setActualWeight(this.getActualWeight() - weaponPart.getWeightModifier());
        this.setWeight(this.getWeight() - weaponPart.getWeightModifier());
        this.setMinDamage(this.getMinDamage() - weaponPart.getDamage());
        this.setMaxDamage(this.getMaxDamage() - weaponPart.getDamage());
    }
    
    public int getTriggerExplosionTimer() {
        return this.triggerExplosionTimer;
    }
    
    public void setTriggerExplosionTimer(final int triggerExplosionTimer) {
        this.triggerExplosionTimer = triggerExplosionTimer;
    }
    
    public boolean canBePlaced() {
        return this.canBePlaced;
    }
    
    public void setCanBePlaced(final boolean canBePlaced) {
        this.canBePlaced = canBePlaced;
    }
    
    public int getExplosionRange() {
        return this.explosionRange;
    }
    
    public void setExplosionRange(final int explosionRange) {
        this.explosionRange = explosionRange;
    }
    
    public int getExplosionPower() {
        return this.explosionPower;
    }
    
    public void setExplosionPower(final int explosionPower) {
        this.explosionPower = explosionPower;
    }
    
    public int getFireRange() {
        return this.fireRange;
    }
    
    public void setFireRange(final int fireRange) {
        this.fireRange = fireRange;
    }
    
    public int getSmokeRange() {
        return this.smokeRange;
    }
    
    public void setSmokeRange(final int smokeRange) {
        this.smokeRange = smokeRange;
    }
    
    public int getFirePower() {
        return this.firePower;
    }
    
    public void setFirePower(final int firePower) {
        this.firePower = firePower;
    }
    
    public int getNoiseRange() {
        return this.noiseRange;
    }
    
    public void setNoiseRange(final int noiseRange) {
        this.noiseRange = noiseRange;
    }
    
    public int getNoiseDuration() {
        return this.getScriptItem().getNoiseDuration();
    }
    
    public float getExtraDamage() {
        return this.extraDamage;
    }
    
    public void setExtraDamage(final float extraDamage) {
        this.extraDamage = extraDamage;
    }
    
    public int getExplosionTimer() {
        return this.explosionTimer;
    }
    
    public void setExplosionTimer(final int explosionTimer) {
        this.explosionTimer = explosionTimer;
    }
    
    public String getPlacedSprite() {
        return this.placedSprite;
    }
    
    public void setPlacedSprite(final String placedSprite) {
        this.placedSprite = placedSprite;
    }
    
    public boolean canBeReused() {
        return this.canBeReused;
    }
    
    public void setCanBeReused(final boolean canBeReused) {
        this.canBeReused = canBeReused;
    }
    
    public int getSensorRange() {
        return this.sensorRange;
    }
    
    public void setSensorRange(final int sensorRange) {
        this.sensorRange = sensorRange;
    }
    
    public String getRunAnim() {
        return this.RunAnim;
    }
    
    public float getCritDmgMultiplier() {
        return this.critDmgMultiplier;
    }
    
    public void setCritDmgMultiplier(final float critDmgMultiplier) {
        this.critDmgMultiplier = critDmgMultiplier;
    }
    
    @Override
    public String getStaticModel() {
        return (this.staticModel != null) ? this.staticModel : this.weaponSprite;
    }
    
    public float getBaseSpeed() {
        return this.baseSpeed;
    }
    
    public void setBaseSpeed(final float baseSpeed) {
        this.baseSpeed = baseSpeed;
    }
    
    public float getBloodLevel() {
        return this.bloodLevel;
    }
    
    public void setBloodLevel(final float b) {
        this.bloodLevel = Math.max(0.0f, Math.min(1.0f, b));
    }
    
    public void setWeaponLength(final float weaponLength) {
        this.WeaponLength = weaponLength;
    }
    
    public String getAmmoBox() {
        return this.ammoBox;
    }
    
    public void setAmmoBox(final String ammoBox) {
        this.ammoBox = ammoBox;
    }
    
    public String getMagazineType() {
        return this.magazineType;
    }
    
    public void setMagazineType(final String magazineType) {
        this.magazineType = magazineType;
    }
    
    public String getEjectAmmoStartSound() {
        return this.getScriptItem().getEjectAmmoStartSound();
    }
    
    public String getEjectAmmoSound() {
        return this.getScriptItem().getEjectAmmoSound();
    }
    
    public String getEjectAmmoStopSound() {
        return this.getScriptItem().getEjectAmmoStopSound();
    }
    
    public String getInsertAmmoStartSound() {
        return this.getScriptItem().getInsertAmmoStartSound();
    }
    
    public String getInsertAmmoSound() {
        return this.getScriptItem().getInsertAmmoSound();
    }
    
    public String getInsertAmmoStopSound() {
        return this.getScriptItem().getInsertAmmoStopSound();
    }
    
    public String getRackSound() {
        return this.rackSound;
    }
    
    public void setRackSound(final String rackSound) {
        this.rackSound = rackSound;
    }
    
    public boolean isReloadable(final IsoGameCharacter isoGameCharacter) {
        return this.isRanged();
    }
    
    public boolean isContainsClip() {
        return this.containsClip;
    }
    
    public void setContainsClip(final boolean containsClip) {
        this.containsClip = containsClip;
    }
    
    public InventoryItem getBestMagazine(final IsoGameCharacter isoGameCharacter) {
        if (StringUtils.isNullOrEmpty(this.getMagazineType())) {
            return null;
        }
        final InventoryItem bestTypeRecurse = isoGameCharacter.getInventory().getBestTypeRecurse(this.getMagazineType(), (inventoryItem, inventoryItem2) -> inventoryItem.getCurrentAmmoCount() - inventoryItem2.getCurrentAmmoCount());
        if (bestTypeRecurse == null || bestTypeRecurse.getCurrentAmmoCount() == 0) {
            return null;
        }
        return bestTypeRecurse;
    }
    
    public String getWeaponReloadType() {
        return this.weaponReloadType;
    }
    
    public void setWeaponReloadType(final String weaponReloadType) {
        this.weaponReloadType = weaponReloadType;
    }
    
    public boolean isRackAfterShoot() {
        return this.rackAfterShoot;
    }
    
    public void setRackAfterShoot(final boolean rackAfterShoot) {
        this.rackAfterShoot = rackAfterShoot;
    }
    
    public boolean isRoundChambered() {
        return this.roundChambered;
    }
    
    public void setRoundChambered(final boolean roundChambered) {
        this.roundChambered = roundChambered;
    }
    
    public boolean isSpentRoundChambered() {
        return this.bSpentRoundChambered;
    }
    
    public void setSpentRoundChambered(final boolean bSpentRoundChambered) {
        this.bSpentRoundChambered = bSpentRoundChambered;
    }
    
    public int getSpentRoundCount() {
        return this.spentRoundCount;
    }
    
    public void setSpentRoundCount(final int n) {
        this.spentRoundCount = PZMath.clamp(n, 0, this.getMaxAmmo());
    }
    
    public boolean isManuallyRemoveSpentRounds() {
        return this.getScriptItem().isManuallyRemoveSpentRounds();
    }
    
    public int getAmmoPerShoot() {
        return this.ammoPerShoot;
    }
    
    public void setAmmoPerShoot(final int ammoPerShoot) {
        this.ammoPerShoot = ammoPerShoot;
    }
    
    public float getJamGunChance() {
        return this.jamGunChance;
    }
    
    public void setJamGunChance(final float jamGunChance) {
        this.jamGunChance = jamGunChance;
    }
    
    public boolean isJammed() {
        return this.isJammed;
    }
    
    public void setJammed(final boolean isJammed) {
        this.isJammed = isJammed;
    }
    
    public String getClickSound() {
        return this.clickSound;
    }
    
    public void setClickSound(final String clickSound) {
        this.clickSound = clickSound;
    }
    
    public ArrayList<ModelWeaponPart> getModelWeaponPart() {
        return this.modelWeaponPart;
    }
    
    public void setModelWeaponPart(final ArrayList<ModelWeaponPart> modelWeaponPart) {
        this.modelWeaponPart = modelWeaponPart;
    }
    
    public String getOriginalWeaponSprite() {
        return this.originalWeaponSprite;
    }
    
    public void setOriginalWeaponSprite(final String originalWeaponSprite) {
        this.originalWeaponSprite = originalWeaponSprite;
    }
    
    public boolean haveChamber() {
        return this.haveChamber;
    }
    
    public void setHaveChamber(final boolean haveChamber) {
        this.haveChamber = haveChamber;
    }
    
    public String getDamageCategory() {
        return this.damageCategory;
    }
    
    public void setDamageCategory(final String damageCategory) {
        this.damageCategory = damageCategory;
    }
    
    public boolean isDamageMakeHole() {
        return this.damageMakeHole;
    }
    
    public void setDamageMakeHole(final boolean damageMakeHole) {
        this.damageMakeHole = damageMakeHole;
    }
    
    public String getHitFloorSound() {
        return this.hitFloorSound;
    }
    
    public void setHitFloorSound(final String hitFloorSound) {
        this.hitFloorSound = hitFloorSound;
    }
    
    public boolean isInsertAllBulletsReload() {
        return this.insertAllBulletsReload;
    }
    
    public void setInsertAllBulletsReload(final boolean insertAllBulletsReload) {
        this.insertAllBulletsReload = insertAllBulletsReload;
    }
    
    public String getFireMode() {
        return this.fireMode;
    }
    
    public void setFireMode(final String fireMode) {
        this.fireMode = fireMode;
    }
    
    public ArrayList<String> getFireModePossibilities() {
        return this.fireModePossibilities;
    }
    
    public void setFireModePossibilities(final ArrayList<String> fireModePossibilities) {
        this.fireModePossibilities = fireModePossibilities;
    }
    
    public void randomizeBullets() {
        if (!this.isRanged() || Rand.NextBool(4)) {
            return;
        }
        this.setCurrentAmmoCount(Rand.Next(this.getMaxAmmo() - 2, this.getMaxAmmo()));
        if (!StringUtils.isNullOrEmpty(this.getMagazineType())) {
            this.setContainsClip(true);
        }
        if (this.haveChamber()) {
            this.setRoundChambered(true);
        }
    }
    
    public float getStopPower() {
        return this.getScriptItem().stopPower;
    }
    
    public boolean isInstantExplosion() {
        return this.explosionTimer <= 0 && this.sensorRange <= 0 && this.getRemoteControlID() == -1;
    }
}
