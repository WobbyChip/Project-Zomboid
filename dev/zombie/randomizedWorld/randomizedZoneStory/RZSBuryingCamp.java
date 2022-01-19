// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedZoneStory;

import zombie.iso.objects.IsoDeadBody;
import java.util.ArrayList;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoDirections;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoMetaGrid;

public class RZSBuryingCamp extends RandomizedZoneStoryBase
{
    public RZSBuryingCamp() {
        this.name = "Burying Camp";
        this.chance = 7;
        this.minZoneHeight = 6;
        this.minZoneWidth = 6;
        this.minimumDays = 20;
        this.zoneType.add(ZoneType.Forest.toString());
    }
    
    @Override
    public void randomizeZoneStory(final IsoMetaGrid.Zone zone) {
        this.cleanAreaForStory(this, zone);
        final boolean nextBool = Rand.NextBool(2);
        final int n = zone.x + 1;
        final int n2 = zone.y + 1;
        int n3 = 0;
        int n4 = 0;
        for (int next = Rand.Next(3, 7), i = 0; i < next; ++i) {
            if (nextBool) {
                this.addTileObject(n + i, zone.y + 2, zone.z, "location_community_cemetary_01_22");
                if (i == 2) {
                    this.addTileObject(n + i, zone.y + 3, zone.z, "location_community_cemetary_01_35");
                    this.addTileObject(n + i, zone.y + 4, zone.z, "location_community_cemetary_01_34");
                    n3 = n + i;
                    n4 = zone.y + 5;
                }
                else {
                    this.addTileObject(n + i, zone.y + 3, zone.z, "location_community_cemetary_01_43");
                    this.addTileObject(n + i, zone.y + 4, zone.z, "location_community_cemetary_01_42");
                    if (Rand.NextBool(2)) {
                        this.addTileObject(n + i, zone.y + 6, zone.z, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(16, 19)));
                    }
                }
            }
            else {
                this.addTileObject(zone.x + 2, n2 + i, zone.z, "location_community_cemetary_01_23");
                if (i == 2) {
                    this.addTileObject(zone.x + 3, n2 + i, zone.z, "location_community_cemetary_01_32");
                    this.addTileObject(zone.x + 4, n2 + i, zone.z, "location_community_cemetary_01_33");
                    n3 = zone.x + 5;
                    n4 = n2 + i;
                }
                else {
                    this.addTileObject(zone.x + 3, n2 + i, zone.z, "location_community_cemetary_01_40");
                    this.addTileObject(zone.x + 4, n2 + i, zone.z, "location_community_cemetary_01_41");
                    if (Rand.NextBool(2)) {
                        this.addTileObject(zone.x + 6, n2 + i, zone.z, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Rand.Next(16, 19)));
                    }
                }
            }
        }
        this.addItemOnGround(this.getSq(n3 + 1, n4 + 1, zone.z), "Base.Shovel");
        final ArrayList<IsoZombie> addZombiesOnSquare = this.addZombiesOnSquare(1, null, null, this.getRandomFreeSquare(this, zone));
        if (addZombiesOnSquare != null && !addZombiesOnSquare.isEmpty()) {
            final IsoZombie isoZombie = addZombiesOnSquare.get(0);
            final IsoDeadBody randomDeadBody = RandomizedWorldBase.createRandomDeadBody(this.getSq(n3, n4, zone.z), null, Rand.Next(7, 12), 0, null);
            if (randomDeadBody != null) {
                this.addBloodSplat(randomDeadBody.getSquare(), 10);
                isoZombie.faceLocationF(randomDeadBody.x, randomDeadBody.y);
                isoZombie.setX(randomDeadBody.x + 1.0f);
                isoZombie.setY(randomDeadBody.y);
                isoZombie.setEatBodyTarget(randomDeadBody, true);
            }
        }
        this.addItemOnGround(this.getRandomFreeSquare(this, zone), "Base.WhiskeyEmpty");
        this.addItemOnGround(this.getRandomFreeSquare(this, zone), "Base.WineEmpty");
    }
}
