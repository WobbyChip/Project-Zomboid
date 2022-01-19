// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;

public final class IsoDummyCameraCharacter extends IsoGameCharacter
{
    public IsoDummyCameraCharacter(final float n, final float n2, final float n3) {
        super(null, n, n2, n3);
        IsoCamera.CamCharacter = this;
    }
    
    @Override
    public void update() {
    }
}
