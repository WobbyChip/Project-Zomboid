// 
// Decompiled by Procyon v0.5.36
// 

package zombie.network;

import zombie.debug.DebugType;
import zombie.core.Core;
import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import java.util.TreeMap;
import zombie.core.network.ByteBufferWriter;
import java.util.Map;

public class PacketTypes
{
    public static final short SteamGeneric_ProfileName = 0;
    public static final short ContainerDeadBody = 0;
    public static final short ContainerWorldObject = 1;
    public static final short ContainerObject = 2;
    public static final short ContainerVehicle = 3;
    public static final Map<Short, PacketType> packetTypes;
    
    public static void doPingPacket(final ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(28);
    }
    
    static {
        packetTypes = new TreeMap<Short, PacketType>();
        for (final PacketType packetType : PacketType.values()) {
            final PacketType packetType2 = PacketTypes.packetTypes.put(packetType.getId(), packetType);
            if (packetType2 != null) {
                DebugLog.Multiplayer.error((Object)String.format("PacketType: duplicate \"%s\" \"%s\" id=%d", packetType2.name(), packetType.name(), packetType.getId()));
            }
        }
    }
    
    public enum PacketType
    {
        ServerPulse(1, 1, 0, (CallbackServerProcess)null, GameClient::receiveServerPulse, (CallbackClientProcess)null), 
        Login(2, 1, 3, GameServer::receiveLogin, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        HumanVisual(3, 1, 2, GameServer::receiveHumanVisual, GameClient::receiveHumanVisual, (CallbackClientProcess)null), 
        KeepAlive(4, 1, 0, GameServer::receiveKeepAlive, GameClient::receiveKeepAlive, GameClient::skipPacket), 
        Vehicles(5, 1, 2, GameServer::receiveVehicles, GameClient::receiveVehicles, GameClient::receiveVehiclesLoading), 
        PlayerConnect(6, 1, 3, GameServer::receivePlayerConnect, GameClient::receivePlayerConnect, (CallbackClientProcess)null), 
        VehiclesUnreliable(7, 2, 0, GameServer::receiveVehicles, GameClient::receiveVehicles, GameClient::receiveVehiclesLoading), 
        MetaGrid(9, 1, 2, (CallbackServerProcess)null, GameClient::receiveMetaGrid, (CallbackClientProcess)null), 
        Helicopter(11, 1, 2, (CallbackServerProcess)null, GameClient::receiveHelicopter, (CallbackClientProcess)null), 
        SyncIsoObject(12, 1, 2, GameServer::receiveSyncIsoObject, GameClient::receiveSyncIsoObject, (CallbackClientProcess)null), 
        PlayerTimeout(13, 1, 2, (CallbackServerProcess)null, GameClient::receivePlayerTimeout, GameClient::receivePlayerTimeout), 
        SteamGeneric(14, 1, 2, GameServer::receiveSteamGeneric, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        ServerMap(15, 1, 3, (CallbackServerProcess)null, GameClient::receiveServerMap, GameClient::receiveServerMapLoading), 
        PassengerMap(16, 1, 2, GameServer::receivePassengerMap, GameClient::receivePassengerMap, (CallbackClientProcess)null), 
        AddItemToMap(17, 1, 2, GameServer::receiveAddItemToMap, GameClient::receiveAddItemToMap, (CallbackClientProcess)null), 
        SentChunk(18, 1, 2, (CallbackServerProcess)null, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        SyncClock(19, 1, 2, (CallbackServerProcess)null, GameClient::receiveSyncClock, (CallbackClientProcess)null), 
        AddInventoryItemToContainer(20, 1, 2, GameServer::receiveAddInventoryItemToContainer, GameClient::receiveAddInventoryItemToContainer, (CallbackClientProcess)null), 
        ConnectionDetails(21, 1, 2, (CallbackServerProcess)null, GameClient::receiveConnectionDetails, GameClient::receiveConnectionDetails), 
        RemoveInventoryItemFromContainer(22, 1, 2, GameServer::receiveRemoveInventoryItemFromContainer, GameClient::receiveRemoveInventoryItemFromContainer, (CallbackClientProcess)null), 
        RemoveItemFromSquare(23, 1, 2, GameServer::receiveRemoveItemFromSquare, GameClient::receiveRemoveItemFromSquare, (CallbackClientProcess)null), 
        RequestLargeAreaZip(24, 1, 2, GameServer::receiveRequestLargeAreaZip, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        Equip(25, 1, 2, GameServer::receiveEquip, GameClient::receiveEquip, (CallbackClientProcess)null), 
        HitCharacter(26, 0, 3, GameServer::receiveHitCharacter, GameClient::receiveHitCharacter, (CallbackClientProcess)null), 
        AddCoopPlayer(27, 1, 2, GameServer::receiveAddCoopPlayer, GameClient::receiveAddCoopPlayer, (CallbackClientProcess)null), 
        WeaponHit(28, 1, 2, GameServer::receiveWeaponHit, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        KillZombie(30, 1, 2, GameServer::receiveKillZombie, GameClient::receiveKillZombie, (CallbackClientProcess)null), 
        SandboxOptions(31, 1, 2, GameServer::receiveSandboxOptions, GameClient::receiveSandboxOptions, (CallbackClientProcess)null), 
        SmashWindow(32, 1, 2, GameServer::receiveSmashWindow, GameClient::receiveSmashWindow, (CallbackClientProcess)null), 
        PlayerDeath(33, 0, 3, GameServer::receivePlayerDeath, GameClient::receivePlayerDeath, (CallbackClientProcess)null), 
        RequestZipList(34, 0, 2, GameServer::receiveRequestZipList, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        ItemStats(35, 1, 2, GameServer::receiveItemStats, GameClient::receiveItemStats, (CallbackClientProcess)null), 
        NotRequiredInZip(36, 0, 0, GameServer::receiveNotRequiredInZip, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        RequestData(37, 1, 3, GameServer::receiveRequestData, GameClient::receiveRequestData, GameClient::receiveRequestData), 
        GlobalObjects(38, 1, 2, GameServer::receiveGlobalObjects, GameClient::receiveGlobalObjects, (CallbackClientProcess)null), 
        ZombieDeath(39, 1, 3, GameServer::receiveZombieDeath, GameClient::receiveZombieDeath, (CallbackClientProcess)null), 
        AccessDenied(40, 1, 2, (CallbackServerProcess)null, (CallbackClientProcess)null, GameClient::receiveAccessDenied), 
        PlayerDamage(41, 1, 2, GameServer::receivePlayerDamage, GameClient::receivePlayerDamage, (CallbackClientProcess)null), 
        Bandage(42, 1, 2, GameServer::receiveBandage, GameClient::receiveBandage, (CallbackClientProcess)null), 
        EatFood(43, 1, 2, GameServer::receiveEatFood, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        RequestItemsForContainer(44, 1, 2, GameServer::receiveRequestItemsForContainer, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        Drink(45, 1, 2, GameServer::receiveDrink, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        SyncAlarmClock(46, 1, 2, GameServer::receiveSyncAlarmClock, GameClient::receiveSyncAlarmClock, (CallbackClientProcess)null), 
        PacketCounts(47, 1, 2, GameServer::receivePacketCounts, GameClient::receivePacketCounts, (CallbackClientProcess)null), 
        SendModData(48, 1, 2, GameServer::receiveSendModData, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        RemoveContestedItemsFromInventory(49, 1, 2, (CallbackServerProcess)null, GameClient::receiveRemoveContestedItemsFromInventory, (CallbackClientProcess)null), 
        ScoreboardUpdate(50, 1, 2, GameServer::receiveScoreboardUpdate, GameClient::receiveScoreboardUpdate, (CallbackClientProcess)null), 
        ReceiveModData(51, 1, 2, (CallbackServerProcess)null, GameClient::receiveReceiveModData, (CallbackClientProcess)null), 
        ServerQuit(52, 1, 2, (CallbackServerProcess)null, GameClient::receiveServerQuit, (CallbackClientProcess)null), 
        PlaySound(53, 1, 2, GameServer::receivePlaySound, GameClient::receivePlaySound, (CallbackClientProcess)null), 
        WorldSound(54, 1, 2, GameServer::receiveWorldSound, GameClient::receiveWorldSound, (CallbackClientProcess)null), 
        AddAmbient(55, 1, 2, (CallbackServerProcess)null, GameClient::receiveAddAmbient, (CallbackClientProcess)null), 
        SyncClothing(56, 1, 2, GameServer::receiveSyncClothing, GameClient::receiveSyncClothing, (CallbackClientProcess)null), 
        ClientCommand(57, 1, 2, GameServer::receiveClientCommand, GameClient::receiveClientCommand, (CallbackClientProcess)null), 
        ObjectModData(58, 1, 2, GameServer::receiveObjectModData, GameClient::receiveObjectModData, (CallbackClientProcess)null), 
        ObjectChange(59, 1, 2, (CallbackServerProcess)null, GameClient::receiveObjectChange, (CallbackClientProcess)null), 
        BloodSplatter(60, 1, 2, (CallbackServerProcess)null, GameClient::receiveBloodSplatter, (CallbackClientProcess)null), 
        ZombieSound(61, 1, 2, (CallbackServerProcess)null, GameClient::receiveZombieSound, (CallbackClientProcess)null), 
        ZombieDescriptors(62, 1, 2, (CallbackServerProcess)null, GameClient::receiveZombieDescriptors, (CallbackClientProcess)null), 
        SlowFactor(63, 1, 2, (CallbackServerProcess)null, GameClient::receiveSlowFactor, (CallbackClientProcess)null), 
        Weather(64, 1, 2, (CallbackServerProcess)null, GameClient::receiveWeather, (CallbackClientProcess)null), 
        @Deprecated
        RequestPlayerData(67, 1, 2, GameServer::receiveRequestPlayerData, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        RemoveCorpseFromMap(68, 1, 2, GameServer::receiveRemoveCorpseFromMap, GameClient::receiveRemoveCorpseFromMap, (CallbackClientProcess)null), 
        AddCorpseToMap(69, 1, 2, GameServer::receiveAddCorpseToMap, GameClient::receiveAddCorpseToMap, (CallbackClientProcess)null), 
        StartFire(75, 1, 2, GameServer::receiveStartFire, GameClient::receiveStartFire, (CallbackClientProcess)null), 
        UpdateItemSprite(76, 1, 2, GameServer::receiveUpdateItemSprite, GameClient::receiveUpdateItemSprite, (CallbackClientProcess)null), 
        StartRain(77, 1, 2, (CallbackServerProcess)null, GameClient::receiveStartRain, (CallbackClientProcess)null), 
        StopRain(78, 1, 2, (CallbackServerProcess)null, GameClient::receiveStopRain, (CallbackClientProcess)null), 
        WorldMessage(79, 1, 2, GameServer::receiveWorldMessage, GameClient::receiveWorldMessage, (CallbackClientProcess)null), 
        getModData(80, 1, 2, GameServer::receiveGetModData, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        ReceiveCommand(81, 2, 3, GameServer::receiveReceiveCommand, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        ReloadOptions(82, 1, 2, (CallbackServerProcess)null, GameClient::receiveReloadOptions, (CallbackClientProcess)null), 
        Kicked(83, 1, 2, (CallbackServerProcess)null, GameClient::receiveKicked, GameClient::receiveKickedLoading), 
        ExtraInfo(84, 1, 2, GameServer::receiveExtraInfo, GameClient::receiveExtraInfo, (CallbackClientProcess)null), 
        AddItemInInventory(85, 1, 2, (CallbackServerProcess)null, GameClient::receiveAddItemInInventory, (CallbackClientProcess)null), 
        ChangeSafety(86, 1, 2, GameServer::receiveChangeSafety, GameClient::receiveChangeSafety, (CallbackClientProcess)null), 
        Ping(87, 0, 0, GameServer::receivePing, GameClient::receivePing, GameClient::receivePing), 
        WriteLog(88, 1, 2, GameServer::receiveWriteLog, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        AddXP(89, 1, 2, (CallbackServerProcess)null, GameClient::receiveAddXP, (CallbackClientProcess)null), 
        UpdateOverlaySprite(90, 1, 2, GameServer::receiveUpdateOverlaySprite, GameClient::receiveUpdateOverlaySprite, (CallbackClientProcess)null), 
        Checksum(91, 1, 3, GameServer::receiveChecksum, GameClient::receiveChecksum, GameClient::receiveChecksumLoading), 
        ConstructedZone(92, 1, 2, GameServer::receiveConstructedZone, GameClient::receiveConstructedZone, (CallbackClientProcess)null), 
        RegisterZone(94, 1, 2, GameServer::receiveRegisterZone, GameClient::receiveRegisterZone, (CallbackClientProcess)null), 
        WoundInfection(97, 1, 2, GameServer::receiveWoundInfection, GameClient::receiveWoundInfection, (CallbackClientProcess)null), 
        Stitch(98, 1, 2, GameServer::receiveStitch, GameClient::receiveStitch, (CallbackClientProcess)null), 
        Disinfect(99, 1, 2, GameServer::receiveDisinfect, GameClient::receiveDisinfect, (CallbackClientProcess)null), 
        AdditionalPain(100, 1, 2, GameServer::receiveAdditionalPain, GameClient::receiveAdditionalPain, (CallbackClientProcess)null), 
        RemoveGlass(101, 1, 2, GameServer::receiveRemoveGlass, GameClient::receiveRemoveGlass, (CallbackClientProcess)null), 
        Splint(102, 1, 2, GameServer::receiveSplint, GameClient::receiveSplint, (CallbackClientProcess)null), 
        RemoveBullet(103, 1, 2, GameServer::receiveRemoveBullet, GameClient::receiveRemoveBullet, (CallbackClientProcess)null), 
        CleanBurn(104, 1, 2, GameServer::receiveCleanBurn, GameClient::receiveCleanBurn, (CallbackClientProcess)null), 
        SyncThumpable(105, 1, 2, GameServer::receiveSyncThumpable, GameClient::receiveSyncThumpable, (CallbackClientProcess)null), 
        SyncDoorKey(106, 1, 2, GameServer::receiveSyncDoorKey, GameClient::receiveSyncDoorKey, (CallbackClientProcess)null), 
        AddXpCommand(107, 1, 2, (CallbackServerProcess)null, GameClient::receiveAddXpCommand, (CallbackClientProcess)null), 
        Teleport(108, 1, 2, GameServer::receiveTeleport, GameClient::receiveTeleport, (CallbackClientProcess)null), 
        RemoveBlood(109, 1, 2, GameServer::receiveRemoveBlood, GameClient::receiveRemoveBlood, (CallbackClientProcess)null), 
        AddExplosiveTrap(110, 1, 2, GameServer::receiveAddExplosiveTrap, GameClient::receiveAddExplosiveTrap, (CallbackClientProcess)null), 
        BodyDamageUpdate(112, 1, 2, GameServer::receiveBodyDamageUpdate, GameClient::receiveBodyDamageUpdate, (CallbackClientProcess)null), 
        SyncSafehouse(114, 1, 2, GameServer::receiveSyncSafehouse, GameClient::receiveSyncSafehouse, (CallbackClientProcess)null), 
        SledgehammerDestroy(115, 1, 2, GameServer::receiveSledgehammerDestroy, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        StopFire(116, 1, 2, GameServer::receiveStopFire, GameClient::receiveStopFire, (CallbackClientProcess)null), 
        Cataplasm(117, 1, 2, GameServer::receiveCataplasm, GameClient::receiveCataplasm, (CallbackClientProcess)null), 
        AddAlarm(118, 1, 2, (CallbackServerProcess)null, GameClient::receiveAddAlarm, (CallbackClientProcess)null), 
        PlaySoundEveryPlayer(119, 1, 2, (CallbackServerProcess)null, GameClient::receivePlaySoundEveryPlayer, (CallbackClientProcess)null), 
        SyncFurnace(120, 1, 2, GameServer::receiveSyncFurnace, GameClient::receiveSyncFurnace, (CallbackClientProcess)null), 
        SendCustomColor(121, 1, 2, GameServer::receiveSendCustomColor, GameClient::receiveSendCustomColor, (CallbackClientProcess)null), 
        SyncCompost(122, 1, 2, GameServer::receiveSyncCompost, GameClient::receiveSyncCompost, (CallbackClientProcess)null), 
        ChangePlayerStats(123, 1, 2, GameServer::receiveChangePlayerStats, GameClient::receiveChangePlayerStats, (CallbackClientProcess)null), 
        AddXpFromPlayerStatsUI(124, 1, 2, GameServer::receiveAddXpFromPlayerStatsUI, GameClient::receiveAddXpFromPlayerStatsUI, (CallbackClientProcess)null), 
        SyncXP(126, 1, 2, GameServer::receiveSyncXP, GameClient::receiveSyncXP, (CallbackClientProcess)null), 
        PacketTypeShort(127, 1, 2, GameServer::receivePacketTypeShort, GameClient::receivePacketTypeShort, (CallbackClientProcess)null), 
        Userlog(128, 1, 2, GameServer::receiveUserlog, GameClient::receiveUserlog, (CallbackClientProcess)null), 
        AddUserlog(129, 1, 2, GameServer::receiveAddUserlog, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        RemoveUserlog(130, 1, 2, GameServer::receiveRemoveUserlog, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        AddWarningPoint(131, 1, 2, GameServer::receiveAddWarningPoint, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        MessageForAdmin(132, 1, 2, (CallbackServerProcess)null, GameClient::receiveMessageForAdmin, (CallbackClientProcess)null), 
        WakeUpPlayer(133, 1, 2, GameServer::receiveWakeUpPlayer, GameClient::receiveWakeUpPlayer, (CallbackClientProcess)null), 
        @Deprecated
        SendTransactionID(134, 1, 2, (CallbackServerProcess)null, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        GetDBSchema(135, 1, 2, GameServer::receiveGetDBSchema, GameClient::receiveGetDBSchema, (CallbackClientProcess)null), 
        GetTableResult(136, 1, 2, GameServer::receiveGetTableResult, GameClient::receiveGetTableResult, (CallbackClientProcess)null), 
        ExecuteQuery(137, 1, 2, GameServer::receiveExecuteQuery, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        ChangeTextColor(138, 1, 2, GameServer::receiveChangeTextColor, GameClient::receiveChangeTextColor, (CallbackClientProcess)null), 
        SyncNonPvpZone(139, 1, 2, GameServer::receiveSyncNonPvpZone, GameClient::receiveSyncNonPvpZone, (CallbackClientProcess)null), 
        SyncFaction(140, 1, 2, GameServer::receiveSyncFaction, GameClient::receiveSyncFaction, (CallbackClientProcess)null), 
        SendFactionInvite(141, 1, 2, GameServer::receiveSendFactionInvite, GameClient::receiveSendFactionInvite, (CallbackClientProcess)null), 
        AcceptedFactionInvite(142, 1, 2, GameServer::receiveAcceptedFactionInvite, GameClient::receiveAcceptedFactionInvite, (CallbackClientProcess)null), 
        AddTicket(143, 1, 2, GameServer::receiveAddTicket, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        ViewTickets(144, 1, 2, GameServer::receiveViewTickets, GameClient::receiveViewTickets, (CallbackClientProcess)null), 
        RemoveTicket(145, 1, 2, GameServer::receiveRemoveTicket, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        RequestTrading(146, 1, 2, GameServer::receiveRequestTrading, GameClient::receiveRequestTrading, (CallbackClientProcess)null), 
        TradingUIAddItem(147, 1, 2, GameServer::receiveTradingUIAddItem, GameClient::receiveTradingUIAddItem, (CallbackClientProcess)null), 
        TradingUIRemoveItem(148, 1, 2, GameServer::receiveTradingUIRemoveItem, GameClient::receiveTradingUIRemoveItem, (CallbackClientProcess)null), 
        TradingUIUpdateState(149, 1, 2, GameServer::receiveTradingUIUpdateState, GameClient::receiveTradingUIUpdateState, (CallbackClientProcess)null), 
        SendItemListNet(150, 1, 2, GameServer::receiveSendItemListNet, GameClient::receiveSendItemListNet, (CallbackClientProcess)null), 
        ChunkObjectState(151, 1, 2, GameServer::receiveChunkObjectState, GameClient::receiveChunkObjectState, (CallbackClientProcess)null), 
        ReadAnnotedMap(152, 1, 2, GameServer::receiveReadAnnotedMap, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        RequestInventory(153, 1, 2, GameServer::receiveRequestInventory, GameClient::receiveRequestInventory, (CallbackClientProcess)null), 
        SendInventory(154, 1, 2, GameServer::receiveSendInventory, GameClient::receiveSendInventory, (CallbackClientProcess)null), 
        InvMngReqItem(155, 1, 2, GameServer::receiveInvMngReqItem, GameClient::receiveInvMngReqItem, (CallbackClientProcess)null), 
        InvMngGetItem(156, 1, 2, GameServer::receiveInvMngGetItem, GameClient::receiveInvMngGetItem, (CallbackClientProcess)null), 
        InvMngRemoveItem(157, 1, 2, GameServer::receiveInvMngRemoveItem, GameClient::receiveInvMngRemoveItem, (CallbackClientProcess)null), 
        StartPause(158, 1, 3, (CallbackServerProcess)null, GameClient::receiveStartPause, (CallbackClientProcess)null), 
        StopPause(159, 1, 3, (CallbackServerProcess)null, GameClient::receiveStopPause, (CallbackClientProcess)null), 
        TimeSync(160, 1, 2, GameServer::receiveTimeSync, GameClient::receiveTimeSync, (CallbackClientProcess)null), 
        SyncIsoObjectReq(161, 1, 2, GameServer::receiveSyncIsoObjectReq, GameClient::receiveSyncIsoObjectReq, (CallbackClientProcess)null), 
        PlayerSave(162, 1, 2, GameServer::receivePlayerSave, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        SyncWorldObjectsReq(163, 1, 2, (CallbackServerProcess)null, GameClient::receiveSyncWorldObjectsReq, (CallbackClientProcess)null), 
        SyncObjects(164, 1, 2, GameServer::receiveSyncObjects, GameClient::receiveSyncObjects, (CallbackClientProcess)null), 
        SendPlayerProfile(166, 1, 3, GameServer::receiveSendPlayerProfile, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        LoadPlayerProfile(167, 1, 3, GameServer::receiveLoadPlayerProfile, GameClient::receiveLoadPlayerProfile, (CallbackClientProcess)null), 
        SpawnRegion(171, 1, 2, (CallbackServerProcess)null, GameClient::receiveSpawnRegion, GameClient::receiveSpawnRegion), 
        PlayerDamageFromCarCrash(172, 1, 2, (CallbackServerProcess)null, GameClient::receivePlayerDamageFromCarCrash, (CallbackClientProcess)null), 
        PlayerAttachedItem(173, 1, 2, GameServer::receivePlayerAttachedItem, GameClient::receivePlayerAttachedItem, (CallbackClientProcess)null), 
        ZombieHelmetFalling(174, 1, 2, GameServer::receiveZombieHelmetFalling, GameClient::receiveZombieHelmetFalling, (CallbackClientProcess)null), 
        AddBrokenGlass(175, 1, 2, (CallbackServerProcess)null, GameClient::receiveAddBrokenGlass, (CallbackClientProcess)null), 
        SyncPerks(177, 1, 2, GameServer::receiveSyncPerks, GameClient::receiveSyncPerks, (CallbackClientProcess)null), 
        SyncWeight(178, 1, 2, GameServer::receiveSyncWeight, GameClient::receiveSyncWeight, (CallbackClientProcess)null), 
        SyncInjuries(179, 1, 2, GameServer::receiveSyncInjuries, GameClient::receiveSyncInjuries, (CallbackClientProcess)null), 
        SyncEquippedRadioFreq(181, 1, 2, GameServer::receiveSyncEquippedRadioFreq, GameClient::receiveSyncEquippedRadioFreq, (CallbackClientProcess)null), 
        InitPlayerChat(182, 1, 2, (CallbackServerProcess)null, GameClient::receiveInitPlayerChat, (CallbackClientProcess)null), 
        PlayerJoinChat(183, 1, 2, (CallbackServerProcess)null, GameClient::receivePlayerJoinChat, (CallbackClientProcess)null), 
        PlayerLeaveChat(184, 1, 2, (CallbackServerProcess)null, GameClient::receivePlayerLeaveChat, (CallbackClientProcess)null), 
        ChatMessageFromPlayer(185, 1, 2, GameServer::receiveChatMessageFromPlayer, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        ChatMessageToPlayer(186, 1, 2, (CallbackServerProcess)null, GameClient::receiveChatMessageToPlayer, (CallbackClientProcess)null), 
        PlayerStartPMChat(187, 1, 2, GameServer::receivePlayerStartPMChat, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        AddChatTab(189, 1, 2, (CallbackServerProcess)null, GameClient::receiveAddChatTab, (CallbackClientProcess)null), 
        RemoveChatTab(190, 1, 2, (CallbackServerProcess)null, GameClient::receiveRemoveChatTab, (CallbackClientProcess)null), 
        PlayerConnectedToChat(191, 1, 2, (CallbackServerProcess)null, GameClient::receivePlayerConnectedToChat, (CallbackClientProcess)null), 
        PlayerNotFound(192, 1, 2, (CallbackServerProcess)null, GameClient::receivePlayerNotFound, (CallbackClientProcess)null), 
        SendSafehouseInvite(193, 1, 2, GameServer::receiveSendSafehouseInvite, GameClient::receiveSendSafehouseInvite, (CallbackClientProcess)null), 
        AcceptedSafehouseInvite(194, 1, 2, GameServer::receiveAcceptedSafehouseInvite, GameClient::receiveAcceptedSafehouseInvite, (CallbackClientProcess)null), 
        ClimateManagerPacket(200, 1, 2, GameServer::receiveClimateManagerPacket, GameClient::receiveClimateManagerPacket, (CallbackClientProcess)null), 
        IsoRegionServerPacket(201, 1, 2, (CallbackServerProcess)null, GameClient::receiveIsoRegionServerPacket, (CallbackClientProcess)null), 
        IsoRegionClientRequestFullUpdate(202, 1, 2, GameServer::receiveIsoRegionClientRequestFullUpdate, (CallbackClientProcess)null, (CallbackClientProcess)null), 
        EventPacket(210, 0, 3, GameServer::receiveEventPacket, GameClient::receiveEventPacket, (CallbackClientProcess)null), 
        Statistic(211, 1, 0, GameServer::receiveStatistic, GameClient::receiveStatistic, (CallbackClientProcess)null), 
        StatisticRequest(212, 1, 2, GameServer::receiveStatisticRequest, GameClient::receiveStatisticRequest, (CallbackClientProcess)null), 
        PlayerUpdateReliable(213, 0, 2, GameServer::receivePlayerUpdate, GameClient::receivePlayerUpdate, (CallbackClientProcess)null), 
        ActionPacket(214, 1, 3, GameServer::receiveActionPacket, GameClient::receiveActionPacket, (CallbackClientProcess)null), 
        ZombieControl(215, 0, 2, (CallbackServerProcess)null, GameClient::receiveZombieControl, (CallbackClientProcess)null), 
        PlayWorldSound(216, 1, 2, GameServer::receivePlayWorldSound, GameClient::receivePlayWorldSound, (CallbackClientProcess)null), 
        StopSound(217, 1, 2, GameServer::receiveStopSound, GameClient::receiveStopSound, (CallbackClientProcess)null), 
        PlayerUpdate(218, 2, 0, GameServer::receivePlayerUpdate, GameClient::receivePlayerUpdate, (CallbackClientProcess)null), 
        ZombieSimulation(219, 2, 0, GameServer::receiveZombieSimulation, GameClient::receiveZombieSimulation, (CallbackClientProcess)null), 
        PingFromClient(220, 1, 0, GameServer::receivePingFromClient, GameClient::receivePingFromClient, (CallbackClientProcess)null), 
        ZombieSimulationReliable(221, 0, 2, GameServer::receiveZombieSimulation, GameClient::receiveZombieSimulation, (CallbackClientProcess)null), 
        EatBody(222, 1, 2, GameServer::receiveEatBody, GameClient::receiveEatBody, (CallbackClientProcess)null), 
        Thump(223, 1, 2, GameServer::receiveThump, GameClient::receiveThump, (CallbackClientProcess)null), 
        GlobalModData(32000, 0, 2, GameServer::receiveGlobalModData, GameClient::receiveGlobalModData, (CallbackClientProcess)null), 
        GlobalModDataRequest(32001, 0, 2, GameServer::receiveGlobalModDataRequest, (CallbackClientProcess)null, (CallbackClientProcess)null);
        
        private final short id;
        public int PacketPriority;
        public int PacketReliability;
        public byte OrderingChannel;
        CallbackServerProcess serverProcess;
        CallbackClientProcess mainLoopHandlePacketInternal;
        CallbackClientProcess gameLoadingDealWithNetData;
        public int incomePackets;
        public int outcomePackets;
        public int incomeBytes;
        public int outcomeBytes;
        
        private PacketType(final int n2, final int n3, final int n4, final CallbackServerProcess callbackServerProcess, final CallbackClientProcess callbackClientProcess, final CallbackClientProcess callbackClientProcess2) {
            this(n2, n3, n4, 0, callbackServerProcess, callbackClientProcess, callbackClientProcess2);
        }
        
        private PacketType(final int n, final int packetPriority, final int packetReliability, final int n2, final CallbackServerProcess serverProcess, final CallbackClientProcess mainLoopHandlePacketInternal, final CallbackClientProcess gameLoadingDealWithNetData) {
            this.id = (short)n;
            this.PacketPriority = packetPriority;
            this.PacketReliability = packetReliability;
            this.OrderingChannel = (byte)n2;
            this.serverProcess = serverProcess;
            this.mainLoopHandlePacketInternal = mainLoopHandlePacketInternal;
            this.gameLoadingDealWithNetData = gameLoadingDealWithNetData;
            this.resetStatistics();
        }
        
        public void resetStatistics() {
            this.incomePackets = 0;
            this.outcomePackets = 0;
            this.incomeBytes = 0;
            this.outcomeBytes = 0;
        }
        
        public void send(final UdpConnection udpConnection) {
            udpConnection.endPacket(this.PacketPriority, this.PacketReliability, this.OrderingChannel);
        }
        
        public void doPacket(final ByteBufferWriter byteBufferWriter) {
            byteBufferWriter.putByte((byte)(-122));
            byteBufferWriter.putShort(this.getId());
        }
        
        public short getId() {
            return this.id;
        }
        
        public void onServerPacket(final ByteBuffer byteBuffer, final UdpConnection udpConnection, final short n) throws Exception {
            this.serverProcess.call(byteBuffer, udpConnection, n);
        }
        
        public void onMainLoopHandlePacketInternal(final ByteBuffer byteBuffer, final short n) throws IOException {
            this.mainLoopHandlePacketInternal.call(byteBuffer, n);
        }
        
        public boolean onGameLoadingDealWithNetData(final ByteBuffer byteBuffer, final short n) {
            if (this.gameLoadingDealWithNetData == null) {
                if (Core.bDebug) {
                    DebugLog.log(DebugType.Network, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name()));
                }
                return false;
            }
            try {
                this.gameLoadingDealWithNetData.call(byteBuffer, n);
                return true;
            }
            catch (Exception ex) {
                return false;
            }
        }
        
        private static /* synthetic */ PacketType[] $values() {
            return new PacketType[] { PacketType.ServerPulse, PacketType.Login, PacketType.HumanVisual, PacketType.KeepAlive, PacketType.Vehicles, PacketType.PlayerConnect, PacketType.VehiclesUnreliable, PacketType.MetaGrid, PacketType.Helicopter, PacketType.SyncIsoObject, PacketType.PlayerTimeout, PacketType.SteamGeneric, PacketType.ServerMap, PacketType.PassengerMap, PacketType.AddItemToMap, PacketType.SentChunk, PacketType.SyncClock, PacketType.AddInventoryItemToContainer, PacketType.ConnectionDetails, PacketType.RemoveInventoryItemFromContainer, PacketType.RemoveItemFromSquare, PacketType.RequestLargeAreaZip, PacketType.Equip, PacketType.HitCharacter, PacketType.AddCoopPlayer, PacketType.WeaponHit, PacketType.KillZombie, PacketType.SandboxOptions, PacketType.SmashWindow, PacketType.PlayerDeath, PacketType.RequestZipList, PacketType.ItemStats, PacketType.NotRequiredInZip, PacketType.RequestData, PacketType.GlobalObjects, PacketType.ZombieDeath, PacketType.AccessDenied, PacketType.PlayerDamage, PacketType.Bandage, PacketType.EatFood, PacketType.RequestItemsForContainer, PacketType.Drink, PacketType.SyncAlarmClock, PacketType.PacketCounts, PacketType.SendModData, PacketType.RemoveContestedItemsFromInventory, PacketType.ScoreboardUpdate, PacketType.ReceiveModData, PacketType.ServerQuit, PacketType.PlaySound, PacketType.WorldSound, PacketType.AddAmbient, PacketType.SyncClothing, PacketType.ClientCommand, PacketType.ObjectModData, PacketType.ObjectChange, PacketType.BloodSplatter, PacketType.ZombieSound, PacketType.ZombieDescriptors, PacketType.SlowFactor, PacketType.Weather, PacketType.RequestPlayerData, PacketType.RemoveCorpseFromMap, PacketType.AddCorpseToMap, PacketType.StartFire, PacketType.UpdateItemSprite, PacketType.StartRain, PacketType.StopRain, PacketType.WorldMessage, PacketType.getModData, PacketType.ReceiveCommand, PacketType.ReloadOptions, PacketType.Kicked, PacketType.ExtraInfo, PacketType.AddItemInInventory, PacketType.ChangeSafety, PacketType.Ping, PacketType.WriteLog, PacketType.AddXP, PacketType.UpdateOverlaySprite, PacketType.Checksum, PacketType.ConstructedZone, PacketType.RegisterZone, PacketType.WoundInfection, PacketType.Stitch, PacketType.Disinfect, PacketType.AdditionalPain, PacketType.RemoveGlass, PacketType.Splint, PacketType.RemoveBullet, PacketType.CleanBurn, PacketType.SyncThumpable, PacketType.SyncDoorKey, PacketType.AddXpCommand, PacketType.Teleport, PacketType.RemoveBlood, PacketType.AddExplosiveTrap, PacketType.BodyDamageUpdate, PacketType.SyncSafehouse, PacketType.SledgehammerDestroy, PacketType.StopFire, PacketType.Cataplasm, PacketType.AddAlarm, PacketType.PlaySoundEveryPlayer, PacketType.SyncFurnace, PacketType.SendCustomColor, PacketType.SyncCompost, PacketType.ChangePlayerStats, PacketType.AddXpFromPlayerStatsUI, PacketType.SyncXP, PacketType.PacketTypeShort, PacketType.Userlog, PacketType.AddUserlog, PacketType.RemoveUserlog, PacketType.AddWarningPoint, PacketType.MessageForAdmin, PacketType.WakeUpPlayer, PacketType.SendTransactionID, PacketType.GetDBSchema, PacketType.GetTableResult, PacketType.ExecuteQuery, PacketType.ChangeTextColor, PacketType.SyncNonPvpZone, PacketType.SyncFaction, PacketType.SendFactionInvite, PacketType.AcceptedFactionInvite, PacketType.AddTicket, PacketType.ViewTickets, PacketType.RemoveTicket, PacketType.RequestTrading, PacketType.TradingUIAddItem, PacketType.TradingUIRemoveItem, PacketType.TradingUIUpdateState, PacketType.SendItemListNet, PacketType.ChunkObjectState, PacketType.ReadAnnotedMap, PacketType.RequestInventory, PacketType.SendInventory, PacketType.InvMngReqItem, PacketType.InvMngGetItem, PacketType.InvMngRemoveItem, PacketType.StartPause, PacketType.StopPause, PacketType.TimeSync, PacketType.SyncIsoObjectReq, PacketType.PlayerSave, PacketType.SyncWorldObjectsReq, PacketType.SyncObjects, PacketType.SendPlayerProfile, PacketType.LoadPlayerProfile, PacketType.SpawnRegion, PacketType.PlayerDamageFromCarCrash, PacketType.PlayerAttachedItem, PacketType.ZombieHelmetFalling, PacketType.AddBrokenGlass, PacketType.SyncPerks, PacketType.SyncWeight, PacketType.SyncInjuries, PacketType.SyncEquippedRadioFreq, PacketType.InitPlayerChat, PacketType.PlayerJoinChat, PacketType.PlayerLeaveChat, PacketType.ChatMessageFromPlayer, PacketType.ChatMessageToPlayer, PacketType.PlayerStartPMChat, PacketType.AddChatTab, PacketType.RemoveChatTab, PacketType.PlayerConnectedToChat, PacketType.PlayerNotFound, PacketType.SendSafehouseInvite, PacketType.AcceptedSafehouseInvite, PacketType.ClimateManagerPacket, PacketType.IsoRegionServerPacket, PacketType.IsoRegionClientRequestFullUpdate, PacketType.EventPacket, PacketType.Statistic, PacketType.StatisticRequest, PacketType.PlayerUpdateReliable, PacketType.ActionPacket, PacketType.ZombieControl, PacketType.PlayWorldSound, PacketType.StopSound, PacketType.PlayerUpdate, PacketType.ZombieSimulation, PacketType.PingFromClient, PacketType.ZombieSimulationReliable, PacketType.EatBody, PacketType.Thump, PacketType.GlobalModData, PacketType.GlobalModDataRequest };
        }
        
        static {
            $VALUES = $values();
        }
    }
    
    public interface CallbackServerProcess
    {
        void call(final ByteBuffer p0, final UdpConnection p1, final short p2) throws Exception;
    }
    
    public interface CallbackClientProcess
    {
        void call(final ByteBuffer p0, final short p1) throws IOException;
    }
}
