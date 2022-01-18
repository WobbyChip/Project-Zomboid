// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.textures;

import java.io.Serializable;

class AlphaColorIndex implements Serializable
{
    byte alpha;
    byte blue;
    byte green;
    byte red;
    
    AlphaColorIndex(final int n, final int n2, final int n3, final int n4) {
        this.red = (byte)n;
        this.green = (byte)n2;
        this.blue = (byte)n3;
        this.alpha = (byte)n4;
    }
}
