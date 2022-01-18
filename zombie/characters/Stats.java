// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.core.math.PZMath;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.DataInputStream;

public final class Stats
{
    public float Anger;
    public float boredom;
    public float endurance;
    public boolean enduranceRecharging;
    public float endurancelast;
    public float endurancedanger;
    public float endurancewarn;
    public float fatigue;
    public float fitness;
    public float hunger;
    public float idleboredom;
    public float morale;
    public float stress;
    public float Fear;
    public float Panic;
    public float Sanity;
    public float Sickness;
    public float Boredom;
    public float Pain;
    public float Drunkenness;
    public int NumVisibleZombies;
    public int LastNumVisibleZombies;
    public boolean Tripping;
    public float TrippingRotAngle;
    public float thirst;
    public int NumChasingZombies;
    public int LastVeryCloseZombies;
    public static int NumCloseZombies;
    public int LastNumChasingZombies;
    public float stressFromCigarettes;
    public float ChasingZombiesDanger;
    public int MusicZombiesVisible;
    public int MusicZombiesTargeting;
    
    public Stats() {
        this.Anger = 0.0f;
        this.boredom = 0.0f;
        this.endurance = 1.0f;
        this.enduranceRecharging = false;
        this.endurancelast = 1.0f;
        this.endurancedanger = 0.25f;
        this.endurancewarn = 0.5f;
        this.fatigue = 0.0f;
        this.fitness = 1.0f;
        this.hunger = 0.0f;
        this.idleboredom = 0.0f;
        this.morale = 0.5f;
        this.stress = 0.0f;
        this.Fear = 0.0f;
        this.Panic = 0.0f;
        this.Sanity = 1.0f;
        this.Sickness = 0.0f;
        this.Boredom = 0.0f;
        this.Pain = 0.0f;
        this.Drunkenness = 0.0f;
        this.NumVisibleZombies = 0;
        this.LastNumVisibleZombies = 0;
        this.Tripping = false;
        this.TrippingRotAngle = 0.0f;
        this.thirst = 0.0f;
        this.NumChasingZombies = 0;
        this.LastVeryCloseZombies = 0;
        this.LastNumChasingZombies = 0;
        this.stressFromCigarettes = 0.0f;
        this.MusicZombiesVisible = 0;
        this.MusicZombiesTargeting = 0;
    }
    
    public int getNumVisibleZombies() {
        return this.NumVisibleZombies;
    }
    
    public int getNumChasingZombies() {
        return this.LastNumChasingZombies;
    }
    
