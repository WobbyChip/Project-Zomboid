// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters.BodyDamage;

import zombie.GameWindow;
import java.nio.ByteBuffer;
import zombie.network.BodyDamageSync;
import zombie.core.math.PZMath;
import zombie.SandboxOptions;
import zombie.characters.IsoPlayer;
import zombie.inventory.types.Clothing;
import zombie.characterTextures.BloodBodyPartType;
import zombie.core.Rand;
import zombie.GameTime;
import zombie.characters.IsoGameCharacter;

public final class BodyPart
{
    BodyPartType Type;
    private float BiteDamage;
    private float BleedDamage;
    private float DamageScaler;
    private float Health;
    private boolean bandaged;
    private boolean bitten;
    private boolean bleeding;
    private boolean IsBleedingStemmed;
    private boolean IsCortorised;
    private boolean scratched;
    private boolean stitched;
    private boolean deepWounded;
    private boolean IsInfected;
    private boolean IsFakeInfected;
    private final IsoGameCharacter ParentChar;
    private float bandageLife;
    private float scratchTime;
    private float biteTime;
    private boolean alcoholicBandage;
    private float stiffness;
    private float woundInfectionLevel;
    private boolean infectedWound;
    private float ScratchDamage;
    private float CutDamage;
    private float WoundDamage;
    private float BurnDamage;
    private float BulletDamage;
    private float FractureDamage;
    private float bleedingTime;
    private float deepWoundTime;
    private boolean haveGlass;
    private float stitchTime;
    private float alcoholLevel;
    private float additionalPain;
    private String bandageType;
    private boolean getBandageXp;
    private boolean getStitchXp;
    private boolean getSplintXp;
    private float fractureTime;
    private boolean splint;
    private float splintFactor;
    private boolean haveBullet;
    private float burnTime;
    private boolean needBurnWash;
    private float lastTimeBurnWash;
    private String splintItem;
    private float plantainFactor;
    private float comfreyFactor;
    private float garlicFactor;
    private float cutTime;
    private boolean cut;
    private float scratchSpeedModifier;
    private float cutSpeedModifier;
    private float burnSpeedModifier;
    private float deepWoundSpeedModifier;
    private float wetness;
    protected Thermoregulator.ThermalNode thermalNode;
    
    public BodyPart(final BodyPartType type, final IsoGameCharacter parentChar) {
        this.BiteDamage = 2.1875f;
        this.BleedDamage = 0.2857143f;
        this.DamageScaler = 0.0057142857f;
        this.bandageLife = 0.0f;
        this.scratchTime = 0.0f;
        this.biteTime = 0.0f;
        this.alcoholicBandage = false;
        this.stiffness = 0.0f;
        this.woundInfectionLevel = 0.0f;
        this.infectedWound = false;
        this.ScratchDamage = 0.9375f;
        this.CutDamage = 1.875f;
        this.WoundDamage = 3.125f;
        this.BurnDamage = 3.75f;
        this.BulletDamage = 3.125f;
        this.FractureDamage = 3.125f;
        this.bleedingTime = 0.0f;
        this.deepWoundTime = 0.0f;
        this.haveGlass = false;
        this.stitchTime = 0.0f;
        this.alcoholLevel = 0.0f;
        this.additionalPain = 0.0f;
        this.bandageType = null;
        this.getBandageXp = true;
        this.getStitchXp = true;
        this.getSplintXp = true;
        this.fractureTime = 0.0f;
        this.splint = false;
        this.splintFactor = 0.0f;
        this.haveBullet = false;
        this.burnTime = 0.0f;
        this.needBurnWash = false;
        this.lastTimeBurnWash = 0.0f;
        this.splintItem = null;
        this.plantainFactor = 0.0f;
        this.comfreyFactor = 0.0f;
        this.garlicFactor = 0.0f;
        this.cutTime = 0.0f;
        this.cut = false;
        this.scratchSpeedModifier = 0.0f;
        this.cutSpeedModifier = 0.0f;
        this.burnSpeedModifier = 0.0f;
        this.deepWoundSpeedModifier = 0.0f;
        this.wetness = 0.0f;
        this.Type = type;
        this.ParentChar = parentChar;
        if (type == BodyPartType.Neck) {
            this.DamageScaler *= 5.0f;
        }
        if (type == BodyPartType.Hand_L || type == BodyPartType.Hand_R || type == BodyPartType.ForeArm_L || type == BodyPartType.ForeArm_R) {
            this.scratchSpeedModifier = 85.0f;
            this.cutSpeedModifier = 95.0f;
            this.burnSpeedModifier = 45.0f;
            this.deepWoundSpeedModifier = 60.0f;
        }
        if (type == BodyPartType.UpperArm_L || type == BodyPartType.UpperArm_R) {
            this.scratchSpeedModifier = 65.0f;
            this.cutSpeedModifier = 75.0f;
            this.burnSpeedModifier = 35.0f;
            this.deepWoundSpeedModifier = 40.0f;
        }
        if (type == BodyPartType.UpperLeg_L || type == BodyPartType.UpperLeg_R || type == BodyPartType.LowerLeg_L || type == BodyPartType.LowerLeg_R) {
            this.scratchSpeedModifier = 45.0f;
            this.cutSpeedModifier = 55.0f;
            this.burnSpeedModifier = 15.0f;
            this.deepWoundSpeedModifier = 20.0f;
        }
        if (type == BodyPartType.Foot_L || type == BodyPartType.Foot_R) {
            this.scratchSpeedModifier = 35.0f;
            this.cutSpeedModifier = 45.0f;
            this.burnSpeedModifier = 10.0f;
            this.deepWoundSpeedModifier = 15.0f;
        }
        this.RestoreToFullHealth();
    }
    
    public void AddDamage(final float n) {
        this.Health -= n;
        if (this.Health < 0.0f) {
            this.Health = 0.0f;
        }
    }
    
    public boolean isBandageDirty() {
        return this.getBandageLife() <= 0.0f;
    }
    
