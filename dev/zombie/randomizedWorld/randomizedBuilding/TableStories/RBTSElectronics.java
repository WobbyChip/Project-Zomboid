// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding.TableStories;

import zombie.core.Rand;
import zombie.iso.BuildingDef;

public final class RBTSElectronics extends RBTableStoryBase
{
    public RBTSElectronics() {
        this.chance = 5;
        this.rooms.add("livingroom");
        this.rooms.add("kitchen");
        this.rooms.add("bedroom");
    }
    
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        String s = "Base.ElectronicsMag1";
        switch (Rand.Next(0, 4)) {
            case 0: {
                s = "Base.ElectronicsMag2";
                break;
            }
            case 1: {
                s = "Base.ElectronicsMag3";
                break;
            }
            case 2: {
                s = "Base.ElectronicsMag5";
                break;
            }
        }
        this.addWorldItem(s, this.table1.getSquare(), 0.36f, 0.789f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        this.addWorldItem("Base.ElectronicsScrap", this.table1.getSquare(), 0.71f, 0.82f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        this.addWorldItem("Base.Screwdriver", this.table1.getSquare(), 0.36f, 0.421f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
        String s2 = "Radio.CDPlayer";
        switch (Rand.Next(0, 6)) {
            case 0: {
                s2 = "Base.Torch";
                break;
            }
            case 1: {
                s2 = "Base.Remote";
                break;
            }
            case 2: {
                s2 = "Base.VideoGame";
                break;
            }
            case 3: {
                s2 = "Base.CordlessPhone";
                break;
            }
            case 4: {
                s2 = "Base.Headphones";
                break;
            }
        }
        this.addWorldItem(s2, this.table1.getSquare(), 0.695f, 0.43f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
    }
}
