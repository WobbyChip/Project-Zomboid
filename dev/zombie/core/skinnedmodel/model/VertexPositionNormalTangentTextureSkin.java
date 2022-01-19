// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import java.nio.ByteBuffer;
import zombie.core.skinnedmodel.Vector4;
import zombie.iso.Vector2;
import zombie.core.skinnedmodel.Vector3;

public final class VertexPositionNormalTangentTextureSkin
{
    public Vector3 Position;
    public Vector3 Normal;
    public Vector3 Tangent;
    public Vector2 TextureCoordinates;
    public Vector4 BlendWeights;
    public UInt4 BlendIndices;
    
    public VertexPositionNormalTangentTextureSkin() {
    }
    
    public VertexPositionNormalTangentTextureSkin(final Vector3 position, final Vector3 normal, final Vector3 tangent, final Vector2 textureCoordinates, final Vector4 blendWeights, final UInt4 blendIndices) {
        this.Position = position;
        this.Normal = normal;
        this.Tangent = tangent;
        this.TextureCoordinates = textureCoordinates;
        this.BlendWeights = blendWeights;
        this.BlendIndices = blendIndices;
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
        byteBuffer.putFloat(this.BlendWeights.x);
        byteBuffer.putFloat(this.BlendWeights.y);
        byteBuffer.putFloat(this.BlendWeights.z);
        byteBuffer.putFloat(this.BlendWeights.w);
        byteBuffer.putFloat((float)this.BlendIndices.X);
        byteBuffer.putFloat((float)this.BlendIndices.Y);
        byteBuffer.putFloat((float)this.BlendIndices.Z);
        byteBuffer.putFloat((float)this.BlendIndices.W);
    }
}
