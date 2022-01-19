// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.BodyDamage;

import zombie.iso.weather.Temperature;
import zombie.inventory.InventoryItem;
import zombie.characterTextures.BloodClothingType;
import zombie.inventory.types.Clothing;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.inventory.types.WeaponType;
import zombie.GameTime;
import zombie.core.math.PZMath;
import zombie.characters.Moodles.MoodleType;
import zombie.debug.DebugLog;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.characterTextures.BloodBodyPartType;
import java.util.ArrayList;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.iso.weather.ClimateManager;
import zombie.characters.Stats;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoGameCharacter;

public final class Thermoregulator
{
    private static final boolean DISABLE_ENERGY_MULTIPLIER = false;
    private final BodyDamage bodyDamage;
    private final IsoGameCharacter character;
    private final IsoPlayer player;
    private final Stats stats;
    private final Nutrition nutrition;
    private final ClimateManager climate;
    private static final ItemVisuals itemVisuals;
    private static final ItemVisuals itemVisualsCache;
    private static final ArrayList<BloodBodyPartType> coveredParts;
    private static float SIMULATION_MULTIPLIER;
    private float setPoint;
    private float metabolicRate;
    private float metabolicRateReal;
    private float metabolicTarget;
    private double fluidsMultiplier;
    private double energyMultiplier;
    private double fatigueMultiplier;
    private float bodyHeatDelta;
    private float coreHeatDelta;
    private boolean thermalChevronUp;
    private ThermalNode core;
    private ThermalNode[] nodes;
    private float totalHeatRaw;
    private float totalHeat;
    private float primTotal;
    private float secTotal;
    private float externalAirTemperature;
    private float airTemperature;
    private float airAndWindTemp;
    private float rateOfChangeCounter;
    private float coreCelciusCache;
    private float coreRateOfChange;
    private float thermalDamage;
    private float damageCounter;
    
    public Thermoregulator(final BodyDamage bodyDamage) {
        this.setPoint = 37.0f;
        this.metabolicRate = Metabolics.Default.getMet();
        this.metabolicRateReal = this.metabolicRate;
        this.metabolicTarget = Metabolics.Default.getMet();
        this.fluidsMultiplier = 1.0;
        this.energyMultiplier = 1.0;
        this.fatigueMultiplier = 1.0;
        this.bodyHeatDelta = 0.0f;
        this.coreHeatDelta = 0.0f;
        this.thermalChevronUp = true;
        this.totalHeatRaw = 0.0f;
        this.totalHeat = 0.0f;
        this.primTotal = 0.0f;
        this.secTotal = 0.0f;
        this.externalAirTemperature = 27.0f;
        this.rateOfChangeCounter = 0.0f;
        this.coreCelciusCache = 37.0f;
        this.coreRateOfChange = 0.0f;
        this.thermalDamage = 0.0f;
        this.damageCounter = 0.0f;
        this.bodyDamage = bodyDamage;
        this.character = bodyDamage.getParentChar();
        this.stats = this.character.getStats();
        if (this.character instanceof IsoPlayer) {
            this.player = (IsoPlayer)this.character;
            this.nutrition = ((IsoPlayer)this.character).getNutrition();
        }
        else {
            this.player = null;
            this.nutrition = null;
        }
        this.climate = ClimateManager.getInstance();
        this.initNodes();
    }
    
