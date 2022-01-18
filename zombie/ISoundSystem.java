// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

public interface ISoundSystem
{
    void init();
    
    void update();
    
    void purge();
    
    void fadeOutAll(final float p0);
    
    ISoundInstance playSound(final SoundFormat p0, final String p1, final String p2, final int p3, final boolean p4, final boolean p5, final float p6);
    
    ISoundInstance playSound(final SoundFormat p0, final String p1, final String p2, final int p3, final boolean p4, final boolean p5, final float p6, final float p7);
    
    ISoundInstance playSound(final SoundFormat p0, final String p1, final String p2, final int p3, final boolean p4, final boolean p5, final float p6, final float p7, final float p8);
    
    ISoundInstance playSound(final SoundFormat p0, final String p1, final String p2, final boolean p3, final boolean p4, final float p5);
    
    ISoundInstance playSound(final SoundFormat p0, final String p1, final String p2, final boolean p3, final boolean p4, final float p5, final float p6);
    
    ISoundInstance playSound(final SoundFormat p0, final String p1, final String p2, final boolean p3, final boolean p4, final float p5, final float p6, final float p7);
    
    void cacheSound(final SoundFormat p0, final String p1, final String p2, final int p3);
    
    void cacheSound(final SoundFormat p0, final String p1, final String p2);
    
    void clearSoundCache();
    
    int countInstances(final String p0);
    
    void setInstanceLimit(final String p0, final int p1, final InstanceFailAction p2);
    
    public enum SoundFormat
    {
        Ogg, 
        Wav;
        
        private static /* synthetic */ SoundFormat[] $values() {
            return new SoundFormat[] { SoundFormat.Ogg, SoundFormat.Wav };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public enum InstanceFailAction
    {
        FailToPlay, 
        StopOldest, 
        StopRandom;
        
        private static /* synthetic */ InstanceFailAction[] $values() {
            return new InstanceFailAction[] { InstanceFailAction.FailToPlay, InstanceFailAction.StopOldest, InstanceFailAction.StopRandom };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public interface ISoundInstance
    {
        boolean isStreamed();
        
        boolean isLooped();
        
        boolean isPlaying();
        
        int countInstances();
        
        void setLooped(final boolean p0);
        
        void pause();
        
        void stop();
        
        void play();
        
        void blendVolume(final float p0, final float p1, final boolean p2);
        
        void setVolume(final float p0);
        
        float getVolume();
        
        void setPanning(final float p0);
        
        float getPanning();
        
        void setPitch(final float p0);
        
        float getPitch();
        
        boolean disposed();
    }
}
