// 
// Decompiled by Procyon v0.5.36
// 

package zombie.characters;

import fmod.fmod.IFMODParameterUpdater;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import zombie.debug.DebugOptions;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import fmod.fmod.FMODManager;
import fmod.fmod.FMODVoice;
import zombie.iso.IsoObject;
import fmod.fmod.FMODSoundBank;
import zombie.network.GameServer;
import zombie.audio.BaseSoundEmitter;
import zombie.SoundManager;
import fmod.fmod.EmitterType;
import fmod.fmod.FMODSoundEmitter;
import zombie.interfaces.ICommonSoundEmitter;

public final class CharacterSoundEmitter extends BaseCharacterSoundEmitter implements ICommonSoundEmitter
{
    float currentPriority;
    final FMODSoundEmitter vocals;
    final FMODSoundEmitter footsteps;
    final FMODSoundEmitter extra;
    private long footstep1;
    private long footstep2;
    
    public CharacterSoundEmitter(final IsoGameCharacter parameterUpdater) {
        super(parameterUpdater);
        this.vocals = new FMODSoundEmitter();
        this.footsteps = new FMODSoundEmitter();
        this.extra = new FMODSoundEmitter();
        this.footstep1 = 0L;
        this.footstep2 = 0L;
        this.vocals.emitterType = EmitterType.Voice;
        this.vocals.parent = this.character;
        this.vocals.parameterUpdater = (IFMODParameterUpdater)parameterUpdater;
        this.footsteps.emitterType = EmitterType.Footstep;
        this.footsteps.parent = this.character;
        this.footsteps.parameterUpdater = (IFMODParameterUpdater)parameterUpdater;
        this.extra.emitterType = EmitterType.Extra;
        this.extra.parent = this.character;
        this.extra.parameterUpdater = (IFMODParameterUpdater)parameterUpdater;
    }
    
    @Override
    public void register() {
        SoundManager.instance.registerEmitter((BaseSoundEmitter)this.vocals);
        SoundManager.instance.registerEmitter((BaseSoundEmitter)this.footsteps);
        SoundManager.instance.registerEmitter((BaseSoundEmitter)this.extra);
    }
    
    @Override
    public void unregister() {
        SoundManager.instance.unregisterEmitter((BaseSoundEmitter)this.vocals);
        SoundManager.instance.unregisterEmitter((BaseSoundEmitter)this.footsteps);
        SoundManager.instance.unregisterEmitter((BaseSoundEmitter)this.extra);
    }
    
    @Override
    public long playVocals(final String s) {
        if (GameServer.bServer) {
            return 0L;
        }
        final FMODVoice voice = FMODSoundBank.instance.getVoice(s);
        if (voice == null) {
            return this.vocals.playSoundImpl(s, false, (IsoObject)null);
        }
        final float priority = voice.priority;
        final long playSound = this.vocals.playSound(voice.sound, (IsoObject)this.character);
        this.currentPriority = priority;
        return playSound;
    }
    
