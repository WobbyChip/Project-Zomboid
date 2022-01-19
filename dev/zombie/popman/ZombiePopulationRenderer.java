// 
// Decompiled by Procyon v0.5.36
// 

package zombie.popman;

import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.config.ConfigFile;
import zombie.config.BooleanConfigOption;
import zombie.iso.LotHeader;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoCell;
import zombie.vehicles.VehiclesDB2;
import zombie.network.GameClient;
import zombie.characters.IsoPlayer;
import zombie.ai.states.WalkTowardState;
import zombie.characters.IsoZombie;
import zombie.iso.RoomDef;
import zombie.iso.BuildingDef;
import zombie.core.math.PZMath;
import zombie.iso.IsoWorld;
import zombie.MapCollisionData;
import zombie.ui.UIElement;
import zombie.core.Core;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.textures.Texture;
import zombie.ui.UIFont;
import zombie.ui.TextManager;
import zombie.core.SpriteRenderer;
import zombie.config.ConfigOption;
import java.util.ArrayList;

public final class ZombiePopulationRenderer
{
    private float xPos;
    private float yPos;
    private float offx;
    private float offy;
    private float zoom;
    private float draww;
    private float drawh;
    private static final int VERSION = 1;
    private final ArrayList<ConfigOption> options;
    private BooleanDebugOption CellGrid;
    private BooleanDebugOption MetaGridBuildings;
    private BooleanDebugOption ZombiesStanding;
    private BooleanDebugOption ZombiesMoving;
    private BooleanDebugOption MCDObstacles;
    private BooleanDebugOption MCDRegularChunkOutlines;
    private BooleanDebugOption MCDRooms;
    private BooleanDebugOption Vehicles;
    
    public ZombiePopulationRenderer() {
        this.options = new ArrayList<ConfigOption>();
        this.CellGrid = new BooleanDebugOption("CellGrid", true);
        this.MetaGridBuildings = new BooleanDebugOption("MetaGrid.Buildings", true);
        this.ZombiesStanding = new BooleanDebugOption("Zombies.Standing", true);
        this.ZombiesMoving = new BooleanDebugOption("Zombies.Moving", true);
        this.MCDObstacles = new BooleanDebugOption("MapCollisionData.Obstacles", true);
        this.MCDRegularChunkOutlines = new BooleanDebugOption("MapCollisionData.RegularChunkOutlines", true);
        this.MCDRooms = new BooleanDebugOption("MapCollisionData.Rooms", true);
        this.Vehicles = new BooleanDebugOption("Vehicles", true);
    }
    
    private native void n_render(final float p0, final int p1, final int p2, final float p3, final float p4, final int p5, final int p6);
    
    private native void n_setWallFollowerStart(final int p0, final int p1);
    
    private native void n_setWallFollowerEnd(final int p0, final int p1);
    
    private native void n_wallFollowerMouseMove(final int p0, final int p1);
    
    private native void n_setDebugOption(final String p0, final String p1);
    
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
    
    public void renderCircle(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7) {
        final int n8 = 32;
        double n9 = n + n3 * Math.cos(Math.toRadians(0.0f / n8));
        double n10 = n2 + n3 * Math.sin(Math.toRadians(0.0f / n8));
        for (int i = 1; i <= n8; ++i) {
            final double n11 = n + n3 * Math.cos(Math.toRadians(i * 360.0f / n8));
            final double n12 = n2 + n3 * Math.sin(Math.toRadians(i * 360.0f / n8));
            SpriteRenderer.instance.renderline(null, (int)this.worldToScreenX((float)n9), (int)this.worldToScreenY((float)n10), (int)this.worldToScreenX((float)n11), (int)this.worldToScreenY((float)n12), n4, n5, n6, n7);
            n9 = n11;
            n10 = n12;
        }
    }
    
    public void renderZombie(final float n, final float n2, final float n3, final float n4, final float n5) {
        final float n6 = 1.0f / this.zoom + 0.5f;
        this.renderRect(n - n6 / 2.0f, n2 - n6 / 2.0f, n6, n6, n3, n4, n5, 1.0f);
    }
    
