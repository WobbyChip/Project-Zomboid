// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import zombie.iso.IsoCell;

public final class IsoLuaCharacter extends IsoGameCharacter
{
    public IsoLuaCharacter(final float n, final float n2, final float n3) {
        super(null, n, n2, n3);
        (this.descriptor = SurvivorFactory.CreateSurvivor()).setInstance(this);
        this.InitSpriteParts(this.descriptor);
    }
    
    @Override
    public void update() {
    }
}
