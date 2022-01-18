// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio;

import zombie.core.Core;
import zombie.SoundManager;
import fmod.fmod.FMOD_STUDIO_EVENT_DESCRIPTION;

public final class GameSoundClip
{
    public static short INIT_FLAG_DISTANCE_MIN;
    public static short INIT_FLAG_DISTANCE_MAX;
    public final GameSound gameSound;
    public String event;
    public FMOD_STUDIO_EVENT_DESCRIPTION eventDescription;
    public String file;
    public float volume;
    public float pitch;
    public float distanceMin;
    public float distanceMax;
    public float reverbMaxRange;
    public float reverbFactor;
    public int priority;
    public short initFlags;
    public short reloadEpoch;
    
    public GameSoundClip(final GameSound gameSound) {
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.distanceMin = 10.0f;
        this.distanceMax = 10.0f;
        this.reverbMaxRange = 10.0f;
        this.reverbFactor = 0.0f;
        this.priority = 5;
        this.initFlags = 0;
        this.gameSound = gameSound;
        this.reloadEpoch = gameSound.reloadEpoch;
    }
    
    public String getEvent() {
        return this.event;
    }
    
    public String getFile() {
        return this.file;
    }
    
    public float getVolume() {
        return this.volume;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public boolean hasMinDistance() {
        return (this.initFlags & GameSoundClip.INIT_FLAG_DISTANCE_MIN) != 0x0;
    }
    
    public boolean hasMaxDistance() {
        return (this.initFlags & GameSoundClip.INIT_FLAG_DISTANCE_MAX) != 0x0;
    }
    
    public float getMinDistance() {
        return this.distanceMin;
    }
    
    public float getMaxDistance() {
        return this.distanceMax;
    }
    
    public float getEffectiveVolume() {
        float n = 1.0f;
        switch (this.gameSound.master) {
            case Primary: {
                n = SoundManager.instance.getSoundVolume();
                break;
            }
            case Ambient: {
                n = SoundManager.instance.getAmbientVolume();
                break;
            }
            case Music: {
                n = SoundManager.instance.getMusicVolume();
                break;
            }
            case VehicleEngine: {
                n = SoundManager.instance.getVehicleEngineVolume();
                break;
            }
        }
        return n * this.volume * this.gameSound.getUserVolume();
    }
    
    public float getEffectiveVolumeInMenu() {
        float n = 1.0f;
        switch (this.gameSound.master) {
            case Primary: {
                n = Core.getInstance().getOptionSoundVolume() / 10.0f;
                break;
            }
            case Ambient: {
                n = Core.getInstance().getOptionAmbientVolume() / 10.0f;
                break;
            }
            case Music: {
                n = Core.getInstance().getOptionMusicVolume() / 10.0f;
                break;
            }
            case VehicleEngine: {
                n = Core.getInstance().getOptionVehicleEngineVolume() / 10.0f;
                break;
            }
        }
        return n * this.volume * this.gameSound.getUserVolume();
    }
    
    public GameSoundClip checkReloaded() {
        if (this.reloadEpoch == this.gameSound.reloadEpoch) {
            return this;
        }
        GameSoundClip gameSoundClip = null;
        for (int i = 0; i < this.gameSound.clips.size(); ++i) {
            final GameSoundClip gameSoundClip2 = this.gameSound.clips.get(i);
            if (gameSoundClip2 == this) {
                return this;
            }
            if (gameSoundClip2.event != null && gameSoundClip2.event.equals(this.event)) {
                gameSoundClip = gameSoundClip2;
            }
            if (gameSoundClip2.file != null && gameSoundClip2.file.equals(this.file)) {
                gameSoundClip = gameSoundClip2;
            }
        }
        if (gameSoundClip == null) {
            this.reloadEpoch = this.gameSound.reloadEpoch;
            return this;
        }
        return gameSoundClip;
    }
    
    public boolean hasSustainPoints() {
        return this.eventDescription != null && this.eventDescription.bHasSustainPoints;
    }
    
    static {
        GameSoundClip.INIT_FLAG_DISTANCE_MIN = 1;
        GameSoundClip.INIT_FLAG_DISTANCE_MAX = 2;
    }
}
