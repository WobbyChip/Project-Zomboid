// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.sprite;

import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCamera;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoDirections;
import zombie.iso.IsoObject;
import java.util.concurrent.atomic.AtomicBoolean;
import zombie.popman.ObjectPool;

public final class IsoSpriteInstance
{
    public static final ObjectPool<IsoSpriteInstance> pool;
    private static final AtomicBoolean lock;
    public IsoSprite parentSprite;
    public float tintb;
    public float tintg;
    public float tintr;
    public float Frame;
    public float alpha;
    public float targetAlpha;
    public boolean bCopyTargetAlpha;
    public boolean bMultiplyObjectAlpha;
    public boolean Flip;
    public float offZ;
    public float offX;
    public float offY;
    public float AnimFrameIncrease;
    static float multiplier;
    public boolean Looped;
    public boolean Finished;
    public boolean NextFrame;
    public float scaleX;
    public float scaleY;
    
    public static IsoSpriteInstance get(final IsoSprite parentSprite) {
        while (!IsoSpriteInstance.lock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }
        final IsoSpriteInstance isoSpriteInstance = IsoSpriteInstance.pool.alloc();
        IsoSpriteInstance.lock.set(false);
        isoSpriteInstance.parentSprite = parentSprite;
        isoSpriteInstance.reset();
        return isoSpriteInstance;
    }
    
    private void reset() {
        this.tintb = 1.0f;
        this.tintg = 1.0f;
        this.tintr = 1.0f;
        this.Frame = 0.0f;
        this.alpha = 1.0f;
        this.targetAlpha = 1.0f;
        this.bCopyTargetAlpha = true;
        this.bMultiplyObjectAlpha = false;
        this.Flip = false;
        this.offZ = 0.0f;
        this.offX = 0.0f;
        this.offY = 0.0f;
        this.AnimFrameIncrease = 1.0f;
        IsoSpriteInstance.multiplier = 1.0f;
        this.Looped = true;
        this.Finished = false;
        this.NextFrame = false;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
    }
    
    public IsoSpriteInstance() {
        this.tintb = 1.0f;
        this.tintg = 1.0f;
        this.tintr = 1.0f;
        this.Frame = 0.0f;
        this.alpha = 1.0f;
        this.targetAlpha = 1.0f;
        this.bCopyTargetAlpha = true;
        this.bMultiplyObjectAlpha = false;
        this.offZ = 0.0f;
        this.offX = 0.0f;
        this.offY = 0.0f;
        this.AnimFrameIncrease = 1.0f;
        this.Looped = true;
        this.Finished = false;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
    }
    
    public void setFrameSpeedPerFrame(final float n) {
        this.AnimFrameIncrease = n * IsoSpriteInstance.multiplier;
    }
    
    public int getID() {
        return this.parentSprite.ID;
    }
    
    public String getName() {
        return this.parentSprite.getName();
    }
    
    public IsoSprite getParentSprite() {
        return this.parentSprite;
    }
    
    public IsoSpriteInstance(final IsoSprite parentSprite) {
        this.tintb = 1.0f;
        this.tintg = 1.0f;
        this.tintr = 1.0f;
        this.Frame = 0.0f;
        this.alpha = 1.0f;
        this.targetAlpha = 1.0f;
        this.bCopyTargetAlpha = true;
        this.bMultiplyObjectAlpha = false;
        this.offZ = 0.0f;
        this.offX = 0.0f;
        this.offY = 0.0f;
        this.AnimFrameIncrease = 1.0f;
        this.Looped = true;
        this.Finished = false;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.parentSprite = parentSprite;
    }
    
    public float getTintR() {
        return this.tintr;
    }
    
    public float getTintG() {
        return this.tintg;
    }
    
    public float getTintB() {
        return this.tintb;
    }
    
    public float getAlpha() {
        return this.alpha;
    }
    
    public float getTargetAlpha() {
        return this.targetAlpha;
    }
    
    public boolean isCopyTargetAlpha() {
        return this.bCopyTargetAlpha;
    }
    
    public boolean isMultiplyObjectAlpha() {
        return this.bMultiplyObjectAlpha;
    }
    
