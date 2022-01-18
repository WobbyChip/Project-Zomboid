// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.categories;

import java.nio.ByteBuffer;
import zombie.erosion.obj.ErosionObjOverlaySprites;
import zombie.erosion.ErosionMain;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.IsoObject;
import zombie.erosion.ErosionData;
import zombie.iso.IsoGridSquare;
import java.util.HashMap;
import zombie.erosion.obj.ErosionObjOverlay;
import java.util.ArrayList;

public final class WallVines extends ErosionCategory
{
    private ArrayList<ErosionObjOverlay> objs;
    private static final int DIRNW = 0;
    private static final int DIRN = 1;
    private static final int DIRW = 2;
    private int[][] objsRef;
    private HashMap<String, Integer> spriteToObj;
    private HashMap<String, Integer> spriteToStage;
    private int[] spawnChance;
    
    public WallVines() {
        this.objs = new ArrayList<ErosionObjOverlay>();
        this.objsRef = new int[3][2];
        this.spriteToObj = new HashMap<String, Integer>();
        this.spriteToStage = new HashMap<String, Integer>();
        this.spawnChance = new int[100];
    }
    
    @Override
    public boolean replaceExistingObject(final IsoGridSquare isoGridSquare, final ErosionData.Square catModData, final ErosionData.Chunk chunk, final boolean b, final boolean b2) {
        for (int i = isoGridSquare.getObjects().size() - 1; i >= 1; --i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            if (isoObject.AttachedAnimSprite != null) {
                for (int j = 0; j < isoObject.AttachedAnimSprite.size(); ++j) {
                    final IsoSprite parentSprite = isoObject.AttachedAnimSprite.get(j).parentSprite;
                    if (parentSprite != null && parentSprite.getName() != null && parentSprite.getName().startsWith("f_wallvines_1_") && this.spriteToObj.containsKey(parentSprite.getName())) {
                        final CategoryData categoryData = (CategoryData)this.setCatModData(catModData);
                        categoryData.gameObj = this.spriteToObj.get(parentSprite.getName());
                        categoryData.stage = this.spriteToStage.get(parentSprite.getName());
                        categoryData.maxStage = 2;
                        categoryData.spawnTime = 0;
                        isoObject.AttachedAnimSprite.remove(j);
                        if (isoObject.AttachedAnimSprite != null && j < isoObject.AttachedAnimSprite.size()) {
                            isoObject.AttachedAnimSprite.remove(j);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean validateSpawn(final IsoGridSquare isoGridSquare, final ErosionData.Square catModData, final ErosionData.Chunk chunk, final boolean b, final boolean b2, final boolean b3) {
        if (!b) {
            return false;
        }
        final int noiseMainInt = catModData.noiseMainInt;
        final int n = this.spawnChance[noiseMainInt];
        if (n == 0) {
            return false;
        }
        if (catModData.rand(isoGridSquare.x, isoGridSquare.y, 101) >= n) {
            return false;
        }
        final IsoObject validWall = this.validWall(isoGridSquare, true, true);
        final IsoObject validWall2 = this.validWall(isoGridSquare, false, true);
        int n2;
        if (validWall != null && validWall2 != null) {
            n2 = 0;
        }
        else if (validWall != null) {
            n2 = 1;
        }
        else {
            if (validWall2 == null) {
                return false;
            }
            n2 = 2;
        }
        final CategoryData categoryData = (CategoryData)this.setCatModData(catModData);
        categoryData.gameObj = this.objsRef[n2][catModData.rand(isoGridSquare.x, isoGridSquare.y, this.objsRef[n2].length)];
        categoryData.maxStage = ((noiseMainInt > 65) ? 3 : ((noiseMainInt > 60) ? 2 : ((noiseMainInt > 55) ? 1 : 0)));
        categoryData.stage = 0;
        categoryData.spawnTime = 100 - noiseMainInt;
        if (categoryData.maxStage == 3) {
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ() + 1);
            if (gridSquare != null) {
                final IsoObject validWall3 = this.validWall(gridSquare, n2 == 1, true);
                final ErosionObjOverlay erosionObjOverlay = this.objs.get(categoryData.gameObj);
                if (validWall3 != null && erosionObjOverlay != null) {
                    final CategoryData hasTop = new CategoryData();
                    hasTop.gameObj = this.objsRef[n2][catModData.rand(isoGridSquare.x, isoGridSquare.y, this.objsRef[n2].length)];
                    hasTop.maxStage = ((noiseMainInt > 75) ? 2 : ((noiseMainInt > 70) ? 1 : 0));
                    hasTop.stage = 0;
                    hasTop.spawnTime = categoryData.spawnTime + (int)(erosionObjOverlay.cycleTime / (categoryData.maxStage + 1.0f) * 4.0f);
                    categoryData.hasTop = hasTop;
                }
                else {
                    categoryData.maxStage = 2;
                }
            }
            else {
                categoryData.maxStage = 2;
            }
        }
        return true;
    }
    
    @Override
    public void update(final IsoGridSquare isoGridSquare, final ErosionData.Square square, final Data data, final ErosionData.Chunk chunk, final int n) {
        final CategoryData categoryData = (CategoryData)data;
        if (n < categoryData.spawnTime || categoryData.doNothing) {
            return;
        }
        if (categoryData.gameObj >= 0 && categoryData.gameObj < this.objs.size()) {
            final ErosionObjOverlay erosionObjOverlay = this.objs.get(categoryData.gameObj);
            final int maxStage = categoryData.maxStage;
            int n2 = (int)Math.floor((n - categoryData.spawnTime) / (erosionObjOverlay.cycleTime / (maxStage + 1.0f)));
            if (n2 < categoryData.stage) {
                n2 = categoryData.stage;
            }
            if (n2 > maxStage) {
                n2 = maxStage;
            }
            if (n2 > erosionObjOverlay.stages) {
                n2 = erosionObjOverlay.stages;
            }
            if (n2 == 3 && categoryData.hasTop != null && categoryData.hasTop.spawnTime > n) {
                n2 = 2;
            }
            final int season = ErosionMain.getInstance().getSeasons().getSeason();
            if (n2 != categoryData.stage || categoryData.dispSeason != season) {
                IsoObject isoObject = null;
                final IsoObject validWall = this.validWall(isoGridSquare, true, true);
                final IsoObject validWall2 = this.validWall(isoGridSquare, false, true);
                if (validWall != null && validWall2 != null) {
                    isoObject = validWall;
                }
                else if (validWall != null) {
                    isoObject = validWall;
                }
                else if (validWall2 != null) {
                    isoObject = validWall2;
                }
                categoryData.dispSeason = season;
                if (isoObject != null) {
                    final int setOverlay = erosionObjOverlay.setOverlay(isoObject, categoryData.curID, n2, season, 0.0f);
                    if (setOverlay >= 0) {
                        categoryData.curID = setOverlay;
                    }
                }
                else {
                    categoryData.doNothing = true;
                }
                if (n2 == 3 && categoryData.hasTop != null) {
                    final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ() + 1);
                    if (gridSquare != null) {
                        this.update(gridSquare, square, categoryData.hasTop, chunk, n);
                    }
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
            this.spawnChance[i] = ((i >= 50) ? 100 : 0);
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
        final String s = "f_wallvines_1_";
        final int[] array = { 5, 2, 4, 1 };
        final int[] array2 = { 2, 2, 1, 1, 0, 0 };
        final int[] array3 = new int[3];
        for (int j = 0; j < array2.length; ++j) {
            final ErosionObjOverlaySprites erosionObjOverlaySprites = new ErosionObjOverlaySprites(4, "WallVines");
            for (int k = 0; k <= 3; ++k) {
                for (int l = 0; l <= 2; ++l) {
                    final int n = l * 24 + k * 6 + j;
                    erosionObjOverlaySprites.setSprite(k, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n), array[l]);
                    if (l == 2) {
                        erosionObjOverlaySprites.setSprite(k, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n), array[l + 1]);
                    }
                    this.spriteToObj.put(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n), Integer.valueOf(this.objs.size()));
                    this.spriteToStage.put(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n), Integer.valueOf(k));
                }
            }
            this.objs.add(new ErosionObjOverlay(erosionObjOverlaySprites, 60, false));
            this.objsRef[array2[j]][array3[array2[j]]++] = this.objs.size() - 1;
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
        public CategoryData hasTop;
        
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
            if (this.hasTop != null) {
                byteBuffer.put((byte)1);
                byteBuffer.put((byte)this.hasTop.gameObj);
                byteBuffer.putShort((short)this.hasTop.spawnTime);
                byteBuffer.putInt(this.hasTop.curID);
            }
            else {
                byteBuffer.put((byte)0);
            }
        }
        
        @Override
        public void load(final ByteBuffer byteBuffer, final int n) {
            super.load(byteBuffer, n);
            this.gameObj = byteBuffer.get();
            this.maxStage = byteBuffer.get();
            this.spawnTime = byteBuffer.getShort();
            this.curID = byteBuffer.getInt();
            if (byteBuffer.get() == 1) {
                this.hasTop = new CategoryData();
                this.hasTop.gameObj = byteBuffer.get();
                this.hasTop.spawnTime = byteBuffer.getShort();
                this.hasTop.curID = byteBuffer.getInt();
            }
        }
    }
}
