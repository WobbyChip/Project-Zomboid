// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion;

import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.config.ConfigFile;
import zombie.config.BooleanConfigOption;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import se.krka.kahlua.vm.KahluaTable;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.LotHeader;
import zombie.iso.areas.isoregion.data.DataRoot;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.core.Colors;
import zombie.core.utils.Bits;
import zombie.iso.RoomDef;
import zombie.iso.BuildingDef;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.iso.IsoWorld;
import zombie.iso.areas.isoregion.regions.IsoChunkRegion;
import zombie.MapCollisionData;
import zombie.ui.UIElement;
import zombie.core.Core;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.textures.Texture;
import zombie.ui.UIFont;
import zombie.ui.TextManager;
import zombie.core.SpriteRenderer;
import zombie.core.Color;
import zombie.config.ConfigOption;
import java.util.ArrayList;
import java.util.HashSet;
import zombie.iso.areas.isoregion.data.DataChunk;
import java.util.List;

public class IsoRegionsRenderer
{
    private final List<DataChunk> tempChunkList;
    private final List<String> debugLines;
    private float xPos;
    private float yPos;
    private float offx;
    private float offy;
    private float zoom;
    private float draww;
    private float drawh;
    private boolean hasSelected;
    private boolean validSelection;
    private int selectedX;
    private int selectedY;
    private int selectedZ;
    private final HashSet<Integer> drawnCells;
    private boolean editSquareInRange;
    private int editSquareX;
    private int editSquareY;
    private final ArrayList<ConfigOption> editOptions;
    private boolean EditingEnabled;
    private final BooleanDebugOption EditWallN;
    private final BooleanDebugOption EditWallW;
    private final BooleanDebugOption EditDoorN;
    private final BooleanDebugOption EditDoorW;
    private final BooleanDebugOption EditFloor;
    private final ArrayList<ConfigOption> zLevelOptions;
    private final BooleanDebugOption zLevelPlayer;
    private final BooleanDebugOption zLevel0;
    private final BooleanDebugOption zLevel1;
    private final BooleanDebugOption zLevel2;
    private final BooleanDebugOption zLevel3;
    private final BooleanDebugOption zLevel4;
    private final BooleanDebugOption zLevel5;
    private final BooleanDebugOption zLevel6;
    private final BooleanDebugOption zLevel7;
    private static final int VERSION = 1;
    private final ArrayList<ConfigOption> options;
    private final BooleanDebugOption CellGrid;
    private final BooleanDebugOption MetaGridBuildings;
    private final BooleanDebugOption IsoRegionRender;
    private final BooleanDebugOption IsoRegionRenderChunks;
    private final BooleanDebugOption IsoRegionRenderChunksPlus;
    
    public IsoRegionsRenderer() {
        this.tempChunkList = new ArrayList<DataChunk>();
        this.debugLines = new ArrayList<String>();
        this.hasSelected = false;
        this.validSelection = false;
        this.drawnCells = new HashSet<Integer>();
        this.editSquareInRange = false;
        this.editOptions = new ArrayList<ConfigOption>();
        this.EditingEnabled = false;
        this.EditWallN = new BooleanDebugOption(this.editOptions, "Edit.WallN", false);
        this.EditWallW = new BooleanDebugOption(this.editOptions, "Edit.WallW", false);
        this.EditDoorN = new BooleanDebugOption(this.editOptions, "Edit.DoorN", false);
        this.EditDoorW = new BooleanDebugOption(this.editOptions, "Edit.DoorW", false);
        this.EditFloor = new BooleanDebugOption(this.editOptions, "Edit.Floor", false);
        this.zLevelOptions = new ArrayList<ConfigOption>();
        this.zLevelPlayer = new BooleanDebugOption(this.zLevelOptions, "zLevel.Player", true);
        this.zLevel0 = new BooleanDebugOption(this.zLevelOptions, "zLevel.0", false, 0);
        this.zLevel1 = new BooleanDebugOption(this.zLevelOptions, "zLevel.1", false, 1);
        this.zLevel2 = new BooleanDebugOption(this.zLevelOptions, "zLevel.2", false, 2);
        this.zLevel3 = new BooleanDebugOption(this.zLevelOptions, "zLevel.3", false, 3);
        this.zLevel4 = new BooleanDebugOption(this.zLevelOptions, "zLevel.4", false, 4);
        this.zLevel5 = new BooleanDebugOption(this.zLevelOptions, "zLevel.5", false, 5);
        this.zLevel6 = new BooleanDebugOption(this.zLevelOptions, "zLevel.6", false, 6);
        this.zLevel7 = new BooleanDebugOption(this.zLevelOptions, "zLevel.7", false, 7);
        this.options = new ArrayList<ConfigOption>();
        this.CellGrid = new BooleanDebugOption(this.options, "CellGrid", true);
        this.MetaGridBuildings = new BooleanDebugOption(this.options, "MetaGrid.Buildings", true);
        this.IsoRegionRender = new BooleanDebugOption(this.options, "IsoRegion.Render", true);
        this.IsoRegionRenderChunks = new BooleanDebugOption(this.options, "IsoRegion.RenderChunks", false);
        this.IsoRegionRenderChunksPlus = new BooleanDebugOption(this.options, "IsoRegion.RenderChunksPlus", false);
    }
    
