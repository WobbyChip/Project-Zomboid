// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.config.ConfigOption;
import zombie.input.Mouse;
import org.joml.Matrix4f;
import zombie.inventory.types.MapItem;
import zombie.worldMap.symbols.WorldMapSymbolsV1;
import zombie.worldMap.styles.WorldMapStyleV1;
import zombie.worldMap.markers.WorldMapMarkersV1;
import zombie.worldMap.symbols.WorldMapSymbols;
import zombie.worldMap.markers.WorldMapMarkers;
import zombie.worldMap.styles.WorldMapStyle;

public class UIWorldMapV1
{
    final UIWorldMap m_ui;
    protected final WorldMap m_worldMap;
    protected final WorldMapStyle m_style;
    protected final WorldMapRenderer m_renderer;
    protected final WorldMapMarkers m_markers;
    protected WorldMapSymbols m_symbols;
    protected WorldMapMarkersV1 m_markersV1;
    protected WorldMapStyleV1 m_styleV1;
    protected WorldMapSymbolsV1 m_symbolsV1;
    
    public UIWorldMapV1(final UIWorldMap ui) {
        this.m_markersV1 = null;
        this.m_styleV1 = null;
        this.m_symbolsV1 = null;
        this.m_ui = ui;
        this.m_worldMap = this.m_ui.m_worldMap;
        this.m_style = this.m_ui.m_style;
        this.m_renderer = this.m_ui.m_renderer;
        this.m_markers = this.m_ui.m_markers;
        this.m_symbols = this.m_ui.m_symbols;
    }
    
    public void setMapItem(final MapItem mapItem) {
        this.m_ui.setMapItem(mapItem);
        this.m_symbols = this.m_ui.m_symbols;
    }
    
    public WorldMapRenderer getRenderer() {
        return this.m_renderer;
    }
    
    public WorldMapMarkers getMarkers() {
        return this.m_markers;
    }
    
    public WorldMapStyle getStyle() {
        return this.m_style;
    }
    
    public WorldMapMarkersV1 getMarkersAPI() {
        if (this.m_markersV1 == null) {
            this.m_markersV1 = new WorldMapMarkersV1(this.m_ui);
        }
        return this.m_markersV1;
    }
    
    public WorldMapStyleV1 getStyleAPI() {
        if (this.m_styleV1 == null) {
            this.m_styleV1 = new WorldMapStyleV1(this.m_ui);
        }
        return this.m_styleV1;
    }
    
    public WorldMapSymbolsV1 getSymbolsAPI() {
        if (this.m_symbolsV1 == null) {
            this.m_symbolsV1 = new WorldMapSymbolsV1(this.m_ui, this.m_symbols);
        }
        return this.m_symbolsV1;
    }
    
    public void addData(final String s) {
        final boolean hasData = this.m_worldMap.hasData();
        this.m_worldMap.addData(s);
        if (!hasData) {
            this.m_renderer.setMap(this.m_worldMap, this.m_ui.getAbsoluteX().intValue(), this.m_ui.getAbsoluteY().intValue(), this.m_ui.getWidth().intValue(), this.m_ui.getHeight().intValue());
            this.resetView();
        }
    }
    
    public int getDataCount() {
        return this.m_worldMap.getDataCount();
    }
    
    public String getDataFileByIndex(final int n) {
        return this.m_worldMap.getDataByIndex(n).m_relativeFileName;
    }
    
    public void clearData() {
        this.m_worldMap.clearData();
    }
    
    public void endDirectoryData() {
        this.m_worldMap.endDirectoryData();
    }
    
    public void addImages(final String s) {
        final boolean hasImages = this.m_worldMap.hasImages();
        this.m_worldMap.addImages(s);
        if (!hasImages) {
            this.m_renderer.setMap(this.m_worldMap, this.m_ui.getAbsoluteX().intValue(), this.m_ui.getAbsoluteY().intValue(), this.m_ui.getWidth().intValue(), this.m_ui.getHeight().intValue());
            this.resetView();
        }
    }
    
    public int getImagesCount() {
        return this.m_worldMap.getImagesCount();
    }
    
