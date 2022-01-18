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

public final class NatureBush extends ErosionCategory
{
    private final int[][] soilRef;
    private ArrayList<ErosionObj> objs;
    private int[] spawnChance;
    private BushInit[] bush;
    
    public NatureBush() {
        this.soilRef = new int[][] { { 11, 11, 12, 13 }, { 5, 5, 7, 8, 11, 11, 12, 13, 11, 11, 12, 13 }, { 5, 5, 7, 8, 5, 5, 7, 8, 11, 11, 12, 13 }, { 1, 1, 4, 5 }, { 5, 5, 7, 8, 1, 1, 4, 5, 1, 1, 4, 5 }, { 5, 5, 7, 8, 5, 5, 7, 8, 1, 1, 4, 5 }, { 9, 10, 14, 15 }, { 5, 5, 7, 8, 9, 10, 14, 15, 9, 10, 14, 15 }, { 5, 5, 7, 8, 5, 5, 7, 8, 9, 10, 14, 15 }, { 2, 3, 16, 16 }, { 5, 5, 7, 8, 2, 3, 16, 16, 2, 3, 16, 16 }, { 5, 5, 7, 8, 5, 5, 7, 8, 2, 3, 16, 16 } };
        this.objs = new ArrayList<ErosionObj>();
        this.spawnChance = new int[100];
        this.bush = new BushInit[] { new BushInit("Spicebush", 0.05f, 0.35f, false), new BushInit("Ninebark", 0.65f, 0.75f, true), new BushInit("Ninebark", 0.65f, 0.75f, true), new BushInit("Blueberry", 0.4f, 0.5f, true), new BushInit("Blackberry", 0.4f, 0.5f, true), new BushInit("Piedmont azalea", 0.0f, 0.15f, true), new BushInit("Piedmont azalea", 0.0f, 0.15f, true), new BushInit("Arrowwood viburnum", 0.3f, 0.8f, true), new BushInit("Red chokeberry", 0.9f, 1.0f, true), new BushInit("Red chokeberry", 0.9f, 1.0f, true), new BushInit("Beautyberry", 0.7f, 0.85f, true), new BushInit("New jersey tea", 0.4f, 0.8f, true), new BushInit("New jersey tea", 0.4f, 0.8f, true), new BushInit("Wild hydrangea", 0.2f, 0.35f, true), new BushInit("Wild hydrangea", 0.2f, 0.35f, true), new BushInit("Shrubby St. John's wort", 0.35f, 0.75f, true) };
    }
    
    @Override
    public boolean replaceExistingObject(final IsoGridSquare isoGridSquare, final ErosionData.Square square, final ErosionData.Chunk chunk, final boolean b, final boolean b2) {
        final int size = isoGridSquare.getObjects().size();
        boolean b3 = false;
        for (int i = size - 1; i >= 1; --i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            final IsoSprite sprite = isoObject.getSprite();
            if (sprite != null && sprite.getName() != null) {
                if (sprite.getName().startsWith("vegetation_foliage")) {
                    int n = square.soil;
                    if (n < 0 || n >= this.soilRef.length) {
                        n = square.rand(isoGridSquare.x, isoGridSquare.y, this.soilRef.length);
                    }
                    final int[] array = this.soilRef[n];
                    final int noiseMainInt = square.noiseMainInt;
                    final CategoryData categoryData = (CategoryData)this.setCatModData(square);
                    categoryData.gameObj = array[square.rand(isoGridSquare.x, isoGridSquare.y, array.length)] - 1;
                    categoryData.maxStage = (int)Math.floor(noiseMainInt / 60.0f);
                    categoryData.stage = categoryData.maxStage;
                    categoryData.spawnTime = 0;
                    isoGridSquare.RemoveTileObject(isoObject);
                    b3 = true;
                }
                if (sprite.getName().startsWith("f_bushes_1_")) {
                    final int int1 = Integer.parseInt(sprite.getName().replace("f_bushes_1_", ""));
                    final CategoryData categoryData2 = (CategoryData)this.setCatModData(square);
                    categoryData2.gameObj = int1 % 16;
                    categoryData2.maxStage = 1;
                    categoryData2.stage = categoryData2.maxStage;
                    categoryData2.spawnTime = 0;
                    isoGridSquare.RemoveTileObject(isoObject);
                    b3 = true;
                }
            }
        }
        return b3;
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
        if (catModData.rand(isoGridSquare.x, isoGridSquare.y, 101) < this.spawnChance[noiseMainInt]) {
            final CategoryData categoryData = (CategoryData)this.setCatModData(catModData);
            categoryData.gameObj = array[catModData.rand(isoGridSquare.x, isoGridSquare.y, array.length)] - 1;
            categoryData.maxStage = (int)Math.floor(noiseMainInt / 60.0f);
            categoryData.stage = 0;
            categoryData.spawnTime = 100 - noiseMainInt;
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
            if (stage < categoryData.stage) {
                stage = categoryData.stage;
            }
            if (stage > maxStage) {
                stage = maxStage;
            }
            this.updateObj(square, data, isoGridSquare, erosionObj, false, stage, this.currentSeason(square.magicNum, erosionObj), this.currentBloom(square.magicNum, erosionObj));
        }
        else {
            categoryData.doNothing = true;
        }
    }
    
