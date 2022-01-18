// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.BodyDamage;

import zombie.FliesSound;
import zombie.characters.IsoSurvivor;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.textures.Texture;
import zombie.core.SpriteRenderer;
import zombie.audio.parameters.ParameterZombieState;
import zombie.debug.DebugType;
import zombie.SandboxOptions;
import zombie.iso.IsoMovingObject;
import zombie.util.StringUtils;
import zombie.characters.IsoZombie;
import zombie.network.ServerOptions;
import zombie.network.GameClient;
import zombie.characterTextures.BloodBodyPartType;
import zombie.debug.DebugOptions;
import zombie.network.GameServer;
import zombie.inventory.types.HandWeapon;
import zombie.WorldSoundManager;
import zombie.inventory.types.Drainable;
import zombie.characters.Moodles.MoodleType;
import zombie.vehicles.VehicleWindow;
import zombie.vehicles.VehiclePart;
import zombie.characters.ClothingWetness;
import zombie.vehicles.BaseVehicle;
import zombie.iso.IsoGridSquare;
import zombie.ZomboidGlobals;
import zombie.core.math.PZMath;
import zombie.iso.weather.ClimateManager;
import zombie.inventory.types.Literature;
import zombie.core.Rand;
import zombie.core.Core;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Food;
import zombie.GameTime;
import zombie.characters.Stats;
import java.io.IOException;
import zombie.debug.DebugLog;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoGameCharacter;
import java.util.ArrayList;

public final class BodyDamage
{
    public final ArrayList<BodyPart> BodyParts;
    public final ArrayList<BodyPartLast> BodyPartsLastState;
    public int DamageModCount;
    public float InfectionGrowthRate;
    public float InfectionLevel;
    public boolean IsInfected;
    public float InfectionTime;
    public float InfectionMortalityDuration;
    public float FakeInfectionLevel;
    public boolean IsFakeInfected;
    public float OverallBodyHealth;
    public float StandardHealthAddition;
    public float ReducedHealthAddition;
    public float SeverlyReducedHealthAddition;
    public float SleepingHealthAddition;
    public float HealthFromFood;
    public float HealthReductionFromSevereBadMoodles;
    public int StandardHealthFromFoodTime;
    public float HealthFromFoodTimer;
    public float BoredomLevel;
    public float BoredomDecreaseFromReading;
    public float InitialThumpPain;
    public float InitialScratchPain;
    public float InitialBitePain;
    public float InitialWoundPain;
    public float ContinualPainIncrease;
    public float PainReductionFromMeds;
    public float StandardPainReductionWhenWell;
    public int OldNumZombiesVisible;
    public int CurrentNumZombiesVisible;
    public float PanicIncreaseValue;
    public float PanicIncreaseValueFrame;
    public float PanicReductionValue;
    public float DrunkIncreaseValue;
    public float DrunkReductionValue;
    public boolean IsOnFire;
    public boolean BurntToDeath;
    public float Wetness;
    public float CatchACold;
    public boolean HasACold;
    public float ColdStrength;
    public float ColdProgressionRate;
    public int TimeToSneezeOrCough;
    public int MildColdSneezeTimerMin;
    public int MildColdSneezeTimerMax;
    public int ColdSneezeTimerMin;
    public int ColdSneezeTimerMax;
    public int NastyColdSneezeTimerMin;
    public int NastyColdSneezeTimerMax;
    public int SneezeCoughActive;
    public int SneezeCoughTime;
    public int SneezeCoughDelay;
    public float UnhappynessLevel;
    public float ColdDamageStage;
    public IsoGameCharacter ParentChar;
    private float FoodSicknessLevel;
    private int RemotePainLevel;
    private float Temperature;
    private float lastTemperature;
    private float PoisonLevel;
    private boolean reduceFakeInfection;
    private float painReduction;
    private float coldReduction;
    private Thermoregulator thermoregulator;
    public static final float InfectionLevelToZombify = 0.001f;
    static String behindStr;
    static String leftStr;
    static String rightStr;
    
    public BodyDamage(final IsoGameCharacter parentChar) {
        this.BodyParts = new ArrayList<BodyPart>(18);
        this.BodyPartsLastState = new ArrayList<BodyPartLast>(18);
        this.DamageModCount = 60;
        this.InfectionGrowthRate = 0.001f;
        this.InfectionLevel = 0.0f;
        this.InfectionTime = -1.0f;
        this.InfectionMortalityDuration = -1.0f;
        this.FakeInfectionLevel = 0.0f;
        this.OverallBodyHealth = 100.0f;
        this.StandardHealthAddition = 0.002f;
        this.ReducedHealthAddition = 0.0013f;
        this.SeverlyReducedHealthAddition = 8.0E-4f;
        this.SleepingHealthAddition = 0.02f;
        this.HealthFromFood = 0.015f;
        this.HealthReductionFromSevereBadMoodles = 0.0165f;
        this.StandardHealthFromFoodTime = 1600;
        this.HealthFromFoodTimer = 0.0f;
        this.BoredomLevel = 0.0f;
        this.BoredomDecreaseFromReading = 0.5f;
        this.InitialThumpPain = 14.0f;
        this.InitialScratchPain = 18.0f;
        this.InitialBitePain = 25.0f;
        this.InitialWoundPain = 80.0f;
        this.ContinualPainIncrease = 0.001f;
        this.PainReductionFromMeds = 30.0f;
        this.StandardPainReductionWhenWell = 0.01f;
        this.OldNumZombiesVisible = 0;
        this.CurrentNumZombiesVisible = 0;
        this.PanicIncreaseValue = 7.0f;
        this.PanicIncreaseValueFrame = 0.035f;
        this.PanicReductionValue = 0.06f;
        this.DrunkIncreaseValue = 20.5f;
        this.DrunkReductionValue = 0.0042f;
        this.IsOnFire = false;
        this.BurntToDeath = false;
        this.Wetness = 0.0f;
        this.CatchACold = 0.0f;
        this.HasACold = false;
        this.ColdStrength = 0.0f;
        this.ColdProgressionRate = 0.0112f;
        this.TimeToSneezeOrCough = 0;
        this.MildColdSneezeTimerMin = 600;
        this.MildColdSneezeTimerMax = 800;
        this.ColdSneezeTimerMin = 300;
        this.ColdSneezeTimerMax = 600;
        this.NastyColdSneezeTimerMin = 200;
        this.NastyColdSneezeTimerMax = 300;
        this.SneezeCoughActive = 0;
        this.SneezeCoughTime = 0;
        this.SneezeCoughDelay = 25;
        this.UnhappynessLevel = 0.0f;
        this.ColdDamageStage = 0.0f;
        this.FoodSicknessLevel = 0.0f;
        this.Temperature = 37.0f;
        this.lastTemperature = 37.0f;
        this.PoisonLevel = 0.0f;
        this.reduceFakeInfection = false;
        this.painReduction = 0.0f;
        this.coldReduction = 0.0f;
        this.BodyParts.add(new BodyPart(BodyPartType.Hand_L, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.Hand_R, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.ForeArm_L, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.ForeArm_R, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.UpperArm_L, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.UpperArm_R, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.Torso_Upper, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.Torso_Lower, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.Head, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.Neck, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.Groin, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.UpperLeg_L, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.UpperLeg_R, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.LowerLeg_L, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.LowerLeg_R, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.Foot_L, parentChar));
        this.BodyParts.add(new BodyPart(BodyPartType.Foot_R, parentChar));
        for (final BodyPart bodyPart : this.BodyParts) {
            this.BodyPartsLastState.add(new BodyPartLast());
        }
        this.RestoreToFullHealth();
        this.ParentChar = parentChar;
        if (this.ParentChar instanceof IsoPlayer) {
            this.thermoregulator = new Thermoregulator(this);
        }
        this.setBodyPartsLastState();
    }
    
    public BodyPart getBodyPart(final BodyPartType bodyPartType) {
        return this.BodyParts.get(BodyPartType.ToIndex(bodyPartType));
    }
    
    public BodyPartLast getBodyPartsLastState(final BodyPartType bodyPartType) {
        return this.BodyPartsLastState.get(BodyPartType.ToIndex(bodyPartType));
    }
    
    public void setBodyPartsLastState() {
        for (int i = 0; i < this.getBodyParts().size(); ++i) {
            this.BodyPartsLastState.get(i).copy(this.getBodyParts().get(i));
        }
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        for (int i = 0; i < this.getBodyParts().size(); ++i) {
            final BodyPart bodyPart = this.getBodyParts().get(i);
            bodyPart.SetBitten(byteBuffer.get() == 1);
            bodyPart.setScratched(byteBuffer.get() == 1, false);
            bodyPart.setBandaged(byteBuffer.get() == 1, 0.0f);
            bodyPart.setBleeding(byteBuffer.get() == 1);
            bodyPart.setDeepWounded(byteBuffer.get() == 1);
            bodyPart.SetFakeInfected(byteBuffer.get() == 1);
            bodyPart.SetInfected(byteBuffer.get() == 1);
            bodyPart.SetHealth(byteBuffer.getFloat());
            if (n >= 37 && n <= 43) {
                byteBuffer.getInt();
            }
            if (n >= 44) {
                if (bodyPart.bandaged()) {
                    bodyPart.setBandageLife(byteBuffer.getFloat());
                }
                bodyPart.setInfectedWound(byteBuffer.get() == 1);
                if (bodyPart.isInfectedWound()) {
                    bodyPart.setWoundInfectionLevel(byteBuffer.getFloat());
                }
                bodyPart.setBiteTime(byteBuffer.getFloat());
                bodyPart.setScratchTime(byteBuffer.getFloat());
                bodyPart.setBleedingTime(byteBuffer.getFloat());
                bodyPart.setAlcoholLevel(byteBuffer.getFloat());
                bodyPart.setAdditionalPain(byteBuffer.getFloat());
                bodyPart.setDeepWoundTime(byteBuffer.getFloat());
                bodyPart.setHaveGlass(byteBuffer.get() == 1);
                bodyPart.setGetBandageXp(byteBuffer.get() == 1);
                if (n >= 48) {
                    bodyPart.setStitched(byteBuffer.get() == 1);
                    bodyPart.setStitchTime(byteBuffer.getFloat());
                }
                bodyPart.setGetStitchXp(byteBuffer.get() == 1);
                bodyPart.setGetSplintXp(byteBuffer.get() == 1);
                bodyPart.setFractureTime(byteBuffer.getFloat());
                bodyPart.setSplint(byteBuffer.get() == 1, 0.0f);
                if (bodyPart.isSplint()) {
                    bodyPart.setSplintFactor(byteBuffer.getFloat());
                }
                bodyPart.setHaveBullet(byteBuffer.get() == 1, 0);
                bodyPart.setBurnTime(byteBuffer.getFloat());
                bodyPart.setNeedBurnWash(byteBuffer.get() == 1);
                bodyPart.setLastTimeBurnWash(byteBuffer.getFloat());
                bodyPart.setSplintItem(GameWindow.ReadString(byteBuffer));
                bodyPart.setBandageType(GameWindow.ReadString(byteBuffer));
                bodyPart.setCutTime(byteBuffer.getFloat());
                if (n >= 153) {
                    bodyPart.setWetness(byteBuffer.getFloat());
                }
                if (n >= 167) {
                    bodyPart.setStiffness(byteBuffer.getFloat());
                }
            }
        }
        this.setBodyPartsLastState();
        this.setInfectionLevel(byteBuffer.getFloat());
        this.setFakeInfectionLevel(byteBuffer.getFloat());
        this.setWetness(byteBuffer.getFloat());
        this.setCatchACold(byteBuffer.getFloat());
        this.setHasACold(byteBuffer.get() == 1);
        this.setColdStrength(byteBuffer.getFloat());
        this.setUnhappynessLevel(byteBuffer.getFloat());
        this.setBoredomLevel(byteBuffer.getFloat());
        this.setFoodSicknessLevel(byteBuffer.getFloat());
        this.PoisonLevel = byteBuffer.getFloat();
        this.setTemperature(byteBuffer.getFloat());
        this.setReduceFakeInfection(byteBuffer.get() == 1);
        this.setHealthFromFoodTimer(byteBuffer.getFloat());
        this.painReduction = byteBuffer.getFloat();
        this.coldReduction = byteBuffer.getFloat();
        this.InfectionTime = byteBuffer.getFloat();
        this.InfectionMortalityDuration = byteBuffer.getFloat();
        this.ColdDamageStage = byteBuffer.getFloat();
        this.calculateOverallHealth();
        if (n >= 153 && byteBuffer.get() == 1) {
            if (this.thermoregulator != null) {
                this.thermoregulator.load(byteBuffer, n);
            }
            else {
                new Thermoregulator(this).load(byteBuffer, n);
                DebugLog.log("Couldnt load Thermoregulator, == null");
            }
        }
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        for (int i = 0; i < this.getBodyParts().size(); ++i) {
            final BodyPart bodyPart = this.getBodyParts().get(i);
            byteBuffer.put((byte)(bodyPart.bitten() ? 1 : 0));
            byteBuffer.put((byte)(bodyPart.scratched() ? 1 : 0));
            byteBuffer.put((byte)(bodyPart.bandaged() ? 1 : 0));
            byteBuffer.put((byte)(bodyPart.bleeding() ? 1 : 0));
            byteBuffer.put((byte)(bodyPart.deepWounded() ? 1 : 0));
            byteBuffer.put((byte)(bodyPart.IsFakeInfected() ? 1 : 0));
            byteBuffer.put((byte)(bodyPart.IsInfected() ? 1 : 0));
            byteBuffer.putFloat(bodyPart.getHealth());
            if (bodyPart.bandaged()) {
                byteBuffer.putFloat(bodyPart.getBandageLife());
            }
            byteBuffer.put((byte)(bodyPart.isInfectedWound() ? 1 : 0));
            if (bodyPart.isInfectedWound()) {
                byteBuffer.putFloat(bodyPart.getWoundInfectionLevel());
            }
            byteBuffer.putFloat(bodyPart.getBiteTime());
            byteBuffer.putFloat(bodyPart.getScratchTime());
            byteBuffer.putFloat(bodyPart.getBleedingTime());
            byteBuffer.putFloat(bodyPart.getAlcoholLevel());
            byteBuffer.putFloat(bodyPart.getAdditionalPain());
            byteBuffer.putFloat(bodyPart.getDeepWoundTime());
            byteBuffer.put((byte)(bodyPart.haveGlass() ? 1 : 0));
            byteBuffer.put((byte)(bodyPart.isGetBandageXp() ? 1 : 0));
            byteBuffer.put((byte)(bodyPart.stitched() ? 1 : 0));
            byteBuffer.putFloat(bodyPart.getStitchTime());
            byteBuffer.put((byte)(bodyPart.isGetStitchXp() ? 1 : 0));
            byteBuffer.put((byte)(bodyPart.isGetSplintXp() ? 1 : 0));
            byteBuffer.putFloat(bodyPart.getFractureTime());
            byteBuffer.put((byte)(bodyPart.isSplint() ? 1 : 0));
            if (bodyPart.isSplint()) {
                byteBuffer.putFloat(bodyPart.getSplintFactor());
            }
            byteBuffer.put((byte)(bodyPart.haveBullet() ? 1 : 0));
            byteBuffer.putFloat(bodyPart.getBurnTime());
            byteBuffer.put((byte)(bodyPart.isNeedBurnWash() ? 1 : 0));
            byteBuffer.putFloat(bodyPart.getLastTimeBurnWash());
            GameWindow.WriteString(byteBuffer, bodyPart.getSplintItem());
            GameWindow.WriteString(byteBuffer, bodyPart.getBandageType());
            byteBuffer.putFloat(bodyPart.getCutTime());
            byteBuffer.putFloat(bodyPart.getWetness());
            byteBuffer.putFloat(bodyPart.getStiffness());
        }
        byteBuffer.putFloat(this.InfectionLevel);
        byteBuffer.putFloat(this.getFakeInfectionLevel());
        byteBuffer.putFloat(this.getWetness());
        byteBuffer.putFloat(this.getCatchACold());
        byteBuffer.put((byte)(this.isHasACold() ? 1 : 0));
        byteBuffer.putFloat(this.getColdStrength());
        byteBuffer.putFloat(this.getUnhappynessLevel());
        byteBuffer.putFloat(this.getBoredomLevel());
        byteBuffer.putFloat(this.getFoodSicknessLevel());
        byteBuffer.putFloat(this.PoisonLevel);
        byteBuffer.putFloat(this.Temperature);
        byteBuffer.put((byte)(this.isReduceFakeInfection() ? 1 : 0));
        byteBuffer.putFloat(this.HealthFromFoodTimer);
        byteBuffer.putFloat(this.painReduction);
        byteBuffer.putFloat(this.coldReduction);
        byteBuffer.putFloat(this.InfectionTime);
        byteBuffer.putFloat(this.InfectionMortalityDuration);
        byteBuffer.putFloat(this.ColdDamageStage);
        byteBuffer.put((byte)((this.thermoregulator != null) ? 1 : 0));
        if (this.thermoregulator != null) {
            this.thermoregulator.save(byteBuffer);
        }
    }
    