    public void DamageUpdate() {
        if (this.getDeepWoundTime() > 0.0f && !this.stitched()) {
            if (this.bandaged()) {
                this.Health -= this.WoundDamage / 2.0f * this.DamageScaler * GameTime.getInstance().getMultiplier();
            }
            else {
                this.Health -= this.WoundDamage * this.DamageScaler * GameTime.getInstance().getMultiplier();
            }
        }
        if (this.getScratchTime() > 0.0f && !this.bandaged()) {
            this.Health -= this.ScratchDamage * this.DamageScaler * GameTime.getInstance().getMultiplier();
        }
        if (this.getCutTime() > 0.0f && !this.bandaged()) {
            this.Health -= this.CutDamage * this.DamageScaler * GameTime.getInstance().getMultiplier();
        }
        if (this.getBiteTime() > 0.0f && !this.bandaged()) {
            this.Health -= this.BiteDamage * this.DamageScaler * GameTime.getInstance().getMultiplier();
        }
        if (this.getBleedingTime() > 0.0f && !this.bandaged()) {
            this.ParentChar.getBodyDamage().ReduceGeneralHealth(this.BleedDamage * this.DamageScaler * GameTime.getInstance().getMultiplier() * (this.getBleedingTime() / 10.0f));
            if (Rand.NextBool(Rand.AdjustForFramerate(1000))) {
                this.ParentChar.addBlood(BloodBodyPartType.FromIndex(BodyPartType.ToIndex(this.getType())), false, false, true);
            }
        }
        if (this.haveBullet()) {
            if (this.bandaged()) {
                this.Health -= this.BulletDamage / 2.0f * this.DamageScaler * GameTime.getInstance().getMultiplier();
            }
            else {
                this.Health -= this.BulletDamage * this.DamageScaler * GameTime.getInstance().getMultiplier();
            }
        }
        if (this.getBurnTime() > 0.0f && !this.bandaged()) {
            this.Health -= this.BurnDamage * this.DamageScaler * GameTime.getInstance().getMultiplier();
        }
        if (this.getFractureTime() > 0.0f && !this.isSplint()) {
            this.Health -= this.FractureDamage * this.DamageScaler * GameTime.getInstance().getMultiplier();
        }
        if (this.getBiteTime() > 0.0f) {
            if (this.bandaged()) {
                this.setBiteTime(this.getBiteTime() - (float)(1.0E-4 * GameTime.getInstance().getMultiplier()));
                this.setBandageLife(this.getBandageLife() - (float)(1.0E-4 * GameTime.getInstance().getMultiplier()));
            }
            else {
                this.setBiteTime(this.getBiteTime() - (float)(5.0E-6 * GameTime.getInstance().getMultiplier()));
            }
        }
        if (this.getBurnTime() > 0.0f) {
            if (this.bandaged()) {
                this.setBurnTime(this.getBurnTime() - (float)(1.0E-4 * GameTime.getInstance().getMultiplier()));
                this.setBandageLife(this.getBandageLife() - (float)(1.0E-4 * GameTime.getInstance().getMultiplier()));
            }
            else {
                this.setBurnTime(this.getBurnTime() - (float)(5.0E-6 * GameTime.getInstance().getMultiplier()));
            }
            if (this.getLastTimeBurnWash() - this.getBurnTime() >= 20.0f) {
                this.setLastTimeBurnWash(0.0f);
                this.setNeedBurnWash(true);
            }
        }
        if (this.getBleedingTime() > 0.0f) {
            if (this.bandaged()) {
                this.setBleedingTime(this.getBleedingTime() - (float)(2.0E-4 * GameTime.getInstance().getMultiplier()));
                if (this.getDeepWoundTime() > 0.0f) {
                    this.setBandageLife(this.getBandageLife() - (float)(0.005 * GameTime.getInstance().getMultiplier()));
                }
                else {
                    this.setBandageLife(this.getBandageLife() - (float)(3.0E-4 * GameTime.getInstance().getMultiplier()));
                }
            }
            else {
                this.setBleedingTime(this.getBleedingTime() - (float)(2.0E-5 * GameTime.getInstance().getMultiplier()));
            }
            if (this.getBleedingTime() < 3.0f && this.haveGlass()) {
                this.setBleedingTime(3.0f);
            }
            if (this.getBleedingTime() < 0.0f) {
                this.setBleedingTime(0.0f);
                this.setBleeding(false);
            }
        }
        if (!this.isInfectedWound() && !this.IsInfected && !this.alcoholicBandage && this.getAlcoholLevel() <= 0.0f && (this.getDeepWoundTime() > 0.0f || this.getScratchTime() > 0.0f || this.getCutTime() > 0.0f || this.getStitchTime() > 0.0f)) {
            int n = 40000;
            if (!this.bandaged()) {
                n -= 10000;
            }
            else if (this.getBandageLife() == 0.0f) {
                n -= 35000;
            }
            if (this.getScratchTime() > 0.0f) {
                n -= 20000;
            }
            if (this.getCutTime() > 0.0f) {
                n -= 25000;
            }
            if (this.getDeepWoundTime() > 0.0f) {
                n -= 30000;
            }
            if (this.haveGlass()) {
                n -= 24000;
            }
            if (this.getBurnTime() > 0.0f) {
                n -= 23000;
                if (this.isNeedBurnWash()) {
                    n -= 7000;
                }
            }
            if (BodyPartType.ToIndex(this.getType()) <= BodyPartType.ToIndex(BodyPartType.Torso_Lower) && this.ParentChar.getClothingItem_Torso() instanceof Clothing) {
                final Clothing clothing = (Clothing)this.ParentChar.getClothingItem_Torso();
                if (clothing.isDirty()) {
                    n -= 20000;
                }
                if (clothing.isBloody()) {
                    n -= 24000;
                }
            }
            if (BodyPartType.ToIndex(this.getType()) >= BodyPartType.ToIndex(BodyPartType.UpperLeg_L) && BodyPartType.ToIndex(this.getType()) <= BodyPartType.ToIndex(BodyPartType.LowerLeg_R) && this.ParentChar.getClothingItem_Legs() instanceof Clothing) {
                final Clothing clothing2 = (Clothing)this.ParentChar.getClothingItem_Legs();
                if (clothing2.isDirty()) {
                    n -= 20000;
                }
                if (clothing2.isBloody()) {
                    n -= 24000;
                }
            }
            if (n <= 5000) {
                n = 5000;
            }
            if (Rand.Next(Rand.AdjustForFramerate(n)) == 0) {
                this.setInfectedWound(true);
            }
        }
        else if (this.isInfectedWound()) {
            boolean b = false;
            if (this.getAlcoholLevel() > 0.0f) {
                this.setAlcoholLevel(this.getAlcoholLevel() - 2.0E-4f * GameTime.getInstance().getMultiplier());
                this.setWoundInfectionLevel(this.getWoundInfectionLevel() - 2.0E-4f * GameTime.getInstance().getMultiplier());
                if (this.getAlcoholLevel() < 0.0f) {
                    this.setAlcoholLevel(0.0f);
                }
                b = true;
            }
            if (this.ParentChar.getReduceInfectionPower() > 0.0f) {
                this.setWoundInfectionLevel(this.getWoundInfectionLevel() - 2.0E-4f * GameTime.getInstance().getMultiplier());
                this.ParentChar.setReduceInfectionPower(this.ParentChar.getReduceInfectionPower() - 2.0E-4f * GameTime.getInstance().getMultiplier());
                if (this.ParentChar.getReduceInfectionPower() < 0.0f) {
                    this.ParentChar.setReduceInfectionPower(0.0f);
                }
                b = true;
            }
            if (this.getGarlicFactor() > 0.0f) {
                this.setWoundInfectionLevel(this.getWoundInfectionLevel() - 2.0E-4f * GameTime.getInstance().getMultiplier());
                this.setGarlicFactor(this.getGarlicFactor() - 8.0E-4f * GameTime.getInstance().getMultiplier());
                b = true;
            }
            if (!b) {
                if (this.IsInfected) {
                    this.setWoundInfectionLevel(this.getWoundInfectionLevel() + 2.0E-4f * GameTime.getInstance().getMultiplier());
                }
                else if (this.haveGlass()) {
                    this.setWoundInfectionLevel(this.getWoundInfectionLevel() + 1.0E-4f * GameTime.getInstance().getMultiplier());
                }
                else {
                    this.setWoundInfectionLevel(this.getWoundInfectionLevel() + 1.0E-5f * GameTime.getInstance().getMultiplier());
                }
                if (this.getWoundInfectionLevel() > 10.0f) {
                    this.setWoundInfectionLevel(10.0f);
                }
            }
        }
        if (this.isInfectedWound() && this.getBandageLife() > 0.0f) {
            if (this.alcoholicBandage) {
                this.setWoundInfectionLevel(this.getWoundInfectionLevel() - 6.0E-4f * GameTime.getInstance().getMultiplier());
            }
            this.setBandageLife(this.getBandageLife() - (float)(2.0E-4 * GameTime.getInstance().getMultiplier()));
        }
        if (this.getScratchTime() > 0.0f) {
            if (this.bandaged()) {
                this.setScratchTime(this.getScratchTime() - (float)(1.5E-4 * GameTime.getInstance().getMultiplier()));
                this.setBandageLife(this.getBandageLife() - (float)(8.0E-5 * GameTime.getInstance().getMultiplier()));
                if (this.getPlantainFactor() > 0.0f) {
                    this.setScratchTime(this.getScratchTime() - (float)(1.0E-4 * GameTime.getInstance().getMultiplier()));
                    this.setPlantainFactor(this.getPlantainFactor() - (float)(8.0E-4 * GameTime.getInstance().getMultiplier()));
                }
            }
            else {
                this.setScratchTime(this.getScratchTime() - (float)(1.0E-5 * GameTime.getInstance().getMultiplier()));
            }
            if (this.getScratchTime() < 0.0f) {
                this.setScratchTime(0.0f);
                this.setGetBandageXp(true);
                this.setGetStitchXp(true);
                this.setScratched(false, false);
                this.setBleeding(false);
                this.setBleedingTime(0.0f);
            }
        }
        if (this.getCutTime() > 0.0f) {
            if (this.bandaged()) {
                this.setCutTime(this.getCutTime() - (float)(5.0E-5 * GameTime.getInstance().getMultiplier()));
                this.setBandageLife(this.getBandageLife() - (float)(1.0E-5 * GameTime.getInstance().getMultiplier()));
                if (this.getPlantainFactor() > 0.0f) {
                    this.setCutTime(this.getCutTime() - (float)(5.0E-5 * GameTime.getInstance().getMultiplier()));
                    this.setPlantainFactor(this.getPlantainFactor() - (float)(8.0E-4 * GameTime.getInstance().getMultiplier()));
                }
            }
            else {
                this.setCutTime(this.getCutTime() - (float)(1.0E-6 * GameTime.getInstance().getMultiplier()));
            }
            if (this.getCutTime() < 0.0f) {
                this.setCutTime(0.0f);
                this.setGetBandageXp(true);
                this.setGetStitchXp(true);
                this.setBleeding(false);
                this.setBleedingTime(0.0f);
            }
        }
        if (this.getDeepWoundTime() > 0.0f) {
            if (this.bandaged()) {
                this.setDeepWoundTime(this.getDeepWoundTime() - (float)(2.0E-5 * GameTime.getInstance().getMultiplier()));
                this.setBandageLife(this.getBandageLife() - (float)(1.0E-4 * GameTime.getInstance().getMultiplier()));
                if (this.getPlantainFactor() > 0.0f) {
                    this.setDeepWoundTime(this.getDeepWoundTime() - (float)(7.0E-6 * GameTime.getInstance().getMultiplier()));
                    this.setPlantainFactor(this.getPlantainFactor() - (float)(8.0E-4 * GameTime.getInstance().getMultiplier()));
                    if (this.getPlantainFactor() < 0.0f) {
                        this.setPlantainFactor(0.0f);
                    }
                }
            }
            else {
                this.setDeepWoundTime(this.getDeepWoundTime() - (float)(2.0E-6 * GameTime.getInstance().getMultiplier()));
            }
            if ((this.haveGlass() || !this.bandaged()) && this.getDeepWoundTime() < 3.0f) {
                this.setDeepWoundTime(3.0f);
            }
            if (this.getDeepWoundTime() < 0.0f) {
                this.setGetBandageXp(true);
                this.setGetStitchXp(true);
                this.setDeepWoundTime(0.0f);
                this.setDeepWounded(false);
            }
        }
        if (this.getStitchTime() > 0.0f && this.getStitchTime() < 50.0f) {
            if (this.bandaged()) {
                this.setStitchTime(this.getStitchTime() + (float)(4.0E-4 * GameTime.getInstance().getMultiplier()));
                this.setBandageLife(this.getBandageLife() - (float)(1.0E-4 * GameTime.getInstance().getMultiplier()));
                if (!this.alcoholicBandage && Rand.Next(Rand.AdjustForFramerate(80000)) == 0) {
                    this.setInfectedWound(true);
                }
                this.setStitchTime(this.getStitchTime() + (float)(1.0E-4 * GameTime.getInstance().getMultiplier()));
            }
            else {
                this.setStitchTime(this.getStitchTime() + (float)(2.0E-4 * GameTime.getInstance().getMultiplier()));
                if (Rand.Next(Rand.AdjustForFramerate(20000)) == 0) {
                    this.setInfectedWound(true);
                }
            }
            if (this.getStitchTime() > 30.0f) {
                this.setGetStitchXp(true);
            }
            if (this.getStitchTime() > 50.0f) {
                this.setStitchTime(50.0f);
            }
        }
        if (this.getFractureTime() > 0.0f) {
            if (this.getSplintFactor() > 0.0f) {
                this.setFractureTime(this.getFractureTime() - (float)(5.0E-5 * GameTime.getInstance().getMultiplier() * this.getSplintFactor()));
            }
            else {
                this.setFractureTime(this.getFractureTime() - (float)(5.0E-6 * GameTime.getInstance().getMultiplier()));
            }
            if (this.getComfreyFactor() > 0.0f) {
                this.setFractureTime(this.getFractureTime() - (float)(5.0E-6 * GameTime.getInstance().getMultiplier()));
                this.setComfreyFactor(this.getComfreyFactor() - (float)(5.0E-4 * GameTime.getInstance().getMultiplier()));
            }
            if (this.getFractureTime() < 0.0f) {
                this.setFractureTime(0.0f);
                this.setGetSplintXp(true);
            }
        }
        if (this.getAdditionalPain() > 0.0f) {
            this.setAdditionalPain(this.getAdditionalPain() - (float)(0.005 * GameTime.getInstance().getMultiplier()));
            if (this.getAdditionalPain() < 0.0f) {
                this.setAdditionalPain(0.0f);
            }
        }
        if (this.getStiffness() > 0.0f && this.ParentChar instanceof IsoPlayer && ((IsoPlayer)this.ParentChar).getFitness() != null && !((IsoPlayer)this.ParentChar).getFitness().onGoingStiffness()) {
            this.setStiffness(this.getStiffness() - (float)(0.002 * GameTime.getInstance().getMultiplier()));
            if (this.getStiffness() < 0.0f) {
                this.setStiffness(0.0f);
            }
        }
        if (this.getBandageLife() < 0.0f) {
            this.setBandageLife(0.0f);
            this.setGetBandageXp(true);
        }
        if ((this.getWoundInfectionLevel() > 0.0f || this.isInfectedWound()) && this.getBurnTime() <= 0.0f && this.getFractureTime() <= 0.0f && this.getDeepWoundTime() <= 0.0f && this.getScratchTime() <= 0.0f && this.getBiteTime() <= 0.0f) {
            this.setWoundInfectionLevel(0.0f);
        }
        if (this.Health < 0.0f) {
            this.Health = 0.0f;
        }
    }
    
