// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import fmod.fmod.FMOD_STUDIO_EVENT_CALLBACK_TYPE;
import zombie.iso.IsoGridSquare;
import fmod.fmod.FMODAudio;
import zombie.iso.IsoWorld;
import zombie.util.StringUtils;
import zombie.iso.IsoUtils;
import zombie.characters.IsoPlayer;
import zombie.network.GameServer;
import java.util.Iterator;
import zombie.scripting.objects.ScriptModule;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import fmod.fmod.FMODManager;
import zombie.network.GameClient;
import java.util.Arrays;
import fmod.fmod.FMOD_STUDIO_PLAYBACK_STATE;
import fmod.javafmodJNI;
import zombie.audio.GameSound;
import fmod.javafmod;
import zombie.iso.IsoObject;
import zombie.core.Rand;
import zombie.core.Core;
import zombie.audio.FMODParameter;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import java.util.BitSet;
import zombie.audio.GameSoundClip;
import zombie.gameStates.MainScreenState;
import zombie.debug.DebugLog;
import fmod.fmod.FMOD_STUDIO_EVENT_CALLBACK;
import zombie.audio.BaseSoundEmitter;
import java.util.HashSet;
import fmod.fmod.Audio;
import java.util.ArrayList;
import fmod.fmod.FMODSoundEmitter;
import zombie.audio.FMODParameterList;
import zombie.audio.parameters.ParameterMusicZombiesVisible;
import zombie.audio.parameters.ParameterMusicZombiesTargeting;
import zombie.audio.parameters.ParameterMusicWakeState;
import zombie.audio.parameters.ParameterMusicState;
import zombie.audio.parameters.ParameterMusicLibrary;
import fmod.fmod.IFMODParameterUpdater;

public final class SoundManager extends BaseSoundManager implements IFMODParameterUpdater
{
    public float SoundVolume;
    public float MusicVolume;
    public float AmbientVolume;
    public float VehicleEngineVolume;
    private final ParameterMusicLibrary parameterMusicLibrary;
    private final ParameterMusicState parameterMusicState;
    private final ParameterMusicWakeState parameterMusicWakeState;
    private final ParameterMusicZombiesTargeting parameterMusicZombiesTargeting;
    private final ParameterMusicZombiesVisible parameterMusicZombiesVisible;
    private final FMODParameterList fmodParameters;
    private boolean initialized;
    private long inGameGroupBus;
    private long musicGroupBus;
    private FMODSoundEmitter musicEmitter;
    private long musicCombinedEvent;
    private FMODSoundEmitter uiEmitter;
    private final Music music;
    public ArrayList<Audio> ambientPieces;
    private boolean muted;
    private long[] bankList;
    private long[] eventDescList;
    private long[] eventInstList;
    private long[] pausedEventInstances;
    private float[] pausedEventVolumes;
    private int pausedEventCount;
    private final HashSet<BaseSoundEmitter> emitters;
    private static ArrayList<AmbientSoundEffect> ambientSoundEffects;
    public static BaseSoundManager instance;
    private String currentMusicName;
    private String currentMusicLibrary;
    private final FMOD_STUDIO_EVENT_CALLBACK musicEventCallback;
    
    public SoundManager() {
        this.SoundVolume = 0.8f;
        this.MusicVolume = 0.36f;
        this.AmbientVolume = 0.8f;
        this.VehicleEngineVolume = 0.5f;
        this.parameterMusicLibrary = new ParameterMusicLibrary();
        this.parameterMusicState = new ParameterMusicState();
        this.parameterMusicWakeState = new ParameterMusicWakeState();
        this.parameterMusicZombiesTargeting = new ParameterMusicZombiesTargeting();
        this.parameterMusicZombiesVisible = new ParameterMusicZombiesVisible();
        this.fmodParameters = new FMODParameterList();
        this.initialized = false;
        this.inGameGroupBus = 0L;
        this.musicGroupBus = 0L;
        this.musicEmitter = null;
        this.musicCombinedEvent = 0L;
        this.uiEmitter = null;
        this.music = new Music();
        this.ambientPieces = new ArrayList<Audio>();
        this.muted = false;
        this.bankList = new long[32];
        this.eventDescList = new long[256];
        this.eventInstList = new long[256];
        this.pausedEventInstances = new long[128];
        this.pausedEventVolumes = new float[128];
        this.emitters = new HashSet<BaseSoundEmitter>();
        this.musicEventCallback = new FMOD_STUDIO_EVENT_CALLBACK() {
            public void timelineMarker(final long n, final String anObject, final int i) {
                DebugLog.Sound.debugln("timelineMarker %s %d", anObject, i);
                if ("Lightning".equals(anObject)) {
                    MainScreenState.getInstance().lightningTimelineMarker = true;
                }
            }
        };
    }
    
