// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import zombie.iso.IsoObject;

public abstract class BaseCharacterSoundEmitter
{
    protected final IsoGameCharacter character;
    
    public BaseCharacterSoundEmitter(final IsoGameCharacter character) {
        this.character = character;
    }
    
    public abstract void register();
    
    public abstract void unregister();
    
    public abstract long playVocals(final String p0);
    
    public abstract void playFootsteps(final String p0, final float p1);
    
    public abstract long playSound(final String p0);
    
    public abstract long playSound(final String p0, final IsoObject p1);
    
    public abstract long playSoundImpl(final String p0, final IsoObject p1);
    
    public abstract void tick();
    
    public abstract void set(final float p0, final float p1, final float p2);
    
    public abstract boolean isClear();
    
    public abstract void setPitch(final long p0, final float p1);
    
    public abstract void setVolume(final long p0, final float p1);
    
    public abstract int stopSound(final long p0);
    
    public abstract int stopSoundByName(final String p0);
    
    public abstract void stopOrTriggerSound(final long p0);
    
    public abstract void stopOrTriggerSoundByName(final String p0);
    
    public abstract void stopAll();
    
    public abstract boolean hasSoundsToStart();
    
    public abstract boolean isPlaying(final long p0);
    
    public abstract boolean isPlaying(final String p0);
    
    public abstract void setParameterValue(final long p0, final FMOD_STUDIO_PARAMETER_DESCRIPTION p1, final float p2);
}
