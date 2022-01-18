// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding.TableStories;

import zombie.core.Rand;
import zombie.iso.BuildingDef;

public final class RBTSFoodPreparation extends RBTableStoryBase
{
    public RBTSFoodPreparation() {
        this.chance = 8;
        this.ignoreAgainstWall = true;
        this.rooms.add("livingroom");
        this.rooms.add("kitchen");
    }
    
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        this.addWorldItem("Base.BakingTray", this.table1.getSquare(), 0.695f, 0.648f, this.table1.getSurfaceOffsetNoTable() / 96.0f, 1);
        String s = "Base.Chicken";
        switch (Rand.Next(0, 4)) {
            case 0: {
                s = "Base.Steak";
                break;
            }
            case 1: {
                s = "Base.MuttonChop";
                break;
            }
            case 2: {
                s = "Base.Smallbirdmeat";
                break;
            }
        }
        this.addWorldItem(s, this.table1.getSquare(), 0.531f, 0.625f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        this.addWorldItem(s, this.table1.getSquare(), 0.836f, 0.627f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        this.addWorldItem(Rand.NextBool(2) ? "Base.Pepper" : "Base.Salt", this.table1.getSquare(), 0.492f, 0.94f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        this.addWorldItem("Base.KitchenKnife", this.table1.getSquare(), 0.492f, 0.29f, this.table1.getSurfaceOffsetNoTable() / 96.0f, 1);
        String s2 = "farming.Tomato";
        switch (Rand.Next(0, 4)) {
            case 0: {
                s2 = "Base.BellPepper";
                break;
            }
            case 1: {
                s2 = "Base.Broccoli";
                break;
            }
            case 2: {
                s2 = "Base.Carrots";
                break;
            }
        }
        this.addWorldItem(s2, this.table1.getSquare(), 0.77f, 0.97f, this.table1.getSurfaceOffsetNoTable() / 96.0f, 70);
    }
}
