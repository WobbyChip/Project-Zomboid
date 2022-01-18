// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.categories;

import java.util.ArrayList;
import java.nio.ByteBuffer;
import zombie.erosion.season.ErosionSeason;
import zombie.erosion.ErosionMain;
import zombie.erosion.obj.ErosionObj;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.IsoDirections;
import zombie.iso.IsoObject;
import zombie.iso.IsoGridSquare;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionData;
import zombie.erosion.ErosionRegions;

public abstract class ErosionCategory
{
    public int ID;
    public ErosionRegions.Region region;
    protected SeasonDisplay[] seasonDisp;
    
    public ErosionCategory() {
        this.seasonDisp = new SeasonDisplay[6];
        for (int i = 0; i < 6; ++i) {
            this.seasonDisp[i] = new SeasonDisplay();
        }
    }
    
    protected Data getCatModData(final ErosionData.Square square) {
        for (int i = 0; i < square.regions.size(); ++i) {
            final Data data = square.regions.get(i);
            if (data.regionID == this.region.ID && data.categoryID == this.ID) {
                return data;
            }
        }
        return null;
    }
    
    protected Data setCatModData(final ErosionData.Square square) {
        Data e = this.getCatModData(square);
        if (e == null) {
            e = this.allocData();
            e.regionID = this.region.ID;
            e.categoryID = this.ID;
            square.regions.add(e);
            if (square.regions.size() > 5) {
                DebugLog.log("> 5 regions on a square");
            }
        }
        return e;
    }
    
    protected IsoObject validWall(final IsoGridSquare isoGridSquare, final boolean b, final boolean b2) {
        if (isoGridSquare == null) {
            return null;
        }
        final IsoGridSquare isoGridSquare2 = b ? isoGridSquare.getTileInDirection(IsoDirections.N) : isoGridSquare.getTileInDirection(IsoDirections.W);
        IsoObject isoObject = null;
        if (isoGridSquare.isWallTo(isoGridSquare2)) {
            if ((b && isoGridSquare.Is(IsoFlagType.cutN) && !isoGridSquare.Is(IsoFlagType.canPathN)) || (!b && isoGridSquare.Is(IsoFlagType.cutW) && !isoGridSquare.Is(IsoFlagType.canPathW))) {
                isoObject = isoGridSquare.getWall(b);
            }
        }
        else if (b2 && (isoGridSquare.isWindowBlockedTo(isoGridSquare2) || isoGridSquare.isWindowTo(isoGridSquare2))) {
            isoObject = isoGridSquare.getWindowTo(isoGridSquare2);
            if (isoObject == null) {
                isoObject = isoGridSquare.getWall(b);
            }
        }
        if (isoObject == null) {
            return null;
        }
        if (isoGridSquare.getZ() <= 0) {
            return isoObject;
        }
        final String name = isoObject.getSprite().getName();
        if (name != null && !name.contains("roof")) {
            return isoObject;
        }
        return null;
    }
    
    protected float clerp(final float n, final float n2, final float n3) {
        final float n4 = (float)(1.0 - Math.cos(n * 3.141592653589793)) / 2.0f;
        return n2 * (1.0f - n4) + n3 * n4;
    }
    
    protected int currentSeason(final float n, final ErosionObj erosionObj) {
        final ErosionSeason seasons = ErosionMain.getInstance().getSeasons();
        final int season = seasons.getSeason();
        final float seasonDay = seasons.getSeasonDay();
        final float seasonDays = seasons.getSeasonDays();
        final float n2 = seasonDays / 2.0f;
        final float n3 = n2 * n;
        final SeasonDisplay seasonDisplay = this.seasonDisp[season];
        int n4;
        if (seasonDisplay.split && seasonDay >= n2 + n3) {
            n4 = seasonDisplay.season2;
        }
        else if ((seasonDisplay.split && seasonDay >= n3) || seasonDay >= seasonDays * n) {
            n4 = seasonDisplay.season1;
        }
        else {
            SeasonDisplay seasonDisplay2;
            if (season == 5) {
                seasonDisplay2 = this.seasonDisp[4];
            }
            else if (season == 1) {
                seasonDisplay2 = this.seasonDisp[5];
            }
            else if (season == 2) {
                seasonDisplay2 = this.seasonDisp[1];
            }
            else {
                seasonDisplay2 = this.seasonDisp[2];
            }
            if (seasonDisplay2.split) {
                n4 = seasonDisplay2.season2;
            }
            else {
                n4 = seasonDisplay2.season1;
            }
        }
        return n4;
    }
    
