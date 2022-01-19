// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util.list;

import java.util.Objects;

public interface FloatConsumer
{
    void accept(final float p0);
    
    default FloatConsumer andThen(final FloatConsumer obj) {
        Objects.requireNonNull(obj);
        return n -> {
            this.accept(n);
            obj.accept(n);
        };
    }
}
