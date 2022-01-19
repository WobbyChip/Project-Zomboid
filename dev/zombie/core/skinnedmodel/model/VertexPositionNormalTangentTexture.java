// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import java.nio.ByteBuffer;
import zombie.iso.Vector2;
import zombie.core.skinnedmodel.Vector3;

public final class VertexPositionNormalTangentTexture
{
    public Vector3 Position;
    public Vector3 Normal;
    public Vector3 Tangent;
    public Vector2 TextureCoordinates;
    
    public VertexPositionNormalTangentTexture(final Vector3 position, final Vector3 normal, final Vector3 tangent, final Vector2 textureCoordinates) {
        this.Position = position;
        this.Normal = normal;
        this.Tangent = tangent;
        this.TextureCoordinates = textureCoordinates;
    }
    
    public VertexPositionNormalTangentTexture() {
        this.Position = new Vector3(0.0f, 0.0f, 0.0f);
        this.Normal = new Vector3(0.0f, 0.0f, 1.0f);
        this.Tangent = new Vector3(0.0f, 1.0f, 0.0f);
        this.TextureCoordinates = new Vector2(0.0f, 0.0f);
    }
    
    public void put(final ByteBuffer byteBuffer) {
        byteBuffer.putFloat(this.Position.x());
        byteBuffer.putFloat(this.Position.y());
        byteBuffer.putFloat(this.Position.z());
        byteBuffer.putFloat(this.Normal.x());
        byteBuffer.putFloat(this.Normal.y());
        byteBuffer.putFloat(this.Normal.z());
        byteBuffer.putFloat(this.Tangent.x());
        byteBuffer.putFloat(this.Tangent.y());
        byteBuffer.putFloat(this.Tangent.z());
        byteBuffer.putFloat(this.TextureCoordinates.x);
        byteBuffer.putFloat(this.TextureCoordinates.y);
    }
}
