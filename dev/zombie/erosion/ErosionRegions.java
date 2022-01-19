// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion;

import zombie.erosion.categories.Flowerbed;
import zombie.erosion.categories.WallCracks;
import zombie.erosion.categories.WallVines;
import zombie.erosion.categories.StreetCracks;
import zombie.erosion.categories.NatureGeneric;
import zombie.erosion.categories.NaturePlants;
import zombie.erosion.categories.NatureBush;
import zombie.erosion.categories.NatureTrees;
import zombie.erosion.categories.ErosionCategory;
import java.util.ArrayList;

public final class ErosionRegions
{
    public static final int REGION_NATURE = 0;
    public static final int CATEGORY_TREES = 0;
    public static final int CATEGORY_BUSH = 1;
    public static final int CATEGORY_PLANTS = 2;
    public static final int CATEGORY_GENERIC = 3;
    public static final int REGION_STREET = 1;
    public static final int CATEGORY_STREET_CRACKS = 0;
    public static final int REGION_WALL = 2;
    public static final int CATEGORY_WALL_VINES = 0;
    public static final int CATEGORY_WALL_CRACKS = 1;
    public static final int REGION_FLOWERBED = 3;
    public static final int CATEGORY_FLOWERBED = 0;
    public static final ArrayList<Region> regions;
    
    private static void addRegion(final Region e) {
        e.ID = ErosionRegions.regions.size();
        ErosionRegions.regions.add(e);
    }
    
    public static ErosionCategory getCategory(final int index, final int index2) {
        return ErosionRegions.regions.get(index).categories.get(index2);
    }
    
    public static void init() {
        ErosionRegions.regions.clear();
        addRegion(new Region(0, "blends_natural_01", true, true, false).addCategory(0, new NatureTrees()).addCategory(1, new NatureBush()).addCategory(2, new NaturePlants()).addCategory(3, new NatureGeneric()));
        addRegion(new Region(1, "blends_street", true, true, false).addCategory(0, new StreetCracks()));
        addRegion(new Region(2, null, false, false, true).addCategory(0, new WallVines()).addCategory(1, new WallCracks()));
        addRegion(new Region(3, null, true, true, false).addCategory(0, new Flowerbed()));
        for (int i = 0; i < ErosionRegions.regions.size(); ++i) {
            ErosionRegions.regions.get(i).init();
        }
    }
    
    public static void Reset() {
        for (int i = 0; i < ErosionRegions.regions.size(); ++i) {
            ErosionRegions.regions.get(i).Reset();
        }
        ErosionRegions.regions.clear();
    }
    
    static {
        regions = new ArrayList<Region>();
    }
    
    public static final class Region
    {
        public int ID;
        public String tileNameMatch;
        public boolean checkExterior;
        public boolean isExterior;
        public boolean hasWall;
        public final ArrayList<ErosionCategory> categories;
        
        public Region(final int id, final String tileNameMatch, final boolean checkExterior, final boolean isExterior, final boolean hasWall) {
            this.categories = new ArrayList<ErosionCategory>();
            this.ID = id;
            this.tileNameMatch = tileNameMatch;
            this.checkExterior = checkExterior;
            this.isExterior = isExterior;
            this.hasWall = hasWall;
        }
        
        public Region addCategory(final int id, final ErosionCategory e) {
            e.ID = id;
            e.region = this;
            this.categories.add(e);
            return this;
        }
        
        public void init() {
            for (int i = 0; i < this.categories.size(); ++i) {
                this.categories.get(i).init();
            }
        }
        
        public void Reset() {
            this.categories.clear();
        }
    }
}
