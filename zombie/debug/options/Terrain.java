// 
// Decompiled by Procyon v0.5.36
// 

package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public final class Terrain extends OptionGroup
{
    public final RenderTiles RenderTiles;
    
    public Terrain() {
        super("Terrain");
        this.RenderTiles = new RenderTiles(this.Group);
    }
    
    public final class RenderTiles extends OptionGroup
    {
        public final BooleanDebugOption Enable;
        public final BooleanDebugOption NewRender;
        public final BooleanDebugOption Shadows;
        public final BooleanDebugOption BloodDecals;
        public final BooleanDebugOption Water;
        public final BooleanDebugOption WaterShore;
        public final BooleanDebugOption WaterBody;
        public final BooleanDebugOption Lua;
        public final BooleanDebugOption VegetationCorpses;
        public final BooleanDebugOption MinusFloorCharacters;
        public final BooleanDebugOption RenderGridSquares;
        public final BooleanDebugOption RenderSprites;
        public final BooleanDebugOption OverlaySprites;
        public final BooleanDebugOption AttachedAnimSprites;
        public final BooleanDebugOption AttachedChildren;
        public final BooleanDebugOption AttachedWallBloodSplats;
        public final BooleanDebugOption UseShaders;
        public final BooleanDebugOption HighContrastBg;
        public final IsoGridSquare IsoGridSquare;
        
        public RenderTiles(final IDebugOptionGroup debugOptionGroup) {
            super(debugOptionGroup, "RenderTiles");
            this.Enable = OptionGroup.newDebugOnlyOption(this.Group, "Enable", true);
            this.NewRender = OptionGroup.newDebugOnlyOption(this.Group, "NewRender", true);
            this.Shadows = OptionGroup.newDebugOnlyOption(this.Group, "Shadows", true);
            this.BloodDecals = OptionGroup.newDebugOnlyOption(this.Group, "BloodDecals", true);
            this.Water = OptionGroup.newDebugOnlyOption(this.Group, "Water", true);
            this.WaterShore = OptionGroup.newDebugOnlyOption(this.Group, "WaterShore", true);
            this.WaterBody = OptionGroup.newDebugOnlyOption(this.Group, "WaterBody", true);
            this.Lua = OptionGroup.newDebugOnlyOption(this.Group, "Lua", true);
            this.VegetationCorpses = OptionGroup.newDebugOnlyOption(this.Group, "VegetationCorpses", true);
            this.MinusFloorCharacters = OptionGroup.newDebugOnlyOption(this.Group, "MinusFloorCharacters", true);
            this.RenderGridSquares = OptionGroup.newDebugOnlyOption(this.Group, "RenderGridSquares", true);
            this.RenderSprites = OptionGroup.newDebugOnlyOption(this.Group, "RenderSprites", true);
            this.OverlaySprites = OptionGroup.newDebugOnlyOption(this.Group, "OverlaySprites", true);
            this.AttachedAnimSprites = OptionGroup.newDebugOnlyOption(this.Group, "AttachedAnimSprites", true);
            this.AttachedChildren = OptionGroup.newDebugOnlyOption(this.Group, "AttachedChildren", true);
            this.AttachedWallBloodSplats = OptionGroup.newDebugOnlyOption(this.Group, "AttachedWallBloodSplats", true);
            this.UseShaders = OptionGroup.newOption(this.Group, "UseShaders", true);
            this.HighContrastBg = OptionGroup.newDebugOnlyOption(this.Group, "HighContrastBg", false);
            this.IsoGridSquare = new IsoGridSquare(this.Group);
        }
        
        public final class IsoGridSquare extends OptionGroup
        {
            public final BooleanDebugOption RenderMinusFloor;
            public final BooleanDebugOption DoorsAndWalls;
            public final BooleanDebugOption DoorsAndWalls_SimpleLighting;
            public final BooleanDebugOption Objects;
            public final BooleanDebugOption MeshCutdown;
            public final BooleanDebugOption IsoPadding;
            public final BooleanDebugOption IsoPaddingDeDiamond;
            public final BooleanDebugOption IsoPaddingAttached;
            public final BooleanDebugOption ShoreFade;
            public final Walls Walls;
            public final Floor Floor;
            
            public IsoGridSquare(final IDebugOptionGroup debugOptionGroup) {
                super(debugOptionGroup, "IsoGridSquare");
                this.RenderMinusFloor = OptionGroup.newDebugOnlyOption(this.Group, "RenderMinusFloor", true);
                this.DoorsAndWalls = OptionGroup.newDebugOnlyOption(this.Group, "DoorsAndWalls", true);
                this.DoorsAndWalls_SimpleLighting = OptionGroup.newDebugOnlyOption(this.Group, "DoorsAndWallsSL", true);
                this.Objects = OptionGroup.newDebugOnlyOption(this.Group, "Objects", true);
                this.MeshCutdown = OptionGroup.newDebugOnlyOption(this.Group, "MeshCutDown", true);
                this.IsoPadding = OptionGroup.newDebugOnlyOption(this.Group, "IsoPadding", true);
                this.IsoPaddingDeDiamond = OptionGroup.newDebugOnlyOption(this.Group, "IsoPaddingDeDiamond", true);
                this.IsoPaddingAttached = OptionGroup.newDebugOnlyOption(this.Group, "IsoPaddingAttached", true);
                this.ShoreFade = OptionGroup.newDebugOnlyOption(this.Group, "ShoreFade", true);
                this.Walls = new Walls(this.Group);
                this.Floor = new Floor(this.Group);
            }
            
            public final class Walls extends OptionGroup
            {
                public final BooleanDebugOption NW;
                public final BooleanDebugOption W;
                public final BooleanDebugOption N;
                public final BooleanDebugOption Render;
                public final BooleanDebugOption Lighting;
                public final BooleanDebugOption LightingDebug;
                public final BooleanDebugOption LightingOldDebug;
                public final BooleanDebugOption AttachedSprites;
                
                public Walls(final IDebugOptionGroup debugOptionGroup) {
                    super(debugOptionGroup, "Walls");
                    this.NW = OptionGroup.newDebugOnlyOption(this.Group, "NW", true);
                    this.W = OptionGroup.newDebugOnlyOption(this.Group, "W", true);
                    this.N = OptionGroup.newDebugOnlyOption(this.Group, "N", true);
                    this.Render = OptionGroup.newDebugOnlyOption(this.Group, "Render", true);
                    this.Lighting = OptionGroup.newDebugOnlyOption(this.Group, "Lighting", true);
                    this.LightingDebug = OptionGroup.newDebugOnlyOption(this.Group, "LightingDebug", false);
                    this.LightingOldDebug = OptionGroup.newDebugOnlyOption(this.Group, "LightingOldDebug", false);
                    this.AttachedSprites = OptionGroup.newDebugOnlyOption(this.Group, "AttachedSprites", true);
                }
            }
            
            public final class Floor extends OptionGroup
            {
                public final BooleanDebugOption Lighting;
                public final BooleanDebugOption LightingOld;
                public final BooleanDebugOption LightingDebug;
                
                public Floor(final IDebugOptionGroup debugOptionGroup) {
                    super(debugOptionGroup, "Floor");
                    this.Lighting = OptionGroup.newDebugOnlyOption(this.Group, "Lighting", true);
                    this.LightingOld = OptionGroup.newDebugOnlyOption(this.Group, "LightingOld", false);
                    this.LightingDebug = OptionGroup.newDebugOnlyOption(this.Group, "LightingDebug", false);
                }
            }
        }
    }
}
