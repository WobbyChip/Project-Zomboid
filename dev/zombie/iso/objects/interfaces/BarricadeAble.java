// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects.interfaces;

import zombie.iso.IsoGridSquare;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoBarricade;

public interface BarricadeAble
{
    boolean isBarricaded();
    
    boolean isBarricadeAllowed();
    
    IsoBarricade getBarricadeOnSameSquare();
    
    IsoBarricade getBarricadeOnOppositeSquare();
    
    IsoBarricade getBarricadeForCharacter(final IsoGameCharacter p0);
    
    IsoBarricade getBarricadeOppositeCharacter(final IsoGameCharacter p0);
    
    IsoGridSquare getSquare();
    
    IsoGridSquare getOppositeSquare();
    
    boolean getNorth();
}
