// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.audio.BaseSoundEmitter;
import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
import fmod.fmod.Audio;

public abstract class BaseSoundManager
{
    public boolean AllowMusic;
    
    public BaseSoundManager() {
        this.AllowMusic = true;
    }
    
    public abstract boolean isRemastered();
    
    public abstract void update1();
    
    public abstract void update3();
    
    public abstract void update2();
    
    public abstract void update4();
    
    public abstract void CacheSound(final String p0);
    
    public abstract void StopSound(final Audio p0);
    
    public abstract void StopMusic();
    
    public abstract void Purge();
    
    public abstract void stop();
    
    protected abstract boolean HasMusic(final Audio p0);
    
    public abstract void Update();
    
    public abstract Audio Start(final Audio p0, final float p1, final String p2);
    
    public abstract Audio PrepareMusic(final String p0);
    
    public abstract void PlayWorldSoundWav(final String p0, final IsoGridSquare p1, final float p2, final float p3, final float p4, final int p5, final boolean p6);
    
    public abstract Audio PlayWorldSoundWav(final String p0, final boolean p1, final IsoGridSquare p2, final float p3, final float p4, final float p5, final boolean p6);
    
    public abstract Audio PlayWorldSoundWav(final String p0, final IsoGridSquare p1, final float p2, final float p3, final float p4, final boolean p5);
    
    public abstract Audio PlayWorldSound(final String p0, final IsoGridSquare p1, final float p2, final float p3, final float p4, final int p5, final boolean p6);
    
    public abstract Audio PlayWorldSound(final String p0, final boolean p1, final IsoGridSquare p2, final float p3, final float p4, final float p5, final boolean p6);
    
    public abstract Audio PlayWorldSoundImpl(final String p0, final boolean p1, final int p2, final int p3, final int p4, final float p5, final float p6, final float p7, final boolean p8);
    
    public abstract Audio PlayWorldSound(final String p0, final IsoGridSquare p1, final float p2, final float p3, final float p4, final boolean p5);
    
    public abstract void update3D();
    
    public abstract Audio PlaySoundWav(final String p0, final int p1, final boolean p2, final float p3);
    
    public abstract Audio PlaySoundWav(final String p0, final boolean p1, final float p2);
    
    public abstract Audio PlaySoundWav(final String p0, final boolean p1, final float p2, final float p3);
    
    public abstract Audio PlayWorldSoundWavImpl(final String p0, final boolean p1, final IsoGridSquare p2, final float p3, final float p4, final float p5, final boolean p6);
    
    public abstract Audio PlayJukeboxSound(final String p0, final boolean p1, final float p2);
    
    public abstract Audio PlaySoundEvenSilent(final String p0, final boolean p1, final float p2);
    
    public abstract Audio PlaySound(final String p0, final boolean p1, final float p2);
    
    public abstract Audio PlaySound(final String p0, final boolean p1, final float p2, final float p3);
    
    public abstract Audio PlayMusic(final String p0, final String p1, final boolean p2, final float p3);
    
    public abstract void PlayAsMusic(final String p0, final Audio p1, final boolean p2, final float p3);
    
    public abstract void setMusicState(final String p0);
    
    public abstract void setMusicWakeState(final IsoPlayer p0, final String p1);
    
    public abstract void DoMusic(final String p0, final boolean p1);
    
    public abstract float getMusicPosition();
    
    public abstract void CheckDoMusic();
    
    public abstract void stopMusic(final String p0);
    
    public abstract void playMusicNonTriggered(final String p0, final float p1);
    
    public abstract void playAmbient(final String p0);
    
    public abstract void playMusic(final String p0);
    
    public abstract boolean isPlayingMusic();
    
    public abstract boolean IsMusicPlaying();
    
    public abstract String getCurrentMusicName();
    
    public abstract String getCurrentMusicLibrary();
    
    public abstract void PlayAsMusic(final String p0, final Audio p1, final float p2, final boolean p3);
    
    public abstract long playUISound(final String p0);
    
    public abstract boolean isPlayingUISound(final String p0);
    
    public abstract boolean isPlayingUISound(final long p0);
    
    public abstract void stopUISound(final long p0);
    
    public abstract void FadeOutMusic(final String p0, final int p1);
    
    public abstract Audio BlendThenStart(final Audio p0, final float p1, final String p2);
    
    public abstract void BlendVolume(final Audio p0, final float p1, final float p2);
    
    public abstract void BlendVolume(final Audio p0, final float p1);
    
    public abstract void setSoundVolume(final float p0);
    
    public abstract float getSoundVolume();
    
    public abstract void setAmbientVolume(final float p0);
    
    public abstract float getAmbientVolume();
    
    public abstract void setMusicVolume(final float p0);
    
    public abstract float getMusicVolume();
    
    public abstract void setVehicleEngineVolume(final float p0);
    
    public abstract float getVehicleEngineVolume();
    
    public abstract void playNightAmbient(final String p0);
    
    public abstract ArrayList<Audio> getAmbientPieces();
    
    public abstract void pauseSoundAndMusic();
    
    public abstract void resumeSoundAndMusic();
    
    public abstract void debugScriptSounds();
    
    public abstract void registerEmitter(final BaseSoundEmitter p0);
    
    public abstract void unregisterEmitter(final BaseSoundEmitter p0);
    
    public abstract boolean isListenerInRange(final float p0, final float p1, final float p2);
}
