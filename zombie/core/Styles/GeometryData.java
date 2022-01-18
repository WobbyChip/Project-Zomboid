// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Styles;

public class GeometryData
{
    private final FloatList vertexData;
    private final ShortList indexData;
    
    public GeometryData(final FloatList vertexData, final ShortList indexData) {
        this.vertexData = vertexData;
        this.indexData = indexData;
    }
    
    public void clear() {
        this.vertexData.clear();
        this.indexData.clear();
    }
    
    public FloatList getVertexData() {
        return this.vertexData;
    }
    
    public ShortList getIndexData() {
        return this.indexData;
    }
}
