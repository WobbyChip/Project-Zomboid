// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.config.EnumConfigOption;
import zombie.config.IntegerConfigOption;
import zombie.config.StringConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.core.Translator;
import zombie.config.BooleanConfigOption;
import java.util.Collection;
import zombie.sandbox.CustomStringSandboxOption;
import zombie.sandbox.CustomIntegerSandboxOption;
import zombie.sandbox.CustomEnumSandboxOption;
import zombie.sandbox.CustomDoubleSandboxOption;
import zombie.util.Type;
import zombie.sandbox.CustomBooleanSandboxOption;
import zombie.sandbox.CustomSandboxOption;
import zombie.network.GameClient;
import se.krka.kahlua.vm.KahluaTableIterator;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import zombie.network.GameServer;
import zombie.iso.SliceY;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import zombie.core.logger.ExceptionLogger;
import java.io.File;
import zombie.network.ServerSettingsManager;
import java.util.Iterator;
import zombie.config.ConfigOption;
import java.util.HashSet;
import zombie.config.ConfigFile;
import zombie.debug.DebugLog;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.Rand;
import zombie.core.Core;
import zombie.Lua.LuaManager;
import se.krka.kahlua.vm.KahluaTable;
import zombie.sandbox.CustomSandboxOptions;
import java.util.HashMap;
import java.util.ArrayList;

public final class SandboxOptions
{
    public static final SandboxOptions instance;
    public int Speed;
    public final EnumSandboxOption Zombies;
    public final EnumSandboxOption Distribution;
    public final EnumSandboxOption DayLength;
    public final EnumSandboxOption StartYear;
    public final EnumSandboxOption StartMonth;
    public final EnumSandboxOption StartDay;
    public final EnumSandboxOption StartTime;
    public final EnumSandboxOption WaterShut;
    public final EnumSandboxOption ElecShut;
    public final IntegerSandboxOption WaterShutModifier;
    public final IntegerSandboxOption ElecShutModifier;
    public final EnumSandboxOption FoodLoot;
    public final EnumSandboxOption LiteratureLoot;
    public final EnumSandboxOption MedicalLoot;
    public final EnumSandboxOption SurvivalGearsLoot;
    public final EnumSandboxOption CannedFoodLoot;
    public final EnumSandboxOption WeaponLoot;
    public final EnumSandboxOption RangedWeaponLoot;
    public final EnumSandboxOption AmmoLoot;
    public final EnumSandboxOption MechanicsLoot;
    public final EnumSandboxOption OtherLoot;
    public final EnumSandboxOption Temperature;
    public final EnumSandboxOption Rain;
    public final EnumSandboxOption ErosionSpeed;
    public final IntegerSandboxOption ErosionDays;
    public final DoubleSandboxOption XpMultiplier;
    public final EnumSandboxOption Farming;
    public final EnumSandboxOption CompostTime;
    public final EnumSandboxOption StatsDecrease;
    public final EnumSandboxOption NatureAbundance;
    public final EnumSandboxOption Alarm;
    public final EnumSandboxOption LockedHouses;
    public final BooleanSandboxOption StarterKit;
    public final BooleanSandboxOption Nutrition;
    public final EnumSandboxOption FoodRotSpeed;
    public final EnumSandboxOption FridgeFactor;
    public final EnumSandboxOption LootRespawn;
    public final IntegerSandboxOption SeenHoursPreventLootRespawn;
    public final StringSandboxOption WorldItemRemovalList;
    public final DoubleSandboxOption HoursForWorldItemRemoval;
    public final BooleanSandboxOption ItemRemovalListBlacklistToggle;
    public final EnumSandboxOption TimeSinceApo;
    public final EnumSandboxOption PlantResilience;
    public final EnumSandboxOption PlantAbundance;
    public final EnumSandboxOption EndRegen;
    public final EnumSandboxOption Helicopter;
    public final EnumSandboxOption MetaEvent;
    public final EnumSandboxOption SleepingEvent;
    public final DoubleSandboxOption GeneratorFuelConsumption;
    public final EnumSandboxOption GeneratorSpawning;
    public final EnumSandboxOption SurvivorHouseChance;
    public final EnumSandboxOption AnnotatedMapChance;
    public final IntegerSandboxOption CharacterFreePoints;
    public final EnumSandboxOption ConstructionBonusPoints;
    public final EnumSandboxOption NightDarkness;
    public final BooleanSandboxOption BoneFracture;
    public final EnumSandboxOption InjurySeverity;
    public final DoubleSandboxOption HoursForCorpseRemoval;
    public final EnumSandboxOption DecayingCorpseHealthImpact;
    public final EnumSandboxOption BloodLevel;
    public final EnumSandboxOption ClothingDegradation;
    public final BooleanSandboxOption FireSpread;
    public final IntegerSandboxOption DaysForRottenFoodRemoval;
    public final BooleanSandboxOption AllowExteriorGenerator;
    public final EnumSandboxOption MaxFogIntensity;
    public final EnumSandboxOption MaxRainFxIntensity;
    public final BooleanSandboxOption EnableSnowOnGround;
    public final BooleanSandboxOption AttackBlockMovements;
    public final EnumSandboxOption VehicleStoryChance;
    public final EnumSandboxOption ZoneStoryChance;
    public final BooleanSandboxOption AllClothesUnlocked;
    public final BooleanSandboxOption EnableVehicles;
    public final EnumSandboxOption CarSpawnRate;
    public final DoubleSandboxOption ZombieAttractionMultiplier;
    public final BooleanSandboxOption VehicleEasyUse;
    public final EnumSandboxOption InitialGas;
    public final EnumSandboxOption FuelStationGas;
    public final EnumSandboxOption LockedCar;
    public final DoubleSandboxOption CarGasConsumption;
    public final EnumSandboxOption CarGeneralCondition;
    public final EnumSandboxOption CarDamageOnImpact;
    public final EnumSandboxOption DamageToPlayerFromHitByACar;
    public final BooleanSandboxOption TrafficJam;
    public final EnumSandboxOption CarAlarm;
    public final BooleanSandboxOption PlayerDamageFromCrash;
    public final DoubleSandboxOption SirenShutoffHours;
    public final EnumSandboxOption ChanceHasGas;
    public final EnumSandboxOption RecentlySurvivorVehicles;
    public final BooleanSandboxOption MultiHitZombies;
    public final EnumSandboxOption RearVulnerability;
    protected final ArrayList<SandboxOption> options;
    protected final HashMap<String, SandboxOption> optionByName;
    public final Map Map;
    public final ZombieLore Lore;
    public final ZombieConfig zombieConfig;
    public final int FIRST_YEAR = 1993;
    private final int SANDBOX_VERSION = 4;
    private final ArrayList<SandboxOption> m_customOptions;
    