    public float worldToScreenX(float n) {
        n -= this.xPos;
        n *= this.zoom;
        n += this.offx;
        n += this.draww / 2.0f;
        return n;
    }
    
    public float worldToScreenY(float n) {
        n -= this.yPos;
        n *= this.zoom;
        n += this.offy;
        n += this.drawh / 2.0f;
        return n;
    }
    
    public float uiToWorldX(float n) {
        n -= this.draww / 2.0f;
        n /= this.zoom;
        n += this.xPos;
        return n;
    }
    
    public float uiToWorldY(float n) {
        n -= this.drawh / 2.0f;
        n /= this.zoom;
        n += this.yPos;
        return n;
    }
    
    public void renderStringUI(final float n, final float n2, final String s, final Color color) {
        this.renderStringUI(n, n2, s, color.r, color.g, color.b, color.a);
    }
    
    public void renderStringUI(final float n, final float n2, final String s, final double n3, final double n4, final double n5, final double n6) {
        final float n7 = this.offx + n;
        final float n8 = this.offy + n2;
        SpriteRenderer.instance.render(null, n7 - 2.0f, n8 - 2.0f, (float)(TextManager.instance.MeasureStringX(UIFont.Small, s) + 4), (float)(TextManager.instance.font.getLineHeight() + 4), 0.0f, 0.0f, 0.0f, 0.75f, null);
        TextManager.instance.DrawString(n7, n8, s, n3, n4, n5, n6);
    }
    
    public void renderString(final float n, final float n2, final String s, final double n3, final double n4, final double n5, final double n6) {
        final float worldToScreenX = this.worldToScreenX(n);
        final float worldToScreenY = this.worldToScreenY(n2);
        SpriteRenderer.instance.render(null, worldToScreenX - 2.0f, worldToScreenY - 2.0f, (float)(TextManager.instance.MeasureStringX(UIFont.Small, s) + 4), (float)(TextManager.instance.font.getLineHeight() + 4), 0.0f, 0.0f, 0.0f, 0.75f, null);
        TextManager.instance.DrawString(worldToScreenX, worldToScreenY, s, n3, n4, n5, n6);
    }
    
    public void renderRect(final float n, final float n2, float n3, float n4, final float n5, final float n6, final float n7, final float n8) {
        final float worldToScreenX = this.worldToScreenX(n);
        final float worldToScreenY = this.worldToScreenY(n2);
        final float worldToScreenX2 = this.worldToScreenX(n + n3);
        final float worldToScreenY2 = this.worldToScreenY(n2 + n4);
        n3 = worldToScreenX2 - worldToScreenX;
        n4 = worldToScreenY2 - worldToScreenY;
        if (worldToScreenX >= this.offx + this.draww || worldToScreenX2 < this.offx || worldToScreenY >= this.offy + this.drawh || worldToScreenY2 < this.offy) {
            return;
        }
        SpriteRenderer.instance.render(null, worldToScreenX, worldToScreenY, n3, n4, n5, n6, n7, n8, null);
    }
    
    public void renderLine(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        final float worldToScreenX = this.worldToScreenX(n);
        final float worldToScreenY = this.worldToScreenY(n2);
        final float worldToScreenX2 = this.worldToScreenX(n3);
        final float worldToScreenY2 = this.worldToScreenY(n4);
        if ((worldToScreenX >= Core.getInstance().getScreenWidth() && worldToScreenX2 >= Core.getInstance().getScreenWidth()) || (worldToScreenY >= Core.getInstance().getScreenHeight() && worldToScreenY2 >= Core.getInstance().getScreenHeight()) || (worldToScreenX < 0.0f && worldToScreenX2 < 0.0f) || (worldToScreenY < 0.0f && worldToScreenY2 < 0.0f)) {
            return;
        }
        SpriteRenderer.instance.renderline(null, (int)worldToScreenX, (int)worldToScreenY, (int)worldToScreenX2, (int)worldToScreenY2, n5, n6, n7, n8);
    }
    
    public void outlineRect(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        this.renderLine(n, n2, n + n3, n2, n5, n6, n7, n8);
        this.renderLine(n + n3, n2, n + n3, n2 + n4, n5, n6, n7, n8);
        this.renderLine(n, n2 + n4, n + n3, n2 + n4, n5, n6, n7, n8);
        this.renderLine(n, n2, n, n2 + n4, n5, n6, n7, n8);
    }
    
