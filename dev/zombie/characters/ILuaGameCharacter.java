// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.core.skinnedmodel.advancedanimation.debug.AnimatorDebugMonitor;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;
import zombie.scripting.objects.Recipe;
import java.util.List;
import zombie.vehicles.BaseVehicle;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoWindow;
import zombie.vehicles.VehiclePart;
import zombie.ai.State;
import zombie.ui.UIFont;
import zombie.iso.IsoDirections;
import zombie.inventory.types.Literature;
import zombie.characters.skills.PerkFactory;
import zombie.characters.CharacterTimedActions.BaseAction;
import java.util.Stack;
import zombie.characters.traits.TraitCollection;
import zombie.characters.Moodles.Moodles;
import zombie.core.textures.ColorInfo;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.core.skinnedmodel.visual.BaseVisual;

public interface ILuaGameCharacter extends ILuaVariableSource, ILuaGameCharacterAttachedItems, ILuaGameCharacterDamage, ILuaGameCharacterClothing, ILuaGameCharacterHealth
{
    String getFullName();
    
    SurvivorDesc getDescriptor();
    
    void setDescriptor(final SurvivorDesc p0);
    
    boolean isRangedWeaponEmpty();
    
    void setRangedWeaponEmpty(final boolean p0);
    
    BaseVisual getVisual();
    
    BaseCharacterSoundEmitter getEmitter();
    
    void resetModel();
    
    void resetModelNextFrame();
    
    IsoSpriteInstance getSpriteDef();
    
    boolean hasItems(final String p0, final int p1);
    
    int getXpForLevel(final int p0);
    
    IsoGameCharacter.XP getXp();
    
    boolean isAsleep();
    
    void setAsleep(final boolean p0);
    
    int getZombieKills();
    
    void setForceWakeUpTime(final float p0);
    
    ItemContainer getInventory();
    
    InventoryItem getPrimaryHandItem();
    
    void setPrimaryHandItem(final InventoryItem p0);
    
    InventoryItem getSecondaryHandItem();
    
    void setSecondaryHandItem(final InventoryItem p0);
    
    boolean hasEquipped(final String p0);
    
    boolean hasEquippedTag(final String p0);
    
    boolean isHandItem(final InventoryItem p0);
    
    boolean isPrimaryHandItem(final InventoryItem p0);
    
    boolean isSecondaryHandItem(final InventoryItem p0);
    
    boolean isItemInBothHands(final InventoryItem p0);
    
    boolean removeFromHands(final InventoryItem p0);
    
    void setSpeakColourInfo(final ColorInfo p0);
    
    boolean isSpeaking();
    
    Moodles getMoodles();
    
    Stats getStats();
    
    TraitCollection getTraits();
    
    int getMaxWeight();
    
    void PlayAnim(final String p0);
    
    void PlayAnimWithSpeed(final String p0, final float p1);
    
    void PlayAnimUnlooped(final String p0);
    
    void StartTimedActionAnim(final String p0);
    
    void StartTimedActionAnim(final String p0, final String p1);
    
    void StopTimedActionAnim();
    
    Stack<BaseAction> getCharacterActions();
    
    void StartAction(final BaseAction p0);
    
    void StopAllActionQueue();
    
    int getPerkLevel(final PerkFactory.Perk p0);
    
    IsoGameCharacter.PerkInfo getPerkInfo(final PerkFactory.Perk p0);
    
    void setPerkLevelDebug(final PerkFactory.Perk p0, final int p1);
    
    void LoseLevel(final PerkFactory.Perk p0);
    
    void LevelPerk(final PerkFactory.Perk p0, final boolean p1);
    
    void LevelPerk(final PerkFactory.Perk p0);
    
    void ReadLiterature(final Literature p0);
    
    void setDir(final IsoDirections p0);
    
    void Callout();
    
    boolean IsSpeaking();
    
    void Say(final String p0);
    
    void Say(final String p0, final float p1, final float p2, final float p3, final UIFont p4, final float p5, final String p6);
    
    void setHaloNote(final String p0);
    
    void setHaloNote(final String p0, final float p1);
    
    void setHaloNote(final String p0, final int p1, final int p2, final int p3, final float p4);
    
    void initSpritePartsEmpty();
    
    boolean HasTrait(final String p0);
    
    void changeState(final State p0);
    
    boolean isCurrentState(final State p0);
    
    State getCurrentState();
    
    void pathToLocation(final int p0, final int p1, final int p2);
    
