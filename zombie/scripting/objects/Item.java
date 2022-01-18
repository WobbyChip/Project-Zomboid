// 
// Decompiled by Procyon v0.5.36
// 

package zombie.scripting.objects;

import zombie.core.Core;
import zombie.network.GameClient;
import java.util.Iterator;
import java.security.InvalidParameterException;
import zombie.Lua.LuaManager;
import java.util.Arrays;
import zombie.radio.media.MediaData;
import zombie.radio.devices.DeviceData;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.radio.ZomboidRadio;
import zombie.core.Translator;
import zombie.core.Color;
import zombie.worldMap.MapDefinitions;
import zombie.inventory.types.MapItem;
import zombie.inventory.types.Moveable;
import zombie.debug.DebugLog;
import zombie.inventory.types.Radio;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.ComboItem;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.AlarmClockClothing;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.Literature;
import zombie.inventory.types.Food;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.WeaponPart;
import zombie.inventory.types.KeyRing;
import zombie.core.Rand;
import zombie.inventory.types.Key;
import zombie.inventory.InventoryItem;
import zombie.world.WorldDictionary;
import zombie.util.StringUtils;
import zombie.scripting.ScriptManager;
import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;
import zombie.characterTextures.BloodClothingType;
import zombie.core.textures.Texture;
import zombie.core.skinnedmodel.population.ClothingItem;
import java.util.List;
import se.krka.kahlua.vm.KahluaTable;
import java.util.ArrayList;

public final class Item extends BaseScriptObject
{
    public String clothingExtraSubmenu;
    public String DisplayName;
    public boolean Hidden;
    public boolean CantEat;
    public String Icon;
    public boolean Medical;
    public boolean CannedFood;
    public boolean SurvivalGear;
    public boolean MechanicsItem;
    public boolean UseWorldItem;
    public float ScaleWorldIcon;
    public String CloseKillMove;
    public float WeaponLength;
    public float ActualWeight;
    public float WeightWet;
    public float WeightEmpty;
    public float HungerChange;
    public float CriticalChance;
    public int Count;
    public int DaysFresh;
    public int DaysTotallyRotten;
    public int MinutesToCook;
    public int MinutesToBurn;
    public boolean IsCookable;
    private String CookingSound;
    public float StressChange;
    public float BoredomChange;
    public float UnhappyChange;
    public boolean AlwaysWelcomeGift;
    public String ReplaceOnDeplete;
    public boolean Ranged;
    public boolean CanStoreWater;
    public float MaxRange;
    public float MinRange;
    public float ThirstChange;
    public float FatigueChange;
    public float MinAngle;
    public boolean RequiresEquippedBothHands;
    public float MaxDamage;
    public float MinDamage;
    public float MinimumSwingTime;
    public String SwingSound;
    public String WeaponSprite;
    public boolean AngleFalloff;
    public int SoundVolume;
    public float ToHitModifier;
    public int SoundRadius;
    public float OtherCharacterVolumeBoost;
    public final ArrayList<String> Categories;
    public final ArrayList<String> Tags;
    public String ImpactSound;
    public float SwingTime;
    public boolean KnockBackOnNoDeath;
    public boolean SplatBloodOnNoDeath;
    public float SwingAmountBeforeImpact;
    public String AmmoType;
    public int maxAmmo;
    public String GunType;
    public int DoorDamage;
    public int ConditionLowerChance;
    public int ConditionMax;
    public boolean CanBandage;
    public String name;
    public String moduleDotType;
    public int MaxHitCount;
    public boolean UseSelf;
    public boolean OtherHandUse;
    public String OtherHandRequire;
    public String PhysicsObject;
    public String SwingAnim;
    public float WeaponWeight;
    public float EnduranceChange;
    public String IdleAnim;
    public String RunAnim;
    public String attachmentType;
    public String makeUpType;
    public String consolidateOption;
    public ArrayList<String> RequireInHandOrInventory;
    public String DoorHitSound;
    public String ReplaceOnUse;
    public boolean DangerousUncooked;
    public boolean Alcoholic;
    public float PushBackMod;
    public int SplatNumber;
    public float NPCSoundBoost;
    public boolean RangeFalloff;
    public boolean UseEndurance;
    public boolean MultipleHitConditionAffected;
    public boolean ShareDamage;
    public boolean ShareEndurance;
    public boolean CanBarricade;
    public boolean UseWhileEquipped;
    public boolean UseWhileUnequipped;
    public int TicksPerEquipUse;
    public boolean DisappearOnUse;
    public float UseDelta;
    public boolean AlwaysKnockdown;
    public float EnduranceMod;
    public float KnockdownMod;
    public boolean CantAttackWithLowestEndurance;
    public String ReplaceOnUseOn;
    public boolean IsWaterSource;
    public ArrayList<String> attachmentsProvided;
    public String FoodType;
    public boolean Poison;
    public Integer PoisonDetectionLevel;
    public int PoisonPower;
    public KahluaTable DefaultModData;
    public boolean IsAimedFirearm;
    public boolean IsAimedHandWeapon;
    public boolean CanStack;
    public float AimingMod;
    public int ProjectileCount;
    public float HitAngleMod;
    public float SplatSize;
    public float Temperature;
    public int NumberOfPages;
    public int LvlSkillTrained;
    public int NumLevelsTrained;
    public String SkillTrained;
    public int Capacity;
    public int WeightReduction;
    public String SubCategory;
    public boolean ActivatedItem;
    public float LightStrength;
    public boolean TorchCone;
    public int LightDistance;
    public String CanBeEquipped;
    public boolean TwoHandWeapon;
    public String CustomContextMenu;
    public String Tooltip;
    public List<String> ReplaceOnCooked;
    public String DisplayCategory;
    public Boolean Trap;
    public boolean OBSOLETE;
    public boolean FishingLure;
    public boolean canBeWrite;
    public int AimingPerkCritModifier;
    public float AimingPerkRangeModifier;
    public float AimingPerkHitChanceModifier;
    public int HitChance;
    public float AimingPerkMinAngleModifier;
    public int RecoilDelay;
    public boolean PiercingBullets;
    public float SoundGain;
    public boolean ProtectFromRainWhenEquipped;
    private float maxRangeModifier;
    private float minRangeRangedModifier;
    private float damageModifier;
    private float recoilDelayModifier;
    private int clipSizeModifier;
    private ArrayList<String> mountOn;
    private String partType;
    private int ClipSize;
    private int reloadTime;
    private int reloadTimeModifier;
    private int aimingTime;
    private int aimingTimeModifier;
    private int hitChanceModifier;
    private float angleModifier;
    private float weightModifier;
    private int PageToWrite;
    private boolean RemoveNegativeEffectOnCooked;
    private int treeDamage;
    private float alcoholPower;
    private String PutInSound;
    private String OpenSound;
    private String CloseSound;
    private String breakSound;
    private String customEatSound;
    private String bulletOutSound;
    private String ShellFallSound;
    private float bandagePower;
    private float ReduceInfectionPower;
    private String OnCooked;
    private String OnlyAcceptCategory;
    private String AcceptItemFunction;
    private boolean padlock;
    private boolean digitalPadlock;
    private List<String> teachedRecipes;
    private int triggerExplosionTimer;
    private boolean canBePlaced;
    private int explosionRange;
    private int explosionPower;
    private int fireRange;
    private int firePower;
    private int smokeRange;
    private int noiseRange;
    private int noiseDuration;
    private float extraDamage;
    private int explosionTimer;
    private String PlacedSprite;
    private boolean canBeReused;
    private int sensorRange;
    private boolean canBeRemote;
    private boolean remoteController;
    private int remoteRange;
    private String countDownSound;
    private String explosionSound;
    private int fluReduction;
    private int ReduceFoodSickness;
    private int painReduction;
    private float rainFactor;
    public float torchDot;
    public int colorRed;
    public int colorGreen;
    public int colorBlue;
    public boolean twoWay;
    public int transmitRange;
    public int micRange;
    public float baseVolumeRange;
    public boolean isPortable;
    public boolean isTelevision;
    public int minChannel;
    public int maxChannel;
    public boolean usesBattery;
    public boolean isHighTier;
    public String HerbalistType;
    private float carbohydrates;
    private float lipids;
    private float proteins;
    private float calories;
    private boolean packaged;
    private boolean cantBeFrozen;
    private String evolvedRecipeName;
    private String ReplaceOnRotten;
    private float metalValue;
    private String AlarmSound;
    private String itemWhenDry;
    private float wetCooldown;
    private boolean isWet;
    private String onEat;
    private boolean cantBeConsolided;
    private boolean BadInMicrowave;
    private boolean GoodHot;
    private boolean BadCold;
    public String map;
    private boolean keepOnDeplete;
    public int vehicleType;
    private int maxCapacity;
    private int itemCapacity;
    private boolean ConditionAffectsCapacity;
    private float brakeForce;
    private int chanceToSpawnDamaged;
    private float conditionLowerNormal;
    private float conditionLowerOffroad;
    private float wheelFriction;
    private float suspensionDamping;
    private float suspensionCompression;
    private float engineLoudness;
    public String ClothingItem;
    private ClothingItem clothingItemAsset;
    private String staticModel;
    public String primaryAnimMask;
    public String secondaryAnimMask;
    public String primaryAnimMaskAttachment;
    public String secondaryAnimMaskAttachment;
    public String replaceInSecondHand;
    public String replaceInPrimaryHand;
    public String replaceWhenUnequip;
    public ItemReplacement replacePrimaryHand;
    public ItemReplacement replaceSecondHand;
    public String worldObjectSprite;
    public String ItemName;
    public Texture NormalTexture;
    public List<Texture> SpecialTextures;
    public List<String> SpecialWorldTextureNames;
    public String WorldTextureName;
    public Texture WorldTexture;
    public String eatType;
    private ArrayList<String> IconsForTexture;
    private float baseSpeed;
    private ArrayList<BloodClothingType> bloodClothingType;
    private float stompPower;
    public float runSpeedModifier;
    public float combatSpeedModifier;
    public ArrayList<String> clothingItemExtra;
    public ArrayList<String> clothingItemExtraOption;
    private Boolean removeOnBroken;
    public Boolean canHaveHoles;
    private boolean cosmetic;
    private String ammoBox;
    public boolean hairDye;
    private String insertAmmoStartSound;
    private String insertAmmoSound;
    private String insertAmmoStopSound;
    private String ejectAmmoStartSound;
    private String ejectAmmoSound;
    private String ejectAmmoStopSound;
    private String rackSound;
    private String clickSound;
    private String equipSound;
    private String unequipSound;
    private String bringToBearSound;
    private String magazineType;
    private String weaponReloadType;
    private boolean rackAfterShoot;
    private float jamGunChance;
    private ArrayList<ModelWeaponPart> modelWeaponPart;
    private boolean haveChamber;
    private boolean manuallyRemoveSpentRounds;
    private float biteDefense;
    private float scratchDefense;
    private float bulletDefense;
    private String damageCategory;
    private boolean damageMakeHole;
    public float neckProtectionModifier;
    private String attachmentReplacement;
    private boolean insertAllBulletsReload;
    private int chanceToFall;
    public String fabricType;
    public boolean equippedNoSprint;
    public String worldStaticModel;
    private float critDmgMultiplier;
    private float insulation;
    private float windresist;
    private float waterresist;
    private String fireMode;
    private ArrayList<String> fireModePossibilities;
    public boolean RemoveUnhappinessWhenCooked;
    private short registry_id;
    private boolean existsAsVanilla;
    private String modID;
    private String fileAbsPath;
    public float stopPower;
    private String recordedMediaCat;
    private byte acceptMediaType;
    private boolean noTransmit;
    private boolean worldRender;
    public String HitSound;
    public String hitFloorSound;
    public String BodyLocation;
    public Stack<String> PaletteChoices;
    public String SpriteName;
    public String PalettesStart;
    public static HashMap<Integer, String> NetIDToItem;
    public static HashMap<String, Integer> NetItemToID;
    static int IDMax;
    public Type type;
    private boolean Spice;
    private int UseForPoison;
    
