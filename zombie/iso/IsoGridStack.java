// 
// Decompiled by Procyon v0.5.36
// 

package zombie.iso;

import java.util.ArrayList;

public class IsoGridStack
{
    public ArrayList<ArrayList<IsoGridSquare>> Squares;
    
    public IsoGridStack(final int initialCapacity) {
        this.Squares = new ArrayList<ArrayList<IsoGridSquare>>(initialCapacity);
        for (int i = 0; i < initialCapacity; ++i) {
            this.Squares.add(new ArrayList<IsoGridSquare>(5000));
        }
    }
}
