// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

public final class SoftwareModelMeshInstance
{
    public SoftwareModelMesh softwareMesh;
    public VertexBufferObject vb;
    public String name;
    
    public SoftwareModelMeshInstance(final String name, final SoftwareModelMesh softwareMesh) {
        this.name = name;
        this.softwareMesh = softwareMesh;
        this.vb = new VertexBufferObject();
        this.vb.elements = softwareMesh.indicesUnskinned;
    }
}