    public float getHealth() {
        return this.Health;
    }
    
    public void SetHealth(final float health) {
        this.Health = health;
    }
    
    public void AddHealth(final float n) {
        this.Health += n;
        if (this.Health > 100.0f) {
            this.Health = 100.0f;
        }
    }
    
    public void ReduceHealth(final float n) {
        this.Health -= n;
        if (this.Health < 0.0f) {
            this.Health = 0.0f;
        }
    }
    
    public boolean HasInjury() {
        return this.bitten | this.scratched | this.deepWounded | this.bleeding | this.getBiteTime() > 0.0f | this.getScratchTime() > 0.0f | this.getCutTime() > 0.0f | this.getFractureTime() > 0.0f | this.haveBullet() | this.getBurnTime() > 0.0f;
    }
    
    public boolean bandaged() {
        return this.bandaged;
    }
    
    public boolean bitten() {
        return this.bitten;
    }
    
    public boolean bleeding() {
        return this.bleeding;
    }
    
    public boolean IsBleedingStemmed() {
        return this.IsBleedingStemmed;
    }
    
    public boolean IsCortorised() {
        return this.IsCortorised;
    }
    
    public boolean IsInfected() {
        return this.IsInfected;
    }
    
    public void SetInfected(final boolean isInfected) {
        this.IsInfected = isInfected;
    }
    
