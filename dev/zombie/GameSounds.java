// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import fmod.fmod.FMOD_STUDIO_PLAYBACK_STATE;
import zombie.characters.IsoPlayer;
import fmod.javafmod;
import zombie.network.GameClient;
import zombie.config.DoubleConfigOption;
import zombie.core.math.PZMath;
import zombie.config.ConfigOption;
import zombie.config.ConfigFile;
import java.io.File;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import zombie.core.logger.ExceptionLogger;
import fmod.javafmodJNI;
import fmod.fmod.FMODVoice;
import fmod.fmod.FMODFootstep;
import java.util.HashSet;
import zombie.core.Core;
import zombie.scripting.objects.GameSoundScript;
import zombie.scripting.ScriptManager;
import fmod.fmod.FMOD_STUDIO_EVENT_DESCRIPTION;
import zombie.audio.BaseSoundBank;
import fmod.fmod.FMODSoundBank;
import zombie.util.StringUtils;
import java.util.Iterator;
import zombie.debug.DebugLog;
import fmod.fmod.FMODManager;
import zombie.audio.GameSoundClip;
import zombie.network.GameServer;
import java.util.ArrayList;
import zombie.audio.GameSound;
import java.util.HashMap;

public final class GameSounds
{
    public static final int VERSION = 1;
    protected static final HashMap<String, GameSound> soundByName;
    protected static final ArrayList<GameSound> sounds;
    private static final BankPreviewSound previewBank;
    private static final FilePreviewSound previewFile;
    public static boolean soundIsPaused;
    private static IPreviewSound previewSound;
    
    public static void addSound(final GameSound value) {
        initClipEvents(value);
        assert !GameSounds.sounds.contains(value);
        int size = GameSounds.sounds.size();
        if (GameSounds.soundByName.containsKey(value.getName())) {
            for (size = 0; size < GameSounds.sounds.size() && !GameSounds.sounds.get(size).getName().equals(value.getName()); ++size) {}
            GameSounds.sounds.remove(size);
        }
        GameSounds.sounds.add(size, value);
        GameSounds.soundByName.put(value.getName(), value);
    }
    
