// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoPlayer;
import zombie.iso.RoomDef;
import zombie.characters.IsoGameCharacter;
import zombie.audio.FMODGlobalParameter;

public final class ParameterRoomSize extends FMODGlobalParameter
{
    public ParameterRoomSize() {
        super("RoomSize");
    }
    
    @Override
    public float calculateCurrentValue() {
        final IsoGameCharacter character = this.getCharacter();
        if (character == null) {
            return 0.0f;
        }
        final RoomDef currentRoomDef = character.getCurrentRoomDef();
        if (currentRoomDef == null) {
            return 0.0f;
        }
        return (float)currentRoomDef.getArea();
    }
    
    private IsoGameCharacter getCharacter() {
        IsoGameCharacter isoGameCharacter = null;
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null && (isoGameCharacter == null || (isoGameCharacter.isDead() && isoPlayer.isAlive()))) {
                isoGameCharacter = isoPlayer;
            }
        }
        return isoGameCharacter;
    }
}