    public void SetFakeInfected(final boolean isFakeInfected) {
        this.IsFakeInfected = isFakeInfected;
    }
    
    public boolean IsFakeInfected() {
        return this.IsFakeInfected;
    }
    
    public void DisableFakeInfection() {
        this.IsFakeInfected = false;
    }
    
    public boolean scratched() {
        return this.scratched;
    }
    
    public boolean stitched() {
        return this.stitched;
    }
    
    public boolean deepWounded() {
        return this.deepWounded;
    }
    
    public void RestoreToFullHealth() {
        this.Health = 100.0f;
        this.additionalPain = 0.0f;
        this.alcoholicBandage = false;
        this.alcoholLevel = 0.0f;
        this.bleeding = false;
        this.bandaged = false;
        this.bandageLife = 0.0f;
        this.biteTime = 0.0f;
        this.bitten = false;
        this.bleedingTime = 0.0f;
        this.burnTime = 0.0f;
        this.comfreyFactor = 0.0f;
        this.deepWounded = false;
        this.deepWoundTime = 0.0f;
        this.fractureTime = 0.0f;
        this.garlicFactor = 0.0f;
        this.haveBullet = false;
        this.haveGlass = false;
        this.infectedWound = false;
        this.IsBleedingStemmed = false;
        this.IsCortorised = false;
        this.IsFakeInfected = false;
        this.IsInfected = false;
        this.lastTimeBurnWash = 0.0f;
        this.needBurnWash = false;
        this.plantainFactor = 0.0f;
        this.scratched = false;
        this.scratchTime = 0.0f;
        this.splint = false;
        this.splintFactor = 0.0f;
        this.splintItem = null;
        this.stitched = false;
        this.stitchTime = 0.0f;
        this.woundInfectionLevel = 0.0f;
        this.cutTime = 0.0f;
        this.cut = false;
    }
    
    public void setBandaged(final boolean b, final float n) {
        this.setBandaged(b, n, false, null);
    }
    
    public void setBandaged(final boolean bandaged, final float bandageLife, final boolean alcoholicBandage, final String bandageType) {
        if (bandaged) {
            if (this.bleeding) {
                this.bleeding = false;
            }
            this.bitten = false;
            this.scratched = false;
            this.cut = false;
            this.alcoholicBandage = alcoholicBandage;
            this.stitched = false;
            this.deepWounded = false;
            this.setBandageType(bandageType);
            this.setGetBandageXp(false);
        }
        else {
            if (this.getScratchTime() > 0.0f) {
                this.scratched = true;
            }
            if (this.getCutTime() > 0.0f) {
                this.cut = true;
            }
            if (this.getBleedingTime() > 0.0f) {
                this.bleeding = true;
            }
            if (this.getBiteTime() > 0.0f) {
                this.bitten = true;
            }
            if (this.getStitchTime() > 0.0f) {
                this.stitched = true;
            }
            if (this.getDeepWoundTime() > 0.0f) {
                this.deepWounded = true;
            }
        }
        this.setBandageLife(bandageLife);
        this.bandaged = bandaged;
    }
    
    public void SetBitten(final boolean bitten) {
        this.bitten = bitten;
        if (bitten) {
            this.bleeding = true;
            this.IsBleedingStemmed = false;
            this.IsCortorised = false;
            this.bandaged = false;
            this.setInfectedWound(true);
            this.setBiteTime(Rand.Next(50.0f, 80.0f));
            if (this.ParentChar.Traits.FastHealer.isSet()) {
                this.setBiteTime(Rand.Next(30.0f, 50.0f));
            }
            if (this.ParentChar.Traits.SlowHealer.isSet()) {
                this.setBiteTime(Rand.Next(80.0f, 150.0f));
            }
        }
        if (SandboxOptions.instance.Lore.Transmission.getValue() != 4) {
            this.IsInfected = true;
            this.IsFakeInfected = false;
        }
        if (this.IsInfected && SandboxOptions.instance.Lore.Mortality.getValue() == 7) {
            this.IsInfected = false;
            this.IsFakeInfected = true;
        }
        this.generateBleeding();
    }
    
    public void SetBitten(final boolean bitten, boolean b) {
        this.bitten = bitten;
        if (SandboxOptions.instance.Lore.Transmission.getValue() == 4) {
            this.IsInfected = false;
            this.IsFakeInfected = false;
            b = false;
        }
        if (bitten) {
            this.bleeding = true;
            this.IsBleedingStemmed = false;
            this.IsCortorised = false;
            this.bandaged = false;
            if (b) {
                this.IsInfected = true;
            }
            this.IsFakeInfected = false;
            if (this.IsInfected && SandboxOptions.instance.Lore.Mortality.getValue() == 7) {
                this.IsInfected = false;
                this.IsFakeInfected = true;
            }
        }
    }
    
    public void setBleeding(final boolean bleeding) {
        this.bleeding = bleeding;
    }
    
    public void SetBleedingStemmed(final boolean b) {
        if (this.bleeding) {
            this.bleeding = false;
            this.IsBleedingStemmed = true;
        }
    }
    
    public void SetCortorised(final boolean isCortorised) {
        this.IsCortorised = isCortorised;
        if (isCortorised) {
            this.bleeding = false;
            this.IsBleedingStemmed = false;
            this.deepWounded = false;
            this.bandaged = false;
        }
    }
    
    public void setCut(final boolean b) {
        this.setCut(b, true);
    }
    
    public void setCut(final boolean cut, final boolean b) {
        this.cut = cut;
        if (cut) {
            this.setStitched(false);
            this.setBandaged(false, 0.0f);
            float cutTime = Rand.Next(10.0f, 20.0f);
            if (this.ParentChar.Traits.FastHealer.isSet()) {
                cutTime = Rand.Next(5.0f, 10.0f);
            }
            if (this.ParentChar.Traits.SlowHealer.isSet()) {
                cutTime = Rand.Next(20.0f, 30.0f);
            }
            switch (SandboxOptions.instance.InjurySeverity.getValue()) {
                case 1: {
                    cutTime *= 0.5f;
                    break;
                }
                case 3: {
                    cutTime *= 1.5f;
                    break;
                }
            }
            this.setCutTime(cutTime);
            this.generateBleeding();
            if (!b) {
                this.generateZombieInfection(25);
            }
        }
        else {
            this.setBleeding(false);
        }
    }
    