    public void setBoundsInCells(final int n, final int n2, final int n3, final int n4) {
        final boolean b = n * 300 != this.m_worldMap.m_minX || n2 * 300 != this.m_worldMap.m_minY || n3 * 300 + 299 != this.m_worldMap.m_maxX || n4 + 300 + 299 != this.m_worldMap.m_maxY;
        this.m_worldMap.setBoundsInCells(n, n2, n3, n4);
        if (b && this.m_worldMap.hasData()) {
            this.resetView();
        }
    }
    
    public void setBoundsInSquares(final int n, final int n2, final int n3, final int n4) {
        final boolean b = n != this.m_worldMap.m_minX || n2 != this.m_worldMap.m_minY || n3 != this.m_worldMap.m_maxX || n4 != this.m_worldMap.m_maxY;
        this.m_worldMap.setBoundsInSquares(n, n2, n3, n4);
        if (b && this.m_worldMap.hasData()) {
            this.resetView();
        }
    }
    
    public void setBoundsFromWorld() {
        this.m_worldMap.setBoundsFromWorld();
    }
    
    public void setBoundsFromData() {
        this.m_worldMap.setBoundsFromData();
    }
    
    public int getMinXInCells() {
        return this.m_worldMap.getMinXInCells();
    }
    
    public int getMinYInCells() {
        return this.m_worldMap.getMinYInCells();
    }
    
    public int getMaxXInCells() {
        return this.m_worldMap.getMaxXInCells();
    }
    
    public int getMaxYInCells() {
        return this.m_worldMap.getMaxYInCells();
    }
    
    public int getWidthInCells() {
        return this.m_worldMap.getWidthInCells();
    }
    
    public int getHeightInCells() {
        return this.m_worldMap.getHeightInCells();
    }
    
    public int getMinXInSquares() {
        return this.m_worldMap.getMinXInSquares();
    }
    
    public int getMinYInSquares() {
        return this.m_worldMap.getMinYInSquares();
    }
    
    public int getMaxXInSquares() {
        return this.m_worldMap.getMaxXInSquares();
    }
    
    public int getMaxYInSquares() {
        return this.m_worldMap.getMaxYInSquares();
    }
    
    public int getWidthInSquares() {
        return this.m_worldMap.getWidthInSquares();
    }
    
    public int getHeightInSquares() {
        return this.m_worldMap.getHeightInSquares();
    }
    
