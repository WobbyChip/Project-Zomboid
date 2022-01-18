// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio;

import zombie.chat.ChatMessage;
import zombie.iso.weather.ClimateManager;
import zombie.inventory.types.Radio;
import zombie.core.Color;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.characters.IsoPlayer;
import zombie.radio.devices.DeviceData;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import zombie.radio.scripting.RadioScript;
import zombie.core.logger.ExceptionLogger;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.radio.scripting.RadioChannel;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.network.GameClient;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.GameWindow;
import zombie.core.network.ByteBufferWriter;
import zombie.core.Rand;
import java.util.Iterator;
import zombie.chat.ChatElement;
import zombie.GameTime;
import java.util.HashMap;
import zombie.radio.media.RecordedMedia;
import zombie.radio.StorySounds.SLSoundManager;
import java.util.List;
import java.util.Map;
import zombie.radio.scripting.RadioScriptManager;
import zombie.radio.devices.WaveSignalDevice;
import java.util.ArrayList;

public final class ZomboidRadio
{
    public static final String SAVE_FILE = "RADIO_SAVE.txt";
    private final ArrayList<WaveSignalDevice> devices;
    private final ArrayList<WaveSignalDevice> broadcastDevices;
    private RadioScriptManager scriptManager;
    private int DaysSinceStart;
    private int lastRecordedHour;
    private final String[] playerLastLine;
    private final Map<Integer, String> channelNames;
    private final Map<String, Map<Integer, String>> categorizedChannels;
    private final List<Integer> knownFrequencies;
    private RadioDebugConsole debugConsole;
    private boolean hasRecievedServerData;
    private SLSoundManager storySoundManager;
    private static final String[] staticSounds;
    public static boolean DEBUG_MODE;
    public static boolean DEBUG_XML;
    public static boolean DEBUG_SOUND;
    public static boolean POST_RADIO_SILENCE;
    public static boolean DISABLE_BROADCASTING;
    private static ZomboidRadio instance;
    private static RecordedMedia recordedMedia;
    public static boolean LOUISVILLE_OBFUSCATION;
    private String lastSaveFile;
    private String lastSaveContent;
    private HashMap<Integer, FreqListEntry> freqlist;
    private boolean hasAppliedRangeDistortion;
    private StringBuilder stringBuilder;
    private boolean hasAppliedInterference;
    private static int[] obfuscateChannels;
    
    public static boolean hasInstance() {
        return ZomboidRadio.instance != null;
    }
    
    public static ZomboidRadio getInstance() {
        if (ZomboidRadio.instance == null) {
            ZomboidRadio.instance = new ZomboidRadio();
        }
        return ZomboidRadio.instance;
    }
    
    private ZomboidRadio() {
        this.devices = new ArrayList<WaveSignalDevice>();
        this.broadcastDevices = new ArrayList<WaveSignalDevice>();
        this.DaysSinceStart = 0;
        this.playerLastLine = new String[4];
        this.channelNames = new HashMap<Integer, String>();
        this.categorizedChannels = new HashMap<String, Map<Integer, String>>();
        this.knownFrequencies = new ArrayList<Integer>();
        this.hasRecievedServerData = false;
        this.storySoundManager = null;
        this.freqlist = new HashMap<Integer, FreqListEntry>();
        this.hasAppliedRangeDistortion = false;
        this.stringBuilder = new StringBuilder();
        this.hasAppliedInterference = false;
        this.lastRecordedHour = GameTime.instance.getHour();
        SLSoundManager.DEBUG = ZomboidRadio.DEBUG_SOUND;
        for (int i = 0; i < ZomboidRadio.staticSounds.length; ++i) {
            ChatElement.addNoLogText(ZomboidRadio.staticSounds[i]);
        }
        ChatElement.addNoLogText("~");
        ZomboidRadio.recordedMedia = new RecordedMedia();
    }
    
