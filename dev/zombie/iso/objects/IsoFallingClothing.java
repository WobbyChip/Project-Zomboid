// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.iso.IsoGridSquare;
import zombie.Lua.LuaEventManager;
import zombie.iso.IsoMovingObject;
import zombie.core.skinnedmodel.model.WorldItemModelDrawer;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoPhysicsObject;

public class IsoFallingClothing extends IsoPhysicsObject
{
    private InventoryItem clothing;
    private int dropTimer;
    public boolean addWorldItem;
    
    @Override
    public String getObjectName() {
        return "FallingClothing";
    }
    
    public IsoFallingClothing(final IsoCell isoCell) {
        super(isoCell);
        this.clothing = null;
        this.dropTimer = 0;
        this.addWorldItem = true;
    }
    
    public IsoFallingClothing(final IsoCell isoCell, final float n, final float n2, final float z, final float velX, final float velY, final InventoryItem clothing) {
        super(isoCell);
        this.clothing = null;
        this.dropTimer = 0;
        this.addWorldItem = true;
        this.clothing = clothing;
        this.dropTimer = 60;
        this.velX = velX;
        this.velY = velY;
        final float n3 = Rand.Next(4000) / 10000.0f;
        final float n4 = Rand.Next(4000) / 10000.0f;
        final float n5 = n3 - 0.2f;
        final float n6 = n4 - 0.2f;
        this.velX += n5;
        this.velY += n6;
        this.x = n;
        this.y = n2;
        this.z = z;
        this.nx = n;
        this.ny = n2;
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        this.terminalVelocity = -0.02f;
        final Texture loadFrameExplicit = this.sprite.LoadFrameExplicit(clothing.getTex().getName());
        if (loadFrameExplicit != null) {
            this.sprite.Animate = false;
            final int tileScale = Core.TileScale;
            this.sprite.def.scaleAspect((float)loadFrameExplicit.getWidthOrig(), (float)loadFrameExplicit.getHeightOrig(), (float)(16 * tileScale), (float)(16 * tileScale));
        }
        this.speedMod = 4.5f;
    }
    
    @Override
    public void collideGround() {
        this.drop();
    }
    
    @Override
    public void collideWall() {
        this.drop();
    }
    
    @Override
    public void update() {
        super.update();
        --this.dropTimer;
        if (this.dropTimer <= 0) {
            this.drop();
        }
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (WorldItemModelDrawer.renderMain(this.clothing, this.getCurrentSquare(), this.getX(), this.getY(), this.getZ(), (60 - this.dropTimer) / 60.0f * 360.0f)) {
            return;
        }
        super.render(n, n2, n3, colorInfo, b, b2, shader);
    }
    
    void drop() {
        final IsoGridSquare currentSquare = this.getCurrentSquare();
        if (currentSquare != null && this.clothing != null) {
            if (this.addWorldItem) {
                currentSquare.AddWorldInventoryItem(this.clothing, this.getX() % 1.0f, this.getY() % 1.0f, currentSquare.getApparentZ(this.getX() % 1.0f, this.getY() % 1.0f) - currentSquare.getZ());
            }
            this.clothing = null;
            this.setDestroyed(true);
            currentSquare.getMovingObjects().remove(this);
            this.getCell().Remove(this);
            LuaEventManager.triggerEvent("OnContainerUpdate", currentSquare);
        }
    }
    
    void Trigger() {
    }
}
