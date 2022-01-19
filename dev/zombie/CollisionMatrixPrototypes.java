// 
// Decompiled by Procyon v0.5.36
// 

package zombie;

import java.util.HashMap;

public class CollisionMatrixPrototypes
{
    public static CollisionMatrixPrototypes instance;
    public HashMap<Integer, boolean[][][]> Map;
    
    public CollisionMatrixPrototypes() {
        this.Map = new HashMap<Integer, boolean[][][]>();
    }
    
    public int ToBitMatrix(final boolean[][][] array) {
        int set = 0;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                for (int k = 0; k < 3; ++k) {
                    if (array[i][j][k]) {
                        set = BitMatrix.Set(set, i - 1, j - 1, k - 1, true);
                    }
                }
            }
        }
        return set;
    }
    
    public boolean[][][] Add(final int i) {
        if (this.Map.containsKey(i)) {
            return this.Map.get(i);
        }
        final boolean[][][] value = new boolean[3][3][3];
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 3; ++k) {
                for (int l = 0; l < 3; ++l) {
                    value[j][k][l] = BitMatrix.Is(i, j - 1, k - 1, l - 1);
                }
            }
        }
        this.Map.put(i, value);
        return value;
    }
    
    static {
        CollisionMatrixPrototypes.instance = new CollisionMatrixPrototypes();
    }
}
