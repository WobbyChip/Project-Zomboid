// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.raknet;

import zombie.network.ServerOptions;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.IsoCell;
import zombie.inventory.InventoryItem;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import zombie.iso.IsoUtils;
import fmod.SoundBuffer;
import zombie.input.GameKeyboard;
import zombie.network.GameServer;
import fmod.FMOD_DriverInfo;
import zombie.core.Core;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.Platform;
import zombie.characters.IsoPlayer;
import zombie.network.GameClient;
import java.util.Random;
import java.util.ArrayList;
import fmod.FMOD_RESULT;
import fmod.javafmod;
import fmod.fmod.FMODManager;
import zombie.debug.DebugLog;
import java.util.concurrent.Semaphore;
import fmod.FMODSoundBuffer;
import zombie.inventory.types.Radio;

public class VoiceManager
{
    public static VoiceManager instance;
    private static int qulity;
    private static final int pcmsize = 2;
    private static final int bufferSize = 192;
    private static boolean serverVOIPEnable;
    private static int discretizate;
    private static int period;
    private static int complexity;
    private static int buffering;
    private static float minDistance;
    private static float maxDistance;
    private static boolean is3D;
    private Radio myRadio;
    private RakVoice voice;
    private int voice_bufsize;
    private long recSound;
    private static FMODSoundBuffer recBuf;
    private boolean startInit;
    private boolean initialiseRecDev;
    private boolean initialisedRecDev;
    private Semaphore RecDevSemaphore;
    private boolean isModeVAD;
    private boolean isModePPT;
    private int vadMode;
    private int volumeMic;
    private int volumePlayers;
    public static boolean VoipDisabled;
    private boolean isEnable;
    private boolean isDebug;
    private boolean isDebugLoopback;
    private boolean isDebugLoopbackLong;
    private long fmod_channel_group_voip;
    private int FMODVoiceRecordDriverId;
    private byte[] serverbuf;
    private Thread thread;
    private boolean bQuit;
    private long time_last;
    private boolean isServer;
    private long indicator_is_voice;
    public static long tem_t;
    public static final int modePPT = 1;
    public static final int modeVAD = 2;
    public static final int modeMute = 3;
    public static final int VADModeQuality = 1;
    public static final int VADModeLowBitrate = 2;
    public static final int VADModeAggressive = 3;
    public static final int VADModeVeryAggressive = 4;
    byte[] buf;
    private final Long recBuf_Current_read;
    private final Object notifier;
    private boolean bIsClient;
    private boolean bTestingMicrophone;
    private long testingMicrophoneMS;
    private static long timestamp;
    
    public VoiceManager() {
        this.myRadio = null;
        this.startInit = false;
        this.initialiseRecDev = false;
        this.initialisedRecDev = false;
        this.isModeVAD = false;
        this.isModePPT = false;
        this.vadMode = 3;
        this.isEnable = true;
        this.isDebug = false;
        this.isDebugLoopback = false;
        this.isDebugLoopbackLong = false;
        this.fmod_channel_group_voip = 0L;
        this.serverbuf = null;
        this.indicator_is_voice = 0L;
        this.buf = new byte[192];
        this.recBuf_Current_read = new Long(0L);
        this.notifier = new Object();
        this.bIsClient = false;
        this.bTestingMicrophone = false;
        this.testingMicrophoneMS = 0L;
    }
    
    public static VoiceManager getInstance() {
        return VoiceManager.instance;
    }
    
    int VoiceInitClient() {
        DebugLog.log("[VOICE MANAGER] VoiceInit");
        this.isServer = false;
        this.voice = new RakVoice();
        if (this.voice == null) {
            return -1;
        }
        this.RecDevSemaphore = new Semaphore(1);
        VoiceManager.recBuf = null;
        this.voice_bufsize = 192;
        final RakVoice voice = this.voice;
        RakVoice.RVInit(this.voice_bufsize);
        final RakVoice voice2 = this.voice;
        RakVoice.SetComplexity(VoiceManager.complexity);
        return 0;
    }
    
