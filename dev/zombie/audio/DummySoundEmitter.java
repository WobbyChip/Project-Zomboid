// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio;

import zombie.iso.IsoObject;
import zombie.iso.IsoGridSquare;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;

public class DummySoundEmitter extends BaseSoundEmitter
{
    @Override
    public void randomStart() {
    }
    
    @Override
    public void setPos(final float n, final float n2, final float n3) {
    }
    
    @Override
    public int stopSound(final long n) {
        return 0;
    }
    
    @Override
    public int stopSoundByName(final String s) {
        return 0;
    }
    
    @Override
    public void stopOrTriggerSound(final long n) {
    }
    
    @Override
    public void stopOrTriggerSoundByName(final String s) {
    }
    
    @Override
    public void setVolume(final long n, final float n2) {
    }
    
    @Override
    public void setPitch(final long n, final float n2) {
    }
    
    @Override
    public boolean hasSustainPoints(final long n) {
        return false;
    }
    
    @Override
    public void setParameterValue(final long n, final FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_STUDIO_PARAMETER_DESCRIPTION, final float n2) {
    }
    
    @Override
    public void setTimelinePosition(final long n, final String s) {
    }
    
    @Override
    public void triggerCue(final long n) {
    }
    
    @Override
    public void set3D(final long n, final boolean b) {
    }
    
    @Override
    public void setVolumeAll(final float n) {
    }
    
    @Override
    public void stopAll() {
    }
    
    @Override
    public long playSound(final String s) {
        return 0L;
    }
    
    @Override
    public long playSound(final String s, final int n, final int n2, final int n3) {
        return 0L;
    }
    
    @Override
    public long playSound(final String s, final IsoGridSquare isoGridSquare) {
        return 0L;
    }
    
    @Override
    public long playSoundImpl(final String s, final IsoGridSquare isoGridSquare) {
        return 0L;
    }
    
    @Override
    public long playSound(final String s, final boolean b) {
        return 0L;
    }
    
    @Override
    public long playSoundImpl(final String s, final boolean b, final IsoObject isoObject) {
        return 0L;
    }
    
    @Override
    public long playSound(final String s, final IsoObject isoObject) {
        return 0L;
    }
    
    @Override
    public long playSoundImpl(final String s, final IsoObject isoObject) {
        return 0L;
    }
    
    @Override
    public long playClip(final GameSoundClip gameSoundClip, final IsoObject isoObject) {
        return 0L;
    }
    
    @Override
    public long playAmbientSound(final String s) {
        return 0L;
    }
    
    @Override
    public void tick() {
    }
    
    @Override
    public boolean hasSoundsToStart() {
        return false;
    }
    
    @Override
    public boolean isEmpty() {
        return true;
    }
    
    @Override
    public boolean isPlaying(final long n) {
        return false;
    }
    
    @Override
    public boolean isPlaying(final String s) {
        return false;
    }
    
    @Override
    public boolean restart(final long n) {
        return false;
    }
    
    @Override
    public long playSoundLooped(final String s) {
        return 0L;
    }
    
    @Override
    public long playSoundLoopedImpl(final String s) {
        return 0L;
    }
    
    @Override
    public long playAmbientLoopedImpl(final String s) {
        return 0L;
    }
}
