// 
// Decompiled by Procyon v0.5.36
// 

package zombie.util;

public class Type
{
    public static <R, I> R tryCastTo(final I obj, final Class<R> clazz) {
        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        }
        return null;
    }
    
    public static boolean asBoolean(final Object o) {
        return asBoolean(o, false);
    }
    
    public static boolean asBoolean(final Object o, final boolean b) {
        if (o == null) {
            return b;
        }
        final Boolean b2 = tryCastTo(o, Boolean.class);
        if (b2 == null) {
            return b;
        }
        return b2;
    }
}
