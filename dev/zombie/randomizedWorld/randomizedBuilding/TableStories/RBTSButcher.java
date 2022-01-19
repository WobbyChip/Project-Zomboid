// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding.TableStories;

import zombie.core.Rand;
import zombie.iso.BuildingDef;

public final class RBTSButcher extends RBTableStoryBase
{
    public RBTSButcher() {
        this.chance = 3;
        this.ignoreAgainstWall = true;
        this.rooms.add("livingroom");
        this.rooms.add("kitchen");
    }
    
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        String s = "Base.DeadRabbit";
        String s2 = "Base.Rabbitmeat";
        switch (Rand.Next(0, 4)) {
            case 0: {
                s = "Base.DeadBird";
                s2 = "Base.Smallbirdmeat";
                break;
            }
            case 1: {
                s = "Base.DeadSquirrel";
                s2 = "Base.Smallanimalmeat";
                break;
            }
            case 2: {
                s = "Base.Panfish";
                s2 = "Base.FishFillet";
                break;
            }
            case 3: {
                s = "Base.BaitFish";
                s2 = "Base.FishFillet";
                break;
            }
            case 4: {
                s = "Base.Catfish";
                s2 = "Base.FishFillet";
                break;
            }
        }
        this.addWorldItem(s, this.table1.getSquare(), 0.453f, 0.64f, this.table1.getSurfaceOffsetNoTable() / 96.0f, 1);
        this.addWorldItem(s2, this.table1.getSquare(), 0.835f, 0.851f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        this.addWorldItem("Base.KitchenKnife", this.table1.getSquare(), 0.742f, 0.445f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
    }
}