    public void generateZombieInfection(final int n) {
        if (Rand.Next(100) < n) {
            this.IsInfected = true;
        }
        if (!this.IsInfected && this.ParentChar.Traits.Hypercondriac.isSet() && Rand.Next(100) < 80) {
            this.IsFakeInfected = true;
        }
        if (SandboxOptions.instance.Lore.Transmission.getValue() == 2 || SandboxOptions.instance.Lore.Transmission.getValue() == 4) {
            this.IsInfected = false;
            this.IsFakeInfected = false;
        }
        if (this.IsInfected && SandboxOptions.instance.Lore.Mortality.getValue() == 7) {
            this.IsInfected = false;
            this.IsFakeInfected = true;
        }
    }
    
    public void setScratched(final boolean scratched, final boolean b) {
        this.scratched = scratched;
        if (scratched) {
            this.setStitched(false);
            this.setBandaged(false, 0.0f);
            float scratchTime = Rand.Next(7.0f, 15.0f);
            if (this.ParentChar.Traits.FastHealer.isSet()) {
                scratchTime = Rand.Next(4.0f, 10.0f);
            }
            if (this.ParentChar.Traits.SlowHealer.isSet()) {
                scratchTime = Rand.Next(15.0f, 25.0f);
            }
            switch (SandboxOptions.instance.InjurySeverity.getValue()) {
                case 1: {
                    this.scratchTime *= 0.5f;
                    break;
                }
                case 3: {
                    this.scratchTime *= 1.5f;
                    break;
                }
            }
            this.setScratchTime(scratchTime);
            this.generateBleeding();
            if (!b) {
                this.generateZombieInfection(7);
            }
        }
        else {
            this.setBleeding(false);
        }
    }
    
    public void SetScratchedWeapon(final boolean scratched) {
        this.scratched = scratched;
        if (scratched) {
            this.setStitched(false);
            this.setBandaged(false, 0.0f);
            float scratchTime = Rand.Next(5.0f, 10.0f);
            if (this.ParentChar.Traits.FastHealer.isSet()) {
                scratchTime = Rand.Next(1.0f, 5.0f);
            }
            if (this.ParentChar.Traits.SlowHealer.isSet()) {
                scratchTime = Rand.Next(10.0f, 20.0f);
            }
            switch (SandboxOptions.instance.InjurySeverity.getValue()) {
                case 1: {
                    this.scratchTime *= 0.5f;
                    break;
                }
                case 3: {
                    this.scratchTime *= 1.5f;
                    break;
                }
            }
            this.setScratchTime(scratchTime);
            this.generateBleeding();
        }
    }
    
    public void generateDeepWound() {
        float deepWoundTime = Rand.Next(15.0f, 20.0f);
        if (this.ParentChar.Traits.FastHealer.isSet()) {
            deepWoundTime = Rand.Next(11.0f, 15.0f);
        }
        else if (this.ParentChar.Traits.SlowHealer.isSet()) {
            deepWoundTime = Rand.Next(20.0f, 32.0f);
        }
        switch (SandboxOptions.instance.InjurySeverity.getValue()) {
            case 1: {
                deepWoundTime *= 0.5f;
                break;
            }
            case 3: {
                deepWoundTime *= 1.5f;
                break;
            }
        }
        this.setDeepWoundTime(deepWoundTime);
        this.setDeepWounded(true);
        this.generateBleeding();
    }
    
    public void generateDeepShardWound() {
        float deepWoundTime = Rand.Next(15.0f, 20.0f);
        if (this.ParentChar.Traits.FastHealer.isSet()) {
            deepWoundTime = Rand.Next(11.0f, 15.0f);
        }
        else if (this.ParentChar.Traits.SlowHealer.isSet()) {
            deepWoundTime = Rand.Next(20.0f, 32.0f);
        }
        switch (SandboxOptions.instance.InjurySeverity.getValue()) {
            case 1: {
                deepWoundTime *= 0.5f;
                break;
            }
            case 3: {
                deepWoundTime *= 1.5f;
                break;
            }
        }
        this.setDeepWoundTime(deepWoundTime);
        this.setHaveGlass(true);
        this.setDeepWounded(true);
        this.generateBleeding();
    }
    
    public void SetScratchedWindow(final boolean scratched) {
        if (scratched) {
            this.setBandaged(false, 0.0f);
            this.setStitched(false);
            if (Rand.Next(7) == 0) {
                this.generateDeepShardWound();
            }
            else {
                this.scratched = scratched;
                float scratchTime = Rand.Next(12.0f, 20.0f);
                if (this.ParentChar.Traits.FastHealer.isSet()) {
                    scratchTime = Rand.Next(5.0f, 10.0f);
                }
                if (this.ParentChar.Traits.SlowHealer.isSet()) {
                    scratchTime = Rand.Next(20.0f, 30.0f);
                }
                switch (SandboxOptions.instance.InjurySeverity.getValue()) {
                    case 1: {
                        this.scratchTime *= 0.5f;
                        break;
                    }
                    case 3: {
                        this.scratchTime *= 1.5f;
                        break;
                    }
                }
                this.setScratchTime(scratchTime);
            }
            this.generateBleeding();
        }
    }
    
    public void setStitched(final boolean stitched) {
        if (stitched) {
            this.setBleedingTime(0.0f);
            this.setBleeding(false);
            this.setDeepWoundTime(0.0f);
            this.setDeepWounded(false);
            this.setGetStitchXp(false);
        }
        else if (this.stitched) {
            this.stitched = false;
            if (this.getStitchTime() < 40.0f) {
                this.setDeepWoundTime(Rand.Next(10.0f, this.getStitchTime()));
                this.setBleedingTime(Rand.Next(10.0f, this.getStitchTime()));
                this.setStitchTime(0.0f);
                this.setDeepWounded(true);
            }
            else {
                this.setScratchTime(Rand.Next(2.0f, this.getStitchTime() - 40.0f));
                this.scratched = true;
                this.setStitchTime(0.0f);
            }
        }
        this.stitched = stitched;
    }
    
    public void damageFromFirearm(final float n) {
        this.setHaveBullet(true, 0);
    }
    
    public float getPain() {
        float n = 0.0f;
        if (this.getScratchTime() > 0.0f) {
            n += this.getScratchTime() * 1.7f;
        }
        if (this.getCutTime() > 0.0f) {
            n += this.getCutTime() * 2.5f;
        }
        if (this.getBiteTime() > 0.0f) {
            if (this.bandaged()) {
                n += 30.0f;
            }
            else if (!this.bandaged()) {
                n += 50.0f;
            }
        }
        if (this.getDeepWoundTime() > 0.0f) {
            n += this.getDeepWoundTime() * 3.7f;
        }
        if (this.getStitchTime() > 0.0f && this.getStitchTime() < 35.0f) {
            if (this.bandaged()) {
                n += (35.0f - this.getStitchTime()) / 2.0f;
            }
            else {
                n += 35.0f - this.getStitchTime();
            }
        }
        if (this.getFractureTime() > 0.0f) {
            if (this.getSplintFactor() > 0.0f) {
                n += this.getFractureTime() / 2.0f;
            }
            else {
                n += this.getFractureTime();
            }
        }
        if (this.haveBullet()) {
            n += 50.0f;
        }
        if (this.haveGlass()) {
            n += 10.0f;
        }
        if (this.getBurnTime() > 0.0f) {
            n += this.getBurnTime();
        }
        if (this.bandaged()) {
            n /= 1.5f;
        }
        if (this.getWoundInfectionLevel() > 0.0f) {
            n += this.getWoundInfectionLevel();
        }
        float n2 = n + this.getAdditionalPain(true);
        switch (SandboxOptions.instance.InjurySeverity.getValue()) {
            case 1: {
                n2 *= 0.7f;
                break;
            }
            case 3: {
                n2 *= 1.3f;
                break;
            }
        }
        return n2;
    }
    
