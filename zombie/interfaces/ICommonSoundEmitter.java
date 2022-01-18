// 
// Decompiled by Procyon v0.5.36
// 

package zombie.interfaces;

public interface ICommonSoundEmitter
{
    void setPos(final float p0, final float p1, final float p2);
    
    long playSound(final String p0);
    
    @Deprecated
    long playSound(final String p0, final boolean p1);
    
    void tick();
    
    boolean isEmpty();
    
    void setPitch(final long p0, final float p1);
    
    void setVolume(final long p0, final float p1);
    
    boolean hasSustainPoints(final long p0);
    
    void triggerCue(final long p0);
    
    int stopSound(final long p0);
    
    void stopOrTriggerSound(final long p0);
    
    void stopOrTriggerSoundByName(final String p0);
    
    boolean isPlaying(final long p0);
    
    boolean isPlaying(final String p0);
}
