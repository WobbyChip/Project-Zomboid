// 
// Decompiled by Procyon v0.5.36
// 

package zombie.world;

import java.util.HashMap;
import zombie.inventory.InventoryItem;
import zombie.gameStates.ChooseGameInfo;
import zombie.world.logger.Log;
import java.util.Date;
import java.text.SimpleDateFormat;
import zombie.world.logger.WorldDictionaryLogger;
import zombie.GameWindow;
import zombie.network.GameServer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.erosion.categories.ErosionCategory;
import zombie.erosion.ErosionRegions;
import java.util.ArrayList;
import zombie.core.Core;
import zombie.network.GameClient;
import zombie.scripting.objects.Item;
import zombie.debug.DebugLog;
import java.util.List;
import java.util.Map;

public class WorldDictionary
{
    public static final String SAVE_FILE_READABLE = "WorldDictionaryReadable.lua";
    public static final String SAVE_FILE_LOG = "WorldDictionaryLog.lua";
    public static final String SAVE_FILE = "WorldDictionary";
    public static final String SAVE_EXT = ".bin";
    public static final boolean logUnset = false;
    public static final boolean logMissingObjectID = false;
    private static final Map<String, ItemInfo> itemLoadList;
    private static final List<String> objNameLoadList;
    private static DictionaryData data;
    private static boolean isNewGame;
    private static boolean allowScriptItemLoading;
    private static final String netValidator = "DICTIONARY_PACKET_END";
    private static byte[] clientRemoteData;
    
    protected static void log(final String s) {
        log(s, true);
    }
    
    protected static void log(final String s, final boolean b) {
        if (b) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
    }
    
    public static void setIsNewGame(final boolean isNewGame) {
        WorldDictionary.isNewGame = isNewGame;
    }
    
    public static boolean isIsNewGame() {
        return WorldDictionary.isNewGame;
    }
    
    public static void StartScriptLoading() {
        WorldDictionary.allowScriptItemLoading = true;
        WorldDictionary.itemLoadList.clear();
    }
    
    public static void ScriptsLoaded() {
        WorldDictionary.allowScriptItemLoading = false;
    }
    
