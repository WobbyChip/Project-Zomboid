// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import zombie.vehicles.BaseVehicle;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoDeadBody;
import zombie.characters.IsoZombie;
import zombie.iso.Vector2;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;

public final class RVSCrashHorde extends RandomizedVehicleStoryBase
{
    public RVSCrashHorde() {
        this.name = "Crash Horde";
        this.minZoneWidth = 8;
        this.minZoneHeight = 8;
        this.setChance(1);
        this.setMinimumDays(60);
    }
    
    @Override
    public void randomizeVehicleStory(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk) {
        this.callVehicleStorySpawner(zone, isoChunk, 0.0f);
    }
    
    @Override
    public boolean initVehicleStorySpawner(final IsoMetaGrid.Zone zone, final IsoChunk isoChunk, final boolean b) {
        final VehicleStorySpawner instance = VehicleStorySpawner.getInstance();
        instance.clear();
        float n = 0.5235988f;
        if (b) {
            n = 0.0f;
        }
        final Vector2 toVector = IsoDirections.N.ToVector();
        toVector.rotate(Rand.Next(-n, n));
        instance.addElement("vehicle1", 0.0f, 0.0f, toVector.getDirection(), 2.0f, 5.0f);
        instance.setParameter("zone", zone);
        instance.setParameter("burnt", Rand.NextBool(5));
        return true;
    }
    
    @Override
    public void spawnElement(final VehicleStorySpawner vehicleStorySpawner, final VehicleStorySpawner.Element element) {
        if (element.square == null) {
            return;
        }
        final float z = element.z;
        final IsoMetaGrid.Zone zone = vehicleStorySpawner.getParameter("zone", IsoMetaGrid.Zone.class);
        final boolean parameterBoolean = vehicleStorySpawner.getParameterBoolean("burnt");
        final String id = element.id;
        switch (id) {
            case "vehicle1": {
                final BaseVehicle addVehicleFlipped = this.addVehicleFlipped(zone, element.position.x, element.position.y, z + 0.25f, element.direction, parameterBoolean ? "normalburnt" : "bad", null, null, null);
                if (addVehicleFlipped == null) {
                    break;
                }
                final int next = Rand.Next(4);
                String smashed = null;
                switch (next) {
                    case 0: {
                        smashed = "Front";
                        break;
                    }
                    case 1: {
                        smashed = "Rear";
                        break;
                    }
                    case 2: {
                        smashed = "Left";
                        break;
                    }
                    case 3: {
                        smashed = "Right";
                        break;
                    }
                }
                final BaseVehicle setSmashed = addVehicleFlipped.setSmashed(smashed);
                setSmashed.setBloodIntensity("Front", Rand.Next(0.7f, 1.0f));
                setSmashed.setBloodIntensity("Rear", Rand.Next(0.7f, 1.0f));
                setSmashed.setBloodIntensity("Left", Rand.Next(0.7f, 1.0f));
                setSmashed.setBloodIntensity("Right", Rand.Next(0.7f, 1.0f));
                final ArrayList<IsoZombie> addZombiesOnVehicle = this.addZombiesOnVehicle(Rand.Next(2, 4), null, null, setSmashed);
                if (addZombiesOnVehicle == null) {
                    break;
                }
                for (int i = 0; i < addZombiesOnVehicle.size(); ++i) {
                    final IsoZombie isoZombie = addZombiesOnVehicle.get(i);
                    isoZombie.upKillCount = false;
                    this.addBloodSplat(isoZombie.getSquare(), Rand.Next(10, 20));
                    if (parameterBoolean) {
                        isoZombie.setSkeleton(true);
                        isoZombie.getHumanVisual().setSkinTextureIndex(0);
                    }
                    else {
                        isoZombie.DoCorpseInventory();
                        if (Rand.NextBool(10)) {
                            isoZombie.setFakeDead(true);
                            isoZombie.bCrawling = true;
                            isoZombie.setCanWalk(false);
                            isoZombie.setCrawlerType(1);
                        }
                    }
                    new IsoDeadBody(isoZombie, false);
                }
                this.addZombiesOnVehicle(Rand.Next(12, 20), null, null, setSmashed);
                break;
            }
        }
    }
}