    public static boolean isStaticSound(final String s) {
        if (s != null) {
            final String[] staticSounds = ZomboidRadio.staticSounds;
            for (int length = staticSounds.length, i = 0; i < length; ++i) {
                if (s.equals(staticSounds[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public RadioScriptManager getScriptManager() {
        return this.scriptManager;
    }
    
    public int getDaysSinceStart() {
        return this.DaysSinceStart;
    }
    
    public ArrayList<WaveSignalDevice> getDevices() {
        return this.devices;
    }
    
    public ArrayList<WaveSignalDevice> getBroadcastDevices() {
        return this.broadcastDevices;
    }
    
    public void setHasRecievedServerData(final boolean hasRecievedServerData) {
        this.hasRecievedServerData = hasRecievedServerData;
    }
    
    public void addChannelName(final String s, final int n, final String s2) {
        this.addChannelName(s, n, s2, true);
    }
    
    public void addChannelName(final String s, final int n, final String s2, final boolean b) {
        if (b || !this.channelNames.containsKey(n)) {
            if (!this.categorizedChannels.containsKey(s2)) {
                this.categorizedChannels.put(s2, new HashMap<Integer, String>());
            }
            this.categorizedChannels.get(s2).put(n, s);
            this.channelNames.put(n, s);
            this.knownFrequencies.add(n);
        }
    }
    
    public void removeChannelName(final int n) {
        if (this.channelNames.containsKey(n)) {
            this.channelNames.remove(n);
            for (final Map.Entry<String, Map<Integer, String>> entry : this.categorizedChannels.entrySet()) {
                if (entry.getValue().containsKey(n)) {
                    entry.getValue().remove(n);
                }
            }
        }
    }
    
    public Map<Integer, String> GetChannelList(final String s) {
        if (this.categorizedChannels.containsKey(s)) {
            return this.categorizedChannels.get(s);
        }
        return null;
    }
    
    public String getChannelName(final int n) {
        if (this.channelNames.containsKey(n)) {
            return this.channelNames.get(n);
        }
        return null;
    }
    
    public int getRandomFrequency() {
        return this.getRandomFrequency(88000, 108000);
    }
    
    public int getRandomFrequency(final int n, final int n2) {
        int i;
        do {
            i = Rand.Next(n, n2) / 200 * 200;
        } while (this.knownFrequencies.contains(i));
        return i;
    }
    
    public Map<String, Map<Integer, String>> getFullChannelList() {
        return this.categorizedChannels;
    }
    
    public void WriteRadioServerDataPacket(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(this.categorizedChannels.size());
        for (final Map.Entry<String, Map<Integer, String>> entry : this.categorizedChannels.entrySet()) {
            GameWindow.WriteString(byteBufferWriter.bb, entry.getKey());
            byteBufferWriter.putInt(entry.getValue().size());
            for (final Map.Entry<Integer, String> entry2 : entry.getValue().entrySet()) {
                byteBufferWriter.putInt(entry2.getKey());
                GameWindow.WriteString(byteBufferWriter.bb, entry2.getValue());
            }
        }
    }
    
    public void Init(final int n) {
        boolean b = false;
        final boolean enabled = DebugLog.isEnabled(DebugType.Radio);
        if (enabled) {
            DebugLog.Radio.println("");
            DebugLog.Radio.println("################## Radio Init ##################");
        }
        RadioAPI.getInstance();
        ZomboidRadio.recordedMedia.init();
        this.lastRecordedHour = GameTime.instance.getHour();
        final GameMode gameMode = this.getGameMode();
        if (ZomboidRadio.DEBUG_MODE && !gameMode.equals(GameMode.Server)) {
            DebugLog.setLogEnabled(DebugType.Radio, true);
            this.debugConsole = new RadioDebugConsole();
        }
        if (gameMode.equals(GameMode.Client)) {
            GameClient.sendRadioServerDataRequest();
            if (enabled) {
                DebugLog.Radio.println("Radio (Client) loaded.");
                DebugLog.Radio.println("################################################");
            }
            return;
        }
        (this.scriptManager = RadioScriptManager.getInstance()).init(n);
        try {
            if (!Core.getInstance().isNoSave()) {
                ZomboidFileSystem.instance.getFileInCurrentSave("radio", "data").mkdirs();
            }
            final Iterator<RadioData> iterator = RadioData.fetchAllRadioData().iterator();
            while (iterator.hasNext()) {
                for (final RadioChannel radioChannel : iterator.next().getRadioChannels()) {
                    ObfuscateChannelCheck(radioChannel);
                    RadioChannel radioChannel2 = null;
                    if (this.scriptManager.getChannels().containsKey(radioChannel.GetFrequency())) {
                        radioChannel2 = this.scriptManager.getChannels().get(radioChannel.GetFrequency());
                    }
                    if (radioChannel2 == null || (radioChannel2.getRadioData().isVanilla() && !radioChannel.getRadioData().isVanilla())) {
                        this.scriptManager.AddChannel(radioChannel, true);
                    }
                    else {
                        if (!enabled) {
                            continue;
                        }
                        DebugLog.Radio.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, radioChannel.GetName(), radioChannel.GetFrequency()));
                    }
                }
            }
            LuaEventManager.triggerEvent("OnLoadRadioScripts", this.scriptManager, n == -1);
            if (n == -1) {
                if (enabled) {
                    DebugLog.Radio.println("Radio setting new game start times");
                }
                final SandboxOptions instance = SandboxOptions.instance;
                int n2 = instance.TimeSinceApo.getValue() - 1;
                if (n2 < 0) {
                    n2 = 0;
                }
                if (enabled) {
                    DebugLog.log(DebugType.Radio, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, instance.TimeSinceApo.getValue()));
                }
                if (n2 > 0) {
                    this.DaysSinceStart = (int)(n2 * 30.5f);
                    if (enabled) {
                        DebugLog.Radio.println(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.DaysSinceStart));
                    }
                    this.scriptManager.simulateScriptsUntil(this.DaysSinceStart, true);
                }
                this.checkGameModeSpecificStart();
            }
            else {
                if (!this.Load()) {
                    int n3 = SandboxOptions.instance.TimeSinceApo.getValue() - 1;
                    if (n3 < 0) {
                        n3 = 0;
                    }
                    this.DaysSinceStart = (int)(n3 * 30.5f);
                    this.DaysSinceStart += GameTime.instance.getNightsSurvived();
                }
                if (this.DaysSinceStart > 0) {
                    this.scriptManager.simulateScriptsUntil(this.DaysSinceStart, false);
                }
            }
            b = true;
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
        if (!enabled) {
            return;
        }
        if (b) {
            DebugLog.Radio.println("Radio loaded.");
        }
        DebugLog.Radio.println("################################################");
        DebugLog.Radio.println("");
    }
    
    private void checkGameModeSpecificStart() {
        if (Core.GameMode.equals("Initial Infection")) {
            for (final Map.Entry<Integer, RadioChannel> entry : this.scriptManager.getChannels().entrySet()) {
                final RadioScript radioScript = entry.getValue().getRadioScript("init_infection");
                if (radioScript != null) {
                    radioScript.clearExitOptions();
                    radioScript.AddExitOption(entry.getValue().getCurrentScript().GetName(), 100, 0);
                    entry.getValue().setActiveScript("init_infection", this.DaysSinceStart);
                }
                else {
                    entry.getValue().getCurrentScript().setStartDayStamp(this.DaysSinceStart + 1);
                }
            }
        }
        else if (Core.GameMode.equals("Six Months Later")) {
            for (final Map.Entry<Integer, RadioChannel> entry2 : this.scriptManager.getChannels().entrySet()) {
                if (entry2.getValue().GetName().equals("Classified M1A1")) {
                    entry2.getValue().setActiveScript("numbers", this.DaysSinceStart);
                }
                else {
                    if (!entry2.getValue().GetName().equals("NNR Radio")) {
                        continue;
                    }
                    entry2.getValue().setActiveScript("pastor", this.DaysSinceStart);
                }
            }
        }
    }
    
    public void Save() throws FileNotFoundException, IOException {
        if (Core.getInstance().isNoSave()) {
            return;
        }
        final GameMode gameMode = this.getGameMode();
        if (gameMode.equals(GameMode.Server) || gameMode.equals(GameMode.SinglePlayer)) {
            if (this.scriptManager == null) {
                return;
            }
            final File fileInCurrentSave = ZomboidFileSystem.instance.getFileInCurrentSave("radio", "data");
            if (fileInCurrentSave.exists() && fileInCurrentSave.isDirectory()) {
                final String fileNameInCurrentSave = ZomboidFileSystem.instance.getFileNameInCurrentSave("radio", "data", "RADIO_SAVE.txt");
                String string;
                try {
                    final StringWriter stringWriter = new StringWriter(1024);
                    try {
                        stringWriter.write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, this.DaysSinceStart, System.lineSeparator()));
                        stringWriter.write(invokedynamic(makeConcatWithConstants:(ZLjava/lang/String;)Ljava/lang/String;, ZomboidRadio.LOUISVILLE_OBFUSCATION, System.lineSeparator()));
                        this.scriptManager.Save(stringWriter);
                        string = stringWriter.toString();
                        stringWriter.close();
                    }
                    catch (Throwable t) {
                        try {
                            stringWriter.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                        throw t;
                    }
                }
                catch (IOException ex) {
                    ExceptionLogger.logException(ex);
                    return;
                }
                if (fileNameInCurrentSave.equals(this.lastSaveFile) && string.equals(this.lastSaveContent)) {
                    return;
                }
                this.lastSaveFile = fileNameInCurrentSave;
                this.lastSaveContent = string;
                final File file = new File(fileNameInCurrentSave);
                if (DebugLog.isEnabled(DebugType.Radio)) {
                    DebugLog.Radio.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, fileNameInCurrentSave));
                }
                try {
                    final FileWriter fileWriter = new FileWriter(file, false);
                    try {
                        fileWriter.write(string);
                        fileWriter.close();
                    }
                    catch (Throwable t2) {
                        try {
                            fileWriter.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                        throw t2;
                    }
                }
                catch (Exception ex2) {
                    ExceptionLogger.logException(ex2);
                }
            }
        }
        if (ZomboidRadio.recordedMedia != null) {
            try {
                ZomboidRadio.recordedMedia.save();
            }
            catch (Exception ex3) {
                ex3.printStackTrace();
            }
        }
    }
    
    public boolean Load() throws FileNotFoundException, IOException {
        boolean b = false;
        final GameMode gameMode = this.getGameMode();
        if (gameMode.equals(GameMode.Server) || gameMode.equals(GameMode.SinglePlayer)) {
            final Iterator<Map.Entry<Integer, RadioChannel>> iterator = this.scriptManager.getChannels().entrySet().iterator();
            while (iterator.hasNext()) {
                iterator.next().getValue().setActiveScriptNull();
            }
            final String fileNameInCurrentSave = ZomboidFileSystem.instance.getFileNameInCurrentSave("radio", "data", "RADIO_SAVE.txt");
            final File file = new File(fileNameInCurrentSave);
            if (!file.exists()) {
                return false;
            }
            if (DebugLog.isEnabled(DebugType.Radio)) {
                DebugLog.log(DebugType.Radio, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, fileNameInCurrentSave));
            }
            try {
                final FileReader in = new FileReader(file);
                try {
                    final BufferedReader bufferedReader = new BufferedReader(in);
                    try {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            final String trim = line.trim();
                            if (!trim.startsWith("DaysSinceStart") && !trim.startsWith("LvObfuscation")) {
                                this.scriptManager.Load(bufferedReader);
                                b = true;
                                break;
                            }
                            if (trim.startsWith("DaysSinceStart")) {
                                this.DaysSinceStart = Integer.parseInt(trim.split("=")[1].trim());
                            }
                            if (!trim.startsWith("LvObfuscation")) {
                                continue;
                            }
                            ZomboidRadio.LOUISVILLE_OBFUSCATION = Boolean.parseBoolean(trim.split("=")[1].trim());
                        }
                        bufferedReader.close();
                    }
                    catch (Throwable t) {
                        try {
                            bufferedReader.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                        throw t;
                    }
                    in.close();
                }
                catch (Throwable t2) {
                    try {
                        in.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                    throw t2;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return b;
    }
    
    public void Reset() {
        ZomboidRadio.instance = null;
        if (this.scriptManager != null) {
            this.scriptManager.reset();
        }
    }
    
    public void UpdateScripts(final int lastRecordedHour, final int n) {
        final GameMode gameMode = this.getGameMode();
        if (gameMode.equals(GameMode.Server) || gameMode.equals(GameMode.SinglePlayer)) {
            if (lastRecordedHour == 0 && this.lastRecordedHour != 0) {
                ++this.DaysSinceStart;
            }
            this.lastRecordedHour = lastRecordedHour;
            if (this.scriptManager != null) {
                this.scriptManager.UpdateScripts(this.DaysSinceStart, lastRecordedHour, n);
            }
            try {
                this.Save();
            }
            catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        if (gameMode.equals(GameMode.Client) || gameMode.equals(GameMode.SinglePlayer)) {
            for (int i = 0; i < this.devices.size(); ++i) {
                final WaveSignalDevice waveSignalDevice = this.devices.get(i);
                if (waveSignalDevice.getDeviceData().getIsTurnedOn() && waveSignalDevice.HasPlayerInRange()) {
                    waveSignalDevice.getDeviceData().TriggerPlayerListening(true);
                }
            }
        }
        if (gameMode.equals(GameMode.Client) && !this.hasRecievedServerData) {
            GameClient.sendRadioServerDataRequest();
        }
    }
    
    public void render() {
        final GameMode gameMode = this.getGameMode();
        if (ZomboidRadio.DEBUG_MODE && !gameMode.equals(GameMode.Server) && this.debugConsole != null) {
            this.debugConsole.render();
        }
        if (!gameMode.equals(GameMode.Server) && this.storySoundManager != null) {
            this.storySoundManager.render();
        }
    }
    
    private void addFrequencyListEntry(final boolean isInvItem, final DeviceData deviceData, final int sourceX, final int sourceY) {
        if (deviceData == null) {
            return;
        }
        if (!this.freqlist.containsKey(deviceData.getChannel())) {
            this.freqlist.put(deviceData.getChannel(), new FreqListEntry(isInvItem, deviceData, sourceX, sourceY));
        }
        else if (this.freqlist.get(deviceData.getChannel()).deviceData.getTransmitRange() < deviceData.getTransmitRange()) {
            final FreqListEntry freqListEntry = this.freqlist.get(deviceData.getChannel());
            freqListEntry.isInvItem = isInvItem;
            freqListEntry.deviceData = deviceData;
            freqListEntry.sourceX = sourceX;
            freqListEntry.sourceY = sourceY;
        }
    }
    
    public void update() {
        this.LouisvilleObfuscationCheck();
        if (ZomboidRadio.DEBUG_MODE && this.debugConsole != null) {
            this.debugConsole.update();
        }
        final GameMode gameMode = this.getGameMode();
        if (!gameMode.equals(GameMode.Server) && this.storySoundManager != null) {
            this.storySoundManager.update(this.DaysSinceStart, GameTime.instance.getHour(), GameTime.instance.getMinutes());
        }
        if ((gameMode.equals(GameMode.Server) || gameMode.equals(GameMode.SinglePlayer)) && this.scriptManager != null) {
            this.scriptManager.update();
        }
        if (gameMode.equals(GameMode.SinglePlayer) || gameMode.equals(GameMode.Client)) {
            for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                final IsoPlayer isoPlayer = IsoPlayer.players[i];
                if (isoPlayer != null) {
                    if (isoPlayer.getLastSpokenLine() != null && (this.playerLastLine[i] == null || !this.playerLastLine[i].equals(isoPlayer.getLastSpokenLine()))) {
                        this.playerLastLine[i] = isoPlayer.getLastSpokenLine();
                        if (gameMode.equals(GameMode.Client)) {
                            if (isoPlayer.accessLevel.equals("admin") || isoPlayer.accessLevel.equals("gm") || isoPlayer.accessLevel.equals("overseer") || isoPlayer.accessLevel.equals("moderator")) {
                                if (ServerOptions.instance.DisableRadioStaff.getValue()) {
                                    continue;
                                }
                                if (ServerOptions.instance.DisableRadioAdmin.getValue() && isoPlayer.accessLevel.equals("admin")) {
                                    continue;
                                }
                                if (ServerOptions.instance.DisableRadioGM.getValue() && isoPlayer.accessLevel.equals("gm")) {
                                    continue;
                                }
                                if (ServerOptions.instance.DisableRadioOverseer.getValue() && isoPlayer.accessLevel.equals("overseer")) {
                                    continue;
                                }
                                if (ServerOptions.instance.DisableRadioModerator.getValue() && isoPlayer.accessLevel.equals("moderator")) {
                                    continue;
                                }
                            }
                            if (ServerOptions.instance.DisableRadioInvisible.getValue() && isoPlayer.isInvisible()) {
                                continue;
                            }
                        }
                        this.freqlist.clear();
                        if (!GameClient.bClient && !GameServer.bServer) {
                            for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                                this.checkPlayerForDevice(IsoPlayer.players[j], isoPlayer);
                            }
                        }
                        else if (GameClient.bClient) {
                            final ArrayList<IsoPlayer> players = GameClient.instance.getPlayers();
                            for (int k = 0; k < players.size(); ++k) {
                                this.checkPlayerForDevice(players.get(k), isoPlayer);
                            }
                        }
                        for (final WaveSignalDevice waveSignalDevice : this.broadcastDevices) {
                            if (waveSignalDevice != null && waveSignalDevice.getDeviceData() != null && waveSignalDevice.getDeviceData().getIsTurnedOn() && waveSignalDevice.getDeviceData().getIsTwoWay() && waveSignalDevice.HasPlayerInRange() && !waveSignalDevice.getDeviceData().getMicIsMuted() && this.GetDistance((int)isoPlayer.getX(), (int)isoPlayer.getY(), (int)waveSignalDevice.getX(), (int)waveSignalDevice.getY()) < waveSignalDevice.getDeviceData().getMicRange()) {
                                this.addFrequencyListEntry(true, waveSignalDevice.getDeviceData(), (int)waveSignalDevice.getX(), (int)waveSignalDevice.getY());
                            }
                        }
                        if (this.freqlist.size() > 0) {
                            final Color speakColour = isoPlayer.getSpeakColour();
                            for (final Map.Entry<Integer, FreqListEntry> entry : this.freqlist.entrySet()) {
                                final FreqListEntry freqListEntry = entry.getValue();
                                this.SendTransmission(freqListEntry.sourceX, freqListEntry.sourceY, entry.getKey(), this.playerLastLine[i], null, speakColour.r, speakColour.g, speakColour.b, freqListEntry.deviceData.getTransmitRange(), false);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void checkPlayerForDevice(final IsoPlayer isoPlayer, final IsoPlayer isoPlayer2) {
        final boolean b = isoPlayer == isoPlayer2;
        if (isoPlayer != null) {
            final Radio equipedRadio = isoPlayer.getEquipedRadio();
            if (equipedRadio != null && equipedRadio.getDeviceData() != null && equipedRadio.getDeviceData().getIsPortable() && equipedRadio.getDeviceData().getIsTwoWay() && equipedRadio.getDeviceData().getIsTurnedOn() && !equipedRadio.getDeviceData().getMicIsMuted() && (b || this.GetDistance((int)isoPlayer2.getX(), (int)isoPlayer2.getY(), (int)isoPlayer.getX(), (int)isoPlayer.getY()) < equipedRadio.getDeviceData().getMicRange())) {
                this.addFrequencyListEntry(true, equipedRadio.getDeviceData(), (int)isoPlayer.getX(), (int)isoPlayer.getY());
            }
        }
    }
    
    private boolean DeviceInRange(final int n, final int n2, final int n3, final int n4, final int n5) {
        return n > n3 - n5 && n < n3 + n5 && n2 > n4 - n5 && n2 < n4 + n5 && Math.sqrt(Math.pow(n - n3, 2.0) + Math.pow(n2 - n4, 2.0)) < n5;
    }
    
    private int GetDistance(final int n, final int n2, final int n3, final int n4) {
        return (int)Math.sqrt(Math.pow(n - n3, 2.0) + Math.pow(n2 - n4, 2.0));
    }
    
    private void DistributeToPlayer(final IsoPlayer isoPlayer, final int n, final int n2, final int n3, String doDeviceRangeDistortion, final String s, final float n4, final float n5, final float n6, final int n7, final boolean b) {
        if (isoPlayer != null) {
            final Radio equipedRadio = isoPlayer.getEquipedRadio();
            if (equipedRadio != null && equipedRadio.getDeviceData() != null && equipedRadio.getDeviceData().getIsPortable() && equipedRadio.getDeviceData().getIsTurnedOn() && equipedRadio.getDeviceData().getChannel() == n3) {
                if (equipedRadio.getDeviceData().getDeviceVolume() <= 0.0f) {
                    return;
                }
                if (equipedRadio.getDeviceData().isPlayingMedia() || equipedRadio.getDeviceData().isNoTransmit()) {
                    return;
                }
                boolean b2 = false;
                int getDistance = -1;
                if (n7 < 0) {
                    b2 = true;
                }
                else {
                    getDistance = this.GetDistance((int)isoPlayer.getX(), (int)isoPlayer.getY(), n, n2);
                    if (getDistance > 3 && getDistance < n7) {
                        b2 = true;
                    }
                }
                if (b2) {
                    if (n7 > 0) {
                        this.hasAppliedRangeDistortion = false;
                        doDeviceRangeDistortion = this.doDeviceRangeDistortion(doDeviceRangeDistortion, n7, getDistance);
                    }
                    if (!this.hasAppliedRangeDistortion) {
                        equipedRadio.AddDeviceText(doDeviceRangeDistortion, n4, n5, n6, s, getDistance);
                    }
                    else {
                        equipedRadio.AddDeviceText(doDeviceRangeDistortion, 0.5f, 0.5f, 0.5f, s, getDistance);
                    }
                }
            }
        }
    }
    
    private void DistributeTransmission(final int n, final int n2, final int n3, String doDeviceRangeDistortion, final String s, final float n4, final float n5, final float n6, final int n7, final boolean b) {
        if (!b) {
            if (!GameClient.bClient && !GameServer.bServer) {
                for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                    this.DistributeToPlayer(IsoPlayer.players[i], n, n2, n3, doDeviceRangeDistortion, s, n4, n5, n6, n7, b);
                }
            }
            else if (GameClient.bClient) {
                final Iterator<Map.Entry<Short, IsoPlayer>> iterator = GameClient.IDToPlayerMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    this.DistributeToPlayer(iterator.next().getValue(), n, n2, n3, doDeviceRangeDistortion, s, n4, n5, n6, n7, b);
                }
            }
        }
        if (this.devices.size() == 0) {
            return;
        }
        for (int j = 0; j < this.devices.size(); ++j) {
            final WaveSignalDevice waveSignalDevice = this.devices.get(j);
            if (waveSignalDevice != null && waveSignalDevice.getDeviceData() != null && waveSignalDevice.getDeviceData().getIsTurnedOn() && b == waveSignalDevice.getDeviceData().getIsTelevision()) {
                if (waveSignalDevice.getDeviceData().isPlayingMedia() || waveSignalDevice.getDeviceData().isNoTransmit()) {
                    return;
                }
                if (n3 == waveSignalDevice.getDeviceData().getChannel()) {
                    boolean b2 = false;
                    if (n7 == -1) {
                        b2 = true;
                    }
                    else if (n != (int)waveSignalDevice.getX() && n2 != (int)waveSignalDevice.getY()) {
                        b2 = true;
                    }
                    if (b2) {
                        int getDistance = -1;
                        if (n7 > 0) {
                            this.hasAppliedRangeDistortion = false;
                            getDistance = this.GetDistance((int)waveSignalDevice.getX(), (int)waveSignalDevice.getY(), n, n2);
                            doDeviceRangeDistortion = this.doDeviceRangeDistortion(doDeviceRangeDistortion, n7, getDistance);
                        }
                        if (!this.hasAppliedRangeDistortion) {
                            waveSignalDevice.AddDeviceText(doDeviceRangeDistortion, n4, n5, n6, s, getDistance);
                        }
                        else {
                            waveSignalDevice.AddDeviceText(doDeviceRangeDistortion, 0.5f, 0.5f, 0.5f, s, getDistance);
                        }
                    }
                }
            }
        }
    }
    
    private String doDeviceRangeDistortion(String scrambleString, final int n, final int n2) {
        final float n3 = n * 0.9f;
        if (n3 < n && n2 > n3) {
            scrambleString = this.scrambleString(scrambleString, (int)(100.0f * ((n2 - n3) / (n - n3))), false);
            this.hasAppliedRangeDistortion = true;
        }
        return scrambleString;
    }
    
    public GameMode getGameMode() {
        if (!GameClient.bClient && !GameServer.bServer) {
            return GameMode.SinglePlayer;
        }
        if (GameServer.bServer) {
            return GameMode.Server;
        }
        return GameMode.Client;
    }
    
    public String getRandomBzztFzzt() {
        return ZomboidRadio.staticSounds[Rand.Next(ZomboidRadio.staticSounds.length)];
    }
    
    private String applyWeatherInterference(final String s, final int n) {
        if (ClimateManager.getInstance().getWeatherInterference() <= 0.0f) {
            return s;
        }
        return this.scrambleString(s, (int)(ClimateManager.getInstance().getWeatherInterference() * 100.0f), n == -1);
    }
    
    private String scrambleString(final String s, final int n, final boolean b) {
        return this.scrambleString(s, n, b, null);
    }
    
    public String scrambleString(final String s, final int n, final boolean b, final String s2) {
        this.hasAppliedInterference = false;
        final StringBuilder stringBuilder = this.stringBuilder;
        stringBuilder.setLength(0);
        if (n <= 0) {
            return s;
        }
        if (n >= 100) {
            return (s2 != null) ? s2 : this.getRandomBzztFzzt();
        }
        this.hasAppliedInterference = true;
        if (b) {
            final char[] charArray = s.toCharArray();
            int n2 = 0;
            int n3 = 0;
            String str = "";
            for (int i = 0; i < charArray.length; ++i) {
                final char ch = charArray[i];
                if (n3 != 0) {
                    str = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;C)Ljava/lang/String;, str, ch);
                    if (ch == ']') {
                        stringBuilder.append(str);
                        str = "";
                        n3 = 0;
                    }
                }
                else if (ch != '[' && (!Character.isWhitespace(ch) || i <= 0 || Character.isWhitespace(charArray[i - 1]))) {
                    str = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;C)Ljava/lang/String;, str, ch);
                }
                else {
                    if (Rand.Next(100) > n) {
                        stringBuilder.append(str).append(" ");
                        n2 = 0;
                    }
                    else if (n2 == 0) {
                        stringBuilder.append((s2 != null) ? s2 : this.getRandomBzztFzzt()).append(" ");
                        n2 = 1;
                    }
                    if (ch == '[') {
                        str = "[";
                        n3 = 1;
                    }
                    else {
                        str = "";
                    }
                }
            }
            if (str != null && str.length() > 0) {
                stringBuilder.append(str);
            }
        }
        else {
            int n4 = 0;
            final String[] split = s.split("\\s+");
            for (int j = 0; j < split.length; ++j) {
                final String str2 = split[j];
                if (Rand.Next(100) > n) {
                    stringBuilder.append(str2).append(" ");
                    n4 = 0;
                }
                else if (n4 == 0) {
                    stringBuilder.append((s2 != null) ? s2 : this.getRandomBzztFzzt()).append(" ");
                    n4 = 1;
                }
            }
        }
        return stringBuilder.toString();
    }
    
    public void ReceiveTransmission(final int n, final int n2, final int n3, final String s, final String s2, final float n4, final float n5, final float n6, final int n7, final boolean b) {
        if (this.getGameMode().equals(GameMode.Server)) {
            this.SendTransmission(n, n2, n3, s, s2, n4, n5, n6, n7, b);
        }
        else {
            this.DistributeTransmission(n, n2, n3, s, s2, n4, n5, n6, n7, b);
        }
    }
    
    public void SendTransmission(final int n, final int n2, final ChatMessage chatMessage, final int n3) {
        final Color textColor = chatMessage.getTextColor();
        this.SendTransmission(n, n2, chatMessage.getRadioChannel(), chatMessage.getText(), null, textColor.r, textColor.g, textColor.b, n3, false);
    }
    
    public void SendTransmission(final int n, final int n2, final int n3, String applyWeatherInterference, String s, float n4, float n5, float n6, final int n7, final boolean b) {
        final GameMode gameMode = this.getGameMode();
        if (!b && (gameMode == GameMode.Server || gameMode == GameMode.SinglePlayer)) {
            this.hasAppliedInterference = false;
            applyWeatherInterference = this.applyWeatherInterference(applyWeatherInterference, n7);
            if (this.hasAppliedInterference) {
                n4 = 0.5f;
                n5 = 0.5f;
                n6 = 0.5f;
                s = "";
            }
        }
        if (gameMode.equals(GameMode.SinglePlayer)) {
            this.ReceiveTransmission(n, n2, n3, applyWeatherInterference, s, n4, n5, n6, n7, b);
        }
        else if (gameMode.equals(GameMode.Server)) {
            GameServer.sendIsoWaveSignal(n, n2, n3, applyWeatherInterference, s, n4, n5, n6, n7, b);
        }
        else if (gameMode.equals(GameMode.Client)) {
            GameClient.sendIsoWaveSignal(n, n2, n3, applyWeatherInterference, s, n4, n5, n6, n7, b);
        }
    }
    
    public void PlayerListensChannel(final int n, final boolean b, final boolean b2) {
        final GameMode gameMode = this.getGameMode();
        if (gameMode.equals(GameMode.SinglePlayer) || gameMode.equals(GameMode.Server)) {
            if (this.scriptManager != null) {
                this.scriptManager.PlayerListensChannel(n, b, b2);
            }
        }
        else if (gameMode.equals(GameMode.Client)) {
            GameClient.sendPlayerListensChannel(n, b, b2);
        }
    }
    
    public void RegisterDevice(final WaveSignalDevice waveSignalDevice) {
        if (waveSignalDevice == null) {
            return;
        }
        if (!GameServer.bServer && !this.devices.contains(waveSignalDevice)) {
            this.devices.add(waveSignalDevice);
        }
        if (!GameServer.bServer && waveSignalDevice.getDeviceData().getIsTwoWay() && !this.broadcastDevices.contains(waveSignalDevice)) {
            this.broadcastDevices.add(waveSignalDevice);
        }
    }
    
    public void UnRegisterDevice(final WaveSignalDevice waveSignalDevice) {
        if (waveSignalDevice == null) {
            return;
        }
        if (!GameServer.bServer && this.devices.contains(waveSignalDevice)) {
            this.devices.remove(waveSignalDevice);
        }
        if (!GameServer.bServer && waveSignalDevice.getDeviceData().getIsTwoWay() && this.broadcastDevices.contains(waveSignalDevice)) {
            this.broadcastDevices.remove(waveSignalDevice);
        }
    }
    
    public Object clone() {
        return null;
    }
    
    public String computerize(final String s) {
        final StringBuilder stringBuilder = this.stringBuilder;
        stringBuilder.setLength(0);
        for (final char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                stringBuilder.append(Rand.NextBool(2) ? Character.toLowerCase(c) : Character.toUpperCase(c));
            }
            else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }
    
    public RecordedMedia getRecordedMedia() {
        return ZomboidRadio.recordedMedia;
    }
    
    public void setDisableBroadcasting(final boolean disable_BROADCASTING) {
        ZomboidRadio.DISABLE_BROADCASTING = disable_BROADCASTING;
    }
    
    public boolean getDisableBroadcasting() {
        return ZomboidRadio.DISABLE_BROADCASTING;
    }
    
    public void setDisableMediaLineLearning(final boolean disable_LINE_LEARNING) {
        RecordedMedia.DISABLE_LINE_LEARNING = disable_LINE_LEARNING;
    }
    
    public boolean getDisableMediaLineLearning() {
        return RecordedMedia.DISABLE_LINE_LEARNING;
    }
    
    private void LouisvilleObfuscationCheck() {
        if (GameClient.bClient || GameServer.bServer) {
            return;
        }
        final IsoPlayer instance = IsoPlayer.getInstance();
        if (instance != null && instance.getY() < 3550.0f) {
            ZomboidRadio.LOUISVILLE_OBFUSCATION = true;
        }
    }
    
    public static void ObfuscateChannelCheck(final RadioChannel radioChannel) {
        if (!radioChannel.isVanilla()) {
            return;
        }
        final int getFrequency = radioChannel.GetFrequency();
        for (int i = 0; i < ZomboidRadio.obfuscateChannels.length; ++i) {
            if (getFrequency == ZomboidRadio.obfuscateChannels[i]) {
                radioChannel.setLouisvilleObfuscate(true);
            }
        }
    }
    
    static {
        staticSounds = new String[] { "<bzzt>", "<fzzt>", "<wzzt>", "<szzt>" };
        ZomboidRadio.DEBUG_MODE = false;
        ZomboidRadio.DEBUG_XML = false;
        ZomboidRadio.DEBUG_SOUND = false;
        ZomboidRadio.POST_RADIO_SILENCE = false;
        ZomboidRadio.DISABLE_BROADCASTING = false;
        ZomboidRadio.LOUISVILLE_OBFUSCATION = false;
        ZomboidRadio.obfuscateChannels = new int[] { 200, 201, 204, 93200, 98000, 101200 };
    }
    
    private static final class FreqListEntry
    {
        public boolean isInvItem;
        public DeviceData deviceData;
        public int sourceX;
        public int sourceY;
        
        public FreqListEntry(final boolean isInvItem, final DeviceData deviceData, final int sourceX, final int sourceY) {
            this.isInvItem = false;
            this.sourceX = 0;
            this.sourceY = 0;
            this.isInvItem = isInvItem;
            this.deviceData = deviceData;
            this.sourceX = sourceX;
            this.sourceY = sourceY;
        }
    }
}
