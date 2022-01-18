// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.config.StringConfigOption;
import zombie.config.IntegerConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigOption;
import zombie.config.ConfigFile;
import java.io.FileWriter;
import zombie.debug.DebugLog;
import zombie.core.logger.LoggerManager;
import java.io.IOException;
import zombie.core.Core;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.core.Translator;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import zombie.core.Rand;
import java.util.HashMap;
import java.util.ArrayList;

public class ServerOptions
{
    public static final ServerOptions instance;
    private ArrayList<String> publicOptions;
    public static HashMap<String, String> clientOptionsList;
    public static final int MAX_PORT = 65535;
    private ArrayList<ServerOption> options;
    private HashMap<String, ServerOption> optionByName;
    public DoubleServerOption nightlengthmodifier;
    public BooleanServerOption PVP;
    public BooleanServerOption PauseEmpty;
    public BooleanServerOption GlobalChat;
    public StringServerOption ChatStreams;
    public BooleanServerOption Open;
    public TextServerOption ServerWelcomeMessage;
    public BooleanServerOption LogLocalChat;
    public BooleanServerOption AutoCreateUserInWhiteList;
    public BooleanServerOption DisplayUserName;
    public BooleanServerOption ShowFirstAndLastName;
    public StringServerOption SpawnPoint;
    public BooleanServerOption SafetySystem;
    public BooleanServerOption ShowSafety;
    public IntegerServerOption SafetyToggleTimer;
    public IntegerServerOption SafetyCooldownTimer;
    public StringServerOption SpawnItems;
    public IntegerServerOption DefaultPort;
    public IntegerServerOption ResetID;
    public StringServerOption Mods;
    public StringServerOption Map;
    public BooleanServerOption DoLuaChecksum;
    public BooleanServerOption DenyLoginOnOverloadedServer;
    public BooleanServerOption Public;
    public StringServerOption PublicName;
    public TextServerOption PublicDescription;
    public IntegerServerOption MaxPlayers;
    public IntegerServerOption PingFrequency;
    public IntegerServerOption PingLimit;
    public IntegerServerOption HoursForLootRespawn;
    public IntegerServerOption MaxItemsForLootRespawn;
    public BooleanServerOption ConstructionPreventsLootRespawn;
    public BooleanServerOption DropOffWhiteListAfterDeath;
    public BooleanServerOption NoFire;
    public BooleanServerOption AnnounceDeath;
    public DoubleServerOption MinutesPerPage;
    public IntegerServerOption SaveWorldEveryMinutes;
    public BooleanServerOption PlayerSafehouse;
    public BooleanServerOption AdminSafehouse;
    public BooleanServerOption SafehouseAllowTrepass;
    public BooleanServerOption SafehouseAllowFire;
    public BooleanServerOption SafehouseAllowLoot;
    public BooleanServerOption SafehouseAllowRespawn;
    public IntegerServerOption SafehouseDaySurvivedToClaim;
    public IntegerServerOption SafeHouseRemovalTime;
    public BooleanServerOption AllowDestructionBySledgehammer;
    public BooleanServerOption KickFastPlayers;
    public StringServerOption ServerPlayerID;
    public IntegerServerOption RCONPort;
    public StringServerOption RCONPassword;
    public BooleanServerOption DiscordEnable;
    public StringServerOption DiscordToken;
    public StringServerOption DiscordChannel;
    public StringServerOption DiscordChannelID;
    public StringServerOption Password;
    public IntegerServerOption MaxAccountsPerUser;
    public BooleanServerOption SleepAllowed;
    public BooleanServerOption SleepNeeded;
    public IntegerServerOption SteamPort1;
    public IntegerServerOption SteamPort2;
    public StringServerOption WorkshopItems;
    public StringServerOption SteamScoreboard;
    public BooleanServerOption SteamVAC;
    public BooleanServerOption UPnP;
    public IntegerServerOption UPnPLeaseTime;
    public BooleanServerOption UPnPZeroLeaseTimeFallback;
    public BooleanServerOption UPnPForce;
    public IntegerServerOption CoopServerLaunchTimeout;
    public IntegerServerOption CoopMasterPingTimeout;
    public BooleanServerOption VoiceEnable;
    public IntegerServerOption VoiceComplexity;
    public IntegerServerOption VoicePeriod;
    public IntegerServerOption VoiceSampleRate;
    public IntegerServerOption VoiceBuffering;
    public DoubleServerOption VoiceMinDistance;
    public DoubleServerOption VoiceMaxDistance;
    public BooleanServerOption Voice3D;
    public IntegerServerOption PhysicsDelay;
    public DoubleServerOption SpeedLimit;
    public StringServerOption server_browser_announced_ip;
    public BooleanServerOption UseTCPForMapDownloads;
    public BooleanServerOption PlayerRespawnWithSelf;
    public BooleanServerOption PlayerRespawnWithOther;
    public DoubleServerOption FastForwardMultiplier;
    public BooleanServerOption PlayerSaveOnDamage;
    public BooleanServerOption SaveTransactionID;
    public BooleanServerOption DisableSafehouseWhenPlayerConnected;
    public BooleanServerOption Faction;
    public IntegerServerOption FactionDaySurvivedToCreate;
    public IntegerServerOption FactionPlayersRequiredForTag;
    public BooleanServerOption AllowTradeUI;
    public BooleanServerOption DisableRadioStaff;
    public BooleanServerOption DisableRadioAdmin;
    public BooleanServerOption DisableRadioGM;
    public BooleanServerOption DisableRadioOverseer;
    public BooleanServerOption DisableRadioModerator;
    public BooleanServerOption DisableRadioInvisible;
    public StringServerOption ClientCommandFilter;
    public IntegerServerOption ItemNumbersLimitPerContainer;
    public IntegerServerOption BloodSplatLifespanDays;
    public BooleanServerOption AllowNonAsciiUsername;
    public BooleanServerOption BanKickGlobalSound;
    public BooleanServerOption RemovePlayerCorpsesOnCorpseRemoval;
    public IntegerServerOption ZombieUpdateMaxHighPriority;
    public DoubleServerOption ZombieUpdateDelta;
    public DoubleServerOption ZombieUpdateRadiusLowPriority;
    public DoubleServerOption ZombieUpdateRadiusHighPriority;
    public BooleanServerOption TrashDeleteAll;
    public BooleanServerOption PVPMeleeWhileHitReaction;
    public BooleanServerOption MouseOverToSeeDisplayName;
    public BooleanServerOption HidePlayersBehindYou;
    public DoubleServerOption PVPMeleeDamageModifier;
    public DoubleServerOption PVPFirearmDamageModifier;
    public DoubleServerOption CarEngineAttractionModifier;
    public BooleanServerOption PlayerBumpPlayer;
    public static ArrayList<String> cardList;
    
