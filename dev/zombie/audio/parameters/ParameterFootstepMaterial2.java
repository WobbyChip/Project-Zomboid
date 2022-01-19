// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoGameCharacter;
import zombie.audio.FMODLocalParameter;

public final class ParameterFootstepMaterial2 extends FMODLocalParameter
{
    private final IsoGameCharacter character;
    
    public ParameterFootstepMaterial2(final IsoGameCharacter character) {
        super("FootstepMaterial2");
        this.character = character;
    }
    
    @Override
    public float calculateCurrentValue() {
        return (float)this.getMaterial().label;
    }
    
    private FootstepMaterial2 getMaterial() {
        if (this.character.getCurrentSquare() == null) {
            return FootstepMaterial2.None;
        }
        if (this.character.getCurrentSquare().getBrokenGlass() != null) {
            return FootstepMaterial2.BrokenGlass;
        }
        final float puddlesInGround = this.character.getCurrentSquare().getPuddlesInGround();
        if (puddlesInGround > 0.5f) {
            return FootstepMaterial2.PuddleDeep;
        }
        if (puddlesInGround > 0.1f) {
            return FootstepMaterial2.PuddleShallow;
        }
        return FootstepMaterial2.None;
    }
    
    enum FootstepMaterial2
    {
        None(0), 
        BrokenGlass(1), 
        PuddleShallow(2), 
        PuddleDeep(3);
        
        final int label;
        
        private FootstepMaterial2(final int label) {
            this.label = label;
        }
        
        private static /* synthetic */ FootstepMaterial2[] $values() {
            return new FootstepMaterial2[] { FootstepMaterial2.None, FootstepMaterial2.BrokenGlass, FootstepMaterial2.PuddleShallow, FootstepMaterial2.PuddleDeep };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