    public void renderVehicle(final int i, final float n, final float n2, final float n3, final float n4, final float n5) {
        final float n6 = 2.0f / this.zoom + 0.5f;
        this.renderRect(n - n6 / 2.0f, n2 - n6 / 2.0f, n6, n6, n3, n4, n5, 1.0f);
        this.renderString(n, n2, String.format("%d", i), n3, n4, n5, 1.0);
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
    
    public void render(final UIElement uiElement, final float n, final float n2, final float n3) {
        synchronized (MapCollisionData.instance.renderLock) {
            this._render(uiElement, n, n2, n3);
        }
    }
    
    private void _render(final UIElement uiElement, final float zoom, final float xPos, final float yPos) {
        this.draww = (float)uiElement.getWidth().intValue();
        this.drawh = (float)uiElement.getHeight().intValue();
        this.xPos = xPos;
        this.yPos = yPos;
        this.offx = (float)uiElement.getAbsoluteX().intValue();
        this.offy = (float)uiElement.getAbsoluteY().intValue();
        this.zoom = zoom;
        final IsoCell currentCell = IsoWorld.instance.CurrentCell;
        final IsoChunkMap isoChunkMap = IsoWorld.instance.CurrentCell.ChunkMap[0];
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
        if (this.MetaGridBuildings.getValue()) {
            for (int i = clamp; i <= clamp3; ++i) {
                for (int j = clamp2; j <= clamp4; ++j) {
                    final LotHeader info = grid[i][j].info;
                    if (info != null) {
                        for (int k = 0; k < info.Buildings.size(); ++k) {
                            final BuildingDef buildingDef = info.Buildings.get(k);
                            for (int l = 0; l < buildingDef.rooms.size(); ++l) {
                                if (buildingDef.rooms.get(l).level <= 0) {
                                    final ArrayList<RoomDef.RoomRect> rects = buildingDef.rooms.get(l).getRects();
                                    for (int index = 0; index < rects.size(); ++index) {
                                        final RoomDef.RoomRect roomRect = rects.get(index);
                                        if (buildingDef.bAlarmed) {
                                            this.renderRect((float)roomRect.getX(), (float)roomRect.getY(), (float)roomRect.getW(), (float)roomRect.getH(), 0.8f, 0.8f, 0.5f, 0.3f);
                                        }
                                        else {
                                            this.renderRect((float)roomRect.getX(), (float)roomRect.getY(), (float)roomRect.getW(), (float)roomRect.getH(), 0.5f, 0.5f, 0.8f, 0.3f);
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
            for (int n5 = clamp2; n5 <= clamp4; ++n5) {
                this.renderLine((float)(metaGrid.minX * 300), (float)((metaGrid.minY + n5) * 300), (float)((metaGrid.maxX + 1) * 300), (float)((metaGrid.minY + n5) * 300), 1.0f, 1.0f, 1.0f, 0.15f);
            }
            for (int n6 = clamp; n6 <= clamp3; ++n6) {
                this.renderLine((float)((metaGrid.minX + n6) * 300), (float)(metaGrid.minY * 300), (float)((metaGrid.minX + n6) * 300), (float)((metaGrid.maxY + 1) * 300), 1.0f, 1.0f, 1.0f, 0.15f);
            }
        }
        for (int index2 = 0; index2 < IsoWorld.instance.CurrentCell.getZombieList().size(); ++index2) {
            final IsoZombie isoZombie = IsoWorld.instance.CurrentCell.getZombieList().get(index2);
            this.renderZombie(isoZombie.x, isoZombie.y, 1.0f, 1.0f, 0.0f);
            if (isoZombie.getCurrentState() == WalkTowardState.instance()) {
                this.renderLine(isoZombie.x, isoZombie.y, (float)isoZombie.getPathTargetX(), (float)isoZombie.getPathTargetY(), 1.0f, 1.0f, 1.0f, 0.5f);
            }
        }
        for (int n7 = 0; n7 < IsoPlayer.numPlayers; ++n7) {
            final IsoPlayer isoPlayer = IsoPlayer.players[n7];
            if (isoPlayer != null) {
                this.renderZombie(isoPlayer.x, isoPlayer.y, 0.0f, 0.5f, 0.0f);
            }
        }
        if (GameClient.bClient) {
            MPDebugInfo.instance.render(this, zoom);
            return;
        }
        if (this.Vehicles.getValue()) {
            VehiclesDB2.instance.renderDebug(this);
        }
        this.n_render(zoom, (int)this.offx, (int)this.offy, xPos, yPos, (int)this.draww, (int)this.drawh);
    }
    
    public void setWallFollowerStart(final int n, final int n2) {
        if (GameClient.bClient) {
            return;
        }
        this.n_setWallFollowerStart(n, n2);
    }
    
    public void setWallFollowerEnd(final int n, final int n2) {
        if (GameClient.bClient) {
            return;
        }
        this.n_setWallFollowerEnd(n, n2);
    }
    
    public void wallFollowerMouseMove(final int n, final int n2) {
        if (GameClient.bClient) {
            return;
        }
        this.n_wallFollowerMouseMove(n, n2);
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
        for (int i = 0; i < this.options.size(); ++i) {
            final ConfigOption configOption = this.options.get(i);
            this.n_setDebugOption(configOption.getName(), configOption.getValueAsString());
        }
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
        for (int j = 0; j < this.options.size(); ++j) {
            final ConfigOption configOption2 = this.options.get(j);
            this.n_setDebugOption(configOption2.getName(), configOption2.getValueAsString());
        }
    }
    
    public class BooleanDebugOption extends BooleanConfigOption
    {
        public BooleanDebugOption(final String s, final boolean b) {
            super(s, b);
            ZombiePopulationRenderer.this.options.add(this);
        }
    }
}