    public ServerOptions() {
        this.publicOptions = new ArrayList<String>();
        this.options = new ArrayList<ServerOption>();
        this.optionByName = new HashMap<String, ServerOption>();
        this.nightlengthmodifier = new DoubleServerOption(this, "nightlengthmodifier", 0.1, 1.0, 1.0);
        this.PVP = new BooleanServerOption(this, "PVP", true);
        this.PauseEmpty = new BooleanServerOption(this, "PauseEmpty", true);
        this.GlobalChat = new BooleanServerOption(this, "GlobalChat", true);
        this.ChatStreams = new StringServerOption(this, "ChatStreams", "s,r,a,w,y,sh,f,all");
        this.Open = new BooleanServerOption(this, "Open", true);
        this.ServerWelcomeMessage = new TextServerOption(this, "ServerWelcomeMessage", "Welcome to Project Zomboid Multiplayer! <LINE> <LINE> To interact with the Chat panel: press Tab, T, or Enter. <LINE> <LINE> The Tab key will change the target stream of the message. <LINE> <LINE> Global Streams: /all <LINE> Local Streams: /say, /yell <LINE> Special Steams: /whisper, /safehouse, /faction. <LINE> <LINE> Press the Up arrow to cycle through your message history. Click the Gear icon to customize chat. <LINE> <LINE> Happy surviving!");
        this.LogLocalChat = new BooleanServerOption(this, "LogLocalChat", false);
        this.AutoCreateUserInWhiteList = new BooleanServerOption(this, "AutoCreateUserInWhiteList", false);
        this.DisplayUserName = new BooleanServerOption(this, "DisplayUserName", true);
        this.ShowFirstAndLastName = new BooleanServerOption(this, "ShowFirstAndLastName", false);
        this.SpawnPoint = new StringServerOption(this, "SpawnPoint", "0,0,0");
        this.SafetySystem = new BooleanServerOption(this, "SafetySystem", true);
        this.ShowSafety = new BooleanServerOption(this, "ShowSafety", true);
        this.SafetyToggleTimer = new IntegerServerOption(this, "SafetyToggleTimer", 0, 1000, 2);
        this.SafetyCooldownTimer = new IntegerServerOption(this, "SafetyCooldownTimer", 0, 1000, 3);
        this.SpawnItems = new StringServerOption(this, "SpawnItems", "");
        this.DefaultPort = new IntegerServerOption(this, "DefaultPort", 0, 65535, 16261);
        this.ResetID = new IntegerServerOption(this, "ResetID", 0, Integer.MAX_VALUE, Rand.Next(1000000000));
        this.Mods = new StringServerOption(this, "Mods", "");
        this.Map = new StringServerOption(this, "Map", "Muldraugh, KY");
        this.DoLuaChecksum = new BooleanServerOption(this, "DoLuaChecksum", true);
        this.DenyLoginOnOverloadedServer = new BooleanServerOption(this, "DenyLoginOnOverloadedServer", true);
        this.Public = new BooleanServerOption(this, "Public", false);
        this.PublicName = new StringServerOption(this, "PublicName", "My PZ Server");
        this.PublicDescription = new TextServerOption(this, "PublicDescription", "");
        this.MaxPlayers = new IntegerServerOption(this, "MaxPlayers", 1, 32, 16);
        this.PingFrequency = new IntegerServerOption(this, "PingFrequency", 0, Integer.MAX_VALUE, 10);
        this.PingLimit = new IntegerServerOption(this, "PingLimit", 0, Integer.MAX_VALUE, 400);
        this.HoursForLootRespawn = new IntegerServerOption(this, "HoursForLootRespawn", 0, Integer.MAX_VALUE, 0);
        this.MaxItemsForLootRespawn = new IntegerServerOption(this, "MaxItemsForLootRespawn", 1, Integer.MAX_VALUE, 4);
        this.ConstructionPreventsLootRespawn = new BooleanServerOption(this, "ConstructionPreventsLootRespawn", true);
        this.DropOffWhiteListAfterDeath = new BooleanServerOption(this, "DropOffWhiteListAfterDeath", false);
        this.NoFire = new BooleanServerOption(this, "NoFire", false);
        this.AnnounceDeath = new BooleanServerOption(this, "AnnounceDeath", false);
        this.MinutesPerPage = new DoubleServerOption(this, "MinutesPerPage", 0.0, 60.0, 1.0);
        this.SaveWorldEveryMinutes = new IntegerServerOption(this, "SaveWorldEveryMinutes", 0, Integer.MAX_VALUE, 0);
        this.PlayerSafehouse = new BooleanServerOption(this, "PlayerSafehouse", false);
        this.AdminSafehouse = new BooleanServerOption(this, "AdminSafehouse", false);
        this.SafehouseAllowTrepass = new BooleanServerOption(this, "SafehouseAllowTrepass", true);
        this.SafehouseAllowFire = new BooleanServerOption(this, "SafehouseAllowFire", true);
        this.SafehouseAllowLoot = new BooleanServerOption(this, "SafehouseAllowLoot", true);
        this.SafehouseAllowRespawn = new BooleanServerOption(this, "SafehouseAllowRespawn", false);
        this.SafehouseDaySurvivedToClaim = new IntegerServerOption(this, "SafehouseDaySurvivedToClaim", 0, Integer.MAX_VALUE, 0);
        this.SafeHouseRemovalTime = new IntegerServerOption(this, "SafeHouseRemovalTime", 0, Integer.MAX_VALUE, 144);
        this.AllowDestructionBySledgehammer = new BooleanServerOption(this, "AllowDestructionBySledgehammer", true);
        this.KickFastPlayers = new BooleanServerOption(this, "KickFastPlayers", false);
        this.ServerPlayerID = new StringServerOption(this, "ServerPlayerID", Integer.toString(Rand.Next(Integer.MAX_VALUE)));
        this.RCONPort = new IntegerServerOption(this, "RCONPort", 0, 65535, 27015);
        this.RCONPassword = new StringServerOption(this, "RCONPassword", "");
        this.DiscordEnable = new BooleanServerOption(this, "DiscordEnable", false);
        this.DiscordToken = new StringServerOption(this, "DiscordToken", "");
        this.DiscordChannel = new StringServerOption(this, "DiscordChannel", "");
        this.DiscordChannelID = new StringServerOption(this, "DiscordChannelID", "");
        this.Password = new StringServerOption(this, "Password", "");
        this.MaxAccountsPerUser = new IntegerServerOption(this, "MaxAccountsPerUser", 0, Integer.MAX_VALUE, 0);
        this.SleepAllowed = new BooleanServerOption(this, "SleepAllowed", false);
        this.SleepNeeded = new BooleanServerOption(this, "SleepNeeded", false);
        this.SteamPort1 = new IntegerServerOption(this, "SteamPort1", 0, 65535, 8766);
        this.SteamPort2 = new IntegerServerOption(this, "SteamPort2", 0, 65535, 8767);
        this.WorkshopItems = new StringServerOption(this, "WorkshopItems", "");
        this.SteamScoreboard = new StringServerOption(this, "SteamScoreboard", "true");
        this.SteamVAC = new BooleanServerOption(this, "SteamVAC", true);
        this.UPnP = new BooleanServerOption(this, "UPnP", true);
        this.UPnPLeaseTime = new IntegerServerOption(this, "UPnPLeaseTime", 0, Integer.MAX_VALUE, 86400);
        this.UPnPZeroLeaseTimeFallback = new BooleanServerOption(this, "UPnPZeroLeaseTimeFallback", true);
        this.UPnPForce = new BooleanServerOption(this, "UPnPForce", true);
        this.CoopServerLaunchTimeout = new IntegerServerOption(this, "CoopServerLaunchTimeout", 5, 600, 20);
        this.CoopMasterPingTimeout = new IntegerServerOption(this, "CoopMasterPingTimeout", 5, 600, 60);
        this.VoiceEnable = new BooleanServerOption(this, "VoiceEnable", true);
        this.VoiceComplexity = new IntegerServerOption(this, "VoiceComplexity", 0, 10, 5);
        this.VoicePeriod = new IntegerServerOption(this, "VoicePeriod", 2, 60, 20);
        this.VoiceSampleRate = new IntegerServerOption(this, "VoiceSampleRate", 8000, 24000, 24000);
        this.VoiceBuffering = new IntegerServerOption(this, "VoiceBuffering", 800, 32000, 8000);
        this.VoiceMinDistance = new DoubleServerOption(this, "VoiceMinDistance", 0.0, 100000.0, 10.0);
        this.VoiceMaxDistance = new DoubleServerOption(this, "VoiceMaxDistance", 0.0, 100000.0, 300.0);
        this.Voice3D = new BooleanServerOption(this, "Voice3D", true);
        this.PhysicsDelay = new IntegerServerOption(this, "PhysicsDelay", 300, 1000, 500);
        this.SpeedLimit = new DoubleServerOption(this, "SpeedLimit", 10.0, 150.0, 70.0);
        this.server_browser_announced_ip = new StringServerOption(this, "server_browser_announced_ip", "");
        this.UseTCPForMapDownloads = new BooleanServerOption(this, "UseTCPForMapDownloads", false);
        this.PlayerRespawnWithSelf = new BooleanServerOption(this, "PlayerRespawnWithSelf", false);
        this.PlayerRespawnWithOther = new BooleanServerOption(this, "PlayerRespawnWithOther", false);
        this.FastForwardMultiplier = new DoubleServerOption(this, "FastForwardMultiplier", 1.0, 100.0, 40.0);
        this.PlayerSaveOnDamage = new BooleanServerOption(this, "PlayerSaveOnDamage", true);
        this.SaveTransactionID = new BooleanServerOption(this, "SaveTransactionID", false);
        this.DisableSafehouseWhenPlayerConnected = new BooleanServerOption(this, "DisableSafehouseWhenPlayerConnected", false);
        this.Faction = new BooleanServerOption(this, "Faction", true);
        this.FactionDaySurvivedToCreate = new IntegerServerOption(this, "FactionDaySurvivedToCreate", 0, Integer.MAX_VALUE, 0);
        this.FactionPlayersRequiredForTag = new IntegerServerOption(this, "FactionPlayersRequiredForTag", 1, Integer.MAX_VALUE, 1);
        this.AllowTradeUI = new BooleanServerOption(this, "AllowTradeUI", true);
        this.DisableRadioStaff = new BooleanServerOption(this, "DisableRadioStaff", false);
        this.DisableRadioAdmin = new BooleanServerOption(this, "DisableRadioAdmin", true);
        this.DisableRadioGM = new BooleanServerOption(this, "DisableRadioGM", true);
        this.DisableRadioOverseer = new BooleanServerOption(this, "DisableRadioOverseer", false);
        this.DisableRadioModerator = new BooleanServerOption(this, "DisableRadioModerator", false);
        this.DisableRadioInvisible = new BooleanServerOption(this, "DisableRadioInvisible", true);
        this.ClientCommandFilter = new StringServerOption(this, "ClientCommandFilter", "-vehicle.*;+vehicle.damageWindow;+vehicle.fixPart;+vehicle.installPart;+vehicle.uninstallPart");
        this.ItemNumbersLimitPerContainer = new IntegerServerOption(this, "ItemNumbersLimitPerContainer", 0, 9000, 0);
        this.BloodSplatLifespanDays = new IntegerServerOption(this, "BloodSplatLifespanDays", 0, 365, 0);
        this.AllowNonAsciiUsername = new BooleanServerOption(this, "AllowNonAsciiUsername", false);
        this.BanKickGlobalSound = new BooleanServerOption(this, "BanKickGlobalSound", true);
        this.RemovePlayerCorpsesOnCorpseRemoval = new BooleanServerOption(this, "RemovePlayerCorpsesOnCorpseRemoval", false);
        this.ZombieUpdateMaxHighPriority = new IntegerServerOption(this, "ZombieUpdateMaxHighPriority", 1, 500, 50);
        this.ZombieUpdateDelta = new DoubleServerOption(this, "ZombieUpdateDelta", 0.1, 5.0, 0.5);
        this.ZombieUpdateRadiusLowPriority = new DoubleServerOption(this, "ZombieUpdateRadiusLowPriority", 0.0, 500.0, 45.0);
        this.ZombieUpdateRadiusHighPriority = new DoubleServerOption(this, "ZombieUpdateRadiusHighPriority", 0.0, 500.0, 10.0);
        this.TrashDeleteAll = new BooleanServerOption(this, "TrashDeleteAll", false);
        this.PVPMeleeWhileHitReaction = new BooleanServerOption(this, "PVPMeleeWhileHitReaction", false);
        this.MouseOverToSeeDisplayName = new BooleanServerOption(this, "MouseOverToSeeDisplayName", true);
        this.HidePlayersBehindYou = new BooleanServerOption(this, "HidePlayersBehindYou", true);
        this.PVPMeleeDamageModifier = new DoubleServerOption(this, "PVPMeleeDamageModifier", 0.0, 500.0, 30.0);
        this.PVPFirearmDamageModifier = new DoubleServerOption(this, "PVPFirearmDamageModifier", 0.0, 500.0, 50.0);
        this.CarEngineAttractionModifier = new DoubleServerOption(this, "CarEngineAttractionModifier", 0.0, 10.0, 0.5);
        this.PlayerBumpPlayer = new BooleanServerOption(this, "PlayerBumpPlayer", false);
        this.publicOptions.clear();
        this.publicOptions.addAll(this.optionByName.keySet());
        this.publicOptions.remove("Password");
        this.publicOptions.remove("RCONPort");
        this.publicOptions.remove("RCONPassword");
        this.publicOptions.remove(this.DiscordToken.getName());
        this.publicOptions.remove(this.DiscordChannel.getName());
        this.publicOptions.remove(this.DiscordChannelID.getName());
        Collections.sort(this.publicOptions);
    }
    