    public Item() {
        this.clothingExtraSubmenu = null;
        this.DisplayName = null;
        this.Hidden = false;
        this.CantEat = false;
        this.Icon = "None";
        this.Medical = false;
        this.CannedFood = false;
        this.SurvivalGear = false;
        this.MechanicsItem = false;
        this.UseWorldItem = false;
        this.ScaleWorldIcon = 1.0f;
        this.CloseKillMove = null;
        this.WeaponLength = 0.4f;
        this.ActualWeight = 1.0f;
        this.WeightWet = 0.0f;
        this.WeightEmpty = 0.0f;
        this.HungerChange = 0.0f;
        this.CriticalChance = 20.0f;
        this.Count = 1;
        this.DaysFresh = 1000000000;
        this.DaysTotallyRotten = 1000000000;
        this.MinutesToCook = 60;
        this.MinutesToBurn = 120;
        this.IsCookable = false;
        this.CookingSound = null;
        this.StressChange = 0.0f;
        this.BoredomChange = 0.0f;
        this.UnhappyChange = 0.0f;
        this.AlwaysWelcomeGift = false;
        this.ReplaceOnDeplete = null;
        this.Ranged = false;
        this.CanStoreWater = false;
        this.MaxRange = 1.0f;
        this.MinRange = 0.0f;
        this.ThirstChange = 0.0f;
        this.FatigueChange = 0.0f;
        this.MinAngle = 1.0f;
        this.RequiresEquippedBothHands = false;
        this.MaxDamage = 1.5f;
        this.MinDamage = 0.0f;
        this.MinimumSwingTime = 0.0f;
        this.SwingSound = "BaseballBatSwing";
        this.AngleFalloff = false;
        this.SoundVolume = 0;
        this.ToHitModifier = 1.0f;
        this.SoundRadius = 0;
        this.Categories = new ArrayList<String>();
        this.Tags = new ArrayList<String>();
        this.ImpactSound = "BaseballBatHit";
        this.SwingTime = 1.0f;
        this.KnockBackOnNoDeath = true;
        this.SplatBloodOnNoDeath = false;
        this.SwingAmountBeforeImpact = 0.0f;
        this.AmmoType = null;
        this.maxAmmo = 0;
        this.GunType = null;
        this.DoorDamage = 1;
        this.ConditionLowerChance = 1000000;
        this.ConditionMax = 10;
        this.CanBandage = false;
        this.MaxHitCount = 1000;
        this.UseSelf = false;
        this.OtherHandUse = false;
        this.SwingAnim = "Rifle";
        this.WeaponWeight = 1.0f;
        this.EnduranceChange = 0.0f;
        this.IdleAnim = "Idle";
        this.RunAnim = "Run";
        this.attachmentType = null;
        this.makeUpType = null;
        this.consolidateOption = null;
        this.RequireInHandOrInventory = null;
        this.DoorHitSound = "BaseballBatHit";
        this.ReplaceOnUse = null;
        this.DangerousUncooked = false;
        this.Alcoholic = false;
        this.PushBackMod = 1.0f;
        this.SplatNumber = 2;
        this.NPCSoundBoost = 1.0f;
        this.RangeFalloff = false;
        this.UseEndurance = true;
        this.MultipleHitConditionAffected = true;
        this.ShareDamage = true;
        this.ShareEndurance = false;
        this.CanBarricade = false;
        this.UseWhileEquipped = true;
        this.UseWhileUnequipped = false;
        this.TicksPerEquipUse = 30;
        this.DisappearOnUse = true;
        this.UseDelta = 0.03125f;
        this.AlwaysKnockdown = false;
        this.EnduranceMod = 1.0f;
        this.KnockdownMod = 1.0f;
        this.CantAttackWithLowestEndurance = false;
        this.ReplaceOnUseOn = null;
        this.IsWaterSource = false;
        this.attachmentsProvided = null;
        this.FoodType = null;
        this.Poison = false;
        this.PoisonDetectionLevel = null;
        this.PoisonPower = 0;
        this.DefaultModData = null;
        this.IsAimedFirearm = false;
        this.IsAimedHandWeapon = false;
        this.CanStack = true;
        this.AimingMod = 1.0f;
        this.ProjectileCount = 1;
        this.HitAngleMod = 0.0f;
        this.SplatSize = 1.0f;
        this.Temperature = 0.0f;
        this.NumberOfPages = -1;
        this.LvlSkillTrained = -1;
        this.NumLevelsTrained = 1;
        this.SkillTrained = "";
        this.Capacity = 0;
        this.WeightReduction = 0;
        this.SubCategory = "";
        this.ActivatedItem = false;
        this.LightStrength = 0.0f;
        this.TorchCone = false;
        this.LightDistance = 0;
        this.CanBeEquipped = "";
        this.TwoHandWeapon = false;
        this.CustomContextMenu = null;
        this.Tooltip = null;
        this.ReplaceOnCooked = null;
        this.DisplayCategory = null;
        this.Trap = false;
        this.OBSOLETE = false;
        this.FishingLure = false;
        this.canBeWrite = false;
        this.AimingPerkCritModifier = 0;
        this.AimingPerkRangeModifier = 0.0f;
        this.AimingPerkHitChanceModifier = 0.0f;
        this.HitChance = 0;
        this.AimingPerkMinAngleModifier = 0.0f;
        this.RecoilDelay = 0;
        this.PiercingBullets = false;
        this.SoundGain = 1.0f;
        this.ProtectFromRainWhenEquipped = false;
        this.maxRangeModifier = 0.0f;
        this.minRangeRangedModifier = 0.0f;
        this.damageModifier = 0.0f;
        this.recoilDelayModifier = 0.0f;
        this.clipSizeModifier = 0;
        this.mountOn = null;
        this.partType = null;
        this.ClipSize = 0;
        this.reloadTime = 0;
        this.reloadTimeModifier = 0;
        this.aimingTime = 0;
        this.aimingTimeModifier = 0;
        this.hitChanceModifier = 0;
        this.angleModifier = 0.0f;
        this.weightModifier = 0.0f;
        this.PageToWrite = 0;
        this.RemoveNegativeEffectOnCooked = false;
        this.treeDamage = 0;
        this.alcoholPower = 0.0f;
        this.PutInSound = null;
        this.OpenSound = null;
        this.CloseSound = null;
        this.breakSound = null;
        this.customEatSound = null;
        this.bulletOutSound = null;
        this.ShellFallSound = null;
        this.bandagePower = 0.0f;
        this.ReduceInfectionPower = 0.0f;
        this.OnCooked = null;
        this.OnlyAcceptCategory = null;
        this.AcceptItemFunction = null;
        this.padlock = false;
        this.digitalPadlock = false;
        this.teachedRecipes = null;
        this.triggerExplosionTimer = 0;
        this.canBePlaced = false;
        this.explosionRange = 0;
        this.explosionPower = 0;
        this.fireRange = 0;
        this.firePower = 0;
        this.smokeRange = 0;
        this.noiseRange = 0;
        this.noiseDuration = 0;
        this.extraDamage = 0.0f;
        this.explosionTimer = 0;
        this.PlacedSprite = null;
        this.canBeReused = false;
        this.sensorRange = 0;
        this.canBeRemote = false;
        this.remoteController = false;
        this.remoteRange = 0;
        this.countDownSound = null;
        this.explosionSound = null;
        this.fluReduction = 0;
        this.ReduceFoodSickness = 0;
        this.painReduction = 0;
        this.rainFactor = 0.0f;
        this.torchDot = 0.96f;
        this.colorRed = 255;
        this.colorGreen = 255;
        this.colorBlue = 255;
        this.twoWay = false;
        this.transmitRange = 0;
        this.micRange = 0;
        this.baseVolumeRange = 0.0f;
        this.isPortable = false;
        this.isTelevision = false;
        this.minChannel = 88000;
        this.maxChannel = 108000;
        this.usesBattery = false;
        this.isHighTier = false;
        this.carbohydrates = 0.0f;
        this.lipids = 0.0f;
        this.proteins = 0.0f;
        this.calories = 0.0f;
        this.packaged = false;
        this.cantBeFrozen = false;
        this.evolvedRecipeName = null;
        this.ReplaceOnRotten = null;
        this.metalValue = 0.0f;
        this.AlarmSound = null;
        this.itemWhenDry = null;
        this.wetCooldown = 0.0f;
        this.isWet = false;
        this.onEat = null;
        this.cantBeConsolided = false;
        this.BadInMicrowave = false;
        this.GoodHot = false;
        this.BadCold = false;
        this.map = null;
        this.keepOnDeplete = false;
        this.vehicleType = 0;
        this.maxCapacity = -1;
        this.itemCapacity = -1;
        this.ConditionAffectsCapacity = false;
        this.brakeForce = 0.0f;
        this.chanceToSpawnDamaged = 0;
        this.conditionLowerNormal = 0.0f;
        this.conditionLowerOffroad = 0.0f;
        this.wheelFriction = 0.0f;
        this.suspensionDamping = 0.0f;
        this.suspensionCompression = 0.0f;
        this.engineLoudness = 0.0f;
        this.ClothingItem = null;
        this.clothingItemAsset = null;
        this.staticModel = null;
        this.primaryAnimMask = null;
        this.secondaryAnimMask = null;
        this.primaryAnimMaskAttachment = null;
        this.secondaryAnimMaskAttachment = null;
        this.replaceInSecondHand = null;
        this.replaceInPrimaryHand = null;
        this.replaceWhenUnequip = null;
        this.replacePrimaryHand = null;
        this.replaceSecondHand = null;
        this.worldObjectSprite = null;
        this.SpecialTextures = new ArrayList<Texture>();
        this.SpecialWorldTextureNames = new ArrayList<String>();
        this.baseSpeed = 1.0f;
        this.stompPower = 1.0f;
        this.runSpeedModifier = 1.0f;
        this.combatSpeedModifier = 1.0f;
        this.removeOnBroken = false;
        this.canHaveHoles = true;
        this.cosmetic = false;
        this.ammoBox = null;
        this.hairDye = false;
        this.insertAmmoStartSound = null;
        this.insertAmmoSound = null;
        this.insertAmmoStopSound = null;
        this.ejectAmmoStartSound = null;
        this.ejectAmmoSound = null;
        this.ejectAmmoStopSound = null;
        this.rackSound = null;
        this.clickSound = "Stormy9mmClick";
        this.equipSound = null;
        this.unequipSound = null;
        this.bringToBearSound = null;
        this.magazineType = null;
        this.weaponReloadType = null;
        this.rackAfterShoot = false;
        this.jamGunChance = 1.0f;
        this.modelWeaponPart = null;
        this.haveChamber = true;
        this.manuallyRemoveSpentRounds = false;
        this.biteDefense = 0.0f;
        this.scratchDefense = 0.0f;
        this.bulletDefense = 0.0f;
        this.damageCategory = null;
        this.damageMakeHole = false;
        this.neckProtectionModifier = 1.0f;
        this.attachmentReplacement = null;
        this.insertAllBulletsReload = false;
        this.chanceToFall = 0;
        this.fabricType = null;
        this.equippedNoSprint = false;
        this.worldStaticModel = null;
        this.critDmgMultiplier = 0.0f;
        this.insulation = 0.0f;
        this.windresist = 0.0f;
        this.waterresist = 0.0f;
        this.fireMode = null;
        this.fireModePossibilities = null;
        this.RemoveUnhappinessWhenCooked = false;
        this.registry_id = -1;
        this.existsAsVanilla = false;
        this.stopPower = 5.0f;
        this.acceptMediaType = -1;
        this.noTransmit = false;
        this.worldRender = true;
        this.HitSound = "BaseballBatHit";
        this.hitFloorSound = "BatOnFloor";
        this.BodyLocation = "";
        this.PaletteChoices = new Stack<String>();
        this.SpriteName = null;
        this.PalettesStart = "";
        this.type = Type.Normal;
        this.Spice = false;
    }
    
    public String getDisplayName() {
        return this.DisplayName;
    }
    
    public void setDisplayName(final String displayName) {
        this.DisplayName = displayName;
    }
    
    public boolean isHidden() {
        return this.Hidden;
    }
    
    public String getDisplayCategory() {
        return this.DisplayCategory;
    }
    
    public String getIcon() {
        return this.Icon;
    }
    
    public void setIcon(final String icon) {
        this.Icon = icon;
    }
    
    public int getNoiseDuration() {
        return this.noiseDuration;
    }
    
    public Texture getNormalTexture() {
        return this.NormalTexture;
    }
    
    public int getNumberOfPages() {
        return this.NumberOfPages;
    }
    
    public float getActualWeight() {
        return this.ActualWeight;
    }
    
    public void setActualWeight(final float actualWeight) {
        this.ActualWeight = actualWeight;
    }
    
    public float getWeightWet() {
        return this.WeightWet;
    }
    
    public void setWeightWet(final float weightWet) {
        this.WeightWet = weightWet;
    }
    
    public float getWeightEmpty() {
        return this.WeightEmpty;
    }
    
    public void setWeightEmpty(final float weightEmpty) {
        this.WeightEmpty = weightEmpty;
    }
    
    public float getHungerChange() {
        return this.HungerChange;
    }
    
    public void setHungerChange(final float hungerChange) {
        this.HungerChange = hungerChange;
    }
    
    public float getThirstChange() {
        return this.ThirstChange;
    }
    
    public void setThirstChange(final float thirstChange) {
        this.ThirstChange = thirstChange;
    }
    
