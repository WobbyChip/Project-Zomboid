// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.core.properties.PropertyContainer;
import zombie.util.list.PZArrayList;
import zombie.iso.IsoGridSquare;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.IsoObject;
import zombie.characters.IsoPlayer;
import fmod.fmod.FMODManager;
import zombie.characters.IsoGameCharacter;
import zombie.audio.FMODLocalParameter;

public final class ParameterFootstepMaterial extends FMODLocalParameter
{
    private final IsoGameCharacter character;
    
    public ParameterFootstepMaterial(final IsoGameCharacter character) {
        super("FootstepMaterial");
        this.character = character;
    }
    
    @Override
    public float calculateCurrentValue() {
        return (float)this.getMaterial().label;
    }
    
    private FootstepMaterial getMaterial() {
        if (FMODManager.instance.getNumListeners() == 1) {
            int i = 0;
            while (i < IsoPlayer.numPlayers) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null && isoPlayer != this.character && !isoPlayer.Traits.Deaf.isSet()) {
                    if ((int)isoPlayer.getZ() < (int)this.character.getZ()) {
                        return FootstepMaterial.Upstairs;
                    }
                    break;
                }
                else {
                    ++i;
                }
            }
        }
        IsoObject isoObject = null;
        final IsoGridSquare currentSquare = this.character.getCurrentSquare();
        if (currentSquare != null) {
            final PZArrayList<IsoObject> objects = currentSquare.getObjects();
            for (int j = 0; j < objects.size(); ++j) {
                final IsoObject isoObject2 = objects.get(j);
                if (!(isoObject2 instanceof IsoWorldInventoryObject)) {
                    final PropertyContainer properties = isoObject2.getProperties();
                    if (properties != null) {
                        if (properties.Is(IsoFlagType.solidfloor)) {}
                        if (properties.Is("FootstepMaterial")) {
                            isoObject = isoObject2;
                        }
                    }
                }
            }
        }
        if (isoObject != null) {
            try {
                return FootstepMaterial.valueOf(isoObject.getProperties().Val("FootstepMaterial"));
            }
            catch (IllegalArgumentException ex) {}
        }
        return FootstepMaterial.Concrete;
    }
    
    enum FootstepMaterial
    {
        Upstairs(0), 
        BrokenGlass(1), 
        Concrete(2), 
        Grass(3), 
        Gravel(4), 
        Puddle(5), 
        Snow(6), 
        Wood(7), 
        Carpet(8), 
        Dirt(9), 
        Sand(10), 
        Ceramic(11), 
        Metal(12);
        
        final int label;
        
        private FootstepMaterial(final int label) {
            this.label = label;
        }
        
        private static /* synthetic */ FootstepMaterial[] $values() {
            return new FootstepMaterial[] { FootstepMaterial.Upstairs, FootstepMaterial.BrokenGlass, FootstepMaterial.Concrete, FootstepMaterial.Grass, FootstepMaterial.Gravel, FootstepMaterial.Puddle, FootstepMaterial.Snow, FootstepMaterial.Wood, FootstepMaterial.Carpet, FootstepMaterial.Dirt, FootstepMaterial.Sand, FootstepMaterial.Ceramic, FootstepMaterial.Metal };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
