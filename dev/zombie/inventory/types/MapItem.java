// 
// Decompiled by Procyon v0.5.36
// 

package zombie.inventory.types;

import zombie.GameWindow;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import zombie.core.logger.ExceptionLogger;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import zombie.core.Core;
import java.io.File;
import zombie.ZomboidFileSystem;
import zombie.iso.SliceY;
import zombie.scripting.objects.Item;
import zombie.scripting.ScriptManager;
import zombie.worldMap.symbols.WorldMapSymbols;
import zombie.inventory.InventoryItem;

public class MapItem extends InventoryItem
{
    public static MapItem WORLD_MAP_INSTANCE;
    private static final byte[] FILE_MAGIC;
    private String m_mapID;
    private final WorldMapSymbols m_symbols;
    
    public static MapItem getSingleton() {
        if (MapItem.WORLD_MAP_INSTANCE == null) {
            final Item findItem = ScriptManager.instance.FindItem("Base.Map");
            if (findItem == null) {
                return null;
            }
            MapItem.WORLD_MAP_INSTANCE = new MapItem("Base", "World Map", "WorldMap", findItem);
        }
        return MapItem.WORLD_MAP_INSTANCE;
    }
    
    public static void SaveWorldMap() {
        if (MapItem.WORLD_MAP_INSTANCE == null) {
            return;
        }
        try {
            final ByteBuffer sliceBuffer = SliceY.SliceBuffer;
            sliceBuffer.clear();
            sliceBuffer.put(MapItem.FILE_MAGIC);
            sliceBuffer.putInt(186);
            MapItem.WORLD_MAP_INSTANCE.getSymbols().save(sliceBuffer);
            final FileOutputStream out = new FileOutputStream(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator)));
            try {
                final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);
                try {
                    bufferedOutputStream.write(sliceBuffer.array(), 0, sliceBuffer.position());
                    bufferedOutputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedOutputStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
                out.close();
            }
            catch (Throwable t2) {
                try {
                    out.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public static void LoadWorldMap() {
        if (getSingleton() == null) {
            return;
        }
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ZomboidFileSystem.instance.getGameModeCacheDir(), File.separator, Core.GameSaveWorld, File.separator));
        try {
            final FileInputStream in = new FileInputStream(file);
            try {
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                try {
                    final ByteBuffer sliceBuffer = SliceY.SliceBuffer;
                    sliceBuffer.clear();
                    sliceBuffer.limit(bufferedInputStream.read(sliceBuffer.array()));
                    final byte[] array = new byte[4];
                    sliceBuffer.get(array);
                    if (!Arrays.equals(array, MapItem.FILE_MAGIC)) {
                        throw new IOException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath()));
                    }
                    getSingleton().getSymbols().load(sliceBuffer, sliceBuffer.getInt());
                    bufferedInputStream.close();
                }
                catch (Throwable t) {
                    try {
                        bufferedInputStream.close();
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
        catch (FileNotFoundException ex2) {}
        catch (Exception ex) {
            ExceptionLogger.logException(ex);
        }
    }
    
    public static void Reset() {
        if (MapItem.WORLD_MAP_INSTANCE == null) {
            return;
        }
        MapItem.WORLD_MAP_INSTANCE.getSymbols().clear();
        MapItem.WORLD_MAP_INSTANCE = null;
    }
    
    public MapItem(final String s, final String s2, final String s3, final String s4) {
        super(s, s2, s3, s4);
        this.m_symbols = new WorldMapSymbols();
    }
    
    public MapItem(final String s, final String s2, final String s3, final Item item) {
        super(s, s2, s3, item);
        this.m_symbols = new WorldMapSymbols();
    }
    
    @Override
    public int getSaveType() {
        return Item.Type.Map.ordinal();
    }
    
    @Override
    public boolean IsMap() {
        return true;
    }
    
    public void setMapID(final String mapID) {
        this.m_mapID = mapID;
    }
    
    public String getMapID() {
        return this.m_mapID;
    }
    
    public WorldMapSymbols getSymbols() {
        return this.m_symbols;
    }
    
    @Override
    public void save(final ByteBuffer byteBuffer, final boolean b) throws IOException {
        super.save(byteBuffer, b);
        GameWindow.WriteString(byteBuffer, this.m_mapID);
        this.m_symbols.save(byteBuffer);
    }
    
    @Override
    public void load(final ByteBuffer byteBuffer, final int n) throws IOException {
        super.load(byteBuffer, n);
        this.m_mapID = GameWindow.ReadString(byteBuffer);
        this.m_symbols.load(byteBuffer, n);
    }
    
    static {
        FILE_MAGIC = new byte[] { 87, 77, 83, 89 };
    }
}