    public int getCount() {
        return this.Count;
    }
    
    public void setCount(final int count) {
        this.Count = count;
    }
    
    public int getDaysFresh() {
        return this.DaysFresh;
    }
    
    public void setDaysFresh(final int daysFresh) {
        this.DaysFresh = daysFresh;
    }
    
    public int getDaysTotallyRotten() {
        return this.DaysTotallyRotten;
    }
    
    public void setDaysTotallyRotten(final int daysTotallyRotten) {
        this.DaysTotallyRotten = daysTotallyRotten;
    }
    
    public int getMinutesToCook() {
        return this.MinutesToCook;
    }
    
    public void setMinutesToCook(final int minutesToCook) {
        this.MinutesToCook = minutesToCook;
    }
    
    public int getMinutesToBurn() {
        return this.MinutesToBurn;
    }
    
    public void setMinutesToBurn(final int minutesToBurn) {
        this.MinutesToBurn = minutesToBurn;
    }
    
    public boolean isIsCookable() {
        return this.IsCookable;
    }
    
    public void setIsCookable(final boolean isCookable) {
        this.IsCookable = isCookable;
    }
    
    public String getCookingSound() {
        return this.CookingSound;
    }
    
    public float getStressChange() {
        return this.StressChange;
    }
    
    public void setStressChange(final float stressChange) {
        this.StressChange = stressChange;
    }
    
    public float getBoredomChange() {
        return this.BoredomChange;
    }
    
    public void setBoredomChange(final float boredomChange) {
        this.BoredomChange = boredomChange;
    }
    
    public float getUnhappyChange() {
        return this.UnhappyChange;
    }
    
    public void setUnhappyChange(final float unhappyChange) {
        this.UnhappyChange = unhappyChange;
    }
    
    public boolean isAlwaysWelcomeGift() {
        return this.AlwaysWelcomeGift;
    }
    
    public void setAlwaysWelcomeGift(final boolean alwaysWelcomeGift) {
        this.AlwaysWelcomeGift = alwaysWelcomeGift;
    }
    
    public boolean isRanged() {
        return this.Ranged;
    }
    
    public boolean getCanStoreWater() {
        return this.CanStoreWater;
    }
    
    public void setRanged(final boolean ranged) {
        this.Ranged = ranged;
    }
    
    public float getMaxRange() {
        return this.MaxRange;
    }
    
    public void setMaxRange(final float maxRange) {
        this.MaxRange = maxRange;
    }
    
    public float getMinAngle() {
        return this.MinAngle;
    }
    
    public void setMinAngle(final float minAngle) {
        this.MinAngle = minAngle;
    }
    
    public float getMaxDamage() {
        return this.MaxDamage;
    }
    
    public void setMaxDamage(final float maxDamage) {
        this.MaxDamage = maxDamage;
    }
    
    public float getMinDamage() {
        return this.MinDamage;
    }
    
    public void setMinDamage(final float minDamage) {
        this.MinDamage = minDamage;
    }
    
    public float getMinimumSwingTime() {
        return this.MinimumSwingTime;
    }
    
    public void setMinimumSwingTime(final float minimumSwingTime) {
        this.MinimumSwingTime = minimumSwingTime;
    }
    
    public String getSwingSound() {
        return this.SwingSound;
    }
    
    public void setSwingSound(final String swingSound) {
        this.SwingSound = swingSound;
    }
    
    public String getWeaponSprite() {
        return this.WeaponSprite;
    }
    
    public void setWeaponSprite(final String weaponSprite) {
        this.WeaponSprite = weaponSprite;
    }
    
    public boolean isAngleFalloff() {
        return this.AngleFalloff;
    }
    
    public void setAngleFalloff(final boolean angleFalloff) {
        this.AngleFalloff = angleFalloff;
    }
    
    public int getSoundVolume() {
        return this.SoundVolume;
    }
    
    public void setSoundVolume(final int soundVolume) {
        this.SoundVolume = soundVolume;
    }
    
    public float getToHitModifier() {
        return this.ToHitModifier;
    }
    
    public void setToHitModifier(final float toHitModifier) {
        this.ToHitModifier = toHitModifier;
    }
    
    public int getSoundRadius() {
        return this.SoundRadius;
    }
    
    public void setSoundRadius(final int soundRadius) {
        this.SoundRadius = soundRadius;
    }
    
    public float getOtherCharacterVolumeBoost() {
        return this.OtherCharacterVolumeBoost;
    }
    
    public void setOtherCharacterVolumeBoost(final float otherCharacterVolumeBoost) {
        this.OtherCharacterVolumeBoost = otherCharacterVolumeBoost;
    }
    
    public ArrayList<String> getCategories() {
        return this.Categories;
    }
    
    public void setCategories(final ArrayList<String> c) {
        this.Categories.clear();
        this.Categories.addAll(c);
    }
    
    public ArrayList<String> getTags() {
        return this.Tags;
    }
    
    public String getImpactSound() {
        return this.ImpactSound;
    }
    
    public void setImpactSound(final String impactSound) {
        this.ImpactSound = impactSound;
    }
    
    public float getSwingTime() {
        return this.SwingTime;
    }
    
    public void setSwingTime(final float swingTime) {
        this.SwingTime = swingTime;
    }
    
    public boolean isKnockBackOnNoDeath() {
        return this.KnockBackOnNoDeath;
    }
    
    public void setKnockBackOnNoDeath(final boolean knockBackOnNoDeath) {
        this.KnockBackOnNoDeath = knockBackOnNoDeath;
    }
    
    public boolean isSplatBloodOnNoDeath() {
        return this.SplatBloodOnNoDeath;
    }
    
    public void setSplatBloodOnNoDeath(final boolean splatBloodOnNoDeath) {
        this.SplatBloodOnNoDeath = splatBloodOnNoDeath;
    }
    
    public float getSwingAmountBeforeImpact() {
        return this.SwingAmountBeforeImpact;
    }
    
    public void setSwingAmountBeforeImpact(final float swingAmountBeforeImpact) {
        this.SwingAmountBeforeImpact = swingAmountBeforeImpact;
    }
    
    public String getAmmoType() {
        return this.AmmoType;
    }
    
    public void setAmmoType(final String ammoType) {
        this.AmmoType = ammoType;
    }
    
    public int getDoorDamage() {
        return this.DoorDamage;
    }
    
    public void setDoorDamage(final int doorDamage) {
        this.DoorDamage = doorDamage;
    }
    
    public int getConditionLowerChance() {
        return this.ConditionLowerChance;
    }
    
    public void setConditionLowerChance(final int conditionLowerChance) {
        this.ConditionLowerChance = conditionLowerChance;
    }
    
    public int getConditionMax() {
        return this.ConditionMax;
    }
    
    public void setConditionMax(final int conditionMax) {
        this.ConditionMax = conditionMax;
    }
    
    public boolean isCanBandage() {
        return this.CanBandage;
    }
    
    public void setCanBandage(final boolean canBandage) {
        this.CanBandage = canBandage;
    }
    
    public boolean isCosmetic() {
        return this.cosmetic;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getModuleName() {
        return this.module.name;
    }
    
    public String getFullName() {
        return this.moduleDotType;
    }
    
    public void setName(final String name) {
        this.name = name;
        this.moduleDotType = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.module.name, name);
    }
    
    public int getMaxHitCount() {
        return this.MaxHitCount;
    }
    
    public void setMaxHitCount(final int maxHitCount) {
        this.MaxHitCount = maxHitCount;
    }
    
    public boolean isUseSelf() {
        return this.UseSelf;
    }
    
    public void setUseSelf(final boolean useSelf) {
        this.UseSelf = useSelf;
    }
    
    public boolean isOtherHandUse() {
        return this.OtherHandUse;
    }
    
    public void setOtherHandUse(final boolean otherHandUse) {
        this.OtherHandUse = otherHandUse;
    }
    
    public String getOtherHandRequire() {
        return this.OtherHandRequire;
    }
    
    public void setOtherHandRequire(final String otherHandRequire) {
        this.OtherHandRequire = otherHandRequire;
    }
    
    public String getPhysicsObject() {
        return this.PhysicsObject;
    }
    
    public void setPhysicsObject(final String physicsObject) {
        this.PhysicsObject = physicsObject;
    }
    
    public String getSwingAnim() {
        return this.SwingAnim;
    }
    
    public void setSwingAnim(final String swingAnim) {
        this.SwingAnim = swingAnim;
    }
    
    public float getWeaponWeight() {
        return this.WeaponWeight;
    }
    
    public void setWeaponWeight(final float weaponWeight) {
        this.WeaponWeight = weaponWeight;
    }
    
    public float getEnduranceChange() {
        return this.EnduranceChange;
    }
    
    public void setEnduranceChange(final float enduranceChange) {
        this.EnduranceChange = enduranceChange;
    }
    
    public String getBreakSound() {
        return this.breakSound;
    }
    
    public String getBulletOutSound() {
        return this.bulletOutSound;
    }
    
    public String getCloseSound() {
        return this.CloseSound;
    }
    
    public String getClothingItem() {
        return this.ClothingItem;
    }
    
    public void setClothingItemAsset(final ClothingItem clothingItemAsset) {
        this.clothingItemAsset = clothingItemAsset;
    }
    
    public ClothingItem getClothingItemAsset() {
        return this.clothingItemAsset;
    }
    
    public ArrayList<String> getClothingItemExtra() {
        return this.clothingItemExtra;
    }
    
    public ArrayList<String> getClothingItemExtraOption() {
        return this.clothingItemExtraOption;
    }
    
    public String getFabricType() {
        return this.fabricType;
    }
    
    public ArrayList<String> getIconsForTexture() {
        return this.IconsForTexture;
    }
    
    public String getCustomEatSound() {
        return this.customEatSound;
    }
    
    public String getEatType() {
        return this.eatType;
    }
    
    public String getCountDownSound() {
        return this.countDownSound;
    }
    
    public String getBringToBearSound() {
        return this.bringToBearSound;
    }
    
    public String getEjectAmmoStartSound() {
        return this.ejectAmmoStartSound;
    }
    
    public String getEjectAmmoSound() {
        return this.ejectAmmoSound;
    }
    
    public String getEjectAmmoStopSound() {
        return this.ejectAmmoStopSound;
    }
    
    public String getInsertAmmoStartSound() {
        return this.insertAmmoStartSound;
    }
    
    public String getInsertAmmoSound() {
        return this.insertAmmoSound;
    }
    
    public String getInsertAmmoStopSound() {
        return this.insertAmmoStopSound;
    }
    
    public String getEquipSound() {
        return this.equipSound;
    }
    
    public String getUnequipSound() {
        return this.unequipSound;
    }
    
    public String getExplosionSound() {
        return this.explosionSound;
    }
    
    public String getStaticModel() {
        return this.staticModel;
    }
    
    public String getOpenSound() {
        return this.OpenSound;
    }
    
    public String getPutInSound() {
        return this.PutInSound;
    }
    
    public String getShellFallSound() {
        return this.ShellFallSound;
    }
    
    public String getSkillTrained() {
        return this.SkillTrained;
    }
    
    public String getDoorHitSound() {
        return this.DoorHitSound;
    }
    
    public void setDoorHitSound(final String doorHitSound) {
        this.DoorHitSound = doorHitSound;
    }
    
    public boolean isManuallyRemoveSpentRounds() {
        return this.manuallyRemoveSpentRounds;
    }
    
    public float getRainFactor() {
        return this.rainFactor;
    }
    
    public String getReplaceOnUse() {
        return this.ReplaceOnUse;
    }
    
    public void setReplaceOnUse(final String replaceOnUse) {
        this.ReplaceOnUse = replaceOnUse;
    }
    
    public String getReplaceOnDeplete() {
        return this.ReplaceOnDeplete;
    }
    
    public void setReplaceOnDeplete(final String replaceOnDeplete) {
        this.ReplaceOnDeplete = replaceOnDeplete;
    }
    
    public boolean isDangerousUncooked() {
        return this.DangerousUncooked;
    }
    
    public void setDangerousUncooked(final boolean dangerousUncooked) {
        this.DangerousUncooked = dangerousUncooked;
    }
    
    public boolean isAlcoholic() {
        return this.Alcoholic;
    }
    
    public void setAlcoholic(final boolean alcoholic) {
        this.Alcoholic = alcoholic;
    }
    
    public float getPushBackMod() {
        return this.PushBackMod;
    }
    
    public void setPushBackMod(final float pushBackMod) {
        this.PushBackMod = pushBackMod;
    }
    
    public int getSplatNumber() {
        return this.SplatNumber;
    }
    
    public void setSplatNumber(final int splatNumber) {
        this.SplatNumber = splatNumber;
    }
    
    public float getNPCSoundBoost() {
        return this.NPCSoundBoost;
    }
    
