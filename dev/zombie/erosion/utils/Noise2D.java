// 
// Decompiled by Procyon v0.5.36
// 

package zombie.erosion.utils;

import java.util.ArrayList;

public class Noise2D
{
    private ArrayList<Layer> layers;
    private static final int[] perm;
    
    public Noise2D() {
        this.layers = new ArrayList<Layer>(3);
    }
    
    private float lerp(final float n, final float n2, final float n3) {
        return n2 + n * (n3 - n2);
    }
    
    private float fade(final float n) {
        return n * n * n * (n * (n * 6.0f - 15.0f) + 10.0f);
    }
    
    private float noise(final float n, final float n2, final int[] array) {
        final int n3 = (int)Math.floor(n - Math.floor(n / 255.0f) * 255.0);
        final int n4 = (int)Math.floor(n2 - Math.floor(n2 / 255.0f) * 255.0);
        final float fade = this.fade(n - (float)Math.floor(n));
        return this.lerp(this.fade(n2 - (float)Math.floor(n2)), this.lerp(fade, (float)Noise2D.perm[array[array[n3] + n4]], (float)Noise2D.perm[array[array[n3 + 1] + n4]]), this.lerp(fade, (float)Noise2D.perm[array[array[n3] + n4 + 1]], (float)Noise2D.perm[array[array[n3 + 1] + n4 + 1]]));
    }
    
    public float layeredNoise(final float n, final float n2) {
        float n3 = 0.0f;
        float n4 = 0.0f;
        for (int i = 0; i < this.layers.size(); ++i) {
            final Layer layer = this.layers.get(i);
            n4 += layer.amp;
            n3 += this.noise(n * layer.freq, n2 * layer.freq, layer.p) * layer.amp;
        }
        return n3 / n4 / 255.0f;
    }
    
    public void addLayer(final int n, final float freq, final float amp) {
        final int n2 = (int)Math.floor(n - Math.floor(n / 256.0f) * 256.0);
        final Layer e = new Layer();
        e.freq = freq;
        e.amp = amp;
        for (int i = 0; i < 256; ++i) {
            final int n3 = (int)Math.floor(n2 + i - Math.floor((n2 + i) / 256.0f) * 256.0);
            e.p[n3] = Noise2D.perm[i];
            e.p[256 + n3] = e.p[n3];
        }
        this.layers.add(e);
    }
    
    public void reset() {
        if (this.layers.size() > 0) {
            this.layers.clear();
        }
    }
    
    static {
        perm = new int[] { 151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180 };
    }
    
    private class Layer
    {
        public float freq;
        public float amp;
        public int[] p;
        
        private Layer() {
            this.p = new int[512];
        }
    }
}
