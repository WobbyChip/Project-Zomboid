// 
// Decompiled by Procyon v0.5.36
// 

package zombie.core;

import java.util.UUID;

public class GUID
{
    public static String generateGUID() {
        return UUID.randomUUID().toString();
    }
}