    public void load(final DataInputStream dataInputStream) throws IOException {
        this.Anger = dataInputStream.readFloat();
        this.boredom = dataInputStream.readFloat();
        this.endurance = dataInputStream.readFloat();
        this.fatigue = dataInputStream.readFloat();
        this.fitness = dataInputStream.readFloat();
        this.hunger = dataInputStream.readFloat();
        this.morale = dataInputStream.readFloat();
        this.stress = dataInputStream.readFloat();
        this.Fear = dataInputStream.readFloat();
        this.Panic = dataInputStream.readFloat();
        this.Sanity = dataInputStream.readFloat();
        this.Sickness = dataInputStream.readFloat();
        this.Boredom = dataInputStream.readFloat();
        this.Pain = dataInputStream.readFloat();
        this.Drunkenness = dataInputStream.readFloat();
        this.thirst = dataInputStream.readFloat();
    }
    
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        this.Anger = byteBuffer.getFloat();
        this.boredom = byteBuffer.getFloat();
        this.endurance = byteBuffer.getFloat();
        this.fatigue = byteBuffer.getFloat();
        this.fitness = byteBuffer.getFloat();
        this.hunger = byteBuffer.getFloat();
        this.morale = byteBuffer.getFloat();
        this.stress = byteBuffer.getFloat();
        this.Fear = byteBuffer.getFloat();
        this.Panic = byteBuffer.getFloat();
        this.Sanity = byteBuffer.getFloat();
        this.Sickness = byteBuffer.getFloat();
        this.Boredom = byteBuffer.getFloat();
        this.Pain = byteBuffer.getFloat();
        this.Drunkenness = byteBuffer.getFloat();
        this.thirst = byteBuffer.getFloat();
        if (n >= 97) {
            this.stressFromCigarettes = byteBuffer.getFloat();
        }
    }
    
    public void save(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeFloat(this.Anger);
        dataOutputStream.writeFloat(this.boredom);
        dataOutputStream.writeFloat(this.endurance);
        dataOutputStream.writeFloat(this.fatigue);
        dataOutputStream.writeFloat(this.fitness);
        dataOutputStream.writeFloat(this.hunger);
        dataOutputStream.writeFloat(this.morale);
        dataOutputStream.writeFloat(this.stress);
        dataOutputStream.writeFloat(this.Fear);
        dataOutputStream.writeFloat(this.Panic);
        dataOutputStream.writeFloat(this.Sanity);
        dataOutputStream.writeFloat(this.Sickness);
        dataOutputStream.writeFloat(this.Boredom);
        dataOutputStream.writeFloat(this.Pain);
        dataOutputStream.writeFloat(this.Drunkenness);
        dataOutputStream.writeFloat(this.thirst);
    }
    
    public void save(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.putFloat(this.Anger);
        byteBuffer.putFloat(this.boredom);
        byteBuffer.putFloat(this.endurance);
        byteBuffer.putFloat(this.fatigue);
        byteBuffer.putFloat(this.fitness);
        byteBuffer.putFloat(this.hunger);
        byteBuffer.putFloat(this.morale);
        byteBuffer.putFloat(this.stress);
        byteBuffer.putFloat(this.Fear);
        byteBuffer.putFloat(this.Panic);
        byteBuffer.putFloat(this.Sanity);
        byteBuffer.putFloat(this.Sickness);
        byteBuffer.putFloat(this.Boredom);
        byteBuffer.putFloat(this.Pain);
        byteBuffer.putFloat(this.Drunkenness);
        byteBuffer.putFloat(this.thirst);
        byteBuffer.putFloat(this.stressFromCigarettes);
    }
    
    public float getAnger() {
        return this.Anger;
    }
    
    public void setAnger(final float anger) {
        this.Anger = anger;
    }
    
    public float getBoredom() {
        return this.boredom;
    }
    
    public void setBoredom(final float boredom) {
        this.boredom = boredom;
    }
    
    public float getEndurance() {
        return this.endurance;
    }
    
    public void setEndurance(final float endurance) {
        this.endurance = endurance;
    }
    
    public float getEndurancelast() {
        return this.endurancelast;
    }
    
    public void setEndurancelast(final float endurancelast) {
        this.endurancelast = endurancelast;
    }
    
    public float getEndurancedanger() {
        return this.endurancedanger;
    }
    
    public void setEndurancedanger(final float endurancedanger) {
        this.endurancedanger = endurancedanger;
    }
    
    public float getEndurancewarn() {
        return this.endurancewarn;
    }
    
    public void setEndurancewarn(final float endurancewarn) {
        this.endurancewarn = endurancewarn;
    }
    
    public boolean getEnduranceRecharging() {
        return this.enduranceRecharging;
    }
    
    public float getFatigue() {
        return this.fatigue;
    }
    
    public void setFatigue(final float fatigue) {
        this.fatigue = fatigue;
    }
    
    public float getFitness() {
        return this.fitness;
    }
    
    public void setFitness(final float fitness) {
        this.fitness = fitness;
    }
    
    public float getHunger() {
        return this.hunger;
    }
    
    public void setHunger(final float hunger) {
        this.hunger = hunger;
    }
    
    public float getIdleboredom() {
        return this.idleboredom;
    }
    
    public void setIdleboredom(final float idleboredom) {
        this.idleboredom = idleboredom;
    }
    
    public float getMorale() {
        return this.morale;
    }
    
    public void setMorale(final float morale) {
        this.morale = morale;
    }
    
    public float getStress() {
        return this.stress + this.getStressFromCigarettes();
    }
    
    public void setStress(final float stress) {
        this.stress = stress;
    }
    
    public float getStressFromCigarettes() {
        return this.stressFromCigarettes;
    }
    
    public void setStressFromCigarettes(final float n) {
        this.stressFromCigarettes = PZMath.clamp(n, 0.0f, this.getMaxStressFromCigarettes());
    }
    
    public float getMaxStressFromCigarettes() {
        return 0.51f;
    }
    
    public float getFear() {
        return this.Fear;
    }
    
    public void setFear(final float fear) {
        this.Fear = fear;
    }
    
    public float getPanic() {
        return this.Panic;
    }
    
    public void setPanic(final float panic) {
        this.Panic = panic;
    }
    
    public float getSanity() {
        return this.Sanity;
    }
    
    public void setSanity(final float sanity) {
        this.Sanity = sanity;
    }
    
    public float getSickness() {
        return this.Sickness;
    }
    
    public void setSickness(final float sickness) {
        this.Sickness = sickness;
    }
    
    public float getPain() {
        return this.Pain;
    }
    
    public void setPain(final float pain) {
        this.Pain = pain;
    }
    
    public float getDrunkenness() {
        return this.Drunkenness;
    }
    
    public void setDrunkenness(final float drunkenness) {
        this.Drunkenness = drunkenness;
    }
    
    public int getVisibleZombies() {
        return this.NumVisibleZombies;
    }
    
    public void setNumVisibleZombies(final int numVisibleZombies) {
        this.NumVisibleZombies = numVisibleZombies;
    }
    
    public boolean isTripping() {
        return this.Tripping;
    }
    
    public void setTripping(final boolean tripping) {
        this.Tripping = tripping;
    }
    
    public float getTrippingRotAngle() {
        return this.TrippingRotAngle;
    }
    
    public void setTrippingRotAngle(final float trippingRotAngle) {
        this.TrippingRotAngle = trippingRotAngle;
    }
    
    public float getThirst() {
        return this.thirst;
    }
    
    public void setThirst(final float thirst) {
        this.thirst = thirst;
    }
    
    public void resetStats() {
        this.Anger = 0.0f;
        this.boredom = 0.0f;
        this.fatigue = 0.0f;
        this.hunger = 0.0f;
        this.idleboredom = 0.0f;
        this.morale = 0.5f;
        this.stress = 0.0f;
        this.Fear = 0.0f;
        this.Panic = 0.0f;
        this.Sanity = 1.0f;
        this.Sickness = 0.0f;
        this.Boredom = 0.0f;
        this.Pain = 0.0f;
        this.Drunkenness = 0.0f;
        this.thirst = 0.0f;
    }
    
    static {
        Stats.NumCloseZombies = 0;
    }
}
