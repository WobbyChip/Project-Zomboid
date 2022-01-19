// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import zombie.audio.GameSound;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.characters.IsoPlayer;
import fmod.fmod.FMOD_STUDIO_PLAYBACK_STATE;
import zombie.GameSounds;
import zombie.SoundManager;
import fmod.fmod.FMODManager;
import zombie.core.Core;
import fmod.javafmod;
import zombie.network.GameServer;
import zombie.WorldSoundManager;
import zombie.network.GameClient;
import zombie.GameTime;
import fmod.fmod.FMOD_STUDIO_EVENT_DESCRIPTION;

public class Alarm
{
    protected static long inst;
    protected static FMOD_STUDIO_EVENT_DESCRIPTION event;
    public boolean finished;
    private int x;
    private int y;
    private float volume;
    private float occlusion;
    private float endGameTime;
    
    public Alarm(final int x, final int y) {
        this.finished = false;
        this.x = x;
        this.y = y;
        this.endGameTime = (float)GameTime.instance.getWorldAgeHours() + 49 / 3600.0f * (1440.0f / GameTime.instance.getMinutesPerDay());
    }
    
    public void update() {
        if (!GameClient.bClient) {
            WorldSoundManager.instance.addSound(this, this.x, this.y, 0, 600, 600);
        }
        if (!GameServer.bServer) {
            this.updateSound();
            if (GameTime.getInstance().getWorldAgeHours() >= this.endGameTime) {
                if (Alarm.inst != 0L) {
                    javafmod.FMOD_Studio_EventInstance_Stop(Alarm.inst, false);
                    Alarm.inst = 0L;
                }
                this.finished = true;
            }
        }
    }
    
    protected void updateSound() {
        if (GameServer.bServer || Core.SoundDisabled || this.finished) {
            return;
        }
        if (FMODManager.instance.getNumListeners() == 0) {
            return;
        }
        if (Alarm.inst == 0L) {
            Alarm.event = FMODManager.instance.getEventDescription("event:/Meta/HouseAlarm");
            if (Alarm.event != null) {
                javafmod.FMOD_Studio_LoadEventSampleData(Alarm.event.address);
                Alarm.inst = javafmod.FMOD_Studio_System_CreateEventInstance(Alarm.event.address);
            }
        }
        if (Alarm.inst > 0L) {
            float soundVolume = SoundManager.instance.getSoundVolume();
            final GameSound sound = GameSounds.getSound("HouseAlarm");
            if (sound != null) {
                soundVolume *= sound.getUserVolume();
            }
            if (soundVolume != this.volume) {
                javafmod.FMOD_Studio_EventInstance_SetVolume(Alarm.inst, soundVolume);
                this.volume = soundVolume;
            }
            javafmod.FMOD_Studio_EventInstance3D(Alarm.inst, (float)this.x, (float)this.y, 0.0f);
            if (javafmod.FMOD_Studio_GetPlaybackState(Alarm.inst) != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_PLAYING.index && javafmod.FMOD_Studio_GetPlaybackState(Alarm.inst) != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STARTING.index) {
                if (javafmod.FMOD_Studio_GetPlaybackState(Alarm.inst) == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index) {
                    this.finished = true;
                    return;
                }
                javafmod.FMOD_Studio_StartEvent(Alarm.inst);
                System.out.println(javafmod.FMOD_Studio_GetPlaybackState(Alarm.inst));
            }
            float occlusion = 0.0f;
            if (IsoPlayer.numPlayers == 1) {
                final IsoGridSquare currentSquare = IsoPlayer.getInstance().getCurrentSquare();
                if (currentSquare != null && !currentSquare.Is(IsoFlagType.exterior)) {
                    occlusion = 0.2f;
                    final IsoGridSquare gridSquare = IsoWorld.instance.getCell().getGridSquare(this.x, this.y, 0);
                    if (gridSquare != null && gridSquare.getBuilding() == currentSquare.getBuilding()) {
                        occlusion = 0.0f;
                    }
                }
            }
            if (this.occlusion != occlusion) {
                this.occlusion = occlusion;
                javafmod.FMOD_Studio_EventInstance_SetParameterByName(Alarm.inst, "Occlusion", this.occlusion);
            }
        }
    }
}