    public SandboxOptions() {
        this.Speed = 3;
        this.options = new ArrayList<SandboxOption>();
        this.optionByName = new HashMap<String, SandboxOption>();
        this.Map = new Map();
        this.Lore = new ZombieLore();
        this.zombieConfig = new ZombieConfig();
        this.m_customOptions = new ArrayList<SandboxOption>();
        this.Zombies = (EnumSandboxOption)this.newEnumOption("Zombies", 6, 4).setTranslation("ZombieCount");
        this.Distribution = (EnumSandboxOption)this.newEnumOption("Distribution", 2, 1).setTranslation("ZombieDistribution");
        this.DayLength = this.newEnumOption("DayLength", 26, 2);
        this.StartYear = this.newEnumOption("StartYear", 100, 1);
        this.StartMonth = this.newEnumOption("StartMonth", 12, 7);
        this.StartDay = this.newEnumOption("StartDay", 31, 23);
        this.StartTime = this.newEnumOption("StartTime", 9, 2);
        this.WaterShut = this.newEnumOption("WaterShut", 8, 2).setValueTranslation("Shutoff");
        this.ElecShut = this.newEnumOption("ElecShut", 8, 2).setValueTranslation("Shutoff");
        this.WaterShutModifier = (IntegerSandboxOption)this.newIntegerOption("WaterShutModifier", -1, Integer.MAX_VALUE, 14).setTranslation("WaterShut");
        this.ElecShutModifier = (IntegerSandboxOption)this.newIntegerOption("ElecShutModifier", -1, Integer.MAX_VALUE, 14).setTranslation("ElecShut");
        this.FoodLoot = (EnumSandboxOption)this.newEnumOption("FoodLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootFood");
        this.CannedFoodLoot = (EnumSandboxOption)this.newEnumOption("CannedFoodLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootCannedFood");
        this.LiteratureLoot = (EnumSandboxOption)this.newEnumOption("LiteratureLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootLiterature");
        this.SurvivalGearsLoot = (EnumSandboxOption)this.newEnumOption("SurvivalGearsLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootSurvivalGears");
        this.MedicalLoot = (EnumSandboxOption)this.newEnumOption("MedicalLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootMedical");
        this.WeaponLoot = (EnumSandboxOption)this.newEnumOption("WeaponLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootWeapon");
        this.RangedWeaponLoot = (EnumSandboxOption)this.newEnumOption("RangedWeaponLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootRangedWeapon");
        this.AmmoLoot = (EnumSandboxOption)this.newEnumOption("AmmoLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootAmmo");
        this.MechanicsLoot = (EnumSandboxOption)this.newEnumOption("MechanicsLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootMechanics");
        this.OtherLoot = (EnumSandboxOption)this.newEnumOption("OtherLoot", 5, 2).setValueTranslation("Rarity").setTranslation("LootOther");
        this.Temperature = (EnumSandboxOption)this.newEnumOption("Temperature", 5, 3).setTranslation("WorldTemperature");
        this.Rain = (EnumSandboxOption)this.newEnumOption("Rain", 5, 3).setTranslation("RainAmount");
        this.ErosionSpeed = this.newEnumOption("ErosionSpeed", 5, 3);
        this.ErosionDays = this.newIntegerOption("ErosionDays", -1, 36500, 0);
        this.XpMultiplier = this.newDoubleOption("XpMultiplier", 0.001, 1000.0, 1.0);
        this.ZombieAttractionMultiplier = this.newDoubleOption("ZombieAttractionMultiplier", 0.0, 100.0, 1.0);
        this.VehicleEasyUse = this.newBooleanOption("VehicleEasyUse", false);
        this.Farming = (EnumSandboxOption)this.newEnumOption("Farming", 5, 3).setTranslation("FarmingSpeed");
        this.CompostTime = this.newEnumOption("CompostTime", 8, 2);
        this.StatsDecrease = (EnumSandboxOption)this.newEnumOption("StatsDecrease", 5, 3).setTranslation("StatDecrease");
        this.NatureAbundance = (EnumSandboxOption)this.newEnumOption("NatureAbundance", 5, 3).setTranslation("NatureAmount");
        this.Alarm = (EnumSandboxOption)this.newEnumOption("Alarm", 6, 4).setTranslation("HouseAlarmFrequency");
        this.LockedHouses = (EnumSandboxOption)this.newEnumOption("LockedHouses", 6, 4).setTranslation("LockedHouseFrequency");
        this.StarterKit = this.newBooleanOption("StarterKit", false);
        this.Nutrition = this.newBooleanOption("Nutrition", false);
        this.FoodRotSpeed = (EnumSandboxOption)this.newEnumOption("FoodRotSpeed", 5, 3).setTranslation("FoodSpoil");
        this.FridgeFactor = (EnumSandboxOption)this.newEnumOption("FridgeFactor", 5, 3).setTranslation("FridgeEffect");
        this.LootRespawn = this.newEnumOption("LootRespawn", 5, 1).setValueTranslation("Respawn");
        this.SeenHoursPreventLootRespawn = this.newIntegerOption("SeenHoursPreventLootRespawn", 0, Integer.MAX_VALUE, 0);
        this.WorldItemRemovalList = this.newStringOption("WorldItemRemovalList", "Base.Vest,Base.Shirt,Base.Blouse,Base.Skirt,Base.Shoes,Base.Hat,Base.Glasses");
        this.HoursForWorldItemRemoval = this.newDoubleOption("HoursForWorldItemRemoval", 0.0, 3.4028234663852886E38, 24.0);
        this.ItemRemovalListBlacklistToggle = this.newBooleanOption("ItemRemovalListBlacklistToggle", false);
        this.TimeSinceApo = this.newEnumOption("TimeSinceApo", 13, 1);
        this.PlantResilience = this.newEnumOption("PlantResilience", 5, 3);
        this.PlantAbundance = this.newEnumOption("PlantAbundance", 5, 3).setValueTranslation("NatureAmount");
        this.EndRegen = (EnumSandboxOption)this.newEnumOption("EndRegen", 5, 3).setTranslation("EnduranceRegen");
        this.Helicopter = this.newEnumOption("Helicopter", 4, 2).setValueTranslation("HelicopterFreq");
        this.MetaEvent = this.newEnumOption("MetaEvent", 3, 2).setValueTranslation("MetaEventFreq");
        this.SleepingEvent = this.newEnumOption("SleepingEvent", 3, 1).setValueTranslation("MetaEventFreq");
        this.GeneratorSpawning = this.newEnumOption("GeneratorSpawning", 5, 3);
        this.GeneratorFuelConsumption = this.newDoubleOption("GeneratorFuelConsumption", 0.0, 100.0, 1.0);
        this.SurvivorHouseChance = this.newEnumOption("SurvivorHouseChance", 6, 3);
        this.VehicleStoryChance = this.newEnumOption("VehicleStoryChance", 6, 3).setValueTranslation("SurvivorHouseChance");
        this.ZoneStoryChance = this.newEnumOption("ZoneStoryChance", 6, 3).setValueTranslation("SurvivorHouseChance");
        this.AnnotatedMapChance = this.newEnumOption("AnnotatedMapChance", 6, 4);
        this.CharacterFreePoints = this.newIntegerOption("CharacterFreePoints", -100, 100, 0);
        this.ConstructionBonusPoints = this.newEnumOption("ConstructionBonusPoints", 5, 3);
        this.NightDarkness = this.newEnumOption("NightDarkness", 4, 3);
        this.InjurySeverity = this.newEnumOption("InjurySeverity", 3, 2);
        this.BoneFracture = this.newBooleanOption("BoneFracture", true);
        this.HoursForCorpseRemoval = this.newDoubleOption("HoursForCorpseRemoval", -1.0, 3.4028234663852886E38, -1.0);
        this.DecayingCorpseHealthImpact = this.newEnumOption("DecayingCorpseHealthImpact", 4, 3);
        this.BloodLevel = this.newEnumOption("BloodLevel", 5, 3);
        this.ClothingDegradation = this.newEnumOption("ClothingDegradation", 4, 3);
        this.FireSpread = this.newBooleanOption("FireSpread", true);
        this.DaysForRottenFoodRemoval = this.newIntegerOption("DaysForRottenFoodRemoval", -1, Integer.MAX_VALUE, -1);
        this.AllowExteriorGenerator = this.newBooleanOption("AllowExteriorGenerator", true);
        this.MaxFogIntensity = this.newEnumOption("MaxFogIntensity", 3, 1);
        this.MaxRainFxIntensity = this.newEnumOption("MaxRainFxIntensity", 3, 1);
        this.EnableSnowOnGround = this.newBooleanOption("EnableSnowOnGround", true);
        this.MultiHitZombies = this.newBooleanOption("MultiHitZombies", false);
        this.RearVulnerability = this.newEnumOption("RearVulnerability", 3, 3);
        this.AttackBlockMovements = this.newBooleanOption("AttackBlockMovements", true);
        this.AllClothesUnlocked = this.newBooleanOption("AllClothesUnlocked", false);
        this.CarSpawnRate = this.newEnumOption("CarSpawnRate", 5, 4);
        this.ChanceHasGas = this.newEnumOption("ChanceHasGas", 3, 2);
        this.InitialGas = this.newEnumOption("InitialGas", 6, 3);
        this.FuelStationGas = this.newEnumOption("FuelStationGas", 8, 4);
        this.CarGasConsumption = this.newDoubleOption("CarGasConsumption", 0.0, 100.0, 1.0);
        this.LockedCar = this.newEnumOption("LockedCar", 6, 4);
        this.CarGeneralCondition = this.newEnumOption("CarGeneralCondition", 5, 3);
        this.CarDamageOnImpact = this.newEnumOption("CarDamageOnImpact", 5, 3);
        this.DamageToPlayerFromHitByACar = this.newEnumOption("DamageToPlayerFromHitByACar", 5, 1);
        this.TrafficJam = this.newBooleanOption("TrafficJam", true);
        this.CarAlarm = (EnumSandboxOption)this.newEnumOption("CarAlarm", 6, 4).setTranslation("CarAlarmFrequency");
        this.PlayerDamageFromCrash = this.newBooleanOption("PlayerDamageFromCrash", true);
        this.SirenShutoffHours = this.newDoubleOption("SirenShutoffHours", 0.0, 168.0, 0.0);
        this.RecentlySurvivorVehicles = this.newEnumOption("RecentlySurvivorVehicles", 3, 2);
        this.EnableVehicles = this.newBooleanOption("EnableVehicles", true);
        CustomSandboxOptions.instance.initInstance(this);
        this.loadGameFile("Apocalypse");
        this.setDefaultsToCurrentValues();
    }
    
