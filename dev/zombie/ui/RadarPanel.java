// 
// Decompiled by Procyon v0.5.36
// 

package zombie.ui;

import java.util.ArrayDeque;
import zombie.popman.ZombiePopulationManager;
import zombie.core.Core;
import zombie.iso.IsoUtils;
import zombie.characters.IsoZombie;
import java.util.Arrays;
import java.util.Collection;
import zombie.core.PerformanceSettings;
import zombie.iso.LotHeader;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.RoomDef;
import zombie.iso.BuildingDef;
import zombie.iso.IsoWorld;
import zombie.IndieGL;
import zombie.core.textures.TextureDraw;
import java.util.function.Consumer;
import zombie.core.SpriteRenderer;
import zombie.network.GameClient;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoCamera;
import java.util.ArrayList;
import zombie.core.textures.Texture;

public final class RadarPanel extends UIElement
{
    private int playerIndex;
    private float xPos;
    private float yPos;
    private float offx;
    private float offy;
    private float zoom;
    private float draww;
    private float drawh;
    private Texture mask;
    private Texture border;
    private ArrayList<ZombiePos> zombiePos;
    private ZombiePosPool zombiePosPool;
    private int zombiePosFrameCount;
    private boolean[] zombiePosOccupied;
    
    public RadarPanel(final int playerIndex) {
        this.zombiePos = new ArrayList<ZombiePos>();
        this.zombiePosPool = new ZombiePosPool();
        this.zombiePosOccupied = new boolean[360];
        this.setX(IsoCamera.getScreenLeft(playerIndex) + 20);
        this.setY(IsoCamera.getScreenTop(playerIndex) + IsoCamera.getScreenHeight(playerIndex) - 120 - 20);
        this.setWidth(120.0);
        this.setHeight(120.0);
        this.mask = Texture.getSharedTexture("media/ui/RadarMask.png");
        this.border = Texture.getSharedTexture("media/ui/RadarBorder.png");
        this.playerIndex = playerIndex;
    }
    
    @Override
    public void update() {
        int n = 0;
        if (IsoPlayer.players[this.playerIndex] != null && IsoPlayer.players[this.playerIndex].getJoypadBind() != -1) {
            n = -72;
        }
        this.setX(IsoCamera.getScreenLeft(this.playerIndex) + 20);
        this.setY(IsoCamera.getScreenTop(this.playerIndex) + IsoCamera.getScreenHeight(this.playerIndex) - this.getHeight() - 20.0 + n);
    }
    
