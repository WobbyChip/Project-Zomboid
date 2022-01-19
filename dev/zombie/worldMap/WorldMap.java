// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.asset.Asset;
import zombie.inventory.types.MapItem;
import zombie.worldMap.symbols.MapSymbolDefinitions;
import java.util.Iterator;
import zombie.ZomboidFileSystem;
import zombie.util.StringUtils;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import java.util.ArrayList;
import zombie.asset.AssetStateObserver;

public final class WorldMap implements AssetStateObserver
{
    public final ArrayList<WorldMapData> m_data;
    public final ArrayList<WorldMapImages> m_images;
    public int m_minDataX;
    public int m_minDataY;
    public int m_maxDataX;
    public int m_maxDataY;
    public int m_minX;
    public int m_minY;
    public int m_maxX;
    public int m_maxY;
    private boolean m_boundsFromData;
    public final ArrayList<WorldMapData> m_lastDataInDirectory;
    
    public WorldMap() {
        this.m_data = new ArrayList<WorldMapData>();
        this.m_images = new ArrayList<WorldMapImages>();
        this.m_boundsFromData = false;
        this.m_lastDataInDirectory = new ArrayList<WorldMapData>();
    }
    
    public void setBoundsInCells(final int n, final int n2, final int n3, final int n4) {
        this.setBoundsInSquares(n * 300, n2 * 300, n3 * 300 + 299, n4 * 300 + 299);
    }
    
    public void setBoundsInSquares(final int minX, final int minY, final int maxX, final int maxY) {
        this.m_minX = minX;
        this.m_minY = minY;
        this.m_maxX = maxX;
        this.m_maxY = maxY;
    }
    
    public void setBoundsFromData() {
        this.m_boundsFromData = true;
        this.setBoundsInCells(this.m_minDataX, this.m_minDataY, this.m_maxDataX, this.m_maxDataY);
    }
    
    public void setBoundsFromWorld() {
        final IsoMetaGrid metaGrid = IsoWorld.instance.getMetaGrid();
        this.setBoundsInCells(metaGrid.getMinX(), metaGrid.getMinY(), metaGrid.getMaxX(), metaGrid.getMaxY());
    }
    
    public void addData(final String relativeFileName) {
        if (StringUtils.isNullOrWhitespace(relativeFileName)) {
            return;
        }
        final WorldMapData orCreateData = WorldMapData.getOrCreateData(ZomboidFileSystem.instance.getString(relativeFileName));
        if (orCreateData != null && !this.m_data.contains(orCreateData)) {
            orCreateData.m_relativeFileName = relativeFileName;
            this.m_data.add(orCreateData);
            ((ArrayList<WorldMap>)orCreateData.getObserverCb()).add(this);
            if (orCreateData.isReady()) {
                this.updateDataBounds();
            }
        }
    }
    
    public int getDataCount() {
        return this.m_data.size();
    }
    
    public WorldMapData getDataByIndex(final int index) {
        return this.m_data.get(index);
    }
    
    public void clearData() {
        final Iterator<WorldMapData> iterator = this.m_data.iterator();
        while (iterator.hasNext()) {
            iterator.next().getObserverCb().remove(this);
        }
        this.m_data.clear();
        this.m_lastDataInDirectory.clear();
        this.updateDataBounds();
    }
    
    public void endDirectoryData() {
        if (this.hasData()) {
            final WorldMapData dataByIndex = this.getDataByIndex(this.getDataCount() - 1);
            if (!this.m_lastDataInDirectory.contains(dataByIndex)) {
                this.m_lastDataInDirectory.add(dataByIndex);
            }
        }
    }
    
    public boolean isLastDataInDirectory(final WorldMapData o) {
        return this.m_lastDataInDirectory.contains(o);
    }
    
