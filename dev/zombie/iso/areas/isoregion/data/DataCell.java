// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso.areas.isoregion.data;

import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public final class DataCell
{
    public final DataRoot dataRoot;
    protected final Map<Integer, DataChunk> dataChunks;
    
    protected DataCell(final DataRoot dataRoot, final int n, final int n2, final int n3) {
        this.dataChunks = new HashMap<Integer, DataChunk>();
        this.dataRoot = dataRoot;
    }
    
    protected DataRoot getDataRoot() {
        return this.dataRoot;
    }
    
    protected DataChunk getChunk(final int i) {
        return this.dataChunks.get(i);
    }
    
    protected DataChunk addChunk(final int n, final int n2, final int i) {
        final DataChunk dataChunk = new DataChunk(n, n2, this, i);
        this.dataChunks.put(i, dataChunk);
        return dataChunk;
    }
    
    protected void setChunk(final DataChunk dataChunk) {
        this.dataChunks.put(dataChunk.getHashId(), dataChunk);
    }
    
    protected void getAllChunks(final List<DataChunk> list) {
        final Iterator<Map.Entry<Integer, DataChunk>> iterator = this.dataChunks.entrySet().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getValue());
        }
    }
}
