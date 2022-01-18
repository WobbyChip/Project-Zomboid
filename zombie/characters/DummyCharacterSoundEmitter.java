// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import java.util.Iterator;
import zombie.iso.IsoObject;
import zombie.iso.IsoMovingObject;
import zombie.network.GameClient;
import zombie.core.Rand;
import java.util.HashMap;

public final class DummyCharacterSoundEmitter extends BaseCharacterSoundEmitter
{
    public float x;
    public float y;
    public float z;
    private final HashMap<Long, String> sounds;
    
    public DummyCharacterSoundEmitter(final IsoGameCharacter isoGameCharacter) {
        super(isoGameCharacter);
        this.sounds = new HashMap<Long, String>();
    }
    
    @Override
    public void register() {
    }
    
    @Override
    public void unregister() {
    }
    
    @Override
    public long playVocals(final String s) {
        return 0L;
    }
    
    @Override
    public void playFootsteps(final String s, final float n) {
    }
    
    @Override
    public long playSound(final String value) {
        final long l = Rand.Next(Integer.MAX_VALUE);
        this.sounds.put(l, value);
        if (GameClient.bClient) {
            GameClient.instance.PlaySound(value, false, this.character);
        }
        return l;
    }
    
    @Override
    public long playSound(final String s, final IsoObject isoObject) {
        return this.playSound(s);
    }
    
    @Override
    public long playSoundImpl(final String value, final IsoObject isoObject) {
        final long next = Rand.Next(Long.MAX_VALUE);
        this.sounds.put(next, value);
        return next;
    }
    
    @Override
    public void tick() {
    }
    
    @Override
    public void set(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public boolean isClear() {
        return this.sounds.isEmpty();
    }
    
    @Override
    public void setPitch(final long n, final float n2) {
    }
    
    @Override
    public void setVolume(final long n, final float n2) {
    }
    
    @Override
    public int stopSound(final long n) {
        if (GameClient.bClient) {
            GameClient.instance.StopSound(this.character, this.sounds.get(n), false);
        }
        this.sounds.remove(n);
        return 0;
    }
    
    @Override
    public void stopOrTriggerSound(final long n) {
        if (GameClient.bClient) {
            GameClient.instance.StopSound(this.character, this.sounds.get(n), true);
        }
        this.sounds.remove(n);
    }
    
    @Override
    public void stopOrTriggerSoundByName(final String s) {
        this.sounds.values().remove(s);
    }
    
    @Override
    public void stopAll() {
        if (GameClient.bClient) {
            final Iterator<String> iterator = this.sounds.values().iterator();
            while (iterator.hasNext()) {
                GameClient.instance.StopSound(this.character, iterator.next(), false);
            }
        }
        this.sounds.clear();
    }
    
    @Override
    public int stopSoundByName(final String s) {
        this.sounds.values().remove(s);
        return 0;
    }
    
    @Override
    public boolean hasSoundsToStart() {
        return false;
    }
    
    @Override
    public boolean isPlaying(final long l) {
        return this.sounds.containsKey(l);
    }
    
    @Override
    public boolean isPlaying(final String value) {
        return this.sounds.containsValue(value);
    }
    
    @Override
    public void setParameterValue(final long n, final FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_STUDIO_PARAMETER_DESCRIPTION, final float n2) {
    }
    
    public boolean hasSustainPoints(final long n) {
        return false;
    }
}