    public static void onLoadItem(final Item scriptItem) {
        if (GameClient.bClient) {
            return;
        }
        if (!WorldDictionary.allowScriptItemLoading) {
            log("Warning script item loaded after WorldDictionary is initialised");
            if (Core.bDebug) {
                throw new RuntimeException("This shouldn't be happening.");
            }
        }
        ItemInfo itemInfo = WorldDictionary.itemLoadList.get(scriptItem.getFullName());
        if (itemInfo == null) {
            itemInfo = new ItemInfo();
            itemInfo.itemName = scriptItem.getName();
            itemInfo.moduleName = scriptItem.getModuleName();
            itemInfo.fullType = scriptItem.getFullName();
            WorldDictionary.itemLoadList.put(scriptItem.getFullName(), itemInfo);
        }
        if (itemInfo.modID != null && !scriptItem.getModID().equals(itemInfo.modID)) {
            if (itemInfo.modOverrides == null) {
                itemInfo.modOverrides = new ArrayList<String>();
            }
            if (!itemInfo.modOverrides.contains(itemInfo.modID)) {
                itemInfo.modOverrides.add(itemInfo.modID);
            }
            else {
                log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, itemInfo.fullType, itemInfo.modID));
            }
        }
        itemInfo.modID = scriptItem.getModID();
        if (itemInfo.modID.equals("pz-vanilla")) {
            itemInfo.existsAsVanilla = true;
        }
        itemInfo.isModded = !itemInfo.modID.equals("pz-vanilla");
        itemInfo.obsolete = scriptItem.getObsolete();
        itemInfo.scriptItem = scriptItem;
    }
    
    private static void collectObjectNames() {
        WorldDictionary.objNameLoadList.clear();
        if (GameClient.bClient) {
            return;
        }
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < ErosionRegions.regions.size(); ++i) {
            for (int j = 0; j < ErosionRegions.regions.get(i).categories.size(); ++j) {
                final ErosionCategory erosionCategory = ErosionRegions.regions.get(i).categories.get(j);
                list.clear();
                erosionCategory.getObjectNames(list);
                for (final String s : list) {
                    if (!WorldDictionary.objNameLoadList.contains(s)) {
                        WorldDictionary.objNameLoadList.add(s);
                    }
                }
            }
        }
    }
    
    public static void loadDataFromServer(final ByteBuffer byteBuffer) throws IOException {
        if (GameClient.bClient) {
            byteBuffer.get(WorldDictionary.clientRemoteData = new byte[byteBuffer.getInt()], 0, WorldDictionary.clientRemoteData.length);
        }
    }
    
    public static void saveDataForClient(final ByteBuffer byteBuffer) throws IOException {
        if (GameServer.bServer) {
            final int position = byteBuffer.position();
            byteBuffer.putInt(0);
            final int position2 = byteBuffer.position();
            if (WorldDictionary.data.serverDataCache != null) {
                byteBuffer.put(WorldDictionary.data.serverDataCache);
            }
            else {
                if (Core.bDebug) {
                    throw new RuntimeException("Should be sending data from the serverDataCache here.");
                }
                WorldDictionary.data.saveToByteBuffer(byteBuffer);
            }
            GameWindow.WriteString(byteBuffer, "DICTIONARY_PACKET_END");
            final int position3 = byteBuffer.position();
            byteBuffer.position(position);
            byteBuffer.putInt(position3 - position2);
            byteBuffer.position(position3);
        }
    }
    
    public static void init() throws WorldDictionaryException {
        boolean b = true;
        collectObjectNames();
        WorldDictionaryLogger.startLogging();
        WorldDictionaryLogger.log("-------------------------------------------------------", false);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        WorldDictionaryLogger.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, simpleDateFormat.format(new Date())), false);
        log("Checking dictionary...");
        Log.Info info = null;
        try {
            if (!GameClient.bClient) {
                if (WorldDictionary.data == null || WorldDictionary.data.isClient()) {
                    WorldDictionary.data = new DictionaryData();
                }
            }
            else if (WorldDictionary.data == null || !WorldDictionary.data.isClient()) {
                WorldDictionary.data = new DictionaryDataClient();
            }
            WorldDictionary.data.reset();
            if (GameClient.bClient) {
                if (WorldDictionary.clientRemoteData == null) {
                    throw new WorldDictionaryException("WorldDictionary data not received from server.");
                }
                final ByteBuffer wrap = ByteBuffer.wrap(WorldDictionary.clientRemoteData);
                WorldDictionary.data.loadFromByteBuffer(wrap);
                if (!GameWindow.ReadString(wrap).equals("DICTIONARY_PACKET_END")) {
                    throw new WorldDictionaryException("WorldDictionary data received from server is corrupt.");
                }
                WorldDictionary.clientRemoteData = null;
            }
            WorldDictionary.data.backupCurrentDataSet();
            WorldDictionary.data.load();
            final ArrayList<String> list = new ArrayList<String>();
            info = new Log.Info(simpleDateFormat.format(new Date()), Core.GameSaveWorld, 186, list);
            WorldDictionaryLogger.log(info);
            WorldDictionary.data.parseItemLoadList(WorldDictionary.itemLoadList);
            WorldDictionary.data.parseCurrentItemSet();
            WorldDictionary.itemLoadList.clear();
            WorldDictionary.data.parseObjectNameLoadList(WorldDictionary.objNameLoadList);
            WorldDictionary.objNameLoadList.clear();
            WorldDictionary.data.getItemMods(list);
            WorldDictionary.data.saveAsText("WorldDictionaryReadable.lua");
            WorldDictionary.data.save();
            WorldDictionary.data.deleteBackupCurrentDataSet();
        }
        catch (Exception ex) {
            b = false;
            ex.printStackTrace();
            log("Warning: error occurred loading dictionary!");
            if (info != null) {
                info.HasErrored = true;
            }
            if (WorldDictionary.data != null) {
                WorldDictionary.data.createErrorBackups();
            }
        }
        try {
            WorldDictionaryLogger.saveLog("WorldDictionaryLog.lua");
            WorldDictionaryLogger.reset();
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
        if (!b) {
            throw new WorldDictionaryException("WorldDictionary: Cannot load world due to WorldDictionary error.");
        }
    }
    
    public static void onWorldLoaded() {
    }
    
    public static ItemInfo getItemInfoFromType(final String s) {
        return WorldDictionary.data.getItemInfoFromType(s);
    }
    
    public static ItemInfo getItemInfoFromID(final short n) {
        return WorldDictionary.data.getItemInfoFromID(n);
    }
    
    public static short getItemRegistryID(final String s) {
        return WorldDictionary.data.getItemRegistryID(s);
    }
    
    public static String getItemTypeFromID(final short n) {
        return WorldDictionary.data.getItemTypeFromID(n);
    }
    
    public static String getItemTypeDebugString(final short n) {
        return WorldDictionary.data.getItemTypeDebugString(n);
    }
    
    public static String getSpriteNameFromID(final int n) {
        return WorldDictionary.data.getSpriteNameFromID(n);
    }
    
    public static int getIdForSpriteName(final String s) {
        return WorldDictionary.data.getIdForSpriteName(s);
    }
    
    public static String getObjectNameFromID(final byte b) {
        return WorldDictionary.data.getObjectNameFromID(b);
    }
    
    public static byte getIdForObjectName(final String s) {
        return WorldDictionary.data.getIdForObjectName(s);
    }
    
    public static String getItemModID(final short n) {
        final ItemInfo itemInfoFromID = getItemInfoFromID(n);
        if (itemInfoFromID != null) {
            return itemInfoFromID.modID;
        }
        return null;
    }
    
    public static String getItemModID(final String s) {
        final ItemInfo itemInfoFromType = getItemInfoFromType(s);
        if (itemInfoFromType != null) {
            return itemInfoFromType.modID;
        }
        return null;
    }
    
    public static String getModNameFromID(final String s) {
        if (s != null) {
            if (s.equals("pz-vanilla")) {
                return "Project Zomboid";
            }
            final ChooseGameInfo.Mod modDetails = ChooseGameInfo.getModDetails(s);
            if (modDetails != null && modDetails.getName() != null) {
                return modDetails.getName();
            }
        }
        return "Unknown mod";
    }
    
    public static void DebugPrintItem(final InventoryItem inventoryItem) {
        final Item scriptItem = inventoryItem.getScriptItem();
        if (scriptItem != null) {
            DebugPrintItem(scriptItem);
        }
        else {
            final String fullType = inventoryItem.getFullType();
            ItemInfo itemInfo = null;
            if (fullType != null) {
                itemInfo = getItemInfoFromType(fullType);
            }
            if (itemInfo == null && inventoryItem.getRegistry_id() >= 0) {
                itemInfo = getItemInfoFromID(inventoryItem.getRegistry_id());
            }
            if (itemInfo != null) {
                itemInfo.DebugPrint();
            }
            else {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (fullType != null) ? fullType : "unknown"));
            }
        }
    }
    
    public static void DebugPrintItem(final Item item) {
        final String fullName = item.getFullName();
        ItemInfo itemInfo = null;
        if (fullName != null) {
            itemInfo = getItemInfoFromType(fullName);
        }
        if (itemInfo == null && item.getRegistry_id() >= 0) {
            itemInfo = getItemInfoFromID(item.getRegistry_id());
        }
        if (itemInfo != null) {
            itemInfo.DebugPrint();
        }
        else {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (fullName != null) ? fullName : "unknown"));
        }
    }
    
    public static void DebugPrintItem(final String s) {
        final ItemInfo itemInfoFromType = getItemInfoFromType(s);
        if (itemInfoFromType != null) {
            itemInfoFromType.DebugPrint();
        }
        else {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
    }
    
    public static void DebugPrintItem(final short n) {
        final ItemInfo itemInfoFromID = getItemInfoFromID(n);
        if (itemInfoFromID != null) {
            itemInfoFromID.DebugPrint();
        }
        else {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(S)Ljava/lang/String;, n));
        }
    }
    
    static {
        itemLoadList = new HashMap<String, ItemInfo>();
        objNameLoadList = new ArrayList<String>();
        WorldDictionary.isNewGame = true;
        WorldDictionary.allowScriptItemLoading = false;
    }
}
