// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.StorySounds;

import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.audio.GameSound;
import zombie.characters.IsoPlayer;
import zombie.SoundManager;
import fmod.fmod.FMODManager;
import zombie.GameSounds;
import fmod.javafmod;
import zombie.iso.Vector2;
import java.util.ArrayList;
import java.util.Stack;

public final class StoryEmitter
{
    public int max;
    public float volumeMod;
    public boolean coordinate3D;
    public Stack<Sound> SoundStack;
    public ArrayList<Sound> Instances;
    public ArrayList<Sound> ToStart;
    private Vector2 soundVect;
    private Vector2 playerVect;
    
    public StoryEmitter() {
        this.max = -1;
        this.volumeMod = 1.0f;
        this.coordinate3D = true;
        this.SoundStack = new Stack<Sound>();
        this.Instances = new ArrayList<Sound>();
        this.ToStart = new ArrayList<Sound>();
        this.soundVect = new Vector2();
        this.playerVect = new Vector2();
    }
    
    public int stopSound(final long n) {
        return javafmod.FMOD_Channel_Stop(n);
    }
    
    public long playSound(final String s, final float n, final float x, final float y, final float z, final float minRange, final float maxRange) {
        if (this.max != -1 && this.max <= this.Instances.size() + this.ToStart.size()) {
            return 0L;
        }
        final GameSound sound = GameSounds.getSound(s);
        if (sound == null) {
            return 0L;
        }
        sound.getRandomClip();
        final long loadSound = FMODManager.instance.loadSound(s);
        if (loadSound == 0L) {
            return 0L;
        }
        Sound e;
        if (this.SoundStack.isEmpty()) {
            e = new Sound();
        }
        else {
            e = this.SoundStack.pop();
        }
        e.minRange = minRange;
        e.maxRange = maxRange;
        e.x = x;
        e.y = y;
        e.z = z;
        e.volume = SoundManager.instance.getSoundVolume() * n * this.volumeMod;
        e.sound = loadSound;
        e.channel = javafmod.FMOD_System_PlaySound(loadSound, true);
        this.ToStart.add(e);
        javafmod.FMOD_Channel_Set3DAttributes(e.channel, e.x - IsoPlayer.getInstance().x, e.y - IsoPlayer.getInstance().y, e.z - IsoPlayer.getInstance().z, 0.0f, 0.0f, 0.0f);
        javafmod.FMOD_Channel_Set3DOcclusion(e.channel, 1.0f, 1.0f);
        if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance().Traits.Deaf.isSet()) {
            javafmod.FMOD_Channel_SetVolume(e.channel, 0.0f);
        }
        else {
            javafmod.FMOD_Channel_SetVolume(e.channel, e.volume);
        }
        return e.channel;
    }
    
    public void tick() {
        for (int i = 0; i < this.ToStart.size(); ++i) {
            final Sound e = this.ToStart.get(i);
            javafmod.FMOD_Channel_SetPaused(e.channel, false);
            this.Instances.add(e);
        }
        this.ToStart.clear();
        for (int j = 0; j < this.Instances.size(); ++j) {
            final Sound sound = this.Instances.get(j);
            if (!javafmod.FMOD_Channel_IsPlaying(sound.channel)) {
                this.SoundStack.push(sound);
                this.Instances.remove(sound);
                --j;
            }
            else {
                float n = IsoUtils.DistanceManhatten(sound.x, sound.y, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, sound.z, IsoPlayer.getInstance().z) / sound.maxRange;
                if (n > 1.0f) {
                    n = 1.0f;
                }
                if (!this.coordinate3D) {
                    javafmod.FMOD_Channel_Set3DAttributes(sound.channel, Math.abs(sound.x - IsoPlayer.getInstance().x), Math.abs(sound.y - IsoPlayer.getInstance().y), Math.abs(sound.z - IsoPlayer.getInstance().z), 0.0f, 0.0f, 0.0f);
                }
                else {
                    javafmod.FMOD_Channel_Set3DAttributes(sound.channel, Math.abs(sound.x - IsoPlayer.getInstance().x), Math.abs(sound.z - IsoPlayer.getInstance().z), Math.abs(sound.y - IsoPlayer.getInstance().y), 0.0f, 0.0f, 0.0f);
                }
                javafmod.FMOD_System_SetReverbDefault(0, FMODManager.FMOD_PRESET_MOUNTAINS);
                javafmod.FMOD_Channel_SetReverbProperties(sound.channel, 0, 1.0f);
                javafmod.FMOD_Channel_Set3DMinMaxDistance(sound.channel, sound.minRange, sound.maxRange);
                final IsoGridSquare currentSquare = IsoPlayer.getInstance().getCurrentSquare();
                this.soundVect.set(sound.x, sound.y);
                this.playerVect.set(IsoPlayer.getInstance().x, IsoPlayer.getInstance().y);
                final float n2 = (float)Math.toDegrees(this.playerVect.angleTo(this.soundVect));
                float n3 = (float)Math.toDegrees(IsoPlayer.getInstance().getForwardDirection().getDirectionNeg());
                if (n3 >= 0.0f && n3 <= 90.0f) {
                    n3 = -90.0f - n3;
                }
                else if (n3 > 90.0f && n3 <= 180.0f) {
                    n3 = 90.0f + (180.0f - n3);
                }
                else if (n3 < 0.0f && n3 >= -90.0f) {
                    n3 = 0.0f - (90.0f + n3);
                }
                else if (n3 < 0.0f && n3 >= -180.0f) {
                    n3 = 90.0f - (180.0f + n3);
                }
                final float n4 = Math.abs(n2 - n3) % 360.0f;
                final float n5 = (180.0f - ((n4 > 180.0f) ? (360.0f - n4) : n4)) / 180.0f;
                float n6 = n / 0.4f;
                if (n6 > 1.0f) {
                    n6 = 1.0f;
                }
                float n7 = 0.85f * n6 * n5;
                float n8 = 0.85f * n6 * n5;
                if (currentSquare.getRoom() != null) {
                    n7 = 0.75f + 0.1f * n6 + 0.1f * n5;
                    n8 = 0.75f + 0.1f * n6 + 0.1f * n5;
                }
                javafmod.FMOD_Channel_Set3DOcclusion(sound.channel, n7, n8);
            }
        }
    }
    
    public static final class Sound
    {
        public long sound;
        public long channel;
        public float volume;
        public float x;
        public float y;
        public float z;
        public float minRange;
        public float maxRange;
    }
}
