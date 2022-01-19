// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.profiling.PerformanceProfileFrameProbe;
import zombie.world.moddata.GlobalModData;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import zombie.iso.objects.BSFurnace;
import zombie.iso.BuildingDef;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.inventory.types.Radio;
import zombie.iso.objects.IsoWaveSignal;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.WorldSoundManager;
import zombie.network.packets.ActionPacket;
import zombie.network.packets.EventPacket;
import zombie.popman.MPDebugInfo;
import zombie.network.packets.DeadPlayerPacket;
import zombie.network.packets.DeadZombiePacket;
import zombie.network.packets.hit.HitCharacterPacket;
import java.util.Map;
import zombie.vehicles.PolygonalMap2;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.inventory.types.AlarmClock;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.RainManager;
import java.util.Calendar;
import zombie.characters.SurvivorDesc;
import zombie.network.packets.SyncInjuriesPacket;
import zombie.core.Color;
import zombie.iso.IsoCell;
import zombie.characters.SurvivorFactory;
import zombie.iso.RoomDef;
import zombie.VirtualZombieManager;
import zombie.iso.IsoMetaCell;
import zombie.SharedDescriptors;
import zombie.PersistentOutfits;
import zombie.world.WorldDictionary;
import zombie.erosion.ErosionMain;
import zombie.gameStates.ChooseGameInfo;
import zombie.iso.objects.IsoMannequin;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.types.DrainableComboItem;
import zombie.vehicles.VehiclePart;
import zombie.inventory.types.InventoryContainer;
import zombie.core.network.ByteBufferReader;
import zombie.inventory.CompressIdenticalItems;
import zombie.vehicles.BaseVehicle;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.inventory.ItemContainer;
import zombie.globalObjects.SGlobalObjectNetwork;
import se.krka.kahlua.vm.KahluaTable;
import zombie.network.packets.SyncClothingPacket;
import zombie.core.logger.ExceptionLogger;
import zombie.network.packets.PlayWorldSoundPacket;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.GameSounds;
import zombie.network.packets.PlaySoundPacket;
import zombie.network.packets.StopSoundPacket;
import java.util.regex.Matcher;
import zombie.iso.areas.SafeHouse;
import java.util.regex.Pattern;
import zombie.characters.BodyDamage.BodyPart;
import zombie.inventory.types.Food;
import zombie.characters.Stats;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoThumpable;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoDoor;
import zombie.inventory.InventoryItemFactory;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoFire;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.IsoMetaGrid;
import zombie.iso.objects.IsoTrap;
import zombie.inventory.types.HandWeapon;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoCompost;
import zombie.iso.areas.NonPvpZone;
import zombie.core.textures.ColorInfo;
import zombie.characters.Faction;
import zombie.inventory.InventoryItem;
import zombie.core.stash.StashSystem;
import zombie.iso.IsoChunk;
import zombie.core.raknet.RakVoice;
import zombie.iso.IsoChunkMap;
import zombie.network.packets.PlayerPacket;
import zombie.characters.IsoZombie;
import java.util.Iterator;
import zombie.popman.NetworkZombiePacker;
import java.util.Objects;
import zombie.SystemDisabler;
import zombie.asset.AssetManagers;
import zombie.ZomboidGlobals;
import zombie.inventory.RecipeManager;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.scripting.ScriptManager;
import zombie.SandboxOptions;
import zombie.sandbox.CustomSandboxOptions;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureAssetManager;
import zombie.core.textures.TextureID;
import zombie.core.textures.TextureIDAssetManager;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelAssetManager;
import zombie.core.skinnedmodel.model.ModelMesh;
import zombie.core.skinnedmodel.model.MeshAssetManager;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.ClothingItemAssetManager;
import zombie.core.skinnedmodel.advancedanimation.AnimNodeAssetManager;
import zombie.core.skinnedmodel.model.AnimationAsset;
import zombie.core.skinnedmodel.model.AnimationAssetManager;
import zombie.core.skinnedmodel.model.AiSceneAsset;
import zombie.core.skinnedmodel.model.AiSceneAssetManager;
import zombie.characters.skills.CustomPerks;
import zombie.core.Translator;
import zombie.core.Languages;
import zombie.DebugFileWatcher;
import java.io.IOException;
import zombie.characters.skills.PerkFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import zombie.commands.CommandBase;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import zombie.core.ThreadGroups;
import zombie.iso.Vector3;
import zombie.vehicles.VehiclesDB2;
import zombie.savefile.ServerPlayerDB;
import zombie.GameWindow;
import zombie.iso.IsoMovingObject;
import zombie.MapCollisionData;
import zombie.popman.NetworkZombieManager;
import zombie.GameTime;
import zombie.iso.IsoCamera;
import zombie.core.math.PZMath;
import java.util.Collection;
import zombie.Lua.LuaManager;
import zombie.util.PublicServerUtil;
import zombie.gameStates.IngameState;
import zombie.core.PerformanceSettings;
import java.net.ConnectException;
import zombie.GameProfiler;
import zombie.debug.DebugOptions;
import zombie.vehicles.VehicleManager;
import zombie.AmbientStreamManager;
import zombie.AmbientSoundManager;
import zombie.SoundManager;
import zombie.globalObjects.SGlobalObjects;
import zombie.Lua.LuaEventManager;
import zombie.iso.SpawnPoints;
import zombie.iso.IsoWorld;
import zombie.iso.weather.ClimateManager;
import zombie.network.chat.ChatServer;
import zombie.iso.LosUtil;
import zombie.debug.LogSeverity;
import zombie.core.znet.PortMapper;
import java.sql.SQLException;
import zombie.core.znet.SteamWorkshop;
import zombie.core.znet.SteamGameServer;
import zombie.popman.ZombiePopulationManager;
import zombie.core.raknet.RakNetPeerInterface;
import zombie.debug.DebugType;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.physics.Bullet;
import zombie.vehicles.Clipper;
import zombie.util.PZSQLUtils;
import zombie.core.znet.SteamUtils;
import zombie.core.logger.LoggerManager;
import zombie.core.ProxyPrintStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;
import zombie.ZomboidFileSystem;
import java.io.FileNotFoundException;
import zombie.debug.DebugLog;
import zombie.core.network.ByteBufferWriter;
import zombie.core.utils.UpdateLimit;
import java.nio.ByteBuffer;
import zombie.iso.Vector2;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.iso.ObjectsSyncRequests;
import zombie.core.raknet.UdpConnection;
import java.util.HashSet;
import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.raknet.UdpEngine;
import java.util.HashMap;

public class GameServer
{
    public static final int MAX_PLAYERS = 512;
    public static final int TimeLimitForProcessPackets = 70;
    public static final int PacketsUpdateRate = 200;
    public static final int FPS = 10;
    private static final long[] packetCounts;
    private static final HashMap<String, CCFilter> ccFilters;
    public static int test;
    public static int DEFAULT_PORT;
    public static String IPCommandline;
    public static int PortCommandline;
    public static int SteamPortCommandline1;
    public static int SteamPortCommandline2;
    public static Boolean SteamVACCommandline;
    public static boolean GUICommandline;
    public static boolean bServer;
    public static boolean bCoop;
    public static boolean bDebug;
    public static UdpEngine udpEngine;
    public static final HashMap<Short, Long> IDToAddressMap;
    public static final HashMap<Short, IsoPlayer> IDToPlayerMap;
    public static final ArrayList<IsoPlayer> Players;
    public static float timeSinceKeepAlive;
    public static int MaxTicksSinceKeepAliveBeforeStall;
    public static final HashSet<UdpConnection> DebugPlayer;
    public static int ResetID;
    public static final ArrayList<String> ServerMods;
    public static final ArrayList<Long> WorkshopItems;
    public static String[] WorkshopInstallFolders;
    public static long[] WorkshopTimeStamps;
    public static String ServerName;
    public static final DiscordBot discordBot;
    public static String checksum;
    public static String GameMap;
    public static boolean bFastForward;
    public static boolean UseTCPForMapDownloads;
    public static final HashMap<String, Integer> transactionIDMap;
    public static final ObjectsSyncRequests worldObjectsServerSyncReq;
    public static String ip;
    static int count;
    private static final UdpConnection[] SlotToConnection;
    private static final HashMap<IsoPlayer, Long> PlayerToAddressMap;
    private static final ArrayList<Integer> alreadyRemoved;
    private static int SendZombies;
    private static boolean bDone;
    private static boolean launched;
    private static final ArrayList<String> consoleCommands;
    private static final HashMap<Integer, IZomboidPacket> MainLoopPlayerUpdate;
    private static final ConcurrentLinkedQueue<IZomboidPacket> MainLoopPlayerUpdateQ;
    private static final ConcurrentLinkedQueue<IZomboidPacket> MainLoopNetDataHighPriorityQ;
    private static final ConcurrentLinkedQueue<IZomboidPacket> MainLoopNetDataQ;
    private static final ArrayList<IZomboidPacket> MainLoopNetData2;
    private static final HashMap<Short, Vector2> playerToCoordsMap;
    private static final HashMap<Short, Integer> playerMovedToFastMap;
    private static final ByteBuffer large_file_bb;
    private static long previousSave;
    private String poisonousBerry;
    private String poisonousMushroom;
    private String difficulty;
    private static int droppedPackets;
    private static int countOfDroppedPackets;
    private static int countOfDroppedConnections;
    public static UdpConnection removeZombiesConnection;
    private static UpdateLimit calcCountPlayersInRelevantPositionLimiter;
    
    public GameServer() {
        this.poisonousBerry = null;
        this.poisonousMushroom = null;
        this.difficulty = "Hardcore";
    }
    
