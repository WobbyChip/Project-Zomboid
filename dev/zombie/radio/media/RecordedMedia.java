// 
// Decompiled by Procyon v0.5.36
// 

package zombie.radio.media;

import zombie.characters.IsoPlayer;
import java.text.Normalizer;
import java.io.FileOutputStream;
import java.io.IOException;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import java.io.FileInputStream;
import zombie.world.WorldDictionary;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.Rand;
import java.util.Comparator;
import java.util.Iterator;
import zombie.debug.DebugLog;
import zombie.Lua.LuaEventManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ArrayList;

public class RecordedMedia
{
    public static boolean DISABLE_LINE_LEARNING;
    private static final int SPAWN_COMMON = 0;
    private static final int SPAWN_RARE = 1;
    private static final int SPAWN_EXCEPTIONAL = 2;
    public static final int VERSION = 1;
    public static final String SAVE_FILE = "recorded_media.bin";
    private final ArrayList<String> indexes;
    private final Map<String, MediaData> mediaDataMap;
    private final Map<String, ArrayList<MediaData>> categorizedMap;
    private final ArrayList<String> categories;
    private final HashSet<String> listenedLines;
    private final HashSet<Short> homeVhsSpawned;
    private final Map<Integer, ArrayList<MediaData>> retailVhsSpawnTable;
    private final Map<Integer, ArrayList<MediaData>> retailCdSpawnTable;
    private boolean REQUIRES_SAVING;
    
    public RecordedMedia() {
        this.indexes = new ArrayList<String>();
        this.mediaDataMap = new HashMap<String, MediaData>();
        this.categorizedMap = new HashMap<String, ArrayList<MediaData>>();
        this.categories = new ArrayList<String>();
        this.listenedLines = new HashSet<String>();
        this.homeVhsSpawned = new HashSet<Short>();
        this.retailVhsSpawnTable = new HashMap<Integer, ArrayList<MediaData>>();
        this.retailCdSpawnTable = new HashMap<Integer, ArrayList<MediaData>>();
        this.REQUIRES_SAVING = true;
    }
    
    public boolean hasListenedLineAndAdd(final String s) {
        if (RecordedMedia.DISABLE_LINE_LEARNING) {
            return false;
        }
        if (this.listenedLines.contains(s)) {
            return true;
        }
        this.listenedLines.add(s);
        this.REQUIRES_SAVING = true;
        return false;
    }
    
    public void init() {
        try {
            this.load();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        LuaEventManager.triggerEvent("OnInitRecordedMedia", this);
        this.retailCdSpawnTable.put(0, new ArrayList<MediaData>());
        this.retailCdSpawnTable.put(1, new ArrayList<MediaData>());
        this.retailCdSpawnTable.put(2, new ArrayList<MediaData>());
        this.retailVhsSpawnTable.put(0, new ArrayList<MediaData>());
        this.retailVhsSpawnTable.put(1, new ArrayList<MediaData>());
        this.retailVhsSpawnTable.put(2, new ArrayList<MediaData>());
        final ArrayList<MediaData> list = this.categorizedMap.get("CDs");
        if (list != null) {
            for (final MediaData e : list) {
                if (e.getSpawning() == 1) {
                    this.retailCdSpawnTable.get(1).add(e);
                }
                else if (e.getSpawning() == 2) {
                    this.retailCdSpawnTable.get(2).add(e);
                }
                else {
                    this.retailCdSpawnTable.get(0).add(e);
                }
            }
        }
        else {
            DebugLog.General.error((Object)"categorizedMap with CDs is empty");
        }
        final ArrayList<MediaData> list2 = this.categorizedMap.get("Retail-VHS");
        if (list2 != null) {
            for (final MediaData e2 : list2) {
                if (e2.getSpawning() == 1) {
                    this.retailVhsSpawnTable.get(1).add(e2);
                }
                else if (e2.getSpawning() == 2) {
                    this.retailVhsSpawnTable.get(2).add(e2);
                }
                else {
                    this.retailVhsSpawnTable.get(0).add(e2);
                }
            }
        }
        else {
            DebugLog.General.error((Object)"categorizedMap with Retail-VHS is empty");
        }
        try {
            this.save();
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }
    
    public static byte getMediaTypeForCategory(final String s) {
        if (s == null) {
            return -1;
        }
        return (byte)(s.equalsIgnoreCase("cds") ? 0 : 1);
    }
    
    public ArrayList<String> getCategories() {
        return this.categories;
    }
    
    public ArrayList<MediaData> getAllMediaForType(final byte b) {
        final ArrayList<MediaData> list = new ArrayList<MediaData>();
        for (final Map.Entry<String, MediaData> entry : this.mediaDataMap.entrySet()) {
            if (entry.getValue().getMediaType() == b) {
                list.add(entry.getValue());
            }
        }
        list.sort(new MediaNameSorter());
        return list;
    }
    
    public MediaData register(final String s, final String e, final String s2, int n) {
        if (this.mediaDataMap.containsKey(e)) {
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e));
            return null;
        }
        if (n < 0) {
            n = 0;
        }
        final MediaData e2 = new MediaData(e, s2, n);
        this.mediaDataMap.put(e, e2);
        e2.setCategory(s);
        if (!this.categorizedMap.containsKey(s)) {
            this.categorizedMap.put(s, new ArrayList<MediaData>());
            this.categories.add(s);
        }
        this.categorizedMap.get(s).add(e2);
        short index;
        if (this.indexes.contains(e)) {
            index = (short)this.indexes.indexOf(e);
        }
        else {
            index = (short)this.indexes.size();
            this.indexes.add(e);
        }
        e2.setIndex(index);
        this.REQUIRES_SAVING = true;
        return e2;
    }
    