    public float uiToWorldX(final float n, final float n2, final float n3, final float n4, final float n5) {
        return this.m_renderer.uiToWorldX(n, n2, n3, n4, n5, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
    }
    
    public float uiToWorldY(final float n, final float n2, final float n3, final float n4, final float n5) {
        return this.m_renderer.uiToWorldY(n, n2, n3, n4, n5, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
    }
    
    protected float worldToUIX(final float n, final float n2, final float n3, final float n4, final float n5, final Matrix4f matrix4f, final Matrix4f matrix4f2) {
        return this.m_renderer.worldToUIX(n, n2, n3, n4, n5, matrix4f, matrix4f2);
    }
    
    protected float worldToUIY(final float n, final float n2, final float n3, final float n4, final float n5, final Matrix4f matrix4f, final Matrix4f matrix4f2) {
        return this.m_renderer.worldToUIY(n, n2, n3, n4, n5, matrix4f, matrix4f2);
    }
    
    protected float worldOriginUIX(final float n, final float n2) {
        return this.m_renderer.worldOriginUIX(n, n2);
    }
    
    protected float worldOriginUIY(final float n, final float n2) {
        return this.m_renderer.worldOriginUIY(n, n2);
    }
    
    protected float zoomMult() {
        return this.m_renderer.zoomMult();
    }
    
    protected float getWorldScale(final float n) {
        return this.m_renderer.getWorldScale(n);
    }
    
    public float worldOriginX() {
        return this.m_renderer.worldOriginUIX(this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldX());
    }
    
    public float worldOriginY() {
        return this.m_renderer.worldOriginUIY(this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldY());
    }
    
    public float getBaseZoom() {
        return this.m_renderer.getBaseZoom();
    }
    
    public float getZoomF() {
        return this.m_renderer.getDisplayZoomF();
    }
    
    public float getWorldScale() {
        return this.m_renderer.getWorldScale(this.m_renderer.getDisplayZoomF());
    }
    
    public float getCenterWorldX() {
        return this.m_renderer.getCenterWorldX();
    }
    
    public float getCenterWorldY() {
        return this.m_renderer.getCenterWorldY();
    }
    
    public float uiToWorldX(final float n, final float n2) {
        if (!this.m_worldMap.hasData() && !this.m_worldMap.hasImages()) {
            return 0.0f;
        }
        return this.uiToWorldX(n, n2, this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldX(), this.m_renderer.getCenterWorldY());
    }
    
    public float uiToWorldY(final float n, final float n2) {
        if (!this.m_worldMap.hasData() && !this.m_worldMap.hasImages()) {
            return 0.0f;
        }
        return this.uiToWorldY(n, n2, this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldY(), this.m_renderer.getCenterWorldY());
    }
    
    public float worldToUIX(final float n, final float n2) {
        if (!this.m_worldMap.hasData() && !this.m_worldMap.hasImages()) {
            return 0.0f;
        }
        return this.worldToUIX(n, n2, this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldX(), this.m_renderer.getCenterWorldY(), this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
    }
    
    public float worldToUIY(final float n, final float n2) {
        if (!this.m_worldMap.hasData() && !this.m_worldMap.hasImages()) {
            return 0.0f;
        }
        return this.worldToUIY(n, n2, this.m_renderer.getDisplayZoomF(), this.m_renderer.getCenterWorldX(), this.m_renderer.getCenterWorldY(), this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix());
    }
    
    public void centerOn(final float n, final float n2) {
        if (!this.m_worldMap.hasData() && !this.m_worldMap.hasImages()) {
            return;
        }
        this.m_renderer.centerOn(n, n2);
    }
    
    public void moveView(final float n, final float n2) {
        if (!this.m_worldMap.hasData() && !this.m_worldMap.hasImages()) {
            return;
        }
        this.m_renderer.moveView((int)n, (int)n2);
    }
    
    public void zoomAt(final float n, final float n2, final float n3) {
        if (!this.m_worldMap.hasData() && !this.m_worldMap.hasImages()) {
            return;
        }
        this.m_renderer.zoomAt((int)n, (int)n2, -(int)n3);
    }
    
    public void setZoom(final float zoom) {
        this.m_renderer.setZoom(zoom);
    }
    
    public void resetView() {
        if (!this.m_worldMap.hasData() && !this.m_worldMap.hasImages()) {
            return;
        }
        this.m_renderer.resetView();
    }
    
    public float mouseToWorldX() {
        return this.uiToWorldX((float)(Mouse.getXA() - this.m_ui.getAbsoluteX().intValue()), (float)(Mouse.getYA() - this.m_ui.getAbsoluteY().intValue()));
    }
    
    public float mouseToWorldY() {
        return this.uiToWorldY((float)(Mouse.getXA() - this.m_ui.getAbsoluteX().intValue()), (float)(Mouse.getYA() - this.m_ui.getAbsoluteY().intValue()));
    }
    
    public void setBackgroundRGBA(final float n, final float n2, final float n3, final float n4) {
        this.m_ui.m_color.init(n, n2, n3, n4);
    }
    
    public void setDropShadowWidth(final int dropShadowWidth) {
        this.m_ui.m_renderer.setDropShadowWidth(dropShadowWidth);
    }
    
    public void setUnvisitedRGBA(final float n, final float n2, final float n3, final float n4) {
        WorldMapVisited.getInstance().setUnvisitedRGBA(n, n2, n3, n4);
    }
    
    public void setUnvisitedGridRGBA(final float n, final float n2, final float n3, final float n4) {
        WorldMapVisited.getInstance().setUnvisitedGridRGBA(n, n2, n3, n4);
    }
    
    public int getOptionCount() {
        return this.m_renderer.getOptionCount();
    }
    
    public ConfigOption getOptionByIndex(final int n) {
        return this.m_renderer.getOptionByIndex(n);
    }
    
    public void setBoolean(final String s, final boolean b) {
        this.m_renderer.setBoolean(s, b);
    }
    
    public boolean getBoolean(final String s) {
        return this.m_renderer.getBoolean(s);
    }
    
    public void setDouble(final String s, final double n) {
        this.m_renderer.setDouble(s, n);
    }
    
    public double getDouble(final String s, final double n) {
        return this.m_renderer.getDouble(s, n);
    }
}
