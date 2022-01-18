// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.core.PerformanceSettings;
import zombie.network.GameServer;

public class IsoPhysicsObject extends IsoMovingObject
{
    public float speedMod;
    public float velX;
    public float velY;
    public float velZ;
    public float terminalVelocity;
    
    public IsoPhysicsObject(final IsoCell isoCell) {
        super(isoCell);
        this.speedMod = 1.0f;
        this.velX = 0.0f;
        this.velY = 0.0f;
        this.velZ = 0.0f;
        this.terminalVelocity = -0.05f;
        this.solid = false;
        this.shootable = false;
    }
    
    public void collideGround() {
    }
    
    public void collideWall() {
    }
    
    @Override
    public void update() {
        final IsoGridSquare currentSquare = this.getCurrentSquare();
        super.update();
        if (this.isCollidedThisFrame()) {
            if (this.isCollidedN() || this.isCollidedS()) {
                this.velY = -this.velY;
                this.velY *= 0.5f;
                this.collideWall();
            }
            if (this.isCollidedE() || this.isCollidedW()) {
                this.velX = -this.velX;
                this.velX *= 0.5f;
                this.collideWall();
            }
        }
        final float n = 30.0f / (GameServer.bServer ? 10 : PerformanceSettings.getLockFPS());
        final float n2 = 1.0f - 0.1f * this.speedMod * n;
        this.velX *= n2;
        this.velY *= n2;
        this.velZ -= 0.005f * n;
        if (this.velZ < this.terminalVelocity) {
            this.velZ = this.terminalVelocity;
        }
        this.setNx(this.getNx() + this.velX * this.speedMod * 0.3f * n);
        this.setNy(this.getNy() + this.velY * this.speedMod * 0.3f * n);
        final float z = this.getZ();
        this.setZ(this.getZ() + this.velZ * 0.4f * n);
        if (this.getZ() < 0.0f) {
            this.setZ(0.0f);
            this.velZ = -this.velZ * 0.5f;
            this.collideGround();
        }
        if (this.getCurrentSquare() != null && (int)this.getZ() < (int)z && ((currentSquare != null && currentSquare.TreatAsSolidFloor()) || this.getCurrentSquare().TreatAsSolidFloor())) {
            this.setZ((float)(int)z);
            this.velZ = -this.velZ * 0.5f;
            this.collideGround();
        }
        if (Math.abs(this.velX) < 1.0E-4f) {
            this.velX = 0.0f;
        }
        if (Math.abs(this.velY) < 1.0E-4f) {
            this.velY = 0.0f;
        }
        if (this.velX + this.velY == 0.0f) {
            this.sprite.Animate = false;
        }
        final float n3 = 0.0f;
        this.sy = n3;
        this.sx = n3;
    }
    
    @Override
    public float getGlobalMovementMod(final boolean b) {
        return 1.0f;
    }
}