    public MediaData getMediaDataFromIndex(final short index) {
        if (index >= 0 && index < this.indexes.size()) {
            return this.getMediaData(this.indexes.get(index));
        }
        return null;
    }
    
    public short getIndexForMediaData(final MediaData mediaData) {
        return (short)this.indexes.indexOf(mediaData.getId());
    }
    
    public MediaData getMediaData(final String s) {
        return this.mediaDataMap.get(s);
    }
    
    public MediaData getRandomFromCategory(final String s) {
        if (this.categorizedMap.containsKey(s)) {
            MediaData mediaData = null;
            if (s.equalsIgnoreCase("cds")) {
                final int next = Rand.Next(0, 1000);
                if (next < 100) {
                    if (this.retailCdSpawnTable.get(2).size() > 0) {
                        mediaData = this.retailCdSpawnTable.get(2).get(Rand.Next(0, this.retailCdSpawnTable.get(2).size()));
                    }
                }
                else if (next < 400) {
                    if (this.retailCdSpawnTable.get(1).size() > 0) {
                        mediaData = this.retailCdSpawnTable.get(1).get(Rand.Next(0, this.retailCdSpawnTable.get(1).size()));
                    }
                }
                else {
                    mediaData = this.retailCdSpawnTable.get(0).get(Rand.Next(0, this.retailCdSpawnTable.get(0).size()));
                }
                if (mediaData != null) {
                    return mediaData;
                }
                return this.retailCdSpawnTable.get(0).get(Rand.Next(0, this.retailCdSpawnTable.get(0).size()));
            }
            else if (s.equalsIgnoreCase("retail-vhs")) {
                final int next2 = Rand.Next(0, 1000);
                if (next2 < 100) {
                    if (this.retailVhsSpawnTable.get(2).size() > 0) {
                        mediaData = this.retailVhsSpawnTable.get(2).get(Rand.Next(0, this.retailVhsSpawnTable.get(2).size()));
                    }
                }
                else if (next2 < 400) {
                    if (this.retailVhsSpawnTable.get(1).size() > 0) {
                        mediaData = this.retailVhsSpawnTable.get(1).get(Rand.Next(0, this.retailVhsSpawnTable.get(1).size()));
                    }
                }
                else {
                    mediaData = this.retailVhsSpawnTable.get(0).get(Rand.Next(0, this.retailVhsSpawnTable.get(0).size()));
                }
                if (mediaData != null) {
                    return mediaData;
                }
                return this.retailVhsSpawnTable.get(0).get(Rand.Next(0, this.retailVhsSpawnTable.get(0).size()));
            }
            else if (s.equalsIgnoreCase("home-vhs") && Rand.Next(0, 1000) < 200) {
                final ArrayList<MediaData> list = this.categorizedMap.get("Home-VHS");
                final MediaData mediaData2 = list.get(Rand.Next(0, list.size()));
                if (!this.homeVhsSpawned.contains(mediaData2.getIndex())) {
                    this.homeVhsSpawned.add(mediaData2.getIndex());
                    this.REQUIRES_SAVING = true;
                    return mediaData2;
                }
            }
        }
        return null;
    }
    
