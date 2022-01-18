// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import zombie.util.StringUtils;
import zombie.characterTextures.BloodClothingType;
import java.util.ArrayList;
import zombie.network.GameClient;
import zombie.characters.skills.PerkFactory;
import zombie.characterTextures.BloodBodyPartType;
import zombie.core.math.PZMath;
import zombie.util.io.BitHeaderRead;
import java.io.IOException;
import java.util.Iterator;
import zombie.util.io.BitHeaderWrite;
import zombie.GameWindow;
import zombie.util.io.BitHeader;
import java.nio.ByteBuffer;
import zombie.inventory.InventoryItemFactory;
import zombie.vehicles.VehicleWindow;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.BaseVehicle;
import zombie.iso.objects.IsoClothingWasher;
import zombie.iso.objects.IsoClothingDryer;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.weather.ClimateManager;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.characters.WornItems.WornItem;
import zombie.characters.WornItems.WornItems;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import zombie.ui.ObjectTooltip;
import zombie.iso.IsoWorld;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoGameCharacter;
import zombie.scripting.objects.Item;
import zombie.core.Color;
import zombie.core.Rand;
import zombie.core.Translator;
import java.util.HashMap;
import zombie.inventory.InventoryItem;

public class Clothing extends InventoryItem
{
    private float temperature;
    private float insulation;
    private float windresistance;
    private float waterResistance;
    HashMap<Integer, ClothingPatch> patches;
    protected String SpriteName;
    protected String palette;
    public float bloodLevel;
    private float dirtyness;
    private float wetness;
    private float WeightWet;
    private float lastWetnessUpdate;
    private final String dirtyString;
    private final String bloodyString;
    private final String wetString;
    private final String soakedString;
    private final String wornString;
    private int ConditionLowerChance;
    private float stompPower;
    private float runSpeedModifier;
    private float combatSpeedModifier;
    private Boolean removeOnBroken;
    private Boolean canHaveHoles;
    private float biteDefense;
    private float scratchDefense;
    private float bulletDefense;
    public static final int CONDITION_PER_HOLES = 3;
    private float neckProtectionModifier;
    private int chanceToFall;
    
    @Override
    public String getCategory() {
        if (this.mainCategory != null) {
            return this.mainCategory;
        }
        return "Clothing";
    }
    
    public Clothing(final String s, final String s2, final String s3, final String s4, final String palette, final String spriteName) {
        super(s, s2, s3, s4);
        this.insulation = 0.0f;
        this.windresistance = 0.0f;
        this.waterResistance = 0.0f;
        this.SpriteName = null;
        this.bloodLevel = 0.0f;
        this.dirtyness = 0.0f;
        this.wetness = 0.0f;
        this.WeightWet = 0.0f;
        this.lastWetnessUpdate = -1.0f;
        this.dirtyString = Translator.getText("IGUI_ClothingName_Dirty");
        this.bloodyString = Translator.getText("IGUI_ClothingName_Bloody");
        this.wetString = Translator.getText("IGUI_ClothingName_Wet");
        this.soakedString = Translator.getText("IGUI_ClothingName_Soaked");
        this.wornString = Translator.getText("IGUI_ClothingName_Worn");
        this.ConditionLowerChance = 10000;
        this.stompPower = 1.0f;
        this.runSpeedModifier = 1.0f;
        this.combatSpeedModifier = 1.0f;
        this.removeOnBroken = false;
        this.canHaveHoles = true;
        this.biteDefense = 0.0f;
        this.scratchDefense = 0.0f;
        this.bulletDefense = 0.0f;
        this.neckProtectionModifier = 1.0f;
        this.chanceToFall = 0;
        this.SpriteName = spriteName;
        this.col = new Color(Rand.Next(255), Rand.Next(255), Rand.Next(255));
        this.palette = palette;
    }
    
    public Clothing(final String s, final String s2, final String s3, final Item item, final String palette, final String spriteName) {
        super(s, s2, s3, item);
        this.insulation = 0.0f;
        this.windresistance = 0.0f;
        this.waterResistance = 0.0f;
        this.SpriteName = null;
        this.bloodLevel = 0.0f;
        this.dirtyness = 0.0f;
        this.wetness = 0.0f;
        this.WeightWet = 0.0f;
        this.lastWetnessUpdate = -1.0f;
        this.dirtyString = Translator.getText("IGUI_ClothingName_Dirty");
        this.bloodyString = Translator.getText("IGUI_ClothingName_Bloody");
        this.wetString = Translator.getText("IGUI_ClothingName_Wet");
        this.soakedString = Translator.getText("IGUI_ClothingName_Soaked");
        this.wornString = Translator.getText("IGUI_ClothingName_Worn");
        this.ConditionLowerChance = 10000;
        this.stompPower = 1.0f;
        this.runSpeedModifier = 1.0f;
        this.combatSpeedModifier = 1.0f;
        this.removeOnBroken = false;
        this.canHaveHoles = true;
        this.biteDefense = 0.0f;
        this.scratchDefense = 0.0f;
        this.bulletDefense = 0.0f;
        this.neckProtectionModifier = 1.0f;
        this.chanceToFall = 0;
        this.SpriteName = spriteName;
        this.col = new Color(Rand.Next(255), Rand.Next(255), Rand.Next(255));
        this.palette = palette;
    }
    
    @Override
    public boolean IsClothing() {
        return true;
    }
    
