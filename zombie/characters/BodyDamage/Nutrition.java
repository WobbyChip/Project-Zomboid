// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.BodyDamage;

import zombie.characters.skills.PerkFactory;
import java.nio.ByteBuffer;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.State;
import zombie.ai.states.SwipeStatePlayer;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.GameTime;
import zombie.network.GameClient;
import zombie.SandboxOptions;
import zombie.network.GameServer;
import zombie.characters.IsoPlayer;

public final class Nutrition
{
    private IsoPlayer parent;
    private float carbohydrates;
    private float lipids;
    private float proteins;
    private float calories;
    private float carbohydratesDecreraseFemale;
    private float carbohydratesDecreraseMale;
    private float lipidsDecreraseFemale;
    private float lipidsDecreraseMale;
    private float proteinsDecreraseFemale;
    private float proteinsDecreraseMale;
    private float caloriesDecreraseFemaleNormal;
    private float caloriesDecreaseMaleNormal;
    private float caloriesDecreraseFemaleExercise;
    private float caloriesDecreaseMaleExercise;
    private float caloriesDecreraseFemaleSleeping;
    private float caloriesDecreaseMaleSleeping;
    private int caloriesToGainWeightMale;
    private int caloriesToGainWeightMaxMale;
    private int caloriesToGainWeightFemale;
    private int caloriesToGainWeightMaxFemale;
    private int caloriesDecreaseMax;
    private float weightGain;
    private float weightLoss;
    private float weight;
    private int updatedWeight;
    private boolean isFemale;
    private int syncWeightTimer;
    private float caloriesMax;
    private float caloriesMin;
    private boolean incWeight;
    private boolean incWeightLot;
    private boolean decWeight;
    
    public Nutrition(final IsoPlayer parent) {
        this.carbohydrates = 0.0f;
        this.lipids = 0.0f;
        this.proteins = 0.0f;
        this.calories = 0.0f;
        this.carbohydratesDecreraseFemale = 0.0032f;
        this.carbohydratesDecreraseMale = 0.0035f;
        this.lipidsDecreraseFemale = 7.0E-4f;
        this.lipidsDecreraseMale = 0.00113f;
        this.proteinsDecreraseFemale = 7.0E-4f;
        this.proteinsDecreraseMale = 8.6E-4f;
        this.caloriesDecreraseFemaleNormal = 1.0E-5f;
        this.caloriesDecreaseMaleNormal = 0.016f;
        this.caloriesDecreraseFemaleExercise = 1.0E-5f;
        this.caloriesDecreaseMaleExercise = 0.13f;
        this.caloriesDecreraseFemaleSleeping = 0.01f;
        this.caloriesDecreaseMaleSleeping = 0.003f;
        this.caloriesToGainWeightMale = 1100;
        this.caloriesToGainWeightMaxMale = 4000;
        this.caloriesToGainWeightFemale = 1000;
        this.caloriesToGainWeightMaxFemale = 3000;
        this.caloriesDecreaseMax = 2500;
        this.weightGain = 1.3E-5f;
        this.weightLoss = 8.5E-6f;
        this.weight = 60.0f;
        this.updatedWeight = 0;
        this.isFemale = false;
        this.syncWeightTimer = 0;
        this.caloriesMax = 0.0f;
        this.caloriesMin = 0.0f;
        this.incWeight = false;
        this.incWeightLot = false;
        this.decWeight = false;
        this.parent = parent;
        if (this.isFemale) {
            this.setWeight(60.0f);
        }
        else {
            this.setWeight(80.0f);
        }
        this.setCalories(800.0f);
    }
    