    public void setNPCSoundBoost(final float npcSoundBoost) {
        this.NPCSoundBoost = npcSoundBoost;
    }
    
    public boolean isRangeFalloff() {
        return this.RangeFalloff;
    }
    
    public void setRangeFalloff(final boolean rangeFalloff) {
        this.RangeFalloff = rangeFalloff;
    }
    
    public boolean isUseEndurance() {
        return this.UseEndurance;
    }
    
    public void setUseEndurance(final boolean useEndurance) {
        this.UseEndurance = useEndurance;
    }
    
    public boolean isMultipleHitConditionAffected() {
        return this.MultipleHitConditionAffected;
    }
    
    public void setMultipleHitConditionAffected(final boolean multipleHitConditionAffected) {
        this.MultipleHitConditionAffected = multipleHitConditionAffected;
    }
    
    public boolean isShareDamage() {
        return this.ShareDamage;
    }
    
    public void setShareDamage(final boolean shareDamage) {
        this.ShareDamage = shareDamage;
    }
    
    public boolean isShareEndurance() {
        return this.ShareEndurance;
    }
    
    public void setShareEndurance(final boolean shareEndurance) {
        this.ShareEndurance = shareEndurance;
    }
    
    public boolean isCanBarricade() {
        return this.CanBarricade;
    }
    
    public void setCanBarricade(final boolean canBarricade) {
        this.CanBarricade = canBarricade;
    }
    
    public boolean isUseWhileEquipped() {
        return this.UseWhileEquipped;
    }
    
    public void setUseWhileEquipped(final boolean useWhileEquipped) {
        this.UseWhileEquipped = useWhileEquipped;
    }
    
    public boolean isUseWhileUnequipped() {
        return this.UseWhileUnequipped;
    }
    
    public void setUseWhileUnequipped(final boolean useWhileUnequipped) {
        this.UseWhileUnequipped = useWhileUnequipped;
    }
    
    public void setTicksPerEquipUse(final int ticksPerEquipUse) {
        this.TicksPerEquipUse = ticksPerEquipUse;
    }
    
    public float getTicksPerEquipUse() {
        return (float)this.TicksPerEquipUse;
    }
    
    public boolean isDisappearOnUse() {
        return this.DisappearOnUse;
    }
    
    public void setDisappearOnUse(final boolean disappearOnUse) {
        this.DisappearOnUse = disappearOnUse;
    }
    
    public float getUseDelta() {
        return this.UseDelta;
    }
    
