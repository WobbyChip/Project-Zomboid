// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.categories;

import java.nio.ByteBuffer;
import zombie.erosion.season.ErosionIceQueen;
import zombie.erosion.obj.ErosionObjSprites;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoObject;
import zombie.erosion.ErosionData;
import zombie.iso.IsoGridSquare;
import zombie.erosion.obj.ErosionObj;
import java.util.ArrayList;

public final class NatureGeneric extends ErosionCategory
{
    private ArrayList<ErosionObj> objs;
    private static final int GRASS = 0;
    private static final int FERNS = 1;
    private static final int GENERIC = 2;
    private ArrayList<ArrayList<Integer>> objsRef;
    private int[] spawnChance;
    
    public NatureGeneric() {
        this.objs = new ArrayList<ErosionObj>();
        this.objsRef = new ArrayList<ArrayList<Integer>>();
        this.spawnChance = new int[100];
    }
    
    @Override
    public boolean replaceExistingObject(final IsoGridSquare isoGridSquare, final ErosionData.Square catModData, final ErosionData.Chunk chunk, final boolean b, final boolean b2) {
        for (int i = isoGridSquare.getObjects().size() - 1; i >= 1; --i) {
            final IsoObject isoObject = isoGridSquare.getObjects().get(i);
            final IsoSprite sprite = isoObject.getSprite();
            if (sprite != null && sprite.getName() != null && sprite.getName().startsWith("blends_grassoverlays")) {
                float n = 0.3f;
                float n2 = 12.0f;
                if ("Forest".equals(isoGridSquare.getZoneType())) {
                    n = 0.5f;
                    n2 = 6.0f;
                }
                else if ("DeepForest".equals(isoGridSquare.getZoneType())) {
                    n = 0.7f;
                    n2 = 3.0f;
                }
                final CategoryData categoryData = (CategoryData)this.setCatModData(catModData);
                ArrayList<Integer> list = this.objsRef.get(0);
                final int noiseMainInt = catModData.noiseMainInt;
                if (catModData.rand(isoGridSquare.x, isoGridSquare.y, 101) < noiseMainInt / n2) {
                    if (catModData.magicNum < n) {
                        list = this.objsRef.get(1);
                    }
                    else {
                        list = this.objsRef.get(2);
                    }
                    categoryData.notGrass = true;
                    categoryData.maxStage = ((noiseMainInt > 60) ? 1 : 0);
                }
                else {
                    categoryData.maxStage = ((noiseMainInt > 67) ? 2 : (noiseMainInt > 50));
                }
                categoryData.gameObj = list.get(catModData.rand(isoGridSquare.x, isoGridSquare.y, list.size()));
                categoryData.stage = categoryData.maxStage;
                categoryData.spawnTime = 0;
                categoryData.dispSeason = -1;
                isoObject.setName(this.objs.get(categoryData.gameObj).name);
                isoObject.doNotSync = true;
                return categoryData.hasSpawned = true;
            }
        }
        return false;
    }
    
    @Override
    public boolean validateSpawn(final IsoGridSquare isoGridSquare, final ErosionData.Square catModData, final ErosionData.Chunk chunk, final boolean b, final boolean b2, final boolean b3) {
        if (isoGridSquare.getObjects().size() > (b2 ? 2 : 1)) {
            return false;
        }
        final int noiseMainInt = catModData.noiseMainInt;
        if (catModData.rand(isoGridSquare.x, isoGridSquare.y, 101) < this.spawnChance[noiseMainInt]) {
            float n = 0.3f;
            float n2 = 12.0f;
            if ("Forest".equals(isoGridSquare.getZoneType())) {
                n = 0.5f;
                n2 = 6.0f;
            }
            else if ("DeepForest".equals(isoGridSquare.getZoneType())) {
                n = 0.7f;
                n2 = 3.0f;
            }
            final CategoryData categoryData = (CategoryData)this.setCatModData(catModData);
            ArrayList<Integer> list = this.objsRef.get(0);
            if (catModData.rand(isoGridSquare.x, isoGridSquare.y, 101) < noiseMainInt / n2) {
                if (catModData.magicNum < n) {
                    list = this.objsRef.get(1);
                }
                else {
                    list = this.objsRef.get(2);
                }
                categoryData.notGrass = true;
                categoryData.maxStage = ((noiseMainInt > 60) ? 1 : 0);
            }
            else {
                categoryData.maxStage = ((noiseMainInt > 67) ? 2 : ((noiseMainInt > 50) ? 1 : 0));
            }
            categoryData.gameObj = list.get(catModData.rand(isoGridSquare.x, isoGridSquare.y, list.size()));
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
            int maxStage2 = (int)Math.floor((n - categoryData.spawnTime) / (erosionObj.cycleTime / (maxStage + 1.0f)));
            if (maxStage2 > maxStage) {
                maxStage2 = maxStage;
            }
            if (maxStage2 >= erosionObj.stages) {
                maxStage2 = erosionObj.stages - 1;
            }
            if (categoryData.stage == categoryData.maxStage) {
                maxStage2 = categoryData.maxStage;
            }
            int n2 = 0;
            if (!categoryData.notGrass) {
                n2 = this.currentSeason(square.magicNum, erosionObj);
                final int groundGrassType = this.getGroundGrassType(isoGridSquare);
                if (groundGrassType == 2) {
                    n2 = Math.max(n2, 3);
                }
                else if (groundGrassType == 3) {
                    n2 = Math.max(n2, 4);
                }
            }
            this.updateObj(square, data, isoGridSquare, erosionObj, false, maxStage2, n2, false);
        }
        else {
            categoryData.doNothing = true;
        }
    }
    
