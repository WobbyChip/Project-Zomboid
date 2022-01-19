// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import zombie.iso.IsoMetaGrid;
import zombie.iso.RoomDef;
import zombie.iso.IsoWorld;
import zombie.worldMap.editor.WorldMapEditorState;
import zombie.worldMap.symbols.WorldMapSymbolsV1;
import zombie.worldMap.styles.WorldMapStyleV1;
import zombie.worldMap.markers.WorldMapMarkersV1;
import zombie.worldMap.markers.WorldMapGridSquareMarker;
import zombie.worldMap.symbols.MapSymbolDefinitions;
import zombie.Lua.LuaManager;
import zombie.input.GameKeyboard;
import java.util.Iterator;
import zombie.core.SpriteRenderer;
import zombie.debug.DebugOptions;
import zombie.core.Core;
import zombie.ui.UIFont;
import zombie.ui.TextManager;
import zombie.core.math.PZMath;
import zombie.characters.IsoPlayer;
import zombie.core.textures.Texture;
import zombie.inventory.types.MapItem;
import se.krka.kahlua.vm.KahluaTable;
import zombie.iso.BuildingDef;
import zombie.worldMap.styles.WorldMapStyleLayer;
import zombie.worldMap.symbols.WorldMapSymbols;
import zombie.worldMap.markers.WorldMapMarkers;
import zombie.worldMap.styles.WorldMapStyle;
import java.util.ArrayList;
import zombie.ui.UIElement;

public class UIWorldMap extends UIElement
{
    static final ArrayList<WorldMapFeature> s_tempFeatures;
    protected final WorldMap m_worldMap;
    protected final WorldMapStyle m_style;
    protected final WorldMapRenderer m_renderer;
    protected final WorldMapMarkers m_markers;
    protected WorldMapSymbols m_symbols;
    protected final WorldMapStyleLayer.RGBAf m_color;
    protected final UIWorldMapV1 m_APIv1;
    private boolean m_dataWasReady;
    private final ArrayList<BuildingDef> m_buildingsWithoutFeatures;
    private boolean m_bBuildingsWithoutFeatures;
    
    public UIWorldMap(final KahluaTable kahluaTable) {
        super(kahluaTable);
        this.m_worldMap = new WorldMap();
        this.m_style = new WorldMapStyle();
        this.m_renderer = new WorldMapRenderer();
        this.m_markers = new WorldMapMarkers();
        this.m_symbols = null;
        this.m_color = new WorldMapStyleLayer.RGBAf().init(0.85882354f, 0.84313726f, 0.7529412f, 1.0f);
        this.m_APIv1 = new UIWorldMapV1(this);
        this.m_dataWasReady = false;
        this.m_buildingsWithoutFeatures = new ArrayList<BuildingDef>();
        this.m_bBuildingsWithoutFeatures = false;
    }
    
    public UIWorldMapV1 getAPI() {
        return this.m_APIv1;
    }
    
    public UIWorldMapV1 getAPIv1() {
        return this.m_APIv1;
    }
    