    public boolean IsFakeInfected() {
        return this.isIsFakeInfected();
    }
    
    public void OnFire(final boolean isOnFire) {
        this.setIsOnFire(isOnFire);
    }
    
    public boolean IsOnFire() {
        return this.isIsOnFire();
    }
    
    public boolean WasBurntToDeath() {
        return this.isBurntToDeath();
    }
    
    public void IncreasePanicFloat(final float n) {
        float n2 = 1.0f;
        if (this.getParentChar().getBetaEffect() > 0.0f) {
            n2 -= this.getParentChar().getBetaDelta();
            if (n2 > 1.0f) {
                n2 = 1.0f;
            }
            if (n2 < 0.0f) {
                n2 = 0.0f;
            }
        }
        if (this.getParentChar().getCharacterTraits().Cowardly.isSet()) {
            n2 *= 2.0f;
        }
        if (this.getParentChar().getCharacterTraits().Brave.isSet()) {
            n2 *= 0.3f;
        }
        if (this.getParentChar().getCharacterTraits().Desensitized.isSet()) {
            n2 = 0.0f;
        }
        final Stats stats = this.ParentChar.getStats();
        stats.Panic += this.getPanicIncreaseValueFrame() * n * n2;
        if (this.getParentChar().getStats().Panic > 100.0f) {
            this.ParentChar.getStats().Panic = 100.0f;
        }
    }
    
    public void IncreasePanic(int n) {
        if (this.getParentChar().getVehicle() != null) {
            n /= 2;
        }
        float n2 = 1.0f;
        if (this.getParentChar().getBetaEffect() > 0.0f) {
            n2 -= this.getParentChar().getBetaDelta();
            if (n2 > 1.0f) {
                n2 = 1.0f;
            }
            if (n2 < 0.0f) {
                n2 = 0.0f;
            }
        }
        if (this.getParentChar().getCharacterTraits().Cowardly.isSet()) {
            n2 *= 2.0f;
        }
        if (this.getParentChar().getCharacterTraits().Brave.isSet()) {
            n2 *= 0.3f;
        }
        if (this.getParentChar().getCharacterTraits().Desensitized.isSet()) {
            n2 = 0.0f;
        }
        final Stats stats = this.ParentChar.getStats();
        stats.Panic += this.getPanicIncreaseValue() * n * n2;
        if (this.getParentChar().getStats().Panic > 100.0f) {
            this.ParentChar.getStats().Panic = 100.0f;
        }
    }
    
    public void ReducePanic() {
        if (this.ParentChar.getStats().Panic <= 0.0f) {
            return;
        }
        final float n = this.getPanicReductionValue() * (GameTime.getInstance().getMultiplier() / 1.6f);
        int n2 = (int)Math.floor(new Double(GameTime.instance.getNightsSurvived()) / 30.0);
        if (n2 > 5) {
            n2 = 5;
        }
        float n3 = n + this.getPanicReductionValue() * n2;
        if (this.ParentChar.isAsleep()) {
            n3 *= 2.0f;
        }
        final Stats stats = this.ParentChar.getStats();
        stats.Panic -= n3;
        if (this.getParentChar().getStats().Panic < 0.0f) {
            this.ParentChar.getStats().Panic = 0.0f;
        }
    }
    
    public void UpdatePanicState() {
        final int numVisibleZombies = this.getParentChar().getStats().NumVisibleZombies;
        if (numVisibleZombies > this.getOldNumZombiesVisible()) {
            this.IncreasePanic(numVisibleZombies - this.getOldNumZombiesVisible());
        }
        else {
            this.ReducePanic();
        }
        this.setOldNumZombiesVisible(numVisibleZombies);
    }
    
    public void JustDrankBooze(final Food food, float n) {
        float n2 = 1.0f;
        if (this.getParentChar().Traits.HeavyDrinker.isSet()) {
            n2 = 0.3f;
        }
        if (this.getParentChar().Traits.LightDrinker.isSet()) {
            n2 = 4.0f;
        }
        if (food.getBaseHunger() != 0.0f) {
            n = food.getHungChange() * n / food.getBaseHunger() * 2.0f;
        }
        float n3 = n2 * n;
        if (food.getName().toLowerCase().contains("beer")) {
            n3 *= 0.25f;
        }
        if (this.getParentChar().getStats().hunger > 0.8) {
            n3 *= 1.25;
        }
        else if (this.getParentChar().getStats().hunger > 0.6) {
            n3 *= (float)1.1;
        }
        final Stats stats = this.ParentChar.getStats();
        stats.Drunkenness += this.getDrunkIncreaseValue() * n3;
        if (this.getParentChar().getStats().Drunkenness > 100.0f) {
            this.ParentChar.getStats().Drunkenness = 100.0f;
        }
        this.getParentChar().SleepingTablet(0.02f * n);
        this.getParentChar().BetaAntiDepress(0.4f * n);
        this.getParentChar().BetaBlockers(0.2f * n);
        this.getParentChar().PainMeds(0.2f * n);
    }
    
