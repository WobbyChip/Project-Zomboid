// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoGameCharacter;
import zombie.audio.FMODLocalParameter;

public final class ParameterMeleeHitSurface extends FMODLocalParameter
{
    private final IsoGameCharacter character;
    private Material material;
    
    public ParameterMeleeHitSurface(final IsoGameCharacter character) {
        super("MeleeHitSurface");
        this.material = Material.Default;
        this.character = character;
    }
    
    @Override
    public float calculateCurrentValue() {
        return (float)this.getMaterial().label;
    }
    
    private Material getMaterial() {
        return this.material;
    }
    
    public void setMaterial(final Material material) {
        this.material = material;
    }
    
    public enum Material
    {
        Default(0), 
        Body(1), 
        Fabric(2), 
        Glass(3), 
        Head(4), 
        Metal(5), 
        Plastic(6), 
        Stone(7), 
        Wood(8);
        
        final int label;
        
        private Material(final int label) {
            this.label = label;
        }
        
        private static /* synthetic */ Material[] $values() {
            return new Material[] { Material.Default, Material.Body, Material.Fabric, Material.Glass, Material.Head, Material.Metal, Material.Plastic, Material.Stone, Material.Wood };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