    public FMODParameterList getFMODParameters() {
        return this.fmodParameters;
    }
    
    public void startEvent(final long n, final GameSoundClip gameSoundClip, final BitSet set) {
        final FMODParameterList fmodParameters = this.getFMODParameters();
        final ArrayList parameters = gameSoundClip.eventDescription.parameters;
        for (int i = 0; i < parameters.size(); ++i) {
            final FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_STUDIO_PARAMETER_DESCRIPTION = parameters.get(i);
            if (!set.get(fmod_STUDIO_PARAMETER_DESCRIPTION.globalIndex)) {
                final FMODParameter value = fmodParameters.get(fmod_STUDIO_PARAMETER_DESCRIPTION);
                if (value != null) {
                    value.startEventInstance(n);
                }
            }
        }
    }
    
    public void updateEvent(final long n, final GameSoundClip gameSoundClip) {
    }
    
    public void stopEvent(final long n, final GameSoundClip gameSoundClip, final BitSet set) {
        final FMODParameterList fmodParameters = this.getFMODParameters();
        final ArrayList parameters = gameSoundClip.eventDescription.parameters;
        for (int i = 0; i < parameters.size(); ++i) {
            final FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_STUDIO_PARAMETER_DESCRIPTION = parameters.get(i);
            if (!set.get(fmod_STUDIO_PARAMETER_DESCRIPTION.globalIndex)) {
                final FMODParameter value = fmodParameters.get(fmod_STUDIO_PARAMETER_DESCRIPTION);
                if (value != null) {
                    value.stopEventInstance(n);
                }
            }
        }
    }
    
    @Override
    public boolean isRemastered() {
        final int optionMusicLibrary = Core.getInstance().getOptionMusicLibrary();
        return optionMusicLibrary == 1 || (optionMusicLibrary == 3 && Rand.Next(2) == 0);
    }
    
    @Override
    public void BlendVolume(final Audio audio, final float n) {
    }
    
    @Override
    public void BlendVolume(final Audio audio, final float n, final float n2) {
    }
    
    @Override
    public Audio BlendThenStart(final Audio audio, final float n, final String s) {
        return null;
    }
    
    @Override
    public void FadeOutMusic(final String s, final int n) {
    }
    
    @Override
    public void PlayAsMusic(final String s, final Audio audio, final float n, final boolean b) {
    }
    
    @Override
    public long playUISound(final String s) {
        final GameSound sound = GameSounds.getSound(s);
        if (sound == null || sound.clips.isEmpty()) {
            return 0L;
        }
        final long playClip = this.uiEmitter.playClip(sound.getRandomClip(), (IsoObject)null);
        this.uiEmitter.tick();
        javafmod.FMOD_System_Update();
        return playClip;
    }
    
    @Override
    public boolean isPlayingUISound(final String s) {
        return this.uiEmitter.isPlaying(s);
    }
    
    @Override
    public boolean isPlayingUISound(final long n) {
        return this.uiEmitter.isPlaying(n);
    }
    
    @Override
    public void stopUISound(final long n) {
        this.uiEmitter.stopSound(n);
    }
    
    @Override
    public boolean IsMusicPlaying() {
        return false;
    }
    
    @Override
    public boolean isPlayingMusic() {
        return this.music.isPlaying();
    }
    
    @Override
    public ArrayList<Audio> getAmbientPieces() {
        return this.ambientPieces;
    }
    
