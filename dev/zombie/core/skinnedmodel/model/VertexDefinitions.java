// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.skinnedmodel.model;

import zombie.iso.Vector2;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.Color;
import zombie.core.skinnedmodel.Vector3;

public class VertexDefinitions
{
    class VertexPositionColour
    {
        public Vector3 Position;
        public int Colour;
        
        public VertexPositionColour(final Vector3 position, final Color color) {
            this.Position = position;
            this.Colour = HelperFunctions.ToRgba(color);
        }
        
        public VertexPositionColour(final float n, final float n2, final float n3, final Color color) {
            this.Position = new Vector3(n, n2, n3);
            this.Colour = HelperFunctions.ToRgba(color);
        }
    }
    
    class VertexPositionNormal
    {
        public Vector3 Position;
        public Vector3 Normal;
        
        public VertexPositionNormal(final Vector3 position, final Vector3 normal, final Vector2 vector2) {
            this.Position = position;
            this.Normal = normal;
        }
        
        public VertexPositionNormal(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.Position = new Vector3(n, n2, n3);
            this.Normal = new Vector3(n4, n5, n6);
        }
    }
    
    class VertexPositionNormalTexture
    {
        public Vector3 Position;
        public Vector3 Normal;
        public Vector2 TextureCoordinates;
        
        public VertexPositionNormalTexture(final Vector3 position, final Vector3 normal, final Vector2 textureCoordinates) {
            this.Position = position;
            this.Normal = normal;
            this.TextureCoordinates = textureCoordinates;
        }
        
        public VertexPositionNormalTexture(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
            this.Position = new Vector3(n, n2, n3);
            this.Normal = new Vector3(n4, n5, n6);
            this.TextureCoordinates = new Vector2(n7, n8);
        }
    }
    
    class VertexPositionNormalTangentTexture
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
        
        public VertexPositionNormalTangentTexture(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11) {
            this.Position = new Vector3(n, n2, n3);
            this.Normal = new Vector3(n4, n5, n6);
            this.Tangent = new Vector3(n7, n8, n9);
            this.TextureCoordinates = new Vector2(n10, n11);
        }
    }
}