    public void update() {
        if (GameServer.bServer) {
            return;
        }
        if (!SandboxOptions.instance.Nutrition.getValue()) {
            return;
        }
        if (this.parent == null || this.parent.isDead()) {
            return;
        }
        if (GameClient.bClient && !this.parent.isLocalPlayer()) {
            return;
        }
        this.setCarbohydrates(this.getCarbohydrates() - (this.isFemale ? this.carbohydratesDecreraseFemale : this.carbohydratesDecreraseMale) * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
        this.setLipids(this.getLipids() - (this.isFemale ? this.lipidsDecreraseFemale : this.lipidsDecreraseMale) * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
        this.setProteins(this.getProteins() - (this.isFemale ? this.proteinsDecreraseFemale : this.proteinsDecreraseMale) * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
        this.updateCalories();
        this.updateWeight();
    }
    
    private void updateCalories() {
        float caloriesModifier = 1.0f;
        if (!this.parent.getCharacterActions().isEmpty()) {
            caloriesModifier = this.parent.getCharacterActions().get(0).caloriesModifier;
        }
        if (this.parent.isCurrentState(SwipeStatePlayer.instance()) || this.parent.isCurrentState(ClimbOverFenceState.instance()) || this.parent.isCurrentState(ClimbThroughWindowState.instance())) {
            caloriesModifier = 8.0f;
        }
        float n = 1.0f;
        if (this.parent.getBodyDamage() != null && this.parent.getBodyDamage().getThermoregulator() != null) {
            n = (float)this.parent.getBodyDamage().getThermoregulator().getEnergyMultiplier();
        }
        final float n2 = this.getWeight() / 80.0f;
        if (this.parent.IsRunning()) {
            this.setCalories(this.getCalories() - (this.isFemale ? this.caloriesDecreraseFemaleExercise : this.caloriesDecreaseMaleExercise) * 1.0f * n2 * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
        }
        else if (this.parent.isAsleep()) {
            this.setCalories(this.getCalories() - (this.isFemale ? this.caloriesDecreraseFemaleSleeping : this.caloriesDecreaseMaleSleeping) * caloriesModifier * n * n2 * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
        }
        else {
            this.setCalories(this.getCalories() - (this.isFemale ? this.caloriesDecreraseFemaleNormal : this.caloriesDecreaseMaleNormal) * caloriesModifier * n * n2 * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
        }
        if (this.getCalories() > this.caloriesMax) {
            this.caloriesMax = this.getCalories();
        }
        if (this.getCalories() < this.caloriesMin) {
            this.caloriesMin = this.getCalories();
        }
    }
    
    private void updateWeight() {
        if (this.parent.isGodMod()) {
            if (this.isFemale) {
                this.setWeight(60.0f);
            }
            else {
                this.setWeight(80.0f);
            }
            this.setCalories(1000.0f);
        }
        this.setIncWeight(false);
        this.setIncWeightLot(false);
        this.setDecWeight(false);
        final float n = (float)this.caloriesToGainWeightMale;
        float n2 = (float)this.caloriesToGainWeightMaxMale;
        if (this.isFemale) {
            final float n3 = (float)this.caloriesToGainWeightFemale;
            n2 = (float)this.caloriesToGainWeightMaxFemale;
        }
        final float n4 = 1600.0f + (this.getWeight() - 80.0f) * 40.0f;
        float n5 = (this.getWeight() - 70.0f) * 30.0f;
        if (n5 > 0.0f) {
            n5 = 0.0f;
        }
        if (this.getCalories() > n4) {
            this.setIncWeight(true);
            float n6 = this.getCalories() / n2;
            if (n6 > 1.0f) {
                n6 = 1.0f;
            }
            float weightGain = this.weightGain;
            if (this.getCarbohydrates() > 700.0f || this.getLipids() > 700.0f) {
                weightGain *= 3.0f;
                this.setIncWeightLot(true);
            }
            else if (this.getCarbohydrates() > 400.0f || this.getLipids() > 400.0f) {
                weightGain *= 2.0f;
                this.setIncWeightLot(true);
            }
            this.setWeight(this.getWeight() + weightGain * n6 * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
        }
        else if (this.getCalories() < n5) {
            this.setDecWeight(true);
            float n7 = Math.abs(this.getCalories()) / this.caloriesDecreaseMax;
            if (n7 > 1.0f) {
                n7 = 1.0f;
            }
            this.setWeight(this.getWeight() - this.weightLoss * n7 * GameTime.getInstance().getGameWorldSecondsSinceLastUpdate());
        }
        ++this.updatedWeight;
        if (this.updatedWeight >= 2000) {
            this.applyTraitFromWeight();
            this.updatedWeight = 0;
        }
        if (GameClient.bClient) {
            ++this.syncWeightTimer;
            if (this.syncWeightTimer >= 5000) {
                GameClient.sendWeight(this.parent);
                this.syncWeightTimer = 0;
            }
        }
    }
    
    public void save(final ByteBuffer byteBuffer) {
        byteBuffer.putFloat(this.getCalories());
        byteBuffer.putFloat(this.getProteins());
        byteBuffer.putFloat(this.getLipids());
        byteBuffer.putFloat(this.getCarbohydrates());
        byteBuffer.putFloat(this.getWeight());
    }
    
    public void load(final ByteBuffer byteBuffer) {
        this.setCalories(byteBuffer.getFloat());
        this.setProteins(byteBuffer.getFloat());
        this.setLipids(byteBuffer.getFloat());
        this.setCarbohydrates(byteBuffer.getFloat());
        this.setWeight(byteBuffer.getFloat());
    }
    
    public void applyWeightFromTraits() {
        if (this.parent.getTraits() != null && !this.parent.getTraits().isEmpty()) {
            if (this.parent.Traits.Emaciated.isSet()) {
                this.setWeight(50.0f);
            }
            if (this.parent.Traits.VeryUnderweight.isSet()) {
                this.setWeight(60.0f);
            }
            if (this.parent.Traits.Underweight.isSet()) {
                this.setWeight(70.0f);
            }
            if (this.parent.Traits.Overweight.isSet()) {
                this.setWeight(95.0f);
            }
            if (this.parent.Traits.Obese.isSet()) {
                this.setWeight(105.0f);
            }
        }
    }
    
    public void applyTraitFromWeight() {
        this.parent.getTraits().remove("Underweight");
        this.parent.getTraits().remove("Very Underweight");
        this.parent.getTraits().remove("Emaciated");
        this.parent.getTraits().remove("Overweight");
        this.parent.getTraits().remove("Obese");
        if (this.getWeight() >= 100.0f) {
            this.parent.getTraits().add("Obese");
        }
        if (this.getWeight() >= 85.0f && this.getWeight() < 100.0f) {
            this.parent.getTraits().add("Overweight");
        }
        if (this.getWeight() > 65.0f && this.getWeight() <= 75.0f) {
            this.parent.getTraits().add("Underweight");
        }
        if (this.getWeight() > 50.0f && this.getWeight() <= 65.0f) {
            this.parent.getTraits().add("Very Underweight");
        }
        if (this.getWeight() <= 50.0f) {
            this.parent.getTraits().add("Emaciated");
        }
    }
    
    public boolean characterHaveWeightTrouble() {
        return this.parent.Traits.Emaciated.isSet() || this.parent.Traits.Obese.isSet() || this.parent.Traits.VeryUnderweight.isSet() || this.parent.Traits.Underweight.isSet() || this.parent.Traits.Overweight.isSet();
    }
    
    public boolean canAddFitnessXp() {
        return (this.parent.getPerkLevel(PerkFactory.Perks.Fitness) < 9 || !this.characterHaveWeightTrouble()) && (this.parent.getPerkLevel(PerkFactory.Perks.Fitness) < 6 || (!this.parent.Traits.Emaciated.isSet() && !this.parent.Traits.Obese.isSet() && !this.parent.Traits.VeryUnderweight.isSet()));
    }
    
    public float getCarbohydrates() {
        return this.carbohydrates;
    }
    
    public void setCarbohydrates(float carbohydrates) {
        if (carbohydrates < -500.0f) {
            carbohydrates = -500.0f;
        }
        if (carbohydrates > 1000.0f) {
            carbohydrates = 1000.0f;
        }
        this.carbohydrates = carbohydrates;
    }
    
    public float getProteins() {
        return this.proteins;
    }
    
    public void setProteins(float proteins) {
        if (proteins < -500.0f) {
            proteins = -500.0f;
        }
        if (proteins > 1000.0f) {
            proteins = 1000.0f;
        }
        this.proteins = proteins;
    }
    
    public float getCalories() {
        return this.calories;
    }
    
    public void setCalories(float calories) {
        if (calories < -2200.0f) {
            calories = -2200.0f;
        }
        if (calories > 3700.0f) {
            calories = 3700.0f;
        }
        this.calories = calories;
    }
    
    public float getLipids() {
        return this.lipids;
    }
    
    public void setLipids(float lipids) {
        if (lipids < -500.0f) {
            lipids = -500.0f;
        }
        if (lipids > 1000.0f) {
            lipids = 1000.0f;
        }
        this.lipids = lipids;
    }
    
    public float getWeight() {
        return this.weight;
    }
    
    public void setWeight(float weight) {
        if (weight < 35.0f) {
            weight = 35.0f;
            this.parent.getBodyDamage().ReduceGeneralHealth(this.parent.getBodyDamage().getHealthReductionFromSevereBadMoodles() * GameTime.instance.getMultiplier());
        }
        this.weight = weight;
    }
    
    public boolean isIncWeight() {
        return this.incWeight;
    }
    
    public void setIncWeight(final boolean incWeight) {
        this.incWeight = incWeight;
    }
    
    public boolean isIncWeightLot() {
        return this.incWeightLot;
    }
    
    public void setIncWeightLot(final boolean incWeightLot) {
        this.incWeightLot = incWeightLot;
    }
    
    public boolean isDecWeight() {
        return this.decWeight;
    }
    
    public void setDecWeight(final boolean decWeight) {
        this.decWeight = decWeight;
    }
}
