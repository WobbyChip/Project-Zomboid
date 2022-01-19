// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;

public class IsoRadio extends IsoWaveSignal
{
    public IsoRadio(final IsoCell isoCell) {
        super(isoCell);
    }
    
    public IsoRadio(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final IsoSprite isoSprite) {
        super(isoCell, isoGridSquare, isoSprite);
    }
    
    @Override
    public String getObjectName() {
        return "Radio";
    }
    
    @Override
    protected void init(final boolean b) {
        super.init(b);
    }
}