    public static SandboxOptions getInstance() {
        return SandboxOptions.instance;
    }
    
    public void toLua() {
        final KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget((Object)"SandboxVars");
        for (int i = 0; i < this.options.size(); ++i) {
            this.options.get(i).toTable(kahluaTable);
        }
    }
    
    public void updateFromLua() {
        if (Core.GameMode.equals("LastStand")) {
            GameTime.instance.multiplierBias = 1.2f;
        }
        final KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget((Object)"SandboxVars");
        for (int i = 0; i < this.options.size(); ++i) {
            this.options.get(i).fromTable(kahluaTable);
        }
        switch (this.Speed) {
            case 1: {
                GameTime.instance.multiplierBias = 0.8f;
                break;
            }
            case 2: {
                GameTime.instance.multiplierBias = 0.9f;
                break;
            }
            case 3: {
                GameTime.instance.multiplierBias = 1.0f;
                break;
            }
            case 4: {
                GameTime.instance.multiplierBias = 1.1f;
                break;
            }
            case 5: {
                GameTime.instance.multiplierBias = 1.2f;
                break;
            }
        }
        if (this.Zombies.getValue() == 1) {
            VirtualZombieManager.instance.MaxRealZombies = 400;
        }
        if (this.Zombies.getValue() == 2) {
            VirtualZombieManager.instance.MaxRealZombies = 350;
        }
        if (this.Zombies.getValue() == 3) {
            VirtualZombieManager.instance.MaxRealZombies = 300;
        }
        if (this.Zombies.getValue() == 4) {
            VirtualZombieManager.instance.MaxRealZombies = 200;
        }
        if (this.Zombies.getValue() == 5) {
            VirtualZombieManager.instance.MaxRealZombies = 100;
        }
        if (this.Zombies.getValue() == 6) {
            VirtualZombieManager.instance.MaxRealZombies = 0;
        }
        VirtualZombieManager.instance.MaxRealZombies = 1;
        this.applySettings();
    }
    
    public void initSandboxVars() {
        final KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget((Object)"SandboxVars");
        for (int i = 0; i < this.options.size(); ++i) {
            final SandboxOption sandboxOption = this.options.get(i);
            sandboxOption.fromTable(kahluaTable);
            sandboxOption.toTable(kahluaTable);
        }
    }
    
    public int randomWaterShut(final int n) {
        switch (n) {
            case 2: {
                return Rand.Next(0, 30);
            }
            case 3: {
                return Rand.Next(0, 60);
            }
            case 4: {
                return Rand.Next(0, 180);
            }
            case 5: {
                return Rand.Next(0, 360);
            }
            case 6: {
                return Rand.Next(0, 1800);
            }
            case 7: {
                return Rand.Next(60, 180);
            }
            case 8: {
                return Rand.Next(180, 360);
            }
            default: {
                return -1;
            }
        }
    }
    
    public int randomElectricityShut(final int n) {
        switch (n) {
            case 2: {
                return Rand.Next(14, 30);
            }
            case 3: {
                return Rand.Next(14, 60);
            }
            case 4: {
                return Rand.Next(14, 180);
            }
            case 5: {
                return Rand.Next(14, 360);
            }
            case 6: {
                return Rand.Next(14, 1800);
            }
            case 7: {
                return Rand.Next(60, 180);
            }
            case 8: {
                return Rand.Next(180, 360);
            }
            default: {
                return -1;
            }
        }
    }
    
    public int getTemperatureModifier() {
        return this.Temperature.getValue();
    }
    
    public int getRainModifier() {
        return this.Rain.getValue();
    }
    
    public int getErosionSpeed() {
        return this.ErosionSpeed.getValue();
    }
    
    public int getFoodLootModifier() {
        return this.FoodLoot.getValue();
    }
    
    public int getWeaponLootModifier() {
        return this.WeaponLoot.getValue();
    }
    
    public int getOtherLootModifier() {
        return this.OtherLoot.getValue();
    }
    
    public int getWaterShutModifier() {
        return this.WaterShutModifier.getValue();
    }
    
    public int getElecShutModifier() {
        return this.ElecShutModifier.getValue();
    }
    
    public int getTimeSinceApo() {
        return this.TimeSinceApo.getValue();
    }
    
    public double getEnduranceRegenMultiplier() {
        switch (this.EndRegen.getValue()) {
            case 1: {
                return 1.8;
            }
            case 2: {
                return 1.3;
            }
            case 4: {
                return 0.7;
            }
            case 5: {
                return 0.4;
            }
            default: {
                return 1.0;
            }
        }
    }
    
    public double getStatsDecreaseMultiplier() {
        switch (this.StatsDecrease.getValue()) {
            case 1: {
                return 2.0;
            }
            case 2: {
                return 1.6;
            }
            case 4: {
                return 0.8;
            }
            case 5: {
                return 0.65;
            }
            default: {
                return 1.0;
            }
        }
    }
    
    public int getDayLengthMinutes() {
        switch (this.DayLength.getValue()) {
            case 1: {
                return 15;
            }
            case 2: {
                return 30;
            }
            default: {
                return (this.DayLength.getValue() - 2) * 60;
            }
        }
    }
    
    public int getDayLengthMinutesDefault() {
        switch (this.DayLength.getDefaultValue()) {
            case 1: {
                return 15;
            }
            case 2: {
                return 30;
            }
            default: {
                return (this.DayLength.getDefaultValue() - 2) * 60;
            }
        }
    }
    
    public int getCompostHours() {
        switch (this.CompostTime.getValue()) {
            case 1: {
                return 168;
            }
            case 2: {
                return 336;
            }
            case 3: {
                return 504;
            }
            case 4: {
                return 672;
            }
            case 5: {
                return 1008;
            }
            case 6: {
                return 1344;
            }
            case 7: {
                return 1680;
            }
            case 8: {
                return 2016;
            }
            default: {
                return 336;
            }
        }
    }
    
    public void applySettings() {
        GameTime.instance.setStartYear(this.getFirstYear() + this.StartYear.getValue() - 1);
        GameTime.instance.setStartMonth(this.StartMonth.getValue() - 1);
        GameTime.instance.setStartDay(this.StartDay.getValue() - 1);
        GameTime.instance.setMinutesPerDay((float)this.getDayLengthMinutes());
        if (this.StartTime.getValue() == 1) {
            GameTime.instance.setStartTimeOfDay(7.0f);
        }
        else if (this.StartTime.getValue() == 2) {
            GameTime.instance.setStartTimeOfDay(9.0f);
        }
        else if (this.StartTime.getValue() == 3) {
            GameTime.instance.setStartTimeOfDay(12.0f);
        }
        else if (this.StartTime.getValue() == 4) {
            GameTime.instance.setStartTimeOfDay(14.0f);
        }
        else if (this.StartTime.getValue() == 5) {
            GameTime.instance.setStartTimeOfDay(17.0f);
        }
        else if (this.StartTime.getValue() == 6) {
            GameTime.instance.setStartTimeOfDay(21.0f);
        }
        else if (this.StartTime.getValue() == 7) {
            GameTime.instance.setStartTimeOfDay(0.0f);
        }
        else if (this.StartTime.getValue() == 8) {
            GameTime.instance.setStartTimeOfDay(2.0f);
        }
        else if (this.StartTime.getValue() == 9) {
            GameTime.instance.setStartTimeOfDay(5.0f);
        }
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.put((byte)83);
        byteBuffer.put((byte)65);
        byteBuffer.put((byte)78);
        byteBuffer.put((byte)68);
        byteBuffer.putInt(186);
        byteBuffer.putInt(4);
        byteBuffer.putInt(this.options.size());
        for (int i = 0; i < this.options.size(); ++i) {
            final SandboxOption sandboxOption = this.options.get(i);
            GameWindow.WriteStringUTF(byteBuffer, sandboxOption.asConfigOption().getName());
            GameWindow.WriteStringUTF(byteBuffer, sandboxOption.asConfigOption().getValueAsString());
        }
    }
    
