// 
// Decompiled by Procyon v0.5.36
// 

package zombie.worldMap;

import java.util.Map;
import zombie.GameWindow;
import java.nio.ByteBuffer;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import zombie.util.SharedStrings;

public final class WorldMapBinary
{
    private static final int VERSION1 = 1;
    private static final int VERSION_LATEST = 1;
    private final SharedStrings m_sharedStrings;
    private final TIntObjectHashMap<String> m_stringTable;
    private final WorldMapProperties m_properties;
    private final ArrayList<WorldMapProperties> m_sharedProperties;
    
    public WorldMapBinary() {
        this.m_sharedStrings = new SharedStrings();
        this.m_stringTable = (TIntObjectHashMap<String>)new TIntObjectHashMap();
        this.m_properties = new WorldMapProperties();
        this.m_sharedProperties = new ArrayList<WorldMapProperties>();
    }
    
    public boolean read(final String name, final WorldMapData worldMapData) throws Exception {
        final FileInputStream in = new FileInputStream(name);
        try {
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
            try {
                final int read = bufferedInputStream.read();
                final int read2 = bufferedInputStream.read();
                final int read3 = bufferedInputStream.read();
                final int read4 = bufferedInputStream.read();
                if (read != 73 || read2 != 71 || read3 != 77 || read4 != 66) {
                    throw new IOException("invalid format (magic doesn't match)");
                }
                final int int1 = this.readInt(bufferedInputStream);
                if (int1 < 1 || int1 > 1) {
                    throw new IOException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, int1));
                }
                final int int2 = this.readInt(bufferedInputStream);
                final int int3 = this.readInt(bufferedInputStream);
                this.readStringTable(bufferedInputStream);
                for (int i = 0; i < int3; ++i) {
                    for (int j = 0; j < int2; ++j) {
                        final WorldMapCell cell = this.parseCell(bufferedInputStream);
                        if (cell != null) {
                            worldMapData.m_cells.add(cell);
                        }
                    }
                }
                final boolean b = true;
                bufferedInputStream.close();
                in.close();
                return b;
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
    
    private int readByte(final InputStream inputStream) throws IOException {
        return inputStream.read();
    }
    
    private int readInt(final InputStream inputStream) throws IOException {
        final int read = inputStream.read();
        final int read2 = inputStream.read();
        final int read3 = inputStream.read();
        final int read4 = inputStream.read();
        if ((read | read2 | read3 | read4) < 0) {
            throw new EOFException();
        }
        return (read << 0) + (read2 << 8) + (read3 << 16) + (read4 << 24);
    }
    
    private int readShort(final InputStream inputStream) throws IOException {
        final int read = inputStream.read();
        final int read2 = inputStream.read();
        if ((read | read2) < 0) {
            throw new EOFException();
        }
        return (short)((read << 0) + (read2 << 8));
    }
    
    private void readStringTable(final InputStream inputStream) throws IOException {
        final ByteBuffer allocate = ByteBuffer.allocate(1024);
        final byte[] array = new byte[1024];
        for (int int1 = this.readInt(inputStream), i = 0; i < int1; ++i) {
            allocate.clear();
            final int short1 = this.readShort(inputStream);
            allocate.putShort((short)short1);
            inputStream.read(array, 0, short1);
            allocate.put(array, 0, short1);
            allocate.flip();
            this.m_stringTable.put(i, (Object)GameWindow.ReadStringUTF(allocate));
        }
    }
    
    private String readStringIndexed(final InputStream inputStream) throws IOException {
        final int short1 = this.readShort(inputStream);
        if (!this.m_stringTable.containsKey(short1)) {
            throw new IOException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, short1));
        }
        return (String)this.m_stringTable.get(short1);
    }
    
    private WorldMapCell parseCell(final InputStream inputStream) throws IOException {
        final int int1 = this.readInt(inputStream);
        if (int1 == -1) {
            return null;
        }
        final int int2 = this.readInt(inputStream);
        final WorldMapCell worldMapCell = new WorldMapCell();
        worldMapCell.m_x = int1;
        worldMapCell.m_y = int2;
        for (int int3 = this.readInt(inputStream), i = 0; i < int3; ++i) {
            worldMapCell.m_features.add(this.parseFeature(worldMapCell, inputStream));
        }
        return worldMapCell;
    }
    
    private WorldMapFeature parseFeature(final WorldMapCell worldMapCell, final InputStream inputStream) throws IOException {
        final WorldMapFeature worldMapFeature = new WorldMapFeature(worldMapCell);
        worldMapFeature.m_geometries.add(this.parseGeometry(inputStream));
        this.parseFeatureProperties(inputStream, worldMapFeature);
        return worldMapFeature;
    }
    
    private void parseFeatureProperties(final InputStream inputStream, final WorldMapFeature worldMapFeature) throws IOException {
        this.m_properties.clear();
        for (int byte1 = this.readByte(inputStream), i = 0; i < byte1; ++i) {
            this.m_properties.put(this.m_sharedStrings.get(this.readStringIndexed(inputStream)), this.m_sharedStrings.get(this.readStringIndexed(inputStream)));
        }
        worldMapFeature.m_properties = this.getOrCreateProperties(this.m_properties);
    }
    
    private WorldMapProperties getOrCreateProperties(final WorldMapProperties worldMapProperties) {
        for (int i = 0; i < this.m_sharedProperties.size(); ++i) {
            if (this.m_sharedProperties.get(i).equals(worldMapProperties)) {
                return this.m_sharedProperties.get(i);
            }
        }
        final WorldMapProperties e = new WorldMapProperties();
        e.putAll(worldMapProperties);
        this.m_sharedProperties.add(e);
        return e;
    }
    
    private WorldMapGeometry parseGeometry(final InputStream inputStream) throws IOException {
        final WorldMapGeometry worldMapGeometry = new WorldMapGeometry();
        worldMapGeometry.m_type = WorldMapGeometry.Type.valueOf(this.readStringIndexed(inputStream));
        for (int byte1 = this.readByte(inputStream), i = 0; i < byte1; ++i) {
            final WorldMapPoints e = new WorldMapPoints();
            this.parseGeometryCoordinates(inputStream, e);
            worldMapGeometry.m_points.add(e);
        }
        worldMapGeometry.calculateBounds();
        return worldMapGeometry;
    }
    
    private void parseGeometryCoordinates(final InputStream inputStream, final WorldMapPoints worldMapPoints) throws IOException {
        for (int short1 = this.readShort(inputStream), i = 0; i < short1; ++i) {
            worldMapPoints.add(this.readShort(inputStream));
            worldMapPoints.add(this.readShort(inputStream));
        }
    }
}
