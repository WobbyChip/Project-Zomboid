// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.objects;

import zombie.WorldSoundManager;
import zombie.core.Core;
import zombie.characters.IsoPlayer;
import zombie.SoundManager;
import zombie.core.Rand;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoCell;
import fmod.fmod.Audio;
import zombie.iso.IsoObject;

public class IsoJukebox extends IsoObject
{
    private Audio JukeboxTrack;
    private boolean IsPlaying;
    private float MusicRadius;
    private boolean Activated;
    private int WorldSoundPulseRate;
    private int WorldSoundPulseDelay;
    
    public IsoJukebox(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final IsoSprite isoSprite) {
        super(isoCell, isoGridSquare, isoSprite);
        this.JukeboxTrack = null;
        this.IsPlaying = false;
        this.MusicRadius = 30.0f;
        this.Activated = false;
        this.WorldSoundPulseRate = 150;
        this.WorldSoundPulseDelay = 0;
    }
    
    @Override
    public String getObjectName() {
        return "Jukebox";
    }
    
    public IsoJukebox(final IsoCell isoCell) {
        super(isoCell);
        this.JukeboxTrack = null;
        this.IsPlaying = false;
        this.MusicRadius = 30.0f;
        this.Activated = false;
        this.WorldSoundPulseRate = 150;
        this.WorldSoundPulseDelay = 0;
    }
    
    public IsoJukebox(final IsoCell isoCell, final IsoGridSquare isoGridSquare, final String s) {
        super(isoCell, isoGridSquare, s);
        this.JukeboxTrack = null;
        this.IsPlaying = false;
        this.MusicRadius = 30.0f;
        this.Activated = false;
        this.WorldSoundPulseRate = 150;
        this.WorldSoundPulseDelay = 0;
        this.JukeboxTrack = null;
        this.IsPlaying = false;
        this.Activated = false;
        this.WorldSoundPulseDelay = 0;
    }
    
    @Override
    public void addToWorld() {
        super.addToWorld();
        this.getCell().addToStaticUpdaterObjectList(this);
    }
    
    public void SetPlaying(final boolean isPlaying) {
        if (this.IsPlaying == isPlaying) {
            return;
        }
        this.IsPlaying = isPlaying;
        if (this.IsPlaying && this.JukeboxTrack == null) {
            String s = null;
            switch (Rand.Next(4)) {
                case 0: {
                    s = "paws1";
                    break;
                }
                case 1: {
                    s = "paws2";
                    break;
                }
                case 2: {
                    s = "paws3";
                    break;
                }
                case 3: {
                    s = "paws4";
                    break;
                }
            }
            this.JukeboxTrack = SoundManager.instance.PlaySound(s, false, 0.0f);
        }
    }
    
    @Override
    public boolean onMouseLeftClick(final int n, final int n2) {
        final IsoPlayer instance = IsoPlayer.getInstance();
        if (instance == null || instance.isDead()) {
            return false;
        }
        if (IsoPlayer.getInstance().getCurrentSquare() == null) {
            return false;
        }
        if (Math.abs(this.square.getX() - IsoPlayer.getInstance().getCurrentSquare().getX()) + Math.abs(this.square.getY() - IsoPlayer.getInstance().getCurrentSquare().getY() + Math.abs(this.square.getZ() - IsoPlayer.getInstance().getCurrentSquare().getZ())) < 4) {
            if (!this.Activated) {
                if (Core.NumJukeBoxesActive < Core.MaxJukeBoxesActive) {
                    this.WorldSoundPulseDelay = 0;
                    this.SetPlaying(this.Activated = true);
                    ++Core.NumJukeBoxesActive;
                }
            }
            else {
                this.WorldSoundPulseDelay = 0;
                this.SetPlaying(false);
                this.Activated = false;
                if (this.JukeboxTrack != null) {
                    SoundManager.instance.StopSound(this.JukeboxTrack);
                    this.JukeboxTrack.stop();
                    this.JukeboxTrack = null;
                }
                --Core.NumJukeBoxesActive;
            }
        }
        return true;
    }
    
    @Override
    public void update() {
        if (IsoPlayer.getInstance() == null) {
            return;
        }
        if (IsoPlayer.getInstance().getCurrentSquare() == null) {
            return;
        }
        if (this.Activated) {
            float n = 0.0f;
            final int n2 = Math.abs(this.square.getX() - IsoPlayer.getInstance().getCurrentSquare().getX()) + Math.abs(this.square.getY() - IsoPlayer.getInstance().getCurrentSquare().getY() + Math.abs(this.square.getZ() - IsoPlayer.getInstance().getCurrentSquare().getZ()));
            if (n2 < this.MusicRadius) {
                this.SetPlaying(true);
                n = (this.MusicRadius - n2) / this.MusicRadius;
            }
            if (this.JukeboxTrack != null) {
                if (n + 0.2f > 1.0f) {}
                SoundManager.instance.BlendVolume(this.JukeboxTrack, n);
                if (this.WorldSoundPulseDelay > 0) {
                    --this.WorldSoundPulseDelay;
                }
                if (this.WorldSoundPulseDelay == 0) {
                    WorldSoundManager.instance.addSound(IsoPlayer.getInstance(), this.square.getX(), this.square.getY(), this.square.getZ(), 70, 70, true);
                    this.WorldSoundPulseDelay = this.WorldSoundPulseRate;
                }
                if (!this.JukeboxTrack.isPlaying()) {
                    this.WorldSoundPulseDelay = 0;
                    this.SetPlaying(false);
                    this.Activated = false;
                    if (this.JukeboxTrack != null) {
                        SoundManager.instance.StopSound(this.JukeboxTrack);
                        this.JukeboxTrack.stop();
                        this.JukeboxTrack = null;
                    }
                    --Core.NumJukeBoxesActive;
                }
            }
        }
    }
}
