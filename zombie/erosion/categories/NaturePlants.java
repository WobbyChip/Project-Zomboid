// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.categories;

import java.nio.ByteBuffer;
import zombie.erosion.obj.ErosionObjSprites;
import zombie.iso.sprite.IsoSprite;
import zombie.core.Rand;
import zombie.iso.IsoObject;
import zombie.erosion.ErosionData;
import zombie.iso.IsoGridSquare;
import zombie.erosion.obj.ErosionObj;
import java.util.ArrayList;

public final class NaturePlants extends ErosionCategory
{
    private final int[][] soilRef;
    private int[] spawnChance;
    private ArrayList<ErosionObj> objs;
    private final PlantInit[] plants;
    
    public NaturePlants() {
        this.soilRef = new int[][] { { 17, 17, 17, 17, 17, 17, 17, 17, 17, 1, 2, 8, 8 }, { 11, 12, 1, 2, 8, 1, 2, 8, 1, 2, 8, 1, 2, 8, 1, 2, 8 }, { 11, 12, 11, 12, 11, 12, 11, 12, 15, 16, 18, 19 }, { 22, 22, 22, 22, 22, 22, 22, 22, 22, 3, 4, 14 }, { 15, 16, 3, 4, 14, 3, 4, 14, 3, 4, 14, 3, 4, 14 }, { 11, 12, 15, 16, 15, 16, 15, 16, 15, 16, 21 }, { 13, 13, 13, 13, 13, 13, 13, 13, 13, 5, 6, 24 }, { 18, 19, 5, 6, 24, 5, 6, 24, 5, 6, 24, 5, 6, 24 }, { 18, 19, 18, 19, 18, 19, 18, 19, 20, 21 }, { 7, 7, 7, 7, 7, 7, 7, 7, 7, 9, 10, 23 }, { 19, 20, 9, 10, 23, 9, 10, 23, 9, 10, 23, 9, 10, 23 }, { 15, 16, 18, 19, 20, 19, 20, 19, 20 } };
        this.spawnChance = new int[100];
        this.objs = new ArrayList<ErosionObj>();
        this.plants = new PlantInit[] { new PlantInit("Butterfly Weed", true, 0.05f, 0.25f), new PlantInit("Butterfly Weed", true, 0.05f, 0.25f), new PlantInit("Swamp Sunflower", true, 0.2f, 0.45f), new PlantInit("Swamp Sunflower", true, 0.2f, 0.45f), new PlantInit("Purple Coneflower", true, 0.1f, 0.35f), new PlantInit("Purple Coneflower", true, 0.1f, 0.35f), new PlantInit("Joe-Pye Weed", true, 0.8f, 1.0f), new PlantInit("Blazing Star", true, 0.25f, 0.65f), new PlantInit("Wild Bergamot", true, 0.45f, 0.6f), new PlantInit("Wild Bergamot", true, 0.45f, 0.6f), new PlantInit("White Beard-tongue", true, 0.2f, 0.65f), new PlantInit("White Beard-tongue", true, 0.2f, 0.65f), new PlantInit("Ironweed", true, 0.75f, 0.85f), new PlantInit("White Baneberry", true, 0.4f, 0.8f), new PlantInit("Wild Columbine", true, 0.85f, 1.0f), new PlantInit("Wild Columbine", true, 0.85f, 1.0f), new PlantInit("Jack-in-the-pulpit", false, 0.0f, 0.0f), new PlantInit("Wild Ginger", true, 0.1f, 0.9f), new PlantInit("Wild Ginger", true, 0.1f, 0.9f), new PlantInit("Wild Geranium", true, 0.65f, 0.9f), new PlantInit("Alumroot", true, 0.35f, 0.75f), new PlantInit("Wild Blue Phlox", true, 0.15f, 0.55f), new PlantInit("Polemonium Reptans", true, 0.4f, 0.6f), new PlantInit("Foamflower", true, 0.45f, 1.0f) };
    }
    