    public float getBiteTime() {
        return this.biteTime;
    }
    
    public void setBiteTime(final float biteTime) {
        this.biteTime = biteTime;
    }
    
    public float getDeepWoundTime() {
        return this.deepWoundTime;
    }
    
    public void setDeepWoundTime(final float deepWoundTime) {
        this.deepWoundTime = deepWoundTime;
    }
    
    public boolean haveGlass() {
        return this.haveGlass;
    }
    
    public void setHaveGlass(final boolean haveGlass) {
        this.haveGlass = haveGlass;
    }
    
    public float getStitchTime() {
        return this.stitchTime;
    }
    
    public void setStitchTime(final float stitchTime) {
        this.stitchTime = stitchTime;
    }
    
    public int getIndex() {
        return BodyPartType.ToIndex(this.Type);
    }
    
    public float getAlcoholLevel() {
        return this.alcoholLevel;
    }
    
    public void setAlcoholLevel(final float alcoholLevel) {
        this.alcoholLevel = alcoholLevel;
    }
    
    public float getAdditionalPain(final boolean b) {
        if (b) {
            return this.additionalPain + this.stiffness / 3.5f;
        }
        return this.additionalPain;
    }
    
    public float getAdditionalPain() {
        return this.additionalPain;
    }
    
    public void setAdditionalPain(final float additionalPain) {
        this.additionalPain = additionalPain;
    }
    
    public String getBandageType() {
        return this.bandageType;
    }
    
    public void setBandageType(final String bandageType) {
        this.bandageType = bandageType;
    }
    
    public boolean isGetBandageXp() {
        return this.getBandageXp;
    }
    
    public void setGetBandageXp(final boolean getBandageXp) {
        this.getBandageXp = getBandageXp;
    }
    
    public boolean isGetStitchXp() {
        return this.getStitchXp;
    }
    
    public void setGetStitchXp(final boolean getStitchXp) {
        this.getStitchXp = getStitchXp;
    }
    
    public float getSplintFactor() {
        return this.splintFactor;
    }
    
    public void setSplintFactor(final float splintFactor) {
        this.splintFactor = splintFactor;
    }
    
    public float getFractureTime() {
        return this.fractureTime;
    }
    
    public void setFractureTime(final float fractureTime) {
        this.fractureTime = fractureTime;
    }
    
    public boolean isGetSplintXp() {
        return this.getSplintXp;
    }
    
    public void setGetSplintXp(final boolean getSplintXp) {
        this.getSplintXp = getSplintXp;
    }
    
    public boolean isSplint() {
        return this.splint;
    }
    
    public void setSplint(final boolean splint, final float splintFactor) {
        this.splint = splint;
        this.setSplintFactor(splintFactor);
        if (splint) {
            this.setGetSplintXp(false);
        }
    }
    
    public boolean haveBullet() {
        return this.haveBullet;
    }
    
    public void setHaveBullet(final boolean haveBullet, final int n) {
        if (this.haveBullet && !haveBullet) {
            float deepWoundTime = Rand.Next(17.0f, 23.0f) - n / 2;
            if (this.ParentChar != null && this.ParentChar.Traits != null) {
                if (this.ParentChar.Traits.FastHealer.isSet()) {
                    deepWoundTime = Rand.Next(12.0f, 18.0f) - n / 2;
                }
                else if (this.ParentChar.Traits.SlowHealer.isSet()) {
                    deepWoundTime = Rand.Next(22.0f, 28.0f) - n / 2;
                }
            }
            switch (SandboxOptions.instance.InjurySeverity.getValue()) {
                case 1: {
                    deepWoundTime *= 0.5f;
                    break;
                }
                case 3: {
                    deepWoundTime *= 1.5f;
                    break;
                }
            }
            this.setDeepWoundTime(deepWoundTime);
            this.setDeepWounded(true);
            this.haveBullet = false;
            this.generateBleeding();
        }
        else if (haveBullet) {
            this.haveBullet = true;
            this.generateBleeding();
        }
        this.haveBullet = haveBullet;
    }
    
    public float getBurnTime() {
        return this.burnTime;
    }
    
    public void setBurnTime(final float burnTime) {
        this.burnTime = burnTime;
    }
    
    public boolean isNeedBurnWash() {
        return this.needBurnWash;
    }
    
    public void setNeedBurnWash(final boolean needBurnWash) {
        if (this.needBurnWash && !needBurnWash) {
            this.setLastTimeBurnWash(this.getBurnTime());
        }
        this.needBurnWash = needBurnWash;
    }
    
    public float getLastTimeBurnWash() {
        return this.lastTimeBurnWash;
    }
    
    public void setLastTimeBurnWash(final float lastTimeBurnWash) {
        this.lastTimeBurnWash = lastTimeBurnWash;
    }
    
    public boolean isInfectedWound() {
        return this.infectedWound;
    }
    
    public void setInfectedWound(final boolean infectedWound) {
        this.infectedWound = infectedWound;
    }
    
    public BodyPartType getType() {
        return this.Type;
    }
    
    public float getBleedingTime() {
        return this.bleedingTime;
    }
    
    public void setBleedingTime(final float bleedingTime) {
        this.bleedingTime = bleedingTime;
        if (!this.bandaged()) {
            this.setBleeding(bleedingTime > 0.0f);
        }
    }
    
    public boolean isDeepWounded() {
        return this.deepWounded;
    }
    
    public void setDeepWounded(final boolean deepWounded) {
        this.deepWounded = deepWounded;
        if (deepWounded) {
            this.bleeding = true;
            this.IsBleedingStemmed = false;
            this.IsCortorised = false;
            this.bandaged = false;
            this.stitched = false;
        }
    }
    
    public float getBandageLife() {
        return this.bandageLife;
    }
    
    public void setBandageLife(final float bandageLife) {
        this.bandageLife = bandageLife;
        if (this.bandageLife <= 0.0f) {
            this.alcoholicBandage = false;
        }
    }
    
    public float getScratchTime() {
        return this.scratchTime;
    }
    
    public void setScratchTime(float min) {
        min = Math.min(100.0f, min);
        this.scratchTime = min;
    }
    
    public float getWoundInfectionLevel() {
        return this.woundInfectionLevel;
    }
    
    public void setWoundInfectionLevel(final float woundInfectionLevel) {
        this.woundInfectionLevel = woundInfectionLevel;
        if (this.woundInfectionLevel <= 0.0f) {
            this.setInfectedWound(false);
            if (this.woundInfectionLevel < -2.0f) {
                this.woundInfectionLevel = -2.0f;
            }
        }
        else {
            this.setInfectedWound(true);
        }
    }
    
    public void setBurned() {
        float next = Rand.Next(50.0f, 100.0f);
        switch (SandboxOptions.instance.InjurySeverity.getValue()) {
            case 1: {
                next *= 0.5f;
                break;
            }
            case 3: {
                next *= 1.5f;
                break;
            }
        }
        this.setBurnTime(next);
        this.setNeedBurnWash(true);
        this.setLastTimeBurnWash(0.0f);
    }
    
    public String getSplintItem() {
        return this.splintItem;
    }
    
    public void setSplintItem(final String splintItem) {
        this.splintItem = splintItem;
    }
    
    public float getPlantainFactor() {
        return this.plantainFactor;
    }
    
    public void setPlantainFactor(final float n) {
        this.plantainFactor = PZMath.clamp(n, 0.0f, 100.0f);
    }
    