    footstep getFootstepToPlay() {
        if (FMODManager.instance.getNumListeners() == 1) {
            int i = 0;
            while (i < IsoPlayer.numPlayers) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null && isoPlayer != this.character && !isoPlayer.Traits.Deaf.isSet()) {
                    if ((int)isoPlayer.getZ() < (int)this.character.getZ()) {
                        return footstep.upstairs;
                    }
                    break;
                }
                else {
                    ++i;
                }
            }
        }
        final IsoObject floor = this.character.getCurrentSquare().getFloor();
        if (floor == null || floor.getSprite() == null || floor.getSprite().getName() == null) {
            return footstep.concrete;
        }
        final String name = floor.getSprite().getName();
        if (name.endsWith("blends_natural_01_5") || name.endsWith("blends_natural_01_6") || name.endsWith("blends_natural_01_7") || name.endsWith("blends_natural_01_0")) {
            return footstep.gravel;
        }
        if (name.endsWith("blends_street_01_48") || name.endsWith("blends_street_01_53") || name.endsWith("blends_street_01_54") || name.endsWith("blends_street_01_55")) {
            return footstep.gravel;
        }
        if (name.startsWith("blends_natural_01")) {
            return footstep.grass;
        }
        if (name.startsWith("floors_interior_tilesandwood_01_")) {
            final int int1 = Integer.parseInt(name.replaceFirst("floors_interior_tilesandwood_01_", ""));
            if (int1 > 40 && int1 < 48) {
                return footstep.wood;
            }
            return footstep.concrete;
        }
        else {
            if (name.startsWith("carpentry_02_")) {
                return footstep.wood;
            }
            if (name.startsWith("floors_interior_carpet_")) {
                return footstep.wood;
            }
            return footstep.concrete;
        }
    }
    
    @Override
    public void playFootsteps(final String s, final float n) {
        if (GameServer.bServer) {
            return;
        }
        int playing = this.footsteps.isPlaying(this.footstep1) ? 1 : 0;
        final boolean playing2 = this.footsteps.isPlaying(this.footstep2);
        if (playing != 0 && playing2) {
            final long footstep1 = this.footstep1;
            this.footstep1 = this.footstep2;
            this.footstep2 = footstep1;
            if (this.footsteps.restart(this.footstep2)) {
                return;
            }
            this.footsteps.stopSound(this.footstep2);
            this.footstep2 = 0L;
        }
        else if (playing2) {
            this.footstep1 = this.footstep2;
            this.footstep2 = 0L;
            playing = 1;
        }
        final long playSoundImpl = this.footsteps.playSoundImpl(s, false, (IsoObject)null);
        if (playing == 0) {
            this.footstep1 = playSoundImpl;
        }
        else {
            this.footstep2 = playSoundImpl;
        }
    }
    
    @Override
    public long playSound(final String s) {
        if (DebugLog.isEnabled(DebugType.Sound)) {
            DebugLog.Sound.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.character.isZombie() ? " for zombie" : " for player"));
        }
        if (this.character.isInvisible() && !DebugOptions.instance.Character.Debug.PlaySoundWhenInvisible.getValue()) {
            return 0L;
        }
        return this.extra.playSound(s);
    }
    
    @Override
    public long playSound(final String s, final boolean b) {
        if (DebugLog.isEnabled(DebugType.Sound)) {
            DebugLog.Sound.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.character.isZombie() ? " for zombie" : " for player"));
        }
        return this.extra.playSound(s, b);
    }
    
    @Override
    public long playSound(final String s, final IsoObject isoObject) {
        if (DebugLog.isEnabled(DebugType.Sound)) {
            DebugLog.Sound.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.character.isZombie() ? " for zombie" : " for player"));
        }
        if (GameServer.bServer) {
            return 0L;
        }
        return this.extra.playSound(s, isoObject);
    }
    
    @Override
    public long playSoundImpl(final String s, final IsoObject isoObject) {
        if (DebugLog.isEnabled(DebugType.Sound)) {
            DebugLog.Sound.debugln(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, this.character.isZombie() ? " for zombie" : " for player"));
        }
        return this.extra.playSoundImpl(s, false, isoObject);
    }
    
    @Override
    public void tick() {
        this.vocals.tick();
        this.footsteps.tick();
        this.extra.tick();
    }
    
    @Override
    public void setPos(final float n, final float n2, final float n3) {
        this.set(n, n2, n3);
    }
    
    @Override
    public void set(final float x, final float y, final float z) {
        final FMODSoundEmitter vocals = this.vocals;
        final FMODSoundEmitter footsteps = this.footsteps;
        this.extra.x = x;
        footsteps.x = x;
        vocals.x = x;
        final FMODSoundEmitter vocals2 = this.vocals;
        final FMODSoundEmitter footsteps2 = this.footsteps;
        this.extra.y = y;
        footsteps2.y = y;
        vocals2.y = y;
        final FMODSoundEmitter vocals3 = this.vocals;
        final FMODSoundEmitter footsteps3 = this.footsteps;
        this.extra.z = z;
        footsteps3.z = z;
        vocals3.z = z;
    }
    
    @Override
    public boolean isEmpty() {
        return this.isClear();
    }
    
    @Override
    public boolean isClear() {
        return this.vocals.isEmpty() && this.footsteps.isEmpty() && this.extra.isEmpty();
    }
    
    @Override
    public void setPitch(final long n, final float n2) {
        this.extra.setPitch(n, n2);
        this.footsteps.setPitch(n, n2);
        this.vocals.setPitch(n, n2);
    }
    
    @Override
    public void setVolume(final long n, final float n2) {
        this.extra.setVolume(n, n2);
        this.footsteps.setVolume(n, n2);
        this.vocals.setVolume(n, n2);
    }
    
    @Override
    public boolean hasSustainPoints(final long n) {
        if (this.extra.isPlaying(n)) {
            return this.extra.hasSustainPoints(n);
        }
        if (this.footsteps.isPlaying(n)) {
            return this.footsteps.hasSustainPoints(n);
        }
        return this.vocals.isPlaying(n) && this.vocals.hasSustainPoints(n);
    }
    
    @Override
    public void triggerCue(final long n) {
        if (this.extra.isPlaying(n)) {
            this.extra.triggerCue(n);
        }
        else if (this.footsteps.isPlaying(n)) {
            this.footsteps.triggerCue(n);
        }
        else if (this.vocals.isPlaying(n)) {
            this.vocals.triggerCue(n);
        }
    }
    
    @Override
    public int stopSound(final long n) {
        this.extra.stopSound(n);
        this.footsteps.stopSound(n);
        this.vocals.stopSound(n);
        return 0;
    }
    
    @Override
    public void stopOrTriggerSound(final long n) {
        this.extra.stopOrTriggerSound(n);
        this.footsteps.stopOrTriggerSound(n);
        this.vocals.stopOrTriggerSound(n);
    }
    
    @Override
    public void stopOrTriggerSoundByName(final String s) {
        this.extra.stopOrTriggerSoundByName(s);
        this.footsteps.stopOrTriggerSoundByName(s);
        this.vocals.stopOrTriggerSoundByName(s);
    }
    
    @Override
    public void stopAll() {
        this.extra.stopAll();
        this.footsteps.stopAll();
        this.vocals.stopAll();
    }
    
    @Override
    public int stopSoundByName(final String s) {
        this.extra.stopSoundByName(s);
        this.footsteps.stopSoundByName(s);
        this.vocals.stopSoundByName(s);
        return 0;
    }
    
    @Override
    public boolean hasSoundsToStart() {
        return this.extra.hasSoundsToStart() || this.footsteps.hasSoundsToStart() || this.vocals.hasSoundsToStart();
    }
    
    @Override
    public boolean isPlaying(final long n) {
        return this.extra.isPlaying(n) || this.footsteps.isPlaying(n) || this.vocals.isPlaying(n);
    }
    
    @Override
    public boolean isPlaying(final String s) {
        return this.extra.isPlaying(s) || this.footsteps.isPlaying(s) || this.vocals.isPlaying(s);
    }
    
    @Override
    public void setParameterValue(final long n, final FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_STUDIO_PARAMETER_DESCRIPTION, final float n2) {
        this.extra.setParameterValue(n, fmod_STUDIO_PARAMETER_DESCRIPTION, n2);
    }
    
    enum footstep
    {
        upstairs, 
        grass, 
        wood, 
        concrete, 
        gravel, 
        snow;
        
        private static /* synthetic */ footstep[] $values() {
            return new footstep[] { footstep.upstairs, footstep.grass, footstep.wood, footstep.concrete, footstep.gravel, footstep.snow };
        }
        
        static {
            $VALUES = $values();
        }
    }
}
