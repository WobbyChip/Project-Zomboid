// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.categories;

import java.nio.ByteBuffer;
import zombie.erosion.obj.ErosionObjOverlaySprites;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.erosion.ErosionData;
import zombie.iso.IsoGridSquare;
import zombie.erosion.obj.ErosionObjOverlay;
import java.util.ArrayList;

public final class WallCracks extends ErosionCategory
{
    private ArrayList<ErosionObjOverlay> objs;
    private static final int DIRNW = 0;
    private static final int DIRN = 1;
    private static final int DIRW = 2;
    private ArrayList<ArrayList<Integer>> objsRef;
    private ArrayList<ArrayList<Integer>> botRef;
    private ArrayList<ArrayList<Integer>> topRef;
    private int[] spawnChance;
    
    public WallCracks() {
        this.objs = new ArrayList<ErosionObjOverlay>();
        this.objsRef = new ArrayList<ArrayList<Integer>>();
        this.botRef = new ArrayList<ArrayList<Integer>>();
        this.topRef = new ArrayList<ArrayList<Integer>>();
        this.spawnChance = new int[100];
    }
    
    @Override
    public boolean replaceExistingObject(final IsoGridSquare isoGridSquare, final ErosionData.Square square, final ErosionData.Chunk chunk, final boolean b, final boolean b2) {
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
        IsoObject validWall = this.validWall(isoGridSquare, true, false);
        if (validWall != null) {
            final String name = validWall.getSprite().getName();
            if (name != null && name.startsWith("fencing")) {
                validWall = null;
            }
        }
        IsoObject validWall2 = this.validWall(isoGridSquare, false, false);
        if (validWall2 != null) {
            final String name2 = validWall2.getSprite().getName();
            if (name2 != null && name2.startsWith("fencing")) {
                validWall2 = null;
            }
        }
        int index;
        if (validWall != null && validWall2 != null) {
            index = 0;
        }
        else if (validWall != null) {
            index = 1;
        }
        else {
            if (validWall2 == null) {
                return false;
            }
            index = 2;
        }
        final boolean b4 = noiseMainInt < 35 && catModData.magicNum > 0.3f;
        final CategoryData categoryData = (CategoryData)this.setCatModData(catModData);
        categoryData.gameObj = this.objsRef.get(index).get(catModData.rand(isoGridSquare.x, isoGridSquare.y, this.objsRef.get(index).size()));
        categoryData.alpha = 0.0f;
        categoryData.spawnTime = noiseMainInt;
        if (b4) {
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ() + 1);
            if (gridSquare != null && this.validWall(gridSquare, index == 1, false) != null) {
                final int rand = catModData.rand(isoGridSquare.x, isoGridSquare.y, this.botRef.get(index).size());
                categoryData.gameObj = this.botRef.get(index).get(rand);
                final CategoryData hasTop = new CategoryData();
                hasTop.gameObj = this.topRef.get(index).get(rand);
                hasTop.alpha = 0.0f;
                hasTop.spawnTime = categoryData.spawnTime;
                categoryData.hasTop = hasTop;
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
            final float alpha = categoryData.alpha;
            float alpha2 = (n - categoryData.spawnTime) / 100.0f;
            if (alpha2 > 1.0f) {
                alpha2 = 1.0f;
            }
            if (alpha2 < 0.0f) {
                alpha2 = 0.0f;
            }
            if (alpha2 != alpha) {
                IsoObject isoObject = null;
                final IsoObject validWall = this.validWall(isoGridSquare, true, false);
                final IsoObject validWall2 = this.validWall(isoGridSquare, false, false);
                if (validWall != null && validWall2 != null) {
                    isoObject = validWall;
                }
                else if (validWall != null) {
                    isoObject = validWall;
                }
                else if (validWall2 != null) {
                    isoObject = validWall2;
                }
                if (isoObject != null) {
                    final int setOverlay = erosionObjOverlay.setOverlay(isoObject, categoryData.curID, 0, 0, alpha2);
                    if (setOverlay >= 0) {
                        categoryData.alpha = alpha2;
                        categoryData.curID = setOverlay;
                    }
                }
                else {
                    categoryData.doNothing = true;
                }
                if (categoryData.hasTop != null) {
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
            this.spawnChance[i] = ((i <= 50) ? 100 : 0);
        }
        final String s = "d_wallcracks_1_";
        final int[] array = { 2, 2, 2, 1, 1, 1, 0, 0, 0 };
        for (int j = 0; j < 3; ++j) {
            this.objsRef.add(new ArrayList<Integer>());
            this.topRef.add(new ArrayList<Integer>());
            this.botRef.add(new ArrayList<Integer>());
        }
        for (int k = 0; k < array.length; ++k) {
            for (int l = 0; l <= 7; ++l) {
                final int n = l * 9 + k;
                final ErosionObjOverlaySprites erosionObjOverlaySprites = new ErosionObjOverlaySprites(1, "WallCracks");
                erosionObjOverlaySprites.setSprite(0, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, s, n), 0);
                this.objs.add(new ErosionObjOverlay(erosionObjOverlaySprites, 60, true));
                this.objsRef.get(array[k]).add(this.objs.size() - 1);
                if (l == 0) {
                    this.botRef.get(array[k]).add(this.objs.size() - 1);
                }
                else if (l == 1) {
                    this.topRef.get(array[k]).add(this.objs.size() - 1);
                }
            }
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
        public int curID;
        public float alpha;
        public CategoryData hasTop;
        
        private CategoryData() {
            this.curID = -999999;
        }
        
        @Override
        public void save(final ByteBuffer byteBuffer) {
            super.save(byteBuffer);
            byteBuffer.put((byte)this.gameObj);
            byteBuffer.putShort((short)this.spawnTime);
            byteBuffer.putInt(this.curID);
            byteBuffer.putFloat(this.alpha);
            if (this.hasTop != null) {
                byteBuffer.put((byte)1);
                byteBuffer.put((byte)this.hasTop.gameObj);
                byteBuffer.putShort((short)this.hasTop.spawnTime);
                byteBuffer.putInt(this.hasTop.curID);
                byteBuffer.putFloat(this.hasTop.alpha);
            }
            else {
                byteBuffer.put((byte)0);
            }
        }
        
        @Override
        public void load(final ByteBuffer byteBuffer, final int n) {
            super.load(byteBuffer, n);
            this.gameObj = byteBuffer.get();
            this.spawnTime = byteBuffer.getShort();
            this.curID = byteBuffer.getInt();
            this.alpha = byteBuffer.getFloat();
            if (byteBuffer.get() == 1) {
                this.hasTop = new CategoryData();
                this.hasTop.gameObj = byteBuffer.get();
                this.hasTop.spawnTime = byteBuffer.getShort();
                this.hasTop.curID = byteBuffer.getInt();
                this.hasTop.alpha = byteBuffer.getFloat();
            }
        }
    }
}