    public void JustTookPill(final InventoryItem inventoryItem) {
        if ("PillsBeta".equals(inventoryItem.getType())) {
            if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0f) {
                this.getParentChar().BetaBlockers(0.15f);
            }
            else {
                this.getParentChar().BetaBlockers(0.3f);
            }
            inventoryItem.Use();
        }
        else if ("PillsAntiDep".equals(inventoryItem.getType())) {
            if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0f) {
                this.getParentChar().BetaAntiDepress(0.15f);
            }
            else {
                this.getParentChar().BetaAntiDepress(0.3f);
            }
            inventoryItem.Use();
        }
        else if ("PillsSleepingTablets".equals(inventoryItem.getType())) {
            inventoryItem.Use();
            this.getParentChar().SleepingTablet(0.1f);
            if (this.getParentChar() instanceof IsoPlayer) {
                ((IsoPlayer)this.getParentChar()).setSleepingPillsTaken(((IsoPlayer)this.getParentChar()).getSleepingPillsTaken() + 1);
            }
        }
        else if ("Pills".equals(inventoryItem.getType())) {
            inventoryItem.Use();
            if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0f) {
                this.getParentChar().PainMeds(0.15f);
            }
            else {
                this.getParentChar().PainMeds(0.45f);
            }
        }
        else if ("PillsVitamins".equals(inventoryItem.getType())) {
            inventoryItem.Use();
            if (this.getParentChar() != null && this.getParentChar().getStats().Drunkenness > 10.0f) {
                final Stats stats = this.getParentChar().getStats();
                stats.fatigue += inventoryItem.getFatigueChange() / 2.0f;
            }
            else {
                final Stats stats2 = this.getParentChar().getStats();
                stats2.fatigue += inventoryItem.getFatigueChange();
            }
        }
    }
    
    public void JustAteFood(final Food food, final float n) {
        if (food.getPoisonPower() > 0) {
            float n2 = food.getPoisonPower() * n;
            if (this.getParentChar().Traits.IronGut.isSet()) {
                n2 /= 2.0f;
            }
            if (this.getParentChar().Traits.WeakStomach.isSet()) {
                n2 *= 2.0f;
            }
            this.PoisonLevel += n2;
            final Stats stats = this.ParentChar.getStats();
            stats.Pain += food.getPoisonPower() * n / 6.0f;
        }
        if (food.isTaintedWater()) {
            this.PoisonLevel += 20.0f * n;
            final Stats stats2 = this.ParentChar.getStats();
            stats2.Pain += 10.0f * n / 6.0f;
        }
        if (food.getReduceInfectionPower() > 0.0f) {
            this.getParentChar().setReduceInfectionPower(food.getReduceInfectionPower());
        }
        this.setBoredomLevel(this.getBoredomLevel() + food.getBoredomChange() * n);
        if (this.getBoredomLevel() < 0.0f) {
            this.setBoredomLevel(0.0f);
        }
        this.setUnhappynessLevel(this.getUnhappynessLevel() + food.getUnhappyChange() * n);
        if (this.getUnhappynessLevel() < 0.0f) {
            this.setUnhappynessLevel(0.0f);
        }
        if (food.isAlcoholic()) {
            this.JustDrankBooze(food, n);
        }
        if (this.getParentChar().getStats().hunger <= 0.0f) {
            final float n3 = Math.abs(food.getHungerChange()) * n;
            this.setHealthFromFoodTimer((float)(int)(this.getHealthFromFoodTimer() + n3 * this.getHealthFromFoodTimeByHunger()));
            if (food.isCooked()) {
                this.setHealthFromFoodTimer((float)(int)(this.getHealthFromFoodTimer() + n3 * this.getHealthFromFoodTimeByHunger()));
            }
            if (this.getHealthFromFoodTimer() > 11000.0f) {
                this.setHealthFromFoodTimer(11000.0f);
            }
        }
        if ("Tutorial".equals(Core.getInstance().getGameMode())) {
            return;
        }
        if (!food.isCooked() && food.isbDangerousUncooked()) {
            this.setHealthFromFoodTimer(0.0f);
            int n4 = 75;
            if (this.getParentChar().Traits.IronGut.isSet()) {
                n4 /= 2;
            }
            if (this.getParentChar().Traits.WeakStomach.isSet()) {
                n4 *= 2;
            }
            if (Rand.Next(100) < n4 && !this.isInfected()) {
                this.PoisonLevel += 15.0f * n;
            }
        }
        if (food.getAge() >= food.getOffAgeMax()) {
            float n5 = food.getAge() - food.getOffAgeMax();
            if (n5 == 0.0f) {
                n5 = 1.0f;
            }
            if (n5 > 5.0f) {
                n5 = 5.0f;
            }
            int n6;
            if (food.getOffAgeMax() > food.getOffAge()) {
                n6 = (int)(n5 / (food.getOffAgeMax() - food.getOffAge()) * 100.0f);
            }
            else {
                n6 = 100;
            }
            if (this.getParentChar().Traits.IronGut.isSet()) {
                n6 /= 2;
            }
            if (this.getParentChar().Traits.WeakStomach.isSet()) {
                n6 *= 2;
            }
            if (Rand.Next(100) < n6 && !this.isInfected()) {
                this.PoisonLevel += 5.0f * Math.abs(food.getHungChange() * 10.0f) * n;
            }
        }
    }
    
    public void JustAteFood(final Food food) {
        this.JustAteFood(food, 100.0f);
    }
    
    private float getHealthFromFoodTimeByHunger() {
        return 13000.0f;
    }
    
    public void JustReadSomething(final Literature literature) {
        this.setBoredomLevel(this.getBoredomLevel() + literature.getBoredomChange());
        if (this.getBoredomLevel() < 0.0f) {
            this.setBoredomLevel(0.0f);
        }
        this.setUnhappynessLevel(this.getUnhappynessLevel() + literature.getUnhappyChange());
        if (this.getUnhappynessLevel() < 0.0f) {
            this.setUnhappynessLevel(0.0f);
        }
    }
    
    public void JustTookPainMeds() {
        final Stats stats = this.ParentChar.getStats();
        stats.Pain -= this.getPainReductionFromMeds();
        if (this.getParentChar().getStats().Pain < 0.0f) {
            this.ParentChar.getStats().Pain = 0.0f;
        }
    }
    
    public void UpdateWetness() {
        final IsoGridSquare currentSquare = this.getParentChar().getCurrentSquare();
        final BaseVehicle vehicle = this.getParentChar().getVehicle();
        final IsoGameCharacter parentChar = this.getParentChar();
        int n = (currentSquare == null || (!currentSquare.isInARoom() && !currentSquare.haveRoof)) ? 1 : 0;
        if (vehicle != null && vehicle.hasRoof(vehicle.getSeat(this.getParentChar()))) {
            n = 0;
        }
        final ClothingWetness clothingWetness = this.getParentChar().getClothingWetness();
        float n2 = 0.0f;
        float n3 = 0.0f;
        float n4 = 0.0f;
        if (vehicle != null && ClimateManager.getInstance().isRaining()) {
            final VehiclePart partById = vehicle.getPartById("Windshield");
            if (partById != null) {
                final VehicleWindow window = partById.getWindow();
                if (window != null && window.isDestroyed()) {
                    final float rainIntensity = ClimateManager.getInstance().getRainIntensity();
                    float n5 = rainIntensity * rainIntensity * (vehicle.getCurrentSpeedKmHour() / 50.0f);
                    if (n5 < 0.1f) {
                        n5 = 0.0f;
                    }
                    if (n5 > 1.0f) {
                        n5 = 1.0f;
                    }
                    n4 = n5 * 3.0f;
                    n2 = n5;
                }
            }
        }
        if (n != 0 && parentChar.isAsleep() && parentChar.getBed() != null && "Tent".equals(parentChar.getBed().getName())) {
            n = 0;
        }
        if (n != 0 && ClimateManager.getInstance().isRaining()) {
            float rainIntensity2 = ClimateManager.getInstance().getRainIntensity();
            if (rainIntensity2 < 0.1) {
                rainIntensity2 = 0.0f;
            }
            n2 = rainIntensity2;
        }
        else if (n == 0 || !ClimateManager.getInstance().isRaining()) {
            final float airTemperatureForCharacter = ClimateManager.getInstance().getAirTemperatureForCharacter(this.getParentChar());
            float n6 = 0.1f;
            if (airTemperatureForCharacter > 5.0f) {
                n6 += (airTemperatureForCharacter - 5.0f) / 10.0f;
            }
            float n7 = n6 - n4;
            if (n7 < 0.0f) {
                n7 = 0.0f;
            }
            n3 = n7;
        }
        if (clothingWetness != null) {
            clothingWetness.updateWetness(n2, n3);
        }
        float n8 = 0.0f;
        if (this.BodyParts.size() > 0) {
            for (int i = 0; i < this.BodyParts.size(); ++i) {
                n8 += this.BodyParts.get(i).getWetness();
            }
            n8 /= this.BodyParts.size();
        }
        this.Wetness = PZMath.clamp(n8, 0.0f, 100.0f);
        float catchAColdDelta = 0.0f;
        if (this.thermoregulator != null) {
            catchAColdDelta = this.thermoregulator.getCatchAColdDelta();
        }
        if (!this.isHasACold() && catchAColdDelta > 0.1f) {
            if (this.getParentChar().Traits.ProneToIllness.isSet()) {
                catchAColdDelta *= 1.7f;
            }
            if (this.getParentChar().Traits.Resilient.isSet()) {
                catchAColdDelta *= 0.45f;
            }
            if (this.getParentChar().Traits.Outdoorsman.isSet()) {
                catchAColdDelta *= 0.1f;
            }
            this.setCatchACold(this.getCatchACold() + (float)ZomboidGlobals.CatchAColdIncreaseRate * catchAColdDelta * GameTime.instance.getMultiplier());
            if (this.getCatchACold() >= 100.0f) {
                this.setCatchACold(0.0f);
                this.setHasACold(true);
                this.setColdStrength(20.0f);
                this.setTimeToSneezeOrCough(0);
            }
        }
        if (catchAColdDelta <= 0.1f) {
            this.setCatchACold(this.getCatchACold() - (float)ZomboidGlobals.CatchAColdDecreaseRate);
            if (this.getCatchACold() <= 0.0f) {
                this.setCatchACold(0.0f);
            }
        }
    }
    
    public void TriggerSneezeCough() {
        if (this.getSneezeCoughActive() > 0) {
            return;
        }
        if (Rand.Next(100) > 50) {
            this.setSneezeCoughActive(1);
        }
        else {
            this.setSneezeCoughActive(2);
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) == 2) {
            this.setSneezeCoughActive(1);
        }
        this.setSneezeCoughTime(this.getSneezeCoughDelay());
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) == 2) {
            this.setTimeToSneezeOrCough(this.getMildColdSneezeTimerMin() + Rand.Next(this.getMildColdSneezeTimerMax() - this.getMildColdSneezeTimerMin()));
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) == 3) {
            this.setTimeToSneezeOrCough(this.getColdSneezeTimerMin() + Rand.Next(this.getColdSneezeTimerMax() - this.getColdSneezeTimerMin()));
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) == 4) {
            this.setTimeToSneezeOrCough(this.getNastyColdSneezeTimerMin() + Rand.Next(this.getNastyColdSneezeTimerMax() - this.getNastyColdSneezeTimerMin()));
        }
        boolean b = false;
        if (this.getParentChar().getPrimaryHandItem() != null && (this.getParentChar().getPrimaryHandItem().getType().equals("Tissue") || this.getParentChar().getPrimaryHandItem().getType().equals("ToiletPaper"))) {
            if (((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() > 0.0f) {
                ((Drainable)this.getParentChar().getPrimaryHandItem()).setUsedDelta(((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() - 0.1f);
                if (((Drainable)this.getParentChar().getPrimaryHandItem()).getUsedDelta() <= 0.0f) {
                    this.getParentChar().getPrimaryHandItem().Use();
                }
                b = true;
            }
        }
        else if (this.getParentChar().getSecondaryHandItem() != null && (this.getParentChar().getSecondaryHandItem().getType().equals("Tissue") || this.getParentChar().getSecondaryHandItem().getType().equals("ToiletPaper")) && ((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() > 0.0f) {
            ((Drainable)this.getParentChar().getSecondaryHandItem()).setUsedDelta(((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() - 0.1f);
            if (((Drainable)this.getParentChar().getSecondaryHandItem()).getUsedDelta() <= 0.0f) {
                this.getParentChar().getSecondaryHandItem().Use();
            }
            b = true;
        }
        if (b) {
            this.setSneezeCoughActive(this.getSneezeCoughActive() + 2);
        }
        else {
            int n = 20;
            int n2 = 20;
            if (this.getSneezeCoughActive() == 1) {
                n = 20;
                n2 = 25;
            }
            if (this.getSneezeCoughActive() == 2) {
                n = 35;
                n2 = 40;
            }
            WorldSoundManager.instance.addSound(this.getParentChar(), (int)this.getParentChar().getX(), (int)this.getParentChar().getY(), (int)this.getParentChar().getZ(), n, n2, true);
        }
    }
    
    public int IsSneezingCoughing() {
        return this.getSneezeCoughActive();
    }
    
    public void UpdateCold() {
        if (this.isHasACold()) {
            boolean b = true;
            final IsoGridSquare currentSquare = this.getParentChar().getCurrentSquare();
            if (currentSquare == null || !currentSquare.isInARoom() || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Wet) > 0 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hypothermia) >= 1 || this.getParentChar().getStats().fatigue > 0.5f || this.getParentChar().getStats().hunger > 0.25f || this.getParentChar().getStats().thirst > 0.25f) {
                b = false;
            }
            if (this.getColdReduction() > 0.0f) {
                b = true;
                this.setColdReduction(this.getColdReduction() - 0.005f * GameTime.instance.getMultiplier());
                if (this.getColdReduction() < 0.0f) {
                    this.setColdReduction(0.0f);
                }
            }
            if (b) {
                float n = 1.0f;
                if (this.getParentChar().Traits.ProneToIllness.isSet()) {
                    n = 0.5f;
                }
                if (this.getParentChar().Traits.Resilient.isSet()) {
                    n = 1.5f;
                }
                this.setColdStrength(this.getColdStrength() - this.getColdProgressionRate() * n * GameTime.instance.getMultiplier());
                if (this.getColdReduction() > 0.0f) {
                    this.setColdStrength(this.getColdStrength() - this.getColdProgressionRate() * n * GameTime.instance.getMultiplier());
                }
                if (this.getColdStrength() < 0.0f) {
                    this.setColdStrength(0.0f);
                    this.setHasACold(false);
                    this.setCatchACold(0.0f);
                }
            }
            else {
                float n2 = 1.0f;
                if (this.getParentChar().Traits.ProneToIllness.isSet()) {
                    n2 = 1.2f;
                }
                if (this.getParentChar().Traits.Resilient.isSet()) {
                    n2 = 0.8f;
                }
                this.setColdStrength(this.getColdStrength() + this.getColdProgressionRate() * n2 * GameTime.instance.getMultiplier());
                if (this.getColdStrength() > 100.0f) {
                    this.setColdStrength(100.0f);
                }
            }
            if (this.getSneezeCoughTime() > 0) {
                this.setSneezeCoughTime(this.getSneezeCoughTime() - 1);
                if (this.getSneezeCoughTime() == 0) {
                    this.setSneezeCoughActive(0);
                }
            }
            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HasACold) > 1 && this.getTimeToSneezeOrCough() >= 0 && !this.ParentChar.IsSpeaking()) {
                this.setTimeToSneezeOrCough(this.getTimeToSneezeOrCough() - 1);
                if (this.getTimeToSneezeOrCough() <= 0) {
                    this.TriggerSneezeCough();
                }
            }
        }
    }
    
    public float getColdStrength() {
        if (this.isHasACold()) {
            return this.ColdStrength;
        }
        return 0.0f;
    }
    
    public float getWetness() {
        return this.Wetness;
    }
    
    public void AddDamage(final BodyPartType bodyPartType, final float n) {
        this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).AddDamage(n);
    }
    
    public void AddGeneralHealth(final float n) {
        int n2 = 0;
        for (int i = 0; i < BodyPartType.ToIndex(BodyPartType.MAX); ++i) {
            if (this.getBodyParts().get(i).getHealth() < 100.0f) {
                ++n2;
            }
        }
        if (n2 > 0) {
            final float n3 = n / n2;
            for (int j = 0; j < BodyPartType.ToIndex(BodyPartType.MAX); ++j) {
                if (this.getBodyParts().get(j).getHealth() < 100.0f) {
                    this.getBodyParts().get(j).AddHealth(n3);
                }
            }
        }
    }
    
    public void ReduceGeneralHealth(final float n) {
        if (this.getOverallBodyHealth() <= 10.0f) {
            this.getParentChar().forceAwake();
        }
        if (n <= 0.0f) {
            return;
        }
        final float n2 = n / BodyPartType.ToIndex(BodyPartType.MAX);
        for (int i = 0; i < BodyPartType.ToIndex(BodyPartType.MAX); ++i) {
            this.getBodyParts().get(i).ReduceHealth(n2 / BodyPartType.getDamageModifyer(i));
        }
    }
    
    public void AddDamage(final int index, final float n) {
        this.getBodyParts().get(index).AddDamage(n);
    }
    
    public void splatBloodFloorBig() {
        this.getParentChar().splatBloodFloorBig();
        this.getParentChar().splatBloodFloorBig();
        this.getParentChar().splatBloodFloorBig();
    }
    
    public void DamageFromWeapon(final HandWeapon handWeapon) {
        if (GameServer.bServer) {
            if (handWeapon != null) {
                this.getParentChar().sendObjectChange("DamageFromWeapon", new Object[] { "weapon", handWeapon.getFullType() });
            }
            return;
        }
        if (this.getParentChar() instanceof IsoPlayer && !((IsoPlayer)this.getParentChar()).isLocalPlayer()) {
            return;
        }
        boolean b = true;
        int n = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.MAX));
        if (DebugOptions.instance.MultiplayerTorsoHit.getValue()) {
            n = Rand.Next(BodyPartType.ToIndex(BodyPartType.Torso_Upper), BodyPartType.ToIndex(BodyPartType.Head));
        }
        boolean b2 = false;
        boolean b3 = false;
        boolean b4 = false;
        int n2;
        if (handWeapon.getCategories().contains("Blunt") || handWeapon.getCategories().contains("SmallBlunt")) {
            n2 = 0;
            b2 = true;
        }
        else if (!handWeapon.isAimedFirearm()) {
            n2 = 1;
            b3 = true;
        }
        else {
            b4 = true;
            n2 = 2;
        }
        final BodyPart bodyPart = this.getBodyPart(BodyPartType.FromIndex(n));
        if (Rand.Next(100) < this.getParentChar().getBodyPartClothingDefense(bodyPart.getIndex(), b3, b4)) {
            b = false;
            this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(n), false);
        }
        if (!b) {
            return;
        }
        this.getParentChar().addHole(BloodBodyPartType.FromIndex(n));
        this.getParentChar().splatBloodFloorBig();
        this.getParentChar().splatBloodFloorBig();
        this.getParentChar().splatBloodFloorBig();
        if (b3) {
            if (Rand.NextBool(6)) {
                bodyPart.generateDeepWound();
            }
            else if (Rand.NextBool(3)) {
                bodyPart.setCut(true);
            }
            else {
                bodyPart.setScratched(true, true);
            }
        }
        else if (b2) {
            if (Rand.NextBool(4)) {
                bodyPart.setCut(true);
            }
            else {
                bodyPart.setScratched(true, true);
            }
        }
        else if (b4) {
            bodyPart.setHaveBullet(true, 0);
        }
        float n3 = Rand.Next(handWeapon.getMinDamage(), handWeapon.getMaxDamage()) * 15.0f;
        if (n == BodyPartType.ToIndex(BodyPartType.Head)) {
            n3 *= 4.0f;
        }
        if (n == BodyPartType.ToIndex(BodyPartType.Neck)) {
            n3 *= 4.0f;
        }
        if (n == BodyPartType.ToIndex(BodyPartType.Torso_Upper)) {
            n3 *= 2.0f;
        }
        if (GameClient.bClient) {
            if (handWeapon.isRanged()) {
                n3 *= (float)ServerOptions.getInstance().PVPFirearmDamageModifier.getValue();
            }
            else {
                n3 *= (float)ServerOptions.getInstance().PVPMeleeDamageModifier.getValue();
            }
        }
        this.AddDamage(n, n3);
        switch (n2) {
            case 0: {
                final Stats stats = this.ParentChar.getStats();
                stats.Pain += this.getInitialThumpPain() * BodyPartType.getPainModifyer(n);
                break;
            }
            case 1: {
                final Stats stats2 = this.ParentChar.getStats();
                stats2.Pain += this.getInitialScratchPain() * BodyPartType.getPainModifyer(n);
                break;
            }
            case 2: {
                final Stats stats3 = this.ParentChar.getStats();
                stats3.Pain += this.getInitialBitePain() * BodyPartType.getPainModifyer(n);
                break;
            }
        }
        if (this.getParentChar().getStats().Pain > 100.0f) {
            this.ParentChar.getStats().Pain = 100.0f;
        }
        if (this.ParentChar instanceof IsoPlayer && GameClient.bClient && ((IsoPlayer)this.ParentChar).isLocalPlayer()) {
            final IsoPlayer isoPlayer = (IsoPlayer)this.ParentChar;
            isoPlayer.updateMovementRates();
            GameClient.sendPlayerInjuries(isoPlayer);
            GameClient.sendPlayerDamage(isoPlayer);
        }
    }
    
    public boolean AddRandomDamageFromZombie(final IsoZombie isoZombie, String hitReaction) {
        if (StringUtils.isNullOrEmpty(hitReaction)) {
            hitReaction = "Bite";
        }
        this.getParentChar().setVariable("hitpvp", false);
        if (GameServer.bServer) {
            this.getParentChar().sendObjectChange("AddRandomDamageFromZombie", new Object[] { "zombie", isoZombie.OnlineID });
            return true;
        }
        int n = 0;
        final int n2 = 15 + this.getParentChar().getMeleeCombatMod();
        final int n3 = 85;
        final int n4 = 65;
        final String testDotSide = this.getParentChar().testDotSide(isoZombie);
        final boolean equals = testDotSide.equals(BodyDamage.behindStr);
        final boolean b = testDotSide.equals(BodyDamage.leftStr) || testDotSide.equals(BodyDamage.rightStr);
        final int max = Math.max(this.getParentChar().getSurroundingAttackingZombies(), 1);
        int n5 = n2 - (max - 1) * 10;
        int n6 = n3 - (max - 1) * 30;
        int n7 = n4 - (max - 1) * 15;
        int n8 = 3;
        if (SandboxOptions.instance.Lore.Strength.getValue() == 1) {
            n8 = 2;
        }
        if (SandboxOptions.instance.Lore.Strength.getValue() == 3) {
            n8 = 6;
        }
        if (this.ParentChar.Traits.ThickSkinned.isSet()) {
            n5 *= (int)1.3;
        }
        if (this.ParentChar.Traits.ThinSkinned.isSet()) {
            n5 /= (int)1.3;
        }
        if (!"EndDeath".equals(this.getParentChar().getHitReaction())) {
            if (!this.getParentChar().isGodMod() && max >= n8 && SandboxOptions.instance.Lore.ZombiesDragDown.getValue() && !this.getParentChar().isSitOnGround()) {
                n6 = 0;
                n7 = 0;
                n5 = 0;
                this.getParentChar().setHitReaction("EndDeath");
                this.getParentChar().setDeathDragDown(true);
            }
            else {
                this.getParentChar().setHitReaction(hitReaction);
            }
        }
        if (equals) {
            n5 -= 15;
            n6 -= 25;
            n7 -= 35;
            if (SandboxOptions.instance.RearVulnerability.getValue() == 1) {
                n5 += 15;
                n6 += 25;
                n7 += 35;
            }
            if (SandboxOptions.instance.RearVulnerability.getValue() == 2) {
                n5 += 7;
                n6 += 17;
                n7 += 23;
            }
            if (max > 2) {
                n6 -= 15;
                n7 -= 15;
            }
        }
        if (b) {
            n5 -= 30;
            n6 -= 7;
            n7 -= 27;
            if (SandboxOptions.instance.RearVulnerability.getValue() == 1) {
                n5 += 30;
                n6 += 7;
                n7 += 27;
            }
            if (SandboxOptions.instance.RearVulnerability.getValue() == 2) {
                n5 += 15;
                n6 += 4;
                n7 += 15;
            }
        }
        int i;
        if (!isoZombie.bCrawling) {
            i = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.Groin) + 1);
            float n9 = 10.0f * max;
            if (equals) {
                n9 += 5.0f;
            }
            if (b) {
                n9 += 2.0f;
            }
            if (equals && Rand.Next(100) < n9) {
                i = BodyPartType.ToIndex(BodyPartType.Neck);
            }
            if (i == BodyPartType.ToIndex(BodyPartType.Head) || i == BodyPartType.ToIndex(BodyPartType.Neck)) {
                int n10 = 70;
                if (equals) {
                    n10 = 90;
                }
                if (b) {
                    n10 = 80;
                }
                if (Rand.Next(100) > n10) {
                    for (int j = 0; j == 0; j = 0) {
                        j = 1;
                        i = Rand.Next(BodyPartType.ToIndex(BodyPartType.MAX));
                        if (i == BodyPartType.ToIndex(BodyPartType.Head) || i == BodyPartType.ToIndex(BodyPartType.Neck)) {}
                    }
                }
            }
        }
        else {
            if (Rand.Next(2) != 0) {
                return false;
            }
            i = Rand.Next(BodyPartType.ToIndex(BodyPartType.UpperLeg_L), BodyPartType.ToIndex(BodyPartType.MAX));
        }
        if (isoZombie.inactive) {
            n5 += 20;
            n6 += 20;
            n7 += 20;
        }
        final float n11 = Rand.Next(1000) / 1000.0f * (Rand.Next(10) + 10);
        if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
            DebugLog.log(DebugType.Combat, invokedynamic(makeConcatWithConstants:(FLjava/lang/String;)Ljava/lang/String;, n11, ((IsoPlayer)this.ParentChar).getDisplayName()));
        }
        boolean b2 = false;
        boolean b3 = true;
        if (Rand.Next(100) > n5) {
            isoZombie.scratch = true;
            this.getParentChar().helmetFall(i == BodyPartType.ToIndex(BodyPartType.Neck) || i == BodyPartType.ToIndex(BodyPartType.Head));
            if (Rand.Next(100) > n7) {
                isoZombie.scratch = false;
                isoZombie.laceration = true;
            }
            if (Rand.Next(100) > n6) {
                isoZombie.scratch = false;
                isoZombie.laceration = false;
                b3 = false;
            }
            if (isoZombie.scratch) {
                final Float value = this.getParentChar().getBodyPartClothingDefense(i, false, false);
                isoZombie.parameterZombieState.setState(ParameterZombieState.State.AttackScratch);
                if (this.getHealth() > 0.0f) {
                    this.getParentChar().getEmitter().playSound("ZombieScratch");
                }
                if (Rand.Next(100) < value) {
                    this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(i), b3);
                    return false;
                }
                this.getParentChar().addHole(BloodBodyPartType.FromIndex(i), true);
                b2 = true;
                this.AddDamage(i, n11);
                this.SetScratched(i, true);
                this.getParentChar().addBlood(BloodBodyPartType.FromIndex(i), true, false, true);
                n = 1;
                if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
                    DebugLog.log(DebugType.Combat, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ((IsoPlayer)this.ParentChar).username));
                }
            }
            else if (isoZombie.laceration) {
                final Float value2 = this.getParentChar().getBodyPartClothingDefense(i, false, false);
                isoZombie.parameterZombieState.setState(ParameterZombieState.State.AttackLacerate);
                if (this.getHealth() > 0.0f) {
                    this.getParentChar().getEmitter().playSound("ZombieScratch");
                }
                if (Rand.Next(100) < value2) {
                    this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(i), b3);
                    return false;
                }
                this.getParentChar().addHole(BloodBodyPartType.FromIndex(i), true);
                b2 = true;
                this.AddDamage(i, n11);
                this.SetCut(i, true);
                this.getParentChar().addBlood(BloodBodyPartType.FromIndex(i), true, false, true);
                n = 1;
                if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
                    DebugLog.log(DebugType.Combat, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ((IsoPlayer)this.ParentChar).username));
                }
            }
            else {
                final Float value3 = this.getParentChar().getBodyPartClothingDefense(i, true, false);
                isoZombie.parameterZombieState.setState(ParameterZombieState.State.AttackBite);
                if (this.getHealth() > 0.0f) {
                    this.getParentChar().getEmitter().playSound("ZombieBite");
                }
                if (Rand.Next(100) < value3) {
                    this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(i), b3);
                    return false;
                }
                this.getParentChar().addHole(BloodBodyPartType.FromIndex(i), true);
                b2 = true;
                this.AddDamage(i, n11);
                this.SetBitten(i, true);
                if (i == BodyPartType.ToIndex(BodyPartType.Neck)) {
                    this.getParentChar().addBlood(BloodBodyPartType.FromIndex(i), false, true, true);
                    this.getParentChar().addBlood(BloodBodyPartType.FromIndex(i), false, true, true);
                    this.getParentChar().addBlood(BloodBodyPartType.Torso_Upper, false, true, false);
                }
                this.getParentChar().addBlood(BloodBodyPartType.FromIndex(i), false, true, true);
                if (GameServer.bServer && this.ParentChar instanceof IsoPlayer) {
                    DebugLog.log(DebugType.Combat, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ((IsoPlayer)this.ParentChar).username));
                }
                n = 2;
                this.getParentChar().splatBloodFloorBig();
                this.getParentChar().splatBloodFloorBig();
                this.getParentChar().splatBloodFloorBig();
            }
        }
        if (!b2) {
            this.getParentChar().addHoleFromZombieAttacks(BloodBodyPartType.FromIndex(i), b3);
        }
        switch (n) {
            case 0: {
                final Stats stats = this.ParentChar.getStats();
                stats.Pain += this.getInitialThumpPain() * BodyPartType.getPainModifyer(i);
                break;
            }
            case 1: {
                final Stats stats2 = this.ParentChar.getStats();
                stats2.Pain += this.getInitialScratchPain() * BodyPartType.getPainModifyer(i);
                break;
            }
            case 2: {
                final Stats stats3 = this.ParentChar.getStats();
                stats3.Pain += this.getInitialBitePain() * BodyPartType.getPainModifyer(i);
                break;
            }
        }
        if (this.getParentChar().getStats().Pain > 100.0f) {
            this.ParentChar.getStats().Pain = 100.0f;
        }
        if (GameClient.bClient && ServerOptions.instance.PlayerSaveOnDamage.getValue()) {
            GameWindow.savePlayer();
        }
        if (this.ParentChar instanceof IsoPlayer && GameClient.bClient && ((IsoPlayer)this.ParentChar).isLocalPlayer()) {
            final IsoPlayer isoPlayer = (IsoPlayer)this.ParentChar;
            isoPlayer.updateMovementRates();
            GameClient.sendPlayerInjuries(isoPlayer);
            GameClient.sendPlayerDamage(isoPlayer);
        }
        return true;
    }
    
    public boolean doesBodyPartHaveInjury(final BodyPartType bodyPartType) {
        return this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).HasInjury();
    }
    
    public boolean doBodyPartsHaveInjuries(final BodyPartType bodyPartType, final BodyPartType bodyPartType2) {
        return this.doesBodyPartHaveInjury(bodyPartType) || this.doesBodyPartHaveInjury(bodyPartType2);
    }
    
    public boolean isBodyPartBleeding(final BodyPartType bodyPartType) {
        return this.getBodyPart(bodyPartType).getBleedingTime() > 0.0f;
    }
    
    public boolean areBodyPartsBleeding(final BodyPartType bodyPartType, final BodyPartType bodyPartType2) {
        return this.isBodyPartBleeding(bodyPartType) || this.isBodyPartBleeding(bodyPartType2);
    }
    
    public void DrawUntexturedQuad(final int n, final int n2, final int n3, final int n4, final float n5, final float n6, final float n7, final float n8) {
        SpriteRenderer.instance.renderi(null, n, n2, n3, n4, n5, n6, n7, n8, null);
    }
    
    public float getBodyPartHealth(final BodyPartType bodyPartType) {
        return this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).getHealth();
    }
    
    public float getBodyPartHealth(final int index) {
        return this.getBodyParts().get(index).getHealth();
    }
    
    public String getBodyPartName(final BodyPartType bodyPartType) {
        return BodyPartType.ToString(bodyPartType);
    }
    
    public String getBodyPartName(final int n) {
        return BodyPartType.ToString(BodyPartType.FromIndex(n));
    }
    
    public float getHealth() {
        return this.getOverallBodyHealth();
    }
    
    public float getInfectionLevel() {
        return this.InfectionLevel;
    }
    
    public float getApparentInfectionLevel() {
        final float n = (this.getFakeInfectionLevel() > this.InfectionLevel) ? this.getFakeInfectionLevel() : this.InfectionLevel;
        if (this.getFoodSicknessLevel() > n) {
            return this.getFoodSicknessLevel();
        }
        return n;
    }
    
    public int getNumPartsBleeding() {
        int n = 0;
        for (int i = 0; i < BodyPartType.ToIndex(BodyPartType.MAX); ++i) {
            if (this.getBodyParts().get(i).bleeding()) {
                ++n;
            }
        }
        return n;
    }
    
    public int getNumPartsScratched() {
        int n = 0;
        for (int i = 0; i < BodyPartType.ToIndex(BodyPartType.MAX); ++i) {
            if (this.getBodyParts().get(i).scratched()) {
                ++n;
            }
        }
        return n;
    }
    
    public int getNumPartsBitten() {
        int n = 0;
        for (int i = 0; i < BodyPartType.ToIndex(BodyPartType.MAX); ++i) {
            if (this.getBodyParts().get(i).bitten()) {
                ++n;
            }
        }
        return n;
    }
    
    public boolean HasInjury() {
        for (int i = 0; i < BodyPartType.ToIndex(BodyPartType.MAX); ++i) {
            if (this.getBodyParts().get(i).HasInjury()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean IsBandaged(final BodyPartType bodyPartType) {
        return this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).bandaged();
    }
    
    public boolean IsDeepWounded(final BodyPartType bodyPartType) {
        return this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).deepWounded();
    }
    
    public boolean IsBandaged(final int index) {
        return this.getBodyParts().get(index).bandaged();
    }
    
    public boolean IsBitten(final BodyPartType bodyPartType) {
        return this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).bitten();
    }
    
    public boolean IsBitten(final int index) {
        return this.getBodyParts().get(index).bitten();
    }
    
    public boolean IsBleeding(final BodyPartType bodyPartType) {
        return this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).bleeding();
    }
    
    public boolean IsBleeding(final int index) {
        return this.getBodyParts().get(index).bleeding();
    }
    
    public boolean IsBleedingStemmed(final BodyPartType bodyPartType) {
        return this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).IsBleedingStemmed();
    }
    
    public boolean IsBleedingStemmed(final int index) {
        return this.getBodyParts().get(index).IsBleedingStemmed();
    }
    
    public boolean IsCortorised(final BodyPartType bodyPartType) {
        return this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).IsCortorised();
    }
    
    public boolean IsCortorised(final int index) {
        return this.getBodyParts().get(index).IsCortorised();
    }
    
    public boolean IsInfected() {
        return this.IsInfected;
    }
    
    public boolean IsInfected(final BodyPartType bodyPartType) {
        return this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).IsInfected();
    }
    
    public boolean IsInfected(final int index) {
        return this.getBodyParts().get(index).IsInfected();
    }
    
    public boolean IsFakeInfected(final int index) {
        return this.getBodyParts().get(index).IsFakeInfected();
    }
    
    public void DisableFakeInfection(final int index) {
        this.getBodyParts().get(index).DisableFakeInfection();
    }
    
    public boolean IsScratched(final BodyPartType bodyPartType) {
        return this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).scratched();
    }
    
    public boolean IsCut(final BodyPartType bodyPartType) {
        return this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).getCutTime() > 0.0f;
    }
    
    public boolean IsScratched(final int index) {
        return this.getBodyParts().get(index).scratched();
    }
    
    public boolean IsStitched(final BodyPartType bodyPartType) {
        return this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).stitched();
    }
    
    public boolean IsStitched(final int index) {
        return this.getBodyParts().get(index).stitched();
    }
    
    public boolean IsWounded(final BodyPartType bodyPartType) {
        return this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).deepWounded();
    }
    
    public boolean IsWounded(final int index) {
        return this.getBodyParts().get(index).deepWounded();
    }
    
    public void RestoreToFullHealth() {
        for (int i = 0; i < BodyPartType.ToIndex(BodyPartType.MAX); ++i) {
            this.getBodyParts().get(i).RestoreToFullHealth();
        }
        if (this.getParentChar() != null && this.getParentChar().getStats() != null) {
            this.getParentChar().getStats().resetStats();
        }
        this.setInfected(false);
        this.setIsFakeInfected(false);
        this.setOverallBodyHealth(100.0f);
        this.setInfectionLevel(0.0f);
        this.setFakeInfectionLevel(0.0f);
        this.setBoredomLevel(0.0f);
        this.setWetness(0.0f);
        this.setCatchACold(0.0f);
        this.setHasACold(false);
        this.setColdStrength(0.0f);
        this.setSneezeCoughActive(0);
        this.setSneezeCoughTime(0);
        this.setTemperature(37.0f);
        this.setUnhappynessLevel(0.0f);
        this.setFoodSicknessLevel(this.PoisonLevel = 0.0f);
        this.Temperature = 37.0f;
        this.lastTemperature = this.Temperature;
        this.setInfectionTime(-1.0f);
        this.setInfectionMortalityDuration(-1.0f);
        if (this.thermoregulator != null) {
            this.thermoregulator.reset();
        }
    }
    
    public void SetBandaged(final int index, final boolean b, final float n, final boolean b2, final String s) {
        this.getBodyParts().get(index).setBandaged(b, n, b2, s);
    }
    
    public void SetBitten(final BodyPartType bodyPartType, final boolean b) {
        this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).SetBitten(b);
    }
    
    public void SetBitten(final int index, final boolean b) {
        this.getBodyParts().get(index).SetBitten(b);
    }
    
    public void SetBitten(final int index, final boolean b, final boolean b2) {
        this.getBodyParts().get(index).SetBitten(b, b2);
    }
    
    public void SetBleeding(final BodyPartType bodyPartType, final boolean bleeding) {
        this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).setBleeding(bleeding);
    }
    
    public void SetBleeding(final int index, final boolean bleeding) {
        this.getBodyParts().get(index).setBleeding(bleeding);
    }
    
    public void SetBleedingStemmed(final BodyPartType bodyPartType, final boolean b) {
        this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).SetBleedingStemmed(b);
    }
    
    public void SetBleedingStemmed(final int index, final boolean b) {
        this.getBodyParts().get(index).SetBleedingStemmed(b);
    }
    
    public void SetCortorised(final BodyPartType bodyPartType, final boolean b) {
        this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).SetCortorised(b);
    }
    
    public void SetCortorised(final int index, final boolean b) {
        this.getBodyParts().get(index).SetCortorised(b);
    }
    
    public BodyPart setScratchedWindow() {
        final int next = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.ForeArm_R) + 1);
        this.getBodyPart(BodyPartType.FromIndex(next)).AddDamage(10.0f);
        this.getBodyPart(BodyPartType.FromIndex(next)).SetScratchedWindow(true);
        return this.getBodyPart(BodyPartType.FromIndex(next));
    }
    
    public void SetScratched(final BodyPartType bodyPartType, final boolean b) {
        this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).setScratched(b, false);
    }
    
    public void SetScratched(final int index, final boolean b) {
        this.getBodyParts().get(index).setScratched(b, false);
    }
    
    public void SetScratchedFromWeapon(final int index, final boolean b) {
        this.getBodyParts().get(index).SetScratchedWeapon(b);
    }
    
    public void SetCut(final int index, final boolean b) {
        this.getBodyParts().get(index).setCut(b, false);
    }
    
    public void SetWounded(final BodyPartType bodyPartType, final boolean deepWounded) {
        this.getBodyParts().get(BodyPartType.ToIndex(bodyPartType)).setDeepWounded(deepWounded);
    }
    
    public void SetWounded(final int index, final boolean deepWounded) {
        this.getBodyParts().get(index).setDeepWounded(deepWounded);
    }
    
    public void ShowDebugInfo() {
        if (this.getDamageModCount() > 0) {
            this.setDamageModCount(this.getDamageModCount() - 1);
        }
    }
    
    public void UpdateBoredom() {
        if (this.getParentChar() instanceof IsoSurvivor) {
            return;
        }
        if (this.getParentChar() instanceof IsoPlayer && ((IsoPlayer)this.getParentChar()).Asleep) {
            return;
        }
        if (this.getParentChar().getCurrentSquare().getRoom() != null) {
            if (!this.getParentChar().isReading()) {
                this.setBoredomLevel((float)(this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate * GameTime.instance.getMultiplier()));
            }
            else {
                this.setBoredomLevel((float)(this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate / 5.0 * GameTime.instance.getMultiplier()));
            }
            if (this.getParentChar().IsSpeaking() && !this.getParentChar().callOut) {
                this.setBoredomLevel((float)(this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * GameTime.instance.getMultiplier()));
            }
            if (this.getParentChar().getNumSurvivorsInVicinity() > 0) {
                this.setBoredomLevel((float)(this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 0.10000000149011612 * GameTime.instance.getMultiplier()));
            }
        }
        else if (this.getParentChar().getVehicle() != null) {
            if (Math.abs(this.getParentChar().getVehicle().getCurrentSpeedKmHour()) <= 0.1f) {
                if (this.getParentChar().isReading()) {
                    this.setBoredomLevel((float)(this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate / 5.0 * GameTime.instance.getMultiplier()));
                }
                else {
                    this.setBoredomLevel((float)(this.getBoredomLevel() + ZomboidGlobals.BoredomIncreaseRate * GameTime.instance.getMultiplier()));
                }
            }
            else {
                this.setBoredomLevel((float)(this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 0.5 * GameTime.instance.getMultiplier()));
            }
        }
        else {
            this.setBoredomLevel((float)(this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 0.10000000149011612 * GameTime.instance.getMultiplier()));
        }
        if (this.getParentChar().getStats().Drunkenness > 20.0f) {
            this.setBoredomLevel((float)(this.getBoredomLevel() - ZomboidGlobals.BoredomDecreaseRate * 2.0 * GameTime.instance.getMultiplier()));
        }
        if (this.getParentChar().getStats().Panic > 5.0f) {
            this.setBoredomLevel(0.0f);
        }
        if (this.getBoredomLevel() > 100.0f) {
            this.setBoredomLevel(100.0f);
        }
        if (this.getBoredomLevel() < 0.0f) {
            this.setBoredomLevel(0.0f);
        }
        if (this.getUnhappynessLevel() > 100.0f) {
            this.setUnhappynessLevel(100.0f);
        }
        if (this.getUnhappynessLevel() < 0.0f) {
            this.setUnhappynessLevel(0.0f);
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bored) > 1 && !this.getParentChar().isReading()) {
            this.setUnhappynessLevel((float)(this.getUnhappynessLevel() + ZomboidGlobals.UnhappinessIncrease * (float)this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bored) * GameTime.instance.getMultiplier()));
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Stress) > 1 && !this.getParentChar().isReading()) {
            this.setUnhappynessLevel((float)(this.getUnhappynessLevel() + ZomboidGlobals.UnhappinessIncrease / 2.0 * (float)this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Stress) * GameTime.instance.getMultiplier()));
        }
        if (this.getParentChar().Traits.Smoker.isSet()) {
            this.getParentChar().setTimeSinceLastSmoke(this.getParentChar().getTimeSinceLastSmoke() + 1.0E-4f * GameTime.instance.getMultiplier());
            if (this.getParentChar().getTimeSinceLastSmoke() > 1.0f) {
                double n = Math.floor(this.getParentChar().getTimeSinceLastSmoke() / 10.0f) + 1.0;
                if (n > 10.0) {
                    n = 10.0;
                }
                this.getParentChar().getStats().setStressFromCigarettes((float)(this.getParentChar().getStats().getStressFromCigarettes() + ZomboidGlobals.StressFromBiteOrScratch / 8.0 * n * GameTime.instance.getMultiplier()));
            }
        }
    }
    
    public float getUnhappynessLevel() {
        return this.UnhappynessLevel;
    }
    
    public float getBoredomLevel() {
        return this.BoredomLevel;
    }
    
    public void UpdateStrength() {
        if (this.getParentChar() != this.getParentChar()) {
            return;
        }
        int n = 0;
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 2) {
            ++n;
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 3) {
            n += 2;
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4) {
            n += 2;
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 2) {
            ++n;
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 3) {
            n += 2;
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4) {
            n += 2;
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 2) {
            ++n;
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 3) {
            n += 2;
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 4) {
            n += 3;
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 2) {
            ++n;
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 3) {
            ++n;
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 4) {
            ++n;
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Injured) == 2) {
            ++n;
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Injured) == 3) {
            n += 2;
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Injured) == 4) {
            n += 3;
        }
        this.getParentChar().setMaxWeight((int)(this.getParentChar().getMaxWeightBase() * this.getParentChar().getWeightMod()) - n);
        if (this.getParentChar().getMaxWeight() < 0) {
            this.getParentChar().setMaxWeight(0);
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.FoodEaten) > 0) {
            this.getParentChar().setMaxWeight(this.getParentChar().getMaxWeight() + 2);
        }
        if (this.getParentChar() instanceof IsoPlayer) {
            this.getParentChar().setMaxWeight((int)(this.getParentChar().getMaxWeight() * ((IsoPlayer)this.getParentChar()).getMaxWeightDelta()));
        }
    }
    
    public float pickMortalityDuration() {
        float n = 1.0f;
        if (this.getParentChar().Traits.Resilient.isSet()) {
            n = 1.25f;
        }
        if (this.getParentChar().Traits.ProneToIllness.isSet()) {
            n = 0.75f;
        }
        switch (SandboxOptions.instance.Lore.Mortality.getValue()) {
            case 1: {
                return 0.0f;
            }
            case 2: {
                return Rand.Next(0.0f, 30.0f) / 3600.0f * n;
            }
            case 3: {
                return Rand.Next(0.5f, 1.0f) / 60.0f * n;
            }
            case 4: {
                return Rand.Next(3.0f, 12.0f) * n;
            }
            case 5: {
                return Rand.Next(2.0f, 3.0f) * 24.0f * n;
            }
            case 6: {
                return Rand.Next(1.0f, 2.0f) * 7.0f * 24.0f * n;
            }
            case 7: {
                return -1.0f;
            }
            default: {
                return -1.0f;
            }
        }
    }
    
    public void Update() {
        if (this.getParentChar() instanceof IsoZombie) {
            return;
        }
        if (GameServer.bServer) {
            this.RestoreToFullHealth();
            final byte bleedingLevel = ((IsoPlayer)this.getParentChar()).bleedingLevel;
            if (bleedingLevel > 0) {
                final float n = 1.0f / bleedingLevel * 200.0f * GameTime.instance.getInvMultiplier();
                if (Rand.Next((int)n) < n * 0.3f) {
                    this.getParentChar().splatBloodFloor();
                }
                if (Rand.Next((int)n) == 0) {
                    this.getParentChar().splatBloodFloor();
                }
            }
            return;
        }
        if (GameClient.bClient && this.getParentChar() instanceof IsoPlayer && ((IsoPlayer)this.getParentChar()).bRemote) {
            this.RestoreToFullHealth();
            final byte bleedingLevel2 = ((IsoPlayer)this.getParentChar()).bleedingLevel;
            if (bleedingLevel2 > 0) {
                final float n2 = 1.0f / bleedingLevel2 * 200.0f * GameTime.instance.getInvMultiplier();
                if (Rand.Next((int)n2) < n2 * 0.3f) {
                    this.getParentChar().splatBloodFloor();
                }
                if (Rand.Next((int)n2) == 0) {
                    this.getParentChar().splatBloodFloor();
                }
            }
            return;
        }
        if (this.getParentChar().isGodMod()) {
            this.RestoreToFullHealth();
            ((IsoPlayer)this.getParentChar()).bleedingLevel = 0;
            return;
        }
        if (this.getParentChar().isInvincible()) {
            this.setOverallBodyHealth(100.0f);
            for (int i = 0; i < BodyPartType.MAX.index(); ++i) {
                this.getBodyPart(BodyPartType.FromIndex(i)).SetHealth(100.0f);
            }
            return;
        }
        final float pain = this.ParentChar.getStats().Pain;
        int n3 = this.getNumPartsBleeding() * 2 + this.getNumPartsScratched() + this.getNumPartsBitten() * 6;
        if (this.getHealth() >= 60.0f && n3 <= 3) {
            n3 = 0;
        }
        ((IsoPlayer)this.getParentChar()).bleedingLevel = (byte)n3;
        if (n3 > 0) {
            final float n4 = 1.0f / n3 * 200.0f * GameTime.instance.getInvMultiplier();
            if (Rand.Next((int)n4) < n4 * 0.3f) {
                this.getParentChar().splatBloodFloor();
            }
            if (Rand.Next((int)n4) == 0) {
                this.getParentChar().splatBloodFloor();
            }
        }
        if (this.thermoregulator != null) {
            this.thermoregulator.update();
        }
        this.UpdateWetness();
        this.UpdateCold();
        this.UpdateBoredom();
        this.UpdateStrength();
        this.UpdatePanicState();
        this.UpdateTemperatureState();
        this.UpdateIllness();
        if (this.getOverallBodyHealth() == 0.0f) {
            return;
        }
        if (this.PoisonLevel == 0.0f && this.getFoodSicknessLevel() > 0.0f) {
            this.setFoodSicknessLevel(this.getFoodSicknessLevel() - (float)(ZomboidGlobals.FoodSicknessDecrease * GameTime.instance.getMultiplier()));
        }
        if (!this.isInfected()) {
            for (int j = 0; j < BodyPartType.ToIndex(BodyPartType.MAX); ++j) {
                if (this.IsInfected(j)) {
                    this.setInfected(true);
                    if (this.IsFakeInfected(j)) {
                        this.DisableFakeInfection(j);
                        this.setInfectionLevel(this.getFakeInfectionLevel());
                        this.setFakeInfectionLevel(0.0f);
                        this.setIsFakeInfected(false);
                        this.setReduceFakeInfection(false);
                    }
                }
            }
            if (this.isInfected() && this.getInfectionTime() < 0.0f && SandboxOptions.instance.Lore.Mortality.getValue() != 7) {
                this.setInfectionTime(this.getCurrentTimeForInfection());
                this.setInfectionMortalityDuration(this.pickMortalityDuration());
            }
        }
        if (!this.isInfected() && !this.isIsFakeInfected()) {
            for (int k = 0; k < BodyPartType.ToIndex(BodyPartType.MAX); ++k) {
                if (this.IsFakeInfected(k)) {
                    this.setIsFakeInfected(true);
                    break;
                }
            }
        }
        if (this.isIsFakeInfected() && !this.isReduceFakeInfection() && this.getParentChar().getReduceInfectionPower() == 0.0f) {
            this.setFakeInfectionLevel(this.getFakeInfectionLevel() + this.getInfectionGrowthRate() * GameTime.instance.getMultiplier());
            if (this.getFakeInfectionLevel() > 100.0f) {
                this.setFakeInfectionLevel(100.0f);
                this.setReduceFakeInfection(true);
            }
        }
        final Stats stats = this.ParentChar.getStats();
        stats.Drunkenness -= this.getDrunkReductionValue() * GameTime.instance.getMultiplier();
        if (this.getParentChar().getStats().Drunkenness < 0.0f) {
            this.ParentChar.getStats().Drunkenness = 0.0f;
        }
        float n5 = 0.0f;
        if (this.getHealthFromFoodTimer() > 0.0f) {
            n5 += this.getHealthFromFood() * GameTime.instance.getMultiplier();
            this.setHealthFromFoodTimer(this.getHealthFromFoodTimer() - 1.0f * GameTime.instance.getMultiplier());
        }
        int n6 = 0;
        if (this.getParentChar() == this.getParentChar() && (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 2 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 2 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 2)) {
            n6 = 1;
        }
        if (this.getParentChar() == this.getParentChar() && (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 3 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 3 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 3)) {
            n6 = 2;
        }
        if (this.getParentChar() == this.getParentChar() && (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4)) {
            n6 = 3;
        }
        if (this.getParentChar().isAsleep()) {
            n6 = -1;
        }
        switch (n6) {
            case 0: {
                n5 += this.getStandardHealthAddition() * GameTime.instance.getMultiplier();
                break;
            }
            case 1: {
                n5 += this.getReducedHealthAddition() * GameTime.instance.getMultiplier();
                break;
            }
            case 2: {
                n5 += this.getSeverlyReducedHealthAddition() * GameTime.instance.getMultiplier();
                break;
            }
            case 3: {
                n5 += 0.0f;
                break;
            }
        }
        if (this.getParentChar().isAsleep()) {
            if (GameClient.bClient) {
                n5 += 15.0f * GameTime.instance.getGameWorldSecondsSinceLastUpdate() / 3600.0f;
            }
            else {
                n5 += this.getSleepingHealthAddition() * GameTime.instance.getMultiplier();
            }
            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4 || this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4) {
                n5 = 0.0f;
            }
        }
        this.AddGeneralHealth(n5);
        float n7 = 0.0f;
        if (this.PoisonLevel > 0.0f) {
            if (this.PoisonLevel > 10.0f && this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) >= 1) {
                n7 += 0.0035f * Math.min(this.PoisonLevel / 10.0f, 3.0f) * GameTime.instance.getMultiplier();
            }
            float n8 = 0.0f;
            if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.FoodEaten) > 0) {
                n8 = 1.5E-4f * this.getParentChar().getMoodles().getMoodleLevel(MoodleType.FoodEaten);
            }
            this.PoisonLevel -= (float)(n8 + ZomboidGlobals.PoisonLevelDecrease * GameTime.instance.getMultiplier());
            if (this.PoisonLevel < 0.0f) {
                this.PoisonLevel = 0.0f;
            }
            this.setFoodSicknessLevel(this.getFoodSicknessLevel() + this.getInfectionGrowthRate() * (2 + Math.round(this.PoisonLevel / 10.0f)) * GameTime.instance.getMultiplier());
            if (this.getFoodSicknessLevel() > 100.0f) {
                this.setFoodSicknessLevel(100.0f);
            }
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Hungry) == 4) {
            n7 += this.getHealthReductionFromSevereBadMoodles() / 50.0f * GameTime.instance.getMultiplier();
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Sick) == 4 && this.FoodSicknessLevel > this.InfectionLevel) {
            n7 += this.getHealthReductionFromSevereBadMoodles() * GameTime.instance.getMultiplier();
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Bleeding) == 4) {
            n7 += this.getHealthReductionFromSevereBadMoodles() * GameTime.instance.getMultiplier();
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.Thirst) == 4) {
            n7 += this.getHealthReductionFromSevereBadMoodles() / 10.0f * GameTime.instance.getMultiplier();
        }
        if (this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HeavyLoad) > 2 && this.getParentChar().getVehicle() == null && !this.getParentChar().isAsleep() && !this.getParentChar().isSitOnGround() && this.getThermoregulator().getMetabolicTarget() != Metabolics.SeatedResting.getMet() && this.getHealth() > 75.0f && Rand.Next(Rand.AdjustForFramerate(10)) == 0) {
            n7 += this.getHealthReductionFromSevereBadMoodles() / ((5 - this.getParentChar().getMoodles().getMoodleLevel(MoodleType.HeavyLoad)) / 10.0f) * GameTime.instance.getMultiplier();
        }
        this.ReduceGeneralHealth(n7);
        if (this.ParentChar.getPainEffect() > 0.0f) {
            final Stats stats2 = this.ParentChar.getStats();
            stats2.Pain -= 0.023333333f * (GameTime.getInstance().getMultiplier() / 1.6f);
            this.ParentChar.setPainEffect(this.ParentChar.getPainEffect() - GameTime.getInstance().getMultiplier() / 1.6f);
        }
        else {
            this.ParentChar.setPainDelta(0.0f);
            float n9 = 0.0f;
            for (int l = 0; l < BodyPartType.ToIndex(BodyPartType.MAX); ++l) {
                n9 += this.getBodyParts().get(l).getPain() * BodyPartType.getPainModifyer(l);
            }
            final float pain2 = n9 - this.getPainReduction();
            if (pain2 > this.ParentChar.getStats().Pain) {
                final Stats stats3 = this.ParentChar.getStats();
                stats3.Pain += (pain2 - this.ParentChar.getStats().Pain) / 500.0f;
            }
            else {
                this.ParentChar.getStats().Pain = pain2;
            }
        }
        this.setPainReduction(this.getPainReduction() - 0.005f * GameTime.getInstance().getMultiplier());
        if (this.getPainReduction() < 0.0f) {
            this.setPainReduction(0.0f);
        }
        if (this.getParentChar().getStats().Pain > 100.0f) {
            this.ParentChar.getStats().Pain = 100.0f;
        }
        if (this.isInfected()) {
            final int value = SandboxOptions.instance.Lore.Mortality.getValue();
            if (value == 1) {
                this.ReduceGeneralHealth(110.0f);
                this.setInfectionLevel(100.0f);
            }
            else if (value != 7) {
                final float currentTimeForInfection = this.getCurrentTimeForInfection();
                if (this.InfectionMortalityDuration < 0.0f) {
                    this.InfectionMortalityDuration = this.pickMortalityDuration();
                }
                if (this.InfectionTime < 0.0f) {
                    this.InfectionTime = currentTimeForInfection;
                }
                if (this.InfectionTime > currentTimeForInfection) {
                    this.InfectionTime = currentTimeForInfection;
                }
                final float min = Math.min((currentTimeForInfection - this.InfectionTime) / this.InfectionMortalityDuration, 1.0f);
                this.setInfectionLevel(min * 100.0f);
                if (min == 1.0f) {
                    this.ReduceGeneralHealth(110.0f);
                }
                else {
                    final float n10 = min * min;
                    final float n11 = (1.0f - n10 * n10) * 100.0f;
                    final float n12 = this.getOverallBodyHealth() - n11;
                    if (n12 > 0.0f && n11 <= 99.0f) {
                        this.ReduceGeneralHealth(n12);
                    }
                }
            }
        }
        for (int index = 0; index < BodyPartType.ToIndex(BodyPartType.MAX); ++index) {
            this.getBodyParts().get(index).DamageUpdate();
        }
        this.calculateOverallHealth();
        if (this.getOverallBodyHealth() <= 0.0f) {
            if (this.isIsOnFire()) {
                this.setBurntToDeath(true);
                for (int index2 = 0; index2 < BodyPartType.ToIndex(BodyPartType.MAX); ++index2) {
                    this.getBodyParts().get(index2).SetHealth((float)Rand.Next(90));
                }
            }
            else {
                this.setBurntToDeath(false);
            }
        }
        if (this.isReduceFakeInfection() && this.getOverallBodyHealth() > 0.0f) {
            this.setFakeInfectionLevel(this.getFakeInfectionLevel() - this.getInfectionGrowthRate() * GameTime.instance.getMultiplier() * 2.0f);
        }
        if (this.getParentChar().getReduceInfectionPower() > 0.0f && this.getOverallBodyHealth() > 0.0f) {
            this.setFakeInfectionLevel(this.getFakeInfectionLevel() - this.getInfectionGrowthRate() * GameTime.instance.getMultiplier());
            this.getParentChar().setReduceInfectionPower(this.getParentChar().getReduceInfectionPower() - this.getInfectionGrowthRate() * GameTime.instance.getMultiplier());
            if (this.getParentChar().getReduceInfectionPower() < 0.0f) {
                this.getParentChar().setReduceInfectionPower(0.0f);
            }
        }
        if (this.getFakeInfectionLevel() <= 0.0f) {
            for (int index3 = 0; index3 < BodyPartType.ToIndex(BodyPartType.MAX); ++index3) {
                this.getBodyParts().get(index3).SetFakeInfected(false);
            }
            this.setIsFakeInfected(false);
            this.setFakeInfectionLevel(0.0f);
            this.setReduceFakeInfection(false);
        }
        if (pain == this.ParentChar.getStats().Pain) {
            final Stats stats4 = this.ParentChar.getStats();
            stats4.Pain -= (float)(0.25 * (GameTime.getInstance().getMultiplier() / 1.6f));
        }
        if (this.ParentChar.getStats().Pain < 0.0f) {
            this.ParentChar.getStats().Pain = 0.0f;
        }
    }
    
    private void calculateOverallHealth() {
        float n = 0.0f;
        for (int i = 0; i < BodyPartType.ToIndex(BodyPartType.MAX); ++i) {
            n += (100.0f - this.getBodyParts().get(i).getHealth()) * BodyPartType.getDamageModifyer(i);
        }
        if (n > 100.0f) {
            n = 100.0f;
        }
        this.setOverallBodyHealth(100.0f - (n + this.getDamageFromPills()));
    }
    
    public static float getSicknessFromCorpsesRate(final int n) {
        if (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue() == 1) {
            return 0.0f;
        }
        if (n > 5) {
            float n2 = (float)ZomboidGlobals.FoodSicknessDecrease * 0.07f;
            switch (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue()) {
                case 2: {
                    n2 *= 0.01f;
                    break;
                }
                case 4: {
                    n2 *= 0.11f;
                    break;
                }
            }
            return n2 * Math.min(n - 5, 20);
        }
        return 0.0f;
    }
    
    private void UpdateIllness() {
        if (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue() == 1) {
            return;
        }
        final float sicknessFromCorpsesRate = getSicknessFromCorpsesRate(FliesSound.instance.getCorpseCount(this.getParentChar()));
        if (sicknessFromCorpsesRate > 0.0f) {
            this.setFoodSicknessLevel(this.getFoodSicknessLevel() + sicknessFromCorpsesRate * GameTime.getInstance().getMultiplier());
        }
    }
    
    private void UpdateTemperatureState() {
        final float moveSpeed = 0.06f;
        if (this.getParentChar() instanceof IsoPlayer) {
            if (this.ColdDamageStage > 0.0f) {
                final float n = 100.0f - this.ColdDamageStage * 100.0f;
                if (this.OverallBodyHealth > n) {
                    this.ReduceGeneralHealth(this.OverallBodyHealth - n);
                }
            }
            ((IsoPlayer)this.getParentChar()).setMoveSpeed(moveSpeed);
        }
    }
    
    private float getDamageFromPills() {
        if (this.getParentChar() instanceof IsoPlayer) {
            final IsoPlayer isoPlayer = (IsoPlayer)this.getParentChar();
            if (isoPlayer.getSleepingPillsTaken() == 10) {
                return 40.0f;
            }
            if (isoPlayer.getSleepingPillsTaken() == 11) {
                return 80.0f;
            }
            if (isoPlayer.getSleepingPillsTaken() >= 12) {
                return 100.0f;
            }
        }
        return 0.0f;
    }
    
    public boolean UseBandageOnMostNeededPart() {
        int n = 0;
        BodyPart bodyPart = null;
        for (int i = 0; i < this.getBodyParts().size(); ++i) {
            int n2 = 0;
            if (!this.getBodyParts().get(i).bandaged()) {
                if (this.getBodyParts().get(i).bleeding()) {
                    n2 += 100;
                }
                if (this.getBodyParts().get(i).scratched()) {
                    n2 += 50;
                }
                if (this.getBodyParts().get(i).bitten()) {
                    n2 += 50;
                }
                if (n2 > n) {
                    n = n2;
                    bodyPart = this.getBodyParts().get(i);
                }
            }
        }
        if (n > 0 && bodyPart != null) {
            bodyPart.setBandaged(true, 10.0f);
            return true;
        }
        return false;
    }
    
    public ArrayList<BodyPart> getBodyParts() {
        return this.BodyParts;
    }
    
    public int getDamageModCount() {
        return this.DamageModCount;
    }
    
    public void setDamageModCount(final int damageModCount) {
        this.DamageModCount = damageModCount;
    }
    
    public float getInfectionGrowthRate() {
        return this.InfectionGrowthRate;
    }
    
    public void setInfectionGrowthRate(final float infectionGrowthRate) {
        this.InfectionGrowthRate = infectionGrowthRate;
    }
    
    public void setInfectionLevel(final float infectionLevel) {
        this.InfectionLevel = infectionLevel;
    }
    
    public boolean isInfected() {
        return this.IsInfected;
    }
    
    public void setInfected(final boolean isInfected) {
        this.IsInfected = isInfected;
    }
    
    public float getInfectionTime() {
        return this.InfectionTime;
    }
    
    public void setInfectionTime(final float infectionTime) {
        this.InfectionTime = infectionTime;
    }
    
    public float getInfectionMortalityDuration() {
        return this.InfectionMortalityDuration;
    }
    
    public void setInfectionMortalityDuration(final float infectionMortalityDuration) {
        this.InfectionMortalityDuration = infectionMortalityDuration;
    }
    
    private float getCurrentTimeForInfection() {
        if (this.getParentChar() instanceof IsoPlayer) {
            return (float)((IsoPlayer)this.getParentChar()).getHoursSurvived();
        }
        return (float)GameTime.getInstance().getWorldAgeHours();
    }
    
    @Deprecated
    public boolean isInf() {
        return this.IsInfected;
    }
    
    @Deprecated
    public void setInf(final boolean isInfected) {
        this.IsInfected = isInfected;
    }
    
    public float getFakeInfectionLevel() {
        return this.FakeInfectionLevel;
    }
    
    public void setFakeInfectionLevel(final float fakeInfectionLevel) {
        this.FakeInfectionLevel = fakeInfectionLevel;
    }
    
    public boolean isIsFakeInfected() {
        return this.IsFakeInfected;
    }
    
    public void setIsFakeInfected(final boolean isFakeInfected) {
        this.IsFakeInfected = isFakeInfected;
        this.getBodyParts().get(0).SetFakeInfected(isFakeInfected);
    }
    
    public float getOverallBodyHealth() {
        return this.OverallBodyHealth;
    }
    
    public void setOverallBodyHealth(final float overallBodyHealth) {
        this.OverallBodyHealth = overallBodyHealth;
    }
    
    public float getStandardHealthAddition() {
        return this.StandardHealthAddition;
    }
    
    public void setStandardHealthAddition(final float standardHealthAddition) {
        this.StandardHealthAddition = standardHealthAddition;
    }
    
    public float getReducedHealthAddition() {
        return this.ReducedHealthAddition;
    }
    
    public void setReducedHealthAddition(final float reducedHealthAddition) {
        this.ReducedHealthAddition = reducedHealthAddition;
    }
    
    public float getSeverlyReducedHealthAddition() {
        return this.SeverlyReducedHealthAddition;
    }
    
    public void setSeverlyReducedHealthAddition(final float severlyReducedHealthAddition) {
        this.SeverlyReducedHealthAddition = severlyReducedHealthAddition;
    }
    
    public float getSleepingHealthAddition() {
        return this.SleepingHealthAddition;
    }
    
    public void setSleepingHealthAddition(final float sleepingHealthAddition) {
        this.SleepingHealthAddition = sleepingHealthAddition;
    }
    
    public float getHealthFromFood() {
        return this.HealthFromFood;
    }
    
    public void setHealthFromFood(final float healthFromFood) {
        this.HealthFromFood = healthFromFood;
    }
    
    public float getHealthReductionFromSevereBadMoodles() {
        return this.HealthReductionFromSevereBadMoodles;
    }
    
    public void setHealthReductionFromSevereBadMoodles(final float healthReductionFromSevereBadMoodles) {
        this.HealthReductionFromSevereBadMoodles = healthReductionFromSevereBadMoodles;
    }
    
    public int getStandardHealthFromFoodTime() {
        return this.StandardHealthFromFoodTime;
    }
    
    public void setStandardHealthFromFoodTime(final int standardHealthFromFoodTime) {
        this.StandardHealthFromFoodTime = standardHealthFromFoodTime;
    }
    
    public float getHealthFromFoodTimer() {
        return this.HealthFromFoodTimer;
    }
    
    public void setHealthFromFoodTimer(final float healthFromFoodTimer) {
        this.HealthFromFoodTimer = healthFromFoodTimer;
    }
    
    public void setBoredomLevel(final float boredomLevel) {
        this.BoredomLevel = boredomLevel;
    }
    
    public float getBoredomDecreaseFromReading() {
        return this.BoredomDecreaseFromReading;
    }
    
    public void setBoredomDecreaseFromReading(final float boredomDecreaseFromReading) {
        this.BoredomDecreaseFromReading = boredomDecreaseFromReading;
    }
    
    public float getInitialThumpPain() {
        return this.InitialThumpPain;
    }
    
    public void setInitialThumpPain(final float initialThumpPain) {
        this.InitialThumpPain = initialThumpPain;
    }
    
    public float getInitialScratchPain() {
        return this.InitialScratchPain;
    }
    
    public void setInitialScratchPain(final float initialScratchPain) {
        this.InitialScratchPain = initialScratchPain;
    }
    
    public float getInitialBitePain() {
        return this.InitialBitePain;
    }
    
    public void setInitialBitePain(final float initialBitePain) {
        this.InitialBitePain = initialBitePain;
    }
    
    public float getInitialWoundPain() {
        return this.InitialWoundPain;
    }
    
    public void setInitialWoundPain(final float initialWoundPain) {
        this.InitialWoundPain = initialWoundPain;
    }
    
    public float getContinualPainIncrease() {
        return this.ContinualPainIncrease;
    }
    
    public void setContinualPainIncrease(final float continualPainIncrease) {
        this.ContinualPainIncrease = continualPainIncrease;
    }
    
    public float getPainReductionFromMeds() {
        return this.PainReductionFromMeds;
    }
    
    public void setPainReductionFromMeds(final float painReductionFromMeds) {
        this.PainReductionFromMeds = painReductionFromMeds;
    }
    
    public float getStandardPainReductionWhenWell() {
        return this.StandardPainReductionWhenWell;
    }
    
    public void setStandardPainReductionWhenWell(final float standardPainReductionWhenWell) {
        this.StandardPainReductionWhenWell = standardPainReductionWhenWell;
    }
    
    public int getOldNumZombiesVisible() {
        return this.OldNumZombiesVisible;
    }
    
    public void setOldNumZombiesVisible(final int oldNumZombiesVisible) {
        this.OldNumZombiesVisible = oldNumZombiesVisible;
    }
    
    public int getCurrentNumZombiesVisible() {
        return this.CurrentNumZombiesVisible;
    }
    
    public void setCurrentNumZombiesVisible(final int currentNumZombiesVisible) {
        this.CurrentNumZombiesVisible = currentNumZombiesVisible;
    }
    
    public float getPanicIncreaseValue() {
        return this.PanicIncreaseValue;
    }
    
    public float getPanicIncreaseValueFrame() {
        return this.PanicIncreaseValueFrame;
    }
    
    public void setPanicIncreaseValue(final float panicIncreaseValue) {
        this.PanicIncreaseValue = panicIncreaseValue;
    }
    
    public float getPanicReductionValue() {
        return this.PanicReductionValue;
    }
    
    public void setPanicReductionValue(final float panicReductionValue) {
        this.PanicReductionValue = panicReductionValue;
    }
    
    public float getDrunkIncreaseValue() {
        return this.DrunkIncreaseValue;
    }
    
    public void setDrunkIncreaseValue(final float drunkIncreaseValue) {
        this.DrunkIncreaseValue = drunkIncreaseValue;
    }
    
    public float getDrunkReductionValue() {
        return this.DrunkReductionValue;
    }
    
    public void setDrunkReductionValue(final float drunkReductionValue) {
        this.DrunkReductionValue = drunkReductionValue;
    }
    
    public boolean isIsOnFire() {
        return this.IsOnFire;
    }
    
    public void setIsOnFire(final boolean isOnFire) {
        this.IsOnFire = isOnFire;
    }
    
    public boolean isBurntToDeath() {
        return this.BurntToDeath;
    }
    
    public void setBurntToDeath(final boolean burntToDeath) {
        this.BurntToDeath = burntToDeath;
    }
    
    public void setWetness(final float wetness) {
        float n = 0.0f;
        if (this.BodyParts.size() > 0) {
            for (int i = 0; i < this.BodyParts.size(); ++i) {
                final BodyPart bodyPart = this.BodyParts.get(i);
                bodyPart.setWetness(wetness);
                n += bodyPart.getWetness();
            }
            n /= this.BodyParts.size();
        }
        this.Wetness = PZMath.clamp(n, 0.0f, 100.0f);
    }
    
    public float getCatchACold() {
        return this.CatchACold;
    }
    
    public void setCatchACold(final float catchACold) {
        this.CatchACold = catchACold;
    }
    
    public boolean isHasACold() {
        return this.HasACold;
    }
    
    public void setHasACold(final boolean hasACold) {
        this.HasACold = hasACold;
    }
    
    public void setColdStrength(final float coldStrength) {
        this.ColdStrength = coldStrength;
    }
    
    public float getColdProgressionRate() {
        return this.ColdProgressionRate;
    }
    
    public void setColdProgressionRate(final float coldProgressionRate) {
        this.ColdProgressionRate = coldProgressionRate;
    }
    
    public int getTimeToSneezeOrCough() {
        return this.TimeToSneezeOrCough;
    }
    
    public void setTimeToSneezeOrCough(final int timeToSneezeOrCough) {
        this.TimeToSneezeOrCough = timeToSneezeOrCough;
    }
    
    public int getMildColdSneezeTimerMin() {
        return this.MildColdSneezeTimerMin;
    }
    
    public void setMildColdSneezeTimerMin(final int mildColdSneezeTimerMin) {
        this.MildColdSneezeTimerMin = mildColdSneezeTimerMin;
    }
    
    public int getMildColdSneezeTimerMax() {
        return this.MildColdSneezeTimerMax;
    }
    
    public void setMildColdSneezeTimerMax(final int mildColdSneezeTimerMax) {
        this.MildColdSneezeTimerMax = mildColdSneezeTimerMax;
    }
    
    public int getColdSneezeTimerMin() {
        return this.ColdSneezeTimerMin;
    }
    
    public void setColdSneezeTimerMin(final int coldSneezeTimerMin) {
        this.ColdSneezeTimerMin = coldSneezeTimerMin;
    }
    
    public int getColdSneezeTimerMax() {
        return this.ColdSneezeTimerMax;
    }
    
    public void setColdSneezeTimerMax(final int coldSneezeTimerMax) {
        this.ColdSneezeTimerMax = coldSneezeTimerMax;
    }
    
    public int getNastyColdSneezeTimerMin() {
        return this.NastyColdSneezeTimerMin;
    }
    
    public void setNastyColdSneezeTimerMin(final int nastyColdSneezeTimerMin) {
        this.NastyColdSneezeTimerMin = nastyColdSneezeTimerMin;
    }
    
    public int getNastyColdSneezeTimerMax() {
        return this.NastyColdSneezeTimerMax;
    }
    
    public void setNastyColdSneezeTimerMax(final int nastyColdSneezeTimerMax) {
        this.NastyColdSneezeTimerMax = nastyColdSneezeTimerMax;
    }
    
    public int getSneezeCoughActive() {
        return this.SneezeCoughActive;
    }
    
    public void setSneezeCoughActive(final int sneezeCoughActive) {
        this.SneezeCoughActive = sneezeCoughActive;
    }
    
    public int getSneezeCoughTime() {
        return this.SneezeCoughTime;
    }
    
    public void setSneezeCoughTime(final int sneezeCoughTime) {
        this.SneezeCoughTime = sneezeCoughTime;
    }
    
    public int getSneezeCoughDelay() {
        return this.SneezeCoughDelay;
    }
    
    public void setSneezeCoughDelay(final int sneezeCoughDelay) {
        this.SneezeCoughDelay = sneezeCoughDelay;
    }
    
    public void setUnhappynessLevel(final float unhappynessLevel) {
        this.UnhappynessLevel = unhappynessLevel;
    }
    
    public IsoGameCharacter getParentChar() {
        return this.ParentChar;
    }
    
    public void setParentChar(final IsoGameCharacter parentChar) {
        this.ParentChar = parentChar;
    }
    
    public float getTemperature() {
        return this.Temperature;
    }
    
    public void setTemperature(final float temperature) {
        this.lastTemperature = this.Temperature;
        this.Temperature = temperature;
    }
    
    public float getTemperatureChangeTick() {
        return this.Temperature - this.lastTemperature;
    }
    
    public void setPoisonLevel(final float poisonLevel) {
        this.PoisonLevel = poisonLevel;
    }
    
    public float getPoisonLevel() {
        return this.PoisonLevel;
    }
    
    public float getFoodSicknessLevel() {
        return this.FoodSicknessLevel;
    }
    
    public void setFoodSicknessLevel(final float a) {
        this.FoodSicknessLevel = Math.max(a, 0.0f);
    }
    
    public boolean isReduceFakeInfection() {
        return this.reduceFakeInfection;
    }
    
    public void setReduceFakeInfection(final boolean reduceFakeInfection) {
        this.reduceFakeInfection = reduceFakeInfection;
    }
    
    public void AddRandomDamage() {
        final BodyPart bodyPart = this.getBodyParts().get(Rand.Next(this.getBodyParts().size()));
        switch (Rand.Next(4)) {
            case 0: {
                bodyPart.generateDeepWound();
                if (Rand.Next(4) == 0) {
                    bodyPart.setInfectedWound(true);
                    break;
                }
                break;
            }
            case 1: {
                bodyPart.generateDeepShardWound();
                if (Rand.Next(4) == 0) {
                    bodyPart.setInfectedWound(true);
                    break;
                }
                break;
            }
            case 2: {
                bodyPart.setFractureTime((float)Rand.Next(30, 50));
                break;
            }
            case 3: {
                bodyPart.setBurnTime((float)Rand.Next(30, 50));
                break;
            }
        }
    }
    
    public float getPainReduction() {
        return this.painReduction;
    }
    
    public void setPainReduction(final float painReduction) {
        this.painReduction = painReduction;
    }
    
    public float getColdReduction() {
        return this.coldReduction;
    }
    
    public void setColdReduction(final float coldReduction) {
        this.coldReduction = coldReduction;
    }
    
    public int getRemotePainLevel() {
        return this.RemotePainLevel;
    }
    
    public void setRemotePainLevel(final int remotePainLevel) {
        this.RemotePainLevel = remotePainLevel;
    }
    
    public float getColdDamageStage() {
        return this.ColdDamageStage;
    }
    
    public void setColdDamageStage(final float coldDamageStage) {
        this.ColdDamageStage = coldDamageStage;
    }
    
    public Thermoregulator getThermoregulator() {
        return this.thermoregulator;
    }
    
    public void decreaseBodyWetness(final float n) {
        float n2 = 0.0f;
        if (this.BodyParts.size() > 0) {
            for (int i = 0; i < this.BodyParts.size(); ++i) {
                final BodyPart bodyPart = this.BodyParts.get(i);
                bodyPart.setWetness(bodyPart.getWetness() - n);
                n2 += bodyPart.getWetness();
            }
            n2 /= this.BodyParts.size();
        }
        this.Wetness = PZMath.clamp(n2, 0.0f, 100.0f);
    }
    
    public void increaseBodyWetness(final float n) {
        float n2 = 0.0f;
        if (this.BodyParts.size() > 0) {
            for (int i = 0; i < this.BodyParts.size(); ++i) {
                final BodyPart bodyPart = this.BodyParts.get(i);
                bodyPart.setWetness(bodyPart.getWetness() + n);
                n2 += bodyPart.getWetness();
            }
            n2 /= this.BodyParts.size();
        }
        this.Wetness = PZMath.clamp(n2, 0.0f, 100.0f);
    }
    
    static {
        BodyDamage.behindStr = "BEHIND";
        BodyDamage.leftStr = "LEFT";
        BodyDamage.rightStr = "RIGHT";
    }
}