    @Override
    public void init() {
        for (int i = 0; i < 100; ++i) {
            this.spawnChance[i] = (int)this.clerp((i - 0) / 100.0f, 0.0f, 99.0f);
        }
        this.seasonDisp[5].season1 = 5;
        this.seasonDisp[5].season2 = 0;
        this.seasonDisp[5].split = false;
        this.seasonDisp[1].season1 = 1;
        this.seasonDisp[1].season2 = 0;
        this.seasonDisp[1].split = false;
        this.seasonDisp[2].season1 = 2;
        this.seasonDisp[2].season2 = 3;
        this.seasonDisp[2].split = true;
        this.seasonDisp[4].season1 = 4;
        this.seasonDisp[4].season2 = 5;
        this.seasonDisp[4].split = true;
        final int[] array = { 1, 2, 3, 4, 5 };
        final int[] array2 = { 2, 1, 0 };
        for (int j = 0; j < 3; ++j) {
            this.objsRef.add(new ArrayList<Integer>());
        }
        for (int k = 0; k <= 5; ++k) {
            final ErosionObjSprites erosionObjSprites = new ErosionObjSprites(3, "Grass", false, false, false);
            for (int l = 0; l < array.length; ++l) {
                for (int n = 0; n < array2.length; ++n) {
                    erosionObjSprites.setBase(array2[n], invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, 0 + l * 18 + n * 6 + k), array[l]);
                }
            }
            this.objs.add(new ErosionObj(erosionObjSprites, 60, 0.0f, 0.0f, false));
            this.objsRef.get(0).add(this.objs.size() - 1);
        }
        for (int n2 = 0; n2 <= 15; ++n2) {
            final ErosionObjSprites erosionObjSprites2 = new ErosionObjSprites(2, "Generic", false, false, false);
            for (int n3 = 0; n3 <= 1; ++n3) {
                erosionObjSprites2.setBase(n3, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n3 * 16 + n2), 0);
            }
            this.objs.add(new ErosionObj(erosionObjSprites2, 60, 0.0f, 0.0f, true));
            this.objsRef.get(2).add(this.objs.size() - 1);
        }
        final ErosionIceQueen instance = ErosionIceQueen.instance;
        for (int n4 = 0; n4 <= 7; ++n4) {
            final ErosionObjSprites erosionObjSprites3 = new ErosionObjSprites(2, "Fern", true, false, false);
            for (int n5 = 0; n5 <= 1; ++n5) {
                final int n6 = 48 + n5 * 32 + n4;
                erosionObjSprites3.setBase(n5, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n6), 0);
                instance.addSprite(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n6), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n6 + 16));
            }
            this.objs.add(new ErosionObj(erosionObjSprites3, 60, 0.0f, 0.0f, true));
            this.objsRef.get(1).add(this.objs.size() - 1);
        }
    }
    
    @Override
    protected Data allocData() {
        return new CategoryData();
    }
    
    private int toInt(final char c) {
        switch (c) {
            case '0': {
                return 0;
            }
            case '1': {
                return 1;
            }
            case '2': {
                return 2;
            }
            case '3': {
                return 3;
            }
            case '4': {
                return 4;
            }
            case '5': {
                return 5;
            }
            case '6': {
                return 6;
            }
            case '7': {
                return 7;
            }
            case '8': {
                return 8;
            }
            case '9': {
                return 9;
            }
            default: {
                return 0;
            }
        }
    }
    
    private int getGroundGrassType(final IsoGridSquare isoGridSquare) {
        final IsoObject floor = isoGridSquare.getFloor();
        if (floor == null) {
            return 0;
        }
        final IsoSprite sprite = floor.getSprite();
        if (sprite != null && sprite.getName() != null && sprite.getName().startsWith("blends_natural_01_")) {
            int n = 0;
            for (int i = 18; i < sprite.getName().length(); ++i) {
                n += this.toInt(sprite.getName().charAt(i));
                if (i < sprite.getName().length() - 1) {
                    n *= 10;
                }
            }
            final int n2 = n / 8;
            final int n3 = n % 8;
            if (n2 == 2 && (n3 == 0 || n3 >= 5)) {
                return 1;
            }
            if (n2 == 4 && (n3 == 0 || n3 >= 5)) {
                return 2;
            }
            if (n2 == 6 && (n3 == 0 || n3 >= 5)) {
                return 3;
            }
        }
        return 0;
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
        public boolean notGrass;
        
        @Override
        public void save(final ByteBuffer byteBuffer) {
            super.save(byteBuffer);
            byteBuffer.put((byte)this.gameObj);
            byteBuffer.put((byte)this.maxStage);
            byteBuffer.putShort((short)this.spawnTime);
            byteBuffer.put((byte)(this.notGrass ? 1 : 0));
        }
        
        @Override
        public void load(final ByteBuffer byteBuffer, final int n) {
            super.load(byteBuffer, n);
            this.gameObj = byteBuffer.get();
            this.maxStage = byteBuffer.get();
            this.spawnTime = byteBuffer.getShort();
            this.notGrass = (byteBuffer.get() == 1);
        }
    }
}
