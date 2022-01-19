// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.iso.sprite.IsoSpriteInstance;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoWorld;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoPhysicsObject;

public class IsoZombieGiblets extends IsoPhysicsObject
{
    public float tintb;
    public float tintg;
    public float tintr;
    public float time;
    boolean invis;
    
    public IsoZombieGiblets(final IsoCell isoCell) {
        super(isoCell);
        this.tintb = 1.0f;
        this.tintg = 1.0f;
        this.tintr = 1.0f;
        this.time = 0.0f;
        this.invis = false;
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
    public void update() {
        if (Rand.Next(Rand.AdjustForFramerate(12)) == 0 && this.getZ() > (int)this.getZ() && this.getCurrentSquare() != null && this.getCurrentSquare().getChunk() != null) {
            this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, (float)(int)this.z, Rand.Next(8));
        }
        if (Core.bLastStand && Rand.Next(Rand.AdjustForFramerate(15)) == 0 && this.getZ() > (int)this.getZ() && this.getCurrentSquare() != null && this.getCurrentSquare().getChunk() != null) {
            this.getCurrentSquare().getChunk().addBloodSplat(this.x, this.y, (float)(int)this.z, Rand.Next(8));
        }
        super.update();
        this.time += GameTime.instance.getMultipliedSecondsSinceLastUpdate();
        if (this.velX == 0.0f && this.velY == 0.0f && this.getZ() == (int)this.getZ()) {
            this.setCollidable(false);
            IsoWorld.instance.CurrentCell.getRemoveList().add(this);
        }
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        if (this.invis) {
            return;
        }
        final float r = colorInfo.r;
        final float g = colorInfo.g;
        final float b3 = colorInfo.b;
        colorInfo.r = 0.5f;
        colorInfo.g = 0.5f;
        colorInfo.b = 0.5f;
        final IsoSpriteInstance def = this.sprite.def;
        final IsoSpriteInstance def2 = this.def;
        final float n4 = 1.0f - this.time / 1.0f;
        def2.targetAlpha = n4;
        this.setTargetAlpha(def.targetAlpha = n4);
        super.render(n, n2, n3, colorInfo, b, b2, shader);
        if (Core.bDebug) {}
        colorInfo.r = r;
        colorInfo.g = g;
        colorInfo.b = b3;
    }
    
    public IsoZombieGiblets(final GibletType gibletType, final IsoCell isoCell, final float n, final float n2, final float z, final float velX, final float velY) {
        super(isoCell);
        this.tintb = 1.0f;
        this.tintg = 1.0f;
        this.tintr = 1.0f;
        this.time = 0.0f;
        this.invis = false;
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
        this.setAlpha(0.2f);
        this.def = IsoSpriteInstance.get(this.sprite);
        this.def.alpha = 0.2f;
        this.sprite.def.alpha = 0.4f;
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        if (Rand.Next(3) != 0) {
            this.def.alpha = 0.0f;
            this.sprite.def.alpha = 0.0f;
            this.invis = true;
        }
        switch (gibletType) {
            case A: {
                this.sprite.setFromCache("Giblet", "00", 3);
                break;
            }
            case B: {
                this.sprite.setFromCache("Giblet", "01", 3);
                break;
            }
            case Eye: {
                this.sprite.setFromCache("Eyeball", "00", 1);
                break;
            }
        }
    }
    
    public enum GibletType
    {
        A, 
        B, 
        Eye;
        
        private static /* synthetic */ GibletType[] $values() {
            return new GibletType[] { GibletType.A, GibletType.B, GibletType.Eye };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