    public void load() throws IOException {
        this.indexes.clear();
        if (Core.getInstance().isNoSave()) {
            return;
        }
        final String pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator);
        final File file = new File(pathname);
        if (!file.exists()) {
            if (!WorldDictionary.isIsNewGame()) {
                DebugLog.log("RecordedMedia data file is missing from world folder.");
            }
            return;
        }
        try {
            final FileInputStream fileInputStream = new FileInputStream(file);
            try {
                DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
                final ByteBuffer allocate = ByteBuffer.allocate((int)file.length());
                allocate.clear();
                allocate.limit(fileInputStream.read(allocate.array()));
                allocate.getInt();
                for (int int1 = allocate.getInt(), i = 0; i < int1; ++i) {
                    this.indexes.add(GameWindow.ReadString(allocate));
                }
                for (int int2 = allocate.getInt(), j = 0; j < int2; ++j) {
                    this.listenedLines.add(GameWindow.ReadString(allocate));
                }
                for (int int3 = allocate.getInt(), k = 0; k < int3; ++k) {
                    this.homeVhsSpawned.add(allocate.getShort());
                }
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
        }
    }
    
    public void save() throws IOException {
        if (Core.getInstance().isNoSave() || !this.REQUIRES_SAVING) {
            return;
        }
        try {
            int n = 0 + this.indexes.size() * 40 + this.listenedLines.size() * 40 + this.homeVhsSpawned.size() * 2;
            n += 512;
            final ByteBuffer wrap = ByteBuffer.wrap(new byte[n]);
            wrap.putInt(1);
            wrap.putInt(this.indexes.size());
            for (int i = 0; i < this.indexes.size(); ++i) {
                GameWindow.WriteString(wrap, this.indexes.get(i));
            }
            wrap.putInt(this.listenedLines.size());
            final String[] array = this.listenedLines.toArray(new String[0]);
            for (int j = 0; j < array.length; ++j) {
                GameWindow.WriteString(wrap, array[j]);
            }
            wrap.putInt(this.homeVhsSpawned.size());
            final Short[] array2 = this.homeVhsSpawned.toArray(new Short[0]);
            for (int k = 0; k < array2.length; ++k) {
                wrap.putShort(array2[k]);
            }
            wrap.flip();
            final String pathname = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator);
            final File file = new File(pathname);
            DebugLog.log(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pathname));
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.getChannel().truncate(0L);
            fileOutputStream.write(wrap.array(), 0, wrap.limit());
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        this.REQUIRES_SAVING = false;
    }
    
    public static String toAscii(String normalize) {
        final StringBuilder sb = new StringBuilder(normalize.length());
        normalize = Normalizer.normalize(normalize, Normalizer.Form.NFD);
        for (final char c : normalize.toCharArray()) {
            if (c <= '\u007f') {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    public boolean hasListenedToLine(final IsoPlayer isoPlayer, final String o) {
        return this.listenedLines.contains(o);
    }
    
    public boolean hasListenedToAll(final IsoPlayer isoPlayer, final MediaData mediaData) {
        for (int i = 0; i < mediaData.getLineCount(); ++i) {
            if (!this.hasListenedToLine(isoPlayer, mediaData.getLine(i).getTextGuid())) {
                return false;
            }
        }
        return mediaData.getLineCount() > 0;
    }
    
    static {
        RecordedMedia.DISABLE_LINE_LEARNING = false;
    }
    
    public static class MediaNameSorter implements Comparator<MediaData>
    {
        @Override
        public int compare(final MediaData mediaData, final MediaData mediaData2) {
            return mediaData.getTranslatedItemDisplayName().compareToIgnoreCase(mediaData2.getTranslatedItemDisplayName());
        }
    }
}
