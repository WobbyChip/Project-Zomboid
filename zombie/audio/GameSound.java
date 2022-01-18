// 
// Decompiled by Procyon v0.5.36
// 

package zombie.audio;

import zombie.core.Rand;
import zombie.SystemDisabler;
import java.util.ArrayList;

public final class GameSound
{
    public String name;
    public String category;
    public boolean loop;
    public boolean is3D;
    public final ArrayList<GameSoundClip> clips;
    private float userVolume;
    public MasterVolume master;
    public short reloadEpoch;
    
    public GameSound() {
        this.category = "General";
        this.loop = false;
        this.is3D = true;
        this.clips = new ArrayList<GameSoundClip>();
        this.userVolume = 1.0f;
        this.master = MasterVolume.Primary;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public boolean isLooped() {
        return this.loop;
    }
    
    public void setUserVolume(final float b) {
        this.userVolume = Math.max(0.0f, Math.min(2.0f, b));
    }
    
    public float getUserVolume() {
        if (!SystemDisabler.getEnableAdvancedSoundOptions()) {
            return 1.0f;
        }
        return this.userVolume;
    }
    
    public GameSoundClip getRandomClip() {
        return this.clips.get(Rand.Next(this.clips.size()));
    }
    
    public String getMasterName() {
        return this.master.name();
    }
    
    public void reset() {
        this.name = null;
        this.category = "General";
        this.loop = false;
        this.is3D = true;
        this.clips.clear();
        this.userVolume = 1.0f;
        this.master = MasterVolume.Primary;
        ++this.reloadEpoch;
    }
    
    public enum MasterVolume
    {
        Primary, 
        Ambient, 
        Music, 
        VehicleEngine;
        
        private static /* synthetic */ MasterVolume[] $values() {
            return new MasterVolume[] { MasterVolume.Primary, MasterVolume.Ambient, MasterVolume.Music, MasterVolume.VehicleEngine };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