    void pathToLocationF(final float p0, final float p1, final float p2);
    
    boolean CanAttack();
    
    void smashCarWindow(final VehiclePart p0);
    
    void smashWindow(final IsoWindow p0);
    
    void openWindow(final IsoWindow p0);
    
    void closeWindow(final IsoWindow p0);
    
    void climbThroughWindow(final IsoWindow p0);
    
    void climbThroughWindow(final IsoWindow p0, final Integer p1);
    
    void climbThroughWindowFrame(final IsoObject p0);
    
    void climbSheetRope();
    
    void climbDownSheetRope();
    
    boolean canClimbSheetRope(final IsoGridSquare p0);
    
    boolean canClimbDownSheetRopeInCurrentSquare();
    
    boolean canClimbDownSheetRope(final IsoGridSquare p0);
    
    void climbThroughWindow(final IsoThumpable p0);
    
    void climbThroughWindow(final IsoThumpable p0, final Integer p1);
    
    void climbOverFence(final IsoDirections p0);
    
    boolean isAboveTopOfStairs();
    
    double getHoursSurvived();
    
    boolean isOutside();
    
    boolean isFemale();
    
    void setFemale(final boolean p0);
    
    boolean isZombie();
    
    boolean isEquipped(final InventoryItem p0);
    
    boolean isEquippedClothing(final InventoryItem p0);
    
    boolean isAttachedItem(final InventoryItem p0);
    
    void faceThisObject(final IsoObject p0);
    
    void facePosition(final int p0, final int p1);
    
    void faceThisObjectAlt(final IsoObject p0);
    
    int getAlreadyReadPages(final String p0);
    
    void setAlreadyReadPages(final String p0, final int p1);
    
    boolean isSafety();
    
    void setSafety(final boolean p0);
    
    float getSafetyCooldown();
    
    void setSafetyCooldown(final float p0);
    
    float getMeleeDelay();
    
    void setMeleeDelay(final float p0);
    
    float getRecoilDelay();
    
    void setRecoilDelay(final float p0);
    
    int getMaintenanceMod();
    
    float getHammerSoundMod();
    
    float getWeldingSoundMod();
    
    boolean isGodMod();
    
    void setGodMod(final boolean p0);
    
    BaseVehicle getVehicle();
    
    void setVehicle(final BaseVehicle p0);
    
    float getInventoryWeight();
    
    List<String> getKnownRecipes();
    
    boolean isRecipeKnown(final Recipe p0);
    
    boolean isRecipeKnown(final String p0);
    
    long playSound(final String p0);
    
    void stopOrTriggerSound(final long p0);
    
    void addWorldSoundUnlessInvisible(final int p0, final int p1, final boolean p2);
    
    boolean isKnownPoison(final InventoryItem p0);
    
    String getBedType();
    
    void setBedType(final String p0);
    
    PolygonalMap2.Path getPath2();
    
    void setPath2(final PolygonalMap2.Path p0);
    
    PathFindBehavior2 getPathFindBehavior2();
    
    IsoObject getBed();
    
    void setBed(final IsoObject p0);
    
    boolean isReading();
    
    void setReading(final boolean p0);
    
    float getTimeSinceLastSmoke();
    
    void setTimeSinceLastSmoke(final float p0);
    
    boolean isInvisible();
    
    void setInvisible(final boolean p0);
    
    boolean isDriving();
    
    boolean isInARoom();
    
    boolean isUnlimitedCarry();
    
    void setUnlimitedCarry(final boolean p0);
    
    boolean isBuildCheat();
    
    void setBuildCheat(final boolean p0);
    
    boolean isFarmingCheat();
    
    void setFarmingCheat(final boolean p0);
    
    boolean isHealthCheat();
    
    void setHealthCheat(final boolean p0);
    
    boolean isMechanicsCheat();
    
    void setMechanicsCheat(final boolean p0);
    
    boolean isMovablesCheat();
    
    void setMovablesCheat(final boolean p0);
    
    boolean isTimedActionInstantCheat();
    
    void setTimedActionInstantCheat(final boolean p0);
    
    boolean isTimedActionInstant();
    
    boolean isShowAdminTag();
    
    void setShowAdminTag(final boolean p0);
    
    void reportEvent(final String p0);
    
    AnimatorDebugMonitor getDebugMonitor();
    
    void setDebugMonitor(final AnimatorDebugMonitor p0);
    
    boolean isAiming();
    
    void resetBeardGrowingTime();
    
    void resetHairGrowingTime();
}
