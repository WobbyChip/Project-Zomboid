// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core.Collections;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

class UtilUnsafe
{
    private UtilUnsafe() {
    }
    
    public static Unsafe getUnsafe() {
        if (UtilUnsafe.class.getClassLoader() == null) {
            return Unsafe.getUnsafe();
        }
        try {
            final Field declaredField = Unsafe.class.getDeclaredField("theUnsafe");
            declaredField.setAccessible(true);
            return (Unsafe)declaredField.get(UtilUnsafe.class);
        }
        catch (Exception cause) {
            throw new RuntimeException("Could not obtain access to sun.misc.Unsafe", cause);
        }
    }
}