    public void load(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.mark();
        final byte value = byteBuffer.get();
        final byte value2 = byteBuffer.get();
        final byte value3 = byteBuffer.get();
        final byte value4 = byteBuffer.get();
        int int1;
        if (value == 83 && value2 == 65 && value3 == 78 && value4 == 68) {
            int1 = byteBuffer.getInt();
        }
        else {
            int1 = 41;
            byteBuffer.reset();
        }
        if (int1 >= 88) {
            int int2 = 2;
            if (int1 >= 131) {
                int2 = byteBuffer.getInt();
            }
            for (int int3 = byteBuffer.getInt(), i = 0; i < int3; ++i) {
                final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
                final String readStringUTF2 = GameWindow.ReadStringUTF(byteBuffer);
                final String upgradeOptionName = this.upgradeOptionName(readStringUTF, int2);
                final String upgradeOptionValue = this.upgradeOptionValue(upgradeOptionName, readStringUTF2, int2);
                final SandboxOption sandboxOption = this.optionByName.get(upgradeOptionName);
                if (sandboxOption == null) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, upgradeOptionName));
                }
                else {
                    sandboxOption.asConfigOption().parse(upgradeOptionValue);
                }
            }
            if (int1 < 157) {
                SandboxOptions.instance.CannedFoodLoot.setValue(SandboxOptions.instance.FoodLoot.getValue());
                SandboxOptions.instance.AmmoLoot.setValue(SandboxOptions.instance.WeaponLoot.getValue());
                SandboxOptions.instance.RangedWeaponLoot.setValue(SandboxOptions.instance.WeaponLoot.getValue());
                SandboxOptions.instance.MedicalLoot.setValue(SandboxOptions.instance.OtherLoot.getValue());
                SandboxOptions.instance.LiteratureLoot.setValue(SandboxOptions.instance.OtherLoot.getValue());
                SandboxOptions.instance.SurvivalGearsLoot.setValue(SandboxOptions.instance.OtherLoot.getValue());
                SandboxOptions.instance.MechanicsLoot.setValue(SandboxOptions.instance.OtherLoot.getValue());
            }
        }
    }
    
    public int getFirstYear() {
        return 1993;
    }
    
    private static String[] parseName(final String s) {
        final String[] array = { null, s };
        if (s.contains(".")) {
            final String[] split = s.split("\\.");
            if (split.length == 2) {
                array[0] = split[0];
                array[1] = split[1];
            }
        }
        return array;
    }
    
    private BooleanSandboxOption newBooleanOption(final String s, final boolean b) {
        return new BooleanSandboxOption(this, s, b);
    }
    
    private DoubleSandboxOption newDoubleOption(final String s, final double n, final double n2, final double n3) {
        return new DoubleSandboxOption(this, s, n, n2, n3);
    }
    
    private EnumSandboxOption newEnumOption(final String s, final int n, final int n2) {
        return new EnumSandboxOption(this, s, n, n2);
    }
    
    private IntegerSandboxOption newIntegerOption(final String s, final int n, final int n2, final int n3) {
        return new IntegerSandboxOption(this, s, n, n2, n3);
    }
    
    private StringSandboxOption newStringOption(final String s, final String s2) {
        return new StringSandboxOption(this, s, s2);
    }
    
    protected SandboxOptions addOption(final SandboxOption sandboxOption) {
        this.options.add(sandboxOption);
        this.optionByName.put(sandboxOption.asConfigOption().getName(), sandboxOption);
        return this;
    }
    
    public int getNumOptions() {
        return this.options.size();
    }
    
    public SandboxOption getOptionByIndex(final int index) {
        return this.options.get(index);
    }
    
    public SandboxOption getOptionByName(final String key) {
        return this.optionByName.get(key);
    }
    
    public void set(final String key, final Object valueFromObject) {
        if (key == null || valueFromObject == null) {
            throw new IllegalArgumentException();
        }
        final SandboxOption sandboxOption = this.optionByName.get(key);
        if (sandboxOption == null) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, key));
        }
        sandboxOption.asConfigOption().setValueFromObject(valueFromObject);
    }
    
    public void copyValuesFrom(final SandboxOptions sandboxOptions) {
        if (sandboxOptions == null) {
            throw new NullPointerException();
        }
        for (int i = 0; i < this.options.size(); ++i) {
            this.options.get(i).asConfigOption().setValueFromObject(sandboxOptions.options.get(i).asConfigOption().getValueAsObject());
        }
    }
    
    public void resetToDefault() {
        for (int i = 0; i < this.options.size(); ++i) {
            this.options.get(i).asConfigOption().resetToDefault();
        }
    }
    
    public void setDefaultsToCurrentValues() {
        for (int i = 0; i < this.options.size(); ++i) {
            this.options.get(i).asConfigOption().setDefaultToCurrentValue();
        }
    }
    
    public SandboxOptions newCopy() {
        final SandboxOptions sandboxOptions = new SandboxOptions();
        sandboxOptions.copyValuesFrom(this);
        return sandboxOptions;
    }
    
    public static boolean isValidPresetName(final String s) {
        return s != null && !s.isEmpty() && !s.contains("/") && !s.contains("\\") && !s.contains(":") && !s.contains(";") && !s.contains("\"") && !s.contains(".");
    }
    
    private boolean readTextFile(final String s, final boolean b) {
        final ConfigFile configFile = new ConfigFile();
        if (configFile.read(s)) {
            final int version = configFile.getVersion();
            HashSet<String> set = null;
            if (b && version == 1) {
                set = new HashSet<String>();
                for (int i = 0; i < this.options.size(); ++i) {
                    if ("ZombieLore".equals(this.options.get(i).getTableName())) {
                        set.add(this.options.get(i).getShortName());
                    }
                }
            }
            for (int j = 0; j < configFile.getOptions().size(); ++j) {
                final ConfigOption configOption = configFile.getOptions().get(j);
                String name = configOption.getName();
                final String valueAsString = configOption.getValueAsString();
                if (set != null && set.contains(name)) {
                    name = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name);
                }
                if (b && version == 1) {
                    if ("WaterShutModifier".equals(name)) {
                        name = "WaterShut";
                    }
                    else if ("ElecShutModifier".equals(name)) {
                        name = "ElecShut";
                    }
                }
                final String upgradeOptionName = this.upgradeOptionName(name, version);
                final String upgradeOptionValue = this.upgradeOptionValue(upgradeOptionName, valueAsString, version);
                final SandboxOption sandboxOption = this.optionByName.get(upgradeOptionName);
                if (sandboxOption != null) {
                    sandboxOption.asConfigOption().parse(upgradeOptionValue);
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean writeTextFile(final String s, final int n) {
        final ConfigFile configFile = new ConfigFile();
        final ArrayList<ConfigOption> list = new ArrayList<ConfigOption>();
        final Iterator<SandboxOption> iterator = this.options.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().asConfigOption());
        }
        return configFile.write(s, n, list);
    }
    
    public boolean loadServerTextFile(final String s) {
        return this.readTextFile(ServerSettingsManager.instance.getNameInSettingsFolder(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s)), false);
    }
    
    public boolean loadServerLuaFile(final String s) {
        final boolean luaFile = this.readLuaFile(ServerSettingsManager.instance.getNameInSettingsFolder(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s)));
        if (this.Lore.Speed.getValue() == 1) {
            this.Lore.Speed.setValue(2);
        }
        return luaFile;
    }
    
    public boolean saveServerLuaFile(final String s) {
        return this.writeLuaFile(ServerSettingsManager.instance.getNameInSettingsFolder(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s)), false);
    }
    
    public boolean loadPresetFile(final String s) {
        return this.readTextFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LuaManager.getSandboxCacheDir(), File.separator, s), true);
    }
    
    public boolean savePresetFile(final String s) {
        return isValidPresetName(s) && this.writeTextFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, LuaManager.getSandboxCacheDir(), File.separator, s), 4);
    }
    
    public boolean loadGameFile(final String s) {
        final File mediaFile = ZomboidFileSystem.instance.getMediaFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        if (!mediaFile.exists()) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        try {
            LuaManager.loaded.remove(mediaFile.getAbsolutePath().replace("\\", "/"));
            final Object runLua = LuaManager.RunLua(mediaFile.getAbsolutePath());
            if (runLua instanceof KahluaTable) {
                for (int i = 0; i < this.options.size(); ++i) {
                    this.options.get(i).fromTable((KahluaTable)runLua);
                }
                return true;
            }
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, mediaFile.getName()));
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            return false;
        }
    }
    
    public boolean saveGameFile(final String s) {
        return Core.bDebug && this.writeLuaFile(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s), true);
    }
    
    private void saveCurrentGameBinFile() {
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("map_sand.bin");
        try {
            final FileOutputStream out = new FileOutputStream(fileInCurrentSave);
            try {
                final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);
                try {
                    synchronized (SliceY.SliceBufferLock) {
                        SliceY.SliceBuffer.clear();
                        this.save(SliceY.SliceBuffer);
                        bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
                    }
                    bufferedOutputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedOutputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                out.close();
            }
            catch (Throwable t2) {
                try {
                    out.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public void handleOldZombiesFile1() {
        if (GameServer.bServer) {
            return;
        }
        final String fileNameInCurrentSave = ZomboidFileSystem.instance.getFileNameInCurrentSave("zombies.ini");
        final ConfigFile configFile = new ConfigFile();
        if (configFile.read(fileNameInCurrentSave)) {
            for (int i = 0; i < configFile.getOptions().size(); ++i) {
                final ConfigOption configOption = configFile.getOptions().get(i);
                final SandboxOption sandboxOption = this.optionByName.get(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, configOption.getName()));
                if (sandboxOption != null) {
                    sandboxOption.asConfigOption().parse(configOption.getValueAsString());
                }
            }
        }
    }
    
    public void handleOldZombiesFile2() {
        if (GameServer.bServer) {
            return;
        }
        final File file = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("zombies.ini"));
        if (!file.exists()) {
            return;
        }
        try {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath()));
            file.delete();
            this.saveCurrentGameBinFile();
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public void handleOldServerZombiesFile() {
        if (!GameServer.bServer) {
            return;
        }
        if (this.loadServerZombiesFile(GameServer.ServerName)) {
            final String nameInSettingsFolder = ServerSettingsManager.instance.getNameInSettingsFolder(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, GameServer.ServerName));
            try {
                final File file = new File(nameInSettingsFolder);
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath()));
                file.delete();
                this.saveServerLuaFile(GameServer.ServerName);
            }
            catch (Exception ex) {
                ExceptionLogger.logException(ex);
            }
        }
    }
    
    public boolean loadServerZombiesFile(final String s) {
        final String nameInSettingsFolder = ServerSettingsManager.instance.getNameInSettingsFolder(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        final ConfigFile configFile = new ConfigFile();
        if (configFile.read(nameInSettingsFolder)) {
            for (int i = 0; i < configFile.getOptions().size(); ++i) {
                final ConfigOption configOption = configFile.getOptions().get(i);
                final SandboxOption sandboxOption = this.optionByName.get(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, configOption.getName()));
                if (sandboxOption != null) {
                    sandboxOption.asConfigOption().parse(configOption.getValueAsString());
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean readLuaFile(final String pathname) {
        final File absoluteFile = new File(pathname).getAbsoluteFile();
        if (!absoluteFile.exists()) {
            return false;
        }
        final Object rawget = LuaManager.env.rawget((Object)"SandboxVars");
        Object o = null;
        if (rawget instanceof KahluaTable) {
            o = rawget;
        }
        try {
            LuaManager.loaded.remove(absoluteFile.getAbsolutePath().replace("\\", "/"));
            LuaManager.RunLua(absoluteFile.getAbsolutePath());
            final Object rawget2 = LuaManager.env.rawget((Object)"SandboxVars");
            if (rawget2 instanceof KahluaTable) {
                final KahluaTable kahluaTable = (KahluaTable)rawget2;
                int intValue = 0;
                final Object rawget3 = kahluaTable.rawget((Object)"VERSION");
                if (rawget3 != null) {
                    if (rawget3 instanceof Double) {
                        intValue = ((Double)rawget3).intValue();
                    }
                    else {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;, rawget3, pathname));
                    }
                    kahluaTable.rawset((Object)"VERSION", (Object)null);
                }
                final KahluaTable upgradeLuaTable = this.upgradeLuaTable("", kahluaTable, intValue);
                for (int i = 0; i < this.options.size(); ++i) {
                    this.options.get(i).fromTable(upgradeLuaTable);
                }
            }
            return true;
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            return false;
        }
        finally {
            if (o != null) {
                LuaManager.env.rawset((Object)"SandboxVars", o);
            }
        }
    }
    
    private boolean writeLuaFile(final String pathname, final boolean b) {
        final File absoluteFile = new File(pathname).getAbsoluteFile();
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
        try {
            final FileWriter fileWriter = new FileWriter(absoluteFile);
            try {
                final HashMap<String, ArrayList<SandboxOption>> hashMap = new HashMap<String, ArrayList<SandboxOption>>();
                final ArrayList<String> list = new ArrayList<String>();
                hashMap.put("", new ArrayList<SandboxOption>());
                for (final SandboxOption sandboxOption : this.options) {
                    if (sandboxOption.getTableName() == null) {
                        hashMap.get("").add(sandboxOption);
                    }
                    else {
                        if (hashMap.get(sandboxOption.getTableName()) == null) {
                            hashMap.put(sandboxOption.getTableName(), new ArrayList<SandboxOption>());
                            list.add(sandboxOption.getTableName());
                        }
                        hashMap.get(sandboxOption.getTableName()).add(sandboxOption);
                    }
                }
                final String lineSeparator = System.lineSeparator();
                if (b) {
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                }
                else {
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                }
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                for (final SandboxOption sandboxOption2 : hashMap.get("")) {
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, sandboxOption2.asConfigOption().getName(), sandboxOption2.asConfigOption().getValueAsLuaString(), lineSeparator));
                }
                for (final String key : list) {
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, key, lineSeparator));
                    for (final SandboxOption sandboxOption3 : hashMap.get(key)) {
                        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, sandboxOption3.getShortName(), sandboxOption3.asConfigOption().getValueAsLuaString(), lineSeparator));
                    }
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lineSeparator));
                }
                fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                fileWriter.close();
            }
            catch (Throwable t) {
                try {
                    fileWriter.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
                throw t;
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
            return false;
        }
        return true;
    }
    
    public void load() {
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("map_sand.bin");
        try {
            final FileInputStream in = new FileInputStream(fileInCurrentSave);
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    synchronized (SliceY.SliceBufferLock) {
                        SliceY.SliceBuffer.clear();
                        SliceY.SliceBuffer.limit(bufferedInputStream.read(SliceY.SliceBuffer.array()));
                        this.load(SliceY.SliceBuffer);
                        this.handleOldZombiesFile1();
                        this.applySettings();
                        this.toLua();
                    }
                    bufferedInputStream.close();
                    in.close();
                    return;
                }
                catch (Throwable t) {
                    try {
                        bufferedInputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
            }
            catch (Throwable t2) {
                try {
                    in.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (FileNotFoundException ex2) {}
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
        this.resetToDefault();
        this.updateFromLua();
    }
    
    public void loadCurrentGameBinFile() {
        final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("map_sand.bin");
        try {
            final FileInputStream in = new FileInputStream(fileInCurrentSave);
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    synchronized (SliceY.SliceBufferLock) {
                        SliceY.SliceBuffer.clear();
                        SliceY.SliceBuffer.limit(bufferedInputStream.read(SliceY.SliceBuffer.array()));
                        this.load(SliceY.SliceBuffer);
                    }
                    this.toLua();
                    bufferedInputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedInputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                in.close();
            }
            catch (Throwable t2) {
                try {
                    in.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    private String upgradeOptionName(final String s, final int n) {
        return s;
    }
    
    private String upgradeOptionValue(final String s, String s2, final int n) {
        if (n < 3 && "DayLength".equals(s)) {
            this.DayLength.parse(s2);
            if (this.DayLength.getValue() == 8) {
                this.DayLength.setValue(14);
            }
            else if (this.DayLength.getValue() == 9) {
                this.DayLength.setValue(26);
            }
            s2 = this.DayLength.getValueAsString();
        }
        if (n < 4 && "CarSpawnRate".equals(s)) {
            try {
                final int n2 = (int)Double.parseDouble(s2);
                if (n2 > 1) {
                    s2 = Integer.toString(n2 + 1);
                }
            }
            catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
        return s2;
    }
    
    private KahluaTable upgradeLuaTable(final String target, final KahluaTable kahluaTable, final int n) {
        final KahluaTable table = LuaManager.platform.newTable();
        final KahluaTableIterator iterator = kahluaTable.iterator();
        while (iterator.advance()) {
            if (!(iterator.getKey() instanceof String)) {
                throw new IllegalStateException("expected a String key");
            }
            if (iterator.getValue() instanceof KahluaTable) {
                table.rawset(iterator.getKey(), (Object)this.upgradeLuaTable(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;, target, iterator.getKey()), (KahluaTable)iterator.getValue(), n));
            }
            else {
                final String upgradeOptionName = this.upgradeOptionName(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;, target, iterator.getKey()), n);
                table.rawset((Object)upgradeOptionName.replace(target, ""), (Object)this.upgradeOptionValue(upgradeOptionName, iterator.getValue().toString(), n));
            }
        }
        return table;
    }
    
    public void sendToServer() {
        if (GameClient.bClient) {
            GameClient.instance.sendSandboxOptionsToServer(this);
        }
    }
    
    public void newCustomOption(final CustomSandboxOption customSandboxOption) {
        final CustomBooleanSandboxOption customBooleanSandboxOption = Type.tryCastTo(customSandboxOption, CustomBooleanSandboxOption.class);
        if (customBooleanSandboxOption != null) {
            this.addCustomOption(new BooleanSandboxOption(this, customBooleanSandboxOption.m_id, customBooleanSandboxOption.defaultValue), customSandboxOption);
            return;
        }
        final CustomDoubleSandboxOption customDoubleSandboxOption = Type.tryCastTo(customSandboxOption, CustomDoubleSandboxOption.class);
        if (customDoubleSandboxOption != null) {
            this.addCustomOption(new DoubleSandboxOption(this, customDoubleSandboxOption.m_id, customDoubleSandboxOption.min, customDoubleSandboxOption.max, customDoubleSandboxOption.defaultValue), customSandboxOption);
            return;
        }
        final CustomEnumSandboxOption customEnumSandboxOption = Type.tryCastTo(customSandboxOption, CustomEnumSandboxOption.class);
        if (customEnumSandboxOption != null) {
            final EnumSandboxOption enumSandboxOption = new EnumSandboxOption(this, customEnumSandboxOption.m_id, customEnumSandboxOption.numValues, customEnumSandboxOption.defaultValue);
            if (customEnumSandboxOption.m_valueTranslation != null) {
                enumSandboxOption.setValueTranslation(customEnumSandboxOption.m_valueTranslation);
            }
            this.addCustomOption(enumSandboxOption, customSandboxOption);
            return;
        }
        final CustomIntegerSandboxOption customIntegerSandboxOption = Type.tryCastTo(customSandboxOption, CustomIntegerSandboxOption.class);
        if (customIntegerSandboxOption != null) {
            this.addCustomOption(new IntegerSandboxOption(this, customIntegerSandboxOption.m_id, customIntegerSandboxOption.min, customIntegerSandboxOption.max, customIntegerSandboxOption.defaultValue), customSandboxOption);
            return;
        }
        final CustomStringSandboxOption customStringSandboxOption = Type.tryCastTo(customSandboxOption, CustomStringSandboxOption.class);
        if (customStringSandboxOption != null) {
            this.addCustomOption(new StringSandboxOption(this, customStringSandboxOption.m_id, customStringSandboxOption.defaultValue), customSandboxOption);
            return;
        }
        throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Lzombie/sandbox/CustomSandboxOption;)Ljava/lang/String;, customSandboxOption));
    }
    
    private void addCustomOption(final SandboxOption e, final CustomSandboxOption customSandboxOption) {
        e.setCustom();
        if (customSandboxOption.m_page != null) {
            e.setPageName(customSandboxOption.m_page);
        }
        if (customSandboxOption.m_translation != null) {
            e.setTranslation(customSandboxOption.m_translation);
        }
        this.m_customOptions.add(e);
    }
    
    private void removeCustomOptions() {
        this.options.removeAll(this.m_customOptions);
        final Iterator<SandboxOption> iterator = this.m_customOptions.iterator();
        while (iterator.hasNext()) {
            this.optionByName.remove(iterator.next().asConfigOption().getName());
        }
        this.m_customOptions.clear();
    }
    
    public static void Reset() {
        SandboxOptions.instance.removeCustomOptions();
    }
    
    public boolean getAllClothesUnlocked() {
        return this.AllClothesUnlocked.getValue();
    }
    
    static {
        instance = new SandboxOptions();
    }
    
    public final class Map
    {
        public final BooleanSandboxOption AllowMiniMap;
        public final BooleanSandboxOption AllowWorldMap;
        public final BooleanSandboxOption MapAllKnown;
        
        Map() {
            this.AllowMiniMap = SandboxOptions.this.newBooleanOption("Map.AllowMiniMap", false);
            this.AllowWorldMap = SandboxOptions.this.newBooleanOption("Map.AllowWorldMap", true);
            this.MapAllKnown = SandboxOptions.this.newBooleanOption("Map.MapAllKnown", false);
        }
    }
    
    public final class ZombieLore
    {
        public final EnumSandboxOption Speed;
        public final EnumSandboxOption Strength;
        public final EnumSandboxOption Toughness;
        public final EnumSandboxOption Transmission;
        public final EnumSandboxOption Mortality;
        public final EnumSandboxOption Reanimate;
        public final EnumSandboxOption Cognition;
        public final EnumSandboxOption CrawlUnderVehicle;
        public final EnumSandboxOption Memory;
        public final EnumSandboxOption Decomp;
        public final EnumSandboxOption Sight;
        public final EnumSandboxOption Hearing;
        public final EnumSandboxOption Smell;
        public final BooleanSandboxOption ThumpNoChasing;
        public final BooleanSandboxOption ThumpOnConstruction;
        public final EnumSandboxOption ActiveOnly;
        public final BooleanSandboxOption TriggerHouseAlarm;
        public final BooleanSandboxOption ZombiesDragDown;
        public final BooleanSandboxOption ZombiesFenceLunge;
        
        private ZombieLore() {
            this.Speed = (EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Speed", 4, 2).setTranslation("ZSpeed");
            this.Strength = (EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Strength", 4, 2).setTranslation("ZStrength");
            this.Toughness = (EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Toughness", 4, 2).setTranslation("ZToughness");
            this.Transmission = (EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Transmission", 4, 1).setTranslation("ZTransmission");
            this.Mortality = (EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Mortality", 7, 5).setTranslation("ZInfectionMortality");
            this.Reanimate = (EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Reanimate", 6, 3).setTranslation("ZReanimateTime");
            this.Cognition = (EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Cognition", 4, 3).setTranslation("ZCognition");
            this.CrawlUnderVehicle = (EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.CrawlUnderVehicle", 7, 5).setTranslation("ZCrawlUnderVehicle");
            this.Memory = (EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Memory", 4, 2).setTranslation("ZMemory");
            this.Decomp = (EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Decomp", 4, 1).setTranslation("ZDecomposition");
            this.Sight = (EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Sight", 3, 2).setTranslation("ZSight");
            this.Hearing = (EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Hearing", 3, 2).setTranslation("ZHearing");
            this.Smell = (EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.Smell", 3, 2).setTranslation("ZSmell");
            this.ThumpNoChasing = SandboxOptions.this.newBooleanOption("ZombieLore.ThumpNoChasing", false);
            this.ThumpOnConstruction = SandboxOptions.this.newBooleanOption("ZombieLore.ThumpOnConstruction", true);
            this.ActiveOnly = (EnumSandboxOption)SandboxOptions.this.newEnumOption("ZombieLore.ActiveOnly", 3, 1).setTranslation("ActiveOnly");
            this.TriggerHouseAlarm = SandboxOptions.this.newBooleanOption("ZombieLore.TriggerHouseAlarm", false);
            this.ZombiesDragDown = SandboxOptions.this.newBooleanOption("ZombieLore.ZombiesDragDown", true);
            this.ZombiesFenceLunge = SandboxOptions.this.newBooleanOption("ZombieLore.ZombiesFenceLunge", true);
        }
    }
    
    public final class ZombieConfig
    {
        public final DoubleSandboxOption PopulationMultiplier;
        public final DoubleSandboxOption PopulationStartMultiplier;
        public final DoubleSandboxOption PopulationPeakMultiplier;
        public final IntegerSandboxOption PopulationPeakDay;
        public final DoubleSandboxOption RespawnHours;
        public final DoubleSandboxOption RespawnUnseenHours;
        public final DoubleSandboxOption RespawnMultiplier;
        public final DoubleSandboxOption RedistributeHours;
        public final IntegerSandboxOption FollowSoundDistance;
        public final IntegerSandboxOption RallyGroupSize;
        public final IntegerSandboxOption RallyTravelDistance;
        public final IntegerSandboxOption RallyGroupSeparation;
        public final IntegerSandboxOption RallyGroupRadius;
        
        private ZombieConfig() {
            this.PopulationMultiplier = SandboxOptions.this.newDoubleOption("ZombieConfig.PopulationMultiplier", 0.0, 4.0, 1.0);
            this.PopulationStartMultiplier = SandboxOptions.this.newDoubleOption("ZombieConfig.PopulationStartMultiplier", 0.0, 4.0, 1.0);
            this.PopulationPeakMultiplier = SandboxOptions.this.newDoubleOption("ZombieConfig.PopulationPeakMultiplier", 0.0, 4.0, 1.5);
            this.PopulationPeakDay = SandboxOptions.this.newIntegerOption("ZombieConfig.PopulationPeakDay", 1, 365, 28);
            this.RespawnHours = SandboxOptions.this.newDoubleOption("ZombieConfig.RespawnHours", 0.0, 8760.0, 72.0);
            this.RespawnUnseenHours = SandboxOptions.this.newDoubleOption("ZombieConfig.RespawnUnseenHours", 0.0, 8760.0, 16.0);
            this.RespawnMultiplier = SandboxOptions.this.newDoubleOption("ZombieConfig.RespawnMultiplier", 0.0, 1.0, 0.1);
            this.RedistributeHours = SandboxOptions.this.newDoubleOption("ZombieConfig.RedistributeHours", 0.0, 8760.0, 12.0);
            this.FollowSoundDistance = SandboxOptions.this.newIntegerOption("ZombieConfig.FollowSoundDistance", 10, 1000, 100);
            this.RallyGroupSize = SandboxOptions.this.newIntegerOption("ZombieConfig.RallyGroupSize", 0, 1000, 20);
            this.RallyTravelDistance = SandboxOptions.this.newIntegerOption("ZombieConfig.RallyTravelDistance", 5, 50, 20);
            this.RallyGroupSeparation = SandboxOptions.this.newIntegerOption("ZombieConfig.RallyGroupSeparation", 5, 25, 15);
            this.RallyGroupRadius = SandboxOptions.this.newIntegerOption("ZombieConfig.RallyGroupRadius", 1, 10, 3);
        }
    }
    
    public static class BooleanSandboxOption extends BooleanConfigOption implements SandboxOption
    {
        protected String translation;
        protected String tableName;
        protected String shortName;
        protected boolean bCustom;
        protected String pageName;
        
        public BooleanSandboxOption(final SandboxOptions sandboxOptions, final String s, final boolean b) {
            super(s, b);
            final String[] name = SandboxOptions.parseName(s);
            this.tableName = name[0];
            this.shortName = name[1];
            sandboxOptions.addOption(this);
        }
        
        @Override
        public ConfigOption asConfigOption() {
            return this;
        }
        
        @Override
        public String getShortName() {
            return this.shortName;
        }
        
        @Override
        public String getTableName() {
            return this.tableName;
        }
        
        @Override
        public SandboxOption setTranslation(final String translation) {
            this.translation = translation;
            return this;
        }
        
        @Override
        public String getTranslatedName() {
            return Translator.getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.translation == null) ? this.getShortName() : this.translation));
        }
        
        @Override
        public String getTooltip() {
            return Translator.getTextOrNull(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.translation == null) ? this.getShortName() : this.translation));
        }
        
        @Override
        public void fromTable(KahluaTable kahluaTable) {
            if (this.tableName != null) {
                final Object rawget = kahluaTable.rawget((Object)this.tableName);
                if (!(rawget instanceof KahluaTable)) {
                    return;
                }
                kahluaTable = (KahluaTable)rawget;
            }
            final Object rawget2 = kahluaTable.rawget((Object)this.getShortName());
            if (rawget2 != null) {
                this.setValueFromObject(rawget2);
            }
        }
        
        @Override
        public void toTable(KahluaTable kahluaTable) {
            if (this.tableName != null) {
                final Object rawget = kahluaTable.rawget((Object)this.tableName);
                if (rawget instanceof KahluaTable) {
                    kahluaTable = (KahluaTable)rawget;
                }
                else {
                    final KahluaTable table = LuaManager.platform.newTable();
                    kahluaTable.rawset((Object)this.tableName, (Object)table);
                    kahluaTable = table;
                }
            }
            kahluaTable.rawset((Object)this.getShortName(), this.getValueAsObject());
        }
        
        @Override
        public void setCustom() {
            this.bCustom = true;
        }
        
        @Override
        public boolean isCustom() {
            return this.bCustom;
        }
        
        @Override
        public SandboxOption setPageName(final String pageName) {
            this.pageName = pageName;
            return this;
        }
        
        @Override
        public String getPageName() {
            return this.pageName;
        }
    }
    
    public static class DoubleSandboxOption extends DoubleConfigOption implements SandboxOption
    {
        protected String translation;
        protected String tableName;
        protected String shortName;
        protected boolean bCustom;
        protected String pageName;
        
        public DoubleSandboxOption(final SandboxOptions sandboxOptions, final String s, final double n, final double n2, final double n3) {
            super(s, n, n2, n3);
            final String[] name = SandboxOptions.parseName(s);
            this.tableName = name[0];
            this.shortName = name[1];
            sandboxOptions.addOption(this);
        }
        
        @Override
        public ConfigOption asConfigOption() {
            return this;
        }
        
        @Override
        public String getShortName() {
            return this.shortName;
        }
        
        @Override
        public String getTableName() {
            return this.tableName;
        }
        
        @Override
        public SandboxOption setTranslation(final String translation) {
            this.translation = translation;
            return this;
        }
        
        @Override
        public String getTranslatedName() {
            return Translator.getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.translation == null) ? this.getShortName() : this.translation));
        }
        
        @Override
        public String getTooltip() {
            String s;
            if ("ZombieConfig".equals(this.tableName)) {
                s = Translator.getTextOrNull(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.translation == null) ? this.getShortName() : this.translation));
            }
            else {
                s = Translator.getTextOrNull(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.translation == null) ? this.getShortName() : this.translation));
            }
            final String text = Translator.getText("Sandbox_MinMaxDefault", this.min, this.max, this.defaultValue);
            if (s == null) {
                return text;
            }
            if (text == null) {
                return s;
            }
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, text);
        }
        
        @Override
        public void fromTable(KahluaTable kahluaTable) {
            if (this.tableName != null) {
                final Object rawget = kahluaTable.rawget((Object)this.tableName);
                if (!(rawget instanceof KahluaTable)) {
                    return;
                }
                kahluaTable = (KahluaTable)rawget;
            }
            final Object rawget2 = kahluaTable.rawget((Object)this.getShortName());
            if (rawget2 != null) {
                this.setValueFromObject(rawget2);
            }
        }
        
        @Override
        public void toTable(KahluaTable kahluaTable) {
            if (this.tableName != null) {
                final Object rawget = kahluaTable.rawget((Object)this.tableName);
                if (rawget instanceof KahluaTable) {
                    kahluaTable = (KahluaTable)rawget;
                }
                else {
                    final KahluaTable table = LuaManager.platform.newTable();
                    kahluaTable.rawset((Object)this.tableName, (Object)table);
                    kahluaTable = table;
                }
            }
            kahluaTable.rawset((Object)this.getShortName(), this.getValueAsObject());
        }
        
        @Override
        public void setCustom() {
            this.bCustom = true;
        }
        
        @Override
        public boolean isCustom() {
            return this.bCustom;
        }
        
        @Override
        public SandboxOption setPageName(final String pageName) {
            this.pageName = pageName;
            return this;
        }
        
        @Override
        public String getPageName() {
            return this.pageName;
        }
    }
    
    public static class StringSandboxOption extends StringConfigOption implements SandboxOption
    {
        protected String translation;
        protected String tableName;
        protected String shortName;
        protected boolean bCustom;
        protected String pageName;
        
        public StringSandboxOption(final SandboxOptions sandboxOptions, final String s, final String s2) {
            super(s, s2);
            final String[] name = SandboxOptions.parseName(s);
            this.tableName = name[0];
            this.shortName = name[1];
            sandboxOptions.addOption(this);
        }
        
        @Override
        public ConfigOption asConfigOption() {
            return this;
        }
        
        @Override
        public String getShortName() {
            return this.shortName;
        }
        
        @Override
        public String getTableName() {
            return this.tableName;
        }
        
        @Override
        public SandboxOption setTranslation(final String translation) {
            this.translation = translation;
            return this;
        }
        
        @Override
        public String getTranslatedName() {
            return Translator.getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.translation == null) ? this.getShortName() : this.translation));
        }
        
        @Override
        public String getTooltip() {
            return Translator.getTextOrNull(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.translation == null) ? this.getShortName() : this.translation));
        }
        
        @Override
        public void fromTable(KahluaTable kahluaTable) {
            if (this.tableName != null) {
                final Object rawget = kahluaTable.rawget((Object)this.tableName);
                if (!(rawget instanceof KahluaTable)) {
                    return;
                }
                kahluaTable = (KahluaTable)rawget;
            }
            final Object rawget2 = kahluaTable.rawget((Object)this.getShortName());
            if (rawget2 != null) {
                this.setValueFromObject(rawget2);
            }
        }
        
        @Override
        public void toTable(KahluaTable kahluaTable) {
            if (this.tableName != null) {
                final Object rawget = kahluaTable.rawget((Object)this.tableName);
                if (rawget instanceof KahluaTable) {
                    kahluaTable = (KahluaTable)rawget;
                }
                else {
                    final KahluaTable table = LuaManager.platform.newTable();
                    kahluaTable.rawset((Object)this.tableName, (Object)table);
                    kahluaTable = table;
                }
            }
            kahluaTable.rawset((Object)this.getShortName(), this.getValueAsObject());
        }
        
        @Override
        public void setCustom() {
            this.bCustom = true;
        }
        
        @Override
        public boolean isCustom() {
            return this.bCustom;
        }
        
        @Override
        public SandboxOption setPageName(final String pageName) {
            this.pageName = pageName;
            return this;
        }
        
        @Override
        public String getPageName() {
            return this.pageName;
        }
    }
    
    public static class IntegerSandboxOption extends IntegerConfigOption implements SandboxOption
    {
        protected String translation;
        protected String tableName;
        protected String shortName;
        protected boolean bCustom;
        protected String pageName;
        
        public IntegerSandboxOption(final SandboxOptions sandboxOptions, final String s, final int n, final int n2, final int n3) {
            super(s, n, n2, n3);
            final String[] name = SandboxOptions.parseName(s);
            this.tableName = name[0];
            this.shortName = name[1];
            sandboxOptions.addOption(this);
        }
        
        @Override
        public ConfigOption asConfigOption() {
            return this;
        }
        
        @Override
        public String getShortName() {
            return this.shortName;
        }
        
        @Override
        public String getTableName() {
            return this.tableName;
        }
        
        @Override
        public SandboxOption setTranslation(final String translation) {
            this.translation = translation;
            return this;
        }
        
        @Override
        public String getTranslatedName() {
            return Translator.getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.translation == null) ? this.getShortName() : this.translation));
        }
        
        @Override
        public String getTooltip() {
            String s;
            if ("ZombieConfig".equals(this.tableName)) {
                s = Translator.getTextOrNull(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.translation == null) ? this.getShortName() : this.translation));
            }
            else {
                s = Translator.getTextOrNull(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.translation == null) ? this.getShortName() : this.translation));
            }
            final String text = Translator.getText("Sandbox_MinMaxDefault", this.min, this.max, this.defaultValue);
            if (s == null) {
                return text;
            }
            if (text == null) {
                return s;
            }
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, text);
        }
        
        @Override
        public void fromTable(KahluaTable kahluaTable) {
            if (this.tableName != null) {
                final Object rawget = kahluaTable.rawget((Object)this.tableName);
                if (!(rawget instanceof KahluaTable)) {
                    return;
                }
                kahluaTable = (KahluaTable)rawget;
            }
            final Object rawget2 = kahluaTable.rawget((Object)this.getShortName());
            if (rawget2 != null) {
                this.setValueFromObject(rawget2);
            }
        }
        
        @Override
        public void toTable(KahluaTable kahluaTable) {
            if (this.tableName != null) {
                final Object rawget = kahluaTable.rawget((Object)this.tableName);
                if (rawget instanceof KahluaTable) {
                    kahluaTable = (KahluaTable)rawget;
                }
                else {
                    final KahluaTable table = LuaManager.platform.newTable();
                    kahluaTable.rawset((Object)this.tableName, (Object)table);
                    kahluaTable = table;
                }
            }
            kahluaTable.rawset((Object)this.getShortName(), this.getValueAsObject());
        }
        
        @Override
        public void setCustom() {
            this.bCustom = true;
        }
        
        @Override
        public boolean isCustom() {
            return this.bCustom;
        }
        
        @Override
        public SandboxOption setPageName(final String pageName) {
            this.pageName = pageName;
            return this;
        }
        
        @Override
        public String getPageName() {
            return this.pageName;
        }
    }
    
    public static class EnumSandboxOption extends EnumConfigOption implements SandboxOption
    {
        protected String translation;
        protected String tableName;
        protected String shortName;
        protected boolean bCustom;
        protected String pageName;
        protected String valueTranslation;
        
        public EnumSandboxOption(final SandboxOptions sandboxOptions, final String s, final int n, final int n2) {
            super(s, n, n2);
            final String[] name = SandboxOptions.parseName(s);
            this.tableName = name[0];
            this.shortName = name[1];
            sandboxOptions.addOption(this);
        }
        
        @Override
        public ConfigOption asConfigOption() {
            return this;
        }
        
        @Override
        public String getShortName() {
            return this.shortName;
        }
        
        @Override
        public String getTableName() {
            return this.tableName;
        }
        
        @Override
        public SandboxOption setTranslation(final String translation) {
            this.translation = translation;
            return this;
        }
        
        @Override
        public String getTranslatedName() {
            return Translator.getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.translation == null) ? this.getShortName() : this.translation));
        }
        
        @Override
        public String getTooltip() {
            return Translator.getTextOrNull(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (this.translation == null) ? this.getShortName() : this.translation));
        }
        
        @Override
        public void fromTable(KahluaTable kahluaTable) {
            if (this.tableName != null) {
                final Object rawget = kahluaTable.rawget((Object)this.tableName);
                if (!(rawget instanceof KahluaTable)) {
                    return;
                }
                kahluaTable = (KahluaTable)rawget;
            }
            final Object rawget2 = kahluaTable.rawget((Object)this.getShortName());
            if (rawget2 != null) {
                this.setValueFromObject(rawget2);
            }
        }
        
        @Override
        public void toTable(KahluaTable kahluaTable) {
            if (this.tableName != null) {
                final Object rawget = kahluaTable.rawget((Object)this.tableName);
                if (rawget instanceof KahluaTable) {
                    kahluaTable = (KahluaTable)rawget;
                }
                else {
                    final KahluaTable table = LuaManager.platform.newTable();
                    kahluaTable.rawset((Object)this.tableName, (Object)table);
                    kahluaTable = table;
                }
            }
            kahluaTable.rawset((Object)this.getShortName(), this.getValueAsObject());
        }
        
        @Override
        public void setCustom() {
            this.bCustom = true;
        }
        
        @Override
        public boolean isCustom() {
            return this.bCustom;
        }
        
        @Override
        public SandboxOption setPageName(final String pageName) {
            this.pageName = pageName;
            return this;
        }
        
        @Override
        public String getPageName() {
            return this.pageName;
        }
        
        public EnumSandboxOption setValueTranslation(final String valueTranslation) {
            this.valueTranslation = valueTranslation;
            return this;
        }
        
        public String getValueTranslation() {
            return (this.valueTranslation != null) ? this.valueTranslation : ((this.translation == null) ? this.getShortName() : this.translation);
        }
        
        public String getValueTranslationByIndex(final int n) {
            if (n < 1 || n > this.getNumValues()) {
                throw new ArrayIndexOutOfBoundsException();
            }
            return Translator.getText(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getValueTranslation(), n));
        }
    }
    
    public interface SandboxOption
    {
        ConfigOption asConfigOption();
        
        String getShortName();
        
        String getTableName();
        
        SandboxOption setTranslation(final String p0);
        
        String getTranslatedName();
        
        String getTooltip();
        
        void fromTable(final KahluaTable p0);
        
        void toTable(final KahluaTable p0);
        
        void setCustom();
        
        boolean isCustom();
        
        SandboxOption setPageName(final String p0);
        
        String getPageName();
    }
}