    @Override
    public boolean replaceExistingObject(final IsoGridSquare isoGridSquare, final ErosionData.Square catModData, final ErosionData.Chunk chunk, final boolean b, final boolean b2) {
        for (int i = isoGridSquare.getObjects().size() - 1; i >= 1; --i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            final IsoSprite sprite = isoObject.getSprite();
            if (sprite != null && sprite.getName() != null) {
                if (sprite.getName().startsWith("d_plants_1_")) {
                    final int int1 = Integer.parseInt(sprite.getName().replace("d_plants_1_", ""));
                    final CategoryData categoryData = (CategoryData)this.setCatModData(catModData);
                    categoryData.gameObj = ((int1 < 32) ? (int1 % 8) : ((int1 < 48) ? (int1 % 8 + 8) : (int1 % 8 + 16)));
                    categoryData.stage = 0;
                    categoryData.spawnTime = 0;
                    isoGridSquare.RemoveTileObjectErosionNoRecalc(isoObject);
                    return true;
                }
                if ("vegetation_groundcover_01_16".equals(sprite.getName()) || "vegetation_groundcover_01_17".equals(sprite.getName())) {
                    final CategoryData categoryData2 = (CategoryData)this.setCatModData(catModData);
                    categoryData2.gameObj = 21;
                    categoryData2.stage = 0;
                    categoryData2.spawnTime = 0;
                    isoGridSquare.RemoveTileObjectErosionNoRecalc(isoObject);
                    while (--i > 0) {
                        final IsoObject isoObject2 = isoGridSquare.getObjects().get(i);
                        final IsoSprite sprite2 = isoObject2.getSprite();
                        if (sprite2 != null && sprite2.getName() != null && sprite2.getName().startsWith("vegetation_groundcover_01_")) {
                            isoGridSquare.RemoveTileObjectErosionNoRecalc(isoObject2);
                        }
                    }
                    return true;
                }
                if ("vegetation_groundcover_01_18".equals(sprite.getName()) || "vegetation_groundcover_01_19".equals(sprite.getName()) || "vegetation_groundcover_01_20".equals(sprite.getName()) || "vegetation_groundcover_01_21".equals(sprite.getName()) || "vegetation_groundcover_01_22".equals(sprite.getName()) || "vegetation_groundcover_01_23".equals(sprite.getName())) {
                    final CategoryData categoryData3 = (CategoryData)this.setCatModData(catModData);
                    categoryData3.gameObj = Rand.Next(this.plants.length);
                    categoryData3.stage = 0;
                    categoryData3.spawnTime = 0;
                    isoGridSquare.RemoveTileObjectErosionNoRecalc(isoObject);
                    while (--i > 0) {
                        final IsoObject isoObject3 = isoGridSquare.getObjects().get(i);
                        final IsoSprite sprite3 = isoObject3.getSprite();
                        if (sprite3 != null && sprite3.getName() != null && sprite3.getName().startsWith("vegetation_groundcover_01_")) {
                            isoGridSquare.RemoveTileObjectErosionNoRecalc(isoObject3);
                        }
                    }
                    return true;
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
        if (catModData.rand(isoGridSquare.x, isoGridSquare.y, 101) < this.spawnChance[noiseMainInt]) {
            final CategoryData categoryData = (CategoryData)this.setCatModData(catModData);
            categoryData.gameObj = array[catModData.rand(isoGridSquare.x, isoGridSquare.y, array.length)] - 1;
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
            this.updateObj(square, data, isoGridSquare, erosionObj, false, 0, this.currentSeason(square.magicNum, erosionObj), this.currentBloom(square.magicNum, erosionObj));
        }
        else {
            this.clearCatModData(square);
        }
    }
    
    @Override
    public void init() {
        for (int i = 0; i < 100; ++i) {
            if (i >= 20 && i < 50) {
                this.spawnChance[i] = (int)this.clerp((i - 20) / 30.0f, 0.0f, 8.0f);
            }
            else if (i >= 50 && i < 80) {
                this.spawnChance[i] = (int)this.clerp((i - 50) / 30.0f, 8.0f, 0.0f);
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
        final String s = "d_plants_1_";
        final ArrayList<String> list = new ArrayList<String>();
        for (int j = 0; j <= 7; ++j) {
            list.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, j));
        }
        final ArrayList<String> list2 = new ArrayList<String>();
        for (int k = 8; k <= 15; ++k) {
            list2.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, k));
        }
        int n = 16;
        for (int l = 0; l < this.plants.length; ++l) {
            if (l >= 8) {
                n = 24;
            }
            if (l >= 16) {
                n = 32;
            }
            final PlantInit plantInit = this.plants[l];
            final ErosionObjSprites erosionObjSprites = new ErosionObjSprites(1, plantInit.name, false, plantInit.hasFlower, false);
            erosionObjSprites.setBase(0, list, 1);
            erosionObjSprites.setBase(0, list2, 4);
            erosionObjSprites.setBase(0, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n + l), 2);
            erosionObjSprites.setFlower(0, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n + l + 8));
            this.objs.add(new ErosionObj(erosionObjSprites, 30, plantInit.hasFlower ? plantInit.bloomstart : 0.0f, plantInit.hasFlower ? plantInit.bloomend : 0.0f, false));
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
        public int spawnTime;
        
        @Override
        public void save(final ByteBuffer byteBuffer) {
            super.save(byteBuffer);
            byteBuffer.put((byte)this.gameObj);
            byteBuffer.putShort((short)this.spawnTime);
        }
        
        @Override
        public void load(final ByteBuffer byteBuffer, final int n) {
            super.load(byteBuffer, n);
            this.gameObj = byteBuffer.get();
            this.spawnTime = byteBuffer.getShort();
        }
    }
    
    private class PlantInit
    {
        public String name;
        public boolean hasFlower;
        public float bloomstart;
        public float bloomend;
        
        public PlantInit(final String name, final boolean hasFlower, final float bloomstart, final float bloomend) {
            this.name = name;
            this.hasFlower = hasFlower;
            this.bloomstart = bloomstart;
            this.bloomend = bloomend;
        }
    }
}