    int VoiceInitServer(final boolean b, final int discretizate, final int n, final int n2, final int n3, final double n4, final double n5, final boolean b2) {
        DebugLog.log("[VOICE MANAGER] VoiceInit");
        this.isServer = true;
        if (!(n == 2 | n == 5 | n == 10 | n == 20 | n == 40 | n == 60)) {
            DebugLog.log("[VOICE MANAGER] VoiceInit ERROR: invalid period");
            return -1;
        }
        if (!(discretizate == 8000 | discretizate == 16000 | discretizate == 24000)) {
            DebugLog.log("[VOICE MANAGER] VoiceInit ERROR: invalid samplerate");
            return -1;
        }
        if (n2 < 0 | n2 > 10) {
            DebugLog.log("[VOICE MANAGER] VoiceInit ERROR: invalid qulity");
            return -1;
        }
        if (n3 < 0 | n3 > 32000) {
            DebugLog.log("[VOICE MANAGER] VoiceInit ERROR: invalid buffering");
            return -1;
        }
        this.voice = new RakVoice();
        if (this.voice == null) {
            DebugLog.log("[VOICE MANAGER] VoiceInit ERROR: RakVoice internal error");
            return -1;
        }
        VoiceManager.discretizate = discretizate;
        final RakVoice voice = this.voice;
        RakVoice.RVInitServer(b, discretizate, n, n2, n3, (float)n4, (float)n5, b2);
        return 0;
    }
    
    int VoiceDeinit() {
        DebugLog.log("[VOICE MANAGER] VoiceDeinit");
        final RakVoice voice = this.voice;
        RakVoice.CloseAllChannels();
        final RakVoice voice2 = this.voice;
        RakVoice.RVDeinit();
        return 0;
    }
    