    private void gatherInGameEventInstances() {
        this.pausedEventCount = 0;
        final int fmod_Studio_System_GetBankCount = javafmodJNI.FMOD_Studio_System_GetBankCount();
        if (this.bankList.length < fmod_Studio_System_GetBankCount) {
            this.bankList = new long[fmod_Studio_System_GetBankCount];
        }
        for (int fmod_Studio_System_GetBankList = javafmodJNI.FMOD_Studio_System_GetBankList(this.bankList), i = 0; i < fmod_Studio_System_GetBankList; ++i) {
            final int fmod_Studio_Bank_GetEventCount = javafmodJNI.FMOD_Studio_Bank_GetEventCount(this.bankList[i]);
            if (this.eventDescList.length < fmod_Studio_Bank_GetEventCount) {
                this.eventDescList = new long[fmod_Studio_Bank_GetEventCount];
            }
            for (int fmod_Studio_Bank_GetEventList = javafmodJNI.FMOD_Studio_Bank_GetEventList(this.bankList[i], this.eventDescList), j = 0; j < fmod_Studio_Bank_GetEventList; ++j) {
                final int fmod_Studio_EventDescription_GetInstanceCount = javafmodJNI.FMOD_Studio_EventDescription_GetInstanceCount(this.eventDescList[j]);
                if (this.eventInstList.length < fmod_Studio_EventDescription_GetInstanceCount) {
                    this.eventInstList = new long[fmod_Studio_EventDescription_GetInstanceCount];
                }
                for (int fmod_Studio_EventDescription_GetInstanceList = javafmodJNI.FMOD_Studio_EventDescription_GetInstanceList(this.eventDescList[j], this.eventInstList), k = 0; k < fmod_Studio_EventDescription_GetInstanceList; ++k) {
                    if (javafmod.FMOD_Studio_GetPlaybackState(this.eventInstList[k]) != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index) {
                        if (!javafmodJNI.FMOD_Studio_EventInstance_GetPaused(this.eventInstList[k])) {
                            if (this.pausedEventInstances.length < this.pausedEventCount + 1) {
                                this.pausedEventInstances = Arrays.copyOf(this.pausedEventInstances, this.pausedEventCount + 128);
                                this.pausedEventVolumes = Arrays.copyOf(this.pausedEventVolumes, this.pausedEventInstances.length);
                            }
                            this.pausedEventInstances[this.pausedEventCount] = this.eventInstList[k];
                            this.pausedEventVolumes[this.pausedEventCount] = javafmodJNI.FMOD_Studio_EventInstance_GetVolume(this.eventInstList[k]);
                            ++this.pausedEventCount;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void pauseSoundAndMusic() {
        final boolean b = true;
        if (GameClient.bClient) {
            this.muted = true;
            if (b) {
                javafmod.FMOD_Studio_Bus_SetMute(this.inGameGroupBus, true);
                javafmod.FMOD_Studio_Bus_SetMute(this.musicGroupBus, true);
            }
            else {
                this.setSoundVolume(0.0f);
                this.setMusicVolume(0.0f);
                this.setAmbientVolume(0.0f);
                this.setVehicleEngineVolume(0.0f);
            }
            GameSounds.soundIsPaused = true;
            return;
        }
        if (b) {
            javafmod.FMOD_Studio_Bus_SetPaused(this.inGameGroupBus, true);
            javafmod.FMOD_Studio_Bus_SetPaused(this.musicGroupBus, true);
            javafmod.FMOD_Channel_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, true);
            GameSounds.soundIsPaused = true;
            return;
        }
        final long fmod_System_GetMasterChannelGroup = javafmod.FMOD_System_GetMasterChannelGroup();
        javafmod.FMOD_ChannelGroup_SetPaused(fmod_System_GetMasterChannelGroup, true);
        javafmod.FMOD_ChannelGroup_SetVolume(fmod_System_GetMasterChannelGroup, 0.0f);
        javafmodJNI.FMOD_Studio_System_FlushCommands();
        this.gatherInGameEventInstances();
        for (int i = 0; i < this.pausedEventCount; ++i) {
            javafmodJNI.FMOD_Studio_EventInstance_SetPaused(this.pausedEventInstances[i], true);
        }
        javafmod.FMOD_Channel_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, true);
        javafmod.FMOD_ChannelGroup_SetPaused(fmod_System_GetMasterChannelGroup, false);
        javafmodJNI.FMOD_Studio_System_FlushCommands();
        javafmod.FMOD_ChannelGroup_SetVolume(fmod_System_GetMasterChannelGroup, 1.0f);
        GameSounds.soundIsPaused = true;
    }
    
    @Override
    public void resumeSoundAndMusic() {
        final boolean b = true;
        if (this.muted) {
            this.muted = false;
            if (b) {
                javafmod.FMOD_Studio_Bus_SetMute(this.inGameGroupBus, false);
                javafmod.FMOD_Studio_Bus_SetMute(this.musicGroupBus, false);
                javafmod.FMOD_ChannelGroup_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, false);
            }
            else {
                this.setSoundVolume(Core.getInstance().getOptionSoundVolume() / 10.0f);
                this.setMusicVolume(Core.getInstance().getOptionMusicVolume() / 10.0f);
                this.setAmbientVolume(Core.getInstance().getOptionAmbientVolume() / 10.0f);
                this.setVehicleEngineVolume(Core.getInstance().getOptionVehicleEngineVolume() / 10.0f);
            }
            GameSounds.soundIsPaused = false;
            return;
        }
        if (b) {
            javafmod.FMOD_Studio_Bus_SetPaused(this.inGameGroupBus, false);
            javafmod.FMOD_Studio_Bus_SetPaused(this.musicGroupBus, false);
            javafmod.FMOD_ChannelGroup_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, false);
            GameSounds.soundIsPaused = false;
            return;
        }
        final long fmod_System_GetMasterChannelGroup = javafmod.FMOD_System_GetMasterChannelGroup();
        javafmod.FMOD_ChannelGroup_SetPaused(fmod_System_GetMasterChannelGroup, true);
        javafmodJNI.FMOD_Studio_System_FlushCommands();
        for (int i = 0; i < this.pausedEventCount; ++i) {
            try {
                javafmodJNI.FMOD_Studio_EventInstance_SetPaused(this.pausedEventInstances[i], false);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
        this.pausedEventCount = 0;
        javafmod.FMOD_ChannelGroup_SetPaused(fmod_System_GetMasterChannelGroup, false);
        javafmod.FMOD_ChannelGroup_SetVolume(fmod_System_GetMasterChannelGroup, 1.0f);
        javafmod.FMOD_ChannelGroup_SetPaused(FMODManager.instance.channelGroupInGameNonBankSounds, false);
        GameSounds.soundIsPaused = false;
    }
    
    private void debugScriptSound(final Item item, final String s) {
        if (s == null || s.isEmpty()) {
            return;
        }
        if (!GameSounds.isKnownSound(s)) {
            DebugLog.General.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, item.getFullName()));
        }
    }
    
    @Override
    public void debugScriptSounds() {
        if (!Core.bDebug) {
            return;
        }
        final Iterator<ScriptModule> iterator = ScriptManager.instance.ModuleMap.values().iterator();
        while (iterator.hasNext()) {
            for (final Item item : iterator.next().ItemMap.values()) {
                this.debugScriptSound(item, item.getBreakSound());
                this.debugScriptSound(item, item.getBulletOutSound());
                this.debugScriptSound(item, item.getCloseSound());
                this.debugScriptSound(item, item.getCustomEatSound());
                this.debugScriptSound(item, item.getDoorHitSound());
                this.debugScriptSound(item, item.getCountDownSound());
                this.debugScriptSound(item, item.getExplosionSound());
                this.debugScriptSound(item, item.getImpactSound());
                this.debugScriptSound(item, item.getOpenSound());
                this.debugScriptSound(item, item.getPutInSound());
                this.debugScriptSound(item, item.getShellFallSound());
                this.debugScriptSound(item, item.getSwingSound());
            }
        }
    }
    
    @Override
    public void registerEmitter(final BaseSoundEmitter e) {
        this.emitters.add(e);
    }
    
    @Override
    public void unregisterEmitter(final BaseSoundEmitter o) {
        this.emitters.remove(o);
    }
    
    @Override
    public boolean isListenerInRange(final float n, final float n2, final float n3) {
        if (GameServer.bServer) {
            return false;
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            final IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer != null && !isoPlayer.Traits.Deaf.isSet() && IsoUtils.DistanceToSquared(isoPlayer.x, isoPlayer.y, n, n2) < n3 * n3) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void playNightAmbient(final String s) {
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        for (int i = 0; i < SoundManager.ambientSoundEffects.size(); ++i) {
            final AmbientSoundEffect e = SoundManager.ambientSoundEffects.get(i);
            if (e.getName().equals(s)) {
                e.setVolume(Rand.Next(700, 1500) / 1000.0f);
                e.start();
                this.ambientPieces.add((Audio)e);
                return;
            }
        }
        final AmbientSoundEffect ambientSoundEffect = new AmbientSoundEffect(s);
        ambientSoundEffect.setVolume(Rand.Next(700, 1500) / 1000.0f);
        ambientSoundEffect.setName(s);
        ambientSoundEffect.start();
        this.ambientPieces.add((Audio)ambientSoundEffect);
        SoundManager.ambientSoundEffects.add(ambientSoundEffect);
    }
    
    @Override
    public void playMusic(final String s) {
        this.DoMusic(s, false);
    }
    
    @Override
    public void playAmbient(final String s) {
    }
    
    @Override
    public void playMusicNonTriggered(final String s, final float n) {
    }
    
    @Override
    public void stopMusic(final String s) {
        if (!this.isPlayingMusic()) {
            return;
        }
        if (StringUtils.isNullOrWhitespace(s) || s.equalsIgnoreCase(this.getCurrentMusicName())) {
            this.StopMusic();
        }
    }
    
    @Override
    public void CheckDoMusic() {
    }
    
    @Override
    public float getMusicPosition() {
        if (this.isPlayingMusic()) {
            return this.music.getPosition();
        }
        return 0.0f;
    }
    
    @Override
    public void DoMusic(final String currentMusicName, final boolean b) {
        if (!this.AllowMusic || Core.getInstance().getOptionMusicVolume() == 0) {
            return;
        }
        if (this.isPlayingMusic()) {
            this.StopMusic();
        }
        final boolean b2 = Core.getInstance().getOptionMusicLibrary() == 1;
        final GameSound sound = GameSounds.getSound(currentMusicName);
        GameSoundClip randomClip = null;
        if (sound != null && !sound.clips.isEmpty()) {
            randomClip = sound.getRandomClip();
        }
        if (randomClip != null && randomClip.getEvent() != null) {
            if (randomClip.eventDescription != null) {
                final long address = randomClip.eventDescription.address;
                javafmod.FMOD_Studio_LoadEventSampleData(address);
                this.music.instance = javafmod.FMOD_Studio_System_CreateEventInstance(address);
                this.music.clip = randomClip;
                this.music.effectiveVolume = randomClip.getEffectiveVolume();
                javafmod.FMOD_Studio_EventInstance_SetParameterByName(this.music.instance, "Volume", 10.0f);
                javafmod.FMOD_Studio_EventInstance_SetVolume(this.music.instance, this.music.effectiveVolume);
                javafmod.FMOD_Studio_StartEvent(this.music.instance);
            }
        }
        else if (randomClip != null && randomClip.getFile() != null) {
            final long loadSound = FMODManager.instance.loadSound(randomClip.getFile());
            if (loadSound > 0L) {
                this.music.channel = javafmod.FMOD_System_PlaySound(loadSound, true);
                this.music.clip = randomClip;
                this.music.effectiveVolume = randomClip.getEffectiveVolume();
                javafmod.FMOD_Channel_SetVolume(this.music.channel, this.music.effectiveVolume);
                javafmod.FMOD_Channel_SetPitch(this.music.channel, randomClip.pitch);
                javafmod.FMOD_Channel_SetPaused(this.music.channel, false);
            }
        }
        this.currentMusicName = currentMusicName;
        this.currentMusicLibrary = (b2 ? "official" : "earlyaccess");
    }
    
    @Override
    public void PlayAsMusic(final String s, final Audio audio, final boolean b, final float n) {
    }
    
    @Override
    public void setMusicState(final String s) {
        switch (s) {
            case "MainMenu": {
                this.parameterMusicState.setState(ParameterMusicState.State.MainMenu);
                break;
            }
            case "Loading": {
                this.parameterMusicState.setState(ParameterMusicState.State.Loading);
                break;
            }
            case "InGame": {
                this.parameterMusicState.setState(ParameterMusicState.State.InGame);
                break;
            }
            case "PauseMenu": {
                this.parameterMusicState.setState(ParameterMusicState.State.PauseMenu);
                break;
            }
            case "Tutorial": {
                this.parameterMusicState.setState(ParameterMusicState.State.Tutorial);
                break;
            }
            default: {
                DebugLog.General.warn("unknown MusicState \"%s\"", s);
                break;
            }
        }
    }
    
    @Override
    public void setMusicWakeState(final IsoPlayer isoPlayer, final String s) {
        switch (s) {
            case "Awake": {
                this.parameterMusicWakeState.setState(isoPlayer, ParameterMusicWakeState.State.Awake);
                break;
            }
            case "Sleeping": {
                this.parameterMusicWakeState.setState(isoPlayer, ParameterMusicWakeState.State.Sleeping);
                break;
            }
            case "WakeNormal": {
                this.parameterMusicWakeState.setState(isoPlayer, ParameterMusicWakeState.State.WakeNormal);
                break;
            }
            case "WakeNightmare": {
                this.parameterMusicWakeState.setState(isoPlayer, ParameterMusicWakeState.State.WakeNightmare);
                break;
            }
            case "WakeZombies": {
                this.parameterMusicWakeState.setState(isoPlayer, ParameterMusicWakeState.State.WakeZombies);
                break;
            }
            default: {
                DebugLog.General.warn("unknown MusicWakeState \"%s\"", s);
                break;
            }
        }
    }
    
    @Override
    public Audio PlayMusic(final String s, final String s2, final boolean b, final float n) {
        return null;
    }
    
    @Override
    public Audio PlaySound(final String s, final boolean b, final float n, final float n2) {
        return null;
    }
    
    @Override
    public Audio PlaySound(final String s, final boolean b, final float n) {
        if (GameServer.bServer) {
            return null;
        }
        if (IsoWorld.instance == null) {
            return null;
        }
        final BaseSoundEmitter freeEmitter = IsoWorld.instance.getFreeEmitter();
        freeEmitter.setPos(0.0f, 0.0f, 0.0f);
        if (freeEmitter.playSound(s) != 0L) {
            return (Audio)new FMODAudio(freeEmitter);
        }
        return null;
    }
    
    @Override
    public Audio PlaySoundEvenSilent(final String s, final boolean b, final float n) {
        return null;
    }
    
    @Override
    public Audio PlayJukeboxSound(final String s, final boolean b, final float n) {
        return null;
    }
    
    @Override
    public Audio PlaySoundWav(final String s, final boolean b, final float n, final float n2) {
        return null;
    }
    
    @Override
    public Audio PlaySoundWav(final String s, final boolean b, final float n) {
        return null;
    }
    
    @Override
    public Audio PlaySoundWav(final String s, final int n, final boolean b, final float n2) {
        return null;
    }
    
    @Override
    public void update3D() {
    }
    
    @Override
    public Audio PlayWorldSound(final String s, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b) {
        return this.PlayWorldSound(s, false, isoGridSquare, n, n2, n3, b);
    }
    
    @Override
    public Audio PlayWorldSound(final String s, final boolean b, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b2) {
        if (GameServer.bServer || isoGridSquare == null) {
            return null;
        }
        if (GameClient.bClient) {
            GameClient.instance.PlayWorldSound(s, isoGridSquare.x, isoGridSquare.y, (byte)isoGridSquare.z);
        }
        return this.PlayWorldSoundImpl(s, b, isoGridSquare.getX(), isoGridSquare.getY(), isoGridSquare.getZ(), n, n2, n3, b2);
    }
    
    @Override
    public Audio PlayWorldSoundImpl(final String s, final boolean b, final int n, final int n2, final int n3, final float n4, final float n5, final float n6, final boolean b2) {
        final BaseSoundEmitter freeEmitter = IsoWorld.instance.getFreeEmitter(n + 0.5f, n2 + 0.5f, (float)n3);
        freeEmitter.playSoundImpl(s, (IsoObject)null);
        return (Audio)new FMODAudio(freeEmitter);
    }
    
    @Override
    public Audio PlayWorldSound(final String s, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final int n4, final boolean b) {
        return this.PlayWorldSound(s, isoGridSquare, n, n2, n3, b);
    }
    
    @Override
    public Audio PlayWorldSoundWav(final String s, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b) {
        return this.PlayWorldSoundWav(s, false, isoGridSquare, n, n2, n3, b);
    }
    
    @Override
    public Audio PlayWorldSoundWav(final String s, final boolean b, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b2) {
        if (GameServer.bServer || isoGridSquare == null) {
            return null;
        }
        if (GameClient.bClient) {
            GameClient.instance.PlayWorldSound(s, isoGridSquare.getX(), isoGridSquare.getY(), (byte)isoGridSquare.getZ());
        }
        return this.PlayWorldSoundWavImpl(s, b, isoGridSquare, n, n2, n3, b2);
    }
    
    @Override
    public Audio PlayWorldSoundWavImpl(final String s, final boolean b, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b2) {
        final BaseSoundEmitter freeEmitter = IsoWorld.instance.getFreeEmitter(isoGridSquare.getX() + 0.5f, isoGridSquare.getY() + 0.5f, (float)isoGridSquare.getZ());
        freeEmitter.playSound(s);
        return (Audio)new FMODAudio(freeEmitter);
    }
    
    @Override
    public void PlayWorldSoundWav(final String s, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final int n4, final boolean b) {
        this.PlayWorldSoundWav(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, Integer.valueOf(Rand.Next(n4) + 1).toString()), isoGridSquare, n, n2, n3, b);
    }
    
    @Override
    public Audio PrepareMusic(final String s) {
        return null;
    }
    
    @Override
    public Audio Start(final Audio audio, final float n, final String s) {
        return null;
    }
    
    @Override
    public void Update() {
        if (!this.initialized) {
            this.initialized = true;
            this.inGameGroupBus = javafmod.FMOD_Studio_System_GetBus("bus:/InGame");
            this.musicGroupBus = javafmod.FMOD_Studio_System_GetBus("bus:/Music");
            this.musicEmitter = new FMODSoundEmitter();
            this.musicEmitter.parameterUpdater = (IFMODParameterUpdater)this;
            this.fmodParameters.add(this.parameterMusicLibrary);
            this.fmodParameters.add(this.parameterMusicState);
            this.fmodParameters.add(this.parameterMusicWakeState);
            this.fmodParameters.add(this.parameterMusicZombiesTargeting);
            this.fmodParameters.add(this.parameterMusicZombiesVisible);
            this.uiEmitter = new FMODSoundEmitter();
        }
        FMODSoundEmitter.update();
        this.updateMusic();
        this.uiEmitter.tick();
        for (int i = 0; i < this.ambientPieces.size(); ++i) {
            final Audio o = this.ambientPieces.get(i);
            if (IsoPlayer.allPlayersDead()) {
                o.stop();
            }
            if (!o.isPlaying()) {
                o.stop();
                this.ambientPieces.remove(o);
                --i;
            }
            else if (o instanceof AmbientSoundEffect) {
                ((AmbientSoundEffect)o).update();
            }
        }
        AmbientStreamManager.instance.update();
        if (!this.AllowMusic) {
            this.StopMusic();
        }
        if (this.music.isPlaying()) {
            this.music.update();
        }
        FMODManager.instance.tick();
    }
    
    @Override
    protected boolean HasMusic(final Audio audio) {
        return false;
    }
    
    @Override
    public void Purge() {
    }
    
    @Override
    public void stop() {
        final Iterator<BaseSoundEmitter> iterator = this.emitters.iterator();
        while (iterator.hasNext()) {
            iterator.next().stopAll();
        }
        this.emitters.clear();
        javafmod.FMOD_ChannelGroup_Stop(javafmod.FMOD_System_GetMasterChannelGroup());
        this.pausedEventCount = 0;
    }
    
    @Override
    public void StopMusic() {
        this.music.stop();
    }
    
    @Override
    public void StopSound(final Audio audio) {
        audio.stop();
    }
    
    @Override
    public void CacheSound(final String s) {
    }
    
    @Override
    public void update4() {
    }
    
    @Override
    public void update2() {
    }
    
    @Override
    public void update3() {
    }
    
    @Override
    public void update1() {
    }
    
    @Override
    public void setSoundVolume(final float soundVolume) {
        this.SoundVolume = soundVolume;
    }
    
    @Override
    public float getSoundVolume() {
        return this.SoundVolume;
    }
    
    @Override
    public void setAmbientVolume(final float ambientVolume) {
        this.AmbientVolume = ambientVolume;
    }
    
    @Override
    public float getAmbientVolume() {
        return this.AmbientVolume;
    }
    
    @Override
    public void setMusicVolume(final float musicVolume) {
        this.MusicVolume = musicVolume;
        if (this.muted) {
            return;
        }
    }
    
    @Override
    public float getMusicVolume() {
        return this.MusicVolume;
    }
    
    @Override
    public void setVehicleEngineVolume(final float vehicleEngineVolume) {
        this.VehicleEngineVolume = vehicleEngineVolume;
    }
    
    @Override
    public float getVehicleEngineVolume() {
        return this.VehicleEngineVolume;
    }
    
    @Override
    public String getCurrentMusicName() {
        if (this.isPlayingMusic()) {
            return this.currentMusicName;
        }
        return null;
    }
    
    @Override
    public String getCurrentMusicLibrary() {
        if (this.isPlayingMusic()) {
            return this.currentMusicLibrary;
        }
        return null;
    }
    
    private void updateMusic() {
        this.fmodParameters.update();
        if (!this.musicEmitter.isPlaying(this.musicCombinedEvent)) {
            this.musicCombinedEvent = this.musicEmitter.playSound("MusicCombined");
            if (this.musicCombinedEvent != 0L && !System.getProperty("os.name").contains("OS X")) {
                javafmod.FMOD_Studio_EventInstance_SetCallback(this.musicCombinedEvent, this.musicEventCallback, FMOD_STUDIO_EVENT_CALLBACK_TYPE.FMOD_STUDIO_EVENT_CALLBACK_TIMELINE_MARKER.bit);
            }
        }
        if (this.musicEmitter.isPlaying(this.musicCombinedEvent)) {
            this.musicEmitter.setVolume(this.musicCombinedEvent, this.AllowMusic ? this.getMusicVolume() : 0.0f);
        }
        this.musicEmitter.tick();
    }
    
    static {
        SoundManager.ambientSoundEffects = new ArrayList<AmbientSoundEffect>();
    }
    
    private static final class Music
    {
        public GameSoundClip clip;
        public long instance;
        public long channel;
        public long sound;
        public float effectiveVolume;
        
        public boolean isPlaying() {
            if (this.instance != 0L) {
                final int fmod_Studio_GetPlaybackState = javafmod.FMOD_Studio_GetPlaybackState(this.instance);
                return fmod_Studio_GetPlaybackState != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index && fmod_Studio_GetPlaybackState != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index;
            }
            return this.channel != 0L && javafmod.FMOD_Channel_IsPlaying(this.channel);
        }
        
        public void update() {
            this.clip = this.clip.checkReloaded();
            final float effectiveVolume = this.clip.getEffectiveVolume();
            if (this.effectiveVolume == effectiveVolume) {
                return;
            }
            this.effectiveVolume = effectiveVolume;
            if (this.instance != 0L) {
                javafmod.FMOD_Studio_EventInstance_SetVolume(this.instance, this.effectiveVolume);
            }
            if (this.channel != 0L) {
                javafmod.FMOD_Channel_SetVolume(this.channel, this.effectiveVolume);
            }
        }
        
        public float getPosition() {
            if (this.instance != 0L) {
                return (float)javafmod.FMOD_Studio_GetTimelinePosition(this.instance);
            }
            if (this.channel != 0L) {
                return (float)javafmod.FMOD_Channel_GetPosition(this.channel, FMODManager.FMOD_TIMEUNIT_MS);
            }
            return 0.0f;
        }
        
        public void stop() {
            if (this.instance != 0L) {
                javafmod.FMOD_Studio_EventInstance_Stop(this.instance, false);
                javafmod.FMOD_Studio_ReleaseEventInstance(this.instance);
                this.instance = 0L;
            }
            if (this.channel != 0L) {
                javafmod.FMOD_Channel_Stop(this.channel);
                this.channel = 0L;
                javafmod.FMOD_Sound_Release(this.sound);
                this.sound = 0L;
            }
        }
    }
    
    public static final class AmbientSoundEffect implements Audio
    {
        public String name;
        public long eventInstance;
        public float gain;
        public GameSoundClip clip;
        public float effectiveVolume;
        
        public AmbientSoundEffect(final String s) {
            final GameSound sound = GameSounds.getSound(s);
            if (sound == null || sound.clips.isEmpty()) {
                return;
            }
            final GameSoundClip randomClip = sound.getRandomClip();
            if (randomClip.getEvent() == null) {
                return;
            }
            if (randomClip.eventDescription == null) {
                return;
            }
            this.eventInstance = javafmod.FMOD_Studio_System_CreateEventInstance(randomClip.eventDescription.address);
            if (this.eventInstance < 0L) {
                return;
            }
            this.clip = randomClip;
        }
        
        public void setVolume(final float gain) {
            if (this.eventInstance <= 0L) {
                return;
            }
            this.gain = gain;
            this.effectiveVolume = this.clip.getEffectiveVolume();
            javafmod.FMOD_Studio_EventInstance_SetVolume(this.eventInstance, this.gain * this.effectiveVolume);
        }
        
        public void start() {
            if (this.eventInstance <= 0L) {
                return;
            }
            javafmod.FMOD_Studio_StartEvent(this.eventInstance);
        }
        
        public void pause() {
        }
        
        public void stop() {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
            if (this.eventInstance <= 0L) {
                return;
            }
            javafmod.FMOD_Studio_EventInstance_Stop(this.eventInstance, false);
        }
        
        public boolean isPlaying() {
            if (this.eventInstance <= 0L) {
                return false;
            }
            final int fmod_Studio_GetPlaybackState = javafmod.FMOD_Studio_GetPlaybackState(this.eventInstance);
            return fmod_Studio_GetPlaybackState == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STARTING.index || fmod_Studio_GetPlaybackState == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_PLAYING.index || fmod_Studio_GetPlaybackState == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_SUSTAINING.index;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void update() {
            if (this.clip == null) {
                return;
            }
            this.clip = this.clip.checkReloaded();
            final float effectiveVolume = this.clip.getEffectiveVolume();
            if (this.effectiveVolume != effectiveVolume) {
                this.effectiveVolume = effectiveVolume;
                javafmod.FMOD_Studio_EventInstance_SetVolume(this.eventInstance, this.gain * this.effectiveVolume);
            }
        }
    }
}