    public static void PauseAllClients() {
        final String s = "[SERVERMSG] Server saving...Please wait";
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.StartPause.doPacket(startPacket);
            startPacket.putUTF(s);
            PacketTypes.PacketType.StartPause.send(udpConnection);
        }
    }
    
    public static void UnPauseAllClients() {
        final String s = "[SERVERMSG] Server saved game...enjoy :)";
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.StopPause.doPacket(startPacket);
            startPacket.putUTF(s);
            PacketTypes.PacketType.StopPause.send(udpConnection);
        }
    }
    
    private static String parseIPFromCommandline(final String[] array, final int n, final String s) {
        if (n == array.length - 1) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            System.exit(0);
        }
        else if (array[n + 1].trim().isEmpty()) {
            DebugLog.log("empty argument given to \"\" + option + \"\"");
            System.exit(0);
        }
        else {
            final String[] split = array[n + 1].trim().split("\\.");
            if (split.length == 4) {
                for (int i = 0; i < 4; ++i) {
                    try {
                        final int int1 = Integer.parseInt(split[i]);
                        if (int1 < 0 || int1 > 255) {
                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, array[n + 1]));
                            System.exit(0);
                        }
                    }
                    catch (NumberFormatException ex) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, array[n + 1]));
                        System.exit(0);
                    }
                }
            }
            else {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, array[n + 1]));
                System.exit(0);
            }
        }
        return array[n + 1];
    }
    
    private static int parsePortFromCommandline(final String[] array, final int n, final String s) {
        if (n == array.length - 1) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            System.exit(0);
        }
        else if (array[n + 1].trim().isEmpty()) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            System.exit(0);
        }
        else {
            try {
                return Integer.parseInt(array[n + 1].trim());
            }
            catch (NumberFormatException ex) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
                System.exit(0);
            }
        }
        return -1;
    }
    
    private static boolean parseBooleanFromCommandline(final String[] array, final int n, final String s) {
        if (n == array.length - 1) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            System.exit(0);
        }
        else if (array[n + 1].trim().isEmpty()) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            System.exit(0);
        }
        else {
            final String trim = array[n + 1].trim();
            if ("true".equalsIgnoreCase(trim)) {
                return true;
            }
            if ("false".equalsIgnoreCase(trim)) {
                return false;
            }
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
            System.exit(0);
        }
        return false;
    }
    
    public static void setupCoop() throws FileNotFoundException {
        CoopSlave.init();
    }
    
    public static void main(final String[] array) {
        GameServer.bServer = true;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                if (array[i].startsWith("-cachedir=")) {
                    ZomboidFileSystem.instance.setCacheDir(array[i].replace("-cachedir=", "").trim());
                }
                else if (array[i].equals("-coop")) {
                    GameServer.bCoop = true;
                }
            }
        }
        if (GameServer.bCoop) {
            try {
                CoopSlave.initStreams();
            }
            catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        else {
            try {
                final PrintStream printStream = new PrintStream(new FileOutputStream(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator)), true);
                System.setOut(new ProxyPrintStream(System.out, printStream));
                System.setErr(new ProxyPrintStream(System.err, printStream));
            }
            catch (FileNotFoundException ex2) {
                ex2.printStackTrace();
            }
        }
        DebugLog.init();
        LoggerManager.init();
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir()));
        if (GameServer.bCoop) {
            try {
                setupCoop();
                CoopSlave.status("UI_ServerStatus_Initialising");
            }
            catch (FileNotFoundException ex3) {
                ex3.printStackTrace();
                SteamUtils.shutdown();
                System.exit(37);
                return;
            }
        }
        PZSQLUtils.init();
        Clipper.init();
        Bullet.init();
        Rand.init();
        if (System.getProperty("debug") != null) {
            GameServer.bDebug = true;
            Core.bDebug = true;
        }
        DebugLog.General.println("versionNumber=%s demo=%s", Core.getInstance().getVersionNumber(), false);
        DebugLog.General.println("svnRevision=%s date=%s time=%s", "", "", "");
        for (int j = 0; j < array.length; ++j) {
            if (array[j] != null) {
                if (array[j].startsWith("-disablelog=")) {
                    for (final String anObject : array[j].replace("-disablelog=", "").split(",")) {
                        if ("All".equals(anObject)) {
                            final DebugType[] values = DebugType.values();
                            for (int length2 = values.length, l = 0; l < length2; ++l) {
                                DebugLog.setLogEnabled(values[l], false);
                            }
                        }
                        else {
                            try {
                                DebugLog.setLogEnabled(DebugType.valueOf(anObject), false);
                            }
                            catch (IllegalArgumentException ex13) {}
                        }
                    }
                }
                else if (array[j].startsWith("-debuglog=")) {
                    for (final String s : array[j].replace("-debuglog=", "").split(",")) {
                        try {
                            DebugLog.setLogEnabled(DebugType.valueOf(s), true);
                        }
                        catch (IllegalArgumentException ex14) {}
                    }
                }
                else if (array[j].equals("-adminusername")) {
                    if (j == array.length - 1) {
                        DebugLog.log("expected argument after \"-adminusername\"");
                        System.exit(0);
                    }
                    else if (!ServerWorldDatabase.isValidUserName(array[j + 1].trim())) {
                        DebugLog.log("invalid username given to \"-adminusername\"");
                        System.exit(0);
                    }
                    else {
                        ServerWorldDatabase.instance.CommandLineAdminUsername = array[j + 1].trim();
                        ++j;
                    }
                }
                else if (array[j].equals("-adminpassword")) {
                    if (j == array.length - 1) {
                        DebugLog.log("expected argument after \"-adminpassword\"");
                        System.exit(0);
                    }
                    else if (array[j + 1].trim().isEmpty()) {
                        DebugLog.log("empty argument given to \"-adminpassword\"");
                        System.exit(0);
                    }
                    else {
                        ServerWorldDatabase.instance.CommandLineAdminPassword = array[j + 1].trim();
                        ++j;
                    }
                }
                else if (!array[j].startsWith("-cachedir=")) {
                    if (array[j].equals("-ip")) {
                        GameServer.IPCommandline = parseIPFromCommandline(array, j, "-ip");
                        ++j;
                    }
                    else if (array[j].equals("-gui")) {
                        GameServer.GUICommandline = true;
                    }
                    else if (array[j].equals("-nosteam")) {
                        System.setProperty("zomboid.steam", "0");
                    }
                    else if (array[j].equals("-statistic")) {
                        final int portFromCommandline = parsePortFromCommandline(array, j, "-statistic");
                        if (portFromCommandline >= 0) {
                            MPStatistic.getInstance().setPeriod(portFromCommandline);
                        }
                    }
                    else if (array[j].equals("-port")) {
                        GameServer.PortCommandline = parsePortFromCommandline(array, j, "-port");
                        ++j;
                    }
                    else if (array[j].equals("-steamport1")) {
                        GameServer.SteamPortCommandline1 = parsePortFromCommandline(array, j, "-steamport1");
                        ++j;
                    }
                    else if (array[j].equals("-steamport2")) {
                        GameServer.SteamPortCommandline2 = parsePortFromCommandline(array, j, "-steamport2");
                        ++j;
                    }
                    else if (array[j].equals("-steamvac")) {
                        GameServer.SteamVACCommandline = parseBooleanFromCommandline(array, j, "-steamvac");
                        ++j;
                    }
                    else if (array[j].equals("-servername")) {
                        if (j == array.length - 1) {
                            DebugLog.log("expected argument after \"-servername\"");
                            System.exit(0);
                        }
                        else if (array[j + 1].trim().isEmpty()) {
                            DebugLog.log("empty argument given to \"-servername\"");
                            System.exit(0);
                        }
                        else {
                            GameServer.ServerName = array[j + 1].trim();
                            ++j;
                        }
                    }
                    else if (array[j].equals("-coop")) {
                        ServerWorldDatabase.instance.doAdmin = false;
                    }
                    else {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, array[j]));
                    }
                }
            }
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, GameServer.ServerName));
        final String worldVersionUnsupported = isWorldVersionUnsupported();
        if (worldVersionUnsupported != null) {
            DebugLog.log(worldVersionUnsupported);
            CoopSlave.status(worldVersionUnsupported);
            return;
        }
        SteamUtils.init();
        RakNetPeerInterface.init();
        ZombiePopulationManager.init();
        ServerOptions.instance.init();
        initClientCommandFilter();
        if (GameServer.PortCommandline != -1) {
            ServerOptions.instance.DefaultPort.setValue(GameServer.PortCommandline);
        }
        if (GameServer.SteamPortCommandline1 != -1) {
            ServerOptions.instance.SteamPort1.setValue(GameServer.SteamPortCommandline1);
        }
        if (GameServer.SteamPortCommandline2 != -1) {
            ServerOptions.instance.SteamPort2.setValue(GameServer.SteamPortCommandline2);
        }
        if (GameServer.SteamVACCommandline != null) {
            ServerOptions.instance.SteamVAC.setValue(GameServer.SteamVACCommandline);
        }
        GameServer.DEFAULT_PORT = ServerOptions.instance.DefaultPort.getValue();
        GameServer.UseTCPForMapDownloads = ServerOptions.instance.UseTCPForMapDownloads.getValue();
        if (CoopSlave.instance != null) {
            ServerOptions.instance.ServerPlayerID.setValue("");
        }
        if (SteamUtils.isSteamModeEnabled()) {
            final String value = ServerOptions.instance.PublicName.getValue();
            if (value == null || value.isEmpty()) {
                ServerOptions.instance.PublicName.setValue("My PZ Server");
            }
        }
        String value2 = ServerOptions.instance.Map.getValue();
        if (value2 != null && !value2.trim().isEmpty()) {
            GameServer.GameMap = value2.trim();
            if (GameServer.GameMap.contains(";")) {
                value2 = GameServer.GameMap.split(";")[0];
            }
            Core.GameMap = value2.trim();
        }
        final String value3 = ServerOptions.instance.Mods.getValue();
        if (value3 != null) {
            for (final String s2 : value3.split(";")) {
                if (!s2.trim().isEmpty()) {
                    GameServer.ServerMods.add(s2.trim());
                }
            }
        }
        if (SteamUtils.isSteamModeEnabled()) {
            if (!SteamGameServer.Init(GameServer.IPCommandline, ServerOptions.instance.SteamPort1.getValue(), ServerOptions.instance.SteamPort2.getValue(), GameServer.DEFAULT_PORT, ServerOptions.instance.SteamVAC.getValue() ? 3 : 2, Core.getInstance().getSteamServerVersion())) {
                SteamUtils.shutdown();
                return;
            }
            SteamGameServer.SetProduct("zomboid");
            SteamGameServer.SetGameDescription("Project Zomboid");
            SteamGameServer.SetModDir("zomboid");
            SteamGameServer.SetDedicatedServer(true);
            SteamGameServer.SetMaxPlayerCount(ServerOptions.getInstance().getMaxPlayers());
            SteamGameServer.SetServerName(ServerOptions.instance.PublicName.getValue());
            SteamGameServer.SetMapName(ServerOptions.instance.Map.getValue());
            if (ServerOptions.instance.Public.getValue()) {
                SteamGameServer.SetGameTags((CoopSlave.instance != null) ? "hosted" : "");
            }
            else {
                SteamGameServer.SetGameTags(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (CoopSlave.instance != null) ? ";hosted" : ""));
            }
            SteamGameServer.SetKeyValue("description", ServerOptions.instance.PublicDescription.getValue());
            SteamGameServer.SetKeyValue("version", Core.getInstance().getVersionNumber());
            SteamGameServer.SetKeyValue("open", ServerOptions.instance.Open.getValue() ? "1" : "0");
            SteamGameServer.SetKeyValue("public", ServerOptions.instance.Public.getValue() ? "1" : "0");
            SteamGameServer.SetKeyValue("mods", ServerOptions.instance.Mods.getValue());
            if (GameServer.bDebug) {}
            final String value4 = ServerOptions.instance.WorkshopItems.getValue();
            if (value4 != null) {
                final String[] split4 = value4.split(";");
                for (int length5 = split4.length, n3 = 0; n3 < length5; ++n3) {
                    final String trim = split4[n3].trim();
                    if (!trim.isEmpty() && SteamUtils.isValidSteamID(trim)) {
                        GameServer.WorkshopItems.add(SteamUtils.convertStringToSteamID(trim));
                    }
                }
            }
            SteamWorkshop.init();
            SteamGameServer.LogOnAnonymous();
            SteamGameServer.EnableHeartBeats(true);
            DebugLog.log("Waiting for response from Steam servers");
            while (true) {
                SteamUtils.runLoop();
                final int getSteamServersConnectState = SteamGameServer.GetSteamServersConnectState();
                if (getSteamServersConnectState == SteamGameServer.STEAM_SERVERS_CONNECTED) {
                    if (!GameServerWorkshopItems.Install(GameServer.WorkshopItems)) {
                        return;
                    }
                    break;
                }
                else {
                    if (getSteamServersConnectState == SteamGameServer.STEAM_SERVERS_CONNECTFAILURE) {
                        DebugLog.log("Failed to connect to Steam servers");
                        SteamUtils.shutdown();
                        return;
                    }
                    try {
                        Thread.sleep(100L);
                    }
                    catch (InterruptedException ex15) {}
                }
            }
        }
        int n4 = 0;
        try {
            ServerWorldDatabase.instance.create();
        }
        catch (SQLException | ClassNotFoundException ex16) {
            final Throwable t;
            t.printStackTrace();
        }
        if (ServerOptions.instance.UPnP.getValue()) {
            DebugLog.log("Router detection/configuration starting.");
            DebugLog.log("If the server hangs here, set UPnP=false.");
            PortMapper.startup();
            if (PortMapper.discover()) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, PortMapper.getGatewayInfo()));
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, PortMapper.getExternalAddress()));
                DebugLog.log("trying to setup port forwarding rules...");
                final int value5 = ServerOptions.instance.UPnPLeaseTime.getValue();
                final boolean value6 = ServerOptions.instance.UPnPForce.getValue();
                if (PortMapper.addMapping(GameServer.DEFAULT_PORT, GameServer.DEFAULT_PORT, "PZ Server default port", "UDP", value5, value6)) {
                    DebugLog.log(DebugType.Network, "Default port has been mapped successfully");
                }
                else {
                    DebugLog.log(DebugType.Network, "Failed to map default port");
                }
                if (SteamUtils.isSteamModeEnabled()) {
                    final int value7 = ServerOptions.instance.SteamPort1.getValue();
                    if (PortMapper.addMapping(value7, value7, "PZ Server SteamPort1", "UDP", value5, value6)) {
                        DebugLog.log(DebugType.Network, "SteamPort1 has been mapped successfully");
                    }
                    else {
                        DebugLog.log(DebugType.Network, "Failed to map SteamPort1");
                    }
                    final int value8 = ServerOptions.instance.SteamPort2.getValue();
                    if (PortMapper.addMapping(value8, value8, "PZ Server SteamPort2", "UDP", value5, value6)) {
                        DebugLog.log(DebugType.Network, "SteamPort2 has been mapped successfully");
                    }
                    else {
                        DebugLog.log(DebugType.Network, "Failed to map SteamPort2");
                    }
                }
                if (GameServer.UseTCPForMapDownloads) {
                    for (int n5 = 1; n5 <= ServerOptions.getInstance().getMaxPlayers(); ++n5) {
                        final int n6 = GameServer.DEFAULT_PORT + n5;
                        if (PortMapper.addMapping(n6, n6, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n5), "TCP", value5, value6)) {
                            DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n6));
                        }
                        else {
                            DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n6));
                        }
                    }
                }
            }
            else {
                DebugLog.log(DebugType.Network, "No UPnP-enabled Internet gateway found, you must configure port forwarding on your gateway manually in order to make your server accessible from the Internet.");
            }
        }
        Core.GameMode = "Multiplayer";
        GameServer.bDone = false;
        DebugLog.log(DebugType.Network, "Initialising Server Systems...");
        CoopSlave.status("UI_ServerStatus_Initialising");
        try {
            doMinimumInit();
        }
        catch (Exception ex4) {
            DebugLog.General.printException(ex4, "Exception Thrown", LogSeverity.Error);
            DebugLog.General.println("Server Terminated.");
        }
        LosUtil.init(100, 100);
        ChatServer.getInstance().init();
        DebugLog.log(DebugType.Network, "Loading world...");
        CoopSlave.status("UI_ServerStatus_LoadingWorld");
        try {
            ClimateManager.setInstance(new ClimateManager());
            IsoWorld.instance.init();
        }
        catch (Exception ex5) {
            DebugLog.General.printException(ex5, "Exception Thrown", LogSeverity.Error);
            DebugLog.General.println("Server Terminated.");
            CoopSlave.status("UI_ServerStatus_Terminated");
            return;
        }
        if (!ZomboidFileSystem.instance.getFileInCurrentSave("z_outfits.bin").exists()) {
            ServerOptions.instance.changeOption("ResetID", new Integer(Rand.Next(100000000)).toString());
        }
        try {
            SpawnPoints.instance.initServer2();
        }
        catch (Exception ex6) {
            ex6.printStackTrace();
        }
        LuaEventManager.triggerEvent("OnGameTimeLoaded");
        SGlobalObjects.initSystems();
        SoundManager.instance = new SoundManager();
        (AmbientStreamManager.instance = new AmbientSoundManager()).init();
        ServerMap.instance.LastSaved = System.currentTimeMillis();
        VehicleManager.instance = new VehicleManager();
        ServerPlayersVehicles.instance.init();
        DebugOptions.instance.init();
        GameProfiler.init();
        try {
            startServer();
        }
        catch (ConnectException ex7) {
            ex7.printStackTrace();
            SteamUtils.shutdown();
            return;
        }
        if (SteamUtils.isSteamModeEnabled()) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, SteamGameServer.GetSteamID()));
        }
        final UpdateLimit updateLimit = new UpdateLimit(100L);
        PerformanceSettings.setLockFPS(10);
        final IngameState ingameState = new IngameState();
        final float[] array2 = new float[20];
        for (int n7 = 0; n7 < 20; ++n7) {
            array2[n7] = (float)PerformanceSettings.getLockFPS();
        }
        float n8 = (float)PerformanceSettings.getLockFPS();
        long n9 = System.currentTimeMillis();
        System.currentTimeMillis();
        if (!SteamUtils.isSteamModeEnabled()) {
            PublicServerUtil.init();
            PublicServerUtil.insertOrUpdate();
        }
        ServerLOS.init();
        NetworkAIParams.Init();
        final int value9 = ServerOptions.instance.RCONPort.getValue();
        final String value10 = ServerOptions.instance.RCONPassword.getValue();
        if (value9 != 0 && value10 != null && !value10.isEmpty()) {
            RCONServer.init(value9, value10);
        }
        LuaManager.GlobalObject.refreshAnimSets(true);
        while (!GameServer.bDone) {
            try {
                final long nanoTime = System.nanoTime();
                MPStatistics.countServerNetworkingFPS();
                GameServer.MainLoopNetData2.clear();
                for (IZomboidPacket e = GameServer.MainLoopNetDataHighPriorityQ.poll(); e != null; e = GameServer.MainLoopNetDataHighPriorityQ.poll()) {
                    GameServer.MainLoopNetData2.add(e);
                }
                MPStatistic.getInstance().setPacketsLength(GameServer.MainLoopNetData2.size());
                for (int index = 0; index < GameServer.MainLoopNetData2.size(); ++index) {
                    final IZomboidPacket zomboidPacket = GameServer.MainLoopNetData2.get(index);
                    if (zomboidPacket.isConnect()) {
                        final UdpConnection connection = ((DelayedConnection)zomboidPacket).connection;
                        LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, connection.index, ((DelayedConnection)zomboidPacket).hostString));
                        GameServer.udpEngine.connections.add(connection);
                    }
                    else if (zomboidPacket.isDisconnect()) {
                        final UdpConnection connection2 = ((DelayedConnection)zomboidPacket).connection;
                        LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, connection2.idStr, connection2.username, connection2.index));
                        GameServer.udpEngine.connections.remove(connection2);
                        disconnect(connection2);
                    }
                    else {
                        final short type = ((ZomboidNetData)zomboidPacket).type;
                        mainLoopDealWithNetData((ZomboidNetData)zomboidPacket);
                    }
                }
                GameServer.MainLoopPlayerUpdate.clear();
                for (IZomboidPacket zomboidPacket2 = GameServer.MainLoopPlayerUpdateQ.poll(); zomboidPacket2 != null; zomboidPacket2 = GameServer.MainLoopPlayerUpdateQ.poll()) {
                    final ZomboidNetData value11 = (ZomboidNetData)zomboidPacket2;
                    final ZomboidNetData zomboidNetData = GameServer.MainLoopPlayerUpdate.put((int)value11.buffer.getShort(0), value11);
                    if (zomboidNetData != null) {
                        ZomboidNetDataPool.instance.discard(zomboidNetData);
                    }
                }
                GameServer.MainLoopNetData2.clear();
                GameServer.MainLoopNetData2.addAll(GameServer.MainLoopPlayerUpdate.values());
                GameServer.MainLoopPlayerUpdate.clear();
                MPStatistic.getInstance().setPacketsLength(GameServer.MainLoopNetData2.size());
                for (int index2 = 0; index2 < GameServer.MainLoopNetData2.size(); ++index2) {
                    s_performance.mainLoopDealWithNetData.invokeAndMeasure((ZomboidNetData)GameServer.MainLoopNetData2.get(index2), GameServer::mainLoopDealWithNetData);
                }
                GameServer.MainLoopNetData2.clear();
                for (IZomboidPacket e2 = GameServer.MainLoopNetDataQ.poll(); e2 != null; e2 = GameServer.MainLoopNetDataQ.poll()) {
                    GameServer.MainLoopNetData2.add(e2);
                }
                for (int index3 = 0; index3 < GameServer.MainLoopNetData2.size(); ++index3) {
                    if (index3 % 10 == 0 && (System.nanoTime() - nanoTime) / 1000000L > 70L) {
                        if (GameServer.droppedPackets == 0) {
                            DebugLog.log("Server is too busy. Server will drop updates of vehicle's physics. Server is closed for new connections.");
                        }
                        GameServer.droppedPackets += 2;
                        GameServer.countOfDroppedPackets += GameServer.MainLoopNetData2.size() - index3;
                        break;
                    }
                    s_performance.mainLoopDealWithNetData.invokeAndMeasure((ZomboidNetData)GameServer.MainLoopNetData2.get(index3), GameServer::mainLoopDealWithNetData);
                }
                GameServer.MainLoopNetData2.clear();
                if (GameServer.droppedPackets == 1) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, GameServer.countOfDroppedPackets, GameServer.countOfDroppedConnections));
                    GameServer.countOfDroppedPackets = 0;
                    GameServer.countOfDroppedConnections = 0;
                }
                GameServer.droppedPackets = Math.max(0, Math.min(1000, GameServer.droppedPackets - 1));
                if (!updateLimit.Check()) {
                    final long clamp = PZMath.clamp((5000000L - System.nanoTime() + nanoTime) / 1000000L, 0L, 100L);
                    if (clamp <= 0L) {
                        continue;
                    }
                    try {
                        MPStatistic.getInstance().Main.StartSleep();
                        Thread.sleep(clamp);
                        MPStatistic.getInstance().Main.EndSleep();
                    }
                    catch (InterruptedException ex8) {
                        ex8.printStackTrace();
                    }
                }
                else {
                    MPStatistic.getInstance().Main.Start();
                    final IsoCamera.FrameState frameState = IsoCamera.frameState;
                    ++frameState.frameCount;
                    s_performance.frameStep.start();
                    GameServer.timeSinceKeepAlive += GameTime.getInstance().getMultiplier();
                    final double value12 = ServerOptions.instance.ZombieUpdateDelta.getValue();
                    ++GameServer.SendZombies;
                    if (GameServer.SendZombies / n8 > value12) {
                        GameServer.SendZombies = 0;
                    }
                    MPStatistic.getInstance().ServerMapPreupdate.Start();
                    ServerMap.instance.preupdate();
                    MPStatistic.getInstance().ServerMapPreupdate.End();
                    synchronized (GameServer.consoleCommands) {
                        for (int index4 = 0; index4 < GameServer.consoleCommands.size(); ++index4) {
                            final String s3 = GameServer.consoleCommands.get(index4);
                            try {
                                if (CoopSlave.instance == null || !CoopSlave.instance.handleCommand(s3)) {
                                    System.out.println(handleServerCommand(s3, null));
                                }
                            }
                            catch (Exception ex9) {
                                ex9.printStackTrace();
                            }
                        }
                        GameServer.consoleCommands.clear();
                    }
                    if (GameServer.removeZombiesConnection != null) {
                        NetworkZombieManager.removeZombies(GameServer.removeZombiesConnection);
                        GameServer.removeZombiesConnection = null;
                    }
                    s_performance.RCONServerUpdate.invokeAndMeasure(RCONServer::update);
                    try {
                        MapCollisionData.instance.updateGameState();
                        MPStatistic.getInstance().IngameStateUpdate.Start();
                        ingameState.update();
                        MPStatistic.getInstance().IngameStateUpdate.End();
                        VehicleManager.instance.serverUpdate();
                    }
                    catch (Exception ex10) {
                        ex10.printStackTrace();
                    }
                    int n10 = 0;
                    int n11 = 0;
                    for (int index5 = 0; index5 < GameServer.Players.size(); ++index5) {
                        final IsoPlayer isoPlayer = GameServer.Players.get(index5);
                        if (isoPlayer.isAlive()) {
                            if (!IsoWorld.instance.CurrentCell.getObjectList().contains(isoPlayer)) {
                                IsoWorld.instance.CurrentCell.getObjectList().add(isoPlayer);
                            }
                            ++n11;
                            if (isoPlayer.isAsleep()) {
                                ++n10;
                            }
                        }
                        ServerMap.instance.characterIn(isoPlayer);
                    }
                    setFastForward(ServerOptions.instance.SleepAllowed.getValue() && n11 > 0 && n10 == n11);
                    final boolean check = GameServer.calcCountPlayersInRelevantPositionLimiter.Check();
                    for (int n12 = 0; n12 < GameServer.udpEngine.connections.size(); ++n12) {
                        final UdpConnection udpConnection = GameServer.udpEngine.connections.get(n12);
                        if (check) {
                            udpConnection.calcCountPlayersInRelevantPosition();
                        }
                        for (int n13 = 0; n13 < 4; ++n13) {
                            final Vector3 vector3 = udpConnection.connectArea[n13];
                            if (vector3 != null) {
                                ServerMap.instance.characterIn((int)vector3.x, (int)vector3.y, (int)vector3.z);
                            }
                            ClientServerMap.characterIn(udpConnection, n13);
                        }
                        if (udpConnection.playerDownloadServer != null) {
                            udpConnection.playerDownloadServer.update();
                        }
                    }
                    for (int index6 = 0; index6 < IsoWorld.instance.CurrentCell.getObjectList().size(); ++index6) {
                        final IsoMovingObject o = IsoWorld.instance.CurrentCell.getObjectList().get(index6);
                        if (o instanceof IsoPlayer && !GameServer.Players.contains(o)) {
                            DebugLog.log("Disconnected player in CurrentCell.getObjectList() removed");
                            IsoWorld.instance.CurrentCell.getObjectList().remove(index6--);
                        }
                    }
                    if (++n4 > 150) {
                        for (int n14 = 0; n14 < GameServer.udpEngine.connections.size(); ++n14) {
                            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(n14);
                            try {
                                if (udpConnection2.username == null && !udpConnection2.awaitingCoopApprove) {
                                    disconnect(udpConnection2);
                                    GameServer.udpEngine.forceDisconnect(udpConnection2.getConnectedGUID());
                                }
                            }
                            catch (Exception ex11) {
                                ex11.printStackTrace();
                            }
                        }
                        n4 = 0;
                    }
                    GameServer.worldObjectsServerSyncReq.serverSendRequests(GameServer.udpEngine);
                    MPStatistic.getInstance().ServerMapPostupdate.Start();
                    ServerMap.instance.postupdate();
                    MPStatistic.getInstance().ServerMapPostupdate.End();
                    try {
                        ServerGUI.update();
                    }
                    catch (Exception ex12) {
                        ex12.printStackTrace();
                    }
                    final long n15 = n9;
                    n9 = System.currentTimeMillis();
                    final long n16 = n9 - n15;
                    final float v = 1000.0f / n16;
                    if (!Float.isNaN(v)) {
                        n8 += (float)Math.min((v - n8) * 0.05, 1.0);
                    }
                    GameTime.instance.FPSMultiplier = 60.0f / n8;
                    launchCommandHandler();
                    MPStatistic.getInstance().process(n16);
                    if (!SteamUtils.isSteamModeEnabled()) {
                        PublicServerUtil.update();
                        PublicServerUtil.updatePlayerCountIfChanged();
                    }
                    for (int n17 = 0; n17 < GameServer.udpEngine.connections.size(); ++n17) {
                        final UdpConnection udpConnection3 = GameServer.udpEngine.connections.get(n17);
                        if (udpConnection3.accessLevel.equals("admin") && udpConnection3.sendPulse && udpConnection3.isFullyConnected()) {
                            final ByteBufferWriter startPacket = udpConnection3.startPacket();
                            PacketTypes.PacketType.ServerPulse.doPacket(startPacket);
                            startPacket.putLong(System.currentTimeMillis());
                            PacketTypes.PacketType.ServerPulse.send(udpConnection3);
                        }
                        if (udpConnection3.checksumState == UdpConnection.ChecksumState.Different && udpConnection3.checksumTime + 8000L < System.currentTimeMillis()) {
                            DebugLog.log("timed out connection because checksum was different");
                            udpConnection3.checksumState = UdpConnection.ChecksumState.Init;
                            udpConnection3.forceDisconnect();
                        }
                        else if (!udpConnection3.chunkObjectState.isEmpty()) {
                            for (int n18 = 0; n18 < udpConnection3.chunkObjectState.size(); n18 += 2) {
                                if (!udpConnection3.RelevantTo((float)(udpConnection3.chunkObjectState.get(n18) * 10 + 5), (float)(udpConnection3.chunkObjectState.get(n18 + 1) * 10 + 5), (float)(udpConnection3.ChunkGridWidth * 4 * 10))) {
                                    udpConnection3.chunkObjectState.remove(n18, 2);
                                    n18 -= 2;
                                }
                            }
                        }
                    }
                    if (CoopSlave.instance != null) {
                        CoopSlave.instance.update();
                        if (CoopSlave.instance.masterLost()) {
                            DebugLog.log("Coop master is not responding, terminating");
                            ServerMap.instance.QueueQuit();
                        }
                    }
                    SteamUtils.runLoop();
                    GameWindow.fileSystem.updateAsyncTransactions();
                }
            }
            finally {
                s_performance.frameStep.end();
            }
        }
        CoopSlave.status("UI_ServerStatus_Terminated");
        DebugLog.log(DebugType.Network, "Server exited");
        ServerGUI.shutdown();
        ServerPlayerDB.getInstance().close();
        VehiclesDB2.instance.Reset();
        SteamUtils.shutdown();
        System.exit(0);
    }
    
    private static void launchCommandHandler() {
        if (GameServer.launched) {
            return;
        }
        GameServer.launched = true;
        final BufferedReader bufferedReader2;
        final String e;
        new Thread(ThreadGroups.Workers, () -> {
            try {
                new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    bufferedReader2.readLine();
                    if (e == null) {
                        break;
                    }
                    else if (!e.isEmpty()) {
                        synchronized (GameServer.consoleCommands) {
                            GameServer.consoleCommands.add(e);
                        }
                    }
                    else {
                        continue;
                    }
                }
                GameServer.consoleCommands.add("process-status@eof");
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }, "command handler").start();
    }
    
    public static String rcon(final String s) {
        try {
            return handleServerCommand(s, null);
        }
        catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
    
    private static String handleServerCommand(final String x, final UdpConnection udpConnection) {
        if (x == null) {
            return null;
        }
        System.out.println(x);
        String username = "admin";
        String accessLevel = "admin";
        if (udpConnection != null) {
            username = udpConnection.username;
            accessLevel = udpConnection.accessLevel;
        }
        if (udpConnection != null && udpConnection.isCoopHost) {
            accessLevel = "admin";
        }
        final Class commandCls = CommandBase.findCommandCls(x);
        if (commandCls != null) {
            final Constructor constructor = commandCls.getConstructors()[0];
            try {
                return constructor.newInstance(username, accessLevel, x, udpConnection).Execute();
            }
            catch (InvocationTargetException ex) {
                ex.printStackTrace();
                return "A InvocationTargetException error occured";
            }
            catch (IllegalAccessException ex2) {
                ex2.printStackTrace();
                return "A IllegalAccessException error occured";
            }
            catch (InstantiationException ex3) {
                ex3.printStackTrace();
                return "A InstantiationException error occured";
            }
            catch (SQLException ex4) {
                ex4.printStackTrace();
                return "A SQL error occured";
            }
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, x);
    }
    
    static void receiveTeleport(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final String readString = GameWindow.ReadString(byteBuffer);
        final float float1 = byteBuffer.getFloat();
        final float float2 = byteBuffer.getFloat();
        final float float3 = byteBuffer.getFloat();
        final IsoPlayer playerByRealUserName = getPlayerByRealUserName(readString);
        if (playerByRealUserName == null) {
            return;
        }
        final UdpConnection connectionFromPlayer = getConnectionFromPlayer(playerByRealUserName);
        if (connectionFromPlayer == null) {
            return;
        }
        final ByteBufferWriter startPacket = connectionFromPlayer.startPacket();
        PacketTypes.PacketType.Teleport.doPacket(startPacket);
        startPacket.putByte((byte)playerByRealUserName.PlayerIndex);
        startPacket.putFloat(float1);
        startPacket.putFloat(float2);
        startPacket.putFloat(float3);
        PacketTypes.PacketType.Teleport.send(connectionFromPlayer);
    }
    
    public static void sendPlayerExtraInfo(final IsoPlayer isoPlayer, final UdpConnection udpConnection) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            final ByteBufferWriter startPacket = udpConnection2.startPacket();
            PacketTypes.PacketType.ExtraInfo.doPacket(startPacket);
            startPacket.putShort(isoPlayer.OnlineID);
            startPacket.putUTF(isoPlayer.accessLevel);
            startPacket.putByte((byte)(isoPlayer.isGodMod() ? 1 : 0));
            startPacket.putByte((byte)(isoPlayer.isGhostMode() ? 1 : 0));
            startPacket.putByte((byte)(isoPlayer.isInvisible() ? 1 : 0));
            startPacket.putByte((byte)(isoPlayer.isNoClip() ? 1 : 0));
            startPacket.putByte((byte)(isoPlayer.isShowAdminTag() ? 1 : 0));
            PacketTypes.PacketType.ExtraInfo.send(udpConnection2);
        }
    }
    
    static void receiveExtraInfo(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final String readString = GameWindow.ReadString(byteBuffer);
        final boolean godMod = byteBuffer.get() == 1;
        final boolean ghostMode = byteBuffer.get() == 1;
        final boolean invisible = byteBuffer.get() == 1;
        final boolean noClip = byteBuffer.get() == 1;
        final boolean showAdminTag = byteBuffer.get() == 1;
        final boolean canHearAll = byteBuffer.get() == 1;
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short1);
        if (isoPlayer != null) {
            udpConnection.accessLevel = readString;
            isoPlayer.setGodMod(godMod);
            isoPlayer.setGhostMode(ghostMode);
            isoPlayer.setInvisible(invisible);
            isoPlayer.setNoClip(noClip);
            isoPlayer.setShowAdminTag(showAdminTag);
            isoPlayer.setCanHearAll(canHearAll);
            sendPlayerExtraInfo(isoPlayer, udpConnection);
        }
    }
    
    static void receiveAddXpFromPlayerStatsUI(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        if (!canModifyPlayerStats(udpConnection)) {
            return;
        }
        final IsoPlayer key = GameServer.IDToPlayerMap.get(byteBuffer.getShort());
        final int int1 = byteBuffer.getInt();
        int int2 = 0;
        int int3 = 0;
        int n2 = 0;
        if (key != null && !key.isDead() && int1 == 0) {
            int3 = byteBuffer.getInt();
            int2 = byteBuffer.getInt();
            n2 = ((byteBuffer.get() == 1) ? 1 : 0);
            key.getXp().AddXP(PerkFactory.Perks.fromIndex(int3), (float)int2, false, (boolean)(n2 != 0), false, true);
        }
        if (key != null) {
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.getConnectedGUID() == GameServer.PlayerToAddressMap.get(key)) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.AddXpFromPlayerStatsUI.doPacket(startPacket);
                    startPacket.putShort(key.getOnlineID());
                    if (int1 == 0) {
                        startPacket.putInt(0);
                        startPacket.putInt(int3);
                        startPacket.putInt(int2);
                        startPacket.putByte((byte)n2);
                    }
                    PacketTypes.PacketType.AddXpFromPlayerStatsUI.send(udpConnection2);
                }
            }
        }
    }
    
    private static boolean canSeePlayerStats(final UdpConnection udpConnection) {
        return !udpConnection.accessLevel.equals("");
    }
    
    private static boolean canModifyPlayerStats(final UdpConnection udpConnection) {
        return udpConnection.accessLevel.equals("admin") || udpConnection.accessLevel.equals("moderator") || udpConnection.accessLevel.equals("overseer");
    }
    
    static void receiveSyncXP(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        if (!canModifyPlayerStats(udpConnection)) {
            return;
        }
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(byteBuffer.getShort());
        if (isoPlayer != null && !isoPlayer.isDead()) {
            try {
                isoPlayer.getXp().load(byteBuffer, 186);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.SyncXP.doPacket(startPacket);
                    startPacket.putShort(isoPlayer.getOnlineID());
                    try {
                        isoPlayer.getXp().save(startPacket.bb);
                    }
                    catch (IOException ex2) {
                        ex2.printStackTrace();
                    }
                    PacketTypes.PacketType.SyncXP.send(udpConnection2);
                }
            }
        }
    }
    
    static void receiveChangePlayerStats(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final IsoPlayer key = GameServer.IDToPlayerMap.get(byteBuffer.getShort());
        if (key == null) {
            return;
        }
        final String readString = GameWindow.ReadString(byteBuffer);
        key.setPlayerStats(byteBuffer, readString);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                if (udpConnection2.getConnectedGUID() == GameServer.PlayerToAddressMap.get(key)) {
                    udpConnection2.allChatMuted = key.isAllChatMuted();
                    udpConnection2.accessLevel = key.accessLevel;
                }
                key.createPlayerStats(udpConnection2.startPacket(), readString);
                PacketTypes.PacketType.ChangePlayerStats.send(udpConnection2);
            }
        }
    }
    
    public static void doMinimumInit() throws IOException {
        Rand.init();
        ZomboidFileSystem.instance.init();
        DebugFileWatcher.instance.init();
        ZomboidFileSystem.instance.loadMods(new ArrayList<String>(GameServer.ServerMods));
        LuaManager.init();
        Languages.instance.init();
        Translator.loadFiles();
        PerkFactory.init();
        CustomPerks.instance.init();
        CustomPerks.instance.initLua();
        final AssetManagers assetManagers = GameWindow.assetManagers;
        AiSceneAssetManager.instance.create(AiSceneAsset.ASSET_TYPE, assetManagers);
        AnimationAssetManager.instance.create(AnimationAsset.ASSET_TYPE, assetManagers);
        AnimNodeAssetManager.instance.create(AnimationAsset.ASSET_TYPE, assetManagers);
        ClothingItemAssetManager.instance.create(ClothingItem.ASSET_TYPE, assetManagers);
        MeshAssetManager.instance.create(ModelMesh.ASSET_TYPE, assetManagers);
        ModelAssetManager.instance.create(Model.ASSET_TYPE, assetManagers);
        TextureIDAssetManager.instance.create(TextureID.ASSET_TYPE, assetManagers);
        TextureAssetManager.instance.create(Texture.ASSET_TYPE, assetManagers);
        if (GameServer.GUICommandline && System.getProperty("softreset") == null) {
            ServerGUI.init();
        }
        CustomSandboxOptions.instance.init();
        CustomSandboxOptions.instance.initInstance(SandboxOptions.instance);
        ScriptManager.instance.Load();
        ClothingDecals.init();
        BeardStyles.init();
        HairStyles.init();
        OutfitManager.init();
        JAssImpImporter.Init();
        ModelManager.NoOpenGL = !ServerGUI.isCreated();
        ModelManager.instance.create();
        System.out.println("LOADING ASSETS: START");
        while (GameWindow.fileSystem.hasWork()) {
            GameWindow.fileSystem.updateAsyncTransactions();
        }
        System.out.println("LOADING ASSETS: FINISH");
        try {
            LuaManager.initChecksum();
            LuaManager.LoadDirBase("shared");
            LuaManager.LoadDirBase("client", true);
            LuaManager.LoadDirBase("server");
            LuaManager.finishChecksum();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        RecipeManager.LoadedAfterLua();
        if (new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getCacheDir(), File.separator, File.separator, GameServer.ServerName)).exists()) {
            SandboxOptions.instance.loadServerLuaFile(GameServer.ServerName);
            SandboxOptions.instance.handleOldServerZombiesFile();
            SandboxOptions.instance.toLua();
        }
        else {
            SandboxOptions.instance.handleOldServerZombiesFile();
            SandboxOptions.instance.saveServerLuaFile(GameServer.ServerName);
            SandboxOptions.instance.toLua();
        }
        LuaEventManager.triggerEvent("OnGameBoot");
        ZomboidGlobals.Load();
        SpawnPoints.instance.initServer1();
        ServerGUI.init2();
    }
    
    public static void startServer() throws ConnectException {
        String value = ServerOptions.instance.Password.getValue();
        if (CoopSlave.instance != null && SteamUtils.isSteamModeEnabled()) {
            value = "";
        }
        GameServer.udpEngine = new UdpEngine(GameServer.DEFAULT_PORT, ServerOptions.getInstance().getMaxPlayers(), value, true);
        DebugLog.log(DebugType.Network, "*** SERVER STARTED ****");
        DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, SteamUtils.isSteamModeEnabled() ? "enabled" : "not enabled"));
        DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, GameServer.DEFAULT_PORT));
        GameServer.ResetID = ServerOptions.instance.ResetID.getValue();
        if (CoopSlave.instance != null) {
            if (SteamUtils.isSteamModeEnabled()) {
                CoopSlave.instance.sendMessage("server-address", (String)null, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, GameServer.udpEngine.getPeer().GetServerIP(), GameServer.DEFAULT_PORT));
                CoopSlave.instance.sendMessage("steam-id", null, SteamUtils.convertSteamIDToString(SteamGameServer.GetSteamID()));
            }
            else {
                CoopSlave.instance.sendMessage("server-address", (String)null, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, "127.0.0.1", GameServer.DEFAULT_PORT));
            }
        }
        LuaEventManager.triggerEvent("OnServerStarted");
        if (SteamUtils.isSteamModeEnabled()) {
            CoopSlave.status("UI_ServerStatus_Started");
        }
        else {
            CoopSlave.status("UI_ServerStatus_Started");
        }
        GameServer.discordBot.connect(ServerOptions.instance.DiscordEnable.getValue(), ServerOptions.instance.DiscordToken.getValue(), ServerOptions.instance.DiscordChannel.getValue(), ServerOptions.instance.DiscordChannelID.getValue());
    }
    
    private static void mainLoopDealWithNetData(final ZomboidNetData zomboidNetData) {
        if (!SystemDisabler.getDoMainLoopDealWithNetData()) {
            return;
        }
        final ByteBuffer buffer = zomboidNetData.buffer;
        final UdpConnection activeConnection = GameServer.udpEngine.getActiveConnection(zomboidNetData.connection);
        if (zomboidNetData.type >= 0 && zomboidNetData.type < GameServer.packetCounts.length) {
            final long[] packetCounts = GameServer.packetCounts;
            final short type = zomboidNetData.type;
            ++packetCounts[type];
            if (activeConnection != null) {
                final long[] packetCounts2 = activeConnection.packetCounts;
                final short type2 = zomboidNetData.type;
                ++packetCounts2[type2];
            }
        }
        MPStatistic.getInstance().addIncomePacket(zomboidNetData.type, buffer.limit());
        try {
            final PacketTypes.PacketType packetType = PacketTypes.packetTypes.get(zomboidNetData.type);
            if (activeConnection == null) {
                DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, packetType.name()));
                return;
            }
            if (activeConnection.username == null) {
                switch (packetType) {
                    case Login:
                    case Ping:
                    case ScoreboardUpdate: {
                        break;
                    }
                    default: {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, packetType.name(), activeConnection.getInetSocketAddress().getHostString()));
                        activeConnection.forceDisconnect();
                        ZomboidNetDataPool.instance.discard(zomboidNetData);
                        return;
                    }
                }
            }
            packetType.onServerPacket(buffer, activeConnection, zomboidNetData.type);
        }
        catch (Exception ex) {
            if (activeConnection == null) {
                DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, zomboidNetData.type));
            }
            else {
                DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(SLjava/lang/String;)Ljava/lang/String;, zomboidNetData.type, activeConnection.username));
            }
            ex.printStackTrace();
        }
        ZomboidNetDataPool.instance.discard(zomboidNetData);
    }
    
    static void receiveInvMngRemoveItem(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final long long1 = byteBuffer.getLong();
        final IsoPlayer key = GameServer.IDToPlayerMap.get(byteBuffer.getShort());
        if (key == null) {
            return;
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.getConnectedGUID() == GameServer.PlayerToAddressMap.get(key)) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.InvMngRemoveItem.doPacket(startPacket);
                startPacket.putLong(long1);
                PacketTypes.PacketType.InvMngRemoveItem.send(udpConnection2);
                break;
            }
        }
    }
    
    static void receiveInvMngGetItem(final ByteBuffer src, final UdpConnection udpConnection, final short n) throws IOException {
        final IsoPlayer key = GameServer.IDToPlayerMap.get(src.getShort());
        if (key == null) {
            return;
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.getConnectedGUID() == GameServer.PlayerToAddressMap.get(key)) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.InvMngGetItem.doPacket(startPacket);
                src.rewind();
                startPacket.bb.put(src);
                PacketTypes.PacketType.InvMngGetItem.send(udpConnection2);
                break;
            }
        }
    }
    
    static void receiveInvMngReqItem(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        long long1 = 0L;
        String readString = null;
        if (byteBuffer.get() == 1) {
            readString = GameWindow.ReadString(byteBuffer);
        }
        else {
            long1 = byteBuffer.getLong();
        }
        final short short1 = byteBuffer.getShort();
        final IsoPlayer key = GameServer.IDToPlayerMap.get(byteBuffer.getShort());
        if (key == null) {
            return;
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.getConnectedGUID() == GameServer.PlayerToAddressMap.get(key)) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.InvMngReqItem.doPacket(startPacket);
                if (readString != null) {
                    startPacket.putByte((byte)1);
                    startPacket.putUTF(readString);
                }
                else {
                    startPacket.putByte((byte)0);
                    startPacket.putLong(long1);
                }
                startPacket.putShort(short1);
                PacketTypes.PacketType.InvMngReqItem.send(udpConnection2);
                break;
            }
        }
    }
    
    static void receiveRequestZipList(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) throws Exception {
        if (udpConnection.playerDownloadServer != null) {
            udpConnection.playerDownloadServer.receiveRequestArray(byteBuffer);
        }
    }
    
    static void receiveRequestLargeAreaZip(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        if (udpConnection.playerDownloadServer != null) {
            final int int1 = byteBuffer.getInt();
            final int int2 = byteBuffer.getInt();
            final int int3 = byteBuffer.getInt();
            udpConnection.connectArea[0] = new Vector3((float)int1, (float)int2, (float)int3);
            udpConnection.ChunkGridWidth = int3;
            ZombiePopulationManager.instance.updateLoadedAreas();
        }
    }
    
    static void receiveNotRequiredInZip(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        if (udpConnection.playerDownloadServer != null) {
            udpConnection.playerDownloadServer.receiveCancelRequest(byteBuffer);
        }
    }
    
    static void receiveLogin(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final String trim = GameWindow.ReadString(byteBuffer).trim();
        final String trim2 = GameWindow.ReadString(byteBuffer).trim();
        final String trim3 = GameWindow.ReadString(byteBuffer).trim();
        if (!trim3.equals(Core.getInstance().getVersionNumber())) {
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.AccessDenied.doPacket(startPacket);
            LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, trim, trim3, Core.getInstance().getVersionNumber()));
            startPacket.putUTF(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, trim3, Core.getInstance().getVersionNumber()));
            PacketTypes.PacketType.AccessDenied.send(udpConnection);
            udpConnection.forceDisconnect();
        }
        udpConnection.ip = udpConnection.getInetSocketAddress().getHostString();
        udpConnection.idStr = udpConnection.ip;
        if (SteamUtils.isSteamModeEnabled()) {
            udpConnection.steamID = GameServer.udpEngine.getClientSteamID(udpConnection.getConnectedGUID());
            udpConnection.ownerID = GameServer.udpEngine.getClientOwnerSteamID(udpConnection.getConnectedGUID());
            udpConnection.idStr = SteamUtils.convertSteamIDToString(udpConnection.steamID);
            if (udpConnection.steamID != udpConnection.ownerID) {
                udpConnection.idStr = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, udpConnection.idStr, SteamUtils.convertSteamIDToString(udpConnection.ownerID));
            }
        }
        udpConnection.password = trim2;
        LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, udpConnection.idStr, trim));
        if (CoopSlave.instance == null || !SteamUtils.isSteamModeEnabled()) {
            final ServerWorldDatabase.LogonResult authClient = ServerWorldDatabase.instance.authClient(trim, trim2, udpConnection.ip, udpConnection.steamID);
            if (authClient.bAuthorized) {
                for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                    final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                    for (int j = 0; j < 4; ++j) {
                        if (trim.equals(udpConnection2.usernames[j])) {
                            LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, trim));
                            final ByteBufferWriter startPacket2 = udpConnection.startPacket();
                            PacketTypes.PacketType.AccessDenied.doPacket(startPacket2);
                            startPacket2.putUTF("AlreadyConnected");
                            PacketTypes.PacketType.AccessDenied.send(udpConnection);
                            udpConnection.forceDisconnect();
                            return;
                        }
                    }
                }
                udpConnection.username = trim;
                udpConnection.usernames[0] = trim;
                GameServer.transactionIDMap.put(trim, authClient.transactionID);
                if (CoopSlave.instance != null) {
                    udpConnection.isCoopHost = (GameServer.udpEngine.connections.size() == 1);
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Z)Ljava/lang/String;, udpConnection.idStr, udpConnection.isCoopHost));
                }
                udpConnection.accessLevel = authClient.accessLevel;
                if (!ServerOptions.instance.DoLuaChecksum.getValue() || authClient.accessLevel.equals("admin")) {
                    udpConnection.checksumState = UdpConnection.ChecksumState.Done;
                }
                if (!authClient.accessLevel.equals("") && getPlayerCount() >= ServerOptions.getInstance().getMaxPlayers()) {
                    final ByteBufferWriter startPacket3 = udpConnection.startPacket();
                    PacketTypes.PacketType.AccessDenied.doPacket(startPacket3);
                    startPacket3.putUTF("ServerFull");
                    PacketTypes.PacketType.AccessDenied.send(udpConnection);
                    udpConnection.forceDisconnect();
                    return;
                }
                if (!ServerWorldDatabase.instance.containsUser(trim) && ServerWorldDatabase.instance.containsCaseinsensitiveUser(trim)) {
                    final ByteBufferWriter startPacket4 = udpConnection.startPacket();
                    PacketTypes.PacketType.AccessDenied.doPacket(startPacket4);
                    startPacket4.putUTF("InvalidUsername");
                    PacketTypes.PacketType.AccessDenied.send(udpConnection);
                    udpConnection.forceDisconnect();
                    return;
                }
                LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, udpConnection.idStr, trim));
                try {
                    if (ServerOptions.instance.AutoCreateUserInWhiteList.getValue() && !ServerWorldDatabase.instance.containsUser(trim)) {
                        ServerWorldDatabase.instance.addUser(trim, trim2);
                    }
                    else {
                        ServerWorldDatabase.instance.setPassword(trim, trim2);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                ServerWorldDatabase.instance.updateLastConnectionDate(trim, trim2);
                if (SteamUtils.isSteamModeEnabled()) {
                    ServerWorldDatabase.instance.setUserSteamID(trim, SteamUtils.convertSteamIDToString(udpConnection.steamID));
                }
                receiveClientConnect(udpConnection, authClient);
            }
            else {
                final ByteBufferWriter startPacket5 = udpConnection.startPacket();
                PacketTypes.PacketType.AccessDenied.doPacket(startPacket5);
                if (authClient.banned) {
                    LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, trim));
                    if (authClient.bannedReason != null && !authClient.bannedReason.isEmpty()) {
                        startPacket5.putUTF(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, authClient.bannedReason));
                    }
                    else {
                        startPacket5.putUTF("Banned");
                    }
                }
                else if (!authClient.bAuthorized) {
                    LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, trim, authClient.dcReason));
                    startPacket5.putUTF((authClient.dcReason != null) ? authClient.dcReason : "AccessDenied");
                }
                PacketTypes.PacketType.AccessDenied.send(udpConnection);
                udpConnection.forceDisconnect();
            }
            return;
        }
        for (int k = 0; k < GameServer.udpEngine.connections.size(); ++k) {
            final UdpConnection udpConnection3 = GameServer.udpEngine.connections.get(k);
            if (udpConnection3 != udpConnection) {
                if (udpConnection3.steamID == udpConnection.steamID) {
                    LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, trim));
                    final ByteBufferWriter startPacket6 = udpConnection.startPacket();
                    PacketTypes.PacketType.AccessDenied.doPacket(startPacket6);
                    startPacket6.putUTF("AlreadyConnected");
                    PacketTypes.PacketType.AccessDenied.send(udpConnection);
                    udpConnection.forceDisconnect();
                    return;
                }
            }
        }
        udpConnection.username = trim;
        udpConnection.usernames[0] = trim;
        udpConnection.isCoopHost = (GameServer.udpEngine.connections.size() == 1);
        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Z)Ljava/lang/String;, udpConnection.idStr, udpConnection.isCoopHost));
        udpConnection.accessLevel = "";
        if (!ServerOptions.instance.DoLuaChecksum.getValue() || udpConnection.accessLevel.equals("admin")) {
            udpConnection.checksumState = UdpConnection.ChecksumState.Done;
        }
        if (getPlayerCount() >= ServerOptions.getInstance().getMaxPlayers()) {
            final ByteBufferWriter startPacket7 = udpConnection.startPacket();
            PacketTypes.PacketType.AccessDenied.doPacket(startPacket7);
            startPacket7.putUTF("ServerFull");
            PacketTypes.PacketType.AccessDenied.send(udpConnection);
            udpConnection.forceDisconnect();
            return;
        }
        if (isServerDropPackets() && !udpConnection.accessLevel.equals("admin") && ServerOptions.instance.DenyLoginOnOverloadedServer.getValue()) {
            final ByteBufferWriter startPacket8 = udpConnection.startPacket();
            PacketTypes.PacketType.AccessDenied.doPacket(startPacket8);
            LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, trim));
            startPacket8.putUTF("Server is too busy.");
            PacketTypes.PacketType.AccessDenied.send(udpConnection);
            udpConnection.forceDisconnect();
            ++GameServer.countOfDroppedConnections;
        }
        LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, udpConnection.idStr, trim));
        final ServerWorldDatabase instance = ServerWorldDatabase.instance;
        Objects.requireNonNull(instance);
        final ServerWorldDatabase.LogonResult logonResult = instance.new LogonResult();
        logonResult.accessLevel = udpConnection.accessLevel;
        receiveClientConnect(udpConnection, logonResult);
    }
    
    static void receiveSendInventory(final ByteBuffer src, final UdpConnection udpConnection, final short n) {
        final Long n2 = GameServer.IDToAddressMap.get(src.getInt());
        if (n2 != null) {
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() == n2) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.SendInventory.doPacket(startPacket);
                    startPacket.bb.put(src);
                    PacketTypes.PacketType.SendInventory.send(udpConnection2);
                    break;
                }
            }
        }
    }
    
    static void receivePlayerStartPMChat(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        ChatServer.getInstance().processPlayerStartWhisperChatPacket(byteBuffer);
    }
    
    static void receiveRequestInventory(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final Long n2 = GameServer.IDToAddressMap.get(byteBuffer.getShort());
        if (n2 != null) {
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() == n2) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.RequestInventory.doPacket(startPacket);
                    startPacket.putShort(short1);
                    PacketTypes.PacketType.RequestInventory.send(udpConnection2);
                    break;
                }
            }
        }
    }
    
    static void receiveStatistic(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        try {
            udpConnection.statistic.parse(byteBuffer);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static void receiveStatisticRequest(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        if (!udpConnection.accessLevel.equals("admin") && !Core.bDebug) {
            DebugLog.General.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, udpConnection.username));
            return;
        }
        try {
            udpConnection.statistic.enable = byteBuffer.get();
            sendStatistic(udpConnection);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static void receiveZombieSimulation(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        NetworkZombiePacker.getInstance().receivePacket(byteBuffer, udpConnection);
    }
    
    public static void sendShortStatistic() {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.statistic.enable == 3) {
                sendShortStatistic(udpConnection);
            }
        }
    }
    
    public static void sendShortStatistic(final UdpConnection udpConnection) {
        try {
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.StatisticRequest.doPacket(startPacket);
            MPStatistic.getInstance().write(startPacket);
            PacketTypes.PacketType.StatisticRequest.send(udpConnection);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            udpConnection.cancelPacket();
        }
    }
    
    public static void sendStatistic() {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.statistic.enable == 1) {
                sendStatistic(udpConnection);
            }
        }
    }
    
    public static void sendStatistic(final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.StatisticRequest.doPacket(startPacket);
        try {
            MPStatistic.getInstance().getStatisticTable(startPacket.bb);
            PacketTypes.PacketType.StatisticRequest.send(udpConnection);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            udpConnection.cancelPacket();
        }
    }
    
    public static void getStatisticFromClients() {
        try {
            for (final UdpConnection udpConnection : GameServer.udpEngine.connections) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.Statistic.doPacket(startPacket);
                startPacket.putLong(System.currentTimeMillis());
                PacketTypes.PacketType.Statistic.send(udpConnection);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void updateZombieControl(final IsoZombie isoZombie, final short n, final int n2) {
        try {
            if (isoZombie.authOwner == null) {
                return;
            }
            final ByteBufferWriter startPacket = isoZombie.authOwner.startPacket();
            PacketTypes.PacketType.ZombieControl.doPacket(startPacket);
            startPacket.putShort(isoZombie.OnlineID);
            startPacket.putShort(n);
            startPacket.putInt(n2);
            PacketTypes.PacketType.ZombieControl.send(isoZombie.authOwner);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static void receivePlayerUpdate(final ByteBuffer src, final UdpConnection udpConnection, final short n) {
        if (udpConnection.checksumState != UdpConnection.ChecksumState.Done) {
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.Kicked.doPacket(startPacket);
            startPacket.putUTF("You have been kicked from this server.");
            PacketTypes.PacketType.Kicked.send(udpConnection);
            udpConnection.forceDisconnect();
            return;
        }
        final PlayerPacket playerPacket = PlayerPacket.l_receive.playerPacket;
        playerPacket.parse(src);
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(playerPacket.id);
        if (isoPlayer.replay != null) {
            isoPlayer.replay.recordPlayerPacket(playerPacket);
            if (isoPlayer.replay.isPlay()) {
                return;
            }
        }
        try {
            if (isoPlayer == null) {
                DebugLog.General.error(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, playerPacket.id));
            }
            else {
                if (!isoPlayer.networkAI.isSetVehicleHit()) {
                    isoPlayer.networkAI.parse(playerPacket);
                }
                isoPlayer.bleedingLevel = playerPacket.bleedingLevel;
                if (isoPlayer.networkAI.distance.getLength() > IsoChunkMap.ChunkWidthInTiles) {
                    MPStatistic.getInstance().teleport();
                }
                RakVoice.SetPlayerCoordinate(udpConnection.getConnectedGUID(), playerPacket.realx, playerPacket.realy, playerPacket.realz, isoPlayer.isCanHearAll());
                udpConnection.ReleventPos[isoPlayer.PlayerIndex].x = playerPacket.realx;
                udpConnection.ReleventPos[isoPlayer.PlayerIndex].y = playerPacket.realy;
                udpConnection.ReleventPos[isoPlayer.PlayerIndex].z = playerPacket.realz;
                playerPacket.id = isoPlayer.getOnlineID();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        if (ServerOptions.instance.KickFastPlayers.getValue()) {
            final Vector2 vector2 = GameServer.playerToCoordsMap.get((int)playerPacket.id);
            if (vector2 == null) {
                final Vector2 value = new Vector2();
                value.x = playerPacket.x;
                value.y = playerPacket.y;
                GameServer.playerToCoordsMap.put(playerPacket.id, value);
            }
            else {
                if (!isoPlayer.accessLevel.equals("") && !isoPlayer.isGhostMode() && (Math.abs(playerPacket.x - vector2.x) > 4.0f || Math.abs(playerPacket.y - vector2.y) > 4.0f)) {
                    if (GameServer.playerMovedToFastMap.get(playerPacket.id) == null) {
                        GameServer.playerMovedToFastMap.put(playerPacket.id, 1);
                    }
                    else {
                        GameServer.playerMovedToFastMap.put(playerPacket.id, GameServer.playerMovedToFastMap.get((int)playerPacket.id) + 1);
                    }
                    LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;, isoPlayer.getDisplayName(), GameServer.playerMovedToFastMap.get((int)playerPacket.id)));
                    if (GameServer.playerMovedToFastMap.get(playerPacket.id) == 10) {
                        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, isoPlayer.getDisplayName()));
                        final ByteBufferWriter startPacket2 = udpConnection.startPacket();
                        PacketTypes.PacketType.Kicked.doPacket(startPacket2);
                        startPacket2.putUTF("You have been kicked from this server.");
                        PacketTypes.PacketType.Kicked.send(udpConnection);
                        udpConnection.forceDisconnect();
                        return;
                    }
                }
                vector2.x = playerPacket.x;
                vector2.y = playerPacket.y;
            }
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection.getConnectedGUID() != udpConnection2.getConnectedGUID() && udpConnection2.isFullyConnected() && (n == PacketTypes.PacketType.PlayerUpdateReliable.getId() || udpConnection2.RelevantTo(playerPacket.x, playerPacket.y))) {
                final ByteBufferWriter startPacket3 = udpConnection2.startPacket();
                PacketTypes.packetTypes.get(n).doPacket(startPacket3);
                src.position(0);
                startPacket3.bb.put(src);
                PacketTypes.packetTypes.get(n).send(udpConnection2);
            }
        }
    }
    
    static void receivePacketCounts(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        if (udpConnection.accessLevel.isEmpty()) {
            return;
        }
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.PacketCounts.doPacket(startPacket);
        for (int i = 0; i < 256; ++i) {
            startPacket.putLong(GameServer.packetCounts[i]);
        }
        PacketTypes.PacketType.PacketCounts.send(udpConnection);
    }
    
    static void receiveSandboxOptions(final ByteBuffer src, final UdpConnection udpConnection, final short n) {
        try {
            SandboxOptions.instance.load(src);
            SandboxOptions.instance.applySettings();
            SandboxOptions.instance.toLua();
            SandboxOptions.instance.saveServerLuaFile(GameServer.ServerName);
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.SandboxOptions.doPacket(startPacket);
                src.rewind();
                startPacket.bb.put(src);
                PacketTypes.PacketType.SandboxOptions.send(udpConnection2);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static void receiveChunkObjectState(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final short short2 = byteBuffer.getShort();
        final IsoChunk chunk = ServerMap.instance.getChunk(short1, short2);
        if (chunk == null) {
            udpConnection.chunkObjectState.add(short1);
            udpConnection.chunkObjectState.add(short2);
        }
        else {
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.ChunkObjectState.doPacket(startPacket);
            startPacket.putShort(short1);
            startPacket.putShort(short2);
            try {
                if (chunk.saveObjectState(startPacket.bb)) {
                    PacketTypes.PacketType.ChunkObjectState.send(udpConnection);
                }
                else {
                    udpConnection.cancelPacket();
                }
            }
            catch (Throwable t) {
                t.printStackTrace();
                udpConnection.cancelPacket();
            }
        }
    }
    
    static void receiveReadAnnotedMap(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        StashSystem.prepareBuildingStash(GameWindow.ReadString(byteBuffer));
    }
    
    static void receiveTradingUIRemoveItem(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final short short2 = byteBuffer.getShort();
        final int int1 = byteBuffer.getInt();
        final Long n2 = GameServer.IDToAddressMap.get(short2);
        if (n2 != null) {
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() == n2) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.TradingUIRemoveItem.doPacket(startPacket);
                    startPacket.putShort(short1);
                    startPacket.putInt(int1);
                    PacketTypes.PacketType.TradingUIRemoveItem.send(udpConnection2);
                    break;
                }
            }
        }
    }
    
    static void receiveTradingUIUpdateState(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final short short2 = byteBuffer.getShort();
        final int int1 = byteBuffer.getInt();
        final Long n2 = GameServer.IDToAddressMap.get(short2);
        if (n2 != null) {
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() == n2) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.TradingUIUpdateState.doPacket(startPacket);
                    startPacket.putShort(short1);
                    startPacket.putInt(int1);
                    PacketTypes.PacketType.TradingUIUpdateState.send(udpConnection2);
                    break;
                }
            }
        }
    }
    
    static void receiveTradingUIAddItem(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final short short2 = byteBuffer.getShort();
        InventoryItem loadItem = null;
        try {
            loadItem = InventoryItem.loadItem(byteBuffer, 186);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        if (loadItem == null) {
            return;
        }
        final Long n2 = GameServer.IDToAddressMap.get(short2);
        if (n2 != null) {
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() == n2) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.TradingUIAddItem.doPacket(startPacket);
                    startPacket.putShort(short1);
                    try {
                        loadItem.saveWithSize(startPacket.bb, false);
                    }
                    catch (IOException ex2) {
                        ex2.printStackTrace();
                    }
                    PacketTypes.PacketType.TradingUIAddItem.send(udpConnection2);
                    break;
                }
            }
        }
    }
    
    static void receiveRequestTrading(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final short short2 = byteBuffer.getShort();
        final byte value = byteBuffer.get();
        Long n2 = GameServer.IDToAddressMap.get(short1);
        if (value == 0) {
            n2 = GameServer.IDToAddressMap.get(short2);
        }
        if (n2 != null) {
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() == n2) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.RequestTrading.doPacket(startPacket);
                    if (value == 0) {
                        startPacket.putShort(short1);
                    }
                    else {
                        startPacket.putShort(short2);
                    }
                    startPacket.putByte(value);
                    PacketTypes.PacketType.RequestTrading.send(udpConnection2);
                    break;
                }
            }
        }
    }
    
    static void receiveSyncFaction(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final String readString = GameWindow.ReadString(byteBuffer);
        final String readString2 = GameWindow.ReadString(byteBuffer);
        final int int1 = byteBuffer.getInt();
        Faction faction = Faction.getFaction(readString);
        boolean b = false;
        if (faction == null) {
            faction = new Faction(readString, readString2);
            b = true;
            Faction.getFactions().add(faction);
        }
        faction.getPlayers().clear();
        if (byteBuffer.get() == 1) {
            faction.setTag(GameWindow.ReadString(byteBuffer));
            faction.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0f));
        }
        for (int i = 0; i < int1; ++i) {
            faction.getPlayers().add(GameWindow.ReadString(byteBuffer));
        }
        if (!faction.getOwner().equals(readString2)) {
            faction.setOwner(readString2);
        }
        final boolean b2 = byteBuffer.get() == 1;
        if (ChatServer.isInited()) {
            if (b) {
                ChatServer.getInstance().createFactionChat(readString);
            }
            if (b2) {
                ChatServer.getInstance().removeFactionChat(readString);
            }
            else {
                ChatServer.getInstance().syncFactionChatMembers(readString, readString2, faction.getPlayers());
            }
        }
        if (b2) {
            Faction.getFactions().remove(faction);
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, readString, faction.getOwner()));
        }
        for (int j = 0; j < GameServer.udpEngine.connections.size(); ++j) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(j);
            if (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.SyncFaction.doPacket(startPacket);
                faction.writeToBuffer(startPacket, b2);
                PacketTypes.PacketType.SyncFaction.send(udpConnection2);
            }
        }
    }
    
    static void receiveSyncNonPvpZone(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        final String readString = GameWindow.ReadString(byteBuffer);
        NonPvpZone nonPvpZone = NonPvpZone.getZoneByTitle(readString);
        if (nonPvpZone == null) {
            nonPvpZone = NonPvpZone.addNonPvpZone(readString, int1, int2, int3, int4);
        }
        if (nonPvpZone == null) {
            return;
        }
        final boolean b = byteBuffer.get() == 1;
        sendNonPvpZone(nonPvpZone, b, udpConnection);
        if (b) {
            NonPvpZone.removeNonPvpZone(readString, true);
            DebugLog.log(invokedynamic(makeConcatWithConstants:(IILjava/lang/String;)Ljava/lang/String;, int1, int2, nonPvpZone.getTitle()));
        }
    }
    
    public static void sendNonPvpZone(final NonPvpZone nonPvpZone, final boolean b, final UdpConnection udpConnection) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.SyncNonPvpZone.doPacket(startPacket);
                startPacket.putInt(nonPvpZone.getX());
                startPacket.putInt(nonPvpZone.getY());
                startPacket.putInt(nonPvpZone.getX2());
                startPacket.putInt(nonPvpZone.getY2());
                startPacket.putUTF(nonPvpZone.getTitle());
                startPacket.putBoolean(b);
                PacketTypes.PacketType.SyncNonPvpZone.send(udpConnection2);
            }
        }
    }
    
    static void receiveChangeTextColor(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short1);
        if (isoPlayer == null) {
            return;
        }
        final float float1 = byteBuffer.getFloat();
        final float float2 = byteBuffer.getFloat();
        final float float3 = byteBuffer.getFloat();
        isoPlayer.setSpeakColourInfo(new ColorInfo(float1, float2, float3, 1.0f));
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.ChangeTextColor.doPacket(startPacket);
                startPacket.putShort(short1);
                startPacket.putFloat(float1);
                startPacket.putFloat(float2);
                startPacket.putFloat(float3);
                PacketTypes.PacketType.ChangeTextColor.send(udpConnection2);
            }
        }
    }
    
    @Deprecated
    static void receiveTransactionID(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        final short short1 = byteBuffer.getShort();
        final int int1 = byteBuffer.getInt();
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short1);
        if (isoPlayer != null) {
            GameServer.transactionIDMap.put(isoPlayer.username, int1);
            isoPlayer.setTransactionID(int1);
            ServerWorldDatabase.instance.saveTransactionID(isoPlayer.username, int1);
        }
    }
    
    static void receiveSyncCompost(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(byteBuffer.getInt(), byteBuffer.getInt(), byteBuffer.getInt());
        if (gridSquare != null) {
            IsoCompost compost = gridSquare.getCompost();
            if (compost == null) {
                compost = new IsoCompost(gridSquare.getCell(), gridSquare);
                gridSquare.AddSpecialObject(compost);
            }
            compost.setCompost(byteBuffer.getFloat());
            sendCompost(compost, udpConnection);
        }
    }
    
    public static void sendCompost(final IsoCompost isoCompost, final UdpConnection udpConnection) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.RelevantTo((float)isoCompost.square.x, (float)isoCompost.square.y) && ((udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) || udpConnection == null)) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.SyncCompost.doPacket(startPacket);
                startPacket.putInt(isoCompost.square.x);
                startPacket.putInt(isoCompost.square.y);
                startPacket.putInt(isoCompost.square.z);
                startPacket.putFloat(isoCompost.getCompost());
                PacketTypes.PacketType.SyncCompost.send(udpConnection2);
            }
        }
    }
    
    static void receiveCataplasm(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get((int)short1);
        if (isoPlayer != null) {
            final int int1 = byteBuffer.getInt();
            final float float1 = byteBuffer.getFloat();
            final float float2 = byteBuffer.getFloat();
            final float float3 = byteBuffer.getFloat();
            if (float1 > 0.0f) {
                isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setPlantainFactor(float1);
            }
            if (float2 > 0.0f) {
                isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setComfreyFactor(float2);
            }
            if (float3 > 0.0f) {
                isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setGarlicFactor(float3);
            }
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.Cataplasm.doPacket(startPacket);
                    startPacket.putShort(short1);
                    startPacket.putInt(int1);
                    startPacket.putFloat(float1);
                    startPacket.putFloat(float2);
                    startPacket.putFloat(float3);
                    PacketTypes.PacketType.Cataplasm.send(udpConnection2);
                }
            }
        }
    }
    
    static void receiveSledgehammerDestroy(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        if (ServerOptions.instance.AllowDestructionBySledgehammer.getValue()) {
            receiveRemoveItemFromSquare(byteBuffer, udpConnection, n);
        }
    }
    
    public static void AddExplosiveTrap(final HandWeapon handWeapon, final IsoGridSquare isoGridSquare, final boolean b) {
        final IsoTrap isoTrap = new IsoTrap(handWeapon, isoGridSquare.getCell(), isoGridSquare);
        int n = 0;
        if (handWeapon.getExplosionRange() > 0) {
            n = handWeapon.getExplosionRange();
        }
        if (handWeapon.getFireRange() > 0) {
            n = handWeapon.getFireRange();
        }
        if (handWeapon.getSmokeRange() > 0) {
            n = handWeapon.getSmokeRange();
        }
        isoGridSquare.AddTileObject(isoTrap);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.AddExplosiveTrap.doPacket(startPacket);
            startPacket.putInt(isoGridSquare.x);
            startPacket.putInt(isoGridSquare.y);
            startPacket.putInt(isoGridSquare.z);
            try {
                handWeapon.saveWithSize(startPacket.bb, false);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            startPacket.putInt(n);
            startPacket.putBoolean(b);
            startPacket.putBoolean(false);
            PacketTypes.PacketType.AddExplosiveTrap.send(udpConnection);
        }
    }
    
    static void receiveAddExplosiveTrap(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
        if (gridSquare != null) {
            InventoryItem loadItem = null;
            try {
                loadItem = InventoryItem.loadItem(byteBuffer, 186);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            if (loadItem == null) {
                return;
            }
            final HandWeapon handWeapon = (HandWeapon)loadItem;
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;III)Ljava/lang/String;, udpConnection.username, loadItem.getFullType(), int1, int2, int3));
            LoggerManager.getLogger("map").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;III)Ljava/lang/String;, udpConnection.idStr, udpConnection.username, loadItem.getFullType(), int1, int2, int3));
            if (handWeapon.isInstantExplosion()) {
                final IsoTrap isoTrap = new IsoTrap(handWeapon, gridSquare.getCell(), gridSquare);
                gridSquare.AddTileObject(isoTrap);
                isoTrap.triggerExplosion(false);
            }
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.AddExplosiveTrap.doPacket(startPacket);
                    startPacket.putInt(int1);
                    startPacket.putInt(int2);
                    startPacket.putInt(int3);
                    try {
                        handWeapon.saveWithSize(startPacket.bb, false);
                    }
                    catch (IOException ex2) {
                        ex2.printStackTrace();
                    }
                    PacketTypes.PacketType.AddExplosiveTrap.send(udpConnection2);
                }
            }
        }
    }
    
    public static void sendHelicopter(final float n, final float n2, final boolean b) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.Helicopter.doPacket(startPacket);
            startPacket.putFloat(n);
            startPacket.putFloat(n2);
            startPacket.putBoolean(b);
            PacketTypes.PacketType.Helicopter.send(udpConnection);
        }
    }
    
    static void receiveRegisterZone(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final String readString = GameWindow.ReadString(byteBuffer);
        final String readString2 = GameWindow.ReadString(byteBuffer);
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        final int int5 = byteBuffer.getInt();
        final int int6 = byteBuffer.getInt();
        final boolean b = byteBuffer.get() == 1;
        final ArrayList<IsoMetaGrid.Zone> zones = IsoWorld.instance.getMetaGrid().getZonesAt(int1, int2, int3);
        boolean b2 = false;
        for (final IsoMetaGrid.Zone zone : zones) {
            if (readString2.equals(zone.getType())) {
                b2 = true;
                zone.setName(readString);
                zone.setLastActionTimestamp(int6);
            }
        }
        if (!b2) {
            IsoWorld.instance.getMetaGrid().registerZone(readString, readString2, int1, int2, int3, int4, int5);
        }
        if (b) {
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.RegisterZone.doPacket(startPacket);
                    startPacket.putUTF(readString);
                    startPacket.putUTF(readString2);
                    startPacket.putInt(int1);
                    startPacket.putInt(int2);
                    startPacket.putInt(int3);
                    startPacket.putInt(int4);
                    startPacket.putInt(int5);
                    startPacket.putInt(int6);
                    PacketTypes.PacketType.RegisterZone.send(udpConnection2);
                }
            }
        }
    }
    
    public static void sendZone(final IsoMetaGrid.Zone zone, final UdpConnection udpConnection) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.RegisterZone.doPacket(startPacket);
                startPacket.putUTF(zone.name);
                startPacket.putUTF(zone.type);
                startPacket.putInt(zone.x);
                startPacket.putInt(zone.y);
                startPacket.putInt(zone.z);
                startPacket.putInt(zone.w);
                startPacket.putInt(zone.h);
                startPacket.putInt(zone.lastActionTimestamp);
                PacketTypes.PacketType.RegisterZone.send(udpConnection2);
            }
        }
    }
    
    static void receiveConstructedZone(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final IsoMetaGrid.Zone zone = IsoWorld.instance.MetaGrid.getZoneAt(byteBuffer.getInt(), byteBuffer.getInt(), byteBuffer.getInt());
        if (zone != null) {
            zone.setHaveConstruction(true);
        }
    }
    
    public static void addXp(final IsoPlayer isoPlayer, final PerkFactory.Perk perk, final int n) {
        if (GameServer.PlayerToAddressMap.containsKey(isoPlayer)) {
            final UdpConnection activeConnection = GameServer.udpEngine.getActiveConnection(GameServer.PlayerToAddressMap.get(isoPlayer));
            if (activeConnection == null) {
                return;
            }
            final ByteBufferWriter startPacket = activeConnection.startPacket();
            PacketTypes.PacketType.AddXP.doPacket(startPacket);
            startPacket.putByte((byte)isoPlayer.PlayerIndex);
            startPacket.putInt(perk.index());
            startPacket.putInt(n);
            PacketTypes.PacketType.AddXP.send(activeConnection);
        }
    }
    
    static void receiveWriteLog(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        LoggerManager.getLogger(GameWindow.ReadString(byteBuffer)).write(GameWindow.ReadString(byteBuffer));
    }
    
    static void receiveChecksum(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        NetChecksum.comparer.serverPacket(byteBuffer, udpConnection);
    }
    
    private static void answerPing(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        final String readString = GameWindow.ReadString(byteBuffer);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.Ping.doPacket(startPacket);
                startPacket.putUTF(readString);
                startPacket.putInt(GameServer.udpEngine.connections.size());
                startPacket.putInt(512);
                PacketTypes.PacketType.Ping.send(udpConnection2);
            }
        }
    }
    
    static void receiveUpdateItemSprite(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        final int int5 = byteBuffer.getInt();
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, int4);
        if (gridSquare != null && int5 < gridSquare.getObjects().size()) {
            try {
                final IsoObject isoObject = gridSquare.getObjects().get(int5);
                if (isoObject != null) {
                    isoObject.sprite = IsoSpriteManager.instance.getSprite(int1);
                    if (isoObject.sprite == null && !readStringUTF.isEmpty()) {
                        isoObject.setSprite(readStringUTF);
                    }
                    isoObject.RemoveAttachedAnims();
                    for (int n2 = byteBuffer.get() & 0xFF, i = 0; i < n2; ++i) {
                        final IsoSprite sprite = IsoSpriteManager.instance.getSprite(byteBuffer.getInt());
                        if (sprite != null) {
                            isoObject.AttachExistingAnim(sprite, 0, 0, false, 0, false, 0.0f);
                        }
                    }
                    isoObject.transmitUpdatedSpriteToClients(udpConnection);
                }
            }
            catch (Exception ex) {}
        }
    }
    
    static void receiveWorldMessage(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        if (udpConnection.allChatMuted) {
            return;
        }
        final String readString = GameWindow.ReadString(byteBuffer);
        String s = GameWindow.ReadString(byteBuffer);
        if (s.length() > 256) {
            s = s.substring(0, 256);
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            final ByteBufferWriter startPacket = udpConnection2.startPacket();
            PacketTypes.PacketType.WorldMessage.doPacket(startPacket);
            startPacket.putUTF(readString);
            startPacket.putUTF(s);
            PacketTypes.PacketType.WorldMessage.send(udpConnection2);
        }
        GameServer.discordBot.sendMessage(readString, s);
        LoggerManager.getLogger("chat").write(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, udpConnection.index, udpConnection.username, s));
    }
    
    static void receiveGetModData(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        LuaEventManager.triggerEvent("SendCustomModData");
    }
    
    static void receiveStopFire(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final byte value = byteBuffer.get();
        if (value == 1) {
            final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(byteBuffer.getShort());
            if (isoPlayer != null) {
                isoPlayer.sendObjectChange("StopBurning");
            }
            return;
        }
        if (value == 2) {
            final IsoZombie value2 = ServerMap.instance.ZombieMap.get(byteBuffer.getShort());
            if (value2 != null) {
                value2.StopBurning();
            }
            return;
        }
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(int1, int2, int3);
        if (gridSquare == null) {
            return;
        }
        gridSquare.stopFire();
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.RelevantTo((float)int1, (float)int2) && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.StopFire.doPacket(startPacket);
                startPacket.putInt(int1);
                startPacket.putInt(int2);
                startPacket.putInt(int3);
                PacketTypes.PacketType.StopFire.send(udpConnection2);
            }
        }
    }
    
    static void receiveStartFire(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        final boolean b = byteBuffer.get() == 1;
        final int int5 = byteBuffer.getInt();
        final boolean b2 = byteBuffer.get() == 1;
        if (!b2 && ServerOptions.instance.NoFire.getValue()) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, udpConnection.username));
            return;
        }
        final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(int1, int2, int3);
        if (gridSquare == null) {
            return;
        }
        final IsoFire isoFire = b2 ? new IsoFire(gridSquare.getCell(), gridSquare, b, int4, int5, true) : new IsoFire(gridSquare.getCell(), gridSquare, b, int4, int5);
        IsoFireManager.Add(isoFire);
        gridSquare.getObjects().add(isoFire);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.RelevantTo((float)int1, (float)int2)) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.StartFire.doPacket(startPacket);
                startPacket.putInt(int1);
                startPacket.putInt(int2);
                startPacket.putInt(int3);
                startPacket.putInt(int4);
                startPacket.putBoolean(b);
                startPacket.putInt(isoFire.SpreadDelay);
                startPacket.putInt(isoFire.Life);
                startPacket.putInt(isoFire.numFlameParticles);
                startPacket.putBoolean(b2);
                PacketTypes.PacketType.StartFire.send(udpConnection2);
            }
        }
    }
    
    public static void startFireOnClient(final IsoGridSquare isoGridSquare, final int n, final boolean b, final int n2, final boolean b2) {
        final IsoFire isoFire = b2 ? new IsoFire(isoGridSquare.getCell(), isoGridSquare, b, n, n2, true) : new IsoFire(isoGridSquare.getCell(), isoGridSquare, b, n, n2);
        IsoFireManager.Add(isoFire);
        isoGridSquare.getObjects().add(isoFire);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.RelevantTo((float)isoGridSquare.getX(), (float)isoGridSquare.getY())) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.StartFire.doPacket(startPacket);
                startPacket.putInt(isoGridSquare.getX());
                startPacket.putInt(isoGridSquare.getY());
                startPacket.putInt(isoGridSquare.getZ());
                startPacket.putInt(n);
                startPacket.putBoolean(b);
                startPacket.putInt(isoFire.SpreadDelay);
                startPacket.putInt(isoFire.Life);
                startPacket.putInt(isoFire.numFlameParticles);
                startPacket.putBoolean(b2);
                PacketTypes.PacketType.StartFire.send(udpConnection);
            }
        }
    }
    
    public static void sendOptionsToClients() {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.ReloadOptions.doPacket(startPacket);
            startPacket.putInt(ServerOptions.instance.getPublicOptions().size());
            for (final String s : ServerOptions.instance.getPublicOptions()) {
                startPacket.putUTF(s);
                startPacket.putUTF(ServerOptions.instance.getOption(s));
            }
            PacketTypes.PacketType.ReloadOptions.send(udpConnection);
        }
    }
    
    public static void sendCorpse(final IsoDeadBody isoDeadBody) {
        final IsoGridSquare square = isoDeadBody.getSquare();
        if (square == null) {
            return;
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.RelevantTo((float)square.x, (float)square.y)) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.AddCorpseToMap.doPacket(startPacket);
                startPacket.putInt(square.x);
                startPacket.putInt(square.y);
                startPacket.putInt(square.z);
                isoDeadBody.writeToRemoteBuffer(startPacket);
                PacketTypes.PacketType.AddCorpseToMap.send(udpConnection);
            }
        }
    }
    
    static void receiveAddCorpseToMap(final ByteBuffer src, final UdpConnection udpConnection, final short n) {
        final int int1 = src.getInt();
        final int int2 = src.getInt();
        final int int3 = src.getInt();
        final IsoObject fromBuffer = WorldItemTypes.createFromBuffer(src);
        if (fromBuffer == null || !(fromBuffer instanceof IsoDeadBody)) {
            return;
        }
        fromBuffer.loadFromRemoteBuffer(src, false);
        final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(int1, int2, int3);
        if (gridSquare != null) {
            gridSquare.addCorpse((IsoDeadBody)fromBuffer, true);
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.RelevantTo((float)int1, (float)int2)) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.AddCorpseToMap.doPacket(startPacket);
                    src.rewind();
                    startPacket.bb.put(src);
                    PacketTypes.PacketType.AddCorpseToMap.send(udpConnection2);
                }
            }
        }
        LoggerManager.getLogger("item").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;III)Ljava/lang/String;, udpConnection.idStr, udpConnection.username, int1, int2, int3));
    }
    
    static void receiveSmashWindow(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final IsoObject itemFromXYZIndexBuffer = IsoWorld.instance.getItemFromXYZIndexBuffer(byteBuffer);
        if (itemFromXYZIndexBuffer != null && itemFromXYZIndexBuffer instanceof IsoWindow) {
            final byte value = byteBuffer.get();
            if (value == 1) {
                ((IsoWindow)itemFromXYZIndexBuffer).smashWindow(true);
                smashWindow((IsoWindow)itemFromXYZIndexBuffer, 1);
            }
            else if (value == 2) {
                ((IsoWindow)itemFromXYZIndexBuffer).setGlassRemoved(true);
                smashWindow((IsoWindow)itemFromXYZIndexBuffer, 2);
            }
        }
    }
    
    private static void sendPlayerConnect(final IsoPlayer isoPlayer, final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.PlayerConnect.doPacket(startPacket);
        if (udpConnection.getConnectedGUID() != GameServer.PlayerToAddressMap.get(isoPlayer)) {
            startPacket.putShort(isoPlayer.OnlineID);
        }
        else {
            startPacket.putShort((short)(-1));
            startPacket.putByte((byte)isoPlayer.PlayerIndex);
            startPacket.putShort(isoPlayer.OnlineID);
            try {
                GameTime.getInstance().saveToPacket(startPacket.bb);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        startPacket.putFloat(isoPlayer.x);
        startPacket.putFloat(isoPlayer.y);
        startPacket.putFloat(isoPlayer.z);
        startPacket.putUTF(isoPlayer.username);
        if (udpConnection.getConnectedGUID() != GameServer.PlayerToAddressMap.get(isoPlayer)) {
            try {
                isoPlayer.getDescriptor().save(startPacket.bb);
                isoPlayer.getHumanVisual().save(startPacket.bb);
                final ItemVisuals itemVisuals = new ItemVisuals();
                isoPlayer.getItemVisuals(itemVisuals);
                itemVisuals.save(startPacket.bb);
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
        if (SteamUtils.isSteamModeEnabled()) {
            startPacket.putLong(isoPlayer.getSteamID());
        }
        startPacket.putByte((byte)(isoPlayer.isGodMod() ? 1 : 0));
        startPacket.putByte((byte)(isoPlayer.isGhostMode() ? 1 : 0));
        startPacket.putByte((byte)(isoPlayer.isSafety() ? 1 : 0));
        startPacket.putUTF(isoPlayer.accessLevel);
        startPacket.putByte((byte)(isoPlayer.isInvisible() ? 1 : 0));
        if (udpConnection.getConnectedGUID() != GameServer.PlayerToAddressMap.get(isoPlayer)) {
            try {
                isoPlayer.getXp().save(startPacket.bb);
            }
            catch (IOException ex3) {
                ex3.printStackTrace();
            }
        }
        startPacket.putUTF(isoPlayer.getTagPrefix());
        startPacket.putFloat(isoPlayer.getTagColor().r);
        startPacket.putFloat(isoPlayer.getTagColor().g);
        startPacket.putFloat(isoPlayer.getTagColor().b);
        startPacket.putDouble(isoPlayer.getHoursSurvived());
        startPacket.putInt(isoPlayer.getZombieKills());
        startPacket.putUTF(isoPlayer.getDisplayName());
        startPacket.putFloat(isoPlayer.getSpeakColour().r);
        startPacket.putFloat(isoPlayer.getSpeakColour().g);
        startPacket.putFloat(isoPlayer.getSpeakColour().b);
        startPacket.putBoolean(isoPlayer.showTag);
        startPacket.putBoolean(isoPlayer.factionPvp);
        startPacket.putInt(isoPlayer.getAttachedItems().size());
        for (int i = 0; i < isoPlayer.getAttachedItems().size(); ++i) {
            startPacket.putUTF(isoPlayer.getAttachedItems().get(i).getLocation());
            startPacket.putUTF(isoPlayer.getAttachedItems().get(i).getItem().getFullType());
        }
        startPacket.putInt(isoPlayer.remoteSneakLvl);
        startPacket.putInt(isoPlayer.remoteStrLvl);
        startPacket.putInt(isoPlayer.remoteFitLvl);
        PacketTypes.PacketType.PlayerConnect.send(udpConnection);
        if (udpConnection.getConnectedGUID() != GameServer.PlayerToAddressMap.get(isoPlayer)) {
            updateHandEquips(udpConnection, isoPlayer);
        }
    }
    
    @Deprecated
    static void receiveRequestPlayerData(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(byteBuffer.getShort());
        if (isoPlayer != null) {
            sendPlayerConnect(isoPlayer, udpConnection);
        }
    }
    
    static void receiveChatMessageFromPlayer(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        ChatServer.getInstance().processMessageFromPlayerPacket(byteBuffer);
    }
    
    public static void loadModData(final IsoGridSquare isoGridSquare) {
        if (isoGridSquare.getModData().rawget((Object)"id") != null && isoGridSquare.getModData().rawget((Object)"id") != null && (isoGridSquare.getModData().rawget((Object)"remove") == null || ((String)isoGridSquare.getModData().rawget((Object)"remove")).equals("false"))) {
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), (Object)new Double(isoGridSquare.getX()));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), (Object)new Double(isoGridSquare.getY()));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), (Object)new Double(isoGridSquare.getZ()));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"typeOfSeed"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), (Object)isoGridSquare.getModData().rawget((Object)"nbOfGrow"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"id"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"waterLvl"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"lastWaterHour"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"waterNeeded"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"waterNeededMax"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"mildewLvl"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"aphidLvl"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"fliesLvl"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"fertilizer"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"nextGrowing"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"hasVegetable"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"hasSeed"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"health"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"badCare"));
            GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)isoGridSquare.getModData().rawget((Object)"id")).intValue()), isoGridSquare.getModData().rawget((Object)"state"));
            if (isoGridSquare.getModData().rawget((Object)"hoursElapsed") != null) {
                GameTime.getInstance().getModData().rawset((Object)"hoursElapsed", isoGridSquare.getModData().rawget((Object)"hoursElapsed"));
            }
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.RelevantTo((float)isoGridSquare.getX(), (float)isoGridSquare.getY())) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.ReceiveModData.doPacket(startPacket);
                startPacket.putInt(isoGridSquare.getX());
                startPacket.putInt(isoGridSquare.getY());
                startPacket.putInt(isoGridSquare.getZ());
                try {
                    isoGridSquare.getModData().save(startPacket.bb);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
                PacketTypes.PacketType.ReceiveModData.send(udpConnection);
            }
        }
    }
    
    static void receiveSendModData(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(int1, int2, int3);
        if (gridSquare == null) {
            return;
        }
        try {
            gridSquare.getModData().load(byteBuffer, 186);
            if (gridSquare.getModData().rawget((Object)"id") != null && (gridSquare.getModData().rawget((Object)"remove") == null || ((String)gridSquare.getModData().rawget((Object)"remove")).equals("false"))) {
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), (Object)new Double(gridSquare.getX()));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), (Object)new Double(gridSquare.getY()));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), (Object)new Double(gridSquare.getZ()));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"typeOfSeed"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), (Object)gridSquare.getModData().rawget((Object)"nbOfGrow"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"id"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"waterLvl"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"lastWaterHour"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"waterNeeded"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"waterNeededMax"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"mildewLvl"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"aphidLvl"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"fliesLvl"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"fertilizer"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"nextGrowing"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"hasVegetable"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"hasSeed"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"health"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"badCare"));
                GameTime.getInstance().getModData().rawset(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, ((Double)gridSquare.getModData().rawget((Object)"id")).intValue()), gridSquare.getModData().rawget((Object)"state"));
                if (gridSquare.getModData().rawget((Object)"hoursElapsed") != null) {
                    GameTime.getInstance().getModData().rawset((Object)"hoursElapsed", gridSquare.getModData().rawget((Object)"hoursElapsed"));
                }
            }
            LuaEventManager.triggerEvent("onLoadModDataFromServer", gridSquare);
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.RelevantTo((float)gridSquare.getX(), (float)gridSquare.getY()) && (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID())) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.ReceiveModData.doPacket(startPacket);
                    startPacket.putInt(int1);
                    startPacket.putInt(int2);
                    startPacket.putInt(int3);
                    try {
                        gridSquare.getModData().save(startPacket.bb);
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    PacketTypes.PacketType.ReceiveModData.send(udpConnection2);
                }
            }
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }
    
    static void receiveWeaponHit(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final IsoObject isoObjectRefFromByteBuffer = getIsoObjectRefFromByteBuffer(byteBuffer);
        final short short1 = byteBuffer.getShort();
        final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short1);
        if (isoObjectRefFromByteBuffer != null && isoPlayer != null) {
            InventoryItem createItem = null;
            if (!readStringUTF.isEmpty()) {
                createItem = InventoryItemFactory.CreateItem(readStringUTF);
                if (createItem == null || !(createItem instanceof HandWeapon)) {
                    return;
                }
            }
            if (createItem == null && !(isoObjectRefFromByteBuffer instanceof IsoWindow)) {
                return;
            }
            final int n2 = (int)isoObjectRefFromByteBuffer.getX();
            final int n3 = (int)isoObjectRefFromByteBuffer.getY();
            final int n4 = (int)isoObjectRefFromByteBuffer.getZ();
            if (isoObjectRefFromByteBuffer instanceof IsoDoor) {
                ((IsoDoor)isoObjectRefFromByteBuffer).WeaponHit(isoPlayer, (HandWeapon)createItem);
            }
            else if (isoObjectRefFromByteBuffer instanceof IsoThumpable) {
                ((IsoThumpable)isoObjectRefFromByteBuffer).WeaponHit(isoPlayer, (HandWeapon)createItem);
            }
            else if (isoObjectRefFromByteBuffer instanceof IsoWindow) {
                ((IsoWindow)isoObjectRefFromByteBuffer).WeaponHit(isoPlayer, (HandWeapon)createItem);
            }
            else if (isoObjectRefFromByteBuffer instanceof IsoBarricade) {
                ((IsoBarricade)isoObjectRefFromByteBuffer).WeaponHit(isoPlayer, (HandWeapon)createItem);
            }
            if (isoObjectRefFromByteBuffer.getObjectIndex() == -1) {
                LoggerManager.getLogger("map").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;III)Ljava/lang/String;, udpConnection.idStr, udpConnection.username, (isoObjectRefFromByteBuffer.getName() != null) ? isoObjectRefFromByteBuffer.getName() : isoObjectRefFromByteBuffer.getObjectName(), readStringUTF.isEmpty() ? "BareHands" : readStringUTF, n2, n3, n4));
            }
        }
    }
    
    private static void putIsoObjectRefToByteBuffer(final IsoObject isoObject, final ByteBuffer byteBuffer) {
        byteBuffer.putInt(isoObject.square.x);
        byteBuffer.putInt(isoObject.square.y);
        byteBuffer.putInt(isoObject.square.z);
        byteBuffer.put((byte)isoObject.square.getObjects().indexOf(isoObject));
    }
    
    private static IsoObject getIsoObjectRefFromByteBuffer(final ByteBuffer byteBuffer) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final byte value = byteBuffer.get();
        final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(int1, int2, int3);
        if (gridSquare != null && value >= 0 && value < gridSquare.getObjects().size()) {
            return gridSquare.getObjects().get(value);
        }
        return null;
    }
    
    static void receiveDrink(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final byte value = byteBuffer.get();
        final float float1 = byteBuffer.getFloat();
        final IsoPlayer playerFromConnection = getPlayerFromConnection(udpConnection, value);
        if (playerFromConnection != null) {
            final Stats stats = playerFromConnection.getStats();
            stats.thirst -= float1;
            if (playerFromConnection.getStats().thirst < 0.0f) {
                playerFromConnection.getStats().thirst = 0.0f;
            }
        }
    }
    
    private static void process(final ZomboidNetData zomboidNetData) {
        final ByteBuffer buffer = zomboidNetData.buffer;
        GameServer.udpEngine.getActiveConnection(zomboidNetData.connection);
        try {
            final short type = zomboidNetData.type;
            doZomboidDataInMainLoop(zomboidNetData);
        }
        catch (Exception ex) {
            DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, zomboidNetData.type));
            ex.printStackTrace();
        }
    }
    
    static void receiveEatFood(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final byte value = byteBuffer.get();
        final float float1 = byteBuffer.getFloat();
        InventoryItem loadItem = null;
        try {
            loadItem = InventoryItem.loadItem(byteBuffer, 186);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        if (loadItem instanceof Food) {
            final IsoPlayer playerFromConnection = getPlayerFromConnection(udpConnection, value);
            if (playerFromConnection != null) {
                playerFromConnection.Eat(loadItem, float1);
            }
        }
    }
    
    static void receivePingFromClient(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.PingFromClient.doPacket(startPacket);
        try {
            startPacket.putLong(byteBuffer.getLong());
            MPStatistics.write(udpConnection, startPacket.bb);
            PacketTypes.PacketType.PingFromClient.send(udpConnection);
            MPStatistics.requested();
        }
        catch (Exception ex) {
            udpConnection.cancelPacket();
        }
    }
    
    static void receiveBandage(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short1);
        if (isoPlayer != null) {
            final int int1 = byteBuffer.getInt();
            final boolean b = byteBuffer.get() == 1;
            final float float1 = byteBuffer.getFloat();
            final boolean b2 = byteBuffer.get() == 1;
            final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
            isoPlayer.getBodyDamage().SetBandaged(int1, b, float1, b2, readStringUTF);
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.Bandage.doPacket(startPacket);
                    startPacket.putShort(short1);
                    startPacket.putInt(int1);
                    startPacket.putBoolean(b);
                    startPacket.putFloat(float1);
                    startPacket.putBoolean(b2);
                    GameWindow.WriteStringUTF(startPacket.bb, readStringUTF);
                    PacketTypes.PacketType.Bandage.send(udpConnection2);
                }
            }
        }
    }
    
    static void receiveStitch(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short1);
        if (isoPlayer != null) {
            final int int1 = byteBuffer.getInt();
            final boolean stitched = byteBuffer.get() == 1;
            final float float1 = byteBuffer.getFloat();
            isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setStitched(stitched);
            isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setStitchTime(float1);
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.Stitch.doPacket(startPacket);
                    startPacket.putShort(short1);
                    startPacket.putInt(int1);
                    startPacket.putBoolean(stitched);
                    startPacket.putFloat(float1);
                    PacketTypes.PacketType.Stitch.send(udpConnection2);
                }
            }
        }
    }
    
    static void receiveWoundInfection(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short1);
        if (isoPlayer != null) {
            final int int1 = byteBuffer.getInt();
            final boolean infectedWound = byteBuffer.get() == 1;
            isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setInfectedWound(infectedWound);
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.WoundInfection.doPacket(startPacket);
                    startPacket.putShort(short1);
                    startPacket.putInt(int1);
                    startPacket.putBoolean(infectedWound);
                    PacketTypes.PacketType.WoundInfection.send(udpConnection2);
                }
            }
        }
    }
    
    static void receiveDisinfect(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short1);
        if (isoPlayer != null) {
            final int int1 = byteBuffer.getInt();
            final float float1 = byteBuffer.getFloat();
            final BodyPart bodyPart = isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
            bodyPart.setAlcoholLevel(bodyPart.getAlcoholLevel() + float1);
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.Disinfect.doPacket(startPacket);
                    startPacket.putShort(short1);
                    startPacket.putInt(int1);
                    startPacket.putFloat(float1);
                    PacketTypes.PacketType.Disinfect.send(udpConnection2);
                }
            }
        }
    }
    
    static void receiveSplint(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short1);
        if (isoPlayer != null) {
            final int int1 = byteBuffer.getInt();
            final boolean b = byteBuffer.get() == 1;
            final String splintItem = b ? GameWindow.ReadStringUTF(byteBuffer) : null;
            final float n2 = b ? byteBuffer.getFloat() : 0.0f;
            final BodyPart bodyPart = isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
            bodyPart.setSplint(b, n2);
            bodyPart.setSplintItem(splintItem);
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.Splint.doPacket(startPacket);
                    startPacket.putShort(short1);
                    startPacket.putInt(int1);
                    startPacket.putBoolean(b);
                    if (b) {
                        startPacket.putUTF(splintItem);
                        startPacket.putFloat(n2);
                    }
                    PacketTypes.PacketType.Splint.send(udpConnection2);
                }
            }
        }
    }
    
    static void receiveAdditionalPain(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short1);
        if (isoPlayer != null) {
            final int int1 = byteBuffer.getInt();
            final float float1 = byteBuffer.getFloat();
            final BodyPart bodyPart = isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
            bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + float1);
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.AdditionalPain.doPacket(startPacket);
                    startPacket.putShort(short1);
                    startPacket.putInt(int1);
                    startPacket.putFloat(float1);
                    PacketTypes.PacketType.AdditionalPain.send(udpConnection2);
                }
            }
        }
    }
    
    static void receiveRemoveGlass(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short1);
        if (isoPlayer != null) {
            final int int1 = byteBuffer.getInt();
            isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setHaveGlass(false);
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.RemoveGlass.doPacket(startPacket);
                    startPacket.putShort(short1);
                    startPacket.putInt(int1);
                    PacketTypes.PacketType.RemoveGlass.send(udpConnection2);
                }
            }
        }
    }
    
    static void receiveRemoveBullet(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short1);
        if (isoPlayer != null) {
            final int int1 = byteBuffer.getInt();
            final int int2 = byteBuffer.getInt();
            isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setHaveBullet(false, int2);
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.RemoveBullet.doPacket(startPacket);
                    startPacket.putShort(short1);
                    startPacket.putInt(int1);
                    startPacket.putInt(int2);
                    PacketTypes.PacketType.RemoveBullet.send(udpConnection2);
                }
            }
        }
    }
    
    static void receiveCleanBurn(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short1);
        if (isoPlayer != null) {
            final int int1 = byteBuffer.getInt();
            isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setNeedBurnWash(false);
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.CleanBurn.doPacket(startPacket);
                    startPacket.putShort(short1);
                    startPacket.putInt(int1);
                    PacketTypes.PacketType.CleanBurn.send(udpConnection2);
                }
            }
        }
    }
    
    static void receiveBodyDamageUpdate(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        BodyDamageSync.instance.serverPacket(byteBuffer);
    }
    
    static void receiveReceiveCommand(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final String readString = GameWindow.ReadString(byteBuffer);
        String s = handleClientCommand(readString.substring(1), udpConnection);
        if (s == null) {
            s = handleServerCommand(readString.substring(1), udpConnection);
        }
        if (s == null) {
            s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readString);
        }
        if (readString.substring(1).startsWith("roll") || readString.substring(1).startsWith("card")) {
            ChatServer.getInstance().sendMessageToServerChat(udpConnection, s);
        }
        else {
            ChatServer.getInstance().sendMessageToServerChat(udpConnection, s);
        }
    }
    
    private static String handleClientCommand(final String input, final UdpConnection udpConnection) {
        if (input == null) {
            return null;
        }
        final ArrayList<String> list = new ArrayList<String>();
        final Matcher matcher = Pattern.compile("([^\"]\\S*|\".*?\")\\s*").matcher(input);
        while (matcher.find()) {
            list.add(matcher.group(1).replace("\"", ""));
        }
        final int size = list.size();
        final String[] array = list.toArray(new String[size]);
        final String s = (size > 0) ? array[0].toLowerCase() : "";
        if (s.equals("card")) {
            PlayWorldSoundServer("ChatDrawCard", false, getAnyPlayerFromConnection(udpConnection).getCurrentSquare(), 0.0f, 3.0f, 1.0f, false);
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, udpConnection.username, ServerOptions.getRandomCard());
        }
        if (s.equals("roll")) {
            if (size != 2) {
                return ServerOptions.clientOptionsList.get("roll");
            }
            try {
                final int int1 = Integer.parseInt(array[1]);
                PlayWorldSoundServer("ChatRollDice", false, getAnyPlayerFromConnection(udpConnection).getCurrentSquare(), 0.0f, 3.0f, 1.0f, false);
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, udpConnection.username, int1, Rand.Next(int1));
            }
            catch (Exception ex2) {
                return ServerOptions.clientOptionsList.get("roll");
            }
        }
        if (s.equals("changepwd")) {
            if (size == 3) {
                final String s2 = array[1];
                final String s3 = array[2];
                try {
                    return ServerWorldDatabase.instance.changePwd(udpConnection.username, s2.trim(), s3.trim());
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                    return "A SQL error occured";
                }
            }
            return ServerOptions.clientOptionsList.get("changepwd");
        }
        if (s.equals("dragons")) {
            return "Sorry, you don't have the required materials.";
        }
        if (s.equals("dance")) {
            return "Stop kidding me...";
        }
        if (!s.equals("safehouse")) {
            return null;
        }
        if (size != 2 || udpConnection == null) {
            return ServerOptions.clientOptionsList.get("safehouse");
        }
        if (!ServerOptions.instance.PlayerSafehouse.getValue() && !ServerOptions.instance.AdminSafehouse.getValue()) {
            return "Safehouses are disabled on this server.";
        }
        if (!"release".equals(array[1])) {
            return ServerOptions.clientOptionsList.get("safehouse");
        }
        final SafeHouse hasSafehouse = SafeHouse.hasSafehouse(udpConnection.username);
        if (hasSafehouse == null) {
            return "You don't own a safehouse.";
        }
        if (!ServerOptions.instance.PlayerSafehouse.getValue() && !"admin".equals(udpConnection.accessLevel) && !"moderator".equals(udpConnection.accessLevel)) {
            return "Only admin or moderator may release safehouses";
        }
        hasSafehouse.removeSafeHouse(null);
        return "Safehouse released";
    }
    
    public static void doZomboidDataInMainLoop(final ZomboidNetData e) {
        synchronized (GameServer.MainLoopNetDataHighPriorityQ) {
            GameServer.MainLoopNetDataHighPriorityQ.add(e);
        }
    }
    
    static void receiveEquip(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final byte value = byteBuffer.get();
        final byte value2 = byteBuffer.get();
        final byte value3 = byteBuffer.get();
        InventoryItem inventoryItem = null;
        final IsoPlayer playerFromConnection = getPlayerFromConnection(udpConnection, value);
        if (value3 == 1) {
            try {
                inventoryItem = InventoryItem.loadItem(byteBuffer, 186);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            if (inventoryItem == null) {
                LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, udpConnection.idStr));
                return;
            }
        }
        if (playerFromConnection != null) {
            if (inventoryItem != null) {
                inventoryItem.setContainer(playerFromConnection.getInventory());
            }
            if (value2 == 0) {
                playerFromConnection.setPrimaryHandItem(inventoryItem);
            }
            else {
                if (value3 == 2) {
                    inventoryItem = playerFromConnection.getPrimaryHandItem();
                }
                playerFromConnection.setSecondaryHandItem(inventoryItem);
            }
            try {
                if (value3 == 1 && inventoryItem != null && byteBuffer.get() == 1) {
                    inventoryItem.getVisual().load(byteBuffer, 186);
                }
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
        if (playerFromConnection == null) {
            return;
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && getAnyPlayerFromConnection(udpConnection2) != null) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.Equip.doPacket(startPacket);
                startPacket.putShort(playerFromConnection.OnlineID);
                startPacket.putByte(value2);
                startPacket.putByte(value3);
                if (value3 == 1) {
                    try {
                        inventoryItem.saveWithSize(startPacket.bb, false);
                        if (inventoryItem.getVisual() != null) {
                            startPacket.bb.put((byte)1);
                            inventoryItem.getVisual().save(startPacket.bb);
                        }
                        else {
                            startPacket.bb.put((byte)0);
                        }
                    }
                    catch (IOException ex3) {
                        ex3.printStackTrace();
                    }
                }
                PacketTypes.PacketType.Equip.send(udpConnection2);
            }
        }
    }
    
    static void receivePlayerConnect(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        receivePlayerConnect(byteBuffer, udpConnection, udpConnection.username);
        sendInitialWorldState(udpConnection);
    }
    
    static void receiveScoreboardUpdate(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.ScoreboardUpdate.doPacket(startPacket);
        final ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> list2 = new ArrayList<String>();
        final ArrayList<Long> list3 = new ArrayList<Long>();
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            for (int j = 0; j < 4; ++j) {
                if (udpConnection2.usernames[j] != null) {
                    list.add(udpConnection2.usernames[j]);
                    final IsoPlayer playerByRealUserName = getPlayerByRealUserName(udpConnection2.usernames[j]);
                    if (playerByRealUserName != null) {
                        list2.add(playerByRealUserName.getDisplayName());
                    }
                    else {
                        final String displayName = ServerWorldDatabase.instance.getDisplayName(udpConnection2.usernames[j]);
                        list2.add((displayName == null) ? udpConnection2.usernames[j] : displayName);
                    }
                    if (SteamUtils.isSteamModeEnabled()) {
                        list3.add(udpConnection2.steamID);
                    }
                }
            }
        }
        startPacket.putInt(list.size());
        for (int k = 0; k < list.size(); ++k) {
            startPacket.putUTF(list.get(k));
            startPacket.putUTF(list2.get(k));
            if (SteamUtils.isSteamModeEnabled()) {
                startPacket.putLong(list3.get(k));
            }
        }
        PacketTypes.PacketType.ScoreboardUpdate.send(udpConnection);
    }
    
    static void receiveStopSound(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final StopSoundPacket stopSoundPacket = new StopSoundPacket();
        stopSoundPacket.parse(byteBuffer);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                if (udpConnection2.isFullyConnected()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.StopSound.doPacket(startPacket);
                    stopSoundPacket.write(startPacket);
                    PacketTypes.PacketType.StopSound.send(udpConnection2);
                }
            }
        }
    }
    
    static void receivePlaySound(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final PlaySoundPacket playSoundPacket = new PlaySoundPacket();
        playSoundPacket.parse(byteBuffer);
        final IsoMovingObject movingObject = playSoundPacket.getMovingObject();
        if (!playSoundPacket.isConsistent()) {
            return;
        }
        int max = 70;
        final GameSound sound = GameSounds.getSound(playSoundPacket.getName());
        if (sound != null) {
            for (int i = 0; i < sound.clips.size(); ++i) {
                final GameSoundClip gameSoundClip = sound.clips.get(i);
                if (gameSoundClip.hasMaxDistance()) {
                    max = Math.max(max, (int)gameSoundClip.distanceMax);
                }
            }
        }
        for (int j = 0; j < GameServer.udpEngine.connections.size(); ++j) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(j);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                if (udpConnection2.isFullyConnected()) {
                    if (getAnyPlayerFromConnection(udpConnection2) != null && (movingObject == null || udpConnection2.RelevantTo(movingObject.getX(), movingObject.getY(), (float)max))) {
                        final ByteBufferWriter startPacket = udpConnection2.startPacket();
                        PacketTypes.PacketType.PlaySound.doPacket(startPacket);
                        playSoundPacket.write(startPacket);
                        PacketTypes.PacketType.PlaySound.send(udpConnection2);
                    }
                }
            }
        }
    }
    
    static void receivePlayWorldSound(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final PlayWorldSoundPacket playWorldSoundPacket = new PlayWorldSoundPacket();
        playWorldSoundPacket.parse(byteBuffer);
        if (!playWorldSoundPacket.isConsistent()) {
            return;
        }
        int max = 70;
        final GameSound sound = GameSounds.getSound(playWorldSoundPacket.getName());
        if (sound != null) {
            for (int i = 0; i < sound.clips.size(); ++i) {
                final GameSoundClip gameSoundClip = sound.clips.get(i);
                if (gameSoundClip.hasMaxDistance()) {
                    max = Math.max(max, (int)gameSoundClip.distanceMax);
                }
            }
        }
        for (int j = 0; j < GameServer.udpEngine.connections.size(); ++j) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(j);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                if (udpConnection2.isFullyConnected()) {
                    if (getAnyPlayerFromConnection(udpConnection2) != null && udpConnection2.RelevantTo((float)playWorldSoundPacket.getX(), (float)playWorldSoundPacket.getY(), (float)max)) {
                        final ByteBufferWriter startPacket = udpConnection2.startPacket();
                        PacketTypes.PacketType.PlayWorldSound.doPacket(startPacket);
                        playWorldSoundPacket.write(startPacket);
                        PacketTypes.PacketType.PlayWorldSound.send(udpConnection2);
                    }
                }
            }
        }
    }
    
    private static void PlayWorldSound(final String s, final IsoGridSquare isoGridSquare, final float n) {
        if (!GameServer.bServer || isoGridSquare == null) {
            return;
        }
        final int x = isoGridSquare.getX();
        final int y = isoGridSquare.getY();
        final int z = isoGridSquare.getZ();
        final PlayWorldSoundPacket playWorldSoundPacket = new PlayWorldSoundPacket();
        playWorldSoundPacket.set(s, x, y, (byte)z);
        DebugLog.log(DebugType.Sound, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;F)Ljava/lang/String;, playWorldSoundPacket.getDescription(), n));
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (getAnyPlayerFromConnection(udpConnection) != null && udpConnection.RelevantTo((float)x, (float)y, n * 2.0f)) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.PlayWorldSound.doPacket(startPacket);
                playWorldSoundPacket.write(startPacket);
                PacketTypes.PacketType.PlayWorldSound.send(udpConnection);
            }
        }
    }
    
    public static void PlayWorldSoundServer(final String s, final boolean b, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b2) {
        PlayWorldSound(s, isoGridSquare, n2);
    }
    
    public static void PlayWorldSoundWavServer(final String s, final boolean b, final IsoGridSquare isoGridSquare, final float n, final float n2, final float n3, final boolean b2) {
        PlayWorldSound(s, isoGridSquare, n2);
    }
    
    public static void PlaySoundAtEveryPlayer(final String s, final int n, final int n2, final int n3) {
        PlaySoundAtEveryPlayer(s, n, n2, n3, false);
    }
    
    public static void PlaySoundAtEveryPlayer(final String s) {
        PlaySoundAtEveryPlayer(s, -1, -1, -1, true);
    }
    
    public static void PlaySoundAtEveryPlayer(final String s, int n, int n2, int n3, final boolean b) {
        if (!GameServer.bServer) {
            return;
        }
        if (b) {
            DebugLog.log(DebugType.Sound, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        else {
            DebugLog.log(DebugType.Sound, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, s, n, n2));
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            final IsoPlayer anyPlayerFromConnection = getAnyPlayerFromConnection(udpConnection);
            if (anyPlayerFromConnection != null && !anyPlayerFromConnection.isDeaf()) {
                if (b) {
                    n = (int)anyPlayerFromConnection.getX();
                    n2 = (int)anyPlayerFromConnection.getY();
                    n3 = (int)anyPlayerFromConnection.getZ();
                }
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.PlaySoundEveryPlayer.doPacket(startPacket);
                startPacket.putUTF(s);
                startPacket.putInt(n);
                startPacket.putInt(n2);
                startPacket.putInt(n3);
                PacketTypes.PacketType.PlaySoundEveryPlayer.send(udpConnection);
            }
        }
    }
    
    public static void sendZombieSound(final IsoZombie.ZombieSound zombieSound, final IsoZombie isoZombie) {
        final float n = (float)zombieSound.radius();
        DebugLog.log(DebugType.Sound, invokedynamic(makeConcatWithConstants:(Lzombie/characters/IsoZombie$ZombieSound;)Ljava/lang/String;, zombieSound));
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.isFullyConnected()) {
                if (udpConnection.RelevantTo(isoZombie.getX(), isoZombie.getY(), n)) {
                    final ByteBufferWriter startPacket = udpConnection.startPacket();
                    PacketTypes.PacketType.ZombieSound.doPacket(startPacket);
                    startPacket.putShort(isoZombie.OnlineID);
                    startPacket.putByte((byte)zombieSound.ordinal());
                    PacketTypes.PacketType.ZombieSound.send(udpConnection);
                }
            }
        }
    }
    
    static void receiveZombieHelmetFalling(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final byte value = byteBuffer.get();
        final short short1 = byteBuffer.getShort();
        final String readString = GameWindow.ReadString(byteBuffer);
        final IsoZombie value2 = ServerMap.instance.ZombieMap.get(short1);
        if (getPlayerFromConnection(udpConnection, value) == null || value2 == null) {
            return;
        }
        value2.serverRemoveItemFromZombie(readString);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && getAnyPlayerFromConnection(udpConnection) != null) {
                try {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.ZombieHelmetFalling.doPacket(startPacket);
                    startPacket.putShort(short1);
                    startPacket.putUTF(readString);
                    PacketTypes.PacketType.ZombieHelmetFalling.send(udpConnection2);
                }
                catch (Throwable t) {
                    udpConnection.cancelPacket();
                    ExceptionLogger.logException(t);
                }
            }
        }
    }
    
    static void receivePlayerAttachedItem(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final byte value = byteBuffer.get();
        final String readString = GameWindow.ReadString(byteBuffer);
        final int n2 = (byteBuffer.get() == 1) ? 1 : 0;
        InventoryItem createItem = null;
        if (n2 != 0) {
            createItem = InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer));
            if (createItem == null) {
                return;
            }
        }
        final IsoPlayer playerFromConnection = getPlayerFromConnection(udpConnection, value);
        if (playerFromConnection == null) {
            return;
        }
        playerFromConnection.setAttachedItem(readString, createItem);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && getAnyPlayerFromConnection(udpConnection) != null) {
                try {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.PlayerAttachedItem.doPacket(startPacket);
                    startPacket.putShort(playerFromConnection.OnlineID);
                    GameWindow.WriteString(startPacket.bb, readString);
                    startPacket.putByte((byte)n2);
                    if (n2 != 0) {
                        GameWindow.WriteString(startPacket.bb, createItem.getFullType());
                    }
                    PacketTypes.PacketType.PlayerAttachedItem.send(udpConnection2);
                }
                catch (Throwable t) {
                    udpConnection.cancelPacket();
                    ExceptionLogger.logException(t);
                }
            }
        }
    }
    
    static void receiveSyncClothing(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final SyncClothingPacket syncClothingPacket = new SyncClothingPacket();
        syncClothingPacket.parse(byteBuffer);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && getAnyPlayerFromConnection(udpConnection) != null) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.SyncClothing.doPacket(startPacket);
                syncClothingPacket.write(startPacket);
                PacketTypes.PacketType.SyncClothing.send(udpConnection2);
            }
        }
    }
    
    static void receiveHumanVisual(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final IsoPlayer playerFromConnection = getPlayerFromConnection(udpConnection, byteBuffer.get());
        if (playerFromConnection == null) {
            return;
        }
        try {
            playerFromConnection.getHumanVisual().load(byteBuffer, 186);
        }
        catch (Throwable t) {
            ExceptionLogger.logException(t);
            return;
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && getAnyPlayerFromConnection(udpConnection) != null) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.HumanVisual.doPacket(startPacket);
                try {
                    startPacket.putShort(playerFromConnection.OnlineID);
                    playerFromConnection.getHumanVisual().save(startPacket.bb);
                    PacketTypes.PacketType.HumanVisual.send(udpConnection2);
                }
                catch (Throwable t2) {
                    udpConnection2.cancelPacket();
                    ExceptionLogger.logException(t2);
                }
            }
        }
    }
    
    public static void initClientCommandFilter() {
        final String value = ServerOptions.getInstance().ClientCommandFilter.getValue();
        GameServer.ccFilters.clear();
        for (final String s : value.split(";")) {
            if (!s.isEmpty()) {
                if (s.contains(".")) {
                    if (s.startsWith("+") || s.startsWith("-")) {
                        final String[] split2 = s.split("\\.");
                        if (split2.length == 2) {
                            final String substring = split2[0].substring(1);
                            final String command = split2[1];
                            final CCFilter value2 = new CCFilter();
                            value2.command = command;
                            value2.allow = split2[0].startsWith("+");
                            value2.next = GameServer.ccFilters.get(substring);
                            GameServer.ccFilters.put(substring, value2);
                        }
                    }
                }
            }
        }
    }
    
    static void receiveClientCommand(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final byte value = byteBuffer.get();
        final String readString = GameWindow.ReadString(byteBuffer);
        final String readString2 = GameWindow.ReadString(byteBuffer);
        final boolean b = byteBuffer.get() == 1;
        KahluaTable table = null;
        if (b) {
            table = LuaManager.platform.newTable();
            try {
                TableNetworkUtils.load(table, byteBuffer);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }
        IsoPlayer isoPlayer = getPlayerFromConnection(udpConnection, value);
        if (value == -1) {
            isoPlayer = getAnyPlayerFromConnection(udpConnection);
        }
        if (isoPlayer == null) {
            DebugLog.log("receiveClientCommand: player is null");
            return;
        }
        final CCFilter ccFilter = GameServer.ccFilters.get(readString);
        if (ccFilter == null || ccFilter.passes(readString2)) {
            LoggerManager.getLogger("cmd").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;III)Ljava/lang/String;, udpConnection.idStr, isoPlayer.username, readString, readString2, (int)isoPlayer.getX(), (int)isoPlayer.getY(), (int)isoPlayer.getZ()));
        }
        LuaEventManager.triggerEvent("OnClientCommand", readString, readString2, isoPlayer, table);
    }
    
    static void receiveGlobalObjects(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final byte value = byteBuffer.get();
        IsoPlayer isoPlayer = getPlayerFromConnection(udpConnection, value);
        if (value == -1) {
            isoPlayer = getAnyPlayerFromConnection(udpConnection);
        }
        if (isoPlayer == null) {
            DebugLog.log("receiveGlobalObjects: player is null");
            return;
        }
        SGlobalObjectNetwork.receive(byteBuffer, isoPlayer);
    }
    
    public static IsoPlayer getAnyPlayerFromConnection(final UdpConnection udpConnection) {
        for (int i = 0; i < 4; ++i) {
            if (udpConnection.players[i] != null) {
                return udpConnection.players[i];
            }
        }
        return null;
    }
    
    private static IsoPlayer getPlayerFromConnection(final UdpConnection udpConnection, final int n) {
        if (n >= 0 && n < 4) {
            return udpConnection.players[n];
        }
        return null;
    }
    
    public static IsoPlayer getPlayerByRealUserName(final String anObject) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            for (int j = 0; j < 4; ++j) {
                final IsoPlayer isoPlayer = udpConnection.players[j];
                if (isoPlayer != null && isoPlayer.username.equals(anObject)) {
                    return isoPlayer;
                }
            }
        }
        return null;
    }
    
    public static IsoPlayer getPlayerByUserName(final String s) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            for (int j = 0; j < 4; ++j) {
                final IsoPlayer isoPlayer = udpConnection.players[j];
                if (isoPlayer != null && (isoPlayer.getDisplayName().equals(s) || isoPlayer.getUsername().equals(s))) {
                    return isoPlayer;
                }
            }
        }
        return null;
    }
    
    public static IsoPlayer getPlayerByUserNameForCommand(final String s) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            for (int j = 0; j < 4; ++j) {
                final IsoPlayer isoPlayer = udpConnection.players[j];
                if (isoPlayer != null && (isoPlayer.getDisplayName().toLowerCase().equals(s.toLowerCase()) || isoPlayer.getDisplayName().toLowerCase().startsWith(s.toLowerCase()))) {
                    return isoPlayer;
                }
            }
        }
        return null;
    }
    
    public static UdpConnection getConnectionByPlayerOnlineID(final short s) {
        return GameServer.udpEngine.getActiveConnection(GameServer.IDToAddressMap.get(s));
    }
    
    public static UdpConnection getConnectionFromPlayer(final IsoPlayer key) {
        final Long n = GameServer.PlayerToAddressMap.get(key);
        if (n == null) {
            return null;
        }
        return GameServer.udpEngine.getActiveConnection(n);
    }
    
    static void receiveRemoveBlood(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final boolean b = byteBuffer.get() == 1;
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
        if (gridSquare == null) {
            return;
        }
        gridSquare.removeBlood(false, b);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2 != udpConnection && udpConnection2.RelevantTo((float)int1, (float)int2)) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.RemoveBlood.doPacket(startPacket);
                startPacket.putInt(int1);
                startPacket.putInt(int2);
                startPacket.putInt(int3);
                startPacket.putBoolean(b);
                PacketTypes.PacketType.RemoveBlood.send(udpConnection2);
            }
        }
    }
    
    public static void sendAddItemToContainer(final ItemContainer itemContainer, final InventoryItem inventoryItem) {
        IsoObject isoObject = itemContainer.getParent();
        if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
            isoObject = itemContainer.getContainingItem().getWorldItem();
        }
        final IsoGridSquare square = isoObject.getSquare();
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.RelevantTo((float)square.x, (float)square.y)) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(startPacket);
                if (isoObject instanceof IsoDeadBody) {
                    startPacket.putShort((short)0);
                    startPacket.putInt(isoObject.square.getX());
                    startPacket.putInt(isoObject.square.getY());
                    startPacket.putInt(isoObject.square.getZ());
                    startPacket.putByte((byte)isoObject.getStaticMovingObjectIndex());
                }
                else if (isoObject instanceof IsoWorldInventoryObject) {
                    startPacket.putShort((short)1);
                    startPacket.putInt(isoObject.square.getX());
                    startPacket.putInt(isoObject.square.getY());
                    startPacket.putInt(isoObject.square.getZ());
                    startPacket.putInt(((IsoWorldInventoryObject)isoObject).getItem().id);
                }
                else if (isoObject instanceof BaseVehicle) {
                    startPacket.putShort((short)3);
                    startPacket.putInt(isoObject.square.getX());
                    startPacket.putInt(isoObject.square.getY());
                    startPacket.putInt(isoObject.square.getZ());
                    startPacket.putShort(((BaseVehicle)isoObject).VehicleID);
                    startPacket.putByte((byte)itemContainer.vehiclePart.getIndex());
                }
                else {
                    startPacket.putShort((short)2);
                    startPacket.putInt(isoObject.square.getX());
                    startPacket.putInt(isoObject.square.getY());
                    startPacket.putInt(isoObject.square.getZ());
                    startPacket.putByte((byte)isoObject.square.getObjects().indexOf(isoObject));
                    startPacket.putByte((byte)isoObject.getContainerIndex(itemContainer));
                }
                try {
                    CompressIdenticalItems.save(startPacket.bb, inventoryItem);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                PacketTypes.PacketType.AddInventoryItemToContainer.send(udpConnection);
            }
        }
    }
    
    public static void sendRemoveItemFromContainer(final ItemContainer itemContainer, final InventoryItem inventoryItem) {
        IsoObject isoObject = itemContainer.getParent();
        if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
            isoObject = itemContainer.getContainingItem().getWorldItem();
        }
        if (isoObject == null) {
            DebugLog.log("sendRemoveItemFromContainer: o is null");
            return;
        }
        final IsoGridSquare square = isoObject.getSquare();
        if (square == null) {
            DebugLog.log("sendRemoveItemFromContainer: square is null");
            return;
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.RelevantTo((float)square.x, (float)square.y)) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.RemoveInventoryItemFromContainer.doPacket(startPacket);
                if (isoObject instanceof IsoDeadBody) {
                    startPacket.putShort((short)0);
                    startPacket.putInt(isoObject.square.getX());
                    startPacket.putInt(isoObject.square.getY());
                    startPacket.putInt(isoObject.square.getZ());
                    startPacket.putByte((byte)isoObject.getStaticMovingObjectIndex());
                    startPacket.putInt(1);
                    startPacket.putInt(inventoryItem.id);
                }
                else if (isoObject instanceof IsoWorldInventoryObject) {
                    startPacket.putShort((short)1);
                    startPacket.putInt(isoObject.square.getX());
                    startPacket.putInt(isoObject.square.getY());
                    startPacket.putInt(isoObject.square.getZ());
                    startPacket.putInt(((IsoWorldInventoryObject)isoObject).getItem().id);
                    startPacket.putInt(1);
                    startPacket.putInt(inventoryItem.id);
                }
                else if (isoObject instanceof BaseVehicle) {
                    startPacket.putShort((short)3);
                    startPacket.putInt(isoObject.square.getX());
                    startPacket.putInt(isoObject.square.getY());
                    startPacket.putInt(isoObject.square.getZ());
                    startPacket.putShort(((BaseVehicle)isoObject).VehicleID);
                    startPacket.putByte((byte)itemContainer.vehiclePart.getIndex());
                    startPacket.putInt(1);
                    startPacket.putInt(inventoryItem.id);
                }
                else {
                    startPacket.putShort((short)2);
                    startPacket.putInt(isoObject.square.getX());
                    startPacket.putInt(isoObject.square.getY());
                    startPacket.putInt(isoObject.square.getZ());
                    startPacket.putByte((byte)isoObject.square.getObjects().indexOf(isoObject));
                    startPacket.putByte((byte)isoObject.getContainerIndex(itemContainer));
                    startPacket.putInt(1);
                    startPacket.putInt(inventoryItem.id);
                }
                PacketTypes.PacketType.RemoveInventoryItemFromContainer.send(udpConnection);
            }
        }
    }
    
    static void receiveRemoveInventoryItemFromContainer(final ByteBuffer src, final UdpConnection udpConnection, final short n) {
        GameServer.alreadyRemoved.clear();
        final ByteBufferReader byteBufferReader = new ByteBufferReader(src);
        final short short1 = byteBufferReader.getShort();
        final int int1 = byteBufferReader.getInt();
        final int int2 = byteBufferReader.getInt();
        final int int3 = byteBufferReader.getInt();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
        if (isoGridSquare == null) {
            isoGridSquare = ServerMap.instance.getGridSquare(int1, int2, int3);
        }
        final HashSet<String> set = new HashSet<String>();
        int n2 = 0;
        if (short1 == 0) {
            final byte byte1 = byteBufferReader.getByte();
            n2 = src.getInt();
            if (isoGridSquare != null && byte1 >= 0 && byte1 < isoGridSquare.getStaticMovingObjects().size()) {
                final IsoMovingObject isoMovingObject = isoGridSquare.getStaticMovingObjects().get(byte1);
                if (isoMovingObject != null && isoMovingObject.getContainer() != null) {
                    for (int i = 0; i < n2; ++i) {
                        final int int4 = byteBufferReader.getInt();
                        final InventoryItem itemWithID = isoMovingObject.getContainer().getItemWithID(int4);
                        if (itemWithID == null) {
                            GameServer.alreadyRemoved.add(int4);
                        }
                        else {
                            isoMovingObject.getContainer().Remove(itemWithID);
                            set.add(itemWithID.getFullType());
                        }
                    }
                    isoMovingObject.getContainer().setExplored(true);
                    isoMovingObject.getContainer().setHasBeenLooted(true);
                }
            }
        }
        else if (short1 == 1) {
            if (isoGridSquare != null) {
                final long long1 = byteBufferReader.getLong();
                n2 = src.getInt();
                ItemContainer inventory = null;
                for (int j = 0; j < isoGridSquare.getWorldObjects().size(); ++j) {
                    final IsoWorldInventoryObject isoWorldInventoryObject = isoGridSquare.getWorldObjects().get(j);
                    if (isoWorldInventoryObject != null && isoWorldInventoryObject.getItem() instanceof InventoryContainer && isoWorldInventoryObject.getItem().id == long1) {
                        inventory = ((InventoryContainer)isoWorldInventoryObject.getItem()).getInventory();
                        break;
                    }
                }
                if (inventory != null) {
                    for (int k = 0; k < n2; ++k) {
                        final int int5 = byteBufferReader.getInt();
                        final InventoryItem itemWithID2 = inventory.getItemWithID(int5);
                        if (itemWithID2 == null) {
                            GameServer.alreadyRemoved.add(int5);
                        }
                        else {
                            inventory.Remove(itemWithID2);
                            set.add(itemWithID2.getFullType());
                        }
                    }
                    inventory.setExplored(true);
                    inventory.setHasBeenLooted(true);
                }
            }
        }
        else if (short1 == 2) {
            final byte byte2 = byteBufferReader.getByte();
            final byte byte3 = byteBufferReader.getByte();
            n2 = src.getInt();
            if (isoGridSquare != null && byte2 >= 0 && byte2 < isoGridSquare.getObjects().size()) {
                final IsoObject isoObject = isoGridSquare.getObjects().get(byte2);
                final ItemContainer itemContainer = (isoObject != null) ? isoObject.getContainerByIndex(byte3) : null;
                if (itemContainer != null) {
                    for (int l = 0; l < n2; ++l) {
                        final int int6 = byteBufferReader.getInt();
                        final InventoryItem itemWithID3 = itemContainer.getItemWithID(int6);
                        if (itemWithID3 == null) {
                            GameServer.alreadyRemoved.add(int6);
                        }
                        else {
                            itemContainer.Remove(itemWithID3);
                            itemContainer.setExplored(true);
                            itemContainer.setHasBeenLooted(true);
                            set.add(itemWithID3.getFullType());
                        }
                    }
                    LuaManager.updateOverlaySprite(isoObject);
                }
            }
        }
        else if (short1 == 3) {
            final short short2 = byteBufferReader.getShort();
            final byte byte4 = byteBufferReader.getByte();
            n2 = src.getInt();
            final BaseVehicle vehicleByID = VehicleManager.instance.getVehicleByID(short2);
            if (vehicleByID != null) {
                final VehiclePart vehiclePart = (vehicleByID == null) ? null : vehicleByID.getPartByIndex(byte4);
                final ItemContainer itemContainer2 = (vehiclePart == null) ? null : vehiclePart.getItemContainer();
                if (itemContainer2 != null) {
                    for (int n3 = 0; n3 < n2; ++n3) {
                        final int int7 = byteBufferReader.getInt();
                        final InventoryItem itemWithID4 = itemContainer2.getItemWithID(int7);
                        if (itemWithID4 == null) {
                            GameServer.alreadyRemoved.add(int7);
                        }
                        else {
                            itemContainer2.Remove(itemWithID4);
                            itemContainer2.setExplored(true);
                            itemContainer2.setHasBeenLooted(true);
                            set.add(itemWithID4.getFullType());
                        }
                    }
                }
            }
        }
        for (int n4 = 0; n4 < GameServer.udpEngine.connections.size(); ++n4) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(n4);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && isoGridSquare != null && udpConnection2.RelevantTo((float)isoGridSquare.x, (float)isoGridSquare.y)) {
                src.rewind();
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.RemoveInventoryItemFromContainer.doPacket(startPacket);
                startPacket.bb.put(src);
                PacketTypes.PacketType.RemoveInventoryItemFromContainer.send(udpConnection2);
            }
        }
        if (!GameServer.alreadyRemoved.isEmpty()) {
            final ByteBufferWriter startPacket2 = udpConnection.startPacket();
            PacketTypes.PacketType.RemoveContestedItemsFromInventory.doPacket(startPacket2);
            startPacket2.putInt(GameServer.alreadyRemoved.size());
            for (int index = 0; index < GameServer.alreadyRemoved.size(); ++index) {
                startPacket2.putLong(GameServer.alreadyRemoved.get(index));
            }
            PacketTypes.PacketType.RemoveContestedItemsFromInventory.send(udpConnection);
        }
        GameServer.alreadyRemoved.clear();
        LoggerManager.getLogger("item").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;IIIILjava/lang/String;)Ljava/lang/String;, udpConnection.idStr, udpConnection.username, n2, int1, int2, int3, set.toString()));
    }
    
    private static void readItemStats(final ByteBuffer byteBuffer, final InventoryItem inventoryItem) {
        final int int1 = byteBuffer.getInt();
        final float float1 = byteBuffer.getFloat();
        final boolean b = byteBuffer.get() == 1;
        inventoryItem.setUses(int1);
        if (inventoryItem instanceof DrainableComboItem) {
            ((DrainableComboItem)inventoryItem).setDelta(float1);
            ((DrainableComboItem)inventoryItem).updateWeight();
        }
        if (b && inventoryItem instanceof Food) {
            final Food food = (Food)inventoryItem;
            food.setHungChange(byteBuffer.getFloat());
            food.setCalories(byteBuffer.getFloat());
            food.setCarbohydrates(byteBuffer.getFloat());
            food.setLipids(byteBuffer.getFloat());
            food.setProteins(byteBuffer.getFloat());
            food.setThirstChange(byteBuffer.getFloat());
            food.setFluReduction(byteBuffer.getInt());
            food.setPainReduction(byteBuffer.getFloat());
            food.setEndChange(byteBuffer.getFloat());
            food.setReduceFoodSickness(byteBuffer.getInt());
            food.setStressChange(byteBuffer.getFloat());
            food.setFatigueChange(byteBuffer.getFloat());
        }
    }
    
    static void receiveItemStats(final ByteBuffer src, final UdpConnection udpConnection, final short n) {
        final short short1 = src.getShort();
        final int int1 = src.getInt();
        final int int2 = src.getInt();
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, src.getInt());
        switch (short1) {
            case 0: {
                final byte value = src.get();
                final int int3 = src.getInt();
                if (gridSquare == null || value < 0) {
                    break;
                }
                if (value >= gridSquare.getStaticMovingObjects().size()) {
                    break;
                }
                final ItemContainer container = gridSquare.getStaticMovingObjects().get(value).getContainer();
                if (container == null) {
                    break;
                }
                final InventoryItem itemWithID = container.getItemWithID(int3);
                if (itemWithID == null) {
                    break;
                }
                readItemStats(src, itemWithID);
                break;
            }
            case 2: {
                final byte value2 = src.get();
                final byte value3 = src.get();
                final int int4 = src.getInt();
                if (gridSquare == null || value2 < 0) {
                    break;
                }
                if (value2 >= gridSquare.getObjects().size()) {
                    break;
                }
                final ItemContainer containerByIndex = gridSquare.getObjects().get(value2).getContainerByIndex(value3);
                if (containerByIndex == null) {
                    break;
                }
                final InventoryItem itemWithID2 = containerByIndex.getItemWithID(int4);
                if (itemWithID2 == null) {
                    break;
                }
                readItemStats(src, itemWithID2);
                break;
            }
            case 1: {
                final int int5 = src.getInt();
                if (gridSquare == null) {
                    break;
                }
                for (int i = 0; i < gridSquare.getWorldObjects().size(); ++i) {
                    final IsoWorldInventoryObject isoWorldInventoryObject = gridSquare.getWorldObjects().get(i);
                    if (isoWorldInventoryObject.getItem() != null && isoWorldInventoryObject.getItem().id == int5) {
                        readItemStats(src, isoWorldInventoryObject.getItem());
                        break;
                    }
                    if (isoWorldInventoryObject.getItem() instanceof InventoryContainer) {
                        final InventoryItem itemWithID3 = ((InventoryContainer)isoWorldInventoryObject.getItem()).getInventory().getItemWithID(int5);
                        if (itemWithID3 != null) {
                            readItemStats(src, itemWithID3);
                            break;
                        }
                    }
                }
                break;
            }
            case 3: {
                final short short2 = src.getShort();
                final byte value4 = src.get();
                final int int6 = src.getInt();
                final BaseVehicle vehicleByID = VehicleManager.instance.getVehicleByID(short2);
                if (vehicleByID == null) {
                    break;
                }
                final VehiclePart partByIndex = vehicleByID.getPartByIndex(value4);
                if (partByIndex == null) {
                    break;
                }
                final ItemContainer itemContainer = partByIndex.getItemContainer();
                if (itemContainer == null) {
                    break;
                }
                final InventoryItem itemWithID4 = itemContainer.getItemWithID(int6);
                if (itemWithID4 == null) {
                    break;
                }
                readItemStats(src, itemWithID4);
                break;
            }
        }
        for (int j = 0; j < GameServer.udpEngine.connections.size(); ++j) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(j);
            if (udpConnection2 != udpConnection) {
                if (udpConnection2.RelevantTo((float)int1, (float)int2)) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.ItemStats.doPacket(startPacket);
                    src.rewind();
                    startPacket.bb.put(src);
                    PacketTypes.PacketType.ItemStats.send(udpConnection2);
                }
            }
        }
    }
    
    static void receiveRequestItemsForContainer(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
        final short short1 = byteBuffer.getShort();
        GameWindow.ReadString(byteBuffer);
        GameWindow.ReadString(byteBuffer);
        final int int1 = byteBufferReader.getInt();
        final int int2 = byteBufferReader.getInt();
        final int int3 = byteBufferReader.getInt();
        final short short2 = byteBufferReader.getShort();
        byte index = -1;
        byte b = -1;
        int int4 = 0;
        short short3 = 0;
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
        ItemContainer itemContainer = null;
        if (short2 == 2) {
            index = byteBufferReader.getByte();
            b = byteBufferReader.getByte();
            if (gridSquare != null && index >= 0 && index < gridSquare.getObjects().size()) {
                final IsoObject isoObject = gridSquare.getObjects().get(index);
                if (isoObject != null) {
                    itemContainer = isoObject.getContainerByIndex(b);
                    if (itemContainer == null || itemContainer.isExplored()) {
                        return;
                    }
                }
            }
        }
        else if (short2 == 3) {
            short3 = byteBufferReader.getShort();
            b = byteBufferReader.getByte();
            final BaseVehicle vehicleByID = VehicleManager.instance.getVehicleByID(short3);
            if (vehicleByID != null) {
                final VehiclePart partByIndex = vehicleByID.getPartByIndex(b);
                itemContainer = ((partByIndex == null) ? null : partByIndex.getItemContainer());
                if (itemContainer == null || itemContainer.isExplored()) {
                    return;
                }
            }
        }
        else if (short2 == 1) {
            int4 = byteBufferReader.getInt();
            for (int i = 0; i < gridSquare.getWorldObjects().size(); ++i) {
                final IsoWorldInventoryObject isoWorldInventoryObject = gridSquare.getWorldObjects().get(i);
                if (isoWorldInventoryObject != null && isoWorldInventoryObject.getItem() instanceof InventoryContainer && isoWorldInventoryObject.getItem().id == int4) {
                    itemContainer = ((InventoryContainer)isoWorldInventoryObject.getItem()).getInventory();
                    break;
                }
            }
        }
        else if (short2 == 0) {
            index = byteBufferReader.getByte();
            if (gridSquare != null && index >= 0 && index < gridSquare.getStaticMovingObjects().size()) {
                final IsoMovingObject isoMovingObject = gridSquare.getStaticMovingObjects().get(index);
                if (isoMovingObject != null && isoMovingObject.getContainer() != null) {
                    if (isoMovingObject.getContainer().isExplored()) {
                        return;
                    }
                    itemContainer = isoMovingObject.getContainer();
                }
            }
        }
        if (itemContainer == null || itemContainer.isExplored()) {
            return;
        }
        itemContainer.setExplored(true);
        final int size = itemContainer.Items.size();
        ItemPickerJava.fillContainer(itemContainer, GameServer.IDToPlayerMap.get(short1));
        if (size == itemContainer.Items.size()) {
            return;
        }
        for (int j = 0; j < GameServer.udpEngine.connections.size(); ++j) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(j);
            if (udpConnection2.RelevantTo((float)gridSquare.x, (float)gridSquare.y)) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(startPacket);
                startPacket.putShort(short2);
                startPacket.putInt(int1);
                startPacket.putInt(int2);
                startPacket.putInt(int3);
                if (short2 == 0) {
                    startPacket.putByte(index);
                }
                else if (short2 == 1) {
                    startPacket.putInt(int4);
                }
                else if (short2 == 3) {
                    startPacket.putShort(short3);
                    startPacket.putByte(b);
                }
                else {
                    startPacket.putByte(index);
                    startPacket.putByte(b);
                }
                try {
                    CompressIdenticalItems.save(startPacket.bb, itemContainer.getItems(), null);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                PacketTypes.PacketType.AddInventoryItemToContainer.send(udpConnection2);
            }
        }
    }
    
    public static void sendItemsInContainer(final IsoObject isoObject, final ItemContainer itemContainer) {
        if (GameServer.udpEngine == null) {
            return;
        }
        if (itemContainer == null) {
            DebugLog.log("sendItemsInContainer: container is null");
            return;
        }
        if (isoObject instanceof IsoWorldInventoryObject) {
            final IsoWorldInventoryObject isoWorldInventoryObject = (IsoWorldInventoryObject)isoObject;
            if (!(isoWorldInventoryObject.getItem() instanceof InventoryContainer)) {
                DebugLog.log("sendItemsInContainer: IsoWorldInventoryObject item isn't a container");
                return;
            }
            if (((InventoryContainer)isoWorldInventoryObject.getItem()).getInventory() != itemContainer) {
                DebugLog.log("sendItemsInContainer: wrong container for IsoWorldInventoryObject");
                return;
            }
        }
        else if (isoObject instanceof BaseVehicle) {
            if (itemContainer.vehiclePart == null || itemContainer.vehiclePart.getItemContainer() != itemContainer || itemContainer.vehiclePart.getVehicle() != isoObject) {
                DebugLog.log("sendItemsInContainer: wrong container for BaseVehicle");
                return;
            }
        }
        else if (isoObject instanceof IsoDeadBody) {
            if (itemContainer != isoObject.getContainer()) {
                DebugLog.log("sendItemsInContainer: wrong container for IsoDeadBody");
                return;
            }
        }
        else if (isoObject.getContainerIndex(itemContainer) == -1) {
            DebugLog.log("sendItemsInContainer: wrong container for IsoObject");
            return;
        }
        if (isoObject == null || itemContainer == null || itemContainer.getItems().isEmpty()) {
            return;
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.RelevantTo((float)isoObject.square.x, (float)isoObject.square.y)) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(startPacket);
                if (isoObject instanceof IsoDeadBody) {
                    startPacket.putShort((short)0);
                }
                else if (isoObject instanceof IsoWorldInventoryObject) {
                    startPacket.putShort((short)1);
                }
                else if (isoObject instanceof BaseVehicle) {
                    startPacket.putShort((short)3);
                }
                else {
                    startPacket.putShort((short)2);
                }
                startPacket.putInt(isoObject.getSquare().getX());
                startPacket.putInt(isoObject.getSquare().getY());
                startPacket.putInt(isoObject.getSquare().getZ());
                if (isoObject instanceof IsoDeadBody) {
                    startPacket.putByte((byte)isoObject.getStaticMovingObjectIndex());
                }
                else if (isoObject instanceof IsoWorldInventoryObject) {
                    startPacket.putLong(((IsoWorldInventoryObject)isoObject).getItem().id);
                }
                else if (isoObject instanceof BaseVehicle) {
                    startPacket.putShort(((BaseVehicle)isoObject).VehicleID);
                    startPacket.putByte((byte)itemContainer.vehiclePart.getIndex());
                }
                else {
                    startPacket.putByte((byte)isoObject.getObjectIndex());
                    startPacket.putByte((byte)isoObject.getContainerIndex(itemContainer));
                }
                try {
                    CompressIdenticalItems.save(startPacket.bb, itemContainer.getItems(), null);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                PacketTypes.PacketType.AddInventoryItemToContainer.send(udpConnection);
            }
        }
    }
    
    private static void logDupeItem(final UdpConnection udpConnection) {
        IsoPlayer isoPlayer = null;
        for (int i = 0; i < GameServer.Players.size(); ++i) {
            if (udpConnection.username.equals(GameServer.Players.get(i).username)) {
                isoPlayer = GameServer.Players.get(i);
                break;
            }
        }
        String playerCoords = "";
        if (isoPlayer != null) {
            playerCoords = LoggerManager.getPlayerCoords(isoPlayer);
        }
        LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, isoPlayer.getDisplayName(), playerCoords));
        ServerWorldDatabase.instance.addUserlog(udpConnection.username, Userlog.UserlogType.DupeItem, "", "server", 1);
    }
    
    static void receiveAddInventoryItemToContainer(final ByteBuffer src, final UdpConnection udpConnection, final short n) {
        final ByteBufferReader byteBufferReader = new ByteBufferReader(src);
        final short short1 = byteBufferReader.getShort();
        final int int1 = byteBufferReader.getInt();
        final int int2 = byteBufferReader.getInt();
        final int int3 = byteBufferReader.getInt();
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
        final HashSet<String> set = new HashSet<String>();
        final boolean b = false;
        if (gridSquare != null) {
            ItemContainer itemContainer = null;
            IsoObject isoObject = null;
            if (short1 == 0) {
                final byte byte1 = byteBufferReader.getByte();
                if (byte1 < 0 || byte1 >= gridSquare.getStaticMovingObjects().size()) {
                    DebugLog.log("ERROR sendItemsToContainer invalid corpse index");
                    return;
                }
                final IsoMovingObject isoMovingObject = gridSquare.getStaticMovingObjects().get(byte1);
                if (isoMovingObject != null && isoMovingObject.getContainer() != null) {
                    itemContainer = isoMovingObject.getContainer();
                }
            }
            else if (short1 == 1) {
                final int int4 = byteBufferReader.getInt();
                for (int i = 0; i < gridSquare.getWorldObjects().size(); ++i) {
                    final IsoWorldInventoryObject isoWorldInventoryObject = gridSquare.getWorldObjects().get(i);
                    if (isoWorldInventoryObject != null && isoWorldInventoryObject.getItem() instanceof InventoryContainer && isoWorldInventoryObject.getItem().id == int4) {
                        itemContainer = ((InventoryContainer)isoWorldInventoryObject.getItem()).getInventory();
                        break;
                    }
                }
                if (itemContainer == null) {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, int4));
                    return;
                }
            }
            else if (short1 == 2) {
                byte byte2 = byteBufferReader.getByte();
                byte byte3 = byteBufferReader.getByte();
                if (byte2 < 0 || byte2 >= gridSquare.getObjects().size()) {
                    DebugLog.log("ERROR sendItemsToContainer invalid object index");
                    for (int j = 0; j < gridSquare.getObjects().size(); ++j) {
                        if (gridSquare.getObjects().get(j).getContainer() != null) {
                            byte2 = (byte)j;
                            byte3 = 0;
                            break;
                        }
                    }
                    if (byte2 == -1) {
                        return;
                    }
                }
                isoObject = gridSquare.getObjects().get(byte2);
                itemContainer = ((isoObject != null) ? isoObject.getContainerByIndex(byte3) : null);
            }
            else if (short1 == 3) {
                final short short2 = byteBufferReader.getShort();
                final byte byte4 = byteBufferReader.getByte();
                final BaseVehicle vehicleByID = VehicleManager.instance.getVehicleByID(short2);
                if (vehicleByID == null) {
                    DebugLog.log("ERROR sendItemsToContainer invalid vehicle id");
                    return;
                }
                final VehiclePart partByIndex = vehicleByID.getPartByIndex(byte4);
                itemContainer = ((partByIndex == null) ? null : partByIndex.getItemContainer());
            }
            if (itemContainer != null) {
                try {
                    final ArrayList<InventoryItem> load = CompressIdenticalItems.load(byteBufferReader.bb, 186, null, null);
                    for (int k = 0; k < load.size(); ++k) {
                        final InventoryItem inventoryItem = load.get(k);
                        if (inventoryItem != null) {
                            if (itemContainer.containsID(inventoryItem.id)) {
                                System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, udpConnection.username));
                                logDupeItem(udpConnection);
                            }
                            else {
                                itemContainer.addItem(inventoryItem);
                                itemContainer.setExplored(true);
                                set.add(inventoryItem.getFullType());
                                if (isoObject instanceof IsoMannequin) {
                                    ((IsoMannequin)isoObject).wearItem(inventoryItem, null);
                                }
                            }
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (isoObject != null) {
                    LuaManager.updateOverlaySprite(isoObject);
                    if ("campfire".equals(itemContainer.getType())) {
                        isoObject.sendObjectChange("container.customTemperature");
                    }
                }
            }
        }
        else {
            DebugLog.log("ERROR sendItemsToContainer square is null");
        }
        for (int l = 0; l < GameServer.udpEngine.connections.size(); ++l) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(l);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.RelevantTo((float)gridSquare.x, (float)gridSquare.y)) {
                src.rewind();
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(startPacket);
                startPacket.bb.put(src);
                PacketTypes.PacketType.AddInventoryItemToContainer.send(udpConnection2);
            }
        }
        LoggerManager.getLogger("item").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;IIIILjava/lang/String;)Ljava/lang/String;, udpConnection.idStr, udpConnection.username, b, int1, int2, int3, set.toString()));
    }
    
    public static void addConnection(final UdpConnection udpConnection) {
        synchronized (GameServer.MainLoopNetDataHighPriorityQ) {
            GameServer.MainLoopNetDataHighPriorityQ.add(new DelayedConnection(udpConnection, true));
        }
    }
    
    public static void addDisconnect(final UdpConnection udpConnection) {
        synchronized (GameServer.MainLoopNetDataHighPriorityQ) {
            GameServer.MainLoopNetDataHighPriorityQ.add(new DelayedConnection(udpConnection, false));
        }
    }
    
    public static void disconnectPlayer(final IsoPlayer isoPlayer, final UdpConnection udpConnection) {
        if (isoPlayer == null) {
            return;
        }
        ChatServer.getInstance().disconnectPlayer(isoPlayer.getOnlineID());
        if (isoPlayer.getVehicle() != null) {
            VehiclesDB2.instance.updateVehicleAndTrailer(isoPlayer.getVehicle());
            if (isoPlayer.getVehicle().getDriver() == isoPlayer) {
                isoPlayer.getVehicle().setNetPlayerAuthorization((byte)0);
                isoPlayer.getVehicle().netPlayerId = -1;
                isoPlayer.getVehicle().getController().clientForce = 0.0f;
                isoPlayer.getVehicle().netLinearVelocity.set(0.0f, 0.0f, 0.0f);
            }
            final int seat = isoPlayer.getVehicle().getSeat(isoPlayer);
            if (seat != -1) {
                isoPlayer.getVehicle().clearPassenger(seat);
            }
        }
        if (!isoPlayer.isDead()) {
            ServerWorldDatabase.instance.saveTransactionID(isoPlayer.username, isoPlayer.getTransactionID());
        }
        NetworkZombieManager.getInstance().clearTargetAuth(udpConnection, isoPlayer);
        isoPlayer.removeFromWorld();
        isoPlayer.removeFromSquare();
        GameServer.PlayerToAddressMap.remove(isoPlayer);
        GameServer.IDToAddressMap.remove(isoPlayer.OnlineID);
        GameServer.IDToPlayerMap.remove(isoPlayer.OnlineID);
        GameServer.Players.remove(isoPlayer);
        udpConnection.usernames[isoPlayer.PlayerIndex] = null;
        udpConnection.players[isoPlayer.PlayerIndex] = null;
        udpConnection.playerIDs[isoPlayer.PlayerIndex] = -1;
        udpConnection.ReleventPos[isoPlayer.PlayerIndex] = null;
        udpConnection.connectArea[isoPlayer.PlayerIndex] = null;
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            final ByteBufferWriter startPacket = udpConnection2.startPacket();
            PacketTypes.PacketType.PlayerTimeout.doPacket(startPacket);
            startPacket.putShort(isoPlayer.OnlineID);
            PacketTypes.PacketType.PlayerTimeout.send(udpConnection2);
        }
        ServerLOS.instance.removePlayer(isoPlayer);
        ZombiePopulationManager.instance.updateLoadedAreas();
        DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, isoPlayer.getDisplayName(), udpConnection.idStr));
        LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, udpConnection.idStr, isoPlayer.getUsername(), LoggerManager.getPlayerCoords(isoPlayer)));
    }
    
    public static void heartBeat() {
        ++GameServer.count;
    }
    
    public static short getFreeSlot() {
        for (short n = 0; n < GameServer.udpEngine.getMaxConnections(); ++n) {
            if (GameServer.SlotToConnection[n] == null) {
                return n;
            }
        }
        return -1;
    }
    
    public static void receiveClientConnect(final UdpConnection udpConnection, final ServerWorldDatabase.LogonResult logonResult) {
        final short freeSlot = getFreeSlot();
        final short n = (short)(freeSlot * 4);
        if (udpConnection.playerDownloadServer != null) {
            try {
                GameServer.IDToAddressMap.put(n, udpConnection.getConnectedGUID());
                udpConnection.playerDownloadServer.destroy();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        GameServer.playerToCoordsMap.put(n, new Vector2());
        GameServer.playerMovedToFastMap.put(n, 0);
        GameServer.SlotToConnection[freeSlot] = udpConnection;
        udpConnection.playerIDs[0] = n;
        GameServer.IDToAddressMap.put(n, udpConnection.getConnectedGUID());
        udpConnection.playerDownloadServer = new PlayerDownloadServer(udpConnection, GameServer.DEFAULT_PORT + freeSlot + 1);
        DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;SI)Ljava/lang/String;, udpConnection.username, n, udpConnection.playerDownloadServer.port));
        udpConnection.playerDownloadServer.startConnectionTest();
        final KahluaTable spawnRegions = SpawnPoints.instance.getSpawnRegions();
        for (int i = 1; i < spawnRegions.size() + 1; ++i) {
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.SpawnRegion.doPacket(startPacket);
            startPacket.putInt(i);
            try {
                ((KahluaTable)spawnRegions.rawget(i)).save(startPacket.bb);
                PacketTypes.PacketType.SpawnRegion.send(udpConnection);
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
        VehicleManager.serverSendVehiclesConfig(udpConnection);
        final ByteBufferWriter startPacket2 = udpConnection.startPacket();
        PacketTypes.PacketType.ConnectionDetails.doPacket(startPacket2);
        if (SteamUtils.isSteamModeEnabled() && CoopSlave.instance != null && !udpConnection.isCoopHost) {
            startPacket2.putByte((byte)1);
            startPacket2.putLong(CoopSlave.instance.hostSteamID);
            startPacket2.putUTF(GameServer.ServerName);
        }
        else {
            startPacket2.putByte((byte)0);
        }
        startPacket2.putByte((byte)freeSlot);
        startPacket2.putInt(udpConnection.playerDownloadServer.port);
        startPacket2.putBoolean(GameServer.UseTCPForMapDownloads);
        startPacket2.putUTF(logonResult.accessLevel);
        startPacket2.putUTF(GameServer.GameMap);
        if (SteamUtils.isSteamModeEnabled()) {
            startPacket2.putShort((short)GameServer.WorkshopItems.size());
            for (int j = 0; j < GameServer.WorkshopItems.size(); ++j) {
                startPacket2.putLong(GameServer.WorkshopItems.get(j));
                startPacket2.putLong(GameServer.WorkshopTimeStamps[j]);
            }
        }
        final ArrayList<ChooseGameInfo.Mod> list = new ArrayList<ChooseGameInfo.Mod>();
        for (final String s : GameServer.ServerMods) {
            final String modDir = ZomboidFileSystem.instance.getModDir(s);
            ChooseGameInfo.Mod modInfo;
            if (modDir != null) {
                try {
                    modInfo = ChooseGameInfo.readModInfo(modDir);
                }
                catch (Exception ex3) {
                    ExceptionLogger.logException(ex3);
                    modInfo = new ChooseGameInfo.Mod(s);
                    modInfo.setId(s);
                    modInfo.setName(s);
                }
            }
            else {
                modInfo = new ChooseGameInfo.Mod(s);
                modInfo.setId(s);
                modInfo.setName(s);
            }
            list.add(modInfo);
        }
        startPacket2.putInt(list.size());
        for (final ChooseGameInfo.Mod mod : list) {
            startPacket2.putUTF(mod.getId());
            startPacket2.putUTF(mod.getUrl());
            startPacket2.putUTF(mod.getName());
        }
        final Vector3 startLocation = ServerMap.instance.getStartLocation(logonResult);
        logonResult.x = (int)startLocation.x;
        logonResult.y = (int)startLocation.y;
        logonResult.z = (int)startLocation.z;
        startPacket2.putInt(logonResult.x);
        startPacket2.putInt(logonResult.y);
        startPacket2.putInt(logonResult.z);
        startPacket2.putInt(ServerOptions.instance.getPublicOptions().size());
        for (final String s2 : ServerOptions.instance.getPublicOptions()) {
            startPacket2.putUTF(s2);
            startPacket2.putUTF(ServerOptions.instance.getOption(s2));
        }
        try {
            SandboxOptions.instance.save(startPacket2.bb);
            GameTime.getInstance().saveToPacket(startPacket2.bb);
        }
        catch (IOException ex4) {
            ex4.printStackTrace();
        }
        ErosionMain.getInstance().getConfig().save(startPacket2.bb);
        try {
            SGlobalObjects.saveInitialStateForClient(startPacket2.bb);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        startPacket2.putInt(GameServer.ResetID);
        GameWindow.WriteString(startPacket2.bb, Core.getInstance().getPoisonousBerry());
        GameWindow.WriteString(startPacket2.bb, Core.getInstance().getPoisonousMushroom());
        startPacket2.putBoolean(udpConnection.isCoopHost);
        try {
            WorldDictionary.saveDataForClient(startPacket2.bb);
        }
        catch (Exception ex5) {
            ex5.printStackTrace();
        }
        PacketTypes.PacketType.ConnectionDetails.send(udpConnection);
        if (!SteamUtils.isSteamModeEnabled()) {
            PublicServerUtil.updatePlayers();
        }
    }
    
    private static void sendLargeFile(final UdpConnection udpConnection, final String s) {
        int min;
        for (int position = GameServer.large_file_bb.position(), i = 0; i < position; i += min) {
            min = Math.min(1000, position - i);
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.RequestData.doPacket(startPacket);
            startPacket.putUTF(s);
            startPacket.putInt(position);
            startPacket.putInt(i);
            startPacket.putInt(min);
            startPacket.bb.put(GameServer.large_file_bb.array(), i, min);
            PacketTypes.PacketType.RequestData.send(udpConnection);
        }
    }
    
    static void receiveRequestData(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final String readString = GameWindow.ReadString(byteBuffer);
        if ("descriptors.bin".equals(readString)) {
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.RequestData.doPacket(startPacket);
            startPacket.putUTF(readString);
            try {
                PersistentOutfits.instance.save(startPacket.bb);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            PacketTypes.PacketType.RequestData.send(udpConnection);
        }
        if ("playerzombiedesc".equals(readString)) {
            final ByteBufferWriter startPacket2 = udpConnection.startPacket();
            PacketTypes.PacketType.RequestData.doPacket(startPacket2);
            startPacket2.putUTF(readString);
            final SharedDescriptors.Descriptor[] playerZombieDescriptors = SharedDescriptors.getPlayerZombieDescriptors();
            int n2 = 0;
            for (int i = 0; i < playerZombieDescriptors.length; ++i) {
                if (playerZombieDescriptors[i] != null) {
                    ++n2;
                }
            }
            try {
                startPacket2.putShort((short)n2);
                for (final SharedDescriptors.Descriptor descriptor : playerZombieDescriptors) {
                    if (descriptor != null) {
                        descriptor.save(startPacket2.bb);
                    }
                }
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
            }
            PacketTypes.PacketType.RequestData.send(udpConnection);
        }
        if ("map_meta.bin".equals(readString)) {
            try {
                GameServer.large_file_bb.clear();
                IsoWorld.instance.MetaGrid.savePart(GameServer.large_file_bb, 0, true);
                IsoWorld.instance.MetaGrid.savePart(GameServer.large_file_bb, 1, true);
                sendLargeFile(udpConnection, readString);
            }
            catch (Exception ex3) {
                ex3.printStackTrace();
                final ByteBufferWriter startPacket3 = udpConnection.startPacket();
                PacketTypes.PacketType.Kicked.doPacket(startPacket3);
                startPacket3.putUTF("You have been kicked from this server because map_meta.bin could not be saved.");
                PacketTypes.PacketType.Kicked.send(udpConnection);
                udpConnection.forceDisconnect();
                addDisconnect(udpConnection);
            }
        }
        if ("map_zone.bin".equals(readString)) {
            try {
                GameServer.large_file_bb.clear();
                IsoWorld.instance.MetaGrid.saveZone(GameServer.large_file_bb);
                sendLargeFile(udpConnection, readString);
            }
            catch (Exception ex4) {
                ex4.printStackTrace();
                final ByteBufferWriter startPacket4 = udpConnection.startPacket();
                PacketTypes.PacketType.Kicked.doPacket(startPacket4);
                startPacket4.putUTF("You have been kicked from this server because map_zone.bin could not be saved.");
                PacketTypes.PacketType.Kicked.send(udpConnection);
                udpConnection.forceDisconnect();
                addDisconnect(udpConnection);
            }
        }
    }
    
    public static void sendMetaGrid(final int n, final int n2, final int n3, final UdpConnection udpConnection) {
        final IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
        if (n < metaGrid.getMinX() || n > metaGrid.getMaxX() || n2 < metaGrid.getMinY() || n2 > metaGrid.getMaxY()) {
            return;
        }
        final IsoMetaCell cellData = metaGrid.getCellData(n, n2);
        if (cellData.info == null || n3 < 0 || n3 >= cellData.info.RoomList.size()) {
            return;
        }
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.MetaGrid.doPacket(startPacket);
        startPacket.putShort((short)n);
        startPacket.putShort((short)n2);
        startPacket.putShort((short)n3);
        startPacket.putBoolean(cellData.info.getRoom(n3).def.bLightsActive);
        PacketTypes.PacketType.MetaGrid.send(udpConnection);
    }
    
    public static void sendMetaGrid(final int n, final int n2, final int n3) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            sendMetaGrid(n, n2, n3, GameServer.udpEngine.connections.get(i));
        }
    }
    
    private static void preventIndoorZombies(final int n, final int n2, final int n3) {
        final RoomDef room = IsoWorld.instance.MetaGrid.getRoomAt(n, n2, n3);
        if (room == null) {
            return;
        }
        final boolean spawnBuilding = isSpawnBuilding(room.getBuilding());
        room.getBuilding().setAllExplored(true);
        final ArrayList<IsoZombie> zombieList = IsoWorld.instance.CurrentCell.getZombieList();
        for (int i = 0; i < zombieList.size(); ++i) {
            final IsoZombie isoZombie = zombieList.get(i);
            if ((spawnBuilding || isoZombie.bIndoorZombie) && isoZombie.getSquare() != null && isoZombie.getSquare().getRoom() != null && isoZombie.getSquare().getRoom().def.building == room.getBuilding()) {
                VirtualZombieManager.instance.removeZombieFromWorld(isoZombie);
                if (i >= zombieList.size() || zombieList.get(i) != isoZombie) {
                    --i;
                }
            }
        }
    }
    
    private static void receivePlayerConnect(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final String username) {
        DebugLog.General.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, username, udpConnection.ip));
        final byte value = byteBuffer.get();
        if (value < 0 || value >= 4 || udpConnection.players[value] != null) {
            return;
        }
        final byte value2 = byteBuffer.get();
        udpConnection.ReleventRange = (byte)(value2 / 2 + 2);
        final float float1 = byteBuffer.getFloat();
        final float float2 = byteBuffer.getFloat();
        final float float3 = byteBuffer.getFloat();
        udpConnection.ReleventPos[value].x = float1;
        udpConnection.ReleventPos[value].y = float2;
        udpConnection.ReleventPos[value].z = float3;
        udpConnection.connectArea[value] = null;
        udpConnection.ChunkGridWidth = value2;
        udpConnection.loadedCells[value] = new ClientServerMap(value, (int)float1, (int)float2, value2);
        final SurvivorDesc createSurvivor = SurvivorFactory.CreateSurvivor();
        try {
            createSurvivor.load(byteBuffer, 186, null);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        final IsoPlayer key = new IsoPlayer(null, createSurvivor, (int)float1, (int)float2, (int)float3);
        key.PlayerIndex = value;
        key.OnlineChunkGridWidth = value2;
        GameServer.Players.add(key);
        key.bRemote = true;
        try {
            key.getHumanVisual().load(byteBuffer, 186);
            key.getItemVisuals().load(byteBuffer, 186);
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
        }
        final short n = udpConnection.playerIDs[value];
        GameServer.IDToPlayerMap.put(n, key);
        udpConnection.players[value] = key;
        GameServer.PlayerToAddressMap.put(key, udpConnection.getConnectedGUID());
        key.setOnlineID(n);
        try {
            key.getXp().load(byteBuffer, 186);
        }
        catch (IOException ex3) {
            ex3.printStackTrace();
        }
        key.setAllChatMuted(byteBuffer.get() == 1);
        udpConnection.allChatMuted = key.isAllChatMuted();
        key.setTagPrefix(GameWindow.ReadString(byteBuffer));
        key.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0f));
        key.setTransactionID(byteBuffer.getInt());
        key.setHoursSurvived(byteBuffer.getDouble());
        key.setZombieKills(byteBuffer.getInt());
        key.setDisplayName(GameWindow.ReadString(byteBuffer));
        key.setSpeakColour(new Color(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0f));
        key.showTag = (byteBuffer.get() == 1);
        key.factionPvp = (byteBuffer.get() == 1);
        if (SteamUtils.isSteamModeEnabled()) {
            key.setSteamID(udpConnection.steamID);
            GameWindow.ReadStringUTF(byteBuffer);
            SteamGameServer.BUpdateUserData(udpConnection.steamID, udpConnection.username, 0);
        }
        if (byteBuffer.get() == 1) {
            InventoryItem loadItem;
            try {
                loadItem = InventoryItem.loadItem(byteBuffer, 186);
            }
            catch (IOException ex4) {
                ex4.printStackTrace();
                return;
            }
            if (loadItem == null) {
                LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, udpConnection.idStr));
                return;
            }
            key.setPrimaryHandItem(loadItem);
        }
        final byte value3 = byteBuffer.get();
        if (value3 == 2) {
            key.setSecondaryHandItem(key.getPrimaryHandItem());
        }
        if (value3 == 1) {
            InventoryItem loadItem2;
            try {
                loadItem2 = InventoryItem.loadItem(byteBuffer, 186);
            }
            catch (IOException ex5) {
                ex5.printStackTrace();
                return;
            }
            if (loadItem2 == null) {
                LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, udpConnection.idStr));
                return;
            }
            key.setSecondaryHandItem(loadItem2);
        }
        for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
            final String readString = GameWindow.ReadString(byteBuffer);
            final InventoryItem createItem = InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer));
            if (createItem != null) {
                key.setAttachedItem(readString, createItem);
            }
        }
        key.remoteSneakLvl = byteBuffer.getInt();
        key.username = username;
        key.accessLevel = udpConnection.accessLevel;
        if (!key.accessLevel.equals("") && CoopSlave.instance == null) {
            key.setGhostMode(true);
            key.setInvisible(true);
            key.setGodMod(true);
        }
        ChatServer.getInstance().initPlayer(key.OnlineID);
        udpConnection.setFullyConnected();
        sendWeather(udpConnection);
        for (int j = 0; j < GameServer.udpEngine.connections.size(); ++j) {
            sendPlayerConnect(key, GameServer.udpEngine.connections.get(j));
        }
        final SyncInjuriesPacket syncInjuriesPacket = new SyncInjuriesPacket();
        for (final IsoPlayer isoPlayer : GameServer.IDToPlayerMap.values()) {
            if (isoPlayer.getOnlineID() != key.getOnlineID() && isoPlayer.isAlive()) {
                sendPlayerConnect(isoPlayer, udpConnection);
                syncInjuriesPacket.set(isoPlayer);
                sendPlayerInjuries(udpConnection, syncInjuriesPacket);
            }
        }
        udpConnection.loadedCells[value].setLoaded();
        udpConnection.loadedCells[value].sendPacket(udpConnection);
        preventIndoorZombies((int)float1, (int)float2, (int)float3);
        ServerLOS.instance.addPlayer(key);
        LoggerManager.getLogger("user").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, udpConnection.idStr, key.username, LoggerManager.getPlayerCoords(key)));
    }
    
    static void receivePlayerSave(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        if ((Calendar.getInstance().getTimeInMillis() - GameServer.previousSave) / 60000L < 0L) {
            return;
        }
        final byte value = byteBuffer.get();
        if (value < 0 || value >= 4) {
            return;
        }
        final short short1 = byteBuffer.getShort();
        byteBuffer.getFloat();
        byteBuffer.getFloat();
        byteBuffer.getFloat();
        ServerMap.instance.saveZoneInsidePlayerInfluence(short1);
    }
    
    static void receiveSendPlayerProfile(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        ServerPlayerDB.getInstance().serverUpdateNetworkCharacter(byteBuffer, udpConnection);
    }
    
    static void receiveLoadPlayerProfile(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        ServerPlayerDB.getInstance().serverLoadNetworkCharacter(byteBuffer, udpConnection);
    }
    
    private static void coopAccessGranted(final int n, final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.AddCoopPlayer.doPacket(startPacket);
        startPacket.putBoolean(true);
        startPacket.putByte((byte)n);
        PacketTypes.PacketType.AddCoopPlayer.send(udpConnection);
    }
    
    private static void coopAccessDenied(final String s, final int n, final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.AddCoopPlayer.doPacket(startPacket);
        startPacket.putBoolean(false);
        startPacket.putByte((byte)n);
        startPacket.putUTF(s);
        PacketTypes.PacketType.AddCoopPlayer.send(udpConnection);
    }
    
    static void receiveAddCoopPlayer(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final byte value = byteBuffer.get();
        final byte value2 = byteBuffer.get();
        if (value2 < 0 || value2 >= 4) {
            coopAccessDenied("Invalid coop player index", value2, udpConnection);
            return;
        }
        if (udpConnection.players[value2] != null && !udpConnection.players[value2].isDead()) {
            coopAccessDenied(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, value2 + 1), (int)value2, udpConnection);
            return;
        }
        if (value == 1) {
            final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
            if (readStringUTF.isEmpty()) {
                coopAccessDenied("No username given", value2, udpConnection);
                return;
            }
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                for (byte b = 0; b < 4; ++b) {
                    if (udpConnection2 != udpConnection || value2 != b) {
                        if (readStringUTF.equals(udpConnection2.usernames[b])) {
                            coopAccessDenied(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, readStringUTF), (int)value2, udpConnection);
                            return;
                        }
                    }
                }
            }
            DebugLog.log(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, value2 + 1, readStringUTF));
            if (udpConnection.players[value2] != null) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, value2 + 1, readStringUTF));
                final short onlineID = udpConnection.players[value2].OnlineID;
                disconnectPlayer(udpConnection.players[value2], udpConnection);
                final float float1 = byteBuffer.getFloat();
                final float float2 = byteBuffer.getFloat();
                udpConnection.usernames[value2] = readStringUTF;
                udpConnection.ReleventPos[value2] = new Vector3(float1, float2, 0.0f);
                udpConnection.connectArea[value2] = new Vector3(float1 / 10.0f, float2 / 10.0f, (float)udpConnection.ChunkGridWidth);
                udpConnection.playerIDs[value2] = onlineID;
                GameServer.IDToAddressMap.put(onlineID, udpConnection.getConnectedGUID());
                coopAccessGranted(value2, udpConnection);
                ZombiePopulationManager.instance.updateLoadedAreas();
                if (ChatServer.isInited()) {
                    ChatServer.getInstance().initPlayer(onlineID);
                }
                return;
            }
            if (getPlayerCount() >= ServerOptions.getInstance().getMaxPlayers()) {
                coopAccessDenied("Server is full", value2, udpConnection);
                return;
            }
            int n2 = -1;
            for (int j = 0; j < GameServer.udpEngine.getMaxConnections(); j = (short)(j + 1)) {
                if (GameServer.SlotToConnection[j] == udpConnection) {
                    n2 = j;
                    break;
                }
            }
            final short s = (short)(n2 * 4 + value2);
            DebugLog.log(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;S)Ljava/lang/String;, value2 + 1, readStringUTF, s));
            final float float3 = byteBuffer.getFloat();
            final float float4 = byteBuffer.getFloat();
            udpConnection.usernames[value2] = readStringUTF;
            udpConnection.ReleventPos[value2] = new Vector3(float3, float4, 0.0f);
            udpConnection.playerIDs[value2] = s;
            udpConnection.connectArea[value2] = new Vector3(float3 / 10.0f, float4 / 10.0f, (float)udpConnection.ChunkGridWidth);
            GameServer.IDToAddressMap.put(s, udpConnection.getConnectedGUID());
            coopAccessGranted(value2, udpConnection);
            ZombiePopulationManager.instance.updateLoadedAreas();
        }
        else {
            if (value != 2) {
                return;
            }
            final String s2 = udpConnection.usernames[value2];
            if (s2 == null) {
                coopAccessDenied("Coop player login wasn't received", value2, udpConnection);
                return;
            }
            DebugLog.log(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, value2 + 1, s2));
            receivePlayerConnect(byteBuffer, udpConnection, s2);
        }
    }
    
    private static void sendInitialWorldState(final UdpConnection udpConnection) {
        if (RainManager.isRaining()) {
            sendStartRain(udpConnection);
        }
        try {
            ClimateManager.getInstance().sendInitialState(udpConnection);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static void receiveObjectModData(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        final boolean b = byteBuffer.get() == 1;
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
        if (gridSquare != null && int4 >= 0 && int4 < gridSquare.getObjects().size()) {
            final IsoObject isoObject = gridSquare.getObjects().get(int4);
            if (b) {
                final int waterAmount = isoObject.getWaterAmount();
                try {
                    isoObject.getModData().load(byteBuffer, 186);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (waterAmount != isoObject.getWaterAmount()) {
                    LuaEventManager.triggerEvent("OnWaterAmountChange", isoObject, waterAmount);
                }
            }
            else if (isoObject.hasModData()) {
                isoObject.getModData().wipe();
            }
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.RelevantTo((float)int1, (float)int2)) {
                    sendObjectModData(isoObject, udpConnection2);
                }
            }
        }
        else if (gridSquare != null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(IIII)Ljava/lang/String;, int4, int1, int2, int3));
        }
        else if (GameServer.bDebug) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, int1, int2, int3));
        }
    }
    
    private static void sendObjectModData(final IsoObject isoObject, final UdpConnection udpConnection) {
        if (isoObject.getSquare() == null) {
            return;
        }
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.ObjectModData.doPacket(startPacket);
        startPacket.putInt(isoObject.getSquare().getX());
        startPacket.putInt(isoObject.getSquare().getY());
        startPacket.putInt(isoObject.getSquare().getZ());
        startPacket.putInt(isoObject.getSquare().getObjects().indexOf(isoObject));
        if (isoObject.getModData().isEmpty()) {
            startPacket.putByte((byte)0);
        }
        else {
            startPacket.putByte((byte)1);
            try {
                isoObject.getModData().save(startPacket.bb);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        PacketTypes.PacketType.ObjectModData.send(udpConnection);
    }
    
    public static void sendObjectModData(final IsoObject isoObject) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.RelevantTo(isoObject.getX(), isoObject.getY())) {
                sendObjectModData(isoObject, udpConnection);
            }
        }
    }
    
    public static void sendSlowFactor(final IsoGameCharacter key) {
        if (!(key instanceof IsoPlayer)) {
            return;
        }
        if (!GameServer.PlayerToAddressMap.containsKey(key)) {
            return;
        }
        final UdpConnection activeConnection = GameServer.udpEngine.getActiveConnection(GameServer.PlayerToAddressMap.get(key));
        if (activeConnection == null) {
            return;
        }
        final ByteBufferWriter startPacket = activeConnection.startPacket();
        PacketTypes.PacketType.SlowFactor.doPacket(startPacket);
        startPacket.putByte((byte)((IsoPlayer)key).PlayerIndex);
        startPacket.putFloat(key.getSlowTimer());
        startPacket.putFloat(key.getSlowFactor());
        PacketTypes.PacketType.SlowFactor.send(activeConnection);
    }
    
    private static void sendObjectChange(final IsoObject isoObject, final String s, final KahluaTable kahluaTable, final UdpConnection udpConnection) {
        if (isoObject.getSquare() == null) {
            return;
        }
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.ObjectChange.doPacket(startPacket);
        if (isoObject instanceof IsoPlayer) {
            startPacket.putByte((byte)1);
            startPacket.putShort(((IsoPlayer)isoObject).OnlineID);
        }
        else if (isoObject instanceof BaseVehicle) {
            startPacket.putByte((byte)2);
            startPacket.putShort(((BaseVehicle)isoObject).getId());
        }
        else if (isoObject instanceof IsoWorldInventoryObject) {
            startPacket.putByte((byte)3);
            startPacket.putInt(isoObject.getSquare().getX());
            startPacket.putInt(isoObject.getSquare().getY());
            startPacket.putInt(isoObject.getSquare().getZ());
            startPacket.putInt(((IsoWorldInventoryObject)isoObject).getItem().getID());
        }
        else {
            startPacket.putByte((byte)0);
            startPacket.putInt(isoObject.getSquare().getX());
            startPacket.putInt(isoObject.getSquare().getY());
            startPacket.putInt(isoObject.getSquare().getZ());
            startPacket.putInt(isoObject.getSquare().getObjects().indexOf(isoObject));
        }
        startPacket.putUTF(s);
        isoObject.saveChange(s, kahluaTable, startPacket.bb);
        PacketTypes.PacketType.ObjectChange.send(udpConnection);
    }
    
    public static void sendObjectChange(final IsoObject isoObject, final String s, final KahluaTable kahluaTable) {
        if (isoObject == null) {
            return;
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.RelevantTo(isoObject.getX(), isoObject.getY())) {
                sendObjectChange(isoObject, s, kahluaTable, udpConnection);
            }
        }
    }
    
    public static void sendObjectChange(final IsoObject isoObject, final String s, final Object... array) {
        if (array.length == 0) {
            sendObjectChange(isoObject, s, null);
            return;
        }
        if (array.length % 2 != 0) {
            return;
        }
        final KahluaTable table = LuaManager.platform.newTable();
        for (int i = 0; i < array.length; i += 2) {
            final Object o = array[i + 1];
            if (o instanceof Float) {
                table.rawset(array[i], (Object)(double)o);
            }
            else if (o instanceof Integer) {
                table.rawset(array[i], (Object)(double)o);
            }
            else if (o instanceof Short) {
                table.rawset(array[i], (Object)(double)o);
            }
            else {
                table.rawset(array[i], o);
            }
        }
        sendObjectChange(isoObject, s, table);
    }
    
    private static void updateHandEquips(final UdpConnection udpConnection, final IsoPlayer isoPlayer) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.Equip.doPacket(startPacket);
        startPacket.putShort(isoPlayer.OnlineID);
        startPacket.putByte((byte)0);
        startPacket.putByte((byte)((isoPlayer.getPrimaryHandItem() != null) ? 1 : 0));
        if (isoPlayer.getPrimaryHandItem() != null) {
            try {
                isoPlayer.getPrimaryHandItem().saveWithSize(startPacket.bb, false);
                if (isoPlayer.getPrimaryHandItem().getVisual() != null) {
                    startPacket.bb.put((byte)1);
                    isoPlayer.getPrimaryHandItem().getVisual().save(startPacket.bb);
                }
                else {
                    startPacket.bb.put((byte)0);
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        PacketTypes.PacketType.Equip.send(udpConnection);
        final ByteBufferWriter startPacket2 = udpConnection.startPacket();
        PacketTypes.PacketType.Equip.doPacket(startPacket2);
        startPacket2.putShort(isoPlayer.OnlineID);
        startPacket2.putByte((byte)1);
        if (isoPlayer.getSecondaryHandItem() == isoPlayer.getPrimaryHandItem() && isoPlayer.getSecondaryHandItem() != null) {
            startPacket2.putByte((byte)2);
        }
        else {
            startPacket2.putByte((byte)((isoPlayer.getSecondaryHandItem() != null) ? 1 : 0));
        }
        if (isoPlayer.getSecondaryHandItem() != null) {
            try {
                isoPlayer.getSecondaryHandItem().saveWithSize(startPacket2.bb, false);
                if (isoPlayer.getSecondaryHandItem().getVisual() != null) {
                    startPacket2.bb.put((byte)1);
                    isoPlayer.getSecondaryHandItem().getVisual().save(startPacket2.bb);
                }
                else {
                    startPacket2.bb.put((byte)0);
                }
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
        PacketTypes.PacketType.Equip.send(udpConnection);
    }
    
    public static void sendZombie(final IsoZombie isoZombie) {
        if (GameServer.bFastForward) {
            return;
        }
    }
    
    public static void SyncCustomLightSwitchSettings(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final byte value = byteBuffer.get();
        final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(int1, int2, int3);
        if (gridSquare != null && value >= 0 && value < gridSquare.getObjects().size()) {
            if (gridSquare.getObjects().get(value) instanceof IsoLightSwitch) {
                ((IsoLightSwitch)gridSquare.getObjects().get(value)).receiveSyncCustomizedSettings(byteBuffer, udpConnection);
            }
            else {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, int1, int2, int3));
            }
        }
        else if (gridSquare != null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(BIII)Ljava/lang/String;, value, int1, int2, int3));
        }
        else {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, int1, int2, int3));
        }
    }
    
    private static void sendAlarmClock_Player(final short n, final long n2, final boolean b, final int n3, final int n4, final boolean b2, final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.SyncAlarmClock.doPacket(startPacket);
        startPacket.putShort(AlarmClock.PacketPlayer);
        startPacket.putShort(n);
        startPacket.putLong(n2);
        startPacket.putByte((byte)(b ? 1 : 0));
        if (!b) {
            startPacket.putInt(n3);
            startPacket.putInt(n4);
            startPacket.putByte((byte)(b2 ? 1 : 0));
        }
        PacketTypes.PacketType.SyncAlarmClock.send(udpConnection);
    }
    
    private static void sendAlarmClock_World(final int n, final int n2, final int n3, final long n4, final boolean b, final int n5, final int n6, final boolean b2, final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.SyncAlarmClock.doPacket(startPacket);
        startPacket.putShort(AlarmClock.PacketWorld);
        startPacket.putInt(n);
        startPacket.putInt(n2);
        startPacket.putInt(n3);
        startPacket.putLong(n4);
        startPacket.putByte((byte)(b ? 1 : 0));
        if (!b) {
            startPacket.putInt(n5);
            startPacket.putInt(n6);
            startPacket.putByte((byte)(b2 ? 1 : 0));
        }
        PacketTypes.PacketType.SyncAlarmClock.send(udpConnection);
    }
    
    static void receiveSyncAlarmClock(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        if (short1 == AlarmClock.PacketPlayer) {
            final short short2 = byteBuffer.getShort();
            final int int1 = byteBuffer.getInt();
            final boolean b = byteBuffer.get() == 1;
            int int2 = 0;
            int int3 = 0;
            boolean b2 = false;
            if (!b) {
                int2 = byteBuffer.getInt();
                int3 = byteBuffer.getInt();
                b2 = (byteBuffer.get() == 1);
            }
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2 != udpConnection) {
                    sendAlarmClock_Player(short2, int1, b, int2, int3, b2, udpConnection2);
                }
            }
            return;
        }
        if (short1 != AlarmClock.PacketWorld) {
            return;
        }
        final int int4 = byteBuffer.getInt();
        final int int5 = byteBuffer.getInt();
        final int int6 = byteBuffer.getInt();
        final int int7 = byteBuffer.getInt();
        final boolean b3 = byteBuffer.get() == 1;
        int int8 = 0;
        int int9 = 0;
        boolean alarmSet = false;
        if (!b3) {
            int8 = byteBuffer.getInt();
            int9 = byteBuffer.getInt();
            alarmSet = (byteBuffer.get() == 1);
        }
        final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(int4, int5, int6);
        if (gridSquare == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, int4, int5, int6));
            return;
        }
        AlarmClock alarmClock = null;
        for (int j = 0; j < gridSquare.getWorldObjects().size(); ++j) {
            final IsoWorldInventoryObject isoWorldInventoryObject = gridSquare.getWorldObjects().get(j);
            if (isoWorldInventoryObject != null && isoWorldInventoryObject.getItem() instanceof AlarmClock && isoWorldInventoryObject.getItem().id == int7) {
                alarmClock = (AlarmClock)isoWorldInventoryObject.getItem();
                break;
            }
        }
        if (alarmClock == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, int4, int5, int6));
        }
        else {
            if (b3) {
                alarmClock.stopRinging();
            }
            else {
                alarmClock.setHour(int8);
                alarmClock.setMinute(int9);
                alarmClock.setAlarmSet(alarmSet);
            }
            for (int k = 0; k < GameServer.udpEngine.connections.size(); ++k) {
                final UdpConnection udpConnection3 = GameServer.udpEngine.connections.get(k);
                if (udpConnection3 != udpConnection) {
                    sendAlarmClock_World(int4, int5, int6, int7, b3, int8, int9, alarmSet, udpConnection3);
                }
            }
        }
    }
    
    static void receiveSyncIsoObject(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        if (!DebugOptions.instance.Network.Server.SyncIsoObject.getValue()) {
            return;
        }
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final byte value = byteBuffer.get();
        final byte value2 = byteBuffer.get();
        final byte value3 = byteBuffer.get();
        if (value2 != 1) {
            return;
        }
        final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(int1, int2, int3);
        if (gridSquare != null && value >= 0 && value < gridSquare.getObjects().size()) {
            gridSquare.getObjects().get(value).syncIsoObject(true, value3, udpConnection, byteBuffer);
        }
        else if (gridSquare != null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(BIII)Ljava/lang/String;, value, int1, int2, int3));
        }
        else {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, int1, int2, int3));
        }
    }
    
    static void receiveSyncIsoObjectReq(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        if (short1 > 50 || short1 <= 0) {
            return;
        }
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.SyncIsoObjectReq.doPacket(startPacket);
        startPacket.putShort(short1);
        for (short n2 = 0; n2 < short1; ++n2) {
            final int int1 = byteBuffer.getInt();
            final int int2 = byteBuffer.getInt();
            final int int3 = byteBuffer.getInt();
            final byte value = byteBuffer.get();
            final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(int1, int2, int3);
            if (gridSquare != null && value >= 0 && value < gridSquare.getObjects().size()) {
                gridSquare.getObjects().get(value).syncIsoObjectSend(startPacket);
            }
            else if (gridSquare != null) {
                startPacket.putInt(gridSquare.getX());
                startPacket.putInt(gridSquare.getY());
                startPacket.putInt(gridSquare.getZ());
                startPacket.putByte(value);
                startPacket.putByte((byte)0);
                startPacket.putByte((byte)0);
            }
            else {
                startPacket.putInt(int1);
                startPacket.putInt(int2);
                startPacket.putInt(int3);
                startPacket.putByte(value);
                startPacket.putByte((byte)2);
                startPacket.putByte((byte)0);
            }
        }
        PacketTypes.PacketType.SyncIsoObjectReq.send(udpConnection);
    }
    
    static void receiveSyncObjects(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final short short1 = byteBuffer.getShort();
        if (short1 == 1) {
            SyncObjectChunkHashes(byteBuffer, udpConnection);
        }
        else if (short1 == 3) {
            SyncObjectsGridSquareRequest(byteBuffer, udpConnection);
        }
        else if (short1 == 5) {
            SyncObjectsRequest(byteBuffer, udpConnection);
        }
    }
    
    public static void SyncObjectChunkHashes(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        final short short1 = byteBuffer.getShort();
        if (short1 > 10 || short1 <= 0) {
            return;
        }
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.SyncObjects.doPacket(startPacket);
        startPacket.putShort((short)2);
        final int position = startPacket.bb.position();
        startPacket.putShort((short)0);
        int n = 0;
        for (short n2 = 0; n2 < short1; ++n2) {
            final int int1 = byteBuffer.getInt();
            final int int2 = byteBuffer.getInt();
            byteBuffer.getLong();
            final IsoChunk chunk = ServerMap.instance.getChunk(int1, int2);
            if (chunk != null) {
                ++n;
                startPacket.putShort((short)chunk.wx);
                startPacket.putShort((short)chunk.wy);
                startPacket.putLong(chunk.getHashCodeObjects());
                final int position2 = startPacket.bb.position();
                startPacket.putShort((short)0);
                int n3 = 0;
                for (int i = int1 * 10; i < int1 * 10 + 10; ++i) {
                    for (int j = int2 * 10; j < int2 * 10 + 10; ++j) {
                        for (int k = 0; k <= 7; ++k) {
                            final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(i, j, k);
                            if (gridSquare == null) {
                                break;
                            }
                            startPacket.putByte((byte)(gridSquare.getX() - chunk.wx * 10));
                            startPacket.putByte((byte)(gridSquare.getY() - chunk.wy * 10));
                            startPacket.putByte((byte)gridSquare.getZ());
                            startPacket.putInt((int)gridSquare.getHashCodeObjects());
                            ++n3;
                        }
                    }
                }
                final int position3 = startPacket.bb.position();
                startPacket.bb.position(position2);
                startPacket.putShort((short)n3);
                startPacket.bb.position(position3);
            }
        }
        final int position4 = startPacket.bb.position();
        startPacket.bb.position(position);
        startPacket.putShort((short)n);
        startPacket.bb.position(position4);
        PacketTypes.PacketType.SyncObjects.send(udpConnection);
    }
    
    public static void SyncObjectChunkHashes(final IsoChunk isoChunk, final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.SyncObjects.doPacket(startPacket);
        startPacket.putShort((short)2);
        startPacket.putShort((short)1);
        startPacket.putShort((short)isoChunk.wx);
        startPacket.putShort((short)isoChunk.wy);
        startPacket.putLong(isoChunk.getHashCodeObjects());
        final int position = startPacket.bb.position();
        startPacket.putShort((short)0);
        int n = 0;
        for (int i = isoChunk.wx * 10; i < isoChunk.wx * 10 + 10; ++i) {
            for (int j = isoChunk.wy * 10; j < isoChunk.wy * 10 + 10; ++j) {
                for (int k = 0; k <= 7; ++k) {
                    final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(i, j, k);
                    if (gridSquare == null) {
                        break;
                    }
                    startPacket.putByte((byte)(gridSquare.getX() - isoChunk.wx * 10));
                    startPacket.putByte((byte)(gridSquare.getY() - isoChunk.wy * 10));
                    startPacket.putByte((byte)gridSquare.getZ());
                    startPacket.putInt((int)gridSquare.getHashCodeObjects());
                    ++n;
                }
            }
        }
        final int position2 = startPacket.bb.position();
        startPacket.bb.position(position);
        startPacket.putShort((short)n);
        startPacket.bb.position(position2);
        PacketTypes.PacketType.SyncObjects.send(udpConnection);
    }
    
    public static void SyncObjectsGridSquareRequest(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        final short short1 = byteBuffer.getShort();
        if (short1 > 100 || short1 <= 0) {
            return;
        }
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.SyncObjects.doPacket(startPacket);
        startPacket.putShort((short)4);
        final int position = startPacket.bb.position();
        startPacket.putShort((short)0);
        int n = 0;
        for (short n2 = 0; n2 < short1; ++n2) {
            final int int1 = byteBuffer.getInt();
            final int int2 = byteBuffer.getInt();
            final byte value = byteBuffer.get();
            final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(int1, int2, value);
            if (gridSquare != null) {
                ++n;
                startPacket.putInt(int1);
                startPacket.putInt(int2);
                startPacket.putByte(value);
                startPacket.putByte((byte)gridSquare.getObjects().size());
                startPacket.putInt(0);
                final int position2 = startPacket.bb.position();
                for (int i = 0; i < gridSquare.getObjects().size(); ++i) {
                    startPacket.putLong(gridSquare.getObjects().get(i).customHashCode());
                }
                final int position3 = startPacket.bb.position();
                startPacket.bb.position(position2 - 4);
                startPacket.putInt(position3);
                startPacket.bb.position(position3);
            }
        }
        final int position4 = startPacket.bb.position();
        startPacket.bb.position(position);
        startPacket.putShort((short)n);
        startPacket.bb.position(position4);
        PacketTypes.PacketType.SyncObjects.send(udpConnection);
    }
    
    public static void SyncObjectsRequest(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        final short short1 = byteBuffer.getShort();
        if (short1 > 100 || short1 <= 0) {
            return;
        }
        for (short n = 0; n < short1; ++n) {
            final int int1 = byteBuffer.getInt();
            final int int2 = byteBuffer.getInt();
            final byte value = byteBuffer.get();
            final long long1 = byteBuffer.getLong();
            final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(int1, int2, value);
            if (gridSquare != null) {
                for (int i = 0; i < gridSquare.getObjects().size(); ++i) {
                    if (gridSquare.getObjects().get(i).customHashCode() == long1) {
                        final ByteBufferWriter startPacket = udpConnection.startPacket();
                        PacketTypes.PacketType.SyncObjects.doPacket(startPacket);
                        startPacket.putShort((short)6);
                        startPacket.putInt(int1);
                        startPacket.putInt(int2);
                        startPacket.putByte(value);
                        startPacket.putLong(long1);
                        startPacket.putByte((byte)gridSquare.getObjects().size());
                        for (int j = 0; j < gridSquare.getObjects().size(); ++j) {
                            startPacket.putLong(gridSquare.getObjects().get(j).customHashCode());
                        }
                        try {
                            gridSquare.getObjects().get(i).writeToRemoteBuffer(startPacket);
                        }
                        catch (Throwable t) {
                            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, t.getMessage()));
                            udpConnection.cancelPacket();
                            break;
                        }
                        PacketTypes.PacketType.SyncObjects.send(udpConnection);
                        break;
                    }
                }
            }
        }
    }
    
    static void receiveSyncDoorKey(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final byte value = byteBuffer.get();
        final int int4 = byteBuffer.getInt();
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
        if (gridSquare != null && value >= 0 && value < gridSquare.getObjects().size()) {
            final IsoObject isoObject = gridSquare.getObjects().get(value);
            if (isoObject instanceof IsoDoor) {
                ((IsoDoor)isoObject).keyId = int4;
                for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                    final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                    if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                        final ByteBufferWriter startPacket = udpConnection2.startPacket();
                        PacketTypes.PacketType.SyncDoorKey.doPacket(startPacket);
                        startPacket.putInt(int1);
                        startPacket.putInt(int2);
                        startPacket.putInt(int3);
                        startPacket.putByte(value);
                        startPacket.putInt(int4);
                        PacketTypes.PacketType.SyncDoorKey.send(udpConnection2);
                    }
                }
                return;
            }
            DebugLog.log(invokedynamic(makeConcatWithConstants:(BIII)Ljava/lang/String;, value, int1, int2, int3));
        }
        else {
            if (gridSquare != null) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(BIII)Ljava/lang/String;, value, int1, int2, int3));
                return;
            }
            DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, int1, int2, int3));
        }
    }
    
    static void receiveSyncThumpable(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final byte value = byteBuffer.get();
        final int int4 = byteBuffer.getInt();
        final byte value2 = byteBuffer.get();
        final int int5 = byteBuffer.getInt();
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
        if (gridSquare != null && value >= 0 && value < gridSquare.getObjects().size()) {
            final IsoObject isoObject = gridSquare.getObjects().get(value);
            if (isoObject instanceof IsoThumpable) {
                final IsoThumpable isoThumpable = (IsoThumpable)isoObject;
                isoThumpable.lockedByCode = int4;
                isoThumpable.lockedByPadlock = (value2 == 1);
                isoThumpable.keyId = int5;
                for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                    final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                    if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                        final ByteBufferWriter startPacket = udpConnection2.startPacket();
                        PacketTypes.PacketType.SyncThumpable.doPacket(startPacket);
                        startPacket.putInt(int1);
                        startPacket.putInt(int2);
                        startPacket.putInt(int3);
                        startPacket.putByte(value);
                        startPacket.putInt(int4);
                        startPacket.putByte(value2);
                        startPacket.putInt(int5);
                        PacketTypes.PacketType.SyncThumpable.send(udpConnection2);
                    }
                }
                return;
            }
            DebugLog.log(invokedynamic(makeConcatWithConstants:(BIII)Ljava/lang/String;, value, int1, int2, int3));
        }
        else {
            if (gridSquare != null) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(BIII)Ljava/lang/String;, value, int1, int2, int3));
                return;
            }
            DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, int1, int2, int3));
        }
    }
    
    static void receiveRemoveItemFromSquare(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
        if (gridSquare != null && int4 >= 0 && int4 < gridSquare.getObjects().size()) {
            final IsoObject isoObject = gridSquare.getObjects().get(int4);
            if (!(isoObject instanceof IsoWorldInventoryObject)) {
                IsoRegions.setPreviousFlags(gridSquare);
            }
            DebugLog.log(DebugType.Objects, invokedynamic(makeConcatWithConstants:(Lzombie/iso/IsoObject;IIII)Ljava/lang/String;, isoObject, int4, int1, int2, int3));
            if (isoObject instanceof IsoWorldInventoryObject) {
                LoggerManager.getLogger("item").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;IIILjava/lang/String;)Ljava/lang/String;, udpConnection.idStr, udpConnection.username, int1, int2, int3, ((IsoWorldInventoryObject)isoObject).getItem().getFullType()));
            }
            else {
                String s = (isoObject.getName() != null) ? isoObject.getName() : isoObject.getObjectName();
                if (isoObject.getSprite() != null && isoObject.getSprite().getName() != null) {
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, isoObject.getSprite().getName());
                }
                LoggerManager.getLogger("map").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;III)Ljava/lang/String;, udpConnection.idStr, udpConnection.username, s, int1, int2, int3));
            }
            if (isoObject.isTableSurface()) {
                for (int i = int4 + 1; i < gridSquare.getObjects().size(); ++i) {
                    final IsoObject isoObject2 = gridSquare.getObjects().get(i);
                    if (isoObject2.isTableTopObject() || isoObject2.isTableSurface()) {
                        isoObject2.setRenderYOffset(isoObject2.getRenderYOffset() - isoObject.getSurfaceOffset());
                    }
                }
            }
            if (!(isoObject instanceof IsoWorldInventoryObject)) {
                LuaEventManager.triggerEvent("OnObjectAboutToBeRemoved", isoObject);
            }
            if (!gridSquare.getObjects().contains(isoObject)) {
                throw new IllegalArgumentException("OnObjectAboutToBeRemoved not allowed to remove the object");
            }
            isoObject.removeFromWorld();
            isoObject.removeFromSquare();
            gridSquare.RecalcAllWithNeighbours(true);
            if (!(isoObject instanceof IsoWorldInventoryObject)) {
                IsoWorld.instance.CurrentCell.checkHaveRoof(int1, int2);
                MapCollisionData.instance.squareChanged(gridSquare);
                PolygonalMap2.instance.squareChanged(gridSquare);
                ServerMap.instance.physicsCheck(int1, int2);
                IsoRegions.squareChanged(gridSquare, true);
            }
            for (int j = 0; j < GameServer.udpEngine.connections.size(); ++j) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(j);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.RemoveItemFromSquare.doPacket(startPacket);
                    startPacket.putInt(int1);
                    startPacket.putInt(int2);
                    startPacket.putInt(int3);
                    startPacket.putInt(int4);
                    PacketTypes.PacketType.RemoveItemFromSquare.send(udpConnection2);
                }
            }
        }
    }
    
    public static int RemoveItemFromMap(final IsoObject isoObject) {
        final int x = isoObject.getSquare().getX();
        final int y = isoObject.getSquare().getY();
        final int z = isoObject.getSquare().getZ();
        final int index = isoObject.getSquare().getObjects().indexOf(isoObject);
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(x, y, z);
        if (gridSquare != null && !(isoObject instanceof IsoWorldInventoryObject)) {
            IsoRegions.setPreviousFlags(gridSquare);
        }
        LuaEventManager.triggerEvent("OnObjectAboutToBeRemoved", isoObject);
        if (!isoObject.getSquare().getObjects().contains(isoObject)) {
            throw new IllegalArgumentException("OnObjectAboutToBeRemoved not allowed to remove the object");
        }
        isoObject.removeFromWorld();
        isoObject.removeFromSquare();
        if (gridSquare != null) {
            gridSquare.RecalcAllWithNeighbours(true);
        }
        if (!(isoObject instanceof IsoWorldInventoryObject)) {
            IsoWorld.instance.CurrentCell.checkHaveRoof(x, y);
            MapCollisionData.instance.squareChanged(gridSquare);
            PolygonalMap2.instance.squareChanged(gridSquare);
            ServerMap.instance.physicsCheck(x, y);
            IsoRegions.squareChanged(gridSquare, true);
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.RelevantTo((float)x, (float)y)) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.RemoveItemFromSquare.doPacket(startPacket);
                startPacket.putInt(x);
                startPacket.putInt(y);
                startPacket.putInt(z);
                startPacket.putInt(index);
                PacketTypes.PacketType.RemoveItemFromSquare.send(udpConnection);
            }
        }
        return index;
    }
    
    public static void sendBloodSplatter(final HandWeapon handWeapon, final float n, final float n2, final float n3, final Vector2 vector2, final boolean b, final boolean b2) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypes.PacketType.BloodSplatter.doPacket(startPacket);
            startPacket.putUTF((handWeapon != null) ? handWeapon.getType() : "");
            startPacket.putFloat(n);
            startPacket.putFloat(n2);
            startPacket.putFloat(n3);
            startPacket.putFloat(vector2.getX());
            startPacket.putFloat(vector2.getY());
            startPacket.putByte((byte)(b ? 1 : 0));
            startPacket.putByte((byte)(b2 ? 1 : 0));
            byte b3 = 0;
            if (handWeapon != null) {
                b3 = (byte)Math.max(handWeapon.getSplatNumber(), 1);
            }
            startPacket.putByte(b3);
            PacketTypes.PacketType.BloodSplatter.send(udpConnection);
        }
    }
    
    static void receiveAddItemToMap(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final IsoObject fromBuffer = WorldItemTypes.createFromBuffer(byteBuffer);
        if (fromBuffer instanceof IsoFire && ServerOptions.instance.NoFire.getValue()) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, udpConnection.username));
            return;
        }
        fromBuffer.loadFromRemoteBuffer(byteBuffer);
        if (fromBuffer.square != null) {
            DebugLog.log(DebugType.Objects, invokedynamic(makeConcatWithConstants:(Lzombie/iso/IsoObject;IFFF)Ljava/lang/String;, fromBuffer, fromBuffer.getObjectIndex(), fromBuffer.getX(), fromBuffer.getY(), fromBuffer.getZ()));
            if (fromBuffer instanceof IsoWorldInventoryObject) {
                LoggerManager.getLogger("item").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;IIILjava/lang/String;)Ljava/lang/String;, udpConnection.idStr, udpConnection.username, (int)fromBuffer.getX(), (int)fromBuffer.getY(), (int)fromBuffer.getZ(), ((IsoWorldInventoryObject)fromBuffer).getItem().getFullType()));
            }
            else {
                String s = (fromBuffer.getName() != null) ? fromBuffer.getName() : fromBuffer.getObjectName();
                if (fromBuffer.getSprite() != null && fromBuffer.getSprite().getName() != null) {
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, fromBuffer.getSprite().getName());
                }
                LoggerManager.getLogger("map").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;FFF)Ljava/lang/String;, udpConnection.idStr, udpConnection.username, s, fromBuffer.getX(), fromBuffer.getY(), fromBuffer.getZ()));
            }
            fromBuffer.addToWorld();
            fromBuffer.square.RecalcProperties();
            if (!(fromBuffer instanceof IsoWorldInventoryObject)) {
                fromBuffer.square.restackSheetRope();
                IsoWorld.instance.CurrentCell.checkHaveRoof(fromBuffer.square.getX(), fromBuffer.square.getY());
                MapCollisionData.instance.squareChanged(fromBuffer.square);
                PolygonalMap2.instance.squareChanged(fromBuffer.square);
                ServerMap.instance.physicsCheck(fromBuffer.square.x, fromBuffer.square.y);
                IsoRegions.squareChanged(fromBuffer.square);
            }
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.RelevantTo((float)fromBuffer.square.x, (float)fromBuffer.square.y)) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.AddItemToMap.doPacket(startPacket);
                    fromBuffer.writeToRemoteBuffer(startPacket);
                    PacketTypes.PacketType.AddItemToMap.send(udpConnection2);
                }
            }
            if (!(fromBuffer instanceof IsoWorldInventoryObject)) {
                LuaEventManager.triggerEvent("OnObjectAdded", fromBuffer);
            }
            else {
                ((IsoWorldInventoryObject)fromBuffer).dropTime = GameTime.getInstance().getWorldAgeHours();
            }
        }
        else if (GameServer.bDebug) {
            DebugLog.log("AddItemToMap: sq is null");
        }
    }
    
    public static void disconnect(final UdpConnection udpConnection) {
        if (udpConnection.playerDownloadServer != null) {
            try {
                udpConnection.playerDownloadServer.destroy();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            udpConnection.playerDownloadServer = null;
        }
        for (int i = 0; i < 4; ++i) {
            final IsoPlayer isoPlayer = udpConnection.players[i];
            if (isoPlayer != null) {
                ChatServer.getInstance().disconnectPlayer(udpConnection.playerIDs[i]);
                disconnectPlayer(isoPlayer, udpConnection);
            }
            udpConnection.usernames[i] = null;
            udpConnection.players[i] = null;
            udpConnection.playerIDs[i] = -1;
            udpConnection.ReleventPos[i] = null;
            udpConnection.connectArea[i] = null;
        }
        for (int j = 0; j < GameServer.udpEngine.getMaxConnections(); ++j) {
            if (GameServer.SlotToConnection[j] == udpConnection) {
                GameServer.SlotToConnection[j] = null;
            }
        }
        final Iterator<Map.Entry<Short, Long>> iterator = GameServer.IDToAddressMap.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue() == udpConnection.getConnectedGUID()) {
                iterator.remove();
            }
        }
        if (!SteamUtils.isSteamModeEnabled()) {
            PublicServerUtil.updatePlayers();
        }
        if (CoopSlave.instance != null && udpConnection.isCoopHost) {
            DebugLog.log("Host user disconnected, stopping the server");
            ServerMap.instance.QueueQuit();
        }
    }
    
    public static void addIncoming(final short n, final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        ZomboidNetData zomboidNetData;
        if (byteBuffer.limit() > 2048) {
            zomboidNetData = ZomboidNetDataPool.instance.getLong(byteBuffer.limit());
        }
        else {
            zomboidNetData = ZomboidNetDataPool.instance.get();
        }
        zomboidNetData.read(n, byteBuffer, udpConnection);
        zomboidNetData.time = System.currentTimeMillis();
        if (zomboidNetData.type == PacketTypes.PacketType.PlayerUpdate.getId() || zomboidNetData.type == PacketTypes.PacketType.PlayerUpdateReliable.getId()) {
            GameServer.MainLoopPlayerUpdateQ.add(zomboidNetData);
        }
        else if (zomboidNetData.type == PacketTypes.PacketType.VehiclesUnreliable.getId() || zomboidNetData.type == PacketTypes.PacketType.Vehicles.getId()) {
            if (zomboidNetData.buffer.get(0) == 9) {
                GameServer.MainLoopNetDataQ.add(zomboidNetData);
            }
            else {
                GameServer.MainLoopNetDataHighPriorityQ.add(zomboidNetData);
            }
        }
        else {
            GameServer.MainLoopNetDataHighPriorityQ.add(zomboidNetData);
        }
    }
    
    public static void smashWindow(final IsoWindow isoWindow, final int n) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.RelevantTo(isoWindow.getX(), isoWindow.getY())) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.SmashWindow.doPacket(startPacket);
                startPacket.putInt(isoWindow.square.getX());
                startPacket.putInt(isoWindow.square.getY());
                startPacket.putInt(isoWindow.square.getZ());
                startPacket.putByte((byte)isoWindow.square.getObjects().indexOf(isoWindow));
                startPacket.putByte((byte)n);
                PacketTypes.PacketType.SmashWindow.send(udpConnection);
            }
        }
    }
    
    public static boolean doSendZombies() {
        return GameServer.SendZombies == 0;
    }
    
    static void receiveHitCharacter(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        try {
            final HitCharacterPacket process = HitCharacterPacket.process(byteBuffer);
            if (process != null) {
                process.parse(byteBuffer);
                if (Core.bDebug) {
                    if (!DebugLog.isEnabled(DebugType.Damage)) {
                        DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, process.getHitDescription()));
                    }
                    DebugLog.log(DebugType.Damage, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, process.getDescription()));
                }
                sendHitCharacter(process, udpConnection);
                process.tryProcess();
            }
        }
        catch (Exception ex) {
            DebugLog.Multiplayer.printException(ex, "ReceiveHitCharacter: failed", LogSeverity.Error);
        }
    }
    
    private static void sendHitCharacter(final HitCharacterPacket hitCharacterPacket, final UdpConnection udpConnection) {
        if (Core.bDebug) {
            if (!DebugLog.isEnabled(DebugType.Damage)) {
                DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hitCharacterPacket.getHitDescription()));
            }
            DebugLog.log(DebugType.Damage, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, hitCharacterPacket.getDescription()));
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && hitCharacterPacket.isRelevant(udpConnection2)) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.HitCharacter.doPacket(startPacket);
                hitCharacterPacket.write(startPacket);
                PacketTypes.PacketType.HitCharacter.send(udpConnection2);
            }
        }
    }
    
    static void receiveZombieDeath(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        try {
            final DeadZombiePacket deadZombiePacket = new DeadZombiePacket();
            deadZombiePacket.parse(byteBuffer);
            if (Core.bDebug) {
                if (!DebugLog.isEnabled(DebugType.Death)) {
                    DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, deadZombiePacket.getDeathDescription()));
                }
                DebugLog.log(DebugType.Death, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, deadZombiePacket.getDescription()));
            }
            if (deadZombiePacket.isConsistent()) {
                if (deadZombiePacket.getZombie().isReanimatedPlayer()) {
                    sendZombieDeath(deadZombiePacket.getZombie());
                }
                else {
                    sendZombieDeath(deadZombiePacket);
                }
                deadZombiePacket.process();
            }
        }
        catch (Exception ex) {
            DebugLog.Multiplayer.printException(ex, "ReceiveZombieDeath: failed", LogSeverity.Error);
        }
    }
    
    public static void sendZombieDeath(final IsoZombie isoZombie) {
        try {
            final DeadZombiePacket deadZombiePacket = new DeadZombiePacket();
            deadZombiePacket.set(isoZombie);
            sendZombieDeath(deadZombiePacket);
        }
        catch (Exception ex) {
            DebugLog.Multiplayer.printException(ex, "SendZombieDeath: failed", LogSeverity.Error);
        }
    }
    
    private static void sendZombieDeath(final DeadZombiePacket deadZombiePacket) {
        try {
            if (Core.bDebug) {
                if (!DebugLog.isEnabled(DebugType.Death)) {
                    DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, deadZombiePacket.getDeathDescription()));
                }
                DebugLog.log(DebugType.Death, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, deadZombiePacket.getDescription()));
            }
            for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
                final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
                if (udpConnection.RelevantTo(deadZombiePacket.getZombie().getX(), deadZombiePacket.getZombie().getY())) {
                    final ByteBufferWriter startPacket = udpConnection.startPacket();
                    PacketTypes.PacketType.ZombieDeath.doPacket(startPacket);
                    try {
                        deadZombiePacket.write(startPacket);
                        PacketTypes.PacketType.ZombieDeath.send(udpConnection);
                    }
                    catch (Exception ex) {
                        udpConnection.cancelPacket();
                        DebugLog.Multiplayer.printException(ex, "SendZombieDeath: failed", LogSeverity.Error);
                    }
                }
            }
        }
        catch (Exception ex2) {
            DebugLog.Multiplayer.printException(ex2, "SendZombieDeath: failed", LogSeverity.Error);
        }
    }
    
    static void receivePlayerDeath(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        try {
            final DeadPlayerPacket deadPlayerPacket = new DeadPlayerPacket();
            deadPlayerPacket.parse(byteBuffer);
            if (Core.bDebug) {
                if (!DebugLog.isEnabled(DebugType.Death)) {
                    DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, deadPlayerPacket.getDeathDescription()));
                }
                DebugLog.log(DebugType.Death, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, deadPlayerPacket.getDescription()));
            }
            final String username = deadPlayerPacket.getPlayer().username;
            ChatServer.getInstance().disconnectPlayer(deadPlayerPacket.getPlayer().getOnlineID());
            ServerWorldDatabase.instance.saveTransactionID(username, 0);
            deadPlayerPacket.getPlayer().setTransactionID(0);
            GameServer.transactionIDMap.put(username, 0);
            if (deadPlayerPacket.getPlayer().accessLevel.equals("") && !ServerOptions.instance.Open.getValue() && ServerOptions.instance.DropOffWhiteListAfterDeath.getValue()) {
                try {
                    ServerWorldDatabase.instance.removeUser(username);
                }
                catch (SQLException ex) {
                    DebugLog.Multiplayer.printException(ex, "ReceivePlayerDeath: db failed", LogSeverity.Warning);
                }
            }
            if (deadPlayerPacket.isConsistent()) {
                sendPlayerDeath(deadPlayerPacket, udpConnection);
                deadPlayerPacket.process();
            }
            deadPlayerPacket.getPlayer().setStateMachineLocked(true);
        }
        catch (Exception ex2) {
            DebugLog.Multiplayer.printException(ex2, "ReceivePlayerDeath: failed", LogSeverity.Error);
        }
    }
    
    public static void sendPlayerDeath(final DeadPlayerPacket deadPlayerPacket, final UdpConnection udpConnection) {
        if (Core.bDebug) {
            if (!DebugLog.isEnabled(DebugType.Death)) {
                DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, deadPlayerPacket.getDeathDescription()));
            }
            DebugLog.log(DebugType.Death, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, deadPlayerPacket.getDescription()));
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection == null || udpConnection.getConnectedGUID() != udpConnection2.getConnectedGUID()) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.PlayerDeath.doPacket(startPacket);
                deadPlayerPacket.write(startPacket);
                PacketTypes.PacketType.PlayerDeath.send(udpConnection2);
            }
        }
    }
    
    static void receivePlayerDamage(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        try {
            final short short1 = byteBuffer.getShort();
            final float float1 = byteBuffer.getFloat();
            if (Core.bDebug) {
                if (!DebugLog.isEnabled(DebugType.Damage)) {
                    DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, short1));
                }
                DebugLog.log(DebugType.Damage, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, short1));
            }
            final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(short1);
            if (isoPlayer != null) {
                isoPlayer.getBodyDamage().load(byteBuffer, IsoWorld.getWorldVersion());
                isoPlayer.getStats().setPain(float1);
                sendPlayerDamage(isoPlayer, udpConnection);
            }
        }
        catch (Exception ex) {
            DebugLog.Multiplayer.printException(ex, "ReceivePlayerDamage: failed", LogSeverity.Error);
        }
    }
    
    public static void sendPlayerDamage(final IsoPlayer isoPlayer, final UdpConnection udpConnection) {
        if (Core.bDebug) {
            if (!DebugLog.isEnabled(DebugType.Damage)) {
                DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, isoPlayer.getOnlineID()));
            }
            DebugLog.log(DebugType.Damage, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, isoPlayer.getOnlineID()));
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection.getConnectedGUID() != udpConnection2.getConnectedGUID()) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.PlayerDamage.doPacket(startPacket);
                try {
                    startPacket.putShort(isoPlayer.getOnlineID());
                    startPacket.putFloat(isoPlayer.getStats().getPain());
                    isoPlayer.getBodyDamage().save(startPacket.bb);
                    PacketTypes.PacketType.PlayerDamage.send(udpConnection2);
                }
                catch (Exception ex) {
                    udpConnection2.cancelPacket();
                    DebugLog.Multiplayer.printException(ex, "SendPlayerDamage: failed", LogSeverity.Error);
                }
            }
        }
    }
    
    static void receiveSyncInjuries(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        try {
            final SyncInjuriesPacket syncInjuriesPacket = new SyncInjuriesPacket();
            syncInjuriesPacket.parse(byteBuffer);
            if (Core.bDebug) {
                final String format = String.format("Receive: %s", syncInjuriesPacket.getDescription());
                if (!DebugLog.isEnabled(DebugType.Damage)) {
                    DebugLog.log(DebugType.Multiplayer, format);
                }
                DebugLog.log(DebugType.Damage, format);
            }
            final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(syncInjuriesPacket.id);
            if (isoPlayer != null) {
                syncInjuriesPacket.process(isoPlayer);
                sendPlayerInjuries(isoPlayer, udpConnection);
            }
        }
        catch (Exception ex) {
            DebugLog.Multiplayer.printException(ex, "ReceivePlayerInjuries: failed", LogSeverity.Error);
        }
    }
    
    private static void sendPlayerInjuries(final IsoPlayer isoPlayer, final UdpConnection udpConnection) {
        final SyncInjuriesPacket syncInjuriesPacket = new SyncInjuriesPacket();
        syncInjuriesPacket.set(isoPlayer);
        if (Core.bDebug) {
            final String format = String.format("Send: %s", syncInjuriesPacket.getDescription());
            if (!DebugLog.isEnabled(DebugType.Damage)) {
                DebugLog.log(DebugType.Multiplayer, format);
            }
            DebugLog.log(DebugType.Damage, format);
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                sendPlayerInjuries(udpConnection2, syncInjuriesPacket);
            }
        }
    }
    
    private static void sendPlayerInjuries(final UdpConnection udpConnection, final SyncInjuriesPacket syncInjuriesPacket) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.SyncInjuries.doPacket(startPacket);
        syncInjuriesPacket.write(startPacket);
        PacketTypes.PacketType.SyncInjuries.send(udpConnection);
    }
    
    static void receiveKeepAlive(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        MPDebugInfo.instance.serverPacket(byteBuffer, udpConnection);
    }
    
    static void receiveRemoveCorpseFromMap(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        final short short1 = byteBuffer.getShort();
        if (Core.bDebug) {
            final String format = String.format("ReceiveRemoveCorpse: id=%d, index=%d, pos=( %d ; %d ; %d )", short1, int4, int1, int2, int3);
            if (!DebugLog.isEnabled(DebugType.Death)) {
                DebugLog.log(DebugType.Multiplayer, format);
            }
            DebugLog.log(DebugType.Death, format);
        }
        final IsoGridSquare gridSquare = ServerMap.instance.getGridSquare(int1, int2, int3);
        if (gridSquare == null) {
            DebugLog.Multiplayer.error((Object)"ReceiveRemoveCorpse: incorrect square");
            return;
        }
        if (int4 >= 0 && int4 < gridSquare.getStaticMovingObjects().size()) {
            gridSquare.removeCorpse((IsoDeadBody)gridSquare.getStaticMovingObjects().get(int4), true);
            sendRemoveCorpseFromMap(int1, int2, int3, int4, short1, udpConnection);
        }
        else {
            DebugLog.Multiplayer.error((Object)"ReceiveRemoveCorpse: no corpse on square");
        }
    }
    
    private static void sendRemoveCorpseFromMap(final int i, final int j, final int k, final int l, final short s, final UdpConnection udpConnection) {
        if (Core.bDebug) {
            final String format = String.format("SendRemoveCorpse: id=%d, index=%d, pos=( %d ; %d ; %d )", s, l, i, j, k);
            if (!DebugLog.isEnabled(DebugType.Death)) {
                DebugLog.log(DebugType.Multiplayer, format);
            }
            DebugLog.log(DebugType.Death, format);
        }
        for (int n = 0; n < GameServer.udpEngine.connections.size(); ++n) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(n);
            if (udpConnection == null || (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.RelevantTo((float)i, (float)j))) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.RemoveCorpseFromMap.doPacket(startPacket);
                startPacket.putInt(i);
                startPacket.putInt(j);
                startPacket.putInt(k);
                startPacket.putInt(l);
                startPacket.putShort(s);
                PacketTypes.PacketType.RemoveCorpseFromMap.send(udpConnection2);
            }
        }
    }
    
    public static void sendRemoveCorpseFromMap(final IsoDeadBody o) {
        sendRemoveCorpseFromMap(o.getSquare().getX(), o.getSquare().getY(), o.getSquare().getZ(), o.getSquare().getStaticMovingObjects().indexOf(o), o.getOnlineID(), null);
    }
    
    static void receiveEventPacket(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        try {
            final EventPacket eventPacket = new EventPacket();
            eventPacket.parse(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, eventPacket.getDescription()));
            }
            for (final UdpConnection udpConnection2 : GameServer.udpEngine.connections) {
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && eventPacket.isRelevant(udpConnection2)) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.EventPacket.doPacket(startPacket);
                    eventPacket.write(startPacket);
                    PacketTypes.PacketType.EventPacket.send(udpConnection2);
                }
            }
        }
        catch (Exception ex) {
            DebugLog.Multiplayer.printException(ex, "ReceiveEventUpdate: failed", LogSeverity.Error);
        }
    }
    
    static void receiveActionPacket(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        try {
            final ActionPacket actionPacket = new ActionPacket();
            actionPacket.parse(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, actionPacket.getDescription()));
            }
            for (final UdpConnection udpConnection2 : GameServer.udpEngine.connections) {
                if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && actionPacket.isRelevant(udpConnection2)) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.ActionPacket.doPacket(startPacket);
                    actionPacket.write(startPacket);
                    PacketTypes.PacketType.ActionPacket.send(udpConnection2);
                }
            }
        }
        catch (Exception ex) {
            DebugLog.Multiplayer.printException(ex, "ReceiveAction: failed", LogSeverity.Error);
        }
    }
    
    static void receiveKillZombie(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        try {
            final short short1 = byteBuffer.getShort();
            final boolean b = byteBuffer.get() != 0;
            if (Core.bDebug) {
                final String format = String.format("ReceiveKillZombie: id=%d, isFallOnFront=%b", short1, b);
                if (!DebugLog.isEnabled(DebugType.Death)) {
                    DebugLog.log(DebugType.Multiplayer, format);
                }
                DebugLog.log(DebugType.Death, format);
            }
            final IsoZombie value = ServerMap.instance.ZombieMap.get(short1);
            if (value != null) {
                value.setFallOnFront(b);
                value.becomeCorpse();
            }
            else {
                DebugLog.Multiplayer.error((Object)"ReceiveKillZombie: zombie not found");
            }
        }
        catch (Exception ex) {
            DebugLog.Multiplayer.printException(ex, "ReceiveKillZombie: failed", LogSeverity.Error);
        }
    }
    
    public static void sendKillZombie(final IsoZombie isoZombie) {
        if (Core.bDebug) {
            if (!DebugLog.isEnabled(DebugType.Death)) {
                DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, isoZombie.getOnlineID()));
            }
            DebugLog.log(DebugType.Death, invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, isoZombie.getOnlineID()));
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (udpConnection.RelevantTo(isoZombie.x, isoZombie.y)) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.KillZombie.doPacket(startPacket);
                startPacket.putShort(isoZombie.getOnlineID());
                PacketTypes.PacketType.KillZombie.send(udpConnection);
            }
        }
    }
    
    public static void receiveEatBody(final ByteBuffer src, final UdpConnection udpConnection, final short n) {
        try {
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, "ReceiveEatBody");
            }
            final short short1 = src.getShort();
            final IsoZombie value = ServerMap.instance.ZombieMap.get(short1);
            if (value == null) {
                DebugLog.Multiplayer.error(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, short1));
                return;
            }
            for (final UdpConnection udpConnection2 : GameServer.udpEngine.connections) {
                if (udpConnection2.RelevantTo(value.x, value.y)) {
                    if (Core.bDebug) {
                        DebugLog.log(DebugType.Multiplayer, "SendEatBody");
                    }
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.EatBody.doPacket(startPacket);
                    src.position(0);
                    startPacket.bb.put(src);
                    PacketTypes.PacketType.EatBody.send(udpConnection2);
                }
            }
        }
        catch (Exception ex) {
            DebugLog.Multiplayer.printException(ex, "ReceiveEatBody: failed", LogSeverity.Error);
        }
    }
    
    public static void receiveThump(final ByteBuffer src, final UdpConnection udpConnection, final short n) {
        try {
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, "ReceiveThump");
            }
            final short short1 = src.getShort();
            final IsoZombie value = ServerMap.instance.ZombieMap.get(short1);
            if (value == null) {
                DebugLog.Multiplayer.error(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, short1));
                return;
            }
            for (final UdpConnection udpConnection2 : GameServer.udpEngine.connections) {
                if (udpConnection2.RelevantTo(value.x, value.y)) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.Thump.doPacket(startPacket);
                    src.position(0);
                    startPacket.bb.put(src);
                    PacketTypes.PacketType.Thump.send(udpConnection2);
                }
            }
        }
        catch (Exception ex) {
            DebugLog.Multiplayer.printException(ex, "ReceiveEatBody: failed", LogSeverity.Error);
        }
    }
    
    public static void sendWorldSound(final UdpConnection udpConnection, final WorldSoundManager.WorldSound worldSound) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.WorldSound.doPacket(startPacket);
        try {
            startPacket.putInt(worldSound.x);
            startPacket.putInt(worldSound.y);
            startPacket.putInt(worldSound.z);
            startPacket.putInt(worldSound.radius);
            startPacket.putInt(worldSound.volume);
            startPacket.putByte((byte)(worldSound.stresshumans ? 1 : 0));
            startPacket.putFloat(worldSound.zombieIgnoreDist);
            startPacket.putFloat(worldSound.stressMod);
            startPacket.putByte((byte)(worldSound.sourceIsZombie ? 1 : 0));
            PacketTypes.PacketType.WorldSound.send(udpConnection);
        }
        catch (Exception ex) {
            DebugLog.Sound.printException(ex, "SendWorldSound: failed", LogSeverity.Error);
            udpConnection.cancelPacket();
        }
    }
    
    public static void sendWorldSound(final WorldSoundManager.WorldSound worldSound, final UdpConnection udpConnection) {
        if (Core.bDebug) {
            if (!DebugLog.isEnabled(DebugType.Sound)) {
                DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(IIII)Ljava/lang/String;, worldSound.x, worldSound.y, worldSound.z, worldSound.radius));
            }
            DebugLog.log(DebugType.Sound, invokedynamic(makeConcatWithConstants:(IIII)Ljava/lang/String;, worldSound.x, worldSound.y, worldSound.z, worldSound.radius));
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection == null || udpConnection.getConnectedGUID() != udpConnection2.getConnectedGUID()) {
                if (udpConnection2.isFullyConnected()) {
                    if (getAnyPlayerFromConnection(udpConnection2) != null && udpConnection2.RelevantTo((float)worldSound.x, (float)worldSound.y, (float)worldSound.radius)) {
                        sendWorldSound(udpConnection2, worldSound);
                    }
                }
            }
        }
    }
    
    static void receiveWorldSound(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        final int int5 = byteBuffer.getInt();
        final boolean b = byteBuffer.get() == 1;
        final float float1 = byteBuffer.getFloat();
        final float float2 = byteBuffer.getFloat();
        final boolean b2 = byteBuffer.get() == 1;
        if (Core.bDebug) {
            if (!DebugLog.isEnabled(DebugType.Sound)) {
                DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(IIII)Ljava/lang/String;, int1, int2, int3, int4));
            }
            DebugLog.log(DebugType.Sound, invokedynamic(makeConcatWithConstants:(IIII)Ljava/lang/String;, int1, int2, int3, int4));
        }
        final WorldSoundManager.WorldSound addSound = WorldSoundManager.instance.addSound(null, int1, int2, int3, int4, int5, b, float1, float2, b2, false, true);
        if (addSound != null) {
            sendWorldSound(addSound, udpConnection);
        }
    }
    
    private static void sendStartRain(final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.StartRain.doPacket(startPacket);
        startPacket.putInt(RainManager.randRainMin);
        startPacket.putInt(RainManager.randRainMax);
        startPacket.putFloat(RainManager.RainDesiredIntensity);
        PacketTypes.PacketType.StartRain.send(udpConnection);
    }
    
    public static void startRain() {
        if (GameServer.udpEngine == null) {
            return;
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            sendStartRain(GameServer.udpEngine.connections.get(i));
        }
    }
    
    private static void sendStopRain(final UdpConnection udpConnection) {
        PacketTypes.PacketType.StopRain.doPacket(udpConnection.startPacket());
        PacketTypes.PacketType.StopRain.send(udpConnection);
    }
    
    public static void stopRain() {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            sendStopRain(GameServer.udpEngine.connections.get(i));
        }
    }
    
    private static void sendWeather(final UdpConnection udpConnection) {
        final GameTime instance = GameTime.getInstance();
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.Weather.doPacket(startPacket);
        startPacket.putByte((byte)instance.getDawn());
        startPacket.putByte((byte)instance.getDusk());
        startPacket.putByte((byte)(instance.isThunderDay() ? 1 : 0));
        startPacket.putFloat(instance.Moon);
        startPacket.putFloat(instance.getAmbientMin());
        startPacket.putFloat(instance.getAmbientMax());
        startPacket.putFloat(instance.getViewDistMin());
        startPacket.putFloat(instance.getViewDistMax());
        startPacket.putFloat(IsoWorld.instance.getGlobalTemperature());
        startPacket.putUTF(IsoWorld.instance.getWeather());
        ErosionMain.getInstance().sendState(startPacket.bb);
        PacketTypes.PacketType.Weather.send(udpConnection);
    }
    
    public static void sendWeather() {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            sendWeather(GameServer.udpEngine.connections.get(i));
        }
    }
    
    private static void syncClock(final UdpConnection udpConnection) {
        final GameTime instance = GameTime.getInstance();
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.SyncClock.doPacket(startPacket);
        startPacket.putBoolean(GameServer.bFastForward);
        startPacket.putFloat(instance.getTimeOfDay());
        PacketTypes.PacketType.SyncClock.send(udpConnection);
    }
    
    public static void syncClock() {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            syncClock(GameServer.udpEngine.connections.get(i));
        }
    }
    
    public static void sendServerCommand(final String s, final String s2, final KahluaTable kahluaTable, final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.ClientCommand.doPacket(startPacket);
        startPacket.putUTF(s);
        startPacket.putUTF(s2);
        if (kahluaTable == null || kahluaTable.isEmpty()) {
            startPacket.putByte((byte)0);
        }
        else {
            startPacket.putByte((byte)1);
            try {
                final KahluaTableIterator iterator = kahluaTable.iterator();
                while (iterator.advance()) {
                    if (!TableNetworkUtils.canSave(iterator.getKey(), iterator.getValue())) {
                        DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;, iterator.getKey(), iterator.getValue()));
                    }
                }
                TableNetworkUtils.save(kahluaTable, startPacket.bb);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        PacketTypes.PacketType.ClientCommand.send(udpConnection);
    }
    
    public static void sendServerCommand(final String s, final String s2, final KahluaTable kahluaTable) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            sendServerCommand(s, s2, kahluaTable, GameServer.udpEngine.connections.get(i));
        }
    }
    
    public static void sendServerCommandV(final String s, final String s2, final Object... array) {
        if (array.length == 0) {
            sendServerCommand(s, s2, null);
            return;
        }
        if (array.length % 2 != 0) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, s, s2));
            return;
        }
        final KahluaTable table = LuaManager.platform.newTable();
        for (int i = 0; i < array.length; i += 2) {
            final Object o = array[i + 1];
            if (o instanceof Float) {
                table.rawset(array[i], (Object)(double)o);
            }
            else if (o instanceof Integer) {
                table.rawset(array[i], (Object)(double)o);
            }
            else if (o instanceof Short) {
                table.rawset(array[i], (Object)(double)o);
            }
            else {
                table.rawset(array[i], o);
            }
        }
        sendServerCommand(s, s2, table);
    }
    
    public static void sendServerCommand(final IsoPlayer isoPlayer, final String s, final String s2, final KahluaTable kahluaTable) {
        if (!GameServer.PlayerToAddressMap.containsKey(isoPlayer)) {
            return;
        }
        final UdpConnection activeConnection = GameServer.udpEngine.getActiveConnection(GameServer.PlayerToAddressMap.get(isoPlayer));
        if (activeConnection == null) {
            return;
        }
        sendServerCommand(s, s2, kahluaTable, activeConnection);
    }
    
    public static ArrayList<IsoPlayer> getPlayers() {
        final ArrayList<IsoPlayer> list = new ArrayList<IsoPlayer>();
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            for (int j = 0; j < 4; ++j) {
                final IsoPlayer e = udpConnection.players[j];
                if (e != null && e.OnlineID != -1) {
                    list.add(e);
                }
            }
        }
        return list;
    }
    
    public static int getPlayerCount() {
        int n = 0;
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            for (int j = 0; j < 4; ++j) {
                if (udpConnection.playerIDs[j] != -1) {
                    ++n;
                }
            }
        }
        return n;
    }
    
    public static void sendAmbient(final String s, final int n, final int n2, final int n3, final float n4) {
        DebugLog.log(DebugType.Sound, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;III)Ljava/lang/String;, s, n, n2, n3));
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (getAnyPlayerFromConnection(udpConnection) != null) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.AddAmbient.doPacket(startPacket);
                startPacket.putUTF(s);
                startPacket.putInt(n);
                startPacket.putInt(n2);
                startPacket.putInt(n3);
                startPacket.putFloat(n4);
                PacketTypes.PacketType.AddAmbient.send(udpConnection);
            }
        }
    }
    
    static void receiveChangeSafety(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final IsoPlayer playerFromConnection = getPlayerFromConnection(udpConnection, byteBuffer.get());
        if (playerFromConnection == null) {
            return;
        }
        playerFromConnection.setSafety(!playerFromConnection.isSafety());
        if (playerFromConnection.isSafety()) {
            LoggerManager.getLogger("pvp").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, playerFromConnection.username, LoggerManager.getPlayerCoords(playerFromConnection)));
        }
        else {
            LoggerManager.getLogger("pvp").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, playerFromConnection.username, LoggerManager.getPlayerCoords(playerFromConnection)));
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.ChangeSafety.doPacket(startPacket);
                startPacket.putShort(playerFromConnection.OnlineID);
                startPacket.putByte((byte)(playerFromConnection.isSafety() ? 1 : 0));
                PacketTypes.PacketType.ChangeSafety.send(udpConnection2);
            }
        }
    }
    
    static void receivePing(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        udpConnection.ping = true;
        answerPing(byteBuffer, udpConnection);
    }
    
    public static void updateOverlayForClients(final IsoObject isoObject, final String s, final float n, final float n2, final float n3, final float n4, final UdpConnection udpConnection) {
        if (GameServer.udpEngine == null) {
            return;
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2 != null) {
                if (isoObject.square != null) {
                    if (udpConnection2.RelevantTo((float)isoObject.square.x, (float)isoObject.square.y) && (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID())) {
                        final ByteBufferWriter startPacket = udpConnection2.startPacket();
                        PacketTypes.PacketType.UpdateOverlaySprite.doPacket(startPacket);
                        GameWindow.WriteStringUTF(startPacket.bb, s);
                        startPacket.putInt(isoObject.getSquare().getX());
                        startPacket.putInt(isoObject.getSquare().getY());
                        startPacket.putInt(isoObject.getSquare().getZ());
                        startPacket.putFloat(n);
                        startPacket.putFloat(n2);
                        startPacket.putFloat(n3);
                        startPacket.putFloat(n4);
                        startPacket.putInt(isoObject.getSquare().getObjects().indexOf(isoObject));
                        PacketTypes.PacketType.UpdateOverlaySprite.send(udpConnection2);
                    }
                }
            }
        }
    }
    
    static void receiveUpdateOverlaySprite(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final String readStringUTF = GameWindow.ReadStringUTF(byteBuffer);
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final float float1 = byteBuffer.getFloat();
        final float float2 = byteBuffer.getFloat();
        final float float3 = byteBuffer.getFloat();
        final float float4 = byteBuffer.getFloat();
        final int int4 = byteBuffer.getInt();
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
        if (gridSquare != null && int4 < gridSquare.getObjects().size()) {
            try {
                final IsoObject isoObject = gridSquare.getObjects().get(int4);
                if (isoObject != null && isoObject.setOverlaySprite(readStringUTF, float1, float2, float3, float4, false)) {
                    updateOverlayForClients(isoObject, readStringUTF, float1, float2, float3, float4, udpConnection);
                }
            }
            catch (Exception ex) {}
        }
    }
    
    public static void sendReanimatedZombieID(final IsoPlayer key, final IsoZombie isoZombie) {
        if (GameServer.PlayerToAddressMap.containsKey(key)) {
            sendObjectChange(key, "reanimatedID", "ID", isoZombie.OnlineID);
        }
    }
    
    static void receiveSyncSafehouse(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        final String readString = GameWindow.ReadString(byteBuffer);
        final int int5 = byteBuffer.getInt();
        SafeHouse o = SafeHouse.getSafeHouse(int1, int2, int3, int4);
        boolean b = false;
        if (o == null) {
            o = SafeHouse.addSafeHouse(int1, int2, int3, int4, readString, false);
            b = true;
        }
        if (o == null) {
            return;
        }
        o.getPlayers().clear();
        for (int i = 0; i < int5; ++i) {
            o.addPlayer(GameWindow.ReadString(byteBuffer));
        }
        final int int6 = byteBuffer.getInt();
        o.playersRespawn.clear();
        for (int j = 0; j < int6; ++j) {
            o.playersRespawn.add(GameWindow.ReadString(byteBuffer));
        }
        final boolean b2 = byteBuffer.get() == 1;
        o.setTitle(GameWindow.ReadString(byteBuffer));
        o.setOwner(readString);
        sendSafehouse(o, b2, udpConnection);
        if (ChatServer.isInited()) {
            if (b) {
                ChatServer.getInstance().createSafehouseChat(o.getId());
            }
            if (b2) {
                ChatServer.getInstance().removeSafehouseChat(o.getId());
            }
            else {
                ChatServer.getInstance().syncSafehouseChatMembers(o.getId(), readString, o.getPlayers());
            }
        }
        if (b2) {
            SafeHouse.getSafehouseList().remove(o);
            DebugLog.log(invokedynamic(makeConcatWithConstants:(IIIILjava/lang/String;)Ljava/lang/String;, int1, int2, int3, int4, o.getOwner()));
        }
    }
    
    public static void sendSafehouse(final SafeHouse safeHouse, final boolean b, final UdpConnection udpConnection) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.SyncSafehouse.doPacket(startPacket);
                startPacket.putInt(safeHouse.getX());
                startPacket.putInt(safeHouse.getY());
                startPacket.putInt(safeHouse.getW());
                startPacket.putInt(safeHouse.getH());
                startPacket.putUTF(safeHouse.getOwner());
                startPacket.putInt(safeHouse.getPlayers().size());
                final Iterator<String> iterator = safeHouse.getPlayers().iterator();
                while (iterator.hasNext()) {
                    startPacket.putUTF(iterator.next());
                }
                startPacket.putInt(safeHouse.playersRespawn.size());
                for (int j = 0; j < safeHouse.playersRespawn.size(); ++j) {
                    startPacket.putUTF(safeHouse.playersRespawn.get(j));
                }
                startPacket.putBoolean(b);
                startPacket.putUTF(safeHouse.getTitle());
                PacketTypes.PacketType.SyncSafehouse.send(udpConnection2);
            }
        }
    }
    
    static void receivePacketTypeShort(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        switch (byteBuffer.getShort()) {
            case 1000: {
                receiveWaveSignal(byteBuffer);
                break;
            }
            case 1001: {
                receivePlayerListensChannel(byteBuffer);
                break;
            }
            case 1002: {
                sendRadioServerData(udpConnection);
                break;
            }
            case 1004: {
                receiveRadioDeviceDataState(byteBuffer, udpConnection);
                break;
            }
            case 1200: {
                SyncCustomLightSwitchSettings(byteBuffer, udpConnection);
                break;
            }
        }
    }
    
    static void receiveSteamGeneric(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        switch (byteBuffer.get()) {
            case 0: {
                final byte value = byteBuffer.get();
                GameWindow.ReadStringUTF(byteBuffer);
                final IsoPlayer playerFromConnection = getPlayerFromConnection(udpConnection, value);
                if (playerFromConnection != null) {
                    SteamGameServer.BUpdateUserData(playerFromConnection.getSteamID(), playerFromConnection.username, 0);
                    break;
                }
                break;
            }
        }
    }
    
    public static void receiveRadioDeviceDataState(final ByteBuffer byteBuffer, final UdpConnection udpConnection) {
        final byte value = byteBuffer.get();
        if (value == 1) {
            final int int1 = byteBuffer.getInt();
            final int int2 = byteBuffer.getInt();
            final int int3 = byteBuffer.getInt();
            final int int4 = byteBuffer.getInt();
            final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
            if (gridSquare != null && int4 >= 0 && int4 < gridSquare.getObjects().size()) {
                final IsoObject isoObject = gridSquare.getObjects().get(int4);
                if (isoObject instanceof IsoWaveSignal) {
                    final DeviceData deviceData = ((IsoWaveSignal)isoObject).getDeviceData();
                    if (deviceData != null) {
                        try {
                            deviceData.receiveDeviceDataStatePacket(byteBuffer, null);
                        }
                        catch (Exception ex) {
                            System.out.print(ex.getMessage());
                        }
                    }
                }
            }
        }
        else if (value == 0) {
            final IsoPlayer playerFromConnection = getPlayerFromConnection(udpConnection, byteBuffer.get());
            final byte value2 = byteBuffer.get();
            if (playerFromConnection != null) {
                Radio radio = null;
                if (value2 == 1 && playerFromConnection.getPrimaryHandItem() instanceof Radio) {
                    radio = (Radio)playerFromConnection.getPrimaryHandItem();
                }
                if (value2 == 2 && playerFromConnection.getSecondaryHandItem() instanceof Radio) {
                    radio = (Radio)playerFromConnection.getSecondaryHandItem();
                }
                if (radio != null && radio.getDeviceData() != null) {
                    try {
                        radio.getDeviceData().receiveDeviceDataStatePacket(byteBuffer, udpConnection);
                    }
                    catch (Exception ex2) {
                        System.out.print(ex2.getMessage());
                    }
                }
            }
        }
        else if (value == 2) {
            final short short1 = byteBuffer.getShort();
            final short short2 = byteBuffer.getShort();
            final BaseVehicle vehicleByID = VehicleManager.instance.getVehicleByID(short1);
            if (vehicleByID != null) {
                final VehiclePart partByIndex = vehicleByID.getPartByIndex(short2);
                if (partByIndex != null) {
                    final DeviceData deviceData2 = partByIndex.getDeviceData();
                    if (deviceData2 != null) {
                        try {
                            deviceData2.receiveDeviceDataStatePacket(byteBuffer, null);
                        }
                        catch (Exception ex3) {
                            System.out.print(ex3.getMessage());
                        }
                    }
                }
            }
        }
    }
    
    private static void sendRadioServerData(final UdpConnection udpConnection) {
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypesShort.doPacket((short)1002, startPacket);
        ZomboidRadio.getInstance().WriteRadioServerDataPacket(startPacket);
        PacketTypes.PacketType.PacketTypeShort.send(udpConnection);
    }
    
    public static void sendIsoWaveSignal(final int n, final int n2, final int n3, final String s, final String s2, final float n4, final float n5, final float n6, final int n7, final boolean b) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            final ByteBufferWriter startPacket = udpConnection.startPacket();
            PacketTypesShort.doPacket((short)1000, startPacket);
            startPacket.putInt(n);
            startPacket.putInt(n2);
            startPacket.putInt(n3);
            startPacket.putBoolean(s != null);
            if (s != null) {
                GameWindow.WriteString(startPacket.bb, s);
            }
            startPacket.putByte((byte)((s2 != null) ? 1 : 0));
            if (s2 != null) {
                startPacket.putUTF(s2);
            }
            startPacket.putFloat(n4);
            startPacket.putFloat(n5);
            startPacket.putFloat(n6);
            startPacket.putInt(n7);
            startPacket.putByte((byte)(b ? 1 : 0));
            PacketTypes.PacketType.PacketTypeShort.send(udpConnection);
        }
    }
    
    public static void receiveWaveSignal(final ByteBuffer byteBuffer) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final boolean b = byteBuffer.get() == 1;
        String readString = null;
        if (b) {
            readString = GameWindow.ReadString(byteBuffer);
        }
        String readString2 = null;
        if (byteBuffer.get() == 1) {
            readString2 = GameWindow.ReadString(byteBuffer);
        }
        ZomboidRadio.getInstance().ReceiveTransmission(int1, int2, int3, readString, readString2, byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getInt(), byteBuffer.get() == 1);
    }
    
    public static void receivePlayerListensChannel(final ByteBuffer byteBuffer) {
        ZomboidRadio.getInstance().PlayerListensChannel(byteBuffer.getInt(), byteBuffer.get() == 1, byteBuffer.get() == 1);
    }
    
    public static void sendAlarm(final int n, final int n2) {
        DebugLog.log(DebugType.Multiplayer, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, n, n2));
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (getAnyPlayerFromConnection(udpConnection) != null) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.AddAlarm.doPacket(startPacket);
                startPacket.putInt(n);
                startPacket.putInt(n2);
                PacketTypes.PacketType.AddAlarm.send(udpConnection);
            }
        }
    }
    
    public static boolean isSpawnBuilding(final BuildingDef buildingDef) {
        return SpawnPoints.instance.isSpawnBuilding(buildingDef);
    }
    
    private static void setFastForward(final boolean bFastForward) {
        if (bFastForward == GameServer.bFastForward) {
            return;
        }
        GameServer.bFastForward = bFastForward;
        syncClock();
        if (!GameServer.bFastForward) {
            GameServer.SendZombies = 0;
        }
    }
    
    static void receiveSendCustomColor(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        final float float1 = byteBuffer.getFloat();
        final float float2 = byteBuffer.getFloat();
        final float float3 = byteBuffer.getFloat();
        final float float4 = byteBuffer.getFloat();
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
        if (gridSquare != null && int4 < gridSquare.getObjects().size()) {
            final IsoObject isoObject = gridSquare.getObjects().get(int4);
            if (isoObject != null) {
                isoObject.setCustomColor(float1, float2, float3, float4);
            }
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.RelevantTo((float)int1, (float)int2) && ((udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) || udpConnection == null)) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.SendCustomColor.doPacket(startPacket);
                startPacket.putInt(int1);
                startPacket.putInt(int2);
                startPacket.putInt(int3);
                startPacket.putInt(int4);
                startPacket.putFloat(float1);
                startPacket.putFloat(float2);
                startPacket.putFloat(float3);
                startPacket.putFloat(float4);
                PacketTypes.PacketType.SendCustomColor.send(udpConnection2);
            }
        }
    }
    
    static void receiveSyncFurnace(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final IsoGridSquare gridSquare = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
        if (gridSquare == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, int1, int2, int3));
            return;
        }
        BSFurnace bsFurnace = null;
        for (int i = 0; i < gridSquare.getObjects().size(); ++i) {
            if (gridSquare.getObjects().get(i) instanceof BSFurnace) {
                bsFurnace = (BSFurnace)gridSquare.getObjects().get(i);
                break;
            }
        }
        if (bsFurnace == null) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, int1, int2, int3));
            return;
        }
        bsFurnace.fireStarted = (byteBuffer.get() == 1);
        bsFurnace.fuelAmount = byteBuffer.getFloat();
        bsFurnace.fuelDecrease = byteBuffer.getFloat();
        bsFurnace.heat = byteBuffer.getFloat();
        bsFurnace.sSprite = GameWindow.ReadString(byteBuffer);
        bsFurnace.sLitSprite = GameWindow.ReadString(byteBuffer);
        sendFuranceChange(bsFurnace, udpConnection);
    }
    
    static void receiveVehicles(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        VehicleManager.instance.serverPacket(byteBuffer, udpConnection);
    }
    
    static void receiveTimeSync(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        GameTime.getInstance();
        GameTime.receiveTimeSync(byteBuffer, udpConnection);
    }
    
    public static void sendFuranceChange(final BSFurnace bsFurnace, final UdpConnection udpConnection) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.RelevantTo((float)bsFurnace.square.x, (float)bsFurnace.square.y) && ((udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) || udpConnection == null)) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.SyncFurnace.doPacket(startPacket);
                startPacket.putInt(bsFurnace.square.x);
                startPacket.putInt(bsFurnace.square.y);
                startPacket.putInt(bsFurnace.square.z);
                startPacket.putByte((byte)(bsFurnace.isFireStarted() ? 1 : 0));
                startPacket.putFloat(bsFurnace.getFuelAmount());
                startPacket.putFloat(bsFurnace.getFuelDecrease());
                startPacket.putFloat(bsFurnace.getHeat());
                GameWindow.WriteString(startPacket.bb, bsFurnace.sSprite);
                GameWindow.WriteString(startPacket.bb, bsFurnace.sLitSprite);
                PacketTypes.PacketType.SyncFurnace.send(udpConnection2);
            }
        }
    }
    
    static void receiveUserlog(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final String readString = GameWindow.ReadString(byteBuffer);
        final ArrayList<Userlog> userlog = ServerWorldDatabase.instance.getUserlog(readString);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.Userlog.doPacket(startPacket);
                startPacket.putInt(userlog.size());
                startPacket.putUTF(readString);
                for (int j = 0; j < userlog.size(); ++j) {
                    final Userlog userlog2 = userlog.get(j);
                    startPacket.putInt(Userlog.UserlogType.FromString(userlog2.getType()).index());
                    startPacket.putUTF(userlog2.getText());
                    startPacket.putUTF(userlog2.getIssuedBy());
                    startPacket.putInt(userlog2.getAmount());
                }
                PacketTypes.PacketType.Userlog.send(udpConnection2);
            }
        }
    }
    
    static void receiveAddUserlog(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) throws SQLException {
        final String readString = GameWindow.ReadString(byteBuffer);
        final String readString2 = GameWindow.ReadString(byteBuffer);
        final String readString3 = GameWindow.ReadString(byteBuffer);
        ServerWorldDatabase.instance.addUserlog(readString, Userlog.UserlogType.FromString(readString2), readString3, udpConnection.username, 1);
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, udpConnection.username, readString, readString3));
    }
    
    static void receiveRemoveUserlog(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) throws SQLException {
        final String readString = GameWindow.ReadString(byteBuffer);
        final String readString2 = GameWindow.ReadString(byteBuffer);
        final String readString3 = GameWindow.ReadString(byteBuffer);
        ServerWorldDatabase.instance.removeUserLog(readString, readString2, readString3);
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, udpConnection.username, readString, readString2, readString3));
    }
    
    static void receiveAddWarningPoint(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) throws SQLException {
        final String readString = GameWindow.ReadString(byteBuffer);
        final String readString2 = GameWindow.ReadString(byteBuffer);
        final int int1 = byteBuffer.getInt();
        ServerWorldDatabase.instance.addWarningPoint(readString, readString2, int1, udpConnection.username);
        LoggerManager.getLogger("admin").write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, udpConnection.username, int1, readString, readString2));
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.username.equals(readString)) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.WorldMessage.doPacket(startPacket);
                startPacket.putUTF(udpConnection.username);
                startPacket.putUTF(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, int1, readString2));
                PacketTypes.PacketType.WorldMessage.send(udpConnection2);
            }
        }
    }
    
    public static void sendAdminMessage(final String s, final int n, final int n2, final int n3) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            if (canSeePlayerStats(udpConnection)) {
                final ByteBufferWriter startPacket = udpConnection.startPacket();
                PacketTypes.PacketType.MessageForAdmin.doPacket(startPacket);
                startPacket.putUTF(s);
                startPacket.putInt(n);
                startPacket.putInt(n2);
                startPacket.putInt(n3);
                PacketTypes.PacketType.MessageForAdmin.send(udpConnection);
            }
        }
    }
    
    static void receiveWakeUpPlayer(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final IsoPlayer isoPlayer = GameServer.IDToPlayerMap.get(byteBuffer.getShort());
        isoPlayer.setAsleep(false);
        isoPlayer.setAsleepTime(0.0f);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.WakeUpPlayer.doPacket(startPacket);
                startPacket.putShort(isoPlayer.OnlineID);
                PacketTypes.PacketType.WakeUpPlayer.send(udpConnection2);
            }
        }
    }
    
    static void receiveGetDBSchema(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final DBSchema dbSchema = ServerWorldDatabase.instance.getDBSchema();
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection != null && udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.GetDBSchema.doPacket(startPacket);
                final HashMap<String, HashMap<String, String>> schema = dbSchema.getSchema();
                startPacket.putInt(schema.size());
                for (final String key : schema.keySet()) {
                    final HashMap<String, String> hashMap = schema.get(key);
                    startPacket.putUTF(key);
                    startPacket.putInt(hashMap.size());
                    for (final String key2 : hashMap.keySet()) {
                        startPacket.putUTF(key2);
                        startPacket.putUTF(hashMap.get(key2));
                    }
                }
                PacketTypes.PacketType.GetDBSchema.send(udpConnection2);
            }
        }
    }
    
    static void receiveGetTableResult(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) throws SQLException {
        final int int1 = byteBuffer.getInt();
        final String readString = GameWindow.ReadString(byteBuffer);
        final ArrayList<DBResult> tableResult = ServerWorldDatabase.instance.getTableResult(readString);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection != null && udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) {
                doTableResult(udpConnection2, readString, tableResult, 0, int1);
            }
        }
    }
    
    private static void doTableResult(final UdpConnection udpConnection, final String s, final ArrayList<DBResult> list, final int n, final int n2) {
        int n3 = 0;
        boolean b = true;
        final ByteBufferWriter startPacket = udpConnection.startPacket();
        PacketTypes.PacketType.GetTableResult.doPacket(startPacket);
        startPacket.putInt(n);
        startPacket.putUTF(s);
        if (list.size() < n2) {
            startPacket.putInt(list.size());
        }
        else if (list.size() - n < n2) {
            startPacket.putInt(list.size() - n);
        }
        else {
            startPacket.putInt(n2);
        }
        for (int i = n; i < list.size(); ++i) {
            DBResult dbResult = null;
            try {
                dbResult = list.get(i);
                startPacket.putInt(dbResult.getColumns().size());
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            for (final String key : dbResult.getColumns()) {
                startPacket.putUTF(key);
                startPacket.putUTF(dbResult.getValues().get(key));
            }
            if (++n3 >= n2) {
                b = false;
                PacketTypes.PacketType.GetTableResult.send(udpConnection);
                doTableResult(udpConnection, s, list, n + n3, n2);
                break;
            }
        }
        if (b) {
            PacketTypes.PacketType.GetTableResult.send(udpConnection);
        }
    }
    
    static void receiveExecuteQuery(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) throws SQLException {
        if (udpConnection.accessLevel == null || !udpConnection.accessLevel.equals("admin")) {
            return;
        }
        try {
            final String readString = GameWindow.ReadString(byteBuffer);
            final KahluaTable table = LuaManager.platform.newTable();
            table.load(byteBuffer, 186);
            ServerWorldDatabase.instance.executeQuery(readString, table);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    static void receiveSendFactionInvite(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final String readString = GameWindow.ReadString(byteBuffer);
        final String readString2 = GameWindow.ReadString(byteBuffer);
        final Long n2 = GameServer.IDToAddressMap.get(getPlayerByUserName(GameWindow.ReadString(byteBuffer)).getOnlineID());
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() == n2) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.SendFactionInvite.doPacket(startPacket);
                startPacket.putUTF(readString);
                startPacket.putUTF(readString2);
                PacketTypes.PacketType.SendFactionInvite.send(udpConnection2);
                break;
            }
        }
    }
    
    static void receiveAcceptedFactionInvite(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final String readString = GameWindow.ReadString(byteBuffer);
        final String readString2 = GameWindow.ReadString(byteBuffer);
        final Long n2 = GameServer.IDToAddressMap.get(getPlayerByUserName(readString2).getOnlineID());
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() == n2) {
                final Faction playerFaction = Faction.getPlayerFaction(udpConnection2.username);
                if (playerFaction != null && playerFaction.getName().equals(readString)) {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.AcceptedFactionInvite.doPacket(startPacket);
                    startPacket.putUTF(readString);
                    startPacket.putUTF(readString2);
                    PacketTypes.PacketType.AcceptedFactionInvite.send(udpConnection2);
                }
            }
        }
    }
    
    static void receiveViewTickets(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) throws SQLException {
        String readString = GameWindow.ReadString(byteBuffer);
        if ("".equals(readString)) {
            readString = null;
        }
        sendTickets(readString, udpConnection);
    }
    
    private static void sendTickets(final String s, final UdpConnection udpConnection) throws SQLException {
        final ArrayList<DBTicket> tickets = ServerWorldDatabase.instance.getTickets(s);
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.ViewTickets.doPacket(startPacket);
                startPacket.putInt(tickets.size());
                for (int j = 0; j < tickets.size(); ++j) {
                    final DBTicket dbTicket = tickets.get(j);
                    startPacket.putUTF(dbTicket.getAuthor());
                    startPacket.putUTF(dbTicket.getMessage());
                    startPacket.putInt(dbTicket.getTicketID());
                    if (dbTicket.getAnswer() != null) {
                        startPacket.putByte((byte)1);
                        startPacket.putUTF(dbTicket.getAnswer().getAuthor());
                        startPacket.putUTF(dbTicket.getAnswer().getMessage());
                        startPacket.putInt(dbTicket.getAnswer().getTicketID());
                    }
                    else {
                        startPacket.putByte((byte)0);
                    }
                }
                PacketTypes.PacketType.ViewTickets.send(udpConnection2);
                break;
            }
        }
    }
    
    static void receiveAddTicket(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) throws SQLException {
        final String readString = GameWindow.ReadString(byteBuffer);
        final String readString2 = GameWindow.ReadString(byteBuffer);
        final int int1 = byteBuffer.getInt();
        if (int1 == -1) {
            sendAdminMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, readString, readString2), -1, -1, -1);
        }
        ServerWorldDatabase.instance.addTicket(readString, readString2, int1);
        sendTickets(readString, udpConnection);
    }
    
    static void receiveRemoveTicket(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) throws SQLException {
        ServerWorldDatabase.instance.removeTicket(byteBuffer.getInt());
        sendTickets(null, udpConnection);
    }
    
    public static boolean sendItemListNet(final UdpConnection udpConnection, final IsoPlayer isoPlayer, final ArrayList<InventoryItem> list, final IsoPlayer isoPlayer2, final String s, final String s2) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection == null || udpConnection2 != udpConnection) {
                if (isoPlayer2 != null) {
                    boolean b = false;
                    for (int j = 0; j < udpConnection2.players.length; ++j) {
                        final IsoPlayer isoPlayer3 = udpConnection2.players[j];
                        if (isoPlayer3 != null && isoPlayer3 == isoPlayer2) {
                            b = true;
                            break;
                        }
                    }
                    if (!b) {
                        continue;
                    }
                }
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.SendItemListNet.doPacket(startPacket);
                startPacket.putByte((byte)((isoPlayer2 != null) ? 1 : 0));
                if (isoPlayer2 != null) {
                    startPacket.putShort(isoPlayer2.getOnlineID());
                }
                startPacket.putByte((byte)((isoPlayer != null) ? 1 : 0));
                if (isoPlayer != null) {
                    startPacket.putShort(isoPlayer.getOnlineID());
                }
                GameWindow.WriteString(startPacket.bb, s);
                startPacket.putByte((byte)((s2 != null) ? 1 : 0));
                if (s2 != null) {
                    GameWindow.WriteString(startPacket.bb, s2);
                }
                try {
                    CompressIdenticalItems.save(startPacket.bb, list, null);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    udpConnection2.cancelPacket();
                    return false;
                }
                PacketTypes.PacketType.SendItemListNet.send(udpConnection2);
            }
        }
        return true;
    }
    
    static void receiveSendItemListNet(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        IsoPlayer isoPlayer = null;
        if (byteBuffer.get() == 1) {
            isoPlayer = GameServer.IDToPlayerMap.get(byteBuffer.getShort());
        }
        IsoPlayer isoPlayer2 = null;
        if (byteBuffer.get() == 1) {
            isoPlayer2 = GameServer.IDToPlayerMap.get(byteBuffer.getShort());
        }
        final String readString = GameWindow.ReadString(byteBuffer);
        String readString2 = null;
        if (byteBuffer.get() == 1) {
            readString2 = GameWindow.ReadString(byteBuffer);
        }
        final ArrayList<InventoryItem> list = new ArrayList<InventoryItem>();
        try {
            CompressIdenticalItems.load(byteBuffer, 186, list, null);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        if (isoPlayer == null) {
            LuaEventManager.triggerEvent("OnReceiveItemListNet", isoPlayer2, list, isoPlayer, readString, readString2);
        }
        else {
            sendItemListNet(udpConnection, isoPlayer2, list, isoPlayer, readString, readString2);
        }
    }
    
    public static void sendPlayerDamagedByCarCrash(final IsoPlayer isoPlayer, final float n) {
        final UdpConnection connectionFromPlayer = getConnectionFromPlayer(isoPlayer);
        if (connectionFromPlayer == null) {
            return;
        }
        final ByteBufferWriter startPacket = connectionFromPlayer.startPacket();
        PacketTypes.PacketType.PlayerDamageFromCarCrash.doPacket(startPacket);
        startPacket.putFloat(n);
        PacketTypes.PacketType.PlayerDamageFromCarCrash.send(connectionFromPlayer);
    }
    
    static void receiveClimateManagerPacket(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final ClimateManager instance = ClimateManager.getInstance();
        if (instance != null) {
            try {
                instance.receiveClimatePacket(byteBuffer, udpConnection);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    static void receivePassengerMap(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        PassengerMap.serverReceivePacket(byteBuffer, udpConnection);
    }
    
    static void receiveIsoRegionClientRequestFullUpdate(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        IsoRegions.receiveClientRequestFullDataChunks(byteBuffer, udpConnection);
    }
    
    private static String isWorldVersionUnsupported() {
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getSaveDir(), File.separator, File.separator, GameServer.ServerName, File.separator));
        if (file.exists()) {
            DebugLog.log("checking server WorldVersion in map_t.bin");
            try {
                final FileInputStream in = new FileInputStream(file);
                try {
                    final DataInputStream dataInputStream = new DataInputStream(in);
                    try {
                        final byte byte1 = dataInputStream.readByte();
                        final byte byte2 = dataInputStream.readByte();
                        final byte byte3 = dataInputStream.readByte();
                        final byte byte4 = dataInputStream.readByte();
                        if (byte1 != 71 || byte2 != 77 || byte3 != 84 || byte4 != 77) {
                            final String s = "The server savefile appears to be from an old version of the game and cannot be loaded.";
                            dataInputStream.close();
                            in.close();
                            return s;
                        }
                        final int int1 = dataInputStream.readInt();
                        if (int1 > 186) {
                            final String s2 = "The server savefile appears to be from a newer version of the game and cannot be loaded.";
                            dataInputStream.close();
                            in.close();
                            return s2;
                        }
                        if (int1 <= 143) {
                            final String s3 = "The server savefile appears to be from a pre-animations version of the game and cannot be loaded.\nDue to the extent of changes required to implement animations, saves from earlier versions are not compatible.";
                            dataInputStream.close();
                            in.close();
                            return s3;
                        }
                        dataInputStream.close();
                    }
                    catch (Throwable t) {
                        try {
                            dataInputStream.close();
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
            }
        }
        else {
            DebugLog.log("map_t.bin does not exist, cannot determine the server's WorldVersion.  This is ok the first time a server is started.");
        }
        return null;
    }
    
    public String getPoisonousBerry() {
        return this.poisonousBerry;
    }
    
    public void setPoisonousBerry(final String poisonousBerry) {
        this.poisonousBerry = poisonousBerry;
    }
    
    public String getPoisonousMushroom() {
        return this.poisonousMushroom;
    }
    
    public void setPoisonousMushroom(final String poisonousMushroom) {
        this.poisonousMushroom = poisonousMushroom;
    }
    
    public String getDifficulty() {
        return this.difficulty;
    }
    
    public void setDifficulty(final String difficulty) {
        this.difficulty = difficulty;
    }
    
    public static void transmitBrokenGlass(final IsoGridSquare isoGridSquare) {
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection = GameServer.udpEngine.connections.get(i);
            try {
                if (udpConnection.RelevantTo((float)isoGridSquare.getX(), (float)isoGridSquare.getY())) {
                    final ByteBufferWriter startPacket = udpConnection.startPacket();
                    PacketTypes.PacketType.AddBrokenGlass.doPacket(startPacket);
                    startPacket.putInt((short)isoGridSquare.getX());
                    startPacket.putInt((short)isoGridSquare.getY());
                    startPacket.putInt((short)isoGridSquare.getZ());
                    PacketTypes.PacketType.AddBrokenGlass.send(udpConnection);
                }
            }
            catch (Throwable t) {
                udpConnection.cancelPacket();
                ExceptionLogger.logException(t);
            }
        }
    }
    
    public static boolean isServerDropPackets() {
        return GameServer.droppedPackets > 0;
    }
    
    static void receiveSyncPerks(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final byte value = byteBuffer.get();
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final IsoPlayer playerFromConnection = getPlayerFromConnection(udpConnection, value);
        if (playerFromConnection == null) {
            return;
        }
        playerFromConnection.remoteSneakLvl = int1;
        playerFromConnection.remoteStrLvl = int2;
        playerFromConnection.remoteFitLvl = int3;
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && getAnyPlayerFromConnection(udpConnection) != null) {
                try {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.SyncPerks.doPacket(startPacket);
                    startPacket.putShort(playerFromConnection.OnlineID);
                    startPacket.putInt(int1);
                    startPacket.putInt(int2);
                    startPacket.putInt(int3);
                    PacketTypes.PacketType.SyncPerks.send(udpConnection2);
                }
                catch (Throwable t) {
                    udpConnection.cancelPacket();
                    ExceptionLogger.logException(t);
                }
            }
        }
    }
    
    static void receiveSyncWeight(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final byte value = byteBuffer.get();
        final float float1 = byteBuffer.getFloat();
        final IsoPlayer playerFromConnection = getPlayerFromConnection(udpConnection, value);
        if (playerFromConnection == null) {
            return;
        }
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && getAnyPlayerFromConnection(udpConnection) != null) {
                try {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.SyncWeight.doPacket(startPacket);
                    startPacket.putShort(playerFromConnection.OnlineID);
                    startPacket.putFloat(float1);
                    PacketTypes.PacketType.SyncWeight.send(udpConnection2);
                }
                catch (Throwable t) {
                    udpConnection.cancelPacket();
                    ExceptionLogger.logException(t);
                }
            }
        }
    }
    
    static void receiveSyncEquippedRadioFreq(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final byte value = byteBuffer.get();
        final int int1 = byteBuffer.getInt();
        final ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < int1; ++i) {
            list.add(byteBuffer.getInt());
        }
        final IsoPlayer playerFromConnection = getPlayerFromConnection(udpConnection, value);
        if (playerFromConnection == null) {
            return;
        }
        for (int j = 0; j < GameServer.udpEngine.connections.size(); ++j) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(j);
            if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && getAnyPlayerFromConnection(udpConnection) != null) {
                try {
                    final ByteBufferWriter startPacket = udpConnection2.startPacket();
                    PacketTypes.PacketType.SyncEquippedRadioFreq.doPacket(startPacket);
                    startPacket.putShort(playerFromConnection.OnlineID);
                    startPacket.putInt(int1);
                    for (int k = 0; k < list.size(); ++k) {
                        startPacket.putInt(list.get(k));
                    }
                    PacketTypes.PacketType.SyncEquippedRadioFreq.send(udpConnection2);
                }
                catch (Throwable t) {
                    udpConnection.cancelPacket();
                    ExceptionLogger.logException(t);
                }
            }
        }
    }
    
    static void receiveGlobalModData(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        GlobalModData.instance.receive(byteBuffer);
    }
    
    static void receiveGlobalModDataRequest(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        GlobalModData.instance.receiveRequest(byteBuffer, udpConnection);
    }
    
    static void receiveSendSafehouseInvite(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final String readString = GameWindow.ReadString(byteBuffer);
        final String readString2 = GameWindow.ReadString(byteBuffer);
        final Long n2 = GameServer.IDToAddressMap.get(getPlayerByUserName(GameWindow.ReadString(byteBuffer)).getOnlineID());
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() == n2) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.SendSafehouseInvite.doPacket(startPacket);
                startPacket.putUTF(readString);
                startPacket.putUTF(readString2);
                startPacket.putInt(int1);
                startPacket.putInt(int2);
                startPacket.putInt(int3);
                startPacket.putInt(int4);
                PacketTypes.PacketType.SendSafehouseInvite.send(udpConnection2);
                break;
            }
        }
    }
    
    static void receiveAcceptedSafehouseInvite(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) {
        final String readString = GameWindow.ReadString(byteBuffer);
        final String readString2 = GameWindow.ReadString(byteBuffer);
        final String readString3 = GameWindow.ReadString(byteBuffer);
        final Long n2 = GameServer.IDToAddressMap.get(getPlayerByUserName(readString2).getOnlineID());
        final int int1 = byteBuffer.getInt();
        final int int2 = byteBuffer.getInt();
        final int int3 = byteBuffer.getInt();
        final int int4 = byteBuffer.getInt();
        for (int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
            final UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(i);
            if (udpConnection2.getConnectedGUID() == n2) {
                final ByteBufferWriter startPacket = udpConnection2.startPacket();
                PacketTypes.PacketType.AcceptedSafehouseInvite.doPacket(startPacket);
                startPacket.putUTF(readString);
                startPacket.putUTF(readString2);
                startPacket.putUTF(readString3);
                startPacket.putInt(int1);
                startPacket.putInt(int2);
                startPacket.putInt(int3);
                startPacket.putInt(int4);
                PacketTypes.PacketType.AcceptedSafehouseInvite.send(udpConnection2);
            }
        }
    }
    
    static {
        packetCounts = new long[256];
        ccFilters = new HashMap<String, CCFilter>();
        GameServer.test = 432432;
        GameServer.DEFAULT_PORT = 16261;
        GameServer.IPCommandline = null;
        GameServer.PortCommandline = -1;
        GameServer.SteamPortCommandline1 = -1;
        GameServer.SteamPortCommandline2 = -1;
        GameServer.bServer = false;
        GameServer.bCoop = false;
        GameServer.bDebug = false;
        IDToAddressMap = new HashMap<Short, Long>();
        IDToPlayerMap = new HashMap<Short, IsoPlayer>();
        Players = new ArrayList<IsoPlayer>();
        GameServer.timeSinceKeepAlive = 0.0f;
        GameServer.MaxTicksSinceKeepAliveBeforeStall = 60;
        DebugPlayer = new HashSet<UdpConnection>();
        GameServer.ResetID = 0;
        ServerMods = new ArrayList<String>();
        WorkshopItems = new ArrayList<Long>();
        GameServer.ServerName = "servertest";
        discordBot = new DiscordBot(GameServer.ServerName, (s, s2) -> ChatServer.getInstance().sendMessageFromDiscordToGeneralChat(s, s2));
        GameServer.checksum = "";
        GameServer.GameMap = "Muldraugh, KY";
        transactionIDMap = new HashMap<String, Integer>();
        worldObjectsServerSyncReq = new ObjectsSyncRequests(false);
        GameServer.ip = "127.0.0.1";
        GameServer.count = 0;
        SlotToConnection = new UdpConnection[512];
        PlayerToAddressMap = new HashMap<IsoPlayer, Long>();
        alreadyRemoved = new ArrayList<Integer>();
        GameServer.SendZombies = 0;
        GameServer.launched = false;
        consoleCommands = new ArrayList<String>();
        MainLoopPlayerUpdate = new HashMap<Integer, IZomboidPacket>();
        MainLoopPlayerUpdateQ = new ConcurrentLinkedQueue<IZomboidPacket>();
        MainLoopNetDataHighPriorityQ = new ConcurrentLinkedQueue<IZomboidPacket>();
        MainLoopNetDataQ = new ConcurrentLinkedQueue<IZomboidPacket>();
        MainLoopNetData2 = new ArrayList<IZomboidPacket>();
        playerToCoordsMap = new HashMap<Short, Vector2>();
        playerMovedToFastMap = new HashMap<Short, Integer>();
        large_file_bb = ByteBuffer.allocate(3145728);
        GameServer.previousSave = Calendar.getInstance().getTimeInMillis();
        GameServer.droppedPackets = 0;
        GameServer.countOfDroppedPackets = 0;
        GameServer.countOfDroppedConnections = 0;
        GameServer.removeZombiesConnection = null;
        GameServer.calcCountPlayersInRelevantPositionLimiter = new UpdateLimit(2000L);
    }
    
    private static class s_performance
    {
        static final PerformanceProfileFrameProbe frameStep;
        static final PerformanceProfileProbe mainLoopDealWithNetData;
        static final PerformanceProfileProbe RCONServerUpdate;
        
        static {
            frameStep = new PerformanceProfileFrameProbe("GameServer.frameStep");
            mainLoopDealWithNetData = new PerformanceProfileProbe("GameServer.mainLoopDealWithNetData");
            RCONServerUpdate = new PerformanceProfileProbe("RCONServer.update");
        }
    }
    
    private static final class CCFilter
    {
        String command;
        boolean allow;
        CCFilter next;
        
        boolean matches(final String anObject) {
            return this.command.equals(anObject) || "*".equals(this.command);
        }
        
        boolean passes(final String s) {
            if (this.matches(s)) {
                return this.allow;
            }
            return this.next == null || this.next.passes(s);
        }
    }
    
    private static class DelayedConnection implements IZomboidPacket
    {
        public UdpConnection connection;
        public boolean connect;
        public String hostString;
        
        public DelayedConnection(final UdpConnection connection, final boolean connect) {
            this.connection = connection;
            this.connect = connect;
            if (connect) {
                try {
                    if (SteamUtils.isSteamModeEnabled()) {
                        this.hostString = SteamUtils.convertSteamIDToString(GameServer.udpEngine.getClientSteamID(connection.getConnectedGUID()));
                    }
                    else {
                        this.hostString = connection.getInetSocketAddress().getHostString();
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        @Override
        public boolean isConnect() {
            return this.connect;
        }
        
        @Override
        public boolean isDisconnect() {
            return !this.connect;
        }
    }
}
