// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.characterTextures.BloodBodyPartType;
import zombie.inventory.types.HandWeapon;
import zombie.vehicles.BaseVehicle;
import zombie.characters.BodyDamage.BodyDamage;

public interface ILuaGameCharacterDamage
{
    BodyDamage getBodyDamage();
    
    BodyDamage getBodyDamageRemote();
    
    float getHealth();
    
    void setHealth(final float p0);
    
    float Hit(final BaseVehicle p0, final float p1, final boolean p2, final float p3, final float p4);
    
    float Hit(final HandWeapon p0, final IsoGameCharacter p1, final float p2, final boolean p3, final float p4);
    
    float Hit(final HandWeapon p0, final IsoGameCharacter p1, final float p2, final boolean p3, final float p4, final boolean p5);
    
    boolean isOnFire();
    
    void StopBurning();
    
    void sendStopBurning();
    
    int getLastHitCount();
    
    void setLastHitCount(final int p0);
    
    void addHole(final BloodBodyPartType p0);
    
    void addBlood(final BloodBodyPartType p0, final boolean p1, final boolean p2, final boolean p3);
    
    boolean isBumped();
    
    String getBumpType();
    
    boolean isOnDeathDone();
    
    void setOnDeathDone(final boolean p0);
    
    boolean isOnKillDone();
    
    void setOnKillDone(final boolean p0);
    
    boolean isDeathDragDown();
    
    void setDeathDragDown(final boolean p0);
    
    boolean isPlayingDeathSound();
    
    void setPlayingDeathSound(final boolean p0);
}