    public static void setSimulationMultiplier(final float simulation_MULTIPLIER) {
        Thermoregulator.SIMULATION_MULTIPLIER = simulation_MULTIPLIER;
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.putFloat(this.setPoint);
        byteBuffer.putFloat(this.metabolicRate);
        byteBuffer.putFloat(this.metabolicTarget);
        byteBuffer.putFloat(this.bodyHeatDelta);
        byteBuffer.putFloat(this.coreHeatDelta);
        byteBuffer.putFloat(this.thermalDamage);
        byteBuffer.putFloat(this.damageCounter);
        byteBuffer.putInt(this.nodes.length);
        for (int i = 0; i < this.nodes.length; ++i) {
            final ThermalNode thermalNode = this.nodes[i];
            byteBuffer.putInt(BodyPartType.ToIndex(thermalNode.bodyPartType));
            byteBuffer.putFloat(thermalNode.celcius);
            byteBuffer.putFloat(thermalNode.skinCelcius);
            byteBuffer.putFloat(thermalNode.heatDelta);
            byteBuffer.putFloat(thermalNode.primaryDelta);
            byteBuffer.putFloat(thermalNode.secondaryDelta);
        }
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        this.setPoint = byteBuffer.getFloat();
        this.metabolicRate = byteBuffer.getFloat();
        this.metabolicTarget = byteBuffer.getFloat();
        this.bodyHeatDelta = byteBuffer.getFloat();
        this.coreHeatDelta = byteBuffer.getFloat();
        this.thermalDamage = byteBuffer.getFloat();
        this.damageCounter = byteBuffer.getFloat();
        for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
            final int int2 = byteBuffer.getInt();
            final float float1 = byteBuffer.getFloat();
            final float float2 = byteBuffer.getFloat();
            final float float3 = byteBuffer.getFloat();
            final float float4 = byteBuffer.getFloat();
            final float float5 = byteBuffer.getFloat();
            final ThermalNode nodeForType = this.getNodeForType(BodyPartType.FromIndex(int2));
            if (nodeForType != null) {
                nodeForType.celcius = float1;
                nodeForType.skinCelcius = float2;
                nodeForType.heatDelta = float3;
                nodeForType.primaryDelta = float4;
                nodeForType.secondaryDelta = float5;
            }
            else {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, BodyPartType.ToString(BodyPartType.FromIndex(int2))));
            }
        }
    }
    
    public void reset() {
        this.setPoint = 37.0f;
        this.metabolicRate = Metabolics.Default.getMet();
        this.metabolicTarget = this.metabolicRate;
        this.core.celcius = 37.0f;
        this.bodyHeatDelta = 0.0f;
        this.coreHeatDelta = 0.0f;
        this.thermalDamage = 0.0f;
        for (int i = 0; i < this.nodes.length; ++i) {
            final ThermalNode thermalNode = this.nodes[i];
            if (thermalNode != this.core) {
                thermalNode.celcius = 35.0f;
            }
            thermalNode.primaryDelta = 0.0f;
            thermalNode.secondaryDelta = 0.0f;
            thermalNode.skinCelcius = 33.0f;
            thermalNode.heatDelta = 0.0f;
        }
    }
    
    private void initNodes() {
        final ArrayList<ThermalNode> list = new ArrayList<ThermalNode>();
        for (int i = 0; i < this.bodyDamage.getBodyParts().size(); ++i) {
            final BodyPart bodyPart = this.bodyDamage.getBodyParts().get(i);
            ThermalNode thermalNode = null;
            switch (bodyPart.getType()) {
                case Torso_Upper: {
                    thermalNode = new ThermalNode(true, 37.0f, bodyPart, 0.25f);
                    this.core = thermalNode;
                    break;
                }
                case Head: {
                    thermalNode = new ThermalNode(37.0f, bodyPart, 1.0f);
                    break;
                }
                case Neck: {
                    thermalNode = new ThermalNode(37.0f, bodyPart, 0.5f);
                    break;
                }
                case Torso_Lower: {
                    thermalNode = new ThermalNode(37.0f, bodyPart, 0.25f);
                    break;
                }
                case Groin: {
                    thermalNode = new ThermalNode(37.0f, bodyPart, 0.5f);
                    break;
                }
                case UpperLeg_L:
                case UpperLeg_R: {
                    thermalNode = new ThermalNode(37.0f, bodyPart, 0.5f);
                    break;
                }
                case LowerLeg_L:
                case LowerLeg_R: {
                    thermalNode = new ThermalNode(37.0f, bodyPart, 0.5f);
                    break;
                }
                case Foot_L:
                case Foot_R: {
                    thermalNode = new ThermalNode(37.0f, bodyPart, 0.5f);
                    break;
                }
                case UpperArm_L:
                case UpperArm_R: {
                    thermalNode = new ThermalNode(37.0f, bodyPart, 0.25f);
                    break;
                }
                case ForeArm_L:
                case ForeArm_R: {
                    thermalNode = new ThermalNode(37.0f, bodyPart, 0.25f);
                    break;
                }
                case Hand_L:
                case Hand_R: {
                    thermalNode = new ThermalNode(37.0f, bodyPart, 1.0f);
                    break;
                }
                default: {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Lzombie/characters/BodyDamage/BodyPartType;)Ljava/lang/String;, this.bodyDamage.getBodyParts().get(i).getType()));
                    break;
                }
            }
            if (thermalNode != null) {
                list.add(bodyPart.thermalNode = thermalNode);
            }
        }
        list.toArray(this.nodes = new ThermalNode[list.size()]);
        for (int j = 0; j < this.nodes.length; ++j) {
            final ThermalNode thermalNode2 = this.nodes[j];
            final BodyPartType parent = BodyPartContacts.getParent(thermalNode2.bodyPartType);
            if (parent != null) {
                thermalNode2.upstream = this.getNodeForType(parent);
            }
            final BodyPartType[] children = BodyPartContacts.getChildren(thermalNode2.bodyPartType);
            if (children != null && children.length > 0) {
                thermalNode2.downstream = new ThermalNode[children.length];
                for (int k = 0; k < children.length; ++k) {
                    thermalNode2.downstream[k] = this.getNodeForType(children[k]);
                }
            }
        }
        this.core.celcius = this.setPoint;
    }
    
    public ThermalNode getNodeForType(final BodyPartType bodyPartType) {
        for (int i = 0; i < this.nodes.length; ++i) {
            if (this.nodes[i].bodyPartType == bodyPartType) {
                return this.nodes[i];
            }
        }
        return null;
    }
    
    public ThermalNode getNodeForBloodType(final BloodBodyPartType bloodBodyPartType) {
        for (int i = 0; i < this.nodes.length; ++i) {
            if (this.nodes[i].bloodBPT == bloodBodyPartType) {
                return this.nodes[i];
            }
        }
        return null;
    }
    
    public float getBodyHeatDelta() {
        return this.bodyHeatDelta;
    }
    
    public double getFluidsMultiplier() {
        return this.fluidsMultiplier;
    }
    
    public double getEnergyMultiplier() {
        return this.energyMultiplier;
    }
    
    public double getFatigueMultiplier() {
        return this.fatigueMultiplier;
    }
    
    public float getMovementModifier() {
        float n = 1.0f;
        if (this.player != null) {
            final int moodleLevel = this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia);
            if (moodleLevel == 2) {
                n = 0.66f;
            }
            else if (moodleLevel == 3) {
                n = 0.33f;
            }
            else if (moodleLevel == 4) {
                n = 0.0f;
            }
            final int moodleLevel2 = this.player.getMoodles().getMoodleLevel(MoodleType.Hyperthermia);
            if (moodleLevel2 == 2) {
                n = 0.66f;
            }
            else if (moodleLevel2 == 3) {
                n = 0.33f;
            }
            else if (moodleLevel2 == 4) {
                n = 0.0f;
            }
        }
        return n;
    }
    
    public float getCombatModifier() {
        float n = 1.0f;
        if (this.player != null) {
            final int moodleLevel = this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia);
            if (moodleLevel == 2) {
                n = 0.66f;
            }
            else if (moodleLevel == 3) {
                n = 0.33f;
            }
            else if (moodleLevel == 4) {
                n = 0.1f;
            }
            final int moodleLevel2 = this.player.getMoodles().getMoodleLevel(MoodleType.Hyperthermia);
            if (moodleLevel2 == 2) {
                n = 0.66f;
            }
            else if (moodleLevel2 == 3) {
                n = 0.33f;
            }
            else if (moodleLevel2 == 4) {
                n = 0.1f;
            }
        }
        return n;
    }
    
    public float getCoreTemperature() {
        return this.core.celcius;
    }
    
    public float getHeatGeneration() {
        return this.metabolicRateReal;
    }
    
    public float getMetabolicRate() {
        return this.metabolicRate;
    }
    
    public float getMetabolicTarget() {
        return this.metabolicTarget;
    }
    
    public float getMetabolicRateReal() {
        return this.metabolicRateReal;
    }
    
    public float getSetPoint() {
        return this.setPoint;
    }
    
    public float getCoreHeatDelta() {
        return this.coreHeatDelta;
    }
    
    public float getCoreRateOfChange() {
        return this.coreRateOfChange;
    }
    
    public float getExternalAirTemperature() {
        return this.externalAirTemperature;
    }
    
    public float getCoreTemperatureUI() {
        final float clamp = PZMath.clamp(this.core.celcius, 20.0f, 42.0f);
        float n;
        if (clamp < 37.0f) {
            n = (clamp - 20.0f) / 17.0f * 0.5f;
        }
        else {
            n = 0.5f + (clamp - 37.0f) / 5.0f * 0.5f;
        }
        return n;
    }
    
    public float getHeatGenerationUI() {
        final float clamp = PZMath.clamp(this.metabolicRateReal, 0.0f, Metabolics.MAX.getMet());
        float n;
        if (clamp < Metabolics.Default.getMet()) {
            n = clamp / Metabolics.Default.getMet() * 0.5f;
        }
        else {
            n = 0.5f + (clamp - Metabolics.Default.getMet()) / (Metabolics.MAX.getMet() - Metabolics.Default.getMet()) * 0.5f;
        }
        return n;
    }
    
    public boolean thermalChevronUp() {
        return this.thermalChevronUp;
    }
    
    public int thermalChevronCount() {
        if (this.coreRateOfChange > 0.01f) {
            return 3;
        }
        if (this.coreRateOfChange > 0.001f) {
            return 2;
        }
        if (this.coreRateOfChange > 1.0E-4f) {
            return 1;
        }
        return 0;
    }
    
    public float getCatchAColdDelta() {
        float n = 0.0f;
        if (this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia) < 1) {
            return n;
        }
        for (int i = 0; i < this.nodes.length; ++i) {
            final ThermalNode thermalNode = this.nodes[i];
            float n2 = 0.0f;
            if (thermalNode.skinCelcius < 33.0f) {
                final float n3 = 1.0f - (thermalNode.skinCelcius - 20.0f) / 13.0f;
                n2 = n3 * n3;
            }
            float n4 = 0.25f * n2 * thermalNode.skinSurface;
            if (thermalNode.bodyWetness > 0.0f) {
                n4 *= 1.0f + thermalNode.bodyWetness * 1.0f;
            }
            if (thermalNode.clothingWetness > 0.5f) {
                n4 *= 1.0f + (thermalNode.clothingWetness - 0.5f) * 2.0f;
            }
            if (thermalNode.bodyPartType == BodyPartType.Neck) {
                n4 *= 8.0f;
            }
            else if (thermalNode.bodyPartType == BodyPartType.Torso_Upper) {
                n4 *= 16.0f;
            }
            else if (thermalNode.bodyPartType == BodyPartType.Head) {
                n4 *= 4.0f;
            }
            n += n4;
        }
        if (this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia) > 1) {
            n *= this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia);
        }
        return n;
    }
    
    public float getTimedActionTimeModifier() {
        float n = 1.0f;
        for (int i = 0; i < this.nodes.length; ++i) {
            final ThermalNode thermalNode = this.nodes[i];
            float n2 = 0.0f;
            if (thermalNode.skinCelcius < 33.0f) {
                final float n3 = 1.0f - (thermalNode.skinCelcius - 20.0f) / 13.0f;
                n2 = n3 * n3;
            }
            final float n4 = 0.25f * n2 * thermalNode.skinSurface;
            if (thermalNode.bodyPartType == BodyPartType.Hand_R || thermalNode.bodyPartType == BodyPartType.Hand_L) {
                n += 0.3f * n4;
            }
            else if (thermalNode.bodyPartType == BodyPartType.ForeArm_R || thermalNode.bodyPartType == BodyPartType.ForeArm_L) {
                n += 0.15f * n4;
            }
            else if (thermalNode.bodyPartType == BodyPartType.UpperArm_R || thermalNode.bodyPartType == BodyPartType.UpperArm_L) {
                n += 0.1f * n4;
            }
        }
        return n;
    }
    
    public static float getSkinCelciusMin() {
        return 20.0f;
    }
    
    public static float getSkinCelciusFavorable() {
        return 33.0f;
    }
    
    public static float getSkinCelciusMax() {
        return 42.0f;
    }
    
    public void setMetabolicTarget(final Metabolics metabolics) {
        this.setMetabolicTarget(metabolics.getMet());
    }
    
    public void setMetabolicTarget(final float metabolicTarget) {
        if (metabolicTarget < 0.0f || metabolicTarget < this.metabolicTarget) {
            return;
        }
        this.metabolicTarget = metabolicTarget;
        if (this.metabolicTarget > Metabolics.MAX.getMet()) {
            this.metabolicTarget = Metabolics.MAX.getMet();
        }
    }
    
    private void updateCoreRateOfChange() {
        this.rateOfChangeCounter += GameTime.instance.getMultiplier();
        if (this.rateOfChangeCounter > 100.0f) {
            this.rateOfChangeCounter = 0.0f;
            this.coreRateOfChange = this.core.celcius - this.coreCelciusCache;
            this.thermalChevronUp = (this.coreRateOfChange >= 0.0f);
            this.coreRateOfChange = PZMath.abs(this.coreRateOfChange);
            this.coreCelciusCache = this.core.celcius;
        }
    }
    
    public float getSimulationMultiplier() {
        return Thermoregulator.SIMULATION_MULTIPLIER;
    }
    
    public float getDefaultMultiplier() {
        return this.getSimulationMultiplier(Multiplier.Default);
    }
    
    public float getMetabolicRateIncMultiplier() {
        return this.getSimulationMultiplier(Multiplier.MetabolicRateInc);
    }
    
    public float getMetabolicRateDecMultiplier() {
        return this.getSimulationMultiplier(Multiplier.MetabolicRateDec);
    }
    
    public float getBodyHeatMultiplier() {
        return this.getSimulationMultiplier(Multiplier.BodyHeat);
    }
    
    public float getCoreHeatExpandMultiplier() {
        return this.getSimulationMultiplier(Multiplier.CoreHeatExpand);
    }
    
    public float getCoreHeatContractMultiplier() {
        return this.getSimulationMultiplier(Multiplier.CoreHeatContract);
    }
    
    public float getSkinCelciusMultiplier() {
        return this.getSimulationMultiplier(Multiplier.SkinCelcius);
    }
    
    public float getTemperatureAir() {
        return this.climate.getAirTemperatureForCharacter(this.character, false);
    }
    
    public float getTemperatureAirAndWind() {
        return this.climate.getAirTemperatureForCharacter(this.character, true);
    }
    
    public float getDbg_totalHeatRaw() {
        return this.totalHeatRaw;
    }
    
    public float getDbg_totalHeat() {
        return this.totalHeat;
    }
    
    public float getCoreCelcius() {
        return (this.core != null) ? this.core.celcius : 0.0f;
    }
    
    public float getDbg_primTotal() {
        return this.primTotal;
    }
    
    public float getDbg_secTotal() {
        return this.secTotal;
    }
    
    private float getSimulationMultiplier(final Multiplier multiplier) {
        float multiplier2 = GameTime.instance.getMultiplier();
        switch (multiplier) {
            case MetabolicRateInc: {
                multiplier2 *= 0.001f;
                break;
            }
            case MetabolicRateDec: {
                multiplier2 *= 4.0E-4f;
                break;
            }
            case BodyHeat: {
                multiplier2 *= 2.5E-4f;
                break;
            }
            case CoreHeatExpand: {
                multiplier2 *= 5.0E-5f;
                break;
            }
            case CoreHeatContract: {
                multiplier2 *= 5.0E-4f;
                break;
            }
            case SkinCelcius:
            case SkinCelciusExpand: {
                multiplier2 *= 0.0025f;
                break;
            }
            case SkinCelciusContract: {
                multiplier2 *= 0.005f;
                break;
            }
            case PrimaryDelta: {
                multiplier2 *= 5.0E-4f;
                break;
            }
            case SecondaryDelta: {
                multiplier2 *= 2.5E-4f;
                break;
            }
        }
        return multiplier2 * Thermoregulator.SIMULATION_MULTIPLIER;
    }
    
    public float getThermalDamage() {
        return this.thermalDamage;
    }
    
    private void updateThermalDamage(final float n) {
        this.damageCounter += GameTime.instance.getRealworldSecondsSinceLastUpdate();
        if (this.damageCounter > 1.0f) {
            this.damageCounter = 0.0f;
            if (this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia) == 4 && n < 0.0f && this.core.celcius - this.coreCelciusCache <= 0.0f) {
                this.thermalDamage += 1.0f / (120.0f + 480.0f * (1.0f - (this.core.celcius - 20.0f) / 5.0f)) * PZMath.clamp_01(PZMath.abs(n) / 10.0f);
            }
            else if (this.player.getMoodles().getMoodleLevel(MoodleType.Hyperthermia) == 4 && n > 37.0f && this.core.celcius - this.coreCelciusCache >= 0.0f) {
                this.thermalDamage += 1.0f / (120.0f + 480.0f * ((this.core.celcius - 41.0f) / 1.0f)) * PZMath.clamp_01((n - 37.0f) / 8.0f);
                this.thermalDamage = Math.min(this.thermalDamage, 0.3f);
            }
            else {
                this.thermalDamage -= 0.011111111f;
            }
            this.thermalDamage = PZMath.clamp_01(this.thermalDamage);
        }
        this.player.getBodyDamage().ColdDamageStage = this.thermalDamage;
    }
    
    public void update() {
        this.airTemperature = this.climate.getAirTemperatureForCharacter(this.character, false);
        this.airAndWindTemp = this.climate.getAirTemperatureForCharacter(this.character, true);
        this.externalAirTemperature = this.airTemperature;
        this.updateSetPoint();
        this.updateCoreRateOfChange();
        this.updateMetabolicRate();
        this.updateClothing();
        this.updateNodesHeatDelta();
        this.updateHeatDeltas();
        this.updateNodes();
        this.updateBodyMultipliers();
        this.updateThermalDamage(this.airAndWindTemp);
    }
    
    private float getSicknessValue() {
        return this.stats.getSickness();
    }
    
    private void updateSetPoint() {
        this.setPoint = 37.0f;
        if (this.stats.getSickness() > 0.0f) {
            this.setPoint += this.stats.getSickness() * 2.0f;
        }
    }
    
    private void updateMetabolicRate() {
        this.setMetabolicTarget(Metabolics.Default.getMet());
        if (this.player != null) {
            if (this.player.isAttacking()) {
                switch (WeaponType.getWeaponType(this.player)) {
                    case barehand: {
                        this.setMetabolicTarget(Metabolics.MediumWork);
                        break;
                    }
                    case twohanded: {
                        this.setMetabolicTarget(Metabolics.HeavyWork);
                        break;
                    }
                    case onehanded: {
                        this.setMetabolicTarget(Metabolics.MediumWork);
                        break;
                    }
                    case heavy: {
                        this.setMetabolicTarget(Metabolics.Running15kmh);
                        break;
                    }
                    case knife: {
                        this.setMetabolicTarget(Metabolics.LightWork);
                        break;
                    }
                    case spear: {
                        this.setMetabolicTarget(Metabolics.MediumWork);
                        break;
                    }
                    case handgun: {
                        this.setMetabolicTarget(Metabolics.UsingTools);
                        break;
                    }
                    case firearm: {
                        this.setMetabolicTarget(Metabolics.LightWork);
                        break;
                    }
                    case throwing: {
                        this.setMetabolicTarget(Metabolics.MediumWork);
                        break;
                    }
                    case chainsaw: {
                        this.setMetabolicTarget(Metabolics.Running15kmh);
                        break;
                    }
                }
            }
            if (this.player.isPlayerMoving()) {
                if (this.player.isSprinting()) {
                    this.setMetabolicTarget(Metabolics.Running15kmh);
                }
                else if (this.player.isRunning()) {
                    this.setMetabolicTarget(Metabolics.Running10kmh);
                }
                else if (this.player.isSneaking()) {
                    this.setMetabolicTarget(Metabolics.Walking2kmh);
                }
                else if (this.player.CurrentSpeed > 0.0f) {
                    this.setMetabolicTarget(Metabolics.Walking5kmh);
                }
            }
        }
        this.setMetabolicTarget(PZMath.clamp_01(1.0f - this.stats.getEndurance()) * Metabolics.DefaultExercise.getMet() * this.getEnergy());
        final float clamp_01 = PZMath.clamp_01(this.player.getInventory().getCapacityWeight() / this.player.getMaxWeight());
        this.setMetabolicTarget(this.metabolicTarget * (1.0f + clamp_01 * clamp_01 * 0.35f));
        if (!PZMath.equal(this.metabolicRate, this.metabolicTarget)) {
            final float n = this.metabolicTarget - this.metabolicRate;
            if (this.metabolicTarget > this.metabolicRate) {
                this.metabolicRate += n * this.getSimulationMultiplier(Multiplier.MetabolicRateInc);
            }
            else {
                this.metabolicRate += n * this.getSimulationMultiplier(Multiplier.MetabolicRateDec);
            }
        }
        float movementModifier = 1.0f;
        if (this.player.getMoodles().getMoodleLevel(MoodleType.Hypothermia) >= 1) {
            movementModifier = this.getMovementModifier();
        }
        this.metabolicRateReal = this.metabolicRate * (0.2f + 0.8f * this.getEnergy() * movementModifier);
        this.metabolicTarget = -1.0f;
    }
    
    private void updateNodesHeatDelta() {
        final float n = (PZMath.clamp_01((this.player.getNutrition().getWeight() / 75.0f - 0.5f) * 0.666f) - 0.5f) * 2.0f;
        final float fitness = this.stats.getFitness();
        float n2 = 1.0f;
        if (this.airAndWindTemp > this.setPoint - 2.0f) {
            if (this.airTemperature < this.setPoint + 2.0f) {
                n2 = 1.0f - (this.airTemperature - (this.setPoint - 2.0f)) / 4.0f;
            }
            else {
                n2 = 0.0f;
            }
        }
        float n3 = 1.0f;
        if (this.climate.getHumidity() > 0.5f) {
            n3 -= (this.climate.getHumidity() - 0.5f) * 2.0f;
        }
        if (this.core.celcius < 37.0f) {
            final float n4 = (this.core.celcius - 20.0f) / 17.0f;
        }
        float totalHeatRaw = 0.0f;
        for (int i = 0; i < this.nodes.length; ++i) {
            final ThermalNode thermalNode = this.nodes[i];
            thermalNode.calculateInsulation();
            float airTemperature = this.airTemperature;
            if (this.airAndWindTemp < this.airTemperature) {
                airTemperature -= (this.airTemperature - this.airAndWindTemp) / (1.0f + thermalNode.windresist);
            }
            final float n5 = airTemperature - thermalNode.skinCelcius;
            float n6;
            if (n5 <= 0.0f) {
                n6 = n5 * (1.0f + 0.75f * thermalNode.bodyWetness);
            }
            else {
                n6 = n5 / (1.0f + 3.0f * thermalNode.bodyWetness);
            }
            thermalNode.heatDelta = n6 * 0.3f / (1.0f + thermalNode.insulation) * thermalNode.skinSurface;
            if (thermalNode.primaryDelta > 0.0f) {
                final float n7 = Metabolics.Default.getMet() * thermalNode.primaryDelta * thermalNode.skinSurface / (1.0f + thermalNode.insulation) * ((0.2f + 0.8f * this.getBodyFluids()) * (0.1f + 0.9f * n2)) * n3 * (1.0f - 0.2f * n) * (1.0f + 0.2f * fitness);
                final ThermalNode thermalNode2 = thermalNode;
                thermalNode2.heatDelta -= n7;
            }
            else {
                final float n8 = Metabolics.Default.getMet() * PZMath.abs(thermalNode.primaryDelta) * thermalNode.skinSurface * (0.2f + 0.8f * this.getEnergy()) * (1.0f + 0.2f * n) * (1.0f + 0.2f * fitness);
                final ThermalNode thermalNode3 = thermalNode;
                thermalNode3.heatDelta += n8;
            }
            if (thermalNode.secondaryDelta > 0.0f) {
                final float n9 = Metabolics.MAX.getMet() * 0.75f * thermalNode.secondaryDelta * thermalNode.skinSurface / (1.0f + thermalNode.insulation) * (0.1f + 0.9f * this.getBodyFluids()) * (0.85f + 0.15f * n3) * (1.0f - 0.2f * n) * (1.0f + 0.2f * fitness);
                final ThermalNode thermalNode4 = thermalNode;
                thermalNode4.heatDelta -= n9;
            }
            else {
                final float n10 = Metabolics.Default.getMet() * PZMath.abs(thermalNode.secondaryDelta) * thermalNode.skinSurface * (0.1f + 0.9f * this.getEnergy()) * (1.0f + 0.2f * n) * (1.0f + 0.2f * fitness);
                final ThermalNode thermalNode5 = thermalNode;
                thermalNode5.heatDelta += n10;
            }
            totalHeatRaw += thermalNode.heatDelta;
        }
        this.totalHeatRaw = totalHeatRaw;
        this.totalHeat = totalHeatRaw + this.metabolicRateReal;
    }
    
    private void updateHeatDeltas() {
        this.coreHeatDelta = this.totalHeat * this.getSimulationMultiplier(Multiplier.BodyHeat);
        if (this.coreHeatDelta < 0.0f) {
            if (this.core.celcius > this.setPoint) {
                this.coreHeatDelta *= 1.0f + (this.core.celcius - this.setPoint) / 2.0f;
            }
        }
        else if (this.core.celcius < this.setPoint) {
            this.coreHeatDelta *= 1.0f + (this.setPoint - this.core.celcius) / 4.0f;
        }
        final ThermalNode core = this.core;
        core.celcius += this.coreHeatDelta;
        this.core.celcius = PZMath.clamp(this.core.celcius, 20.0f, 42.0f);
        this.bodyDamage.setTemperature(this.core.celcius);
        this.bodyHeatDelta = 0.0f;
        if (this.core.celcius > this.setPoint) {
            this.bodyHeatDelta = this.core.celcius - this.setPoint;
        }
        else if (this.core.celcius < this.setPoint) {
            this.bodyHeatDelta = this.core.celcius - this.setPoint;
        }
        if (this.bodyHeatDelta < 0.0f) {
            final float abs = PZMath.abs(this.bodyHeatDelta);
            if (abs <= 1.0f) {
                this.bodyHeatDelta *= 0.8f;
            }
            else {
                this.bodyHeatDelta = -0.8f + -0.2f * ((PZMath.clamp(abs, 1.0f, 11.0f) - 1.0f) / 10.0f);
            }
        }
        this.bodyHeatDelta = PZMath.clamp(this.bodyHeatDelta, -1.0f, 1.0f);
    }
    
    private void updateNodes() {
        float primTotal = 0.0f;
        float secTotal = 0.0f;
        for (int i = 0; i < this.nodes.length; ++i) {
            final ThermalNode thermalNode = this.nodes[i];
            final float n = 1.0f + thermalNode.insulation;
            final float n2 = this.metabolicRateReal / Metabolics.MAX.getMet();
            if (this.bodyHeatDelta < 0.0f) {
                thermalNode.primaryDelta = this.bodyHeatDelta * (1.0f + thermalNode.distToCore);
            }
            else {
                thermalNode.primaryDelta = this.bodyHeatDelta * (1.0f + (1.0f - thermalNode.distToCore));
            }
            thermalNode.primaryDelta = PZMath.clamp(thermalNode.primaryDelta, -1.0f, 1.0f);
            thermalNode.secondaryDelta = thermalNode.primaryDelta * PZMath.abs(thermalNode.primaryDelta) * PZMath.abs(thermalNode.primaryDelta);
            primTotal += thermalNode.primaryDelta * thermalNode.skinSurface;
            secTotal += thermalNode.secondaryDelta * thermalNode.skinSurface;
            if (this.stats.getDrunkenness() > 0.0f) {
                final ThermalNode thermalNode2 = thermalNode;
                thermalNode2.primaryDelta += this.stats.getDrunkenness() * 0.02f;
            }
            thermalNode.primaryDelta = PZMath.clamp(thermalNode.primaryDelta, -1.0f, 1.0f);
            float n3 = this.core.celcius - 20.0f;
            final float celcius = this.core.celcius;
            if (n3 < this.airTemperature) {
                if (this.airTemperature < 33.0f) {
                    n3 = this.airTemperature;
                }
                else {
                    n3 = PZMath.clamp(33.0f + 4.0f * ((this.airTemperature - 33.0f) / 6.0f) * (0.4f + 0.6f * (1.0f - thermalNode.distToCore)), 33.0f, this.airTemperature);
                    if (n3 > celcius) {
                        n3 = celcius - 0.25f;
                    }
                }
            }
            final float n4 = this.core.celcius - 4.0f;
            float n5;
            if (thermalNode.primaryDelta < 0.0f) {
                n5 = PZMath.c_lerp(n4, n4 - 12.0f * (0.4f + 0.6f * thermalNode.distToCore) / n, PZMath.abs(thermalNode.primaryDelta));
            }
            else {
                final float n6 = 0.4f + 0.6f * (1.0f - thermalNode.distToCore);
                n5 = PZMath.c_lerp(n4, Math.min(n4 + 4.0f * n6 * Math.max(n * 0.5f * n6, 1.0f), celcius), thermalNode.primaryDelta);
            }
            final float n7 = PZMath.clamp(n5, n3, celcius) - thermalNode.skinCelcius;
            float n8 = 1.0f;
            if (n7 < 0.0f && thermalNode.skinCelcius > 33.0f) {
                n8 = 3.0f;
            }
            else if (n7 > 0.0f && thermalNode.skinCelcius < 33.0f) {
                n8 = 3.0f;
            }
            final ThermalNode thermalNode3 = thermalNode;
            thermalNode3.skinCelcius += n7 * n8 * this.getSimulationMultiplier(Multiplier.SkinCelcius);
            if (thermalNode != this.core) {
                if (thermalNode.skinCelcius >= this.core.celcius) {
                    thermalNode.celcius = this.core.celcius;
                }
                else {
                    thermalNode.celcius = PZMath.lerp(thermalNode.skinCelcius, this.core.celcius, 0.5f);
                }
            }
        }
        this.primTotal = primTotal;
        this.secTotal = secTotal;
    }
    
    private void updateBodyMultipliers() {
        this.energyMultiplier = 1.0;
        this.fluidsMultiplier = 1.0;
        this.fatigueMultiplier = 1.0;
        final float abs = PZMath.abs(this.primTotal);
        final float n = abs * abs;
        if (this.primTotal < 0.0f) {
            this.energyMultiplier += 0.05f * n;
            this.fatigueMultiplier += 0.25f * n;
        }
        else if (this.primTotal > 0.0f) {
            this.fluidsMultiplier += 0.25f * n;
            this.fatigueMultiplier += 0.25f * n;
        }
        final float abs2 = PZMath.abs(this.secTotal);
        final float n2 = abs2 * abs2;
        if (this.secTotal < 0.0f) {
            this.energyMultiplier += 0.1f * n2;
            this.fatigueMultiplier += 0.75f * n2;
        }
        else if (this.secTotal > 0.0f) {
            this.fluidsMultiplier += 3.75f * n2;
            this.fatigueMultiplier += 1.75f * n2;
        }
    }
    
    private void updateClothing() {
        this.character.getItemVisuals(Thermoregulator.itemVisuals);
        int n = (Thermoregulator.itemVisuals.size() != Thermoregulator.itemVisualsCache.size()) ? 1 : 0;
        if (n == 0) {
            for (int i = 0; i < Thermoregulator.itemVisuals.size(); ++i) {
                if (i >= Thermoregulator.itemVisualsCache.size() || Thermoregulator.itemVisuals.get(i) != Thermoregulator.itemVisualsCache.get(i)) {
                    n = 1;
                    break;
                }
            }
        }
        if (n != 0) {
            for (int j = 0; j < this.nodes.length; ++j) {
                this.nodes[j].clothing.clear();
            }
            Thermoregulator.itemVisualsCache.clear();
            for (int k = 0; k < Thermoregulator.itemVisuals.size(); ++k) {
                final ItemVisual e = Thermoregulator.itemVisuals.get(k);
                final InventoryItem inventoryItem = e.getInventoryItem();
                Thermoregulator.itemVisualsCache.add(e);
                if (inventoryItem instanceof Clothing) {
                    final Clothing clothing = (Clothing)inventoryItem;
                    if (clothing.getInsulation() > 0.0f || clothing.getWindresistance() > 0.0f) {
                        boolean b = false;
                        final ArrayList<BloodClothingType> bloodClothingType = inventoryItem.getBloodClothingType();
                        if (bloodClothingType != null) {
                            Thermoregulator.coveredParts.clear();
                            BloodClothingType.getCoveredParts(bloodClothingType, Thermoregulator.coveredParts);
                            for (int l = 0; l < Thermoregulator.coveredParts.size(); ++l) {
                                final BloodBodyPartType bloodBodyPartType = Thermoregulator.coveredParts.get(l);
                                if (bloodBodyPartType.index() >= 0 && bloodBodyPartType.index() < this.nodes.length) {
                                    b = true;
                                    this.nodes[bloodBodyPartType.index()].clothing.add(clothing);
                                }
                            }
                        }
                        if (!b && clothing.getBodyLocation() != null) {
                            final String lowerCase = clothing.getBodyLocation().toLowerCase();
                            switch (lowerCase) {
                                case "hat":
                                case "mask": {
                                    this.nodes[BodyPartType.ToIndex(BodyPartType.Head)].clothing.add(clothing);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public float getEnergy() {
        return 0.6f * (1.0f - (0.4f * this.stats.getHunger() + 0.6f * this.stats.getHunger() * this.stats.getHunger())) + 0.4f * (1.0f - (0.4f * this.stats.getFatigue() + 0.6f * this.stats.getFatigue() * this.stats.getFatigue()));
    }
    
    public float getBodyFluids() {
        return 1.0f - this.stats.getThirst();
    }
    
    static {
        itemVisuals = new ItemVisuals();
        itemVisualsCache = new ItemVisuals();
        coveredParts = new ArrayList<BloodBodyPartType>();
        Thermoregulator.SIMULATION_MULTIPLIER = 1.0f;
    }
    
    private enum Multiplier
    {
        Default, 
        MetabolicRateInc, 
        MetabolicRateDec, 
        BodyHeat, 
        CoreHeatExpand, 
        CoreHeatContract, 
        SkinCelcius, 
        SkinCelciusContract, 
        SkinCelciusExpand, 
        PrimaryDelta, 
        SecondaryDelta;
        
        private static /* synthetic */ Multiplier[] $values() {
            return new Multiplier[] { Multiplier.Default, Multiplier.MetabolicRateInc, Multiplier.MetabolicRateDec, Multiplier.BodyHeat, Multiplier.CoreHeatExpand, Multiplier.CoreHeatContract, Multiplier.SkinCelcius, Multiplier.SkinCelciusContract, Multiplier.SkinCelciusExpand, Multiplier.PrimaryDelta, Multiplier.SecondaryDelta };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public class ThermalNode
    {
        private final float distToCore;
        private final float skinSurface;
        private final BodyPartType bodyPartType;
        private final BloodBodyPartType bloodBPT;
        private final BodyPart bodyPart;
        private final boolean isCore;
        private final float insulationLayerMultiplierUI;
        private ThermalNode upstream;
        private ThermalNode[] downstream;
        private float insulation;
        private float windresist;
        private float celcius;
        private float skinCelcius;
        private float heatDelta;
        private float primaryDelta;
        private float secondaryDelta;
        private float clothingWetness;
        private float bodyWetness;
        private ArrayList<Clothing> clothing;
        
        public ThermalNode(final Thermoregulator thermoregulator, final float n, final BodyPart bodyPart, final float n2) {
            this(thermoregulator, false, n, bodyPart, n2);
        }
        
        public ThermalNode(final boolean isCore, final float celcius, final BodyPart bodyPart, final float insulationLayerMultiplierUI) {
            this.celcius = 37.0f;
            this.skinCelcius = 33.0f;
            this.heatDelta = 0.0f;
            this.primaryDelta = 0.0f;
            this.secondaryDelta = 0.0f;
            this.clothingWetness = 0.0f;
            this.bodyWetness = 0.0f;
            this.clothing = new ArrayList<Clothing>();
            this.isCore = isCore;
            this.celcius = celcius;
            this.distToCore = BodyPartType.GetDistToCore(bodyPart.Type);
            this.skinSurface = BodyPartType.GetSkinSurface(bodyPart.Type);
            this.bodyPartType = bodyPart.Type;
            this.bloodBPT = BloodBodyPartType.FromIndex(BodyPartType.ToIndex(bodyPart.Type));
            this.bodyPart = bodyPart;
            this.insulationLayerMultiplierUI = insulationLayerMultiplierUI;
        }
        
        private void calculateInsulation() {
            final int size = this.clothing.size();
            this.insulation = 0.0f;
            this.windresist = 0.0f;
            this.clothingWetness = 0.0f;
            this.bodyWetness = ((this.bodyPart != null) ? (this.bodyPart.getWetness() * 0.01f) : 0.0f);
            this.bodyWetness = PZMath.clamp_01(this.bodyWetness);
            if (size > 0) {
                for (int i = 0; i < size; ++i) {
                    final Clothing clothing = this.clothing.get(i);
                    final ItemVisual visual = clothing.getVisual();
                    final float clamp = PZMath.clamp(clothing.getWetness() * 0.01f, 0.0f, 1.0f);
                    this.clothingWetness += clamp;
                    if (visual.getHole(this.bloodBPT) <= 0.0f) {
                        final float trueInsulationValue = Temperature.getTrueInsulationValue(clothing.getInsulation());
                        final float trueWindresistanceValue = Temperature.getTrueWindresistanceValue(clothing.getWindresistance());
                        final float n = 0.5f + 0.5f * PZMath.clamp(clothing.getCurrentCondition() * 0.01f, 0.0f, 1.0f);
                        final float n2 = trueInsulationValue * ((1.0f - clamp * 0.75f) * n);
                        final float n3 = trueWindresistanceValue * ((1.0f - clamp * 0.45f) * n);
                        this.insulation += n2;
                        this.windresist += n3;
                    }
                }
                this.clothingWetness /= size;
                this.insulation += size * 0.05f;
                this.windresist += size * 0.05f;
            }
        }
        
        public boolean hasUpstream() {
            return this.upstream != null;
        }
        
        public boolean hasDownstream() {
            return this.downstream != null && this.downstream.length > 0;
        }
        
        public float getDistToCore() {
            return this.distToCore;
        }
        
        public float getSkinSurface() {
            return this.skinSurface;
        }
        
        public boolean isCore() {
            return this.isCore;
        }
        
        public float getInsulation() {
            return this.insulation;
        }
        
        public float getWindresist() {
            return this.windresist;
        }
        
        public float getCelcius() {
            return this.celcius;
        }
        
        public float getSkinCelcius() {
            return this.skinCelcius;
        }
        
        public float getHeatDelta() {
            return this.heatDelta;
        }
        
        public float getPrimaryDelta() {
            return this.primaryDelta;
        }
        
        public float getSecondaryDelta() {
            return this.secondaryDelta;
        }
        
        public float getClothingWetness() {
            return this.clothingWetness;
        }
        
        public float getBodyWetness() {
            return this.bodyWetness;
        }
        
        public float getBodyResponse() {
            return PZMath.lerp(this.primaryDelta, this.secondaryDelta, 0.5f);
        }
        
        public float getSkinCelciusUI() {
            final float clamp = PZMath.clamp(this.getSkinCelcius(), 20.0f, 42.0f);
            float n;
            if (clamp < 33.0f) {
                n = (clamp - 20.0f) / 13.0f * 0.5f;
            }
            else {
                n = 0.5f + (clamp - 33.0f) / 9.0f;
            }
            return n;
        }
        
        public float getHeatDeltaUI() {
            return PZMath.clamp((this.heatDelta * 0.2f + 1.0f) / 2.0f, 0.0f, 1.0f);
        }
        
        public float getPrimaryDeltaUI() {
            return PZMath.clamp((this.primaryDelta + 1.0f) / 2.0f, 0.0f, 1.0f);
        }
        
        public float getSecondaryDeltaUI() {
            return PZMath.clamp((this.secondaryDelta + 1.0f) / 2.0f, 0.0f, 1.0f);
        }
        
        public float getInsulationUI() {
            return PZMath.clamp(this.insulation * this.insulationLayerMultiplierUI, 0.0f, 1.0f);
        }
        
        public float getWindresistUI() {
            return PZMath.clamp(this.windresist * this.insulationLayerMultiplierUI, 0.0f, 1.0f);
        }
        
        public float getClothingWetnessUI() {
            return PZMath.clamp(this.clothingWetness, 0.0f, 1.0f);
        }
        
        public float getBodyWetnessUI() {
            return PZMath.clamp(this.bodyWetness, 0.0f, 1.0f);
        }
        
        public float getBodyResponseUI() {
            return PZMath.clamp((this.getBodyResponse() + 1.0f) / 2.0f, 0.0f, 1.0f);
        }
    }
}
