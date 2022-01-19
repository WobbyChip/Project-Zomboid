// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory;

import zombie.radio.ZomboidRadio;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.SurvivorDesc;
import zombie.core.stash.StashSystem;
import zombie.core.textures.ColorInfo;
import zombie.util.Type;
import zombie.inventory.types.Drainable;
import zombie.util.StringUtils;
import zombie.inventory.types.InventoryContainer;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.scripting.objects.ItemReplacement;
import zombie.characters.IsoPlayer;
import java.io.DataInputStream;
import zombie.util.io.BitHeaderRead;
import zombie.core.math.PZMath;
import zombie.core.logger.ExceptionLogger;
import zombie.util.io.BitHeaderWrite;
import zombie.core.utils.Bits;
import zombie.util.io.BitHeader;
import zombie.debug.DebugLog;
import zombie.GameWindow;
import zombie.audio.BaseSoundEmitter;
import zombie.iso.objects.RainManager;
import zombie.Lua.LuaEventManager;
import zombie.iso.IsoWorld;
import zombie.GameTime;
import zombie.iso.IsoGridSquare;
import zombie.network.GameClient;
import zombie.world.ItemInfo;
import zombie.radio.media.MediaData;
import zombie.ui.UIFont;
import zombie.world.WorldDictionary;
import zombie.core.Colors;
import zombie.vehicles.VehiclePart;
import zombie.inventory.types.Key;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import zombie.scripting.ScriptManager;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.HandWeapon;
import zombie.ui.TextManager;
import zombie.inventory.types.Food;
import zombie.inventory.types.DrainableComboItem;
import zombie.ui.ObjectTooltip;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.core.Translator;
import java.io.IOException;
import zombie.Lua.LuaManager;
import zombie.characterTextures.BloodClothingType;
import zombie.core.skinnedmodel.visual.ItemVisual;
import java.nio.ByteBuffer;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.core.Color;
import se.krka.kahlua.vm.KahluaTable;
import zombie.iso.IsoDirections;
import zombie.iso.IsoObject;
import java.util.ArrayList;
import zombie.core.textures.Texture;
import zombie.scripting.objects.Item;
import zombie.characters.IsoGameCharacter;

public class InventoryItem
{
    protected IsoGameCharacter previousOwner;
    protected Item ScriptItem;
    protected ItemType cat;
    protected ItemContainer container;
    protected int containerX;
    protected int containerY;
    protected String name;
    protected String replaceOnUse;
    protected String replaceOnUseFullType;
    protected int ConditionMax;
    protected ItemContainer rightClickContainer;
    protected Texture texture;
    protected Texture texturerotten;
    protected Texture textureCooked;
    protected Texture textureBurnt;
    protected String type;
    protected String fullType;
    protected int uses;
    protected float Age;
    protected float LastAged;
    protected boolean IsCookable;
    protected float CookingTime;
    protected float MinutesToCook;
    protected float MinutesToBurn;
    public boolean Cooked;
    protected boolean Burnt;
    protected int OffAge;
    protected int OffAgeMax;
    protected float Weight;
    protected float ActualWeight;
    protected String WorldTexture;
    protected String Description;
    protected int Condition;
    protected String OffString;
    protected String FreshString;
    protected String CookedString;
    protected String UnCookedString;
    protected String FrozenString;
    protected String BurntString;
    private String brokenString;
    protected String module;
    protected float boredomChange;
    protected float unhappyChange;
    protected float stressChange;
    protected ArrayList<IsoObject> Taken;
    protected IsoDirections placeDir;
    protected IsoDirections newPlaceDir;
    private KahluaTable table;
    public String ReplaceOnUseOn;
    public Color col;
    public boolean IsWaterSource;
    public boolean CanStoreWater;
    public boolean CanStack;
    private boolean activated;
    private boolean isTorchCone;
    private int lightDistance;
    private int Count;
    public float fatigueChange;
    public IsoWorldInventoryObject worldItem;
    private String customMenuOption;
    private String tooltip;
    private String displayCategory;
    private int haveBeenRepaired;
    private boolean broken;
    private String originalName;
    public int id;
    public boolean RequiresEquippedBothHands;
    public ByteBuffer byteData;
    public ArrayList<String> extraItems;
    private boolean customName;
    private String breakSound;
    protected boolean alcoholic;
    private float alcoholPower;
    private float bandagePower;
    private float ReduceInfectionPower;
    private boolean customWeight;
    private boolean customColor;
    private int keyId;
    private boolean taintedWater;
    private boolean remoteController;
    private boolean canBeRemote;
    private int remoteControlID;
    private int remoteRange;
    private float colorRed;
    private float colorGreen;
    private float colorBlue;
    private String countDownSound;
    private String explosionSound;
    private IsoGameCharacter equipParent;
    private String evolvedRecipeName;
    private float metalValue;
    private float itemHeat;
    private float meltingTime;
    private String worker;
    private boolean isWet;
    private float wetCooldown;
    private String itemWhenDry;
    private boolean favorite;
    protected ArrayList<String> requireInHandOrInventory;
    private String map;
    private String stashMap;
    public boolean keepOnDeplete;
    private boolean zombieInfected;
    private boolean rainFactorZero;
    private float itemCapacity;
    private int maxCapacity;
    private float brakeForce;
    private int chanceToSpawnDamaged;
    private float conditionLowerNormal;
    private float conditionLowerOffroad;
    private float wheelFriction;
    private float suspensionDamping;
    private float suspensionCompression;
    private float engineLoudness;
    protected ItemVisual visual;
    protected String staticModel;
    private ArrayList<String> iconsForTexture;
    private ArrayList<BloodClothingType> bloodClothingType;
    private int stashChance;
    private String ammoType;
    private int maxAmmo;
    private int currentAmmoCount;
    private String gunType;
    private String attachmentType;
    private ArrayList<String> attachmentsProvided;
    private int attachedSlot;
    private String attachedSlotType;
    private String attachmentReplacement;
    private String attachedToModel;
    private String m_alternateModelName;
    private short registry_id;
    public int worldZRotation;
    public float worldScale;
    private short recordedMediaIndex;
    private byte mediaType;
    public float jobDelta;
    public String jobType;
    static ByteBuffer tempBuffer;
    public String mainCategory;
    private boolean canBeActivated;
    private float lightStrength;
    public String CloseKillMove;
    private boolean beingFilled;
    
