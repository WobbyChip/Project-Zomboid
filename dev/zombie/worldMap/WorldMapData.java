// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.core.math.PZMath;
import java.util.Iterator;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import zombie.asset.AssetType;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.asset.Asset;

public final class WorldMapData extends Asset
{
    public static final HashMap<String, WorldMapData> s_fileNameToData;
    public String m_relativeFileName;
    public final ArrayList<WorldMapCell> m_cells;
    public final HashMap<Integer, WorldMapCell> m_cellLookup;
    public int m_minX;
    public int m_minY;
    public int m_maxX;
    public int m_maxY;
    public static final AssetType ASSET_TYPE;
    
    public static WorldMapData getOrCreateData(final String key) {
        WorldMapData value = WorldMapData.s_fileNameToData.get(key);
        if (value == null && Files.exists(Paths.get(key, new String[0]), new LinkOption[0])) {
            value = (WorldMapData)WorldMapDataAssetManager.instance.load(new AssetPath(key));
            WorldMapData.s_fileNameToData.put(key, value);
        }
        return value;
    }
    
    public WorldMapData(final AssetPath assetPath, final AssetManager assetManager) {
        super(assetPath, assetManager);
        this.m_cells = new ArrayList<WorldMapCell>();
        this.m_cellLookup = new HashMap<Integer, WorldMapCell>();
    }
    
    public WorldMapData(final AssetPath assetPath, final AssetManager assetManager, final AssetManager.AssetParams assetParams) {
        super(assetPath, assetManager);
        this.m_cells = new ArrayList<WorldMapCell>();
        this.m_cellLookup = new HashMap<Integer, WorldMapCell>();
    }
    
    public void clear() {
        final Iterator<WorldMapCell> iterator = this.m_cells.iterator();
        while (iterator.hasNext()) {
            iterator.next().dispose();
        }
        this.m_cells.clear();
        this.m_cellLookup.clear();
        this.m_minX = 0;
        this.m_minY = 0;
        this.m_maxX = 0;
        this.m_maxY = 0;
    }
    
    public int getWidthInCells() {
        return this.m_maxX - this.m_minX + 1;
    }
    
    public int getHeightInCells() {
        return this.m_maxY - this.m_minY + 1;
    }
    
    public int getWidthInSquares() {
        return this.getWidthInCells() * 300;
    }
    
    public int getHeightInSquares() {
        return this.getHeightInCells() * 300;
    }
    
    public void onLoaded() {
        this.m_minX = Integer.MAX_VALUE;
        this.m_minY = Integer.MAX_VALUE;
        this.m_maxX = Integer.MIN_VALUE;
        this.m_maxY = Integer.MIN_VALUE;
        this.m_cellLookup.clear();
        for (final WorldMapCell value : this.m_cells) {
            this.m_cellLookup.put(this.getCellKey(value.m_x, value.m_y), value);
            this.m_minX = Math.min(this.m_minX, value.m_x);
            this.m_minY = Math.min(this.m_minY, value.m_y);
            this.m_maxX = Math.max(this.m_maxX, value.m_x);
            this.m_maxY = Math.max(this.m_maxY, value.m_y);
        }
    }
    
    public WorldMapCell getCell(final int n, final int n2) {
        return this.m_cellLookup.get(this.getCellKey(n, n2));
    }
    
    private Integer getCellKey(final int n, final int n2) {
        return n + n2 * 1000;
    }
    
    public void hitTest(final float n, final float n2, final ArrayList<WorldMapFeature> list) {
        final int n3 = (int)PZMath.floor(n / 300.0f);
        final int n4 = (int)PZMath.floor(n2 / 300.0f);
        if (n3 < this.m_minX || n3 > this.m_maxX || n4 < this.m_minY || n4 > this.m_maxY) {
            return;
        }
        final WorldMapCell cell = this.getCell(n3, n4);
        if (cell == null) {
            return;
        }
        cell.hitTest(n, n2, list);
    }
    
    public static void Reset() {
    }
    
    @Override
    public AssetType getType() {
        return WorldMapData.ASSET_TYPE;
    }
    
    @Override
    protected void onBeforeEmpty() {
        super.onBeforeEmpty();
        this.clear();
    }
    
    static {
        s_fileNameToData = new HashMap<String, WorldMapData>();
        ASSET_TYPE = new AssetType("WorldMapData");
    }
}