    private void updateDataBounds() {
        this.m_minDataX = Integer.MAX_VALUE;
        this.m_minDataY = Integer.MAX_VALUE;
        this.m_maxDataX = Integer.MIN_VALUE;
        this.m_maxDataY = Integer.MIN_VALUE;
        for (int i = 0; i < this.m_data.size(); ++i) {
            final WorldMapData worldMapData = this.m_data.get(i);
            if (worldMapData.isReady()) {
                this.m_minDataX = Math.min(this.m_minDataX, worldMapData.m_minX);
                this.m_minDataY = Math.min(this.m_minDataY, worldMapData.m_minY);
                this.m_maxDataX = Math.max(this.m_maxDataX, worldMapData.m_maxX);
                this.m_maxDataY = Math.max(this.m_maxDataY, worldMapData.m_maxY);
            }
        }
        if (this.m_minDataX > this.m_maxDataX) {
            final int n = 0;
            this.m_maxDataY = n;
            this.m_minDataY = n;
            this.m_maxDataX = n;
            this.m_minDataX = n;
        }
    }
    
    public boolean hasData() {
        return !this.m_data.isEmpty();
    }
    
    public void addImages(final String s) {
        if (StringUtils.isNullOrWhitespace(s)) {
            return;
        }
        final WorldMapImages orCreate = WorldMapImages.getOrCreate(s);
        if (orCreate != null && !this.m_images.contains(orCreate)) {
            this.m_images.add(orCreate);
        }
    }
    
    public boolean hasImages() {
        return !this.m_images.isEmpty();
    }
    
    public int getImagesCount() {
        return this.m_images.size();
    }
    
    public WorldMapImages getImagesByIndex(final int index) {
        return this.m_images.get(index);
    }
    
    public int getMinXInCells() {
        return this.m_minX / 300;
    }
    
    public int getMinYInCells() {
        return this.m_minY / 300;
    }
    
    public int getMaxXInCells() {
        return this.m_maxX / 300;
    }
    
    public int getMaxYInCells() {
        return this.m_maxY / 300;
    }
    
    public int getWidthInCells() {
        return this.getMaxXInCells() - this.getMinXInCells() + 1;
    }
    
    public int getHeightInCells() {
        return this.getMaxYInCells() - this.getMinYInCells() + 1;
    }
    
    public int getMinXInSquares() {
        return this.m_minX;
    }
    
    public int getMinYInSquares() {
        return this.m_minY;
    }
    
    public int getMaxXInSquares() {
        return this.m_maxX;
    }
    
    public int getMaxYInSquares() {
        return this.m_maxY;
    }
    
    public int getWidthInSquares() {
        return this.m_maxX - this.m_minX + 1;
    }
    
    public int getHeightInSquares() {
        return this.m_maxY - this.m_minY + 1;
    }
    
    public WorldMapCell getCell(final int n, final int n2) {
        for (int i = 0; i < this.m_data.size(); ++i) {
            final WorldMapData worldMapData = this.m_data.get(i);
            if (worldMapData.isReady()) {
                final WorldMapCell cell = worldMapData.getCell(n, n2);
                if (cell != null) {
                    return cell;
                }
            }
        }
        return null;
    }
    
    public int getDataWidthInCells() {
        return this.m_maxDataX - this.m_minDataX + 1;
    }
    
    public int getDataHeightInCells() {
        return this.m_maxDataY - this.m_minDataY + 1;
    }
    
    public int getDataWidthInSquares() {
        return this.getDataWidthInCells() * 300;
    }
    
    public int getDataHeightInSquares() {
        return this.getDataHeightInCells() * 300;
    }
    
    public static void Reset() {
        WorldMapSettings.Reset();
        WorldMapVisited.Reset();
        WorldMapData.Reset();
        WorldMapImages.Reset();
        MapSymbolDefinitions.Reset();
        MapItem.Reset();
    }
    
    @Override
    public void onStateChanged(final Asset.State state, final Asset.State state2, final Asset asset) {
        this.updateDataBounds();
        if (this.m_boundsFromData) {
            this.setBoundsInCells(this.m_minDataX, this.m_minDataY, this.m_maxDataX, this.m_maxDataY);
        }
    }
}
