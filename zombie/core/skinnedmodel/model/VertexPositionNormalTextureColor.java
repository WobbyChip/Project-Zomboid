// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import java.nio.ByteBuffer;
import zombie.iso.Vector2;
import zombie.core.skinnedmodel.Vector3;
import zombie.core.Color;

public final class VertexPositionNormalTextureColor
{
    public Color Color;
    public Vector3 Position;
    public Vector3 Normal;
    public Vector2 TextureCoordinates;
    
    public void put(final ByteBuffer byteBuffer) {
        byteBuffer.putFloat(this.Position.x());
        byteBuffer.putFloat(this.Position.y());
        byteBuffer.putFloat(this.Position.z());
        byteBuffer.putFloat(this.Normal.x());
        byteBuffer.putFloat(this.Normal.y());
        byteBuffer.putFloat(this.Normal.z());
        byteBuffer.putFloat(this.TextureCoordinates.x);
        byteBuffer.putFloat(this.TextureCoordinates.y);
        byteBuffer.put((byte)(this.Color.r * 255.0f));
        byteBuffer.put((byte)(this.Color.g * 255.0f));
        byteBuffer.put((byte)(this.Color.b * 255.0f));
    }
}