    private void initOptions() {
        initClientCommandsHelp();
        final Iterator<ServerOption> iterator = this.options.iterator();
        while (iterator.hasNext()) {
            iterator.next().asConfigOption().resetToDefault();
        }
    }
    
    public ArrayList<String> getPublicOptions() {
        return this.publicOptions;
    }
    
    public ArrayList<ServerOption> getOptions() {
        return this.options;
    }
    
    public static void initClientCommandsHelp() {
        (ServerOptions.clientOptionsList = new HashMap<String, String>()).put("help", Translator.getText("UI_ServerOptionDesc_Help"));
        ServerOptions.clientOptionsList.put("changepwd", Translator.getText("UI_ServerOptionDesc_ChangePwd"));
        ServerOptions.clientOptionsList.put("roll", Translator.getText("UI_ServerOptionDesc_Roll"));
        ServerOptions.clientOptionsList.put("card", Translator.getText("UI_ServerOptionDesc_Card"));
        ServerOptions.clientOptionsList.put("safehouse", Translator.getText("UI_ServerOptionDesc_SafeHouse"));
    }
    
    public void init() {
        this.initOptions();
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator));
        if (!file.exists()) {
            file.mkdirs();
        }
        if (new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, File.separator, GameServer.ServerName)).exists()) {
            try {
                Core.getInstance().loadOptions();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            if (this.loadServerTextFile(GameServer.ServerName)) {
                this.saveServerTextFile(GameServer.ServerName);
            }
        }
        else {
            this.initSpawnRegionsFile(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, File.separator, GameServer.ServerName)));
            this.saveServerTextFile(GameServer.ServerName);
        }
        LoggerManager.init();
    }
    
    public void resetRegionFile() {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, File.separator, GameServer.ServerName));
        file.delete();
        this.initSpawnRegionsFile(file);
    }
    
    private void initSpawnRegionsFile(final File file) {
        if (file.exists()) {
            return;
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file.getPath()));
        try {
            file.createNewFile();
            final FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, GameServer.ServerName, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter.close();
            final FileWriter fileWriter2 = new FileWriter(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, file.getParent(), File.separator, GameServer.ServerName));
            fileWriter2.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter2.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter2.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter2.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter2.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter2.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter2.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
            fileWriter2.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public String getOption(final String s) {
        final ServerOption optionByName = this.getOptionByName(s);
        return (optionByName == null) ? null : optionByName.asConfigOption().getValueAsString();
    }
    
    public Boolean getBoolean(final String s) {
        final ServerOption optionByName = this.getOptionByName(s);
        if (optionByName instanceof BooleanServerOption) {
            return (Boolean)((BooleanServerOption)optionByName).getValueAsObject();
        }
        return null;
    }
    
    public Float getFloat(final String s) {
        final ServerOption optionByName = this.getOptionByName(s);
        if (optionByName instanceof DoubleServerOption) {
            return (float)((DoubleServerOption)optionByName).getValue();
        }
        return null;
    }
    
    public Double getDouble(final String s) {
        final ServerOption optionByName = this.getOptionByName(s);
        if (optionByName instanceof DoubleServerOption) {
            return ((DoubleServerOption)optionByName).getValue();
        }
        return null;
    }
    
    public Integer getInteger(final String s) {
        final ServerOption optionByName = this.getOptionByName(s);
        if (optionByName instanceof IntegerServerOption) {
            return ((IntegerServerOption)optionByName).getValue();
        }
        return null;
    }
    
    public void putOption(final String s, final String s2) {
        final ServerOption optionByName = this.getOptionByName(s);
        if (optionByName != null) {
            optionByName.asConfigOption().parse(s2);
        }
    }
    
    public void putSaveOption(final String s, final String s2) {
        this.putOption(s, s2);
        this.saveServerTextFile(GameServer.ServerName);
    }
    
    public String changeOption(final String s, final String s2) {
        final ServerOption optionByName = this.getOptionByName(s);
        if (optionByName == null) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s);
        }
        optionByName.asConfigOption().parse(s2);
        if (!this.saveServerTextFile(GameServer.ServerName)) {
            return "An error as occured.";
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, optionByName.asConfigOption().getValueAsString());
    }
    
    public static ServerOptions getInstance() {
        return ServerOptions.instance;
    }
    
    public static ArrayList<String> getClientCommandList(final boolean b) {
        String s = " <LINE> ";
        if (!b) {
            s = "\n";
        }
        if (ServerOptions.clientOptionsList == null) {
            initClientCommandsHelp();
        }
        final ArrayList<String> list = new ArrayList<String>();
        final Iterator<String> iterator = ServerOptions.clientOptionsList.keySet().iterator();
        list.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        while (iterator.hasNext()) {
            final String key = iterator.next();
            list.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, key, (String)ServerOptions.clientOptionsList.get(key), iterator.hasNext() ? s : ""));
        }
        return list;
    }
    
    public static String getRandomCard() {
        if (ServerOptions.cardList == null) {
            (ServerOptions.cardList = new ArrayList<String>()).add("the Ace of Clubs");
            ServerOptions.cardList.add("a Two of Clubs");
            ServerOptions.cardList.add("a Three of Clubs");
            ServerOptions.cardList.add("a Four of Clubs");
            ServerOptions.cardList.add("a Five of Clubs");
            ServerOptions.cardList.add("a Six of Clubs");
            ServerOptions.cardList.add("a Seven of Clubs");
            ServerOptions.cardList.add("a Height of Clubs");
            ServerOptions.cardList.add("a Nine of Clubs");
            ServerOptions.cardList.add("a Ten of Clubs");
            ServerOptions.cardList.add("the Jack of Clubs");
            ServerOptions.cardList.add("the Queen of Clubs");
            ServerOptions.cardList.add("the King of Clubs");
            ServerOptions.cardList.add("the Ace of Diamonds");
            ServerOptions.cardList.add("a Two of Diamonds");
            ServerOptions.cardList.add("a Three of Diamonds");
            ServerOptions.cardList.add("a Four of Diamonds");
            ServerOptions.cardList.add("a Five of Diamonds");
            ServerOptions.cardList.add("a Six of Diamonds");
            ServerOptions.cardList.add("a Seven of Diamonds");
            ServerOptions.cardList.add("a Height of Diamonds");
            ServerOptions.cardList.add("a Nine of Diamonds");
            ServerOptions.cardList.add("a Ten of Diamonds");
            ServerOptions.cardList.add("the Jack of Diamonds");
            ServerOptions.cardList.add("the Queen of Diamonds");
            ServerOptions.cardList.add("the King of Diamonds");
            ServerOptions.cardList.add("the Ace of Hearts");
            ServerOptions.cardList.add("a Two of Hearts");
            ServerOptions.cardList.add("a Three of Hearts");
            ServerOptions.cardList.add("a Four of Hearts");
            ServerOptions.cardList.add("a Five of Hearts");
            ServerOptions.cardList.add("a Six of Hearts");
            ServerOptions.cardList.add("a Seven of Hearts");
            ServerOptions.cardList.add("a Height of Hearts");
            ServerOptions.cardList.add("a Nine of Hearts");
            ServerOptions.cardList.add("a Ten of Hearts");
            ServerOptions.cardList.add("the Jack of Hearts");
            ServerOptions.cardList.add("the Queen of Hearts");
            ServerOptions.cardList.add("the King of Hearts");
            ServerOptions.cardList.add("the Ace of Spades");
            ServerOptions.cardList.add("a Two of Spades");
            ServerOptions.cardList.add("a Three of Spades");
            ServerOptions.cardList.add("a Four of Spades");
            ServerOptions.cardList.add("a Five of Spades");
            ServerOptions.cardList.add("a Six of Spades");
            ServerOptions.cardList.add("a Seven of Spades");
            ServerOptions.cardList.add("a Height of Spades");
            ServerOptions.cardList.add("a Nine of Spades");
            ServerOptions.cardList.add("a Ten of Spades");
            ServerOptions.cardList.add("the Jack of Spades");
            ServerOptions.cardList.add("the Queen of Spades");
            ServerOptions.cardList.add("the King of Spades");
        }
        return ServerOptions.cardList.get(Rand.Next(ServerOptions.cardList.size()));
    }
    
    public void addOption(final ServerOption serverOption) {
        if (this.optionByName.containsKey(serverOption.asConfigOption().getName())) {
            throw new IllegalArgumentException();
        }
        this.options.add(serverOption);
        this.optionByName.put(serverOption.asConfigOption().getName(), serverOption);
    }
    
    public int getNumOptions() {
        return this.options.size();
    }
    
    public ServerOption getOptionByIndex(final int index) {
        return this.options.get(index);
    }
    
    public ServerOption getOptionByName(final String key) {
        return this.optionByName.get(key);
    }
    
    public boolean loadServerTextFile(final String s) {
        final ConfigFile configFile = new ConfigFile();
        if (configFile.read(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, File.separator, s))) {
            for (final ConfigOption configOption : configFile.getOptions()) {
                final ServerOption serverOption = this.optionByName.get(configOption.getName());
                if (serverOption != null) {
                    serverOption.asConfigOption().parse(configOption.getValueAsString());
                }
            }
            return true;
        }
        return false;
    }
    
    public boolean saveServerTextFile(final String s) {
        final ConfigFile configFile = new ConfigFile();
        final String s2 = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, File.separator, s);
        final ArrayList<ConfigOption> list = new ArrayList<ConfigOption>();
        final Iterator<ServerOption> iterator = this.options.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().asConfigOption());
        }
        return configFile.write(s2, 0, list);
    }
    
    public int getMaxPlayers() {
        return Math.min(32, getInstance().MaxPlayers.getValue());
    }
    
    static {
        ServerOptions.clientOptionsList = null;
        instance = new ServerOptions();
        ServerOptions.cardList = null;
    }
    
    public static class BooleanServerOption extends BooleanConfigOption implements ServerOption
    {
        public BooleanServerOption(final ServerOptions serverOptions, final String s, final boolean b) {
            super(s, b);
            serverOptions.addOption(this);
        }
        
        @Override
        public ConfigOption asConfigOption() {
            return this;
        }
        
        @Override
        public String getTooltip() {
            return Translator.getTextOrNull(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
    }
    
    public static class DoubleServerOption extends DoubleConfigOption implements ServerOption
    {
        public DoubleServerOption(final ServerOptions serverOptions, final String s, final double n, final double n2, final double n3) {
            super(s, n, n2, n3);
            serverOptions.addOption(this);
        }
        
        @Override
        public ConfigOption asConfigOption() {
            return this;
        }
        
        @Override
        public String getTooltip() {
            return Translator.getTextOrNull(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
    }
    
    public static class IntegerServerOption extends IntegerConfigOption implements ServerOption
    {
        public IntegerServerOption(final ServerOptions serverOptions, final String s, final int n, final int n2, final int n3) {
            super(s, n, n2, n3);
            serverOptions.addOption(this);
        }
        
        @Override
        public ConfigOption asConfigOption() {
            return this;
        }
        
        @Override
        public String getTooltip() {
            return Translator.getTextOrNull(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
    }
    
    public static class StringServerOption extends StringConfigOption implements ServerOption
    {
        public StringServerOption(final ServerOptions serverOptions, final String s, final String s2) {
            super(s, s2);
            serverOptions.addOption(this);
        }
        
        @Override
        public ConfigOption asConfigOption() {
            return this;
        }
        
        @Override
        public String getTooltip() {
            return Translator.getTextOrNull(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
    }
    
    public static class TextServerOption extends StringConfigOption implements ServerOption
    {
        public TextServerOption(final ServerOptions serverOptions, final String s, final String s2) {
            super(s, s2);
            serverOptions.addOption(this);
        }
        
        @Override
        public String getType() {
            return "text";
        }
        
        @Override
        public ConfigOption asConfigOption() {
            return this;
        }
        
        @Override
        public String getTooltip() {
            return Translator.getTextOrNull(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name));
        }
    }
    
    public interface ServerOption
    {
        ConfigOption asConfigOption();
        
        String getTooltip();
    }
}
