// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion;

import zombie.iso.IsoObject;
import zombie.erosion.categories.ErosionCategory;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.IsoGridSquare;

public final class ErosionWorld
{
    public boolean init() {
        ErosionRegions.init();
        return true;
    }
    
    public void validateSpawn(final IsoGridSquare isoGridSquare, final ErosionData.Square square, final ErosionData.Chunk chunk) {
        final boolean is = isoGridSquare.Is(IsoFlagType.exterior);
        final boolean has = isoGridSquare.Has(IsoObjectType.wall);
        final IsoObject floor = isoGridSquare.getFloor();
        final String s = (floor != null && floor.getSprite() != null) ? floor.getSprite().getName() : null;
        if (s == null) {
            square.doNothing = true;
            return;
        }
        boolean b = false;
        for (int i = 0; i < ErosionRegions.regions.size(); ++i) {
            final ErosionRegions.Region region = ErosionRegions.regions.get(i);
            final String tileNameMatch = region.tileNameMatch;
            if ((tileNameMatch == null || s.startsWith(tileNameMatch)) && (!region.checkExterior || region.isExterior == is) && (!region.hasWall || region.hasWall == has)) {
                for (int j = 0; j < region.categories.size(); ++j) {
                    final ErosionCategory erosionCategory = region.categories.get(j);
                    boolean b2 = erosionCategory.replaceExistingObject(isoGridSquare, square, chunk, is, has);
                    if (!b2) {
                        b2 = erosionCategory.validateSpawn(isoGridSquare, square, chunk, is, has, false);
                    }
                    if (b2) {
                        b = true;
                        break;
                    }
                }
            }
        }
        if (!b) {
            square.doNothing = true;
        }
    }
    
    public void update(final IsoGridSquare isoGridSquare, final ErosionData.Square square, final ErosionData.Chunk chunk, final int n) {
        if (square.regions == null) {
            return;
        }
        for (int i = 0; i < square.regions.size(); ++i) {
            final ErosionCategory.Data data = square.regions.get(i);
            final ErosionCategory category = ErosionRegions.getCategory(data.regionID, data.categoryID);
            final int size = square.regions.size();
            category.update(isoGridSquare, square, data, chunk, n);
            if (size > square.regions.size()) {
                --i;
            }
        }
    }
}
