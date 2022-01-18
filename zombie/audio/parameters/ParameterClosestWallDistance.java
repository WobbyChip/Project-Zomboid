// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoPlayer;
import zombie.characters.IsoGameCharacter;
import zombie.iso.NearestWalls;
import zombie.audio.FMODGlobalParameter;

public final class ParameterClosestWallDistance extends FMODGlobalParameter
{
    public ParameterClosestWallDistance() {
        super("ClosestWallDistance");
    }
    
    @Override
    public float calculateCurrentValue() {
        final IsoGameCharacter character = this.getCharacter();
        if (character == null) {
            return 127.0f;
        }
        return (float)NearestWalls.ClosestWallDistance(character.getCurrentSquare());
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