    public int getSaveType() {
        throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getName()));
    }
    
    public IsoWorldInventoryObject getWorldItem() {
        return this.worldItem;
    }
    
    public void setEquipParent(final IsoGameCharacter equipParent) {
        this.equipParent = equipParent;
    }
    
    public IsoGameCharacter getEquipParent() {
        if (this.equipParent != null && (this.equipParent.getPrimaryHandItem() == this || this.equipParent.getSecondaryHandItem() == this)) {
            return this.equipParent;
        }
        return null;
    }
    
    public String getBringToBearSound() {
        return this.getScriptItem().getBringToBearSound();
    }
    
    public String getEquipSound() {
        return this.getScriptItem().getEquipSound();
    }
    
    public String getUnequipSound() {
        return this.getScriptItem().getUnequipSound();
    }
    
    public void setWorldItem(final IsoWorldInventoryObject worldItem) {
        this.worldItem = worldItem;
    }
    
    public void setJobDelta(final float jobDelta) {
        this.jobDelta = jobDelta;
    }
    
    public float getJobDelta() {
        return this.jobDelta;
    }
    
    public void setJobType(final String jobType) {
        this.jobType = jobType;
    }
    
    public String getJobType() {
        return this.jobType;
    }
    
    public boolean hasModData() {
        return this.table != null && !this.table.isEmpty();
    }
    
    public KahluaTable getModData() {
        if (this.table == null) {
            this.table = LuaManager.platform.newTable();
        }
        return this.table;
    }
    
    public void storeInByteData(final IsoObject isoObject) {
        InventoryItem.tempBuffer.clear();
        try {
            isoObject.save(InventoryItem.tempBuffer, false);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        InventoryItem.tempBuffer.flip();
        if (this.byteData == null || this.byteData.capacity() < InventoryItem.tempBuffer.limit() - 2 + 8) {
            this.byteData = ByteBuffer.allocate(InventoryItem.tempBuffer.limit() - 2 + 8);
        }
        InventoryItem.tempBuffer.get();
        InventoryItem.tempBuffer.get();
        this.byteData.clear();
        this.byteData.put((byte)87);
        this.byteData.put((byte)86);
        this.byteData.put((byte)69);
        this.byteData.put((byte)82);
        this.byteData.putInt(186);
        this.byteData.put(InventoryItem.tempBuffer);
        this.byteData.flip();
    }
    
    public ByteBuffer getByteData() {
        return this.byteData;
    }
    
    public boolean isRequiresEquippedBothHands() {
        return this.RequiresEquippedBothHands;
    }
    
    public float getA() {
        return this.col.a;
    }
    
    public float getR() {
        return this.col.r;
    }
    
    public float getG() {
        return this.col.g;
    }
    
    public float getB() {
        return this.col.b;
    }
    
    public InventoryItem(final String module, final String s, final String type, final String s2) {
        this.previousOwner = null;
        this.ScriptItem = null;
        this.cat = ItemType.None;
        this.containerX = 0;
        this.containerY = 0;
        this.replaceOnUse = null;
        this.replaceOnUseFullType = null;
        this.ConditionMax = 10;
        this.rightClickContainer = null;
        this.uses = 1;
        this.Age = 0.0f;
        this.LastAged = -1.0f;
        this.IsCookable = false;
        this.CookingTime = 0.0f;
        this.MinutesToCook = 60.0f;
        this.MinutesToBurn = 120.0f;
        this.Cooked = false;
        this.Burnt = false;
        this.OffAge = 1000000000;
        this.OffAgeMax = 1000000000;
        this.Weight = 1.0f;
        this.ActualWeight = 1.0f;
        this.Condition = 10;
        this.OffString = Translator.getText("Tooltip_food_Rotten");
        this.FreshString = Translator.getText("Tooltip_food_Fresh");
        this.CookedString = Translator.getText("Tooltip_food_Cooked");
        this.UnCookedString = Translator.getText("Tooltip_food_Uncooked");
        this.FrozenString = Translator.getText("Tooltip_food_Frozen");
        this.BurntString = Translator.getText("Tooltip_food_Burnt");
        this.brokenString = Translator.getText("Tooltip_broken");
        this.module = "Base";
        this.boredomChange = 0.0f;
        this.unhappyChange = 0.0f;
        this.stressChange = 0.0f;
        this.Taken = new ArrayList<IsoObject>();
        this.placeDir = IsoDirections.Max;
        this.newPlaceDir = IsoDirections.Max;
        this.table = null;
        this.ReplaceOnUseOn = null;
        this.col = Color.white;
        this.IsWaterSource = false;
        this.CanStoreWater = false;
        this.CanStack = false;
        this.activated = false;
        this.isTorchCone = false;
        this.lightDistance = 0;
        this.Count = 1;
        this.fatigueChange = 0.0f;
        this.worldItem = null;
        this.customMenuOption = null;
        this.tooltip = null;
        this.displayCategory = null;
        this.haveBeenRepaired = 1;
        this.broken = false;
        this.originalName = null;
        this.id = 0;
        this.extraItems = null;
        this.customName = false;
        this.breakSound = null;
        this.alcoholic = false;
        this.alcoholPower = 0.0f;
        this.bandagePower = 0.0f;
        this.ReduceInfectionPower = 0.0f;
        this.customWeight = false;
        this.customColor = false;
        this.keyId = -1;
        this.taintedWater = false;
        this.remoteController = false;
        this.canBeRemote = false;
        this.remoteControlID = -1;
        this.remoteRange = 0;
        this.colorRed = 1.0f;
        this.colorGreen = 1.0f;
        this.colorBlue = 1.0f;
        this.countDownSound = null;
        this.explosionSound = null;
        this.equipParent = null;
        this.evolvedRecipeName = null;
        this.metalValue = 0.0f;
        this.itemHeat = 1.0f;
        this.meltingTime = 0.0f;
        this.isWet = false;
        this.wetCooldown = -1.0f;
        this.itemWhenDry = null;
        this.favorite = false;
        this.requireInHandOrInventory = null;
        this.map = null;
        this.stashMap = null;
        this.keepOnDeplete = false;
        this.zombieInfected = false;
        this.rainFactorZero = false;
        this.itemCapacity = -1.0f;
        this.maxCapacity = -1;
        this.brakeForce = 0.0f;
        this.chanceToSpawnDamaged = 0;
        this.conditionLowerNormal = 0.0f;
        this.conditionLowerOffroad = 0.0f;
        this.wheelFriction = 0.0f;
        this.suspensionDamping = 0.0f;
        this.suspensionCompression = 0.0f;
        this.engineLoudness = 0.0f;
        this.visual = null;
        this.staticModel = null;
        this.iconsForTexture = null;
        this.bloodClothingType = new ArrayList<BloodClothingType>();
        this.stashChance = 80;
        this.ammoType = null;
        this.maxAmmo = 0;
        this.currentAmmoCount = 0;
        this.gunType = null;
        this.attachmentType = null;
        this.attachmentsProvided = null;
        this.attachedSlot = -1;
        this.attachedSlotType = null;
        this.attachmentReplacement = null;
        this.attachedToModel = null;
        this.m_alternateModelName = null;
        this.registry_id = -1;
        this.worldZRotation = -1;
        this.worldScale = 1.0f;
        this.recordedMediaIndex = -1;
        this.mediaType = -1;
        this.jobDelta = 0.0f;
        this.jobType = null;
        this.mainCategory = null;
        this.CloseKillMove = null;
        this.beingFilled = false;
        this.col = Color.white;
        this.texture = Texture.trygetTexture(s2);
        if (this.texture == null) {
            this.texture = Texture.getSharedTexture("media/inventory/Question_On.png");
        }
        this.module = module;
        this.name = s;
        this.originalName = s;
        this.type = type;
        this.fullType = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, module, type);
        this.WorldTexture = s2.replace("Item_", "media/inventory/world/WItem_");
        this.WorldTexture = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.WorldTexture);
    }
    
    public InventoryItem(final String module, final String s, final String type, final Item item) {
        this.previousOwner = null;
        this.ScriptItem = null;
        this.cat = ItemType.None;
        this.containerX = 0;
        this.containerY = 0;
        this.replaceOnUse = null;
        this.replaceOnUseFullType = null;
        this.ConditionMax = 10;
        this.rightClickContainer = null;
        this.uses = 1;
        this.Age = 0.0f;
        this.LastAged = -1.0f;
        this.IsCookable = false;
        this.CookingTime = 0.0f;
        this.MinutesToCook = 60.0f;
        this.MinutesToBurn = 120.0f;
        this.Cooked = false;
        this.Burnt = false;
        this.OffAge = 1000000000;
        this.OffAgeMax = 1000000000;
        this.Weight = 1.0f;
        this.ActualWeight = 1.0f;
        this.Condition = 10;
        this.OffString = Translator.getText("Tooltip_food_Rotten");
        this.FreshString = Translator.getText("Tooltip_food_Fresh");
        this.CookedString = Translator.getText("Tooltip_food_Cooked");
        this.UnCookedString = Translator.getText("Tooltip_food_Uncooked");
        this.FrozenString = Translator.getText("Tooltip_food_Frozen");
        this.BurntString = Translator.getText("Tooltip_food_Burnt");
        this.brokenString = Translator.getText("Tooltip_broken");
        this.module = "Base";
        this.boredomChange = 0.0f;
        this.unhappyChange = 0.0f;
        this.stressChange = 0.0f;
        this.Taken = new ArrayList<IsoObject>();
        this.placeDir = IsoDirections.Max;
        this.newPlaceDir = IsoDirections.Max;
        this.table = null;
        this.ReplaceOnUseOn = null;
        this.col = Color.white;
        this.IsWaterSource = false;
        this.CanStoreWater = false;
        this.CanStack = false;
        this.activated = false;
        this.isTorchCone = false;
        this.lightDistance = 0;
        this.Count = 1;
        this.fatigueChange = 0.0f;
        this.worldItem = null;
        this.customMenuOption = null;
        this.tooltip = null;
        this.displayCategory = null;
        this.haveBeenRepaired = 1;
        this.broken = false;
        this.originalName = null;
        this.id = 0;
        this.extraItems = null;
        this.customName = false;
        this.breakSound = null;
        this.alcoholic = false;
        this.alcoholPower = 0.0f;
        this.bandagePower = 0.0f;
        this.ReduceInfectionPower = 0.0f;
        this.customWeight = false;
        this.customColor = false;
        this.keyId = -1;
        this.taintedWater = false;
        this.remoteController = false;
        this.canBeRemote = false;
        this.remoteControlID = -1;
        this.remoteRange = 0;
        this.colorRed = 1.0f;
        this.colorGreen = 1.0f;
        this.colorBlue = 1.0f;
        this.countDownSound = null;
        this.explosionSound = null;
        this.equipParent = null;
        this.evolvedRecipeName = null;
        this.metalValue = 0.0f;
        this.itemHeat = 1.0f;
        this.meltingTime = 0.0f;
        this.isWet = false;
        this.wetCooldown = -1.0f;
        this.itemWhenDry = null;
        this.favorite = false;
        this.requireInHandOrInventory = null;
        this.map = null;
        this.stashMap = null;
        this.keepOnDeplete = false;
        this.zombieInfected = false;
        this.rainFactorZero = false;
        this.itemCapacity = -1.0f;
        this.maxCapacity = -1;
        this.brakeForce = 0.0f;
        this.chanceToSpawnDamaged = 0;
        this.conditionLowerNormal = 0.0f;
        this.conditionLowerOffroad = 0.0f;
        this.wheelFriction = 0.0f;
        this.suspensionDamping = 0.0f;
        this.suspensionCompression = 0.0f;
        this.engineLoudness = 0.0f;
        this.visual = null;
        this.staticModel = null;
        this.iconsForTexture = null;
        this.bloodClothingType = new ArrayList<BloodClothingType>();
        this.stashChance = 80;
        this.ammoType = null;
        this.maxAmmo = 0;
        this.currentAmmoCount = 0;
        this.gunType = null;
        this.attachmentType = null;
        this.attachmentsProvided = null;
        this.attachedSlot = -1;
        this.attachedSlotType = null;
        this.attachmentReplacement = null;
        this.attachedToModel = null;
        this.m_alternateModelName = null;
        this.registry_id = -1;
        this.worldZRotation = -1;
        this.worldScale = 1.0f;
        this.recordedMediaIndex = -1;
        this.mediaType = -1;
        this.jobDelta = 0.0f;
        this.jobType = null;
        this.mainCategory = null;
        this.CloseKillMove = null;
        this.beingFilled = false;
        this.col = Color.white;
        this.texture = item.NormalTexture;
        this.module = module;
        this.name = s;
        this.originalName = s;
        this.type = type;
        this.fullType = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, module, type);
        this.WorldTexture = item.WorldTextureName;
    }
    
    public String getType() {
        return this.type;
    }
    
    public Texture getTex() {
        return this.texture;
    }
    
    public String getCategory() {
        if (this.mainCategory != null) {
            return this.mainCategory;
        }
        return "Item";
    }
    
    public boolean IsRotten() {
        return this.Age > this.OffAge;
    }
    
    public float HowRotten() {
        if (this.OffAgeMax - this.OffAge == 0) {
            return (this.Age > this.OffAge) ? 1.0f : 0.0f;
        }
        return (this.Age - this.OffAge) / (this.OffAgeMax - this.OffAge);
    }
    
    public boolean CanStack(final InventoryItem inventoryItem) {
        return false;
    }
    
    public boolean ModDataMatches(final InventoryItem inventoryItem) {
        final KahluaTable modData = inventoryItem.getModData();
        final KahluaTable modData2 = inventoryItem.getModData();
        if (modData == null && modData2 == null) {
            return true;
        }
        if (modData == null) {
            return false;
        }
        if (modData2 == null) {
            return false;
        }
        if (modData.len() != modData2.len()) {
            return false;
        }
        final KahluaTableIterator iterator = modData.iterator();
        while (iterator.advance()) {
            if (!modData2.rawget(iterator.getKey()).equals(iterator.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    public void DoTooltip(final ObjectTooltip objectTooltip) {
        objectTooltip.render();
        final UIFont font = objectTooltip.getFont();
        final int lineSpacing = objectTooltip.getLineSpacing();
        final int n = 5;
        String s = "";
        if (this.Burnt) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.BurntString);
        }
        else if (this.OffAge < 1000000000 && this.Age < this.OffAge) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.FreshString);
        }
        else if (this.OffAgeMax < 1000000000 && this.Age >= this.OffAgeMax) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.OffString);
        }
        if (this.isCooked() && !this.Burnt) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.CookedString);
        }
        else if (this.IsCookable && !this.Burnt && !(this instanceof DrainableComboItem)) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.UnCookedString);
        }
        if (this instanceof Food && ((Food)this).isFrozen()) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.FrozenString);
        }
        final String trim = s.trim();
        String s2;
        if (trim.isEmpty()) {
            objectTooltip.DrawText(font, s2 = this.getName(), 5.0, n, 1.0, 1.0, 0.800000011920929, 1.0);
        }
        else if (this.OffAgeMax < 1000000000 && this.Age >= this.OffAgeMax) {
            objectTooltip.DrawText(font, s2 = Translator.getText("IGUI_FoodNaming", trim, this.name), 5.0, n, 1.0, 0.10000000149011612, 0.10000000149011612, 1.0);
        }
        else {
            objectTooltip.DrawText(font, s2 = Translator.getText("IGUI_FoodNaming", trim, this.name), 5.0, n, 1.0, 1.0, 0.800000011920929, 1.0);
        }
        objectTooltip.adjustWidth(5, s2);
        int n2 = n + (lineSpacing + 5);
        if (this.extraItems != null) {
            objectTooltip.DrawText(font, Translator.getText("Tooltip_item_Contains"), 5.0, n2, 1.0, 1.0, 0.800000011920929, 1.0);
            int n3 = 5 + TextManager.instance.MeasureStringX(font, Translator.getText("Tooltip_item_Contains")) + 4;
            final int n4 = (lineSpacing - 10) / 2;
            for (int i = 0; i < this.extraItems.size(); ++i) {
                objectTooltip.DrawTextureScaled(InventoryItemFactory.CreateItem(this.extraItems.get(i)).getTex(), n3, n2 + n4, 10.0, 10.0, 1.0);
                n3 += 11;
            }
            n2 = n2 + lineSpacing + 5;
        }
        if (this instanceof Food && ((Food)this).spices != null) {
            objectTooltip.DrawText(font, Translator.getText("Tooltip_item_Spices"), 5.0, n2, 1.0, 1.0, 0.800000011920929, 1.0);
            int n5 = 5 + TextManager.instance.MeasureStringX(font, Translator.getText("Tooltip_item_Spices")) + 4;
            final int n6 = (lineSpacing - 10) / 2;
            for (int j = 0; j < ((Food)this).spices.size(); ++j) {
                objectTooltip.DrawTextureScaled(InventoryItemFactory.CreateItem(((Food)this).spices.get(j)).getTex(), n5, n2 + n6, 10.0, 10.0, 1.0);
                n5 += 11;
            }
            n2 = n2 + lineSpacing + 5;
        }
        final ObjectTooltip.Layout beginLayout = objectTooltip.beginLayout();
        beginLayout.setMinLabelWidth(80);
        final ObjectTooltip.LayoutItem addItem = beginLayout.addItem();
        addItem.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_item_Weight")), 1.0f, 1.0f, 0.8f, 1.0f);
        final boolean equipped = this.isEquipped();
        if (this instanceof HandWeapon || this instanceof Clothing || this instanceof DrainableComboItem) {
            if (equipped) {
                addItem.setValue(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getCleanString(this.getEquippedWeight()), this.getCleanString(this.getUnequippedWeight()), Translator.getText("Tooltip_item_Unequipped")), 1.0f, 1.0f, 1.0f, 1.0f);
            }
            else if (this.getAttachedSlot() > -1) {
                addItem.setValue(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getCleanString(this.getHotbarEquippedWeight()), this.getCleanString(this.getUnequippedWeight()), Translator.getText("Tooltip_item_Unequipped")), 1.0f, 1.0f, 1.0f, 1.0f);
            }
            else {
                addItem.setValue(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getCleanString(this.getUnequippedWeight()), this.getCleanString(this.getEquippedWeight()), Translator.getText("Tooltip_item_Equipped")), 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        else {
            float unequippedWeight = this.getUnequippedWeight();
            if (unequippedWeight > 0.0f && unequippedWeight < 0.01f) {
                unequippedWeight = 0.01f;
            }
            addItem.setValueRightNoPlus(unequippedWeight);
        }
        if (objectTooltip.getWeightOfStack() > 0.0f) {
            final ObjectTooltip.LayoutItem addItem2 = beginLayout.addItem();
            addItem2.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_item_StackWeight")), 1.0f, 1.0f, 0.8f, 1.0f);
            float weightOfStack = objectTooltip.getWeightOfStack();
            if (weightOfStack > 0.0f && weightOfStack < 0.01f) {
                weightOfStack = 0.01f;
            }
            addItem2.setValueRightNoPlus(weightOfStack);
        }
        if (this.getMaxAmmo() > 0 && !(this instanceof HandWeapon)) {
            final ObjectTooltip.LayoutItem addItem3 = beginLayout.addItem();
            addItem3.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_AmmoCount")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem3.setValue(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.getCurrentAmmoCount(), this.getMaxAmmo()), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.gunType != null) {
            Item item = ScriptManager.instance.FindItem(this.getGunType());
            if (item == null) {
                item = ScriptManager.instance.FindItem(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getModule(), this.ammoType));
            }
            if (item != null) {
                final ObjectTooltip.LayoutItem addItem4 = beginLayout.addItem();
                addItem4.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("ContextMenu_GunType")), 1.0f, 1.0f, 0.8f, 1.0f);
                addItem4.setValue(item.getDisplayName(), 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        if (Core.bDebug && DebugOptions.instance.TooltipInfo.getValue()) {
            final ObjectTooltip.LayoutItem addItem5 = beginLayout.addItem();
            addItem5.setLabel("getActualWeight()", 1.0f, 1.0f, 0.8f, 1.0f);
            addItem5.setValueRightNoPlus(this.getActualWeight());
            final ObjectTooltip.LayoutItem addItem6 = beginLayout.addItem();
            addItem6.setLabel("getWeight()", 1.0f, 1.0f, 0.8f, 1.0f);
            addItem6.setValueRightNoPlus(this.getWeight());
            final ObjectTooltip.LayoutItem addItem7 = beginLayout.addItem();
            addItem7.setLabel("getEquippedWeight()", 1.0f, 1.0f, 0.8f, 1.0f);
            addItem7.setValueRightNoPlus(this.getEquippedWeight());
            final ObjectTooltip.LayoutItem addItem8 = beginLayout.addItem();
            addItem8.setLabel("getUnequippedWeight()", 1.0f, 1.0f, 0.8f, 1.0f);
            addItem8.setValueRightNoPlus(this.getUnequippedWeight());
            final ObjectTooltip.LayoutItem addItem9 = beginLayout.addItem();
            addItem9.setLabel("getContentsWeight()", 1.0f, 1.0f, 0.8f, 1.0f);
            addItem9.setValueRightNoPlus(this.getContentsWeight());
            if (this instanceof Key || "Doorknob".equals(this.type)) {
                final ObjectTooltip.LayoutItem addItem10 = beginLayout.addItem();
                addItem10.setLabel("DBG: keyId", 1.0f, 1.0f, 0.8f, 1.0f);
                addItem10.setValueRightNoPlus(this.getKeyId());
            }
            final ObjectTooltip.LayoutItem addItem11 = beginLayout.addItem();
            addItem11.setLabel("ID", 1.0f, 1.0f, 0.8f, 1.0f);
            addItem11.setValueRightNoPlus(this.id);
            final ObjectTooltip.LayoutItem addItem12 = beginLayout.addItem();
            addItem12.setLabel("DictionaryID", 1.0f, 1.0f, 0.8f, 1.0f);
            addItem12.setValueRightNoPlus(this.registry_id);
            if (this.getClothingItem() != null) {
                final ObjectTooltip.LayoutItem addItem13 = beginLayout.addItem();
                addItem13.setLabel("ClothingItem", 1.0f, 1.0f, 1.0f, 1.0f);
                addItem13.setValue(this.getClothingItem().m_Name, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        if (this.getFatigueChange() != 0.0f) {
            final ObjectTooltip.LayoutItem addItem14 = beginLayout.addItem();
            addItem14.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_item_Fatigue")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem14.setValueRight((int)(this.getFatigueChange() * 100.0f), false);
        }
        if (this instanceof DrainableComboItem) {
            final ObjectTooltip.LayoutItem addItem15 = beginLayout.addItem();
            addItem15.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("IGUI_invpanel_Remaining")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem15.setProgress(((DrainableComboItem)this).getUsedDelta(), 0.0f, 0.6f, 0.0f, 0.7f);
        }
        if (this.isTaintedWater()) {
            beginLayout.addItem().setLabel(Translator.getText("Tooltip_item_TaintedWater"), 1.0f, 0.5f, 0.5f, 1.0f);
        }
        this.DoTooltip(objectTooltip, beginLayout);
        if (this.getRemoteControlID() != -1) {
            final ObjectTooltip.LayoutItem addItem16 = beginLayout.addItem();
            addItem16.setLabel(Translator.getText("Tooltip_TrapControllerID"), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem16.setValue(Integer.toString(this.getRemoteControlID()), 1.0f, 1.0f, 0.8f, 1.0f);
        }
        if (!FixingManager.getFixes(this).isEmpty()) {
            final ObjectTooltip.LayoutItem addItem17 = beginLayout.addItem();
            addItem17.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_Repaired")), 1.0f, 1.0f, 0.8f, 1.0f);
            if (this.getHaveBeenRepaired() == 1) {
                addItem17.setValue(Translator.getText("Tooltip_never"), 1.0f, 1.0f, 1.0f, 1.0f);
            }
            else {
                addItem17.setValue(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getHaveBeenRepaired() - 1), 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        if (this.isEquippedNoSprint()) {
            beginLayout.addItem().setLabel(Translator.getText("Tooltip_CantSprintEquipped"), 1.0f, 0.1f, 0.1f, 1.0f);
        }
        if (this.isWet()) {
            final ObjectTooltip.LayoutItem addItem18 = beginLayout.addItem();
            addItem18.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_Wetness")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem18.setProgress(this.getWetCooldown() / 10000.0f, 0.0f, 0.6f, 0.0f, 0.7f);
        }
        if (this.getMaxCapacity() > 0) {
            final ObjectTooltip.LayoutItem addItem19 = beginLayout.addItem();
            addItem19.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_container_Capacity")), 1.0f, 1.0f, 0.8f, 1.0f);
            float numberByCondition = (float)this.getMaxCapacity();
            if (this.isConditionAffectsCapacity()) {
                numberByCondition = VehiclePart.getNumberByCondition((float)this.getMaxCapacity(), (float)this.getCondition(), 5.0f);
            }
            if (this.getItemCapacity() > -1.0f) {
                addItem19.setValue(invokedynamic(makeConcatWithConstants:(FF)Ljava/lang/String;, this.getItemCapacity(), numberByCondition), 1.0f, 1.0f, 0.8f, 1.0f);
            }
            else {
                addItem19.setValue(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, numberByCondition), 1.0f, 1.0f, 0.8f, 1.0f);
            }
        }
        if (this.getConditionMax() > 0 && this.getMechanicType() > 0) {
            final ObjectTooltip.LayoutItem addItem20 = beginLayout.addItem();
            addItem20.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_weapon_Condition")), 1.0f, 1.0f, 0.8f, 1.0f);
            addItem20.setValue(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.getCondition(), this.getConditionMax()), 1.0f, 1.0f, 0.8f, 1.0f);
        }
        if (this.isRecordedMedia()) {
            final MediaData mediaData = this.getMediaData();
            if (mediaData != null) {
                if (mediaData.getTranslatedTitle() != null) {
                    final ObjectTooltip.LayoutItem addItem21 = beginLayout.addItem();
                    addItem21.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_media_title")), 1.0f, 1.0f, 0.8f, 1.0f);
                    addItem21.setValue(mediaData.getTranslatedTitle(), 1.0f, 1.0f, 1.0f, 1.0f);
                    if (mediaData.getTranslatedSubTitle() != null) {
                        final ObjectTooltip.LayoutItem addItem22 = beginLayout.addItem();
                        addItem22.setLabel("", 1.0f, 1.0f, 0.8f, 1.0f);
                        addItem22.setValue(mediaData.getTranslatedSubTitle(), 1.0f, 1.0f, 1.0f, 1.0f);
                    }
                }
                if (mediaData.getTranslatedAuthor() != null) {
                    final ObjectTooltip.LayoutItem addItem23 = beginLayout.addItem();
                    addItem23.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Translator.getText("Tooltip_media_author")), 1.0f, 1.0f, 0.8f, 1.0f);
                    addItem23.setValue(mediaData.getTranslatedAuthor(), 1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
        if (DebugOptions.instance.TooltipModName.getValue() && !this.isVanilla()) {
            final ObjectTooltip.LayoutItem addItem24 = beginLayout.addItem();
            final Color cornFlowerBlue = Colors.CornFlowerBlue;
            addItem24.setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getModName()), cornFlowerBlue.r, cornFlowerBlue.g, cornFlowerBlue.b, 1.0f);
            final ItemInfo itemInfoFromID = WorldDictionary.getItemInfoFromID(this.registry_id);
            if (itemInfoFromID != null && itemInfoFromID.getModOverrides() != null) {
                final ObjectTooltip.LayoutItem addItem25 = beginLayout.addItem();
                final float n7 = 0.5f;
                addItem25.setLabel("This item overrides:", n7, n7, n7, 1.0f);
                for (int k = 0; k < itemInfoFromID.getModOverrides().size(); ++k) {
                    beginLayout.addItem().setLabel(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, WorldDictionary.getModNameFromID(itemInfoFromID.getModOverrides().get(k))), n7, n7, n7, 1.0f);
                }
            }
        }
        if (this.getTooltip() != null) {
            beginLayout.addItem().setLabel(Translator.getText(this.tooltip), 1.0f, 1.0f, 0.8f, 1.0f);
        }
        final int render = beginLayout.render(5, n2, objectTooltip);
        objectTooltip.endLayout(beginLayout);
        objectTooltip.setHeight(render + objectTooltip.padBottom);
        if (objectTooltip.getWidth() < 150.0) {
            objectTooltip.setWidth(150.0);
        }
    }
    
    public String getCleanString(final float n) {
        return Float.toString((int)((n + 0.005) * 100.0) / 100.0f);
    }
    
    public void DoTooltip(final ObjectTooltip objectTooltip, final ObjectTooltip.Layout layout) {
    }
    
    public void SetContainerPosition(final int containerX, final int containerY) {
        this.containerX = containerX;
        this.containerY = containerY;
    }
    
    public void Use() {
        this.Use(false);
    }
    
    public void UseItem() {
        this.Use(false);
    }
    
    public void Use(final boolean b) {
        this.Use(b, false);
    }
    
    public void Use(final boolean b, final boolean b2) {
        if (!this.isDisappearOnUse() && !b) {
            return;
        }
        --this.uses;
        if (this.replaceOnUse != null && !b2 && !b && this.container != null) {
            String replaceOnUse = this.replaceOnUse;
            if (!this.replaceOnUse.contains(".")) {
                replaceOnUse = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.module, replaceOnUse);
            }
            final InventoryItem addItem = this.container.AddItem(replaceOnUse);
            if (addItem != null) {
                addItem.setConditionFromModData(this);
            }
            this.container.setDrawDirty(true);
            this.container.setDirty(true);
            addItem.setFavorite(this.isFavorite());
        }
        if (this.uses <= 0) {
            if (this.keepOnDeplete) {
                return;
            }
            if (this.container != null) {
                if (this.container.parent instanceof IsoGameCharacter && !(this instanceof HandWeapon)) {
                    ((IsoGameCharacter)this.container.parent).removeFromHands(this);
                }
                this.container.Items.remove(this);
                this.container.setDirty(true);
                this.container.setDrawDirty(true);
                this.container = null;
            }
        }
    }
    
    public boolean shouldUpdateInWorld() {
        if (!GameClient.bClient && !this.rainFactorZero && this.canStoreWater() && this.getReplaceOnUseOn() != null && this.getReplaceOnUseOnString() != null) {
            final IsoGridSquare square = this.getWorldItem().getSquare();
            return square != null && square.isOutside();
        }
        return false;
    }
    
    public void update() {
        if (this.isWet()) {
            this.wetCooldown -= 1.0f * GameTime.instance.getMultiplier();
            if (this.wetCooldown <= 0.0f) {
                final InventoryItem createItem = InventoryItemFactory.CreateItem(this.itemWhenDry);
                if (this.isFavorite()) {
                    createItem.setFavorite(true);
                }
                final IsoWorldInventoryObject worldItem = this.getWorldItem();
                if (worldItem != null) {
                    final IsoGridSquare square = worldItem.getSquare();
                    square.AddWorldInventoryItem(createItem, worldItem.getX() % 1.0f, worldItem.getY() % 1.0f, worldItem.getZ() % 1.0f);
                    square.transmitRemoveItemFromSquare(worldItem);
                    if (this.getContainer() != null) {
                        this.getContainer().setDirty(true);
                        this.getContainer().setDrawDirty(true);
                    }
                    square.chunk.recalcHashCodeObjects();
                    this.setWorldItem(null);
                }
                else if (this.getContainer() != null) {
                    this.getContainer().addItem(createItem);
                    this.getContainer().Remove(this);
                }
                this.setWet(false);
                IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
                LuaEventManager.triggerEvent("OnContainerUpdate");
            }
        }
        if (!GameClient.bClient && !this.rainFactorZero && this.getWorldItem() != null && this.canStoreWater() && this.getReplaceOnUseOn() != null && this.getReplaceOnUseOnString() != null && RainManager.isRaining()) {
            final IsoWorldInventoryObject worldItem2 = this.getWorldItem();
            final IsoGridSquare square2 = worldItem2.getSquare();
            if (square2 != null && square2.isOutside()) {
                final InventoryItem createItem2 = InventoryItemFactory.CreateItem(this.getReplaceOnUseOnString());
                createItem2.setCondition(this.getCondition());
                if (createItem2 instanceof DrainableComboItem && createItem2.canStoreWater()) {
                    if (((DrainableComboItem)createItem2).getRainFactor() == 0.0f) {
                        this.rainFactorZero = true;
                        return;
                    }
                    ((DrainableComboItem)createItem2).setUsedDelta(0.0f);
                    worldItem2.swapItem(createItem2);
                }
            }
        }
    }
    
    public boolean finishupdate() {
        return (GameClient.bClient || this.rainFactorZero || !this.canStoreWater() || this.getReplaceOnUseOn() == null || this.getReplaceOnUseOnString() == null || this.getWorldItem() == null || this.getWorldItem().getObjectIndex() == -1) && !this.isWet();
    }
    
    public void updateSound(final BaseSoundEmitter baseSoundEmitter) {
    }
    
    public String getFullType() {
        assert this.fullType != null && this.fullType.equals(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.module, this.type));
        return this.fullType;
    }
    
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        if (GameWindow.DEBUG_SAVE) {
            DebugLog.log(this.getFullType());
        }
        byteBuffer.putShort(this.registry_id);
        byteBuffer.put((byte)this.getSaveType());
        byteBuffer.putInt(this.id);
        final BitHeaderWrite allocWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, byteBuffer);
        if (this.uses != 1) {
            allocWrite.addFlags(1);
            if (this.uses > 32767) {
                byteBuffer.putShort((short)32767);
            }
            else {
                byteBuffer.putShort((short)this.uses);
            }
        }
        if (this.IsDrainable() && ((DrainableComboItem)this).getUsedDelta() < 1.0f) {
            allocWrite.addFlags(2);
            byteBuffer.put((byte)((byte)(((DrainableComboItem)this).getUsedDelta() * 255.0f) - 128));
        }
        if (this.Condition != this.ConditionMax) {
            allocWrite.addFlags(4);
            byteBuffer.put((byte)this.getCondition());
        }
        if (this.visual != null) {
            allocWrite.addFlags(8);
            this.visual.save(byteBuffer);
        }
        if (this.isCustomColor() && (this.col.r != 1.0f || this.col.g != 1.0f || this.col.b != 1.0f || this.col.a != 1.0f)) {
            allocWrite.addFlags(16);
            byteBuffer.put(Bits.packFloatUnitToByte(this.getColor().r));
            byteBuffer.put(Bits.packFloatUnitToByte(this.getColor().g));
            byteBuffer.put(Bits.packFloatUnitToByte(this.getColor().b));
            byteBuffer.put(Bits.packFloatUnitToByte(this.getColor().a));
        }
        if (this.itemCapacity != -1.0f) {
            allocWrite.addFlags(32);
            byteBuffer.putFloat(this.itemCapacity);
        }
        final BitHeaderWrite allocWrite2 = BitHeader.allocWrite(BitHeader.HeaderSize.Integer, byteBuffer);
        if (this.table != null && !this.table.isEmpty()) {
            allocWrite2.addFlags(1);
            this.table.save(byteBuffer);
        }
        if (this.isActivated()) {
            allocWrite2.addFlags(2);
        }
        if (this.haveBeenRepaired != 1) {
            allocWrite2.addFlags(4);
            byteBuffer.putShort((short)this.getHaveBeenRepaired());
        }
        if (this.name != null && !this.name.equals(this.originalName)) {
            allocWrite2.addFlags(8);
            GameWindow.WriteString(byteBuffer, this.name);
        }
        if (this.byteData != null) {
            allocWrite2.addFlags(16);
            this.byteData.rewind();
            byteBuffer.putInt(this.byteData.limit());
            byteBuffer.put(this.byteData);
            this.byteData.flip();
        }
        if (this.extraItems != null && this.extraItems.size() > 0) {
            allocWrite2.addFlags(32);
            byteBuffer.putInt(this.extraItems.size());
            for (int i = 0; i < this.extraItems.size(); ++i) {
                byteBuffer.putShort(WorldDictionary.getItemRegistryID(this.extraItems.get(i)));
            }
        }
        if (this.isCustomName()) {
            allocWrite2.addFlags(64);
        }
        if (this.isCustomWeight()) {
            allocWrite2.addFlags(128);
            byteBuffer.putFloat(this.isCustomWeight() ? this.getActualWeight() : -1.0f);
        }
        if (this.keyId != -1) {
            allocWrite2.addFlags(256);
            byteBuffer.putInt(this.getKeyId());
        }
        if (this.isTaintedWater()) {
            allocWrite2.addFlags(512);
        }
        if (this.remoteControlID != -1 || this.remoteRange != 0) {
            allocWrite2.addFlags(1024);
            byteBuffer.putInt(this.getRemoteControlID());
            byteBuffer.putInt(this.getRemoteRange());
        }
        if (this.colorRed != 1.0f || this.colorGreen != 1.0f || this.colorBlue != 1.0f) {
            allocWrite2.addFlags(2048);
            byteBuffer.put(Bits.packFloatUnitToByte(this.colorRed));
            byteBuffer.put(Bits.packFloatUnitToByte(this.colorGreen));
            byteBuffer.put(Bits.packFloatUnitToByte(this.colorBlue));
        }
        if (this.worker != null) {
            allocWrite2.addFlags(4096);
            GameWindow.WriteString(byteBuffer, this.getWorker());
        }
        if (this.wetCooldown != -1.0f) {
            allocWrite2.addFlags(8192);
            byteBuffer.putFloat(this.wetCooldown);
        }
        if (this.isFavorite()) {
            allocWrite2.addFlags(16384);
        }
        if (this.stashMap != null) {
            allocWrite2.addFlags(32768);
            GameWindow.WriteString(byteBuffer, this.stashMap);
        }
        if (this.isInfected()) {
            allocWrite2.addFlags(65536);
        }
        if (this.currentAmmoCount != 0) {
            allocWrite2.addFlags(131072);
            byteBuffer.putInt(this.currentAmmoCount);
        }
        if (this.attachedSlot != -1) {
            allocWrite2.addFlags(262144);
            byteBuffer.putInt(this.attachedSlot);
        }
        if (this.attachedSlotType != null) {
            allocWrite2.addFlags(524288);
            GameWindow.WriteString(byteBuffer, this.attachedSlotType);
        }
        if (this.attachedToModel != null) {
            allocWrite2.addFlags(1048576);
            GameWindow.WriteString(byteBuffer, this.attachedToModel);
        }
        if (this.maxCapacity != -1) {
            allocWrite2.addFlags(2097152);
            byteBuffer.putInt(this.maxCapacity);
        }
        if (this.isRecordedMedia()) {
            allocWrite2.addFlags(4194304);
            byteBuffer.putShort(this.recordedMediaIndex);
        }
        if (this.worldZRotation > -1) {
            allocWrite2.addFlags(8388608);
            byteBuffer.putInt(this.worldZRotation);
        }
        if (this.worldScale != 1.0f) {
            allocWrite2.addFlags(16777216);
            byteBuffer.putFloat(this.worldScale);
        }
        if (!allocWrite2.equals(0)) {
            allocWrite.addFlags(64);
            allocWrite2.write();
        }
        else {
            byteBuffer.position(allocWrite2.getStartPosition());
        }
        allocWrite.write();
        allocWrite.release();
        allocWrite2.release();
    }
    
    public static InventoryItem loadItem(final ByteBuffer byteBuffer, final int n) throws IOException {
        return loadItem(byteBuffer, n, true);
    }
    
    public static InventoryItem loadItem(final ByteBuffer byteBuffer, final int n, final boolean b) throws IOException {
        final int int1 = byteBuffer.getInt();
        if (int1 <= 0) {
            throw new IOException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, int1));
        }
        final int position = byteBuffer.position();
        final short short1 = byteBuffer.getShort();
        int value = -1;
        if (n >= 70) {
            value = byteBuffer.get();
            if (value < 0) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(BLjava/lang/String;)Ljava/lang/String;, value, WorldDictionary.getItemTypeDebugString(short1)));
                return null;
            }
        }
        InventoryItem createItem = InventoryItemFactory.CreateItem(short1);
        if (b && value != -1 && createItem != null && createItem.getSaveType() != value) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;BI)Ljava/lang/String;, createItem.getFullType(), value, createItem.getSaveType()));
            createItem = null;
        }
        if (createItem != null) {
            try {
                createItem.load(byteBuffer, n);
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
                createItem = null;
            }
        }
        if (createItem == null) {
            if (byteBuffer.position() < position + int1) {
                while (byteBuffer.position() < position + int1) {
                    byteBuffer.get();
                }
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, WorldDictionary.getItemTypeDebugString(short1)));
            }
            else if (byteBuffer.position() >= position + int1) {
                byteBuffer.position(position + int1);
                DebugLog.log(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, position + int1, WorldDictionary.getItemTypeDebugString(short1)));
            }
            return null;
        }
        if (int1 != -1 && byteBuffer.position() != position + int1) {
            byteBuffer.position(position + int1);
            DebugLog.log(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, position + int1, WorldDictionary.getItemTypeDebugString(short1)));
            if (Core.bDebug) {
                throw new IOException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, WorldDictionary.getItemTypeDebugString(short1)));
            }
        }
        return createItem;
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        this.id = byteBuffer.getInt();
        final BitHeaderRead allocRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
        if (!allocRead.equals(0)) {
            if (allocRead.hasFlags(1)) {
                this.uses = byteBuffer.getShort();
            }
            if (allocRead.hasFlags(2)) {
                ((DrainableComboItem)this).setUsedDelta(PZMath.clamp((byteBuffer.get() + 128) / 255.0f, 0.0f, 1.0f));
            }
            if (allocRead.hasFlags(4)) {
                this.setCondition(byteBuffer.get(), false);
            }
            if (allocRead.hasFlags(8)) {
                (this.visual = new ItemVisual()).load(byteBuffer, n);
            }
            if (allocRead.hasFlags(16)) {
                this.setColor(new Color(Bits.unpackByteToFloatUnit(byteBuffer.get()), Bits.unpackByteToFloatUnit(byteBuffer.get()), Bits.unpackByteToFloatUnit(byteBuffer.get()), Bits.unpackByteToFloatUnit(byteBuffer.get())));
            }
            if (allocRead.hasFlags(32)) {
                this.itemCapacity = byteBuffer.getFloat();
            }
            if (allocRead.hasFlags(64)) {
                final BitHeaderRead allocRead2 = BitHeader.allocRead(BitHeader.HeaderSize.Integer, byteBuffer);
                if (allocRead2.hasFlags(1)) {
                    if (this.table == null) {
                        this.table = LuaManager.platform.newTable();
                    }
                    this.table.load(byteBuffer, n);
                }
                this.activated = allocRead2.hasFlags(2);
                if (allocRead2.hasFlags(4)) {
                    this.setHaveBeenRepaired(byteBuffer.getShort());
                }
                if (allocRead2.hasFlags(8)) {
                    this.name = GameWindow.ReadString(byteBuffer);
                }
                if (allocRead2.hasFlags(16)) {
                    final int int1 = byteBuffer.getInt();
                    this.byteData = ByteBuffer.allocate(int1);
                    for (int i = 0; i < int1; ++i) {
                        this.byteData.put(byteBuffer.get());
                    }
                    this.byteData.flip();
                }
                if (allocRead2.hasFlags(32)) {
                    final int int2 = byteBuffer.getInt();
                    if (int2 > 0) {
                        this.extraItems = new ArrayList<String>();
                        for (int j = 0; j < int2; ++j) {
                            this.extraItems.add(WorldDictionary.getItemTypeFromID(byteBuffer.getShort()));
                        }
                    }
                }
                this.setCustomName(allocRead2.hasFlags(64));
                if (allocRead2.hasFlags(128)) {
                    final float float1 = byteBuffer.getFloat();
                    if (float1 >= 0.0f) {
                        this.setActualWeight(float1);
                        this.setWeight(float1);
                        this.setCustomWeight(true);
                    }
                }
                if (allocRead2.hasFlags(256)) {
                    this.setKeyId(byteBuffer.getInt());
                }
                this.setTaintedWater(allocRead2.hasFlags(512));
                if (allocRead2.hasFlags(1024)) {
                    this.setRemoteControlID(byteBuffer.getInt());
                    this.setRemoteRange(byteBuffer.getInt());
                }
                if (allocRead2.hasFlags(2048)) {
                    final float unpackByteToFloatUnit = Bits.unpackByteToFloatUnit(byteBuffer.get());
                    final float unpackByteToFloatUnit2 = Bits.unpackByteToFloatUnit(byteBuffer.get());
                    final float unpackByteToFloatUnit3 = Bits.unpackByteToFloatUnit(byteBuffer.get());
                    this.setColorRed(unpackByteToFloatUnit);
                    this.setColorGreen(unpackByteToFloatUnit2);
                    this.setColorBlue(unpackByteToFloatUnit3);
                    this.setColor(new Color(this.colorRed, this.colorGreen, this.colorBlue));
                }
                if (allocRead2.hasFlags(4096)) {
                    this.setWorker(GameWindow.ReadString(byteBuffer));
                }
                if (allocRead2.hasFlags(8192)) {
                    this.setWetCooldown(byteBuffer.getFloat());
                }
                this.setFavorite(allocRead2.hasFlags(16384));
                if (allocRead2.hasFlags(32768)) {
                    this.stashMap = GameWindow.ReadString(byteBuffer);
                }
                this.setInfected(allocRead2.hasFlags(65536));
                if (allocRead2.hasFlags(131072)) {
                    this.setCurrentAmmoCount(byteBuffer.getInt());
                }
                if (allocRead2.hasFlags(262144)) {
                    this.attachedSlot = byteBuffer.getInt();
                }
                if (allocRead2.hasFlags(524288)) {
                    if (n < 179) {
                        byteBuffer.getShort();
                        this.attachedSlotType = null;
                    }
                    else {
                        this.attachedSlotType = GameWindow.ReadString(byteBuffer);
                    }
                }
                if (allocRead2.hasFlags(1048576)) {
                    this.attachedToModel = GameWindow.ReadString(byteBuffer);
                }
                if (allocRead2.hasFlags(2097152)) {
                    this.maxCapacity = byteBuffer.getInt();
                }
                if (allocRead2.hasFlags(4194304)) {
                    this.setRecordedMediaIndex(byteBuffer.getShort());
                }
                if (allocRead2.hasFlags(8388608)) {
                    this.setWorldZRotation(byteBuffer.getInt());
                }
                if (allocRead2.hasFlags(16777216)) {
                    this.worldScale = byteBuffer.getFloat();
                }
                allocRead2.release();
            }
        }
        allocRead.release();
    }
    
    public boolean IsFood() {
        return false;
    }
    
    public boolean IsWeapon() {
        return false;
    }
    
    public boolean IsDrainable() {
        return false;
    }
    
    public boolean IsLiterature() {
        return false;
    }
    
    public boolean IsClothing() {
        return false;
    }
    
    public boolean IsInventoryContainer() {
        return false;
    }
    
    public boolean IsMap() {
        return false;
    }
    
    static InventoryItem LoadFromFile(final DataInputStream dataInputStream) throws IOException {
        GameWindow.ReadString(dataInputStream);
        return null;
    }
    
    public ItemContainer getOutermostContainer() {
        if (this.container == null || "floor".equals(this.container.type)) {
            return null;
        }
        ItemContainer itemContainer;
        for (itemContainer = this.container; itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getContainer() != null && !"floor".equals(itemContainer.getContainingItem().getContainer().type); itemContainer = itemContainer.getContainingItem().getContainer()) {}
        return itemContainer;
    }
    
    public boolean isInLocalPlayerInventory() {
        if (!GameClient.bClient) {
            return false;
        }
        final ItemContainer outermostContainer = this.getOutermostContainer();
        return outermostContainer != null && outermostContainer.getParent() instanceof IsoPlayer && ((IsoPlayer)outermostContainer.getParent()).isLocalPlayer();
    }
    
    public boolean isInPlayerInventory() {
        final ItemContainer outermostContainer = this.getOutermostContainer();
        return outermostContainer != null && outermostContainer.getParent() instanceof IsoPlayer;
    }
    
    public ItemReplacement getItemReplacementPrimaryHand() {
        return this.ScriptItem.replacePrimaryHand;
    }
    
    public ItemReplacement getItemReplacementSecondHand() {
        return this.ScriptItem.replaceSecondHand;
    }
    
    public ClothingItem getClothingItem() {
        if ("RightHand".equalsIgnoreCase(this.getAlternateModelName())) {
            return this.getItemReplacementPrimaryHand().clothingItem;
        }
        if ("LeftHand".equalsIgnoreCase(this.getAlternateModelName())) {
            return this.getItemReplacementSecondHand().clothingItem;
        }
        return this.ScriptItem.getClothingItemAsset();
    }
    
    public String getAlternateModelName() {
        if (this.getContainer() != null && this.getContainer().getParent() instanceof IsoGameCharacter) {
            final IsoGameCharacter isoGameCharacter = (IsoGameCharacter)this.getContainer().getParent();
            if (isoGameCharacter.getPrimaryHandItem() == this && this.getItemReplacementPrimaryHand() != null) {
                return "RightHand";
            }
            if (isoGameCharacter.getSecondaryHandItem() == this && this.getItemReplacementSecondHand() != null) {
                return "LeftHand";
            }
        }
        return this.m_alternateModelName;
    }
    
    public ItemVisual getVisual() {
        final ClothingItem clothingItem = this.getClothingItem();
        if (clothingItem == null || !clothingItem.isReady()) {
            return this.visual = null;
        }
        if (this.visual == null) {
            (this.visual = new ItemVisual()).setItemType(this.getFullType());
            this.visual.pickUninitializedValues(clothingItem);
        }
        this.visual.setClothingItemName(clothingItem.m_Name);
        this.visual.setAlternateModelName(this.getAlternateModelName());
        return this.visual;
    }
    
    public boolean allowRandomTint() {
        final ClothingItem clothingItem = this.getClothingItem();
        return clothingItem != null && clothingItem.m_AllowRandomTint;
    }
    
    public void synchWithVisual() {
        if (!(this instanceof Clothing) && !(this instanceof InventoryContainer)) {
            return;
        }
        final ItemVisual visual = this.getVisual();
        if (visual == null) {
            return;
        }
        if (this instanceof Clothing && this.getBloodClothingType() != null) {
            BloodClothingType.calcTotalBloodLevel((Clothing)this);
        }
        final ClothingItem clothingItem = this.getClothingItem();
        if (clothingItem.m_AllowRandomTint) {
            this.setColor(new Color(visual.m_Tint.r, visual.m_Tint.g, visual.m_Tint.b));
        }
        else {
            this.setColor(new Color(this.getColorRed(), this.getColorGreen(), this.getColorBlue()));
        }
        if ((clothingItem.m_BaseTextures.size() <= 1 && visual.m_TextureChoice <= -1) || this.getIconsForTexture() == null) {
            return;
        }
        String s = null;
        if (visual.m_BaseTexture > -1 && this.getIconsForTexture().size() > visual.m_BaseTexture) {
            s = this.getIconsForTexture().get(visual.m_BaseTexture);
        }
        else if (visual.m_TextureChoice > -1 && this.getIconsForTexture().size() > visual.m_TextureChoice) {
            s = this.getIconsForTexture().get(visual.m_TextureChoice);
        }
        if (StringUtils.isNullOrWhitespace(s)) {
            return;
        }
        this.texture = Texture.trygetTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        if (this.texture == null) {
            this.texture = Texture.getSharedTexture("media/inventory/Question_On.png");
        }
    }
    
    public int getContainerX() {
        return this.containerX;
    }
    
    public void setContainerX(final int containerX) {
        this.containerX = containerX;
    }
    
    public int getContainerY() {
        return this.containerY;
    }
    
    public void setContainerY(final int containerY) {
        this.containerY = containerY;
    }
    
    public boolean isDisappearOnUse() {
        return this.getScriptItem().isDisappearOnUse();
    }
    
    public String getName() {
        if (this.isBroken()) {
            return Translator.getText("IGUI_ItemNaming", this.brokenString, this.name);
        }
        if (this.isTaintedWater()) {
            return Translator.getText("IGUI_ItemNameTaintedWater", this.name);
        }
        if (this.getRemoteControlID() != -1) {
            return Translator.getText("IGUI_ItemNameControllerLinked", this.name);
        }
        if (this.getMechanicType() > 0) {
            return Translator.getText("IGUI_ItemNameMechanicalType", this.name, Translator.getText(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getMechanicType())));
        }
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getReplaceOnUse() {
        return this.replaceOnUse;
    }
    
    public void setReplaceOnUse(final String replaceOnUse) {
        this.replaceOnUse = replaceOnUse;
        this.replaceOnUseFullType = StringUtils.moduleDotType(this.getModule(), replaceOnUse);
    }
    
    public String getReplaceOnUseFullType() {
        return this.replaceOnUseFullType;
    }
    
    public int getConditionMax() {
        return this.ConditionMax;
    }
    
    public void setConditionMax(final int conditionMax) {
        this.ConditionMax = conditionMax;
    }
    
    public ItemContainer getRightClickContainer() {
        return this.rightClickContainer;
    }
    
    public void setRightClickContainer(final ItemContainer rightClickContainer) {
        this.rightClickContainer = rightClickContainer;
    }
    
    public String getSwingAnim() {
        return this.getScriptItem().SwingAnim;
    }
    
    public Texture getTexture() {
        return this.texture;
    }
    
    public void setTexture(final Texture texture) {
        this.texture = texture;
    }
    
    public Texture getTexturerotten() {
        return this.texturerotten;
    }
    
    public void setTexturerotten(final Texture texturerotten) {
        this.texturerotten = texturerotten;
    }
    
    public Texture getTextureCooked() {
        return this.textureCooked;
    }
    
    public void setTextureCooked(final Texture textureCooked) {
        this.textureCooked = textureCooked;
    }
    
    public Texture getTextureBurnt() {
        return this.textureBurnt;
    }
    
    public void setTextureBurnt(final Texture textureBurnt) {
        this.textureBurnt = textureBurnt;
    }
    
    public void setType(final String type) {
        this.type = type;
        this.fullType = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.module, type);
    }
    
    public int getUses() {
        return 1;
    }
    
    public void setUses(final int n) {
    }
    
    public float getAge() {
        return this.Age;
    }
    
    public void setAge(final float age) {
        this.Age = age;
    }
    
    public float getLastAged() {
        return this.LastAged;
    }
    
    public void setLastAged(final float lastAged) {
        this.LastAged = lastAged;
    }
    
    public void updateAge() {
    }
    
    public void setAutoAge() {
    }
    
    public boolean isIsCookable() {
        return this.IsCookable;
    }
    
    public boolean isCookable() {
        return this.IsCookable;
    }
    
    public void setIsCookable(final boolean isCookable) {
        this.IsCookable = isCookable;
    }
    
    public float getCookingTime() {
        return this.CookingTime;
    }
    
    public void setCookingTime(final float cookingTime) {
        this.CookingTime = cookingTime;
    }
    
    public float getMinutesToCook() {
        return this.MinutesToCook;
    }
    
    public void setMinutesToCook(final float minutesToCook) {
        this.MinutesToCook = minutesToCook;
    }
    
    public float getMinutesToBurn() {
        return this.MinutesToBurn;
    }
    
    public void setMinutesToBurn(final float minutesToBurn) {
        this.MinutesToBurn = minutesToBurn;
    }
    
    public boolean isCooked() {
        return this.Cooked;
    }
    
    public void setCooked(final boolean cooked) {
        this.Cooked = cooked;
    }
    
    public boolean isBurnt() {
        return this.Burnt;
    }
    
    public void setBurnt(final boolean burnt) {
        this.Burnt = burnt;
    }
    
    public int getOffAge() {
        return this.OffAge;
    }
    
    public void setOffAge(final int offAge) {
        this.OffAge = offAge;
    }
    
    public int getOffAgeMax() {
        return this.OffAgeMax;
    }
    
    public void setOffAgeMax(final int offAgeMax) {
        this.OffAgeMax = offAgeMax;
    }
    
    public float getWeight() {
        return this.Weight;
    }
    
    public void setWeight(final float weight) {
        this.Weight = weight;
    }
    
    public float getActualWeight() {
        if (this.getDisplayName().equals(this.getFullType())) {
            return 0.0f;
        }
        return this.ActualWeight;
    }
    
    public void setActualWeight(final float actualWeight) {
        this.ActualWeight = actualWeight;
    }
    
    public String getWorldTexture() {
        return this.WorldTexture;
    }
    
    public void setWorldTexture(final String worldTexture) {
        this.WorldTexture = worldTexture;
    }
    
    public String getDescription() {
        return this.Description;
    }
    
    public void setDescription(final String description) {
        this.Description = description;
    }
    
    public int getCondition() {
        return this.Condition;
    }
    
    public void setCondition(int max, final boolean b) {
        max = Math.max(0, max);
        if (this.Condition > 0 && max <= 0 && b && this.getBreakSound() != null && !this.getBreakSound().isEmpty() && IsoPlayer.getInstance() != null) {
            IsoPlayer.getInstance().playSound(this.getBreakSound());
        }
        this.setBroken((this.Condition = max) <= 0);
    }
    
    public void setCondition(final int n) {
        this.setCondition(n, true);
    }
    
    public String getOffString() {
        return this.OffString;
    }
    
    public void setOffString(final String offString) {
        this.OffString = offString;
    }
    
    public String getCookedString() {
        return this.CookedString;
    }
    
    public void setCookedString(final String cookedString) {
        this.CookedString = cookedString;
    }
    
    public String getUnCookedString() {
        return this.UnCookedString;
    }
    
    public void setUnCookedString(final String unCookedString) {
        this.UnCookedString = unCookedString;
    }
    
    public String getBurntString() {
        return this.BurntString;
    }
    
    public void setBurntString(final String burntString) {
        this.BurntString = burntString;
    }
    
    public String getModule() {
        return this.module;
    }
    
    public void setModule(final String module) {
        this.module = module;
        this.fullType = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, module, this.type);
    }
    
    public boolean isAlwaysWelcomeGift() {
        return this.getScriptItem().isAlwaysWelcomeGift();
    }
    
    public boolean isCanBandage() {
        return this.getScriptItem().isCanBandage();
    }
    
    public float getBoredomChange() {
        return this.boredomChange;
    }
    
    public void setBoredomChange(final float boredomChange) {
        this.boredomChange = boredomChange;
    }
    
    public float getUnhappyChange() {
        return this.unhappyChange;
    }
    
    public void setUnhappyChange(final float unhappyChange) {
        this.unhappyChange = unhappyChange;
    }
    
    public float getStressChange() {
        return this.stressChange;
    }
    
    public void setStressChange(final float stressChange) {
        this.stressChange = stressChange;
    }
    
    public ArrayList<String> getTags() {
        return this.ScriptItem.getTags();
    }
    
    public boolean hasTag(final String anotherString) {
        final ArrayList<String> tags = this.getTags();
        for (int i = 0; i < tags.size(); ++i) {
            if (tags.get(i).equalsIgnoreCase(anotherString)) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<IsoObject> getTaken() {
        return this.Taken;
    }
    
    public void setTaken(final ArrayList<IsoObject> taken) {
        this.Taken = taken;
    }
    
    public IsoDirections getPlaceDir() {
        return this.placeDir;
    }
    
    public void setPlaceDir(final IsoDirections placeDir) {
        this.placeDir = placeDir;
    }
    
    public IsoDirections getNewPlaceDir() {
        return this.newPlaceDir;
    }
    
    public void setNewPlaceDir(final IsoDirections newPlaceDir) {
        this.newPlaceDir = newPlaceDir;
    }
    
    public void setReplaceOnUseOn(final String replaceOnUseOn) {
        this.ReplaceOnUseOn = replaceOnUseOn;
    }
    
    public String getReplaceOnUseOn() {
        return this.ReplaceOnUseOn;
    }
    
    public String getReplaceOnUseOnString() {
        String replaceOnUseOn = this.getReplaceOnUseOn();
        if (replaceOnUseOn.split("-")[0].trim().contains("WaterSource")) {
            final String s;
            replaceOnUseOn = (s = replaceOnUseOn.split("-")[1]);
            if (!replaceOnUseOn.contains(".")) {
                replaceOnUseOn = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getModule(), s);
            }
        }
        return replaceOnUseOn;
    }
    
    public void setIsWaterSource(final boolean isWaterSource) {
        this.IsWaterSource = isWaterSource;
    }
    
    public boolean isWaterSource() {
        return this.IsWaterSource;
    }
    
    boolean CanStackNoTemp(final InventoryItem inventoryItem) {
        return false;
    }
    
    public void CopyModData(final KahluaTable kahluaTable) {
        this.copyModData(kahluaTable);
    }
    
    public void copyModData(final KahluaTable kahluaTable) {
        if (this.table != null) {
            this.table.wipe();
        }
        if (kahluaTable == null) {
            return;
        }
        LuaManager.copyTable(this.getModData(), kahluaTable);
    }
    
    public int getCount() {
        return this.Count;
    }
    
    public void setCount(final int count) {
        this.Count = count;
    }
    
    public boolean isActivated() {
        return this.activated;
    }
    
    public void setActivated(final boolean activated) {
        this.activated = activated;
        if (this.canEmitLight() && GameClient.bClient && this.getEquipParent() != null) {
            if (this.getEquipParent().getPrimaryHandItem() == this) {
                this.getEquipParent().reportEvent("EventSetActivatedPrimary");
            }
            else if (this.getEquipParent().getSecondaryHandItem() == this) {
                this.getEquipParent().reportEvent("EventSetActivatedSecondary");
            }
        }
    }
    
    public void setActivatedRemote(final boolean activated) {
        this.activated = activated;
    }
    
    public void setCanBeActivated(final boolean canBeActivated) {
        this.canBeActivated = canBeActivated;
    }
    
    public boolean canBeActivated() {
        return this.canBeActivated;
    }
    
    public void setLightStrength(final float lightStrength) {
        this.lightStrength = lightStrength;
    }
    
    public float getLightStrength() {
        return this.lightStrength;
    }
    
    public boolean isTorchCone() {
        return this.isTorchCone;
    }
    
    public void setTorchCone(final boolean isTorchCone) {
        this.isTorchCone = isTorchCone;
    }
    
    public float getTorchDot() {
        return this.getScriptItem().torchDot;
    }
    
    public int getLightDistance() {
        return this.lightDistance;
    }
    
    public void setLightDistance(final int lightDistance) {
        this.lightDistance = lightDistance;
    }
    
    public boolean canEmitLight() {
        if (this.getLightStrength() <= 0.0f) {
            return false;
        }
        final Drainable drainable = Type.tryCastTo(this, Drainable.class);
        return drainable == null || drainable.getUsedDelta() > 0.0f;
    }
    
    public boolean isEmittingLight() {
        return this.canEmitLight() && (!this.canBeActivated() || this.isActivated());
    }
    
    public boolean canStoreWater() {
        return this.CanStoreWater;
    }
    
    public float getFatigueChange() {
        return this.fatigueChange;
    }
    
    public void setFatigueChange(final float fatigueChange) {
        this.fatigueChange = fatigueChange;
    }
    
    public float getCurrentCondition() {
        return this.Condition / (float)this.ConditionMax * 100.0f;
    }
    
    public void setColor(final Color col) {
        this.col = col;
    }
    
    public Color getColor() {
        return this.col;
    }
    
    public ColorInfo getColorInfo() {
        return new ColorInfo(this.col.getRedFloat(), this.col.getGreenFloat(), this.col.getBlueFloat(), this.col.getAlphaFloat());
    }
    
    public boolean isTwoHandWeapon() {
        return this.getScriptItem().TwoHandWeapon;
    }
    
    public String getCustomMenuOption() {
        return this.customMenuOption;
    }
    
    public void setCustomMenuOption(final String customMenuOption) {
        this.customMenuOption = customMenuOption;
    }
    
    public void setTooltip(final String tooltip) {
        this.tooltip = tooltip;
    }
    
    public String getTooltip() {
        return this.tooltip;
    }
    
    public String getDisplayCategory() {
        return this.displayCategory;
    }
    
    public void setDisplayCategory(final String displayCategory) {
        this.displayCategory = displayCategory;
    }
    
    public int getHaveBeenRepaired() {
        return this.haveBeenRepaired;
    }
    
    public void setHaveBeenRepaired(final int haveBeenRepaired) {
        this.haveBeenRepaired = haveBeenRepaired;
    }
    
    public boolean isBroken() {
        return this.broken;
    }
    
    public void setBroken(final boolean broken) {
        this.broken = broken;
    }
    
    public String getDisplayName() {
        return this.name;
    }
    
    public boolean isTrap() {
        return this.getScriptItem().Trap;
    }
    
    public void addExtraItem(final String e) {
        if (this.extraItems == null) {
            this.extraItems = new ArrayList<String>();
        }
        this.extraItems.add(e);
    }
    
    public boolean haveExtraItems() {
        return this.extraItems != null;
    }
    
    public ArrayList<String> getExtraItems() {
        return this.extraItems;
    }
    
    public float getExtraItemsWeight() {
        if (!this.haveExtraItems()) {
            return 0.0f;
        }
        float n = 0.0f;
        for (int i = 0; i < this.extraItems.size(); ++i) {
            n += InventoryItemFactory.CreateItem(this.extraItems.get(i)).getActualWeight();
        }
        return n * 0.6f;
    }
    
    public boolean isCustomName() {
        return this.customName;
    }
    
    public void setCustomName(final boolean customName) {
        this.customName = customName;
    }
    
    public boolean isFishingLure() {
        return this.getScriptItem().FishingLure;
    }
    
    public void copyConditionModData(final InventoryItem inventoryItem) {
        if (inventoryItem.hasModData()) {
            final KahluaTableIterator iterator = inventoryItem.getModData().iterator();
            while (iterator.advance()) {
                if (iterator.getKey() instanceof String && ((String)iterator.getKey()).startsWith("condition:")) {
                    this.getModData().rawset(iterator.getKey(), iterator.getValue());
                }
            }
        }
    }
    
    public void setConditionFromModData(final InventoryItem inventoryItem) {
        if (inventoryItem.hasModData()) {
            final Object rawget = inventoryItem.getModData().rawget(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getType()));
            if (rawget != null && rawget instanceof Double) {
                this.setCondition((int)Math.round((double)rawget * this.getConditionMax()));
            }
        }
    }
    
    public String getBreakSound() {
        return this.breakSound;
    }
    
    public void setBreakSound(final String breakSound) {
        this.breakSound = breakSound;
    }
    
    public void setBeingFilled(final boolean beingFilled) {
        this.beingFilled = beingFilled;
    }
    
    public boolean isBeingFilled() {
        return this.beingFilled;
    }
    
    public boolean isAlcoholic() {
        return this.alcoholic;
    }
    
    public void setAlcoholic(final boolean alcoholic) {
        this.alcoholic = alcoholic;
    }
    
    public float getAlcoholPower() {
        return this.alcoholPower;
    }
    
    public void setAlcoholPower(final float alcoholPower) {
        this.alcoholPower = alcoholPower;
    }
    
    public float getBandagePower() {
        return this.bandagePower;
    }
    
    public void setBandagePower(final float bandagePower) {
        this.bandagePower = bandagePower;
    }
    
    public float getReduceInfectionPower() {
        return this.ReduceInfectionPower;
    }
    
    public void setReduceInfectionPower(final float reduceInfectionPower) {
        this.ReduceInfectionPower = reduceInfectionPower;
    }
    
    public final void saveWithSize(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        final int position = byteBuffer.position();
        byteBuffer.putInt(0);
        final int position2 = byteBuffer.position();
        this.save(byteBuffer, b);
        final int position3 = byteBuffer.position();
        byteBuffer.position(position);
        byteBuffer.putInt(position3 - position2);
        byteBuffer.position(position3);
    }
    
    public boolean isCustomWeight() {
        return this.customWeight;
    }
    
    public void setCustomWeight(final boolean customWeight) {
        this.customWeight = customWeight;
    }
    
    public float getContentsWeight() {
        if (!StringUtils.isNullOrEmpty(this.getAmmoType())) {
            final Item findItem = ScriptManager.instance.FindItem(this.getAmmoType());
            if (findItem != null) {
                return findItem.getActualWeight() * this.getCurrentAmmoCount();
            }
        }
        return 0.0f;
    }
    
    public float getHotbarEquippedWeight() {
        return (this.getActualWeight() + this.getContentsWeight()) * 0.7f;
    }
    
    public float getEquippedWeight() {
        return (this.getActualWeight() + this.getContentsWeight()) * 0.3f;
    }
    
    public float getUnequippedWeight() {
        return this.getActualWeight() + this.getContentsWeight();
    }
    
    public boolean isEquipped() {
        return this.getContainer() != null && this.getContainer().getParent() instanceof IsoGameCharacter && ((IsoGameCharacter)this.getContainer().getParent()).isEquipped(this);
    }
    
    public int getKeyId() {
        return this.keyId;
    }
    
    public void setKeyId(final int keyId) {
        this.keyId = keyId;
    }
    
    public boolean isTaintedWater() {
        return this.taintedWater;
    }
    
    public void setTaintedWater(final boolean taintedWater) {
        this.taintedWater = taintedWater;
    }
    
    public boolean isRemoteController() {
        return this.remoteController;
    }
    
    public void setRemoteController(final boolean remoteController) {
        this.remoteController = remoteController;
    }
    
    public boolean canBeRemote() {
        return this.canBeRemote;
    }
    
    public void setCanBeRemote(final boolean canBeRemote) {
        this.canBeRemote = canBeRemote;
    }
    
    public int getRemoteControlID() {
        return this.remoteControlID;
    }
    
    public void setRemoteControlID(final int remoteControlID) {
        this.remoteControlID = remoteControlID;
    }
    
    public int getRemoteRange() {
        return this.remoteRange;
    }
    
    public void setRemoteRange(final int remoteRange) {
        this.remoteRange = remoteRange;
    }
    
    public String getExplosionSound() {
        return this.explosionSound;
    }
    
    public void setExplosionSound(final String explosionSound) {
        this.explosionSound = explosionSound;
    }
    
    public String getCountDownSound() {
        return this.countDownSound;
    }
    
    public void setCountDownSound(final String countDownSound) {
        this.countDownSound = countDownSound;
    }
    
    public float getColorRed() {
        return this.colorRed;
    }
    
    public void setColorRed(final float colorRed) {
        this.colorRed = colorRed;
    }
    
    public float getColorGreen() {
        return this.colorGreen;
    }
    
    public void setColorGreen(final float colorGreen) {
        this.colorGreen = colorGreen;
    }
    
    public float getColorBlue() {
        return this.colorBlue;
    }
    
    public void setColorBlue(final float colorBlue) {
        this.colorBlue = colorBlue;
    }
    
    public String getEvolvedRecipeName() {
        return this.evolvedRecipeName;
    }
    
    public void setEvolvedRecipeName(final String evolvedRecipeName) {
        this.evolvedRecipeName = evolvedRecipeName;
    }
    
    public float getMetalValue() {
        return this.metalValue;
    }
    
    public void setMetalValue(final float metalValue) {
        this.metalValue = metalValue;
    }
    
    public float getItemHeat() {
        return this.itemHeat;
    }
    
    public void setItemHeat(float itemHeat) {
        if (itemHeat > 2.0f) {
            itemHeat = 2.0f;
        }
        if (itemHeat < 0.0f) {
            itemHeat = 0.0f;
        }
        this.itemHeat = itemHeat;
    }
    
    public float getInvHeat() {
        return 1.0f - this.itemHeat;
    }
    
    public float getMeltingTime() {
        return this.meltingTime;
    }
    
    public void setMeltingTime(float meltingTime) {
        if (meltingTime > 100.0f) {
            meltingTime = 100.0f;
        }
        if (meltingTime < 0.0f) {
            meltingTime = 0.0f;
        }
        this.meltingTime = meltingTime;
    }
    
    public String getWorker() {
        return this.worker;
    }
    
    public void setWorker(final String worker) {
        this.worker = worker;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public boolean isWet() {
        return this.isWet;
    }
    
    public void setWet(final boolean isWet) {
        this.isWet = isWet;
    }
    
    public float getWetCooldown() {
        return this.wetCooldown;
    }
    
    public void setWetCooldown(final float wetCooldown) {
        this.wetCooldown = wetCooldown;
    }
    
    public String getItemWhenDry() {
        return this.itemWhenDry;
    }
    
    public void setItemWhenDry(final String itemWhenDry) {
        this.itemWhenDry = itemWhenDry;
    }
    
    public boolean isFavorite() {
        return this.favorite;
    }
    
    public void setFavorite(final boolean favorite) {
        this.favorite = favorite;
    }
    
    public ArrayList<String> getRequireInHandOrInventory() {
        return this.requireInHandOrInventory;
    }
    
    public void setRequireInHandOrInventory(final ArrayList<String> requireInHandOrInventory) {
        this.requireInHandOrInventory = requireInHandOrInventory;
    }
    
    public boolean isCustomColor() {
        return this.customColor;
    }
    
    public void setCustomColor(final boolean customColor) {
        this.customColor = customColor;
    }
    
    public void doBuildingStash() {
        if (this.stashMap != null) {
            if (GameClient.bClient) {
                GameClient.sendBuildingStashToDo(this.stashMap);
            }
            else {
                StashSystem.prepareBuildingStash(this.stashMap);
            }
        }
    }
    
    public void setStashMap(final String stashMap) {
        this.stashMap = stashMap;
    }
    
    public int getMechanicType() {
        return this.getScriptItem().vehicleType;
    }
    
    public float getItemCapacity() {
        return this.itemCapacity;
    }
    
    public void setItemCapacity(final float itemCapacity) {
        this.itemCapacity = itemCapacity;
    }
    
    public int getMaxCapacity() {
        return this.maxCapacity;
    }
    
    public void setMaxCapacity(final int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    
    public boolean isConditionAffectsCapacity() {
        return this.ScriptItem != null && this.ScriptItem.isConditionAffectsCapacity();
    }
    
    public float getBrakeForce() {
        return this.brakeForce;
    }
    
    public void setBrakeForce(final float brakeForce) {
        this.brakeForce = brakeForce;
    }
    
    public int getChanceToSpawnDamaged() {
        return this.chanceToSpawnDamaged;
    }
    
    public void setChanceToSpawnDamaged(final int chanceToSpawnDamaged) {
        this.chanceToSpawnDamaged = chanceToSpawnDamaged;
    }
    
    public float getConditionLowerNormal() {
        return this.conditionLowerNormal;
    }
    
    public void setConditionLowerNormal(final float conditionLowerNormal) {
        this.conditionLowerNormal = conditionLowerNormal;
    }
    
    public float getConditionLowerOffroad() {
        return this.conditionLowerOffroad;
    }
    
    public void setConditionLowerOffroad(final float conditionLowerOffroad) {
        this.conditionLowerOffroad = conditionLowerOffroad;
    }
    
    public float getWheelFriction() {
        return this.wheelFriction;
    }
    
    public void setWheelFriction(final float wheelFriction) {
        this.wheelFriction = wheelFriction;
    }
    
    public float getSuspensionDamping() {
        return this.suspensionDamping;
    }
    
    public void setSuspensionDamping(final float suspensionDamping) {
        this.suspensionDamping = suspensionDamping;
    }
    
    public float getSuspensionCompression() {
        return this.suspensionCompression;
    }
    
    public void setSuspensionCompression(final float suspensionCompression) {
        this.suspensionCompression = suspensionCompression;
    }
    
    public void setInfected(final boolean zombieInfected) {
        this.zombieInfected = zombieInfected;
    }
    
    public boolean isInfected() {
        return this.zombieInfected;
    }
    
    public float getEngineLoudness() {
        return this.engineLoudness;
    }
    
    public void setEngineLoudness(final float engineLoudness) {
        this.engineLoudness = engineLoudness;
    }
    
    public String getStaticModel() {
        return this.getScriptItem().getStaticModel();
    }
    
    public ArrayList<String> getIconsForTexture() {
        return this.iconsForTexture;
    }
    
    public void setIconsForTexture(final ArrayList<String> iconsForTexture) {
        this.iconsForTexture = iconsForTexture;
    }
    
    public float getScore(final SurvivorDesc survivorDesc) {
        return 0.0f;
    }
    
    public IsoGameCharacter getPreviousOwner() {
        return this.previousOwner;
    }
    
    public void setPreviousOwner(final IsoGameCharacter previousOwner) {
        this.previousOwner = previousOwner;
    }
    
    public Item getScriptItem() {
        return this.ScriptItem;
    }
    
    public void setScriptItem(final Item scriptItem) {
        this.ScriptItem = scriptItem;
    }
    
    public ItemType getCat() {
        return this.cat;
    }
    
    public void setCat(final ItemType cat) {
        this.cat = cat;
    }
    
    public ItemContainer getContainer() {
        return this.container;
    }
    
    public void setContainer(final ItemContainer container) {
        this.container = container;
    }
    
    public ArrayList<BloodClothingType> getBloodClothingType() {
        return this.bloodClothingType;
    }
    
    public void setBloodClothingType(final ArrayList<BloodClothingType> bloodClothingType) {
        this.bloodClothingType = bloodClothingType;
    }
    
    public void setBlood(final BloodBodyPartType bloodBodyPartType, final float n) {
        final ItemVisual visual = this.getVisual();
        if (visual != null) {
            visual.setBlood(bloodBodyPartType, n);
        }
    }
    
    public float getBlood(final BloodBodyPartType bloodBodyPartType) {
        final ItemVisual visual = this.getVisual();
        if (visual != null) {
            return visual.getBlood(bloodBodyPartType);
        }
        return 0.0f;
    }
    
    public void setDirt(final BloodBodyPartType bloodBodyPartType, final float n) {
        final ItemVisual visual = this.getVisual();
        if (visual != null) {
            visual.setDirt(bloodBodyPartType, n);
        }
    }
    
    public float getDirt(final BloodBodyPartType bloodBodyPartType) {
        final ItemVisual visual = this.getVisual();
        if (visual != null) {
            return visual.getDirt(bloodBodyPartType);
        }
        return 0.0f;
    }
    
    public String getClothingItemName() {
        return this.getScriptItem().ClothingItem;
    }
    
    public int getStashChance() {
        return this.stashChance;
    }
    
    public void setStashChance(final int stashChance) {
        this.stashChance = stashChance;
    }
    
    public String getEatType() {
        return this.getScriptItem().eatType;
    }
    
    public boolean isUseWorldItem() {
        return this.getScriptItem().UseWorldItem;
    }
    
    public boolean isHairDye() {
        return this.getScriptItem().hairDye;
    }
    
    public String getAmmoType() {
        return this.ammoType;
    }
    
    public void setAmmoType(final String ammoType) {
        this.ammoType = ammoType;
    }
    
    public int getMaxAmmo() {
        return this.maxAmmo;
    }
    
    public void setMaxAmmo(final int maxAmmo) {
        this.maxAmmo = maxAmmo;
    }
    
    public int getCurrentAmmoCount() {
        return this.currentAmmoCount;
    }
    
    public void setCurrentAmmoCount(final int currentAmmoCount) {
        this.currentAmmoCount = currentAmmoCount;
    }
    
    public String getGunType() {
        return this.gunType;
    }
    
    public void setGunType(final String gunType) {
        this.gunType = gunType;
    }
    
    public boolean hasBlood() {
        if (this instanceof Clothing) {
            if (this.getBloodClothingType() == null || this.getBloodClothingType().isEmpty()) {
                return false;
            }
            final ArrayList<BloodBodyPartType> coveredParts = BloodClothingType.getCoveredParts(this.getBloodClothingType());
            if (coveredParts == null) {
                return false;
            }
            for (int i = 0; i < coveredParts.size(); ++i) {
                if (this.getBlood(coveredParts.get(i)) > 0.0f) {
                    return true;
                }
            }
        }
        else {
            if (this instanceof HandWeapon) {
                return ((HandWeapon)this).getBloodLevel() > 0.0f;
            }
            if (this instanceof InventoryContainer) {
                return ((InventoryContainer)this).getBloodLevel() > 0.0f;
            }
        }
        return false;
    }
    
    public boolean hasDirt() {
        if (this instanceof Clothing) {
            if (this.getBloodClothingType() == null || this.getBloodClothingType().isEmpty()) {
                return false;
            }
            final ArrayList<BloodBodyPartType> coveredParts = BloodClothingType.getCoveredParts(this.getBloodClothingType());
            if (coveredParts == null) {
                return false;
            }
            for (int i = 0; i < coveredParts.size(); ++i) {
                if (this.getDirt(coveredParts.get(i)) > 0.0f) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String getAttachmentType() {
        return this.attachmentType;
    }
    
    public void setAttachmentType(final String attachmentType) {
        this.attachmentType = attachmentType;
    }
    
    public int getAttachedSlot() {
        return this.attachedSlot;
    }
    
    public void setAttachedSlot(final int attachedSlot) {
        this.attachedSlot = attachedSlot;
    }
    
    public ArrayList<String> getAttachmentsProvided() {
        return this.attachmentsProvided;
    }
    
    public void setAttachmentsProvided(final ArrayList<String> attachmentsProvided) {
        this.attachmentsProvided = attachmentsProvided;
    }
    
    public String getAttachedSlotType() {
        return this.attachedSlotType;
    }
    
    public void setAttachedSlotType(final String attachedSlotType) {
        this.attachedSlotType = attachedSlotType;
    }
    
    public String getAttachmentReplacement() {
        return this.attachmentReplacement;
    }
    
    public void setAttachmentReplacement(final String attachmentReplacement) {
        this.attachmentReplacement = attachmentReplacement;
    }
    
    public String getAttachedToModel() {
        return this.attachedToModel;
    }
    
    public void setAttachedToModel(final String attachedToModel) {
        this.attachedToModel = attachedToModel;
    }
    
    public String getFabricType() {
        return this.getScriptItem().fabricType;
    }
    
    public String getStringItemType() {
        final Item findItem = ScriptManager.instance.FindItem(this.getFullType());
        if (findItem == null || findItem.getType() == null) {
            return "Other";
        }
        if (findItem.getType() == Item.Type.Food) {
            if (findItem.CannedFood) {
                return "CannedFood";
            }
            return "Food";
        }
        else {
            if ("Ammo".equals(findItem.getDisplayCategory())) {
                return "Ammo";
            }
            if (findItem.getType() == Item.Type.Weapon && !findItem.isRanged()) {
                return "MeleeWeapon";
            }
            if (findItem.getType() == Item.Type.WeaponPart || (findItem.getType() == Item.Type.Weapon && findItem.isRanged()) || (findItem.getType() == Item.Type.Normal && !StringUtils.isNullOrEmpty(findItem.getAmmoType()))) {
                return "RangedWeapon";
            }
            if (findItem.getType() == Item.Type.Literature) {
                return "Literature";
            }
            if (findItem.Medical) {
                return "Medical";
            }
            if (findItem.SurvivalGear) {
                return "SurvivalGear";
            }
            if (findItem.MechanicsItem) {
                return "Mechanic";
            }
            return "Other";
        }
    }
    
    public boolean isProtectFromRainWhileEquipped() {
        return this.getScriptItem().ProtectFromRainWhenEquipped;
    }
    
    public boolean isEquippedNoSprint() {
        return this.getScriptItem().equippedNoSprint;
    }
    
    public String getBodyLocation() {
        return this.getScriptItem().BodyLocation;
    }
    
    public String getMakeUpType() {
        return this.getScriptItem().makeUpType;
    }
    
    public boolean isHidden() {
        return this.getScriptItem().isHidden();
    }
    
    public String getConsolidateOption() {
        return this.getScriptItem().consolidateOption;
    }
    
    public ArrayList<String> getClothingItemExtra() {
        return this.getScriptItem().clothingItemExtra;
    }
    
    public ArrayList<String> getClothingItemExtraOption() {
        return this.getScriptItem().clothingItemExtraOption;
    }
    
    public String getWorldStaticItem() {
        return this.getScriptItem().worldStaticModel;
    }
    
    public void setRegistry_id(final Item item) {
        if (item.getFullName().equals(this.getFullType())) {
            this.registry_id = item.getRegistry_id();
        }
        else if (Core.bDebug) {
            WorldDictionary.DebugPrintItem(item);
            throw new RuntimeException("These types should always match");
        }
    }
    
    public short getRegistry_id() {
        return this.registry_id;
    }
    
    public String getModID() {
        if (this.ScriptItem != null && this.ScriptItem.getModID() != null) {
            return this.ScriptItem.getModID();
        }
        return WorldDictionary.getItemModID(this.registry_id);
    }
    
    public String getModName() {
        return WorldDictionary.getModNameFromID(this.getModID());
    }
    
    public boolean isVanilla() {
        if (this.getModID() != null) {
            return this.getModID().equals("pz-vanilla");
        }
        if (Core.bDebug) {
            WorldDictionary.DebugPrintItem(this);
            throw new RuntimeException("Item has no modID?");
        }
        return true;
    }
    
    public short getRecordedMediaIndex() {
        return this.recordedMediaIndex;
    }
    
    public void setRecordedMediaIndex(final short recordedMediaIndex) {
        this.recordedMediaIndex = recordedMediaIndex;
        if (this.recordedMediaIndex >= 0) {
            final MediaData mediaDataFromIndex = ZomboidRadio.getInstance().getRecordedMedia().getMediaDataFromIndex(this.recordedMediaIndex);
            this.mediaType = -1;
            if (mediaDataFromIndex != null) {
                this.name = mediaDataFromIndex.getTranslatedItemDisplayName();
                this.mediaType = mediaDataFromIndex.getMediaType();
            }
            else {
                this.recordedMediaIndex = -1;
            }
        }
    }
    
    public boolean isRecordedMedia() {
        return this.recordedMediaIndex >= 0;
    }
    
    public MediaData getMediaData() {
        if (this.isRecordedMedia()) {
            return ZomboidRadio.getInstance().getRecordedMedia().getMediaDataFromIndex(this.recordedMediaIndex);
        }
        return null;
    }
    
    public byte getMediaType() {
        return this.mediaType;
    }
    
    public void setMediaType(final byte mediaType) {
        this.mediaType = mediaType;
    }
    
    public void setRecordedMediaData(final MediaData mediaData) {
        if (mediaData != null && mediaData.getIndex() >= 0) {
            this.setRecordedMediaIndex(mediaData.getIndex());
        }
    }
    
    public void setWorldZRotation(final int worldZRotation) {
        this.worldZRotation = worldZRotation;
    }
    
    public void setWorldScale(final float worldScale) {
        this.worldScale = worldScale;
    }
    
    static {
        InventoryItem.tempBuffer = ByteBuffer.allocate(20000);
    }
}
