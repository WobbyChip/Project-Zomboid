// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.categories;

import java.nio.ByteBuffer;
import zombie.erosion.obj.ErosionObjSprites;
import zombie.erosion.ErosionMain;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoObject;
import zombie.erosion.ErosionData;
import zombie.iso.IsoGridSquare;
import zombie.erosion.obj.ErosionObj;
import java.util.ArrayList;

public final class Flowerbed extends ErosionCategory
{
    private final int[] tileID;
    private final ArrayList<ErosionObj> objs;
    
    public Flowerbed() {
        this.tileID = new int[] { 16, 17, 18, 19, 20, 21, 22, 23, 28, 29, 30, 31 };
        this.objs = new ArrayList<ErosionObj>();
    }
    
    @Override
    public boolean replaceExistingObject(final IsoGridSquare isoGridSquare, final ErosionData.Square square, final ErosionData.Chunk chunk, final boolean b, final boolean b2) {
        for (int i = isoGridSquare.getObjects().size() - 1; i >= 0; --i) {
            final IsoSprite sprite = isoGridSquare.getObjects().get(i).getSprite();
            if (sprite != null && sprite.getName() != null) {
                if (sprite.getName().startsWith("f_flowerbed_1")) {
                    int int1 = Integer.parseInt(sprite.getName().replace("f_flowerbed_1_", ""));
                    if (int1 <= 23) {
                        if (int1 >= 12) {
                            int1 -= 12;
                        }
                        final CategoryData categoryData = (CategoryData)this.setCatModData(square);
                        categoryData.hasSpawned = true;
                        categoryData.gameObj = int1;
                        categoryData.dispSeason = -1;
                        isoGridSquare.getObjects().get(i).setName(this.objs.get(categoryData.gameObj).name);
                        return true;
                    }
                }
                if (sprite.getName().startsWith("vegetation_ornamental_01")) {
                    final int int2 = Integer.parseInt(sprite.getName().replace("vegetation_ornamental_01_", ""));
                    for (int j = 0; j < this.tileID.length; ++j) {
                        if (this.tileID[j] == int2) {
                            final CategoryData categoryData2 = (CategoryData)this.setCatModData(square);
                            categoryData2.hasSpawned = true;
                            categoryData2.gameObj = j;
                            categoryData2.dispSeason = -1;
                            isoGridSquare.getObjects().get(i).setName(this.objs.get(categoryData2.gameObj).name);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean validateSpawn(final IsoGridSquare isoGridSquare, final ErosionData.Square square, final ErosionData.Chunk chunk, final boolean b, final boolean b2, final boolean b3) {
        return false;
    }
    
    @Override
    public void update(final IsoGridSquare isoGridSquare, final ErosionData.Square square, final Data data, final ErosionData.Chunk chunk, final int n) {
        final CategoryData categoryData = (CategoryData)data;
        if (categoryData.doNothing) {
            return;
        }
        if (categoryData.gameObj >= 0 && categoryData.gameObj < this.objs.size()) {
            final ErosionObj erosionObj = this.objs.get(categoryData.gameObj);
            final boolean b = false;
            final int n2 = 0;
            final int season = ErosionMain.getInstance().getSeasons().getSeason();
            final boolean b2 = false;
            if (season == 5) {
                final IsoObject object = erosionObj.getObject(isoGridSquare, false);
                if (object != null) {
                    object.setSprite(ErosionMain.getInstance().getSpriteManager().getSprite("blends_natural_01_64"));
                    object.setName(null);
                }
                this.clearCatModData(square);
            }
            else {
                this.updateObj(square, data, isoGridSquare, erosionObj, b, n2, season, b2);
            }
        }
        else {
            this.clearCatModData(square);
        }
    }
    
    @Override
    public void init() {
        final String s = "vegetation_ornamental_01_";
        for (int i = 0; i < this.tileID.length; ++i) {
            final ErosionObjSprites erosionObjSprites = new ErosionObjSprites(1, "Flowerbed", false, false, false);
            erosionObjSprites.setBase(0, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, this.tileID[i]), 1);
            erosionObjSprites.setBase(0, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, this.tileID[i]), 2);
            erosionObjSprites.setBase(0, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, this.tileID[i] + 16), 4);
            this.objs.add(new ErosionObj(erosionObjSprites, 30, 0.0f, 0.0f, false));
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
        
        @Override
        public void save(final ByteBuffer byteBuffer) {
            super.save(byteBuffer);
            byteBuffer.put((byte)this.gameObj);
        }
        
        @Override
        public void load(final ByteBuffer byteBuffer, final int n) {
            super.load(byteBuffer, n);
            this.gameObj = byteBuffer.get();
        }
    }
}
