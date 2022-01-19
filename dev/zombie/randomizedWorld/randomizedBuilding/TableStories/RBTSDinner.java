// 
// Decompiled by Procyon v0.5.36
// 

package zombie.randomizedWorld.randomizedBuilding.TableStories;

import zombie.core.Rand;
import zombie.iso.BuildingDef;

public final class RBTSDinner extends RBTableStoryBase
{
    private boolean hasPlate;
    private String plate;
    private String foodType;
    
    public RBTSDinner() {
        this.hasPlate = true;
        this.plate = null;
        this.foodType = "Base.Chicken";
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
                this.generateFood();
                if (this.hasPlate) {
                    this.addWorldItem(this.plate, this.table1.getSquare(), 0.6875f, 0.8437f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(80, 105));
                }
                this.addWorldItem(this.foodType, this.table1.getSquare(), 0.6875f, 0.8437f, this.table1.getSurfaceOffsetNoTable() / 96.0f + 0.01f, Rand.Next(80, 105));
                this.addWorldItem(Rand.NextBool(2) ? "Base.GlassWine" : "Base.GlassTumbler", this.table1.getSquare(), 0.406f, 0.656f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                this.addWorldItem(Rand.NextBool(3) ? "Base.KitchenKnife" : "Base.ButterKnife", this.table1.getSquare(), 0.3359f, 0.9765f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(260, 275));
                this.addWorldItem("Base.Fork", this.table1.getSquare(), 0.851f, 0.96265f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(260, 275));
                this.generateFood();
                if (this.hasPlate) {
                    this.addWorldItem(this.plate, this.table1.getSquare(), 0.5519f, 0.4531f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(240, 285));
                }
                this.addWorldItem(this.foodType, this.table1.getSquare(), 0.5519f, 0.4531f, this.table1.getSurfaceOffsetNoTable() / 96.0f + 0.01f, Rand.Next(240, 285));
                this.addWorldItem(Rand.NextBool(2) ? "Base.GlassWine" : "Base.GlassTumbler", this.table1.getSquare(), 0.523f, 0.57f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                this.addWorldItem(Rand.NextBool(3) ? "Base.KitchenKnife" : "Base.ButterKnife", this.table1.getSquare(), 0.305f, 0.2656f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(75, 95));
                this.addWorldItem("Base.Fork", this.table1.getSquare(), 0.704f, 0.265f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(75, 95));
                if (Rand.Next(100) < 70) {
                    this.generateFood();
                    if (this.hasPlate) {
                        this.addWorldItem(this.plate, this.table2.getSquare(), 0.6484f, 0.8353f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(80, 105));
                    }
                    this.addWorldItem(this.foodType, this.table2.getSquare(), 0.6484f, 0.8353f, this.table1.getSurfaceOffsetNoTable() / 96.0f + 0.01f, Rand.Next(80, 105));
                    this.addWorldItem(Rand.NextBool(2) ? "Base.GlassWine" : "Base.GlassTumbler", this.table2.getSquare(), 0.429f, 0.632f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                    this.addWorldItem("Base.Fork", this.table2.getSquare(), 0.234f, 0.92f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(260, 275));
                    this.addWorldItem(Rand.NextBool(3) ? "Base.KitchenKnife" : "Base.ButterKnife", this.table2.getSquare(), 0.851f, 0.96265f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(260, 275));
                }
                if (Rand.Next(100) < 50) {
                    this.generateFood();
                    if (this.hasPlate) {
                        this.addWorldItem(this.plate, this.table2.getSquare(), 0.5859f, 0.3941f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(240, 285));
                    }
                    this.addWorldItem(this.foodType, this.table2.getSquare(), 0.5859f, 0.3941f, this.table1.getSurfaceOffsetNoTable() / 96.0f + 0.01f, Rand.Next(240, 285));
                    this.addWorldItem(Rand.NextBool(2) ? "Base.GlassWine" : "Base.GlassTumbler", this.table2.getSquare(), 0.71f, 0.539f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                    this.addWorldItem("Base.Fork", this.table2.getSquare(), 0.195f, 0.21f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(75, 95));
                    this.addWorldItem(Rand.NextBool(3) ? "Base.KitchenKnife" : "Base.ButterKnife", this.table2.getSquare(), 0.851f, 0.3f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(75, 95));
                }
                if (Rand.NextBool(2)) {
                    this.addWorldItem(Rand.NextBool(2) ? "Base.Salt" : "Base.Pepper", this.table1.getSquare(), 0.984f, 0.734f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                }
                switch (Rand.Next(0, 5)) {
                    case 0: {
                        this.addWorldItem("Base.Mustard", this.table2.getSquare(), 0.46f, 0.664f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                        break;
                    }
                    case 1: {
                        this.addWorldItem("Base.Ketchup", this.table2.getSquare(), 0.46f, 0.664f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                        break;
                    }
                    case 2: {
                        this.addWorldItem("Base.Marinara", this.table2.getSquare(), 0.46f, 0.664f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                        break;
                    }
                }
                switch (Rand.Next(0, 4)) {
                    case 0: {
                        this.addWorldItem("Base.Wine", this.table2.getSquare(), 0.228f, 0.593f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                        break;
                    }
                    case 1: {
                        this.addWorldItem("Base.Wine2", this.table2.getSquare(), 0.228f, 0.593f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                        break;
                    }
                    case 2: {
                        this.addWorldItem("Base.WaterBottleFull", this.table2.getSquare(), 0.228f, 0.593f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                        break;
                    }
                    case 3: {
                        this.addWorldItem("Base.BeerBottle", this.table2.getSquare(), 0.228f, 0.593f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                        break;
                    }
                }
                if (Rand.NextBool(3)) {
                    switch (Rand.Next(0, 5)) {
                        case 0: {
                            this.addWorldItem("Base.WineEmpty", this.table1.getSquare(), 0.96f, 0.6f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                            break;
                        }
                        case 1: {
                            this.addWorldItem("Base.WineEmpty2", this.table1.getSquare(), 0.96f, 0.6f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                            break;
                        }
                        case 2: {
                            this.addWorldItem("Base.WaterBottleEmpty", this.table1.getSquare(), 0.96f, 0.6f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                            break;
                        }
                        case 3: {
                            this.addWorldItem("Base.BeerEmpty", this.table1.getSquare(), 0.96f, 0.6f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                            break;
                        }
                        case 4: {
                            this.addWorldItem("Base.BeerCan", this.table1.getSquare(), 0.96f, 0.6f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                            break;
                        }
                    }
                }
            }
            else {
                this.generateFood();
                if (this.hasPlate) {
                    this.addWorldItem(this.plate, this.table1.getSquare(), 0.343f, 0.562f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                }
                this.addWorldItem(this.foodType, this.table1.getSquare(), 0.343f, 0.562f, this.table1.getSurfaceOffsetNoTable() / 96.0f + 0.01f, Rand.Next(0, 15));
                this.addWorldItem(Rand.NextBool(2) ? "Base.GlassWine" : "Base.GlassTumbler", this.table1.getSquare(), 0.469f, 0.469f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                this.addWorldItem(Rand.NextBool(3) ? "Base.KitchenKnife" : "Base.ButterKnife", this.table1.getSquare(), 0.281f, 0.843f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                this.addWorldItem("Base.Fork", this.table1.getSquare(), 0.234f, 0.298f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                this.generateFood();
                if (this.hasPlate) {
                    this.addWorldItem(this.plate, this.table1.getSquare(), 0.867f, 0.695f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(165, 185));
                }
                this.addWorldItem(this.foodType, this.table1.getSquare(), 0.867f, 0.695f, this.table1.getSurfaceOffsetNoTable() / 96.0f + 0.01f, Rand.Next(165, 185));
                this.addWorldItem(Rand.NextBool(2) ? "Base.GlassWine" : "Base.GlassTumbler", this.table1.getSquare(), 0.648f, 0.383f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                this.addWorldItem(Rand.NextBool(3) ? "Base.KitchenKnife" : "Base.ButterKnife", this.table1.getSquare(), 0.945f, 0.304f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(165, 185));
                this.addWorldItem("Base.Fork", this.table1.getSquare(), 0.945f, 0.96f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(165, 185));
                if (Rand.Next(100) < 70) {
                    this.generateFood();
                    if (this.hasPlate) {
                        this.addWorldItem(this.plate, this.table2.getSquare(), 0.35f, 0.617f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                    }
                    this.addWorldItem(this.foodType, this.table2.getSquare(), 0.35f, 0.617f, this.table1.getSurfaceOffsetNoTable() / 96.0f + 0.01f, Rand.Next(0, 15));
                    this.addWorldItem(Rand.NextBool(2) ? "Base.GlassWine" : "Base.GlassTumbler", this.table2.getSquare(), 0.46f, 0.476f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                    this.addWorldItem(Rand.NextBool(3) ? "Base.KitchenKnife" : "Base.ButterKnife", this.table2.getSquare(), 0.265f, 0.828f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                    this.addWorldItem("Base.Fork", this.table2.getSquare(), 0.25f, 0.34f, this.table2.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                }
                if (Rand.Next(100) < 50) {
                    this.generateFood();
                    if (this.hasPlate) {
                        this.addWorldItem(this.plate, this.table2.getSquare(), 0.89f, 0.6f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(165, 185));
                    }
                    this.addWorldItem(this.foodType, this.table2.getSquare(), 0.89f, 0.6f, this.table1.getSurfaceOffsetNoTable() / 96.0f + 0.01f, Rand.Next(165, 185));
                    this.addWorldItem(Rand.NextBool(2) ? "Base.GlassWine" : "Base.GlassTumbler", this.table2.getSquare(), 0.648f, 0.638f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 15));
                    this.addWorldItem(Rand.NextBool(3) ? "Base.KitchenKnife" : "Base.ButterKnife", this.table2.getSquare(), 0.937f, 0.281f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(165, 185));
                    this.addWorldItem("Base.Fork", this.table2.getSquare(), 0.945f, 0.835f, this.table2.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(165, 185));
                }
                if (Rand.NextBool(2)) {
                    this.addWorldItem(Rand.NextBool(2) ? "Base.Salt" : "Base.Pepper", this.table1.getSquare(), 0.656f, 0.718f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                }
                switch (Rand.Next(0, 5)) {
                    case 0: {
                        this.addWorldItem("Base.Mustard", this.table2.getSquare(), 0.61f, 0.328f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                        break;
                    }
                    case 1: {
                        this.addWorldItem("Base.Ketchup", this.table2.getSquare(), 0.61f, 0.328f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                        break;
                    }
                    case 2: {
                        this.addWorldItem("Base.Marinara", this.table2.getSquare(), 0.61f, 0.328f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                        break;
                    }
                }
                switch (Rand.Next(0, 4)) {
                    case 0: {
                        this.addWorldItem("Base.Wine", this.table2.getSquare(), 0.468f, 0.09f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                        break;
                    }
                    case 1: {
                        this.addWorldItem("Base.Wine2", this.table2.getSquare(), 0.468f, 0.09f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                        break;
                    }
                    case 2: {
                        this.addWorldItem("Base.WaterBottleFull", this.table2.getSquare(), 0.468f, 0.09f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                        break;
                    }
                    case 3: {
                        this.addWorldItem("Base.BeerBottle", this.table2.getSquare(), 0.468f, 0.09f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                        break;
                    }
                }
                if (Rand.NextBool(3)) {
                    switch (Rand.Next(0, 5)) {
                        case 0: {
                            this.addWorldItem("Base.WineEmpty", this.table2.getSquare(), 0.851f, 0.15f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                            break;
                        }
                        case 1: {
                            this.addWorldItem("Base.WineEmpty2", this.table2.getSquare(), 0.851f, 0.15f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                            break;
                        }
                        case 2: {
                            this.addWorldItem("Base.WaterBottleEmpty", this.table2.getSquare(), 0.851f, 0.15f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                            break;
                        }
                        case 3: {
                            this.addWorldItem("Base.BeerEmpty", this.table2.getSquare(), 0.851f, 0.15f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                            break;
                        }
                        case 4: {
                            this.addWorldItem("Base.BeerCan", this.table2.getSquare(), 0.851f, 0.15f, this.table1.getSurfaceOffsetNoTable() / 96.0f, Rand.Next(0, 360));
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public void generateFood() {
        this.foodType = "Base.Chicken";
        if (Rand.NextBool(4)) {
            this.foodType = "Base.TVDinner";
            this.hasPlate = false;
        }
        else {
            this.hasPlate = true;
            if (this.plate == null) {
                switch (Rand.Next(0, 4)) {
                    case 0: {
                        this.plate = "Base.Plate";
                        break;
                    }
                    case 1: {
                        this.plate = "Base.PlateBlue";
                        break;
                    }
                    case 2: {
                        this.plate = "Base.PlateOrange";
                        break;
                    }
                    case 3: {
                        this.plate = "Base.PlateFancy";
                        break;
                    }
                }
            }
            switch (Rand.Next(0, 4)) {
                case 0: {
                    this.foodType = "Base.Chicken";
                    break;
                }
                case 1: {
                    this.foodType = "Base.PorkChop";
                    break;
                }
                case 2: {
                    this.foodType = "Base.Steak";
                    break;
                }
                case 3: {
                    this.foodType = "Base.Salmon";
                    break;
                }
            }
        }
    }
}
