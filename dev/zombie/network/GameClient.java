/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  fmod.fmod.FMODManager
 *  fmod.fmod.FMOD_STUDIO_EVENT_DESCRIPTION
 *  fmod.javafmod
 *  gnu.trove.map.hash.TShortObjectHashMap
 *  se.krka.kahlua.vm.KahluaTable
 *  se.krka.kahlua.vm.KahluaTableIterator
 */
package zombie.network;

import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_EVENT_DESCRIPTION;
import fmod.javafmod;
import gnu.trove.map.hash.TShortObjectHashMap;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.AmbientStreamManager;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.MapCollisionData;
import zombie.PersistentOutfits;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.NetworkTeleport;
import zombie.characters.NetworkZombieVariables;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.skills.PerkFactory;
import zombie.chat.ChatManager;
import zombie.core.BoxedStaticValues;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferReader;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.UdpEngine;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.SteamUser;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.erosion.ErosionConfig;
import zombie.erosion.ErosionMain;
import zombie.gameStates.ConnectToServerState;
import zombie.gameStates.MainScreenState;
import zombie.globalObjects.CGlobalObjectNetwork;
import zombie.inventory.CompressIdenticalItems;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Radio;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridOcclusionData;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectSyncRequests;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.ObjectsSyncRequests;
import zombie.iso.Vector2;
import zombie.iso.WorldStreamer;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.objects.BSFurnace;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.RainManager;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.weather.ClimateManager;
import zombie.network.BodyDamageSync;
import zombie.network.ClientServerMap;
import zombie.network.CoopMaster;
import zombie.network.DBResult;
import zombie.network.DBTicket;
import zombie.network.GameServer;
import zombie.network.MPStatistic;
import zombie.network.MPStatisticClient;
import zombie.network.MPStatistics;
import zombie.network.NetChecksum;
import zombie.network.NetworkVariables;
import zombie.network.PacketTypes;
import zombie.network.PacketTypesShort;
import zombie.network.PassengerMap;
import zombie.network.ServerOptions;
import zombie.network.ServerWorldDatabase;
import zombie.network.TableNetworkUtils;
import zombie.network.Userlog;
import zombie.network.WorldItemTypes;
import zombie.network.ZomboidNetData;
import zombie.network.ZomboidNetDataPool;
import zombie.network.packets.ActionPacket;
import zombie.network.packets.DeadPlayerPacket;
import zombie.network.packets.DeadZombiePacket;
import zombie.network.packets.EventPacket;
import zombie.network.packets.PlaySoundPacket;
import zombie.network.packets.PlayWorldSoundPacket;
import zombie.network.packets.PlayerPacket;
import zombie.network.packets.StopSoundPacket;
import zombie.network.packets.SyncClothingPacket;
import zombie.network.packets.SyncInjuriesPacket;
import zombie.network.packets.hit.HitCharacterPacket;
import zombie.network.packets.hit.PlayerHitPlayerPacket;
import zombie.network.packets.hit.PlayerHitSquarePacket;
import zombie.network.packets.hit.PlayerHitVehiclePacket;
import zombie.network.packets.hit.PlayerHitZombiePacket;
import zombie.network.packets.hit.VehicleHitPacket;
import zombie.network.packets.hit.VehicleHitPlayerPacket;
import zombie.network.packets.hit.VehicleHitZombiePacket;
import zombie.network.packets.hit.ZombieHitPlayerPacket;
import zombie.popman.MPDebugInfo;
import zombie.popman.NetworkZombieSimulator;
import zombie.popman.ZombieCountOptimiser;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.savefile.ClientPlayerDB;
import zombie.scripting.ScriptManager;
import zombie.ui.ServerPulseGraph;
import zombie.util.AddCoopPlayer;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclePart;
import zombie.world.moddata.GlobalModData;

public class GameClient {
    public static final GameClient instance = new GameClient();
    public static final int DEFAULT_PORT = 16361;
    public static boolean bClient = false;
    public static UdpConnection connection;
    public static int count;
    public static String ip;
    public static String localIP;
    public static String password;
    public static boolean allChatMuted;
    public static String username;
    public static String serverPassword;
    public UdpEngine udpEngine;
    public static String accessLevel;
    public byte ID = (byte)-1;
    public float timeSinceKeepAlive = 0.0f;
    UpdateLimit itemSendFrequency = new UpdateLimit(3000L);
    public static int port;
    public boolean bPlayerConnectSent = false;
    private boolean bClientStarted = false;
    private int ResetID = 0;
    private boolean bConnectionLost = false;
    public static String checksum;
    public static boolean checksumValid;
    public static List<Long> pingsList;
    public static String GameMap;
    public static boolean bFastForward;
    public static final ClientServerMap[] loadedCells;
    public int DEBUG_PING = 5;
    public IsoObjectSyncRequests objectSyncReq = new IsoObjectSyncRequests();
    public ObjectsSyncRequests worldObjectsSyncReq = new ObjectsSyncRequests(true);
    public static boolean bCoopInvite;
    private ArrayList<IsoPlayer> connectedPlayers = new ArrayList();
    private static boolean isPaused;
    private final ArrayList<IsoPlayer> players = new ArrayList();
    private boolean idMapDirty = true;
    private static final int sendZombieWithoutNeighbor = 4000;
    private static final int sendZombieWithNeighbor = 200;
    public final UpdateLimit sendZombieTimer = new UpdateLimit(4000L);
    public final UpdateLimit sendZombieRequestsTimer = new UpdateLimit(200L);
    public static Map<Short, Vector2> positions;
    private int safehouseUpdateTimer = 0;
    private boolean delayPacket = false;
    private final long[] packetCountsFromAllClients = new long[256];
    private final long[] packetCountsFromServer = new long[256];
    private final KahluaTable packetCountsTable = LuaManager.platform.newTable();
    private final ArrayList<Integer> delayedDisconnect = new ArrayList();
    private volatile RequestState request;
    public KahluaTable ServerSpawnRegions = null;
    static final ConcurrentLinkedQueue<ZomboidNetData> MainLoopNetDataQ;
    static final ArrayList<ZomboidNetData> MainLoopNetData;
    static final ArrayList<ZomboidNetData> LoadingMainLoopNetData;
    static final ArrayList<ZomboidNetData> DelayedCoopNetData;
    public boolean bConnected = false;
    UpdateLimit PlayerUpdateReliableLimit = new UpdateLimit(2000L);
    public int TimeSinceLastUpdate = 0;
    ByteBuffer staticTest = ByteBuffer.allocate(20000);
    ByteBufferWriter wr = new ByteBufferWriter(this.staticTest);
    long StartHeartMilli = 0L;
    long EndHeartMilli = 0L;
    public int ping = 0;
    public static float ServerPredictedAhead;
    public static final HashMap<Short, IsoPlayer> IDToPlayerMap;
    public static final TShortObjectHashMap<IsoZombie> IDToZombieMap;
    public static boolean bIngame;
    public static boolean askPing;
    public final ArrayList<String> ServerMods = new ArrayList();
    public ErosionConfig erosionConfig;
    public static Calendar startAuth;
    public static String poisonousBerry;
    public static String poisonousMushroom;
    final ArrayList<ZomboidNetData> incomingNetData = new ArrayList();
    private final HashMap<ItemContainer, ArrayList<InventoryItem>> itemsToSend = new HashMap();
    private final HashMap<ItemContainer, ArrayList<InventoryItem>> itemsToSendRemove = new HashMap();
    KahluaTable dbSchema;

    public IsoPlayer getPlayerByOnlineID(short s) {
        return IDToPlayerMap.get(s);
    }

    public void init() {
        DebugLog.setLogEnabled(DebugType.Network, true);
        LoadingMainLoopNetData.clear();
        MainLoopNetDataQ.clear();
        MainLoopNetData.clear();
        DelayedCoopNetData.clear();
        bIngame = false;
        IDToPlayerMap.clear();
        IDToZombieMap.clear();
        pingsList.clear();
        IDToZombieMap.setAutoCompactionFactor(0.0f);
        this.bPlayerConnectSent = false;
        this.bConnectionLost = false;
        this.delayedDisconnect.clear();
        GameWindow.bServerDisconnected = false;
        this.ServerSpawnRegions = null;
        this.startClient();
    }

    public void startClient() {
        if (this.bClientStarted) {
            this.udpEngine.Connect(ip, port, serverPassword);
            return;
        }
        try {
            this.udpEngine = new UdpEngine(Rand.Next(10000) + 12345, 1, null, false);
            this.udpEngine.Connect(ip, port, serverPassword);
            this.bClientStarted = true;
        }
        catch (Exception exception) {
            DebugLog.Network.printException(exception, "Exception thrown during GameClient.startClient.", LogSeverity.Error);
        }
    }

    static void receiveStatistic(ByteBuffer byteBuffer, short s) {
        try {
            long l = byteBuffer.getLong();
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.Statistic.doPacket(byteBufferWriter);
            byteBufferWriter.putLong(l);
            MPStatisticClient.getInstance().send(byteBufferWriter);
            PacketTypes.PacketType.Statistic.send(connection);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    static void receiveStatisticRequest(ByteBuffer byteBuffer, short s) {
        try {
            MPStatistic.getInstance().setStatisticTable(byteBuffer);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        LuaEventManager.triggerEvent("OnServerStatisticReceived");
    }

    static void receivePlayerUpdate(ByteBuffer byteBuffer, short s) {
        PlayerPacket playerPacket = PlayerPacket.l_receive.playerPacket;
        playerPacket.parse(byteBuffer);
        try {
            IsoPlayer isoPlayer = IDToPlayerMap.get(playerPacket.id);
            if (isoPlayer == null) {
                DebugLog.General.error("receivePlayerUpdate: Client received position for unknown player (id:" + playerPacket.id + "). Client will ignore this data.");
            } else {
                IsoGridSquare isoGridSquare;
                if (DebugOptions.instance.MultiplayerShowPosition.getValue()) {
                    if (positions.containsKey(isoPlayer.getOnlineID())) {
                        positions.get(isoPlayer.getOnlineID()).set(playerPacket.realx, playerPacket.realy);
                    } else {
                        positions.put(isoPlayer.getOnlineID(), new Vector2(playerPacket.realx, playerPacket.realy));
                    }
                }
                if (!isoPlayer.networkAI.isSetVehicleHit()) {
                    isoPlayer.networkAI.parse(playerPacket);
                }
                isoPlayer.bleedingLevel = playerPacket.bleedingLevel;
                if (isoPlayer.getVehicle() == null && !playerPacket.usePathFinder && (isoPlayer.networkAI.distance.getLength() > 7.0f || IsoUtils.DistanceTo(playerPacket.x, playerPacket.y, playerPacket.z, isoPlayer.x, isoPlayer.y, isoPlayer.z) > 1.0f && (int)isoPlayer.z != playerPacket.z)) {
                    NetworkTeleport.update(isoPlayer, playerPacket);
                    if (NetworkTeleport.teleport(isoPlayer, playerPacket, 1.0f)) {
                        DebugLog.Multiplayer.warn(String.format("Player %d teleport from (%.2f, %.2f, %.2f) to (%.2f, %.2f %d)", isoPlayer.getOnlineID(), Float.valueOf(isoPlayer.x), Float.valueOf(isoPlayer.y), Float.valueOf(isoPlayer.z), Float.valueOf(playerPacket.x), Float.valueOf(playerPacket.y), playerPacket.z));
                    }
                }
                if ((isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(playerPacket.x, playerPacket.y, (double)playerPacket.z)) != null) {
                    if (isoPlayer.isAlive() && !IsoWorld.instance.CurrentCell.getObjectList().contains(isoPlayer)) {
                        IsoWorld.instance.CurrentCell.getObjectList().add(isoPlayer);
                        isoPlayer.setCurrent(isoGridSquare);
                    }
                } else if (IsoWorld.instance.CurrentCell.getObjectList().contains(isoPlayer)) {
                    isoPlayer.removeFromWorld();
                    isoPlayer.removeFromSquare();
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    static void receiveZombieSimulation(ByteBuffer byteBuffer, short s) {
        short s2;
        short s3;
        boolean bl;
        NetworkZombieSimulator.getInstance().clear();
        boolean bl2 = bl = byteBuffer.get() == 1;
        if (bl) {
            GameClient.instance.sendZombieTimer.setUpdatePeriod(200L);
        } else {
            GameClient.instance.sendZombieTimer.setUpdatePeriod(4000L);
        }
        short s4 = byteBuffer.getShort();
        for (s3 = 0; s3 < s4; s3 = (short)(s3 + 1)) {
            s2 = byteBuffer.getShort();
            IsoZombie isoZombie = (IsoZombie)IDToZombieMap.get(s2);
            if (isoZombie == null) continue;
            VirtualZombieManager.instance.removeZombieFromWorld(isoZombie);
        }
        s3 = byteBuffer.getShort();
        for (s2 = 0; s2 < s3; s2 = (short)(s2 + 1)) {
            short s5 = byteBuffer.getShort();
            NetworkZombieSimulator.getInstance().add(s5);
        }
        NetworkZombieSimulator.getInstance().added();
        NetworkZombieSimulator.getInstance().receivePacket(byteBuffer);
    }

    static void receiveZombieControl(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        short s3 = byteBuffer.getShort();
        int n = byteBuffer.getInt();
        IsoZombie isoZombie = (IsoZombie)IDToZombieMap.get(s2);
        if (isoZombie == null) {
            return;
        }
        NetworkZombieVariables.setInt(isoZombie, s3, n);
    }

    public void Shutdown() {
        if (this.bClientStarted) {
            this.udpEngine.Shutdown();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update() {
        Object object;
        int n;
        ZombieCountOptimiser.startCount();
        if (this.safehouseUpdateTimer == 0 && ServerOptions.instance.DisableSafehouseWhenPlayerConnected.getValue()) {
            this.safehouseUpdateTimer = 3000;
            SafeHouse.updateSafehousePlayersConnected();
        }
        if (this.safehouseUpdateTimer > 0) {
            --this.safehouseUpdateTimer;
        }
        Object object2 = MainLoopNetDataQ.poll();
        while (object2 != null) {
            MainLoopNetData.add((ZomboidNetData)object2);
            object2 = MainLoopNetDataQ.poll();
        }
        if (this.bConnectionLost) {
            if (!this.bPlayerConnectSent) {
                for (int i = 0; i < MainLoopNetData.size(); ++i) {
                    ZomboidNetData zomboidNetData = MainLoopNetData.get(i);
                    this.gameLoadingDealWithNetData(zomboidNetData);
                }
                MainLoopNetData.clear();
            } else {
                for (int i = 0; i < MainLoopNetData.size(); ++i) {
                    ZomboidNetData zomboidNetData = MainLoopNetData.get(i);
                    if (zomboidNetData.type != PacketTypes.PacketType.Kicked.getId()) continue;
                    GameWindow.kickReason = GameWindow.ReadStringUTF(zomboidNetData.buffer);
                }
                MainLoopNetData.clear();
            }
            GameWindow.bServerDisconnected = true;
            return;
        }
        object2 = this.delayedDisconnect;
        synchronized (object2) {
            while (!this.delayedDisconnect.isEmpty()) {
                int n2 = this.delayedDisconnect.remove(0);
                switch (n2) {
                    case 21: {
                        LuaEventManager.triggerEvent("OnDisconnect");
                        break;
                    }
                    case 18: {
                        LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_AlreadyConnected"));
                        break;
                    }
                    case 32: {
                        LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_ConnectionLost"));
                        break;
                    }
                    case 23: {
                        LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_Banned"));
                        break;
                    }
                    case 17: {
                        LuaEventManager.triggerEvent("OnConnectFailed", null);
                        break;
                    }
                    case 24: {
                        LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_InvalidServerPassword"));
                    }
                }
            }
        }
        if (!this.bPlayerConnectSent) {
            for (int i = 0; i < MainLoopNetData.size(); ++i) {
                ZomboidNetData zomboidNetData = MainLoopNetData.get(i);
                if (this.gameLoadingDealWithNetData(zomboidNetData)) continue;
                LoadingMainLoopNetData.add(zomboidNetData);
            }
            MainLoopNetData.clear();
            WorldStreamer.instance.updateMain();
            return;
        }
        if (!LoadingMainLoopNetData.isEmpty()) {
            DebugLog.log(DebugType.Network, "Processing delayed packets...");
            MainLoopNetData.addAll(0, LoadingMainLoopNetData);
            LoadingMainLoopNetData.clear();
        }
        if (!DelayedCoopNetData.isEmpty() && IsoWorld.instance.AddCoopPlayers.isEmpty()) {
            DebugLog.log(DebugType.Network, "Processing delayed coop packets...");
            MainLoopNetData.addAll(0, DelayedCoopNetData);
            DelayedCoopNetData.clear();
        }
        long l = System.currentTimeMillis();
        for (n = 0; n < MainLoopNetData.size(); ++n) {
            object = MainLoopNetData.get(n);
            if (((ZomboidNetData)object).time + (long)this.DEBUG_PING > l) continue;
            this.mainLoopDealWithNetData((ZomboidNetData)object);
            MainLoopNetData.remove(n--);
        }
        for (n = 0; n < IsoWorld.instance.CurrentCell.getObjectList().size(); ++n) {
            object = IsoWorld.instance.CurrentCell.getObjectList().get(n);
            if (!(object instanceof IsoPlayer) || ((IsoPlayer)object).isLocalPlayer() || this.getPlayers().contains(object)) continue;
            if (Core.bDebug) {
                DebugLog.log("Disconnected/Distant player " + ((IsoPlayer)object).username + " in CurrentCell.getObjectList() removed");
            }
            IsoWorld.instance.CurrentCell.getObjectList().remove(n--);
        }
        try {
            this.sendAddedRemovedItems(false);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            ExceptionLogger.logException(exception);
        }
        try {
            VehicleManager.instance.clientUpdate();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        this.objectSyncReq.sendRequests(connection);
        this.worldObjectsSyncReq.sendRequests(connection);
        WorldStreamer.instance.updateMain();
        MPStatisticClient.getInstance().update();
        this.timeSinceKeepAlive += GameTime.getInstance().getMultiplier();
    }

    public void smashWindow(IsoWindow isoWindow, int n) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SmashWindow.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(isoWindow.square.getX());
        byteBufferWriter.putInt(isoWindow.square.getY());
        byteBufferWriter.putInt(isoWindow.square.getZ());
        byteBufferWriter.putByte((byte)isoWindow.square.getObjects().indexOf(isoWindow));
        byteBufferWriter.putByte((byte)n);
        PacketTypes.PacketType.SmashWindow.send(connection);
    }

    public static void getCustomModData() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.getModData.doPacket(byteBufferWriter);
        PacketTypes.PacketType.getModData.send(connection);
    }

    static void receiveStitch(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            int n = byteBuffer.getInt();
            boolean bl = byteBuffer.get() == 1;
            float f = byteBuffer.getFloat();
            isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(n)).setStitched(bl);
            isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(n)).setStitchTime(f);
        }
    }

    static void receiveBandage(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            int n = byteBuffer.getInt();
            boolean bl = byteBuffer.get() == 1;
            float f = byteBuffer.getFloat();
            boolean bl2 = byteBuffer.get() == 1;
            String string = GameWindow.ReadStringUTF(byteBuffer);
            isoPlayer.getBodyDamage().SetBandaged(n, bl, f, bl2, string);
        }
    }

    static void receivePingFromClient(ByteBuffer byteBuffer, short s) {
        MPStatistics.parse(byteBuffer);
    }

    static void receiveWoundInfection(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            int n = byteBuffer.getInt();
            boolean bl = byteBuffer.get() == 1;
            isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(n)).setInfectedWound(bl);
        }
    }

    static void receiveDisinfect(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            int n = byteBuffer.getInt();
            float f = byteBuffer.getFloat();
            BodyPart bodyPart = isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(n));
            bodyPart.setAlcoholLevel(bodyPart.getAlcoholLevel() + f);
        }
    }

