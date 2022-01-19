// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.categories;

import java.nio.ByteBuffer;
import zombie.erosion.obj.ErosionObjSprites;
import zombie.erosion.season.ErosionIceQueen;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoObject;
import zombie.erosion.ErosionData;
import zombie.iso.IsoGridSquare;
import zombie.erosion.obj.ErosionObj;
import java.util.ArrayList;

public final class NatureTrees extends ErosionCategory
{
    private final int[][] soilRef;
    private final TreeInit[] trees;
    private int[] spawnChance;
    private ArrayList<ErosionObj> objs;
    
    public NatureTrees() {
        this.soilRef = new int[][] { { 2, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5 }, { 1, 1, 2, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5 }, { 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 3, 3, 4, 4, 4, 5 }, { 1, 7, 7, 7, 9, 9, 9, 9, 9, 9, 9 }, { 2, 2, 1, 1, 1, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 9, 9, 9, 9 }, { 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 7, 7, 7, 9 }, { 1, 2, 8, 8, 8, 6, 6, 6, 6, 6, 6, 6, 6 }, { 1, 1, 2, 2, 3, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 6, 6, 6, 6, 6 }, { 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 8, 8, 8, 6 }, { 3, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11 }, { 1, 1, 3, 3, 3, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11 }, { 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 10, 10, 10, 11 } };
        this.trees = new TreeInit[] { new TreeInit("American Holly", "e_americanholly_1", true), new TreeInit("Canadian Hemlock", "e_canadianhemlock_1", true), new TreeInit("Virginia Pine", "e_virginiapine_1", true), new TreeInit("Riverbirch", "e_riverbirch_1", false), new TreeInit("Cockspur Hawthorn", "e_cockspurhawthorn_1", false), new TreeInit("Dogwood", "e_dogwood_1", false), new TreeInit("Carolina Silverbell", "e_carolinasilverbell_1", false), new TreeInit("Yellowwood", "e_yellowwood_1", false), new TreeInit("Eastern Redbud", "e_easternredbud_1", false), new TreeInit("Redmaple", "e_redmaple_1", false), new TreeInit("American Linden", "e_americanlinden_1", false) };
        this.spawnChance = new int[100];
        this.objs = new ArrayList<ErosionObj>();
    }
    