    @Override
    public void init() {
        for (int i = 0; i < 100; ++i) {
            if (i >= 45 && i < 60) {
                this.spawnChance[i] = (int)this.clerp((i - 45) / 15.0f, 0.0f, 20.0f);
            }
            if (i >= 60 && i < 90) {
                this.spawnChance[i] = (int)this.clerp((i - 60) / 30.0f, 20.0f, 0.0f);
            }
        }
        this.seasonDisp[5].season1 = 0;
        this.seasonDisp[5].season2 = 0;
        this.seasonDisp[5].split = false;
        this.seasonDisp[1].season1 = 1;
        this.seasonDisp[1].season2 = 0;
        this.seasonDisp[1].split = false;
        this.seasonDisp[2].season1 = 2;
        this.seasonDisp[2].season2 = 2;
        this.seasonDisp[2].split = true;
        this.seasonDisp[4].season1 = 4;
        this.seasonDisp[4].season2 = 0;
        this.seasonDisp[4].split = true;
        final ErosionIceQueen instance = ErosionIceQueen.instance;
        final String s = "f_bushes_1_";
        for (int j = 1; j <= this.bush.length; ++j) {
            final int n = j - 1;
            final int n2 = n - (int)Math.floor(n / 8.0f) * 8;
            final BushInit bushInit = this.bush[n];
            final ErosionObjSprites erosionObjSprites = new ErosionObjSprites(2, bushInit.name, true, bushInit.hasFlower, true);
            final int n3 = 0 + n2;
            final int n4 = n3 + 16;
            final int n5 = n4 + 16;
            final int n6 = n5 + 16;
            final int n7 = 64 + n;
            final int n8 = n7 + 16;
            erosionObjSprites.setBase(0, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n3), 0);
            erosionObjSprites.setBase(1, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n3 + 8), 0);
            instance.addSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n3), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n4));
            instance.addSprite(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n3 + 8), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n4 + 8));
            erosionObjSprites.setChildSprite(0, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n5), 1);
            erosionObjSprites.setChildSprite(1, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n5 + 8), 1);
            erosionObjSprites.setChildSprite(0, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n6), 4);
            erosionObjSprites.setChildSprite(1, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n6 + 8), 4);
            erosionObjSprites.setChildSprite(0, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n7), 2);
            erosionObjSprites.setChildSprite(1, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n7 + 32), 2);
            if (bushInit.hasFlower) {
                erosionObjSprites.setFlower(0, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n8));
                erosionObjSprites.setFlower(1, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n8 + 32));
            }
            this.objs.add(new ErosionObj(erosionObjSprites, 60, bushInit.hasFlower ? bushInit.bloomstart : 0.0f, bushInit.hasFlower ? bushInit.bloomend : 0.0f, true));
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
    
    private class BushInit
    {
        public String name;
        public float bloomstart;
        public float bloomend;
        public boolean hasFlower;
        
        public BushInit(final String name, final float bloomstart, final float bloomend, final boolean hasFlower) {
            this.name = name;
            this.bloomstart = bloomstart;
            this.bloomend = bloomend;
            this.hasFlower = hasFlower;
        }
    }
}