    public void setUseDelta(final float useDelta) {
        this.UseDelta = useDelta;
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
    
    public String getBodyLocation() {
        return this.BodyLocation;
    }
    
    public void setBodyLocation(final String bodyLocation) {
        this.BodyLocation = bodyLocation;
    }
    
    public Stack<String> getPaletteChoices() {
        return this.PaletteChoices;
    }
    
    public void setPaletteChoices(final Stack<String> paletteChoices) {
        this.PaletteChoices = paletteChoices;
    }
    
    public String getSpriteName() {
        return this.SpriteName;
    }
    
    public void setSpriteName(final String spriteName) {
        this.SpriteName = spriteName;
    }
    
    public String getPalettesStart() {
        return this.PalettesStart;
    }
    
    public void setPalettesStart(final String palettesStart) {
        this.PalettesStart = palettesStart;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public void setType(final Type type) {
        this.type = type;
    }
    
    public String getTypeString() {
        return this.type.name();
    }
    
    public String getMapID() {
        return this.map;
    }
    
    @Override
    public void Load(final String name, final String[] array) {
        this.name = name;
        this.moduleDotType = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.module.name, name);
        final int n = Item.IDMax++;
        Item.NetIDToItem.put(n, this.moduleDotType);
        Item.NetItemToID.put(this.moduleDotType, n);
        this.modID = ScriptManager.getCurrentLoadFileMod();
        if (this.modID.equals("pz-vanilla")) {
            this.existsAsVanilla = true;
        }
        this.fileAbsPath = ScriptManager.getCurrentLoadFileAbsPath();
        for (int length = array.length, i = 0; i < length; ++i) {
            this.DoParam(array[i]);
        }
        if (this.DisplayName == null) {
            this.DisplayName = this.getFullName();
            this.Hidden = true;
        }
        if (!StringUtils.isNullOrWhitespace(this.replaceInPrimaryHand)) {
            final String[] split = this.replaceInPrimaryHand.trim().split("\\s+");
            if (split.length == 2) {
                this.replacePrimaryHand = new ItemReplacement();
                this.replacePrimaryHand.clothingItemName = split[0].trim();
                this.replacePrimaryHand.maskVariableValue = split[1].trim();
                this.replacePrimaryHand.maskVariableName = "RightHandMask";
            }
        }
        if (!StringUtils.isNullOrWhitespace(this.replaceInSecondHand)) {
            final String[] split2 = this.replaceInSecondHand.trim().split("\\s+");
            if (split2.length == 2) {
                this.replaceSecondHand = new ItemReplacement();
                this.replaceSecondHand.clothingItemName = split2[0].trim();
                this.replaceSecondHand.maskVariableValue = split2[1].trim();
                this.replaceSecondHand.maskVariableName = "LeftHandMask";
            }
        }
        if (!StringUtils.isNullOrWhitespace(this.primaryAnimMask)) {
            this.replacePrimaryHand = new ItemReplacement();
            this.replacePrimaryHand.maskVariableValue = this.primaryAnimMask;
            this.replacePrimaryHand.maskVariableName = "RightHandMask";
            this.replacePrimaryHand.attachment = this.primaryAnimMaskAttachment;
        }
        if (!StringUtils.isNullOrWhitespace(this.secondaryAnimMask)) {
            this.replaceSecondHand = new ItemReplacement();
            this.replaceSecondHand.maskVariableValue = this.secondaryAnimMask;
            this.replaceSecondHand.maskVariableName = "LeftHandMask";
            this.replaceSecondHand.attachment = this.secondaryAnimMaskAttachment;
        }
        WorldDictionary.onLoadItem(this);
    }
    
    public InventoryItem InstanceItem(final String s) {
        InventoryItem inventoryItem = null;
        if (this.type == Type.Key) {
            inventoryItem = new Key(this.module.name, this.DisplayName, this.name, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.Icon));
            ((Key)inventoryItem).setDigitalPadlock(this.digitalPadlock);
            ((Key)inventoryItem).setPadlock(this.padlock);
            if (((Key)inventoryItem).isPadlock()) {
                ((Key)inventoryItem).setNumberOfKey(2);
                ((Key)inventoryItem).setKeyId(Rand.Next(10000000));
            }
        }
        if (this.type == Type.KeyRing) {
            inventoryItem = new KeyRing(this.module.name, this.DisplayName, this.name, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.Icon));
        }
        if (this.type == Type.WeaponPart) {
            inventoryItem = new WeaponPart(this.module.name, this.DisplayName, this.name, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.Icon));
            final WeaponPart weaponPart = (WeaponPart)inventoryItem;
            weaponPart.setDamage(this.damageModifier);
            weaponPart.setClipSize(this.clipSizeModifier);
            weaponPart.setMaxRange(this.maxRangeModifier);
            weaponPart.setMinRangeRanged(this.minRangeRangedModifier);
            weaponPart.setRecoilDelay(this.recoilDelayModifier);
            weaponPart.setMountOn(this.mountOn);
            weaponPart.setPartType(this.partType);
            weaponPart.setReloadTime(this.reloadTimeModifier);
            weaponPart.setAimingTime(this.aimingTimeModifier);
            weaponPart.setHitChance(this.hitChanceModifier);
            weaponPart.setAngle(this.angleModifier);
            weaponPart.setWeightModifier(this.weightModifier);
        }
        if (this.type == Type.Container) {
            inventoryItem = new InventoryContainer(this.module.name, this.DisplayName, this.name, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.Icon));
            final InventoryContainer inventoryContainer = (InventoryContainer)inventoryItem;
            inventoryContainer.setItemCapacity((float)this.Capacity);
            inventoryContainer.setCapacity(this.Capacity);
            inventoryContainer.setWeightReduction(this.WeightReduction);
            inventoryContainer.setCanBeEquipped(this.CanBeEquipped);
            inventoryContainer.getInventory().setPutSound(this.PutInSound);
            inventoryContainer.getInventory().setCloseSound(this.CloseSound);
            inventoryContainer.getInventory().setOpenSound(this.OpenSound);
            inventoryContainer.getInventory().setOnlyAcceptCategory(this.OnlyAcceptCategory);
            inventoryContainer.getInventory().setAcceptItemFunction(this.AcceptItemFunction);
        }
        if (this.type == Type.Food) {
            inventoryItem = new Food(this.module.name, this.DisplayName, this.name, this);
            final Food food = (Food)inventoryItem;
            food.Poison = this.Poison;
            food.setPoisonLevelForRecipe(this.PoisonDetectionLevel);
            food.setFoodType(this.FoodType);
            food.setPoisonPower(this.PoisonPower);
            food.setUseForPoison(this.UseForPoison);
            food.setThirstChange(this.ThirstChange / 100.0f);
            food.setHungChange(this.HungerChange / 100.0f);
            food.setBaseHunger(this.HungerChange / 100.0f);
            food.setEndChange(this.EnduranceChange / 100.0f);
            food.setOffAge(this.DaysFresh);
            food.setOffAgeMax(this.DaysTotallyRotten);
            food.setIsCookable(this.IsCookable);
            food.setMinutesToCook((float)this.MinutesToCook);
            food.setMinutesToBurn((float)this.MinutesToBurn);
            food.setbDangerousUncooked(this.DangerousUncooked);
            food.setReplaceOnUse(this.ReplaceOnUse);
            food.setReplaceOnCooked(this.ReplaceOnCooked);
            food.setSpice(this.Spice);
            food.setRemoveNegativeEffectOnCooked(this.RemoveNegativeEffectOnCooked);
            food.setCustomEatSound(this.customEatSound);
            food.setOnCooked(this.OnCooked);
            food.setFluReduction(this.fluReduction);
            food.setReduceFoodSickness(this.ReduceFoodSickness);
            food.setPainReduction((float)this.painReduction);
            food.setHerbalistType(this.HerbalistType);
            food.setCarbohydrates(this.carbohydrates);
            food.setLipids(this.lipids);
            food.setProteins(this.proteins);
            food.setCalories(this.calories);
            food.setPackaged(this.packaged);
            food.setCanBeFrozen(!this.cantBeFrozen);
            food.setReplaceOnRotten(this.ReplaceOnRotten);
            food.setOnEat(this.onEat);
            food.setBadInMicrowave(this.BadInMicrowave);
            food.setGoodHot(this.GoodHot);
            food.setBadCold(this.BadCold);
        }
        if (this.type == Type.Literature) {
            inventoryItem = new Literature(this.module.name, this.DisplayName, this.name, this);
            final Literature literature = (Literature)inventoryItem;
            literature.setReplaceOnUse(this.ReplaceOnUse);
            literature.setNumberOfPages(this.NumberOfPages);
            literature.setAlreadyReadPages(0);
            literature.setSkillTrained(this.SkillTrained);
            literature.setLvlSkillTrained(this.LvlSkillTrained);
            literature.setNumLevelsTrained(this.NumLevelsTrained);
            literature.setCanBeWrite(this.canBeWrite);
            literature.setPageToWrite(this.PageToWrite);
            literature.setTeachedRecipes(this.teachedRecipes);
        }
        else if (this.type == Type.AlarmClock) {
            inventoryItem = new AlarmClock(this.module.name, this.DisplayName, this.name, this);
            final AlarmClock alarmClock = (AlarmClock)inventoryItem;
            alarmClock.setAlarmSound(this.AlarmSound);
            alarmClock.setSoundRadius(this.SoundRadius);
        }
        else if (this.type == Type.AlarmClockClothing) {
            String s2 = "";
            String s3 = null;
            if (!this.PaletteChoices.isEmpty() || s != null) {
                s3 = this.PaletteChoices.get(Rand.Next(this.PaletteChoices.size()));
                if (s != null) {
                    s3 = s;
                }
                s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s3.replace(this.PalettesStart, ""));
            }
            inventoryItem = new AlarmClockClothing(this.module.name, this.DisplayName, this.name, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.Icon.replace(".png", ""), s2), s3, this.SpriteName);
            final AlarmClockClothing alarmClockClothing = (AlarmClockClothing)inventoryItem;
            alarmClockClothing.setTemperature(this.Temperature);
            alarmClockClothing.setInsulation(this.insulation);
            alarmClockClothing.setConditionLowerChance(this.ConditionLowerChance);
            alarmClockClothing.setStompPower(this.stompPower);
            alarmClockClothing.setRunSpeedModifier(this.runSpeedModifier);
            alarmClockClothing.setCombatSpeedModifier(this.combatSpeedModifier);
            alarmClockClothing.setRemoveOnBroken(this.removeOnBroken);
            alarmClockClothing.setCanHaveHoles(this.canHaveHoles);
            alarmClockClothing.setWeightWet(this.WeightWet);
            alarmClockClothing.setBiteDefense(this.biteDefense);
            alarmClockClothing.setBulletDefense(this.bulletDefense);
            alarmClockClothing.setNeckProtectionModifier(this.neckProtectionModifier);
            alarmClockClothing.setScratchDefense(this.scratchDefense);
            alarmClockClothing.setChanceToFall(this.chanceToFall);
            alarmClockClothing.setWindresistance(this.windresist);
            alarmClockClothing.setWaterResistance(this.waterresist);
            alarmClockClothing.setAlarmSound(this.AlarmSound);
            alarmClockClothing.setSoundRadius(this.SoundRadius);
        }
        else if (this.type == Type.Weapon) {
            inventoryItem = new HandWeapon(this.module.name, this.DisplayName, this.name, this);
            final HandWeapon handWeapon = (HandWeapon)inventoryItem;
            handWeapon.setMultipleHitConditionAffected(this.MultipleHitConditionAffected);
            handWeapon.setConditionLowerChance(this.ConditionLowerChance);
            handWeapon.SplatSize = this.SplatSize;
            handWeapon.aimingMod = this.AimingMod;
            handWeapon.setMinDamage(this.MinDamage);
            handWeapon.setMaxDamage(this.MaxDamage);
            handWeapon.setBaseSpeed(this.baseSpeed);
            handWeapon.setPhysicsObject(this.PhysicsObject);
            handWeapon.setOtherHandRequire(this.OtherHandRequire);
            handWeapon.setOtherHandUse(this.OtherHandUse);
            handWeapon.setMaxRange(this.MaxRange);
            handWeapon.setMinRange(this.MinRange);
            handWeapon.setShareEndurance(this.ShareEndurance);
            handWeapon.setKnockdownMod(this.KnockdownMod);
            handWeapon.bIsAimedFirearm = this.IsAimedFirearm;
            handWeapon.RunAnim = this.RunAnim;
            handWeapon.IdleAnim = this.IdleAnim;
            handWeapon.HitAngleMod = (float)Math.toRadians(this.HitAngleMod);
            handWeapon.bIsAimedHandWeapon = this.IsAimedHandWeapon;
            handWeapon.setCantAttackWithLowestEndurance(this.CantAttackWithLowestEndurance);
            handWeapon.setAlwaysKnockdown(this.AlwaysKnockdown);
            handWeapon.setEnduranceMod(this.EnduranceMod);
            handWeapon.setUseSelf(this.UseSelf);
            handWeapon.setMaxHitCount(this.MaxHitCount);
            handWeapon.setMinimumSwingTime(this.MinimumSwingTime);
            handWeapon.setSwingTime(this.SwingTime);
            handWeapon.setDoSwingBeforeImpact(this.SwingAmountBeforeImpact);
            handWeapon.setMinAngle(this.MinAngle);
            handWeapon.setDoorDamage(this.DoorDamage);
            handWeapon.setTreeDamage(this.treeDamage);
            handWeapon.setDoorHitSound(this.DoorHitSound);
            handWeapon.setHitFloorSound(this.hitFloorSound);
            handWeapon.setZombieHitSound(this.HitSound);
            handWeapon.setPushBackMod(this.PushBackMod);
            handWeapon.setWeight(this.WeaponWeight);
            handWeapon.setImpactSound(this.ImpactSound);
            handWeapon.setSplatNumber(this.SplatNumber);
            handWeapon.setKnockBackOnNoDeath(this.KnockBackOnNoDeath);
            handWeapon.setSplatBloodOnNoDeath(this.SplatBloodOnNoDeath);
            handWeapon.setSwingSound(this.SwingSound);
            handWeapon.setBulletOutSound(this.bulletOutSound);
            handWeapon.setShellFallSound(this.ShellFallSound);
            handWeapon.setAngleFalloff(this.AngleFalloff);
            handWeapon.setSoundVolume(this.SoundVolume);
            handWeapon.setSoundRadius(this.SoundRadius);
            handWeapon.setToHitModifier(this.ToHitModifier);
            handWeapon.setOtherBoost(this.NPCSoundBoost);
            handWeapon.setRanged(this.Ranged);
            handWeapon.setRangeFalloff(this.RangeFalloff);
            handWeapon.setUseEndurance(this.UseEndurance);
            handWeapon.setCriticalChance(this.CriticalChance);
            handWeapon.setCritDmgMultiplier(this.critDmgMultiplier);
            handWeapon.setShareDamage(this.ShareDamage);
            handWeapon.setCanBarracade(this.CanBarricade);
            handWeapon.setWeaponSprite(this.WeaponSprite);
            handWeapon.setOriginalWeaponSprite(this.WeaponSprite);
            handWeapon.setSubCategory(this.SubCategory);
            handWeapon.setCategories(this.Categories);
            handWeapon.setSoundGain(this.SoundGain);
            handWeapon.setAimingPerkCritModifier(this.AimingPerkCritModifier);
            handWeapon.setAimingPerkRangeModifier(this.AimingPerkRangeModifier);
            handWeapon.setAimingPerkHitChanceModifier(this.AimingPerkHitChanceModifier);
            handWeapon.setHitChance(this.HitChance);
            handWeapon.setRecoilDelay(this.RecoilDelay);
            handWeapon.setAimingPerkMinAngleModifier(this.AimingPerkMinAngleModifier);
            handWeapon.setPiercingBullets(this.PiercingBullets);
            handWeapon.setClipSize(this.ClipSize);
            handWeapon.setReloadTime(this.reloadTime);
            handWeapon.setAimingTime(this.aimingTime);
            handWeapon.setTriggerExplosionTimer(this.triggerExplosionTimer);
            handWeapon.setSensorRange(this.sensorRange);
            handWeapon.setWeaponLength(this.WeaponLength);
            handWeapon.setPlacedSprite(this.PlacedSprite);
            handWeapon.setExplosionTimer(this.explosionTimer);
            handWeapon.setCanBePlaced(this.canBePlaced);
            handWeapon.setCanBeReused(this.canBeReused);
            handWeapon.setExplosionRange(this.explosionRange);
            handWeapon.setExplosionPower(this.explosionPower);
            handWeapon.setFireRange(this.fireRange);
            handWeapon.setFirePower(this.firePower);
            handWeapon.setSmokeRange(this.smokeRange);
            handWeapon.setNoiseRange(this.noiseRange);
            handWeapon.setExtraDamage(this.extraDamage);
            handWeapon.setAmmoBox(this.ammoBox);
            handWeapon.setRackSound(this.rackSound);
            handWeapon.setClickSound(this.clickSound);
            handWeapon.setMagazineType(this.magazineType);
            handWeapon.setWeaponReloadType(this.weaponReloadType);
            handWeapon.setInsertAllBulletsReload(this.insertAllBulletsReload);
            handWeapon.setRackAfterShoot(this.rackAfterShoot);
            handWeapon.setJamGunChance(this.jamGunChance);
            handWeapon.setModelWeaponPart(this.modelWeaponPart);
            handWeapon.setHaveChamber(this.haveChamber);
            handWeapon.setDamageCategory(this.damageCategory);
            handWeapon.setDamageMakeHole(this.damageMakeHole);
            handWeapon.setFireMode(this.fireMode);
            handWeapon.setFireModePossibilities(this.fireModePossibilities);
        }
        else if (this.type == Type.Normal) {
            inventoryItem = new ComboItem(this.module.name, this.DisplayName, this.name, this);
        }
        else if (this.type == Type.Clothing) {
            String s4 = "";
            String s5 = null;
            if (!this.PaletteChoices.isEmpty() || s != null) {
                s5 = this.PaletteChoices.get(Rand.Next(this.PaletteChoices.size()));
                if (s != null) {
                    s5 = s;
                }
                s4 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s5.replace(this.PalettesStart, ""));
            }
            inventoryItem = new Clothing(this.module.name, this.DisplayName, this.name, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.Icon.replace(".png", ""), s4), s5, this.SpriteName);
            final AlarmClockClothing alarmClockClothing2 = (AlarmClockClothing)inventoryItem;
            alarmClockClothing2.setTemperature(this.Temperature);
            alarmClockClothing2.setInsulation(this.insulation);
            alarmClockClothing2.setConditionLowerChance(this.ConditionLowerChance);
            alarmClockClothing2.setStompPower(this.stompPower);
            alarmClockClothing2.setRunSpeedModifier(this.runSpeedModifier);
            alarmClockClothing2.setCombatSpeedModifier(this.combatSpeedModifier);
            alarmClockClothing2.setRemoveOnBroken(this.removeOnBroken);
            alarmClockClothing2.setCanHaveHoles(this.canHaveHoles);
            alarmClockClothing2.setWeightWet(this.WeightWet);
            alarmClockClothing2.setBiteDefense(this.biteDefense);
            alarmClockClothing2.setBulletDefense(this.bulletDefense);
            alarmClockClothing2.setNeckProtectionModifier(this.neckProtectionModifier);
            alarmClockClothing2.setScratchDefense(this.scratchDefense);
            alarmClockClothing2.setChanceToFall(this.chanceToFall);
            alarmClockClothing2.setWindresistance(this.windresist);
            alarmClockClothing2.setWaterResistance(this.waterresist);
        }
        else if (this.type == Type.Drainable) {
            inventoryItem = new DrainableComboItem(this.module.name, this.DisplayName, this.name, this);
            final DrainableComboItem drainableComboItem = (DrainableComboItem)inventoryItem;
            drainableComboItem.setUseWhileEquiped(this.UseWhileEquipped);
            drainableComboItem.setUseWhileUnequiped(this.UseWhileUnequipped);
            drainableComboItem.setTicksPerEquipUse(this.TicksPerEquipUse);
            drainableComboItem.setUseDelta(this.UseDelta);
            drainableComboItem.setReplaceOnDeplete(this.ReplaceOnDeplete);
            drainableComboItem.setIsCookable(this.IsCookable);
            drainableComboItem.setRainFactor(this.rainFactor);
            drainableComboItem.setCanConsolidate(!this.cantBeConsolided);
            drainableComboItem.setWeightEmpty(this.WeightEmpty);
        }
        else if (this.type == Type.Radio) {
            inventoryItem = new Radio(this.module.name, this.DisplayName, this.name, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.Icon));
            final Radio radio = (Radio)inventoryItem;
            final DeviceData deviceData = radio.getDeviceData();
            if (deviceData != null) {
                if (this.DisplayName != null) {
                    deviceData.setDeviceName(this.DisplayName);
                }
                deviceData.setIsTwoWay(this.twoWay);
                deviceData.setTransmitRange(this.transmitRange);
                deviceData.setMicRange(this.micRange);
                deviceData.setBaseVolumeRange(this.baseVolumeRange);
                deviceData.setIsPortable(this.isPortable);
                deviceData.setIsTelevision(this.isTelevision);
                deviceData.setMinChannelRange(this.minChannel);
                deviceData.setMaxChannelRange(this.maxChannel);
                deviceData.setIsBatteryPowered(this.usesBattery);
                deviceData.setIsHighTier(this.isHighTier);
                deviceData.setUseDelta(this.UseDelta);
                deviceData.setMediaType(this.acceptMediaType);
                deviceData.setNoTransmit(this.noTransmit);
                deviceData.generatePresets();
                deviceData.setRandomChannel();
            }
            if (!radio.ReadFromWorldSprite(this.worldObjectSprite)) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.moduleDotType != null) ? this.moduleDotType : "unknown"));
            }
        }
        else if (this.type == Type.Moveable) {
            inventoryItem = new Moveable(this.module.name, this.DisplayName, this.name, this);
            final Radio radio2 = (Radio)inventoryItem;
            radio2.ReadFromWorldSprite(this.worldObjectSprite);
            this.ActualWeight = radio2.getActualWeight();
        }
        else if (this.type == Type.Map) {
            final MapItem mapItem = new MapItem(this.module.name, this.DisplayName, this.name, this);
            if (StringUtils.isNullOrWhitespace(this.map)) {
                mapItem.setMapID(MapDefinitions.getInstance().pickRandom());
            }
            else {
                mapItem.setMapID(this.map);
            }
            inventoryItem = mapItem;
        }
        if (this.colorRed < 255 || this.colorGreen < 255 || this.colorBlue < 255) {
            inventoryItem.setColor(new Color(this.colorRed / 255.0f, this.colorGreen / 255.0f, this.colorBlue / 255.0f));
        }
        inventoryItem.setAlcoholPower(this.alcoholPower);
        inventoryItem.setConditionMax(this.ConditionMax);
        inventoryItem.setCondition(this.ConditionMax);
        inventoryItem.setCanBeActivated(this.ActivatedItem);
        inventoryItem.setLightStrength(this.LightStrength);
        inventoryItem.setTorchCone(this.TorchCone);
        inventoryItem.setLightDistance(this.LightDistance);
        inventoryItem.setActualWeight(this.ActualWeight);
        inventoryItem.setWeight(this.ActualWeight);
        inventoryItem.setUses(this.Count);
        inventoryItem.setScriptItem(this);
        inventoryItem.setBoredomChange(this.BoredomChange);
        inventoryItem.setStressChange(this.StressChange / 100.0f);
        inventoryItem.setUnhappyChange(this.UnhappyChange);
        inventoryItem.setReplaceOnUseOn(this.ReplaceOnUseOn);
        inventoryItem.setRequireInHandOrInventory(this.RequireInHandOrInventory);
        inventoryItem.setAttachmentsProvided(this.attachmentsProvided);
        inventoryItem.setAttachmentReplacement(this.attachmentReplacement);
        inventoryItem.setIsWaterSource(this.IsWaterSource);
        inventoryItem.CanStoreWater = this.CanStoreWater;
        inventoryItem.CanStack = this.CanStack;
        inventoryItem.copyModData(this.DefaultModData);
        inventoryItem.setCount(this.Count);
        inventoryItem.setFatigueChange(this.FatigueChange / 100.0f);
        inventoryItem.setTooltip(this.Tooltip);
        inventoryItem.setDisplayCategory(this.DisplayCategory);
        inventoryItem.setAlcoholic(this.Alcoholic);
        inventoryItem.RequiresEquippedBothHands = this.RequiresEquippedBothHands;
        inventoryItem.setBreakSound(this.breakSound);
        inventoryItem.setReplaceOnUse(this.ReplaceOnUse);
        inventoryItem.setBandagePower(this.bandagePower);
        inventoryItem.setReduceInfectionPower(this.ReduceInfectionPower);
        inventoryItem.setCanBeRemote(this.canBeRemote);
        inventoryItem.setRemoteController(this.remoteController);
        inventoryItem.setRemoteRange(this.remoteRange);
        inventoryItem.setCountDownSound(this.countDownSound);
        inventoryItem.setExplosionSound(this.explosionSound);
        inventoryItem.setColorRed(this.colorRed / 255.0f);
        inventoryItem.setColorGreen(this.colorGreen / 255.0f);
        inventoryItem.setColorBlue(this.colorBlue / 255.0f);
        inventoryItem.setEvolvedRecipeName(this.evolvedRecipeName);
        inventoryItem.setMetalValue(this.metalValue);
        inventoryItem.setWet(this.isWet);
        inventoryItem.setWetCooldown(this.wetCooldown);
        inventoryItem.setItemWhenDry(this.itemWhenDry);
        inventoryItem.keepOnDeplete = this.keepOnDeplete;
        inventoryItem.setItemCapacity((float)this.itemCapacity);
        inventoryItem.setMaxCapacity(this.maxCapacity);
        inventoryItem.setBrakeForce(this.brakeForce);
        inventoryItem.setChanceToSpawnDamaged(this.chanceToSpawnDamaged);
        inventoryItem.setConditionLowerNormal(this.conditionLowerNormal);
        inventoryItem.setConditionLowerOffroad(this.conditionLowerOffroad);
        inventoryItem.setWheelFriction(this.wheelFriction);
        inventoryItem.setSuspensionCompression(this.suspensionCompression);
        inventoryItem.setEngineLoudness(this.engineLoudness);
        inventoryItem.setSuspensionDamping(this.suspensionDamping);
        if (this.CustomContextMenu != null) {
            inventoryItem.setCustomMenuOption(Translator.getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.CustomContextMenu)));
        }
        if (this.IconsForTexture != null && !this.IconsForTexture.isEmpty()) {
            inventoryItem.setIconsForTexture(this.IconsForTexture);
        }
        inventoryItem.setBloodClothingType(this.bloodClothingType);
        inventoryItem.CloseKillMove = this.CloseKillMove;
        inventoryItem.setAmmoType(this.AmmoType);
        inventoryItem.setMaxAmmo(this.maxAmmo);
        inventoryItem.setGunType(this.GunType);
        inventoryItem.setAttachmentType(this.attachmentType);
        if (this.recordedMediaCat != null) {
            final MediaData randomFromCategory = ZomboidRadio.getInstance().getRecordedMedia().getRandomFromCategory(this.recordedMediaCat);
            if (randomFromCategory != null) {
                inventoryItem.setRecordedMediaIndex(randomFromCategory.getIndex());
            }
        }
        final long seed = OutfitRNG.getSeed();
        OutfitRNG.setSeed(Rand.Next(Integer.MAX_VALUE));
        inventoryItem.synchWithVisual();
        OutfitRNG.setSeed(seed);
        inventoryItem.setRegistry_id(this);
        return inventoryItem;
    }
    
    public void DoParam(final String s) {
        if (s.trim().length() == 0) {
            return;
        }
        try {
            final String[] split = s.split("=");
            final String trim = split[0].trim();
            final String trim2 = split[1].trim();
            if (trim.trim().equalsIgnoreCase("BodyLocation")) {
                this.BodyLocation = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("Palettes")) {
                final String[] split2 = trim2.split("/");
                for (int i = 0; i < split2.length; ++i) {
                    this.PaletteChoices.add(split2[i].trim());
                }
            }
            else if (trim.trim().equalsIgnoreCase("HitSound")) {
                this.HitSound = trim2.trim();
                if (this.HitSound.equals("null")) {
                    this.HitSound = null;
                }
            }
            else if (trim.trim().equalsIgnoreCase("HitFloorSound")) {
                this.hitFloorSound = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("PalettesStart")) {
                this.PalettesStart = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("DisplayName")) {
                this.DisplayName = Translator.getDisplayItemName(trim2.trim());
                this.DisplayName = Translator.getItemNameFromFullType(this.getFullName());
            }
            else if (trim.trim().equalsIgnoreCase("MetalValue")) {
                this.metalValue = new Float(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("SpriteName")) {
                this.SpriteName = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("Type")) {
                this.type = Type.valueOf(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("SplatSize")) {
                this.SplatSize = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("CanStoreWater")) {
                this.CanStoreWater = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("IsWaterSource")) {
                this.IsWaterSource = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("Poison")) {
                this.Poison = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("FoodType")) {
                this.FoodType = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("PoisonDetectionLevel")) {
                this.PoisonDetectionLevel = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("PoisonPower")) {
                this.PoisonPower = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("UseForPoison")) {
                this.UseForPoison = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("SwingAnim")) {
                this.SwingAnim = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("Icon")) {
                this.Icon = trim2;
                this.ItemName = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.Icon);
                this.NormalTexture = Texture.trygetTexture(this.ItemName);
                if (this.NormalTexture == null) {
                    this.NormalTexture = Texture.getSharedTexture("media/inventory/Question_On.png");
                }
                this.WorldTextureName = this.ItemName.replace("Item_", "media/inventory/world/WItem_");
                this.WorldTextureName = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.WorldTextureName);
                this.WorldTexture = Texture.getSharedTexture(this.WorldTextureName);
                if (this.type == Type.Food) {
                    Texture texture = Texture.trygetTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.ItemName));
                    String s2 = this.WorldTextureName.replace(".png", "Rotten.png");
                    if (texture == null) {
                        texture = Texture.trygetTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.ItemName));
                        s2 = s2.replace("Rotten.png", "Spoiled.png");
                    }
                    this.SpecialWorldTextureNames.add(s2);
                    this.SpecialTextures.add(texture);
                    this.SpecialTextures.add(Texture.trygetTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.ItemName)));
                    this.SpecialWorldTextureNames.add(this.WorldTextureName.replace(".png", "Cooked.png"));
                    Texture texture2 = Texture.trygetTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.ItemName));
                    String s3 = this.WorldTextureName.replace(".png", "Overdone.png");
                    if (texture2 == null) {
                        texture2 = Texture.trygetTexture(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.ItemName));
                        s3 = s3.replace("Overdone.png", "Burnt.png");
                    }
                    this.SpecialTextures.add(texture2);
                    this.SpecialWorldTextureNames.add(s3);
                }
            }
            else if (trim.trim().equalsIgnoreCase("UseWorldItem")) {
                this.UseWorldItem = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("Medical")) {
                this.Medical = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("CannedFood")) {
                this.CannedFood = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("MechanicsItem")) {
                this.MechanicsItem = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("SurvivalGear")) {
                this.SurvivalGear = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("ScaleWorldIcon")) {
                this.ScaleWorldIcon = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("HairDye")) {
                this.hairDye = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("DoorHitSound")) {
                this.DoorHitSound = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("Weight")) {
                this.ActualWeight = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("WeightWet")) {
                this.WeightWet = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("WeightEmpty")) {
                this.WeightEmpty = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("HungerChange")) {
                this.HungerChange = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("ThirstChange")) {
                this.ThirstChange = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("FatigueChange")) {
                this.FatigueChange = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("EnduranceChange")) {
                this.EnduranceChange = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("CriticalChance")) {
                this.CriticalChance = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("critDmgMultiplier")) {
                this.critDmgMultiplier = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("DaysFresh")) {
                this.DaysFresh = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("DaysTotallyRotten")) {
                this.DaysTotallyRotten = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("IsCookable")) {
                this.IsCookable = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("CookingSound")) {
                this.CookingSound = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("MinutesToCook")) {
                this.MinutesToCook = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("MinutesToBurn")) {
                this.MinutesToBurn = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("BoredomChange")) {
                this.BoredomChange = (float)Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("StressChange")) {
                this.StressChange = (float)Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("UnhappyChange")) {
                this.UnhappyChange = (float)Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("RemoveUnhappinessWhenCooked")) {
                this.RemoveUnhappinessWhenCooked = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("ReplaceOnDeplete")) {
                this.ReplaceOnDeplete = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("ReplaceOnUseOn")) {
                this.ReplaceOnUseOn = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("Ranged")) {
                this.Ranged = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("UseSelf")) {
                this.UseSelf = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("OtherHandUse")) {
                this.OtherHandUse = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("DangerousUncooked")) {
                this.DangerousUncooked = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("MaxRange")) {
                this.MaxRange = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("MinRange")) {
                this.MinRange = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("MinAngle")) {
                this.MinAngle = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("MaxDamage")) {
                this.MaxDamage = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("BaseSpeed")) {
                this.baseSpeed = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("stompPower")) {
                this.stompPower = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("combatSpeedModifier")) {
                this.combatSpeedModifier = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("runSpeedModifier")) {
                this.runSpeedModifier = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("clothingItemExtra")) {
                this.clothingItemExtra = new ArrayList<String>();
                final String[] split3 = trim2.split(";");
                for (int j = 0; j < split3.length; ++j) {
                    this.clothingItemExtra.add(split3[j]);
                }
            }
            else if (trim.trim().equalsIgnoreCase("clothingExtraSubmenu")) {
                this.clothingExtraSubmenu = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("removeOnBroken")) {
                this.removeOnBroken = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("canHaveHoles")) {
                this.canHaveHoles = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("Cosmetic")) {
                this.cosmetic = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("ammoBox")) {
                this.ammoBox = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("InsertAmmoStartSound")) {
                this.insertAmmoStartSound = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("InsertAmmoSound")) {
                this.insertAmmoSound = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("InsertAmmoStopSound")) {
                this.insertAmmoStopSound = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("EjectAmmoStartSound")) {
                this.ejectAmmoStartSound = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("EjectAmmoSound")) {
                this.ejectAmmoSound = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("EjectAmmoStopSound")) {
                this.ejectAmmoStopSound = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("rackSound")) {
                this.rackSound = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("clickSound")) {
                this.clickSound = trim2;
            }
            else if (trim.equalsIgnoreCase("BringToBearSound")) {
                this.bringToBearSound = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if (trim.equalsIgnoreCase("EquipSound")) {
                this.equipSound = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if (trim.equalsIgnoreCase("UnequipSound")) {
                this.unequipSound = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("magazineType")) {
                this.magazineType = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("jamGunChance")) {
                this.jamGunChance = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("modelWeaponPart")) {
                if (this.modelWeaponPart == null) {
                    this.modelWeaponPart = new ArrayList<ModelWeaponPart>();
                }
                final String[] split4 = trim2.split("\\s+");
                if (split4.length >= 2 && split4.length <= 4) {
                    ModelWeaponPart e = null;
                    for (int k = 0; k < this.modelWeaponPart.size(); ++k) {
                        final ModelWeaponPart modelWeaponPart = this.modelWeaponPart.get(k);
                        if (modelWeaponPart.partType.equals(split4[0])) {
                            e = modelWeaponPart;
                            break;
                        }
                    }
                    if (e == null) {
                        e = new ModelWeaponPart();
                    }
                    e.partType = split4[0];
                    e.modelName = split4[1];
                    e.attachmentNameSelf = ((split4.length > 2) ? split4[2] : null);
                    e.attachmentParent = ((split4.length > 3) ? split4[3] : null);
                    if (!e.partType.contains(".")) {
                        e.partType = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.module.name, e.partType);
                    }
                    if (!e.modelName.contains(".")) {
                        e.modelName = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.module.name, e.modelName);
                    }
                    if ("none".equalsIgnoreCase(e.attachmentNameSelf)) {
                        e.attachmentNameSelf = null;
                    }
                    if ("none".equalsIgnoreCase(e.attachmentParent)) {
                        e.attachmentParent = null;
                    }
                    this.modelWeaponPart.add(e);
                }
            }
            else if (trim.trim().equalsIgnoreCase("rackAfterShoot")) {
                this.rackAfterShoot = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("haveChamber")) {
                this.haveChamber = Boolean.parseBoolean(trim2);
            }
            else if (trim.equalsIgnoreCase("ManuallyRemoveSpentRounds")) {
                this.manuallyRemoveSpentRounds = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("biteDefense")) {
                this.biteDefense = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("bulletDefense")) {
                this.bulletDefense = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("neckProtectionModifier")) {
                this.neckProtectionModifier = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("damageCategory")) {
                this.damageCategory = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("fireMode")) {
                this.fireMode = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("damageMakeHole")) {
                this.damageMakeHole = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("equippedNoSprint")) {
                this.equippedNoSprint = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("scratchDefense")) {
                this.scratchDefense = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("weaponReloadType")) {
                this.weaponReloadType = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("insertAllBulletsReload")) {
                this.insertAllBulletsReload = Boolean.parseBoolean(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("clothingItemExtraOption")) {
                this.clothingItemExtraOption = new ArrayList<String>();
                final String[] split5 = trim2.split(";");
                for (int l = 0; l < split5.length; ++l) {
                    this.clothingItemExtraOption.add(split5[l]);
                }
            }
            else if (trim.trim().equalsIgnoreCase("ConditionLowerChanceOneIn")) {
                this.ConditionLowerChance = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("MultipleHitConditionAffected")) {
                this.MultipleHitConditionAffected = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("CanBandage")) {
                this.CanBandage = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("ConditionMax")) {
                this.ConditionMax = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("SoundGain")) {
                this.SoundGain = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("MinDamage")) {
                this.MinDamage = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("MinimumSwingTime")) {
                this.MinimumSwingTime = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("SwingSound")) {
                this.SwingSound = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("ReplaceOnUse")) {
                this.ReplaceOnUse = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("WeaponSprite")) {
                this.WeaponSprite = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("AimingPerkCritModifier")) {
                this.AimingPerkCritModifier = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("AimingPerkRangeModifier")) {
                this.AimingPerkRangeModifier = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("AimingPerkHitChanceModifier")) {
                this.AimingPerkHitChanceModifier = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("AngleModifier")) {
                this.angleModifier = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("WeightModifier")) {
                this.weightModifier = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("AimingPerkMinAngleModifier")) {
                this.AimingPerkMinAngleModifier = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("HitChance")) {
                this.HitChance = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("RecoilDelay")) {
                this.RecoilDelay = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("StopPower")) {
                this.stopPower = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("PiercingBullets")) {
                this.PiercingBullets = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("AngleFalloff")) {
                this.AngleFalloff = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("SoundVolume")) {
                this.SoundVolume = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("ToHitModifier")) {
                this.ToHitModifier = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("SoundRadius")) {
                this.SoundRadius = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("Categories")) {
                final String[] split6 = trim2.split(";");
                for (int n = 0; n < split6.length; ++n) {
                    this.Categories.add(split6[n].trim());
                }
            }
            else if (trim.trim().equalsIgnoreCase("Tags")) {
                final String[] split7 = trim2.split(";");
                for (int n2 = 0; n2 < split7.length; ++n2) {
                    this.Tags.add(split7[n2].trim());
                }
            }
            else if (trim.trim().equalsIgnoreCase("OtherCharacterVolumeBoost")) {
                this.OtherCharacterVolumeBoost = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("ImpactSound")) {
                this.ImpactSound = trim2;
                if (this.ImpactSound.equals("null")) {
                    this.ImpactSound = null;
                }
            }
            else if (trim.trim().equalsIgnoreCase("SwingTime")) {
                this.SwingTime = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("KnockBackOnNoDeath")) {
                this.KnockBackOnNoDeath = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("Alcoholic")) {
                this.Alcoholic = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("SplatBloodOnNoDeath")) {
                this.SplatBloodOnNoDeath = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("SwingAmountBeforeImpact")) {
                this.SwingAmountBeforeImpact = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("AmmoType")) {
                this.AmmoType = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("maxAmmo")) {
                this.maxAmmo = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("GunType")) {
                this.GunType = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("HitAngleMod")) {
                this.HitAngleMod = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("OtherHandRequire")) {
                this.OtherHandRequire = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("AlwaysWelcomeGift")) {
                this.AlwaysWelcomeGift = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("CantAttackWithLowestEndurance")) {
                this.CantAttackWithLowestEndurance = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("EnduranceMod")) {
                this.EnduranceMod = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("KnockdownMod")) {
                this.KnockdownMod = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("DoorDamage")) {
                this.DoorDamage = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("MaxHitCount")) {
                this.MaxHitCount = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("PhysicsObject")) {
                this.PhysicsObject = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("Count")) {
                this.Count = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("SwingAnim")) {
                this.SwingAnim = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("WeaponWeight")) {
                this.WeaponWeight = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("IdleAnim")) {
                this.IdleAnim = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("RunAnim")) {
                this.RunAnim = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("RequireInHandOrInventory")) {
                this.RequireInHandOrInventory = new ArrayList<String>(Arrays.asList(trim2.split("/")));
            }
            else if (trim.trim().equalsIgnoreCase("fireModePossibilities")) {
                this.fireModePossibilities = new ArrayList<String>(Arrays.asList(trim2.split("/")));
            }
            else if (trim.trim().equalsIgnoreCase("attachmentsProvided")) {
                this.attachmentsProvided = new ArrayList<String>(Arrays.asList(trim2.split(";")));
            }
            else if (trim.trim().equalsIgnoreCase("attachmentReplacement")) {
                this.attachmentReplacement = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("PushBackMod")) {
                this.PushBackMod = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("NPCSoundBoost")) {
                this.NPCSoundBoost = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("SplatNumber")) {
                this.SplatNumber = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("RangeFalloff")) {
                this.RangeFalloff = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("UseEndurance")) {
                this.UseEndurance = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("ShareDamage")) {
                this.ShareDamage = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("ShareEndurance")) {
                this.ShareEndurance = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("AlwaysKnockdown")) {
                this.AlwaysKnockdown = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("IsAimedFirearm")) {
                this.IsAimedFirearm = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("bulletOutSound")) {
                this.bulletOutSound = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("ShellFallSound")) {
                this.ShellFallSound = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("IsAimedHandWeapon")) {
                this.IsAimedHandWeapon = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("AimingMod")) {
                this.AimingMod = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("ProjectileCount")) {
                this.ProjectileCount = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("CanStack")) {
                this.IsAimedFirearm = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("HerbalistType")) {
                this.HerbalistType = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("CanBarricade")) {
                this.CanBarricade = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("UseWhileEquipped")) {
                this.UseWhileEquipped = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("UseWhileUnequipped")) {
                this.UseWhileUnequipped = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("TicksPerEquipUse")) {
                this.TicksPerEquipUse = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("DisappearOnUse")) {
                this.DisappearOnUse = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("Temperature")) {
                this.Temperature = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("Insulation")) {
                this.insulation = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("WindResistance")) {
                this.windresist = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("WaterResistance")) {
                this.waterresist = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("CloseKillMove")) {
                this.CloseKillMove = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("UseDelta")) {
                this.UseDelta = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("RainFactor")) {
                this.rainFactor = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("TorchDot")) {
                this.torchDot = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("NumberOfPages")) {
                this.NumberOfPages = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("SkillTrained")) {
                this.SkillTrained = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("LvlSkillTrained")) {
                this.LvlSkillTrained = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("NumLevelsTrained")) {
                this.NumLevelsTrained = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("Capacity")) {
                this.Capacity = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("MaxCapacity")) {
                this.maxCapacity = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("ItemCapacity")) {
                this.itemCapacity = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("ConditionAffectsCapacity")) {
                this.ConditionAffectsCapacity = Boolean.parseBoolean(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("BrakeForce")) {
                this.brakeForce = (float)Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("ChanceToSpawnDamaged")) {
                this.chanceToSpawnDamaged = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("WeaponLength")) {
                this.WeaponLength = new Float(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("ClipSize")) {
                this.ClipSize = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("ReloadTime")) {
                this.reloadTime = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("AimingTime")) {
                this.aimingTime = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("AimingTimeModifier")) {
                this.aimingTimeModifier = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("ReloadTimeModifier")) {
                this.reloadTimeModifier = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("HitChanceModifier")) {
                this.hitChanceModifier = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("WeightReduction")) {
                this.WeightReduction = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("CanBeEquipped")) {
                this.CanBeEquipped = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("SubCategory")) {
                this.SubCategory = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("ActivatedItem")) {
                this.ActivatedItem = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("ProtectFromRainWhenEquipped")) {
                this.ProtectFromRainWhenEquipped = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("LightStrength")) {
                this.LightStrength = new Float(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("TorchCone")) {
                this.TorchCone = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("LightDistance")) {
                this.LightDistance = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("TwoHandWeapon")) {
                this.TwoHandWeapon = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("Tooltip")) {
                this.Tooltip = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("DisplayCategory")) {
                this.DisplayCategory = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("BadInMicrowave")) {
                this.BadInMicrowave = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("GoodHot")) {
                this.GoodHot = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("BadCold")) {
                this.BadCold = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("AlarmSound")) {
                this.AlarmSound = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("RequiresEquippedBothHands")) {
                this.RequiresEquippedBothHands = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("ReplaceOnCooked")) {
                this.ReplaceOnCooked = Arrays.asList(trim2.trim().split(";"));
            }
            else if (trim.trim().equalsIgnoreCase("CustomContextMenu")) {
                this.CustomContextMenu = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("Trap")) {
                this.Trap = Boolean.parseBoolean(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("Wet")) {
                this.isWet = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("WetCooldown")) {
                this.wetCooldown = Float.parseFloat(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("ItemWhenDry")) {
                this.itemWhenDry = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("FishingLure")) {
                this.FishingLure = Boolean.parseBoolean(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("CanBeWrite")) {
                this.canBeWrite = Boolean.parseBoolean(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("PageToWrite")) {
                this.PageToWrite = Integer.parseInt(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("Spice")) {
                this.Spice = trim2.trim().equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("RemoveNegativeEffectOnCooked")) {
                this.RemoveNegativeEffectOnCooked = trim2.trim().equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("ClipSizeModifier")) {
                this.clipSizeModifier = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("RecoilDelayModifier")) {
                this.recoilDelayModifier = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("MaxRangeModifier")) {
                this.maxRangeModifier = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("MinRangeModifier")) {
                this.minRangeRangedModifier = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("DamageModifier")) {
                this.damageModifier = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("Map")) {
                this.map = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("PutInSound")) {
                this.PutInSound = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("CloseSound")) {
                this.CloseSound = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("OpenSound")) {
                this.OpenSound = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("BreakSound")) {
                this.breakSound = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("TreeDamage")) {
                this.treeDamage = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("CustomEatSound")) {
                this.customEatSound = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("AlcoholPower")) {
                this.alcoholPower = Float.parseFloat(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("BandagePower")) {
                this.bandagePower = Float.parseFloat(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("ReduceInfectionPower")) {
                this.ReduceInfectionPower = Float.parseFloat(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("OnCooked")) {
                this.OnCooked = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("OnlyAcceptCategory")) {
                this.OnlyAcceptCategory = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("AcceptItemFunction")) {
                this.AcceptItemFunction = StringUtils.discardNullOrWhitespace(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("Padlock")) {
                this.padlock = trim2.trim().equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("DigitalPadlock")) {
                this.digitalPadlock = trim2.trim().equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("triggerExplosionTimer")) {
                this.triggerExplosionTimer = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("sensorRange")) {
                this.sensorRange = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("remoteRange")) {
                this.remoteRange = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("CountDownSound")) {
                this.countDownSound = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("explosionSound")) {
                this.explosionSound = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("PlacedSprite")) {
                this.PlacedSprite = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("explosionTimer")) {
                this.explosionTimer = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("explosionRange")) {
                this.explosionRange = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("explosionPower")) {
                this.explosionPower = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("fireRange")) {
                this.fireRange = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("firePower")) {
                this.firePower = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("canBePlaced")) {
                this.canBePlaced = trim2.trim().equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("CanBeReused")) {
                this.canBeReused = trim2.trim().equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("canBeRemote")) {
                this.canBeRemote = trim2.trim().equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("remoteController")) {
                this.remoteController = trim2.trim().equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("smokeRange")) {
                this.smokeRange = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("noiseRange")) {
                this.noiseRange = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("noiseDuration")) {
                this.noiseDuration = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("extraDamage")) {
                this.extraDamage = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("TwoWay")) {
                this.twoWay = Boolean.parseBoolean(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("TransmitRange")) {
                this.transmitRange = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("MicRange")) {
                this.micRange = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("BaseVolumeRange")) {
                this.baseVolumeRange = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("IsPortable")) {
                this.isPortable = Boolean.parseBoolean(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("IsTelevision")) {
                this.isTelevision = Boolean.parseBoolean(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("MinChannel")) {
                this.minChannel = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("MaxChannel")) {
                this.maxChannel = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("UsesBattery")) {
                this.usesBattery = Boolean.parseBoolean(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("IsHighTier")) {
                this.isHighTier = Boolean.parseBoolean(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("WorldObjectSprite")) {
                this.worldObjectSprite = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("fluReduction")) {
                this.fluReduction = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("ReduceFoodSickness")) {
                this.ReduceFoodSickness = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("painReduction")) {
                this.painReduction = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("ColorRed")) {
                this.colorRed = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("ColorGreen")) {
                this.colorGreen = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("ColorBlue")) {
                this.colorBlue = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("calories")) {
                this.calories = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("carbohydrates")) {
                this.carbohydrates = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("lipids")) {
                this.lipids = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("proteins")) {
                this.proteins = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("Packaged")) {
                this.packaged = trim2.trim().equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("CantBeFrozen")) {
                this.cantBeFrozen = trim2.trim().equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("EvolvedRecipeName")) {
                Translator.setDefaultItemEvolvedRecipeName(this.getFullName(), trim2);
                this.evolvedRecipeName = Translator.getItemEvolvedRecipeName(this.getFullName());
            }
            else if (trim.trim().equalsIgnoreCase("ReplaceOnRotten")) {
                this.ReplaceOnRotten = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("CantBeConsolided")) {
                this.cantBeConsolided = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("OnEat")) {
                this.onEat = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("KeepOnDeplete")) {
                this.keepOnDeplete = trim2.equalsIgnoreCase("true");
            }
            else if (trim.trim().equalsIgnoreCase("VehicleType")) {
                this.vehicleType = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("ChanceToFall")) {
                this.chanceToFall = Integer.parseInt(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("conditionLowerOffroad")) {
                this.conditionLowerOffroad = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("ConditionLowerStandard")) {
                this.conditionLowerNormal = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("wheelFriction")) {
                this.wheelFriction = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("suspensionDamping")) {
                this.suspensionDamping = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("suspensionCompression")) {
                this.suspensionCompression = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("engineLoudness")) {
                this.engineLoudness = Float.parseFloat(trim2);
            }
            else if (trim.trim().equalsIgnoreCase("attachmentType")) {
                this.attachmentType = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("makeUpType")) {
                this.makeUpType = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("consolidateOption")) {
                this.consolidateOption = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("fabricType")) {
                this.fabricType = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("TeachedRecipes")) {
                this.teachedRecipes = new ArrayList<String>();
                final String[] split8 = trim2.split(";");
                for (int n3 = 0; n3 < split8.length; ++n3) {
                    final String trim3 = split8[n3].trim();
                    this.teachedRecipes.add(trim3);
                    if (Translator.debug) {
                        Translator.getRecipeName(trim3);
                    }
                }
            }
            else if (trim.trim().equalsIgnoreCase("MountOn")) {
                this.mountOn = new ArrayList<String>();
                final String[] split9 = trim2.split(";");
                for (int n4 = 0; n4 < split9.length; ++n4) {
                    this.mountOn.add(split9[n4].trim());
                }
            }
            else if (trim.trim().equalsIgnoreCase("PartType")) {
                this.partType = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("ClothingItem")) {
                this.ClothingItem = trim2;
            }
            else if (trim.trim().equalsIgnoreCase("EvolvedRecipe")) {
                final String[] split10 = trim2.split(";");
                for (int n5 = 0; n5 < split10.length; ++n5) {
                    final String s4 = split10[n5];
                    int m = 0;
                    boolean b = false;
                    String anObject;
                    if (s4.contains(":")) {
                        anObject = s4.split(":")[0];
                        final String s5 = s4.split(":")[1];
                        if (s5.contains("|")) {
                            final String[] split11 = s5.split("\\|");
                            for (int n6 = 0; n6 < split11.length; ++n6) {
                                if ("Cooked".equals(split11[n6])) {
                                    b = true;
                                }
                            }
                            m = Integer.parseInt(split11[0]);
                        }
                        else {
                            m = Integer.parseInt(s4.split(":")[1]);
                        }
                    }
                    else {
                        anObject = s4;
                    }
                    final ItemRecipe itemRecipe = new ItemRecipe(this.name, this.module.getName(), m);
                    EvolvedRecipe e2 = null;
                    for (final EvolvedRecipe evolvedRecipe : ScriptManager.instance.ModuleMap.get("Base").EvolvedRecipeMap) {
                        if (evolvedRecipe.name.equals(anObject)) {
                            e2 = evolvedRecipe;
                            break;
                        }
                    }
                    itemRecipe.cooked = b;
                    if (e2 == null) {
                        e2 = new EvolvedRecipe(anObject);
                        ScriptManager.instance.ModuleMap.get("Base").EvolvedRecipeMap.add(e2);
                    }
                    e2.itemsList.put(this.name, itemRecipe);
                }
            }
            else if (trim.trim().equalsIgnoreCase("StaticModel")) {
                this.staticModel = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("worldStaticModel")) {
                this.worldStaticModel = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("primaryAnimMask")) {
                this.primaryAnimMask = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("secondaryAnimMask")) {
                this.secondaryAnimMask = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("primaryAnimMaskAttachment")) {
                this.primaryAnimMaskAttachment = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("secondaryAnimMaskAttachment")) {
                this.secondaryAnimMaskAttachment = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("replaceInSecondHand")) {
                this.replaceInSecondHand = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("replaceInPrimaryHand")) {
                this.replaceInPrimaryHand = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("replaceWhenUnequip")) {
                this.replaceWhenUnequip = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("EatType")) {
                this.eatType = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("IconsForTexture")) {
                this.IconsForTexture = new ArrayList<String>();
                final String[] split12 = trim2.split(";");
                for (int n7 = 0; n7 < split12.length; ++n7) {
                    this.IconsForTexture.add(split12[n7].trim());
                }
            }
            else if (trim.trim().equalsIgnoreCase("BloodLocation")) {
                this.bloodClothingType = new ArrayList<BloodClothingType>();
                final String[] split13 = trim2.split(";");
                for (int n8 = 0; n8 < split13.length; ++n8) {
                    this.bloodClothingType.add(BloodClothingType.fromString(split13[n8].trim()));
                }
            }
            else if (trim.trim().equalsIgnoreCase("MediaCategory")) {
                this.recordedMediaCat = trim2.trim();
            }
            else if (trim.trim().equalsIgnoreCase("AcceptMediaType")) {
                this.acceptMediaType = Byte.parseByte(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("NoTransmit")) {
                this.noTransmit = Boolean.parseBoolean(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("WorldRender")) {
                this.worldRender = Boolean.parseBoolean(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("CantEat")) {
                this.CantEat = Boolean.parseBoolean(trim2.trim());
            }
            else if (trim.trim().equalsIgnoreCase("OBSOLETE")) {
                this.OBSOLETE = trim2.trim().toLowerCase().equals("true");
            }
            else {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, trim.trim(), trim2.trim()));
                if (this.DefaultModData == null) {
                    this.DefaultModData = LuaManager.platform.newTable();
                }
                try {
                    this.DefaultModData.rawset((Object)trim.trim(), (Object)Double.parseDouble(trim2.trim()));
                }
                catch (Exception ex) {
                    this.DefaultModData.rawset((Object)trim.trim(), (Object)trim2);
                }
            }
        }
        catch (Exception ex2) {
            throw new InvalidParameterException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s.trim(), this.name));
        }
    }
    
    public int getLevelSkillTrained() {
        return this.LvlSkillTrained;
    }
    
    public int getNumLevelsTrained() {
        return this.NumLevelsTrained;
    }
    
    public int getMaxLevelTrained() {
        if (this.LvlSkillTrained == -1) {
            return -1;
        }
        return this.LvlSkillTrained + this.NumLevelsTrained;
    }
    
    public List<String> getTeachedRecipes() {
        return this.teachedRecipes;
    }
    
    public float getTemperature() {
        return this.Temperature;
    }
    
    public void setTemperature(final float temperature) {
        this.Temperature = temperature;
    }
    
    public boolean isConditionAffectsCapacity() {
        return this.ConditionAffectsCapacity;
    }
    
    public int getChanceToFall() {
        return this.chanceToFall;
    }
    
    public float getInsulation() {
        return this.insulation;
    }
    
    public void setInsulation(final float insulation) {
        this.insulation = insulation;
    }
    
    public float getWindresist() {
        return this.windresist;
    }
    
    public void setWindresist(final float windresist) {
        this.windresist = windresist;
    }
    
    public float getWaterresist() {
        return this.waterresist;
    }
    
    public void setWaterresist(final float waterresist) {
        this.waterresist = waterresist;
    }
    
    public boolean getObsolete() {
        return this.OBSOLETE;
    }
    
    public String getAcceptItemFunction() {
        return this.AcceptItemFunction;
    }
    
    public ArrayList<BloodClothingType> getBloodClothingType() {
        return this.bloodClothingType;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lzombie/scripting/objects/Item$Type;)Ljava/lang/String;, this.getClass().getSimpleName(), (this.module != null) ? this.module.name : "null", this.name, this.type);
    }
    
    public String getReplaceWhenUnequip() {
        return this.replaceWhenUnequip;
    }
    
    public void resolveItemTypes() {
        this.AmmoType = ScriptManager.instance.resolveItemType(this.module, this.AmmoType);
        this.magazineType = ScriptManager.instance.resolveItemType(this.module, this.magazineType);
        if (this.RequireInHandOrInventory != null) {
            for (int i = 0; i < this.RequireInHandOrInventory.size(); ++i) {
                this.RequireInHandOrInventory.set(i, ScriptManager.instance.resolveItemType(this.module, this.RequireInHandOrInventory.get(i)));
            }
        }
    }
    
    public short getRegistry_id() {
        return this.registry_id;
    }
    
    public void setRegistry_id(final short registry_id) {
        if (this.registry_id != -1) {
            WorldDictionary.DebugPrintItem(registry_id);
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.getFullName() != null) ? this.getFullName() : "unknown"));
        }
        this.registry_id = registry_id;
    }
    
    public String getModID() {
        return this.modID;
    }
    
    public boolean getExistsAsVanilla() {
        return this.existsAsVanilla;
    }
    
    public String getFileAbsPath() {
        return this.fileAbsPath;
    }
    
    public void setModID(final String modID) {
        if (GameClient.bClient) {
            if (this.modID == null) {
                this.modID = modID;
            }
            else if (!modID.equals(this.modID) && Core.bDebug) {
                WorldDictionary.DebugPrintItem(this);
                throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (modID != null) ? modID : "null"));
            }
        }
    }
    
    public String getRecordedMediaCat() {
        return this.recordedMediaCat;
    }
    
    public Boolean isWorldRender() {
        return this.worldRender;
    }
    
    public Boolean isCantEat() {
        return this.CantEat;
    }
    
    static {
        Item.NetIDToItem = new HashMap<Integer, String>();
        Item.NetItemToID = new HashMap<String, Integer>();
        Item.IDMax = 0;
    }
    
    public enum Type
    {
        Normal, 
        Weapon, 
        Food, 
        Literature, 
        Drainable, 
        Clothing, 
        Container, 
        WeaponPart, 
        Key, 
        KeyRing, 
        Moveable, 
        Radio, 
        AlarmClock, 
        AlarmClockClothing, 
        Map;
        
        private static /* synthetic */ Type[] $values() {
            return new Type[] { Type.Normal, Type.Weapon, Type.Food, Type.Literature, Type.Drainable, Type.Clothing, Type.Container, Type.WeaponPart, Type.Key, Type.KeyRing, Type.Moveable, Type.Radio, Type.AlarmClock, Type.AlarmClockClothing, Type.Map };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