    int VoiceConnectAccept(final long n) {
        if (!this.isEnable) {
            return 0;
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, n));
        return 0;
    }
    
    int InitRecDeviceForTest() {
        try {
            this.RecDevSemaphore.acquire();
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        this.recSound = javafmod.FMOD_System_CreateRecordSound((long)this.FMODVoiceRecordDriverId, (long)(FMODManager.FMOD_2D | FMODManager.FMOD_OPENUSER | FMODManager.FMOD_SOFTWARE), (long)FMODManager.FMOD_SOUND_FORMAT_PCM16, (long)VoiceManager.discretizate);
        if (this.recSound == 0L) {
            DebugLog.log("[VOICE MANAGER] Error: FMOD_System_CreateSound return zero");
        }
        DebugLog.log("[VOICE MANAGER] FMOD_System_CreateSound OK");
        javafmod.FMOD_System_SetRecordVolume(1L - Math.round(Math.pow(1.4, 11 - this.volumeMic)));
        if (this.initialiseRecDev) {
            final int fmod_System_RecordStart = javafmod.FMOD_System_RecordStart(this.FMODVoiceRecordDriverId, this.recSound, true);
            if (fmod_System_RecordStart != FMOD_RESULT.FMOD_OK.ordinal()) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, fmod_System_RecordStart));
            }
        }
        javafmod.FMOD_System_SetVADMode(this.vadMode - 1);
        VoiceManager.recBuf = new FMODSoundBuffer(this.recSound);
        this.initialisedRecDev = true;
        this.RecDevSemaphore.release();
        return 0;
    }
    
    int VoiceOpenChannelReply(final long n) {
        if (!this.isEnable) {
            return 0;
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, n));
        if (this.isServer) {
            return 0;
        }
        try {
            this.RecDevSemaphore.acquire();
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        this.initialisedRecDev = false;
        final RakVoice voice = this.voice;
        VoiceManager.serverVOIPEnable = RakVoice.GetServerVOIPEnable();
        final RakVoice voice2 = this.voice;
        VoiceManager.discretizate = RakVoice.GetSampleRate();
        final RakVoice voice3 = this.voice;
        VoiceManager.period = RakVoice.GetSendFramePeriod();
        final RakVoice voice4 = this.voice;
        VoiceManager.buffering = RakVoice.GetBuffering();
        final RakVoice voice5 = this.voice;
        VoiceManager.minDistance = RakVoice.GetMinDistance();
        final RakVoice voice6 = this.voice;
        VoiceManager.maxDistance = RakVoice.GetMaxDistance();
        final RakVoice voice7 = this.voice;
        VoiceManager.is3D = RakVoice.GetIs3D();
        final ArrayList<VoiceManagerData> data = VoiceManagerData.data;
        for (int i = 0; i < data.size(); ++i) {
            final VoiceManagerData voiceManagerData = data.get(i);
            if (voiceManagerData.userplaysound != 0L) {
                if (VoiceManager.is3D) {
                    javafmod.FMOD_Sound_SetMode(voiceManagerData.userplaysound, FMODManager.FMOD_3D | FMODManager.FMOD_OPENUSER | FMODManager.FMOD_LOOP_NORMAL | FMODManager.FMOD_CREATESTREAM);
                }
                else {
                    javafmod.FMOD_Sound_SetMode(voiceManagerData.userplaysound, FMODManager.FMOD_OPENUSER | FMODManager.FMOD_LOOP_NORMAL | FMODManager.FMOD_CREATESTREAM);
                }
            }
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, VoiceManager.discretizate));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, VoiceManager.period));
        DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, VoiceManager.buffering));
        if (javafmod.FMOD_System_SetRawPlayBufferingPeriod((long)VoiceManager.buffering) != FMOD_RESULT.FMOD_OK.ordinal()) {
            DebugLog.log("[VOICE MANAGER] Error: FMOD_System_SetRawPlayBufferingPeriod ");
        }
        if (this.recSound != 0L) {
            final int fmod_Sound_Release = javafmod.FMOD_Sound_Release(this.recSound);
            if (fmod_Sound_Release != FMOD_RESULT.FMOD_OK.ordinal()) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, fmod_Sound_Release));
            }
            this.recSound = 0L;
        }
        this.recSound = javafmod.FMOD_System_CreateRecordSound((long)this.FMODVoiceRecordDriverId, (long)(FMODManager.FMOD_2D | FMODManager.FMOD_OPENUSER | FMODManager.FMOD_SOFTWARE), (long)FMODManager.FMOD_SOUND_FORMAT_PCM16, (long)VoiceManager.discretizate);
        if (this.recSound == 0L) {
            DebugLog.log("[VOICE MANAGER] Error: FMOD_System_CreateSound return zero");
        }
        DebugLog.log("[VOICE MANAGER] FMOD_System_CreateSound OK");
        javafmod.FMOD_System_SetRecordVolume(1L - Math.round(Math.pow(1.4, 11 - this.volumeMic)));
        if (this.initialiseRecDev) {
            final int fmod_System_RecordStart = javafmod.FMOD_System_RecordStart(this.FMODVoiceRecordDriverId, this.recSound, true);
            if (fmod_System_RecordStart != FMOD_RESULT.FMOD_OK.ordinal()) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, fmod_System_RecordStart));
            }
        }
        javafmod.FMOD_System_SetVADMode(this.vadMode - 1);
        VoiceManager.recBuf = new FMODSoundBuffer(this.recSound);
        if (this.isDebug) {
            VoiceDebug.createAndShowGui();
        }
        this.initialisedRecDev = true;
        this.RecDevSemaphore.release();
        return 0;
    }
    
    int VoiceConnectReq(final long n) {
        if (!this.isEnable) {
            return 0;
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, n));
        final RakVoice voice = this.voice;
        RakVoice.RequestVoiceChannel(n);
        return 0;
    }
    
    int VoiceConnectClose(final long n) {
        if (!this.isEnable) {
            return 0;
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, n));
        final RakVoice voice = this.voice;
        RakVoice.CloseVoiceChannel(n);
        return 0;
    }
    
    public static void GetDataCallbackRnd(final int n, final short[] array) {
        final Random random = new Random();
        for (int i = 0; i < array.length; ++i) {
            array[i] = (short)random.nextInt();
        }
    }
    
    public static void GetDataCallback100Hz(final short[] array) {
        DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, array.length));
        for (int i = 0; i < array.length; ++i) {
            array[i] = (short)(Math.sin(6.283185307179586 * (VoiceManager.tem_t / 8000.0) * 100.0) * 16000.0);
            VoiceManager.tem_t = (VoiceManager.tem_t + 1L) % 8000L;
        }
    }
    
    public void setMode(final int n) {
        if (n == 3) {
            this.isModeVAD = false;
            this.isModePPT = false;
        }
        else if (n == 1) {
            this.isModeVAD = false;
            this.isModePPT = true;
        }
        else if (n == 2) {
            this.isModeVAD = true;
            this.isModePPT = false;
        }
    }
    
    public void setVADMode(final int vadMode) {
        if (vadMode < 1 | vadMode > 4) {
            return;
        }
        this.vadMode = vadMode;
        if (!this.initialisedRecDev) {
            return;
        }
        javafmod.FMOD_System_SetVADMode(this.vadMode - 1);
    }
    
    public void setVolumePlayers(final int volumePlayers) {
        if (volumePlayers < 0 | volumePlayers > 11) {
            return;
        }
        if (volumePlayers <= 10) {
            this.volumePlayers = volumePlayers;
        }
        else {
            this.volumePlayers = 12;
        }
        if (!this.initialisedRecDev) {
            return;
        }
        final ArrayList<VoiceManagerData> data = VoiceManagerData.data;
        for (int i = 0; i < data.size(); ++i) {
            final VoiceManagerData voiceManagerData = data.get(i);
            if (voiceManagerData != null) {
                if (voiceManagerData.userplaychannel != 0L) {
                    javafmod.FMOD_Channel_SetVolume(voiceManagerData.userplaychannel, (float)(this.volumePlayers * 0.2));
                }
            }
        }
    }
    
    public void setVolumeMic(final int volumeMic) {
        if (volumeMic < 0 | volumeMic > 11) {
            return;
        }
        if (volumeMic <= 10) {
            this.volumeMic = volumeMic;
        }
        else {
            this.volumeMic = 12;
        }
        if (!this.initialisedRecDev) {
            return;
        }
        javafmod.FMOD_System_SetRecordVolume(1L - Math.round(Math.pow(1.4, 11 - this.volumeMic)));
    }
    
    public static void playerSetMute(final String s) {
        final ArrayList<IsoPlayer> players = GameClient.instance.getPlayers();
        for (int i = 0; i < players.size(); ++i) {
            final IsoPlayer isoPlayer = players.get(i);
            if (s.equals(isoPlayer.username)) {
                final VoiceManagerData value = VoiceManagerData.get(isoPlayer.OnlineID);
                value.userplaymute = !value.userplaymute;
                isoPlayer.isVoiceMute = value.userplaymute;
                break;
            }
        }
    }
    
    public static boolean playerGetMute(final String s) {
        final ArrayList<IsoPlayer> players = GameClient.instance.getPlayers();
        for (int i = 0; i < players.size(); ++i) {
            final IsoPlayer isoPlayer = players.get(i);
            if (s.equals(isoPlayer.username)) {
                return VoiceManagerData.get(isoPlayer.OnlineID).userplaymute;
            }
        }
        return true;
    }
    
    public void LuaRegister(final Platform platform, final KahluaTable kahluaTable) {
        final KahluaTable table = platform.newTable();
        table.rawset((Object)"playerSetMute", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                VoiceManager.playerSetMute((String)luaCallFrame.get(1));
                return 1;
            }
        });
        table.rawset((Object)"playerGetMute", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                luaCallFrame.push((Object)VoiceManager.playerGetMute((String)luaCallFrame.get(1)));
                return 1;
            }
        });
        table.rawset((Object)"RecordDevices", (Object)new JavaFunction() {
            public int call(final LuaCallFrame luaCallFrame, final int n) {
                if (Core.SoundDisabled || VoiceManager.VoipDisabled) {
                    luaCallFrame.push((Object)luaCallFrame.getPlatform().newTable());
                    return 1;
                }
                final int fmod_System_GetRecordNumDrivers = javafmod.FMOD_System_GetRecordNumDrivers();
                final KahluaTable table = luaCallFrame.getPlatform().newTable();
                for (int i = 0; i < fmod_System_GetRecordNumDrivers; ++i) {
                    final FMOD_DriverInfo fmod_DriverInfo = new FMOD_DriverInfo();
                    javafmod.FMOD_System_GetRecordDriverInfo(i, fmod_DriverInfo);
                    table.rawset(i + 1, (Object)fmod_DriverInfo.name);
                }
                luaCallFrame.push((Object)table);
                return 1;
            }
        });
        kahluaTable.rawset((Object)"VoiceManager", (Object)table);
    }
    
    private long getuserplaysound(final short n) {
        final VoiceManagerData value = VoiceManagerData.get(n);
        if (value.userplaychannel == 0L) {
            value.userplaysound = 0L;
            if (VoiceManager.is3D) {
                value.userplaysound = javafmod.FMOD_System_CreateRAWPlaySound((long)(FMODManager.FMOD_3D | FMODManager.FMOD_OPENUSER | FMODManager.FMOD_LOOP_NORMAL | FMODManager.FMOD_CREATESTREAM), (long)FMODManager.FMOD_SOUND_FORMAT_PCM16, (long)VoiceManager.discretizate);
            }
            else {
                value.userplaysound = javafmod.FMOD_System_CreateRAWPlaySound((long)(FMODManager.FMOD_OPENUSER | FMODManager.FMOD_LOOP_NORMAL | FMODManager.FMOD_CREATESTREAM), (long)FMODManager.FMOD_SOUND_FORMAT_PCM16, (long)VoiceManager.discretizate);
            }
            if (value.userplaysound == 0L) {
                DebugLog.log("[VOICE MANAGER] Error: FMOD_System_CreateSound return zero");
            }
            value.userplaychannel = javafmod.FMOD_System_PlaySound(value.userplaysound, false);
            if (value.userplaychannel == 0L) {
                DebugLog.log("[VOICE MANAGER] Error: FMOD_System_PlaySound return zero");
            }
            javafmod.FMOD_Channel_SetVolume(value.userplaychannel, (float)(this.volumePlayers * 0.2));
            javafmod.FMOD_Channel_Set3DMinMaxDistance(value.userplaychannel, VoiceManager.minDistance, VoiceManager.maxDistance);
            javafmod.FMOD_Channel_SetChannelGroup(value.userplaychannel, this.fmod_channel_group_voip);
        }
        return value.userplaysound;
    }
    
    public void InitVMClient() {
        if (Core.SoundDisabled || VoiceManager.VoipDisabled) {
            this.isEnable = false;
            this.initialiseRecDev = false;
            this.initialisedRecDev = false;
            DebugLog.log("[VOICE MANAGER] Init: Disable");
            return;
        }
        final int fmod_System_GetRecordNumDrivers = javafmod.FMOD_System_GetRecordNumDrivers();
        this.FMODVoiceRecordDriverId = Core.getInstance().getOptionVoiceRecordDevice() - 1;
        if (this.FMODVoiceRecordDriverId < 0 && fmod_System_GetRecordNumDrivers > 0) {
            Core.getInstance().setOptionVoiceRecordDevice(1);
            this.FMODVoiceRecordDriverId = Core.getInstance().getOptionVoiceRecordDevice() - 1;
        }
        if (fmod_System_GetRecordNumDrivers < 1) {
            DebugLog.log("[VOICE MANAGER] Any microphone not found");
            this.initialiseRecDev = false;
        }
        else if (this.FMODVoiceRecordDriverId < 0 | this.FMODVoiceRecordDriverId >= fmod_System_GetRecordNumDrivers) {
            DebugLog.log("[VOICE MANAGER] Invalid record device");
            this.initialiseRecDev = false;
        }
        else {
            this.initialiseRecDev = true;
        }
        DebugLog.log("[VOICE MANAGER] Init: Start");
        this.isEnable = Core.getInstance().getOptionVoiceEnable();
        this.setMode(Core.getInstance().getOptionVoiceMode());
        this.vadMode = Core.getInstance().getOptionVoiceVADMode();
        this.volumeMic = Core.getInstance().getOptionVoiceVolumeMic();
        this.volumePlayers = Core.getInstance().getOptionVoiceVolumePlayers();
        if (!this.isEnable) {
            DebugLog.log("[VOICE MANAGER] Disabled");
            return;
        }
        this.fmod_channel_group_voip = javafmod.FMOD_System_CreateChannelGroup("VOIP");
        this.VoiceInitClient();
        this.recSound = 0L;
        this.InitRecDeviceForTest();
        if (this.isDebug) {
            VoiceDebug.createAndShowGui();
        }
        DebugLog.log("[VOICE MANAGER] Init: End");
        this.time_last = System.currentTimeMillis();
        this.bQuit = false;
        (this.thread = new Thread() {
            @Override
            public void run() {
                while (!VoiceManager.this.bQuit && !VoiceManager.this.bQuit) {
                    try {
                        VoiceManager.this.UpdateVMClient();
                        Thread.sleep(VoiceManager.period / 2);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).setName("VoiceManagerClient");
        this.thread.start();
    }
    
    public void loadConfig() {
        this.isEnable = Core.getInstance().getOptionVoiceEnable();
        this.setMode(Core.getInstance().getOptionVoiceMode());
        this.vadMode = Core.getInstance().getOptionVoiceVADMode();
        this.volumeMic = Core.getInstance().getOptionVoiceVolumeMic();
        this.volumePlayers = Core.getInstance().getOptionVoiceVolumePlayers();
    }
    
    public void UpdateRecordDevice() {
        if (!this.initialisedRecDev) {
            return;
        }
        final int fmod_System_RecordStop = javafmod.FMOD_System_RecordStop(this.FMODVoiceRecordDriverId);
        if (fmod_System_RecordStop != FMOD_RESULT.FMOD_OK.ordinal()) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, fmod_System_RecordStop));
        }
        this.FMODVoiceRecordDriverId = Core.getInstance().getOptionVoiceRecordDevice() - 1;
        if (this.FMODVoiceRecordDriverId < 0) {
            DebugLog.log("[VOICE MANAGER] Error: No record device found");
            return;
        }
        final int fmod_System_RecordStart = javafmod.FMOD_System_RecordStart(this.FMODVoiceRecordDriverId, this.recSound, true);
        if (fmod_System_RecordStart != FMOD_RESULT.FMOD_OK.ordinal()) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, fmod_System_RecordStart));
        }
    }
    
    public void DeinitVMClient() {
        if (this.thread != null) {
            this.bQuit = true;
            synchronized (this.notifier) {
                this.notifier.notify();
            }
            while (this.thread.isAlive()) {
                try {
                    Thread.sleep(10L);
                }
                catch (InterruptedException ex) {}
            }
            this.thread = null;
        }
        if (this.recSound != 0L) {
            javafmod.FMOD_RecordSound_Release(this.recSound);
        }
        final ArrayList<VoiceManagerData> data = VoiceManagerData.data;
        for (int i = 0; i < data.size(); ++i) {
            final VoiceManagerData voiceManagerData = data.get(i);
            if (voiceManagerData.userplaychannel != 0L) {
                javafmod.FMOD_Channel_Stop(voiceManagerData.userplaychannel);
            }
            if (voiceManagerData.userplaysound != 0L) {
                javafmod.FMOD_RAWPlaySound_Release(voiceManagerData.userplaysound);
                voiceManagerData.userplaysound = 0L;
            }
        }
        VoiceManagerData.data.clear();
    }
    
    void debug_print(final byte[] array, final Long n) {
        final long[] array2 = new long[16];
        final long n2 = n / 16L;
        for (int i = 0; i < 16; ++i) {
            array2[i] = 0L;
            for (int n3 = 0; n3 < n2; ++n3) {
                final long[] array3 = array2;
                final int n4 = i;
                array3[n4] += array[i * (int)n2 + n3];
            }
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(JJJJJJJJJJJJJJJ)Ljava/lang/String;, array2[0], array2[2], array2[3], array2[4], array2[5], array2[6], array2[7], array2[8], array2[9], array2[10], array2[11], array2[12], array2[13], array2[14], array2[15]));
    }
    
    public void setTestingMicrophone(final boolean bTestingMicrophone) {
        if (bTestingMicrophone) {
            this.testingMicrophoneMS = System.currentTimeMillis();
        }
        if (bTestingMicrophone != this.bTestingMicrophone) {
            this.bTestingMicrophone = bTestingMicrophone;
            this.notifyThread();
        }
    }
    
    public void notifyThread() {
        synchronized (this.notifier) {
            this.notifier.notify();
        }
    }
    
    public void update() {
        if (GameServer.bServer) {
            return;
        }
        if (this.bTestingMicrophone && System.currentTimeMillis() - this.testingMicrophoneMS > 1000L) {
            this.setTestingMicrophone(false);
        }
        if (GameClient.bClient && GameClient.connection != null) {
            if (!this.bIsClient) {
                this.bIsClient = true;
                this.notifyThread();
            }
        }
        else if (this.bIsClient) {
            this.bIsClient = false;
            this.notifyThread();
        }
    }
    
    synchronized void UpdateVMClient() throws InterruptedException {
        while (!this.bQuit && !this.bIsClient && !this.bTestingMicrophone) {
            DebugLog.log("[VOICE MANAGER] UpdateVMClient going to sleep");
            synchronized (this.notifier) {
                try {
                    this.notifier.wait();
                }
                catch (InterruptedException ex) {}
            }
            DebugLog.log("[VOICE MANAGER] UpdateVMClient woke up");
        }
        if (!VoiceManager.serverVOIPEnable) {
            return;
        }
        if (IsoPlayer.getInstance() != null) {
            IsoPlayer.getInstance().isSpeek = (System.currentTimeMillis() - this.indicator_is_voice <= 300L);
        }
        if (this.initialiseRecDev) {
            this.RecDevSemaphore.acquire();
            javafmod.FMOD_System_GetRecordPosition(this.FMODVoiceRecordDriverId, this.recBuf_Current_read);
            if (VoiceManager.recBuf != null) {
                while (VoiceManager.recBuf.pull((long)this.recBuf_Current_read)) {
                    if (IsoPlayer.getInstance() != null) {
                        final GameClient instance = GameClient.instance;
                        if (GameClient.connection != null) {
                            if (VoiceManager.is3D && IsoPlayer.getInstance().isDead()) {
                                continue;
                            }
                            if (this.isModePPT && GameKeyboard.isKeyDown(Core.getInstance().getKey("Enable voice transmit"))) {
                                final RakVoice voice = this.voice;
                                final GameClient instance2 = GameClient.instance;
                                RakVoice.SendFrame(GameClient.connection.connectedGUID, IsoPlayer.getInstance().OnlineID, VoiceManager.recBuf.buf(), VoiceManager.recBuf.get_size());
                                this.indicator_is_voice = System.currentTimeMillis();
                            }
                            if (this.isModeVAD && VoiceManager.recBuf.get_vad() != 0L) {
                                final RakVoice voice2 = this.voice;
                                final GameClient instance3 = GameClient.instance;
                                RakVoice.SendFrame(GameClient.connection.connectedGUID, IsoPlayer.getInstance().OnlineID, VoiceManager.recBuf.buf(), VoiceManager.recBuf.get_size());
                                this.indicator_is_voice = System.currentTimeMillis();
                            }
                        }
                    }
                    if (this.isDebug) {
                        if (GameClient.IDToPlayerMap.values().size() > 0) {
                            VoiceDebug.updateGui(null, VoiceManager.recBuf);
                        }
                        else if (this.isDebugLoopback) {
                            VoiceDebug.updateGui(null, VoiceManager.recBuf);
                        }
                        else {
                            VoiceDebug.updateGui(null, VoiceManager.recBuf);
                        }
                    }
                    if (this.isDebugLoopback) {
                        javafmod.FMOD_System_RAWPlayData(this.getuserplaysound((short)0), VoiceManager.recBuf.buf(), VoiceManager.recBuf.get_size());
                    }
                }
            }
            this.RecDevSemaphore.release();
        }
        final ArrayList<IsoPlayer> players = GameClient.instance.getPlayers();
        final ArrayList<VoiceManagerData> data = VoiceManagerData.data;
        for (int i = 0; i < data.size(); ++i) {
            final VoiceManagerData voiceManagerData = data.get(i);
            boolean b = false;
            for (int j = 0; j < players.size(); ++j) {
                if (players.get(j).OnlineID == voiceManagerData.index) {
                    b = true;
                    break;
                }
            }
            if (this.isDebugLoopback & voiceManagerData.index == 0) {
                break;
            }
            if (voiceManagerData.userplaychannel != 0L & !b) {
                javafmod.FMOD_Channel_Stop(voiceManagerData.userplaychannel);
                voiceManagerData.userplaychannel = 0L;
            }
        }
        final long n = System.currentTimeMillis() - this.time_last;
        if (n >= VoiceManager.period) {
            this.time_last += n;
            if (IsoPlayer.getInstance() == null) {
                return;
            }
            for (int k = 0; k < players.size(); ++k) {
                final IsoPlayer isoPlayer = players.get(k);
                if (isoPlayer != IsoPlayer.getInstance()) {
                    final VoiceManagerData value = VoiceManagerData.get(isoPlayer.OnlineID);
                    while (true) {
                        final RakVoice voice3 = this.voice;
                        if (!RakVoice.ReceiveFrame(isoPlayer.OnlineID, this.buf)) {
                            break;
                        }
                        value.voicetimeout = 10L;
                        if (value.userplaymute) {
                            continue;
                        }
                        if (IsoPlayer.getInstance().isCanHearAll()) {
                            javafmod.FMOD_Channel_Set3DAttributes(value.userplaychannel, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, IsoPlayer.getInstance().z, 0.0f, 0.0f, 0.0f);
                        }
                        else if (VoiceManager.is3D) {
                            final ArrayList<Integer> checkForNearbyRadios = this.checkForNearbyRadios(isoPlayer);
                            logFrame(isoPlayer, this.myRadio);
                            if (this.myRadio != null) {
                                javafmod.FMOD_Channel_SetVolume(value.userplaychannel, this.myRadio.getDeviceData().getDeviceVolume());
                            }
                            else {
                                javafmod.FMOD_Channel_SetVolume(value.userplaychannel, 1.0f);
                            }
                            if (!checkForNearbyRadios.isEmpty()) {
                                javafmod.FMOD_Channel_Set3DAttributes(value.userplaychannel, (float)checkForNearbyRadios.get(0), (float)checkForNearbyRadios.get(1), (float)checkForNearbyRadios.get(2), 0.0f, 0.0f, 0.0f);
                            }
                            else {
                                javafmod.FMOD_Channel_Set3DAttributes(value.userplaychannel, isoPlayer.x, isoPlayer.y, isoPlayer.z, 0.0f, 0.0f, 0.0f);
                            }
                        }
                        javafmod.FMOD_System_RAWPlayData(this.getuserplaysound(isoPlayer.OnlineID), this.buf, (long)this.buf.length);
                        if (!this.isDebugLoopbackLong) {
                            continue;
                        }
                        final RakVoice voice4 = this.voice;
                        final GameClient instance4 = GameClient.instance;
                        RakVoice.SendFrame(GameClient.connection.connectedGUID, IsoPlayer.getInstance().OnlineID, this.buf, this.buf.length);
                    }
                    if (value.voicetimeout == 0L) {
                        isoPlayer.isSpeek = false;
                    }
                    else {
                        final VoiceManagerData voiceManagerData2 = value;
                        --voiceManagerData2.voicetimeout;
                        isoPlayer.isSpeek = true;
                    }
                }
            }
        }
    }
    
    private static void logFrame(final IsoPlayer isoPlayer, final Radio radio) {
        if (GameClient.bClient && IsoPlayer.getInstance() != null && isoPlayer != null) {
            final float distanceTo = IsoUtils.DistanceTo(IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY(), isoPlayer.getX(), isoPlayer.getY());
            if (distanceTo > VoiceManager.maxDistance) {
                final long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis > VoiceManager.timestamp) {
                    VoiceManager.timestamp = currentTimeMillis + 5000L;
                    DebugLog.Multiplayer.warn((Object)String.format("player \"%s\" (cheat=%b) freqs=[%s] received VOIP frame from distant player \"%s\" (cheat=%b) freqs=[%s] at distance=%f with radio=%b", IsoPlayer.getInstance().getUsername(), IsoPlayer.getInstance().isCanHearAll(), IsoPlayer.getInstance().invRadioFreq.stream().map((Function<? super Object, ?>)Object::toString).collect((Collector<? super Object, ?, String>)Collectors.joining(", ")), isoPlayer.getUsername(), isoPlayer.isCanHearAll(), isoPlayer.invRadioFreq.stream().map((Function<? super Object, ?>)Object::toString).collect((Collector<? super Object, ?, String>)Collectors.joining(", ")), distanceTo, radio != null));
                }
            }
        }
    }
    
    private ArrayList<Integer> checkForNearbyRadios(final IsoPlayer isoPlayer) {
        final ArrayList<Integer> list = new ArrayList<Integer>();
        this.myRadio = null;
        final IsoPlayer instance = IsoPlayer.getInstance();
        for (int i = 0; i < instance.getInventory().getItems().size(); ++i) {
            final InventoryItem inventoryItem = instance.getInventory().getItems().get(i);
            if (inventoryItem instanceof Radio) {
                final Radio myRadio = (Radio)inventoryItem;
                if (myRadio.getDeviceData() != null && myRadio.getDeviceData().getIsTurnedOn() && isoPlayer.invRadioFreq.contains(myRadio.getDeviceData().getChannel())) {
                    list.add((int)instance.x);
                    list.add((int)instance.y);
                    list.add((int)instance.z);
                    this.myRadio = myRadio;
                    break;
                }
            }
        }
        if (list.isEmpty()) {
            for (int n = (int)instance.getX() - 4; n < instance.getX() + 5.0f; ++n) {
                for (int n2 = (int)instance.getY() - 4; n2 < instance.getY() + 5.0f; ++n2) {
                    for (int n3 = (int)instance.getZ() - 1; n3 < instance.getZ() + 1.0f; ++n3) {
                        final IsoGridSquare gridSquare = IsoCell.getInstance().getGridSquare(n, n2, n3);
                        if (gridSquare != null && gridSquare.getWorldObjects() != null) {
                            for (int j = 0; j < gridSquare.getWorldObjects().size(); ++j) {
                                final IsoWorldInventoryObject isoWorldInventoryObject = gridSquare.getWorldObjects().get(j);
                                if (isoWorldInventoryObject.getItem() != null && isoWorldInventoryObject.getItem() instanceof Radio) {
                                    final Radio myRadio2 = (Radio)isoWorldInventoryObject.getItem();
                                    if (myRadio2.getDeviceData() != null && myRadio2.getDeviceData().getIsTurnedOn() && isoPlayer.invRadioFreq.contains(myRadio2.getDeviceData().getChannel())) {
                                        list.add(gridSquare.x);
                                        list.add(gridSquare.y);
                                        list.add(gridSquare.z);
                                        this.myRadio = myRadio2;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return list;
    }
    
    void InitVMServer() {
        this.VoiceInitServer(ServerOptions.instance.VoiceEnable.getValue(), ServerOptions.instance.VoiceSampleRate.getValue(), ServerOptions.instance.VoicePeriod.getValue(), ServerOptions.instance.VoiceComplexity.getValue(), ServerOptions.instance.VoiceBuffering.getValue(), ServerOptions.instance.VoiceMinDistance.getValue(), ServerOptions.instance.VoiceMaxDistance.getValue(), ServerOptions.instance.Voice3D.getValue());
    }
    
    public int getMicVolumeIndicator() {
        if (VoiceManager.recBuf == null) {
            return 0;
        }
        return (int)VoiceManager.recBuf.get_loudness();
    }
    
    public boolean getMicVolumeError() {
        return VoiceManager.recBuf == null || VoiceManager.recBuf.get_interror();
    }
    
    public boolean getServerVOIPEnable() {
        return VoiceManager.serverVOIPEnable;
    }
    
    public void VMServerBan(final short n, final boolean b) {
        final RakVoice voice = this.voice;
        RakVoice.SetVoiceBan(n, b);
    }
    
    static {
        VoiceManager.instance = new VoiceManager();
        VoiceManager.serverVOIPEnable = true;
        VoiceManager.discretizate = 16000;
        VoiceManager.period = 300;
        VoiceManager.complexity = 1;
        VoiceManager.buffering = 8000;
        VoiceManager.is3D = false;
        VoiceManager.VoipDisabled = false;
        VoiceManager.timestamp = 0L;
    }
}
