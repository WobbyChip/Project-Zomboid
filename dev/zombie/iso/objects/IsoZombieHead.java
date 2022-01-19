// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.iso.sprite.IsoSpriteInstance;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.GameTime;
import zombie.iso.IsoCell;
import zombie.iso.IsoMovingObject;

public class IsoZombieHead extends IsoMovingObject
{
    public float tintb;
    public float tintg;
    public float tintr;
    public float time;
    
    public IsoZombieHead(final IsoCell isoCell) {
        super(isoCell);
        this.tintb = 1.0f;
        this.tintg = 1.0f;
        this.tintr = 1.0f;
        this.time = 0.0f;
    }
    
    @Override
    public boolean Serialize() {
        return false;
    }
    
    @Override
    public String getObjectName() {
        return "ZombieHead";
    }
    
    @Override
    public void update() {
        super.update();
        this.time += GameTime.instance.getMultipliedSecondsSinceLastUpdate();
        final float n = 0.0f;
        this.sy = n;
        this.sx = n;
    }
    
    @Override
    public void render(final float n, final float n2, final float n3, final ColorInfo colorInfo, final boolean b, final boolean b2, final Shader shader) {
        this.setTargetAlpha(1.0f);
        super.render(n, n2, n3, colorInfo, b, b2, shader);
    }
    
    public IsoZombieHead(final GibletType gibletType, final IsoCell isoCell, final float n, final float n2, final float z) {
        super(isoCell);
        this.tintb = 1.0f;
        this.tintg = 1.0f;
        this.tintr = 1.0f;
        this.time = 0.0f;
        this.solid = false;
        this.shootable = false;
        this.x = n;
        this.y = n2;
        this.z = z;
        this.nx = n;
        this.ny = n2;
        this.setAlpha(0.5f);
        this.def = IsoSpriteInstance.get(this.sprite);
        this.def.alpha = 1.0f;
        this.sprite.def.alpha = 1.0f;
        this.offsetX = -26.0f;
        this.offsetY = -242.0f;
        switch (gibletType) {
            case A: {
                this.sprite.LoadFramesNoDirPageDirect("media/gibs/Giblet", "00", 3);
                break;
            }
            case B: {
                this.sprite.LoadFramesNoDirPageDirect("media/gibs/Giblet", "01", 3);
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
