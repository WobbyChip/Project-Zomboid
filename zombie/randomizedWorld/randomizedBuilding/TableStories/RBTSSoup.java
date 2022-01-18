// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding.TableStories;

import zombie.core.Rand;
import zombie.iso.BuildingDef;

public final class RBTSSoup extends RBTableStoryBase
{
    public RBTSSoup() {
        this.need2Tables = true;
        this.chance = 10;
        this.ignoreAgainstWall = true;
        this.rooms.add("livingroom");
        this.rooms.add("kitchen");
    }
    
    @Override
    public void randomizeBuilding(final BuildingDef buildingDef) {
        if (this.table2 != null) {
            if (this.westTable) {
                this.addWorldItem(Rand.NextBool(2) ? "Base.SoupBowl" : "Base.Bowl", this.table1.getSquare(), 0.6875f, 0.8437f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
                if (Rand.NextBool(3)) {
                    this.addWorldItem("Base.Spoon", this.table1.getSquare(), 0.3359f, 0.9765f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(260, 275));
                }
                this.addWorldItem(Rand.NextBool(2) ? "Base.SoupBowl" : "Base.Bowl", this.table1.getSquare(), 0.6719f, 0.4531f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
                if (Rand.NextBool(3)) {
                    this.addWorldItem("Base.Spoon", this.table1.getSquare(), 0.375f, 0.2656f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(75, 95));
                }
                if (Rand.Next(100) < 70) {
                    this.addWorldItem(Rand.NextBool(2) ? "Base.SoupBowl" : "Base.Bowl", this.table2.getSquare(), 0.6484f, 0.8353f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
                    if (Rand.NextBool(3)) {
                        this.addWorldItem("Base.Spoon", this.table2.getSquare(), 0.8468f, 0.8906f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(260, 275));
                    }
                }
                if (Rand.Next(100) < 50) {
                    this.addWorldItem(Rand.NextBool(2) ? "Base.SoupBowl" : "Base.Bowl", this.table2.getSquare(), 0.5859f, 0.3941f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
                    if (Rand.NextBool(3)) {
                        this.addWorldItem("Base.Spoon", this.table2.getSquare(), 0.7965f, 0.2343f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(75, 95));
                    }
                }
                this.addWorldItem(Rand.NextBool(2) ? "Base.PotOfSoup" : "Base.PotOfStew", this.table2.getSquare(), 0.289f, 0.585f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
                this.addWorldItem("Base.Spoon", this.table2.getSquare(), 0.132f, 0.835f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 35));
                if (Rand.NextBool(3)) {
                    this.addWorldItem("farming.Salad", this.table1.getSquare(), 0.992f, 0.726f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
                }
            }
            else {
                this.addWorldItem(Rand.NextBool(2) ? "Base.SoupBowl" : "Base.Bowl", this.table1.getSquare(), 0.906f, 0.718f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
                if (Rand.NextBool(3)) {
                    this.addWorldItem("Base.Spoon", this.table1.getSquare(), 0.945f, 0.336f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(165, 185));
                }
                this.addWorldItem(Rand.NextBool(2) ? "Base.SoupBowl" : "Base.Bowl", this.table1.getSquare(), 0.406f, 0.562f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
                if (Rand.NextBool(3)) {
                    this.addWorldItem("Base.Spoon", this.table1.getSquare(), 0.265f, 0.299f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                }
                if (Rand.Next(100) < 70) {
                    this.addWorldItem(Rand.NextBool(2) ? "Base.SoupBowl" : "Base.Bowl", this.table2.getSquare(), 0.929f, 0.726f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
                    if (Rand.NextBool(3)) {
                        this.addWorldItem("Base.Spoon", this.table2.getSquare(), 0.976f, 0.46f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(165, 185));
                    }
                }
                if (Rand.Next(100) < 50) {
                    this.addWorldItem(Rand.NextBool(2) ? "Base.SoupBowl" : "Base.Bowl", this.table2.getSquare(), 0.382f, 0.78f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
                    if (Rand.NextBool(3)) {
                        this.addWorldItem("Base.Spoon", this.table2.getSquare(), 0.273f, 0.82f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                    }
                }
                this.addWorldItem(Rand.NextBool(2) ? "Base.PotOfSoup" : "Base.PotOfStew", this.table2.getSquare(), 0.679f, 0.289f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
                this.addWorldItem("Base.Spoon", this.table2.getSquare(), 0.937f, 0.187f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(85, 110));
                if (Rand.NextBool(3)) {
                    this.addWorldItem("farming.Salad", this.table1.getSquare(), 0.679f, 0.882f, this.table1.getSurfaceOffsetNoTable() / 96.0f);
                }
            }
        }
    }
}
