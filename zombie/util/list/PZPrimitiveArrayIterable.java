// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.list;

import java.util.Iterator;

public final class PZPrimitiveArrayIterable
{
    public static Iterable<Float> fromArray(final float[] array) {
        return new Iterable<Float>() {
            private final float[] m_list = array;
            
            @Override
            public Iterator<Float> iterator() {
                return new Iterator<Float>() {
                    private int pos = 0;
                    
                    @Override
                    public boolean hasNext() {
                        return Iterable.this.m_list.length > this.pos;
                    }
                    
                    @Override
                    public Float next() {
                        return Iterable.this.m_list[this.pos++];
                    }
                };
            }
        };
    }
}