    public float getGarlicFactor() {
        return this.garlicFactor;
    }
    
    public void setGarlicFactor(final float n) {
        this.garlicFactor = PZMath.clamp(n, 0.0f, 100.0f);
    }
    
    public float getComfreyFactor() {
        return this.comfreyFactor;
    }
    
    public void setComfreyFactor(final float n) {
        this.comfreyFactor = PZMath.clamp(n, 0.0f, 100.0f);
    }
    
    public void sync(final BodyPart bodyPart, final BodyDamageSync.Updater updater) {
        if (updater.updateField((byte)1, this.Health, bodyPart.Health)) {
            bodyPart.Health = this.Health;
        }
        if (this.bandaged != bodyPart.bandaged) {
            updater.updateField((byte)2, this.bandaged);
            bodyPart.bandaged = this.bandaged;
        }
        if (this.bitten != bodyPart.bitten) {
            updater.updateField((byte)3, this.bitten);
            bodyPart.bitten = this.bitten;
        }
        if (this.bleeding != bodyPart.bleeding) {
            updater.updateField((byte)4, this.bleeding);
            bodyPart.bleeding = this.bleeding;
        }
        if (this.IsBleedingStemmed != bodyPart.IsBleedingStemmed) {
            updater.updateField((byte)5, this.IsBleedingStemmed);
            bodyPart.IsBleedingStemmed = this.IsBleedingStemmed;
        }
        if (this.scratched != bodyPart.scratched) {
            updater.updateField((byte)7, this.scratched);
            bodyPart.scratched = this.scratched;
        }
        if (this.cut != bodyPart.cut) {
            updater.updateField((byte)39, this.cut);
            bodyPart.cut = this.cut;
        }
        if (this.stitched != bodyPart.stitched) {
            updater.updateField((byte)8, this.stitched);
            bodyPart.stitched = this.stitched;
        }
        if (this.deepWounded != bodyPart.deepWounded) {
            updater.updateField((byte)9, this.deepWounded);
            bodyPart.deepWounded = this.deepWounded;
        }
        if (this.IsInfected != bodyPart.IsInfected) {
            updater.updateField((byte)10, this.IsInfected);
            bodyPart.IsInfected = this.IsInfected;
        }
        if (this.IsFakeInfected != bodyPart.IsFakeInfected) {
            updater.updateField((byte)11, this.IsFakeInfected);
            bodyPart.IsFakeInfected = this.IsFakeInfected;
        }
        if (updater.updateField((byte)12, this.bandageLife, bodyPart.bandageLife)) {
            bodyPart.bandageLife = this.bandageLife;
        }
        if (updater.updateField((byte)13, this.scratchTime, bodyPart.scratchTime)) {
            bodyPart.scratchTime = this.scratchTime;
        }
        if (updater.updateField((byte)14, this.biteTime, bodyPart.biteTime)) {
            bodyPart.biteTime = this.biteTime;
        }
        if (this.alcoholicBandage != bodyPart.alcoholicBandage) {
            updater.updateField((byte)15, this.alcoholicBandage);
            bodyPart.alcoholicBandage = this.alcoholicBandage;
        }
        if (updater.updateField((byte)16, this.woundInfectionLevel, bodyPart.woundInfectionLevel)) {
            bodyPart.woundInfectionLevel = this.woundInfectionLevel;
        }
        if (this.infectedWound != bodyPart.infectedWound) {
            updater.updateField((byte)17, this.infectedWound);
            bodyPart.infectedWound = this.infectedWound;
        }
        if (updater.updateField((byte)18, this.bleedingTime, bodyPart.bleedingTime)) {
            bodyPart.bleedingTime = this.bleedingTime;
        }
        if (updater.updateField((byte)19, this.deepWoundTime, bodyPart.deepWoundTime)) {
            bodyPart.deepWoundTime = this.deepWoundTime;
        }
        if (updater.updateField((byte)40, this.cutTime, bodyPart.cutTime)) {
            bodyPart.cutTime = this.cutTime;
        }
        if (this.haveGlass != bodyPart.haveGlass) {
            updater.updateField((byte)20, this.haveGlass);
            bodyPart.haveGlass = this.haveGlass;
        }
        if (updater.updateField((byte)21, this.stitchTime, bodyPart.stitchTime)) {
            bodyPart.stitchTime = this.stitchTime;
        }
        if (updater.updateField((byte)22, this.alcoholLevel, bodyPart.alcoholLevel)) {
            bodyPart.alcoholLevel = this.alcoholLevel;
        }
        if (updater.updateField((byte)23, this.additionalPain, bodyPart.additionalPain)) {
            bodyPart.additionalPain = this.additionalPain;
        }
        if (this.bandageType != bodyPart.bandageType) {
            updater.updateField((byte)24, this.bandageType);
            bodyPart.bandageType = this.bandageType;
        }
        if (this.getBandageXp != bodyPart.getBandageXp) {
            updater.updateField((byte)25, this.getBandageXp);
            bodyPart.getBandageXp = this.getBandageXp;
        }
        if (this.getStitchXp != bodyPart.getStitchXp) {
            updater.updateField((byte)26, this.getStitchXp);
            bodyPart.getStitchXp = this.getStitchXp;
        }
        if (this.getSplintXp != bodyPart.getSplintXp) {
            updater.updateField((byte)27, this.getSplintXp);
            bodyPart.getSplintXp = this.getSplintXp;
        }
        if (updater.updateField((byte)28, this.fractureTime, bodyPart.fractureTime)) {
            bodyPart.fractureTime = this.fractureTime;
        }
        if (this.splint != bodyPart.splint) {
            updater.updateField((byte)29, this.splint);
            bodyPart.splint = this.splint;
        }
        if (updater.updateField((byte)30, this.splintFactor, bodyPart.splintFactor)) {
            bodyPart.splintFactor = this.splintFactor;
        }
        if (this.haveBullet != bodyPart.haveBullet) {
            updater.updateField((byte)31, this.haveBullet);
            bodyPart.haveBullet = this.haveBullet;
        }
        if (updater.updateField((byte)32, this.burnTime, bodyPart.burnTime)) {
            bodyPart.burnTime = this.burnTime;
        }
        if (this.needBurnWash != bodyPart.needBurnWash) {
            updater.updateField((byte)33, this.needBurnWash);
            bodyPart.needBurnWash = this.needBurnWash;
        }
        if (updater.updateField((byte)34, this.lastTimeBurnWash, bodyPart.lastTimeBurnWash)) {
            bodyPart.lastTimeBurnWash = this.lastTimeBurnWash;
        }
        if (this.splintItem != bodyPart.splintItem) {
            updater.updateField((byte)35, this.splintItem);
            bodyPart.splintItem = this.splintItem;
        }
        if (updater.updateField((byte)36, this.plantainFactor, bodyPart.plantainFactor)) {
            bodyPart.plantainFactor = this.plantainFactor;
        }
        if (updater.updateField((byte)37, this.comfreyFactor, bodyPart.comfreyFactor)) {
            bodyPart.comfreyFactor = this.comfreyFactor;
        }
        if (updater.updateField((byte)38, this.garlicFactor, bodyPart.garlicFactor)) {
            bodyPart.garlicFactor = this.garlicFactor;
        }
    }
    