    protected void setMapItem(final MapItem mapItem) {
        this.m_symbols = mapItem.getSymbols();
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        if (this.Parent != null && this.Parent.getMaxDrawHeight() != -1.0 && this.Parent.getMaxDrawHeight() <= this.getY()) {
            return;
        }
        this.DrawTextureScaledColor(null, 0.0, 0.0, this.getWidth(), this.getHeight(), (double)this.m_color.r, (double)this.m_color.g, (double)this.m_color.b, (double)this.m_color.a);
        if (!this.m_worldMap.hasData()) {}
        this.setStencilRect(0.0, 0.0, this.getWidth(), this.getHeight());
        this.m_renderer.setMap(this.m_worldMap, this.getAbsoluteX().intValue(), this.getAbsoluteY().intValue(), this.getWidth().intValue(), this.getHeight().intValue());
        this.m_renderer.updateView();
        final float displayZoomF = this.m_renderer.getDisplayZoomF();
        final float centerWorldX = this.m_renderer.getCenterWorldX();
        final float centerWorldY = this.m_renderer.getCenterWorldY();
        this.m_APIv1.getWorldScale(displayZoomF);
        if (this.m_renderer.getBoolean("HideUnvisited") && WorldMapVisited.getInstance() != null) {
            this.m_renderer.setVisited(WorldMapVisited.getInstance());
        }
        else {
            this.m_renderer.setVisited(null);
        }
        this.m_renderer.render(this);
        if (this.m_renderer.getBoolean("Symbols")) {
            this.m_symbols.render(this);
        }
        this.m_markers.render(this);
        if (this.m_renderer.getBoolean("Players") && displayZoomF < 20.0f) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null) {
                    if (!isoPlayer.isDead()) {
                        float n = isoPlayer.x;
                        float n2 = isoPlayer.y;
                        if (isoPlayer.getVehicle() != null) {
                            n = isoPlayer.getVehicle().getX();
                            n2 = isoPlayer.getVehicle().getY();
                        }
                        this.DrawTextureScaledColor(null, PZMath.floor(this.m_APIv1.worldToUIX(n, n2, displayZoomF, centerWorldX, centerWorldY, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix())) - 3.0, PZMath.floor(this.m_APIv1.worldToUIY(n, n2, displayZoomF, centerWorldX, centerWorldY, this.m_renderer.getProjectionMatrix(), this.m_renderer.getModelViewMatrix())) - 3.0, 6.0, 6.0, 1.0, 0.0, 0.0, 1.0);
                    }
                }
            }
        }
        final int fontHeight = TextManager.instance.getFontHeight(UIFont.Small);
        if (Core.bDebug && this.m_renderer.getBoolean("DebugInfo")) {
            this.DrawTextureScaledColor(null, 0.0, 0.0, 200.0, fontHeight * 4.0, 1.0, 1.0, 1.0, 1.0);
            final float mouseToWorldX = this.m_APIv1.mouseToWorldX();
            final float mouseToWorldY = this.m_APIv1.mouseToWorldY();
            final double n3 = 0.0;
            final double n4 = 0.0;
            final double n5 = 0.0;
            final double n6 = 1.0;
            final int n7 = 0;
            this.DrawText(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, (int)mouseToWorldX, (int)mouseToWorldY), 0.0, (double)n7, n3, n4, n5, n6);
            final int n8 = n7 + fontHeight;
            this.DrawText(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, (int)(mouseToWorldX / 300.0f), (int)(mouseToWorldY / 300.0f)), 0.0, (double)fontHeight, n3, n4, n5, n6);
            final int n9 = n8 + fontHeight;
            this.DrawText(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.m_renderer.getDisplayZoomF()), 0.0, (double)n9, n3, n4, n5, n6);
            this.DrawText(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, this.m_renderer.getWorldScale(this.m_renderer.getZoomF())), 0.0, (double)(n9 + fontHeight), n3, n4, n5, n6);
        }
        this.clearStencilRect();
        this.repaintStencilRect(0.0, 0.0, this.width, this.height);
        if (Core.bDebug && DebugOptions.instance.UIRenderOutline.getValue()) {
            final Double value = -this.getXScroll();
            final Double value2 = -this.getYScroll();
            final double n10 = this.isMouseOver() ? 0.0 : 1.0;
            this.DrawTextureScaledColor(null, value, value2, 1.0, (double)this.height, n10, 1.0, 1.0, 0.5);
            this.DrawTextureScaledColor(null, value + 1.0, value2, this.width - 2.0, 1.0, n10, 1.0, 1.0, 0.5);
            this.DrawTextureScaledColor(null, value + this.width - 1.0, value2, 1.0, (double)this.height, n10, 1.0, 1.0, 0.5);
            this.DrawTextureScaledColor(null, value + 1.0, value2 + this.height - 1.0, this.width - 2.0, 1.0, n10, 1.0, 1.0, 0.5);
        }
        if (Core.bDebug && this.m_renderer.getBoolean("HitTest")) {
            final float mouseToWorldX2 = this.m_APIv1.mouseToWorldX();
            final float mouseToWorldY2 = this.m_APIv1.mouseToWorldY();
            UIWorldMap.s_tempFeatures.clear();
            for (final WorldMapData worldMapData : this.m_worldMap.m_data) {
                if (!worldMapData.isReady()) {
                    continue;
                }
                worldMapData.hitTest(mouseToWorldX2, mouseToWorldY2, UIWorldMap.s_tempFeatures);
            }
            if (!UIWorldMap.s_tempFeatures.isEmpty()) {
                final WorldMapFeature worldMapFeature = UIWorldMap.s_tempFeatures.get(UIWorldMap.s_tempFeatures.size() - 1);
                final int n11 = worldMapFeature.m_cell.m_x * 300;
                final int n12 = worldMapFeature.m_cell.m_y * 300;
                final int intValue = this.getAbsoluteX().intValue();
                final int intValue2 = this.getAbsoluteY().intValue();
                final WorldMapPoints worldMapPoints = worldMapFeature.m_geometries.get(0).m_points.get(0);
                for (int j = 0; j < worldMapPoints.numPoints(); ++j) {
                    final int x = worldMapPoints.getX(j);
                    final int y = worldMapPoints.getY(j);
                    final int x2 = worldMapPoints.getX((j + 1) % worldMapPoints.numPoints());
                    final int y2 = worldMapPoints.getY((j + 1) % worldMapPoints.numPoints());
                    SpriteRenderer.instance.renderline(null, intValue + (int)this.m_APIv1.worldToUIX((float)(n11 + x), (float)(n12 + y)), intValue2 + (int)this.m_APIv1.worldToUIY((float)(n11 + x), (float)(n12 + y)), intValue + (int)this.m_APIv1.worldToUIX((float)(n11 + x2), (float)(n12 + y2)), intValue2 + (int)this.m_APIv1.worldToUIY((float)(n11 + x2), (float)(n12 + y2)), 1.0f, 0.0f, 0.0f, 1.0f);
                }
            }
        }
        if (Core.bDebug && this.m_renderer.getBoolean("BuildingsWithoutFeatures") && !this.m_renderer.getBoolean("Isometric")) {
            this.renderBuildingsWithoutFeatures();
        }
        super.render();
    }
    
    @Override
    public void update() {
        super.update();
    }
    
    @Override
    public Boolean onMouseDown(final double n, final double n2) {
        if (GameKeyboard.isKeyDown(42)) {
            this.m_renderer.resetView();
        }
        return super.onMouseDown(n, n2);
    }
    
    @Override
    public Boolean onMouseUp(final double n, final double n2) {
        return super.onMouseUp(n, n2);
    }
    
    @Override
    public void onMouseUpOutside(final double n, final double n2) {
        super.onMouseUpOutside(n, n2);
    }
    
    @Override
    public Boolean onMouseMove(final double n, final double n2) {
        return super.onMouseMove(n, n2);
    }
    
    @Override
    public Boolean onMouseWheel(final double n) {
        return super.onMouseWheel(n);
    }
    
    public static void setExposed(final LuaManager.Exposer exposed) {
        exposed.setExposed(MapItem.class);
        exposed.setExposed(MapSymbolDefinitions.class);
        exposed.setExposed(MapSymbolDefinitions.MapSymbolDefinition.class);
        exposed.setExposed(UIWorldMap.class);
        exposed.setExposed(UIWorldMapV1.class);
        exposed.setExposed(WorldMapGridSquareMarker.class);
        exposed.setExposed(WorldMapMarkers.class);
        exposed.setExposed(WorldMapRenderer.WorldMapBooleanOption.class);
        exposed.setExposed(WorldMapRenderer.WorldMapDoubleOption.class);
        exposed.setExposed(WorldMapVisited.class);
        WorldMapMarkersV1.setExposed(exposed);
        WorldMapStyleV1.setExposed(exposed);
        WorldMapSymbolsV1.setExposed(exposed);
        exposed.setExposed(WorldMapEditorState.class);
        exposed.setExposed(WorldMapSettings.class);
    }
    
    private void renderBuildingsWithoutFeatures() {
        if (this.m_bBuildingsWithoutFeatures) {
            final Iterator<BuildingDef> iterator = this.m_buildingsWithoutFeatures.iterator();
            while (iterator.hasNext()) {
                this.debugRenderBuilding(iterator.next(), 1.0f, 0.0f, 0.0f, 1.0f);
            }
            return;
        }
        this.m_bBuildingsWithoutFeatures = true;
        this.m_buildingsWithoutFeatures.clear();
        final IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
        for (int i = 0; i < metaGrid.Buildings.size(); ++i) {
            final BuildingDef e = metaGrid.Buildings.get(i);
            boolean b = false;
            for (int j = 0; j < e.rooms.size(); ++j) {
                final RoomDef roomDef = e.rooms.get(j);
                if (roomDef.level <= 0) {
                    final ArrayList<RoomDef.RoomRect> rects = roomDef.getRects();
                    for (int k = 0; k < rects.size(); ++k) {
                        final RoomDef.RoomRect roomRect = rects.get(k);
                        UIWorldMap.s_tempFeatures.clear();
                        for (final WorldMapData worldMapData : this.m_worldMap.m_data) {
                            if (!worldMapData.isReady()) {
                                continue;
                            }
                            worldMapData.hitTest(roomRect.x + roomRect.w / 2.0f, roomRect.y + roomRect.h / 2.0f, UIWorldMap.s_tempFeatures);
                        }
                        if (!UIWorldMap.s_tempFeatures.isEmpty()) {
                            b = true;
                            break;
                        }
                    }
                    if (b) {
                        break;
                    }
                }
            }
            if (!b) {
                this.m_buildingsWithoutFeatures.add(e);
            }
        }
    }
    
    private void debugRenderBuilding(final BuildingDef buildingDef, final float n, final float n2, final float n3, final float n4) {
        for (int i = 0; i < buildingDef.rooms.size(); ++i) {
            final ArrayList<RoomDef.RoomRect> rects = buildingDef.rooms.get(i).getRects();
            for (int j = 0; j < rects.size(); ++j) {
                final RoomDef.RoomRect roomRect = rects.get(j);
                final float worldToUIX = this.m_APIv1.worldToUIX((float)roomRect.x, (float)roomRect.y);
                final float worldToUIY = this.m_APIv1.worldToUIY((float)roomRect.x, (float)roomRect.y);
                this.DrawTextureScaledColor(null, (double)worldToUIX, (double)worldToUIY, (double)(this.m_APIv1.worldToUIX((float)roomRect.getX2(), (float)roomRect.getY2()) - worldToUIX), (double)(this.m_APIv1.worldToUIY((float)roomRect.getX2(), (float)roomRect.getY2()) - worldToUIY), (double)n, (double)n2, (double)n3, (double)n4);
            }
        }
    }
    
    static {
        s_tempFeatures = new ArrayList<WorldMapFeature>();
    }
}
