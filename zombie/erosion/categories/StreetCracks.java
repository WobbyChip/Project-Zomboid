// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.categories;

import java.nio.ByteBuffer;
import zombie.erosion.obj.ErosionObjSprites;
import zombie.erosion.obj.ErosionObjOverlaySprites;
import zombie.iso.IsoObject;
import zombie.erosion.ErosionData;
import zombie.iso.IsoGridSquare;
import zombie.erosion.obj.ErosionObjOverlay;
import zombie.erosion.obj.ErosionObj;
import java.util.ArrayList;

public final class StreetCracks extends ErosionCategory
{
    private ArrayList<ErosionObj> objs;
    private ArrayList<ErosionObjOverlay> crackObjs;
    private int[] spawnChance;
    
    public StreetCracks() {
        this.objs = new ArrayList<ErosionObj>();
        this.crackObjs = new ArrayList<ErosionObjOverlay>();
        this.spawnChance = new int[100];
    }
    
    @Override
    public boolean replaceExistingObject(final IsoGridSquare isoGridSquare, final ErosionData.Square square, final ErosionData.Chunk chunk, final boolean b, final boolean b2) {
        return false;
    }
    
    @Override
    public boolean validateSpawn(final IsoGridSquare isoGridSquare, final ErosionData.Square catModData, final ErosionData.Chunk chunk, final boolean b, final boolean b2, final boolean b3) {
        final int noiseMainInt = catModData.noiseMainInt;
        final int n = this.spawnChance[noiseMainInt];
        if (n == 0) {
            return false;
        }
        if (catModData.rand(isoGridSquare.x, isoGridSquare.y, 101) >= n) {
            return false;
        }
        final CategoryData categoryData = (CategoryData)this.setCatModData(catModData);
        categoryData.gameObj = catModData.rand(isoGridSquare.x, isoGridSquare.y, this.crackObjs.size());
        categoryData.maxStage = ((noiseMainInt > 65) ? 2 : ((noiseMainInt > 55) ? 1 : 0));
        categoryData.stage = 0;
        categoryData.spawnTime = 50 + (100 - noiseMainInt);
        if (catModData.magicNum > 0.5f) {
            categoryData.hasGrass = true;
        }
        return true;
    }
    
    @Override
    public void update(final IsoGridSquare isoGridSquare, final ErosionData.Square square, final Data data, final ErosionData.Chunk chunk, final int n) {
        final CategoryData categoryData = (CategoryData)data;
        if (n < categoryData.spawnTime || categoryData.doNothing) {
            return;
        }
        final IsoObject floor = isoGridSquare.getFloor();
        if (categoryData.gameObj >= 0 && categoryData.gameObj < this.crackObjs.size() && floor != null) {
            final ErosionObjOverlay erosionObjOverlay = this.crackObjs.get(categoryData.gameObj);
            int stage = (int)Math.floor((n - categoryData.spawnTime) / (erosionObjOverlay.cycleTime / (categoryData.maxStage + 1.0f)));
            if (stage < categoryData.stage) {
                stage = categoryData.stage;
            }
            if (stage >= erosionObjOverlay.stages) {
                stage = erosionObjOverlay.stages - 1;
            }
            if (stage != categoryData.stage) {
                final int setOverlay = erosionObjOverlay.setOverlay(floor, categoryData.curID, stage, 0, 0.0f);
                if (setOverlay >= 0) {
                    categoryData.curID = setOverlay;
                }
                categoryData.stage = stage;
            }
            else if (!categoryData.hasGrass && stage == erosionObjOverlay.stages - 1) {
                categoryData.doNothing = true;
            }
            if (categoryData.hasGrass) {
                final ErosionObj erosionObj = this.objs.get(categoryData.gameObj);
                if (erosionObj != null) {
                    this.updateObj(square, data, isoGridSquare, erosionObj, false, stage, this.currentSeason(square.magicNum, erosionObj), false);
                }
            }
        }
        else {
            categoryData.doNothing = true;
        }
    }
    
    @Override
    public void init() {
        for (int i = 0; i < 100; ++i) {
            this.spawnChance[i] = ((i >= 40) ? ((int)this.clerp((i - 40) / 60.0f, 0.0f, 60.0f)) : 0);
        }
        this.seasonDisp[5].season1 = 5;
        this.seasonDisp[5].season2 = 0;
        this.seasonDisp[5].split = false;
        this.seasonDisp[1].season1 = 1;
        this.seasonDisp[1].season2 = 0;
        this.seasonDisp[1].split = false;
        this.seasonDisp[2].season1 = 2;
        this.seasonDisp[2].season2 = 4;
        this.seasonDisp[2].split = true;
        this.seasonDisp[4].season1 = 4;
        this.seasonDisp[4].season2 = 5;
        this.seasonDisp[4].split = true;
        final String s = "d_streetcracks_1_";
        final int[] array = { 5, 1, 2, 4 };
        for (int j = 0; j <= 7; ++j) {
            final ErosionObjOverlaySprites erosionObjOverlaySprites = new ErosionObjOverlaySprites(3, "StreeCracks");
            final ErosionObjSprites erosionObjSprites = new ErosionObjSprites(3, "CrackGrass", false, false, false);
            for (int k = 0; k <= 2; ++k) {
                for (int l = 0; l <= array.length; ++l) {
                    final int n = l * 24 + k * 8 + j;
                    if (l == 0) {
                        erosionObjOverlaySprites.setSprite(k, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n), 0);
                    }
                    else {
                        erosionObjSprites.setBase(k, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n), array[l - 1]);
                    }
                }
            }
            this.crackObjs.add(new ErosionObjOverlay(erosionObjOverlaySprites, 60, false));
            this.objs.add(new ErosionObj(erosionObjSprites, 60, 0.0f, 0.0f, false));
        }
    }
    
    @Override
    protected Data allocData() {
        return new CategoryData();
    }
    
    @Override
    public void getObjectNames(final ArrayList<String> list) {
        for (int i = 0; i < this.objs.size(); ++i) {
            if (this.objs.get(i).name != null && !list.contains(this.objs.get(i).name)) {
                list.add(this.objs.get(i).name);
            }
        }
    }
    
    private static final class CategoryData extends Data
    {
        public int gameObj;
        public int maxStage;
        public int spawnTime;
        public int curID;
        public boolean hasGrass;
        
        private CategoryData() {
            this.curID = -999999;
        }
        
        @Override
        public void save(final ByteBuffer byteBuffer) {
            super.save(byteBuffer);
            byteBuffer.put((byte)this.gameObj);
            byteBuffer.put((byte)this.maxStage);
            byteBuffer.putShort((short)this.spawnTime);
            byteBuffer.putInt(this.curID);
            byteBuffer.put((byte)(this.hasGrass ? 1 : 0));
        }
        
        @Override
        public void load(final ByteBuffer byteBuffer, final int n) {
            super.load(byteBuffer, n);
            this.gameObj = byteBuffer.get();
            this.maxStage = byteBuffer.get();
            this.spawnTime = byteBuffer.getShort();
            this.curID = byteBuffer.getInt();
            this.hasGrass = (byteBuffer.get() == 1);
        }
    }
}