    static void receiveSplint(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            int n = byteBuffer.getInt();
            boolean bl = byteBuffer.get() == 1;
            String string = bl ? GameWindow.ReadStringUTF(byteBuffer) : null;
            float f = bl ? byteBuffer.getFloat() : 0.0f;
            BodyPart bodyPart = isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(n));
            bodyPart.setSplint(bl, f);
            bodyPart.setSplintItem(string);
        }
    }

    static void receiveRemoveGlass(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            int n = byteBuffer.getInt();
            isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(n)).setHaveGlass(false);
        }
    }

    static void receiveRemoveBullet(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            int n = byteBuffer.getInt();
            int n2 = byteBuffer.getInt();
            isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(n)).setHaveBullet(false, n2);
        }
    }

    static void receiveCleanBurn(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            int n = byteBuffer.getInt();
            isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(n)).setNeedBurnWash(false);
        }
    }

    static void receiveAdditionalPain(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            int n = byteBuffer.getInt();
            float f = byteBuffer.getFloat();
            BodyPart bodyPart = isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(n));
            bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + f);
        }
    }

    private void delayPacket(int n, int n2, int n3) {
        if (IsoWorld.instance == null) {
            return;
        }
        for (int i = 0; i < IsoWorld.instance.AddCoopPlayers.size(); ++i) {
            AddCoopPlayer addCoopPlayer = IsoWorld.instance.AddCoopPlayers.get(i);
            if (!addCoopPlayer.isLoadingThisSquare(n, n2)) continue;
            this.delayPacket = true;
            return;
        }
    }

    private void mainLoopDealWithNetData(ZomboidNetData zomboidNetData) {
        ByteBuffer byteBuffer = zomboidNetData.buffer;
        int n = byteBuffer.position();
        this.delayPacket = false;
        if (zomboidNetData.type >= 0 && zomboidNetData.type < 256) {
            short s = zomboidNetData.type;
            this.packetCountsFromServer[s] = this.packetCountsFromServer[s] + 1L;
        }
        try {
            this.mainLoopHandlePacketInternal(zomboidNetData, byteBuffer);
            if (this.delayPacket) {
                byteBuffer.position(n);
                DelayedCoopNetData.add(zomboidNetData);
                return;
            }
        }
        catch (Exception exception) {
            DebugLog.Network.printException(exception, "Error with packet of type: " + zomboidNetData.type, LogSeverity.Error);
        }
        ZomboidNetDataPool.instance.discard(zomboidNetData);
    }

    private void mainLoopHandlePacketInternal(ZomboidNetData zomboidNetData, ByteBuffer byteBuffer) throws IOException {
        if (!DebugOptions.instance.Network.Client.MainLoop.getValue()) {
            return;
        }
        PacketTypes.PacketType packetType = PacketTypes.packetTypes.get(zomboidNetData.type);
        packetType.onMainLoopHandlePacketInternal(byteBuffer, zomboidNetData.type);
    }

    static void receiveAddBrokenGlass(ByteBuffer byteBuffer, short s) {
        int n;
        int n2;
        int n3 = byteBuffer.getInt();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n3, n2 = byteBuffer.getInt(), n = byteBuffer.getInt());
        if (isoGridSquare != null) {
            isoGridSquare.addBrokenGlass();
        }
    }

    static void receivePlayerDamageFromCarCrash(ByteBuffer byteBuffer, short s) {
        float f = byteBuffer.getFloat();
        if (IsoPlayer.getInstance().getVehicle() == null) {
            DebugLog.Multiplayer.error("Receive damage from car crash, can't find vehicle");
            return;
        }
        IsoPlayer.getInstance().getVehicle().addRandomDamageFromCrash(IsoPlayer.getInstance(), f);
    }

    static void receivePacketCounts(ByteBuffer byteBuffer, short s) {
        for (int i = 0; i < 256; ++i) {
            GameClient.instance.packetCountsFromAllClients[i] = byteBuffer.getLong();
        }
    }

    public void requestPacketCounts() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PacketCounts.doPacket(byteBufferWriter);
        PacketTypes.PacketType.PacketCounts.send(connection);
    }

    public KahluaTable getPacketCounts(int n) {
        long[] arrl = n == 1 ? this.packetCountsFromAllClients : this.packetCountsFromServer;
        for (int i = 0; i < 256; ++i) {
            this.packetCountsTable.rawset(i + 1, (Object)BoxedStaticValues.toDouble(arrl[i]));
        }
        return this.packetCountsTable;
    }

    public static boolean IsClientPaused() {
        return isPaused;
    }

    static void receiveStartPause(ByteBuffer byteBuffer, short s) {
        isPaused = true;
        LuaEventManager.triggerEvent("OnServerStartSaving");
    }

    static void receiveStopPause(ByteBuffer byteBuffer, short s) {
        isPaused = false;
        LuaEventManager.triggerEvent("OnServerFinishSaving");
    }

    static void receiveChatMessageToPlayer(ByteBuffer byteBuffer, short s) {
        ChatManager.getInstance().processChatMessagePacket(byteBuffer);
    }

    static void receivePlayerConnectedToChat(ByteBuffer byteBuffer, short s) {
        ChatManager.getInstance().setFullyConnected();
    }

    static void receivePlayerJoinChat(ByteBuffer byteBuffer, short s) {
        ChatManager.getInstance().processJoinChatPacket(byteBuffer);
    }

    static void receiveInvMngRemoveItem(ByteBuffer byteBuffer, short s) {
        int n = byteBuffer.getInt();
        InventoryItem inventoryItem = IsoPlayer.getInstance().getInventory().getItemWithIDRecursiv(n);
        if (inventoryItem == null) {
            DebugLog.log("ERROR: invMngRemoveItem can not find " + n + " item.");
            return;
        }
        IsoPlayer.getInstance().removeWornItem(inventoryItem);
        if (inventoryItem.getCategory().equals("Clothing")) {
            LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
        }
        if (inventoryItem == IsoPlayer.getInstance().getPrimaryHandItem()) {
            IsoPlayer.getInstance().setPrimaryHandItem(null);
            LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
        } else if (inventoryItem == IsoPlayer.getInstance().getSecondaryHandItem()) {
            IsoPlayer.getInstance().setSecondaryHandItem(null);
            LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
        }
        boolean bl = IsoPlayer.getInstance().getInventory().removeItemWithIDRecurse(n);
        if (!bl) {
            DebugLog.log("ERROR: GameClient.invMngRemoveItem can not remove item " + n);
        }
    }

    static void receiveInvMngGetItem(ByteBuffer byteBuffer, short s) throws IOException {
        int n = byteBuffer.getInt();
        InventoryItem inventoryItem = null;
        try {
            inventoryItem = InventoryItem.loadItem(byteBuffer, 186);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (inventoryItem != null) {
            IsoPlayer.getInstance().getInventory().addItem(inventoryItem);
        }
    }

    static void receiveInvMngReqItem(ByteBuffer byteBuffer, short s) throws IOException {
        int n = 0;
        String string = null;
        if (byteBuffer.get() == 1) {
            string = GameWindow.ReadString(byteBuffer);
        } else {
            n = byteBuffer.getInt();
        }
        short s2 = byteBuffer.getShort();
        InventoryItem inventoryItem = null;
        if (string == null) {
            inventoryItem = IsoPlayer.getInstance().getInventory().getItemWithIDRecursiv(n);
            if (inventoryItem == null) {
                DebugLog.log("ERROR: invMngRemoveItem can not find " + n + " item.");
                return;
            }
        } else {
            inventoryItem = InventoryItemFactory.CreateItem(string);
        }
        if (inventoryItem != null) {
            if (string == null) {
                IsoPlayer.getInstance().removeWornItem(inventoryItem);
                if (inventoryItem.getCategory().equals("Clothing")) {
                    LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
                }
                if (inventoryItem == IsoPlayer.getInstance().getPrimaryHandItem()) {
                    IsoPlayer.getInstance().setPrimaryHandItem(null);
                    LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
                } else if (inventoryItem == IsoPlayer.getInstance().getSecondaryHandItem()) {
                    IsoPlayer.getInstance().setSecondaryHandItem(null);
                    LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
                }
                IsoPlayer.getInstance().getInventory().removeItemWithIDRecurse(inventoryItem.getID());
            } else {
                IsoPlayer.getInstance().getInventory().RemoveOneOf(string.split("\\.")[1]);
            }
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.InvMngGetItem.doPacket(byteBufferWriter);
            byteBufferWriter.putShort(s2);
            inventoryItem.saveWithSize(byteBufferWriter.bb, false);
            PacketTypes.PacketType.InvMngGetItem.send(connection);
        }
    }

    public static void invMngRequestItem(long l, String string, IsoPlayer isoPlayer) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.InvMngReqItem.doPacket(byteBufferWriter);
        if (string != null) {
            byteBufferWriter.putByte((byte)1);
            byteBufferWriter.putUTF(string);
        } else {
            byteBufferWriter.putByte((byte)0);
            byteBufferWriter.putLong(l);
        }
        byteBufferWriter.putShort(IsoPlayer.getInstance().getOnlineID());
        byteBufferWriter.putShort(isoPlayer.getOnlineID());
        PacketTypes.PacketType.InvMngReqItem.send(connection);
    }

    public static void invMngRequestRemoveItem(long l, IsoPlayer isoPlayer) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.InvMngRemoveItem.doPacket(byteBufferWriter);
        byteBufferWriter.putLong(l);
        byteBufferWriter.putShort(isoPlayer.getOnlineID());
        PacketTypes.PacketType.InvMngRemoveItem.send(connection);
    }

    static void receiveSyncFaction(ByteBuffer byteBuffer, short s) {
        int n;
        String string = GameWindow.ReadString(byteBuffer);
        String string2 = GameWindow.ReadString(byteBuffer);
        int n2 = byteBuffer.getInt();
        Faction faction = Faction.getFaction(string);
        if (faction == null) {
            faction = new Faction(string, string2);
            Faction.getFactions().add(faction);
        }
        faction.getPlayers().clear();
        if (byteBuffer.get() == 1) {
            faction.setTag(GameWindow.ReadString(byteBuffer));
            faction.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0f));
        }
        for (n = 0; n < n2; ++n) {
            faction.getPlayers().add(GameWindow.ReadString(byteBuffer));
        }
        faction.setOwner(string2);
        int n3 = n = byteBuffer.get() == 1 ? 1 : 0;
        if (n != 0) {
            Faction.getFactions().remove(faction);
            DebugLog.log("faction: removed " + string + " owner=" + faction.getOwner());
        }
        LuaEventManager.triggerEvent("SyncFaction", string);
    }

    static void receiveSyncNonPvpZone(ByteBuffer byteBuffer, short s) {
        boolean bl;
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        int n4 = byteBuffer.getInt();
        String string = GameWindow.ReadString(byteBuffer);
        NonPvpZone nonPvpZone = NonPvpZone.getZoneByTitle(string);
        if (nonPvpZone == null) {
            nonPvpZone = NonPvpZone.addNonPvpZone(string, n, n2, n3, n4);
        }
        if (nonPvpZone == null) {
            return;
        }
        boolean bl2 = bl = byteBuffer.get() == 1;
        if (bl) {
            NonPvpZone.removeNonPvpZone(string, true);
        }
    }

    static void receiveChangeTextColor(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer == null) {
            return;
        }
        float f = byteBuffer.getFloat();
        float f2 = byteBuffer.getFloat();
        float f3 = byteBuffer.getFloat();
        isoPlayer.setSpeakColourInfo(new ColorInfo(f, f2, f3, 1.0f));
    }

    static void receivePlaySoundEveryPlayer(ByteBuffer byteBuffer, short s) {
        String string = GameWindow.ReadString(byteBuffer);
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        DebugLog.log(DebugType.Sound, "sound: received " + string + " at " + n + "," + n2 + "," + n3);
        if (!Core.SoundDisabled) {
            FMOD_STUDIO_EVENT_DESCRIPTION fMOD_STUDIO_EVENT_DESCRIPTION = FMODManager.instance.getEventDescription(string);
            if (fMOD_STUDIO_EVENT_DESCRIPTION == null) {
                return;
            }
            long l = javafmod.FMOD_Studio_System_CreateEventInstance((long)fMOD_STUDIO_EVENT_DESCRIPTION.address);
            if (l <= 0L) {
                return;
            }
            javafmod.FMOD_Studio_EventInstance_SetVolume((long)l, (float)((float)Core.getInstance().getOptionAmbientVolume() / 20.0f));
            javafmod.FMOD_Studio_EventInstance3D((long)l, (float)n, (float)n2, (float)n3);
            javafmod.FMOD_Studio_StartEvent((long)l);
            javafmod.FMOD_Studio_ReleaseEventInstance((long)l);
        }
    }

    static void receiveCataplasm(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            int n = byteBuffer.getInt();
            float f = byteBuffer.getFloat();
            float f2 = byteBuffer.getFloat();
            float f3 = byteBuffer.getFloat();
            if (f > 0.0f) {
                isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(n)).setPlantainFactor(f);
            }
            if (f2 > 0.0f) {
                isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(n)).setComfreyFactor(f2);
            }
            if (f3 > 0.0f) {
                isoPlayer.getBodyDamage().getBodyPart(BodyPartType.FromIndex(n)).setGarlicFactor(f3);
            }
        }
    }

    static void receiveStopFire(ByteBuffer byteBuffer, short s) {
        int n;
        int n2;
        int n3 = byteBuffer.getInt();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n3, n2 = byteBuffer.getInt(), n = byteBuffer.getInt());
        if (isoGridSquare == null) {
            return;
        }
        isoGridSquare.stopFire();
    }

    static void receiveAddAlarm(ByteBuffer byteBuffer, short s) {
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        DebugLog.log(DebugType.Multiplayer, "ReceiveAlarm at [ " + n + " , " + n2 + " ]");
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, 0);
        if (isoGridSquare == null || isoGridSquare.getBuilding() == null || isoGridSquare.getBuilding().getDef() == null) {
            return;
        }
        isoGridSquare.getBuilding().getDef().bAlarmed = true;
        AmbientStreamManager.instance.doAlarm(isoGridSquare.room.def);
    }

    static void receiveAddExplosiveTrap(ByteBuffer byteBuffer, short s) {
        int n;
        int n2;
        int n3 = byteBuffer.getInt();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n3, n2 = byteBuffer.getInt(), n = byteBuffer.getInt());
        if (isoGridSquare != null) {
            InventoryItem inventoryItem = null;
            try {
                inventoryItem = InventoryItem.loadItem(byteBuffer, 186);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            HandWeapon handWeapon = inventoryItem != null ? (HandWeapon)inventoryItem : null;
            IsoTrap isoTrap = new IsoTrap(handWeapon, isoGridSquare.getCell(), isoGridSquare);
            if (!handWeapon.isInstantExplosion()) {
                isoGridSquare.AddTileObject(isoTrap);
            } else {
                isoTrap.triggerExplosion(handWeapon.getSensorRange() > 0);
            }
        }
    }

    static void receiveTeleport(ByteBuffer byteBuffer, short s) {
        byte by = byteBuffer.get();
        IsoPlayer isoPlayer = IsoPlayer.players[by];
        if (isoPlayer == null || isoPlayer.isDead()) {
            return;
        }
        if (isoPlayer.getVehicle() != null) {
            isoPlayer.getVehicle().exit(isoPlayer);
            LuaEventManager.triggerEvent("OnExitVehicle", isoPlayer);
        }
        isoPlayer.setX(byteBuffer.getFloat());
        isoPlayer.setY(byteBuffer.getFloat());
        isoPlayer.setZ(byteBuffer.getFloat());
        isoPlayer.setLx(isoPlayer.getX());
        isoPlayer.setLy(isoPlayer.getY());
        isoPlayer.setLz(isoPlayer.getZ());
    }

    static void receiveRemoveBlood(ByteBuffer byteBuffer, short s) {
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        boolean bl = byteBuffer.get() == 1;
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare != null) {
            isoGridSquare.removeBlood(true, bl);
        }
    }

    static void receiveSyncThumpable(ByteBuffer byteBuffer, short s) {
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        byte by = byteBuffer.get();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare == null) {
            instance.delayPacket(n, n2, n3);
            return;
        }
        if (by >= 0 && by < isoGridSquare.getObjects().size()) {
            IsoObject isoObject = isoGridSquare.getObjects().get(by);
            if (isoObject instanceof IsoThumpable) {
                IsoThumpable isoThumpable = (IsoThumpable)isoObject;
                isoThumpable.lockedByCode = byteBuffer.getInt();
                isoThumpable.lockedByPadlock = byteBuffer.get() == 1;
                isoThumpable.keyId = byteBuffer.getInt();
            } else {
                DebugLog.log("syncThumpable: expected IsoThumpable index=" + by + " is invalid x,y,z=" + n + "," + n2 + "," + n3);
            }
        } else {
            DebugLog.log("syncThumpable: index=" + by + " is invalid x,y,z=" + n + "," + n2 + "," + n3);
        }
    }

    static void receiveSyncDoorKey(ByteBuffer byteBuffer, short s) {
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        byte by = byteBuffer.get();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare == null) {
            instance.delayPacket(n, n2, n3);
            return;
        }
        if (by >= 0 && by < isoGridSquare.getObjects().size()) {
            IsoObject isoObject = isoGridSquare.getObjects().get(by);
            if (isoObject instanceof IsoDoor) {
                IsoDoor isoDoor = (IsoDoor)isoObject;
                isoDoor.keyId = byteBuffer.getInt();
            } else {
                DebugLog.log("SyncDoorKey: expected IsoDoor index=" + by + " is invalid x,y,z=" + n + "," + n2 + "," + n3);
            }
        } else {
            DebugLog.log("SyncDoorKey: index=" + by + " is invalid x,y,z=" + n + "," + n2 + "," + n3);
        }
    }

    static void receiveConstructedZone(ByteBuffer byteBuffer, short s) {
        int n;
        int n2;
        int n3 = byteBuffer.getInt();
        IsoMetaGrid.Zone zone = IsoWorld.instance.MetaGrid.getZoneAt(n3, n2 = byteBuffer.getInt(), n = byteBuffer.getInt());
        if (zone != null) {
            zone.setHaveConstruction(true);
        }
    }

    static void receiveAddCoopPlayer(ByteBuffer byteBuffer, short s) {
        boolean bl = byteBuffer.get() == 1;
        byte by = byteBuffer.get();
        if (bl) {
            for (int i = 0; i < IsoWorld.instance.AddCoopPlayers.size(); ++i) {
                IsoWorld.instance.AddCoopPlayers.get(i).accessGranted(by);
            }
        } else {
            String string = GameWindow.ReadStringUTF(byteBuffer);
            for (int i = 0; i < IsoWorld.instance.AddCoopPlayers.size(); ++i) {
                IsoWorld.instance.AddCoopPlayers.get(i).accessDenied(by, string);
            }
        }
    }

    static void receiveZombieDescriptors(ByteBuffer byteBuffer, short s) {
        try {
            SharedDescriptors.Descriptor descriptor = new SharedDescriptors.Descriptor();
            descriptor.load(byteBuffer, 186);
            SharedDescriptors.registerPlayerZombieDescriptor(descriptor);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void checksumServer() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(checksum + ScriptManager.instance.getChecksum());
        PacketTypes.PacketType.Checksum.send(connection);
    }

    static void receiveRegisterZone(ByteBuffer byteBuffer, short s) {
        String string = GameWindow.ReadString(byteBuffer);
        String string2 = GameWindow.ReadString(byteBuffer);
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        int n4 = byteBuffer.getInt();
        int n5 = byteBuffer.getInt();
        int n6 = byteBuffer.getInt();
        ArrayList<IsoMetaGrid.Zone> arrayList = IsoWorld.instance.getMetaGrid().getZonesAt(n, n2, n3);
        boolean bl = false;
        for (IsoMetaGrid.Zone zone : arrayList) {
            if (!string2.equals(zone.getType())) continue;
            bl = true;
            zone.setName(string);
            zone.setLastActionTimestamp(n6);
        }
        if (!bl) {
            IsoWorld.instance.getMetaGrid().registerZone(string, string2, n, n2, n3, n4, n5);
        }
    }

    static void receiveAddXP(ByteBuffer byteBuffer, short s) {
        byte by = byteBuffer.get();
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        IsoPlayer isoPlayer = IsoPlayer.players[by];
        if (isoPlayer == null || isoPlayer.isDead()) {
            return;
        }
        PerkFactory.Perk perk = PerkFactory.Perks.fromIndex(n);
        isoPlayer.getXp().AddXP(perk, (float)n2);
    }

    static void receiveAddXpCommand(ByteBuffer byteBuffer, short s) {
        IsoPlayer isoPlayer = IDToPlayerMap.get(byteBuffer.getShort());
        PerkFactory.Perk perk = PerkFactory.Perks.fromIndex(byteBuffer.getInt());
        if (isoPlayer != null && !isoPlayer.isDead()) {
            isoPlayer.getXp().AddXP(perk, (float)byteBuffer.getInt());
        }
    }

    public void sendAddXpFromPlayerStatsUI(IsoPlayer isoPlayer, PerkFactory.Perk perk, int n, boolean bl, boolean bl2) {
        if (!GameClient.canModifyPlayerStats()) {
            return;
        }
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.AddXpFromPlayerStatsUI.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(isoPlayer.getOnlineID());
        if (!bl2) {
            byteBufferWriter.putInt(0);
            byteBufferWriter.putInt(perk.index());
            byteBufferWriter.putInt(n);
            byteBufferWriter.putByte((byte)(bl ? 1 : 0));
        }
        PacketTypes.PacketType.AddXpFromPlayerStatsUI.send(connection);
    }

    static void receiveSyncXP(ByteBuffer byteBuffer, short s) {
        IsoPlayer isoPlayer = IDToPlayerMap.get(byteBuffer.getShort());
        if (isoPlayer != null && !isoPlayer.isDead()) {
            try {
                isoPlayer.getXp().load(byteBuffer, 186);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    public void sendSyncXp(IsoPlayer isoPlayer) {
        if (!GameClient.canModifyPlayerStats()) {
            return;
        }
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncXP.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(isoPlayer.getOnlineID());
        try {
            isoPlayer.getXp().save(byteBufferWriter.bb);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        PacketTypes.PacketType.SyncXP.send(connection);
    }

    public void sendTransactionID(IsoPlayer isoPlayer) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SendTransactionID.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(isoPlayer.getOnlineID());
        byteBufferWriter.putInt(isoPlayer.getTransactionID());
        PacketTypes.PacketType.SendTransactionID.send(connection);
    }

    static void receiveUserlog(ByteBuffer byteBuffer, short s) {
        ArrayList<Userlog> arrayList = new ArrayList<Userlog>();
        int n = byteBuffer.getInt();
        String string = GameWindow.ReadString(byteBuffer);
        for (int i = 0; i < n; ++i) {
            arrayList.add(new Userlog(string, Userlog.UserlogType.fromIndex(byteBuffer.getInt()).toString(), GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer), byteBuffer.getInt()));
        }
        LuaEventManager.triggerEvent("OnReceiveUserlog", string, arrayList);
    }

    static void receiveAddXpFromPlayerStatsUI(ByteBuffer byteBuffer, short s) {
        IsoPlayer isoPlayer = IDToPlayerMap.get(byteBuffer.getShort());
        int n = byteBuffer.getInt();
        if (isoPlayer != null && !isoPlayer.isDead() && n == 0) {
            PerkFactory.Perk perk = PerkFactory.Perks.fromIndex(byteBuffer.getInt());
            isoPlayer.getXp().AddXP(perk, byteBuffer.getInt(), false, byteBuffer.get() == 1, false, true);
        }
    }

    static void receivePing(ByteBuffer byteBuffer, short s) {
        String string = GameWindow.ReadString(byteBuffer);
        String string2 = byteBuffer.getInt() - 1 + "/" + byteBuffer.getInt();
        LuaEventManager.triggerEvent("ServerPinged", string, string2);
        connection.forceDisconnect();
        askPing = false;
    }

    static void receiveChecksumLoading(ByteBuffer byteBuffer, short s) {
        NetChecksum.comparer.clientPacket(byteBuffer);
    }

    static void receiveKickedLoading(ByteBuffer byteBuffer, short s) {
        GameWindow.kickReason = GameWindow.ReadStringUTF(byteBuffer);
        GameWindow.bServerDisconnected = true;
    }

    static void receiveServerMapLoading(ByteBuffer byteBuffer, short s) {
        ClientServerMap.receivePacket(byteBuffer);
    }

    static void receiveChangeSafety(ByteBuffer byteBuffer, short s) {
        IsoPlayer isoPlayer = IDToPlayerMap.get(byteBuffer.getShort());
        if (isoPlayer != null) {
            isoPlayer.setSafety(byteBuffer.get() == 1);
        }
    }

    static void receiveAddItemInInventory(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        String string = GameWindow.ReadString(byteBuffer);
        int n = byteBuffer.getInt();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null && !isoPlayer.isDead()) {
            isoPlayer.getInventory().AddItems(string, n);
        }
    }

    static void receiveKicked(ByteBuffer byteBuffer, short s) {
        String string = GameWindow.ReadString(byteBuffer);
        if (string != null && !string.equals("")) {
            ChatManager.getInstance().showServerChatMessage(string);
        }
        GameClient.connection.username = null;
        GameWindow.savePlayer();
        GameWindow.bServerDisconnected = true;
        GameWindow.kickReason = string;
        connection.forceDisconnect();
        connection.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addDisconnectPacket(int n) {
        ArrayList<Integer> arrayList = this.delayedDisconnect;
        synchronized (arrayList) {
            this.delayedDisconnect.add(n);
        }
    }

    public void connectionLost() {
        this.bConnectionLost = true;
        positions.clear();
    }

    public static void SendCommandToServer(String string) {
        if (ServerOptions.clientOptionsList == null) {
            ServerOptions.initClientCommandsHelp();
        }
        if (string.startsWith("/roll")) {
            try {
                int n = Integer.parseInt(string.split(" ")[1]);
                if (n > 100) {
                    ChatManager.getInstance().showServerChatMessage(ServerOptions.clientOptionsList.get("roll"));
                    return;
                }
            }
            catch (Exception exception) {
                ChatManager.getInstance().showServerChatMessage(ServerOptions.clientOptionsList.get("roll"));
                return;
            }
            if (!IsoPlayer.getInstance().getInventory().contains("Dice") && accessLevel.equals("")) {
                ChatManager.getInstance().showServerChatMessage(ServerOptions.clientOptionsList.get("roll"));
                return;
            }
        }
        if (string.startsWith("/card") && !IsoPlayer.getInstance().getInventory().contains("CardDeck") && accessLevel.equals("")) {
            ChatManager.getInstance().showServerChatMessage(ServerOptions.clientOptionsList.get("card"));
            return;
        }
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ReceiveCommand.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(string);
        PacketTypes.PacketType.ReceiveCommand.send(connection);
    }

    public static void sendServerPing(long l) {
        if (connection != null) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.PingFromClient.doPacket(byteBufferWriter);
            byteBufferWriter.putLong(l);
            PacketTypes.PacketType.PingFromClient.send(connection);
        }
    }

    private boolean gameLoadingDealWithNetData(ZomboidNetData zomboidNetData) {
        ByteBuffer byteBuffer = zomboidNetData.buffer;
        try {
            PacketTypes.PacketType packetType = PacketTypes.packetTypes.get(zomboidNetData.type);
            return packetType.onGameLoadingDealWithNetData(byteBuffer, zomboidNetData.type);
        }
        catch (Exception exception) {
            DebugLog.log(DebugType.Network, "Error with packet of type: " + zomboidNetData.type);
            exception.printStackTrace();
            ZomboidNetDataPool.instance.discard(zomboidNetData);
            return true;
        }
    }

    static void receiveWorldMessage(ByteBuffer byteBuffer, short s) {
        String string = GameWindow.ReadStringUTF(byteBuffer);
        String string2 = GameWindow.ReadString(byteBuffer);
        string2 = string2.replaceAll("<", "&lt;");
        string2 = string2.replaceAll(">", "&gt;");
        ChatManager.getInstance().addMessage(string, string2);
    }

    static void receiveReloadOptions(ByteBuffer byteBuffer, short s) {
        int n = byteBuffer.getInt();
        for (int i = 0; i < n; ++i) {
            ServerOptions.instance.putOption(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer));
        }
    }

    static void receiveStartRain(ByteBuffer byteBuffer, short s) {
        RainManager.setRandRainMin(byteBuffer.getInt());
        RainManager.setRandRainMax(byteBuffer.getInt());
        RainManager.startRaining();
        RainManager.RainDesiredIntensity = byteBuffer.getFloat();
    }

    static void receiveStopRain(ByteBuffer byteBuffer, short s) {
        RainManager.stopRaining();
    }

    static void receiveWeather(ByteBuffer byteBuffer, short s) {
        GameTime gameTime = GameTime.getInstance();
        gameTime.setDawn(byteBuffer.get() & 0xFF);
        gameTime.setDusk(byteBuffer.get() & 0xFF);
        gameTime.setThunderDay(byteBuffer.get() == 1);
        gameTime.setMoon(byteBuffer.getFloat());
        gameTime.setAmbientMin(byteBuffer.getFloat());
        gameTime.setAmbientMax(byteBuffer.getFloat());
        gameTime.setViewDistMin(byteBuffer.getFloat());
        gameTime.setViewDistMax(byteBuffer.getFloat());
        IsoWorld.instance.setGlobalTemperature(byteBuffer.getFloat());
        IsoWorld.instance.setWeather(GameWindow.ReadStringUTF(byteBuffer));
        ErosionMain.getInstance().receiveState(byteBuffer);
    }

    static void receiveSyncClock(ByteBuffer byteBuffer, short s) {
        GameTime gameTime = GameTime.getInstance();
        boolean bl = bFastForward;
        bFastForward = byteBuffer.get() == 1;
        float f = byteBuffer.getFloat();
        float f2 = gameTime.getTimeOfDay() - gameTime.getLastTimeOfDay();
        gameTime.setTimeOfDay(f);
        gameTime.setLastTimeOfDay(f - f2);
        if (gameTime.getLastTimeOfDay() < 0.0f) {
            gameTime.setLastTimeOfDay(f - f2 + 24.0f);
        }
        gameTime.ServerLastTimeOfDay = gameTime.ServerTimeOfDay;
        gameTime.ServerTimeOfDay = f;
        if (gameTime.ServerLastTimeOfDay <= 7.0f && gameTime.ServerTimeOfDay > 7.0f) {
            gameTime.setNightsSurvived(gameTime.getNightsSurvived() + 1);
        }
        if (gameTime.ServerLastTimeOfDay > gameTime.ServerTimeOfDay) {
            ++gameTime.ServerNewDays;
        }
    }

    static void receiveClientCommand(ByteBuffer byteBuffer, short s) {
        String string = GameWindow.ReadString(byteBuffer);
        String string2 = GameWindow.ReadString(byteBuffer);
        boolean bl = byteBuffer.get() == 1;
        KahluaTable kahluaTable = null;
        if (bl) {
            kahluaTable = LuaManager.platform.newTable();
            try {
                TableNetworkUtils.load(kahluaTable, byteBuffer);
            }
            catch (Exception exception) {
                exception.printStackTrace();
                return;
            }
        }
        LuaEventManager.triggerEvent("OnServerCommand", string, string2, (Object)kahluaTable);
    }

    static void receiveGlobalObjects(ByteBuffer byteBuffer, short s) throws IOException {
        CGlobalObjectNetwork.receive(byteBuffer);
    }

    private boolean receiveLargeFilePart(ByteBuffer byteBuffer, String string) {
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        File file = new File(string);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, n2 > 0);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);){
            bufferedOutputStream.write(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        return n2 + n3 >= n;
    }

    static void receiveRequestData(ByteBuffer byteBuffer, short s) {
        boolean bl;
        boolean bl2;
        String string = GameWindow.ReadStringUTF(byteBuffer);
        if ("descriptors.bin".equals(string)) {
            try {
                instance.receiveDataZombieDescriptors(byteBuffer);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
            GameClient.instance.request = RequestState.ReceivedDescriptors;
        }
        if ("playerzombiedesc".equals(string)) {
            try {
                instance.receivePlayerZombieDescriptors(byteBuffer);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
            GameClient.instance.request = RequestState.ReceivedPlayerZombieDescriptors;
        }
        if ("map_meta.bin".equals(string) && (bl2 = instance.receiveLargeFilePart(byteBuffer, ZomboidFileSystem.instance.getFileNameInCurrentSave("map_meta.bin")))) {
            GameClient.instance.request = RequestState.ReceivedMetaGrid;
        }
        if ("map_zone.bin".equals(string) && (bl = instance.receiveLargeFilePart(byteBuffer, ZomboidFileSystem.instance.getFileNameInCurrentSave("map_zone.bin")))) {
            GameClient.instance.request = RequestState.ReceivedMapZone;
        }
    }

    public void GameLoadingRequestData() {
        this.request = RequestState.Start;
        while (this.request != RequestState.Complete) {
            switch (this.request) {
                case Start: {
                    ByteBufferWriter byteBufferWriter = connection.startPacket();
                    PacketTypes.PacketType.RequestData.doPacket(byteBufferWriter);
                    byteBufferWriter.putUTF("descriptors.bin");
                    PacketTypes.PacketType.RequestData.send(connection);
                    this.request = RequestState.RequestDescriptors;
                    break;
                }
                case RequestDescriptors: {
                    break;
                }
                case ReceivedDescriptors: {
                    ByteBufferWriter byteBufferWriter = connection.startPacket();
                    PacketTypes.PacketType.RequestData.doPacket(byteBufferWriter);
                    byteBufferWriter.putUTF("map_meta.bin");
                    PacketTypes.PacketType.RequestData.send(connection);
                    this.request = RequestState.RequestMetaGrid;
                    break;
                }
                case RequestMetaGrid: {
                    break;
                }
                case ReceivedMetaGrid: {
                    ByteBufferWriter byteBufferWriter = connection.startPacket();
                    PacketTypes.PacketType.RequestData.doPacket(byteBufferWriter);
                    byteBufferWriter.putUTF("map_zone.bin");
                    PacketTypes.PacketType.RequestData.send(connection);
                    this.request = RequestState.RequestMapZone;
                    break;
                }
                case RequestMapZone: {
                    break;
                }
                case ReceivedMapZone: {
                    ByteBufferWriter byteBufferWriter = connection.startPacket();
                    PacketTypes.PacketType.RequestData.doPacket(byteBufferWriter);
                    byteBufferWriter.putUTF("playerzombiedesc");
                    PacketTypes.PacketType.RequestData.send(connection);
                    this.request = RequestState.RequestPlayerZombieDescriptors;
                    break;
                }
                case RequestPlayerZombieDescriptors: {
                    break;
                }
                case ReceivedPlayerZombieDescriptors: {
                    this.request = RequestState.Complete;
                    break;
                }
            }
            try {
                Thread.sleep(30L);
            }
            catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }

    static void receiveMetaGrid(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        short s3 = byteBuffer.getShort();
        short s4 = byteBuffer.getShort();
        IsoMetaGrid isoMetaGrid = IsoWorld.instance.MetaGrid;
        if (s2 < isoMetaGrid.getMinX() || s2 > isoMetaGrid.getMaxX() || s3 < isoMetaGrid.getMinY() || s3 > isoMetaGrid.getMaxY()) {
            return;
        }
        IsoMetaCell isoMetaCell = isoMetaGrid.getCellData(s2, s3);
        if (isoMetaCell.info == null || s4 < 0 || s4 >= isoMetaCell.info.RoomList.size()) {
            return;
        }
        isoMetaCell.info.getRoom((int)s4).def.bLightsActive = byteBuffer.get() == 1;
    }

    static void receiveSendCustomColor(ByteBuffer byteBuffer, short s) {
        IsoObject isoObject;
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        int n4 = byteBuffer.getInt();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare == null) {
            instance.delayPacket(n, n2, n3);
            return;
        }
        if (isoGridSquare != null && n4 < isoGridSquare.getObjects().size() && (isoObject = isoGridSquare.getObjects().get(n4)) != null) {
            isoObject.setCustomColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat()));
        }
    }

    static void receiveServerPulse(ByteBuffer byteBuffer, short s) {
        if (ServerPulseGraph.instance != null) {
            ServerPulseGraph.instance.add(byteBuffer.getLong());
        }
    }

    static void receiveUpdateItemSprite(ByteBuffer byteBuffer, short s) {
        int n = byteBuffer.getInt();
        String string = GameWindow.ReadStringUTF(byteBuffer);
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        int n4 = byteBuffer.getInt();
        int n5 = byteBuffer.getInt();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n2, n3, n4);
        if (isoGridSquare == null) {
            instance.delayPacket(n2, n3, n4);
            return;
        }
        if (isoGridSquare != null && n5 < isoGridSquare.getObjects().size()) {
            try {
                IsoObject isoObject = isoGridSquare.getObjects().get(n5);
                if (isoObject != null) {
                    boolean bl = isoObject.sprite != null && isoObject.sprite.getProperties().Is("HitByCar") && isoObject.sprite.getProperties().Val("DamagedSprite") != null && !isoObject.sprite.getProperties().Val("DamagedSprite").isEmpty();
                    isoObject.sprite = IsoSpriteManager.instance.getSprite(n);
                    if (isoObject.sprite == null && !string.isEmpty()) {
                        isoObject.setSprite(string);
                    }
                    isoObject.RemoveAttachedAnims();
                    int n6 = byteBuffer.get() & 0xFF;
                    for (int i = 0; i < n6; ++i) {
                        int n7 = byteBuffer.getInt();
                        IsoSprite isoSprite = IsoSpriteManager.instance.getSprite(n7);
                        if (isoSprite == null) continue;
                        isoObject.AttachExistingAnim(isoSprite, 0, 0, false, 0, false, 0.0f);
                    }
                    if (isoObject instanceof IsoThumpable && bl && (isoObject.sprite == null || !isoObject.sprite.getProperties().Is("HitByCar"))) {
                        ((IsoThumpable)isoObject).setBlockAllTheSquare(false);
                    }
                    isoGridSquare.RecalcAllWithNeighbours(true);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    static void receiveUpdateOverlaySprite(ByteBuffer byteBuffer, short s) {
        String string = GameWindow.ReadStringUTF(byteBuffer);
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        float f = byteBuffer.getFloat();
        float f2 = byteBuffer.getFloat();
        float f3 = byteBuffer.getFloat();
        float f4 = byteBuffer.getFloat();
        int n4 = byteBuffer.getInt();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare == null) {
            instance.delayPacket(n, n2, n3);
            return;
        }
        if (isoGridSquare != null && n4 < isoGridSquare.getObjects().size()) {
            try {
                IsoObject isoObject = isoGridSquare.getObjects().get(n4);
                if (isoObject != null) {
                    isoObject.setOverlaySprite(string, f, f2, f3, f4, false);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private KahluaTable copyTable(KahluaTable kahluaTable) {
        KahluaTable kahluaTable2 = LuaManager.platform.newTable();
        KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
        while (kahluaTableIterator.advance()) {
            Object object = kahluaTableIterator.getKey();
            Object object2 = kahluaTableIterator.getValue();
            if (object2 instanceof KahluaTable) {
                kahluaTable2.rawset(object, (Object)this.copyTable((KahluaTable)object2));
                continue;
            }
            kahluaTable2.rawset(object, object2);
        }
        return kahluaTable2;
    }

    public KahluaTable getServerSpawnRegions() {
        return this.copyTable(this.ServerSpawnRegions);
    }

    public static void toggleSafety(IsoPlayer isoPlayer) {
        if (isoPlayer == null) {
            return;
        }
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ChangeSafety.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)isoPlayer.PlayerIndex);
        PacketTypes.PacketType.ChangeSafety.send(connection);
    }

    static void receiveStartFire(ByteBuffer byteBuffer, short s) {
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        int n4 = byteBuffer.getInt();
        boolean bl = byteBuffer.get() == 1;
        int n5 = byteBuffer.getInt();
        int n6 = byteBuffer.getInt();
        int n7 = byteBuffer.getInt();
        boolean bl2 = byteBuffer.get() == 1;
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare == null) {
            instance.delayPacket(n, n2, n3);
            return;
        }
        if (!IsoFire.CanAddFire(isoGridSquare, bl, bl2)) {
            DebugLog.log("not adding fire that is on the server " + n + "," + n2);
            return;
        }
        IsoFire isoFire = bl2 ? new IsoFire(IsoWorld.instance.CurrentCell, isoGridSquare, bl, n4, n6, true) : new IsoFire(IsoWorld.instance.CurrentCell, isoGridSquare, bl, n4, n6);
        isoFire.SpreadDelay = n5;
        isoFire.numFlameParticles = n7;
        IsoFireManager.Add(isoFire);
        isoGridSquare.getObjects().add(isoFire);
    }

    static void receiveAddCorpseToMap(ByteBuffer byteBuffer, short s) {
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        IsoObject isoObject = WorldItemTypes.createFromBuffer(byteBuffer);
        isoObject.loadFromRemoteBuffer(byteBuffer, false);
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare == null) {
            instance.delayPacket(n, n2, n3);
            return;
        }
        if (isoGridSquare != null) {
            isoGridSquare.addCorpse((IsoDeadBody)isoObject, true);
        }
    }

    static void receiveReceiveModData(ByteBuffer byteBuffer, short s) {
        int n;
        int n2;
        int n3 = byteBuffer.getInt();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n3, n2 = byteBuffer.getInt(), n = byteBuffer.getInt());
        if (isoGridSquare == null && IsoWorld.instance.isValidSquare(n3, n2, n) && IsoWorld.instance.CurrentCell.getChunkForGridSquare(n3, n2, n) != null) {
            isoGridSquare = IsoGridSquare.getNew(IsoWorld.instance.getCell(), null, n3, n2, n);
        }
        if (isoGridSquare == null) {
            instance.delayPacket(n3, n2, n);
            return;
        }
        try {
            isoGridSquare.getModData().load(byteBuffer, 186);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        LuaEventManager.triggerEvent("onLoadModDataFromServer", isoGridSquare);
    }

    static void receiveObjectModData(ByteBuffer byteBuffer, short s) {
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        int n4 = byteBuffer.getInt();
        boolean bl = byteBuffer.get() == 1;
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare == null) {
            instance.delayPacket(n, n2, n3);
            return;
        }
        if (isoGridSquare != null && n4 >= 0 && n4 < isoGridSquare.getObjects().size()) {
            IsoObject isoObject = isoGridSquare.getObjects().get(n4);
            if (bl) {
                try {
                    isoObject.getModData().load(byteBuffer, 186);
                }
                catch (IOException iOException) {
                    iOException.printStackTrace();
                }
            } else {
                isoObject.getModData().wipe();
            }
        } else if (isoGridSquare != null) {
            DebugLog.log("receiveObjectModData: index=" + n4 + " is invalid x,y,z=" + n + "," + n2 + "," + n3);
        } else if (Core.bDebug) {
            DebugLog.log("receiveObjectModData: sq is null x,y,z=" + n + "," + n2 + "," + n3);
        }
    }

    static void receiveObjectChange(ByteBuffer byteBuffer, short s) {
        byte by = byteBuffer.get();
        if (by == 1) {
            IsoPlayer isoPlayer;
            short s2 = byteBuffer.getShort();
            String string = GameWindow.ReadString(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log("receiveObjectChange " + string);
            }
            if ((isoPlayer = IDToPlayerMap.get(s2)) != null) {
                isoPlayer.loadChange(string, byteBuffer);
            }
        } else if (by == 2) {
            BaseVehicle baseVehicle;
            short s3 = byteBuffer.getShort();
            String string = GameWindow.ReadString(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log("receiveObjectChange " + string);
            }
            if ((baseVehicle = VehicleManager.instance.getVehicleByID(s3)) != null) {
                baseVehicle.loadChange(string, byteBuffer);
            } else if (Core.bDebug) {
                DebugLog.log("receiveObjectChange: unknown vehicle id=" + s3);
            }
        } else if (by == 3) {
            IsoGridSquare isoGridSquare;
            int n = byteBuffer.getInt();
            int n2 = byteBuffer.getInt();
            int n3 = byteBuffer.getInt();
            int n4 = byteBuffer.getInt();
            String string = GameWindow.ReadString(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log("receiveObjectChange " + string);
            }
            if ((isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3)) == null) {
                instance.delayPacket(n, n2, n3);
                return;
            }
            for (int i = 0; i < isoGridSquare.getWorldObjects().size(); ++i) {
                IsoWorldInventoryObject isoWorldInventoryObject = isoGridSquare.getWorldObjects().get(i);
                if (isoWorldInventoryObject.getItem() == null || isoWorldInventoryObject.getItem().getID() != n4) continue;
                isoWorldInventoryObject.loadChange(string, byteBuffer);
                return;
            }
            if (Core.bDebug) {
                DebugLog.log("receiveObjectChange: itemID=" + n4 + " is invalid x,y,z=" + n + "," + n2 + "," + n3);
            }
        } else {
            IsoGridSquare isoGridSquare;
            int n = byteBuffer.getInt();
            int n5 = byteBuffer.getInt();
            int n6 = byteBuffer.getInt();
            int n7 = byteBuffer.getInt();
            String string = GameWindow.ReadString(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log("receiveObjectChange " + string);
            }
            if ((isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n5, n6)) == null) {
                instance.delayPacket(n, n5, n6);
                return;
            }
            if (isoGridSquare != null && n7 >= 0 && n7 < isoGridSquare.getObjects().size()) {
                IsoObject isoObject = isoGridSquare.getObjects().get(n7);
                isoObject.loadChange(string, byteBuffer);
            } else if (isoGridSquare != null) {
                if (Core.bDebug) {
                    DebugLog.log("receiveObjectChange: index=" + n7 + " is invalid x,y,z=" + n + "," + n5 + "," + n6);
                }
            } else if (Core.bDebug) {
                DebugLog.log("receiveObjectChange: sq is null x,y,z=" + n + "," + n5 + "," + n6);
            }
        }
    }

    static void receiveKeepAlive(ByteBuffer byteBuffer, short s) {
        MPDebugInfo.instance.clientPacket(byteBuffer);
    }

    static void receiveSmashWindow(ByteBuffer byteBuffer, short s) {
        IsoObject isoObject = instance.getIsoObjectRefFromByteBuffer(byteBuffer);
        if (isoObject instanceof IsoWindow) {
            byte by = byteBuffer.get();
            if (by == 1) {
                ((IsoWindow)isoObject).smashWindow(true);
            } else if (by == 2) {
                ((IsoWindow)isoObject).setGlassRemoved(true);
            }
        } else if (Core.bDebug) {
            DebugLog.log("SmashWindow not a window!");
        }
    }

    static void receiveRemoveContestedItemsFromInventory(ByteBuffer byteBuffer, short s) {
        int n = byteBuffer.getInt();
        for (int i = 0; i < n; ++i) {
            int n2 = byteBuffer.getInt();
            for (int j = 0; j < IsoPlayer.numPlayers; ++j) {
                IsoPlayer isoPlayer = IsoPlayer.players[j];
                if (isoPlayer == null || isoPlayer.isDead()) continue;
                isoPlayer.getInventory().removeItemWithIDRecurse(n2);
            }
        }
    }

    static void receiveServerQuit(ByteBuffer byteBuffer, short s) {
        GameWindow.savePlayer();
        GameWindow.kickReason = "Server shut down safely. Players and map data saved.";
        GameWindow.bServerDisconnected = true;
    }

    static void receiveHitCharacter(ByteBuffer byteBuffer, short s) {
        try {
            HitCharacterPacket hitCharacterPacket = HitCharacterPacket.process(byteBuffer);
            if (hitCharacterPacket != null) {
                hitCharacterPacket.parse(byteBuffer);
                if (Core.bDebug) {
                    if (!DebugLog.isEnabled(DebugType.Damage)) {
                        DebugLog.log(DebugType.Multiplayer, "ReceiveHitCharacter: " + hitCharacterPacket.getHitDescription());
                    }
                    DebugLog.log(DebugType.Damage, "ReceiveHitCharacter: " + hitCharacterPacket.getDescription());
                }
                hitCharacterPacket.tryProcess();
            }
        }
        catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveHitCharacter: failed", LogSeverity.Error);
        }
    }

    public static boolean sendHitCharacter(IsoGameCharacter isoGameCharacter, IsoMovingObject isoMovingObject, HandWeapon handWeapon, float f, boolean bl, float f2, boolean bl2, boolean bl3, boolean bl4) {
        boolean bl5 = false;
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.HitCharacter.doPacket(byteBufferWriter);
        try {
            HitCharacterPacket hitCharacterPacket = null;
            if (isoGameCharacter instanceof IsoZombie) {
                if (isoMovingObject instanceof IsoPlayer) {
                    ZombieHitPlayerPacket zombieHitPlayerPacket = new ZombieHitPlayerPacket();
                    zombieHitPlayerPacket.set((IsoZombie)isoGameCharacter, (IsoPlayer)isoMovingObject);
                    hitCharacterPacket = zombieHitPlayerPacket;
                } else {
                    DebugLog.Multiplayer.warn(String.format("SendHitCharacter: unknown target type (wielder=%s, target=%s)", isoGameCharacter.getClass().getName(), isoMovingObject.getClass().getName()));
                }
            } else if (isoGameCharacter instanceof IsoPlayer) {
                if (isoMovingObject == null) {
                    PlayerHitSquarePacket playerHitSquarePacket = new PlayerHitSquarePacket();
                    playerHitSquarePacket.set((IsoPlayer)isoGameCharacter, handWeapon, bl2);
                    hitCharacterPacket = playerHitSquarePacket;
                } else if (isoMovingObject instanceof IsoPlayer) {
                    PlayerHitPlayerPacket playerHitPlayerPacket = new PlayerHitPlayerPacket();
                    playerHitPlayerPacket.set((IsoPlayer)isoGameCharacter, (IsoPlayer)isoMovingObject, handWeapon, f, bl, f2, bl2, bl4);
                    hitCharacterPacket = playerHitPlayerPacket;
                } else if (isoMovingObject instanceof IsoZombie) {
                    PlayerHitZombiePacket playerHitZombiePacket = new PlayerHitZombiePacket();
                    playerHitZombiePacket.set((IsoPlayer)isoGameCharacter, (IsoZombie)isoMovingObject, handWeapon, f, bl, f2, bl2, bl3, bl4);
                    hitCharacterPacket = playerHitZombiePacket;
                } else if (isoMovingObject instanceof BaseVehicle) {
                    PlayerHitVehiclePacket playerHitVehiclePacket = new PlayerHitVehiclePacket();
                    playerHitVehiclePacket.set((IsoPlayer)isoGameCharacter, (BaseVehicle)isoMovingObject, handWeapon, bl2);
                    hitCharacterPacket = playerHitVehiclePacket;
                } else {
                    DebugLog.Multiplayer.warn(String.format("SendHitCharacter: unknown target type (wielder=%s, target=%s)", isoGameCharacter.getClass().getName(), isoMovingObject.getClass().getName()));
                }
            } else {
                DebugLog.Multiplayer.warn(String.format("SendHitCharacter: unknown wielder type (wielder=%s, target=%s)", isoGameCharacter.getClass().getName(), isoMovingObject.getClass().getName()));
            }
            if (hitCharacterPacket != null) {
                hitCharacterPacket.write(byteBufferWriter);
                PacketTypes.PacketType.HitCharacter.send(connection);
                if (Core.bDebug) {
                    if (!DebugLog.isEnabled(DebugType.Damage)) {
                        DebugLog.log(DebugType.Multiplayer, "SendHitCharacter: " + hitCharacterPacket.getHitDescription());
                    }
                    DebugLog.log(DebugType.Damage, "SendHitCharacter: " + hitCharacterPacket.getDescription());
                }
                bl5 = true;
            }
        }
        catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendHitCharacter: failed", LogSeverity.Error);
        }
        return bl5;
    }

    public static void sendHitVehicle(IsoPlayer isoPlayer, IsoGameCharacter isoGameCharacter, BaseVehicle baseVehicle, float f, boolean bl, int n, float f2, boolean bl2) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.HitCharacter.doPacket(byteBufferWriter);
        try {
            VehicleHitPacket vehicleHitPacket = null;
            if (isoGameCharacter instanceof IsoPlayer) {
                VehicleHitPlayerPacket vehicleHitPlayerPacket = new VehicleHitPlayerPacket();
                vehicleHitPlayerPacket.set(isoPlayer, (IsoPlayer)isoGameCharacter, baseVehicle, f, bl, n, f2, bl2);
                vehicleHitPacket = vehicleHitPlayerPacket;
            } else if (isoGameCharacter instanceof IsoZombie) {
                VehicleHitZombiePacket vehicleHitZombiePacket = new VehicleHitZombiePacket();
                vehicleHitZombiePacket.set(isoPlayer, (IsoZombie)isoGameCharacter, baseVehicle, f, bl, n, f2, bl2);
                vehicleHitPacket = vehicleHitZombiePacket;
            } else {
                DebugLog.Multiplayer.warn(String.format("SendHitVehicle: unknown target type (wielder=%s, target=%s)", isoPlayer.getClass().getName(), isoGameCharacter.getClass().getName()));
            }
            if (vehicleHitPacket != null) {
                vehicleHitPacket.write(byteBufferWriter);
                PacketTypes.PacketType.HitCharacter.send(connection);
                if (Core.bDebug) {
                    if (!DebugLog.isEnabled(DebugType.Damage)) {
                        DebugLog.log(DebugType.Multiplayer, "SendHitVehicle: " + vehicleHitPacket.getHitDescription());
                    }
                    DebugLog.log(DebugType.Damage, "SendHitVehicle: " + vehicleHitPacket.getDescription());
                }
            }
        }
        catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendHitVehicle: failed", LogSeverity.Error);
        }
    }

    static void receiveZombieDeath(ByteBuffer byteBuffer, short s) {
        try {
            DeadZombiePacket deadZombiePacket = new DeadZombiePacket();
            deadZombiePacket.parse(byteBuffer);
            if (Core.bDebug) {
                if (!DebugLog.isEnabled(DebugType.Death)) {
                    DebugLog.log(DebugType.Multiplayer, "ReceiveZombieDeath: " + deadZombiePacket.getDeathDescription());
                }
                DebugLog.log(DebugType.Death, "ReceiveZombieDeath: " + deadZombiePacket.getDescription());
            }
        }
        catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveZombieDeath: failed", LogSeverity.Error);
        }
    }

    public static void sendZombieDeath(IsoZombie isoZombie) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ZombieDeath.doPacket(byteBufferWriter);
        try {
            DeadZombiePacket deadZombiePacket = new DeadZombiePacket();
            deadZombiePacket.set(isoZombie);
            deadZombiePacket.write(byteBufferWriter);
            PacketTypes.PacketType.ZombieDeath.send(connection);
            if (Core.bDebug) {
                if (!DebugLog.isEnabled(DebugType.Death)) {
                    DebugLog.log(DebugType.Multiplayer, "SendZombieDeath: " + deadZombiePacket.getDeathDescription());
                }
                DebugLog.log(DebugType.Death, "SendZombieDeath: " + deadZombiePacket.getDescription());
            }
        }
        catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendZombieDeath: failed", LogSeverity.Error);
        }
    }

    static void receivePlayerDeath(ByteBuffer byteBuffer, short s) {
        try {
            DeadPlayerPacket deadPlayerPacket = new DeadPlayerPacket();
            deadPlayerPacket.parse(byteBuffer);
            if (Core.bDebug) {
                if (!DebugLog.isEnabled(DebugType.Death)) {
                    DebugLog.log(DebugType.Multiplayer, "ReceivePlayerDeath: " + deadPlayerPacket.getDeathDescription());
                }
                DebugLog.log(DebugType.Death, "ReceivePlayerDeath: " + deadPlayerPacket.getDescription());
            }
        }
        catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceivePlayerDeath: failed", LogSeverity.Error);
        }
    }

    public void sendPlayerDeath(IsoPlayer isoPlayer) {
        isoPlayer.setTransactionID(0);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlayerDeath.doPacket(byteBufferWriter);
        try {
            DeadPlayerPacket deadPlayerPacket = new DeadPlayerPacket();
            deadPlayerPacket.set(isoPlayer);
            deadPlayerPacket.write(byteBufferWriter);
            PacketTypes.PacketType.PlayerDeath.send(connection);
            if (Core.bDebug) {
                if (!DebugLog.isEnabled(DebugType.Death)) {
                    DebugLog.log(DebugType.Multiplayer, "SendPlayerDeath: " + deadPlayerPacket.getDeathDescription());
                }
                DebugLog.log(DebugType.Death, "SendPlayerDeath: " + deadPlayerPacket.getDescription());
            }
        }
        catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendPlayerDeath: failed", LogSeverity.Error);
        }
    }

    static void receivePlayerDamage(ByteBuffer byteBuffer, short s) {
        try {
            IsoPlayer isoPlayer;
            short s2 = byteBuffer.getShort();
            float f = byteBuffer.getFloat();
            if (Core.bDebug) {
                if (!DebugLog.isEnabled(DebugType.Damage)) {
                    DebugLog.log(DebugType.Multiplayer, "ReceivePlayerDamage: " + s2);
                }
                DebugLog.log(DebugType.Damage, "ReceivePlayerDamage: " + s2);
            }
            if ((isoPlayer = IDToPlayerMap.get(s2)) != null) {
                isoPlayer.getBodyDamage().load(byteBuffer, IsoWorld.getWorldVersion());
                isoPlayer.getStats().setPain(f);
                isoPlayer.getBodyDamage().Update();
                if (ServerOptions.instance.PlayerSaveOnDamage.getValue()) {
                    GameWindow.savePlayer();
                }
            }
        }
        catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceivePlayerDamage: failed", LogSeverity.Error);
        }
    }

    public static void sendPlayerDamage(IsoPlayer isoPlayer) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlayerDamage.doPacket(byteBufferWriter);
        try {
            byteBufferWriter.putShort(isoPlayer.getOnlineID());
            byteBufferWriter.putFloat(isoPlayer.getStats().getPain());
            isoPlayer.getBodyDamage().save(byteBufferWriter.bb);
            PacketTypes.PacketType.PlayerDamage.send(connection);
            if (Core.bDebug) {
                if (!DebugLog.isEnabled(DebugType.Damage)) {
                    DebugLog.log(DebugType.Multiplayer, "SendPlayerDamage: " + isoPlayer.getOnlineID());
                }
                DebugLog.log(DebugType.Damage, "SendPlayerDamage: " + isoPlayer.getOnlineID());
            }
        }
        catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendPlayerDamage: failed", LogSeverity.Error);
        }
    }

    static void receiveSyncInjuries(ByteBuffer byteBuffer, short s) {
        try {
            Object object;
            SyncInjuriesPacket syncInjuriesPacket = new SyncInjuriesPacket();
            syncInjuriesPacket.parse(byteBuffer);
            if (Core.bDebug) {
                object = String.format("Receive: %s", syncInjuriesPacket.getDescription());
                if (!DebugLog.isEnabled(DebugType.Damage)) {
                    DebugLog.log(DebugType.Multiplayer, (String)object);
                }
                DebugLog.log(DebugType.Damage, (String)object);
            }
            if ((object = IDToPlayerMap.get(syncInjuriesPacket.id)) != null && !((IsoPlayer)object).isLocalPlayer()) {
                syncInjuriesPacket.process((IsoPlayer)object);
            }
        }
        catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceivePlayerInjuries: failed", LogSeverity.Error);
        }
    }

    public static void sendPlayerInjuries(IsoPlayer isoPlayer) {
        SyncInjuriesPacket syncInjuriesPacket = new SyncInjuriesPacket();
        syncInjuriesPacket.set(isoPlayer);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncInjuries.doPacket(byteBufferWriter);
        try {
            syncInjuriesPacket.write(byteBufferWriter);
            PacketTypes.PacketType.SyncInjuries.send(connection);
            if (Core.bDebug) {
                String string = String.format("Send: %s", syncInjuriesPacket.getDescription());
                if (!DebugLog.isEnabled(DebugType.Damage)) {
                    DebugLog.log(DebugType.Multiplayer, string);
                }
                DebugLog.log(DebugType.Damage, string);
            }
        }
        catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendPlayerInjuries: failed", LogSeverity.Error);
        }
    }

    static void receiveRemoveCorpseFromMap(ByteBuffer byteBuffer, short s) {
        Object object;
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        int n4 = byteBuffer.getInt();
        short s2 = byteBuffer.getShort();
        if (Core.bDebug) {
            object = String.format("ReceiveRemoveCorpse: id=%d, index=%d, pos=( %d ; %d ; %d )", s2, n4, n, n2, n3);
            if (!DebugLog.isEnabled(DebugType.Death)) {
                DebugLog.log(DebugType.Multiplayer, (String)object);
            }
            DebugLog.log(DebugType.Death, (String)object);
        }
        if ((object = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3)) == null) {
            instance.delayPacket(n, n2, n3);
            DebugLog.Multiplayer.error("ReceiveRemoveCorpse: incorrect square");
            return;
        }
        if (n4 >= 0 && n4 < ((IsoGridSquare)object).getStaticMovingObjects().size()) {
            IsoDeadBody isoDeadBody = (IsoDeadBody)((IsoGridSquare)object).getStaticMovingObjects().get(n4);
            ((IsoGridSquare)object).removeCorpse(isoDeadBody, true);
        } else {
            DebugLog.Multiplayer.error("ReceiveRemoveCorpse: no corpse on square");
        }
    }

    public static void sendRemoveCorpseFromMap(IsoDeadBody isoDeadBody) {
        Object object;
        int n = isoDeadBody.getSquare().getX();
        int n2 = isoDeadBody.getSquare().getY();
        int n3 = isoDeadBody.getSquare().getZ();
        int n4 = isoDeadBody.getSquare().getStaticMovingObjects().indexOf(isoDeadBody);
        short s = isoDeadBody.getOnlineID();
        if (Core.bDebug) {
            object = String.format("SendRemoveCorpse: id=%d, index=%d, pos=( %d ; %d ; %d )", s, n4, n, n2, n3);
            if (!DebugLog.isEnabled(DebugType.Death)) {
                DebugLog.log(DebugType.Multiplayer, (String)object);
            }
            DebugLog.log(DebugType.Death, (String)object);
        }
        object = connection.startPacket();
        PacketTypes.PacketType.RemoveCorpseFromMap.doPacket((ByteBufferWriter)object);
        ((ByteBufferWriter)object).putInt(n);
        ((ByteBufferWriter)object).putInt(n2);
        ((ByteBufferWriter)object).putInt(n3);
        ((ByteBufferWriter)object).putInt(n4);
        ((ByteBufferWriter)object).putShort(s);
        PacketTypes.PacketType.RemoveCorpseFromMap.send(connection);
    }

    public static void sendEvent(IsoPlayer isoPlayer, String string) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.EventPacket.doPacket(byteBufferWriter);
        try {
            EventPacket eventPacket = new EventPacket();
            if (eventPacket.set(isoPlayer, string)) {
                eventPacket.write(byteBufferWriter);
                PacketTypes.PacketType.EventPacket.send(connection);
                if (Core.bDebug) {
                    DebugLog.log(DebugType.Multiplayer, "SendEvent: " + eventPacket.getDescription());
                }
            } else {
                connection.cancelPacket();
            }
        }
        catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendEvent: failed", LogSeverity.Error);
        }
    }

    static void receiveEventPacket(ByteBuffer byteBuffer, short s) {
        try {
            EventPacket eventPacket = new EventPacket();
            eventPacket.parse(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, "ReceiveEvent: " + eventPacket.getDescription());
            }
            eventPacket.tryProcess();
        }
        catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveEvent: failed", LogSeverity.Error);
        }
    }

    public static void sendKillZombie(IsoZombie isoZombie) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.KillZombie.doPacket(byteBufferWriter);
        try {
            byteBufferWriter.putShort(isoZombie.getOnlineID());
            byteBufferWriter.putBoolean(isoZombie.isFallOnFront());
            PacketTypes.PacketType.KillZombie.send(connection);
            if (Core.bDebug) {
                String string = String.format("SendKillZombie: id=%d, isFallOnFront=%b", isoZombie.getOnlineID(), isoZombie.isFallOnFront());
                if (!DebugLog.isEnabled(DebugType.Death)) {
                    DebugLog.log(DebugType.Multiplayer, string);
                }
                DebugLog.log(DebugType.Death, string);
            }
        }
        catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendKillZombie: failed", LogSeverity.Error);
        }
    }

    static void receiveKillZombie(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoZombie isoZombie = (IsoZombie)IDToZombieMap.get(s2);
        if (isoZombie != null) {
            if (isoZombie.getCurrentSquare() == null) {
                if (Core.bDebug) {
                    DebugLog.log(DebugType.Death, String.format("ReceiveKillZombie %d: no current square", s2));
                }
                return;
            }
            if (!isoZombie.shouldBecomeCorpse()) {
                if (Core.bDebug) {
                    DebugLog.log(DebugType.Multiplayer, String.format("ReceiveKillZombie %d: wait for death", s2));
                }
                return;
            }
            if (Core.bDebug) {
                DebugLog.Multiplayer.warn(String.format("ReceiveKillZombie %d: create corpse", s2));
            }
            IsoDeadBody isoDeadBody = new IsoDeadBody(isoZombie);
            if (IDToZombieMap.containsKey(s2)) {
                DebugLog.Multiplayer.error(String.format("ReceiveKillZombie %d: zombie is still present", s2));
            }
        } else if (Core.bDebug) {
            DebugLog.log(DebugType.Death, String.format("ReceiveKillZombie %d: already killed", s2));
        }
    }

    public static void sendAction(BaseAction baseAction, boolean bl) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ActionPacket.doPacket(byteBufferWriter);
        try {
            ActionPacket actionPacket = new ActionPacket();
            actionPacket.set(bl, baseAction);
            actionPacket.write(byteBufferWriter);
            PacketTypes.PacketType.ActionPacket.send(connection);
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, "SendAction: " + actionPacket.getDescription());
            }
        }
        catch (Exception exception) {
            connection.cancelPacket();
            DebugLog.Multiplayer.printException(exception, "SendAction: failed", LogSeverity.Error);
        }
    }

    static void receiveActionPacket(ByteBuffer byteBuffer, short s) {
        try {
            ActionPacket actionPacket = new ActionPacket();
            actionPacket.parse(byteBuffer);
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, "ReceiveAction: " + actionPacket.getDescription());
            }
            actionPacket.process();
        }
        catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveAction: failed", LogSeverity.Error);
        }
    }

    public static void sendEatBody(IsoZombie isoZombie, IsoMovingObject isoMovingObject) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.EatBody.doPacket(byteBufferWriter);
        try {
            byteBufferWriter.putShort(isoZombie.getOnlineID());
            if (isoMovingObject instanceof IsoDeadBody) {
                IsoDeadBody isoDeadBody = (IsoDeadBody)isoMovingObject;
                byteBufferWriter.putByte((byte)1);
                byteBufferWriter.putBoolean(isoZombie.getVariableBoolean("onknees"));
                byteBufferWriter.putFloat(isoZombie.getEatSpeed());
                byteBufferWriter.putFloat(isoZombie.getStateEventDelayTimer());
                byteBufferWriter.putInt(isoDeadBody.getStaticMovingObjectIndex());
                byteBufferWriter.putFloat(isoDeadBody.getSquare().getX());
                byteBufferWriter.putFloat(isoDeadBody.getSquare().getY());
                byteBufferWriter.putFloat(isoDeadBody.getSquare().getZ());
            } else if (isoMovingObject instanceof IsoPlayer) {
                byteBufferWriter.putByte((byte)2);
                byteBufferWriter.putBoolean(isoZombie.getVariableBoolean("onknees"));
                byteBufferWriter.putFloat(isoZombie.getEatSpeed());
                byteBufferWriter.putFloat(isoZombie.getStateEventDelayTimer());
                byteBufferWriter.putShort(((IsoPlayer)isoMovingObject).getOnlineID());
            } else {
                byteBufferWriter.putByte((byte)0);
            }
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, "SendEatBody");
            }
            PacketTypes.PacketType.EatBody.send(connection);
        }
        catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "SendEatBody: failed", LogSeverity.Error);
            connection.cancelPacket();
        }
    }

    public static void receiveEatBody(ByteBuffer byteBuffer, short s) {
        try {
            IsoZombie isoZombie;
            short s2 = byteBuffer.getShort();
            byte by = byteBuffer.get();
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, String.format("ReceiveEatBody: zombie=%d type=%d", s2, by));
            }
            if ((isoZombie = (IsoZombie)IDToZombieMap.get(s2)) == null) {
                DebugLog.Multiplayer.error("ReceiveEatBody: zombie " + s2 + " not found");
                return;
            }
            if (by == 1) {
                float f;
                float f2;
                boolean bl = byteBuffer.get() != 0;
                float f3 = byteBuffer.getFloat();
                float f4 = byteBuffer.getFloat();
                int n = byteBuffer.getInt();
                float f5 = byteBuffer.getFloat();
                IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(f5, f2 = byteBuffer.getFloat(), f = byteBuffer.getFloat());
                if (isoGridSquare == null) {
                    DebugLog.Multiplayer.error("ReceiveEatBody: incorrect square");
                    return;
                }
                if (n >= 0 && n < isoGridSquare.getStaticMovingObjects().size()) {
                    IsoDeadBody isoDeadBody = (IsoDeadBody)isoGridSquare.getStaticMovingObjects().get(n);
                    if (isoDeadBody != null) {
                        isoZombie.setTarget(null);
                        isoZombie.setEatBodyTarget(isoDeadBody, true, f3);
                        isoZombie.setVariable("onknees", bl);
                        isoZombie.setStateEventDelayTimer(f4);
                    } else {
                        DebugLog.Multiplayer.error("ReceiveEatBody: no corpse with index " + n + " on square");
                    }
                } else {
                    DebugLog.Multiplayer.error("ReceiveEatBody: no corpse on square");
                }
            } else if (by == 2) {
                boolean bl = byteBuffer.get() != 0;
                float f = byteBuffer.getFloat();
                float f6 = byteBuffer.getFloat();
                short s3 = byteBuffer.getShort();
                IsoPlayer isoPlayer = IDToPlayerMap.get(s3);
                if (isoPlayer == null) {
                    DebugLog.Multiplayer.error("ReceiveEatBody: player " + s3 + " not found");
                    return;
                }
                isoZombie.setTarget(null);
                isoZombie.setEatBodyTarget(isoPlayer, true, f);
                isoZombie.setVariable("onknees", bl);
                isoZombie.setStateEventDelayTimer(f6);
            } else {
                isoZombie.setEatBodyTarget(null, false);
            }
        }
        catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveEatBody: failed", LogSeverity.Error);
        }
    }

    public static void sendThump(IsoGameCharacter isoGameCharacter, Thumpable thumpable) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Thump.doPacket(byteBufferWriter);
        try {
            short s = isoGameCharacter.getOnlineID();
            String string = isoGameCharacter.getVariableString("ThumpType");
            byteBufferWriter.putShort(s);
            byteBufferWriter.putByte((byte)NetworkVariables.ThumpType.fromString(string).ordinal());
            if (thumpable instanceof IsoObject) {
                IsoObject isoObject = (IsoObject)thumpable;
                byteBufferWriter.putInt(isoObject.getObjectIndex());
                byteBufferWriter.putFloat(isoObject.getSquare().getX());
                byteBufferWriter.putFloat(isoObject.getSquare().getY());
                byteBufferWriter.putFloat(isoObject.getSquare().getZ());
            } else {
                byteBufferWriter.putInt(-1);
            }
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, String.format("SendThump: zombie=%d type=%s target=%s", s, string, thumpable == null ? "null" : thumpable.getClass().getSimpleName()));
            }
            PacketTypes.PacketType.Thump.send(connection);
        }
        catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "SendThump: failed", LogSeverity.Error);
            connection.cancelPacket();
        }
    }

    public static void receiveThump(ByteBuffer byteBuffer, short s) {
        try {
            float f;
            float f2;
            IsoZombie isoZombie;
            short s2 = byteBuffer.getShort();
            String string = NetworkVariables.ThumpType.fromByte(byteBuffer.get()).toString();
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, String.format("ReceiveThump: zombie=%d type=%s", s2, string));
            }
            if ((isoZombie = (IsoZombie)IDToZombieMap.get(s2)) == null) {
                DebugLog.Multiplayer.error("ReceiveThump: zombie " + s2 + " not found");
                return;
            }
            isoZombie.setVariable("ThumpType", string);
            int n = byteBuffer.getInt();
            if (n == -1) {
                isoZombie.setThumpTarget(null);
                return;
            }
            float f3 = byteBuffer.getFloat();
            IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(f3, f2 = byteBuffer.getFloat(), f = byteBuffer.getFloat());
            if (isoGridSquare == null) {
                DebugLog.Multiplayer.error("ReceiveThump: incorrect square");
                return;
            }
            IsoObject isoObject = isoGridSquare.getObjects().get(n);
            if (isoObject instanceof Thumpable) {
                isoZombie.setThumpTarget(isoObject);
            } else {
                DebugLog.Multiplayer.error("ReceiveThump: no thumpable with index " + n + " on square");
            }
        }
        catch (Exception exception) {
            DebugLog.Multiplayer.printException(exception, "ReceiveThump: failed", LogSeverity.Error);
        }
    }

    public void sendWorldSound(WorldSoundManager.WorldSound worldSound) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.WorldSound.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(worldSound.x);
        byteBufferWriter.putInt(worldSound.y);
        byteBufferWriter.putInt(worldSound.z);
        byteBufferWriter.putInt(worldSound.radius);
        byteBufferWriter.putInt(worldSound.volume);
        byteBufferWriter.putByte(worldSound.stresshumans ? (byte)1 : 0);
        byteBufferWriter.putFloat(worldSound.zombieIgnoreDist);
        byteBufferWriter.putFloat(worldSound.stressMod);
        byteBufferWriter.putByte(worldSound.sourceIsZombie ? (byte)1 : 0);
        PacketTypes.PacketType.WorldSound.send(connection);
    }

    static void receiveRemoveItemFromSquare(ByteBuffer byteBuffer, short s) {
        if (IsoWorld.instance.CurrentCell == null) {
            return;
        }
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        int n4 = byteBuffer.getInt();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare == null) {
            instance.delayPacket(n, n2, n3);
            return;
        }
        if (isoGridSquare != null && n4 >= 0 && n4 < isoGridSquare.getObjects().size()) {
            IsoObject isoObject = isoGridSquare.getObjects().get(n4);
            isoGridSquare.RemoveTileObject(isoObject);
            if (isoObject instanceof IsoWorldInventoryObject || isoObject.getContainer() != null) {
                LuaEventManager.triggerEvent("OnContainerUpdate", isoObject);
            }
        } else if (Core.bDebug) {
            DebugLog.log("RemoveItemFromMap: sq is null or index is invalid");
        }
    }

    static void receiveLoadPlayerProfile(ByteBuffer byteBuffer, short s) {
        ClientPlayerDB.getInstance().clientLoadNetworkCharacter(byteBuffer, connection);
    }

    static void receiveRemoveInventoryItemFromContainer(ByteBuffer byteBuffer, short s) {
        int n;
        int n2;
        if (IsoWorld.instance.CurrentCell == null) {
            return;
        }
        ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
        short s2 = byteBuffer.getShort();
        int n3 = byteBufferReader.getInt();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n3, n2 = byteBufferReader.getInt(), n = byteBufferReader.getInt());
        if (isoGridSquare != null) {
            if (s2 == 0) {
                byte by = byteBufferReader.getByte();
                int n4 = byteBuffer.getInt();
                if (by < 0 || by >= isoGridSquare.getStaticMovingObjects().size()) {
                    DebugLog.log("ERROR: removeItemFromContainer: invalid corpse index");
                    return;
                }
                IsoObject isoObject = isoGridSquare.getStaticMovingObjects().get(by);
                if (isoObject != null && isoObject.getContainer() != null) {
                    for (int i = 0; i < n4; ++i) {
                        int n5 = byteBufferReader.getInt();
                        isoObject.getContainer().removeItemWithID(n5);
                        isoObject.getContainer().setExplored(true);
                    }
                }
            } else if (s2 == 1) {
                int n6;
                int n7 = byteBufferReader.getInt();
                int n8 = byteBuffer.getInt();
                ItemContainer itemContainer = null;
                for (n6 = 0; n6 < isoGridSquare.getWorldObjects().size(); ++n6) {
                    IsoWorldInventoryObject isoWorldInventoryObject = isoGridSquare.getWorldObjects().get(n6);
                    if (isoWorldInventoryObject == null || !(isoWorldInventoryObject.getItem() instanceof InventoryContainer) || isoWorldInventoryObject.getItem().id != n7) continue;
                    itemContainer = ((InventoryContainer)isoWorldInventoryObject.getItem()).getInventory();
                    break;
                }
                if (itemContainer == null) {
                    DebugLog.log("ERROR removeItemFromContainer can't find world item with id=" + n7);
                    return;
                }
                for (n6 = 0; n6 < n8; ++n6) {
                    int n9 = byteBufferReader.getInt();
                    itemContainer.removeItemWithID(n9);
                    itemContainer.setExplored(true);
                }
            } else if (s2 == 2) {
                ItemContainer itemContainer;
                byte by = byteBufferReader.getByte();
                byte by2 = byteBufferReader.getByte();
                int n10 = byteBuffer.getInt();
                if (by < 0 || by >= isoGridSquare.getObjects().size()) {
                    DebugLog.log("ERROR: removeItemFromContainer: invalid object index");
                    return;
                }
                IsoObject isoObject = isoGridSquare.getObjects().get(by);
                ItemContainer itemContainer2 = itemContainer = isoObject != null ? isoObject.getContainerByIndex(by2) : null;
                if (itemContainer != null) {
                    for (int i = 0; i < n10; ++i) {
                        int n11 = byteBufferReader.getInt();
                        itemContainer.removeItemWithID(n11);
                        itemContainer.setExplored(true);
                    }
                }
            } else if (s2 == 3) {
                short s3 = byteBufferReader.getShort();
                byte by = byteBufferReader.getByte();
                int n12 = byteBuffer.getInt();
                BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(s3);
                if (baseVehicle == null) {
                    DebugLog.log("ERROR: removeItemFromContainer: invalid vehicle id");
                    return;
                }
                VehiclePart vehiclePart = baseVehicle.getPartByIndex(by);
                if (vehiclePart == null) {
                    DebugLog.log("ERROR: removeItemFromContainer: invalid part index");
                    return;
                }
                ItemContainer itemContainer = vehiclePart.getItemContainer();
                if (itemContainer == null) {
                    DebugLog.log("ERROR: removeItemFromContainer: part " + vehiclePart.getId() + " has no container");
                    return;
                }
                if (itemContainer != null) {
                    for (int i = 0; i < n12; ++i) {
                        int n13 = byteBufferReader.getInt();
                        itemContainer.removeItemWithID(n13);
                        itemContainer.setExplored(true);
                    }
                    vehiclePart.setContainerContentAmount(itemContainer.getCapacityWeight());
                }
            } else {
                DebugLog.log("ERROR: removeItemFromContainer: invalid object index");
            }
        } else {
            instance.delayPacket(n3, n2, n);
        }
    }

    static void receiveAddInventoryItemToContainer(ByteBuffer byteBuffer, short s) {
        int n;
        int n2;
        if (IsoWorld.instance.CurrentCell == null) {
            return;
        }
        ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
        short s2 = byteBuffer.getShort();
        int n3 = byteBufferReader.getInt();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n3, n2 = byteBufferReader.getInt(), n = byteBufferReader.getInt());
        if (isoGridSquare != null) {
            Object object;
            ItemContainer itemContainer = null;
            VehiclePart vehiclePart = null;
            if (s2 == 0) {
                byte by = byteBufferReader.getByte();
                if (by < 0 || by >= isoGridSquare.getStaticMovingObjects().size()) {
                    DebugLog.log("ERROR: sendItemsToContainer: invalid corpse index");
                    return;
                }
                IsoObject i = isoGridSquare.getStaticMovingObjects().get(by);
                if (i != null && i.getContainer() != null) {
                    itemContainer = i.getContainer();
                }
            } else if (s2 == 1) {
                int arrayList = byteBufferReader.getInt();
                for (int i = 0; i < isoGridSquare.getWorldObjects().size(); ++i) {
                    object = isoGridSquare.getWorldObjects().get(i);
                    if (object == null || !(((IsoWorldInventoryObject)object).getItem() instanceof InventoryContainer) || object.getItem().id != arrayList) continue;
                    itemContainer = ((InventoryContainer)((IsoWorldInventoryObject)object).getItem()).getInventory();
                    break;
                }
                if (itemContainer == null) {
                    DebugLog.log("ERROR: sendItemsToContainer: can't find world item with id=" + arrayList);
                    return;
                }
            } else if (s2 == 2) {
                byte exception = byteBufferReader.getByte();
                byte by = byteBufferReader.getByte();
                if (exception < 0 || exception >= isoGridSquare.getObjects().size()) {
                    DebugLog.log("ERROR: sendItemsToContainer: invalid object index");
                    return;
                }
                object = isoGridSquare.getObjects().get(exception);
                itemContainer = object != null ? ((IsoObject)object).getContainerByIndex(by) : null;
            } else if (s2 == 3) {
                short s3 = byteBufferReader.getShort();
                byte by = byteBufferReader.getByte();
                object = VehicleManager.instance.getVehicleByID(s3);
                if (object == null) {
                    DebugLog.log("ERROR: sendItemsToContainer: invalid vehicle id");
                    return;
                }
                vehiclePart = ((BaseVehicle)object).getPartByIndex(by);
                if (vehiclePart == null) {
                    DebugLog.log("ERROR: sendItemsToContainer: invalid part index");
                    return;
                }
                itemContainer = vehiclePart.getItemContainer();
                if (itemContainer == null) {
                    DebugLog.log("ERROR: sendItemsToContainer: part " + vehiclePart.getId() + " has no container");
                    return;
                }
            } else {
                DebugLog.log("ERROR: sendItemsToContainer: unknown container type");
            }
            if (itemContainer != null) {
                try {
                    ArrayList<InventoryItem> arrayList = CompressIdenticalItems.load(byteBufferReader.bb, 186, null, null);
                    for (int i = 0; i < arrayList.size(); ++i) {
                        object = arrayList.get(i);
                        if (object == null) continue;
                        if (itemContainer.containsID(((InventoryItem)object).id)) {
                            if (s2 == 0) continue;
                            System.out.println("Error: Dupe item ID. id = " + ((InventoryItem)object).id);
                            continue;
                        }
                        itemContainer.addItem((InventoryItem)object);
                        itemContainer.setExplored(true);
                        if (!(itemContainer.getParent() instanceof IsoMannequin)) continue;
                        ((IsoMannequin)itemContainer.getParent()).wearItem((InventoryItem)object, null);
                    }
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (vehiclePart != null) {
                    vehiclePart.setContainerContentAmount(itemContainer.getCapacityWeight());
                }
            }
        } else {
            instance.delayPacket(n3, n2, n);
        }
    }

    private void readItemStats(ByteBuffer byteBuffer, InventoryItem inventoryItem) {
        int n = byteBuffer.getInt();
        float f = byteBuffer.getFloat();
        boolean bl = byteBuffer.get() == 1;
        inventoryItem.setUses(n);
        if (inventoryItem instanceof DrainableComboItem) {
            ((DrainableComboItem)inventoryItem).setDelta(f);
            ((DrainableComboItem)inventoryItem).updateWeight();
        }
        if (bl && inventoryItem instanceof Food) {
            Food food = (Food)inventoryItem;
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

    static void receiveItemStats(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        block0 : switch (s2) {
            case 0: {
                InventoryItem inventoryItem;
                IsoMovingObject isoMovingObject;
                ItemContainer itemContainer;
                byte by = byteBuffer.get();
                int n4 = byteBuffer.getInt();
                if (isoGridSquare == null || by < 0 || by >= isoGridSquare.getStaticMovingObjects().size() || (itemContainer = (isoMovingObject = isoGridSquare.getStaticMovingObjects().get(by)).getContainer()) == null || (inventoryItem = itemContainer.getItemWithID(n4)) == null) break;
                instance.readItemStats(byteBuffer, inventoryItem);
                break;
            }
            case 2: {
                InventoryItem inventoryItem;
                IsoObject isoObject;
                ItemContainer itemContainer;
                byte by = byteBuffer.get();
                byte by2 = byteBuffer.get();
                int n5 = byteBuffer.getInt();
                if (isoGridSquare == null || by < 0 || by >= isoGridSquare.getObjects().size() || (itemContainer = (isoObject = isoGridSquare.getObjects().get(by)).getContainerByIndex(by2)) == null || (inventoryItem = itemContainer.getItemWithID(n5)) == null) break;
                instance.readItemStats(byteBuffer, inventoryItem);
                break;
            }
            case 1: {
                int n6 = byteBuffer.getInt();
                if (isoGridSquare == null) break;
                for (int i = 0; i < isoGridSquare.getWorldObjects().size(); ++i) {
                    ItemContainer itemContainer;
                    InventoryItem inventoryItem;
                    IsoWorldInventoryObject isoWorldInventoryObject = isoGridSquare.getWorldObjects().get(i);
                    if (isoWorldInventoryObject.getItem() != null && isoWorldInventoryObject.getItem().id == n6) {
                        instance.readItemStats(byteBuffer, isoWorldInventoryObject.getItem());
                        break block0;
                    }
                    if (!(isoWorldInventoryObject.getItem() instanceof InventoryContainer) || (inventoryItem = (itemContainer = ((InventoryContainer)isoWorldInventoryObject.getItem()).getInventory()).getItemWithID(n6)) == null) continue;
                    instance.readItemStats(byteBuffer, inventoryItem);
                    break block0;
                }
                break;
            }
            case 3: {
                InventoryItem inventoryItem;
                ItemContainer itemContainer;
                VehiclePart vehiclePart;
                short s3 = byteBuffer.getShort();
                byte by = byteBuffer.get();
                int n7 = byteBuffer.getInt();
                BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(s3);
                if (baseVehicle == null || (vehiclePart = baseVehicle.getPartByIndex(by)) == null || (itemContainer = vehiclePart.getItemContainer()) == null || (inventoryItem = itemContainer.getItemWithID(n7)) == null) break;
                instance.readItemStats(byteBuffer, inventoryItem);
                break;
            }
        }
    }

    public static boolean canSeePlayerStats() {
        return !accessLevel.equals("");
    }

    public static boolean canModifyPlayerStats() {
        return accessLevel.equals("admin") || accessLevel.equals("moderator") || accessLevel.equals("overseer");
    }

    public void sendPersonalColor(IsoPlayer isoPlayer) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ChangeTextColor.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(isoPlayer.getOnlineID());
        byteBufferWriter.putFloat(Core.getInstance().getMpTextColor().r);
        byteBufferWriter.putFloat(Core.getInstance().getMpTextColor().g);
        byteBufferWriter.putFloat(Core.getInstance().getMpTextColor().b);
        PacketTypes.PacketType.ChangeTextColor.send(connection);
    }

    public void sendChangedPlayerStats(IsoPlayer isoPlayer) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        byteBufferWriter = isoPlayer.createPlayerStats(byteBufferWriter, username);
        PacketTypes.PacketType.ChangePlayerStats.send(connection);
    }

    static void receiveChangePlayerStats(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer == null) {
            return;
        }
        String string = GameWindow.ReadString(byteBuffer);
        isoPlayer.setPlayerStats(byteBuffer, string);
        allChatMuted = isoPlayer.isAllChatMuted();
    }

    public void writePlayerConnectData(ByteBufferWriter byteBufferWriter, IsoPlayer isoPlayer) {
        Object object;
        byteBufferWriter.putByte((byte)isoPlayer.PlayerIndex);
        byteBufferWriter.putByte((byte)IsoChunkMap.ChunkGridWidth);
        byteBufferWriter.putFloat(isoPlayer.x);
        byteBufferWriter.putFloat(isoPlayer.y);
        byteBufferWriter.putFloat(isoPlayer.z);
        try {
            isoPlayer.getDescriptor().save(byteBufferWriter.bb);
            isoPlayer.getHumanVisual().save(byteBufferWriter.bb);
            object = new ItemVisuals();
            isoPlayer.getItemVisuals((ItemVisuals)object);
            ((ItemVisuals)object).save(byteBufferWriter.bb);
            isoPlayer.getXp().save(byteBufferWriter.bb);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        byteBufferWriter.putBoolean(isoPlayer.isAllChatMuted());
        byteBufferWriter.putUTF(isoPlayer.getTagPrefix());
        byteBufferWriter.putFloat(isoPlayer.getTagColor().r);
        byteBufferWriter.putFloat(isoPlayer.getTagColor().g);
        byteBufferWriter.putFloat(isoPlayer.getTagColor().b);
        byteBufferWriter.putInt(isoPlayer.getTransactionID());
        byteBufferWriter.putDouble(isoPlayer.getHoursSurvived());
        byteBufferWriter.putInt(isoPlayer.getZombieKills());
        byteBufferWriter.putUTF(isoPlayer.getDisplayName());
        byteBufferWriter.putFloat(isoPlayer.getSpeakColour().r);
        byteBufferWriter.putFloat(isoPlayer.getSpeakColour().g);
        byteBufferWriter.putFloat(isoPlayer.getSpeakColour().b);
        byteBufferWriter.putBoolean(isoPlayer.showTag);
        byteBufferWriter.putBoolean(isoPlayer.factionPvp);
        if (SteamUtils.isSteamModeEnabled()) {
            byteBufferWriter.putUTF(SteamFriends.GetFriendPersonaName(SteamUser.GetSteamID()));
        }
        if ((object = isoPlayer.getPrimaryHandItem()) == null) {
            byteBufferWriter.putByte((byte)0);
        } else {
            byteBufferWriter.putByte((byte)1);
            try {
                ((InventoryItem)object).saveWithSize(byteBufferWriter.bb, false);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        InventoryItem inventoryItem = isoPlayer.getSecondaryHandItem();
        if (inventoryItem == null) {
            byteBufferWriter.putByte((byte)0);
        } else if (inventoryItem == object) {
            byteBufferWriter.putByte((byte)2);
        } else {
            byteBufferWriter.putByte((byte)1);
            try {
                inventoryItem.saveWithSize(byteBufferWriter.bb, false);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        byteBufferWriter.putInt(isoPlayer.getAttachedItems().size());
        for (int i = 0; i < isoPlayer.getAttachedItems().size(); ++i) {
            byteBufferWriter.putUTF(isoPlayer.getAttachedItems().get(i).getLocation());
            byteBufferWriter.putUTF(isoPlayer.getAttachedItems().get(i).getItem().getFullType());
        }
        byteBufferWriter.putInt(isoPlayer.getPerkLevel(PerkFactory.Perks.Sneak));
        GameClient.connection.username = isoPlayer.username;
    }

    public void sendPlayerConnect(IsoPlayer isoPlayer) {
        isoPlayer.setOnlineID((short)-1);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlayerConnect.doPacket(byteBufferWriter);
        this.writePlayerConnectData(byteBufferWriter, isoPlayer);
        PacketTypes.PacketType.PlayerConnect.send(connection);
        allChatMuted = isoPlayer.isAllChatMuted();
        GameClient.sendPerks(isoPlayer);
        isoPlayer.updateEquippedRadioFreq();
        this.bPlayerConnectSent = true;
    }

    public void sendPlayerSave(IsoPlayer isoPlayer) {
        if (connection == null) {
            return;
        }
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlayerSave.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)isoPlayer.PlayerIndex);
        byteBufferWriter.putShort(isoPlayer.OnlineID);
        byteBufferWriter.putFloat(isoPlayer.x);
        byteBufferWriter.putFloat(isoPlayer.y);
        byteBufferWriter.putFloat(isoPlayer.z);
        PacketTypes.PacketType.PlayerSave.send(connection);
    }

    public void sendPlayer2(IsoPlayer isoPlayer) {
        if (!(bClient && isoPlayer.isLocalPlayer() && isoPlayer.networkAI.isNeedToUpdate())) {
            return;
        }
        if (PlayerPacket.l_send.playerPacket.set(isoPlayer)) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType packetType = this.PlayerUpdateReliableLimit.Check() ? PacketTypes.PacketType.PlayerUpdateReliable : PacketTypes.PacketType.PlayerUpdate;
            packetType.doPacket(byteBufferWriter);
            PlayerPacket.l_send.playerPacket.write(byteBufferWriter);
            packetType.send(connection);
        }
    }

    public void sendPlayer(IsoPlayer isoPlayer) {
        isoPlayer.networkAI.needToUpdate();
    }

    public void sendSteamProfileName(long l) {
        if (!SteamUtils.isSteamModeEnabled()) {
            return;
        }
        for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
            IsoPlayer isoPlayer = IsoPlayer.players[i];
            if (isoPlayer == null || isoPlayer.getSteamID() != l) continue;
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.SteamGeneric.doPacket(byteBufferWriter);
            byteBufferWriter.putShort((short)0);
            byteBufferWriter.putByte((byte)isoPlayer.getPlayerNum());
            byteBufferWriter.putUTF(SteamFriends.GetFriendPersonaName(l));
            PacketTypes.PacketType.SteamGeneric.send(connection);
            return;
        }
    }

    public void heartBeat() {
        ++count;
    }

    public static IsoZombie getZombie(short s) {
        return (IsoZombie)IDToZombieMap.get(s);
    }

    public static void sendPlayerExtraInfo(IsoPlayer isoPlayer) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ExtraInfo.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(isoPlayer.OnlineID);
        byteBufferWriter.putUTF(isoPlayer.accessLevel);
        byteBufferWriter.putByte(isoPlayer.isGodMod() ? (byte)1 : 0);
        byteBufferWriter.putByte(isoPlayer.isGhostMode() ? (byte)1 : 0);
        byteBufferWriter.putByte(isoPlayer.isInvisible() ? (byte)1 : 0);
        byteBufferWriter.putByte(isoPlayer.isNoClip() ? (byte)1 : 0);
        byteBufferWriter.putByte(isoPlayer.isShowAdminTag() ? (byte)1 : 0);
        byteBufferWriter.putByte(isoPlayer.isCanHearAll() ? (byte)1 : 0);
        PacketTypes.PacketType.ExtraInfo.send(connection);
    }

    static void receiveExtraInfo(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        String string = GameWindow.ReadString(byteBuffer);
        boolean bl = byteBuffer.get() == 1;
        boolean bl2 = byteBuffer.get() == 1;
        boolean bl3 = byteBuffer.get() == 1;
        boolean bl4 = byteBuffer.get() == 1;
        boolean bl5 = byteBuffer.get() == 1;
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            isoPlayer.accessLevel = string;
            isoPlayer.setGodMod(bl);
            isoPlayer.setInvisible(bl3);
            isoPlayer.setGhostMode(bl2);
            isoPlayer.setNoClip(bl4);
            isoPlayer.setShowAdminTag(bl5);
            if (!isoPlayer.bRemote) {
                accessLevel = string;
                for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                    IsoPlayer isoPlayer2 = IsoPlayer.players[i];
                    if (isoPlayer2 == null || isoPlayer2.accessLevel.equals("")) continue;
                    accessLevel = isoPlayer2.accessLevel;
                    break;
                }
            }
        }
    }

    static void receiveConnectionDetails(ByteBuffer byteBuffer, short s) {
        Calendar calendar = Calendar.getInstance();
        System.out.println("LOGGED INTO : " + (calendar.getTimeInMillis() - startAuth.getTimeInMillis()) + " millisecond");
        ConnectToServerState connectToServerState = new ConnectToServerState(byteBuffer);
        connectToServerState.enter();
        MainScreenState.getInstance().setConnectToServerState(connectToServerState);
    }

    public void setResetID(int n) {
        this.loadResetID();
        if (this.ResetID != n) {
            File file;
            File file2;
            int n2;
            boolean bl = true;
            ArrayList<String> arrayList = IsoPlayer.getAllFileNames();
            arrayList.add("map_p.bin");
            if (bl) {
                for (n2 = 0; n2 < arrayList.size(); ++n2) {
                    try {
                        file2 = ZomboidFileSystem.instance.getFileInCurrentSave(arrayList.get(n2));
                        if (!file2.exists()) continue;
                        file = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + arrayList.get(n2));
                        if (file.exists()) {
                            file.delete();
                        }
                        file2.renameTo(file);
                        continue;
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
            DebugLog.log("server was reset, deleting " + Core.GameSaveWorld);
            LuaManager.GlobalObject.deleteSave(Core.GameSaveWorld);
            LuaManager.GlobalObject.createWorld(Core.GameSaveWorld);
            if (bl) {
                for (n2 = 0; n2 < arrayList.size(); ++n2) {
                    try {
                        file2 = ZomboidFileSystem.instance.getFileInCurrentSave(arrayList.get(n2));
                        file = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + arrayList.get(n2));
                        if (file == null) continue;
                        file.renameTo(file2);
                        continue;
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
        this.ResetID = n;
        this.saveResetID();
    }

    public void loadResetID() {
        File file = ZomboidFileSystem.instance.getFileInCurrentSave("serverid.dat");
        if (file.exists()) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
            }
            catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            try {
                this.ResetID = dataInputStream.readInt();
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
            try {
                fileInputStream.close();
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    private void saveResetID() {
        File file = ZomboidFileSystem.instance.getFileInCurrentSave("serverid.dat");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        }
        catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
        try {
            dataOutputStream.writeInt(this.ResetID);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        try {
            fileOutputStream.close();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    static void receivePlayerConnect(ByteBuffer byteBuffer, short s) {
        Object object;
        int n;
        boolean bl = false;
        short s2 = byteBuffer.getShort();
        int n2 = -1;
        if (s2 == -1) {
            bl = true;
            n2 = byteBuffer.get();
            s2 = byteBuffer.getShort();
            try {
                GameTime.getInstance().load(byteBuffer);
                GameTime.getInstance().ServerTimeOfDay = GameTime.getInstance().getTimeOfDay();
                GameTime.getInstance().ServerNewDays = 0;
                GameTime.getInstance().setMinutesPerDay(SandboxOptions.instance.getDayLengthMinutes());
                LuaEventManager.triggerEvent("OnGameTimeLoaded");
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        } else if (IDToPlayerMap.containsKey(s2)) {
            return;
        }
        float f = byteBuffer.getFloat();
        float f2 = byteBuffer.getFloat();
        float f3 = byteBuffer.getFloat();
        IsoPlayer isoPlayer = null;
        if (bl) {
            var9_10 = GameWindow.ReadString(byteBuffer);
            for (n = 0; n < IsoWorld.instance.AddCoopPlayers.size(); ++n) {
                IsoWorld.instance.AddCoopPlayers.get(n).receivePlayerConnect(n2);
            }
            isoPlayer = IsoPlayer.players[n2];
            isoPlayer.username = var9_10;
            isoPlayer.setOnlineID(s2);
        } else {
            var9_10 = GameWindow.ReadString(byteBuffer);
            SurvivorDesc survivorDesc = SurvivorFactory.CreateSurvivor();
            try {
                survivorDesc.load(byteBuffer, 186, null);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
            try {
                isoPlayer = new IsoPlayer(IsoWorld.instance.CurrentCell, survivorDesc, (int)f, (int)f2, (int)f3);
                isoPlayer.bRemote = true;
                isoPlayer.getHumanVisual().load(byteBuffer, 186);
                isoPlayer.getItemVisuals().load(byteBuffer, 186);
                isoPlayer.username = var9_10;
                isoPlayer.updateUsername();
                isoPlayer.setSceneCulled(false);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            isoPlayer.setX(f);
            isoPlayer.setY(f2);
            isoPlayer.setZ(f3);
            isoPlayer.networkAI.targetX = f;
            isoPlayer.networkAI.targetY = f2;
            isoPlayer.networkAI.targetZ = (int)f3;
        }
        isoPlayer.setOnlineID(s2);
        if (SteamUtils.isSteamModeEnabled()) {
            isoPlayer.setSteamID(byteBuffer.getLong());
        }
        isoPlayer.setGodMod(byteBuffer.get() == 1);
        isoPlayer.setGhostMode(byteBuffer.get() == 1);
        isoPlayer.setSafety(byteBuffer.get() == 1);
        isoPlayer.accessLevel = GameWindow.ReadString(byteBuffer);
        isoPlayer.setInvisible(byteBuffer.get() == 1);
        if (!bl) {
            try {
                isoPlayer.getXp().load(byteBuffer, 186);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        isoPlayer.setTagPrefix(GameWindow.ReadString(byteBuffer));
        isoPlayer.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0f));
        isoPlayer.setHoursSurvived(byteBuffer.getDouble());
        isoPlayer.setZombieKills(byteBuffer.getInt());
        isoPlayer.setDisplayName(GameWindow.ReadString(byteBuffer));
        isoPlayer.setSpeakColour(new Color(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0f));
        isoPlayer.showTag = byteBuffer.get() == 1;
        isoPlayer.factionPvp = byteBuffer.get() == 1;
        int n3 = byteBuffer.getInt();
        for (n = 0; n < n3; ++n) {
            String string = GameWindow.ReadString(byteBuffer);
            InventoryItem inventoryItem = InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer));
            if (inventoryItem == null) continue;
            isoPlayer.setAttachedItem(string, inventoryItem);
        }
        n = byteBuffer.getInt();
        int n4 = byteBuffer.getInt();
        int n5 = byteBuffer.getInt();
        isoPlayer.remoteSneakLvl = n;
        isoPlayer.remoteStrLvl = n4;
        isoPlayer.remoteFitLvl = n5;
        if (Core.bDebug) {
            DebugLog.log(DebugType.Network, "Player Connect received for player " + username + " id " + s2 + (bl ? " (local)" : " (remote)"));
        }
        if (DebugOptions.instance.MultiplayerShowPosition.getValue() && !bl) {
            if (positions.containsKey(isoPlayer.getOnlineID())) {
                positions.get(isoPlayer.getOnlineID()).set(f, f2);
            } else {
                positions.put(isoPlayer.getOnlineID(), new Vector2(f, f2));
            }
        }
        IDToPlayerMap.put(s2, isoPlayer);
        GameClient.instance.idMapDirty = true;
        LuaEventManager.triggerEvent("OnMiniScoreboardUpdate");
        if (bl) {
            GameClient.getCustomModData();
        }
        if (!bl && ServerOptions.instance.DisableSafehouseWhenPlayerConnected.getValue() && (object = SafeHouse.hasSafehouse(isoPlayer)) != null) {
            ((SafeHouse)object).setPlayerConnected(((SafeHouse)object).getPlayerConnected() + 1);
        }
        object = ServerOptions.getInstance().getOption("ServerWelcomeMessage");
        if (bl && object != null && !((String)object).equals("")) {
            ChatManager.getInstance().showServerChatMessage((String)object);
        }
    }

    static void receiveScoreboardUpdate(ByteBuffer byteBuffer, short s) {
        int n = byteBuffer.getInt();
        GameClient.instance.connectedPlayers = new ArrayList();
        ArrayList<String> arrayList = new ArrayList<String>();
        ArrayList<String> arrayList2 = new ArrayList<String>();
        ArrayList<String> arrayList3 = new ArrayList<String>();
        for (int i = 0; i < n; ++i) {
            String string = GameWindow.ReadString(byteBuffer);
            String string2 = GameWindow.ReadString(byteBuffer);
            arrayList.add(string);
            arrayList2.add(string2);
            GameClient.instance.connectedPlayers.add(instance.getPlayerFromUsername(string));
            if (!SteamUtils.isSteamModeEnabled()) continue;
            String string3 = SteamUtils.convertSteamIDToString(byteBuffer.getLong());
            arrayList3.add(string3);
        }
        LuaEventManager.triggerEvent("OnScoreboardUpdate", arrayList, arrayList2, arrayList3);
    }

    public boolean receivePlayerConnectWhileLoading(ByteBuffer byteBuffer) {
        boolean bl = false;
        short s = byteBuffer.getShort();
        int n = -1;
        if (s != -1) {
            return false;
        }
        if (s == -1) {
            bl = true;
            n = byteBuffer.get();
            s = byteBuffer.getShort();
            try {
                GameTime.getInstance().load(byteBuffer);
                LuaEventManager.triggerEvent("OnGameTimeLoaded");
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        float f = byteBuffer.getFloat();
        float f2 = byteBuffer.getFloat();
        float f3 = byteBuffer.getFloat();
        IsoPlayer isoPlayer = null;
        if (bl) {
            var9_10 = GameWindow.ReadString(byteBuffer);
            isoPlayer = IsoPlayer.players[n];
            isoPlayer.username = var9_10;
            isoPlayer.setOnlineID(s);
        } else {
            var9_10 = GameWindow.ReadString(byteBuffer);
            SurvivorDesc survivorDesc = SurvivorFactory.CreateSurvivor();
            try {
                survivorDesc.load(byteBuffer, 186, null);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
            try {
                isoPlayer = new IsoPlayer(IsoWorld.instance.CurrentCell, survivorDesc, (int)f, (int)f2, (int)f3);
                isoPlayer.getHumanVisual().load(byteBuffer, 186);
                isoPlayer.getItemVisuals().load(byteBuffer, 186);
                isoPlayer.username = var9_10;
                isoPlayer.updateUsername();
                isoPlayer.setSceneCulled(false);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            isoPlayer.bRemote = true;
            isoPlayer.setX(f);
            isoPlayer.setY(f2);
            isoPlayer.setZ(f3);
        }
        isoPlayer.setOnlineID(s);
        if (Core.bDebug) {
            DebugLog.log(DebugType.Network, "Player Connect received for player " + username + " id " + s + (bl ? " (me)" : " (not me)"));
        }
        int n2 = byteBuffer.getInt();
        for (int i = 0; i < n2; ++i) {
            ServerOptions.instance.putOption(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer));
        }
        isoPlayer.setGodMod(byteBuffer.get() == 1);
        isoPlayer.setGhostMode(byteBuffer.get() == 1);
        isoPlayer.setSafety(byteBuffer.get() == 1);
        isoPlayer.accessLevel = GameWindow.ReadString(byteBuffer);
        isoPlayer.setInvisible(byteBuffer.get() == 1);
        IDToPlayerMap.put(s, isoPlayer);
        this.idMapDirty = true;
        GameClient.getCustomModData();
        String string = ServerOptions.getInstance().getOption("ServerWelcomeMessage");
        if (bl && string != null && !string.equals("")) {
            ChatManager.getInstance().showServerChatMessage(string);
        }
        return true;
    }

    public ArrayList<IsoPlayer> getPlayers() {
        if (!this.idMapDirty) {
            return this.players;
        }
        this.players.clear();
        this.players.addAll(IDToPlayerMap.values());
        this.idMapDirty = false;
        return this.players;
    }

    private IsoObject getIsoObjectRefFromByteBuffer(ByteBuffer byteBuffer) {
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        byte by = byteBuffer.get();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare == null) {
            this.delayPacket(n, n2, n3);
            return null;
        }
        if (by >= 0 && by < isoGridSquare.getObjects().size()) {
            return isoGridSquare.getObjects().get(by);
        }
        return null;
    }

    public void sendWeaponHit(IsoPlayer isoPlayer, HandWeapon handWeapon, IsoObject isoObject) {
        if (isoPlayer == null || isoObject == null || !isoPlayer.isLocalPlayer()) {
            return;
        }
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.WeaponHit.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(isoObject.square.x);
        byteBufferWriter.putInt(isoObject.square.y);
        byteBufferWriter.putInt(isoObject.square.z);
        byteBufferWriter.putByte((byte)isoObject.getObjectIndex());
        byteBufferWriter.putShort(isoPlayer.OnlineID);
        byteBufferWriter.putUTF(handWeapon != null ? handWeapon.getFullType() : "");
        PacketTypes.PacketType.WeaponHit.send(connection);
    }

    public static void SyncCustomLightSwitchSettings(ByteBuffer byteBuffer) {
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        byte by = byteBuffer.get();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
        if (isoGridSquare != null && by >= 0 && by < isoGridSquare.getObjects().size()) {
            if (isoGridSquare.getObjects().get(by) instanceof IsoLightSwitch) {
                ((IsoLightSwitch)isoGridSquare.getObjects().get(by)).receiveSyncCustomizedSettings(byteBuffer, null);
            } else {
                DebugLog.log("Sync Lightswitch custom settings: found object not a instance of IsoLightSwitch, x,y,z=" + n + "," + n2 + "," + n3);
            }
        } else if (isoGridSquare != null) {
            DebugLog.log("Sync Lightswitch custom settings: index=" + by + " is invalid x,y,z=" + n + "," + n2 + "," + n3);
        } else if (Core.bDebug) {
            DebugLog.log("Sync Lightswitch custom settings: sq is null x,y,z=" + n + "," + n2 + "," + n3);
        }
    }

    static void receiveSyncIsoObjectReq(ByteBuffer byteBuffer, short s) {
        if (!SystemDisabler.doObjectStateSyncEnable) {
            return;
        }
        int n = byteBuffer.getShort();
        for (int i = 0; i < n; ++i) {
            GameClient.receiveSyncIsoObject(byteBuffer, s);
        }
    }

    static void receiveSyncWorldObjectsReq(ByteBuffer byteBuffer, short s) {
        DebugLog.log("SyncWorldObjectsReq client : ");
        int n = byteBuffer.getShort();
        for (int i = 0; i < n; ++i) {
            int n2 = byteBuffer.getInt();
            int n3 = byteBuffer.getInt();
            GameClient.instance.worldObjectsSyncReq.receiveSyncIsoChunk(n2, n3);
            short s2 = byteBuffer.getShort();
            DebugLog.log("[" + n2 + "," + n3 + "]:" + s2 + " ");
            IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n2 * 10, n3 * 10, 0);
            if (isoGridSquare == null) {
                return;
            }
            IsoChunk isoChunk = isoGridSquare.getChunk();
            ++isoChunk.ObjectsSyncCount;
            isoChunk.recalcHashCodeObjects();
        }
        DebugLog.log(";\n");
    }

    static void receiveSyncObjects(ByteBuffer byteBuffer, short s) {
        if (!SystemDisabler.doWorldSyncEnable) {
            return;
        }
        short s2 = byteBuffer.getShort();
        if (s2 == 2) {
            GameClient.instance.worldObjectsSyncReq.receiveGridSquareHashes(byteBuffer);
        }
        if (s2 == 4) {
            GameClient.instance.worldObjectsSyncReq.receiveGridSquareObjectHashes(byteBuffer);
        }
        if (s2 == 6) {
            GameClient.instance.worldObjectsSyncReq.receiveObject(byteBuffer);
        }
    }

    static void receiveSyncIsoObject(ByteBuffer byteBuffer, short s) {
        if (!DebugOptions.instance.Network.Client.SyncIsoObject.getValue()) {
            return;
        }
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        byte by = byteBuffer.get();
        byte by2 = byteBuffer.get();
        byte by3 = byteBuffer.get();
        if (by2 != 2) {
            GameClient.instance.objectSyncReq.receiveIsoSync(n, n2, n3, by);
        }
        if (by2 == 1) {
            IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
            if (isoGridSquare == null) {
                return;
            }
            if (by >= 0 && by < isoGridSquare.getObjects().size()) {
                isoGridSquare.getObjects().get(by).syncIsoObject(true, by3, null, byteBuffer);
            } else {
                DebugLog.Network.warn("SyncIsoObject: index=" + by + " is invalid x,y,z=" + n + "," + n2 + "," + n3);
            }
        }
    }

    static void receiveSyncAlarmClock(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        if (s2 == AlarmClock.PacketPlayer) {
            short s3 = byteBuffer.getShort();
            long l = byteBuffer.getLong();
            boolean bl = byteBuffer.get() == 1;
            int n = bl ? 0 : byteBuffer.getInt();
            int n2 = bl ? 0 : byteBuffer.getInt();
            byte by = bl ? (byte)0 : byteBuffer.get();
            IsoPlayer isoPlayer = IDToPlayerMap.get(s3);
            if (isoPlayer == null) {
                return;
            }
            for (int i = 0; i < isoPlayer.getInventory().getItems().size(); ++i) {
                InventoryItem inventoryItem = isoPlayer.getInventory().getItems().get(i);
                if (!(inventoryItem instanceof AlarmClock) || (long)inventoryItem.getID() != l) continue;
                if (bl) {
                    ((AlarmClock)inventoryItem).stopRinging();
                    break;
                }
                ((AlarmClock)inventoryItem).setAlarmSet(by == 1);
                ((AlarmClock)inventoryItem).setHour(n);
                ((AlarmClock)inventoryItem).setMinute(n2);
                break;
            }
            return;
        }
        if (s2 == AlarmClock.PacketWorld) {
            int n = byteBuffer.getInt();
            int n3 = byteBuffer.getInt();
            int n4 = byteBuffer.getInt();
            int n5 = byteBuffer.getInt();
            boolean bl = byteBuffer.get() == 1;
            int n6 = bl ? 0 : byteBuffer.getInt();
            int n7 = bl ? 0 : byteBuffer.getInt();
            byte by = bl ? (byte)0 : byteBuffer.get();
            IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n3, n4);
            if (isoGridSquare == null) {
                return;
            }
            for (int i = 0; i < isoGridSquare.getWorldObjects().size(); ++i) {
                IsoWorldInventoryObject isoWorldInventoryObject = isoGridSquare.getWorldObjects().get(i);
                if (isoWorldInventoryObject == null || !(isoWorldInventoryObject.getItem() instanceof AlarmClock) || isoWorldInventoryObject.getItem().id != n5) continue;
                AlarmClock alarmClock = (AlarmClock)isoWorldInventoryObject.getItem();
                if (bl) {
                    alarmClock.stopRinging();
                    break;
                }
                alarmClock.setAlarmSet(by == 1);
                alarmClock.setHour(n6);
                alarmClock.setMinute(n7);
                break;
            }
            return;
        }
    }

    static void receiveAddItemToMap(ByteBuffer byteBuffer, short s) {
        if (IsoWorld.instance.CurrentCell == null) {
            return;
        }
        IsoObject isoObject = WorldItemTypes.createFromBuffer(byteBuffer);
        isoObject.loadFromRemoteBuffer(byteBuffer);
        if (isoObject.square != null) {
            if (isoObject instanceof IsoLightSwitch) {
                ((IsoLightSwitch)isoObject).addLightSourceFromSprite();
            }
            isoObject.addToWorld();
            IsoWorld.instance.CurrentCell.checkHaveRoof(isoObject.square.getX(), isoObject.square.getY());
            if (!(isoObject instanceof IsoWorldInventoryObject)) {
                for (int i = 0; i < IsoPlayer.numPlayers; ++i) {
                    LosUtil.cachecleared[i] = true;
                }
                IsoGridSquare.setRecalcLightTime(-1);
                GameTime.instance.lightSourceUpdate = 100.0f;
                MapCollisionData.instance.squareChanged(isoObject.square);
                PolygonalMap2.instance.squareChanged(isoObject.square);
                if (isoObject == isoObject.square.getPlayerBuiltFloor()) {
                    IsoGridOcclusionData.SquareChanged();
                }
            }
            if (isoObject instanceof IsoWorldInventoryObject || isoObject.getContainer() != null) {
                LuaEventManager.triggerEvent("OnContainerUpdate", isoObject);
            }
        }
    }

    static void skipPacket(ByteBuffer byteBuffer, short s) {
    }

    static void receiveAccessDenied(ByteBuffer byteBuffer, short s) {
        String string = GameWindow.ReadString(byteBuffer);
        String[] arrstring = string.split("##");
        LuaEventManager.triggerEvent("OnConnectFailed", arrstring.length > 0 ? Translator.getText("UI_OnConnectFailed_" + arrstring[0], arrstring.length > 1 ? arrstring[1] : null, arrstring.length > 2 ? arrstring[2] : null, arrstring.length > 3 ? arrstring[3] : null) : null);
    }

    static void receivePlayerTimeout(ByteBuffer byteBuffer, short s) {
        IsoPlayer isoPlayer;
        short s2 = byteBuffer.getShort();
        if (DebugOptions.instance.MultiplayerShowPosition.getValue()) {
            positions.remove(s2);
        }
        if ((isoPlayer = IDToPlayerMap.get(s2)) == null) {
            return;
        }
        DebugLog.log("Received timeout for player " + isoPlayer.username + " id " + isoPlayer.OnlineID);
        NetworkZombieSimulator.getInstance().clearTargetAuth(isoPlayer);
        if (isoPlayer.getVehicle() != null) {
            int n = isoPlayer.getVehicle().getSeat(isoPlayer);
            if (n != -1) {
                isoPlayer.getVehicle().clearPassenger(n);
            }
            VehicleManager.instance.sendReqestGetPosition(isoPlayer.getVehicle().VehicleID);
        }
        isoPlayer.removeFromWorld();
        isoPlayer.removeFromSquare();
        IDToPlayerMap.remove(isoPlayer.OnlineID);
        GameClient.instance.idMapDirty = true;
        LuaEventManager.triggerEvent("OnMiniScoreboardUpdate");
    }

    public void disconnect() {
        this.bConnected = false;
        if (IsoPlayer.getInstance() != null) {
            IsoPlayer.getInstance().setOnlineID((short)-1);
        }
    }

    public void addIncoming(short s, ByteBuffer byteBuffer) {
        if (connection == null) {
            return;
        }
        if (s == PacketTypes.PacketType.SentChunk.getId()) {
            WorldStreamer.instance.receiveChunkPart(byteBuffer);
            return;
        }
        if (s == PacketTypes.PacketType.NotRequiredInZip.getId()) {
            WorldStreamer.instance.receiveNotRequired(byteBuffer);
            return;
        }
        if (s == PacketTypes.PacketType.LoadPlayerProfile.getId()) {
            ClientPlayerDB.getInstance().clientLoadNetworkCharacter(byteBuffer, connection);
            return;
        }
        ZomboidNetData zomboidNetData = null;
        zomboidNetData = byteBuffer.remaining() > 2048 ? ZomboidNetDataPool.instance.getLong(byteBuffer.remaining()) : ZomboidNetDataPool.instance.get();
        zomboidNetData.read(s, byteBuffer, connection);
        zomboidNetData.time = System.currentTimeMillis();
        MainLoopNetDataQ.add(zomboidNetData);
    }

    public void doDisconnect(String string) {
        if (this.bConnected && connection != null) {
            connection.forceDisconnect();
            this.bConnected = false;
            connection = null;
            bClient = false;
        }
    }

    public void removeZombieFromCache(IsoZombie isoZombie) {
        if (IDToZombieMap.containsKey(isoZombie.OnlineID)) {
            IDToZombieMap.remove(isoZombie.OnlineID);
        }
    }

    static void receiveEquip(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        byte by = byteBuffer.get();
        byte by2 = byteBuffer.get();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer == IsoPlayer.getInstance()) {
            return;
        }
        InventoryItem inventoryItem = null;
        if (by2 == 1) {
            try {
                inventoryItem = InventoryItem.loadItem(byteBuffer, 186);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (isoPlayer != null && by == 1 && by2 == 2) {
            inventoryItem = isoPlayer.getPrimaryHandItem();
        }
        if (isoPlayer != null) {
            if (by == 0) {
                isoPlayer.setPrimaryHandItem(inventoryItem);
            } else {
                isoPlayer.setSecondaryHandItem(inventoryItem);
            }
            try {
                if (inventoryItem != null) {
                    inventoryItem.setContainer(isoPlayer.getInventory());
                    if (by2 == 1 && byteBuffer.get() == 1) {
                        inventoryItem.getVisual().load(byteBuffer, 186);
                    }
                }
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    public void equip(IsoPlayer isoPlayer, int n) {
        InventoryItem inventoryItem = null;
        inventoryItem = n == 0 ? isoPlayer.getPrimaryHandItem() : isoPlayer.getSecondaryHandItem();
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Equip.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)isoPlayer.PlayerIndex);
        byteBufferWriter.putByte((byte)n);
        if (inventoryItem == null) {
            byteBufferWriter.putByte((byte)0);
        } else if (n == 1 && isoPlayer.getPrimaryHandItem() == isoPlayer.getSecondaryHandItem()) {
            byteBufferWriter.putByte((byte)2);
        } else {
            byteBufferWriter.putByte((byte)1);
            try {
                inventoryItem.saveWithSize(byteBufferWriter.bb, false);
                if (inventoryItem.getVisual() != null) {
                    byteBufferWriter.bb.put((byte)1);
                    inventoryItem.getVisual().save(byteBufferWriter.bb);
                } else {
                    byteBufferWriter.bb.put((byte)0);
                }
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        PacketTypes.PacketType.Equip.send(connection);
    }

    public void sendWorldMessage(String string) {
        ChatManager.getInstance().showInfoMessage(string);
    }

    private void convertGameSaveWorldDirectory(String string, String string2) {
        File file = new File(string);
        if (!file.isDirectory()) {
            return;
        }
        File file2 = new File(string2);
        boolean bl = file.renameTo(file2);
        if (bl) {
            DebugLog.log("CONVERT: The GameSaveWorld directory was renamed from " + string + " to " + string2);
        } else {
            DebugLog.log("ERROR: The GameSaveWorld directory cannot rename from " + string + " to " + string2);
        }
    }

    public void doConnect(String string, String string2, String string3, String string4, String string5, String string6) {
        username = string.trim();
        password = string2.trim();
        ip = string3.trim();
        localIP = string4.trim();
        port = Integer.parseInt(string5.trim());
        serverPassword = string6.trim();
        instance.init();
        Core.GameSaveWorld = ip + "_" + port + "_" + ServerWorldDatabase.encrypt(string);
        this.convertGameSaveWorldDirectory(ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + ip + "_" + port + "_" + string, ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + Core.GameSaveWorld);
        if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
            Core.GameSaveWorld = CoopMaster.instance.getPlayerSaveFolder(CoopMaster.instance.getServerName());
        }
    }

    public void doConnectCoop(String string) {
        username = SteamFriends.GetPersonaName();
        password = "";
        ip = string;
        localIP = "";
        port = 0;
        serverPassword = "";
        this.init();
        if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
            Core.GameSaveWorld = CoopMaster.instance.getPlayerSaveFolder(CoopMaster.instance.getServerName());
        }
    }

    public void scoreboardUpdate() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ScoreboardUpdate.doPacket(byteBufferWriter);
        PacketTypes.PacketType.ScoreboardUpdate.send(connection);
    }

    public void sendWorldSound(Object object, int n, int n2, int n3, int n4, int n5, boolean bl, float f, float f2) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.WorldSound.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(n);
        byteBufferWriter.putInt(n2);
        byteBufferWriter.putInt(n3);
        byteBufferWriter.putInt(n4);
        byteBufferWriter.putInt(n5);
        byteBufferWriter.putByte(bl ? (byte)1 : 0);
        byteBufferWriter.putFloat(f);
        byteBufferWriter.putFloat(f2);
        byteBufferWriter.putByte(object instanceof IsoZombie ? (byte)1 : 0);
        PacketTypes.PacketType.WorldSound.send(connection);
    }

    static void receivePlayWorldSound(ByteBuffer byteBuffer, short s) {
        PlayWorldSoundPacket playWorldSoundPacket = new PlayWorldSoundPacket();
        playWorldSoundPacket.parse(byteBuffer);
        playWorldSoundPacket.process();
        DebugLog.log(DebugType.Sound, playWorldSoundPacket.getDescription());
    }

    static void receivePlaySound(ByteBuffer byteBuffer, short s) {
        PlaySoundPacket playSoundPacket = new PlaySoundPacket();
        playSoundPacket.parse(byteBuffer);
        playSoundPacket.process();
        DebugLog.log(DebugType.Sound, playSoundPacket.getDescription());
    }

    static void receiveStopSound(ByteBuffer byteBuffer, short s) {
        StopSoundPacket stopSoundPacket = new StopSoundPacket();
        stopSoundPacket.parse(byteBuffer);
        stopSoundPacket.process();
        DebugLog.log(DebugType.Sound, stopSoundPacket.getDescription());
    }

    static void receiveWorldSound(ByteBuffer byteBuffer, short s) {
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        int n4 = byteBuffer.getInt();
        int n5 = byteBuffer.getInt();
        boolean bl = byteBuffer.get() == 1;
        float f = byteBuffer.getFloat();
        float f2 = byteBuffer.getFloat();
        boolean bl2 = byteBuffer.get() == 1;
        WorldSoundManager.instance.addSound(null, n, n2, n3, n4, n5, bl, f, f2, bl2, false, true);
    }

    private void receiveDataZombieDescriptors(ByteBuffer byteBuffer) throws IOException {
        DebugLog.log(DebugType.NetworkFileDebug, "received zombie descriptors");
        PersistentOutfits.instance.load(byteBuffer);
    }

    private void receivePlayerZombieDescriptors(ByteBuffer byteBuffer) throws IOException {
        short s = byteBuffer.getShort();
        DebugLog.log(DebugType.NetworkFileDebug, "received " + s + " player-zombie descriptors");
        for (short s2 = 0; s2 < s; s2 = (short)(s2 + 1)) {
            SharedDescriptors.Descriptor descriptor = new SharedDescriptors.Descriptor();
            descriptor.load(byteBuffer, 186);
            SharedDescriptors.registerPlayerZombieDescriptor(descriptor);
        }
    }

    static void receiveAddAmbient(ByteBuffer byteBuffer, short s) {
        String string = GameWindow.ReadString(byteBuffer);
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        float f = byteBuffer.getFloat();
        DebugLog.log(DebugType.Sound, "ambient: received " + string + " at " + n + "," + n2 + " radius=" + n3);
        AmbientStreamManager.instance.addAmbient(string, n, n2, n3, f);
    }

    public void sendClientCommand(IsoPlayer isoPlayer, String string, String string2, KahluaTable kahluaTable) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ClientCommand.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)(isoPlayer != null ? isoPlayer.PlayerIndex : -1));
        byteBufferWriter.putUTF(string);
        byteBufferWriter.putUTF(string2);
        if (kahluaTable == null || kahluaTable.isEmpty()) {
            byteBufferWriter.putByte((byte)0);
        } else {
            byteBufferWriter.putByte((byte)1);
            try {
                KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
                while (kahluaTableIterator.advance()) {
                    if (TableNetworkUtils.canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) continue;
                    DebugLog.log("ERROR: sendClientCommand: can't save key,value=" + kahluaTableIterator.getKey() + "," + kahluaTableIterator.getValue());
                }
                TableNetworkUtils.save(kahluaTable, byteBufferWriter.bb);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        PacketTypes.PacketType.ClientCommand.send(connection);
    }

    public void sendClientCommandV(IsoPlayer isoPlayer, String string, String string2, Object ... arrobject) {
        if (arrobject.length == 0) {
            this.sendClientCommand(isoPlayer, string, string2, null);
            return;
        }
        if (arrobject.length % 2 != 0) {
            DebugLog.log("ERROR: sendClientCommand called with wrong number of arguments (" + string + " " + string2 + ")");
            return;
        }
        KahluaTable kahluaTable = LuaManager.platform.newTable();
        for (int i = 0; i < arrobject.length; i += 2) {
            Object object = arrobject[i + 1];
            if (object instanceof Float) {
                kahluaTable.rawset(arrobject[i], (Object)((Float)object).doubleValue());
                continue;
            }
            if (object instanceof Integer) {
                kahluaTable.rawset(arrobject[i], (Object)((Integer)object).doubleValue());
                continue;
            }
            if (object instanceof Short) {
                kahluaTable.rawset(arrobject[i], (Object)((Short)object).doubleValue());
                continue;
            }
            kahluaTable.rawset(arrobject[i], object);
        }
        this.sendClientCommand(isoPlayer, string, string2, kahluaTable);
    }

    public void sendClothing(IsoPlayer isoPlayer, String string, InventoryItem inventoryItem) {
        if (isoPlayer == null || isoPlayer.OnlineID == -1) {
            return;
        }
        SyncClothingPacket syncClothingPacket = new SyncClothingPacket();
        syncClothingPacket.set(isoPlayer, string, inventoryItem);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncClothing.doPacket(byteBufferWriter);
        syncClothingPacket.write(byteBufferWriter);
        PacketTypes.PacketType.SyncClothing.send(connection);
    }

    static void receiveSyncClothing(ByteBuffer byteBuffer, short s) {
        SyncClothingPacket syncClothingPacket = new SyncClothingPacket();
        syncClothingPacket.parse(byteBuffer);
    }

    public void sendAttachedItem(IsoPlayer isoPlayer, String string, InventoryItem inventoryItem) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlayerAttachedItem.doPacket(byteBufferWriter);
        try {
            byteBufferWriter.putByte((byte)isoPlayer.PlayerIndex);
            GameWindow.WriteString(byteBufferWriter.bb, string);
            if (inventoryItem != null) {
                byteBufferWriter.putByte((byte)1);
                GameWindow.WriteString(byteBufferWriter.bb, inventoryItem.getFullType());
            } else {
                byteBufferWriter.putByte((byte)0);
            }
            PacketTypes.PacketType.PlayerAttachedItem.send(connection);
        }
        catch (Throwable throwable) {
            connection.cancelPacket();
            ExceptionLogger.logException(throwable);
        }
    }

    static void receivePlayerAttachedItem(ByteBuffer byteBuffer, short s) {
        boolean bl;
        short s2 = byteBuffer.getShort();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer == null || isoPlayer.isLocalPlayer()) {
            return;
        }
        String string = GameWindow.ReadString(byteBuffer);
        boolean bl2 = bl = byteBuffer.get() == 1;
        if (bl) {
            String string2 = GameWindow.ReadString(byteBuffer);
            InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string2);
            if (inventoryItem == null) {
                return;
            }
            isoPlayer.setAttachedItem(string, inventoryItem);
        } else {
            isoPlayer.setAttachedItem(string, null);
        }
    }

    public void sendVisual(IsoPlayer isoPlayer) {
        if (isoPlayer == null || isoPlayer.OnlineID == -1) {
            return;
        }
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.HumanVisual.doPacket(byteBufferWriter);
        try {
            byteBufferWriter.putByte((byte)isoPlayer.PlayerIndex);
            isoPlayer.getHumanVisual().save(byteBufferWriter.bb);
            PacketTypes.PacketType.HumanVisual.send(connection);
        }
        catch (Throwable throwable) {
            connection.cancelPacket();
            ExceptionLogger.logException(throwable);
        }
    }

    static void receiveHumanVisual(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer == null || isoPlayer.isLocalPlayer()) {
            return;
        }
        try {
            isoPlayer.getHumanVisual().load(byteBuffer, 186);
            isoPlayer.resetModelNextFrame();
        }
        catch (Throwable throwable) {
            ExceptionLogger.logException(throwable);
        }
    }

    static void receiveBloodSplatter(ByteBuffer byteBuffer, short s) {
        int n;
        String string = GameWindow.ReadString(byteBuffer);
        float f = byteBuffer.getFloat();
        float f2 = byteBuffer.getFloat();
        float f3 = byteBuffer.getFloat();
        float f4 = byteBuffer.getFloat();
        float f5 = byteBuffer.getFloat();
        boolean bl = byteBuffer.get() == 1;
        boolean bl2 = byteBuffer.get() == 1;
        int n2 = byteBuffer.get();
        IsoCell isoCell = IsoWorld.instance.CurrentCell;
        IsoGridSquare isoGridSquare = isoCell.getGridSquare(f, f2, f3);
        if (isoGridSquare == null) {
            instance.delayPacket((int)f, (int)f2, (int)f3);
            return;
        }
        if (bl2 && SandboxOptions.instance.BloodLevel.getValue() > 1) {
            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    if (i == 0 && j == 0) continue;
                    new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoCell, f, f2, f3, (float)i * Rand.Next(0.25f, 0.5f), (float)j * Rand.Next(0.25f, 0.5f));
                }
            }
            new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, isoCell, f, f2, f3, f4 * 0.8f, f5 * 0.8f);
            return;
        }
        if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
            for (n = 0; n < n2; ++n) {
                isoGridSquare.splatBlood(3, 0.3f);
            }
            isoGridSquare.getChunk().addBloodSplat(f, f2, (int)f3, Rand.Next(20));
            new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoCell, f, f2, f3, f4 * 1.5f, f5 * 1.5f);
        }
        n = 3;
        int n3 = 0;
        int n4 = 1;
        switch (SandboxOptions.instance.BloodLevel.getValue()) {
            case 1: {
                n4 = 0;
                break;
            }
            case 2: {
                n4 = 1;
                n = 5;
                n3 = 2;
                break;
            }
            case 4: {
                n4 = 3;
                n = 2;
                break;
            }
            case 5: {
                n4 = 10;
                n = 0;
            }
        }
        for (int i = 0; i < n4; ++i) {
            if (Rand.Next(bl ? 8 : n) == 0) {
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoCell, f, f2, f3, f4 * 1.5f, f5 * 1.5f);
            }
            if (Rand.Next(bl ? 8 : n) == 0) {
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoCell, f, f2, f3, f4 * 1.5f, f5 * 1.5f);
            }
            if (Rand.Next(bl ? 8 : n) == 0) {
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoCell, f, f2, f3, f4 * 1.8f, f5 * 1.8f);
            }
            if (Rand.Next(bl ? 8 : n) == 0) {
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoCell, f, f2, f3, f4 * 1.9f, f5 * 1.9f);
            }
            if (Rand.Next(bl ? 4 : n3) == 0) {
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoCell, f, f2, f3, f4 * 3.5f, f5 * 3.5f);
            }
            if (Rand.Next(bl ? 4 : n3) == 0) {
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoCell, f, f2, f3, f4 * 3.8f, f5 * 3.8f);
            }
            if (Rand.Next(bl ? 4 : n3) == 0) {
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoCell, f, f2, f3, f4 * 3.9f, f5 * 3.9f);
            }
            if (Rand.Next(bl ? 4 : n3) == 0) {
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoCell, f, f2, f3, f4 * 1.5f, f5 * 1.5f);
            }
            if (Rand.Next(bl ? 4 : n3) == 0) {
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoCell, f, f2, f3, f4 * 3.8f, f5 * 3.8f);
            }
            if (Rand.Next(bl ? 4 : n3) == 0) {
                new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, isoCell, f, f2, f3, f4 * 3.9f, f5 * 3.9f);
            }
            if (Rand.Next(bl ? 9 : 6) != 0) continue;
            new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, isoCell, f, f2, f3, f4 * 0.8f, f5 * 0.8f);
        }
    }

    static void receiveZombieSound(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        byte by = byteBuffer.get();
        IsoZombie.ZombieSound zombieSound = IsoZombie.ZombieSound.fromIndex(by);
        DebugLog.log(DebugType.Sound, "sound: received " + by + " for zombie " + s2);
        IsoZombie isoZombie = (IsoZombie)IDToZombieMap.get(s2);
        if (isoZombie != null && isoZombie.getCurrentSquare() != null) {
            float f = zombieSound.radius();
            switch (zombieSound) {
                case Burned: {
                    String string = isoZombie.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
                    isoZombie.getEmitter().playVocals(string);
                    break;
                }
                case DeadCloseKilled: {
                    isoZombie.getEmitter().playSoundImpl("HeadStab", null);
                    String string = isoZombie.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
                    isoZombie.getEmitter().playVocals(string);
                    isoZombie.getEmitter().tick();
                    break;
                }
                case DeadNotCloseKilled: {
                    isoZombie.getEmitter().playSoundImpl("HeadSmash", null);
                    String string = isoZombie.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
                    isoZombie.getEmitter().playVocals(string);
                    isoZombie.getEmitter().tick();
                    break;
                }
                case Hurt: {
                    isoZombie.playHurtSound();
                    break;
                }
                case Idle: {
                    String string = isoZombie.isFemale() ? "FemaleZombieIdle" : "MaleZombieIdle";
                    isoZombie.getEmitter().playVocals(string);
                    break;
                }
                case Lunge: {
                    String string = isoZombie.isFemale() ? "FemaleZombieAttack" : "MaleZombieAttack";
                    isoZombie.getEmitter().playVocals(string);
                    break;
                }
                default: {
                    DebugLog.log("unhandled zombie sound " + zombieSound);
                }
            }
        }
    }

    static void receiveSlowFactor(ByteBuffer byteBuffer, short s) {
        byte by = byteBuffer.get();
        float f = byteBuffer.getFloat();
        float f2 = byteBuffer.getFloat();
        IsoPlayer isoPlayer = IsoPlayer.players[by];
        if (isoPlayer == null || isoPlayer.isDead()) {
            return;
        }
        isoPlayer.setSlowTimer(f);
        isoPlayer.setSlowFactor(f2);
        DebugLog.log(DebugType.Combat, "slowTimer=" + f + " slowFactor=" + f2);
    }

    public void sendCustomColor(IsoObject isoObject) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SendCustomColor.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(isoObject.getSquare().getX());
        byteBufferWriter.putInt(isoObject.getSquare().getY());
        byteBufferWriter.putInt(isoObject.getSquare().getZ());
        byteBufferWriter.putInt(isoObject.getSquare().getObjects().indexOf(isoObject));
        byteBufferWriter.putFloat(isoObject.getCustomColor().r);
        byteBufferWriter.putFloat(isoObject.getCustomColor().g);
        byteBufferWriter.putFloat(isoObject.getCustomColor().b);
        byteBufferWriter.putFloat(isoObject.getCustomColor().a);
        PacketTypes.PacketType.SendCustomColor.send(connection);
    }

    public void sendBandage(int n, int n2, boolean bl, float f, boolean bl2, String string) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Bandage.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)n);
        byteBufferWriter.putInt(n2);
        byteBufferWriter.putBoolean(bl);
        byteBufferWriter.putFloat(f);
        byteBufferWriter.putBoolean(bl2);
        GameWindow.WriteStringUTF(byteBufferWriter.bb, string);
        PacketTypes.PacketType.Bandage.send(connection);
    }

    public void sendStitch(int n, int n2, boolean bl, float f) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Stitch.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)n);
        byteBufferWriter.putInt(n2);
        byteBufferWriter.putBoolean(bl);
        byteBufferWriter.putFloat(f);
        PacketTypes.PacketType.Stitch.send(connection);
    }

    public void sendWoundInfection(int n, int n2, boolean bl) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.WoundInfection.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)n);
        byteBufferWriter.putInt(n2);
        byteBufferWriter.putBoolean(bl);
        PacketTypes.PacketType.WoundInfection.send(connection);
    }

    public void sendDisinfect(int n, int n2, float f) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Disinfect.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)n);
        byteBufferWriter.putInt(n2);
        byteBufferWriter.putFloat(f);
        PacketTypes.PacketType.Disinfect.send(connection);
    }

    public void sendSplint(int n, int n2, boolean bl, float f, String string) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Splint.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)n);
        byteBufferWriter.putInt(n2);
        byteBufferWriter.putBoolean(bl);
        if (bl) {
            if (string == null) {
                string = "";
            }
            byteBufferWriter.putUTF(string);
            byteBufferWriter.putFloat(f);
        }
        PacketTypes.PacketType.Splint.send(connection);
    }

    public void sendAdditionalPain(int n, int n2, float f) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.AdditionalPain.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)n);
        byteBufferWriter.putInt(n2);
        byteBufferWriter.putFloat(f);
        PacketTypes.PacketType.AdditionalPain.send(connection);
    }

    public void sendRemoveGlass(int n, int n2) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RemoveGlass.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)n);
        byteBufferWriter.putInt(n2);
        PacketTypes.PacketType.RemoveGlass.send(connection);
    }

    public void sendRemoveBullet(int n, int n2, int n3) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RemoveBullet.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((byte)n);
        byteBufferWriter.putInt(n2);
        byteBufferWriter.putInt(n3);
        PacketTypes.PacketType.RemoveBullet.send(connection);
    }

    public void sendCleanBurn(int n, int n2) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.CleanBurn.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((byte)n);
        byteBufferWriter.putInt(n2);
        PacketTypes.PacketType.CleanBurn.send(connection);
    }

    public void eatFood(IsoPlayer isoPlayer, Food food, float f) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.EatFood.doPacket(byteBufferWriter);
        try {
            byteBufferWriter.putByte((byte)isoPlayer.PlayerIndex);
            byteBufferWriter.putFloat(f);
            food.saveWithSize(byteBufferWriter.bb, false);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        PacketTypes.PacketType.EatFood.send(connection);
    }

    public void drink(IsoPlayer isoPlayer, float f) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Drink.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)isoPlayer.PlayerIndex);
        byteBufferWriter.putFloat(f);
        PacketTypes.PacketType.Drink.send(connection);
    }

    public void addToItemSendBuffer(IsoObject isoObject, ItemContainer itemContainer, InventoryItem inventoryItem) {
        ArrayList<InventoryItem> arrayList;
        if (isoObject instanceof IsoWorldInventoryObject) {
            arrayList = ((IsoWorldInventoryObject)isoObject).getItem();
            if (inventoryItem == null || arrayList == null || !(arrayList instanceof InventoryContainer) || itemContainer != ((InventoryContainer)((Object)arrayList)).getInventory()) {
                DebugLog.log("ERROR: addToItemSendBuffer parent=" + isoObject + " item=" + inventoryItem);
                if (Core.bDebug) {
                    throw new IllegalStateException();
                }
                return;
            }
        } else if (isoObject instanceof BaseVehicle) {
            if (itemContainer.vehiclePart == null || itemContainer.vehiclePart.getItemContainer() != itemContainer || itemContainer.vehiclePart.getVehicle() != isoObject) {
                DebugLog.log("ERROR: addToItemSendBuffer parent=" + isoObject + " item=" + inventoryItem);
                if (Core.bDebug) {
                    throw new IllegalStateException();
                }
                return;
            }
        } else if (isoObject == null || inventoryItem == null || isoObject.getContainerIndex(itemContainer) == -1) {
            DebugLog.log("ERROR: addToItemSendBuffer parent=" + isoObject + " item=" + inventoryItem);
            if (Core.bDebug) {
                throw new IllegalStateException();
            }
            return;
        }
        if (this.itemsToSendRemove.containsKey(itemContainer) && (arrayList = this.itemsToSendRemove.get(itemContainer)).remove(inventoryItem)) {
            if (arrayList.isEmpty()) {
                this.itemsToSendRemove.remove(itemContainer);
            }
            return;
        }
        if (this.itemsToSend.containsKey(itemContainer)) {
            this.itemsToSend.get(itemContainer).add(inventoryItem);
        } else {
            arrayList = new ArrayList<InventoryItem>();
            this.itemsToSend.put(itemContainer, arrayList);
            arrayList.add(inventoryItem);
        }
    }

    public void addToItemRemoveSendBuffer(IsoObject isoObject, ItemContainer itemContainer, InventoryItem inventoryItem) {
        Serializable serializable;
        if (isoObject instanceof IsoWorldInventoryObject) {
            serializable = ((IsoWorldInventoryObject)isoObject).getItem();
            if (inventoryItem == null || serializable == null || !(serializable instanceof InventoryContainer) || itemContainer != ((InventoryContainer)((Object)serializable)).getInventory()) {
                DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + isoObject + " item=" + inventoryItem);
                if (Core.bDebug) {
                    throw new IllegalStateException();
                }
                return;
            }
        } else if (isoObject instanceof BaseVehicle) {
            if (itemContainer.vehiclePart == null || itemContainer.vehiclePart.getItemContainer() != itemContainer || itemContainer.vehiclePart.getVehicle() != isoObject) {
                DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + isoObject + " item=" + inventoryItem);
                if (Core.bDebug) {
                    throw new IllegalStateException();
                }
                return;
            }
        } else if (isoObject instanceof IsoDeadBody) {
            if (inventoryItem == null || itemContainer != isoObject.getContainer()) {
                DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + isoObject + " item=" + inventoryItem);
                if (Core.bDebug) {
                    throw new IllegalStateException();
                }
                return;
            }
        } else if (isoObject == null || inventoryItem == null || isoObject.getContainerIndex(itemContainer) == -1) {
            DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + isoObject + " item=" + inventoryItem);
            if (Core.bDebug) {
                throw new IllegalStateException();
            }
            return;
        }
        if (!SystemDisabler.doWorldSyncEnable) {
            if (this.itemsToSend.containsKey(itemContainer) && ((ArrayList)(serializable = this.itemsToSend.get(itemContainer))).remove(inventoryItem)) {
                if (((ArrayList)serializable).isEmpty()) {
                    this.itemsToSend.remove(itemContainer);
                }
                return;
            }
            if (this.itemsToSendRemove.containsKey(itemContainer)) {
                this.itemsToSendRemove.get(itemContainer).add(inventoryItem);
            } else {
                serializable = new ArrayList();
                ((ArrayList)serializable).add(inventoryItem);
                this.itemsToSendRemove.put(itemContainer, (ArrayList<InventoryItem>)serializable);
            }
            return;
        }
        serializable = itemContainer.getParent();
        if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
            serializable = itemContainer.getContainingItem().getWorldItem();
        }
        UdpConnection udpConnection = connection;
        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.RemoveInventoryItemFromContainer.doPacket(byteBufferWriter);
        if (serializable instanceof IsoDeadBody) {
            byteBufferWriter.putShort((short)0);
            byteBufferWriter.putInt(((IsoObject)serializable).square.getX());
            byteBufferWriter.putInt(((IsoObject)serializable).square.getY());
            byteBufferWriter.putInt(((IsoObject)serializable).square.getZ());
            byteBufferWriter.putByte((byte)((IsoObject)serializable).getStaticMovingObjectIndex());
            byteBufferWriter.putInt(1);
            byteBufferWriter.putInt(inventoryItem.id);
        } else if (serializable instanceof IsoWorldInventoryObject) {
            byteBufferWriter.putShort((short)1);
            byteBufferWriter.putInt(((IsoObject)serializable).square.getX());
            byteBufferWriter.putInt(((IsoObject)serializable).square.getY());
            byteBufferWriter.putInt(((IsoObject)serializable).square.getZ());
            byteBufferWriter.putInt(((IsoWorldInventoryObject)serializable).getItem().id);
            byteBufferWriter.putInt(1);
            byteBufferWriter.putInt(inventoryItem.id);
        } else if (serializable instanceof BaseVehicle) {
            byteBufferWriter.putShort((short)3);
            byteBufferWriter.putInt(((IsoObject)serializable).square.getX());
            byteBufferWriter.putInt(((IsoObject)serializable).square.getY());
            byteBufferWriter.putInt(((IsoObject)serializable).square.getZ());
            byteBufferWriter.putShort(((BaseVehicle)serializable).VehicleID);
            byteBufferWriter.putByte((byte)itemContainer.vehiclePart.getIndex());
            byteBufferWriter.putInt(1);
            byteBufferWriter.putInt(inventoryItem.id);
        } else {
            byteBufferWriter.putShort((short)2);
            byteBufferWriter.putInt(((IsoObject)serializable).square.getX());
            byteBufferWriter.putInt(((IsoObject)serializable).square.getY());
            byteBufferWriter.putInt(((IsoObject)serializable).square.getZ());
            byteBufferWriter.putByte((byte)((IsoObject)serializable).square.getObjects().indexOf(serializable));
            byteBufferWriter.putByte((byte)((IsoObject)serializable).getContainerIndex(itemContainer));
            byteBufferWriter.putInt(1);
            byteBufferWriter.putInt(inventoryItem.id);
        }
        PacketTypes.PacketType.RemoveInventoryItemFromContainer.send(udpConnection);
    }

    public void sendAddedRemovedItems(boolean bl) {
        ByteBufferWriter byteBufferWriter;
        IsoObject isoObject;
        ArrayList<InventoryItem> arrayList;
        ItemContainer itemContainer;
        boolean bl2;
        boolean bl3 = bl2 = bl || this.itemSendFrequency.Check();
        if (!SystemDisabler.doWorldSyncEnable && !this.itemsToSendRemove.isEmpty() && bl2) {
            for (Map.Entry<ItemContainer, ArrayList<InventoryItem>> entry : this.itemsToSendRemove.entrySet()) {
                int n;
                itemContainer = entry.getKey();
                arrayList = entry.getValue();
                isoObject = itemContainer.getParent();
                if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
                    isoObject = itemContainer.getContainingItem().getWorldItem();
                }
                if (isoObject.square == null) continue;
                try {
                    byteBufferWriter = connection.startPacket();
                    PacketTypes.PacketType.RemoveInventoryItemFromContainer.doPacket(byteBufferWriter);
                    if (isoObject instanceof IsoDeadBody) {
                        byteBufferWriter.putShort((short)0);
                        byteBufferWriter.putInt(isoObject.square.getX());
                        byteBufferWriter.putInt(isoObject.square.getY());
                        byteBufferWriter.putInt(isoObject.square.getZ());
                        byteBufferWriter.putByte((byte)isoObject.getStaticMovingObjectIndex());
                    } else if (isoObject instanceof IsoWorldInventoryObject) {
                        byteBufferWriter.putShort((short)1);
                        byteBufferWriter.putInt(isoObject.square.getX());
                        byteBufferWriter.putInt(isoObject.square.getY());
                        byteBufferWriter.putInt(isoObject.square.getZ());
                        byteBufferWriter.putInt(((IsoWorldInventoryObject)isoObject).getItem().id);
                    } else if (isoObject instanceof BaseVehicle) {
                        byteBufferWriter.putShort((short)3);
                        byteBufferWriter.putInt(isoObject.square.getX());
                        byteBufferWriter.putInt(isoObject.square.getY());
                        byteBufferWriter.putInt(isoObject.square.getZ());
                        byteBufferWriter.putShort(((BaseVehicle)isoObject).VehicleID);
                        byteBufferWriter.putByte((byte)itemContainer.vehiclePart.getIndex());
                    } else {
                        byteBufferWriter.putShort((short)2);
                        byteBufferWriter.putInt(isoObject.square.getX());
                        byteBufferWriter.putInt(isoObject.square.getY());
                        byteBufferWriter.putInt(isoObject.square.getZ());
                        byteBufferWriter.putByte((byte)isoObject.square.getObjects().indexOf(isoObject));
                        byteBufferWriter.putByte((byte)isoObject.getContainerIndex(itemContainer));
                    }
                    byteBufferWriter.putInt(arrayList.size());
                    for (n = 0; n < arrayList.size(); ++n) {
                        InventoryItem inventoryItem = arrayList.get(n);
                        byteBufferWriter.putInt(inventoryItem.id);
                    }
                    PacketTypes.PacketType.RemoveInventoryItemFromContainer.send(connection);
                }
                catch (Exception exception) {
                    DebugLog.log("sendAddedRemovedItems: itemsToSendRemove container:" + itemContainer + "." + isoObject + " items:" + arrayList);
                    if (arrayList != null) {
                        for (n = 0; n < arrayList.size(); ++n) {
                            if (arrayList.get(n) == null) {
                                DebugLog.log("item:null");
                                continue;
                            }
                            DebugLog.log("item:" + arrayList.get(n).getName());
                        }
                        DebugLog.log("itemSize:" + arrayList.size());
                    }
                    exception.printStackTrace();
                    connection.cancelPacket();
                }
            }
            this.itemsToSendRemove.clear();
        }
        if (!this.itemsToSend.isEmpty() && bl2) {
            for (Map.Entry<ItemContainer, ArrayList<InventoryItem>> entry : this.itemsToSend.entrySet()) {
                itemContainer = entry.getKey();
                arrayList = entry.getValue();
                isoObject = itemContainer.getParent();
                if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
                    isoObject = itemContainer.getContainingItem().getWorldItem();
                }
                if (isoObject.square == null) continue;
                try {
                    byteBufferWriter = connection.startPacket();
                    PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(byteBufferWriter);
                    if (isoObject instanceof IsoDeadBody) {
                        byteBufferWriter.putShort((short)0);
                        byteBufferWriter.putInt(isoObject.square.getX());
                        byteBufferWriter.putInt(isoObject.square.getY());
                        byteBufferWriter.putInt(isoObject.square.getZ());
                        byteBufferWriter.putByte((byte)isoObject.getStaticMovingObjectIndex());
                        try {
                            CompressIdenticalItems.save(byteBufferWriter.bb, arrayList, null);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    } else if (isoObject instanceof IsoWorldInventoryObject) {
                        byteBufferWriter.putShort((short)1);
                        byteBufferWriter.putInt(isoObject.square.getX());
                        byteBufferWriter.putInt(isoObject.square.getY());
                        byteBufferWriter.putInt(isoObject.square.getZ());
                        byteBufferWriter.putInt(((IsoWorldInventoryObject)isoObject).getItem().id);
                        try {
                            CompressIdenticalItems.save(byteBufferWriter.bb, arrayList, null);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    } else if (isoObject instanceof BaseVehicle) {
                        byteBufferWriter.putShort((short)3);
                        byteBufferWriter.putInt(isoObject.square.getX());
                        byteBufferWriter.putInt(isoObject.square.getY());
                        byteBufferWriter.putInt(isoObject.square.getZ());
                        byteBufferWriter.putShort(((BaseVehicle)isoObject).VehicleID);
                        byteBufferWriter.putByte((byte)itemContainer.vehiclePart.getIndex());
                        try {
                            CompressIdenticalItems.save(byteBufferWriter.bb, arrayList, null);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    } else {
                        byteBufferWriter.putShort((short)2);
                        byteBufferWriter.putInt(isoObject.square.getX());
                        byteBufferWriter.putInt(isoObject.square.getY());
                        byteBufferWriter.putInt(isoObject.square.getZ());
                        byteBufferWriter.putByte((byte)isoObject.square.getObjects().indexOf(isoObject));
                        byteBufferWriter.putByte((byte)isoObject.getContainerIndex(itemContainer));
                        try {
                            CompressIdenticalItems.save(byteBufferWriter.bb, arrayList, null);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                    PacketTypes.PacketType.AddInventoryItemToContainer.send(connection);
                }
                catch (Exception exception) {
                    DebugLog.log("sendAddedRemovedItems: itemsToSend container:" + itemContainer + "." + isoObject + " items:" + arrayList);
                    if (arrayList != null) {
                        for (int i = 0; i < arrayList.size(); ++i) {
                            if (arrayList.get(i) == null) {
                                DebugLog.log("item:null");
                                continue;
                            }
                            DebugLog.log("item:" + arrayList.get(i).getName());
                        }
                        DebugLog.log("itemSize:" + arrayList.size());
                    }
                    exception.printStackTrace();
                    connection.cancelPacket();
                }
            }
            this.itemsToSend.clear();
        }
    }

    public void checkAddedRemovedItems(IsoObject isoObject) {
        if (isoObject == null) {
            return;
        }
        if (this.itemsToSend.isEmpty() && this.itemsToSendRemove.isEmpty()) {
            return;
        }
        if (isoObject instanceof IsoDeadBody) {
            if (this.itemsToSend.containsKey(isoObject.getContainer()) || this.itemsToSendRemove.containsKey(isoObject.getContainer())) {
                this.sendAddedRemovedItems(true);
            }
            return;
        }
        if (isoObject instanceof IsoWorldInventoryObject) {
            ItemContainer itemContainer;
            InventoryItem inventoryItem = ((IsoWorldInventoryObject)isoObject).getItem();
            if (inventoryItem instanceof InventoryContainer && (this.itemsToSend.containsKey(itemContainer = ((InventoryContainer)inventoryItem).getInventory()) || this.itemsToSendRemove.containsKey(itemContainer))) {
                this.sendAddedRemovedItems(true);
            }
            return;
        }
        if (isoObject instanceof BaseVehicle) {
            return;
        }
        for (int i = 0; i < isoObject.getContainerCount(); ++i) {
            ItemContainer itemContainer = isoObject.getContainerByIndex(i);
            if (!this.itemsToSend.containsKey(itemContainer) && !this.itemsToSendRemove.containsKey(itemContainer)) continue;
            this.sendAddedRemovedItems(true);
            return;
        }
    }

    private void writeItemStats(ByteBufferWriter byteBufferWriter, InventoryItem inventoryItem) {
        byteBufferWriter.putInt(inventoryItem.id);
        byteBufferWriter.putInt(inventoryItem.getUses());
        byteBufferWriter.putFloat(inventoryItem instanceof DrainableComboItem ? ((DrainableComboItem)inventoryItem).getUsedDelta() : 0.0f);
        if (inventoryItem instanceof Food) {
            Food food = (Food)inventoryItem;
            byteBufferWriter.putBoolean(true);
            byteBufferWriter.putFloat(food.getHungChange());
            byteBufferWriter.putFloat(food.getCalories());
            byteBufferWriter.putFloat(food.getCarbohydrates());
            byteBufferWriter.putFloat(food.getLipids());
            byteBufferWriter.putFloat(food.getProteins());
            byteBufferWriter.putFloat(food.getThirstChange());
            byteBufferWriter.putInt(food.getFluReduction());
            byteBufferWriter.putFloat(food.getPainReduction());
            byteBufferWriter.putFloat(food.getEndChange());
            byteBufferWriter.putInt(food.getReduceFoodSickness());
            byteBufferWriter.putFloat(food.getStressChange());
            byteBufferWriter.putFloat(food.getFatigueChange());
        } else {
            byteBufferWriter.putBoolean(false);
        }
    }

    public void sendItemStats(InventoryItem inventoryItem) {
        Object object;
        IsoObject isoObject;
        if (inventoryItem == null) {
            return;
        }
        if (inventoryItem.getWorldItem() != null && inventoryItem.getWorldItem().getWorldObjectIndex() != -1) {
            IsoWorldInventoryObject isoWorldInventoryObject = inventoryItem.getWorldItem();
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.ItemStats.doPacket(byteBufferWriter);
            byteBufferWriter.putShort((short)1);
            byteBufferWriter.putInt(isoWorldInventoryObject.square.getX());
            byteBufferWriter.putInt(isoWorldInventoryObject.square.getY());
            byteBufferWriter.putInt(isoWorldInventoryObject.square.getZ());
            this.writeItemStats(byteBufferWriter, inventoryItem);
            PacketTypes.PacketType.ItemStats.send(connection);
            return;
        }
        if (inventoryItem.getContainer() == null) {
            DebugLog.log("ERROR: sendItemStats(): item is neither in a container nor on the ground");
            if (Core.bDebug) {
                throw new IllegalStateException();
            }
            return;
        }
        ItemContainer itemContainer = inventoryItem.getContainer();
        IsoObject isoObject2 = itemContainer.getParent();
        if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
            isoObject2 = itemContainer.getContainingItem().getWorldItem();
        }
        if ((isoObject = isoObject2) instanceof IsoWorldInventoryObject) {
            object = ((IsoWorldInventoryObject)isoObject).getItem();
            if (!(object instanceof InventoryContainer) || itemContainer != ((InventoryContainer)object).getInventory()) {
                DebugLog.log("ERROR: sendItemStats() parent=" + isoObject + " item=" + inventoryItem);
                if (Core.bDebug) {
                    throw new IllegalStateException();
                }
                return;
            }
        } else if (isoObject instanceof BaseVehicle) {
            if (itemContainer.vehiclePart == null || itemContainer.vehiclePart.getItemContainer() != itemContainer || itemContainer.vehiclePart.getVehicle() != isoObject) {
                DebugLog.log("ERROR: sendItemStats() parent=" + isoObject + " item=" + inventoryItem);
                if (Core.bDebug) {
                    throw new IllegalStateException();
                }
                return;
            }
        } else if (isoObject instanceof IsoDeadBody) {
            if (itemContainer != isoObject.getContainer()) {
                DebugLog.log("ERROR: sendItemStats() parent=" + isoObject + " item=" + inventoryItem);
                if (Core.bDebug) {
                    throw new IllegalStateException();
                }
                return;
            }
        } else if (isoObject == null || isoObject.getContainerIndex(itemContainer) == -1) {
            DebugLog.log("ERROR: sendItemStats() parent=" + isoObject + " item=" + inventoryItem);
            if (Core.bDebug) {
                throw new IllegalStateException();
            }
            return;
        }
        object = connection.startPacket();
        PacketTypes.PacketType.ItemStats.doPacket((ByteBufferWriter)object);
        if (isoObject2 instanceof IsoDeadBody) {
            ((ByteBufferWriter)object).putShort((short)0);
            ((ByteBufferWriter)object).putInt(isoObject2.square.getX());
            ((ByteBufferWriter)object).putInt(isoObject2.square.getY());
            ((ByteBufferWriter)object).putInt(isoObject2.square.getZ());
            ((ByteBufferWriter)object).putByte((byte)isoObject2.getStaticMovingObjectIndex());
        } else if (isoObject2 instanceof IsoWorldInventoryObject) {
            ((ByteBufferWriter)object).putShort((short)1);
            ((ByteBufferWriter)object).putInt(isoObject2.square.getX());
            ((ByteBufferWriter)object).putInt(isoObject2.square.getY());
            ((ByteBufferWriter)object).putInt(isoObject2.square.getZ());
        } else if (isoObject2 instanceof BaseVehicle) {
            ((ByteBufferWriter)object).putShort((short)3);
            ((ByteBufferWriter)object).putInt(isoObject2.square.getX());
            ((ByteBufferWriter)object).putInt(isoObject2.square.getY());
            ((ByteBufferWriter)object).putInt(isoObject2.square.getZ());
            ((ByteBufferWriter)object).putShort(((BaseVehicle)isoObject2).VehicleID);
            ((ByteBufferWriter)object).putByte((byte)itemContainer.vehiclePart.getIndex());
        } else {
            ((ByteBufferWriter)object).putShort((short)2);
            ((ByteBufferWriter)object).putInt(isoObject2.square.getX());
            ((ByteBufferWriter)object).putInt(isoObject2.square.getY());
            ((ByteBufferWriter)object).putInt(isoObject2.square.getZ());
            ((ByteBufferWriter)object).putByte((byte)isoObject2.getObjectIndex());
            ((ByteBufferWriter)object).putByte((byte)isoObject2.getContainerIndex(itemContainer));
        }
        this.writeItemStats((ByteBufferWriter)object, inventoryItem);
        PacketTypes.PacketType.ItemStats.send(connection);
    }

    public void PlayWorldSound(String string, int n, int n2, byte by) {
        PlayWorldSoundPacket playWorldSoundPacket = new PlayWorldSoundPacket();
        playWorldSoundPacket.set(string, n, n2, by);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlayWorldSound.doPacket(byteBufferWriter);
        playWorldSoundPacket.write(byteBufferWriter);
        PacketTypes.PacketType.PlayWorldSound.send(connection);
    }

    public void PlaySound(String string, boolean bl, IsoMovingObject isoMovingObject) {
        PlaySoundPacket playSoundPacket = new PlaySoundPacket();
        playSoundPacket.set(string, bl, isoMovingObject);
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlaySound.doPacket(byteBufferWriter);
        playSoundPacket.write(byteBufferWriter);
        PacketTypes.PacketType.PlaySound.send(connection);
    }

    public void StopSound(IsoMovingObject isoMovingObject, String string, boolean bl) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.StopSound.doPacket(byteBufferWriter);
        StopSoundPacket stopSoundPacket = new StopSoundPacket();
        stopSoundPacket.set(isoMovingObject, string, bl);
        stopSoundPacket.write(byteBufferWriter);
        PacketTypes.PacketType.StopSound.send(connection);
    }

    public void startLocalServer() throws Exception {
        bClient = true;
        ip = "127.0.0.1";
        Thread thread = new Thread(ThreadGroups.Workers, () -> {
            String string = System.getProperty("file.separator");
            String string2 = System.getProperty("java.class.path");
            String string3 = System.getProperty("java.home") + string + "bin" + string + "java";
            ProcessBuilder processBuilder = new ProcessBuilder(string3, "-Xms2048m", "-Xmx2048m", "-Djava.library.path=../natives/", "-cp", "lwjgl.jar;lwjgl_util.jar;sqlitejdbc-v056.jar;../bin/", "zombie.network.GameServer");
            processBuilder.redirectErrorStream(true);
            Process process = null;
            try {
                process = processBuilder.start();
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            boolean bl = false;
            try {
                while (!((Reader)inputStreamReader).ready()) {
                    try {
                        int n;
                        while ((n = ((Reader)inputStreamReader).read()) != -1) {
                            System.out.print((char)n);
                        }
                    }
                    catch (IOException iOException) {
                        iOException.printStackTrace();
                    }
                    try {
                        ((Reader)inputStreamReader).close();
                    }
                    catch (IOException iOException) {
                        iOException.printStackTrace();
                    }
                }
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        });
        thread.setUncaughtExceptionHandler(GameWindow::uncaughtException);
        thread.start();
    }

    public static void sendPing() {
        if (bClient) {
            ByteBufferWriter byteBufferWriter = connection.startPingPacket();
            PacketTypes.doPingPacket(byteBufferWriter);
            byteBufferWriter.putLong(System.currentTimeMillis());
            byteBufferWriter.putLong(0L);
            connection.endPingPacket();
        }
    }

    public static void registerZone(IsoMetaGrid.Zone zone, boolean bl) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RegisterZone.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(zone.name);
        byteBufferWriter.putUTF(zone.type);
        byteBufferWriter.putInt(zone.x);
        byteBufferWriter.putInt(zone.y);
        byteBufferWriter.putInt(zone.z);
        byteBufferWriter.putInt(zone.w);
        byteBufferWriter.putInt(zone.h);
        byteBufferWriter.putInt(zone.getLastActionTimestamp());
        byteBufferWriter.putBoolean(bl);
        PacketTypes.PacketType.SyncAlarmClock.send(connection);
    }

    static void receiveHelicopter(ByteBuffer byteBuffer, short s) {
        boolean bl;
        float f = byteBuffer.getFloat();
        float f2 = byteBuffer.getFloat();
        boolean bl2 = bl = byteBuffer.get() == 1;
        if (IsoWorld.instance != null && IsoWorld.instance.helicopter != null) {
            IsoWorld.instance.helicopter.clientSync(f, f2, bl);
        }
    }

    static void receiveVehicles(ByteBuffer byteBuffer, short s) {
        VehicleManager.instance.clientPacket(byteBuffer);
    }

    static void receiveVehiclesLoading(ByteBuffer byteBuffer, short s) throws IOException {
        VehicleManager.loadingClientPacket(byteBuffer);
        throw new IOException();
    }

    static void receiveTimeSync(ByteBuffer byteBuffer, short s) {
        GameTime.receiveTimeSync(byteBuffer, connection);
    }

    public static void sendSafehouse(SafeHouse safeHouse, boolean bl) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncSafehouse.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(safeHouse.getX());
        byteBufferWriter.putInt(safeHouse.getY());
        byteBufferWriter.putInt(safeHouse.getW());
        byteBufferWriter.putInt(safeHouse.getH());
        byteBufferWriter.putUTF(safeHouse.getOwner());
        byteBufferWriter.putInt(safeHouse.getPlayers().size());
        for (String string : safeHouse.getPlayers()) {
            byteBufferWriter.putUTF(string);
        }
        byteBufferWriter.putInt(safeHouse.playersRespawn.size());
        for (String string : safeHouse.playersRespawn) {
            byteBufferWriter.putUTF(string);
        }
        byteBufferWriter.putBoolean(bl);
        byteBufferWriter.putUTF(safeHouse.getTitle());
        PacketTypes.PacketType.SyncSafehouse.send(connection);
    }

    static void receiveSyncSafehouse(ByteBuffer byteBuffer, short s) {
        int n;
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        int n4 = byteBuffer.getInt();
        int n5 = byteBuffer.getInt();
        String string = GameWindow.ReadString(byteBuffer);
        int n6 = byteBuffer.getInt();
        SafeHouse safeHouse = SafeHouse.getSafeHouse(n2, n3, n4, n5);
        if (safeHouse == null) {
            safeHouse = SafeHouse.addSafeHouse(n2, n3, n4, n5, string, true);
        }
        if (safeHouse == null) {
            return;
        }
        safeHouse.getPlayers().clear();
        for (n = 0; n < n6; ++n) {
            safeHouse.getPlayers().add(GameWindow.ReadString(byteBuffer));
        }
        n = byteBuffer.getInt();
        safeHouse.playersRespawn.clear();
        for (int i = 0; i < n; ++i) {
            safeHouse.playersRespawn.add(GameWindow.ReadString(byteBuffer));
        }
        if (byteBuffer.get() == 1) {
            SafeHouse.getSafehouseList().remove(safeHouse);
        }
        safeHouse.setTitle(GameWindow.ReadString(byteBuffer));
        safeHouse.setOwner(string);
        LuaEventManager.triggerEvent("OnSafehousesChanged");
    }

    public IsoPlayer getPlayerFromUsername(String string) {
        ArrayList<IsoPlayer> arrayList = this.getPlayers();
        for (int i = 0; i < arrayList.size(); ++i) {
            IsoPlayer isoPlayer = arrayList.get(i);
            if (!isoPlayer.getUsername().equals(string)) continue;
            return isoPlayer;
        }
        return null;
    }

    public static void destroy(IsoObject isoObject) {
        if (ServerOptions.instance.AllowDestructionBySledgehammer.getValue()) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.SledgehammerDestroy.doPacket(byteBufferWriter);
            IsoGridSquare isoGridSquare = isoObject.getSquare();
            byteBufferWriter.putInt(isoGridSquare.getX());
            byteBufferWriter.putInt(isoGridSquare.getY());
            byteBufferWriter.putInt(isoGridSquare.getZ());
            byteBufferWriter.putInt(isoGridSquare.getObjects().indexOf(isoObject));
            PacketTypes.PacketType.SledgehammerDestroy.send(connection);
            isoGridSquare.RemoveTileObject(isoObject);
        }
    }

    public static void sendTeleport(IsoPlayer isoPlayer, float f, float f2, float f3) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Teleport.doPacket(byteBufferWriter);
        GameWindow.WriteString(byteBufferWriter.bb, isoPlayer.getUsername());
        byteBufferWriter.putFloat(f);
        byteBufferWriter.putFloat(f2);
        byteBufferWriter.putFloat(f3);
        PacketTypes.PacketType.Teleport.send(connection);
    }

    public static void sendStopFire(IsoGridSquare isoGridSquare) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.StopFire.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)0);
        byteBufferWriter.putInt(isoGridSquare.getX());
        byteBufferWriter.putInt(isoGridSquare.getY());
        byteBufferWriter.putInt(isoGridSquare.getZ());
        PacketTypes.PacketType.StopFire.send(connection);
    }

    public static void sendStopFire(IsoGameCharacter isoGameCharacter) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.StopFire.doPacket(byteBufferWriter);
        if (isoGameCharacter instanceof IsoPlayer) {
            byteBufferWriter.putByte((byte)1);
            byteBufferWriter.putShort(isoGameCharacter.getOnlineID());
        }
        if (isoGameCharacter instanceof IsoZombie) {
            byteBufferWriter.putByte((byte)2);
            byteBufferWriter.putShort(((IsoZombie)isoGameCharacter).OnlineID);
        }
        PacketTypes.PacketType.StopFire.send(connection);
    }

    public void sendCataplasm(int n, int n2, float f, float f2, float f3) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Cataplasm.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)n);
        byteBufferWriter.putInt(n2);
        byteBufferWriter.putFloat(f);
        byteBufferWriter.putFloat(f2);
        byteBufferWriter.putFloat(f3);
        PacketTypes.PacketType.Cataplasm.send(connection);
    }

    static void receiveBodyDamageUpdate(ByteBuffer byteBuffer, short s) {
        BodyDamageSync.instance.clientPacket(byteBuffer);
    }

    static void receivePacketTypeShort(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        switch (s2) {
            case 1000: {
                GameClient.receiveWaveSignal(byteBuffer);
                break;
            }
            case 1002: {
                GameClient.receiveRadioServerData(byteBuffer);
                break;
            }
            case 1004: {
                GameClient.receiveRadioDeviceDataState(byteBuffer);
                break;
            }
            case 1200: {
                GameClient.SyncCustomLightSwitchSettings(byteBuffer);
            }
        }
    }

    public static void receiveRadioDeviceDataState(ByteBuffer byteBuffer) {
        byte by = byteBuffer.get();
        if (by == 1) {
            DeviceData deviceData;
            IsoObject isoObject;
            int n = byteBuffer.getInt();
            int n2 = byteBuffer.getInt();
            int n3 = byteBuffer.getInt();
            int n4 = byteBuffer.getInt();
            IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n, n2, n3);
            if (isoGridSquare != null && n4 >= 0 && n4 < isoGridSquare.getObjects().size() && (isoObject = isoGridSquare.getObjects().get(n4)) instanceof IsoWaveSignal && (deviceData = ((IsoWaveSignal)isoObject).getDeviceData()) != null) {
                try {
                    deviceData.receiveDeviceDataStatePacket(byteBuffer, null);
                }
                catch (Exception exception) {
                    System.out.print(exception.getMessage());
                }
            }
        } else if (by == 0) {
            short s = byteBuffer.getShort();
            IsoPlayer isoPlayer = IDToPlayerMap.get(s);
            byte by2 = byteBuffer.get();
            if (isoPlayer != null) {
                Radio radio = null;
                if (by2 == 1 && isoPlayer.getPrimaryHandItem() instanceof Radio) {
                    radio = (Radio)isoPlayer.getPrimaryHandItem();
                }
                if (by2 == 2 && isoPlayer.getSecondaryHandItem() instanceof Radio) {
                    radio = (Radio)isoPlayer.getSecondaryHandItem();
                }
                if (radio != null && radio.getDeviceData() != null) {
                    try {
                        radio.getDeviceData().receiveDeviceDataStatePacket(byteBuffer, connection);
                    }
                    catch (Exception exception) {
                        System.out.print(exception.getMessage());
                    }
                }
            }
        } else if (by == 2) {
            DeviceData deviceData;
            VehiclePart vehiclePart;
            short s = byteBuffer.getShort();
            short s2 = byteBuffer.getShort();
            BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(s);
            if (baseVehicle != null && (vehiclePart = baseVehicle.getPartByIndex(s2)) != null && (deviceData = vehiclePart.getDeviceData()) != null) {
                try {
                    deviceData.receiveDeviceDataStatePacket(byteBuffer, null);
                }
                catch (Exception exception) {
                    System.out.print(exception.getMessage());
                }
            }
        }
    }

    public static void sendRadioServerDataRequest() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypesShort.doPacket((short)1002, byteBufferWriter);
        PacketTypes.PacketType.PacketTypeShort.send(connection);
    }

    private static void receiveRadioServerData(ByteBuffer byteBuffer) {
        ZomboidRadio zomboidRadio = ZomboidRadio.getInstance();
        int n = byteBuffer.getInt();
        for (int i = 0; i < n; ++i) {
            String string = GameWindow.ReadString(byteBuffer);
            int n2 = byteBuffer.getInt();
            for (int j = 0; j < n2; ++j) {
                int n3 = byteBuffer.getInt();
                String string2 = GameWindow.ReadString(byteBuffer);
                zomboidRadio.addChannelName(string2, n3, string);
            }
        }
        zomboidRadio.setHasRecievedServerData(true);
    }

    public static void sendIsoWaveSignal(int n, int n2, int n3, String string, String string2, float f, float f2, float f3, int n4, boolean bl) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypesShort.doPacket((short)1000, byteBufferWriter);
        byteBufferWriter.putInt(n);
        byteBufferWriter.putInt(n2);
        byteBufferWriter.putInt(n3);
        byteBufferWriter.putBoolean(string != null);
        if (string != null) {
            GameWindow.WriteString(byteBufferWriter.bb, string);
        }
        byteBufferWriter.putByte(string2 != null ? (byte)1 : 0);
        if (string2 != null) {
            byteBufferWriter.putUTF(string2);
        }
        byteBufferWriter.putFloat(f);
        byteBufferWriter.putFloat(f2);
        byteBufferWriter.putFloat(f3);
        byteBufferWriter.putInt(n4);
        byteBufferWriter.putByte(bl ? (byte)1 : 0);
        PacketTypes.PacketType.PacketTypeShort.send(connection);
    }

    private static void receiveWaveSignal(ByteBuffer byteBuffer) {
        if (!ChatManager.getInstance().isWorking()) {
            return;
        }
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        String string = null;
        byte by = byteBuffer.get();
        if (by == 1) {
            string = GameWindow.ReadString(byteBuffer);
        }
        String string2 = null;
        if (byteBuffer.get() == 1) {
            string2 = GameWindow.ReadString(byteBuffer);
        }
        float f = byteBuffer.getFloat();
        float f2 = byteBuffer.getFloat();
        float f3 = byteBuffer.getFloat();
        int n4 = byteBuffer.getInt();
        boolean bl = byteBuffer.get() == 1;
        ZomboidRadio.getInstance().ReceiveTransmission(n, n2, n3, string, string2, f, f2, f3, n4, bl);
    }

    public static void sendPlayerListensChannel(int n, boolean bl, boolean bl2) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypesShort.doPacket((short)1001, byteBufferWriter);
        byteBufferWriter.putInt(n);
        byteBufferWriter.putByte(bl ? (byte)1 : 0);
        byteBufferWriter.putByte(bl2 ? (byte)1 : 0);
        PacketTypes.PacketType.PacketTypeShort.send(connection);
    }

    static void receiveSyncFurnace(ByteBuffer byteBuffer, short s) {
        int n;
        int n2;
        int n3 = byteBuffer.getInt();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n3, n2 = byteBuffer.getInt(), n = byteBuffer.getInt());
        if (isoGridSquare == null) {
            instance.delayPacket(n3, n2, n);
            return;
        }
        if (isoGridSquare != null) {
            BSFurnace bSFurnace = null;
            for (int i = 0; i < isoGridSquare.getObjects().size(); ++i) {
                if (!(isoGridSquare.getObjects().get(i) instanceof BSFurnace)) continue;
                bSFurnace = (BSFurnace)isoGridSquare.getObjects().get(i);
                break;
            }
            if (bSFurnace == null) {
                DebugLog.log("receiveFurnaceChange: furnace is null x,y,z=" + n3 + "," + n2 + "," + n);
                return;
            }
            bSFurnace.fireStarted = byteBuffer.get() == 1;
            bSFurnace.fuelAmount = byteBuffer.getFloat();
            bSFurnace.fuelDecrease = byteBuffer.getFloat();
            bSFurnace.heat = byteBuffer.getFloat();
            bSFurnace.sSprite = GameWindow.ReadString(byteBuffer);
            bSFurnace.sLitSprite = GameWindow.ReadString(byteBuffer);
            bSFurnace.updateLight();
        }
    }

    public static void sendFurnaceChange(BSFurnace bSFurnace) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncFurnace.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(bSFurnace.getSquare().getX());
        byteBufferWriter.putInt(bSFurnace.getSquare().getY());
        byteBufferWriter.putInt(bSFurnace.getSquare().getZ());
        byteBufferWriter.putByte(bSFurnace.isFireStarted() ? (byte)1 : 0);
        byteBufferWriter.putFloat(bSFurnace.getFuelAmount());
        byteBufferWriter.putFloat(bSFurnace.getFuelDecrease());
        byteBufferWriter.putFloat(bSFurnace.getHeat());
        GameWindow.WriteString(byteBufferWriter.bb, bSFurnace.sSprite);
        GameWindow.WriteString(byteBufferWriter.bb, bSFurnace.sLitSprite);
        PacketTypes.PacketType.SyncFurnace.send(connection);
    }

    public static void sendCompost(IsoCompost isoCompost) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncCompost.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(isoCompost.getSquare().getX());
        byteBufferWriter.putInt(isoCompost.getSquare().getY());
        byteBufferWriter.putInt(isoCompost.getSquare().getZ());
        byteBufferWriter.putFloat(isoCompost.getCompost());
        PacketTypes.PacketType.SyncCompost.send(connection);
    }

    static void receiveSyncCompost(ByteBuffer byteBuffer, short s) {
        int n;
        int n2;
        int n3 = byteBuffer.getInt();
        IsoGridSquare isoGridSquare = IsoWorld.instance.CurrentCell.getGridSquare(n3, n2 = byteBuffer.getInt(), n = byteBuffer.getInt());
        if (isoGridSquare != null) {
            IsoCompost isoCompost = isoGridSquare.getCompost();
            if (isoCompost == null) {
                isoCompost = new IsoCompost(isoGridSquare.getCell(), isoGridSquare);
                isoGridSquare.AddSpecialObject(isoCompost);
            }
            isoCompost.setCompost(byteBuffer.getFloat());
            isoCompost.updateSprite();
        }
    }

    public void requestUserlog(String string) {
        if (!GameClient.canSeePlayerStats()) {
            return;
        }
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.Userlog.doPacket(byteBufferWriter);
        GameWindow.WriteString(byteBufferWriter.bb, string);
        PacketTypes.PacketType.Userlog.send(connection);
    }

    public void addUserlog(String string, String string2, String string3) {
        if (!GameClient.canSeePlayerStats()) {
            return;
        }
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.AddUserlog.doPacket(byteBufferWriter);
        GameWindow.WriteString(byteBufferWriter.bb, string);
        GameWindow.WriteString(byteBufferWriter.bb, string2);
        GameWindow.WriteString(byteBufferWriter.bb, string3);
        PacketTypes.PacketType.AddUserlog.send(connection);
    }

    public void removeUserlog(String string, String string2, String string3) {
        if (!GameClient.canModifyPlayerStats()) {
            return;
        }
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RemoveUserlog.doPacket(byteBufferWriter);
        GameWindow.WriteString(byteBufferWriter.bb, string);
        GameWindow.WriteString(byteBufferWriter.bb, string2);
        GameWindow.WriteString(byteBufferWriter.bb, string3);
        PacketTypes.PacketType.RemoveUserlog.send(connection);
    }

    public void addWarningPoint(String string, String string2, int n) {
        if (!GameClient.canModifyPlayerStats()) {
            return;
        }
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.AddWarningPoint.doPacket(byteBufferWriter);
        GameWindow.WriteString(byteBufferWriter.bb, string);
        GameWindow.WriteString(byteBufferWriter.bb, string2);
        byteBufferWriter.putInt(n);
        PacketTypes.PacketType.AddWarningPoint.send(connection);
    }

    static void receiveMessageForAdmin(ByteBuffer byteBuffer, short s) {
        if (GameClient.canSeePlayerStats()) {
            String string = GameWindow.ReadString(byteBuffer);
            int n = byteBuffer.getInt();
            int n2 = byteBuffer.getInt();
            int n3 = byteBuffer.getInt();
            LuaEventManager.triggerEvent("OnAdminMessage", string, n, n2, n3);
        }
    }

    public void wakeUpPlayer(IsoPlayer isoPlayer) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.WakeUpPlayer.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(isoPlayer.OnlineID);
        PacketTypes.PacketType.WakeUpPlayer.send(connection);
    }

    static void receiveWakeUpPlayer(ByteBuffer byteBuffer, short s) {
        IsoPlayer isoPlayer = IDToPlayerMap.get(byteBuffer.getShort());
        if (isoPlayer != null) {
            SleepingEvent.instance.wakeUp(isoPlayer, true);
        }
    }

    public void getDBSchema() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.GetDBSchema.doPacket(byteBufferWriter);
        PacketTypes.PacketType.GetDBSchema.send(connection);
    }

    static void receiveGetDBSchema(ByteBuffer byteBuffer, short s) {
        if (accessLevel.equals("Observer") || accessLevel.equals("")) {
            return;
        }
        GameClient.instance.dbSchema = LuaManager.platform.newTable();
        int n = byteBuffer.getInt();
        for (int i = 0; i < n; ++i) {
            KahluaTable kahluaTable = LuaManager.platform.newTable();
            String string = GameWindow.ReadString(byteBuffer);
            int n2 = byteBuffer.getInt();
            for (int j = 0; j < n2; ++j) {
                KahluaTable kahluaTable2 = LuaManager.platform.newTable();
                String string2 = GameWindow.ReadString(byteBuffer);
                String string3 = GameWindow.ReadString(byteBuffer);
                kahluaTable2.rawset((Object)"name", (Object)string2);
                kahluaTable2.rawset((Object)"type", (Object)string3);
                kahluaTable.rawset(j, (Object)kahluaTable2);
            }
            GameClient.instance.dbSchema.rawset((Object)string, (Object)kahluaTable);
        }
        LuaEventManager.triggerEvent("OnGetDBSchema", (Object)GameClient.instance.dbSchema);
    }

    public void getTableResult(String string, int n) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.GetTableResult.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(n);
        byteBufferWriter.putUTF(string);
        PacketTypes.PacketType.GetTableResult.send(connection);
    }

    static void receiveGetTableResult(ByteBuffer byteBuffer, short s) {
        ArrayList<DBResult> arrayList = new ArrayList<DBResult>();
        int n = byteBuffer.getInt();
        String string = GameWindow.ReadString(byteBuffer);
        int n2 = byteBuffer.getInt();
        ArrayList<String> arrayList2 = new ArrayList<String>();
        for (int i = 0; i < n2; ++i) {
            DBResult dBResult = new DBResult();
            dBResult.setTableName(string);
            int n3 = byteBuffer.getInt();
            for (int j = 0; j < n3; ++j) {
                String string2 = GameWindow.ReadString(byteBuffer);
                String string3 = GameWindow.ReadString(byteBuffer);
                dBResult.getValues().put(string2, string3);
                if (i != 0) continue;
                arrayList2.add(string2);
            }
            dBResult.setColumns(arrayList2);
            arrayList.add(dBResult);
        }
        LuaEventManager.triggerEvent("OnGetTableResult", arrayList, n, string);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void executeQuery(String string, KahluaTable kahluaTable) {
        if (!accessLevel.equals("admin")) {
            return;
        }
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ExecuteQuery.doPacket(byteBufferWriter);
        try {
            byteBufferWriter.putUTF(string);
            kahluaTable.save(byteBufferWriter.bb);
        }
        catch (Throwable throwable) {
            ExceptionLogger.logException(throwable);
        }
        finally {
            PacketTypes.PacketType.ExecuteQuery.send(connection);
        }
    }

    public ArrayList<IsoPlayer> getConnectedPlayers() {
        return this.connectedPlayers;
    }

    public static void sendNonPvpZone(NonPvpZone nonPvpZone, boolean bl) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncNonPvpZone.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(nonPvpZone.getX());
        byteBufferWriter.putInt(nonPvpZone.getY());
        byteBufferWriter.putInt(nonPvpZone.getX2());
        byteBufferWriter.putInt(nonPvpZone.getY2());
        byteBufferWriter.putUTF(nonPvpZone.getTitle());
        byteBufferWriter.putBoolean(bl);
        PacketTypes.PacketType.SyncNonPvpZone.send(connection);
    }

    public static void sendFaction(Faction faction, boolean bl) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncFaction.doPacket(byteBufferWriter);
        faction.writeToBuffer(byteBufferWriter, bl);
        PacketTypes.PacketType.SyncFaction.send(connection);
    }

    public static void sendFactionInvite(Faction faction, IsoPlayer isoPlayer, String string) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SendFactionInvite.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(faction.getName());
        byteBufferWriter.putUTF(isoPlayer.getUsername());
        byteBufferWriter.putUTF(string);
        PacketTypes.PacketType.SendFactionInvite.send(connection);
    }

    static void receiveSendFactionInvite(ByteBuffer byteBuffer, short s) {
        String string = GameWindow.ReadString(byteBuffer);
        String string2 = GameWindow.ReadString(byteBuffer);
        LuaEventManager.triggerEvent("ReceiveFactionInvite", string, string2);
    }

    public static void acceptFactionInvite(Faction faction, String string) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.AcceptedFactionInvite.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(faction.getName());
        byteBufferWriter.putUTF(string);
        PacketTypes.PacketType.AcceptedFactionInvite.send(connection);
    }

    static void receiveAcceptedFactionInvite(ByteBuffer byteBuffer, short s) {
        String string = GameWindow.ReadString(byteBuffer);
        String string2 = GameWindow.ReadString(byteBuffer);
        Faction faction = Faction.getFaction(string);
        if (faction != null) {
            faction.addPlayer(string2);
        }
        LuaEventManager.triggerEvent("AcceptedFactionInvite", string, string2);
    }

    public static void addTicket(String string, String string2, int n) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.AddTicket.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(string);
        byteBufferWriter.putUTF(string2);
        byteBufferWriter.putInt(n);
        PacketTypes.PacketType.AddTicket.send(connection);
    }

    public static void getTickets(String string) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ViewTickets.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(string);
        PacketTypes.PacketType.ViewTickets.send(connection);
    }

    static void receiveViewTickets(ByteBuffer byteBuffer, short s) {
        ArrayList<DBTicket> arrayList = new ArrayList<DBTicket>();
        int n = byteBuffer.getInt();
        for (int i = 0; i < n; ++i) {
            DBTicket dBTicket = new DBTicket(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer), byteBuffer.getInt());
            arrayList.add(dBTicket);
            if (byteBuffer.get() != 1) continue;
            DBTicket dBTicket2 = new DBTicket(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer), byteBuffer.getInt());
            dBTicket2.setIsAnswer(true);
            dBTicket.setAnswer(dBTicket2);
        }
        LuaEventManager.triggerEvent("ViewTickets", arrayList);
    }

    static void receiveChecksum(ByteBuffer byteBuffer, short s) {
        NetChecksum.comparer.clientPacket(byteBuffer);
    }

    public static void removeTicket(int n) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RemoveTicket.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(n);
        PacketTypes.PacketType.RemoveTicket.send(connection);
    }

    public static boolean sendItemListNet(IsoPlayer isoPlayer, ArrayList<InventoryItem> arrayList, IsoPlayer isoPlayer2, String string, String string2) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SendItemListNet.doPacket(byteBufferWriter);
        byteBufferWriter.putByte(isoPlayer2 != null ? (byte)1 : 0);
        if (isoPlayer2 != null) {
            byteBufferWriter.putShort(isoPlayer2.getOnlineID());
        }
        byteBufferWriter.putByte(isoPlayer != null ? (byte)1 : 0);
        if (isoPlayer != null) {
            byteBufferWriter.putShort(isoPlayer.getOnlineID());
        }
        GameWindow.WriteString(byteBufferWriter.bb, string);
        byteBufferWriter.putByte(string2 != null ? (byte)1 : 0);
        if (string2 != null) {
            GameWindow.WriteString(byteBufferWriter.bb, string2);
        }
        try {
            CompressIdenticalItems.save(byteBufferWriter.bb, arrayList, null);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            connection.cancelPacket();
            return false;
        }
        PacketTypes.PacketType.SendItemListNet.send(connection);
        return true;
    }

    static void receiveSendItemListNet(ByteBuffer byteBuffer, short s) {
        IsoPlayer isoPlayer = null;
        if (byteBuffer.get() != 1) {
            isoPlayer = IDToPlayerMap.get(byteBuffer.getShort());
        }
        IsoPlayer isoPlayer2 = null;
        if (byteBuffer.get() == 1) {
            isoPlayer2 = IDToPlayerMap.get(byteBuffer.getShort());
        }
        String string = GameWindow.ReadString(byteBuffer);
        String string2 = null;
        if (byteBuffer.get() == 1) {
            string2 = GameWindow.ReadString(byteBuffer);
        }
        int n = byteBuffer.getShort();
        ArrayList<InventoryItem> arrayList = new ArrayList<InventoryItem>(n);
        try {
            for (int i = 0; i < n; ++i) {
                InventoryItem inventoryItem = InventoryItem.loadItem(byteBuffer, 186);
                if (inventoryItem == null) continue;
                arrayList.add(inventoryItem);
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        LuaEventManager.triggerEvent("OnReceiveItemListNet", isoPlayer2, arrayList, isoPlayer, string, string2);
    }

    public void requestTrading(IsoPlayer isoPlayer, IsoPlayer isoPlayer2) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RequestTrading.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(isoPlayer.OnlineID);
        byteBufferWriter.putShort(isoPlayer2.OnlineID);
        byteBufferWriter.putByte((byte)0);
        PacketTypes.PacketType.RequestTrading.send(connection);
    }

    public void acceptTrading(IsoPlayer isoPlayer, IsoPlayer isoPlayer2, boolean bl) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RequestTrading.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(isoPlayer2.OnlineID);
        byteBufferWriter.putShort(isoPlayer.OnlineID);
        byteBufferWriter.putByte(bl ? (byte)1 : 2);
        PacketTypes.PacketType.RequestTrading.send(connection);
    }

    static void receiveRequestTrading(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        byte by = byteBuffer.get();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            if (by == 0) {
                LuaEventManager.triggerEvent("RequestTrade", isoPlayer);
            } else {
                LuaEventManager.triggerEvent("AcceptedTrade", by == 1);
            }
        }
    }

    public void tradingUISendAddItem(IsoPlayer isoPlayer, IsoPlayer isoPlayer2, InventoryItem inventoryItem) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.TradingUIAddItem.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(isoPlayer.OnlineID);
        byteBufferWriter.putShort(isoPlayer2.OnlineID);
        try {
            inventoryItem.saveWithSize(byteBufferWriter.bb, false);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        PacketTypes.PacketType.TradingUIAddItem.send(connection);
    }

    static void receiveTradingUIAddItem(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        InventoryItem inventoryItem = null;
        try {
            inventoryItem = InventoryItem.loadItem(byteBuffer, 186);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
        if (inventoryItem == null) {
            return;
        }
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            LuaEventManager.triggerEvent("TradingUIAddItem", isoPlayer, inventoryItem);
        }
    }

    public void tradingUISendRemoveItem(IsoPlayer isoPlayer, IsoPlayer isoPlayer2, int n) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.TradingUIRemoveItem.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(isoPlayer.OnlineID);
        byteBufferWriter.putShort(isoPlayer2.OnlineID);
        byteBufferWriter.putInt(n);
        PacketTypes.PacketType.TradingUIRemoveItem.send(connection);
    }

    static void receiveTradingUIRemoveItem(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        int n = byteBuffer.getInt();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            LuaEventManager.triggerEvent("TradingUIRemoveItem", isoPlayer, n);
        }
    }

    public void tradingUISendUpdateState(IsoPlayer isoPlayer, IsoPlayer isoPlayer2, int n) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.TradingUIUpdateState.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(isoPlayer.OnlineID);
        byteBufferWriter.putShort(isoPlayer2.OnlineID);
        byteBufferWriter.putInt(n);
        PacketTypes.PacketType.TradingUIUpdateState.send(connection);
    }

    static void receiveTradingUIUpdateState(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        int n = byteBuffer.getInt();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            LuaEventManager.triggerEvent("TradingUIUpdateState", isoPlayer, n);
        }
    }

    public static void sendBuildingStashToDo(String string) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ReadAnnotedMap.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(string);
        PacketTypes.PacketType.ReadAnnotedMap.send(connection);
    }

    public static void setServerStatisticEnable(boolean bl) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.StatisticRequest.doPacket(byteBufferWriter);
        byteBufferWriter.putBoolean(bl);
        PacketTypes.PacketType.StatisticRequest.send(connection);
        MPStatistic.clientStatisticEnable = bl;
    }

    public static boolean getServerStatisticEnable() {
        return MPStatistic.clientStatisticEnable;
    }

    public static void sendRequestInventory(IsoPlayer isoPlayer) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RequestInventory.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(IsoPlayer.getInstance().getOnlineID());
        byteBufferWriter.putShort(isoPlayer.getOnlineID());
        PacketTypes.PacketType.RequestInventory.send(connection);
    }

    private int sendInventoryPutItems(ByteBufferWriter byteBufferWriter, LinkedHashMap<String, InventoryItem> linkedHashMap, long l) {
        int n = linkedHashMap.size();
        Iterator<String> iterator = linkedHashMap.keySet().iterator();
        while (iterator.hasNext()) {
            InventoryItem inventoryItem = linkedHashMap.get(iterator.next());
            byteBufferWriter.putUTF(inventoryItem.getModule());
            byteBufferWriter.putUTF(inventoryItem.getType());
            byteBufferWriter.putLong(inventoryItem.getID());
            byteBufferWriter.putLong(l);
            byteBufferWriter.putBoolean(IsoPlayer.getInstance().isEquipped(inventoryItem));
            if (inventoryItem instanceof DrainableComboItem) {
                byteBufferWriter.putFloat(((DrainableComboItem)inventoryItem).getUsedDelta());
            } else {
                byteBufferWriter.putFloat(inventoryItem.getCondition());
            }
            byteBufferWriter.putInt(inventoryItem.getCount());
            if (inventoryItem instanceof DrainableComboItem) {
                byteBufferWriter.putUTF(Translator.getText("IGUI_ItemCat_Drainable"));
            } else {
                byteBufferWriter.putUTF(inventoryItem.getCategory());
            }
            byteBufferWriter.putUTF(inventoryItem.getContainer().getType());
            byteBufferWriter.putBoolean(inventoryItem.getWorker() != null && inventoryItem.getWorker().equals("inInv"));
            if (!(inventoryItem instanceof InventoryContainer) || ((InventoryContainer)inventoryItem).getItemContainer() == null || ((InventoryContainer)inventoryItem).getItemContainer().getItems().isEmpty()) continue;
            LinkedHashMap<String, InventoryItem> linkedHashMap2 = ((InventoryContainer)inventoryItem).getItemContainer().getItems4Admin();
            n += linkedHashMap2.size();
            this.sendInventoryPutItems(byteBufferWriter, linkedHashMap2, inventoryItem.getID());
        }
        return n;
    }

    static void receiveRequestInventory(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SendInventory.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(s2);
        int n = byteBufferWriter.bb.position();
        byteBufferWriter.putInt(0);
        LinkedHashMap<String, InventoryItem> linkedHashMap = IsoPlayer.getInstance().getInventory().getItems4Admin();
        int n2 = instance.sendInventoryPutItems(byteBufferWriter, linkedHashMap, -1L);
        int n3 = byteBufferWriter.bb.position();
        byteBufferWriter.bb.position(n);
        byteBufferWriter.putInt(n2);
        byteBufferWriter.bb.position(n3);
        PacketTypes.PacketType.SendInventory.send(connection);
    }

    static void receiveSendInventory(ByteBuffer byteBuffer, short s) {
        int n = byteBuffer.getInt();
        KahluaTable kahluaTable = LuaManager.platform.newTable();
        for (int i = 0; i < n; ++i) {
            KahluaTable kahluaTable2 = LuaManager.platform.newTable();
            String string = GameWindow.ReadStringUTF(byteBuffer) + "." + GameWindow.ReadStringUTF(byteBuffer);
            long l = byteBuffer.getLong();
            long l2 = byteBuffer.getLong();
            boolean bl = byteBuffer.get() == 1;
            float f = byteBuffer.getFloat();
            int n2 = byteBuffer.getInt();
            String string2 = GameWindow.ReadStringUTF(byteBuffer);
            String string3 = GameWindow.ReadStringUTF(byteBuffer);
            boolean bl2 = byteBuffer.get() == 1;
            kahluaTable2.rawset((Object)"fullType", (Object)string);
            kahluaTable2.rawset((Object)"itemId", (Object)l);
            kahluaTable2.rawset((Object)"isEquip", (Object)bl);
            kahluaTable2.rawset((Object)"var", (Object)((double)Math.round((double)f * 100.0) / 100.0));
            kahluaTable2.rawset((Object)"count", (Object)("" + n2));
            kahluaTable2.rawset((Object)"cat", (Object)string2);
            kahluaTable2.rawset((Object)"parrentId", (Object)l2);
            kahluaTable2.rawset((Object)"hasParrent", (Object)(l2 != -1L ? 1 : 0));
            kahluaTable2.rawset((Object)"container", (Object)string3);
            kahluaTable2.rawset((Object)"inInv", (Object)bl2);
            kahluaTable.rawset(kahluaTable.size() + 1, (Object)kahluaTable2);
        }
        LuaEventManager.triggerEvent("MngInvReceiveItems", (Object)kahluaTable);
    }

    public static void sendGetItemInvMng(long l) {
    }

    static void receiveSpawnRegion(ByteBuffer byteBuffer, short s) {
        if (GameClient.instance.ServerSpawnRegions == null) {
            GameClient.instance.ServerSpawnRegions = LuaManager.platform.newTable();
        }
        int n = byteBuffer.getInt();
        KahluaTable kahluaTable = LuaManager.platform.newTable();
        try {
            kahluaTable.load(byteBuffer, 186);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        GameClient.instance.ServerSpawnRegions.rawset(n, (Object)kahluaTable);
    }

    static void receivePlayerConnectLoading(ByteBuffer byteBuffer) throws IOException {
        int n = byteBuffer.position();
        if (!instance.receivePlayerConnectWhileLoading(byteBuffer)) {
            byteBuffer.position(n);
            throw new IOException();
        }
    }

    static void receiveClimateManagerPacket(ByteBuffer byteBuffer, short s) {
        ClimateManager climateManager = ClimateManager.getInstance();
        if (climateManager != null) {
            try {
                climateManager.receiveClimatePacket(byteBuffer, null);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    static void receiveServerMap(ByteBuffer byteBuffer, short s) {
        ClientServerMap.receivePacket(byteBuffer);
    }

    static void receivePassengerMap(ByteBuffer byteBuffer, short s) {
        PassengerMap.clientReceivePacket(byteBuffer);
    }

    static void receiveIsoRegionServerPacket(ByteBuffer byteBuffer, short s) {
        IsoRegions.receiveServerUpdatePacket(byteBuffer);
    }

    public static void sendIsoRegionDataRequest() {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.IsoRegionClientRequestFullUpdate.doPacket(byteBufferWriter);
        PacketTypes.PacketType.IsoRegionClientRequestFullUpdate.send(connection);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendSandboxOptionsToServer(SandboxOptions sandboxOptions) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SandboxOptions.doPacket(byteBufferWriter);
        try {
            sandboxOptions.save(byteBufferWriter.bb);
        }
        catch (IOException iOException) {
            ExceptionLogger.logException(iOException);
        }
        finally {
            PacketTypes.PacketType.SandboxOptions.send(connection);
        }
    }

    static void receiveSandboxOptions(ByteBuffer byteBuffer, short s) {
        try {
            SandboxOptions.instance.load(byteBuffer);
            SandboxOptions.instance.applySettings();
            SandboxOptions.instance.toLua();
        }
        catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }
    }

    static void receiveChunkObjectState(ByteBuffer byteBuffer, short s) {
        short s2;
        short s3 = byteBuffer.getShort();
        IsoChunk isoChunk = IsoWorld.instance.CurrentCell.getChunk(s3, s2 = byteBuffer.getShort());
        if (isoChunk == null) {
            return;
        }
        try {
            isoChunk.loadObjectState(byteBuffer);
        }
        catch (Throwable throwable) {
            ExceptionLogger.logException(throwable);
        }
    }

    static void receivePlayerLeaveChat(ByteBuffer byteBuffer, short s) {
        ChatManager.getInstance().processLeaveChatPacket(byteBuffer);
    }

    static void receiveInitPlayerChat(ByteBuffer byteBuffer, short s) {
        ChatManager.getInstance().processInitPlayerChatPacket(byteBuffer);
    }

    static void receiveAddChatTab(ByteBuffer byteBuffer, short s) {
        ChatManager.getInstance().processAddTabPacket(byteBuffer);
    }

    static void receiveRemoveChatTab(ByteBuffer byteBuffer, short s) {
        ChatManager.getInstance().processRemoveTabPacket(byteBuffer);
    }

    static void receivePlayerNotFound(ByteBuffer byteBuffer, short s) {
        ChatManager.getInstance().processPlayerNotFound();
    }

    public static void sendZombieHelmetFall(IsoPlayer isoPlayer, IsoGameCharacter isoGameCharacter, InventoryItem inventoryItem) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.ZombieHelmetFalling.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)isoPlayer.PlayerIndex);
        byteBufferWriter.putShort(isoGameCharacter.getOnlineID());
        byteBufferWriter.putUTF(inventoryItem.getType());
        PacketTypes.PacketType.ZombieHelmetFalling.send(connection);
    }

    static void receiveZombieHelmetFalling(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        IsoZombie isoZombie = (IsoZombie)IDToZombieMap.get(s2);
        String string = GameWindow.ReadString(byteBuffer);
        if (isoZombie == null || StringUtils.isNullOrEmpty(string)) {
            return;
        }
        isoZombie.helmetFall(true, string);
    }

    public static void sendPerks(IsoPlayer isoPlayer) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncPerks.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)isoPlayer.PlayerIndex);
        byteBufferWriter.putInt(isoPlayer.getPerkLevel(PerkFactory.Perks.Sneak));
        byteBufferWriter.putInt(isoPlayer.getPerkLevel(PerkFactory.Perks.Strength));
        byteBufferWriter.putInt(isoPlayer.getPerkLevel(PerkFactory.Perks.Fitness));
        PacketTypes.PacketType.SyncPerks.send(connection);
    }

    static void receiveSyncPerks(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer == null || isoPlayer.isLocalPlayer()) {
            return;
        }
        isoPlayer.remoteSneakLvl = n;
        isoPlayer.remoteStrLvl = n2;
        isoPlayer.remoteFitLvl = n3;
    }

    public static void sendWeight(IsoPlayer isoPlayer) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncWeight.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)isoPlayer.PlayerIndex);
        byteBufferWriter.putFloat(isoPlayer.getNutrition().getWeight());
        PacketTypes.PacketType.SyncWeight.send(connection);
    }

    static void receiveSyncWeight(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        float f = byteBuffer.getFloat();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer == null || isoPlayer.isLocalPlayer()) {
            return;
        }
        isoPlayer.getNutrition().setWeight(f);
    }

    static void receiveGlobalModData(ByteBuffer byteBuffer, short s) {
        GlobalModData.instance.receive(byteBuffer);
    }

    public static void sendSafehouseInvite(SafeHouse safeHouse, IsoPlayer isoPlayer, String string) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SendSafehouseInvite.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(safeHouse.getTitle());
        byteBufferWriter.putUTF(isoPlayer.getUsername());
        byteBufferWriter.putUTF(string);
        byteBufferWriter.putInt(safeHouse.getX());
        byteBufferWriter.putInt(safeHouse.getY());
        byteBufferWriter.putInt(safeHouse.getW());
        byteBufferWriter.putInt(safeHouse.getH());
        PacketTypes.PacketType.SendSafehouseInvite.send(connection);
    }

    static void receiveSendSafehouseInvite(ByteBuffer byteBuffer, short s) {
        String string = GameWindow.ReadString(byteBuffer);
        String string2 = GameWindow.ReadString(byteBuffer);
        int n = byteBuffer.getInt();
        int n2 = byteBuffer.getInt();
        int n3 = byteBuffer.getInt();
        int n4 = byteBuffer.getInt();
        SafeHouse safeHouse = SafeHouse.getSafeHouse(n, n2, n3, n4);
        LuaEventManager.triggerEvent("ReceiveSafehouseInvite", safeHouse, string2);
    }

    public static void acceptSafehouseInvite(SafeHouse safeHouse, String string) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.AcceptedSafehouseInvite.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(safeHouse.getTitle());
        byteBufferWriter.putUTF(string);
        byteBufferWriter.putUTF(username);
        byteBufferWriter.putInt(safeHouse.getX());
        byteBufferWriter.putInt(safeHouse.getY());
        byteBufferWriter.putInt(safeHouse.getW());
        byteBufferWriter.putInt(safeHouse.getH());
        PacketTypes.PacketType.AcceptedSafehouseInvite.send(connection);
    }

    static void receiveAcceptedSafehouseInvite(ByteBuffer byteBuffer, short s) {
        int n;
        int n2;
        int n3;
        String string = GameWindow.ReadString(byteBuffer);
        String string2 = GameWindow.ReadString(byteBuffer);
        String string3 = GameWindow.ReadString(byteBuffer);
        int n4 = byteBuffer.getInt();
        SafeHouse safeHouse = SafeHouse.getSafeHouse(n4, n3 = byteBuffer.getInt(), n2 = byteBuffer.getInt(), n = byteBuffer.getInt());
        if (safeHouse != null) {
            safeHouse.addPlayer(string3);
        }
        LuaEventManager.triggerEvent("AcceptedSafehouseInvite", safeHouse.getTitle(), string2);
    }

    public static void sendEquippedRadioFreq(IsoPlayer isoPlayer) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.SyncEquippedRadioFreq.doPacket(byteBufferWriter);
        byteBufferWriter.putByte((byte)isoPlayer.PlayerIndex);
        byteBufferWriter.putInt(isoPlayer.invRadioFreq.size());
        for (int i = 0; i < isoPlayer.invRadioFreq.size(); ++i) {
            byteBufferWriter.putInt((Integer)isoPlayer.invRadioFreq.get(i));
        }
        PacketTypes.PacketType.SyncEquippedRadioFreq.send(connection);
    }

    static void receiveSyncEquippedRadioFreq(ByteBuffer byteBuffer, short s) {
        short s2 = byteBuffer.getShort();
        int n = byteBuffer.getInt();
        IsoPlayer isoPlayer = IDToPlayerMap.get(s2);
        if (isoPlayer != null) {
            int n2;
            isoPlayer.invRadioFreq.clear();
            for (n2 = 0; n2 < n; ++n2) {
                isoPlayer.invRadioFreq.add(byteBuffer.getInt());
            }
            System.out.println("SYNC EQUIPPED RADIO FOR PLAYER: " + isoPlayer.getDisplayName());
            for (n2 = 0; n2 < isoPlayer.invRadioFreq.size(); ++n2) {
                System.out.println(isoPlayer.invRadioFreq.get(n2));
            }
        }
    }

    static {
        count = 0;
        ip = "localhost";
        localIP = "";
        password = "testpass";
        allChatMuted = false;
        username = "lemmy101";
        serverPassword = "";
        accessLevel = "";
        port = GameServer.DEFAULT_PORT;
        checksum = "";
        checksumValid = false;
        pingsList = new ArrayList<Long>();
        loadedCells = new ClientServerMap[4];
        isPaused = false;
        positions = new HashMap<Short, Vector2>(ServerOptions.getInstance().getMaxPlayers());
        MainLoopNetDataQ = new ConcurrentLinkedQueue();
        MainLoopNetData = new ArrayList();
        LoadingMainLoopNetData = new ArrayList();
        DelayedCoopNetData = new ArrayList();
        ServerPredictedAhead = 0.0f;
        IDToPlayerMap = new HashMap();
        IDToZombieMap = new TShortObjectHashMap();
        askPing = false;
        startAuth = null;
        poisonousBerry = null;
        poisonousMushroom = null;
    }

    private static final class RequestState
    extends Enum<RequestState> {
        public static final /* enum */ RequestState Start = new RequestState();
        public static final /* enum */ RequestState RequestDescriptors = new RequestState();
        public static final /* enum */ RequestState ReceivedDescriptors = new RequestState();
        public static final /* enum */ RequestState RequestMetaGrid = new RequestState();
        public static final /* enum */ RequestState ReceivedMetaGrid = new RequestState();
        public static final /* enum */ RequestState RequestMapZone = new RequestState();
        public static final /* enum */ RequestState ReceivedMapZone = new RequestState();
        public static final /* enum */ RequestState RequestPlayerZombieDescriptors = new RequestState();
        public static final /* enum */ RequestState ReceivedPlayerZombieDescriptors = new RequestState();
        public static final /* enum */ RequestState Complete = new RequestState();
        private static final /* synthetic */ RequestState[] $VALUES;

        public static RequestState[] values() {
            return (RequestState[])$VALUES.clone();
        }

        public static RequestState valueOf(String string) {
            return Enum.valueOf(RequestState.class, string);
        }

        private static /* synthetic */ RequestState[] $values() {
            return new RequestState[]{Start, RequestDescriptors, ReceivedDescriptors, RequestMetaGrid, ReceivedMetaGrid, RequestMapZone, ReceivedMapZone, RequestPlayerZombieDescriptors, ReceivedPlayerZombieDescriptors, Complete};
        }

        static {
            $VALUES = RequestState.$values();
        }
    }
}