    @Override
    public int getSaveType() {
        return Item.Type.Clothing.ordinal();
    }
    
    public void Unwear() {
        if (this.container != null && this.container.parent instanceof IsoGameCharacter) {
            final IsoGameCharacter isoGameCharacter = (IsoGameCharacter)this.container.parent;
            isoGameCharacter.removeWornItem(this);
            if (isoGameCharacter instanceof IsoPlayer) {
                LuaEventManager.triggerEvent("OnClothingUpdated", isoGameCharacter);
            }
            IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
        }
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
        if (!this.isCosmetic()) {
            final ObjectTooltip.LayoutItem addItem = layout.addItem();
            addItem.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_Condition")), n, n2, n3, n4);
            addItem.setProgress(this.Condition / (float)this.ConditionMax, n5, n6, n7, n8);
            final ObjectTooltip.LayoutItem addItem2 = layout.addItem();
            addItem2.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_item_Insulation")), 1.0f, 1.0f, 0.8f, 1.0f);
            final float insulation = this.getInsulation();
            if (insulation > 0.8f) {
                addItem2.setProgress(insulation, 0.0f, 0.6f, 0.0f, 0.7f);
            }
            else if (insulation > 0.6f) {
                addItem2.setProgress(insulation, 0.3f, 0.6f, 0.0f, 0.7f);
            }
            else if (insulation > 0.4f) {
                addItem2.setProgress(insulation, 0.6f, 0.6f, 0.0f, 0.7f);
            }
            else if (insulation > 0.2f) {
                addItem2.setProgress(insulation, 0.6f, 0.3f, 0.0f, 0.7f);
            }
            else {
                addItem2.setProgress(insulation, 0.6f, 0.0f, 0.0f, 0.7f);
            }
            final float windresistance = this.getWindresistance();
            if (windresistance > 0.0f) {
                final ObjectTooltip.LayoutItem addItem3 = layout.addItem();
                addItem3.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_item_Windresist")), 1.0f, 1.0f, 0.8f, 1.0f);
                if (windresistance > 0.8f) {
                    addItem3.setProgress(windresistance, 0.0f, 0.6f, 0.0f, 0.7f);
                }
                else if (windresistance > 0.6f) {
                    addItem3.setProgress(windresistance, 0.3f, 0.6f, 0.0f, 0.7f);
                }
                else if (windresistance > 0.4f) {
                    addItem3.setProgress(windresistance, 0.6f, 0.6f, 0.0f, 0.7f);
                }
                else if (windresistance > 0.2f) {
                    addItem3.setProgress(windresistance, 0.6f, 0.3f, 0.0f, 0.7f);
                }
                else {
                    addItem3.setProgress(windresistance, 0.6f, 0.0f, 0.0f, 0.7f);
                }
            }
            final float waterResistance = this.getWaterResistance();
            if (waterResistance > 0.0f) {
                final ObjectTooltip.LayoutItem addItem4 = layout.addItem();
                addItem4.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_item_Waterresist")), 1.0f, 1.0f, 0.8f, 1.0f);
                if (waterResistance > 0.8f) {
                    addItem4.setProgress(waterResistance, 0.0f, 0.6f, 0.0f, 0.7f);
                }
                else if (waterResistance > 0.6f) {
                    addItem4.setProgress(waterResistance, 0.3f, 0.6f, 0.0f, 0.7f);
                }
                else if (waterResistance > 0.4f) {
                    addItem4.setProgress(waterResistance, 0.6f, 0.6f, 0.0f, 0.7f);
                }
                else if (waterResistance > 0.2f) {
                    addItem4.setProgress(waterResistance, 0.6f, 0.3f, 0.0f, 0.7f);
                }
                else {
                    addItem4.setProgress(waterResistance, 0.6f, 0.0f, 0.0f, 0.7f);
                }
            }
        }
        if (this.bloodLevel != 0.0f) {
            final ObjectTooltip.LayoutItem addItem5 = layout.addItem();
            addItem5.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_clothing_bloody")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem5.setProgress(this.bloodLevel / 100.0f, n5, n6, n7, n8);
        }
        if (this.dirtyness >= 1.0f) {
            final ObjectTooltip.LayoutItem addItem6 = layout.addItem();
            addItem6.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_clothing_dirty")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem6.setProgress(this.dirtyness / 100.0f, n5, n6, n7, n8);
        }
        if (this.wetness != 0.0f) {
            final ObjectTooltip.LayoutItem addItem7 = layout.addItem();
            addItem7.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_clothing_wet")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem7.setProgress(this.wetness / 100.0f, n5, n6, n7, n8);
        }
        if (!this.isEquipped() && objectTooltip.getCharacter() != null) {
            float n9 = 0.0f;
            float n10 = 0.0f;
            float n11 = 0.0f;
            final WornItems wornItems = objectTooltip.getCharacter().getWornItems();
            for (int i = 0; i < wornItems.size(); ++i) {
                final WornItem value = wornItems.get(i);
                if (this.getBodyLocation().equals(value.getLocation()) || wornItems.getBodyLocationGroup().isExclusive(this.getBodyLocation(), value.getLocation())) {
                    n9 += ((Clothing)value.getItem()).getBiteDefense();
                    n10 += ((Clothing)value.getItem()).getScratchDefense();
                    n11 += ((Clothing)value.getItem()).getBulletDefense();
                }
            }
            final float biteDefense = this.getBiteDefense();
            if (biteDefense != n9) {
                final ObjectTooltip.LayoutItem addItem8 = layout.addItem();
                if (biteDefense > 0.0f || n9 > 0.0f) {
                    addItem8.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_BiteDefense")), 1.0f, 1.0f, 0.8f, 1.0f);
                    if (biteDefense > n9) {
                        addItem8.setValue(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, (int)biteDefense, (int)(biteDefense - n9)), 0.0f, 1.0f, 0.0f, 1.0f);
                    }
                    else {
                        addItem8.setValue(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, (int)biteDefense, (int)(n9 - biteDefense)), 1.0f, 0.0f, 0.0f, 1.0f);
                    }
                }
            }
            else if (this.getBiteDefense() != 0.0f) {
                final ObjectTooltip.LayoutItem addItem9 = layout.addItem();
                addItem9.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_BiteDefense")), 1.0f, 1.0f, 0.8f, 1.0f);
                addItem9.setValueRightNoPlus((int)this.getBiteDefense());
            }
            final float scratchDefense = this.getScratchDefense();
            if (scratchDefense != n10) {
                final ObjectTooltip.LayoutItem addItem10 = layout.addItem();
                if (scratchDefense > 0.0f || n10 > 0.0f) {
                    addItem10.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_ScratchDefense")), 1.0f, 1.0f, 0.8f, 1.0f);
                    if (scratchDefense > n10) {
                        addItem10.setValue(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, (int)scratchDefense, (int)(scratchDefense - n10)), 0.0f, 1.0f, 0.0f, 1.0f);
                    }
                    else {
                        addItem10.setValue(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, (int)scratchDefense, (int)(n10 - scratchDefense)), 1.0f, 0.0f, 0.0f, 1.0f);
                    }
                }
            }
            else if (this.getScratchDefense() != 0.0f) {
                final ObjectTooltip.LayoutItem addItem11 = layout.addItem();
                addItem11.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_ScratchDefense")), 1.0f, 1.0f, 0.8f, 1.0f);
                addItem11.setValueRightNoPlus((int)this.getScratchDefense());
            }
            final float bulletDefense = this.getBulletDefense();
            if (bulletDefense != n11) {
                final ObjectTooltip.LayoutItem addItem12 = layout.addItem();
                if (bulletDefense > 0.0f || n11 > 0.0f) {
                    addItem12.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_BulletDefense")), 1.0f, 1.0f, 0.8f, 1.0f);
                    if (bulletDefense > n11) {
                        addItem12.setValue(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, (int)bulletDefense, (int)(bulletDefense - n11)), 0.0f, 1.0f, 0.0f, 1.0f);
                    }
                    else {
                        addItem12.setValue(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, (int)bulletDefense, (int)(n11 - bulletDefense)), 1.0f, 0.0f, 0.0f, 1.0f);
                    }
                }
            }
            else if (this.getBulletDefense() != 0.0f) {
                final ObjectTooltip.LayoutItem addItem13 = layout.addItem();
                addItem13.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_BulletDefense")), 1.0f, 1.0f, 0.8f, 1.0f);
                addItem13.setValueRightNoPlus((int)this.getBulletDefense());
            }
        }
        else {
            if (this.getBiteDefense() != 0.0f) {
                final ObjectTooltip.LayoutItem addItem14 = layout.addItem();
                addItem14.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_BiteDefense")), 1.0f, 1.0f, 0.8f, 1.0f);
                addItem14.setValueRightNoPlus((int)this.getBiteDefense());
            }
            if (this.getScratchDefense() != 0.0f) {
                final ObjectTooltip.LayoutItem addItem15 = layout.addItem();
                addItem15.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_ScratchDefense")), 1.0f, 1.0f, 0.8f, 1.0f);
                addItem15.setValueRightNoPlus((int)this.getScratchDefense());
            }
            if (this.getBulletDefense() != 0.0f) {
                final ObjectTooltip.LayoutItem addItem16 = layout.addItem();
                addItem16.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_BulletDefense")), 1.0f, 1.0f, 0.8f, 1.0f);
                addItem16.setValueRightNoPlus((int)this.getBulletDefense());
            }
        }
        if (this.getRunSpeedModifier() != 1.0f) {
            final ObjectTooltip.LayoutItem addItem17 = layout.addItem();
            addItem17.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_RunSpeedModifier")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem17.setValueRightNoPlus(this.getRunSpeedModifier());
        }
        if (this.getCombatSpeedModifier() != 1.0f) {
            final ObjectTooltip.LayoutItem addItem18 = layout.addItem();
            addItem18.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_CombatSpeedModifier")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem18.setValueRightNoPlus(this.getCombatSpeedModifier());
        }
        if (Core.bDebug && DebugOptions.instance.TooltipInfo.getValue()) {
            if (this.bloodLevel != 0.0f) {
                final ObjectTooltip.LayoutItem addItem19 = layout.addItem();
                addItem19.setLabel("DBG: bloodLevel:", 1.0f, 1.0f, 0.8f, 1.0f);
                addItem19.setValueRight((int)Math.ceil(this.bloodLevel), false);
            }
            if (this.dirtyness != 0.0f) {
                final ObjectTooltip.LayoutItem addItem20 = layout.addItem();
                addItem20.setLabel("DBG: dirtyness:", 1.0f, 1.0f, 0.8f, 1.0f);
                addItem20.setValueRight((int)Math.ceil(this.dirtyness), false);
            }
            if (this.wetness != 0.0f) {
                final ObjectTooltip.LayoutItem addItem21 = layout.addItem();
                addItem21.setLabel("DBG: wetness:", 1.0f, 1.0f, 0.8f, 1.0f);
                addItem21.setValueRight((int)Math.ceil(this.wetness), false);
            }
        }
    }
    
    public boolean isDirty() {
        return this.dirtyness > 15.0f;
    }
    
    public boolean isBloody() {
        return this.bloodLevel > 25.0f;
    }
    
    @Override
    public String getName() {
        String substring = "";
        if (this.isDirty()) {
            substring = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, substring, this.dirtyString);
        }
        if (this.isBloody()) {
            substring = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, substring, this.bloodyString);
        }
        if (this.getWetness() >= 100.0f) {
            substring = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, substring, this.soakedString);
        }
        else if (this.getWetness() > 25.0f) {
            substring = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, substring, this.wetString);
        }
        if (this.getCondition() < this.getConditionMax() / 3) {
            substring = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, substring, this.wornString);
        }
        if (substring.length() > 2) {
            substring = substring.substring(0, substring.length() - 2);
        }
        final String trim = substring.trim();
        if (trim.isEmpty()) {
            return this.name;
        }
        return Translator.getText("IGUI_ClothingNaming", trim, this.name);
    }
    
    @Override
    public void update() {
        if (this.container == null || SandboxOptions.instance.ClothingDegradation.getValue() == 1) {
            return;
        }
    }
    
    public void updateWetness() {
        this.updateWetness(false);
    }
    
    public void updateWetness(final boolean b) {
        if (!b && this.isEquipped()) {
            return;
        }
        if (this.getBloodClothingType() == null) {
            this.setWetness(0.0f);
            return;
        }
        final float lastWetnessUpdate = (float)GameTime.getInstance().getWorldAgeHours();
        if (this.lastWetnessUpdate < 0.0f) {
            this.lastWetnessUpdate = lastWetnessUpdate;
        }
        else if (this.lastWetnessUpdate > lastWetnessUpdate) {
            this.lastWetnessUpdate = lastWetnessUpdate;
        }
        final float n = lastWetnessUpdate - this.lastWetnessUpdate;
        if (n < 0.016666668f) {
            return;
        }
        this.lastWetnessUpdate = lastWetnessUpdate;
        switch (this.getWetDryState()) {
            case Dryer: {
                if (this.getWetness() > 0.0f) {
                    float n2 = n * 20.0f;
                    if (this.isEquipped()) {
                        n2 *= 2.0f;
                    }
                    this.setWetness(this.getWetness() - n2);
                    break;
                }
                break;
            }
            case Wetter: {
                if (this.getWetness() < 100.0f) {
                    float rainIntensity = ClimateManager.getInstance().getRainIntensity();
                    if (rainIntensity < 0.1f) {
                        rainIntensity = 0.0f;
                    }
                    this.setWetness(this.getWetness() + rainIntensity * n * 100.0f);
                    break;
                }
                break;
            }
        }
    }
    
    public float getBulletDefense() {
        return this.bulletDefense;
    }
    
    public void setBulletDefense(final float bulletDefense) {
        this.bulletDefense = bulletDefense;
    }
    
    private WetDryState getWetDryState() {
        if (this.getWorldItem() == null) {
            if (this.container == null) {
                return WetDryState.Invalid;
            }
            if (this.container.parent instanceof IsoDeadBody) {
                final IsoDeadBody isoDeadBody = (IsoDeadBody)this.container.parent;
                if (isoDeadBody.getSquare() == null) {
                    return WetDryState.Invalid;
                }
                if (isoDeadBody.getSquare().isInARoom()) {
                    return WetDryState.Dryer;
                }
                if (ClimateManager.getInstance().isRaining()) {
                    return WetDryState.Wetter;
                }
                return WetDryState.Dryer;
            }
            else if (this.container.parent instanceof IsoGameCharacter) {
                final IsoGameCharacter isoGameCharacter = (IsoGameCharacter)this.container.parent;
                if (isoGameCharacter.getCurrentSquare() == null) {
                    return WetDryState.Invalid;
                }
                if (isoGameCharacter.getCurrentSquare().isInARoom() || isoGameCharacter.getCurrentSquare().haveRoof) {
                    return WetDryState.Dryer;
                }
                if (!ClimateManager.getInstance().isRaining()) {
                    return WetDryState.Dryer;
                }
                if (!this.isEquipped()) {
                    return WetDryState.Dryer;
                }
                if (isoGameCharacter.isAsleep() && isoGameCharacter.getBed() != null && "Tent".equals(isoGameCharacter.getBed().getName())) {
                    return WetDryState.Dryer;
                }
                final BaseVehicle vehicle = isoGameCharacter.getVehicle();
                if (vehicle != null && vehicle.hasRoof(vehicle.getSeat(isoGameCharacter))) {
                    final VehiclePart partById = vehicle.getPartById("Windshield");
                    if (partById != null) {
                        final VehicleWindow window = partById.getWindow();
                        if (window != null && window.isHittable()) {
                            return WetDryState.Dryer;
                        }
                    }
                }
                return WetDryState.Wetter;
            }
            else {
                if (this.container.parent == null) {
                    return WetDryState.Dryer;
                }
                if (this.container.parent instanceof IsoClothingDryer && ((IsoClothingDryer)this.container.parent).isActivated()) {
                    return WetDryState.Invalid;
                }
                if (this.container.parent instanceof IsoClothingWasher && ((IsoClothingWasher)this.container.parent).isActivated()) {
                    return WetDryState.Invalid;
                }
                return WetDryState.Dryer;
            }
        }
        else {
            if (this.getWorldItem().getSquare() == null) {
                return WetDryState.Invalid;
            }
            if (this.getWorldItem().getSquare().isInARoom()) {
                return WetDryState.Dryer;
            }
            if (ClimateManager.getInstance().isRaining()) {
                return WetDryState.Wetter;
            }
            return WetDryState.Dryer;
        }
    }
    
    public void flushWetness() {
        if (this.lastWetnessUpdate < 0.0f) {
            return;
        }
        this.updateWetness(true);
        this.lastWetnessUpdate = -1.0f;
    }
    
    @Override
    public boolean finishupdate() {
        return this.container == null || !(this.container.parent instanceof IsoGameCharacter) || !this.isEquipped();
    }
    
    @Override
    public void Use(final boolean b, final boolean b2) {
        if (this.uses <= 1) {
            this.Unwear();
        }
        super.Use(b, b2);
    }
    
    @Override
    public boolean CanStack(final InventoryItem inventoryItem) {
        return (this.ModDataMatches(inventoryItem) && this.palette == null && ((Clothing)inventoryItem).palette == null) || this.palette.equals(((Clothing)inventoryItem).palette);
    }
    
    public static Clothing CreateFromSprite(final String s) {
        try {
            return (Clothing)InventoryItemFactory.CreateItem(s, 1.0f);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        final BitHeaderWrite allocWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, byteBuffer);
        if (this.getSpriteName() != null) {
            allocWrite.addFlags(1);
            GameWindow.WriteString(byteBuffer, this.getSpriteName());
        }
        if (this.dirtyness != 0.0f) {
            allocWrite.addFlags(2);
            byteBuffer.putFloat(this.dirtyness);
        }
        if (this.bloodLevel != 0.0f) {
            allocWrite.addFlags(4);
            byteBuffer.putFloat(this.bloodLevel);
        }
        if (this.wetness != 0.0f) {
            allocWrite.addFlags(8);
            byteBuffer.putFloat(this.wetness);
        }
        if (this.lastWetnessUpdate != 0.0f) {
            allocWrite.addFlags(16);
            byteBuffer.putFloat(this.lastWetnessUpdate);
        }
        if (this.patches != null) {
            allocWrite.addFlags(32);
            byteBuffer.put((byte)this.patches.size());
            for (final int intValue : this.patches.keySet()) {
                byteBuffer.put((byte)intValue);
                this.patches.get(intValue).save(byteBuffer, false);
            }
        }
        allocWrite.write();
        allocWrite.release();
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        super.load(byteBuffer, n);
        final BitHeaderRead allocRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
        if (!allocRead.equals(0)) {
            if (allocRead.hasFlags(1)) {
                this.setSpriteName(GameWindow.ReadString(byteBuffer));
            }
            if (allocRead.hasFlags(2)) {
                this.dirtyness = byteBuffer.getFloat();
            }
            if (allocRead.hasFlags(4)) {
                this.bloodLevel = byteBuffer.getFloat();
            }
            if (allocRead.hasFlags(8)) {
                this.wetness = byteBuffer.getFloat();
            }
            if (allocRead.hasFlags(16)) {
                this.lastWetnessUpdate = byteBuffer.getFloat();
            }
            if (allocRead.hasFlags(32)) {
                for (byte value = byteBuffer.get(), b = 0; b < value; ++b) {
                    final byte value2 = byteBuffer.get();
                    final ClothingPatch value3 = new ClothingPatch();
                    value3.load(byteBuffer, n);
                    if (this.patches == null) {
                        this.patches = new HashMap<Integer, ClothingPatch>();
                    }
                    this.patches.put((int)value2, value3);
                }
            }
        }
        allocRead.release();
        this.synchWithVisual();
    }
    
    public String getSpriteName() {
        return this.SpriteName;
    }
    
    public void setSpriteName(final String spriteName) {
        this.SpriteName = spriteName;
    }
    
    public String getPalette() {
        if (this.palette == null) {
            return "Trousers_White";
        }
        return this.palette;
    }
    
    public void setPalette(final String palette) {
        this.palette = palette;
    }
    
    public float getTemperature() {
        return this.temperature;
    }
    
    public void setTemperature(final float temperature) {
        this.temperature = temperature;
    }
    
    public void setDirtyness(final float n) {
        this.dirtyness = PZMath.clamp(n, 0.0f, 100.0f);
    }
    
    public void setBloodLevel(final float n) {
        this.bloodLevel = PZMath.clamp(n, 0.0f, 100.0f);
    }
    
    public float getDirtyness() {
        return this.dirtyness;
    }
    
    public float getBloodlevel() {
        return this.bloodLevel;
    }
    
    public float getBloodlevelForPart(final BloodBodyPartType bloodBodyPartType) {
        return this.getVisual().getBlood(bloodBodyPartType);
    }
    
    public float getBloodLevel() {
        return this.bloodLevel;
    }
    
    public float getBloodLevelForPart(final BloodBodyPartType bloodBodyPartType) {
        return this.getVisual().getBlood(bloodBodyPartType);
    }
    
    @Override
    public float getWeight() {
        final float actualWeight = this.getActualWeight();
        float weightWet = this.getWeightWet();
        if (weightWet <= 0.0f) {
            weightWet = actualWeight * 1.25f;
        }
        return PZMath.lerp(actualWeight, weightWet, this.getWetness() / 100.0f);
    }
    
    public void setWetness(final float n) {
        this.wetness = PZMath.clamp(n, 0.0f, 100.0f);
    }
    
    public float getWetness() {
        return this.wetness;
    }
    
    public float getWeightWet() {
        return this.WeightWet;
    }
    
    public void setWeightWet(final float weightWet) {
        this.WeightWet = weightWet;
    }
    
    public int getConditionLowerChance() {
        return this.ConditionLowerChance;
    }
    
    public void setConditionLowerChance(final int conditionLowerChance) {
        this.ConditionLowerChance = conditionLowerChance;
    }
    
    @Override
    public void setCondition(final int n) {
        this.setCondition(n, true);
        if (n <= 0) {
            this.Unwear();
            if (this.getContainer() != null) {
                this.getContainer().setDrawDirty(true);
            }
            if (this.isRemoveOnBroken() && this.getContainer() != null) {
                this.container.Remove(this);
            }
        }
    }
    
    public float getClothingDirtynessIncreaseLevel() {
        if (SandboxOptions.instance.ClothingDegradation.getValue() == 2) {
            return 2.5E-4f;
        }
        if (SandboxOptions.instance.ClothingDegradation.getValue() == 4) {
            return 0.025f;
        }
        return 0.0025f;
    }
    
    public float getInsulation() {
        return this.insulation;
    }
    
    public void setInsulation(final float insulation) {
        this.insulation = insulation;
    }
    
    public float getStompPower() {
        return this.stompPower;
    }
    
    public void setStompPower(final float stompPower) {
        this.stompPower = stompPower;
    }
    
    public float getRunSpeedModifier() {
        return this.runSpeedModifier;
    }
    
    public void setRunSpeedModifier(final float runSpeedModifier) {
        this.runSpeedModifier = runSpeedModifier;
    }
    
    public float getCombatSpeedModifier() {
        return this.combatSpeedModifier;
    }
    
    public void setCombatSpeedModifier(final float combatSpeedModifier) {
        this.combatSpeedModifier = combatSpeedModifier;
    }
    
    public Boolean isRemoveOnBroken() {
        return this.removeOnBroken;
    }
    
    public void setRemoveOnBroken(final Boolean removeOnBroken) {
        this.removeOnBroken = removeOnBroken;
    }
    
    public Boolean getCanHaveHoles() {
        return this.canHaveHoles;
    }
    
    public void setCanHaveHoles(final Boolean canHaveHoles) {
        this.canHaveHoles = canHaveHoles;
    }
    
    public boolean isCosmetic() {
        return this.getScriptItem().isCosmetic();
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), this.getClothingItemName());
    }
    
    public float getBiteDefense() {
        if (this.getCondition() <= 0) {
            return 0.0f;
        }
        return this.biteDefense;
    }
    
    public void setBiteDefense(final float biteDefense) {
        this.biteDefense = biteDefense;
    }
    
    public float getScratchDefense() {
        if (this.getCondition() <= 0) {
            return 0.0f;
        }
        return this.scratchDefense;
    }
    
    public void setScratchDefense(final float scratchDefense) {
        this.scratchDefense = scratchDefense;
    }
    
    public float getNeckProtectionModifier() {
        return this.neckProtectionModifier;
    }
    
    public void setNeckProtectionModifier(final float neckProtectionModifier) {
        this.neckProtectionModifier = neckProtectionModifier;
    }
    
    public int getChanceToFall() {
        return this.chanceToFall;
    }
    
    public void setChanceToFall(final int chanceToFall) {
        this.chanceToFall = chanceToFall;
    }
    
    public float getWindresistance() {
        return this.windresistance;
    }
    
    public void setWindresistance(final float windresistance) {
        this.windresistance = windresistance;
    }
    
    public float getWaterResistance() {
        return this.waterResistance;
    }
    
    public void setWaterResistance(final float waterResistance) {
        this.waterResistance = waterResistance;
    }
    
    public int getHolesNumber() {
        if (this.getVisual() != null) {
            return this.getVisual().getHolesNumber();
        }
        return 0;
    }
    
    public int getPatchesNumber() {
        return this.patches.size();
    }
    
    public float getDefForPart(final BloodBodyPartType bloodBodyPartType, final boolean b, final boolean b2) {
        if (this.getVisual().getHole(bloodBodyPartType) > 0.0f) {
            return 0.0f;
        }
        final ClothingPatch patchType = this.getPatchType(bloodBodyPartType);
        float n = this.getScratchDefense();
        if (b) {
            n = this.getBiteDefense();
        }
        if (b2) {
            n = this.getBulletDefense();
        }
        if (bloodBodyPartType == BloodBodyPartType.Neck && this.getScriptItem().neckProtectionModifier < 1.0f) {
            n *= this.getScriptItem().neckProtectionModifier;
        }
        if (patchType != null) {
            int n2 = patchType.scratchDefense;
            if (b) {
                n2 = patchType.biteDefense;
            }
            if (b2) {
                n2 = patchType.biteDefense;
            }
            if (!patchType.hasHole) {
                n += n2;
            }
            else {
                n = (float)n2;
            }
        }
        return n;
    }
    
    public static int getBiteDefenseFromItem(final IsoGameCharacter isoGameCharacter, final InventoryItem inventoryItem) {
        final int max = Math.max(1, isoGameCharacter.getPerkLevel(PerkFactory.Perks.Tailoring));
        final ClothingPatchFabricType fromType = ClothingPatchFabricType.fromType(inventoryItem.getFabricType());
        if (fromType.maxBiteDef > 0) {
            return (int)Math.max(1.0f, fromType.maxBiteDef * (max / 10.0f));
        }
        return 0;
    }
    
    public static int getScratchDefenseFromItem(final IsoGameCharacter isoGameCharacter, final InventoryItem inventoryItem) {
        return (int)Math.max(1.0f, ClothingPatchFabricType.fromType(inventoryItem.getFabricType()).maxScratchDef * (Math.max(1, isoGameCharacter.getPerkLevel(PerkFactory.Perks.Tailoring)) / 10.0f));
    }
    
    public ClothingPatch getPatchType(final BloodBodyPartType bloodBodyPartType) {
        if (this.patches != null) {
            return this.patches.get(bloodBodyPartType.index());
        }
        return null;
    }
    
    public void removePatch(final BloodBodyPartType hole) {
        if (this.patches == null) {
            return;
        }
        this.getVisual().removePatch(hole.index());
        final ClothingPatch clothingPatch = this.patches.get(hole.index());
        if (clothingPatch != null && clothingPatch.hasHole) {
            this.getVisual().setHole(hole);
            this.setCondition(this.getCondition() - clothingPatch.conditionGain);
        }
        this.patches.remove(hole.index());
        if (GameClient.bClient && this.getContainer() != null && this.getContainer().getParent() instanceof IsoPlayer) {
            GameClient.instance.sendClothing((IsoPlayer)this.getContainer().getParent(), "", null);
        }
    }
    
    public boolean canFullyRestore(final IsoGameCharacter isoGameCharacter, final BloodBodyPartType bloodBodyPartType, final InventoryItem inventoryItem) {
        return isoGameCharacter.getPerkLevel(PerkFactory.Perks.Tailoring) > 7 && inventoryItem.getFabricType().equals(this.getFabricType()) && this.getVisual().getHole(bloodBodyPartType) > 0.0f;
    }
    
    public void addPatch(final IsoGameCharacter isoGameCharacter, final BloodBodyPartType leatherPatch, final InventoryItem inventoryItem) {
        final ClothingPatchFabricType fromType = ClothingPatchFabricType.fromType(inventoryItem.getFabricType());
        if (this.canFullyRestore(isoGameCharacter, leatherPatch, inventoryItem)) {
            this.getVisual().removeHole(leatherPatch.index());
            this.setCondition(this.getCondition() + this.getCondLossPerHole());
            return;
        }
        if (fromType == ClothingPatchFabricType.Cotton) {
            this.getVisual().setBasicPatch(leatherPatch);
        }
        else if (fromType == ClothingPatchFabricType.Denim) {
            this.getVisual().setDenimPatch(leatherPatch);
        }
        else {
            this.getVisual().setLeatherPatch(leatherPatch);
        }
        if (this.patches == null) {
            this.patches = new HashMap<Integer, ClothingPatch>();
        }
        final int max = Math.max(1, isoGameCharacter.getPerkLevel(PerkFactory.Perks.Tailoring));
        final float hole = this.getVisual().getHole(leatherPatch);
        int condLossPerHole = this.getCondLossPerHole();
        if (max < 3) {
            condLossPerHole -= 2;
        }
        else if (max < 6) {
            --condLossPerHole;
        }
        final ClothingPatch value = new ClothingPatch(max, fromType.index, hole > 0.0f);
        if (hole > 0.0f) {
            final int max2 = Math.max(1, condLossPerHole);
            this.setCondition(this.getCondition() + max2);
            value.conditionGain = max2;
        }
        this.patches.put(leatherPatch.index(), value);
        this.getVisual().removeHole(leatherPatch.index());
        if (GameClient.bClient && isoGameCharacter instanceof IsoPlayer) {
            GameClient.instance.sendClothing((IsoPlayer)isoGameCharacter, "", null);
        }
    }
    
    public ArrayList<BloodBodyPartType> getCoveredParts() {
        return BloodClothingType.getCoveredParts(this.getScriptItem().getBloodClothingType());
    }
    
    public int getNbrOfCoveredParts() {
        return BloodClothingType.getCoveredPartCount(this.getScriptItem().getBloodClothingType());
    }
    
    public int getCondLossPerHole() {
        final int nbrOfCoveredParts = this.getNbrOfCoveredParts();
        int n;
        if (nbrOfCoveredParts <= 2) {
            n = 10;
        }
        else if (nbrOfCoveredParts <= 5) {
            n = 3;
        }
        else {
            n = 2;
        }
        return n;
    }
    
    public void copyPatchesTo(final Clothing clothing) {
        clothing.patches = this.patches;
    }
    
    public String getClothingExtraSubmenu() {
        return this.ScriptItem.clothingExtraSubmenu;
    }
    
    public boolean canBe3DRender() {
        return !StringUtils.isNullOrEmpty(this.getWorldStaticItem()) || ("Bip01_Head".equalsIgnoreCase(this.getClothingItem().m_AttachBone) && (!this.isCosmetic() || "Eyes".equals(this.getBodyLocation())));
    }
    
    private enum WetDryState
    {
        Invalid, 
        Dryer, 
        Wetter;
        
        private static /* synthetic */ WetDryState[] $values() {
            return new WetDryState[] { WetDryState.Invalid, WetDryState.Dryer, WetDryState.Wetter };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public class ClothingPatch
    {
        public int tailorLvl;
        public int fabricType;
        public int scratchDefense;
        public int biteDefense;
        public boolean hasHole;
        public int conditionGain;
        
        public String getFabricTypeName() {
            return Translator.getText(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.fabricType));
        }
        
        public int getScratchDefense() {
            return this.scratchDefense;
        }
        
        public int getBiteDefense() {
            return this.biteDefense;
        }
        
        public int getFabricType() {
            return this.fabricType;
        }
        
        public ClothingPatch() {
            this.tailorLvl = 0;
            this.fabricType = 0;
            this.scratchDefense = 0;
            this.biteDefense = 0;
            this.conditionGain = 0;
        }
        
        public ClothingPatch(final int tailorLvl, final int fabricType, final boolean hasHole) {
            this.tailorLvl = 0;
            this.fabricType = 0;
            this.scratchDefense = 0;
            this.biteDefense = 0;
            this.conditionGain = 0;
            this.tailorLvl = tailorLvl;
            this.fabricType = fabricType;
            this.hasHole = hasHole;
            final ClothingPatchFabricType fromIndex = ClothingPatchFabricType.fromIndex(fabricType);
            this.scratchDefense = (int)Math.max(1.0f, fromIndex.maxScratchDef * (tailorLvl / 10.0f));
            if (fromIndex.maxBiteDef > 0) {
                this.biteDefense = (int)Math.max(1.0f, fromIndex.maxBiteDef * (tailorLvl / 10.0f));
            }
        }
        
        public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
            byteBuffer.put((byte)this.tailorLvl);
            byteBuffer.put((byte)this.fabricType);
            byteBuffer.put((byte)this.scratchDefense);
            byteBuffer.put((byte)this.biteDefense);
            byteBuffer.put((byte)(this.hasHole ? 1 : 0));
            byteBuffer.putShort((short)this.conditionGain);
        }
        
        public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
            this.tailorLvl = byteBuffer.get();
            if (n < 178) {
                this.fabricType = byteBuffer.getShort();
            }
            else {
                this.fabricType = byteBuffer.get();
            }
            this.scratchDefense = byteBuffer.get();
            this.biteDefense = byteBuffer.get();
            this.hasHole = (byteBuffer.get() == 1);
            this.conditionGain = byteBuffer.getShort();
        }
        
        @Deprecated
        public void save_old(final ByteBuffer byteBuffer, final boolean b) throws IOException {
            byteBuffer.putInt(this.tailorLvl);
            byteBuffer.putInt(this.fabricType);
            byteBuffer.putInt(this.scratchDefense);
            byteBuffer.putInt(this.biteDefense);
            byteBuffer.put((byte)(this.hasHole ? 1 : 0));
            byteBuffer.putInt(this.conditionGain);
        }
        
        @Deprecated
        public void load_old(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
            this.tailorLvl = byteBuffer.getInt();
            this.fabricType = byteBuffer.getInt();
            this.scratchDefense = byteBuffer.getInt();
            this.biteDefense = byteBuffer.getInt();
            this.hasHole = (byteBuffer.get() == 1);
            this.conditionGain = byteBuffer.getInt();
        }
    }
    
    public enum ClothingPatchFabricType
    {
        Cotton(1, "Cotton", 5, 0), 
        Denim(2, "Denim", 10, 5), 
        Leather(3, "Leather", 20, 10);
        
        public int index;
        public String type;
        public int maxScratchDef;
        public int maxBiteDef;
        
        private ClothingPatchFabricType(final int index, final String type, final int maxScratchDef, final int maxBiteDef) {
            this.index = index;
            this.type = type;
            this.maxScratchDef = maxScratchDef;
            this.maxBiteDef = maxBiteDef;
        }
        
        public String getType() {
            return this.type;
        }
        
        public static ClothingPatchFabricType fromType(final String anObject) {
            if (StringUtils.isNullOrEmpty(anObject)) {
                return null;
            }
            if (ClothingPatchFabricType.Cotton.type.equals(anObject)) {
                return ClothingPatchFabricType.Cotton;
            }
            if (ClothingPatchFabricType.Denim.type.equals(anObject)) {
                return ClothingPatchFabricType.Denim;
            }
            if (ClothingPatchFabricType.Leather.type.equals(anObject)) {
                return ClothingPatchFabricType.Leather;
            }
            return null;
        }
        
        public static ClothingPatchFabricType fromIndex(final int n) {
            if (n == 1) {
                return ClothingPatchFabricType.Cotton;
            }
            if (n == 2) {
                return ClothingPatchFabricType.Denim;
            }
            if (n == 3) {
                return ClothingPatchFabricType.Leather;
            }
            return null;
        }
        
        private static /* synthetic */ ClothingPatchFabricType[] $values() {
            return new ClothingPatchFabricType[] { ClothingPatchFabricType.Cotton, ClothingPatchFabricType.Denim, ClothingPatchFabricType.Leather };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