    public void sync(final ByteBuffer byteBuffer, final byte b) {
        switch (b) {
            case 23: {
                this.additionalPain = byteBuffer.getFloat();
                break;
            }
            case 15: {
                this.alcoholicBandage = (byteBuffer.get() == 1);
                break;
            }
            case 22: {
                this.alcoholLevel = byteBuffer.getFloat();
                break;
            }
            case 2: {
                this.bandaged = (byteBuffer.get() == 1);
                break;
            }
            case 12: {
                this.bandageLife = byteBuffer.getFloat();
                break;
            }
            case 24: {
                this.bandageType = GameWindow.ReadStringUTF(byteBuffer);
                break;
            }
            case 14: {
                this.biteTime = byteBuffer.getFloat();
                break;
            }
            case 3: {
                this.bitten = (byteBuffer.get() == 1);
                break;
            }
            case 4: {
                this.bleeding = (byteBuffer.get() == 1);
                break;
            }
            case 18: {
                this.bleedingTime = byteBuffer.getFloat();
                break;
            }
            case 32: {
                this.burnTime = byteBuffer.getFloat();
                break;
            }
            case 37: {
                this.comfreyFactor = byteBuffer.getFloat();
                break;
            }
            case 9: {
                this.deepWounded = (byteBuffer.get() == 1);
                break;
            }
            case 19: {
                this.deepWoundTime = byteBuffer.getFloat();
                break;
            }
            case 40: {
                this.cutTime = byteBuffer.getFloat();
                break;
            }
            case 28: {
                this.fractureTime = byteBuffer.getFloat();
                break;
            }
            case 38: {
                this.garlicFactor = byteBuffer.getFloat();
                break;
            }
            case 25: {
                this.getBandageXp = (byteBuffer.get() == 1);
                break;
            }
            case 27: {
                this.getSplintXp = (byteBuffer.get() == 1);
                break;
            }
            case 26: {
                this.getStitchXp = (byteBuffer.get() == 1);
                break;
            }
            case 31: {
                this.haveBullet = (byteBuffer.get() == 1);
                break;
            }
            case 20: {
                this.haveGlass = (byteBuffer.get() == 1);
                break;
            }
            case 1: {
                this.Health = byteBuffer.getFloat();
                break;
            }
            case 17: {
                this.infectedWound = (byteBuffer.get() == 1);
                break;
            }
            case 5: {
                this.IsBleedingStemmed = (byteBuffer.get() == 1);
                break;
            }
            case 6: {
                this.IsCortorised = (byteBuffer.get() == 1);
                break;
            }
            case 11: {
                this.IsFakeInfected = (byteBuffer.get() == 1);
                break;
            }
            case 10: {
                this.IsInfected = (byteBuffer.get() == 1);
                break;
            }
            case 34: {
                this.lastTimeBurnWash = byteBuffer.getFloat();
                break;
            }
            case 33: {
                this.needBurnWash = (byteBuffer.get() == 1);
                break;
            }
            case 36: {
                this.plantainFactor = byteBuffer.getFloat();
                break;
            }
            case 7: {
                this.scratched = (byteBuffer.get() == 1);
                break;
            }
            case 39: {
                this.cut = (byteBuffer.get() == 1);
                break;
            }
            case 13: {
                this.scratchTime = byteBuffer.getFloat();
                break;
            }
            case 29: {
                this.splint = (byteBuffer.get() == 1);
                break;
            }
            case 30: {
                this.splintFactor = byteBuffer.getFloat();
                break;
            }
            case 35: {
                this.splintItem = GameWindow.ReadStringUTF(byteBuffer);
                break;
            }
            case 8: {
                this.stitched = (byteBuffer.get() == 1);
                break;
            }
            case 21: {
                this.stitchTime = byteBuffer.getFloat();
                break;
            }
            case 16: {
                this.woundInfectionLevel = byteBuffer.getFloat();
                break;
            }
        }
    }
    
    public float getCutTime() {
        return this.cutTime;
    }
    
    public void setCutTime(float min) {
        min = Math.min(100.0f, min);
        this.cutTime = min;
    }
    
    public boolean isCut() {
        return this.cut;
    }
    
    public float getScratchSpeedModifier() {
        return this.scratchSpeedModifier;
    }
    
    public void setScratchSpeedModifier(final float scratchSpeedModifier) {
        this.scratchSpeedModifier = scratchSpeedModifier;
    }
    
    public float getCutSpeedModifier() {
        return this.cutSpeedModifier;
    }
    
    public void setCutSpeedModifier(final float cutSpeedModifier) {
        this.cutSpeedModifier = cutSpeedModifier;
    }
    
    public float getBurnSpeedModifier() {
        return this.burnSpeedModifier;
    }
    
    public void setBurnSpeedModifier(final float burnSpeedModifier) {
        this.burnSpeedModifier = burnSpeedModifier;
    }
    
    public float getDeepWoundSpeedModifier() {
        return this.deepWoundSpeedModifier;
    }
    
    public void setDeepWoundSpeedModifier(final float deepWoundSpeedModifier) {
        this.deepWoundSpeedModifier = deepWoundSpeedModifier;
    }
    
    public boolean isBurnt() {
        return this.getBurnTime() > 0.0f;
    }
    
    public void generateBleeding() {
        float next = 0.0f;
        if (this.scratched()) {
            next = Rand.Next(this.getScratchTime() * 0.3f, this.getScratchTime() * 0.6f);
        }
        if (this.isCut()) {
            next += Rand.Next(this.getCutTime() * 0.7f, this.getCutTime() * 1.0f);
        }
        if (this.isBurnt()) {
            next += Rand.Next(this.getBurnTime() * 0.3f, this.getBurnTime() * 0.6f);
        }
        if (this.isDeepWounded()) {
            next += Rand.Next(this.getDeepWoundTime() * 0.7f, this.getDeepWoundTime());
        }
        if (this.haveGlass()) {
            next += Rand.Next(5.0f, 10.0f);
        }
        if (this.haveBullet()) {
            next += Rand.Next(5.0f, 10.0f);
        }
        if (this.bitten()) {
            next += Rand.Next(7.5f, 15.0f);
        }
        switch (SandboxOptions.instance.InjurySeverity.getValue()) {
            case 1: {
                next *= 0.5f;
                break;
            }
            case 3: {
                next *= 1.5f;
                break;
            }
        }
        this.setBleedingTime(next * BodyPartType.getBleedingTimeModifyer(BodyPartType.ToIndex(this.getType())));
    }
    
    public float getInnerTemperature() {
        if (this.thermalNode != null) {
            return this.thermalNode.getCelcius();
        }
        return 0.0f;
    }
    
    public float getSkinTemperature() {
        if (this.thermalNode != null) {
            return this.thermalNode.getSkinCelcius();
        }
        return 0.0f;
    }
    
    public float getDistToCore() {
        if (this.thermalNode != null) {
            return this.thermalNode.getDistToCore();
        }
        return BodyPartType.GetDistToCore(this.Type);
    }
    
    public float getSkinSurface() {
        if (this.thermalNode != null) {
            return this.thermalNode.getSkinSurface();
        }
        return BodyPartType.GetSkinSurface(this.Type);
    }
    
    public Thermoregulator.ThermalNode getThermalNode() {
        return this.thermalNode;
    }
    
    public float getWetness() {
        return this.wetness;
    }
    
    public void setWetness(final float n) {
        this.wetness = PZMath.clamp(n, 0.0f, 100.0f);
    }
    
    public float getStiffness() {
        return this.stiffness;
    }
    
    public void setStiffness(final float n) {
        this.stiffness = PZMath.clamp(n, 0.0f, 100.0f);
    }
}