    @Override
    public boolean replaceExistingObject(final IsoGridSquare isoGridSquare, final ErosionData.Square catModData, final ErosionData.Chunk chunk, final boolean b, final boolean b2) {
        for (int i = isoGridSquare.getObjects().size() - 1; i >= 1; --i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            final IsoSprite sprite = isoObject.getSprite();
            if (sprite != null && sprite.getName() != null) {
                if (sprite.getName().startsWith("jumbo_tree_01")) {
                    int n = catModData.soil;
                    if (n < 0 || n >= this.soilRef.length) {
                        n = catModData.rand(isoGridSquare.x, isoGridSquare.y, this.soilRef.length);
                    }
                    final int[] array = this.soilRef[n];
                    final int noiseMainInt = catModData.noiseMainInt;
                    final CategoryData categoryData = (CategoryData)this.setCatModData(catModData);
                    categoryData.gameObj = array[catModData.rand(isoGridSquare.x, isoGridSquare.y, array.length)] - 1;
                    categoryData.maxStage = 5 + (int)Math.floor(noiseMainInt / 51.0f) - 1;
                    categoryData.stage = categoryData.maxStage;
                    categoryData.spawnTime = 0;
                    categoryData.dispSeason = -1;
                    isoObject.setName(this.objs.get(categoryData.gameObj).name);
                    return categoryData.hasSpawned = true;
                }
                if (sprite.getName().startsWith("vegetation_trees")) {
                    int n2 = catModData.soil;
                    if (n2 < 0 || n2 >= this.soilRef.length) {
                        n2 = catModData.rand(isoGridSquare.x, isoGridSquare.y, this.soilRef.length);
                    }
                    final int[] array2 = this.soilRef[n2];
                    final int noiseMainInt2 = catModData.noiseMainInt;
                    final CategoryData categoryData2 = (CategoryData)this.setCatModData(catModData);
                    categoryData2.gameObj = array2[catModData.rand(isoGridSquare.x, isoGridSquare.y, array2.length)] - 1;
                    categoryData2.maxStage = 3 + (int)Math.floor(noiseMainInt2 / 51.0f) - 1;
                    categoryData2.stage = categoryData2.maxStage;
                    categoryData2.spawnTime = 0;
                    categoryData2.dispSeason = -1;
                    isoObject.setName(this.objs.get(categoryData2.gameObj).name);
                    return categoryData2.hasSpawned = true;
                }
                for (int j = 0; j < this.trees.length; ++j) {
                    if (sprite.getName().startsWith(this.trees[j].tile)) {
                        final CategoryData categoryData3 = (CategoryData)this.setCatModData(catModData);
                        categoryData3.gameObj = j;
                        categoryData3.maxStage = 3;
                        categoryData3.stage = categoryData3.maxStage;
                        categoryData3.spawnTime = 0;
                        isoGridSquare.RemoveTileObject(isoObject);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean validateSpawn(final IsoGridSquare isoGridSquare, final ErosionData.Square catModData, final ErosionData.Chunk chunk, final boolean b, final boolean b2, final boolean b3) {
        if (isoGridSquare.getObjects().size() > (b2 ? 2 : 1)) {
            return false;
        }
        if (catModData.soil < 0 || catModData.soil >= this.soilRef.length) {
            return false;
        }
        final int[] array = this.soilRef[catModData.soil];
        final int noiseMainInt = catModData.noiseMainInt;
        final int n = this.spawnChance[noiseMainInt];
        if (n > 0 && catModData.rand(isoGridSquare.x, isoGridSquare.y, 101) < n) {
            final CategoryData categoryData = (CategoryData)this.setCatModData(catModData);
            categoryData.gameObj = array[catModData.rand(isoGridSquare.x, isoGridSquare.y, array.length)] - 1;
            categoryData.maxStage = 2 + (int)Math.floor((noiseMainInt - 50) / 17) - 1;
            categoryData.stage = 0;
            categoryData.spawnTime = 30 + (100 - noiseMainInt);
            return true;
        }
        return false;
    }
    
    @Override
    public void update(final IsoGridSquare isoGridSquare, final ErosionData.Square square, final Data data, final ErosionData.Chunk chunk, final int n) {
        final CategoryData categoryData = (CategoryData)data;
        if (n < categoryData.spawnTime || categoryData.doNothing) {
            return;
        }
        if (categoryData.gameObj >= 0 && categoryData.gameObj < this.objs.size()) {
            final ErosionObj erosionObj = this.objs.get(categoryData.gameObj);
            final int maxStage = categoryData.maxStage;
            int stage = (int)Math.floor((n - categoryData.spawnTime) / (erosionObj.cycleTime / (maxStage + 1.0f)));
            if (stage < data.stage) {
                stage = data.stage;
            }
            if (stage > maxStage) {
                stage = maxStage;
            }
            this.updateObj(square, data, isoGridSquare, erosionObj, true, stage, this.currentSeason(square.magicNum, erosionObj), false);
        }
        else {
            this.clearCatModData(square);
        }
    }
    
    @Override
    public void init() {
        for (int i = 0; i < 100; ++i) {
            this.spawnChance[i] = ((i >= 50) ? ((int)this.clerp((i - 50) / 50.0f, 0.0f, 90.0f)) : 0);
        }
        final int[] array = { 0, 5, 1, 2, 3, 4 };
        this.seasonDisp[5].season1 = 0;
        this.seasonDisp[5].season2 = 0;
        this.seasonDisp[5].split = false;
        this.seasonDisp[1].season1 = 1;
        this.seasonDisp[1].season2 = 0;
        this.seasonDisp[1].split = false;
        this.seasonDisp[2].season1 = 2;
        this.seasonDisp[2].season2 = 3;
        this.seasonDisp[2].split = true;
        this.seasonDisp[4].season1 = 4;
        this.seasonDisp[4].season2 = 0;
        this.seasonDisp[4].split = true;
        String s = null;
        final ErosionIceQueen instance = ErosionIceQueen.instance;
        for (int j = 0; j < this.trees.length; ++j) {
            final String name = this.trees[j].name;
            final String tile = this.trees[j].tile;
            final boolean b = !this.trees[j].evergreen;
            final ErosionObjSprites erosionObjSprites = new ErosionObjSprites(6, name, true, false, b);
            for (int k = 0; k < 6; ++k) {
                for (int l = 0; l < array.length; ++l) {
                    if (k > 3) {
                        final int n = 0 + l * 2 + (k - 4);
                        if (l == 0) {
                            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, tile.replace("_1", "JUMBO_1"), n);
                            erosionObjSprites.setBase(k, s, 0);
                        }
                        else if (l == 1) {
                            instance.addSprite(s, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, tile.replace("_1", "JUMBO_1"), n));
                        }
                        else if (b) {
                            erosionObjSprites.setChildSprite(k, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, tile.replace("_1", "JUMBO_1"), n), array[l]);
                        }
                    }
                    else {
                        final int n2 = 0 + l * 4 + k;
                        if (l == 0) {
                            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, tile, n2);
                            erosionObjSprites.setBase(k, s, 0);
                        }
                        else if (l == 1) {
                            instance.addSprite(s, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, tile, n2));
                        }
                        else if (b) {
                            erosionObjSprites.setChildSprite(k, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, tile, n2), array[l]);
                        }
                    }
                }
            }
            this.objs.add(new ErosionObj(erosionObjSprites, 60, 0.0f, 0.0f, true));
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
        
        @Override
        public void save(final ByteBuffer byteBuffer) {
            super.save(byteBuffer);
            byteBuffer.put((byte)this.gameObj);
            byteBuffer.put((byte)this.maxStage);
            byteBuffer.putShort((short)this.spawnTime);
        }
        
        @Override
        public void load(final ByteBuffer byteBuffer, final int n) {
            super.load(byteBuffer, n);
            this.gameObj = byteBuffer.get();
            this.maxStage = byteBuffer.get();
            this.spawnTime = byteBuffer.getShort();
        }
    }
    
    private class TreeInit
    {
        public String name;
        public String tile;
        public boolean evergreen;
        
        public TreeInit(final String name, final String tile, final boolean evergreen) {
            this.name = name;
            this.tile = tile;
            this.evergreen = evergreen;
        }
    }
}
