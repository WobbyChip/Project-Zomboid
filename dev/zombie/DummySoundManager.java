// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
import fmod.fmod.Audio;
import java.util.ArrayList;

public final class DummySoundManager extends BaseSoundManager
{
    private static ArrayList<Audio> ambientPieces;
    
    @Override
    public boolean isRemastered() {
        return false;
    }
    
    @Override
    public void update1() {
    }
    
    @Override
    public void update3() {
    }
    
    @Override
    public void update2() {
    }
    
    @Override
    public void update4() {
    }
    
    @Override
    public void CacheSound(final String s) {
    }
    
    @Override
    public void StopSound(final Audio audio) {
    }
    
    @Override
    public void StopMusic() {
    }
    
    @Override
    public void Purge() {
    }
    
    @Override
    public void stop() {
    }
    
    @Override
    protected boolean HasMusic(final Audio audio) {
        return false;
    }
    
    @Override
    public void Update() {
    }
    
    @Override
    public Audio Start(final Audio audio, final float n, final String s) {
        return null;
    }
    
    @Override
    public Audio PrepareMusic(final String s) {
        return null;
    }
    
    @Override
    public void PlayWorldSoundWav(final String s, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final int n4, final boolean b) {
    }
    
    @Override
    public Audio PlayWorldSoundWav(final String s, final boolean b, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b2) {
        return null;
    }
    
    @Override
    public Audio PlayWorldSoundWav(final String s, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b) {
        return null;
    }
    
    @Override
    public Audio PlayWorldSound(final String s, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final int n4, final boolean b) {
        return null;
    }
    
    @Override
    public Audio PlayWorldSound(final String s, final boolean b, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b2) {
        return null;
    }
    
    @Override
    public Audio PlayWorldSoundImpl(final String s, final boolean b, final int n, final int n2, final int n3, final float n4, final float n5, final float n6, final boolean b2) {
        return null;
    }
    
    @Override
    public Audio PlayWorldSound(final String s, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b) {
        return null;
    }
    
    @Override
    public void update3D() {
    }
    
    @Override
    public Audio PlaySoundWav(final String s, final int n, final boolean b, final float n2) {
        return null;
    }
    
    @Override
    public Audio PlaySoundWav(final String s, final boolean b, final float n) {
        return null;
    }
    
    @Override
    public Audio PlaySoundWav(final String s, final boolean b, final float n, final float n2) {
        return null;
    }
    
    @Override
    public Audio PlayJukeboxSound(final String s, final boolean b, final float n) {
        return null;
    }
    
    @Override
    public Audio PlaySoundEvenSilent(final String s, final boolean b, final float n) {
        return null;
    }
    
    @Override
    public Audio PlaySound(final String s, final boolean b, final float n) {
        return null;
    }
    
    @Override
    public Audio PlaySound(final String s, final boolean b, final float n, final float n2) {
        return null;
    }
    
    @Override
    public Audio PlayMusic(final String s, final String s2, final boolean b, final float n) {
        return null;
    }
    
    @Override
    public void PlayAsMusic(final String s, final Audio audio, final boolean b, final float n) {
    }
    
    @Override
    public void setMusicState(final String s) {
    }
    
    @Override
    public void setMusicWakeState(final IsoPlayer isoPlayer, final String s) {
    }
    
    @Override
    public void DoMusic(final String s, final boolean b) {
    }
    
    @Override
    public float getMusicPosition() {
        return 0.0f;
    }
    
    @Override
    public void CheckDoMusic() {
    }
    
    @Override
    public void stopMusic(final String s) {
    }
    
    @Override
    public void playMusicNonTriggered(final String s, final float n) {
    }
    
    @Override
    public void playAmbient(final String s) {
    }
    
    @Override
    public void playMusic(final String s) {
    }
    
    @Override
    public boolean isPlayingMusic() {
        return false;
    }
    
    @Override
    public boolean IsMusicPlaying() {
        return false;
    }
    
    @Override
    public void PlayAsMusic(final String s, final Audio audio, final float n, final boolean b) {
    }
    
    @Override
    public long playUISound(final String s) {
        return 0L;
    }
    
    @Override
    public boolean isPlayingUISound(final String s) {
        return false;
    }
    
    @Override
    public boolean isPlayingUISound(final long n) {
        return false;
    }
    
    @Override
    public void stopUISound(final long n) {
    }
    
    @Override
    public void FadeOutMusic(final String s, final int n) {
    }
    
    @Override
    public Audio BlendThenStart(final Audio audio, final float n, final String s) {
        return null;
    }
    
    @Override
    public void BlendVolume(final Audio audio, final float n, final float n2) {
    }
    
    @Override
    public void BlendVolume(final Audio audio, final float n) {
    }
    
    @Override
    public void setSoundVolume(final float n) {
    }
    
    @Override
    public float getSoundVolume() {
        return 0.0f;
    }
    
    @Override
    public void setMusicVolume(final float n) {
    }
    
    @Override
    public float getMusicVolume() {
        return 0.0f;
    }
    
    @Override
    public void setVehicleEngineVolume(final float n) {
    }
    
    @Override
    public float getVehicleEngineVolume() {
        return 0.0f;
    }
    
    @Override
    public void setAmbientVolume(final float n) {
    }
    
    @Override
    public float getAmbientVolume() {
        return 0.0f;
    }
    
    @Override
    public void playNightAmbient(final String s) {
    }
    
    @Override
    public ArrayList<Audio> getAmbientPieces() {
        return DummySoundManager.ambientPieces;
    }
    
    @Override
    public void pauseSoundAndMusic() {
    }
    
    @Override
    public void resumeSoundAndMusic() {
    }
    
    @Override
    public void debugScriptSounds() {
    }
    
    @Override
    public void registerEmitter(final BaseSoundEmitter baseSoundEmitter) {
    }
    
    @Override
    public void unregisterEmitter(final BaseSoundEmitter baseSoundEmitter) {
    }
    
    @Override
    public boolean isListenerInRange(final float n, final float n2, final float n3) {
        return false;
    }
    
    @Override
    public Audio PlayWorldSoundWavImpl(final String s, final boolean b, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b2) {
        return null;
    }
    
    @Override
    public String getCurrentMusicName() {
        return null;
    }
    
    @Override
    public String getCurrentMusicLibrary() {
        return null;
    }
    
    static {
        DummySoundManager.ambientPieces = new ArrayList<Audio>();
    }
}