    public void renderCellInfo(final int n, final int n2, final int n3, final int n4, final float f) {
        final float n5 = this.worldToScreenX((float)(n * 300)) + 4.0f;
        final float n6 = this.worldToScreenY((float)(n2 * 300)) + 4.0f;
        String s = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n3, n4);
        if (f > 0.0f) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, String.format(" %.2f", f));
        }
        SpriteRenderer.instance.render(null, n5 - 2.0f, n6 - 2.0f, (float)(TextManager.instance.MeasureStringX(UIFont.Small, s) + 4), (float)(TextManager.instance.font.getLineHeight() + 4), 0.0f, 0.0f, 0.0f, 0.75f, null);
        TextManager.instance.DrawString(n5, n6, s, 1.0, 1.0, 1.0, 1.0);
    }
    
    public void renderZombie(final float n, final float n2, final float n3, final float n4, final float n5) {
        final float n6 = 1.0f / this.zoom + 0.5f;
        this.renderRect(n - n6 / 2.0f, n2 - n6 / 2.0f, n6, n6, n3, n4, n5, 1.0f);
    }
    
    public void renderSquare(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        final float n7 = 1.0f;
        this.renderRect(n, n2, n7, n7, n3, n4, n5, n6);
    }
    
    public void renderEntity(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7) {
        final float n8 = n / this.zoom + 0.5f;
        this.renderRect(n2 - n8 / 2.0f, n3 - n8 / 2.0f, n8, n8, n4, n5, n6, n7);
    }
    
    public void render(final UIElement uiElement, final float n, final float n2, final float n3) {
        synchronized (MapCollisionData.instance.renderLock) {
            this._render(uiElement, n, n2, n3);
        }
    }
    
    private void debugLine(final String s) {
        this.debugLines.add(s);
    }
    
    public void recalcSurroundings() {
        IsoRegions.forceRecalcSurroundingChunks();
    }
    
    public boolean hasChunkRegion(final int n, final int n2) {
        return IsoRegions.getDataRoot().getIsoChunkRegion(n, n2, this.getZLevel()) != null;
    }
    
    public IsoChunkRegion getChunkRegion(final int n, final int n2) {
        return IsoRegions.getDataRoot().getIsoChunkRegion(n, n2, this.getZLevel());
    }
    
    public void setSelected(final int n, final int n2) {
        this.setSelectedWorld((int)this.uiToWorldX((float)n), (int)this.uiToWorldY((float)n2));
    }
    
    public void setSelectedWorld(final int selectedX, final int selectedY) {
        this.selectedZ = this.getZLevel();
        this.hasSelected = true;
        this.selectedX = selectedX;
        this.selectedY = selectedY;
    }
    
    public void unsetSelected() {
        this.hasSelected = false;
    }
    
    public boolean isHasSelected() {
        return this.hasSelected;
    }
    
    private void _render(final UIElement uiElement, final float zoom, final float xPos, final float yPos) {
        this.debugLines.clear();
        this.drawnCells.clear();
        this.draww = (float)uiElement.getWidth().intValue();
        this.drawh = (float)uiElement.getHeight().intValue();
        this.xPos = xPos;
        this.yPos = yPos;
        this.offx = (float)uiElement.getAbsoluteX().intValue();
        this.offy = (float)uiElement.getAbsoluteY().intValue();
        this.zoom = zoom;
        this.debugLine(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, zoom));
        this.debugLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getZLevel()));
        final IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
        final IsoMetaCell[][] grid = metaGrid.Grid;
        final int n = (int)(this.uiToWorldX(0.0f) / 300.0f) - metaGrid.minX;
        final int n2 = (int)(this.uiToWorldY(0.0f) / 300.0f) - metaGrid.minY;
        final int n3 = (int)(this.uiToWorldX(this.draww) / 300.0f) + 1 - metaGrid.minX;
        final int n4 = (int)(this.uiToWorldY(this.drawh) / 300.0f) + 1 - metaGrid.minY;
        final int clamp = PZMath.clamp(n, 0, metaGrid.getWidth() - 1);
        final int clamp2 = PZMath.clamp(n2, 0, metaGrid.getHeight() - 1);
        final int clamp3 = PZMath.clamp(n3, 0, metaGrid.getWidth() - 1);
        final int clamp4 = PZMath.clamp(n4, 0, metaGrid.getHeight() - 1);
        final float max = Math.max(1.0f - zoom / 2.0f, 0.1f);
        IsoChunkRegion isoChunkRegion = null;
        IsoWorldRegion isoWorldRegion = null;
        this.validSelection = false;
        if (this.IsoRegionRender.getValue()) {
            IsoPlayer.getInstance();
            final DataRoot dataRoot = IsoRegions.getDataRoot();
            this.tempChunkList.clear();
            dataRoot.getAllChunks(this.tempChunkList);
            this.debugLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.tempChunkList.size()));
            this.debugLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, dataRoot.regionManager.getChunkRegionCount()));
            this.debugLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, dataRoot.regionManager.getWorldRegionCount()));
            if (this.hasSelected) {
                isoChunkRegion = dataRoot.getIsoChunkRegion(this.selectedX, this.selectedY, this.selectedZ);
                isoWorldRegion = dataRoot.getIsoWorldRegion(this.selectedX, this.selectedY, this.selectedZ);
                if (isoWorldRegion != null && !isoWorldRegion.isEnclosed() && (!this.IsoRegionRenderChunks.getValue() || !this.IsoRegionRenderChunksPlus.getValue())) {
                    isoWorldRegion = null;
                    isoChunkRegion = null;
                }
                if (isoChunkRegion != null) {
                    this.validSelection = true;
                }
            }
            for (int i = 0; i < this.tempChunkList.size(); ++i) {
                final DataChunk dataChunk = this.tempChunkList.get(i);
                final int n5 = dataChunk.getChunkX() * 10;
                final int n6 = dataChunk.getChunkY() * 10;
                if (zoom > 0.1f) {
                    final float worldToScreenX = this.worldToScreenX((float)n5);
                    final float worldToScreenY = this.worldToScreenY((float)n6);
                    final float worldToScreenX2 = this.worldToScreenX((float)(n5 + 10));
                    final float worldToScreenY2 = this.worldToScreenY((float)(n6 + 10));
                    if (worldToScreenX < this.offx + this.draww && worldToScreenX2 >= this.offx && worldToScreenY < this.offy + this.drawh) {
                        if (worldToScreenY2 >= this.offy) {
                            this.renderRect((float)n5, (float)n6, 10.0f, 10.0f, 0.0f, max, 0.0f, 1.0f);
                        }
                    }
                }
            }
        }
        if (this.MetaGridBuildings.getValue()) {
            final float clamp5 = PZMath.clamp(0.3f * (zoom / 5.0f), 0.15f, 0.3f);
            for (int j = clamp; j < clamp3; ++j) {
                for (int k = clamp2; k < clamp4; ++k) {
                    final LotHeader info = grid[j][k].info;
                    if (info != null) {
                        for (int l = 0; l < info.Buildings.size(); ++l) {
                            final BuildingDef buildingDef = info.Buildings.get(l);
                            for (int n7 = 0; n7 < buildingDef.rooms.size(); ++n7) {
                                if (buildingDef.rooms.get(n7).level <= 0) {
                                    final ArrayList<RoomDef.RoomRect> rects = buildingDef.rooms.get(n7).getRects();
                                    for (int index = 0; index < rects.size(); ++index) {
                                        final RoomDef.RoomRect roomRect = rects.get(index);
                                        if (buildingDef.bAlarmed) {
                                            this.renderRect((float)roomRect.getX(), (float)roomRect.getY(), (float)roomRect.getW(), (float)roomRect.getH(), 0.8f * clamp5, 0.8f * clamp5, 0.5f * clamp5, 1.0f);
                                        }
                                        else {
                                            this.renderRect((float)roomRect.getX(), (float)roomRect.getY(), (float)roomRect.getW(), (float)roomRect.getH(), 0.5f * clamp5, 0.5f * clamp5, 0.8f * clamp5, 1.0f);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.IsoRegionRender.getValue()) {
            final int zLevel = this.getZLevel();
            final DataRoot dataRoot2 = IsoRegions.getDataRoot();
            this.tempChunkList.clear();
            dataRoot2.getAllChunks(this.tempChunkList);
            for (int n8 = 0; n8 < this.tempChunkList.size(); ++n8) {
                final DataChunk dataChunk2 = this.tempChunkList.get(n8);
                final int n9 = dataChunk2.getChunkX() * 10;
                final int n10 = dataChunk2.getChunkY() * 10;
                if (zoom <= 0.1f) {
                    final int n11 = n9 / 300;
                    final int n12 = n10 / 300;
                    final int hash = IsoRegions.hash(n11, n12);
                    if (!this.drawnCells.contains(hash)) {
                        this.drawnCells.add(hash);
                        this.renderRect((float)(n11 * 300), (float)(n12 * 300), 300.0f, 300.0f, 0.0f, max, 0.0f, 1.0f);
                    }
                }
                else if (zoom >= 1.0f) {
                    final float worldToScreenX3 = this.worldToScreenX((float)n9);
                    final float worldToScreenY3 = this.worldToScreenY((float)n10);
                    final float worldToScreenX4 = this.worldToScreenX((float)(n9 + 10));
                    final float worldToScreenY4 = this.worldToScreenY((float)(n10 + 10));
                    if (worldToScreenX3 < this.offx + this.draww && worldToScreenX4 >= this.offx && worldToScreenY3 < this.offy + this.drawh) {
                        if (worldToScreenY4 >= this.offy) {
                            for (int n13 = 0; n13 < 10; ++n13) {
                                for (int n14 = 0; n14 < 10; ++n14) {
                                    for (int n15 = (zLevel > 0) ? (zLevel - 1) : zLevel; n15 <= zLevel; ++n15) {
                                        final float n16 = (n15 < zLevel) ? 0.25f : 1.0f;
                                        final byte square = dataChunk2.getSquare(n13, n14, n15);
                                        if (square >= 0) {
                                            final IsoChunkRegion isoChunkRegion2 = dataChunk2.getIsoChunkRegion(n13, n14, n15);
                                            if (isoChunkRegion2 != null) {
                                                if (zoom > 6.0f && this.IsoRegionRenderChunks.getValue() && this.IsoRegionRenderChunksPlus.getValue()) {
                                                    final Color color = isoChunkRegion2.getColor();
                                                    float n17 = 1.0f;
                                                    if (isoChunkRegion != null && isoChunkRegion2 != isoChunkRegion) {
                                                        n17 = 0.25f;
                                                    }
                                                    this.renderSquare((float)(n9 + n13), (float)(n10 + n14), color.r, color.g, color.b, n17 * n16);
                                                }
                                                else {
                                                    final IsoWorldRegion isoWorldRegion2 = isoChunkRegion2.getIsoWorldRegion();
                                                    if (isoWorldRegion2 != null && isoWorldRegion2.isEnclosed()) {
                                                        float n18 = 1.0f;
                                                        Color color2;
                                                        if (this.IsoRegionRenderChunks.getValue()) {
                                                            color2 = isoChunkRegion2.getColor();
                                                            if (isoChunkRegion != null && isoChunkRegion2 != isoChunkRegion) {
                                                                n18 = 0.25f;
                                                            }
                                                        }
                                                        else {
                                                            color2 = isoWorldRegion2.getColor();
                                                            if (isoWorldRegion != null && isoWorldRegion2 != isoWorldRegion) {
                                                                n18 = 0.25f;
                                                            }
                                                        }
                                                        this.renderSquare((float)(n9 + n13), (float)(n10 + n14), color2.r, color2.g, color2.b, n18 * n16);
                                                    }
                                                }
                                            }
                                            if (n15 > 0 && n15 == zLevel) {
                                                final IsoChunkRegion isoChunkRegion3 = dataChunk2.getIsoChunkRegion(n13, n14, n15);
                                                final IsoWorldRegion isoWorldRegion3 = (isoChunkRegion3 != null) ? isoChunkRegion3.getIsoWorldRegion() : null;
                                                if ((isoChunkRegion3 == null || isoWorldRegion3 == null || !isoWorldRegion3.isEnclosed()) && Bits.hasFlags(square, 16)) {
                                                    this.renderSquare((float)(n9 + n13), (float)(n10 + n14), 0.5f, 0.5f, 0.5f, 1.0f);
                                                }
                                            }
                                            if (Bits.hasFlags(square, 1) || Bits.hasFlags(square, 4)) {
                                                this.renderRect((float)(n9 + n13), (float)(n10 + n14), 1.0f, 0.1f, 1.0f, 1.0f, 1.0f, 1.0f * n16);
                                            }
                                            if (Bits.hasFlags(square, 2) || Bits.hasFlags(square, 8)) {
                                                this.renderRect((float)(n9 + n13), (float)(n10 + n14), 0.1f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f * n16);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.CellGrid.getValue()) {
            float max2 = 1.0f;
            if (zoom < 0.1f) {
                max2 = Math.max(zoom / 0.1f, 0.25f);
            }
            for (int n19 = clamp2; n19 <= clamp4; ++n19) {
                this.renderLine((float)(metaGrid.minX * 300), (float)((metaGrid.minY + n19) * 300), (float)((metaGrid.maxX + 1) * 300), (float)((metaGrid.minY + n19) * 300), 1.0f, 1.0f, 1.0f, 0.15f * max2);
                if (zoom > 1.0f) {
                    for (int n20 = 1; n20 < 30; ++n20) {
                        this.renderLine((float)(metaGrid.minX * 300), (float)((metaGrid.minY + n19) * 300 + n20 * 10), (float)((metaGrid.maxX + 1) * 300), (float)((metaGrid.minY + n19) * 300 + n20 * 10), 1.0f, 1.0f, 1.0f, 0.0325f);
                    }
                }
                else if (zoom > 0.15f) {
                    this.renderLine((float)(metaGrid.minX * 300), (float)((metaGrid.minY + n19) * 300 + 100), (float)((metaGrid.maxX + 1) * 300), (float)((metaGrid.minY + n19) * 300 + 100), 1.0f, 1.0f, 1.0f, 0.075f);
                    this.renderLine((float)(metaGrid.minX * 300), (float)((metaGrid.minY + n19) * 300 + 200), (float)((metaGrid.maxX + 1) * 300), (float)((metaGrid.minY + n19) * 300 + 200), 1.0f, 1.0f, 1.0f, 0.075f);
                }
            }
            for (int n21 = clamp; n21 <= clamp3; ++n21) {
                this.renderLine((float)((metaGrid.minX + n21) * 300), (float)(metaGrid.minY * 300), (float)((metaGrid.minX + n21) * 300), (float)((metaGrid.maxY + 1) * 300), 1.0f, 1.0f, 1.0f, 0.15f * max2);
                if (zoom > 1.0f) {
                    for (int n22 = 1; n22 < 30; ++n22) {
                        this.renderLine((float)((metaGrid.minX + n21) * 300 + n22 * 10), (float)(metaGrid.minY * 300), (float)((metaGrid.minX + n21) * 300 + n22 * 10), (float)((metaGrid.maxY + 1) * 300), 1.0f, 1.0f, 1.0f, 0.0325f);
                    }
                }
                else if (zoom > 0.15f) {
                    this.renderLine((float)((metaGrid.minX + n21) * 300 + 100), (float)(metaGrid.minY * 300), (float)((metaGrid.minX + n21) * 300 + 100), (float)((metaGrid.maxY + 1) * 300), 1.0f, 1.0f, 1.0f, 0.075f);
                    this.renderLine((float)((metaGrid.minX + n21) * 300 + 200), (float)(metaGrid.minY * 300), (float)((metaGrid.minX + n21) * 300 + 200), (float)((metaGrid.maxY + 1) * 300), 1.0f, 1.0f, 1.0f, 0.075f);
                }
            }
        }
        for (int n23 = 0; n23 < IsoPlayer.numPlayers; ++n23) {
            final IsoPlayer isoPlayer = IsoPlayer.players[n23];
            if (isoPlayer != null) {
                this.renderZombie(isoPlayer.x, isoPlayer.y, 0.0f, 0.5f, 0.0f);
            }
        }
        if (this.isEditingEnabled()) {
            final float n24 = this.editSquareInRange ? 0.0f : 1.0f;
            final float n25 = this.editSquareInRange ? 1.0f : 0.0f;
            if (this.EditWallN.getValue() || this.EditDoorN.getValue()) {
                this.renderRect((float)this.editSquareX, (float)this.editSquareY, 1.0f, 0.25f, n24, n25, 0.0f, 0.5f);
                this.renderRect((float)this.editSquareX, (float)this.editSquareY, 1.0f, 0.05f, n24, n25, 0.0f, 1.0f);
                this.renderRect((float)this.editSquareX, (float)this.editSquareY, 0.05f, 0.25f, n24, n25, 0.0f, 1.0f);
                this.renderRect((float)this.editSquareX, this.editSquareY + 0.2f, 1.0f, 0.05f, n24, n25, 0.0f, 1.0f);
                this.renderRect(this.editSquareX + 0.95f, (float)this.editSquareY, 0.05f, 0.25f, n24, n25, 0.0f, 1.0f);
            }
            else if (this.EditWallW.getValue() || this.EditDoorW.getValue()) {
                this.renderRect((float)this.editSquareX, (float)this.editSquareY, 0.25f, 1.0f, n24, n25, 0.0f, 0.5f);
                this.renderRect((float)this.editSquareX, (float)this.editSquareY, 0.25f, 0.05f, n24, n25, 0.0f, 1.0f);
                this.renderRect((float)this.editSquareX, (float)this.editSquareY, 0.05f, 1.0f, n24, n25, 0.0f, 1.0f);
                this.renderRect((float)this.editSquareX, this.editSquareY + 0.95f, 0.25f, 0.05f, n24, n25, 0.0f, 1.0f);
                this.renderRect(this.editSquareX + 0.2f, (float)this.editSquareY, 0.05f, 1.0f, n24, n25, 0.0f, 1.0f);
            }
            else {
                this.renderRect((float)this.editSquareX, (float)this.editSquareY, 1.0f, 1.0f, n24, n25, 0.0f, 0.5f);
                this.renderRect((float)this.editSquareX, (float)this.editSquareY, 1.0f, 0.05f, n24, n25, 0.0f, 1.0f);
                this.renderRect((float)this.editSquareX, (float)this.editSquareY, 0.05f, 1.0f, n24, n25, 0.0f, 1.0f);
                this.renderRect((float)this.editSquareX, this.editSquareY + 0.95f, 1.0f, 0.05f, n24, n25, 0.0f, 1.0f);
                this.renderRect(this.editSquareX + 0.95f, (float)this.editSquareY, 0.05f, 1.0f, n24, n25, 0.0f, 1.0f);
            }
        }
        if (isoChunkRegion != null) {
            this.debugLine("- ChunkRegion -");
            this.debugLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, isoChunkRegion.getID()));
            this.debugLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, isoChunkRegion.getSquareSize()));
            this.debugLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, isoChunkRegion.getRoofCnt()));
            this.debugLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, isoChunkRegion.getNeighborCount()));
            this.debugLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, isoChunkRegion.getConnectedNeighbors().size()));
            this.debugLine(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, isoChunkRegion.getIsEnclosed()));
        }
        if (isoWorldRegion != null) {
            this.debugLine("- WorldRegion -");
            this.debugLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, isoWorldRegion.getID()));
            this.debugLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, isoWorldRegion.getSquareSize()));
            this.debugLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, isoWorldRegion.getRoofCnt()));
            this.debugLine(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, isoWorldRegion.isFullyRoofed()));
            this.debugLine(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, isoWorldRegion.getRoofedPercentage()));
            this.debugLine(invokedynamic(makeConcatWithConstants:(Z)Ljava/lang/String;, isoWorldRegion.isEnclosed()));
            this.debugLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, isoWorldRegion.getNeighbors().size()));
            this.debugLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, isoWorldRegion.size()));
        }
        int n26 = 15;
        for (int n27 = 0; n27 < this.debugLines.size(); ++n27) {
            this.renderStringUI(10.0f, (float)n26, this.debugLines.get(n27), Colors.CornFlowerBlue);
            n26 += 18;
        }
    }
    
    public void setEditSquareCoord(final int editSquareX, final int editSquareY) {
        this.editSquareX = editSquareX;
        this.editSquareY = editSquareY;
        this.editSquareInRange = false;
        if (this.editCoordInRange(editSquareX, editSquareY)) {
            this.editSquareInRange = true;
        }
    }
    
    private boolean editCoordInRange(final int n, final int n2) {
        return IsoWorld.instance.getCell().getGridSquare(n, n2, 0) != null;
    }
    
    public void editSquare(final int n, final int n2) {
        if (this.isEditingEnabled()) {
            final int zLevel = this.getZLevel();
            IsoGridSquare isoGridSquare = IsoWorld.instance.getCell().getGridSquare(n, n2, zLevel);
            final byte squareFlags = IsoRegions.getDataRoot().getSquareFlags(n, n2, zLevel);
            if (this.editCoordInRange(n, n2)) {
                if (isoGridSquare == null) {
                    isoGridSquare = IsoWorld.instance.getCell().createNewGridSquare(n, n2, zLevel, true);
                    if (isoGridSquare == null) {
                        return;
                    }
                }
                this.editSquareInRange = true;
                for (int i = 0; i < this.editOptions.size(); ++i) {
                    final BooleanDebugOption booleanDebugOption = this.editOptions.get(i);
                    if (booleanDebugOption.getValue()) {
                        final String name = booleanDebugOption.getName();
                        switch (name) {
                            case "Edit.WallW":
                            case "Edit.WallN": {
                                IsoThumpable isoThumpable;
                                if (booleanDebugOption.getName().equals("Edit.WallN")) {
                                    if (squareFlags > 0 && Bits.hasFlags(squareFlags, 1)) {
                                        return;
                                    }
                                    isoThumpable = new IsoThumpable(IsoWorld.instance.getCell(), isoGridSquare, "walls_exterior_wooden_01_25", true, null);
                                }
                                else {
                                    if (squareFlags > 0 && Bits.hasFlags(squareFlags, 2)) {
                                        return;
                                    }
                                    isoThumpable = new IsoThumpable(IsoWorld.instance.getCell(), isoGridSquare, "walls_exterior_wooden_01_24", true, null);
                                }
                                isoThumpable.setMaxHealth(100);
                                isoThumpable.setName("Wall Debug");
                                isoThumpable.setBreakSound("BreakObject");
                                isoGridSquare.AddSpecialObject(isoThumpable);
                                isoGridSquare.RecalcAllWithNeighbours(true);
                                isoThumpable.transmitCompleteItemToServer();
                                if (isoGridSquare.getZone() != null) {
                                    isoGridSquare.getZone().setHaveConstruction(true);
                                }
                                break;
                            }
                            case "Edit.DoorW":
                            case "Edit.DoorN": {
                                IsoThumpable isoThumpable2;
                                if (booleanDebugOption.getName().equals("Edit.DoorN")) {
                                    if (squareFlags > 0 && Bits.hasFlags(squareFlags, 1)) {
                                        return;
                                    }
                                    isoThumpable2 = new IsoThumpable(IsoWorld.instance.getCell(), isoGridSquare, "walls_exterior_wooden_01_35", true, null);
                                }
                                else {
                                    if (squareFlags > 0 && Bits.hasFlags(squareFlags, 2)) {
                                        return;
                                    }
                                    isoThumpable2 = new IsoThumpable(IsoWorld.instance.getCell(), isoGridSquare, "walls_exterior_wooden_01_34", true, null);
                                }
                                isoThumpable2.setMaxHealth(100);
                                isoThumpable2.setName("Door Frame Debug");
                                isoThumpable2.setBreakSound("BreakObject");
                                isoGridSquare.AddSpecialObject(isoThumpable2);
                                isoGridSquare.RecalcAllWithNeighbours(true);
                                isoThumpable2.transmitCompleteItemToServer();
                                if (isoGridSquare.getZone() != null) {
                                    isoGridSquare.getZone().setHaveConstruction(true);
                                }
                                break;
                            }
                            case "Edit.Floor": {
                                if (squareFlags > 0 && Bits.hasFlags(squareFlags, 16)) {
                                    return;
                                }
                                if (zLevel == 0) {
                                    return;
                                }
                                isoGridSquare.addFloor("carpentry_02_56");
                                if (isoGridSquare.getZone() != null) {
                                    isoGridSquare.getZone().setHaveConstruction(true);
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
            else {
                this.editSquareInRange = false;
            }
        }
    }
    
    public boolean isEditingEnabled() {
        return this.EditingEnabled;
    }
    
    public void editRotate() {
        if (this.EditWallN.getValue()) {
            this.EditWallN.setValue(false);
            this.EditWallW.setValue(true);
        }
        else if (this.EditWallW.getValue()) {
            this.EditWallW.setValue(false);
            this.EditWallN.setValue(true);
        }
        if (this.EditDoorN.getValue()) {
            this.EditDoorN.setValue(false);
            this.EditDoorW.setValue(true);
        }
        else if (this.EditDoorW.getValue()) {
            this.EditDoorW.setValue(false);
            this.EditDoorN.setValue(true);
        }
    }
    
    public ConfigOption getEditOptionByName(final String anObject) {
        for (int i = 0; i < this.editOptions.size(); ++i) {
            final ConfigOption configOption = this.editOptions.get(i);
            if (configOption.getName().equals(anObject)) {
                return configOption;
            }
        }
        return null;
    }
    
    public int getEditOptionCount() {
        return this.editOptions.size();
    }
    
    public ConfigOption getEditOptionByIndex(final int index) {
        return this.editOptions.get(index);
    }
    
    public void setEditOption(final int n, final boolean b) {
        for (int i = 0; i < this.editOptions.size(); ++i) {
            final BooleanDebugOption booleanDebugOption = this.editOptions.get(i);
            if (i != n) {
                booleanDebugOption.setValue(false);
            }
            else {
                booleanDebugOption.setValue(b);
                this.EditingEnabled = b;
            }
        }
    }
    
    public int getZLevel() {
        if (this.zLevelPlayer.getValue()) {
            return (int)IsoPlayer.getInstance().getZ();
        }
        for (int i = 0; i < this.zLevelOptions.size(); ++i) {
            final BooleanDebugOption booleanDebugOption = this.zLevelOptions.get(i);
            if (booleanDebugOption.getValue()) {
                return booleanDebugOption.zLevel;
            }
        }
        return 0;
    }
    
    public ConfigOption getZLevelOptionByName(final String anObject) {
        for (int i = 0; i < this.zLevelOptions.size(); ++i) {
            final ConfigOption configOption = this.zLevelOptions.get(i);
            if (configOption.getName().equals(anObject)) {
                return configOption;
            }
        }
        return null;
    }
    
    public int getZLevelOptionCount() {
        return this.zLevelOptions.size();
    }
    
    public ConfigOption getZLevelOptionByIndex(final int index) {
        return this.zLevelOptions.get(index);
    }
    
    public void setZLevelOption(final int n, final boolean value) {
        for (int i = 0; i < this.zLevelOptions.size(); ++i) {
            final BooleanDebugOption booleanDebugOption = this.zLevelOptions.get(i);
            if (i != n) {
                booleanDebugOption.setValue(false);
            }
            else {
                booleanDebugOption.setValue(value);
            }
        }
        if (!value) {
            this.zLevelPlayer.setValue(true);
        }
    }
    
    public ConfigOption getOptionByName(final String anObject) {
        for (int i = 0; i < this.options.size(); ++i) {
            final ConfigOption configOption = this.options.get(i);
            if (configOption.getName().equals(anObject)) {
                return configOption;
            }
        }
        return null;
    }
    
    public int getOptionCount() {
        return this.options.size();
    }
    
    public ConfigOption getOptionByIndex(final int index) {
        return this.options.get(index);
    }
    
    public void setBoolean(final String s, final boolean value) {
        final ConfigOption optionByName = this.getOptionByName(s);
        if (optionByName instanceof BooleanConfigOption) {
            ((BooleanConfigOption)optionByName).setValue(value);
        }
    }
    
    public boolean getBoolean(final String s) {
        final ConfigOption optionByName = this.getOptionByName(s);
        return optionByName instanceof BooleanConfigOption && ((BooleanConfigOption)optionByName).getValue();
    }
    
    public void save() {
        new ConfigFile().write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator), 1, (ArrayList<? extends ConfigOption>)this.options);
    }
    
    public void load() {
        final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator);
        final ConfigFile configFile = new ConfigFile();
        if (configFile.read(s)) {
            for (int i = 0; i < configFile.getOptions().size(); ++i) {
                final ConfigOption configOption = configFile.getOptions().get(i);
                final ConfigOption optionByName = this.getOptionByName(configOption.getName());
                if (optionByName != null) {
                    optionByName.parse(configOption.getValueAsString());
                }
            }
        }
    }
    
    public static class BooleanDebugOption extends BooleanConfigOption
    {
        private int index;
        private int zLevel;
        
        public BooleanDebugOption(final ArrayList<ConfigOption> list, final String s, final boolean b, final int zLevel) {
            super(s, b);
            this.zLevel = 0;
            this.index = list.size();
            this.zLevel = zLevel;
            list.add(this);
        }
        
        public BooleanDebugOption(final ArrayList<ConfigOption> list, final String s, final boolean b) {
            super(s, b);
            this.zLevel = 0;
            this.index = list.size();
            list.add(this);
        }
        
        public int getIndex() {
            return this.index;
        }
    }
}
