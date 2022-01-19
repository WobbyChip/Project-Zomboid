// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.iso.Vector2;
import zombie.core.skinnedmodel.Vector3;

public final class SoftwareModelMesh
{
    public int[] indicesUnskinned;
    public VertexPositionNormalTangentTextureSkin[] verticesUnskinned;
    public String Texture;
    public VertexBufferObject vb;
    
    public SoftwareModelMesh(final VertexPositionNormalTangentTextureSkin[] verticesUnskinned, final int[] indicesUnskinned) {
        this.indicesUnskinned = indicesUnskinned;
        this.verticesUnskinned = verticesUnskinned;
    }
    
    public SoftwareModelMesh(final VertexPositionNormalTangentTexture[] array, final int[] indicesUnskinned) {
        this.indicesUnskinned = indicesUnskinned;
        this.verticesUnskinned = new VertexPositionNormalTangentTextureSkin[array.length];
        for (int i = 0; i < array.length; ++i) {
            final VertexPositionNormalTangentTexture vertexPositionNormalTangentTexture = array[i];
            this.verticesUnskinned[i] = new VertexPositionNormalTangentTextureSkin();
            this.verticesUnskinned[i].Position = new Vector3(vertexPositionNormalTangentTexture.Position.x(), vertexPositionNormalTangentTexture.Position.y(), vertexPositionNormalTangentTexture.Position.z());
            this.verticesUnskinned[i].Normal = new Vector3(vertexPositionNormalTangentTexture.Normal.x(), vertexPositionNormalTangentTexture.Normal.y(), vertexPositionNormalTangentTexture.Normal.z());
            this.verticesUnskinned[i].TextureCoordinates = new Vector2(vertexPositionNormalTangentTexture.TextureCoordinates.x, vertexPositionNormalTangentTexture.TextureCoordinates.y);
        }
    }
}