    @Override
    public void render() {
        if (!this.isVisible()) {
            return;
        }
        if (IsoPlayer.players[this.playerIndex] == null) {
            return;
        }
        if (GameClient.bClient) {
            return;
        }
        this.draww = (float)this.getWidth().intValue();
        this.drawh = (float)this.getHeight().intValue();
        this.xPos = IsoPlayer.players[this.playerIndex].getX();
        this.yPos = IsoPlayer.players[this.playerIndex].getY();
        this.offx = (float)this.getAbsoluteX().intValue();
        this.offy = (float)this.getAbsoluteY().intValue();
        this.zoom = 3.0f;
        this.stencilOn();
        SpriteRenderer.instance.render(null, this.offx, this.offy, (float)this.getWidth().intValue(), this.drawh, 0.0f, 0.2f, 0.0f, 0.66f, null);
        this.renderBuildings();
        this.renderRect(this.xPos - 0.5f, this.yPos - 0.5f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
        this.stencilOff();
        this.renderZombies();
        SpriteRenderer.instance.render(this.border, this.offx - 4.0f, this.offy - 4.0f, this.draww + 8.0f, this.drawh + 8.0f, 1.0f, 1.0f, 1.0f, 0.25f, null);
    }
    
    private void stencilOn() {
        IndieGL.glStencilMask(255);
        IndieGL.glClear(1280);
        IndieGL.enableStencilTest();
        IndieGL.glStencilFunc(519, 128, 255);
        IndieGL.glStencilOp(7680, 7680, 7681);
        IndieGL.enableAlphaTest();
        IndieGL.glAlphaFunc(516, 0.1f);
        IndieGL.glColorMask(false, false, false, false);
        SpriteRenderer.instance.renderi(this.mask, (int)this.x, (int)this.y, (int)this.width, (int)this.height, 1.0f, 1.0f, 1.0f, 1.0f, null);
        IndieGL.glColorMask(true, true, true, true);
        IndieGL.glAlphaFunc(516, 0.0f);
        IndieGL.glStencilFunc(514, 128, 128);
        IndieGL.glStencilOp(7680, 7680, 7680);
    }
    
    private void stencilOff() {
        IndieGL.glAlphaFunc(519, 0.0f);
        IndieGL.disableStencilTest();
        IndieGL.disableAlphaTest();
        IndieGL.glStencilFunc(519, 255, 255);
        IndieGL.glStencilOp(7680, 7680, 7680);
        IndieGL.glClear(1280);
    }
    
    private void renderBuildings() {
        final IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
        final IsoMetaCell[][] grid = metaGrid.Grid;
        final int a = (int)((this.xPos - 100.0f) / 300.0f) - metaGrid.minX;
        final int a2 = (int)((this.yPos - 100.0f) / 300.0f) - metaGrid.minY;
        final int a3 = (int)((this.xPos + 100.0f) / 300.0f) - metaGrid.minX;
        final int a4 = (int)((this.yPos + 100.0f) / 300.0f) - metaGrid.minY;
        final int max = Math.max(a, 0);
        final int max2 = Math.max(a2, 0);
        final int min = Math.min(a3, grid.length - 1);
        final int min2 = Math.min(a4, grid[0].length - 1);
        for (int i = max; i <= min; ++i) {
            for (int j = max2; j <= min2; ++j) {
                final LotHeader info = grid[i][j].info;
                if (info != null) {
                    for (int k = 0; k < info.Buildings.size(); ++k) {
                        final BuildingDef buildingDef = info.Buildings.get(k);
                        for (int l = 0; l < buildingDef.rooms.size(); ++l) {
                            if (buildingDef.rooms.get(l).level <= 0) {
                                final ArrayList<RoomDef.RoomRect> rects = buildingDef.rooms.get(l).getRects();
                                for (int index = 0; index < rects.size(); ++index) {
                                    final RoomDef.RoomRect roomRect = rects.get(index);
                                    this.renderRect((float)roomRect.getX(), (float)roomRect.getY(), (float)roomRect.getW(), (float)roomRect.getH(), 0.5f, 0.5f, 0.8f, 0.3f);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void renderZombies() {
        final float n = this.offx + this.draww / 2.0f;
        final float n2 = this.offy + this.drawh / 2.0f;
        final float n3 = this.draww / 2.0f;
        final float n4 = 0.5f * this.zoom;
        if (++this.zombiePosFrameCount >= PerformanceSettings.getLockFPS() / 5) {
            this.zombiePosFrameCount = 0;
            this.zombiePosPool.release(this.zombiePos);
            this.zombiePos.clear();
            Arrays.fill(this.zombiePosOccupied, false);
            final ArrayList<IsoZombie> zombieList = IsoWorld.instance.CurrentCell.getZombieList();
            for (int i = 0; i < zombieList.size(); ++i) {
                final IsoZombie isoZombie = zombieList.get(i);
                final float worldToScreenX = this.worldToScreenX(isoZombie.getX());
                final float worldToScreenY = this.worldToScreenY(isoZombie.getY());
                if (IsoUtils.DistanceToSquared(n, n2, worldToScreenX, worldToScreenY) > n3 * n3) {
                    this.zombiePosOccupied[(int)((Math.toDegrees(Math.atan2(worldToScreenY - n2, worldToScreenX - n) + 3.141592653589793) + 180.0) % 360.0)] = true;
                }
                else {
                    this.zombiePos.add(this.zombiePosPool.alloc(isoZombie.x, isoZombie.y));
                }
            }
            if (Core.bLastStand) {
                if (ZombiePopulationManager.instance.radarXY == null) {
                    ZombiePopulationManager.instance.radarXY = new float[2048];
                }
                final float[] radarXY = ZombiePopulationManager.instance.radarXY;
                synchronized (radarXY) {
                    for (int j = 0; j < ZombiePopulationManager.instance.radarCount; ++j) {
                        final float n5 = radarXY[j * 2 + 0];
                        final float n6 = radarXY[j * 2 + 1];
                        final float worldToScreenX2 = this.worldToScreenX(n5);
                        final float worldToScreenY2 = this.worldToScreenY(n6);
                        if (IsoUtils.DistanceToSquared(n, n2, worldToScreenX2, worldToScreenY2) > n3 * n3) {
                            this.zombiePosOccupied[(int)((Math.toDegrees(Math.atan2(worldToScreenY2 - n2, worldToScreenX2 - n) + 3.141592653589793) + 180.0) % 360.0)] = true;
                        }
                        else {
                            this.zombiePos.add(this.zombiePosPool.alloc(n5, n6));
                        }
                    }
                    ZombiePopulationManager.instance.radarRenderFlag = true;
                }
            }
        }
        for (int size = this.zombiePos.size(), k = 0; k < size; ++k) {
            final ZombiePos zombiePos = this.zombiePos.get(k);
            this.renderRect(zombiePos.x - 0.5f, zombiePos.y - 0.5f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f);
        }
        for (int l = 0; l < this.zombiePosOccupied.length; ++l) {
            if (this.zombiePosOccupied[l]) {
                final double radians = Math.toRadians(l / (float)this.zombiePosOccupied.length * 360.0f);
                SpriteRenderer.instance.render(null, n + (n3 + 1.0f) * (float)Math.cos(radians) - n4, n2 + (n3 + 1.0f) * (float)Math.sin(radians) - n4, 1.0f * this.zoom, 1.0f * this.zoom, 1.0f, 1.0f, 0.0f, 1.0f, null);
            }
        }
    }
    
    private float worldToScreenX(float n) {
        n -= this.xPos;
        n *= this.zoom;
        n += this.offx;
        n += this.draww / 2.0f;
        return n;
    }
    
    private float worldToScreenY(float n) {
        n -= this.yPos;
        n *= this.zoom;
        n += this.offy;
        n += this.drawh / 2.0f;
        return n;
    }
    
    private void renderRect(final float n, final float n2, float n3, float n4, final float n5, final float n6, final float n7, final float n8) {
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
    
    private static final class ZombiePos
    {
        public float x;
        public float y;
        
        public ZombiePos(final float x, final float y) {
            this.x = x;
            this.y = y;
        }
        
        public ZombiePos set(final float x, final float y) {
            this.x = x;
            this.y = y;
            return this;
        }
    }
    
    private static class ZombiePosPool
    {
        private ArrayDeque<ZombiePos> pool;
        
        private ZombiePosPool() {
            this.pool = new ArrayDeque<ZombiePos>();
        }
        
        public ZombiePos alloc(final float n, final float n2) {
            return this.pool.isEmpty() ? new ZombiePos(n, n2) : this.pool.pop().set(n, n2);
        }
        
        public void release(final Collection<ZombiePos> c) {
            this.pool.addAll(c);
        }
    }
}