    private static void initClipEvents(final GameSound gameSound) {
        if (GameServer.bServer) {
            return;
        }
        for (final GameSoundClip gameSoundClip : gameSound.clips) {
            if (gameSoundClip.event != null) {
                if (gameSoundClip.eventDescription != null) {
                    continue;
                }
                gameSoundClip.eventDescription = FMODManager.instance.getEventDescription(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, gameSoundClip.event));
                if (gameSoundClip.eventDescription != null) {
                    continue;
                }
                DebugLog.Sound.warn("No such FMOD event \"%s\" for GameSound \"%s\"", gameSoundClip.event, gameSound.getName());
            }
        }
    }
    
    public static boolean isKnownSound(final String key) {
        return GameSounds.soundByName.containsKey(key);
    }
    
    public static GameSound getSound(final String s) {
        return getOrCreateSound(s);
    }
    
    public static GameSound getOrCreateSound(final String event) {
        if (StringUtils.isNullOrEmpty(event)) {
            return null;
        }
        GameSound gameSound = GameSounds.soundByName.get(event);
        if (gameSound == null) {
            DebugLog.General.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, event));
            gameSound = new GameSound();
            gameSound.name = event;
            gameSound.category = "AUTO";
            final GameSoundClip e = new GameSoundClip(gameSound);
            gameSound.clips.add(e);
            GameSounds.sounds.add(gameSound);
            GameSounds.soundByName.put(event.replace(".wav", "").replace(".ogg", ""), gameSound);
            if (BaseSoundBank.instance instanceof FMODSoundBank) {
                final FMOD_STUDIO_EVENT_DESCRIPTION eventDescription = FMODManager.instance.getEventDescription(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, event));
                if (eventDescription != null) {
                    e.event = event;
                    e.eventDescription = eventDescription;
                }
                else {
                    String file = null;
                    if (ZomboidFileSystem.instance.getAbsolutePath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, event)) != null) {
                        file = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, event);
                    }
                    else if (ZomboidFileSystem.instance.getAbsolutePath(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, event)) != null) {
                        file = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, event);
                    }
                    if (file != null && FMODManager.instance.loadSound(file) != 0L) {
                        e.file = file;
                    }
                }
                if (e.event == null && e.file == null) {
                    DebugLog.General.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, event));
                }
            }
        }
        return gameSound;
    }
    
    private static void loadNonBankSounds() {
        if (!(BaseSoundBank.instance instanceof FMODSoundBank)) {
            return;
        }
        final Iterator<GameSound> iterator = GameSounds.sounds.iterator();
        while (iterator.hasNext()) {
            for (final GameSoundClip gameSoundClip : iterator.next().clips) {
                if (gameSoundClip.getFile() != null) {
                    if (gameSoundClip.getFile().isEmpty()) {
                        continue;
                    }
                    continue;
                }
            }
        }
    }
    
    public static void ScriptsLoaded() {
        final ArrayList<GameSoundScript> allGameSounds = ScriptManager.instance.getAllGameSounds();
        for (int i = 0; i < allGameSounds.size(); ++i) {
            final GameSoundScript gameSoundScript = allGameSounds.get(i);
            if (!gameSoundScript.gameSound.clips.isEmpty()) {
                addSound(gameSoundScript.gameSound);
            }
        }
        allGameSounds.clear();
        loadNonBankSounds();
        loadINI();
        if (Core.bDebug && BaseSoundBank.instance instanceof FMODSoundBank) {
            final HashSet<String> set = new HashSet<String>();
            final Iterator<GameSound> iterator = GameSounds.sounds.iterator();
            while (iterator.hasNext()) {
                for (final GameSoundClip gameSoundClip : iterator.next().clips) {
                    if (gameSoundClip.getEvent() != null && !gameSoundClip.getEvent().isEmpty()) {
                        set.add(gameSoundClip.getEvent());
                    }
                }
            }
            final FMODSoundBank fmodSoundBank = (FMODSoundBank)BaseSoundBank.instance;
            for (final FMODFootstep fmodFootstep : fmodSoundBank.footstepMap.values()) {
                set.add(fmodFootstep.wood);
                set.add(fmodFootstep.concrete);
                set.add(fmodFootstep.grass);
                set.add(fmodFootstep.upstairs);
                set.add(fmodFootstep.woodCreak);
            }
            final Iterator<FMODVoice> iterator4 = fmodSoundBank.voiceMap.values().iterator();
            while (iterator4.hasNext()) {
                set.add(iterator4.next().sound);
            }
            final ArrayList<String> list = new ArrayList<String>();
            final long[] array = new long[32];
            final long[] array2 = new long[1024];
            for (int fmod_Studio_System_GetBankList = javafmodJNI.FMOD_Studio_System_GetBankList(array), j = 0; j < fmod_Studio_System_GetBankList; ++j) {
                for (int fmod_Studio_Bank_GetEventList = javafmodJNI.FMOD_Studio_Bank_GetEventList(array[j], array2), k = 0; k < fmod_Studio_Bank_GetEventList; ++k) {
                    try {
                        final String replace = javafmodJNI.FMOD_Studio_EventDescription_GetPath(array2[k]).replace("event:/", "");
                        if (!set.contains(replace)) {
                            list.add(replace);
                        }
                    }
                    catch (Exception ex) {
                        DebugLog.General.warn(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, array2[k]));
                    }
                }
            }
            list.sort(String::compareTo);
            final Iterator<String> iterator5 = list.iterator();
            while (iterator5.hasNext()) {
                DebugLog.General.warn("FMOD event \"%s\" not used by any GameSound", iterator5.next());
            }
        }
    }
    
    public static void ReloadFile(final String s) {
        try {
            ScriptManager.instance.LoadFile(s, true);
            final ArrayList<GameSoundScript> allGameSounds = ScriptManager.instance.getAllGameSounds();
            for (int i = 0; i < allGameSounds.size(); ++i) {
                final GameSoundScript gameSoundScript = allGameSounds.get(i);
                if (GameSounds.sounds.contains(gameSoundScript.gameSound)) {
                    initClipEvents(gameSoundScript.gameSound);
                }
                else if (!gameSoundScript.gameSound.clips.isEmpty()) {
                    addSound(gameSoundScript.gameSound);
                }
            }
        }
        catch (Throwable t) {
            ExceptionLogger.logException(t);
        }
    }
    
    public static ArrayList<String> getCategories() {
        final HashSet<String> c = new HashSet<String>();
        final Iterator<GameSound> iterator = GameSounds.sounds.iterator();
        while (iterator.hasNext()) {
            c.add(iterator.next().getCategory());
        }
        final ArrayList list = new ArrayList<Comparable>(c);
        Collections.sort((List<Comparable>)list);
        return (ArrayList<String>)list;
    }
    
    public static ArrayList<GameSound> getSoundsInCategory(final String anObject) {
        final ArrayList<GameSound> list = new ArrayList<GameSound>();
        for (final GameSound e : GameSounds.sounds) {
            if (e.getCategory().equals(anObject)) {
                list.add(e);
            }
        }
        return list;
    }
    
    public static void loadINI() {
        final String s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator);
        final ConfigFile configFile = new ConfigFile();
        if (!configFile.read(s)) {
            return;
        }
        if (configFile.getVersion() > 1) {
            return;
        }
        for (final ConfigOption configOption : configFile.getOptions()) {
            final GameSound gameSound = GameSounds.soundByName.get(configOption.getName());
            if (gameSound == null) {
                continue;
            }
            gameSound.setUserVolume(PZMath.tryParseFloat(configOption.getValueAsString(), 1.0f));
        }
    }
    
    public static void saveINI() {
        final ArrayList<DoubleConfigOption> list = new ArrayList<DoubleConfigOption>();
        for (final GameSound gameSound : GameSounds.sounds) {
            final DoubleConfigOption e = new DoubleConfigOption(gameSound.getName(), 0.0, 2.0, 0.0);
            e.setValue(gameSound.getUserVolume());
            list.add(e);
        }
        if (!new ConfigFile().write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator), 1, (ArrayList<? extends ConfigOption>)list)) {
            return;
        }
        list.clear();
    }
    
    public static void previewSound(final String s) {
        if (Core.SoundDisabled) {
            return;
        }
        if (!isKnownSound(s)) {
            return;
        }
        final GameSound sound = getSound(s);
        if (sound == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            return;
        }
        final GameSoundClip randomClip = sound.getRandomClip();
        if (randomClip == null) {
            DebugLog.log("GameSound.clips is empty");
            return;
        }
        if (GameSounds.soundIsPaused) {
            if (!GameClient.bClient) {
                javafmod.FMOD_ChannelGroup_SetVolume(javafmod.FMOD_System_GetMasterChannelGroup(), 1.0f);
            }
            GameSounds.soundIsPaused = false;
        }
        if (GameSounds.previewSound != null) {
            GameSounds.previewSound.stop();
        }
        if (randomClip.getEvent() != null) {
            if (GameSounds.previewBank.play(randomClip)) {
                GameSounds.previewSound = GameSounds.previewBank;
            }
        }
        else if (randomClip.getFile() != null && GameSounds.previewFile.play(randomClip)) {
            GameSounds.previewSound = GameSounds.previewFile;
        }
    }
    
    public static void stopPreview() {
        if (GameSounds.previewSound == null) {
            return;
        }
        GameSounds.previewSound.stop();
        GameSounds.previewSound = null;
    }
    
    public static boolean isPreviewPlaying() {
        if (GameSounds.previewSound == null) {
            return false;
        }
        if (GameSounds.previewSound.update()) {
            GameSounds.previewSound = null;
            return false;
        }
        return GameSounds.previewSound.isPlaying();
    }
    
    public static void fix3DListenerPosition(final boolean b) {
        if (Core.SoundDisabled) {
            return;
        }
        if (b) {
            javafmod.FMOD_Studio_Listener3D(0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        }
        else {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null && !isoPlayer.Traits.Deaf.isSet()) {
                    javafmod.FMOD_Studio_Listener3D(i, isoPlayer.x, isoPlayer.y, isoPlayer.z * 3.0f, 0.0f, 0.0f, 0.0f, -1.0f / (float)Math.sqrt(2.0), -1.0f / (float)Math.sqrt(2.0), 0.0f, 0.0f, 0.0f, 1.0f);
                }
            }
        }
    }
    
    public static void Reset() {
        GameSounds.sounds.clear();
        GameSounds.soundByName.clear();
        if (GameSounds.previewSound != null) {
            GameSounds.previewSound.stop();
            GameSounds.previewSound = null;
        }
    }
    
    static {
        soundByName = new HashMap<String, GameSound>();
        sounds = new ArrayList<GameSound>();
        previewBank = new BankPreviewSound();
        previewFile = new FilePreviewSound();
        GameSounds.soundIsPaused = false;
    }
    
    private static final class BankPreviewSound implements IPreviewSound
    {
        long instance;
        GameSoundClip clip;
        float effectiveGain;
        
        @Override
        public boolean play(final GameSoundClip clip) {
            if (clip.eventDescription == null) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, clip.getEvent()));
                return false;
            }
            this.instance = javafmod.FMOD_Studio_System_CreateEventInstance(clip.eventDescription.address);
            if (this.instance < 0L) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, this.instance));
                this.instance = 0L;
                return false;
            }
            this.clip = clip;
            this.effectiveGain = clip.getEffectiveVolumeInMenu();
            javafmod.FMOD_Studio_EventInstance_SetVolume(this.instance, this.effectiveGain);
            javafmod.FMOD_Studio_EventInstance_SetParameterByName(this.instance, "Occlusion", 0.0f);
            javafmod.FMOD_Studio_StartEvent(this.instance);
            if (clip.gameSound.master == GameSound.MasterVolume.Music) {
                javafmod.FMOD_Studio_EventInstance_SetParameterByName(this.instance, "Volume", 10.0f);
            }
            return true;
        }
        
        @Override
        public boolean isPlaying() {
            if (this.instance == 0L) {
                return false;
            }
            final int fmod_Studio_GetPlaybackState = javafmod.FMOD_Studio_GetPlaybackState(this.instance);
            return fmod_Studio_GetPlaybackState == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index || fmod_Studio_GetPlaybackState != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index;
        }
        
        @Override
        public boolean update() {
            if (this.instance == 0L) {
                return false;
            }
            final int fmod_Studio_GetPlaybackState = javafmod.FMOD_Studio_GetPlaybackState(this.instance);
            if (fmod_Studio_GetPlaybackState == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index) {
                return false;
            }
            if (fmod_Studio_GetPlaybackState == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index) {
                javafmod.FMOD_Studio_ReleaseEventInstance(this.instance);
                this.instance = 0L;
                this.clip = null;
                return true;
            }
            final float effectiveVolumeInMenu = this.clip.getEffectiveVolumeInMenu();
            if (this.effectiveGain != effectiveVolumeInMenu) {
                this.effectiveGain = effectiveVolumeInMenu;
                javafmod.FMOD_Studio_EventInstance_SetVolume(this.instance, this.effectiveGain);
            }
            return false;
        }
        
        @Override
        public void stop() {
            if (this.instance == 0L) {
                return;
            }
            javafmod.FMOD_Studio_EventInstance_Stop(this.instance, false);
            javafmod.FMOD_Studio_ReleaseEventInstance(this.instance);
            this.instance = 0L;
            this.clip = null;
        }
    }
    
    private static final class FilePreviewSound implements IPreviewSound
    {
        long channel;
        GameSoundClip clip;
        float effectiveGain;
        
        @Override
        public boolean play(final GameSoundClip clip) {
            final GameSound gameSound = clip.gameSound;
            final long loadSound = FMODManager.instance.loadSound(clip.getFile(), gameSound.isLooped());
            if (loadSound == 0L) {
                return false;
            }
            this.channel = javafmod.FMOD_System_PlaySound(loadSound, true);
            this.clip = clip;
            this.effectiveGain = clip.getEffectiveVolumeInMenu();
            javafmod.FMOD_Channel_SetVolume(this.channel, this.effectiveGain);
            javafmod.FMOD_Channel_SetPitch(this.channel, clip.pitch);
            if (gameSound.isLooped()) {
                javafmod.FMOD_Channel_SetMode(this.channel, (long)FMODManager.FMOD_LOOP_NORMAL);
            }
            javafmod.FMOD_Channel_SetPaused(this.channel, false);
            return true;
        }
        
        @Override
        public boolean isPlaying() {
            return this.channel != 0L && javafmod.FMOD_Channel_IsPlaying(this.channel);
        }
        
        @Override
        public boolean update() {
            if (this.channel == 0L) {
                return false;
            }
            if (!javafmod.FMOD_Channel_IsPlaying(this.channel)) {
                this.channel = 0L;
                this.clip = null;
                return true;
            }
            final float effectiveVolumeInMenu = this.clip.getEffectiveVolumeInMenu();
            if (this.effectiveGain != effectiveVolumeInMenu) {
                this.effectiveGain = effectiveVolumeInMenu;
                javafmod.FMOD_Channel_SetVolume(this.channel, this.effectiveGain);
            }
            return false;
        }
        
        @Override
        public void stop() {
            if (this.channel == 0L) {
                return;
            }
            javafmod.FMOD_Channel_Stop(this.channel);
            this.channel = 0L;
            this.clip = null;
        }
    }
    
    private interface IPreviewSound
    {
        boolean play(final GameSoundClip p0);
        
        boolean isPlaying();
        
        boolean update();
        
        void stop();
    }
}
