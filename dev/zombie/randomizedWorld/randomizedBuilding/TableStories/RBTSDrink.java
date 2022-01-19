// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding.TableStories;

import zombie.core.Rand;
import zombie.iso.BuildingDef;

public final class RBTSDrink extends RBTableStoryBase
{
    public RBTSDrink() {
        this.chance = 7;
        this.rooms.add("livingroom");
        this.rooms.add("kitchen");
    }
    
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        this.addWorldItem(this.getDrink(), this.table1.getSquare(), 0.539f, 0.742f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        if (Rand.Next(70) < 100) {
            this.addWorldItem(this.getDrink(), this.table1.getSquare(), 0.734f, 0.797f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        }
        if (Rand.Next(70) < 100) {
            this.addWorldItem(this.getDrink(), this.table1.getSquare(), 0.554f, 0.57f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        }
        if (Rand.Next(70) < 100) {
            this.addWorldItem(this.getDrink(), this.table1.getSquare(), 0.695f, 0.336f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        }
        if (Rand.Next(70) < 100) {
            this.addWorldItem(this.getDrink(), this.table1.getSquare(), 0.875f, 0.687f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        }
        if (Rand.Next(70) < 100) {
            this.addWorldItem(this.getDrink(), this.table1.getSquare(), 0.476f, 0.273f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        }
        this.addWorldItem("Base.PlasticCup", this.table1.getSquare(), 0.843f, 0.531f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        String s = "Base.Crisps";
        switch (Rand.Next(0, 4)) {
            case 0: {
                s = "Base.Crisps2";
                break;
            }
            case 1: {
                s = "Base.Crisps3";
                break;
            }
            case 2: {
                s = "Base.Crisps4";
                break;
            }
        }
        this.addWorldItem(s, this.table1.getSquare(), 0.87f, 0.86f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        if (Rand.Next(70) < 100) {
            this.addWorldItem("Base.Cigarettes", this.table1.getSquare(), 0.406f, 0.843f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        }
        if (Rand.Next(70) < 100) {
            this.addWorldItem("Base.Cigarettes", this.table1.getSquare(), 0.578f, 0.953f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        }
    }
    
    public String getDrink() {
        if (Rand.NextBool(5)) {
            return "Base.PlasticCup";
        }
        switch (Rand.Next(0, 4)) {
            case 0: {
                return "Base.BeerBottle";
            }
            case 1: {
                return "Base.BeerEmpty";
            }
            case 2: {
                return "Base.BeerCan";
            }
            case 3: {
                return "Base.BeerCanEmpty";
            }
            default: {
                return "Base.BeerBottle";
            }
        }
    }
}
