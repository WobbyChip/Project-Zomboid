// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.characters.IsoPlayer;
import zombie.iso.IsoDirections;
import zombie.iso.IsoObject;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCell;
import zombie.iso.IsoPushableObject;

public class IsoWheelieBin extends IsoPushableObject
{
    float velx;
    float vely;
    
    @Override
    public String getObjectName() {
        return "WheelieBin";
    }
    
    public IsoWheelieBin(final IsoCell isoCell) {
        super(isoCell);
        this.velx = 0.0f;
        this.vely = 0.0f;
        this.container = new ItemContainer("wheeliebin", this.square, this);
        this.Collidable = true;
        this.solid = true;
        this.shootable = false;
        this.width = 0.3f;
        this.dir = IsoDirections.E;
        this.setAlphaAndTarget(0.0f);
        this.offsetX = -26.0f;
        this.offsetY = -248.0f;
        this.OutlineOnMouseover = true;
        this.sprite.LoadFramesPageSimple("TileObjectsExt_7", "TileObjectsExt_5", "TileObjectsExt_6", "TileObjectsExt_8");
    }
    
    public IsoWheelieBin(final IsoCell isoCell, final int n, final int n2, final int n3) {
        super(isoCell, n, n2, n3);
        this.velx = 0.0f;
        this.vely = 0.0f;
        this.x = n + 0.5f;
        this.y = n2 + 0.5f;
        this.z = (float)n3;
        this.nx = this.x;
        this.ny = this.y;
        this.offsetX = -26.0f;
        this.offsetY = -248.0f;
        this.weight = 6.0f;
        this.sprite.LoadFramesPageSimple("TileObjectsExt_7", "TileObjectsExt_5", "TileObjectsExt_6", "TileObjectsExt_8");
        this.square = this.getCell().getGridSquare(n, n2, n3);
        this.current = this.getCell().getGridSquare(n, n2, n3);
        this.container = new ItemContainer("wheeliebin", this.square, this);
        this.Collidable = true;
        this.solid = true;
        this.shootable = false;
        this.width = 0.3f;
        this.dir = IsoDirections.E;
        this.setAlphaAndTarget(0.0f);
        this.OutlineOnMouseover = true;
    }
    
    @Override
    public void update() {
        this.velx = this.getX() - this.getLx();
        this.vely = this.getY() - this.getLy();
        float n = 1.0f - this.container.getContentsWeight() / 500.0f;
        if (n < 0.0f) {
            n = 0.0f;
        }
        if (n < 0.7f) {
            n *= n;
        }
        if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance().getDragObject() != this) {
            if (this.velx != 0.0f && this.vely == 0.0f && (this.dir == IsoDirections.E || this.dir == IsoDirections.W)) {
                this.setNx(this.getNx() + this.velx * 0.65f * n);
            }
            if (this.vely != 0.0f && this.velx == 0.0f && (this.dir == IsoDirections.N || this.dir == IsoDirections.S)) {
                this.setNy(this.getNy() + this.vely * 0.65f * n);
            }
        }
        super.update();
    }
    
    @Override
    public float getWeight(final float n, final float n2) {
        float n3 = this.container.getContentsWeight() / 500.0f;
        if (n3 < 0.0f) {
            n3 = 0.0f;
        }
        if (n3 > 1.0f) {
            return this.getWeight() * 8.0f;
        }
        final float n4 = this.getWeight() * n3 + 1.5f;
        if (this.dir == IsoDirections.W || (this.dir == IsoDirections.E && n2 == 0.0f)) {
            return n4 / 2.0f;
        }
        if (this.dir == IsoDirections.N || (this.dir == IsoDirections.S && n == 0.0f)) {
            return n4 / 2.0f;
        }
        return n4 * 3.0f;
    }
}
