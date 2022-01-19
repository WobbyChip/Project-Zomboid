// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoGameCharacter;
import zombie.audio.FMODLocalParameter;

public final class ParameterCharacterInside extends FMODLocalParameter
{
    private final IsoGameCharacter character;
    
    public ParameterCharacterInside(final IsoGameCharacter character) {
        super("CharacterInside");
        this.character = character;
    }
    
    @Override
    public float calculateCurrentValue() {
        if (this.character.getVehicle() != null) {
            return 2.0f;
        }
        if (this.character.getCurrentBuilding() == null) {
            return 0.0f;
        }
        return 1.0f;
    }
}
