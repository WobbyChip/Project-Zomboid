// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.iso.objects.IsoDeadBody;
import zombie.characters.IsoGameCharacter;
import zombie.iso.RoomDef;
import zombie.inventory.InventoryItem;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.iso.BuildingDef;

public final class RDSSuicidePact extends RandomizedDeadSurvivorBase
{
    public RDSSuicidePact() {
        this.name = "Suicide Pact";
        this.setChance(7);
        this.setMinimumDays(60);
    }
    
    @Override
    public void randomizeDeadSurvivor(final BuildingDef buildingDef) {
        final RoomDef livingRoomOrKitchen = this.getLivingRoomOrKitchen(buildingDef);
        final IsoGameCharacter randomZombieForCorpse = RandomizedWorldBase.createRandomZombieForCorpse(livingRoomOrKitchen);
        if (randomZombieForCorpse == null) {
            return;
        }
        randomZombieForCorpse.addVisualDamage("ZedDmg_HEAD_Bullet");
        final IsoDeadBody bodyFromZombie = RandomizedWorldBase.createBodyFromZombie(randomZombieForCorpse);
        if (bodyFromZombie == null) {
            return;
        }
        this.addBloodSplat(bodyFromZombie.getSquare(), 4);
        bodyFromZombie.setPrimaryHandItem(this.addWeapon("Base.Pistol", true));
        final IsoGameCharacter randomZombieForCorpse2 = RandomizedWorldBase.createRandomZombieForCorpse(livingRoomOrKitchen);
        if (randomZombieForCorpse2 == null) {
            return;
        }
        randomZombieForCorpse2.addVisualDamage("ZedDmg_HEAD_Bullet");
        final IsoDeadBody bodyFromZombie2 = RandomizedWorldBase.createBodyFromZombie(randomZombieForCorpse2);
        if (bodyFromZombie2 == null) {
            return;
        }
        this.addBloodSplat(bodyFromZombie2.getSquare(), 4);
    }
}
