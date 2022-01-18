// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio.parameters;

import zombie.characters.IsoPlayer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.Moodles.MoodleType;
import zombie.audio.FMODGlobalParameter;

public final class ParameterMoodlePanic extends FMODGlobalParameter
{
    public ParameterMoodlePanic() {
        super("MoodlePanic");
    }
    
    @Override
    public float calculateCurrentValue() {
        final IsoGameCharacter character = this.getCharacter();
        if (character == null) {
            return 0.0f;
        }
        return character.getMoodles().getMoodleLevel(MoodleType.Panic) / 4.0f;
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
