// 
// Decompiled by Procyon v0.5.36
// 

package zombie.world;

import java.io.FileWriter;
import java.io.FileOutputStream;
import zombie.network.GameServer;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import java.io.FileInputStream;
import java.io.IOException;
import com.google.common.io.Files;
import java.time.Instant;
import zombie.ZomboidFileSystem;
import zombie.scripting.ScriptManager;
import zombie.world.logger.WorldDictionaryLogger;
import zombie.world.logger.Log;
import java.util.Iterator;
import java.util.List;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.debug.DebugLog;
import zombie.core.Core;
import java.util.HashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class DictionaryData
{
    protected final Map<Short, ItemInfo> itemIdToInfoMap;
    protected final Map<String, ItemInfo> itemTypeToInfoMap;
    protected final Map<String, Integer> spriteNameToIdMap;
    protected final Map<Integer, String> spriteIdToNameMap;
    protected final Map<String, Byte> objectNameToIdMap;
    protected final Map<Byte, String> objectIdToNameMap;
    protected final ArrayList<String> unsetObject;
    protected final ArrayList<String> unsetSprites;
    protected short NextItemID;
    protected int NextSpriteNameID;
    protected byte NextObjectNameID;
    protected byte[] serverDataCache;
    private File dataBackupPath;
    
    public DictionaryData() {
        this.itemIdToInfoMap = new HashMap<Short, ItemInfo>();
        this.itemTypeToInfoMap = new HashMap<String, ItemInfo>();
        this.spriteNameToIdMap = new HashMap<String, Integer>();
        this.spriteIdToNameMap = new HashMap<Integer, String>();
        this.objectNameToIdMap = new HashMap<String, Byte>();
        this.objectIdToNameMap = new HashMap<Byte, String>();
        this.unsetObject = new ArrayList<String>();
        this.unsetSprites = new ArrayList<String>();
        this.NextItemID = 0;
        this.NextSpriteNameID = 0;
        this.NextObjectNameID = 0;
    }
    
    protected boolean isClient() {
        return false;
    }
    
    protected void reset() {
        this.NextItemID = 0;
        this.NextSpriteNameID = 0;
        this.NextObjectNameID = 0;
        this.itemIdToInfoMap.clear();
        this.itemTypeToInfoMap.clear();
        this.objectIdToNameMap.clear();
        this.objectNameToIdMap.clear();
        this.spriteIdToNameMap.clear();
        this.spriteNameToIdMap.clear();
    }
    
    protected final ItemInfo getItemInfoFromType(final String s) {
        return this.itemTypeToInfoMap.get(s);
    }
    
    protected final ItemInfo getItemInfoFromID(final short s) {
        return this.itemIdToInfoMap.get(s);
    }
    
    protected final short getItemRegistryID(final String s) {
        final ItemInfo itemInfo = this.itemTypeToInfoMap.get(s);
        if (itemInfo != null) {
            return itemInfo.registryID;
        }
        if (Core.bDebug) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, s));
        }
        return -1;
    }
    
    protected final String getItemTypeFromID(final short s) {
        final ItemInfo itemInfo = this.itemIdToInfoMap.get(s);
        if (itemInfo != null) {
            return itemInfo.fullType;
        }
        return null;
    }
    
    protected final String getItemTypeDebugString(final short n) {
        String itemTypeFromID = this.getItemTypeFromID(n);
        if (itemTypeFromID == null) {
            itemTypeFromID = "Unknown";
        }
        return itemTypeFromID;
    }
    
    protected final String getSpriteNameFromID(final int n) {
        if (n >= 0) {
            if (this.spriteIdToNameMap.containsKey(n)) {
                return this.spriteIdToNameMap.get(n);
            }
            final IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, n);
            if (sprite != null && sprite.name != null) {
                return sprite.name;
            }
        }
        DebugLog.log(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, n));
        return null;
    }
    
    protected final int getIdForSpriteName(final String anObject) {
        if (anObject != null) {
            if (this.spriteNameToIdMap.containsKey(anObject)) {
                return this.spriteNameToIdMap.get(anObject);
            }
            final IsoSprite sprite = IsoSpriteManager.instance.getSprite(anObject);
            if (sprite != null && sprite.ID >= 0 && sprite.ID != 20000000 && sprite.name.equals(anObject)) {
                return sprite.ID;
            }
        }
        return -1;
    }
    
    protected final String getObjectNameFromID(final byte b) {
        if (b >= 0) {
            if (this.objectIdToNameMap.containsKey(b)) {
                return this.objectIdToNameMap.get(b);
            }
            if (Core.bDebug) {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, b));
            }
        }
        return null;
    }
    
    protected final byte getIdForObjectName(final String s) {
        if (s != null) {
            if (this.objectNameToIdMap.containsKey(s)) {
                return this.objectNameToIdMap.get(s);
            }
            if (Core.bDebug) {}
        }
        return -1;
    }
    
    protected final void getItemMods(final List<String> list) {
        list.clear();
        for (final Map.Entry<Short, ItemInfo> entry : this.itemIdToInfoMap.entrySet()) {
            if (!list.contains(entry.getValue().modID)) {
                list.add(entry.getValue().modID);
            }
            if (entry.getValue().modOverrides != null) {
                final List<String> modOverrides = entry.getValue().modOverrides;
                for (int i = 0; i < modOverrides.size(); ++i) {
                    if (!list.contains(modOverrides.get(i))) {
                        list.add(modOverrides.get(i));
                    }
                }
            }
        }
    }
    
    protected final void getModuleList(final List<String> list) {
        for (final Map.Entry<Short, ItemInfo> entry : this.itemIdToInfoMap.entrySet()) {
            if (!list.contains(entry.getValue().moduleName)) {
                list.add(entry.getValue().moduleName);
            }
        }
    }
    
    protected void parseItemLoadList(final Map<String, ItemInfo> map) throws WorldDictionaryException {
        final Iterator<Map.Entry<String, ItemInfo>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            final ItemInfo itemInfo = iterator.next().getValue();
            final ItemInfo itemInfo2 = this.itemTypeToInfoMap.get(itemInfo.fullType);
            if (itemInfo2 == null) {
                if (itemInfo.obsolete) {
                    continue;
                }
                if (this.NextItemID >= 32767) {
                    throw new WorldDictionaryException("Max item ID value reached for WorldDictionary!");
                }
                final ItemInfo itemInfo3 = itemInfo;
                final short nextItemID = this.NextItemID;
                this.NextItemID = (short)(nextItemID + 1);
                itemInfo3.registryID = nextItemID;
                itemInfo.isLoaded = true;
                this.itemTypeToInfoMap.put(itemInfo.fullType, itemInfo);
                this.itemIdToInfoMap.put(itemInfo.registryID, itemInfo);
                WorldDictionaryLogger.log(new Log.RegisterItem(itemInfo.copy()));
            }
            else {
                if (itemInfo2.removed && !itemInfo.obsolete) {
                    itemInfo2.removed = false;
                    WorldDictionaryLogger.log(new Log.ReinstateItem(itemInfo2.copy()));
                }
                if (!itemInfo2.modID.equals(itemInfo.modID)) {
                    final String modID = itemInfo2.modID;
                    itemInfo2.modID = itemInfo.modID;
                    itemInfo2.isModded = !itemInfo.modID.equals("pz-vanilla");
                    WorldDictionaryLogger.log(new Log.ModIDChangedItem(itemInfo2.copy(), modID, itemInfo2.modID));
                }
                if (itemInfo.obsolete && (!itemInfo2.obsolete || !itemInfo2.removed)) {
                    itemInfo2.obsolete = true;
                    itemInfo2.removed = true;
                    WorldDictionaryLogger.log(new Log.ObsoleteItem(itemInfo2.copy()));
                }
                itemInfo2.isLoaded = true;
            }
        }
    }
    
    protected void parseCurrentItemSet() throws WorldDictionaryException {
        final Iterator<Map.Entry<String, ItemInfo>> iterator = this.itemTypeToInfoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            final ItemInfo itemInfo = iterator.next().getValue();
            if (!itemInfo.isLoaded) {
                itemInfo.removed = true;
                WorldDictionaryLogger.log(new Log.RemovedItem(itemInfo.copy(), false));
            }
            if (itemInfo.scriptItem == null) {
                itemInfo.scriptItem = ScriptManager.instance.getItem(itemInfo.fullType);
            }
            if (itemInfo.scriptItem != null) {
                itemInfo.scriptItem.setRegistry_id(itemInfo.registryID);
            }
            else {
                itemInfo.removed = true;
                WorldDictionaryLogger.log(new Log.RemovedItem(itemInfo.copy(), true));
            }
        }
    }
    
    protected void parseObjectNameLoadList(final List<String> list) throws WorldDictionaryException {
        for (int i = 0; i < list.size(); ++i) {
            final String s = list.get(i);
            if (!this.objectNameToIdMap.containsKey(s)) {
                if (this.NextObjectNameID >= 127) {
                    WorldDictionaryLogger.log("Max value for object names reached.");
                    if (Core.bDebug) {
                        throw new WorldDictionaryException("Max value for object names reached.");
                    }
                }
                else {
                    final byte nextObjectNameID = this.NextObjectNameID;
                    this.NextObjectNameID = (byte)(nextObjectNameID + 1);
                    final byte b = nextObjectNameID;
                    this.objectIdToNameMap.put(b, s);
                    this.objectNameToIdMap.put(s, b);
                    WorldDictionaryLogger.log(new Log.RegisterObject(s, b));
                }
            }
        }
    }
    
    protected void backupCurrentDataSet() throws IOException {
        this.dataBackupPath = null;
        if (Core.getInstance().isNoSave()) {
            return;
        }
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator));
        if (file.exists()) {
            Files.copy(file, this.dataBackupPath = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator, Instant.now().getEpochSecond())));
        }
    }
    
    protected void deleteBackupCurrentDataSet() throws IOException {
        if (Core.getInstance().isNoSave()) {
            this.dataBackupPath = null;
            return;
        }
        if (this.dataBackupPath != null) {
            this.dataBackupPath.delete();
        }
        this.dataBackupPath = null;
    }
    
    protected void createErrorBackups() {
        if (Core.getInstance().isNoSave()) {
            return;
        }
        try {
            WorldDictionary.log("Attempting to copy WorldDictionary backups...");
            final String pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator, Instant.now().getEpochSecond(), File.separator);
            WorldDictionary.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
            final File file = new File(pathname);
            boolean mkdir = true;
            if (!file.exists()) {
                mkdir = file.mkdir();
            }
            if (!mkdir) {
                WorldDictionary.log("Could not create backup folder folder.");
                return;
            }
            if (this.dataBackupPath != null) {
                final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
                if (this.dataBackupPath.exists()) {
                    Files.copy(this.dataBackupPath, file2);
                }
            }
            final File file3 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator));
            final File file4 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
            if (file3.exists()) {
                Files.copy(file3, file4);
            }
            final File file5 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator));
            final File file6 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
            if (file5.exists()) {
                Files.copy(file5, file6);
            }
            final File file7 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator));
            final File file8 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
            if (file7.exists()) {
                Files.copy(file7, file8);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    protected void load() throws IOException, WorldDictionaryException {
        if (Core.getInstance().isNoSave()) {
            return;
        }
        final String pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator);
        final File file = new File(pathname);
        if (file.exists()) {
            try {
                final FileInputStream fileInputStream = new FileInputStream(file);
                try {
                    DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
                    final ByteBuffer allocate = ByteBuffer.allocate((int)file.length());
                    allocate.clear();
                    allocate.limit(fileInputStream.read(allocate.array()));
                    this.loadFromByteBuffer(allocate);
                    fileInputStream.close();
                }
                catch (Throwable t) {
                    try {
                        fileInputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                throw new WorldDictionaryException("Error loading WorldDictionary.", ex);
            }
            return;
        }
        if (!WorldDictionary.isIsNewGame()) {
            throw new WorldDictionaryException("WorldDictionary data file is missing from world folder.");
        }
    }
    
    protected void loadFromByteBuffer(final ByteBuffer byteBuffer) throws IOException {
        this.NextItemID = byteBuffer.getShort();
        this.NextObjectNameID = byteBuffer.get();
        this.NextSpriteNameID = byteBuffer.getInt();
        final ArrayList<String> list = new ArrayList<String>();
        for (int int1 = byteBuffer.getInt(), i = 0; i < int1; ++i) {
            list.add(GameWindow.ReadString(byteBuffer));
        }
        final ArrayList<String> list2 = new ArrayList<String>();
        for (int int2 = byteBuffer.getInt(), j = 0; j < int2; ++j) {
            list2.add(GameWindow.ReadString(byteBuffer));
        }
        for (int int3 = byteBuffer.getInt(), k = 0; k < int3; ++k) {
            final ItemInfo itemInfo = new ItemInfo();
            itemInfo.load(byteBuffer, 186, list, list2);
            this.itemIdToInfoMap.put(itemInfo.registryID, itemInfo);
            this.itemTypeToInfoMap.put(itemInfo.fullType, itemInfo);
        }
        for (int int4 = byteBuffer.getInt(), l = 0; l < int4; ++l) {
            final byte value = byteBuffer.get();
            final String readString = GameWindow.ReadString(byteBuffer);
            this.objectIdToNameMap.put(value, readString);
            this.objectNameToIdMap.put(readString, value);
        }
        for (int int5 = byteBuffer.getInt(), n = 0; n < int5; ++n) {
            final int int6 = byteBuffer.getInt();
            final String readString2 = GameWindow.ReadString(byteBuffer);
            this.spriteIdToNameMap.put(int6, readString2);
            this.spriteNameToIdMap.put(readString2, int6);
        }
    }
    
    protected void save() throws IOException, WorldDictionaryException {
        if (Core.getInstance().isNoSave()) {
            return;
        }
        try {
            final ByteBuffer wrap = ByteBuffer.wrap(new byte[5242880]);
            this.saveToByteBuffer(wrap);
            wrap.flip();
            if (GameServer.bServer) {
                final byte[] array = new byte[wrap.limit()];
                wrap.get(array, 0, array.length);
                this.serverDataCache = array;
            }
            final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator));
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.getChannel().truncate(0L);
            fileOutputStream.write(wrap.array(), 0, wrap.limit());
            fileOutputStream.flush();
            fileOutputStream.close();
            Files.copy(file, new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator)));
            file.delete();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new WorldDictionaryException("Error saving WorldDictionary.", ex);
        }
    }
    
    protected void saveToByteBuffer(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.putShort(this.NextItemID);
        byteBuffer.put(this.NextObjectNameID);
        byteBuffer.putInt(this.NextSpriteNameID);
        final ArrayList list = new ArrayList<String>();
        this.getItemMods(list);
        byteBuffer.putInt(list.size());
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            GameWindow.WriteString(byteBuffer, iterator.next());
        }
        final ArrayList<Object> list2 = new ArrayList<Object>();
        this.getModuleList((List<String>)list2);
        byteBuffer.putInt(list2.size());
        final Iterator<String> iterator2 = list2.iterator();
        while (iterator2.hasNext()) {
            GameWindow.WriteString(byteBuffer, iterator2.next());
        }
        byteBuffer.putInt(this.itemIdToInfoMap.size());
        final Iterator<Map.Entry<Short, ItemInfo>> iterator3 = this.itemIdToInfoMap.entrySet().iterator();
        while (iterator3.hasNext()) {
            iterator3.next().getValue().save(byteBuffer, list, (List<String>)list2);
        }
        byteBuffer.putInt(this.objectIdToNameMap.size());
        for (final Map.Entry<Byte, String> entry : this.objectIdToNameMap.entrySet()) {
            byteBuffer.put(entry.getKey());
            GameWindow.WriteString(byteBuffer, entry.getValue());
        }
        byteBuffer.putInt(this.spriteIdToNameMap.size());
        for (final Map.Entry<Integer, String> entry2 : this.spriteIdToNameMap.entrySet()) {
            byteBuffer.putInt(entry2.getKey());
            GameWindow.WriteString(byteBuffer, entry2.getValue());
        }
    }
    
    protected void saveAsText(final String s) throws IOException, WorldDictionaryException {
        if (Core.getInstance().isNoSave()) {
            return;
        }
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator));
        if (file.exists() && file.isDirectory()) {
            final File file2 = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator, s));
            try {
                final FileWriter fileWriter = new FileWriter(file2, false);
                try {
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    for (final Map.Entry<Short, ItemInfo> entry : this.itemIdToInfoMap.entrySet()) {
                        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                        entry.getValue().saveAsText(fileWriter, "\t\t");
                        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    }
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    for (final Map.Entry<Byte, String> entry2 : this.objectIdToNameMap.entrySet()) {
                        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, entry2.getKey(), (String)entry2.getValue(), System.lineSeparator()));
                    }
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    for (final Map.Entry<Integer, String> entry3 : this.spriteIdToNameMap.entrySet()) {
                        fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, entry3.getKey(), (String)entry3.getValue(), System.lineSeparator()));
                    }
                    fileWriter.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    fileWriter.close();
                }
                catch (Throwable t) {
                    try {
                        fileWriter.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                throw new WorldDictionaryException("Error saving WorldDictionary as text.", ex);
            }
        }
    }
}