    public void render(final IsoObject isoObject, final float n, final float n2, final float n3, final IsoDirections isoDirections, final float n4, final float n5, final ColorInfo colorInfo) {
        this.parentSprite.render(this, isoObject, n, n2, n3, isoDirections, n4, n5, colorInfo, true);
    }
    
    public void SetAlpha(final float alpha) {
        this.alpha = alpha;
        this.bCopyTargetAlpha = false;
    }
    
    public void SetTargetAlpha(final float targetAlpha) {
        this.targetAlpha = targetAlpha;
        this.bCopyTargetAlpha = false;
    }
    
    public void update() {
    }
    
    protected void renderprep(final IsoObject isoObject) {
        if (isoObject != null && this.bCopyTargetAlpha) {
            this.targetAlpha = isoObject.getTargetAlpha(IsoCamera.frameState.playerIndex);
            this.alpha = isoObject.getAlpha(IsoCamera.frameState.playerIndex);
            return;
        }
        if (this.bMultiplyObjectAlpha) {
            return;
        }
        if (this.alpha < this.targetAlpha) {
            this.alpha += IsoSprite.alphaStep;
            if (this.alpha > this.targetAlpha) {
                this.alpha = this.targetAlpha;
            }
        }
        else if (this.alpha > this.targetAlpha) {
            this.alpha -= IsoSprite.alphaStep;
            if (this.alpha < this.targetAlpha) {
                this.alpha = this.targetAlpha;
            }
        }
        if (this.alpha < 0.0f) {
            this.alpha = 0.0f;
        }
        if (this.alpha > 1.0f) {
            this.alpha = 1.0f;
        }
    }
    
    public float getFrame() {
        return this.Frame;
    }
    
    public boolean isFinished() {
        return this.Finished;
    }
    
    public void Dispose() {
    }
    
    public void RenderGhostTileColor(final int n, final int n2, final int n3, final float tintr, final float tintg, final float tintb, final float n4) {
        if (this.parentSprite == null) {
            return;
        }
        final IsoSpriteInstance value = get(this.parentSprite);
        value.Frame = this.Frame;
        value.tintr = tintr;
        value.tintg = tintg;
        value.tintb = tintb;
        final IsoSpriteInstance isoSpriteInstance = value;
        value.targetAlpha = n4;
        isoSpriteInstance.alpha = n4;
        final ColorInfo defColorInfo = IsoGridSquare.getDefColorInfo();
        final ColorInfo defColorInfo2 = IsoGridSquare.getDefColorInfo();
        final ColorInfo defColorInfo3 = IsoGridSquare.getDefColorInfo();
        final ColorInfo defColorInfo4 = IsoGridSquare.getDefColorInfo();
        final float n5 = 1.0f;
        defColorInfo4.a = n5;
        defColorInfo3.b = n5;
        defColorInfo2.g = n5;
        defColorInfo.r = n5;
        this.parentSprite.render(value, null, (float)n, (float)n2, (float)n3, IsoDirections.N, 0.0f, -144.0f, IsoGridSquare.getDefColorInfo(), true);
    }
    
    public void setScale(final float scaleX, final float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    
    public float getScaleX() {
        return this.scaleX;
    }
    
    public float getScaleY() {
        return this.scaleY;
    }
    
    public void scaleAspect(final float n, final float n2, float n3, float n4) {
        if (n > 0.0f && n2 > 0.0f && n3 > 0.0f && n4 > 0.0f) {
            final float n5 = n4 * n / n2;
            final float n6 = n3 * n2 / n;
            if (n5 <= n3) {
                n3 = n5;
            }
            else {
                n4 = n6;
            }
            this.scaleX = n3 / n;
            this.scaleY = n4 / n2;
        }
    }
    
    public static void add(final IsoSpriteInstance isoSpriteInstance) {
        isoSpriteInstance.reset();
        while (!IsoSpriteInstance.lock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }
        IsoSpriteInstance.pool.release(isoSpriteInstance);
        IsoSpriteInstance.lock.set(false);
    }
    
    static {
        pool = new ObjectPool<IsoSpriteInstance>(IsoSpriteInstance::new);
        lock = new AtomicBoolean(false);
        IsoSpriteInstance.multiplier = 1.0f;
    }
}