    protected boolean currentBloom(final float n, final ErosionObj erosionObj) {
        boolean b = false;
        final ErosionSeason seasons = ErosionMain.getInstance().getSeasons();
        final int season = seasons.getSeason();
        if (erosionObj.hasFlower && season == 2) {
            final float seasonDay = seasons.getSeasonDay();
            final float seasonDays = seasons.getSeasonDays();
            final float n2 = seasonDays / 2.0f * n;
            final float n3 = seasonDays - n2;
            final float n4 = seasonDay - n2;
            final float n5 = n3 * erosionObj.bloomEnd;
            final float n6 = n3 * erosionObj.bloomStart;
            final float n7 = (n5 - n6) / 2.0f;
            final float n8 = n7 * n;
            final float n9 = n6 + n7 + n8;
            if (n4 >= n6 + n8 && n4 <= n9) {
                b = true;
            }
        }
        return b;
    }
    
    public void updateObj(final ErosionData.Square square, final Data data, final IsoGridSquare isoGridSquare, final ErosionObj erosionObj, final boolean b, final int stage, final int dispSeason, final boolean dispBloom) {
        if (!data.hasSpawned) {
            if (!erosionObj.placeObject(isoGridSquare, stage, b, dispSeason, dispBloom)) {
                this.clearCatModData(square);
                return;
            }
            data.hasSpawned = true;
        }
        else if (data.stage != stage || data.dispSeason != dispSeason || data.dispBloom != dispBloom) {
            final IsoObject object = erosionObj.getObject(isoGridSquare, false);
            if (object == null) {
                this.clearCatModData(square);
                return;
            }
            erosionObj.setStageObject(stage, object, dispSeason, dispBloom);
        }
        data.stage = stage;
        data.dispSeason = dispSeason;
        data.dispBloom = dispBloom;
    }
    
    protected void clearCatModData(final ErosionData.Square square) {
        for (int i = 0; i < square.regions.size(); ++i) {
            final Data data = square.regions.get(i);
            if (data.regionID == this.region.ID && data.categoryID == this.ID) {
                square.regions.remove(i);
                return;
            }
        }
    }
    
    public abstract void init();
    
    public abstract boolean replaceExistingObject(final IsoGridSquare p0, final ErosionData.Square p1, final ErosionData.Chunk p2, final boolean p3, final boolean p4);
    
    public abstract boolean validateSpawn(final IsoGridSquare p0, final ErosionData.Square p1, final ErosionData.Chunk p2, final boolean p3, final boolean p4, final boolean p5);
    
    public abstract void update(final IsoGridSquare p0, final ErosionData.Square p1, final Data p2, final ErosionData.Chunk p3, final int p4);
    
    protected abstract Data allocData();
    
    public static Data loadCategoryData(final ByteBuffer byteBuffer, final int n) {
        final byte value = byteBuffer.get();
        final byte value2 = byteBuffer.get();
        final Data allocData = ErosionRegions.getCategory(value, value2).allocData();
        allocData.regionID = value;
        allocData.categoryID = value2;
        allocData.load(byteBuffer, n);
        return allocData;
    }
    
    public abstract void getObjectNames(final ArrayList<String> p0);
    
    public static class Data
    {
        public int regionID;
        public int categoryID;
        public boolean doNothing;
        public boolean hasSpawned;
        public int stage;
        public int dispSeason;
        public boolean dispBloom;
        
        public void save(final ByteBuffer byteBuffer) {
            byte b = 0;
            if (this.doNothing) {
                b |= 0x1;
            }
            if (this.hasSpawned) {
                b |= 0x2;
            }
            if (this.dispBloom) {
                b |= 0x4;
            }
            if (this.stage == 1) {
                b |= 0x8;
            }
            else if (this.stage == 2) {
                b |= 0x10;
            }
            else if (this.stage == 3) {
                b |= 0x20;
            }
            else if (this.stage == 4) {
                b |= 0x40;
            }
            else if (this.stage > 4) {
                b |= (byte)128;
            }
            byteBuffer.put((byte)this.regionID);
            byteBuffer.put((byte)this.categoryID);
            byteBuffer.put((byte)this.dispSeason);
            byteBuffer.put(b);
            if (this.stage > 4) {
                byteBuffer.put((byte)this.stage);
            }
        }
        
        public void load(final ByteBuffer byteBuffer, final int n) {
            this.stage = 0;
            this.dispSeason = byteBuffer.get();
            final byte value = byteBuffer.get();
            this.doNothing = ((value & 0x1) != 0x0);
            this.hasSpawned = ((value & 0x2) != 0x0);
            this.dispBloom = ((value & 0x4) != 0x0);
            if ((value & 0x8) != 0x0) {
                this.stage = 1;
            }
            else if ((value & 0x10) != 0x0) {
                this.stage = 2;
            }
            else if ((value & 0x20) != 0x0) {
                this.stage = 3;
            }
            else if ((value & 0x40) != 0x0) {
                this.stage = 4;
            }
            else if ((value & 0x80) != 0x0) {
                this.stage = byteBuffer.get();
            }
        }
    }
    
    protected class SeasonDisplay
    {
        int season1;
        int season2;
        boolean split;
    }
}
