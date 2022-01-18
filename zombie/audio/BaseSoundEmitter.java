// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio;

import zombie.iso.IsoObject;
import zombie.iso.IsoGridSquare;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;

public abstract class BaseSoundEmitter
{
    public abstract void randomStart();
    
    public abstract void setPos(final float p0, final float p1, final float p2);
    
    public abstract int stopSound(final long p0);
    
    public abstract int stopSoundByName(final String p0);
    
    public abstract void stopOrTriggerSound(final long p0);
    
    public abstract void stopOrTriggerSoundByName(final String p0);
    
    public abstract void setVolume(final long p0, final float p1);
    
    public abstract void setPitch(final long p0, final float p1);
    
    public abstract boolean hasSustainPoints(final long p0);
    
    public abstract void setParameterValue(final long p0, final FMOD_STUDIO_PARAMETER_DESCRIPTION p1, final float p2);
    
    public abstract void setTimelinePosition(final long p0, final String p1);
    
    public abstract void triggerCue(final long p0);
    
    public abstract void setVolumeAll(final float p0);
    
    public abstract void stopAll();
    
    public abstract long playSound(final String p0);
    
    public abstract long playSound(final String p0, final int p1, final int p2, final int p3);
    
    public abstract long playSound(final String p0, final IsoGridSquare p1);
    
    public abstract long playSoundImpl(final String p0, final IsoGridSquare p1);
    
    @Deprecated
    public abstract long playSound(final String p0, final boolean p1);
    
    @Deprecated
    public abstract long playSoundImpl(final String p0, final boolean p1, final IsoObject p2);
    
    public abstract long playSoundLooped(final String p0);
    
    public abstract long playSoundLoopedImpl(final String p0);
    
    public abstract long playSound(final String p0, final IsoObject p1);
    
    public abstract long playSoundImpl(final String p0, final IsoObject p1);
    
    public abstract long playClip(final GameSoundClip p0, final IsoObject p1);
    
    public abstract long playAmbientSound(final String p0);
    
    public abstract long playAmbientLoopedImpl(final String p0);
    
    public abstract void set3D(final long p0, final boolean p1);
    
    public abstract void tick();
    
    public abstract boolean hasSoundsToStart();
    
    public abstract boolean isEmpty();
    
    public abstract boolean isPlaying(final long p0);
    
    public abstract boolean isPlaying(final String p0);
    
    public abstract boolean restart(final long p0);
}
