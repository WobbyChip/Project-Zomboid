// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.iso.sprite.IsoSpriteInstance;
import zombie.core.Rand;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.GameTime;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.IsoCell;
import zombie.iso.Vector2;
import zombie.iso.IsoPhysicsObject;

public class IsoBloodDrop extends IsoPhysicsObject
{
    public float tintb;
    public float tintg;
    public float tintr;
    public float time;
    float sx;
    float sy;
    float lsx;
    float lsy;
    static Vector2 temp;
    
    public IsoBloodDrop(final IsoCell isoCell) {
        super(isoCell);
        this.tintb = 1.0f;
        this.tintg = 1.0f;
        this.tintr = 1.0f;
        this.time = 0.0f;
        this.sx = 0.0f;
        this.sy = 0.0f;
        this.lsx = 0.0f;
        this.lsy = 0.0f;
    }
    
    @Override
    public boolean Serialize() {
        return false;
    }
    
    @Override
    public String getObjectName() {
        return "ZombieGiblets";
    }
    
    @Override
    public void collideGround() {
        final float n = this.x - (int)this.x;
        final float n2 = this.y - (int)this.y;
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare((int)this.x, (int)this.y, (int)this.z);
        if (gridSquare != null) {
            gridSquare.getFloor().addChild(this);
            this.setCollidable(false);
            IsoWorld.instance.CurrentCell.getRemoveList().add(this);
        }
    }
    
    @Override
    public void collideWall() {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare((int)this.x, (int)this.y, (int)this.z);
        if (gridSquare != null) {
            IsoObject isoObject = null;
            if (this.isCollidedN()) {
                isoObject = gridSquare.getWall(true);
            }
            else if (this.isCollidedS()) {
                final IsoGridSquare gridSquare2 = IsoWorld.instance.CurrentCell.getGridSquare((int)this.x, (int)this.y + 1, (int)this.z);
                if (gridSquare2 != null) {
                    isoObject = gridSquare2.getWall(true);
                }
            }
            else if (this.isCollidedW()) {
                isoObject = gridSquare.getWall(false);
            }
            else if (this.isCollidedE()) {
                final IsoGridSquare gridSquare3 = IsoWorld.instance.CurrentCell.getGridSquare((int)this.x + 1, (int)this.y, (int)this.z);
                if (gridSquare3 != null) {
                    isoObject = gridSquare3.getWall(false);
                }
            }
            if (isoObject != null) {
                isoObject.addChild(this);
                this.setCollidable(false);
                IsoWorld.instance.CurrentCell.getRemoveList().add(this);
            }
        }
    }
    
    @Override
    public void update() {
        super.update();
        this.time += GameTime.instance.getMultipliedSecondsSinceLastUpdate();
        if (this.velX == 0.0f && this.velY == 0.0f && this.getZ() == (int)this.getZ()) {
            this.setCollidable(false);
            IsoWorld.instance.CurrentCell.getRemoveList().add(this);
        }
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        this.setTargetAlpha(0.3f);
        this.sprite.render(this, n, n2, n3, this.dir, this.offsetX, this.offsetY, colorInfo, true);
    }
    
    public IsoBloodDrop(final IsoCell isoCell, final float n, final float n2, final float z, final float n3, final float n4) {
        super(isoCell);
        this.tintb = 1.0f;
        this.tintg = 1.0f;
        this.tintr = 1.0f;
        this.time = 0.0f;
        this.sx = 0.0f;
        this.sy = 0.0f;
        this.lsx = 0.0f;
        this.lsy = 0.0f;
        this.velX = n3 * 2.0f;
        this.velY = n4 * 2.0f;
        this.terminalVelocity = -0.1f;
        this.velZ += (Rand.Next(10000) / 10000.0f - 0.5f) * 0.05f;
        final float n5 = Rand.Next(9000) / 10000.0f + 0.1f;
        this.velX *= n5;
        this.velY *= n5;
        this.velZ += n5 * 0.05f;
        if (Rand.Next(7) == 0) {
            this.velX *= 2.0f;
            this.velY *= 2.0f;
        }
        this.velX *= 0.8f;
        this.velY *= 0.8f;
        IsoBloodDrop.temp.x = this.velX;
        IsoBloodDrop.temp.y = this.velY;
        IsoBloodDrop.temp.rotate((Rand.Next(1000) / 1000.0f - 0.5f) * 0.07f);
        if (Rand.Next(3) == 0) {
            IsoBloodDrop.temp.rotate((Rand.Next(1000) / 1000.0f - 0.5f) * 0.1f);
        }
        if (Rand.Next(5) == 0) {
            IsoBloodDrop.temp.rotate((Rand.Next(1000) / 1000.0f - 0.5f) * 0.2f);
        }
        if (Rand.Next(8) == 0) {
            IsoBloodDrop.temp.rotate((Rand.Next(1000) / 1000.0f - 0.5f) * 0.3f);
        }
        if (Rand.Next(10) == 0) {
            IsoBloodDrop.temp.rotate((Rand.Next(1000) / 1000.0f - 0.5f) * 0.4f);
        }
        this.velX = IsoBloodDrop.temp.x;
        this.velY = IsoBloodDrop.temp.y;
        this.x = n;
        this.y = n2;
        this.z = z;
        this.nx = n;
        this.ny = n2;
        this.setAlpha(0.5f);
        this.def = IsoSpriteInstance.get(this.sprite);
        this.def.alpha = 0.4f;
        this.sprite.def.alpha = 0.4f;
        this.offsetX = -26.0f;
        this.offsetY = -242.0f;
        this.offsetX += 8.0f;
        this.offsetY += 9.0f;
        this.sprite.LoadFramesNoDirPageSimple("BloodSplat");
    }
    
    static {
        IsoBloodDrop.temp = new Vector2();
    }
}
