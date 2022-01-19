// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.obj;

import zombie.iso.sprite.IsoSpriteInstance;
import java.util.ArrayList;
import zombie.iso.IsoObject;

public final class ErosionObjOverlay
{
    private final ErosionObjOverlaySprites sprites;
    public String name;
    public int stages;
    public boolean applyAlpha;
    public int cycleTime;
    
    public ErosionObjOverlay(final ErosionObjOverlaySprites sprites, final int cycleTime, final boolean applyAlpha) {
        this.sprites = sprites;
        this.name = sprites.name;
        this.stages = sprites.stages;
        this.applyAlpha = applyAlpha;
        this.cycleTime = cycleTime;
    }
    
    public int setOverlay(final IsoObject isoObject, final int n, final int n2, final int n3, final float n4) {
        if (n2 >= 0 && n2 < this.stages && isoObject != null) {
            if (n >= 0) {
                this.removeOverlay(isoObject, n);
            }
            final IsoSpriteInstance instance = this.sprites.getSprite(n2, n3).newInstance();
            if (isoObject.AttachedAnimSprite == null) {
                isoObject.AttachedAnimSprite = new ArrayList<IsoSpriteInstance>();
            }
            isoObject.AttachedAnimSprite.add(instance);
            return instance.getID();
        }
        return -1;
    }
    
    public boolean removeOverlay(final IsoObject isoObject, final int n) {
        if (isoObject == null) {
            return false;
        }
        final ArrayList<IsoSpriteInstance> attachedAnimSprite = isoObject.AttachedAnimSprite;
        if (attachedAnimSprite == null || attachedAnimSprite.isEmpty()) {
            return false;
        }
        for (int i = 0; i < isoObject.AttachedAnimSprite.size(); ++i) {
            if (isoObject.AttachedAnimSprite.get(i).parentSprite.ID == n) {
                isoObject.AttachedAnimSprite.remove(i--);
            }
        }
        for (int j = attachedAnimSprite.size() - 1; j >= 0; --j) {
            if (attachedAnimSprite.get(j).getID() == n) {
                attachedAnimSprite.remove(j);
                return true;
            }
        }
        return false;
    }
}
