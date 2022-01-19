// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.hash;

import java.io.Serializable;

public class DefaultIntHashFunction implements IntHashFunction, Serializable
{
    private static final long serialVersionUID = 1L;
    public static final IntHashFunction INSTANCE;
    
    protected DefaultIntHashFunction() {
    }
    
    @Override
    public int hash(final int n) {
        return n;
    }
    
    static {
        INSTANCE = new DefaultIntHashFunction();
    }
}
